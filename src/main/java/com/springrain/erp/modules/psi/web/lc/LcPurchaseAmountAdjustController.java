/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.lc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseAmountAdjust;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrderItem;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseAmountAdjustService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 采购金额调整Controller
 * @author Michael
 * @version 2015-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/lcPurchaseAmountAdjust")
public class LcPurchaseAmountAdjustController extends BaseController {
	
	
	@Autowired
	private LcPurchaseAmountAdjustService lcPurchaseAmountAdjustService;
	@Autowired
	private LcPurchaseOrderService purchaseOrderService;
	@Autowired
	private PsiSupplierService psiSupplierService;
	@Autowired
	private SystemService  systemService;
	
	
	@RequestMapping(value = {"list", ""})
	public String list(LcPurchaseAmountAdjust lcPurchaseAmountAdjust, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
		if(lcPurchaseAmountAdjust.getCreateDate()==null){
			lcPurchaseAmountAdjust.setCreateDate(sdf.parse((sdf.format(DateUtils.addMonths(new Date(), -1)))));
		}
		if(lcPurchaseAmountAdjust.getUpdateDate()==null){
			lcPurchaseAmountAdjust.setUpdateDate(sdf.parse((sdf.format(new Date()))));
		}
		
		 //配件供应商    
        List<PsiSupplier> suppliers=this.psiSupplierService.findSupplierByType(new String[]{"0","2"});
        Page<LcPurchaseAmountAdjust> page = lcPurchaseAmountAdjustService.find(new Page<LcPurchaseAmountAdjust>(request, response), lcPurchaseAmountAdjust); 
        model.addAttribute("suppliers", suppliers);    
        model.addAttribute("page", page);
		return "modules/psi/lc/lcPurchaseAmountAdjustList";
	}

	@RequestMapping(value = "form")
	public String form(LcPurchaseAmountAdjust lcPurchaseAmountAdjust, Model model) {
		if(lcPurchaseAmountAdjust.getId()!=null){
			lcPurchaseAmountAdjust=this.lcPurchaseAmountAdjustService.get(lcPurchaseAmountAdjust.getId());
		}
		Integer supplierId=0;
		Map<Integer,String> curMap = Maps.newHashMap();//作为获得结果集
		Map<Integer,String> supplierMap = this.psiSupplierService.getIdNameCurrency(curMap);
		if(lcPurchaseAmountAdjust.getSupplier()!=null&&lcPurchaseAmountAdjust.getSupplier().getId()!=null){
			supplierId=lcPurchaseAmountAdjust.getSupplier().getId();
		}else{
			for(Integer supplierIdTemp:supplierMap.keySet()){
				supplierId=supplierIdTemp;
				break;
			}
		}
		
		if(StringUtils.isEmpty(lcPurchaseAmountAdjust.getCurrency())){
			lcPurchaseAmountAdjust.setCurrency(curMap.get(supplierId));
		}
		
		//获取最近4个月的订单  及产品
		List<LcPurchaseOrder>   list = this.purchaseOrderService.findPurchaseOrders(supplierId, DateUtils.addMonths(new Date(), -4));
		Map<Integer,String>  orderMap = Maps.newTreeMap();
		Map<Integer,Set<String>>  nameMap = Maps.newHashMap();
		
		for(LcPurchaseOrder order:list){
			Integer orderId = order.getId();
			orderMap.put(orderId, order.getOrderNo());
			Set<String>  productNames = Sets.newHashSet();
			for(LcPurchaseOrderItem item:order.getItems()){
				productNames.add(item.getProductNameColor());
			}
			nameMap.put(orderId, productNames);
		}
		model.addAttribute("orderMap", orderMap);
		model.addAttribute("nameMap", JSON.toJSON(nameMap));
		model.addAttribute("supplierMap", supplierMap);
		model.addAttribute("lcPurchaseAmountAdjust", lcPurchaseAmountAdjust);
		return "modules/psi/lc/lcPurchaseAmountAdjustForm";
	}
  
	@RequiresPermissions("psi:purchaseAdjust:review")
	@RequestMapping(value = "review")
	public String review(LcPurchaseAmountAdjust lcPurchaseAmountAdjust, Model model) {
		if(lcPurchaseAmountAdjust.getId()!=null){
			lcPurchaseAmountAdjust=this.lcPurchaseAmountAdjustService.get(lcPurchaseAmountAdjust.getId());
		}
		model.addAttribute("lcPurchaseAmountAdjust", lcPurchaseAmountAdjust);
		return "modules/psi/lc/lcPurchaseAmountAdjustReview";
	}
	
	@RequestMapping(value = "save")
	public String save(LcPurchaseAmountAdjust lcPurchaseAmountAdjust,@RequestParam("attchmentFile")MultipartFile[] attchmentFiles, Model model, RedirectAttributes redirectAttributes) {
		//保存供应商id
		lcPurchaseAmountAdjustService.save(lcPurchaseAmountAdjust,attchmentFiles);
		addMessage(redirectAttributes, "保存采购金额调整项'" + lcPurchaseAmountAdjust.getSubject()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPurchaseAmountAdjust/?repage";
	}
	
	@RequestMapping(value = "apply")
	public String apply(LcPurchaseAmountAdjust lcPurchaseAmountAdjust, Model model, RedirectAttributes redirectAttributes) {
		lcPurchaseAmountAdjust = this.lcPurchaseAmountAdjustService.get(lcPurchaseAmountAdjust.getId());
		List<User> users=systemService.findUserByPermission("psi:purchaseAdjust:review");
		String email="";
		for(User user:users){
			email+=user.getEmail()+",";
		}
		if(StringUtils.isNotEmpty(email)){
			lcPurchaseAmountAdjust.setAdjustSta("a");
			this.lcPurchaseAmountAdjustService.save(lcPurchaseAmountAdjust);
			email = email.substring(0, email.length()-1);
			String subject="采购金额调整已申请，请尽快审核！";
			String content = "Hi,<br/>采购金额调整单：<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPurchaseAmountAdjust/review?id="+lcPurchaseAmountAdjust.getId()+"'>"+lcPurchaseAmountAdjust.getOrderNo()+"</a>已申请审核调整价格，请及时审核!";
			this.lcPurchaseAmountAdjustService.sendNoticeEmail(email, content, subject, UserUtils.getUser().getEmail(), null);
			addMessage(redirectAttributes, "申请采购金额调整项'" + lcPurchaseAmountAdjust.getSubject()+ "'成功");
		}else{
			addMessage(redirectAttributes, "error:申请采购金额调整项'" + lcPurchaseAmountAdjust.getSubject()+ "'失败，审核人邮件为空");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/lcPurchaseAmountAdjust/?repage";
	}
		
	@RequiresPermissions("psi:purchaseAdjust:review")
	@RequestMapping(value = "reviewSave")
	public String reviewSave(LcPurchaseAmountAdjust lcPurchaseAmountAdjust, Model model, RedirectAttributes redirectAttributes) {
		//保存供应商id
		lcPurchaseAmountAdjust = this.lcPurchaseAmountAdjustService.get(lcPurchaseAmountAdjust.getId());
		String email = lcPurchaseAmountAdjust.getCreateUser().getEmail();
		lcPurchaseAmountAdjust.setAdjustSta("r");
		lcPurchaseAmountAdjustService.save(lcPurchaseAmountAdjust);
		String subject="采购金额调整已审核通过，请知悉！";
		String content = "Hi,<br/>采购金额调整单<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPurchaseAmountAdjust/view?id="+lcPurchaseAmountAdjust.getId()+"'>"+lcPurchaseAmountAdjust.getOrderNo()+"</a>,已审核通过，请知悉！";
		this.lcPurchaseAmountAdjustService.sendNoticeEmail(email, content, subject, UserUtils.getUser().getEmail(), null);
		addMessage(redirectAttributes, "审核采购金额调整项'" + lcPurchaseAmountAdjust.getSubject()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPurchaseAmountAdjust/?repage";
	}
	@RequestMapping(value = "cancel")
	public String cancel(LcPurchaseAmountAdjust lcPurchaseAmountAdjust, Model model, RedirectAttributes redirectAttributes) {
		if(lcPurchaseAmountAdjust.getId()==null){
			return "";
		}
		lcPurchaseAmountAdjust = this.lcPurchaseAmountAdjustService.get(lcPurchaseAmountAdjust.getId());
		lcPurchaseAmountAdjust.setAdjustSta("8");
		lcPurchaseAmountAdjust.setCancelDate(new Date());
		lcPurchaseAmountAdjust.setCancelUser(UserUtils.getUser());    
		lcPurchaseAmountAdjustService.save(lcPurchaseAmountAdjust);
		addMessage(redirectAttributes, "取消采购金额调整项'" + lcPurchaseAmountAdjust.getSubject() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPurchaseAmountAdjust/?repage";
	}
	
	@RequestMapping(value = "view")
	public String view(LcPurchaseAmountAdjust lcPurchaseAmountAdjust, Model model) {
		if(lcPurchaseAmountAdjust.getId()!=null){
			lcPurchaseAmountAdjust=this.lcPurchaseAmountAdjustService.get(lcPurchaseAmountAdjust.getId());
		}
		model.addAttribute("lcPurchaseAmountAdjust", lcPurchaseAmountAdjust);
		return "modules/psi/lc/lcPurchaseAmountAdjustView";
	}
	
}
