/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.parts;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsDelivery;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrder;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsPayment;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsPaymentItem;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsPaymentService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 配件订单付款Controller
 * @author Michael
 * @version 2015-06-15
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiPartsPayment")
public class PsiPartsPaymentController extends BaseController {
   
	@Autowired
	private PsiPartsPaymentService psiPartsPaymentService;
	@Autowired
	private PsiSupplierService psiSupplierService;
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"list", ""})
	public String list(PsiPartsPayment psiPartsPayment, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);   
		today.setMinutes(0);
		if (psiPartsPayment.getCreateDate() == null) {
			psiPartsPayment.setCreateDate(DateUtils.addMonths(today, -1));
			psiPartsPayment.setUpdateDate(today);
		}
		//配件供应商
        List<PsiSupplier> suppliers=this.psiSupplierService.findSupplierByType(new String[]{"0","2"});
        Page<PsiPartsPayment> page = psiPartsPaymentService.find(new Page<PsiPartsPayment>(request, response), psiPartsPayment); 
        model.addAttribute("page", page);
        model.addAttribute("suppliers", suppliers);
		return "modules/psi/parts/psiPartsPaymentList";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "form")
	public String form(PsiPartsPayment psiPartsPayment, Model model) {
		model.addAttribute("psiPartsPayment", psiPartsPayment);
		return "modules/psi/parts/psiPartsPaymentForm";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "add")
	public String add(PsiPartsPayment psiPartsPayment, Model model) {
		PsiSupplier supplier = new PsiSupplier();
		Map<String, String> accountMaps= null;
		//配件供应商
        List<PsiSupplier> suppliers=this.psiSupplierService.findSupplierByType(new String[]{"0","2"});
      //根据配件供应商id,选择配件信息
      	Integer supplierId =0;
        if(psiPartsPayment.getSupplier()!=null&&psiPartsPayment.getSupplier().getId()!=null){
			supplierId=psiPartsPayment.getSupplier().getId();
			//获取选择供应商的货币类型
			for(PsiSupplier sup:suppliers){
				if(sup.getId().equals(psiPartsPayment.getSupplier().getId())){
					supplier=sup;
					psiPartsPayment.setCurrencyType(sup.getCurrencyType());
					break;
				}
			}
		}else{
			supplier=suppliers.get(0);
			supplierId=supplier.getId();
			psiPartsPayment.setCurrencyType(supplier.getCurrencyType());
		}
        
        accountMaps= supplier.getAccountMap();
        //查询出未付款完成的配件订单
        Map<String,PsiPartsOrder> partsOrderMap=this.psiPartsPaymentService.getUnPaymentDoneOrder(supplierId,null);
        Map<String,PsiPartsDelivery> ladingMap=this.psiPartsPaymentService.getUnPaymentDoneLading(supplierId,null);
        
        model.addAttribute("accountMaps", accountMaps);
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("orderKeys", JSON.toJSON(partsOrderMap.keySet()));
		model.addAttribute("ladingKeys", JSON.toJSON(ladingMap.keySet()));
	    model.addAttribute("partsOrderMap", JSON.toJSON(partsOrderMap));
	    model.addAttribute("ladingMap", JSON.toJSON(ladingMap));
	    model.addAttribute("psiPartsPayment", psiPartsPayment);
		return "modules/psi/parts/psiPartsPaymentAdd";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sure")
	public String sure(PsiPartsPayment psiPartsPayment, Model model) {
		if(psiPartsPayment.getId()==null){
			return null;
		}
		psiPartsPayment=this.psiPartsPaymentService.get(psiPartsPayment.getId());
		//账号信息
		Map<String, String> accountMaps= psiPartsPayment.getSupplier().getAccountMap();
		model.addAttribute("accountMaps", JSON.toJSON(accountMaps));
		model.addAttribute("psiPartsPayment", psiPartsPayment);
		return "modules/psi/parts/psiPartsPaymentSure";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "view")
	public String view(PsiPartsPayment psiPartsPayment, Model model) {
		if(psiPartsPayment.getId()==null){
			return null;
		}
		psiPartsPayment=this.psiPartsPaymentService.get(psiPartsPayment.getId());
		model.addAttribute("psiPartsPayment", psiPartsPayment);
		return "modules/psi/parts/psiPartsPaymentView";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "addSave")
	public String addSave(PsiPartsPayment psiPartsPayment, Model model, RedirectAttributes redirectAttributes) throws Exception {
		if(psiPartsPayment.getItems()!=null){
			psiPartsPaymentService.addSave(psiPartsPayment);     
			addMessage(redirectAttributes, "保存配件订单付款'" + psiPartsPayment.getPaymentNo() + "'成功");
			return "redirect:"+Global.getAdminPath()+"/psi/psiPartsPayment/?repage";    
		}
		return null;
	
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "editSave")
	public String editSave(PsiPartsPayment psiPartsPayment, Model model, RedirectAttributes redirectAttributes) {
		if(psiPartsPayment.getItems()!=null){
			if(this.psiPartsPaymentService.editSave(psiPartsPayment)){
				addMessage(redirectAttributes, "保存配件订单付款'" + psiPartsPayment.getPaymentNo() + "'成功");
				return "redirect:"+Global.getAdminPath()+"/psi/psiPartsPayment/?repage";
			}
		}
		return null;
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "edit")
	public String edit(PsiPartsPayment psiPartsPayment, Model model) {
		Map<String, String> accountMaps= null;
		psiPartsPayment =this.psiPartsPaymentService.get(psiPartsPayment.getId());
		StringBuilder sb = new StringBuilder("");
		for(PsiPartsPaymentItem item:psiPartsPayment.getItems()){
			sb.append(item.getId()+",");
		}
		String itemIds="";
		if(StringUtils.isNotEmpty(sb.toString())){
			itemIds=sb.toString().substring(0,sb.toString().length()-1);
		}
		psiPartsPayment.setOldItemIds(itemIds);
		
		
		PsiSupplier  supplier= psiPartsPayment.getSupplier();
		//查出未付款的订单信息
		 Map<String,PsiPartsOrder> partsOrderMap=this.psiPartsPaymentService.getUnPaymentDoneOrder(supplier.getId(),supplier.getCurrencyType());
	     Map<String,PsiPartsDelivery> ladingMap=this.psiPartsPaymentService.getUnPaymentDoneLading(supplier.getId(),supplier.getCurrencyType());
	     
	     accountMaps= supplier.getAccountMap();
		model.addAttribute("orderSet", partsOrderMap.keySet());
		model.addAttribute("ladingSet",ladingMap.keySet());
		model.addAttribute("accountMaps", accountMaps);
		
		model.addAttribute("orderKeys", JSON.toJSON(partsOrderMap.keySet()));
		model.addAttribute("ladingKeys", JSON.toJSON(ladingMap.keySet()));
		model.addAttribute("orderMaps", JSON.toJSON(partsOrderMap));
		model.addAttribute("ladingMaps", JSON.toJSON(ladingMap));
		
		model.addAttribute("psiPartsPayment", psiPartsPayment);
		return "modules/psi/parts/psiPartsPaymentEdit";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "cancel")
	public String cancel(PsiPartsPayment psiPartsPayment, Model model, RedirectAttributes redirectAttributes) {
		if(psiPartsPayment.getId()==null){
			return null;
		}
		String flag= "";
		if(psiPartsPayment.getPaymentSta()!=null){
			flag=psiPartsPayment.getPaymentSta();
			psiPartsPayment.setPaymentSta(null);
		}
		
		if(!"".equals(flag)){
			psiPartsPayment=this.psiPartsPaymentService.get(psiPartsPayment.getId());
			//如果是申请状态下取消     追回申请款项；
			if(flag.equals("4")){
				psiPartsPayment.setPaymentSta("0");   //草稿状态
				this.psiPartsPaymentService.cancel(psiPartsPayment);
			}else if(flag.equals("5")){
				psiPartsPayment.setPaymentSta("3");   //已取消
				this.psiPartsPaymentService.cancel(psiPartsPayment);
			}else if(flag.equals("6")){
				psiPartsPayment.setPaymentSta("3");   //已取消
				psiPartsPayment.setCancelDate(new Date());
				psiPartsPayment.setCancelUser(UserUtils.getUser());
				this.psiPartsPaymentService.save(psiPartsPayment);
			}
		}
		
		addMessage(redirectAttributes, "取消配件订单付款'" + psiPartsPayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiPartsPayment/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sureSave")
	public String sureSave(MultipartFile memoFile,PsiPartsPayment psiPartsPayment, Model model, RedirectAttributes redirectAttributes) {
		if(psiPartsPayment.getId()==null){
			return null;
		}
		psiPartsPayment=this.psiPartsPaymentService.get(psiPartsPayment.getId());
		psiPartsPaymentService.sureSave(memoFile,psiPartsPayment);
		addMessage(redirectAttributes, "确认配件订单付款'" + psiPartsPayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiPartsPayment/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "save")
	public String save(PsiPartsPayment psiPartsPayment, Model model, RedirectAttributes redirectAttributes) {
		psiPartsPaymentService.save(psiPartsPayment);
		addMessage(redirectAttributes, "保存配件订单付款'" + psiPartsPayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiPartsPayment/?repage";
	}
	
	
}
