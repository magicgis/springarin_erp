/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
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
import com.springrain.erp.common.utils.MapValueComparator;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonProduct2Dao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct2;
import com.springrain.erp.modules.amazoninfo.entity.OutOffStockDto;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.psi.dao.PsiInventoryFbaDao;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.sys.service.DictService;

/**
 * 仓库Service
 * @author tim
 * @version 2014-11-17
 */
@Component
@Transactional(readOnly = true)
public class PsiInventoryFbaService extends BaseService {
	
	@Autowired
	private PsiInventoryFbaDao 		inventoryFbaDao;
	@Autowired
	private PsiProductService 		psiProductService;
	@Autowired
	private SaleReportService 		saleReportService;
	@Autowired
	private DictService             dictService;
	@Autowired
	private AmazonProduct2Dao       amazonProduct2Dao;
	@Autowired
	private PsiProductEliminateService       eliminateService;
	
	public Page<PsiInventoryFba> find(Page<PsiInventoryFba> page, PsiInventoryFba psiInventoryFba) {
		DetachedCriteria dc = inventoryFbaDao.createDetachedCriteria();
		String country = psiInventoryFba.getCountry();
		dc.add(Restrictions.eq("country",country));
		//排除名字为other Old的产品
	/*	List<String> temp =new ArrayList<String>();
		List<String> temp1 = psiProductService.findProductSkusByName(country, "Inateck other", "");
		List<String> temp2 = psiProductService.findProductSkusByName(country, "Inateck Old", "");
		temp.addAll(temp1);
		temp.addAll(temp2);
		if(temp.size()>0){
			dc.add(Restrictions.not(Restrictions.in("sku",temp)));
		}*/
		if(psiInventoryFba.getDataDate()==null){
			Date today = new Date();
			int hour = today.getHours(); 
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			if(hour>=7){
				psiInventoryFba.setDataDate(today);
			}else{
				psiInventoryFba.setDataDate(DateUtils.addDays(today, -1));
			}
		}
		dc.add(Restrictions.eq("dataDate",psiInventoryFba.getDataDate()));
		if (StringUtils.isNotEmpty(psiInventoryFba.getSku())){
			dc.add(Restrictions.or(Restrictions.like("sku","%"+psiInventoryFba.getSku()+"%"),Restrictions.like("asin","%"+psiInventoryFba.getSku()+"%")));
		}
		return inventoryFbaDao.find(page, dc);
	}
	
	
	public Page<PsiInventoryFba> findAllCountry(Page<PsiInventoryFba> page, PsiInventoryFba psiInventoryFba) {
		DetachedCriteria dc = inventoryFbaDao.createDetachedCriteria();
		//排除名字为other的产品
		//List<String> temp = psiProductService.findProductSkusByName("Inateck other");
		/*List<String> temp =new ArrayList<String>();
		List<String> temp1 = psiProductService.findProductSkusByName("Inateck other");
		List<String> temp2 = psiProductService.findProductSkusByName("Inateck Old");
		temp.addAll(temp1);
		temp.addAll(temp2);
		if(temp.size()>0){
			dc.add(Restrictions.not(Restrictions.in("sku",temp)));
		}*/
		if(psiInventoryFba.getDataDate()==null){
			Date today = new Date();
			int hour = today.getHours(); 
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			if(hour>=7){
				psiInventoryFba.setDataDate(today);
			}else{
				psiInventoryFba.setDataDate(DateUtils.addDays(today, -1));
			}
		}
		dc.add(Restrictions.eq("dataDate",psiInventoryFba.getDataDate()));
		if (StringUtils.isNotEmpty(psiInventoryFba.getSku())){
			dc.add(Restrictions.or(Restrictions.like("sku","%"+psiInventoryFba.getSku()+"%"),Restrictions.like("asin","%"+psiInventoryFba.getSku()+"%")));
		}
		return inventoryFbaDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(List<PsiInventoryFba> inventoryFba) {
		inventoryFbaDao.save(inventoryFba);
	}
	
	@Transactional(readOnly = false)
	public void save(List<PsiInventoryFba> inventoryFba,String country) {
		inventoryFbaDao.save(inventoryFba);
	}
	
	public Date getMaxDataDate(){
		String sql = "SELECT MAX(data_date) FROM psi_inventory_fba";
		List<Object> list = inventoryFbaDao.findBySql(sql);
		if(list.size()==1){
			return (Date)list.get(0);
		}
		return null;
	}
	
	public Integer getMaxId(){
		String sql = "SELECT MAX(id) FROM psi_inventory_fba";
		List<Object> list = inventoryFbaDao.findBySql(sql);
		if(list!=null && list.size()==1){
			Integer rs =  (Integer)list.get(0);
			rs = (rs==null?0:rs);
			return rs;
		}
		return 0;
	}
 	
	
	public List<String> findHasInventory() {
		Date date = getMaxDataDate();
		if(date != null ){
			String sql = "SELECT DISTINCT a.`sku` FROM psi_inventory_fba a ,psi_sku b WHERE a.`sku` = b.`sku` AND  a.`fulfillable_quantity`>0 AND NOT(b.`product_name` LIKE '%other%' or b.`product_name` LIKE '%Old%') AND b.`del_flag` ='0' and a.`data_date` = :p1";
			List<String> rs = inventoryFbaDao.findBySql(sql,new Parameter(date));
			return rs;
		}
		return null;
	}
	
	
	public List<Object[]> findAllPsiInventoryFba(PsiInventoryFba psiInventoryFba){
		String sql="SELECT c.brand,c.name,f.`country`,f.`sku`,f.`fnsku`,f.`asin`,f.`fulfillable_quantity`," +
				"f.`unsellable_quantity`,f.`reserved_quantity`,f.`orrect_quantity`,f.`warehouse_quantity`,f.`transit_quantity`,f.`total_quantity`" +
				"FROM psi_inventory_fba f " +
				"left JOIN (SELECT DISTINCT a.`sku`,p.`brand`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) AS NAME,a.country " +
				"FROM psi_sku a JOIN psi_product p  ON a.`product_id`=p.id" +
				" WHERE  a.`sku` !=''  AND a.`del_flag`='0' and p.del_flag='0' "+
				//" AND a.`product_name` NOT LIKE '%Inateck other%' AND a.`product_name` NOT LIKE '%Old%' " +
				" ) c ON f.`sku`=c.sku and f.country=c.country where f.`data_date`=:p1 ";
		if(psiInventoryFba.getDataDate()==null){
			Date today = new Date();
			int hour = today.getHours(); 
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			if(hour>=7){
				psiInventoryFba.setDataDate(today);
			}else{
				psiInventoryFba.setDataDate(DateUtils.addDays(today, -1));
			}
		}
		if (StringUtils.isNotEmpty(psiInventoryFba.getSku())){
			sql+=" and (f.sku like '%"+psiInventoryFba.getSku()+"%' or f.asin like '%"+psiInventoryFba.getSku()+"%' ) ";
		}
		sql+=" order by c.name,f.country ";
		return inventoryFbaDao.findBySql(sql,new Parameter(psiInventoryFba.getDataDate()));
	}
	
	
	/**
	 *@author Michael 2015-08-25 
	 * 
	 */
	
	public Map<String,Integer> getFbaInventroyData(Set<String> countrys){
		Map<String,Integer> maps =Maps.newHashMap();
		String sql ="SELECT a.`sku`,a.`fulfillable_quantity` FROM psi_inventory_fba AS a WHERE  a.`data_date`= (SELECT MAX(a.`data_date`) FROM psi_inventory_fba AS a ) AND  a.`country` in :p1 AND a.`fulfillable_quantity`>0 ";
		List<Object[]> objs=this.inventoryFbaDao.findBySql(sql,new Parameter(countrys));
		for(Object[] obj:objs){
			maps.put(obj[0].toString(), (Integer)obj[1]);
		}
		return maps;
	}
	
	public Map<String,Map<String,Integer>> getFbaInventroyDataByAsin(){
		Map<String,Map<String,Integer>>  maps =Maps.newHashMap();
		String sql ="SELECT country,a.asin,SUM(a.`fulfillable_quantity`) FROM psi_inventory_fba AS a WHERE  a.`data_date`= (SELECT MAX(a.`data_date`) FROM psi_inventory_fba AS a ) AND a.`fulfillable_quantity`>0 GROUP BY country,a.`asin`";
		List<Object[]> objs=this.inventoryFbaDao.findBySql(sql);
		for(Object[] obj:objs){
			Map<String,Integer> temp=maps.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				maps.put(obj[0].toString(), temp);
			}
			temp.put(obj[1].toString(), ((BigDecimal)obj[2]).intValue());
			
			Map<String,Integer> euTemp=maps.get("eu");
			if(euTemp==null){
				euTemp=Maps.newHashMap();
				maps.put("eu", euTemp);
			}
			euTemp.put(obj[1].toString(), ( (BigDecimal)obj[2]).intValue()+(euTemp.get(obj[1].toString())==null?0:euTemp.get(obj[1].toString())) );
		}
		return maps;
	}
	
	
	public Map<String,Integer> getFbaInventroyWarningData(Set<String> skus){
		Map<String,Integer> maps =Maps.newHashMap();
		String sql ="SELECT a.`sku`,a.`fulfillable_quantity` FROM psi_inventory_fba AS a WHERE  a.`data_date`= (SELECT MAX(a.`data_date`) FROM psi_inventory_fba AS a ) AND  a.`sku` not in :p1 ";
		List<Object[]> objs=this.inventoryFbaDao.findBySql(sql,new Parameter(skus));
		for(Object[] obj:objs){
			maps.put(obj[0].toString(), (Integer)obj[1]);
		}
		return maps;
	}
	
	/**
	 *一个sku的最近库存 
	 */
	public Map<String,Integer> getFbaInventroy(Set<String> skus){
		Map<String,Integer> maps =Maps.newLinkedHashMap();
		String sql ="SELECT a.`sku`,SUM(a.`fulfillable_quantity`) FROM psi_inventory_fba AS a 	WHERE  a.`data_date`= (SELECT MAX(a.`data_date`) FROM psi_inventory_fba AS a ) AND a.`sku` IN :p1 GROUP BY a.`sku` ORDER BY a.`country`,a.`sku` ";
		List<Object[]> objs=this.inventoryFbaDao.findBySql(sql,new Parameter(skus));
		for(Object[] obj:objs){
			maps.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
		}
		return maps;
	}
	
	
	
	//获得30天销量
	public Map<String,Integer> get30DaysSales(List<String> noSaleSku,String country,Date referenceDate){
		//有产品名，颜色和国家获得绑定的sku，没绑定的就用当前的
		Map<String,String> skuBadingMap =  this.psiProductService.getAllBandingSku();
		 Map<String,Integer>  resMap = Maps.newHashMap();
		 String sql ="SELECT CONCAT(a.`product_name`,',',a.`country`,',',a.`color`) AS pro,a.`sku`,SUM(a.`sales_volume`) AS b FROM amazoninfo_sale_report AS a  WHERE  a.order_type='1' and a.`date` BETWEEN DATE_ADD(:p1,INTERVAL -31 DAY ) AND DATE_ADD(:p1,INTERVAL -1 DAY) AND a.`sku` not in :p2  ";
		 Parameter para =null;
		 if(StringUtils.isNotEmpty(country)){
			sql+=" AND a.`country` =:p3 ";
			para=new Parameter(referenceDate,noSaleSku,country);
		 }else{
			para=new Parameter(referenceDate,noSaleSku);
		 }
		 sql+="  GROUP BY a.`product_name`,a.`color`,a.`country`";
	 
	    List<Object[]> list = this.inventoryFbaDao.findBySql(sql,para);
		for(Object[] obj:list){
			if(obj!=null&&obj[0]!=null){
				String sku = obj[1].toString();
				if(skuBadingMap.containsKey(obj[0].toString())){
					sku = skuBadingMap.get(obj[0].toString());
				}
				Integer res = Integer.parseInt(obj[2].toString());
				//有可能同一个sku在多个平台
				if(resMap.containsKey(sku)){
					res+=resMap.get(sku);
				}
				resMap.put(sku, res);
			}
		}
		return resMap;
	}
	
	//获得31天销量  发信  key:sku  value：数量
	public Map<String,Integer> get31DaysSales(List<String> noSaleSku){
		 Map<String,Integer>  resMap = Maps.newHashMap();
		 String sql ="";
	     List<Object[]> list = null;
	     if(noSaleSku!=null){
	    	// sql ="SELECT  a.sku,SUM(a.`sales_volume`) AS b FROM amazoninfo_sale_report AS a  WHERE a.order_type='1' and a.`date` BETWEEN DATE_ADD(CURDATE(),INTERVAL -31 DAY ) AND DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND a.`sku` not in :p1  GROUP BY a.sku";
	    	 sql="SELECT a.`day31_sales`,bb.`sku` FROM psi_product_variance AS a,(SELECT b.sku,(CASE WHEN b.`color_code` ='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END) AS productName,b.`country_code` FROM psi_inventory AS b where b.`sku` NOT IN :p1 GROUP BY b.`sku`)   AS bb" +
		 				" WHERE a.`country`=bb.`country_code`AND a.`product_name`=bb.productName ";
	    	 list = this.inventoryFbaDao.findBySql(sql,new Parameter(noSaleSku));
	     }else{
	    	 //sql ="SELECT  a.sku,SUM(a.`sales_volume`) AS b FROM amazoninfo_sale_report AS a  WHERE a.order_type='1' and a.`date` BETWEEN DATE_ADD(CURDATE(),INTERVAL -31 DAY ) AND DATE_ADD(CURDATE(),INTERVAL -1 DAY) GROUP BY a.sku";
	    	 sql="SELECT a.`day31_sales`,bb.`sku` FROM psi_product_variance AS a,(SELECT b.sku,(CASE WHEN b.`color_code` ='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END) AS productName,b.`country_code` FROM psi_inventory AS b GROUP BY b.`sku`)   AS bb" +
	 				" WHERE a.`country`=bb.`country_code`AND a.`product_name`=bb.productName ";
	    	 list = this.inventoryFbaDao.findBySql(sql);
	     }
		 for(Object[] obj:list){
			if(obj!=null&&obj[0]!=null){
				String sku = obj[1].toString();
				resMap.put(sku, Integer.parseInt(obj[0].toString()));
			}
		 }
		return resMap;
	}
	
	
	//获得31天销量  发信  key:sku  value：数量
	public Map<String,Integer> get31SalesQuantity(Set<String> adSkus){
		 Map<String,Integer>  resMap = Maps.newHashMap();
	     List<Object[]> list = null;
    	 String sql="SELECT a.`day31_sales`,bb.`sku` FROM psi_product_variance AS a,(SELECT b.sku,(CASE WHEN b.`color_code` ='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END) AS productName,b.`country_code` FROM psi_inventory AS b WHERE b.`sku` IN :p1 GROUP BY b.`sku`)   AS bb" +
 				" WHERE a.`country`=bb.`country_code`AND a.`product_name`=bb.productName  ";
    	 list = this.inventoryFbaDao.findBySql(sql,new Parameter(adSkus));
		 for(Object[] obj:list){
			if(obj!=null&&obj[0]!=null){
				String sku = obj[1].toString();
				resMap.put(sku, Integer.parseInt(obj[0].toString()));
			}
		 }
		return resMap;
	}
	
	
	//fba总库存数  发信
	public Map<String,String> getFbaInventroy(List<String> noSaleSku,Date dataDate){
		Map<String,String> maps =Maps.newHashMap();
		String sql = "SELECT a.`data_date` FROM psi_inventory_fba AS a ORDER BY a.`data_date` DESC LIMIT 1";
		List<Date> dates=this.inventoryFbaDao.findBySql(sql);
		if(dates!=null&&dates.size()>0){
			Date date  = dates.get(0);
			sql ="SELECT a.`sku`,a.country,a.`fulfillable_quantity`,a.`transit_quantity`, CASE WHEN a.`orrect_quantity` IS NOT NULL THEN (a.`fulfillable_quantity`+a.`orrect_quantity`+a.`transit_quantity`) ELSE (a.`fulfillable_quantity`+a.`transit_quantity`) END AS totalQuantity,a.`asin` FROM psi_inventory_fba AS a WHERE a.`data_date`=:p1 ";
			List<Object[]> objs=null;
			if(noSaleSku!=null){
				sql+=" AND a.`sku` not in :p2 ";
				objs=this.inventoryFbaDao.findBySql(sql,new Parameter(date,noSaleSku));
			}else{
				objs=this.inventoryFbaDao.findBySql(sql,new Parameter(date));
			}
			for(Object[] obj:objs){
				maps.put(obj[0].toString()+","+obj[1].toString(), obj[2].toString()+","+ obj[3].toString()+","+ obj[4].toString()+","+ obj[5].toString());
			}
		}
		return maps;
	}
	
	//fba库存数
	public Map<String,Integer> getFbaInventroy(List<String> noSaleSku,String country,Date referenceDate){
		Map<String,Integer> maps =Maps.newHashMap();
		String sql ="SELECT a.`sku`,a.`fulfillable_quantity` FROM psi_inventory_fba AS a where a.`data_date`=:p1 AND a.`sku` not in :p2 ";
		 Parameter para =null;
		 if(StringUtils.isNotEmpty(country)){
			sql+=" AND a.`country` =:p3 ";
			para=new Parameter(referenceDate,noSaleSku,country);
		 }else{
			para=new Parameter(referenceDate,noSaleSku);
		 }
		 
		List<Object[]> objs=this.inventoryFbaDao.findBySql(sql,para);
		for(Object[] obj:objs){
			maps.put(obj[0].toString(), (Integer)obj[1]);
		}
		return maps;
	}
	
	
	//查出淘汰的
	public List<String> getNoSaleSku(){
		//产品淘汰分平台、颜色 sku表中country字段ebay按照de处理
		String sql ="SELECT DISTINCT b.`sku` FROM psi_product_eliminate AS a, psi_sku AS b WHERE a.`del_flag`='0' AND b.`del_flag`='0' AND"+
					" a.`product_name`=b.`product_name` AND a.`country`=CASE WHEN b.`country` ='ebay' THEN 'de' ELSE b.`country` END AND a.`color`=b.`color` AND a.`is_sale`='4' ";
		List<String> list= this.inventoryFbaDao.findBySql(sql);
		
		return list;
	}
	
	/**
	 * 获得7天30天60天       或者90天   120天150天的销量
	 */
	public Map<String,String[]> getPeriodSales(Integer type,Set<String> overSku,Date referenceDate){
		 Map<String,String[]>  resMap = Maps.newHashMap();
		 String appStr = "";
		 if(type.equals(3)){
			 appStr+="  a.`date` BETWEEN DATE_ADD(:p2,INTERVAL -90 DAY ) AND DATE_ADD(:p2,INTERVAL -1 DAY )  ";
		 }else if(type.equals(4)){
			 appStr+=" a.`date` BETWEEN DATE_ADD(:p2,INTERVAL -120 DAY ) AND DATE_ADD(:p2,INTERVAL -1 DAY )  ";
		 }else if(type.equals(5)){
			 appStr+=" a.`date` BETWEEN DATE_ADD(:p2,INTERVAL -150 DAY ) AND DATE_ADD(:p2,INTERVAL -1 DAY )  ";
		 }
		 String sql =" SELECT a.`sku`,SUM( CASE WHEN a.`date` BETWEEN DATE_ADD(:p2,INTERVAL -7 DAY ) AND DATE_ADD(:p2,INTERVAL -1 DAY )  THEN a.`sales_volume` ELSE 0 END ) AS a,SUM( CASE WHEN a.`date` BETWEEN DATE_ADD(:p2,INTERVAL -30 DAY ) AND DATE_ADD(:p2,INTERVAL -1 DAY )  THEN a.`sales_volume` ELSE 0 END ) AS b," +
		 		" SUM( CASE WHEN a.`date` BETWEEN DATE_ADD(:p2,INTERVAL -60 DAY ) AND DATE_ADD(:p2,INTERVAL -1 DAY )  THEN a.`sales_volume` ELSE 0 END ) AS c ,SUM( CASE WHEN a.`date` BETWEEN DATE_ADD(:p2,INTERVAL -90 DAY ) AND DATE_ADD(:p2,INTERVAL -1 DAY )  THEN a.`sales_volume` ELSE 0 END ) AS e, SUM(a.`sales_volume`)"+ 
		 		", a.country FROM amazoninfo_sale_report AS a  where a.order_type='1' and "+appStr+" AND a.`sku` in :p1 GROUP BY a.`sku` ";
		List<Object[]> list = this.inventoryFbaDao.findBySql(sql,new Parameter(overSku,referenceDate));
		for(Object[] obj:list){
			if(obj!=null){
				resMap.put(obj[0].toString(), new String []{obj[1].toString(),obj[2].toString(),obj[3].toString(),obj[4].toString(),obj[5].toString(),obj[6].toString()});
			}
		}
		return resMap;
	}
	
	
	/**
	 * 查出所有断货的sku
	 */
	public Map<String,String> getOutOfStockSku(Date startDate,Date endDate,String country,List<String> noSaleSku){
		Map<String,String> skuCountryMap= Maps.newHashMap();
		//如果产品原来一个都没买过，就排除
		String sql="SELECT b.`sku` FROM psi_inventory_fba AS b WHERE b.`fulfillable_quantity`>0 AND b.`data_date`>=DATE_ADD(:p2,INTERVAL -3 MONTH) AND  b.`data_date`<=:p2  AND b.`sku` NOT IN :p1  GROUP BY b.`sku`";
		List<String> skus= this.inventoryFbaDao.findBySql(sql,new Parameter(noSaleSku,startDate));
		if(skus!=null&&skus.size()>0){
			sql ="SELECT a.`sku`,a.`country` FROM psi_inventory_fba AS a WHERE a.`fulfillable_quantity`=0 AND a.sku in :p3  AND  a.`data_date`>=:p1 AND a.`data_date`<=:p2  ";
			Parameter para =null;
			if(StringUtils.isNotEmpty(country)){
				sql+=" AND a.`country` =:p4 ";
				para=new Parameter(startDate,endDate,skus,country);
			}else{
				para=new Parameter(startDate,endDate,skus);
			}
			sql+=" GROUP BY a.`sku`";
			List<Object[]> list= this.inventoryFbaDao.findBySql(sql,para);
			if(list!=null&&list.size()>0){
				for(Object [] obj:list){
					skuCountryMap.put(obj[0].toString(), obj[1].toString());
				}
			}
		}
		
		return skuCountryMap;
	}
	
	/**
	 * 查询今天断货的sku  key:国家  value：sku
	 */
	public Map<String,List<String>> getOutOfStockSku(Set<String> noUsedSkuCountry,Date dataDate){
		Map<String,List<String>> outMap =Maps.newHashMap();
		//如果产品原来一个都没买过，就排除
		//String sql="SELECT b.`sku` FROM psi_inventory_fba AS b WHERE b.`fulfillable_quantity`>0 AND b.`data_date`>=DATE_ADD(DATE_FORMAT(SYSDATE(),'%Y-%m-%d'),INTERVAL -3 MONTH)   AND b.`sku` NOT IN :p1  GROUP BY b.`sku`";
		String sql="SELECT b.`sku` FROM psi_inventory_fba AS b WHERE b.`fulfillable_quantity`>0 AND b.`data_date`>=DATE_ADD(DATE_FORMAT(SYSDATE(),'%Y-%m-%d'),INTERVAL -3 MONTH)   GROUP BY b.`sku`";
		List<String> skus= this.inventoryFbaDao.findBySql(sql);
		if(skus!=null&&skus.size()>0){
			sql ="SELECT a.`sku`,a.`country` FROM psi_inventory_fba AS a WHERE a.`fulfillable_quantity`=0  AND  a.`data_date`=:p1 AND a.sku in :p2 ";
			List<Object[]> list= this.inventoryFbaDao.findBySql(sql,new Parameter(dataDate,skus));
			if(list!=null&&list.size()>0){
				for(Object[] obj:list){
					List<String> skustemp = Lists.newArrayList();
					String sku = obj[0].toString();
					String country = obj[1].toString();
					String skuCountry=sku+","+country;
					if("com,ca".contains(country)&&!noUsedSkuCountry.contains(skuCountry)){
						//如果是美国、加拿大  过滤掉不使用的sku
						continue;
					}
					if(outMap.get(country)!=null){
						skustemp=outMap.get(country);
					}
					
					skustemp.add(sku);
					outMap.put(country, skustemp);
				}
			}
		}
		return outMap;
	}
	
	/**
	 * 查询断货天数   先查 断货sku的哪几天不断货，有可能都断货     发信用 key:sku,value:断货时间段
	 */
	public Map<String,String> getOutOfStockMail(Date startDate,Date endDate,Set<String> outOfSku){
		Set<String> curSet = Sets.newHashSet();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Map<String,String>  resMap = Maps.newHashMap();
			String sql ="SELECT a.`sku`,GROUP_CONCAT(DATE_FORMAT(a.`data_date`,'%Y/%m/%d') ORDER BY a.`data_date`) AS date_str,a.country FROM psi_inventory_fba AS a WHERE a.`fulfillable_quantity`>0  AND  a.`data_date`>=:p1  AND a.sku in :p2  GROUP BY a.`sku` ";
			List<Object[]> list = this.inventoryFbaDao.findBySql(sql,new Parameter(startDate,outOfSku));
			
			for(Object[] obj:list){
				if(obj!=null){
					List<String> arrStr=DateUtils.toReverseInterval(startDate, endDate, obj[1].toString().split(","));
					//sku帮错，有可能导致该sku一直断货，查不出数据
					if(arrStr!=null&&arrStr.size()>0){
						String dateStr=arrStr.get(arrStr.size()-1);
						resMap.put(obj[0].toString(),dateStr);
						curSet.add(obj[0].toString());
					}else{
						resMap.put(obj[0].toString(),"");
						curSet.add(obj[0].toString());
					}
					
				}
			}
		//如果断货sku里的没断货天数没数据，说明完全断货
		for(String sku:outOfSku){
			if(!curSet.contains(sku)){
				String	dateStr=sdf.format(startDate)+"-"+sdf.format(endDate);
				resMap.put(sku,dateStr);
				
			}
		}
		return resMap;
	}
	
	/**
	 * 查询断货天数   先查 断货sku的哪几天不断货，有可能都断货
	 * @throws ParseException 
	 */
	public Map<String,OutOffStockDto> getOutOfStock(Date startDate,Date endDate,long peroidDays,String country,Map<String,String> outOfMap,String isCheck) throws ParseException{
		Set<String> curSet = Sets.newHashSet();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Map<String,OutOffStockDto>  resMap = Maps.newLinkedHashMap();
		if(!"1".equals(peroidDays+1+"")){
			String sql ="SELECT a.`sku`,GROUP_CONCAT(DATE_FORMAT(a.`data_date`,'%Y/%m/%d') ORDER BY a.`data_date`) AS date_str,(:p3-COUNT(1)),a.country FROM psi_inventory_fba AS a WHERE a.`fulfillable_quantity`>0  AND  a.`data_date`>=:p1 AND a.`data_date`<=:p2 AND a.sku in :p4 ";
			Parameter para =null;
			if(StringUtils.isNotEmpty(country)){
				sql+=" AND a.`country` =:p5 ";
				para=new Parameter(startDate,endDate,peroidDays+1,outOfMap.keySet(),country);
			}else{
				para=new Parameter(startDate,endDate,peroidDays+1,outOfMap.keySet());
			}
			//sql+=" GROUP BY a.`sku` HAVING (:p3-COUNT(1))>0 order by (:p3-COUNT(1)) ,country";
			sql+=" GROUP BY a.`sku` order by (:p3-COUNT(1)) ,country";
			List<Object[]> list = this.inventoryFbaDao.findBySql(sql,para);
			
			for(Object[] obj:list){
				if(obj!=null){
					String offStr="";
					String sku  = obj[0].toString();
					curSet.add(sku);
					List<String> dateList = DateUtils.toReverseInterval(startDate, endDate, obj[1].toString().split(","));
					
					//sku帮错，有可能导致该sku一直断货，查不出数据
					if(dateList!=null&&dateList.size()>0){
						if("1".equals(isCheck)){
							//如果选中只看到今天断货的，
							String dateStr=dateList.get(dateList.size()-1);
							if(dateStr.contains(sdf.format(endDate))){
								if(dateStr.split("-").length>1){
									Date	sDate = sdf.parse(dateStr.split("-")[0]);
									Date	eDate = sdf.parse(dateStr.split("-")[1]);
									resMap.put(obj[0].toString(),new OutOffStockDto(obj[0].toString(), null, dateStr, null, Integer.parseInt(DateUtils.spaceDays(sDate, eDate)+""),obj[3].toString()));
								}else{
									resMap.put(obj[0].toString(),new OutOffStockDto(obj[0].toString(), null, sdf.format(endDate), null, 1,obj[3].toString()));
								}
							}
						}else{
							for(String str:dateList){
								offStr+=str+",";
							}
							resMap.put(obj[0].toString(),new OutOffStockDto(obj[0].toString(), null, offStr.substring(0,offStr.length()-1), null, Integer.parseInt(obj[2].toString()),obj[3].toString()));
						}
					}
				}
			}
		}
		//如果断货sku里的没断货天数没数据，说明完全断货
		for(Map.Entry<String, String> entry:outOfMap.entrySet()){
			String sku = entry.getKey();
			if(!curSet.contains(sku)){
				String dateStr = "";
				if("1".equals(peroidDays+1+"")){
					dateStr=sdf.format(endDate);
				}else{
					dateStr=sdf.format(startDate)+"-"+sdf.format(endDate);
				}
				if(StringUtils.isNotEmpty(country)){
					resMap.put(sku,new OutOffStockDto(sku, null,dateStr, null, Integer.parseInt(peroidDays+1+""),country));
				}else{
					resMap.put(sku,new OutOffStockDto(sku, null,dateStr, null, Integer.parseInt(peroidDays+1+""),entry.getValue()));
				}
				
			}
		}
		
		
		return resMap;
	}
	
	
	
	/**
	 *获取所有产品的价格    key:sku,国家
	 */
	public Map<String,Float> getSalesPrice(Set<String> salesSku,String country){
		Map<String,Float> resMap = Maps.newHashMap();
		List<Integer> priceIds = Lists.newArrayList();
		String sql ="";
		Parameter para =null;
		if(StringUtils.isNotEmpty(country)){
			sql = "SELECT MAX(a.`id`) FROM amazoninfo_product_history_price AS a WHERE a.`sale_price`>0 AND a.`sku` IN :p1 AND a.country=:p2  GROUP BY a.`sku`,country  ";
			para=new Parameter(salesSku,country);
		}else{
			sql = "SELECT MAX(a.`id`) FROM amazoninfo_product_history_price AS a WHERE a.`sale_price`>0 AND a.`sku` IN :p1 GROUP BY a.`sku`,country";
			para=new Parameter(salesSku);
		}
		priceIds =this.inventoryFbaDao.findBySql(sql,para);
		
		sql ="SELECT  b.sku,b.`country`,b.`sale_price` FROM  amazoninfo_product_history_price AS b WHERE   b.id IN :p1";
		List<Object[]> list = this.inventoryFbaDao.findBySql(sql,new Parameter(priceIds));
		for(Object[] obj:list){
			if(obj!=null){
				resMap.put(obj[0].toString()+","+obj[1], obj[2]==null?null:this.changeToUSD(obj[1].toString(),Float.parseFloat(obj[2].toString())));
			}
		}
		return resMap;
	}
	
	private Float changeToUSD(String country,Float price){
		Float resPrice =0f;
		if("de,fr,es,it".contains(country)){
			resPrice= price*AmazonProduct2Service.getRateConfig().get("EUR/USD");
		}else if("com".equals(country)){
			resPrice=price;
		}else if("uk".equals(country)){
			resPrice= price*AmazonProduct2Service.getRateConfig().get("GBP/USD");
		}else if("jp".equals(country)){
			resPrice= price*AmazonProduct2Service.getRateConfig().get("JPY/USD");
		}else if("ca".equals(country)){
			resPrice= price*AmazonProduct2Service.getRateConfig().get("CAD/USD");
		}else if("mx".equals(country)){
			resPrice= price*AmazonProduct2Service.getRateConfig().get("MXN/USD");
		}else{
			resPrice=price;
		}
		return resPrice;
	}
	
	/**
	 * 获得积压断货和即将断货的map
	 * @throws ParseException 
	 * refOverMap;     key:country  value:sku,name,sales30,fbaQuantiy;
	 * refoutOfMap;    key:country  value:sku,name,quantum,断货日期;
	 */
	
	public void getOverAndOutOfStock(Map<String,List<String>> refOutOfMap,Map<String,List<String>> refOverMap) throws ParseException{
			//查询帖子是否正常
			Set<String> postsSet=this.getPostInfo();
			//获取fba仓库最新的日期
			Date dataDate =getLastDateFbaStock();
			Map<String,String> skuMap = this.psiProductService.findAllProductNamesWithSku();
			Map<String,Set<String>> zeroProMap = this.getStockZero();
			Set<String> sameAsinSet = this.getEuZeroGroupByAsin(dataDate);
			//不销售的sku
			List<String> noSaleSkus= this.getNoSaleSku();
			Set<String> noSaleSkuSets = Sets.newHashSet();
			noSaleSkuSets.addAll(noSaleSkus);
//			//查询所有的fba库存 key:sku+国家 key:fba实+在途+fba总
			Map<String,String> fbaMap = this.getFbaInventroy(null,dataDate);
//			//查询30天销量  key:sku
//			Map<String,Integer> sale30Map = this.get30DaysSales(null);
//			
			 //依产品名颜色为粒度的库存		key:国家  value名字、数量
			Map<String,Map<String,String>> nameColorfbaMap = getFbaInventoryMap(dataDate,null);
			//从方差表获得最近一个月的销量
			Map<String,Map<String,Integer>> sale31Map=get31SalesMap(null);
			
			Map<String,Set<String>> notSaleOrNewProColorMap = this.eliminateService.getNotSaleOrNewProduct();
			
			//遍历amazon2表
			List<AmazonProduct2>  amaList = amazonProduct2Dao.findAll();
			//目前还在使用的sku
			Map<String,String>  skuAsinMap = Maps.newHashMap();
			Set<String>  usedSkuCountry =Sets.newHashSet();
			//asin对应的sku及国家价格
			Map<String,Map<String,List<String>>> asinMap = Maps.newHashMap();
			Map<String,Boolean>  asinHasPriceEu = Maps.newHashMap();
			for(AmazonProduct2 pro:amaList){
				String sku 		= pro.getSku();
				String asin 	= pro.getAsin();
				String active 	= pro.getActive();
				String country = pro.getCountry();
				String isFba   = pro.getIsFba();
				String price =pro.getSalePrice()==null?" ":(pro.getSalePrice()+" ");
				if("1".equals(active)&&"1".equals(isFba)){
					if(skuAsinMap.get(sku)==null){
						skuAsinMap.put(sku, asin);
					}
					String skuCountry =sku+","+country; 
					usedSkuCountry.add(skuCountry);
					if(!"jp,ca,com,mx,".contains(country+",")){
						Map<String,List<String>> innerSkuMap =null;
						if(asinMap.get(asin)==null){
							innerSkuMap =Maps.newHashMap();
						}else{
							innerSkuMap=asinMap.get(asin);
						}
						List<String> innerList=null;
						if(innerSkuMap.get(sku)==null){
							innerList=Lists.newArrayList();
						}else{
							innerList=innerSkuMap.get(sku);
						}
						String countryPrice =country+";"+price;
						innerList.add(countryPrice);
						innerSkuMap.put(sku, innerList);
						asinMap.put(asin, innerSkuMap);
						boolean hasPrice=false;
						if(StringUtils.isNotEmpty(price.trim())){
							hasPrice=true;
						}
						//只要有一次有价格
						if(asinHasPriceEu.get(asin)==null||!asinHasPriceEu.get(asin)){
							asinHasPriceEu.put(asin, hasPrice);
						}
					}
				}
			}
			
					
			//处理积压、断货
			Map<String,List<String>> overMap = this.overStock(sale31Map, nameColorfbaMap,notSaleOrNewProColorMap,3);
			//淘汰品在断货里面
			Map<String,List<String>> outOfMap = this.getOutOfStockSku(usedSkuCountry,dataDate);
			
			//查询断货的时间段
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date endDate = sdf.parse(sdf.format(new Date()));
			Date startDate = DateUtils.addMonths(endDate, -2);
			Map<String,String> dateMap= Maps.newHashMap();
			if(outOfMap!=null&&outOfMap.size()>0){
				//获取所有断货sku
				Set<String> outSku = Sets.newHashSet();
				for(Map.Entry<String, List<String>> entry:outOfMap.entrySet()){
					outSku.addAll(entry.getValue());
				}
				dateMap=this.getOutOfStockMail(startDate,endDate,outSku);
			}
			
			List<String> countrys = dictService.findByType("platform");
			for(String country:countrys){
				if(overMap.get(country)!=null){
					for(int i =0;i<overMap.get(country).size();i++){
						String name=overMap.get(country).get(i);
						String sales30=(sale31Map.get(country)!=null&&sale31Map.get(country).get(name)!=null)?sale31Map.get(country).get(name)+"":"";
						String fbaQuantiy=nameColorfbaMap.get(country).get(name).split(",")[0]+"";
						List<String> resInfos = null;
						if(refOverMap.get(country)==null){
							resInfos = Lists.newArrayList();
						}else{
							resInfos=refOverMap.get(country);
						}
						String resInfo = name+","+sales30+","+fbaQuantiy;
						resInfos.add(resInfo);
						refOverMap.put(country, resInfos);
					}
				}
				
				Map<String,Set<String>> selfZeroMap = this.getSelfStockZero();
				
				if(outOfMap.get(country)!=null){
					Set<String> totalStock = zeroProMap.get(country);
					Set<String> setZero = selfZeroMap.get(country);
					//如果这个sku的替换sku没断货     不显示
					Map<String,Set<String>> changeSkuMap = this.getChangeSkuMap();
					for(int i =0;i<outOfMap.get(country).size();i++){
						String sku = outOfMap.get(country).get(i);
						String name=skuMap.get(sku)==null?sku+"(没匹配)":skuMap.get(sku);
						String asin = skuAsinMap.get(sku);
						//如果是泛欧产品 并且帖子状态为正常
						if("fr,it,es,uk".contains(country)&&this.saleReportService.getPanEuProduct().contains(name)&&postsSet.contains(country+"_"+asin)){
							continue;    
						}
						
						//如果亚马逊库存为0并且31天销量为0或者为空
						if(totalStock!=null&&totalStock.contains(name)&&(sale31Map.get(country)==null||sale31Map.get(country).get(name)==null||sale31Map.get(country).get(name).intValue()==0)){
							continue;
						}
						//欧洲国家     sku对应的asin找不到   或者  是淘汰品如果整个eu都没价格 不显示
						if(!"jp,ca,com,mx,".contains(country)&&(asinHasPriceEu.get(asin)==null||sameAsinSet.contains(asin)||(noSaleSkuSets.contains(sku)&&(!asinHasPriceEu.get(asin))))){
							continue;
						}
						//如果是美国产品   淘汰产品断货一个月的不要      如果美国仓还有货的 就不算断货   前面那个SKU不再使用了 也不需要再抓了
						//if("jp,ca,com,mx,".contains(country)&&(!setZero.contains(name))){
						//	continue;
						//}
						 
						if(changeSkuMap.get(sku)!=null){
							Set<String> changeSkus = changeSkuMap.get(sku);
							int m =0;
							Integer otherTotal=0;
							for(String skuTemp:changeSkus){
								//如果是淘汰品      替换sku的库存大于10,不显示在断货里
								String skuKey=skuTemp+","+country;
								if(fbaMap.get(skuKey)!=null&&!skuTemp.equals(sku)){
									if(noSaleSkuSets.contains(sku)){
										//如果是淘汰品,替换sku之和大于10
										otherTotal+=Integer.parseInt(fbaMap.get(skuKey).split(",")[0]);
										if(otherTotal>10){
											m=1;
											break;
										}
									}else{
										//如果不是淘汰品其他sku有一个大于0就不显示
										if(!skuTemp.equals(sku)&&Integer.parseInt(fbaMap.get(skuKey).split(",")[0])>0){
											m=1;
											break;
										}
									}
								}
							}
							if(m==1){
								continue;
							}
						}
						
						
						String outDateStr  = dateMap.get(sku);
						long quantum = 1;
						//根据时间段算出断货时间
						if(outDateStr.split("-").length>1){
							quantum=DateUtils.spaceDays(sdf.parse(outDateStr.split("-")[0]), sdf.parse(outDateStr.split("-")[1]))+1;
							outDateStr=outDateStr.split("-")[0];
						}
						List<String> resInfos = null;
						if(refOutOfMap.get(country)==null){
							resInfos = Lists.newArrayList();
						}else{
							resInfos=refOutOfMap.get(country);
						}
						if(StringUtils.isEmpty(outDateStr)){
							//sku上错了，   98-FE2002NEW-US  de
							continue;
						}
						
						//如果是美国产品   淘汰产品断货一个月的不要      如果美国仓还有货的 就不算断货   前面那个SKU不再使用了 也不需要再抓了
						if("jp,ca,com,mx,".contains(country)&&(noSaleSkuSets.contains(sku)&&quantum>30)){
							continue;
						}
						String resInfo = sku+","+name+","+quantum+","+outDateStr;
						resInfos.add(resInfo);
						refOutOfMap.put(country, resInfos);
					}
				}
			}
	}
	

	
	/**
	 * * refPreOutOfMap  key:country   key:name,sales30,fbaQuantiy,tranQuantity,canSaleDays  key:sku,inventoryQuantity  key:country  value: price,inventoryQuantity 
	 *  refPreOutOfMap  key:country   key:name,fbaQuantiy,tranQuantity,canSaleDays  value:List：sku,inventoryQuantity,price  
	 */
	public Map<String,Map<String,List<String>>>  getPreOutOfStock() throws ParseException{
		Map<String,Map<String,List<String>>> refPreOutOfMap=Maps.newHashMap();
		//获取fba仓库最新的日期
		Date dataDate =getLastDateFbaStock();
		try{
			//查询所有的fba库存 key:sku+国家 key:fba实+在途+fba总
			Map<String,String> fbaMap = this.getFbaInventroy(null,dataDate);
			
	        //以产品名颜色为粒度的库存		key:国家  value名字、数量
			Map<String,Map<String,String>> nameColorfbaMap = getFbaInventoryMap(dataDate,null);
			
			Map<String,Map<String,Integer>> sale31Map=get31SalesMap(null);
			//查询30天销量  key:sku
			Map<String,Integer> sku31Map = this.get31DaysSales(null);
			Map<String,String> fbaInventoryMap = Maps.newHashMap();
			
			//遍历amazon2表
			List<AmazonProduct2>  amaList = amazonProduct2Dao.findAll();
			//目前还在使用的sku对应的asin
			Map<String,String>  skuAsinMap = Maps.newHashMap();
			Set<String>  usedSkuCountry =Sets.newHashSet();
			//获得sku及在各个平台销售的信息  key:sku   key:country value：价格
			Map<String,Map<String,String>> ama2SkuInfoMap = Maps.newHashMap();  
			//asin对应的sku及国家价格
			Map<String,Map<String,List<String>>> asinMap = Maps.newHashMap();
			
			for(AmazonProduct2 pro:amaList){
				String sku 		= pro.getSku();
				String asin 	= pro.getAsin();
				String active 	= pro.getActive();
				String country = pro.getCountry();
				String isFba   = pro.getIsFba();
				String price =pro.getSalePrice()==null?"":(pro.getSalePrice()+"");
				if("1".equals(active)&&"1".equals(isFba)){
					//获得sku在各个国家的销售价格
					Map<String,String>  priceMap = null;
					if(ama2SkuInfoMap.get(sku)==null){
						priceMap = Maps.newHashMap();
					}else{
						priceMap = ama2SkuInfoMap.get(sku);
					}
					priceMap.put(country, price);
					ama2SkuInfoMap.put(sku, priceMap);
					
					if(skuAsinMap.get(sku)==null){
						skuAsinMap.put(sku, asin);
					}
					String skuCountry =sku+","+country; 
					usedSkuCountry.add(skuCountry);
					if(!"jp,ca,com,mx,".contains(country+",")){
						Map<String,List<String>> innerSkuMap =null;
						if(asinMap.get(asin)==null){
							innerSkuMap =Maps.newHashMap();
						}else{
							innerSkuMap=asinMap.get(asin);
						}
						List<String> innerList=null;
						if(innerSkuMap.get(sku)==null){
							innerList=Lists.newArrayList();
						}else{
							innerList=innerSkuMap.get(sku);
						}
						String countryPrice =country+";"+price;
						innerList.add(countryPrice);
						innerSkuMap.put(sku, innerList);
						asinMap.put(asin, innerSkuMap);
					}
				}
			}
			
			for(Map.Entry<String, String> entry:fbaMap.entrySet()){
				String skuCountry = entry.getKey();
				//如果sku、国家                     能过滤掉美国和加拿大的错误sku   大小写
				if(usedSkuCountry.contains(skuCountry)){
					fbaInventoryMap.put(skuCountry.split(",")[0], entry.getValue().split(",")[0]);
				};
			}
			
			//算出即将断货的产品
			Map<String,List<String>> preOutOfMap = this.preOutOfStock(sale31Map, nameColorfbaMap);
			//找出断货国家的sku
			Map<String,Map<String,Set<String>>> proSkusMap =getProductCountrySkusMap(dataDate,null);
			List<String> countrys = dictService.findByType("platform");
			String euArr[]={"de","fr","it","es","uk"};
			for(String country:countrys){
				if(preOutOfMap.get(country)!=null){
					Map<String,List<String>> resInfosMap = null;
					if(refPreOutOfMap.get(country)==null){
						resInfosMap = Maps.newHashMap();
					}else{
						resInfosMap=refPreOutOfMap.get(country);
					}
					List<String> nameColors=preOutOfMap.get(country);
					for(String nameColor:nameColors){
						Set<String> otherSkus = Sets.newHashSet();
						Set<String> selfAsins = Sets.newHashSet();
						String[] quantityInfos = nameColorfbaMap.get(country).get(nameColor).split(",");
						Integer canSaleDays =Integer.parseInt(quantityInfos[0])*31/sale31Map.get(country).get(nameColor);
						String  nameInfo  =nameColor+","+quantityInfos[0]+","+quantityInfos[1]+","+canSaleDays+","+sale31Map.get(country).get(nameColor);
						//查出这个产品欧洲其他国家的sku
						for(String tempCountry:euArr){
							if(!tempCountry.equals(country)){
								if(proSkusMap.get(nameColor)!=null&&proSkusMap.get(nameColor).get(tempCountry)!=null){
									otherSkus.addAll(proSkusMap.get(nameColor).get(tempCountry));
								}
							}else{
								if(proSkusMap.get(nameColor)!=null&&proSkusMap.get(nameColor).get(tempCountry)!=null){
									for(String sku:proSkusMap.get(nameColor).get(tempCountry)){
										selfAsins.add(skuAsinMap.get(sku));
									};
								}
							}
						}
						//如果其他国家的sku在当前国家有价格，说明已经cross了，如果没有并且库存不为0，取库存最大的和最直销的
						Map<String,List<String>>  hasPriceMap = Maps.newHashMap();
						for(String otherSku:otherSkus){
							String fbaInventoryQ=fbaInventoryMap.get(otherSku);
							//如果库存为0或者为空，排掉
							if(StringUtils.isEmpty(fbaInventoryQ)||"0".equals(fbaInventoryQ)||otherSku.contains("LOCAL")||otherSku.contains("local")||otherSku.contains("-Old")||otherSku.contains("-old")){
								continue;
							}
							//如果有价格，说明已经cross sku了
							if(ama2SkuInfoMap.get(otherSku)!=null){
								String price=ama2SkuInfoMap.get(otherSku).get(country);
								//如果这些sku对应的asin相同自己国家的asin里面存在就提示出来
								String skuAsin = skuAsinMap.get(otherSku);
								if(!selfAsins.contains(skuAsin)){
									continue;
								}
								List<String> list = Lists.newArrayList();
								if(StringUtils.isNotEmpty(price)){
									if(hasPriceMap.get("has")!=null){
										list=hasPriceMap.get("has");
									}
									list.add(otherSku+","+price+","+fbaInventoryQ);
									hasPriceMap.put("has", list);
								}else{
									if(hasPriceMap.get("no")!=null){
										list=hasPriceMap.get("no");
									}
									list.add(otherSku+",(请上贴),"+fbaInventoryQ);
									hasPriceMap.put("no", list);
								}
							}
						}
						//整合数据
						List<String>  resList = Lists.newArrayList();
						if(hasPriceMap.size()>0){
							if(hasPriceMap.get("has")!=null){
								resList.addAll(hasPriceMap.get("has"));
							}else if(hasPriceMap.get("no")!=null){
								if(hasPriceMap.get("no").size()==1){
									resList.addAll(hasPriceMap.get("no"));
								}else{
								//找出库存最大的，和可销售天数最大的
								TreeMap<Integer,String> canSaleTreeMap = Maps.newTreeMap();
								TreeMap<Integer,String> maxInventoryTreeMap = Maps.newTreeMap();
								for(String res:hasPriceMap.get("no")){
									String arr[]=res.split(",");
									Integer stockQuantity = Integer.parseInt(arr[2]);
									Integer saleDays = 9999;
									//如果30天销量为0，说明最滞销
									if(sku31Map.get(arr[0])!=null&&!sku31Map.get(arr[0]).equals(0)){
										saleDays = stockQuantity*31/sku31Map.get(arr[0]);
									}
									maxInventoryTreeMap.put(stockQuantity, arr[0]);
									canSaleTreeMap.put(saleDays, arr[0]);
								}
								String maxInfos=maxInventoryTreeMap.get(maxInventoryTreeMap.lastKey())+","+canSaleTreeMap.get(canSaleTreeMap.lastKey());
								for(String res:hasPriceMap.get("no")){
									if(maxInfos.contains(res.split(",")[0])){
										resList.add(res);
									}
								}
							}
								
							}
						}
						resInfosMap.put(nameInfo,resList);
					}
					refPreOutOfMap.put(country, resInfosMap);
				}
				
			}
			
			//对可售天进行升序排序处理
			// refPreOutOfMap  key:country   key:name,fbaQuantiy,tranQuantity,canSaleDays
			for(Map.Entry<String, Map<String, List<String>>> entry:refPreOutOfMap.entrySet()){
				String countryTemp = entry.getKey();
				Map<String,List<String>> noSortMap = entry.getValue();
				Map<String,Integer> noSortKeyMap = Maps.newHashMap(); 
				for(Map.Entry<String,List<String>> entry1:noSortMap.entrySet()){
					String proInfo = entry1.getKey();
					String canSaleDays =proInfo.split(",")[3];
					noSortKeyMap.put(proInfo, Integer.parseInt(canSaleDays));
				}
				MapValueComparator bvc =  new MapValueComparator(noSortKeyMap,true);  
				TreeMap<String,Integer> sortKeyMap = new TreeMap<String,Integer>(bvc);  
				sortKeyMap.putAll(noSortKeyMap); 
		        Map<String,List<String>> sortMap=Maps.newLinkedHashMap(); 
		        for(Map.Entry<String,Integer> entry2:sortKeyMap.entrySet()){
		        	String sortKey = entry2.getKey();
		        	sortMap.put(sortKey, noSortMap.get(sortKey));
		        }
		        refPreOutOfMap.put(countryTemp, sortMap);
			}
		}catch(Exception ex){
			logger.error("即将断货异常"+ex.getMessage(),ex);
			ex.printStackTrace();
		}
		return refPreOutOfMap;
	}
	
	//fba库存积压
		public Map<String,List<String>> overStock(Map<String,Map<String,Integer>> sale30Map,Map<String,Map<String,String>> fbaMap,Map<String,Set<String>> notSaleOrNewProColorMap,Integer type){
			Map<String,List<String>> overMap = Maps.newHashMap();
			//获取积压的sku
			for(Map.Entry<String,Map<String,String>> entry:fbaMap.entrySet()){
				String country = entry.getKey();
				Map<String,String> fbaInnerMap = entry.getValue();
				List<String> proColors = null;
				for(Map.Entry<String,String> entry1:fbaInnerMap.entrySet()){
					String proColor = entry1.getKey();
					Integer fullQuantity = Integer.parseInt(fbaInnerMap.get(proColor).split(",")[0]);
					if(fullQuantity.equals(0)||(notSaleOrNewProColorMap.get(country)!=null&&notSaleOrNewProColorMap.get(country).contains(proColor))){
						continue;
					}
					//如果30天销售没有这个    或者库存数为0   或者fba库存/30天销售>3
					if(sale30Map.get(country)==null||sale30Map.get(country).get(proColor)==null||sale30Map.get(country).get(proColor).equals(0)||fullQuantity/sale30Map.get(country).get(proColor)>type){
						if(overMap.get(country)==null){
							proColors= Lists.newArrayList();
							overMap.put(country, proColors);
						}else{
							proColors= overMap.get(country);
						}
						proColors.add(proColor);
					}
				}
			}
			return overMap;
		}
	
	
	//即将断货
	public Map<String,List<String>> preOutOfStock(Map<String,Map<String,Integer>> sale31Map,Map<String,Map<String,String>> fbaMap){
		Map<String,List<String>> preOutOfMap = Maps.newHashMap();
		//获取积压的sku
//		for(String country:fbaMap.keySet()){
		for(Map.Entry<String,Map<String,String>> entry:fbaMap.entrySet()){
			String country = entry.getKey();
			Map<String,String> nameMap = entry.getValue();
			for(Map.Entry<String, String> entry1:nameMap.entrySet()){
				String proName = entry1.getKey();
				String quantityStr = entry1.getValue();
				Integer fullQuantity = Integer.parseInt(quantityStr.split(",")[0]);
				Integer totalQuantity = Integer.parseInt(quantityStr.split(",")[1]);
				if(sale31Map==null||sale31Map.get(country)==null){
					continue;
				}
				Integer saleQuantity=sale31Map.get(country).get(proName);
				if(fullQuantity.equals(0)||saleQuantity==null||saleQuantity.equals(0)){
					continue;
				}
				//如果fba实/日均销<20,并且fba总/日均销<30 或者fba实/日均销<10
				if((fullQuantity*31/saleQuantity)<=20){
					if((totalQuantity*31/saleQuantity)<=30||(fullQuantity*31/saleQuantity)<=10){
						List<String> names = null;
						if(preOutOfMap.get(country)==null){
							names=Lists.newArrayList();
						}else{
							names= preOutOfMap.get(country);
						}
						names.add(proName);
						preOutOfMap.put(country, names);
					}
				}
			}
		}
		return preOutOfMap;
	}
	
	
	/**
	 *建立cross贴所需数据，，，，断货、即将断货 
	 */
	public Map<String,List<String>> crossOutOfStock(Map<String,Map<String,Integer>> sale31Map,Map<String,Map<String,String>> fbaMap,Map<String,List<String>> outOfProMap){
		Map<String,List<String>> preOutOfMap = Maps.newHashMap();
		//自动建贴
		for(Map.Entry<String,Map<String,String>> entry:fbaMap.entrySet()){
			String country = entry.getKey();
			Map<String,String> nameMap = entry.getValue();
			for(Map.Entry<String,String> entry1:nameMap.entrySet()){
				String proName = entry1.getKey();
				String quantityStr = entry1.getValue();
				Integer fullQuantity = Integer.parseInt(quantityStr.split(",")[0]);
				if(sale31Map==null||sale31Map.get(country)==null){
					continue;
				}
				Integer saleQuantity=sale31Map.get(country).get(proName);
				if(fullQuantity.equals(0)||saleQuantity==null||saleQuantity.equals(0)){
					continue;
				}
				if((fullQuantity*31/saleQuantity)<5){
					List<String> names = null;
					if(preOutOfMap.get(country)==null){
						names=Lists.newArrayList();
					}else{
						names= preOutOfMap.get(country);
					}
					names.add(proName);
					preOutOfMap.put(country, names);
				}
			}
			if(outOfProMap.get(country)!=null&&outOfProMap.get(country).size()>0){
				List<String> list = preOutOfMap.get(country);
				if(list==null){
					list=Lists.newArrayList();
				}
				if(outOfProMap.get(country)!=null){
					list.addAll(outOfProMap.get(country));//加入断货的产品
				}
				if(list!=null){
					Set<String> set = Sets.newHashSet();
					set.addAll(list);
					list = Lists.newArrayList();
					for(String proColor:set){
						list.add(proColor);
					}
				}
			
				preOutOfMap.put(country, list);
			}
		}
		return preOutOfMap;
	}
	
	
	public Map<String,String> getSkuInventoryQuantityNoChina(){
		Map<String,String> resMap =Maps.newHashMap();
		String sql="SELECT a.sku,a.`new_quantity` FROM psi_inventory  AS a WHERE a.`warehouse_id`!='21' ";
		List<Object[]> objs = this.inventoryFbaDao.findBySql(sql);
		for(Object[] obj:objs){
			String sku = obj[0].toString();
			String quantity = obj[1].toString();
			resMap.put(sku, quantity);
		}
		return resMap;
	}
	
	/**
	 * 查询整个产品的总和  key:nameColor value:fba(实)， fba(总)
	 * 关联本地库存表和fba库存表
	 */
	
	public Map<String,Map<String,String>>   getFbaInventoryMap(Date dataDate,String paltform){
		Map<String,Map<String,String>>  resMap =Maps.newHashMap();
		String sql ="";
		List<Object[]> objs =null;
		if(StringUtils.isEmpty(paltform)){
			sql="SELECT   CASE WHEN bb.`color_code`='' THEN bb.product_name ELSE CONCAT(bb.product_name,'_',bb.`color_code`) END AS nameColor,aa.country ,SUM(aa.fulfillable_quantity) as funnQuantity,SUM(aa.total) as totalQuantity FROM" +
					" (SELECT b.sku,b.`country`,b.`fulfillable_quantity`,(b.`fulfillable_quantity`+(CASE  WHEN b.orrect_quantity IS NULL THEN 0 ELSE  b.orrect_quantity END)+b.`transit_quantity`) AS total FROM psi_inventory_fba AS b" +
					" WHERE b.`data_date`=:p1) AS aa ,(SELECT DISTINCT a.product_id,a.`product_name`,a.`color_code`,a.`sku`,a.country_code FROM psi_inventory AS a ) AS bb " +
					" WHERE BINARY(aa.sku)=bb.sku  AND aa.country = bb.country_code  GROUP BY bb.product_name,bb.`color_code`,aa.country  HAVING totalQuantity>0 ";
			 objs = this.inventoryFbaDao.findBySql(sql,new Parameter(dataDate));
		}else{
			sql="SELECT   CASE WHEN bb.`color_code`='' THEN bb.product_name ELSE CONCAT(bb.product_name,'_',bb.`color_code`) END AS nameColor,aa.country ,SUM(aa.fulfillable_quantity) as funnQuantity,SUM(aa.total) as totalQuantity FROM" +
					" (SELECT b.sku,b.`country`,b.`fulfillable_quantity`,(b.`fulfillable_quantity`+(CASE  WHEN b.orrect_quantity IS NULL THEN 0 ELSE  b.orrect_quantity END)+b.`transit_quantity`) AS total FROM psi_inventory_fba AS b" +
					" WHERE b.`data_date`=:p1) AS aa ,(SELECT DISTINCT a.product_id,a.`product_name`,a.`color_code`,a.`sku`,a.country_code FROM psi_inventory AS a ) AS bb " +
					" WHERE BINARY(aa.sku)=bb.sku  AND aa.country = bb.country_code  AND aa.country=:p2  GROUP BY bb.product_name,bb.`color_code`,aa.country  HAVING totalQuantity>0";
			 objs = this.inventoryFbaDao.findBySql(sql,new Parameter(dataDate,paltform));
		}
		for(Object[] obj:objs){
			String name = obj[0].toString();
			String country = obj[1].toString();
			Map<String,String> nameMap = null;
			if(resMap.get(country)==null){
				nameMap=Maps.newHashMap();
			}else{
				nameMap=resMap.get(country);
			}
			String quantity = obj[2].toString()+","+obj[3].toString();
			nameMap.put(name, quantity);
			resMap.put(country, nameMap);
		}
		
		return resMap;
	}
	
	/**
	 * 当前fba库存里这个国家的产品名、国家、sku
	 * key:名字 key：国家，skuSet
	 */
	
	public Map<String,Map<String,Set<String>>>   getProductCountrySkusMap(Date dataDate,Set<String>  hasEleSkus){
		Map<String,Map<String,Set<String>>>  resMap =Maps.newHashMap();
		//String sql="SELECT a.`id` FROM psi_product AS a WHERE a.`is_sale`='0' OR a.`del_flag`='1'";
		String sql="SELECT a.`id` FROM psi_product AS a WHERE a.`del_flag`='1'";
		List<Integer> productIds = this.inventoryFbaDao.findBySql(sql);
		List<Object[]> objs = Lists.newArrayList();
		if(hasEleSkus!=null&&hasEleSkus.size()>0){
			sql="SELECT CASE WHEN bb.`color_code`='' THEN bb.product_name ELSE CONCAT(bb.product_name,'_',bb.`color_code`) END AS nameColor,aa.country" +
					" ,aa.sku FROM (SELECT b.sku,b.country FROM psi_inventory_fba AS b WHERE b.`data_date`=:p1) AS aa ,(SELECT DISTINCT a.product_id," +
					"a.`product_name`,a.`color_code`,a.`sku`,a.country_code FROM psi_inventory AS a ) AS bb " +
					"WHERE BINARY(aa.sku)=bb.sku AND BINARY(aa.sku) NOT IN :p3 AND bb.product_id not in :p2" +
					" GROUP BY bb.product_name,bb.`color_code`,bb.country_code,aa.sku";
			objs = this.inventoryFbaDao.findBySql(sql,new Parameter(dataDate,productIds,hasEleSkus));
		}else{
			sql="SELECT CASE WHEN bb.`color_code`='' THEN bb.product_name ELSE CONCAT(bb.product_name,'_',bb.`color_code`) END AS nameColor,aa.country" +
					" ,aa.sku FROM (SELECT b.sku,b.country FROM psi_inventory_fba AS b WHERE b.`data_date`=:p1) AS aa ,(SELECT DISTINCT a.product_id," +
					"a.`product_name`,a.`color_code`,a.`sku`,a.country_code FROM psi_inventory AS a ) AS bb " +
					"WHERE BINARY(aa.sku)=bb.sku AND bb.product_id not in :p2" +
					" GROUP BY bb.product_name,bb.`color_code`,bb.country_code,aa.sku";
			objs = this.inventoryFbaDao.findBySql(sql,new Parameter(dataDate,productIds));
		}
		
		for(Object[] obj:objs){
			String name = obj[0].toString();
			String country = obj[1].toString();
			String sku = obj[2].toString();
			Map<String,Set<String>> countryMap = null;
			if(resMap.get(name)==null){
				countryMap=Maps.newHashMap();
			}else{
				countryMap=resMap.get(name);
			}
			Set<String> set =null;
			if(countryMap.get(country)==null){
				set =Sets.newHashSet();
			}else{
				set =countryMap.get(country);
			}
			set.add(sku);
			countryMap.put(country, set);
			resMap.put(name, countryMap);
		}
		return resMap;
	}
	
	
	/**
	 *查询31天销量 
	 * 
	 */
	public Map<String,Map<String,Integer>>   get31SalesMap(String platfrom){
		Map<String,Map<String,Integer>> resMap =Maps.newHashMap();
		String sql="";
		List<Object[]> objs =null;
		if(StringUtils.isEmpty(platfrom)){
			sql="SELECT a.`product_name`,a.`country`,a.`day31_sales` FROM psi_product_variance AS a ";
			objs = this.inventoryFbaDao.findBySql(sql);
		}else{
			sql="SELECT a.`product_name`,a.`country`,a.`day31_sales` FROM psi_product_variance AS a WHERE a.`country`=:p1 ";
			objs = this.inventoryFbaDao.findBySql(sql,new Parameter(platfrom));
		}
		for(Object[] obj:objs){
			String name = obj[0].toString();
			String country = obj[1].toString();
			Integer quantity = Integer.parseInt(obj[2].toString());
			Map<String,Integer> nameMap = null;
			if(resMap.get(country)==null){
				nameMap=Maps.newHashMap();
			}else{
				nameMap=resMap.get(country);
			}
			nameMap.put(name, quantity);
			resMap.put(country, nameMap);
		}
		return resMap;
	}
	

	
	public Integer get31Sales(String country,String name){
		String sql="";
		List<Object> list=null;
		if("eu".equals(country)){
			sql="SELECT sum(a.`day31_sales`) FROM psi_product_variance AS a where  a.`product_name`=:p1 and a.`country` in ('de','fr','it','es','uk') ";
			list=inventoryFbaDao.findBySql(sql,new Parameter(name));
			if(list!=null&&list.size()>0){
				if(list.get(0)==null){
					return 0;
				}
				return ((BigDecimal)list.get(0)).intValue();
			}
		}else{
			sql="SELECT a.`day31_sales` FROM psi_product_variance AS a where a.`product_name`=:p1 and a.`country` =:p2 ";
			list=inventoryFbaDao.findBySql(sql,new Parameter(name,country));
			if(list!=null&&list.size()>0){
				if(list.get(0)==null){
					return 0;
				}
				return Integer.parseInt(list.get(0).toString());
			}
		}
		return 0;
	}
	
	
	/**
	 *获得  同产品  新老sku
	 * 
	 */
	public Map<String,Set<String>>  getChangeSkuMap(){
		Map<String,Set<String>> productMap =Maps.newHashMap();
		String sql="SELECT  CONCAT(a.`product_id`,a.`country_code`,a.`color_code`) AS keyStr,a.`sku` FROM psi_inventory AS a GROUP BY a.`sku`";
		List<Object[]> objs = this.inventoryFbaDao.findBySql(sql);
		for(Object[] obj:objs){
			String key = obj[0].toString();
			String sku = obj[1].toString();
			if(sku.contains("LOCAL")||sku.contains("local")){
				continue;
			}
			Set<String> comSkus = null;
			if(productMap.get(key)==null){
				comSkus = Sets.newHashSet();
			}else{
				comSkus = productMap.get(key);
			}
			comSkus.add(sku);
			productMap.put(key, comSkus);
		}
		
		Map<String,Set<String>> resMap =Maps.newHashMap();
		for(Map.Entry<String,Set<String>> entry:productMap.entrySet()){
			Set<String> skus = entry.getValue();
			if(skus!=null&&skus.size()>1){
				for(String sku:skus){
					resMap.put(sku, skus);
				}
			}
			//获取相同的sku
		}
		return resMap;
	}
	
	
	
	/**
	 *自动生成cross贴 
	 * key：名字、asin、可售天      value：sku、价格、库存数
	 */
	public Map<String,Map<String,List<String>>>  getCreateCrossData() throws ParseException{
		Map<String,Map<String,List<String>>> refPreOutOfMap=Maps.newHashMap();
		String euArr[]={"de","fr","it","es","uk"};
		//库存总数为0的不显示
		Map<String,Set<String>> zeroProMap = this.getStockZero();
		Map<String,Set<String>> selfZeroMap = this.getSelfStockZero();
		
		Map<String,String> skuMap = this.psiProductService.findAllProductNamesWithSku();
		//不销售的sku
		List<String> noSaleSkus= this.getNoSaleSku();
		Set<String> noSaleSkuSets = Sets.newHashSet();
		noSaleSkuSets.addAll(noSaleSkus);
		//获取fba仓库最新的日期
		Date dataDate =getLastDateFbaStock();
			//查询所有的fba库存 key:sku+国家 key:fba实+在途+fba总
			Map<String,String> fbaMap = this.getFbaInventroy(null,dataDate);
			Map<String,String> skuCountryMap =this.getCountrySkuMap();
	        //依产品名颜色为粒度的库存		key:国家  value名字、数量
			Map<String,Map<String,String>> nameColorfbaMap = getFbaInventoryMap(dataDate,null);
			//从方差表获得最近一个月的销量
			Map<String,Map<String,Integer>> sale31Map=get31SalesMap(null);
			//查询30天销量  key:sku
			Map<String,Integer> sku31Map = this.get31DaysSales(null);
			Map<String,String>  bandingSkuMap = this.psiProductService.getAllBandingSku2();
			Set<String> sameAsinSet = this.getEuZeroGroupByAsin(dataDate);
			Map<String,String> fbaInventoryMap = Maps.newHashMap();
			//遍历amazon2表
			List<AmazonProduct2>  amaList = amazonProduct2Dao.findAll();
			//目前还在使用的sku对应的asin
			Map<String,String>  skuAsinMap = Maps.newHashMap();
			Set<String>  usedSkuCountry =Sets.newHashSet();
			//获得sku及在各个平台销售的信息  key:sku   key:country value：价格
			Map<String,Map<String,String>> ama2SkuInfoMap = Maps.newHashMap();  
			//asin对应的sku及国家价格
			Map<String,Map<String,List<String>>> asinMap = Maps.newHashMap();
			Map<String,Boolean>  asinHasPriceEu = Maps.newHashMap();
			for(AmazonProduct2 pro:amaList){
				String sku 		= pro.getSku();
				String asin 	= pro.getAsin();
				String active 	= pro.getActive();
				String country = pro.getCountry();
				String isFba   = pro.getIsFba();
				String price =pro.getSalePrice()==null?"":(pro.getSalePrice()+"");
				if("1".equals(active)&&"1".equals(isFba)){
					//获得sku在各个国家的销售价格
					Map<String,String>  priceMap = null;
					if(ama2SkuInfoMap.get(sku)==null){
						priceMap = Maps.newHashMap();
					}else{
						priceMap = ama2SkuInfoMap.get(sku);
					}
					priceMap.put(country, price);
					ama2SkuInfoMap.put(sku, priceMap);
					
					if(skuAsinMap.get(sku)==null){
						skuAsinMap.put(sku, asin);
					}
					String skuCountry =sku+","+country; 
					usedSkuCountry.add(skuCountry);
					if(!"jp,ca,mx,".contains(country+",")){
						Map<String,List<String>> innerSkuMap =null;
						if(asinMap.get(asin)==null){
							innerSkuMap =Maps.newHashMap();
						}else{
							innerSkuMap=asinMap.get(asin);
						}
						List<String> innerList=null;
						if(innerSkuMap.get(sku)==null){
							innerList=Lists.newArrayList();
						}else{
							innerList=innerSkuMap.get(sku);
						}
						String countryPrice =country+";"+price;
						innerList.add(countryPrice);
						innerSkuMap.put(sku, innerList);
						asinMap.put(asin, innerSkuMap);
						
						boolean hasPrice=false;
						if(StringUtils.isNotEmpty(price.trim())){
							hasPrice=true;
						}
						//只要有一次有价格
						if(asinHasPriceEu.get(asin)==null||!asinHasPriceEu.get(asin)){
							asinHasPriceEu.put(asin, hasPrice);
						}
					}
				}
			}
			
			for(Map.Entry<String, String> entry:fbaMap.entrySet()){
				String skuCountry = entry.getKey();
				//如果sku、国家                     能过滤掉美国和加拿大的错误sku   大小写
				if(usedSkuCountry.contains(skuCountry)){
					fbaInventoryMap.put(skuCountry.split(",")[0], entry.getValue().split(",")[0]);
				};
			}
			
			//找出断货的产品start
			//淘汰品在断货里面
			Map<String,List<String>> outOfMap = this.getOutOfStockSku(usedSkuCountry,dataDate);
			
			//查询断货的时间段
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date endDate = sdf.parse(sdf.format(new Date()));
			Date startDate = DateUtils.addMonths(endDate, -2);
			Map<String,String> dateMap= Maps.newHashMap();
			if(outOfMap!=null&&outOfMap.size()>0){
				//获取所有断货sku
				Set<String> outSku = Sets.newHashSet();
				for(Map.Entry<String,List<String>> entry:outOfMap.entrySet()){
					outSku.addAll(entry.getValue());
				}
				dateMap=this.getOutOfStockMail(startDate,endDate,outSku);
			}
			
			Map<String,List<String>> outOfProMap = Maps.newHashMap();
			for(String country:euArr){
				if(outOfMap.get(country)!=null){
					Set<String> set = zeroProMap.get(country);
					Set<String> selfZero = selfZeroMap.get(country);
					//如果这个sku的替换sku没断货     不显示
					Map<String,Set<String>> changeSkuMap = this.getChangeSkuMap();
					for(int i =0;i<outOfMap.get(country).size();i++){
						String sku = outOfMap.get(country).get(i);
						if(StringUtils.isEmpty(skuMap.get(sku))){
							continue;   //没名字就不cross
						}
						String name=skuMap.get(sku);
						String asin = skuAsinMap.get(sku);
//						if(name.contains("DS1001")&&country.equals("it")){
//							System.out.println("1");
//						}
						//如果亚马逊库存为0并且31天销量为0或者为空       或者整个欧洲本地库存为0
						if(((set!=null&&set.contains(name)&&(sale31Map.get(country).get(name)==null||sale31Map.get(country).get(name).intValue()==0)))||(selfZero!=null&&selfZero.contains(name))){
							continue;
						}
						//欧洲国家     sku对应的asin找不到   或者  是淘汰品如果整个eu都没价格 不显示
						if(asinHasPriceEu.get(asin)==null||(noSaleSkuSets.contains(sku)&&(!asinHasPriceEu.get(asin)||sameAsinSet.contains(asin)))){
							continue;
						}
						 
						if(changeSkuMap.get(sku)!=null){
							Set<String> changeSkus = changeSkuMap.get(sku);
							int m =0;
							Integer otherTotal=0;
							for(String skuTemp:changeSkus){
								//如果是淘汰品      替换sku的库存大于10,不显示在断货里
								String skuKey=skuTemp+","+country;
								if(fbaMap.get(skuKey)!=null&&!skuTemp.equals(sku)){
									if(noSaleSkuSets.contains(sku)){
										//如果是淘汰品,替换sku之和大于10
										otherTotal+=Integer.parseInt(fbaMap.get(skuKey).split(",")[0]);
										if(otherTotal>10){
											m=1;
											break;
										}
									}else{
										//如果不是淘汰品其他sku有一个大于0就不显示
										if(!skuTemp.equals(sku)&&Integer.parseInt(fbaMap.get(skuKey).split(",")[0])>0){
											m=1;
											break;
										}
									}
								}
							}
							if(m==1){
								continue;
							}
						}
						
						
						String outDateStr  = dateMap.get(sku);
						//根据时间段算出断货时间
						if(outDateStr.split("-").length>1){
							outDateStr=outDateStr.split("-")[0];
						}
						List<String> resInfos = null;
						if(outOfProMap.get(country)==null){
							resInfos = Lists.newArrayList();
						}else{
							resInfos=outOfProMap.get(country);
						}
						if(StringUtils.isEmpty(outDateStr)){
							//sku上错了，   98-FE2002NEW-US  de
							continue;
						}
						String resInfo = name;
						resInfos.add(resInfo);
						outOfProMap.put(country, resInfos);
					}
				}
			}    
			   
			//算出即将断货的产品
			Map<String,List<String>> preOutOfMap = this.crossOutOfStock(sale31Map, nameColorfbaMap,outOfProMap);
			//英国带电的sku排除
			Set<String>  hasEleSkus = getUnCrossSku();
			//产品、国家、sku关系
			Map<String,Map<String,Set<String>>> proSkusMap =getProductCountrySkusMap(dataDate,hasEleSkus);
			
			for(String country:euArr){
				if(preOutOfMap.get(country)!=null){
					Map<String,List<String>> resInfosMap = null;
					if(refPreOutOfMap.get(country)==null){
						resInfosMap = Maps.newHashMap();
					}else{
						resInfosMap=refPreOutOfMap.get(country);
					}
					List<String> nameColors=preOutOfMap.get(country);
					for(String nameColor:nameColors){
						Set<String> otherSkus = Sets.newHashSet();
						Set<String> selfAsins = Sets.newHashSet();
						if(nameColorfbaMap.get(country).get(nameColor)==null){
							//本地库存没有记录的不cross
							continue;
						}
						String[] quantityInfos = nameColorfbaMap.get(country).get(nameColor).split(",");
						if(sale31Map==null||sale31Map.get(country)==null||sale31Map.get(country).get(nameColor)==null||sale31Map.get(country).get(nameColor).intValue()==0){
							//30天没销量的不要
							continue;
						}
						Integer canSaleDays =Integer.parseInt(quantityInfos[0])*31/sale31Map.get(country).get(nameColor);
						//查出这个产品欧洲其他国家的sku
						for(String tempCountry:euArr){
							if(!tempCountry.equals(country)){
								if(proSkusMap.get(nameColor)!=null&&proSkusMap.get(nameColor).get(tempCountry)!=null){
									otherSkus.addAll(proSkusMap.get(nameColor).get(tempCountry));
								}
							}else{
								if(proSkusMap.get(nameColor)!=null&&proSkusMap.get(nameColor).get(tempCountry)!=null){//获取自己国家的sku
									for(String sku:proSkusMap.get(nameColor).get(tempCountry)){//获取自己国家的sku、asin
										String asin =skuAsinMap.get(sku);
										if(asin!=null){
											//依asin为key
											selfAsins.add(asin);
										}
									};
								}
							}
						}
						
						for(String asin:selfAsins){//对一个产品多个asin的情况进行处理
							Set<String> skuSet = proSkusMap.get(nameColor).get(country); //这个产品颜色这个国家，所有的sku
							String usedSku="";
							String	bandingSku=bandingSkuMap.get(nameColor+","+country);
							if(skuSet.contains(bandingSku)&&asin.equals(skuAsinMap.get(bandingSku))){//如果绑定的sku存在，并且asin相同
								usedSku=bandingSku;
							}else{
								for(String selfSku:skuSet){
									if(asin.equals(skuAsinMap.get(selfSku))){
										usedSku=selfSku;
										break;
									}
								}
							}
							
							if(StringUtils.isEmpty(usedSku)){
								continue;
							}
							String  nameInfo  =nameColor+","+asin+","+usedSku+","+canSaleDays;
							//如果其他国家的sku在当前国家有价格，说明已经cross了，如果没有并且库存不为0，取库存最大的和最直销的
							List<String> resList = Lists.newArrayList();
							//可销售天数最大的
							Map<String,Integer> noSortCanSaleMap = Maps.newHashMap();
							Map<String,String> crossSkuMap = Maps.newHashMap();
							for(String otherSku:otherSkus){
								String fbaInventoryQ=fbaInventoryMap.get(otherSku);
								//如果库存为0或者为空，排掉    如果小于10排掉
								if(StringUtils.isEmpty(fbaInventoryQ)||((Integer.parseInt(fbaInventoryQ))<10)||"0".equals(fbaInventoryQ)||otherSku.contains("LOCAL")||otherSku.contains("local")||otherSku.contains("-Old")||otherSku.contains("-old")){
									continue;
								}
								
								//如果有价格，说明已经cross sku了
								if(ama2SkuInfoMap.get(otherSku)!=null){
									String price=ama2SkuInfoMap.get(otherSku).get(country);
									//如果这些sku对应的asin相同自己国家的asin里面存在就提示出来
									String skuAsin = skuAsinMap.get(otherSku);
									if(!asin.equals(skuAsin)){
										continue;
									}
									//获得这个sku的name  和这个sku所在的国家
									String otherCountry = skuCountryMap.get(otherSku);
									String otherName=skuMap.get(otherSku);
									String sameCountryQ =fbaInventoryQ;
									if(nameColorfbaMap.get(otherCountry)!=null&&nameColorfbaMap.get(otherCountry).get(otherName)!=null){
										sameCountryQ=nameColorfbaMap.get(otherCountry).get(otherName).split(",")[0];
									}
									Integer saleDays = 9999;
									//如果30天销量为0，说明最滞销
									if(sku31Map.get(otherSku)!=null&&!sku31Map.get(otherSku).equals(0)){
										saleDays = Integer.parseInt(sameCountryQ)*31/sku31Map.get(otherSku);
									}
									noSortCanSaleMap.put(otherSku, saleDays);
									if(StringUtils.isNotEmpty(price)){
										resList.add(otherSku+","+price+","+fbaInventoryQ);
										crossSkuMap.put(otherSku, otherSku+","+price+","+fbaInventoryQ);
									}else{
										resList.add(otherSku+",price,"+fbaInventoryQ);
										crossSkuMap.put(otherSku,otherSku+",price,"+fbaInventoryQ);
									}
									
								}
							}
							
							//对list进行排序
							List<String> sortList = Lists.newArrayList();
							if(resList.size()>0){
								MapValueComparator bvc =  new MapValueComparator(noSortCanSaleMap,false);  
								TreeMap<String,Integer> sortCanSaleMap = new TreeMap<String,Integer>(bvc); 
								sortCanSaleMap.putAll(noSortCanSaleMap); 
						        for(Map.Entry<String, Integer> entry:sortCanSaleMap.entrySet()){
						        	String sortKey = entry.getKey();
						        	sortList.add(crossSkuMap.get(sortKey));
						        }
							}
							resInfosMap.put(nameInfo,sortList);
						}
						
					}
					refPreOutOfMap.put(country, resInfosMap);
				}
			}
			
		return refPreOutOfMap;
	}
	
	/**
	 *获得fba最新一天的数据 
	 */
	public Date getLastDateFbaStock(){
		String sql = "SELECT a.`data_date` FROM psi_inventory_fba AS a ORDER BY a.`data_date` DESC LIMIT 1";
		List<Date> dates=this.inventoryFbaDao.findBySql(sql);
		if(dates!=null&&dates.size()>0){
			return dates.get(0);
		}
		return null;
	}
	
	
	//淘汰的和新品
	public List<String> getNoSaleAndNewSku(){
		//String sql ="SELECT b.`sku` FROM psi_product AS a , psi_sku AS b WHERE a.`del_flag`='0' AND b.`del_flag`='0'  AND a.`id`=b.`product_id` AND (a.`is_sale`='0' OR a.`is_new`='1') ";
		//产品淘汰分平台、颜色 sku表中country字段ebay按照de处理
		String sql ="SELECT DISTINCT b.`sku` FROM `psi_product_eliminate` a, psi_sku AS b WHERE  b.`del_flag`='0' AND a.`del_flag`='0' AND "+
				" a.`product_name`=b.`product_name` AND a.`country`=CASE WHEN b.`country` ='ebay' THEN 'de' ELSE b.`country` END AND a.`color`=b.`color` "+
				" AND (a.`is_sale`='4' OR a.`is_new`='1') ";
		return this.inventoryFbaDao.findBySql(sql);
	}
	
	/**
	 *获得总数为0的淘汰品 
	 */
	public Map<String,Set<String>> getStockZero(){
		Map<String,Set<String>> resMap = Maps.newHashMap();
		String sql = "SELECT a.`data_date` FROM psi_product_in_stock AS a ORDER BY a.`data_date` DESC LIMIT 1";
		List<Date> dates=this.inventoryFbaDao.findBySql(sql);
		if(dates!=null&&dates.size()>0){
			sql = "SELECT  a.`product_name`,a.`country`FROM psi_product_in_stock AS a WHERE a.`data_date`=:p1 AND a.`total_stock`=0";
			List<Object[]> objs=this.inventoryFbaDao.findBySql(sql,new Parameter(dates.get(0)));
			if(objs!=null&&objs.size()>0){
				for(Object[] obj:objs){
					String proColor=obj[0].toString();
				    String country=obj[1].toString();
				    Set<String> pros = null;
				    if(resMap.get(country)==null){
				    	pros=Sets.newHashSet();
				    }else{
				    	pros=resMap.get(country);
				    }
				    pros.add(proColor);
				    resMap.put(country, pros);
				}
			}
		}
		return resMap;
	}
	
	/**
	 * 查出欧洲同一个asin总库存
	 * 
	 */
	public Set<String> getEuZeroGroupByAsin(Date dataDate){
		Set<String> set = Sets.newHashSet();
		String sql="SELECT a.`asin` FROM psi_inventory_fba AS a WHERE a.`data_date`=:p1 AND a.`country` IN ('de','fr','it','uk','es') GROUP BY a.`asin` HAVING  SUM(CASE WHEN  a.`orrect_quantity` IS NULL THEN (a.`transit_quantity`+a.`fulfillable_quantity`) ELSE (a.`orrect_quantity`+a.`transit_quantity`+a.`fulfillable_quantity`) END )=0";
		List<String> objs=this.inventoryFbaDao.findBySql(sql,new Parameter(dataDate));
		if(objs!=null&&objs.size()>0){
			set.addAll(objs);
		}
		return set;
	}
	
	
	
	/**
	 * 查出本地仓为空的
	 * 
	 */
	public Map<String,Set<String>> getSelfStockZero(){
		 Map<String,Set<String>> res = Maps.newHashMap();
		String sql="SELECT CASE WHEN a.`color_code` ='' THEN a.`product_name`ELSE  CONCAT(a.`product_name`,' ',a.`color_code`) END AS productColor,a.`country_code` FROM psi_inventory AS a WHERE a.`warehouse_id` in ('19','120') GROUP BY a.`product_name`,a.`color_code`,a.`country_code` HAVING SUM(a.`new_quantity`)=0";
		List<Object[]> objs=this.inventoryFbaDao.findBySql(sql);
		if(objs!=null&&objs.size()>0){
			for(Object[] obj:objs){
				String country = obj[1].toString();
				String pro  =obj[0].toString();
				Set<String> set =null;
				if(res.get(country)==null){
					set=Sets.newHashSet();
				}else{
					set = res.get(country);
				}
				set.add(pro);
				res.put(country, set);
			}
		}
		return res;
	}
	

	//查出本地库的sku所对应的国家
	public 	Map<String,String> getCountrySkuMap(){
		Map<String,String> resMap = Maps.newHashMap();
		String sql ="SELECT a.`sku`,a.country_code FROM psi_inventory AS a GROUP BY a.sku";
		List<Object[]> list = this.inventoryFbaDao.findBySql(sql);
		for(Object[] obj:list){
			resMap.put(obj[0].toString(),obj[1].toString());
		}
		return resMap;
	}
	
	
	/**
	 * 帖子状态为正常的
	 */
	public Set<String> getPostInfo(){
		Set<String>  set=Sets.newHashSet();
		String sql=" SELECT country,ASIN FROM amazoninfo_product2 WHERE  active='1' "+
         " AND country IS NOT NULL AND ASIN IS NOT NULL AND (price IS NOT NULL OR sale_price IS NOT NULL) GROUP BY country,ASIN  ";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj: list) {
			set.add(obj[0].toString()+"_"+obj[1].toString());
		}
		return set;
		
	}
	
	
	/**
	 *获得欧洲产品的总库存数 
	 */
	public Map<String,Integer> getEuFbaInventory(){
		String sql = "SELECT MAX(data_date) FROM psi_inventory_fba";
		List<Object> date = inventoryFbaDao.findBySql(sql);
		sql = "	SELECT CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,SUM(b.`fulfillable_quantity`+IFNULL(b.orrect_quantity,0)+b.`transit_quantity`) as totalQuantity  FROM psi_sku a ,psi_inventory_fba b WHERE a.`sku` = b.`sku` AND a.`country` = b.`country` " +
				"AND a.`del_flag` = '0' AND NOT(a.`product_name` LIKE '%other' OR a.`product_name` LIKE '%Old%' ) AND b.`data_date` = :p1 AND a.`country` IN ('de','fr','uk','it','es') GROUP BY a.`product_name`,a.`color`";
		List<Object[]> list = inventoryFbaDao.findBySql(sql,new Parameter(date.get(0)));
		Map<String,Integer> rs = Maps.newHashMap();
		for(Object[] obj:list){
			String productName=obj[0].toString();
			Integer quantity = Integer.parseInt(obj[1].toString());
			rs.put(productName, quantity);
		}
		return rs;

	}
	
	public Map<String,Integer> getAllCountryFbaInventory(){
		String sql = "SELECT MAX(data_date) FROM psi_inventory_fba";
		List<Object> date = inventoryFbaDao.findBySql(sql);
		sql = "	SELECT a.`product_name` AS productName,SUM(b.`fulfillable_quantity`+IFNULL(b.orrect_quantity,0)+b.`transit_quantity`) as totalQuantity  FROM psi_sku a ,psi_inventory_fba b WHERE a.`sku` = b.`sku` AND a.`country` = b.`country` " +
				"AND a.`del_flag` = '0' AND NOT(a.`product_name` LIKE '%other' OR a.`product_name` LIKE '%Old%' ) AND b.`data_date` = :p1  GROUP BY a.`product_name` ";
		List<Object[]> list = inventoryFbaDao.findBySql(sql,new Parameter(date.get(0)));
		Map<String,Integer> rs = Maps.newHashMap();
		for(Object[] obj:list){
			String productName=obj[0].toString();
			Integer quantity = Integer.parseInt(obj[1].toString());
			rs.put(productName, quantity);
		}
		return rs;
	}
	
	/**
	 *获取一段时间内的fba库存值 
	 */
	public Map<Date,Integer> getFbaInventoryByAsinCountry(String country,String asin,Date startDate,Date endDate){
		String sql = "SELECT a.`data_date`,(a.`fulfillable_quantity`+IFNULL(a.orrect_quantity,0)+a.`transit_quantity`) AS a FROM psi_inventory_fba AS a" +
				"  WHERE  a.`asin`=:p1 AND a.`country`=:p2  AND a.`data_date` BETWEEN :p3 AND :p4 order by a.`data_date` ";
		List<Object[]> list = inventoryFbaDao.findBySql(sql,new Parameter(asin,country,startDate,endDate));
		Map<Date,Integer> rs = Maps.newHashMap();
		for(Object[] obj:list){
			Date dateDate=(Date)obj[0];
			Integer quantity = Integer.parseInt(obj[1].toString());
			rs.put(dateDate, quantity);
		}
		return rs;
	}
	 
	/**
	 *查出英国的带电源的sku 
	 */
	public Set<String> getUnCrossSku(){
		String sql="SELECT s.`sku` FROM psi_sku s ,amazoninfo_product2 p,(SELECT CONCAT(a.`brand`,' ',a.model) AS pname FROM psi_product a" +
				"  WHERE a.`del_flag` = '0'  AND (CASE WHEN  a.type='Keyboard'  THEN '1' ELSE a.`has_power` END ) = '1' AND a.`platform` LIKE '%uk%') d  " +
				" WHERE s.`product_name` = d.pname AND s.`country` = 'uk' AND s.`del_flag` = '0' AND p.`active` = '1' AND p.`country` = 'uk'" +
				" AND p.`sku` = s.`sku` ";
		List<String> list = this.inventoryFbaDao.findBySql(sql);
		Set<String> set = Sets.newHashSet();
		if(list!=null&&list.size()>0){
			set.addAll(list);
		}
		return set;
	}
	
	//产品名称，国家，日期
	public Map<String,Map<String,List<Date>>> getOutOfStock(Date start,Date end){
		Map<String,Date> dateMap=findSaldDate();
		Map<String,String> euMap=isPanEuMap();
		String sql=" SELECT p.`data_date`,p.`country`,CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN CONCAT('_',s.`color`) ELSE '' END) NAME, "+
				"  SUM(p.`fulfillable_quantity`+IFNULL(p.`orrect_quantity`,0)) quantity FROM psi_inventory_fba p "+
				" JOIN psi_sku s ON p.`country`=s.`country` AND s.sku=p.`sku` AND s.`del_flag`='0' "+
				" WHERE p.`data_date`>=:p1 AND p.`data_date`<=:p2 "+
				" GROUP BY p.`data_date`,p.`country`,NAME having quantity>0 ORDER BY p.`data_date`  ";
		List<Object[]> list = this.inventoryFbaDao.findBySql(sql,new Parameter(start,end));
		Map<String,Map<String,List<Date>>> map=Maps.newHashMap();
		Map<String,Map<String,Date>> countryDate=Maps.newHashMap();
		List<String> countryList=Lists.newArrayList("de","fr","it","es","uk");
		for (Object[] obj: list) {
			Date date=(Date)obj[0];
			String country=obj[1].toString();
			String name=obj[2].toString();
			String key=name+"_"+country;
			if(dateMap.get(key)!=null&&dateMap.get(key).after(date)){//未淘汰
				if(countryDate!=null&&countryDate.get(name)!=null&&countryDate.get(name).get(country)!=null){
					 if(!DateUtils.addDays(countryDate.get(name).get(country), 1).equals(date)){
						    Map<String,List<Date>> temp=map.get(name);
							if(temp==null){
								temp=Maps.newHashMap();
								map.put(name, temp);
							}
							List<Date> dateList=temp.get(country);
							if(dateList==null){
								dateList=Lists.newArrayList();
							}
							dateList.add(date);
					 }
				}
				if("de,fr,it,es,uk".contains(country)&&euMap.get(name)!=null){
					for (String tempCountry: countryList) {
						
							Map<String,Date> temp=countryDate.get(name);
							if(temp==null){
								temp=Maps.newHashMap();
								countryDate.put(name, temp);
							}
							temp.put(tempCountry, date);
					}
				}else{
					
						Map<String,Date> temp=countryDate.get(name);
						if(temp==null){
							temp=Maps.newHashMap();
							countryDate.put(name, temp);
						}
						temp.put(country, date);
				}
			}	
		}
		return map;
	}
	
	//产品名称，国家，日期
		public Map<String,Map<String,List<Date>>> getOutOfStock2(Date start,Date end){
			Map<String,Date> dateMap=findSaldDate();
			Map<String,String> addMonth=eliminateService.findAddedMonth();
			String sql=" SELECT p.`data_date`,p.`country`,product_name_color NAME FROM amazoninfo_out_of_product p "+
					" WHERE p.`data_date`>=:p1 AND p.`data_date`<=:p2 ORDER BY p.`data_date` ";
			List<Object[]> list = this.inventoryFbaDao.findBySql(sql,new Parameter(start,end));
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
			Map<String,Map<String,List<Date>>> map=Maps.newLinkedHashMap();
			for (Object[] obj: list) {
				Date date=(Date)obj[0];
				String country=obj[1].toString();
				String name=obj[2].toString();
				String key=name+"_"+country;
				String saleDayStr=addMonth.get(name+"_"+country);
				Date saleDay=null;
				if(StringUtils.isNotBlank(saleDayStr)){
					try {
						saleDay = dateFormat.parse(saleDayStr);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				
				if(dateMap.get(key)!=null&&dateMap.get(key).after(date)&&addMonth.get(name+"_"+country)!=null&&saleDay!=null&&(date.after(saleDay)||date.equals(saleDay))){//未淘汰
					Map<String,List<Date>> temp=map.get(name);
					if(temp==null){ 
						temp=Maps.newLinkedHashMap();
						map.put(name, temp);
					}
					List<Date> dateList=temp.get(country);
					if(dateList==null){
						dateList=Lists.newArrayList();
						temp.put(country, dateList);
					}
					dateList.add(date);
				}	
			}
			return map;
		}
	
	public Map<String, String> isPanEuMap (){
		String sql = "SELECT DISTINCT a.`product_name` FROM amazoninfo_pan_eu a WHERE a.`is_pan_eu` = '1' and a.`product_name` is not null";
		Map<String, String> rs = Maps.newHashMap();
		List<Object> list = inventoryFbaDao.findBySql(sql);
		for (Object object : list) {
			rs.put(object.toString(), "1");
		}
		return rs;
	}
	
	public Map<String,Date> findSaldDate(){
		Map<String,Date> map=Maps.newHashMap();
		String sql=" SELECT CONCAT(e.`product_name`,CASE WHEN e.`color`!='' THEN CONCAT('_',e.`color`) ELSE '' END) NAME,e.`country`,IFNULL(e.eliminate_time,CURDATE())  "+
				   " FROM psi_product_eliminate e  "+
				   " WHERE (is_sale!='4' OR (is_sale='4' AND eliminate_time IS NOT NULL)) AND del_flag='0' ";
		List<Object[]> list = this.inventoryFbaDao.findBySql(sql);
		for (Object[] obj: list) {
			String name=obj[0].toString();
			String country=obj[1].toString();
			Date date=(Date)obj[2];
			map.put(name+"_"+country,date);
		}
		return map;
	}
	
	/**
	 *获得欧洲,美国fba产品的总库存数 
	 */
	public Map<String,Integer> getFbaInventoryByDataDate(String countryCode,String dataDate){
		String 	sql = "	SELECT CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,SUM(b.`fulfillable_quantity`+IFNULL(b.orrect_quantity,0)) as totalQuantity  FROM psi_sku a ,psi_inventory_fba b WHERE a.`sku` = b.`sku` AND a.`country` = b.`country` " +
				"AND a.`del_flag` = '0' AND NOT(a.`product_name` LIKE '%other' OR a.`product_name` LIKE '%Old%' ) AND b.`data_date` ='"+dataDate+"' ";
		if("eu".equals(countryCode)){
			sql+=" AND a.`country` IN ('de','fr','uk','it','es') ";
		}else if("am".equals(countryCode)){
			sql+=" AND a.`country` IN ('com','ca') ";
		}else{
			sql+=" AND a.`country` = '"+countryCode+"'";
		}
		sql+=" GROUP BY a.`product_name`,a.`color` ";
		List<Object[]> list = inventoryFbaDao.findBySql(sql);
		Map<String,Integer> rs = Maps.newHashMap();
		for(Object[] obj:list){
			String productName=obj[0].toString();
			Integer quantity = Integer.parseInt(obj[1].toString());
			rs.put(productName, quantity);
		}
		return rs;
	}
	
	/**
	 *美国fba产品的总库存数 
	 */
	public Map<String,Integer> findUsFbaInventoryByDataDate(String countryCode,String dataDate){
		String 	sql = "	SELECT CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,SUM(IFNULL(b.total_quantity,0)) as totalQuantity  FROM psi_sku a ,psi_inventory_fba b WHERE a.`sku` = b.`sku` AND a.`country` = b.`country` " +
				"AND a.`del_flag` = '0' AND NOT(a.`product_name` LIKE '%other' OR a.`product_name` LIKE '%Old%' ) AND b.`data_date` ='"+dataDate+"' ";
		if("eu".equals(countryCode)){
			sql+=" AND a.`country` IN ('de','fr','uk','it','es') ";
		}else if("am".equals(countryCode)){
			sql+=" AND a.`country` IN ('com','ca') ";
		}else{
			sql+=" AND a.`country` = '"+countryCode+"'";
		}
		sql+=" GROUP BY a.`product_name`,a.`color` order by totalQuantity desc";
		List<Object[]> list = inventoryFbaDao.findBySql(sql);
		Map<String,Integer> rs = Maps.newLinkedHashMap();
		for(Object[] obj:list){
			String productName=obj[0].toString();
			Integer quantity = Integer.parseInt(obj[1].toString());
			rs.put(productName, quantity);
		}
		return rs;
	}
	
	/**
	 *美国本地仓产品的总库存数 
	 *2017-08-24
	 */
	public Map<String,Integer> findUsHisInventoryByDataDate(String country,String dataDate){
		String 	sql = "SELECT t.`product_name`,t.`overseas` FROM `psi_product_in_stock` t WHERE t.`data_date`='"+dataDate+"' AND t.`country`='"+country+"' AND t.`overseas`>0;	";
		List<Object[]> list = inventoryFbaDao.findBySql(sql);
		Map<String,Integer> rs = Maps.newLinkedHashMap();
		for(Object[] obj:list){
			String productName=obj[0].toString();
			Integer quantity = Integer.parseInt(obj[1].toString());
			rs.put(productName, quantity);
		}
		return rs;
	}
	
	public List<Object[]> findErrorInventoryBySku(){
		String sql="SELECT DISTINCT aa.sku,aa.date2,aa.date1,aa.qty,SUM(t.quantity_ordered),aa.lastQty  FROM "+
				" (SELECT aa.sku,aa.date1,bb.date2,(bb.qty-aa.qty) qty,aa.qty lastQty FROM   "+
				" (SELECT a.`sku`,MAX(a.`last_update_date`) date1,SUM(a.`fulfillable_quantity`+IFNULL(a.`transit_quantity`,0)+IFNULL(a.`orrect_quantity`,0)) qty FROM `psi_inventory_fba` a WHERE a.`data_date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) GROUP BY a.sku ) aa , "+ 
				" (SELECT a.`sku`,MIN(a.`last_update_date`) date2,SUM(a.`fulfillable_quantity`+IFNULL(a.`transit_quantity`,0)+IFNULL(a.`orrect_quantity`,0)) qty FROM `psi_inventory_fba` a WHERE a.`data_date` = DATE_ADD(CURDATE(),INTERVAL -2 DAY) GROUP BY a.sku ) bb "+
				" WHERE aa.sku = bb.sku AND (bb.qty-aa.qty)!=0  "+
				" ) aa   "+
				" JOIN amazoninfo_orderitem t ON t.sellersku=aa.sku  "+
				" JOIN amazoninfo_order r ON r.id=t.order_id   "+
				" WHERE r.create_date>=aa.date2 AND r.create_date<=aa.date1 AND r.order_status NOT IN ('Canceled','Pending') "+
				" GROUP BY aa.sku,aa.date1,aa.date2,aa.qty,aa.lastQty  HAVING (qty-SUM(t.quantity_ordered)>50 OR SUM(t.quantity_ordered)-qty>50) and (  (SUM(t.quantity_ordered)<10 AND qty/5>SUM(t.quantity_ordered))  "+
				" OR (  (SUM(t.quantity_ordered)<10 AND qty/5<=SUM(t.quantity_ordered)) OR SUM(t.quantity_ordered)>=10  )     ) ";
		return  inventoryFbaDao.findBySql(sql);
	}
	
//	public static void main(String[] args) throws ParseException {
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring-context.xml");
//		PsiInventoryFbaService saleReporService = context.getBean(PsiInventoryFbaService.class);
//		saleReporService.getPreOutOfStock();
//	}
	
}




