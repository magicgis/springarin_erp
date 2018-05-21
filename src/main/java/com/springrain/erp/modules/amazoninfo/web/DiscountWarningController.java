/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.amazoninfo.entity.DiscountWarning;
import com.springrain.erp.modules.amazoninfo.entity.DiscountWarningItem;
import com.springrain.erp.modules.amazoninfo.service.DiscountWarningService;

/**
 * 折扣预警Controller
 * @author Michael
 * @version 2015-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/discountWarning")
public class DiscountWarningController extends BaseController {

	@Autowired
	private DiscountWarningService discountWarningService;
	@Autowired
	private PsiInventoryFbaService fbaService; 
	
	@RequestMapping(value = {"list", ""})
	public String list(DiscountWarning discountWarning, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(discountWarning.getCountry())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					//只有一个平台上贴权限时设置默认country
					if (StringUtils.isEmpty(discountWarning.getCountry())) {
						discountWarning.setCountry(dict.getValue());
					} else {
						discountWarning.setCountry(null);
						break;
					}
				}
			}
		}
		
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
		if(discountWarning.getCreateDate()==null){
			try {
				discountWarning.setCreateDate(sdf.parse((sdf.format(DateUtils.addMonths(new Date(), -1)))));
			} catch (ParseException e) {
			}
		}
		if(discountWarning.getUpdateDate()==null){
			try {
				discountWarning.setUpdateDate(sdf.parse((sdf.format(new Date()))));
			} catch (ParseException e) {
			}
		}
		
        Page<DiscountWarning> page = discountWarningService.find(new Page<DiscountWarning>(request, response), discountWarning); 
        model.addAttribute("page", page);
		return "modules/amazoninfo/discountWarningList";
	}

	@RequestMapping(value = "add")
	public String add(DiscountWarning discountWarning, Model model) {
		Map<String,Integer> fbaMap =Maps.newHashMap();
		Map<String,String> skuMap = Maps.newHashMap();
		List<String> countrySet =Lists.newArrayList();
		String country = discountWarning.getCountry();
		
		//根据上贴权限设置默认的country
		List<Dict> dicts = DictUtils.getDictList("platform");
		for (Dict dict : dicts) {
			if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
				countrySet.add(dict.getValue());
			}
		}
		
		
		Set<String> countrys = Sets.newHashSet();
		if(StringUtils.isEmpty(country)){
			if(countrySet.size()>0){
				discountWarning.setCountry(countrySet.get(0));
				if("de,uk,es,it,fr".contains(discountWarning.getCountry())){
					countrys.add("de");
					countrys.add("uk");
					countrys.add("es");
					countrys.add("it");
					countrys.add("fr");
				}else{
					countrys.add(discountWarning.getCountry());
				}
				fbaMap =fbaService.getFbaInventroyData(countrys);
				skuMap =this.discountWarningService.getSkuMap(countrys,fbaMap.keySet());
				discountWarning.setCountry(countrySet.get(0));
			}
		}else{
			if("de,uk,es,it,fr".contains(discountWarning.getCountry())){
				countrys.add("de");
				countrys.add("uk");
				countrys.add("es");
				countrys.add("it");
				countrys.add("fr");
			}else{
				countrys.add(discountWarning.getCountry());
			}
			fbaMap =fbaService.getFbaInventroyData(countrys);
			skuMap =this.discountWarningService.getSkuMap(countrys,fbaMap.keySet());
		}
		
		model.addAttribute("countrySet", countrySet);
		model.addAttribute("fbaMap", JSON.toJSON(fbaMap));
		model.addAttribute("skuMap", JSON.toJSON(skuMap));
		model.addAttribute("discountWarning", discountWarning);
		return "modules/amazoninfo/discountWarningAdd";
	}
  
	@RequestMapping(value = "addSave")
	public String addSave(DiscountWarning discountWarning, Model model, RedirectAttributes redirectAttributes) {
		discountWarningService.addSave(discountWarning);
		addMessage(redirectAttributes, "新增折扣预警'" + ""+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/discountWarning/?repage";
	}
	
	@RequestMapping(value = "edit")
	public String edit(DiscountWarning discountWarning, Model model) {
		StringBuilder sb = new StringBuilder("");
		discountWarning = this.discountWarningService.get(discountWarning.getId());
		Set<String> countrys = Sets.newHashSet();
		if("de,uk,es,it,fr".contains(discountWarning.getCountry())){
			countrys.add("de");
			countrys.add("uk");
			countrys.add("es");
			countrys.add("it");
			countrys.add("fr");
		}else{
			countrys.add(discountWarning.getCountry());
		}
		Map<String,Integer> fbaMap =fbaService.getFbaInventroyData(countrys);
		Map<String,String> skuMap =this.discountWarningService.getSkuMap(countrys,fbaMap.keySet());
		
		for (DiscountWarningItem item :discountWarning.getItems()) {
			sb.append(item.getId()+",");
		}
		
		discountWarning.setOldItemIds(sb.toString().substring(0,sb.toString().length()-1));
	
		model.addAttribute("fbaMap", JSON.toJSON(fbaMap));
		model.addAttribute("fbaMapSelf", fbaMap);
		model.addAttribute("skuMapSelf", skuMap);
		model.addAttribute("skuMap", JSON.toJSON(skuMap));
		model.addAttribute("discountWarning", discountWarning);
		return "modules/amazoninfo/discountWarningEdit";
	}

	@RequestMapping(value = "editSave")
	public String editSave(DiscountWarning discountWarning, Model model, RedirectAttributes redirectAttributes) {
		discountWarningService.editSave(discountWarning);
		addMessage(redirectAttributes, "编辑折扣预警'" + ""+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/discountWarning/?repage";
	}
	
	@RequestMapping(value = "cancel")
	public String cancel(DiscountWarning discountWarning, Model model,RedirectAttributes redirectAttributes) {
		discountWarning = this.discountWarningService.get(discountWarning.getId());
		discountWarning.setWarningSta("2");//已取消
		this.discountWarningService.save(discountWarning);
		addMessage(redirectAttributes, "取消折扣预警'" + discountWarning.getPromotionId()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/discountWarning/?repage";
	}
	
}
