/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiQuestionSupplier;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiQuestionSupplierService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;

/**
 * 贴码错误信息记录Controller
 * @author Michael
 * @version 2015-06-01
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiQuestionSupplier")
public class PsiQuestionSupplierController extends BaseController {
	
	@Autowired
	private PsiQuestionSupplierService psiQuestionSupplierService;
	@Autowired
	private PsiSupplierService         psiSupplierService;
	@Autowired
	private PsiProductService          psiProductService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiQuestionSupplier psiQuestionSupplier, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PsiQuestionSupplier> page=new Page<PsiQuestionSupplier>(request, response);
        psiQuestionSupplierService.find(page, psiQuestionSupplier); 
        List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		List<PsiProduct>  products =this.psiProductService.findAll();
		model.addAttribute("products", products);
		model.addAttribute("suppliers", suppliers);
        model.addAttribute("page", page);
		return "modules/psi/psiQuestionSupplierList";
	}
	
	@RequestMapping(value = "form")
	public String form(PsiQuestionSupplier psiQuestionSupplier, String isPass, Model model) {
		if(psiQuestionSupplier.getId()!=null){
			psiQuestionSupplier=this.psiQuestionSupplierService.get(psiQuestionSupplier.getId());
		}
		if(psiQuestionSupplier.getQuestionDate()!=null){
			psiQuestionSupplier.setQuestionDate(new Date());
		}
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		Map<Integer,List<String>> productMap=this.psiSupplierService.getSupplierProducts();
		model.addAttribute("productMap", JSON.toJSON(productMap));
		if(psiQuestionSupplier.getSupplier()!=null&&psiQuestionSupplier.getSupplier().getId()!=null){
			model.addAttribute("products", productMap.get(psiQuestionSupplier.getSupplier().getId()));
		}
		
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("psiQuestionSupplier", psiQuestionSupplier);
		if (StringUtils.isEmpty(isPass) && StringUtils.isNotEmpty(psiQuestionSupplier.getResult())) {
			if (psiQuestionSupplier.getResult().contains("OK")) {
				isPass = "1";
			} else {
				isPass = "0";
			}
		}
		model.addAttribute("isPass", isPass);
		return "modules/psi/psiQuestionSupplierForm";
	}
	
	
	@RequestMapping(value = "view")
	public String view(PsiQuestionSupplier psiQuestionSupplier, Model model) {
	    if(psiQuestionSupplier.getId()!=null){
	    	psiQuestionSupplier=this.psiQuestionSupplierService.get(psiQuestionSupplier.getId());
	    }
		model.addAttribute("psiQuestionSupplier", psiQuestionSupplier);
		return "modules/psi/psiQuestionSupplierView";
	}
  
	
	@RequestMapping(value = "save")
	public String save(PsiQuestionSupplier psiQuestionSupplier, String isPass,@RequestParam("attchmentFile")MultipartFile[] attchmentFiles, Model model, RedirectAttributes redirectAttributes) {
		if (psiQuestionSupplier.getId()==null && StringUtils.isNotEmpty(isPass) && StringUtils.isNotEmpty(psiQuestionSupplier.getOrderNo())) {
			String result = "1".equals(isPass)?"OK":"NG";
			int count = psiQuestionSupplierService.getCountByOrderNo(psiQuestionSupplier.getOrderNo());
			if (count >= 1) {
				result = result + "("+(count+1)+"次)";
			}
			psiQuestionSupplier.setResult(result);
		}
		psiQuestionSupplierService.save(psiQuestionSupplier,attchmentFiles);
		addMessage(redirectAttributes, "保存贴码错误信息记录'" + psiQuestionSupplier.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiQuestionSupplier/?repage";
	}
	
	
	@RequestMapping(value = "delete")
	public String delete(Integer id, RedirectAttributes redirectAttributes) {
		psiQuestionSupplierService.delete(id);
		addMessage(redirectAttributes, "删除贴码错误信息记录成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiQuestionSupplier/?repage";
	}
	
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "exp")
	public String exp(PsiQuestionSupplier question, Model model, HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Page<PsiQuestionSupplier> page  =new Page<PsiQuestionSupplier>(request, response);
		page.setPageSize(600000);
		List<PsiQuestionSupplier> list = psiQuestionSupplierService.find(page,question).getList(); 
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		String[] title = { "  产品   ","   供应商名称     ","出错时间","  问题类型   "," 采购批次 " ," 事件 "," 后果  ","  处理   ","  赔偿/处罚   ","  创建人 "};
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
		for(PsiQuestionSupplier sup:list){
			int i =0;
			row = sheet.createRow(j++);
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getProductName());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getSupplier().getNikename()); 
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getQuestionDate()!=null?sdf1.format(sup.getQuestionDate()):"");
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getQuestionType());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getOrderNo());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getEvent());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getConsequence());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getDeal());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getPunishment());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sup.getCreateUser().getName());
		}
		
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		String fileName = "supplierQuestion" + sdf.format(new Date()) + ".xls";
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
