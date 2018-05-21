package com.springrain.erp.modules.psi.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.ProductSupplier;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductMoldFee;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.service.PsiProductMoldFeeService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 产品改进信息记录Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/productMoldFee")
public class PsiProductMoldFeeController extends BaseController {
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private PsiProductMoldFeeService psiProductMoldFeeService;
	
	@Autowired
	private PsiSupplierService 		 	psiSupplierService;
	
	@ModelAttribute
	public PsiProductMoldFee get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return psiProductMoldFeeService.get(id);
		}else{
			return new PsiProductMoldFee();
		}
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<PsiProductMoldFee> list = psiProductMoldFeeService.find();
        model.addAttribute("list", list);
		return "modules/psi/psiProductMoldFeeList";
	}
	
	@RequestMapping(value = "form")
	public String form(PsiProductMoldFee psiProductMoldFee, Model model) {
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		model.addAttribute("suppliers", suppliers);
		List<PsiProduct> products = Lists.newArrayList();
		List<String> colorNameList = Lists.newArrayList();
		PsiSupplier supplier = null;
		if(psiProductMoldFee.getSupplier()==null){
			supplier = suppliers.get(0);
		}else{
			supplier = this.psiSupplierService.get(psiProductMoldFee.getSupplier().getId());
		}
		if (supplier != null && supplier.getProducts() != null) {
			for(ProductSupplier proSup : supplier.getProducts()){
				PsiProduct product = proSup.getProduct();
				products.add(product);
			}
			for (PsiProduct psiProduct : products) {
				for (String colorName : psiProduct.getProductNameWithColor()) {
					if (StringUtils.isEmpty(psiProductMoldFee.getProductName()) || !psiProductMoldFee.getProductName().contains(colorName)) {
						colorNameList.add(colorName);
					}
				}
			}
		}
		model.addAttribute("colorNameList", colorNameList);
		model.addAttribute("psiProductMoldFee", psiProductMoldFee);
		return "modules/psi/psiProductMoldFeeForm";
	}
	
	@RequestMapping(value = "save")
	public String save(PsiProductMoldFee psiProductMoldFee, RedirectAttributes redirectAttributes) {
		psiProductMoldFee.setCreateBy(UserUtils.getUser());
		psiProductMoldFee.setCreateDate(new Date());
		psiProductMoldFeeService.save(psiProductMoldFee);
		addMessage(redirectAttributes, "操作成功!");
		return "redirect:"+Global.getAdminPath()+"/psi/productMoldFee/?repage";
	}

}
