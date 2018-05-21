package com.springrain.erp.modules.psi.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PsiSupplierTaxAdjust;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PsiSupplierTaxAdjustService;

@Controller
@RequestMapping(value = "${adminPath}/psi/psiSupplierTaxAdjust")
public class PsiSupplierTaxAdjustController extends BaseController {
	@Autowired
	private      PsiSupplierTaxAdjustService		 psiSupplierTaxAdjustService;
	@Autowired
	private      PsiSupplierService                  psiSupplierService;
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "list", "" })
	public String list(PsiSupplierTaxAdjust psiSupplierTaxAdjust,HttpServletRequest request,HttpServletResponse response, Model model) {
		Page<PsiSupplierTaxAdjust> page=psiSupplierTaxAdjustService.find(new Page<PsiSupplierTaxAdjust>(request, response),psiSupplierTaxAdjust);
		model.addAttribute("page",page);
		model.addAttribute("psiSupplierTaxAdjust",psiSupplierTaxAdjust);
		return "modules/psi/psiSupplierTaxAdjustList";
	}
	
	
	@RequiresPermissions("psi:supplierTaxAdjust:edit")
	@RequestMapping(value = { "add"})
	public String add(PsiSupplierTaxAdjust  psiSupplierTaxAdjust,RedirectAttributes redirectAttributes, Model model) throws IOException {
		List<PsiSupplier> suppliers=psiSupplierService.findAll();
		model.addAttribute("suppliers",suppliers);
		return "modules/psi/psiSupplierTaxAdjustForm";
	}
	
	@RequiresPermissions("psi:supplierTaxAdjust:edit")
	@RequestMapping(value = { "addSave"})
	public String addSave(PsiSupplierTaxAdjust  tax,MultipartFile taxFile,RedirectAttributes redirectAttributes, Model model) throws IOException {
		this.psiSupplierTaxAdjustService.save(tax,taxFile);
		addMessage(redirectAttributes, "供应商税点改动保存成功'" + tax.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiSupplierTaxAdjust/?repage";
	}
	
	
	@RequiresPermissions("psi:supplierTaxAdjust:review")
	@RequestMapping(value = { "review"})
	public String review(PsiSupplierTaxAdjust  psiSupplierTaxAdjust,RedirectAttributes redirectAttributes, Model model) throws IOException {
		List<PsiSupplier> suppliers=psiSupplierService.findAll();
		psiSupplierTaxAdjust=this.psiSupplierTaxAdjustService.get(psiSupplierTaxAdjust.getId());
		
		model.addAttribute("psiSupplierTaxAdjust",psiSupplierTaxAdjust);
		model.addAttribute("suppliers",suppliers);
		return "modules/psi/psiSupplierTaxAdjustReview";
	}
	
	
	@RequiresPermissions("psi:supplierTaxAdjust:review")
	@RequestMapping(value = { "reviewSave"})
	public String reviewSave(PsiSupplierTaxAdjust  tax,RedirectAttributes redirectAttributes, Model model) {
		this.psiSupplierTaxAdjustService.reviewSave(tax);
		addMessage(redirectAttributes, "审核供应商税点改动成功'" + tax.getId() + "'成 功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiSupplierTaxAdjust/?repage";
	}
	
	
	
	
	@RequestMapping(value = { "cancel"})
	public String cancel(PsiSupplierTaxAdjust  tax,RedirectAttributes redirectAttributes, Model model) {
		this.psiSupplierTaxAdjustService.cancelSave(tax);
		addMessage(redirectAttributes, "取消供应商税点改动'" + tax.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiSupplierTaxAdjust/?repage";
	}
	
}
