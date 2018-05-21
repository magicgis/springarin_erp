/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseWeek;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseWeight;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseWeekService;


@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/enterpriseWeek")
public class EnterpriseWeekController extends BaseController {
	
	
	@Autowired
	private EnterpriseWeekService enterpriseWeekService;
	
	
	@ResponseBody
	@RequestMapping(value = {"updateWeight"})
	public String updateWeight(String country,Float updValue,String type) {
		return this.enterpriseWeekService.updateWeight(country, updValue, type);
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(EnterpriseWeek enterpriseWeek, HttpServletRequest request, HttpServletResponse response, Model model,String removeCountry,String start,String end){
		List<EnterpriseWeek> list=enterpriseWeekService.findEnterpriseWeek(enterpriseWeek);
		if(list.size()>0){
			EnterpriseWeek temp=list.get(list.size()-1);
			model.addAttribute("avg",temp);
			model.addAttribute("avgTotal",new BigDecimal(temp.getMonday()+temp.getTuesday()+temp.getWednesday()+temp.getThursday()+temp.getFriday()+temp.getSaturday()+temp.getSunday()).setScale(2,4).floatValue());
			
		}
	     model.addAttribute("list", list);
		 Calendar calendar = Calendar.getInstance();
		 calendar.set(Calendar.DAY_OF_MONTH, 1); 
		 calendar.add(Calendar.DATE, -1);
		 Calendar curr = Calendar.getInstance();
		 curr.set(Calendar.YEAR,curr.get(Calendar.YEAR)-1);
		 curr.set(Calendar.DAY_OF_MONTH, 1);
		 if(enterpriseWeek.getStartDate()==null){
			 enterpriseWeek.setStartDate(curr.getTime());
		 }
		 if(enterpriseWeek.getEndDate()==null){
			 enterpriseWeek.setEndDate(calendar.getTime()); 
		 }
		 if(StringUtils.isNotBlank(start)){
			 try {
				enterpriseWeek.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse(start));
			} catch (ParseException e) {
				enterpriseWeek.setStartDate(curr.getTime());
			}
		 }
		 if(StringUtils.isNotBlank(end)){
			 try {
				enterpriseWeek.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse(end));
			} catch (ParseException e) {
				 enterpriseWeek.setEndDate(calendar.getTime()); 
			}
		 }
		model.addAttribute("removeCountry", removeCountry);
		model.addAttribute("enterpriseWeek", enterpriseWeek);
		List<EnterpriseWeight> weightList=enterpriseWeekService.findEnterpriseWeight(enterpriseWeek);
		for (EnterpriseWeight enterpriseWeight : weightList) {
			if("0".equals(enterpriseWeight.getFlag())){
				model.addAttribute("weightBefore", enterpriseWeight);
			}else{
				model.addAttribute("weightAfter", enterpriseWeight);
			}
		
		}
		
		return "modules/amazoninfo/createEnterpriseWeekCount";
	}
	
	@RequestMapping(value = "create")
	public String save(EnterpriseWeek enterpriseWeek, RedirectAttributes redirectAttributes, Model model,String removeCountry) {
		enterpriseWeekService.createEnterpriseWeek(enterpriseWeek,removeCountry);
		model.addAttribute("enterpriseWeek", enterpriseWeek);
		model.addAttribute("removeCountry", removeCountry);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/enterpriseWeek/?start="+new SimpleDateFormat("yyyy-MM-dd").format(enterpriseWeek.getStartDate())+"&end="+new SimpleDateFormat("yyyy-MM-dd").format(enterpriseWeek.getEndDate())+"&removeCountry="+removeCountry;
		//return "redirect:"+Global.getAdminPath()+"/amazoninfo/enterpriseWeek/";
	}
	
}
