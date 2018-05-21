/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PsiVatInvoiceInfo;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PsiVatInvoiceInfoService;

/**
 * 增值税发票信息Controller
 * @author Michael
 * @version 2015-06-01
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiVatInvoiceInfo")
public class PsiVatInvoiceInfoController extends BaseController {
	@Autowired
	private PsiVatInvoiceInfoService psiVatInvoiceInfoService;
	@Autowired
	private PsiSupplierService       psiSupplierService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiVatInvoiceInfo psiVatInvoiceInfo, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		Page<PsiVatInvoiceInfo> page=new Page<PsiVatInvoiceInfo>(request, response);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today =sdf.parse(sdf.format(new Date()));
		if (psiVatInvoiceInfo.getCreateDate() == null) {
			psiVatInvoiceInfo.setCreateDate(DateUtils.addYears(today, -1));
			psiVatInvoiceInfo.setInvoiceDate(today);
		}
        psiVatInvoiceInfoService.find(page, psiVatInvoiceInfo); 
        model.addAttribute("page", page);
		return "modules/psi/psiVatInvoiceInfoList";
	}
	
	
	@RequestMapping(value = "form")
	public String form(PsiVatInvoiceInfo psiVatInvoiceInfo,Model model) {
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		Map<Integer,List<String>> productMap=this.psiSupplierService.getSupplierProducts2();
		model.addAttribute("productMap", JSON.toJSON(productMap));
		if(psiVatInvoiceInfo.getSupplier()!=null&&psiVatInvoiceInfo.getSupplier().getId()!=null){
			model.addAttribute("products", productMap.get(psiVatInvoiceInfo.getSupplier().getId()));
		}
		model.addAttribute("suppliers", suppliers);
		return "modules/psi/psiVatInvoiceInfoForm";
	}
	
	@RequestMapping(value = "received")
	public String received(Date endDate,Model model) {
		if(endDate==null){
			endDate=new Date();
		}
		//收货、付款金额
		List<Object[]> receiveds = psiVatInvoiceInfoService.getInfo(endDate);
		//发票信息
		Map<String,Object> voiceMap= psiVatInvoiceInfoService.getInvoiceInfo(endDate);
		
		Map<String,Object> purchaseMap= psiVatInvoiceInfoService.findAllPurchase();
		model.addAttribute("purchaseMap",purchaseMap);
		
		model.addAttribute("receiveds", receiveds);
		model.addAttribute("voiceMap", voiceMap);
		model.addAttribute("endDate", endDate);
		return "modules/psi/psiVatInvoiceInfoReceived";
	}
	
	@RequestMapping(value = "readExcel")
	public String readExcel(PsiVatInvoiceInfo psiVatInvoiceInfo, String isPass, Model model) {
		return "modules/psi/psiVatInvoiceInfoExcel";
	}
	
	@RequestMapping(value = "excelSave")
	public String excelSave(MultipartFile excelFile,PsiVatInvoiceInfo psiVatInvoiceInfo, Model model,RedirectAttributes redirectAttributes) throws InvalidFormatException, IOException {
		String rs =this.psiVatInvoiceInfoService.excelSave(excelFile, psiVatInvoiceInfo);
		if(StringUtils.isNotEmpty(rs)){
			if(rs.length()>500){
				rs=rs.substring(0, 499).replace("\"", "'")+"...";
			}else{
				rs=rs.replace("\"", "'")+"...";
			}
			addMessage(redirectAttributes, "error:解析excel失败'" +rs);   
		}else{
			addMessage(redirectAttributes, "保存增值税发票信息成功");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiVatInvoiceInfo/?repage";
	}
	
	
	@RequestMapping(value = "save")
	public String save(PsiVatInvoiceInfo psiVatInvoiceInfo, Model model, RedirectAttributes redirectAttributes) {
		PsiSupplier supplier = psiSupplierService.get(psiVatInvoiceInfo.getSupplier().getId());
		psiVatInvoiceInfo.setSupplierName(supplier.getName());
		psiVatInvoiceInfo.setRemainingQuantity(psiVatInvoiceInfo.getQuantity());
		String rs=psiVatInvoiceInfoService.save(psiVatInvoiceInfo);
		if(StringUtils.isNotEmpty(rs)){
			addMessage(redirectAttributes, rs);
		}else{
			addMessage(redirectAttributes, "保存增值税发票信息'" + psiVatInvoiceInfo.getId() + "'成功");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiVatInvoiceInfo/?repage";
	}
	
	
	@RequestMapping(value = "delete")
	public String delete(Integer id, RedirectAttributes redirectAttributes) {
		psiVatInvoiceInfoService.delete(id);
		addMessage(redirectAttributes, "删除增值税发票信息成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiVatInvoiceInfo/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateRemark"})
	public String updateRemark(Integer id,String remark) {
		try {
			return this.psiVatInvoiceInfoService.updateRemark(id, URLDecoder.decode(remark, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return "false";
		}
	}
	
	@RequestMapping(value = {"detailList"})
	public String list(PsiVatInvoiceInfo psiVatInvoiceInfo, Model model) throws ParseException {
		model.addAttribute("psiVatInvoiceInfo",psiVatInvoiceInfoService.get(psiVatInvoiceInfo.getId()));
        model.addAttribute("detailList",psiVatInvoiceInfoService.findUseInfo(psiVatInvoiceInfo.getId()));
		return "modules/psi/psiVatInvoiceUseInfoList";
	}
	
	@RequestMapping(value = "uploadFile")
	public String uploadFile(@RequestParam("excel")MultipartFile excelFile,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			    
				try {
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/quantity/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
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
					Integer id=MathUtils.roundUp(row.getCell(0).getNumericCellValue());
				    //String name=StringUtils.trim(row.getCell(0).getStringCellValue());
				   // String invoice=StringUtils.trim(row.getCell(1).getStringCellValue());
				    Integer quantity=MathUtils.roundUp(row.getCell(3).getNumericCellValue());
				    psiVatInvoiceInfoService.updateRemainingQuantity(quantity,id);
				}
			addMessage(redirectAttributes,"文件上传成功");
		} catch (Exception e) {
			addMessage(redirectAttributes,"文件上传失败"+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiVatInvoiceInfo/?repage";
	}

	
	
	@RequestMapping(value =  "expVatInfo")
	public String expVatInfo(PsiVatInvoiceInfo psiVatInvoiceInfo, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		Page<PsiVatInvoiceInfo> page=new Page<PsiVatInvoiceInfo>(request, response,-1);
		page.setPageSize(60000);
        psiVatInvoiceInfoService.find(page, psiVatInvoiceInfo); 
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.YELLOW.index);
		style.setFillForegroundColor(HSSFColor.YELLOW.index);
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
		
		HSSFCellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		
	
		HSSFCellStyle cellStyle= wb.createCellStyle();
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		
		 List<String> title=Lists.newArrayList("开票时间","发票号","供应商","产品","数量","已用数量","金额(含税)","金额(不含税)","税额","备注");
		 HSSFSheet sheet = wb.createSheet();
   	     HSSFRow row = sheet.createRow(0);
		  row.setHeight((short) 400);
		  HSSFCell cell = null;		
		  for(int i = 0; i < title.size(); i++){
				cell = row.createCell(i);
				cell.setCellStyle(style);
				cell.setCellValue(title.get(i));
				sheet.autoSizeColumn((short)i);
		  }
		  List<PsiVatInvoiceInfo> list= page.getList();
		  int rowIndex=1;
		  for (PsiVatInvoiceInfo obj: list) {
			  int j=0;
			  row = sheet.createRow(rowIndex++);
			  row.createCell(j++).setCellValue(dateFormat.format(obj.getInvoiceDate()));
			  row.getCell(j-1).setCellStyle(contentStyle);
			  row.createCell(j++).setCellValue(obj.getInvoiceNo());
			  row.getCell(j-1).setCellStyle(contentStyle);
			  row.createCell(j++).setCellValue(obj.getSupplierName());
			  row.getCell(j-1).setCellStyle(contentStyle);
			  row.createCell(j++).setCellValue(obj.getProductName());
			  row.getCell(j-1).setCellStyle(contentStyle);
			  row.createCell(j++).setCellValue(obj.getQuantity());
			  row.getCell(j-1).setCellStyle(contentStyle);
			  
			  if(obj.getRemainingQuantity()!=obj.getQuantity()){
				  row.createCell(j++).setCellValue(obj.getQuantity()-obj.getRemainingQuantity());
			  }else if(obj.getRemainingQuantity()==obj.getQuantity()){
				  row.createCell(j++).setCellValue(0);
			  }else{
				  row.createCell(j++).setCellValue("");
			  }
			  row.getCell(j-1).setCellStyle(contentStyle);
			  
			  row.createCell(j++).setCellValue(obj.getTotalAmount().doubleValue());
			  row.getCell(j-1).setCellStyle(cellStyle);
			  
			  row.createCell(j++).setCellValue(obj.getTotalAmount().doubleValue()/1.17);
			  row.getCell(j-1).setCellStyle(cellStyle);
			  
			  row.createCell(j++).setCellValue(obj.getTotalAmount().doubleValue()-obj.getTotalAmount().doubleValue()/1.17);
			  row.getCell(j-1).setCellStyle(cellStyle);
			  
			  if(obj.getRemark()!=null){
				  row.createCell(j++).setCellValue(obj.getRemark());
			  }else{
				  row.createCell(j++).setCellValue("");
			  }
			  row.getCell(j-1).setCellStyle(contentStyle);
		  }
		  for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
		  }
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
				String fileName = "VatInfo" + sdf.format(new Date()) + ".xls";
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
