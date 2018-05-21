package com.springrain.erp.modules.amazoninfo.web.order;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
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
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.SettlementReportOrderService;
import com.springrain.erp.modules.sys.utils.DictUtils;

/**
 * 订单费用结算统计Controller
 * 
 * @author leehong
 * @version 2015-10-26
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/orderCost")
public class AmazonOrderCostController extends BaseController {

	@Autowired
	private SettlementReportOrderService settlementReportOrderService;

	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = { "list", "" })
	public String list(Date startTime, Date endTime, String country, Model model) {
		if(startTime==null){
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			endTime = today;
			startTime = DateUtils.addMonths(endTime, -1);
		}
		List<String> settlementIds = Lists.newArrayList();
		Map<String, String> countryMap = settlementReportOrderService.coverCountry(startTime, endTime);

		//拿到时间范围内的settId
		Map<String, List<String>> map = settlementReportOrderService.coverSettlementId(startTime, endTime);
		settlementIds = map.get("total");
		List<Object[]> results = settlementReportOrderService.find(settlementIds);
		Map<String, List<Object[]>> countrySettlementMap = exchange(results, countryMap);
		
		model.addAttribute("countrySettlementMap", countrySettlementMap);
		model.addAttribute("results", results);
		model.addAttribute("countryMap", countryMap);
		model.addAttribute("country", country);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		model.addAttribute("currencySymbol", getCurrencySymbol());
		return "modules/amazoninfo/order/amazonOrderCostList";
	}

	@RequestMapping(value = "expOrderCost")
	public String expOrderCost(String settlementId, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		List<Object[]> titleList = settlementReportOrderService.findTitle(settlementId);
		List<Object[]> detailList = settlementReportOrderService.findDetail(settlementId);

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

		CellStyle titleStyle = wb.createCellStyle();
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 15); // 字体高度
		font1.setFontName(" 黑体 "); // 字体
		font1.setBoldweight((short) 15);
		titleStyle.setFont(font1);
		
		Object[] objectTitle = titleList.get(0);
		String currencyType = "EUR";
		String country = DictUtils.getDictLabel(objectTitle[0].toString(), "platform", "");
		if (objectTitle[0] != null) {
			currencyType = getCurrencySymbol().get(objectTitle[0].toString());
		}

		HSSFCell cell = null;
		List<String> title = Lists.newArrayList( "transaction_type","   order_id   ", "sales("+currencyType+")", "amazon_fee("+currencyType+")", "actual_income("+currencyType+")");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int rowIndex = 1;
		if (detailList != null) {
			double saleTotal = 0;
			double feeTotal = 0;
			for (int i = 0; i < detailList.size(); i++) {
				Object[] objects = detailList.get(i);
				double sales = ((BigDecimal)(objects[2])).doubleValue();
				double fee = ((BigDecimal)(objects[3])).doubleValue();
				saleTotal += sales;
				feeTotal += fee;
				
				int j = 0;
				row = sheet.createRow(rowIndex++);
				row.setHeight((short) 400);
				row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(objects[1].toString());
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(objects[0]==null?"":objects[0].toString());
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(fee);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(sales + fee);
			}
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue("Total");
			row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(saleTotal);
			row.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(feeTotal);
			row.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(saleTotal + feeTotal);

			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");


				String fileName = country + "_OrderReportFeeSettlement_";
				fileName = URLEncoder.encode(fileName, "UTF-8")+settlementId+"_("+ objectTitle[2] +"-" + objectTitle[3] + ").xls";
				fileName = fileName.replaceAll("%7C", "|");
				response.addHeader("Content-Disposition",
						"attachment;filename=" + fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@RequestMapping(value = "expOrderCostDetail")
	public String expOrderCostDetail(String settlementId, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		List<Object[]> titleList = settlementReportOrderService.findTitle(settlementId);
		List<Object[]> detailList = settlementReportOrderService.findOrderDetail(settlementId);

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

		CellStyle titleStyle = wb.createCellStyle();
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 15); // 字体高度
		font1.setFontName(" 黑体 "); // 字体
		font1.setBoldweight((short) 15);
		titleStyle.setFont(font1);
		
		Object[] objectTitle = titleList.get(0);
		String currencyType = "EUR";
		String country = DictUtils.getDictLabel(objectTitle[0].toString(), "platform", "");
		if (objectTitle[0] != null) {
			currencyType = getCurrencySymbol().get(objectTitle[0].toString());
		}

		HSSFCell cell = null;
		List<String> title = Lists.newArrayList( "transaction_type","    orde_id    ", "principal("+currencyType+")", 
				"shipping("+currencyType+")", "cod("+currencyType+")", "cross_border_fulfillment_fee("+currencyType+")", 
				"fba_per_unit_fulfillment_fee("+currencyType+")", "fba_weight_based_fee("+currencyType+")", 
				"commission("+currencyType+")", "shipping_chargeback("+currencyType+")", "giftwrap_chargeback("+currencyType+")", 
				"refund_commission("+currencyType+")", "restocking_fee("+currencyType+")","promotion("+currencyType+")",
				"cod_fee("+currencyType+")", "other_fee("+currencyType+")", "shipping_hb("+currencyType+")", "shipment_fee("+currencyType+")", 
				"fba_per_order_fulfillment_fee("+currencyType+")", "gift_wrap("+currencyType+")", "goodwill("+currencyType+")");
		
		
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int rowIndex = 1;
		if (detailList != null) {
			double principalTotal = 0;
			double shippingTotal = 0;
			double codTotal = 0;
			double shippingHbTotal = 0;
			double shipmentFeeTotal = 0;
			double giftWrapTotal = 0;
			double goodwillTotal = 0;
			double crossBorderFulfillmentFeeTotal = 0;
			double fbaPerUnitFulfillmentFeeTotal = 0;
			double FbaPerOrderFulfillmentFeeTotal = 0;
			double fbaWeightBasedFeeTotal = 0;
			double commissionTotal = 0;
			double shippingChargebackTotal = 0;
			double giftwrapChargebackTotal = 0;
			double refundCommissionTotal = 0;
			double restockingFeeTotal = 0;
			double promotionTotal = 0;
			double codFeeTotal = 0;
			double otherFeeTotal = 0;
			for (int i = 0; i < detailList.size(); i++) {
				Object[] objects = detailList.get(i);
				double principal = ((BigDecimal)(objects[2])).doubleValue();
				double shipping = ((BigDecimal)(objects[3])).doubleValue();
				double cod = ((BigDecimal)(objects[4])).doubleValue();
				double crossBorderFulfillmentFee = ((BigDecimal)(objects[5])).doubleValue();
				double fbaPerUnitFulfillmentFee = ((BigDecimal)(objects[6])).doubleValue();
				double fbaWeightBasedFee = ((BigDecimal)(objects[7])).doubleValue();
				double commission = ((BigDecimal)(objects[8])).doubleValue();
				double shippingChargeback = ((BigDecimal)(objects[9])).doubleValue();
				double giftwrapChargeback = ((BigDecimal)(objects[10])).doubleValue();
				double refundCommission = ((BigDecimal)(objects[11])).doubleValue();
				double restockingFee = ((BigDecimal)(objects[12])).doubleValue();
				double promotion = ((BigDecimal)(objects[13])).doubleValue();
				double codFee = ((BigDecimal)(objects[14])).doubleValue();
				double otherFee = ((BigDecimal)(objects[15])).doubleValue();
				double shippingHb = ((BigDecimal)(objects[16])).doubleValue();
				double shipmentFee = ((BigDecimal)(objects[17])).doubleValue();
				double FbaPerOrderFulfillmentFee = ((BigDecimal)(objects[18])).doubleValue();
				double giftWrap = ((BigDecimal)(objects[19])).doubleValue();
				double goodwill = ((BigDecimal)(objects[20])).doubleValue();
				
				principalTotal += principal;
				shippingTotal += shipping;
				codTotal += cod;
				crossBorderFulfillmentFeeTotal += crossBorderFulfillmentFee;
				fbaPerUnitFulfillmentFeeTotal += fbaPerUnitFulfillmentFee;
				fbaWeightBasedFeeTotal += fbaWeightBasedFee;
				commissionTotal += commission;
				shippingChargebackTotal += shippingChargeback;
				giftwrapChargebackTotal += giftwrapChargeback;
				refundCommissionTotal += refundCommission;
				restockingFeeTotal += restockingFee;
				promotionTotal += promotion;
				codFeeTotal += codFee;
				otherFeeTotal += otherFee;
				shippingHbTotal += shippingHb;
				shipmentFeeTotal += shipmentFee;
				FbaPerOrderFulfillmentFeeTotal += FbaPerOrderFulfillmentFee;
				giftWrapTotal += giftWrap;
				goodwillTotal += goodwill;
				
				int j = 0;
				row = sheet.createRow(rowIndex++);
				row.setHeight((short) 400);
				row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(objects[1].toString());
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(objects[0]==null?"":objects[0].toString());
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(principal);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(shipping);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(cod);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(crossBorderFulfillmentFee);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(fbaPerUnitFulfillmentFee);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(fbaWeightBasedFee);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(commission);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(shippingChargeback);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(giftwrapChargeback);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(refundCommission);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(restockingFee);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(promotion);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(codFee);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(otherFee);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(shippingHb);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(shipmentFee);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(FbaPerOrderFulfillmentFee);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(giftWrap);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(goodwill);
			}
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue("Total");
			row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(principalTotal);
			row.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(shippingTotal);
			row.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(codTotal);
			row.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(crossBorderFulfillmentFeeTotal);
			row.createCell(6, Cell.CELL_TYPE_STRING).setCellValue(fbaPerUnitFulfillmentFeeTotal);
			row.createCell(7, Cell.CELL_TYPE_STRING).setCellValue(fbaWeightBasedFeeTotal);
			row.createCell(8, Cell.CELL_TYPE_STRING).setCellValue(commissionTotal);
			row.createCell(9, Cell.CELL_TYPE_STRING).setCellValue(shippingChargebackTotal);
			row.createCell(10, Cell.CELL_TYPE_STRING).setCellValue(giftwrapChargebackTotal);
			row.createCell(11, Cell.CELL_TYPE_STRING).setCellValue(refundCommissionTotal);
			row.createCell(12, Cell.CELL_TYPE_STRING).setCellValue(restockingFeeTotal);
			row.createCell(13, Cell.CELL_TYPE_STRING).setCellValue(promotionTotal);
			row.createCell(14, Cell.CELL_TYPE_STRING).setCellValue(codFeeTotal);
			row.createCell(15, Cell.CELL_TYPE_STRING).setCellValue(otherFeeTotal);
			row.createCell(16, Cell.CELL_TYPE_STRING).setCellValue(shippingHbTotal);
			row.createCell(17, Cell.CELL_TYPE_STRING).setCellValue(shipmentFeeTotal);
			row.createCell(18, Cell.CELL_TYPE_STRING).setCellValue(FbaPerOrderFulfillmentFeeTotal);
			row.createCell(19, Cell.CELL_TYPE_STRING).setCellValue(giftWrapTotal);
			row.createCell(20, Cell.CELL_TYPE_STRING).setCellValue(goodwillTotal);

			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");


				String fileName = country + "_OrderBillingDetailsStatistics_";
				fileName = URLEncoder.encode(fileName, "UTF-8")+settlementId+"_("+ objectTitle[2] +"-" + objectTitle[3] + ").xls";
				fileName = fileName.replaceAll("%7C", "|");
				response.addHeader("Content-Disposition",
						"attachment;filename=" + fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static Map<String, List<Object[]>> exchange(List<Object[]> results, Map<String, String> countryMap){
		Map<String, List<Object[]>> countrySettlementMap = Maps.newHashMap();
		List<Object[]> deList = Lists.newArrayList();
		List<Object[]> frList = Lists.newArrayList();
		List<Object[]> comList = Lists.newArrayList();
		List<Object[]> esList = Lists.newArrayList();
		List<Object[]> caList = Lists.newArrayList();
		List<Object[]> jpList = Lists.newArrayList();
		List<Object[]> itList = Lists.newArrayList();
		List<Object[]> ukList = Lists.newArrayList();
		List<Object[]> mxList = Lists.newArrayList();
		for (int i = 0; i < results.size(); i++) {
			Object[] objects = results.get(i);
			if ("de".equals(countryMap.get(objects[0].toString()))) {
				deList.add(objects);
			} else if ("fr".equals(countryMap.get(objects[0].toString()))) {
				frList.add(objects);
			} else if ("com".equals(countryMap.get(objects[0].toString()))) {
				comList.add(objects);
			} else if ("es".equals(countryMap.get(objects[0].toString()))) {
				esList.add(objects);
			} else if ("ca".equals(countryMap.get(objects[0].toString()))) {
				caList.add(objects);
			} else if ("jp".equals(countryMap.get(objects[0].toString()))) {
				jpList.add(objects);
			} else if ("it".equals(countryMap.get(objects[0].toString()))) {
				itList.add(objects);
			} else if ("uk".equals(countryMap.get(objects[0].toString()))) {
				ukList.add(objects);
			} else if ("mx".equals(countryMap.get(objects[0].toString()))) {
				mxList.add(objects);
			}
		}
		countrySettlementMap.put("de", deList);
		countrySettlementMap.put("fr", frList);
		countrySettlementMap.put("com", comList);
		countrySettlementMap.put("es", esList);
		countrySettlementMap.put("ca", caList);
		countrySettlementMap.put("jp", jpList);
		countrySettlementMap.put("it", itList);
		countrySettlementMap.put("uk", ukList);
		countrySettlementMap.put("mx", mxList);
		return countrySettlementMap;
	}
	
	private static Map<String, String> getCurrencySymbol(){
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
	
}
