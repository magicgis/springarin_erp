package com.springrain.erp.modules.amazoninfo.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.ZipUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonOperationalReport;
import com.springrain.erp.modules.amazoninfo.entity.AmazonSpreadReport;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.scheduler.SendEmailByOrderMonitor;
import com.springrain.erp.modules.amazoninfo.service.AmazonOperationReportService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;

@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/amazonOperationalReport")
public class AmazonOperationReportController extends BaseController {
	@Autowired
	private AmazonOperationReportService  amazonOperationReportService;
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	
	private static DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
	private static DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
	@RequestMapping(value = "")
	public String list(AmazonOperationalReport amazonOperationalReport,HttpServletRequest request, HttpServletResponse response, Model model) {
		if(amazonOperationalReport.getEndDate()==null){
			amazonOperationalReport.setEndDate(new Date());
			amazonOperationalReport.setCreateDate(DateUtils.addMonths(new Date(),-2));
		}
		return "modules/amazoninfo/amazonOperationalReportList";
	}
	 
	@RequestMapping(value = "spreadReportList")
	public String spreadReportList(AmazonSpreadReport amazonSpreadReport,HttpServletRequest request, HttpServletResponse response, Model model) {
		if(amazonSpreadReport.getCreateDate()==null){
			amazonSpreadReport.setEndDate(DateUtils.addDays(new Date(),-4));
			amazonSpreadReport.setCreateDate(DateUtils.addDays(new Date(),-19));
		}
		if(StringUtils.isBlank(amazonSpreadReport.getCountry())){
			amazonSpreadReport.setCountry("de");
		}
		Map<String,String> nameAndLineMap=groupDictService.getLineNameByName();//产品名-产品线
		List<AmazonSpreadReport> reportList=amazonOperationReportService.findAvgSpread(amazonSpreadReport);
		model.addAttribute("reportList",reportList);
		model.addAttribute("nameAndLineMap",nameAndLineMap);
		return "modules/amazoninfo/amazonSpreadReportList";
	}
	 
	@RequestMapping(value = "sessionCharts")
	public String sessionCharts(AmazonSpreadReport amazonSpreadReport,HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("country",amazonSpreadReport.getCountry());
		model.addAttribute("productName",amazonSpreadReport.getProductName());
		model.addAttribute("asin",amazonSpreadReport.getAsin());
		model.addAttribute("sku",amazonSpreadReport.getSku());
		model.addAttribute("start",amazonSpreadReport.getStart());
		model.addAttribute("end",amazonSpreadReport.getEnd());
		model.addAttribute("type","Session ");
		try {
			amazonSpreadReport.setCreateDate(formatDay.parse(amazonSpreadReport.getStart()));
			amazonSpreadReport.setEndDate(formatDay.parse(amazonSpreadReport.getEnd()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		Date start = amazonSpreadReport.getCreateDate();
		Date end = amazonSpreadReport.getEndDate();
		List<String> xAxis  = Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			 String key = formatDay.format(start);
		     xAxis.add(key);
			 start = DateUtils.addDays(start, 1);
		}
		Map<String,Integer> sessionMap=amazonOperationReportService.findSession(amazonSpreadReport);
		model.addAttribute("sessionMap",sessionMap);
		model.addAttribute("xAxis",xAxis);
		return "modules/amazoninfo/amazonSpreadSessionReportList";
	}
	
	@RequestMapping(value = "orderCharts")
	public String orderCharts(AmazonSpreadReport amazonSpreadReport,HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("country",amazonSpreadReport.getCountry());
		model.addAttribute("productName",amazonSpreadReport.getProductName());
		model.addAttribute("asin",amazonSpreadReport.getAsin());
		model.addAttribute("sku",amazonSpreadReport.getSku());
		model.addAttribute("start",amazonSpreadReport.getStart());
		model.addAttribute("end",amazonSpreadReport.getEnd());
		model.addAttribute("type","Order ");
		try {
			amazonSpreadReport.setCreateDate(formatDay.parse(amazonSpreadReport.getStart()));
			amazonSpreadReport.setEndDate(formatDay.parse(amazonSpreadReport.getEnd()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		Date start = amazonSpreadReport.getCreateDate();
		Date end = amazonSpreadReport.getEndDate();
		List<String> xAxis  = Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			 String key = formatDay.format(start);
		     xAxis.add(key);
			 start = DateUtils.addDays(start, 1);
		}
		Map<String,Integer> sessionMap=amazonOperationReportService.findOrder(amazonSpreadReport);
		model.addAttribute("sessionMap",sessionMap);
		model.addAttribute("xAxis",xAxis);
		return "modules/amazoninfo/amazonSpreadSessionReportList";
	}
	
	@RequestMapping(value = "salesVolumeCharts")
	public String salesVolumeCharts(AmazonSpreadReport amazonSpreadReport,HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("country",amazonSpreadReport.getCountry());
		model.addAttribute("productName",amazonSpreadReport.getProductName());
		model.addAttribute("asin",amazonSpreadReport.getAsin());
		model.addAttribute("sku",amazonSpreadReport.getSku());
		model.addAttribute("start",amazonSpreadReport.getStart());
		model.addAttribute("end",amazonSpreadReport.getEnd());
		model.addAttribute("type","Ps ");
		try {
			amazonSpreadReport.setCreateDate(formatDay.parse(amazonSpreadReport.getStart()));
			amazonSpreadReport.setEndDate(formatDay.parse(amazonSpreadReport.getEnd()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		Date start = amazonSpreadReport.getCreateDate();
		Date end = amazonSpreadReport.getEndDate();
		List<String> xAxis  = Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			 String key = formatDay.format(start);
		     xAxis.add(key);
			 start = DateUtils.addDays(start, 1);
		}
		Map<String,Integer> sessionMap=amazonOperationReportService.findSalesVolume(amazonSpreadReport);
		model.addAttribute("sessionMap",sessionMap);
		model.addAttribute("xAxis",xAxis);
		return "modules/amazoninfo/amazonSpreadSessionReportList";
	}
	
	
	@RequestMapping(value = "salesCharts")
	public String salesCharts(AmazonSpreadReport amazonSpreadReport,HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("country",amazonSpreadReport.getCountry());
		model.addAttribute("productName",amazonSpreadReport.getProductName());
		model.addAttribute("asin",amazonSpreadReport.getAsin());
		model.addAttribute("sku",amazonSpreadReport.getSku());
		model.addAttribute("start",amazonSpreadReport.getStart());
		model.addAttribute("end",amazonSpreadReport.getEnd());
		model.addAttribute("type","Revenue ");
		try {
			amazonSpreadReport.setCreateDate(formatDay.parse(amazonSpreadReport.getStart()));
			amazonSpreadReport.setEndDate(formatDay.parse(amazonSpreadReport.getEnd()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		Date start = amazonSpreadReport.getCreateDate();
		Date end = amazonSpreadReport.getEndDate();
		List<String> xAxis  = Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			 String key = formatDay.format(start);
		     xAxis.add(key);
			 start = DateUtils.addDays(start, 1);
		}
		Map<String,Float> sessionMap=amazonOperationReportService.findSales(amazonSpreadReport);
		model.addAttribute("sessionMap",sessionMap);
		model.addAttribute("xAxis",xAxis);
		return "modules/amazoninfo/amazonSpreadSessionReportList";
	}
	
	
	@RequestMapping(value = "conversionCharts")
	public String conversionCharts(AmazonSpreadReport amazonSpreadReport,HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("country",amazonSpreadReport.getCountry());
		model.addAttribute("productName",amazonSpreadReport.getProductName());
		model.addAttribute("asin",amazonSpreadReport.getAsin());
		model.addAttribute("sku",amazonSpreadReport.getSku());
		model.addAttribute("start",amazonSpreadReport.getStart());
		model.addAttribute("end",amazonSpreadReport.getEnd());
		model.addAttribute("type","CR ");
		try {
			amazonSpreadReport.setCreateDate(formatDay.parse(amazonSpreadReport.getStart()));
			amazonSpreadReport.setEndDate(formatDay.parse(amazonSpreadReport.getEnd()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		Date start = amazonSpreadReport.getCreateDate();
		Date end = amazonSpreadReport.getEndDate();
		List<String> xAxis  = Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			 String key = formatDay.format(start);
		     xAxis.add(key);
			 start = DateUtils.addDays(start, 1);
		}
		Map<String,Float> sessionMap=amazonOperationReportService.findConversion(amazonSpreadReport);
		model.addAttribute("sessionMap",sessionMap);
		model.addAttribute("xAxis",xAxis);
		return "modules/amazoninfo/amazonSpreadSessionReportList";
	}
	 @RequestMapping(value = "exportCompareData")
	 public String exportCompareData(AmazonOperationalReport amazonOperationalReport,HttpServletRequest request, HttpServletResponse response, Model model) {
		    String date1 = amazonOperationalReport.getDate1();
		    String date2 = amazonOperationalReport.getDate2();
		    Date start=new Date();
		    Date end=new Date();
		    if("2".equals(amazonOperationalReport.getSearchType())){
				//按周查询
					if ("2015-53".equals(date1)) {
						date1 = "2016-01";
					}
					if ("2015-53".equals(date2)) {
						date2 = "2016-01";
					}
					
						try {
							if(date1.equals(date2)){
								end = formatWeek.parse(date2);
								start = DateUtils.addWeeks(end, -1);
								date1 = DateUtils.getWeekStr(start, formatWeek,5, "-");
								date2 = DateUtils.getWeekStr(end, formatWeek,5, "-");
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
			}else if("3".equals(amazonOperationalReport.getSearchType())){
				//按月查询
						try {
							if (date1.equals(date2)) {
							  end = formatMonth.parse(date2);
							  start = DateUtils.addMonths(end, -1);
							  date1 = formatMonth.format(start);
							  date2 = formatMonth.format(end);
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
				
			}else{
				try{
					if (date1.equals(date2)) {
						end = formatDay.parse(date2);
						start = DateUtils.addDays(end, -1);
						date1 = formatDay.format(start);
						date2 = formatDay.format(end);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				
			}
		 Map<String,Map<String,Map<String,Map<String,AmazonOperationalReport>>>>  map=amazonOperationReportService.compareAllCountry(amazonOperationalReport,date2,date1);
		 Map<String, Map<String,Map<String,AmazonOperationalReport>>> orderTypeMap=amazonOperationReportService.getSalesTypeByProduct(amazonOperationalReport.getSearchType(),date1,date2);
		 Map<String,Map<String,AmazonOperationalReport>> orderType1=Maps.newHashMap();
		 Map<String,Map<String,AmazonOperationalReport>> orderType2=Maps.newHashMap();
		 if(orderTypeMap!=null&&orderTypeMap.size()>0){
			 orderType1=orderTypeMap.get(date1);
			 orderType2=orderTypeMap.get(date2);
		 }
		
		 HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
			style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBottomBorderColor(HSSFColor.BLACK.index);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setLeftBorderColor(HSSFColor.BLACK.index);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setRightBorderColor(HSSFColor.BLACK.index);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setTopBorderColor(HSSFColor.BLACK.index);
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 10); // 字体高度
			font.setFontName(" 宋体 "); // 字体
			font.setBoldweight((short) 10);
			style.setFont(font);
			row.setHeight((short) 600);
			CellStyle contentStyle = wb.createCellStyle();
			contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
			contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
			HSSFCellStyle cellStyle = wb.createCellStyle();
	        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
	        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
	        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
	        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
	        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
	        
	    	HSSFCellStyle cellStyle2 = wb.createCellStyle();
	    	cellStyle2.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));
	    	cellStyle2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    	cellStyle2.setBottomBorderColor(HSSFColor.BLACK.index);
	    	cellStyle2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    	cellStyle2.setLeftBorderColor(HSSFColor.BLACK.index);
	    	cellStyle2.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    	cellStyle2.setRightBorderColor(HSSFColor.BLACK.index);
	    	cellStyle2.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    	cellStyle2.setTopBorderColor(HSSFColor.BLACK.index);
	        HSSFFont font1 = wb.createFont();
			font1.setColor(HSSFColor.RED.index);
			cellStyle2.setFont(font1);
			
			HSSFCellStyle cellStyle3 = wb.createCellStyle();
			cellStyle3.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));
	    	cellStyle3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    	cellStyle3.setBottomBorderColor(HSSFColor.BLACK.index);
	    	cellStyle3.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    	cellStyle3.setLeftBorderColor(HSSFColor.BLACK.index);
	    	cellStyle3.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    	cellStyle3.setRightBorderColor(HSSFColor.BLACK.index);
	    	cellStyle3.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    	cellStyle3.setTopBorderColor(HSSFColor.BLACK.index);
	    
			
	    	
	    	HSSFCellStyle cellStyle5 = wb.createCellStyle();
	        cellStyle5.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
	        cellStyle5.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	        cellStyle5.setBottomBorderColor(HSSFColor.BLACK.index);
	        cellStyle5.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	        cellStyle5.setLeftBorderColor(HSSFColor.BLACK.index);
	        cellStyle5.setBorderRight(HSSFCellStyle.BORDER_THIN);
	        cellStyle5.setRightBorderColor(HSSFColor.BLACK.index);
	        cellStyle5.setBorderTop(HSSFCellStyle.BORDER_THIN);
	        cellStyle5.setTopBorderColor(HSSFColor.BLACK.index);
	        
	        
			
			
			HSSFCell cell = null;
			List<String> title = Lists.newArrayList("平台","产品","类型",date1,"","","","","",date2,"","","","","",date1+" VS "+date2,"","","","","总增幅占比","",date1+" VS "+date2+"增幅","","","","","销售额变动原因分析",date1+" VS "+date2,"","");
			List<String> title2 = Lists.newArrayList("平台","产品","类型","session","转化率","session订单","销量","单价","销售额","session","转化率","session订单","销量","单价","销售额","session","转化率","销量","单价","销售额","减少","增加","session","转化率","销量","单价","销售额","销售额变动原因分析","大订单","促销","闪购");
			for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				/*if(i==7||i==12||i==17||i==22){
					sheet.addMergedRegion(new CellRangeAddress(0, 0,i-4,i));
				}else if(i==26){
					sheet.addMergedRegion(new CellRangeAddress(0, 0,i-3,i));
				}*/
				if(i==17||i==24||i==30){
					sheet.addMergedRegion(new CellRangeAddress(0, 0,i-2,i));
				}else if(i==21){
					sheet.addMergedRegion(new CellRangeAddress(0, 0,i-1,i));
				}
				sheet.autoSizeColumn((short) i);
		    }
			row = sheet.createRow(1);
			for (int i = 0; i < title2.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title2.get(i));
				cell.setCellStyle(style);
				/*if(i==0||i==1||i==2||i==title.size()-1){
					sheet.addMergedRegion(new CellRangeAddress(0,1,i,i));
				}*/
				sheet.autoSizeColumn((short) i);
		    }
			int rownum=2;// //国家/日期/产品类型/产品
			for (Map.Entry<String, Map<String, Map<String, Map<String, AmazonOperationalReport>>>> entry : map.entrySet()) { 
			    String country=entry.getKey();
				Map<String,Map<String,Map<String,AmazonOperationalReport>>> temp=entry.getValue();
				//for (String date: temp.keySet()) {
					Map<String,Map<String,AmazonOperationalReport>> dateMap1=temp.get(date1);
					Map<String,Map<String,AmazonOperationalReport>> dateMap2=temp.get(date2);
					Set<String> typeSet=Sets.newHashSet();
					if(dateMap1!=null){
						typeSet.addAll(dateMap1.keySet());
					}
					if(dateMap2!=null){
						typeSet.addAll(dateMap2.keySet());
					}
					
					if(typeSet!=null&&typeSet.size()>0){
						Integer allMaxOrder1=0;
		    			Integer allMaxOrder2=0;
		    			Integer allPromotionsOrder1=0;
		    			Integer allPromotionsOrder2=0;
		    			Integer allFlashSalesOrder1=0;
		    			Integer allFlashSalesOrder2=0;
						for (String type: typeSet) {
							Integer totalSession1=0;
							float totalConversionRate1=0f;
							Integer totalSalesVolume1=0;
							float totalUnitPrice1=0f;
							float totalSales1=0f;
							
							Integer totalSession2=0;
							float totalConversionRate2=0f;
							Integer totalSalesVolume2=0;
							float totalUnitPrice2=0f;
							float totalSales2=0f;
							
							Integer totalMaxOrder1=0;
			    			Integer totalMaxOrder2=0;
			    			Integer totalPromotionsOrder1=0;
			    			Integer totalPromotionsOrder2=0;
			    			Integer totalFlashSalesOrder1=0;
			    			Integer totalFlashSalesOrder2=0;
							
							if("total".equals(type)){
								continue;
							}
							
							Set<String> nameSet=Sets.newHashSet();
							if(dateMap1!=null&&dateMap1.get(type)!=null){
								nameSet.addAll(dateMap1.get(type).keySet());
							}
							if(dateMap2!=null&&dateMap2.get(type)!=null){
								nameSet.addAll(dateMap2.get(type).keySet());
							}
							if(nameSet==null||nameSet.size()==0){
								continue;
							}
							for (String name: nameSet) {
								Integer session1=0;
								float conversionRate1=0f;
								Integer salesVolume1=0;
								float unitPrice1=0f;
								float sales1=0f;
								
								Integer session2=0;
								float conversionRate2=0f;
								Integer salesVolume2=0;
								float unitPrice2=0f;
								float sales2=0f;
								
								if("total".equals(name)){
									continue;
								}
								int j=0;
								row=sheet.createRow(rownum++);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		
					    		
					    		
					    		if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null){
					    			Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
					    			session1=typeMap.get(name).getSession();
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session1);
						    		row.getCell(j-1).setCellStyle(cellStyle5);
						    		
					    			if(typeMap.get(name).getSession()!=0){
						    			conversionRate1=typeMap.get(name).getSessionOrder()*1.0f/typeMap.get(name).getSession();
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate1);//转化率
						    		}else{
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		}
						    		row.getCell(j-1).setCellStyle(cellStyle3);
						    		
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(typeMap.get(name).getSessionOrder());
						    		row.getCell(j-1).setCellStyle(contentStyle);
						    		
						    		salesVolume1=typeMap.get(name).getSalesVolume();
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume1);
						    		row.getCell(j-1).setCellStyle(contentStyle);
						    		
						    		if(typeMap.get(name).getSalesVolume()!=0){
						    			unitPrice1=typeMap.get(name).getSales()/typeMap.get(name).getSalesVolume();
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice1);
						    			row.getCell(j-1).setCellStyle(cellStyle);
						    		}else{
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    			row.getCell(j-1).setCellStyle(contentStyle);
						    		}
						    		
						    		sales1=typeMap.get(name).getSales();
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales1);
						    		row.getCell(j-1).setCellStyle(cellStyle5);
					    			
					    		}else{
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    		}
					    		
					    		
					    		
					    		if(dateMap2!=null&&dateMap2.get(type)!=null&&dateMap2.get(type).get(name)!=null){
					    			AmazonOperationalReport report=dateMap2.get(type).get(name);
									session2=report.getSession();
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session2);
						    		row.getCell(j-1).setCellStyle(cellStyle5);
						    		
						    		if(report.getSession()!=0){
						    			conversionRate2=report.getSessionOrder()*1.0f/report.getSession();
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate2);//转化率
						    		}else{
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		}
						    		row.getCell(j-1).setCellStyle(cellStyle3);
						    		
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSessionOrder());
						    		row.getCell(j-1).setCellStyle(contentStyle);
						    		
						    		salesVolume2=report.getSalesVolume();
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume2);
						    		row.getCell(j-1).setCellStyle(contentStyle);
						    		
						    		if(report.getSalesVolume()!=0){
						    			unitPrice2=report.getSales()/report.getSalesVolume();
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice2);
						    			row.getCell(j-1).setCellStyle(cellStyle);
						    		}else{
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    			row.getCell(j-1).setCellStyle(contentStyle);
						    		}
						    		sales2=report.getSales();
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2);
						    		row.getCell(j-1).setCellStyle(cellStyle5);
					    		}else{
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    		}
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session2-session1);
				    			row.getCell(j-1).setCellStyle(cellStyle5);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate2-conversionRate1);
				    			row.getCell(j-1).setCellStyle(cellStyle3);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume2-salesVolume1);
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice2-unitPrice1);
				    			row.getCell(j-1).setCellStyle(cellStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
				    			row.getCell(j-1).setCellStyle(cellStyle5);
					    			
				    			if(sales2-sales1<=0){
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			}else{
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
				    			}
				    			row.getCell(j-2).setCellStyle(cellStyle5);
				    			row.getCell(j-1).setCellStyle(cellStyle5);
				    			
				    			
				    			float temp1=0f;
				    			float temp2=0f;
				    			float temp3=0f;
				    			float temp4=0f;
				    			float temp5=0f;
				    		
				    			    if(session1!=0){
				    			    	temp1=new BigDecimal((session2-session1)*1.0f/session1).setScale(2,4).floatValue();
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp1);
						    			row.getCell(j-1).setCellStyle(session2-session1<0?cellStyle2:cellStyle3);
					    			}else{
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    			row.getCell(j-1).setCellStyle(contentStyle);
					    			}
					    			
					    			temp2=new BigDecimal(conversionRate2-conversionRate1).setScale(2, 4).floatValue();
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp2);
						    		row.getCell(j-1).setCellStyle(conversionRate2-conversionRate1<0?cellStyle2:cellStyle3);
					    			
					    			if(salesVolume1!=0){
					    				temp3=new BigDecimal((salesVolume2-salesVolume1)*1.0f/salesVolume1).setScale(2, 4).floatValue();
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp3);
						    			row.getCell(j-1).setCellStyle(salesVolume2-salesVolume1<0?cellStyle2:cellStyle3);
					    			}else{
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    			row.getCell(j-1).setCellStyle(contentStyle);
					    			}
					    			
					    			if(unitPrice1!=0){
					    				temp4=new BigDecimal((unitPrice2-unitPrice1)*1.0f/unitPrice1).setScale(2, 4).floatValue();
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp4);
						    			row.getCell(j-1).setCellStyle(unitPrice2-unitPrice1<0?cellStyle2:cellStyle3);
					    			}else{
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    			row.getCell(j-1).setCellStyle(contentStyle);
					    			}
					    			if(sales1!=0){
					    				temp5=new BigDecimal((sales2-sales1)*1.0f/sales1).setScale(2, 4).floatValue();
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp5);
						    			row.getCell(j-1).setCellStyle(sales2-sales1<0?cellStyle2:cellStyle3);
					    			}else{
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    			row.getCell(j-1).setCellStyle(contentStyle);
					    			}
					    			
					    			String info="";
					    			if(temp1<0){
					    				info+="session降低"+new BigDecimal(temp1*100).setScale(2,4).floatValue()+"%;";
					    			}else if(temp1>0){
					    				info+="session上涨"+new BigDecimal(temp1*100).setScale(2,4).floatValue()+"%;";
					    			}
					    			if(temp2<0){
					    				info+=" 转化率降低"+new BigDecimal(temp2*100).setScale(2,4).floatValue()+"%;";
					    			}else if(temp2>0){
					    				info+=" 转化率上涨"+new BigDecimal(temp2*100).setScale(2,4).floatValue()+"%;";
					    			}
					    			
					    			if(temp4<0){
					    				info+="单价降低"+new BigDecimal(temp4*100).setScale(2,4).floatValue()+"%;";
					    			}else if(temp4>0){
					    				info+="单价上涨"+new BigDecimal(temp4*100).setScale(2,4).floatValue()+"%;";
					    			}
					    			
					    			Integer maxOrder1=0;
					    			Integer maxOrder2=0;
					    			Integer promotionsOrder1=0;
					    			Integer promotionsOrder2=0;
					    			Integer flashSalesOrder1=0;
					    			Integer flashSalesOrder2=0;
					    			
					    			if(orderType1!=null&&orderType1.get(country)!=null&&orderType1.get(country).get(name)!=null){
					    				AmazonOperationalReport rp=orderType1.get(country).get(name);
					    				info+=date1+":";
					    				if(rp.getPromotionsOrder()!=null&&rp.getPromotionsOrder()!=0){
					    					info+="促销,"+rp.getPromotionsOrder()+";";
					    					promotionsOrder1=rp.getPromotionsOrder();
					    					totalPromotionsOrder1+=rp.getPromotionsOrder();
					    					allPromotionsOrder1+=rp.getPromotionsOrder();
					    				}
					    				if(rp.getFlashSalesOrder()!=null&&rp.getFlashSalesOrder()!=0){
					    					info+="闪购,"+rp.getFlashSalesOrder()+";";
					    					flashSalesOrder1=rp.getFlashSalesOrder();
					    					totalFlashSalesOrder1+=rp.getFlashSalesOrder();
					    					allFlashSalesOrder1+=rp.getFlashSalesOrder();
					    				}
					    				if(rp.getMaxOrder()!=null&&rp.getMaxOrder()!=0){
					    					info+="大订单,"+rp.getMaxOrder()+";";
					    					maxOrder1=rp.getMaxOrder();
					    					totalMaxOrder1+=rp.getMaxOrder();
					    					allMaxOrder1+=rp.getMaxOrder();
					    				}
					    			}
					    			if(orderType2!=null&&orderType2.get(country)!=null&&orderType2.get(country).get(name)!=null){
					    				AmazonOperationalReport rp=orderType2.get(country).get(name);
					    				info+=date2+":";
					    				if(rp.getPromotionsOrder()!=null&&rp.getPromotionsOrder()!=0){
					    					info+="促销,"+rp.getPromotionsOrder()+";";
					    					promotionsOrder2=rp.getPromotionsOrder();
					    					totalPromotionsOrder2+=rp.getPromotionsOrder();
					    					allPromotionsOrder2+=rp.getPromotionsOrder();
					    				}
					    				if(rp.getFlashSalesOrder()!=null&&rp.getFlashSalesOrder()!=0){
					    					info+="闪购,"+rp.getFlashSalesOrder()+";";
					    					flashSalesOrder2=rp.getFlashSalesOrder();
					    					totalFlashSalesOrder2+=rp.getFlashSalesOrder();
					    					allFlashSalesOrder2+=rp.getFlashSalesOrder();
					    				}
					    				if(rp.getMaxOrder()!=null&&rp.getMaxOrder()!=0){
					    					info+="大订单,"+rp.getMaxOrder()+";";
					    					maxOrder2=rp.getMaxOrder();
					    					totalMaxOrder2+=rp.getPromotionsOrder();
					    					allMaxOrder2+=rp.getMaxOrder();
					    				}
					    			}
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(info);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(maxOrder2-maxOrder1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(promotionsOrder2-promotionsOrder1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(flashSalesOrder2-flashSalesOrder1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			
							}
							//total
							int j=0;
							row=sheet.createRow(rownum++);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("总计");
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		
				    		
				    		if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null){
				    			Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
				    			totalSession1=typeMap.get("total").getSession();
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSession1);
					    		row.getCell(j-1).setCellStyle(cellStyle5);
					    		
					    		if(typeMap.get("total").getSession()!=0){
					    			totalConversionRate1=typeMap.get("total").getSessionOrder()*1.0f/typeMap.get("total").getSession();
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalConversionRate1);//转化率
					    		}else{
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		}
					    		row.getCell(j-1).setCellStyle(cellStyle3);
					    		
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(typeMap.get("total").getSessionOrder());
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		
					    		
					    		totalSalesVolume1=typeMap.get("total").getSalesVolume();
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSalesVolume1);
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		
					    		if(typeMap.get("total").getSalesVolume()!=0){
					    			totalUnitPrice1=typeMap.get("total").getSales()/typeMap.get("total").getSalesVolume();
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalUnitPrice1);
					    			row.getCell(j-1).setCellStyle(cellStyle);
					    		}else{
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    		}
					    		totalSales1=typeMap.get("total").getSales();
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales1);
					    		row.getCell(j-1).setCellStyle(cellStyle5);
				    		}else{
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    		}

				    		
				    		
				    		if(dateMap2!=null&&dateMap2.get(type)!=null&&dateMap2.get(type).get("total")!=null){
				    			AmazonOperationalReport report=dateMap2.get(type).get("total");
				    			totalSession2=report.getSession();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSession2);
					    		row.getCell(j-1).setCellStyle(cellStyle5);
					    		
					    		if(report.getSession()!=0){
					    			totalConversionRate2=report.getSessionOrder()*1.0f/report.getSession();
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalConversionRate2);//转化率
					    		}else{
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		}
					    		row.getCell(j-1).setCellStyle(cellStyle3);
					    		
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSessionOrder());
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		
					    		
					    		totalSalesVolume2=report.getSalesVolume();
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSalesVolume2);
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		
					    		if(report.getSalesVolume()!=0){
					    			totalUnitPrice2=report.getSales()/report.getSalesVolume();
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalUnitPrice2);
					    			row.getCell(j-1).setCellStyle(cellStyle);
					    		}else{
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    		}
					    		totalSales2=report.getSales();
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales2);
					    		row.getCell(j-1).setCellStyle(cellStyle5);
				    		}else{
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    		}
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSession2-totalSession1);
			    			row.getCell(j-1).setCellStyle(cellStyle5);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalConversionRate2-totalConversionRate1);
			    			row.getCell(j-1).setCellStyle(cellStyle3);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSalesVolume2-totalSalesVolume1);
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalUnitPrice2-totalUnitPrice1);
			    			row.getCell(j-1).setCellStyle(cellStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales2-totalSales1);
			    			row.getCell(j-1).setCellStyle(cellStyle5);
			    			
			    			if(totalSales2-totalSales1<=0){
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales2-totalSales1);
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales2-totalSales1);
			    			}
			    			row.getCell(j-2).setCellStyle(cellStyle5);
			    			row.getCell(j-1).setCellStyle(cellStyle5);
			    			
			    			float temp1=0f;
			    			float temp2=0f;
			    			float temp3=0f;
			    			float temp4=0f;
			    			float temp5=0f;
			    			
			    			
			    			 if(totalSession1!=0){
			    				 temp1=new BigDecimal((totalSession2-totalSession1)*1.0f/totalSession1).setScale(2, 4).floatValue();
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp1);
					    			row.getCell(j-1).setCellStyle(totalSession2-totalSession1<0?cellStyle2:cellStyle3);
				    			}else{
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
				    			}
				    		
				    			temp2=new BigDecimal(totalConversionRate2-totalConversionRate1).setScale(2, 4).floatValue();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp2);
					    		row.getCell(j-1).setCellStyle(totalConversionRate2-totalConversionRate1<0?cellStyle2:cellStyle3);
				    			
				    			if(totalSalesVolume1!=0){
				    				temp3=new BigDecimal((totalSalesVolume2-totalSalesVolume1)*1.0f/totalSalesVolume1).setScale(2, 4).floatValue();
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp3);
					    			row.getCell(j-1).setCellStyle(totalSalesVolume2-totalSalesVolume1<0?cellStyle2:cellStyle3);
				    			}else{
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
				    			}
				    			
				    			if(totalUnitPrice1!=0){
				    				temp4=new BigDecimal((totalUnitPrice2-totalUnitPrice1)/totalUnitPrice1).setScale(2, 4).floatValue();
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp4);
					    			row.getCell(j-1).setCellStyle(totalUnitPrice2-totalUnitPrice1<0?cellStyle2:cellStyle3);
				    			}else{
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
				    			}
				    			if(totalSales1!=0){
				    				temp5=new BigDecimal((totalSales2-totalSales1)*1.0f/totalSales1).setScale(2, 4).floatValue();
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp5);
					    			row.getCell(j-1).setCellStyle(totalSales2-totalSales1<0?cellStyle2:cellStyle3);
				    			}else{
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
				    			}
				    			
				    			/*if(totalSales2-totalSales1<=0){
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales2-totalSales1);
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			}else{
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales2-totalSales1);
				    			}
				    			row.getCell(j-2).setCellStyle(cellStyle);
				    			row.getCell(j-1).setCellStyle(cellStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(cellStyle);*/
				    			
					    			String info="";
					    			if(temp1<0){
					    				info+="session降低"+new BigDecimal(temp1*100).setScale(2,4).floatValue()+"%;";
					    			}else if(temp1>0){
					    				info+="session上涨"+new BigDecimal(temp1*100).setScale(2,4).floatValue()+"%;";
					    			}
					    			if(temp2<0){
					    				info+=" 转化率降低"+new BigDecimal(temp2*100).setScale(2,4).floatValue()+"%;";
					    			}else if(temp2>0){
					    				info+=" 转化率上涨"+new BigDecimal(temp2*100).setScale(2,4).floatValue()+"%;";
					    			}
					    			/*if(temp3<0){
					    				info+="销量降低"+temp3+"%;";
					    			}else if(temp3>0){
					    				info+="销量上涨"+temp3+"%;";
					    			}*/
					    			if(temp4<0){
					    				info+="单价降低"+new BigDecimal(temp4*100).setScale(2,4).floatValue()+"%;";
					    			}else if(temp4>0){
					    				info+="单价上涨"+new BigDecimal(temp4*100).setScale(2,4).floatValue()+"%;";
					    			}
					    			/*if(temp5<0){
					    				info+="销售额降低"+temp5+"%;";
					    			}else if(temp5>0){
					    				info+="销售额上涨"+temp5+"%;";
					    			}*/
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(info);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalMaxOrder2-totalMaxOrder1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalPromotionsOrder2-totalPromotionsOrder1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalFlashSalesOrder2-totalFlashSalesOrder1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
						}
						
						Integer session1=0;
						float conversionRate1=0f;
						Integer salesVolume1=0;
						float unitPrice1=0f;
						float sales1=0f;
						
						Integer session2=0;
						float conversionRate2=0f;
						Integer salesVolume2=0;
						float unitPrice2=0f;
						float sales2=0f;
						
						
						int j=0;
						row=sheet.createRow(rownum++);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("总计");
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("总计");
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		
			    		if(dateMap1!=null&&dateMap1.get("total")!=null&&dateMap1.get("total").get("total")!=null){
				    		AmazonOperationalReport amazonReport=dateMap1.get("total").get("total");
				    		session1=amazonReport.getSession();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session1);
				    		row.getCell(j-1).setCellStyle(cellStyle5);
				    		
				    		if(amazonReport.getSession()!=0){
				    			conversionRate1=amazonReport.getSessionOrder()*1.0f/amazonReport.getSession();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate1);//转化率
				    		}else{
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		}
				    		row.getCell(j-1).setCellStyle(cellStyle3);
				    		
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(amazonReport.getSessionOrder());
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		
				    		salesVolume1=amazonReport.getSalesVolume();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume1);
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		
				    		if(amazonReport.getSalesVolume()!=0){
				    			unitPrice1=amazonReport.getSales()/amazonReport.getSalesVolume();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice1);
				    			row.getCell(j-1).setCellStyle(cellStyle);
				    		}else{
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    		}
				    		sales1=amazonReport.getSales();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales1);
				    		row.getCell(j-1).setCellStyle(cellStyle5);
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    		}
			    		
			    		
			    		if(dateMap2!=null&&dateMap2.get("total")!=null&&dateMap2.get("total").get("total")!=null){
			    			AmazonOperationalReport report=dateMap2.get("total").get("total");
			    			session2=report.getSession();
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session2);
				    		row.getCell(j-1).setCellStyle(cellStyle5);
				    		
				    		if(report.getSession()!=0){
				    			conversionRate2=report.getSessionOrder()*1.0f/report.getSession();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate2);//转化率
				    		}else{
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		}
				    		row.getCell(j-1).setCellStyle(cellStyle3);
				    		
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSessionOrder());
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		
				    		salesVolume2=report.getSalesVolume();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume2);
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		
				    		if(report.getSalesVolume()!=0){
				    			unitPrice2=report.getSales()/report.getSalesVolume();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice2);
				    			row.getCell(j-1).setCellStyle(cellStyle);
				    		}else{
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    		}
				    		sales2=report.getSales();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2);
				    		row.getCell(j-1).setCellStyle(cellStyle5);
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    		}
			    		
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session2-session1);
		    			row.getCell(j-1).setCellStyle(cellStyle5);
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate2-conversionRate1);
		    			row.getCell(j-1).setCellStyle(cellStyle3);
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume2-salesVolume1);
		    			row.getCell(j-1).setCellStyle(contentStyle);
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice2-unitPrice1);
		    			row.getCell(j-1).setCellStyle(cellStyle);
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
		    			row.getCell(j-1).setCellStyle(cellStyle5);
		    			
		    			if(sales2-sales1<=0){
		    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
		    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    			}else{
		    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
		    			}
		    			row.getCell(j-2).setCellStyle(cellStyle5);
		    			row.getCell(j-1).setCellStyle(cellStyle5);
		    			
		    			float temp1=0f;
		    			float temp2=0f;
		    			float temp3=0f;
		    			float temp4=0f;
		    			float temp5=0f;
		    			
		    			 if(session1!=0){
		    			    	temp1=new BigDecimal((session2-session1)*1.0f/session1).setScale(2,4).floatValue();
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp1);
				    			row.getCell(j-1).setCellStyle(session2-session1<0?cellStyle2:cellStyle3);
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    			
			    			temp2=new BigDecimal(conversionRate2-conversionRate1).setScale(2, 4).floatValue();
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp2);
				    		row.getCell(j-1).setCellStyle(conversionRate2-conversionRate1<0?cellStyle2:cellStyle3);
			    			
			    			if(salesVolume1!=0){
			    				temp3=new BigDecimal((salesVolume2-salesVolume1)*1.0f/salesVolume1).setScale(2, 4).floatValue();
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp3);
				    			row.getCell(j-1).setCellStyle(salesVolume2-salesVolume1<0?cellStyle2:cellStyle3);
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    			
			    			if(unitPrice1!=0){
			    				temp4=new BigDecimal((unitPrice2-unitPrice1)*1.0f/unitPrice1).setScale(2, 4).floatValue();
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp4);
				    			row.getCell(j-1).setCellStyle(unitPrice2-unitPrice1<0?cellStyle2:cellStyle3);
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    			if(sales1!=0){
			    				temp5=new BigDecimal((sales2-sales1)*1.0f/sales1).setScale(2, 4).floatValue();
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp5);
				    			row.getCell(j-1).setCellStyle(sales2-sales1<0?cellStyle2:cellStyle3);
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    			
			    			String info="";
			    			if(temp1<0){
			    				info+="session降低"+new BigDecimal(temp1*100).setScale(2, 4).floatValue()+"%;";
			    			}else if(temp1>0){
			    				info+="session上涨"+new BigDecimal(temp1*100).setScale(2, 4).floatValue()+"%;";
			    			}
			    			if(temp2<0){
			    				info+=" 转化率降低"+new BigDecimal(temp2*100).setScale(2, 4).floatValue()+"%;";
			    			}else if(temp2>0){
			    				info+=" 转化率上涨"+new BigDecimal(temp2*100).setScale(2, 4).floatValue()+"%;";
			    			}
			    			
			    			if(temp4<0){
			    				info+="单价降低"+new BigDecimal(temp4*100).setScale(2, 4).floatValue()+"%;";
			    			}else if(temp4>0){
			    				info+="单价上涨"+new BigDecimal(temp4*100).setScale(2, 4).floatValue()+"%;";
			    			}
		    			
		    			/*if(sales2-sales1<=0){
		    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
		    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    			}else{
		    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
		    			}
		    			row.getCell(j-2).setCellStyle(cellStyle);
		    			row.getCell(j-1).setCellStyle(cellStyle);
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    			row.getCell(j-1).setCellStyle(contentStyle);
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    			row.getCell(j-1).setCellStyle(cellStyle);
			    			
			    			String info="";
			    			if(temp1<0){
			    				info+="session降低"+temp1+"%;";
			    			}else if(temp1>0){
			    				info+="session上涨"+temp1+"%;";
			    			}
			    			if(temp2<0){
			    				info+=" 转化率降低"+temp2+"%;";
			    			}else if(temp2>0){
			    				info+=" 转化率上涨"+temp2+"%;";
			    			}
			    			if(temp3<0){
			    				info+="销量降低"+temp3+"%;";
			    			}else if(temp3>0){
			    				info+="销量上涨"+temp3+"%;";
			    			}
			    			if(temp4<0){
			    				info+="单价降低"+temp4+"%;";
			    			}else if(temp4>0){
			    				info+="单价上涨"+temp4+"%;";
			    			}
			    			if(temp5<0){
			    				info+="销售额降低"+temp5+"%;";
			    			}else if(temp5>0){
			    				info+="销售额上涨"+temp5+"%;";
			    			}*/
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(info);
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(allMaxOrder2-allMaxOrder1);
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(allPromotionsOrder2-allPromotionsOrder1);
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(allFlashSalesOrder2-allFlashSalesOrder1);
			    			row.getCell(j-1).setCellStyle(contentStyle);
					}
					
					
				//}
				
			}
			for (int i = 0; i < title.size(); i++) {
		   		   sheet.autoSizeColumn((short)i,true);
			}
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	            String fileName ="运营数据对比统计" + sdf.format(new Date());
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName+".xls");
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
	 }	 
	 
	 
	 
	    private static Map<String,String> threadOperationReport = Maps.newHashMap();
	    
	    @RequestMapping(value = "byTimeTempExport")
	    public String byTimeTempExport(final AmazonOperationalReport amazonOperationalReport,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes){
	    	final String baseDirStr= ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/amazonOperationReport";
	    	Date start = amazonOperationalReport.getCreateDate();
			Date end = amazonOperationalReport.getEndDate();
			String type=amazonOperationalReport.getSearchType();
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
			final String key =  dateFormat.format(start)+"-"+dateFormat.format(end)+"-"+type;
	    	File zipFile = new File (baseDirStr+"/"+key+".xls");
			if(threadOperationReport.get(key)==null){
				if(!zipFile.exists()|| zipFile.lastModified()+12*3600000<new Date().getTime()){
		    		new Thread(){
		    			public void run() {
		    				threadOperationReport.put(key, "1");
		    				File baseDir = new File(baseDirStr+"/"+baseDirStr);
		    				if(!baseDir.exists()){
		    					baseDir.mkdirs();
	    					}
		    				if(amazonOperationalReport.getEndDate()==null){
		    					amazonOperationalReport.setEndDate(new Date());
		    					amazonOperationalReport.setCreateDate(DateUtils.addMonths(new Date(),-2));
		    				}
		    		    	Map<String,Map<String,Map<String,Map<String,AmazonOperationalReport>>>> map=amazonOperationReportService.findAllCountry(amazonOperationalReport);
		    				//Map<String,Map<String,Map<String,AmazonOperationalReport>>> typeMap=amazonOperationReportService.findAllCountryByType(amazonOperationalReport);
		    		    	Map<String, Map<String,Map<String,AmazonOperationalReport>>> orderTypeMap=amazonOperationReportService.getOrderType(amazonOperationalReport);
		    		    	
		    				HSSFWorkbook wb = new HSSFWorkbook();
		    				HSSFSheet sheet = wb.createSheet();
		    				HSSFRow row = sheet.createRow(0);
		    				HSSFCellStyle style = wb.createCellStyle();
		    				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		    				style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		    				style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		    				style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		    				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
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
		    				CellStyle contentStyle = wb.createCellStyle();
		    				contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    				contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		    				contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    				contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		    				contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    				contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		    				contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    				contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		    				HSSFCellStyle cellStyle = wb.createCellStyle();
		    		        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		    		        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    		        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		    		        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    		        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		    		        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    		        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
		    		        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    		        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		    				HSSFCell cell = null;
		    				List<String> title = Lists.newArrayList("平台","时间("+("1".equals(amazonOperationalReport.getSearchType())?"日":("2".equals(amazonOperationalReport.getSearchType())?"周":"月"))+")","类型","产品","session","转化率","session订单","销量","总订单量","退货数","退货率","单价","销售额","总评价","差评","差评率","大订单","促销","闪购");

		    				for (int i = 0; i < title.size(); i++) {
		    					cell = row.createCell(i);
		    					cell.setCellValue(title.get(i));
		    					cell.setCellStyle(style);
		    					sheet.autoSizeColumn((short) i);
		    			    }
		    				int rownum=1;
		    				for (Map.Entry<String, Map<String, Map<String, Map<String, AmazonOperationalReport>>>> entry: map.entrySet()) { 
		    				    String country=entry.getKey();
		    					Map<String,Map<String,Map<String,AmazonOperationalReport>>> temp=entry.getValue();
		    					for (Map.Entry<String, Map<String, Map<String, AmazonOperationalReport>>> entryTemp: temp.entrySet()) { 
		    					    String date=entryTemp.getKey();
		    						Map<String,Map<String,AmazonOperationalReport>> dateMap=entryTemp.getValue();
		    						for (Map.Entry<String, Map<String, AmazonOperationalReport>> entryDate: dateMap.entrySet()) {
		    						    String type=entryDate.getKey();
		    							Map<String,AmazonOperationalReport> typeMap=entryDate.getValue();
		    							Integer session=0;
		    							Integer salesVolume=0;
		    							Integer returnVolume=0;
		    							Integer orderVolume=0;
		    							Integer sessionOrder=0;
		    							float sales=0f;
		    							Integer badReview=0;
		    							Integer totalReview=0;
		    							
		    							Integer maxOrder=0;
		    							Integer flashOrder=0;
		    							Integer promotionsOrder=0;
		    							for (Map.Entry<String, AmazonOperationalReport> entryType: typeMap.entrySet()) {
		    							    String name=entryType.getKey();
		    							    AmazonOperationalReport report=entryType.getValue();
		    								int j=0;
		    								row=sheet.createRow(rownum++);
		    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
		    					    		row.getCell(j-1).setCellStyle(contentStyle);
		    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
		    					    		row.getCell(j-1).setCellStyle(contentStyle);
		    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
		    					    		row.getCell(j-1).setCellStyle(contentStyle);
		    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
		    					    		row.getCell(j-1).setCellStyle(contentStyle);
		    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSession());
		    					    		session+=report.getSession();
		    					    		row.getCell(j-1).setCellStyle(contentStyle);
		    					    		
		    					    		if(report.getSession()!=0){
		    					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSessionOrder()*100f/report.getSession());//转化率
		    					    		}else{
		    					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//
		    					    		}
		    					    		row.getCell(j-1).setCellStyle(cellStyle);
		    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSessionOrder());
		    					    		row.getCell(j-1).setCellStyle(contentStyle);
		    					    		sessionOrder+=report.getSessionOrder();
		    					    		
		    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSalesVolume());
		    					    		salesVolume+=report.getSalesVolume();
		    					    	
		    					    		row.getCell(j-1).setCellStyle(contentStyle);
		    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getOrderVolume());
		    					    		orderVolume+=report.getOrderVolume();
		    					    		row.getCell(j-1).setCellStyle(contentStyle);
		    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getReturnVolume());
		    					    		returnVolume+=report.getReturnVolume();
		    					    		row.getCell(j-1).setCellStyle(contentStyle);
		    					    		if(report.getOrderVolume()!=0){
		    					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getReturnVolume()*100f/report.getOrderVolume());//退货率
		    					    		}else{
		    					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//退货率
		    					    		}
		    					    		
		    					    		row.getCell(j-1).setCellStyle(cellStyle);
		    					    		
		    					    		if(report.getSalesVolume()!=0){
		    					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSales()/report.getSalesVolume());
		    					    			row.getCell(j-1).setCellStyle(cellStyle);
		    					    		}else{
		    					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    					    			row.getCell(j-1).setCellStyle(contentStyle);
		    					    		}
		    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSales());
		    					    		sales+=report.getSales();
		    					    		row.getCell(j-1).setCellStyle(cellStyle);
		    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getTotalReview());
		    					    		totalReview+=report.getTotalReview();
		    					    		badReview+=report.getBadReview();
		    					    		row.getCell(j-1).setCellStyle(contentStyle);
		    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getBadReview());
		    					    		row.getCell(j-1).setCellStyle(contentStyle);
		    					    		if(report.getTotalReview()!=0){
		    					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getBadReview()*100f/report.getTotalReview());
		    					    		}else{
		    					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//退货率
		    					    		}
		    					    		row.getCell(j-1).setCellStyle(cellStyle);//
		    					    		
		    					    		if(orderTypeMap!=null&&orderTypeMap.get(date)!=null&&orderTypeMap.get(date).get(country)!=null&&orderTypeMap.get(date).get(country).get(name)!=null){
		    					    			AmazonOperationalReport rp=orderTypeMap.get(date).get(country).get(name);
		    					    			if(rp.getMaxOrder()!=null&&rp.getMaxOrder()!=0){
		    					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rp.getMaxOrder());//max
		    						    			row.getCell(j-1).setCellStyle(contentStyle);
		    						    			if(maxOrder<rp.getMaxOrder()){
		    						    				maxOrder=rp.getMaxOrder();
		    						    			}
		    					    			}else{
		    					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    						    			row.getCell(j-1).setCellStyle(contentStyle);
		    					    			}
		    					    			if(rp.getPromotionsOrder()!=null&&rp.getPromotionsOrder()!=0){
		    					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rp.getPromotionsOrder());//max
		    						    			row.getCell(j-1).setCellStyle(contentStyle);
		    						    			promotionsOrder+=rp.getPromotionsOrder();
		    					    			}else{
		    					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    						    			row.getCell(j-1).setCellStyle(contentStyle);
		    					    			}
		    					    			if(rp.getFlashSalesOrder()!=null&&rp.getFlashSalesOrder()!=0){
		    					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rp.getFlashSalesOrder());//max
		    						    			row.getCell(j-1).setCellStyle(contentStyle);
		    						    			flashOrder+=rp.getFlashSalesOrder();
		    					    			}else{
		    					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    						    			row.getCell(j-1).setCellStyle(contentStyle);
		    					    			}
		    					    		}else{
		    					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    					    			row.getCell(j-1).setCellStyle(cellStyle);
		    					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//pro
		    					    			row.getCell(j-1).setCellStyle(cellStyle);
		    					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//flash
		    					    			row.getCell(j-1).setCellStyle(cellStyle);
		    					    		}
		    							}
		    							
		    							int j=0;
		    							row=sheet.createRow(rownum++);
		    				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
		    				    		row.getCell(j-1).setCellStyle(contentStyle);
		    				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
		    				    		row.getCell(j-1).setCellStyle(contentStyle);
		    				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
		    				    		row.getCell(j-1).setCellStyle(contentStyle);
		    				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("总计");
		    				    		row.getCell(j-1).setCellStyle(contentStyle);
		    				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session);
		    				    		row.getCell(j-1).setCellStyle(contentStyle);
		    				    		
		    				    		if(session!=0){
		    				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sessionOrder*100f/session);//转化率
		    				    		}else{
		    				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    				    		}
		    				    		row.getCell(j-1).setCellStyle(cellStyle);
		    				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sessionOrder);
		    				    		row.getCell(j-1).setCellStyle(contentStyle);
		    				    		
		    				    		
		    				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);

		    				    		row.getCell(j-1).setCellStyle(contentStyle);
		    				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(orderVolume);
		    				    		row.getCell(j-1).setCellStyle(contentStyle);
		    				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(returnVolume);
		    				    		row.getCell(j-1).setCellStyle(contentStyle);
		    				    		if(orderVolume!=0){
		    				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(returnVolume*100f/orderVolume);//退货率
		    				    		}else{
		    				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//退货率
		    				    		}
		    				    		
		    				    		row.getCell(j-1).setCellStyle(cellStyle);
		    				    		
		    				    		if(salesVolume!=0){
		    				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales/salesVolume);
		    				    			row.getCell(j-1).setCellStyle(cellStyle);
		    				    		}else{
		    				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    				    			row.getCell(j-1).setCellStyle(contentStyle);
		    				    		}
		    				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales);
		    				
		    				    		row.getCell(j-1).setCellStyle(cellStyle);
		    				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalReview);
		    				    		row.getCell(j-1).setCellStyle(contentStyle);
		    				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(badReview);
		    				    		row.getCell(j-1).setCellStyle(contentStyle);
		    				    		if(totalReview!=0){
		    				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(badReview*100f/totalReview);
		    				    		}else{
		    				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//退货率
		    				    		}
		    				    		row.getCell(j-1).setCellStyle(cellStyle);//
		    				    		
		    				    		if(maxOrder!=0){
		    			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(maxOrder);//max
		    				    			row.getCell(j-1).setCellStyle(contentStyle);
		    			    			}else{
		    			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    				    			row.getCell(j-1).setCellStyle(contentStyle);
		    			    			}
		    			    			if(promotionsOrder!=0){
		    			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(promotionsOrder);//max
		    				    			row.getCell(j-1).setCellStyle(contentStyle);
		    			    			}else{
		    			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    				    			row.getCell(j-1).setCellStyle(contentStyle);
		    			    			}
		    			    			if(flashOrder!=0){
		    			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(flashOrder);//max
		    				    			row.getCell(j-1).setCellStyle(contentStyle);
		    			    			}else{
		    			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    				    			row.getCell(j-1).setCellStyle(contentStyle);
		    			    			}
		    						}
		    						
		    					}
		    					
		    				}
		    				
		    				for (int i = 0; i < title.size(); i++) {
		    			   		   sheet.autoSizeColumn((short)i,true);
		    				}
		    				try {
		    					File totalFile = new File(baseDirStr,key+".xls");
		    					
		    					FileOutputStream totalFos =new FileOutputStream(totalFile);
		    					wb.write(totalFos);
		    				} catch (Exception e) {
		    					e.printStackTrace();
		    				}
		    				threadOperationReport.remove(key);
		    			};
		    		}.start();
		    		addMessage(redirectAttributes, "一分钟后刷新下载...");
				}else{
					try {
	    				response.addHeader("Content-Disposition", "attachment;filename="
	    						+zipFile.getName());
	    				OutputStream out = response.getOutputStream();
	    				out.write(FileUtils.readFileToByteArray(zipFile));
	    				out.flush();
	    				out.close();
	        		} catch (Exception e) {
	    				e.printStackTrace();
	    			}
	        		
				}
			}
			return "redirect:"+Global.getAdminPath()+"/amazoninfo/businessReport/listByDate";
	    }	
		 
	@ResponseBody  
    @RequestMapping(value = "byTimeExport")
	public String byTimeExport(AmazonOperationalReport amazonOperationalReport,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
    	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    	if(StringUtils.isBlank(amazonOperationalReport.getDate1())){
			amazonOperationalReport.setEndDate(new Date());
			amazonOperationalReport.setCreateDate(DateUtils.addMonths(new Date(),-1));
		}else{
			amazonOperationalReport.setEndDate(dateFormat.parse(amazonOperationalReport.getDate2()));
			amazonOperationalReport.setCreateDate(dateFormat.parse(amazonOperationalReport.getDate1()));
		}
    	
    	Map<String,Map<String,Map<String,Map<String,AmazonOperationalReport>>>> map=amazonOperationReportService.findAllCountry(amazonOperationalReport);
    	Map<String, Map<String,Map<String,AmazonOperationalReport>>> orderTypeMap=amazonOperationReportService.getOrderType(amazonOperationalReport);
    	
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
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
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCell cell = null;
		List<String> title = Lists.newArrayList("平台","时间("+("1".equals(amazonOperationalReport.getSearchType())?"日":("2".equals(amazonOperationalReport.getSearchType())?"周":"月"))+")","类型","产品","session","转化率","session订单","销量","总订单量","退货数","退货率","单价","销售额","总评价","差评","差评率","大订单","促销","闪购");

		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
	    }
		int rownum=1;
		for (Map.Entry<String, Map<String, Map<String, Map<String, AmazonOperationalReport>>>> entry: map.entrySet()) { 
		    String country=entry.getKey();
			Map<String,Map<String,Map<String,AmazonOperationalReport>>> temp=entry.getValue();
			for (Map.Entry<String, Map<String, Map<String, AmazonOperationalReport>>> entryTemp: temp.entrySet()) { 
			    String date=entryTemp.getKey();
				Map<String,Map<String,AmazonOperationalReport>> dateMap=entryTemp.getValue();
				for (Map.Entry<String, Map<String, AmazonOperationalReport>> entryDate: dateMap.entrySet()) {
				    String type=entryDate.getKey();
					Map<String,AmazonOperationalReport> typeMap=entryDate.getValue();
					Integer session=0;
					Integer salesVolume=0;
					Integer returnVolume=0;
					Integer orderVolume=0;
					Integer sessionOrder=0;
					float sales=0f;
					Integer badReview=0;
					Integer totalReview=0;
					
					Integer maxOrder=0;
					Integer flashOrder=0;
					Integer promotionsOrder=0;
					for (Map.Entry<String, AmazonOperationalReport> entryType: typeMap.entrySet()) {
					    String name=entryType.getKey();
					    AmazonOperationalReport report=entryType.getValue();
						int j=0;
						row=sheet.createRow(rownum++);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSession());
			    		session+=report.getSession();
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		
			    		if(report.getSession()!=0){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSessionOrder()*100f/report.getSession());//转化率
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//
			    		}
			    		row.getCell(j-1).setCellStyle(cellStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSessionOrder());
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		sessionOrder+=report.getSessionOrder();
			    		
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSalesVolume());
			    		salesVolume+=report.getSalesVolume();
			    	
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getOrderVolume());
			    		orderVolume+=report.getOrderVolume();
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getReturnVolume());
			    		returnVolume+=report.getReturnVolume();
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		if(report.getOrderVolume()!=0){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getReturnVolume()*100f/report.getOrderVolume());//退货率
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//退货率
			    		}
			    		
			    		row.getCell(j-1).setCellStyle(cellStyle);
			    		
			    		if(report.getSalesVolume()!=0){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSales()/report.getSalesVolume());
			    			row.getCell(j-1).setCellStyle(cellStyle);
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    		}
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSales());
			    		sales+=report.getSales();
			    		row.getCell(j-1).setCellStyle(cellStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getTotalReview());
			    		totalReview+=report.getTotalReview();
			    		badReview+=report.getBadReview();
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getBadReview());
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		if(report.getTotalReview()!=0){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getBadReview()*100f/report.getTotalReview());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//退货率
			    		}
			    		row.getCell(j-1).setCellStyle(cellStyle);//
			    		
			    		if(orderTypeMap!=null&&orderTypeMap.get(date)!=null&&orderTypeMap.get(date).get(country)!=null&&orderTypeMap.get(date).get(country).get(name)!=null){
			    			AmazonOperationalReport rp=orderTypeMap.get(date).get(country).get(name);
			    			if(rp.getMaxOrder()!=null&&rp.getMaxOrder()!=0){
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rp.getMaxOrder());//max
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			if(maxOrder<rp.getMaxOrder()){
				    				maxOrder=rp.getMaxOrder();
				    			}
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    			if(rp.getPromotionsOrder()!=null&&rp.getPromotionsOrder()!=0){
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rp.getPromotionsOrder());//max
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			promotionsOrder+=rp.getPromotionsOrder();
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    			if(rp.getFlashSalesOrder()!=null&&rp.getFlashSalesOrder()!=0){
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rp.getFlashSalesOrder());//max
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			flashOrder+=rp.getFlashSalesOrder();
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
			    			row.getCell(j-1).setCellStyle(cellStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//pro
			    			row.getCell(j-1).setCellStyle(cellStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//flash
			    			row.getCell(j-1).setCellStyle(cellStyle);
			    		}
					}
					
					int j=0;
					row=sheet.createRow(rownum++);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("总计");
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		
		    		if(session!=0){
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sessionOrder*100f/session);//转化率
		    		}else{
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    		}
		    		row.getCell(j-1).setCellStyle(cellStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sessionOrder);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		
		    		
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);

		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(orderVolume);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(returnVolume);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		if(orderVolume!=0){
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(returnVolume*100f/orderVolume);//退货率
		    		}else{
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//退货率
		    		}
		    		
		    		row.getCell(j-1).setCellStyle(cellStyle);
		    		
		    		if(salesVolume!=0){
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales/salesVolume);
		    			row.getCell(j-1).setCellStyle(cellStyle);
		    		}else{
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    			row.getCell(j-1).setCellStyle(contentStyle);
		    		}
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales);
		
		    		row.getCell(j-1).setCellStyle(cellStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalReview);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(badReview);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		if(totalReview!=0){
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(badReview*100f/totalReview);
		    		}else{
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//退货率
		    		}
		    		row.getCell(j-1).setCellStyle(cellStyle);//
		    		
		    		if(maxOrder!=0){
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(maxOrder);//max
		    			row.getCell(j-1).setCellStyle(contentStyle);
	    			}else{
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    			row.getCell(j-1).setCellStyle(contentStyle);
	    			}
	    			if(promotionsOrder!=0){
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(promotionsOrder);//max
		    			row.getCell(j-1).setCellStyle(contentStyle);
	    			}else{
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    			row.getCell(j-1).setCellStyle(contentStyle);
	    			}
	    			if(flashOrder!=0){
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(flashOrder);//max
		    			row.getCell(j-1).setCellStyle(contentStyle);
	    			}else{
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    			row.getCell(j-1).setCellStyle(contentStyle);
	    			}
				}
				
			}
			
		}
		
		for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
		}
		/*try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
            String fileName ="运营数据统计" + sdf.format(new Date());
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName+".xls");
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		try {
			final String baseDirStr= ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/amazonOperationReport";
	    	Date start = amazonOperationalReport.getCreateDate();
			Date end = amazonOperationalReport.getEndDate();
			String type=amazonOperationalReport.getSearchType();
			SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyyMMddHHmmss");
			SimpleDateFormat dateFormat3=new SimpleDateFormat("yyyyMMdd");
			final String key =  dateFormat3.format(start)+"-"+dateFormat3.format(end)+"-"+type+"-"+dateFormat2.format(new Date());
			File totalFile = new File(baseDirStr,key+".xls");
			File baseDir = new File(baseDirStr);
			if(!baseDir.exists()){
				baseDir.mkdirs();
			}
			FileOutputStream totalFos =new FileOutputStream(totalFile);
			wb.write(totalFos);
			return key+".xls";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
  
    @RequestMapping(value = "exportCompareData2")
	 public String exportCompareData2(AmazonOperationalReport amazonOperationalReport,HttpServletRequest request, HttpServletResponse response, Model model) {
		    String date1 = amazonOperationalReport.getDate1();
		    String date2 = amazonOperationalReport.getDate2();
		    Date start=new Date();
		    Date end=new Date();
		    if("2".equals(amazonOperationalReport.getSearchType())){
				//按周查询
					if ("2015-53".equals(date1)) {
						date1 = "2016-01";
					}
					if ("2015-53".equals(date2)) {
						date2 = "2016-01";
					}
					
						try {
							if(date1.equals(date2)){
								end = formatWeek.parse(date2);
								start = DateUtils.addWeeks(end, -1);
								date1 = DateUtils.getWeekStr(start, formatWeek,5, "-");
								date2 = DateUtils.getWeekStr(end, formatWeek,5, "-");
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
			}else if("3".equals(amazonOperationalReport.getSearchType())){
				//按月查询
						try {
							if (date1.equals(date2)) {
							  end = formatMonth.parse(date2);
							  start = DateUtils.addMonths(end, -1);
							  date1 = formatMonth.format(start);
							  date2 = formatMonth.format(end);
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
				
			}else{
				try{
					if (date1.equals(date2)) {
						end = formatDay.parse(date2);
						start = DateUtils.addDays(end, -1);
						date1 = formatDay.format(start);
						date2 = formatDay.format(end);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				
			}
		 Map<String,Map<String,Map<String,Map<String,AmazonOperationalReport>>>>  map=amazonOperationReportService.compareAllCountry(amazonOperationalReport,date2,date1);
		 Map<String, Map<String,Map<String,AmazonOperationalReport>>> orderTypeMap=amazonOperationReportService.getSalesTypeByProduct(amazonOperationalReport.getSearchType(),date1,date2);
		 Map<String,Map<String,AmazonOperationalReport>> orderType1=Maps.newHashMap();
		 Map<String,Map<String,AmazonOperationalReport>> orderType2=Maps.newHashMap();
		 if(orderTypeMap!=null&&orderTypeMap.size()>0){
			 orderType1=orderTypeMap.get(date1);
			 orderType2=orderTypeMap.get(date2);
		 }
		
		 HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
			style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBottomBorderColor(HSSFColor.BLACK.index);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setLeftBorderColor(HSSFColor.BLACK.index);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setRightBorderColor(HSSFColor.BLACK.index);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setTopBorderColor(HSSFColor.BLACK.index);
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 10); // 字体高度
			font.setFontName(" 宋体 "); // 字体
			font.setBoldweight((short) 10);
			style.setFont(font);
			row.setHeight((short) 600);
			CellStyle contentStyle = wb.createCellStyle();
			contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
			contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
			HSSFCellStyle cellStyle = wb.createCellStyle();
	        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
	        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
	        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
	        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
	        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
	        
	    	HSSFCellStyle cellStyle2 = wb.createCellStyle();
	    	cellStyle2.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));
	    	cellStyle2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    	cellStyle2.setBottomBorderColor(HSSFColor.BLACK.index);
	    	cellStyle2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    	cellStyle2.setLeftBorderColor(HSSFColor.BLACK.index);
	    	cellStyle2.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    	cellStyle2.setRightBorderColor(HSSFColor.BLACK.index);
	    	cellStyle2.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    	cellStyle2.setTopBorderColor(HSSFColor.BLACK.index);
	        HSSFFont font1 = wb.createFont();
			font1.setColor(HSSFColor.RED.index);
			cellStyle2.setFont(font1);
			
			HSSFCellStyle cellStyle3 = wb.createCellStyle();
			cellStyle3.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));
	    	cellStyle3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	    	cellStyle3.setBottomBorderColor(HSSFColor.BLACK.index);
	    	cellStyle3.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	    	cellStyle3.setLeftBorderColor(HSSFColor.BLACK.index);
	    	cellStyle3.setBorderRight(HSSFCellStyle.BORDER_THIN);
	    	cellStyle3.setRightBorderColor(HSSFColor.BLACK.index);
	    	cellStyle3.setBorderTop(HSSFCellStyle.BORDER_THIN);
	    	cellStyle3.setTopBorderColor(HSSFColor.BLACK.index);
	    
			
	    	
	    	HSSFCellStyle cellStyle5 = wb.createCellStyle();
	        cellStyle5.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
	        cellStyle5.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	        cellStyle5.setBottomBorderColor(HSSFColor.BLACK.index);
	        cellStyle5.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	        cellStyle5.setLeftBorderColor(HSSFColor.BLACK.index);
	        cellStyle5.setBorderRight(HSSFCellStyle.BORDER_THIN);
	        cellStyle5.setRightBorderColor(HSSFColor.BLACK.index);
	        cellStyle5.setBorderTop(HSSFCellStyle.BORDER_THIN);
	        cellStyle5.setTopBorderColor(HSSFColor.BLACK.index);
	        
	        
			
			
			HSSFCell cell = null;
			List<String> title = Lists.newArrayList("平台","产品","类型","session","","","","转化率","","","","session订单","","销量","","","","单价","","","","销售额","","","","","","销售额变动原因分析",date1+" VS "+date2,"","");
			List<String> title2 = Lists.newArrayList("平台","产品","类型",date1,date2,"对比","增幅",date1,date2,"对比","增幅",date1,date2,date1,date2,"对比","增幅",date1,date2,"对比","增幅",date1,date2,"对比","减少","增加","增幅","销售额变动原因分析","大订单","促销","闪购");
			for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
		    }
			row = sheet.createRow(1);
			for (int i = 0; i < title2.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title2.get(i));
				cell.setCellStyle(style);

				sheet.autoSizeColumn((short) i);
		    }
			int rownum=2;// //国家/日期/产品类型/产品
			for (Map.Entry<String, Map<String, Map<String, Map<String, AmazonOperationalReport>>>> entry : map.entrySet()) { 
		        String country=entry.getKey();
				Map<String,Map<String,Map<String,AmazonOperationalReport>>> temp=entry.getValue();
				
					Map<String,Map<String,AmazonOperationalReport>> dateMap1=temp.get(date1);
					Map<String,Map<String,AmazonOperationalReport>> dateMap2=temp.get(date2);
					
					Set<String> typeSet=Sets.newHashSet();
					if(dateMap1!=null){
						typeSet.addAll(dateMap1.keySet());
					}
					if(dateMap2!=null){
						typeSet.addAll(dateMap2.keySet());
					}
					if(typeSet!=null){
						Integer allMaxOrder1=0;
		    			Integer allMaxOrder2=0;
		    			Integer allPromotionsOrder1=0;
		    			Integer allPromotionsOrder2=0;
		    			Integer allFlashSalesOrder1=0;
		    			Integer allFlashSalesOrder2=0;
						for (String type: typeSet) {
							Integer totalSession1=0;
							float totalConversionRate1=0f;
							Integer totalSalesVolume1=0;
							float totalUnitPrice1=0f;
							float totalSales1=0f;
							
							Integer totalSession2=0;
							float totalConversionRate2=0f;
							Integer totalSalesVolume2=0;
							float totalUnitPrice2=0f;
							float totalSales2=0f;
							
							Integer totalMaxOrder1=0;
			    			Integer totalMaxOrder2=0;
			    			Integer totalPromotionsOrder1=0;
			    			Integer totalPromotionsOrder2=0;
			    			Integer totalFlashSalesOrder1=0;
			    			Integer totalFlashSalesOrder2=0;
							
							if("total".equals(type)){
								continue;
							}
							//Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
							Set<String> nameSet=Sets.newHashSet();
							if(dateMap1!=null&&dateMap1.get(type)!=null){
								nameSet.addAll(dateMap1.get(type).keySet());
							}
							if(dateMap2!=null&&dateMap2.get(type)!=null){
								nameSet.addAll(dateMap2.get(type).keySet());
							}
							
							
							for (String name: nameSet) {
								Integer session1=0;
								float conversionRate1=0f;
								Integer salesVolume1=0;
								float unitPrice1=0f;
								float sales1=0f;
								
								Integer session2=0;
								float conversionRate2=0f;
								Integer salesVolume2=0;
								float unitPrice2=0f;
								float sales2=0f;
								
								if("total".equals(name)){
									continue;
								}
								int j=0;
								row=sheet.createRow(rownum++);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		

				    			float temp1=0f;
				    			float temp2=0f;
				    			float temp3=0f;
				    			float temp4=0f;
				    			float temp5=0f;
					    		
					    		
					    		if(dateMap2!=null&&dateMap2.get(type)!=null&&dateMap2.get(type).get(name)!=null){
                                    if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null){
                                    	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
                                    	session1=typeMap.get(name).getSession();
    						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session1);
    						    		row.getCell(j-1).setCellStyle(cellStyle5);
                                    }else{
                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
							    		row.getCell(j-1).setCellStyle(contentStyle);
                                    }
						    		
						    		AmazonOperationalReport report=dateMap2.get(type).get(name);
									session2=report.getSession();
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session2);
						    		row.getCell(j-1).setCellStyle(cellStyle5);
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session2-session1);
					    			row.getCell(j-1).setCellStyle(cellStyle5);
					    			if(session1!=0){
					    			    temp1=new BigDecimal((session2-session1)*1.0f/session1).setScale(2,4).floatValue();
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp1);
							    		row.getCell(j-1).setCellStyle(session2-session1<0?cellStyle2:cellStyle3);
						    		}else{
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
							    		row.getCell(j-1).setCellStyle(contentStyle);
						    		}
						    			
					    			
						    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null&&dateMap1.get(type).get(name).getSession()!=0){
	                                    	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                    	conversionRate1=typeMap.get(name).getSessionOrder()*1.0f/typeMap.get(name).getSession();
							    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate1);//转化率
	                                 }else{
	                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                 }
						    		row.getCell(j-1).setCellStyle(cellStyle3);
						    		
						    		
						    		if(report.getSession()!=0){
						    			conversionRate2=report.getSessionOrder()*1.0f/report.getSession();
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate2);//转化率
						    		}else{
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		}
						    		row.getCell(j-1).setCellStyle(cellStyle3);
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate2-conversionRate1);
					    			row.getCell(j-1).setCellStyle(cellStyle3);
					    			temp2=new BigDecimal(conversionRate2-conversionRate1).setScale(2, 4).floatValue();
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp2);
						    		row.getCell(j-1).setCellStyle(conversionRate2-conversionRate1<0?cellStyle2:cellStyle3);
					    			
						    		

						    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null){
	                                    	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(typeMap.get(name).getSessionOrder());
	    						    		row.getCell(j-1).setCellStyle(contentStyle);
	                                 }else{
	                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                    	row.getCell(j-1).setCellStyle(contentStyle);
	                                 }
						    		
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSessionOrder());
						    		row.getCell(j-1).setCellStyle(contentStyle);
					    			
						    		
						    		
						    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null){
	                                    	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                    	salesVolume1=typeMap.get(name).getSalesVolume();
	    						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume1);
	    						    		row.getCell(j-1).setCellStyle(contentStyle);
	                                 }else{
	                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                    	row.getCell(j-1).setCellStyle(contentStyle);
	                                 }
						    		
						    		salesVolume2=report.getSalesVolume();
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume2);
						    		row.getCell(j-1).setCellStyle(contentStyle);
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume2-salesVolume1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			if(salesVolume1!=0){
					    				temp3=new BigDecimal((salesVolume2-salesVolume1)*1.0f/salesVolume1).setScale(2, 4).floatValue();
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp3);
						    			row.getCell(j-1).setCellStyle(salesVolume2-salesVolume1<0?cellStyle2:cellStyle3);
					    			}else{
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    			row.getCell(j-1).setCellStyle(contentStyle);
					    			}
						    		
					    			
					    			 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null&&dateMap1.get(type).get(name).getSalesVolume()!=0){
	                                    	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                    	unitPrice1=typeMap.get(name).getSales()/typeMap.get(name).getSalesVolume();
							    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice1);
							    			row.getCell(j-1).setCellStyle(cellStyle);
	                                 }else{
	                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                    	row.getCell(j-1).setCellStyle(contentStyle);
	                                 }
					    			 
					    			if(report.getSalesVolume()!=0){
						    			unitPrice2=report.getSales()/report.getSalesVolume();
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice2);
						    			row.getCell(j-1).setCellStyle(cellStyle);
						    		}else{
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    			row.getCell(j-1).setCellStyle(contentStyle);
						    		}
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice2-unitPrice1);
					    			row.getCell(j-1).setCellStyle(cellStyle);
					    			if(unitPrice1!=0){
					    				temp4=new BigDecimal((unitPrice2-unitPrice1)*1.0f/unitPrice1).setScale(2, 4).floatValue();
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp4);
						    			row.getCell(j-1).setCellStyle(unitPrice2-unitPrice1<0?cellStyle2:cellStyle3);
					    			}else{
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    			row.getCell(j-1).setCellStyle(contentStyle);
					    			}
					    			
					    			
					    			
						    		

					    			 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null){
	                                    	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                    	sales1=typeMap.get(name).getSales();
	    						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales1);
	    						    		row.getCell(j-1).setCellStyle(cellStyle5);
	                                 }else{
	                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                    	row.getCell(j-1).setCellStyle(contentStyle);
	                                 }
						    		
						    		sales2=report.getSales();
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2);
						    		row.getCell(j-1).setCellStyle(cellStyle5);
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
					    			row.getCell(j-1).setCellStyle(cellStyle5);
						    			
					    			if(sales2-sales1<=0){
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			}else{
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
					    			}
					    			row.getCell(j-2).setCellStyle(cellStyle5);
					    			row.getCell(j-1).setCellStyle(cellStyle5);
						    	

					    			if(sales1!=0){
					    				temp5=new BigDecimal((sales2-sales1)*1.0f/sales1).setScale(2, 4).floatValue();
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp5);
						    			row.getCell(j-1).setCellStyle(sales2-sales1<0?cellStyle2:cellStyle3);
					    			}else{
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    			row.getCell(j-1).setCellStyle(contentStyle);
					    			}
					    			
					    		}else{

						    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null){
	                                    	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                    	session1=typeMap.get(name).getSession();
	    						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session1);
	    						    		row.getCell(j-1).setCellStyle(cellStyle5);
	                                 }else{
	                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                    	row.getCell(j-1).setCellStyle(contentStyle);
	                                 }
						    		
						    		
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		row.getCell(j-1).setCellStyle(cellStyle5);
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(cellStyle5);
					    			
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
							    	row.getCell(j-1).setCellStyle(contentStyle);
						    		
						    			
							    	 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null&&dateMap1.get(type).get(name).getSession()!=0){
	                                    	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                    	conversionRate1=typeMap.get(name).getSessionOrder()*1.0f/typeMap.get(name).getSession();
							    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate1);//转化率
	                                 }else{
	                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                 }
						    		row.getCell(j-1).setCellStyle(cellStyle3);
						    		
						    		
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		row.getCell(j-1).setCellStyle(cellStyle3);
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(cellStyle3);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		row.getCell(j-1).setCellStyle(cellStyle3);
					    			
						    		
						    		
						    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null){
	                                    	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(typeMap.get(name).getSessionOrder());
	    						    		row.getCell(j-1).setCellStyle(contentStyle);
	                                 }else{
	                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                    	row.getCell(j-1).setCellStyle(contentStyle);
	                                 }
						    		
						    		
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		row.getCell(j-1).setCellStyle(contentStyle);
					    			
						    		
						    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null){
	                                    	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                    	salesVolume1=typeMap.get(name).getSalesVolume();
	    						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume1);
	    						    		row.getCell(j-1).setCellStyle(contentStyle);
	                                 }else{
	                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                    	row.getCell(j-1).setCellStyle(contentStyle);
	                                 }
						    		
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		row.getCell(j-1).setCellStyle(contentStyle);
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		row.getCell(j-1).setCellStyle(contentStyle);
						    		
						    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null&&dateMap1.get(type).get(name).getSalesVolume()!=0){
	                                    	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                    	unitPrice1=typeMap.get(name).getSales()/typeMap.get(name).getSalesVolume();
							    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice1);
							    			row.getCell(j-1).setCellStyle(cellStyle);
	                                 }else{
	                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                    	row.getCell(j-1).setCellStyle(contentStyle);
	                                 }
						    		
						    	
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		row.getCell(j-1).setCellStyle(contentStyle);
						    		
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(cellStyle);
					    			
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		row.getCell(j-1).setCellStyle(contentStyle);
					    			
					    			
					    		
						    		if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get(name)!=null){
                                    	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
                                    	sales1=typeMap.get(name).getSales();
    						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales1);
    						    		row.getCell(j-1).setCellStyle(cellStyle5);
                                    }else{
                                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
                                    	row.getCell(j-1).setCellStyle(contentStyle);
                                    }
						    		
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		row.getCell(j-1).setCellStyle(cellStyle5);
						    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(cellStyle5);
						    			
					    			
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			
					    			row.getCell(j-2).setCellStyle(cellStyle5);
					    			row.getCell(j-1).setCellStyle(cellStyle5);
						    	

					    			
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    		row.getCell(j-1).setCellStyle(contentStyle);
					    			
					    		}
					    		

					    			String info="";
					    			if(temp1<0){
					    				info+="session降低"+new BigDecimal(temp1*100).setScale(2, 4).floatValue()+"%;";
					    			}else if(temp1>0){
					    				info+="session上涨"+new BigDecimal(temp1*100).setScale(2, 4).floatValue()+"%;";
					    			}
					    			if(temp2<0){
					    				info+=" 转化率降低"+new BigDecimal(temp2*100).setScale(2, 4).floatValue()+"%;";
					    			}else if(temp2>0){
					    				info+=" 转化率上涨"+new BigDecimal(temp2*100).setScale(2, 4).floatValue()+"%;";
					    			}
					    			
					    			if(temp4<0){
					    				info+="单价降低"+new BigDecimal(temp4*100).setScale(2, 4).floatValue()+"%;";
					    			}else if(temp4>0){
					    				info+="单价上涨"+new BigDecimal(temp4*100).setScale(2, 4).floatValue()+"%;";
					    			}
					    			
					    			Integer maxOrder1=0;
					    			Integer maxOrder2=0;
					    			Integer promotionsOrder1=0;
					    			Integer promotionsOrder2=0;
					    			Integer flashSalesOrder1=0;
					    			Integer flashSalesOrder2=0;
					    			
					    			if(orderType1!=null&&orderType1.get(country)!=null&&orderType1.get(country).get(name)!=null){
					    				AmazonOperationalReport rp=orderType1.get(country).get(name);
					    				info+=date1+":";
					    				if(rp.getPromotionsOrder()!=null&&rp.getPromotionsOrder()!=0){
					    					info+="促销,"+rp.getPromotionsOrder()+";";
					    					promotionsOrder1=rp.getPromotionsOrder();
					    					totalPromotionsOrder1+=rp.getPromotionsOrder();
					    					allPromotionsOrder1+=rp.getPromotionsOrder();
					    				}
					    				if(rp.getFlashSalesOrder()!=null&&rp.getFlashSalesOrder()!=0){
					    					info+="闪购,"+rp.getFlashSalesOrder()+";";
					    					flashSalesOrder1=rp.getFlashSalesOrder();
					    					totalFlashSalesOrder1+=rp.getFlashSalesOrder();
					    					allFlashSalesOrder1+=rp.getFlashSalesOrder();
					    				}
					    				if(rp.getMaxOrder()!=null&&rp.getMaxOrder()!=0){
					    					info+="大订单,"+rp.getMaxOrder()+";";
					    					maxOrder1=rp.getMaxOrder();
					    					totalMaxOrder1+=rp.getMaxOrder();
					    					allMaxOrder1+=rp.getMaxOrder();
					    				}
					    			}
					    			if(orderType2!=null&&orderType2.get(country)!=null&&orderType2.get(country).get(name)!=null){
					    				AmazonOperationalReport rp=orderType2.get(country).get(name);
					    				info+=date2+":";
					    				if(rp.getPromotionsOrder()!=null&&rp.getPromotionsOrder()!=0){
					    					info+="促销,"+rp.getPromotionsOrder()+";";
					    					promotionsOrder2=rp.getPromotionsOrder();
					    					totalPromotionsOrder2+=rp.getPromotionsOrder();
					    					allPromotionsOrder2+=rp.getPromotionsOrder();
					    				}
					    				if(rp.getFlashSalesOrder()!=null&&rp.getFlashSalesOrder()!=0){
					    					info+="闪购,"+rp.getFlashSalesOrder()+";";
					    					flashSalesOrder2=rp.getFlashSalesOrder();
					    					totalFlashSalesOrder2+=rp.getFlashSalesOrder();
					    					allFlashSalesOrder2+=rp.getFlashSalesOrder();
					    				}
					    				if(rp.getMaxOrder()!=null&&rp.getMaxOrder()!=0){
					    					info+="大订单,"+rp.getMaxOrder()+";";
					    					maxOrder2=rp.getMaxOrder();
					    					totalMaxOrder2+=rp.getPromotionsOrder();
					    					allMaxOrder2+=rp.getMaxOrder();
					    				}
					    			}
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(info);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(maxOrder2-maxOrder1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(promotionsOrder2-promotionsOrder1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(flashSalesOrder2-flashSalesOrder1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			
							}
							//total
							int j=0;
							row=sheet.createRow(rownum++);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("总计");
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
				    		row.getCell(j-1).setCellStyle(contentStyle);

				    		float temp1=0f;
			    			float temp2=0f;
			    			float temp3=0f;
			    			float temp4=0f;
			    			float temp5=0f;
				    		if(dateMap2!=null&&dateMap2.get(type)!=null&&dateMap2.get(type).get("total")!=null){
					    		if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null){
                                	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
                                	totalSession1=typeMap.get("total").getSession();
    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSession1);
    					    		row.getCell(j-1).setCellStyle(cellStyle5);
    					    		
                                }else{
                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
                                	row.getCell(j-1).setCellStyle(contentStyle);
                                }
					    		
					    		AmazonOperationalReport report=dateMap2.get(type).get("total");
				    			totalSession2=report.getSession();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSession2);
					    		row.getCell(j-1).setCellStyle(cellStyle5);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSession2-totalSession1);
				    			row.getCell(j-1).setCellStyle(cellStyle5);
				    			 if(totalSession1!=0){
				    				 temp1=new BigDecimal((totalSession2-totalSession1)*1.0f/totalSession1).setScale(2, 4).floatValue();
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp1);
						    			row.getCell(j-1).setCellStyle(totalSession2-totalSession1<0?cellStyle2:cellStyle3);
					    			}else{
					    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    			row.getCell(j-1).setCellStyle(contentStyle);
					    			}
					    		
				    			 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null&&dateMap1.get(type).get("total").getSession()!=0){
	                                	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                	totalConversionRate1=typeMap.get("total").getSessionOrder()*1.0f/typeMap.get("total").getSession();
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalConversionRate1);//转化率
	    					    		
	                             }else{
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                	row.getCell(j-1).setCellStyle(contentStyle);
	                            }
					    		row.getCell(j-1).setCellStyle(cellStyle3);
					    		
					    		
					    		if(report.getSession()!=0){
					    			totalConversionRate2=report.getSessionOrder()*1.0f/report.getSession();
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalConversionRate2);//转化率
					    		}else{
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		}
					    		row.getCell(j-1).setCellStyle(cellStyle3);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalConversionRate2-totalConversionRate1);
				    			row.getCell(j-1).setCellStyle(cellStyle3);
				    			temp2=new BigDecimal(totalConversionRate2-totalConversionRate1).setScale(2, 4).floatValue();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp2);
					    		row.getCell(j-1).setCellStyle(totalConversionRate2-totalConversionRate1<0?cellStyle2:cellStyle3);
					    		

					    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null){
	                                	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(typeMap.get("total").getSessionOrder());
	    					    		row.getCell(j-1).setCellStyle(contentStyle);
	                             }else{
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                	row.getCell(j-1).setCellStyle(contentStyle);
	                            }
					    		
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSessionOrder());
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		
					    		
					    		
					    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null){
	                                	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                	totalSalesVolume1=typeMap.get("total").getSalesVolume();
	    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSalesVolume1);
	                             }else{
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                	row.getCell(j-1).setCellStyle(contentStyle);
	                            }
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		
					    		
					    		totalSalesVolume2=report.getSalesVolume();
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSalesVolume2);
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSalesVolume2-totalSalesVolume1);
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			if(totalSalesVolume1!=0){
				    				temp3=new BigDecimal((totalSalesVolume2-totalSalesVolume1)*1.0f/totalSalesVolume1).setScale(2, 4).floatValue();
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp3);
					    			row.getCell(j-1).setCellStyle(totalSalesVolume2-totalSalesVolume1<0?cellStyle2:cellStyle3);
				    			}else{
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
				    			}
				    			
				    		
				    			
				    			 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null&&dateMap1.get(type).get("total").getSalesVolume()!=0){
	                                	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                	totalUnitPrice1=typeMap.get("total").getSales()/typeMap.get("total").getSalesVolume();
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalUnitPrice1);
						    			row.getCell(j-1).setCellStyle(cellStyle);
	                             }else{
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                	row.getCell(j-1).setCellStyle(contentStyle);
	                            }
				    			
				    			
				    			if(report.getSalesVolume()!=0){
					    			totalUnitPrice2=report.getSales()/report.getSalesVolume();
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalUnitPrice2);
					    			row.getCell(j-1).setCellStyle(cellStyle);
					    		}else{
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    		}
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalUnitPrice2-totalUnitPrice1);
				    			row.getCell(j-1).setCellStyle(cellStyle);
				    			if(totalUnitPrice1!=0){
				    				temp4=new BigDecimal((totalUnitPrice2-totalUnitPrice1)/totalUnitPrice1).setScale(2, 4).floatValue();
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp4);
					    			row.getCell(j-1).setCellStyle(totalUnitPrice2-totalUnitPrice1<0?cellStyle2:cellStyle3);
				    			}else{
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
				    			}
					    		

					    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null){
	                                	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                	totalSales1=typeMap.get("total").getSales();
	    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales1);
	    					    		row.getCell(j-1).setCellStyle(cellStyle5);
	                             }else{
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                	row.getCell(j-1).setCellStyle(contentStyle);
	                            }
				    			
					    		
					    		totalSales2=report.getSales();
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales2);
					    		row.getCell(j-1).setCellStyle(cellStyle5);
					    		if(totalSales2-totalSales1<=0){
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales2-totalSales1);
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			}else{
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales2-totalSales1);
				    			}
					    		row.getCell(j-2).setCellStyle(cellStyle5);
				    			row.getCell(j-1).setCellStyle(cellStyle5);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales2-totalSales1);
				    			row.getCell(j-1).setCellStyle(cellStyle5);
				    			
				    			if(totalSales1!=0){
				    				temp5=new BigDecimal((totalSales2-totalSales1)*1.0f/totalSales1).setScale(2, 4).floatValue();
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp5);
					    			row.getCell(j-1).setCellStyle(totalSales2-totalSales1<0?cellStyle2:cellStyle3);
				    			}else{
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    			row.getCell(j-1).setCellStyle(contentStyle);
				    			}

				    		}else{
				    			
					    		
					    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null){
	                                	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                	totalSession1=typeMap.get("total").getSession();
	    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSession1);
	    					    		row.getCell(j-1).setCellStyle(cellStyle5);
	                             }else{
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                	row.getCell(j-1).setCellStyle(contentStyle);
	                            }
					    		
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		row.getCell(j-1).setCellStyle(cellStyle5);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(cellStyle5);
				    			 
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						    	row.getCell(j-1).setCellStyle(contentStyle);
					    		
					    		
					    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null&&dateMap1.get(type).get("total").getSession()!=0){
	                                	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                	totalConversionRate1=typeMap.get("total").getSessionOrder()*1.0f/typeMap.get("total").getSession();
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalConversionRate1);//转化率
						    			row.getCell(j-1).setCellStyle(cellStyle3);
	                             }else{
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                	row.getCell(j-1).setCellStyle(cellStyle3);
	                            }
					    		 
					    		 
					    		
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		
					    		row.getCell(j-1).setCellStyle(cellStyle3);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(cellStyle3);
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		row.getCell(j-1).setCellStyle(cellStyle3);
					    		

					    		

					    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null){
	                                	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(typeMap.get("total").getSessionOrder());
	    					    		row.getCell(j-1).setCellStyle(contentStyle);
	    					    		
	                             }else{
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                	row.getCell(j-1).setCellStyle(contentStyle);
	                            }
					    		
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		
					    		
					    	
					    		

					    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null){
	                                	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                	
	    					    		totalSalesVolume1=typeMap.get("total").getSalesVolume();
	    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSalesVolume1);
	    					    		row.getCell(j-1).setCellStyle(contentStyle);
	    					    		
	                             }else{
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                	row.getCell(j-1).setCellStyle(contentStyle);
	                            }
					    		
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		row.getCell(j-1).setCellStyle(contentStyle);
				    			
				    			
				    			
				    			
				    			 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null&&dateMap1.get(type).get("total").getSalesVolume()!=0){
	                                	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                	totalUnitPrice1=typeMap.get("total").getSales()/typeMap.get("total").getSalesVolume();
						    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalUnitPrice1);
						    			row.getCell(j-1).setCellStyle(cellStyle);
	    					    		
	                             }else{
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                	row.getCell(j-1).setCellStyle(contentStyle);
	                            }
				    			
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		row.getCell(j-1).setCellStyle(contentStyle);
					    		
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(cellStyle);
				    			
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		row.getCell(j-1).setCellStyle(contentStyle);
				    			
					    		

					    	
					    	
					    		 if(dateMap1!=null&&dateMap1.get(type)!=null&&dateMap1.get(type).get("total")!=null){
	                                	Map<String,AmazonOperationalReport> typeMap=dateMap1.get(type);
	                                	totalSales1=typeMap.get("total").getSales();
	    					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSales1);
	    					    		row.getCell(j-1).setCellStyle(cellStyle5);
	    					    		
	                             }else{
	                                	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	                                	row.getCell(j-1).setCellStyle(contentStyle);
	                            }
				    			
					    		
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		row.getCell(j-1).setCellStyle(cellStyle5);
					    		
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			
					    		row.getCell(j-2).setCellStyle(cellStyle5);
				    			row.getCell(j-1).setCellStyle(cellStyle5);
					    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(cellStyle5);
				    			
				    			
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					    		row.getCell(j-1).setCellStyle(contentStyle);
				    			
				    		}
				    		

					    			String info="";
					    			if(temp1<0){
					    				info+="session降低"+new BigDecimal(temp1*100).setScale(2, 4).floatValue()+"%;";
					    			}else if(temp1>0){
					    				info+="session上涨"+new BigDecimal(temp1*100).setScale(2, 4).floatValue()+"%;";
					    			}
					    			if(temp2<0){
					    				info+=" 转化率降低"+new BigDecimal(temp2*100).setScale(2, 4).floatValue()+"%;";
					    			}else if(temp2>0){
					    				info+=" 转化率上涨"+new BigDecimal(temp2*100).setScale(2, 4).floatValue()+"%;";
					    			}
					    			
					    			if(temp4<0){
					    				info+="单价降低"+new BigDecimal(temp4*100).setScale(2, 4).floatValue()+"%;";
					    			}else if(temp4>0){
					    				info+="单价上涨"+new BigDecimal(temp4*100).setScale(2, 4).floatValue()+"%;";
					    			}
					    			
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(info);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalMaxOrder2-totalMaxOrder1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalPromotionsOrder2-totalPromotionsOrder1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
					    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalFlashSalesOrder2-totalFlashSalesOrder1);
					    			row.getCell(j-1).setCellStyle(contentStyle);
						}
						
						Integer session1=0;
						float conversionRate1=0f;
						Integer salesVolume1=0;
						float unitPrice1=0f;
						float sales1=0f;
						
						Integer session2=0;
						float conversionRate2=0f;
						Integer salesVolume2=0;
						float unitPrice2=0f;
						float sales2=0f;
						
						
						int j=0;
						row=sheet.createRow(rownum++);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("总计");
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("总计");
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		

		    			float temp1=0f;
		    			float temp2=0f;
		    			float temp3=0f;
		    			float temp4=0f;
		    			float temp5=0f;
			    		
			    		if(dateMap2!=null&&dateMap2.get("total")!=null&&dateMap2.get("total").get("total")!=null){
			    			AmazonOperationalReport amazonReport=dateMap1.get("total").get("total");
				    		session1=amazonReport.getSession();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session1);
				    		row.getCell(j-1).setCellStyle(cellStyle5);
				    		AmazonOperationalReport report=dateMap2.get("total").get("total");
			    			session2=report.getSession();
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session2);
				    		row.getCell(j-1).setCellStyle(cellStyle5);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session2-session1);
			    			row.getCell(j-1).setCellStyle(cellStyle5);
			    			if(session1!=0){
		    			    	temp1=new BigDecimal((session2-session1)*1.0f/session1).setScale(2,4).floatValue();
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp1);
				    			row.getCell(j-1).setCellStyle(session2-session1<0?cellStyle2:cellStyle3);
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
				    		
				    		
				    		if(amazonReport.getSession()!=0){
				    			conversionRate1=amazonReport.getSessionOrder()*1.0f/amazonReport.getSession();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate1);//转化率
				    		}else{
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		}
				    		row.getCell(j-1).setCellStyle(cellStyle3);
				    		if(report.getSession()!=0){
				    			conversionRate2=report.getSessionOrder()*1.0f/report.getSession();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate2);//转化率
				    		}else{
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		}
				    		row.getCell(j-1).setCellStyle(cellStyle3);
				    		
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate2-conversionRate1);
			    			row.getCell(j-1).setCellStyle(cellStyle3);
			    			temp2=new BigDecimal(conversionRate2-conversionRate1).setScale(2, 4).floatValue();
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp2);
				    		row.getCell(j-1).setCellStyle(conversionRate2-conversionRate1<0?cellStyle2:cellStyle3);
			    			
				    		
				    		
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(amazonReport.getSessionOrder());
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSessionOrder());
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		
				    		
				    		
				    		
				    		
				    		salesVolume1=amazonReport.getSalesVolume();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume1);
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		salesVolume2=report.getSalesVolume();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume2);
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume2-salesVolume1);
			    			row.getCell(j-1).setCellStyle(contentStyle);

			    			if(salesVolume1!=0){
			    				temp3=new BigDecimal((salesVolume2-salesVolume1)*1.0f/salesVolume1).setScale(2, 4).floatValue();
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp3);
				    			row.getCell(j-1).setCellStyle(salesVolume2-salesVolume1<0?cellStyle2:cellStyle3);
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    			
			    			if(amazonReport.getSalesVolume()!=0){
				    			unitPrice1=amazonReport.getSales()/amazonReport.getSalesVolume();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice1);
				    			row.getCell(j-1).setCellStyle(cellStyle);
				    		}else{
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    		}
			    			if(report.getSalesVolume()!=0){
				    			unitPrice2=report.getSales()/report.getSalesVolume();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice2);
				    			row.getCell(j-1).setCellStyle(cellStyle);
				    		}else{
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    		}
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice2-unitPrice1);
			    			row.getCell(j-1).setCellStyle(cellStyle);
			    			if(unitPrice1!=0){
			    				temp4=new BigDecimal((unitPrice2-unitPrice1)*1.0f/unitPrice1).setScale(2, 4).floatValue();
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp4);
				    			row.getCell(j-1).setCellStyle(unitPrice2-unitPrice1<0?cellStyle2:cellStyle3);
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    			

				    		sales1=amazonReport.getSales();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales1);
				    		row.getCell(j-1).setCellStyle(cellStyle5);
			    			
				    		sales2=report.getSales();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2);
				    		row.getCell(j-1).setCellStyle(cellStyle5);

			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
			    			row.getCell(j-1).setCellStyle(cellStyle5);
			    			
			    			if(sales2-sales1<=0){
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
			    			}
			    			row.getCell(j-2).setCellStyle(cellStyle5);
			    			row.getCell(j-1).setCellStyle(cellStyle5);

			    			if(sales1!=0){
			    				temp5=new BigDecimal((sales2-sales1)*1.0f/sales1).setScale(2, 4).floatValue();
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp5);
				    			row.getCell(j-1).setCellStyle(sales2-sales1<0?cellStyle2:cellStyle3);
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    			
			    		}else{
			    			AmazonOperationalReport amazonReport=dateMap1.get("total").get("total");
				    		session1=amazonReport.getSession();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session1);
				    		row.getCell(j-1).setCellStyle(cellStyle5);
				    		
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		row.getCell(j-1).setCellStyle(cellStyle5);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(cellStyle5);
			    			
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		row.getCell(j-1).setCellStyle(contentStyle);
			    			
				    		
				    		
				    		if(amazonReport.getSession()!=0){
				    			conversionRate1=amazonReport.getSessionOrder()*1.0f/amazonReport.getSession();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(conversionRate1);//转化率
				    		}else{
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		}
				    		row.getCell(j-1).setCellStyle(cellStyle3);
				    		
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		
				    		row.getCell(j-1).setCellStyle(cellStyle3);
				    		
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(cellStyle3);
			    		
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		row.getCell(j-1).setCellStyle(cellStyle3);
			    			
				    		
				    		
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(amazonReport.getSessionOrder());
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		
				    		
				    		
				    		
				    		
				    		salesVolume1=amazonReport.getSalesVolume();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume1);
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    	
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);

			    		
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		row.getCell(j-1).setCellStyle(contentStyle);
			    			
			    			
			    			if(amazonReport.getSalesVolume()!=0){
				    			unitPrice1=amazonReport.getSales()/amazonReport.getSalesVolume();
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unitPrice1);
				    			row.getCell(j-1).setCellStyle(cellStyle);
				    		}else{
				    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    		}
			    			
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		row.getCell(j-1).setCellStyle(contentStyle);
				    		
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(cellStyle);
			    			
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		row.getCell(j-1).setCellStyle(contentStyle);
			    			
			    			

				    		sales1=amazonReport.getSales();
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales1);
				    		row.getCell(j-1).setCellStyle(cellStyle5);
			    			
				    	
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		row.getCell(j-1).setCellStyle(cellStyle5);

			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(cellStyle5);
			    			
			    			
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales2-sales1);
			    			
			    			row.getCell(j-2).setCellStyle(cellStyle5);
			    			row.getCell(j-1).setCellStyle(cellStyle5);

			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    		row.getCell(j-1).setCellStyle(contentStyle);
			    			
			    		}

			    			String info="";
			    			if(temp1<0){
			    				info+="session降低"+new BigDecimal(temp1*100).setScale(2, 4).floatValue()+"%;";
			    			}else if(temp1>0){
			    				info+="session上涨"+new BigDecimal(temp1*100).setScale(2, 4).floatValue()+"%;";
			    			}
			    			if(temp2<0){
			    				info+=" 转化率降低"+new BigDecimal(temp2*100).setScale(2, 4).floatValue()+"%;";
			    			}else if(temp2>0){
			    				info+=" 转化率上涨"+new BigDecimal(temp2*100).setScale(2, 4).floatValue()+"%;";
			    			}
			    			
			    			if(temp4<0){
			    				info+="单价降低"+new BigDecimal(temp4*100).setScale(2, 4).floatValue()+"%;";
			    			}else if(temp4>0){
			    				info+="单价上涨"+new BigDecimal(temp4*100).setScale(2, 4).floatValue()+"%;";
			    			}

			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(info);
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(allMaxOrder2-allMaxOrder1);
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(allPromotionsOrder2-allPromotionsOrder1);
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(allFlashSalesOrder2-allFlashSalesOrder1);
			    			row.getCell(j-1).setCellStyle(contentStyle);
					}
					

				
			}
			for (int i = 0; i < title.size(); i++) {
		   		   sheet.autoSizeColumn((short)i,true);
			}
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	            String fileName ="运营数据对比统计2" + sdf.format(new Date());
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName+".xls");
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
	 }	 
    
    
    @RequestMapping(value = "byTimeExport2")
	public String byTimeExport2(SaleReport saleReport,HttpServletRequest request, HttpServletResponse response, Model model) {
    	AmazonOperationalReport amazonOperationalReport=new AmazonOperationalReport();
    	if(saleReport.getStart()!=null){
    		amazonOperationalReport.setEndDate(saleReport.getEnd());
			amazonOperationalReport.setCreateDate(saleReport.getStart());
    	}
    	//1 d 2 w 3 m
    	amazonOperationalReport.setSearchType(saleReport.getSearchType());
    	if(amazonOperationalReport.getEndDate()==null){
			amazonOperationalReport.setEndDate(new Date());
			amazonOperationalReport.setCreateDate(DateUtils.addMonths(new Date(),-1));
		}
    	Map<String,Map<String,Map<String,Map<String,AmazonOperationalReport>>>> map=amazonOperationReportService.findAllCountry(amazonOperationalReport);
    	Map<String, Map<String,Map<String,AmazonOperationalReport>>> orderTypeMap=amazonOperationReportService.getOrderType(amazonOperationalReport);
    	
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
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
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCell cell = null;
		List<String> title = Lists.newArrayList("产品","平台","时间("+("1".equals(amazonOperationalReport.getSearchType())?"日":("2".equals(amazonOperationalReport.getSearchType())?"周":"月"))+")","类型","session","转化率","session订单","销量","总订单量","退货数","退货率","单价","销售额","总评价","差评","差评率","大订单","促销","闪购");

		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
	    }
		int rownum=1;
		 for (Map.Entry<String, Map<String, Map<String, Map<String, AmazonOperationalReport>>>> entry : map.entrySet()) { 
		    String country=entry.getKey();
			Map<String,Map<String,Map<String,AmazonOperationalReport>>> temp=entry.getValue();
			for (Map.Entry<String, Map<String, Map<String, AmazonOperationalReport>>> entryTemp : temp.entrySet()) { 
			    String date=entryTemp.getKey();
				Map<String,Map<String,AmazonOperationalReport>> dateMap=entryTemp.getValue();
				for (Map.Entry<String, Map<String, AmazonOperationalReport>> entryDate : dateMap.entrySet()) { 
				    String type=entryDate.getKey();
					Map<String,AmazonOperationalReport> typeMap=entryDate.getValue();
					Integer session=0;
					Integer salesVolume=0;
					Integer returnVolume=0;
					Integer orderVolume=0;
					Integer sessionOrder=0;
					float sales=0f;
					Integer badReview=0;
					Integer totalReview=0;
					
					Integer maxOrder=0;
					Integer flashOrder=0;
					Integer promotionsOrder=0;
					for (Map.Entry<String, AmazonOperationalReport> entryType: typeMap.entrySet()) { 
					    String name=entryType.getKey();
					    AmazonOperationalReport report=entryType.getValue();
						int j=0;
						row=sheet.createRow(rownum++);
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSession());
			    		session+=report.getSession();
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		
			    		if(typeMap.get(name).getSession()!=0){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSessionOrder()*100f/report.getSession());//转化率
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//
			    		}
			    		row.getCell(j-1).setCellStyle(cellStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSessionOrder());
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		sessionOrder+=report.getSessionOrder();
			    		
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSalesVolume());
			    		salesVolume+=report.getSalesVolume();
			    	
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getOrderVolume());
			    		orderVolume+=report.getOrderVolume();
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getReturnVolume());
			    		returnVolume+=report.getReturnVolume();
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		if(report.getOrderVolume()!=0){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getReturnVolume()*100f/report.getOrderVolume());//退货率
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//退货率
			    		}
			    		
			    		row.getCell(j-1).setCellStyle(cellStyle);
			    		
			    		if(report.getSalesVolume()!=0){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSales()/report.getSalesVolume());
			    			row.getCell(j-1).setCellStyle(cellStyle);
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.getCell(j-1).setCellStyle(contentStyle);
			    		}
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSales());
			    		sales+=report.getSales();
			    		row.getCell(j-1).setCellStyle(cellStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getTotalReview());
			    		totalReview+=report.getTotalReview();
			    		badReview+=report.getBadReview();
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getBadReview());
			    		row.getCell(j-1).setCellStyle(contentStyle);
			    		if(report.getTotalReview()!=0){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getBadReview()*100f/report.getTotalReview());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//退货率
			    		}
			    		row.getCell(j-1).setCellStyle(cellStyle);//
			    		
			    		if(orderTypeMap!=null&&orderTypeMap.get(date)!=null&&orderTypeMap.get(date).get(country)!=null&&orderTypeMap.get(date).get(country).get(name)!=null){
			    			AmazonOperationalReport rp=orderTypeMap.get(date).get(country).get(name);
			    			if(rp.getMaxOrder()!=null&&rp.getMaxOrder()!=0){
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rp.getMaxOrder());//max
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			if(maxOrder<rp.getMaxOrder()){
				    				maxOrder=rp.getMaxOrder();
				    			}
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    			if(rp.getPromotionsOrder()!=null&&rp.getPromotionsOrder()!=0){
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rp.getPromotionsOrder());//max
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			promotionsOrder+=rp.getPromotionsOrder();
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    			if(rp.getFlashSalesOrder()!=null&&rp.getFlashSalesOrder()!=0){
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rp.getFlashSalesOrder());//max
				    			row.getCell(j-1).setCellStyle(contentStyle);
				    			flashOrder+=rp.getFlashSalesOrder();
			    			}else{
			    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
				    			row.getCell(j-1).setCellStyle(contentStyle);
			    			}
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
			    			row.getCell(j-1).setCellStyle(cellStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//pro
			    			row.getCell(j-1).setCellStyle(cellStyle);
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//flash
			    			row.getCell(j-1).setCellStyle(cellStyle);
			    		}
					}
					
					int j=0;
					row=sheet.createRow(rownum++);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(session);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		
		    		if(session!=0){
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sessionOrder*100f/session);//转化率
		    		}else{
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    		}
		    		row.getCell(j-1).setCellStyle(cellStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sessionOrder);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		
		    		
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);

		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(orderVolume);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(returnVolume);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		if(orderVolume!=0){
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(returnVolume*100f/orderVolume);//退货率
		    		}else{
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//退货率
		    		}
		    		
		    		row.getCell(j-1).setCellStyle(cellStyle);
		    		
		    		if(salesVolume!=0){
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales/salesVolume);
		    			row.getCell(j-1).setCellStyle(cellStyle);
		    		}else{
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    			row.getCell(j-1).setCellStyle(contentStyle);
		    		}
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales);
		
		    		row.getCell(j-1).setCellStyle(cellStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalReview);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(badReview);
		    		row.getCell(j-1).setCellStyle(contentStyle);
		    		if(totalReview!=0){
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(badReview*100f/totalReview);
		    		}else{
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//退货率
		    		}
		    		row.getCell(j-1).setCellStyle(cellStyle);//
		    		
		    		if(maxOrder!=0){
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(maxOrder);//max
		    			row.getCell(j-1).setCellStyle(contentStyle);
	    			}else{
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    			row.getCell(j-1).setCellStyle(contentStyle);
	    			}
	    			if(promotionsOrder!=0){
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(promotionsOrder);//max
		    			row.getCell(j-1).setCellStyle(contentStyle);
	    			}else{
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    			row.getCell(j-1).setCellStyle(contentStyle);
	    			}
	    			if(flashOrder!=0){
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(flashOrder);//max
		    			row.getCell(j-1).setCellStyle(contentStyle);
	    			}else{
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");//max
		    			row.getCell(j-1).setCellStyle(contentStyle);
	    			}
				}
				
			}
			
		}
		
		for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
            String fileName ="运营数据统计" + sdf.format(new Date());
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName+".xls");
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    
    
    @RequestMapping(value = "exportSpreadReportList")
	public String exportSpreadReportList(AmazonSpreadReport amazonSpreadReport,HttpServletRequest request, HttpServletResponse response, Model model) {
		if(amazonSpreadReport.getCreateDate()==null){
			amazonSpreadReport.setEndDate(DateUtils.addDays(new Date(),-4));
			amazonSpreadReport.setCreateDate(DateUtils.addDays(new Date(),-19));
		}
		if(StringUtils.isBlank(amazonSpreadReport.getCountry())){
			amazonSpreadReport.setCountry("de");
		}
		Map<String,String> nameAndLineMap=groupDictService.getLineNameByName();//产品名-产品线
		List<AmazonSpreadReport> reportList=amazonOperationReportService.findAvgSpread(amazonSpreadReport);
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
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
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCell cell = null;
		List<String> title = Lists.newArrayList("Product Name","Product Line","Asin","Sku","Price","Cost","Profit","Daily Session","Daily Order","Daily Ps","Daily Revenue","CR(%)");

		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
	    }
		int rownum=1;
		for (AmazonSpreadReport report : reportList) {
			row=sheet.createRow(rownum++);
			int j=0;
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getProductName());
    		row.getCell(j-1).setCellStyle(contentStyle);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(nameAndLineMap.get(report.getProductName())==null?"":nameAndLineMap.get(report.getProductName()));
    		row.getCell(j-1).setCellStyle(contentStyle);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAsin());
    		row.getCell(j-1).setCellStyle(contentStyle);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSku());
    		row.getCell(j-1).setCellStyle(contentStyle);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getPrice());
    		row.getCell(j-1).setCellStyle(cellStyle);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getCost());
    		row.getCell(j-1).setCellStyle(cellStyle);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getProfit());
    		row.getCell(j-1).setCellStyle(cellStyle);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSession());
    		row.getCell(j-1).setCellStyle(contentStyle);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getOrder());
    		row.getCell(j-1).setCellStyle(contentStyle);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSalesVolume());
    		row.getCell(j-1).setCellStyle(contentStyle);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSales());
    		row.getCell(j-1).setCellStyle(cellStyle);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getConversion());
    		row.getCell(j-1).setCellStyle(cellStyle);
		}
		
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
            String fileName ="运营统计" + sdf.format(new Date());
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName+".xls");
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	 
}
