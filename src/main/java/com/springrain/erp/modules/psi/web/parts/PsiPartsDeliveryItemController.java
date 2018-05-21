/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.parts;

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
import com.springrain.erp.modules.psi.entity.parts.PsiPartsDeliveryItem;
import com.springrain.erp.modules.psi.service.parts.PsiPartsDeliveryItemService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 配件收货详情Controller
 * @author Michael
 * @version 2015-07-03
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiPartsDeliveryItem")
public class PsiPartsDeliveryItemController extends BaseController {

	@Autowired
	private PsiPartsDeliveryItemService psiPartsDeliveryItemService;
	
	@RequiresPermissions("psi:psiPartsDeliveryItem:view")
	@RequestMapping(value = {"list", ""})
	public String list(PsiPartsDeliveryItem psiPartsDeliveryItem, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<PsiPartsDeliveryItem> page = psiPartsDeliveryItemService.find(new Page<PsiPartsDeliveryItem>(request, response), psiPartsDeliveryItem); 
        model.addAttribute("page", page);
		return "modules/psi/parts/psiPartsDeliveryItemList";
	}
  
	@RequiresPermissions("psi:psiPartsDeliveryItem:view")
	@RequestMapping(value = "form")
	public String form(PsiPartsDeliveryItem psiPartsDeliveryItem, Model model) {
		model.addAttribute("psiPartsDeliveryItem", psiPartsDeliveryItem);
		return "modules/psi/parts/psiPartsDeliveryItemForm";
	}

	@RequiresPermissions("psi:psiPartsDeliveryItem:edit")
	@RequestMapping(value = "save")
	public String save(PsiPartsDeliveryItem psiPartsDeliveryItem, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, psiPartsDeliveryItem)){
			return form(psiPartsDeliveryItem, model);
		}
		psiPartsDeliveryItemService.save(psiPartsDeliveryItem);
		return "redirect:"+Global.getAdminPath()+"/modules/psi/parts/psiPartsDeliveryItem/?repage";
	}
	
	@RequiresPermissions("psi:psiPartsDeliveryItem:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		psiPartsDeliveryItemService.delete(id);
		addMessage(redirectAttributes, "删除配件收货详情成功");
		return "redirect:"+Global.getAdminPath()+"/modules/psi/parts/psiPartsDeliveryItem/?repage";
	}

}
