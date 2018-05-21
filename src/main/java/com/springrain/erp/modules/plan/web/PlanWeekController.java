package com.springrain.erp.modules.plan.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.plan.dto.Month;
import com.springrain.erp.modules.plan.dto.PlanMap;
import com.springrain.erp.modules.plan.entity.Plan;
import com.springrain.erp.modules.plan.service.PlanService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 日常工作计划Controller
 * @author tim
 * @version 2014-03-25
 */
@Controller
@RequestMapping(value = "${adminPath}/plan/week")
public class PlanWeekController extends BaseController {

	@Autowired
	private PlanService planService;
	
	@Autowired
	private SystemService systemService;
	
	@RequestMapping(value = {""})
	public String list(String year,String month ,String week,String userId, Model model) {
		User user;
		if(StringUtils.isBlank(userId)){
			 user = UserUtils.getUser();
			 userId = user.getId();
		}else{
			user = systemService.getUser(userId);
		}
		Month monthDto = null;
		if(StringUtils.isBlank(week)){
			if(StringUtils.isBlank(year)&&StringUtils.isBlank(month)){
				year = DateUtils.getYear();
				month = DateUtils.getMonth();
			}
			monthDto = Month.getInstance(year, month);
			week = monthDto.getCurrentWeek()+"";
		}else{
			monthDto = Month.getInstance(year, month);
			month = Integer.parseInt(month)+"";
		}
		month = Integer.parseInt(month)+"";
        List<Plan> plans = planService.findLogPlan(monthDto.getWeeks().get(Integer.parseInt(week)-1),year, month,week,userId); 
        Map<String,Plan> plansMap = Maps.newHashMap();
        for (Plan plan : plans) {
        	String type = plan.getType();
        	if("1".equals(type)){
        		plansMap.put("weekPlan", plan);
        	}else if("0".equals(type)){
        		plansMap.put(plan.getFlag(), plan);
        	}
		}
        model.addAttribute("user", user);
        model.addAttribute("plans", plansMap);
        model.addAttribute("monthDto", monthDto);
        model.addAttribute("currentWeek",week);
        model.addAttribute("currentWeekDto", monthDto.getCurrentWeekDto(Integer.parseInt(week)));
		return "modules/plan/planWeek";
	}

	@RequestMapping(value = "save")
	public String save(String week,PlanMap planMap,Model model, RedirectAttributes redirectAttributes) {
		if(planMap.getPlansMap()!=null){
			List<Plan> plans = Lists.newArrayList(planMap.getPlansMap().values());
			for (Plan plan : plans) {
				if (!beanValidator(model,plan)){
					return "modules/plan/week" ;
				}
			}
			for (Iterator<Plan> iterator = plans.iterator(); iterator.hasNext();) {
				Plan plan = iterator.next();
				if(plan.getContent()==null){
					iterator.remove();
				}
			}
			String flag = plans.get(0).getFlag();
			String[]temp = flag.split("/");
			String userId = "";
			if(plans.size()>0){
				planService.save(plans);
				userId = plans.get(0).getCreateBy().getId();
				addMessage(redirectAttributes, "保存日常日志成功");
			}
			return "redirect:"+Global.getAdminPath()+"/plan/week/?repage&year="+temp[0]+"&month="+temp[1]+"&week="+week+"&userId="+userId;
		}
		return "redirect:"+Global.getAdminPath()+"/plan/week/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = "saveClog")
	public String saveClog(Plan plan) {
		try {
			plan.setContent(URLDecoder.decode(plan.getContent(),"utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(plan.getContent(), e);
		}
		plan.setFlag(DateUtils.getDate("yyyy/MM/dd"));
		plan.setType("0");
		plan.setCreateBy(UserUtils.getUser());
		planService.save(plan);
		return plan.getId();
	}
	
}
