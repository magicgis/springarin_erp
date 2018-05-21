/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.Reflections;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.Advertising;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingByWeek;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingByWeekDto;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingCountDto;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingCountItemDto;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingDto;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingGroupByweekDto;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingGroupDto;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingSearchTermReport;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingTotalByWeekDto;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingTotalDto;
import com.springrain.erp.modules.amazoninfo.entity.AmazonSearchTermReport;
import com.springrain.erp.modules.amazoninfo.entity.BusinessReport;
import com.springrain.erp.modules.amazoninfo.entity.SaleProfit;
import com.springrain.erp.modules.amazoninfo.service.AdvertisingService;
import com.springrain.erp.modules.amazoninfo.service.SaleProfitService;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.entity.lc.LcPsiLadingBill;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 广告报表Controller
 * @author Tim
 * @version 2015-03-03
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/advertising")
public class AdvertisingController extends BaseController {
	@Autowired
	private AdvertisingService advertisingService;
	
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	@Autowired
	private PsiProductService  productService;
	
	@Autowired
	private SaleProfitService saleProfitService;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@RequestMapping(value = {"list", ""})
	public String list(final String orderBy,Advertising advertising, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(advertising.getCountry())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					advertising.setCountry(dict.getValue());
					break;
				}
			}
		}
		if(StringUtils.isEmpty(advertising.getCountry())){
			advertising.setCountry("de");
		}
		List<Advertising> list = advertisingService.find(advertising); 
		Map<String,Map<String, AdvertisingGroupDto>> groupMap = Maps.newLinkedHashMap(); 
		boolean flag = "jp".equals(advertising.getCountry());
		float rate = 1f;
		if (flag) {
			rate = MathUtils.getRate("JPY", "USD", null);
			model.addAttribute("currencySymbol", "($)");
		}
		
		for (Advertising advertising2 : list) {
			if (flag) { //国家为日本时费用数据转换为美元
				advertising2.setTotalSpend(advertising2.getTotalSpend() * rate);
				advertising2.setSameSkuOrderSales(advertising2.getSameSkuOrderSales() * rate);
				advertising2.setOtherSkuOrderSales(advertising2.getOtherSkuOrderSales() * rate);
			}
			String groupName = advertising2.getGroupName();
			String name = advertising2.getName();
			Map<String, AdvertisingGroupDto> groups = groupMap.get(name);
			if(groups==null){
				groups = Maps.newLinkedHashMap(); 
				groupMap.put(name, groups);		
			}
			AdvertisingGroupDto group = groups.get(groupName);
			if(group==null){
				group = new AdvertisingGroupDto(advertising2.getName(),groupName);
				groups.put(groupName, group);
			}
			group.getAdvertisings().add(advertising2);
		}
		List<AdvertisingDto> dtos = Lists.newArrayList();
		 for (Map.Entry<String, Map<String, AdvertisingGroupDto>> entryRs : groupMap.entrySet()) { 
		    String name =entryRs.getKey();
			AdvertisingDto dto = new AdvertisingDto(name);
			dto.getGroups().addAll(entryRs.getValue().values());
			dtos.add(dto);
		}
		AdvertisingTotalDto total = new AdvertisingTotalDto();
		total.setDtos(dtos);
		total.initData();
		//排序
		if(StringUtils.isNotEmpty(orderBy)){
			Comparator comparator = new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					String[] temps =  orderBy.split(" ");
					final String prop = temps[0] ;
					Comparable c1 = (Comparable)Reflections.invokeGetter(o1, prop);
					Comparable c2 = (Comparable)Reflections.invokeGetter(o2, prop);
					return c2.compareTo(c1);
				}
			};
			Collections.sort(total.getDtos(),comparator);
			for (AdvertisingDto dto : total.getDtos()) {
				Collections.sort(dto.getGroups(),comparator);
				for (AdvertisingGroupDto group : dto.getGroups()) {
					Collections.sort(group.getAdvertisings(),comparator);
				}
			}
		}
        model.addAttribute("total", total);
        model.addAttribute("orderBy",orderBy);
		return "modules/amazoninfo/advertisingList";
	}
	
	@RequestMapping(value = {"searchTermList"})
	public String searchTermList(AmazonSearchTermReport amazonSearchTermReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(amazonSearchTermReport.getUpdateTime()==null){
			Date date=new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			amazonSearchTermReport.setUpdateTime(DateUtils.addDays(date,-1));
		}
		if(StringUtils.isBlank(amazonSearchTermReport.getCountry())){
			amazonSearchTermReport.setCountry("de");
		}
		DateFormat df=new SimpleDateFormat("yyyyMMdd");
		if(df.format(amazonSearchTermReport.getUpdateTime()).compareTo("20180506")>=0){
			AdvertisingSearchTermReport report=new AdvertisingSearchTermReport();
			report.setCountry(amazonSearchTermReport.getCountry());
			report.setUpdateTime(amazonSearchTermReport.getUpdateTime());
			report.setKeyword(amazonSearchTermReport.getKeyword());
			Page<AdvertisingSearchTermReport> page = new Page<AdvertisingSearchTermReport>(request, response);
			page=advertisingService.find(page,report);
			model.addAttribute("page", page);
			return "modules/amazoninfo/amazonSearchTermReportList2";
		}else{
			Page<AmazonSearchTermReport> page = new Page<AmazonSearchTermReport>(request, response);
			page=advertisingService.find(page,amazonSearchTermReport);
			model.addAttribute("page", page);
			return "modules/amazoninfo/amazonSearchTermReportList";
		}
	}	
	
	
	@RequestMapping(value = {"listByWeek"})
	public String listByWeek(final String orderBy,AdvertisingByWeek advertisingByWeek, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(advertisingByWeek.getCountry())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					advertisingByWeek.setCountry(dict.getValue());
					break;
				}
			}
		}
		if(StringUtils.isEmpty(advertisingByWeek.getCountry())){
			advertisingByWeek.setCountry("de");
		}
		List<AdvertisingByWeek> list = advertisingService.find(advertisingByWeek); 
		Map<String,Map<String, AdvertisingGroupByweekDto>> groupMap = Maps.newLinkedHashMap(); 
		for (AdvertisingByWeek advertisingByWeek2 : list) {
			String groupName = advertisingByWeek2.getGroupName();
			String name = advertisingByWeek2.getName();
			Map<String, AdvertisingGroupByweekDto> groups = groupMap.get(name);
			if(groups==null){
				groups = Maps.newLinkedHashMap(); 
				groupMap.put(name, groups);		
			}
			AdvertisingGroupByweekDto group = groups.get(groupName);
			if(group==null){
				group = new AdvertisingGroupByweekDto(advertisingByWeek2.getName(),groupName);
				groups.put(groupName, group);
			}
			group.getAdvertisings().add(advertisingByWeek2);
		}
		List<AdvertisingByWeekDto> dtos = Lists.newArrayList();
		for (Map.Entry<String, Map<String, AdvertisingGroupByweekDto>> entryRs : groupMap.entrySet()) { 
		    String name = entryRs.getKey();
			AdvertisingByWeekDto dto = new AdvertisingByWeekDto(name);
			dto.getGroups().addAll(entryRs.getValue().values());
			dtos.add(dto);
		}
		AdvertisingTotalByWeekDto total = new AdvertisingTotalByWeekDto();
		total.setDtos(dtos);
		total.initData();
		//排序
		if(StringUtils.isNotEmpty(orderBy)){
			Comparator comparator = new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					String[] temps =  orderBy.split(" ");
					final String prop = temps[0] ;
					Comparable c1 = (Comparable)Reflections.invokeGetter(o1, prop);
					Comparable c2 = (Comparable)Reflections.invokeGetter(o2, prop);
					return c2.compareTo(c1);
				}
			};
			Collections.sort(total.getDtos(),comparator);
			for (AdvertisingByWeekDto dto : total.getDtos()) {
				Collections.sort(dto.getGroups(),comparator);
				for (AdvertisingGroupByweekDto group : dto.getGroups()) {
					Collections.sort(group.getAdvertisings(),comparator);
				}
			}
		}
        model.addAttribute("total", total);
        model.addAttribute("orderBy",orderBy);
		return "modules/amazoninfo/advertisingListByweek";
	}
	
	
	@RequestMapping(value = {"detail"})
	public String detail(Advertising advertising, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(advertising.getGroupName()!=null){
			try {
				advertising.setGroupName(URLDecoder.decode(advertising.getGroupName(),"utf-8"));
			} catch (UnsupportedEncodingException e) {}
		}
		if(advertising.getName()!=null){
			try {
				advertising.setName(URLDecoder.decode(advertising.getName(),"utf-8"));
			} catch (UnsupportedEncodingException e) {}
		}
		if(advertising.getKeyword()!=null){
			try {
				advertising.setKeyword(URLDecoder.decode(advertising.getKeyword(),"utf-8"));
			} catch (UnsupportedEncodingException e) {}
		}
		List<Advertising> list = advertisingService.findByDate(advertising);
		boolean flag = "jp".equals(advertising.getCountry());
		float rate = 1f;
		if (flag) {
			rate = MathUtils.getRate("JPY", "USD", null);
			model.addAttribute("currencySymbol", "($)");
		}
		for (Advertising advertising2 : list) {
			if (flag) { //国家为日本时费用数据转换为美元
				advertising2.setTotalSpend(advertising2.getTotalSpend() * rate);
				advertising2.setSameSkuOrderSales(advertising2.getSameSkuOrderSales() * rate);
				advertising2.setOtherSkuOrderSales(advertising2.getOtherSkuOrderSales() * rate);
			}
		}
		model.addAttribute("advertising", advertising);
		model.addAttribute("detail", list);
		return "modules/amazoninfo/advertisingDetail";
	}
	
	@RequestMapping(value = {"export"})
	public void export(Advertising advertising,String typeFlag,HttpServletRequest request,HttpServletResponse response) {
		Map<String,Map<String,AdvertisingCountDto>> data =  advertisingService.countByProductName(advertising.getCreateDate(), advertising.getDataDate(),typeFlag, advertising.getCountry(),advertising.getKeyword());
		String country = advertising.getCountry();
		if("com".equals(country)){
			country = "us";
		}
		String currencySymbol = "";
		if ("jp".equals(country)) { //日本报表导出单位换算为美元
			currencySymbol = "($)";
		}
		String typeStr = "";
		if("1".equals(typeFlag)){
			typeStr = "按周";
		}else if("2".equals(typeFlag)){
			typeStr = "按月";
		}else if("3".equals(typeFlag)){
			typeStr = "按天";
		}
		
		
		Map<String,String> skuMap = productService.getProductNameBySku();
		

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
			
		
		HSSFCellStyle decimalStyle = wb.createCellStyle();
		decimalStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		decimalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setRightBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCell cell = null;
		
		List<String> title = Lists.newArrayList("产品名","日期","类型","sku","花费"+currencySymbol,"销售额"+currencySymbol,"Acos(%)","销量","impressions","clicks","keyword");
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
		}
		
		  int rowInx=1;
		  float totalSpend1 = 0f;
		  float totalSales1 = 0f;
		  for (Map.Entry<String, Map<String, AdvertisingCountDto>> entry : data.entrySet()) { 
		    String type =entry.getKey();
			Map<String, AdvertisingCountDto> dtos = entry.getValue();
			for (Map.Entry<String, AdvertisingCountDto> entryDto : dtos.entrySet()) { 
			    String date =entryDto.getKey();
				AdvertisingCountDto dto = entryDto.getValue();
				totalSpend1 += dto.getTotalSpend();
				totalSales1 += dto.getTotalOrderSales();
				row=sheet.createRow(rowInx++); 
				String dateStr = date;
				if("1".equals(typeFlag)){
					String temp = DateUtils.getDate(DateUtils.getFirstDayOfWeek(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(4))), "yyyy/MM/dd");
					temp +=("-"+DateUtils.getDate(DateUtils.getLastDayOfWeek(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(4))), "yyyy/MM/dd"));
					dateStr = dateStr+"("+temp+")";
				}else if("2".equals(typeFlag)){
					try {
						String temp = DateUtils.getDate(DateUtils.getFirstDayOfMonth(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(4))-1), "yyyy/MM/dd");
						temp +=("-"+DateUtils.getDate(DateUtils.getLastDayOfMonth(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(4))-1), "yyyy/MM/dd"));
						dateStr = dateStr+"("+temp+")";
					} catch (Exception e) {}
					
				}
				int index =0;
				row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(dateStr);
				row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(type);
				row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("");
				
				float totalSpend = dto.getTotalSpend();
				BigDecimal temp = new BigDecimal(totalSpend);
				temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
				totalSpend = temp.floatValue();
				row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(totalSpend);
				
				float totalSales = dto.getTotalOrderSales();
				temp = new BigDecimal(totalSales);
				temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
				totalSales = temp.floatValue();
				row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(totalSales);
				float acos = 0f;
				if(dto.getTotalOrderSales().floatValue()!=0f){
					acos = dto.getTotalSpend()*100/dto.getTotalOrderSales();
					temp = new BigDecimal(acos);
					temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
					acos = temp.floatValue();
				}
				row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(acos);
				row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(dto.getQuantity());
				row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(dto.getImpressions());
				row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(dto.getClicks());
				row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("ttl");
				
				for (AdvertisingCountItemDto item :dto.getItems()) {
					row=sheet.createRow(rowInx++); 
					index=0;
					row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(skuMap.get(item.getSku()));
					row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(item.getSku());
					totalSpend = item.getTotalSpend();
					temp = new BigDecimal(totalSpend);
					temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
					totalSpend = temp.floatValue();
					row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(totalSpend);
					totalSales = item.getTotalOrderSales();
					temp = new BigDecimal(totalSales);
					temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
					totalSales = temp.floatValue();
					row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(totalSales);
					acos = 0f;
					if(item.getTotalOrderSales().floatValue()!=0f){
						acos = item.getTotalSpend()*100/item.getTotalOrderSales();
						temp = new BigDecimal(acos);
						temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
						acos = temp.floatValue();
					}
					row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(acos);
					row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(item.getSaleV());
					row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(item.getImpressions());
					row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(item.getClicks());
					row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(item.getKeyword());
					
				}
			}
		}
		  row=sheet.createRow(rowInx++); 
		  int index=0;
		  row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("合计");
		  row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("合计");
		  row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(totalSpend1);
		  row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(totalSales1);
		  float total = 0f;
		  if(totalSales1>0){
				BigDecimal temp = new BigDecimal(totalSpend1*100/totalSales1);
				temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
				total = temp.floatValue();
		  }
		  row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue(total);
		  row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(index++,Cell.CELL_TYPE_STRING).setCellValue("");
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			String fileName = "广告费用汇总数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xls"; 
			fileName = URLEncoder.encode(fileName, "UTF-8");
			fileName = fileName.replaceAll("%7C", "-");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		  ExportExcel export = new ExportExcel(country.toUpperCase()+"广告报表费用"+typeStr+"导出", Lists.newArrayList("产品名","日期","类型","sku","花费"+currencySymbol,"销售额"+currencySymbol,"Acos(%)","销量","impressions","clicks","keyword"));
		  float totalSpend1 = 0f;
		  float totalSales1 = 0f;
		  for (Map.Entry<String, Map<String, AdvertisingCountDto>> entry : data.entrySet()) { 
		    String type =entry.getKey();
			Map<String, AdvertisingCountDto> dtos = entry.getValue();
			for (Map.Entry<String, AdvertisingCountDto> entryDto : dtos.entrySet()) { 
			    String date =entryDto.getKey();
				AdvertisingCountDto dto = entryDto.getValue();
				totalSpend1 += dto.getTotalSpend();
				totalSales1 += dto.getTotalOrderSales();
				Row row = export.addRow();
				String dateStr = date;
				if("1".equals(typeFlag)){
					String temp = DateUtils.getDate(DateUtils.getFirstDayOfWeek(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(4))), "yyyy/MM/dd");
					temp +=("-"+DateUtils.getDate(DateUtils.getLastDayOfWeek(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(4))), "yyyy/MM/dd"));
					dateStr = dateStr+"("+temp+")";
				}else if("2".equals(typeFlag)){
					try {
						String temp = DateUtils.getDate(DateUtils.getFirstDayOfMonth(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(4))-1), "yyyy/MM/dd");
						temp +=("-"+DateUtils.getDate(DateUtils.getLastDayOfMonth(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(4))-1), "yyyy/MM/dd"));
						dateStr = dateStr+"("+temp+")";
					} catch (Exception e) {}
					
				}
				int index =0;
				export.addCell(row,index++,dateStr,2,null);
				export.addCell(row,index++,type,2,null);
				export.addCell(row,index++,"",2,null);
				float totalSpend = dto.getTotalSpend();
				BigDecimal temp = new BigDecimal(totalSpend);
				temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
				totalSpend = temp.floatValue();
				export.addCell(row,index++,totalSpend,2,null);
				float totalSales = dto.getTotalOrderSales();
				temp = new BigDecimal(totalSales);
				temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
				totalSales = temp.floatValue();
				export.addCell(row,index++,totalSales,2,null);
				float acos = 0f;
				if(dto.getTotalOrderSales().floatValue()!=0f){
					acos = dto.getTotalSpend()*100/dto.getTotalOrderSales();
					temp = new BigDecimal(acos);
					temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
					acos = temp.floatValue();
				}
				export.addCell(row,index++,acos,2,null);
				
				export.addCell(row,index++,dto.getQuantity(),2,Integer.class);
				export.addCell(row,index++,dto.getImpressions(),2,Integer.class);
				export.addCell(row,index++,dto.getClicks(),2,Integer.class);
				export.addCell(row,index++,"ttl",2,null);
				
				for (AdvertisingCountItemDto item :dto.getItems()) {
					row = export.addRow();
					export.addCell(row,0,skuMap.get(item.getSku()),2,null);
					export.addCell(row,1,"",2,null);
					export.addCell(row,2,"",2,null);
					export.addCell(row,3,item.getSku(),2,null);
					totalSpend = item.getTotalSpend();
					temp = new BigDecimal(totalSpend);
					temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
					totalSpend = temp.floatValue();
					export.addCell(row,4,totalSpend,2,null);
					totalSales = item.getTotalOrderSales();
					temp = new BigDecimal(totalSales);
					temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
					totalSales = temp.floatValue();
					export.addCell(row,5,totalSales,2,null);
					acos = 0f;
					if(item.getTotalOrderSales().floatValue()!=0f){
						acos = item.getTotalSpend()*100/item.getTotalOrderSales();
						temp = new BigDecimal(acos);
						temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
						acos = temp.floatValue();
					}
					export.addCell(row,6,acos,2,null);
					
					export.addCell(row,7,item.getSaleV(),2,Integer.class);
					export.addCell(row,8,item.getImpressions(),2,Integer.class);
					export.addCell(row,9,item.getClicks(),2,Integer.class);
					export.addCell(row,10,item.getKeyword(),2,String.class);
				}
			}
		}
		Row row = export.addRow();
		export.addCell(row,0,"合计",2,null);
		export.addCell(row,1,"",2,null);
		export.addCell(row,2,"合计",2,null);
		export.addCell(row,3,"",2,null);
		export.addCell(row,4,totalSpend1,2,null);
		export.addCell(row,5,totalSales1,2,null);
		float total = 0f;
		if(totalSales1>0){
			BigDecimal temp = new BigDecimal(totalSpend1*100/totalSales1);
			temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
			total = temp.floatValue();
		}
		export.addCell(row,6,total,2,null);
		export.addCell(row,7,"",2,null);
		export.addCell(row,8,"",2,null);
		export.addCell(row,9,"",2,null);
		export.addCell(row,10,"",2,null);
		String fileName = "广告费用统计数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
		try {
			export.write(response,fileName).dispose();
		} catch (IOException e) {}*/
	}
	
	
	@RequestMapping(value = {"exportWeek"})
	public void exportWeek(AdvertisingByWeek advertisingByWeek,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		List<AdvertisingByWeek> list = advertisingService.find(advertisingByWeek); 
		Map<String,Map<String, AdvertisingGroupByweekDto>> groupMap = Maps.newLinkedHashMap(); 
		for (AdvertisingByWeek advertisingByWeek2 : list) {
			String groupName = advertisingByWeek2.getGroupName();
			String name = advertisingByWeek2.getName();
			Map<String, AdvertisingGroupByweekDto> groups = groupMap.get(name);
			if(groups==null){
				groups = Maps.newLinkedHashMap(); 
				groupMap.put(name, groups);		
			}
			AdvertisingGroupByweekDto group = groups.get(groupName);
			if(group==null){
				group = new AdvertisingGroupByweekDto(advertisingByWeek2.getName(),groupName);
				groups.put(groupName, group);
			}
			group.getAdvertisings().add(advertisingByWeek2);
		}
		List<AdvertisingByWeekDto> dtos = Lists.newArrayList();
		 for (Map.Entry<String, Map<String, AdvertisingGroupByweekDto>>  entryRs : groupMap.entrySet()) { 
		    String name =entryRs.getKey();
			AdvertisingByWeekDto dto = new AdvertisingByWeekDto(name);
			dto.getGroups().addAll(entryRs.getValue().values());
			dtos.add(dto);
		}
		AdvertisingTotalByWeekDto total = new AdvertisingTotalByWeekDto();
		total.setDtos(dtos);
		total.initData();
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		String[] title = {"productName","Campaign","Ad group","SKU","Keywords","Match type","total spend","Impressions","Clicks","Orders placed","Orders profit","Other orders placed","Variations orders placed","Variations profit","Conversion","ROI"};
		
		  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		  //设置Excel中的边框(表头的边框)
//		  //设置字体
		  HSSFFont font = wb.createFont();
		  font.setFontHeightInPoints((short) 11); // 字体高度
		  style.setFont(font);
		  row.setHeight((short) 400);
		  HSSFCell cell = null;		
		  for (int i = 0; i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
		  DecimalFormat df = new DecimalFormat("0.##");
		  int excelNo =1;
		  Map<String,String> skuMap = this.productService.getProductNameBySku();
		  for(AdvertisingByWeekDto dto  : total.getDtos()){
			  for(AdvertisingGroupByweekDto groupDto:dto.getGroups()){
				  for(AdvertisingByWeek week:groupDto.getAdvertisings()){
					  row = sheet.createRow(excelNo++);  //生成行
					  int i =0;
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(skuMap.get(week.getSku())); 
		         	  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getName());
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getGroupName()); 
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getSku()); 
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getKeyword()); 
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getType()); 
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(df.format(week.getTotalSpend()));
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getImpressions()); 
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getClicks()); 
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(df.format(week.getWeekSameSkuUnitsOrdered())); 
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getWeekSameSkuUnitsLirun()); 
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getWeekOtherSkuUnitsOrdered()); 
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getWeekParentSkuUnitsOrdered()); 
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getWeekParentSkuUnitsLirun()); 
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getConversion()); 
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(week.getRoi()); 
				  }
			  }
		  }
		  
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");

		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");

		String fileName = "PurchaseOrderData" + sdf.format(new Date()) + ".xls";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
		try {
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		  
	
	@RequestMapping(value = "listByDate")
	public String listByDate(String active,final String orderBy, String date1, String date2, Advertising advertising, HttpServletRequest request, HttpServletResponse response, Model model) throws NumberFormatException, ParseException {
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
		Date start = advertising.getCreateDate();
		Date end = advertising.getDataDate();
		//国家为空时汇总统计各平台广告信息
		if(StringUtils.isBlank(advertising.getCountry())){
			//查询2个时间节点
			if("1".equals(advertising.getSearchFlag())){
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
				advertising.setCreateDate(start);
				advertising.setDataDate(end);
			}else if("2".equals(advertising.getSearchFlag())){
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
				advertising.setCreateDate(start);
				advertising.setDataDate(end);
				date1 = formatMonth.format(start);
				date2 = formatMonth.format(end);
			}else{
				if(StringUtils.isBlank(date1)){
					Date today = new Date();
					int hours = today.getHours();
					today.setHours(0);
					today.setMinutes(0);
					today.setSeconds(0);
					if (hours > 10) {
						end = DateUtils.addDays(today, -1);
						start = DateUtils.addDays(today, -2);
					} else {
						end = DateUtils.addDays(today, -2);
						start = DateUtils.addDays(today, -3);
					}
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
				advertising.setCreateDate(start);
				advertising.setDataDate(end);
			}
			
			Map<String, Map<String, Advertising>> data = advertisingService.totalByDate(advertising, date1, date2);
			model.addAttribute("data", data);
			//构建x轴
			List<String> xAxis  = Lists.newArrayList();
			Map<String, String> tip = Maps.newHashMap();

			String type = "日";
			if("1".equals(advertising.getSearchFlag())){
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
			}else if("2".equals(advertising.getSearchFlag())){
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
			return "modules/amazoninfo/advertisingListByDate";
		}
		//分平台查询
		String type = "日"; 
		if("1".equals(advertising.getSearchFlag())){
			//按周查询
			if(StringUtils.isBlank(date1)){
				Date today = new Date();
				date2 = formatWeek.format(today);
				date1 = formatWeek.format(DateUtils.addWeeks(today, -20));
			} else {
				if ("2015-53".equals(date1)) {
					date1 = "2016-01";
				}
				if ("2015-53".equals(date2)) {
					date2 = "2016-01";
				}
			}
			start = formatWeek.parse(date1);
			end = formatWeek.parse(date2);
			advertising.setCreateDate(start);
			advertising.setDataDate(end);
			type = "周";
		}else if("2".equals(advertising.getSearchFlag())){
			//按月查询
			if(StringUtils.isBlank(date1)){
				Date today = new Date();
				date2 = formatMonth.format(today);
				date1 = formatMonth.format(DateUtils.addMonths(today, -12));
			}
			start = formatMonth.parse(date1);
			end = formatMonth.parse(date2);
			advertising.setCreateDate(start);
			advertising.setDataDate(end);
			type = "月";
		}else{
			if(StringUtils.isBlank(date1)){
				Date today = new Date();
				int hours = today.getHours();
				if ("com".equals(advertising.getCountry()) && hours < 10) {
					today = DateUtils.addDays(today, -2);
				} else {
					today = DateUtils.addDays(today, -1);
				}
				date2 = formatDay.format(today);
				date1 = formatDay.format(DateUtils.addMonths(today, -1));
			}
			start = formatDay.parse(date1);
			end = formatDay.parse(date2);
			advertising.setCreateDate(start);
			advertising.setDataDate(end);
		}
		if(StringUtils.isBlank(advertising.getCountry())){
			advertising.setCountry("de");
		}
		
		List<Advertising> list = advertisingService.findByCountry(advertising, date1, date2, orderBy);
		//排序
		if(StringUtils.isNotEmpty(orderBy) && !orderBy.contains("dates")){
			Comparator comparator = new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					String[] temps =  orderBy.split(" ");
					final String prop = temps[0] ;
					Comparable c1 = (Comparable)Reflections.invokeGetter(o1, prop);
					Comparable c2 = (Comparable)Reflections.invokeGetter(o2, prop);
					if (temps[1] != null && "ASC".equals(temps[1])) {
						return c2.compareTo(c1);
					}
					return c1.compareTo(c2);
				}
			};
			Collections.sort(list, comparator);
		}
        model.addAttribute("list", list);
        model.addAttribute("type", type);
        model.addAttribute("orderBy", orderBy);
        
        //---图表数据
		if(StringUtils.isEmpty(active)){
			active = "2";
		}
        Date beforeDate=null;
		Date afterDate=null;
        
        String searchFlag = advertising.getSearchFlag();
    	if(searchFlag.equals("0")){
			beforeDate = advertising.getCreateDate();
			afterDate = advertising.getDataDate();
		}else if (searchFlag.equals("1")){
			beforeDate =DateUtils.getMonday(advertising.getCreateDate());
			afterDate =DateUtils.getSunday(advertising.getDataDate());
		}else if (searchFlag.equals("2")){
			beforeDate =DateUtils.getFirstDayOfMonth(advertising.getCreateDate());
			afterDate =DateUtils.getLastDayOfMonth(advertising.getDataDate());
		}
        
        if(StringUtils.isEmpty(searchFlag)){
			searchFlag = "0";
		}
		List<String> xAxis = Lists.newArrayList();
		
		TreeMap<String, String> timeMap = Maps.newTreeMap();
		Date datet = end;
			if(!start.after(end)){
				DateFormat format3 = new SimpleDateFormat("yyyy/M/d");
				int i = 0 ;
				if(searchFlag.equals("0")){
					Date date = beforeDate;
					while(!date.after(afterDate)){
						i++;
						xAxis.add("'"+format3.format(date)+"'");
						if(!date.after(datet)){
							timeMap.put(formatDay.format(date), "0");
						}
						date = DateUtils.addDays(beforeDate, i);
					}
				}else if (searchFlag.equals("1")){
					Date date = afterDate;
					Date datetSunday=DateUtils.getSunday(datet);
					while(date.after(beforeDate)){
						i++;
						if(!date.after(datetSunday)){
							String yearAndWeek = formatWeek.format(date);
							if ("01".equals(yearAndWeek.split("-")[1]) && date.getMonth()==11) {
								int year = Integer.parseInt(yearAndWeek.split("-")[0]) + 1;
								yearAndWeek = year + "-" + yearAndWeek.split("-")[1];
							}
							timeMap.put(yearAndWeek, "0");
						}
						date = DateUtils.addDays(afterDate, -7*i);
					}
					
					//迭代timeMap 时间排序
					for (Iterator<String> iterator = timeMap.keySet().iterator(); iterator.hasNext();) {
						String str = iterator.next();
						xAxis.add("'"+str+"周'");
					}
				}else if (searchFlag.equals("2")){
					Date date = beforeDate;
					Date datetFirstDay= DateUtils.addMonths(DateUtils.getFirstDayOfMonth(datet),1);
					SimpleDateFormat format = new SimpleDateFormat("yyyy/M");
					while(!date.after(afterDate)){
						i++;
						xAxis.add("'"+format.format(date)+"月'");
						if(!date.after(datetFirstDay)){
							timeMap.put(formatMonth.format(date), "0");
						}
						date = DateUtils.addMonths(beforeDate,i);
					}
				}
			}
			
			Map<String,List<String>> clicksData = Maps.newHashMap();
			Map<String,List<String>> averageCPCData = Maps.newHashMap();
			Map<String,List<String>> acosData = Maps.newHashMap();
			
			DecimalFormat df = new DecimalFormat("#.00");
			
			for (Advertising adv : list) {
				
				String key = DictUtils.getDictLabel(adv.getCountry(),"platform","");
				key = key.split("\\|")[0];
				String clicks = adv.getClicks()==null?"0":adv.getClicks()+"";
				String averageCPC = adv.getAverageCPC()==null?"0":adv.getAverageCPC()+"";
				String acos = adv.getAcos()==null?"0":adv.getAcos()+"";
				String dateStr = adv.getGroupName();
				
				List<String> time = Lists.newArrayList(timeMap.keySet());
				if(!("0".equals(clicks))){
					List<String> temp = clicksData.get(key);
					if(temp==null){
						temp = Lists.newArrayList();
						for (Iterator<Entry<String, String>> iterator = timeMap.entrySet().iterator(); iterator.hasNext();) {
							iterator.next();
							temp.add("0");
						}
						clicksData.put(key, temp);
						temp = clicksData.get(key);
					}
					if(time.indexOf(dateStr)!=-1){
						int index = time.indexOf(dateStr);
						if(index<temp.size()){
							temp.set(index, clicks);
						}
					}
				}
				if(!("0".equals(averageCPC)&&"0.0".equals(averageCPC))){
					List<String> temp = averageCPCData.get(key);
					if(temp==null){
						temp = Lists.newArrayList();
						for (Iterator<Entry<String, String>> iterator = timeMap.entrySet().iterator(); iterator.hasNext();) {
							iterator.next();
							temp.add("0");
						}
						averageCPCData.put(key, temp);
						temp = averageCPCData.get(key);
					}
					
					if(time.indexOf(dateStr)!=-1){
						int index = time.indexOf(dateStr);
						if(index<temp.size()){
							temp.set(index,df.format(Float.parseFloat(averageCPC)));
						}
					}
				}
				if(!("0".equals(acos)&&"0.0".equals(acos))){
					List<String> temp = acosData.get(key);
					if(temp==null){
						temp = Lists.newArrayList();
						for (Iterator<Entry<String, String>> iterator = timeMap.entrySet().iterator(); iterator.hasNext();) {
							iterator.next();
							temp.add("0");
						}
						acosData.put(key, temp);
						temp = acosData.get(key);
					}
					
					if(time.indexOf(dateStr)!=-1){
						int index = time.indexOf(dateStr);
						if(index<temp.size()){
							temp.set(index,df.format(Float.parseFloat(acos)));
						}
					}
				}
			}
			
		model.addAttribute("clicksData", clicksData);
		model.addAttribute("averageCPCData", averageCPCData);
		model.addAttribute("acosData", acosData);
		
		model.addAttribute("xAxis", xAxis.toString());
		model.addAttribute("active", active);
		model.addAttribute("date1", date1);
		model.addAttribute("date2", date2);
		return "modules/amazoninfo/advertisingListByDate";
	}
	
	@RequestMapping(value = "exportByCountryAndDate")
	public String exportByCountryAndDate(String date1, String date2, Advertising advertising, HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, ParseException {
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
		Date start = advertising.getCreateDate();
		Date end = advertising.getDataDate();
		String type = "日"; 
		String titleStr = "ByDay";
		if("1".equals(advertising.getSearchFlag())){
			//按周查询
			if(StringUtils.isBlank(date1)){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -20);	//默认查询20周
				end = today;
				date1 = DateUtils.getWeekStr(start, formatWeek, 5, "-");
				date2 = DateUtils.getWeekStr(end, formatWeek, 5, "-");
			} else {
				start = formatWeek.parse(date1);
				end = formatWeek.parse(date2);
			}
			advertising.setCreateDate(start);
			advertising.setDataDate(end);
			type = "周";
			titleStr = "ByWeek";
		}else if("2".equals(advertising.getSearchFlag())){
			//按月查询
			if(StringUtils.isBlank(date1)){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -12);	//默认查询12个月
				end = today;
			}else{
				start = formatMonth.parse(date1);
				end = formatMonth.parse(date2);
			}
			advertising.setCreateDate(start);
			advertising.setDataDate(end);
			date1 = formatMonth.format(start);
			date2 = formatMonth.format(end);
			type = "月";
			titleStr = "ByMonth";
		}else{
			if(StringUtils.isBlank(date1)){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);	//默认查询1个月
				end = today;
			}else{
				start = formatDay.parse(date1);
				end = formatDay.parse(date2);
			}
			date1 = formatDay.format(start);
			date2 = formatDay.format(end);
			advertising.setCreateDate(start);
			advertising.setDataDate(end);
		}
		if(StringUtils.isBlank(advertising.getCountry())){
			advertising.setCountry("de");
		}	
		List<Advertising> list = advertisingService.findByCountry(advertising, date1, date2, null);
		
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

		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);

		String country = DictUtils.getDictLabel(advertising.getCountry(), "platform", "");
		String currencySymbol = AdvertisingService.getCurrencySymbol().get(advertising.getCountry());
		if (currencySymbol == null) {
			currencySymbol = "EUR";
		}
		String[] title = { "  国家  ", "  Date   ", "clicks", "Average CPC("+currencySymbol+")", "Total Spend("+currencySymbol+")", 
				"平均 Conversion", "平均 Acos","Order Sales("+currencySymbol+")","Order Placed"};

		row.setHeight((short) 600);
		HSSFCell cell = null;
		for (int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int excelNo = 1;
		for (Advertising entry : list) {
			int cellNum = 0;
			row = sheet.createRow(excelNo++); // 生成行
			row.setHeight((short) 400);
			// 国家
			row.createCell(cellNum++, Cell.CELL_TYPE_STRING).setCellValue(country);
			// 日期
			row.createCell(cellNum++, Cell.CELL_TYPE_STRING).setCellValue(entry.getGroupName()+type+entry.getName());
			// clicks
			row.createCell(cellNum++, Cell.CELL_TYPE_STRING).setCellValue(entry.getClicks());
			// Average CPC
			if (entry.getClicks() > 0) {
				row.createCell(cellNum++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f", entry.getAverageCPC()));
			} else {
				row.createCell(cellNum++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			// Total Spend
			row.createCell(cellNum++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f",entry.getTotalSpend()));
			// 平均 Conversion
			row.createCell(cellNum++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f",entry.getConversion()) + "%");
			// 平均 Acos
			row.createCell(cellNum++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f",entry.getAcos()) + "%");
			// Order Sales
			row.createCell(cellNum++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f",entry.getOrderSales()));
			// Order Placed
			row.createCell(cellNum++, Cell.CELL_TYPE_STRING).setCellValue(entry.getOrdersPlaced());
		}

		for (int i = 0; i < excelNo-1; i++) {
			for (int j = 0; j < title.length; j++) {
				sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
			}
		}
		for (int i = 0; i < title.length; i++) {
			sheet.autoSizeColumn((short) i);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			String fileName = titleStr+"统计"+country+"广告费用汇总数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xls"; 
			fileName = URLEncoder.encode(fileName, "UTF-8");
			fileName = fileName.replaceAll("%7C", "-");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/advertising/listByDate/?repage";
	}
	
	@RequestMapping(value = {"exportTotalByDate"})
	public String exportTotalByDate(String date1, String date2, Advertising advertising, HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, ParseException {
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
		Date start = advertising.getCreateDate();
		Date end = advertising.getDataDate();
		//查询2个时间节点
		if("1".equals(advertising.getSearchFlag())){
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
			advertising.setCreateDate(start);
			advertising.setDataDate(end);
		}else if("2".equals(advertising.getSearchFlag())){
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
			advertising.setCreateDate(start);
			advertising.setDataDate(end);
			date1 = formatMonth.format(start);
			date2 = formatMonth.format(end);
		}else{
			if(StringUtils.isBlank(date1)){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addDays(today, -1);
				end = today;
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
			advertising.setCreateDate(start);
			advertising.setDataDate(end);
		}
			
		Map<String, Map<String, Advertising>> data = advertisingService.totalByDate(advertising, date1, date2);
		//构建x轴
		List<String> xAxis  = Lists.newArrayList();
		Map<String, String> tip = Maps.newHashMap();

		String type = "日";
		String titleStr = "ByDay";
		if("1".equals(advertising.getSearchFlag())){
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
		}else if("2".equals(advertising.getSearchFlag())){
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
			title.add("clicks");title.add("Average CPC(€)");title.add("Total Spend(€)");title.add("Acos");
		}
		title.add("clicks差");title.add("Acos差");

		int num=0;
		int startIndex=1;
		int endIndex=4;
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i==0||i==(title.size()-2)||i==(title.size()-1)){	//第一列和最后两列
				cell.setCellValue(title.get(i));
			}else if(i==startIndex && i != (title.size()-2)){	//i==1||i==5
				int index = num++;
				String content = xAxis.get(index) + type;
				if("1".equals(advertising.getSearchFlag())){
					content = content + "(" + tip.get(xAxis.get(index)) + ")";
				}
				cell.setCellValue(content);
				startIndex+=4;
			}else if(i==endIndex && i != (title.size())){	//i==4||i==8合并两列
				sheet.addMergedRegion(new CellRangeAddress(0, 0,i-3,i));
				endIndex+=4;
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
			if(!"com.unitek".equals(dict.getValue()) && !"ca".equals(dict.getValue())
					&& !"mx".equals(dict.getValue())){
				row = sheet.createRow(rownum++);
			    row.setHeightInPoints(22);
			    cell = row.createCell(0,Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(dict.getLabel());
				List<Integer> clicksList = Lists.newArrayList();
				List<Float> acosList = Lists.newArrayList();
				for (int i = 1; i <= xAxis.size(); i++) {
					try {
						Integer clicks = data.get(dict.getValue()).get(xAxis.get(i-1)).getClicks();
						cell = row.createCell(4*i-3,Cell.CELL_TYPE_STRING);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(clicks);
						clicksList.add(clicks);
					} catch (Exception e) {
						row.createCell(4*i-3,Cell.CELL_TYPE_STRING).setCellValue("");
						clicksList.add(0);
					}
					try {
						Float averageCPC = data.get(dict.getValue()).get(xAxis.get(i-1)).getAverageCPC();
						cell = row.createCell(4*i-2,Cell.CELL_TYPE_STRING);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(String.format("%.2f", averageCPC));
					} catch (Exception e) {
						row.createCell(4*i-2,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					try {
						Float totalSpend = data.get(dict.getValue()).get(xAxis.get(i-1)).getTotalSpend();
						cell = row.createCell(4*i-1,Cell.CELL_TYPE_STRING);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(String.format("%.2f", totalSpend));
					} catch (Exception e) {
						row.createCell(4*i-1,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					try {
						Float aCos = data.get(dict.getValue()).get(xAxis.get(i-1)).getAcos();
						cell = row.createCell(4*i,Cell.CELL_TYPE_STRING);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(String.format("%.2f", aCos)+"%");
						acosList.add(aCos);
					} catch (Exception e) {
						row.createCell(4*i,Cell.CELL_TYPE_STRING).setCellValue("");
						acosList.add(0f);
					}
				}
			    cell = row.createCell(9,Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(clicksList.get(1)-clicksList.get(0));
				
				Float f1 = Float.parseFloat(String.format("%.2f", acosList.get(1)));
				Float f2 = Float.parseFloat(String.format("%.2f", acosList.get(0)));
				cell = row.createCell(10,Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);
				Float acosCha = f1-f2;
				cell.setCellValue(String.format("%.2f", acosCha) + "%");
			}
		}
	    //加上总计
	    row = sheet.createRow(rownum++);
	    row.setHeightInPoints(22);
	    cell = row.createCell(0,Cell.CELL_TYPE_STRING);
		cell.setCellStyle(cellStyle);
		cell.setCellValue("总计");
		List<Integer> clicksList = Lists.newArrayList();
		List<Float> acosList = Lists.newArrayList();
		for (int i = 1; i <= xAxis.size(); i++) {
			try {
				Integer clicks = data.get("total").get(xAxis.get(i-1)).getClicks();
				cell = row.createCell(4*i-3,Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(clicks);
				clicksList.add(clicks);
			} catch (Exception e) {
				row.createCell(4*i-3,Cell.CELL_TYPE_STRING).setCellValue("");
				clicksList.add(0);
			}
			try {
				Integer clicks = data.get("total").get(xAxis.get(i-1)).getClicks();
				Float totalSpend = data.get("total").get(xAxis.get(i-1)).getTotalSpend();
				Float averageCPC = totalSpend/clicks;
				if (clicks > 0) {
					cell = row.createCell(4*i-2,Cell.CELL_TYPE_STRING);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(String.format("%.2f", averageCPC));
				} else {
					row.createCell(4*i-2,Cell.CELL_TYPE_STRING).setCellValue("");
				}
			} catch (Exception e) {
				row.createCell(4*i-2,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			try {
				Float totalSpend = data.get("total").get(xAxis.get(i-1)).getTotalSpend();
				cell = row.createCell(4*i-1,Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(String.format("%.2f", totalSpend));
			} catch (Exception e) {
				row.createCell(4*i-1,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			try {
				Float acos = data.get("total").get(xAxis.get(i-1)).getAcos();
				cell = row.createCell(4*i,Cell.CELL_TYPE_STRING);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(String.format("%.2f", acos)+"%");
				acosList.add(acos);
			} catch (Exception e) {
				row.createCell(4*i,Cell.CELL_TYPE_STRING).setCellValue("");
				acosList.add(0f);
			}
		}
		cell = row.createCell(9,Cell.CELL_TYPE_STRING);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(clicksList.get(1)-clicksList.get(0));
		Float f1 = Float.parseFloat(String.format("%.2f", acosList.get(1)));
		Float f2 = Float.parseFloat(String.format("%.2f", acosList.get(0)));
		cell = row.createCell(10,Cell.CELL_TYPE_STRING);
		cell.setCellStyle(cellStyle);
		Float acosCha = f1-f2;
		cell.setCellValue(String.format("%.2f", acosCha) + "%");

		for (int i = 0; i < title.size(); i++) {
	   	   sheet.autoSizeColumn((short)i,true);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	        String fileName = titleStr + "多平台广告汇总比较" + sdf.format(new Date()) + ".xls";
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
	
	private static DateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
	private static DateFormat formatWeek = new SimpleDateFormat("yyyyww");
	private static DateFormat formatMonth = new SimpleDateFormat("yyyyMM");
	
	@RequestMapping(value = {"adsAnalyse"})
	public String adsAnalyse(Advertising advertising, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(advertising.getCountry())){
			advertising.setCountry("de");
		}
		if(StringUtils.isBlank(advertising.getSearchFlag())||"0".equals(advertising.getSearchFlag())){
			advertising.setSearchFlag("2");
		}
		Map<String,Map<String,Advertising>> adsMap=advertisingService.findAdsReport(advertising);
        model.addAttribute("adsMap",adsMap);
        List<PsiProductTypeGroupDict> lineList=groupDictService.getAllList();
        model.addAttribute("lineList",lineList);
        
        Date start = advertising.getCreateDate();
		if(start.getDay()==0&&"2".equals(advertising.getSearchFlag())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = advertising.getDataDate();
		List<String> xAxis  = Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			if("2".equals(advertising.getSearchFlag())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
                if(week==53){
                	year =DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				start = DateUtils.addWeeks(start, 1);
			}else if("3".equals(advertising.getSearchFlag())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				
				start = DateUtils.addDays(start, 1);
			}
		}
		model.addAttribute("xAxis", xAxis);
		return "modules/amazoninfo/advertisingAnalyseList";
	}
	

	@RequestMapping(value = {"findKeyWordByName"})
	public String findKeyWordByName(Advertising advertising,String createDate1,String dataDate1, HttpServletRequest request, HttpServletResponse response, Model model){
		DateFormat formatDay1 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			advertising.setCreateDate(formatDay1.parse(createDate1));
			advertising.setDataDate(formatDay1.parse(dataDate1));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Map<String,Map<String,Advertising>> keywordMap=advertisingService.findAdsReportByKeyWord(advertising);
		Date start = advertising.getCreateDate();
		if(start.getDay()==0&&"2".equals(advertising.getSearchFlag())){
				start = DateUtils.addDays(start, -1);
		}
		Date end = advertising.getDataDate();
		List<String> xAxis  = Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			if("2".equals(advertising.getSearchFlag())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(start).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				start = DateUtils.addWeeks(start, 1);
			}else if("3".equals(advertising.getSearchFlag())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				
				start = DateUtils.addDays(start, 1);
			}
		}
		model.addAttribute("xAxis", xAxis);
		model.addAttribute("keywordMap", keywordMap);
		return "modules/amazoninfo/advertisingAnalyseKeyWordList";
	}
	
	
	@RequestMapping(value = {"exportAdsKeyword"})
	public String exportAdsKeyword(Advertising advertising, HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, ParseException {
		Map<String,Map<String,Advertising>> adsMap=advertisingService.findAdsReport(advertising);
        Date start = advertising.getCreateDate();
		if(start.getDay()==0&&"2".equals(advertising.getSearchFlag())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = advertising.getDataDate();
		List<String> xAxis  = Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			if("2".equals(advertising.getSearchFlag())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(start).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				start = DateUtils.addWeeks(start, 1);
			}else if("3".equals(advertising.getSearchFlag())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				
				start = DateUtils.addDays(start, 1);
			}
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
			
		
		HSSFCellStyle decimalStyle = wb.createCellStyle();
		decimalStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		decimalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setRightBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCell cell = null;
			
		List<String> title = Lists.newArrayList("产品","类型");
		for (String date: xAxis) {
			title.add(date);
		}

		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
		}
		
		int rowInx=1;
		////有click无销量关键词，广告成本大于平均成本2倍 关键词 	
	    for (Map.Entry<String, Map<String, Advertising>> entry : adsMap.entrySet()) { 
		    String name=entry.getKey();
		    Map<String, Advertising> adValue=entry.getValue();
			int j=0;
			row = sheet.createRow(rowInx++);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("clicks");
			for (String date: xAxis) {
				if(adValue!=null&&adValue.get(date)!=null){
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(adValue.get(date).getClicks());
					row.getCell(j-1).setCellStyle(decimalStyle);
				}else{
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}
			int j1=0;
			row = sheet.createRow(rowInx++);
			row.createCell(j1++,Cell.CELL_TYPE_STRING).setCellValue(name);
			row.createCell(j1++,Cell.CELL_TYPE_STRING).setCellValue("Ads Quantity");
			for (String date: xAxis) {
				if(adValue!=null&&adValue.get(date)!=null){
					row.createCell(j1++,Cell.CELL_TYPE_STRING).setCellValue(adValue.get(date).getSameSkuOrdersPlaced());
					row.getCell(j1-1).setCellStyle(decimalStyle);
				}else{
					row.createCell(j1++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				
			}
			int j2=0;
			row = sheet.createRow(rowInx++);
			row.createCell(j2++,Cell.CELL_TYPE_STRING).setCellValue(name);
			row.createCell(j2++,Cell.CELL_TYPE_STRING).setCellValue("Ads Cost");
			for (String date: xAxis) {
				if(adValue!=null&&adValue.get(date)!=null){
					row.createCell(j2++,Cell.CELL_TYPE_STRING).setCellValue(new BigDecimal(adValue.get(date).getTotalSpend()).setScale(2,4).floatValue());
					row.getCell(j2-1).setCellStyle(decimalStyle);
				}else{
					row.createCell(j2++,Cell.CELL_TYPE_STRING).setCellValue("");
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
	        String fileName = "广告产品" + sdf.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value = {"exportAdsKeyword2"})
	public String exportAdsKeyword2(Advertising advertising, HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, ParseException {
		Map<String, Map<String, Advertising>> data= advertisingService.findExceptionAdsKeyWord(advertising);
	
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
			
		List<String> title = Lists.newArrayList("产品","关键字","类型");
		

		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
		}
		
		int rowInx=1;
		////有click无销量关键词，广告成本大于平均成本2倍 关键词 	
		for (Map.Entry<String, Map<String, Advertising>> entry : data.entrySet()) { 
		    String name=entry.getKey();
			Map<String, Advertising> keyWordMap=entry.getValue();
			for (Map.Entry<String, Advertising> entryRs : keyWordMap.entrySet()) {
			    String keyword=entryRs.getKey();
				if(!"total".equals(keyword)){
					Advertising ads=entryRs.getValue();
					if(ads.getSameSkuOrdersPlaced()==0){
						row = sheet.createRow(rowInx++);
						int j=0;
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(keyword);
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("有("+ads.getClicks()+")click无销量");
						continue;
					}
					
					int size=keyWordMap.keySet().size()-1;
					Float avgSpend=keyWordMap.get("total").getTotalSpend()/keyWordMap.get("total").getSameSkuOrdersPlaced()/size;
					if(ads.getTotalSpend()/ads.getSameSkuOrdersPlaced()>avgSpend*2){
						row = sheet.createRow(rowInx++);
						int j=0;
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(keyword);
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("广告成本("+new BigDecimal(ads.getTotalSpend()/ads.getSameSkuOrdersPlaced()).setScale(2,4)+")大于平均成本("+new BigDecimal(avgSpend).setScale(2,4)+")2倍");
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
	        String fileName = "keyword" + sdf.format(new Date()) + ".xls";
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
	
	@RequestMapping(value = {"exportSearchTermList"})
	public String exportSearchTermList(AmazonSearchTermReport amazonSearchTermReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AmazonSearchTermReport> page = new Page<AmazonSearchTermReport>(request, response,-1);
		page.setPageSize(60000);
		page=advertisingService.find(page,amazonSearchTermReport);
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
		DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		
		HSSFCellStyle decimalStyle = wb.createCellStyle();
		decimalStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		
		
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
			
		HSSFCellStyle cellStyle = wb.createCellStyle();
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCell cell = null;
			
		List<String> title = Lists.newArrayList("Campaign Name","Ad Group Name","Customer Search Term","Keyword","Match Type","First Day of Impression"
              ,"Last Day of Impression","Impressions","Clicks","CTR(%)","Total Spend", "Average CPC","ACoS", "Orders placed within 1-week of a click",
              "Product Sales within 1-week of a click", "Conversion Rate within 1-week of a click","Same SKU units Ordered within 1-week of click",
              "Other SKU units Ordered within 1-week of click","Same SKU units Product Sales within 1-week of click","Other SKU units Product Sales within 1-week of click");
		

		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
		}
		
		int rowInx=1;
        for (AmazonSearchTermReport report : page.getList()) {
        	row = sheet.createRow(rowInx++);
			int j=0;
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getCampaignName());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAdGroupName());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getCustomerSearchTerm());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getKeyword());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getMatchType());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dateFormat.format(report.getStartDate()));
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dateFormat.format(report.getEndDate()));
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getImpressions());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getClicks());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getCtr());
			row.getCell(j-1).setCellStyle(decimalStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getTotalSpend());
			row.getCell(j-1).setCellStyle(decimalStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAverageCpc());
			row.getCell(j-1).setCellStyle(decimalStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAcos());
			row.getCell(j-1).setCellStyle(decimalStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getOrdersPlaced());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getProductSales());
			row.getCell(j-1).setCellStyle(decimalStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getConversionRate());
			row.getCell(j-1).setCellStyle(decimalStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSameSku());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getOtherSku());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSameSkuSale());
			row.getCell(j-1).setCellStyle(decimalStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getOtherSkuSale());
			row.getCell(j-1).setCellStyle(decimalStyle);
		}
		

		for (int i = 0; i < title.size(); i++) {
	   	   sheet.autoSizeColumn((short)i,true);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	        String fileName = "searchTerm" + sdf.format(new Date()) + ".xls";
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
		  
	@RequestMapping(value = "listByProduct")
	public String listByProduct(SaleProfit saleProfit, String flag, HttpServletRequest request, HttpServletResponse response, Model model) throws NumberFormatException, ParseException {
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
		if (StringUtils.isEmpty(flag)) {
			flag = "2";
		}
		Calendar calendar = Calendar.getInstance();
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		if (StringUtils.isEmpty(saleProfit.getDay())) {
			if ("2".equals(flag)) { //按月
				if (dayOfMonth >= 15) {	//15号之后上个月的结算报告都出来了
					saleProfit.setDay(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -1)));
					saleProfit.setEnd(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -1)));
				} else {
					saleProfit.setDay(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -2)));
					saleProfit.setEnd(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -2)));
				}
			} else if ("1".equals(flag)){//按周
				saleProfit.setDay(formatWeek.format(DateUtils.addWeeks(calendar.getTime(), -1)));
				saleProfit.setEnd(formatWeek.format(DateUtils.addWeeks(calendar.getTime(), -1)));
			} else {
				saleProfit.setDay(formatDay.format(DateUtils.addMonths(calendar.getTime(), -1)));
				saleProfit.setEnd(formatDay.format(DateUtils.addMonths(calendar.getTime(), -1)));
			}
		}
		Date date = null;
		Date date1 = null;
		if ("2".equals(flag)) {
			date = formatDay.parse(saleProfit.getDay() + "-01");
			date1 = formatDay.parse(saleProfit.getEnd() + "-01");
		} else if ("1".equals(flag)) {
			date = formatWeek.parse(saleProfit.getDay());
			date1 = formatWeek.parse(saleProfit.getEnd());
		} else {
			date = formatDay.parse(saleProfit.getDay());
			date1 = formatDay.parse(saleProfit.getEnd());
		}
		model.addAttribute("date", date);
		model.addAttribute("date1", date1);
		model.addAttribute("flag", flag);
		model.addAttribute("saleProfit", saleProfit);
		Map<String, Map<String, Map<String, SaleProfit>>> data = advertisingService.getAdFeeList(saleProfit, flag);
		model.addAttribute("data", data);
		return "modules/amazoninfo/advertisingListByProduct";
	}
	
	@RequestMapping(value = {"exportProductReport"})
	public String exportProductReport(SaleProfit saleProfit, String flag, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		
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
		

		HSSFCellStyle decimalStyle = wb.createCellStyle();
		decimalStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		
		HSSFCellStyle intStyle = wb.createCellStyle();
		intStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		
		HSSFCell cell = null;
		List<String> countryList = Lists.newArrayList("total","com","de","uk","fr","it","es","ca","jp");
		List<String> title = Lists.newArrayList("日期","产品名","产品线");
		for (String country : countryList) {
			if ("total".equals(country)) {
				title.add("总费用");
				title.add("总销量");
				title.add("总销售额");
			} else {
				String name = SystemService.countryNameMap.get(country);
				title.add(name+"费用");
				title.add(name+"销量");
				title.add(name+"销售额");
			}
		}
			
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		DateFormat formatMonth = new SimpleDateFormat("yyyy-MM");
		if (StringUtils.isEmpty(flag)) {
			flag = "2";
		}
		Calendar calendar = Calendar.getInstance();
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		if (StringUtils.isEmpty(saleProfit.getDay())) {
			if ("2".equals(flag)) { //按月
				if (dayOfMonth >= 15) {	//15号之后上个月的结算报告都出来了
					saleProfit.setDay(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -1)));
					saleProfit.setEnd(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -1)));
				} else {
					saleProfit.setDay(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -2)));
					saleProfit.setEnd(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -2)));
				}
			} else if ("1".equals(flag)){//按周
				saleProfit.setDay(formatWeek.format(DateUtils.addWeeks(calendar.getTime(), -1)));
				saleProfit.setEnd(formatWeek.format(DateUtils.addWeeks(calendar.getTime(), -1)));
			} else {
				saleProfit.setDay(formatDay.format(DateUtils.addMonths(calendar.getTime(), -1)));
				saleProfit.setEnd(formatDay.format(DateUtils.addMonths(calendar.getTime(), -1)));
			}
		}
		String type = "日";
		if ("1".equals(flag)) {
			type = "周";
		} else if ("2".equals(flag)) {
			type = "月";
		}
		Map<String, Map<String, Map<String, SaleProfit>>> data = advertisingService.getAdFeeList(saleProfit, flag);
    	HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
    	for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int rowIndex = 1;
		
        for (Entry<String, Map<String, Map<String, SaleProfit>>> entry : data.entrySet()) {
        	String day = entry.getKey();
        	Map<String, Map<String, SaleProfit>> dayMap = entry.getValue();
        	for (Entry<String, Map<String, SaleProfit>> dayEntry : dayMap.entrySet()) {
             	String productName = dayEntry.getKey();
             	Map<String, SaleProfit> productMap = dayEntry.getValue();
             	if (productMap.get("total") == null || (productMap.get("total").getAdAmsFee() + productMap.get("total").getAdInEventFee())==0) {
             		continue;
				}
             	row = sheet.createRow(rowIndex);
             	int j=0;
    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(day);
    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(productName);
    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(productMap.get("total").getLine());
             	for (String country : countryList) {
             		if (productMap.get(country) != null) {
             			float fee = productMap.get(country).getAdAmsFee() + productMap.get(country).getAdInEventFee();
             			float volume = productMap.get(country).getAdAmsSalesVolume() + productMap.get(country).getAdInEventSalesVolume();
             			float sales = productMap.get(country).getAdAmsSales() + productMap.get(country).getAdInEventSales();
             			if (fee != 0) {
             				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(fee);
             				row.getCell(j-1).setCellStyle(decimalStyle);
						} else {
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
             			if (volume != 0) {
             				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(volume);
             				row.getCell(j-1).setCellStyle(intStyle);
						} else {
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
             			if (sales != 0) {
             				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sales);
             				row.getCell(j-1).setCellStyle(decimalStyle);
						} else {
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
					} else {
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
            			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
            			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
				}
             	rowIndex++;
        	}
			
			for (int i = 0; i < title.size(); i++) {
			 	sheet.autoSizeColumn((short)i,true);
			}
		}
		
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	        String fileName = saleProfit.getDay()+type+"-"+saleProfit.getEnd()+type+ sdf.format(new Date()) + ".xls";
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
	

	@RequestMapping(value = {"viewWeekReport"})
	public String viewWeekReport(Advertising advertising,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		List<String> weekSet=Lists.newArrayList();
		Date start=advertising.getCreateDate();
		Date end=advertising.getDataDate();
		if(start==null){
			start=DateUtils.addWeeks(new Date(),-3);
			end=DateUtils.addWeeks(new Date(),-1);
			advertising.setCreateDate(start);
			advertising.setDataDate(end);
		}
		Set<String> groupNameSet=Sets.newHashSet();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyww");
		SimpleDateFormat monthFormat=new SimpleDateFormat("yyyyMM");
		Map<String,Map<String,Float>> rateMap=advertisingService.findRate(advertising);
		while(end.after(start)||end.equals(start)){
			String key = dateFormat.format(start);
			int year =DateUtils.getSunday(start).getYear()+1900;
			int week =  Integer.parseInt(key.substring(4));
			if (week == 53) {	//出现53周可能夸年了
				year = DateUtils.getMonday(start).getYear()+1900;
			}
			if(week<10){
				key = year+"0"+week;
			}else{
				key =year+""+week;
			}
			weekSet.add(key);
			start = DateUtils.addWeeks(start,1);
		}
		List<String> totalWeek=Lists.newArrayList();
		if(StringUtils.isNotBlank(advertising.getGroupName())){
			advertising.setGroupName(HtmlUtils.htmlUnescape(advertising.getGroupName()));
		}
		Map<String,Map<String,Advertising>> advertisingMap=advertisingService.findWeekAdvertising2(weekSet,advertising.getCountry(),advertising.getGroupName());
		Map<String,Map<String,Advertising>> totalMap=Maps.newHashMap();
		//查询数据是所有clicks大于等于200  颜色规则  cost/order: us大于等于8  日本大于等于700  conv_nature<=0.05   conv_ads<=0.05   ratio<=0.7
		for (int i=0;i<weekSet.size();i++) {
			if(i+1<weekSet.size()){
				 String week1=weekSet.get(i);
				 String week2=weekSet.get(i+1);
				 totalWeek.add(week1+"-"+week2);
				for (Map.Entry<String,Map<String,Advertising>> map : advertisingMap.entrySet()) {
					String country=map.getKey();
					/*if(rateMap==null||rateMap.get(country)==null){
							continue;
					}*/
					Map<String,Advertising> advMap=map.getValue();
					
					 Integer click=0;
					 String name="";
					 String groupName="";
					 String sku="";
					 Float spend=0f;
					 Integer order=0;
					 String tempKey="";
					 Float sales=0f;
					 Advertising ad1=advMap.get(week1);
					 if(ad1!=null){
						 click+=ad1.getClicks();
						 name=ad1.getName();
						 groupName=ad1.getGroupName();
						 sku=ad1.getSku();
						 spend+=ad1.getTotalSpend();
						 order+=ad1.getSameSkuOrdersPlaced();
						 tempKey=ad1.getCountry();
						 sales+=ad1.getSameSkuOrderSales();
					 }
					 Advertising ad2=advMap.get(week2);
					 if(ad2!=null){
						 click+=ad2.getClicks();
						 name=ad2.getName();
						 groupName=ad2.getGroupName();
						 sku=ad2.getSku();
						 spend+=ad2.getTotalSpend();
						 order+=ad2.getSameSkuOrdersPlaced();
						 tempKey=ad2.getCountry();
						 sales+=ad2.getSameSkuOrderSales();
					 }
					 //if(click>=200){
						    Advertising adv=new Advertising();
							adv.setCountry(tempKey);
							adv.setName(name);
							adv.setGroupName(groupName);
							adv.setSku(sku);
							adv.setTotalSpend(spend);
							adv.setClicks(click);
							adv.setSameSkuOrdersPlaced(order);
							adv.setSameSkuOrderSales(sales);
							String month=monthFormat.format(DateUtils.addMonths(DateUtils.addDays(dateFormat.parse(week2),7), -1));
							if(rateMap!=null&&rateMap.get(month)!=null&&rateMap.get(month).get(country)!=null){
								adv.setConversion(rateMap.get(month).get(country));
							}
						    Map<String,Advertising> temp=totalMap.get(country);
							if(temp==null){
								temp=Maps.newHashMap();
								totalMap.put(country, temp);
							}
							temp.put(week1+"-"+week2,adv);
							groupNameSet.add(groupName);
					// }
				}
			}
		}
		model.addAttribute("totalWeek", totalWeek);
		model.addAttribute("totalMap", totalMap);
		model.addAttribute("advertising",advertising);
		model.addAttribute("groupNameSet",groupNameSet);
		return "modules/amazoninfo/advertisingByWeekGroupNameList";
	}
	
	
	@RequestMapping(value = {"exportWeekReport"})
	public String exportWeekReport(HttpServletRequest request, HttpServletResponse response, Model model) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		
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
		
		
		HSSFCellStyle decimalStyle = wb.createCellStyle();
		decimalStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		
		
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		
		
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
		style1.setFillBackgroundColor(HSSFColor.PINK.index);
		style1.setFillForegroundColor(HSSFColor.PINK.index);
		style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
		style2.setFillBackgroundColor(HSSFColor.ORANGE.index);
		style2.setFillForegroundColor(HSSFColor.ORANGE.index);
		style2.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		
		HSSFCellStyle style3 = wb.createCellStyle();
		style3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
		style3.setFillBackgroundColor(HSSFColor.SKY_BLUE.index);
		style3.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style3.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
			
		HSSFCellStyle cellStyle = wb.createCellStyle();
	    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCell cell = null;
			
		List<String> title = Lists.newArrayList("country","name","group_name","sku","spend","click","order","cost/order","conv_nature","conv_ads","ratio");
		
		Map<String,Float> rateMap=advertisingService.findRate();
		Set<String> weekSet=Sets.newHashSet();
		Date start=DateUtils.addWeeks(new Date(),-1);
		List<String> typeList=Lists.newArrayList();
		for (int i=0;i<2;i++) {
			String key = new SimpleDateFormat("yyyyww").format(start);
			int year =DateUtils.getSunday(start).getYear()+1900;
			int week =  Integer.parseInt(key.substring(4));
			if (week == 53) {	//出现53周可能夸年了
				year = DateUtils.getMonday(start).getYear()+1900;
			}
			if(week<10){
				key = year+"0"+week;
			}else{
				key =year+""+week;
			}
			weekSet.add(key);
			start = DateUtils.addWeeks(start,-1);
			typeList.add(key);
		}
		Map<String,List<Advertising>> advertisingMap=advertisingService.findWeekAdvertising(weekSet);
		
        for (Map.Entry<String,List<Advertising>> report : advertisingMap.entrySet()) {
        	String country=report.getKey();
        	HSSFSheet sheet = wb.createSheet("com".equals(country)?"US":country.toUpperCase());
    		HSSFRow row = sheet.createRow(0);
    		row.setHeight((short) 600);
    		for(int i=0;i<title.size();i++){
    			cell = row.createCell(i);
    			cell.setCellStyle(style);
    			cell.setCellValue(title.get(i));
    		}
    		int rowInx=1;
        	List<Advertising> advList=report.getValue();
        	for (Advertising advertising : advList) {
        		String key=country+advertising.getName()+advertising.getGroupName()+advertising.getSku();
        		if(rateMap.get(key)!=null){
        			row = sheet.createRow(rowInx++);
        			int j=0;
        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"US":country.toUpperCase());
        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(advertising.getName());
        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(advertising.getGroupName());
        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(advertising.getSku());
        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(advertising.getTotalSpend());
        			row.getCell(j-1).setCellStyle(decimalStyle);
        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(advertising.getClicks());
        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(advertising.getSameSkuOrdersPlaced());
        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(advertising.getSameSkuOrderSales());
        			
        			if(advertising.getSameSkuOrderSales()>=700&&"jp".equals(country)){
        				row.getCell(j-1).setCellStyle(style1);
        			}else if(advertising.getSameSkuOrderSales()>=8&&!"jp".equals(country)){
        				row.getCell(j-1).setCellStyle(style1);
        			}else{
        				row.getCell(j-1).setCellStyle(decimalStyle);
        			}
        			
        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rateMap.get(key));
        			if(rateMap.get(key)<=0.05){
        				row.getCell(j-1).setCellStyle(style1);
        			}else{
        				row.getCell(j-1).setCellStyle(decimalStyle);
        			}
        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(advertising.getConversion());
        			if(advertising.getConversion()<=0.05){
        				row.getCell(j-1).setCellStyle(style2);
        			}else{
        				row.getCell(j-1).setCellStyle(decimalStyle);
        			}
        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(advertising.getConversion()/rateMap.get(key));
        			if(advertising.getConversion()/rateMap.get(key)<=0.7){
        				row.getCell(j-1).setCellStyle(style3);
        			}else{
        				row.getCell(j-1).setCellStyle(decimalStyle);
        			}
        			
        		}
        		
			}
			
			for (int i = 0; i < title.size(); i++) {
			   	   sheet.autoSizeColumn((short)i,true);
			}
		}
		
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
	        String fileName = typeList.get(1)+"-"+typeList.get(0)+ ".xls";
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
	
	//分产品导出广告数据
	@RequestMapping(value = "exportByProduct")
	public String exportByProduct(Advertising advertising, HttpServletRequest request,HttpServletResponse response, Model model) {
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
		contentStyle.setWrapText(true);
		
		//取整数显示
		HSSFCellStyle intStyle = wb.createCellStyle();
		intStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		intStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		intStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		intStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		intStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		intStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		intStyle.setRightBorderColor(HSSFColor.BLACK.index);
		intStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		intStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		//两位小数显示
		HSSFCellStyle decimalStyle = wb.createCellStyle();
		decimalStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		decimalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setRightBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		//两位小数显示
		HSSFCellStyle percentageStyle = wb.createCellStyle();
		percentageStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
		percentageStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		percentageStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		percentageStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		percentageStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		percentageStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		percentageStyle.setRightBorderColor(HSSFColor.BLACK.index);
		percentageStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		percentageStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCell cell = null;

		String currency = "(EUR)";
		if ("com".equals(advertising.getCountry())) {
			currency = "(USD)";
		} else if ("ca".equals(advertising.getCountry())) {
			currency = "(CAD)";
		} else if ("jp".equals(advertising.getCountry())) {
			currency = "(JPY)";
		} else if ("uk".equals(advertising.getCountry())) {
			currency = "(GBP)";
		}
		
		List<String> title = Lists.newArrayList("产品名称", "产品线", "CPO"+currency+"", "广告订单数", "Spend"+currency+"", "Acos", 
				"Sessions", "Clicks", "自然转化率", "广告转化率");
		Map<String, String> nameTypeMap = psiProductService.findProductTypeMap();
		Map<String, String> typeLineMap = groupDictService.getTypeLine(null);
		Map<String, Advertising> map = advertisingService.countByProduct(advertising, saleProfitService.findSkuNames());
		Map<String, BusinessReport> sessionMap = advertisingService.countSessionByProduct(advertising, saleProfitService.getProductNameByAsin());
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int rowIndex = 1;
		for (Entry<String, Advertising> entry : map.entrySet()) {
			int j = 0;
			row = sheet.createRow(rowIndex);
			row.setHeight((short) 400);
			String productName = entry.getKey();
			String type = nameTypeMap.get(productName);
			Advertising ad = entry.getValue();
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(productName);
			sheet.getRow(rowIndex).getCell(j-1).setCellStyle(contentStyle);
			if (StringUtils.isNotEmpty(type) && typeLineMap.get(type.toLowerCase()) != null) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeLineMap.get(type.toLowerCase())+"线");
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			sheet.getRow(rowIndex).getCell(j-1).setCellStyle(contentStyle);
			if (ad.getOrdersPlaced() > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ad.getTotalSpend()/ad.getOrdersPlaced());
				sheet.getRow(rowIndex).getCell(j-1).setCellStyle(decimalStyle);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				sheet.getRow(rowIndex).getCell(j-1).setCellStyle(contentStyle);
			}
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ad.getOrdersPlaced());
			sheet.getRow(rowIndex).getCell(j-1).setCellStyle(intStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ad.getTotalSpend());
			sheet.getRow(rowIndex).getCell(j-1).setCellStyle(decimalStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ad.getAcos()/100);
			sheet.getRow(rowIndex).getCell(j-1).setCellStyle(percentageStyle);
			Integer sessions = 0;
			if (sessionMap.get(productName) != null) {
				sessions = sessionMap.get(productName).getSessions();
			}
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sessions);
			sheet.getRow(rowIndex).getCell(j-1).setCellStyle(intStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ad.getClicks());
			sheet.getRow(rowIndex).getCell(j-1).setCellStyle(intStyle);
			if (sessions > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sessionMap.get(productName).getOrdersPlaced()/(float)sessions);
				sheet.getRow(rowIndex).getCell(j-1).setCellStyle(percentageStyle);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				sheet.getRow(rowIndex).getCell(j-1).setCellStyle(contentStyle);
			}
			if (ad.getClicks() > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ad.getOrdersPlaced()/(float)ad.getClicks());
				sheet.getRow(rowIndex).getCell(j-1).setCellStyle(percentageStyle);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				sheet.getRow(rowIndex).getCell(j-1).setCellStyle(contentStyle);
			}
			rowIndex++;
		}

		for (int i = 0; i < title.size(); i++) {
			if (i == 0) {
				sheet.setColumnWidth(i, 6600);
			} else if (i < 8) {
				sheet.setColumnWidth(i, 3600);
			} else {
				sheet.setColumnWidth(i, 4600);
			}
		}

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String start = sdf.format(advertising.getCreateDate());
			String end = sdf.format(advertising.getDataDate());
			String country = SystemService.countryNameMap.get(advertising.getCountry());
			String fileName = country+start + "~" + end +"分产品广告数据报表.xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("分产品统计广告报表异常", e);
		}
		return null;
	}
	
	//分产品导出广告数据
    @RequestMapping(value = "findAdvertising")
    public String findAdvertising(Advertising advertising, HttpServletRequest request,HttpServletResponse response, Model model) {
        List<Advertising> findAdvertising = advertisingService.findAdvertising();
        return null;
    }
    
    
    @RequestMapping(value = "priceExport")
    public String priceExport(String keyword,String country,String updateTime,LcPsiLadingBill psiLadingBill,
            HttpServletRequest request, HttpServletResponse response,
            Model model) throws UnsupportedEncodingException {
        int excelNo = 1;
        Calendar c = Calendar.getInstance();
        String day = "";
        try {
            c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(updateTime));
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if("de,fr,uk,it,es,de,ca".contains(country)){
            c.add(Calendar.DATE, - 2);
            Date d = c.getTime();
            day = new SimpleDateFormat("yyyy-MM-dd").format(d);
        }else if("jp".equals(country)){
            c.add(Calendar.DATE, - 1);
            Date d = c.getTime();
            day = new SimpleDateFormat("yyyy-MM-dd").format(d);
        }else{
            day = DateUtils.getDate("yyyy-MM-dd");
        }
        List<Object[]> list = advertisingService.priceExport(country,day,keyword);
        Map<String, PsiProduct> map = Maps.newHashMap();;

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow row = sheet.createRow(0);

        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        sheet.autoSizeColumn((short)0); //调整第一列宽度
        sheet.autoSizeColumn((short)1); //调整第二列宽度
        sheet.autoSizeColumn((short)2); //调整第三列宽度
        sheet.autoSizeColumn((short)10);
        String[] title = { "country ", "campaign_name", "ad_group_name ", "customer_search_term", "keyword",
                "match_type", "impressions", "clicks", "total_spend", "same_sku","update_time"};

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
        row.setHeight((short) 500);
        HSSFCell cell = null;
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
            sheet.autoSizeColumn((short) i);
        }
        for (Object[] obj : list) {

            row = sheet.createRow(excelNo++);
            row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(obj[1]!=null?obj[1].toString():"");
            row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(obj[2]!=null?obj[2].toString():""); 
            row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(obj[3]!=null?obj[3].toString():"");
            row.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(obj[4]!=null?obj[4].toString():"");
            row.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(obj[5]!=null?obj[5].toString():"");
            row.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(obj[6]!=null?obj[6].toString():"");
            row.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[7]!=null?obj[7].toString():""));
            row.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[8]!=null?obj[8].toString():""));
            row.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(Double.parseDouble(obj[9]!=null?obj[9].toString():""));
            row.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[10]!=null?obj[10].toString():""));
            row.createCell(10, Cell.CELL_TYPE_STRING).setCellValue(obj[11]!=null?obj[11].toString():"");
        }

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");

        SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
        String fileName = "日数据导出" + sdf.format(new Date()) + ".xls";
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename="
                + fileName);
        try {
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    @RequestMapping(value = "negativeExport")
    public String negativeExport(String platform, HttpServletRequest request, HttpServletResponse response,Model model) throws UnsupportedEncodingException {
        int excelNo = 1;
       
        List<Object[]> list = advertisingService.findNegativeKeyword(platform);
      
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow row = sheet.createRow(0);

        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        sheet.autoSizeColumn((short)0); //调整第一列宽度
        sheet.autoSizeColumn((short)1); //调整第二列宽度
        sheet.autoSizeColumn((short)2); //调整第三列宽度
        sheet.autoSizeColumn((short)10);
        String[] title = {"campaign_name", "ad_group_name ","keyword"};

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
        row.setHeight((short) 500);
        HSSFCell cell = null;
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
            sheet.autoSizeColumn((short) i);
        }
        if(list!=null){
        	for (Object[] obj : list) {

                row = sheet.createRow(excelNo++);
                row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(obj[0]!=null?obj[0].toString():"");
                row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(obj[1]!=null?obj[1].toString():""); 
                row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(obj[2]!=null?obj[2].toString():"");
            }
        }
        

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");

        SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
        String fileName = "negative" + sdf.format(new Date()) + ".xls";
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename="
                + fileName);
        try {
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
