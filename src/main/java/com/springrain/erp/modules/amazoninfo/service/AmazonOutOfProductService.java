/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonOutOfProductDao;
import com.springrain.erp.modules.amazoninfo.dao.PriceFeedDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonOutOfProduct;
import com.springrain.erp.modules.amazoninfo.entity.PriceFeed;
import com.springrain.erp.modules.amazoninfo.scheduler.SaveHistoryInfoMonitor;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 亚马逊帖子上架Service
 * @author tim
 * @version 2014-08-06
 */
@Component
@Transactional(readOnly = true)
public class AmazonOutOfProductService extends BaseService {

	private final static Logger logger = LoggerFactory.getLogger(AmazonOutOfProductService.class);
	@Autowired
	private AmazonOutOfProductDao   outOfProductDao;
	@Autowired
	private SaleReportService 		reportService;
	
	
	
	public AmazonOutOfProduct get(Integer id) {
		return outOfProductDao.get(id);
	}
	
	
	@Transactional(readOnly = false)
	public void save(AmazonOutOfProduct outOfproduct) {
		outOfProductDao.save(outOfproduct);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		outOfProductDao.deleteById(id);
	}
	
	@Transactional(readOnly = false)
	public void createOutOfData(){
		String sql = "INSERT INTO amazoninfo_out_of_product(product_name_color,country,data_date) "+
			 	" SELECT aaaa.proName,aaaa.country,bbbb.data_date FROM  "+
			 	" (SELECT aaa.proName,ccc.country FROM (SELECT aa.proName,(SUBSTRING_INDEX(SUBSTRING_INDEX(aa.country,',',bb.help_topic_id+1),',',-1)) AS country FROM (SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName  "+
			 	" ,(CASE WHEN (a.has_power = '1' OR a.TYPE = 'Keyboard') THEN 'de,uk,com,mx,ca,jp' ELSE 'de,com,mx,ca,jp' END) AS country FROM psi_product a JOIN mysql.help_topic b  "+
			 	"  ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.del_flag = '0') aa JOIN mysql.help_topic bb  ON bb.help_topic_id < (LENGTH(aa.country) - LENGTH(REPLACE(aa.country,',',''))+1))aaa ,psi_product_eliminate ccc WHERE aaa.proName = CONCAT(ccc.product_name,CASE WHEN ccc.color!='' THEN '_' ELSE '' END ,ccc.color) AND aaa.country = ccc.country AND ccc.is_sale != '4' AND ccc.added_month<DATE_ADD(CURDATE(),INTERVAL -8 DAY)) aaaa, "+
			 	"  (SELECT a.data_date, CONCAT(b.product_name,CASE WHEN b.color!='' THEN '_' ELSE '' END,b.color) AS proName,(CASE WHEN a.country='fr'|| a.country='it' || a.country='es' THEN 'de' ELSE a.country END) AS country,SUM(a.fulfillable_quantity)  AS num  FROM psi_inventory_fba AS a ,psi_sku b WHERE BINARY(a.sku) = b.sku AND a.country = b.country  AND b.del_flag='0' AND  a.data_date=CURDATE()   GROUP BY b.product_name,b.color,(CASE WHEN a.country='fr'|| a.country='it' || a.country='es' THEN 'de' ELSE a.country END) HAVING num=0) bbbb WHERE aaaa.proName = bbbb.proName AND aaaa.country = bbbb.country";
				outOfProductDao.updateBySql(sql,null);
	}
	
	
	//根据数据日期查询断货的sku
    public Map<String,Integer> getFbaDataByDate(Date dataDate){
    	String sql ="SELECT a.`sku`,SUM(a.`fulfillable_quantity`) FROM psi_inventory_fba AS a WHERE a.`data_date`=:p1 GROUP BY a.sku";
    	List<Object[]> list = this.outOfProductDao.findBySql(sql,new Parameter(dataDate));
		Map<String,Integer> resMap= Maps.newHashMap();
		if(list!=null&&list.size()>0){
			for(Object [] obj:list){
				resMap.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
			}
		}
		return resMap;
    }

    //根据日期，查看各个国家是否有销量
    public Map<String,Set<String>> getSaleQuantityByDate(Date dataDate){
    	String sql="SELECT DISTINCT a.`country`,a.`sku`  FROM amazoninfo_sale_report AS a WHERE a.`date`=:p1 AND a.`sales_volume`!=0 ";
    	List<Object[]> list = this.outOfProductDao.findBySql(sql,new Parameter(dataDate));
		Map<String,Set<String>> resMap= Maps.newHashMap();
		if(list!=null&&list.size()>0){
			for(Object [] obj:list){
				String country=obj[0].toString();
				String sku = obj[1].toString();
				Set<String> set = null;
				if(resMap.get(country)==null){
					set = Sets.newHashSet();
				}else{
					set = resMap.get(country);
				}
				set.add(sku);
				resMap.put(country, set);
			}
		}
		return resMap;
    }
	
	//获取当前产品关系
	public Map<String,Map<String,Set<String>>> getProductSkus(){
		//产品淘汰分平台、颜色
		String sql = "SELECT DISTINCT CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END AS productColor, a.country, a.sku  FROM psi_sku AS a,`psi_product_eliminate` AS b "+
				" WHERE a.`product_name` =b.product_name AND a.country=b.country AND a.color=b.color AND b.is_sale!='4' AND a.use_barcode='1' AND a.`del_flag`='0' AND b.del_flag='0'"+
				" AND a.`product_name` NOT IN ('Inateck Old','Inateck other') AND a.`country` NOT IN ('ebay','es','it')";
		List<Object[]> list = this.outOfProductDao.findBySql(sql);
		Map<String,Map<String,Set<String>>> resMap= Maps.newHashMap();
		Map<String,Set<String>>  skuMap =this.getSkuByInventory();
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String proColor  =obj[0].toString();
				String country   =obj[1].toString();
				String sku       =obj[2].toString();
				Map<String,Set<String>> inMap = null;
				if(resMap.get(proColor)==null){
					inMap = Maps.newHashMap();
				}else{
					inMap = resMap.get(proColor);
				}
				Set<String> inSet = null;
				if(inMap.get(country)==null){
					inSet = Sets.newHashSet();
				}else{
					inSet = inMap.get(country);
				}
				inSet.add(sku);
				//获取该产品的所有库存里的sku
				String key = proColor+"_"+country;
				if(skuMap.get(key)!=null){
					inSet.addAll(skuMap.get(key));
				}
				inMap.put(country, inSet);
				resMap.put(proColor, inMap);
			}
		}
		return resMap;
	}
	
	
	//排掉以前一直没货的
	public Set<String> getHasInventoyBeforeDate(Date date){
		Set<String> setTemp = Sets.newHashSet();
		String sql="SELECT DISTINCT a.`sku` FROM psi_inventory_fba AS a WHERE a.`fulfillable_quantity`>0 AND a.`data_date`<:p1 ";
		List<String> list = this.outOfProductDao.findBySql(sql,new Parameter(date));
		if(list!=null&&list.size()>0){
			setTemp.addAll(list);
		}
		return setTemp;
	}
	
	
	//查询断货日期
	public List<AmazonOutOfProduct> getOutOfData(Date startDate,Date endDate,String endDateStr,String country){
		List<AmazonOutOfProduct> rs = Lists.newArrayList();
		String sql ="";
		List<Object[]> list =null;
		if(StringUtils.isEmpty(country)){
			sql="SELECT a.`product_name_color`,a.`country`,COUNT(*) as dasy,GROUP_CONCAT(DATE_FORMAT(a.`data_date`,'%Y/%m/%d') ORDER BY a.`data_date`) AS dateStr FROM amazoninfo_out_of_product AS a WHERE a.`data_date` BETWEEN :p1 AND :p2 GROUP BY a.`product_name_color`,a.`country`";
			list = this.outOfProductDao.findBySql(sql,new Parameter(startDate,endDate));
		}else{
			sql="SELECT a.`product_name_color`,a.`country`,COUNT(*) as dasy,GROUP_CONCAT(DATE_FORMAT(a.`data_date`,'%Y/%m/%d') ORDER BY a.`data_date`) AS dateStr FROM amazoninfo_out_of_product AS a WHERE a.`data_date` BETWEEN :p1 AND :p2 AND a.`country`=:p3 GROUP BY a.`product_name_color`";
			list = this.outOfProductDao.findBySql(sql,new Parameter(startDate,endDate,country));
		}
		if(list!=null&&list.size()>0){
			if(StringUtils.isNotEmpty(endDateStr)){
				for(Object[] obj:list){
					if(obj[3].toString().contains(endDateStr)){
						AmazonOutOfProduct outOf = new AmazonOutOfProduct(obj[0].toString(), obj[1].toString(), Integer.parseInt(obj[2].toString()), obj[3].toString());
						rs.add(outOf);
					}
				}
			}else{
				for(Object[] obj:list){
					AmazonOutOfProduct outOf = new AmazonOutOfProduct(obj[0].toString(), obj[1].toString(), Integer.parseInt(obj[2].toString()), obj[3].toString());
					rs.add(outOf);
				}
			}
		}
		return rs;
	}
	
	
	public Map<String,Set<String>> getSkuByInventory(){
		Map<String,Set<String>> map = Maps.newHashMap();
		String sql="SELECT DISTINCT a.sku ,CASE WHEN a.`color_code`='' THEN CONCAT(a.`product_name`,'_',a.`country_code`) ELSE CONCAT(a.`product_name`,'_',a.`color_code`,'_',a.`country_code`) END as proColorCountry FROM psi_inventory AS a WHERE a.`warehouse_id` IN ('21','130') AND a.`country_code` NOT IN('es','it') ";
		List<Object[]> list = this.outOfProductDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				Set<String> set = null;
				String key = obj[1].toString();
				String sku = obj[0].toString();
				if(map.get(key)==null){
					set = Sets.newHashSet();
				}else{
					set = map.get(key);
				}
				set.add(sku);
				map.put(key, set);
			}
		}
		return map;
	
	}
}
