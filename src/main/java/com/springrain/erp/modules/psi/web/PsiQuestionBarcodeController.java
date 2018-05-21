/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiQuestionBarcode;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiQuestionBarcodeService;

/**
 * 贴码错误信息记录Controller
 * @author Michael
 * @version 2015-06-01
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiQuestionBarcode")
public class PsiQuestionBarcodeController extends BaseController {

	@Autowired
	private PsiQuestionBarcodeService psiQuestionBarcodeService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiQuestionBarcode psiQuestionBarcode, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<PsiQuestionBarcode> page = psiQuestionBarcodeService.find(new Page<PsiQuestionBarcode>(request, response), psiQuestionBarcode); 
        model.addAttribute("page", page);
		return "modules/psi/psiQuestionBarcodeList";
	}
	
	@RequestMapping(value = "form")
	public String form(PsiQuestionBarcode psiQuestionBarcode, Model model) {
		if(psiQuestionBarcode.getId()!=null){
			psiQuestionBarcode=this.psiQuestionBarcodeService.get(psiQuestionBarcode.getId());
		}
		if(psiQuestionBarcode.getQuestionDate()!=null){
			psiQuestionBarcode.setQuestionDate(new Date());
		}
		//获取产品id和产品名颜色
		Map<String,String> proMap = this.psiQuestionBarcodeService.getMapByProAttr();
		model.addAttribute("proMap", proMap);
		model.addAttribute("psiQuestionBarcode", psiQuestionBarcode);
		return "modules/psi/psiQuestionBarcodeForm";
	}
	
	
	@RequestMapping(value = "view")
	public String view(PsiQuestionBarcode psiQuestionBarcode, Model model) {
	    if(psiQuestionBarcode.getId()!=null){
	    	psiQuestionBarcode=this.psiQuestionBarcodeService.get(psiQuestionBarcode.getId());
	    }
		model.addAttribute("psiQuestionBarcode", psiQuestionBarcode);
		return "modules/psi/psiQuestionBarcodeView";
	}
  
	
	@RequestMapping(value = "save")
	public String save(PsiQuestionBarcode psiQuestionBarcode, Model model, RedirectAttributes redirectAttributes) {
		psiQuestionBarcodeService.save(psiQuestionBarcode);
		addMessage(redirectAttributes, "保存贴码错误信息记录'" + psiQuestionBarcode.getProductName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiQuestionBarcode/?repage";
	}
	
	
	@RequestMapping(value = "delete")
	public String delete(Integer id, RedirectAttributes redirectAttributes) {
		psiQuestionBarcodeService.delete(id);
		addMessage(redirectAttributes, "删除贴码错误信息记录成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiQuestionBarcode/?repage";
	}
	
	

}
