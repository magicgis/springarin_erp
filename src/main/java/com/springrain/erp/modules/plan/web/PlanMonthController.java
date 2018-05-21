package com.springrain.erp.modules.plan.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.OfficeService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 日常工作计划Controller
 * @author tim
 * @version 2014-03-25
 */
@Controller
@RequestMapping(value = "${adminPath}/plan/month")
public class PlanMonthController extends BaseController {

	@Autowired
	private PlanService planService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private OfficeService officeService;
		
	@RequestMapping(value = {""})
	public String list(String year,String month ,String userId,String dep,Model model) {
		User user;
		if(StringUtils.isBlank(userId)){
			if(StringUtils.isBlank(dep)){
				user = UserUtils.getUser();
			}else{
				List<User> users = officeService.get(dep).getUserList();
				if(users.size()==0){
					return "modules/plan/planMonth";
				}else{
					user = users.get(0);
				}
			}	
		}else{
			user = systemService.getUser(userId);
		}
		if(StringUtils.isBlank(year)){
			year = DateUtils.getYear();
			month = DateUtils.getMonth(); 
		}
		month = Integer.parseInt(month)+"";
        List<Plan> plans = planService.findMonthPlan(year, month, user.getId(), user.getOffice().getId()); 
        Map<String,Plan> plansMap = Maps.newHashMap();
        for (Plan plan : plans) {
        	String type = plan.getType();
        	if("1".equals(type)){
        		String flag = plan.getFlag();
        		plansMap.put("w"+flag.split("/")[2], plan);
        	}else if("2".equals(type)){
        		plansMap.put("monthPlan", plan);
        	}else if("3".equals(type)){
        		plansMap.put("depPlan", plan);
        	}
		}
        Month monthDto = Month.getInstance(year, month);
        model.addAttribute("user", user);
        model.addAttribute("cuser", UserUtils.getUser());
        model.addAttribute("plans", plansMap);
        model.addAttribute("monthDto", monthDto);
		return "modules/plan/planMonth";
	}
	
	@RequestMapping(value = "saveDepPlan")
	public String save(Plan plan,Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, plan)){
			return "modules/plan/month" ;
		}
		String flag = plan.getFlag();
		String[]temp = flag.split("/");
		if(plan.getContent()!=null){
			planService.save(plan);
			addMessage(redirectAttributes, "保存部门工作计划成功");
		}
		return "redirect:"+Global.getAdminPath()+"/plan/month/?repage&year="+temp[0]+"&month="+temp[1]+"&userId="+plan.getCreateBy().getId();
	}
	
	@RequestMapping(value = "saveMonthPlan")
	public String saveMonthPlan(Plan plan,Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, plan)){
			return "modules/plan/month" ;
		}
		String flag = plan.getFlag();
		String[]temp = flag.split("/");
		if(plan.getContent()!=null){
			planService.save(plan);
			addMessage(redirectAttributes, "保存我的工作计划成功");
		}
		return "redirect:"+Global.getAdminPath()+"/plan/month/?repage&year="+temp[0]+"&month="+temp[1]+"&userId="+plan.getCreateBy().getId();
	}
	
	@RequestMapping(value = "saveWeekPlan")
	public String saveWeekPlan(PlanMap planMap,Model model, RedirectAttributes redirectAttributes) {
		if(planMap.getPlansMap()!=null){
			List<Plan> plans = Lists.newArrayList(planMap.getPlansMap().values());
			for (Plan plan : plans) {
				if (!beanValidator(model,plan)){
					return "modules/plan/month" ;
				}
			}
			String flag = plans.get(0).getFlag();
			String[]temp = flag.split("/");
			for (Iterator<Plan> iterator = plans.iterator(); iterator.hasNext();) {
				Plan plan = iterator.next();
				if(plan.getContent()==null){
					iterator.remove();
				}
			}
			String userId = "";
			if(plans.size()>0){
				Plan plan = plans.get(plans.size()-1);
				//每月末自动写入下月初相同周的周计划
				if(plan.getContent().length()>0){
					try{
						String week = plan.getFlag().split("/")[2];
						Month monthDto = Month.getInstance(temp[0],temp[1]);
						Date[] dates = monthDto.getCurrentWeekDto(Integer.parseInt(week)).getDayData();
						Date date = dates[dates.length-1];
						if(date.getMonth()+1 != Integer.parseInt(temp[1])){
							Plan tp = new Plan();
							tp.setContent(plan.getContent());
							tp.setCreateBy(plan.getCreateBy());
							tp.setFlag((date.getYear()+1900)+"/"+(date.getMonth()+1)+"/1");
							tp.setType("1");
							plans.add(tp);
						}
					}catch(Exception e){
						logger.error(e.getMessage(), e);
					}
				}
				planService.save(plans);
				userId = plans.get(0).getCreateBy().getId();
				addMessage(redirectAttributes, "保存我的周工作计划成功");
			}
			return "redirect:"+Global.getAdminPath()+"/plan/month/?repage&year="+temp[0]+"&month="+temp[1]+"&userId="+userId;
		}
		return "redirect:"+Global.getAdminPath()+"/plan/month/?repage";
	}
	
	@RequestMapping(value = "view")
	public String view(Date date,String dep,String userId,Model model) {
		Office office;
		if(StringUtils.isBlank(dep)){
			office = UserUtils.getUser().getOffice();
			userId ="";
		}else{
			office = officeService.get(dep);
		}	
		if(date ==null){
			date = new Date();
		}
		String cweek = Month.getCurrentWeek(date)+ "" ;
		
		List<Plan> plans = planService.viewPlan(date,cweek,office,UserUtils.getUserById(userId)); 
		
        Map<String, HashMap<String,List<Plan>>> plansMap = Maps.newHashMap();
        Map<String, String> userIdMap = Maps.newHashMap();
        Plan depPlan = null;
        for (Plan plan : plans) {
        	User user = plan.getCreateBy();
        	String key =user.getLoginName();
        	if("3".equals(plan.getType())){
        		depPlan = plan;
        	}else{
	        	if(plansMap.get(key)==null){
	        		plansMap.put(key, new HashMap<String,List<Plan>>());
	        	}
	        	if(userIdMap.get(key)==null){
	        		userIdMap.put(key, user.getId());
	        	}
        	}
        	HashMap<String, List<Plan>> temp = plansMap.get(key);
        	String type = plan.getType();
        	if("1".equals(type)){
        		temp.put("weekPlan",Lists.newArrayList(plan));
        	}else if("2".equals(type)){
        		temp.put("monthPlan", Lists.newArrayList(plan));
        	}else if("0".equals(type)){
        		if(temp.get("log")==null){
        			temp.put("log",Lists.<Plan>newArrayList());
        		}
        		temp.get("log").add(plan);
        	}
		}
    	DateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
        String dateStr = format.format(date);
        String[] temp = dateStr.split("-");
        model.addAttribute("depPlan",depPlan);
        model.addAttribute("year",temp[0]);
        model.addAttribute("month",temp[1]);
        model.addAttribute("week",cweek);
        model.addAttribute("plans", plansMap);
        model.addAttribute("userIdMap", userIdMap);
        model.addAttribute("date",dateStr);
        model.addAttribute("depId",office.getId());
        model.addAttribute("userId",userId);
        model.addAttribute("users",office.getUserList());
        model.addAttribute("cdepId",UserUtils.getUser().getOffice().getId());
		return "modules/plan/planView";
	}
	
	@RequestMapping(value = "monthView")
	public String monthView(String month,String dep,String userId,Model model) {
		Office office;
		if(StringUtils.isBlank(dep)){
			office = UserUtils.getUser().getOffice();
			userId ="";
		}else{
			office = officeService.get(dep);
		}	
		if(month ==null){
			month = DateUtils.getMonth();
		}	
		month = Integer.parseInt(month)+"";
		List<Plan> plans = planService.viewMonthPlan(month,office,UserUtils.getUserById(userId)); 
		
        Map<String, HashMap<String,List<Plan>>> plansMap = Maps.newHashMap();
        Map<String, String> userIdMap = Maps.newHashMap();
        Plan depPlan = null;
        for (Plan plan : plans) {
        	User user = plan.getCreateBy();
        	String key =user.getLoginName();
        	if("3".equals(plan.getType())){
        		depPlan = plan;
        	}else{
	        	if(plansMap.get(key)==null){
	        		plansMap.put(key, new HashMap<String,List<Plan>>());
	        	}
	        	if(userIdMap.get(key)==null){
	        		userIdMap.put(key, user.getId());
	        	}
        	}
        	HashMap<String, List<Plan>> temp = plansMap.get(key);
        	String type = plan.getType();
        	if("1".equals(type)){
        		if(temp.get("weekPlan")==null){
        			temp.put("weekPlan",Lists.<Plan>newArrayList(null,null,null,null,null));
        		}
        		int index =Integer.parseInt(plan.getFlag().split("/")[2])-1;
        		temp.get("weekPlan").set(index,plan);
        	}else if("2".equals(type)){
        		temp.put("monthPlan", Lists.newArrayList(plan));
        	}else if("0".equals(type)){
        		if(temp.get("log")==null){
        			temp.put("log",Lists.<Plan>newArrayList());
        		}
        		temp.get("log").add(plan);
        	}
		}
        model.addAttribute("depPlan",depPlan);
        model.addAttribute("year",DateUtils.getYear());
        model.addAttribute("month",month);
        model.addAttribute("plans", plansMap);
        model.addAttribute("userIdMap", userIdMap);
        model.addAttribute("depId",office.getId());
        model.addAttribute("userId",userId);
        model.addAttribute("users",office.getUserList());
        model.addAttribute("cdepId",UserUtils.getUser().getOffice().getId());
		return "modules/plan/planMonthView";
	}
}
