package com.springrain.erp.modules.psi.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PurchaseOrderService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/psi/supplier")
public class PsiSupplierController extends BaseController {

	@Autowired
	private PsiSupplierService psiSupplierService;
	
	@Autowired
	private PurchaseOrderService purchaseOrderService;

	@ModelAttribute
	public PsiSupplier get(@RequestParam(required = false) Integer id) {
		if (id != null) {
			return psiSupplierService.get(id);
		} else {
			return new PsiSupplier();
		}
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "list", "" })
	public String list(PsiSupplier supplier, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<PsiSupplier> page = new Page<PsiSupplier>(request, response);
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id asc");
		} else {
			page.setOrderBy(orderBy + ",id asc");
		}
		page.setPageSize(20);
		page.setOrderBy(orderBy);
		
		psiSupplierService.find(page, supplier);
		model.addAttribute("page", page);
		return "modules/psi/psiSupplierList";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "view")
	public String view(PsiSupplier psiSupplier, Model model) {
		model.addAttribute("supplier", psiSupplier);
		return "modules/psi/psiSupplierView";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"add","update"})
	public String addOrUpdate(PsiSupplier psiSupplier, Model model) {
		model.addAttribute("supplier", psiSupplier);
		if(psiSupplier.getId()==null){
			model.addAttribute("canEdit", true);
		}else{
			model.addAttribute("canEdit", !purchaseOrderService.hasUnDoneOrder(psiSupplier.getId()));
		}
		return "modules/psi/psiSupplierForm";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "save")
	public String save(PsiSupplier psiSupplier,	@RequestParam("attchmentFile")MultipartFile[] attchmentFiles,@RequestParam("reviewFile")MultipartFile[] reviewFiles,RedirectAttributes redirectAttributes) {
		String time  = new Date().getTime()/1000+"";
		User user = UserUtils.getUser();
		if(psiSupplier.getId()!=null){
			psiSupplier.setUptime(Integer.parseInt(time));
			psiSupplier.setUpdateUser(user);
		}else{
			psiSupplier.setDelFlag("0"); //逻辑删除标志，0代表正常
			psiSupplier.setEliminate("1");//1:非淘汰
			psiSupplier.setAddtime(Integer.parseInt(time));
			psiSupplier.setCreateUser(user);
		}
		
		
		for (MultipartFile attchmentFile : attchmentFiles) {
			if(attchmentFile.getSize()!=0){
				String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/supplier";
				File baseDir = new File(baseDirStr+"/"+psiSupplier.getNikename()); 
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
				String name=UUID.randomUUID().toString()+suffix;
				File dest = new File(baseDir,name);
				try {
					FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
					psiSupplier.setAttchmentPathAppend("/psi/supplier/"+psiSupplier.getNikename()+"/"+name);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String curDateStr = sdf.format(new Date());
		String [] flows=getFlowNo(psiSupplier.getReviewPath(), curDateStr, reviewFiles.length);
		for (int i =0;i<reviewFiles.length;i++) {
			MultipartFile attchmentFile = reviewFiles[i];
			if(attchmentFile.getSize()!=0){
				String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/supplier";
				File baseDir = new File(baseDirStr+"/"+psiSupplier.getNikename()); 
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
				String name=flows[i]+suffix;
				File dest = new File(baseDir,name);
				try {
					FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
					psiSupplier.setReviewPathAppend("/psi/supplier/"+psiSupplier.getNikename()+"/"+name);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
		}
		
		psiSupplierService.save(psiSupplier);
		addMessage(redirectAttributes, "供应商更新成功！");
		return "redirect:" + Global.getAdminPath() + "/psi/supplier/list";
	}
	
	
	
	public String[] getFlowNo(String reviewPath,String curDateStr,int size){
		String [] flowNos = new String[size];
		if(StringUtils.isNotEmpty(reviewPath)&&reviewPath.indexOf(curDateStr)>=0){
			//获取最后一个名字号
			String[] filePaths=reviewPath.split(",");
			String lastFilePath = filePaths[filePaths.length-1];
			String curFlowNo = lastFilePath.substring(lastFilePath.lastIndexOf("/")+1,lastFilePath.lastIndexOf(".")); 
			for(int i=0;i<size;i++){
				try{
					flowNos[i]=((Integer)(Integer.parseInt(curFlowNo)+i+1)).toString();
				}catch(Exception ex){
					curFlowNo=curDateStr+"01";
				}
			}
		}else{
			//为空或者当天不存在从0开始计数
			for(int i=0;i<size;i++){
				flowNos[i]=curDateStr+String.format("%02d", 1+i);
			}
		}
		return flowNos;
	}

	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "delete")
	public String delete(PsiSupplier psiSupplier,
			RedirectAttributes redirectAttributes) {
		int i = psiSupplierService.delete(psiSupplier.getId());
		if (i > 0) {
			addMessage(redirectAttributes, "供应商删除成功！");
		} else {
			addMessage(redirectAttributes, "供应商删除失败！");
		}
		return "redirect:" + Global.getAdminPath() + "/psi/supplier/list";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "eliminate")
	public String eliminate(PsiSupplier psiSupplier,
			RedirectAttributes redirectAttributes) {
		String type=psiSupplier.getEliminate();
		int i = psiSupplierService.eliminate(psiSupplier.getId(),type);
		if (i > 0) {
			addMessage(redirectAttributes, "供应商"+("0".equals(type)?"淘汰":"非淘汰")+"成功！");
		} else {
			addMessage(redirectAttributes, "供应商"+("0".equals(type)?"淘汰":"非淘汰")+"失败！");
		}
		return "redirect:" + Global.getAdminPath() + "/psi/supplier/list";
	}
	
	@RequiresPermissions("psi:all:view")
	@ResponseBody
	@RequestMapping(value = "nameIsExist")
	public String nameIsExist(String name,String oldName) {
		if(StringUtils.isBlank(name)){
			return "true";
		}
		if(StringUtils.isNotBlank(oldName)){
			if(oldName.equals(name)){
				return "true";
			}
		}
	    return (!psiSupplierService.nameIsExsit(name))+"";
	}
	
	
	@RequestMapping(value="upload")
	@ResponseBody
	public  String uploadFile(String id,MultipartFile uploadFile,String uploadType,HttpServletResponse response,RedirectAttributes redirectAttributes) throws UnsupportedEncodingException{
		if(uploadFile.getSize()!=0){
			PsiSupplier supplier=this.psiSupplierService.get(Integer.parseInt(id));
			int nikeName=supplier.getId();
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/supplier/"+nikeName;
			File baseDir = new File(baseDirStr); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String name = uploadFile.getOriginalFilename();
			String suffix = uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf(".")); 
			String suffixName="";
			String[] suffixArr=StringUtils.isBlank(supplier.getSuffixName())?"BL-TR-ISO-BI-PPT-BC".split("-"):supplier.getSuffixName().split("-");
			if("0".equals(uploadType)){//0">营业执照复印件 BL
				name = nikeName+"_BL"+suffix;
				suffixName=suffix+"-"+suffixArr[1]+"-"+suffixArr[2]+"-"+suffixArr[3]+"-"+suffixArr[4]+"-"+suffixArr[5];
			}else if("1".equals(uploadType)){//1">税务登记复印件 TR
				name = nikeName+"_TR"+suffix;
				 suffixName=suffixArr[0]+"-"+suffix+"-"+suffixArr[2]+"-"+suffixArr[3]+"-"+suffixArr[4]+"-"+suffixArr[5];
			}else if("2".equals(uploadType)){//2">ISO认证及其他认证复印件 ISO
				name=nikeName+"_ISO"+suffix;
				 suffixName=suffixArr[0]+"-"+suffixArr[1]+"-"+suffix+"-"+suffixArr[3]+"-"+suffixArr[4]+"-"+suffixArr[5];
			}else if("3".equals(uploadType)){//3">银行资料 BI
				name=nikeName+"_BI"+suffix;
				 suffixName=suffixArr[0]+"-"+suffixArr[1]+"-"+suffixArr[2]+"-"+suffix+"-"+suffixArr[4]+"-"+suffixArr[5];
			}else if("4".equals(uploadType)){//4">公司介绍PPT PPT
				name=nikeName+"_PPT"+suffix;
				suffixName=suffixArr[0]+"-"+suffixArr[1]+"-"+suffixArr[2]+"-"+suffixArr[3]+"-"+suffix+"-"+suffixArr[5];
			}else{//5">基本资料统计 BC
				name=nikeName+"_BC"+suffix;
				suffixName=suffixArr[0]+"-"+suffixArr[1]+"-"+suffixArr[2]+"-"+suffixArr[3]+"-"+suffixArr[4]+"-"+suffix;
			}
			File dest = new File(baseDir,name);
			try {
				FileUtils.copyInputStreamToFile(uploadFile.getInputStream(),dest);
				this.psiSupplierService.updateSuffixName(Integer.parseInt(id), suffixName);
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

	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "exp")
	public String exp(PsiSupplier supplier, Model model, HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		Page<PsiSupplier> page  =new Page<PsiSupplier>(request, response);
		page.setPageSize(600000);
		List<PsiSupplier> list = psiSupplierService.find(page,supplier).getList(); 
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		String[] title = { " 序号  ","   供应商名称     ","  类型   ","  简称 "," 定金比例  " ," 货币类型 "," 联系人  ","  电话   ","  邮箱   ","  QQ ","  联系地址   "};
	    style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
	    style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
	    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    //设置字体
	    HSSFFont font = wb.createFont();
	    font.setFontHeightInPoints((short) 11); // 字体高度
	    style.setFont(font);
	    row.setHeight((short) 400);
	    HSSFCell cell = null;		
	    for (int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}
	    
		int j =1;
		for(PsiSupplier sup:list){
			int i =0;
			row = sheet.createRow(j++);
			String typeStr = "";
			if("0".equals(sup.getType())){
				typeStr="产品供应商";
			}else if("1".equals(sup.getType())){
				typeStr="物流供应商";
			}else if("2".equals(sup.getType())){	
				typeStr="包材供应商";
			}else{
				typeStr="其他";
			}
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getId()); 
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getName()); 
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(typeStr);
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getNikename());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getDeposit()+"%");
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getCurrencyType());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getContact());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getPhone());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getMail());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getQq());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getAddress());
		}
		
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		String fileName = "suppliers" + sdf.format(new Date()) + ".xls";
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
	
	@RequestMapping(value = "download")
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)  throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir();  
        String downLoadPath = ctxPath + fileName;
        fileName = fileName.substring(fileName.lastIndexOf("/")+1);
        try {   
            long fileLength = new File(downLoadPath).length();   
            response.setContentType("application/x-msdownload;");   
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName,"utf-8"));   
            response.setHeader("Content-Length", String.valueOf(fileLength));   
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));   
            bos = new BufferedOutputStream(response.getOutputStream());   
            byte[] buff = new byte[2048];   
            int bytesRead;   
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {   
                bos.write(buff, 0, bytesRead);   
            }   
        } catch (Exception e) {   
            logger.error("供应商附件下载失败！", e);
        } finally {   
            if (bis != null)   
                bis.close();   
            if (bos != null)   
                bos.close();   
        }   
        return null;   
    }
    
	@ResponseBody
	@RequestMapping(value = "shortNameIsExist")
	public String shortNameIsExist(String name,String oldName) {
		if(StringUtils.isBlank(name)){
			return "true";
		}
		if(StringUtils.isNotBlank(oldName)){
			if(oldName.equals(name)){
				return "true";
			}
		}
	    return (!psiSupplierService.shortNameIsExsit(name))+"";
	}
}
