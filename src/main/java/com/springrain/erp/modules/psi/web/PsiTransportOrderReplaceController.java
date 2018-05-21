/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
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

import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.config.LogisticsSupplier;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderReplace;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderReplaceService;
import com.springrain.erp.modules.sys.utils.UserUtils;
/**
 * 运单表Controller
 * @author Michael
 * @version 2015-01-15
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiTransportOrderReplace")
public class PsiTransportOrderReplaceController extends BaseController{
	
	@Autowired
	private PsiTransportOrderReplaceService 		psiTransportOrderReplaceService;
	@Autowired
	private PsiSupplierService 						psiSupplierService;
	
	
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiTransportOrderReplace psiTransportOrderReplace, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		
		 HttpSession session = request.getSession(true);
		if(psiTransportOrderReplace.getCreateDate()!=null){
			if(session.isNew()){   
			   session.setAttribute("psiTransport_createDate", psiTransportOrderReplace.getCreateDate());
		    }
		}else{
			Date date=(Date)session.getAttribute("psiTransport_createDate");
			if(date!=null){
				psiTransportOrderReplace.setCreateDate(date);
			}else{
				psiTransportOrderReplace.setCreateDate(DateUtils.addMonths(today, -3));
			}
		}
		
		Page<PsiTransportOrderReplace> page = new Page<PsiTransportOrderReplace>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("id desc");
		}else{
			page.setOrderBy(orderBy);
		}	
		
		List<PsiSupplier> tranSuppliers = this.psiSupplierService.findAllTransporter();
        page = psiTransportOrderReplaceService.find(page, psiTransportOrderReplace); 
        
        model.addAttribute("tranSuppliers", tranSuppliers);
        model.addAttribute("site",LogisticsSupplier.getWebSite());
        model.addAttribute("page", page);
		return "modules/psi/psiTransportOrderReplaceList";
	}
	

	

	@RequestMapping(value = "edit")
	public String edit(PsiTransportOrderReplace psiTransportOrderReplace, Model model) throws IOException {
		List<PsiSupplier> tranSuppliers=this.psiSupplierService.findAllTransporter();
		if(psiTransportOrderReplace.getId()!=null){
			psiTransportOrderReplace=this.psiTransportOrderReplaceService.get(psiTransportOrderReplace.getId());
		}
		String currencyStr ="CNY,USD,EUR,JPY,CAD,GBP";
		List<String> currencys=Lists.newArrayList(currencyStr.split(","));
		model.addAttribute("tranSuppliers", tranSuppliers);
		model.addAttribute("currencys", currencys);
		model.addAttribute("psiTransportOrderReplace", psiTransportOrderReplace);
		return "modules/psi/psiTransportOrderReplaceEdit";
	}
	
	
	@RequestMapping(value = "cancel")
	public String cancel(PsiTransportOrderReplace psiTransportOrderReplace, Model model, RedirectAttributes redirectAttributes) throws IOException {
		if(psiTransportOrderReplace.getId()!=null){
			psiTransportOrderReplace=this.psiTransportOrderReplaceService.get(psiTransportOrderReplace.getId());
			psiTransportOrderReplace.setReplaceSta("8");
			psiTransportOrderReplace.setCancelDate(new Date());
			psiTransportOrderReplace.setCancelUser(UserUtils.getUser());
		}
		addMessage(redirectAttributes, "取消运单'" + psiTransportOrderReplace.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrderReplace/?repage";
	}
	
	@RequestMapping(value = "view")
	public String view(PsiTransportOrderReplace psiTransportOrderReplace, Model model) throws IOException {
		List<PsiSupplier> tranSuppliers=this.psiSupplierService.findAllTransporter();
		if(psiTransportOrderReplace.getId()!=null){
			psiTransportOrderReplace=this.psiTransportOrderReplaceService.get(psiTransportOrderReplace.getId());
		}
		String currencyStr ="CNY,USD,EUR,JPY,CAD,GBP";
		List<String> currencys=Lists.newArrayList(currencyStr.split(","));
		model.addAttribute("tranSuppliers", tranSuppliers);
		model.addAttribute("currencys", currencys);
		model.addAttribute("psiTransportOrderReplace", psiTransportOrderReplace);
		return "modules/psi/psiTransportOrderReplaceView";
	}
	
	
	@RequestMapping(value = "editSave")
	public String editSave(PsiTransportOrderReplace psiTransportOrderReplace, Model model,@RequestParam("localFile")MultipartFile[] localFile,@RequestParam("tranFile")MultipartFile[] tranFile,@RequestParam("dapFile")MultipartFile[] dapFile,@RequestParam("otherFile")MultipartFile[] otherFile,@RequestParam("insuranceFile")MultipartFile[] insuranceFile,@RequestParam("taxFile")MultipartFile[] taxFile, RedirectAttributes redirectAttributes) throws IOException {
		this.psiTransportOrderReplaceService.editSaveData(psiTransportOrderReplace, localFile, tranFile, dapFile, otherFile, insuranceFile, taxFile);
		addMessage(redirectAttributes, "编辑运单'" + psiTransportOrderReplace.getId() + "'成功");   
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrderReplace/?repage";
	}
	
	
	@RequestMapping(value="upload")
	@ResponseBody
	public  String uploadFile(String psiTransportId,MultipartFile uploadFile,String uploadType,HttpServletResponse response,RedirectAttributes redirectAttributes) throws UnsupportedEncodingException{
		if(uploadFile.getSize()!=0){
			PsiTransportOrderReplace psiTransportOrderReplace=this.psiTransportOrderReplaceService.get(Integer.parseInt(psiTransportId));
			String psiTransportNo=psiTransportOrderReplace.getId()+"";
			
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransportReplace/"+psiTransportNo;
			File baseDir = new File(baseDirStr); 
			if(!baseDir.exists()){
				boolean aa=baseDir.mkdirs();
			}
			
			String name = uploadFile.getOriginalFilename();
			String suffix = uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf(".")); 
			String suffixName="";
			String[] suffixArr=StringUtils.isBlank(psiTransportOrderReplace.getSuffixName())?"PI-PL-WB-TI".split("-"):psiTransportOrderReplace.getSuffixName().split("-");
			String suffixStr=StringUtils.isBlank(psiTransportOrderReplace.getSuffixName())?"PI-PL-WB-TI":psiTransportOrderReplace.getSuffixName();
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
				suffixName= suffix+"-"+(suffixStr.substring(index.get(0)+1)==null?"":suffixStr.substring(index.get(0)+1));
			}else if("2".equals(uploadType)){//PL
				name=psiTransportNo+"_PL"+suffix;
				suffixName=suffixStr.substring(0,index.get(0))+"-"+suffix+"-"+(suffixStr.substring(index.get(1)+1)==null?"":suffixStr.substring(index.get(1)+1));
			}else if("0".equals(uploadType)){//运单 0 WB
				name=psiTransportNo+"_WB"+suffix;
				suffixName=suffixStr.substring(0,index.get(1))+"-"+suffix+"-"+(suffixStr.substring(index.get(2)+1)==null?"":suffixStr.substring(index.get(2)+1));
			}else if("3".equals(uploadType)){//运输发票 TI
				name=psiTransportNo+"_TI"+suffix;
				if(index.get(3)+1<suffixStr.length()){
					suffixName=suffixStr.substring(0,index.get(2))+"-"+suffix+"-"+(suffixStr.substring(index.get(3)+1)==null?"":suffixStr.substring(index.get(3)+1));
				}else{
					suffixName=suffixStr.substring(0,index.get(2))+"-"+suffix;
				}
			}
			
			File dest = new File(baseDir,name);
			try {
				FileUtils.copyInputStreamToFile(uploadFile.getInputStream(),dest);
				this.psiTransportOrderReplaceService.updateSuffixName(Integer.parseInt(psiTransportId), suffixName);
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
	}
	
	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/psi/psiTransportReplace";  
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
	@RequestMapping(value =  "expTransport" )
	public String expTransport(PsiTransportOrderReplace psiTransportOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
			SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			int excelNo =1;
			
		    List<PsiTransportOrderReplace> list=this.psiTransportOrderReplaceService.findList(psiTransportOrder); 
		    if(list.size()>65535){
		    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
		    }
		    
		    HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	
			String[] title = { "  CreateDate ","  NO. ","localAmount","货币类型","tranAmount","货币类型","dapAmount","货币类型","insuranceAmount","货币类型","otherAmount","货币类型","dutyTaxes" ,"taxTaxes","insuranceAmount"};
			
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
			 for(PsiTransportOrderReplace transportOrder: list){
				 int i =0;
				 row = sheet.createRow(excelNo++);
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sdf1.format(transportOrder.getCreateDate()));
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getId());
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getLocalAmountIn()!=null?(transportOrder.getLocalAmountIn()+""):"");
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(StringUtils.isNotEmpty(transportOrder.getCurrencyIn1())?transportOrder.getCurrencyIn1():"");
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getTranAmountIn()!=null?(transportOrder.getTranAmountIn()+""):"");
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(StringUtils.isNotEmpty(transportOrder.getCurrencyIn2())?transportOrder.getCurrencyIn2():"");
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getDapAmountIn()!=null?(transportOrder.getDapAmountIn()+""):"");
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(StringUtils.isNotEmpty(transportOrder.getCurrencyIn3())?transportOrder.getCurrencyIn3():"");
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getOtherAmountIn()!=null?(transportOrder.getOtherAmountIn()+""):"");
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(StringUtils.isNotEmpty(transportOrder.getCurrencyIn4())?transportOrder.getCurrencyIn4():"");
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getDutyTaxesIn()!=null?(transportOrder.getDutyTaxesIn()+""):"");
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getTaxTaxesIn()!=null?(transportOrder.getTaxTaxesIn()+""):"");
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(transportOrder.getInsuranceAmountIn()!=null?(transportOrder.getInsuranceAmountIn()+""):"");
			  }
			
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
	
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	
		String fileName = "TransprotOrderReplaceData" + sdf.format(new Date()) + ".xls";
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
