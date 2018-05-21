package com.springrain.erp.modules.amazoninfo.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.config.HsCode;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct2;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProductTypeCharge;
import com.springrain.erp.modules.amazoninfo.entity.ProductPrice;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.ProductPriceService;
import com.springrain.erp.modules.amazoninfo.service.SettlementReportOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.MfnOrderService;
import com.springrain.erp.modules.psi.entity.PsiProductTransportRate;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiTransportPaymentService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 亚马逊产品Controller
 * @author tim
 * @version 2014-06-04
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/amazonProduct")
public class AmazonProductController extends BaseController {

	@Autowired
	private AmazonProductService amazonProductService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private AmazonPostsDetailService amazonPostsDetailService;
	
	@Autowired
	private ProductPriceService productPriceService;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	@Autowired
	private PsiTransportPaymentService psiTransportPaymentService;

	@Autowired
	private SettlementReportOrderService settlementReportOrderService;
	
	@Autowired
	private MfnOrderService 		mfnOrderService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	
	@ModelAttribute
	public AmazonProduct get(@RequestParam(required=false) String id) {
		if (!StringUtils.isBlank(id)){
			return amazonProductService.get(id);
		}else{
			return new AmazonProduct();
		}
	}
	
	@RequiresPermissions("amazoninfo:amazonProduct:view")
	@RequestMapping(value = {"list", ""})
	public String list(AmazonProduct amazonProduct, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<AmazonProduct> page = amazonProductService.find(new Page<AmazonProduct>(request, response), amazonProduct); 
        model.addAttribute("page", page);
		return "modules/amazoninfo/amazonProductList";
	}
	
	@RequiresPermissions("amazoninfo:amazonProduct:view")
	@RequestMapping(value = {"list2"})
	public String list(AmazonProduct2 amazonProduct2, HttpServletRequest request, HttpServletResponse response, Model model) {
		 Page<AmazonProduct2>  page = new Page<AmazonProduct2>(request, response);
		 String orderBy = page.getOrderBy();
		 if("".equals(orderBy)){
			 page.setOrderBy("active desc");
		 }else{
			 page.setOrderBy(orderBy+",active desc");
		 }	 
		 page = amazonProduct2Service.find(page, amazonProduct2); 
		 page.setOrderBy(orderBy);
         Map<String, String> nameMap = Maps.newHashMap();
		 for (AmazonProduct2 product : page.getList()) {
	    	 String name = amazonProductService.findProductName(product.getAsin(),product.getCountry());
	    	 if(null!=name){
	    		 nameMap.put(product.getAsin(),name);
	    	 }
		 }
		 model.addAttribute("nameMap", nameMap);
         model.addAttribute("page", page);
		 return "modules/amazoninfo/amazonProductList2";
	}
	
	@RequestMapping(value = {"businessPriceList"})
	public String businessPriceList(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<AmazonProduct2> list = amazonProduct2Service.findB2bPriceList();
		//产品  国家  AmazonProduct2
        Map<String, Map<String, AmazonProduct2>> nameMap = Maps.newHashMap();
        Map<String,String> countryAsinNameMap = amazonProductService.getProductNameAsin();
		for (AmazonProduct2 product : list) {
	    	String name = countryAsinNameMap.get(product.getAsin()+","+product.getCountry());
			if (null != name) {
				Map<String, AmazonProduct2> map = nameMap.get(name);
				if (map == null) {
					map = Maps.newHashMap();
					nameMap.put(name, map);
				}
				map.put(product.getCountry(), product);
	    	}
		}
		model.addAttribute("nameMap", nameMap);
		return "modules/amazoninfo/amazonBusinessPriceList";
	}
	
	@RequiresPermissions("amazoninfo:amazonProduct:view")
	@RequestMapping(value = {"barcodeSearch"})
	public String barcodeSearch(AmazonProduct2 amazonProduct2, HttpServletRequest request, HttpServletResponse response, Model model) {
		 Page<AmazonProduct2>  page = new Page<AmazonProduct2>(request, response);
		 String orderBy = page.getOrderBy();
		 if("".equals(orderBy)){
			 page.setOrderBy("active desc");
		 }else{
			 page.setOrderBy(orderBy+",active desc");
		 }	 
		 page = amazonProduct2Service.find(page, amazonProduct2); 
		 page.setOrderBy(orderBy);
         Map<String, String> nameMap = Maps.newHashMap();
		 for (AmazonProduct2 product : page.getList()) {
	    	 String name = amazonProductService.findProductName(product.getAsin(),product.getCountry());
	    	 if(null!=name){
	    		 nameMap.put(product.getAsin(),name);
	    	 }
		 }
		 model.addAttribute("nameMap", nameMap);
         model.addAttribute("page", page);
		 return "modules/amazoninfo/amazonProductList3";
	}
	
	@RequiresPermissions("amazoninfo:amazonProduct:view")
	@RequestMapping(value = {"export"})
	public void export(AmazonProduct2 amazonProduct2, HttpServletRequest request, HttpServletResponse response, Model model) {
		 List<AmazonProduct2> list = amazonProduct2Service.findAllActive(amazonProduct2.getCountry());
         Map<String, String> nameMap =amazonProductService.findProductNameMap();
		 //title
         Map<String, String> titleMap = amazonPostsDetailService.getTitleMap(amazonProduct2.getCountry());
         ExportExcel export = new ExportExcel(null, Lists.newArrayList("ProductName","Country","Title","Sku","Fnsku","Ean","Asin","Price","SalePrice","HighPrice","LowPrice"));
         for (AmazonProduct2 amazonProduct : list) {
        	Row row = export.addRow();
        	export.addCell(row,0,nameMap.get(amazonProduct.getAsin()));
        	export.addCell(row,1,DictUtils.getDictLabel(amazonProduct.getCountry(),"platform", ""));
        	if(StringUtils.isNotBlank(amazonProduct2.getCountry())){
        		export.addCell(row,2,titleMap.get(amazonProduct.getAsin()));
        	}else{
        		export.addCell(row,2,titleMap.get(amazonProduct.getAsin()+"_"+amazonProduct.getCountry()));
        	}
        	export.addCell(row,3,amazonProduct.getSku());
        	export.addCell(row,4,amazonProduct.getFnsku());
        	export.addCell(row,5,amazonProduct.getEan());
        	export.addCell(row,6,amazonProduct.getAsin());
        	export.addCell(row,7,amazonProduct.getPrice());
        	export.addCell(row,8,amazonProduct.getSalePrice());
        	export.addCell(row,9,amazonProduct.getHighWarnPrice());
        	export.addCell(row,10,amazonProduct.getWarnPrice());
		 }
         try {
			export.write(response, "ProductInfo"+DateUtils.getDate()+".xlsx").dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@ResponseBody
	@RequestMapping(value = {"asin"})
	public String asin(String accountName) {
		List<Map<String, String>> rs = amazonProductService.findAsin(accountName);
		return JSON.toJSONString(rs);
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateWarnPrice"})
	public String updateWarnPrice(Integer product2Id,@RequestParam(required=false)Float warnPrice,@RequestParam(required=false)Float highWarnPrice) {
		AmazonProduct2 product2 = amazonProduct2Service.get(product2Id);
		product2.setWarnPrice(warnPrice);
		product2.setHighWarnPrice(highWarnPrice);
		product2.setLastWarnPriceUpdate(new Date());
		product2.setUpdateDate(new Date());
		product2.setWarnPriceByUser(UserUtils.getUser());
		amazonProduct2Service.save(product2);
		return product2.getWarnPriceByUser().getName();
	}
	
	
	@RequestMapping(value = {"mfnInventoryView"})
	public String mfnInventoryView(String country,String accountName,HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String,String> accountMap=amazonAccountConfigService.findCountryByAccount();
		if(StringUtils.isEmpty(accountName)){
			for (Map.Entry<String,String> entry: accountMap.entrySet()) {
				accountName = entry.getKey();
				country = entry.getValue();
				break;
			}
		}else{
			country=accountMap.get(accountName);
		}
		List<String> accountList=Lists.newArrayList();
		for (Map.Entry<String,String> entry: accountMap.entrySet()) {
			  String tempCountry=entry.getValue();
			  if(tempCountry.contains("de")||tempCountry.contains("com")||tempCountry.contains("jp")){
				  accountList.add(entry.getKey());
			  }
		}
		model.addAttribute("data", amazonProduct2Service.findMfnInventoyInfo(country,accountName));
		model.addAttribute("quantityMap", mfnOrderService.findUnshippedQuantity(country));
		model.addAttribute("country", country);
		model.addAttribute("accountList", accountList);
		model.addAttribute("accountName", accountName);
		return "modules/amazoninfo/mfnInventoryView";
	}
	
	
	@RequestMapping(value = {"getProductPrice"})
	public String getProductPrice(ProductPrice productPrice,HttpServletRequest request, HttpServletResponse response, Model model){
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (productPrice.getDate()== null) {
			productPrice.setDate(DateUtils.addDays(today, -1));
		}
		List<Object[]> price=productPriceService.getProductPrice(productPrice.getDate());
		model.addAttribute("price", price);
		model.addAttribute("productPrice", productPrice);
		return "modules/amazoninfo/amazonProductFeeList";
	}
	
	@RequestMapping(value = {"getWholeSalePrice"})
	public String getWholeSalePrice(String type,HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String,Float> wholePrice=amazonProduct2Service.getWholeSalePrice(type);
		Map<String,Float> safePrice=amazonProduct2Service.getSafePrice(type);
		
		Map<String, String> productPositionMap = psiProductEliminateService.findAllProductPosition();
		model.addAttribute("wholePrice", wholePrice);
		model.addAttribute("safePrice", safePrice);
		model.addAttribute("productPositionMap", productPositionMap);
		model.addAttribute("type", type);
		return "modules/amazoninfo/wholeSalePriceList";
	}
	
	
	@RequestMapping(value = {"exportPrice"})
	public String exportPrice(ProductPrice productPrice,HttpServletRequest request, HttpServletResponse response, Model model){
		List<Object[]> price=productPriceService.getProductPrice(productPrice.getDate());
		
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
		
		row.setHeight((short) 600);
		HSSFCell cell = null;	
		List<String>  title=Lists.newArrayList("Product","国家","SKU","DE FBA","DE佣金(%)","FR FBA","FR佣金(%)","UK FBA","UK佣金(%)","ES FBA","ES佣金(%)","IT FBA","IT佣金(%)","US FBA","US佣金(%)","CA FBA","CA佣金(%)","JP FBA","JP佣金(%)");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		int  rowIndex=1;
		for (Object[] obj: price) {
				row=sheet.createRow(rowIndex++);
				int m=0;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[0].toString());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[18].toString());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[1].toString());
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Float.parseFloat(obj[10].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[2].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Float.parseFloat(obj[11].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[3].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Float.parseFloat(obj[12].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[4].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Float.parseFloat(obj[13].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[5].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Float.parseFloat(obj[14].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[6].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Float.parseFloat(obj[15].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[7].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Float.parseFloat(obj[16].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[8].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Float.parseFloat(obj[17].toString()));
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(obj[9].toString()));
				
		}
		 for (int i=1;i<rowIndex;i++) {
       	      for (int j = 0; j < title.size(); j++) {
       	    	 if(j==0||j==1||j==2||j%2==0){
       	    		sheet.getRow(i).getCell(j).setCellStyle(contentStyle);
       	    	 }else{
       	    		sheet.getRow(i).getCell(j).setCellStyle(cellStyle);
       	    	 }
	        	  
			 }
         }
		  for (int i = 0; i < title.size(); i++) {
       		 sheet.autoSizeColumn((short)i, true);
		  }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "产品费用" + sdf.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value = {"exportWholeSalePrice"})
	public String exportWholeSalePrice(String type,HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String,Float> wholePrice=amazonProduct2Service.getWholeSalePrice(type);
		Map<String,Float> safePrice=amazonProduct2Service.getSafePrice(type);
		Map<String, String> productPositionMap = psiProductEliminateService.findAllProductPosition();
		
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
		

		HSSFCellStyle cellStyle1 = wb.createCellStyle();
        cellStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setTopBorderColor(HSSFColor.BLACK.index);
        HSSFFont font1=wb.createFont();
        font1.setColor(HSSFColor.RED.index);
        cellStyle1.setFont(font1);
        
		row.setHeight((short) 600);
		HSSFCell cell = null;	
		List<String>  title=Lists.newArrayList("产品","在售","30天平均价","九折价","八折价","七折价");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		int  rowIndex=1;
		 for (Map.Entry<String,Float> entry : wholePrice.entrySet()) { 
		        String name=entry.getKey();
				row=sheet.createRow(rowIndex++);
				int m=0;
				String key=name+"_de";
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(name);
				row.getCell(m-1).setCellStyle(contentStyle);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("4".equals(productPositionMap.get(key))?"不可售":"可销售");
				row.getCell(m-1).setCellStyle(contentStyle);
				if(safePrice==null||safePrice.get(name)==null){
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(entry.getValue());
					row.getCell(m-1).setCellStyle(cellStyle1);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle);
				}else{
					Float price=safePrice.get(name);
					if(wholePrice.get(name)>=price){
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(entry.getValue());
						row.getCell(m-1).setCellStyle(cellStyle);
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(price);
						row.getCell(m-1).setCellStyle(cellStyle1);
					}
					
					if(wholePrice.get(name)*0.9>=price){
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(entry.getValue()*0.9);
						row.getCell(m-1).setCellStyle(cellStyle);
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(price);
						row.getCell(m-1).setCellStyle(cellStyle1);
					}
					if(wholePrice.get(name)*0.8>=price){
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(entry.getValue()*0.8);
						row.getCell(m-1).setCellStyle(cellStyle);
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(price);
						row.getCell(m-1).setCellStyle(cellStyle1);
					}
					if(wholePrice.get(name)*0.7>=price){
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(entry.getValue()*0.7);
						row.getCell(m-1).setCellStyle(cellStyle);
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(price);
						row.getCell(m-1).setCellStyle(cellStyle1);
					}
					
				}
		}
		 
		  for (int i = 0; i < title.size(); i++) {
       		 sheet.autoSizeColumn((short)i, true);
		  }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "批发价格" + sdf.format(new Date()) + ".xls";
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
	
	@RequestMapping(value = {"getProductBreakEvenPrice"})
	public String getProductBreakEvenPrice(HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String,AmazonProductTypeCharge> codeMap= amazonPostsDetailService.findCharge(); 
		model.addAttribute("codeMap", codeMap);
		List<String> countryList=Lists.newArrayList("de","fr","it","es","uk","com","ca","jp");
		Map<String,Float> vatMap=Maps.newHashMap();
		Map<String,Float> dealFee=Maps.newHashMap();
		Map<String,Float> commissionFee=Maps.newHashMap();
		for(String country:countryList){
			String temp = country.toUpperCase();
			if("UK".equals(temp)){
				temp = "GB";
			}
			if("COM".equals(temp)){
				temp = "US";
			}
			CountryCode vatCode = CountryCode.valueOf(temp);
			if(vatCode!=null){
				vatMap.put(country,vatCode.getVat()/100f);
			}
			String currency="";
			Float deal=0f;
			Float commission=0f;
			if("de,fr,it,es".contains(country)){
				currency="EUR";
				deal=2.5f;
				if("es".equals(country)){
					commission=0f;
				}else{
					commission=0.5f;
				}
			}else if("uk".equals(country)){
				deal=2f;
				currency="GBP";
				commission=0.4f;
			}else if("jp".equals(country)){
				currency="JPY";
				deal=400f;
				commission=50f;
			}else if("ca".equals(country)){
				currency="CAD";
				deal=3.5f;
				commission=1f;
			}else if("com".equals(country)){
				deal=3.5f;
				currency="USD";
				commission=1f;
			}
			dealFee.put(country,deal*MathUtils.getRate(currency,"USD", null));
			commissionFee.put(country,commission*MathUtils.getRate(currency,"USD", null));
		}
		List<Dict> dicts = DictUtils.getDictList("product_type");
		Map<String,HsCode> dutyMap=Maps.newHashMap();
		for (Dict dict : dicts) {
			try{
				if(HsCode.get(dict.getValue())!=null){
					dutyMap.put(dict.getValue(),HsCode.get(dict.getValue()));
				}
			}catch(Exception e){}
		}
		model.addAttribute("vatMap", vatMap);
		model.addAttribute("dutyMap", dutyMap);
		model.addAttribute("dealFee", dealFee);
		model.addAttribute("commissionFee", commissionFee);
		Map<String, PsiProductTransportRate>  countryTranFee = psiTransportPaymentService.findTransportPriceByToCountry();
		model.addAttribute("countryTranFee", countryTranFee);
		model.addAttribute("cnyToUsd", AmazonProduct2Service.getRateConfig().get("USD/CNY"));
		model.addAttribute("eurToUsd",MathUtils.getRate("EUR","USD", null));
		model.addAttribute("cadToUsd",MathUtils.getRate("CAD","USD", null));
		model.addAttribute("jpyToUsd",MathUtils.getRate("JPY","USD", null));
		model.addAttribute("gbpToUsd",MathUtils.getRate("GBP","USD", null));
		return "modules/amazoninfo/productBreakEvenEstimate";
	}
	
	
	@RequestMapping(value = {"exportAllCountryPrice"})
	public void exportAllCountryPrice(AmazonProduct2 amazonProduct2, HttpServletRequest request, HttpServletResponse response, Model model) {
		 Map<String,Object[]> map=amazonProduct2Service.findProductPrice();
         ExportExcel export = new ExportExcel(null, Lists.newArrayList("ProductName","de","fr","it","es","uk","com","ca","jp"));
         for (Map.Entry<String, Object[]> entry: map.entrySet()) { 
            String name=entry.getKey();
        	Row row = export.addRow();
        	export.addCell(row,0,name);
        	Object[] obj=entry.getValue();
        	export.addCell(row,1,obj[1]==null?"":obj[1]);
        	export.addCell(row,2,obj[2]==null?"":obj[2]);
        	export.addCell(row,3,obj[3]==null?"":obj[3]);
        	export.addCell(row,4,obj[4]==null?"":obj[4]);
        	export.addCell(row,5,obj[5]==null?"":obj[5]);
        	export.addCell(row,6,obj[6]==null?"":obj[6]);
        	export.addCell(row,7,obj[7]==null?"":obj[7]);
        	export.addCell(row,8,obj[8]==null?"":obj[8]);
		 }
         try {
			export.write(response, "ProductPriceInfo"+DateUtils.getDate()+".xlsx").dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value = {"exportCommission"})
	public String exportCommission(String createDate,String endDate,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException{
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		Date start=dateFormat.parse(createDate);
		Date end=dateFormat.parse(endDate);
		Map<String,List<Object[]>> map=settlementReportOrderService.compareCommission(start,end);
		HSSFWorkbook wb = new HSSFWorkbook();
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
		
		List<String>  title=Lists.newArrayList("订单号","SKU","报表佣金(%)","SKU佣金(%)","时间","数量","总金额","折扣");
		
		for (Map.Entry<String,List<Object[]>> entry: map.entrySet()) {
			  List<Object[]> list=entry.getValue();
			  String country=entry.getKey();
			  HSSFSheet sheet= wb.createSheet(("com".equals(country)?"US":country.toUpperCase()));
			  HSSFRow row = sheet.createRow(0);
			  row.setHeight((short) 400);
			  HSSFCell cell = null;		
			  for(int i = 0; i < title.size(); i++){
					cell = row.createCell(i);
					cell.setCellStyle(style);
					cell.setCellValue(title.get(i));
					sheet.autoSizeColumn((short)i);
			 }	
			 int rowIndex=1;
			 //r.`amazon_order_id`,t.sku,r.`country`,ROUND(SUM(-IFNULL(t.`commission`,0))*100/SUM(t.`principal`)) commission,e.commission_pcent,
			 //DATE_FORMAT(r.`posted_date`,'%Y%m%d') posted_date,SUM(t.quantity) quantity,SUM(t.`principal`) principal
			 for (Object[] obj: list) {
					row=sheet.createRow(rowIndex++);
					int m=0;
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[0].toString());
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[1].toString());
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[3].toString());
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[4].toString());
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[5].toString());
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[6].toString());
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[7].toString());
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[8].toString());
			}	
		}
		
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "Commission" + sdf.format(new Date()) + ".xls";
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
	
	/*@RequiresPermissions("amazoninfo:amazonProduct:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			ImportExcel ei = new ImportExcel(file, 0 , 0);
			List<AmazonProduct> list = ei.getDataList(AmazonProduct.class);
			amazonProductService.save(list);
			addMessage(redirectAttributes, "已成功导入 ");
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonProduct/?repage";
    }
	
	@RequiresPermissions("amazoninfo:amazonProduct:edit")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "产品导入模板.xlsx";
    		List<User> list = Lists.newArrayList(); 
    		new ExportExcel("", AmazonProduct.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonProduct/?repage";
    }*/
}
