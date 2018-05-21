/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.EvaluateWarning;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.scheduler.AmazonWSConfig;
import com.springrain.erp.modules.amazoninfo.service.EvaluateWarningService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 折扣预警Controller
 * @author Michael
 * @version 2015-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/evaluateWarning")
public class EvaluateWarningController extends BaseController {

	@Autowired
	private EvaluateWarningService evaluateWarningService;
	
	@RequestMapping(value = {"list", ""})
	public String list(EvaluateWarning evaluateWarning, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if(StringUtils.isEmpty(evaluateWarning.getCountry())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					if(StringUtils.isEmpty(evaluateWarning.getCountry())){
						evaluateWarning.setCountry(dict.getValue());
					} else {
						evaluateWarning.setCountry(null);
						break;
					}
				}
			}
		}
		
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
		if(evaluateWarning.getCreateDate()==null){
			evaluateWarning.setCreateDate(sdf.parse((sdf.format(DateUtils.addMonths(new Date(), -3)))));
		}
		if(evaluateWarning.getUpdateDate()==null){
			evaluateWarning.setUpdateDate(sdf.parse((sdf.format(new Date()))));
		}
		
        Page<EvaluateWarning> page = evaluateWarningService.find(new Page<EvaluateWarning>(request, response), evaluateWarning); 
        model.addAttribute("page", page);
		return "modules/amazoninfo/evaluateWarningList";
	}

	@RequestMapping(value = "form")
	public String form(EvaluateWarning evaluateWarning, Model model) {
		if(evaluateWarning.getId()!=null){
			evaluateWarning=this.evaluateWarningService.get(evaluateWarning.getId());
		}
		String country = evaluateWarning.getCountry();
		List<String> countrySet =Lists.newArrayList();
		
		List<Dict> dicts = DictUtils.getDictList("platform");
		if(UserUtils.getUser().isAdmin()){
			for (Dict dict : dicts) {
				if(!dict.getValue().equals("com.inteck")){
					countrySet.add(dict.getValue());
				}
			}
		} else {
			//根据上贴权限设置默认的country
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					countrySet.add(dict.getValue());
				}
			}
		}
		
		if(StringUtils.isEmpty(country)){
			if(countrySet.size()>0){
				evaluateWarning.setCountry(countrySet.get(0));
				evaluateWarning.setCountry(countrySet.get(0));
			}
		}
		model.addAttribute("countrySet", countrySet);
		model.addAttribute("evaluateWarning", evaluateWarning);
		return "modules/amazoninfo/evaluateWarningForm";
	}
  
	@RequestMapping(value = "save")
	public String save(EvaluateWarning evaluateWarning, Model model, RedirectAttributes redirectAttributes) {
		if(evaluateWarning.getId()==null){
			evaluateWarning.setCreateDate(new Date());
			evaluateWarning.setCreateUser(UserUtils.getUser());
			evaluateWarning.setUpdateDate(localDateToOtherCountryDate(evaluateWarning.getCreateDate(),evaluateWarning.getCountry()));
		}
		evaluateWarningService.save(evaluateWarning);
		addMessage(redirectAttributes, "保存评测折扣预警'" + ""+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/evaluateWarning/?repage";
	}
	
	
	
	@RequestMapping(value = "cancel")
	public String cancel(EvaluateWarning evaluateWarning, Model model,RedirectAttributes redirectAttributes) {
		evaluateWarning = this.evaluateWarningService.get(evaluateWarning.getId());
		evaluateWarning.setWarningSta("2");//已取消
		this.evaluateWarningService.save(evaluateWarning);
		addMessage(redirectAttributes, "取消评测折扣预警'" + evaluateWarning.getPromotionId()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/evaluateWarning/?repage";
	}
	
	public Date localDateToOtherCountryDate(Date date,String country) {
		DateFormat sdf = AmazonOrder.getFormat();
		if("de,it,es,fr".contains(country)){
			sdf.setTimeZone(AmazonWSConfig.get("de").getTimeZone());
		}else{
			sdf.setTimeZone(AmazonWSConfig.get(country).getTimeZone());
		}
		String time = sdf.format(date);
		sdf.setTimeZone(TimeZone.getDefault());
		try {
			return sdf.parse(time);
		} catch (ParseException e) {}
		return null;
   }

}
