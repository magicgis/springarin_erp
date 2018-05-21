/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.modules.amazoninfo.dao.ProductCatelogDao;
import com.springrain.erp.modules.amazoninfo.entity.ProductCatelog;
import com.springrain.erp.modules.amazoninfo.entity.ProductCatelogItem;

@Component
@Transactional(readOnly = true)
public class ProductCatelogService extends BaseService {
	
	@Autowired
	private ProductCatelogDao productCatelogDao;
	
	public ProductCatelog get(Integer id) {
		return productCatelogDao.get(id);
	}
	
	//类型 /国家 
	public Map<String,Map<String,List<ProductCatelog>>> find(ProductCatelog productCatelog) {
		DetachedCriteria dc = productCatelogDao.createDetachedCriteria();
		if(productCatelog.getQueryDate()==null){
			Date today = new Date();
			if(today.getHours()<19){
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				productCatelog.setQueryDate(DateUtils.addDays(today, -1));
			}else{
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				productCatelog.setQueryDate(today);
			}
		}
		dc.add(Restrictions.eq("queryDate",productCatelog.getQueryDate()));
		dc.addOrder(Order.desc("marketShare"));
		List<ProductCatelog> list =  productCatelogDao.find(dc);
		Map<String,Map<String,List<ProductCatelog>>> rs = Maps.newHashMap();
		for (ProductCatelog product : list) {
			String type =  product.getType();
			String country = product.getCountry();
			Map<String,List<ProductCatelog>> countrys = rs.get(type);
			if(countrys==null){
				countrys = Maps.newHashMap();
				rs.put(type, countrys);
			}
			List<ProductCatelog> lists = countrys.get(country);
			if(lists==null){
				lists = Lists.newArrayList();
				countrys.put(country, lists);
			}
			lists.add(product);
		}
		return rs;
	}
	
	public List<Object[]> findOutBrandErrorInfo(){
		String sql = "SELECT brand ,country,asins,(LENGTH(aa.asins)-LENGTH(REPLACE(aa.asins,',',''))+1) AS noprice , num  FROM  (SELECT  b.`brand`,a.`country`,GROUP_CONCAT(DISTINCT CASE WHEN b.`price`=0 THEN b.asin else null end) AS asins,a.`query_date`,COUNT(1) AS num FROM amazoninfo_type_catelog a ,amazoninfo_type_catelog_item b WHERE a.`id` = b.`catelog_id`  AND b.`me` = 0 AND b.`brand`!=''  GROUP BY b.`brand`,a.`country`,a.`query_date` HAVING num>3 AND asins IS NOT NULL) aa WHERE (LENGTH(aa.asins)-LENGTH(REPLACE(aa.asins,',',''))) >=2 AND aa.query_date =CURDATE()";
		return productCatelogDao.findBySql(sql);
	}
	
	
	public Map<String, String> findTypePcentTip(){
		Map<String, String> rs = Maps.newLinkedHashMap();
		String sql = "SELECT a.`type`,TRUNCATE(Sum(CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN a.`sales`"
				+" WHEN a.`country`='uk' THEN a.`sales`*"+MathUtils.getRate("GBP", "EUR", null)+" WHEN a.`country`='ca' THEN a.`sales`*"
				+MathUtils.getRate("CAD", "EUR", null)+" WHEN a.`country`='jp' THEN a.`sales`*"+
				MathUtils.getRate("JPY", "EUR", null)+" WHEN a.`country`='mx' THEN a.`sales`*"+MathUtils.getRate("MXN", "EUR", null)+" ELSE a.`sales`*"+MathUtils.getRate("USD", "EUR", null)+" END ),2) as sales ,SUM(a.`sales_volume`) FROM amazoninfo_sale_report_type a WHERE a.`date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY)  AND a.`order_type` = '1' GROUP BY a.`type` ORDER BY sales DESC";
		List<Object[]> list = productCatelogDao.findBySql(sql);
		
		float total = 0f ;
		for (Object[] objects : list) {
			total+=(Float.parseFloat(objects[1].toString()));
		}
		
		for (Object[] objects : list) {
			String type = objects[0].toString();
			float pcent = Float.parseFloat(objects[1].toString())*100/total;
			BigDecimal bigDecimal = new BigDecimal(pcent);
			bigDecimal = bigDecimal.setScale(0,BigDecimal.ROUND_HALF_UP);
			String tip = "Sales:"+objects[1].toString()+"€<br/>Volume:"+objects[2].toString()+"<br/>Sale Percent:"+bigDecimal.intValue()+"%";
			rs.put(type, tip);
		}
		return rs;
	}
	
	
	@Transactional(readOnly = false)
	public void save(ProductCatelog productCatelog) {
		productCatelogDao.save(productCatelog);
	}
	
	@Transactional(readOnly = false)
	public void save(List<ProductCatelog> productCatelogs) {
		productCatelogDao.save(productCatelogs);
	}
	
	
	public List<Object[]> getAsinsInCatelogs(){
		String sql = "SELECT account_name,GROUP_CONCAT(DISTINCT a.`ASIN`),MAX(a.`query_time`) AS today,a.country  FROM amazoninfo_catalog_rank a  WHERE  a.`query_time` > DATE_ADD(CURDATE(),INTERVAL -15 DAY) AND  a.`catalog_name` IS NOT NULL AND a.`rank`<=100   GROUP BY a.`account_name` HAVING today!=CURDATE() OR a.`country` IN ('ca','com')  ORDER BY a.`account_name`";
		return productCatelogDao.findBySql(sql);
	}
	
	@Transactional(readOnly = false)
	public void updateRank(String asin,String country,Map<String, String> asinNameMap,String cateId,Map<String, String> cateNames,Integer rankNum,Map<String, String> catePaths,Map<String, String> pathNames) {
		//只更新100以前的
		String sql="INSERT INTO amazoninfo_catalog_rank(asin,country,product_name,query_time,catalog,catalog_name,rank,path,path_name) " +
				" values(:p1,:p2,:p3,curdate(),:p4,:p5,:p6,:p7,:p8)" +
				" ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name),`rank` =VALUES(rank),`path` = VALUES(path),`path_name` = VALUES(path_name),`catalog_name` = VALUES(catalog_name)";
		
		productCatelogDao.updateBySql(sql, new Parameter(asin,country,asinNameMap.get(asin),cateId,cateNames.get(cateId),rankNum,catePaths.get(cateId),pathNames.get(cateId)));
	}
	
	
	//key asin_country
	public Map<String,Object[]> findOutProductsBrand(){
		String sql = "SELECT DISTINCT CONCAT(a.`asin`,'_',a.`country`),a.`product_name`,a.`brand` FROM amazoninfo_type_catelog_item a WHERE a.`me` = '0' AND a.`product_name` IS NOT NULL AND a.`brand` IS NOT NULL";
		List<Object[]> list = productCatelogDao.findBySql(sql);
		Map<String,Object[]> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(), objects);
		}
		return rs;
	} 
	
	//key asin_country
	public List<Object> find20Products(){
		String sql = "SELECT DISTINCT CONCAT(a.`asin`,'_',a.`country`) FROM amazoninfo_type_catelog_item a WHERE a.`me` = '0'";
		List<Object> list = productCatelogDao.findBySql(sql);
		return list;
	} 
	

	
	public List<ProductCatelog> initCataLogData(){
		String sql = "SELECT GROUP_CONCAT(DISTINCT CONCAT(a.`product_name`,'_',a.country)) FROM amazoninfo_catalog_rank a  WHERE  a.`query_time` = CURDATE() AND  a.`catalog_name` IS NOT NULL AND a.`rank`<=100   GROUP BY a.`country`";
		List<Object> namesObj = productCatelogDao.findBySql(sql);
		List<String> names = Lists.newArrayList();
		for (Object objects : namesObj) {
			names.addAll(Lists.newArrayList(objects.toString().split(",")));
		}
		sql = "SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`,'_',a.`country`) ,SUM(a.`sales`),sum(a.`sales_volume`) FROM amazoninfo_sale_report a WHERE a.`date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) and a.`order_type` = '1' AND CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`,'_',a.`country`) IN :p1  GROUP BY a.`product_name`,a.`color`,a.`country`";
		List<Object[]> list = productCatelogDao.findBySql(sql,new Parameter(names));
		Map<String,Object[]> sales = Maps.newHashMap();
		for (Object[] objects : list) {
			sales.put(objects[0].toString(), objects);
		}
		
		/*sql = "SELECT CONCAT(a.`type`,'_',a.`country`),a.`sales`,a.`sales_volume` FROM amazoninfo_sale_report_type a WHERE a.`date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY)  AND a.`order_type` = '1'";
		
		list = productCatelogDao.findBySql(sql);
		Map<String,Object[]> typeSales = Maps.newHashMap();
		for (Object[] objects : list) {
			typeSales.put(objects[0].toString(), objects);
		}*/
		
		//昨日均价
		sql = "SELECT CONCAT(a.`catalog_link_id`,'_',a.country),a.`avg_price` FROM amazoninfo_type_catelog a WHERE a.`query_date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) and a.`avg_price` >0";
		
		list = productCatelogDao.findBySql(sql);
		Map<String,Float> yestDayPrice = Maps.newHashMap();
		for (Object[] objects : list) {
			yestDayPrice.put(objects[0].toString(),Float.parseFloat(objects[1].toString()));
		}
		
		//30日均占有率
		sql = "SELECT CONCAT(a.`catalog_link_id`,'_',a.country),AVG(a.`market_share`) FROM amazoninfo_type_catelog a WHERE a.`query_date` >= DATE_ADD(CURDATE(),INTERVAL -30 DAY) AND  a.`market_share` >0 GROUP BY a.`catalog_link_id` ,a.`country`";
		
		list = productCatelogDao.findBySql(sql);
		Map<String,Float> msAvg30 = Maps.newHashMap();
		for (Object[] objects : list) {
			msAvg30.put(objects[0].toString(),Float.parseFloat(objects[1].toString()));
		}
		
		sql ="SELECT country,b.type,catalog,a.`catalog_name`,ROUND(100*SUM(CASE WHEN a.`rank`<=20 THEN (21-a.`rank`)/210 ELSE 0 END),2) AS zb,GROUP_CONCAT(CONCAT(a.`product_name`,'!',a.`ASIN`,'!',a.rank) ORDER BY a.rank)  FROM amazoninfo_catalog_rank a ,(SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName "+
			 " ,a.`TYPE` FROM psi_product a JOIN mysql.help_topic b "+
			 " ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1))b WHERE a.`query_time` = curdate() AND a.`catalog_name` IS NOT NULL AND a.`rank`<=100 AND a.`product_name` = b.proName GROUP BY a.`catalog`,a.`country` ORDER BY a.`country`,b.type,zb DESC";
		list = productCatelogDao.findBySql(sql);
		Date today = new Date();
		List<ProductCatelog> rs = Lists.newArrayList();
		for (Object[] objs : list) {
			String country = objs[0].toString();
			String type =  objs[1].toString();
			String catalogId =  objs[2].toString();
			String catalogName =  objs[3].toString();
			String key = catalogId+"_"+country;
			ProductCatelog productCatelog = new ProductCatelog();
			productCatelog.setCountry(country);
			productCatelog.setQueryDate(today);
			productCatelog.setCreateDate(today);
			productCatelog.setAvg30MarketShare(msAvg30.get(key));
			productCatelog.setMarketShare(Float.parseFloat(objs[4].toString()));
			productCatelog.setCatalogLinkId(catalogId);
			productCatelog.setCatalogName(catalogName);
			productCatelog.setYestdayAvgPrice(yestDayPrice.get(key));
			productCatelog.setType(type);
			
			String itemStrs = objs[5].toString();
			Float catalogSales = 0f;
			Integer catalogVolume = 0;
			for (String nameAndAsin : itemStrs.split(",")) {
				String name = nameAndAsin.split("!")[0];
				String asin = nameAndAsin.split("!")[1];
				String rankStr =  nameAndAsin.split("!")[2];
				Integer rank = null;
				try {
					rank = Integer.parseInt(rankStr);
				} catch (NumberFormatException e) {}
				
				ProductCatelogItem item = new ProductCatelogItem();
				item.setAsin(asin);
				item.setRank(rank);
				item.setProductName(name);
				item.setMe("1");
				item.setFirstTo20("0");
				item.setCountry(country);
				item.setBrand(name.substring(0,name.indexOf(" ")));
				item.setProductCatelog(productCatelog);
				productCatelog.getItems().add(item);
				Object[] data = sales.get(name+"_"+country);
				if(data!=null){
					catalogSales +=Float.parseFloat(data[1].toString());
					catalogVolume += Integer.parseInt(data[2].toString());
					item.setSales(Float.parseFloat(data[1].toString()));
					item.setSalesVolume(Integer.parseInt(data[2].toString()));
				}
			}
			productCatelog.setSales(catalogSales);
			productCatelog.setSalesVolume(catalogVolume);
			rs.add(productCatelog);
		}
		return rs;
	}

	public static String getLink(String country, String asin){
		String suff = country;
		if("uk,jp".contains(country)){
			 suff = "co."+suff;
		}else if("mx".equals(country)){
			suff = "com."+suff;
		}
		return "https://www.amazon."+suff+"/dp/"+asin;
	}
	
}
