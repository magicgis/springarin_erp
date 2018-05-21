package com.springrain.erp.modules.psi.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.ProductParts;
import com.springrain.erp.modules.psi.service.PsiProductPartsService;

@Controller
@RequestMapping(value = "${adminPath}/psi/productParts")
public class PsiProductPartsController extends BaseController {

	@Autowired
	private PsiProductPartsService		 productPartsService;

	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "list", "" })
	public String list(ProductParts proParts, HttpServletRequest request,	HttpServletResponse response, Model model) {
	    model.addAttribute("proPartsMap",productPartsService.getProPartsMap(proParts));
	    model.addAttribute("proParts",proParts);
		return "modules/psi/parts/productPartsList";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"updateMixtrueRatio"})
	public String updateMixtrueRatio(Integer proPartsId,Integer ratio) {
		return this.productPartsService.updateMixtrueRatio(proPartsId, ratio);
	}
	
}
