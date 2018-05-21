/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductImprove;
import com.springrain.erp.modules.psi.service.PsiProductImproveService;
import com.springrain.erp.modules.psi.service.PsiProductService;

/**
 * 产品改进信息记录Controller
 * @author Michael
 * @version 2015-06-01
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/productImprove")
public class PsiProductImproveController extends BaseController {
	
	@Autowired
	private PsiProductService          psiProductService;
	@Autowired
	private PsiProductImproveService          productImproveService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiProductImprove productImprove, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PsiProductImprove> page=new Page<PsiProductImprove>(request, response);
        productImproveService.find(page, productImprove); 
        model.addAttribute("productImprove", productImprove);
        model.addAttribute("page", page);
		return "modules/psi/psiProductImproveList";
	}
	
	@RequestMapping(value = "form")
	public String form(PsiProductImprove productImprove, String isPass, Model model) {
		if(productImprove.getImproveDate()!=null){
			productImprove.setImproveDate(new Date());
		}
		List<PsiProduct>  products =this.psiProductService.findAll();
		model.addAttribute("products", products);
		return "modules/psi/psiProductImproveForm";
	}
	
	
	@RequestMapping(value = "view")
	public String view(PsiProductImprove productImprove, Model model) {
	    if(productImprove.getId()!=null){
	    	productImprove=this.productImproveService.get(productImprove.getId());
	    }
		model.addAttribute("productImprove", productImprove);
		return "modules/psi/psiProductImproveView";
	}
  
	
	@RequestMapping(value = "getTips")
	@ResponseBody
	public String getTips(String name) {
		return productImproveService.getTips(name);
	}
	
	@RequestMapping(value = "save")
	public String save(PsiProductImprove productImprove,Model model, RedirectAttributes redirectAttributes) {
		productImproveService.save(productImprove);
		addMessage(redirectAttributes, "保存产品改进信息记录'" + productImprove.getProductName()+"颜色"+productImprove.getColor() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/productImprove/?repage";
	}
	
	
	@RequestMapping(value = "delete")
	public String delete(Integer id, RedirectAttributes redirectAttributes) {
		productImproveService.delete(id);
		addMessage(redirectAttributes, "删除产品改进信息记录成功");
		return "redirect:"+Global.getAdminPath()+"/psi/productImprove/?repage";
	}
	
}
