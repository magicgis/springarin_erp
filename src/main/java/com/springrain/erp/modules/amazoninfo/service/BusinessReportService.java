package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MapValueComparator;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.BusinessReportDao;
import com.springrain.erp.modules.amazoninfo.entity.Advertising;
import com.springrain.erp.modules.amazoninfo.entity.BusinessReport;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;

/**
 * 亚马逊商业报表Service
 * @author tim
 * @version 2014-05-28
 */
@Component
@Transactional(readOnly = true)
public class BusinessReportService extends BaseService {

	@Autowired
	private BusinessReportDao     businessReportDao;
	@Autowired
	private MailManager 		  mailManager;
	@Autowired
	private AmazonProductService  productService;
	
	public BusinessReport get(String id) {
		return businessReportDao.get(id);
	}
	
	public Page<BusinessReport> find(Page<BusinessReport> page, BusinessReport businessReport) {
		DetachedCriteria dc = businessReportDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(businessReport.getpAsin())){
			String sql = "SELECT DISTINCT a.`asin` FROM psi_sku a WHERE a.`del_flag`='0' AND CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END)  LIKE :p1";
			List<Object> list = businessReportDao.findBySql(sql, new Parameter("%"+businessReport.getpAsin()+"%"));
			if(list.size()>0){
				dc.add(Restrictions.or(Restrictions.in("childAsin",list),Restrictions.like("childAsin", "%"+businessReport.getpAsin()+"%")));
			}else{
				dc.add(Restrictions.like("childAsin", "%"+businessReport.getpAsin()+"%"));
			}
		}
		if (StringUtils.isNotEmpty(businessReport.getCountry())){
			dc.add(Restrictions.eq("country", businessReport.getCountry()));
		}
		if (businessReport.getCreateDate()!=null){
			dc.add(Restrictions.ge("dataDate",businessReport.getCreateDate()));
		}
		if (businessReport.getDataDate()!=null){
			dc.add(Restrictions.le("dataDate",businessReport.getDataDate()));
		}
		String sql = "select DISTINCT asin from amazoninfo_product2 where country = '"+businessReport.getCountry()+"'";
		List<Object> list = businessReportDao.findBySql(sql);
		if (list == null || list.size() == 0) {
			return page;
		}
		dc.add(Restrictions.in("childAsin",list));
		dc.add(Restrictions.eq("delFlag", "0"));
		return  businessReportDao.findExt(page, dc);
	}
	
	public Page<BusinessReport> findAsin(Page<BusinessReport> page, BusinessReport businessReport) {
		DetachedCriteria dc = businessReportDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(businessReport.getChildAsin())){
			dc.add(Restrictions.eq("childAsin",businessReport.getChildAsin()));
		}
		if (StringUtils.isNotEmpty(businessReport.getCountry())){
			dc.add(Restrictions.eq("country", businessReport.getCountry()));
		}
		
		if (businessReport.getCreateDate()!=null){
			dc.add(Restrictions.ge("dataDate",businessReport.getCreateDate()));
		}
		
		if (businessReport.getDataDate()!=null){
			dc.add(Restrictions.le("dataDate",businessReport.getDataDate()));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		return  businessReportDao.find(page, dc);
	}
	
	public Map<String,Map<String, Object[]>> findCountTypeData(String country,String date1,String date2, String searchFlag,String[]types)throws ParseException{
		String typeSql = "'%Y-%m-%d'";
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = "";
		if(date1.split("-")[0].equals(date2.split("-")[0])){
			dateStr = "M/d";
		}else{
			dateStr = "yyyy/M/d";
		}
		//查询2个时间节点
		String type = "";
		if("1".equals(searchFlag)){
			typeSql="'%x-%v'";//按周查询
			format = new SimpleDateFormat("yyyy-ww");
			dateStr = "yyyy-ww";
			type = "周";
			if ("2016-01".equals(date1)) {
				date1 = "2015-53";
			}
			if ("2016-01".equals(date2)) {
				date2 = "2015-53";
			}
			date1 = getWeekStr(date1);
		}else if("2".equals(searchFlag)){
			typeSql="'%Y-%m'";//按月查询
			format = new SimpleDateFormat("yyyy-MM");
			dateStr = "yyyy-MM";
			type = "月";
		}
		Set<String> tempset = Sets.newHashSet();
		for (int i = 0; i < types.length; i++) {
			tempset.add(types[i]);
		}
		String sql = "SELECT b.`TYPE`,DATE_FORMAT(a.`data_date`,"+typeSql+") AS dates,SUM(a.`sessions`),SUM(ifnull(a.`orders_placed`,0)) " +
				"	FROM amazoninfo_business_report a ,psi_product b ,(SELECT DISTINCT ASIN,country,product_id FROM psi_sku " +
				"	WHERE del_flag='0') AS c WHERE  a.`child_asin` = c.`asin` AND b.`id` = c.`product_id`  AND a.`country`=c.`country`  " +
				"	AND a.`country`=:p1 AND b.`del_flag`='0'   AND DATE_FORMAT(a.`data_date`,"+typeSql+")>= :p2 AND DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p3 AND b.`TYPE` IN :p4 GROUP BY b.`TYPE`,dates";
		List<Object[]> list = businessReportDao.findBySql(sql, new Parameter(country,date1,date2,tempset));
		Map<String,Map<String, Object[]>> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			//String productType =HtmlUtils.htmlUnescape( objects[0].toString());
			String productType =objects[0].toString();
			String key = objects[1].toString();
			if("1".equals(searchFlag)){
				key = DateUtils.getWeekStr(key, 5, "-", true);
			}
			Date date = format.parse(key);
			String dateKey = "";
			if("1".equals(searchFlag)){
				dateKey = DateUtils.getWeekStr(date, format, 5, "-") + type;
			} else {
				dateKey = DateUtils.getDate(date,dateStr) + type;
			}
			Map<String, Object[]> temp = rs.get(productType);
			if(temp==null){
				temp = Maps.newHashMap();
				rs.put(productType, temp);
			}
			temp.put(dateKey, objects);
			
			Map<String, Object[]> total=rs.get("total");
			if(total==null){
				total=Maps.newHashMap();
				rs.put("total", total);
			}
			Object[] obj=total.get(dateKey);
			if(obj==null){
				obj=new Object[4];
				total.put(dateKey, obj);
			}
			obj[0]="total";
			obj[1]=dateKey;
			obj[2]=Integer.parseInt(objects[2]==null?"0":objects[2].toString())+Integer.parseInt(obj[2]==null?"0":obj[2].toString());
			obj[3]=Integer.parseInt(objects[3]==null?"0":objects[3].toString())+Integer.parseInt(obj[3]==null?"0":obj[3].toString());
		}
		return rs;
	}
	
	
	public List<BusinessReport> findCountData(String productName,Date startDate,Date endDate){
		return businessReportDao.findCountData(productName, startDate,endDate);
	}
	
	private static String getWeekStr(String date){
		if ("2016-01".equals(date)) {
			date = "2015-53";
		} else if(date.startsWith("2016")){
			Integer i = Integer.parseInt(date.split("-")[1]);
			i = i-1;
			date = "2016-"+(i<10?("0"+i):i);
		} 
		return date;
	}
	
	public List<BusinessReport> findCountData(String productName, String startStr, String endStr, String searchFlag){
		String typeSql = "'%Y-%m-%d'";
		//查询2个时间节点
		if("1".equals(searchFlag)){
			typeSql="'%x-%v'";//按周查询
			startStr = getWeekStr(startStr);
			endStr = getWeekStr(endStr);
		}else if("2".equals(searchFlag)){
			typeSql="'%Y-%m'";//按月查询
		}
		List<BusinessReport> rs = Lists.newArrayList();
		List<Object> temp = businessReportDao.findBySql("SELECT DISTINCT ASIN FROM psi_sku a WHERE del_flag='0' AND a.`asin` IS NOT NULL AND   CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END)=:p1", new Parameter(productName));
		String params = "";
		StringBuffer buf= new StringBuffer();
		for (Object object : temp) {
			buf.append("'"+object.toString()+"',");
		}
		params=buf.toString();
		if (params.length() > 0) {
			String sql = "SELECT SUM(t.`sessions`),SUM(t.`conversion`),t.`country`,DATE_FORMAT(t.`data_date`,"+typeSql+") AS dates,SUM(ifnull(t.`orders_placed`,0))"+
					" FROM `amazoninfo_business_report` t "+
					" WHERE t.`del_flag`='0' AND DATE_FORMAT(t.`data_date`,"+typeSql+") >=:p1 AND DATE_FORMAT(t.`data_date`,"+typeSql+") <=:p2"+
					" AND t.`child_asin` IN ("+params.substring(0,params.length()-1)+")" +
					" GROUP BY t.`country`,dates ORDER BY dates DESC";
			List<Object[]> list = businessReportDao.findBySql(sql, new Parameter(startStr, endStr));
			Map<String, BusinessReport> totalMap = Maps.newHashMap();
			for (Object[] obj : list) {
				BusinessReport br = new BusinessReport();
				Integer session = obj[0]==null?0:((BigDecimal)obj[0]).intValue();
				br.setSessions(session);
				Integer ordersPlaced = obj[4]==null?0:((BigDecimal)obj[4]).intValue();
				br.setConversion(session==0?0:Float.parseFloat(String.format("%.2f", (float)ordersPlaced/session*100f)));
				br.setCountry(obj[2].toString());
				String date = obj[3].toString();
				if("1".equals(searchFlag)){
					date = DateUtils.getWeekStr(date, 5, "-", true);
				}
				br.setTitle(date);
				rs.add(br);
				BusinessReport total = totalMap.get(date);
				if (total == null) {
					total = new BusinessReport();
					totalMap.put(date, total);
					total.setCountry("total");
					total.setTitle(date);
					total.setSessions(0);
					total.setConversion(0f);
					total.setOrdersPlaced(0);
				}
				total.setSessions(total.getSessions() + session);
				total.setOrdersPlaced(total.getOrdersPlaced() + ordersPlaced);
			}
			for (Map.Entry<String,BusinessReport> entry : totalMap.entrySet()) {  
				BusinessReport total = entry.getValue();
				if (total.getSessions() != 0) {
					total.setConversion(Float.parseFloat(String.format("%.2f", (float)total.getOrdersPlaced()/total.getSessions()*100f)));
				}
				rs.add(total);
			}
			
		}
		return rs;
	}
	
	public List<BusinessReport> findImageAnalysisData(String country, String productName, String startStr, String endStr, String searchFlag){
		String typeSql = "'%Y-%m-%d'";
		//查询2个时间节点
		if("1".equals(searchFlag)){
			typeSql="'%x-%v'";//按周查询
			startStr = getWeekStr(startStr);
			endStr = getWeekStr(endStr);
		}else if("2".equals(searchFlag)){
			typeSql="'%Y-%m'";//按月查询
		}
		List<BusinessReport> rs = Lists.newArrayList();
		List<Object> temp = businessReportDao.findBySql("SELECT DISTINCT CONCAT(country,ASIN) FROM psi_sku a WHERE del_flag='0' AND a.country='"+country+"' AND a.`asin` IS NOT NULL AND   CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END)=:p1", new Parameter(productName));
		String params = "";
		StringBuffer buf= new StringBuffer();
		for (Object object : temp) {
			buf.append("'"+object.toString()+"',");
		}
		params = buf.toString();
		if (params.length() > 0) {
			String sql = "SELECT SUM(t.`sessions`),SUM(t.`conversion`),t.`country`,DATE_FORMAT(t.`data_date`,"+typeSql+") AS dates,SUM(ifnull(t.`orders_placed`,0))"+
					" FROM `amazoninfo_business_report` t "+
					" WHERE t.`del_flag`='0' AND t.`country`='"+country+"' AND DATE_FORMAT(t.`data_date`,"+typeSql+") >=:p1 AND DATE_FORMAT(t.`data_date`,"+typeSql+") <=:p2"+
					" AND CONCAT(t.`country`,t.`child_asin`) IN ("+params.substring(0,params.length()-1)+")" +
					" GROUP BY t.`country`,dates ORDER BY dates DESC";
			List<Object[]> list = businessReportDao.findBySql(sql, new Parameter(startStr, endStr));
			//Map<String, BusinessReport> totalMap = Maps.newHashMap();
			for (Object[] obj : list) {
				BusinessReport br = new BusinessReport();
				Integer session = obj[0]==null?0:((BigDecimal)obj[0]).intValue();
				br.setSessions(session);
				Integer ordersPlaced = obj[4]==null?0:((BigDecimal)obj[4]).intValue();
				br.setConversion(session==0?0:Float.parseFloat(String.format("%.2f", (float)ordersPlaced/session*100f)));
				br.setCountry(obj[2].toString());
				String date = obj[3].toString();
				if("1".equals(searchFlag)){
					date = DateUtils.getWeekStr(date, 5, "-", true);
				}
				br.setTitle(date);
				rs.add(br);
				/*BusinessReport total = totalMap.get(date);
				if (total == null) {
					total = new BusinessReport();
					totalMap.put(date, total);
					total.setCountry("total");
					total.setTitle(date);
					total.setSessions(0);
					total.setConversion(0f);
					total.setOrdersPlaced(0);
				}
				total.setSessions(total.getSessions() + session);
				total.setOrdersPlaced(total.getOrdersPlaced() + ordersPlaced);*/
			}
			/*for (String key : totalMap.keySet()) {
				BusinessReport total = totalMap.get(key);
				if (total.getSessions() != 0) {
					total.setConversion(Float.parseFloat(String.format("%.2f", (float)total.getOrdersPlaced()/total.getSessions()*100f)));
				}
				rs.add(total);
			}*/
		}
		return rs;
	}
	
	public Map<String,Map<String,Object[]>> findCountTypesData(String typeName,String searchFlag,String date1,String date2) throws ParseException{
		String typeSql = "'%Y-%m-%d'";
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = "";
		if(date1.split("-")[0].equals(date2.split("-")[0])){
			dateStr = "M/d";
		}else{
			dateStr = "yyyy/M/d";
		}
		//查询2个时间节点
		String type = "";
		if("1".equals(searchFlag)){
			typeSql="'%x-%v'";//按周查询
			format = new SimpleDateFormat("yyyy-ww");
			dateStr = "yyyy-ww";
			type = "周";
			if ("2016-01".equals(date1)) {
				date1 = "2015-53";
			}
			if ("2016-01".equals(date2)) {
				date2 = "2015-53";
			}
			date1 = getWeekStr(date1);
		}else if("2".equals(searchFlag)){
			typeSql="'%Y-%m'";//按月查询
			format = new SimpleDateFormat("yyyy-MM");
			dateStr = "yyyy-MM";
			type = "月";
		}
		String sql = "SELECT a.`country`,DATE_FORMAT(a.`data_date`,"+typeSql+") AS dates,SUM(a.`sessions`),SUM(ifnull(a.`orders_placed`,0))"+  
				" FROM amazoninfo_business_report a ,psi_product b ,(SELECT DISTINCT ASIN,country,product_id FROM psi_sku "+
				" WHERE del_flag='0') AS c WHERE  a.`child_asin` = c.`asin` AND b.`id` = c.`product_id`  AND a.`country`=c.`country`  "+
				" AND b.`TYPE`= :p1  AND DATE_FORMAT(a.`data_date`,"+typeSql+")>= :p2  AND DATE_FORMAT(a.`data_date`,"+typeSql+") <= :p3 " +
				" AND b.`del_flag`='0' GROUP BY a.`country`,dates";
		List<Object[]> list = businessReportDao.findBySql(sql, new Parameter(typeName,date1,date2));
		Map<String,Map<String,Object[]>> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			String country =HtmlUtils.htmlUnescape(objects[0].toString());
			String key = objects[1].toString();
			if("1".equals(searchFlag)){
				key = DateUtils.getWeekStr(key, 5, "-", true);
			}
			Date date = format.parse(key);
			String dateKey = "";
			if("1".equals(searchFlag)){
				dateKey = DateUtils.getWeekStr(date, format, 5, "-") + type;
			} else {
				dateKey = DateUtils.getDate(date,dateStr) + type;
			}
			Map<String, Object[]> tempMap = rs.get(country);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(country, tempMap);
			}
			tempMap.put(dateKey, objects);
		}
		return rs;
	}
	
	public Map<String,Map<String,Object[]>> findCountTypesDataByGroup(Set<String> typeName,String date1, String date2, String searchFlag) throws ParseException{
		String typeSql = "'%Y-%m-%d'";
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = "";
		if(date1.split("-")[0].equals(date2.split("-")[0])){
			dateStr = "M/d";
		}else{
			dateStr = "yyyy/M/d";
		}
		//查询2个时间节点
		String type = "";
		if("1".equals(searchFlag)){
			typeSql="'%x-%v'";//按周查询
			format = new SimpleDateFormat("yyyy-ww");
			dateStr = "yyyy-ww";
			type = "周";
			if ("2016-01".equals(date1)) {
				date1 = "2015-53";
			}
			if ("2016-01".equals(date2)) {
				date2 = "2015-53";
			}
			date1 = getWeekStr(date1);
		}else if("2".equals(searchFlag)){
			typeSql="'%Y-%m'";//按月查询
			format = new SimpleDateFormat("yyyy-MM");
			dateStr = "yyyy-MM";
			type = "月";
		}
		String sql = "SELECT a.`country`,DATE_FORMAT(a.`data_date`,"+typeSql+") AS dates,SUM(a.`sessions`),SUM(ifnull(a.`orders_placed`,0))"+  
		"	FROM amazoninfo_business_report a ,psi_product b ,(SELECT DISTINCT ASIN,country,product_id FROM psi_sku "+
		"	WHERE del_flag='0') AS c WHERE  a.`child_asin` = c.`asin` AND b.`id` = c.`product_id`  AND a.`country`=c.`country`  "+
		"	AND  b.`del_flag`='0' AND DATE_FORMAT(a.`data_date`,"+typeSql+")>= :p1  AND DATE_FORMAT(a.`data_date`,"+typeSql+") <= :p2   AND b.`TYPE` in :p3 GROUP BY a.`country`,dates";
		List<Object[]> list = businessReportDao.findBySql(sql, new Parameter(date1,date2,typeName));
		Map<String,Map<String,Object[]>> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			String country =HtmlUtils.htmlUnescape(objects[0].toString());
			String key = objects[1].toString();
			if("1".equals(searchFlag)){
				key = DateUtils.getWeekStr(key, 5, "-", true);
			}
			Date date = format.parse(key);
			String dateKey = "";
			if("1".equals(searchFlag)){
				dateKey = DateUtils.getWeekStr(date, format, 5, "-") + type;
			} else {
				dateKey = DateUtils.getDate(date,dateStr) + type;
			}
			
			Map<String, Object[]> tempMap = rs.get(country);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(country, tempMap);
			}
			tempMap.put(dateKey, objects);
		}
		return rs;
	}
	
	public Map<String,Map<String,BusinessReport>> findCountProductsData(List<String> asins,String country,Date startDate,Date endDate){
		return businessReportDao.findCountProductsData(asins,country,startDate,endDate);
	}
	
	public Map<String,Map<String,BusinessReport>> findCountProductsData(List<String> asins,String country,String date1,String date2,String searchFlag){
		String typeSql = "'%Y-%m-%d'";
		//查询2个时间节点
		if("1".equals(searchFlag)){
			typeSql="'%x-%v'";//按周查询
			if ("2016-01".equals(date1)) {
				date1 = "2015-53";
			}
			if ("2016-01".equals(date2)) {
				date2 = "2015-53";
			}
			date1 = getWeekStr(date1);
		}else if("2".equals(searchFlag)){
			typeSql="'%Y-%m'";//按月查询
		}
		Map<String,Map<String,BusinessReport>> rs = Maps.newHashMap();
		String sql = "SELECT SUM(a.`sessions`),SUM(ifnull(a.`orders_placed`,0)),a.`child_asin`,DATE_FORMAT(a.`data_date`,"+typeSql+") dates"+
				" FROM amazoninfo_business_report a  WHERE a.`del_flag`='0' AND a.`country`=:p1 AND a.`child_asin` " +
				"IN :p2 AND DATE_FORMAT(a.`data_date`,"+typeSql+")>= :p3"+
				" AND DATE_FORMAT(a.`data_date`,"+typeSql+") <= :p4 GROUP BY a.`child_asin`,dates ORDER BY dates DESC";
		List<Object[]> list = businessReportDao.findBySql(sql, new Parameter(country,asins,date1,date2));
		for (Object[] obj : list) {
			BusinessReport br = new BusinessReport();
			br.setSessions(obj[0]==null?0:Integer.parseInt(obj[0].toString()));
			br.setOrdersPlaced(obj[1]==null?0:Integer.parseInt(obj[1].toString()));
			String dateKey = obj[3].toString();
			if("1".equals(searchFlag)){
				dateKey = DateUtils.getWeekStr(dateKey, 5, "-", true);
			}
			br.setTitle(dateKey);	//设置日期字符串（日、周、月）
			String asin = obj[2].toString();
			br.setChildAsin(asin);
			Map<String,BusinessReport> data = rs.get(dateKey);
			if(data==null){
				data = Maps.newHashMap();
				rs.put(dateKey,data);
			}
			data.put(asin,br);
		}
		return rs;
		
	}
	
	public Map<String,Map<String,BusinessReport>> findCountProductsData(Set<String> asins,Date startDate,Date endDate){
		return businessReportDao.findCountProductsData(asins,startDate,endDate);
	}
	
	
	public Map<String,Map<String,Integer>> getAdsQuantity(Set<String> skus, SaleReport saleReport){
		Map<String,Map<String,Integer>> rs=Maps.newLinkedHashMap();
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		
		String sql="SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks) FROM "+
					 " (SELECT a.country, a.`data_date` ,a.clicks "+
					" FROM amazoninfo_advertising a  "+
					" WHERE a.sku IN :p1 AND a.`data_date`>=:p2 AND a.`data_date`<=:p3  AND a.country=:p4  "+
					" UNION SELECT a.country, a.`data_date`,a.clicks "+
					" FROM amazoninfo_advertising_report a  "+
					" WHERE a.advertised_sku IN :p1 AND a.`data_date`>=:p2 AND a.`data_date`<=:p3  AND a.country=:p4  "+
					" ) a GROUP BY a.`country`,dates ";
		
		/*String sql=" SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks)  FROM amazoninfo_advertising a "+
        " where a.sku in :p1 and a.`data_date`>=:p2 and a.`data_date`<=:p3  and a.country=:p4 "+
        " GROUP BY a.`country`,dates  ";*/
		
		List<Object[]> list = businessReportDao.findBySql(sql, new Parameter(skus,start,end,saleReport.getCountry()));
	
		for (Object[] obj : list) {
			String country = obj[0].toString();
			String date = obj[1].toString();
			Integer ads = (obj[2]==null?0:Integer.parseInt(obj[2].toString()));
			
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Map<String,Integer> countrys = rs.get(country);
			if(countrys==null){
				countrys = Maps.newLinkedHashMap();
				rs.put(country, countrys);
			}
			countrys.put(date, ads);
		}
		return rs;
	}
	
	public Map<String,Map<String,Integer>> getAmsQuantity(String productName, SaleReport saleReport){
		Map<String,Map<String,Integer>> rs=Maps.newLinkedHashMap();
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		
		String sql=" SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks)  FROM amazoninfo_aws_adversting a "+
        " where a.`data_date`>=:p1 and a.`data_date`<=:p2 and a.product_name = :p3 and country=:p4 "+
        " GROUP BY a.`country`,dates  ";
		List<Object[]> list = businessReportDao.findBySql(sql, new Parameter(start,end,productName,saleReport.getCountry()));
	
		for (Object[] obj : list) {
			String country = obj[0].toString();
			String date = obj[1].toString();
			Integer ads = (obj[2]==null?0:Integer.parseInt(obj[2].toString()));
			
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Map<String,Integer> countrys = rs.get(country);
			if(countrys==null){
				countrys = Maps.newLinkedHashMap();
				rs.put(country, countrys);
			}
			countrys.put(date, ads);
		}
		return rs;
	}
	
	// 国家[日期/数据]
	public Map<String, Map<String, BusinessReport>> findCountProductsData(Set<String> asins, SaleReport saleReport){
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		List<Object[]> list = null;
		String sql = "SELECT SUM(a.`sessions`),SUM(ifnull(a.`orders_placed`,0)),DATE_FORMAT(a.`data_date`,"+typeSql+") dates, a.`country`,SUM(a.`b2b_orders_placed`)"+
				" FROM amazoninfo_business_report a WHERE a.`del_flag`='0' AND a.`child_asin` " +
				"IN :p1 AND a.`data_date`>= :p2 AND a.`data_date` <= :p3 and country=:p4 GROUP BY a.`country`,dates ORDER BY dates DESC";
		list = businessReportDao.findBySql(sql, new Parameter(asins,start,end,saleReport.getCountry()));
		Map<String, Map<String,BusinessReport>> rs = Maps.newHashMap();
		for (Object[] obj : list) {
			Integer sessions = obj[0]==null?0:((BigDecimal)obj[0]).intValue();
			Integer ordersPlaced = obj[1]==null?0:((BigDecimal)obj[1]).intValue();
			String date = obj[2].toString();
			String country = obj[3].toString();
			Integer b2bOrdersPlaced = obj[4]==null?0:((BigDecimal)obj[4]).intValue();
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Map<String,BusinessReport> countrys = rs.get(country);
			if(countrys==null){
				countrys = Maps.newLinkedHashMap();
				rs.put(country, countrys);
			}
			BusinessReport br = countrys.get(date);
			if(br==null){
				br = new BusinessReport();
				br.setSessions(0);
				br.setOrdersPlaced(0);
				br.setCountry(country);
				br.setB2bOrdersPlaced(0);
				countrys.put(date, br);
			}
			if(sessions!=null){
				br.setSessions(sessions+br.getSessions());
			}
			if(ordersPlaced!=null){
				br.setOrdersPlaced(ordersPlaced+br.getOrdersPlaced());
			}
			if(b2bOrdersPlaced!=null){
				br.setB2bOrdersPlaced(b2bOrdersPlaced+br.getB2bOrdersPlaced());
			}
		}
		return rs;
	}
	
	
	public Page<BusinessReport> findByDate(Page<BusinessReport> page, BusinessReport businessReport,String searchFlag,String groupName) throws NumberFormatException, ParseException {
		DetachedCriteria dc = businessReportDao.createDetachedCriteria();
		
		if (StringUtils.isNotEmpty(businessReport.getCountry())){
			dc.add(Restrictions.eq("country", businessReport.getCountry()));
		}
		
		if(searchFlag.equals("0")||searchFlag.equals("")){
			searchFlag="0";
			if (businessReport.getCreateDate()!=null){
				dc.add(Restrictions.ge("dataDate",businessReport.getCreateDate()));
			}
			if (businessReport.getDataDate()!=null){
				dc.add(Restrictions.le("dataDate",businessReport.getDataDate()));
			}
		}else if (searchFlag.equals("1")){
			
			Date beforeDate=null;
			Date afterDate=null;
			
			if (businessReport.getCreateDate()!=null){
				beforeDate =DateUtils.getMonday(businessReport.getCreateDate());
				dc.add(Restrictions.ge("dataDate",beforeDate));
			}
			if (businessReport.getDataDate()!=null){
			    afterDate =DateUtils.getSunday(businessReport.getDataDate());
				dc.add(Restrictions.le("dataDate",afterDate));
			}

		}else if (searchFlag.equals("2")){
			Date beforeDate=null;
			Date afterDate=null;
			if (businessReport.getCreateDate()!=null){
				beforeDate =DateUtils.getFirstDayOfMonth(businessReport.getCreateDate());
				dc.add(Restrictions.ge("dataDate",beforeDate));
			}
			if (businessReport.getDataDate()!=null){
				afterDate =DateUtils.getLastDayOfMonth(businessReport.getDataDate());
				dc.add(Restrictions.le("dataDate",afterDate));
			}
		}
		if(StringUtils.isNotBlank(groupName)){
			String asinSql="SELECT DISTINCT s.asin FROM psi_product d "+
					" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type'  "+
					" JOIN psi_product_type_group g ON t.id=g.`dict_id`  "+
					" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0'  "+
					" JOIN psi_sku s ON s.`product_name`=CONCAT(d.brand,' ',d.model) AND s.del_flag='0' "+
					" WHERE p.id=:p1 AND ASIN IS NOT NULL  ";
			List<String> list = businessReportDao.findBySql(asinSql, new Parameter(groupName));
			if(list!=null&&list.size()>0){
				dc.add(Restrictions.in("childAsin",list));
			}else{
				dc.add(Restrictions.eq("childAsin",groupName));
			}
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		return  businessReportDao.findExtByDay(page, dc,searchFlag);
	}
	
	public Page<BusinessReport> findProductByDate(Page<BusinessReport> page, BusinessReport businessReport) throws NumberFormatException, ParseException {
		DetachedCriteria dc = businessReportDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(businessReport.getChildAsin())){
			dc.add(Restrictions.eq("childAsin",businessReport.getChildAsin()));
		}
		if (StringUtils.isNotEmpty(businessReport.getCountry())){
			dc.add(Restrictions.eq("country", businessReport.getCountry()));
		}
		String searchFlag = businessReport.getSearchFlag()==null?"":businessReport.getSearchFlag();
		if(searchFlag.equals("0")||searchFlag.equals("")){
			searchFlag="0";
			if (businessReport.getCreateDate()!=null){
				dc.add(Restrictions.ge("dataDate",businessReport.getCreateDate()));
			}
			if (businessReport.getDataDate()!=null){
				dc.add(Restrictions.le("dataDate",businessReport.getDataDate()));
			}
		}else if (searchFlag.equals("1")){
			
			Date beforeDate=null;
			Date afterDate=null;
			
			if (businessReport.getCreateDate()!=null){
				beforeDate =DateUtils.getMonday(businessReport.getCreateDate());
				dc.add(Restrictions.ge("dataDate",beforeDate));
			}
			if (businessReport.getDataDate()!=null){
			    afterDate =DateUtils.getSunday(businessReport.getDataDate());
				dc.add(Restrictions.le("dataDate",afterDate));
			}

		}else if (searchFlag.equals("2")){
			Date beforeDate=null;
			Date afterDate=null;
			if (businessReport.getCreateDate()!=null){
				beforeDate =DateUtils.getFirstDayOfMonth(businessReport.getCreateDate());
				dc.add(Restrictions.ge("dataDate",beforeDate));
			}
			if (businessReport.getDataDate()!=null){
				afterDate =DateUtils.getLastDayOfMonth(businessReport.getDataDate());
				dc.add(Restrictions.le("dataDate",afterDate));
			}
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		return  businessReportDao.findExtByDay(page, dc,searchFlag);
	}
	
	@Transactional(readOnly = false)
	public void save(BusinessReport businessReport) {
		businessReportDao.save(businessReport);
	}
	
	@Transactional(readOnly = false)
	public void saveAll(Collection<BusinessReport> businessReports) {
		businessReports.remove(null);
		businessReportDao.save(new ArrayList<BusinessReport>(businessReports));
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		businessReportDao.deleteById(id);
	}

	public Map<String,Map<String, BusinessReport>> totalSessions(BusinessReport businessReport, String startStr, String endStr,String groupName) {
		String typeSql = "'%Y-%m-%d'";
		//查询2个时间节点
		if("1".equals(businessReport.getSearchFlag())){
			typeSql="'%x-%v'";//按周查询
			if ("2016-01".equals(startStr)) {
				startStr = "2015-53";
			} else if(startStr.startsWith("2016")){
				Integer i = Integer.parseInt(startStr.substring(5));
				i = i-1;
				startStr = "2016-"+(i<10?("0"+i):i);
			}
			if ("2016-01".equals(endStr)) {
				endStr = "2015-53";
			} else if(endStr.startsWith("2016")){
				Integer i = Integer.parseInt(endStr.substring(5));
				i = i-1;
				endStr = "2016-"+(i<10?("0"+i):i);
			}
		}else if("2".equals(businessReport.getSearchFlag())){
			typeSql="'%Y-%m'";//按月查询
		}
		String sql = "SELECT t.`country`,SUM(t.`sessions`),SUM(ifnull(t.`orders_placed`,0)),DATE_FORMAT(t.`data_date`,"+typeSql+") dates FROM `amazoninfo_business_report` t ";
		sql+=" WHERE (DATE_FORMAT(t.`data_date`,"+typeSql+") = :p1 or DATE_FORMAT(t.`data_date`,"+typeSql+") = :p2) AND t.`del_flag`='0' ";
		Parameter param=null;
		if(StringUtils.isNotBlank(groupName)){
			String asinSql="SELECT DISTINCT s.asin FROM psi_product d "+
					" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type'  "+
					" JOIN psi_product_type_group g ON t.id=g.`dict_id`  "+
					" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0'  "+
					" JOIN psi_sku s ON s.`product_name`=CONCAT(d.brand,' ',d.model) AND s.del_flag='0' "+
					" WHERE p.id=:p1 AND ASIN IS NOT NULL  ";
			List<String> list = businessReportDao.findBySql(asinSql, new Parameter(groupName));
			if(list!=null&&list.size()>0){
				sql+=" and child_asin in :p3 ";
				param= new Parameter(startStr, endStr,list);
			}else{
				sql+=" and child_asin=:p3 ";
				param= new Parameter(startStr, endStr,groupName);
			}
		}else{
			param= new Parameter(startStr, endStr);
		}
		sql+=" GROUP BY t.`country`,dates ";
		List<Object[]> list = businessReportDao.findBySql(sql,param);
		
		Map<String,Map<String, BusinessReport>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			int sessionsVolume = ((BigDecimal)objs[1]).intValue();
			int ordersPlacedVolume = ((BigDecimal)objs[2]).intValue();
			String date = objs[3].toString();
			if("1".equals(businessReport.getSearchFlag())){
				date = DateUtils.getWeekStr(date, 5, "-", true);
			}
			float conversion = sessionsVolume==0?0f:((BigDecimal)objs[2]).floatValue()/((BigDecimal)objs[1]).floatValue()*100f;
			Map<String, BusinessReport> datas = rs.get(country);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(country, datas);
			}
			datas.put(date, new BusinessReport(country, null, null, null, null, null, sessionsVolume, 
					null, null, null, null, null, null, null, ordersPlacedVolume, conversion, null, null, null, null, null));
			
			Map<String, BusinessReport> datas1 = rs.get("total");
			if(datas1==null){
				datas1 = Maps.newLinkedHashMap();
				rs.put("total", datas1);
			}
			BusinessReport saleEntry = datas1.get(date);
			if(saleEntry==null){
				saleEntry = new BusinessReport(null, null, null, null, null, null, 0, 
						null, null, null, null, null, null, null, 0, 0f, null, null, null, null, null);
				datas1.put(date, saleEntry);
			}
			saleEntry.setSessions(sessionsVolume + saleEntry.getSessions());
			saleEntry.setOrdersPlaced(ordersPlacedVolume + saleEntry.getOrdersPlaced());
			saleEntry.setConversion(conversion + saleEntry.getConversion());
		}
		return rs;
	}
	
	public Map<String,Map<String,Integer>> getAdsQuantity(BusinessReport businessReport,Date start,Date end,String productName,String groupName){
		Map<String,Map<String,Integer>> rs=Maps.newLinkedHashMap();
		String typeSql = "'%Y-%m-%d'";
		//查询2个时间节点
		if("1".equals(businessReport.getSearchFlag())){
			typeSql="'%x-%v'";//按周查询
		}else if("2".equals(businessReport.getSearchFlag())){
			typeSql="'%Y-%m'";//按月查询
		}
		String sql=" SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks)  FROM amazoninfo_advertising a ";
		if(StringUtils.isNotBlank(productName)){
			sql+=" JOIN psi_sku b ON a.sku=b.sku AND a.`country`=b.`country` AND b.`del_flag`='0' ";
		}
        sql+=" where a.`data_date`>=:p1 and a.`data_date`<=:p2 ";
        if(StringUtils.isNotBlank(productName)){
			sql+=" and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END)='"+productName+"'  ";
		}
        Parameter param=null;
        if(StringUtils.isNotBlank(groupName)){
			String skuSql="SELECT DISTINCT s.sku FROM psi_product d "+
					" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type'  "+
					" JOIN psi_product_type_group g ON t.id=g.`dict_id`  "+
					" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0'  "+
					" JOIN psi_sku s ON s.`product_name`=CONCAT(d.brand,' ',d.model) AND s.del_flag='0' "+
					" WHERE p.id=:p1 AND ASIN IS NOT NULL  ";
			List<String> list = businessReportDao.findBySql(skuSql, new Parameter(groupName));
			if(list!=null&&list.size()>0){
				sql+=" and sku in :p3 ";
				param=new Parameter(start,end,list);
			}else{
				sql+=" and sku = :p3 ";
				param=new Parameter(start,end,groupName);
			}
		}else{
			param=new Parameter(start,end);
		}
        sql+=" GROUP BY a.`country`,dates  ";
        
		List<Object[]> list = businessReportDao.findBySql(sql,param);
		for (Object[] obj: list) {
			String date = obj[1].toString(); 
			if("1".equals(businessReport.getSearchFlag())){
				Integer i = Integer.parseInt(date.substring(5));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"-01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016-"+(i<10?("0"+i):i);
				}
			}
			Map<String,Integer> temp=rs.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				rs.put(obj[0].toString(), temp);
			}
			temp.put(date,Integer.parseInt(obj[2].toString()));
			
			Map<String,Integer> totalTemp=rs.get("total");
			if(totalTemp==null){
				totalTemp=Maps.newLinkedHashMap();
				rs.put("total",totalTemp);
			}
			totalTemp.put(obj[0].toString(),(totalTemp.get(obj[0].toString())==null?0:totalTemp.get(obj[0].toString()))+Integer.parseInt(obj[2].toString()));
		}
		
		DateFormat df=new SimpleDateFormat("yyyyMMdd");
		
	
			sql=" SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks)  FROM amazoninfo_advertising_report a ";
			if(StringUtils.isNotBlank(productName)){
				sql+=" JOIN psi_sku b ON a.advertised_sku=b.sku AND a.`country`=b.`country` AND b.`del_flag`='0' ";
			}
	        sql+=" where a.`data_date`>=:p1 and a.`data_date`<=:p2 ";
	        if(StringUtils.isNotBlank(productName)){
				sql+=" and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END)='"+productName+"'  ";
			}
	      
	        if(StringUtils.isNotBlank(groupName)){
				String skuSql="SELECT DISTINCT s.sku FROM psi_product d "+
						" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type'  "+
						" JOIN psi_product_type_group g ON t.id=g.`dict_id`  "+
						" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0'  "+
						" JOIN psi_sku s ON s.`product_name`=CONCAT(d.brand,' ',d.model) AND s.del_flag='0' "+
						" WHERE p.id=:p1 AND ASIN IS NOT NULL  ";
				list = businessReportDao.findBySql(skuSql, new Parameter(groupName));
				if(list!=null&&list.size()>0){
					sql+=" and sku in :p3 ";
					param=new Parameter(start,end,list);
				}else{
					sql+=" and sku = :p3 ";
					param=new Parameter(start,end,groupName);
				}
			}else{
				param=new Parameter(start,end);
			}
	        sql+=" GROUP BY a.`country`,dates  ";
	        
			list = businessReportDao.findBySql(sql,param);
			for (Object[] obj: list) {
				String date = obj[1].toString(); 
				if("1".equals(businessReport.getSearchFlag())){
					Integer i = Integer.parseInt(date.substring(5));
					if(i==53){
						Integer year = Integer.parseInt(date.substring(0,4));
						date =  (year+1)+"-01";
					}else if (date.contains("2016")){
						i = i+1;
						date = "2016-"+(i<10?("0"+i):i);
					}
				}
				Map<String,Integer> temp=rs.get(obj[0].toString());
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					rs.put(obj[0].toString(), temp);
				}
				temp.put(date,Integer.parseInt(obj[2].toString()));
				
				Map<String,Integer> totalTemp=rs.get("total");
				if(totalTemp==null){
					totalTemp=Maps.newLinkedHashMap();
					rs.put("total",totalTemp);
				}
				totalTemp.put(obj[0].toString(),(totalTemp.get(obj[0].toString())==null?0:totalTemp.get(obj[0].toString()))+Integer.parseInt(obj[2].toString()));
			}
		
		return rs;
	}
	
	public Map<String,Map<String,Integer>> getAdsQuantityByName(String type,String start,String end,Set<String> productNameSet){
		Map<String,Map<String,Integer>> rs=Maps.newLinkedHashMap();
		String typeSql = "'%Y-%m-%d'";
		//查询2个时间节点
		if("1".equals(type)){
			typeSql="'%x-%v'";//按周查询
			start = getWeekStr(start);
		}else if("2".equals(type)){
			typeSql="'%Y-%m'";//按月查询
		}
		List<Object[]> list =Lists.newArrayList();
		if(productNameSet!=null){
			List<Object> temp = businessReportDao.findBySql("SELECT DISTINCT sku FROM psi_sku a WHERE del_flag='0' AND a.`sku` IS NOT NULL AND   CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) in :p1", new Parameter(productNameSet));
			String params = "";
			StringBuffer buf= new StringBuffer();
			for (Object object : temp) {
				buf.append("'"+object.toString()+"',");
			}
			params=buf.toString();
			if (params.length() > 0) {
				/*String sql=" SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks)  FROM amazoninfo_advertising a "+
		           " where DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2 "+
		           " AND sku IN ("+params.substring(0,params.length()-1)+")" +
		           " GROUP BY a.`country`,dates  ";*/
				
				String sql="SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks) FROM "+
							 " (SELECT a.country, a.`data_date` ,a.clicks "+
							" FROM amazoninfo_advertising a  "+
							" WHERE  DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 AND  DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2  AND sku IN ("+params.substring(0,params.length()-1)+")" +
							" UNION SELECT a.country, a.`data_date`,a.clicks "+
							" FROM amazoninfo_advertising_report a  "+
							" WHERE  DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 AND  DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2  and  a.advertised_sku IN ("+params.substring(0,params.length()-1)+")" +
							" ) a GROUP BY a.`country`,dates ";
				
				list=businessReportDao.findBySql(sql, new Parameter(start,end));
			}
		}else{
			String sql="SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks) FROM "+
						 " (SELECT a.country, a.`data_date` ,a.clicks "+
						" FROM amazoninfo_advertising a  "+
						" WHERE DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2  "+
						" UNION SELECT a.country, a.`data_date`,a.clicks "+
						" FROM amazoninfo_advertising_report a  "+
						" WHERE DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2 "+
						" ) a GROUP BY a.`country`,dates ";
			list = businessReportDao.findBySql(sql, new Parameter(start,end));
		}
		/*String sql=" SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks)  FROM amazoninfo_advertising a ";
		if(productNameSet!=null){
			sql+=" JOIN psi_sku b ON a.sku=b.sku AND a.`country`=b.`country` AND b.`del_flag`='0' ";
		}
        sql+=" where DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2 ";
        if(productNameSet!=null){
			sql+=" and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) in :p3  ";
		}
        sql+=" GROUP BY a.`country`,dates  ";
		List<Object[]> list =Lists.newArrayList();
		if(productNameSet!=null){
			list = businessReportDao.findBySql(sql, new Parameter(start,end,productNameSet));
		}else{
			list = businessReportDao.findBySql(sql, new Parameter(start,end));
		}		
		*/
		
		
		for (Object[] obj: list) {
			String date = obj[1].toString(); 
			if("1".equals(type)){
				date = DateUtils.getWeekStr(date, 5, "-", true);
			}
			Map<String,Integer> temp=rs.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				rs.put(obj[0].toString(), temp);
			}
			temp.put(date,Integer.parseInt(obj[2].toString()));
			
			Map<String,Integer> totalTemp=rs.get("total");
			if(totalTemp==null){
				totalTemp=Maps.newLinkedHashMap();
				rs.put("total",totalTemp);
			}
			totalTemp.put(date,(totalTemp.get(date)==null?0:totalTemp.get(date))+Integer.parseInt(obj[2].toString()));
		}
		return rs;
	}
	
	public Map<String,Map<String,Advertising>> getAdsByName(String country, String type,String start,String end,Set<String> productNameSet){
		Map<String,Map<String,Advertising>> rs=Maps.newLinkedHashMap();
		String typeSql = "'%Y-%m-%d'";
		//查询2个时间节点
		if("1".equals(type)){
			typeSql="'%x-%v'";//按周查询
			start = getWeekStr(start);
		}else if("2".equals(type)){
			typeSql="'%Y-%m'";//按月查询
		}
		String sql=" SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks),SUM(a.`same_sku_orders_placed`+a.`other_sku_orders_placed`),SUM(a.impressions)  FROM amazoninfo_advertising a "+
			" JOIN psi_sku b ON a.sku=b.sku AND a.`country`=b.`country` AND b.`del_flag`='0' "+
	        " where a.`data_date`>=:p1 and a.`data_date`<=:p2 "+
			" and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) in :p3 AND a.country=:p4 "+
	        " GROUP BY dates  ";
		List<Object[]> list = businessReportDao.findBySql(sql, new Parameter(start,end,productNameSet,country));
		for (Object[] obj: list) {
			Map<String,Advertising> temp=rs.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				rs.put(obj[0].toString(), temp);
			}
			String date = obj[1].toString(); 
			if("1".equals(type)){
				date = DateUtils.getWeekStr(date, 5, "-", true);
			}
			
			Advertising advertising = new Advertising();
			Integer clicks = Integer.parseInt(obj[2].toString());
			Integer ordersPlaced = Integer.parseInt(obj[3].toString());
			Integer impressions = Integer.parseInt(obj[4].toString());
			advertising.setClicks(clicks);
			advertising.setSameSkuOrdersPlaced(ordersPlaced);
			advertising.setImpressions(impressions);
			advertising.setConversion(0f);
			advertising.setOnePageBid(0f);	//点击率
			if (clicks > 0) {
				float conversion = ordersPlaced*100/(float)clicks;
				advertising.setConversion(Math.round(conversion*100)/100f);
			}
			if (impressions > 0) {
				float conversion = clicks*100/(float)impressions;
				advertising.setOnePageBid(Math.round(conversion*100)/100f);
			}
			temp.put(date, advertising);
			
		}
		return rs;
	}
	
	
	public Map<String,Map<String,Integer>> getAdsQuantityByCountry(String type,String start,String end,String country,Set<String> productNameSet){
		Map<String,Map<String,Integer>> rs=Maps.newLinkedHashMap();
		String typeSql = "'%Y-%m-%d'";
		//查询2个时间节点
		if("1".equals(type)){
			typeSql="'%x-%v'";//按周查询
			start = getWeekStr(start);
		}else if("2".equals(type)){
			typeSql="'%Y-%m'";//按月查询
		}
		String sql=" SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) pname,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks)  FROM amazoninfo_advertising a ";
		if(productNameSet!=null){
			sql+=" JOIN psi_sku b ON a.sku=b.sku AND a.`country`=b.`country` AND b.`del_flag`='0' ";
		}
        sql+=" where DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2 and a.country=:p3 ";
        if(productNameSet!=null){
			sql+=" and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) in :p4  ";
		}
        sql+=" GROUP BY pname,dates  ";
		List<Object[]> list =Lists.newArrayList();
		if(productNameSet!=null){
			list = businessReportDao.findBySql(sql, new Parameter(start,end,country,productNameSet));
		}else{
			list = businessReportDao.findBySql(sql, new Parameter(start,end,country));
		}		
		
		for (Object[] obj: list) {
			String date = obj[1].toString(); 
			if("1".equals(type)){
				date = DateUtils.getWeekStr(date, 5, "-", true);
			}
			Map<String,Integer> temp=rs.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				rs.put(obj[0].toString(), temp);
			}
			temp.put(date,Integer.parseInt(obj[2].toString()));
			
			Map<String,Integer> totalTemp=rs.get("total");
			if(totalTemp==null){
				totalTemp=Maps.newLinkedHashMap();
				rs.put("total",totalTemp);
			}
			totalTemp.put(date,(totalTemp.get(date)==null?0:totalTemp.get(date))+Integer.parseInt(obj[2].toString()));
		}
		
			sql=" SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) pname,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks)  FROM amazoninfo_advertising_report a ";
			if(productNameSet!=null){
				sql+=" JOIN psi_sku b ON a.advertised_sku=b.sku AND a.`country`=b.`country` AND b.`del_flag`='0' ";
			}
	        sql+=" where DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2 and a.country=:p3 ";
	        if(productNameSet!=null){
				sql+=" and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) in :p4  ";
			}
	        sql+=" GROUP BY pname,dates  ";
			list =Lists.newArrayList();
			if(productNameSet!=null){
				list = businessReportDao.findBySql(sql, new Parameter(start,end,country,productNameSet));
			}else{
				list = businessReportDao.findBySql(sql, new Parameter(start,end,country));
			}		
			
			for (Object[] obj: list) {
				String date = obj[1].toString(); 
				if("1".equals(type)){
					date = DateUtils.getWeekStr(date, 5, "-", true);
				}
				Map<String,Integer> temp=rs.get(obj[0].toString());
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					rs.put(obj[0].toString(), temp);
				}
				temp.put(date,Integer.parseInt(obj[2].toString()));
				
				Map<String,Integer> totalTemp=rs.get("total");
				if(totalTemp==null){
					totalTemp=Maps.newLinkedHashMap();
					rs.put("total",totalTemp);
				}
				totalTemp.put(date,(totalTemp.get(date)==null?0:totalTemp.get(date))+Integer.parseInt(obj[2].toString()));
			}
		
		return rs;
	}
	
	public Map<String,Map<String,Integer>> getAdsQuantityByCountryType(String type,String start,String end,String country,Set<String> typeSet){
		Map<String,Map<String,Integer>> rs=Maps.newLinkedHashMap();
		String typeSql = "'%Y-%m-%d'";
		//查询2个时间节点
		if("1".equals(type)){
			typeSql="'%x-%v'";//按周查询
			start = getWeekStr(start);
		}else if("2".equals(type)){
			typeSql="'%Y-%m'";//按月查询
		}
		//String sql=" SELECT p.type,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks)  FROM amazoninfo_advertising a ";
	/*	-String sql=" SELECT p.type,a.dates ,SUM(a.clicks) "+
				" FROM (SELECT t.country, DATE_FORMAT(t.`data_date`, "+typeSql+") AS dates, SUM(t.clicks) AS clicks,t.sku FROM amazoninfo_advertising t"+
				" WHERE DATE_FORMAT(t.`data_date`,"+typeSql+")>=:p1"+
				" AND DATE_FORMAT(t.`data_date`,"+typeSql+")<=:p2 and t.country=:p3 GROUP BY t.sku,dates) AS a ";*/
		String sql=" SELECT p.type,a.dates ,SUM(a.clicks)  " +
				" FROM (SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks) clicks,sku FROM  "+
				 " (SELECT a.country, a.`data_date` ,a.clicks,a.sku "+
					" FROM amazoninfo_advertising a  "+
					" WHERE DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2 and a.country=:p3  "+
					" UNION SELECT a.country, a.`data_date`,a.clicks,a.advertised_sku sku "+
					" FROM amazoninfo_advertising_report a  "+
					" WHERE DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2 and a.country=:p3 "+
					" ) a GROUP BY a.`country`,dates,sku ) AS a ";
		if(typeSet!=null){
			sql+=" JOIN psi_sku b ON a.sku=b.sku AND a.`country`=b.`country` AND b.`del_flag`='0' ";
			sql+=" join psi_product p on concat(p.brand,' ',model)=b.`product_name`  and p.del_flag='0' ";
		}
        //sql+=" where DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2 and a.country=:p3 ";
        if(typeSet!=null){
        	//sql+=" and p.type in :p4  ";
        	sql+=" where p.type in :p4  ";
		}
        sql+=" GROUP BY p.type,dates  ";
		List<Object[]> list =Lists.newArrayList();
		if(typeSet!=null){
			list = businessReportDao.findBySql(sql, new Parameter(start,end,country,typeSet));
		}else{
			list = businessReportDao.findBySql(sql, new Parameter(start,end,country));
		}		
		
		for (Object[] obj: list) {
			String date = obj[1].toString(); 
			if("1".equals(type)){
				date = DateUtils.getWeekStr(date, 5, "-", true);
			}
			Map<String,Integer> temp=rs.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				rs.put(obj[0].toString(), temp);
			}
			temp.put(date,Integer.parseInt(obj[2].toString()));
			
			Map<String,Integer> totalTemp=rs.get("total");
			if(totalTemp==null){
				totalTemp=Maps.newLinkedHashMap();
				rs.put("total",totalTemp);
			}
			totalTemp.put(date,(totalTemp.get(date)==null?0:totalTemp.get(date))+Integer.parseInt(obj[2].toString()));
		}
		return rs;
	}
	
	
	public Map<String,Map<String,Integer>> getAdsQuantity(String type,String start,String end,Set<String> typeSet){
		Map<String,Map<String,Integer>> rs=Maps.newLinkedHashMap();
		String typeSql = "'%Y-%m-%d'";
		//查询2个时间节点
		if("1".equals(type)){
			typeSql="'%x-%v'";//按周查询
			start = getWeekStr(start);
		}else if("2".equals(type)){
			typeSql="'%Y-%m'";//按月查询
		}
		//String sql=" SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks)  FROM amazoninfo_advertising a ";
		/*String sql=" SELECT a.country,a.dates ,SUM(a.clicks)  " +
				" FROM (SELECT t.country, DATE_FORMAT(t.`data_date`, "+typeSql+") AS dates, SUM(t.clicks) AS clicks,t.sku FROM amazoninfo_advertising t"+
				" WHERE DATE_FORMAT(t.`data_date`,"+typeSql+")>=:p1"+
				" AND DATE_FORMAT(t.`data_date`,"+typeSql+")<=:p2 GROUP BY t.sku,dates) AS a ";
		*/
		String sql=" SELECT a.country,a.dates ,SUM(a.clicks)  " +
				" FROM (SELECT a.country,DATE_FORMAT(a.`data_date`,"+typeSql+") dates ,SUM(a.clicks) clicks,sku FROM  "+
				 " (SELECT a.country, a.`data_date` ,a.clicks,a.sku "+
					" FROM amazoninfo_advertising a  "+
					" WHERE DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2  "+
					" UNION SELECT a.country, a.`data_date`,a.clicks,a.advertised_sku sku "+
					" FROM amazoninfo_advertising_report a  "+
					" WHERE DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2 "+
					" ) a GROUP BY a.`country`,dates,sku ) AS a ";
		if(typeSet!=null){
			sql+=" JOIN psi_sku b ON a.sku=b.sku AND a.`country`=b.`country` AND b.`del_flag`='0' ";
		    sql+=" join psi_product p on concat(p.brand,' ',model)=b.`product_name`  and p.del_flag='0' ";
		}
        //sql+=" where DATE_FORMAT(a.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(a.`data_date`,"+typeSql+")<=:p2 ";
        if(typeSet!=null){
			//sql+=" and p.type in :p3  ";
			sql+=" where p.type in :p3  ";
		}
        sql+=" GROUP BY a.`country`,dates  ";
		List<Object[]> list = Lists.newArrayList();
		if(typeSet!=null){
			list = businessReportDao.findBySql(sql, new Parameter(start,end,typeSet));
		}else{
			list = businessReportDao.findBySql(sql, new Parameter(start,end));
		}
		for (Object[] obj: list) {
			String date = obj[1].toString(); 
			if("1".equals(type)){
				date = DateUtils.getWeekStr(date, 5, "-", true);
			}
			Map<String,Integer> temp=rs.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				rs.put(obj[0].toString(), temp);
			}
			temp.put(date,Integer.parseInt(obj[2].toString()));
			
			Map<String,Integer> totalTemp=rs.get("total");
			if(totalTemp==null){
				totalTemp=Maps.newLinkedHashMap();
				rs.put("total",totalTemp);
			}
			totalTemp.put(date,(totalTemp.get(date)==null?0:totalTemp.get(date))+Integer.parseInt(obj[2].toString()));
		}
		return rs;
	}
	
	/***
	 * 
	 * sessin,转化率（0-3天，4-6天比较）下降50%提醒 start   michael 2016-09-18
	 */ 
	public void sendSessionWarnEmail(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateStar = getMaxDate();
		int week1 =DateUtils.getWeekOfYear(dateStar);
		int week2 =DateUtils.getWeekOfYear(DateUtils.addDays(dateStar, -7));
		Map<String,Map<String,String>> rsMap=this.getSessionWarning(DateUtils.addDays(dateStar,-1));
		StringBuffer contents= new StringBuffer("");
		if(rsMap!=null&&rsMap.size()>0){
			contents.append("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;以下是以"+sdf.format(DateUtils.addDays(dateStar,-1))+"为参照点，往前推14天的数据比较（tips:7天session小于150的不统计）：<br/><table width='90%' style='border:1px solid #cad9ea;color:#666; '>" +
					"<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>国家</th><th>产品名</th>" +
					"<th>session("+week2+"周)</th><th>session("+week1+"周)</th><th>Conversion("+week2+")</th><th>Conversion("+week1+")</th><th>session波动率</th><th>Conversion波动率</th></tr>");
			String coutryStr="de,fr,it,es,jp,com,uk,ca";
			for(String country:coutryStr.split(",")){
				if(rsMap.get(country)==null){
					continue;
				}
				
				Map<String,String> productMap = rsMap.get(country);
				Map<String,Integer> sessionMap= Maps.newHashMap();
				//加入排序
				for (Map.Entry<String,String> entry : productMap.entrySet()) {  
				    String asinName=entry.getKey();
					String sessionConv = entry.getValue();
					String rate[] = sessionConv.split("_");
					sessionMap.put(asinName, Integer.parseInt(rate[0]));
				}
				List<String> asinNames= Lists.newArrayList();
				MapValueComparator bvc =  new MapValueComparator(sessionMap,true);  
				TreeMap<String,Integer> sortKeyMap = new TreeMap<String,Integer>(bvc);  
				sortKeyMap.putAll(sessionMap); 
		        asinNames.addAll(sortKeyMap.keySet());
				for(int i=0;i<asinNames.size();i++){
					String asinName = asinNames.get(i);
					String sessionConv = productMap.get(asinName);
					String asinArr[] = asinName.split(",");
					String rate[] = sessionConv.split("_");
					String proName="";
					if(asinArr.length==1){
						proName=asinArr[0];
					}else{
						proName=asinArr[1];
					}
					String color="#f5fafe";
					if(i==0&&!"de".equals(country)){
						color="#99CCFF";
					}
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:"+color+"; '><td>"+("com".equals(country)?"us":country)+
							"</td><td>"+proName+"</td><td>"+rate[2]+"</td><td>"+rate[3]+"</td><td>"+rate[4]+"%</td><td>"+rate[5]+"%</td><td>"+rate[0]+"%</td><td>"+rate[1]+"%</td></tr>");
				}
			}
			contents.append("</table>");
			Date date = new Date();
			//发信给德国仓库人员：
			String toAddress="amazon-sales@inateck.com";
			final MailInfo mailInfo = new MailInfo(toAddress,"Session或者Conversion下降50%预警"+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(contents.toString());
			new Thread(){
				public void run(){
					 mailManager.send(mailInfo);
				}
			}.start();
		}
	}
	
	public  Date getMaxDate(){
		String sql="SELECT MAX(data_date) AS maxDataDate FROM amazoninfo_business_report AS a";
		List<Date> dates=this.businessReportDao.findBySql(sql);
		return dates.get(0);
	}
	 
	public  List<Object> getDates(String country){
		String countryTemp = country;
		if("com.inateck".equals(country)){
			countryTemp = "com";
		}
		String sql="SELECT DISTINCT CONCAT(UNIX_TIMESTAMP(a.`data_date`),'') FROM amazoninfo_business_report a WHERE a.`country` = :p1 AND a.`data_date`>DATE_ADD(CURDATE(),INTERVAL -35 DAY)";
		List<Object> dates=this.businessReportDao.findBySql(sql,new Parameter(countryTemp));
		return dates;
	}
	
	
	public Map<String,Map<String,String>> getSessionWarning(Date date){
		Map<String,String> proNameMap =this.productService.getProductNameAsin();
		String sql="SELECT a.`country`,a.`child_asin`," +
			"	SUM( CASE WHEN a.`data_date` BETWEEN DATE_ADD(:p1,INTERVAL -6 DAY ) AND :p1  THEN a.`sessions`ELSE 0 END) AS session1," +
			"	SUM( CASE WHEN a.`data_date` BETWEEN DATE_ADD(:p1,INTERVAL -13 DAY ) AND DATE_ADD(:p1,INTERVAL -7 DAY )  THEN a.`sessions` ELSE 0 END ) AS session2," +
			"	SUM( CASE WHEN a.`data_date` BETWEEN DATE_ADD(:p1,INTERVAL -6 DAY ) AND :p1  THEN IFNULL(a.`orders_placed`,0) ELSE 0 END ) AS orders_placed1," +
			"	SUM( CASE WHEN a.`data_date` BETWEEN DATE_ADD(:p1,INTERVAL -13 DAY ) AND DATE_ADD(:p1,INTERVAL -7 DAY )  THEN IFNULL(a.`orders_placed`,0) ELSE 0 END ) AS orders_placed2" +
			"	FROM amazoninfo_business_report AS a WHERE a.data_date  BETWEEN DATE_ADD(:p1,INTERVAL -13 DAY ) AND :p1 " +
			"	GROUP BY a.`child_asin`,a.`country` HAVING (session1>150||session2>150)";
		List<Object[]> objs=this.businessReportDao.findBySql(sql,new Parameter(date));
		Map<String,Map<String,String>>  rsMap = Maps.newHashMap();
		for(Object[] obj: objs){
			String country =obj[0].toString();
			String asin =obj[1].toString();
			Integer sessionAfter = Integer.parseInt(obj[2].toString());
			Integer sessionBefore = Integer.parseInt(obj[3].toString());
			Integer orderAfter = Integer.parseInt(obj[4].toString());
			Integer orderBefore = Integer.parseInt(obj[5].toString());
			if(sessionBefore.intValue()==0||orderBefore.intValue()==0){
				continue;
			}
			try{
				Float  sessionRate =(float) (sessionAfter-sessionBefore)/sessionBefore;
				BigDecimal conAfterRate= BigDecimal.ZERO;
				if(sessionAfter.intValue()!=0){
					conAfterRate= new BigDecimal(orderAfter).divide(new BigDecimal(sessionAfter), 3, BigDecimal.ROUND_HALF_UP);
				}
				
				BigDecimal conBeforeRate= new BigDecimal(orderBefore).divide(new BigDecimal(sessionBefore), 3, BigDecimal.ROUND_HALF_UP);
				Float  orderRate=conAfterRate.subtract(conBeforeRate).divide(conBeforeRate,2, BigDecimal.ROUND_HALF_UP).floatValue();
				if(sessionRate<-0.5f||orderRate<-0.5f){//7天内减去(8-14)天/(8-14)天
					Map<String,String> inMap =null;
					if(rsMap.get(country)==null){
						inMap = Maps.newHashMap();
					}else{
						inMap = rsMap.get(country);
					}
					String key=asin+","+country;
					inMap.put(asin+","+(proNameMap.get(key)!=null?proNameMap.get(key):""), (int)(sessionRate*100)+"_"+(int)(orderRate*100)+"_"+sessionBefore+"_"+sessionAfter+"_"+conBeforeRate.multiply(new BigDecimal("100")).setScale(2)+"_"+conAfterRate.multiply(new BigDecimal("100")).setScale(2));
					rsMap.put(country, inMap);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		return rsMap;
	}
	
	public Map<String, Map<String, String>> findImageEvent(String productName ,String startDate,String endDate){
		 Map<String, Map<String,String>> rs = Maps.newHashMap();
		 String dateType = "'%Y/%c/%e'";
		 if(startDate.substring(0, 4).equals(endDate.substring(0, 4))){
			 dateType = "'%c/%e'";
		 }
		 String sql ="SELECT a.country,a.event_type,DATE_FORMAT(a.`event_data`, "+dateType+") FROM amazoninfo_session_event a WHERE a.`event_data`>=:p1 AND a.`event_data`<=:p2 AND a.`product_name` = :p3 and a.event_type='11' "; 
		 List<Object[]> list= this.businessReportDao.findBySql(sql,new Parameter(startDate,endDate,productName));
		 if(list!=null&&list.size()>0){
			 for (Object[] objs : list) {
				 String country = objs[0].toString();
				 String type = objs[1].toString();
				 String date = objs[2].toString();
				 Map<String,String> data = rs.get(country);
				 if(data==null){
					 data = Maps.newHashMap();
					 rs.put(country, data);
				 }
				 data.put(date, type);
			 }
		 }
		return rs;
	}
	
	public List<String> findImageChanges(String country,String startDate,String endDate){
		String sql ="SELECT DISTINCT a.product_name FROM amazoninfo_session_event a WHERE a.`country`=:p1 AND a.`event_data`>=:p2 AND a.`event_data`<=:p3 and a.event_type='11' "; 
		List<String> list = businessReportDao.findBySql(sql,new Parameter(country,startDate,endDate));
		return  list;
	}
	
	
	// 国家[日期/数据]
		public Map<String, Map<String, BusinessReport>> findCountProductsData(List<String> nameList){
			Date today=new Date();
			String typeSql = "'%Y%m%d'";
			List<Object[]> list = null;
			String sql = "SELECT SUM(a.`sessions`),SUM(ifnull(a.`orders_placed`,0)),DATE_FORMAT(a.`data_date`,"+typeSql+") dates,a.`country`,  "+
			        " CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) name "+
					" FROM amazoninfo_business_report a  " +
					 " JOIN psi_sku b ON a.child_asin=b.asin AND a.`country`=b.`country` AND b.`del_flag`='0' "+
					
					" WHERE a.`del_flag`='0' AND a.`data_date`>= :p1 AND a.`data_date` <= :p2 ";
			if(nameList!=null){
				sql+=" and CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) in :p3 ";
			}
			sql+=" GROUP BY a.`country`,dates,name ";
			if(nameList!=null){
				list = businessReportDao.findBySql(sql, new Parameter(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(today,-1)),today,nameList));
			}else{
				list = businessReportDao.findBySql(sql, new Parameter(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(today,-1)),today));
			}
			
			Map<String, Map<String,BusinessReport>> rs = Maps.newHashMap();
			for (Object[] obj : list) {
				Integer sessions = obj[0]==null?0:((BigDecimal)obj[0]).intValue();
				Integer ordersPlaced = obj[1]==null?0:((BigDecimal)obj[1]).intValue();
				String date = obj[2].toString();
				String country = obj[3].toString();
				String name=obj[4].toString();
				
				Map<String,BusinessReport> countrys = rs.get(country+"-"+name);
				if(countrys==null){
					countrys = Maps.newLinkedHashMap();
					rs.put(country+"-"+name, countrys);
				}
				BusinessReport br = countrys.get(date);
				if(br==null){
					br = new BusinessReport();
					br.setSessions(0);
					br.setOrdersPlaced(0);
					br.setCountry(country);
					countrys.put(date, br);
				}
				if(sessions!=null){
					br.setSessions(sessions+br.getSessions());
				}
				if(ordersPlaced!=null){
					br.setOrdersPlaced(ordersPlaced+br.getOrdersPlaced());
				}
				String month=date.substring(0,6);
				BusinessReport monthBr= countrys.get(month); 
				if(monthBr==null){
					monthBr = new BusinessReport();
					monthBr.setSessions(0);
					monthBr.setOrdersPlaced(0);
					monthBr.setCountry(country);
					countrys.put(month, monthBr);
				}
				if(sessions!=null){
					monthBr.setSessions(sessions+monthBr.getSessions());
				}
				if(ordersPlaced!=null){
					monthBr.setOrdersPlaced(ordersPlaced+monthBr.getOrdersPlaced());
				}
			}
			return rs;
		}
}
