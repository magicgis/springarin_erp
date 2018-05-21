/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AdvertisingByWeekDao;
import com.springrain.erp.modules.amazoninfo.dao.AdvertisingDao;
import com.springrain.erp.modules.amazoninfo.dao.AdvertisingSearchTermReportDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonSearchTermReportDao;
import com.springrain.erp.modules.amazoninfo.entity.Advertising;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingByWeek;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingCountDto;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingCountItemDto;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingSearchTermReport;
import com.springrain.erp.modules.amazoninfo.entity.AmazonSearchTermReport;
import com.springrain.erp.modules.amazoninfo.entity.BusinessReport;
import com.springrain.erp.modules.amazoninfo.entity.SaleProfit;

/**
 * 广告报表Service
 * @author Tim
 * @version 2015-03-03
 */
@Component
@Transactional(readOnly = true)
public class AdvertisingService extends BaseService {

	@Autowired
	private AdvertisingDao advertisingDao;
	@Autowired
	private AdvertisingByWeekDao advertisingByWeekDao;
	@Autowired
	private  AmazonSearchTermReportDao amazonSearchTermReportDao;
	@Autowired
    private  MailManager mailManager;
	@Autowired
    private  AdvertisingSearchTermReportDao searchTermReportDao;
	
	@Transactional(readOnly = false)
	public void saveSearchTermList(List<AmazonSearchTermReport> report){
		amazonSearchTermReportDao.save(report);
	}
	
	public List<String> findExistCountry(){
		String sql="SELECT DISTINCT country  FROM amazoninfo_search_term_report t WHERE t.`update_time` =:p1 ";
		return amazonSearchTermReportDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
	}
	
	public Map<String,Set<String>> findCountryByDate(){
		Map<String,Set<String>>  map=Maps.newHashMap();
		String sql="SELECT DISTINCT country,DATE_FORMAT(update_time,'%Y%m%d')  FROM amazoninfo_search_term_report t WHERE t.`update_time`>=:p1 ";
		List<Object[]> list=amazonSearchTermReportDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(new Date(),-6))));
		for (Object[] obj: list) {
			Set<String> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Sets.newHashSet();
				map.put(obj[0].toString(),temp);
			}
			temp.add(obj[1].toString());
		}
 		return map;
	}
	
	public Advertising get(String id) {
		return advertisingDao.get(id);
	}
	
	public List<AmazonSearchTermReport> find(AmazonSearchTermReport searchTerm) {
		if(searchTerm.getUpdateTime()==null){
			Date date=new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			searchTerm.setUpdateTime(date);
		}
		if(StringUtils.isBlank(searchTerm.getCountry())){
			searchTerm.setCountry("de");
		}
		DetachedCriteria dc = amazonSearchTermReportDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country", searchTerm.getCountry()));
		dc.add(Restrictions.eq("updateTime",searchTerm.getUpdateTime()));
		return amazonSearchTermReportDao.find(dc);
	}
	
	public Page<AmazonSearchTermReport> find(Page<AmazonSearchTermReport> page,AmazonSearchTermReport searchTerm) {
		
		DetachedCriteria dc = amazonSearchTermReportDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country", searchTerm.getCountry()));
		dc.add(Restrictions.eq("updateTime",searchTerm.getUpdateTime()));
		if(StringUtils.isNotBlank(searchTerm.getKeyword())){
			searchTerm.setKeyword(HtmlUtils.htmlUnescape(searchTerm.getKeyword()));
			if(searchTerm.getKeyword().contains("/")){
				String[] arr=searchTerm.getKeyword().split("/");
				dc.add(Restrictions.and(Restrictions.like("keyword", "%"+arr[1]+"%"),Restrictions.or(Restrictions.like("adGroupName", "%"+arr[0]+"%"),Restrictions.like("campaignName", "%"+arr[0]+"%"))));
			}else{
				dc.add(Restrictions.or(Restrictions.like("keyword", "%"+searchTerm.getKeyword()+"%"),Restrictions.or(Restrictions.like("adGroupName", "%"+searchTerm.getKeyword()+"%"),Restrictions.like("campaignName", "%"+searchTerm.getKeyword()+"%"))));
			}
		}
		//dc.addOrder(Order.desc("endDate"));
		return amazonSearchTermReportDao.find(page, dc);
	}
	
    public Page<AdvertisingSearchTermReport> find(Page<AdvertisingSearchTermReport> page,AdvertisingSearchTermReport searchTerm) {
		if(StringUtils.isBlank(searchTerm.getCountry())){
			searchTerm.setCountry("de");
		}
		DetachedCriteria dc = searchTermReportDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country", searchTerm.getCountry()));
		dc.add(Restrictions.eq("dataDate",searchTerm.getUpdateTime()));
		if(StringUtils.isNotBlank(searchTerm.getKeyword())){
			searchTerm.setKeyword(HtmlUtils.htmlUnescape(searchTerm.getKeyword()));
			if(searchTerm.getKeyword().contains("/")){
				String[] arr=searchTerm.getKeyword().split("/");
				dc.add(Restrictions.and(Restrictions.like("keyword", "%"+arr[1]+"%"),Restrictions.or(Restrictions.like("adGroupName", "%"+arr[0]+"%"),Restrictions.like("campaignName", "%"+arr[0]+"%"))));
			}else{
				dc.add(Restrictions.or(Restrictions.like("keyword", "%"+searchTerm.getKeyword()+"%"),Restrictions.or(Restrictions.like("adGroupName", "%"+searchTerm.getKeyword()+"%"),Restrictions.like("campaignName", "%"+searchTerm.getKeyword()+"%"))));
			}
		}
		return searchTermReportDao.find(page, dc);
	}

	public List<Advertising> find(Advertising advertising) {
		DetachedCriteria dc = advertisingDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(advertising.getKeyword())){
			dc.add(Restrictions.or(Restrictions.like("keyword", "%"+advertising.getKeyword()+"%"),Restrictions.like("sku", "%"+advertising.getKeyword()+"%"),Restrictions.like("groupName", "%"+advertising.getKeyword()+"%")));
		}
		dc.add(Restrictions.eq("country", advertising.getCountry()));
		if(advertising.getDataDate()==null){
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			if("com".equals(advertising.getCountry())){
				advertising.setDataDate(DateUtils.addDays(today, -2));
			}else{
				advertising.setDataDate(DateUtils.addDays(today, -1));
			}
			advertising.setCreateDate(DateUtils.addMonths(advertising.getDataDate(),-1));
		}
		dc.add(Restrictions.and(Restrictions.ge("dataDate",advertising.getCreateDate()),Restrictions.lt("dataDate", DateUtils.addDays(advertising.getDataDate(),1))));
		
		return advertisingDao.findCount(dc);
	}
	
	public List<AdvertisingByWeek> find(AdvertisingByWeek advertisingByWeek) {
		DetachedCriteria dc = advertisingByWeekDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(advertisingByWeek.getKeyword())){
			dc.add(Restrictions.or(Restrictions.like("keyword", "%"+advertisingByWeek.getKeyword()+"%"),Restrictions.like("sku", "%"+advertisingByWeek.getKeyword()+"%")));
		}
		dc.add(Restrictions.eq("country", advertisingByWeek.getCountry()));
		if(advertisingByWeek.getStartWeek()==null){
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			today = DateUtils.addWeeks(today, -1);
			advertisingByWeek.setStartWeek(DateUtils.getDate(today, "yyyyww"));
			advertisingByWeek.setEndWeek(advertisingByWeek.getStartWeek());
		}		
		dc.add(Restrictions.and(Restrictions.ge("week",getWeekStr(advertisingByWeek.getStartWeek())),Restrictions.le("week",getWeekStr(advertisingByWeek.getEndWeek()))));
		
		return advertisingByWeekDao.findCount(dc);
	}
	
	private static String getWeekStr(String date){
		if ("201601".equals(date)) {
			date = "201553";
		} else if(date.startsWith("2016")){
			Integer i = Integer.parseInt(date.substring(4));
			i = i-1;
			date = "2016"+(i<10?("0"+i):i);
		} 
		return date;
	}
	
	
	public List<Advertising> findByDate(Advertising advertising) {
		DetachedCriteria dc = advertisingDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(advertising.getKeyword())){
			dc.add(Restrictions.eq("keyword",advertising.getKeyword()));
		}
		if (StringUtils.isNotEmpty(advertising.getSku())){
			dc.add(Restrictions.eq("sku", advertising.getSku()));
		}
		if (StringUtils.isNotEmpty(advertising.getGroupName())){
			dc.add(Restrictions.eq("groupName",advertising.getGroupName()));
		}
		if (StringUtils.isNotEmpty(advertising.getName())){
			dc.add(Restrictions.eq("name",advertising.getName()));
		}
		dc.add(Restrictions.eq("country", advertising.getCountry()));
		if(advertising.getDataDate()==null){
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			advertising.setDataDate(DateUtils.addDays(today, -1));
			advertising.setCreateDate(DateUtils.addMonths(advertising.getDataDate(),-1));
		}
		dc.add(Restrictions.and(Restrictions.ge("dataDate",advertising.getCreateDate()),Restrictions.lt("dataDate", DateUtils.addDays(advertising.getDataDate(),1))));
		return advertisingDao.findByDate(advertising, dc);
	}
	
	public List<Advertising> findAll(Advertising advertising) {
		DetachedCriteria dc = advertisingDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country", advertising.getCountry()));
		dc.add(Restrictions.and(Restrictions.ge("dataDate",advertising.getCreateDate()),Restrictions.lt("dataDate", DateUtils.addDays(advertising.getDataDate(),1))));
		return advertisingDao.find(dc);
	}
	
	//类型
	public List<Object[]> countByType(Date start,Date end ,String typeFlag,String country){
		String temp = "";
		if("1".equals(typeFlag)){
			temp = "%Y%u";
		}else {
			temp = "%Y%m";
		}
		String sql = "SELECT DATE_FORMAT(a.`data_date`,'"+temp+"') days,ss.type,SUM(a.`total_spend`),TRUNCATE(SUM(a.`total_spend`)*100/SUM((a.`other_sku_order_sales`+a.`same_sku_order_sales`)),2) FROM amazoninfo_advertising a,(SELECT p.`TYPE`,s.`sku` FROM psi_product p ,psi_sku s WHERE p.id=s.`product_id` AND s.`del_flag` = '0' AND p.`del_flag`='0' AND s.`country`=:p1) ss  WHERE  ss.sku= a.`sku` AND a.`country` = :p1  AND a.`data_date`>=:p2 AND a.`data_date`<=:p3  GROUP BY ss.type,days ";
		List<Object[]> rs = advertisingDao.findBySql(sql, new Parameter(country,start,end));
		return rs;
	}
	
	//产品类型明显 时间点/数据
	public Map<String,Map<String,AdvertisingCountDto>> countByProductName(Date start,Date end ,String typeFlag,String country,String search){
		String temp = "";
		if("1".equals(typeFlag)){
			temp = "%Y%u";
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
		}else  if("2".equals(typeFlag)){
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			temp = "%Y%m";
		}else  if("3".equals(typeFlag)){
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			temp = "%Y%m%d";
		}
//		String sql = "SELECT ss.type,DATE_FORMAT(a.`data_date`,'"+temp+
//					"') days,a.`sku`,SUM(a.`total_spend`),SUM((a.`other_sku_order_sales`+a.`same_sku_order_sales`)) FROM amazoninfo_advertising a,(SELECT p.`TYPE`,s.`sku` FROM psi_product p ,psi_sku s WHERE p.id=s.`product_id` AND s.`del_flag` = '0' AND p.`del_flag`='0' AND s.`country`=:p1) ss  WHERE  ss.sku= a.`sku` AND a.`country` = :p1  AND a.`data_date`>=:p2 AND a.`data_date`<=:p3  GROUP BY ss.type,a.`sku`,days order by a.`data_date` ";

		String searchSql = "";
		if(search.trim().length()>0){
			searchSql = "and ( a.keyword like '%"+search+"%' or a.sku like '%"+search+"%' )";
		}
		
		
		String sql = "SELECT a.sku asku,DATE_FORMAT(a.`data_date`,'"+temp+
					"') days,a.`sku`,TRUNCATE(CASE WHEN a.`country`='jp' THEN SUM(a.`total_spend`)*"+MathUtils.getRate("JPY", "USD", null)+" ELSE SUM(a.`total_spend`) END,2),TRUNCATE(CASE WHEN a.`country`='jp' THEN SUM((a.`other_sku_order_sales`+a.`same_sku_order_sales`))*"+MathUtils.getRate("JPY", "USD", null)+" ELSE SUM((a.`other_sku_order_sales`+a.`same_sku_order_sales`)) END,2),sum(a.same_sku_orders_placed),sum(a.impressions),sum(a.clicks),a.keyword FROM amazoninfo_advertising a  WHERE a.`country` = :p1  AND a.`data_date`>=:p2 AND a.`data_date`<=:p3 "+searchSql+"   GROUP BY a.`sku`,a.keyword,days order by a.`data_date` ";
		List<Object[]> list = advertisingDao.findBySql(sql, new Parameter(country,start,end));
		Map<String,Map<String,AdvertisingCountDto>> rs = Maps.newHashMap();
		
		
		String skuSql="SELECT p.`TYPE`,s.`sku` FROM psi_product p  JOIN psi_sku s ON p.id=s.`product_id` "+
                      " WHERE  s.`country`=:p1 AND  s.`del_flag` = '0' AND p.`del_flag`='0' ";
		List<Object[]> skuList= advertisingDao.findBySql(skuSql, new Parameter(country));
		Map<String,String> skuMap=Maps.newHashMap();
		for (Object[] obj: skuList) {
			skuMap.put(obj[1].toString(), obj[0].toString());
		}
		for (Object[] objects : list) {
			String date = objects[1].toString();
			String sku = objects[2].toString();
			String type = skuMap.get(sku);
			float totalSpend = 0f;
			float totalSales = 0f;
			Object obj = objects[3];
			if(obj!=null){
				totalSpend = ((BigDecimal)obj).floatValue();
			}
			obj = objects[4];
			if(obj!=null){
				totalSales = ((BigDecimal)obj).floatValue();
			}
			int saleV = 0;
			int impressions = 0;
			int clicks = 0 ;
			
			obj = objects[5];
			if(obj!=null){
				saleV = Integer.parseInt(obj.toString());
			}
			
			obj = objects[6];
			if(obj!=null){
				impressions = Integer.parseInt(obj.toString());
			}
			
			obj = objects[7];
			if(obj!=null){
				clicks = Integer.parseInt(obj.toString());
			}
			
			
			Map<String,AdvertisingCountDto> dataDetail = rs.get(type);
			if(dataDetail==null){
				dataDetail = Maps.newLinkedHashMap();
				rs.put(type, dataDetail);
			}
			AdvertisingCountDto dto = dataDetail.get(date);
			if(dto==null){
				dto = new AdvertisingCountDto(type, 0f, 0f, date);
				dto.setClicks(0);
				dto.setImpressions(0);
				dto.setQuantity(0);
				dataDetail.put(date, dto);
			}
			dto.getItems().add(new AdvertisingCountItemDto(type, sku, totalSpend, totalSales, date,saleV,impressions,clicks,objects[8].toString()));
			dto.setTotalOrderSales(dto.getTotalOrderSales()+totalSales);
			dto.setTotalSpend(dto.getTotalSpend()+totalSpend);
			dto.setClicks(dto.getClicks()+clicks);
			dto.setImpressions(dto.getImpressions()+impressions);
			dto.setQuantity(dto.getQuantity()+saleV);
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void save(Advertising advertising) {
		advertisingDao.save(advertising);
	}
	
	@Transactional(readOnly = false)
	public void save(List<Advertising> advertisings) {
		advertisingDao.save(advertisings);
	}
	
	@Transactional(readOnly = false)
	public void saveWeeks(List<AdvertisingByWeek> weeks) {
		advertisingByWeekDao.save(weeks);
	}
	
	public Date getMaxDate(String country) {
		String sql = "SELECT MAX(a.`data_date`) FROM amazoninfo_advertising a WHERE a.`country` = :p1";
		List<Object> list  = advertisingDao.findBySql(sql, new Parameter(country));
		if(list.size()==1){
			return (Timestamp)list.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void save(String country,String balance,String remark) {
		String sql = "UPDATE account_balance a SET a.`balance` = :p1,a.`update_time` = NOW(),a.remark=:p3 WHERE a.`country`=:p2 ";
		advertisingDao.updateBySql(sql, new Parameter(balance,country,remark));
	}
	
	public Map<String,Float> accountBalance() {
		String sql = "SELECT a.`account_name`,a.`balance` FROM account_balance a ";
		List<Object[]> list = advertisingDao.findBySql(sql);
		 Map<String,Float>  rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(),((BigDecimal)objects[1]).floatValue());
		}
		return rs;
	}

	public List<Advertising> findByCountry(Advertising advertising, String startStr, String endStr, String orderBy) {
		String typeSql = "'%Y-%m-%d'";
		boolean flag = false;
		//查询2个时间节点
		if("1".equals(advertising.getSearchFlag())){
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
			flag = true;
		}else if("2".equals(advertising.getSearchFlag())){
			typeSql="'%Y-%m'";//按月查询
		}
		String sql = "SELECT t.`country`,SUM(t.`clicks`) AS clicks,SUM(t.`total_spend`) AS totalSpend,SUM(t.`same_sku_orders_placed`),SUM(t.`other_sku_orders_placed`)," +
				" SUM(t.`same_sku_order_sales`),SUM( t.`other_sku_order_sales`) AS order_sales," +
				" DATE_FORMAT(t.`data_date`,"+typeSql+") dates " +
				" FROM `amazoninfo_advertising` t " +
				" WHERE DATE_FORMAT(t.`data_date`,"+typeSql+")>=:p1 and DATE_FORMAT(t.`data_date`,"+typeSql+")<=:p2 AND t.`country`=:p3" +
				" GROUP BY t.`country`,dates ";
				if (StringUtils.isNotEmpty(orderBy) && orderBy.contains("dates")) {
					sql +=" ORDER BY " + orderBy;
				} else if (StringUtils.isEmpty(orderBy)) {
					sql +=" ORDER BY dates DESC";
				}
		List<Object[]> list = advertisingDao.findBySql(sql, new Parameter(startStr, endStr, advertising.getCountry()));
		
		List<Advertising> rsList = Lists.newArrayList();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			int clicks = ((BigDecimal)objs[1]).intValue();
			float totalSpend = ((BigDecimal)objs[2]).floatValue();
			int sameSkuOrdersPlaced = ((BigDecimal)objs[3]).intValue();
			int otherSkuOrdersPlaced = ((BigDecimal)objs[4]).intValue();
			float sameSkuOrderSales = ((BigDecimal)objs[5]).floatValue();
			float otherSkuOrderSales = ((BigDecimal)objs[6]).floatValue();
			String date = objs[7].toString();
			if("1".equals(advertising.getSearchFlag())){
				date = DateUtils.getWeekStr(date, 5, "-", true);
			}
			float conversion = clicks==0?0f:(float)(sameSkuOrdersPlaced+otherSkuOrdersPlaced)/clicks*100f;
			String name = "";
			if (flag) {//计算日期区间
				try {
					Date first1 = DateUtils.getFirstDayOfWeek(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]));
					name = "(" + DateUtils.getDate(first1,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first1),"yyyy-MM-dd")+")";
				} catch (Exception e) {}
			}
			//groupName字段放date,name放日期区间
			rsList.add(new Advertising(country, name, date, null, null, null, null, 
					null, clicks, totalSpend, conversion, sameSkuOrdersPlaced, sameSkuOrderSales, 
					otherSkuOrdersPlaced, otherSkuOrderSales, null, null,null,null,null,null));
		}
		return rsList;
	}

	public Map<String,Map<String, Advertising>> totalByDate(Advertising advertising, String startStr, String endStr) {
		String typeSql = "'%Y-%m-%d'";
		//查询2个时间节点
		if("1".equals(advertising.getSearchFlag())){
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
		}else if("2".equals(advertising.getSearchFlag())){
			typeSql="'%Y-%m'";//按月查询
		}
		String sql = "SELECT t.`country`,SUM(t.`clicks`),SUM(t.`total_spend`),SUM(t.`same_sku_orders_placed`),SUM(t.`other_sku_orders_placed`)," +
				" SUM(t.`same_sku_order_sales`),SUM( t.`other_sku_order_sales`) AS order_sales," +
				" DATE_FORMAT(t.`data_date`,"+typeSql+") dates" +
				" FROM `amazoninfo_advertising` t " +
				" WHERE (DATE_FORMAT(t.`data_date`,"+typeSql+")=:p1 or DATE_FORMAT(t.`data_date`,"+typeSql+")=:p2)" +
				" GROUP BY t.`country`,dates";
		List<Object[]> list = advertisingDao.findBySql(sql, new Parameter(startStr, endStr));
		
		Map<String,Map<String, Advertising>> rs = Maps.newHashMap();
		Map<String, String> changeMap = getCurrencySymbol();
		for (Object[] objs : list) {
			String country = objs[0].toString(); 
			float rate = 1f;
			try {
				rate = MathUtils.getRate(changeMap.get(country), "EUR", null);
			} catch (Exception e) {}
			int clicks = ((BigDecimal)objs[1]).intValue();
			float totalSpend = ((BigDecimal)objs[2]).floatValue() * rate;
			int sameSkuOrdersPlaced = ((BigDecimal)objs[3]).intValue();
			int otherSkuOrdersPlaced = ((BigDecimal)objs[4]).intValue();
			float sameSkuOrderSales = ((BigDecimal)objs[5]).floatValue() * rate;
			float otherSkuOrderSales = ((BigDecimal)objs[6]).floatValue() * rate;
			String date = objs[7].toString();
			if("1".equals(advertising.getSearchFlag())){
				date = DateUtils.getWeekStr(date, 5, "-", true);
			}
			float conversion = clicks==0?0f:(float)(sameSkuOrdersPlaced+otherSkuOrdersPlaced)/clicks*100f;
			Map<String, Advertising> datas = rs.get(country);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(country, datas);
			}
			
			datas.put(date, new Advertising(country, null, null, null, null, null, null, 
					null, clicks, totalSpend, conversion, sameSkuOrdersPlaced, sameSkuOrderSales, 
					otherSkuOrdersPlaced, otherSkuOrderSales, null, null,null,null,null,null));
			
			Map<String, Advertising> datas1 = rs.get("total");
			if(datas1==null){
				datas1 = Maps.newLinkedHashMap();
				rs.put("total", datas1);
			}
			Advertising entry = datas1.get(date);
			if(entry==null){
				entry = new Advertising(null, null, null, null, null, null, null, 
						null, 0, 0f, null, 0, 0f, 0, 0f, null, null,null,null,null,null);
				datas1.put(date, entry);
			}
			entry.setClicks(clicks + entry.getClicks());
			entry.setTotalSpend(totalSpend + entry.getTotalSpend());
			entry.setSameSkuOrderSales(sameSkuOrderSales + entry.getSameSkuOrderSales());
			entry.setOtherSkuOrderSales(otherSkuOrderSales + entry.getOtherSkuOrderSales());
			entry.setSameSkuOrdersPlaced(sameSkuOrdersPlaced + entry.getSameSkuOrdersPlaced());
			entry.setOtherSkuOrdersPlaced(otherSkuOrdersPlaced + entry.getOtherSkuOrdersPlaced());
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void updateAdvertsingByWeek(String country){
		 String sql="INSERT INTO `amazoninfo_advertising_week` (country,group_name,name,keyword,type,sku,week,clicks,impressions,total_spend,week_same_sku_units_sales,week_same_sku_units_ordered,update_date)  "+ 
					"SELECT a.`country`,a.`group_name`,a.`name`,a.`keyword`,a.`type`,a.`sku`,DATE_FORMAT(a.`data_date`,'%Y%U') AS weeks,SUM(a.`clicks`),SUM(a.`impressions`),SUM(a.`total_spend`),SUM(a.`week_same_sku_units_sales`),SUM(a.`week_same_sku_units_ordered`),curdate() FROM amazoninfo_advertising a WHERE a.country = :p1 and  DATE_FORMAT(a.`data_date`,'%Y%U') >=  DATE_FORMAT(DATE_ADD(CURDATE(),INTERVAL -65 DAY),'%Y%U') AND a.`data_date`>'2016-4-9' AND DATE_FORMAT(a.`data_date`,'%Y%U')< DATE_FORMAT(CURDATE(),'%Y%U') GROUP BY a.`country`,a.`group_name`,a.`name`,a.`keyword`,a.`type`,a.`sku`,weeks"+
					" ON DUPLICATE KEY UPDATE `clicks` = VALUES(clicks),`impressions` = VALUES(impressions),`total_spend` = VALUES(total_spend),`week_same_sku_units_sales` =VALUES(week_same_sku_units_sales),`week_same_sku_units_ordered` = VALUES(week_same_sku_units_ordered),`update_date` = VALUES(update_date) ";
		 advertisingDao.updateBySql(sql, new Parameter(country));
	}
	
	public boolean initData(String country){
		String sql = "SELECT COUNT(*) FROM amazoninfo_advertising_week a WHERE a.`week` = DATE_FORMAT(DATE_ADD(CURDATE(),INTERVAL -1 WEEK),'%Y%U') AND a.`week_other_sku_units_ordered` >0 and a.country = :p1";
		List<Object> list = advertisingDao.findBySql(sql,new Parameter(country));
		return Integer.parseInt(list.get(0).toString())==0;
	}
	
	
	@Transactional(readOnly = false)
	public void updateAdvertsingOtherAsin(AdvertisingByWeek week){
		 String sql="UPDATE amazoninfo_advertising_week a SET a.`week_same_sku_units_lirun` = :p1,a.`week_other_sku_units_ordered`=:p2,a.`week_other_sku_units_sales`=:p3,a.`week_other_sku_units_lirun`=:p4,a.`week_parent_sku_units_ordered`=:p5,a.`week_parent_sku_units_sales`=:p6,a.`week_parent_sku_units_lirun`=:p7 WHERE a.`country` = :p8 AND  a.`group_name`=:p9 AND a.`keyword` = :p10 AND a.`name` = :p11 AND a.`sku` = :p12 AND a.`type` = :p13 AND a.`week` = :p14";
		 advertisingDao.updateBySql(sql, new Parameter(week.getWeekSameSkuUnitsLirun(),week.getWeekOtherSkuUnitsOrdered(),week.getWeekOtherSkuUnitsSales(),week.getWeekOtherSkuUnitsLirun(),week.getWeekParentSkuUnitsOrdered(),week.getWeekParentSkuUnitsSales(),week.getWeekParentSkuUnitsLirun(),week.getCountry(),week.getGroupName(),week.getKeyword(),week.getName(),week.getSku(),week.getType(),week.getWeek()));
	}
	
	@Transactional(readOnly = false)
	public void updateAdvertsingOtherAsin(List<AdvertisingByWeek> weeks){
		for (AdvertisingByWeek week : weeks) {
			 String sql="UPDATE amazoninfo_advertising_week a SET a.`week_same_sku_units_lirun` = :p1,a.`week_other_sku_units_ordered`=:p2,a.`week_other_sku_units_sales`=:p3,a.`week_other_sku_units_lirun`=:p4,a.`week_parent_sku_units_ordered`=:p5,a.`week_parent_sku_units_sales`=:p6,a.`week_parent_sku_units_lirun`=:p7 WHERE a.`country` = :p8 AND  a.`group_name`=:p9 AND a.`keyword` = :p10 AND a.`name` = :p11 AND a.`sku` = :p12 AND a.`type` = :p13 AND a.`week` = :p14";
			 advertisingDao.updateBySql(sql, new Parameter(week.getWeekSameSkuUnitsLirun(),week.getWeekOtherSkuUnitsOrdered(),week.getWeekOtherSkuUnitsSales(),week.getWeekOtherSkuUnitsLirun(),week.getWeekParentSkuUnitsOrdered(),week.getWeekParentSkuUnitsSales(),week.getWeekParentSkuUnitsLirun(),week.getCountry(),week.getGroupName(),week.getKeyword(),week.getName(),week.getSku(),week.getType(),week.getWeek()));
		}
	}
	
	public List<AdvertisingByWeek> findCurrWeekData(String country){
		DetachedCriteria dc = advertisingByWeekDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country", country));
		Date today = DateUtils.addWeeks(new Date(), -1);
		int currentWeek = DateUtils.getWeekOfYear(today);
		int year  = today.getYear()+1900;
		if(year==2016){
			currentWeek = currentWeek-1;
		}
		String weekStr = year+""+(currentWeek<10?("0"+currentWeek):currentWeek);
		dc.add(Restrictions.eq("week",weekStr));
		List<AdvertisingByWeek> rs =  advertisingByWeekDao.find(dc);
		return rs;
	}
	
	public Map<String,Map<String,String>> getRelationshipAsin(){
		String sql = "SELECT  a.`country`,GROUP_CONCAT(DISTINCT a.`ASIN`) FROM amazoninfo_posts_detail a WHERE a.`query_time` > DATE_ADD(CURDATE(),INTERVAL -1 WEEK)  AND a.`parent_id`  IS NOT NULL  GROUP BY a.`country`,a.`parent_id` HAVING COUNT(1)>1";
		List<Object[]> list = advertisingByWeekDao.findBySql(sql);
		Map<String,Map<String,String>> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			String country = objects[0].toString();
			String asin = objects[1].toString();
			Map<String,String> asinMap = rs.get(country);
			if(asinMap==null){
				asinMap = Maps.newHashMap();
				rs.put(country, asinMap);
			}
			String[]asins = asin.split(",");
			for (String asinStr : asins) {
				String temp = asinMap.get(asinStr); 
				if(temp==null){
					asinMap.put(asinStr, asin);
				}else{
					asinMap.put(asinStr, asin+","+temp);
				}
			}
		}
		return rs;
	}
	
	//[国家[asin price]]
	public Map<String,Map<String,Float>> getProductPrice(){
		String sql = "SELECT * FROM amazoninfo_product_sale_price a ";
		List<Object[]> list = advertisingByWeekDao.findBySql(sql);
		Map<String, Object[]> map = Maps.newHashMap();
		for (Object[] objs : list) {
			map.put(objs[1].toString(), objs);
		}
		sql = "SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`),a.`country`,GROUP_CONCAT(DISTINCT a.asin)  FROM psi_sku a WHERE a.`del_flag` = '0' AND a.`product_name` !='inateck old' AND  a.`product_name` !='inateck other' AND a.`asin` IS NOT NULL GROUP BY a.`product_name`,a.`color`,a.`country`";
		list = advertisingByWeekDao.findBySql(sql);
		Map<String,Map<String,Float>> rs = Maps.newHashMap();
		rs.put("de",new HashMap<String, Float>());
		rs.put("fr",new HashMap<String, Float>());
		rs.put("es",new HashMap<String, Float>());
		rs.put("it",new HashMap<String, Float>());
		rs.put("uk",new HashMap<String, Float>());
		rs.put("jp",new HashMap<String, Float>());
		rs.put("ca",new HashMap<String, Float>());
		rs.put("com",new HashMap<String, Float>());
		
		for (Object[] objs : list) {
			String name = objs[0].toString();
			String country = objs[1].toString();
			String asins = objs[2].toString();
			Object[] objects = map.get(name);
			if(objects!=null){
				Float price = 0f;
				if("de".equals(country)&&objects[2]!=null){
					price = Float.parseFloat(objects[2].toString());
				}else if("fr".equals(country)&&objects[5]!=null){
					price = Float.parseFloat(objects[5].toString());
				}else if("es".equals(country)&&objects[7]!=null){
					price = Float.parseFloat(objects[7].toString());
				}else if("it".equals(country)&&objects[6]!=null){
					price = Float.parseFloat(objects[6].toString());
				}else if("uk".equals(country)&&objects[4]!=null){
					price = Float.parseFloat(objects[4].toString());
				}else if("jp".equals(country)&&objects[8]!=null){
					price = Float.parseFloat(objects[8].toString());
				}else if("com".equals(country)&&objects[3]!=null){
					price = Float.parseFloat(objects[3].toString());
				}else if("ca".equals(country)&&objects[9]!=null){
					price = Float.parseFloat(objects[9].toString());
				}
				if(price>0f){
					for (String asin : asins.split(",")) {
						rs.get(country).put(asin,price);
					}
				}
			}
		}
		return rs;
	}
	
	public Map<String,String> getSkuAndAsinMap(){
		String sql = "SELECT DISTINCT a.asin,a.`sku`  FROM psi_sku a WHERE a.`del_flag` = '0' AND a.`product_name` !='inateck old' AND  a.`product_name` !='inateck other' AND a.`asin` IS NOT NULL ";
		List<Object[]> list = advertisingByWeekDao.findBySql(sql);
		Map<String,String> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[1].toString(), objects[0].toString());
		}
		return rs;
	}
	
	
	
	/**
	 *查询当天所有投放广告的sku 
	 */
	public List<String> getOneDaySku(String country,Date date){
		String sql = "SELECT a.sku  FROM amazoninfo_advertising AS a WHERE a.`data_date`=:p1 AND a.`country`=:p2 AND DATEDIFF(DATE_FORMAT(a.`create_date`,'%Y-%m-%d'),a.`data_date`)<4 GROUP BY a.`sku`";
		return advertisingByWeekDao.findBySql(sql,new Parameter(date,country));
	}
	
	/**
	 *获得每个国家最新的数据 
	 */
	public Map<String,Date> getCountryDateMap(){
		Map<String,Date>  countryDateMap = Maps.newHashMap();
		String sql = "SELECT MAX(a.`data_date`),a.`country` FROM amazoninfo_advertising AS a GROUP BY a.`country`";
		List<Object[]> objs= advertisingByWeekDao.findBySql(sql);
		for(Object[] obj :objs){
			countryDateMap.put(obj[1].toString(),(Date)obj[0]);
		}
		return countryDateMap;
	}


	
	public static Map<String, String> getCurrencySymbol(){
		Map<String, String> map = Maps.newHashMap();
		map.put("de","EUR");
		map.put("fr","EUR");
		map.put("it","EUR");
		map.put("es","EUR");
		map.put("uk","GBP");
		map.put("com","USD");
		map.put("ca","CAD");
		map.put("jp","JPY");
		map.put("mx","MXN");
		return map;
	}
	
	
	public Map<String,Map<String,Advertising>> findAdsReport(Advertising advertising){
		Map<String,Map<String,Advertising>> map=Maps.newHashMap();
		Date start = advertising.getCreateDate();
		Date end = advertising.getDataDate();
		String typeSql = "'%Y%m%d'";
		if("2".equals(advertising.getSearchFlag())){//1：按日期       2： 按星期       3： 按月份统计
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today,-8);
				end = today;
				advertising.setCreateDate(start);
				advertising.setDataDate(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					advertising.setDataDate(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(advertising.getSearchFlag())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -3);
				end = today;
				advertising.setCreateDate(start);
				advertising.setDataDate(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					advertising.setDataDate(end1);
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
				advertising.setCreateDate(start);
				advertising.setDataDate(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					advertising.setDataDate(end1);
				}
			}
		}
		
		//String sql="SELECT  t.`country`,DATE_FORMAT(t.`data_date`,"+typeSql+") dates,CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.color) END ) NAME,SUM(t.`clicks`),SUM(t.`same_sku_orders_placed`),SUM(t.`total_spend`)  FROM amazoninfo_advertising t  "+
		//"	JOIN psi_sku p ON t.`country`=p.`country` AND t.sku=p.sku AND p.`del_flag`='0' ";
		
		String sql="SELECT  t.`country`,DATE_FORMAT(t.`data_date`,"+typeSql+") dates,CONCAT(p.`product_name`,CASE WHEN p.`color` = '' THEN '' ELSE CONCAT('_', p.color)  END) name,SUM(t.`clicks`),SUM(t.`same_sku_orders_placed`),SUM(t.`total_spend`)  FROM amazoninfo_advertising t  "+
		         "	,psi_sku p ";

		if(StringUtils.isNotBlank(advertising.getGroupName())){
			sql+=",psi_product d,sys_dict tt,psi_product_type_group g,psi_product_type_dict pp ";
		}
		sql+= "	WHERE t.`sku`=p.sku   AND t.`data_date`>=:p1 AND t.`data_date`<=:p2 AND t.country=:p3 AND t.`country`=p.`country`   ";
		if(StringUtils.isNotBlank(advertising.getGroupName())){
			sql+= 	" AND CONCAT(d.brand,' ',d.model)=p.product_name AND d.del_flag='0' AND d.type=tt.`value` AND tt.`del_flag`='0' AND  tt.`type`='product_type' AND tt.id=g.`dict_id` AND pp.id=g.id  AND pp.`del_flag`='0' ";
			 
		}
		    
		if(StringUtils.isNotBlank(advertising.getGroupName())){
			sql+=" and pp.id='"+advertising.getGroupName()+"'";
		}
		sql+= "	GROUP BY t.`country`,dates,p.`product_name`,p.`color` ORDER BY name ";	
		List<Object[]> list = advertisingDao.findBySql(sql, new Parameter(start,end,advertising.getCountry()));
		
		for (Object[] obj: list) {
			//String country=obj[0].toString();
			String date=obj[1].toString();
			String name=obj[2].toString();
			Integer click=Integer.parseInt(obj[3]==null?"0":obj[3].toString());
			Integer quantity=Integer.parseInt(obj[4]==null?"0":obj[4].toString());
			Float adsCost=Float.parseFloat(obj[5]==null?"0":obj[5].toString());
			Advertising ads=new Advertising();
			ads.setClicks(click);
			ads.setSameSkuOrdersPlaced(quantity);
			ads.setTotalSpend(adsCost);
			Map<String,Advertising> temp=map.get(name);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(name, temp);
			}
			temp.put(date, ads);
		}
		return map;
	}
	
	
	public Map<String,Map<String,Advertising>> findAdsReportByKeyWord(Advertising advertising){
		Map<String,Map<String,Advertising>> map=Maps.newHashMap();
		Date start = advertising.getCreateDate();
		Date end = advertising.getDataDate();
		String typeSql = "'%Y%m%d'";
		if("2".equals(advertising.getSearchFlag())){//1：按日期       2： 按星期       3： 按月份统计
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -8);
				end = today;
				advertising.setCreateDate(start);
				advertising.setDataDate(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					advertising.setDataDate(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(advertising.getSearchFlag())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -6);
				end = today;
				advertising.setCreateDate(start);
				advertising.setDataDate(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					advertising.setDataDate(end1);
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
				advertising.setCreateDate(start);
				advertising.setDataDate(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					advertising.setDataDate(end1);
				}
			}
		}
		
		String sql="SELECT  t.`country`,DATE_FORMAT(t.`data_date`,"+typeSql+") dates,t.keyword,SUM(t.`clicks`),SUM(t.`same_sku_orders_placed`),SUM(t.`total_spend`)  FROM amazoninfo_advertising t  "+
		"	,(SELECT DISTINCT CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.color) END ) NAME,p.sku,p.`product_name` FROM psi_sku p  WHERE  p.`del_flag` = '0' AND p.`country` = :p3) p "+
	    "	WHERE  p.sku = t.`sku` and t.country=:p3 AND t.`data_date`>=:p1 AND t.`data_date`<=:p2   "+
		"  and p.name=:p4 and t.`clicks`>0 "+
		"	group by dates,t.`country`,t.keyword ";	
		List<Object[]> list = advertisingDao.findBySql(sql, new Parameter(start,end,advertising.getCountry(),advertising.getName()));
		
		for (Object[] obj: list) {
			//String country=obj[0].toString();
			String date=obj[1].toString();
			String name=obj[2].toString();
			Integer click=Integer.parseInt(obj[3]==null?"0":obj[3].toString());
			Integer quantity=Integer.parseInt(obj[4]==null?"0":obj[4].toString());
			Float adsCost=Float.parseFloat(obj[5]==null?"0":obj[5].toString());
			Advertising ads=new Advertising();
			ads.setClicks(click);
			ads.setSameSkuOrdersPlaced(quantity);
			ads.setTotalSpend(adsCost);
			Map<String,Advertising> temp=map.get(name);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(name, temp);
			}
			temp.put(date, ads);
		}
		return map;
	}
	
	//产品 类型 关键字
	public Map<String,Map<String,Advertising>>  findExceptionAdsKeyWord(Advertising advertising){

		//有click无销量关键词，广告成本大于平均成本2倍 关键词 
		String sql="SELECT p.NAME,t.`keyword`,SUM(t.`clicks`),SUM(t.`same_sku_orders_placed`),SUM(t.total_spend)  "+
				" FROM amazoninfo_advertising t "+
				"  ,(SELECT DISTINCT CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.color) END ) NAME,p.sku,p.`product_name` FROM psi_sku p  WHERE  p.`del_flag` = '0' AND p.`country` = :p3 ) p  ";
				if(StringUtils.isNotBlank(advertising.getGroupName())){
				/*	sql+=" join psi_product d on CONCAT(d.brand,' ',d.model)=p.`product_name` and d.del_flag='0' "+
					" JOIN sys_dict tt ON d.type=tt.`value` AND tt.`del_flag`='0' AND  tt.`type`='product_type' "+
					" JOIN psi_product_type_group g ON tt.id=g.`dict_id` "+
					" JOIN psi_product_type_dict pp ON pp.id=g.id  AND pp.`del_flag`='0' ";*/
					sql+=",psi_product d,sys_dict tt,psi_product_type_group g,psi_product_type_dict pp ";
				}		
				sql+="  WHERE  p.sku = t.`sku` AND t.country = :p3 AND  t.`data_date` >= :p1 AND t.`data_date` <= :p2  and t.`clicks`>0 ";
				if(StringUtils.isNotBlank(advertising.getGroupName())){
					sql+=" AND CONCAT(d.brand,' ',d.model)=p.product_name AND d.del_flag='0' AND d.type=tt.`value` AND tt.`del_flag`='0' AND  tt.`type`='product_type' AND tt.id=g.`dict_id` AND pp.id=g.id  AND pp.`del_flag`='0' ";
				}
					if(StringUtils.isNotBlank(advertising.getGroupName())){
				sql+=" and pp.id='"+advertising.getGroupName()+"'";
			}
		       sql+="  GROUP BY NAME,t.`keyword` ";
		       
		Map<String,Map<String,Advertising>> tempMap=Maps.newHashMap();       
		List<Object[]> list = advertisingDao.findBySql(sql, new Parameter(advertising.getCreateDate(),advertising.getDataDate(),advertising.getCountry()));
	    for (Object[] obj: list) {
			String name=obj[0].toString();
			String keyword=obj[1].toString();
			Integer click=Integer.parseInt(obj[2].toString());
			Integer quantity=Integer.parseInt(obj[3].toString());
			Float totalSpend=Float.parseFloat(obj[4].toString());
			Advertising ads=new Advertising();
			ads.setClicks(click);
			ads.setSameSkuOrdersPlaced(quantity);
			ads.setTotalSpend(totalSpend);
			Map<String,Advertising> temp=tempMap.get(name);
			if(temp==null){
				temp=Maps.newHashMap();
				tempMap.put(name, temp);
			}
			temp.put(keyword, ads);
			Advertising tempAds=temp.get("total");
			if(tempAds==null){
				temp.put("total", ads);
			}else{
				Integer tempClick=click+tempAds.getClicks();
				Integer tempQuantity=quantity+tempAds.getSameSkuOrdersPlaced();
				Float tempTotalSpend=totalSpend+tempAds.getTotalSpend();
				tempAds.setClicks(tempClick);
				tempAds.setSameSkuOrdersPlaced(tempQuantity);
				tempAds.setTotalSpend(tempTotalSpend);
			}
		}
		return tempMap;
	}
	
	/**
	 * 分产品统计广告费用
	 * @param saleProfit
	 * @param flag	1:按月  2：按天 3：按周
	 * @return map [日期[产品[国家  数据]]]
	 */
	public Map<String, Map<String, Map<String, SaleProfit>>> getAdFeeList(SaleProfit saleProfit, String flag){
		Map<String, Map<String, Map<String, SaleProfit>>> map = Maps.newLinkedHashMap();
		String temp = "";
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
				&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry()) && !"nonEn".equals(saleProfit.getCountry())) {
			temp = " AND t.`country`='"+saleProfit.getCountry()+"' ";
		} else if ("en".equals(saleProfit.getCountry())) {
			temp = " AND t.`country` in ('com','uk','ca') ";
		} else if ("eu".equals(saleProfit.getCountry())) {
			temp = " AND t.`country` in ('de','uk','fr','it','es') ";
		} else if ("nonEn".equals(saleProfit.getCountry())) {	//非英语国家
			temp = " AND t.`country` in ('de','fr','it','es','jp') ";
		}
		String sql = "";
		Parameter parameter = new Parameter(saleProfit.getDay().replace("-", ""), saleProfit.getEnd().replace("-", ""));
		if (StringUtils.isNotEmpty(flag) && "2".equals(flag)) {	//按月
			sql ="SELECT t.`product_name`,t.`line`,t.`month`, " +
					" SUM(t.`ad_in_event_fee`), SUM(t.`ad_in_profit_fee`),SUM(t.`ad_out_event_fee`),SUM(t.`ad_out_profit_fee`),"+
					" SUM(t.`ad_in_event_sales`), SUM(t.`ad_in_profit_sales`),SUM(t.`ad_out_event_sales`),SUM(t.`ad_out_profit_sales`),"+
					" SUM(t.`ad_in_event_sales_volume`), SUM(t.`ad_in_profit_sales_volume`),SUM(t.`ad_out_event_sales_volume`),SUM(t.`ad_out_profit_sales_volume`),"+
					" SUM(t.`ad_ams_sales`) ,SUM(t.`ad_ams_sales_volume`),SUM(t.`ad_ams_fee`),t.`country`"+
					" FROM `amazoninfo_report_month_type` t WHERE t.`month` >=:p1 AND t.`month` <=:p2 "+temp+" GROUP BY t.`product_name`,t.`month` ORDER BY t.`month`,SUM(t.`sales_volume`) DESC";
		} else if (StringUtils.isNotEmpty(flag) && "1".equals(flag)) {//按周
			Date start = DateUtils.getFirstDayOfWeek(Integer.parseInt(saleProfit.getDay().split("-")[0]), Integer.parseInt(saleProfit.getDay().split("-")[1]));
			Date end = DateUtils.getLastDayOfWeek(Integer.parseInt(saleProfit.getEnd().split("-")[0]), Integer.parseInt(saleProfit.getEnd().split("-")[1]));
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			parameter = new Parameter(format.format(start), format.format(end));
			sql ="SELECT t.`product_name`,t.`line`, DATE_FORMAT(t.`day`,'%x%v'),"+
					" SUM(t.`ad_in_event_fee`), SUM(t.`ad_in_profit_fee`),SUM(t.`ad_out_event_fee`),SUM(t.`ad_out_profit_fee`),"+
					" SUM(t.`ad_in_event_sales`), SUM(t.`ad_in_profit_sales`),SUM(t.`ad_out_event_sales`),SUM(t.`ad_out_profit_sales`),"+
					" SUM(t.`ad_in_event_sales_volume`), SUM(t.`ad_in_profit_sales_volume`),SUM(t.`ad_out_event_sales_volume`),SUM(t.`ad_out_profit_sales_volume`),"+
					" SUM(IFNULL(t.`ad_ams_sales`,0)) ,SUM(IFNULL(t.`ad_ams_sales_volume`,0)),SUM(IFNULL(t.`ad_ams_fee`,0)),t.`country`"+
					" FROM `amazoninfo_sale_profit` t WHERE t.`day` >= :p1 and t.`day` <= :p2  "+
					" GROUP BY t.`product_name`,DATE_FORMAT(t.`day`,'%x%v') ORDER BY t.`day`";
		} else {
			sql ="SELECT t.`product_name`,t.`line`, t.`day`,"+
					" SUM(t.`ad_in_event_fee`), SUM(t.`ad_in_profit_fee`),SUM(t.`ad_out_event_fee`),SUM(t.`ad_out_profit_fee`),"+
					" SUM(t.`ad_in_event_sales`), SUM(t.`ad_in_profit_sales`),SUM(t.`ad_out_event_sales`),SUM(t.`ad_out_profit_sales`),"+
					" SUM(t.`ad_in_event_sales_volume`), SUM(t.`ad_in_profit_sales_volume`),SUM(t.`ad_out_event_sales_volume`),SUM(t.`ad_out_profit_sales_volume`),"+
					" SUM(IFNULL(t.`ad_ams_sales`,0)) ,SUM(IFNULL(t.`ad_ams_sales_volume`,0)),SUM(IFNULL(t.`ad_ams_fee`,0)),t.`country`"+
					" FROM `amazoninfo_sale_profit` t WHERE t.`day` >= :p1 and t.`day` <= :p2  "+
					" GROUP BY t.`product_name`,t.`day` ORDER BY t.`day` ";
		}
		
		List<Object[]> list = advertisingDao.findBySql(sql, parameter);
		for (Object[] objs : list) {
			String productName = objs[0].toString();
			String line =  objs[1]==null?null: objs[1].toString();
			String day = objs[2].toString();
			//站内站外广告费用(站外广告计算时为返点钱减去总花费,故按负数计算)
			Float adInEventFee = objs[3]==null?0:-Float.parseFloat(objs[3].toString());
			Float adInProfitFee = objs[4]==null?0:-Float.parseFloat(objs[4].toString());
			Float adOutEventFee = objs[5]==null?0:Float.parseFloat(objs[5].toString());
			Float adOutProfitFee = objs[6]==null?0:Float.parseFloat(objs[6].toString());
			//站内站外广告销售额
			Float adInEventSales = objs[7]==null?0:Float.parseFloat(objs[7].toString());
			Float adInProfitSales = objs[8]==null?0:Float.parseFloat(objs[8].toString());
			Float adOutEventSales = objs[9]==null?0:Float.parseFloat(objs[9].toString());
			Float adOutProfitSales = objs[10]==null?0:Float.parseFloat(objs[10].toString());
			//站内站外广告销量
			Integer adInEventSalesVolume = objs[11]==null?0:Integer.parseInt(objs[11].toString());
			Integer adInProfitSalesVolume = objs[12]==null?0:Integer.parseInt(objs[12].toString());
			Integer adOutEventSalesVolume = objs[13]==null?0:Integer.parseInt(objs[13].toString());
			Integer adOutProfitSalesVolume = objs[14]==null?0:Integer.parseInt(objs[14].toString());
			//AMS广告
			Float adAmsSales = objs[15]==null?0:Float.parseFloat(objs[15].toString());
			Integer adAmsSalesVolume = objs[16]==null?0:Integer.parseInt(objs[16].toString());
			Float adAmsFee = objs[17]==null?0:-Float.parseFloat(objs[17].toString());
			String country =  objs[18].toString();

			Map<String, Map<String, SaleProfit>> dayMap = map.get(day);
			if (dayMap == null) {
				dayMap = Maps.newHashMap();
				map.put(day, dayMap);
			}
			Map<String, SaleProfit> productMap = dayMap.get(productName);
			if (productMap == null) {
				productMap = Maps.newHashMap();
				dayMap.put(productName, productMap);
			}
			SaleProfit profit = new SaleProfit();
			productMap.put(country, profit);
			
			profit.setProductName(productName);
			profit.setLine(line);
			profit.setDay(day);
			profit.setAdInEventFee(adInEventFee);
			profit.setAdInProfitFee(adInProfitFee);
			profit.setAdOutEventFee(adOutEventFee);
			profit.setAdOutProfitFee(adOutProfitFee);
			profit.setAdInEventSales(adInEventSales);
			profit.setAdInProfitSales(adInProfitSales);
			profit.setAdOutEventSales(adOutEventSales);
			profit.setAdOutProfitSales(adOutProfitSales);
			profit.setAdInEventSalesVolume(adInEventSalesVolume);
			profit.setAdInProfitSalesVolume(adInProfitSalesVolume);
			profit.setAdOutEventSalesVolume(adOutEventSalesVolume);
			profit.setAdOutProfitSalesVolume(adOutProfitSalesVolume);
			profit.setAdAmsSales(adAmsSales);
			profit.setAdAmsSalesVolume(adAmsSalesVolume);
			profit.setAdAmsFee(adAmsFee);

			SaleProfit totalProfit = productMap.get("total");
			if (totalProfit == null) {
				totalProfit = new SaleProfit();
				totalProfit.setLine(line);
				totalProfit.setAdInEventFee(0f);
				totalProfit.setAdInProfitFee(0f);
				totalProfit.setAdOutEventFee(0f);
				totalProfit.setAdOutProfitFee(0f);
				totalProfit.setAdInEventSales(0f);
				totalProfit.setAdInProfitSales(0f);
				totalProfit.setAdOutEventSales(0f);
				totalProfit.setAdOutProfitSales(0f);
				totalProfit.setAdInEventSalesVolume(0);
				totalProfit.setAdInProfitSalesVolume(0);
				totalProfit.setAdOutEventSalesVolume(0);
				totalProfit.setAdOutProfitSalesVolume(0);
				totalProfit.setAdAmsFee(0f);
				totalProfit.setAdAmsSales(0f);
				totalProfit.setAdAmsSalesVolume(0);
				productMap.put("total", totalProfit);
			}
			totalProfit.setAdInEventFee(totalProfit.getAdInEventFee() + adInEventFee);
			totalProfit.setAdInProfitFee(totalProfit.getAdInProfitFee() + adInProfitFee);
			totalProfit.setAdOutEventFee(totalProfit.getAdOutEventFee() + adOutEventFee);
			totalProfit.setAdOutProfitFee(totalProfit.getAdOutProfitFee() + adOutProfitFee);
			totalProfit.setAdInEventSales(totalProfit.getAdInEventSales() + adInEventSales);
			totalProfit.setAdInProfitSales(totalProfit.getAdInProfitSales() + adInProfitSales);
			totalProfit.setAdOutEventSales(totalProfit.getAdOutEventSales() + adOutEventSales);
			totalProfit.setAdOutProfitSales(totalProfit.getAdOutProfitSales() + adOutProfitSales);
			totalProfit.setAdInEventSalesVolume(totalProfit.getAdInEventSalesVolume() + adInEventSalesVolume);
			totalProfit.setAdInProfitSalesVolume(totalProfit.getAdInProfitSalesVolume() + adInProfitSalesVolume);
			totalProfit.setAdOutEventSalesVolume(totalProfit.getAdOutEventSalesVolume() + adOutEventSalesVolume);
			totalProfit.setAdOutProfitSalesVolume(totalProfit.getAdOutProfitSalesVolume() + adOutProfitSalesVolume);
			totalProfit.setAdAmsSales(totalProfit.getAdAmsSales() + adAmsSales);
			totalProfit.setAdAmsSalesVolume(totalProfit.getAdAmsSalesVolume() + adAmsSalesVolume);
			totalProfit.setAdAmsFee(totalProfit.getAdAmsFee() + adAmsFee);
		}
		return map;
	}
	
	public Map<String,Float> findRate(){
		String sql="SELECT country,NAME,group_name,sku,round(SUM(s.`same_sku_orders_placed`)/SUM(s.`clicks`),3) FROM amazoninfo_advertising s "+
			" WHERE s.`same_sku_orders_placed`>0  and s.`data_date`>=:p1  and s.`data_date`<=:p2  "+//
			" GROUP BY country,NAME,group_name,sku ";
		Date date=DateUtils.addMonths(new Date(),-1);
		List<Object[]> list = advertisingDao.findBySql(sql, new Parameter(DateUtils.getFirstDayOfMonth(date),DateUtils.getLastDayOfMonth(date)));//
		Map<String,Float> map=Maps.newHashMap();
		for (Object[] obj: list) {
			if(obj[4]!=null){
				String country=obj[0].toString();
				String name=obj[1].toString();
				String groupName=obj[2].toString();
				String sku=obj[3].toString();
				Float conver=Float.parseFloat(obj[4].toString());
				map.put(country+name+groupName+sku,conver);
			}
		}
		return map;
	}
	
	public Map<String,Map<String,Float>> findRate(Advertising advertising){
		String sql="SELECT country,NAME,group_name,sku,round(SUM(s.`same_sku_orders_placed`)/SUM(s.`clicks`),3),DATE_FORMAT(s.`data_date`,'%Y%m') dates FROM amazoninfo_advertising s "+
			" WHERE s.`same_sku_orders_placed`>0  and s.`data_date`>=:p1  and s.`data_date`<=:p2  "+//
			" GROUP BY country,NAME,group_name,sku,dates ";
		List<Object[]> list = advertisingDao.findBySql(sql, new Parameter(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(advertising.getCreateDate(),-1)),DateUtils.getLastDayOfMonth(DateUtils.addMonths(advertising.getDataDate(),-1))));//
		Map<String,Map<String,Float>> map=Maps.newHashMap();
		for (Object[] obj: list) {
			if(obj[4]!=null){
				String country=obj[0].toString();
				String name=obj[1].toString();
				String groupName=obj[2].toString();
				String sku=obj[3].toString();
				Float conver=Float.parseFloat(obj[4].toString());
				String month=obj[5].toString();
				//map.put(country+name+groupName+sku,conver);
				Map<String,Float> temp=map.get(month);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(month, temp);
				}
				temp.put(country+name+groupName+sku,conver);
			}
		}
		return map;
	}
	
	
	public Map<String,List<Advertising>> findWeekAdvertising(Set<String> weekSet){
		String sql="SELECT country,NAME,group_name,sku,SUM(s.`total_spend`),SUM(s.`clicks`) clicks,SUM(s.`week_same_sku_units_ordered`), "+
			    " ROUND(SUM(s.`total_spend`)/SUM(s.`week_same_sku_units_ordered`),3),ROUND(SUM(s.`week_same_sku_units_ordered`)/SUM(s.`clicks`),3) "+
				" FROM amazoninfo_advertising_week s  WHERE  WEEK IN :p1 "+
				" GROUP BY country,NAME,group_name,sku HAVING clicks>=200 ";
		List<Object[]> list = advertisingDao.findBySql(sql, new Parameter(weekSet));	
		Map<String,List<Advertising>> map=Maps.newHashMap();
		for (Object[] obj: list) {
			String country=obj[0].toString();
			String name=obj[1].toString();
			String groupName=obj[2].toString();
			String sku=obj[3].toString();
			Float spend=Float.parseFloat(obj[4].toString());
			Integer clicks=Integer.parseInt(obj[5].toString());
			Integer order=Integer.parseInt(obj[6].toString());
			Float costOrder=Float.parseFloat(obj[7].toString());
			Float convAds=Float.parseFloat(obj[8].toString());
			Advertising adv=new Advertising();
			adv.setCountry(country);
			adv.setName(name);
			adv.setGroupName(groupName);
			adv.setSku(sku);
			adv.setTotalSpend(spend);
			adv.setClicks(clicks);
			adv.setSameSkuOrdersPlaced(order);
			adv.setSameSkuOrderSales(costOrder);
			adv.setConversion(convAds);
			List<Advertising> temp=map.get(country);
			if(temp==null){
				temp=Lists.newArrayList();
				map.put(country, temp);
			}
			temp.add(adv);
		}
		return map;
	}
	
	
	public Map<String,Map<String,Advertising>> findWeekAdvertising2(List<String> weekSet,String country1,String groupName1){
		String sql="SELECT country,NAME,group_name,sku,SUM(s.`total_spend`),SUM(s.`clicks`) clicks,SUM(s.`week_same_sku_units_ordered`),WEEK,SUM(week_same_sku_units_sales+week_other_sku_units_sales) "+
				" FROM amazoninfo_advertising_week s WHERE  WEEK IN :p1  ";//
		if(StringUtils.isNotBlank(country1)){
			sql+=" and country='"+country1+"' ";
		}
		if(StringUtils.isNotBlank(groupName1)){
			sql+=" and group_name='"+groupName1+"' ";
		}
			sql+=" GROUP BY country,NAME,group_name,sku,WEEK HAVING clicks>0 ";
		List<Object[]> list = advertisingDao.findBySql(sql, new Parameter(weekSet));	//
		Map<String,Map<String,Advertising>> map=Maps.newHashMap();
		for (Object[] obj: list) {
			String country=obj[0].toString();
			String name=obj[1].toString();
			String groupName=obj[2].toString();
			String sku=obj[3].toString();
			Float spend=Float.parseFloat(obj[4].toString());
			Integer clicks=Integer.parseInt(obj[5].toString());
			Integer order=Integer.parseInt(obj[6].toString());
			String week=obj[7].toString();
			Float sales=Float.parseFloat(obj[8].toString());
			Advertising adv=new Advertising();
			adv.setCountry(country);
			adv.setName(name);
			adv.setGroupName(groupName);
			adv.setSku(sku);
			adv.setTotalSpend(spend);
			adv.setClicks(clicks);
			adv.setSameSkuOrdersPlaced(order);
			adv.setKeyword(week);
			adv.setSameSkuOrderSales(sales);
			String key=country+name+groupName+sku;
			Map<String,Advertising> temp=map.get(key);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(key, temp);
			}
			temp.put(week, adv);
		}
		return map;
	}
	
	//[产品名	广告数据]
	public Map<String, Advertising> countByProduct(Advertising advertising, Map<String, String> skuNameMap){
		Map<String, Advertising> rs = Maps.newLinkedHashMap();
		String searchSql = "";
		if(StringUtils.isNotEmpty(advertising.getKeyword()) && advertising.getKeyword().trim().length()>0){
			searchSql = "and ( t.group_name like '%"+advertising.getKeyword()+"%' or t.keyword like '%"+advertising.getKeyword()+"%' or t.sku like '%"+advertising.getKeyword()+"%' )";
		}
		
		String sql = "SELECT t.`sku`,SUM(t.`impressions`),SUM(t.`clicks`),SUM(t.`total_spend`),"+
				" SUM(t.`same_sku_order_sales`),SUM(t.`same_sku_orders_placed`),SUM(t.`other_sku_order_sales`),SUM(t.`other_sku_orders_placed`)"+
				" FROM `amazoninfo_advertising` t WHERE t.`country`=:p1 AND t.`data_date`>=:p2 AND t.`data_date`<:p3 "+searchSql+"  GROUP BY t.`sku` ";
		List<Object[]> list = advertisingDao.findBySql(sql, new Parameter(advertising.getCountry(),advertising.getCreateDate(), DateUtils.addDays(advertising.getDataDate(), 1)));
		for (Object[] objects : list) {
			if (objects[0] == null) {
				continue;
			}
			String sku = objects[0].toString();
			String productName = skuNameMap.get(sku);
			if (StringUtils.isEmpty(productName)) {
				continue;
			}
			Integer impressions = objects[1]==null?0:Integer.parseInt(objects[1].toString());
			Integer clicks = objects[2]==null?0:Integer.parseInt(objects[2].toString());
			float totalSpend = objects[3]==null?0f:Float.parseFloat(objects[3].toString());
			float sameSales = objects[4]==null?0f:Float.parseFloat(objects[4].toString());
			Integer sameOrders = objects[5]==null?0:Integer.parseInt(objects[5].toString());
			float otherSales = objects[6]==null?0f:Float.parseFloat(objects[6].toString());
			Integer otherOrders = objects[7]==null?0:Integer.parseInt(objects[7].toString());
			Advertising ad = rs.get(productName);
			if (ad == null) {
				ad = new Advertising();
				ad.setImpressions(0);
				ad.setClicks(0);
				ad.setTotalSpend(0f);
				ad.setSameSkuOrderSales(0f);
				ad.setSameSkuOrdersPlaced(0);
				ad.setOtherSkuOrderSales(0f);
				ad.setOtherSkuOrdersPlaced(0);
				rs.put(productName, ad);
			}
			ad.setImpressions(ad.getImpressions() + impressions);
			ad.setClicks(ad.getClicks() + clicks);
			ad.setTotalSpend(ad.getTotalSpend() + totalSpend);
			ad.setSameSkuOrderSales(ad.getSameSkuOrderSales() + sameSales);
			ad.setSameSkuOrdersPlaced(ad.getSameSkuOrdersPlaced() + sameOrders);
			ad.setOtherSkuOrderSales(ad.getOtherSkuOrderSales() + otherSales);
			ad.setOtherSkuOrdersPlaced(ad.getOtherSkuOrdersPlaced() + otherOrders);
		}
		return rs;
	}
	
	//[产品名	session数据]
	public Map<String, BusinessReport> countSessionByProduct(Advertising advertising, Map<String, String> asinNameMap){
		Map<String, BusinessReport> rs = Maps.newLinkedHashMap();
		
		String sql = "SELECT t.`child_asin`,SUM(t.`sessions`),SUM(t.`orders_placed`) "+
				" FROM `amazoninfo_business_report` t WHERE t.`country`=:p1 AND t.`data_date`>=:p2 AND t.`data_date`<:p3  GROUP BY t.`child_asin`";
		List<Object[]> list = advertisingDao.findBySql(sql, new Parameter(advertising.getCountry(),advertising.getCreateDate(), DateUtils.addDays(advertising.getDataDate(), 1)));
		for (Object[] objects : list) {
			if (objects[0] == null) {
				continue;
			}
			String childAsin = objects[0].toString();
			String productName = asinNameMap.get(childAsin);
			if (StringUtils.isEmpty(productName)) {
				continue;
			}
			Integer sessions = objects[1]==null?0:Integer.parseInt(objects[1].toString());
			Integer ordersPlaced = objects[2]==null?0:Integer.parseInt(objects[2].toString());
			
			BusinessReport report = rs.get(productName);
			if (report == null) {
				report = new BusinessReport();
				report.setSessions(0);
				report.setOrdersPlaced(0);
				rs.put(productName, report);
			}
			report.setSessions(report.getSessions() + sessions);
			report.setOrdersPlaced(report.getOrdersPlaced() + ordersPlaced);
		}
		return rs;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		/*ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AdvertisingService advertisingService = applicationContext.getBean(AdvertisingService.class);
		
		
		advertisingService.updateAdvertsingByWeek();
		applicationContext.close();*/
		System.out.println(DateUtils.getWeekOfYear(new Date()));
		
	}
	
	public List<Advertising> findAdvertising(){
	    //查询出所有计划打广告的产品
	    String sql = "SELECT p1.product_name,p1.color_code,p2.country_code "
	                +" FROM psi_marketing_plan_item p1,psi_marketing_plan p2 WHERE p1.marketing_plan_id=p2.id ";
	    List<Object[]> findBySql = advertisingDao.findBySql(sql);
	    StringBuilder content= new StringBuilder();
	    int i=0;
	    for(Object[] objs : findBySql){
	        if(objs[0] != null){
	        String productName = objs[0].toString();
	        String color = objs[1]!=null ? objs[1].toString():"";
	        String country = objs[2]!=null ? objs[2].toString():"";
	        //去查是否打了广告
	         String advFlag = "  SELECT sku.sku FROM psi_sku sku,amazoninfo_advertising adv WHERE sku.sku=adv.sku AND del_flag='0' "
                      +" AND sku.country=:p1 AND sku.product_name=:p2";
	        List<Object[]> advFlagList = advertisingDao.findBySql(advFlag,new Parameter(country,productName));
    	        if(advFlagList.size()!=0){//该产品正在打广告
    	            //查该产品的最低价的sku
    	            String minAll = " SELECT DISTINCT(sku),country FROM amazoninfo_product2 WHERE "
                                      +"  price =(SELECT MIN(price) FROM amazoninfo_product2 WHERE sku IN("
                                      +"  SELECT sku FROM psi_sku WHERE product_name=:p1 AND country=:p2 and del_flag='0') AND active='1') AND sku IN"
                                      +"   (SELECT sku FROM psi_sku WHERE product_name=:p3 AND country=:p4 and del_flag='0') AND country=:p5";
    	            List<Object[]> minAllList = advertisingDao.findBySql(minAll,new Parameter(productName,country,productName,country,country));
    	            for(Object[] obj : minAllList){//查询最低价的sku是否在广告中
	                    String skuExistInAdv = "SELECT s.sku FROM psi_sku s,amazoninfo_advertising a " 
	                    		              + "WHERE s.del_flag='0' and s.sku=a.sku AND a.sku=:p1  AND s.country=:p2 ";
	                    List<Object[]> skuExistInAdvList = advertisingDao.findBySql(skuExistInAdv,new Parameter(obj[0].toString(),country));
	                    if(skuExistInAdvList.size()==0){//不在广告中，通知 产品、sku、国家、颜色
	                       String tell = "SELECT sku,product_name,country,color FROM psi_sku WHERE sku=:p1 AND country=:p2 and del_flag='0'";
	                       List<Object[]> tellList = advertisingDao.findBySql(tell,new Parameter(obj[0].toString(),country));
	                       for(Object[] ob:tellList){
	                           i+=1;
	                           String tellSku=ob[0]!=null?ob[0].toString():"";
                               String tellName=ob[1]!=null?ob[1].toString():"";
                               String tellCountry=ob[2]!=null?ob[2].toString():"";
                               String tellColor=ob[3]!=null?ob[3].toString():"";
                               content.append("产品名称："+tellName);
                               if(tellColor!=""){
                                   content.append("_");
                               }
                               content.append( " "+tellColor+" sku:"+tellSku + " 国家:"+tellCountry + "\n");
	                       }
	                    }
	                }
    	        }else{ // 该产品还未打广告
    	            
    	        }
	        }
	    }
	    System.out.println(content);
	    return null;
	}
	
	//12-01 (12-03 - 12-02)
	public Map<String,Set<String>> findSearchTermByDate(){
		Map<String,Set<String>>  map=Maps.newHashMap();
		String sql="SELECT DISTINCT country,DATE_FORMAT(update_time,'%Y-%m-%d')  FROM amazoninfo_search_term_day_report t WHERE t.`update_time`>=:p1 ";
		List<Object[]> list=amazonSearchTermReportDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(new Date(),-6))));
		for (Object[] obj: list) {
			Set<String> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Sets.newHashSet();
				map.put(obj[0].toString(),temp);
			}
			temp.add(obj[1].toString());
		}
 		return map;
	}
	
	//12-07(05)  12-06(04)  
	@Transactional(readOnly = false)
	public void saveDayDate(String date,String country) throws ParseException{
		List<Object[]>  list=Lists.newArrayList();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		
		String sql="SELECT update_time,count(1) num  FROM amazoninfo_search_term_report t WHERE t.`update_time` in (:p1,:p2) and country=:p3 group by t.`update_time` HAVING num>0 ";
		Date dd=dateFormat.parse(date);//12-01
		if("jp".equals(country)){
			list=amazonSearchTermReportDao.findBySql(sql,new Parameter(DateUtils.addDays(dd,1),dd,country));
		}else{
			list=amazonSearchTermReportDao.findBySql(sql,new Parameter(DateUtils.addDays(dd,1),DateUtils.addDays(dd,2),country));
		}
		if(list.size()==2){
			String daySql="Insert into amazoninfo_search_term_day_report(country,campaign_name,ad_group_name,customer_search_term,keyword,match_type,impressions,clicks,total_spend,same_sku,update_time)  "+
		              " SELECT aa.country,aa.campaign_name,aa.ad_group_name,aa.customer_search_term,aa.keyword, aa.match_type,  "+
                      " (aa.impressions - bb.impressions) AS impressions,(aa.clicks - bb.clicks),(aa.total_spend - bb.total_spend), (aa.same_sku - bb.same_sku),aa.end_date  "+
                      " FROM (SELECT * FROM amazoninfo_search_term_report a  "+
                      "   WHERE a.`update_time` = :p1  AND a.end_date =:p3 AND a.country =:p5  AND a.impressions > 0) aa, "+
                      "   (SELECT *  FROM amazoninfo_search_term_report a  "+
                      "   WHERE a.`update_time` = :p2  AND a.end_date =:p4  AND a.country =:p5  ) bb  "+
                      " 	WHERE aa.country = bb.country  AND aa.ad_group_name = bb.ad_group_name  AND aa.campaign_name = bb.campaign_name  "+
                      " 	  AND BINARY(aa.customer_search_term) = BINARY(bb.customer_search_term)  AND aa.start_date=bb.start_date "+
                      "   AND BINARY(aa.keyword) = BINARY(bb.keyword)  AND aa.match_type = bb.match_type ";
			
			if("jp".equals(country)){//12-02 12-01 
				amazonSearchTermReportDao.updateBySql(daySql,new Parameter(DateUtils.addDays(dd,1),dd,dd,DateUtils.addDays(dd,-1),country));
			}else{//12-03 12-02 12-01
				amazonSearchTermReportDao.updateBySql(daySql,new Parameter(DateUtils.addDays(dd,2),DateUtils.addDays(dd,1),dd,DateUtils.addDays(dd,-1),country));
			}
		}
	}
	
	public Map<String,Map<String,List<Advertising>>> findAdvertisingByCountry(String country){
		Map<String,Map<String,List<Advertising>>> map=Maps.newHashMap();
		String sql="SELECT MAX(a.`data_date`) FROM amazoninfo_advertising AS a where a.`data_date`>=:p1 and country=:p2";
		List<Object> rs=advertisingDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-5),country));
		if(rs!=null&&rs.size()>0&&rs.get(0)!=null){
			Date date=(Timestamp)rs.get(0);	
			String advSql="SELECT g.name,g.`group_name`,g.`sku`,g.`keyword`,g.id FROM amazoninfo_advertising g WHERE g.`data_date`=:p1 and g.`country`=:p2 and name_status is null ";
			List<Object[]> list=advertisingDao.findBySql(advSql,new Parameter(date,country));
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					Advertising adv=new Advertising();
					adv.setName(obj[0].toString());
					adv.setGroupName(obj[1].toString());
					adv.setSku(obj[2].toString());
					adv.setKeyword(obj[3].toString());
					adv.setId(obj[4].toString());
					Map<String,List<Advertising>> temp=map.get(obj[0].toString());
					if(temp==null){
						temp=Maps.newHashMap();
						map.put(obj[0].toString(),temp);
					}
					List<Advertising> advList=temp.get(obj[1].toString());
					if(advList==null){
						advList=Lists.newArrayList();
						temp.put(obj[1].toString(),advList);
					}
					advList.add(adv);
				}
			}
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void updateAdvNameStatu(List<String> runningNameList,String country){
		String sql="SELECT MAX(a.`data_date`) FROM amazoninfo_advertising AS a where a.`data_date`>=:p1 and country=:p2";
		List<Object> rs=advertisingDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-5),country));
		if(rs!=null&&rs.size()>0&&rs.get(0)!=null){
			Date date=(Timestamp)rs.get(0);	
			String updateSql="update amazoninfo_advertising set name_status='Running' where `data_date`=:p1 and country=:p2 and name in :p3";
			advertisingDao.updateBySql(updateSql, new Parameter(date,country,runningNameList));
		}	
		
	}
	
	@Transactional(readOnly = false)
	public void updateAdvGroupNameStatu(Map<String,String> runningGroupNameMap,String country){
		String sql="SELECT MAX(a.`data_date`) FROM amazoninfo_advertising AS a where a.`data_date`>=:p1 and country=:p2";
		List<Object> rs=advertisingDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-5),country));
		if(rs!=null&&rs.size()>0&&rs.get(0)!=null){
			Date date=(Timestamp)rs.get(0);	
			String updateSql="update amazoninfo_advertising set group_name_status=:p1 where `data_date`=:p2 and country=:p3 and name=:p4 and group_name=:p5 ";
			for (Entry<String, String> entry: runningGroupNameMap.entrySet()) {
				 String[] arr=entry.getKey().split("==");
				 advertisingDao.updateBySql(updateSql, new Parameter(entry.getValue(),date,country,arr[0],arr[1]));
			}
		}	
		
	}
	
	@Transactional(readOnly = false)
	public void updateAdvKeywordNameStatu(Map<String,String> keywordMap,String country){
		String sql="SELECT MAX(a.`data_date`) FROM amazoninfo_advertising AS a where a.`data_date`>=:p1 and country=:p2";
		List<Object> rs=advertisingDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-5),country));
		if(rs!=null&&rs.size()>0&&rs.get(0)!=null){
			Date date=(Timestamp)rs.get(0);	
			String updateSql="update amazoninfo_advertising set keyword_status=:p1 where `data_date`=:p2 and country=:p3 and name=:p4 and group_name=:p5 and keyword=:p6 ";
			for (Entry<String, String> entry: keywordMap.entrySet()) {
				 String[] arr=entry.getKey().split("==");
				 advertisingDao.updateBySql(updateSql, new Parameter(entry.getValue(),date,country,arr[0],arr[1],arr[2]));
			}
		}	
	}
	
	@Transactional(readOnly = false)
	public void updateNegativeKey(Map<String,Set<String>> negativeMap,String country){
		String sql="insert into amazoninfo_advertising_negative(country,name,group_name,keyword,data_date) values(:p1,:p2,:p3,:p4,:p5)";
		Date today=new Date();
		for (Map.Entry<String,Set<String>>  entry: negativeMap.entrySet()) {
			String[] arr=entry.getKey().split("==");
			Set<String> sets=entry.getValue();
			for (String keyword: sets) {
				advertisingDao.updateBySql(sql, new Parameter(country,arr[0],arr[1],keyword,today));
			}
		}
	}
	
	  public List<Object[]> priceExport(String country,String date, String keyword) {
	        List<Object[]> findBySql = Lists.newArrayList();
	        String sql ="SELECT *FROM amazoninfo_search_term_day_report WHERE update_time=:p1 AND country=:p2";
	        if(StringUtils.isNotBlank(keyword)){
	            sql += " and keyword like '%"+keyword+"%'";
	            findBySql = amazonSearchTermReportDao.findBySql(sql,new Parameter(date, country));
	        }else{
	            findBySql = amazonSearchTermReportDao.findBySql(sql,new Parameter(date, country));
	        }
	        return findBySql;
	  }
	  
	  public List<Object[]> findNegativeKeyword(String country){
		  String sql="SELECT MAX(a.`data_date`) FROM amazoninfo_advertising_negative AS a where country=:p1";
		  List<Object> rs=advertisingDao.findBySql(sql,new Parameter(country));
		  if(rs!=null&&rs.size()>0&&rs.get(0)!=null){
				Date date=(Date)rs.get(0);	
			    String keySql="select name,group_name,keyword from amazoninfo_advertising_negative  where data_date=:p1 and country=:p2 order by name,group_name";
			    return advertisingDao.findBySql(keySql,new Parameter(date,country));
		  }		
		  return null;
	  }
}
