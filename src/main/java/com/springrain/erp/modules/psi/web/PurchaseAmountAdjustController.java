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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PurchaseAmountAdjust;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PurchaseAmountAdjustService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 采购金额调整Controller
 * @author Michael
 * @version 2015-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/purchaseAmountAdjust")
public class PurchaseAmountAdjustController extends BaseController {
	@Autowired
	private PurchaseAmountAdjustService purchaseAmountAdjustService;
	@Autowired
	private PsiSupplierService psiSupplierService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PurchaseAmountAdjust purchaseAmountAdjust, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
		if(purchaseAmountAdjust.getCreateDate()==null){
			purchaseAmountAdjust.setCreateDate(sdf.parse((sdf.format(DateUtils.addMonths(new Date(), -1)))));
		}
		if(purchaseAmountAdjust.getUpdateDate()==null){
			purchaseAmountAdjust.setUpdateDate(sdf.parse((sdf.format(new Date()))));
		}
		
		 //配件供应商
        List<PsiSupplier> suppliers=this.psiSupplierService.findSupplierByType(new String[]{"0","2"});
        Page<PurchaseAmountAdjust> page = purchaseAmountAdjustService.find(new Page<PurchaseAmountAdjust>(request, response), purchaseAmountAdjust); 
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("page", page);
		return "modules/psi/purchaseAmountAdjustList";
	}

	@RequestMapping(value = "form")
	public String form(PurchaseAmountAdjust purchaseAmountAdjust, Model model) {
		if(purchaseAmountAdjust.getId()!=null){
			purchaseAmountAdjust=this.purchaseAmountAdjustService.get(purchaseAmountAdjust.getId());
		}
		Map<Integer,String> curMap = Maps.newHashMap();//作为获得结果集
		Map<Integer,String> supplierMap = this.psiSupplierService.getIdNameCurrency(curMap);
		model.addAttribute("supplierMap", supplierMap);
		model.addAttribute("curMap", JSON.toJSON(curMap));
		
		model.addAttribute("purchaseAmountAdjust", purchaseAmountAdjust);
		return "modules/psi/purchaseAmountAdjustForm";
	}
  
	@RequestMapping(value = "save")
	public String save(PurchaseAmountAdjust purchaseAmountAdjust, Model model, RedirectAttributes redirectAttributes) {
		if(purchaseAmountAdjust.getId()==null){
			purchaseAmountAdjust.setCreateDate(new Date());
			purchaseAmountAdjust.setCreateUser(UserUtils.getUser());
		}
		//保存供应商id
		purchaseAmountAdjustService.save(purchaseAmountAdjust);
		addMessage(redirectAttributes, "保存采购金额调整项'" + purchaseAmountAdjust.getSubject()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/purchaseAmountAdjust/?repage";
	}
	
	@RequestMapping(value = "cancel")
	public String cancel(PurchaseAmountAdjust purchaseAmountAdjust, Model model, RedirectAttributes redirectAttributes) {
		if(purchaseAmountAdjust.getId()==null){
			return "";
		}
		purchaseAmountAdjust = this.purchaseAmountAdjustService.get(purchaseAmountAdjust.getId());
		purchaseAmountAdjust.setAdjustSta("8");
		purchaseAmountAdjust.setCancelDate(new Date());
		purchaseAmountAdjust.setCancelUser(UserUtils.getUser());
		purchaseAmountAdjustService.save(purchaseAmountAdjust);
		addMessage(redirectAttributes, "取消采购金额调整项'" + purchaseAmountAdjust.getSubject() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/purchaseAmountAdjust/?repage";
	}
	
}
