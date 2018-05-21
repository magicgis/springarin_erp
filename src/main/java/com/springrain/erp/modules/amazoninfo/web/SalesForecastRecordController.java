package com.springrain.erp.modules.amazoninfo.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecastByMonth;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecastRecord;
import com.springrain.erp.modules.amazoninfo.service.SalesForecastRecordService;
import com.springrain.erp.modules.amazoninfo.service.SalesForecastServiceByMonth;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductInStockService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 销量预测修改记录Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/salesForecastRecord")
public class SalesForecastRecordController extends BaseController {

	@Autowired
	private SalesForecastRecordService salesForecastRecordService;

	@Autowired
	private SalesForecastServiceByMonth salesForecastService;
	
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;

	@Autowired
	private PsiProductInStockService inStockService;
	
	private static DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
	
	@ModelAttribute
	public SalesForecastRecord get(@RequestParam(required = false) Integer id) {
		if (id != null) {
			return salesForecastRecordService.get(id);
		} else {
			return new SalesForecastRecord();
		}
	}

	@RequestMapping(value = {"list", ""})
	public String list(SalesForecastRecord salesForecastRecord, String month,String flag, HttpServletRequest request, 
			HttpServletResponse response, Model model) throws ParseException{
		if (StringUtils.isEmpty(salesForecastRecord.getState())) {
			salesForecastRecord.setState("0");
		}
		if (StringUtils.isEmpty(flag)) {	//默认查询急需审批的
			flag = "1";
		}
		Calendar calendar = Calendar.getInstance();
		if (StringUtils.isEmpty(month) || 
				("0".equals(salesForecastRecord.getState()) && !month.equals(monthFormat.format(calendar.getTime())))) {	//默认查询当前月份,审批操作只针对当前月份提交的数据,因为之前提交的数据已无意义
			month = monthFormat.format(new Date());
		}
		Date createTime = monthFormat.parse(month);
		List<String> dates = Lists.newArrayList();
		Map<String, Boolean> isEditMap = Maps.newHashMap();
		
		boolean noEditCurrentMonth = false;
		if (calendar.get(Calendar.DAY_OF_MONTH) >= 15) {	//15号开始不允许修改当月预测数据
			noEditCurrentMonth = true;
		}
		for (int i = 0; i < 6; i++) {
			Date temp = DateUtils.addMonths(createTime, i);
			String monthStr = monthFormat.format(temp);
			dates.add(monthStr);
			if (Integer.parseInt(monthStr.replace("-", "")) < Integer.parseInt(monthFormat.format(calendar.getTime()).replace("-", ""))) {	//小于当前月不能修改
				isEditMap.put(monthStr, false);
			} else if(monthStr.equals(monthFormat.format(calendar.getTime())) && noEditCurrentMonth){	//当前月15号开始不能修改
				isEditMap.put(monthStr, false);
			} else {
				isEditMap.put(monthStr, true);
			}
		}
        model.addAttribute("dates", dates);
        model.addAttribute("isEditMap", isEditMap);
		List<String> colorNameList = Lists.newArrayList();
		if ("1".equals(flag) && "0".equals(salesForecastRecord.getState())) {
			colorNameList = inStockService.findOrderQuantity();	//下单量大于0的产品
		}
		Page<SalesForecastRecord> page = salesForecastRecordService.find(
				new Page<SalesForecastRecord>(request, response), salesForecastRecord, month, flag, colorNameList); 
        model.addAttribute("page", page);
        model.addAttribute("month", month);
        model.addAttribute("flag", flag);
        
		Date today = DateUtils.getLastDayOfMonth(DateUtils.getDateStart(new Date()));
		Date start = DateUtils.addMonths(today, -3);
		Date end = DateUtils.addMonths(today, 6);
		//产品 [国家[月  数]]
		Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAllWithType(start,end);
		model.addAttribute("data",data);
        return "modules/amazoninfo/salesForecastApprovalList";
	}
	
	@RequestMapping(value ="goEdit")
	public String goEdit(String productName, String country, Model model) {
		Date today = DateUtils.getLastDayOfMonth(DateUtils.getDateStart(new Date()));
		Date start = DateUtils.addMonths(today, -3);
		Date end = DateUtils.addMonths(today, 6);
		Map<String, Map<String, SalesForecastByMonth>>  data = salesForecastService.findByCountryType(country,start,end);
		model.addAttribute("data",data);
		List<PsiProductEliminate> list = psiProductEliminateService.findOnSaleNotNew(country);
		List<String> productNames = Lists.newArrayList();
		for (PsiProductEliminate psiProductEliminate : list) {
			productNames.add(psiProductEliminate.getColorName());
		}
		model.addAttribute("productNames", productNames);

		List<String> dates = Lists.newArrayList();
		int i = 3;
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.DAY_OF_MONTH) >= 15) {	//15号开始不允许修改当月预测数据
			model.addAttribute("hiddenCurrentMonth", true);
		}
		for (; i < 9; i++) {
			dates.add(monthFormat.format(DateUtils.addMonths(start,i)));
		}
		model.addAttribute("dates", dates);
		SalesForecastRecord record = salesForecastRecordService.findSalesForecastRecord(country, productName, null);
		if (record == null) {
			record = new SalesForecastRecord();
		} else if(!monthFormat.format(calendar.getTime()).equals(record.getMonth())){	//覆盖不是本月的预测数据,先清空
			record.setForecast1(null);
			record.setForecast2(null);
			record.setForecast3(null);
			record.setForecast4(null);
			record.setForecast5(null);
			record.setForecast6(null);
		}
		model.addAttribute("salesForecastRecord", record);
		model.addAttribute("productName", productName);
		model.addAttribute("country", country);
		return "modules/amazoninfo/salesForecastRecordEdit";
	}

	@RequestMapping(value ="save")
	public String save(SalesForecastRecord salesForecastRecord, Model model, RedirectAttributes redirectAttributes) {
		salesForecastRecord.setCreateBy(UserUtils.getUser());
		salesForecastRecord.setCreateDate(new Date());
		salesForecastRecord.setState("0");
		salesForecastRecord.setMonth(monthFormat.format(new Date()));
		salesForecastRecordService.save(salesForecastRecord);
		addMessage(redirectAttributes, "操作成功！");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/salesForecastByMonth/?country="+salesForecastRecord.getCountry();
	}
	
	@RequestMapping(value = "approval")
	public String approval(SalesForecastRecord salesForecastRecord, String state, HttpServletRequest request, 
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Date createTime = salesForecastRecord.getCreateDate();
		List<String> dates = Lists.newArrayList();
		for (int i = 0; i < 6; i++) {
			dates.add(monthFormat.format(DateUtils.addMonths(createTime, i)));
		}
		salesForecastRecord.setState(state);
		String country = salesForecastRecord.getCountry();
		String productName = salesForecastRecord.getProductName();
		List<SalesForecastByMonth> salesForecastByMonths = Lists.newArrayList();
		if ("1".equals(state)) {	//审核通过后把预测销量更新到预测表
			if (salesForecastRecord.getForecast1() != null) {
				String month = dates.get(0);
				List<SalesForecastByMonth> SalesForecastByMonths = salesForecastService.findSalesForecastList(country, productName, month);
				for (SalesForecastByMonth salesForecastByMonth : SalesForecastByMonths) {
					salesForecastByMonth.setQuantityAuthentication(salesForecastRecord.getForecast1());
					salesForecastByMonths.add(salesForecastByMonth);
				}
			}
			if (salesForecastRecord.getForecast2() != null) {
				String month = dates.get(1);
				List<SalesForecastByMonth> SalesForecastByMonths = salesForecastService.findSalesForecastList(country, productName, month);
				for (SalesForecastByMonth salesForecastByMonth : SalesForecastByMonths) {
					salesForecastByMonth.setQuantityAuthentication(salesForecastRecord.getForecast2());
					salesForecastByMonths.add(salesForecastByMonth);
				}
			}
			if (salesForecastRecord.getForecast3() != null) {
				String month = dates.get(2);
				List<SalesForecastByMonth> SalesForecastByMonths = salesForecastService.findSalesForecastList(country, productName, month);
				for (SalesForecastByMonth salesForecastByMonth : SalesForecastByMonths) {
					salesForecastByMonth.setQuantityAuthentication(salesForecastRecord.getForecast3());
					salesForecastByMonths.add(salesForecastByMonth);
				}
			}
			if (salesForecastRecord.getForecast4() != null) {
				String month = dates.get(3);
				List<SalesForecastByMonth> SalesForecastByMonths = salesForecastService.findSalesForecastList(country, productName, month);
				for (SalesForecastByMonth salesForecastByMonth : SalesForecastByMonths) {
					salesForecastByMonth.setQuantityAuthentication(salesForecastRecord.getForecast4());
					salesForecastByMonths.add(salesForecastByMonth);
				}
			}
			if (salesForecastRecord.getForecast5() != null) {
				String month = dates.get(4);
				List<SalesForecastByMonth> SalesForecastByMonths = salesForecastService.findSalesForecastList(country, productName, month);
				for (SalesForecastByMonth salesForecastByMonth : SalesForecastByMonths) {
					salesForecastByMonth.setQuantityAuthentication(salesForecastRecord.getForecast5());
					salesForecastByMonths.add(salesForecastByMonth);
				}
			}
			if (salesForecastRecord.getForecast6() != null) {
				String month = dates.get(5);
				List<SalesForecastByMonth> SalesForecastByMonths = salesForecastService.findSalesForecastList(country, productName, month);
				for (SalesForecastByMonth salesForecastByMonth : SalesForecastByMonths) {
					salesForecastByMonth.setQuantityAuthentication(salesForecastRecord.getForecast6());
					salesForecastByMonths.add(salesForecastByMonth);
				}
			}
			if (salesForecastByMonths.size() > 0) {
				salesForecastService.save(salesForecastByMonths);
			}
		}
		salesForecastRecordService.save(salesForecastRecord);
		addMessage(redirectAttributes, "审批操作成功！");
        return "redirect:"+Global.getAdminPath()+"/amazoninfo/salesForecastRecord/?repage";
	}

	@RequestMapping(value = "batchApproval")
	public String batchApproval(@RequestParam("eid[]")String[] eid, String state, HttpServletRequest request, 
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		List<SalesForecastRecord> forecastRecords = Lists.newArrayList();
		List<SalesForecastByMonth> salesForecastByMonths = Lists.newArrayList();
		for (String str : eid) {
			Integer id = Integer.parseInt(str);
			SalesForecastRecord salesForecastRecord = salesForecastRecordService.get(id);
			salesForecastRecord.setState(state);
			forecastRecords.add(salesForecastRecord);
			
			Date createTime = salesForecastRecord.getCreateDate();
			List<String> dates = Lists.newArrayList();
			for (int i = 0; i < 6; i++) {
				dates.add(monthFormat.format(DateUtils.addMonths(createTime, i)));
			}

			String country = salesForecastRecord.getCountry();
			String productName = salesForecastRecord.getProductName();
			if ("1".equals(state)) {	//审核通过后把预测销量更新到预测表
				if (salesForecastRecord.getForecast1() != null) {
					String month = dates.get(0);
					List<SalesForecastByMonth> SalesForecastByMonths = salesForecastService.findSalesForecastList(country, productName, month);
					for (SalesForecastByMonth salesForecastByMonth : SalesForecastByMonths) {
						salesForecastByMonth.setQuantityAuthentication(salesForecastRecord.getForecast1());
						salesForecastByMonths.add(salesForecastByMonth);
					}
				}
				if (salesForecastRecord.getForecast2() != null) {
					String month = dates.get(1);
					List<SalesForecastByMonth> SalesForecastByMonths = salesForecastService.findSalesForecastList(country, productName, month);
					for (SalesForecastByMonth salesForecastByMonth : SalesForecastByMonths) {
						salesForecastByMonth.setQuantityAuthentication(salesForecastRecord.getForecast2());
						salesForecastByMonths.add(salesForecastByMonth);
					}
				}
				if (salesForecastRecord.getForecast3() != null) {
					String month = dates.get(2);
					List<SalesForecastByMonth> SalesForecastByMonths = salesForecastService.findSalesForecastList(country, productName, month);
					for (SalesForecastByMonth salesForecastByMonth : SalesForecastByMonths) {
						salesForecastByMonth.setQuantityAuthentication(salesForecastRecord.getForecast3());
						salesForecastByMonths.add(salesForecastByMonth);
					}
				}
				if (salesForecastRecord.getForecast4() != null) {
					String month = dates.get(3);
					List<SalesForecastByMonth> SalesForecastByMonths = salesForecastService.findSalesForecastList(country, productName, month);
					for (SalesForecastByMonth salesForecastByMonth : SalesForecastByMonths) {
						salesForecastByMonth.setQuantityAuthentication(salesForecastRecord.getForecast4());
						salesForecastByMonths.add(salesForecastByMonth);
					}
				}
				if (salesForecastRecord.getForecast5() != null) {
					String month = dates.get(4);
					List<SalesForecastByMonth> SalesForecastByMonths = salesForecastService.findSalesForecastList(country, productName, month);
					for (SalesForecastByMonth salesForecastByMonth : SalesForecastByMonths) {
						salesForecastByMonth.setQuantityAuthentication(salesForecastRecord.getForecast5());
						salesForecastByMonths.add(salesForecastByMonth);
					}
				}
				if (salesForecastRecord.getForecast6() != null) {
					String month = dates.get(5);
					List<SalesForecastByMonth> SalesForecastByMonths = salesForecastService.findSalesForecastList(country, productName, month);
					for (SalesForecastByMonth salesForecastByMonth : SalesForecastByMonths) {
						salesForecastByMonth.setQuantityAuthentication(salesForecastRecord.getForecast6());
						salesForecastByMonths.add(salesForecastByMonth);
					}
				}
			}
		}
		if (salesForecastByMonths.size() > 0) {
			salesForecastService.save(salesForecastByMonths);
		}
		if (forecastRecords.size() > 0) {
			salesForecastRecordService.save(forecastRecords);
		}
		addMessage(redirectAttributes, "审批操作成功！");
        return "redirect:"+Global.getAdminPath()+"/amazoninfo/salesForecastRecord/?repage";
	}
	
	@RequestMapping(value ="ajaxSave")
	@ResponseBody
	public String ajaxSave(String flag, SalesForecastRecord salesForecastRecord, Integer quantityForecast, Model model, RedirectAttributes redirectAttributes) {
		if(salesForecastRecord.getId()!=null && quantityForecast != null && StringUtils.isNotEmpty(flag)){
			if ("1".equals(flag)) {
				salesForecastRecord.setForecast1(quantityForecast);
			} else if("2".equals(flag)){
				salesForecastRecord.setForecast2(quantityForecast);
			} else if("3".equals(flag)){
				salesForecastRecord.setForecast3(quantityForecast);
			} else if("4".equals(flag)){
				salesForecastRecord.setForecast4(quantityForecast);
			} else if("5".equals(flag)){
				salesForecastRecord.setForecast5(quantityForecast);
			} else if("6".equals(flag)){
				salesForecastRecord.setForecast6(quantityForecast);
			}
			salesForecastRecord.setCreateBy(UserUtils.getUser());
			salesForecastRecordService.save(salesForecastRecord);
			return salesForecastRecord.getId()+"";
		}
		return "false";
	}
	
}
