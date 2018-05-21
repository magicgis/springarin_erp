/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.parts;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventory;
import com.springrain.erp.modules.psi.service.parts.PsiPartsInventoryService;

/**
 * 配件库存管理Controller
 * @author Michael
 * @version 2015-06-29
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiPartsInventory")
public class PsiPartsInventoryController extends BaseController {
	@Autowired
	private PsiPartsInventoryService psiPartsInventoryService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiPartsInventory psiPartsInventory, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<PsiPartsInventory> page= psiPartsInventoryService.find(new Page<PsiPartsInventory>(request, response,25), psiPartsInventory); 
        model.addAttribute("page", page);
		return "modules/psi/parts/psiPartsInventoryList";
	}  
  
	@RequestMapping(value = "adjust")
	public String adjust(PsiPartsInventory psiPartsInventory, Model model) {
		Set<Integer> setTemp =null;
		Map<Integer,PsiPartsInventory>  partsInventory = this.psiPartsInventoryService.getPsiPartsInventorys(setTemp);
		model.addAttribute("partsInventory", JSON.toJSON(partsInventory));
		return "modules/psi/parts/psiPartsInventoryAdjust";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "adjustSave")
	public String adjustSave(PsiPartsInventory psiPartsInventory, Model model, RedirectAttributes redirectAttributes) {
		this.psiPartsInventoryService.adjustSave(psiPartsInventory);
		addMessage(redirectAttributes, "保存配件库存调整成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiPartsInventory/?repage";
	}

}
