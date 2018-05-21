/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseGoal;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseTotalGoal;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.entity.SaleReportMonthType;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseGoalService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportMonthTypeService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;


@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/enterpriseGoal")
public class EnterpriseGoalController extends BaseController {
	
	@Autowired
	private EnterpriseGoalService enterpriseGoalService;
	
	@Autowired
	private PsiProductTypeGroupDictService dictService;
	
	@Autowired
	private SaleReportService saleReportService;

	@Autowired
	private SaleReportMonthTypeService saleReportMonthTypeService;
	
	public static Map<String, Integer> rs = Maps.newHashMap();
	
	static{
		//2016年度调整后各月总目标
		rs.put("201601", 2672222);
		rs.put("201602", 2238889);
		rs.put("201603", 2563889);
		rs.put("201604", 2419444);
		rs.put("201605", 2238889);
		rs.put("201606", 2455556);
		rs.put("201607", 2925000);
		rs.put("201608", 3105556);
		rs.put("201609", 3177778);
		rs.put("201610", 3250000);
		rs.put("201611", 3936111);
		rs.put("201612", 5200000);

		//2017年度各月总目标
		rs.put("201701", 2970750);
		rs.put("201702", 2470750);
		rs.put("201703", 2568750);
		rs.put("201704", 2840595);
		rs.put("201705", 3112076);
		rs.put("201706", 3599902);
		rs.put("201707", 4859809);
		rs.put("201708", 5083587);
		rs.put("201709", 5824482);
		rs.put("201710", 7043035);
		rs.put("201711", 9662924);
		rs.put("201712", 12673466);

		//2018年度各月总目标(不含美国)
		rs.put("201801", 2332750);
		rs.put("201802", 1898175);
		rs.put("201803", 2062189);
		rs.put("201804", 1923913);
		rs.put("201805", 1882365);
		rs.put("201806", 1769631);
		rs.put("201807", 2269618);
		rs.put("201808", 2161348);
		rs.put("201809", 2498881);
		rs.put("201810", 2593134);
		rs.put("201811", 2906566);
		rs.put("201812", 3261432);
	}
	
	@RequestMapping(value = "")
	public String goal(EnterpriseGoal enterpriseGoal, RedirectAttributes redirectAttributes, Model model,HttpServletRequest request) {
		String startMonth=request.getParameter("startMonth");
		String endMonth=request.getParameter("endMonth");
		if (StringUtils.isEmpty(startMonth)) {
			startMonth = "201801";
			endMonth = "201812";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");                
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
		try {
			if(StringUtils.isBlank(endMonth)){
				enterpriseGoal.setEndMonth(calendar.getTime());
			}else{
				enterpriseGoal.setEndMonth(sdf.parse(endMonth));
			}
			
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-7);
			if(StringUtils.isBlank(startMonth)){
				enterpriseGoal.setStartMonth(calendar.getTime());
			}else{
				enterpriseGoal.setStartMonth(sdf.parse(startMonth));
			}
			
			if(StringUtils.isBlank(enterpriseGoal.getCountry())){
				enterpriseGoal.setCountry("de");
			}
			Date start = enterpriseGoal.getStartMonth();
			Date end = enterpriseGoal.getEndMonth();
			Map<String,String> data=Maps.newHashMap();
			//Date d=new Date();
			while(end.after(start)||end.equals(start)){
				String key = sdf.format(start);
				//String curMonth=sdf.format(d);
				//if(key.equals(curMonth) && d.getDate()>1 && d.getDate() <= 10){	//1号系统生成数据,2-10号可调整
					//data.put(key,"1");	//1:可编辑  0：不可编辑
					data.put(key,"0");	//年初统一制定目标，不再编辑
				//}else{
				//	data.put(key,"0");
				//}
				start = DateUtils.addMonths(start, 1);
			}
			Map<String,Map<String,Object[]>>  map=enterpriseGoalService.findMonthGoal(enterpriseGoal);
			Map<String,String> allLine=dictService.getProductLine();
			for (Map.Entry<String,String> entry : allLine.entrySet()) { 
		        String key = entry.getKey();
				String value = entry.getValue();
				allLine.put(key, value.substring(0, 1));
			}
			model.addAttribute("map", map);
			model.addAttribute("data", data);
			model.addAttribute("allLine", allLine);
			model.addAttribute("enterpriseGoal",enterpriseGoal);

			//[国家[产品线[月 /销售额]]]
			Map<String,Map<String,Map<String, SaleReport>>> saleData = saleReportService.getCountryLineSales(startMonth, endMonth, enterpriseGoal.getCountry());
			model.addAttribute("saleData", saleData);
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		return "modules/amazoninfo/salesGoalList";
	}
	
	
	@RequestMapping(value = "countryGoal")
	public String countryGoal(EnterpriseGoal enterpriseGoal, RedirectAttributes redirectAttributes, Model model,HttpServletRequest request) {
		String startMonth=request.getParameter("startMonth");
		String endMonth=request.getParameter("endMonth");
		if (StringUtils.isEmpty(startMonth)) {
			startMonth = "201801";
			endMonth = "201812";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");                
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
		try {
			if(StringUtils.isBlank(endMonth)){
				enterpriseGoal.setEndMonth(calendar.getTime());
			}else{
				enterpriseGoal.setEndMonth(sdf.parse(endMonth));
			}
			
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-7);
			if(StringUtils.isBlank(startMonth)){
				enterpriseGoal.setStartMonth(calendar.getTime());
			}else{
				enterpriseGoal.setStartMonth(sdf.parse(startMonth));
			}
			
			Date start = enterpriseGoal.getStartMonth();
			Date end = enterpriseGoal.getEndMonth();
			Map<String,String> data=Maps.newHashMap();
			//Date d=new Date();
			while(end.after(start)||end.equals(start)){
				String key = sdf.format(start);
				//String curMonth=sdf.format(d);
				//if(key.equals(curMonth) && d.getDate()>5 && d.getDate() <= 10){	//5号系统生成数据,6-10号可调整
					//data.put(key,"1");
					data.put(key,"0");	//关闭国家目标编辑功能，直接调整产品类型目标
				//}else{
				//	data.put(key,"0");
				//}
				start = DateUtils.addMonths(start, 1);
			}
			Map<String,Map<String, EnterpriseTotalGoal>>  map= enterpriseGoalService.findMonthTotalGoal(enterpriseGoal);
			model.addAttribute("map", map);
			model.addAttribute("data", data);
			model.addAttribute("enterpriseGoal",enterpriseGoal);
			String saleEndMonth = sdf.format(DateUtils.addMonths(new Date(), -1));
			Map<String,Map<String, Float>> sales = saleReportMonthTypeService.getSalesByMonth(startMonth, saleEndMonth);
			model.addAttribute("sales", sales);
			Map<String, Float> change =  Maps.newHashMap();
			for (Map.Entry<String, Map<String, Float>>  entry : sales.entrySet()) { 
			    String month =entry.getKey();
				Map<String, Float> countryMap = entry.getValue();
				for (Map.Entry<String, Float> entryRs : countryMap.entrySet()) { 
				    String country =entryRs.getKey();
					float sale = entryRs.getValue();
					float goal = 0f;
					try {
						goal = map.get(month).get(country).getGoal();
					} catch (NullPointerException e) {}
					if (change.get(country) == null) {
						change.put(country, sale - goal);
					} else {
						change.put(country, change.get(country) + sale - goal);
					}
				}
			}
			model.addAttribute("change", change);
			
			//年度初始月总目标
			model.addAttribute("rs", rs);
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		return "modules/amazoninfo/salesTotalGoalList";
	}
	
	@RequestMapping(value = "findMonthLineGoal")
	public String findMonthLineGoal(EnterpriseGoal enterpriseGoal, RedirectAttributes redirectAttributes, Model model,HttpServletRequest request) {
		String startMonth=request.getParameter("startMonth");
		String endMonth=request.getParameter("endMonth");
		if (StringUtils.isEmpty(startMonth)) {
			startMonth = "201801";
			endMonth = "201812";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
		try {
			if(StringUtils.isBlank(endMonth)){
				enterpriseGoal.setEndMonth(calendar.getTime());
			}else{
				enterpriseGoal.setEndMonth(sdf.parse(endMonth));
			}
			
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-7);
			if(StringUtils.isBlank(startMonth)){
				enterpriseGoal.setStartMonth(calendar.getTime());
			}else{
				enterpriseGoal.setStartMonth(sdf.parse(startMonth));
			}
			Map<String,Map<String,Float>>  map=enterpriseGoalService.findMonthLineGoal(enterpriseGoal);
			Map<String,String> allLine=dictService.getProductLine();
			for (Map.Entry<String,String> entry : allLine.entrySet()) { 
			    String key =entry.getKey();
				String value = entry.getValue();
				allLine.put(key, value.substring(0, 1));
			}
			//allLine.put("ungrouped", "UnGrouped");
			model.addAttribute("map", map);
			model.addAttribute("allLine", allLine);
			model.addAttribute("enterpriseGoal",enterpriseGoal);

			//[国家[产品线[月 /销售额]]]
			Map<String,Map<String,Map<String, SaleReport>>> saleData = saleReportService.getCountryLineSales(startMonth, endMonth, enterpriseGoal.getCountry());
			model.addAttribute("saleData", saleData);
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		return "modules/amazoninfo/salesGoalLineList";
	}
	
	
	@RequestMapping(value = "addMonthGoal")
	public String addMonthGoal(EnterpriseGoal enterpriseGoal, RedirectAttributes redirectAttributes, Model model,HttpServletRequest request) {
		String startMonth=request.getParameter("startMonth");
		List<EnterpriseGoal> enterpriseGoals=Lists.newArrayList();
		Map<String,String> allLine=dictService.getProductLine();
		for (String key: allLine.keySet()) {
			EnterpriseGoal goal=new EnterpriseGoal();
			goal.setCreateDate(new Date());
			goal.setCreateUser(UserUtils.getUser());
			goal.setMonth(startMonth);
			PsiProductTypeGroupDict productLine=new PsiProductTypeGroupDict();
			productLine.setId(key);
			goal.setProductLine(productLine);
			goal.setCountry(enterpriseGoal.getCountry());
			enterpriseGoals.add(goal);
		}
		enterpriseGoalService.save(enterpriseGoals);
		model.addAttribute("enterpriseGoal", enterpriseGoal);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/enterpriseGoal/";
	}
	
	//更新产品线目标
	@ResponseBody
	@RequestMapping(value = {"updateGoal"})
	public String updateGoal(EnterpriseGoal enterpriseGoal,String lineId) {
		//2017各国家产品线利润率
		Map<String, Map<String, Float>> countryProfitRatio = EnterpriseGoalService.countryLineProfitRatio();
		Map<String,String> allLine=dictService.getProductLine();
		enterpriseGoal.setCreateDate(new Date());
		enterpriseGoal.setCreateUser(UserUtils.getUser());
		PsiProductTypeGroupDict productLine=new PsiProductTypeGroupDict();
		productLine.setId(lineId);
		enterpriseGoal.setProductLine(productLine);
		float profitRatio = 0;
		if ("com,ca,uk".contains(enterpriseGoal.getCountry())) {
			profitRatio = countryProfitRatio.get("en").get(allLine.get(lineId).substring(0, 1));
		} else {
			profitRatio = countryProfitRatio.get("nonEn").get(allLine.get(lineId).substring(0, 1));
		}
		enterpriseGoal.setProfitGoal(enterpriseGoal.getGoal() * profitRatio);
		enterpriseGoalService.save(enterpriseGoal);
		//更新对应国家销售目标和利润
		enterpriseGoalService.updateCountryTotalGoal(enterpriseGoal);
		/*
		//查询系统计算出的总目标,查询当前数据总目标,进行比对
		PsiProductTypeGroupDict dict = dictService.get(lineId);
		if (dict.getName().equals("E 产品线")) {
			enterpriseGoal.setCreateDate(new Date());
			enterpriseGoal.setCreateUser(UserUtils.getUser());
			PsiProductTypeGroupDict productLine=new PsiProductTypeGroupDict();
			productLine.setId(lineId);
			enterpriseGoal.setProductLine(productLine);
			enterpriseGoalService.save(enterpriseGoal);
		} else {
			float targetGoal = enterpriseGoalService.findTargetGoalByMonth(enterpriseGoal.getMonth());
			Float monthGoal = enterpriseGoalService.getTotalLineGoalByMonth(enterpriseGoal.getMonth(), enterpriseGoal.getCountry(), lineId);
			if (monthGoal != null && (monthGoal + enterpriseGoal.getGoal()) < targetGoal) {
				return "更新目标失败,填报月度总目标("+(monthGoal + enterpriseGoal.getGoal())+")小于计划目标值："+targetGoal;
			}
			enterpriseGoal.setCreateDate(new Date());
			enterpriseGoal.setCreateUser(UserUtils.getUser());
			PsiProductTypeGroupDict productLine=new PsiProductTypeGroupDict();
			productLine.setId(lineId);
			enterpriseGoal.setProductLine(productLine);
			enterpriseGoalService.save(enterpriseGoal);
			//更新对应国家目标
			enterpriseGoalService.updateCountryGoalByLine(enterpriseGoal);
		}*/
		
		return "更新目标成功！";
	}
	
	//更新月度总目标
	@ResponseBody
	@RequestMapping(value = {"updateAllGoal"})
	public String updateAllGoal(String month ,String goal) {
		if (goal.contains(",")) {
			goal = goal.replaceAll(",", "");
		}
		Map<String, Integer> goalMap = EnterpriseGoalService.monthGoalMap(month.substring(0, 4));
		Integer initMonthGoal = goalMap.get(month);	//当月既定目标
		try {
			Integer monthGoal = Integer.parseInt(goal);
			//拿到总目标分配到国家和产品线,总目标不能低于原定总目标
			if (initMonthGoal != null && monthGoal < initMonthGoal) {
				return "更新目标失败,目标不得小于计划目标值："+initMonthGoal;
			}
			enterpriseGoalService.updateAllGoal(month, monthGoal);
			return "更新目标成功！";
		} catch (Exception e) {
			logger.error("更新月度目标失败", e);
			return "更新目标失败！";
		}
	}
	
	//更新国家月度目标
	@ResponseBody
	@RequestMapping(value = {"updateTotalGoal"})
	public String updateTotalGoal(EnterpriseTotalGoal enterpriseTotalGoal) {
		//查询系统计算出的总目标,查询当前数据总目标,进行比对
		Float targetGoal = enterpriseGoalService.findTargetGoalByMonth(enterpriseTotalGoal.getMonth());
		float monthGoal = enterpriseGoalService.getTotalGoalByMonth(enterpriseTotalGoal.getMonth(), enterpriseTotalGoal.getCountry());
		if (targetGoal != null && (monthGoal + enterpriseTotalGoal.getGoal()) < targetGoal) {
			return "更新目标失败,填报月度总目标("+(monthGoal + enterpriseTotalGoal.getGoal())+")小于计划目标值："+targetGoal;
		}
		enterpriseTotalGoal.setCreateDate(new Date());
		enterpriseTotalGoal.setCreateUser(UserUtils.getUser());
		//保存并按比例更新产品线目标
		enterpriseGoalService.updateCountryGoal(enterpriseTotalGoal);
		return "更新目标成功";
	}
	
	@ResponseBody
	@RequestMapping(value = {"isExist"})
	public String isExist(EnterpriseGoal enterpriseGoal) {
		return this.enterpriseGoalService.findByCountryMonth(enterpriseGoal);
	}
	
	
	//======================================
	//saveOrEdit
	@RequestMapping(value = "saveOrEdit")
	public String saveOrEdit(EnterpriseGoal enterpriseGoal, RedirectAttributes redirectAttributes, Model model) {
		enterpriseGoal.setCreateDate(new Date());
		enterpriseGoal.setCreateUser(UserUtils.getUser());
		enterpriseGoalService.save(enterpriseGoal);
		model.addAttribute("enterpriseGoal", enterpriseGoal);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/enterpriseGoal/";
	}
	@RequestMapping(value = "save")
	public String save(EnterpriseGoal enterpriseGoal, RedirectAttributes redirectAttributes, Model model) {
		if(enterpriseGoal.getId()!=null){
			model.addAttribute("enterpriseGoal", enterpriseGoalService.get(enterpriseGoal.getId()));
		}else{
			Calendar calendar = Calendar.getInstance();
			String month=new SimpleDateFormat("yyyyMM").format(calendar.getTime());
			enterpriseGoal.setMonth(month);
			model.addAttribute("enterpriseGoal", enterpriseGoal);
		}
		return "modules/amazoninfo/salesGoalAdd";
	}
	
	//分产品线目标&销售额导出
	@RequestMapping(value = "exportLineData")
	public String exportLineData(EnterpriseGoal enterpriseGoal,HttpServletRequest request,HttpServletResponse response, Model model) throws ParseException {
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
		contentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setWrapText(true);

		CellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		contentStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		contentStyle1.setWrapText(true);
		HSSFCell cell = null;
		
		String startMonth=request.getParameter("startMonth");
		String endMonth=request.getParameter("endMonth");
		if (StringUtils.isEmpty(startMonth)) {
			startMonth = "201601";
			endMonth = "201612";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");                
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
		if(StringUtils.isBlank(endMonth)){
			enterpriseGoal.setEndMonth(calendar.getTime());
		}else{
			enterpriseGoal.setEndMonth(sdf.parse(endMonth));
		}
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-7);
		if(StringUtils.isBlank(startMonth)){
			enterpriseGoal.setStartMonth(calendar.getTime());
		}else{
			enterpriseGoal.setStartMonth(sdf.parse(startMonth));
		}
		Date start = enterpriseGoal.getStartMonth();
		Date end = enterpriseGoal.getEndMonth();
		List<String> dataList = Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			String key = sdf.format(start);
			dataList.add(key);
			start = DateUtils.addMonths(start, 1);
		}
		Map<String,Map<String,Object[]>>  map = enterpriseGoalService.findMonthGoal(enterpriseGoal);
		Map<String,String> allLine=dictService.getProductLine();
		for (Map.Entry<String,String> entry : allLine.entrySet()) { 
	        String key = entry.getKey();
			String value = entry.getValue();
			allLine.put(key, value.substring(0, 1));
		}
		String country = enterpriseGoal.getCountry();
		//[国家[产品线[月 /销售额]]]
		Map<String,Map<String,Map<String, SaleReport>>> saleData = saleReportService.getCountryLineSales(startMonth, endMonth, country);
		
		List<String> title = Lists.newArrayList("日期");
		for (Map.Entry<String,String> entry : allLine.entrySet()) { 
			String lineName = entry.getValue();
			//if (!"E".equals(lineName)) {
				title.add(lineName + " 产品线(€)");
				title.add("销售额(€)");
			//}
		}
		title.add("总目标(€)");
		title.add("总销售额(€)");
		//title.add("E 产品线(€)");
		//title.add("销售额(€)");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Map<String, Map<String, Float>> totalMap = Maps.newHashMap();
		int rowIndex = 1;
		for (int i = 0; i < dataList.size(); i++) {
			String month = dataList.get(i);
			int j = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			float totalGoal = 0f;
			float totalSales = 0f;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(month);
			for (Map.Entry<String,String> entry : allLine.entrySet()) { 
			    String lineId =entry.getKey();
				String lineName = entry.getValue();
				//if (!"E".equals(lineName)) {	//E线单独计算
					float goal = 0;
					try {
						goal = ((BigDecimal) map.get(month).get(lineId)[4]).floatValue();
					} catch (NullPointerException e) {}
					totalGoal += goal;
					float sales = 0f;
					try {
						sales = saleData.get(country).get(lineName).get(month).getSales();
					} catch (NullPointerException e) {}
					totalSales += sales;
					Map<String, Float> lineMap = totalMap.get(lineName);
					if (lineMap == null) {
						lineMap = Maps.newHashMap();
						totalMap.put(lineName, lineMap);
					}
					Float lineGoal = lineMap.get("goal");
					if (lineGoal == null) {
						lineMap.put("goal", goal);
					} else {
						lineMap.put("goal", lineGoal + goal);
					}
					Float lineSales = lineMap.get("sales");
					if (lineSales == null) {
						lineMap.put("sales", sales);
					} else {
						lineMap.put("sales", lineSales + sales);
					}
					if (goal > 1) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(goal);
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					}
					if (sales > 1) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales);
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					}
				//}
			}
			Map<String, Float> lineMap = totalMap.get("total");
			if (lineMap == null) {
				lineMap = Maps.newHashMap();
				totalMap.put("total", lineMap);
			}
			Float lineGoal = lineMap.get("goal");
			if (lineGoal == null) {
				lineMap.put("goal", totalGoal);
			} else {
				lineMap.put("goal", lineGoal + totalGoal);
			}
			Float lineSales = lineMap.get("sales");
			if (lineSales == null) {
				lineMap.put("sales", totalSales);
			} else {
				lineMap.put("sales", lineSales + totalSales);
			}
			//汇总数据
			if (totalGoal > 1) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(totalGoal);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			if (totalSales > 1) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(totalSales);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//E产品线单独处理
			/*float goal = 0;
			try {
				goal = ((BigDecimal) map.get(month).get("e5fb0544516740beaee4670355597bc1")[4]).floatValue();
			} catch (NullPointerException e) {}
			float sales = 0f;
			try {
				sales = saleData.get(country).get("E").get(month).getSales();
			} catch (NullPointerException e) {}

			Map<String, Float> elineMap = totalMap.get("E");
			if (elineMap == null) {
				elineMap = Maps.newHashMap();
				totalMap.put("E", elineMap);
			}
			Float elineGoal = elineMap.get("goal");
			if (lineGoal == null) {
				elineMap.put("goal", goal);
			} else {
				elineMap.put("goal", elineGoal + goal);
			}
			Float elineSales = elineMap.get("sales");
			if (lineSales == null) {
				elineMap.put("sales", sales);
			} else {
				elineMap.put("sales", elineSales + sales);
			}
			if (goal > 1) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(goal);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			if (sales > 1) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}*/
		}
		//总计
		int j = 0;
		row = sheet.createRow(rowIndex++);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("total");
		for (Map.Entry<String,String> entry : allLine.entrySet()) { 
			String lineName = entry.getValue();
			//if (!"E".equals(lineName)) {
				float goal = 0;
				try {
					goal = totalMap.get(lineName).get("goal");
				} catch (NullPointerException e) {}
				float sales = 0f;
				try {
					sales = totalMap.get(lineName).get("sales");
				} catch (NullPointerException e) {}
				if (goal > 1) {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(goal);
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				if (sales > 1) {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales);
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
			//}
		}
		float totalGoal = 0;
		try {
			totalGoal = totalMap.get("total").get("goal");
		} catch (NullPointerException e) {}
		float totalSales = 0f;
		try {
			totalSales = totalMap.get("total").get("sales");
		} catch (NullPointerException e) {}
		//汇总数据
		if (totalGoal > 1) {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(totalGoal);
		} else {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
		}
		if (totalSales > 1) {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(totalSales);
		} else {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
		}
		//E产品线单独处理
		/*float goal = 0;
		try {
			goal = totalMap.get("E").get("goal");
		} catch (NullPointerException e) {}
		float sales = 0f;
		try {
			sales = totalMap.get("E").get("sales");
		} catch (NullPointerException e) {}
		if (goal > 1) {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(goal);
		} else {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
		}
		if (sales > 1) {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales);
		} else {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
		}*/

		for (int i = 1; i < rowIndex; i++) {
			for (int m = 0; m < title.size(); m++) {
				sheet.getRow(i).getCell(m).setCellStyle(contentStyle);
			}
		}

		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i);
		}

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMddhhmm");

			String fileName = country + "分产品线目标" + sdformat.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("分产品线月目标导出异常", e);
		}
		return null;
	}
	
	//全平台合计分产品线目标&销售额导出
	@RequestMapping(value = "exportLineTotalData")
	public String exportLineTotalData(EnterpriseGoal enterpriseGoal,HttpServletRequest request,HttpServletResponse response, Model model) throws ParseException {
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
		contentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setWrapText(true);

		CellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		contentStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		contentStyle1.setWrapText(true);
		HSSFCell cell = null;
		
		String startMonth=request.getParameter("startMonth");
		String endMonth=request.getParameter("endMonth");
		if (StringUtils.isEmpty(startMonth)) {
			startMonth = "201601";
			endMonth = "201612";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");                
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
		if(StringUtils.isBlank(endMonth)){
			enterpriseGoal.setEndMonth(calendar.getTime());
		}else{
			enterpriseGoal.setEndMonth(sdf.parse(endMonth));
		}
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-7);
		if(StringUtils.isBlank(startMonth)){
			enterpriseGoal.setStartMonth(calendar.getTime());
		}else{
			enterpriseGoal.setStartMonth(sdf.parse(startMonth));
		}
		Date start = enterpriseGoal.getStartMonth();
		Date end = enterpriseGoal.getEndMonth();
		List<String> dataList = Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			String key = sdf.format(start);
			dataList.add(key);
			start = DateUtils.addMonths(start, 1);
		}
		Map<String, Map<String, Float>>  map = enterpriseGoalService.findMonthLineGoal(enterpriseGoal);
		Map<String,String> allLine=dictService.getProductLine();
		for (Map.Entry<String,String> entry : allLine.entrySet()) { 
		    String key =entry.getKey();
			String value =entry.getValue();
			allLine.put(key, value.substring(0, 1));
		}

		//[国家[产品线[月 /销售额]]]
		Map<String,Map<String,Map<String, SaleReport>>> saleData = saleReportService.getCountryLineSales(startMonth, endMonth, enterpriseGoal.getCountry());
		
		List<String> title = Lists.newArrayList("日期");
		for (Map.Entry<String,String> entry : allLine.entrySet()) { 
			String lineName = entry.getValue();
			//if (!"E".equals(lineName)) {
				title.add(lineName + " 产品线(€)");
				title.add("销售额(€)");
			//}
		}
		title.add("总目标(€)");
		title.add("总销售额(€)");
		//title.add("E 产品线(€)");
		//title.add("销售额(€)");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Map<String, Map<String, Float>> totalMap = Maps.newHashMap();
		int rowIndex = 1;
		String country = "total";
		for (int i = 0; i < dataList.size(); i++) {
			String month = dataList.get(i);
			int j = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			float totalGoal = 0f;
			float totalSales = 0f;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(month);
			for (Map.Entry<String,String> entry : allLine.entrySet()) { 
				String lineId=entry.getKey();
				String lineName =entry.getValue();
				//if (!"E".equals(lineName)) {	//E线单独计算
					float goal = 0;
					try {
						goal = map.get(month).get(lineId);
					} catch (NullPointerException e) {}
					totalGoal += goal;
					float sales = 0f;
					try {
						sales = saleData.get(country).get(lineName).get(month).getSales();
					} catch (NullPointerException e) {}
					totalSales += sales;
					Map<String, Float> lineMap = totalMap.get(lineName);
					if (lineMap == null) {
						lineMap = Maps.newHashMap();
						totalMap.put(lineName, lineMap);
					}
					Float lineGoal = lineMap.get("goal");
					if (lineGoal == null) {
						lineMap.put("goal", goal);
					} else {
						lineMap.put("goal", lineGoal + goal);
					}
					Float lineSales = lineMap.get("sales");
					if (lineSales == null) {
						lineMap.put("sales", sales);
					} else {
						lineMap.put("sales", lineSales + sales);
					}
					if (goal > 1) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(goal);
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					}
					if (sales > 1) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales);
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					}
				//}
			}
			Map<String, Float> lineMap = totalMap.get("total");
			if (lineMap == null) {
				lineMap = Maps.newHashMap();
				totalMap.put("total", lineMap);
			}
			Float lineGoal = lineMap.get("goal");
			if (lineGoal == null) {
				lineMap.put("goal", totalGoal);
			} else {
				lineMap.put("goal", lineGoal + totalGoal);
			}
			Float lineSales = lineMap.get("sales");
			if (lineSales == null) {
				lineMap.put("sales", totalSales);
			} else {
				lineMap.put("sales", lineSales + totalSales);
			}
			//汇总数据
			if (totalGoal > 1) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(totalGoal);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			if (totalSales > 1) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(totalSales);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			/*//E产品线单独处理
			float goal = 0;
			try {
				goal = map.get(month).get("e5fb0544516740beaee4670355597bc1");
			} catch (NullPointerException e) {}
			float sales = 0f;
			try {
				sales = saleData.get(country).get("E").get(month).getSales();
			} catch (NullPointerException e) {}

			Map<String, Float> elineMap = totalMap.get("E");
			if (elineMap == null) {
				elineMap = Maps.newHashMap();
				totalMap.put("E", elineMap);
			}
			Float elineGoal = elineMap.get("goal");
			if (lineGoal == null) {
				elineMap.put("goal", goal);
			} else {
				elineMap.put("goal", elineGoal + goal);
			}
			Float elineSales = elineMap.get("sales");
			if (lineSales == null) {
				elineMap.put("sales", sales);
			} else {
				elineMap.put("sales", elineSales + sales);
			}
			if (goal > 1) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(goal);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			if (sales > 1) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}*/
		}
		//总计
		int j = 0;
		row = sheet.createRow(rowIndex++);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("total");
		for (Map.Entry<String,String> entry : allLine.entrySet()) { 
			String lineName = entry.getValue();
			//if (!"E".equals(lineName)) {
				float goal = 0;
				try {
					goal = totalMap.get(lineName).get("goal");
				} catch (NullPointerException e) {}
				float sales = 0f;
				try {
					sales = totalMap.get(lineName).get("sales");
				} catch (NullPointerException e) {}
				if (goal > 1) {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(goal);
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				if (sales > 1) {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales);
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
			//}
		}
		float totalGoal = 0;
		try {
			totalGoal = totalMap.get("total").get("goal");
		} catch (NullPointerException e) {}
		float totalSales = 0f;
		try {
			totalSales = totalMap.get("total").get("sales");
		} catch (NullPointerException e) {}
		//汇总数据
		if (totalGoal > 1) {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(totalGoal);
		} else {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
		}
		if (totalSales > 1) {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(totalSales);
		} else {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
		}
		//E产品线单独处理
		/*float goal = 0;
		try {
			goal = totalMap.get("E").get("goal");
		} catch (NullPointerException e) {}
		float sales = 0f;
		try {
			sales = totalMap.get("E").get("sales");
		} catch (NullPointerException e) {}
		if (goal > 1) {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(goal);
		} else {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
		}
		if (sales > 1) {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales);
		} else {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
		}*/

		for (int i = 1; i < rowIndex; i++) {
			for (int m = 0; m < title.size(); m++) {
				sheet.getRow(i).getCell(m).setCellStyle(contentStyle);
			}
		}

		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i);
		}

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMddhhmm");
			String name = "全平台汇总分产品线目标";
			if (StringUtils.isNotEmpty(enterpriseGoal.getCountry()) && "notEn".equals(enterpriseGoal.getCountry())) {
				name = "非英语国家汇总分产品线目标";
			}
			if (StringUtils.isNotEmpty(enterpriseGoal.getCountry()) && "en".equals(enterpriseGoal.getCountry())) {
				name = "英语国家汇总分产品线目标";
			}
			String fileName = name + sdformat.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("平台汇总分产品线月目标导出异常", e);
		}
		return null;
	}
	
	//全平台分国家目标&销售额导出
	@RequestMapping(value = "exportCountryData")
	public String exportCountryData(EnterpriseGoal enterpriseGoal,HttpServletRequest request,HttpServletResponse response, Model model) throws ParseException {
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
		contentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setWrapText(true);

		//高亮显示取整
		HSSFCellStyle colorIntStyle = wb.createCellStyle();
		colorIntStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		colorIntStyle.setFillBackgroundColor(HSSFColor.YELLOW.index);
		colorIntStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		colorIntStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		colorIntStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		colorIntStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		colorIntStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		colorIntStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		colorIntStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		colorIntStyle.setRightBorderColor(HSSFColor.BLACK.index);
		colorIntStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		colorIntStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCell cell = null;
		
		String startMonth=request.getParameter("startMonth");
		String endMonth=request.getParameter("endMonth");
		if (StringUtils.isEmpty(startMonth)) {
			startMonth = "201601";
			endMonth = "201612";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");                
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
		if(StringUtils.isBlank(endMonth)){
			enterpriseGoal.setEndMonth(calendar.getTime());
		}else{
			enterpriseGoal.setEndMonth(sdf.parse(endMonth));
		}
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-7);
		if(StringUtils.isBlank(startMonth)){
			enterpriseGoal.setStartMonth(calendar.getTime());
		}else{
			enterpriseGoal.setStartMonth(sdf.parse(startMonth));
		}
		Date start = enterpriseGoal.getStartMonth();
		Date end = enterpriseGoal.getEndMonth();
		List<String> dataList = Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			String key = sdf.format(start);
			dataList.add(key);
			start = DateUtils.addMonths(start, 1);
		}
		Map<String,Map<String, EnterpriseTotalGoal>>  map = enterpriseGoalService.findMonthTotalGoal(enterpriseGoal);

		//[国家[产品线[月 /销售额]]]
		//Map<String,Map<String,Map<String, SaleReport>>> saleData = saleReportService.getCountryLineSales(startMonth, endMonth, null);
		
		String saleEndMonth = sdf.format(DateUtils.addMonths(new Date(), -1));
		Map<String,Map<String, Float>> saleData = saleReportMonthTypeService.getSalesByMonth(startMonth, saleEndMonth);
		
		Map<String, Float> change =  Maps.newHashMap();
		for (Map.Entry<String,Map<String, Float>> entry : saleData.entrySet()) { 
	        String month = entry.getKey();
			Map<String, Float> countryMap = entry.getValue();
			for (Map.Entry<String, Float> entryRs : countryMap.entrySet()) { 
			    String country = entryRs.getKey();
				float sale = entryRs.getValue();
				float goal = 0f;
				try {
					goal = map.get(month).get(country).getGoal();
				} catch (NullPointerException e) {}
				if (change.get(country) == null) {
					change.put(country, sale - goal);
				} else {
					change.put(country, change.get(country) + sale - goal);
				}
			}
		}
		
		List<String> title = Lists.newArrayList(" 日期 ","原定总目标(€)","实际总目标(€)","总销售额(€)","英语国家目标(€)","销售额(€)","非英语国家目标(€)","销售额(€)",
				"德国目标(€)","销售额(€)","美国目标(€)","销售额(€)","英国目标(€)","销售额(€)","法国目标(€)","销售额(€)","目标(€)意大利","销售额(€)","西班牙目标(€)","销售额(€)","加拿大目标(€)","销售额(€)","日本目标(€)","销售额(€)");
	
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int rowIndex = 1;
		float allGoal = 0f;
		float allSales = 0f;
		float allGoalActual = 0f;
		float enTotalGoal = 0f;
		float enTotalSales = 0f;
		float nonEnTotalGoal = 0f;
		float nonEnTotalSales = 0f;
		float deTotalGoal = 0f;
		float deTotalSales = 0f;
		float comTotalGoal = 0f;
		float comTotalSales = 0f;
		float ukTotalGoal = 0f;
		float ukTotalSales = 0f;
		float frTotalGoal = 0f;
		float frTotalSales = 0f;
		float itTotalGoal = 0f;
		float itTotalSales = 0f;
		float esTotalGoal = 0f;
		float esTotalSales = 0f;
		float caTotalGoal = 0f;
		float caTotalSales = 0f;
		float jpTotalGoal = 0f;
		float jpTotalSales = 0f;
		for (int i = 0; i < dataList.size(); i++) {
			String month = dataList.get(i);
			int j = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(month);
			float total = 0f;
			if (rs.get(month) != null) {
				total = rs.get(month);
				allGoal += total;
			}
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(total);
			//总目标
			float goal = 0;
			try {
				goal = map.get(month).get("total").getGoal();
			} catch (NullPointerException e) {}
			allGoalActual += goal;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(goal);
			//总销售额
			float sale = 0f;
			try {
				sale = saleData.get(month).get("total");
			} catch (NullPointerException e) {}
			allSales += sale;
			if (sale > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sale);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//英语国家目标
			float enGoal = 0;
			try {
				enGoal += map.get(month).get("com").getGoal();
			} catch (NullPointerException e) {}
			try {
				enGoal += map.get(month).get("uk").getGoal();
			} catch (NullPointerException e) {}
			try {
				enGoal += map.get(month).get("ca").getGoal();
			} catch (NullPointerException e) {}
			enTotalGoal += enGoal;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(enGoal);
			//英语国家销售额
			float enSale = 0f;
			try {
				enSale += saleData.get(month).get("com");
			} catch (NullPointerException e) {}
			try {
				enSale += saleData.get(month).get("uk");
			} catch (NullPointerException e) {}
			try {
				enSale += saleData.get(month).get("ca");
			} catch (NullPointerException e) {}
			enTotalSales += enSale;
			if (enSale > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(enSale);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//非英语国家目标
			float nonEnGoal = 0;
			try {
				nonEnGoal += map.get(month).get("de").getGoal();
			} catch (NullPointerException e) {}
			try {
				nonEnGoal += map.get(month).get("fr").getGoal();
			} catch (NullPointerException e) {}
			try {
				nonEnGoal += map.get(month).get("it").getGoal();
			} catch (NullPointerException e) {}
			try {
				nonEnGoal += map.get(month).get("es").getGoal();
			} catch (NullPointerException e) {}
			try {
				nonEnGoal += map.get(month).get("jp").getGoal();
			} catch (NullPointerException e) {}
			nonEnTotalGoal += nonEnGoal;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(nonEnGoal);
			//非英语国家销售额
			float nonEnSale = 0f;
			try {
				nonEnSale += saleData.get(month).get("de");
			} catch (NullPointerException e) {}
			try {
				nonEnSale += saleData.get(month).get("fr");
			} catch (NullPointerException e) {}
			try {
				nonEnSale += saleData.get(month).get("it");
			} catch (NullPointerException e) {}
			try {
				nonEnSale += saleData.get(month).get("es");
			} catch (NullPointerException e) {}
			try {
				nonEnSale += saleData.get(month).get("jp");
			} catch (NullPointerException e) {}
			nonEnTotalSales += nonEnSale;
			if (nonEnSale > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(nonEnSale);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//德国目标
			float deGoal = 0;
			try {
				deGoal = map.get(month).get("de").getGoal();
			} catch (NullPointerException e) {}
			deTotalGoal += deGoal;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(deGoal);
			//德国销售额
			float deSale = 0f;
			try {
				deSale = saleData.get(month).get("de");
			} catch (NullPointerException e) {}
			deTotalSales += deSale;
			if (deSale > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(deSale);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//美国目标
			float comGoal = 0;
			try {
				comGoal = map.get(month).get("com").getGoal();
			} catch (NullPointerException e) {}
			comTotalGoal += comGoal;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(comGoal);
			//美国销售额
			float comSale = 0f;
			try {
				comSale = saleData.get(month).get("com");
			} catch (NullPointerException e) {}
			comTotalSales += comSale;
			if (comSale > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(comSale);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//英国目标
			float ukGoal = 0;
			try {
				ukGoal = map.get(month).get("uk").getGoal();
			} catch (NullPointerException e) {}
			ukTotalGoal += ukGoal;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ukGoal);
			//英国销售额
			float ukSale = 0f;
			try {
				ukSale = saleData.get(month).get("uk");
			} catch (NullPointerException e) {}
			ukTotalSales += ukSale;
			if (ukSale > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ukSale);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//法国目标
			float frGoal = 0;
			try {
				frGoal = map.get(month).get("fr").getGoal();
			} catch (NullPointerException e) {}
			frTotalGoal += frGoal;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(frGoal);
			//法国销售额
			float frSale = 0f;
			try {
				frSale = saleData.get(month).get("fr");
			} catch (NullPointerException e) {}
			frTotalSales += frSale;
			if (frSale > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(frSale);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//意大利目标
			float itGoal = 0;
			try {
				itGoal = map.get(month).get("it").getGoal();
			} catch (NullPointerException e) {}
			itTotalGoal += itGoal;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(itGoal);
			//意大利销售额
			float itSale = 0f;
			try {
				itSale = saleData.get(month).get("it");
			} catch (NullPointerException e) {}
			itTotalSales += itSale;
			if (itSale > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(itSale);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//西班牙目标
			float esGoal = 0;
			try {
				esGoal = map.get(month).get("es").getGoal();
			} catch (NullPointerException e) {}
			esTotalGoal += esGoal;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(esGoal);
			//西班牙销售额
			float esSale = 0f;
			try {
				esSale = saleData.get(month).get("es");
			} catch (NullPointerException e) {}
			esTotalSales += esSale;
			if (esSale > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(esSale);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//加拿大目标
			float caGoal = 0;
			try {
				caGoal = map.get(month).get("ca").getGoal();
			} catch (NullPointerException e) {}
			caTotalGoal += caGoal;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(caGoal);
			//加拿大销售额
			float caSale = 0f;
			try {
				caSale = saleData.get(month).get("ca");
			} catch (NullPointerException e) {}
			caTotalSales += caSale;
			if (caSale > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(caSale);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//日本目标
			float jpGoal = 0;
			try {
				jpGoal = map.get(month).get("jp").getGoal();
			} catch (NullPointerException e) {}
			jpTotalGoal += jpGoal;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(jpGoal);
			//日本销售额
			float jpSale = 0f;
			try {
				jpSale = saleData.get(month).get("jp");
			} catch (NullPointerException e) {}
			jpTotalSales += jpSale;
			if (jpSale > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(jpSale);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
		}
		//总计
		int j = 0;
		row = sheet.createRow(rowIndex++);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("Total");
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(allGoal);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(allGoalActual);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(allSales);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(enTotalGoal);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(enTotalSales);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(nonEnTotalGoal);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(nonEnTotalSales);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(deTotalGoal);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(deTotalSales);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(comTotalGoal);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(comTotalSales);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ukTotalGoal);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ukTotalSales);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(frTotalGoal);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(frTotalSales);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(itTotalGoal);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(itTotalSales);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(esTotalGoal);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(esTotalSales);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(caTotalGoal);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(caTotalSales);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(jpTotalGoal);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(jpTotalSales);
		//缺口
		j = 0;
		row = sheet.createRow(rowIndex++);
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("至今缺口");
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
		if (change.size() > 0) {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get("total"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get("com") + change.get("uk") + change.get("ca"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get("de") + change.get("fr") + change.get("it") + change.get("es") + change.get("jp"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get("de"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get("com"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get("uk"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get("fr"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get("it"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get("es"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get("ca"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get("jp"));
		} else {
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
		}
			
		for (int i = 1; i < rowIndex; i++) {
			for (int m = 0; m < title.size(); m++) {
				if (i == rowIndex - 1) {
					sheet.getRow(i).getCell(m).setCellStyle(colorIntStyle);
				} else {
					sheet.getRow(i).getCell(m).setCellStyle(contentStyle);
				}
				
			}
		}

		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i);
		}

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMddhhmm");
			String fileName = "全平台汇总目标" + sdformat.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("平台汇总分产品线月目标导出异常", e);
		}
		return null;
	}
	
	//全平台分国家目标&销售额导出
	@RequestMapping(value = "exportTargetCompleteData")
	public String exportTargetCompleteData(String year, HttpServletRequest request,HttpServletResponse response, Model model) throws ParseException {
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("分产品线完成情况");
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
		contentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setWrapText(true);
		
		//两位小数显示
		HSSFCellStyle percentageOne = wb.createCellStyle();
		percentageOne.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
		percentageOne.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		percentageOne.setBottomBorderColor(HSSFColor.BLACK.index);
		percentageOne.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		percentageOne.setLeftBorderColor(HSSFColor.BLACK.index);
		percentageOne.setBorderRight(HSSFCellStyle.BORDER_THIN);
		percentageOne.setRightBorderColor(HSSFColor.BLACK.index);
		percentageOne.setBorderTop(HSSFCellStyle.BORDER_THIN);
		percentageOne.setTopBorderColor(HSSFColor.BLACK.index);
		
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

		HSSFCell cell = null;
		
		List<String> title = Lists.newArrayList("产品品线 ","月份","市场","运营负责人","目标销售额(€)","实际销售额(€)","销售额完成比例","目标利润(€)","实际利润(€)","利润完成比例","销售额与上月对比","利润与上月对比");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		EnterpriseGoal enterpriseGoal = new EnterpriseGoal();
		int lastYear = Integer.parseInt(year) - 1;
		String start = lastYear+"12";
		String end = year+"12";
		enterpriseGoal.setStartMonth(format.parse(start));
		enterpriseGoal.setEndMonth(format.parse(end));
		//分产品线目标	[月份 [产品线[国家   目标]]]
		Map<String,Map<String,Map<String, EnterpriseGoal>>> lineGoalMap = enterpriseGoalService.findLineGoalWithEnAndNonEn(enterpriseGoal);
		//分产品线目标	[月份 [产品线[国家   实际销量]]]
		Map<String,Map<String,Map<String, SaleReportMonthType>>> lineSalesMap = saleReportMonthTypeService.getAllLineSalesWithEnAndNonEn(start, end);
		//[产品线[国家   salesName]]
		Map<String,Map<String, String>> allLineSales = saleReportMonthTypeService.getAllLineSales();
		int rowIndex = 1;
		List<String> countryList = Lists.newArrayList("en", "nonEn","total");
		for (Entry<String,Map<String,Map<String,EnterpriseGoal>>> monthEntry : lineGoalMap.entrySet()) {
			String month = monthEntry.getKey();
			if (!year.equals(month.subSequence(0, 4))) {
				continue;
			}
			if (month.equals(format.format(new Date()))) {
				break;
			}
			Map<String,Map<String,EnterpriseGoal>> monthGoal = monthEntry.getValue();
			for (Entry<String,Map<String,EnterpriseGoal>> lineEntry : monthGoal.entrySet()) {
				String line = lineEntry.getKey();
				for (String country : countryList) {
					row = sheet.createRow(rowIndex);
					int j = 0;
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("total".equals(line)?"产品线总计":line+"产品线");
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(month);
					String countryStr = SystemService.countryNameMap.get(country);
					if (StringUtils.isEmpty(countryStr)) {
						countryStr = "en".equals(country)?"英语国家":"total".equals(country)?"全球":"非英语国家";
					}
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(countryStr);
					if ("total".equals(line)) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(allLineSales.get(line).get(("en".equals(country)?"com":"nonEn".equals(country)?"de":country)));
					}
					float salesGoal = 0;
					float profitGoal = 0;
					float sales = 0;
					float profit = 0;
					if (lineEntry.getValue() != null && lineEntry.getValue().get(country) != null) {
						salesGoal = lineEntry.getValue().get(country).getGoal();
						profitGoal = lineEntry.getValue().get(country).getProfitGoal();
					}
					if (lineSalesMap.get(month) != null && lineSalesMap.get(month).get(line) != null && lineSalesMap.get(month).get(line).get(country)!=null) {
						sales = lineSalesMap.get(month).get(line).get(country).getSales();
						profit = lineSalesMap.get(month).get(line).get(country).getProfits();
					}
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(salesGoal);
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales);
					if (salesGoal > 0) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales/salesGoal);
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					}
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profitGoal);
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit);
					if (profitGoal > 0) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit/profitGoal);
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					}
					Date currMonth = format.parse(month);
					String lastMonth = format.format(DateUtils.addMonths(currMonth, -1));
					float lastMonthSales = 0;
					float lastMonthProfits = 0;
					if (lineSalesMap.get(lastMonth) != null && lineSalesMap.get(lastMonth).get(line) != null 
							&& lineSalesMap.get(lastMonth).get(line).get(country)!=null) {
						lastMonthSales = lineSalesMap.get(lastMonth).get(line).get(country).getSales();
						lastMonthProfits = lineSalesMap.get(lastMonth).get(line).get(country).getProfits();
					}
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue((lastMonthSales>sales?"下降":"上涨"));
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue((lastMonthProfits>profit?"下降":"上涨"));
					rowIndex++;
				}
			}
		}
		
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i);
			for (int j = 1; j < rowIndex; j++) {
				if (i==4 || i==5 || i==7 || i==8) {
					sheet.getRow(j).getCell(i).setCellStyle(intStyle);
				} else if (i==6 || i==9) {
					sheet.getRow(j).getCell(i).setCellStyle(percentageOne);
				} else {
					sheet.getRow(j).getCell(i).setCellStyle(contentStyle);
				}
			}
		}

		sheet = wb.createSheet("分国家完成情况");
		row = sheet.createRow(0);
		title = Lists.newArrayList("月份","国家 ","目标销售额(€)","实际销售额(€)","销售额完成比例","目标利润(€)","实际利润(€)","利润完成比例","销售额与上月对比","利润与上月对比");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		rowIndex = 1;
		countryList = Lists.newArrayList("com", "uk", "ca", "de", "fr", "it", "es", "jp","total");
		for (Entry<String,Map<String,Map<String,EnterpriseGoal>>> monthEntry : lineGoalMap.entrySet()) {
			String month = monthEntry.getKey();
			if (!year.equals(month.subSequence(0, 4))) {
				continue;
			}
			if (month.equals(format.format(new Date()))) {
				break;
			}
			Map<String,Map<String,EnterpriseGoal>> monthGoal = monthEntry.getValue();
			Map<String,EnterpriseGoal> totalMap = monthGoal.get("total");
			for (String country : countryList) {
				row = sheet.createRow(rowIndex);
				int j = 0;
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(month);
				String countryStr = SystemService.countryNameMap.get(country);
				if (StringUtils.isEmpty(countryStr)) {
					countryStr = "全球汇总";
				}
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(countryStr);
				float salesGoal = 0;
				float profitGoal = 0;
				float sales = 0;
				float profit = 0;
				if (totalMap != null && totalMap.get(country) != null) {
					salesGoal = totalMap.get(country).getGoal();
					profitGoal = totalMap.get(country).getProfitGoal();
				}
				if (lineSalesMap.get(month) != null && lineSalesMap.get(month).get("total") != null && lineSalesMap.get(month).get("total").get(country)!=null) {
					sales = lineSalesMap.get(month).get("total").get(country).getSales();
					profit = lineSalesMap.get(month).get("total").get(country).getProfits();
				}
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(salesGoal);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales);
				if (salesGoal > 0) {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales/salesGoal);
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profitGoal);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit);
				if (profitGoal > 0) {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit/profitGoal);
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				Date currMonth = format.parse(month);
				String lastMonth = format.format(DateUtils.addMonths(currMonth, -1));
				float lastMonthSales = 0;
				float lastMonthProfits = 0;
				if (lineSalesMap.get(lastMonth) != null && lineSalesMap.get(lastMonth).get("total") != null 
						&& lineSalesMap.get(lastMonth).get("total").get(country)!=null) {
					lastMonthSales = lineSalesMap.get(lastMonth).get("total").get(country).getSales();
					lastMonthProfits = lineSalesMap.get(lastMonth).get("total").get(country).getProfits();
				}
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue((lastMonthSales>sales?"下降":"上涨"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue((lastMonthProfits>profit?"下降":"上涨"));
				rowIndex++;
			}
		}
		
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i);
			for (int j = 1; j < rowIndex; j++) {
				if (i==2 || i==3 || i==5 || i==6) {
					sheet.getRow(j).getCell(i).setCellStyle(intStyle);
				} else if (i==4 || i==7) {
					sheet.getRow(j).getCell(i).setCellStyle(percentageOne);
				} else {
					sheet.getRow(j).getCell(i).setCellStyle(contentStyle);
				}
			}
		}

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMddhhmm");
			String fileName = year+"年各月度目标完成情况" + sdformat.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("平台汇总分产品线月目标导出异常", e);
		}
		return null;
	}
}
