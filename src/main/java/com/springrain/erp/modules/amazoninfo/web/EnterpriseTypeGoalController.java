package com.springrain.erp.modules.amazoninfo.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseGoal;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseTypeGoal;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseGoalService;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseTypeGoalService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.utils.UserUtils;


@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/enterpriseTypeGoal")
public class EnterpriseTypeGoalController extends BaseController {
	
	@Autowired
	private EnterpriseTypeGoalService enterpriseTypeGoalService;
	
	@Autowired
	private EnterpriseGoalService enterpriseGoalService;
	
	@Autowired
	private PsiProductTypeGroupDictService psiTypeGroupService;
	
	@ModelAttribute
	public EnterpriseTypeGoal get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return enterpriseTypeGoalService.get(id);
		}else{
			return new EnterpriseTypeGoal();
		}
	}
	
	@RequestMapping(value = "")
	public String list(EnterpriseTypeGoal enterpriseTypeGoal, RedirectAttributes redirectAttributes, Model model,HttpServletRequest request) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		String goalMonth = request.getParameter("goalMonth");
		if (StringUtils.isEmpty(goalMonth)) {
			enterpriseTypeGoal.setGoalMonth(new Date());
		} else {
			enterpriseTypeGoal.setGoalMonth(format.parse(goalMonth));
		}
		Map<String, Map<String, EnterpriseTypeGoal>> data = enterpriseTypeGoalService.findGoalByMonth(format.format(enterpriseTypeGoal.getGoalMonth()));
		model.addAttribute("data", data);
		model.addAttribute("enterpriseTypeGoal", enterpriseTypeGoal);
		Calendar calendar = Calendar.getInstance();
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		//6到10号可编辑当前月目标(目标改成1号计算,2-10号调整)
		if (dayOfMonth >= 2 && dayOfMonth <= 10 && 
				format.format(calendar.getTime()).equals(format.format(enterpriseTypeGoal.getGoalMonth()))) {
			model.addAttribute("isEdit", "1");
		}
		Map<String, String> typeLine = psiTypeGroupService.getTypeLine(goalMonth);
		if (typeLine == null || typeLine.size() == 0) {
			typeLine = psiTypeGroupService.getTypeLine(null);
		}
		model.addAttribute("typeLine", typeLine);
		return "modules/amazoninfo/salesTypeGoalList";
	}
	
	//更新产品线目标
	@ResponseBody
	@RequestMapping(value = {"updateGoal"})
	public String updateGoal(EnterpriseTypeGoal enterpriseTypeGoal) {
		enterpriseTypeGoal.setCreateDate(new Date());
		enterpriseTypeGoal.setCreateUser(UserUtils.getUser());
		enterpriseTypeGoalService.save(enterpriseTypeGoal);
		enterpriseTypeGoalService.update(enterpriseTypeGoal);
		return "更新目标成功！";
	}
	
	//手动控制计算目标
	@RequestMapping(value = "autoGoal")
	public String autoGoal(EnterpriseTypeGoal enterpriseTypeGoal, RedirectAttributes redirectAttributes, Model model,HttpServletRequest request) throws ParseException {
		try {
			Date today = new Date();
			logger.info("开始更新系统目标...");
			SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
			String month = format.format(DateUtils.addMonths(today, -1));	//往前推的到上个月的时间
			if (!"12".equals(month.substring(4))) {//年底不再往后计算动态目标
				//统计上个月各产品线销售额
				enterpriseTypeGoalService.updateDynamicGoal(today);
			}
			logger.info("更新系统目标完毕...");
		} catch (Exception e) {
			logger.error("更新系统目标发生异常", e);
		}
		return "modules/amazoninfo/salesTypeGoalList";
	}
	
	//手动控制分配2016年1至5月利润目标
	@RequestMapping(value = "autoProfitGoal")
	public String autoProfitGoal(EnterpriseTypeGoal enterpriseTypeGoal, RedirectAttributes redirectAttributes, Model model,HttpServletRequest request) throws ParseException {
		Map<String, String> lineMap = Maps.newHashMap();
		lineMap.put("A", "52aa512544ed405c884bd6d266cd5b0b");
		lineMap.put("B", "1e6235091b0b43fb9f310512c6924427");
		lineMap.put("C", "be2ed665694e4fd19dcda7e839bafd03");
		lineMap.put("D", "058a7c4ff7d443628d1bb0e35f1d3f8c");
		List<String> monthList = Lists.newArrayList("201601", "201602", "201603", "201604", "201605");
		List<String> countryList = Lists.newArrayList("en", "de", "fr", "it", "es", "jp");
		for (String month : monthList) {
			for (String country : countryList) {
				for (Map.Entry<String,String> entry : lineMap.entrySet()) { 
				    String line = entry.getKey();
					EnterpriseGoal goal = enterpriseGoalService.findByCountryAndMonthAndLine(month, country, entry.getValue());
					goal.setProfitGoal(goal.getGoal() * getAllProfitRate().get(country).get(line));
					enterpriseGoalService.save(goal);
				}
				
			}
		}
		return "modules/amazoninfo/salesTypeGoalList";
	}

	/**
	 * 毛利率指标
	 * @return
	 */
	public static Map<String, Map<String, Float>> getAllProfitRate() {
		Map<String, Map<String, Float>> rs = Maps.newHashMap();
		//英语国家
		Map<String, Float> enMap = Maps.newHashMap();
		rs.put("en", enMap);
		enMap.put("A", 0.29f);
		enMap.put("B", 0.31f);
		enMap.put("C", 0.23f);
		enMap.put("D", 0.37f);
		
		//德国
		Map<String, Float> deMap = Maps.newHashMap();
		rs.put("de", deMap);
		deMap.put("A", 0.21f);
		deMap.put("B", 0.23f);
		deMap.put("C", 0.23f);
		deMap.put("D", 0.21f);
		//法国
		Map<String, Float> frMap = Maps.newHashMap();
		rs.put("fr", frMap);
		frMap.put("A", 0.22f);
		frMap.put("B", 0.22f);
		frMap.put("C", 0.22f);
		frMap.put("D", 0.22f);
		//意大利
		Map<String, Float> itMap = Maps.newHashMap();
		rs.put("it", itMap);
		itMap.put("A", 0.19f);
		itMap.put("B", 0.19f);
		itMap.put("C", 0.19f);
		itMap.put("D", 0.19f);
		//西班牙
		Map<String, Float> esMap = Maps.newHashMap();
		rs.put("es", esMap);
		esMap.put("A", 0.24f);
		esMap.put("B", 0.24f);
		esMap.put("C", 0.24f);
		esMap.put("D", 0.24f);
		//日本
		Map<String, Float> jpMap = Maps.newHashMap();
		rs.put("jp", jpMap);
		jpMap.put("A", 0.32f);
		jpMap.put("B", 0.32f);
		jpMap.put("C", 0.32f);
		jpMap.put("D", 0.32f);
		
		return rs;
	}
	
	//2016各国家产品线利润占比,英语国家统一 [国家[产品线  比率]],用于初始化前5个月利润目标
	public static Map<String, Map<String, Float>> countryLineProfitRatio() {
		Map<String, Map<String, Float>> rs = Maps.newHashMap();
		//英语国家
		Map<String, Float> enMap = Maps.newHashMap();
		rs.put("en", enMap);
		enMap.put("A", 0.389f);
		enMap.put("B", 0.376f);
		enMap.put("C", 0.036f);
		enMap.put("D", 0.199f);
		
		//德国
		Map<String, Float> deMap = Maps.newHashMap();
		rs.put("de", deMap);
		deMap.put("A", 0.458f);
		deMap.put("B", 0.357f);
		deMap.put("C", 0.08f);
		deMap.put("D", 0.105f);
		//法国
		Map<String, Float> frMap = Maps.newHashMap();
		rs.put("fr", frMap);
		frMap.put("A", 0.411f);
		frMap.put("B", 0.44f);
		frMap.put("C", 0.044f);
		frMap.put("D", 0.105f);
		//意大利
		Map<String, Float> itMap = Maps.newHashMap();
		rs.put("it", itMap);
		itMap.put("A", 0.458f);
		itMap.put("B", 0.29f);
		itMap.put("C", 0.028f);
		itMap.put("D", 0.224f);
		//西班牙
		Map<String, Float> esMap = Maps.newHashMap();
		rs.put("es", esMap);
		esMap.put("A", 0.34f);
		esMap.put("B", 0.475f);
		esMap.put("C", 0.045f);
		esMap.put("D", 0.14f);
		//日本
		Map<String, Float> jpMap = Maps.newHashMap();
		rs.put("jp", jpMap);
		jpMap.put("A", 0.348f);
		jpMap.put("B", 0.525f);
		jpMap.put("C", 0.081f);
		jpMap.put("D", 0.046f);
		
		return rs;
	}
}
