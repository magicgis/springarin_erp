/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.SalesForecastDaoByMonth;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecastByMonth;

/**
 * 销量预测Service
 * @author Tim
 * @version 2015-03-03
 */
@Component
@Transactional(readOnly = true)
public class SalesForecastServiceByMonth extends BaseService {

	@Autowired
	private SalesForecastDaoByMonth SalesForecastByMonthDao;
	
	public SalesForecastByMonth get(Integer id) {
		return SalesForecastByMonthDao.get(id);
	}
	
	
	public Map<String,SalesForecastByMonth> find(String type) {
		DetachedCriteria dc = SalesForecastByMonthDao.createDetachedCriteria();
		dc.add(Restrictions.ge("dataDate",new Date()));
		dc.add(Restrictions.eq("type",type));
		List<SalesForecastByMonth> list = SalesForecastByMonthDao.find(dc);
		Map<String,SalesForecastByMonth> rs = Maps.newHashMap();
		for (SalesForecastByMonth salesForecastByMonth : list) {
			String key = salesForecastByMonth.getProductName();
			key +=","+salesForecastByMonth.getCountry();
			key +=","+DateUtils.formatDate(salesForecastByMonth.getDataDate(),"yyyyMM");
			rs.put(key, salesForecastByMonth);
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void save(SalesForecastByMonth SalesForecastByMonth) {
		SalesForecastByMonthDao.save(SalesForecastByMonth);
	}
	
	@Transactional(readOnly = false)
	public void save(List<SalesForecastByMonth> SalesForecastByMonths) {
		SalesForecastByMonthDao.save(SalesForecastByMonths);
	}
	
	@Transactional(readOnly = false)
	public void delete(String country,String productName) {
		String sql = "UPDATE amazoninfo_sales_forecast SET del_flag ='1' WHERE country= :p1 AND product_name = :p2";
		SalesForecastByMonthDao.updateBySql(sql,new Parameter(country,productName));
	}
	
	@Transactional(readOnly = false)
	public int deleteByStartDate(String country,String productName,Date start) {
		String sql = "UPDATE amazoninfo_sales_forecast SET del_flag ='1' WHERE country= :p1 AND product_name = :p2 And data_date >= :p3";
		return SalesForecastByMonthDao.updateBySql(sql,new Parameter(country,productName,start));
	}
	
	public boolean findProducExsit(String country,String productName){
		String sql = "SELECT COUNT(1) FROM amazoninfo_sales_forecast a  WHERE a.`del_flag` = 0 AND a.`product_name` = :p1 AND a.`country` =:p2";
		return ((BigInteger)SalesForecastByMonthDao.findBySql(sql, new Parameter(productName,country)).get(0)).intValue()>0;
	}
	
	
	//[产品名 [月 数]
	public Map<String,Map<String,Integer>> getRealSale(String country,Date startDate,Date endDate){
		String sql = "SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) AS productName , DATE_FORMAT(a.`date`,'%Y-%m') AS monthGroup , SUM(a.`sales_volume`) FROM amazoninfo_sale_report a WHERE a.order_type='1' and a.`country` = :p1 AND a.`date` >= :p2 AND a.`date`<= :p3 and a.product_name is not null  GROUP BY productName,monthGroup,a.country";
		List<Object[]> list = SalesForecastByMonthDao.findBySql(sql, new Parameter(country,DateUtils.addMonths(startDate,-11),endDate));
		Map<String,Map<String,Integer>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String month = objs[1].toString();
			String productName = objs[0].toString();
			Map<String,Integer> temp = rs.get(productName);
			if(temp==null){
				temp = Maps.newHashMap();
				rs.put(productName, temp);
			}
			temp.put(month,Integer.parseInt(objs[2].toString()));
		}
		return rs;
	}
	
	//产品 [国家[月  数]]
	public Map<String,Map<String,Map<String,Integer>>> getRealSale(Date startDate,Date endDate){
		String sql = "SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) AS productName , DATE_FORMAT(a.`date`,'%Y-%m') AS monthGroup , SUM(a.`sales_volume`),a.country  FROM amazoninfo_sale_report a WHERE a.order_type='1' and a.`date` >= :p1 AND a.`date`<= :p2 and a.product_name is not null GROUP BY productName,monthGroup,a.country";
		List<Object[]> list = SalesForecastByMonthDao.findBySql(sql, new Parameter(DateUtils.addMonths(startDate,-1),endDate));
		Map<String,Map<String,Map<String,Integer>>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String month = objs[1].toString();
			String productName = objs[0].toString();
			Map<String,Map<String,Integer>> temp = rs.get(productName);
			if(temp==null){
				temp = Maps.newHashMap();
				rs.put(productName, temp);
			}
			String country = objs[3].toString();
			Map<String,Integer> temp1 = temp.get(country);
			if(temp1==null){
				temp1 = Maps.newHashMap();
				temp.put(country, temp1);
			}
			temp1.put(month, Integer.parseInt(objs[2].toString()));
			Map<String,Integer> tempTotal = temp.get("total");
			if(tempTotal==null){
				tempTotal = Maps.newHashMap();
				temp.put("total", tempTotal);
			}
			tempTotal.put(month,(tempTotal.get(month)==null?0:tempTotal.get(month))+Integer.parseInt(objs[2].toString()));
			
			if("fr,de,it,es".contains(country)){
				Map<String,Integer> temp2 = temp.get("eu");
				if(temp2==null){
					temp2 = Maps.newHashMap();
					temp.put("eu", temp2);
				}
				temp2.put(month,(temp2.get(month)==null?0:temp2.get(month))+Integer.parseInt(objs[2].toString()));
			}
		}
		return rs;
	}
	
	private static DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
	
	public Map<String,Map<String,SalesForecastByMonth>> find(SalesForecastByMonth SalesForecastByMonth,Date start,Date end){
		DetachedCriteria dc = SalesForecastByMonthDao.createDetachedCriteria();
		
		dc.add(Restrictions.ge("dataDate",start));
		dc.add(Restrictions.le("dataDate",end));
		dc.add(Restrictions.eq("country",SalesForecastByMonth.getCountry()));
		List<SalesForecastByMonth> list =  SalesForecastByMonthDao.find(dc);
		Map<String,Map<String,SalesForecastByMonth>> rs = Maps.newHashMap();
		for (SalesForecastByMonth salesForecastByMonth2 : list) {
			String productName = salesForecastByMonth2.getProductName();
			Map<String,SalesForecastByMonth> monthMap =  rs.get(productName);
			if(monthMap==null){
				monthMap = Maps.newHashMap();
				rs.put(productName, monthMap);
			}
			String month = monthFormat.format(salesForecastByMonth2.getDataDate());
			monthMap.put(month, salesForecastByMonth2);
		}
		return rs;
	}
	
	//产品 [国家[月  数]]
	public Map<String,Map<String,Map<String,SalesForecastByMonth>>> findAll(SalesForecastByMonth SalesForecastByMonth,Date start,Date end){
		DetachedCriteria dc = SalesForecastByMonthDao.createDetachedCriteria();
		
		dc.add(Restrictions.ge("dataDate",start));
		dc.add(Restrictions.le("dataDate",end));
		List<SalesForecastByMonth> list =  SalesForecastByMonthDao.find(dc);
		Map<String,Map<String,Map<String,SalesForecastByMonth>>> rs = Maps.newHashMap();
		for (SalesForecastByMonth salesForecastByMonth2 : list) {
			String productName = salesForecastByMonth2.getProductName();
			Map<String,Map<String,SalesForecastByMonth>> countryMap =  rs.get(productName);
			if(countryMap==null){
				countryMap = Maps.newHashMap();
				rs.put(productName, countryMap);
			}
			String country = salesForecastByMonth2.getCountry();
			Map<String,SalesForecastByMonth> temp1 = countryMap.get(country);
			if(temp1==null){
				temp1 = Maps.newHashMap();
				countryMap.put(country, temp1);
			}
			String month = monthFormat.format(salesForecastByMonth2.getDataDate());
			temp1.put(month, salesForecastByMonth2);
			
			
			Map<String,SalesForecastByMonth> tempTotal = countryMap.get("total");
			if(tempTotal==null){
				tempTotal = Maps.newHashMap();
				countryMap.put("total", tempTotal);
			}
			SalesForecastByMonth totalSales = tempTotal.get(month);
			if(totalSales==null){
				totalSales = new SalesForecastByMonth();
				totalSales.setQuantityForecast(0);
				totalSales.setCountry("total");
				tempTotal.put(month,totalSales);
			}
			if(salesForecastByMonth2.getQuantityForecast()!=null){
				totalSales.setQuantityForecast(totalSales.getQuantityForecast()+salesForecastByMonth2.getQuantityForecast());
			}
			
			if("fr,de,it,es".contains(country)){
				Map<String,SalesForecastByMonth> temp2 = countryMap.get("eu");
				if(temp2==null){
					temp2 = Maps.newHashMap();
					countryMap.put("eu", temp2);
				}
				SalesForecastByMonth euSales = temp2.get(month);
				if(euSales==null){
					euSales = new SalesForecastByMonth();
					euSales.setQuantityForecast(0);
					euSales.setCountry("eu");
					temp2.put(month,euSales);
				}
				if(salesForecastByMonth2.getQuantityForecast()!=null){
					euSales.setQuantityForecast(euSales.getQuantityForecast()+salesForecastByMonth2.getQuantityForecast());
				}
			}
			
			
		}
		return rs;
	}
	
	//产品 [国家[月  数]]
	public Map<String,Map<String,Map<String,SalesForecastByMonth>>> findAllWithType(Date start,Date end){
		String sql = "SELECT t.`id`,t.`country`,t.`product_name`,t.`product_id`,DATE_FORMAT(t.`data_date`,'%Y-%m'),t.`quantity_forecast`,t.`type`,t.`quantity_authentication` " +
				" FROM `amazoninfo_sales_forecast_month` t JOIN psi_product_eliminate e "+
				" ON t.`product_name` = CASE WHEN e.`color`='' THEN e.`product_name` ELSE CONCAT(e.`product_name`,'_',e.`color`) END "+
				" AND t.`country`=e.`country` AND t.`type`=e.`sales_forecast_scheme` "+
				" WHERE e.`is_sale`!='4' AND e.`is_new`='0' AND t.`data_date` >=:p1 AND t.`data_date` <=:p2 ORDER BY t.`product_name`,t.`country`,t.`data_date`";
		
		List<Object[]> list = SalesForecastByMonthDao.findBySql(sql,new Parameter(start, end));
		Map<String,Map<String,Map<String,SalesForecastByMonth>>> rs = Maps.newHashMap();
		for (Object[] obj : list) {
			Integer id = Integer.parseInt(obj[0].toString());
			String country = obj[1].toString();
			String productName = obj[2].toString();
			Integer productId = Integer.parseInt(obj[3].toString());
			String month = obj[4].toString();
			Integer quantityForecast = obj[5]==null?0:Integer.parseInt(obj[5].toString());
			String type = obj[6].toString();
			Integer quantityAuthentication = obj[7]==null?0:Integer.parseInt(obj[7].toString());
			
			SalesForecastByMonth salesForecastByMonth = new SalesForecastByMonth();
			salesForecastByMonth.setId(id);
			salesForecastByMonth.setCountry(country);
			salesForecastByMonth.setProductName(productName);
			salesForecastByMonth.setProductId(productId);
			try {
				salesForecastByMonth.setDataDate(monthFormat.parse(month));
			} catch (Exception e) {}
			salesForecastByMonth.setQuantityForecast(quantityForecast);
			salesForecastByMonth.setType(type);
			salesForecastByMonth.setQuantityAuthentication(quantityAuthentication);
			
			Map<String,Map<String,SalesForecastByMonth>> countryMap =  rs.get(productName);
			if(countryMap==null){
				countryMap = Maps.newHashMap();
				rs.put(productName, countryMap);
			}
			Map<String,SalesForecastByMonth> temp1 = countryMap.get(country);
			if(temp1==null){
				temp1 = Maps.newHashMap();
				countryMap.put(country, temp1);
			}
			temp1.put(month, salesForecastByMonth);
			
			
			Map<String,SalesForecastByMonth> tempTotal = countryMap.get("total");
			if(tempTotal==null){
				tempTotal = Maps.newHashMap();
				countryMap.put("total", tempTotal);
			}
			SalesForecastByMonth totalSales = tempTotal.get(month);
			if(totalSales==null){
				totalSales = new SalesForecastByMonth();
				totalSales.setQuantityForecast(0);
				totalSales.setCountry("total");
				tempTotal.put(month,totalSales);
			}
			if(salesForecastByMonth.getQuantityForecast()!=null){
				if (salesForecastByMonth.getQuantityAuthentication() > 0) {
					totalSales.setQuantityForecast(totalSales.getQuantityForecast()+salesForecastByMonth.getQuantityAuthentication());
				} else {
					totalSales.setQuantityForecast(totalSales.getQuantityForecast()+salesForecastByMonth.getQuantityForecast());
				}
			}
			
			if("fr,de,it,es".contains(country)){
				Map<String,SalesForecastByMonth> temp2 = countryMap.get("eu");
				if(temp2==null){
					temp2 = Maps.newHashMap();
					countryMap.put("eu", temp2);
				}
				SalesForecastByMonth euSales = temp2.get(month);
				if(euSales==null){
					euSales = new SalesForecastByMonth();
					euSales.setQuantityForecast(0);
					euSales.setCountry("eu");
					temp2.put(month,euSales);
				}
				if(salesForecastByMonth.getQuantityForecast()!=null){
					if (salesForecastByMonth.getQuantityAuthentication() > 0) {
						euSales.setQuantityForecast(euSales.getQuantityForecast()+salesForecastByMonth.getQuantityAuthentication());
					} else {
						euSales.setQuantityForecast(euSales.getQuantityForecast()+salesForecastByMonth.getQuantityForecast());
					}
				}
			}
			
			
		}
		return rs;
	}
	
	//产品 [月  数]
	public Map<String,Map<String,SalesForecastByMonth>> findByCountryType(String country, Date start, Date end){
		String sql = "SELECT t.`id`,t.`country`,t.`product_name`,t.`product_id`,DATE_FORMAT(t.`data_date`,'%Y-%m'),t.`quantity_forecast`,t.`type`,t.`quantity_authentication` " +
				" FROM `amazoninfo_sales_forecast_month` t JOIN psi_product_eliminate e "+
				" ON t.`product_name` = CASE WHEN e.`color`='' THEN e.`product_name` ELSE CONCAT(e.`product_name`,'_',e.`color`) END "+
				" AND t.`country`=e.`country` AND t.`type`=e.`sales_forecast_scheme` "+
				" WHERE e.`is_sale`!='4' AND e.`is_new`='0' AND t.`country`=:p1 AND t.`data_date` >=:p2 AND t.`data_date` <=:p3 ORDER BY t.`product_name`,t.`data_date`";
		
		List<Object[]> list = SalesForecastByMonthDao.findBySql(sql,new Parameter(country, start, end));
		Map<String,Map<String,SalesForecastByMonth>> rs = Maps.newHashMap();
		for (Object[] obj : list) {
			Integer id = Integer.parseInt(obj[0].toString());
			String productName = obj[2].toString();
			Integer productId = Integer.parseInt(obj[3].toString());
			String month = obj[4].toString();
			Integer quantityForecast = obj[5]==null?0:Integer.parseInt(obj[5].toString());
			String type = obj[6].toString();
			Integer quantityAuthentication = obj[7]==null?0:Integer.parseInt(obj[7].toString());
			
			SalesForecastByMonth salesForecastByMonth = new SalesForecastByMonth();
			salesForecastByMonth.setId(id);
			salesForecastByMonth.setCountry(country);
			salesForecastByMonth.setProductName(productName);
			salesForecastByMonth.setProductId(productId);
			try {
				salesForecastByMonth.setDataDate(monthFormat.parse(month));
			} catch (Exception e) {}
			salesForecastByMonth.setQuantityForecast(quantityForecast);
			salesForecastByMonth.setType(type);
			salesForecastByMonth.setQuantityAuthentication(quantityAuthentication);
			
			Map<String,SalesForecastByMonth> temp = rs.get(productName);
			if(temp==null){
				temp = Maps.newHashMap();
				rs.put(productName, temp);
			}
			temp.put(month, salesForecastByMonth);
		}
		return rs;
	}
	
	/**
	 * 保存销售按月预测历史信息
	 * @param saveDate
	 * @return
	 */
	@Transactional(readOnly = false)
	public int saveHisDate(Date saveDate) {
		String sql = "INSERT INTO `amazoninfo_sales_forecast_month_his` (country,product_name,product_id,last_update_by,create_by,last_update_date,data_date,quantity_forecast,save_date)"+
				" SELECT country,product_name,product_id,last_update_by,create_by,last_update_date,data_date,quantity_forecast,DATE_FORMAT(:p1,'%Y-%m-%d')"+
				" FROM `amazoninfo_sales_forecast_month` t WHERE t.`data_date` >:p1";
		return SalesForecastByMonthDao.updateBySql(sql,new Parameter(saveDate));
	}
	
	//产品 [国家[月 [预测方案  数]]]
	public Map<String,Map<String,Map<String,Map<String, Integer>>>> findProductForecast(String productName,String start, String end){
		String sql = "SELECT t.`product_name`,t.`country`,DATE_FORMAT(t.`data_date`,'%Y-%m'),t.`quantity_forecast`,t.`type` "+
					" FROM `amazoninfo_sales_forecast_month` t WHERE ";
		if (StringUtils.isNotEmpty(productName)) {
			sql +=	"t.`product_name`='"+productName+"' AND ";
		}
		sql += "DATE_FORMAT(t.`data_date`,'%Y-%m')>=:p1 AND DATE_FORMAT(t.`data_date`,'%Y-%m')<=:p2";
		
		List<Object[]> list =  SalesForecastByMonthDao.findBySql(sql, new Parameter(start,end));
		Map<String,Map<String,Map<String,Map<String, Integer>>>> rs = Maps.newHashMap();
		for (Object[] obj : list) {
			String colorName = obj[0].toString();
			String country = obj[1].toString();
			String date = obj[2].toString();
			Integer quantity = 0;
			try {
				quantity = Integer.parseInt(obj[3].toString());
			} catch (Exception e) {}
			
			String type = obj[4].toString();
			Map<String,Map<String,Map<String, Integer>>> countryMap =  rs.get(colorName);
			if(countryMap==null){
				countryMap = Maps.newHashMap();
				rs.put(colorName, countryMap);
			}
			Map<String,Map<String, Integer>> dateMap = countryMap.get(country);
			if(dateMap==null){
				dateMap = Maps.newHashMap();
				countryMap.put(country, dateMap);
			}
			Map<String, Integer> typeMap = dateMap.get(date);
			if(typeMap==null){
				typeMap = Maps.newHashMap();
				dateMap.put(date, typeMap);
			}
			typeMap.put(type, quantity);
		}
		return rs;
	}
	
	public SalesForecastByMonth findSalesForecast(String country, String productName, String month){
		DetachedCriteria dc = SalesForecastByMonthDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.eq("productName", productName));
		dc.addOrder(Order.asc("type"));
		List<SalesForecastByMonth> list = SalesForecastByMonthDao.find(dc);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		for (SalesForecastByMonth salesForecastByMonth : list) {
			if (month.equals(format.format(salesForecastByMonth.getDataDate()))) {
				return salesForecastByMonth;
			}
		}
		return null;
	}
	
	public List<SalesForecastByMonth> findSalesForecastList(String country, String productName, String month){
		List<SalesForecastByMonth> rs = Lists.newArrayList();
		DetachedCriteria dc = SalesForecastByMonthDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.eq("productName", productName));
		List<SalesForecastByMonth> list = SalesForecastByMonthDao.find(dc);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		for (SalesForecastByMonth salesForecastByMonth : list) {
			if (month.equals(format.format(salesForecastByMonth.getDataDate()))) {
				rs.add(salesForecastByMonth);
			}
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void initSalefastcast() {
		/*String sql = "UPDATE psi_product_eliminate f, (SELECT c.id,d.zhi FROM (SELECT a.`country`,b.`TYPE`,a.`id` FROM psi_product_eliminate a,psi_product b WHERE a.`product_name` = CONCAT(b.`brand`,' ',b.`model`) AND a.`sales_forecast_scheme` IS NULL ) c ,(SELECT aa.country,aa.type,SUBSTRING_INDEX(GROUP_CONCAT(aa.sales_forecast_scheme ORDER BY aa.num DESC),',',1) AS zhi FROM (SELECT a.`country`,b.`TYPE`,a.`sales_forecast_scheme`,COUNT(1) AS num FROM psi_product_eliminate a,psi_product b WHERE a.`product_name` = CONCAT(b.`brand`,' ',b.`model`) AND a.`sales_forecast_scheme` IS NOT NULL  AND a.`is_sale` ='1' AND a.`is_new` = '0'  GROUP BY a.`country`,b.`TYPE`,a.`sales_forecast_scheme`)aa GROUP BY aa.country,aa.type)d "+
           " WHERE c.type = d.type AND c.country = d.country)g SET f.`sales_forecast_scheme` = g.zhi  WHERE f.`id` = g.id ";
		SalesForecastByMonthDao.updateBySql(sql,new Parameter(null));
		sql ="UPDATE psi_product_eliminate  SET sales_forecast_scheme = '1' WHERE `sales_forecast_scheme` IS NULL AND `is_sale` ='1' AND `is_new` = '0'";
		SalesForecastByMonthDao.updateBySql(sql,new Parameter(null));*/
		String sql ="UPDATE psi_product_eliminate  SET sales_forecast_scheme = '3' WHERE `sales_forecast_scheme` IS NULL";
		SalesForecastByMonthDao.updateBySql(sql, new Parameter(null));
	}
	
	
	//产品 [国家[月  数]]
			public Map<String,Map<String,Integer>> findAllWithType(){
				String sql = "SELECT t.`country`,t.`product_name`,DATE_FORMAT(t.`data_date`,'%Y%m'),t.`quantity_forecast` " +
						" FROM `amazoninfo_sales_forecast_month` t JOIN psi_product_eliminate e "+
						" ON t.`product_name` = CASE WHEN e.`color`='' THEN e.`product_name` ELSE CONCAT(e.`product_name`,'_',e.`color`) END "+
						" AND t.`country`=e.`country` AND t.`type`=e.`sales_forecast_scheme` "+
						" WHERE t.`data_date` >=:p1 AND t.`data_date` <=:p2 order by t.`data_date`";
				Date today=new Date();
				List<Object[]> list = SalesForecastByMonthDao.findBySql(sql,new Parameter(DateUtils.addMonths(today,-1),DateUtils.addMonths(today,6)));
				Map<String,Map<String,Integer>> rs = Maps.newLinkedHashMap();
				for (Object[] obj : list) {
					String country = obj[0].toString();
					String productName = obj[1].toString();
					String month = obj[2].toString();
					Integer quantityForecast = obj[3]==null?0:Integer.parseInt(obj[3].toString());
					if(quantityForecast==0){
						continue;
					}
					Map<String,Integer> countryMap =  rs.get(productName+"_"+country);
					if(countryMap==null){
						countryMap = Maps.newLinkedHashMap();
						rs.put(productName+"_"+country, countryMap);
					}
					countryMap.put(month,quantityForecast);
					
					
					if("fr,de,it,uk,es".contains(country)){
						Map<String,Integer> temp2 = rs.get(productName+"_eu");
						if(temp2==null){
							temp2 = Maps.newLinkedHashMap();
							rs.put(productName+"_eu", temp2);
						}
						temp2.put(month, quantityForecast+(temp2.get(month)==null?0:temp2.get(month)));
					}
					
					if("fr,de,it,es".contains(country)){
						Map<String,Integer> temp2 = rs.get(productName+"_euNoUk");
						if(temp2==null){
							temp2 = Maps.newLinkedHashMap();
							rs.put(productName+"_euNoUk", temp2);
						}
						temp2.put(month, quantityForecast+(temp2.get(month)==null?0:temp2.get(month)));
					}
				}
				return rs;
			}
		
}
