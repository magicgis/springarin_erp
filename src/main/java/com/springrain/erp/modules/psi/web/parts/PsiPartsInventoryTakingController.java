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
import com.springrain.erp.modules.psi.entity.parts.PsiParts;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventory;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventoryTaking;
import com.springrain.erp.modules.psi.service.parts.PsiPartsInventoryService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsInventoryTakingService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsService;

/**
 * 配件库存盘点Controller
 * @author Michael
 * @version 2015-07-31
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiPartsInventoryTaking")
public class PsiPartsInventoryTakingController extends BaseController {
	@Autowired
	private PsiPartsInventoryTakingService psiPartsInventoryTakingService;
	@Autowired
	private PsiPartsInventoryService psiPartsInventoryService;
	@Autowired
	private PsiPartsService psiPartsService;
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"list", ""})
	public String list(PsiPartsInventoryTaking psiPartsInventoryTaking, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<PsiPartsInventoryTaking> page = psiPartsInventoryTakingService.find(new Page<PsiPartsInventoryTaking>(request, response), psiPartsInventoryTaking); 
        model.addAttribute("page", page);
		return "modules/psi/parts/psiPartsInventoryTakingList";
	}

	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "form")
	public String form(PsiPartsInventoryTaking psiPartsInventoryTaking, Model model) {
		Set<Integer> setTemp =null;
		Map<Integer,PsiPartsInventory>  partsInventorys = this.psiPartsInventoryService.getPsiPartsInventorys(setTemp);
		Map<Integer,PsiParts> partsInfos=psiPartsService.getPartsByIdsJson();
		//所有配件信息
		model.addAttribute("partsInfos", JSON.toJSON(partsInfos));
		//所有配件库存信息
		model.addAttribute("partsInventorys", JSON.toJSON(partsInventorys));
		model.addAttribute("psiPartsInventoryTaking", psiPartsInventoryTaking);
		return "modules/psi/parts/psiPartsInventoryTakingForm";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "takingSave")
	public String takingSave(PsiPartsInventoryTaking psiPartsInventoryTaking, Model model, RedirectAttributes redirectAttributes) {
		psiPartsInventoryTakingService.takingSave(psiPartsInventoryTaking);
		String typeStr="出库";
		if("0".equals(psiPartsInventoryTaking.getTakingType())){
			typeStr="入库";
		}
		addMessage(redirectAttributes, "保存配件库存盘点'" +typeStr+ "" + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiPartsInventory/?repage";
	}
	

}
