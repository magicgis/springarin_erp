package com.springrain.erp.modules.amazoninfo.web;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.BusinessReport;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.BusinessReportService;
import com.springrain.erp.modules.amazoninfo.service.ProductHistoryPriceService;
import com.springrain.erp.modules.amazoninfo.service.SessionMonitorService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 亚马逊商业报表Controller
 * @author tim
 * @version 2014-05-28
 */
@SuppressWarnings("all")
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/businessReport")
public class BusinessReportController extends BaseController {

	@Autowired
	private BusinessReportService          businessReportService;
	
	@Autowired
	private AmazonProductService           amazonProductService;
	
	@Autowired
	private PsiProductTypeGroupDictService psiTypeGroupService;
	
	@Autowired
	private ProductHistoryPriceService     hisPriceService;
	
	@ModelAttribute
	public BusinessReport get(@RequestParam(required=false) String id) {
		if (!StringUtils.isBlank(id)){
			return businessReportService.get(id);
		}else{
			return new BusinessReport();
		}
	}
	
	@RequiresPermissions("amazoninfo:businessReport:view")
	@RequestMapping(value = {"list", ""})
	public String list(BusinessReport businessReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isBlank(businessReport.getCountry())){
			businessReport.setCountry("de");
		}
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if("com,ca".contains(businessReport.getCountry())){
			today = DateUtils.addDays(today, -1);
		}
		if(businessReport.getCreateDate()==null){
			businessReport.setCreateDate(DateUtils.addDays(today, -2));
		}
		if(businessReport.getDataDate()==null){
			businessReport.setDataDate(DateUtils.addDays(today,-2));
		}	
		Page<BusinessReport> page = new Page<BusinessReport>(request, response,20);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("sessions desc");
		}else{
			page.setOrderBy(orderBy+",sessions desc");
		}	
        page = businessReportService.find(page, businessReport); 
        page.setOrderBy(orderBy);
        
        List<String> products = Lists.newArrayList();
        for (BusinessReport br : page.getList()) {
        	String name = "unknown";
        	if(StringUtils.isNotEmpty(br.getChildAsin())&&StringUtils.isNotEmpty(br.getCountry())){
        		String temp = amazonProductService.findProductName(br.getChildAsin(), br.getCountry());
            	if(StringUtils.isNotEmpty(temp)){
            		String[] temp1 = temp.split(" ");
            		if(temp1.length>1){
            			name = temp1[1];
            			if(temp1.length>2){
            				name =name+" "+temp1[2];
            			}
            		}else{
            			name = temp;
            		}
            	}
        	}
        	products.add(name);
		}
        model.addAttribute("products", products);
        model.addAttribute("page", page);
		return "modules/amazoninfo/businessReportList";
	}
	
	
	
	@RequiresPermissions("amazoninfo:businessReport:view")
	@RequestMapping(value = "listByDate")
	public String listByDate(String groupName,String active,String date1, String date2, BusinessReport businessReport, HttpServletRequest request, HttpServletResponse response, Model model) throws NumberFormatException, ParseException {
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
		model.addAttribute("groupType", psiTypeGroupService.getAllList());
		model.addAttribute("groupName", groupName);
		//国家为空时统计各平台session转化率
		if(StringUtils.isBlank(businessReport.getCountry())){
			
			Date start = businessReport.getCreateDate();
			Date end = businessReport.getDataDate();
			//查询2个时间节点
			if("1".equals(businessReport.getSearchFlag())){
				//按周查询
				if(StringUtils.isBlank(date1)){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addWeeks(today, -1);
					end = today;
					date1 = DateUtils.getWeekStr(start, formatWeek, 5, "-");
					date2 = DateUtils.getWeekStr(end, formatWeek, 5, "-");
				} else {
					if ("2015-53".equals(date1)) {
						date1 = "2016-01";
					}
					if ("2015-53".equals(date2)) {
						date2 = "2016-01";
					}
					if(date1.equals(date2)){
						end = formatWeek.parse(date2);
						start = DateUtils.addWeeks(end, -1);
						date1 = DateUtils.getWeekStr(start, formatWeek, 5, "-");
						date2 = DateUtils.getWeekStr(end, formatWeek, 5, "-");
					} else {
						start = formatWeek.parse(date1);
						end = formatWeek.parse(date2);
					}
				}
				businessReport.setCreateDate(start);
				businessReport.setDataDate(end);
			}else if("2".equals(businessReport.getSearchFlag())){
				//按月查询
				if(StringUtils.isBlank(date1)){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addMonths(today, -1);
					end = today;
				}else{
					if (date1.equals(date2)) {
						end = formatMonth.parse(date2);
						start = DateUtils.addMonths(end, -1);
					}else {
						start = formatMonth.parse(date1);
						end = formatMonth.parse(date2);
					}
				}
				businessReport.setCreateDate(start);
				businessReport.setDataDate(end);
				date1 = formatMonth.format(start);
				date2 = formatMonth.format(end);
			}else{
				if(StringUtils.isBlank(date1)){
					Date today = new Date();
					int hours = today.getHours();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					if (hours >= 8) {//数据存在延时
						start = DateUtils.addDays(today, -4);
						end = DateUtils.addDays(today, -3);
					} else {
						start = DateUtils.addDays(today, -5);
						end = DateUtils.addDays(today, -4);
					}
				}else{
					if (date1.equals(date2)) {
						end = formatDay.parse(date2);
						start = DateUtils.addDays(end, -1);
					}else {
						start = formatDay.parse(date1);
						end = formatDay.parse(date2);
					}
				}
				date1 = formatDay.format(start);
				date2 = formatDay.format(end);
				businessReport.setCreateDate(start);
				businessReport.setDataDate(end);
			}
			
			Map<String, Map<String, BusinessReport>> data = businessReportService.totalSessions(businessReport, date1, date2,groupName);
			model.addAttribute("data", data);
			//构建x轴
			List<String> xAxis  = Lists.newArrayList();
			Map<String, String> tip = Maps.newHashMap();

			String type = "日";
			if("1".equals(businessReport.getSearchFlag())){
				//区间1
				xAxis.add(date1);
				int year1 = Integer.parseInt(date1.substring(0,4));
				int week1 =  Integer.parseInt(date1.substring(5));
				Date first1 = DateUtils.getFirstDayOfWeek(year1, week1);
				tip.put(date1,DateUtils.getDate(first1,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first1),"yyyy-MM-dd"));
				//区间2
				xAxis.add(date2);
				int year2 = Integer.parseInt(date2.substring(0,4));
				int week2 =  Integer.parseInt(date2.substring(5));
				Date first2 = DateUtils.getFirstDayOfWeek(year2, week2);
				tip.put(date2,DateUtils.getDate(first2,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first2),"yyyy-MM-dd"));
				
				type = "周";
			}else if("2".equals(businessReport.getSearchFlag())){
				//区间1
				String key1 = formatMonth.format(start);
				xAxis.add(key1);
				tip.put(key1, key1+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				//区间2
				String key2 = formatMonth.format(end);
				xAxis.add(key2);
				tip.put(key2, key2+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(end),"yyyy-MM-dd"));
				
				type = "月";
			}else{
				//区间1
				String key1 = formatDay.format(start);
				xAxis.add(key1);
				tip.put(key1,DateUtils.getDate(start,"E"));
				//区间2
				String key2 = formatDay.format(end);
				xAxis.add(key2);
				tip.put(key2,DateUtils.getDate(end,"E"));
			}
			model.addAttribute("xAxis", xAxis);
			model.addAttribute("tip", tip);
			model.addAttribute("type", type);
			model.addAttribute("date1", date1);
			model.addAttribute("date2", date2);
			
			return "modules/amazoninfo/businessReportListByDate";
		}
		
		Date today = new Date();
		
		if(StringUtils.isEmpty(active)){
			active = "2";
		}
		
		Date datet = null;
		if(today.getHours()>=8){
			datet = DateUtils.addDays(today, -2);
		}else{
			datet = DateUtils.addDays(today, -3);
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str=sdf.format(datet);
		datet=sdf.parse(str);
		
		Date endDate =null;
		Date startDate=null;
		String searchFlag=businessReport.getSearchFlag();
		if (StringUtils.isNotBlank(date1)) {
			if (searchFlag != null && searchFlag.equals("1")){
				businessReport.setCreateDate(DateUtils.addDays(formatWeek.parse(date1), 1));
				businessReport.setDataDate(DateUtils.addDays(formatWeek.parse(date1), 7));
			}else if (searchFlag != null && searchFlag.equals("2")){
				businessReport.setCreateDate(formatMonth.parse(date1));
				businessReport.setDataDate(DateUtils.getLastDayOfMonth(formatMonth.parse(date1)));
			} else {
				businessReport.setCreateDate(formatDay.parse(date1));
				businessReport.setDataDate(formatDay.parse(date1));
			}
		}
		if(businessReport.getCreateDate()==null && StringUtils.isBlank(date1)){
			startDate= DateUtils.addMonths(datet, -1);
			businessReport.setCreateDate(startDate);
		}else{
			startDate=businessReport.getCreateDate();
		}
		
		if(businessReport.getDataDate()==null && StringUtils.isBlank(date1)){
			endDate=datet;
			businessReport.setDataDate(datet);
		}else{
			endDate = businessReport.getDataDate();
		}
		
		if(StringUtils.isBlank(businessReport.getCountry())){
			businessReport.setCountry("de");
		}
		Page<BusinessReport> page = new Page<BusinessReport>(request, response);
		page.setPageNo(1);
		page.setPageSize(10000);
		String orderBy = page.getOrderBy();
		
		if(searchFlag!=null){
			if(searchFlag.equals("0")||searchFlag.equals("")){
				if("".equals(orderBy)){
					page.setOrderBy("dataDate asc");
				}else{
					page.setOrderBy(orderBy+",dataDate asc");
				}	
			}else if (searchFlag.equals("1")){
				if("".equals(orderBy)){
					page.setOrderBy("weekGroup asc");
				}else{
					page.setOrderBy(orderBy+",weekGroup asc");
				}	
			}else if (searchFlag.equals("2")){
				if("".equals(orderBy)){
					page.setOrderBy("monthGroup asc");
				}else{
					page.setOrderBy(orderBy+",monthGroup asc");
				}
			}
		}
		
		page = businessReportService.findByDate(page, businessReport,searchFlag,groupName); 
        page.setOrderBy(orderBy);
        
        model.addAttribute("page", page);
        
//---图表
        Date beforeDate=null;
		Date afterDate=null;
        
        
    	if(searchFlag.equals("0")||searchFlag.equals("")){
			searchFlag="0";
				beforeDate=businessReport.getCreateDate();
				afterDate=businessReport.getDataDate();
		}else if (searchFlag.equals("1")){
				beforeDate =DateUtils.getMonday(businessReport.getCreateDate());
			    afterDate =DateUtils.getSunday(businessReport.getDataDate());
		}else if (searchFlag.equals("2")){
				beforeDate =DateUtils.getFirstDayOfMonth(businessReport.getCreateDate());
				afterDate =DateUtils.getLastDayOfMonth(businessReport.getDataDate());
		}
        
        if(StringUtils.isEmpty(searchFlag)){
			searchFlag = "0";
		}
        //model.addAttribute("adsData", businessReportService.getAdsQuantity(businessReport, beforeDate, afterDate,null));
        
        Map<String,Map<String,Integer>> adsMap=businessReportService.getAdsQuantity(businessReport, beforeDate, afterDate,null,groupName);
        if(searchFlag.equals("0")&&adsMap!=null&&adsMap.size()>0){
        	Map<String,Map<String,Integer>> newAdsMap=Maps.newLinkedHashMap();
        	 for (Map.Entry<String, Map<String, Integer>> entry: adsMap.entrySet()) { 
        	    String country =entry.getKey();
        		Map<String,Integer> tempMap=entry.getValue();
        		Map<String,Integer> newTempMap=newAdsMap.get(country);
        		if(newTempMap==null){
        			newTempMap=Maps.newLinkedHashMap();
        			newAdsMap.put(country, newTempMap);
        		}
        		Date sDate=businessReport.getCreateDate();
    			Date eDate=businessReport.getDataDate();
        		while(!sDate.after(eDate)){
        			 if(tempMap.get(formatDay.format(sDate))==null){
        				   newTempMap.put(formatDay.format(sDate),0);
        			 }else{
        				   newTempMap.put(formatDay.format(sDate), tempMap.get(formatDay.format(sDate)));
        			 }
        			 sDate = DateUtils.addDays(sDate,1);
        		}
        	}
        	 model.addAttribute("adsData",newAdsMap);
        }else{
        	 model.addAttribute("adsData",adsMap);
        }
       
        
		List<String> xAxis = Lists.newArrayList();
		List<String> tempAxis=Lists.newArrayList();
		TreeMap<Long, String> timeMap = Maps.newTreeMap();
		TreeMap<Long, String> timeMapOrder = Maps.newTreeMap();
			if(!startDate.after(endDate)){
				DateFormat format3 = null;
				if(endDate.getYear()==startDate.getYear()){
					format3 = FORMAT1;
				}else{
					format3 = FORMAT2;
				}	
				int i = 0 ;
				
				
				if(searchFlag.equals("0")){
					Date date = beforeDate;
					while(!date.after(afterDate)){
						i++;
						xAxis.add("'"+format3.format(date)+"'");
						tempAxis.add(formatDay.format(date));
						if(!date.after(datet)){
							timeMap.put(date.getTime(), "0");
						}
						date = DateUtils.addDays(beforeDate, i);
					}
					
				}else if (searchFlag.equals("1")){
					Date date = afterDate;
					Date datetSunday=DateUtils.getSunday(datet);
					while(date.after(beforeDate)){
						i++;
						if(!date.after(datetSunday)){
							timeMap.put(date.getTime(), "0");
						}
						timeMapOrder.put(date.getTime(), "0");
						String key = formatWeek.format(date);
						int year =DateUtils.getSunday(date).getYear()+1900;
						int week =  Integer.parseInt(key.substring(4));
						if(week==53){
			                year =DateUtils.getMonday(date).getYear()+1900;
					    }
						if(week<10){
							key = year+"-0"+week;
						}else{
							key =year+"-"+week;
						}
						tempAxis.add(key);
						date = DateUtils.addDays(afterDate, -7*i);
						
					}
					
					//迭代timeMap 排序时间
					for (Iterator<Entry<Long, String>> iterator = timeMapOrder.entrySet().iterator(); iterator.hasNext();) {
						Entry<Long, String> entry = iterator.next();
						 long  lg=entry.getKey();
						 Date datelg = new Date(lg);
						 xAxis.add("'"+DateUtils.getWeekOfYear(datelg)+"周'");
					}
						
				}else if (searchFlag.equals("2")){
					Date date = beforeDate;
					Date datetFirstDay= DateUtils.addMonths(DateUtils.getFirstDayOfMonth(datet),1);
					SimpleDateFormat format = new SimpleDateFormat("yyyy/M");
					while(!date.after(afterDate)){
						i++;
						xAxis.add("'"+format.format(date)+"月'");
						tempAxis.add(formatMonth.format(date));
						if(!date.after(datetFirstDay)){
							timeMap.put(date.getTime(), "0");
						}
						date = DateUtils.addMonths(beforeDate,i);
					}
						
				}
				
			}
			Map<String,List<String>> sessionsData = Maps.newHashMap();
			Map<String,List<String>> ordersPlacedData = Maps.newHashMap();
			Map<String,List<String>> conversionData = Maps.newHashMap();
			
			DecimalFormat df = new DecimalFormat("#.00");
			
			for (BusinessReport bReport : page.getList()) {
				
				String key = DictUtils.getDictLabel(bReport.getCountry(),"platform","");
				key = key.split("\\|")[0];
				String sessions = bReport.getSessions()==null?"0":bReport.getSessions()+"";
				String ordersPlaced = bReport.getOrdersPlaced()==null?"0":bReport.getOrdersPlaced()+"";
				String conv = bReport.getConversion()==null?"0":bReport.getConversion()+"";
				Date date = bReport.getDataDate();
				if (searchFlag.equals("2")) {
					date = DateUtils.addMonths(date, -1);
				}
				//重新组装dataDate
				if(searchFlag.equals("1")){
					bReport.setDateSpan("("+FORMAT1.format(DateUtils.getMonday(date))+"一"+FORMAT1.format(DateUtils.getSunday(date))+")");
				}else if(searchFlag.equals("2")){
					bReport.setDateSpan("("+FORMAT1.format(DateUtils.addMonths(date, -1))+"一"+FORMAT1.format(DateUtils.addDays(date, -1))+")");
				}
				
				List<Long> time = Lists.newArrayList(timeMap.keySet());
				if(!("0".equals(sessions))){
					List<String> temp = sessionsData.get(key);
					if(temp==null){
						temp = Lists.newArrayList();
						for (Iterator<Entry<Long, String>> iterator = timeMap.entrySet().iterator(); iterator.hasNext();) {
							Entry<Long, String> entry = iterator.next();
							if(entry.getKey().equals(datet.getTime())){
								
								if(searchFlag.equals("0")){
									if(!key.equals("美国")&&!key.equals("加拿大")){
										temp.add("0");
									}
									break;
								}else{
									temp.add("0");
								}
								
							}else{
								temp.add("0");
							}
						}
						sessionsData.put(key, temp);
						temp = sessionsData.get(key);
					}
					if(time.indexOf(date.getTime())!=-1){
						int index = time.indexOf(date.getTime());
						if(index<temp.size()){
							temp.set(index,sessions);
						}
					}
				}
				if(!("0".equals(ordersPlaced))){
					List<String> temp = ordersPlacedData.get(key);
					if(temp==null){
						temp = Lists.newArrayList();
						for (Iterator<Entry<Long, String>> iterator = timeMap.entrySet().iterator(); iterator.hasNext();) {
							Entry<Long, String> entry = iterator.next();
							if(entry.getKey().equals(datet.getTime())){
								if(searchFlag.equals("0")){
									if(!key.equals("美国")&&!key.equals("加拿大")){
										temp.add("0");
									}
									break;
								}else{
									temp.add("0");
								}
							}else{
								temp.add("0");
							}
						}
						ordersPlacedData.put(key, temp);
						temp = ordersPlacedData.get(key);
					}
					if(time.indexOf(date.getTime())!=-1){
						int index = time.indexOf(date.getTime());
						if(index<temp.size()){
							temp.set(index,ordersPlaced);
						}
					}
					
				}
				if(!("0".equals(conv)&&"0.0".equals(conv))){
					List<String> temp = conversionData.get(key);
					if(temp==null){
						temp = Lists.newArrayList();
						for (Iterator<Entry<Long, String>> iterator = timeMap.entrySet().iterator(); iterator.hasNext();) {
							Entry<Long, String> entry = iterator.next();
							if(entry.getKey().equals(datet.getTime())){
								if(searchFlag.equals("0")){
									if(!key.equals("美国")&&!key.equals("加拿大")){
										temp.add("0");
									}
									break;
								}else{
									temp.add("0");
								}
							}else{
								temp.add("0");
							}
						}
						
						conversionData.put(key, temp);
						temp = conversionData.get(key);
					}
					
					if(time.indexOf(date.getTime())!=-1){
						int index = time.indexOf(date.getTime());
						if(index<temp.size()){
							temp.set(index,df.format(Float.parseFloat(conv)));
						}
					}
				}
			}
			
		if(StringUtils.isNotBlank(businessReport.getCountry())){
			String key = DictUtils.getDictLabel(businessReport.getCountry(),"platform","");
			key = key.split("\\|")[0];
			Map<String,String> sessionsMap=Maps.newLinkedHashMap();
			if(sessionsData.size()>0){
				int i=0;
				for (String sessionData: sessionsData.get(key)) {
					sessionsMap.put(tempAxis.get(i), sessionData);
					i++;
				}
				model.addAttribute("sessionsMap", sessionsMap);
			}
		}
		
		model.addAttribute("sessionsData", sessionsData);
		model.addAttribute("conversionData", conversionData);
		model.addAttribute("ordersPlacedData", ordersPlacedData);
		model.addAttribute("xAxis", xAxis.toString());
	//	model.addAttribute("tempAxis", tempAxis.toString());
		
		model.addAttribute("active", active);
		
		return "modules/amazoninfo/businessReportListByDate";
	}
	
	
	
	private final static DateFormat FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	private final static DateFormat FORMAT1 = new SimpleDateFormat("M/d");
	
	private final static DateFormat FORMAT2 = new SimpleDateFormat("yyyy/M/d");
	
	@RequiresPermissions("amazoninfo:businessReport:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(BusinessReport businessReport, HttpServletRequest request, HttpServletResponse response) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(businessReport.getCreateDate()==null){
			businessReport.setCreateDate(DateUtils.addDays(today, -2));
		}
		if(businessReport.getDataDate()==null){
			businessReport.setDataDate(DateUtils.addDays(today,-2));
		}	
		if(StringUtils.isBlank(businessReport.getCountry())){
			businessReport.setCountry("de");
		}
		Page<BusinessReport> page = new Page<BusinessReport>(request, response);
		page.setPageNo(1);
		page.setPageSize(10000);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("sessions desc");
		}else{
			page.setOrderBy(orderBy+",sessions desc");
		}	
        page = businessReportService.find(page, businessReport); 
        List<BusinessReport> data = page.getList();
        for (BusinessReport bus : data) {
        	String name = amazonProductService.findProductName(bus.getChildAsin(), bus.getCountry());
        	if(StringUtils.isNotEmpty(name)){
        		bus.setTitle(name);
        	}
		}
		try {
            String fileName = "报表数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
    		new ExportExcel("报表数据("+FORMAT.format(businessReport.getCreateDate())+"-"+FORMAT.format(businessReport.getDataDate())+")", BusinessReport.class).setDataList(data).deleteColumn(1).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/businessReport/?repage";
    }
	
	@RequiresPermissions("amazoninfo:businessReport:view")
	@RequestMapping(value = "exportByDate", params="searchFlag" )
	public String exportFileByDate(@RequestParam(value ="searchFlag") String searchFlag,String groupName,BusinessReport businessReport, HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, ParseException {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(businessReport.getCreateDate()==null){
			businessReport.setCreateDate(DateUtils.addDays(today, -2));
		}
		if(businessReport.getDataDate()==null){
			businessReport.setDataDate(DateUtils.addDays(today,-2));
		}	
		if(StringUtils.isBlank(businessReport.getCountry())){
			businessReport.setCountry("de");
		}
		Page<BusinessReport> page = new Page<BusinessReport>(request, response);
		page.setPageNo(1);
		page.setPageSize(100000);
		String orderBy = page.getOrderBy();
		String titleStr = "";
		if(searchFlag.equals("0")||searchFlag.equals("")){
			if("".equals(orderBy)){
				page.setOrderBy("dataDate asc");
			}else{
				page.setOrderBy(orderBy+",dataDate asc");
			}	
			titleStr="ByDay";
		}else if (searchFlag.equals("1")){
			if("".equals(orderBy)){
				page.setOrderBy("weekGroup asc");
			}else{
				page.setOrderBy(orderBy+",weekGroup asc");
			}	
			titleStr="ByWeek";
		}else if (searchFlag.equals("2")){
			if("".equals(orderBy)){
				page.setOrderBy("monthGroup asc");
			}else{
				page.setOrderBy(orderBy+",monthGroup asc");
			}
			titleStr="ByMonth";
		}	
		
		page = businessReportService.findByDate(page, businessReport,searchFlag,groupName); 
		List<BusinessReport> data = page.getList();
		//查询数据时dao处理月份错误，为不影响其他地方，此处得到月份-1
		if (searchFlag.equals("2")) {
			for (BusinessReport businessReport2 : data) {
				businessReport2.setDataDate(DateUtils.addMonths(businessReport2.getDataDate(), -1));
			}
		}
		Collections.reverse(data);
		try {
			String fileName = titleStr+"统计报表数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
			new ExportExcel(titleStr+"统计报表数据("+FORMAT.format(businessReport.getCreateDate())+"-"+FORMAT.format(businessReport.getDataDate())+")", BusinessReport.class).setDataList(data).deleteColumn(2).deleteColumn(2).deleteColumn(3).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/businessReport/?repage";
	}
	
	@RequiresPermissions("amazoninfo:businessReport:view")
	@RequestMapping(value = {"count"})
	public String count(String active,String date1, String date2, String productName, String searchFlag, HttpServletRequest request, HttpServletResponse response, Model model) throws NumberFormatException, ParseException {
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
		
		Date today = new Date();
		if(StringUtils.isEmpty(active)){
			active = "1";
		}
		if(StringUtils.isEmpty(searchFlag)){
			searchFlag = "0";
		}
		
		Date datet = null;
		if(today.getHours()>=8){
			datet = DateUtils.addDays(today, -2);
		}else{
			datet = DateUtils.addDays(today, -3);
		}
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str=sdf.format(datet);
		datet=sdf.parse(str);
		
		Date endDate =null;
		Date startDate=null;

		//分类型查询
		String type = "日"; 
		if("1".equals(searchFlag)){
			//按周查询
			if(StringUtils.isBlank(date1)){
				date1 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -20), formatWeek, 5, "-");
				Calendar cal = Calendar.getInstance();
				cal.setTime(today);
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				if (dayOfWeek > 3) {
					date2 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -1), formatWeek, 5, "-");
				} else {
					date2 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -2), formatWeek, 5, "-");
				}
			} else {
				if ("2015-53".equals(date1)) {
					date1 = "2016-01";
				}
				if ("2015-53".equals(date2)) {
					date2 = "2016-01";
				}
			}
			startDate = formatWeek.parse(date1);
			endDate = formatWeek.parse(date2);
			type = "周";
			sdf = formatWeek;
		}else if("2".equals(searchFlag)){
			//按月查询
			if(StringUtils.isBlank(date1)){
				//date2 = formatMonth.format(today);
				date2 = formatMonth.format(DateUtils.addMonths(today, -1));
				date1 = formatMonth.format(DateUtils.addMonths(today, -12));
			}
			startDate = formatMonth.parse(date1);
			endDate = formatMonth.parse(date2);
			type = "月";
			sdf = formatMonth;
		}else{
			if(StringUtils.isBlank(date1)){
				date2 = formatDay.format(datet);
				date1 = formatDay.format(DateUtils.addDays(datet, -20));
			}
			startDate = formatDay.parse(date1);
			endDate = formatDay.parse(date2);
		}

		List<String> xAxis = Lists.newArrayList();
		List<String> tempAxis = Lists.newArrayList();
//---图表
        Date beforeDate=null;
		Date afterDate=null;
        
		if (searchFlag.equals("0") || searchFlag.equals("")) {
			searchFlag = "0";
			beforeDate = startDate;
			afterDate = endDate;
		} else if (searchFlag.equals("1")) {
			beforeDate = DateUtils.getMonday(startDate);
			afterDate = DateUtils.getSunday(endDate);
		} else if (searchFlag.equals("2")) {
			beforeDate = DateUtils.getFirstDayOfMonth(startDate);
			afterDate = DateUtils.getLastDayOfMonth(endDate);
		}
		
		TreeMap<Long, String> timeMap = Maps.newTreeMap();
		TreeMap<Long, String> timeMapOrder = Maps.newTreeMap();
		if(!startDate.after(endDate)){
			DateFormat format3 = null;
			if(endDate.getYear()==startDate.getYear()){
				format3 = FORMAT1;
			}else{
				format3 = FORMAT2;
			}	
			int i = 0 ;
			
			if(searchFlag.equals("0")){
				Date date = beforeDate;
				while(!date.after(afterDate)){
					i++;
					xAxis.add("'"+format3.format(date)+"'");
					tempAxis.add(formatDay.format(date));
					if(!date.after(datet)){
						timeMap.put(date.getTime(), "0");
					}
					date = DateUtils.addDays(beforeDate, i);
				}
			}else if (searchFlag.equals("1")){
				Date date = afterDate;
				Date datetSunday=DateUtils.getSunday(datet);
				while(date.after(beforeDate)){
					i++;
					if(!date.after(datetSunday)){
						timeMap.put(date.getTime(), "0");
					}
					timeMapOrder.put(date.getTime(), "0");
					tempAxis.add(formatWeek.format(date));
					date = DateUtils.addDays(afterDate, -7*i);
				}
				Collections.reverse(tempAxis);
				//迭代timeMap 排序时间
				for (Iterator<Entry<Long, String>> iterator = timeMapOrder.entrySet().iterator(); iterator.hasNext();) {
					Entry<Long, String> entry = iterator.next();
				    long lg=entry.getKey();
					Date datelg = new Date(lg);
					xAxis.add("'"+DateUtils.getWeekStr(DateUtils.addDays(datelg, 1), formatWeek, 5, "-")+"周'");
				}
			}else if (searchFlag.equals("2")){
				Date date = beforeDate;
				Date datetFirstDay= DateUtils.addMonths(DateUtils.getFirstDayOfMonth(datet),1);
				SimpleDateFormat format = new SimpleDateFormat("yyyy/M");
				while(!date.after(afterDate)){
					i++;
					xAxis.add("'"+format.format(date)+"月'");
					tempAxis.add(formatMonth.format(date));
					if(!date.after(datetFirstDay)){
						timeMap.put(date.getTime(), "0");
					}
					date = DateUtils.addMonths(beforeDate,i);
				}
			}
		}
		if (StringUtils.isNotEmpty(productName)) {
			try {
				productName = URLDecoder.decode(productName, "utf-8");
			} catch (UnsupportedEncodingException e) {}
			List<BusinessReport> rs = businessReportService.findCountData(productName, date1, date2, searchFlag);

			model.addAttribute("adsData", businessReportService.getAdsQuantityByName(searchFlag, date1, date2,Sets.newHashSet(productName)));
			 
			//组合两个map在前台显示数据
			Map<String,Map<String,Integer>> sessionDataMap=Maps.newLinkedHashMap();
			Map<String,Map<String,Float>> conversionDataMap=Maps.newLinkedHashMap();
			Set<String> countrySet=Sets.newHashSet();
			
			Map<String,List<String>> sessionsData = Maps.newHashMap();
			Map<String,List<String>> conversionData = Maps.newHashMap();
			for (BusinessReport businessReport : rs) {
				String sessionKey=businessReport.getTitle();//sdf.format(businessReport.getDataDate());
				Map<String,Integer> tempMap=Maps.newLinkedHashMap();
				if(sessionDataMap.get(sessionKey)!=null){
					tempMap=sessionDataMap.get(businessReport.getTitle());
				}
				tempMap.put(businessReport.getCountry(), businessReport.getSessions());
				countrySet.add(businessReport.getCountry());
				sessionDataMap.put(sessionKey, tempMap);
				
				Map<String,Float> tempMap1=Maps.newLinkedHashMap();
				if(conversionDataMap.get(sessionKey)!=null){
					tempMap1=conversionDataMap.get(businessReport.getTitle());
				}
				tempMap1.put(businessReport.getCountry(), businessReport.getConversion());
				conversionDataMap.put(sessionKey, tempMap1);
				
				String key = "";
				if(!"total".equals(businessReport.getCountry())){
					key = DictUtils.getDictLabel(businessReport.getCountry(),"platform","");
					key = key.split("\\|")[0];
				} else {
					key = "total";
				}
					
				String sessions = businessReport.getSessions()==null?"0":businessReport.getSessions()+"";
				String conv = businessReport.getConversion()==null?"0":businessReport.getConversion()+"";
				Date date = sdf.parse(businessReport.getTitle());
				List<Long> time = Lists.newArrayList(timeMap.keySet());
				if(!("0".equals(sessions))){
					List<String> temp = sessionsData.get(key);
					if(temp==null){
						temp = Lists.newArrayList();
						for (Iterator<Entry<Long, String>> iterator = timeMap.entrySet().iterator(); iterator.hasNext();) {
							Entry<Long, String> entry = iterator.next();
							if(entry.getKey().equals(datet.getTime())){
								if(!key.equals("美国")&!key.equals("加拿大")){
									temp.add("0");
								}
								break;
							}else{
								temp.add("0");
							}
						}
						sessionsData.put(key, temp);
						temp = sessionsData.get(key);
					}
					int size = time.indexOf(date.getTime());
					if(size!=-1&& size<=temp.size()-1){
						temp.set(size,sessions);
					}
				}
				if(!("0".equals(conv)&&"0.0".equals(conv))){
					List<String> temp = conversionData.get(key);
					if(temp==null){
						temp = Lists.newArrayList();
						for (Iterator<Entry<Long, String>> iterator = timeMap.entrySet().iterator(); iterator
								.hasNext();) {
							Entry<Long, String> entry = iterator.next();
							if(entry.getKey().equals(datet.getTime())){
								if(!key.equals("美国")&!key.equals("加拿大")){
									temp.add("0");
								}
								break;
							}else{
								temp.add("0");
							}
						}
						conversionData.put(key, temp);
						temp = conversionData.get(key);
					}
					int size = time.indexOf(date.getTime());
					if(size!=-1&& size<=temp.size()-1){
						temp.set(size,conv);
					}
				}
			}
			
			Map<String,Map<String,Integer>> sessionDataMap1=Maps.newLinkedHashMap();
			List<String> list = Lists.newArrayList(sessionDataMap.keySet());
			Map<String, String> tips = Maps.newHashMap();
			for (String key : list) {
				sessionDataMap1.put(key, sessionDataMap.get(key));
				if(searchFlag.equals("1")){
					Date date = sdf.parse(key);
					date = DateUtils.addDays(date, 1);	//系统设计以星期一作为第一天,此处加一
					tips.put(key, "("+FORMAT1.format(DateUtils.getMonday(date))+"一"+FORMAT1.format(DateUtils.getSunday(date))+")");
				}
			}
			if(sessionsData.size()>0){
				
				    Map<String,Map<String,String>> sessionsMap=Maps.newLinkedHashMap();
				    for (Map.Entry<String, List<String>> entry: sessionsData.entrySet()) { 
					        String country=entry.getKey();
						    int i=0;
							String key=getCountryByName(country);
							Map<String,String> temp=sessionsMap.get(key);
							if(temp==null){
								temp=Maps.newLinkedHashMap();
								sessionsMap.put(key, temp);
							}
							for (String sessionData: entry.getValue()) {
								temp.put(tempAxis.get(i), sessionData);
								i++;
							}
						
					}
					model.addAttribute("sessionsMap", sessionsMap);
			}
			
			model.addAttribute("sessionDataMap", sessionDataMap1);
			model.addAttribute("conversionDataMap", conversionDataMap);
			model.addAttribute("sessionsData", sessionsData);
			model.addAttribute("conversionData", conversionData);
			model.addAttribute("tips", tips);
			String show =  "selected: {'英国':false,'法国':false,'日本':false,'意大利':false,'西班牙':false,'加拿大':false},";
			Set<String> set = sessionsData.keySet();
			if(set.size()<=2){
				show = "";
			}else{
				if(!(set.contains("美国")||set.contains("德国"))){
					for (String country : set) {
						show = show.replace("'"+country+"':false","'"+country+"':true");
						break;
					}
				}
			}	
			model.addAttribute("show", show);
			model.addAttribute("countrySet", Lists.newArrayList(countrySet));
		}
		model.addAttribute("xAxis", xAxis.toString());
		model.addAttribute("productNames", amazonProductService.findAllProductName());
		model.addAttribute("date1",date1);
		model.addAttribute("date2",date2);
		model.addAttribute("type",type);
		model.addAttribute("searchFlag",searchFlag);
		model.addAttribute("productName", productName);
		model.addAttribute("active", active);
		return "modules/amazoninfo/reportCountView";
	}
	
	@RequestMapping(value = {"countExport"})
	public void countExport(String date1, String date2, String productName, String active, String searchFlag, Model model, HttpServletRequest request,HttpServletResponse response)  throws NumberFormatException, ParseException{
		if (StringUtils.isEmpty(searchFlag)) {
			searchFlag = "0"; // 0: By Date 1: By Week 2: By Month
		}
		// 分平台查询
		String type = "日";
		if ("1".equals(searchFlag)) {
			// 按周查询
			type = "周";
		} else if ("2".equals(searchFlag)) {
			type = "月";
		}
		if (StringUtils.isNotEmpty(productName)) {
			try {
				productName = URLDecoder.decode(productName, "utf-8");
			} catch (UnsupportedEncodingException e) {}
			//查询数据
			List<BusinessReport> rs = businessReportService.findCountData(productName, date1, date2, searchFlag);

			// 组合两个map在前台显示数据
			Map<String, Map<String, Integer>> sessionDataMap = Maps.newLinkedHashMap();
			Map<String, Map<String, Float>> conversionDataMap = Maps.newLinkedHashMap();
			Set<String> countrySet = Sets.newHashSet();

			for (BusinessReport businessReport : rs) {
				String sessionKey = businessReport.getTitle();
				Map<String, Integer> tempMap = Maps.newLinkedHashMap();
				if (sessionDataMap.get(sessionKey) != null) {
					tempMap = sessionDataMap.get(businessReport.getTitle());
				}
				tempMap.put(businessReport.getCountry(), businessReport.getSessions());
				countrySet.add(businessReport.getCountry());
				sessionDataMap.put(sessionKey, tempMap);

				Map<String, Float> tempMap1 = Maps.newLinkedHashMap();
				if (conversionDataMap.get(sessionKey) != null) {
					tempMap1 = conversionDataMap.get(businessReport.getTitle());
				}
				tempMap1.put(businessReport.getCountry(), businessReport.getConversion());
				conversionDataMap.put(sessionKey, tempMap1);
			}

			Map<String, Map<String, Integer>> sessionDataMap1 = Maps.newLinkedHashMap();
			List<String> list = Lists.newArrayList(sessionDataMap.keySet());
			// Collections.reverse(list);
			Map<String, String> tips = Maps.newHashMap();
			DateFormat sdf = new SimpleDateFormat("yyyy-ww");
			for (String key : list) {
				sessionDataMap1.put(key, sessionDataMap.get(key));
				if(searchFlag.equals("1")){
					Date date = sdf.parse(key);
					date = DateUtils.addDays(date, 1);	//系统设计以星期一作为第一天,此处加一
					tips.put(key, "("+FORMAT1.format(DateUtils.getMonday(date))+"一"+FORMAT1.format(DateUtils.getSunday(date))+")");
				}
			}
			List<String> countryList = Lists.newArrayList(countrySet);
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row1 = sheet.createRow(0);

			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// 设置Excel中的边框(表头的边框)
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			style.setBottomBorderColor(HSSFColor.BLACK.index);

			style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
			style.setLeftBorderColor(HSSFColor.BLACK.index);

			style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
			style.setRightBorderColor(HSSFColor.BLACK.index);

			style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
			style.setTopBorderColor(HSSFColor.BLACK.index);
			// 设置字体
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 16); // 字体高度
			font.setFontName(" 黑体 "); // 字体
			font.setBoldweight((short) 16);
			style.setFont(font);

			// 标题行
			HSSFRow row = sheet.createRow(1);
			row.setHeight((short) 600);//
			HSSFCell cell = null;

			countryList.add(0, productName);

			CellStyle cellStyle = wb.createCellStyle();
			cellStyle.setDataFormat(wb.createDataFormat().getBuiltinFormat("0.00%"));
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);

			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);

			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setRightBorderColor(HSSFColor.BLACK.index);

			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);

			CellStyle titleStyle = wb.createCellStyle();
			titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFFont titleFont = wb.createFont();
			titleFont.setFontHeightInPoints((short) 28); // 字体高度
			titleFont.setFontName(" 黑体 "); // 字体
			font.setBoldweight((short) 28);
			titleStyle.setFont(font);

			CellStyle contentStyle = wb.createCellStyle();
			contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);

			contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);

			contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setRightBorderColor(HSSFColor.BLACK.index);

			contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			contentStyle.setTopBorderColor(HSSFColor.BLACK.index);

			// sessionDataMap1 conversionDataMap
			for (String country : countryList) {
				if("total".equals(country)){
					countryList.remove(country);
					break;
				}
			}
			countryList.add(countryList.size(), "total");
			if ("1".equals(active)) {
				for (int i = 0; i < countryList.size() * 2 - 1; i++) {
					cell = row1.createCell(i);
					cell.setCellStyle(titleStyle);
				}
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, countryList.size() * 2 - 2));
				row1.getCell(0).setCellValue(
						productName + " 分栏统计(" + date1 + type + "~" + date2 + type + ")");

				int num = 1;
				for (int i = 0; i < countryList.size() * 2 - 1; i++) {
					cell = row.createCell(i);
					if (i == 0) {
						cell.setCellValue(productName);
					} else if (i % 2 != 0) {
						if (i == 1) {
							cell.setCellValue(DictUtils.getDictLabel(countryList.get(i), "platform", "总计"));
						} else {
							cell.setCellValue(DictUtils.getDictLabel(countryList.get(i - num), "platform", "总计"));
							num += 1;
						}
					}
					cell.setCellStyle(style);
					sheet.autoSizeColumn((short) i);
				}

				for (int i = 1; i < countryList.size() * 2 - 1; i = i + 2) {
					sheet.addMergedRegion(new CellRangeAddress(1, 1, i, i + 1));
				}

				for (int i = 2; i <= list.size() + 1; i++) {
					row = sheet.createRow(i);
					row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(list.get(i - 2) + type + (tips.get(list.get(i - 2))==null?"":tips.get(list.get(i - 2))));
					row.getCell(0).setCellStyle(contentStyle);
					int num1 = 1;
					for (int j = 1; j < countryList.size() * 2 - 1; j = j + 2) {
						Integer session = 0;
						Float conver = 0f;
						if (j == 1) {
							session = sessionDataMap1.get(list.get(i - 2)).get(countryList.get(j));
							conver = conversionDataMap.get(list.get(i - 2)).get(countryList.get(j));
						} else {
							session = sessionDataMap1.get(list.get(i - 2)).get(countryList.get(j - num1));
							conver = conversionDataMap.get(list.get(i - 2)).get(countryList.get(j - num1));
							num1 += 1;
						}
						row.createCell(j, Cell.CELL_TYPE_NUMERIC).setCellValue(session == null ? 0 : session);
						row.createCell(j + 1, Cell.CELL_TYPE_NUMERIC).setCellValue(conver == null ? 0 : conver / 100);
						row.getCell(j + 1).setCellStyle(cellStyle);
						row.getCell(j).setCellStyle(contentStyle);
					}
				}

				// total
				row = sheet.createRow(list.size() + 2);
				for (int i = 0; i < countryList.size() * 2 - 1; i++) {
					cell = row.createCell(i);
					row.getCell(i).setCellStyle(contentStyle);
					if (i == 0) {
						cell.setCellValue("total");
					} else if (i % 2 != 0) {
						int totalSession = 0;
						for (int j = 2; j <= list.size() + 1; j++) {
							totalSession = totalSession
									+ (int) (sheet.getRow(j).getCell(i).getNumericCellValue());
						}
						cell.setCellValue(totalSession);
					}
				}
			} else if ("2".equals(active)) {
				for (int i = 0; i < countryList.size(); i++) {
					cell = row1.createCell(i);
					cell.setCellStyle(titleStyle);
				}
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, countryList
						.size() - 1));
				row1.getCell(0).setCellValue(
						productName + " session统计(" + date1 + type + "~" + date2 + type + ")");

				for (int i = 0; i < countryList.size(); i++) {
					cell = row.createCell(i);
					if (i == 0) {
						cell.setCellValue(productName);
					} else {
						cell.setCellValue(DictUtils.getDictLabel(countryList.get(i), "platform", ""));
					}
					cell.setCellStyle(style);
					sheet.autoSizeColumn((short) i);
				}
				for (int i = 2; i <= list.size() + 1; i++) {
					row = sheet.createRow(i);
					row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(list.get(i - 2) + type + (tips.get(list.get(i - 2))==null?"":tips.get(list.get(i - 2))));
					row.getCell(0).setCellStyle(contentStyle);
					for (int j = 1; j < countryList.size(); j++) {
						Integer session = sessionDataMap1.get(list.get(i - 2)).get(countryList.get(j));
						row.createCell(j, Cell.CELL_TYPE_NUMERIC).setCellValue(session == null ? 0 : session);
						row.getCell(j).setCellStyle(contentStyle);
					}
				}

				row = sheet.createRow(list.size() + 2);
				for (int i = 0; i < countryList.size(); i++) {
					cell = row.createCell(i);
					row.getCell(i).setCellStyle(contentStyle);
					if (i == 0) {
						cell.setCellValue("total");
					} else {
						int totalSession = 0;
						for (int j = 2; j <= list.size() + 1; j++) {
							totalSession = totalSession
									+ (int) (sheet.getRow(j).getCell(i).getNumericCellValue());
						}
						cell.setCellValue(totalSession);
					}
				}
			} else {
				for (int i = 0; i < countryList.size(); i++) {
					cell = row1.createCell(i);
					cell.setCellStyle(titleStyle);
				}
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, countryList.size() - 1));
				row1.getCell(0).setCellValue(
						productName + " conversion统计(" + date1 + type + "~" + date2 + type + ")");

				for (int i = 0; i < countryList.size(); i++) {
					cell = row.createCell(i);
					if (i == 0) {
						cell.setCellValue(productName);
					} else {
						cell.setCellValue(DictUtils.getDictLabel(countryList.get(i), "platform", ""));
					}
					cell.setCellStyle(style);
					sheet.autoSizeColumn((short) i);
				}
				for (int i = 2; i <= list.size() + 1; i++) {
					row = sheet.createRow(i);
					row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(list.get(i - 2) + type + (tips.get(list.get(i - 2))==null?"":tips.get(list.get(i - 2))));
					row.getCell(0).setCellStyle(cellStyle);
					for (int j = 1; j < countryList.size(); j++) {
						Float conver = conversionDataMap.get(list.get(i - 2)).get(countryList.get(j));
						row.createCell(j, Cell.CELL_TYPE_NUMERIC).setCellValue(conver == null ? 0 : conver / 100);
						row.getCell(j).setCellStyle(cellStyle);
					}
				}

			}
			sheet.autoSizeColumn((short) 0);
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				
				SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");
				String fileName = "businessReport" + sdf1.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition",
						"attachment;filename=" + fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	@RequiresPermissions("amazoninfo:businessReport:view")
	@RequestMapping(value = {"countByType"})
	public String countByType(String date1,String date2, String searchFlag,String typeName,String groupName,String active, Model model)  throws NumberFormatException, ParseException{
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
		
		Date today = new Date();
		if(StringUtils.isEmpty(active)){
			active = "1";
		}
		if(StringUtils.isEmpty(searchFlag)){
			searchFlag = "0";
		}
		if(StringUtils.isNotEmpty(typeName)){
			try {
				typeName = URLDecoder.decode(typeName, "UTF-8");
			} catch (UnsupportedEncodingException e) {}
		}
		
		
		Date datet = null;
		if(today.getHours()>=8){
			datet = DateUtils.addDays(today, -2);
		}else{
			datet = DateUtils.addDays(today, -3);
		}
		
		DateFormat sdf = formatDay;
		String str=sdf.format(datet);
		datet=sdf.parse(str);
		
		Date endDate =null;
		Date startDate=null;

		//分类型查询
		String type = "日"; 
		if("1".equals(searchFlag)){
			//按周查询
			if(StringUtils.isBlank(date1)){
				date1 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -20), formatWeek, 5, "-");
				Calendar cal = Calendar.getInstance();
				cal.setTime(today);
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				if (dayOfWeek > 3) {
					date2 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -1), formatWeek, 5, "-");
				} else {
					date2 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -2), formatWeek, 5, "-");
				}
			} else {
				if ("2015-53".equals(date1)) {
					date1 = "2016-01";
				}
				if ("2015-53".equals(date2)) {
					date2 = "2016-01";
				}
			}
			startDate = formatWeek.parse(date1);
			endDate = formatWeek.parse(date2);
			type = "周";
			sdf = formatWeek;
		}else if("2".equals(searchFlag)){
			//按月查询
			if(StringUtils.isBlank(date1)){
				date2 = formatMonth.format(DateUtils.addMonths(today, -1));
				date1 = formatMonth.format(DateUtils.addMonths(today, -12));
			}
			startDate = formatMonth.parse(date1);
			endDate = formatMonth.parse(date2);
			type = "月";
			sdf = formatMonth;
		}else{
			if(StringUtils.isBlank(date1)){
				date2 = formatDay.format(datet);
				date1 = formatDay.format(DateUtils.addDays(datet, -20));
			}
			startDate = formatDay.parse(date1);
			endDate = formatDay.parse(date2);
		}
			
			Map<String,Map<String,Object[]>> rs = new HashMap<String,Map<String,Object[]>>();
			
			Map<String,Map<String,Integer>> adsDataMap=Maps.newLinkedHashMap();
			if(StringUtils.isBlank(typeName)){
				List<Dict> dict=psiTypeGroupService.getProductType(groupName);
				Set<String> groupTypeName=new HashSet<String>();
				for (Dict dict2 : dict) {
					groupTypeName.add(dict2.getValue());
				}
				if(groupTypeName!=null&&groupTypeName.size()>0){
					rs = businessReportService.findCountTypesDataByGroup(groupTypeName, date1, date2, searchFlag);
					adsDataMap= businessReportService.getAdsQuantity(searchFlag, date1, date2,groupTypeName);
					model.addAttribute("adsData",adsDataMap);
				}
			}else{
				rs = businessReportService.findCountTypesData(typeName, searchFlag, date1, date2);
				adsDataMap=businessReportService.getAdsQuantity(searchFlag, date1, date2,Sets.newHashSet(typeName));
				model.addAttribute("adsData",adsDataMap);
			}
			
			
			
			//组合两个map在前台显示数据
			Map<String,Map<String,Integer>> sessionDataMap=Maps.newLinkedHashMap();
			Map<String,Map<String,Float>> conversionDataMap=Maps.newLinkedHashMap();
			
			Map<String,List<String>> sessionsData = Maps.newHashMap();
			Map<String,List<String>> conversionData = Maps.newHashMap();
			
			List<String> xAxis = Lists.newArrayList();
			List<String> tempAxis = Lists.newArrayList();
			//---图表
	        Date beforeDate=null;
			Date afterDate=null;
	        
			if (searchFlag.equals("0") || searchFlag.equals("")) {
				searchFlag = "0";
				beforeDate = startDate;
				afterDate = endDate;
			} else if (searchFlag.equals("1")) {
				beforeDate = DateUtils.getMonday(startDate);
				afterDate = DateUtils.getSunday(endDate);
			} else if (searchFlag.equals("2")) {
				beforeDate = DateUtils.getFirstDayOfMonth(startDate);
				afterDate = DateUtils.getLastDayOfMonth(endDate);
			}
			
			TreeMap<Long, String> timeMap = Maps.newTreeMap();
			TreeMap<Long, String> timeMapOrder = Maps.newTreeMap();
			DateFormat format3 = null;
			if(!startDate.after(endDate)){
				
				if(endDate.getYear()==startDate.getYear()){
					format3 = FORMAT1;
				}else{
					format3 = FORMAT2;
				}	
				int i = 0 ;
				
				if(searchFlag.equals("0")){
					Date date = beforeDate;
					while(!date.after(afterDate)){
						i++;
						xAxis.add("'"+format3.format(date)+"'");
						tempAxis.add(formatDay.format(date));
						if(!date.after(datet)){
							timeMap.put(date.getTime(), "0");
						}
						date = DateUtils.addDays(beforeDate, i);
					}
				}else if (searchFlag.equals("1")){
					Date date = afterDate;
					Date datetSunday=DateUtils.getSunday(datet);
					while(date.after(beforeDate)){
						i++;
						if(!date.after(datetSunday)){
							timeMap.put(date.getTime(), "0");
						}
						timeMapOrder.put(date.getTime(), "0");
						tempAxis.add(formatWeek.format(date));
						date = DateUtils.addDays(afterDate, -7*i);
					}
					Collections.reverse(tempAxis);
					//迭代timeMap 排序时间
					for (Iterator<Entry<Long, String>> iterator = timeMapOrder.entrySet().iterator(); iterator.hasNext();) {
						Entry<Long, String> entry = iterator.next();
					    long lg=entry.getKey();
						Date datelg = new Date(lg);
						String weekNum = formatWeek.format(DateUtils.addDays(datelg, 1));
						if (datelg.getMonth()==11 && "01".equals(weekNum.split("-")[1])) {
							Integer year = datelg.getYear() + 1901;
							weekNum = year + "-" + weekNum.split("-")[1];
						}
						xAxis.add("'"+weekNum+"周'");
					}
					Map<String, String> tips = Maps.newHashMap();
					for (String key : xAxis) {
						key = key.substring(1, key.length()-2);
						Date day = formatWeek.parse(key);
						day = DateUtils.addDays(day, 1);	//系统设计以星期一作为第一天,此处加一
						tips.put(key + "周", "("+FORMAT1.format(DateUtils.getMonday(day))+"一"+FORMAT1.format(DateUtils.getSunday(day))+")");
					}
					model.addAttribute("tips", tips);
				}else if (searchFlag.equals("2")){
					Date date = beforeDate;
					Date datetFirstDay= DateUtils.addMonths(DateUtils.getFirstDayOfMonth(datet),1);
					while(!date.after(afterDate)){
						i++;
						xAxis.add("'"+formatMonth.format(date)+"月'");
						tempAxis.add(formatMonth.format(date));
						if(!date.after(datetFirstDay)){
							timeMap.put(date.getTime(), "0");
						}
						date = DateUtils.addMonths(beforeDate,i);
					}
				}
			}
			 for (Map.Entry<String, Map<String, Object[]>> entry : rs.entrySet()) { 
			    String country =entry.getKey();
				Map<String,Object[]> data = entry.getValue();
				sessionsData.put(country, new ArrayList<String>());
				conversionData.put(country, new ArrayList<String>());
				for (String strX : xAxis) {
					String date =  strX.replace("'", "");
					if(date.equals(sdf.format(datet))&&"com,ca".contains(country)){
						continue;
					}
					Map<String,Integer> map = sessionDataMap.get(date);
					Map<String,Float> map1 = conversionDataMap.get(date);
					if(map==null){
						map = Maps.newLinkedHashMap();
						sessionDataMap.put(date, map);
						map1 = Maps.newLinkedHashMap();
						conversionDataMap.put(date, map1);
					}
					Object[] objs = data.get(date);
					if(objs==null){
						sessionsData.get(country).add("0");
						conversionData.get(country).add("0");
						map.put(country, 0);
						map1.put(country, 0f);
					}else{
						int session = ((BigDecimal)objs[2]).intValue();
						int order = ((BigDecimal)objs[3]).intValue();
						float conv = 0f;
						if(session>0){
							conv = (float)order*100/session;
							BigDecimal bg = new BigDecimal(conv);
							conv = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						}
						sessionsData.get(country).add(session+"");
						conversionData.get(country).add(conv+"");
						map.put(country, session);
						map1.put(country, conv);
					}
				}
			}
			Map<String,Map<String,Integer>> sessionDataMap1=Maps.newLinkedHashMap();
			List<String> list = Lists.newArrayList(sessionDataMap.keySet());
			Collections.reverse(list);
			for (String key : list) {
				sessionDataMap1.put(key, sessionDataMap.get(key));
			}
			
			if(adsDataMap!=null&&adsDataMap.size()>0){
				Map<String,Map<String,Integer>> adviseDataMap=Maps.newLinkedHashMap();
				 for (Map.Entry<String, Map<String, Integer>> entry: adsDataMap.entrySet()) { 
				    String country=entry.getKey();
					Map<String,Integer> tempCountry=entry.getValue();
					Map<String,Integer> tempAds=adviseDataMap.get(country);
					if(tempAds==null){
						tempAds=Maps.newLinkedHashMap();
						adviseDataMap.put(country, tempAds);
					}
					 for (Map.Entry<String, Integer> entryRs: tempCountry.entrySet()) { 
					    String dateKey =entryRs.getKey();
						String[] arr=dateKey.split("-");
						if(searchFlag.equals("0")){
							if(endDate.getYear()==startDate.getYear()){
								String arrKey=Integer.parseInt(arr[1].toString())+"/"+Integer.parseInt(arr[2].toString());
								tempAds.put(arrKey, entryRs.getValue());
							}else{
								String arrKey=arr[0]+"/"+Integer.parseInt(arr[1].toString())+"/"+Integer.parseInt(arr[2].toString());
								tempAds.put(arrKey, entryRs.getValue());
							}
							
						}else if (searchFlag.equals("1")){
							tempAds.put(dateKey+"周", entryRs.getValue());
						}else if (searchFlag.equals("2")){
							tempAds.put(dateKey+"月", entryRs.getValue());
						}
					}
				}
				model.addAttribute("adviseDataMap", adviseDataMap);
			}
			model.addAttribute("sessionDataMap", sessionDataMap1);
			model.addAttribute("conversionDataMap", conversionDataMap);
			model.addAttribute("sessionsData", sessionsData);
			if(sessionsData.size()>0){
			    Map<String,Map<String,String>> sessionsMap=Maps.newLinkedHashMap();
			    
			    for (Map.Entry<String, List<String>> entry: sessionsData.entrySet()) { 
				        String country=entry.getKey();
					    int i=0;
						String key=getCountryByName(country);
						Map<String,String> temp=sessionsMap.get(key);
						if(temp==null){
							temp=Maps.newLinkedHashMap();
							sessionsMap.put(key, temp);
						}
						for (String sessionData: entry.getValue()) {
							temp.put(tempAxis.get(i), sessionData);
							i++;
						}
					
				}
				model.addAttribute("sessionsMap", sessionsMap);
		}
		
			model.addAttribute("conversionData", conversionData);
			model.addAttribute("countrySet", Lists.newArrayList(sessionsData.keySet()));
		//}
		List<String> typesAll = Lists.newArrayList();
		for (Dict dict : DictUtils.getDictList("product_type")) {
			String dictType = HtmlUtils.htmlUnescape(dict.getValue());
			typesAll.add(dictType);
		}
		model.addAttribute("xAxis", xAxis.toString());
		model.addAttribute("tempAxis", tempAxis.toString());
		model.addAttribute("typeNames",typesAll);
		model.addAttribute("startDate",startDate);
		model.addAttribute("endDate",endDate);
		model.addAttribute("date1",date1);
		model.addAttribute("date2",date2);
		model.addAttribute("searchFlag",searchFlag);
		model.addAttribute("typeName", HtmlUtils.htmlUnescape(typeName));
		model.addAttribute("active", active);
		model.addAttribute("groupName", groupName);
		model.addAttribute("groupType", psiTypeGroupService.getAllList());
		if(StringUtils.isNotBlank(groupName)){
			model.addAttribute("groupTypeName","unGrouped".equals(groupName)?"unGrouped":psiTypeGroupService.get(groupName).getName());
		}
		return "modules/amazoninfo/reportTypesCountView";
	}
	
	
	@RequestMapping(value = {"countByTypeExport"})
	public void countByTypeExport(String date1,String date2,String searchFlag,String typeName,String groupName,String active, 
			Model model, HttpServletRequest request,HttpServletResponse response) throws ParseException{
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");

		Date today = new Date();
		if (StringUtils.isEmpty(active)) {
			active = "1";
		}
		if (StringUtils.isEmpty(searchFlag)) {
			searchFlag = "0";
		}
		if(StringUtils.isNotEmpty(typeName)){
			try {
				typeName = URLDecoder.decode(typeName, "UTF-8");
			} catch (UnsupportedEncodingException e) {}
		}

		Date datet = null;
		if (today.getHours() >= 8) {
			datet = DateUtils.addDays(today, -2);
		} else {
			datet = DateUtils.addDays(today, -3);
		}

		DateFormat sdf = formatDay;
		String str = sdf.format(datet);
		datet = sdf.parse(str);

		Date endDate = null;
		Date startDate = null;

		// 分类型查询
		String type = "日";
		if ("1".equals(searchFlag)) {
			// 按周查询
			if (StringUtils.isBlank(date1)) {
				date2 = formatWeek.format(today);
				date1 = formatWeek.format(DateUtils.addWeeks(today, -20));
				String[] dateStrs = date2.split("-");
				if ("01".equals(dateStrs[1])) {
					int year1 = Integer.parseInt(date1.split("-")[0]);
					int year2 = Integer.parseInt(date2.split("-")[0]);
					if (year1 >= year2) {
						int year = year1 + 1;
						date2 = year + "-" + dateStrs[1];
					}
				}
			}
			startDate = formatWeek.parse(date1);
			endDate = formatWeek.parse(date2);
			type = "周";
			sdf = formatWeek;
		} else if ("2".equals(searchFlag)) {
			// 按月查询
			if (StringUtils.isBlank(date1)) {
				date2 = formatMonth.format(today);
				date1 = formatMonth.format(DateUtils.addMonths(today, -12));
			}
			startDate = formatMonth.parse(date1);
			endDate = formatMonth.parse(date2);
			type = "月";
			sdf = formatMonth;
		} else {
			if (StringUtils.isBlank(date1)) {
				date2 = formatDay.format(datet);
				date1 = formatDay.format(DateUtils.addMonths(datet, -1));
			}
			startDate = formatDay.parse(date1);
			endDate = formatDay.parse(date2);
		}
		if (StringUtils.isNotEmpty(groupName)) {
			try {
				groupName = URLDecoder.decode(groupName, "utf-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		Map<String, Map<String, Object[]>> rs = new HashMap<String, Map<String, Object[]>>();

		if (StringUtils.isBlank(typeName)) {
			List<Dict> dict = psiTypeGroupService.getProductType(groupName);
			Set<String> groupTypeName = new HashSet<String>();
			for (Dict dict2 : dict) {
				groupTypeName.add(dict2.getValue());
			}
			if (groupTypeName != null && groupTypeName.size() > 0) {
				rs = businessReportService.findCountTypesDataByGroup(groupTypeName, date1, date2, searchFlag);
			}
		} else {
			rs = businessReportService.findCountTypesData(typeName, searchFlag, date1, date2);
		}

		// 组合两个map
		Map<String, Map<String, Integer>> sessionDataMap = Maps.newLinkedHashMap();
		Map<String, Map<String, Float>> conversionDataMap = Maps.newLinkedHashMap();

		Set<String> countrySet = Sets.newHashSet();
		List<String> xAxis = Lists.newArrayList();
		Map<String, String> tips = Maps.newHashMap();

		Date beforeDate = null;
		Date afterDate = null;

		if (searchFlag.equals("0") || searchFlag.equals("")) {
			searchFlag = "0";
			beforeDate = startDate;
			afterDate = endDate;
		} else if (searchFlag.equals("1")) {
			beforeDate = DateUtils.getMonday(startDate);
			afterDate = DateUtils.getSunday(endDate);
		} else if (searchFlag.equals("2")) {
			beforeDate = DateUtils.getFirstDayOfMonth(startDate);
			afterDate = DateUtils.getLastDayOfMonth(endDate);
		}

		TreeMap<Long, String> timeMap = Maps.newTreeMap();
		TreeMap<Long, String> timeMapOrder = Maps.newTreeMap();
		if (!startDate.after(endDate)) {
			DateFormat format3 = null;
			if (endDate.getYear() == startDate.getYear()) {
				format3 = FORMAT1;
			} else {
				format3 = FORMAT2;
			}
			int i = 0;

			if (searchFlag.equals("0")) {
				Date date = beforeDate;
				while (!date.after(afterDate)) {
					i++;
					xAxis.add("'" + format3.format(date) + "'");
					if (!date.after(datet)) {
						timeMap.put(date.getTime(), "0");
					}
					date = DateUtils.addDays(beforeDate, i);
				}
			} else if (searchFlag.equals("1")) {
				Date date = afterDate;
				Date datetSunday = DateUtils.getSunday(datet);
				while (date.after(beforeDate)) {
					i++;
					if (!date.after(datetSunday)) {
						timeMap.put(date.getTime(), "0");
					}
					timeMapOrder.put(date.getTime(), "0");
					date = DateUtils.addDays(afterDate, -7 * i);
				}
				// 迭代timeMap 排序时间
				for (Iterator<Entry<Long, String>> iterator = timeMapOrder.entrySet().iterator(); iterator.hasNext();) {
					Entry<Long, String> entry = iterator.next();
					long lg = entry.getKey();
					Date datelg = new Date(lg);
					String weekNum = formatWeek.format(DateUtils.addDays(
							datelg, 1));
					if (datelg.getMonth() == 11
							&& "01".equals(weekNum.split("-")[1])) {
						Integer year = datelg.getYear() + 1901;
						weekNum = year + "-" + weekNum.split("-")[1];
					}
					xAxis.add("'" + weekNum + "周'");
				}
				for (String key : xAxis) {
					key = key.substring(1, key.length() - 2);
					Date day = formatWeek.parse(key);
					day = DateUtils.addDays(day, 1); // 系统设计以星期一作为第一天,此处加一
					tips.put(key + "周", "(" + FORMAT1.format(DateUtils.getMonday(day))
									+ "一" + FORMAT1.format(DateUtils.getSunday(day)) + ")");
				}
			} else if (searchFlag.equals("2")) {
				Date date = beforeDate;
				Date datetFirstDay = DateUtils.addMonths(
						DateUtils.getFirstDayOfMonth(datet), 1);
				while (!date.after(afterDate)) {
					i++;
					xAxis.add("'" + formatMonth.format(date) + "月'");
					if (!date.after(datetFirstDay)) {
						timeMap.put(date.getTime(), "0");
					}
					date = DateUtils.addMonths(beforeDate, i);
				}
			}
		}
		 for (Map.Entry<String, Map<String, Object[]>> entry:  rs.entrySet()) { 
		    String country =entry.getKey();
			countrySet.add(country);
			Map<String, Object[]> data = entry.getValue();
			for (String strX : xAxis) {
				String date = strX.replace("'", "");
				if (date.equals(sdf.format(datet))
						&& "com,ca".contains(country)) {
					continue;
				}
				Map<String, Integer> map = sessionDataMap.get(date);
				Map<String, Float> map1 = conversionDataMap.get(date);
				if (map == null) {
					map = Maps.newLinkedHashMap();
					sessionDataMap.put(date, map);
					map1 = Maps.newLinkedHashMap();
					conversionDataMap.put(date, map1);
				}
				Object[] objs = data.get(date);
				if (objs == null) {
					map.put(country, 0);
					map1.put(country, 0f);
				} else {
					int session = ((BigDecimal) objs[2]).intValue();
					int order = ((BigDecimal) objs[3]).intValue();
					float conv = 0f;
					if (session > 0) {
						conv = (float) order * 100 / session;
						BigDecimal bg = new BigDecimal(conv);
						conv = bg.setScale(2, BigDecimal.ROUND_HALF_UP)
								.floatValue();
					}
					map.put(country, session);
					map1.put(country, conv);
				}
			}
		}
		Map<String, Map<String, Integer>> sessionDataMap1 = Maps.newLinkedHashMap();
		List<String> list = Lists.newArrayList(sessionDataMap.keySet());
		Collections.reverse(list);
		for (String key : list) {
			sessionDataMap1.put(key, sessionDataMap.get(key));
		}
		List<String> countryList = Lists.newArrayList(countrySet);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row1 = sheet.createRow(0);

		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// 设置Excel中的边框(表头的边框)
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);

		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);

		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);

		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		// 设置字体
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);

		// 标题行
		HSSFRow row = sheet.createRow(1);
		row.setHeight((short) 600);//
		HSSFCell cell = null;

		countryList.add(0, typeName);

		CellStyle cellStyle = wb.createCellStyle();
		cellStyle
				.setDataFormat(wb.createDataFormat().getBuiltinFormat("0.00%"));
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);

		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);

		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setRightBorderColor(HSSFColor.BLACK.index);

		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle.setTopBorderColor(HSSFColor.BLACK.index);

		CellStyle titleStyle = wb.createCellStyle();
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont titleFont = wb.createFont();
		titleFont.setFontHeightInPoints((short) 28); // 字体高度
		titleFont.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 28);
		titleStyle.setFont(font);

		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);

		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);

		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setRightBorderColor(HSSFColor.BLACK.index);

		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);

		// sessionDataMap1 conversionDataMap
		if ("1".equals(active)) {
			for (int i = 0; i < countryList.size() * 2 - 1; i++) {
				cell = row1.createCell(i);
				cell.setCellStyle(titleStyle);
			}
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, countryList.size() * 2 - 2));
			row1.getCell(0).setCellValue(
					StringUtils.isBlank(typeName) ? ("unGrouped".equals(groupName) ? "unGrouped"
							: psiTypeGroupService.get(groupName).getName())
							: typeName + " 分栏统计(" + date1 + type + "~" + date2 + type + ")");

			int num = 1;
			for (int i = 0; i < countryList.size() * 2 - 1; i++) {
				cell = row.createCell(i);
				if (i == 0) {
					cell.setCellValue(StringUtils.isBlank(typeName) ? ("unGrouped".equals(groupName) ? "unGrouped"
							: psiTypeGroupService.get(groupName).getName()) : typeName);
				} else if (i % 2 != 0) {
					if (i == 1) {
						cell.setCellValue(DictUtils.getDictLabel(countryList.get(i), "platform", ""));
					} else {
						cell.setCellValue(DictUtils.getDictLabel(countryList.get(i - num), "platform", ""));
						num += 1;
					}
				}
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}

			for (int i = 1; i < countryList.size() * 2 - 1; i = i + 2) {
				sheet.addMergedRegion(new CellRangeAddress(1, 1, i, i + 1));
			}

			for (int i = 2; i <= list.size() + 1; i++) {
				row = sheet.createRow(i);
				row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(list.get(i - 2)
								+ (tips.get(list.get(i - 2)) == null ? "" : tips.get(list.get(i - 2))));
				row.getCell(0).setCellStyle(contentStyle);
				int num1 = 1;
				for (int j = 1; j < countryList.size() * 2 - 1; j = j + 2) {
					Integer session = 0;
					Float conver = 0f;
					if (j == 1) {
						session = sessionDataMap1.get(list.get(i - 2)).get(countryList.get(j));
						conver = conversionDataMap.get(list.get(i - 2)).get(countryList.get(j));
					} else {
						session = sessionDataMap1.get(list.get(i - 2)).get(countryList.get(j - num1));
						conver = conversionDataMap.get(list.get(i - 2)).get(countryList.get(j - num1));
						num1 += 1;
					}
					row.createCell(j, Cell.CELL_TYPE_NUMERIC).setCellValue(session == null ? 0 : session);
					row.createCell(j + 1, Cell.CELL_TYPE_NUMERIC).setCellValue(conver == null ? 0 : conver / 100);
					row.getCell(j + 1).setCellStyle(cellStyle);
					row.getCell(j).setCellStyle(contentStyle);
				}
			}

			// total
			row = sheet.createRow(list.size() + 2);
			for (int i = 0; i < countryList.size() * 2 - 1; i++) {
				cell = row.createCell(i);
				row.getCell(i).setCellStyle(contentStyle);
				if (i == 0) {
					cell.setCellValue("total");
				} else if (i % 2 != 0) {
					int totalSession = 0;
					for (int j = 2; j <= list.size() + 1; j++) {
						totalSession = totalSession + (int) (sheet.getRow(j).getCell(i).getNumericCellValue());
					}
					cell.setCellValue(totalSession);
				}
			}
		} else if ("2".equals(active)) {
			for (int i = 0; i < countryList.size(); i++) {
				cell = row1.createCell(i);
				cell.setCellStyle(titleStyle);
			}
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, countryList.size() - 1));
			row1.getCell(0).setCellValue(typeName + " session统计(" + date1 + type + "~" + date2 + type + ")");

			for (int i = 0; i < countryList.size(); i++) {
				cell = row.createCell(i);
				if (i == 0) {
					cell.setCellValue(typeName);
				} else {
					cell.setCellValue(DictUtils.getDictLabel(countryList.get(i), "platform", ""));
				}
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
			for (int i = 2; i <= list.size() + 1; i++) {
				row = sheet.createRow(i);
				row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(list.get(i - 2)
								+ (tips.get(list.get(i - 2)) == null ? "" : tips.get(list.get(i - 2))));
				row.getCell(0).setCellStyle(contentStyle);
				for (int j = 1; j < countryList.size(); j++) {
					Integer session = sessionDataMap1.get(list.get(i - 2)).get(countryList.get(j));
					row.createCell(j, Cell.CELL_TYPE_NUMERIC).setCellValue(session == null ? 0 : session);
					row.getCell(j).setCellStyle(contentStyle);
				}
			}
			row = sheet.createRow(list.size() + 2);
			for (int i = 0; i < countryList.size(); i++) {
				cell = row.createCell(i);
				row.getCell(i).setCellStyle(contentStyle);
				if (i == 0) {
					cell.setCellValue("total");
				} else {
					int totalSession = 0;
					for (int j = 2; j <= list.size() + 1; j++) {
						totalSession = totalSession
								+ (int) (sheet.getRow(j).getCell(i).getNumericCellValue());
					}
					cell.setCellValue(totalSession);
				}
			}
		} else {
			for (int i = 0; i < countryList.size(); i++) {
				cell = row1.createCell(i);
				cell.setCellStyle(titleStyle);
			}
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, countryList.size() - 1));
			row1.getCell(0).setCellValue(
					typeName + " conversion统计(" + date1 + type + "~" + date2 + type + ")");

			for (int i = 0; i < countryList.size(); i++) {
				cell = row.createCell(i);
				if (i == 0) {
					cell.setCellValue(typeName);
				} else {
					cell.setCellValue(DictUtils.getDictLabel(countryList.get(i), "platform", ""));
				}
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
			for (int i = 2; i <= list.size() + 1; i++) {
				row = sheet.createRow(i);
				row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(list.get(i - 2)
								+ (tips.get(list.get(i - 2)) == null ? "" : tips.get(list.get(i - 2))));
				row.getCell(0).setCellStyle(cellStyle);
				for (int j = 1; j < countryList.size(); j++) {
					Float conver = conversionDataMap.get(list.get(i - 2)).get(countryList.get(j));
					row.createCell(j, Cell.CELL_TYPE_NUMERIC).setCellValue(conver == null ? 0 : conver / 100);
					row.getCell(j).setCellStyle(cellStyle);
				}
			}
		}
		sheet.autoSizeColumn((short) 0);
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");
			String fileName = "countByTypeExport" + sdf1.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequiresPermissions("amazoninfo:businessReport:view")
	@RequestMapping(value = {"countByProducts"})
	public String countByProducts(@RequestParam(required=false,value="productsName")String[] productsName,String date1,String date2,
			String searchFlag,String country,String active, Model model) throws ParseException{
		
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
		Date today = new Date();
		
		if(StringUtils.isEmpty(active)){
			active = "1";
		}
		if(StringUtils.isEmpty(searchFlag)){
			searchFlag = "0";
		}
		if(StringUtils.isEmpty(country)){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					country = dict.getValue();
					break;
				}
			}
		}
		if(StringUtils.isEmpty(country)){
			country = "de";
		}
		Date datet = null;
		if(today.getHours()>=8){
			datet = DateUtils.addDays(today, -2);
		}else{
			datet = DateUtils.addDays(today, -3);
		}
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str=sdf.format(datet);
		datet=sdf.parse(str);
		
		Date endDate =null;
		Date startDate=null;

		//分类型查询
		String type = "日"; 
		if("1".equals(searchFlag)){
			//按周查询
			if(StringUtils.isBlank(date1)){
				date1 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -20), formatWeek, 5, "-");
				Calendar cal = Calendar.getInstance();
				cal.setTime(today);
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				if (dayOfWeek > 3) {
					date2 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -1), formatWeek, 5, "-");
				} else {
					date2 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -2), formatWeek, 5, "-");
				}
			} else {
				if ("2015-53".equals(date1)) {
					date1 = "2016-01";
				}
				if ("2015-53".equals(date2)) {
					date2 = "2016-01";
				}
			}
			startDate = formatWeek.parse(date1);
			endDate = formatWeek.parse(date2);
			type = "周";
			sdf = formatWeek;
		}else if("2".equals(searchFlag)){
			//按月查询
			if(StringUtils.isBlank(date1)){
				date2 = formatMonth.format(DateUtils.addMonths(today, -1));
				date1 = formatMonth.format(DateUtils.addMonths(today, -12));
			}
			startDate = formatMonth.parse(date1);
			endDate = formatMonth.parse(date2);
			type = "月";
			sdf = formatMonth;
		}else{
			if(StringUtils.isBlank(date1)){
				date2 = formatDay.format(datet);
				date1 = formatDay.format(DateUtils.addDays(datet, -20));
			}
			startDate = formatDay.parse(date1);
			endDate = formatDay.parse(date2);
		}
		Map<String,List<String>> asinMap = amazonProductService.getProductsAsinMap(country);
		List<String> xAxis = Lists.newArrayList();
		List<String> tempAxis = Lists.newArrayList();
		if(productsName!=null){
			  
			//---图表
	        Date beforeDate=null;
			Date afterDate=null;
	        
			if (searchFlag.equals("0") || searchFlag.equals("")) {
				searchFlag = "0";
				beforeDate = startDate;
				afterDate = endDate;
			} else if (searchFlag.equals("1")) {
				beforeDate = DateUtils.getSunday(startDate);
				afterDate = DateUtils.getSunday(endDate);
			} else if (searchFlag.equals("2")) {
				beforeDate = DateUtils.getFirstDayOfMonth(startDate);
				afterDate = DateUtils.getLastDayOfMonth(endDate);
			}

			Map<Long, String> timeMap = Maps.newLinkedHashMap();
			TreeMap<Long, String> timeMapOrder = Maps.newTreeMap();
			if(!startDate.after(endDate)){
				DateFormat format3 = null;
				if(endDate.getYear()==startDate.getYear()){
					format3 = FORMAT1;
				}else{
					format3 = FORMAT2;
				}	
				int i = 0 ;
				
				if(searchFlag.equals("0")){
					Date date = beforeDate;
					while(!date.after(afterDate)){
						i++;
						xAxis.add("'"+format3.format(date)+"'");
						tempAxis.add(formatDay.format(date));
						if(!date.after(datet)){
							timeMap.put(date.getTime(), "0");
						}
						date = DateUtils.addDays(beforeDate, i);
					}
				}else if (searchFlag.equals("1")){
					Date date = beforeDate;
					Date datetSunday=DateUtils.getSunday(datet);
					while(!date.after(afterDate)){
						i++;
						if(!date.after(datetSunday)){
							timeMap.put(date.getTime(), "0");
						}
						timeMapOrder.put(date.getTime(), "0");
						tempAxis.add(formatWeek.format(date));
						date = DateUtils.addDays(beforeDate, 7*i);
					}
					//迭代timeMap 排序时间
					for (Iterator<Entry<Long, String>> iterator = timeMapOrder.entrySet().iterator(); iterator.hasNext();) {
						Entry<Long, String> entry = iterator.next();
					    long lg=entry.getKey();
						Date datelg = new Date(lg);
						xAxis.add("'"+DateUtils.getWeekStr(DateUtils.addDays(datelg, 1), formatWeek, 5, "-")+"周'");
					}
				}else if (searchFlag.equals("2")){
					Date date = beforeDate;
					Date datetFirstDay= DateUtils.addMonths(DateUtils.getFirstDayOfMonth(datet),1);
					SimpleDateFormat format = new SimpleDateFormat("yyyy/M");
					while(!date.after(afterDate)){
						i++;
						xAxis.add("'"+format.format(date)+"月'");
						tempAxis.add(formatMonth.format(date));
						if(!date.after(datetFirstDay)){
							timeMap.put(date.getTime(), "0");
						}
						date = DateUtils.addMonths(beforeDate,i);
					}
				}
			}
			List<String> asins = Lists.newArrayList();
			for (String name : productsName) {
				List<String> list =  asinMap.get(name);
				if(list!=null){
					asins.addAll(list);
				}
			}
			
			//添加产品价格
			Map<String,Map<String,Float>> priceMap =hisPriceService.getMulProductPrice(productsName, date1, date2, country, searchFlag);
			
			
			if(asins!=null&&asins.size()>0){
				Map<String,Map<String,BusinessReport>> rs = businessReportService.findCountProductsData(asins,country,date1,date2,searchFlag);
				model.addAttribute("adsData", businessReportService.getAdsQuantityByCountry(searchFlag, date1, date2,country,Sets.newHashSet(productsName)));
				//组合数据详情 [天 产品/数据]
				Map<String,Map<String,Integer>> sessionDataMap=Maps.newLinkedHashMap();
				Map<String,Map<String,Float>> conversionDataMap=Maps.newLinkedHashMap();
				Set<String> withDataProducts=Sets.newHashSet();
				//图表数据 产品名 每天的值
				Map<String,List<String>> sessionsData = Maps.newHashMap();
				Map<String,List<String>> conversionData = Maps.newHashMap();
				Map<String,List<String>> priceData = Maps.newHashMap();
				for (String name : productsName) {
					List<String> asinss = asinMap.get(name);
					List<String> temp = Lists.newArrayList();
					List<String> temp1 = Lists.newArrayList();
					List<String> tempPrice = Lists.newArrayList();
					Map<String,Float> datePriceMap = priceMap.get(name);
					for (Iterator<Entry<Long, String>> iterator = timeMap.entrySet().iterator(); iterator.hasNext();) {
						Entry<Long, String> entry = iterator.next();
						long time = entry.getKey();
						String date = DateUtils.getDate(new Date(time), "yyyy-MM-dd");
						if ("1".equals(searchFlag)) {
							Date datelg = new Date(time);
							date = formatWeek.format(DateUtils.addDays(datelg, 1));
							if (datelg.getMonth() == 11 && "01".equals(date.split("-")[1])) {
								Integer year = datelg.getYear() + 1901;
								date = year + "-" + date.split("-")[1];
							}
						} else if("2".equals(searchFlag)){
							date = DateUtils.getDate(new Date(time), "yyyy-MM");
						}
						int session = 0;
						float conv = 0f;
						float price =0f;
						if(rs.get(date)!=null){
							int order = 0;
							for (String asin : asinss) {
								if(rs.get(date).get(asin)!=null){
									if(rs.get(date).get(asin).getSessions()!=null){
										session = session+rs.get(date).get(asin).getSessions();
									}
									if(rs.get(date).get(asin).getOrdersPlaced()!=null){
										order = order +rs.get(date).get(asin).getOrdersPlaced();
									}
								}
							}
							if(session>0){
								conv = (float)order*100/session;
								BigDecimal bg = new BigDecimal(conv);
								conv = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
							}
						}
						if(datePriceMap!=null&&datePriceMap.get(date)!=null){
							price=datePriceMap.get(date);
						}
						temp.add(session+"");
						temp1.add(""+conv);
						tempPrice.add(price+"");
						Map<String,Integer> tempp = sessionDataMap.get(date);
						if(tempp==null){
							tempp = Maps.newHashMap();
							sessionDataMap.put(date, tempp);
						}
						Map<String,Float> tempp1 = conversionDataMap.get(date);
						if(tempp1==null){
							tempp1 = Maps.newHashMap();
							conversionDataMap.put(date, tempp1);
						}
						tempp.put(name, session);
						tempp1.put(name, conv);
					}
					sessionsData.put(name, temp);
					conversionData.put(name, temp1);
					priceData.put(name, tempPrice);
				}
				
				Map<String,Map<String,Integer>> sessionDataMap1=Maps.newLinkedHashMap();
				List<String> list = Lists.newArrayList(sessionDataMap.keySet());
				Collections.reverse(list);
				Map<String, String> tips = Maps.newHashMap();
				for (String key : list) {
					sessionDataMap1.put(key, sessionDataMap.get(key));
					if(searchFlag.equals("1")){
						Date date = sdf.parse(key);
						date = DateUtils.addDays(date, 1);	//系统设计以星期一作为第一天,此处加一
						tips.put(key, "("+FORMAT1.format(DateUtils.getMonday(date))+"一"+FORMAT1.format(DateUtils.getSunday(date))+")");
					}
				}
				model.addAttribute("tips", tips);
				model.addAttribute("sessionDataMap", sessionDataMap1);
				
				model.addAttribute("conversionDataMap", conversionDataMap);
				model.addAttribute("sessionsData", sessionsData);
				model.addAttribute("priceData", priceData);
				if(sessionsData.size()>0){
				    Map<String,Map<String,String>> sessionsMap=Maps.newLinkedHashMap();
				    for (Map.Entry<String, List<String>> entry: sessionsData.entrySet()) { 
					        String tempName=entry.getKey();
						    int i=0;
							String key=tempName;
							Map<String,String> temp=sessionsMap.get(key);
							if(temp==null){
								temp=Maps.newLinkedHashMap();
								sessionsMap.put(key, temp);
							}
							for (String sessionData: entry.getValue()) {
								temp.put(tempAxis.get(i), sessionData);
								i++;
							}
						
					}
					model.addAttribute("sessionsMap", sessionsMap);
				}
				model.addAttribute("conversionData", conversionData);
			  }
		}
			
		model.addAttribute("xAxis", xAxis.toString());
		if(productsName!=null){
			Set<String> set = asinMap.keySet();
			set.removeAll(Lists.newArrayList(productsName));
			model.addAttribute("productNames", set);
			
			String show =  "";
			StringBuilder buf=new StringBuilder();
			if(productsName.length>2){
				int i = 0;
				buf.append("selected: {");
				for (String product : productsName) {
					if (i < 2) {
						buf.append("'"+product+"':true,");
					} else {
						buf.append("'"+product+"':false,");
					}
					i++;
				}
				show = buf.toString().substring(0, buf.toString().length()-1);
				show += "},";
			}
			model.addAttribute("show", show);
		}else{
			model.addAttribute("productNames", asinMap.keySet());
		}
		if(productsName!=null){
			model.addAttribute("productsName", Lists.newArrayList(productsName));
		}
		model.addAttribute("type",type);
		model.addAttribute("searchFlag",searchFlag);
		model.addAttribute("date1",date1);
		model.addAttribute("date2",date2);
		model.addAttribute("active", active);
		model.addAttribute("country", country);
		return "modules/amazoninfo/reportProductsCountView";
	}
	
	
	@RequestMapping(value = {"countByProductsExport"})
	public void countByProductsExport(@RequestParam(required=false,value="productsName")String[] productsName,String date1,String date2,
			String searchFlag,String country,String active, Model model, HttpServletRequest request,HttpServletResponse response) throws ParseException{

		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
		Date today = new Date();
		
		if(StringUtils.isEmpty(active)){
			active = "1";
		}
		if(StringUtils.isEmpty(searchFlag)){
			searchFlag = "0";
		}
		if(StringUtils.isEmpty(country)){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					country = dict.getValue();
					break;
				}
			}
		}
		if(StringUtils.isEmpty(country)){
			country = "de";
		}
		Date datet = null;
		if(today.getHours()>=8){
			datet = DateUtils.addDays(today, -2);
		}else{
			datet = DateUtils.addDays(today, -3);
		}
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str=sdf.format(datet);
		datet=sdf.parse(str);
		
		Date endDate =null;
		Date startDate=null;

		//分类型查询
		String type = "日"; 
		if("1".equals(searchFlag)){
			//按周查询
			if(StringUtils.isBlank(date1)){
				date1 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -20), formatWeek, 5, "-");
				Calendar cal = Calendar.getInstance();
				cal.setTime(today);
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				if (dayOfWeek > 3) {
					date2 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -1), formatWeek, 5, "-");
				} else {
					date2 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -2), formatWeek, 5, "-");
				}
			} else {
				if ("2015-53".equals(date1)) {
					date1 = "2016-01";
				}
				if ("2015-53".equals(date2)) {
					date2 = "2016-01";
				}
			}
			startDate = formatWeek.parse(date1);
			endDate = formatWeek.parse(date2);
			type = "周";
			sdf = formatWeek;
		}else if("2".equals(searchFlag)){
			//按月查询
			if(StringUtils.isBlank(date1)){
				date2 = formatMonth.format(DateUtils.addMonths(today, -1));
				date1 = formatMonth.format(DateUtils.addMonths(today, -12));
			}
			startDate = formatMonth.parse(date1);
			endDate = formatMonth.parse(date2);
			type = "月";
			sdf = formatMonth;
		}else{
			if(StringUtils.isBlank(date1)){
				date2 = formatDay.format(datet);
				date1 = formatDay.format(DateUtils.addDays(datet, -20));
			}
			startDate = formatDay.parse(date1);
			endDate = formatDay.parse(date2);
		}
		Map<String,List<String>> asinMap = amazonProductService.getProductsAsinMap(country);
		List<String> xAxis = Lists.newArrayList();
		List<String> tempAxis = Lists.newArrayList();
		if(productsName!=null){
			for (int m = 0; m < productsName.length; m++) {
				try {
					productsName[m] = URLDecoder.decode(productsName[m],
							"utf-8");
				} catch (UnsupportedEncodingException e) {
				}

			}

			//---图表
	        Date beforeDate=null;
			Date afterDate=null;
	        
			if (searchFlag.equals("0") || searchFlag.equals("")) {
				searchFlag = "0";
				beforeDate = startDate;
				afterDate = endDate;
			} else if (searchFlag.equals("1")) {
				beforeDate = DateUtils.getSunday(startDate);
				afterDate = DateUtils.getSunday(endDate);
			} else if (searchFlag.equals("2")) {
				beforeDate = DateUtils.getFirstDayOfMonth(startDate);
				afterDate = DateUtils.getLastDayOfMonth(endDate);
			}

			Map<Long, String> timeMap = Maps.newLinkedHashMap();
			TreeMap<Long, String> timeMapOrder = Maps.newTreeMap();
			if(!startDate.after(endDate)){
				DateFormat format3 = null;
				if(endDate.getYear()==startDate.getYear()){
					format3 = FORMAT1;
				}else{
					format3 = FORMAT2;
				}	
				int i = 0 ;
				
				if(searchFlag.equals("0")){
					Date date = beforeDate;
					while(!date.after(afterDate)){
						i++;
						xAxis.add("'"+format3.format(date)+"'");
						tempAxis.add(formatDay.format(date));
						if(!date.after(datet)){
							timeMap.put(date.getTime(), "0");
						}
						date = DateUtils.addDays(beforeDate, i);
					}
				}else if (searchFlag.equals("1")){
					Date date = beforeDate;
					Date datetSunday=DateUtils.getSunday(datet);
					while(!date.after(afterDate)){
						i++;
						if(!date.after(datetSunday)){
							timeMap.put(date.getTime(), "0");
						}
						timeMapOrder.put(date.getTime(), "0");
						tempAxis.add(formatWeek.format(date));
						date = DateUtils.addDays(beforeDate, 7*i);
					}
					//迭代timeMap 排序时间
					for (Iterator<Entry<Long, String>> iterator = timeMapOrder.entrySet().iterator(); iterator.hasNext();) {
						Entry<Long, String> entry = iterator.next();
					    long lg=entry.getKey();
						Date datelg = new Date(lg);
						xAxis.add("'"+DateUtils.getWeekStr(DateUtils.addDays(datelg, 1), formatWeek, 5, "-")+"周'");
					}
				}else if (searchFlag.equals("2")){
					Date date = beforeDate;
					Date datetFirstDay= DateUtils.addMonths(DateUtils.getFirstDayOfMonth(datet),1);
					SimpleDateFormat format = new SimpleDateFormat("yyyy/M");
					while(!date.after(afterDate)){
						i++;
						xAxis.add("'"+format.format(date)+"月'");
						tempAxis.add(formatMonth.format(date));
						if(!date.after(datetFirstDay)){
							timeMap.put(date.getTime(), "0");
						}
						date = DateUtils.addMonths(beforeDate,i);
					}
				}
			}
			List<String> asins = Lists.newArrayList();
			for (String name : productsName) {
				List<String> list =  asinMap.get(name);
				if(list!=null){
					asins.addAll(list);
				}
			}
			
			//添加产品价格
			Map<String,Map<String,Float>> priceMap =hisPriceService.getMulProductPrice(productsName, date1, date2, country, searchFlag);
			
			
			if(asins!=null&&asins.size()>0){
				Map<String,Map<String,BusinessReport>> rs = businessReportService.findCountProductsData(asins,country,date1,date2,searchFlag);
				Map<String,Map<String,Integer>> adsData=businessReportService.getAdsQuantityByCountry(searchFlag, date1, date2,country,Sets.newHashSet(productsName));
				//组合数据详情 [天 产品/数据]
				Map<String,Map<String,Integer>> sessionDataMap=Maps.newLinkedHashMap();
				Map<String,Map<String,Float>> conversionDataMap=Maps.newLinkedHashMap();
				Set<String> withDataProducts=Sets.newHashSet();
				//图表数据 产品名 每天的值
				Map<String,List<String>> sessionsData = Maps.newHashMap();
				Map<String,List<String>> conversionData = Maps.newHashMap();
				Map<String,List<String>> priceData = Maps.newHashMap();
				for (String name : productsName) {
					List<String> asinss = asinMap.get(name);
					List<String> temp = Lists.newArrayList();
					List<String> temp1 = Lists.newArrayList();
					List<String> tempPrice = Lists.newArrayList();
					Map<String,Float> datePriceMap = priceMap.get(name);
					for (Iterator<Entry<Long, String>> iterator = timeMap.entrySet().iterator(); iterator.hasNext();) {
						Entry<Long, String> entry = iterator.next();
						long time = entry.getKey();
						String date = DateUtils.getDate(new Date(time), "yyyy-MM-dd");
						if ("1".equals(searchFlag)) {
							Date datelg = new Date(time);
							date = formatWeek.format(DateUtils.addDays(datelg, 1));
							if (datelg.getMonth() == 11 && "01".equals(date.split("-")[1])) {
								Integer year = datelg.getYear() + 1901;
								date = year + "-" + date.split("-")[1];
							}
						} else if("2".equals(searchFlag)){
							date = DateUtils.getDate(new Date(time), "yyyy-MM");
						}
						int session = 0;
						float conv = 0f;
						float price =0f;
						if(rs.get(date)!=null){
							int order = 0;
							for (String asin : asinss) {
								if(rs.get(date).get(asin)!=null){
									if(rs.get(date).get(asin).getSessions()!=null){
										session = session+rs.get(date).get(asin).getSessions();
									}
									if(rs.get(date).get(asin).getOrdersPlaced()!=null){
										order = order +rs.get(date).get(asin).getOrdersPlaced();
									}
								}
							}
							if(session>0){
								conv = (float)order*100/session;
								BigDecimal bg = new BigDecimal(conv);
								conv = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
							}
						}
						if(datePriceMap!=null&&datePriceMap.get(date)!=null){
							price=datePriceMap.get(date);
						}
						temp.add(session+"");
						temp1.add(""+conv);
						tempPrice.add(price+"");
						Map<String,Integer> tempp = sessionDataMap.get(date);
						if(tempp==null){
							tempp = Maps.newHashMap();
							sessionDataMap.put(date, tempp);
						}
						Map<String,Float> tempp1 = conversionDataMap.get(date);
						if(tempp1==null){
							tempp1 = Maps.newHashMap();
							conversionDataMap.put(date, tempp1);
						}
						tempp.put(name, session);
						tempp1.put(name, conv);
					}
					sessionsData.put(name, temp);
					conversionData.put(name, temp1);
					priceData.put(name, tempPrice);
				}
				
				Map<String,Map<String,Integer>> sessionDataMap1=Maps.newLinkedHashMap();
				List<String> list = Lists.newArrayList(sessionDataMap.keySet());
				Collections.reverse(list);
				Map<String, String> tips = Maps.newHashMap();
				for (String key : list) {
					sessionDataMap1.put(key, sessionDataMap.get(key));
					if(searchFlag.equals("1")){
						Date date = sdf.parse(key);
						date = DateUtils.addDays(date, 1);	//系统设计以星期一作为第一天,此处加一
						tips.put(key, "("+FORMAT1.format(DateUtils.getMonday(date))+"一"+FORMAT1.format(DateUtils.getSunday(date))+")");
					}
				}
				if(sessionsData.size()>0){
				    Map<String,Map<String,String>> sessionsMap=Maps.newLinkedHashMap();
				    for (Map.Entry<String, List<String>> entry: sessionsData.entrySet()) { 
					        String tempName=entry.getKey();
						    int i=0;
							String key=tempName;
							Map<String,String> temp=sessionsMap.get(key);
							if(temp==null){
								temp=Maps.newLinkedHashMap();
								sessionsMap.put(key, temp);
							}
							for (String sessionData: entry.getValue()) {
								temp.put(tempAxis.get(i), sessionData);
								i++;
							}
						
					}
				}
				
				List<String> productNameList = Lists.newArrayList(productsName);

				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet sheet = wb.createSheet();
				HSSFRow row1 = sheet.createRow(0);

				HSSFCellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

				style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
				style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
				style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				// 设置Excel中的边框(表头的边框)
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

				style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
				style.setBottomBorderColor(HSSFColor.BLACK.index);

				style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
				style.setLeftBorderColor(HSSFColor.BLACK.index);

				style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
				style.setRightBorderColor(HSSFColor.BLACK.index);

				style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
				style.setTopBorderColor(HSSFColor.BLACK.index);
				// 设置字体
				HSSFFont font = wb.createFont();
				font.setFontHeightInPoints((short) 16); // 字体高度
				font.setFontName(" 黑体 "); // 字体
				font.setBoldweight((short) 16);
				style.setFont(font);

				// 标题行
				HSSFRow row = sheet.createRow(1);
				row.setHeight((short) 600);//
				HSSFCell cell = null;
				


				CellStyle cellStyle = wb.createCellStyle();
				//cellStyle.setDataFormat(wb.createDataFormat().getBuiltinFormat("0.00%"));
				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
				cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);

				cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);

				cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				style.setRightBorderColor(HSSFColor.BLACK.index);

				cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
				cellStyle.setTopBorderColor(HSSFColor.BLACK.index);

				CellStyle titleStyle = wb.createCellStyle();
				titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				HSSFFont titleFont = wb.createFont();
				titleFont.setFontHeightInPoints((short) 28); // 字体高度
				titleFont.setFontName(" 黑体 "); // 字体
				font.setBoldweight((short) 28);
				titleStyle.setFont(font);

				CellStyle contentStyle = wb.createCellStyle();
				contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);

				contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);

				contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
				style.setRightBorderColor(HSSFColor.BLACK.index);

				contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
				contentStyle.setTopBorderColor(HSSFColor.BLACK.index);

				// sessionDataMap1 conversionDataMap
				if ("1".equals(active)) {
					List<String> title1=Lists.newArrayList("日期");
					List<String> title2=Lists.newArrayList("日期");
					for (int i = 0; i < productNameList.size(); i++) {
						cell = row1.createCell(i);
						cell.setCellStyle(titleStyle);
						title1.add(productNameList.get(i));
						title1.add(productNameList.get(i));
						title1.add(productNameList.get(i));
						title2.add("Session");
						title2.add("Session(ad)");
						title2.add("Conversion(%)");
					}
					
					for (int i = 0; i < title1.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(title1.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
					}
					
					row = sheet.createRow(2);
					for (int i = 0; i < title2.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(title2.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
					}
					
					int rowIndex=3;
					for (Map.Entry<String, Map<String, Integer>> entry: sessionDataMap.entrySet()) { 
					    String date=entry.getKey();
					    Map<String, Integer>  sessionData=entry.getValue();
						row = sheet.createRow(rowIndex++);
						int j=0;
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(date);
						for (String name:productNameList) {
							if(sessionData!=null&&sessionData.get(name)!=null){
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sessionData.get(name));
							}else{
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							if(adsData!=null&&adsData.get(name)!=null&&adsData.get(name).get(date)!=null){
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(adsData.get(name).get(date));
							}else{
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							}
							if(conversionDataMap.get(date)!=null&&conversionDataMap.get(date).get(name)!=null){
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(conversionDataMap.get(date).get(name));
								row.getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							}
						
						}
					}

				} else if ("2".equals(active)) {
					List<String> title1=Lists.newArrayList("日期");
					List<String> title2=Lists.newArrayList("日期");
					for (int i = 0; i < productNameList.size(); i++) {
						cell = row1.createCell(i);
						cell.setCellStyle(titleStyle);
						title1.add(productNameList.get(i));
						title1.add(productNameList.get(i));
						title2.add("Session");
						title2.add("Session(ad)");
					}
					
					for (int i = 0; i < title1.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(title1.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
					}
					
					row = sheet.createRow(2);
					for (int i = 0; i < title2.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(title2.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
					}
					
					int rowIndex=3;
					for (Map.Entry<String, Map<String, Integer>> entry: sessionDataMap.entrySet()) { 
					    String date=entry.getKey();
					    Map<String, Integer>  sessionData=entry.getValue();
					
						row = sheet.createRow(rowIndex++);
						
						int j=0;
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(date);
						for (String name:productNameList) {
							if(sessionData!=null&&sessionData.get(name)!=null){
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sessionData.get(name));
							}else{
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							if(adsData!=null&&adsData.get(name)!=null&&adsData.get(name).get(date)!=null){
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(adsData.get(name).get(date));
								
							}else{
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
						}
					}

				} else {
					List<String> title1=Lists.newArrayList("日期");
					List<String> title2=Lists.newArrayList("日期");
					for (int i = 0; i < productNameList.size(); i++) {
						cell = row1.createCell(i);
						cell.setCellStyle(titleStyle);
						title1.add(productNameList.get(i));
						title2.add("Conversion(%)");
					}
					
					for (int i = 0; i < title1.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(title1.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
					}
					
					row = sheet.createRow(2);
					for (int i = 0; i < title2.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(title2.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
					}
					
					int rowIndex=3;
					for (Map.Entry<String, Map<String, Integer>> entry: sessionDataMap.entrySet()) { 
					    String date=entry.getKey();
						row = sheet.createRow(rowIndex++);
						int j=0;
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(date);
						for (String name:productNameList) {
							if(conversionDataMap.get(date)!=null&&conversionDataMap.get(date).get(name)!=null){
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(conversionDataMap.get(date).get(name));
								row.getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							}
						}
					}
				}
				try {
					request.setCharacterEncoding("UTF-8");
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/x-download");

					SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");

					String fileName = "countByProductsExport" + sdf1.format(new Date()) + ".xls";
					fileName = URLEncoder.encode(fileName, "UTF-8");
					response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
					OutputStream out = response.getOutputStream();
					wb.write(out);
					out.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
			
	}
	
	@RequiresPermissions("amazoninfo:businessReport:view")
	@RequestMapping(value = {"countByProductType"})
	public String countByProductType(@RequestParam(required=false,value="types")String[] types,String date1,String date2,
			String searchFlag,String country,String active,String groupName, Model model) throws ParseException{
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
		
		if(StringUtils.isEmpty(active)){
			active = "1";
		}
		if(StringUtils.isEmpty(searchFlag)){
			searchFlag = "0";
		}
		if(StringUtils.isEmpty(country)){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					country = dict.getValue();
					break;
				}
			}
		}
		if(StringUtils.isEmpty(country)){
			country = "de";
		}
		Date today = new Date();
		Date datet = null;
		if(today.getHours()>=8){
			datet = DateUtils.addDays(today, -2);
		}else{
			datet = DateUtils.addDays(today, -3);
		}
		datet = new Date(datet.getYear(), datet.getMonth(), datet.getDate());
		if("com,ca".contains(country)){
			datet = DateUtils.addDays(datet, -1);
		}
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str=sdf.format(datet);
		datet=sdf.parse(str);
		
		Date endDate =null;
		Date startDate=null;

		//分类型查询
		String type = "日"; 
		if("1".equals(searchFlag)){
			//按周查询
			if(StringUtils.isBlank(date1)){
				date1 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -20), formatWeek, 5, "-");
				Calendar cal = Calendar.getInstance();
				cal.setTime(today);
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				if (dayOfWeek > 3) {
					date2 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -1), formatWeek, 5, "-");
				} else {
					date2 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -2), formatWeek, 5, "-");
				}
			} else {
				if ("2015-53".equals(date1)) {
					date1 = "2016-01";
				}
				if ("2015-53".equals(date2)) {
					date2 = "2016-01";
				}
			}
			startDate = formatWeek.parse(date1);
			endDate = formatWeek.parse(date2);
			type = "周";
			sdf = formatWeek;
		}else if("2".equals(searchFlag)){
			//按月查询
			if(StringUtils.isBlank(date1)){
				date2 = formatMonth.format(DateUtils.addMonths(today, -1));
				date1 = formatMonth.format(DateUtils.addMonths(today, -12));
			}
			startDate = formatMonth.parse(date1);
			endDate = formatMonth.parse(date2);
			type = "月";
			sdf = formatMonth;
		}else{
			if(StringUtils.isBlank(date1)){
				date2 = formatDay.format(datet);
				date1 = formatDay.format(DateUtils.addDays(datet, -20));
			}
			startDate = formatDay.parse(date1);
			endDate = formatDay.parse(date2);
		}
		List<String> xAxis = Lists.newArrayList();
		List<String> tempAxis = Lists.newArrayList();
		if(types!=null){
			//---图表
	        Date beforeDate=null;
			Date afterDate=null;
	        
			if (searchFlag.equals("0") || searchFlag.equals("")) {
				searchFlag = "0";
				beforeDate = startDate;
				afterDate = endDate;
			} else if (searchFlag.equals("1")) {
				beforeDate = DateUtils.getSunday(startDate);
				afterDate = DateUtils.getSunday(endDate);
			} else if (searchFlag.equals("2")) {
				beforeDate = DateUtils.getFirstDayOfMonth(startDate);
				afterDate = DateUtils.getLastDayOfMonth(endDate);
			}

			Map<Long, String> timeMap = Maps.newLinkedHashMap();
			TreeMap<Long, String> timeMapOrder = Maps.newTreeMap();
			if(!startDate.after(endDate)){
				DateFormat format3 = null;
				if(endDate.getYear()==startDate.getYear()){
					format3 = FORMAT1;
				}else{
					format3 = FORMAT2;
				}	
				int i = 0 ;
				
				if(searchFlag.equals("0")){
					Date date = beforeDate;
					while(!date.after(afterDate)){
						i++;
						xAxis.add("'"+format3.format(date)+"'");
						tempAxis.add(formatDay.format(date));
						if(!date.after(datet)){
							timeMap.put(date.getTime(), "0");
						}
						date = DateUtils.addDays(beforeDate, i);
					}
				}else if (searchFlag.equals("1")){
					Date date = beforeDate;
					Date datetSunday=DateUtils.getSunday(datet);
					while(!date.after(afterDate)){
						i++;
						if(!date.after(datetSunday)){
							timeMap.put(date.getTime(), "0");
						}
						timeMapOrder.put(date.getTime(), "0");
						tempAxis.add(formatWeek.format(date));
						date = DateUtils.addDays(beforeDate, 7*i);
					}
					//迭代timeMap 排序时间
					for (Iterator<Entry<Long, String>> iterator = timeMapOrder.entrySet().iterator(); iterator.hasNext();) {
						Entry<Long, String> entry = iterator.next();
					    long lg=entry.getKey();
						Date datelg = new Date(lg);
						xAxis.add("'"+DateUtils.getWeekStr(DateUtils.addDays(datelg, 1), formatWeek, 5, "-")+"周'");
					}
					Map<String, String> tips = Maps.newHashMap();
					for (String key : xAxis) {
						key = key.substring(1, key.length()-2);
						Date day = formatWeek.parse(key);
						day = DateUtils.addDays(day, 1);	//系统设计以星期一作为第一天,此处加一
						tips.put(key + "周", "("+FORMAT1.format(DateUtils.getMonday(day))+"一"+FORMAT1.format(DateUtils.getSunday(day))+")");
					}
					model.addAttribute("tips", tips);
				}else if (searchFlag.equals("2")){
					Date date = beforeDate;
					Date datetFirstDay= DateUtils.addMonths(DateUtils.getFirstDayOfMonth(datet),1);
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
					while(!date.after(afterDate)){
						i++;
						xAxis.add("'"+format.format(date)+"月'");
						tempAxis.add(formatMonth.format(date));
						if(!date.after(datetFirstDay)){
							timeMap.put(date.getTime(), "0");
						}
						date = DateUtils.addMonths(beforeDate,i);
					}
				}
			}
			Map<String,Map<String,Object[]>> rs = businessReportService.findCountTypeData(country, date1, date2,searchFlag,types);
			Map<String,Map<String,Integer>> adsDataMap=businessReportService.getAdsQuantityByCountryType(searchFlag, date1, date2,country,Sets.newHashSet(types));
			model.addAttribute("adsData",adsDataMap);
			if(adsDataMap!=null&&adsDataMap.size()>0){
				Map<String,Map<String,Integer>> adviseDataMap=Maps.newLinkedHashMap();
				 for (Map.Entry<String, Map<String, Integer>> entry: adsDataMap.entrySet()) { 
				    String adsCountry=entry.getKey();
					Map<String,Integer> tempCountry=entry.getValue();
					Map<String,Integer> tempAds=adviseDataMap.get(adsCountry);
					if(tempAds==null){
						tempAds=Maps.newLinkedHashMap();
						adviseDataMap.put(adsCountry, tempAds);
					}
					 for (Map.Entry<String, Integer> entryRs: tempCountry.entrySet()) { 
					    String dateKey =entryRs.getKey();
						String[] arr=dateKey.split("-");
						if(searchFlag.equals("0")){
							if(endDate.getYear()==startDate.getYear()){
								String arrKey=Integer.parseInt(arr[1].toString())+"/"+Integer.parseInt(arr[2].toString());
								tempAds.put(arrKey, entryRs.getValue());
							}else{
								String arrKey=arr[0]+"/"+Integer.parseInt(arr[1].toString())+"/"+Integer.parseInt(arr[2].toString());
								tempAds.put(arrKey, entryRs.getValue());
							}
							
						}else if (searchFlag.equals("1")){
							tempAds.put(dateKey+"周", entryRs.getValue());
						}else if (searchFlag.equals("2")){
							tempAds.put(dateKey+"月",entryRs.getValue());
						}
					}
				}
				model.addAttribute("adviseDataMap", adviseDataMap);
			}
			//组合数据详情 [天 类型/数据]
			Map<String,Map<String,Integer>> sessionDataMap=Maps.newLinkedHashMap();
			Map<String,Map<String,Float>> conversionDataMap=Maps.newLinkedHashMap();
			//图表数据 类型名 每天的值
			Map<String,List<String>> sessionsData = Maps.newHashMap();
			Map<String,List<String>> conversionData = Maps.newHashMap();
			 for (Map.Entry<String, Map<String, Object[]>> entry : rs.entrySet()) { 
			    String name =entry.getKey();
				Map<String,Object[]> data = entry.getValue();
				sessionsData.put(name, new ArrayList<String>());
				conversionData.put(name, new ArrayList<String>());
				for (String strX : xAxis) {
					String date =  strX.replace("'", "");
					Map<String,Integer> map = sessionDataMap.get(date);
					Map<String,Float> map1 = conversionDataMap.get(date);
					if(map==null){
						map = Maps.newLinkedHashMap();
						sessionDataMap.put(date, map);
						map1 = Maps.newLinkedHashMap();
						conversionDataMap.put(date, map1);
					}
					Object[] objs = data.get(date);
					if(objs==null){
						sessionsData.get(name).add("0");
						conversionData.get(name).add("0");
						map.put(name, 0);
						map1.put(name, 0f);
					}else{
						int session = Integer.parseInt(objs[2].toString());
						int order = Integer.parseInt(objs[3].toString());
						float conv = 0f;
						if(session>0){
							conv = (float)order*100/session;
							BigDecimal bg = new BigDecimal(conv);
							conv = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						}
						sessionsData.get(name).add(session+"");
						conversionData.get(name).add(conv+"");
						map.put(name, session);
						map1.put(name, conv);
					}
				}
			}
			Map<String,Map<String,Integer>> sessionDataMap1=Maps.newLinkedHashMap();
			List<String> list = Lists.newArrayList(sessionDataMap.keySet());
			Collections.reverse(list);
			for (String key : list) {
				sessionDataMap1.put(key, sessionDataMap.get(key));
			}
			model.addAttribute("sessionDataMap", sessionDataMap1);
			model.addAttribute("conversionDataMap", conversionDataMap);
			model.addAttribute("sessionsData", sessionsData);
			if(sessionsData.size()>0){
			    Map<String,Map<String,String>> sessionsMap=Maps.newLinkedHashMap();
			    for (Map.Entry<String, List<String>> entry: sessionsData.entrySet()) { 
				        String tempName=entry.getKey();
					    int i=0;
						Map<String,String> temp=sessionsMap.get(tempName);
						if(temp==null){
							temp=Maps.newLinkedHashMap();
							sessionsMap.put(tempName, temp);
						}
						for (String sessionData: entry.getValue()) {
							temp.put(tempAxis.get(i), sessionData);
							i++;
						}
					
				}
				model.addAttribute("sessionsMap", sessionsMap);
			}
			model.addAttribute("conversionData", conversionData);
		}
		model.addAttribute("xAxis", xAxis.toString());
		List<String> typesAll = Lists.newArrayList();
		
		Set<String> typesTemp = null;
		if(types!=null){
			typesTemp = Sets.newHashSet(types);
			model.addAttribute("types",typesTemp);
			
			String show =  "";
			StringBuilder buf=new StringBuilder();
			if(typesTemp.size()>2){
				int i = 0;
				buf.append("selected: {'total':true,");
				for (String product : typesTemp) {
					if (i < 2) {
						buf.append("'"+product+"':true,");
					} else {
						buf.append("'"+product+"':false,");
					}
					i++;
				}
				show = buf.toString().substring(0, buf.toString().length()-1);
				show += "},";
			}
			model.addAttribute("show", show);
		}
		for (Dict dict : DictUtils.getDictList("product_type")) {
			String dictType = HtmlUtils.htmlUnescape(dict.getValue());
			if(dictType==null||typesTemp==null||!typesTemp.contains(dictType)){
				typesAll.add(dictType);
			}
		}
		model.addAttribute("typesAll", typesAll);
		
		model.addAttribute("startDate",startDate);
		model.addAttribute("endDate",endDate);
		model.addAttribute("active", active);
		model.addAttribute("country", country);
		model.addAttribute("groupName", groupName);
		model.addAttribute("date1",date1);
		model.addAttribute("date2",date2);
		model.addAttribute("searchFlag",searchFlag);
		model.addAttribute("groupType", psiTypeGroupService.getAllList());
		return "modules/amazoninfo/reportProductTypeCountView";
	}
	
	@RequestMapping(value = {"countByProductTypeExport"})
	public void countByProductTypeExport(@RequestParam(required = false, value = "types") String[] types, String date1, String date2, String searchFlag, String country,
			String active, Model model, HttpServletRequest request, HttpServletResponse response) throws ParseException {
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");

		if (StringUtils.isEmpty(active)) {
			active = "1";
		}
		if (StringUtils.isEmpty(searchFlag)) {
			searchFlag = "0";
		}
		if (StringUtils.isEmpty(country)) {
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					country = dict.getValue();
					break;
				}
			}
		}
		if (StringUtils.isEmpty(country)) {
			country = "de";
		}
		Date today = new Date();
		Date datet = null;
		if (today.getHours() >= 8) {
			datet = DateUtils.addDays(today, -2);
		} else {
			datet = DateUtils.addDays(today, -3);
		}
		datet = new Date(datet.getYear(), datet.getMonth(), datet.getDate());
		if ("com,ca".contains(country)) {
			datet = DateUtils.addDays(datet, -1);
		}

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str = sdf.format(datet);
		datet = sdf.parse(str);

		Date endDate = null;
		Date startDate = null;

		// 分类型查询
		String type = "日";
		if ("1".equals(searchFlag)) {
			// 按周查询
			if (StringUtils.isBlank(date1)) {
				date1 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -20),
						formatWeek, 5, "-");
				date2 = DateUtils.getWeekStr(today, formatWeek, 5, "-");
			} else {
				if ("2015-53".equals(date1)) {
					date1 = "2016-01";
				}
				if ("2015-53".equals(date2)) {
					date2 = "2016-01";
				}
			}
			startDate = formatWeek.parse(date1);
			endDate = formatWeek.parse(date2);
			type = "周";
			sdf = formatWeek;
		} else if ("2".equals(searchFlag)) {
			// 按月查询
			if (StringUtils.isBlank(date1)) {
				date2 = formatMonth.format(today);
				date1 = formatMonth.format(DateUtils.addMonths(today, -12));
			}
			startDate = formatMonth.parse(date1);
			endDate = formatMonth.parse(date2);
			type = "月";
			sdf = formatMonth;
		} else {
			if (StringUtils.isBlank(date1)) {
				date2 = formatDay.format(datet);
				date1 = formatDay.format(DateUtils.addMonths(datet, -1));
			}
			startDate = formatDay.parse(date1);
			endDate = formatDay.parse(date2);
		}
		List<String> xAxis = Lists.newArrayList();
		Map<String, String> tips = Maps.newHashMap();
		if (types != null) {
			for (int m = 0; m < types.length; m++) {
				try {
					types[m] = URLDecoder.decode(types[m], "utf-8");
				} catch (UnsupportedEncodingException e) {
				}

			}
			Date beforeDate = null;
			Date afterDate = null;

			if (searchFlag.equals("0") || searchFlag.equals("")) {
				searchFlag = "0";
				beforeDate = startDate;
				afterDate = endDate;
			} else if (searchFlag.equals("1")) {
				beforeDate = DateUtils.getSunday(startDate);
				afterDate = DateUtils.getSunday(endDate);
			} else if (searchFlag.equals("2")) {
				beforeDate = DateUtils.getFirstDayOfMonth(startDate);
				afterDate = DateUtils.getLastDayOfMonth(endDate);
			}

			Map<Long, String> timeMap = Maps.newLinkedHashMap();
			TreeMap<Long, String> timeMapOrder = Maps.newTreeMap();
			if (!startDate.after(endDate)) {
				DateFormat format3 = null;
				if (endDate.getYear() == startDate.getYear()) {
					format3 = FORMAT1;
				} else {
					format3 = FORMAT2;
				}
				int i = 0;

				if (searchFlag.equals("0")) {
					Date date = beforeDate;
					while (!date.after(afterDate)) {
						i++;
						xAxis.add("'" + format3.format(date) + "'");
						if (!date.after(datet)) {
							timeMap.put(date.getTime(), "0");
						}
						date = DateUtils.addDays(beforeDate, i);
					}
				} else if (searchFlag.equals("1")) {
					Date date = beforeDate;
					Date datetSunday = DateUtils.getSunday(datet);
					while (!date.after(afterDate)) {
						i++;
						if (!date.after(datetSunday)) {
							timeMap.put(date.getTime(), "0");
						}
						timeMapOrder.put(date.getTime(), "0");
						date = DateUtils.addDays(beforeDate, 7 * i);
					}
					// 迭代timeMap 排序时间
					for (Iterator<Entry<Long, String>> iterator = timeMapOrder
							.entrySet().iterator(); iterator.hasNext();) {
						Entry<Long, String> entry = iterator.next();
						long lg = entry.getKey();
						Date datelg = new Date(lg);
						xAxis.add("'" + DateUtils.getWeekStr(DateUtils.addDays(datelg, 1), formatWeek, 5, "-") + "周'");
					}
					for (String key : xAxis) {
						key = key.substring(1, key.length() - 2);
						Date day = formatWeek.parse(key);
						day = DateUtils.addDays(day, 1); // 系统设计以星期一作为第一天,此处加一
						tips.put(key + "周", "(" + FORMAT1.format(DateUtils.getMonday(day)) + "一" + FORMAT1.format(DateUtils.getSunday(day)) + ")");
					}
				} else if (searchFlag.equals("2")) {
					Date date = beforeDate;
					Date datetFirstDay = DateUtils.addMonths(
							DateUtils.getFirstDayOfMonth(datet), 1);
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
					while (!date.after(afterDate)) {
						i++;
						xAxis.add("'" + format.format(date) + "月'");
						if (!date.after(datetFirstDay)) {
							timeMap.put(date.getTime(), "0");
						}
						date = DateUtils.addMonths(beforeDate, i);
					}
				}
			}
			Map<String, Map<String, Object[]>> rs = businessReportService.findCountTypeData(country, date1, date2, searchFlag, types);

			// 组合数据详情 [天 类型/数据]
			Map<String, Map<String, Integer>> sessionDataMap = Maps.newLinkedHashMap();
			Map<String, Map<String, Float>> conversionDataMap = Maps.newLinkedHashMap();
			// 图表数据 类型名 每天的值

			 for (Map.Entry<String, Map<String, Object[]>>  entry : rs.entrySet()) { 
			    String name = entry.getKey();
				Map<String, Object[]> data = entry.getValue();
				for (String strX : xAxis) {
					String date = strX.replace("'", "");
					Map<String, Integer> map = sessionDataMap.get(date);
					Map<String, Float> map1 = conversionDataMap.get(date);
					if (map == null) {
						map = Maps.newLinkedHashMap();
						sessionDataMap.put(date, map);
						map1 = Maps.newLinkedHashMap();
						conversionDataMap.put(date, map1);
					}
					Object[] objs = data.get(date);
					if (objs == null) {
						map.put(name, 0);
						map1.put(name, 0f);
					} else {
						int session = Integer.parseInt(objs[2].toString());
						int order = Integer.parseInt(objs[3].toString());
						float conv = 0f;
						if (session > 0) {
							conv = (float) order * 100 / session;
							BigDecimal bg = new BigDecimal(conv);
							conv = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						}
						map.put(name, session);
						map1.put(name, conv);
					}
				}
			}
			Map<String, Map<String, Integer>> sessionDataMap1 = Maps
					.newLinkedHashMap();
			List<String> list = Lists.newArrayList(sessionDataMap.keySet());
			Collections.reverse(list);
			for (String key : list) {
				sessionDataMap1.put(key, sessionDataMap.get(key));
			}

			List<String> productTypeList = Lists.newArrayList(types);

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row1 = sheet.createRow(0);

			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// 设置Excel中的边框(表头的边框)
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

			style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			style.setBottomBorderColor(HSSFColor.BLACK.index);

			style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
			style.setLeftBorderColor(HSSFColor.BLACK.index);

			style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
			style.setRightBorderColor(HSSFColor.BLACK.index);

			style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
			style.setTopBorderColor(HSSFColor.BLACK.index);
			// 设置字体
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 16); // 字体高度
			font.setFontName(" 黑体 "); // 字体
			font.setBoldweight((short) 16);
			style.setFont(font);

			// 标题行
			HSSFRow row = sheet.createRow(1);
			row.setHeight((short) 600);//
			HSSFCell cell = null;

			productTypeList.add(0, "日期");

			CellStyle cellStyle = wb.createCellStyle();
			cellStyle.setDataFormat(wb.createDataFormat().getBuiltinFormat("0.00%"));
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);

			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);

			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setRightBorderColor(HSSFColor.BLACK.index);

			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cellStyle.setTopBorderColor(HSSFColor.BLACK.index);

			CellStyle titleStyle = wb.createCellStyle();
			titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFFont titleFont = wb.createFont();
			titleFont.setFontHeightInPoints((short) 28); // 字体高度
			titleFont.setFontName(" 黑体 "); // 字体
			font.setBoldweight((short) 28);
			titleStyle.setFont(font);

			CellStyle contentStyle = wb.createCellStyle();
			contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);

			contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);

			contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setRightBorderColor(HSSFColor.BLACK.index);

			contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			contentStyle.setTopBorderColor(HSSFColor.BLACK.index);

			// sessionDataMap1 conversionDataMap
			if ("1".equals(active)) {
				for (int i = 0; i < productTypeList.size() * 2 - 1; i++) {
					cell = row1.createCell(i);
					cell.setCellStyle(titleStyle);
				}
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0,
						productTypeList.size() * 2 - 2));
				row1.getCell(0).setCellValue(DictUtils.getDictLabel(country, "platform", "")
								+ " 分栏统计(" + date1 + type + "~" + date2 + type + ")");

				int num = 1;
				for (int i = 0; i < productTypeList.size() * 2 - 1; i++) {
					cell = row.createCell(i);
					if (i == 0) {
						cell.setCellValue("日期");
					} else if (i % 2 != 0) {
						if (i == 1) {
							cell.setCellValue(productTypeList.get(i));
						} else {
							cell.setCellValue(productTypeList.get(i - num));
							num += 1;
						}
					}
					cell.setCellStyle(style);
					sheet.autoSizeColumn((short) i);
				}

				for (int i = 1; i < productTypeList.size() * 2 - 1; i = i + 2) {
					sheet.addMergedRegion(new CellRangeAddress(1, 1, i, i + 1));
				}

				for (int i = 2; i <= list.size() + 1; i++) {
					row = sheet.createRow(i);
					row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(list.get(i - 2)
									+ (tips.get(list.get(i - 2)) == null ? "" : tips.get(list.get(i - 2))));
					row.getCell(0).setCellStyle(contentStyle);
					int num1 = 1;
					for (int j = 1; j < productTypeList.size() * 2 - 1; j = j + 2) {
						Integer session = 0;
						Float conver = 0f;

						if (j == 1) {
							session = sessionDataMap1.get(list.get(i - 2)).get(productTypeList.get(j));
							conver = conversionDataMap.get(list.get(i - 2)).get(productTypeList.get(j));
						} else {
							session = sessionDataMap1.get(list.get(i - 2)).get(productTypeList.get(j - num1));
							conver = conversionDataMap.get(list.get(i - 2)).get(productTypeList.get(j - num1));
							num1 += 1;
						}
						row.createCell(j, Cell.CELL_TYPE_NUMERIC).setCellValue(session == null ? 0 : session);
						row.createCell(j + 1, Cell.CELL_TYPE_NUMERIC).setCellValue(conver == null ? 0 : conver / 100);
						row.getCell(j + 1).setCellStyle(cellStyle);
						row.getCell(j).setCellStyle(contentStyle);
					}
				}

				// total
				row = sheet.createRow(list.size() + 2);
				for (int i = 0; i < productTypeList.size() * 2 - 1; i++) {
					cell = row.createCell(i);
					row.getCell(i).setCellStyle(contentStyle);
					if (i == 0) {
						cell.setCellValue("total");
					} else if (i % 2 != 0) {
						int totalSession = 0;
						for (int j = 2; j <= list.size() + 1; j++) {
							totalSession = totalSession + (int) (sheet.getRow(j).getCell(i).getNumericCellValue());
						}
						cell.setCellValue(totalSession);
					}
				}
			} else if ("2".equals(active)) {
				for (int i = 0; i < productTypeList.size(); i++) {
					cell = row1.createCell(i);
					cell.setCellStyle(titleStyle);
				}
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, productTypeList.size() - 1));
				row1.getCell(0).setCellValue(
						DictUtils.getDictLabel(country, "platform", "") + " session统计(" + date1 + type + "~" + date2 + type + ")");

				for (int i = 0; i < productTypeList.size(); i++) {
					cell = row.createCell(i);
					if (i == 0) {
						cell.setCellValue("日期");
					} else {
						cell.setCellValue(productTypeList.get(i));
					}
					cell.setCellStyle(style);
					sheet.autoSizeColumn((short) i);
				}
				for (int i = 2; i <= list.size() + 1; i++) {
					row = sheet.createRow(i);
					row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(list.get(i - 2)
									+ (tips.get(list.get(i - 2)) == null ? "" : tips.get(list.get(i - 2))));
					row.getCell(0).setCellStyle(contentStyle);
					for (int j = 1; j < productTypeList.size(); j++) {
						Integer session = sessionDataMap1.get(list.get(i - 2)).get(productTypeList.get(j));
						row.createCell(j, Cell.CELL_TYPE_NUMERIC).setCellValue(session == null ? 0 : session);
						row.getCell(j).setCellStyle(contentStyle);
					}
				}

				row = sheet.createRow(list.size() + 2);
				for (int i = 0; i < productTypeList.size(); i++) {
					cell = row.createCell(i);
					row.getCell(i).setCellStyle(contentStyle);
					if (i == 0) {
						cell.setCellValue("total");
					} else {
						int totalSession = 0;
						for (int j = 2; j <= list.size() + 1; j++) {
							totalSession = totalSession
									+ (int) (sheet.getRow(j).getCell(i).getNumericCellValue());
						}
						cell.setCellValue(totalSession);
					}
				}

			} else {
				for (int i = 0; i < productTypeList.size(); i++) {
					cell = row1.createCell(i);
					cell.setCellStyle(titleStyle);
				}
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, productTypeList.size() - 1));
				row1.getCell(0).setCellValue(
						DictUtils.getDictLabel(country, "platform", "")+ " conversion统计("+ date1 +type + "~" + date2 +type+ ")");

				for (int i = 0; i < productTypeList.size(); i++) {
					cell = row.createCell(i);
					if (i == 0) {
						cell.setCellValue("日期");
					} else {
						cell.setCellValue(productTypeList.get(i));
					}
					cell.setCellStyle(style);
					sheet.autoSizeColumn((short) i);
				}
				for (int i = 2; i <= list.size() + 1; i++) {
					row = sheet.createRow(i);
					row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(list.get(i - 2)
									+ (tips.get(list.get(i - 2)) == null ? "" : tips.get(list.get(i - 2))));
					row.getCell(0).setCellStyle(cellStyle);
					for (int j = 1; j < productTypeList.size(); j++) {
						Float conver = conversionDataMap.get(list.get(i - 2)).get(productTypeList.get(j));
						row.createCell(j, Cell.CELL_TYPE_NUMERIC).setCellValue(conver == null ? 0 : conver / 100);
						row.getCell(j).setCellStyle(cellStyle);
					}
				}
			}
			sheet.autoSizeColumn((short) 0);
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");

				SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");
				String fileName = "countByProductTypeExport" + sdf1.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Autowired
	private SessionMonitorService sessionMonitorService;
	
	@RequiresPermissions("amazoninfo:businessReport:view")
	@RequestMapping(value = {"product"})
	public String product(BusinessReport businessReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			businessReport.setTitle(URLDecoder.decode(businessReport.getTitle(),"utf-8"));
		} catch (UnsupportedEncodingException e) {}
		
		Page<BusinessReport> page = new Page<BusinessReport>(request, response);
		
		page.setPageNo(1);
		page.setPageSize(10000);
		String orderBy = page.getOrderBy();
		String searchFlag = businessReport.getSearchFlag();
		if(searchFlag!=null){
			if(searchFlag.equals("0")||searchFlag.equals("")){
				if("".equals(orderBy)){
					page.setOrderBy("dataDate asc");
				}else{
					page.setOrderBy(orderBy+",dataDate asc");
				}	
			}else if (searchFlag.equals("1")){
				if("".equals(orderBy)){
					page.setOrderBy("weekGroup asc");
				}else{
					page.setOrderBy(orderBy+",weekGroup asc");
				}	
			}else if (searchFlag.equals("2")){
				if("".equals(orderBy)){
					page.setOrderBy("monthGroup asc");
				}else{
					page.setOrderBy(orderBy+",monthGroup asc");
				}
			}
		}
		if("0".equals(searchFlag)){
			model.addAttribute("sessionMap", sessionMonitorService.getProduct(businessReport));
		}
        try {
			page = businessReportService.findProductByDate(page, businessReport);
		} catch (Exception e) {}
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
        if("1,2".contains(searchFlag)){
	        for (BusinessReport bReport : page.getList()) {
				Date date = bReport.getDataDate();
				//重新组装dataDate
				if(searchFlag.equals("1")){
					bReport.setDateSpan("("+FORMAT1.format(DateUtils.getMonday(date))+"一"+FORMAT1.format(DateUtils.getSunday(date))+")");
				}else if(searchFlag.equals("2")){
					bReport.setDateSpan("("+FORMAT1.format(DateUtils.addMonths(date, -1))+"一"+FORMAT1.format(DateUtils.addDays(date, -1))+")");
				}
	        }	
        }
		return "modules/amazoninfo/businessReportProductList";
	}
	
	@ResponseBody
	@RequestMapping(value = {"result"})
	public String search(String country,String key) {
		try {
			key = URLDecoder.decode(key, "utf-8");
		} catch (UnsupportedEncodingException e) {}
		Map<String, Set<String>> rs = Maps.newLinkedHashMap();
		Set<String>set=Sets.newLinkedHashSet();
		set.add(key);
		processInfo(country,set,rs,1);
		return JSON.toJSONString(rs);
	}
	
	@RequestMapping(value = {"search"})
	public String list() {
		return "modules/amazoninfo/productKeySearch";
	}
	
	@RequestMapping(value = {"exportKeySearch"})
	public void exportKeySearch(String country,String key,HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			key = URLDecoder.decode(key, "utf-8");
		} catch (UnsupportedEncodingException e) {}
		Map<String, Set<String>> rs = Maps.newLinkedHashMap();
		Set<String>set=Sets.newLinkedHashSet();
		set.add(key);
		processInfo(country,set,rs,1);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/download;charset=utf-8");
		response.setHeader("Content-disposition", "attachment;filename="+key+".csv");
		OutputStream o = response.getOutputStream();
		OutputStreamWriter os = new OutputStreamWriter(o, "utf-8");
		BufferedWriter br=new BufferedWriter(os);
		String txt = "key;result;\n";
		br.write(txt);
		
		for (Map.Entry<String,Set<String>> entry: rs.entrySet()) {
			String searchKey=entry.getKey();
			Set<String> sets=entry.getValue();
			for (String temp: sets) {
				br.write(searchKey+";"+temp+";\n");
			}
		}
		br.flush();
		br.close();
	}
	
	private static Map<String, String> mktKey = Maps.newHashMap();
	
	static{
		mktKey.put("de", "4");
		mktKey.put("es", "44551");
		mktKey.put("fr","5");
		mktKey.put("com", "1");
		mktKey.put("it", "35691");
		mktKey.put("uk", "3");
		mktKey.put("jp", "6");
		mktKey.put("ca", "7");
		mktKey.put("mx", "771770");
	}
	
	private Set<String> getKeyInfo(String catalog,String country,String key) throws Exception{
		Set<String> rsList = Sets.newLinkedHashSet();
		String country1 = country;
		if(!(country.equals("jp")||country.equals("com"))){
			country1 = "co.uk";
		}
		if(country.equals("jp")||country.equals("uk")){
			country1 = "co."+country;
		}else if(country.equals("ca")){
			country1 = "com";
		}else if(country.equals("mx")){
			country1 = "com";
		}
		URL url = new URL("http://completion.amazon."+country1+"/search/complete?q="+URLEncoder.encode(HtmlUtils.htmlUnescape(key),"utf-8")+"&search-alias="+catalog+"&mkt="+mktKey.get(country));
		URLConnection  connection = url.openConnection();
		connection.setDoInput(true);
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		InputStream stream = connection.getInputStream();
		byte [] buf = new byte[stream.available()];
		stream.read(buf);
		stream.close();
		String rs =  new String(buf,"utf-8");
		Object obj = JSON.parse(rs);
		if(obj!=null && obj instanceof JSONArray){
			JSONArray list = (JSONArray)obj;
			if(list.size()>2){
				Object obj1 = list.get(1);
				if(obj1!=null && obj1 instanceof JSONArray){
					rsList.addAll(Lists.newArrayList(((JSONArray)obj1).toArray(new String[0])));
				}
			}
			if(catalog.equals("aps")){
				if(list.size()==5){
					Object obj1 = list.get(2);
					if(obj1!=null && obj1 instanceof JSONArray){
						obj1 = ((JSONObject)((JSONArray)obj1).get(0)).get("nodes");
						if(obj1!=null && obj1 instanceof JSONArray){
							JSONArray array = ((JSONArray)obj1);
							for (int i = 0; i < array.size(); i++) {
								String catalogStr = ((JSONObject)array.get(i)).get("alias").toString();
								if("aps".equals(catalogStr)){
									continue;
								}
								rsList.addAll(getKeyInfo(catalogStr,country,key));
							}
						}
					}
				}
			}
		}	
		return rsList;
	}
	
	private void processInfo(String country,Set<String>keys,Map<String, Set<String>> rs,int num){
		if(num>=2){
			return;
		}
		for (String key : keys) {
			int i = 0;
			int flag = 0 ;
			Set<String> lists= Sets.newLinkedHashSet();
			while(i<10 && flag == 0){
				try {
					lists = getKeyInfo("aps",country, key);
					lists.remove(key);
					flag = 1;
				} catch (Exception e) {
					i++;
				}
			}
			if(lists.size()>0){
				rs.put(key, lists);
				processInfo(country,lists,rs,num++);
			}else{
				return ;
			}	
		}
	}

	@RequiresPermissions("amazoninfo:businessReport:view")
	@RequestMapping(value = {"exportSessionByDate"})
	public String exportSessionByDate(String groupName,String date1, String date2, BusinessReport businessReport, HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, ParseException {
			DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
			DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
			
			Date start = businessReport.getCreateDate();
			Date end = businessReport.getDataDate();
			//查询2个时间节点
			if("1".equals(businessReport.getSearchFlag())){
				//按周查询
				if(StringUtils.isBlank(date1)){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addWeeks(today, -1);
					end = today;
					date1 = DateUtils.getWeekStr(start, formatWeek, 5, "-");
					date2 = DateUtils.getWeekStr(end, formatWeek, 5, "-");
				} else {
					if ("2015-53".equals(date1)) {
						date1 = "2016-01";
					}
					if ("2015-53".equals(date2)) {
						date2 = "2016-01";
					}
					if(date1.equals(date2)){
						end = formatWeek.parse(date2);
						start = DateUtils.addWeeks(end, -1);
						date1 = DateUtils.getWeekStr(start, formatWeek, 5, "-");
						date2 = DateUtils.getWeekStr(end, formatWeek, 5, "-");
					} else {
						start = formatWeek.parse(date1);
						end = formatWeek.parse(date2);
					}
				}
				businessReport.setCreateDate(start);
				businessReport.setDataDate(end);
			}else if("2".equals(businessReport.getSearchFlag())){
				//按月查询
				if(StringUtils.isBlank(date1)){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addMonths(today, -1);
					end = today;
				}else{
					if (date1.equals(date2)) {
						end = formatMonth.parse(date2);
						start = DateUtils.addMonths(end, -1);
					}else {
						start = formatMonth.parse(date1);
						end = formatMonth.parse(date2);
					}
				}
				businessReport.setCreateDate(start);
				businessReport.setDataDate(end);
				date1 = formatMonth.format(start);
				date2 = formatMonth.format(end);
			}else{
				if(StringUtils.isBlank(date1)){
					Date today = new Date();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					start = DateUtils.addDays(today, -5);//数据存在延时
					end = DateUtils.addDays(today, -4);
				}else{
					if (date1.equals(date2)) {
						end = formatDay.parse(date2);
						start = DateUtils.addDays(start, -1);
					}else {
						start = formatDay.parse(date1);
						end = formatDay.parse(date2);
					}
				}
				date1 = formatDay.format(start);
				date2 = formatDay.format(end);
			}
			
			Map<String, Map<String, BusinessReport>> data = businessReportService.totalSessions(businessReport, date1, date2,groupName);
			//构建x轴
			List<String> xAxis  = Lists.newArrayList();
			Map<String, String> tip = Maps.newHashMap();

			String type = "日";
			String titleStr = "ByDay";
			if("1".equals(businessReport.getSearchFlag())){
				//区间1
				xAxis.add(date1);
				int year1 = Integer.parseInt(date1.substring(0,4));
				int week1 =  Integer.parseInt(date1.substring(5));
				Date first1 = DateUtils.getFirstDayOfWeek(year1, week1);
				tip.put(date1,DateUtils.getDate(first1,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first1),"yyyy-MM-dd"));
				//区间2
				xAxis.add(date2);
				int year2 = Integer.parseInt(date2.substring(0,4));
				int week2 =  Integer.parseInt(date2.substring(5));
				Date first2 = DateUtils.getFirstDayOfWeek(year2, week2);
				tip.put(date2,DateUtils.getDate(first2,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first2),"yyyy-MM-dd"));
				
				type = "周";
				titleStr = "ByWeek";
			}else if("2".equals(businessReport.getSearchFlag())){
				//区间1
				String key1 = formatMonth.format(start);
				xAxis.add(key1);
				tip.put(key1, key1+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				//区间2
				String key2 = formatMonth.format(end);
				xAxis.add(key2);
				tip.put(key2, key2+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(end),"yyyy-MM-dd"));
				
				type = "月";
				titleStr = "ByMonth";
			}else{
				//区间1
				String key1 = formatDay.format(start);
				xAxis.add(key1);
				tip.put(key1,DateUtils.getDate(start,"E"));
				//区间2
				String key2 = formatDay.format(end);
				xAxis.add(key2);
				tip.put(key2,DateUtils.getDate(end,"E"));
			}
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			style.setBottomBorderColor(HSSFColor.BLACK.index);
			style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
			style.setLeftBorderColor(HSSFColor.BLACK.index);
			style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
			style.setRightBorderColor(HSSFColor.BLACK.index);
			style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
			style.setTopBorderColor(HSSFColor.BLACK.index);
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 16); // 字体高度
			font.setFontName(" 黑体 "); // 字体
			font.setBoldweight((short) 16);
			style.setFont(font);
			row.setHeight((short) 600);
			
			HSSFCellStyle cellStyle = wb.createCellStyle();
	        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFCell cell = null;
			
			List<String> title = Lists.newArrayList("  平台  ");
			for (int i = 0; i < xAxis.size(); i++) {
				title.add("session");title.add("转化率");
			}
			title.add("session差");title.add("转化率差");

			int num=0;
			int startIndex=1;
			int endIndex=2;
			for(int i=0;i<title.size();i++){
				cell = row.createCell(i);
				cell.setCellStyle(style);
				if(i==0||i==(title.size()-2)||i==(title.size()-1)){	//第一列和最后两列
					cell.setCellValue(title.get(i));
				}else if(i==startIndex && i != (title.size()-2)){	//i==1||i==3
					int index = num++;
					String content = xAxis.get(index) + type;
					if("1".equals(businessReport.getSearchFlag())){
						content = content + "(" + tip.get(xAxis.get(index)) + ")";
					}
					cell.setCellValue(content);
					startIndex+=2;
				}else if(i==endIndex && i != (title.size())){	//i==2||i==4合并两列
					sheet.addMergedRegion(new CellRangeAddress(0, 0,i-1,i));
					endIndex+=2;
				}
			}
			row = sheet.createRow(1);
			for(int i=0;i<title.size();i++){
				cell = row.createCell(i);
				cell.setCellStyle(style);
				if(i==0||i==title.size()-2||i==title.size()-1){	//第一列和最后两列合并上下两行
					sheet.addMergedRegion(new CellRangeAddress(0, 1,i,i));
				}else{
				   cell.setCellValue(title.get(i));
				}
			}
			List<Dict> dictAll=DictUtils.getDictList("platform");
			int rownum=2;
	    	for (Dict dict : dictAll) {
				if(!"com.unitek".equals(dict.getValue())){
					row = sheet.createRow(rownum++);
			    	row.setHeightInPoints(22);
			    	cell = row.createCell(0,Cell.CELL_TYPE_STRING);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(dict.getLabel());
					List<Integer> sessionList = Lists.newArrayList();
					List<Float> conversionList = Lists.newArrayList();
					for (int i = 1; i <= xAxis.size(); i++) {
						try {
							Integer session = data.get(dict.getValue()).get(xAxis.get(i-1)).getSessions();
							cell = row.createCell(2*i-1,Cell.CELL_TYPE_STRING);
							cell.setCellStyle(cellStyle);
							cell.setCellValue(session);
							sessionList.add(session);
						} catch (Exception e) {
							row.createCell(2*i-1,Cell.CELL_TYPE_STRING).setCellValue("");
							sessionList.add(0);
						}
						try {
							Float conversion = data.get(dict.getValue()).get(xAxis.get(i-1)).getConversion();
							cell = row.createCell(2*i,Cell.CELL_TYPE_STRING);
							cell.setCellStyle(cellStyle);
							cell.setCellValue(String.format("%.2f", conversion)+"%");
							conversionList.add(conversion);
						} catch (Exception e) {
							row.createCell(2*i,Cell.CELL_TYPE_STRING).setCellValue("");
							conversionList.add(0f);
						}
					}
			    	cell = row.createCell(5,Cell.CELL_TYPE_STRING);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(sessionList.get(1)-sessionList.get(0));
					if (conversionList.get(0) != null && conversionList.get(0) != 0) {
						Float f1 = Float.parseFloat(String.format("%.2f", conversionList.get(1)));
						Float f2 = Float.parseFloat(String.format("%.2f", conversionList.get(0)));
						cell = row.createCell(6,Cell.CELL_TYPE_STRING);
						cell.setCellStyle(cellStyle);
						Float conversionRate = f1-f2;
						cell.setCellValue(String.format("%.2f", conversionRate)+"%");
					}
				}
			}
	    	//加上总计
	    	row = sheet.createRow(rownum++);
	    	row.setHeightInPoints(22);
	    	cell = row.createCell(0,Cell.CELL_TYPE_STRING);
			cell.setCellStyle(cellStyle);
			cell.setCellValue("总计");
			List<Integer> sessionList = Lists.newArrayList();
			List<Float> conversionList = Lists.newArrayList();
			for (int i = 1; i <= xAxis.size(); i++) {
				try {
					Integer session = data.get("total").get(xAxis.get(i-1)).getSessions();
					cell = row.createCell(2*i-1,Cell.CELL_TYPE_STRING);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(session);
					sessionList.add(session);
				} catch (Exception e) {
					row.createCell(2*i-1,Cell.CELL_TYPE_STRING).setCellValue("");
					sessionList.add(0);
				}
				try {
					Float conversion = data.get("total").get(xAxis.get(i-1)).getConversion();
					cell = row.createCell(2*i,Cell.CELL_TYPE_STRING);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(String.format("%.2f", conversion/8)+"%");
					conversionList.add(conversion);
				} catch (Exception e) {
					row.createCell(2*i,Cell.CELL_TYPE_STRING).setCellValue("");
					conversionList.add(0f);
				}
			}
			cell = row.createCell(5,Cell.CELL_TYPE_STRING);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(sessionList.get(1)-sessionList.get(0));
			if (conversionList.get(0) != null && conversionList.get(0) != 0) {
				Float f1 = Float.parseFloat(String.format("%.2f", conversionList.get(1)/8));
				Float f2 = Float.parseFloat(String.format("%.2f", conversionList.get(0)/8));
				cell = row.createCell(6,Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);
				Float conversionRate = f1-f2;
				cell.setCellValue(String.format("%.2f", conversionRate)+"%");
			}

			for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
		    }
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	            String fileName = titleStr + "多平台session汇总比较" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
	}
	
	//首图与转化率分析
	@RequestMapping(value = {"imageAnalysis"})
	public String imageAnalysis(String country, String isChange,String active,String date1, String date2, 
			String productName, String searchFlag, Model model) throws ParseException{
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
		
		Date today = new Date();
		if(StringUtils.isEmpty(country)){
			country = "de";
		}
		if(StringUtils.isEmpty(active)){
			active = "1";
		}
		if(StringUtils.isEmpty(searchFlag)){
			searchFlag = "0";
		}
		if(StringUtils.isEmpty(isChange)){	//首图更改标记
			isChange = "1";
		}
		if (StringUtils.isNotEmpty(productName)) {
			try {
				productName = URLDecoder.decode(productName,"UTF-8");
			} catch (UnsupportedEncodingException e) {}
		}
		
		Date datet = null;
		if(today.getHours()>=8){
			datet = DateUtils.addDays(today, -2);
		}else{
			datet = DateUtils.addDays(today, -3);
		}
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str=sdf.format(datet);
		datet=sdf.parse(str);
		
		Date endDate =null;
		Date startDate=null;

		//分类型查询
		String type = "日"; 
		if("1".equals(searchFlag)){
			//按周查询
			if(StringUtils.isBlank(date1)){
				date1 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -20), formatWeek, 5, "-");
				Calendar cal = Calendar.getInstance();
				cal.setTime(today);
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				if (dayOfWeek > 3) {
					date2 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -1), formatWeek, 5, "-");
				} else {
					date2 = DateUtils.getWeekStr(DateUtils.addWeeks(today, -2), formatWeek, 5, "-");
				}
			} else {
				if ("2015-53".equals(date1)) {
					date1 = "2016-01";
				}
				if ("2015-53".equals(date2)) {
					date2 = "2016-01";
				}
			}
			startDate = formatWeek.parse(date1);
			endDate = formatWeek.parse(date2);
			type = "周";
			sdf = formatWeek;
		}else if("2".equals(searchFlag)){
			//按月查询
			if(StringUtils.isBlank(date1)){
				//date2 = formatMonth.format(today);
				date2 = formatMonth.format(DateUtils.addMonths(today, -1));
				date1 = formatMonth.format(DateUtils.addMonths(today, -12));
			}
			startDate = formatMonth.parse(date1);
			endDate = formatMonth.parse(date2);
			type = "月";
			sdf = formatMonth;
		}else{
			if(StringUtils.isBlank(date1)){
				date2 = formatDay.format(datet);
				date1 = formatDay.format(DateUtils.addDays(datet, -20));
			}
			startDate = formatDay.parse(date1);
			endDate = formatDay.parse(date2);
			//修改首图标记节点
			model.addAttribute("eventMap", businessReportService.findImageEvent(productName,date1,date2));
		}

		List<String> xAxis = Lists.newArrayList();
		List<String> tempAxis = Lists.newArrayList();
//---图表
        Date beforeDate=null;
		Date afterDate=null;
        
		if (searchFlag.equals("0") || searchFlag.equals("")) {
			searchFlag = "0";
			beforeDate = startDate;
			afterDate = endDate;
		} else if (searchFlag.equals("1")) {
			beforeDate = DateUtils.getMonday(startDate);
			afterDate = DateUtils.getSunday(endDate);
		} else if (searchFlag.equals("2")) {
			beforeDate = DateUtils.getFirstDayOfMonth(startDate);
			afterDate = DateUtils.getLastDayOfMonth(endDate);
		}
		
		TreeMap<Long, String> timeMap = Maps.newTreeMap();
		TreeMap<Long, String> timeMapOrder = Maps.newTreeMap();
		if(!startDate.after(endDate)){
			DateFormat format3 = null;
			if(endDate.getYear()==startDate.getYear()){
				format3 = FORMAT1;
			}else{
				format3 = FORMAT2;
			}	
			int i = 0 ;
			
			if(searchFlag.equals("0")){
				Date date = beforeDate;
				while(!date.after(afterDate)){
					i++;
					xAxis.add("'"+format3.format(date)+"'");
					tempAxis.add(formatDay.format(date));
					if(!date.after(datet)){
						timeMap.put(date.getTime(), "0");
					}
					date = DateUtils.addDays(beforeDate, i);
				}
			}else if (searchFlag.equals("1")){
				Date date = afterDate;
				Date datetSunday=DateUtils.getSunday(datet);
				while(date.after(beforeDate)){
					i++;
					if(!date.after(datetSunday)){
						timeMap.put(date.getTime(), "0");
					}
					timeMapOrder.put(date.getTime(), "0");
					tempAxis.add(formatWeek.format(date));
					date = DateUtils.addDays(afterDate, -7*i);
				}
				Collections.reverse(tempAxis);
				//迭代timeMap 排序时间
				for (Iterator<Entry<Long, String>> iterator = timeMapOrder.entrySet().iterator(); iterator.hasNext();) {
					Entry<Long, String> entry = iterator.next();
				    long lg=entry.getKey();
					Date datelg = new Date(lg);
					xAxis.add("'"+DateUtils.getWeekStr(DateUtils.addDays(datelg, 1), formatWeek, 5, "-")+"周'");
				}
			}else if (searchFlag.equals("2")){
				Date date = beforeDate;
				Date datetFirstDay= DateUtils.addMonths(DateUtils.getFirstDayOfMonth(datet),1);
				SimpleDateFormat format = new SimpleDateFormat("yyyy/M");
				while(!date.after(afterDate)){
					i++;
					xAxis.add("'"+format.format(date)+"月'");
					tempAxis.add(formatMonth.format(date));
					if(!date.after(datetFirstDay)){
						timeMap.put(date.getTime(), "0");
					}
					date = DateUtils.addMonths(beforeDate,i);
				}
			}
		}
		if (StringUtils.isNotEmpty(productName)) {
			try {
				productName = URLDecoder.decode(productName, "utf-8");
			} catch (UnsupportedEncodingException e) {}
			//TODO 图片分析数据
			List<BusinessReport> rs = businessReportService.findImageAnalysisData(country, productName, date1, date2, searchFlag);
			model.addAttribute("adsData", businessReportService.getAdsByName(country,searchFlag, date1, date2,Sets.newHashSet(productName)));
			
			//组合两个map在前台显示数据
			Map<String,Map<String,Integer>> sessionDataMap=Maps.newLinkedHashMap();
			Map<String,Map<String,Float>> conversionDataMap=Maps.newLinkedHashMap();
			Set<String> countrySet=Sets.newHashSet();
			
			Map<String,List<String>> sessionsData = Maps.newHashMap();
			Map<String,List<String>> conversionData = Maps.newHashMap();
			for (BusinessReport businessReport : rs) {
				String sessionKey=businessReport.getTitle();//sdf.format(businessReport.getDataDate());
				Map<String,Integer> tempMap=Maps.newLinkedHashMap();
				if(sessionDataMap.get(sessionKey)!=null){
					tempMap=sessionDataMap.get(businessReport.getTitle());
				}
				tempMap.put(businessReport.getCountry(), businessReport.getSessions());
				countrySet.add(businessReport.getCountry());
				sessionDataMap.put(sessionKey, tempMap);
				
				Map<String,Float> tempMap1=Maps.newLinkedHashMap();
				if(conversionDataMap.get(sessionKey)!=null){
					tempMap1=conversionDataMap.get(businessReport.getTitle());
				}
				tempMap1.put(businessReport.getCountry(), businessReport.getConversion());
				conversionDataMap.put(sessionKey, tempMap1);
				
				String key = "";
				if(!"total".equals(businessReport.getCountry())){
					key = DictUtils.getDictLabel(businessReport.getCountry(),"platform","");
					key = key.split("\\|")[0];
				} else {
					key = "total";
				}
					
				String sessions = businessReport.getSessions()==null?"0":businessReport.getSessions()+"";
				String conv = businessReport.getConversion()==null?"0":businessReport.getConversion()+"";
				Date date = sdf.parse(businessReport.getTitle());
				List<Long> time = Lists.newArrayList(timeMap.keySet());
				if(!("0".equals(sessions))){
					List<String> temp = sessionsData.get(key);
					if(temp==null){
						temp = Lists.newArrayList();
						for (Iterator<Entry<Long, String>> iterator = timeMap.entrySet().iterator(); iterator.hasNext();) {
							Entry<Long, String> entry = iterator.next();
							if(entry.getKey().equals(datet.getTime())){
								if(!key.equals("美国")&!key.equals("加拿大")){
									temp.add("0");
								}
								break;
							}else{
								temp.add("0");
							}
						}
						sessionsData.put(key, temp);
						temp = sessionsData.get(key);
					}
					int size = time.indexOf(date.getTime());
					if(size!=-1&& size<=temp.size()-1){
						temp.set(size,sessions);
					}
				}
				if(!("0".equals(conv)&&"0.0".equals(conv))){
					List<String> temp = conversionData.get(key);
					if(temp==null){
						temp = Lists.newArrayList();
						for (Iterator<Entry<Long, String>> iterator = timeMap.entrySet().iterator(); iterator
								.hasNext();) {
							Entry<Long, String> entry = iterator.next();
							if(entry.getKey().equals(datet.getTime())){
								if(!key.equals("美国")&!key.equals("加拿大")){
									temp.add("0");
								}
								break;
							}else{
								temp.add("0");
							}
						}
						conversionData.put(key, temp);
						temp = conversionData.get(key);
					}
					int size = time.indexOf(date.getTime());
					if(size!=-1&& size<=temp.size()-1){
						temp.set(size,conv);
					}
				}
			}
			
			Map<String,Map<String,Integer>> sessionDataMap1=Maps.newLinkedHashMap();
			List<String> list = Lists.newArrayList(sessionDataMap.keySet());
			Map<String, String> tips = Maps.newHashMap();
			for (String key : list) {
				sessionDataMap1.put(key, sessionDataMap.get(key));
				if(searchFlag.equals("1")){
					Date date = sdf.parse(key);
					date = DateUtils.addDays(date, 1);	//系统设计以星期一作为第一天,此处加一
					tips.put(key, "("+FORMAT1.format(DateUtils.getMonday(date))+"一"+FORMAT1.format(DateUtils.getSunday(date))+")");
				}
			}
			if(sessionsData.size()>0){
			    Map<String,Map<String,String>> sessionsMap=Maps.newLinkedHashMap();
			    int i=0;
				String key=getCountryByName(country);
				Map<String,String> temp=sessionsMap.get(country);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					sessionsMap.put(key, temp);
				}
				for (String sessionData: sessionsData.get(SystemService.countryNameMap.get(country))) {
					temp.put(tempAxis.get(i), sessionData);
					i++;
				}
				model.addAttribute("sessionsMap", sessionsMap);
			}
			
			model.addAttribute("sessionDataMap", sessionDataMap1);
			model.addAttribute("conversionDataMap", conversionDataMap);
			model.addAttribute("sessionsData", sessionsData);
			model.addAttribute("conversionData", conversionData);
			model.addAttribute("tips", tips);
			String show =  "";
			model.addAttribute("show", show);
			model.addAttribute("countrySet", Lists.newArrayList(countrySet));
		}
		model.addAttribute("xAxis", xAxis.toString());
		if ("1".equals(isChange)) {
			model.addAttribute("productNames", businessReportService.findImageChanges(country,date1,date2));
		} else {
			model.addAttribute("productNames", amazonProductService.findAllProductName());
		}
		
		model.addAttribute("date1",date1);
		model.addAttribute("date2",date2);
		model.addAttribute("type",type);
		model.addAttribute("searchFlag",searchFlag);
		model.addAttribute("productName", productName);
		model.addAttribute("active", active);
		model.addAttribute("isChange", isChange);
		model.addAttribute("country", country);
		model.addAttribute("countrySimple", SystemService.countryNameMap.get(country));
		
		return "modules/amazoninfo/reportImageAnalysis";
	}
	
	public String getCountryByName(String name){
		if("美国".equals(name)){
			return "com";
		}else if("加拿大".equals(name)){
			return "ca";
		}else if("墨西哥".equals(name)){
			return "mx";
		}else if("日本".equals(name)){
			return "jp";
		}else if("德国".equals(name)){
			return "de";
		}else if("英国".equals(name)){
			return "uk";
		}else if("法国".equals(name)){
			return "fr";
		}else if("意大利".equals(name)){
			return "it";
		}else if("西班牙".equals(name)){
			return "es";
		}
		return name;
	}
	
	public static void main(String[] args) throws Exception {
		URL url = new URL("https://ams.amazon.com/api/keyword-power?keywords=type+c&keywords=type+c+to+displayport&keywords=type+c+to+hdmi&keywords=type+c+pool+filter&matchTypes=BROAD&matchTypes=BROAD&matchTypes=BROAD&matchTypes=BROAD&landingPageUrl=http%3A%2F%2Fwww.amazon.com%2Fl%2F9123884011&cacheWarmup=false");
		URLConnection  connection = url.openConnection();
		connection.setDoInput(true);
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		InputStream stream = connection.getInputStream();
		byte [] buf = new byte[stream.available()];
		stream.read(buf);
		stream.close();
		String rs =  new String(buf,"utf-8");
		Object obj = JSON.parse(rs);
		if(obj!=null && obj instanceof JSONArray){
			
		}
	}
	
	
}




