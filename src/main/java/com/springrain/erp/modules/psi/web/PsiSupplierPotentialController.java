package com.springrain.erp.modules.psi.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplierPotential;
import com.springrain.erp.modules.psi.service.PsiSupplierPotentialService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/psi/supplierPotential")
public class PsiSupplierPotentialController extends BaseController {

	@Autowired
	private PsiSupplierPotentialService psiSupplierPotentialService;

	@ModelAttribute
	public PsiSupplierPotential get(@RequestParam(required = false) Integer id) {
		if (id != null) {
			return psiSupplierPotentialService.get(id);
		} else {
			return new PsiSupplierPotential();
		}
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "list", "" })
	public String list(PsiSupplierPotential supplierPotential, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<PsiSupplierPotential> page = new Page<PsiSupplierPotential>(request, response);
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id asc");
		} else {
			page.setOrderBy(orderBy + ",id asc");
		}
		page.setPageSize(20);
		page.setOrderBy(orderBy);

		psiSupplierPotentialService.find(page, supplierPotential);
		model.addAttribute("page", page);
		return "modules/psi/psiSupplierPotentialList";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "view")
	public String view(PsiSupplierPotential psiSupplierPotential, Model model) {
		model.addAttribute("supplierPotential", psiSupplierPotential);
		return "modules/psi/psiSupplierPotentialView";
	}

	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "save")
	public String save(PsiSupplierPotential psiSupplierPotential,
			RedirectAttributes redirectAttributes) {
		String time  = new Date().getTime()/1000+"";
		User user = UserUtils.getUser();
		if(psiSupplierPotential.getId()!=null){
			psiSupplierPotential.setUptime(Integer.parseInt(time));
			psiSupplierPotential.setUpdateUser(user);
		}else{
			psiSupplierPotential.setCreateRegularFlag("0");  //未生成正式供应商
			psiSupplierPotential.setDelFlag("0"); //逻辑删除标志，0代表正常
			psiSupplierPotential.setAddtime(Integer.parseInt(time));
			psiSupplierPotential.setCreateUser(user);
		}
		psiSupplierPotentialService.save(psiSupplierPotential);
		addMessage(redirectAttributes, "潜在供应商更新成功！");
		return "redirect:" + Global.getAdminPath() + "/psi/supplierPotential/list";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "genPotential")
	public String genPotential(PsiSupplierPotential psiSupplierPotential,RedirectAttributes redirectAttributes) {
		this.psiSupplierPotentialService.gen(psiSupplierPotential);
		addMessage(redirectAttributes, "根据潜在供应商生成正式供应商成功！");
		return "redirect:" + Global.getAdminPath() + "/psi/supplierPotential/list";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "delete")
	public String delete(PsiSupplierPotential psiSupplierPotential,
			RedirectAttributes redirectAttributes) {
		int i = psiSupplierPotentialService.delete(psiSupplierPotential.getId());
		if (i > 0) {
			addMessage(redirectAttributes, "潜在供应商删除成功！");
		} else {
			addMessage(redirectAttributes, "潜在供应商删除失败！");
		}
		return "redirect:" + Global.getAdminPath() + "/psi/supplierPotential/list";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"add","update"})
	public String addOrUpdate(PsiSupplierPotential psiSupplierPotential, Model model) {
		model.addAttribute("supplierPotential", psiSupplierPotential);
		return "modules/psi/psiSupplierPotentialForm";
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
	    return (!psiSupplierPotentialService.nameIsExsit(name))+"";
	}
	
	
	@RequestMapping(value="upload")
	@ResponseBody
	public  String uploadFile(String id,MultipartFile uploadFile,String uploadType,HttpServletResponse response,RedirectAttributes redirectAttributes) throws UnsupportedEncodingException{
		if(uploadFile.getSize()!=0){
			PsiSupplierPotential supplierPotential=this.psiSupplierPotentialService.get(Integer.parseInt(id));
			int nikeName=supplierPotential.getId();
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/supplier/"+nikeName;
			File baseDir = new File(baseDirStr); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String name = uploadFile.getOriginalFilename();
			String suffix = uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf(".")); 
			String suffixName="";
			String[] suffixArr=StringUtils.isBlank(supplierPotential.getSuffixName())?"BL-TR-ISO-BI-PPT-BC".split("-"):supplierPotential.getSuffixName().split("-");
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
				this.psiSupplierPotentialService.updateSuffixName(Integer.parseInt(id), suffixName);
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
	public String exp(PsiSupplierPotential supplierPotential, Model model, HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		Page<PsiSupplierPotential> page  =new Page<PsiSupplierPotential>(request, response);
		page.setPageSize(600000);
		List<PsiSupplierPotential> list = psiSupplierPotentialService.find(page,supplierPotential).getList(); 
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		String[] title = { " 序号  ","   潜在供应商名称     ","  类型   ","  简称 "," 定金比例  " ," 货币类型 "," 联系人  ","  电话   ","  邮箱   ","  QQ ","  联系地址   "};
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
		for(PsiSupplierPotential sup:list){
			int i =0;
			row = sheet.createRow(j++);
			String typeStr = "";
			if("0".equals(sup.getType())){
				typeStr="产品潜在供应商";
			}else if("1".equals(sup.getType())){
				typeStr="物流潜在供应商";
			}else if("2".equals(sup.getType())){	
				typeStr="包材潜在供应商";
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
		String fileName = "supplierPotentials" + sdf.format(new Date()) + ".xls";
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
	    return (!psiSupplierPotentialService.shortNameIsExsit(name))+"";
	}
}
