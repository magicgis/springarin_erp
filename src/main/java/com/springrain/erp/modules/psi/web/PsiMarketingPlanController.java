/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.text.DateFormat;
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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.HisPsiMarketingPlan;
import com.springrain.erp.modules.psi.entity.PsiMarketingPlan;
import com.springrain.erp.modules.psi.entity.PsiMarketingPlanItem;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductAttribute;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.service.PsiMarketingPlanService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderService;

import freemarker.template.utility.DateUtil;

/**
 * 营销计划Controller
 * @author Michael
 * @version 2017-06-12
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiMarketingPlan")
public class PsiMarketingPlanController extends BaseController {
	@Autowired
	private PsiMarketingPlanService 	psiMarketingPlanService;
	@Autowired
	private PsiProductService		 	productService;
	@Autowired
	private LcPurchaseOrderService	 	purchaseOrderService;
	@Autowired
	private PsiProductTypeGroupDictService  groupDictService;
	@Autowired
	private PsiProductAttributeService  psiProductAttributeService;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	
	@RequiresPermissions("psi:psiMarketingPlan:view")
	@RequestMapping(value = {"list", ""})
	public String list(PsiMarketingPlan psiMarketingPlan,String nameColor,String lineId,String isCheck, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(isCheck)){
			isCheck="0";
		}
        Page<PsiMarketingPlan> page = psiMarketingPlanService.find(new Page<PsiMarketingPlan>(request, response), psiMarketingPlan,isCheck,nameColor,lineId); 
        List<PsiProductTypeGroupDict> lineList=groupDictService.getAllList();
        
        List<PsiProductAttribute> attrs = psiProductAttributeService.findAll();
        Map<String,PsiProductAttribute> productAttr = Maps.newHashMap();
		for (PsiProductAttribute psiProductAttribute : attrs) {
			productAttr.put(psiProductAttribute.getColorName(), psiProductAttribute);
		}
		
		model.addAttribute("productAttr", productAttr);
        model.addAttribute("lineList", lineList);
        model.addAttribute("isCheck", isCheck);
        model.addAttribute("nameColor", nameColor);
        model.addAttribute("lineId", lineId);
        model.addAttribute("page", page);
		return "modules/psi/psiMarketingPlanList";
	}

	@RequiresPermissions("psi:psiMarketingPlan:view")
	@RequestMapping(value = "form")
	public String form(PsiMarketingPlan psiMarketingPlan,String lineId, Model model) throws ParseException {
		if(psiMarketingPlan.getId()!=null){
			psiMarketingPlan=this.psiMarketingPlanService.get(psiMarketingPlan.getId());
		}
		//获取未来6个月周集合
		DateFormat formatWeek = new SimpleDateFormat("yyyyww");
		Date startDay = sdf.parse(sdf.format(new Date()));
		if(StringUtils.isNotEmpty(psiMarketingPlan.getStartWeek())){
			String week = psiMarketingPlan.getStartWeek();
			startDay= DateUtils.getFirstDayOfWeek(Integer.parseInt(week.substring(0, 4)), Integer.parseInt(week.substring(4)));
		}
	
		Date endDate = DateUtils.addMonths(psiMarketingPlan.getCreateDate()!=null?psiMarketingPlan.getCreateDate():new Date(),12);
		if(StringUtils.isNotEmpty(psiMarketingPlan.getEndWeek())){
			String week = psiMarketingPlan.getEndWeek();
			endDate= DateUtils.addMonths(DateUtils.getFirstDayOfWeek(Integer.parseInt(week.substring(0, 4)), Integer.parseInt(week.substring(4))),4);
		}
		
		Map<String,String>  weekMap = Maps.newTreeMap();
		while(startDay.before(endDate)){
			String week=DateUtils.getWeekStr(startDay,formatWeek, 4, "");
			weekMap.put(week,DateUtils.getWeekStartEnd(week));
			startDay=DateUtils.addDays(startDay, 7);
		}
		List<PsiProduct>  products = productService.findAll(psiMarketingPlan.getCountryCode());
		
		StringBuilder sb = new StringBuilder("");
		if(psiMarketingPlan.getItems()!=null){
			for(PsiMarketingPlanItem item:psiMarketingPlan.getItems()){
				sb.append(item.getId()+",");
			}
		}
		
		String itemIds="";
		if(StringUtils.isNotEmpty(sb.toString())){
			itemIds=sb.toString().substring(0,sb.toString().length()-1);
		}
		
		psiMarketingPlan.setOldItemIds(itemIds);
		model.addAttribute("lineId", lineId);
		model.addAttribute("weekMap", weekMap);
		model.addAttribute("products", products);
		model.addAttribute("psiMarketingPlan", psiMarketingPlan);
		return "modules/psi/psiMarketingPlanForm";
	}
	
	@RequiresPermissions("psi:psiMarketingPlan:review")
	@RequestMapping(value = "review")
	public String review(PsiMarketingPlan psiMarketingPlan, Model model) throws ParseException {
		psiMarketingPlan=this.psiMarketingPlanService.get(psiMarketingPlan.getId());
		model.addAttribute("psiMarketingPlan", psiMarketingPlan);
		return "modules/psi/psiMarketingPlanReview";
	}
	
	@RequiresPermissions("psi:psiMarketingPlan:view")
	@RequestMapping(value = "view")
	public String view(PsiMarketingPlan psiMarketingPlan, Model model) throws ParseException {
		psiMarketingPlan=this.psiMarketingPlanService.get(psiMarketingPlan.getId());
		List<HisPsiMarketingPlan> plans = this.psiMarketingPlanService.findHis(psiMarketingPlan.getId());
		model.addAttribute("plans", plans);
		model.addAttribute("psiMarketingPlan", psiMarketingPlan);
		return "modules/psi/psiMarketingPlanView";
	}

	@RequiresPermissions("psi:psiMarketingPlan:edit")
	@RequestMapping(value = "save")
	public String save(PsiMarketingPlan psiMarketingPlan,String lineId, Model model, RedirectAttributes redirectAttributes) {
		psiMarketingPlanService.editSave(psiMarketingPlan);
		if(psiMarketingPlan.getId()!=null){
			addMessage(redirectAttributes, "保存营销计划'" + psiMarketingPlan.getId() + "'成功");
		}else{
			addMessage(redirectAttributes, "保存营销计划成功");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiMarketingPlan?lineId="+lineId;
	}
	
	
	@RequiresPermissions("psi:psiMarketingPlan:review")
	@RequestMapping(value = "reviewSave")
	public String reviewSave(PsiMarketingPlan psiMarketingPlan, Model model, RedirectAttributes redirectAttributes) {
		psiMarketingPlanService.reviewSave(psiMarketingPlan);
		addMessage(redirectAttributes, "审核营销计划'" + psiMarketingPlan.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiMarketingPlan/?repage";
	}

	@RequiresPermissions("psi:psiMarketingPlan:edit")
	@RequestMapping(value = "cancel")
	public String cancel(PsiMarketingPlan psiMarketingPlan, Model model, RedirectAttributes redirectAttributes) {
		psiMarketingPlanService.cancel(psiMarketingPlan);
		addMessage(redirectAttributes, "取消营销计划'" + psiMarketingPlan.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiMarketingPlan/?repage";
	}
	
	@RequiresPermissions("psi:psiMarketingPlan:edit")
	@RequestMapping(value = "pause")
	public String pause(PsiMarketingPlan psiMarketingPlan, Model model, RedirectAttributes redirectAttributes) {
		psiMarketingPlan = this.psiMarketingPlanService.get(psiMarketingPlan.getId());
		psiMarketingPlan.setSta("5");
		psiMarketingPlanService.save(psiMarketingPlan);
		addMessage(redirectAttributes, "暂停广告计划'" + psiMarketingPlan.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiMarketingPlan/?repage";
	}
	
	@RequiresPermissions("psi:psiMarketingPlan:edit")
	@RequestMapping(value = "unPause")
	public String unPause(PsiMarketingPlan psiMarketingPlan, Model model, RedirectAttributes redirectAttributes) {
		psiMarketingPlan = this.psiMarketingPlanService.get(psiMarketingPlan.getId());
		psiMarketingPlan.setSta("3");
		psiMarketingPlanService.save(psiMarketingPlan);
		addMessage(redirectAttributes, "恢复暂停广告计划'" + psiMarketingPlan.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiMarketingPlan/?repage";
	}
	

	@ResponseBody
	@RequestMapping(value = "ajaxTips")
	public String ajaxTips(String productName,String countryCode,String colorCode) {
		String productNameColor = productName;
		if(StringUtils.isNotEmpty(colorCode)){
			productNameColor+="_"+colorCode;
		}
		Integer sale31 =this.psiMarketingPlanService.getSales31Days(productNameColor, countryCode);
		Map<String,Integer> inventoryMap = this.psiMarketingPlanService.getInventoryInfo(productName, colorCode);
		Integer inventoryQ = 0;
		StringBuffer otherTips=new StringBuffer("");
		for(Map.Entry<String, Integer> entry :inventoryMap.entrySet()){
			String key = entry.getKey();
			Integer val = entry.getValue();
			if(val!=null&&val>0){
				if(countryCode.equals(key)){
					inventoryQ=val;
				}else{
					otherTips.append(("com".equals(key)?"us":key)+"库存值["+val+"]");
				}
			}
		}
		
		String rs ="产品["+productNameColor+"],国家["+("com".equals(countryCode)?"us":countryCode)+"],31日销["+(sale31==null?0:sale31)+"],库存["+inventoryQ+"]" +
				(otherTips.length()>0?(",其他国家库存为："+otherTips):"");
		return rs;
	}
	
	@ResponseBody
	@RequestMapping(value = "initAds")
	public String initAds(){
		this.psiMarketingPlanService.initAds();
		return "初始化营销广告完成";
	}
	
	@ResponseBody
	@RequestMapping(value = "isExist")
	public String isExist(String productName,String countryCode,String colorCode,Integer id,String type,String startWeek) {
		if(id==null){
			id=0;
		}   
		return this.psiMarketingPlanService.isExist(productName, colorCode, countryCode, id,type, startWeek);
	}
	
}
