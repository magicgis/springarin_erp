/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.config.LogisticsSupplier;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.ZipUtil;
import com.springrain.erp.common.utils.excel.ExportTransportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonUnlineOrderService;
import com.springrain.erp.modules.psi.entity.FbaInbound;
import com.springrain.erp.modules.psi.entity.FbaInboundItem;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductDto;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderContainer;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderItem;
import com.springrain.erp.modules.psi.entity.PsiTransportRevise;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderItem;
import com.springrain.erp.modules.psi.service.FbaInboundService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderItemService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderService;
import com.springrain.erp.modules.psi.service.PsiTransportPaymentItemService;
import com.springrain.erp.modules.psi.service.PurchaseOrderService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.sys.utils.UserUtils;
/**
 * 运单表Controller
 * @author Michael
 * @version 2015-01-15
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiTransportOrder")
public class PsiTransportOrderController extends BaseController{
	
	@Autowired
	private PsiTransportOrderService 		psiTransportOrderService;
	@Autowired
	private PsiTransportOrderItemService 	psiTransportOrderItemService;
	@Autowired
	private PsiSupplierService 				psiSupplierService;
	@Autowired
	private PsiProductService 				productService;
	@Autowired
	private StockService 					stockService;
	@Autowired
	private PsiTransportPaymentItemService  psiTransportPaymentItemService;
	@Autowired
	private AmazonUnlineOrderService        amazonUnlineOrderService;
	@Autowired
	private FbaInboundService 				fbaInBoundService;
	@Autowired
	private AmazonProduct2Service           amazonProduct2Service;
	@Autowired
	private PsiInventoryService             psiInventoryService;
	@Autowired
	private PsiProductTieredPriceService    psiProductTieredPriceService;
	@Autowired
	private PurchaseOrderService		    purchaseOrderService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	private static String filePath;
	
	@RequiresPermissions("psi:transport:view")
	@RequestMapping(value = {"list", ""})
	public String list(PsiTransportOrder psiTransportOrder, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		
		 HttpSession session = request.getSession(true);
		if(psiTransportOrder.getCreateDate()!=null){
			if(session.isNew()){   
			   session.setAttribute("psiTransport_createDate", psiTransportOrder.getCreateDate());
		    }
		}else{
			Date date=(Date)session.getAttribute("psiTransport_createDate");
			if(date!=null){
				psiTransportOrder.setCreateDate(date);
			}else{
				psiTransportOrder.setCreateDate(DateUtils.addMonths(today, -3));
			}
		}
		
		Page<PsiTransportOrder> page = new Page<PsiTransportOrder>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("id desc");
		}else{
			page.setOrderBy(orderBy);
		}	
		
		List<PsiSupplier> tranSuppliers = this.psiSupplierService.findAllTransporter();
        page = psiTransportOrderService.find(page, psiTransportOrder); 
        
        model.addAttribute("tranSuppliers", tranSuppliers);
        model.addAttribute("site",LogisticsSupplier.getWebSite());
        model.addAttribute("page", page);
        model.addAttribute("fnskuMap",amazonProduct2Service.getSkuAndFnskuMap());
		return "modules/psi/psiTransportOrderList";
	}
	

	@RequiresPermissions("psi:transport:view")
	@RequestMapping(value = {"singleTran"})
	public String singleTran(String productName,String tranModel,String tranType,String toCountry,Date startDate,Date endDate,Integer fromStoreId, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		if(startDate==null){
			endDate=sdf.parse(sdf.format(new Date()));
			startDate=DateUtils.addMonths(endDate, -3);
		}
		
		List<Object[]> list=this.psiTransportOrderService.getSingleTran(productName,tranModel,toCountry,fromStoreId,tranType,startDate,endDate);
		//组装神奇的map: 月份（201601）、区域（EU,JP,US）、运输方式（AE、OE、EX）
		Map<String,Map<String,Map<String,Integer>>> tranMap =Maps.newLinkedHashMap();
		for(Object[] obj:list){
			String month =obj[0].toString().substring(0,6);
			String zone ="DE";
			String tModel="EX";
			Integer quantity =Integer.parseInt(obj[7].toString());
			if("0".equals(obj[3].toString())){
				tModel="AE";
			}else if("1".equals(obj[3].toString())){
				tModel="OE";
			}
			
			if("jp".equals(obj[2].toString())||"JP".equals(obj[2].toString())){
				zone="JP";
			}else if("com".equals(obj[2].toString())||"US".equals(obj[2].toString())||"ca".equals(obj[2].toString())||"mx".equals(obj[2].toString())){
				zone="US";
			}
			
			Map<String,Map<String,Integer>> zoneMap = null; 
			if(tranMap.get(month)==null){
				zoneMap =Maps.newHashMap();
			}else{
				zoneMap = tranMap.get(month);
			}
			
			Map<String,Integer> modelMap = null; 
			if(zoneMap.get(zone)==null){
				modelMap=Maps.newHashMap();
				modelMap.put("total", quantity);
			}else{
				modelMap=zoneMap.get(zone);
				modelMap.put("total", quantity+modelMap.get("total"));
				if(modelMap.get(tModel)!=null){
					quantity+=modelMap.get(tModel);
				}
			}
			
			modelMap.put(tModel, quantity);
			zoneMap.put(zone, modelMap);
			tranMap.put(month, zoneMap);
		}
		
		Set<String> set = Sets.newHashSet();
		set.add(productName);
		
		Map<String,String> proInfos=this.productService.getProductVoGw(set);
		if(proInfos!=null&&proInfos.size()>0){
			String arr[] = proInfos.get(productName).split(",");
			model.addAttribute("packNums",arr[0]);
			model.addAttribute("volume", arr[1]);
			model.addAttribute("weight", arr[2]);
		}
		
		
	    Map<String,String> proColorMap =this.purchaseOrderService.getAllProductColors(); 
        model.addAttribute("productColors", proColorMap.keySet());
        model.addAttribute("list", list);
        model.addAttribute("productName", productName);
        model.addAttribute("tranModel", tranModel);
        model.addAttribute("toCountry", toCountry);
        model.addAttribute("fromStoreId", fromStoreId);
        model.addAttribute("tranType", tranType);
        model.addAttribute("tranMap", tranMap);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
		return "modules/psi/transportSingleProductList";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value =  "expTransport" )
	public String expTransport(PsiTransportOrder psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
			SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyyMMdd");
			int excelNo =1;
			
		    List<PsiTransportOrder> list=this.psiTransportOrderService.exp(psiTransportOrder); 
		    if(list.size()>65535){
		    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
		    }
		    
		    HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	
			String[] title = { " Month ", "  NO. ", " FBA ", "   Model   ","  Origin  ", " Des "," Zone "," CTNS "," C.W "," CBM "," TEU ",
					"Unit Price","Local","Tran","DAP","Insurance","Other","Other1","Total Price","Taxes","Duty","Other Taxes"," PKD "," ETD "," ETA "," InStockDate "," T/W "," T/S "," T/R "," T/T "," B/L NO. "," Remark "};
			
			
			  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
			  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
			  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			  //设置Excel中的边框(表头的边框)
			  style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			  
			  style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			  style.setBottomBorderColor(HSSFColor.BLACK.index);
			  
			  style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
			  style.setLeftBorderColor(HSSFColor.BLACK.index);
			  
			  style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
			  style.setRightBorderColor(HSSFColor.BLACK.index);
			  
			  style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
			  style.setTopBorderColor(HSSFColor.BLACK.index);
			  //设置字体
			  HSSFFont font = wb.createFont();
			  font.setFontHeightInPoints((short) 16); // 字体高度
			  font.setFontName(" 黑体 "); // 字体
			  font.setBoldweight((short) 16);
			  style.setFont(font);
			  row.setHeight((short) 600);
			  HSSFCell cell = null;		
			  for (int i = 0; i < title.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(title[i]);
					cell.setCellStyle(style);
					sheet.autoSizeColumn((short) i);
				}
			 for(PsiTransportOrder transportOrder: list){
				 int i =0;
				 row = sheet.createRow(excelNo++);
				 //获取月份
				 String shortMonth = "";
				 if(transportOrder.getPickUpDate()!=null){
					 shortMonth=sdf1.format(transportOrder.getPickUpDate()).substring(4,6);
				 }else{
					 shortMonth=transportOrder.getTransportNo().substring(4,6);
				 }
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(DateUtils.getShortMonth(Integer.parseInt(shortMonth)));
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getTransportNo());
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getShipmentId());
				String modelStr="";
				if("0".equals(transportOrder.getModel())){
					modelStr="AE";
				}else if("1".equals(transportOrder.getModel())){
					modelStr="OE";
				}else if("2".equals(transportOrder.getModel())){
					modelStr="EX";
				}else if("3".equals(transportOrder.getModel())){
					modelStr="TR";
				}
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(modelStr);
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(StringUtils.isEmpty(transportOrder.getOrgin())?"":transportOrder.getOrgin());
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(StringUtils.isEmpty(transportOrder.getDestination())?"":transportOrder.getDestination());
				String  zone="";
				if(transportOrder.getToCountry()!=null){
					zone=getZone(transportOrder.getToCountry());
				}
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(zone);
				row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(transportOrder.getBoxNumber());
				
				if(transportOrder.getWeight()!=null){
					row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(transportOrder.getWeight());
				}else{
					row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
				}
				
				if(transportOrder.getVolume()!=null){
					row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(transportOrder.getVolume());
				}else{
					row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
				}
				
				if(transportOrder.getTeu()!=null){
					row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(transportOrder.getTeu());
				}else{
					row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
				}
				
				if(transportOrder.getUnitPrice()!=null){
					HSSFCellStyle cellStyle = wb.createCellStyle();
		            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		            HSSFCell cell1=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
		            cell1.setCellValue(transportOrder.getUnitPrice());
		            cell1.setCellStyle(cellStyle);
		            
				}else{
					row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
				}
				
				
				String local="";
				if(transportOrder.getLocalAmount()!=null){
					local=transportOrder.getLocalAmount()+"";
				}
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(getCurrencySymbol(transportOrder.getCurrency1())+local);
				
				
				String tranAmount="";
				if(transportOrder.getTranAmount()!=null){
					tranAmount=transportOrder.getTranAmount()+"";
				}
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(getCurrencySymbol(transportOrder.getCurrency2())+tranAmount);
				
				String dapAmount="";
				if(transportOrder.getDapAmount()!=null){
					dapAmount=transportOrder.getDapAmount()+"";
				}
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(getCurrencySymbol(transportOrder.getCurrency3())+dapAmount);
				
				String insuranceAmount="";
				if(transportOrder.getInsuranceAmount()!=null){
					insuranceAmount=transportOrder.getInsuranceAmount()+"";
				}
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(getCurrencySymbol(transportOrder.getCurrency5())+insuranceAmount);
				
				
				String otherAmount="";
				if(transportOrder.getOtherAmount()!=null){
					otherAmount=transportOrder.getOtherAmount()+"";
				}
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(getCurrencySymbol(transportOrder.getCurrency4())+otherAmount);
				
				String otherAmount1="";
				if(transportOrder.getOtherAmount1()!=null){
					otherAmount1=transportOrder.getOtherAmount1()+"";
				}
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(getCurrencySymbol(transportOrder.getCurrency7())+otherAmount1);
				
				if(transportOrder.getTotalAmount()!=null){
					HSSFCellStyle cellStyle = wb.createCellStyle();
		            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		            HSSFCell cell1=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
		            cell1.setCellValue(transportOrder.getTotalAmount());
		            cell1.setCellStyle(cellStyle);
				}else{
					row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
				}
				
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getTaxTaxes()!=null?(getCurrencySymbol(transportOrder.getCurrency6())+transportOrder.getTaxTaxes()+""):"");
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getDutyTaxes()!=null?(getCurrencySymbol(transportOrder.getCurrency6())+transportOrder.getDutyTaxes()+""):"");
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getOtherTaxes()!=null?(getCurrencySymbol(transportOrder.getCurrency6())+transportOrder.getOtherTaxes()+""):"");
				
				String pickDate ="";
				if(transportOrder.getPickUpDate()!=null){
					pickDate=sdf1.format(transportOrder.getPickUpDate());
				}
				
				String etdDate ="";
				if(transportOrder.getEtdDate()!=null){
					etdDate=sdf1.format(transportOrder.getEtdDate());
				}
				
				String actualEtaDate="";
				if(transportOrder.getEtaDate()!=null){
					actualEtaDate=sdf1.format(transportOrder.getEtaDate());
				}
				
				String inStockDate="";
				if(transportOrder.getOperArrivalDate()!=null){
					inStockDate=sdf1.format(transportOrder.getOperArrivalDate());
				}
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(pickDate);
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(etdDate);
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(actualEtaDate);
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(inStockDate);
				
				String spaceDaysTw="";
				if(transportOrder.getPickUpDate()!=null&&transportOrder.getEtdDate()!=null){
					spaceDaysTw=DateUtils.spaceDays(transportOrder.getPickUpDate(), transportOrder.getEtdDate())+1+"";
				}
				
				String spaceDaysTs="";
				if(transportOrder.getEtdDate()!=null&&transportOrder.getPreEtaDate()!=null){
					spaceDaysTs=DateUtils.spaceDays(transportOrder.getEtdDate(), transportOrder.getPreEtaDate())+1+"";
				}
				
				String spaceDaysTr="";
				if(transportOrder.getPreEtaDate()!=null&&transportOrder.getOperArrivalDate()!=null){
					spaceDaysTr=DateUtils.spaceDays(transportOrder.getPreEtaDate(), transportOrder.getOperArrivalDate())+1+"";
				}
				String spaceDaysTT="";
				if(transportOrder.getPickUpDate()!=null&&transportOrder.getOperArrivalDate()!=null){
					spaceDaysTT=DateUtils.spaceDays(transportOrder.getPickUpDate(), transportOrder.getOperArrivalDate())+1+"";
				}
				row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(spaceDaysTw); 
				row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(spaceDaysTs); 
				row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(spaceDaysTr); 
 				row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(spaceDaysTT); 
 				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getLadingBillNo());
 				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getRemark());
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
		return null;
	}
	
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value =  "expNew" )
	public String expNew(PsiTransportOrder psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		Map<String,Map<String,Map<String,Float>>> monthMap = Maps.newTreeMap();
		Map<String,List<PsiTransportOrder>> detailMap = Maps.newTreeMap();
		Map<String,BigDecimal> monthBaseMap = Maps.newHashMap();
			SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			int excelNo =1;
			int excelNo1 =1;
		    List<PsiTransportOrder> list=this.psiTransportOrderService.expNew(psiTransportOrder); 
		    if(list.size()>65535){
		    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
		    }
		    
		    
		    for(PsiTransportOrder transportOrder: list){
		    	// String month =DateUtils.getShortMonth(Integer.parseInt(transportOrder.getTransportNo().substring(4, 6)));
		    	 //获取月份
				 String shortMonth = "";
				 if(transportOrder.getPickUpDate()!=null){
					 shortMonth=sdf1.format(transportOrder.getPickUpDate()).substring(5,7);
				 }else{
					 shortMonth=transportOrder.getTransportNo().substring(4,6);
				 }
				 
				 String month =DateUtils.getShortMonth(Integer.parseInt(shortMonth));
		    	 List<PsiTransportOrder> orders = null;
		    	 if(detailMap.get(month)==null){
		    		 orders = Lists.newArrayList();
		    	 }else{
		    		 orders = detailMap.get(month);
		    	 }
		    	 orders.add(transportOrder);
		    	 detailMap.put(month, orders);
		    }
		    
		    
		    HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet0 = wb.createSheet("明细");
			HSSFSheet sheet1 = wb.createSheet("汇总");
			sheet0.setDefaultColumnWidth(14);
			sheet1.setDefaultColumnWidth(12);
			HSSFRow row = sheet0.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	
			String[] title = { " Month ", "  运输单号 ", " ETD ","  Market  ", "   Model   ","  POL  ", "   POD  " ," 新品名称 "," 新品重量 ","总重量","百分比"};
			  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
			  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
			  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			  //设置Excel中的边框(表头的边框)
			  style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			  //设置字体
			  HSSFFont font = wb.createFont();
			  font.setFontHeightInPoints((short) 16); // 字体高度
			  font.setFontName(" 黑体 "); // 字体
			  font.setBoldweight((short) 16);
			  style.setFont(font);
			  row.setHeight((short) 600);
			  HSSFCell cell = null;		
			  for (int i = 0; i < title.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(title[i]);
					cell.setCellStyle(style);
				}
			  
			 DecimalFormat df = new DecimalFormat("#.##");
			 df.setRoundingMode(RoundingMode.HALF_UP);
				
			 for(Map.Entry<String, List<PsiTransportOrder>> entry: detailMap.entrySet()){
				 String month = entry.getKey();
				  BigDecimal totalWeightOut = new BigDecimal(0);
				  BigDecimal newWeightOut = new BigDecimal(0);
				 for(PsiTransportOrder transportOrder:entry.getValue()){
					 int i =0;
					 row = sheet0.createRow(excelNo++);
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(month);
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getTransportNo());
					String etdDate="";
					if(transportOrder.getEtaDate()!=null){
						etdDate=sdf1.format(transportOrder.getEtdDate());
					}
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(etdDate);
					
					String  market="";
					if(transportOrder.getToCountry()!=null){
						market=getMarket(transportOrder.getToCountry());
					}
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(market);
					
					String modelStr="";
					if("0".equals(transportOrder.getModel())){
						modelStr="AE";
					}else if("1".equals(transportOrder.getModel())){
						modelStr="OE";
					}else if("2".equals(transportOrder.getModel())){
						modelStr="EX";
					}else if("3".equals(transportOrder.getModel())){
						modelStr="TR";
					}
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(modelStr);
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(StringUtils.isEmpty(transportOrder.getOrgin())?"":transportOrder.getOrgin());
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(StringUtils.isEmpty(transportOrder.getDestination())?"":transportOrder.getDestination());
					
					
					BigDecimal totalWeight = new BigDecimal(0);
					BigDecimal newWeight = new BigDecimal(0);
					
					StringBuilder proNames =new StringBuilder();
					
					for(PsiTransportOrderItem item:transportOrder.getItems()){
						PsiProduct pro =item.getProduct();
						BigDecimal 	singleWeight = pro.getGw();
						BigDecimal  boxNumber = new BigDecimal(item.getQuantity()/pro.getPackQuantity());
						totalWeight=totalWeight.add(singleWeight.multiply(boxNumber));
						if("1".equals(pro.getIsNew())){
							newWeight=newWeight.add(singleWeight.multiply(boxNumber));	
							newWeightOut=newWeightOut.add(singleWeight.multiply(boxNumber));
							proNames.append(pro.getModel()).append(",");
						}
						
						totalWeightOut=totalWeightOut.add(singleWeight.multiply(boxNumber));
					}
					
					if(proNames.length()>1){
						proNames=new StringBuilder(proNames.substring(0,proNames.length()-1));
					}
					
					
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(proNames.toString());
					row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(newWeight.floatValue()));
					row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalWeight.floatValue()));
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(df.format(newWeight.divide(totalWeight,4,RoundingMode.HALF_UP).floatValue()*100)+"%");
				
					//整合汇总数据
					
					Map<String,Map<String,Float>> modelMap = null; 
					if(monthMap.get(month)==null){
						modelMap =Maps.newHashMap();
					}else{
						modelMap=monthMap.get(month);
					}
					
					Float countNewWeight= Float.parseFloat(df.format(newWeight.floatValue()));
					Map<String,Float> marketMap = null;
					if(modelMap.get(modelStr)==null){
						marketMap = Maps.newHashMap();
					}else{
						marketMap = modelMap.get(modelStr);
						if(marketMap.get(market)!=null){
							countNewWeight+=marketMap.get(market);
						}
					}
					
					marketMap.put(market, countNewWeight);
					modelMap.put(modelStr, marketMap);
					monthMap.put(month, modelMap);
				 }
				 
				 monthBaseMap.put(month, totalWeightOut);
					
				 row = sheet0.createRow(excelNo++);
				 row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("TOTAL");
				 row.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(newWeightOut));
				 row.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalWeightOut));
				 row.createCell(10, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(newWeightOut.divide(totalWeightOut,4,RoundingMode.HALF_UP).floatValue()*100)+"%");
				
				 
			 }
		
			 
			 HSSFRow row1 = sheet1.createRow(0);
			 String[] title1 = { " Month ", "  Model ", "  Market  ", "   New Pro   ","  C.W  ", "百分比"};
			  row1.setHeight((short) 600);
			  HSSFCell cell1 = null;		
			  for (int i = 0; i < title1.length; i++) {
					cell1 = row1.createCell(i);
					cell1.setCellValue(title1[i]);
					cell1.setCellStyle(style);
				}
			 
			 //解析monthMap  处理数据
		  for(Map.Entry<String, Map<String, Map<String, Float>>> entry:monthMap.entrySet()){
				 String monthKey = entry.getKey();
				 for(Map.Entry<String, Map<String, Float>> entry1:entry.getValue().entrySet()){
					 String modelKey = entry1.getKey();
					 //市场循环完毕增加统计行
					 Float newMarketWeight = 0f;
					 for(Map.Entry<String, Float> entry2:entry1.getValue().entrySet()){
						 String marketKey = entry2.getKey();
						 int i =0;
						 Float curNewWeight =entry2.getValue();
						 row1 = sheet1.createRow(excelNo1++);
						 row1.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(monthKey);
						 row1.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(modelKey);
						 row1.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(marketKey);
						 row1.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(curNewWeight));
						 row1.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(monthBaseMap.get(monthKey)));
						 row1.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(new BigDecimal(curNewWeight).divide(monthBaseMap.get(monthKey),4,RoundingMode.HALF_UP).floatValue()*100)+"%");
						 newMarketWeight+=curNewWeight;
					 }
					 
					 row1 = sheet1.createRow(excelNo1++);
					 row1.createCell(2,Cell.CELL_TYPE_STRING).setCellValue("Total");
					 row1.createCell(3,Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(newMarketWeight));
					 row1.createCell(4,Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(monthBaseMap.get(monthKey)));
					 row1.createCell(5,Cell.CELL_TYPE_STRING).setCellValue(df.format(new BigDecimal(newMarketWeight).divide(monthBaseMap.get(monthKey),4,RoundingMode.HALF_UP).floatValue()*100)+"%");
					 
				 }
			 }
			
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
	
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	
		String fileName = "newProductData" + sdf.format(new Date()) + ".xls";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
		try {
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value =  "expProducts" )
	public String expProducts(PsiTransportOrder psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		    List<PsiTransportOrderItem> list=this.psiTransportOrderItemService.getTransportOrderItems(psiTransportOrder.getId()); 
		    HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			String[] title = { " ItemNo. ", " Product ","Quantity(PCS)","Pcs/ctn"," DE " ," DE(Sku,Fnsku,Quantity) "," UK "," UK(Sku,Fnsku,Quantity) "," FR "," FR(Sku,Fnsku,Quantity) "," IT "," IT(Sku,Fnsku,Quantity) "," ES "," ES(Sku,Fnsku,Quantity) "," JP "," JP(Sku,Fnsku,Quantity) "," COM "," COM(Sku,Fnsku,Quantity) "," CA "," CA(Sku,Fnsku,Quantity) " };
			  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
			  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
			  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//			  //设置字体
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
			  
			 //Map(产品名颜色 ,Map(国家,Map(sku,数量)))
			Map<String,Map<String,Map<String,Integer>>> productMap =Maps.newTreeMap();
			Map<String,String> productInfoMap =Maps.newHashMap();
			Map<String,String> fnskuMap=amazonProduct2Service.getSkuAndFnskuMap();
			for(PsiTransportOrderItem item :list){
				String productName=item.getProductName();
				if(StringUtils.isNotEmpty(item.getColorCode())){
					productName+="_"+item.getColorCode();
				}
				Map<String,Map<String,Integer>> countryMap = null;
				Map<String,Integer> skuMap = null;
				if(productMap.get(productName)!=null){
					countryMap=productMap.get(productName);
				}else{
					countryMap =Maps.newHashMap();
					//productInfoMap.put(productName, item.getProduct().getPackQuantity()+",,"+item.getProduct().getType());
					productInfoMap.put(productName, item.getPackQuantity()+",,"+item.getProduct().getType());
				}
				
				if(countryMap.get(item.getCountryCode())==null){
					skuMap = Maps.newHashMap();
				}else{
					skuMap = countryMap.get(item.getCountryCode());
				}
				
				skuMap.put(item.getSku(), item.getQuantity());
				countryMap.put(item.getCountryCode(), skuMap);
				productMap.put(productName, countryMap);
			}
			
			 int j =1;
			 for(Map.Entry<String, Map<String, Map<String, Integer>>> entry :productMap.entrySet()){
				String productName = entry.getKey();
				row = sheet.createRow(j++);
				Map<String,Map<String,Integer>> countryMap =entry.getValue();
				row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(productName); 
				String proInfo=productInfoMap.get(productName);
				row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(proInfo.split(",,")[1]);
				row.createCell(3,Cell.CELL_TYPE_STRING).setCellValue(proInfo.split(",,")[0]+"/ctn");
				Integer totalQuantity = 0;
				if(countryMap.get("de")!=null){
					Map<String,Integer> skuMap = countryMap.get("de");
					StringBuilder skuStr =new StringBuilder();
					Integer singleQuantiy=0;
					for(Map.Entry<String,Integer> skuEntry:skuMap.entrySet()){
						String sku = skuEntry.getKey();
						totalQuantity+=skuMap.get(sku);
						singleQuantiy+=skuMap.get(sku);
						skuStr.append(sku).append("[").append(fnskuMap.get(sku)).append("](").append(skuMap.get(sku)).append(");");
					}
					row.createCell(4,Cell.CELL_TYPE_STRING).setCellValue(singleQuantiy);
					row.createCell(5,Cell.CELL_TYPE_STRING).setCellValue(skuStr.toString());
				}
				if(countryMap.get("uk")!=null){
					Map<String,Integer> skuMap = countryMap.get("uk");
					StringBuilder skuStr =new StringBuilder();
					Integer singleQuantiy=0;
					for(Map.Entry<String,Integer> skuEntry:skuMap.entrySet()){
						String sku = skuEntry.getKey();
						totalQuantity+=skuMap.get(sku);
						singleQuantiy+=skuMap.get(sku);
						skuStr.append(sku).append("[").append(fnskuMap.get(sku)).append("](").append(skuMap.get(sku)).append(");");
					}
					row.createCell(6,Cell.CELL_TYPE_STRING).setCellValue(singleQuantiy);
					row.createCell(7,Cell.CELL_TYPE_STRING).setCellValue(skuStr.toString());
				}
				if(countryMap.get("fr")!=null){
					Map<String,Integer> skuMap = countryMap.get("fr");
					StringBuilder skuStr =new StringBuilder();
					Integer singleQuantiy=0;
					for(Map.Entry<String,Integer> skuEntry:skuMap.entrySet()){
						String sku = skuEntry.getKey();
						totalQuantity+=skuMap.get(sku);
						singleQuantiy+=skuMap.get(sku);
						skuStr.append(sku).append("[").append(fnskuMap.get(sku)).append("](").append(skuMap.get(sku)).append(");");
					}
					row.createCell(8,Cell.CELL_TYPE_STRING).setCellValue(singleQuantiy);
					row.createCell(9,Cell.CELL_TYPE_STRING).setCellValue(skuStr.toString());
				}
				if(countryMap.get("it")!=null){
					Map<String,Integer> skuMap = countryMap.get("it");
					StringBuilder skuStr =new StringBuilder();
					Integer singleQuantiy=0;
					for(Map.Entry<String,Integer> skuEntry:skuMap.entrySet()){
						String sku = skuEntry.getKey();
						totalQuantity+=skuMap.get(sku);
						singleQuantiy+=skuMap.get(sku);
						skuStr.append(sku).append("[").append(fnskuMap.get(sku)).append("](").append(skuMap.get(sku)).append(");");
					}
					row.createCell(10,Cell.CELL_TYPE_STRING).setCellValue(singleQuantiy);
					row.createCell(11,Cell.CELL_TYPE_STRING).setCellValue(skuStr.toString());
				}
				if(countryMap.get("es")!=null){
					Map<String,Integer> skuMap = countryMap.get("es");
					StringBuilder skuStr =new StringBuilder();
					Integer singleQuantiy=0;
					for(Map.Entry<String,Integer> skuEntry:skuMap.entrySet()){
						String sku = skuEntry.getKey();
						totalQuantity+=skuMap.get(sku);
						singleQuantiy+=skuMap.get(sku);
						skuStr.append(sku).append("[").append(fnskuMap.get(sku)).append("](").append(skuMap.get(sku)).append(");");
					}
					row.createCell(12,Cell.CELL_TYPE_STRING).setCellValue(singleQuantiy);
					row.createCell(13,Cell.CELL_TYPE_STRING).setCellValue(skuStr.toString());
				}
				
				if(countryMap.get("jp")!=null){
					Map<String,Integer> skuMap = countryMap.get("jp");
					StringBuilder skuStr =new StringBuilder();
					Integer singleQuantiy=0;
					for(Map.Entry<String,Integer> skuEntry:skuMap.entrySet()){
						String sku = skuEntry.getKey();
						totalQuantity+=skuMap.get(sku);
						singleQuantiy+=skuMap.get(sku);
						skuStr.append(sku).append("[").append(fnskuMap.get(sku)).append("](").append(skuMap.get(sku)).append(");");
					}
					row.createCell(14,Cell.CELL_TYPE_STRING).setCellValue(singleQuantiy);
					row.createCell(15,Cell.CELL_TYPE_STRING).setCellValue(skuStr.toString());
				}
				
				if(countryMap.get("com")!=null){
					Map<String,Integer> skuMap = countryMap.get("com");
					StringBuilder skuStr =new StringBuilder();
					Integer singleQuantiy=0;
					for(Map.Entry<String,Integer> skuEntry:skuMap.entrySet()){
						String sku = skuEntry.getKey();
						totalQuantity+=skuMap.get(sku);
						singleQuantiy+=skuMap.get(sku);
						skuStr.append(sku).append("[").append(fnskuMap.get(sku)).append("](").append(skuMap.get(sku)).append(");");
					}
					row.createCell(16,Cell.CELL_TYPE_STRING).setCellValue(singleQuantiy);
					row.createCell(17,Cell.CELL_TYPE_STRING).setCellValue(skuStr.toString());
				}
				
				if(countryMap.get("ca")!=null){
					Map<String,Integer> skuMap = countryMap.get("ca");
					StringBuilder skuStr =new StringBuilder();
					Integer singleQuantiy=0;
					for(Map.Entry<String,Integer> skuEntry:skuMap.entrySet()){
						String sku = skuEntry.getKey();
						totalQuantity+=skuMap.get(sku);
						singleQuantiy+=skuMap.get(sku);
						skuStr.append(sku).append("[").append(fnskuMap.get(sku)).append("](").append(skuMap.get(sku)).append(");");
					}
					row.createCell(18,Cell.CELL_TYPE_STRING).setCellValue(singleQuantiy);
					row.createCell(19,Cell.CELL_TYPE_STRING).setCellValue(skuStr.toString());
				}
				
				row.createCell(2,Cell.CELL_TYPE_STRING).setCellValue(totalQuantity);
			 }
				
			
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
	
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	
		String fileName = "tranProInfos" + sdf.format(new Date()) + ".xls";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
		try {
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value =  "expTranElement" )
	public String expTranElement(PsiTransportOrder psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId()); 
			  HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet sheet = wb.createSheet();
				HSSFRow row = sheet.createRow(0);
				HSSFCellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				
				String[] title = {"运单【"+psiTransportOrder.getTransportNo()+"】申报要素明细 "};
				style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
				style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
				style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
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
				  

				Set<Integer> productIds = Sets.newHashSet();
				Map<String,PsiTransportOrderItem> map = Maps.newHashMap();
				for(PsiTransportOrderItem item :psiTransportOrder.getItems()){
					String productName = item.getProductName();
					String key =productName+"_"+item.getPackQuantity();
					if(map.get(key)==null){
						map.put(key, item);
					}
					if(!productIds.contains(item.getProduct().getId())){
						productIds.add(item.getProduct().getId());
					}
				}
				
				Set<String> tempSet = Sets.newHashSet();
				List<PsiTransportOrderItem> tranItems =new ArrayList<PsiTransportOrderItem>(map.values());
				Collections.sort(tranItems);
				
				Map<Integer,PsiProduct> proMap=this.productService.findProductsMap(productIds);
				 int j =1;
				 for(PsiTransportOrderItem item:tranItems){
					 String key =item.getProductName()+"_"+item.getPackQuantity();
					 if(!tempSet.contains(key)){
						 tempSet.add(key);
					 }else{
						 continue;
					 }
					 
					Integer productId = item.getProduct().getId();
					PsiProduct pro = proMap.get(productId);
					String productType = pro.getType();
					String  cName = "";
					String  use ="";
					String  material =item.getProduct().getMaterial();
					String[] names =pro.getChineseName().split(";");
					if(names!=null&&names.length>0){
						cName=names[0];
						if(cName.contains("(")){
							cName=cName.substring(0, cName.indexOf("("));
						}
					}
					
					String declarePoint =pro.getDeclarePoint();
					if(StringUtils.isNotEmpty(declarePoint)){
						int i=declarePoint.lastIndexOf("用途");
						if(i>0){
							String aa=declarePoint.substring(i);
							try{
								use=aa.substring(3,aa.indexOf(";"));
							}catch(Exception ex){}
						}
					}
					String label ="型号：";
					if("Tablet PC bag".equals(productType)||"Kindle cover".equals(productType)){
						label="款号：";
					}
					int i=0;
					row = sheet.createRow(j++);
					row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue(cName); 
					row = sheet.createRow(j++);
					row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("牌子："+pro.getBrand()); 
					row = sheet.createRow(j++);
					row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue(label+pro.getModel());
					row = sheet.createRow(j++);
					row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("用途："+use);
					row = sheet.createRow(j++);
					row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("材质："+material);
					
					if(StringUtils.isNotEmpty(declarePoint)){
						try{
							String[] typeArr=declarePoint.split(";");
							for (String arr : typeArr) {
								String[] cntArr=arr.split(" ");
								if(!cntArr[0].contains("中文")&&!cntArr[0].contains("英文")&&!cntArr[0].contains("型号")&&!cntArr[0].contains("品牌")&&!cntArr[0].contains("款号")&&!cntArr[0].contains("用途")&&!cntArr[0].contains("材质")){
									row = sheet.createRow(j++);
									row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue(cntArr[0]+"："+arr.substring(arr.indexOf(" ")+1));
								}
							}
						}catch(Exception e){}
					}
					
					
					/*if("Cable".equals(productType)||"HDD Adapter".equals(productType)){
						String temp ="";
						if(StringUtils.isNotEmpty(declarePoint)){
							int ii=declarePoint.lastIndexOf("电压");
							if(ii>0){
								String aa=declarePoint.substring(ii);
								try{
									temp=aa.substring(3,aa.indexOf(";"));
								}catch(Exception ex){}
							}
						}
						row = sheet.createRow(j++);
						row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("电压："+temp);
						
					}else	if("Hub".equals(productType)){
						String hasTemp ="";
						if(StringUtils.isNotEmpty(declarePoint)){
							int iii=declarePoint.lastIndexOf("有无接头");
							if(iii>0){
								String aa=declarePoint.substring(iii);
								try{
									hasTemp=aa.substring(5,aa.indexOf(";"));
								}catch(Exception ex){}
							}
						}
						
						row = sheet.createRow(j++);
						row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("有无接头："+hasTemp);
						
						String temp ="";
						if(StringUtils.isNotEmpty(declarePoint)){
							int iiii=declarePoint.lastIndexOf("试用网络");
							if(iiii>0){
								String aa=declarePoint.substring(iiii);
								try{
									temp=aa.substring(5,aa.indexOf(";"));
								}catch(Exception ex){}
							}
						}
						
						row = sheet.createRow(j++);
						row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("试用网络："+temp);
					}else if("Keyboard".equals(productType)){
						String temp ="";
						if(StringUtils.isNotEmpty(declarePoint)){
							int x=declarePoint.lastIndexOf("试用机型");
							if(x>0){
								String aa=declarePoint.substring(x);
								try{
									temp=aa.substring(5,aa.indexOf(";"));
								}catch(Exception ex){}
							}
						}
						row = sheet.createRow(j++);
						row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("试用机型："+temp);
						
						String temp1 ="";
						if(StringUtils.isNotEmpty(declarePoint)){
							int xx=declarePoint.lastIndexOf("连接方式");
							if(xx>0){
								String aa=declarePoint.substring(xx);
								try{
									temp1=aa.substring(5,aa.indexOf(";"));
								}catch(Exception ex){}
							}
						}
						row = sheet.createRow(j++);
						row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("连接方式："+temp1);
					}else	if("Speaker".equals(productType)){
						String temp ="";
						if(StringUtils.isNotEmpty(declarePoint)){
							int x=declarePoint.lastIndexOf("有无箱体");
							if(x>0){
								String aa=declarePoint.substring(x);
								try{
									temp=aa.substring(5,aa.indexOf(";"));
								}catch(Exception ex){}
							}
						}
						row = sheet.createRow(j++);
						row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("有无箱体："+temp);
						
						String temp1 ="";
						if(StringUtils.isNotEmpty(declarePoint)){
							int xx=declarePoint.lastIndexOf("单/双喇叭");
							if(xx>0){
								String aa=declarePoint.substring(xx);
								try{
									temp1=aa.substring(6,aa.indexOf(";"));
								}catch(Exception ex){}
							}
						}
						row = sheet.createRow(j++);
						row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("单/双喇叭："+temp1);
						
						String temp2 ="";
						if(StringUtils.isNotEmpty(declarePoint)){
							int xx=declarePoint.lastIndexOf("功率");
							if(xx>0){
								String aa=declarePoint.substring(xx);
								try{
									temp2=aa.substring(3,aa.indexOf(";"));
								}catch(Exception ex){}
							}
						}
						row = sheet.createRow(j++);
						row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("功率："+temp2);
						
						String temp3 ="";
						if(StringUtils.isNotEmpty(declarePoint)){
							int xx=declarePoint.lastIndexOf("喇叭口径");
							if(xx>0){
								String aa=declarePoint.substring(xx);
								try{
									temp3=aa.substring(5,aa.indexOf(";"));
								}catch(Exception ex){}
							}
						}
						row = sheet.createRow(j++);
						row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("喇叭口径："+temp3);
					}else if("Bluetooth Adaptor".equals(productType)||"HDD Adapter".equals(productType)){
						
						String temp2 ="";
						if(StringUtils.isNotEmpty(declarePoint)){
							int xx=declarePoint.lastIndexOf("功率");
							if(xx>0){
								String aa=declarePoint.substring(xx);
								try{
									temp2=aa.substring(3,aa.indexOf(";"));
								}catch(Exception ex){}
							}
						}
						row = sheet.createRow(j++);
						row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("功率："+temp2);
						
						String temp ="";
						if(StringUtils.isNotEmpty(declarePoint)){
							int xx=declarePoint.lastIndexOf("直流或交流电");
							if(xx>0){
								String aa=declarePoint.substring(xx);
								try{
									temp=aa.substring(7,aa.indexOf(";"));
								}catch(Exception ex){}
							}
						}
						row = sheet.createRow(j++);
						row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("直流或交流电："+temp);
						
						String temp1 ="";
						if(StringUtils.isNotEmpty(declarePoint)){
							int xx=declarePoint.lastIndexOf("精度");
							if(xx>0){
								String aa=declarePoint.substring(xx);
								try{
									temp1=aa.substring(3,aa.indexOf(";"));
								}catch(Exception ex){}
							}
						}
						row = sheet.createRow(j++);
						row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("精度："+temp1);
						
					}*/
					
					
					
					row = sheet.createRow(j++);
					row.createCell(i,Cell.CELL_TYPE_STRING).setCellValue("");
				 }
					
				
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
		
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		
			String fileName = "tranElementInfos" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			try {
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
	}
	
	
	
	//修正各付款项的值
	@RequiresPermissions("psi:tranRevise:edit")
	@RequestMapping(value = "revise")
	public String revise(Integer id,PsiTransportRevise psiTransportRevise, Model model){
		PsiTransportOrder psiTransportOrder=null;
		//Map<Integer,Map<String, String>> accountMaps= Maps.newHashMap();
		 Map<String,String> accountMaps =Maps.newHashMap();
		if(id!=null){
			psiTransportOrder=this.psiTransportOrderService.get(id);
		}else{
			return null;
		}
		String tranNo = psiTransportOrder.getTransportNo();
		Map<Integer,PsiSupplier> supplierMap =Maps.newLinkedHashMap();
		Map<Integer,List<String []>>  itemMap=this.getData(psiTransportOrder,supplierMap);
		
		//如果是切换供应商
		if(psiTransportRevise.getSupplier()!=null&&psiTransportRevise.getSupplier().getId()!=null){
			accountMaps=supplierMap.get(psiTransportRevise.getSupplier().getId()).getAccountMap();
		}else{
			for(Map.Entry<Integer,PsiSupplier> entry:supplierMap.entrySet()){
				Integer supplierId = entry.getKey();
				//accountMaps.put(supplierId, supplierMap.get(supplierId).getAccountMap());
				accountMaps=supplierMap.get(supplierId).getAccountMap();
				break;
			}
		}
		
		String currencyStr ="CNY,USD,EUR,JPY,CAD,GBP";
		List<String> currencys=Lists.newArrayList(currencyStr.split(","));
		
		psiTransportRevise.setTranOrderId(id);
		psiTransportRevise.setTranOrderNo(tranNo);
		
		model.addAttribute("psiTransportRevise", psiTransportRevise);
		model.addAttribute("currencys", currencys);
		//model.addAttribute("accountMaps", JSON.toJSON(accountMaps));
		model.addAttribute("accountMaps", accountMaps);
		model.addAttribute("itemMap", JSON.toJSON(itemMap));
		model.addAttribute("supplierMap", supplierMap);
		return "modules/psi/psiTransportReviseAdd";
	}
	
	
	private Map<Integer,List<String []>> getData(PsiTransportOrder psiTransportOrder,Map<Integer,PsiSupplier> supplierMap){
		//选择承运商   和各承运商的
		Map<Integer,List<String []>>  itemMap = Maps.newHashMap();
		
		if(psiTransportOrder.getLocalAmount()!=null&&psiTransportOrder.getVendor1().getId()!=null){
			List<String[]> list = Lists.newArrayList();
			Integer key=psiTransportOrder.getVendor1().getId();
			 if(itemMap.get(key)!=null){
				 list=itemMap.get(key);
			 }
			 if(supplierMap.get(key)==null){
				 supplierMap.put(key, psiTransportOrder.getVendor1());
			 }
			String amountInfo[]={"LocalAmount",psiTransportOrder.getLocalAmount()+"",psiTransportOrder.getCurrency1()};
			list.add(amountInfo);
			itemMap.put(key, list);
		}
		
		if(psiTransportOrder.getTranAmount()!=null&&psiTransportOrder.getVendor2().getId()!=null){
			List<String[]> list = Lists.newArrayList();
			Integer key=psiTransportOrder.getVendor2().getId();
			 if(itemMap.get(key)!=null){
				 list=itemMap.get(key);
			 }
			 if(supplierMap.get(key)==null){
				 supplierMap.put(key, psiTransportOrder.getVendor2());
			 }
			String amountInfo[]={"TranAmount",psiTransportOrder.getTranAmount()+"",psiTransportOrder.getCurrency2()};
			list.add(amountInfo);
			itemMap.put(key, list);
		}
		
		if(psiTransportOrder.getDapAmount()!=null&&psiTransportOrder.getVendor3().getId()!=null){
			List<String[]> list = Lists.newArrayList();
			Integer key=psiTransportOrder.getVendor3().getId();
			 if(itemMap.get(key)!=null){
				 list=itemMap.get(key);
			 }
			 if(supplierMap.get(key)==null){
				 supplierMap.put(key, psiTransportOrder.getVendor3());
			 }
			String amountInfo[]={"DapAmount",psiTransportOrder.getDapAmount()+"",psiTransportOrder.getCurrency3()};
			list.add(amountInfo);
			itemMap.put(key, list);
		}
		
		if(psiTransportOrder.getOtherAmount()!=null&&psiTransportOrder.getVendor4().getId()!=null){
			List<String[]> list = Lists.newArrayList();
			Integer key=psiTransportOrder.getVendor4().getId();
			 if(itemMap.get(key)!=null){
				 list=itemMap.get(key);
			 }
			 if(supplierMap.get(key)==null){
				 supplierMap.put(key, psiTransportOrder.getVendor4());
			 }
			String amountInfo[]={"OtherAmount",psiTransportOrder.getOtherAmount()+"",psiTransportOrder.getCurrency4()};
			list.add(amountInfo);
			itemMap.put(key, list);
		}
		
		if(psiTransportOrder.getOtherAmount1()!=null&&psiTransportOrder.getVendor7().getId()!=null){
			List<String[]> list = Lists.newArrayList();
			Integer key=psiTransportOrder.getVendor7().getId();
			 if(itemMap.get(key)!=null){
				 list=itemMap.get(key);
			 }
			 if(supplierMap.get(key)==null){
				 supplierMap.put(key, psiTransportOrder.getVendor7());
			 }
			String amountInfo[]={"OtherAmount1",psiTransportOrder.getOtherAmount1()+"",psiTransportOrder.getCurrency7()};
			list.add(amountInfo);
			itemMap.put(key, list);
		}
		
		if(psiTransportOrder.getInsuranceAmount()!=null&&psiTransportOrder.getVendor5().getId()!=null){
			List<String[]> list = Lists.newArrayList();
			Integer key=psiTransportOrder.getVendor5().getId();
			 if(itemMap.get(key)!=null){
				 list=itemMap.get(key);
			 }
			 if(supplierMap.get(key)==null){
				 supplierMap.put(key, psiTransportOrder.getVendor5());
			 }
			String amountInfo[]={"InsuranceAmount",psiTransportOrder.getInsuranceAmount()+"",psiTransportOrder.getCurrency5()};
			list.add(amountInfo);
			itemMap.put(key, list);
		}
		
		if(psiTransportOrder.getTaxTaxes()!=null&&psiTransportOrder.getVendor6().getId()!=null){
			List<String[]> list = Lists.newArrayList();
			Integer key=psiTransportOrder.getVendor6().getId();
			 if(itemMap.get(key)!=null){
				 list=itemMap.get(key);
			 }
			 if(supplierMap.get(key)==null){
				 supplierMap.put(key, psiTransportOrder.getVendor6());
			 }
			String amountInfo[]={"TaxAmount",psiTransportOrder.getTaxTaxes()+"",psiTransportOrder.getCurrency6()};
			list.add(amountInfo);
			itemMap.put(key, list);
		}
		
		if(psiTransportOrder.getDutyTaxes()!=null&&psiTransportOrder.getVendor6().getId()!=null){
			List<String[]> list = Lists.newArrayList();
			Integer key=psiTransportOrder.getVendor6().getId();
			 if(itemMap.get(key)!=null){
				 list=itemMap.get(key);
			 }
			 if(supplierMap.get(key)==null){
				 supplierMap.put(key, psiTransportOrder.getVendor6());
			 }
			String amountInfo[]={"DutyAmount",psiTransportOrder.getDutyTaxes()+"",psiTransportOrder.getCurrency6()};
			list.add(amountInfo);
			itemMap.put(key, list);
		}
		
		if(psiTransportOrder.getOtherTaxes()!=null&&psiTransportOrder.getVendor6().getId()!=null){
			List<String[]> list = Lists.newArrayList();
			Integer key=psiTransportOrder.getVendor6().getId();
			 if(itemMap.get(key)!=null){
				 list=itemMap.get(key);
			 }
			 if(supplierMap.get(key)==null){
				 supplierMap.put(key, psiTransportOrder.getVendor6());
			 }
			String amountInfo[]={"OtherTaxAmount",psiTransportOrder.getOtherTaxes()+"",psiTransportOrder.getCurrency6()};
			list.add(amountInfo);
			itemMap.put(key, list);
		}
		
		return itemMap;
	}
	

	@RequestMapping(value = "byMonth")
	public String byMonth(String productId,String type,Model model,String startDate,String endDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		 
		List<PsiProduct>  products = productService.findAll();
		if(StringUtils.isEmpty(productId)){
			productId=products.get(0).getId()+"";
		}
		if(StringUtils.isEmpty(endDate)){
			endDate =sdf.format(DateUtils.addDays(date,1));
		}
		if(StringUtils.isEmpty(startDate)){
			startDate =sdf.format(DateUtils.addMonths(date, -3));
		}
		PsiProduct curProduct = new PsiProduct();
		for(PsiProduct pro:products){
			if(productId.equals(pro.getId()+"")){
				curProduct=pro;
				break;
			}
		}
		List<Object[]> objs=this.psiTransportOrderService.getCountBySingleProduct(productId, startDate, endDate);
		Map<String,Map<String,Map<String,Float>>>  monthMap  = Maps.newTreeMap();
		for(Object[] obj:objs){
			String transportNo =obj[0].toString();
			String month=DateUtils.getShortMonth(Integer.parseInt(transportNo.substring(4, 6)));
			
			String modelStr="";
			if("0".equals(obj[1].toString())){
				modelStr="AE";
			}else if("1".equals(obj[1].toString())){
				modelStr="OE";
			}else if("2".equals(obj[1].toString())){
				modelStr="EX";
			}else if("3".equals(obj[1].toString())){
				modelStr="TR";
			}
			String market =getMarket(obj[2]==null?"":obj[2].toString());
			Integer quantity = (Integer)obj[3];
			Integer packQuantity =curProduct.getPackQuantity();
			BigDecimal boxNumber =new BigDecimal(quantity/packQuantity);
			BigDecimal volume = curProduct.getBoxVolume();
			BigDecimal weight = curProduct.getGw();
			
			Map<String,Map<String,Float>> marketMap = null; 
			
			if(monthMap.get(month)==null){
				marketMap =Maps.newHashMap();
			}else{
				marketMap=monthMap.get(month);
			}
			
			Map<String,Float> modelMap = null;
			
			if("1".equals(type)){
				Float countWeight= Float.parseFloat(df.format(weight.multiply(boxNumber)));
				if(marketMap.get(market)==null){
					modelMap = Maps.newHashMap();
				}else{
					modelMap = marketMap.get(market);
					if(modelMap.get(modelStr)!=null){
						countWeight+=modelMap.get(modelStr);
					}
				}
				modelMap.put(modelStr, countWeight);
			}else{
				Float countVolume= Float.parseFloat(df.format(volume.multiply(boxNumber)));
				if(marketMap.get(market)==null){
					modelMap = Maps.newHashMap();
				}else{
					modelMap = marketMap.get(market);
					if(modelMap.get(modelStr)!=null){
						countVolume+=modelMap.get(modelStr);
					}
				}
				modelMap.put(modelStr, countVolume);
			}
			
			marketMap.put(market, modelMap);
			monthMap.put(month, marketMap);
		}
		
		model.addAttribute("productId", productId);
		model.addAttribute("type", type);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		
		model.addAttribute("monthMap", monthMap);
		model.addAttribute("products", products);
		return "modules/psi/psiTransportOrderCount";
	}
	
	
	@RequestMapping(value = "expByMonth")
	public String expByMonth(String startDate,String endDate,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		 
		if(StringUtils.isEmpty(endDate)){
			endDate =sdf.format(DateUtils.addDays(date,1));
		}
		if(StringUtils.isEmpty(startDate)){
			startDate =sdf.format(DateUtils.addMonths(date, -3));
		}
		//获取所有产品信息
		Map<Integer,PsiProductDto>  proMap = Maps.newHashMap();
		List<PsiProduct>  products = productService.findAll();
		for(int i =0;i<products.size();i++){
			PsiProduct pro = products.get(i);
			proMap.put(pro.getId(), new PsiProductDto(pro.getId(),pro.getName(),pro.getPackQuantity(),pro.getBoxVolume(),pro.getGw()));
		}
		
		List<Object[]> objs=this.psiTransportOrderService.getCountByAllProduct(startDate, endDate);
		Map<String,Map<String,Map<String,Map<String,String>>>>  monthMap  = Maps.newTreeMap();  //月份、产品、模式、国家、（体积/重量）
		for(Object[] obj:objs){
			String transportNo =obj[0].toString();
			String month=DateUtils.getShortMonth(Integer.parseInt(transportNo.substring(4, 6)));
			String modelStr="";
			if("0".equals(obj[1].toString())){
				modelStr="AE";
			}else if("1".equals(obj[1].toString())){
				modelStr="OE";
			}else if("2".equals(obj[1].toString())){
				modelStr="EX";
			}else if("3".equals(obj[1].toString())){
				modelStr="TR";
			}
			String market =getMarket(obj[2]==null?"":obj[2].toString());
			Integer quantity = Integer.parseInt(obj[3].toString());
			Integer productId = Integer.parseInt(obj[4].toString());
			PsiProductDto curProduct = proMap.get(productId);
			Integer packQuantity =curProduct.getPackQuantity();
			BigDecimal boxNumber =new BigDecimal(quantity/packQuantity);
			BigDecimal volume = curProduct.getBoxVolume();
			BigDecimal weight = curProduct.getGw();
			
			Map<String,Map<String,Map<String,String>>> productMap = null; 
			if(monthMap.get(month)==null){
				productMap =Maps.newHashMap();
			}else{
				productMap=monthMap.get(month);
			}
			
			Map<String,Map<String,String>> marketMap = null; 
			if(productMap.get(productId+"")==null){
				marketMap = Maps.newHashMap();
			}else{
				marketMap = productMap.get(productId+"");
			}
			
			Map<String,String> modelMap = null;
			Float countWeight= Float.parseFloat(df.format(weight.multiply(boxNumber)));
			Float countVolume= Float.parseFloat(df.format(volume.multiply(boxNumber)));
			if(marketMap.get(market)==null){
				modelMap = Maps.newHashMap();
			}else{
				modelMap = marketMap.get(market);
				if(modelMap.get(modelStr)!=null){
					countWeight+=Float.parseFloat(modelMap.get(modelStr).split(";")[0]);
					countVolume+=Float.parseFloat(modelMap.get(modelStr).split(";")[1]);
				}
			}
			modelMap.put(modelStr, countWeight+";"+countVolume);
			marketMap.put(market, modelMap);
			productMap.put(productId+"", marketMap);
			monthMap.put(month, productMap);
		}
		
		
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFillBackgroundColor(HSSFColor.YELLOW.index);
		style1.setFillForegroundColor(HSSFColor.YELLOW.index);
		style1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
		
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		HSSFCell cell1=row.createCell(2);
		cell1.setCellValue("EU");
		HSSFCell cell2=row.createCell(6);
		cell2.setCellValue("US");
		HSSFCell cell3=row.createCell(10);
		cell3.setCellValue("JP");
		HSSFCell cell4=row.createCell(14);
		cell4.setCellValue("CA");
		HSSFCell cell5=row.createCell(18);
		cell5.setCellValue("MX");
		
		
		//参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
		Region region0 = new Region(0, (short) 0, 0, (short) 1);
		Region region1 = new Region(0, (short) 2, 0, (short) 5);
		Region region2 = new Region(0, (short) 6, 0, (short) 9);
		Region region3 = new Region(0, (short) 10, 0, (short) 13);
		Region region4 = new Region(0, (short) 14, 0, (short) 17);
		Region region5 = new Region(0, (short) 18, 0, (short) 21);
		sheet.addMergedRegion(region0);
		sheet.addMergedRegion(region1);
		sheet.addMergedRegion(region2);
		sheet.addMergedRegion(region3);
		sheet.addMergedRegion(region4);
		sheet.addMergedRegion(region5);
		row = sheet.createRow(1);
		String[] title = { "  MONTH  ","  ProductName  ","  AE  ","	OE  ","	 EX  "," TR ","  AE  ","	OE  ","	 EX  "," TR ","  AE  ","	OE  ","	 EX  "," TR ","  AE  ","	OE  ","	 EX  "," TR ","  AE  ","	OE  ","	 EX  "," TR "};
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// //设置字体
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

		int j = 2;
		for (Map.Entry<String, Map<String, Map<String, Map<String, String>>>> entry : monthMap.entrySet()) {
			String monthKey = entry.getKey();
			//单月重量
			Map<String,Float> totalMap = Maps.newHashMap();
			for(Map.Entry<String, Map<String, Map<String, String>>> entry1:entry.getValue().entrySet()){
				String productId = entry1.getKey();
				Map<String,Map<String,String>> marketMap = entry1.getValue();
				row = sheet.createRow(j++);
				int i =0;
				row.createCell(i++, Cell.CELL_TYPE_STRING).setCellValue(monthKey);
				row.createCell(i++, Cell.CELL_TYPE_STRING).setCellValue(proMap.get(Integer.parseInt(productId)).getProductName());
				if(marketMap.get("EU")!=null){
					String temp1 =StringUtils.isNotEmpty(marketMap.get("EU").get("AE"))?marketMap.get("EU").get("AE").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp1)){
						row.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp1)));	
						Float tempTotal =Float.parseFloat(temp1);
						if(totalMap.get("EU,AE")!=null){
							tempTotal+=totalMap.get("EU,AE");
						}
						totalMap.put("EU,AE", tempTotal);
					}
					
					String temp2 =StringUtils.isNotEmpty(marketMap.get("EU").get("OE"))?marketMap.get("EU").get("OE").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp2)){
						row.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp2)));	
						Float tempTotal =Float.parseFloat(temp2);
						if(totalMap.get("EU,OE")!=null){
							tempTotal+=totalMap.get("EU,OE");
						}
						totalMap.put("EU,OE", tempTotal);
					}
					
					
					String temp3 =StringUtils.isNotEmpty(marketMap.get("EU").get("EX"))?marketMap.get("EU").get("EX").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp3)){
						row.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp3)));	
						Float tempTotal =Float.parseFloat(temp3);
						if(totalMap.get("EU,EX")!=null){
							tempTotal+=totalMap.get("EU,EX");
						}
						totalMap.put("EU,EX", tempTotal);
					}
					
					String temp4 =StringUtils.isNotEmpty(marketMap.get("EU").get("TR"))?marketMap.get("EU").get("TR").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp4)){
						row.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp4)));	
						Float tempTotal =Float.parseFloat(temp4);
						if(totalMap.get("EU,TR")!=null){
							tempTotal+=totalMap.get("EU,TR");
						}
						totalMap.put("EU,TR", tempTotal);
					}
				}
				
				
				if(marketMap.get("US")!=null){
					String temp1 =StringUtils.isNotEmpty(marketMap.get("US").get("AE"))?marketMap.get("US").get("AE").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp1)){
						row.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp1)));	
						Float tempTotal =Float.parseFloat(temp1);
						if(totalMap.get("US,AE")!=null){
							tempTotal+=totalMap.get("US,AE");
						}
						totalMap.put("US,AE", tempTotal);
					}
					
					String temp2 =StringUtils.isNotEmpty(marketMap.get("US").get("OE"))?marketMap.get("US").get("OE").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp2)){
						row.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp2)));	
						Float tempTotal =Float.parseFloat(temp2);
						if(totalMap.get("US,OE")!=null){
							tempTotal+=totalMap.get("US,OE");
						}
						totalMap.put("US,OE", tempTotal);
					}
					
					
					String temp3 =StringUtils.isNotEmpty(marketMap.get("US").get("EX"))?marketMap.get("US").get("EX").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp3)){
						row.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp3)));	
						Float tempTotal =Float.parseFloat(temp3);
						if(totalMap.get("US,EX")!=null){
							tempTotal+=totalMap.get("US,EX");
						}
						totalMap.put("US,EX", tempTotal);
					}
					
					String temp4 =StringUtils.isNotEmpty(marketMap.get("US").get("TR"))?marketMap.get("US").get("TR").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp4)){
						row.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp4)));	
						Float tempTotal =Float.parseFloat(temp4);
						if(totalMap.get("EU,US")!=null){
							tempTotal+=totalMap.get("EU,US");
						}
						totalMap.put("EU,US", tempTotal);
					}
				}
				
				
				if(marketMap.get("JP")!=null){
					String temp1 =StringUtils.isNotEmpty(marketMap.get("JP").get("AE"))?marketMap.get("JP").get("AE").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp1)){
						row.createCell(10, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp1)));	
						Float tempTotal =Float.parseFloat(temp1);
						if(totalMap.get("JP,AE")!=null){
							tempTotal+=totalMap.get("JP,AE");
						}
						totalMap.put("JP,AE", tempTotal);
					}
					
					String temp2 =StringUtils.isNotEmpty(marketMap.get("JP").get("OE"))?marketMap.get("JP").get("OE").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp2)){
						row.createCell(11, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp2)));	
						Float tempTotal =Float.parseFloat(temp2);
						if(totalMap.get("JP,OE")!=null){
							tempTotal+=totalMap.get("JP,OE");
						}
						totalMap.put("JP,OE", tempTotal);
					}
					
					
					String temp3 =StringUtils.isNotEmpty(marketMap.get("JP").get("EX"))?marketMap.get("JP").get("EX").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp3)){
						row.createCell(12, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp3)));	
						Float tempTotal =Float.parseFloat(temp3);
						if(totalMap.get("JP,EX")!=null){
							tempTotal+=totalMap.get("JP,EX");
						}
						totalMap.put("JP,EX", tempTotal);
					}
					
					String temp4 =StringUtils.isNotEmpty(marketMap.get("JP").get("TR"))?marketMap.get("JP").get("TR").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp4)){
						row.createCell(13, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp4)));	
						Float tempTotal =Float.parseFloat(temp4);
						if(totalMap.get("JP,TR")!=null){
							tempTotal+=totalMap.get("JP,TR");
						}
						totalMap.put("JP,TR", tempTotal);
					}
				}
				
				
				if(marketMap.get("CA")!=null){
					String temp1 =StringUtils.isNotEmpty(marketMap.get("CA").get("AE"))?marketMap.get("CA").get("AE").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp1)){
						row.createCell(14, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp1)));	
						Float tempTotal =Float.parseFloat(temp1);
						if(totalMap.get("CA,AE")!=null){
							tempTotal+=totalMap.get("CA,AE");
						}
						totalMap.put("CA,AE", tempTotal);
					}
					
					String temp2 =StringUtils.isNotEmpty(marketMap.get("CA").get("OE"))?marketMap.get("CA").get("OE").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp2)){
						row.createCell(15, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp2)));	
						Float tempTotal =Float.parseFloat(temp2);
						if(totalMap.get("CA,OE")!=null){
							tempTotal+=totalMap.get("CA,OE");
						}
						totalMap.put("CA,OE", tempTotal);
					}
					
					
					String temp3 =StringUtils.isNotEmpty(marketMap.get("CA").get("EX"))?marketMap.get("CA").get("EX").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp3)){
						row.createCell(16, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp3)));	
						Float tempTotal =Float.parseFloat(temp3);
						if(totalMap.get("CA,EX")!=null){
							tempTotal+=totalMap.get("CA,EX");
						}
						totalMap.put("CA,EX", tempTotal);
					}
					
					String temp4 =StringUtils.isNotEmpty(marketMap.get("CA").get("TR"))?marketMap.get("CA").get("TR").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp4)){
						row.createCell(17, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp4)));	
						Float tempTotal =Float.parseFloat(temp4);
						if(totalMap.get("CA,TR")!=null){
							tempTotal+=totalMap.get("CA,TR");
						}
						totalMap.put("CA,TR", tempTotal);
					}
				}
				
				if(marketMap.get("MX")!=null){
					String temp1 =StringUtils.isNotEmpty(marketMap.get("MX").get("AE"))?marketMap.get("MX").get("AE").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp1)){
						row.createCell(18, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp1)));	
						Float tempTotal =Float.parseFloat(temp1);
						if(totalMap.get("MX,AE")!=null){
							tempTotal+=totalMap.get("MX,AE");
						}
						totalMap.put("MX,AE", tempTotal);
					}
					
					String temp2 =StringUtils.isNotEmpty(marketMap.get("MX").get("OE"))?marketMap.get("MX").get("OE").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp2)){
						row.createCell(19, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp2)));	
						Float tempTotal =Float.parseFloat(temp2);
						if(totalMap.get("MX,OE")!=null){
							tempTotal+=totalMap.get("MX,OE");
						}
						totalMap.put("MX,OE", tempTotal);
					}
					
					
					String temp3 =StringUtils.isNotEmpty(marketMap.get("MX").get("EX"))?marketMap.get("MX").get("EX").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp3)){
						row.createCell(20, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp3)));	
						Float tempTotal =Float.parseFloat(temp3);
						if(totalMap.get("MX,EX")!=null){
							tempTotal+=totalMap.get("MX,EX");
						}
						totalMap.put("MX,EX", tempTotal);
					}
					
					String temp4 =StringUtils.isNotEmpty(marketMap.get("MX").get("TR"))?marketMap.get("MX").get("TR").split(";")[0]:"";
					if(StringUtils.isNotEmpty(temp4)){
						row.createCell(21, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp4)));	
						Float tempTotal =Float.parseFloat(temp4);
						if(totalMap.get("MX,TR")!=null){
							tempTotal+=totalMap.get("MX,TR");
						}
						totalMap.put("MX,TR", tempTotal);
					}
				}
				
			}
			row = sheet.createRow(j++);
			row.setRowStyle(style1);
			row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("WeightTotal");
			if(totalMap.get("EU,AE")!=null){
				row.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("EU,AE")));
			}
			if(totalMap.get("EU,OE")!=null){
				row.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("EU,OE")));		
			}
			if(totalMap.get("EU,EX")!=null){
				row.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("EU,EX")));
			}
			
			if(totalMap.get("EU,TR")!=null){
				row.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("EU,TR")));
			}
			
			if(totalMap.get("US,AE")!=null){
				row.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("US,AE")));
			}
			if(totalMap.get("US,OE")!=null){
				row.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("US,OE")));		
			}
			if(totalMap.get("US,EX")!=null){
				row.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("US,EX")));
			}
			if(totalMap.get("US,TR")!=null){
				row.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("US,TR")));
			}
			
			if(totalMap.get("JP,AE")!=null){
				row.createCell(10, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("JP,AE")));
			}
			if(totalMap.get("JP,OE")!=null){
				row.createCell(11, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("JP,OE")));		
			}
			if(totalMap.get("JP,EX")!=null){
				row.createCell(12, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("JP,EX")));
			}
			if(totalMap.get("JP,TR")!=null){
				row.createCell(13, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("JP,TR")));
			}
			
			if(totalMap.get("CA,AE")!=null){
				row.createCell(14, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("CA,AE")));
			}
			if(totalMap.get("CA,OE")!=null){
				row.createCell(15, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("CA,OE")));		
			}
			if(totalMap.get("CA,EX")!=null){
				row.createCell(16, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("CA,EX")));
			}
			if(totalMap.get("CA,TR")!=null){
				row.createCell(17, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("CA,TR")));
			}
			
			
			if(totalMap.get("MX,AE")!=null){
				row.createCell(18, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("MX,AE")));
			}
			if(totalMap.get("MX,OE")!=null){
				row.createCell(19, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("MX,OE")));		
			}
			if(totalMap.get("MX,EX")!=null){
				row.createCell(20, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("MX,EX")));
			}
			if(totalMap.get("MX,TR")!=null){
				row.createCell(17, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("MX,TR")));
			}
			
			
			//单月体积
			totalMap = Maps.newHashMap();
			for(Map.Entry<String, Map<String, Map<String, String>>> entry1:entry.getValue().entrySet()){
				String productId = entry1.getKey();
				Map<String,Map<String,String>> marketMap = entry1.getValue();
				row = sheet.createRow(j++);
				int i =0;
				row.createCell(i++, Cell.CELL_TYPE_STRING).setCellValue(monthKey);
				row.createCell(i++, Cell.CELL_TYPE_STRING).setCellValue(proMap.get(Integer.parseInt(productId)).getProductName());
				if(marketMap.get("EU")!=null){
					String temp1 =StringUtils.isNotEmpty(marketMap.get("EU").get("AE"))?marketMap.get("EU").get("AE").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp1)){
						row.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp1)));	
						Float tempTotal =Float.parseFloat(temp1);
						if(totalMap.get("EU,AE")!=null){
							tempTotal+=totalMap.get("EU,AE");
						}
						totalMap.put("EU,AE", tempTotal);
					}
					
					String temp2 =StringUtils.isNotEmpty(marketMap.get("EU").get("OE"))?marketMap.get("EU").get("OE").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp2)){
						row.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp2)));	
						Float tempTotal =Float.parseFloat(temp2);
						if(totalMap.get("EU,OE")!=null){
							tempTotal+=totalMap.get("EU,OE");
						}
						totalMap.put("EU,OE", tempTotal);
					}
					
					
					String temp3 =StringUtils.isNotEmpty(marketMap.get("EU").get("EX"))?marketMap.get("EU").get("EX").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp3)){
						row.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp3)));	
						Float tempTotal =Float.parseFloat(temp3);
						if(totalMap.get("EU,EX")!=null){
							tempTotal+=totalMap.get("EU,EX");
						}
						totalMap.put("EU,EX", tempTotal);
					}
					
					String temp4 =StringUtils.isNotEmpty(marketMap.get("EU").get("TR"))?marketMap.get("EU").get("TR").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp4)){
						row.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp4)));	
						Float tempTotal =Float.parseFloat(temp4);
						if(totalMap.get("EU,TR")!=null){
							tempTotal+=totalMap.get("EU,TR");
						}
						totalMap.put("EU,TR", tempTotal);
					}
				}
				
				
				if(marketMap.get("US")!=null){
					String temp1 =StringUtils.isNotEmpty(marketMap.get("US").get("AE"))?marketMap.get("US").get("AE").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp1)){
						row.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp1)));	
						Float tempTotal =Float.parseFloat(temp1);
						if(totalMap.get("US,AE")!=null){
							tempTotal+=totalMap.get("US,AE");
						}
						totalMap.put("US,AE", tempTotal);
					}
					
					String temp2 =StringUtils.isNotEmpty(marketMap.get("US").get("OE"))?marketMap.get("US").get("OE").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp2)){
						row.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp2)));	
						Float tempTotal =Float.parseFloat(temp2);
						if(totalMap.get("US,OE")!=null){
							tempTotal+=totalMap.get("US,OE");
						}
						totalMap.put("US,OE", tempTotal);
					}
					
					
					String temp3 =StringUtils.isNotEmpty(marketMap.get("US").get("EX"))?marketMap.get("US").get("EX").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp3)){
						row.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp3)));	
						Float tempTotal =Float.parseFloat(temp3);
						if(totalMap.get("US,EX")!=null){
							tempTotal+=totalMap.get("US,EX");
						}
						totalMap.put("US,EX", tempTotal);
					}
					
					String temp4 =StringUtils.isNotEmpty(marketMap.get("US").get("TR"))?marketMap.get("US").get("TR").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp4)){
						row.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp4)));	
						Float tempTotal =Float.parseFloat(temp4);
						if(totalMap.get("EU,US")!=null){
							tempTotal+=totalMap.get("EU,US");
						}
						totalMap.put("EU,US", tempTotal);
					}
				}
				
				
				if(marketMap.get("JP")!=null){
					String temp1 =StringUtils.isNotEmpty(marketMap.get("JP").get("AE"))?marketMap.get("JP").get("AE").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp1)){
						row.createCell(10, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp1)));	
						Float tempTotal =Float.parseFloat(temp1);
						if(totalMap.get("JP,AE")!=null){
							tempTotal+=totalMap.get("JP,AE");
						}
						totalMap.put("JP,AE", tempTotal);
					}
					
					String temp2 =StringUtils.isNotEmpty(marketMap.get("JP").get("OE"))?marketMap.get("JP").get("OE").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp2)){
						row.createCell(11, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp2)));	
						Float tempTotal =Float.parseFloat(temp2);
						if(totalMap.get("JP,OE")!=null){
							tempTotal+=totalMap.get("JP,OE");
						}
						totalMap.put("JP,OE", tempTotal);
					}
					
					
					String temp3 =StringUtils.isNotEmpty(marketMap.get("JP").get("EX"))?marketMap.get("JP").get("EX").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp3)){
						row.createCell(12, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp3)));	
						Float tempTotal =Float.parseFloat(temp3);
						if(totalMap.get("JP,EX")!=null){
							tempTotal+=totalMap.get("JP,EX");
						}
						totalMap.put("JP,EX", tempTotal);
					}
					
					String temp4 =StringUtils.isNotEmpty(marketMap.get("JP").get("TR"))?marketMap.get("JP").get("TR").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp4)){
						row.createCell(13, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp4)));	
						Float tempTotal =Float.parseFloat(temp4);
						if(totalMap.get("JP,TR")!=null){
							tempTotal+=totalMap.get("JP,TR");
						}
						totalMap.put("JP,TR", tempTotal);
					}
				}
				
				
				if(marketMap.get("CA")!=null){
					String temp1 =StringUtils.isNotEmpty(marketMap.get("CA").get("AE"))?marketMap.get("CA").get("AE").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp1)){
						row.createCell(14, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp1)));	
						Float tempTotal =Float.parseFloat(temp1);
						if(totalMap.get("CA,AE")!=null){
							tempTotal+=totalMap.get("CA,AE");
						}
						totalMap.put("CA,AE", tempTotal);
					}
					
					String temp2 =StringUtils.isNotEmpty(marketMap.get("CA").get("OE"))?marketMap.get("CA").get("OE").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp2)){
						row.createCell(15, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp2)));	
						Float tempTotal =Float.parseFloat(temp2);
						if(totalMap.get("CA,OE")!=null){
							tempTotal+=totalMap.get("CA,OE");
						}
						totalMap.put("CA,OE", tempTotal);
					}
					
					
					String temp3 =StringUtils.isNotEmpty(marketMap.get("CA").get("EX"))?marketMap.get("CA").get("EX").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp3)){
						row.createCell(16, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp3)));	
						Float tempTotal =Float.parseFloat(temp3);
						if(totalMap.get("CA,EX")!=null){
							tempTotal+=totalMap.get("CA,EX");
						}
						totalMap.put("CA,EX", tempTotal);
					}
					
					String temp4 =StringUtils.isNotEmpty(marketMap.get("CA").get("TR"))?marketMap.get("CA").get("TR").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp4)){
						row.createCell(17, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp4)));	
						Float tempTotal =Float.parseFloat(temp4);
						if(totalMap.get("CA,TR")!=null){
							tempTotal+=totalMap.get("CA,TR");
						}
						totalMap.put("CA,TR", tempTotal);
					}
				}
				if(marketMap.get("MX")!=null){
					String temp1 =StringUtils.isNotEmpty(marketMap.get("MX").get("AE"))?marketMap.get("MX").get("AE").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp1)){
						row.createCell(18, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp1)));	
						Float tempTotal =Float.parseFloat(temp1);
						if(totalMap.get("MX,AE")!=null){
							tempTotal+=totalMap.get("MX,AE");
						}
						totalMap.put("MX,AE", tempTotal);
					}
					
					String temp2 =StringUtils.isNotEmpty(marketMap.get("MX").get("OE"))?marketMap.get("MX").get("OE").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp2)){
						row.createCell(19, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp2)));	
						Float tempTotal =Float.parseFloat(temp2);
						if(totalMap.get("MX,OE")!=null){
							tempTotal+=totalMap.get("MX,OE");
						}
						totalMap.put("MX,OE", tempTotal);
					}
					
					
					String temp3 =StringUtils.isNotEmpty(marketMap.get("MX").get("EX"))?marketMap.get("MX").get("EX").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp3)){
						row.createCell(20, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp3)));	
						Float tempTotal =Float.parseFloat(temp3);
						if(totalMap.get("MX,EX")!=null){
							tempTotal+=totalMap.get("MX,EX");
						}
						totalMap.put("MX,EX", tempTotal);
					}
					
					String temp4 =StringUtils.isNotEmpty(marketMap.get("MX").get("TR"))?marketMap.get("MX").get("TR").split(";")[1]:"";
					if(StringUtils.isNotEmpty(temp4)){
						row.createCell(21, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(Float.parseFloat(temp4)));	
						Float tempTotal =Float.parseFloat(temp4);
						if(totalMap.get("MX,TR")!=null){
							tempTotal+=totalMap.get("MX,TR");
						}
						totalMap.put("MX,TR", tempTotal);
					}
				}
			}
			row = sheet.createRow(j++);
			row.setRowStyle(style1);
			row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("VolumeTotal");
		
			if(totalMap.get("EU,AE")!=null){
				row.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("EU,AE")));
			}
			if(totalMap.get("EU,OE")!=null){
				row.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("EU,OE")));		
			}
			if(totalMap.get("EU,EX")!=null){
				row.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("EU,EX")));
			}
			if(totalMap.get("EU,TR")!=null){
				row.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("EU,TR")));
			}
			if(totalMap.get("US,AE")!=null){
				row.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("US,AE")));
			}
			if(totalMap.get("US,OE")!=null){
				row.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("US,OE")));		
			}
			if(totalMap.get("US,EX")!=null){
				row.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("US,EX")));
			}
			if(totalMap.get("US,TR")!=null){
				row.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("US,TR")));
			}
			if(totalMap.get("JP,AE")!=null){
				row.createCell(10, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("JP,AE")));
			}
			if(totalMap.get("JP,OE")!=null){
				row.createCell(11, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("JP,OE")));		
			}
			if(totalMap.get("JP,EX")!=null){
				row.createCell(12, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("JP,EX")));
			}
			if(totalMap.get("JP,TR")!=null){
				row.createCell(13, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("JP,TR")));
			}
			
			if(totalMap.get("CA,AE")!=null){
				row.createCell(14, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("CA,AE")));
			}
			if(totalMap.get("CA,OE")!=null){
				row.createCell(15, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("CA,OE")));		
			}
			if(totalMap.get("CA,EX")!=null){
				row.createCell(16, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("CA,EX")));
			}
			if(totalMap.get("CA,TR")!=null){
				row.createCell(17, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("CA,TR")));
			}
			
			if(totalMap.get("MX,AE")!=null){
				row.createCell(18, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("MX,AE")));
			}
			if(totalMap.get("MX,OE")!=null){
				row.createCell(19, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("MX,OE")));		
			}
			if(totalMap.get("MX,EX")!=null){
				row.createCell(20, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("MX,EX")));
			}
			if(totalMap.get("MX,TR")!=null){
				row.createCell(21, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(totalMap.get("MX,TR")));
			}
		}
			
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
	response.setContentType("application/x-download");

	SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");

	String fileName = "tranCounts" + sdf1.format(new Date()) + ".xls";
	fileName = URLEncoder.encode(fileName, "UTF-8");
	response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
	try {
		OutputStream out = response.getOutputStream();
		wb.write(out);
		out.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
		
		return null;
	}
	
	@RequiresPermissions("psi:transport:edit")
	@RequestMapping(value = "add")
	public String add(PsiTransportOrder psiTransportOrder, Model model) throws IOException {
		List<PsiSupplier> tranSuppliers=this.psiSupplierService.findAllTransporter();
		List<Stock> toStock = this.stockService.findStocks("2");
		Map<Integer,String> toCountryMap = Maps.newHashMap();
		for(Stock stock:toStock){
			String country ="";
			if(StringUtils.isNotEmpty(stock.getPlatform())){
				country=stock.getPlatform();
			}else{
				country=stock.getCountrycode();
				if(StringUtils.isNotEmpty(country)){
					country = country.toLowerCase();
				}
			}
			toCountryMap.put(stock.getId(),"us".equals(country)?"com":country);
		}
		List<PsiProduct>  products = productService.findAll();
		String currencyStr ="CNY,USD,EUR,JPY,CAD,GBP";
		List<String> currencys=Lists.newArrayList(currencyStr.split(","));
		
		
		//获取中国仓和绑定的sku start
		Map<String,String> fnskuMap=amazonProduct2Service.getSkuAndFnskuMap();
		List<PsiInventory> inventorys=this.psiInventoryService.findByStock(21);
		Map<String,Set<String>> inventorySkuMap=Maps.newHashMap();
		Map<String,String>  skuQuantityMap = Maps.newHashMap();
		for(PsiInventory inventory:inventorys){
			String key=inventory.getProductId()+","+inventory.getCountryCode()+","+inventory.getColorCode();
			Set<String> set=Sets.newHashSet();
			if(inventorySkuMap.get(key)!=null){
				set=inventorySkuMap.get(key);
			}
			set.add(inventory.getSku());
			inventorySkuMap.put(key, set);
			skuQuantityMap.put(inventory.getSku(), inventory.getSku()+"["+fnskuMap.get(inventory.getSku())+"]("+inventory.getNewQuantity()+")");
		}
		
		Map<String,String>  bangdingSkuMap =this.productService.getAllBandingProductSku();
		
		for(Map.Entry<String,String> entry:bangdingSkuMap.entrySet()){
			String key = entry.getKey();
			Set<String> innerSet = null;
			if(inventorySkuMap.get(key)==null){
				innerSet = Sets.newHashSet();
			}else{
				innerSet = inventorySkuMap.get(key);
			}
			String sku = entry.getValue();
			if(!skuQuantityMap.containsKey(sku)){
				skuQuantityMap.put(sku, sku+"["+fnskuMap.get(sku)+"](无记录)");
			}
			innerSet.add(sku);
			inventorySkuMap.put(key, innerSet);
		}
		
		model.addAttribute("bangdingSkus", JSON.toJSON(bangdingSkuMap.values()));
		model.addAttribute("inventorySkuMap", JSON.toJSON(inventorySkuMap));
		model.addAttribute("skuQuantityMap", JSON.toJSON(skuQuantityMap));
		//获取中国仓和绑定的sku end
		
		psiTransportOrder.setTransportSta("0");//前台穿个新建状态    前台判断用的着；
		//生成流水号
		String transportNo =this.psiTransportOrderService.createFlowNo();
		psiTransportOrder.setTransportNo(transportNo);
		Map<String,String> map=amazonUnlineOrderService.getUnUseUnlineOrder();
		
		model.addAttribute("map", map);
		model.addAttribute("currencys", currencys);
		model.addAttribute("products", products);
		model.addAttribute("toStock", toStock);
		model.addAttribute("tranSuppliers", tranSuppliers);
		model.addAttribute("psiTransportOrder", psiTransportOrder);
		model.addAttribute("toCountryMap", JSON.toJSON(toCountryMap));
		return "modules/psi/psiTransportOrderAdd";
		
	}
	
	@RequiresPermissions("psi:transport:edit")
	@RequestMapping(value = "edit")
	public String edit(String flag,PsiTransportOrder psiTransportOrder,Model model) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
		}
		
		Map<String,String> fnskuMap=amazonProduct2Service.getSkuAndFnskuMap();
		Map<String,String>  skuQuantityMap = Maps.newHashMap();
		//查询该运单有没有建付款单
		List<String> noEdits=this.psiTransportPaymentItemService.findPayItemByTranId(psiTransportOrder.getId());
		StringBuilder canEditStr = new StringBuilder();
		for(String payTypes:noEdits){
			canEditStr.append(payTypes).append(",");
		}
		
		StringBuilder sb = new StringBuilder("");
		StringBuilder sb1 = new StringBuilder("");
		for(PsiTransportOrderItem item:psiTransportOrder.getItems()){
			if(!item.getProductColorCountry().equals(item.getSku())){
				skuQuantityMap.put(item.getSku(), item.getSku()+"["+fnskuMap.get(item.getSku())+"]");
			}
			sb.append(item.getId()+",");
		}
		//海运并且模式是FCL  只要是海运模式有值都传 &&psiTransportOrder.getOceanModel().equals("FCL")
		if(!StringUtils.isEmpty(psiTransportOrder.getOceanModel())&&(psiTransportOrder.getModel().equals("1")||psiTransportOrder.getModel().equals("3"))){
			for(PsiTransportOrderContainer container:psiTransportOrder.getContainerItems()){
				sb1.append(container.getId()+",");
			}
		}
		String itemIds="";
		String containerIds ="";
		if(StringUtils.isNotEmpty(sb.toString())){
			itemIds=sb.toString().substring(0,sb.toString().length()-1);
		}
		
		if(StringUtils.isNotEmpty(sb1.toString())){
			containerIds=sb1.toString().substring(0,sb1.toString().length()-1);
		}
		psiTransportOrder.setOldItemIds(itemIds);
		psiTransportOrder.setOldContainerIds(containerIds);
		
		
		List<PsiSupplier> tranSuppliers=this.psiSupplierService.findAllTransporter();
		List<Stock> toStock = this.stockService.findStocks("2");
		List<PsiProduct>  products = productService.findAll();
		String currencyStr ="CNY,USD,EUR,JPY,CAD,GBP";
		List<String> currencys=Lists.newArrayList(currencyStr.split(","));
		
		model.addAttribute("canEditStr", canEditStr.toString());
		model.addAttribute("currencys", currencys);
		model.addAttribute("products", products);
		model.addAttribute("toStock", toStock);
		model.addAttribute("tranSuppliers", tranSuppliers);
		model.addAttribute("psiTransportOrder", psiTransportOrder);
		model.addAttribute("canCreateFba", psiTransportOrder.getIsCreateFba());
		model.addAttribute("canSplit", psiTransportOrder.getCanSplit());
		model.addAttribute("inventory", psiInventoryService.getSkuInventoryMap(psiTransportOrder.getId(), "1"));
		
		
		
		
		//如果是   是发向fba的
		if("1".equals(psiTransportOrder.getTransportType())){
			//查询所有状态fba贴  并且去向国一至From CN
			List<String> countrys=Lists.newArrayList();
			if("de".equals(psiTransportOrder.getToCountry())){
				countrys.add("de");
				countrys.add("it");
				countrys.add("es");
				countrys.add("fr");
				countrys.add("uk");
			}else{
				countrys.add(psiTransportOrder.getToCountry());
			}
			List<FbaInbound> fbaList=fbaInBoundService.findFba(countrys,psiTransportOrder.getFromStore().getCountrycode(),true);
			model.addAttribute("fbaList", fbaList);
		}
		
		if("0".equals(flag)){
			//获取中国仓和绑定的sku start
			List<PsiInventory> inventorys=this.psiInventoryService.findByStock(21);
			Map<String,Set<String>> inventorySkuMap=Maps.newHashMap();
			
			for(PsiInventory inventory:inventorys){
				String key=inventory.getProductId()+","+inventory.getCountryCode()+","+inventory.getColorCode();
				Set<String> set=Sets.newHashSet();
				if(inventorySkuMap.get(key)!=null){
					set=inventorySkuMap.get(key);
				}
				set.add(inventory.getSku());
				inventorySkuMap.put(key, set);
				skuQuantityMap.put(inventory.getSku(), inventory.getSku()+"["+fnskuMap.get(inventory.getSku())+"]("+inventory.getNewQuantity()+")");
			}
			
			Map<String,String>  bangdingSkuMap =this.productService.getAllBandingProductSku();
			Map<String,String> bangdingMap = Maps.newHashMap();
			for(String sku:bangdingSkuMap.values()){
				bangdingMap.put(sku, "1");
			}
			
			for(Map.Entry<String,String> entry:bangdingSkuMap.entrySet()){
				String key = entry.getKey();
				Set<String> innerSet = null;
				if(inventorySkuMap.get(key)==null){
					innerSet = Sets.newHashSet();
				}else{
					innerSet = inventorySkuMap.get(key);
				}
				String sku = entry.getValue();
				if(!skuQuantityMap.containsKey(sku)){
					skuQuantityMap.put(sku, sku+"["+fnskuMap.get(sku)+"](无记录)");
				}
				innerSet.add(sku);
				inventorySkuMap.put(key, innerSet);
			}
			model.addAttribute("bangdingMap",bangdingMap);
			model.addAttribute("bangdingSkus", JSON.toJSON(bangdingSkuMap.values()));
			model.addAttribute("inventorySkus", inventorySkuMap);
			model.addAttribute("skuQuantitys",  skuQuantityMap);
			model.addAttribute("inventorySkuMap", JSON.toJSON(inventorySkuMap));
			model.addAttribute("skuQuantityMap", JSON.toJSON(skuQuantityMap));
			//获取中国仓和绑定的sku end
		}
		
		Map<String,String> accountMap=productService.findAccountByCountry();
		model.addAttribute("accountMap",accountMap);
		model.addAttribute("accountList",amazonAccountConfigService.findCountryByAccount());
		
		if(psiTransportOrder.getModel().equals("0")||psiTransportOrder.getModel().equals("2")){
			if(psiTransportOrder.getModel().equals("0")){
				model.addAttribute("typeSupplier", LogisticsSupplier.getLogisticsSupplierByType("1"));
			}else{
				model.addAttribute("typeSupplier", LogisticsSupplier.getLogisticsSupplierByType("2"));
			}
			if("0".equals(flag)){
				//产品编辑
				return "modules/psi/psiTransportOrderAeAndExProduct";
			}else if("1".equals(flag)){
				//花费编辑
				return "modules/psi/psiTransportOrderAeAndExCost";
			}
		}else if(psiTransportOrder.getModel().equals("1")||psiTransportOrder.getModel().equals("3")){
			String containerTypesStr="20GP,40GP,40HQ,45HQ";
			List<String> containerTypes=Lists.newArrayList(containerTypesStr.split(","));
			model.addAttribute("containerTypes", containerTypes);
			//model.addAttribute("typeSupplier",LogisticsSupplier.getLogisticsSupplierByType("3"));
			if(psiTransportOrder.getModel().equals("1")){
				model.addAttribute("typeSupplier",LogisticsSupplier.getLogisticsSupplierByType("3"));
			}else{
				model.addAttribute("typeSupplier",LogisticsSupplier.getLogisticsSupplierByType("5"));
			}
			if("0".equals(flag)){
				//产品编辑
				return "modules/psi/psiTransportOrderOeProduct";
			}else if("1".equals(flag)){
				
				//花费编辑
				return "modules/psi/psiTransportOrderOeCost";
			}
		}
		
		return null;
		
	}
	
	@RequiresPermissions("psi:transport:view")
	@RequestMapping(value = "view")
	public String view(PsiTransportOrder psiTransportOrder, Model model) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
		}else if(StringUtils.isNotEmpty(psiTransportOrder.getTransportNo())){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getTransportNo());
		}else{
			return null;
		}
		model.addAttribute("psiTransportOrder", psiTransportOrder);
		return "modules/psi/psiTransportOrderView";
	}
	
	@RequiresPermissions("psi:transport:edit")
	@RequestMapping(value = "payDone")
	public String payDone(PsiTransportOrder psiTransportOrder, Model model) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
		}
		model.addAttribute("psiTransportOrder", psiTransportOrder);
		return "modules/psi/psiTransportOrderPayDone";
	}
	
	@RequiresPermissions("psi:transport:edit")
	@RequestMapping(value = "payDoneSave")
	public String payDoneSave(PsiTransportOrder psiTransportOrder, Model model, RedirectAttributes redirectAttributes) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
		}
		//设置支付完成状态
		psiTransportOrder.setPaymentSta("2");
		this.psiTransportOrderService.save(psiTransportOrder);
		addMessage(redirectAttributes, "运单'" + psiTransportOrder.getTransportNo() + "'已付完款确认成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = {"confirmPay"})
	public String confirmPay(Integer id) {
		try{
		 this.psiTransportOrderService.updateConfirmPay("1", id);
		}catch(Exception ex){
			ex.printStackTrace();
			return "false";
		}
		return "true";
	}
	
	@ResponseBody
	@RequestMapping(value = {"ajaxPrice"})
	public String ajaxPrice(Integer productId,String countryCode,String colorCode) {
		DecimalFormat  df= new DecimalFormat("#.##");
		String res =this.psiTransportOrderService.getProPriceByProductId(productId);
		Float   partsPrice =this.psiTransportOrderService.getPartsPriceByProductId(productId, colorCode);
		Float 	price =0f;
		String  currency="";
		Integer packQuantity=0;
		String hasElectric="0";
		String hasMagnetic="0";
		if(StringUtils.isNotEmpty(res)){
			String[] arr=res.split("_");
			price=Float.parseFloat(arr[0].toString());
			//查找配件单价
			if("CNY".equals(arr[1])){//如果是人民币的换成美元
				currency="USD";
				price=partsPrice+(price/AmazonProduct2Service.getRateConfig().get("USD/CNY"));
			}else{
				price=partsPrice+price;
			}
			price=price*2;
			packQuantity =Integer.parseInt(arr[2]);
			if(arr[3]!=null&&"1".equals(arr[3])){
				hasElectric="1";
			}
			if(arr[6]!=null&&"1".equals(arr[6])){
				hasMagnetic="1";
			}
		}
		String rs="{\"msg\":\"true\",\"price\":\""+df.format(price)+"\",\"packQuantity\":\""+packQuantity+"\",\"currency\":\""+currency+"\",\"hasElectric\":\""+hasElectric+"\",\"hasMagnetic\":\""+hasMagnetic+"\"}";
		return rs;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = {"updateLadingBillNo"})
	public String updateLadingBillNo(Integer tranOrderId,String ladingBilNo) {
		/*PsiTransportOrder psiOrder=psiTransportOrderService.get(tranOrderId);
		if(psiOrder.getShipmentId()!=null){
			fbaInBoundService.upLoadSupplier(psiOrder.getShipmentId(), ladingBilNo, psiOrder.getCarrier());
		}*/
		return this.psiTransportOrderService.updateLadingBillNo(tranOrderId, ladingBilNo);
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateEtaDate"})
	public String updateEtaDate(Integer tranOrderId,String etaDate) {
		return this.psiTransportOrderService.updateEtaDate(tranOrderId, new Date(etaDate));
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateOperArrivalDate"})
	public String updateOperArrivalDate(Integer tranOrderId,String operArrivalDate) {
		try {
			Date newDate = new Date(operArrivalDate);
			PsiTransportOrder psiTransportOrder = psiTransportOrderService.get(tranOrderId);
			String state = psiTransportOrder.getTransportSta();
			if ("1".equals(state) || "2".equals(state) || "3".equals(state)) {	//出库、离港、到港三个状态记录原收货时间用于邮件提醒
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date oldDate = psiTransportOrder.getOperArrivalDate();
				psiTransportOrder.setChangeRecord((oldDate!=null?format.format(oldDate):"空")  + "-->" + format.format(newDate));
			}
			psiTransportOrder.setOperArrivalDate(newDate);
			psiTransportOrderService.save(psiTransportOrder);
		} catch (Exception e) {
			return "false";
		}
		return "true";
		//return this.psiTransportOrderService.updateOperArrivalDate(tranOrderId, new Date(operArrivalDate));
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateModel"})
	public String updateModel(Integer tranOrderId,String model) {
		return this.psiTransportOrderService.updateModel(tranOrderId, model);
	}
	
	//合单
	@RequestMapping(value = "merge")
	public String merge(String ids, RedirectAttributes redirectAttributes) throws IOException {
		psiTransportOrderService.merge(ids);
		addMessage(redirectAttributes, "合并运单成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	@RequestMapping(value = "addSave")
	public String addSave(PsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) throws IOException {
			psiTransportOrderService.addSaveData(psiTransportOrder);
			addMessage(redirectAttributes, "新建运单'" + psiTransportOrder.getTransportNo() + "'成功");
			return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	@RequestMapping(value = "editSave")
	public String editSave(PsiTransportOrder psiTransportOrder,@RequestParam("localFile")MultipartFile[] localFile,@RequestParam("tranFile")MultipartFile[] tranFile,@RequestParam("dapFile")MultipartFile[] dapFile,@RequestParam("otherFile")MultipartFile[] otherFile,@RequestParam("otherFile1")MultipartFile[] otherFile1,@RequestParam("insuranceFile")MultipartFile[] insuranceFile,@RequestParam("taxFile")MultipartFile[] taxFile, RedirectAttributes redirectAttributes) throws IOException {
		if (filePath == null) {
			filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/psiTransport";
		}
		
		psiTransportOrderService.editSaveData(psiTransportOrder,filePath,localFile,tranFile,dapFile,otherFile,otherFile1,insuranceFile,taxFile);
//		if(psiTransportOrder.getShipmentId()!=null){
//			fbaInBoundService.upLoadSupplier(psiTransportOrder.getShipmentId(),psiTransportOrder.getLadingBillNo(), psiTransportOrder.getCarrier());
//		}
		addMessage(redirectAttributes, "编辑运单'" + psiTransportOrder.getTransportNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	@RequestMapping(value = "productEditSave")
	public String productEditSave(PsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) throws IOException {
		String fbaIds = psiTransportOrder.getFbaInboundId();
		String transportSta =psiTransportOrder.getTransportSta(); 
		if (StringUtils.isNotEmpty(fbaIds)&&"0".equals(transportSta)) {	//关联item
			String[] fbaIdStr = fbaIds.split(",");
			Map<String,Integer> fbaSkus = Maps.newHashMap();
			List<FbaInbound> inbounds = Lists.newArrayList();
			for (String fbaId : fbaIdStr) {
				FbaInbound inbound = fbaInBoundService.get(Integer.parseInt(fbaId));
				for(FbaInboundItem item:inbound.getItems()){
					fbaSkus.put(item.getSku(),inbound.getId());
				}
				inbounds.add(inbound);
			}
			
			//对运单里的产品关联fba
			Set<String> tranSkus = Sets.newHashSet();
			for (PsiTransportOrderItem item : psiTransportOrder.getItems()) {
				String sku = item.getSku();
				if("0".equals(item.getOfflineSta())) {	//线上未建贴自动关联fba贴
					boolean flag = false;
					if(fbaSkus.keySet().contains(sku)) {//item的sku跟fba贴sku匹配上就自动关联
						flag = true;
						item.setFbaFlag("1");
						if(item.getFbaInboundId()==null){
							item.setFbaInboundId(fbaSkus.get(sku));
						}
					}
					if (!flag && "1".equals(item.getFbaFlag())) {	//不包含了,说明解绑了
						item.setFbaFlag("0");
						item.setFbaInboundId(null);
					}
					tranSkus.add(sku);
				}
			}
			
			//运单删除的fba里面也删除
			for(FbaInbound inbound:inbounds){
				for (Iterator<FbaInboundItem> iterator = inbound.getItems().iterator(); iterator.hasNext();) {
					FbaInboundItem inboundItem = (FbaInboundItem) iterator.next();
					//如果fba里的sku运单里没有，删除
					if(!tranSkus.contains(inboundItem.getSku())){
						iterator.remove();
					}
				}
				this.fbaInBoundService.save(inbound);
			}
		}
		psiTransportOrderService.editSaveData(psiTransportOrder);
		addMessage(redirectAttributes, "编辑运单'" + psiTransportOrder.getTransportNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	//拆单
	@RequestMapping(value = "splitEditSave")
	public String splitEditSave(PsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) throws IOException {
		String rs = psiTransportOrderService.splitSaveData(psiTransportOrder);
		if ("0".equals(rs)) {
			addMessage(redirectAttributes, "拆分运单'" + psiTransportOrder.getTransportNo() + "'失败,拆单数为0");
		} else {
			addMessage(redirectAttributes, "拆分运单'" + psiTransportOrder.getTransportNo() + "'成功,拆分的运单号为:"+rs);
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	
	@RequestMapping(value = "cancel")
	public String cancel(PsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) {
		if(psiTransportOrder.getId()!=null){
			//this.psiTransportOrderService.updateSta("8", psiTransportOrder.getId());
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
			psiTransportOrder.setTransportSta("8");				//取消状态
			psiTransportOrder.setCancelDate(new Date());		//取消时间
			psiTransportOrder.setCancelUser(UserUtils.getUser());//取消人
			this.psiTransportOrderService.save(psiTransportOrder);
		}
		addMessage(redirectAttributes, "取消运单'" + psiTransportOrder.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	@RequestMapping(value = "genFba")
	public String genFba(PsiTransportOrder psiTransportOrder,RedirectAttributes redirectAttributes) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
			if("1".equals(psiTransportOrder.getTransportType())){
				Map<String,String> accountMap=productService.findAccountByCountry();
				PsiTransportOrderItem  item=psiTransportOrder.getItems().get(0);
				
				FbaInbound inbound =fbaInBoundService.createByTransportCn(psiTransportOrder,accountMap.get(item.getSku()+"_"+item.getCountryCode()));
				psiTransportOrder.appendFbaId(inbound.getId()+"");//保存fbaId
				this.psiTransportOrderService.updateFbaId(psiTransportOrder.getTransportNo(), psiTransportOrder.getFbaInboundId());
				addMessage(redirectAttributes, "运单(" + psiTransportOrder.getTransportNo() + ")生成fba贴("+inbound.getShipmentName()+")成功!!!");
			}
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	/**
	 * 拆分创建FBA贴
	 * @param psiTransportOrder
	 * @param redirectAttributes
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "splitGenFba")
	public String splitGenFba(PsiTransportOrder psiTransportOrder,String country, RedirectAttributes redirectAttributes) throws IOException {
		
		String accountName=country.substring(0,country.lastIndexOf("_"));
		String tempCountry=country.substring(country.lastIndexOf("_")+1);
		String rs = fbaInBoundService.splitCreateFba(psiTransportOrder,tempCountry,accountName);   
		
		if ("0".equals(rs)) {
			addMessage(redirectAttributes, "运单(" + psiTransportOrder.getTransportNo() + ")生成FBA贴失败,选择的产品数为0");
		} else {
			addMessage(redirectAttributes, "运单(" + psiTransportOrder.getTransportNo() + ")生成fba贴("+rs+")成功!!!");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	
	@RequestMapping(value = "arrive")
	public String arrive(PsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
			psiTransportOrder.setTransportSta("5");						//到达状态
			psiTransportOrder.setOperArrivalDate(new Date());			//到达时间
			psiTransportOrder.setOperArrivalFixedDate(new Date());      //到达时间（不能改）
			psiTransportOrder.setOperArrivalUser(UserUtils.getUser());  //到达人
			this.psiTransportOrderService.save(psiTransportOrder);
		}
		addMessage(redirectAttributes, "运单'" + psiTransportOrder.getTransportNo() + "'已到达");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	@RequestMapping(value = "pickUp")
	public String pickUp(PsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) { 
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
			psiTransportOrder.setTransportSta("2");						//已离港状态
			psiTransportOrder.setOperFromPortDate(new Date());			//离港操作时间
			psiTransportOrder.setOperFromPortUser(UserUtils.getUser()); //离港操作人
			this.psiTransportOrderService.save(psiTransportOrder);
		}
		addMessage(redirectAttributes, "运单'" + psiTransportOrder.getTransportNo() + "'已离港");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	@RequestMapping(value = "toPort")
	public String toPort(PsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
			psiTransportOrder.setTransportSta("3");						//已到港状态
			psiTransportOrder.setOperToPortDate(new Date());			//到港操作时间
			psiTransportOrder.setOperToPortUser(UserUtils.getUser()); //到港操作人
			this.psiTransportOrderService.save(psiTransportOrder);
		}
		addMessage(redirectAttributes, "运单'" + psiTransportOrder.getTransportNo() + "'已到港");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	//将运单信息导出为excel表格
		@RequestMapping(value="exp")
		public String exportTransportOrder(PsiTransportOrder psiTransportOrder,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
			psiTransportOrder=this.psiTransportOrderService.getById(psiTransportOrder.getId());
			
			if (StringUtils.isBlank(psiTransportOrder.getSuffixName())||"PI".equals(psiTransportOrder.getSuffixName().split("-")[0])) {
				//Map<Integer,Float> priceMap=psiProductTieredPriceService.getPrice();
				ExportTransportExcel ete = new ExportTransportExcel();
				String date = new SimpleDateFormat("yyyy/MM/dd").format(psiTransportOrder.getCreateDate() == null ? new Date(): psiTransportOrder.getCreateDate());
			//	String date1 = new SimpleDateFormat("yyyyMMdd").format(psiTransportOrder.getCreateDate() == null ? new Date(): psiTransportOrder.getCreateDate());
				String date2 = new SimpleDateFormat("yyyy-MM-dd").format(psiTransportOrder.getCreateDate() == null ? new Date()	: psiTransportOrder.getCreateDate());
				
				List<PsiTransportOrderItem> items1 = psiTransportOrder.getItems();
				Map<String, PsiTransportOrderItem> map = new HashMap<String, PsiTransportOrderItem>();
				for (PsiTransportOrderItem orderItem : items1) {//合并相同产品
					String pid=orderItem.getProduct().getBrand()+" "+orderItem.getProduct().getModel();
					PsiTransportOrderItem transItem = map.get(pid+"_"+orderItem.getPackQuantity());
					if (transItem != null) {//存在
						if(getCurrencyPrice(orderItem.getCurrency(),orderItem.getItemPrice())>getCurrencyPrice(transItem.getCurrency(),transItem.getItemPrice())){
							transItem.setCurrency(orderItem.getCurrency());
							transItem.setItemPrice(orderItem.getItemPrice());
						}
						transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
					} else {
						//orderItem.setItemPrice(orderItem.getItemPrice()/1.17f);
						map.put(pid+"_"+orderItem.getPackQuantity(), orderItem);
					}
				}
				List<PsiTransportOrderItem> mapValuesList = new ArrayList<PsiTransportOrderItem>(map.values());
				Collections.sort(mapValuesList);
				psiTransportOrder.setItems(mapValuesList);
				
				Workbook workbook = null;
				String modelName = "PI";//模板文件名称
				String xmlName = "";
				if ("EU".equals(getZone(psiTransportOrder.getToCountry()))) {
					modelName = "PI-EU";
					xmlName = "PI-EU";
					psiTransportOrder.setRate1(AmazonProduct2Service.getRateConfig().get("EUR/USD"));
					psiTransportOrder.setFormatDate(date);
					psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
					psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
					List<PsiTransportOrderItem> items = psiTransportOrder.getItems();
					BigDecimal totalGw=new BigDecimal(0);
					for (PsiTransportOrderItem item : items) {
					
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":(item.getProduct().getChineseName().split(";")==null?item.getProduct().getChineseName().split("；")[1]:item.getProduct().getChineseName().split(";")[1]));
						//item.setItemPrice(new BigDecimal(getCurrencyPrice(item.getCurrency(),item.getItemPrice())).setScale(2, 4).floatValue());
					//	Float tempPrice=priceMap.get(item.getProduct().getId());
					//	item.setItemPrice(tempPrice==null?1f:tempPrice*2);
						//计算运费
						int num = item.getQuantity()/ item.getPackQuantity();//多少箱
						totalGw=totalGw.add(item.getProduct().getGw().multiply(new BigDecimal(num)));
				   }
					psiTransportOrder.setId(items.size()+1);
					psiTransportOrder.setTranAmount(new BigDecimal(totalGw.floatValue()*30/7).setScale(2, 4).floatValue());
					workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
				} else if ("CA".equals(getZone(psiTransportOrder.getToCountry()))) {
					modelName = "PI-CA";
					xmlName = "PI-CA";
					psiTransportOrder.setFormatDate("出口日期：" + date2);
					
					psiTransportOrder.setCinvoiceNo("INVOICE NO:" +psiTransportOrder.getTransportNo());
					psiTransportOrder.setPinvoiceNo("发票编号:" +psiTransportOrder.getTransportNo());
					List<PsiTransportOrderItem> items = psiTransportOrder.getItems();
					for (PsiTransportOrderItem item : items) {
					
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":(item.getProduct().getChineseName().split(";")==null?item.getProduct().getChineseName().split("；")[1]:item.getProduct().getChineseName().split(";")[1]));
						//item.setItemPrice(new BigDecimal(getCurrencyPrice(item.getCurrency(),item.getItemPrice())).setScale(2, 4).floatValue());
					//	Float tempPrice=priceMap.get(item.getProduct().getId());
					//	item.setItemPrice(tempPrice==null?1f:tempPrice*2);
					}
					workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
				} else if ("JP".equals(getZone(psiTransportOrder.getToCountry()))) {
					modelName = "PI-JP";
					psiTransportOrder.setRate1(new BigDecimal(1 / AmazonProduct2Service.getRateConfig().get("JPY/USD")).setScale(2,4).floatValue());

					psiTransportOrder.setFormatDate("出口日期：" + date2);
					psiTransportOrder.setCinvoiceNo("INVOICE NO:" +psiTransportOrder.getTransportNo());
					psiTransportOrder.setPinvoiceNo("发票编号:" + psiTransportOrder.getTransportNo());
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						List<PsiTransportOrderItem> items = psiTransportOrder.getItems();
						for (PsiTransportOrderItem item : items) {
							item.getProduct().setType(item.getProduct().getChineseName()==null?"":(item.getProduct().getChineseName().split(";")==null?item.getProduct().getChineseName().split("；")[1]:item.getProduct().getChineseName().split(";")[1]));
							//item.setItemPrice(new BigDecimal(getCurrencyPrice(item.getCurrency(),item.getItemPrice())).setScale(2, 4).floatValue());
						//	Float tempPrice=priceMap.get(item.getProduct().getId());
						//	item.setItemPrice(tempPrice==null?1f:tempPrice*2);
						}
						xmlName = "PI-JP-EXP";
						workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 1);
					} else {
						xmlName = "PI-JP-SEA-AIR";
						List<PsiTransportOrderItem> items = psiTransportOrder.getItems();
						for (PsiTransportOrderItem item : items) {
							
							item.getProduct().setType(item.getProduct().getChineseName()==null?"":(item.getProduct().getChineseName().split(";")==null?item.getProduct().getChineseName().split("；")[1]:item.getProduct().getChineseName().split(";")[1]));
							//item.setTempUsdPrice(new BigDecimal(getCurrencyPrice(item.getCurrency(), item.getItemPrice())).setScale(2, 4).floatValue());
						//	Float tempPrice=priceMap.get(item.getProduct().getId());
						//	item.setTempUsdPrice(tempPrice==null?1f:tempPrice*2);
							item.setTempUsdPrice(item.getItemPrice());
							item.setItemPrice(new BigDecimal(item.getTempUsdPrice()/ AmazonProduct2Service.getRateConfig().get("JPY/USD")).setScale(2, 4).floatValue());
							
							/*if ("CNY".equals(item.getCurrency())) {
								item.setItemPrice(new BigDecimal(item.getItemPrice()/ AmazonProduct2Service.getRateConfig().get("JPY/CNY")).setScale(2, 4).floatValue());
							} else if ("USD".equals(item.getCurrency())) {
								item.setItemPrice(new BigDecimal(item.getItemPrice()/ AmazonProduct2Service.getRateConfig().get("JPY/USD")).setScale(2, 4).floatValue());
							} else if ("EUR".equals(item.getCurrency())) {
								item.setItemPrice(new BigDecimal(item.getItemPrice()/ AmazonProduct2Service.getRateConfig().get("JPY/EUR")).setScale(2, 4).floatValue());
							} else if ("CAD".equals(item.getCurrency())) {
								item.setItemPrice(new BigDecimal(item.getItemPrice()/ AmazonProduct2Service.getRateConfig().get("JPY/CAD")).setScale(2, 4).floatValue());
							} else if ("GBP".equals(item.getCurrency())) {
								item.setItemPrice(new BigDecimal(item.getItemPrice()/ AmazonProduct2Service.getRateConfig().get("JPY/GBP")).setScale(2, 4).floatValue());
							} else {
								item.setItemPrice(new BigDecimal(item.getItemPrice()).setScale(2, 4).floatValue());
							}*/
						}
						workbook = ete.writeData(psiTransportOrder, xmlName,
								modelName, 0);
					}

				} else if ("US".equals(getZone(psiTransportOrder.getToCountry()))) {
					modelName = "PI-US";
					psiTransportOrder.setFormatDate("出口日期：" + date2);
					psiTransportOrder.setCinvoiceNo("INVOICE NO:" +psiTransportOrder.getTransportNo());
					psiTransportOrder.setPinvoiceNo("发票编号:" + psiTransportOrder.getTransportNo());
					List<PsiTransportOrderItem> items = psiTransportOrder.getItems();
					for (PsiTransportOrderItem item : items) {
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":(item.getProduct().getChineseName().split(";")==null?item.getProduct().getChineseName().split("；")[1]:item.getProduct().getChineseName().split(";")[1]));
						//item.setItemPrice(new BigDecimal(getCurrencyPrice(item.getCurrency(),item.getItemPrice())).setScale(2, 4).floatValue());
						//Float tempPrice=priceMap.get(item.getProduct().getId());
						//item.setItemPrice(tempPrice==null?1f:tempPrice*2);
					}
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						xmlName = "PI-US-EXP";
						workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 1);
					} else {
						xmlName = "PI-US-SEA-AIR";
						workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
					}

				}
				//下载excel文档
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
				String fileName = modelName + sdf.format(new Date()) + ".xlsx";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition","attachment;filename=" + fileName);
				try {
					OutputStream out = response.getOutputStream();
					workbook.write(out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{//从服务器下载
				String fileName= psiTransportOrder.getTransportNo()+"_PI"+psiTransportOrder.getSuffixName().split("-")[0];
				String path=ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport/"+psiTransportOrder.getTransportNo()+"/"+fileName;
				download(path,response);
			}
			return null;
		}

		@RequestMapping(value="expSI")
		public String exportSlTransportOrder(PsiTransportOrder psiTransportOrder,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
			psiTransportOrder=this.psiTransportOrderService.getById(psiTransportOrder.getId());
			ExportTransportExcel ete = new ExportTransportExcel();
			String date = new SimpleDateFormat("yyyy/MM/dd").format(psiTransportOrder.getCreateDate() == null ? new Date(): psiTransportOrder.getCreateDate());
			psiTransportOrder.setFormatDate(date);
			psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
			psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
			psiTransportOrder.setRate1(AmazonProduct2Service.getRateConfig().get("EUR/USD"));
			List<PsiTransportOrderItem> items =new ArrayList<PsiTransportOrderItem>();
			items.add(psiTransportOrder.getItems().get(0));
			psiTransportOrder.setItems(items);
			//下载excel文档
			Workbook workbook = null;
			String modelName = "SI-EU";//模板文件名称
			String xmlName = "";
			if ("1".equals(psiTransportOrder.getModel())) {//海运
				xmlName = "SI-EU-SEA";
				psiTransportOrder.setWeight(3225.81f);
				psiTransportOrder.setUnitPrice(3225.81f/psiTransportOrder.getRate1());
				workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 1);
			} else {
				xmlName = "SI-EU-EXP-AIR";
				psiTransportOrder.setWeight(new BigDecimal(psiTransportOrder.getWeight()*4.84).setScale(2,4).floatValue());
				workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
			}
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = modelName + sdf.format(new Date()) + ".xlsx";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition","attachment;filename=" + fileName);
			try {
				OutputStream out = response.getOutputStream();
				workbook.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}	
		
		//将运单信息导出为excel表格
			@RequestMapping(value="expPL")
			public String exportPlTransportOrder(PsiTransportOrder psiTransportOrder,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
				psiTransportOrder=this.psiTransportOrderService.getById(psiTransportOrder.getId());
				
				if (StringUtils.isBlank(psiTransportOrder.getSuffixName())||"PL".equals(psiTransportOrder.getSuffixName().split("-")[1])) {
					ExportTransportExcel ete = new ExportTransportExcel();
					String date = new SimpleDateFormat("dd,MMM,yyyy", Locale.US).format(psiTransportOrder.getCreateDate() == null ? new Date(): psiTransportOrder.getCreateDate());
					List<PsiTransportOrderItem> newItems = new ArrayList<PsiTransportOrderItem>();
					List<PsiTransportOrderItem> items1 = psiTransportOrder.getItems();
					Map<String, PsiTransportOrderItem> map = new HashMap<String, PsiTransportOrderItem>();
					for (PsiTransportOrderItem orderItem : items1) {//合并相同产品
						String pid=orderItem.getProduct().getBrand()+" "+orderItem.getProduct().getModel();
						PsiTransportOrderItem transItem = map.get(pid+"_"+orderItem.getPackQuantity());
						if (transItem != null) {//存在
							transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
						} else {
							map.put(pid+"_"+orderItem.getPackQuantity(), orderItem);
						}
					}
					List<PsiTransportOrderItem> mapValuesList = new ArrayList<PsiTransportOrderItem>(map.values());
					Collections.sort(mapValuesList);
					
					psiTransportOrder.setItems(mapValuesList);
					List<PsiTransportOrderItem> items = psiTransportOrder.getItems();
					int count = 1;
					for (int i = 0; i < items.size(); i++) {
						PsiTransportOrderItem item = items.get(i);
						int num = item.getQuantity()/ item.getPackQuantity();//多少箱
						if (num > 0	&& item.getQuantity()% item.getPackQuantity() == 0) {//整箱
							if (count != (count + num - 1)) {
								item.setCartonNo(count + "-"+ (count + num - 1));
							} else {
								item.setCartonNo(count + "");
							}
							count = count + num;
							//item.getProduct().setWeight((item.getProduct().getWeight() == null ? new BigDecimal(0) : item.getProduct().getWeight()).multiply(new BigDecimal(item.getQuantity())).divide(new BigDecimal(1000)));
							item.getProduct().setGw(item.getProduct().getGw().multiply(new BigDecimal(num)));
							item.getProduct().setWeight(item.getProduct().getGw().subtract(new BigDecimal(num)));
							item.getProduct().setType(item.getProduct().getChineseName()==null?"":(item.getProduct().getChineseName().split(";")==null?item.getProduct().getChineseName().split("；")[1]:item.getProduct().getChineseName().split(";")[1]));
							newItems.add(item);
						} else if (num > 0	&& item.getQuantity()% item.getPackQuantity() != 0) {
							int mod = item.getQuantity()% item.getPackQuantity();
							//整箱部分
							//item.setCartonNo(count+"-"+(count+num-1));
							if (count != (count + num - 1)) {
								item.setCartonNo(count + "-"+ (count + num - 1));
							} else {
								item.setCartonNo(count + "");
							}
							count = count + num;
							//item.getProduct().setWeight((item.getProduct().getWeight() == null ? new BigDecimal(0) : item.getProduct().getWeight()).multiply(new BigDecimal(item.getQuantity()- mod)).divide(new BigDecimal(1000)));
							item.getProduct().setGw(item.getProduct().getGw().multiply(new BigDecimal(num)));
							item.getProduct().setWeight(item.getProduct().getGw().subtract(new BigDecimal(num)));
							item.setQuantity(item.getQuantity() - mod);
							item.getProduct().setType(item.getProduct().getChineseName()==null?"":(item.getProduct().getChineseName().split(";")==null?item.getProduct().getChineseName().split("；")[1]:item.getProduct().getChineseName().split(";")[1]));
							newItems.add(item);
							//剩余部分
							PsiTransportOrderItem item1 = new PsiTransportOrderItem();
							item1.setProduct(new PsiProduct());
							item1.setCartonNo(count + "");
							item1.setProductName(item.getProductName());
							item1.getProduct().setType(item.getProduct().getChineseName()==null?"":(item.getProduct().getChineseName().split(";")==null?item.getProduct().getChineseName().split("；")[1]:item.getProduct().getChineseName().split(";")[1]));
							count = count + 1;
							item1.setQuantity(mod);
							//item1.getProduct().setWeight((item.getProduct().getWeight() == null ? new BigDecimal(0) : item.getProduct().getWeight()).multiply(new BigDecimal(mod)).divide(new BigDecimal(1000)));
							item1.getProduct().setBrand(item.getProduct().getBrand());
							item1.getProduct().setModel(item.getProduct().getModel());
							item1.getProduct().setPackLength(new BigDecimal(0));
							item1.getProduct().setPackWidth(new BigDecimal(0));
							item1.getProduct().setPackHeight(new BigDecimal(0));
							item1.getProduct().setGw(new BigDecimal(0));
							item1.getProduct().setWeight(new BigDecimal(0));
							newItems.add(item1);
						} else if (num <= 0) {
							//不足一箱
							item.setCartonNo(count + "");
							count = count + 1;
							item.getProduct().setWeight(new BigDecimal(0));
							//item.getProduct().setWeight((item.getProduct().getWeight() == null ? new BigDecimal(0) : item.getProduct().getWeight()).multiply(new BigDecimal(item.getQuantity())).divide(new BigDecimal(1000)));
							item.getProduct().setPackLength(new BigDecimal(0));
							item.getProduct().setPackWidth(new BigDecimal(0));
							item.getProduct().setPackHeight(new BigDecimal(0));
							item.getProduct().setGw(new BigDecimal(0));
							item.getProduct().setType(item.getProduct().getChineseName()==null?"":(item.getProduct().getChineseName().split(";")==null?item.getProduct().getChineseName().split("；")[1]:item.getProduct().getChineseName().split(";")[1]));
							newItems.add(item);
						}
					}
					psiTransportOrder.setItems(newItems);
					Workbook workbook = null;
					String modelName = "PL";//模板文件名称
					String xmlName = "";
					if ("CA".equals(getZone(psiTransportOrder.getToCountry()))) {
						modelName = "PL-CA";
						xmlName = "PL-CA";
						psiTransportOrder.setFormatDate(date);
					//	psiTransportOrder.setCinvoiceNo("CI" + date1 + "CA");
						psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
						workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
					} else if ("EU".equals(getZone(psiTransportOrder.getToCountry()))) {
						modelName = "PL-EU";
						xmlName = "PL-EU";
						psiTransportOrder.setFormatDate(date);
						//psiTransportOrder.setCinvoiceNo("CI" + date1 + "EU");
						psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
						workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);

					} else if ("JP".equals(getZone(psiTransportOrder.getToCountry()))) {
						modelName = "PL-JP";
						xmlName = "PL-JP";
						psiTransportOrder.setFormatDate(date);
						//psiTransportOrder.setCinvoiceNo("CI" + date1 + "JP");
						psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							xmlName = "PL-JP-EXP";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 1);
						} else {
							xmlName = "PL-JP-SEA-AIR";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);
						}

					} else if ("US".equals(getZone(psiTransportOrder.getToCountry()))) {
						modelName = "PL-US";
						xmlName = "PL-US";
						psiTransportOrder.setFormatDate(date);
						//psiTransportOrder.setCinvoiceNo("CI" + date1 + "US");
						psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							xmlName = "PL-US-EXP";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 1);
						} else {
							xmlName = "PL-US-SEA-AIR";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);
						}

					}
					//下载excel文档
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/x-download");
					SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
					String fileName = modelName + sdf.format(new Date())+ ".xlsx";
					fileName = URLEncoder.encode(fileName, "UTF-8");
					response.addHeader("Content-Disposition","attachment;filename=" + fileName);
					try {
						OutputStream out = response.getOutputStream();
						workbook.write(out);
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					String fileName= psiTransportOrder.getTransportNo()+"_PL"+psiTransportOrder.getSuffixName().split("-")[1];
					String path=ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport/"+psiTransportOrder.getTransportNo()+"/"+fileName;
					download(path,response);
				}
				return null;
			}
		
			
			@RequestMapping(value="upload")
			@ResponseBody
			public  String uploadFile(String psiTransportId,MultipartFile uploadFile,String uploadType,HttpServletResponse response,RedirectAttributes redirectAttributes) throws UnsupportedEncodingException{
				if(uploadFile.getSize()!=0){
					PsiTransportOrder psiTransportOrder=this.psiTransportOrderService.get(Integer.parseInt(psiTransportId));
					String psiTransportNo=psiTransportOrder.getTransportNo();
					
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport/"+psiTransportNo;
					File baseDir = new File(baseDirStr); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					
					String name = uploadFile.getOriginalFilename();
					String suffix = uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf(".")); 
					String suffixName="";
					String[] suffixArr=StringUtils.isBlank(psiTransportOrder.getSuffixName())?"PI-PL-WB-TI".split("-"):psiTransportOrder.getSuffixName().split("-");
					String suffixStr=StringUtils.isBlank(psiTransportOrder.getSuffixName())?"PI-PL-WB-TI":psiTransportOrder.getSuffixName();
					List<Integer> index=new ArrayList<Integer>();
					for(int i=0;i<suffixArr.length;i++){
					    if(i==0){
					    	index.add(suffixArr[0].length());
					    }else{//取到分隔符位置
					    	index.add(index.get(i-1)+suffixArr[i].length()+1);
					    }
					}
					if("1".equals(uploadType)){//PI 3
						name = psiTransportNo+"_PI"+suffix;
					//	suffixName=suffix+"-"+suffixArr[1]+"-"+suffixArr[2]+"-"+suffixArr[3];
						suffixName= suffix+"-"+(suffixStr.substring(index.get(0)+1)==null?"":suffixStr.substring(index.get(0)+1));
					}else if("2".equals(uploadType)){//PL
						name=psiTransportNo+"_PL"+suffix;
					   // suffixName=suffixArr[0]+"-"+suffix+"-"+suffixArr[2]+"-"+suffixArr[3];
						suffixName=suffixStr.substring(0,index.get(0))+"-"+suffix+"-"+(suffixStr.substring(index.get(1)+1)==null?"":suffixStr.substring(index.get(1)+1));
					}else if("0".equals(uploadType)){//运单 0 WB
						name=psiTransportNo+"_WB"+suffix;
					   // suffixName=suffixArr[0]+"-"+suffixArr[1]+"-"+suffix+"-"+suffixArr[3];
						suffixName=suffixStr.substring(0,index.get(1))+"-"+suffix+"-"+(suffixStr.substring(index.get(2)+1)==null?"":suffixStr.substring(index.get(2)+1));
					}else if("3".equals(uploadType)){//运输发票 TI
						name=psiTransportNo+"_TI"+suffix;
						   // suffixName=suffixArr[0]+"-"+suffixArr[1]+"-"+suffixArr[2]+"-"+suffix;
						if(index.get(3)+1<suffixStr.length()){
							suffixName=suffixStr.substring(0,index.get(2))+"-"+suffix+"-"+(suffixStr.substring(index.get(3)+1)==null?"":suffixStr.substring(index.get(3)+1));
						}else{
							suffixName=suffixStr.substring(0,index.get(2))+"-"+suffix;
						}
					}else if("13".equals(uploadType)){//other
						baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport/"+psiTransportNo+"/other";
						baseDir = new File(baseDirStr); 
						if(!baseDir.isDirectory())
							baseDir.mkdirs();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
						name = uploadFile.getOriginalFilename().substring(0,uploadFile.getOriginalFilename().lastIndexOf("."))+sdf.format(new Date())+ suffix;
					}else{
						if("4".equals(uploadType)){//SO
							name=psiTransportNo+"_SO"+suffix;
							//suffixStr=StringUtils.isBlank(psiTransportOrder.getSuffixName())?"PI-PL-WB-TI-SO":psiTransportOrder.getSuffixName();
							//suffixArr=StringUtils.isBlank(psiTransportOrder.getSuffixName())?"PI-PL-WB-TI-SO".split("-"):psiTransportOrder.getSuffixName().split("-");
							suffixStr=getSuffixStr(psiTransportOrder.getSuffixName());
							suffixArr=getSuffixArr(psiTransportOrder.getSuffixName());
						}else if("5".equals(uploadType)){//入仓核实单
							name=psiTransportNo+"_WV"+suffix;
							suffixStr=getSuffixStr(psiTransportOrder.getSuffixName());
							suffixArr=getSuffixArr(psiTransportOrder.getSuffixName());
						}else if("6".equals(uploadType)){//查验单
							name=psiTransportNo+"_IS"+suffix;
							suffixStr=getSuffixStr(psiTransportOrder.getSuffixName());
							suffixArr=getSuffixArr(psiTransportOrder.getSuffixName());
						}else if("7".equals(uploadType)){//报关单
							name=psiTransportNo+"_CD"+suffix;
							suffixStr=getSuffixStr(psiTransportOrder.getSuffixName());
							suffixArr=getSuffixArr(psiTransportOrder.getSuffixName());
						}else if("8".equals(uploadType)){//销售合同
							name=psiTransportNo+"_CS"+suffix;
							suffixStr=getSuffixStr(psiTransportOrder.getSuffixName());
							suffixArr=getSuffixArr(psiTransportOrder.getSuffixName());
						}else if("9".equals(uploadType)){//保险单
							name=psiTransportNo+"_SP"+suffix;
							suffixStr=getSuffixStr(psiTransportOrder.getSuffixName());
							suffixArr=getSuffixArr(psiTransportOrder.getSuffixName());
						}else if("10".equals(uploadType)){//电放单
							name=psiTransportNo+"_RS"+suffix;
							suffixStr=getSuffixStr(psiTransportOrder.getSuffixName());
							suffixArr=getSuffixArr(psiTransportOrder.getSuffixName());
						}else if("11".equals(uploadType)){//到货通知
							name=psiTransportNo+"_AN"+suffix;
							suffixStr=getSuffixStr(psiTransportOrder.getSuffixName());
							suffixArr=getSuffixArr(psiTransportOrder.getSuffixName());
						}else if("12".equals(uploadType)){//进口税单
							name=psiTransportNo+"_IB"+suffix;
							suffixStr=getSuffixStr(psiTransportOrder.getSuffixName());
							suffixArr=getSuffixArr(psiTransportOrder.getSuffixName());
						}
						List<Integer> temp=new ArrayList<Integer>();
						for(int i=0;i<suffixArr.length;i++){
						    if(i==0){
						    	temp.add(suffixArr[0].length());
						    }else{//取到分隔符位置
						    	temp.add(temp.get(i-1)+suffixArr[i].length()+1);
						    }
						}
						if("4".equals(uploadType)){//SO
							if(temp.size()>4&&temp.get(4)+1<suffixStr.length()){
								suffixName=suffixStr.substring(0,temp.get(3))+"-"+suffix+"-"+(suffixStr.substring(temp.get(4)+1)==null?"":suffixStr.substring(temp.get(4)+1));
							}else{
								suffixName=suffixStr.substring(0,temp.get(3))+"-"+suffix;
							}
						}else if("5".equals(uploadType)){
							if(temp.size()>5&&temp.get(5)+1<suffixStr.length()){
								suffixName=suffixStr.substring(0,temp.get(4))+"-"+suffix+"-"+(suffixStr.substring(temp.get(5)+1)==null?"":suffixStr.substring(temp.get(5)+1));
							}else{
								suffixName=suffixStr.substring(0,temp.get(4))+"-"+suffix;
							}
						}else if("6".equals(uploadType)){
							if(temp.size()>6&&temp.get(6)+1<suffixStr.length()){
								suffixName=suffixStr.substring(0,temp.get(5))+"-"+suffix+"-"+(suffixStr.substring(temp.get(6)+1)==null?"":suffixStr.substring(temp.get(6)+1));
							}else{
								suffixName=suffixStr.substring(0,temp.get(5))+"-"+suffix;
							}
						}else if("7".equals(uploadType)){
							if(temp.size()>7&&temp.get(7)+1<suffixStr.length()){
								suffixName=suffixStr.substring(0,temp.get(6))+"-"+suffix+"-"+(suffixStr.substring(temp.get(7)+1)==null?"":suffixStr.substring(temp.get(7)+1));
							}else{
								suffixName=suffixStr.substring(0,temp.get(6))+"-"+suffix;
							}
						}else if("8".equals(uploadType)){
							if(temp.size()>8&&temp.get(8)+1<suffixStr.length()){
								suffixName=suffixStr.substring(0,temp.get(7))+"-"+suffix+"-"+(suffixStr.substring(temp.get(8)+1)==null?"":suffixStr.substring(temp.get(8)+1));
							}else{
								suffixName=suffixStr.substring(0,temp.get(7))+"-"+suffix;
							}
						}else if("9".equals(uploadType)){
							if(temp.size()>9&&temp.get(9)+1<suffixStr.length()){
								suffixName=suffixStr.substring(0,temp.get(8))+"-"+suffix+"-"+(suffixStr.substring(temp.get(9)+1)==null?"":suffixStr.substring(temp.get(9)+1));
							}else{
								suffixName=suffixStr.substring(0,temp.get(8))+"-"+suffix;
							}
						}else if("10".equals(uploadType)){
							if(temp.size()>10&&temp.get(10)+1<suffixStr.length()){
								suffixName=suffixStr.substring(0,temp.get(9))+"-"+suffix+"-"+(suffixStr.substring(temp.get(10)+1)==null?"":suffixStr.substring(temp.get(10)+1));
							}else{
								suffixName=suffixStr.substring(0,temp.get(9))+"-"+suffix;
							}
						}else if("11".equals(uploadType)){
							if(temp.size()>11&&temp.get(11)+1<suffixStr.length()){
								suffixName=suffixStr.substring(0,temp.get(10))+"-"+suffix+"-"+(suffixStr.substring(temp.get(11)+1)==null?"":suffixStr.substring(temp.get(11)+1));
							}else{
								suffixName=suffixStr.substring(0,temp.get(10))+"-"+suffix;
							}
						}else if("12".equals(uploadType)){
							if(temp.size()>12&&temp.get(12)+1<suffixStr.length()){
								suffixName=suffixStr.substring(0,temp.get(11))+"-"+suffix+"-"+(suffixStr.substring(temp.get(12)+1)==null?"":suffixStr.substring(temp.get(12)+1));
							}else{
								suffixName=suffixStr.substring(0,temp.get(11))+"-"+suffix;
							}
						}
					}
					
					File dest = new File(baseDir,name);
					if(dest.exists()){
						dest.delete();
					}
					try {
						FileUtils.copyInputStreamToFile(uploadFile.getInputStream(),dest);
						if("13".equals(uploadType)){
							String elsePath=psiTransportOrder.getElsePath();
							if(StringUtils.isBlank(elsePath)){
								this.psiTransportOrderService.updateElsePath(Integer.parseInt(psiTransportId),name);
							}else{
								this.psiTransportOrderService.updateElsePath(Integer.parseInt(psiTransportId),elsePath+","+name);
							}
						}else{
							this.psiTransportOrderService.updateSuffixName(Integer.parseInt(psiTransportId), suffixName);
						}
						addMessage(redirectAttributes, "文件上传成功");
						return "0";
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
						addMessage(redirectAttributes, "文件上传失败");
						return "1";
					}
					
				}else{
					addMessage(redirectAttributes, "上传文件名为空");
					return "2";
				}
				//return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
			}
			
			private String getSuffixStr(String suffixName){
				String suffixStr="";
				if(StringUtils.isBlank(suffixName)){
					suffixStr="PI-PL-WB-TI-SO-WV-IS-CD-CS-SP-RS-AN-IB";
				}else{
					int len=suffixName.split("-").length;
					if(len==4){
						suffixStr=suffixName+"-SO-WV-IS-CD-CS-SP-RS-AN-IB";
					}else if(len==5){
						suffixStr=suffixName+"-WV-IS-CD-CS-SP-RS-AN-IB";
					}else if(len==6){
						suffixStr=suffixName+"-IS-CD-CS-SP-RS-AN-IB";
					}else if(len==7){
						suffixStr=suffixName+"-CD-CS-SP-RS-AN-IB";
					}else if(len==8){
						suffixStr=suffixName+"-CS-SP-RS-AN-IB";
					}else if(len==9){
						suffixStr=suffixName+"-SP-RS-AN-IB";
					}else if(len==10){
						suffixStr=suffixName+"-RS-AN-IB";
					}else if(len==11){
						suffixStr=suffixName+"-AN-IB";
					}else if(len==12){
						suffixStr=suffixName+"-IB";
					}else{
						suffixStr=suffixName;
					}
				}
				return suffixStr;
			}
			private String[] getSuffixArr(String suffixName){
				String[] suffixArr=null;
				if(StringUtils.isBlank(suffixName)){
					suffixArr="PI-PL-WB-TI-SO-WV-IS-CD-CS-SP-RS-AN-IB".split("-");
				}else{
					int len=suffixName.split("-").length;
					if(len==4){
						suffixArr=(suffixName+"-SO-WV-IS-CD-CS-SP-RS-AN-IB").split("-");
					}else if(len==5){
						suffixArr=(suffixName+"-WV-IS-CD-CS-SP-RS-AN-IB").split("-");
					}else if(len==6){
						suffixArr=(suffixName+"-IS-CD-CS-SP-RS-AN-IB").split("-");
					}else if(len==7){
						suffixArr=(suffixName+"-CD-CS-SP-RS-AN-IB").split("-");
					}else if(len==8){
						suffixArr=(suffixName+"-CS-SP-RS-AN-IB").split("-");
					}else if(len==9){
						suffixArr=(suffixName+"-SP-RS-AN-IB").split("-");
					}else if(len==10){
						suffixArr=(suffixName+"-RS-AN-IB").split("-");
					}else if(len==11){
						suffixArr=(suffixName+"-AN-IB").split("-");
					}else if(len==12){
						suffixArr=(suffixName+"-IB").split("-");
					}else{
						suffixArr=suffixName.split("-");
					}
				}
				return suffixArr;
			}
	    private void download(String path, HttpServletResponse response) {  
	        try {  
	            // path是指欲下载的文件的路径。  
	            File file = new File(path);  
	            // 取得文件名。  
	            String filename = file.getName();  
	            // 以流的形式下载文件。  
	            InputStream fis = new BufferedInputStream(new FileInputStream(path));  
	            byte[] buffer = new byte[fis.available()];  
	            fis.read(buffer);  
	            fis.close();  
	            // 清空response  
	            response.reset();  
	            // 设置response的Header  
	            response.addHeader("Content-Disposition", "attachment;filename="  
	                    + new String(filename.getBytes()));  
	            response.addHeader("Content-Length", "" + file.length());  
	            OutputStream toClient = new BufferedOutputStream(  
	                    response.getOutputStream()); 
	            
	            String suffix=path.substring(path.lastIndexOf(".")+1);
	            if("xlsx".equals(suffix)||"xls".equals(suffix)){
	            	 response.setContentType("application/vnd.ms-excel;charset=utf-8");  
	            }else if("pdf".equals(suffix)){
	            	 response.setContentType("application/pdf;charset=utf-8");  
	            }else if("doc".equals(suffix)||"docx".equals(suffix)){
	            	 response.setContentType("application/msword;charset=utf-8");  
	            }else if("jpeg".equals(suffix)){
	            	 response.setContentType("image/jpeg;charset=utf-8");  
	            }
	           
	            toClient.write(buffer);  
	            toClient.flush();  
	            toClient.close();  
	        } catch (IOException ex) {  
	            ex.printStackTrace();  
	        }  
	  }  
	
	private Map<String,String> getOtherProductType(){
		Map<String,String> map=new HashMap<String,String>();
		map.put("Inateck BR1001", "Receiver");
		map.put("Inateck WP1002", "Wireless presenter");
		map.put("Inateck WP1001", "Wireless presenter");
		return map;
	}
	
	private Float getCurrencyPrice(String currency,Float price){//转化成美元
		Float itemPrice=0.00f;
		if("CNY".equals(currency)){
			itemPrice=price/AmazonProduct2Service.getRateConfig().get("USD/CNY");
		}else if("JPY".equals(currency)){
			itemPrice=price*AmazonProduct2Service.getRateConfig().get("JPY/USD");
		}else if("EUR".equals(currency)){
			itemPrice=price*AmazonProduct2Service.getRateConfig().get("EUR/USD");
		}else if("CAD".equals(currency)){
			itemPrice=price*AmazonProduct2Service.getRateConfig().get("CAD/USD");
		}else if("GBP".equals(currency)){
			itemPrice=price*AmazonProduct2Service.getRateConfig().get("GBP/USD");
		}else{
			itemPrice=price;
		}
		return itemPrice;
	}
			
	private String getZone(String countryCode){
		String zone="";
		if(countryCode.equals("com")||countryCode.equals("US")){
			zone="US";
		}else if(countryCode.equals("DE")||countryCode.equals("de")||countryCode.equals("uk")||countryCode.equals("fr")||countryCode.equals("it")||countryCode.equals("es")){
			zone="EU";
		}else if(countryCode.equals("ca")){
			zone="CA";
		}else if(countryCode.equals("jp")){
			zone="JP";
		}else if(countryCode.equals("mx")){
			zone="MX";
		}
		
		return zone;
	}
	
	
	private String getMarket(String countryCode){
		String zone="";
		if(countryCode.equals("com")||countryCode.equals("US")){
			zone="US";
		}else if(countryCode.equals("DE")||countryCode.equals("de")||countryCode.equals("uk")||countryCode.equals("fr")||countryCode.equals("it")||countryCode.equals("es")){
			zone="EU";
		}else if(countryCode.equals("jp")){
			zone="JP";
		}else if(countryCode.equals("ca")){
			zone="CA";
		}else if(countryCode.equals("mx")){
			zone="MX";
		}else{
			zone="EU";
		}
		return zone;
	}
	
	
	private String getCurrencySymbol(String currency){
		String symbol ="";
		if("CNY".equals(currency)){
			symbol="￥";
		}else if("USD".equals(currency)){
			symbol="$";
		}else if("EUR".equals(currency)){
			symbol="€";
		}else if("GBP".equals(currency)){
			symbol="£";
		}else if("CAD".equals(currency)){
			symbol="C$";
		}else if("JPY".equals(currency)){
			symbol="￥";
		}
		
		return symbol;
	}
	

	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/psi/psiTransport";  
        String downLoadPath = ctxPath + fileName;
        fileName = fileName.substring(fileName.lastIndexOf("/")+1);
        try {   
            long fileLength = new File(downLoadPath).length();   
            response.setContentType("application/x-msdownload;");   
            response.setHeader("Content-disposition", "attachment; filename="  
                    + URLEncoder.encode(fileName,"utf-8"));   
            response.setHeader("Content-Length", String.valueOf(fileLength));   
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));   
            bos = new BufferedOutputStream(response.getOutputStream());   
            byte[] buff = new byte[2048];   
            int bytesRead;   
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {   
                bos.write(buff, 0, bytesRead);   
            }   
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            if (bis != null)   
                bis.close();   
            if (bos != null)   
                bos.close();   
        }   
        return null;   
    }
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value =  "expTotal" )
	public String expTotal(PsiTransportOrder psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		    if(psiTransportOrder.getEtdDate()==null){
		    	psiTransportOrder.setEtdDate(new Date());
		    }
		    Map<String,Map<String,Integer>> map=psiTransportOrderService.findTotalTranQuantity(psiTransportOrder);
			Map<String,Map<String,Object>> priceMap=psiProductTieredPriceService.getMoqPriceBaseMoqNoSupplier();
		    HSSFWorkbook wb = new HSSFWorkbook();
			
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			HSSFCellStyle cellStyle= wb.createCellStyle();
			cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		
			
			List<String> title =Lists.newArrayList("产品","数量");
			if(SecurityUtils.getSubject().isPermitted("psi:product:viewPrice")){
				title.add("价格($)");
			}
			style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 11); // 字体高度
			style.setFont(font);
			
			List<String> countryList=Lists.newArrayList("EU","US","JP");
			for(String country:countryList){
				  HSSFSheet sheet= wb.createSheet(country);
				  HSSFRow row = sheet.createRow(0);
				  row.setHeight((short) 400);
				  HSSFCell cell = null;		
				  for (int i = 0; i < title.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(title.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
				  }
				  int excelNo =1;
				  Map<String,Integer> temp=map.get(country);
				  if(temp!=null&&temp.size()>0){
					  for (Map.Entry<String,Integer> entry: temp.entrySet()) {
						  String name = entry.getKey();
						  row = sheet.createRow(excelNo++);
						  int j =0;
						  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name); 
						  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(entry.getValue()); 
						  if(SecurityUtils.getSubject().isPermitted("psi:product:viewPrice")){
								if(priceMap!=null&&priceMap.get(name)!=null&&priceMap.get(name).get("price")!=null){
									  row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(((BigDecimal)priceMap.get(name).get("price")).floatValue()*2); 
									  row.getCell(j-1).setCellStyle(cellStyle);
								}else{
									  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
								}
						  }
					  }	  
				  }
				  for (int k = 0; k <title.size(); k++) {
			       		 sheet.autoSizeColumn((short)k, true);
				  }
			}
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
	
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	
		String fileName = "total" + sdf.format(new Date()) + ".xls";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
		try {
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@RequestMapping(value="downloadZipFile")
	public String downloadZipFile(String type,String month,String toCountry,String model,HttpServletResponse response) throws Exception {      
		Set<String> countrySet=Sets.newHashSet();
		if(StringUtils.isNotBlank(toCountry)){
			if("eu".equals(toCountry)){
				countrySet.add("DE");
				countrySet.add("de");
			}else if("com".equals(toCountry)){
				countrySet.add("US");
				countrySet.add("ca");
				countrySet.add("com");
			}else{
				countrySet.add("jp");
			}
		}
		
		List<LcPsiTransportOrder> orderList=psiTransportOrderService.findOrderFileByMonth(month,countrySet,model);
		if(orderList!=null&&orderList.size()>0){
			String baseDirStr= ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/tempPsiTransport";
			//PI-PL-WB-TI-SO-WV-IS-CD-CS-SP-RS-AN-IB
			String path=ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport/";
			String lcPath=ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport/";
			List<File> files = new ArrayList<File>();
			if("WB".equals(type)){
				for (LcPsiTransportOrder order : orderList) {
                     if (StringUtils.isNotBlank(order.getSuffixName())&&order.getSuffixName().split("-").length>=4&&!"WB".equals(order.getSuffixName().split("-")[2])) {
                    	 String fileName= order.getTransportNo()+"_WB"+order.getSuffixName().split("-")[2];
                    	 String tempPath=path+order.getTransportNo()+"/"+fileName;
                    	 files.add(new File(tempPath));
					}
				}
				
			}else if("PI".equals(type)){
				for (LcPsiTransportOrder order : orderList) {
                    if (StringUtils.isNotBlank(order.getSuffixName())&&order.getSuffixName().split("-").length>=2&&!"PI".equals(order.getSuffixName().split("-")[0])) {
                   	 String fileName= order.getTransportNo()+"_PI"+order.getSuffixName().split("-")[0];
                   	 String tempPath=path+order.getTransportNo()+"/"+fileName;
                   	 files.add(new File(tempPath));
					}
				}
			}else if("PL".equals(type)){
				for (LcPsiTransportOrder order : orderList) {
                    if (StringUtils.isNotBlank(order.getSuffixName())&&order.getSuffixName().split("-").length>=2&&!"PL".equals(order.getSuffixName().split("-")[1])) {
                   	 String fileName= order.getTransportNo()+"_PL"+order.getSuffixName().split("-")[1];
                   	 String tempPath=path+order.getTransportNo()+"/"+fileName;
                   	 files.add(new File(tempPath));
					}
				}
			}else if("IB".equals(type)){
				for (LcPsiTransportOrder order : orderList) {
                    if (StringUtils.isNotBlank(order.getSuffixName())&&order.getSuffixName().split("-").length>=13&&!"IB".equals(order.getSuffixName().split("-")[12])) {
                   	 String fileName= order.getTransportNo()+"_IB"+order.getSuffixName().split("-")[12];
                   	 String tempPath=path+order.getTransportNo()+"/"+fileName;
                   	 files.add(new File(tempPath));
					}
				}
			}else if("CD".equals(type)){
				for (LcPsiTransportOrder order : orderList) {
                    if (StringUtils.isNotBlank(order.getSuffixName())&&order.getSuffixName().split("-").length>=13&&!"CD".equals(order.getSuffixName().split("-")[7])) {
                   	 String fileName= order.getTransportNo()+"_CD"+order.getSuffixName().split("-")[7];
                   	 String tempPath=path+order.getTransportNo()+"/"+fileName;
                   	 files.add(new File(tempPath));
					}
				}
			}else if("CS".equals(type)){
				for (LcPsiTransportOrder order : orderList) {
                    if (StringUtils.isNotBlank(order.getSuffixName())&&order.getSuffixName().split("-").length>=13&&!"CS".equals(order.getSuffixName().split("-")[8])) {
                   	 String fileName= order.getTransportNo()+"_CS"+order.getSuffixName().split("-")[8];
                   	 String tempPath=path+order.getTransportNo()+"/"+fileName;
                   	 files.add(new File(tempPath));
					}
				}
			}else if("tranPath".equals(type)){
				for (LcPsiTransportOrder order : orderList) {
					String tranPaths=order.getTranPath();
                    if (StringUtils.isNotBlank(tranPaths)) {
                    	 String[] pathArr=tranPaths.split(",");
                    	 for (String arr : pathArr) {
    	                   	 String tempPath=lcPath+"/"+arr;
    	                   	 files.add(new File(tempPath));
						 }
	                   	
					}
				}
			}else if("dapPath".equals(type)){
				for (LcPsiTransportOrder order : orderList) {
					String tranPaths=order.getDapPath();
                    if (StringUtils.isNotBlank(tranPaths)) {
                    	 String[] pathArr=tranPaths.split(",");
                    	 for (String arr : pathArr) {
    	                   	 String tempPath=lcPath+"/"+arr;
    	                   	 files.add(new File(tempPath));
						 }
	                   	
					}
				}
			}else if("taxPath".equals(type)){
				for (LcPsiTransportOrder order : orderList) {
					String tranPaths=order.getTaxPath();
                    if (StringUtils.isNotBlank(tranPaths)) {
                    	 String[] pathArr=tranPaths.split(",");
                    	 for (String arr : pathArr) {
    	                   	 String tempPath=lcPath+"/"+arr;
    	                   	 files.add(new File(tempPath));
						 }
	                   	
					}
				}
			}else if("exportInvoicePath".equals(type)){//fileName=/${psiTransportOrder.transportNo}/export/${psiTransportOrder.exportInvoicePath}
				for (LcPsiTransportOrder order : orderList) {
					//ctxPath + fileName
                    if (StringUtils.isNotBlank(order.getExportInvoicePath())) {
                      	 String tempPath=path+order.getTransportNo()+"/export/"+order.getExportInvoicePath();
                      	 files.add(new File(tempPath));
					}
				}
			}
      
			if(files!=null&&files.size()>0){
		    	
		    	String fileName=baseDirStr+"/"+month+"_"+type;
				if(StringUtils.isNotBlank(model)){
					if("0".equals(type)){
						fileName+="_Air";
					}else if("1".equals(type)){
						fileName+="_Sea";
					}else if("2".equals(type)){
						fileName+="_Express";
					}
				}
				if(StringUtils.isNotBlank(toCountry)){
					fileName+="_"+toCountry;
				}
		    	File zipFile = new File (fileName+".zip");
		    	if(!zipFile.exists()){
		    	    //先得到文件的上级目录，并创建上级目录，在创建文件
		    		zipFile.getParentFile().mkdir();
		    	    try {
		    	        //创建文件
		    	    	zipFile.createNewFile();
		    	    } catch (IOException e) {
		    	        e.printStackTrace();
		    	    }
		    	}
		    	FileOutputStream fous = new FileOutputStream(zipFile); 
		    	ZipOutputStream zipOut = new ZipOutputStream(fous);
		    	ZipUtil.zipFile(files, zipOut);
		    	zipOut.close();
		    	fous.close();
		    	downloadZip(zipFile,response);
			}
		}
		return null;
	}
	
	
	public static HttpServletResponse downloadZip(File file,HttpServletResponse response) {
        try {
        // 以流的形式下载文件。
        InputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        // 清空response
        response.reset();

        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/octet-stream");

       //如果输出的是中文名的文件，在此处就要用URLEncoder.encode方法进行处理
        response.setHeader("Content-Disposition", "attachment;filename=" +URLEncoder.encode(file.getName(), "UTF-8"));
        toClient.write(buffer);
        toClient.flush();
        toClient.close();
        } catch (IOException ex) {
        ex.printStackTrace();
        }finally{
             try {
                    File f = new File(file.getPath());
                    f.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return response;
    }
	
}
