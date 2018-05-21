package com.springrain.erp.modules.amazoninfo.web.order;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.order.VendorOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.VendorOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.VendorReturns;
import com.springrain.erp.modules.amazoninfo.entity.order.VendorShipment;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.order.VendorShipmentService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;



@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/vendorOrder")
public class VendorShipmentController extends BaseController {
	
	@Autowired
	private VendorShipmentService vendorShipmentService;

	@Autowired
	private AmazonProductService amazonProductService;
	@Autowired
	private PsiInventoryService psiInventoryService;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(VendorShipmentController.class);
	
	@RequestMapping(value = "list")
	public String list(VendorShipment vendorShipment, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		Page<VendorShipment> page = new Page<VendorShipment>(request, response,15);
		if (vendorShipment.getShipDate()== null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			vendorShipment.setShipDate(DateUtils.addDays(today, -10));
			vendorShipment.setDeliveryDate(today);
		}
		if(StringUtils.isBlank(vendorShipment.getCountry())){
			vendorShipment.setCountry("de");
		} 
		page = vendorShipmentService.findVendorShipment(page, vendorShipment);
		model.addAttribute("page", page);
		return "/modules/amazoninfo/order/vendorShipmentList";
	}
	

	@RequestMapping(value = "unconfirmedOrder")
	public String unconfirmedOrder(VendorOrder vendorOrder, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<VendorOrder> page = new Page<VendorOrder>(request, response,15);
		if(StringUtils.isBlank(vendorOrder.getCountry())){
			vendorOrder.setCountry("de");
		} 
		page = vendorShipmentService.findVendorOrders(page, vendorOrder);
		Integer stockId=120;
		if("de,fr,it,es,uk".contains(vendorOrder.getCountry())){
			stockId=19;
		}else if("jp".equals(vendorOrder.getCountry())){
			stockId=147;
		}
		Map<String,Integer> stockMap=psiInventoryService.findQuantityByWarehouseId(stockId);
		for (VendorOrder order: page.getList()) {
			boolean flag=false;
		    for (VendorOrderItem item: order.getItems()) {
		    	Integer stockQty=stockMap.get(item.getProductName())==null?0:stockMap.get(item.getProductName());
		    	item.setStockQty(stockQty);
		    	if(stockQty<item.getSubmittedQuantity()){
		    		flag=true;
		    		break;
		    	}
		    }
		    if(flag){
		    	order.setQtyFlag("1");
		    }
		}
		model.addAttribute("page", page);
		return "/modules/amazoninfo/order/vendorOrderList";
	}
	
	@RequestMapping(value = "returnList")
	public String returnList(VendorReturns vendorReturns, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<VendorReturns> page = new Page<VendorReturns>(request, response,15);
		if (vendorReturns.getCreateTime()== null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			vendorReturns.setCreateTime(DateUtils.addDays(today, -30));
			vendorReturns.setQueryTime(today);
		}
		
		page = vendorShipmentService.findVendorReturns(page, vendorReturns);
		model.addAttribute("page", page);
		return "/modules/amazoninfo/order/vendorReturnList";
	}
	
	public static boolean deFlag;
	public static boolean usFlag;
	public static boolean ukFlag;
	
	@RequestMapping(value = "outBound")
	@ResponseBody
	public String outBound(String ids, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		boolean flag=vendorShipmentService.updateVendor(ids);
		if(flag){
			return "1";
		}else{
			return "0";
		}
	}
	
	@RequestMapping(value = "form")
	public String findVendorShipmentInfo(Integer id, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		model.addAttribute("vendorShipment",vendorShipmentService.getShipment(id));
		return "/modules/amazoninfo/order/vendorShipmentForm";
	}
	
	@RequestMapping(value = "vendorForm")
	public String findVendorInfo(Integer id, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		model.addAttribute("vendorOrder",vendorShipmentService.getOrder(id));
		return "/modules/amazoninfo/order/vendorShipmentOrderForm";
	}
	
	@RequestMapping(value = "showDeliveryOrder")
	public String showCurrentOrder(VendorShipment vendorShipment,Model model){
       
		if (vendorShipment.getShipDate()== null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			vendorShipment.setShipDate(today);
			vendorShipment.setDeliveryDate(today);
		}
		if(StringUtils.isBlank(vendorShipment.getCountry())){
			vendorShipment.setCountry("de");
		}
		Map<String,Integer> totalMap=vendorShipmentService.getTotal(vendorShipment);
		Map<String,List<Object[]>> itemMap=vendorShipmentService.getTotalDetail(vendorShipment);
		
		model.addAttribute("totalMap", totalMap);
		model.addAttribute("itemMap", itemMap);
		return "modules/amazoninfo/order/vendorShipmentCount";
	}
	
	
	@RequestMapping(value = "exportExceptionAsn")
	public String exportExceptionAsn(VendorShipment vendorShipment,HttpServletRequest request,HttpServletResponse response, Model model){
		List<Object[]> list=vendorShipmentService.findExceptionAsn(vendorShipment);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		List<String> title = Lists.newArrayList("ASN","Product Name","Quantity");
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFCell cell = null;		
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int index=1;
		for (Object[] obj : list) {
			int j=0;
			row=sheet.createRow(index++);
    		row.setHeight((short) 400);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[0].toString());
			sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[1].toString());
			sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[2].toString());
			sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
		}
		
		for (int i = 0; i < title.size(); i++) {
      		 sheet.autoSizeColumn((short)i,true);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmm");
			String fileName = "vendor_" + sdf.format(new Date()) + ".xls";
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
	
	
	
	@RequestMapping(value = "createDeliveryOrder")
	public String createDeliveryOrder(VendorShipment vendorShipment,HttpServletRequest request,HttpServletResponse response, Model model){
		Map<String,Integer> totalMap=vendorShipmentService.getTotal(vendorShipment);
		Map<String,List<Object[]>> itemMap=vendorShipmentService.getTotalDetail(vendorShipment);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		List<String> title = Lists.newArrayList("Product Name","Total Quantity","ASN","Order Id","Order Status","Accepted Quantity","Received Quantity","Shipped Date");
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		
		CellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		contentStyle1.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		contentStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		HSSFCell cell = null;		
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int index=1;
		for (Map.Entry<String,Integer> entry : totalMap.entrySet()) { 
		    String name = entry.getKey();
			int num=1;
			if(itemMap!=null&&itemMap.get(name)!=null&&itemMap.get(name).size()>0){
				for (Object[] obj : itemMap.get(name)) {
					int j=0;
					row=sheet.createRow(index++);
		    		row.setHeight((short) 400);
					if(num==1){
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
						row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(entry.getValue());
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
						row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[1].toString());
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[2].toString());
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[8].toString());
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[3].toString()));
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[7].toString()));
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm").format((Timestamp)obj[4]));
					/*if("Closed".equals(obj[8].toString())&&obj[3].toString()!=obj[7].toString()){
						sheet.getRow(index-1).getCell(j-6).setCellStyle(contentStyle1);
						sheet.getRow(index-1).getCell(j-5).setCellStyle(contentStyle1);
						sheet.getRow(index-1).getCell(j-4).setCellStyle(contentStyle1);
						sheet.getRow(index-1).getCell(j-3).setCellStyle(contentStyle1);
                    	sheet.getRow(index-1).getCell(j-2).setCellStyle(contentStyle1);
                    	sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle1);
					}else{*/
						sheet.getRow(index-1).getCell(j-6).setCellStyle(contentStyle);
						sheet.getRow(index-1).getCell(j-5).setCellStyle(contentStyle);
						sheet.getRow(index-1).getCell(j-4).setCellStyle(contentStyle);
						sheet.getRow(index-1).getCell(j-3).setCellStyle(contentStyle);
						sheet.getRow(index-1).getCell(j-2).setCellStyle(contentStyle);
                    	sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					/*}*/
					if(itemMap.get(name).size()==num){
						sheet.addMergedRegion(new CellRangeAddress(index-1-itemMap.get(name).size()+1,index-1,0,0));
						sheet.addMergedRegion(new CellRangeAddress(index-1-itemMap.get(name).size()+1,index-1,1,1));
					}
					num++;
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmm");
			String fileName = "vendor_" + sdf.format(new Date()) + ".xls";
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
	
	@RequestMapping(value = "update")
	@ResponseBody
	public String updateTrackFee(VendorShipment vendorShipment,Model model){
		vendorShipmentService.updateTrackFee(vendorShipment);
		return "1";
	}
	
	@RequestMapping(value = "updateCheckFlag")
	@ResponseBody
	public String updateCheckFlag(VendorShipment vendorShipment,Model model){
		vendorShipmentService.updateCheckFlag(vendorShipment);
		return "1";
	}
	
	@RequestMapping(value = "updateExceptionCheckFlag")
	@ResponseBody
	public String updateExceptionCheckFlag(VendorShipment vendorShipment,Model model){
		vendorShipmentService.updateExceptionCheckFlag(vendorShipment);
		return "1";
	}
	
	@RequestMapping(value =  "expVendorOrderByCsv" )
	public String expVendorOrderByCsv(VendorShipment vendorShipment, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		if (vendorShipment.getShipDate()== null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			vendorShipment.setShipDate(DateUtils.addDays(today, -10));
			vendorShipment.setDeliveryDate(today);
		}
		if(StringUtils.isBlank(vendorShipment.getCountry())){
			vendorShipment.setCountry("de");
		}
			
	    List<VendorOrder> list = vendorShipmentService.findForExp(vendorShipment);
	    if(list.size()>65535){
	    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
	    }
	    String country = "DE";
	    if ("com".equals(vendorShipment.getCountry())) {
	    	country = "US";
		}
	    response.setCharacterEncoding("UTF-8");
		response.setContentType("application/download;charset=UTF-8");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = country+"VenderOrdorData" + sdf.format(new Date()) + ".csv";
		response.setHeader("Content-disposition", "attachment;filename=\""
				+fileName);
		OutputStream o;
		try {
			o = response.getOutputStream();
			OutputStreamWriter os = new OutputStreamWriter(o, "utf-8");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Float gbpRate = AmazonProduct2Service.getRateConfig().get("GBP/EUR");
			BigDecimal bd = new BigDecimal(gbpRate);
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			gbpRate = bd.floatValue();
			for (VendorOrder order : list) {
				String countryCode = order.getCountry();
				CountryCode code  = null;
				try {
					if ("uk".equals(countryCode)) {
						code = CountryCode.valueOf("GB");
					} else {
						code = CountryCode.valueOf(countryCode.toUpperCase());
					}
				} catch (Exception e) {}
				
				double afterTax = order.getReceivedTotalCost()==null?0:order.getReceivedTotalCost().doubleValue();
				if (afterTax <= 0) {
					continue;
				}

				float rate = 1f;
				if ("uk".equals(countryCode)) {
					rate = gbpRate;
					afterTax = afterTax*rate;
					bd = new BigDecimal(afterTax);
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					afterTax = bd.doubleValue() ;
				}
				StringBuffer sb = new StringBuffer();
				sb.append(order.getId()).append("\t");
				sb.append(order.getOrderId()).append("\t");
				sb.append(sdf1.format(order.getOrderedDate())).append("\t");
				sb.append(afterTax).append("\t");
				if (code != null) {
					sb.append(code.getNumberCode()).append("\t");
				} else {
					sb.append("").append("\t");
				}
				sb.append(rate).append("\t");
				sb.append("100000");
				sb.append("\n");
				os.write(sb.toString());
			}
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@RequestMapping(value = "exportReturns")
	public String exportReturns(VendorReturns vendorReturns,HttpServletRequest request,HttpServletResponse response, Model model){
		Map<String,Map<String,Integer>> map=vendorShipmentService.countReturns(vendorReturns.getCreateTime(),vendorReturns.getQueryTime());
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		List<String> title = Lists.newArrayList("Month","Product Name","Quantity");
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFCell cell = null;		
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int index=1;
		for (Map.Entry<String,Map<String,Integer>> entry : map.entrySet()) { 
		   String date = entry.getKey();
			
			Map<String,Integer> temp=entry.getValue();
			for (Map.Entry<String,Integer> entryRs : temp.entrySet()) { 
			    String name = entryRs.getKey();
				int j=0;
				row=sheet.createRow(index++);
	    		row.setHeight((short) 400);
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
				sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
				sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(entryRs.getValue());
				sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
			}
		}
		
		for (int i = 0; i < title.size(); i++) {
      		 sheet.autoSizeColumn((short)i,true);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmm");
			String fileName = "VendorReturns_" + sdf.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value = "expUnconfirmedOrder")
	public String expUnconfirmedOrder(VendorOrder vendorOrder,HttpServletRequest request,HttpServletResponse response, Model model){
		Page<VendorOrder> page = new Page<VendorOrder>(request, response,-1);
		if(StringUtils.isBlank(vendorOrder.getCountry())){
			vendorOrder.setCountry("de");
		} 
		page = vendorShipmentService.findVendorOrders(page, vendorOrder);
		Integer stockId=120;
		if("de,fr,it,es,uk".contains(vendorOrder.getCountry())){
			stockId=19;
		}else if("jp".equals(vendorOrder.getCountry())){
			stockId=147;
		}
		Map<String,Integer> stockMap=psiInventoryService.findQuantityByWarehouseId(stockId);
	
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		List<String> title = Lists.newArrayList("OrderId","ShipToLocation","deliveryWindow","asin","sku","productName","unitPrice","submittedQuantity","stockQuantity","status");
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		
		CellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		HSSFFont font1 = wb.createFont();
		font1.setColor(HSSFFont.COLOR_RED);  
		font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
		contentStyle1.setFont(font1);
		
		HSSFCell cell = null;		
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int index=1;
		int startRow=1;
		//"OrderId","Ship","deliveryWindow","asin","sku","productName","unitPrice","submittedQuantity","stockQuantity"
		for (VendorOrder order: page.getList()) { 
	    	int count=0;
	    	int maxItems=order.getItems().size();
			for (VendorOrderItem item : order.getItems()) {
				row=sheet.createRow(index++);
		    	row.setHeight((short) 400);
		    	int j=0;
				if(count==0){
					    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(order.getOrderId());
						row.getCell(j-1).setCellStyle(contentStyle);
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(order.getShipToLocation());
						row.getCell(j-1).setCellStyle(contentStyle);
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(order.getDeliveryWindow());
						row.getCell(j-1).setCellStyle(contentStyle);
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					  row.getCell(j-1).setCellStyle(contentStyle); 
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					  row.getCell(j-1).setCellStyle(contentStyle); 
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					  row.getCell(j-1).setCellStyle(contentStyle); 
				  }
				  
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getAsin());
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getSku());
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getProductName());
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getUnitPrice().floatValue());
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getSubmittedQuantity());
				row.getCell(j-1).setCellStyle(contentStyle);
				Integer stockQty=stockMap.get(item.getProductName())==null?0:stockMap.get(item.getProductName());
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(stockQty);
				if(stockQty<item.getSubmittedQuantity()){
					row.getCell(j-1).setCellStyle(contentStyle1);
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("Out of stock");
					row.getCell(j-1).setCellStyle(contentStyle1);
				}else{
					row.getCell(j-1).setCellStyle(contentStyle);
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(j-1).setCellStyle(contentStyle);
				}
				count++;
			}
			if(maxItems>0){
				  sheet.addMergedRegion(new CellRangeAddress(startRow, startRow+maxItems-1, 0, 0));
				  sheet.addMergedRegion(new CellRangeAddress(startRow, startRow+maxItems-1, 1, 1));
				  sheet.addMergedRegion(new CellRangeAddress(startRow, startRow+maxItems-1, 2, 2));
			 }
			 startRow+=maxItems;
		}
		
		for (int i = 0; i < title.size(); i++) {
      		 sheet.autoSizeColumn((short)i,true);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmm");
			String fileName = "UnconfirmedOrder_" + sdf.format(new Date()) + ".xls";
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
}
