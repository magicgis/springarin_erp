package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonProduct2Dao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct2;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPromotionsWarning;
import com.springrain.erp.modules.amazoninfo.scheduler.SaveHistoryInfoMonitor;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;

/**
 * 亚马逊产品Service
 * @author tim
 * @version 2014-06-04
 */
@Component
@Transactional(readOnly = true)
public class AmazonProduct2Service extends BaseService {
	@Autowired
	private AmazonProduct2Dao 			amazonProduct2Dao;
	@Autowired
	private PsiInventoryFbaService 		inventoryFbaService;
	@Autowired
	private PsiProductEliminateService 	eliminateService;
	@Autowired
	private SaleReportService 			saleReportService;
	
	public AmazonProduct2 get(Integer id) {
		return amazonProduct2Dao.get(id);
	}
	
	public Page<AmazonProduct2> find(Page<AmazonProduct2> page, AmazonProduct2 amazonProduct2) {
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(amazonProduct2.getSku())){
			dc.add(Restrictions.or(Restrictions.like("sku", "%"+amazonProduct2.getSku()+"%"),Restrictions.like("asin", "%"+amazonProduct2.getSku()+"%"),Restrictions.like("ean", "%"+amazonProduct2.getSku()+"%"),Restrictions.like("fnsku", "%"+amazonProduct2.getSku()+"%")));
		}
		
		if(StringUtils.isNotEmpty(amazonProduct2.getCountry())){
			dc.add(Restrictions.eq("country",amazonProduct2.getCountry()));
		}
		dc.add(Restrictions.eq("active",amazonProduct2.getActive()));
		return amazonProduct2Dao.find(page, dc);
	}
	
	public Map<String,Map<String,AmazonProduct2>> find(Set<String> skus){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.in("sku",skus));
		dc.add(Restrictions.eq("active","1"));
		List<AmazonProduct2>  list = amazonProduct2Dao.find(dc);
		Map<String,Map<String,AmazonProduct2>> rs = Maps.newHashMap();
		for (AmazonProduct2 amazonProduct2 : list) {
			String country = amazonProduct2.getCountry();
			String sku = amazonProduct2.getSku();
			Map<String,AmazonProduct2> temp = rs.get(country);
			if(temp==null){
				temp = Maps.newHashMap();
				rs.put(country, temp);
			}
			temp.put(sku, amazonProduct2);
		}
		return rs;
	}
	
	
	public Map<String, Float> findProductPrice(String sku) {
		String sql = "select country,sale_price from amazoninfo_product2 where sku=:p1 and active = '1'";
		List<Object[]> rs = amazonProduct2Dao.findBySql(sql, new Parameter(sku));
		Map<String, Float> result = Maps.newHashMap();
		for (Object[] obj : rs) {
			if(obj[1]!=null){
				result.put(obj[0].toString(),((BigDecimal)obj[1]).floatValue());
			}
		}
		return result;
	}
	
	public List<AmazonProduct2> find(String country){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.eq("country",country));
		return amazonProduct2Dao.find(dc);
	}
	
	public List<AmazonProduct2> findByAccount(String country,String accountName){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.eq("accountName",accountName));
		return amazonProduct2Dao.find(dc);
	}
	
	public Map<String,Map<String,List<AmazonProduct2>>> findLocalProduct(){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.eq("active","1"));
		dc.add(Restrictions.eq("isFba","0"));
		List<AmazonProduct2>  list=amazonProduct2Dao.find(dc);
		Map<String,Map<String,List<AmazonProduct2>>> map=Maps.newHashMap();
		for (AmazonProduct2 product : list) {
			Map<String,List<AmazonProduct2>> temp=map.get(product.getCountry());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(product.getCountry(),temp);
			}
			List<AmazonProduct2> tempList=temp.get(product.getAsin());
			if(tempList==null){
				tempList=Lists.newArrayList();
				temp.put(product.getAsin(), tempList);
			}
			tempList.add(product);
		}
		return map;
	}
	
	public AmazonProduct2 isExist(String country,String asin,String sku){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.eq("active","1"));
		dc.add(Restrictions.eq("isFba","0"));
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.eq("asin",asin));
		dc.add(Restrictions.eq("sku",sku));
		List<AmazonProduct2>  list=amazonProduct2Dao.find(dc);
		if(list!=null&&list.size()==1){
			return list.get(0);
		}
		return null;
	}
	
	
	public List<AmazonProduct2> findAllActive(String country){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(country)){
			dc.add(Restrictions.eq("country",country));
		}
		dc.add(Restrictions.eq("active","1"));
		dc.addOrder(Order.asc("country"));
		return amazonProduct2Dao.find(dc);
	}
	
	public List<AmazonProduct2> findEmptyFnskuOrAsin(List<String> countrys,String accountName){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.in("country",countrys));
		dc.add(Restrictions.or(Restrictions.isNull("fnsku"),Restrictions.isNull("asin"),Restrictions.eq("fnsku",""),Restrictions.eq("asin","")));
		if(StringUtils.isNotBlank(accountName)&&accountName.contains("_")){
			dc.add(Restrictions.like("accountName",accountName.split("_")[0]+"%"));
		}
		return amazonProduct2Dao.find(dc);
	}
	
	
	@Transactional(readOnly = false)
	public void save(AmazonProduct2 amazonProduct2) {
		amazonProduct2Dao.save(amazonProduct2);
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonProduct2> amazonProduct2) {
		amazonProduct2Dao.save(amazonProduct2);
	}
	
	public List<AmazonProduct2> findWarnPriceProduct(){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.gt("warnPrice",0f),Restrictions.gt("highWarnPrice",0f)));
		return amazonProduct2Dao.find(dc);
	}
	
	public List<AmazonProduct2> findActiveProduct(){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.eq("active","1"));
		return amazonProduct2Dao.find(dc);
	}
	
	
	public AmazonProduct2 findWarnPriceProduct(String accountName,String sku){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.and(Restrictions.eq("sku",sku),Restrictions.eq("accountName",accountName)));
		List<AmazonProduct2> rs =  amazonProduct2Dao.find(dc);
		if(rs.size()==1){
			return rs.get(0);
		}
		return null;
	}
	
	public String findFnsku(String country,String sku){
		String sql = "SELECT  DISTINCT CASE WHEN a.`fnsku` != a.asin and a.`fnsku` is not null and a.fnsku != '' THEN CONCAT('FNSKU',':',a.`fnsku`) when a.`fnsku` = a.asin and a.`fnsku` is not null and a.fnsku != ''  then  CONCAT('EAN',':',a.`ean`) else '' END   FROM amazoninfo_product2 a WHERE a.`country` = :p1 AND BINARY(a.`sku`) = :p2 ";
		List<String> rs = amazonProduct2Dao.findBySql(sql,new Parameter(country,sku));
		if(rs.size()==1){
			String result = rs.get(0);
			return result;
		}
		return null;
	}
	
	public String findAsin(String country,String sku){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.and(Restrictions.eq("sku",sku),Restrictions.eq("country",country)));
		List<AmazonProduct2> rs =  amazonProduct2Dao.find(dc);
		if(rs.size()==1){
			return  rs.get(0).getAsin();
		}
		return null;
	}
	
	public Map<String,String> getAllAsinByCountrySku(){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT country,sku,ASIN FROM amazoninfo_product2 where country is not null and sku is not null and asin is not null";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj: list) {
			map.put(obj[0].toString()+"_"+obj[1].toString(), obj[2].toString());
		}
		return map;
	}
	
	public List<AmazonProduct2> findPossibleSku(String country,String name){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		String temp = country.toUpperCase();
		if("COM".equals(temp)){
			temp = "US";
		}
		dc.add(Restrictions.and(Restrictions.like("sku","%-"+name+"-%"),Restrictions.eq("country",country)));
		dc.add(Restrictions.not(Restrictions.like("sku","%+%")));
		dc.add(Restrictions.not(Restrictions.like("sku","%#%")));
		dc.add(Restrictions.eq("active","1"));
		List<AmazonProduct2> rs =  amazonProduct2Dao.find(dc);
		return rs;
	}
	
	
	public List<Object> findSku(String country){
		String sql = "SELECT DISTINCT a.`sku` FROM amazoninfo_product2 a WHERE a.`sku` !='' AND a.`country` =:p1 and a.`active`='1'";
		List<Object> list = amazonProduct2Dao.findBySql(sql,new Parameter(country));
		return list;
	}
	
	
	public Set<String> findUnMateSku(String country,String account){
		Set<String> set = new HashSet<String>();
		if("ebay".equals(country)){
			String sql= "SELECT DISTINCT s.sku FROM psi_sku AS s WHERE s.del_flag='0' AND s.country='ebay'";
			List<Object> objs=amazonProduct2Dao.findBySql(sql,null);
			if(objs.size()>0){
				sql="SELECT DISTINCT e.sku FROM ebay_order r join ebay_orderitem AS e on r.id=e.order_id WHERE r.country='de' " +
						" AND r.`created_time`>DATE_ADD(CURRENT_DATE(),INTERVAL -1 MONTH) and e.sku IS NOT NULL  AND e.sku!='' AND e.sku NOT IN :p1";
				 List<String> list=amazonProduct2Dao.findBySql(sql,new Parameter(objs));
				set.addAll(list);
			}
		}else if("ebay_com".equals(country)){
			String sql= "SELECT DISTINCT s.sku FROM psi_sku AS s WHERE s.del_flag='0' AND s.country='ebay_com'";
			List<Object> objs=amazonProduct2Dao.findBySql(sql,null);
			if(objs.size()>0){
				sql="SELECT DISTINCT e.sku FROM ebay_order r join ebay_orderitem AS e on r.id=e.order_id WHERE r.country='com' AND r.`created_time`>DATE_ADD(CURRENT_DATE(),INTERVAL -1 MONTH) and e.sku IS NOT NULL  AND e.sku!='' AND e.sku NOT IN :p1";
				 List<String> list=amazonProduct2Dao.findBySql(sql,new Parameter(objs));
				set.addAll(list);
			}else{
				sql="SELECT DISTINCT e.sku FROM ebay_order r join ebay_orderitem AS e on r.id=e.order_id WHERE r.country='com' AND r.`created_time`>DATE_ADD(CURRENT_DATE(),INTERVAL -1 MONTH) and e.sku IS NOT NULL  AND e.sku!=''";
				 List<String> list=amazonProduct2Dao.findBySql(sql);
				set.addAll(list);
			}
		}else{
			String makeSql="SELECT DISTINCT s.sku FROM psi_sku AS s WHERE s.del_flag='0' and s.country=:p1 and s.account_name=:p2 ";
			List<String> makeSkuList=amazonProduct2Dao.findBySql(makeSql,new Parameter(country,account));
			/*String curAmaSql = "SELECT DISTINCT e.`sellersku` FROM `amazoninfo_order` r JOIN `amazoninfo_orderitem` AS e ON r.id=e.order_id "+
					" WHERE r.`sales_channel`=:p1 AND r.`purchase_date`>DATE_ADD(CURRENT_DATE(),INTERVAL -1 MONTH) AND  "+
					" e.`sellersku` IS NOT NULL  AND e.`sellersku`!='' AND e.`sellersku` NOT IN :p2";*/
			
			if(makeSkuList.size()>0){
				/*String salesChannel = "Amazon." +country;
				if ("jp,uk".contains(salesChannel)) {
					salesChannel = "Amazon.co." +country;
				} else if ("mx".equals(country)) {
					salesChannel = "Amazon.com." +country;
				}
				List<String> curAmaSkuList =amazonProduct2Dao.findBySql(curAmaSql,new Parameter(salesChannel,makeSkuList));
				set.addAll(curAmaSkuList);*/
				String curAmaSql="SELECT DISTINCT a.sku FROM amazoninfo_product2 as a  WHERE a.country=:p1 and a.account_name=:p2 and a.active='1' and sku not in :p3";
				List<String> curAmaSkuList =amazonProduct2Dao.findBySql(curAmaSql, new Parameter(country,account,makeSkuList));
				if(curAmaSkuList != null && curAmaSkuList.size() > 0){
					set.addAll(curAmaSkuList);
				}
			} else {	//没有已匹配的sku
				String curAmaSql="SELECT DISTINCT a.sku FROM amazoninfo_product2 as a  WHERE a.country=:p1 and a.account_name=:p2 and a.active='1'";
				List<String> curAmaSkuList =amazonProduct2Dao.findBySql(curAmaSql, new Parameter(country,account));
				if(curAmaSkuList != null && curAmaSkuList.size() > 0){
					set.addAll(curAmaSkuList);
				}
			}
			
		}
		return set;
	}
	
	
	public List<Object> findAllProductsFromBarcode(String country){
		Parameter parameter = null;
		String sql="SELECT CONCAT(b.psi_product,CASE  WHEN b.product_color='' THEN '' ELSE CONCAT('_',b.product_color) END) aa,CONCAT(b.product_name,CASE  WHEN b.product_color='' THEN '' ELSE CONCAT('_',b.product_color) END)  FROM psi_barcode AS b  WHERE b.del_flag='0' AND b.product_platform=:p1";
		parameter= new Parameter(country);
		List<Object> list = amazonProduct2Dao.findBySql(sql,parameter);
		return list;
	}
	  
	public String findProductImage(String sku){
		String sql = "SELECT b.`image` FROM psi_sku a, psi_product b WHERE a.`product_name` = CONCAT(b.`brand`,' ',b.`model`) AND a.`del_flag`='0' and b.image is not null AND a.`sku`=:p1";
		List<Object> list = amazonProduct2Dao.findBySql(sql,new Parameter(sku));
		if(list.size()>0){
			return "/inateck-erp"+list.get(0).toString();
		}
		return "";
	}
	
	public Map<String,Float> getPriceByAsin(Set<String> asins,String country){
		String sql = "SELECT a.`asin`,MIN(CASE  WHEN a.`fnsku` IS NULL THEN  (3.99+a.`sale_price`) ELSE a.`sale_price` END) AS price FROM amazoninfo_product2 a WHERE a.`asin` in :p1 AND a.`country`= :p2  GROUP BY a.`asin`";
		List<Object> list = amazonProduct2Dao.findBySql(sql, new Parameter(asins,country));
		Map<String,Float> rs = Maps.newHashMap();
		for (Object object : list) {
			Object[]objs = (Object[])object;
			Float price = null;
			if(objs[1]!=null){
				price = Float.parseFloat(objs[1].toString());
			}
			rs.put(objs[0].toString(),price);
		}
		return rs;
	}
	
	public void findFnskuByFbaInfo(String country,String sku,AmazonProduct2 amazonProduct2,String accountName){
		String sql = "SELECT DISTINCT a.`fnsku` ,a.asin FROM psi_inventory_fba  a WHERE a.`sku` = BINARY(:p1) and a.country = :p2 and account_name=:p3";
		List<Object[]> list =  amazonProduct2Dao.findBySql(sql, new Parameter(sku,country,accountName));
		if(list.size() ==1){
			String fnsku = (list.get(0)[0]).toString();
			String asin = (list.get(0)[1]).toString();
			if(StringUtils.isNotEmpty(fnsku)){
				amazonProduct2.setFnsku(fnsku);
			}
			if(StringUtils.isNotEmpty(asin)){
				amazonProduct2.setAsin(asin);
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void updateBarcode(){
		String sql = "SELECT DISTINCT a.`sku`,c.`fnsku`,c.`asin`,c.`ean`, b.`id` FROM psi_sku a , psi_barcode b,amazoninfo_product2 c WHERE a.`sku`=c.`sku` AND  c.`fnsku` IS NOT NULL AND  a.`barcode` = b.`id` AND a.`use_barcode` = '1' AND a.`del_flag` = '0' AND b.`del_flag` = '0' AND (b.`barcode` IS NULL OR b.`barcode` = '')";
		List<Object[]> list = amazonProduct2Dao.findBySql(sql);
		for (Object[] objs : list) {
			String fnsku = objs[1].toString();
			String asin = objs[2].toString();
			String ean = objs[3]==null?"":objs[3].toString();
			int id =(Integer)objs[4];
			String type ="FNSKU";
			if(fnsku.equals(asin)){
				type = "EAN";
				fnsku = ean;
			}
			sql = "UPDATE psi_barcode SET barcode = :p1 , barcode_type = :p2 WHERE id= :p3 ";
			amazonProduct2Dao.updateBySql(sql, new Parameter(fnsku,type,id));
		}
	}
	
	
	public List<Object[]> findPriceChangeProduct(){
		String sql = "SELECT a.`sku`,a.`country`,COUNT(1) AS num,MAX(a.`sale_price`) maxprice,MIN(a.`sale_price`) minprice,TRUNCATE(((MAX(a.`sale_price`)/MIN(a.`sale_price`)-1)*100),2) bini  FROM amazoninfo_product_history_price a " +
				"	WHERE a.`data_date` >= DATE_ADD(CURDATE(),INTERVAL -2 DAY) GROUP BY a.`sku`,a.`country` HAVING  num =2 AND maxprice != minprice  AND maxprice !=0 AND minprice !=0 ORDER BY a.`country`, bini DESC";
		return amazonProduct2Dao.findBySql(sql);
	}
	
	public Map<String,Map<String,String>> getPriceChangeReason(){
		String sql=" SELECT f.`country`,p.sku,f.`reason`,MAX(request_date) FROM amazoninfo_price_feed f JOIN amazoninfo_price p ON f.id=p.`feed_price_feed_id`  "+
				" WHERE f.`reason` IS NOT NULL AND request_date>=DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND request_date< CURDATE() AND NOT(f.`reason` LIKE '%包邮%') "+
				" GROUP BY f.country,p.sku ";
		Map<String,Map<String,String>> map=Maps.newHashMap();
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj: list) {
			Map<String,String> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(),temp);
			}
			temp.put(obj[1].toString(), (obj[2]==null?"":obj[2].toString()));
		}
		return map;
	}
	
	public AmazonProduct2 getProduct(String country ,String sku){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.eq("sku", sku));
		dc.add(Restrictions.eq("country",country));
		List<AmazonProduct2> list =  amazonProduct2Dao.find(dc);
		if(list.size()==1){
			return list.get(0);
		}else if(list.size()>1){
			for (AmazonProduct2 amazonProduct: list) {
				if(sku.equals(amazonProduct.getSku())){
					return amazonProduct;
				}
			}
		}
		return null;
	}
	
	public AmazonProduct2 getProductByAccount(String accountName ,String sku){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.eq("sku", sku));
		dc.add(Restrictions.eq("accountName",accountName));
		List<AmazonProduct2> list =  amazonProduct2Dao.find(dc);
		if(list.size()==1){
			return list.get(0);
		}else if(list.size()>1){
			for (AmazonProduct2 amazonProduct: list) {
				if(sku.equals(amazonProduct.getSku())){
					return amazonProduct;
				}
			}
		}
		return null;
	}
	
	public Float getProductPriceYesterday(String country ,String sku){
		String sql = "SELECT  a.`sale_price` FROM amazoninfo_product_history_price a WHERE  a.`data_date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND a.`sku` = :p1 AND a.`country` = :p2";
		List<Object> list = amazonProduct2Dao.findBySql(sql, new Parameter(sku,country));
		if(list.size()==1){
			return ((BigDecimal)list.get(0)).floatValue();
		}
		return null;
	}
	
	public Float getProductPriceMaxday(String country ,String sku){
		String sql = "SELECT  a.`sale_price` FROM amazoninfo_product_history_price a WHERE a.`data_date`=(SELECT MAX(data_date) FROM amazoninfo_product_history_price WHERE sku=:p1 AND country=:p2) AND a.`sku` = :p1 AND a.`country` = :p2";
		List<Object> list = amazonProduct2Dao.findBySql(sql, new Parameter(sku,country));
		if(list.size()==1){
			return ((BigDecimal)list.get(0)).floatValue();
		}
		return null;
	}
	
	
	public Map<String,Map<String,Float>> getProductPriceMaxdayByName(){
		Map<String,Map<String,Float>> map=Maps.newHashMap();
		String sql="SELECT DISTINCT a.`country`,CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN CONCAT ('_',b.`color`) ELSE '' END) NAME, "+
        " a.`sale_price` FROM amazoninfo_product_history_price a JOIN psi_sku b ON a.`country`=b.`country` AND a.`sku`=b.`sku` "+
        " WHERE  a.`data_date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) and LOWER(a.sku) not like '%local%' AND a.`sale_price` >0 ORDER BY country,NAME";
		List<Object[]> list = amazonProduct2Dao.findBySql(sql);
		for (Object[] obj : list) {
			Map<String,Float> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(),temp);
			}
			temp.put(obj[1].toString(), ((BigDecimal)obj[2]).floatValue());
		}
		return map;
	}
	
	public Float getLatestPrice(String country,String productName){
		String sql="SELECT  a.`sale_price` FROM amazoninfo_product_history_price a JOIN psi_sku b ON a.`country`=b.`country` AND a.`sku`=b.`sku` "+
				 " WHERE  a.`data_date` =(SELECT MAX(c.`data_date`) FROM amazoninfo_product_history_price c JOIN  psi_sku d ON c.`country`=d.`country` AND c.`sku`=d.`sku`  "+
				 " AND CONCAT(d.`product_name`,CASE WHEN d.`color`!='' THEN CONCAT ('_',d.`color`) ELSE '' END)=:p1 AND d.`country`=:p2 and LOWER(c.sku) not like '%local%') "+
				"  AND CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN CONCAT ('_',b.`color`) ELSE '' END)=:p1 AND b.`country`=:p2 AND a.`sale_price` >0 and LOWER(a.sku) not like '%local%' ";
        List<Object> list=amazonProduct2Dao.findBySql(sql,new Parameter(productName,country));
		if(list.size()>0){
			return ((BigDecimal)list.get(0)).floatValue();
		}
		return null;
	}
	
	public Float getCrossFee(String country,String productName){
		String sql="SELECT fba FROM amazoninfo_product_price "+
           " WHERE product_name=:p1 AND country=:p2 and type='1' and DATE=(SELECT MAX(DATE) FROM amazoninfo_product_price WHERE product_name=:p1 AND country=:p2 and type='1')";//cross fee
		List<Object> list=amazonProduct2Dao.findBySql(sql,new Parameter(productName,country));
		if(list.size()>0){
			return ((BigDecimal)list.get(0)).floatValue();
		}
		return null;
	}
	
	public Object[] getLocalFee(String country,String productName){
		String sql="SELECT fba,amz_price FROM amazoninfo_product_price "+
           " WHERE product_name=:p1 AND country=:p2 and type='0' and DATE=(SELECT MAX(DATE) FROM amazoninfo_product_price WHERE product_name=:p1 AND country=:p2 and type='0')";//本国 fee
		List<Object[]> list=amazonProduct2Dao.findBySql(sql,new Parameter(productName,country));
		if(list.size()>0){
			for (Object[] obj : list) {
				return obj;
			}
		}
		return null;
	}
	
	public Map<String, Float> getAllProductPriceYesterday(){
		String sql = "SELECT CONCAT(a.`sku`,'_',a.`country`),a.`sale_price` FROM amazoninfo_product_history_price a WHERE  a.`data_date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND a.`sale_price` >0";
		List<Object[]> list = amazonProduct2Dao.findBySql(sql);
		Map<String, Float> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(),((BigDecimal)objects[1]).floatValue());
		}
		return rs;
	}
	
	public Float getSalePrice(String country,String asin){
		String sql="SELECT sale_price FROM amazoninfo_product2 WHERE ASIN=:p1 AND country=:p2 AND active='1' AND is_fba='1' AND sale_price IS NOT NULL";
		List<Object> list = amazonProduct2Dao.findBySql(sql,new Parameter(asin,country));
		if(list!=null&&list.size()>0){
			for (Object objects : list) {
				return ((BigDecimal)objects).floatValue();
			}
		}
		return null;
	}
	//sku / name
	public Map<String,String> findAllProductNamesWithSku(){
		String sql = "SELECT DISTINCT a.`sku`,CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) AS productName FROM psi_sku a WHERE  NOT(a.`product_name` LIKE '%other%' or a.product_name like '%Old%') AND a.`del_flag`='0'";
		List<Object[]> list = amazonProduct2Dao.findBySql(sql);
		Map<String, String> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(),objects[1].toString());
		}
		return rs;
	}
	
	public List<Object> findAllProductNames(){
		String sql = "SELECT DISTINCT CONCAT(a.`product_name`,CASE  WHEN a.`product_color`='' THEN '' ELSE CONCAT('_',a.`product_color`) END) AS productName FROM psi_barcode a "+
	    " join psi_product p on p.id=a.psi_product "+
	    " WHERE a.`del_flag`='0' and p.del_flag='0' and (p.components is null or p.components='0') ";
		List<Object> list = amazonProduct2Dao.findBySql(sql);
		return list;
	}
	
	public List<String> findUnionProductNames(){
		String hiddenSql = "SELECT DISTINCT t.`product_name` FROM `psi_product_hidden` t";
		List<String> hiddenList = amazonProduct2Dao.findBySql(hiddenSql);
		List<String> list =Lists.newArrayList();
		String unionSql="SELECT DISTINCT CONCAT(brand,' ',model_short),color FROM psi_product WHERE model!=model_short and (components is null or components='0') AND del_flag='0'";
		List<Object[]> tempList = amazonProduct2Dao.findBySql(unionSql);
		if(tempList!=null&&tempList.size()>0){
			for (Object[] obj : tempList) {
				String name=obj[0].toString();
				String color=(obj[1]==null?"":obj[1].toString());
				if(color.contains(",")){
					list.add(name+"[GROUP]");
					String[] arrStr=color.split(",");
					for (String arr : arrStr) {
						list.add(name+"_"+arr+"[GROUP]");
					}
				}else if(StringUtils.isNotBlank(color)){
					list.add(name+"_"+color+"[GROUP]");
				}else{
					list.add(name+"[GROUP]");
				}
			}
		}
		String unionSql2="SELECT DISTINCT CONCAT(brand,' ',model_short) FROM psi_product WHERE model=model_short and (components is null or components='0') and color like '%,%' AND del_flag='0'";
		List<String> nameList=amazonProduct2Dao.findBySql(unionSql2);
		for (String name: nameList) {
			list.add(name+"[GROUP]");
		}
		
		String sql = "SELECT DISTINCT CONCAT(a.`product_name`,CASE  WHEN a.`product_color`='' THEN '' ELSE CONCAT('_',a.`product_color`) END) AS productName FROM psi_barcode a "+
				  " join psi_product p on p.id=a.psi_product "+
		          " WHERE a.`del_flag`='0' and (p.components is null or p.components='0') ";
		List<String> allList=amazonProduct2Dao.findBySql(sql);
		list.addAll(allList);
		for (String productName : hiddenList) {
			if (list.contains(productName)) {
				list.remove(productName);
			}
		}
		return list;
	}
	
	
	public Integer findProductPackBySku(String sku){
		String sql = "SELECT a.`pack_quantity` FROM psi_product a WHERE a.`del_flag`='0' AND  a.`id`=(SELECT DISTINCT b.`product_id` FROM psi_sku b WHERE b.`sku`=:p1 and not(b.product_name like '%other%') and not(b.product_name like '%old%') AND b.`del_flag`='0' AND b.`country` != 'ebay') ";
		List<Object> list;
		try {
			list = amazonProduct2Dao.findBySql(sql,new Parameter(sku));
			if(list.size()>0){
				return (Integer)list.get(0);
			}
		} catch (Exception e) {
			logger.warn(sku+"获取装箱数时"+e.getMessage());
		}
		return 0;
	}
	
	public boolean countProductBySku(String sku){
		String sql = "SELECT COUNT(1) FROM amazoninfo_product2 a WHERE  a.`sku` = BINARY(:p1) and a.asin !='' and a.fnsku !='' ";
		return ((BigInteger)amazonProduct2Dao.findBySql(sql, new Parameter(sku)).get(0)).intValue()>0;
	}
	
	public Object[] findProductPackAndTypeBySku(String sku){
		Object[] obj=new Object[3];
		String sql = "SELECT SUBSTRING_INDEX(a.`chinese_name`,';',-1),a.`pack_quantity`,a.weight FROM psi_product a WHERE a.`del_flag`='0' AND  a.`id`=(SELECT b.`product_id` FROM psi_sku b WHERE b.`sku`=:p1  AND b.`del_flag`='0' AND b.`country` != 'ebay' limit 1) ";
		List<Object[]> list = amazonProduct2Dao.findBySql(sql,new Parameter(sku));
		if(list.size()>0){
			obj[0]=list.get(0)[0]==null?"":list.get(0)[0].toString();
			obj[1]=(Integer)list.get(0)[1];
			obj[2]=(BigDecimal)(list.get(0)[2]);
			return obj;
		}
		return null;
	}
	
	public Integer findProductReserved(String sku,String country){
		String sql = "SELECT SUM(b.`quantity_ordered`) FROM amazoninfo_order a ,amazoninfo_orderitem b   WHERE a.`id` = b.`order_id` AND b.`sellersku` = :p2 AND a.`sales_channel` LIKE :p1 AND a.`order_status` IN ('Pending','Shipped') AND a.`last_update_date` > DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY)";
		country = "%"+country+"%";
		List<Object> list=null;
		try {
			list = amazonProduct2Dao.findBySql(sql,new Parameter(country,sku));
			if(list!=null&&list.size()>0){
				if(list.get(0)!=null){
					return ((BigDecimal)list.get(0)).intValue();
				}
			}
		} catch (Exception e) {
			logger.warn(sku+"获取3天销量时"+e.getMessage());
		}
		return null;
	}
	
	public Map<String,Integer> findProductReserved(){
		String sql = "SELECT b.`sellersku`,SUM(b.`quantity_ordered`) FROM amazoninfo_order a ,amazoninfo_orderitem b   WHERE a.`id` = b.`order_id`  AND a.`sales_channel` IN ('Amazon.de','Amazon.it','Amazon.fr','Amazon.co.uk','Amazon.com','Amazon.es') AND a.`order_status` IN ('Pending','Shipped') AND a.`last_update_date` > DATE_ADD(CURRENT_DATE(), INTERVAL -1 DAY) GROUP BY b.`sellersku`";
		List<Object[]> list=null;
		try {
			
			list = amazonProduct2Dao.findBySql(sql,null);
			if(list!=null&&list.size()>0){
				Map<String,Integer> rs = Maps.newHashMap();
				for (Object[] objs : list) {
					if(objs[1]!=null){
						rs.put(objs[0].toString(), Integer.parseInt(objs[1].toString()));
					}
				}
				return rs;
			}
		} catch (Exception e) {
			logger.warn("获取3天销量时"+e.getMessage(),e);
		}
		return Maps.newHashMap();
	}
	
	public Map<String,Integer> findFbaPackInTran(String shipmentIdOrPlanId){
		String sql = "SELECT b.`sku`,b.`pack_quantity` FROM lc_psi_transport_order a ,lc_psi_transport_order_item b WHERE a.`id` = b.`transport_order_id` AND ((FIND_IN_SET(:p1, a.`shipment_id`) OR FIND_IN_SET(:p1, a.`fba_inbound_id`)) )  AND b.`del_flag` = '0' AND b.`pack_quantity`>0";
		
		List<Object[]> list = amazonProduct2Dao.findBySql(sql,new Parameter(shipmentIdOrPlanId));
		Map<String,Integer> rs = Maps.newHashMap();
		if(list.size()>0){
			for (Object[] objects : list) {
				rs.put(objects[0].toString(),Integer.parseInt(objects[1].toString()));
			}
		}
		return rs;
	}
	
	public Map<String,Integer> findProductPackBySkus(Set<String> skus,String country){
		String sql = "SELECT bb.sku,a.`pack_quantity` FROM psi_product a,(SELECT b.`product_id`,b.`sku` FROM psi_sku b WHERE b.`sku` IN :p1 AND b.`country`=:p2 AND  b.`del_flag`='0' AND b.`country` != 'ebay') AS bb WHERE a.`del_flag`='0' AND a.`id` = bb.product_id  and a.`pack_quantity`>0";
		Map<String,Integer> rs = Maps.newHashMap();
		try {
			List<Object[]> list = amazonProduct2Dao.findBySql(sql,new Parameter(skus,country));
			if(list.size()>0){
				for (Object[] objects : list) {
					rs.put(objects[0].toString(),(Integer)objects[1]);
				}
			}
		} catch (Exception e) {
			logger.warn(skus+"获取装箱数时"+e.getMessage());
		}
		return rs;
	}
	
	public Map<String,Integer> findProductPackBySkus(String country){
		String sql = "SELECT bb.sku,a.`pack_quantity` FROM psi_product a,(SELECT b.`product_id`,b.`sku` FROM psi_sku b WHERE b.`country`=:p1 AND  b.`del_flag`='0' AND b.`country` != 'ebay') AS bb WHERE a.`del_flag`='0' AND a.`id` = bb.product_id  and a.`pack_quantity`>0";
		Map<String,Integer> rs = Maps.newHashMap();
		try {
			List<Object[]> list = amazonProduct2Dao.findBySql(sql,new Parameter(country));
			if(list.size()>0){
				for (Object[] objects : list) {
					rs.put(objects[0].toString(),(Integer)objects[1]);
				}
			}
		} catch (Exception e) {
			logger.warn("获取装箱数时"+e.getMessage());
		}
		return rs;
	}
	
	public List<Map<String, String>> findAsinsByReview(){
		String sql = "SELECT DISTINCT b.`asin` FROM amazoninfo_orderitem b,amazoninfo_order a WHERE a.`id` = b.`order_id` AND a.`order_status`='Shipped' AND a.`purchase_date` > DATE_ADD(CURDATE(),INTERVAL -1 MONTH) AND b.`product_name` !='inateck old' AND b.`product_name` !='inateck other' ";
		List<Object> list = amazonProduct2Dao.findBySql(sql);
		sql = "SELECT DISTINCT  c.`asin`,c.`country`,d.`is_sale` FROM "+
 " psi_product a,psi_sku b,amazoninfo_product2 c,psi_product_eliminate d"+
 " WHERE b.`sku` = c.`sku`  AND b.`country` = c.`country` "+
 " AND b.`country` = d.`country`AND b.`product_name` = d.`product_name` AND b.`color` = d.`color` AND d.`del_flag` = '0' AND a.`id` = b.`product_id`  AND b.`del_flag` = '0'  AND a.`model` != 'other'  AND a.`model` != 'Old'  AND a.`brand` != 'ORICO' AND c.`asin` IS NOT NULL ";
		List<Object[]> data = amazonProduct2Dao.findBySql(sql,null);
		
		List<Map<String, String>> rs = Lists.newArrayList();
		for (Object[] objs : data) {
			String asin = objs[0].toString();
			String isSale = objs[2].toString();
			if((isSale!=null&&"1".equals(isSale)) || list.contains(asin)){
				Map<String, String> map = Maps.newHashMap();
				map.put("asin",asin);
				rs.add(map);
				map.put("country",objs[1].toString());
				map.put("parent","");
			}
		}
		return rs;
	}

	public String getAsinByEan(String ean){
		String sql="SELECT asin FROM amazoninfo_product2 WHERE TRIM(ean)=:p1";
		List<String> list=amazonProduct2Dao.findBySql(sql,new Parameter(ean));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public List<String> getAllAsin(String productName,String country){
		String sql = "SELECT distinct b.asin FROM psi_sku b WHERE b.`country` =:p1 and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) =:p2 and b.del_flag='0' and b.asin is not null  ";
		List<String> list=amazonProduct2Dao.findBySql(sql,new Parameter(country,productName));
		return list;
	}
	
	public List<List<Object>> findMfnInventoyInfo(String country,String accountName){
		String sql = "SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName,a.`sku`,a.asin,a.`quantity` FROM amazoninfo_product2 a,psi_sku b WHERE a.`sku`= b.`sku` and b.`account_name` = a.`account_name` AND a.`active` = '1' AND a.`is_fba` = '0' and not(a.sku like '%-old%') AND a.`account_name` =:p1 ";
		List<Object[]> list = amazonProduct2Dao.findBySql(sql,new Parameter(accountName));
		Map<String,List<Object>> rs = Maps.newHashMap();
		if(list!=null&&list.size()>0){
			for (Object[] objects : list) {
				rs.put(objects[0].toString(), Lists.newArrayList(objects));
			}
			sql = "SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color_code`='' THEN '' ELSE CONCAT('_',b.`color_code`) END) AS productName , sum(b.`new_quantity`) FROM psi_inventory b  WHERE CONCAT(b.`product_name`,CASE  WHEN b.`color_code`='' THEN '' ELSE CONCAT('_',b.`color_code`) END) IN :p1 AND b.`warehouse_id` =:p2 group by productName";
			String wid = "19";
			List<String> countrys = Lists.newArrayList("com");
			if(country.startsWith("com")){
				wid = "120";
			}else if("jp".equals(country)){
				wid="147";
				countrys.clear();
				countrys.add("jp");
			}else if("de".equals(country)){
				countrys.clear();
				countrys.add("de");
				/*countrys.add("fr");
				countrys.add("it");
				countrys.add("es");
				countrys.add("uk");*/
			}
			List<Object[]> list1 = amazonProduct2Dao.findBySql(sql,new Parameter(rs.keySet(),wid));
			
			sql = "SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName,TRUNCATE(AVG(b.`sales_volume`),2) FROM amazoninfo_sale_report b WHERE  b.order_type='1' and b.`country` IN :p1 AND CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) IN :p2 and b.date >=:p3  and  b.date <=:p4  GROUP BY productName";
			Date end = DateUtils.getDateStart(new Date());
			end = DateUtils.addDays(end, -1);
			Date start = DateUtils.addDays(end, -31);
			Map<String,Object> avgSale = Maps.newHashMap();
			List<Object[]> list2 = amazonProduct2Dao.findBySql(sql,new Parameter(countrys,rs.keySet(),start,end));
			for (Object[] objects : list2) {
				avgSale.put(objects[0].toString(), objects[1]);
			}
			Map<String,List<Object>> rs1 = Maps.newHashMap();
			for (Object[] objects : list1) {
				String  pn = objects[0].toString();
				Integer newQ =  0 ;
				try {
					newQ = Integer.parseInt(objects[1].toString());
				} catch (NumberFormatException e) {}
				Object sale = avgSale.get(pn);
				if(sale==null){
					sale = 0;
				}
				rs.get(pn).add(newQ);
				rs.get(pn).add(sale);
				rs1.put(pn, rs.get(pn));
			}
			return Lists.newArrayList(rs1.values());
		}else{
			return null;
		}
		
		
	}
	
    public Map<String, Float> getRateNewest(){
    	String sql = "SELECT MAX(a.`date`) FROM exchange_rate a";
    	List<Object> list = amazonProduct2Dao.findBySql(sql);
    	Map<String,Float> rs = Maps.newHashMap();
    	if(list.size()==1){
    		sql = "SELECT a.`name`,a.`rate` FROM exchange_rate a WHERE a.`date` = :p1";
    		List<Object[]> list1 = amazonProduct2Dao.findBySql(sql,new Parameter(list.get(0)));
    		for (Object[] objects : list1) {
    			rs.put(objects[0].toString(), (Float)objects[1]);
			}
    	}
    	return rs;
    }
	
    public Map<String,Map<String, Float>> getALLRate(){
    	String sql = "SELECT a.date,a.`name`,a.`rate` FROM exchange_rate a order by a.date ";
		List<Object[]> list1 = amazonProduct2Dao.findBySql(sql);
		Map<String,Map<String, Float>> rs = Maps.newLinkedHashMap();
    	for (Object[] objects : list1) {
			String date = DateUtils.getDate((Date)objects[0],"yyyy-MM-dd");
			String name = objects[1].toString();
    		Float rate = (Float)objects[2];
    		Map<String, Float> rateMap = rs.get(date);
			if(rateMap==null){
				rateMap = Maps.newHashMap();
				rs.put(date, rateMap);
			}
			rateMap.put(name, rate);
		}
		return rs;
    }
    
    public Map<String,Map<String, Float>> getRate(Date start,Date end){
    	String sql = "SELECT a.date,a.`name`,a.`rate` FROM exchange_rate a where date>=:p1 and date<=:p2 order by a.date ";
		List<Object[]> list1 = amazonProduct2Dao.findBySql(sql,new Parameter(start,end));
		Map<String,Map<String, Float>> rs = Maps.newLinkedHashMap();
    	for (Object[] objects : list1) {
			String date = DateUtils.getDate((Date)objects[0],"yyyy-MM-dd");
			String name = objects[1].toString();
    		Float rate = (Float)objects[2];
    		Map<String, Float> rateMap = rs.get(date);
			if(rateMap==null){
				rateMap = Maps.newHashMap();
				rs.put(date, rateMap);
			}
			rateMap.put(name, rate);
		}
		return rs;
    }
    
    public Map<String,List<Object[]>> getWarnPosts1(){
		List<String>  skus = inventoryFbaService.findHasInventory();
		String sql="SELECT f.country,f.asin,f.sku,(CASE WHEN p.`active` ='0' THEN '帖子被禁或删除' ELSE '价格不可读' END) reason FROM psi_inventory_fba f "+
           " JOIN  amazoninfo_product2 p ON f.`country`=p.`country` AND f.`sku`=p.`sku`  "+
           " WHERE f.`fulfillable_quantity`<-1  AND  DATE_FORMAT(f.`data_date`,'%Y-%m-%d')=CURDATE() "+
           " AND (p.`sale_price` IS NULL OR p.`active` ='0')  AND p.`is_fba`='1' and f.sku is not null union"+
           " SELECT f.country,f.asin,f.sku,'帖子被禁或删除' reason FROM psi_inventory_fba f  WHERE  f.`fulfillable_quantity`<-1  AND  DATE_FORMAT(f.`data_date`,'%Y-%m-%d')=CURDATE() "+
           " and f.sku is not null AND NOT EXISTS ( SELECT 1 FROM amazoninfo_product2 p WHERE f.`country`=p.`country` AND f.`sku`=p.`sku` ) ";
		Map<String,List<Object[]>> map=Maps.newLinkedHashMap();
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		if(list==null||list.size()<=0){
			sql="SELECT f.country,f.asin,f.sku,(CASE WHEN p.`active` ='0' THEN '帖子被禁或删除' ELSE '价格不可读' END) reason FROM psi_inventory_fba f "+
			           " JOIN  amazoninfo_product2 p ON f.`country`=p.`country` AND f.`sku`=p.`sku` "+
			           " WHERE f.`fulfillable_quantity`>0  AND  DATE_FORMAT(f.`data_date`,'%Y-%m-%d')=DATE_ADD(CURDATE(),INTERVAL -1 DAY) "+
			           " AND (p.`sale_price` IS NULL OR p.`active` ='0')  AND p.`is_fba`='1' and f.sku is not null union"+
			           " SELECT f.country,f.asin,f.sku,'帖子被禁或删除' reason FROM psi_inventory_fba f  WHERE  f.`fulfillable_quantity`>0  AND  DATE_FORMAT(f.`data_date`,'%Y-%m-%d')=DATE_ADD(CURDATE(),INTERVAL -1 DAY) "+
			           " and f.sku is not null AND NOT EXISTS ( SELECT 1 FROM amazoninfo_product2 p WHERE f.`country`=p.`country` AND f.`sku`=p.`sku`  ) ";
			list=amazonProduct2Dao.findBySql(sql);		
		}
		for (Object[] obj : list) {
			if(skus.contains(obj[2].toString())){
				List<Object[]> objList=map.get(obj[0].toString());
				if(objList==null){
					objList=Lists.newArrayList();
					map.put(obj[0].toString(), objList);
				}
				objList.add(obj);
			}
			
		}
		return map;
	}
    
    public Map<String,Integer> countNoPriceProduct(){
    	Map<String,Integer> map=Maps.newHashMap();
    	String sql="SELECT f.account_name,COUNT(*) FROM  psi_inventory_fba f "+
    			 " JOIN amazoninfo_product2  p ON f.`account_name`=p.`account_name` AND f.`sku`=p.`sku`  "+
    			 " WHERE f.`fulfillable_quantity`>0  AND  f.`data_date`=CURDATE() "+
    			 " AND  p.`is_fba`='1' AND p.active='1' AND f.sku IS NOT NULL AND  p.`sale_price` IS NOT NULL GROUP BY f.account_name ";
    	List<Object[]> list=amazonProduct2Dao.findBySql(sql);
    	if(list==null||list.size()<=0){
    		sql="SELECT f.account_name,COUNT(*) FROM  psi_inventory_fba f "+
       			 " JOIN amazoninfo_product2  p ON f.`account_name`=p.`account_name` AND f.`sku`=p.`sku`  "+
       			 " WHERE f.`fulfillable_quantity`>0  AND  f.`data_date`=DATE_ADD(CURDATE(),INTERVAL -1 DAY) "+
       			 " AND  p.`is_fba`='1' AND p.active='1' AND f.sku IS NOT NULL AND  p.`sale_price` IS NOT NULL GROUP BY f.account_name ";
    		list=amazonProduct2Dao.findBySql(sql);		
		}
    	for (Object[] obj : list) {
			map.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
		}
    	return map;
    }
    
    public Map<String,Integer> findInventory(){
    	Map<String,Integer> map=Maps.newHashMap();
    	String sql="SELECT f.account_name,COUNT(*) FROM  psi_inventory_fba f JOIN amazoninfo_product2  p ON f.`account_name`=p.`account_name` AND f.`sku`=p.`sku` "+
   			 " WHERE f.`fulfillable_quantity`>0  AND  f.`data_date`=CURDATE() AND  p.`is_fba`='1' AND p.active='1' AND f.sku IS NOT NULL "+
   			 "  GROUP BY f.account_name ";
       	List<Object[]> list=amazonProduct2Dao.findBySql(sql);
       	if(list!=null&&list.size()>=0){
       		for (Object[] obj : list) {
       			map.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
       		}
       	}
    	return map;
    }
    
    
    public Map<String,List<Object[]>> getWarnPosts(){
		List<String>  skus = inventoryFbaService.findHasInventory();
		String sql="SELECT f.account_name,f.asin,f.sku,(CASE WHEN p.`active` ='0' THEN '帖子被禁或删除' ELSE '价格不可读' END) reason FROM psi_inventory_fba f "+
           " JOIN  amazoninfo_product2 p ON f.`account_name`=p.`account_name` AND f.`sku`=p.`sku`  "+
           " WHERE f.`fulfillable_quantity`>0  AND  f.`data_date`=CURDATE() "+
           " AND (p.`sale_price` IS NULL OR p.`active` ='0')  AND p.`is_fba`='1' and f.sku is not null union"+
           " SELECT f.account_name,f.asin,f.sku,'帖子被禁或删除' reason FROM psi_inventory_fba f  WHERE  f.`fulfillable_quantity`>0  AND  f.`data_date`=CURDATE() "+
           " and f.sku is not null AND NOT EXISTS ( SELECT 1 FROM amazoninfo_product2 p WHERE f.`account_name`=p.`account_name` AND f.`sku`=p.`sku` ) "+
           " union SELECT p.account_name,p.asin,p.sku,'帖子被禁或删除' reason FROM amazoninfo_product2 p "+
           " JOIN amazoninfo_pan_eu e ON p.`asin`=e.`ASIN` "+
           " WHERE  (p.`sale_price` IS NULL OR p.`active` ='0')  AND p.`is_fba`='1' AND p.sku IS NOT NULL AND p.`country` IN ('uk','fr','it','es')  "+
           " AND EXISTS ( SELECT 1 FROM psi_inventory_fba f WHERE f.`country`='de' AND f.`sku`=p.`sku` AND f.`fulfillable_quantity`>0  AND f.`data_date`=CURDATE() ) ";
		Map<String,List<Object[]>> map=Maps.newLinkedHashMap();
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		if(list==null||list.size()<=0){
			sql="SELECT f.account_name,f.asin,f.sku,(CASE WHEN p.`active` ='0' THEN '帖子被禁或删除' ELSE '价格不可读' END) reason FROM psi_inventory_fba f "+
			           " JOIN  amazoninfo_product2 p ON f.`account_name`=p.`account_name` AND f.`sku`=p.`sku` "+
			           " WHERE f.`fulfillable_quantity`>0  AND  f.`data_date`=DATE_ADD(CURDATE(),INTERVAL -1 DAY) "+
			           " AND (p.`sale_price` IS NULL OR p.`active` ='0')  AND p.`is_fba`='1' and f.sku is not null union"+
			           " SELECT f.account_name,f.asin,f.sku,'帖子被禁或删除' reason FROM psi_inventory_fba f  WHERE  f.`fulfillable_quantity`>0  AND  f.`data_date`=DATE_ADD(CURDATE(),INTERVAL -1 DAY) "+
			           " and f.sku is not null AND NOT EXISTS ( SELECT 1 FROM amazoninfo_product2 p WHERE f.`account_name`=p.`account_name` AND f.`sku`=p.`sku`  ) "+
			           " union SELECT p.account_name,p.asin,p.sku,'帖子被禁或删除' reason FROM amazoninfo_product2 p "+
			           " JOIN amazoninfo_pan_eu e ON p.`asin`=e.`ASIN` "+
			           " WHERE  (p.`sale_price` IS NULL OR p.`active` ='0')  AND p.`is_fba`='1' AND p.sku IS NOT NULL AND p.`country` IN ('uk','fr','it','es')  "+
			           " AND EXISTS ( SELECT 1 FROM psi_inventory_fba f WHERE f.`country`='de' AND f.`sku`=p.`sku` AND f.`fulfillable_quantity`>0  AND f.`data_date`=DATE_ADD(CURDATE(),INTERVAL -1 DAY) ) ";
			list=amazonProduct2Dao.findBySql(sql);		
		}
		for (Object[] obj : list) {
			if(skus!=null&&skus.contains(obj[2].toString())){
				List<Object[]> objList=map.get(obj[0].toString());
				if(objList==null){
					objList=Lists.newArrayList();
					map.put(obj[0].toString(), objList);
				}
				objList.add(obj);
			}
			
		}
		return map;
	}
    
    
    public Map<String,Float> getRateByDate(String dateStr){
    	Map<String, Float> rs = Maps.newHashMap();
    	if (StringUtils.isNotEmpty(dateStr)) {
        	String typeSql = "'%Y%m%d'";
        	String sql = "SELECT name,rate FROM exchange_rate a WHERE DATE_FORMAT(a.date,"+typeSql+")=:p1";
        	List<Object[]> exchangeRateList = amazonProduct2Dao.findBySql(sql, new Parameter(dateStr));
        	for (Object[] objs : exchangeRateList) {
        		rs.put(objs[0].toString(), (Float)objs[1]);
        	}
    	}
    	return rs;
    }
    
    /**
     * 获取平均汇率
     * @param dateStr
     * @param flag 1:月平均汇率  2：年平均汇率
     * @return
     */
    public Map<String,Float> getRateByAvg(String dateStr, String flag){
    	Map<String, Float> rs = Maps.newHashMap();
    	String typeSql = "'%Y%m'";
    	if("2".equals(flag)){
    		typeSql = "'%Y'";
    	}
    	if (StringUtils.isNotEmpty(dateStr)) {
        	String sql = "SELECT t.`name`,AVG(t.`rate`) FROM `exchange_rate` t  WHERE DATE_FORMAT(t.`date`,"+typeSql+")=:p1 GROUP BY t.`name`,DATE_FORMAT(t.`date`,"+typeSql+")";
        	List<Object[]> exchangeRateList = amazonProduct2Dao.findBySql(sql, new Parameter(dateStr));
        	for (Object[] objs : exchangeRateList) {
        		rs.put(objs[0].toString(), Float.parseFloat(objs[1].toString()));
        	}
    	}
    	return rs;
    }
    
    //获取所有的历史汇率按日期分组
    public Map<String,Map<String,Float>> getAllRateByDate(){
    	Map<String,Map<String,Float>> rs = Maps.newHashMap();
    	String typeSql = "'%Y%m%d'";
    	String sql = "SELECT DATE_FORMAT(a.date,"+typeSql+") as dates,name,rate FROM exchange_rate a ";
    	List<Object[]> exchangeRateList = amazonProduct2Dao.findBySql(sql);
    	for (Object[] objs : exchangeRateList) {
    		String dates = objs[0].toString();
    		String name = objs[1].toString();
    		Float rate = (Float)objs[2];
    		Map<String,Float> map = rs.get(dates);
    		if (map == null) {
    			map = Maps.newHashMap();
    			rs.put(dates, map);
			}
    		map.put(name, rate);
    	}
    	return rs;
    }
	
	
	@Transactional(readOnly = false)
	public void saveRate(Map<String,Float> rate){
		String sql = "INSERT INTO exchange_rate (NAME,rate,DATE)VALUES (:p1,:p2,:p3)";
		Date today =DateUtils.addDays(DateUtils.getDateStart(new Date()),1);
		for(Map.Entry<String,Float> entry :rate.entrySet()){
		    String name=entry.getKey();
			amazonProduct2Dao.updateBySql(sql,new Parameter(name,entry.getValue(),today));
		}
	}

	public static Map<String, Float> getRateConfig() {
		return SingletonHolder.rateConfig;
	}

	public static void setRateConfig(Map<String, Float> rateConfig) {
		SingletonHolder.rateConfig.clear();
		SingletonHolder.rateConfig.putAll(rateConfig);
	}
	
	public static Map<String, Map<String, String>> getAmazonAttr() {
		return SingletonHolder.amazonAttrMap;
	}

	public static void setAmazonAttr(Map<String, Map<String, String>> attr) {
		SingletonHolder.amazonAttrMap.clear();
		SingletonHolder.amazonAttrMap.putAll(attr);
	}
	
	private static class SingletonHolder {  
		private static final Map<String, Float> rateConfig = SaveHistoryInfoMonitor.getRate();
		private static final Map<String, Map<String, String>> amazonAttrMap = SaveHistoryInfoMonitor.getAmazonAttrFromDb();  
    }
	
	public Set<String> getPostInfo(){
		Set<String>  set=Sets.newHashSet();
		String sql=" SELECT country,ASIN FROM amazoninfo_product2 WHERE  active='1' "+
         " AND country IS NOT NULL AND ASIN IS NOT NULL AND sale_price IS NOT NULL GROUP BY country,ASIN  ";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj: list) {
			set.add(obj[0].toString()+"_"+obj[1].toString());
		}
		return set;
		
	}
	
	
	
	public Map<String,String> getSkuAndFnskuMap(){
		Map<String,String> fnSkuMap = Maps.newHashMap();
		String sql ="SELECT DISTINCT a.`sku`, (CASE WHEN a.`fnsku` = a.`asin` and a.`fnsku` is not null and a.`fnsku` != '' THEN a.`ean` ELSE a.`fnsku` END)  FROM amazoninfo_product2 AS a WHERE  a.`asin` IS NOT NULL AND a.`is_fba`='1' AND a.`active`='1' ";
		List<Object[]> list =this.amazonProduct2Dao.findBySql(sql);
		for(Object[] object :list){
			if(object[0]!=null&&object[1]!=null){
				fnSkuMap.put(object[0].toString(), object[1].toString());
			}
		}
		return fnSkuMap;
	}
	
	public Map<String,Object[]> countWarnPrice(){
		String sql="SELECT p.country,GROUP_CONCAT(DISTINCT CASE WHEN warn_price IS NULL THEN s.sku ELSE '' END ) lowPrice,GROUP_CONCAT(DISTINCT CASE WHEN high_warn_price IS NULL THEN s.sku ELSE '' END ) hignPrice "+
              " FROM amazoninfo_product2 p JOIN "+
              " (SELECT DISTINCT sku,country,product_name FROM psi_sku WHERE del_flag='0') s ON p.`sku`=s.sku AND p.`country`=s.`country`  "+
              " WHERE p.`is_fba`='1' AND p.`active`='1' AND p.`sale_price` IS NOT NULL AND s.product_name NOT LIKE '%Old%' AND s.product_name NOT LIKE '%other%' GROUP BY p.country HAVING  (lowPrice!='' OR hignPrice!='') ";
		Map<String,Object[]> map=Maps.newHashMap();
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj : list) {
			map.put(obj[0].toString(),obj);
		}		
		return map;      
	}
	
	@Transactional(readOnly = false)
	public void updateAndSaveCostPrice(){
		String sql = "SELECT a.`country`,a.`sku`,CASE WHEN a.country = 'de'|| a.country = 'it' || a.country = 'fr' || a.country = 'es' THEN ROUND(a.`amz_price`/:p1,1)  WHEN a.country = 'uk' THEN ROUND(a.`amz_price`/:p2,1) WHEN a.country = 'ca' THEN ROUND(a.`amz_price`/:p3,1) WHEN a.country = 'jp' THEN ROUND(a.`amz_price`/:p4,0) WHEN a.country = 'mx' THEN ROUND(a.`amz_price`/:p5,1) ELSE ROUND(a.`amz_price`,1) END  FROM amazoninfo_product_price a WHERE a.`date` = DATE_ADD(CURDATE(), INTERVAL -1 DAY) and a.`amz_price`>0 ";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql,new Parameter(MathUtils.getRate("EUR","USD", null),MathUtils.getRate("GBP","USD", null),MathUtils.getRate("CAD","USD", null),MathUtils.getRate("JPY","USD", null),MathUtils.getRate("MXN","USD", null)));
		
		sql = "SELECT CONCAT(b.sku,'_',b.country) FROM psi_product_eliminate  a ,(SELECT a.`product_name`,a.`color`,a.`sku`,a.`country` FROM psi_sku a WHERE a.`del_flag` = '0' AND a.`country` !='ebay' AND a.`product_name` != 'inateck other' AND a.`product_name` != 'inateck old') b WHERE a.`is_sale` = '4' AND a.`del_flag` = '0' AND a.`color` = b.color AND a.product_name = b.product_name AND a.country = b.country";
		List<String> noSale=amazonProduct2Dao.findBySql(sql,null);
		sql = "UPDATE amazoninfo_product2 a SET a.`warn_price`=:p1,a.`high_warn_price` = :p4 WHERE a.`sku`= :p2 AND a.`country`=:p3";
		Set<String> skus = Sets.newHashSet();
		for (Object[] objs : list) {
			float price = Float.parseFloat(objs[2].toString());
			String country = objs[0].toString();
			String key = objs[1].toString()+"_"+country;
			skus.add(key);
			if(price>0){
				float highPrice = price*5f;
				if(noSale.contains(key)){
					price = price*0.5f;
				}
				//尾数改为9
				String temp = Math.round(price)+"";
				String temp1 = Math.round(highPrice)+"";
				if("jp".equals(country)){
					temp = temp.substring(0,temp.length()-1)+"9";
					temp1 = temp1.substring(0,temp1.length()-1)+"9";
				}else{
					temp = temp +".99";
					temp1 = temp1 +".99";
				}
				amazonProduct2Dao.updateBySql(sql, new Parameter(temp,objs[1],country,temp1));
			}
		}
		
		if (skus.size() == 0) {
			logger.info("预估没有算出佣金处理费的产品skus为空");
			return;
		}
		//预估没有算出佣金处理费的产品
		sql = "SELECT a.`id`,a.`is_fba`,a.`sku`,a.`country`,b.names FROM amazoninfo_product2 a , (SELECT a.`country`,a.`sku`, CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS NAMES FROM psi_sku a WHERE a.`del_flag` = '0' AND a.`country` !='ebay' ) b WHERE  a.`active` = '1' AND NOT (a.`sku` LIKE '%old%')  AND a.`sku` = b.sku AND a.`country` = b.country "+
			  "	AND b.names != 'inateck other' AND b.names != 'inateck old' and  CONCAT(b.sku,'_',b.country) not in (:p1) ";
		List<Object[]> noWarn = amazonProduct2Dao.findBySql(sql,new Parameter(skus));
		if(noWarn.size()>0){
			Map<String,Float> priceMap =  saleReportService.getProductPrice("USD",null);	
			for (Object[] objs : noWarn) {
				String country = objs[3].toString();
				String name = objs[4].toString();
				Float price = priceMap.get(name);
				if(price!=null && price>0){
					//处理price
					if("1".equals(objs[1].toString())){
						price = (price*1.15f+2.33f);
					}else{
						price = (price+2.33f);
					}
					if("it,es,fr,uk".contains(country)){
						if(!objs[2].toString().toLowerCase().contains(country)){
							price = price +1f;
						}
					}
					price = price/0.8f;
					//转汇率
					if("de,it,es,fr".contains(country)){
						price = price/MathUtils.getRate("EUR","USD", null);
					}else if ("ca".contains(country)){
						price = price/MathUtils.getRate("CAD","USD", null);
					}else if ("uk".contains(country)){
						price = price/MathUtils.getRate("GBP","USD", null);
					}else if ("jp".contains(country)){
						price = price/MathUtils.getRate("JPY","USD", null);
					}else if("mx".contains(country)){
						price = price/MathUtils.getRate("MXN","USD", null);
					}
					Float highPrice = price*5f;
					if(noSale.contains(objs[2].toString()+"_"+country)){
						price = price*0.5f;
					}
					sql = "UPDATE amazoninfo_product2 a SET a.`warn_price`=:p2,a.`high_warn_price` = :p3 WHERE id =:p1";
				
					//尾数改为9
					String temp = Math.round(price)+"";
					String temp1 = Math.round(highPrice)+"";
					if("jp".equals(country)){
						temp = temp.substring(0,temp.length()-1)+"9";
						temp1 = temp1.substring(0,temp1.length()-1)+"9";
					}else{
						temp = temp +".99";
						temp1 = temp1 +".99";
					}
					amazonProduct2Dao.updateBySql(sql, new Parameter(objs[0],temp,temp1));
				}
			}
		}
	}
	
	//对部分产品进行处理加工
	/*static List<String> product10Pcent = Lists.newArrayList("Tomons DL1001_green","Tomons DL1001_white","Tomons DL1001_black","Inateck FEU3NS-1E_black");
	static List<String> product20Pcent = Lists.newArrayList("Inateck FEU3NS-1_black","Inateck SP1103_dark gray","Inateck TP1001","Inateck MP1305_gray","Inateck MP1300_light gray","Inateck KTU3FR-2O2I","Inateck KTU3FR-4P","Inateck LB1200_dark gray","Inateck LB1200_light gray","Inateck LB1300_dark gray","Inateck LB1300_light gray","Inateck LB1301","Inateck LB1400_dark gray","Inateck LB1400_light gray","Inateck LB1500_dark gray","Inateck LB1500_light gray","Inateck LB1501","Inateck LC1101","Inateck LC1201");
	static List<String> product30Pcent = Lists.newArrayList("Inateck TPB-IA","Inateck TPB-IM","Inateck KT4004","Inateck KT4007","Inateck BCST-10","Inateck BCST-20");
	*/
	
	static List<String> product10Pcent = Lists.newArrayList("");
	static List<String> product20Pcent = Lists.newArrayList("");
	static List<String> product30Pcent = Lists.newArrayList("");
	
	
	@Transactional(readOnly = false)
	public void countCostPrice(Date today){
		String sql = "SELECT  a.`product_name`, ROUND(max(CASE WHEN a.`country` ='de' THEN a.`amz_price` ELSE NULL END)/0.9/:p1,1) AS de_price ,ROUND(max(CASE WHEN a.`country` ='com' THEN a.`amz_price` ELSE NULL END)/0.9,1) AS com_price,ROUND(max(CASE WHEN a.`country` ='uk' THEN a.`amz_price` ELSE NULL END)/0.9/:p2,1) AS uk_price,ROUND(max(CASE WHEN a.`country` ='fr' THEN a.`amz_price` ELSE NULL END)/0.9/:p1,1) AS fr_price,ROUND(max(CASE WHEN a.`country` ='it' THEN a.`amz_price` ELSE NULL END)/0.9/:p1,1) AS it_price,ROUND(max(CASE WHEN a.`country` ='es' THEN a.`amz_price` ELSE NULL END)/0.9/:p1,1) AS es_price,ROUND(max(CASE WHEN a.`country` ='jp' THEN a.`amz_price` ELSE NULL END)/0.9/:p4,0) AS jp_price,ROUND(max(CASE WHEN a.`country` ='ca' THEN a.`amz_price` ELSE NULL END)/0.9/:p3,1) AS ca_price,ROUND(max(CASE WHEN a.`country` ='mx' THEN a.`amz_price` ELSE NULL END)/0.9/:p6,1) AS mx_price, ROUND(max(CASE WHEN a.`country` ='com' THEN (a.`local_price`/0.9/0.88+1*a.tran_gw) ELSE NULL END),1) AS comvendor_price,ROUND(max(CASE WHEN a.`country` ='de' THEN a.`amz_price_by_sea` ELSE NULL END)/0.9/:p1,1) AS de_price_sea ,ROUND(max(CASE WHEN a.`country` ='com' THEN a.`amz_price_by_sea` ELSE NULL END)/0.9,1) AS com_price_sea,ROUND(max(CASE WHEN a.`country` ='uk' THEN a.`amz_price_by_sea` ELSE NULL END)/0.9/:p2,1) AS uk_price_sea,ROUND(max(CASE WHEN a.`country` ='fr' THEN a.`amz_price_by_sea` ELSE NULL END)/0.9/:p1,1) AS fr_price_sea,ROUND(max(CASE WHEN a.`country` ='it' THEN a.`amz_price_by_sea` ELSE NULL END)/0.9/:p1,1) AS it_price_sea,ROUND(max(CASE WHEN a.`country` ='es' THEN a.`amz_price_by_sea` ELSE NULL END)/0.9/:p1,1) AS es_price_sea,ROUND(max(CASE WHEN a.`country` ='jp' THEN a.`amz_price_by_sea` ELSE NULL END)/0.9/:p4,0) AS jp_price_sea,ROUND(max(CASE WHEN a.`country` ='ca' THEN a.`amz_price_by_sea` ELSE NULL END)/0.9/:p3,1) AS ca_price_sea,ROUND(max(CASE WHEN a.`country` ='mx' THEN a.`amz_price_by_sea` ELSE NULL END)/0.9/:p6,1) AS mx_price_sea, ROUND(max(CASE WHEN a.`country` ='com' THEN (a.`local_price`/0.9/0.88+1*a.tran_gw) ELSE NULL END),1) AS comvendor_price_sea FROM amazoninfo_product_price a ,(SELECT DISTINCT b.`sku`,aa.country FROM amazoninfo_financial aa ,amazoninfo_financial_item b WHERE aa.`id` = b.`order_id` AND aa.`type` = 'order' AND aa.`add_time`>DATE_ADD(CURDATE(),INTERVAL -3 MONTH) AND aa.`amazon_order_id` NOT LIKE 'S%') bb   WHERE a.`country` = bb.`country` AND a.`sku` = bb.`sku`  AND a.`date` = :p5 AND a.`type` != '2' AND a.`amz_price` >0 GROUP BY a.`product_name` ORDER BY a.`product_name` ";
		List<Object[]> list = amazonProduct2Dao.findBySql(sql,new Parameter(MathUtils.getRate("EUR","USD", null),MathUtils.getRate("GBP","USD", null),MathUtils.getRate("CAD","USD", null),MathUtils.getRate("JPY","USD", null),today,MathUtils.getRate("MXN","USD", null)));
		sql = "SELECT a.`product_name` FROM amazoninfo_product_sale_price a";
		List<Object> list1 = amazonProduct2Dao.findBySql(sql,null);
		for (Object[] objs : list) {
			String name = objs[0].toString();
			if(list1.contains(name)){
				//update
				String temp = "";
				Float price = 0f;
				if(objs[1]!=null){
					price = Float.parseFloat(objs[1].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",de_price= '"+price+"'";
				}
				if(objs[2]!=null){
					price = Float.parseFloat(objs[2].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",com_price= '"+price+"'";
				}
				if(objs[3]!=null){
					price = Float.parseFloat(objs[3].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",uk_price= '"+price+"'";
				}
				if(objs[4]!=null){
					price = Float.parseFloat(objs[4].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",fr_price= '"+price+"'";
				}
				if(objs[5]!=null){
					price = Float.parseFloat(objs[5].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",it_price= '"+price+"'";
				}
				if(objs[6]!=null){
					price = Float.parseFloat(objs[6].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",es_price= '"+price+"'";
				}
				if(objs[7]!=null){
					price = Float.parseFloat(objs[7].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",jp_price= '"+price+"'";
				}
				if(objs[8]!=null){
					price = Float.parseFloat(objs[8].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",ca_price= '"+price+"'";
				}
				if(objs[9]!=null){
					price = Float.parseFloat(objs[9].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",mx_price= '"+price+"'";
				}
				
				if(objs[10]!=null){
					price = Float.parseFloat(objs[10].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",usvendor_price= '"+price+"'";
				}
				
				
				if(objs[11]!=null){
					price = Float.parseFloat(objs[11].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",de_price_sea= '"+price+"'";
				}
				if(objs[12]!=null){
					price = Float.parseFloat(objs[12].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",com_price_sea= '"+price+"'";
				}
				if(objs[13]!=null){
					price = Float.parseFloat(objs[13].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",uk_price_sea= '"+price+"'";
				}
				if(objs[14]!=null){
					price = Float.parseFloat(objs[14].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",fr_price_sea= '"+price+"'";
				}
				if(objs[15]!=null){
					price = Float.parseFloat(objs[15].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",it_price_sea= '"+price+"'";
				}
				if(objs[16]!=null){
					price = Float.parseFloat(objs[16].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",es_price_sea= '"+price+"'";
				}
				if(objs[17]!=null){
					price = Float.parseFloat(objs[17].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",jp_price_sea= '"+price+"'";
				}
				if(objs[18]!=null){
					price = Float.parseFloat(objs[18].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",ca_price_sea= '"+price+"'";
				}
				if(objs[19]!=null){
					price = Float.parseFloat(objs[19].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",mx_price_sea= '"+price+"'";
				}
				
				if(objs[20]!=null){
					price = Float.parseFloat(objs[20].toString());
					if(product10Pcent.contains(name)){
						price = 1.1f*price;
					}else if(product20Pcent.contains(name)){
						price = 1.2f*price;
					}else if(product30Pcent.contains(name)){
						price = 1.3f*price;
					}  
					temp +=",usvendor_price_sea= '"+price+"'";
				}
				
				
				sql = "update amazoninfo_product_sale_price a set a.update_time = now() "+temp+" where a.product_name = :p1";
				amazonProduct2Dao.updateBySql(sql, new Parameter(name));
			}else{
				//add
				sql = "INSERT INTO `amazoninfo_product_sale_price` (product_name,de_price,com_price,uk_price,fr_price,it_price,es_price,jp_price,ca_price,update_time,mx_price,usvendor_price,de_price_sea,com_price_sea,uk_price_sea,fr_price_sea,it_price_sea,es_price_sea,jp_price_sea,ca_price_sea,mx_price_sea,usvendor_price_sea) values(:p1,:p2,:p3,:p4,:p5,:p6,:p7,:p8,:p9,now(),:p10,:p11,:p12,:p13,:p14,:p15,:p16,:p17,:p18,:p19,:p20,:p21)";
				Object price1 = objs[1];
				Object price2 = objs[2];
				Object price3 = objs[3];
				Object price4 = objs[4];
				Object price5 = objs[5];
				Object price6 = objs[6];
				Object price7 = objs[7];
				Object price8 = objs[8];
				Object price9 = objs[9];
				Object price10 = objs[10];
				
				Object price11 = objs[11];
				Object price12 = objs[12];
				Object price13 = objs[13];
				Object price14 = objs[14];
				Object price15 = objs[15];
				Object price16 = objs[16];
				Object price17 = objs[17];
				Object price18 = objs[18];
				Object price19 = objs[19];
				Object price20 = objs[20];
				
				
				float pcent = 1f;
				if(product10Pcent.contains(name)){
					pcent = 1.1f;
				}else if(product20Pcent.contains(name)){
					pcent = 1.2f;
				}else if(product30Pcent.contains(name)){
					pcent = 1.3f;
				}
				
				if(price1!=null){
					price1 = Float.parseFloat(price1.toString())*pcent;
				}
				if(price2!=null){
					price2 = Float.parseFloat(price2.toString())*pcent;
				}
				if(price3!=null){
					price3 = Float.parseFloat(price3.toString())*pcent;
				}
				if(price4!=null){
					price4 = Float.parseFloat(price4.toString())*pcent;
				}
				if(price5!=null){
					price5 = Float.parseFloat(price5.toString())*pcent;
				}
				if(price6!=null){
					price6 = Float.parseFloat(price6.toString())*pcent;
				}
				if(price7!=null){
					price7 = Float.parseFloat(price7.toString())*pcent;
				}
				if(price8!=null){
					price8 = Float.parseFloat(price8.toString())*pcent;
				}
				if(price9!=null){
					price9 = Float.parseFloat(price9.toString())*pcent;
				}
				if(price10!=null){
					price10 = Float.parseFloat(price10.toString())*pcent;
				}
				

				if(price11!=null){
					price11 = Float.parseFloat(price11.toString())*pcent;
				}
				if(price12!=null){
					price12 = Float.parseFloat(price12.toString())*pcent;
				}
				if(price13!=null){
					price13 = Float.parseFloat(price13.toString())*pcent;
				}
				if(price14!=null){
					price14 = Float.parseFloat(price14.toString())*pcent;
				}
				if(price15!=null){
					price15 = Float.parseFloat(price15.toString())*pcent;
				}
				if(price16!=null){
					price16 = Float.parseFloat(price16.toString())*pcent;
				}
				if(price17!=null){
					price17 = Float.parseFloat(price17.toString())*pcent;
				}
				if(price18!=null){
					price18 = Float.parseFloat(price18.toString())*pcent;
				}
				if(price19!=null){
					price19 = Float.parseFloat(price19.toString())*pcent;
				}
				if(price20!=null){
					price20 = Float.parseFloat(price20.toString())*pcent;
				}
				amazonProduct2Dao.updateBySql(sql, new Parameter(name,price1,price2,price3,price4,price5,price6,price7,price8,price9,price10,price11,price12,price13,price14,price15,price16,price17,price18,price19,price20));
			}
		}
	}
	
	
	/**
	 *查询亚马逊2表的asin
	 * 
	 */
	public Set<String> getAllAsin(){
		Set<String> set = Sets.newHashSet();
		String sql="SELECT DISTINCT a.`asin` FROM amazoninfo_product2 AS a  WHERE a.`asin` IS NOT NULL";
		List<String> list=amazonProduct2Dao.findBySql(sql);
		if(list!=null&&list.size()>0){
			set.addAll(list);
		}
		return set;
	}
	
	public Map<String,Map<String,String>> getPostsStatu(String accountName){
		String sql="SELECT account_name,sku,(CASE WHEN is_fba='1' THEN 'FBA帖' ELSE '本地帖' END) FROM amazoninfo_product2 where account_name=:p1 ";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql,new Parameter(accountName));
		Map<String,Map<String,String>> map=Maps.newHashMap();
		for (Object[] obj: list) {
			Map<String,String> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(),temp);
			}
			temp.put(obj[1].toString(), obj[2].toString());
		}
		return map;
	}
	
	public List<AmazonProduct2> getAmazonProduct(Set<String> country,String suffix){
		List<AmazonProduct2> productList=Lists.newArrayList();
		String sql="SELECT distinct sku,ASIN,ean FROM amazoninfo_product2 WHERE LOWER(sku) NOT LIKE '%old%' and LOWER(sku) NOT LIKE '%local%'  and active='1' and country in :p1 and account_name like :p2";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql,new Parameter(country,suffix+"%"));
		for (Object[] obj: list) {
			AmazonProduct2 product=new AmazonProduct2();
			product.setSku(obj[0].toString());
			product.setAsin(obj[1]==null?"":obj[1].toString());
			product.setEan(obj[2]==null?"":obj[2].toString());
			productList.add(product);
		}
		return productList;
	}
	
	public Map<String,Float> getWholeSalePrice(String type){
		String sql=" SELECT bb.name,aa.price FROM (SELECT a.`sku`,GROUP_CONCAT(DISTINCT a.`sale_price` ORDER BY a.`sale_price` ) AS price  FROM amazoninfo_product_history_price a WHERE a.`country` = 'de' AND a.`data_date` >DATE_ADD(CURDATE(),INTERVAL -30 DAY) AND a.`sale_price` >0 GROUP BY a.`sku`) aa , (SELECT CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END ,b.`color`) AS NAME,b.`sku` FROM psi_sku b WHERE b.`del_flag` = '0' AND b.`country` = 'de' AND b.`product_name` != 'Inateck other' AND b.`product_name` != 'Inateck Old' AND b.`use_barcode` = '1') bb "+
				   " WHERE aa.sku = bb.sku  GROUP BY  bb.name ";
		Map<String,Float> map=Maps.newHashMap();
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj: list) {
			if(obj[1].toString().contains(",")){
				String[] arr=obj[1].toString().split(",");
				if(arr.length==2){
					Float val=(Float.parseFloat(arr[0])+Float.parseFloat(arr[1]))/2;
					if(StringUtils.isNotBlank(type)){//$
						val=val*AmazonProduct2Service.getRateConfig().get("EUR/USD");
					}
					map.put(obj[0].toString(),val);
				}else if(arr.length==3){
					Float val=Float.parseFloat(arr[2]);
					if(StringUtils.isNotBlank(type)){//$
						val=val*AmazonProduct2Service.getRateConfig().get("EUR/USD");
					}
					map.put(obj[0].toString(),val);
				}else{
					Float total=0f;
					for (int i=0;i<arr.length;i++) {
						if(i!=0&&i!=arr.length-1){
							total+=Float.parseFloat(arr[i]);
						}
					}
					Float val=total/(arr.length-2);
					if(StringUtils.isNotBlank(type)){//$
						val=val*AmazonProduct2Service.getRateConfig().get("EUR/USD");
					}
					map.put(obj[0].toString(),val);
				}
			}else{
				Float val=Float.parseFloat(obj[1].toString());
				if(StringUtils.isNotBlank(type)){//$
					val=val*AmazonProduct2Service.getRateConfig().get("EUR/USD");
				}
				map.put(obj[0].toString(),val);
			}
		}
		return map;
	}
	
	//获取美国产品的价格
	public Map<String,Float> getSalePriceDollers(){
		String sql=" SELECT bb.name,aa.price FROM (SELECT a.`sku`,GROUP_CONCAT(DISTINCT a.`sale_price` ORDER BY a.`sale_price` ) AS price  FROM amazoninfo_product_history_price a WHERE a.`country` = 'com' AND a.`data_date` >DATE_ADD(CURDATE(),INTERVAL -30 DAY) AND a.`sale_price` >0 GROUP BY a.`sku`) aa , (SELECT CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END ,b.`color`) AS NAME,b.`sku` FROM psi_sku b WHERE b.`del_flag` = '0' AND b.`country` = 'com' AND b.`product_name` != 'Inateck other' AND b.`product_name` != 'Inateck Old' AND b.`use_barcode` = '1') bb "+
				   " WHERE aa.sku = bb.sku  GROUP BY  bb.name ";
		Map<String,Float> map=Maps.newHashMap();
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj: list) {
			if(obj[1].toString().contains(",")){
				String[] arr=obj[1].toString().split(",");
				if(arr.length==2){
					Float val=(Float.parseFloat(arr[0])+Float.parseFloat(arr[1]))/2;
					map.put(obj[0].toString(),val);
				}else if(arr.length==3){
					Float val=Float.parseFloat(arr[1]);
					map.put(obj[0].toString(),val);
				}else{
					Float total=0f;
					for (int i=0;i<arr.length;i++) {
						if(i!=0&&i!=arr.length-1){
							total+=Float.parseFloat(arr[i]);
						}
					}
					Float val=total/(arr.length-2);
					map.put(obj[0].toString(),val);
				}
			}else{
				Float val=Float.parseFloat(obj[1].toString());
				map.put(obj[0].toString(),val);
			}
		}
		return map;
	}
	
	public Map<String,Float> getSafePrice(String type){
		Map<String,Float> map=Maps.newHashMap();
		//String sqlString="select max(update_time) FROM amazoninfo_product_sale_price";
		//List<Object> rs=amazonProduct2Dao.findBySql(sqlString);
		//if(rs.size()>0){
			//Date date=(Timestamp)rs.get(0);
			String sql="SELECT product_name,de_price FROM amazoninfo_product_sale_price WHERE de_price is not null";
			List<Object[]> rs1=amazonProduct2Dao.findBySql(sql);
			for (Object[] obj: rs1) {
				float val=((BigDecimal)obj[1]).floatValue();
				if(StringUtils.isNotBlank(type)){
					val=val*AmazonProduct2Service.getRateConfig().get("EUR/USD");
				}
				map.put(obj[0].toString(),val);
			}
		//}	
		return map;
	}
	
	public Object[] getAllSafePrice(String productName){
			String sql="SELECT de_price,com_price,uk_price,fr_price,it_price,es_price,jp_price,ca_price,mx_price,usvendor_price FROM amazoninfo_product_sale_price WHERE product_name=:p1 ";
			List<Object[]> rs1=amazonProduct2Dao.findBySql(sql,new Parameter(productName));
			if(rs1!=null&&rs1.size()>0){
				return rs1.get(0);
			}
		    return null;
	}
	
	public Object[] getAllSafePriceBySea(String productName){
		String sql="SELECT de_price_sea,com_price_sea,uk_price_sea,fr_price_sea,it_price_sea,es_price_sea,jp_price_sea,ca_price_sea,mx_price_sea,usvendor_price_sea FROM amazoninfo_product_sale_price WHERE product_name=:p1 ";
		List<Object[]> rs1=amazonProduct2Dao.findBySql(sql,new Parameter(productName));
		if(rs1!=null&&rs1.size()>0){
			return rs1.get(0);
		}
	    return null;
}
	
	
	
	public Map<String,String> getMinPrice(){
		String sql="SELECT country,ASIN,sku,sale_price FROM amazoninfo_product2 WHERE ASIN IS NOT NULL AND sku IS  NOT NULL AND active='1' "+
           " AND sale_price IS NOT NULL AND (is_fba='1' OR (is_fba='0' AND quantity>0)) "+
           " AND LOWER(sku) NOT LIKE '%old/_%' ESCAPE '/' AND LOWER(sku) NOT LIKE '%/_old%' ESCAPE '/'  "+
           " AND LOWER(sku) NOT LIKE '%old-%'  AND LOWER(sku) NOT LIKE '%-old%'  ";
		List<Object[]> rs=amazonProduct2Dao.findBySql(sql);
		Map<String,String> map=Maps.newHashMap();
		for (Object[] obj: rs) {
			String key=obj[0].toString()+"_"+obj[1].toString();
			String sku=obj[2].toString();
			Float price=((BigDecimal)obj[3]).floatValue();
			String temp=map.get(key);
			if(StringUtils.isBlank(temp)){
				map.put(key, sku+","+price);
			}else{
				String[] arr=temp.split(",");
				if(price<Float.parseFloat(arr[1])){
					map.put(key, sku+","+price);
				}
			}
		}
		return map;
	}
	
	public Map<String,String> getMinPrice2(){
		String sql="SELECT country,ASIN,sku,sale_price FROM amazoninfo_product2 WHERE ASIN IS NOT NULL AND sku IS  NOT NULL AND active='1' "+
           " AND sale_price IS NOT NULL AND is_fba='1' "+
           " AND LOWER(sku) NOT LIKE '%old/_%' ESCAPE '/' AND LOWER(sku) NOT LIKE '%/_old%' ESCAPE '/'  "+
           " AND LOWER(sku) NOT LIKE '%old-%'  AND LOWER(sku) NOT LIKE '%-old%'  ";
		List<Object[]> rs=amazonProduct2Dao.findBySql(sql);
		Map<String,String> map=Maps.newHashMap();
		for (Object[] obj: rs) {
			String key=obj[0].toString()+"_"+obj[1].toString();
			String sku=obj[2].toString();
			Float price=((BigDecimal)obj[3]).floatValue();
			String temp=map.get(key);
			if(StringUtils.isBlank(temp)){
				map.put(key, sku+","+price);
			}else{
				String[] arr=temp.split(",");
				if(price<Float.parseFloat(arr[1])){
					map.put(key, sku+","+price);
				}
			}
		}
		return map;
	}
	
	
	public Map<String,String> getMaxPrice(){
		String sql="SELECT country,ASIN,sku,sale_price FROM amazoninfo_product2 WHERE ASIN IS NOT NULL AND sku IS  NOT NULL AND active='1' "+
           " AND sale_price IS NOT NULL AND (is_fba='1' OR (is_fba='0' AND quantity>0)) "+
           " AND LOWER(sku) NOT LIKE '%old/_%' ESCAPE '/' AND LOWER(sku) NOT LIKE '%/_old%' ESCAPE '/'  "+
           " AND LOWER(sku) NOT LIKE '%old-%'  AND LOWER(sku) NOT LIKE '%-old%'  ";
		List<Object[]> rs=amazonProduct2Dao.findBySql(sql);
		Map<String,String> map=Maps.newHashMap();
		for (Object[] obj: rs) {
			String key=obj[0].toString()+"_"+obj[1].toString();
			String sku=obj[2].toString();
			Float price=((BigDecimal)obj[3]).floatValue();
			String temp=map.get(key);
			if(StringUtils.isBlank(temp)){
				map.put(key, sku+","+price);
			}else{
				String[] arr=temp.split(",");
				if(price>=Float.parseFloat(arr[1])){
					map.put(key, sku+","+price);
				}
			}
		}
		return map;
	}
	
	public Map<String,String> getMaxPrice2(){
		String sql="SELECT country,ASIN,sku,sale_price FROM amazoninfo_product2 WHERE ASIN IS NOT NULL AND sku IS  NOT NULL AND active='1' "+
           " AND sale_price IS NOT NULL AND is_fba='1' "+
           " AND LOWER(sku) NOT LIKE '%old/_%' ESCAPE '/' AND LOWER(sku) NOT LIKE '%/_old%' ESCAPE '/'  "+
           " AND LOWER(sku) NOT LIKE '%old-%'  AND LOWER(sku) NOT LIKE '%-old%'  ";
		List<Object[]> rs=amazonProduct2Dao.findBySql(sql);
		Map<String,String> map=Maps.newHashMap();
		for (Object[] obj: rs) {
			String key=obj[0].toString()+"_"+obj[1].toString();
			String sku=obj[2].toString();
			Float price=((BigDecimal)obj[3]).floatValue();
			String temp=map.get(key);
			if(StringUtils.isBlank(temp)){
				map.put(key, sku+","+price);
			}else{
				String[] arr=temp.split(",");
				if(price>=Float.parseFloat(arr[1])){
					map.put(key, sku+","+price);
				}
			}
		}
		return map;
	}
	
	public Map<String,List<AmazonProduct2>> findDeletePostsByUS(){
		AmazonPromotionsWarning warn=new AmazonPromotionsWarning();
		Date date=warn.xmlGregorianToLocalDate("com");
		String sql="SELECT distinct t.asin FROM amazoninfo_promotions_warning w "+
				" JOIN amazoninfo_promotions_warning_item t ON w.id=t.`warning_id` AND t.`del_flag`='0' "+
				" JOIN psi_product_eliminate s ON t.`product_name_color`=CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN CONCAT ('_',s.`color`) ELSE '' END) "+
				" AND s.del_flag='0'  AND s.`country`='com'  "+
				" WHERE t.asin is not null and w.`is_active`='0' and w.`promotion_id` LIKE 'F-%' and w.pro_type not in ('2','0') and w.country='com' AND w.`warning_sta`!='2' AND w.`start_date`<=:p1 AND w.`end_date`>=:p2 ";
		List<String> asinList=amazonProduct2Dao.findBySql(sql,new Parameter(date,date));
		Map<String,List<AmazonProduct2>> productMap=Maps.newHashMap();
		if(asinList!=null&&asinList.size()>0){
			String sql2="SELECT asin,sku,price,ifnull(sale_price,price) FROM amazoninfo_product2 WHERE country='com' AND ASIN IN :p1 AND is_fba='0' AND quantity>0 AND active='1' "+
					" AND LOWER(sku) NOT LIKE '%old/_%' ESCAPE '/' AND LOWER(sku) NOT LIKE '%/_old%' ESCAPE '/'  "+
			        " AND LOWER(sku) NOT LIKE '%old-%'  AND LOWER(sku) NOT LIKE '%-old%'  ";
			List<Object[]> list=amazonProduct2Dao.findBySql(sql2,new Parameter(asinList));
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					String asin=obj[0].toString();
					String sku=obj[1].toString();
					Float price=((BigDecimal)obj[2]).floatValue();
					Float salePrice=((BigDecimal)obj[3]).floatValue();
					List<AmazonProduct2> temp=productMap.get(asin);
					if(temp==null){
						temp=Lists.newArrayList();
						productMap.put(asin,temp);
					}
					AmazonProduct2 product=new AmazonProduct2();
					product.setAsin(asin);
					product.setSku(sku);
					product.setPrice(price);
					product.setSalePrice(salePrice);
					temp.add(product);
				}
			}
		}
		return productMap;
	}
	
	
	//亏本淘汰和特批的数量改为0，有利润促销和亏本非淘汰的 不受规则限制。当FBA库存为0时 即使是亏本淘汰和特批也自动上本地帖
	public Map<String,List<AmazonProduct2>> findDeletePostsByDe(){
		AmazonPromotionsWarning warn=new AmazonPromotionsWarning();
		Date date=warn.xmlGregorianToLocalDate("de");
		String sql="SELECT distinct t.asin FROM amazoninfo_promotions_warning w "+
				" JOIN amazoninfo_promotions_warning_item t ON w.id=t.`warning_id` AND t.`del_flag`='0' "+
				" JOIN psi_product_eliminate s ON t.`product_name_color`=CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN CONCAT ('_',s.`color`) ELSE '' END) "+
				" AND s.del_flag='0'  AND s.`country`='de' "+
				" WHERE t.asin is not null and w.`is_active`='0' and w.`promotion_id` LIKE 'F-%' and w.pro_type not in ('2','0') and w.country='de' AND w.`warning_sta`!='2' AND w.`start_date`<=:p1 AND w.`end_date`>=:p2 ";
		List<String> asinList=amazonProduct2Dao.findBySql(sql,new Parameter(date,date));
		Map<String,List<AmazonProduct2>> productMap=Maps.newHashMap();
		if(asinList!=null&&asinList.size()>0){
			String sql2="SELECT asin,sku,price,ifnull(sale_price,price) FROM amazoninfo_product2 WHERE country='de' AND ASIN IN :p1 AND is_fba='0' AND quantity>0 AND active='1'  "+
					" AND LOWER(sku) NOT LIKE '%old/_%' ESCAPE '/' AND LOWER(sku) NOT LIKE '%/_old%' ESCAPE '/'  "+
			        " AND LOWER(sku) NOT LIKE '%old-%'  AND LOWER(sku) NOT LIKE '%-old%'  AND LOWER(sku) NOT LIKE '%deold'  ";
			List<Object[]> list=amazonProduct2Dao.findBySql(sql2,new Parameter(asinList));
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					String asin=obj[0].toString();
					String sku=obj[1].toString();
					Float price=((BigDecimal)obj[2]).floatValue();
					Float salePrice=((BigDecimal)obj[3]).floatValue();
					List<AmazonProduct2> temp=productMap.get(asin);
					if(temp==null){
						temp=Lists.newArrayList();
						productMap.put(asin,temp);
					}
					AmazonProduct2 product=new AmazonProduct2();
					product.setAsin(asin);
					product.setSku(sku);
					product.setPrice(price);
					product.setSalePrice(salePrice);
					temp.add(product);
				}
			}
		}
		return productMap;
	}
	
	private static final DateFormat MONTH_FORMAT = new SimpleDateFormat("yyyyMM");
	
	/**
	 * 获取日平均汇率
	 * @return
	 */
	public Float findAvgMonthRate(String keyCode,Date month){
		String sql = "SELECT  AVG(a.`rate`) FROM exchange_rate a WHERE a.`name` = :p1 AND DATE_FORMAT(a.`date`,'%Y%m') = :p2 GROUP BY DATE_FORMAT(a.`date`,'%Y%m')";
		List<Object> list = amazonProduct2Dao.findBySql(sql,new Parameter(keyCode,MONTH_FORMAT.format(month)));
		if(list.size()>0){
			return Float.parseFloat(list.get(0).toString());
		}else {
			return AmazonProduct2Service.getRateConfig().get(keyCode);
		}
	}
	
	/**
	 * 获取所有产品当前销售价格
	 * @return[国家[sku	AmazonProduct2]]
	 */
	public Map<String, Map<String, AmazonProduct2>> findAllSalePrice(){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		dc.add(Restrictions.isNotNull("salePrice"));
		dc.add(Restrictions.eq("active", "1"));
		List<AmazonProduct2> list =  amazonProduct2Dao.find(dc);
		Map<String, Map<String, AmazonProduct2>> rsMap = Maps.newHashMap();
		for (AmazonProduct2 amazonProduct2 : list) {
			String country = amazonProduct2.getCountry();
			String sku = amazonProduct2.getSku();
			Map<String, AmazonProduct2> skuMap = rsMap.get(country);
			if (skuMap == null) {
				skuMap = Maps.newHashMap();
				rsMap.put(country, skuMap);
			}
			skuMap.put(sku, amazonProduct2);
		}
		return rsMap;
	}

	//初始化亚马逊账户信息[国家[属性 	值]]
	public Map<String, Map<String, String>> initAmazonAttr() {
		Map<String, Map<String, String>> amazonAttrMap = Maps.newHashMap();
		String sql = "SELECT t.`country`,t.`email`,t.`password`,t.`access_key`,t.`secret_key`,t.`seller_id` FROM `amazoninfo_attr` t";
		List<Object[]> list = amazonProduct2Dao.findBySql(sql);
		for (Object[] obj : list) {
			String country = obj[0].toString();
			String email = obj[1].toString();
			String password = obj[2].toString();
			String accessKey = obj[3].toString();
			String secretKey = obj[4].toString();
			String sellerId = obj[5].toString();
			Map<String, String> countryMap = amazonAttrMap.get(country);
			if (countryMap == null) {
				countryMap = Maps.newHashMap();
				amazonAttrMap.put(country, countryMap);
			}
			countryMap.put("email", email);
			countryMap.put("password", password);
			countryMap.put("accessKey", accessKey);
			countryMap.put("secretKey", secretKey);
			countryMap.put("sellerId", sellerId);
		}
		return amazonAttrMap;
	}
	
	/**
	 * 根据sku和国家获取最近的售价记录
	 * @return
	 */
	public Float findHisPrice(String sku, String country){
		String sql = "SELECT t.`sale_price` FROM `amazoninfo_product_history_price` t WHERE t.`sku`=:p1 AND t.`country`=:p2 ORDER BY t.`data_date` DESC LIMIT 1";
		List<Object> list = amazonProduct2Dao.findBySql(sql,new Parameter(sku, country));
		if(list.size()>0){
			return Float.parseFloat(list.get(0).toString());
		}else {
			return null;
		}
	}
	
	
	public List<String> findDeOldSku(){
		String sql="SELECT a.`sku` FROM amazoninfo_product2 a WHERE a.`is_fba` = '0' AND a.`sku` LIKE '%old%' AND a.`active` = '1' AND a.`country` = 'de' AND a.`sku` NOT LIKE '%orico%' ";
		return amazonProduct2Dao.findBySql(sql);
	}
	
	public List<Object[]> findDeLocalPosts(){
		String sql="SELECT DISTINCT CONCAT(s.product_name,CASE  WHEN s.color='' THEN '' ELSE CONCAT('_',s.color) END) NAME,p.sku FROM amazoninfo_product2  p "+
		" JOIN (select distinct asin,country,product_name,color from psi_sku where `del_flag`='0' AND product_name!='Inateck Old') s ON p.`country`=s.`country` AND p.`asin`=s.asin    "+
		" WHERE  p.`country`='de' AND  p.`active` = '1' AND is_fba='0' AND p.`quantity`>0  ";
		return amazonProduct2Dao.findBySql(sql);
	}
	
	//SELECT aa.sku,aa.names,(bb.price-3.99) price  FROM (SELECT a.`sku`,CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END,b.`color`) AS NAMES FROM amazoninfo_product2 a,psi_sku b  WHERE b.`del_flag` = '0' AND a.`sku` = b.`sku` AND  a.`is_fba` = '0' AND a.`sku` NOT LIKE '%old%' AND a.`active` = '1' AND a.`country` = 'de' AND b.`product_name` !='Inateck other')aa,(SELECT CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END,b.`color`) AS NAMES ,MIN(a.`sale_price`) AS price FROM amazoninfo_product2 a,psi_sku b  WHERE b.`del_flag` = '0' AND a.`sku` = b.`sku` AND  a.`is_fba` = '1' AND a.`sku` NOT LIKE '%old%' AND a.`active` = '1' AND a.`country` = 'de' AND b.`product_name` !='Inateck other' AND a.`sale_price`>0 GROUP BY b.`product_name`,b.`color`)bb WHERE aa.names = bb.names 


	public Map<String,Object[]> findProductLocalPosts(){
		Map<String,Object[]> map=Maps.newHashMap();
		String sql="SELECT aa.sku,aa.names,(bb.price-3.99) price,aa.asin  FROM (SELECT a.`sku`,CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END,b.`color`) AS NAMES,a.`asin` FROM amazoninfo_product2 a,psi_sku b  WHERE b.`del_flag` = '0' AND a.`sku` = b.`sku` AND  a.`is_fba` = '0' AND a.`sku` NOT LIKE '%old%' AND a.`active` = '1' AND a.`country` = 'de' AND b.`product_name` !='Inateck other')aa,(SELECT CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END,b.`color`) AS NAMES ,MIN(a.`sale_price`) AS price FROM amazoninfo_product2 a,psi_sku b  WHERE b.`del_flag` = '0' AND a.`sku` = b.`sku` AND  a.`is_fba` = '1' AND a.`sku` NOT LIKE '%old%' AND a.`active` = '1' AND a.`country` = 'de' AND b.`product_name` !='Inateck other' AND a.`sale_price`>0 GROUP BY b.`product_name`,b.`color`)bb WHERE aa.names = bb.names ";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj: list) {
			String name=obj[1].toString();
			map.put(name, obj);
		}
		return map;
	}
	
	/**
	 * 
	 * @param country
	 * @param asin
	 * @param isFba	1:fba贴  0：本地帖
	 * @return
	 */
	public List<AmazonProduct2> findByAsinAndCountry(String country, Set<String> asin, String isFba){
		DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(country)) {
			dc.add(Restrictions.eq("country", country));
		}
		if (asin != null && asin.size() > 0) {
			dc.add(Restrictions.in("asin", asin));
		}
		if (StringUtils.isNotEmpty(isFba)) {
			dc.add(Restrictions.eq("isFba", isFba));
		}
		dc.add(Restrictions.eq("active", "1"));
		return amazonProduct2Dao.find(dc);
	}
	
	//true 存在
	public boolean isShowPosts(String country,String asin){
		String sql="SELECT count(1) FROM amazoninfo_product2 a WHERE a.`active`='1' AND a.`country`=:p1 AND a.`asin`=:p2 AND a.`sale_price` IS NOT NULL";
		List<Object> count=amazonProduct2Dao.findBySql(sql,new Parameter(country,asin));
		return ((BigInteger)count.get(0)).intValue()>0;
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AmazonProduct2Service  service= applicationContext.getBean(AmazonProduct2Service.class);
		for (AmazonProduct2 product2 : service.findByAsinAndCountry("de", Sets.newHashSet("B00HJ1DULE"), "1")) {
			System.out.println(product2.getSku());
		}
		applicationContext.close();
	}
	
	public Map<String,Float> findAllProductPrice(){
		Map<String,Float> map=Maps.newHashMap();
		String sql="SELECT country,ASIN,MIN(p.`sale_price`) FROM amazoninfo_product2 p WHERE p.`is_fba`='1' AND p.`active`='1' AND p.`sale_price` IS NOT NULL "+
                   " GROUP BY country,ASIN ";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj: list) {
			String country=obj[0].toString();
			String asin=obj[1].toString();
			Float price=Float.parseFloat(obj[2].toString());
			map.put(country+"_"+asin, price);
		}
		return map;
	}
	
	public Map<String,String> findAllProductPrice1(){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT country,ASIN,group_concat(distinct p.`sale_price`) FROM amazoninfo_product2 p WHERE p.`is_fba`='1' AND p.`active`='1' AND p.`sale_price` IS NOT NULL "+
                   " GROUP BY country,ASIN ";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj: list) {
			String country=obj[0].toString();
			String asin=obj[1].toString();
			String price=obj[2].toString();
			map.put(country+"_"+asin, price);
		}
		return map;
	}
	
	/**
	 * 分平台获取asin在售价格
	 * @param countryList 国家集合
	 * @return
	 */
	public Map<String,Map<String,Float>> findCountryAsinPrice(List<String> countryList){
		Map<String,Map<String,Float>> rs = Maps.newHashMap();
		String sql="SELECT t.`country`,t.`asin`,MIN(IFNULL(t.`sale_price`,t.`price`)) AS price"+
				" FROM `amazoninfo_product2` t WHERE t.`country` IN ('com','de') AND t.`active`='1' AND t.`is_fba`='1' and t.`sale_price`>0 GROUP BY t.`asin`,t.`country`";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj : list) {
			String country = obj[0].toString();
			String asin = obj[1].toString();
			if (obj[2] == null) {
				continue;
			}
			Float price = Float.parseFloat(obj[2].toString());
			Map<String, Float> map = rs.get(country);
			if (map == null) {
				map = Maps.newHashMap();
				rs.put(country, map);
			}
			map.put(asin, price);
		}
		return rs;
	}
	
	/**
	 *获取某平台、所有的asin(母贴)集合
	 */
	public Map<String,Set<String>> findCountryAsinName(){
		Map<String,Set<String>> rs = Maps.newHashMap();
		//如果是母贴的话，就随便找个子贴的产品名字（用来匹配产品名）
		String sql=" SELECT a.`query_time` FROM amazoninfo_posts_detail AS a ORDER BY a.`query_time` DESC LIMIT 1 ";
		List<Date> queryDates = amazonProduct2Dao.findBySql(sql);
		Date queryDate = queryDates.get(0);
		sql="SELECT DISTINCT CONCAT(a.`asin`,',',a.`country`) FROM amazoninfo_review_comment AS a ";
		List<String> asinCountrys=amazonProduct2Dao.findBySql(sql);
		
		sql="SELECT a.`id`,a.`country`,a.`ASIN`,a.`product_name`,a.`parent_id` FROM amazoninfo_posts_detail AS a WHERE a.query_time=:p1 AND a.`ASIN` IS NOT NULL " +
				" AND CONCAT(a.`ASIN`,',',a.`country`) IN :p2";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql,new Parameter(DateUtils.addDays(queryDate,-1),asinCountrys));
		
		//获取各个国家淘汰的
		sql="SELECT  CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END,b.`color`) AS pname,b.`country` FROM psi_product_eliminate b WHERE b.`del_flag`='0' AND is_sale='4' ";
		List<Object[]> list2=amazonProduct2Dao.findBySql(sql);
		Map<String,Set<String>> noSaleMap = Maps.newHashMap();
		for(Object[] obj:list2){
			if(obj[0]!=null&&obj[1]!=null){
				String productName=obj[0].toString();
				String country =obj[1].toString();
				Set<String> noSaleProduct =null;
				if(noSaleMap.get(country)==null){
					noSaleProduct=Sets.newHashSet();
				}else{
					noSaleProduct=noSaleMap.get(country);
				}
				noSaleProduct.add(productName);
				noSaleMap.put(country, noSaleProduct);
			}
			
		}
		
		
		//获得非淘汰产品的带电属性
		sql="SELECT  CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END,b.`color`,'_',b.`country`) AS pname, CASE WHEN p.type='Keyboard' THEN '1' ELSE p.`has_power` END has_power FROM psi_product_eliminate b,psi_product p  WHERE  b.`product_id` = p.`id` AND  b.`del_flag` = '0'  AND b.`added_month` IS NOT NULL AND b.`is_sale` != '4' AND b.country !='ca' AND b.country !='es' GROUP BY pname, b.`country`";
		List<Object[]> list1=amazonProduct2Dao.findBySql(sql);
		Map<String,String> powerMap = Maps.newHashMap();
		for(Object[] obj:list1){
			if(obj[0]!=null&&obj[1]!=null){
				powerMap.put(obj[0].toString(),obj[1].toString());
			}
			
		}
		
		List<Object[]> needList = Lists.newArrayList();
		Map<Integer,String> parentNameMap =Maps.newHashMap(); 
		for (Object[] obj : list) {
			if(obj[4]==null){
				//母贴是需要的
				needList.add(obj);
			}else{
				//找一个子贴的名字
				if(parentNameMap.get(Integer.parseInt(obj[4].toString()))==null){
					parentNameMap.put(Integer.parseInt(obj[4].toString()), obj[3]!=null?obj[3].toString():"");
				}
			}
		}
		Map<String,Integer> products = this.eliminateService.findStockProduct();
		for (Object[] obj : needList) {
			Integer id = Integer.parseInt(obj[0].toString());
			String country = obj[1].toString();
			String asin = obj[2].toString();
			String pname = "";
			if(obj[3]==null){
				pname=parentNameMap.get(id);
			}else{
				pname=obj[3].toString();
			}
			
			if(StringUtils.isEmpty(pname)){
				continue;
			}
			//对淘汰产品进行过滤
			if(noSaleMap.get(country)!=null&&noSaleMap.get(country).contains(pname)){
				continue;
			}
			
			//对非淘汰库存为0的过滤----------------
			String key = pname+"_"+country;
			String hasPower=powerMap.get(key);
			if("1".equals(hasPower)){
				if("de,fr,es,it".contains(country)){
					Integer num = products.get(pname+"_eu");
					Integer num1 = products.get(pname+"_uk");
					if(num!=null&&num1!=null&&num.equals(num1)){
						continue;
					}
				}else{
					Integer num = products.get(key);
					if(num!=null&&num.intValue()==0){
						continue;
					}
				}
			}else{
				Integer num = 0 ;
				if("de,fr,es,it,uk".contains(country)){
					num = products.get(pname+"_eu");
				}else{
					num = products.get(key);
				}
				if(num!=null&&num==0){
					continue;
				}
			}
			
			Set<String> asinNames = null;
			if(rs.get(country)==null){
				asinNames=Sets.newHashSet();
			}else{
				asinNames=rs.get(country);
			}
			asinNames.add(asin+","+pname);
			rs.put(country, asinNames);
		}
		return rs;
	}
	
	public Map<String,Object[]> findProductPrice(){
		Map<String,Object[]> map=Maps.newHashMap();
		String sql="SELECT c.name,MIN(CASE WHEN c.country = 'de' THEN c.price ELSE NULL END ) AS de ,"+
				 " MIN(CASE  WHEN c.country = 'fr'  THEN c.price  ELSE NULL  END) AS fr, "+
				 " MIN(CASE  WHEN c.country = 'it'  THEN c.price  ELSE NULL  END) AS it, "+
				 " MIN(CASE  WHEN c.country = 'es'  THEN c.price  ELSE NULL  END) AS es, "+
				 " MIN(CASE  WHEN c.country = 'uk'  THEN c.price  ELSE NULL  END) AS uk, "+
			 " MIN(CASE WHEN c.country = 'com'  THEN c.price ELSE NULL  END) AS us, "+
			 " MIN(CASE  WHEN c.country = 'ca'  THEN c.price  ELSE NULL  END) AS ca, "+
			 " MIN(CASE  WHEN c.country = 'jp'  THEN c.price  ELSE NULL  END) AS jp "+
             " FROM(SELECT bb.*,aa.price FROM "+
             " (SELECT a.`sku`,a.`country`, a.`sale_price` AS price  "+
             " FROM amazoninfo_product2 a WHERE a.`active` = '1' AND a.`is_fba` = '1' AND a.`sale_price` > 0) aa, "+
             " (SELECT  CONCAT( b.`product_name`,CASE WHEN b.`color` != '' THEN '_' ELSE ''  END, b.`color`) AS NAME, "+
             "   b.`sku`,b.country  FROM psi_sku b  WHERE b.`del_flag` = '0' AND b.`product_name` != 'Inateck other' AND b.`product_name` != 'Inateck Old') bb  "+
             " WHERE aa.sku = bb.sku  "+
             " AND aa.country = bb.country) c  "+
             " GROUP BY c.name ";
		
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);	
		for (Object[] obj: list) {
			map.put(obj[0].toString(), obj);
		}
		return map;	
	}
	
	/**
	 *获得某国家的asin 
	 */
	public Map<String,String>  getAsinByCountry(String country){
		Map<String,String>  rs = Maps.newHashMap();
		String sql=" SELECT DISTINCT b.`asin`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`) AS proName FROM psi_sku AS a,amazoninfo_product2 AS b WHERE a.`sku`=BINARY(b.`sku`) AND a.`country`=b.`country` AND a.`country` =:p1 AND a.`product_name`!='Inateck other' AND a.`product_name`!='Inateck old'  AND a.`del_flag`='0' AND b.`active`='1' AND b.`is_fba`='1'";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql,new Parameter(country));	
		for (Object[] obj: list) {
			String asin = obj[0].toString();
			String name = obj[1]!=null?obj[1].toString():"";
			rs.put(asin, name);
		}
		return rs;
	}
	
	/**
	 *获得跟帖扫描的asin 
	 */
	public Map<String,Set<String>> getFollowSellerAsins(){
		//销售前十产品
		String countrys="('com','de','fr','uk','jp')";
		String  sql="SELECT aa.pname FROM  (SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`) AS pname,SUM(a.`sales_volume`) AS num ,a.`country`" +
				" FROM amazoninfo_sale_report a WHERE a.`date` >= DATE_ADD(CURDATE(),INTERVAL -7 DAY) AND a.`product_name` IS NOT NULL  AND a.`country` IN "+countrys+" AND a.`order_type`='1' " +
				"  GROUP BY pname,a.`country`) aa  GROUP BY aa.pname ORDER BY num DESC LIMIT 50";
		List<String> proNames=amazonProduct2Dao.findBySql(sql);	
		sql="SELECT DISTINCT a.`country`,b.`asin`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`) AS proName FROM psi_sku AS a,amazoninfo_product2 AS b WHERE a.`sku`=BINARY(b.`sku`) AND a.`country`=b.`country` AND a.`country` in "+countrys+" AND a.`product_name`!='Inateck other' AND a.`product_name` in :p1  AND a.`del_flag`='0' AND b.`active`='1' AND b.`is_fba`='1'";
		Map<String,Set<String>> rs = Maps.newHashMap();
		List<Object[]> list=amazonProduct2Dao.findBySql(sql,new Parameter(proNames));	
		for (Object[] obj: list) {
			String country = obj[0].toString();
			Set<String> set=null;
			if(rs.get(country)==null){
				set=Sets.newHashSet();
			}else{
				set =rs.get(country); 
			}
			set.add(obj[1].toString()+","+(obj[2]!=null?obj[2].toString():""));
			rs.put(obj[0].toString(), set);
		}
		
		//自己录入的产品
		sql="SELECT a.`country`,a.`asin`,a.`product_name` FROM amazoninfo_follow_asin AS a  WHERE a.`state`='0'";
		List<Object[]> list1=amazonProduct2Dao.findBySql(sql);
		if(list1!=null&&list1.size()>0){
			for (Object[] obj: list1) {
				String country = obj[0].toString();
				Set<String> set=null;
				if(rs.get(country)==null){
					set=Sets.newHashSet();
				}else{
					set =rs.get(country); 
				}
				set.add(obj[1].toString()+","+(obj[2]!=null?obj[2].toString():""));
				rs.put(obj[0].toString(), set);
			}
		}
		
		//buyBox最低的前100个asin
		sql="SELECT DISTINCT a.`country`,b.`child_asin`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`) AS proName FROM psi_sku AS a ,(SELECT aa.`child_asin`,aa.`country` FROM (SELECT a.`child_asin`,a.`country`,SUM(buy_box_percentage) AS buyBox FROM amazoninfo_business_report AS  a WHERE a.`create_date` >=DATE_ADD(CURDATE(),INTERVAL -30 DAY) AND a.`country`" +
				" IN "+countrys+" AND a.`buy_box_percentage`<100  AND a.`buy_box_percentage`>0 GROUP BY  a.`child_asin`,a.`country`)AS aa  ORDER BY aa.buyBox ASC LIMIT 500) AS b " +
				" WHERE a.`asin`=b.child_asin AND a.`country`=b.country AND a.`product_name`!='Inateck Old'";
		List<Object[]> list2=amazonProduct2Dao.findBySql(sql);
		if(list2!=null&&list2.size()>0){
			for (Object[] obj: list2) {
				String country = obj[0].toString();
				Set<String> set=null;
				if(rs.get(country)==null){
					set=Sets.newHashSet();
				}else{
					set =rs.get(country); 
				}
				set.add(obj[1].toString()+","+(obj[2]!=null?obj[2].toString():""));
				rs.put(obj[0].toString(), set);
			}
		}
		return rs;
	}
	
	public Map<String,String>  getNameByCountry(String country){
		Map<String,String>  rs = Maps.newHashMap();
		String sql=" SELECT DISTINCT a.`asin`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`) AS proName FROM psi_sku AS a where a.`country` =:p1 AND a.`product_name`!='Inateck other' AND a.`product_name`!='Inateck old' and asin is not null AND a.`del_flag`='0' ";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql,new Parameter(country));	
		for (Object[] obj: list) {
			String asin = obj[0].toString();
			String name = obj[1]!=null?obj[1].toString():"";
			rs.put(asin, name);
		}
		return rs;
	}
	
	public Map<String,String>  getNameByCountry(){
		Map<String,String>  rs = Maps.newHashMap();
		String sql=" SELECT DISTINCT a.`asin`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`) AS proName,a.country FROM psi_sku AS a where  a.`product_name`!='Inateck other' AND a.`product_name`!='Inateck old' and asin is not null AND a.`del_flag`='0' ";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);	
		for (Object[] obj: list) {
			String asin = obj[0].toString();
			String name = obj[1]!=null?obj[1].toString():"";
			String country=obj[2].toString();
			rs.put(asin+"_"+country, name);
		}
		return rs;
	}
	
	//查询设置B2B价格的产品
	public List<AmazonProduct2> findB2bPriceList(){
		List<AmazonProduct2> rs = Lists.newArrayList();
		String sql = "SELECT SUBSTRING_INDEX(GROUP_CONCAT(t.`id` ORDER BY t.`business_price`),',', 1)"+
				" FROM `amazoninfo_product2` t WHERE t.`business_price` IS NOT NULL AND t.`sale_price` IS NOT NULL AND t.`active`='1' GROUP BY t.`country`,t.`asin`";
		List<Object> list = amazonProduct2Dao.findBySql(sql);
		if (list != null && list.size() > 0) {
			List<Integer> idList = Lists.newArrayList();
			for (Object id : list) {
				idList.add(Integer.parseInt(id.toString()));
			}
			if (idList.size() > 0) {
				DetachedCriteria dc = amazonProduct2Dao.createDetachedCriteria();
				dc.add(Restrictions.in("id", idList));
				rs = amazonProduct2Dao.find(dc);
			}
		}
		return rs;
	}
	
	public Map<String,List<String>> getAllBandingSku(){
		String skuSql ="SELECT b.`product_name`,b.`country`,b.`color`,b.`sku` FROM psi_sku AS b WHERE b.`del_flag`='0' ";
		List<Object[]> psiSkus=this.amazonProduct2Dao.findBySql(skuSql); 
		Map<String,List<String>> skuMap = Maps.newHashMap();
		for(Object[] psiSku:psiSkus){
			String productName = psiSku[0].toString();
			String country = psiSku[1].toString();
			String color   = psiSku[2].toString();
			String sku     = psiSku[3].toString();
			if(StringUtils.isNotEmpty(color)){
				productName=productName+"_"+color;
			}
			String key=productName+","+country;
			List<String> temp=skuMap.get(key);
			if(temp==null){
				temp=Lists.newArrayList();
				skuMap.put(key, temp);
			}
			temp.add(sku);
		}
		return skuMap;
	}
	
	public Map<String,String> findIsActive(){
		 Map<String,String>  map=Maps.newHashMap();
		 String sql="select country,sku,active from amazoninfo_product2";
		 List<Object[]> list=this.amazonProduct2Dao.findBySql(sql); 
		 for (Object[] obj: list) {
			String country=obj[0].toString();
			String sku=obj[1].toString();
			String active=obj[2].toString();
			map.put(country+sku,("1".equals(active)?"在贴":"已删贴"));
		 }
		 return map;
	}
	
	/*public Map<String,String> findNameBySku(String country){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT sku,product_name FROM psi_sku WHERE country=:p1 AND del_flag='0'";
		List<Object[]> list=this.amazonProduct2Dao.findBySql(sql,new Parameter(country)); 
		for (Object[] obj: list) {
			map.put(obj[0].toString(), obj[1].toString());
		}
		return map;
	}*/
	
	public Map<String,String> findNameBySkuByAccount(String accountName){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT sku,product_name FROM psi_sku WHERE account_name=:p1 AND del_flag='0'";
		List<Object[]> list=this.amazonProduct2Dao.findBySql(sql,new Parameter(accountName)); 
		for (Object[] obj: list) {
			map.put(obj[0].toString(), obj[1].toString());
		}
		return map;
	}
	
	public Map<String,String> findNameColorBySku(String country){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT sku,concat(product_name,case when color='' then '' else concat('_',color) end) FROM psi_sku WHERE country=:p1 AND del_flag='0'";
		List<Object[]> list=this.amazonProduct2Dao.findBySql(sql,new Parameter(country)); 
		for (Object[] obj: list) {
			map.put(obj[0].toString(), obj[1].toString());
		}
		return map;
	}
	
	public Map<String,String> findNameColorBySku2(String country){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT concat(product_name,case when color='' then '' else concat('_',color) end),sku FROM psi_sku WHERE country=:p1 and `use_barcode`='1' AND del_flag='0'";
		List<Object[]> list=this.amazonProduct2Dao.findBySql(sql,new Parameter(country)); 
		for (Object[] obj: list) {
			map.put(obj[0].toString(), obj[1].toString());
		}
		return map;
	}
	
	public Map<String,List<Object[]>> findPrice(){
		Map<String,List<Object[]>>  map=Maps.newHashMap();
		//String sql="SELECT p.`country`,p.sku,p.`sale_price` FROM amazoninfo_product2 p WHERE p.`active`='1' AND p.`sale_price` IS NOT NULL AND p.`sale_price`!=p.`price` AND p.`country` IN ('de') LIMIT 4";
		String sql="SELECT p.`country`,p.sku,p.`sale_price` FROM amazoninfo_product2 p WHERE p.`active`='1' AND p.`sale_price` IS NOT NULL AND p.`sale_price`!=p.`price` AND p.`country` IN ('de','fr','it','es','uk')";
		List<Object[]> list=this.amazonProduct2Dao.findBySql(sql); 
		for (Object[] obj: list) {
			List<Object[]> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Lists.newArrayList();
				map.put(obj[0].toString(), temp);
			}
			temp.add(obj);
		}
	    return map;
	}
	
	public String find(String sku,String key){
		String sql="SELECT p.`asin` FROM amazoninfo_product2 p WHERE p.`country`=:p1 AND sku=:p2";
		List<String> list=amazonProduct2Dao.findBySql(sql,new Parameter(key,sku)); 
		if(list!=null&&list.size()>0){
			String asin=list.get(0);
			sql="SELECT DISTINCT CONCAT(p.`product_name`,CASE WHEN p.`color`='' THEN '' ELSE CONCAT('_',color) END) NAME FROM psi_sku p WHERE p.asin =:p1 AND p.`del_flag`='0' ";
			list=amazonProduct2Dao.findBySql(sql,new Parameter(asin)); 
			if(list!=null&&list.size()>0){
				return list.get(0);
			}
		}
		return null;
	}
	
	public List<String> findSku(){
		String sql="SELECT p.`sku` FROM amazoninfo_product2 p WHERE p.`active`='1' AND p.`country`='it' AND p.sku LIKE '84-I%' ";
		return amazonProduct2Dao.findBySql(sql);
	}
	
	public Map<String,String> getMinPrice2ByAccount(){
		String sql="SELECT account_name,ASIN,sku,sale_price FROM amazoninfo_product2 WHERE ASIN IS NOT NULL AND sku IS  NOT NULL AND active='1' "+
           " AND sale_price IS NOT NULL AND is_fba='1' "+
           " AND LOWER(sku) NOT LIKE '%old/_%' ESCAPE '/' AND LOWER(sku) NOT LIKE '%/_old%' ESCAPE '/'  "+
           " AND LOWER(sku) NOT LIKE '%old-%'  AND LOWER(sku) NOT LIKE '%-old%'  ";
		List<Object[]> rs=amazonProduct2Dao.findBySql(sql);
		Map<String,String> map=Maps.newHashMap();
		for (Object[] obj: rs) {
			String key=obj[0].toString()+"_"+obj[1].toString();
			String sku=obj[2].toString();
			Float price=((BigDecimal)obj[3]).floatValue();
			String temp=map.get(key);
			if(StringUtils.isBlank(temp)){
				map.put(key, sku+","+price);
			}else{
				String[] arr=temp.split(",");
				if(price<Float.parseFloat(arr[1])){
					map.put(key, sku+","+price);
				}
			}
		}
		return map;
	}
	
	public Map<String,String> getMaxPrice2ByAccount(){
		String sql="SELECT account_name,ASIN,sku,sale_price FROM amazoninfo_product2 WHERE ASIN IS NOT NULL AND sku IS  NOT NULL AND active='1' "+
           " AND sale_price IS NOT NULL AND is_fba='1' "+
           " AND LOWER(sku) NOT LIKE '%old/_%' ESCAPE '/' AND LOWER(sku) NOT LIKE '%/_old%' ESCAPE '/'  "+
           " AND LOWER(sku) NOT LIKE '%old-%'  AND LOWER(sku) NOT LIKE '%-old%'  ";
		List<Object[]> rs=amazonProduct2Dao.findBySql(sql);
		Map<String,String> map=Maps.newHashMap();
		for (Object[] obj: rs) {
			String key=obj[0].toString()+"_"+obj[1].toString();
			String sku=obj[2].toString();
			Float price=((BigDecimal)obj[3]).floatValue();
			String temp=map.get(key);
			if(StringUtils.isBlank(temp)){
				map.put(key, sku+","+price);
			}else{
				String[] arr=temp.split(",");
				if(price>=Float.parseFloat(arr[1])){
					map.put(key, sku+","+price);
				}
			}
		}
		return map;
	}
	
	public Map<String,Float> findAllProductPriceByAccount(){
		Map<String,Float> map=Maps.newHashMap();
		String sql="SELECT account_name,ASIN,MIN(p.`sale_price`) FROM amazoninfo_product2 p WHERE p.`is_fba`='1' AND p.`active`='1' AND p.`sale_price` IS NOT NULL "+
                   " GROUP BY account_name,ASIN ";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj: list) {
			String country=obj[0].toString();
			String asin=obj[1].toString();
			Float price=Float.parseFloat(obj[2].toString());
			map.put(country+"_"+asin, price);
		}
		return map;
	}
	
	public Map<String,String> findAllProductPrice1ByAccount(){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT account_name,ASIN,group_concat(distinct p.`sale_price`) FROM amazoninfo_product2 p WHERE p.`is_fba`='1' AND p.`active`='1' AND p.`sale_price` IS NOT NULL "+
                   " GROUP BY account_name,ASIN ";
		List<Object[]> list=amazonProduct2Dao.findBySql(sql);
		for (Object[] obj: list) {
			String country=obj[0].toString();
			String asin=obj[1].toString();
			String price=obj[2].toString();
			map.put(country+"_"+asin, price);
		}
		return map;
	}
}