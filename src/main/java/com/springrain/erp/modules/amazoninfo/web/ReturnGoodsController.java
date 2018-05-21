/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.ZipUtil;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.ReturnGoods;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.scheduler.SendEmailByOrderMonitor;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.ReturnGoodsService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.utils.DictUtils;

/**
 * 退货信息Controller
 * @author Tim
 * @version 2014-12-29
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/returnGoods")
public class ReturnGoodsController extends BaseController {
	@Autowired
	private ReturnGoodsService returnGoodsService;
	
	@Autowired
	private AmazonProductService amazonProductService;
	
	@Autowired
	private AmazonOrderService orderService;
	
	@Autowired
	private PsiProductService psiProductService;
	@Autowired
	private PsiInventoryService psiInventoryService;
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	private static DateFormat formatWeek = new SimpleDateFormat("yyyyww");
	private static DateFormat formatMonth = new SimpleDateFormat("yyyyMM");
	
	@RequestMapping(value = {"list", ""})
	public String list(ReturnGoods returnGoods, HttpServletRequest request, HttpServletResponse response, Model model) {
		 Page<ReturnGoods> page = new Page<ReturnGoods>(request, response,20);
		 page.setOrderBy("returnDate desc");
		 page = returnGoodsService.find(page, returnGoods); 
		 Map<String, String> nameMap = Maps.newHashMap();
		 for (ReturnGoods goods : page.getList()) {
	    	 String name = amazonProductService.findProductName(goods.getAsin(),goods.getCountry());
	    	 if(null!=name){
	    		 nameMap.put(goods.getAsin(),name);
	    	 }
		 }
		 model.addAttribute("nameMap", nameMap);
		 model.addAttribute("reasons", returnGoodsService.getReturnGoodsReasons(returnGoods));
		 model.addAttribute("dispositions", returnGoodsService.getReturnGoodsDispositions(returnGoods));
		 
		 model.addAttribute("page", page);
		return "modules/amazoninfo/returnGoodsList";
	}
	
	
	@RequestMapping(value = "differentialReturnCountList")
	public String differentialReturnCountList(ReturnGoods returnGoods, HttpServletRequest request, HttpServletResponse response, Model model) {
		PsiProduct psiProduct=new PsiProduct();
		Date today=DateUtils.addMonths(new Date(),-1);
		today.setHours(0);
		today.setMinutes(0);
		today.setMinutes(0);
	    if(returnGoods.getStartDate()==null){
	    	 returnGoods.setStartDate(DateUtils.getFirstDayOfMonth(today));
	    	 returnGoods.setReturnDate(DateUtils.getLastDayOfMonth(today));
		}
		
		returnGoods.setCountry(returnGoods.getCountry());
		psiProduct.setPlatform(returnGoods.getCountry());
		List<String> page =psiProductService.findCountryProduct(psiProduct);
		Map<String,String> productSupplier =psiProductService.findSupplierByProductName();
		Map<String,Object[]> returnMap=returnGoodsService.findAllReturnCommentInfo(returnGoods);
		Map<String,Object[]> orderMap=returnGoodsService.findAllOrderInfo(returnGoods);
		Map<String,Map<String,Object>> productsMoqAndPrice = psiInventoryService.getProductsMoqAndPrice();
		List<PsiProductTypeGroupDict> lineList=groupDictService.getAllList();
		model.addAttribute("productsMoqAndPrice", productsMoqAndPrice);
		model.addAttribute("page", page);
		model.addAttribute("returnMap", returnMap);
		model.addAttribute("orderMap", orderMap);
		model.addAttribute("goods",returnGoods);
		model.addAttribute("productSupplier",productSupplier);
		model.addAttribute("flag",SecurityUtils.getSubject().isPermitted("psi:inventory:stockPriceView")?"1":"0");
		model.addAttribute("lineList",lineList);
		model.addAttribute("nameTypeMap", psiProductService.findProductTypeMap());
		model.addAttribute("typeLineMap", groupDictService.getTypeLine(null));
		return "modules/amazoninfo/differentialReturnCountList";
	}
	
	
	
	@RequestMapping(value = "returnCountList")
	public String returnCountList(String startDate,String endDate,String country,String productName, HttpServletRequest request, HttpServletResponse response, Model model) {
		ReturnGoods returnGoods=new ReturnGoods();
		 Calendar   cal_1=Calendar.getInstance();
	     cal_1.add(Calendar.MONTH, -1);
	     cal_1.set(Calendar.DAY_OF_MONTH,1);
	     Date start=cal_1.getTime();
	     Calendar cale = Calendar.getInstance();   
	     cale.set(Calendar.DAY_OF_MONTH,0);
	     Date end=cale.getTime();
	     if(returnGoods.getStartDate()==null){
	    	 returnGoods.setStartDate(start);
		}
		if (returnGoods.getReturnDate()== null) {
			returnGoods.setReturnDate(end);
		}
		returnGoods.setStartDate(new Date(startDate)==null?start:new Date(startDate));
		returnGoods.setReturnDate(new Date(endDate)==null?end:new Date(endDate));
		returnGoods.setProductName(productName);
		returnGoods.setCountry(country);
		List<Object[]> page =returnGoodsService.findReturnInfoByProduct(returnGoods);
		model.addAttribute("page", page);
		return "modules/amazoninfo/returnCountList";
	}
	
	@RequestMapping(value = "commentCountList")
	public String commentCountList(String startDate,String endDate,String country,String productName, HttpServletRequest request, HttpServletResponse response, Model model) {
		ReturnGoods returnGoods=new ReturnGoods();
		 Calendar   cal_1=Calendar.getInstance();
	     cal_1.add(Calendar.MONTH, -1);
	     cal_1.set(Calendar.DAY_OF_MONTH,1);
	     Date start=cal_1.getTime();
	     Calendar cale = Calendar.getInstance();   
	     cale.set(Calendar.DAY_OF_MONTH,0);
	     Date end=cale.getTime();
	     if(returnGoods.getStartDate()==null){
	    	 returnGoods.setStartDate(start);
		}
		if (returnGoods.getReturnDate()== null) {
			returnGoods.setReturnDate(end);
		}
		returnGoods.setStartDate(new Date(startDate)==null?start:new Date(startDate));
		returnGoods.setReturnDate(new Date(endDate)==null?end:new Date(endDate));
		returnGoods.setProductName(productName);
		returnGoods.setCountry(country);
		Map<String,String> map =returnGoodsService.findCommentAmount(returnGoods);
		Map<String,String> eventMap =returnGoodsService.findCommentEventAmount(returnGoods);
		model.addAttribute("map", map);
		model.addAttribute("eventMap", eventMap);
		return "modules/amazoninfo/commentCountList";
	}
	private final static DateFormat FORMAT = new SimpleDateFormat("yyyyMMdd");
	
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(ReturnGoods returnGoods, HttpServletRequest request, HttpServletResponse response) {
    	Page<ReturnGoods> page = new Page<ReturnGoods>(request, response,20);
    	page.setPageNo(1);
 		page.setPageSize(100000);
    	page.setOrderBy("returnDate desc");
		page = returnGoodsService.find(page, returnGoods); 
		try {
            String fileName = "退货订单报表数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
            String country = returnGoods.getCountry();
           if("com".equals(country)){
            	country = "US";
            }else{
            	country = country.toUpperCase();
            }
           Set<String> orders = Sets.newHashSet();
           for (ReturnGoods goods : page.getList()) {
   			 if("com".equals(goods.getCountry())){
   				goods.setCountry("us");
   			  }
   			 orders.add(goods.getOrderId());
   		   }
            Map<String,String> rs = orderService.getOrderEmailMap(orders);
            Map<String,String> nameMap  =psiProductService.getProductNameBySku();
            for (ReturnGoods goods : page.getList()) {
            	goods.setProductName(rs.get(goods.getOrderId()));
            	goods.setProuctNameColor(nameMap.get(goods.getSku()));
            }
    		new ExportExcel(country+"退货订单("+FORMAT.format(returnGoods.getStartDate())+"-"+FORMAT.format(returnGoods.getReturnDate())+")", ReturnGoods.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/returnGoods/?repage";
    }
    
    
    @RequestMapping(value = "exportAllCountry", method=RequestMethod.POST)
    public String exportAllCountry(ReturnGoods returnGoods, HttpServletRequest request, HttpServletResponse response) {
    	Page<ReturnGoods> page = new Page<ReturnGoods>(request, response,20);
    	page.setPageNo(1);
 		page.setPageSize(100000);
    	page.setOrderBy("returnDate desc,country asc");
		page = returnGoodsService.findAllCountry(page, returnGoods); 
		Set<String> orders = Sets.newHashSet();
		for (ReturnGoods goods : page.getList()) {
			if("com".equals(goods.getCountry())){
				goods.setCountry("us");
			}
			 orders.add(goods.getOrderId());
		}
		Map<String,String> rs = orderService.getOrderEmailMap(orders);
		Map<String,String> nameMap  =psiProductService.getProductNameBySku();
         for (ReturnGoods goods : page.getList()) {
         	goods.setProductName(rs.get(goods.getOrderId()));
         	goods.setProuctNameColor(nameMap.get(goods.getSku()));
        }
		try {
            String fileName = "退货订单"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
    		new ExportExcel("退货订单("+FORMAT.format(returnGoods.getStartDate())+"-"+FORMAT.format(returnGoods.getReturnDate())+")", ReturnGoods.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/returnGoods/?repage";
    }
    
    private static Map<String,String> threadGetPdf = Maps.newHashMap();

    @RequestMapping(value = "exportPdfs")
    public String exportPdfs(final String country,final String month,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes){
    	String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/invoiceTotal/"+country+"/"+month;
    	File zipFile = new File (baseDirStr+"/Amazon_RetrunGoodsTotal_bill.zip");
		final String key =  month+"_"+country;
		if(threadGetPdf.get(key)==null){
			if(!zipFile.exists()|| zipFile.lastModified()+12*3600000<new Date().getTime()){
	    		new Thread(){
	    			public void run() {
	    				threadGetPdf.put(key, "1");
	    				Date monthDate = null;
						try {
							monthDate = DateUtils.parseDate(month+"-1",new String[]{"yyyy-MM-dd"});
						} catch (ParseException e1) {}
	    				Date start = DateUtils.getFirstDayOfMonth(monthDate);
	    				Date end = DateUtils.getLastDayOfMonth(monthDate);
	    				Map<String, Object[]>  returnGoodsMap = returnGoodsService.getRetrunGoodsOrder(start, end, country);
	    				List<AmazonOrder> orders = orderService.findOrdersByEg(returnGoodsMap.keySet());
	    				Map<String,Map<String, Integer>> params = Maps.newHashMap();
	    				for (Object[] objs : returnGoodsMap.values()) {
	    					String orderId = objs[0].toString();
	    					String sku = objs[1].toString();
	    					Integer num = ((BigDecimal)objs[2]).intValue();
	    					Map<String, Integer> goods = params.get(orderId);
	    					if(goods==null){
	    						goods = Maps.newHashMap();
	    						params.put(orderId, goods);
	    					}
	    					goods.put(sku, num);
	    				}
	    		    	File pdfFile = SendEmailByOrderMonitor.genPdfsByRefund(month,amazonProductService, country, orders, params);
	    		    	if(pdfFile!=null){
	    		    		try {
	    		    			String zipName = pdfFile.getName().replace(".pdf", ".zip");
	    						ZipUtil.zip(pdfFile.getParent()+"/"+zipName,"",pdfFile.getAbsolutePath());
	    					} catch (Exception e) {
	    						e.printStackTrace();
	    					}
	    		    	}
	    		    	threadGetPdf.remove(key);
	    			};
	    		}.start();
	    		addMessage(redirectAttributes, "Is background rendering, please make the request again after 5 minutes ...");
	    		return "redirect:"+Global.getAdminPath()+"/amazoninfo/returnGoods/?country="+country;
			}else{
				try {
    				response.addHeader("Content-Disposition", "attachment;filename="
    						+zipFile.getName());
    				OutputStream out = response.getOutputStream();
    				out.write(FileUtils.readFileToByteArray(zipFile));
    				out.flush();
    				out.close();
        		} catch (Exception e) {
    				e.printStackTrace();
    			}
        		return null;
			}
		}
		addMessage(redirectAttributes, "Is background rendering,Just a moment please ...");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/returnGoods/?country="+country;
    }
    
   //byTimeExport
    @RequestMapping(value = "byTimeExport")
	public String byTimeExport(ReturnGoods returnGoods,String type,HttpServletRequest request, HttpServletResponse response, Model model) {
    	PsiProduct psiProduct=new PsiProduct();
    	 Calendar   cal_1=Calendar.getInstance();
	     cal_1.add(Calendar.MONTH, -1);
	     cal_1.set(Calendar.DAY_OF_MONTH,1);
	     Date start=cal_1.getTime();
	     Calendar cale = Calendar.getInstance();   
	     cale.set(Calendar.DAY_OF_MONTH,0);
	     Date end=cale.getTime();
	     if(returnGoods.getStartDate()==null){
	    	 returnGoods.setStartDate(start);
		}
		if (returnGoods.getReturnDate()== null) {
			returnGoods.setReturnDate(end);
		}
		returnGoods.setCountry(returnGoods.getCountry());
		psiProduct.setPlatform(returnGoods.getCountry());
		List<String> page =psiProductService.findCountryProduct(psiProduct);
		Map<String,Map<String,Object[]>> returnMap=returnGoodsService.findAllReturnCommentInfo(returnGoods,type);
		Map<String,Map<String,Object[]>> orderMap=returnGoodsService.findAllOrderInfo(returnGoods,type);
		Map<String,String> productSupplier =psiProductService.findSupplierByProductName();
		Map<String,PsiProductTypeGroupDict> nameAndLineMap=groupDictService.getLineByProductName();//产品名-产品线名称
		Date startDate=returnGoods.getStartDate();
		if(startDate.getDay()==0&&!"1".equals(type)){
			startDate = DateUtils.addDays(startDate, -1);
		}
		Date endDate=returnGoods.getReturnDate();
		List<String> tip =new ArrayList<String>();
		while(endDate.after(startDate)||endDate.equals(startDate)){
			if("1".equals(type)){
				String key = formatMonth.format(startDate);
				tip.add(key);
				startDate = DateUtils.addMonths(startDate, 1);
			}else{
				String key = formatWeek.format(startDate);
				int year =DateUtils.getSunday(startDate).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(startDate).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				tip.add(key);
				startDate = DateUtils.addWeeks(startDate, 1);
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
		HSSFCell cell = null;
		List<String> title = Lists.newArrayList("退货差评率统计","","");
		Collections.reverse(tip);
		for(String key:tip){
			title.add(key+("1".equals(type)?"M":"W"));
		}
		List<String> title2 = Lists.newArrayList("产品","产品线","供应商");
		List<String> colTitle = Lists.newArrayList("销售数量","退货数量","退货率(%)","订单数量","差评数量","差评率(%)");
		for(String key:tip){
			title2.addAll(colTitle);
		}
		int index=3;
		int endIndex=8;
		int m=0;
		for (int i = 0; i < title2.size(); i++) {
			cell = row.createCell(i);
			if(i==0||i==1||i==2){
				cell.setCellValue(title.get(m++));
				if(i==2){
					sheet.addMergedRegion(new CellRangeAddress(0,0,0,2));
				}
			}else if(i==index){
				cell.setCellValue(title.get(m++));
				index+=colTitle.size();
			}else if(i==endIndex){
				sheet.addMergedRegion(new CellRangeAddress(0,0,endIndex-colTitle.size()+1,endIndex));
				endIndex+=colTitle.size();
			}
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
	    }
		row=sheet.createRow(1);
		for (int i = 0; i < title2.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title2.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
	    }
		int rownum=2;
		for(String name:page){
			row=sheet.createRow(rownum++);
			int j=0;
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
    		sheet.getRow(rownum-1).getCell(j-1).setCellStyle(contentStyle);
    		if(nameAndLineMap.get(name)!=null){
    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(nameAndLineMap.get(name).getName());
    		}else{
    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
    		}
    		sheet.getRow(rownum-1).getCell(j-1).setCellStyle(contentStyle);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(productSupplier.get(name)==null?"":productSupplier.get(name));
    		sheet.getRow(rownum-1).getCell(j-1).setCellStyle(contentStyle);
    		for(String key:tip){
    			int buyAmount=0;
    			int returnAmount=0;
    			
    			int totalComment=0;
    			int badComment=0;
    			if(orderMap.get(name)!=null&&orderMap.get(name).get(key)!=null){
    				buyAmount=((BigDecimal)orderMap.get(name).get(key)[2]).intValue();
    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(buyAmount);
    			}else{
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
    			}
    			sheet.getRow(rownum-1).getCell(j-1).setCellStyle(contentStyle);
    			if(returnMap.get(name)!=null&&returnMap.get(name).get(key)!=null){
    				returnAmount=((BigDecimal)returnMap.get(name).get(key)[2]).intValue();
    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(returnAmount);
    			}else{
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
    			}
    			sheet.getRow(rownum-1).getCell(j-1).setCellStyle(contentStyle);
    			if(buyAmount>0){
    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(returnAmount*100.0f/buyAmount);
    			}else{
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
    			}
    			sheet.getRow(rownum-1).getCell(j-1).setCellStyle(cellStyle);
    			if(returnMap.get(name)!=null&&returnMap.get(name).get(key)!=null){
    				totalComment=((BigDecimal)returnMap.get(name).get(key)[4]).intValue();
    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(totalComment);
    			}else{
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
    			}
    			sheet.getRow(rownum-1).getCell(j-1).setCellStyle(contentStyle);
    			if(returnMap.get(name)!=null&&returnMap.get(name).get(key)!=null){
    				badComment=((BigDecimal)returnMap.get(name).get(key)[3]).intValue();
    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(badComment);
    			}else{
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
    			}
    			sheet.getRow(rownum-1).getCell(j-1).setCellStyle(contentStyle);
    			if(totalComment>0){
    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(badComment*100.0f/totalComment);
    			}else{
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
    			}
    			sheet.getRow(rownum-1).getCell(j-1).setCellStyle(cellStyle);
    		}
		}
		/*for (int i = 0; i < title2.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
		    }*/
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
         String fileName = DictUtils.getDictLabel(returnGoods.getCountry(),"platform","各国合计")+"退货差评率统计" + sdf.format(new Date()) + ".xls";
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
    
    @RequestMapping(value = "differentialReturnCountListExport")
	public String differentialReturnCountListExport(ReturnGoods returnGoods,HttpServletRequest request, HttpServletResponse response, Model model) {
    	PsiProduct psiProduct=new PsiProduct();
    	 Calendar   cal_1=Calendar.getInstance();
	     cal_1.add(Calendar.MONTH, -1);
	     cal_1.set(Calendar.DAY_OF_MONTH,1);
	     Date start=cal_1.getTime();
	     Calendar cale = Calendar.getInstance();   
	     cale.set(Calendar.DAY_OF_MONTH,0);
	     Date end=cale.getTime();
	     if(returnGoods.getStartDate()==null){
	    	 returnGoods.setStartDate(start);
		}
		if (returnGoods.getReturnDate()== null) {
			returnGoods.setReturnDate(end);
		}
		returnGoods.setCountry(returnGoods.getCountry());
		psiProduct.setPlatform(returnGoods.getCountry());
		List<String> page =psiProductService.findCountryProduct(psiProduct);
		Map<String,Object[]> returnMap=returnGoodsService.findAllReturnCommentInfo(returnGoods);
		Map<String,Object[]> orderMap=returnGoodsService.findAllOrderInfo(returnGoods);
		Map<String,String> productSupplier =psiProductService.findSupplierByProductName();
		Map<String,PsiProductTypeGroupDict> nameAndLineMap=groupDictService.getLineByProductName();//产品名-产品线名称
		 try {
		List<String> title = Lists.newArrayList("产品","产品线","销售数量","退货数量","退货率(%)","订单数量","差评数量","差评率(%)","供应商");
		ExportExcel excel = new ExportExcel(returnGoods.getCountry()+"退货率差评率统计",title);
		for (String name : page) {
			Row row = excel.addRow();
			excel.addCell(row,0,name);
			if(nameAndLineMap.get(name)!=null){
				excel.addCell(row,1,nameAndLineMap.get(name).getName());
			}else{
				excel.addCell(row,1,"");
			}
			
    		excel.addCell(row,2,orderMap.get(name)==null?"":orderMap.get(name)[1]);
    		if(returnMap.get(name)!=null){
    			excel.addCell(row,3,returnMap.get(name)[1]);
    			if(returnMap.get(name)[1]!=null&&((BigDecimal)(returnMap.get(name)[1])).intValue()>0&&orderMap.get(name)!=null&&orderMap.get(name)[1]!=null){
    				excel.addCell(row,4,(((BigDecimal)(returnMap.get(name)[1])).intValue())*100.0/((BigDecimal)orderMap.get(name)[1]).intValue());
    			}else{
    				excel.addCell(row,4,"");
    			}
    			excel.addCell(row,5,returnMap.get(name)[3]);
    			excel.addCell(row,6,returnMap.get(name)[2]);
    			if(returnMap.get(name)[2]!=null&&((BigDecimal)(returnMap.get(name)[2])).intValue()>0&&returnMap.get(name)[3]!=null){
    				excel.addCell(row,7,(((BigDecimal)(returnMap.get(name)[2])).intValue())*100.0/((BigDecimal)(returnMap.get(name)[3])).intValue());
    			}else{
    				excel.addCell(row,7,"");
    			}
    			
    		}else{
    			excel.addCell(row,3,"");
    			excel.addCell(row,4,"");
    			excel.addCell(row,5,"");
    			excel.addCell(row,6,"");
    			excel.addCell(row,7,"");
    		}
    		if(StringUtils.isNotBlank(productSupplier.get(name))){
				excel.addCell(row,8,productSupplier.get(name));
			}else{
				excel.addCell(row,8,"");
			}
		}
		  excel.write(response, DateUtils.getDate("yyyyMMddHHmmss")+"_"+returnGoods.getCountry()+".xlsx").dispose();
   		  return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return "redirect:"+Global.getAdminPath()+"/amazoninfo/returnGoods/differentialReturnCountList";
	}
}
