/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.FbaInbound;
import com.springrain.erp.modules.psi.entity.PsiQualityChangeBill;
import com.springrain.erp.modules.psi.entity.PsiSkuChangeBill;
import com.springrain.erp.modules.psi.entity.PsiSkuChangeBillItem;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.service.PsiQualityChangeBillService;
import com.springrain.erp.modules.psi.service.PsiSkuChangeBillService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

import freemarker.template.utility.DateUtil;

/**
 * new to offline Controller
 * @author Michael
 * @version 2015-05-25
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiQualityChangeBill")
public class PsiQualityChangeBillController extends BaseController {

	@Autowired
	private PsiQualityChangeBillService psiQualityChangeBillService;
	@Autowired
	private SystemService   userService;  
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	@RequestMapping(value = {"list", ""})
	public String list(PsiQualityChangeBill psiQualityChangeBill, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if(psiQualityChangeBill.getApplyDate()==null){
			Date date =sdf.parse(sdf.format(new Date()));
			psiQualityChangeBill.setApplyDate(DateUtils.addMonths(date, -1));
			psiQualityChangeBill.setSureDate(date);
		}
		if(psiQualityChangeBill.getWarehouseId()==null){
			psiQualityChangeBill.setWarehouseId(19);
		}
		List<User> allUser = userService.findAllUsers();
        Page<PsiQualityChangeBill> page = psiQualityChangeBillService.find(new Page<PsiQualityChangeBill>(request, response), psiQualityChangeBill);
        
        
        model.addAttribute("allUser", allUser);
        model.addAttribute("page", page);
		return "modules/psi/psiQualityChangeBillList";
	}

	
//	@RequestMapping(value = "sure")
//	public String sure(PsiQualityChangeBill psiQualityChangeBill, Model model, RedirectAttributes redirectAttributes) {
//		if(psiQualityChangeBill.getId()!=null){
//			this.psiQualityChangeBillService.sure(psiQualityChangeBill);
//			addMessage(redirectAttributes, "confirm '" + psiQualityChangeBill.getId()+ "'success");
//		}
//		
//		return "redirect:"+Global.getAdminPath()+"/psi/psiQualityChangeBill/?repage";
//	}
	
	@RequestMapping(value = "cancel")
	public String cancel(PsiQualityChangeBill psiQualityChangeBill, Model model, RedirectAttributes redirectAttributes) {
		if(psiQualityChangeBill.getId()!=null){
			String remark=psiQualityChangeBill.getRemark();
			this.psiQualityChangeBillService.cancel(psiQualityChangeBill.getId(),remark);
			addMessage(redirectAttributes, "confirm '" + psiQualityChangeBill.getId()+ "'success");
		}
		
		return "redirect:"+Global.getAdminPath()+"/psi/psiQualityChangeBill/?repage";
	}
	
	@RequiresPermissions("psi:skuChangeBill:view")
	@RequestMapping(value = "sureSave")
	public String sureSave(PsiQualityChangeBill qualityBill, Model model, RedirectAttributes redirectAttributes) {
		if(qualityBill.getId()!=null){
			if(this.psiQualityChangeBillService.sureSave(qualityBill)){
				addMessage(redirectAttributes, "New_To_Offline adjust confirm '" + qualityBill.getId()+ "'success");
			}else{
				addMessage(redirectAttributes, "New_To_Offline adjust confirm '" + qualityBill.getId()+ "'fail");
			}
		}
		
		return "redirect:"+Global.getAdminPath()+"/psi/psiQualityChangeBill/?repage";
	}
	
}
