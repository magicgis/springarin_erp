/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 仓库Controller
 * @author tim
 * @version 2014-11-17
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/stock")
public class StockController extends BaseController {

	@Autowired
	private StockService 		stockService;
	@Autowired
	private PsiInventoryService inventoryService;
	
	@ModelAttribute
	public Stock get(@RequestParam(required=false) Integer id) {
		if (id!=null&&id>0){
			return stockService.get(id);
		}else{
			return new Stock();
		}
	}
	
	
	@RequiresPermissions("psi:stock:view")
	@RequestMapping(value = {"list", ""})
	public String list(Stock stock, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			stock.setCreateBy(user);
		}
        Page<Stock> page = stockService.find(new Page<Stock>(request, response,20), stock); 
        Map<Integer,Float> capMap=this.inventoryService.getTimelyCapacity();
        
        
        model.addAttribute("capMap", capMap);
        model.addAttribute("page", page);
		return "modules/psi/stockList";
	}

	@RequiresPermissions("psi:stock:view")
	@RequestMapping(value = "form")
	public String form(Stock stock, Model model) {
		model.addAttribute("stock", stock);
		return "modules/psi/stockForm";
	}

	@RequiresPermissions("psi:stock:edit")
	@RequestMapping(value = "save")
	public String save(Stock stock, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, stock)){
			return form(stock, model);
		}
		stockService.save(stock);
		addMessage(redirectAttributes, "保存仓库'" + stock.getStockName()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/stock";
	}
	
	@RequiresPermissions("psi:stock:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		stockService.delete(id);
		addMessage(redirectAttributes, "删除仓库成功");
		return "redirect:"+Global.getAdminPath()+"/modules/psi/stock/?repage";
	}
	
	@Autowired
	private PsiInventoryFbaService psiInventoryFbaService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@RequestMapping(value = "inventoryFba")
	public String inventoryFba(PsiInventoryFba psiInventoryFba, HttpServletRequest request, HttpServletResponse response, Model model) {
		 if(psiInventoryFba.getCountry()==null){
			 psiInventoryFba.setCountry("de");
		 }	
		 Page<PsiInventoryFba> page = new Page<PsiInventoryFba>(request, response,20);
		 String orderBy = page.getOrderBy();
		 if("".equals(orderBy)){
			 page.setOrderBy("totalQuantity desc");
		 }else{
			 page.setOrderBy(orderBy+",totalQuantity desc");
		 }	 
		 page = psiInventoryFbaService.find(page, psiInventoryFba); 
		 page.setOrderBy(orderBy);
		 Map<String, Map<String, Float>> priceMap = Maps.newHashMap();
		 for (PsiInventoryFba fba : page.getList()) {
	    	 priceMap.put(fba.getSku(), amazonProduct2Service.findProductPrice(fba.getSku()));
		 }
		 model.addAttribute("nameMap", psiProductService.findProductNameWithSku(psiInventoryFba.getCountry()));
		 model.addAttribute("priceMap", priceMap);
		 model.addAttribute("page", page);
		 return "modules/psi/psiInventoryFba";
	}
	
	@RequestMapping(value = "inventoryFba/export")
	public String inventoryFbaExport(PsiInventoryFba psiInventoryFba, HttpServletRequest request, HttpServletResponse response, Model model) {
		 Page<PsiInventoryFba> page = new Page<PsiInventoryFba>(request, response,20);
		 page.setPageSize(60000);
		 String orderBy = page.getOrderBy();
		 if("".equals(orderBy)){
			 page.setOrderBy("totalQuantity desc");
		 }else{
			 page.setOrderBy(orderBy+",totalQuantity desc");
		 }	 
		 page = psiInventoryFbaService.find(page, psiInventoryFba); 
		 page.setOrderBy(orderBy);
		 Map<String, String> nameMap = psiProductService.findProductNameWithSku(psiInventoryFba.getCountry());
		 Map<String, Map<String, Float>> priceMap = Maps.newHashMap();
		 for (PsiInventoryFba fba : page.getList()) {
	    	 priceMap.put(fba.getSku(), amazonProduct2Service.findProductPrice(fba.getSku()));
		 }
		 try {
			 	String country = psiInventoryFba.getCountry().toUpperCase();
	            String fileName = DateUtils.getDate(psiInventoryFba.getDataDate(),"yyyyMMdd")+"日FBA库存实时数目表"+DateUtils.getDate("yyyyMMddHHmmss")+"_"+country+".xlsx";
	            List<String> title = Lists.newArrayList("产品名","Sku","Fnsku","Asin"
	            		,"Salechannel Price","FulfillableQuantity",
	            		"UnsellableQuantity","ReservedQuantity","WarehouseQuantity","TransitQuantity","TotalQuantity");
	            ExportExcel excel = new ExportExcel(country+"  FBA实时库存",title);
	        	for (PsiInventoryFba fba : page.getList()) {
	        		Row row = excel.addRow();
	        		excel.addCell(row,0,nameMap.get(fba.getSku()));
	        		excel.addCell(row,1,fba.getSku());
	        		excel.addCell(row,2,fba.getFnsku());
	        		excel.addCell(row,3,fba.getAsin());
	        		String price = "";
        			if(priceMap.get(fba.getSku())!=null&&priceMap.get(fba.getSku()).get(fba.getCountry())!=null){
        				price = priceMap.get(fba.getSku()).get(fba.getCountry())+"";
        			}
	        		excel.addCell(row,4,price);
	        		excel.addCell(row,5,fba.getFulfillableQuantity());
	        		excel.addCell(row,6,fba.getUnsellableQuantity());
	        		excel.addCell(row,7,fba.getReservedQuantity());
	        		excel.addCell(row,8,fba.getWarehouseQuantity());
	        		excel.addCell(row,9,fba.getTransitQuantity());
	        		excel.addCell(row,10,fba.getTotal());
				}
	            excel.write(response, fileName).dispose();
	    		return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		 return "redirect:"+Global.getAdminPath()+"/psi/stock/inventoryFba?country="+psiInventoryFba.getCountry();
	}
	

	@RequestMapping(value = "inventoryFba/exportAll")
	public String inventoryFbaExportAll(PsiInventoryFba psiInventoryFba, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		 List<Object[]> psiList = psiInventoryFbaService.findAllPsiInventoryFba(psiInventoryFba); 
		
		 Map<String, Map<String, Float>> priceMap = Maps.newHashMap();
		 if(psiList!=null){
			 for (int i=0;i<psiList.size();i++) {
				 Object[] object=psiList.get(i);
		    	 priceMap.put((String) object[3], amazonProduct2Service.findProductPrice((String) object[3]));
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

		  HSSFCell cell = null;	
		  List<String> title = Lists.newArrayList("品牌","型号","市场","Sku","Fnsku","Asin"
          		,"Salechannel Price","FulfillableQuantity",
          		"UnsellableQuantity","ReservedQuantity","WarehouseQuantity","TransitQuantity","TotalQuantity");
		  for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
		  }
		  if(psiList!=null){
			  for(int j=0;j<psiList.size();j++){
				    int i=0;
				    Object[] fba=psiList.get(j);
				    row=sheet.createRow(j+1);
				    row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((String)fba[0]);
				    row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((String)fba[1]);
				    row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((String)fba[2]);
				    row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((String)fba[3]);
				    row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((String)fba[4]);
				    row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((String)fba[5]);
		      		String price = "";
		  			if(priceMap.get((String)fba[3])!=null&&priceMap.get((String)fba[3]).get((String)fba[2])!=null){
		  				price = priceMap.get((String)fba[3]).get((String)fba[2])+"";
		  			}
		      	     row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((StringUtils.isBlank(price))?"":price);
			      	 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue((Integer)fba[6]);
			      	 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue((Integer)fba[7]);
			      	 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue((Integer)fba[8]);
			      	 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue((Integer)fba[10]);
			      	 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue((Integer)fba[11]);
			      	 if(fba[9]==null){
			      		row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue((Integer)fba[12]);
			      	 }else{//fba.fulfillableQuantity+fba.transitQuantity+fba.orrectQuantity
			      		row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue((Integer)fba[6]+(Integer)fba[11]+(Integer)fba[9]); 
			      	 }
			      	 
			  }
			  
			  
			  for (int i=0;i<psiList.size();i++) {
		        	 for (int j = 0; j < title.size(); j++) {
			        	  sheet.getRow(i+1).getCell(j).setCellStyle(contentStyle);
					 }
		       }
			  
			  for(int m=0;m<6;m++){
				  sheet.autoSizeColumn((short)m);
			  }
		  }
		 
		 try {
			    request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
		
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		
				String fileName = "FBA库存" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
	         
	    		return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		 return "redirect:"+Global.getAdminPath()+"/psi/stock/inventoryFba?country="+psiInventoryFba.getCountry();
	}
	
	@RequestMapping(value = "inventoryFba/exportAll2")
	public String inventoryFbaExportAll2(PsiInventoryFba psiInventoryFba, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		 Page<PsiInventoryFba> page = new Page<PsiInventoryFba>(request, response,20);
		 page.setPageSize(60000);
		 String orderBy = page.getOrderBy();
		 if("".equals(orderBy)){
			 page.setOrderBy("country desc");
		 }else{
			 page.setOrderBy(orderBy+",country desc");
		 }	 
		 page = psiInventoryFbaService.findAllCountry(page, psiInventoryFba); 
		 page.setOrderBy(orderBy);
		 Map<String, Object[]> nameMap = psiProductService.findProductNameWithSku();
		 
		 Map<String, Map<String, Float>> priceMap = Maps.newHashMap();
		 for (PsiInventoryFba fba : page.getList()) {
	    	 priceMap.put(fba.getSku(), amazonProduct2Service.findProductPrice(fba.getSku()));
		 }
		 
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

		  HSSFCell cell = null;	
		  List<String> title = Lists.newArrayList("品牌","型号","市场","Sku","Fnsku","Asin"
          		,"Salechannel Price","FulfillableQuantity",
          		"UnsellableQuantity","ReservedQuantity","WarehouseQuantity","TransitQuantity","TotalQuantity");
		  for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
		  }
		  if(page.getList()!=null){
			  for(int j=0;j<page.getList().size();j++){
				    int i=0;
				    PsiInventoryFba fba=page.getList().get(j);
				    row=sheet.createRow(j+1);
				    row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(nameMap.get(fba.getSku())==null?"":nameMap.get(fba.getSku())[1].toString());
				    row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(nameMap.get(fba.getSku())==null?"":nameMap.get(fba.getSku())[0].toString());
				    row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(fba.getCountry())?"us":fba.getCountry());
				    row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(fba.getSku());
				    row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(fba.getFnsku());
				    row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(fba.getAsin());
		      		String price = "";
		  			if(priceMap.get(fba.getSku())!=null){
		  				price = priceMap.get(fba.getSku()).get(fba.getCountry())+"";
		  			}
		      	     row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((price==null||price=="null")?"":price);
			      	 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(fba.getFulfillableQuantity());
			      	 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(fba.getUnsellableQuantity());
			      	 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(fba.getReservedQuantity());
			      	 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(fba.getWarehouseQuantity());
			      	 row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(fba.getTransitQuantity());
			      	 if(fba.getOrrectQuantity()==null){
			      		row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(fba.getTotalQuantity());
			      	 }else{//fba.fulfillableQuantity+fba.transitQuantity+fba.orrectQuantity
			      		row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(fba.getFulfillableQuantity()+fba.getTransitQuantity()+fba.getOrrectQuantity()); 
			      	 }
			      	 
			  }
			  
			  for (int i=0;i<page.getList().size();i++) {
		        	 for (int j = 0; j < title.size(); j++) {
			        	  sheet.getRow(i+1).getCell(j).setCellStyle(contentStyle);
					 }
		       }
			  
			  for(int m=0;m<6;m++){
				  sheet.autoSizeColumn((short)m);
			  }
		  }
		 
		 try {
			    request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
		
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		
				String fileName = "FBA库存" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
	         
	    		return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		 return "redirect:"+Global.getAdminPath()+"/psi/stock/inventoryFba?country="+psiInventoryFba.getCountry();
	}
	
	//导出美国2017-08-24fba&本地仓库存金额表(财务临时需求)
	@RequestMapping(value = "inventoryFba/exportus")
	public String exportus(PsiInventoryFba psiInventoryFba, HttpServletRequest request, HttpServletResponse response, Model model) {
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

		Map<String, Integer> fbaMap = psiInventoryFbaService.findUsFbaInventoryByDataDate("com", "2017-08-24 00:00:00");
		Map<String, Float> piPriceMap = inventoryService.getPiPrice("com", DateUtils.addMonths(new Date(), -1));
		Map<String, Integer> hisMap = psiInventoryFbaService.findUsHisInventoryByDataDate("com", "2017-08-24");

		Map<String,Map<String,Object>> productsMoqAndPrice = inventoryService.getProductsMoqAndPrice();
		
		HSSFCell cell = null;
		List<String> title = Lists.newArrayList("产品", "FBA库存", "本地库存", "价格",
				"总价");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int j = 1;
		for (Entry<String, Integer> entry : fbaMap.entrySet()) {
			String productName = entry.getKey();
			Integer fbaQty = entry.getValue();
			int localQty = 0;
			if (hisMap.get(productName) != null) {
				localQty = hisMap.get(productName);
			}
			float price = 0f;
			try {
				price = ((BigDecimal)productsMoqAndPrice.get(productName).get("price")).floatValue();
			} catch (NullPointerException e) {
				logger.info(productName + "singlePrice价格为空");
			}
			//TODO 有PI价格的话用PI价格
			if (piPriceMap.get(productName) != null && piPriceMap.get(productName) > 0) {
				price = piPriceMap.get(productName);
			}
			BigDecimal b  = new BigDecimal(price);
			price = b.setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue();
			row = sheet.createRow(j++);
			int i = 0;
			row.createCell(i++, Cell.CELL_TYPE_STRING).setCellValue(productName);
			row.createCell(i++, Cell.CELL_TYPE_STRING).setCellValue(fbaQty);
			row.createCell(i++, Cell.CELL_TYPE_STRING).setCellValue(localQty);
			if (price > 0) {
				row.createCell(i++, Cell.CELL_TYPE_STRING).setCellValue(price);
				row.createCell(i++, Cell.CELL_TYPE_STRING).setCellValue(
						price * (fbaQty + localQty));
			} else {
				row.createCell(i++, Cell.CELL_TYPE_STRING).setCellValue("");
				row.createCell(i++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
		}
		 
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");

			String fileName = "库存金额" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();

			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/psi/stock/inventoryFba?country="+psiInventoryFba.getCountry();
	}
	
	

}
