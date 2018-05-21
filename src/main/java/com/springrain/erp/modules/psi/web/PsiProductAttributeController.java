package com.springrain.erp.modules.psi.web;

import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.psi.entity.ProductSalesInfo;
import com.springrain.erp.modules.psi.entity.PsiProductAttribute;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.service.ProductSalesInfoService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.sys.utils.UserUtils;


@Controller
@RequestMapping(value = "${adminPath}/psi/psiProductAttribute")
public class PsiProductAttributeController extends BaseController {

	@Autowired
	private PsiProductAttributeService psiProductAttributeService;
	
	
	@Autowired
	private  PsiProductEliminateService psiProductEliminateService;
	
	@Autowired
	private ProductSalesInfoService 		productSalesInfoService;
	
	@Autowired
	private AmazonProductService amazonProductService;

	@ModelAttribute
	public PsiProductAttribute get(@RequestParam(required = false) Integer id) {
		if (id != null) {
			return psiProductAttributeService.get(id);
		} else {
			return new PsiProductAttribute();
		}
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiProductAttribute psiProductAttribute, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<PsiProductAttribute> page=psiProductAttributeService.findAll();
		model.addAttribute("page",page);
	    //产品定位
	    Map<String, String> productPositionMap = psiProductEliminateService.findProductPositionByCountry(null);
		model.addAttribute("productPositionMap", productPositionMap);
		//产品名_国家
		Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();
		model.addAttribute("fancha", fancha);
		Date date = new Date();
		Map<String, String> yearWeek = Maps.newHashMap();
		yearWeek.put("0", getWeekOfYear(date));
		yearWeek.put("1", getWeekOfYear(DateUtils.addWeeks(date, 1)));
		yearWeek.put("2", getWeekOfYear(DateUtils.addWeeks(date, 2)));
		yearWeek.put("3", getWeekOfYear(DateUtils.addWeeks(date, 3)));
		model.addAttribute("yearWeek", yearWeek);	//xx年xx周
		Map<String,Map<String,String>> purchaseMap=psiProductAttributeService.findPurchaseWeek2();
		model.addAttribute("purchaseMap", purchaseMap);
		return "modules/psi/psiMaxInventoryList";
	}
	
	@RequestMapping(value = {"listByCountry"})
	public String listByCountry(PsiProductEliminate psiProductEliminate, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<String> isSalelList = Lists.newArrayList("1","2","3","5");//5为未初始化状态
		Page<PsiProductEliminate> page = psiProductEliminateService.findByPage(new Page<PsiProductEliminate>(request, response), psiProductEliminate,isSalelList); 
		model.addAttribute("page", page);
		//产品名_国家
		Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();
		model.addAttribute("fancha", fancha);
		model.addAttribute("psiProductEliminate", psiProductEliminate);
		return "modules/psi/psiMaxInventoryListByCountry";
	}
	
	@ResponseBody
	@RequestMapping(value = "batchTranType")
	public String batchTranType(@RequestParam("eid[]")Integer[] eid, Integer transportType , Model model, RedirectAttributes redirectAttributes) {
		for (Integer id : eid) {
			PsiProductEliminate eliminate = psiProductEliminateService.get(id);
			eliminate.setTransportType(transportType);
			psiProductEliminateService.save(eliminate);
			if ("de".equals(eliminate.getCountry()) && eliminate.getProduct()!=null && eliminate.getProduct().getFanOu()) {
				//可泛欧产品,德国数据同步更新至欧洲
				psiProductEliminateService.updateAttrByEu(eliminate);
			}
		}
		return "1";
	}


	@ResponseBody
	@RequestMapping(value = {"updateMaxInventory"})
	public String updateTotalGoal(PsiProductAttribute psiMaxInventory,String flag) {
		if ("1".equals(flag)) {
			psiMaxInventory.setCreateDate(new Date());
			psiMaxInventory.setCreateUser(UserUtils.getUser());
		} 
		/*else if("3".equals(flag) && "1".equals(psiMaxInventory.getIsMain())){	//主力产品的运输方式改成海运
			psiMaxInventory.setTransportType(1);
		}*/
		psiProductAttributeService.update(psiMaxInventory, flag);
		return "1";
	}
	
	

	/**
	 * 取得当前日期是多少周
	 * @param date
	 * @return
	 */
	private static String getWeekOfYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setMinimalDaysInFirstWeek(1);
		c.setTime(date);
		String w = DateFormatUtils.format(date, "w");
		return c.get(Calendar.YEAR) + "年第" + w + "周";

	}
	
	
	@RequestMapping(value = {"export"})
	public String export(HttpServletRequest request, HttpServletResponse response) throws ParseException, UnsupportedEncodingException {
			int excelNo =1;
		    XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet();
			XSSFRow row = sheet.createRow(0);
			XSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			List<String> countryList=Lists.newArrayList("de","fr","it","es","uk","com","ca","jp");
			//String[] title = {"产品","最大库存","缓存周期","主力","运输方式","采购周","运输周","滚动31日销","生产周期"};
			String[] title = {"产品","最大库存","采购周","运输周","滚动31日销","生产周期","摄影师"};
			
			style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			XSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 11); // 字体高度
			style.setFont(font);
			row.setHeight((short) 400);
			XSSFCell cell = null;		
			for (int i = 0; i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
			List<PsiProductAttribute> page=psiProductAttributeService.findAll();
			//List<String> salesName=psiProductEliminateService.findIsSaleProductName();
			//产品名_国家
			Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();
			Date date = new Date();
			Map<String, String> yearWeek = Maps.newHashMap();
			yearWeek.put("0", getWeekOfYear(date));
			yearWeek.put("1", getWeekOfYear(DateUtils.addWeeks(date, 1)));
			yearWeek.put("2", getWeekOfYear(DateUtils.addWeeks(date, 2)));
			yearWeek.put("3", getWeekOfYear(DateUtils.addWeeks(date, 3)));
			
			for (PsiProductAttribute product: page) {
				  row = sheet.createRow(excelNo++);
				  int i =0;
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(product.getColorName());
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(product.getQuantity()==null?0:product.getQuantity());
				  if(product.getPurchaseWeek()==null){
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(""); 
				  }else if(product.getPurchaseWeek()==0){
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(yearWeek.get("0"));
				  }else if(product.getPurchaseWeek()==1){
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(yearWeek.get("1"));
				  }else if(product.getPurchaseWeek()==2){
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(yearWeek.get("2"));
				  }else if(product.getPurchaseWeek()==3){
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(yearWeek.get("3"));
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(product.getTransportWeekStr());
				  Integer day31Sales=0;
				  for (String country : countryList) {
					if(fancha!=null&&fancha.get(product.getColorName()+"_"+country)!=null&&fancha.get(product.getColorName()+"_"+country).getDay31Sales()!=null){
						day31Sales+=fancha.get(product.getColorName()+"_"+country).getDay31Sales();
					}
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(day31Sales);
				  if(product.getProduct()==null||product.getProduct().getProducePeriod()==null){
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(product.getProduct().getProducePeriod()); 
				  }
				  if(product.getCameraman()==null){
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(product.getCameraman()); 
				  }
				  
			}

			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fileName = "attribute-不分平台" + sdf.format(new Date()) + ".xlsx";
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
	
	@RequestMapping(value = {"exportByCountry"})
	public String exportByCountry(HttpServletRequest request, HttpServletResponse response) throws ParseException, UnsupportedEncodingException {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet();
		XSSFRow row = sheet.createRow(0);
		XSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		XSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 11); // 字体高度
		style.setFont(font);
		row.setHeight((short) 400);
		XSSFCell cell = null;
		String[] title = {"产品","国家","缓冲周期","运输方式","滚动31日销","生产周期","佣金"};
		for (int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		List<String> isSale = Lists.newArrayList("1","2","3");	//在售(非淘汰)的产品定位标记
		List<PsiProductEliminate> list = psiProductEliminateService.findProductEliminateList(null, null, null, isSale, null);
		//产品名_国家
		Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();

		int excelNo =1;
		for (PsiProductEliminate product: list) {
			  row = sheet.createRow(excelNo++);
			  int i =0;
			  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(product.getColorName());
			  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(product.getCountry())?"us":product.getCountry());
			  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(product.getBufferPeriod()==null?0:product.getBufferPeriod());
			  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((product.getTransportType()!=null&&product.getTransportType()==1)?"海运":"空运");
			  Integer day31Sales=0;
			  String key = product.getColorName() + "_" + product.getCountry();
			  if (fancha.get(key) != null) {
				day31Sales = fancha.get(key).getDay31Sales();
			  }
			  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(day31Sales);
			  if(product.getProduct()==null||product.getProduct().getProducePeriod()==null){
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			  }else{
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(product.getProduct().getProducePeriod()); 
			  }
			  if(product.getCommissionPcent()==null){
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			  }else{
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(product.getCommissionPcent()); 
			  }
		}

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "attribute-区分平台" + sdf.format(new Date()) + ".xlsx";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
		try {
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("attribute-区分平台导出异常", e);
		}
		return null;
	}
	
	
	@RequestMapping(value = "uploadFile")
	public String uploadFile(@RequestParam("excel")MultipartFile excelFile,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			    
				try {
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/cameraman/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					File baseDir = new File(baseDirStr); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String name = excelFile.getOriginalFilename();
					File dest = new File(baseDir,name);
					if(dest.exists()){
						dest.delete();
					}
					FileUtils.copyInputStreamToFile(excelFile.getInputStream(),dest);
				}catch(Exception e){}	
			
				Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				// 循环行Row
				
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						continue;
					}
				    
				    try{
				    	String name=StringUtils.trim(row.getCell(0).getStringCellValue());
				    	String color="";
				    	String productName=name;
				    	if(name.indexOf("_")>0){
				    		color=name.substring(name.lastIndexOf("_")+1);
				    		productName=name.substring(0, name.lastIndexOf("_"));
				    	}
					    String cameraman=StringUtils.trim(row.getCell(1).getStringCellValue());
					    psiProductAttributeService.updateCameraman(cameraman,productName, color);
				    }catch(Exception e){}
				}
			addMessage(redirectAttributes,"文件上传成功");
		} catch (Exception e) {
			addMessage(redirectAttributes,"文件上传失败"+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiProductAttribute";
	}
}
