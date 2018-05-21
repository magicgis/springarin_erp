/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.lc;

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
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsInventory;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsInventoryTaking;
import com.springrain.erp.modules.psi.entity.parts.PsiParts;
import com.springrain.erp.modules.psi.service.lc.LcPsiPartsInventoryService;
import com.springrain.erp.modules.psi.service.lc.LcPsiPartsInventoryTakingService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsService;

/**
 * 配件库存盘点Controller
 * @author Michael
 * @version 2015-07-31
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/lcPsiPartsInventoryTaking")
public class LcPsiPartsInventoryTakingController extends BaseController {
	@Autowired
	private LcPsiPartsInventoryTakingService psiPartsInventoryTakingService;
	@Autowired
	private LcPsiPartsInventoryService psiPartsInventoryService;
	@Autowired
	private PsiPartsService psiPartsService;
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"list", ""})
	public String list(LcPsiPartsInventoryTaking psiPartsInventoryTaking, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<LcPsiPartsInventoryTaking> page = psiPartsInventoryTakingService.find(new Page<LcPsiPartsInventoryTaking>(request, response), psiPartsInventoryTaking); 
        model.addAttribute("page", page);
		return "modules/psi/lc/parts/lcPsiPartsInventoryTakingList";
	}

	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "form")
	public String form(LcPsiPartsInventoryTaking psiPartsInventoryTaking, Model model) {
		Set<Integer> setTemp =null;
		Map<Integer,LcPsiPartsInventory>  partsInventorys = this.psiPartsInventoryService.getPsiPartsInventorys(setTemp);
		Map<Integer,PsiParts> partsInfos=psiPartsService.getPartsByIdsJson();
		//所有配件信息
		model.addAttribute("partsInfos", JSON.toJSON(partsInfos));
		//所有配件库存信息
		model.addAttribute("partsInventorys", JSON.toJSON(partsInventorys));
		model.addAttribute("psiPartsInventoryTaking", psiPartsInventoryTaking);
		return "modules/psi/lc/parts/lcPsiPartsInventoryTakingForm";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "takingSave")
	public String takingSave(LcPsiPartsInventoryTaking psiPartsInventoryTaking, Model model, RedirectAttributes redirectAttributes) {
		psiPartsInventoryTakingService.takingSave(psiPartsInventoryTaking);
		String typeStr="出库";
		if("0".equals(psiPartsInventoryTaking.getTakingType())){
			typeStr="入库";
		}
		addMessage(redirectAttributes, "保存配件库存盘点'" +typeStr+ "" + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsInventory/?repage";
	}
	

}
