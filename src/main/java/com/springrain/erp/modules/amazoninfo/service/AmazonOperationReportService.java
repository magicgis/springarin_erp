/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonOperationalReportDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonSpreadReportDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonOperationalReport;
import com.springrain.erp.modules.amazoninfo.entity.AmazonSpreadReport;

@Component
@Transactional(readOnly = true)
public class AmazonOperationReportService extends BaseService {

	@Autowired
	private AmazonOperationalReportDao amazonOperationalReportDao;
	
	@Autowired
	private AmazonSpreadReportDao amazonSpreadReportDao;
	
	public AmazonOperationalReport get(Integer id) {
		return amazonOperationalReportDao.get(id);
	}
	@Autowired
	private ProductPriceService productPriceService;
	
	@Transactional(readOnly = false)
	public void save(AmazonOperationalReport amazonOperationalReport) {
		amazonOperationalReportDao.save(amazonOperationalReport);
	}
	
	
	
	//国家/日期/产品类型/产品
	public Map<String,Map<String,Map<String,Map<String,AmazonOperationalReport>>>> findAllCountry(AmazonOperationalReport amazonOperationalReport) {
		Map<String,Map<String,Map<String,Map<String,AmazonOperationalReport>>>> map=Maps.newLinkedHashMap();
		Date start = amazonOperationalReport.getCreateDate();
		Date end = amazonOperationalReport.getEndDate();
		String typeSql = "'%Y%m%d'";
		if("2".equals(amazonOperationalReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				amazonOperationalReport.setCreateDate(start);
				amazonOperationalReport.setEndDate(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					amazonOperationalReport.setEndDate(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(amazonOperationalReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				amazonOperationalReport.setCreateDate(start);
				amazonOperationalReport.setEndDate(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					amazonOperationalReport.setEndDate(end1);
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
				amazonOperationalReport.setCreateDate(start);
				amazonOperationalReport.setEndDate(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					amazonOperationalReport.setEndDate(end1);
				}
			}
		}
		
		String sql="SELECT country,DATE_FORMAT(a.`create_date`,"+typeSql+") dates,product_name,SUM(SESSION),SUM(sales_volume),SUM(return_volume),SUM(order_volume) "+
				 " ,TRUNCATE(Sum(CASE WHEN a.`country`='com' THEN a.`sales`*"
					+AmazonProduct2Service.getRateConfig().get("USD/EUR")+" WHEN a.`country`='uk' THEN a.`sales`*"+AmazonProduct2Service.getRateConfig().get("GBP/EUR")+" WHEN a.`country`='ca' THEN a.`sales`*"
				+AmazonProduct2Service.getRateConfig().get("CAD/EUR")+" WHEN a.`country`='jp' THEN a.`sales`*"+
					AmazonProduct2Service.getRateConfig().get("JPY/EUR")+" WHEN a.`country`='mx' THEN a.`sales`*"+AmazonProduct2Service.getRateConfig().get("MXN/EUR")+" ELSE a.`sales` END ),2), "+
				
			  "SUM(bad_review),SUM(total_review),sum(session_order) "+
              " FROM amazoninfo_operational_report a  where a.create_date>=:p1 and a.create_date<=:p2  GROUP BY country,dates,product_name order by country,dates ";
		List<Object[]> list = amazonOperationalReportDao.findBySql(sql, new Parameter(start,end));
		
		Map<String,String> typeMap=getProductNameAndType();
		for (Object[] obj: list) {
			String country=obj[0].toString();
			String date = obj[1].toString(); 
			if("2".equals(amazonOperationalReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			String name=obj[2].toString();
			Integer session=Integer.parseInt(obj[3]==null?"0":obj[3].toString());
			Integer salesVolume=Integer.parseInt(obj[4]==null?"0":obj[4].toString());
			Integer returnVolume=Integer.parseInt(obj[5]==null?"0":obj[5].toString());
			Integer orderVolume=Integer.parseInt(obj[6]==null?"0":obj[6].toString());
			float sales=Float.parseFloat(obj[7]==null?"0":obj[7].toString());
			Integer badReview=Integer.parseInt(obj[8]==null?"0":obj[8].toString());
			Integer totalReview=Integer.parseInt(obj[9]==null?"0":obj[9].toString());
			String type=typeMap.get(name);
			if(StringUtils.isBlank(type)){
				continue;
			}
			Integer sessionOrder=Integer.parseInt(obj[10]==null?"0":obj[10].toString());
			Map<String,Map<String,Map<String,AmazonOperationalReport>>> temp=map.get(country);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(country, temp);
			}
			
			Map<String,Map<String,AmazonOperationalReport>> dateTemp=temp.get(date);
			if(dateTemp==null){
				dateTemp=Maps.newLinkedHashMap();
				temp.put(date, dateTemp);
			}
			
			Map<String,AmazonOperationalReport> typeTemp=dateTemp.get(type);
			if(typeTemp==null){
				typeTemp=Maps.newLinkedHashMap();
				dateTemp.put(type, typeTemp);
			}
			typeTemp.put(name, new AmazonOperationalReport(country,session,salesVolume,returnVolume,orderVolume,sales,badReview,totalReview,sessionOrder));
		
			Map<String,Map<String,Map<String,AmazonOperationalReport>>> totalTemp=map.get("total");
			if(totalTemp==null){
				totalTemp=Maps.newLinkedHashMap();
				map.put("total", totalTemp);
			}
			
			Map<String,Map<String,AmazonOperationalReport>> totalDateTemp=totalTemp.get(date);
			if(totalDateTemp==null){
				totalDateTemp=Maps.newLinkedHashMap();
				totalTemp.put(date, totalDateTemp);
			}
			
			Map<String,AmazonOperationalReport> totalTypeTemp=totalDateTemp.get(type);
			if(totalTypeTemp==null){
				totalTypeTemp=Maps.newLinkedHashMap();
				totalDateTemp.put(type, totalTypeTemp);
			}
			AmazonOperationalReport rp=totalTypeTemp.get(name);
			if(rp==null){
				totalTypeTemp.put(name, new AmazonOperationalReport(country,session,salesVolume,returnVolume,orderVolume,sales,badReview,totalReview,sessionOrder));
			}else{
				totalTypeTemp.put(name, new AmazonOperationalReport(country,(rp.getSession()==null?0:rp.getSession())+session,(rp.getSalesVolume()==null?0:rp.getSalesVolume())+salesVolume,
						(rp.getReturnVolume()==null?0:rp.getReturnVolume())+returnVolume,(rp.getOrderVolume()==null?0:rp.getOrderVolume())+orderVolume,
						rp.getSales()+sales,(rp.getBadReview()==null?0:rp.getBadReview())+badReview,(rp.getTotalReview()==null?0:rp.getTotalReview())+totalReview,(rp.getSessionOrder()==null?0:rp.getSessionOrder())+sessionOrder));
			}
		    
		}
		return map;
	}
	
	public Map<String,String> getProductNameAndType(){
		String sql = "SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName "+
					" , a.type FROM psi_product a JOIN mysql.help_topic b "+
					" ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1)";
		List<Object[]> list = this.amazonOperationalReportDao.findBySql(sql);
		Map<String,String>  rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(),objects[1].toString());
		}
		return rs;
	}
	
	//
	public Map<String, Map<String,Map<String,AmazonOperationalReport>>> getOrderType(AmazonOperationalReport amazonOperationalReport){
		Date start = amazonOperationalReport.getCreateDate();
		Date end = amazonOperationalReport.getEndDate();
		String typeSql = "'%Y%m%d'";
		if("2".equals(amazonOperationalReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				amazonOperationalReport.setCreateDate(start);
				amazonOperationalReport.setEndDate(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					amazonOperationalReport.setEndDate(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(amazonOperationalReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				amazonOperationalReport.setCreateDate(start);
				amazonOperationalReport.setEndDate(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					amazonOperationalReport.setEndDate(end1);
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
				amazonOperationalReport.setCreateDate(start);
				amazonOperationalReport.setEndDate(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					amazonOperationalReport.setEndDate(end1);
				}
			}
		}
		List<Object[]> list = null;
		String sql ="SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`) AS pname,a.`country`,max(max_order) tempOrder,DATE_FORMAT(a.`date`,"+typeSql+") dates,sum(promotions_order),sum(flash_sales_order) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and a.`date`>=:p1 AND a.`date`<=:p2 and product_name is not null  GROUP BY a.`country`,dates,pname";
		list = amazonOperationalReportDao.findBySql(sql, new Parameter(start,end));
		Map<String, Map<String,Map<String,AmazonOperationalReport>>> rs = Maps.newHashMap();
		for (Object[] obj: list) {
			String name=obj[0].toString();
			String country=obj[1].toString();
			Integer maxOrder=Integer.parseInt(obj[2]==null?"0":obj[2].toString());
			String date=obj[3].toString();
			if("2".equals(amazonOperationalReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			Integer promotionsOrder=Integer.parseInt(obj[4]==null?"0":obj[4].toString());
			Integer flashOrder=Integer.parseInt(obj[5]==null?"0":obj[5].toString());
			
			Map<String,Map<String,AmazonOperationalReport>> map=rs.get(date);
			if(map==null){
				map=Maps.newHashMap();
				rs.put(date,map);
			}
			Map<String,AmazonOperationalReport> temp=map.get(country);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(country,temp);
			}
			temp.put(name, new AmazonOperationalReport(maxOrder,promotionsOrder,flashOrder));
			
			Map<String,AmazonOperationalReport> allTemp=map.get("total");
			if(allTemp==null){
				allTemp=Maps.newHashMap();
				map.put("total",allTemp);
			}
			 if(allTemp.get(name)==null){
			    allTemp.put(name,new AmazonOperationalReport(maxOrder,promotionsOrder,flashOrder));
			 }else{
			    AmazonOperationalReport report=allTemp.get(name);
			    allTemp.put(name, new AmazonOperationalReport(report.getMaxOrder()>maxOrder?report.getMaxOrder():maxOrder,report.getPromotionsOrder()+promotionsOrder,report.getFlashSalesOrder()+flashOrder));
			 }
		   }
		   return rs;
     	}
	
	
	    //国家/日期/产品类型/产品
		public Map<String,Map<String,Map<String,Map<String,AmazonOperationalReport>>>> compareAllCountry(AmazonOperationalReport amazonOperationalReport,String endStr,String startStr) {
			Map<String,Map<String,Map<String,Map<String,AmazonOperationalReport>>>> map=Maps.newHashMap();
			//Date start = amazonOperationalReport.getCreateDate();
			//Date end = amazonOperationalReport.getEndDate();
			String typeSql = "'%Y-%m-%d'";
			//查询2个时间节点
			if("2".equals(amazonOperationalReport.getSearchType())){
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
			}else if("3".equals(amazonOperationalReport.getSearchType())){
				typeSql="'%Y-%m'";//按月查询
			}
			
			String sql="SELECT country,DATE_FORMAT(a.`create_date`,"+typeSql+") dates,product_name,SUM(SESSION),SUM(sales_volume),SUM(return_volume),SUM(order_volume) "+
            " ,TRUNCATE(Sum(CASE WHEN a.`country`='com' THEN a.`sales`*"
					+AmazonProduct2Service.getRateConfig().get("USD/EUR")+" WHEN a.`country`='uk' THEN a.`sales`*"+AmazonProduct2Service.getRateConfig().get("GBP/EUR")+" WHEN a.`country`='ca' THEN a.`sales`*"
				+AmazonProduct2Service.getRateConfig().get("CAD/EUR")+" WHEN a.`country`='jp' THEN a.`sales`*"+
					AmazonProduct2Service.getRateConfig().get("JPY/EUR")+" WHEN a.`country`='mx' THEN a.`sales`*"+AmazonProduct2Service.getRateConfig().get("MXN/EUR")+" ELSE a.`sales` END ),2), "+
				" SUM(bad_review),SUM(total_review),sum(session_order) "+
				" FROM amazoninfo_operational_report a where (DATE_FORMAT(a.create_date,"+typeSql+") = :p1 or DATE_FORMAT(a.create_date,"+typeSql+") = :p2)  GROUP BY country,dates,product_name order by country,dates ";
			List<Object[]> list = amazonOperationalReportDao.findBySql(sql, new Parameter(startStr,endStr));
			Map<String,String> typeMap=getProductNameAndType();
			for (Object[] obj: list) {
				String country=obj[0].toString();
				String date = obj[1].toString(); 
				if("2".equals(amazonOperationalReport.getSearchType())){
					date = DateUtils.getWeekStr(date,5, "-", true);
				}
				String name=obj[2].toString();
				Integer session=Integer.parseInt(obj[3]==null?"0":obj[3].toString());
				Integer salesVolume=Integer.parseInt(obj[4]==null?"0":obj[4].toString());
				Integer returnVolume=Integer.parseInt(obj[5]==null?"0":obj[5].toString());
				Integer orderVolume=Integer.parseInt(obj[6]==null?"0":obj[6].toString());
				float sales=Float.parseFloat(obj[7]==null?"0":obj[7].toString());
				Integer badReview=Integer.parseInt(obj[8]==null?"0":obj[8].toString());
				Integer totalReview=Integer.parseInt(obj[9]==null?"0":obj[9].toString());
				String type=typeMap.get(name);
				Integer sessionOrder=Integer.parseInt(obj[10]==null?"0":obj[10].toString());
				if(StringUtils.isBlank(type)){
					continue;
				}
				
				Map<String,Map<String,Map<String,AmazonOperationalReport>>> allTemp=map.get("total");
				if(allTemp==null){
					allTemp=Maps.newHashMap();
					map.put("total", allTemp);
				}

				Map<String,Map<String,AmazonOperationalReport>> allDateTemp=allTemp.get(date);
				if(allDateTemp==null){
					allDateTemp=Maps.newHashMap();
					allTemp.put(date, allDateTemp);
				}
				Map<String,AmazonOperationalReport> allTotalTemp=allDateTemp.get("total");
				if(allTotalTemp==null){
					allTotalTemp=Maps.newHashMap();
					allDateTemp.put("total", allTotalTemp);
				}
				AmazonOperationalReport allTotalReport=allTotalTemp.get("total");
				if(allTotalReport==null){
					allTotalReport=new AmazonOperationalReport(country,session,salesVolume,returnVolume,orderVolume,sales,badReview,totalReview,sessionOrder);
				}else{
					allTotalReport=new AmazonOperationalReport(country,session+allTotalReport.getSession(),salesVolume+allTotalReport.getSalesVolume(),returnVolume+allTotalReport.getReturnVolume(),
							orderVolume+allTotalReport.getOrderVolume(),sales+allTotalReport.getSales(),badReview+allTotalReport.getBadReview(),totalReview+allTotalReport.getTotalReview(),
							sessionOrder+allTotalReport.getSessionOrder());
				}
				allTotalTemp.put("total", allTotalReport);
				
				
				Map<String,AmazonOperationalReport> allTypeTemp=allDateTemp.get(type);
				if(allTypeTemp==null){
					allTypeTemp=Maps.newHashMap();
					allDateTemp.put(type, allTypeTemp);
				}
				AmazonOperationalReport allReport=allTypeTemp.get("total");
				if(allReport==null){
					allReport=new AmazonOperationalReport(country,session,salesVolume,returnVolume,orderVolume,sales,badReview,totalReview,sessionOrder);
				}else{
					allReport=new AmazonOperationalReport(country,session+allReport.getSession(),salesVolume+allReport.getSalesVolume(),returnVolume+allReport.getReturnVolume(),
							orderVolume+allReport.getOrderVolume(),sales+allReport.getSales(),badReview+allReport.getBadReview(),totalReview+allReport.getTotalReview(),
							sessionOrder+allReport.getSessionOrder());
				}
				allTypeTemp.put("total", allReport);
				
				AmazonOperationalReport allReport2=allTypeTemp.get(name);
				if(allReport2==null){
					allReport2=new AmazonOperationalReport(country,session,salesVolume,returnVolume,orderVolume,sales,badReview,totalReview,sessionOrder);
				}else{
					allReport2=new AmazonOperationalReport(country,session+allReport2.getSession(),salesVolume+allReport2.getSalesVolume(),returnVolume+allReport2.getReturnVolume(),
							orderVolume+allReport2.getOrderVolume(),sales+allReport2.getSales(),badReview+allReport2.getBadReview(),totalReview+allReport2.getTotalReview(),
							sessionOrder+allReport2.getSessionOrder());
				}
				allTypeTemp.put(name, allReport2);
			
				
				
				
				
				Map<String,Map<String,Map<String,AmazonOperationalReport>>> temp=map.get(country);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(country, temp);
				}
				
				Map<String,Map<String,AmazonOperationalReport>> dateTemp=temp.get(date);
				if(dateTemp==null){
					dateTemp=Maps.newHashMap();
					temp.put(date, dateTemp);
				}
				Map<String,AmazonOperationalReport> totalTemp=dateTemp.get("total");
				if(totalTemp==null){
					totalTemp=Maps.newHashMap();
					dateTemp.put("total", totalTemp);
				}
				AmazonOperationalReport totalReport=totalTemp.get("total");
				if(totalReport==null){
					totalReport=new AmazonOperationalReport(country,session,salesVolume,returnVolume,orderVolume,sales,badReview,totalReview,sessionOrder);
				}else{
					totalReport=new AmazonOperationalReport(country,session+totalReport.getSession(),salesVolume+totalReport.getSalesVolume(),returnVolume+totalReport.getReturnVolume(),
							orderVolume+totalReport.getOrderVolume(),sales+totalReport.getSales(),badReview+totalReport.getBadReview(),totalReview+totalReport.getTotalReview(),
							sessionOrder+totalReport.getSessionOrder());
				}
				totalTemp.put("total", totalReport);
				
				
				Map<String,AmazonOperationalReport> typeTemp=dateTemp.get(type);
				if(typeTemp==null){
					typeTemp=Maps.newHashMap();
					dateTemp.put(type, typeTemp);
				}
				AmazonOperationalReport report=typeTemp.get("total");
				if(report==null){
					report=new AmazonOperationalReport(country,session,salesVolume,returnVolume,orderVolume,sales,badReview,totalReview,sessionOrder);
				}else{
					report=new AmazonOperationalReport(country,session+report.getSession(),salesVolume+report.getSalesVolume(),returnVolume+report.getReturnVolume(),
							orderVolume+report.getOrderVolume(),sales+report.getSales(),badReview+report.getBadReview(),totalReview+report.getTotalReview(),
							sessionOrder+report.getSessionOrder());
				}
				typeTemp.put("total", report);
				typeTemp.put(name, new AmazonOperationalReport(country,session,salesVolume,returnVolume,orderVolume,sales,badReview,totalReview,sessionOrder));
			}
			return map;
		}
	
	
	//国家/日期/type
		public Map<String,Map<String,Map<String,AmazonOperationalReport>>> findAllCountryByType(AmazonOperationalReport amazonOperationalReport) {
			 Map<String,Map<String,Map<String,AmazonOperationalReport>>> map=Maps.newHashMap();
			Date start = amazonOperationalReport.getCreateDate();
			Date end = amazonOperationalReport.getEndDate();
			String typeSql = "'%Y%m%d'";
			if("2".equals(amazonOperationalReport.getSearchType())){
				if(start==null){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addWeeks(today, -19);
					end = today;
					amazonOperationalReport.setCreateDate(start);
					amazonOperationalReport.setEndDate(end);
				}else{
					Date end1 = DateUtils.addWeeks(start, 3);
					if(end.before(end1)){
						end = end1;
						amazonOperationalReport.setEndDate(end1);
					}
				}
				start = DateUtils.getMonday(start);
				end = DateUtils.getSunday(end);
				typeSql="'%x%v'";
			}else if("3".equals(amazonOperationalReport.getSearchType())){
				if(start==null){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addMonths(today, -18);
					end = today;
					amazonOperationalReport.setCreateDate(start);
					amazonOperationalReport.setEndDate(end);
				}else{
					Date end1 = DateUtils.addMonths(start, 3);
					if(end.before(end1)){
						end = end1;
						amazonOperationalReport.setEndDate(end1);
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
					amazonOperationalReport.setCreateDate(start);
					amazonOperationalReport.setEndDate(end);
				}else{
					Date end1 = DateUtils.addDays(start, 3);
					if(end.before(end1)){
						end = end1;
						amazonOperationalReport.setEndDate(end1);
					}
				}
			}
			
			String sql="SELECT country,DATE_FORMAT(a.`create_date`,"+typeSql+") dates,type,SUM(SESSION),SUM(sales_volume),SUM(return_volume),SUM(order_volume),SUM(sales),SUM(bad_review),SUM(total_review),sum(session_order) "+
	              " FROM amazoninfo_operational_report_type a where a.create_date>=:p1 and a.create_date<=:p2  GROUP BY country,dates,type";
			List<Object[]> list = amazonOperationalReportDao.findBySql(sql, new Parameter(start,end));
			for (Object[] obj: list) {
				String country=obj[0].toString();
				String date = obj[1].toString(); 
				if("2".equals(amazonOperationalReport.getSearchType())){
					Integer i = Integer.parseInt(date.substring(4));
					if(i==53){
						Integer year = Integer.parseInt(date.substring(0,4));
						date =  (year+1)+"01";
					}else if (date.contains("2016")){
						i = i+1;
						date = "2016"+(i<10?("0"+i):i);
					}
				}
				String name=obj[2].toString();
				Integer session=Integer.parseInt(obj[3]==null?"0":obj[3].toString());
				Integer salesVolume=Integer.parseInt(obj[4]==null?"0":obj[4].toString());
				Integer returnVolume=Integer.parseInt(obj[5]==null?"0":obj[5].toString());
				Integer orderVolume=Integer.parseInt(obj[6]==null?"0":obj[6].toString());
				float sales=Float.parseFloat(obj[7]==null?"0":obj[7].toString());
				Integer badReview=Integer.parseInt(obj[8]==null?"0":obj[8].toString());
				Integer totalReview=Integer.parseInt(obj[9]==null?"0":obj[9].toString());
				Integer sessionOrder=Integer.parseInt(obj[10]==null?"0":obj[10].toString());
				Map<String,Map<String,AmazonOperationalReport>> temp=map.get(country);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(country, temp);
				}
				
				Map<String,AmazonOperationalReport> dateTemp=temp.get(date);
				if(dateTemp==null){
					dateTemp=Maps.newHashMap();
					temp.put(date, dateTemp);
				}
				dateTemp.put(name, new AmazonOperationalReport(country,session,salesVolume,returnVolume,orderVolume,sales,badReview,totalReview,sessionOrder));
			}
			return map;
		}
	
	
	public Map<String,List<AmazonOperationalReport>> find(AmazonOperationalReport amazonOperationalReport) {
		Map<String,List<AmazonOperationalReport>> map=Maps.newHashMap();
		DetachedCriteria dc = amazonOperationalReportDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(amazonOperationalReport.getCountry())){
			dc.add(Restrictions.eq("country",amazonOperationalReport.getCountry()));
		}
		dc.add(Restrictions.eq("createDate",amazonOperationalReport.getCreateDate()));
		List<AmazonOperationalReport> list=amazonOperationalReportDao.find(dc);
		for (AmazonOperationalReport report : list) {
			List<AmazonOperationalReport> temp=map.get(report.getCountry());
			if(temp==null){
				temp=Lists.newArrayList();
				map.put(report.getCountry(), temp);
			}
			temp.add(report);
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void saveSpreadDate(){
		String sql="INSERT INTO `amazoninfo_spread_report` (country,create_date,product_name,ASIN,sku,sales_volume,sales,price)  	"+
            " SELECT REPLACE(REPLACE(REPLACE(a.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') AS country,STR_TO_DATE(DATE_FORMAT(a.`purchase_date`,'%Y%m%d'),'%Y%m%d') dates, "+
            " CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN CONCAT ('_',b.`color`) ELSE '' END) NAME,b.`asin`,b.`sellersku`, "+
			" SUM(CASE WHEN a.`order_status`='Pending'||a.`order_status`='Canceled' THEN 0 ELSE b.`quantity_ordered` END) AS sure_sales_volume, "+
			" SUM(CASE WHEN a.`order_status`='Pending'||a.`order_status`='Canceled' THEN 0 ELSE (IFNULL(b.`item_price`,0)-IFNULL(b.`promotion_discount`,0)) END) AS sure_sales, "+
			" SUM(CASE WHEN a.`order_status`='Pending'||a.`order_status`='Canceled' THEN 0 ELSE (IFNULL(b.`item_price`,0)-IFNULL(b.`promotion_discount`,0)) END)/SUM(CASE WHEN a.`order_status`='Pending'||a.`order_status`='Canceled' THEN 0 ELSE b.`quantity_ordered` END) AS price "+
			" FROM  amazoninfo_order a JOIN amazoninfo_orderitem b ON a.id=b.order_id 	 "+			
			" WHERE a.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -15 DAY)   AND a.order_status IN ('Shipped','Pending','Unshipped','Canceled') and b.`product_name` is not null "+
			" GROUP BY dates,country,NAME,b.`sellersku`,b.`asin` "+
			" ON DUPLICATE KEY UPDATE `sales_volume` = VALUES(sales_volume),`sales` =VALUES(sales),`price` = VALUES(price)	";
		amazonSpreadReportDao.updateBySql(sql, null);
		
		String sql2="INSERT INTO `amazoninfo_spread_session_report` (country,create_date,product_name,ASIN,sessions,orders,conversion)  	"+
				" SELECT r.`country`,DATE_FORMAT(r.`data_date`,'%Y-%m-%d') DATE,s.name,r.`child_asin`,SUM(r.`sessions`),SUM(r.orders_placed),SUM(r.orders_placed)*100/SUM(r.`sessions`)  	"+
				" FROM amazoninfo_business_report r  	"+
				" JOIN (SELECT DISTINCT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) NAME,country,ASIN FROM psi_sku a WHERE del_flag='0') s  "+
				" ON r.`child_asin`=s.asin AND r.`country`=s.country where r.data_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -7 DAY)   GROUP BY r.`country`,DATE,s.name,r.`child_asin` "+
				" ON DUPLICATE KEY UPDATE `sessions` = VALUES(sessions),`orders` =VALUES(orders),`conversion` = VALUES(conversion)	";
			amazonSpreadReportDao.updateBySql(sql2, null);
		
		/*String updateSql2="update amazoninfo_spread_report set cost=:p1 where country=:p2 and product_name=:p3 and create_date=CURRENT_DATE() ";
		Map<String,Float> priceMap=productPriceService.findAllProducSalePrice("USD");//name_country
		for (String nameAndCountry: priceMap.keySet()) {
			String name=nameAndCountry.substring(0,nameAndCountry.lastIndexOf("_"));
			String country=nameAndCountry.substring(nameAndCountry.lastIndexOf("_")+1);
			try{
				amazonSpreadReportDao.updateBySql(updateSql2, new Parameter(priceMap.get(nameAndCountry),country,name));
			}catch(Exception e){
			}
		}*/
	}
	
	@Transactional(readOnly = false)
	public void saveHistoryData(){
		String sql="INSERT INTO `amazoninfo_operational_report` (product_name,country,create_date,SESSION,sales_volume,sales,order_volume,total_review,session_order)  	"+
				" SELECT  NAME product_name,country,DATE,SUM(SESSION) SESSION,SUM(salesAmount) sales_volume,SUM(sales) sales,SUM(order_volume) order_volume,SUM(total_review) total_review,sum(session_order) session_order FROM 	"+
				" (SELECT r.`country`,s.name,STR_TO_DATE(DATE_FORMAT(r.`data_date`,'%Y-%m-%d'),'%Y-%m-%d') DATE,r.`sessions` SESSION,0 salesAmount,0 sales,0 order_volume,0 total_review,r.orders_placed session_order 	"+
				" FROM amazoninfo_business_report r  	"+
				" JOIN (SELECT DISTINCT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) NAME,country,ASIN FROM psi_sku a WHERE del_flag='0') s  	"+
				" ON r.`child_asin`=s.asin AND r.`country`=s.country 	"+
				" WHERE r.`data_date`>=DATE_ADD(CURRENT_DATE(),INTERVAL -15 DAY) AND s.name IS NOT NULL 	"+
				" UNION  all	"+
				" SELECT SUBSTRING_INDEX(o.`sales_channel`,'.',-1),CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END) NAME,STR_TO_DATE(DATE_FORMAT(o.purchase_date,'%Y-%m-%d'),'%Y-%m-%d'), 	"+
				" 0,t.quantity_ordered,(IFNULL(t.`item_price`,0)-IFNULL(t.`promotion_discount`,0)) AS sales,	"+
				" (CASE WHEN o.`order_status`='Shipped'  THEN t.quantity_ordered ELSE 0 END), "+
				" (CASE WHEN o.`order_status`='Shipped'  THEN 1 ELSE 0 END),0 "+
				" FROM amazoninfo_order o JOIN  amazoninfo_orderitem t ON o.id=t.`order_id`  	"+
				" WHERE o.`order_status` IN ('Shipped','Pending','Unshipped') AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -15 DAY) AND CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END) IS NOT NULL 	"+
				" ) r GROUP BY NAME,country,DATE								"+
				" ON DUPLICATE KEY UPDATE `SESSION` = VALUES(SESSION),`sales_volume` =VALUES(sales_volume),`sales` = VALUES(sales),`order_volume` = VALUES(order_volume) ,`total_review` = VALUES(total_review),session_order= VALUES(session_order) 	";
		amazonOperationalReportDao.updateBySql(sql, null);
		
		String sql2="INSERT INTO `amazoninfo_operational_report` (product_name,country,create_date,return_volume,bad_review)  	"+
				" SELECT  NAME product_name,country,DATE create_date,SUM(quantity) return_volume,SUM(bad_review) bad_review FROM "+
				"  (SELECT g.`country`,s.NAME,STR_TO_DATE(DATE_FORMAT(o.purchase_date,'%Y-%m-%d'),'%Y-%m-%d') DATE,g.quantity,0 bad_review FROM amazoninfo_return_goods g JOIN amazoninfo_order o ON o.`amazon_order_id` = g.`order_id` "+
				" JOIN (SELECT DISTINCT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) NAME,country,ASIN FROM psi_sku a WHERE del_flag='0') s  "+
				" ON s.`asin`=g.`asin` AND  g.`country`=s.`country` WHERE  o.`order_status`='Shipped' AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -3 MONTH) and s.NAME is not null "+
				" UNION all "+
				" SELECT b.country,b.name,STR_TO_DATE(DATE_FORMAT(o.purchase_date,'%Y-%m-%d'),'%Y-%m-%d') DATE,0,1 FROM 	 "+
				" (SELECT a.`remarks`,a.country,SUBSTRING_INDEX(a.`invoice_number`,',',1)  order_id "+
				" FROM custom_event_manager a  "+
				" WHERE a.type='1' AND a.`invoice_number`!='not find oderID' AND a.`invoice_number` IS NOT NULL) a "+
				" JOIN (SELECT DISTINCT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) NAME,country,ASIN FROM psi_sku a WHERE del_flag='0') b "+
				" ON a.country=b.country AND a.remarks=b.asin "+
				" JOIN amazoninfo_order o ON o.`amazon_order_id` = a.`order_id` WHERE  o.`order_status`='Shipped' AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -3 MONTH) and b.name is not null "+
				" ) r GROUP BY NAME,country,DATE		"+					
				" ON DUPLICATE KEY UPDATE `return_volume` = VALUES(return_volume),`bad_review` =VALUES(bad_review)";
		amazonOperationalReportDao.updateBySql(sql2, null);	
	}
	
	@Transactional(readOnly = false)
	public void updateAndSaveByType() {
	  
		String sql="INSERT INTO amazoninfo_operational_report_type(type,country,SESSION,sales_volume,sales,order_volume,total_review,return_volume,bad_review,create_date,session_order) " +
				" SELECT b.`TYPE`,a.`country`,SUM(IFNULL(a.`SESSION`,0)) SESSION,SUM(IFNULL(a.`sales_volume`,0)) sales_volume,SUM(IFNULL(a.`sales`,0)) sales, " +
				" SUM(IFNULL(a.`order_volume`,0)) order_volume,SUM(IFNULL(a.`total_review`,0)) total_review,SUM(IFNULL(a.`return_volume`,0)) return_volume,SUM(IFNULL(a.`bad_review`,0)) bad_review,a.`create_date`,SUM(IFNULL(a.`session_order`,0)) session_order FROM amazoninfo_operational_report a ,psi_product b  " +
				" WHERE b.`del_flag`='0' AND SUBSTRING_INDEX(a.`product_name`,'_',1) = CONCAT(b.`brand`,' ',b.`model`) AND a.`create_date`>=DATE_ADD(CURRENT_DATE(),INTERVAL -3 MONTH) " +
				" GROUP BY b.`TYPE`,a.`country`,a.`create_date` " +
				" ON DUPLICATE KEY UPDATE `SESSION` = VALUES(SESSION),`sales_volume` =VALUES(sales_volume),`sales` = VALUES(sales),`order_volume` = VALUES(order_volume),`total_review` = VALUES(total_review),`return_volume` = VALUES(return_volume),`bad_review` = VALUES(bad_review),`session_order` = VALUES(session_order) ";
		amazonOperationalReportDao.updateBySql(sql, null);
	}
	
	//session
	public Map<String,Map<String,Integer>> getSessionMap(){
		Map<String,Map<String,Integer>> map=Maps.newHashMap();
		String sql="SELECT r.`country`,s.name,sum(r.`sessions`) FROM amazoninfo_business_report r "+
			" JOIN (SELECT DISTINCT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) NAME,country,ASIN FROM psi_sku a WHERE del_flag='0') s "+
			" ON r.`child_asin`=s.asin AND r.`country`=s.country WHERE r.`data_date`=DATE_ADD(CURRENT_DATE(),INTERVAL -3 DAY) group by s.name,r.`country`";
		List<Object[]> list=amazonOperationalReportDao.findBySql(sql);
		for (Object[] obj: list) {
			Map<String,Integer> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(),temp);
			}
			temp.put(obj[1].toString(),Integer.parseInt(obj[2].toString()));
		}
		return map;
	}
	
	//session转化销量 - 2销量(单价)3 销售额  
	public Map<String,Map<String,Object[]>> getConversionMap(){
		Map<String,Map<String,Object[]>> map=Maps.newHashMap();
		String sql="SELECT SUBSTRING_INDEX(o.`sales_channel`,'.',-1),CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END) NAME,SUM(t.quantity_ordered) "+ 
				" ,SUM(IFNULL(t.`item_price`,0)-IFNULL(t.`promotion_discount`,0)) AS sales  FROM amazoninfo_order o JOIN  amazoninfo_orderitem t ON o.id=t.`order_id` "+
				" WHERE o.`order_status` in ('Shipped','Pending','Unshipped') AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -3 DAY)  AND o.purchase_date<DATE_ADD(CURRENT_DATE(),INTERVAL -2 DAY) "+
				" GROUP BY NAME,SUBSTRING_INDEX(o.`sales_channel`,'.',-1) HAVING NAME IS NOT NULL ";
		List<Object[]> list=amazonOperationalReportDao.findBySql(sql);
		for (Object[] obj: list) {
			Map<String,Object[]> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(),temp);
			}
			temp.put(obj[1].toString(),obj);
		}
		return map;
	}
	
	//退货订单总销量
	  public Map<String,Map<String,Integer>> getOrderMap(){
			Map<String,Map<String,Integer>> map=Maps.newHashMap();
			String sql="SELECT SUBSTRING_INDEX(o.`sales_channel`,'.',-1),CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END) NAME,SUM(t.quantity_ordered) "+ 
					" FROM amazoninfo_order o JOIN  amazoninfo_orderitem t ON o.id=t.`order_id` "+
					" WHERE o.`order_status` in ('Shipped') AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -3 DAY)  AND o.purchase_date<DATE_ADD(CURRENT_DATE(),INTERVAL -2 DAY) "+
					" GROUP BY NAME,SUBSTRING_INDEX(o.`sales_channel`,'.',-1) HAVING NAME IS NOT NULL ";
			List<Object[]> list=amazonOperationalReportDao.findBySql(sql);
			for (Object[] obj: list) {
				Map<String,Integer> temp=map.get(obj[0].toString());
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(obj[0].toString(),temp);
				}
				temp.put(obj[1].toString(),Integer.parseInt(obj[2].toString()));
			}
			return map;
		}
	  
	 //退货数
	  public Map<String,Map<String,Integer>> getReturnMap(){
		    Map<String,Map<String,Integer>> map=Maps.newHashMap();
			String sql=" SELECT g.`country`,s.NAME,SUM(g.quantity) FROM amazoninfo_return_goods g JOIN amazoninfo_order o ON o.`amazon_order_id` = g.`order_id` "+ 
		      " JOIN (SELECT DISTINCT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) NAME,country,ASIN FROM psi_sku a WHERE del_flag='0') s  "+
		      " ON s.`asin`=g.`asin` AND  g.`country`=s.`country` WHERE  o.`order_status`='Shipped' AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -3 DAY)  AND o.purchase_date<DATE_ADD(CURRENT_DATE(),INTERVAL -2 DAY) "+
		      " GROUP BY s.NAME,g.`country`";
			List<Object[]> list=amazonOperationalReportDao.findBySql(sql);
			for (Object[] obj: list) {
				Map<String,Integer> temp=map.get(obj[0].toString());
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(obj[0].toString(),temp);
				}
				temp.put(obj[1].toString(),Integer.parseInt(obj[2].toString()));
			}
			return map;			
	  }
	  
	  //总评价
	  public Map<String,Map<String,Integer>> getReviewMap(){
			Map<String,Map<String,Integer>> map=Maps.newHashMap();
			String sql="SELECT SUBSTRING_INDEX(o.`sales_channel`,'.',-1),CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END) NAME,count(*) "+ 
					" FROM amazoninfo_order o JOIN  amazoninfo_orderitem t ON o.id=t.`order_id` "+
					" WHERE o.`order_status` in ('Shipped') AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -3 DAY)  AND o.purchase_date<DATE_ADD(CURRENT_DATE(),INTERVAL -2 DAY) "+
					" GROUP BY NAME,SUBSTRING_INDEX(o.`sales_channel`,'.',-1) HAVING NAME IS NOT NULL ";
			List<Object[]> list=amazonOperationalReportDao.findBySql(sql);
			for (Object[] obj: list) {
				Map<String,Integer> temp=map.get(obj[0].toString());
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(obj[0].toString(),temp);
				}
				temp.put(obj[1].toString(),Integer.parseInt(obj[2].toString()));
			}
			return map;
		}
	  
	  
	//日期 [国家/产品/type info]
			public Map<String, Map<String,Map<String,AmazonOperationalReport>>> getSalesTypeByProduct(String type,String startStr,String endStr){
				String typeSql = "'%Y-%m-%d'";
				//查询2个时间节点
				if("2".equals(type)){
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
				}else if("3".equals(type)){
					typeSql="'%Y-%m'";//按月查询
				}
				
				List<Object[]> list = null;//1.促销 2闪购 3最大订单
				
				String sql ="SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`) AS pname,a.`country`,max(max_order) tempOrder,DATE_FORMAT(a.`date`,"+typeSql+") dates,sum(promotions_order),sum(flash_sales_order) FROM amazoninfo_sale_report a  WHERE a.order_type='1' and product_name is not null and (DATE_FORMAT(a.`date`,"+typeSql+") = :p1 or DATE_FORMAT(a.`date`,"+typeSql+") = :p2)   GROUP BY a.`country`,dates,pname";
				
				list = amazonOperationalReportDao.findBySql(sql, new Parameter(startStr,endStr));
				Map<String, Map<String,Map<String,AmazonOperationalReport>>> rs = Maps.newHashMap();
				for (Object[] obj: list) {
					
					String name=obj[0].toString();
					String country=obj[1].toString();
					Integer maxOrder=Integer.parseInt(obj[2]==null?"0":obj[2].toString());
					String date=obj[3].toString();
					if("2".equals(type)){
						date = DateUtils.getWeekStr(date,5, "-", true);
					}
					Integer promotionsOrder=Integer.parseInt(obj[4]==null?"0":obj[4].toString());
					Integer flashOrder=Integer.parseInt(obj[5]==null?"0":obj[5].toString());
					
					Map<String,Map<String,AmazonOperationalReport>> map=rs.get(date);
					if(map==null){
						map=Maps.newHashMap();
						rs.put(date,map);
					}
					Map<String,AmazonOperationalReport> temp=map.get(country);
					if(temp==null){
						temp=Maps.newHashMap();
						map.put(country,temp);
					}
					temp.put(name, new AmazonOperationalReport(maxOrder,promotionsOrder,flashOrder));
					
					Map<String,AmazonOperationalReport> allTemp=map.get("total");
					if(allTemp==null){
						allTemp=Maps.newHashMap();
						map.put("total",allTemp);
					}
					 if(allTemp.get(name)==null){
					    allTemp.put(name,new AmazonOperationalReport(maxOrder,promotionsOrder,flashOrder));
					 }else{
					    AmazonOperationalReport report=allTemp.get(name);
					    allTemp.put(name, new AmazonOperationalReport(report.getMaxOrder()>maxOrder?report.getMaxOrder():maxOrder,report.getPromotionsOrder()+promotionsOrder,report.getFlashSalesOrder()+flashOrder));
					 }
				}
				return rs;
			}
			
			
			public List<AmazonSpreadReport> findAvgSpread(AmazonSpreadReport report){
				//亚马逊佣金
				Map<String, Integer> commissionMap = productPriceService.findCommission();
				List<String> countryList = Lists.newArrayList("de","uk","fr","it","es","com","ca","jp");
				Map<String, Float> vat = Maps.newHashMap();
				for (String country : countryList) {
					String temp = country.toUpperCase();
					if("UK".equals(temp)){
						temp = "GB";
					}
					if("COM".equals(temp)){
						temp = "US";
					}
					CountryCode vatCode = CountryCode.valueOf(temp);
					if(vatCode!=null){
						vat.put(country, vatCode.getVat()/100f);
					}
				}
				Map<String,List<AmazonSpreadReport>> returnMap=Maps.newHashMap();
				Map<String,Float> priceMap=productPriceService.findAllProducSalePrice("USD");//name_country
				Map<String,AmazonSpreadReport> map=Maps.newHashMap();
				String sql2="SELECT r.`asin`,r.`country`,r.`product_name`,AVG(r.`sessions`),AVG(r.`orders`),TRUNCATE(AVG(r.`orders`)*100/AVG(r.`sessions`),2) "+
				        " FROM amazoninfo_spread_session_report r "+
						" where r.create_date>=:p1 and r.create_date<=:p2 and country=:p3 "+
						" GROUP BY r.`asin`,r.`country`,r.`product_name` ";
				List<Object[]> list2=amazonSpreadReportDao.findBySql(sql2, new Parameter(report.getCreateDate(),report.getEndDate(),report.getCountry()));
				for (Object[] obj: list2) {
					String asin=obj[0].toString();
					String country=obj[1].toString();
					String name=obj[2].toString();
					Integer session=(int)Float.parseFloat(obj[3]==null?"0":obj[3].toString());
					Integer order=(int)Float.parseFloat(obj[4]==null?"0":obj[4].toString());
					Float conversion=Float.parseFloat(obj[5]==null?"0":obj[5].toString());
					AmazonSpreadReport temp=new AmazonSpreadReport();
					temp.setSession(session);
					temp.setOrder(order);
					temp.setConversion(conversion);
					map.put(name+"_"+country+"_"+asin, temp);
				}
				
				List<AmazonSpreadReport> reportList=Lists.newArrayList();
				String sql="SELECT r.`asin`,r.`sku`,r.`country`,r.`product_name`,AVG(r.`session`),AVG(r.`order`),TRUNCATE(AVG(r.`cost`),2), "+
				        " CASE WHEN country = 'de'|| country = 'it' || country = 'fr' || country = 'es' THEN TRUNCATE(AVG(r.`sales`)*"+MathUtils.getRate("EUR","USD", null)+",2)  WHEN country = 'uk' THEN TRUNCATE(AVG(r.`sales`)*"+MathUtils.getRate("GBP","USD", null)+",2) WHEN country = 'ca' THEN TRUNCATE(AVG(r.`sales`)*"+MathUtils.getRate("CAD","USD", null)+",2) WHEN country = 'jp' THEN TRUNCATE(AVG(r.`sales`)*"+MathUtils.getRate("JPY","USD", null)+",2) WHEN country = 'mx' THEN TRUNCATE(AVG(r.`sales`)*"+MathUtils.getRate("MXN","USD", null)+",2) ELSE TRUNCATE(AVG(r.`sales`),2) END,"+
						" AVG(r.`sales_volume`),TRUNCATE(AVG(r.`order`)*100/AVG(r.`session`),2),TRUNCATE((CASE WHEN country = 'de'|| country = 'it' || country = 'fr' || country = 'es' THEN TRUNCATE(AVG(r.`sales`)*"+MathUtils.getRate("EUR","USD", null)+",2)  WHEN country = 'uk' THEN TRUNCATE(AVG(r.`sales`)*"+MathUtils.getRate("GBP","USD", null)+",2) WHEN country = 'ca' THEN TRUNCATE(AVG(r.`sales`)*"+MathUtils.getRate("CAD","USD", null)+",2) WHEN country = 'jp' THEN TRUNCATE(AVG(r.`sales`)*"+MathUtils.getRate("JPY","USD", null)+",2) WHEN country = 'mx' THEN TRUNCATE(AVG(r.`sales`)*"+MathUtils.getRate("MXN","USD", null)+",2) ELSE TRUNCATE(AVG(r.`sales`),2) END)/AVG(r.`sales_volume`),2) "+
						" FROM amazoninfo_spread_report r "+
						" where r.create_date>=:p1 and r.create_date<=:p2 and country=:p3 "+
						" GROUP BY r.`asin`,r.`sku`,r.`country`,r.`product_name` ";
				
				List<Object[]> list=amazonSpreadReportDao.findBySql(sql, new Parameter(report.getCreateDate(),report.getEndDate(),report.getCountry()));
				for (Object[] obj: list) {
					String asin=obj[0].toString();
					String sku=obj[1].toString();
					String country=obj[2].toString();
					String name=obj[3].toString();
					//Integer session=(int)Float.parseFloat(obj[4]==null?"0":obj[4].toString());
					Integer session=0;
					Float conversion=0f;
					Integer order=0;
					if(map!=null&&map.get(name+"_"+country+"_"+asin)!=null){
						session=map.get(name+"_"+country+"_"+asin).getSession();
						order=map.get(name+"_"+country+"_"+asin).getOrder();
						conversion=map.get(name+"_"+country+"_"+asin).getConversion();
					}
					//Integer order=(int)Float.parseFloat(obj[5]==null?"0":obj[5].toString());
					Float cost=0f;
					if(priceMap.get(name+"_"+country)!=null){
						cost=priceMap.get(name+"_"+country);
					}
					Float sales=Float.parseFloat(obj[7]==null?"0":obj[7].toString());
					Integer salesVolume=(int)Float.parseFloat(obj[8]==null?"0":obj[8].toString());
					//Float conversion=Float.parseFloat(obj[9]==null?"0":obj[9].toString());
					Float price=Float.parseFloat(obj[10]==null?"0":obj[10].toString());
					//Float profit=price-cost;
					int commission = 0;
					try {
						commission = commissionMap.get(name + "_" + country);
					} catch (NullPointerException e) {}
					Float profit = (price-cost)/(1+vat.get(country))-(price-cost)*commission/100f;
					reportList.add(new AmazonSpreadReport(country,name,session,salesVolume,sales,sku,asin,price,cost,order,conversion,profit));
					
					List<AmazonSpreadReport> tempList=returnMap.get(name+"_"+country+"_"+asin);
					if(tempList==null){
						tempList=Lists.newArrayList();
						returnMap.put(name+"_"+country+"_"+asin, tempList);
					}
					tempList.add(new AmazonSpreadReport(country,name,session,salesVolume,sales,sku,asin,price,cost,order,conversion,profit));
				}
				return reportList;
			}
			
			
			public Map<String,Integer> findSession(AmazonSpreadReport report){
				 Map<String,Integer>  map=Maps.newHashMap();
				 String sql="select DATE_FORMAT(create_date,'%Y-%m-%d'),sessions from amazoninfo_spread_session_report where country=:p1 and asin=:p2 and product_name=:p3 and create_date>=:p4 and create_date<=:p5 ";
				 List<Object[]> list=amazonSpreadReportDao.findBySql(sql, new Parameter(report.getCountry(),report.getAsin(),report.getProductName(),report.getCreateDate(),report.getEndDate()));
				 for (Object[] obj: list) {
					 map.put(obj[0].toString(), Integer.parseInt(obj[1]==null?"0":obj[1].toString()));
				 }
				 return map;
			}
			
			public Map<String,Integer> findOrder(AmazonSpreadReport report){
				 Map<String,Integer>  map=Maps.newHashMap();
				 String sql="select DATE_FORMAT(create_date,'%Y-%m-%d'),r.`orders` from amazoninfo_spread_session_report r where country=:p1  and asin=:p2 and product_name=:p3 and create_date>=:p4 and create_date<=:p5 ";
				 List<Object[]> list=amazonSpreadReportDao.findBySql(sql, new Parameter(report.getCountry(),report.getAsin(),report.getProductName(),report.getCreateDate(),report.getEndDate()));
				 for (Object[] obj: list) {
					 map.put(obj[0].toString(), Integer.parseInt(obj[1]==null?"0":obj[1].toString()));
				 }
				 return map;
			}
			
			public Map<String,Integer> findSalesVolume(AmazonSpreadReport report){
				 Map<String,Integer>  map=Maps.newHashMap();
				 String sql="select DATE_FORMAT(create_date,'%Y-%m-%d'),sales_volume from amazoninfo_spread_report where country=:p1 and sku=:p2 and asin=:p3 and product_name=:p4 and create_date>=:p5 and create_date<=:p6 ";
				 List<Object[]> list=amazonSpreadReportDao.findBySql(sql, new Parameter(report.getCountry(),report.getSku(),report.getAsin(),report.getProductName(),report.getCreateDate(),report.getEndDate()));
				 for (Object[] obj: list) {
					 map.put(obj[0].toString(), Integer.parseInt(obj[1]==null?"0":obj[1].toString()));
				 }
				 return map;
			}
	  
			public Map<String,Float> findSales(AmazonSpreadReport report){
				 Map<String,Float>  map=Maps.newHashMap();
				 String sql="select DATE_FORMAT(create_date,'%Y-%m-%d'),sales from amazoninfo_spread_report where country=:p1 and sku=:p2 and asin=:p3 and product_name=:p4 and create_date>=:p5 and create_date<=:p6 ";
				 List<Object[]> list=amazonSpreadReportDao.findBySql(sql, new Parameter(report.getCountry(),report.getSku(),report.getAsin(),report.getProductName(),report.getCreateDate(),report.getEndDate()));
				 for (Object[] obj: list) {
					 map.put(obj[0].toString(), Float.parseFloat(obj[1]==null?"0":obj[1].toString()));
				 }
				 return map;
			}
			
			public Map<String,Float> findConversion(AmazonSpreadReport report){
				 Map<String,Float>  map=Maps.newHashMap();
				 String sql="select DATE_FORMAT(create_date,'%Y-%m-%d'),conversion from amazoninfo_spread_session_report where country=:p1  and asin=:p2 and product_name=:p3 and create_date>=:p4 and create_date<=:p5 ";
				 List<Object[]> list=amazonSpreadReportDao.findBySql(sql, new Parameter(report.getCountry(),report.getAsin(),report.getProductName(),report.getCreateDate(),report.getEndDate()));
				 for (Object[] obj: list) {
					 map.put(obj[0].toString(), Float.parseFloat(obj[1]==null?"0":obj[1].toString()));
				 }
				 return map;
			}
			
}
