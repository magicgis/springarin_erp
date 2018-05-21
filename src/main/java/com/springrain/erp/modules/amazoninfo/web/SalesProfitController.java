package com.springrain.erp.modules.amazoninfo.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.SheetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.SaleProfit;
import com.springrain.erp.modules.amazoninfo.service.SaleProfitService;
import com.springrain.erp.modules.ebay.entity.EbayProductProfit;
import com.springrain.erp.modules.ebay.service.EbayOrderService;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.service.PsiInventoryTurnoverDataService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 利润分析Controller
 */
@SuppressWarnings("all")
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/salesProfits")
public class SalesProfitController extends BaseController {

	@Autowired
	private SaleProfitService saleProfitService;
	
	@Autowired
	private  EbayOrderService ebayOrderService;
	
	@Autowired
	private  PsiInventoryTurnoverDataService turnoverDataService;
	
	private static DateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
	private static DateFormat formatMonth = new SimpleDateFormat("yyyyMM");
	private static DateFormat formatYear = new SimpleDateFormat("yyyy");
	
	/**
	 * 
	 * @param flag	1：按月  2：按天  3:按年
	 * @param groupType	0:按产品  1：按产品类型   2：按产品线
	 * @return
	 */
	@RequestMapping(value = {"list", ""})
	public String list(SaleProfit saleProfit, String flag, String groupType, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if (!UserUtils.hasPermission("amazoninfo:profits:view")) {
			return "redirect:"+Global.getAdminPath()+"/amazoninfo/salesProfits/ebayProfit/?repage";
		}
		if (StringUtils.isNotEmpty(saleProfit.getDay())) {
			saleProfit.setDay(saleProfit.getDay().replaceAll("-", ""));
			saleProfit.setEnd(saleProfit.getEnd().replaceAll("-", ""));
		}
		if (StringUtils.isEmpty(flag)) {
			flag = "1";
		}
		Calendar calendar = Calendar.getInstance();
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		if (StringUtils.isEmpty(saleProfit.getDay())) {
			if ("1".equals(flag)) { //按月
				//采用订单费用接口统计数据，默认显示最新数据
				saleProfit.setDay(formatMonth.format(DateUtils.addDays(calendar.getTime(), -5)));
				saleProfit.setEnd(formatMonth.format(DateUtils.addDays(calendar.getTime(), -5)));
				/*if (dayOfMonth >= 15) {	//15号之后上个月的结算报告都出来了
					saleProfit.setDay(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -1)));
					saleProfit.setEnd(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -1)));
				} else {
					saleProfit.setDay(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -2)));
					saleProfit.setEnd(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -2)));
				}*/
			} else if ("3".equals(flag)){//按年
				saleProfit.setDay(formatYear.format(DateUtils.addMonths(calendar.getTime(), -1)));
				saleProfit.setEnd(formatYear.format(DateUtils.addMonths(calendar.getTime(), -1)));
			} else {
				saleProfit.setDay(formatDay.format(DateUtils.addMonths(calendar.getTime(), -1)));
				saleProfit.setEnd(formatDay.format(DateUtils.addMonths(calendar.getTime(), -1)));
			}
		}
		Date date = null;
		Date date1 = null;
		if ("1".equals(flag)) {
			date = formatDay.parse(saleProfit.getDay() + "01");
			date1 = formatDay.parse(saleProfit.getEnd() + "01");
		} else if ("3".equals(flag)) {
			date = formatDay.parse(saleProfit.getDay() + "0101");
			date1 = formatDay.parse(saleProfit.getEnd() + "0101");
		} else {
			date = formatDay.parse(saleProfit.getDay());
			date1 = formatDay.parse(saleProfit.getEnd());
		}
		model.addAttribute("date", date);
		model.addAttribute("date1", date1);
		saleProfit.setReturnNum(0); //借字段标识为利润分析,过滤type字段为空数据
		List<SaleProfit> list = saleProfitService.getSalesProfitList(saleProfit, flag, groupType).get("1");
		model.addAttribute("list", list);
		model.addAttribute("saleProfit", saleProfit);
		model.addAttribute("flag", flag);
		model.addAttribute("groupType", groupType);
		String message = saleProfitService.getTips(flag, saleProfit);
		if (StringUtils.isNotEmpty(message)) {
			Date sDate = saleProfitService.getSettlementDate(saleProfit);
			Date end = null;
			if ("1".equals(flag)) {
				end = formatDay.parse(saleProfit.getEnd() + "01");
				end = DateUtils.getLastDayOfMonth(end);
			} else if ("3".equals(flag)) {
				end = formatDay.parse(saleProfit.getEnd() + "1231");
			} else {
				end = formatDay.parse(saleProfit.getEnd());
			}
			if (sDate.before(end)) {
				model.addAttribute("message", message);
			}
		}
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry())) {
			model.addAttribute("totalSales", saleProfitService.getSalesProfit(saleProfit, flag));
		}
		Map<String, Map<String, Float>> turnoverRate = turnoverDataService.getTurnoverRateList(saleProfit, flag, groupType);
		model.addAttribute("turnoverRate", turnoverRate);
		return "modules/amazoninfo/sales/amazonSalesProfitList";
	}
	
	/**
	 * B2B销售数据统计
	 * @param flag	1：按月  2：按天  3:按年
	 * @param groupType	0:按产品  1：按产品类型   2：按产品线
	 * @return
	 */
	@RequestMapping(value = {"marketList"})
	public String marketList(SaleProfit saleProfit, String flag, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if (StringUtils.isNotEmpty(saleProfit.getDay())) {
			saleProfit.setDay(saleProfit.getDay().replaceAll("-", ""));
			saleProfit.setEnd(saleProfit.getEnd().replaceAll("-", ""));
		}
		if (StringUtils.isEmpty(flag)) {
			flag = "1";
		}
		Calendar calendar = Calendar.getInstance();
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		if (StringUtils.isEmpty(saleProfit.getDay())) {
			if ("1".equals(flag)) { //按月
				if (dayOfMonth >= 15) {	//15号之后上个月的结算报告都出来了
					saleProfit.setDay(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -1)));
					saleProfit.setEnd(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -1)));
				} else {
					saleProfit.setDay(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -2)));
					saleProfit.setEnd(formatMonth.format(DateUtils.addMonths(calendar.getTime(), -2)));
				}
			} else if ("3".equals(flag)){//按年
				saleProfit.setDay(formatYear.format(DateUtils.addMonths(calendar.getTime(), -1)));
				saleProfit.setEnd(formatYear.format(DateUtils.addMonths(calendar.getTime(), -1)));
			} else {
				saleProfit.setDay(formatDay.format(DateUtils.addMonths(calendar.getTime(), -1)));
				saleProfit.setEnd(formatDay.format(DateUtils.addMonths(calendar.getTime(), -1)));
			}
		}
		Date date = null;
		Date date1 = null;
		if ("1".equals(flag)) {
			date = formatDay.parse(saleProfit.getDay() + "01");
			date1 = formatDay.parse(saleProfit.getEnd() + "01");
		} else if ("3".equals(flag)) {
			date = formatDay.parse(saleProfit.getDay() + "0101");
			date1 = formatDay.parse(saleProfit.getEnd() + "0101");
		} else {
			date = formatDay.parse(saleProfit.getDay());
			date1 = formatDay.parse(saleProfit.getEnd());
		}
		model.addAttribute("date", date);
		model.addAttribute("date1", date1);
		List<SaleProfit> list = saleProfitService.getMarketSalesList(saleProfit, flag);
		model.addAttribute("list", list);
		model.addAttribute("saleProfit", saleProfit);
		model.addAttribute("flag", flag);
		return "modules/amazoninfo/sales/amazonMarketSalesList";
	}

	/**
	 * 
	 * @param flag	1：按月  2：按天
	 * @param groupType	0:按产品  1：按产品类型   2：按产品线
	 * @return
	 */
	@RequestMapping(value = {"export"})
	public String export(SaleProfit saleProfit, String flag, String groupType, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
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

		//普通文本格式
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

		//两位小数
		CellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setWrapText(true);

		//百分比两位小数
		CellStyle contentStyle2 = wb.createCellStyle();
		contentStyle2.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
		contentStyle2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle2.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle2.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle2.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle2.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle2.setWrapText(true);

		//百分比取整
		CellStyle contentStyle3 = wb.createCellStyle();
		contentStyle3.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));
		contentStyle3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle3.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle3.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle3.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle3.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle3.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle3.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle3.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle3.setWrapText(true);
		HSSFCell cell = null;
		
		saleProfit.setDay(saleProfit.getDay().replaceAll("-", ""));
		saleProfit.setEnd(saleProfit.getEnd().replaceAll("-", ""));
		Map<String, List<SaleProfit>> map = saleProfitService.getSalesProfitList(saleProfit, flag, groupType);
		//存货周转率
		Map<String, Map<String, Float>> turnoverRate = Maps.newHashMap();
		List<SaleProfit> list = map.get("1");
		SaleProfit totalProfit = map.get("2").get(0);
		String name = "产品名称";
		if ("1".equals(groupType)) {
			name = "产品类型";
		} else if ("2".equals(groupType)) {
			name = "产品线";
		}
		List<String> title = Lists.newArrayList();
		title.add(name);
		if (!"2".equals(groupType)) {
			title.add("产品线");
		}
		title.add("日期");
		title.add("销量");
		title.add("销售额(€)");
		title.add("税后收入(€)");
		title.add("退款(€)");
		title.add("亚马逊佣金(€)");
		title.add("杂费(€)");
		title.add("运输费(€)");
		title.add("采购成本(€)");
		title.add("利润(€)");
		title.add("利润占比");
		title.add("单个利润(€)");
		title.add("利润率");
		title.add("平均售价(€)");
		title.add("站内market广告费用(€)");
		title.add("站内market广告销量(€)");
		title.add("站内market广告销售额(€)");
		title.add("站内sales广告费用(€)");
		title.add("站内sales广告销量(€)");
		title.add("站内sales广告销售额(€)");
		title.add("站外market广告费用(€)");
		title.add("站外market广告销量(€)");
		title.add("站外market广告销售额(€)");
		title.add("站外sales广告费用(€)");
		title.add("站外sales广告销量(€)");
		title.add("站外sales广告销售额(€)");
		title.add("AMS广告费用(€)");
		title.add("AMS广告销量(€)");
		title.add("AMS广告销售额(€)");
		title.add("替代货数量(€)");
		title.add("替代货成本(€)");
		title.add("替代货亚马逊费用(€)");
		title.add("评测单数量(€)");
		title.add("评测单成本(€)");
		title.add("评测单亚马逊费用(€)");
		title.add("召回数量");
		title.add("召回成本(€)");
		title.add("召回费用(€)");
		title.add("关税(€)");
		if (!"2".equals(flag)) {	//不是按天导出的增加存货周转率
			title.add("存货周转率");
			title.add("财务成本");
			turnoverRate = turnoverDataService.getTurnoverRateList(saleProfit, flag, groupType);
		}
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		
		int rowIndex = 1;
		for (int i = 0; i < list.size(); i++) {
			SaleProfit profit = list.get(i);
			int j = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProductName());
			row.getCell(j-1).setCellStyle(contentStyle);
			if (!"2".equals(groupType)) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getLine());
				row.getCell(j-1).setCellStyle(contentStyle);
			}
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getDay());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSalesVolume());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSalesNoTax());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getRefund());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAmazonFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getOtherFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getTransportFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getBuyCost());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProfits());
			row.getCell(j-1).setCellStyle(contentStyle1);
			if (totalProfit != null && totalProfit.getProfits() > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProfits()/totalProfit.getProfits());
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			row.getCell(j-1).setCellStyle(contentStyle2);
			if (profit.getSalesVolume() > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProfits()/profit.getSalesVolume());
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProfitRate());
			row.getCell(j-1).setCellStyle(contentStyle3);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAvgSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//站内广告
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdInProfitFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdInProfitSalesVolume());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdInProfitSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdInEventFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdInEventSalesVolume());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdInEventSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//站外广告
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdOutProfitFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdOutProfitSalesVolume());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdOutProfitSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdOutEventFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdOutEventSalesVolume());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdOutEventSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//AMS广告
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdAmsFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdAmsSalesVolume());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdAmsSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//替代货和评测单
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSupportNum());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSupportCost());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSupportAmazonFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getReviewNum());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getReviewCost());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getReviewAmazonFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//召回数量和成本
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getRecallNum());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getRecallCost());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//召回费用
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getRecallFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//关税
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getTariff());
			row.getCell(j-1).setCellStyle(contentStyle1);
			if (!"2".equals(flag)) {	//存货周转率
				float rate = 0;
				try {
					rate = turnoverRate.get(profit.getDay()).get(profit.getProductName());
				} catch (Exception e) {}
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(rate);
				row.getCell(j-1).setCellStyle(contentStyle1);
				if (rate > 0) {
					if ("1".equals(flag)) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue((profit.getBuyCost()+profit.getTransportFee())/rate*0.15/12);
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue((profit.getBuyCost()+profit.getTransportFee())/rate*0.15);
					}
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				row.getCell(j-1).setCellStyle(contentStyle1);
			}
		}
		//总计
		int m = 0;
		row = sheet.createRow(rowIndex++);
		row.setHeight((short) 400);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("Total");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		if (!"2".equals(groupType)) {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
		}
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSalesNoTax());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRefund());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAmazonFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getOtherFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getTransportFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getBuyCost());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getProfits());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");	//总计行不需要计算利润占比
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getProfits()/totalProfit.getSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");	//总计行不计算利润率
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAvgSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//站内广告
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInProfitFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInProfitSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInProfitSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInEventFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInEventSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInEventSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//站外广告
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutProfitFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutProfitSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutProfitSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutEventFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutEventSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutEventSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//AMS广告
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdAmsFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdAmsSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdAmsSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//替代货和评测单
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSupportNum());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSupportCost());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSupportAmazonFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getReviewNum());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getReviewCost());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getReviewAmazonFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//召回数量和成本
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRecallNum());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRecallCost());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//召回费用
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRecallFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//关税
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getTariff());
		row.getCell(m-1).setCellStyle(contentStyle1);
		if (!"2".equals(flag)) {	//存货周转率
			float rate = 0;
			try {
				rate = turnoverRate.get("total").get("total");
			} catch (Exception e) {}
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(rate);
			row.getCell(m-1).setCellStyle(contentStyle1);
			if (rate > 0) {
				if ("1".equals(flag)) {
					row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue((totalProfit.getBuyCost()+totalProfit.getTransportFee())/rate*0.15/12);
				} else {
					row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue((totalProfit.getBuyCost()+totalProfit.getTransportFee())/rate*0.15);
				}
			} else {
				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			row.getCell(m-1).setCellStyle(contentStyle1);
		}
		//统计占比
		m = 0;
		row = sheet.createRow(rowIndex++);
		row.setHeight((short) 400);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("统计");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		if (!"2".equals(groupType)) {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
		}
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		if (StringUtils.isEmpty(saleProfit.getCountry())) {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		} else {
			float totalSales = saleProfitService.getSalesProfit(saleProfit, flag);
			if (totalSales > 0) {
				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSales()/totalSales);
			} else {
				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
		}
		row.getCell(m-1).setCellStyle(contentStyle2);
		if (totalProfit.getSales() > 0) {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSalesNoTax()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRefund()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAmazonFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getOtherFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getTransportFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getBuyCost()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getProfits()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
		} else {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
		}
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		if (totalProfit.getSales() > 0) {
			//站内广告
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInProfitFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInProfitSales()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInEventFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInEventSales()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			//站外广告
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutProfitFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutProfitSales()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutEventFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutEventSales()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			//AMS广告
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdAmsFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdAmsSales()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			//替代货和评测单
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSupportCost()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSupportAmazonFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getReviewCost()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getReviewAmazonFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			//召回数量和成本
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRecallCost()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			//召回费用
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRecallFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			//关税
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getTariff()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
		} else {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		}
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			String fileName = "";
			String country = saleProfit.getCountry();
			if (StringUtils.isEmpty(country)) {
				country = "全平台";
			} else {
				if ("en".equals(country)) {
					country = "英语国家";
				} else if ("eu".equals(country)) {
					country = "欧洲";
				} else if ("nonEn".equals(country)) {
					country = "非英语国家";
				} else if ("noUs".equals(country)) {
					country = "全平台不含美国";
				} else {
					country = SystemService.countryNameMap.get(country);
				}
			}
			if ("1".equals(flag)) {
				if (saleProfit.getDay().equals(saleProfit.getEnd())) {
					fileName = saleProfit.getDay() + "月"+country+"分"+("产品名称".equals(name)?"产品":name)+"利润分析报表.xls";
				} else {
					fileName = saleProfit.getDay() + "月~" + saleProfit.getEnd() + "月"+country+"分"+("产品名称".equals(name)?"产品":name)+"利润分析报表.xls";
				}
			} else if ("3".equals(flag)) {
				if (saleProfit.getDay().equals(saleProfit.getEnd())) {
					fileName = saleProfit.getDay() + "年"+country+"分"+("产品名称".equals(name)?"产品":name)+"利润分析报表.xls";
				} else {
					fileName = saleProfit.getDay() + "年~" + saleProfit.getEnd() + "年"+country+"分"+("产品名称".equals(name)?"产品":name)+"利润分析报表.xls";
				}
			} else {
				fileName = saleProfit.getDay() + "日~" + saleProfit.getEnd() + "日"+country+"分"+("产品名称".equals(name)?"产品":name)+"利润分析报表.xls";
			}
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("利润分析报表导出异常", e);
		}
		return null;
	}
	
	@RequestMapping(value = {"ebayProfit"})
	public String ebayProfit(EbayProductProfit profit,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		if(StringUtils.isBlank(profit.getType())||"1".equals(profit.getType())){
			profit.setType("1");
			dateFormat=new SimpleDateFormat("yyyy-MM");
		}
		if (StringUtils.isEmpty(profit.getCountry())) {
			profit.setCountry("de");
		}

	    if(StringUtils.isBlank(profit.getStart())){
	    	Date date=new Date();
	    	date.setHours(0);
	    	date.setMinutes(0);
	    	date.setSeconds(0);
	    	profit.setStart(dateFormat.format(date));
	    	profit.setEnd(dateFormat.format(date));
	    }

		model.addAttribute("date", dateFormat.parse(profit.getStart()));
		model.addAttribute("date1", dateFormat.parse(profit.getEnd()));
		
	    List<EbayProductProfit> profitList=ebayOrderService.find(profit);
    	model.addAttribute("profitList", profitList);
	    model.addAttribute("profit", profit);
	    return "modules/ebay/order/ebaySalesProfitList";
	}	
	
	/**
	 * 运营利润分析(增加运营成本，销售额的10%)
	 * @param flag	1：按月  2：按天  3:按年
	 * @param groupType	0:按产品  1：按产品类型   2：按产品线
	 * @return
	 */
	@RequestMapping(value = "sales")
	public String sales(SaleProfit saleProfit, String flag, String groupType, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if (StringUtils.isNotEmpty(saleProfit.getDay())) {
			saleProfit.setDay(saleProfit.getDay().replaceAll("-", ""));
			saleProfit.setEnd(saleProfit.getEnd().replaceAll("-", ""));
		}
		if (StringUtils.isEmpty(flag)) {
			flag = "1";
		}
		Calendar calendar = Calendar.getInstance();
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		if (StringUtils.isEmpty(saleProfit.getDay())) {
			if ("1".equals(flag)) { //按月
				//采用订单费用接口统计数据，默认显示最新数据
				saleProfit.setDay(formatMonth.format(DateUtils.addDays(calendar.getTime(), -5)));
				saleProfit.setEnd(formatMonth.format(DateUtils.addDays(calendar.getTime(), -5)));
			} else if ("3".equals(flag)){//按年
				saleProfit.setDay(formatYear.format(DateUtils.addMonths(calendar.getTime(), -1)));
				saleProfit.setEnd(formatYear.format(DateUtils.addMonths(calendar.getTime(), -1)));
			} else {
				saleProfit.setDay(formatDay.format(DateUtils.addMonths(calendar.getTime(), -1)));
				saleProfit.setEnd(formatDay.format(DateUtils.addMonths(calendar.getTime(), -1)));
			}
		}
		Date date = null;
		Date date1 = null;
		if ("1".equals(flag)) {
			date = formatDay.parse(saleProfit.getDay() + "01");
			date1 = formatDay.parse(saleProfit.getEnd() + "01");
		} else if ("3".equals(flag)) {
			date = formatDay.parse(saleProfit.getDay() + "0101");
			date1 = formatDay.parse(saleProfit.getEnd() + "0101");
		} else {
			date = formatDay.parse(saleProfit.getDay());
			date1 = formatDay.parse(saleProfit.getEnd());
		}
		model.addAttribute("date", date);
		model.addAttribute("date1", date1);
		saleProfit.setReturnNum(0); //借字段标识为利润分析,过滤type字段为空数据
		List<SaleProfit> list = saleProfitService.getSalesProfitList(saleProfit, flag, groupType).get("1");
		model.addAttribute("list", list);
		model.addAttribute("saleProfit", saleProfit);
		model.addAttribute("flag", flag);
		model.addAttribute("groupType", groupType);
		String message = saleProfitService.getTips(flag, saleProfit);
		if (StringUtils.isNotEmpty(message)) {
			Date sDate = saleProfitService.getSettlementDate(saleProfit);
			Date end = null;
			if ("1".equals(flag)) {
				end = formatDay.parse(saleProfit.getEnd() + "01");
				end = DateUtils.getLastDayOfMonth(end);
			} else if ("3".equals(flag)) {
				end = formatDay.parse(saleProfit.getEnd() + "1231");
			} else {
				end = formatDay.parse(saleProfit.getEnd());
			}
			if (sDate.before(end)) {
				model.addAttribute("message", message);
			}
		}
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry())) {
			model.addAttribute("totalSales", saleProfitService.getSalesProfit(saleProfit, flag));
		}
		Map<String, Map<String, Float>> turnoverRate = turnoverDataService.getTurnoverRateList(saleProfit, flag, groupType);
		model.addAttribute("turnoverRate", turnoverRate);
		return "modules/amazoninfo/sales/amazonSalesProfitList1";
	}

	/**
	 * 
	 * @param flag	1：按月  2：按天
	 * @param groupType	0:按产品  1：按产品类型   2：按产品线
	 * @return
	 */
	@RequestMapping(value = {"exportSales"})
	public String exportSales(SaleProfit saleProfit, String flag, String groupType, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
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

		//普通文本格式
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

		//两位小数
		CellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setWrapText(true);

		//百分比两位小数
		CellStyle contentStyle2 = wb.createCellStyle();
		contentStyle2.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
		contentStyle2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle2.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle2.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle2.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle2.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle2.setWrapText(true);

		//百分比取整
		CellStyle contentStyle3 = wb.createCellStyle();
		contentStyle3.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));
		contentStyle3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle3.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle3.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle3.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle3.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle3.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle3.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle3.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle3.setWrapText(true);
		HSSFCell cell = null;
		
		saleProfit.setDay(saleProfit.getDay().replaceAll("-", ""));
		saleProfit.setEnd(saleProfit.getEnd().replaceAll("-", ""));
		Map<String, List<SaleProfit>> map = saleProfitService.getSalesProfitList(saleProfit, flag, groupType);
		//存货周转率
		Map<String, Map<String, Float>> turnoverRate = Maps.newHashMap();
		List<SaleProfit> list = map.get("1");
		SaleProfit totalProfit = map.get("2").get(0);
		String name = "产品名称";
		if ("1".equals(groupType)) {
			name = "产品类型";
		} else if ("2".equals(groupType)) {
			name = "产品线";
		}
		List<String> title = Lists.newArrayList();
		title.add(name);
		if (!"2".equals(groupType)) {
			title.add("产品线");
		}
		title.add("日期");
		title.add("销量");
		title.add("销售额(€)");
		title.add("税后收入(€)");
		title.add("退款(€)");
		title.add("亚马逊佣金(€)");
		if (!"0".equals(groupType)) {
			title.add("运输费(€)");
			title.add("采购成本(€)");
		}
		title.add("毛利润(€)");
		title.add("利润(€)");
		title.add("利润占比");
		title.add("单个利润(€)");
		title.add("毛利率");
		title.add("利润率");
		title.add("平均售价(€)");
		title.add("站内market广告费用(€)");
		title.add("站内market广告销量(€)");
		title.add("站内market广告销售额(€)");
		title.add("站内sales广告费用(€)");
		title.add("站内sales广告销量(€)");
		title.add("站内sales广告销售额(€)");
		title.add("站外market广告费用(€)");
		title.add("站外market广告销量(€)");
		title.add("站外market广告销售额(€)");
		title.add("站外sales广告费用(€)");
		title.add("站外sales广告销量(€)");
		title.add("站外sales广告销售额(€)");
		title.add("AMS广告费用(€)");
		title.add("AMS广告销量(€)");
		title.add("AMS广告销售额(€)");
		title.add("替代货数量(€)");
		title.add("替代货成本(€)");
		title.add("替代货亚马逊费用(€)");
		title.add("评测单数量(€)");
		title.add("评测单成本(€)");
		title.add("评测单亚马逊费用(€)");
		title.add("召回数量");
		title.add("召回成本(€)");
		title.add("召回费用(€)");
		title.add("月仓储费(€)");
		title.add("长期仓储费(€)");
		if (!"2".equals(flag)) {	//不是按天导出的增加存货周转率
			title.add("存货周转率");
			title.add("财务成本");
			turnoverRate = turnoverDataService.getTurnoverRateList(saleProfit, flag, groupType);
		}
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		
		int rowIndex = 1;
		for (int i = 0; i < list.size(); i++) {
			SaleProfit profit = list.get(i);
			int j = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProductName());
			row.getCell(j-1).setCellStyle(contentStyle);
			if (!"2".equals(groupType)) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getLine());
				row.getCell(j-1).setCellStyle(contentStyle);
			}
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getDay());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSalesVolume());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSalesNoTax());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getRefund());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAmazonFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			if (!"0".equals(groupType)) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getTransportFee());
				row.getCell(j-1).setCellStyle(contentStyle1);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getBuyCost());
				row.getCell(j-1).setCellStyle(contentStyle1);
			}
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProfits());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProfits()-profit.getSales()*0.1);
			row.getCell(j-1).setCellStyle(contentStyle1);
			if (totalProfit != null && totalProfit.getProfits() > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProfits()/totalProfit.getProfits());
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			row.getCell(j-1).setCellStyle(contentStyle2);
			if (profit.getSalesVolume() > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProfits()/profit.getSalesVolume());
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProfitRate());
			row.getCell(j-1).setCellStyle(contentStyle3);
			if (profit.getSales() > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue((profit.getProfits()-profit.getSales()*0.1)/profit.getSales());
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			row.getCell(j-1).setCellStyle(contentStyle3);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAvgSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//站内广告
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdInProfitFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdInProfitSalesVolume());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdInProfitSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdInEventFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdInEventSalesVolume());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdInEventSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//站外广告
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdOutProfitFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdOutProfitSalesVolume());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdOutProfitSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdOutEventFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdOutEventSalesVolume());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdOutEventSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//AMS广告
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdAmsFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdAmsSalesVolume());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getAdAmsSales());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//替代货和评测单
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSupportNum());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSupportCost());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSupportAmazonFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getReviewNum());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getReviewCost());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getReviewAmazonFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//召回数量和成本
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getRecallNum());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getRecallCost());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//召回费用
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getRecallFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			//仓储费
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getStorageFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getLongStorageFee());
			row.getCell(j-1).setCellStyle(contentStyle1);
			if (!"2".equals(flag)) {	//存货周转率
				float rate = 0;
				try {
					rate = turnoverRate.get(profit.getDay()).get(profit.getProductName());
				} catch (Exception e) {}
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(rate);
				row.getCell(j-1).setCellStyle(contentStyle1);
				if (rate > 0) {
					if ("1".equals(flag)) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue((profit.getBuyCost()+profit.getTransportFee())/rate*0.15/12);
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue((profit.getBuyCost()+profit.getTransportFee())/rate*0.15);
					}
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				row.getCell(j-1).setCellStyle(contentStyle1);
			}
		}
		//总计
		int m = 0;
		row = sheet.createRow(rowIndex++);
		row.setHeight((short) 400);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("Total");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		if (!"2".equals(groupType)) {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
		}
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSalesNoTax());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRefund());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAmazonFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		if (!"0".equals(groupType)) {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getTransportFee());
			row.getCell(m-1).setCellStyle(contentStyle1);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getBuyCost());
			row.getCell(m-1).setCellStyle(contentStyle1);
		}
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getProfits());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getProfits()-totalProfit.getSales()*0.1);
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");	//总计行不需要计算利润占比
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getProfits()/totalProfit.getSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");	//总计行不计算毛利润率
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");	//总计行不计算利润率
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAvgSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//站内广告
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInProfitFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInProfitSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInProfitSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInEventFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInEventSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInEventSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//站外广告
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutProfitFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutProfitSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutProfitSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutEventFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutEventSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutEventSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//AMS广告
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdAmsFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdAmsSalesVolume());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdAmsSales());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//替代货和评测单
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSupportNum());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSupportCost());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSupportAmazonFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getReviewNum());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getReviewCost());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getReviewAmazonFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//召回数量和成本
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRecallNum());
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRecallCost());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//召回费用
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRecallFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		//仓储费
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getStorageFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getLongStorageFee());
		row.getCell(m-1).setCellStyle(contentStyle1);
		if (!"2".equals(flag)) {	//存货周转率
			float rate = 0;
			try {
				rate = turnoverRate.get("total").get("total");
			} catch (Exception e) {}
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(rate);
			row.getCell(m-1).setCellStyle(contentStyle1);
			if (rate > 0) {
				if ("1".equals(flag)) {
					row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue((totalProfit.getBuyCost()+totalProfit.getTransportFee())/rate*0.15/12);
				} else {
					row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue((totalProfit.getBuyCost()+totalProfit.getTransportFee())/rate*0.15);
				}
			} else {
				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			row.getCell(m-1).setCellStyle(contentStyle1);
		}
		//统计占比
		m = 0;
		row = sheet.createRow(rowIndex++);
		row.setHeight((short) 400);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("统计");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		if (!"2".equals(groupType)) {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
		}
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		if (StringUtils.isEmpty(saleProfit.getCountry())) {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		} else {
			float totalSales = saleProfitService.getSalesProfit(saleProfit, flag);
			if (totalSales > 0) {
				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSales()/totalSales);
			} else {
				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
		}
		row.getCell(m-1).setCellStyle(contentStyle2);
		if (totalProfit.getSales() > 0) {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSalesNoTax()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRefund()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAmazonFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			if (!"0".equals(groupType)) {
				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getTransportFee()/totalProfit.getSales());
				row.getCell(m-1).setCellStyle(contentStyle2);
				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getBuyCost()/totalProfit.getSales());
				row.getCell(m-1).setCellStyle(contentStyle2);
			}
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getProfits()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue((totalProfit.getProfits()-totalProfit.getSales()*0.1)/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
		} else {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			if (!"0".equals(groupType)) {
				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle);
				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle);
			}
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
		}
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		if (totalProfit.getSales() > 0) {
			//站内广告
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInProfitFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInProfitSales()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInEventFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdInEventSales()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			//站外广告
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutProfitFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutProfitSales()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutEventFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdOutEventSales()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			//AMS广告
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdAmsFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getAdAmsSales()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			//替代货和评测单
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSupportCost()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getSupportAmazonFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getReviewCost()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getReviewAmazonFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			//召回数量和成本
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(m-1).setCellStyle(contentStyle);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRecallCost()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			//召回费用
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getRecallFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			//仓储费
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getStorageFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalProfit.getLongStorageFee()/totalProfit.getSales());
			row.getCell(m-1).setCellStyle(contentStyle2);
		} else {
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		}
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(m-1).setCellStyle(contentStyle);

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			String fileName = "";
			String country = saleProfit.getCountry();
			if (StringUtils.isEmpty(country)) {
				country = "全平台";
			} else {
				if ("en".equals(country)) {
					country = "英语国家";
				} else if ("eu".equals(country)) {
					country = "欧洲";
				} else if ("nonEn".equals(country)) {
					country = "非英语国家";
				} else if ("noUs".equals(country)) {
					country = "全平台不含美国";
				} else {
					country = SystemService.countryNameMap.get(country);
				}
			}
			if ("1".equals(flag)) {
				if (saleProfit.getDay().equals(saleProfit.getEnd())) {
					fileName = saleProfit.getDay() + "月"+country+"分"+("产品名称".equals(name)?"产品":name)+"运营利润分析报表.xls";
				} else {
					fileName = saleProfit.getDay() + "月~" + saleProfit.getEnd() + "月"+country+"分"+("产品名称".equals(name)?"产品":name)+"运营利润分析报表.xls";
				}
			} else if ("3".equals(flag)) {
				if (saleProfit.getDay().equals(saleProfit.getEnd())) {
					fileName = saleProfit.getDay() + "年"+country+"分"+("产品名称".equals(name)?"产品":name)+"运营利润分析报表.xls";
				} else {
					fileName = saleProfit.getDay() + "年~" + saleProfit.getEnd() + "年"+country+"分"+("产品名称".equals(name)?"产品":name)+"运营利润分析报表.xls";
				}
			} else {
				fileName = saleProfit.getDay() + "日~" + saleProfit.getEnd() + "日"+country+"分"+("产品名称".equals(name)?"产品":name)+"运营利润分析报表.xls";
			}
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("运营利润分析报表导出异常", e);
		}
		return null;
	}
	
	
	@RequestMapping(value = "exportMarket")
	public String exportMarket(SaleProfit saleProfit, String flag, HttpServletRequest request,HttpServletResponse response, Model model) {
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

		//两位小数
		CellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setWrapText(true);
		HSSFCell cell = null;
		
		String type = "日";
		if ("1".equals(flag)) {
			type = "月";
		} else if ("3".equals(flag)) {
			type = "年";
		}
		saleProfit.setDay(saleProfit.getDay().replaceAll("-", ""));
		saleProfit.setEnd(saleProfit.getEnd().replaceAll("-", ""));
		List<SaleProfit> list = saleProfitService.getMarketSalesList(saleProfit, flag);
		List<String> title = Lists.newArrayList("日期", "产品名称", "产品线", "平台", "销量", "销售额", "利润");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		List<Integer> index = new ArrayList<Integer>();
		int rowIndex = 1;
		for (int i = 0; i < list.size(); i++) {
			SaleProfit profit = list.get(i);
			int j = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getDay() + type);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProductName());
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getLine() + "线");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("com".equals(profit.getCountry())?"US":profit.getCountry().toUpperCase());
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSalesVolume());
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getSales());
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(profit.getProfits());
		}

		for (int i = 0; i < rowIndex - 1; i++) {
			for (int j = 0; j < title.size(); j++) {
				if (j == 5 || j == 6) {
					sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle1);
				} else {
					sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
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

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");

			String fileName = "B2B销售数据" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("产品分平台属性导出异常", e);
		}
		return null;
	}
}
