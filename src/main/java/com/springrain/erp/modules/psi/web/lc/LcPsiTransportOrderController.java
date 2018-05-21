/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.lc;

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
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
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
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.NumberUtil;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.ZipUtil;
import com.springrain.erp.common.utils.excel.ExportTransportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonUnlineOrderService;
import com.springrain.erp.modules.psi.entity.FbaInbound;
import com.springrain.erp.modules.psi.entity.FbaInboundItem;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductDto;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PsiTransportRevise;
import com.springrain.erp.modules.psi.entity.PsiVatInvoiceInfo;
import com.springrain.erp.modules.psi.entity.PsiVatInvoiceUseInfo;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderContainer;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderItem;
import com.springrain.erp.modules.psi.service.FbaInboundService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PsiVatInvoiceInfoService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderItemService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportPaymentItemService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderService;
import com.springrain.erp.modules.sys.utils.UserUtils;
/**
 * 运单表Controller
 * @author Michael
 * @version 2015-01-15
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/lcPsiTransportOrder")
public class LcPsiTransportOrderController extends BaseController{
	
	
	@Autowired
	private LcPsiTransportOrderService 		psiTransportOrderService;
	@Autowired
	private LcPsiTransportOrderItemService 	psiTransportOrderItemService;
	@Autowired
	private PsiSupplierService 				psiSupplierService;
	@Autowired
	private PsiProductService 				productService;
	@Autowired
	private StockService 					stockService;
	@Autowired
	private LcPsiTransportPaymentItemService  psiTransportPaymentItemService;
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
	private LcPurchaseOrderService		    purchaseOrderService;
	@Autowired
	private PsiProductEliminateService productEliminateService;
	@Autowired
	private PsiVatInvoiceInfoService psiVatInvoiceInfoService;
	@Autowired
	private SaleReportService saleReportService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	private static String filePath;
	
	@RequiresPermissions("psi:transport:view")
	@RequestMapping(value = {"list", ""})
	public String list(LcPsiTransportOrder psiTransportOrder, HttpServletRequest request, HttpServletResponse response, Model model) {
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
		
		Page<LcPsiTransportOrder> page = new Page<LcPsiTransportOrder>(request, response);
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
		return "modules/psi/lc/lcPsiTransportOrderList";
	}
	

	@RequiresPermissions("psi:transport:view")
	@RequestMapping(value = {"singleTran"})
	public String singleTran(String productName,String tranModel,String tranType,String toCountry,Date startDate,Date endDate,Integer fromStoreId, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		Map<String,String> proColorMap =this.purchaseOrderService.getAllProductColors(); 
		model.addAttribute("productColors", proColorMap.keySet());
		
		if(StringUtils.isEmpty(productName)){
			return "modules/psi/lc/lcTransportSingleProductList";
		}
		
		if(startDate==null){
			endDate=sdf.parse(sdf.format(new Date()));
			startDate=DateUtils.addMonths(endDate, -3);
		}
		
		List<Object[]> list=this.psiTransportOrderService.getSingleTran(productName,tranModel,toCountry,fromStoreId,tranType,startDate,endDate);
		//组装神奇的map: 产品 、月份（201601）、区域（EU,JP,US）、运输方式（AE、OE、EX）
		Set<String> set = Sets.newHashSet();
		Map<String,Map<String,Map<String,Map<String,Integer>>>> tranMap =Maps.newLinkedHashMap();
		for(Object[] obj:list){
			String proColorName=obj[12].toString();
			set.add(proColorName);
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
			}else if("com".equals(obj[2].toString())||"US".equals(obj[2].toString())||"ca".equals(obj[2].toString())||"mx".equals(obj[2].toString())||obj[2].toString().startsWith("com")){
				zone="US";
			}
			
			Map<String,Map<String,Map<String,Integer>>>  proMap = null;
			if(tranMap.get(proColorName)==null){
				proMap = Maps.newHashMap();
			}else{
				proMap = tranMap.get(proColorName);
			}
			
			Map<String,Map<String,Integer>> zoneMap = null; 
			if(proMap.get(month)==null){
				zoneMap =Maps.newHashMap();
			}else{
				zoneMap = proMap.get(month);
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
			proMap.put(month, zoneMap);
			tranMap.put(proColorName, proMap);
		}
		Map<String,String> proInfos = Maps.newHashMap();
		if(set!=null&&set.size()>0){
			proInfos=this.productService.getProductVoGw(set);	
		}
		
		model.addAttribute("proInfos", proInfos);
        model.addAttribute("list", list);
        model.addAttribute("productName", productName);
        model.addAttribute("tranModel", tranModel);
        model.addAttribute("toCountry", toCountry);
        model.addAttribute("fromStoreId", fromStoreId);
        model.addAttribute("tranType", tranType);
        model.addAttribute("tranMap", tranMap);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
		return "modules/psi/lc/lcTransportSingleProductList";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value =  "expTransport" )
	public String expTransport(LcPsiTransportOrder psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
			SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyyMMdd");
			int excelNo =1;
			
		    List<LcPsiTransportOrder> list=this.psiTransportOrderService.exp(psiTransportOrder); 
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
			 for(LcPsiTransportOrder transportOrder: list){
				 int i =0;
				 row = sheet.createRow(excelNo++);
//				Calendar cal =Calendar.getInstance();
//				cal.setTime(sdf1.parse(transportOrder.getTransportNo().substring(0, 8)));
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
	public String expNew(LcPsiTransportOrder psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		Map<String,Map<String,Map<String,Float>>> monthMap = Maps.newTreeMap();
		Map<String,List<LcPsiTransportOrder>> detailMap = Maps.newTreeMap();
		Map<String,BigDecimal> monthBaseMap = Maps.newHashMap();
			SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			int excelNo =1;
			int excelNo1 =1;
		    List<LcPsiTransportOrder> list=this.psiTransportOrderService.expNew(psiTransportOrder); 
		    if(list.size()>65535){
		    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
		    }
		    
		    
		    for(LcPsiTransportOrder transportOrder: list){
		    	// String month =DateUtils.getShortMonth(Integer.parseInt(transportOrder.getTransportNo().substring(4, 6)));
		    	 //获取月份
				 String shortMonth = "";
				 if(transportOrder.getPickUpDate()!=null){
					 shortMonth=sdf1.format(transportOrder.getPickUpDate()).substring(5,7);
				 }else{
					 shortMonth=transportOrder.getTransportNo().substring(4,6);
				 }
				 
				 String month =DateUtils.getShortMonth(Integer.parseInt(shortMonth));
		    	 List<LcPsiTransportOrder> orders = null;
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
				
			 for(Map.Entry<String, List<LcPsiTransportOrder>> entry: detailMap.entrySet()){
				 String month = entry.getKey();
				  BigDecimal totalWeightOut = new BigDecimal(0);
				  BigDecimal newWeightOut = new BigDecimal(0);
				 for(LcPsiTransportOrder transportOrder:entry.getValue()){
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
					
					for(LcPsiTransportOrderItem item:transportOrder.getItems()){
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
					if(totalWeight.compareTo(BigDecimal.ZERO)==0){
						row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("0");
					}else{
						row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(df.format(newWeight.divide(totalWeight,4,RoundingMode.HALF_UP).floatValue()*100)+"%");
					}
					
				
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
				 if(totalWeightOut.compareTo(BigDecimal.ZERO)==0){
					 row.createCell(10, Cell.CELL_TYPE_NUMERIC).setCellValue("0%");
				}else{
					 row.createCell(10, Cell.CELL_TYPE_NUMERIC).setCellValue(df.format(newWeightOut.divide(totalWeightOut,4,RoundingMode.HALF_UP).floatValue()*100)+"%");
				}
				
				
				 
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
	@RequestMapping(value =  "expTax" )
	public String expTax(LcPsiTransportOrder psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		int excelNo =1;
	    List<LcPsiTransportOrder> list=this.psiTransportOrderService.expNew(psiTransportOrder); 
	    if(list.size()>65535){
	    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
	    }
	    
	    HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        
		String[] title = { " 日期 ", "  Erp运单号 ", " 件数 ","  报关单号  ", "  报关金额   ","  报关金额(RMB)  ", "  进口金额   ","  进口金额(RMB)  ", "  市场   "};
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
	  for(int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
	  }
	  BigDecimal total = BigDecimal.ZERO;
	  BigDecimal importTotal = BigDecimal.ZERO;
	  for(LcPsiTransportOrder transportOrder: list){
			int i =0;
			row = sheet.createRow(excelNo++);
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sdf1.format(transportOrder.getCreateDate()));
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getTransportNo());
			row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(transportOrder.getBoxNumber());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getDeclareNo());
			
			String zone ="";
			if("DE,de,fr,es,it,uk,".contains(transportOrder.getToCountry()+",")){
				zone="EU";
			}else if("US,ca,com,com2,com3,mx,".contains(transportOrder.getToCountry()+",")){
				zone="US";
			}else if("JP,jp,".contains(transportOrder.getToCountry()+",")){
				zone="JP";
			}
			if(transportOrder.getDeclareAmount()!=null){
				cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(transportOrder.getDeclareAmount().floatValue());
				cell.setCellStyle(cellStyle);
				Float amount =0f;
				if("EU".equals(zone)){
					amount=transportOrder.getDeclareAmount().floatValue()*(AmazonProduct2Service.getRateConfig().get("USD/CNY")/AmazonProduct2Service.getRateConfig().get("USD/EUR"));
				}else if("US".equals(zone)){
					amount=transportOrder.getDeclareAmount().floatValue()*AmazonProduct2Service.getRateConfig().get("USD/CNY");
				}else if("JP".equals(zone)){
					amount=transportOrder.getDeclareAmount().floatValue()*(AmazonProduct2Service.getRateConfig().get("USD/CNY")/AmazonProduct2Service.getRateConfig().get("USD/JPY"));
				}
				total=total.add(new BigDecimal(amount+""));
				cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(amount);
				cell.setCellStyle(cellStyle);
			}else{
				i++;
				i++;
			}
			if(transportOrder.getTotalImportAmount() != null && transportOrder.getTotalImportAmount() > 0){
				cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
				double totalImportAmount = transportOrder.getTotalImportAmount();
				cell.setCellValue(totalImportAmount);
				cell.setCellStyle(cellStyle);
				double amount =0f;
				if("EU".equals(zone)){
					amount=totalImportAmount*(AmazonProduct2Service.getRateConfig().get("USD/CNY")/AmazonProduct2Service.getRateConfig().get("USD/EUR"));
				}else if("US".equals(zone)){
					amount=totalImportAmount*AmazonProduct2Service.getRateConfig().get("USD/CNY");
				}else if("JP".equals(zone)){
					amount=totalImportAmount*(AmazonProduct2Service.getRateConfig().get("USD/CNY")/AmazonProduct2Service.getRateConfig().get("USD/JPY"));
				}
				importTotal=importTotal.add(new BigDecimal(amount+""));
				cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(amount);
				cell.setCellStyle(cellStyle);
			}else{
				i++;
				i++;
			}
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(zone);
		 }
		 row = sheet.createRow(excelNo++);
		 row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("Total");
		 cell=row.createCell(5,Cell.CELL_TYPE_NUMERIC);
		 cell.setCellValue(total.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		 cell.setCellStyle(cellStyle);
		 sheet.addMergedRegion(new Region(excelNo-1, new Short("0"),excelNo-1, new Short("4")));
		 cell=row.createCell(7,Cell.CELL_TYPE_NUMERIC);
		 cell.setCellValue(importTotal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		 cell.setCellStyle(cellStyle);
		 
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
	public String expProducts(LcPsiTransportOrder psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		    List<LcPsiTransportOrderItem> list=this.psiTransportOrderItemService.getTransportOrderItems(psiTransportOrder.getId()); 
		    HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			String[] title = { " ItemNo. ", " Product ","Quantity(PCS)","Pcs/ctn"," DE " ," DE(Sku,Fnsku,Quantity) "," UK "," UK(Sku,Fnsku,Quantity) "," FR "," FR(Sku,Fnsku,Quantity) "," IT "," IT(Sku,Fnsku,Quantity) "," ES "," ES(Sku,Fnsku,Quantity) "," JP "," JP(Sku,Fnsku,Quantity) "," COM "," COM(Sku,Fnsku,Quantity) "," CA "," CA(Sku,Fnsku,Quantity) "," MX "," MX(Sku,Fnsku,Quantity) " };
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
			for(LcPsiTransportOrderItem item :list){
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
				
				if(countryMap.get("mx")!=null){
					Map<String,Integer> skuMap = countryMap.get("mx");
					StringBuilder skuStr =new StringBuilder();
					Integer singleQuantiy=0;
					for(Map.Entry<String,Integer> skuEntry:skuMap.entrySet()){
						String sku = skuEntry.getKey();
						totalQuantity+=skuMap.get(sku);
						singleQuantiy+=skuMap.get(sku);
						skuStr.append(sku).append("[").append(fnskuMap.get(sku)).append("](").append(skuMap.get(sku)).append(");");
					}
					row.createCell(20,Cell.CELL_TYPE_STRING).setCellValue(singleQuantiy);
					row.createCell(21,Cell.CELL_TYPE_STRING).setCellValue(skuStr.toString());
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
	public String expTranElement(LcPsiTransportOrder psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
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
			Map<String,LcPsiTransportOrderItem> map = Maps.newHashMap();
			for(LcPsiTransportOrderItem item :psiTransportOrder.getItems()){
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
			List<LcPsiTransportOrderItem> tranItems =new ArrayList<LcPsiTransportOrderItem>(map.values());
			Collections.sort(tranItems);
			
			Map<Integer,PsiProduct> proMap=this.productService.findProductsMap(productIds);
			 int j =1;
			 for(LcPsiTransportOrderItem item:tranItems){
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
					if(i>=0){
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
		LcPsiTransportOrder psiTransportOrder=null;
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
		return "modules/psi/lc/lcPsiTransportReviseAdd";
	}
	
	
	private Map<Integer,List<String []>> getData(LcPsiTransportOrder psiTransportOrder,Map<Integer,PsiSupplier> supplierMap){
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
		return "modules/psi/lc/lcPsiTransportOrderCount";
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

	String fileName = "LcTtranCounts" + sdf1.format(new Date()) + ".xls";
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
	public String add(LcPsiTransportOrder psiTransportOrder, Model model) throws IOException {
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
		List<PsiInventory> inventorys=this.psiInventoryService.findByStock(130);
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
		
		List<String> componentsList=productService.findComponents();
		String components=componentsList.toString();
		model.addAttribute("components",components);
		
		return "modules/psi/lc/lcPsiTransportOrderAdd";
		
	}
	
	@RequiresPermissions("psi:transport:edit")
	@RequestMapping(value = "edit")
	public String edit(String flag,LcPsiTransportOrder psiTransportOrder,Model model) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
		}
		
		Map<String,Float> rateMap =AmazonProduct2Service.getRateConfig();
		Map<String,Float> currencyMap = Maps.newHashMap();
		//美元转人民币
		currencyMap.put("USD", new BigDecimal(rateMap.get("USD/CNY")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		currencyMap.put("CNY", 1f);
		currencyMap.put("JPY", new BigDecimal(rateMap.get("JPY/CNY")).setScale(3, BigDecimal.ROUND_HALF_UP).floatValue());
		currencyMap.put("EUR", new BigDecimal(rateMap.get("USD/CNY")/rateMap.get("USD/EUR")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		currencyMap.put("CAD", new BigDecimal(rateMap.get("USD/CNY")/rateMap.get("USD/CAD")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		currencyMap.put("MXN", new BigDecimal(rateMap.get("USD/CNY")/rateMap.get("USD/MXN")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		currencyMap.put("GBP", new BigDecimal(rateMap.get("USD/CNY")/rateMap.get("USD/GBP")).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		
		model.addAttribute("rateMap", JSON.toJSON(currencyMap));
		
		
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
		for(LcPsiTransportOrderItem item:psiTransportOrder.getItems()){
			if(!item.getProductColorCountry().equals(item.getSku())){
				skuQuantityMap.put(item.getSku(), item.getSku()+"["+fnskuMap.get(item.getSku())+"]");
			}
			sb.append(item.getId()+",");
		}
		//海运并且模式是FCL  只要是海运模式有值都传 &&psiTransportOrder.getOceanModel().equals("FCL")
		if(!StringUtils.isEmpty(psiTransportOrder.getOceanModel())&&(psiTransportOrder.getModel().equals("1")||psiTransportOrder.getModel().equals("3"))){
			for(LcPsiTransportOrderContainer container:psiTransportOrder.getContainerItems()){
				if(container.getQuantity()!=null){
					sb1.append(container.getId()+",");
				}
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
		model.addAttribute("inventory", psiInventoryService.getSkuInventoryMap(psiTransportOrder.getId(), "2"));
		
		
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
			List<PsiInventory> inventorys=this.psiInventoryService.findByStock(130);
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
		List<String> componentsList=productService.findComponents();
		String components=componentsList.toString();
		model.addAttribute("components",components);
		Map<String,String> accountMap=productService.findAccountByCountry();
		model.addAttribute("accountMap",accountMap);
		model.addAttribute("accountList",amazonAccountConfigService.findCountryByAccount());
		//快递和空运
		if(psiTransportOrder.getModel().equals("0")||psiTransportOrder.getModel().equals("2")){
			if(psiTransportOrder.getModel().equals("0")){
				model.addAttribute("typeSupplier", LogisticsSupplier.getLogisticsSupplierByType("1"));
			}else{
				model.addAttribute("typeSupplier", LogisticsSupplier.getLogisticsSupplierByType("2"));
			}
			if("0".equals(flag)){
				//产品编辑
				return "modules/psi/lc/lcPsiTransportOrderAeAndExProduct";
			}else if("1".equals(flag)){
				//花费编辑
				return "modules/psi/lc/lcPsiTransportOrderAeAndExCost";
			}
		}else if(psiTransportOrder.getModel().equals("1")||psiTransportOrder.getModel().equals("3")||psiTransportOrder.getModel().equals("4")){	//海运  铁路
			String containerTypesStr="20GP,40GP,40HQ,45HQ";
			List<String> containerTypes=Lists.newArrayList(containerTypesStr.split(","));
			model.addAttribute("containerTypes", containerTypes);
			if(psiTransportOrder.getModel().equals("1")){
				model.addAttribute("typeSupplier",LogisticsSupplier.getLogisticsSupplierByType("3"));
			}else{
				model.addAttribute("typeSupplier",LogisticsSupplier.getLogisticsSupplierByType("5"));
			}
		
			if("0".equals(flag)){
				//产品编辑
				return "modules/psi/lc/lcPsiTransportOrderOeProduct";
			}else if("1".equals(flag)){
				//花费编辑
				return "modules/psi/lc/lcPsiTransportOrderOeCost";
			}
		}
		
		return null;
		
	}
	
	@RequiresPermissions("psi:transport:view")
	@RequestMapping(value = "view")
	public String view(LcPsiTransportOrder psiTransportOrder, Model model) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
		}else if(StringUtils.isNotEmpty(psiTransportOrder.getTransportNo())){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getTransportNo());
		}else{
			return null;
		}
		model.addAttribute("psiTransportOrder", psiTransportOrder);
		return "modules/psi/lc/lcPsiTransportOrderView";
	}
	
	@RequiresPermissions("psi:transport:edit")
	@RequestMapping(value = "payDone")
	public String payDone(LcPsiTransportOrder psiTransportOrder, Model model) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
		}
		model.addAttribute("psiTransportOrder", psiTransportOrder);
		return "modules/psi/lc/lcPsiTransportOrderPayDone";
	}
	
	@RequiresPermissions("psi:transport:edit")
	@RequestMapping(value = "payDoneSave")
	public String payDoneSave(LcPsiTransportOrder psiTransportOrder, Model model, RedirectAttributes redirectAttributes) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
		}
		//设置支付完成状态
		psiTransportOrder.setPaymentSta("2");
		this.psiTransportOrderService.save(psiTransportOrder);
		addMessage(redirectAttributes, "运单'" + psiTransportOrder.getTransportNo() + "'已付完款确认成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = {"confirmPay"})
	public String confirmPay(Integer id) {
		try{
		 this.psiTransportOrderService.updateConfirmPay("1", id);
		}catch(Exception ex){
			return "true";
		}
		return "false";
	}
	
	
	@RequestMapping(value = {"genLowerPrice"})
	public String genLowerPrice(Integer id, RedirectAttributes redirectAttributes) {
		LcPsiTransportOrder order=psiTransportOrderService.get(id);
		Map<String,Map<String,PsiProductEliminate>> eliminateMap = productEliminateService.findAllByNameAndCountry();
		Map<String,Map<String, Float>> rateMap=amazonProduct2Service.getRate(DateUtils.addDays(order.getCreateDate(), -1),DateUtils.addDays(order.getCreateDate(),1));
		String date=new SimpleDateFormat("yyyy-MM-dd").format(order.getCreateDate());
		Map<String, Float> rate=Maps.newHashMap();
		if(rateMap!=null){
			rate=rateMap.get(date);
		}
		Map<String,Float> dutyMap=productService.findCustomDutyById();
        for (LcPsiTransportOrderItem item : order.getItems()) {
        	String res =psiTransportOrderService.getProPriceByProductId(item.getProduct().getId());
        	if(StringUtils.isNotEmpty(res)){
				String[] arr=res.split("_");
				Integer taxRefund=(item.getProduct().getTaxRefund()==null?17:item.getProduct().getTaxRefund());
				Float price=Float.parseFloat(arr[0].toString())/(1+(taxRefund/100f));//退税价
				Float itemPrice=Float.parseFloat(arr[0].toString());
				Float productPrice =price;
				Float lowerPrice=0f;
				String currency="";
				Float priceRate=0f;
				if("CNY".equals(arr[1])){//换成美元
					if(rate!=null&&rate.get("USD/CNY")!=null){
						productPrice=productPrice/rate.get("USD/CNY");
						itemPrice=itemPrice/rate.get("USD/CNY");
					}else{
						productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY");
						itemPrice=itemPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY");
					}
				}

				if("it,de,es,fr,uk".contains(item.getCountryCode())){
					currency="EUR";
					lowerPrice=productPrice*1.15f;
					if(dutyMap.get(item.getProduct().getId()+"_eu")!=null&&dutyMap.get(item.getProduct().getId()+"_eu")>0){
						priceRate=1f;
					}else{
						priceRate=2.2f; 
					}
					if(rate!=null&&rate.get("EUR/USD")!=null){
						itemPrice=itemPrice/rate.get("EUR/USD");
						productPrice=productPrice/rate.get("EUR/USD");
					}else{
						itemPrice=itemPrice/AmazonProduct2Service.getRateConfig().get("EUR/USD");
						productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("EUR/USD");
					}
				}else if("com,mx,ca".contains(item.getCountryCode())||item.getCountryCode().startsWith("com")){
					currency="USD";
					lowerPrice=productPrice*1.15f;
					String suf="us";
					if("ca".equals(item.getCountryCode())){
						suf="ca";
					}
					if(dutyMap.get(item.getProduct().getId()+"_"+suf)!=null&&dutyMap.get(item.getProduct().getId()+"_"+suf)>0){
						if(productPrice<=0.057*itemPrice){
							priceRate=1.2f;
						}else if(productPrice>=0.15*itemPrice){
							priceRate=0.8f;
						}else{
							priceRate=1f;
						}
					}else{
						priceRate=2.5f;
					}
				}else if("jp".contains(item.getCountryCode())){
					currency="JPY";
					lowerPrice=productPrice*1.15f;
					if(rate!=null&&rate.get("USD/JPY")!=null){
						itemPrice=itemPrice*rate.get("USD/JPY");
						productPrice=productPrice*rate.get("USD/JPY");
					}else{
						itemPrice=itemPrice*AmazonProduct2Service.getRateConfig().get("USD/JPY");
						productPrice=productPrice*AmazonProduct2Service.getRateConfig().get("USD/JPY");
					}
					if(dutyMap.get(item.getProduct().getId()+"_jp")!=null&&dutyMap.get(item.getProduct().getId()+"_jp")>0){
						priceRate=0.5f; 
					}else{
						priceRate=2f;
					}
				}
				item.setCurrency(currency);
				//item.setLowerPrice(lowerPrice);//报关价 CN_PI 
				//item.setImportPrice(productPrice*priceRate);
				item.setProductPrice(productPrice);//退税价
				item.setItemPrice(itemPrice);//采购价
				String cty = (item.getCountryCode().contains("com")?"com":item.getCountryCode());
				if(eliminateMap.get(item.getNameWithColor())!=null&&eliminateMap.get(item.getNameWithColor()).get(cty)!=null){
					item.setLowerPrice(eliminateMap.get(item.getNameWithColor()).get(cty).getCnpiPrice());
					item.setImportPrice(eliminateMap.get(item.getNameWithColor()).get(cty).getPiPrice());
				}else{
					item.setLowerPrice(0f);
					item.setImportPrice(0f);	
				}
			}
		}
        psiTransportOrderService.merge(order);
        addMessage(redirectAttributes, order.getTransportNo() + "运单价格调整成功");
        return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = {"ajaxPrice"})
	public String ajaxPrice(Integer productId,String countryCode,String colorCode,Integer tranId,String model) {
		Map<String,Map<String,PsiProductEliminate>> eliminateMap = productEliminateService.findAllByNameIdAndCountry();
		Float tempPrice1=0f;
		Float tempPrice2=0f;
		String key=productId+"";
		if(countryCode.startsWith("com")){
			countryCode="com";
		}
		if(StringUtils.isNotBlank(colorCode)){
			key=productId+"_"+colorCode;
		}
		if(eliminateMap.get(key)!=null&&eliminateMap.get(key).get(countryCode)!=null){
			tempPrice1=eliminateMap.get(key).get(countryCode).getCnpiPrice();
			tempPrice2=eliminateMap.get(key).get(countryCode).getPiPrice();
		}
		
		Map<String, Float> rate=Maps.newHashMap();
		if(tranId!=null){
			LcPsiTransportOrder order=psiTransportOrderService.get(tranId);
			model=order.getModel();
			Map<String,Map<String, Float>> rateMap=amazonProduct2Service.getRate(DateUtils.addDays(order.getCreateDate(), -1),DateUtils.addDays(order.getCreateDate(),1));
			String date=new SimpleDateFormat("yyyy-MM-dd").format(order.getCreateDate());
			if(rateMap!=null){
				rate=rateMap.get(date);
			}
		}
		if(StringUtils.isBlank(model)){
			model="0";
		}
		DecimalFormat  df= new DecimalFormat("#.##");
		String res =this.psiTransportOrderService.getProPriceByProductId(productId);
		Float 	price =0f;
		Float 	productPrice =0f;
		String  currency="";
		Integer packQuantity=0;
		String hasElectric="0";
		String hasMagnetic="0";
		if(StringUtils.isNotEmpty(res)){
			String[] arr=res.split("_");
			packQuantity =Integer.parseInt(arr[2]);
			price=Float.parseFloat(arr[0].toString());
			Integer taxRefund=Integer.parseInt(arr[5]);
			productPrice =Float.parseFloat(arr[0].toString())/(1+(taxRefund/100f));
			if("CNY".equals(arr[1])){
				productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY");
				price=price/AmazonProduct2Service.getRateConfig().get("USD/CNY");
			}
			if("it,de,es,fr,uk".contains(countryCode)){
				currency="EUR";
				if(rate!=null&&rate.get("EUR/USD")!=null){
					price=price/rate.get("EUR/USD");
					productPrice=productPrice/rate.get("EUR/USD");
				}else{
					price=price/AmazonProduct2Service.getRateConfig().get("EUR/USD");
					productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("EUR/USD");
				}
				
			}else if("com,mx,ca".contains(countryCode)||countryCode.startsWith("com")){
				currency="USD";
			}else if("jp".contains(countryCode)){
				currency="JPY";
				if(rate!=null&&rate.get("USD/JPY")!=null){
					price=price*rate.get("USD/JPY");
					productPrice=productPrice*rate.get("USD/JPY");
				}else{
					price=price*AmazonProduct2Service.getRateConfig().get("USD/JPY");
					productPrice=productPrice*AmazonProduct2Service.getRateConfig().get("USD/JPY");
				}
				
			}
			if(arr[3]!=null&&"1".equals(arr[3])){
				hasElectric="1";
			}
			if(arr[6]!=null&&"1".equals(arr[6])){
				hasMagnetic="1";
			}
		}
		String rs="{\"msg\":\"true\",\"price\":\""+df.format(price==null?0:price)+"\",\"productPrice\":\""+df.format(productPrice==null?0:productPrice)+"\",\"importPrice\":\""+df.format(tempPrice2==null?0:tempPrice2)+"\",\"packQuantity\":\""+packQuantity+"\",\"currency\":\""+currency+"\",\"hasElectric\":\""+hasElectric+"\",\"hasMagnetic\":\""+hasMagnetic+"\",\"lowerPrice\":\""+df.format(tempPrice1==null?0:tempPrice1)+"\"}";
		return rs;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = {"updateLadingBillNo"})
	public String updateLadingBillNo(Integer tranOrderId,String ladingBilNo) {
		/*LcPsiTransportOrder psiOrder=psiTransportOrderService.get(tranOrderId);
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
			LcPsiTransportOrder psiTransportOrder = psiTransportOrderService.get(tranOrderId);
			String state = psiTransportOrder.getTransportSta();
			if ("1".equals(state) || "2".equals(state) || "3".equals(state)) {	//出库、离港、到港三个状态记录原收货时间用于邮件提醒
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date oldDate = psiTransportOrder.getOperArrivalDate();
				psiTransportOrder.setChangeRecord((oldDate!=null?format.format(oldDate):"空") + "-->" + format.format(newDate));
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
	
	@RequestMapping(value = "addSave")
	public String addSave(LcPsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) throws IOException {
			psiTransportOrderService.addSaveData(psiTransportOrder);
			addMessage(redirectAttributes, "新建运单'" + psiTransportOrder.getTransportNo() + "'成功");
			return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
	@RequestMapping(value = "editSave")
	public String editSave(LcPsiTransportOrder psiTransportOrder,@RequestParam("localFile")MultipartFile[] localFile,@RequestParam("tranFile")MultipartFile[] tranFile,@RequestParam("dapFile")MultipartFile[] dapFile,@RequestParam("otherFile")MultipartFile[] otherFile,@RequestParam("otherFile1")MultipartFile[] otherFile1,@RequestParam("insuranceFile")MultipartFile[] insuranceFile,@RequestParam("taxFile")MultipartFile[] taxFile, RedirectAttributes redirectAttributes) throws IOException {
		if (filePath == null) {
			filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/psiTransport";
		}
		
		psiTransportOrderService.editSaveData(psiTransportOrder,filePath,localFile,tranFile,dapFile,otherFile,otherFile1,insuranceFile,taxFile);
//		if(psiTransportOrder.getShipmentId()!=null){
//			fbaInBoundService.upLoadSupplier(psiTransportOrder.getShipmentId(),psiTransportOrder.getLadingBillNo(), psiTransportOrder.getCarrier());
//		}
		addMessage(redirectAttributes, "编辑运单'" + psiTransportOrder.getTransportNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
	@RequestMapping(value = "productEditSave")
	public String productEditSave(LcPsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) throws IOException {
		//String fbaIds = psiTransportOrder.getFbaInboundId();
		String fbaIds="";
		String transportSta =psiTransportOrder.getTransportSta(); 
		if ( (StringUtils.isNotBlank(psiTransportOrder.getShipmentId())||StringUtils.isNotBlank(psiTransportOrder.getFbaInboundId()))  &&"0".equals(transportSta)) {	//关联item
			Map<String,Integer> fbaSkus = Maps.newHashMap();
			List<FbaInbound> inbounds = Lists.newArrayList();
			Map<String,Integer> quantityMap=Maps.newHashMap();
			if(StringUtils.isNotBlank(psiTransportOrder.getShipmentId())){
				String[] fbaIdStr = psiTransportOrder.getShipmentId().split(",");
				
				for (String shipmentId : fbaIdStr) {
					FbaInbound inbound = fbaInBoundService.getByShipmentId(shipmentId);
					if(inbound!=null){
						fbaIds=inbound.getId()+",";
						for(FbaInboundItem item:inbound.getItems()){
							if(item.getPackQuantity()!=null){
								fbaSkus.put(item.getSku()+","+item.getPackQuantity(),inbound.getId());
							}else{
								fbaSkus.put(item.getSku(),inbound.getId());
							}
						}
						inbounds.add(inbound);
					}
				}
				if(fbaIds.length()>0){
					psiTransportOrder.setFbaInboundId(fbaIds.substring(0,fbaIds.lastIndexOf(",")));
				}
			}else{
				fbaIds = psiTransportOrder.getFbaInboundId();
				String[] fbaIdStr = fbaIds.split(",");
				for (String fbaId : fbaIdStr) {
					FbaInbound inbound = fbaInBoundService.get(Integer.parseInt(fbaId));
					for(FbaInboundItem item:inbound.getItems()){
						if(item.getPackQuantity()!=null){
							fbaSkus.put(item.getSku()+","+item.getPackQuantity(),inbound.getId());
						}else{
							fbaSkus.put(item.getSku(),inbound.getId());
						}
					}
					inbounds.add(inbound);
				}
			}
			
			//对运单里的产品关联fba
			Set<String> tranSkus = Sets.newHashSet();
			for (LcPsiTransportOrderItem item : psiTransportOrder.getItems()) {
				String sku = item.getSku();
				if("0".equals(item.getOfflineSta())) {	//线上未建贴自动关联fba贴
					boolean flag = false;
					if(fbaSkus.keySet().contains(sku+","+item.getPackQuantity())) {//item的sku跟fba贴sku匹配上就自动关联
						flag = true;
						item.setFbaFlag("1");
						if(item.getFbaInboundId()==null){
							item.setFbaInboundId(fbaSkus.get(sku+","+item.getPackQuantity()));
						}
					}else if(fbaSkus.keySet().contains(sku)){
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
				if(item.getFbaInboundId()!=null){
					quantityMap.put(item.getSku()+","+item.getFbaInboundId(), item.getPackQuantity());
				}
			}
			
			//运单删除的fba里面也删除
			for(FbaInbound inbound:inbounds){
				for (Iterator<FbaInboundItem> iterator = inbound.getItems().iterator(); iterator.hasNext();) {
					FbaInboundItem inboundItem = (FbaInboundItem) iterator.next();
					if(quantityMap.get(inboundItem.getSku()+","+inbound.getId())!=null){
						inboundItem.setPackQuantity(quantityMap.get(inboundItem.getSku()+","+inbound.getId()));
					}
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
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
	//拆单
	@RequestMapping(value = "splitEditSave")
	public String splitEditSave(LcPsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) throws IOException{
		String rs = psiTransportOrderService.splitSaveData(psiTransportOrder);
		if ("0".equals(rs)) {
			addMessage(redirectAttributes, "拆分运单'" + psiTransportOrder.getTransportNo() + "'失败,拆单数为0");
		} else {
			addMessage(redirectAttributes, "拆分运单'" + psiTransportOrder.getTransportNo() + "'成功,拆分的运单号为:"+rs);
		}
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
	
	//合单
	@RequestMapping(value = "merge")
	public String merge(String ids, RedirectAttributes redirectAttributes) throws IOException {
		psiTransportOrderService.merge(ids);
		addMessage(redirectAttributes, "合并运单成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
		
	
	@RequestMapping(value = "cancel")
	public String cancel(LcPsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) {
		if(psiTransportOrder.getId()!=null){
			//this.psiTransportOrderService.updateSta("8", psiTransportOrder.getId());
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
			psiTransportOrder.setTransportSta("8");				//取消状态
			psiTransportOrder.setCancelDate(new Date());		//取消时间
			psiTransportOrder.setCancelUser(UserUtils.getUser());//取消人
			this.psiTransportOrderService.save(psiTransportOrder);
		}
		addMessage(redirectAttributes, "取消运单'" + psiTransportOrder.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
	@RequestMapping(value = "genFba")
	public String genFba(LcPsiTransportOrder psiTransportOrder,RedirectAttributes redirectAttributes) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
			if("1".equals(psiTransportOrder.getTransportType())){
				Map<String,String> accountMap=productService.findAccountByCountry();
				LcPsiTransportOrderItem  item=psiTransportOrder.getItems().get(0);
				FbaInbound inbound =fbaInBoundService.createByTransportCn(psiTransportOrder,accountMap.get(item.getSku()+"_"+item.getCountryCode()));
				psiTransportOrder.appendFbaId(inbound.getId()+"");//保存fbaId
				this.psiTransportOrderService.updateFbaId(psiTransportOrder.getTransportNo(), psiTransportOrder.getFbaInboundId());
				addMessage(redirectAttributes, "运单(" + psiTransportOrder.getTransportNo() + ")生成fba贴("+inbound.getShipmentName()+")成功!!!");
			}
		}
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
	
	/**
	 * 拆分创建FBA贴
	 * @param psiTransportOrder
	 * @param redirectAttributes
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "splitGenFba")
	public String splitGenFba(LcPsiTransportOrder psiTransportOrder,String country, RedirectAttributes redirectAttributes) throws IOException {
		String accountName=country.substring(0,country.lastIndexOf("_"));
		String tempCountry=country.substring(country.lastIndexOf("_")+1);
		String rs = fbaInBoundService.splitCreateFba(psiTransportOrder,tempCountry,accountName);   
		if ("0".equals(rs)) {
			addMessage(redirectAttributes, "运单(" + psiTransportOrder.getTransportNo() + ")生成FBA贴失败,选择的产品数为0");
		} else {
			addMessage(redirectAttributes, "运单(" + psiTransportOrder.getTransportNo() + ")生成fba贴("+rs+")成功!!!");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
	
	@RequestMapping(value = "arrive")
	public String arrive(LcPsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
			psiTransportOrder.setTransportSta("5");						//到达状态
			psiTransportOrder.setOperArrivalDate(new Date());			//到达时间
			psiTransportOrder.setOperArrivalFixedDate(new Date());      //到达时间（不能改）
			psiTransportOrder.setOperArrivalUser(UserUtils.getUser());  //到达人
			this.psiTransportOrderService.save(psiTransportOrder);
		}
		addMessage(redirectAttributes, "运单'" + psiTransportOrder.getTransportNo() + "'已到达");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
	@RequestMapping(value = "pickUp")
	public String pickUp(LcPsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) { 
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
			psiTransportOrder.setTransportSta("2");						//已离港状态
			psiTransportOrder.setOperFromPortDate(new Date());			//离港操作时间
			psiTransportOrder.setOperFromPortUser(UserUtils.getUser()); //离港操作人
			this.psiTransportOrderService.save(psiTransportOrder);
		}
		addMessage(redirectAttributes, "运单'" + psiTransportOrder.getTransportNo() + "'已离港");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
	@RequestMapping(value = "toPort")
	public String toPort(LcPsiTransportOrder psiTransportOrder, RedirectAttributes redirectAttributes) {
		if(psiTransportOrder.getId()!=null){
			psiTransportOrder=this.psiTransportOrderService.get(psiTransportOrder.getId());
			psiTransportOrder.setTransportSta("3");						//已到港状态
			psiTransportOrder.setOperToPortDate(new Date());			//到港操作时间
			psiTransportOrder.setOperToPortUser(UserUtils.getUser()); //到港操作人
			this.psiTransportOrderService.save(psiTransportOrder);
		}
		addMessage(redirectAttributes, "运单'" + psiTransportOrder.getTransportNo() + "'已到港");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
		//进口的 有关税  按基础价的0.5   无关税 基础价2倍
		//版本2 有关税   不含税价*0.5,按目的地国家币种  无关税  不含税价*2.2，按目的地国家币种
		@RequestMapping(value="expPI")
		public String expPI(LcPsiTransportOrder psiTransportOrder,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
			psiTransportOrder=this.psiTransportOrderService.getById(psiTransportOrder.getId());

			if (StringUtils.isBlank(psiTransportOrder.getSuffixName())||"PI".equals(psiTransportOrder.getSuffixName().split("-")[0])) {
				ExportTransportExcel ete = new ExportTransportExcel();
				String date= new SimpleDateFormat("yyyy-MM-dd").format(psiTransportOrder.getCreateDate() == null ? new Date()	: psiTransportOrder.getCreateDate());
				List<LcPsiTransportOrderItem> items1 = psiTransportOrder.getItems();
				Map<String, LcPsiTransportOrderItem> map = new HashMap<String, LcPsiTransportOrderItem>();
				for (LcPsiTransportOrderItem orderItem : items1) {//合并相同产品
					LcPsiTransportOrderItem transItem = map.get(orderItem.getProduct().getName()+"_"+orderItem.getPackQuantity());
				
					Float importPrice=orderItem.getImportPrice();//退税价
				
					if (transItem != null) {//存在

						orderItem.setTempPrice(new BigDecimal(importPrice).setScale(2,4));
						
						if(orderItem.getTempPrice().floatValue()>transItem.getTempPrice().floatValue()){
							transItem.setTempPrice(orderItem.getTempPrice());
						}
						transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
					} else {
						
						orderItem.setTempPrice(new BigDecimal(importPrice).setScale(2,4));
						
						map.put(orderItem.getProduct().getName()+"_"+orderItem.getPackQuantity(), orderItem);
					}
				}
				List<LcPsiTransportOrderItem> mapValuesList = new ArrayList<LcPsiTransportOrderItem>(map.values());
				Collections.sort(mapValuesList);
				
				psiTransportOrder.setItems(mapValuesList);
				
				Workbook workbook = null;
				String modelName = "PI-LC-DE-AIR-SEA";//模板文件名称
				String xmlName = "";
				String tranNo=psiTransportOrder.getTransportNo();
				
				psiTransportOrder.setCinvoiceNo(tranNo.substring(0,tranNo.indexOf("_"))+"_HK_"+tranNo.substring(tranNo.indexOf("_")+1));//20170303_LC_YD007	
				psiTransportOrder.setPinvoiceNo(tranNo.substring(0,tranNo.indexOf("_"))+"_HK_"+tranNo.substring(tranNo.indexOf("_")+1));
				
				if ("EU".equals(getZone(psiTransportOrder.getToCountry()))) {
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						modelName = "PI-LC-DE-EXP";
						xmlName = "PI-LC-DE-EXP";
					}else{
						modelName = "PI-LC-DE-AIR-SEA";
						xmlName = "PI-LC-DE-AIR-SEA";
						if("0".equals(psiTransportOrder.getModel())){
							psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*2).setScale(2, 4).floatValue());
						}else{
							psiTransportOrder.setTranAmount(2000f);
						}
					}
					
					psiTransportOrder.setFormatDate(date);
					
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					BigDecimal totalMoney=new BigDecimal(0);
					for (LcPsiTransportOrderItem item : items) {
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
				   }
					psiTransportOrder.setId(items.size()+1);
					
					psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL EUR "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
					workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
				} else if ("CA".equals(getZone(psiTransportOrder.getToCountry()))) {//TOTAL U.S. DOLLAR
					modelName = "PI-LC-CA-EXP";
					xmlName = "PI-LC-CA-EXP";
					psiTransportOrder.setFormatDate(date);
					
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
				
					BigDecimal totalMoney=new BigDecimal(0);
					for (LcPsiTransportOrderItem item : items) {
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
				   }
					psiTransportOrder.setId(items.size()+1);
					psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
					psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL U.S. DOLLAR "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
					workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
				} else if ("JP".equals(getZone(psiTransportOrder.getToCountry()))) { 
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						modelName = "PI-LC-JP-EXP";
						xmlName = "PI-LC-JP-EXP";
					}else{
						modelName = "PI-LC-JP-AIR-SEA";
						xmlName = "PI-LC-JP-AIR-SEA";
					}
					
					psiTransportOrder.setFormatDate(date);
				
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					
					BigDecimal totalMoney=new BigDecimal(0);
					for (LcPsiTransportOrderItem item : items) {
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
				   }
					psiTransportOrder.setId(items.size()+1);
					psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
					psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL JPY "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
					workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);

				} else if ("US".equals(getZone(psiTransportOrder.getToCountry()))) {
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						modelName = "PI-LC-US-EXP";
						xmlName = "PI-LC-US-EXP";
					}else if("0".equals(psiTransportOrder.getModel())){
						modelName = "PI-LC-US-AIR";
						xmlName = "PI-LC-US-AIR";
					}else{
						modelName = "PI-LC-US-SEA";
						xmlName = "PI-LC-US-SEA";
					}
					
					psiTransportOrder.setFormatDate(date);
				
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					
					BigDecimal totalMoney=new BigDecimal(0);
					for (LcPsiTransportOrderItem item : items) {
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
				   }
					psiTransportOrder.setId(items.size()+1);
					psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
					psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL USD "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
					workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);

				}else if("MX".equals(getZone(psiTransportOrder.getToCountry()))){
					modelName = "PI-LC-MX-EXP";
					xmlName = "PI-LC-MX-EXP";
					psiTransportOrder.setFormatDate(date);
					
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
				
					BigDecimal totalMoney=new BigDecimal(0);
					for (LcPsiTransportOrderItem item : items) {
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
				   }
					psiTransportOrder.setId(items.size()+1);
					psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
					psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL U.S. DOLLAR "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
					workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
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
		
		
		//出口
		//版本1 出口的 有关税 就是显示基础价  无关税按之前计算方法   
		//版本2 不含税单价统一乘以1.2倍,按美金
		//含税价/1.17*(1.17-退税率)
		@RequestMapping(value="expCNPI")
		public String expCNPI(LcPsiTransportOrder psiTransportOrder,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
			     psiTransportOrder=this.psiTransportOrderService.getById(psiTransportOrder.getId());
				ExportTransportExcel ete = new ExportTransportExcel();
				String date= new SimpleDateFormat("yyyy-MM-dd").format(psiTransportOrder.getCreateDate() == null ? new Date()	: psiTransportOrder.getCreateDate());
				List<LcPsiTransportOrderItem> items1 = psiTransportOrder.getItems();
				Map<String, LcPsiTransportOrderItem> map = new HashMap<String, LcPsiTransportOrderItem>();
				Map<Integer,Float> priceMap=Maps.newHashMap();
				//Map<Integer,String> supplierMap=productService.findSupplierByProducId();
				for (LcPsiTransportOrderItem orderItem : items1) {//合并相同产品
					LcPsiTransportOrderItem transItem = map.get(orderItem.getProduct().getChineseName()+"_"+(StringUtils.isBlank(orderItem.getProduct().getMaterial())?"":orderItem.getProduct().getMaterial()));
					
					Float lowerPrice=orderItem.getLowerPrice();//退税价
				
					priceMap.put(orderItem.getId(), orderItem.getProductPrice());
					if (transItem != null) {//存在
						
						orderItem.setTempPrice(new BigDecimal(lowerPrice).setScale(2,4));

						transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
						
						String[] arr=transItem.getProduct().getModel().split("/");
						boolean flag=true;
						for (String model: arr) {
							if(model.equals(orderItem.getProduct().getModel())){
								flag=false;
								break;
							}
						}
						if(flag){
							transItem.getProduct().setModel(transItem.getProduct().getModel()+"/"+orderItem.getProduct().getModel());
						}
						transItem.setProductPrice(orderItem.getTempPrice().floatValue()*orderItem.getQuantity()+transItem.getProductPrice());
						transItem.setTempPrice(new BigDecimal(transItem.getProductPrice()/transItem.getQuantity()).setScale(2,4));
					} else {
						orderItem.setTempPrice(new BigDecimal(lowerPrice).setScale(2,4));
						orderItem.setProductPrice(orderItem.getTempPrice().floatValue()*orderItem.getQuantity());
						transItem = map.put(orderItem.getProduct().getChineseName()+"_"+(StringUtils.isBlank(orderItem.getProduct().getMaterial())?"":orderItem.getProduct().getMaterial()), orderItem);
					}
				}
				
				List<LcPsiTransportOrderItem> mapValuesList = new ArrayList<LcPsiTransportOrderItem>(map.values());
				Collections.sort(mapValuesList);
				
				psiTransportOrder.setItems(mapValuesList);
				
				Workbook workbook = null;
				String modelName = "PI-LC-DE-AIR-SEA2";//模板文件名称
				String xmlName = "";
				if ("EU".equals(getZone(psiTransportOrder.getToCountry()))) {
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						modelName = "CN-PI-LC-DE-EXP";
						xmlName = "CN-PI-LC-DE-EXP";
						psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
					}else{
						modelName = "CN-PI-LC-DE-AIR-SEA2";
						xmlName = "CN-PI-LC-DE-AIR-SEA";
						/*if("0".equals(psiTransportOrder.getModel())){
							psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
						}else{
							psiTransportOrder.setTranAmount(4000f);
						}*/
					}
					
					psiTransportOrder.setFormatDate(date);
					psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
					psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					BigDecimal totalMoney=new BigDecimal(0);
					for (LcPsiTransportOrderItem item : items) {
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
				   }
					psiTransportOrder.setId(items.size()+1);
					//psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*40).setScale(2, 4).floatValue());
					psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL USD "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
					workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
				} else if ("CA".equals(getZone(psiTransportOrder.getToCountry()))) {//TOTAL U.S. DOLLAR
					modelName = "CN-PI-LC-CA-EXP";
					xmlName = "CN-PI-LC-CA-EXP";
					psiTransportOrder.setFormatDate(date);
					psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
					psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					BigDecimal totalMoney=new BigDecimal(0);
					for (LcPsiTransportOrderItem item : items) {
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
				   }
					psiTransportOrder.setId(items.size()+1);
					psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
					psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL U.S. DOLLAR "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
					workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
				} else if ("JP".equals(getZone(psiTransportOrder.getToCountry()))) { 
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						modelName = "CN-PI-LC-JP-EXP";
						xmlName = "CN-PI-LC-JP-EXP";
					}else{
						modelName = "CN-PI-LC-JP-AIR-SEA";
						xmlName = "CN-PI-LC-JP-AIR-SEA";
					}
					
					psiTransportOrder.setFormatDate(date);
					psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
					psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					BigDecimal totalMoney=new BigDecimal(0);
					for (LcPsiTransportOrderItem item : items) {
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
				   }
					psiTransportOrder.setId(items.size()+1);
					psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
					psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL USD "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
					workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);

				} else if ("US".equals(getZone(psiTransportOrder.getToCountry()))) {
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						modelName = "CN-PI-LC-US-EXP";
						xmlName = "CN-PI-LC-US-EXP";
					}else if("0".equals(psiTransportOrder.getModel())){
						modelName = "CN-PI-LC-US-AIR";
						xmlName = "CN-PI-LC-US-AIR";
					}else{
						modelName = "CN-PI-LC-US-SEA";
						xmlName = "CN-PI-LC-US-SEA";
					}
					
					psiTransportOrder.setFormatDate(date);
					psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
					psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					BigDecimal totalMoney=new BigDecimal(0);
					for (LcPsiTransportOrderItem item : items) {
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
				   }
					psiTransportOrder.setId(items.size()+1);
					psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
					psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL USD "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
					workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);

				}else if ("MX".equals(getZone(psiTransportOrder.getToCountry()))) {//TOTAL U.S. DOLLAR
					modelName = "CN-PI-LC-MX-EXP";
					xmlName = "CN-PI-LC-MX-EXP";
					psiTransportOrder.setFormatDate(date);
					psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
					psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					BigDecimal totalMoney=new BigDecimal(0);
					for (LcPsiTransportOrderItem item : items) {
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
				   }
					psiTransportOrder.setId(items.size()+1);
					psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
					psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL U.S. DOLLAR "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
					workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
				}
				//下载excel文档
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
				String fileName = "CN_"+modelName + sdf.format(new Date()) + ".xlsx";
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
		
		
		        public List<LcPsiTransportOrderItem> mergeItem(List<LcPsiTransportOrderItem> items1,Map<String, Map<String,Float>> costAndGw,List<LcPsiTransportOrderItem> mapValuesList,float totalPrice,float tranFee){
		        	List<LcPsiTransportOrderItem> remainingList=Lists.newArrayList();
		        	Map<String, LcPsiTransportOrderItem> map = new HashMap<String, LcPsiTransportOrderItem>();
		        	
		        	for (LcPsiTransportOrderItem orderItem : items1) {//合并相同产品
						LcPsiTransportOrderItem transItem = map.get(orderItem.getProduct().getChineseName()+"_"+(StringUtils.isBlank(orderItem.getProduct().getMaterial())?"":orderItem.getProduct().getMaterial()));
						Float lowerPrice=orderItem.getLowerPrice();
						if (transItem != null) {//存在
							//判断    购进单价/均值出口价  5-8
							float taxPrice=costAndGw.get(orderItem.getNameWithColor()).get("price")*AmazonProduct2Service.getRateConfig().get("USD/CNY")/1.17f*(1.17f-orderItem.getProduct().getTaxRefund()/100f);
							float noTranFeePrice=orderItem.getLowerPrice()-(orderItem.getLowerPrice()/totalPrice*tranFee);
							float avgPrice=(noTranFeePrice*orderItem.getQuantity()+transItem.getTempUsdPrice())/(transItem.getQuantity()+ orderItem.getQuantity());
							
							
							if(taxPrice/avgPrice>=0.76&&taxPrice/avgPrice<=1.266){
								orderItem.setTempPrice(new BigDecimal(lowerPrice).setScale(2,4));
								transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
								
								String[] arr=transItem.getProduct().getModel().split("/");
								boolean flag=true;
								for (String model: arr) {
									if(model.equals(orderItem.getProduct().getModel())){
										flag=false;
										break;
									}
								}
								if(flag){
									transItem.getProduct().setModel(transItem.getProduct().getModel()+"/"+orderItem.getProduct().getModel());
								}
								transItem.setProductPrice(orderItem.getTempPrice().floatValue()*orderItem.getQuantity()+transItem.getProductPrice());
								transItem.setTempPrice(new BigDecimal(transItem.getProductPrice()/transItem.getQuantity()).setScale(2,4));
								transItem.setTempUsdPrice(noTranFeePrice*orderItem.getQuantity()+transItem.getTempUsdPrice());
							}else{
								remainingList.add(orderItem);
							}
						} else {
							orderItem.setTempPrice(new BigDecimal(lowerPrice).setScale(2,4));
							orderItem.setProductPrice(orderItem.getTempPrice().floatValue()*orderItem.getQuantity());
							
							float noTranFeePrice=lowerPrice-(lowerPrice/totalPrice*tranFee);
							orderItem.setTempUsdPrice(noTranFeePrice*orderItem.getQuantity());
							
							transItem = map.put(orderItem.getProduct().getChineseName()+"_"+(StringUtils.isBlank(orderItem.getProduct().getMaterial())?"":orderItem.getProduct().getMaterial()), orderItem);
						}
					}
		        	mapValuesList.addAll(map.values());
		        	return remainingList;
		        }
		
		        //含税价/1.17*(1.17-退税率)  购进单价/均值出口价  5-8
				@RequestMapping(value="expCNPI2")
				public String expCNPI2(LcPsiTransportOrder psiTransportOrder,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
					     psiTransportOrder=this.psiTransportOrderService.getById(psiTransportOrder.getId());
						ExportTransportExcel ete = new ExportTransportExcel();
						String date= new SimpleDateFormat("yyyy-MM-dd").format(psiTransportOrder.getCreateDate() == null ? new Date()	: psiTransportOrder.getCreateDate());
						List<LcPsiTransportOrderItem> items1 = psiTransportOrder.getItems();
						Map<String, LcPsiTransportOrderItem> map = new HashMap<String, LcPsiTransportOrderItem>();
						//Map<Integer,Float> priceMap=Maps.newHashMap();
						Map<String, Map<String,Float>> costAndGw = saleReportService.getProductPriceAndTranGw("USD",null);
						
						List<LcPsiTransportOrderItem> remainingList=Lists.newArrayList();
						
						Float totalPrice=0f;
						
						for (LcPsiTransportOrderItem item : items1) {
							totalPrice+=item.getLowerPrice()*item.getQuantity();
						}
						String toCountry="";
						if("de,fr,it,es,uk".contains(items1.get(0).getCountryCode())){
							 toCountry="EU";
						}else if("com".equals(items1.get(0).getCountryCode())||items1.get(0).getCountryCode().startsWith("com")){
							toCountry="US";
						}else if("ca".equals(items1.get(0).getCountryCode())){
							toCountry="CA";
						}else if("jp".equals(items1.get(0).getCountryCode())){
							toCountry="JP";
						}
						Float tranFee=0f;
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							psiTransportOrder.setModel("快递运输");
							if("EU".equals(toCountry)){
								tranFee=psiTransportOrder.getWeight()*5.5f;
							}else if("US".equals(toCountry)){
								tranFee=psiTransportOrder.getWeight()*5.5f;
							}else if("CA".equals(toCountry)){
								tranFee=psiTransportOrder.getWeight()*5.5f;
							}else if("JP".equals(toCountry)){
								tranFee=psiTransportOrder.getWeight()*3f;
							}
						}else if("1".equals(psiTransportOrder.getModel())){//海运
							psiTransportOrder.setModel("海运运输");
	                        if("EU".equals(toCountry)){
	                        	tranFee=2134f;
							}else if("US".equals(toCountry)){
								tranFee=psiTransportOrder.getWeight()*0.9f;
							}else if("CA".equals(toCountry)){
								tranFee=psiTransportOrder.getWeight()*0.9f;
							}else if("JP".equals(toCountry)){
								if(psiTransportOrder.getVolume()<27){
									tranFee=600f;
								}else{
									tranFee=800f;
								}
							}
						}else if("3".equals(psiTransportOrder.getModel())){//海运
							
						}else{
							psiTransportOrder.setModel("航空运输");
	                        if("EU".equals(toCountry)){
	                        	tranFee=psiTransportOrder.getWeight()*2.134f;
							}else if("US".equals(toCountry)){
								tranFee=psiTransportOrder.getWeight()*4.5f;
							}else if("CA".equals(toCountry)){
								tranFee=psiTransportOrder.getWeight()*4.5f;
							}else if("JP".equals(toCountry)){
								tranFee=psiTransportOrder.getWeight()*3f;
							}
						}
						
						tranFee=tranFee*6.5f;
						Map<String,LcPsiTransportOrderItem> unionMap = Maps.newHashMap();
						for (LcPsiTransportOrderItem orderItem : items1) {
							String key = orderItem.getProduct().getId().toString()+orderItem.getLowerPrice();
							if(unionMap.get(key)==null){
								unionMap.put(key, orderItem);
							}else{
								LcPsiTransportOrderItem item = unionMap.get(key);
								item.setQuantity(item.getQuantity()+orderItem.getQuantity());
							}
						}
						
						List<LcPsiTransportOrderItem> unionList = new ArrayList<LcPsiTransportOrderItem>(unionMap.values());
						for (LcPsiTransportOrderItem orderItem : unionList) {//合并相同产品
							LcPsiTransportOrderItem transItem = map.get(orderItem.getProduct().getChineseName()+"_"+(StringUtils.isBlank(orderItem.getProduct().getMaterial())?"":orderItem.getProduct().getMaterial()));
							
							Float lowerPrice=orderItem.getLowerPrice();
							 
							//priceMap.put(orderItem.getId(), orderItem.getProductPrice());
							if (transItem != null) {//存在
								//判断    购进单价/均值出口价  5-8
								float taxPrice=costAndGw.get(orderItem.getNameWithColor()).get("price")*AmazonProduct2Service.getRateConfig().get("USD/CNY")/1.17f*(1.17f-orderItem.getProduct().getTaxRefund()/100f);
								float noTranFeePrice=orderItem.getLowerPrice()-(orderItem.getLowerPrice()/totalPrice*tranFee);
								float avgPrice=(noTranFeePrice*orderItem.getQuantity()+transItem.getTempUsdPrice())/(transItem.getQuantity()+ orderItem.getQuantity());
								
								if(taxPrice/avgPrice>=0.76&&taxPrice/avgPrice<=1.266){
									orderItem.setTempPrice(new BigDecimal(lowerPrice).setScale(2,4));
									transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
									
									String[] arr=transItem.getProduct().getModel().split("/");
									boolean flag=true;
									for (String model: arr) {
										if(model.equals(orderItem.getProduct().getModel())){
											flag=false;
											break;
										}
									}
									if(flag){
										transItem.getProduct().setModel(transItem.getProduct().getModel()+"/"+orderItem.getProduct().getModel());
									}
									transItem.setProductPrice(orderItem.getTempPrice().floatValue()*orderItem.getQuantity()+transItem.getProductPrice());
									transItem.setTempPrice(new BigDecimal(transItem.getProductPrice()/transItem.getQuantity()).setScale(2,4));
									transItem.setTempUsdPrice(noTranFeePrice*orderItem.getQuantity()+transItem.getTempUsdPrice());
								}else{
									remainingList.add(orderItem);
								}
							} else {
								orderItem.setTempPrice(new BigDecimal(lowerPrice).setScale(2,4));
								orderItem.setProductPrice(orderItem.getTempPrice().floatValue()*orderItem.getQuantity());
								
								float noTranFeePrice=lowerPrice-(lowerPrice/totalPrice*tranFee);
								orderItem.setTempUsdPrice(noTranFeePrice*orderItem.getQuantity());
								
								transItem = map.put(orderItem.getProduct().getChineseName()+"_"+(StringUtils.isBlank(orderItem.getProduct().getMaterial())?"":orderItem.getProduct().getMaterial()), orderItem);
							}
						}
						List<LcPsiTransportOrderItem> mapValuesList = new ArrayList<LcPsiTransportOrderItem>(map.values());
						
						if(remainingList!=null&&remainingList.size()>0){
							while(remainingList!=null&&remainingList.size()>0){
								remainingList=mergeItem(remainingList,costAndGw, mapValuesList,totalPrice,tranFee);
							}
						}
						
						Collections.sort(mapValuesList);
						psiTransportOrder.setItems(mapValuesList);
						
						Workbook workbook = null;
						String modelName = "PI-LC-DE-AIR-SEA2";//模板文件名称
						String xmlName = "";
						if ("EU".equals(getZone(psiTransportOrder.getToCountry()))) {
							if ("2".equals(psiTransportOrder.getModel())) {//exp
								modelName = "CN-PI-LC-DE-EXP";
								xmlName = "CN-PI-LC-DE-EXP";
								psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
							}else{
								modelName = "CN-PI-LC-DE-AIR-SEA2";
								xmlName = "CN-PI-LC-DE-AIR-SEA";
							}
							
							psiTransportOrder.setFormatDate(date);
							psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
							psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
							List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
							BigDecimal totalMoney=new BigDecimal(0);
							for (LcPsiTransportOrderItem item : items) {
								item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
								totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
						   }
							psiTransportOrder.setId(items.size()+1);
							psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL CNY "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
							workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
						} else if ("CA".equals(getZone(psiTransportOrder.getToCountry()))) {//TOTAL U.S. DOLLAR
							modelName = "CN-PI-LC-CA-EXP";
							xmlName = "CN-PI-LC-CA-EXP";
							psiTransportOrder.setFormatDate(date);
							psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
							psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
							List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
							BigDecimal totalMoney=new BigDecimal(0);
							for (LcPsiTransportOrderItem item : items) {
								item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
								totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
						   }
							psiTransportOrder.setId(items.size()+1);
							psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
							psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL CNY "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
							workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
						} else if ("JP".equals(getZone(psiTransportOrder.getToCountry()))) { 
							if ("2".equals(psiTransportOrder.getModel())) {//exp
								modelName = "CN-PI-LC-JP-EXP";
								xmlName = "CN-PI-LC-JP-EXP";
							}else{
								modelName = "CN-PI-LC-JP-AIR-SEA";
								xmlName = "CN-PI-LC-JP-AIR-SEA";
							}
							
							psiTransportOrder.setFormatDate(date);
							psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
							psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
							List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
							BigDecimal totalMoney=new BigDecimal(0);
							for (LcPsiTransportOrderItem item : items) {
								item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
								totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
						   }
							psiTransportOrder.setId(items.size()+1);
							psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
							psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL CNY "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
							workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);

						} else if ("US".equals(getZone(psiTransportOrder.getToCountry()))) {
							if ("2".equals(psiTransportOrder.getModel())) {//exp
								modelName = "CN-PI-LC-US-EXP";
								xmlName = "CN-PI-LC-US-EXP";
							}else if("0".equals(psiTransportOrder.getModel())){
								modelName = "CN-PI-LC-US-AIR";
								xmlName = "CN-PI-LC-US-AIR";
							}else{
								modelName = "CN-PI-LC-US-SEA";
								xmlName = "CN-PI-LC-US-SEA";
							}
							
							psiTransportOrder.setFormatDate(date);
							psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
							psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
							List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
							BigDecimal totalMoney=new BigDecimal(0);
							for (LcPsiTransportOrderItem item : items) {
								item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
								totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
						   }
							psiTransportOrder.setId(items.size()+1);
							psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
							psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL CNY "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
							workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);

						}else if ("MX".equals(getZone(psiTransportOrder.getToCountry()))) {//TOTAL U.S. DOLLAR
							modelName = "CN-PI-LC-MX-EXP";
							xmlName = "CN-PI-LC-MX-EXP";
							psiTransportOrder.setFormatDate(date);
							psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
							psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
							List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
							BigDecimal totalMoney=new BigDecimal(0);
							for (LcPsiTransportOrderItem item : items) {
								item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
								totalMoney=totalMoney.add(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())));
						   }
							psiTransportOrder.setId(items.size()+1);
							psiTransportOrder.setTranAmount(new BigDecimal(psiTransportOrder.getWeight()*4).setScale(2, 4).floatValue());
							psiTransportOrder.setRemark(StringUtils.upperCase("TOTAL CNY "+NumberUtil.format(totalMoney.setScale(2, 4).doubleValue())));
							workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
						}
						//下载excel文档
						response.setCharacterEncoding("UTF-8");
						response.setContentType("application/x-download");
						SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
						String fileName = "CN_"+modelName + sdf.format(new Date()) + ".xlsx";
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
		
		

		@RequestMapping(value="expSI")
		public String exportSlTransportOrder(LcPsiTransportOrder psiTransportOrder,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
			psiTransportOrder=this.psiTransportOrderService.getById(psiTransportOrder.getId());
			ExportTransportExcel ete = new ExportTransportExcel();
			String date = new SimpleDateFormat("yyyy/MM/dd").format(psiTransportOrder.getCreateDate() == null ? new Date(): psiTransportOrder.getCreateDate());
			psiTransportOrder.setFormatDate(date);
			psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
			psiTransportOrder.setPinvoiceNo(psiTransportOrder.getTransportNo());
			psiTransportOrder.setRate1(AmazonProduct2Service.getRateConfig().get("EUR/USD"));
			List<LcPsiTransportOrderItem> items =new ArrayList<LcPsiTransportOrderItem>();
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
			public String exportPlTransportOrder(LcPsiTransportOrder psiTransportOrder,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
				psiTransportOrder=this.psiTransportOrderService.getById(psiTransportOrder.getId());
				
				if (StringUtils.isBlank(psiTransportOrder.getSuffixName())||"PL".equals(psiTransportOrder.getSuffixName().split("-")[1])) {
					ExportTransportExcel ete = new ExportTransportExcel();
					String date = new SimpleDateFormat("dd,MMM,yyyy", Locale.US).format(psiTransportOrder.getCreateDate() == null ? new Date(): psiTransportOrder.getCreateDate());
					List<LcPsiTransportOrderItem> newItems = new ArrayList<LcPsiTransportOrderItem>();
					List<LcPsiTransportOrderItem> items1 = psiTransportOrder.getItems();
					Map<String, LcPsiTransportOrderItem> map = new HashMap<String, LcPsiTransportOrderItem>();
					for (LcPsiTransportOrderItem orderItem : items1) {//合并相同产品
						LcPsiTransportOrderItem transItem = map.get(orderItem.getProduct().getName()+"_"+orderItem.getPackQuantity());
						int num = orderItem.getQuantity()/ orderItem.getPackQuantity();//多少箱
						if (num > 0	&& orderItem.getQuantity()% orderItem.getPackQuantity() == 0) {//整箱
							orderItem.getProduct().setGw(orderItem.getProduct().getGw().multiply(new BigDecimal(num)));
							orderItem.getProduct().setWeight(orderItem.getProduct().getGw().subtract(new BigDecimal(num)));
						}else if (num > 0	&& orderItem.getQuantity()% orderItem.getPackQuantity() != 0) {
							int mod = orderItem.getQuantity()% orderItem.getPackQuantity();
							orderItem.getProduct().setGw(orderItem.getProduct().getGw().multiply(new BigDecimal(num+mod*1.0f/orderItem.getPackQuantity())).setScale(2,4));
							orderItem.getProduct().setWeight(orderItem.getProduct().getGw().subtract(new BigDecimal(num+mod*1.0f/orderItem.getPackQuantity())).setScale(2,4));
						}else if (num <= 0) {
							orderItem.getProduct().setGw(orderItem.getProduct().getGw().multiply(new BigDecimal(orderItem.getQuantity()*1.0f/orderItem.getPackQuantity())).setScale(2,4));
							orderItem.getProduct().setWeight(orderItem.getProduct().getGw().subtract(new BigDecimal(orderItem.getQuantity()*1.0f/orderItem.getPackQuantity())).setScale(2,4));
						}
						if (transItem != null) {//存在
							transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
							transItem.getProduct().setGw(transItem.getProduct().getGw().add(orderItem.getProduct().getGw()));
							transItem.getProduct().setWeight(transItem.getProduct().getWeight().add(orderItem.getProduct().getWeight()));
						} else {
							map.put(orderItem.getProduct().getName()+"_"+orderItem.getPackQuantity(), orderItem);
						}
					}
					List<LcPsiTransportOrderItem> mapValuesList = new ArrayList<LcPsiTransportOrderItem>(map.values());
					Collections.sort(mapValuesList);
					
					psiTransportOrder.setItems(mapValuesList);
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					int count = 1;
					for (int i = 0; i < items.size(); i++) {
						LcPsiTransportOrderItem item = items.get(i);
						int num = item.getQuantity()/ item.getPackQuantity();//多少箱
						if (num > 0	&& item.getQuantity()% item.getPackQuantity() == 0) {//整箱
							if (count != (count + num - 1)) {
								item.setCartonNo(count + "-"+ (count + num - 1));
							} else {
								item.setCartonNo(count + "");
							}
							count = count + num;
							//item.getProduct().setWeight((item.getProduct().getWeight() == null ? new BigDecimal(0) : item.getProduct().getWeight()).multiply(new BigDecimal(item.getQuantity())).divide(new BigDecimal(1000)));
							//item.getProduct().setGw(item.getProduct().getGw().multiply(new BigDecimal(num)));
							//item.getProduct().setWeight(item.getProduct().getGw().subtract(new BigDecimal(num)));
							//item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().split(";")[0].substring(0, item.getProduct().getChineseName().split(";")[0].lastIndexOf("(")));
							item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
							
							newItems.add(item);
						} else if (num > 0	&& item.getQuantity()% item.getPackQuantity() != 0) {
							int mod = item.getQuantity()% item.getPackQuantity();
							//整箱部分
							item.setCartonNo(count+"-"+(count+num));
							count = count + num +1;
							//item.getProduct().setGw(item.getProduct().getGw().multiply(new BigDecimal(num+mod*1.0f/item.getPackQuantity())).setScale(2,4));
							//item.getProduct().setWeight(item.getProduct().getGw().subtract(new BigDecimal(num+mod*1.0f/item.getPackQuantity())).setScale(2,4));
							item.setQuantity(item.getQuantity());
							item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
							newItems.add(item);
							//剩余部分
							/*LcPsiTransportOrderItem item1 = new LcPsiTransportOrderItem();
							item1.setProduct(new PsiProduct());
							item1.setCartonNo(count + "");
							item1.setProductName(item.getProductName());
							item1.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
							
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
							item1.getProduct().setMaterial(item.getProduct().getMaterial());
							item1.setPackQuantity(item.getPackQuantity());
							newItems.add(item1);*/
						} else if (num <= 0) {
							//不足一箱
							item.setCartonNo(count + "");
							count = count + 1;
							//item.getProduct().setGw(item.getProduct().getGw().multiply(new BigDecimal(item.getQuantity()*1.0f/item.getPackQuantity())).setScale(2,4));
							//item.getProduct().setWeight(item.getProduct().getGw().subtract(new BigDecimal(item.getQuantity()*1.0f/item.getPackQuantity())).setScale(2,4));
							//item.getProduct().setPackLength(new BigDecimal(0));
							//item.getProduct().setPackWidth(new BigDecimal(0));
							//item.getProduct().setPackHeight(new BigDecimal(0));
							
							//item.getProduct().setType(item.getProduct().getChineseName()==null?"":(item.getProduct().getChineseName().split(";")==null?item.getProduct().getChineseName().split("；")[1]:item.getProduct().getChineseName().split(";")[1]));
							item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
							
							newItems.add(item);
						}
					}
					psiTransportOrder.setItems(newItems);
					Workbook workbook = null;
					String modelName = "";//模板文件名称
					String xmlName = "";
					String tranNo=psiTransportOrder.getTransportNo();
					psiTransportOrder.setCinvoiceNo(tranNo.substring(0,tranNo.indexOf("_"))+"_HK_"+tranNo.substring(tranNo.indexOf("_")+1));//20170303_LC_YD007	
					psiTransportOrder.setPinvoiceNo(tranNo.substring(0,tranNo.indexOf("_"))+"_HK_"+tranNo.substring(tranNo.indexOf("_")+1));
					if ("CA".equals(getZone(psiTransportOrder.getToCountry()))) {
						modelName = "PL-LC-CA-EXP";
						xmlName = "PL-LC-CA-EXP";
						psiTransportOrder.setFormatDate(date);
						
						workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
					}if ("MX".equals(getZone(psiTransportOrder.getToCountry()))) {
						modelName = "PL-LC-MX-EXP";
						xmlName = "PL-LC-MX-EXP";
						psiTransportOrder.setFormatDate(date);
						
						workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
					}  else if ("EU".equals(getZone(psiTransportOrder.getToCountry()))) {
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							xmlName = "PL-LC-DE-EXP";
							modelName = "PL-LC-DE-EXP";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);
						} else {
							xmlName = "PL-LC-DE-AIR-SEA";
							modelName =  "PL-LC-DE-AIR-SEA";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);
						}
						psiTransportOrder.setFormatDate(date);
					
						workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);

					} else if ("JP".equals(getZone(psiTransportOrder.getToCountry()))) {
						psiTransportOrder.setFormatDate(date);
						
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							modelName = "PL-LC-JP-EXP";
							xmlName = "PL-LC-JP-EXP";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);
						} else {
							modelName = "PL-LC-JP-AIR-SEA";
							xmlName = "PL-LC-JP-AIR-SEA";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);
						}

					} else if ("US".equals(getZone(psiTransportOrder.getToCountry()))) {
						psiTransportOrder.setFormatDate(date);
						
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							modelName = "PL-LC-US-EXP";
							xmlName = "PL-LC-US-EXP";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName,0);
						} else {
							modelName = "PL-LC-US-AIR-SEA";
							xmlName = "PL-LC-US-AIR-SEA";
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
			
			
			@RequestMapping(value="expHKPL")
			public String expHKPL(LcPsiTransportOrder psiTransportOrder,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
				    psiTransportOrder=this.psiTransportOrderService.getById(psiTransportOrder.getId());
				
					ExportTransportExcel ete = new ExportTransportExcel();
					String date = new SimpleDateFormat("dd,MMM,yyyy", Locale.US).format(psiTransportOrder.getCreateDate() == null ? new Date(): psiTransportOrder.getCreateDate());
					List<LcPsiTransportOrderItem> newItems = new ArrayList<LcPsiTransportOrderItem>();
					List<LcPsiTransportOrderItem> items1 = psiTransportOrder.getItems();
					Map<String, LcPsiTransportOrderItem> map = new HashMap<String, LcPsiTransportOrderItem>();
				/*	for (LcPsiTransportOrderItem orderItem : items1) {//合并相同产品
						LcPsiTransportOrderItem transItem = map.get(orderItem.getProduct().getName()+"_"+orderItem.getPackQuantity());
						if (transItem != null) {//存在
							transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
						} else {
							map.put(orderItem.getProduct().getName()+"_"+orderItem.getPackQuantity(), orderItem);
						}
					}
					*/
					for (LcPsiTransportOrderItem orderItem : items1) {//合并相同产品
						LcPsiTransportOrderItem transItem = map.get(orderItem.getProduct().getName()+"_"+orderItem.getPackQuantity());
						int num = orderItem.getQuantity()/ orderItem.getPackQuantity();//多少箱
						if (num > 0	&& orderItem.getQuantity()% orderItem.getPackQuantity() == 0) {//整箱
							orderItem.getProduct().setGw(orderItem.getProduct().getGw().multiply(new BigDecimal(num)));
							orderItem.getProduct().setWeight(orderItem.getProduct().getGw().subtract(new BigDecimal(num)));
						}else if (num > 0	&& orderItem.getQuantity()% orderItem.getPackQuantity() != 0) {
							int mod = orderItem.getQuantity()% orderItem.getPackQuantity();
							orderItem.getProduct().setGw(orderItem.getProduct().getGw().multiply(new BigDecimal(num+mod*1.0f/orderItem.getPackQuantity())).setScale(2,4));
							orderItem.getProduct().setWeight(orderItem.getProduct().getGw().subtract(new BigDecimal(num+mod*1.0f/orderItem.getPackQuantity())).setScale(2,4));
						}else if (num <= 0) {
							orderItem.getProduct().setGw(orderItem.getProduct().getGw().multiply(new BigDecimal(orderItem.getQuantity()*1.0f/orderItem.getPackQuantity())).setScale(2,4));
							orderItem.getProduct().setWeight(orderItem.getProduct().getGw().subtract(new BigDecimal(orderItem.getQuantity()*1.0f/orderItem.getPackQuantity())).setScale(2,4));
						}
						if (transItem != null) {//存在
							transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
							transItem.getProduct().setGw(transItem.getProduct().getGw().add(orderItem.getProduct().getGw()));
							transItem.getProduct().setWeight(transItem.getProduct().getWeight().add(orderItem.getProduct().getWeight()));
						} else {
							map.put(orderItem.getProduct().getName()+"_"+orderItem.getPackQuantity(), orderItem);
						}
					}
					
					List<LcPsiTransportOrderItem> mapValuesList = new ArrayList<LcPsiTransportOrderItem>(map.values());
					Collections.sort(mapValuesList);
					
					psiTransportOrder.setItems(mapValuesList);
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					int count = 1;
					for (int i = 0; i < items.size(); i++) {
						LcPsiTransportOrderItem item = items.get(i);
						int num = item.getQuantity()/ item.getPackQuantity();//多少箱
						if (num > 0	&& item.getQuantity()% item.getPackQuantity() == 0) {//整箱
							if (count != (count + num - 1)) {
								item.setCartonNo(count + "-"+ (count + num - 1));
							} else {
								item.setCartonNo(count + "");
							}
							count = count + num;
							//item.getProduct().setGw(item.getProduct().getGw().multiply(new BigDecimal(num)));
							//item.getProduct().setWeight(item.getProduct().getGw().subtract(new BigDecimal(num)));
							item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
							newItems.add(item);
						} else if (num > 0	&& item.getQuantity()% item.getPackQuantity() != 0) {
							int mod = item.getQuantity()% item.getPackQuantity();
							//整箱部分
							item.setCartonNo(count+"-"+(count+num));
							count = count + num +1;
							//item.getProduct().setGw(item.getProduct().getGw().multiply(new BigDecimal(num+mod*1.0f/item.getPackQuantity())).setScale(2,4));
							//item.getProduct().setWeight(item.getProduct().getGw().subtract(new BigDecimal(num+mod*1.0f/item.getPackQuantity())).setScale(2,4));
							item.setQuantity(item.getQuantity());
							item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
							newItems.add(item);
						} else if (num <= 0) {
							//不足一箱
							item.setCartonNo(count + "");
							count = count + 1;
							//item.getProduct().setGw(item.getProduct().getGw().multiply(new BigDecimal(item.getQuantity()*1.0f/item.getPackQuantity())).setScale(2,4));
							//item.getProduct().setWeight(item.getProduct().getGw().subtract(new BigDecimal(item.getQuantity()*1.0f/item.getPackQuantity())).setScale(2,4));
							item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
							newItems.add(item);
						}
					}
					psiTransportOrder.setItems(newItems);
					Workbook workbook = null;
					String modelName = "";//模板文件名称
					String xmlName = "";
					if ("CA".equals(getZone(psiTransportOrder.getToCountry()))) {
						modelName = "HK-PL-LC-CA-EXP";
						xmlName = "PL-LC-CA-EXP";
						psiTransportOrder.setFormatDate(date);
						psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
						workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
					} if ("MX".equals(getZone(psiTransportOrder.getToCountry()))) {
						modelName = "HK-PL-LC-MX-EXP";
						xmlName = "HK-PL-LC-MX-EXP";
						psiTransportOrder.setFormatDate(date);
						psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
						workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);
					}else if ("EU".equals(getZone(psiTransportOrder.getToCountry()))) {
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							xmlName = "PL-LC-DE-EXP";
							modelName = "HK-PL-LC-DE-EXP";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);
						} else {
							xmlName = "PL-LC-DE-AIR-SEA";
							modelName =  "HK-PL-LC-DE-AIR-SEA";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);
						}
						psiTransportOrder.setFormatDate(date);
						psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
						workbook = ete.writeData(psiTransportOrder, xmlName,modelName, 0);

					} else if ("JP".equals(getZone(psiTransportOrder.getToCountry()))) {
						psiTransportOrder.setFormatDate(date);
						psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							modelName = "HK-PL-LC-JP-EXP";
							xmlName = "PL-LC-JP-EXP";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);
						} else {
							modelName = "HK-PL-LC-JP-AIR-SEA";
							xmlName = "PL-LC-JP-AIR-SEA";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);
						}

					} else if ("US".equals(getZone(psiTransportOrder.getToCountry()))) {
						psiTransportOrder.setFormatDate(date);
						psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							modelName = "HK-PL-LC-US-EXP";
							xmlName = "PL-LC-US-EXP";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName,0);
						} else {
							modelName = "HK-PL-LC-US-AIR-SEA";
							xmlName = "PL-LC-US-AIR-SEA";
							workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);
						}

					}
					//下载excel文档
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/x-download");
					SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
					String fileName = "CN-"+modelName + sdf.format(new Date())+ ".xlsx";
					fileName = URLEncoder.encode(fileName, "UTF-8");
					response.addHeader("Content-Disposition","attachment;filename=" + fileName);
					try {
						OutputStream out = response.getOutputStream();
						if(workbook!=null){
							workbook.write(out);
						}
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				
				return null;
			}
			
			//customs declaration
			@RequestMapping(value="expCustomsDeclaration")
			public String expCustomsDeclaration(LcPsiTransportOrder psiTransportOrder,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
				    psiTransportOrder=this.psiTransportOrderService.getById(psiTransportOrder.getId());
				    ExportTransportExcel ete = new ExportTransportExcel();
					String date = new SimpleDateFormat("yyyy/MM/dd").format(psiTransportOrder.getPickUpDate()== null ? new Date(): psiTransportOrder.getPickUpDate());
					List<LcPsiTransportOrderItem> newItems = new ArrayList<LcPsiTransportOrderItem>();
					List<LcPsiTransportOrderItem> items1 = psiTransportOrder.getItems();
					Map<String, LcPsiTransportOrderItem> map = new HashMap<String, LcPsiTransportOrderItem>();
					float totalWeight=0f;
					float totalGw=0f;
					String toCountry="";
					if("de,fr,it,es,uk".contains(items1.get(0).getCountryCode())){
						 toCountry="EU";
					}else if("com".equals(items1.get(0).getCountryCode())||items1.get(0).getCountryCode().startsWith("com")){
						toCountry="US";
					}else if("ca".equals(items1.get(0).getCountryCode())){
						toCountry="CA";
					}else if("jp".equals(items1.get(0).getCountryCode())){
						toCountry="JP";
					}
					Map<String, Map<String,Float>> costAndGw = saleReportService.getProductPriceAndTranGw("USD",null);
					List<LcPsiTransportOrderItem> remainingList=Lists.newArrayList();
					Float totalPrice=0f;
					for (LcPsiTransportOrderItem item : items1) {
						totalPrice+=item.getLowerPrice()*item.getQuantity();
					}
					
					Float tranFee=0f;
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						if("EU".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*5.5f;
						}else if("US".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*5.5f;
						}else if("CA".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*5.5f;
						}else if("JP".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*3f;
						}
					}else if("1".equals(psiTransportOrder.getModel())){//海运
                        if("EU".equals(toCountry)){
                        	tranFee=2134f;
						}else if("US".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*0.9f;
						}else if("CA".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*0.9f;
						}else if("JP".equals(toCountry)){
							if(psiTransportOrder.getVolume()<27){
								tranFee=600f;
							}else{
								tranFee=800f;
							}
						}
					}else if("3".equals(psiTransportOrder.getModel())){//海运
						
					}else{
                        if("EU".equals(toCountry)){
                        	tranFee=psiTransportOrder.getWeight()*2.134f;
						}else if("US".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*4.5f;
						}else if("CA".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*4.5f;
						}else if("JP".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*3f;
						}
					}
					tranFee=tranFee*6.5f;
					Map<String,LcPsiTransportOrderItem> unionMap = Maps.newHashMap();
					for (LcPsiTransportOrderItem orderItem : items1) {
						String key = orderItem.getProduct().getId().toString()+orderItem.getLowerPrice();
						if(unionMap.get(key)==null){
							unionMap.put(key, orderItem);
						}else{
							LcPsiTransportOrderItem item = unionMap.get(key);
							item.setQuantity(item.getQuantity()+orderItem.getQuantity());
						}
					}
					
					List<LcPsiTransportOrderItem> unionList = new ArrayList<LcPsiTransportOrderItem>(unionMap.values());
					for (LcPsiTransportOrderItem orderItem : unionList) {//合并相同产品
						LcPsiTransportOrderItem transItem = map.get(orderItem.getProduct().getChineseName()+"_"+(StringUtils.isBlank(orderItem.getProduct().getMaterial())?"":orderItem.getProduct().getMaterial()));
						
						Float lowerPrice=orderItem.getLowerPrice();
						
						
						if (transItem != null) {//存在
							//判断    购进单价/均值出口价  5-8
							float taxPrice=costAndGw.get(orderItem.getNameWithColor()).get("price")*AmazonProduct2Service.getRateConfig().get("USD/CNY")/1.17f*(1.17f-orderItem.getProduct().getTaxRefund()/100f);
							float noTranFeePrice=orderItem.getLowerPrice()-(orderItem.getLowerPrice()/totalPrice*tranFee);
							float avgPrice=(noTranFeePrice*orderItem.getQuantity()+transItem.getTempUsdPrice())/(transItem.getQuantity()+ orderItem.getQuantity());
							
							if(taxPrice/avgPrice>=0.76&&taxPrice/avgPrice<=1.266){
								orderItem.setTempPrice(new BigDecimal(lowerPrice).setScale(2,4));
								transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
								
								String[] arr=transItem.getProduct().getModel().split("/");
								boolean flag=true;
								for (String model: arr) {
									if(model.equals(orderItem.getProduct().getModel())){
										flag=false;
										break;
									}
								}
								if(flag){
									transItem.getProduct().setModel(transItem.getProduct().getModel()+"/"+orderItem.getProduct().getModel());
								}
								orderItem.getProduct().setGw(orderItem.getProduct().getGw().multiply(new BigDecimal(orderItem.getQuantity()*1.0f/ orderItem.getPackQuantity())));
								orderItem.getProduct().setWeight(orderItem.getProduct().getGw().subtract(new BigDecimal(orderItem.getQuantity()*1.0f/ orderItem.getPackQuantity())));
								totalWeight+=orderItem.getProduct().getWeight().floatValue();
								totalGw+=orderItem.getProduct().getGw().floatValue();
								transItem.setProductPrice(orderItem.getTempPrice().floatValue()*orderItem.getQuantity()+transItem.getProductPrice());
								transItem.setTempPrice(new BigDecimal(transItem.getProductPrice()/transItem.getQuantity()).setScale(2,4));
								transItem.setTempUsdPrice(noTranFeePrice*orderItem.getQuantity()+transItem.getTempUsdPrice());
							}else{
								orderItem.getProduct().setGw(orderItem.getProduct().getGw().multiply(new BigDecimal(orderItem.getQuantity()*1.0f/ orderItem.getPackQuantity())));
								orderItem.getProduct().setWeight(orderItem.getProduct().getGw().subtract(new BigDecimal(orderItem.getQuantity()*1.0f/ orderItem.getPackQuantity())));
								totalWeight+=orderItem.getProduct().getWeight().floatValue();
								totalGw+=orderItem.getProduct().getGw().floatValue();
								remainingList.add(orderItem);
							}
						} else {
							
							orderItem.setTempPrice(new BigDecimal(lowerPrice).setScale(2,4));
							orderItem.setProductPrice(orderItem.getTempPrice().floatValue()*orderItem.getQuantity());
							
							orderItem.getProduct().setGw(orderItem.getProduct().getGw().multiply(new BigDecimal(orderItem.getQuantity()*1.0f/ orderItem.getPackQuantity())));
							orderItem.getProduct().setWeight(orderItem.getProduct().getGw().subtract(new BigDecimal(orderItem.getQuantity()*1.0f/ orderItem.getPackQuantity())));
							totalWeight+=orderItem.getProduct().getWeight().floatValue();
							totalGw+=orderItem.getProduct().getGw().floatValue();
							
							float noTranFeePrice=lowerPrice-(lowerPrice/totalPrice*tranFee);
							orderItem.setTempUsdPrice(noTranFeePrice*orderItem.getQuantity());
							
							transItem = map.put(orderItem.getProduct().getChineseName()+"_"+(StringUtils.isBlank(orderItem.getProduct().getMaterial())?"":orderItem.getProduct().getMaterial()), orderItem);
						}
					}
					List<LcPsiTransportOrderItem> mapValuesList = new ArrayList<LcPsiTransportOrderItem>(map.values());
					
					if(remainingList!=null&&remainingList.size()>0){
						while(remainingList!=null&&remainingList.size()>0){
							remainingList=mergeItem(remainingList,costAndGw, mapValuesList,totalPrice,tranFee);
						}
					}
					
					Collections.sort(mapValuesList);
					psiTransportOrder.setItems(mapValuesList);
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();// 1：sea 0:air 2:express
					psiTransportOrder.setRemark("CNY"+new BigDecimal(tranFee).setScale(2,4));
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						psiTransportOrder.setModel("快递运输");
					}else if("1".equals(psiTransportOrder.getModel())){//海运
						psiTransportOrder.setModel("海运运输");
					}else if("3".equals(psiTransportOrder.getModel())){//海运
						psiTransportOrder.setModel("铁路运输");
						psiTransportOrder.setRemark("");
					}else{
						psiTransportOrder.setModel("航空运输");
					}
					
					/*int count = 1;
					int totalCartonNo=1;*/
					
					if ("CA".equals(getZone(psiTransportOrder.getToCountry()))) {
						psiTransportOrder.setToCountry("加拿大");
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							psiTransportOrder.setCarrier("DDP");
						}else{
							psiTransportOrder.setCarrier("CFR");
						}
					} else if ("EU".equals(getZone(psiTransportOrder.getToCountry()))) {
						psiTransportOrder.setToCountry("德国");
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							psiTransportOrder.setCarrier("DAP");
						}else if("1".equals(psiTransportOrder.getModel())){//海运
							psiTransportOrder.setCarrier("CFR");
						}else{
							psiTransportOrder.setCarrier("CFR");
						}
					} else if ("JP".equals(getZone(psiTransportOrder.getToCountry()))) {
						psiTransportOrder.setToCountry("日本");
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							psiTransportOrder.setCarrier("DAP");
						}else if("1".equals(psiTransportOrder.getModel())){//海运
							psiTransportOrder.setCarrier("CFR");
						}else{
							psiTransportOrder.setCarrier("CFR");
						}
					} else if ("US".equals(getZone(psiTransportOrder.getToCountry()))) {
						psiTransportOrder.setToCountry("美国");
						if ("2".equals(psiTransportOrder.getModel())) {//exp
							psiTransportOrder.setCarrier("DAP");
						}else if("1".equals(psiTransportOrder.getModel())){//海运
							psiTransportOrder.setCarrier("DDP");
						}else{
							psiTransportOrder.setCarrier("CFR");
						}
					}else if ("MX".equals(getZone(psiTransportOrder.getToCountry()))) {
						psiTransportOrder.setToCountry("墨西哥");
					}
					
					for (int i = 0; i < items.size(); i++) {
						LcPsiTransportOrderItem item = items.get(i);
						String unit="个";
						if(item.getProduct().getChineseName().contains(")")){
							unit=item.getProduct().getChineseName().substring(item.getProduct().getChineseName().lastIndexOf("(")+1, item.getProduct().getChineseName().lastIndexOf(")"));
						}
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().split(";")[0].replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						
						if("com".equals(item.getCountryCode())||"ca".equals(item.getCountryCode())||item.getCountryCode().startsWith("com")){
							item.setRemark("人民币   照章征税");
						}else if("jp".equals(item.getCountryCode())){
							item.setRemark("人民币   照章征税");
						}else{
							item.setRemark("人民币   照章征税");
						}
						item.setCountryCode(item.getQuantity()+unit+"  "+psiTransportOrder.getToCountry());
						
						/*totalWeight+=item.getProduct().getWeight().floatValue();
						totalGw+=item.getProduct().getGw().floatValue();*/
						item.setTempTotalPrice(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())).setScale(2, 4));
						newItems.add(item);
					}
					/*psiTransportOrder.setBoxNumber(totalCartonNo);*/
					psiTransportOrder.setWeight(totalWeight);
					psiTransportOrder.setVolume(totalGw);
					psiTransportOrder.setItems(newItems);
					Workbook workbook = null;
					String modelName = "PL_CustomsDeclaration";//模板文件名称
					String xmlName = "PL_CustomsDeclaration";
					psiTransportOrder.setFormatDate(date);
					psiTransportOrder.setCinvoiceNo(psiTransportOrder.getTransportNo());

					
					workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);
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
				
				return null;
			}
			
			
			@RequestMapping(value="expCustomDutyProduct") //CN_PI
			public String expCustomDutyProduct(LcPsiTransportOrder psiTransportOrder,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
				    psiTransportOrder=this.psiTransportOrderService.getById(psiTransportOrder.getId());
					List<LcPsiTransportOrderItem> newItems = new ArrayList<LcPsiTransportOrderItem>();
					List<LcPsiTransportOrderItem> items1 = psiTransportOrder.getItems();
					Map<String, LcPsiTransportOrderItem> map = new HashMap<String, LcPsiTransportOrderItem>();
					
					Map<String, Map<String,Float>> costAndGw = saleReportService.getProductPriceAndTranGw("USD",null);
					
					List<LcPsiTransportOrderItem> remainingList=Lists.newArrayList();
					Float totalPrice=0f;
					for (LcPsiTransportOrderItem item : items1) {
						totalPrice+=item.getLowerPrice()*item.getQuantity();
					}
					String toCountry="";
					if("de,fr,it,es,uk".contains(items1.get(0).getCountryCode())){
						 toCountry="EU";
					}else if("com".equals(items1.get(0).getCountryCode())||items1.get(0).getCountryCode().startsWith("com")){
						toCountry="US";
					}else if("ca".equals(items1.get(0).getCountryCode())){
						toCountry="CA";
					}else if("jp".equals(items1.get(0).getCountryCode())){
						toCountry="JP";
					}
					Float tranFee=0f;
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						if("EU".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*5.5f;
						}else if("US".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*5.5f;
						}else if("CA".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*5.5f;
						}else if("JP".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*3f;
						}
					}else if("1".equals(psiTransportOrder.getModel())){//海运
                        if("EU".equals(toCountry)){
                        	tranFee=2134f;
						}else if("US".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*0.9f;
						}else if("CA".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*0.9f;
						}else if("JP".equals(toCountry)){
							if(psiTransportOrder.getVolume()<27){
								tranFee=600f;
							}else{
								tranFee=800f;
							}
						}
					}else if("3".equals(psiTransportOrder.getModel())){//海运
						
					}else{
                        if("EU".equals(toCountry)){
                        	tranFee=psiTransportOrder.getWeight()*2.134f;
						}else if("US".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*4.5f;
						}else if("CA".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*4.5f;
						}else if("JP".equals(toCountry)){
							tranFee=psiTransportOrder.getWeight()*3f;
						}
					}
					tranFee = tranFee*6.5f;
					Map<String,LcPsiTransportOrderItem> unionMap = Maps.newHashMap();
					for (LcPsiTransportOrderItem orderItem : items1) {
						String key = orderItem.getProduct().getId().toString()+orderItem.getLowerPrice();
						if(unionMap.get(key)==null){
							unionMap.put(key, orderItem);
						}else{
							LcPsiTransportOrderItem item = unionMap.get(key);
							item.setQuantity(item.getQuantity()+orderItem.getQuantity());
						}
					}
					
					List<LcPsiTransportOrderItem> unionList = new ArrayList<LcPsiTransportOrderItem>(unionMap.values());
					for (LcPsiTransportOrderItem orderItem : unionList) {//合并相同产品
						LcPsiTransportOrderItem transItem = map.get(orderItem.getProduct().getChineseName()+"_"+(StringUtils.isBlank(orderItem.getProduct().getMaterial())?"":orderItem.getProduct().getMaterial()));
						
						Float lowerPrice=orderItem.getLowerPrice();
					
						//priceMap.put(orderItem.getId(), orderItem.getProductPrice());
						if (transItem != null) {//存在
							//判断    购进单价/均值出口价  5-8
							float taxPrice=costAndGw.get(orderItem.getNameWithColor()).get("price")*AmazonProduct2Service.getRateConfig().get("USD/CNY")/1.17f*(1.17f-orderItem.getProduct().getTaxRefund()/100f);
							float noTranFeePrice=orderItem.getLowerPrice()-(orderItem.getLowerPrice()/totalPrice*tranFee);
							float avgPrice=(noTranFeePrice*orderItem.getQuantity()+transItem.getTempUsdPrice())/(transItem.getQuantity()+ orderItem.getQuantity());
							
							if(taxPrice/avgPrice>=0.76&&taxPrice/avgPrice<=1.266){
								orderItem.setTempPrice(new BigDecimal(lowerPrice).setScale(2,4));
								transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
								
								String[] arr=transItem.getProduct().getModel().split("/");
								boolean flag=true;
								for (String model: arr) {
									if(model.equals(orderItem.getProduct().getModel())){
										flag=false;
										break;
									}
								}
								if(flag){
									transItem.getProduct().setModel(transItem.getProduct().getModel()+"/"+orderItem.getProduct().getModel());
								}
								transItem.setProductPrice(orderItem.getTempPrice().floatValue()*orderItem.getQuantity()+transItem.getProductPrice());
								transItem.setTempPrice(new BigDecimal(transItem.getProductPrice()/transItem.getQuantity()).setScale(2,4));
								transItem.setTempUsdPrice(noTranFeePrice*orderItem.getQuantity()+transItem.getTempUsdPrice());
							}else{
								remainingList.add(orderItem);
							}
						} else {
							
							orderItem.setTempPrice(new BigDecimal(lowerPrice).setScale(2,4));
							orderItem.setProductPrice(orderItem.getTempPrice().floatValue()*orderItem.getQuantity());
							float noTranFeePrice=lowerPrice-(lowerPrice/totalPrice*tranFee);
							orderItem.setTempUsdPrice(noTranFeePrice*orderItem.getQuantity());
							transItem = map.put(orderItem.getProduct().getChineseName()+"_"+(StringUtils.isBlank(orderItem.getProduct().getMaterial())?"":orderItem.getProduct().getMaterial()), orderItem);
						}
					}
					List<LcPsiTransportOrderItem> mapValuesList = new ArrayList<LcPsiTransportOrderItem>(map.values());
					
					if(remainingList!=null&&remainingList.size()>0){
						while(remainingList!=null&&remainingList.size()>0){
							remainingList=mergeItem(remainingList,costAndGw, mapValuesList,totalPrice,tranFee);
						}
					}
					
					Collections.sort(mapValuesList);
					psiTransportOrder.setItems(mapValuesList);
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						psiTransportOrder.setModel("快递运输");
					}else if("1".equals(psiTransportOrder.getModel())){//海运
						psiTransportOrder.setModel("海运运输");
					}else if("3".equals(psiTransportOrder.getModel())){//海运
						psiTransportOrder.setModel("铁路运输");
					}else{
						psiTransportOrder.setModel("航空运输");
					}
					
					BigDecimal totalMoney=new BigDecimal(0);
					for (int i = 0; i < items.size(); i++) {
						LcPsiTransportOrderItem item = items.get(i);

						String unit="个";
						if(item.getProduct().getChineseName().contains(")")){
							unit=item.getProduct().getChineseName().substring(item.getProduct().getChineseName().lastIndexOf("(")+1, item.getProduct().getChineseName().lastIndexOf(")"));
						}
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						
						if("com".equals(item.getCountryCode())||"ca".equals(item.getCountryCode())||item.getCountryCode().startsWith("com")){
							item.setRemark("人民币   照章征税");
						}else if("jp".equals(item.getCountryCode())){
							item.setRemark("人民币   照章征税");
						}else{
							item.setRemark("人民币   照章征税");
						}
						item.setRemark(unit);
						item.setTempTotalPrice(item.getTempPrice().multiply(new BigDecimal(item.getQuantity())).setScale(2, 4));
						totalMoney=totalMoney.add(item.getTempTotalPrice());
						newItems.add(item);
						
					}
					psiTransportOrder.setItems(newItems);
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
					row.setHeight((short)450);
					CellStyle contentStyle = wb.createCellStyle();
					contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
					contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
					contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
					contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
					contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
					contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
					contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
					contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
					HSSFCell cell = null;
					
					List<String> title = Lists.newArrayList("1.Name of commodity 货物名称","2.Quantity 数量","3.Unit price 单价","4.Amount 金额");
					for (int i = 0; i < title.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(title.get(i));
						cell.setCellStyle(contentStyle);
						sheet.autoSizeColumn((short) i);
				    }
					int rownum=1;
					String currency="";
					for (LcPsiTransportOrderItem item: newItems) {
						row = sheet.createRow(rownum++);
						int j=0;
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getProduct().getType());
						row.getCell(j-1).setCellStyle(contentStyle);
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantity()+"Units/"+item.getRemark());
						row.getCell(j-1).setCellStyle(contentStyle);
						if("com".equals(item.getCountryCode())||"ca".equals(item.getCountryCode())||item.getCountryCode().startsWith("com")){
							currency="CNY ";
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("CNY "+item.getTempPrice());
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("CNY "+item.getTempTotalPrice());
						}else if("jp".equals(item.getCountryCode())){
							currency="CNY ";
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("CNY "+item.getTempPrice());
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("CNY "+item.getTempTotalPrice());
						}else{
							currency="CNY ";
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("CNY "+item.getTempPrice());
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("CNY "+item.getTempTotalPrice());
						}
						row.getCell(j-1).setCellStyle(contentStyle);
						row.getCell(j-2).setCellStyle(contentStyle);
					}
					int k=0;
					row = sheet.createRow(rownum++);
					row.createCell(k++,Cell.CELL_TYPE_STRING).setCellValue("Total Amount");
					row.getCell(k-1).setCellStyle(contentStyle);
					row.createCell(k++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(k-1).setCellStyle(contentStyle);
					row.createCell(k++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(k-1).setCellStyle(contentStyle);
					sheet.addMergedRegion(new CellRangeAddress(rownum-1,rownum-1,0,2));
					row.createCell(k++,Cell.CELL_TYPE_STRING).setCellValue(currency+totalMoney);
					row.getCell(k-1).setCellStyle(contentStyle);
					
					
					sheet.setColumnWidth(0, 100 *86);
					sheet.setColumnWidth(1,46*100);
					sheet.setColumnWidth(2,46*100);
					sheet.setColumnWidth(3,46*100);
					try {
						request.setCharacterEncoding("UTF-8");
						response.setCharacterEncoding("UTF-8");
						response.setContentType("application/x-download");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			            String fileName = "货物明细统计_LC" + sdf.format(new Date()) + ".xls";
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
			
			@RequestMapping(value="expCustomDutyProduct2")  //HK  PI
			public String expCustomDutyProduct2(LcPsiTransportOrder psiTransportOrder,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
				    psiTransportOrder=this.psiTransportOrderService.getById(psiTransportOrder.getId());
					List<LcPsiTransportOrderItem> newItems = new ArrayList<LcPsiTransportOrderItem>();
					/*List<LcPsiTransportOrderItem> items1 = psiTransportOrder.getItems();
					Map<String, LcPsiTransportOrderItem> map = new HashMap<String, LcPsiTransportOrderItem>();
					Map<Integer,Float> priceMap=Maps.newHashMap();
					for (LcPsiTransportOrderItem orderItem : items1) {//合并相同产品
						priceMap.put(orderItem.getId(), orderItem.getProductPrice());
						LcPsiTransportOrderItem transItem = map.get(orderItem.getProduct().getChineseName()+"_"+(StringUtils.isBlank(orderItem.getProduct().getMaterial())?"":orderItem.getProduct().getMaterial()));
						
						if (transItem != null) {//存在
						
							orderItem.setTempPrice(new BigDecimal(orderItem.getImportPrice()).setScale(2,4));
							
							transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
							
							String[] arr=transItem.getProduct().getModel().split("/");
							boolean flag=true;
							for (String model: arr) {
								if(model.equals(orderItem.getProduct().getModel())){
									flag=false;
									break;
								}
							}
							if(flag){
								transItem.getProduct().setModel(transItem.getProduct().getModel()+"/"+orderItem.getProduct().getModel());
							}
							
							transItem.setProductPrice(orderItem.getTempPrice().floatValue()*orderItem.getQuantity()+transItem.getProductPrice());
							transItem.setTempPrice(new BigDecimal(transItem.getProductPrice()/transItem.getQuantity()).setScale(2,4));
						} else {
						
							orderItem.setTempPrice(new BigDecimal(orderItem.getImportPrice()).setScale(2,4));
							
							orderItem.setProductPrice(orderItem.getTempPrice().floatValue()*orderItem.getQuantity());
							map.put(orderItem.getProduct().getChineseName()+"_"+(StringUtils.isBlank(orderItem.getProduct().getMaterial())?"":orderItem.getProduct().getMaterial()), orderItem);
						}
					}
					List<LcPsiTransportOrderItem> mapValuesList = new ArrayList<LcPsiTransportOrderItem>(map.values());
					Collections.sort(mapValuesList);
					psiTransportOrder.setItems(mapValuesList);*/
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					if ("2".equals(psiTransportOrder.getModel())) {//exp
						psiTransportOrder.setModel("快递运输");
					}else if("1".equals(psiTransportOrder.getModel())){//海运
						psiTransportOrder.setModel("海运运输");
					}else if("3".equals(psiTransportOrder.getModel())){//海运
						psiTransportOrder.setModel("铁路运输");
					}else{
						psiTransportOrder.setModel("航空运输");
					}
					
					BigDecimal totalMoney=new BigDecimal(0);
					for (int i = 0; i < items.size(); i++) {
						LcPsiTransportOrderItem item = items.get(i);

						String unit="个";
						if(item.getProduct().getChineseName().contains(")")){
							unit=item.getProduct().getChineseName().substring(item.getProduct().getChineseName().lastIndexOf("(")+1, item.getProduct().getChineseName().lastIndexOf(")"));
						}
						item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
						
						if("com".equals(item.getCountryCode())||"ca".equals(item.getCountryCode())||item.getCountryCode().startsWith("com")){
							item.setRemark("美元   照章征税");
						}else if("jp".equals(item.getCountryCode())){
							item.setRemark("日元   照章征税");
						}else{
							item.setRemark("欧元   照章征税");
						}
						item.setRemark(unit);
						item.setTempTotalPrice(new BigDecimal(item.getImportPrice()).multiply(new BigDecimal(item.getQuantity())).setScale(2, 4));
						totalMoney=totalMoney.add(item.getTempTotalPrice());
						newItems.add(item);
						
					}
					psiTransportOrder.setItems(newItems);
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
					row.setHeight((short)450);
					CellStyle contentStyle = wb.createCellStyle();
					contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
					contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
					contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
					contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
					contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
					contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
					contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
					contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
					HSSFCell cell = null;
					
					List<String> title = Lists.newArrayList("1.Name of commodity 货物名称","2.Quantity 数量","3.Unit price 单价","4.Amount 金额");
					for (int i = 0; i < title.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(title.get(i));
						cell.setCellStyle(contentStyle);
						sheet.autoSizeColumn((short) i);
				    }
					int rownum=1;
					String currency="";
					for (LcPsiTransportOrderItem item: newItems) {
						row = sheet.createRow(rownum++);
						int j=0;
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getProduct().getType());
						row.getCell(j-1).setCellStyle(contentStyle);
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantity()+"Units/"+item.getRemark());
						row.getCell(j-1).setCellStyle(contentStyle);
						if("com".equals(item.getCountryCode())||"ca".equals(item.getCountryCode())||item.getCountryCode().startsWith("com")){
							currency="USD ";
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("USD "+item.getImportPrice());
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("USD "+item.getTempTotalPrice());
						}else if("jp".equals(item.getCountryCode())){
							currency="JPY ";
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("JPY "+item.getImportPrice());
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("JPY "+item.getTempTotalPrice());
						}else{
							currency="EUR ";
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("EUR "+item.getImportPrice());
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("EUR "+item.getTempTotalPrice());
						}
						row.getCell(j-1).setCellStyle(contentStyle);
						row.getCell(j-2).setCellStyle(contentStyle);
					}
					int k=0;
					row = sheet.createRow(rownum++);
					row.createCell(k++,Cell.CELL_TYPE_STRING).setCellValue("Total Amount");
					row.getCell(k-1).setCellStyle(contentStyle);
					row.createCell(k++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(k-1).setCellStyle(contentStyle);
					row.createCell(k++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(k-1).setCellStyle(contentStyle);
					sheet.addMergedRegion(new CellRangeAddress(rownum-1,rownum-1,0,2));
					row.createCell(k++,Cell.CELL_TYPE_STRING).setCellValue(currency+totalMoney);
					row.getCell(k-1).setCellStyle(contentStyle);
					
					
					sheet.setColumnWidth(0, 100 *86);
					sheet.setColumnWidth(1,46*100);
					sheet.setColumnWidth(2,46*100);
					sheet.setColumnWidth(3,46*100);
					try {
						request.setCharacterEncoding("UTF-8");
						response.setCharacterEncoding("UTF-8");
						response.setContentType("application/x-download");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			            String fileName = "货物明细统计_HK" + sdf.format(new Date()) + ".xls";
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
			
			@RequestMapping(value="upload")
			@ResponseBody
			public  String uploadFile(String psiTransportId,MultipartFile uploadFile,String uploadType,HttpServletResponse response,RedirectAttributes redirectAttributes) throws UnsupportedEncodingException{
				if(uploadFile.getSize()!=0){
					LcPsiTransportOrder psiTransportOrder=this.psiTransportOrderService.get(Integer.parseInt(psiTransportId));
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
					}else if("14".equals(uploadType)){//other
						baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport/"+psiTransportNo+"/export";
						baseDir = new File(baseDirStr); 
						if(!baseDir.isDirectory())
							baseDir.mkdirs();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
						name = psiTransportNo+sdf.format(new Date())+ suffix;
					}else if("15".equals(uploadType)){//mix_file    
						name = psiTransportNo+"_BL_LC"+suffix;
						suffixStr=StringUtils.isBlank(psiTransportOrder.getMixFile())?"BL_LC-PL_LC-SC_HK":psiTransportOrder.getMixFile();
						suffixName= suffix+"-"+suffixStr.substring(suffixStr.indexOf("-")+1);
					}else if("16".equals(uploadType)){//mix_file   
						name = psiTransportNo+"_PL_LC"+suffix;
						suffixStr=StringUtils.isBlank(psiTransportOrder.getMixFile())?"BL_LC-PL_LC-SC_HK":psiTransportOrder.getMixFile();
						suffixName= suffixStr.substring(0,suffixStr.indexOf("-"))+"-"+suffix+"-"+suffixStr.substring(suffixStr.lastIndexOf("-")+1);
					}else if("17".equals(uploadType)){//mix_file    
						name = psiTransportNo+"_SC_HK"+suffix;
						suffixStr=StringUtils.isBlank(psiTransportOrder.getMixFile())?"BL_LC-PL_LC-SC_HK":psiTransportOrder.getMixFile();
						suffixName= suffixStr.substring(0,suffixStr.lastIndexOf("-"))+"-"+suffix;
					}else{
						if("4".equals(uploadType)){//SO
							name=psiTransportNo+"_SO"+suffix;
						}else if("5".equals(uploadType)){//入仓核实单
							name=psiTransportNo+"_WV"+suffix;
						}else if("6".equals(uploadType)){//查验单
							name=psiTransportNo+"_IS"+suffix;
						}else if("7".equals(uploadType)){//报关单
							name=psiTransportNo+"_CD"+suffix;
						}else if("8".equals(uploadType)){//销售合同
							name=psiTransportNo+"_CS"+suffix;
						}else if("9".equals(uploadType)){//保险单
							name=psiTransportNo+"_SP"+suffix;
						}else if("10".equals(uploadType)){//电放单
							name=psiTransportNo+"_RS"+suffix;
						}else if("11".equals(uploadType)){//到货通知
							name=psiTransportNo+"_AN"+suffix;
						}else if("12".equals(uploadType)){//进口税单
							name=psiTransportNo+"_IB"+suffix;
						}
						suffixStr=getSuffixStr(psiTransportOrder.getSuffixName());
						suffixArr=getSuffixArr(psiTransportOrder.getSuffixName());
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
						}else if("14".equals(uploadType)){
							this.psiTransportOrderService.updateExportPath(Integer.parseInt(psiTransportId),name);
						}else if("15".equals(uploadType)||"16".equals(uploadType)||"17".equals(uploadType)){
							this.psiTransportOrderService.updateMixFile(Integer.parseInt(psiTransportId), suffixName);
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
				//return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
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
		String zone=countryCode;
		if(countryCode.startsWith("com")||countryCode.equals("com")||countryCode.equals("US")){
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
		if(countryCode.startsWith("com")||countryCode.equals("com")||countryCode.equals("US")){
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
            response.setHeader("Content-disposition", "attachment; filename="   + URLEncoder.encode(fileName,"utf-8"));   
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
	
	
	//一次导出报关底单、销售合同、出口发票
	@RequestMapping("/download3")   
    public ModelAndView download3(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		
		java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null; 
		 try { 
				fileName = HtmlUtils.htmlUnescape(fileName);
		        response.setContentType("text/html;charset=utf-8");   
		        request.setCharacterEncoding("UTF-8");   
		        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/psi/psiTransport/";  
		        String downLoadPath = ctxPath + fileName;
		        fileName = fileName.substring(fileName.lastIndexOf("/")+1);
	            long fileLength = new File(downLoadPath).length();   
	            response.setContentType("application/x-msdownload;");   
	            response.setHeader("Content-disposition", "attachment; filename="   + URLEncoder.encode(fileName,"utf-8"));   
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
	public String expTotal(LcPsiTransportOrder psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
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
			String lcPath=ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/lcPsiTransport/";
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
			}else if("PILC".equals(type)){
				for (LcPsiTransportOrder order : orderList) {//fileName=/${psiTransportOrder.transportNo}/export/${psiTransportOrder.exportInvoicePath}
					//ctxPath + fileName
                    if (StringUtils.isNotBlank(order.getExportInvoicePath())) {
                      	 String tempPath=path+order.getTransportNo()+"/export/"+order.getExportInvoicePath();
                      	 files.add(new File(tempPath));
					}
				}
			}else if("BLLC".equals(type)){//BL_LC-PL_LC-SC_HK
				for (LcPsiTransportOrder order : orderList) {
					if(StringUtils.isNotBlank(order.getMixFile())&&!"BL_LC".equals(order.getMixFile().split("-")[0])){
		                  String fileName= order.getTransportNo()+"_BL_LC"+order.getMixFile().split("-")[0];
		                  String tempPath=path+order.getTransportNo()+"/"+fileName;
		                  files.add(new File(tempPath));
					}
				}
				
			}else if("PLLC".equals(type)){
				for (LcPsiTransportOrder order : orderList) {
					if(StringUtils.isNotBlank(order.getMixFile())&&!"PL_LC".equals(order.getMixFile().split("-")[1])){
		                  String fileName= order.getTransportNo()+"_PL_LC"+order.getMixFile().split("-")[1];
		                  String tempPath=path+order.getTransportNo()+"/"+fileName;
		                  files.add(new File(tempPath));
					}
				}
			}else if("SCHK".equals(type)){
				for (LcPsiTransportOrder order : orderList) {
					if(StringUtils.isNotBlank(order.getMixFile())&&!"SC_HK".equals(order.getMixFile().split("-")[2])){
		                  String fileName= order.getTransportNo()+"_SC_HK"+order.getMixFile().split("-")[2];
		                  String tempPath=path+order.getTransportNo()+"/"+fileName;
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
	
	@RequestMapping(value = {"viewInvoiceList"})
	public String viewInvoiceList(Integer id, RedirectAttributes redirectAttributes, Model model) {
		LcPsiTransportOrder order=psiTransportOrderService.get(id);
		model.addAttribute("order", order);
		model.addAttribute("invoiceList",psiVatInvoiceInfoService.find());
		return "modules/psi/lc/lcPsiTransportOrderInvoiceList";
	}
	
	/*1.优先按发票开立的原始时间寻找  从历史到现在
	2.一个产品优先找本产品自己的发票，如自己发票不够，
	再优先找同一家供应商的相同品名的能满足数量的发票，如数量不够，
	再找第三家相同品名的发票，尽量避免同一报关单多家供应商
	*/
	
	
	@ResponseBody
	@RequestMapping(value = {"genInvoice"})
	public String genInvoice(Integer id, RedirectAttributes redirectAttributes) {
		LcPsiTransportOrder order=psiTransportOrderService.get(id);
		Set<Integer> idSet=Sets.newHashSet();
		for (LcPsiTransportOrderItem item : order.getItems()) {
			idSet.add(item.getId());
		}	
		//清空之前分配
		if(idSet!=null&&idSet.size()>0){
			List<PsiVatInvoiceUseInfo> existList=psiVatInvoiceInfoService.findUseInfo(idSet);
			if(existList!=null&&existList.size()>0){
				List<PsiVatInvoiceUseInfo> tempList=Lists.newArrayList();
				Map<Integer,Integer> qtyMap=Maps.newHashMap();
				for (PsiVatInvoiceUseInfo info : existList) {
					Integer invoiceId=info.getInvoice().getId();
					Integer quantity=info.getQuantity();
					info.setDelFlag("1");
					tempList.add(info);
					qtyMap.put(invoiceId, quantity+(qtyMap.get(invoiceId)==null?0:qtyMap.get(invoiceId)));
				}
				psiVatInvoiceInfoService.save(tempList);
				psiVatInvoiceInfoService.updateQuantity2(qtyMap);
			}
		}
		
		Map<String,Map<String,List<PsiVatInvoiceInfo>>>  invoiceMap=psiVatInvoiceInfoService.findInvoice();
		Map<Integer,Integer> quantityMap=Maps.newHashMap();
		Map<String,String> nameMap=productService.findChineseName();
		List<PsiVatInvoiceUseInfo> useInfoList=Lists.newArrayList();
		
		for (LcPsiTransportOrderItem item : order.getItems()) {
			String pname=item.getProductName();
			Integer quantity=item.getQuantity();
			if(pname.endsWith("US")||pname.endsWith("JP")||pname.endsWith("UK")||pname.endsWith("EU")||pname.endsWith("DE")){
				pname=pname.replace("US","").replace("JP","").replace("UK","").replace("EU","").replace("DE","");
			}
			Map<String,List<PsiVatInvoiceInfo>> map=invoiceMap.get(pname);
			if(map!=null){
				boolean flag=false;
				for (Entry<String, List<PsiVatInvoiceInfo>> temp: map.entrySet()) {
					List<PsiVatInvoiceInfo> psiVatInvoiceInfo=temp.getValue();
					for (PsiVatInvoiceInfo info : psiVatInvoiceInfo) {
						Integer totalQuantity=0;
						if(quantityMap==null||quantityMap.get(info.getId())==null){
							 totalQuantity=info.getRemainingQuantity();
						}else{
							 totalQuantity=quantityMap.get(info.getId());
						}
						PsiVatInvoiceUseInfo useInfo=new PsiVatInvoiceUseInfo();
						if(quantity>0){
							if(totalQuantity>0&&totalQuantity>=quantity){
								useInfo.setCountryCode(item.getCountryCode());
								useInfo.setProductName(item.getProductName());
								useInfo.setInvoiceNo(info.getInvoiceNo());
								useInfo.setInvoice(psiVatInvoiceInfoService.get(info.getId()));
								useInfo.setItem(item);
								useInfo.setQuantity(quantity);
								useInfo.setCreateDate(new Date());
								useInfo.setCreateUser(UserUtils.getUser());
								useInfo.setDelFlag("0");
								quantityMap.put(info.getId(),totalQuantity-quantity);
								quantity=0;
								useInfoList.add(useInfo);
								flag=true;
								break;
							}else if(totalQuantity>0){
								useInfo.setCountryCode(item.getCountryCode());
								useInfo.setProductName(item.getProductName());
								useInfo.setInvoiceNo(info.getInvoiceNo());
								useInfo.setItem(item);
								useInfo.setQuantity(totalQuantity);
								useInfo.setInvoice(psiVatInvoiceInfoService.get(info.getId()));
								useInfo.setCreateDate(new Date());
								useInfo.setCreateUser(UserUtils.getUser());
								useInfo.setDelFlag("0");
								useInfoList.add(useInfo);
								quantity=quantity-totalQuantity;
								quantityMap.put(info.getId(),0);
							}
						}else{
							flag=true;
							break;
						}
					}
					if(flag){
						break;
					}
				}
				if(quantity>0){//还有数量未分配 找同品名
					String chineseName=nameMap.get(pname);
					if(StringUtils.isNotBlank(chineseName)){
						Set<String> nameSet=Sets.newHashSet();
						for (Entry<String, String> temp: nameMap.entrySet()) {
							if(chineseName.equals(temp.getValue())){
								nameSet.add(temp.getKey());
							}
						}
						Set<String> supplierMap=map.keySet();
						if(nameSet.size()>0){
							for (String name: nameSet) {
								for (String supplier: supplierMap) {
									 if(invoiceMap.get(name)!=null&&invoiceMap.get(name).get(supplier)!=null){
										 List<PsiVatInvoiceInfo> psiVatInvoiceInfo=invoiceMap.get(name).get(supplier);
										 for (PsiVatInvoiceInfo info : psiVatInvoiceInfo) {
												Integer totalQuantity=0;
												if(quantityMap==null||quantityMap.get(info.getId())==null){
													 totalQuantity=info.getRemainingQuantity();
												}else{
													 totalQuantity=quantityMap.get(info.getId());
												}
												PsiVatInvoiceUseInfo useInfo=new PsiVatInvoiceUseInfo();
												if(quantity>0){
													if(totalQuantity>0&&totalQuantity>=quantity){
														useInfo.setCountryCode(item.getCountryCode());
														useInfo.setProductName(item.getProductName());
														useInfo.setInvoiceNo(info.getInvoiceNo());
														useInfo.setInvoice(psiVatInvoiceInfoService.get(info.getId()));
														useInfo.setItem(item);
														useInfo.setQuantity(quantity);
														useInfo.setCreateDate(new Date());
														useInfo.setCreateUser(UserUtils.getUser());
														useInfo.setDelFlag("0");
														quantityMap.put(info.getId(),totalQuantity-quantity);
														quantity=0;
														useInfoList.add(useInfo);
														flag=true;
														break;
													}else if(totalQuantity>0){
														useInfo.setCountryCode(item.getCountryCode());
														useInfo.setProductName(item.getProductName());
														useInfo.setInvoiceNo(info.getInvoiceNo());
														useInfo.setItem(item);
														useInfo.setQuantity(totalQuantity);
														useInfo.setInvoice(psiVatInvoiceInfoService.get(info.getId()));
														useInfo.setCreateDate(new Date());
														useInfo.setCreateUser(UserUtils.getUser());
														useInfo.setDelFlag("0");
														useInfoList.add(useInfo);
														quantity=quantity-totalQuantity;
														quantityMap.put(info.getId(),0);
													}
												}else{
													flag=true;
													break;
												}
											}
											if(flag){
												break;
											}
									 }
									 if(flag){
											break;
									 }
								}
								if(flag){
									break;
								}
							}
						}
					}
					
				}
			}else{//找同品名
				String chineseName=nameMap.get(pname);
				if(StringUtils.isNotBlank(chineseName)){
					Set<String> nameSet=Sets.newHashSet();
					for (Entry<String, String> temp: nameMap.entrySet()) {
						if(chineseName.equals(temp.getValue())){
							nameSet.add(temp.getKey());
						}
					}
					if(nameSet.size()>0){
						boolean flag=false;
						for (String name: nameSet) {
							map=invoiceMap.get(name);
							if(map!=null){
								for (Entry<String, List<PsiVatInvoiceInfo>> temp: map.entrySet()) {
									List<PsiVatInvoiceInfo> psiVatInvoiceInfo=temp.getValue();
									for (PsiVatInvoiceInfo info : psiVatInvoiceInfo) {
										Integer totalQuantity=0;
										if(quantityMap==null||quantityMap.get(info.getId())==null){
											 totalQuantity=info.getRemainingQuantity();
										}else{
											 totalQuantity=quantityMap.get(info.getId());
										}
										PsiVatInvoiceUseInfo useInfo=new PsiVatInvoiceUseInfo();
										if(quantity>0){
											if(totalQuantity>0&&totalQuantity>=quantity){
												useInfo.setCountryCode(item.getCountryCode());
												useInfo.setProductName(item.getProductName());
												useInfo.setInvoiceNo(info.getInvoiceNo());
												useInfo.setInvoice(psiVatInvoiceInfoService.get(info.getId()));
												useInfo.setItem(item);
												useInfo.setQuantity(quantity);
												useInfo.setCreateDate(new Date());
												useInfo.setCreateUser(UserUtils.getUser());
												useInfo.setDelFlag("0");
												quantityMap.put(info.getId(),totalQuantity-quantity);
												quantity=0;
												useInfoList.add(useInfo);
												flag=true;
												break;
											}else if(totalQuantity>0){
												useInfo.setCountryCode(item.getCountryCode());
												useInfo.setProductName(item.getProductName());
												useInfo.setInvoiceNo(info.getInvoiceNo());
												useInfo.setItem(item);
												useInfo.setQuantity(totalQuantity);
												useInfo.setCreateDate(new Date());
												useInfo.setCreateUser(UserUtils.getUser());
												useInfo.setDelFlag("0");
												useInfo.setInvoice(psiVatInvoiceInfoService.get(info.getId()));
												useInfoList.add(useInfo);
												quantity=quantity-totalQuantity;
												quantityMap.put(info.getId(),0);
											}
										}else{
											flag=true;
											break;
										}
									}
									if(flag){
										break;
									}
							   }
								if(flag){
									break;
								}
							}
					    }
					}	
				}
			}
		}
		
		if(useInfoList!=null&&useInfoList.size()>0){
			psiVatInvoiceInfoService.save(useInfoList);
		}
		if(quantityMap!=null&&quantityMap.size()>0){
			psiVatInvoiceInfoService.updateQuantity(quantityMap);
		}
		psiTransportOrderService.updateInvoiceFlag(id);
        addMessage(redirectAttributes, order.getTransportNo() + "自动分配发票号码成功");
        return "1";
	}
	

	@ResponseBody
	@RequestMapping(value = {"saveInvoice"})
	public Integer saveOrderItem(Integer inoviceId,Integer quantity,Integer orderItemId,Integer id,String country,String name) {
			PsiVatInvoiceUseInfo useInfo=new PsiVatInvoiceUseInfo();
			useInfo.setId(id);
			useInfo.setDelFlag("0");
			useInfo.setCreateDate(new Date());
			useInfo.setCreateUser(UserUtils.getUser());
			useInfo.setCountryCode(country);
			useInfo.setProductName(name);
			useInfo.setItem(new LcPsiTransportOrderItem(orderItemId));
			useInfo.setInvoice(new PsiVatInvoiceInfo(inoviceId));
			useInfo.setQuantity(quantity);
			psiVatInvoiceInfoService.save(useInfo);
			psiVatInvoiceInfoService.updateQuantity(quantity,inoviceId);
			return useInfo.getId();
	}
	
	@ResponseBody
	@RequestMapping(value = {"deleteItem"})
	public String deleteItem(Integer inoviceId,Integer itemId,Integer quantity) {
		try {
			PsiVatInvoiceUseInfo useInfo=psiVatInvoiceInfoService.getById(itemId);
			useInfo.setDelFlag("1");
			psiVatInvoiceInfoService.save2(useInfo);
			return psiVatInvoiceInfoService.updateQuantity2(quantity,inoviceId);
		} catch (Exception e) {
			return "0";
		}
	}
	
	
	@RequestMapping(value = {"exportInvoice"})
	public String exportInvoice(Integer id, RedirectAttributes redirectAttributes, Model model, HttpServletRequest request,HttpServletResponse response) {
		    LcPsiTransportOrder order=psiTransportOrderService.get(id);
		    HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	
			String[] title = { "No.","产品名","颜色","国家","数量"};
			
			
			  style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
			  style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
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

			int excelNo=1;
			int i=1;
			for (LcPsiTransportOrderItem item : order.getItems()) {
				 int j=0;
				 row = sheet.createRow(excelNo++);
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(i++);
				 row.getCell(j-1).setCellStyle(style);
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getProductName());
				 row.getCell(j-1).setCellStyle(style);
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getColorCode());
				 row.getCell(j-1).setCellStyle(style);
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getCountryCode());
				 row.getCell(j-1).setCellStyle(style);
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantity());
				 row.getCell(j-1).setCellStyle(style);
				 row = sheet.createRow(excelNo++);
				 j=0;
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("发票号");
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("产品名");
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("供应商");
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("分配数量");
				 
				 for (PsiVatInvoiceUseInfo info : item.getInvoices()) {
					 row = sheet.createRow(excelNo++);
					 j=0;
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(info.getInvoice().getInvoiceNo());
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(info.getInvoice().getProductName());
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(info.getInvoice().getSupplierName());
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(info.getQuantity());
				 }
			}
			for (int k= 0; k< title.length; k++) {
				sheet.autoSizeColumn((short)k, true);
			}
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
			
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			
				String fileName = order.getTransportNo() + sdf.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value =  "expAllTransport" )
	public String expAllTransport(LcPsiTransportOrder psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		    psiTransportOrder.setFromStore(new Stock(130));
		    List<LcPsiTransportOrder> list=this.psiTransportOrderService.exp(psiTransportOrder); 
		    if(list.size()>65535){
		    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
		    }
		    Map<String,String> nameMap=productService.findChineseName();
		    
		    HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	
			String[] title = {"运单号","产品型号","国家","产品中文名称","数量","出口单价","出口总金额","进口金额","进口总金额","对应发票号","对应发票数量","报关时间","到库时间"};
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
			
			  style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
			  style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
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
			  
			  HSSFCellStyle cellStyle = wb.createCellStyle();
		      cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		        
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
				int excelNo =1;
			//{"运单号","产品型号","产品中文名称","数量","出口单价","出口总金额","对应发票号","对应发票数量"};
			 for(LcPsiTransportOrder order: list){
				 for (LcPsiTransportOrderItem item: order.getItems()) {
					  String pname=item.getProductName();
					  if(pname.endsWith("US")||pname.endsWith("JP")||pname.endsWith("UK")||pname.endsWith("EU")||pname.endsWith("DE")){
							pname=pname.replace("US","").replace("JP","").replace("UK","").replace("EU","").replace("DE","");
					  }
				 	  if(item.getInvoices()==null||item.getInvoices().size()==0){
				 		 row = sheet.createRow(excelNo++);
				 		 int i =0;
				 		 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(order.getTransportNo());
				 		 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getNameWithColor());
				 		 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getCountryCode());
				 		 if(nameMap.get(pname)!=null){
				 			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(nameMap.get(pname));
				 		 }else{
				 			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				 		 }
				 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantity());
				 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getLowerPrice()==null?0:item.getLowerPrice());
				 		row.getCell(i-1).setCellStyle(cellStyle);
				 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantity()*(item.getLowerPrice()==null?0:item.getLowerPrice()));
				 		row.getCell(i-1).setCellStyle(cellStyle);
				 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getImportPrice()==null?0:item.getImportPrice());
				 		row.getCell(i-1).setCellStyle(cellStyle);
				 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantity()*(item.getImportPrice()==null?0:item.getImportPrice()));
				 		row.getCell(i-1).setCellStyle(cellStyle);
				 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");//operArrivalDate
				 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(order.getExportDate()==null?"":dateFormat.format(order.getExportDate()));
				 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(order.getOperArrivalDate()==null?"":dateFormat.format(order.getOperArrivalDate()));
				 		
				 	  }else{
				 		  for (PsiVatInvoiceUseInfo info : item.getInvoices()) {
				 			 row = sheet.createRow(excelNo++);
				 			 int i =0;
				 			 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(order.getTransportNo());
				 			 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getNameWithColor());
				 			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getCountryCode());
				 			 if(nameMap.get(pname)!=null){
						 			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(nameMap.get(pname));
						 	 }else{
						 			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						 	 }
				 			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantity());
				 			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getLowerPrice()==null?0:item.getLowerPrice());
				 			row.getCell(i-1).setCellStyle(cellStyle);
				 			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantity()*(item.getLowerPrice()==null?0:item.getLowerPrice()));
				 			row.getCell(i-1).setCellStyle(cellStyle);
				 			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getImportPrice()==null?0:item.getImportPrice());
					 		row.getCell(i-1).setCellStyle(cellStyle);
					 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantity()*(item.getImportPrice()==null?0:item.getImportPrice()));
					 		row.getCell(i-1).setCellStyle(cellStyle);
				 			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(info.getInvoice().getInvoiceNo());
					 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(info.getQuantity());
					 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(order.getExportDate()==null?"":dateFormat.format(order.getExportDate()));
					 		row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(order.getOperArrivalDate()==null?"":dateFormat.format(order.getOperArrivalDate()));
						  }
				 	  }
				 }
		    }
			 
		for (int k= 0; k< title.length; k++) {
			sheet.autoSizeColumn((short)k, true);
		}	
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
	
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	
		String fileName = "Data" + sdf.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value =  "genTransFromFba" )
	public String genTransFromFba(Integer id, HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
		FbaInbound fbaInbound=fbaInBoundService.get(id);
		Map<String,String> skuMap=amazonProduct2Service.findNameColorBySku(fbaInbound.getCountry());
		Map<String,Map<String,PsiProductEliminate>> eliminateMap = productEliminateService.findAllByNameAndCountry();
		Map<Integer, String>  productMap=productService.getVomueAndWeight();
		Map<String,Integer> packQuantityMap=productService.getPackQuantity();
		LcPsiTransportOrder order=new LcPsiTransportOrder();
		if("JP".equals(fbaInbound.getShipFromAddress())){
			order.setFromStore(stockService.get(147));
		}else if("US".equals(fbaInbound.getShipFromAddress())){
			order.setFromStore(stockService.get(120));
		}else{
			order.setFromStore(stockService.get(19));
		}
		Stock stock=stockService.findBySign(fbaInbound.getDestinationFulfillmentCenterId());
		order.setToStore(stock);
		order.setOrgin("");
		order.setDestination("");
		order.setModel("4");
		order.setTransportType("1");
		
		order.setPaymentSta("0");//未付款状态
		order.setCreateDate(new Date());
		order.setCreateUser(UserUtils.getUser());
		if(stock.getPlatform().contains("de")){
			order.setToCountry("de");
		}else if(stock.getPlatform().contains("uk")){
			order.setToCountry("uk");
		}else if(stock.getPlatform().contains("fr")){
			order.setToCountry("fr");
		}else if(stock.getPlatform().contains("it")){
			order.setToCountry("it");
		}else if(stock.getPlatform().contains("es")){
			order.setToCountry("es");
		}else if(stock.getPlatform().contains("com")){
			order.setToCountry("com");
		}else if(stock.getPlatform().contains("ca")){
			order.setToCountry("ca");
		}else if(stock.getPlatform().contains("jp")){
			order.setToCountry("jp");
		}else if(stock.getPlatform().contains("mx")){
			order.setToCountry("mx");
		}else{
			order.setToCountry("");
		}
		order.setDestinationDetail(null);
		order.setPickUpDate(fbaInbound.getShippedDate());
		
		if(fbaInbound.getArrivalDate()==null){
			order.setTransportSta("1");//草稿状态
		}else{
			order.setTransportSta("5");//草稿状态
		}
		order.setIsCount("0");
		order.setShipmentId(fbaInbound.getShipmentId());
		order.setFbaInboundId(fbaInbound.getId()+"");
		List<LcPsiTransportOrderItem> orderItems=Lists.newArrayList();
		Float volume =0f;
		Float weight=0f;
		Integer boxNum=0;
		for (FbaInboundItem item : fbaInbound.getItems()) {
			LcPsiTransportOrderItem tranOrderItem=new LcPsiTransportOrderItem();
			String tempName=skuMap.get(item.getSku());
			if(tempName==null){
				continue;
			}
			String name="";
			String color="";
			if(tempName.indexOf("_")>0){
				name = tempName.substring(0,tempName.indexOf("_"));
				color = tempName.substring(tempName.indexOf("_")+1);
			}else{
				name=tempName;
			}
			tranOrderItem.setProductName(name);
			tranOrderItem.setColorCode(color);
			tranOrderItem.setCountryCode(fbaInbound.getCountry());
			tranOrderItem.setSku(item.getSku());
			tranOrderItem.setQuantity(item.getQuantityShipped());
			tranOrderItem.setShippedQuantity(item.getQuantityShipped());
			if(fbaInbound.getArrivalDate()!=null){
				tranOrderItem.setReceiveQuantity(item.getQuantityShipped());
			}
			
			tranOrderItem.setOfflineSta("0");
			tranOrderItem.setFbaFlag("1");
			tranOrderItem.setFbaInboundId(fbaInbound.getId());
			Integer pack=1;
			if(item.getPackQuantity()==null){
				pack=packQuantityMap.get(tempName);
				if(name.contains("Inateck DB1001")){
					if("com,uk,jp,ca,mx,".contains(fbaInbound.getCountry())||fbaInbound.getCountry().startsWith("com")){
						pack=60;
					}else{
						pack=44;
					}
				}else if(name.contains("Inateck DB2001")){
					if("com,jp,ca,mx,".contains(fbaInbound.getCountry())||fbaInbound.getCountry().startsWith("com")){
						pack=32;
					}else{
						pack=24;
					}
				}
			}else{
				pack=item.getPackQuantity();
			}
			
			tranOrderItem.setPackQuantity(pack);
			PsiProduct product=productService.findProductByProductName(name);
			tranOrderItem.setProduct(product);
			String res =psiTransportOrderService.getProPriceByProductId(product.getId());
			Float 	price =0f;
			Float   productPrice=0f;
			String  currency="";
			String countryCode = tranOrderItem.getCountryCode();
			if(StringUtils.isNotEmpty(res)){
				String[] arr=res.split("_");
				price=Float.parseFloat(arr[0].toString());
				Integer taxRefund=(product.getTaxRefund()==null?17:product.getTaxRefund());
				productPrice =Float.parseFloat(arr[0].toString())/(1+(taxRefund/100));
				if("CNY".equals(arr[1])){
					productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY");
					price=price/AmazonProduct2Service.getRateConfig().get("USD/CNY");
				}
				//理诚全部是人民币
				if("it,de,es,fr,uk".contains(countryCode)){
					currency="EUR";
					price=price/AmazonProduct2Service.getRateConfig().get("EUR/USD");
					productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("EUR/USD");
				}else if("com,mx,ca".contains(countryCode)||countryCode.startsWith("com")){
					currency="USD";
				}else if("jp".contains(countryCode)){
					currency="JPY";
					price=price*AmazonProduct2Service.getRateConfig().get("USD/JPY");
				}
				
			}
			
			tranOrderItem.setProductPrice(productPrice);
			tranOrderItem.setItemPrice(price);
			if(eliminateMap.get(tempName)!=null&&eliminateMap.get(tempName).get(fbaInbound.getCountry())!=null){
				tranOrderItem.setLowerPrice(eliminateMap.get(tempName).get(fbaInbound.getCountry()).getCnpiPrice());
				tranOrderItem.setImportPrice(eliminateMap.get(tempName).get(fbaInbound.getCountry()).getPiPrice());
			}
			
			tranOrderItem.setCurrency(currency);
			tranOrderItem.setTransportOrder(order);
			
			orderItems.add(tranOrderItem);
			if(!"1".equals(product.getComponents())){
				volume+=item.getQuantityShipped()*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[0]));
				weight+=item.getQuantityShipped()*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[1]));
				boxNum+=MathUtils.roundUp(item.getQuantityShipped()*1.0d/pack);
			}
		}
		order.setVolume(volume);
		order.setWeight(weight);
		order.setBoxNumber(boxNum);
		order.setItems(orderItems);
		String transportNo =psiTransportOrderService.createFlowNo();
		order.setTransportNo(transportNo);
		psiTransportOrderService.save(order);
		return "1";
	}	
	
	
	@RequestMapping(value = "uploadItemFile")
	public String uploadItemFile(MultipartFile uploadItemFile,Integer id,String country,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws Exception {

		Workbook workBook = WorkbookFactory.create(uploadItemFile.getInputStream());
		Sheet sheet = workBook.getSheetAt(0);
		sheet.setForceFormulaRecalculation(true);
		// 循环行Row
		LcPsiTransportOrder order=psiTransportOrderService.get(id);
		List<LcPsiTransportOrderItem> items=order.getItems();
		Map<String,String> skuMap=amazonProduct2Service.findNameColorBySku2(country);
		Map<String,Map<String,PsiProductEliminate>> eliminateMap = productEliminateService.findAllByNameAndCountry();
		Map<Integer, String>  productMap=productService.getVomueAndWeight();
		Map<String,Integer> packQuantityMap=productService.getPackQuantity();
		Float volume =0f;
		Float weight=0f;
		Integer boxNum=0;
		for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
			LcPsiTransportOrderItem tranOrderItem=new LcPsiTransportOrderItem();
			tranOrderItem.setTransportOrder(order);
			Row row = sheet.getRow(rowNum);
			if (row == null||row.getCell(0)==null||StringUtils.isBlank(getData(row.getCell(0)))) {
				continue;
			}
			String tempName=getData(row.getCell(0)).trim();
			Integer quantity=Integer.parseInt(getData(row.getCell(1)));
			 
			String name="";
			String color="";
			if(tempName.indexOf("_")>0){
				name = tempName.substring(0,tempName.indexOf("_"));
				color = tempName.substring(tempName.indexOf("_")+1);
			}else{
				name=tempName;
			}
			tranOrderItem.setProductName(name);
			tranOrderItem.setColorCode(color);
			tranOrderItem.setCountryCode(country);
			if(skuMap.get(tempName)!=null){
				tranOrderItem.setSku(skuMap.get(tempName));
			}else{
				tranOrderItem.setSku("");
			}
			
			tranOrderItem.setQuantity(quantity);
			if("3".equals(order.getTransportType())){
				tranOrderItem.setOfflineSta("1");
			}else{
				tranOrderItem.setOfflineSta("0");
			}
			
			Integer pack=packQuantityMap.get(tempName);
			if(name.contains("Inateck DB1001")){
					if("com,uk,jp,ca,mx,".contains(country)||country.startsWith("com")){
						pack=60;
					}else{
						pack=44;
					}
			}else if(name.contains("Inateck DB2001")){
					if("com,jp,ca,mx,".contains(country)||country.startsWith("com")){
						pack=32;
					}else{
						pack=24;
					}
			}
			
			tranOrderItem.setPackQuantity(pack);
			PsiProduct product=productService.findProductByProductName(name);
			tranOrderItem.setProduct(product);
			String res =psiTransportOrderService.getProPriceByProductId(product.getId());
			Float 	price =0f;
			Float   productPrice=0f;
			String  currency="";
			String countryCode = tranOrderItem.getCountryCode();
			if(StringUtils.isNotEmpty(res)){
				String[] arr=res.split("_");
				price=Float.parseFloat(arr[0].toString());
				Integer taxRefund=(product.getTaxRefund()==null?17:product.getTaxRefund());
				productPrice =Float.parseFloat(arr[0].toString())/(1+(taxRefund/100));
				if("CNY".equals(arr[1])){
					productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY");
					price=price/AmazonProduct2Service.getRateConfig().get("USD/CNY");
				}
				//理诚全部是人民币
				if("it,de,es,fr,uk".contains(countryCode)){
					currency="EUR";
					price=price/AmazonProduct2Service.getRateConfig().get("EUR/USD");
					productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("EUR/USD");
				}else if("com,mx,ca".contains(countryCode)||countryCode.startsWith("com")){
					currency="USD";
				}else if("jp".contains(countryCode)){
					currency="JPY";
					price=price*AmazonProduct2Service.getRateConfig().get("USD/JPY");
				}
				
			}
			
			tranOrderItem.setProductPrice(productPrice);
			tranOrderItem.setItemPrice(price);
			if(eliminateMap.get(tempName)!=null&&eliminateMap.get(tempName).get(country)!=null){
				tranOrderItem.setLowerPrice(eliminateMap.get(tempName).get(country).getCnpiPrice());
				tranOrderItem.setImportPrice(eliminateMap.get(tempName).get(country).getPiPrice());
			}
			
			tranOrderItem.setCurrency(currency);
			tranOrderItem.setTransportOrder(order);
			
			if(!"1".equals(product.getComponents())){
				volume+=quantity*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[0]));
				weight+=quantity*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[1]));
				boxNum+=MathUtils.roundUp(quantity*1.0d/pack);
			}
			items.add(tranOrderItem);
		}	
		order.setVolume(volume);
		order.setWeight(weight);
		order.setBoxNumber(boxNum);
		psiTransportOrderService.save(order);
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}	
	
	
	private static SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy/MM/dd");
	public String getData(Cell cell){
		String value="";
		if(cell!=null){

			switch (cell.getCellType()) {
		        case HSSFCell.CELL_TYPE_NUMERIC: // 数字
		            value = cell.getNumericCellValue() + "";
		            if (HSSFDateUtil.isCellDateFormatted(cell)) {
		                Date date = cell.getDateCellValue();
		                try{
		                	 value = dateFormat.format(date);
		                }catch(Exception e){
		                	 value = dateFormat.format(dateFormat2.format(date));
		                }
		               
		             } else {
		            	 value = new DecimalFormat("0.00").format(cell.getNumericCellValue());
		                 value=value.replace(".00","");
		             }
		            break;
		        case HSSFCell.CELL_TYPE_STRING: // 字符串
		            value = cell.getStringCellValue();
		            break;
		        case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
		            value = cell.getBooleanCellValue() + "";
		            break;
		        case HSSFCell.CELL_TYPE_FORMULA: // 公式
		            value = new DecimalFormat("0.00").format(cell.getNumericCellValue());
	                value=value.replace(".00","");
		            break;
		        case HSSFCell.CELL_TYPE_BLANK: // 空值
		            value = "";
		            break;
		        case HSSFCell.CELL_TYPE_ERROR: // 故障
		            value = "";//非法字符
		            break;
		        default:
		            value = "";//未知类型
		            break;
		        }
		}
		return value;
	}
	
	
	@RequestMapping(value =  "expFeeByYear" )
	public String expFeeByYear(String year, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		    Map<String,Map<String,Map<String,Map<String,Float>>>> map=psiTransportOrderService.findTransportFee(year);
		    HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet(year+"年");
			
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	
			List<String> title=Lists.newArrayList("国家","供应商","货币单位","类目");
			for(int i=1;i<=12;i++){
				if(i<10){
					title.add(year+"0"+i);
				}else{
					title.add(year+i);
				}
			}
			
			  style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
			  style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
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
			  
			  HSSFCellStyle cellStyle = wb.createCellStyle();
		      cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		        
			  //设置字体
			  HSSFFont font = wb.createFont();
			  font.setFontHeightInPoints((short) 16); // 字体高度
			  font.setFontName(" 黑体 "); // 字体
			  font.setBoldweight((short) 16);
			  style.setFont(font);
			  row.setHeight((short) 600);
			  HSSFCell cell = null;		
			  for (int i = 0; i < title.size(); i++) {
					cell = row.createCell(i);
					cell.setCellValue(title.get(i));
					cell.setCellStyle(style);
					sheet.autoSizeColumn((short) i);
				}
				int excelNo =1;
			
			 for(Map.Entry<String,Map<String,Map<String,Map<String,Float>>>> entry: map.entrySet()){
				 String country=entry.getKey();
				 Map<String,Map<String,Map<String,Float>>> temp=entry.getValue();
				 for (Map.Entry<String,Map<String,Map<String,Float>>> supplierEntry: temp.entrySet()) {
					  String supplier = supplierEntry.getKey();
					  Map<String,Map<String,Float>> supplierTemp = supplierEntry.getValue();
					  for (Map.Entry<String,Map<String,Float>> typeEntry: supplierTemp.entrySet()) {
						  String typeNum = typeEntry.getKey();
						  Map<String,Float> finalMap = typeEntry.getValue();
						  String type = ("0".equals(typeNum)?"前端费用":("1".equals(typeNum)?"后端杂费":"税金"));
						  row = sheet.createRow(excelNo++);
						  int i =0;
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(country);
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(supplier.substring(0,supplier.lastIndexOf("-")));
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(supplier.substring(supplier.lastIndexOf("-")+1));
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(type);
						  
						  for (int k= 4; k< title.size(); k++) {
							  if(finalMap.get(title.get(k))!=null){
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(finalMap.get(title.get(k)));
								  row.getCell(i-1).setCellStyle(cellStyle);
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
								  row.getCell(i-1).setCellStyle(cellStyle);
							  }
						  }
					  }
				 }
		    }
		
	    
		 
		for (int k= 0; k< title.size(); k++) {
			sheet.autoSizeColumn((short)k, true);
		}	
		
		
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
	
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	
		String fileName = year+ sdf.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value = "uploadRateFile")
	public String uploadRateFile(@RequestParam("excel")MultipartFile excelFile,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
				Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null||row.getCell(0)==null) {
						continue;
					}
					String month= StringUtils.trim(row.getCell(0).getStringCellValue());
				    
				    double usdCny=row.getCell(1).getNumericCellValue();
				    double eurCny=row.getCell(2).getNumericCellValue();
				    double jpyCny=row.getCell(3).getNumericCellValue();
				    psiTransportOrderService.saveRateData(month,usdCny,eurCny,jpyCny);
				}
				
		} catch (Exception e) {
			addMessage(redirectAttributes, "上传文件失败");
			logger.error("文件上传失败",e);
		}
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportOrder/?repage";
	}
	
	
	@RequestMapping(value =  "expDeclareByYear" )
	public String expDeclareByYear(String year, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		    Map<String,List<Object[]>> map=psiTransportOrderService.findDeclare(year);
		    Map<String,Object[]> rateMap =psiTransportOrderService.findRate(year);
		    Map<String,Map<String,Float>> cnMap=psiTransportOrderService.findCnPrice(year);
		    Map<String,Map<String,Float>> importMap= psiTransportOrderService.findImportPrice(year);
		    
		    HSSFWorkbook wb = new HSSFWorkbook();
		    
			
		    for (Map.Entry<String,List<Object[]>> entry: map.entrySet()) {
		    	 HSSFSheet sheet = wb.createSheet(entry.getKey());
		    	
		    	 HSSFRow row = sheet.createRow(0);
					HSSFCellStyle style = wb.createCellStyle();
					style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
					
					  style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
					  style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
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
					  
					  HSSFCellStyle cellStyle = wb.createCellStyle();
				      cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
				      
				      HSSFCellStyle cellStyle1 = wb.createCellStyle();
				      cellStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0000"));
				        
					  //设置字体
					  HSSFFont font = wb.createFont();
					  font.setFontHeightInPoints((short) 16); // 字体高度
					  font.setFontName(" 黑体 "); // 字体
					  font.setBoldweight((short) 16);
					  style.setFont(font);
					  row.setHeight((short) 600);
					  HSSFCell cell = null;		
					  List<String> title=Lists.newArrayList("出口月","出口日期","报关单号","合同协议号","件数","毛重","体积","运输方式","目的国","出口报关币种","出口报关金额","出口报关金额/CNY","出口报关金额/USD");
					 
					   if(entry.getKey().equals("DE")){
						  title.add("HK出口金额/EUR");
						  title.add("HK出口金额/USD");
					   }else if(entry.getKey().equals("JP")){
						   title.add("HK出口金额/JPY");
						   title.add("HK出口金额/USD");
					   }else if(entry.getKey().equals("US")){
						   title.add("HK出口金额/USD");
					   }else{
						   title.add("HK出口金额");
					   }
					  for (int i = 0; i < title.size(); i++) {
							cell = row.createCell(i);
							cell.setCellValue(title.get(i));
							cell.setCellStyle(style);
							sheet.autoSizeColumn((short) i);
					  }
					  int excelNo =1;
					  
					//"出口月","出口日期","报关单号","合同协议号","件数","毛重","体积","运输方式","目的国","出口报关币种","出口报关金额","HK出口金额/USD"
					for (Object[] obj: entry.getValue()) {
						row = sheet.createRow(excelNo++);
						int i =0;
						row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(obj[1].toString());
						row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(obj[2].toString());	
						row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(obj[3]==null?"":obj[3].toString());
						row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(obj[4].toString());
						row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[5].toString()));
						row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(Float.parseFloat(obj[6].toString()));
						row.getCell(i-1).setCellStyle(cellStyle);
						row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(Float.parseFloat(obj[7].toString()));
						row.getCell(i-1).setCellStyle(cellStyle);
						row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(obj[8].toString());
						row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(obj[9].toString());
						
						float declare = obj[10]==null?0:Float.parseFloat(obj[10].toString());
						float importPrice = obj[11]==null?0:Float.parseFloat(obj[11].toString());
						float rate = ((BigDecimal) rateMap.get(obj[1].toString())[1]).floatValue();
						float rate1 = ((BigDecimal) rateMap.get(obj[1].toString())[2]).floatValue();
						float rate2 = ((BigDecimal) rateMap.get(obj[1].toString())[3]).floatValue();
						
						float cnyDeclare=0f;
						float usdDeclare=0f;
						if(Integer.parseInt(obj[4].toString().split("_")[0])>=20180401){
							cnyDeclare= declare;
							usdDeclare = declare/rate;
							row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("CNY");
						}else{
							cnyDeclare= declare*rate;
							usdDeclare = declare;
							row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("USD");
						}
						row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(declare);
						row.getCell(i-1).setCellStyle(cellStyle);
						
						row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(cnyDeclare);
						row.getCell(i-1).setCellStyle(cellStyle);
						row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(usdDeclare);
						row.getCell(i-1).setCellStyle(cellStyle);
						
						 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(importPrice);
						 row.getCell(i-1).setCellStyle(cellStyle);
						 if(entry.getKey().equals("DE")){
							 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(importPrice*rate1/rate);
						 }else if(entry.getKey().equals("JP")){
							 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(importPrice*rate2/rate);
						 }
						 row.getCell(i-1).setCellStyle(cellStyle);
					}
					
				   for (int k= 0; k< title.size(); k++) {
				 	  sheet.autoSizeColumn((short)k, true);
				   }	
				
				   
			}
		    
		    
		        HSSFSheet sheet = wb.createSheet(year+" total");
	    	
	    	    HSSFRow row = sheet.createRow(0);
				HSSFCellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
				
				  style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
				  style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
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
				  
				  HSSFCellStyle cellStyle = wb.createCellStyle();
			      cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
			      
			      HSSFCellStyle cellStyle1 = wb.createCellStyle();
			      cellStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0000"));
			        
				  //设置字体
				  HSSFFont font = wb.createFont();
				  font.setFontHeightInPoints((short) 16); // 字体高度
				  font.setFontName(" 黑体 "); // 字体
				  font.setBoldweight((short) 16);
				  style.setFont(font);
				  row.setHeight((short) 600);
				  HSSFCell cell = null;		
				  
				  List<String> cnTitle=Lists.newArrayList("CN出口","币种");
				  for(int i=1;i<=12;i++){
						if(i<10){
							cnTitle.add(year+"0"+i);
						}else{
							cnTitle.add(year+i);
						}
				  }
 				  for (int i = 0; i < cnTitle.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(cnTitle.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
				  }
 				 cell = row.createCell(cnTitle.size());
				 cell.setCellValue("TOTAL");
				 cell.setCellStyle(style);
				 sheet.autoSizeColumn((short) cnTitle.size());
					
 				  int excelNo=1;
 				  for ( Map.Entry<String,Map<String,Float>> entry: cnMap.entrySet()) {
					   String type = entry.getKey();
					   Map<String,Float> typeMap = entry.getValue();
					   row = sheet.createRow(excelNo++);
					   int i =0;
					   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(type);
					   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("USD");
					   float total=0f;
					   for (int k = 2; k < cnTitle.size(); k++) {
						   if(typeMap.get(cnTitle.get(k))!=null){
							   Float price=typeMap.get(cnTitle.get(k));
							   if(Integer.parseInt(cnTitle.get(k))>=201804){//cny
								   float rate = ((BigDecimal) rateMap.get(cnTitle.get(k))[1]).floatValue();
								   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price/rate);
								   total+=price/rate;
							   }else{
								   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price);
								   total+=price;
							   }
							   row.getCell(i-1).setCellStyle(cellStyle);
						   }else{
							   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						   }
					   }
					   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(total);
					   row.getCell(i-1).setCellStyle(cellStyle);
				  }
				  
 				 row = sheet.createRow(excelNo++);
 				 for (int i = 0; i < cnTitle.size(); i++) {
					cell = row.createCell(i);
					cell.setCellValue(cnTitle.get(i));
					cell.setCellStyle(style);
					sheet.autoSizeColumn((short) i);
			     }
 				 cell = row.createCell(cnTitle.size());
				 cell.setCellValue("TOTAL");
				 cell.setCellStyle(style);
				 sheet.autoSizeColumn((short) cnTitle.size());
 				 for ( Map.Entry<String,Map<String,Float>> entry: cnMap.entrySet()) {
					   String type = entry.getKey();
					   Map<String,Float> typeMap = entry.getValue();
					   row = sheet.createRow(excelNo++);
					   int i =0;
					   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(type);
					   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("CNY");
					   float total=0f;
					   for (int k = 2; k < cnTitle.size(); k++) {
						   if(typeMap.get(cnTitle.get(k))!=null){
							   Float price=typeMap.get(cnTitle.get(k));
							   if(Integer.parseInt(cnTitle.get(k))<201804){//usd
								   float rate = ((BigDecimal) rateMap.get(cnTitle.get(k))[1]).floatValue();
								   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price*rate);
								   total+=price*rate;
							   }else{
								   total+=price;
								   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price);
							   }
							   row.getCell(i-1).setCellStyle(cellStyle);
						   }else{
							   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						   }
					   }
					   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(total);
					   row.getCell(i-1).setCellStyle(cellStyle);
				  }
 				  
 				row = sheet.createRow(excelNo++);
 				 List<String> importTitle=Lists.newArrayList("HK出口","币种");
				  for(int i=1;i<=12;i++){
						if(i<10){
							importTitle.add(year+"0"+i);
						}else{
							importTitle.add(year+i);
						}
				  }
				  for (int i = 0; i < importTitle.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(importTitle.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
				  }
				  cell = row.createCell(importTitle.size());
					 cell.setCellValue("TOTAL");
					 cell.setCellStyle(style);
					 sheet.autoSizeColumn((short) importTitle.size());
 				 for ( Map.Entry<String,Map<String,Float>> entry: importMap.entrySet()) {
					   String type = entry.getKey();
					   Map<String,Float> typeMap = entry.getValue();
					   row = sheet.createRow(excelNo++);
					   int i =0;
					   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(type);
					   String currency="";
					   if(type.contains("DE")){
						   currency="EUR";
					   }else if(type.contains("JP")){
						   currency="JPY";
					   }else if(type.contains("US")){
						   currency="USD";
					   }
					   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(currency);
					   float total=0f;
					   for (int k = 2; k < cnTitle.size(); k++) {
						   if(typeMap.get(cnTitle.get(k))!=null){
							   Float price=typeMap.get(cnTitle.get(k));
							   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price);
							   total+=price;
							   row.getCell(i-1).setCellStyle(cellStyle);
						   }else{
							   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						   }
					   }
					   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(total);
					   row.getCell(i-1).setCellStyle(cellStyle);
				  }
				  
 				 
 				 
 				 row = sheet.createRow(excelNo++);
				 
				  for (int i = 0; i < importTitle.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(importTitle.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
				  }
				  cell = row.createCell(importTitle.size());
					 cell.setCellValue("TOTAL");
					 cell.setCellStyle(style);
					 sheet.autoSizeColumn((short) importTitle.size());
				 for ( Map.Entry<String,Map<String,Float>> entry: importMap.entrySet()) {
					   String type = entry.getKey();
					   Map<String,Float> typeMap = entry.getValue();
					   row = sheet.createRow(excelNo++);
					   int i =0;
					   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(type);
					   String currency="";
					   if(type.contains("DE")){
						   currency="CNY";
					   }else if(type.contains("JP")){
						   currency="CNY";
					   }else if(type.contains("US")){
						   currency="CNY";
					   }
					   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(currency);
					   float total=0f;
					   for (int k = 2; k < cnTitle.size(); k++) {
						   float rate=1f;
						   try{
							   if(type.contains("DE")){
								   rate = ((BigDecimal) rateMap.get(cnTitle.get(k))[2]).floatValue();
							   }else if(type.contains("JP")){
								   rate = ((BigDecimal) rateMap.get(cnTitle.get(k))[3]).floatValue();
							   }else if(type.contains("US")){
								   rate = ((BigDecimal) rateMap.get(cnTitle.get(k))[1]).floatValue();
							   }
						   }catch(Exception e){}
						  
						   if(typeMap.get(cnTitle.get(k))!=null){
							   Float price=typeMap.get(cnTitle.get(k));
							   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price*rate);
							   total+=price*rate;
							   row.getCell(i-1).setCellStyle(cellStyle);
						   }else{
							   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						   }
					   }
					   row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(total);
					   row.getCell(i-1).setCellStyle(cellStyle);
				  }
				 
				 
 				 
 				 row = sheet.createRow(excelNo++);
				List<String> rateTitle=Lists.newArrayList("币种");
				 for(int i=1;i<=12;i++){
						if(i<10){
							rateTitle.add(year+"0"+i);
						}else{
							rateTitle.add(year+i);
						}
				  }
				 for (int i = 0; i < rateTitle.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(rateTitle.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
				  }
				 
				 
				 row = sheet.createRow(excelNo++);
				 int i =0;
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("USD-CNY");
				 for (int k = 1; k < rateTitle.size(); k++) {
					 if(rateMap.get(rateTitle.get(k))!=null){
						 float rate = ((BigDecimal) rateMap.get(rateTitle.get(k))[1]).floatValue();
						 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(rate);
						 row.getCell(i-1).setCellStyle(cellStyle1);
					 }else{
						 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }
					
				 }
				 row = sheet.createRow(excelNo++);
				 i =0;
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("EUR-CNY");
				 for (int k = 1; k < rateTitle.size(); k++) {
					 if(rateMap.get(rateTitle.get(k))!=null){
						 float rate = ((BigDecimal) rateMap.get(rateTitle.get(k))[2]).floatValue();
						 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(rate);
						 row.getCell(i-1).setCellStyle(cellStyle1);
					 }else{
						 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }
					
				 }
				 row = sheet.createRow(excelNo++);
				 i =0;
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("JPY-CNY");
				 for (int k = 1; k < rateTitle.size(); k++) {
					 if(rateMap.get(rateTitle.get(k))!=null){
						 float rate = ((BigDecimal) rateMap.get(rateTitle.get(k))[3]).floatValue();
						 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(rate);
						 row.getCell(i-1).setCellStyle(cellStyle1);
					 }else{
						 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }
					
				 }
				 
		 
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
	
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	
		String fileName = year+ sdf.format(new Date()) + ".xls";
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
	
}
