package com.springrain.erp.modules.psi.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.service.PsiProductInStockService;

/**
 * 库存分析Controller
 * @author Leehong
 * @version 2016-4-6
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/inventoryAnalysis")
public class PsiInventoryAnalysisController extends BaseController {
	
	@Autowired
	private PsiProductInStockService psiProductInStockService;
	
	/**
	 * 
	 * @param country 国家(可为空)
	 * @param date	日期
	 * @param type	类型(可为空 1：畅销品:2：普通品:3：新产品:4：淘汰品)
	 * @param model
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value = {"list", ""})
	public String list(String dataFlag, String monthDate, Model model) throws ParseException{
		dataFlag = StringUtils.isEmpty(dataFlag)?"1":dataFlag;
		DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
		DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isEmpty(monthDate)) {
			monthDate = monthFormat.format(DateUtils.addMonths(new Date(), -1));
		}
		String date1 = monthDate + "-15";	//节点一   15号
		Date lastDay = DateUtils.getLastDayOfMonth(dayFormat.parse(date1));
		String date2 = dayFormat.format(lastDay);	//节点二	 月底最后一天
		String dates = "'" + date2 + "'";	//默认月底
		//String dates = "'" + date1 + "','" + date2 + "'";
		if ("2".equals(dataFlag)) {
			dates = "'" + date1 + "'";	//默认月中
		}
		//[数据标记[品类 [产品名称 [国家[key 数量]]]]] key:inventory/day31sale
		Map<String, Map<String, Map<String, Map<String, Map<String, Integer>>>>> rs = psiProductInStockService.getInventoryTypeByDate(dates);
		//[月份[数据标记[品类 [国家 总计]]]]
		Map<String, Map<String, Map<String, Map<String, Integer>>>> typeNums = psiProductInStockService.getInventoryTypeNum(dates);
		//[数据标记[品类  [key(country/country+"31sale") 总计]]]
		Map<String, Map<String, Map<String, Integer>>> data = Maps.newHashMap();
		//[数据标记[品类 [国家 总计]]]
		Map<String, Map<String, Map<String, Integer>>> typeNum = typeNums.get(monthDate);
		for (Map.Entry<String, Map<String, Map<String, Map<String, Map<String, Integer>>>>> flagEntry : rs.entrySet()) {
			String flag = flagEntry.getKey();
			Map<String, Map<String, Map<String, Map<String, Integer>>>> flagMap = flagEntry.getValue();
			String key = "1";//月底
			if (flag.endsWith("-15")) {
				key = "2";	//月中
			}
			Map<String, Map<String, Integer>> flagData = data.get(key);
			if (flagData == null) {
				flagData = Maps.newHashMap();
				data.put(key, flagData);
			}
			/*Map<String, Integer> flagNum = typeNum.get(key);
			if (flagNum == null) {
				flagNum = Maps.newHashMap();
				typeNum.put(key, flagNum);
			}*/
			for ( Map.Entry<String, Map<String, Map<String, Map<String, Integer>>>> entry : flagMap.entrySet()) {
				String type = entry.getKey();
				Map<String, Map<String, Map<String, Integer>>> typeMap = entry.getValue();
				//flagNum.put(type, typeMap.keySet().size());	//记录该品类的数量
				for (Map.Entry<String, Map<String, Map<String, Integer>>> entry1 : typeMap.entrySet()) {
					Map<String, Map<String, Integer>> productNameMap = entry1.getValue();
					for (Map.Entry<String, Map<String, Integer>> entry2 : productNameMap.entrySet()) {
						String country = entry2.getKey();
						Map<String, Integer> typeData = flagData.get(type);
						if (typeData == null) {
							typeData = Maps.newHashMap();
							flagData.put(type, typeData);
						}
						Map<String, Integer> valueMap = entry2.getValue();
						Integer inventory = valueMap.get("inventory")==null?0:valueMap.get("inventory");
						Integer day31sale = valueMap.get("day31sale")==null?0:valueMap.get("day31sale");
						if (typeData.get(country) == null) {
							typeData.put(country, inventory);
							typeData.put(country+"31sale", day31sale);
						} else {
							typeData.put(country, inventory + typeData.get(country));
							typeData.put(country+"31sale", day31sale + typeData.get(country+"31sale"));
						}
						if (Integer.parseInt(flag.replaceAll("-", "").replaceAll("'", "")) < 20160301) {
							if (!"total".equals(country) && !"eu".equals(country)) {
								if (typeData.get("total31sale") == null) {
									typeData.put("total31sale", 0);
								}
								typeData.put("total31sale", typeData.get("total31sale") + day31sale);
							}
						}
					}
				}
			}
		}
		
		//统计历史销量信息
		List<String> xList = Lists.newArrayList();
		for (int i = 11; i >= 0; i--) {
			xList.add(monthFormat.format(DateUtils.addMonths(lastDay, -i)));
		}
		model.addAttribute("xList", xList);
		List<String> datesList = Lists.newArrayList();
		for (String string : xList) {
			String middle = string + "-15";	//月中
			Date lastDate = DateUtils.getLastDayOfMonth(dayFormat.parse(middle));
			String last = dayFormat.format(lastDate);	//月底
			datesList.add(middle);
			datesList.add(last);
		}
		Map<String, Map<String, Integer>> historyInventory = psiProductInStockService.findHistoryInventory(datesList);
		model.addAttribute("historyInventory", historyInventory);

		model.addAttribute("data", data);
		model.addAttribute("typeNum", typeNum);
		model.addAttribute("monthDate", monthDate);
		model.addAttribute("typeList", Lists.newArrayList("1","2","3","4"));
		Map<String,String> typeMap = Maps.newHashMap();
		typeMap.put("1","畅销品");
		typeMap.put("2","普通品");
		typeMap.put("3","新产品");
		typeMap.put("4","淘汰品");
		model.addAttribute("typeMap", typeMap);
		model.addAttribute("dataFlag", dataFlag);
		model.addAttribute("minDate", monthDate + "-01 00:00:00");
		return "modules/psi/psiInventoryAnalysis";
	}
	
	//导出
	@RequestMapping(value = "exportDetail")
	public String exportDetail(String dataFlag, String monthDate, String endDate, HttpServletRequest request,HttpServletResponse response, Model model) throws ParseException {
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
		
		//组织报表需要的数据
		DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
		DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isEmpty(monthDate)) {
			monthDate = monthFormat.format(DateUtils.addMonths(new Date(), -1));
		}
		String date1 = monthDate + "-15";	//节点一   15号
		Date lastDay = DateUtils.getLastDayOfMonth(dayFormat.parse(date1));
		String date2 = dayFormat.format(lastDay);	//节点二	 月底最后一天
		StringBuilder dates = new StringBuilder();
		List<String> monthList = Lists.newArrayList();
		if (StringUtils.isEmpty(endDate) || endDate.equals(monthDate)) {
			monthList.add(monthDate);
			if ("2".equals(dataFlag)) {
				dates.append("'").append(date1).append("',");	//月中
			} else {
				dates.append("'").append(date2).append("',");	//月底
			}
		} else {
			Date start = DateUtils.getLastDayOfMonth(dayFormat.parse(date1));
			Date end = DateUtils.getLastDayOfMonth(dayFormat.parse(endDate+"-15"));
			lastDay = end;
			while (true) {
				monthList.add(monthFormat.format(start));
				if ("2".equals(dataFlag)) {
					dates.append("'").append(monthFormat.format(start)).append("-15").append("',");	//月中
				} else {
					dates.append("'").append(dayFormat.format(start)).append("',");	//月底
				}
				start = DateUtils.addMonths(start, 1);
				if (start.after(end)) {
					break;
				}
			}
		}
		dates = new StringBuilder(dates.substring(0, dates.length()-1));
		
		//[数据标记[品类 [产品名称 [国家[key 数量]]]]] key:inventory/day31sale
		Map<String, Map<String, Map<String, Map<String, Map<String, Integer>>>>> rs = psiProductInStockService.getInventoryTypeByDate(dates.toString());
		//[月份[数据标记[品类 [国家 总计]]]]
		Map<String, Map<String, Map<String, Map<String, Integer>>>> typeNum = psiProductInStockService.getInventoryTypeNum(dates.toString());
		//[月份[数据标记[品类  [key(country/country+"31sale") 总计]]]]
		Map<String, Map<String, Map<String, Map<String, Integer>>>> data = Maps.newHashMap();
		for (Map.Entry<String, Map<String, Map<String, Map<String, Map<String, Integer>>>>> flagEntry : rs.entrySet()) {
			String flag = flagEntry.getKey();
			Map<String, Map<String, Map<String, Map<String, Integer>>>> flagMap = flagEntry.getValue();
			String month = flag.substring(0, 7);
			Map<String, Map<String, Map<String, Integer>>> monthData = data.get(month);
			if (monthData == null) {
				monthData = Maps.newHashMap();
				data.put(month, monthData);
			}
			String key = "1";//月底
			if (flag.endsWith("-15")) {
				key = "2";	//月中
			}
			Map<String, Map<String, Integer>> flagData = monthData.get(key);
			if (flagData == null) {
				flagData = Maps.newHashMap();
				monthData.put(key, flagData);
			}
//			for (String type : flagMap.keySet()) {
//				Map<String, Map<String, Map<String, Integer>>> typeMap = flagMap.get(type);
			for ( Map.Entry<String, Map<String, Map<String, Map<String, Integer>>>> entry3 : flagMap.entrySet()) {
				String type = entry3.getKey();
				Map<String, Map<String, Map<String, Integer>>> typeMap = entry3.getValue();
				for (Map.Entry<String, Map<String, Map<String, Integer>>> entry1 : typeMap.entrySet()) {
					Map<String, Map<String, Integer>> productNameMap = entry1.getValue();
					for (Map.Entry<String, Map<String, Integer>> entry: productNameMap.entrySet()) {
						String country  = entry.getKey();
						Map<String, Integer> typeData = flagData.get(type);
						if (typeData == null) {
							typeData = Maps.newHashMap();
							flagData.put(type, typeData);
						}
						Map<String, Integer> valueMap = entry.getValue();
						Integer inventory = valueMap.get("inventory")==null?0:valueMap.get("inventory");
						Integer day31sale = valueMap.get("day31sale")==null?0:valueMap.get("day31sale");
						if (typeData.get(country) == null) {
							typeData.put(country, inventory);
							typeData.put(country+"31sale", day31sale);
						} else {
							typeData.put(country, inventory + typeData.get(country));
							typeData.put(country+"31sale", day31sale + typeData.get(country+"31sale"));
						}
						if (Integer.parseInt(flag.replaceAll("-", "").replaceAll("'", "")) < 20160301) {
							if (!"total".equals(country) && !"eu".equals(country)) {
								if (typeData.get("total31sale") == null) {
									typeData.put("total31sale", 0);
								}
								typeData.put("total31sale", typeData.get("total31sale") + day31sale);
							}
						}
					}
				}
			}
		}
		
		//统计历史销量信息
		List<String> xList = Lists.newArrayList();
		for (int i = 11; i >= 0; i--) {
			xList.add(monthFormat.format(DateUtils.addMonths(lastDay, -i)));
		}
		
		List<String> datesList = Lists.newArrayList();
		for (String string : xList) {
			String middle = string + "-15";	//月中
			Date lastDate = DateUtils.getLastDayOfMonth(dayFormat.parse(middle));
			String last = dayFormat.format(lastDate);	//月底
			datesList.add(middle);
			datesList.add(last);
		}
		Map<String, Map<String, Integer>> historyInventory = psiProductInStockService.findHistoryInventory(datesList);

		List<String> typeList = Lists.newArrayList("1","2","3","4");
		Map<String,String> typeMap = Maps.newHashMap();
		typeMap.put("1","畅销品");
		typeMap.put("2","普通品");
		typeMap.put("3","新产品");
		typeMap.put("4","淘汰品");
		
		if (StringUtils.isEmpty(dataFlag)) {
			dataFlag = "1";	//默认取月底数据
		}
		
		List<String> countryList=Lists.newArrayList("de","uk","fr","it","es","com","ca","mx","jp");
		
		List<String> title = Lists.newArrayList("时间", "品类", "总库存", "31日销汇总", "欧洲总库存", "欧洲31日销", "美洲总库存", "美洲31日销", "德国总库存", "德国31日销", 
				"英国总库存", "英国31日销", "法国总库存", "法国31日销", "意大利总库存", "意大利31日销", "西班牙总库存", "西班牙31日销", "美国总库存", "美国31日销", "加拿大总库存", 
				"加拿大31日销", "墨西哥总库存", "墨西哥31日销", "日本总库存", "日本31日销");
		//渲染第一个sheet
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int rowIndex = 1;
		for (String month : monthList) {
			for (int i = 0; i < typeList.size(); i++) {
				String type = typeList.get(i);
				int j = 0;
				row = sheet.createRow(rowIndex++);
				row.setHeight((short) 400);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(month);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeMap.get(type));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get("total"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get("total31sale"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get("eu"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get("eu31sale"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get("com")+data.get(month).get(dataFlag).get(type).get("ca"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get("com31sale")+data.get(month).get(dataFlag).get(type).get("ca31sale"));
				for (String country : countryList) {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get(country));
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get(country+"31sale"));
				}
			}
			int totalInventory = data.get(month).get(dataFlag).get("1").get("total")+data.get(month).get(dataFlag).get("2").get("total")+data.get(month).get(dataFlag).get("3").get("total")+data.get(month).get(dataFlag).get("4").get("total");
			int totalDay31Sale = data.get(month).get(dataFlag).get("1").get("total31sale")+data.get(month).get(dataFlag).get("2").get("total31sale")+data.get(month).get(dataFlag).get("3").get("total31sale")+data.get(month).get(dataFlag).get("4").get("total31sale");
			int m = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(month);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("总计");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalInventory);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get("1").get("total31sale")+data.get(month).get(dataFlag).get("2").get("total31sale")+data.get(month).get(dataFlag).get("3").get("total31sale")+data.get(month).get(dataFlag).get("4").get("total31sale"));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get("1").get("eu")+data.get(month).get(dataFlag).get("2").get("eu")+data.get(month).get(dataFlag).get("3").get("eu")+data.get(month).get(dataFlag).get("4").get("eu"));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get("1").get("eu31sale")+data.get(month).get(dataFlag).get("2").get("eu31sale")+data.get(month).get(dataFlag).get("3").get("eu31sale")+data.get(month).get(dataFlag).get("4").get("eu31sale"));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get("1").get("com")+data.get(month).get(dataFlag).get("2").get("com")+data.get(month).get(dataFlag).get("3").get("com")+data.get(month).get(dataFlag).get("4").get("com")
					+data.get(month).get(dataFlag).get("1").get("ca")+data.get(month).get(dataFlag).get("2").get("ca")+data.get(month).get(dataFlag).get("3").get("ca")+data.get(month).get(dataFlag).get("4").get("ca"));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get("1").get("com31sale")+data.get(month).get(dataFlag).get("2").get("com31sale")+data.get(month).get(dataFlag).get("3").get("com31sale")+data.get(month).get(dataFlag).get("4").get("com31sale")
					+data.get(month).get(dataFlag).get("1").get("ca31sale")+data.get(month).get(dataFlag).get("2").get("ca31sale")+data.get(month).get(dataFlag).get("3").get("ca31sale")+data.get(month).get(dataFlag).get("4").get("ca31sale"));
			for (String country : countryList) {
				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get("1").get(country)+data.get(month).get(dataFlag).get("2").get(country)+data.get(month).get(dataFlag).get("3").get(country)+data.get(month).get(dataFlag).get("4").get(country));
				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get("1").get(country+"31sale")+data.get(month).get(dataFlag).get("2").get(country+"31sale")+data.get(month).get(dataFlag).get("3").get(country+"31sale")+data.get(month).get(dataFlag).get("4").get(country+"31sale"));
			}
		}

		for (int i = 0; i < rowIndex - 1; i++) {
			for (int j = 0; j < title.size(); j++) {
				sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
			}
		}

		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i);
		}
		
		//渲染第二个sheet
		HSSFSheet sheet1 = wb.createSheet();
		row = sheet1.createRow(0);
		title = Lists.newArrayList("时间", "平台", "品类", "计数", "31日销", "总库存", "总库存/31日销", "库存百分比", "销量百分比");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet1.autoSizeColumn((short) i);
		}
		rowIndex = 1;
		for (String month : monthList) {
			int totalInventory = data.get(month).get(dataFlag).get("1").get("total")+data.get(month).get(dataFlag).get("2").get("total")+data.get(month).get(dataFlag).get("3").get("total")+data.get(month).get(dataFlag).get("4").get("total");
			int totalDay31Sale = data.get(month).get(dataFlag).get("1").get("total31sale")+data.get(month).get(dataFlag).get("2").get("total31sale")+data.get(month).get(dataFlag).get("3").get("total31sale")+data.get(month).get(dataFlag).get("4").get("total31sale");
			for (int i = 0; i < typeList.size(); i++) {
				String type = typeList.get(i);
				int j = 0;
				row = sheet1.createRow(rowIndex++);
				row.setHeight((short) 400);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(month);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("全球");
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeMap.get(type));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeNum.get(month).get(dataFlag).get(type).get("total"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get("total31sale"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get("total"));
				Float ratio = data.get(month).get(dataFlag).get(type).get("total")/(float)data.get(month).get(dataFlag).get(type).get("total31sale");
				row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(String.format("%.1f", ratio));
				Float f1 = data.get(month).get(dataFlag).get(type).get("total")/(float)totalInventory * 100;
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f", f1) + "%");
				Float f2 = data.get(month).get(dataFlag).get(type).get("total31sale")/(float)totalDay31Sale * 100;
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f", f2) + "%");
			}
			int m = 0;
			row = sheet1.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(month);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("全球");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("总计");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(typeNum.get(month).get(dataFlag).get("1").get("total")+typeNum.get(month).get(dataFlag).get("2").get("total")+typeNum.get(month).get(dataFlag).get("3").get("total")+typeNum.get(month).get(dataFlag).get("4").get("total"));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalDay31Sale);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalInventory);
			row.createCell(m++, Cell.CELL_TYPE_NUMERIC).setCellValue(String.format("%.1f", totalInventory/(float)totalDay31Sale));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("100%");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("100%");
			//欧洲
			int euInventory = data.get(month).get(dataFlag).get("1").get("eu")+data.get(month).get(dataFlag).get("2").get("eu")+data.get(month).get(dataFlag).get("3").get("eu")+data.get(month).get(dataFlag).get("4").get("eu");
			int euDay31Sale = data.get(month).get(dataFlag).get("1").get("eu31sale")+data.get(month).get(dataFlag).get("2").get("eu31sale")+data.get(month).get(dataFlag).get("3").get("eu31sale")+data.get(month).get(dataFlag).get("4").get("eu31sale");
			for (int i = 0; i < typeList.size(); i++) {
				String type = typeList.get(i);
				int j = 0;
				row = sheet1.createRow(rowIndex++);
				row.setHeight((short) 400);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(month);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("欧洲");
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeMap.get(type));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeNum.get(month).get(dataFlag).get(type).get("de"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get("eu31sale"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get("eu"));
				Float ratio = data.get(month).get(dataFlag).get(type).get("eu")/(float)data.get(month).get(dataFlag).get(type).get("eu31sale");
				row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(String.format("%.1f", ratio));
				Float f1 = data.get(month).get(dataFlag).get(type).get("eu")/(float)euInventory * 100;
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f", f1) + "%");
				Float f2 = data.get(month).get(dataFlag).get(type).get("eu31sale")/(float)euDay31Sale * 100;
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f", f2) + "%");
			}
			m = 0;
			row = sheet1.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(month);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("欧洲");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("总计");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(typeNum.get(month).get(dataFlag).get("1").get("de")+typeNum.get(month).get(dataFlag).get("2").get("de")+typeNum.get(month).get(dataFlag).get("3").get("de")+typeNum.get(month).get(dataFlag).get("4").get("de"));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(euDay31Sale);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(euInventory);
			row.createCell(m++, Cell.CELL_TYPE_NUMERIC).setCellValue(String.format("%.1f", totalInventory/(float)totalDay31Sale));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("100%");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("100%");

			//美洲
			int comInventory = data.get(month).get(dataFlag).get("1").get("com")+data.get(month).get(dataFlag).get("2").get("com")+data.get(month).get(dataFlag).get("3").get("com")+data.get(month).get(dataFlag).get("4").get("com");
			int comDay31Sale = data.get(month).get(dataFlag).get("1").get("com31sale")+data.get(month).get(dataFlag).get("2").get("com31sale")+data.get(month).get(dataFlag).get("3").get("com31sale")+data.get(month).get(dataFlag).get("4").get("com31sale");
			int caInventory = data.get(month).get(dataFlag).get("1").get("ca")+data.get(month).get(dataFlag).get("2").get("ca")+data.get(month).get(dataFlag).get("3").get("ca")+data.get(month).get(dataFlag).get("4").get("ca");
			int caDay31Sale = data.get(month).get(dataFlag).get("1").get("ca31sale")+data.get(month).get(dataFlag).get("2").get("ca31sale")+data.get(month).get(dataFlag).get("3").get("ca31sale")+data.get(month).get(dataFlag).get("4").get("ca31sale");
			int mxInventory = data.get(month).get(dataFlag).get("1").get("mx")+data.get(month).get(dataFlag).get("2").get("mx")+data.get(month).get(dataFlag).get("3").get("mx")+data.get(month).get(dataFlag).get("4").get("mx");
			int mxDay31Sale = data.get(month).get(dataFlag).get("1").get("mx31sale")+data.get(month).get(dataFlag).get("2").get("mx31sale")+data.get(month).get(dataFlag).get("3").get("mx31sale")+data.get(month).get(dataFlag).get("4").get("mx31sale");
			//美洲
			int amInventory = comInventory + caInventory + mxInventory;
			int amDay31Sale = comDay31Sale + caDay31Sale + mxDay31Sale;
			for (int i = 0; i < typeList.size(); i++) {
				String type = typeList.get(i);
				int j = 0;
				row = sheet1.createRow(rowIndex++);
				row.setHeight((short) 400);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(month);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("美洲");
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeMap.get(type));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeNum.get(month).get(dataFlag).get(type).get("com"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue((data.get(month).get(dataFlag).get(type).get("com31sale") + data.get(month).get(dataFlag).get(type).get("ca31sale") + data.get(month).get(dataFlag).get(type).get("mx31sale")));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue((data.get(month).get(dataFlag).get(type).get("com") + data.get(month).get(dataFlag).get(type).get("ca") + data.get(month).get(dataFlag).get(type).get("mx")));
				Float ratio = (data.get(month).get(dataFlag).get(type).get("com") + data.get(month).get(dataFlag).get(type).get("ca") + data.get(month).get(dataFlag).get(type).get("mx"))/(float)(data.get(month).get(dataFlag).get(type).get("com31sale") + data.get(month).get(dataFlag).get(type).get("ca31sale") + data.get(month).get(dataFlag).get(type).get("mx31sale"));
				row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(String.format("%.1f", ratio));
				Float f1 = (data.get(month).get(dataFlag).get(type).get("com") + data.get(month).get(dataFlag).get(type).get("ca"))/(float)amInventory * 100;
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f", f1) + "%");
				Float f2 = (data.get(month).get(dataFlag).get(type).get("com31sale") + data.get(month).get(dataFlag).get(type).get("ca31sale"))/(float)amDay31Sale * 100;
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f", f2) + "%");
			}
			m = 0;
			row = sheet1.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(month);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("美洲");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("总计");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(typeNum.get(month).get(dataFlag).get("1").get("com")+typeNum.get(month).get(dataFlag).get("2").get("com")+typeNum.get(month).get(dataFlag).get("3").get("com")+typeNum.get(month).get(dataFlag).get("4").get("com"));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(amDay31Sale);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(amInventory);
			row.createCell(m++, Cell.CELL_TYPE_NUMERIC).setCellValue(String.format("%.1f", amInventory/(float)amDay31Sale));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("100%");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("100%");
			//日本
			int jpInventory = data.get(month).get(dataFlag).get("1").get("jp")+data.get(month).get(dataFlag).get("2").get("jp")+data.get(month).get(dataFlag).get("3").get("jp")+data.get(month).get(dataFlag).get("4").get("jp");
			int jpDay31Sale = data.get(month).get(dataFlag).get("1").get("jp31sale")+data.get(month).get(dataFlag).get("2").get("jp31sale")+data.get(month).get(dataFlag).get("3").get("jp31sale")+data.get(month).get(dataFlag).get("4").get("jp31sale");
			for (int i = 0; i < typeList.size(); i++) {
				String type = typeList.get(i);
				int j = 0;
				row = sheet1.createRow(rowIndex++);
				row.setHeight((short) 400);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(month);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("日本");
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeMap.get(type));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeNum.get(month).get(dataFlag).get(type).get("jp"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get("jp31sale"));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(month).get(dataFlag).get(type).get("jp"));
				Float ratio = data.get(month).get(dataFlag).get(type).get("jp")/(float)data.get(month).get(dataFlag).get(type).get("jp31sale");
				row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(String.format("%.1f", ratio));
				Float f1 = data.get(month).get(dataFlag).get(type).get("jp")/(float)jpInventory * 100;
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f", f1) + "%");
				Float f2 = data.get(month).get(dataFlag).get(type).get("jp31sale")/(float)jpDay31Sale * 100;
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f", f2) + "%");
			}
			m = 0;
			row = sheet1.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(month);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("日本");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("总计");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(typeNum.get(month).get(dataFlag).get("1").get("jp")+typeNum.get(month).get(dataFlag).get("2").get("jp")+typeNum.get(month).get(dataFlag).get("3").get("jp")+typeNum.get(month).get(dataFlag).get("4").get("jp"));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(euDay31Sale);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(euInventory);
			row.createCell(m++, Cell.CELL_TYPE_NUMERIC).setCellValue(String.format("%.1f", totalInventory/(float)totalDay31Sale));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("100%");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("100%");
		}
		for (int i = 0; i < rowIndex - 1; i++) {
			for (int j = 0; j < title.size(); j++) {
				try {
					sheet1.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
				} catch (Exception e) {}
			}
		}

		for (int i = 0; i < title.size(); i++) {
			sheet1.autoSizeColumn((short) i);
		}
		
		//渲染第三个sheet
		HSSFSheet sheet2 = wb.createSheet();
		row = sheet2.createRow(0);
		title = Lists.newArrayList("数据节点");
		for (String str : xList) {
			title.add(str + "月");
		}
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet2.autoSizeColumn((short) i);
		}
		rowIndex = 1;
		for (int i = 2; i > 0; i--) {
			String type = i + "";
			int j = 0;
			row = sheet2.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("1".equals(type)?"月底":"月中");
			for (String str : xList) {
				if (historyInventory.get(type) != null && historyInventory.get(type).get(str) != null) {
					row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(historyInventory.get(type).get(str));
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}
		}
		
		for (int i = 0; i < rowIndex - 1; i++) {
			for (int j = 0; j < title.size(); j++) {
				sheet2.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
			}
		}

		for (int i = 0; i < title.size(); i++) {
			sheet2.autoSizeColumn((short) i);
		}
		String file = monthDate;
		if (StringUtils.isNotEmpty(endDate) && !endDate.equals(monthDate)) {
			file = monthDate + "月至" + endDate;
		}
		
		//命名sheet
		wb.setSheetName(0, file + "月"+("1".equals(dataFlag)?"月底":"月中")+"库存分品类统计");
		wb.setSheetName(1, file + "月"+("1".equals(dataFlag)?"月底":"月中")+"库存水平统计");
		wb.setSheetName(2, "历史库存统计");

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");

			String fileName = file + "月"+("1".equals(dataFlag)?"月底":"月中")+"库存按月分析" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("按月库存分析导出异常", e);
		}
		return null;
	}
	
	//导出
	@RequestMapping(value = "exportDetail1")
	public String exportDetail1(String dataFlag, String monthDate, String endDate, HttpServletRequest request,HttpServletResponse response, Model model) throws ParseException {
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
		
		//组织报表需要的数据
		DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
		DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isEmpty(monthDate)) {
			monthDate = monthFormat.format(DateUtils.addMonths(new Date(), -1));
		}
		String date1 = monthDate + "-15";	//节点一   15号
		Date lastDay = DateUtils.getLastDayOfMonth(dayFormat.parse(date1));
		String date2 = dayFormat.format(lastDay);	//节点二	 月底最后一天
		String dates = "'" + date1 + "','" + date2 + "'";
		//[数据标记[品类 [产品名称 [国家[key 数量]]]]] key:inventory/day31sale
		Map<String, Map<String, Map<String, Map<String, Map<String, Integer>>>>> rs = psiProductInStockService.getInventoryTypeByDate(dates);
		//[数据标记[品类  [key(country/country+"31sale") 总计]]]
		Map<String, Map<String, Map<String, Integer>>> data = Maps.newHashMap();
		//[数据标记[品类  总计]]
		Map<String, Map<String, Integer>> typeNum = Maps.newHashMap();
		for (Map.Entry<String, Map<String, Map<String, Map<String, Map<String, Integer>>>>> flagEntry : rs.entrySet()) {
			String flag = flagEntry.getKey();
			Map<String, Map<String, Map<String, Map<String, Integer>>>> flagMap = flagEntry.getValue();
			String key = "1";//月底
			if (flag.endsWith("-15")) {
				key = "2";	//月中
			}
			Map<String, Map<String, Integer>> flagData = data.get(key);
			if (flagData == null) {
				flagData = Maps.newHashMap();
				data.put(key, flagData);
			}
			Map<String, Integer> flagNum = typeNum.get(key);
			if (flagNum == null) {
				flagNum = Maps.newHashMap();
				typeNum.put(key, flagNum);
			}
			for ( Map.Entry<String, Map<String, Map<String, Map<String, Integer>>>> entry3 : flagMap.entrySet()) {
				String type = entry3.getKey();
				Map<String, Map<String, Map<String, Integer>>> typeMap = entry3.getValue();
				flagNum.put(type, typeMap.keySet().size());	//记录该品类的数量
				for (Map.Entry<String, Map<String, Map<String, Integer>>> entry1 : typeMap.entrySet()) {
					String productName = entry1.getKey();
					Map<String, Map<String, Integer>> productNameMap = typeMap.get(productName);
					for (Map.Entry<String, Map<String, Integer>> entry : productNameMap.entrySet()) {
						String country = entry.getKey();
						Map<String, Integer> typeData = flagData.get(type);
						if (typeData == null) {
							typeData = Maps.newHashMap();
							flagData.put(type, typeData);
						}
						Map<String, Integer> valueMap = entry.getValue();
						Integer inventory = valueMap.get("inventory")==null?0:valueMap.get("inventory");
						Integer day31sale = valueMap.get("day31sale")==null?0:valueMap.get("day31sale");
						if (typeData.get(country) == null) {
							typeData.put(country, inventory);
							typeData.put(country+"31sale", day31sale);
						} else {
							typeData.put(country, inventory + typeData.get(country));
							typeData.put(country+"31sale", day31sale + typeData.get(country+"31sale"));
						}
						if (!"total".equals(country) && !"eu".equals(country)) {
							if (typeData.get("total31sale") == null) {
								typeData.put("total31sale", 0);
							}
							typeData.put("total31sale", typeData.get("total31sale") + day31sale);
						}
					}
				}
			}
		}
		
		//统计历史销量信息
		List<String> xList = Lists.newArrayList();
		for (int i = 11; i >= 0; i--) {
			xList.add(monthFormat.format(DateUtils.addMonths(lastDay, -i)));
		}
		
		List<String> datesList = Lists.newArrayList();
		for (String string : xList) {
			String middle = string + "-15";	//月中
			Date lastDate = DateUtils.getLastDayOfMonth(dayFormat.parse(middle));
			String last = dayFormat.format(lastDate);	//月底
			datesList.add(middle);
			datesList.add(last);
		}
		Map<String, Map<String, Integer>> historyInventory = psiProductInStockService.findHistoryInventory(datesList);

		List<String> typeList = Lists.newArrayList("1","2","3","4");
		Map<String,String> typeMap = Maps.newHashMap();
		typeMap.put("1","畅销品");
		typeMap.put("2","普通品");
		typeMap.put("3","新产品");
		typeMap.put("4","淘汰品");
		
		if (StringUtils.isEmpty(dataFlag)) {
			dataFlag = "1";	//默认取月底数据
		}
		
		List<String> countryList=Lists.newArrayList("de","uk","fr","it","es","com","ca","jp");
		
		List<String> title = Lists.newArrayList("品类", "总库存", "31日销汇总", "欧洲总库存", "欧洲31日销", "美洲总库存", "美洲31日销", "德国总库存", "德国31日销", 
				"英国总库存", "英国31日销", "法国总库存", "法国31日销", "意大利总库存", "意大利31日销", "西班牙总库存", "西班牙31日销", "美国总库存", "美国31日销", "加拿大总库存", 
				"加拿大31日销", "日本总库存", "日本31日销");
		//渲染第一个sheet
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int rowIndex = 1;
		for (int i = 0; i < typeList.size(); i++) {
			String type = typeList.get(i);
			int j = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeMap.get(type));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get(type).get("total"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get(type).get("total31sale"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get(type).get("eu"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get(type).get("eu31sale"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get(type).get("com")+data.get(dataFlag).get(type).get("ca"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get(type).get("com31sale")+data.get(dataFlag).get(type).get("ca31sale"));
			for (String country : countryList) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get(type).get(country));
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get(type).get(country+"31sale"));
			}
		}
		int totalInventory = data.get(dataFlag).get("1").get("total")+data.get(dataFlag).get("2").get("total")+data.get(dataFlag).get("3").get("total")+data.get(dataFlag).get("4").get("total");
		int totalDay31Sale = data.get(dataFlag).get("1").get("total31sale")+data.get(dataFlag).get("2").get("total31sale")+data.get(dataFlag).get("3").get("total31sale")+data.get(dataFlag).get("4").get("total31sale");
		int m = 0;
		row = sheet.createRow(rowIndex++);
		row.setHeight((short) 400);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("总计");
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalInventory);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get("1").get("total31sale")+data.get(dataFlag).get("2").get("total31sale")+data.get(dataFlag).get("3").get("total31sale")+data.get(dataFlag).get("4").get("total31sale"));
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get("1").get("eu")+data.get(dataFlag).get("2").get("eu")+data.get(dataFlag).get("3").get("eu")+data.get(dataFlag).get("4").get("eu"));
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get("1").get("eu31sale")+data.get(dataFlag).get("2").get("eu31sale")+data.get(dataFlag).get("3").get("eu31sale")+data.get(dataFlag).get("4").get("eu31sale"));
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get("1").get("com")+data.get(dataFlag).get("2").get("com")+data.get(dataFlag).get("3").get("com")+data.get(dataFlag).get("4").get("com")
				+data.get(dataFlag).get("1").get("ca")+data.get(dataFlag).get("2").get("ca")+data.get(dataFlag).get("3").get("ca")+data.get(dataFlag).get("4").get("ca"));
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get("1").get("com31sale")+data.get(dataFlag).get("2").get("com31sale")+data.get(dataFlag).get("3").get("com31sale")+data.get(dataFlag).get("4").get("com31sale")
				+data.get(dataFlag).get("1").get("ca31sale")+data.get(dataFlag).get("2").get("ca31sale")+data.get(dataFlag).get("3").get("ca31sale")+data.get(dataFlag).get("4").get("ca31sale"));
		for (String country : countryList) {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get("1").get(country)+data.get(dataFlag).get("2").get(country)+data.get(dataFlag).get("3").get(country)+data.get(dataFlag).get("4").get(country));
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get("1").get(country+"31sale")+data.get(dataFlag).get("2").get(country+"31sale")+data.get(dataFlag).get("3").get(country+"31sale")+data.get(dataFlag).get("4").get(country+"31sale"));
		}

		for (int i = 0; i < rowIndex - 1; i++) {
			for (int j = 0; j < title.size(); j++) {
				sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
			}
		}

		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i);
		}
		
		//渲染第二个sheet
		HSSFSheet sheet1 = wb.createSheet();
		row = sheet1.createRow(0);
		title = Lists.newArrayList("品类", "计数", "31日销", "总库存", "总库存/31日销", "库存百分比", "销量百分比");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet1.autoSizeColumn((short) i);
		}
		rowIndex = 1;
		for (int i = 0; i < typeList.size(); i++) {
			String type = typeList.get(i);
			int j = 0;
			row = sheet1.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeMap.get(type));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(typeNum.get(dataFlag).get(type));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get(type).get("total31sale"));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(dataFlag).get(type).get("total"));
			Float ratio = data.get(dataFlag).get(type).get("total")/(float)data.get(dataFlag).get(type).get("total31sale");
			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(String.format("%.1f", ratio));
			Float f1 = data.get(dataFlag).get(type).get("total")/(float)totalInventory * 100;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f", f1) + "%");
			Float f2 = data.get(dataFlag).get(type).get("total31sale")/(float)totalDay31Sale * 100;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(String.format("%.2f", f2) + "%");
		}
		m = 0;
		row = sheet1.createRow(rowIndex++);
		row.setHeight((short) 400);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("总计");
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(typeNum.get(dataFlag).get("1")+typeNum.get(dataFlag).get("2")+typeNum.get(dataFlag).get("3")+typeNum.get(dataFlag).get("4"));
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalDay31Sale);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalInventory);
		row.createCell(m++, Cell.CELL_TYPE_NUMERIC).setCellValue(String.format("%.1f", totalInventory/(float)totalDay31Sale));
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("100%");
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("100%");
		
		for (int i = 0; i < rowIndex - 1; i++) {
			for (int j = 0; j < title.size(); j++) {
				sheet1.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
			}
		}

		for (int i = 0; i < title.size(); i++) {
			sheet1.autoSizeColumn((short) i);
		}
		
		//渲染第三个sheet
		HSSFSheet sheet2 = wb.createSheet();
		row = sheet2.createRow(0);
		title = Lists.newArrayList("数据节点");
		for (String str : xList) {
			title.add(str + "月");
		}
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet2.autoSizeColumn((short) i);
		}
		rowIndex = 1;
		for (int i = 2; i > 0; i--) {
			String type = i + "";
			int j = 0;
			row = sheet2.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("1".equals(type)?"月底":"月中");
			for (String str : xList) {
				if (historyInventory.get(type) != null && historyInventory.get(type).get(str) != null) {
					row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(historyInventory.get(type).get(str));
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}
		}
		
		for (int i = 0; i < rowIndex - 1; i++) {
			for (int j = 0; j < title.size(); j++) {
				sheet2.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
			}
		}

		for (int i = 0; i < title.size(); i++) {
			sheet2.autoSizeColumn((short) i);
		}
		
		//命名sheet
		wb.setSheetName(0, monthDate + "月"+("1".equals(dataFlag)?"月底":"月中")+"库存分品类统计");
		wb.setSheetName(1, monthDate + "月"+("1".equals(dataFlag)?"月底":"月中")+"库存水平统计");
		wb.setSheetName(2, "历史库存统计");

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");

			String fileName = monthDate + "月"+("1".equals(dataFlag)?"月底":"月中")+"库存按月分析" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("按月库存分析导出异常", e);
		}
		return null;
	}
	
}
