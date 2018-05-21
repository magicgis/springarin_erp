/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.SalesForecastDao;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecast;

/**
 * 销量预测Service
 * @author Tim
 * @version 2015-03-03
 */
@Component
@Transactional(readOnly = true)
public class SalesForecastService extends BaseService {

	@Autowired
	private SalesForecastDao salesForecastDao;
	
	public SalesForecast get(Integer id) {
		return salesForecastDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(SalesForecast salesForecast) {
		salesForecastDao.save(salesForecast);
	}
	
	@Transactional(readOnly = false)
	public void save(List<SalesForecast> salesForecasts) {
		salesForecastDao.save(salesForecasts);
	}
	
	@Transactional(readOnly = false)
	public void delete(String country,String productName) {
		String sql = "UPDATE amazoninfo_sales_forecast SET del_flag ='1' WHERE country= :p1 AND product_name = :p2";
		salesForecastDao.updateBySql(sql,new Parameter(country,productName));
	}
	
	@Transactional(readOnly = false)
	public int deleteByStartDate(String country,String productName,Date start) {
		String sql = "UPDATE amazoninfo_sales_forecast SET del_flag ='1' WHERE country= :p1 AND product_name = :p2 And data_date >= :p3";
		return salesForecastDao.updateBySql(sql,new Parameter(country,productName,start));
	}
	
	public boolean findProducExsit(String country,String productName){
		String sql = "SELECT COUNT(1) FROM amazoninfo_sales_forecast a  WHERE a.`del_flag` = 0 AND a.`product_name` = :p1 AND a.`country` =:p2";
		return ((BigInteger)salesForecastDao.findBySql(sql, new Parameter(productName,country)).get(0)).intValue()>0;
	}
	
	
	//周 [sku 数]
	public Map<String,Map<String,Integer>> getRealSale(String country,Date startDate,Date endDate,Set<String> skus){
		Date temp1 = DateUtils.addWeeks(new Date(), -1);
		temp1 = DateUtils.getLastDayOfWeek(1900+temp1.getYear(), DateUtils.getWeekOfYear(temp1));
		if(endDate.after(temp1)){
			endDate = temp1;
		}
		String sql = "SELECT b.sellersku,DATE_FORMAT(a.`purchase_date`,'%x-%v') AS weekGroup,SUM(b.`quantity_ordered`) AS num FROM amazoninfo_order a , amazoninfo_orderitem b WHERE a.`id` = b.`order_id` and a.order_channel is null and  a.`sales_channel` like :p1 and a.`purchase_date` >= :p2 and a.`purchase_date` <= :p3 and b.sellersku in :p4 and a.order_status in ('Shipped','Pending','Unshipped')   GROUP BY b.`sellersku`,a.`sales_channel`,weekGroup";
		List<Object> list = salesForecastDao.findBySql(sql, new Parameter("%"+country+"%",startDate,DateUtils.addDays(endDate,1),skus));
		 Map<String,Map<String,Integer>> rs = Maps.newHashMap();
		for (Object object : list) {
			Object[] objs = (Object[])object;
			String week = objs[1].toString();
			Map<String,Integer> temp = rs.get(week);
			if(temp==null){
				temp = Maps.newHashMap();
				rs.put(week, temp);
			}
			temp.put(objs[0].toString(), Integer.parseInt(objs[2].toString()));
		}
		return rs;
	}
	
	//周 [国家[sku 数]]
	public Map<String,Map<String,Map<String,Integer>>> getRealSale(Date startDate,Date endDate,Set<String> skus){
		Date temp11 = DateUtils.addWeeks(new Date(), -1);
		temp11 = DateUtils.getLastDayOfWeek(1900+temp11.getYear(), DateUtils.getWeekOfYear(temp11));
		if(endDate.after(temp11)){
			endDate = temp11;
		}
		String sql = "SELECT b.sellersku,DATE_FORMAT(a.`purchase_date`,'%x-%v') AS weekGroup,SUM(b.`quantity_ordered`) AS num ,a.`sales_channel`FROM amazoninfo_order a , amazoninfo_orderitem b WHERE a.`id` = b.`order_id`  and a.`purchase_date` >= :p1 and a.`purchase_date` <= :p2 and a.order_channel is null and b.sellersku in :p3 and a.order_status in ('Shipped','Pending','Unshipped')  GROUP BY b.`sellersku`,a.`sales_channel`,weekGroup";
		List<Object> list = salesForecastDao.findBySql(sql, new Parameter(startDate,DateUtils.addDays(endDate,1),skus));
		Map<String,Map<String,Map<String,Integer>>> rs = Maps.newHashMap();
		for (Object object : list) {
			Object[] objs = (Object[])object;
			String week = objs[1].toString();
			Map<String,Map<String,Integer>> temp = rs.get(week);
			if(temp==null){
				temp = Maps.newHashMap();
				rs.put(week, temp);
			}
			String country = objs[3].toString();
			country = country.substring(country.lastIndexOf(".")+1);
			Map<String,Integer> temp1 = temp.get(country);
			if(temp1==null){
				temp1 = Maps.newHashMap();
				temp.put(country, temp1);
			}
			temp1.put(objs[0].toString(), Integer.parseInt(objs[2].toString()));
		}
		return rs;
	}
	
	//周 [sku 数]
	public Map<String,Map<String,Integer>> getRealSaleByEbay(Date startDate,Date endDate,Set<String> skus){
		Date temp1 = DateUtils.addWeeks(new Date(), -1);
		temp1 = DateUtils.getLastDayOfWeek(1900+temp1.getYear(), DateUtils.getWeekOfYear(temp1));
		if(endDate.after(temp1)){
			endDate = temp1;
		}
		String sql = "SELECT b.`sku`,DATE_FORMAT(a.`paid_time`,'%x-%v') AS weekGroup,SUM(b.`quantity_purchased`) AS num FROM ebay_order a , ebay_orderitem b WHERE a.`id` = b.`order_id`  AND a.`paid_time` >= :p1 AND a.`paid_time` <= :p2 AND b.sku IN :p3 AND a.order_status in ('Completed','Active') and country='de'  GROUP BY b.`sku`,weekGroup";
		List<Object> list = salesForecastDao.findBySql(sql, new Parameter(startDate,DateUtils.addDays(endDate,1),skus));
		 Map<String,Map<String,Integer>> rs = Maps.newHashMap();
		for (Object object : list) {
			Object[] objs = (Object[])object;
			String week = objs[1].toString();
			Map<String,Integer> temp = rs.get(week);
			if(temp==null){
				temp = Maps.newHashMap();
				rs.put(week, temp);
			}
			temp.put(objs[0].toString(), Integer.parseInt(objs[2].toString()));
		}
		return rs;
	}
	
	
	//[产品 skus]
	public Map<String,List<String>> getProductsSkuMap(String country){
		String sql = "SELECT DISTINCT a.`sku`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) AS NAME FROM psi_sku a WHERE  a.`sku` !='' AND a.`country` =:p1 AND a.`del_flag`='0'";
		List<Object> list = salesForecastDao.findBySql(sql,new Parameter(country));
		Map<String,List<String>> rs = Maps.newHashMap();
		for (Object object : list) {
			Object[] objs = (Object[])object;
			String pN = objs[1].toString();
			String sku = objs[0].toString();
			List<String> skus = rs.get(pN);
			if(skus==null){
				skus = Lists.newArrayList();
				rs.put(pN, skus);
			}
			skus.add(sku);
		}
		return rs;
	}
	
	//国家[产品 skus]
	public Map<String,Map<String,List<String>>> getProductsSkuMap(){
		String sql = "SELECT DISTINCT a.`sku`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) AS NAME ,a.`country`  FROM psi_sku a WHERE  a.`sku` !='' AND a.`del_flag`='0'";
		List<Object> list = salesForecastDao.findBySql(sql,null);
		Map<String,Map<String,List<String>>> rs = Maps.newHashMap();
		for (Object object : list) {
			Object[] objs = (Object[])object;
			String pN = objs[1].toString();
			String sku = objs[0].toString();
			String country = objs[2].toString();
			Map<String,List<String>> temp = rs.get(country);
			if(temp==null){
				temp = Maps.newHashMap();
				rs.put(country,temp);
			}
			List<String> skus = temp.get(pN);
			if(skus==null){
				skus = Lists.newArrayList();
				temp.put(pN, skus);
			}
			skus.add(sku);
		}
		return rs;
	}
	
	public List<SalesForecast> find(SalesForecast salesForecast,Date start,Date end){
		DetachedCriteria dc = salesForecastDao.createDetachedCriteria();
		
		dc.add(Restrictions.ge("dataDate",start));
		dc.add(Restrictions.le("dataDate",end));
		if(!"total".equals(salesForecast.getCountry())){
			dc.add(Restrictions.eq("country",salesForecast.getCountry()));
		}
		if(StringUtils.isNotBlank(salesForecast.getProductName())){
			dc.add(Restrictions.like("productName","%"+salesForecast.getProductName()+"%"));
		}
		dc.add(Restrictions.eq("delFlag","0"));
		return salesForecastDao.find(dc);
	}
	
	
	public List<Object> getProducts(SalesForecast salesForecast){
		String sql = "";
		List<Object> list = null;
		String pN = "";
		if(StringUtils.isNotBlank(salesForecast.getProductName())){
			pN = "and a.`product_name` like '%"+salesForecast.getProductName()+"%'";
		}
		if(!"total".equals(salesForecast.getCountry())){
			sql = "SELECT DISTINCT a.`product_name` FROM amazoninfo_sales_forecast a WHERE a.`del_flag`='0' and a.`country` = :p1 "+pN;
			list = salesForecastDao.findBySql(sql,new Parameter(salesForecast.getCountry()));
		}else{
			sql = "SELECT DISTINCT a.`product_name` FROM amazoninfo_sales_forecast a WHERE a.`del_flag`='0' "+pN;
			list = salesForecastDao.findBySql(sql,null);
		}
		return list;
	}
	
	public List<List<Object>> getProductSales(String productName,String country){
		String sql = "SELECT DATE_FORMAT(a.`date`,'%Y/%m') AS monthGroup , SUM(a.`sales_volume`) FROM amazoninfo_sale_report a WHERE a.order_type='1' and a.`country` in :p1 AND CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) = :p2  GROUP BY monthGroup";
		List<String>countrys = null;
		if("eu".equals(country)){
			countrys = Lists.newArrayList("de","uk","fr","it","es");
		} else if("eunouk".equals(country)){
			countrys = Lists.newArrayList("de","fr","it","es");
		}else{
			countrys = Lists.newArrayList(country);
		}
		List<Object[]> list = salesForecastDao.findBySql(sql,new Parameter(countrys,productName));
		List<List<Object>> rs = Lists.newArrayList();
		List<Object> list1 = Lists.newArrayList();
		List<Object> list2 = Lists.newArrayList();
		for (Object[] objs : list) {
			list1.add("'"+(String)objs[0]+"'");
			list2.add(((BigDecimal)objs[1]).intValue());
		}
		rs.add(list1);
		rs.add(list2);
		return rs;
	}
	
	
}
