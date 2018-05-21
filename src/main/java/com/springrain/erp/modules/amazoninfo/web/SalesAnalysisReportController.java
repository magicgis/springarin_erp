package com.springrain.erp.modules.amazoninfo.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseGoal;
import com.springrain.erp.modules.amazoninfo.entity.SaleProfit;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseGoalService;
import com.springrain.erp.modules.amazoninfo.service.SaleAnalysisReportService;
import com.springrain.erp.modules.amazoninfo.service.SaleProfitService;
import com.springrain.erp.modules.psi.entity.ForecastOrder;
import com.springrain.erp.modules.psi.entity.ForecastOrderItem;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductAttribute;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 运营分析报告Controller
 */
@SuppressWarnings("all")
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/salesAnalysisReport")
public class SalesAnalysisReportController extends BaseController {

	@Autowired
	private SaleAnalysisReportService saleAnalysisReportService;
	
	@Autowired
	private PsiProductGroupUserService  psiProductGroupUserService;
	
	@Autowired
	private PsiProductTypeGroupDictService groupDictService;
	
	@Autowired
	private EnterpriseGoalService enterpriseGoalService;
	
	@Autowired
	private SaleProfitService saleProfitService;
	
	private static Map<String, String> typeMap = null;

	/**
	 * 销售额报表分析
	 * @param typeFlag 分析报表分类标记
	 * @return
	 */
	@RequestMapping(value = {"list", ""})
	public String list(SaleProfit saleProfit, String typeFlag,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if (StringUtils.isEmpty(typeFlag)) {
			typeFlag = "t1";
		}
		model.addAttribute("typeFlag", typeFlag);
		model.addAttribute("typeMap", getTypeMap());
		model.addAttribute("currencySymbol", "€");

		String userId = "";	//空字符串返回所有品线
		if(!UserUtils.hasPermission("amazoninfo:analysisReport:all")){
			userId = UserUtils.getUser().getId();	//没有权限的按产品线权限划分
		}
		//查询负责的品线
		List<String> lines = groupDictService.getSalesLines(userId);
		if (lines == null || lines.size() == 0) {
			model.addAttribute("message", "没有产品线查看权限");
			return "modules/amazoninfo/sales/amazonSalesAnalysisReport";//没有权限
		}
		model.addAttribute("lines", lines);
		List<PsiProductTypeGroupDict> lineList = groupDictService.getAllList();
		Map<String, String> lineMap = Maps.newHashMap(); //产品线id&name关系
		for (PsiProductTypeGroupDict psiProductTypeGroupDict : lineList) {
			lineMap.put(psiProductTypeGroupDict.getName().split(" ")[0], psiProductTypeGroupDict.getId());
		}
		model.addAttribute("lineMap", lineMap);
		if (StringUtils.isEmpty(saleProfit.getLine())) {
			saleProfit.setLine(lines.get(0));
		}
		if (StringUtils.isEmpty(saleProfit.getCountry())) {
			saleProfit.setCountry("noUs");	//默认统计非US市场
		}
		if ("t1".equals(typeFlag)) {
			salesData(saleProfit,lineMap, model);
			return "modules/amazoninfo/sales/amazonSalesAnalysisReport";
		} else if ("t2".equals(typeFlag)) {
			typeSalesData(saleProfit, lineMap, model);
			return "modules/amazoninfo/sales/amazonTypeSalesAnalysisReport";
		} else if ("t3".equals(typeFlag)) {
			productSalesData(saleProfit, lineMap, model);
			return "modules/amazoninfo/sales/amazonProductSalesAnalysisReport";
		} else if ("t4".equals(typeFlag)) {
			productPositionData(saleProfit, lineMap, model);
			return "modules/amazoninfo/sales/amazonProductPositionAnalysisReport";
		} else if ("t5".equals(typeFlag)) {//市场营销
			marketingSalesData(saleProfit,lineMap,model);
			return "modules/amazoninfo/sales/amazonMarketingSalesAnalysisReport";
		} else if ("t6".equals(typeFlag)) {
			profitsData(saleProfit, lineMap, model);
			return "modules/amazoninfo/sales/amazonProfitsAnalysisReport";
		} else if ("t7".equals(typeFlag)) {//新品售后报告
			newAfterSalesData(saleProfit,lineMap,model);
			return "modules/amazoninfo/sales/amazonNewAfterSaleAnalysisReport";
		} else if ("t8".equals(typeFlag)) {
			
		}
		return "modules/amazoninfo/sales/amazonSalesAnalysisReport";
	}
	
	//整体销售额报告
	public void salesData(SaleProfit saleProfit, Map<String, String> lineMap, Model model) throws ParseException{
		DateFormat formatMonth = new SimpleDateFormat("yyyyMM");
		Date currDate = null;
		if (StringUtils.isNotEmpty(saleProfit.getDay())) {//yyyyMM
			currDate = formatMonth.parse(saleProfit.getDay());
		} else {
			currDate = DateUtils.addMonths(new Date(), -1);
		}
		model.addAttribute("currDate", currDate);
		List<String> monthList = Lists.newArrayList();
		String currMonth = formatMonth.format(currDate); //当前月
		String lastMonth = formatMonth.format(DateUtils.addMonths(currDate, -1)); //上月(环比)
		String lastYearMonth = formatMonth.format(DateUtils.addMonths(currDate, -12)); //去年(同比)
		monthList.add(currMonth);
		monthList.add(lastMonth);
		monthList.add(lastYearMonth);
		Map<String, SaleProfit> data = Maps.newHashMap();
		List<SaleProfit> list = saleAnalysisReportService.getSalesDataList(saleProfit, null, null, monthList);
		for (SaleProfit profit : list) {
			data.put(profit.getDay(), profit);
		}
		model.addAttribute("currMonth", currMonth);
		model.addAttribute("lastMonth", lastMonth);
		model.addAttribute("lastYearMonth", lastYearMonth);
		model.addAttribute("data", data);
		//目标
		EnterpriseGoal enterpriseGoal = new EnterpriseGoal();
		enterpriseGoal.setStartMonth(currDate);
		enterpriseGoal.setEndMonth(currDate);
		if (!"total".equals(saleProfit.getCountry())) {
			enterpriseGoal.setCountry(saleProfit.getCountry());
		}
		Map<String,Map<String,Float>> goalMap = enterpriseGoalService.findMonthLineGoal(enterpriseGoal);
		if (goalMap != null && goalMap.size() > 0) {
			model.addAttribute("goal", goalMap.get(currMonth).get(lineMap.get(saleProfit.getLine())));
		}
		if ("noUs".equals(saleProfit.getCountry()) || "total".equals(saleProfit.getCountry()) || "eu".equals(saleProfit.getCountry())
				 || "en".equals(saleProfit.getCountry()) || "nonEn".equals(saleProfit.getCountry())) {
			//分国家数据[month[country profit]]
			Map<String, Map<String, SaleProfit>> countryData = Maps.newHashMap();
			List<SaleProfit> countryList = saleAnalysisReportService.getSalesDataList(saleProfit, null, "1", monthList);
			for (SaleProfit profit : countryList) {
				String month = profit.getDay();
				Map<String, SaleProfit> map = countryData.get(month);
				if (map == null) {
					map = Maps.newHashMap();
					countryData.put(month, map);
				}
				map.put(profit.getCountry(), profit);
			}
			model.addAttribute("countryData", countryData);
			//分产品线目标 [月份 [产品线[国家   目标]]]
			Map<String, EnterpriseGoal> countryGoal = enterpriseGoalService.findCountryLineGoal(currMonth).get(saleProfit.getLine());
			model.addAttribute("countryGoal", countryGoal);
			model.addAttribute("countryDataXAxis", countryData.get(currMonth).keySet());
		}
		SaleProfit searchProfit = new SaleProfit();
		searchProfit.setCountry(saleProfit.getCountry());
		searchProfit.setDay(formatMonth.format(DateUtils.addMonths(currDate, -24)));
		searchProfit.setEnd(currMonth);
		searchProfit.setLine(saleProfit.getLine());
		List<SaleProfit> saleProfitList = saleProfitService.getSalesProfitList(searchProfit, "1", "2").get("1");
		//产品线分月度数据[month profit]
		Map<String, SaleProfit> lineMonthData = Maps.newHashMap();
		for (SaleProfit profit : saleProfitList) {
			lineMonthData.put(profit.getDay(), profit);
		}
		model.addAttribute("lineMonthData", lineMonthData);
		List<String> monthXAxis = Lists.newArrayList();	//月度X轴
		String currYear = currMonth.substring(0, 4);
		Date firstMonth = formatMonth.parse(currYear+"01");
		for (int i = 0; i < 12; i++) {
			int month = firstMonth.getMonth()+1;
			monthXAxis.add(month+"");
			if (month == (currDate.getMonth()+1)) {
				break;
			}
			firstMonth = DateUtils.addMonths(firstMonth, 1);
		}
		model.addAttribute("monthXAxis", monthXAxis);
		model.addAttribute("currYear", currYear);
		model.addAttribute("lastYear", lastYearMonth.substring(0, 4));
		model.addAttribute("saleProfit", saleProfit);
	}
	
	//分品类销售额报告
	public void typeSalesData(SaleProfit saleProfit, Map<String, String> lineMap, Model model) throws ParseException{
		DateFormat formatMonth = new SimpleDateFormat("yyyyMM");
		Date currDate = null;
		if (StringUtils.isNotEmpty(saleProfit.getDay())) {//yyyyMM
			currDate = formatMonth.parse(saleProfit.getDay());
		} else {
			currDate = DateUtils.addMonths(new Date(), -1);
		}
		model.addAttribute("currDate", currDate);
		List<String> monthList = Lists.newArrayList();
		String currMonth = formatMonth.format(currDate); //当前月
		String lastMonth = formatMonth.format(DateUtils.addMonths(currDate, -1)); //上月(环比)
		String lastYearMonth = formatMonth.format(DateUtils.addMonths(currDate, -12)); //去年(同比)
		monthList.add(currMonth);
		monthList.add(lastMonth);
		monthList.add(lastYearMonth);
		model.addAttribute("currMonth", currMonth);
		model.addAttribute("lastMonth", lastMonth);
		model.addAttribute("lastYearMonth", lastYearMonth);
		
		//分品类数据[month[type profit]]
		Map<String, Map<String, SaleProfit>> typeData = Maps.newHashMap();
		List<SaleProfit> countryList = saleAnalysisReportService.getSalesDataList(saleProfit, "2", null, monthList);
		for (SaleProfit profit : countryList) {
			String month = profit.getDay();
			Map<String, SaleProfit> map = typeData.get(month);
			if (map == null) {
				map = Maps.newHashMap();
				typeData.put(month, map);
			}
			map.put(profit.getType(), profit);
		}
		model.addAttribute("typeData", typeData);
		model.addAttribute("typeDataXAxis", typeData.get(currMonth).keySet());
		model.addAttribute("saleProfit", saleProfit);
	}
	
	//分产品销售额报告
	public void productSalesData(SaleProfit saleProfit, Map<String, String> lineMap, Model model) throws ParseException{
		DateFormat formatMonth = new SimpleDateFormat("yyyyMM");
		Date currDate = null;
		if (StringUtils.isNotEmpty(saleProfit.getDay())) {//yyyyMM
			currDate = formatMonth.parse(saleProfit.getDay());
		} else {
			currDate = DateUtils.addMonths(new Date(), -1);
		}
		model.addAttribute("currDate", currDate);
		List<String> monthList = Lists.newArrayList();
		String currMonth = formatMonth.format(currDate); //当前月
		String lastMonth = formatMonth.format(DateUtils.addMonths(currDate, -1)); //上月(环比)
		String lastYearMonth = formatMonth.format(DateUtils.addMonths(currDate, -12)); //去年(同比)
		monthList.add(currMonth);
		monthList.add(lastMonth);
		monthList.add(lastYearMonth);
		model.addAttribute("currMonth", currMonth);
		model.addAttribute("lastMonth", lastMonth);
		model.addAttribute("lastYearMonth", lastYearMonth);
		
		//分产品销售额数据[month[product profit]]
		Map<String, Map<String, SaleProfit>> typeData = Maps.newLinkedHashMap();
		List<SaleProfit> countryList = saleAnalysisReportService.getSalesDataList(saleProfit, "1", null, monthList);
		Map<String, SaleProfit> totalData = Maps.newLinkedHashMap();
		for (SaleProfit profit : countryList) {
			if (profit.getSales()==0) {
				continue;
			}
			String month = profit.getDay();
			SaleProfit totalProfit = totalData.get(month);
			if (totalProfit == null) {
				totalProfit = new SaleProfit();
				totalProfit.setSales(0f);
				totalData.put(month, totalProfit);
			}
			totalProfit.setSales(totalProfit.getSales() + profit.getSales());
			Map<String, SaleProfit> map = typeData.get(month);
			if (map == null) {
				map = Maps.newLinkedHashMap();
				typeData.put(month, map);
			}
			map.put(profit.getProductName(), profit);
		}
		model.addAttribute("totalData", totalData);
		model.addAttribute("typeData", typeData);
		model.addAttribute("typeDataXAxis", typeData.get(currMonth).keySet());
		//计算同比、环比销售额差(前台图表展示上涨、下降前10的产品)
		List<SaleProfit> tbList = Lists.newArrayList();
		List<SaleProfit> hbList = Lists.newArrayList();
		for (String productName : typeData.get(currMonth).keySet()) {
			SaleProfit currSaleProfit = typeData.get(currMonth).get(productName);
			//同比
			SaleProfit tbSaleProfit = new SaleProfit();
			tbSaleProfit.setProductName(productName);
			tbSaleProfit.setSales(currSaleProfit.getSales());
			if (typeData.get(lastYearMonth).get(productName)!=null) {
				SaleProfit lastYearSaleProfit = typeData.get(lastYearMonth).get(productName);
				tbSaleProfit.setSales(currSaleProfit.getSales()-lastYearSaleProfit.getSales());	//同比增长销售额
			}
			tbList.add(tbSaleProfit);
			//环比
			SaleProfit hbSaleProfit = new SaleProfit();
			hbSaleProfit.setProductName(productName);
			hbSaleProfit.setSales(currSaleProfit.getSales());
			if (typeData.get(lastMonth).get(productName)!=null) {
				SaleProfit lastMonthSaleProfit = typeData.get(lastMonth).get(productName);
				hbSaleProfit.setSales(currSaleProfit.getSales()-lastMonthSaleProfit.getSales()); //环比增长销售额
			}
			hbList.add(hbSaleProfit);
		}
		Collections.sort(tbList, new Comparator<SaleProfit>(){
            public int compare(SaleProfit arg0, SaleProfit arg1) {
                return arg0.getSales().compareTo(arg1.getSales());
            }
        });
		Collections.sort(hbList, new Comparator<SaleProfit>(){
            public int compare(SaleProfit arg0, SaleProfit arg1) {
                return arg0.getSales().compareTo(arg1.getSales());
            }
        });
		model.addAttribute("tbList", tbList);
		model.addAttribute("hbList", hbList);
		model.addAttribute("saleProfit", saleProfit);
	}
	
	//利润报告
	public void profitsData(SaleProfit saleProfit, Map<String, String> lineMap, Model model) throws ParseException{
		DateFormat formatMonth = new SimpleDateFormat("yyyyMM");
		Date currDate = null;
		if (StringUtils.isNotEmpty(saleProfit.getDay())) {//yyyyMM
			currDate = formatMonth.parse(saleProfit.getDay());
		} else {
			currDate = DateUtils.addMonths(new Date(), -1);
		}
		model.addAttribute("currDate", currDate);
		List<String> monthList = Lists.newArrayList();
		String currMonth = formatMonth.format(currDate); //当前月
		String lastMonth = formatMonth.format(DateUtils.addMonths(currDate, -1)); //上月(环比)
		String lastYearMonth = formatMonth.format(DateUtils.addMonths(currDate, -12)); //去年(同比)
		monthList.add(currMonth);
		monthList.add(lastMonth);
		monthList.add(lastYearMonth);
		Map<String, SaleProfit> data = Maps.newHashMap();
		List<SaleProfit> list = saleAnalysisReportService.getSalesDataList(saleProfit, null, null, monthList);
		for (SaleProfit profit : list) {
			data.put(profit.getDay(), profit);
		}
		model.addAttribute("currMonth", currMonth);
		model.addAttribute("lastMonth", lastMonth);
		model.addAttribute("lastYearMonth", lastYearMonth);
		model.addAttribute("data", data);
		
		if ("noUs".equals(saleProfit.getCountry()) || "total".equals(saleProfit.getCountry()) || "eu".equals(saleProfit.getCountry())
				 || "en".equals(saleProfit.getCountry()) || "nonEn".equals(saleProfit.getCountry())) {
			//分国家数据[month[country profit]]
			Map<String, Map<String, SaleProfit>> countryData = Maps.newHashMap();
			List<SaleProfit> countryList = saleAnalysisReportService.getSalesDataList(saleProfit, null, "1", monthList);
			for (SaleProfit profit : countryList) {
				String month = profit.getDay();
				Map<String, SaleProfit> map = countryData.get(month);
				if (map == null) {
					map = Maps.newHashMap();
					countryData.put(month, map);
				}
				map.put(profit.getCountry(), profit);
			}
			model.addAttribute("countryData", countryData);
			//分产品线目标 [月份 [产品线[国家   目标]]]
			Map<String, EnterpriseGoal> countryGoal = enterpriseGoalService.findCountryLineGoal(currMonth).get(saleProfit.getLine());
			model.addAttribute("countryGoal", countryGoal);
			model.addAttribute("countryDataXAxis", countryData.get(currMonth).keySet());
		}
		//分品类数据[month[type profit]]
		Map<String, Map<String, SaleProfit>> typeData = Maps.newHashMap();
		List<SaleProfit> countryList = saleAnalysisReportService.getSalesDataList(saleProfit, "2", null, monthList);
		for (SaleProfit profit : countryList) {
			String month = profit.getDay();
			Map<String, SaleProfit> map = typeData.get(month);
			if (map == null) {
				map = Maps.newHashMap();
				typeData.put(month, map);
			}
			map.put(profit.getType(), profit);
		}
		model.addAttribute("typeData", typeData);
		model.addAttribute("typeDataXAxis", typeData.get(currMonth).keySet());
		//分产品数据[month[product profit]]
		Map<String, Map<String, SaleProfit>> productData = Maps.newLinkedHashMap();
		List<SaleProfit> profitsList = saleAnalysisReportService.getSalesDataList(saleProfit, "1", null, monthList);
		Map<String, SaleProfit> totalData = Maps.newLinkedHashMap();
		for (SaleProfit profit : profitsList) {
			if (profit.getProfits()==0) {
				continue;
			}
			String month = profit.getDay();
			SaleProfit totalProfit = totalData.get(month);
			if (totalProfit == null) {
				totalProfit = new SaleProfit();
				totalProfit.setSales(0f);
				totalProfit.setProfits(0f);
				totalProfit.setSalesNoTax(0f);
				totalProfit.setTariff(0f);
				totalProfit.setBuyCost(0f);
				totalProfit.setAmazonFee(0f);
				totalProfit.setTransportFee(0f);
				totalProfit.setAdAmsFee(0f);
				totalProfit.setAdInEventFee(0f);
				totalProfit.setAdInProfitFee(0f);
				totalProfit.setSupportAmazonFee(0f);
				totalProfit.setSupportCost(0f);
				totalProfit.setRefund(0f);
				totalProfit.setRecallCost(0f);
				totalProfit.setRecallFee(0f);
				totalProfit.setStorageFee(0f);
				totalProfit.setLongStorageFee(0f);
				totalData.put(month, totalProfit);
			}
			//统计费用汇总
			addFee(totalProfit, profit);
			Map<String, SaleProfit> map = productData.get(month);
			if (map == null) {
				map = Maps.newLinkedHashMap();
				productData.put(month, map);
			}
			map.put(profit.getProductName(), profit);
		}
		model.addAttribute("totalData", totalData);
		model.addAttribute("productData", productData);
		model.addAttribute("productDataXAxis", productData.get(currMonth).keySet());
		//计算同比、环比销售额差(前台图表展示上涨、下降前10的产品)
		List<SaleProfit> tbList = Lists.newArrayList();
		List<SaleProfit> hbList = Lists.newArrayList();
		for (String productName : productData.get(currMonth).keySet()) {
			SaleProfit currSaleProfit = productData.get(currMonth).get(productName);
			//同比
			SaleProfit tbSaleProfit = new SaleProfit();
			tbSaleProfit.setProductName(productName);
			tbSaleProfit.setProfits(currSaleProfit.getProfits());
			if (productData.get(lastYearMonth).get(productName)!=null) {
				SaleProfit lastYearSaleProfit = productData.get(lastYearMonth).get(productName);
				tbSaleProfit.setProfits(currSaleProfit.getProfits()-lastYearSaleProfit.getProfits());	//同比增长
			}
			tbList.add(tbSaleProfit);
			//环比
			SaleProfit hbSaleProfit = new SaleProfit();
			hbSaleProfit.setProductName(productName);
			hbSaleProfit.setProfits(currSaleProfit.getProfits());
			if (productData.get(lastMonth).get(productName)!=null) {
				SaleProfit lastMonthSaleProfit = productData.get(lastMonth).get(productName);
				hbSaleProfit.setProfits(currSaleProfit.getProfits()-lastMonthSaleProfit.getProfits()); //环比增长
			}
			hbList.add(hbSaleProfit);
		}
		Collections.sort(tbList, new Comparator<SaleProfit>(){
            public int compare(SaleProfit arg0, SaleProfit arg1) {
                return arg0.getProfits().compareTo(arg1.getProfits());
            }
        });
		Collections.sort(hbList, new Comparator<SaleProfit>(){
            public int compare(SaleProfit arg0, SaleProfit arg1) {
                return arg0.getProfits().compareTo(arg1.getProfits());
            }
        });
		model.addAttribute("tbList", tbList);
		model.addAttribute("hbList", hbList);
		//新品集合
		List<String> newProductList = saleAnalysisReportService.findNewProduct(currMonth, saleProfit);
		model.addAttribute("newDataXAxis", newProductList);
		model.addAttribute("saleProfit", saleProfit);
	}
	
	//产品结构报告
	public void productPositionData(SaleProfit saleProfit, Map<String, String> lineMap, Model model) throws ParseException{
		DateFormat formatMonth = new SimpleDateFormat("yyyyMM");
		Date currDate = null;
		if (StringUtils.isNotEmpty(saleProfit.getDay())) {//yyyyMM
			currDate = formatMonth.parse(saleProfit.getDay());
		} else {
			currDate = DateUtils.addMonths(new Date(), -1);
		}
		model.addAttribute("currDate", currDate);
		List<String> monthList = Lists.newArrayList();
		String currMonth = formatMonth.format(currDate); //当前月
		String lastMonth = formatMonth.format(DateUtils.addMonths(currDate, -1)); //上月(环比)
		String lastYearMonth = formatMonth.format(DateUtils.addMonths(currDate, -12)); //去年(同比)
		monthList.add(currMonth);
		monthList.add(lastMonth);
		monthList.add(lastYearMonth);
		model.addAttribute("currMonth", currMonth);
		model.addAttribute("lastMonth", lastMonth);
		model.addAttribute("lastYearMonth", lastYearMonth);
		
		//分产品销售额数据[month[position [productName profit]]] //新品、爆款、利润款、主力、淘汰、other
		Map<String, Map<String, Map<String, SaleProfit>>> positionData = Maps.newLinkedHashMap();
		List<SaleProfit> rsList = saleAnalysisReportService.getSalesDataList(saleProfit, "1", null, monthList);
		Map<String, Map<String, SaleProfit>> totalData = Maps.newLinkedHashMap();
		Map<String, Map<String, SaleProfit>> typeTotalData = Maps.newLinkedHashMap();
		Map<String, Map<String, SaleProfit>> tableData = Maps.newLinkedHashMap();
		for (SaleProfit profit : rsList) {
			if (profit.getSales()==0) {
				continue;
			}
			String month = profit.getDay();
			Map<String, SaleProfit> tableMap = tableData.get(month);
			if (tableMap == null) {
				tableMap = Maps.newHashMap();
				tableData.put(month, tableMap);
			}
			tableMap.put(profit.getProductName(), profit);
			String position = profit.getProductAttr();
			if (!"新品,爆款,利润款,主力,淘汰".contains(position)) {
				position = "other";	//未设置产品定位的用other标记
			}
			Map<String, SaleProfit> monthTotal = totalData.get(month);
			if (monthTotal == null) {
				monthTotal = Maps.newHashMap();
				totalData.put(month, monthTotal);
			}
			SaleProfit totalProfit = monthTotal.get(position);
			if (totalProfit == null) {
				totalProfit = new SaleProfit();
				totalProfit.setSales(0f);
				monthTotal.put(position, totalProfit);
			}
			totalProfit.setSales(totalProfit.getSales() + profit.getSales());
			//分新品、淘汰、老品统计
			String type = profit.getProductAttr();
			if (!"新品,淘汰".contains(type)) {
				type = "老品";
			}
			Map<String, SaleProfit> typeMonthTotal = typeTotalData.get(month);
			if (typeMonthTotal == null) {
				typeMonthTotal = Maps.newHashMap();
				typeTotalData.put(month, typeMonthTotal);
			}
			SaleProfit typeTotalProfit = typeMonthTotal.get(type);
			if (typeTotalProfit == null) {
				typeTotalProfit = new SaleProfit();
				typeTotalProfit.setSales(0f);
				typeMonthTotal.put(type, typeTotalProfit);
			}
			typeTotalProfit.setSales(typeTotalProfit.getSales() + profit.getSales());
			
			Map<String, Map<String, SaleProfit>> map = positionData.get(month);
			if (map == null) {
				map = Maps.newLinkedHashMap();
				positionData.put(month, map);
			}
			Map<String, SaleProfit> productMap = map.get(position);
			if (productMap == null) {
				productMap = Maps.newLinkedHashMap();
				map.put(position, productMap);
			}
			productMap.put(profit.getProductName(), profit);
		}
		model.addAttribute("totalData", totalData);
		model.addAttribute("tableData", tableData);
		model.addAttribute("typeTotalData", typeTotalData);
		model.addAttribute("positionData", positionData);
		model.addAttribute("positionDataXAxis", positionData.get(currMonth).keySet());	//Lists.newArrayList("新品","爆款","利润款","主力","淘汰","other")
		model.addAttribute("typeDataXAxis", Lists.newArrayList("新品","淘汰","老品"));
		model.addAttribute("newXAxis", positionData.get(currMonth).get("新品")==null?Lists.newArrayList():positionData.get(currMonth).get("新品").keySet());
		model.addAttribute("hotXAxis", positionData.get(currMonth).get("爆款")==null?Lists.newArrayList():positionData.get(currMonth).get("爆款").keySet());
		model.addAttribute("eliminatedXAxis", positionData.get(currMonth).get("淘汰")==null?Lists.newArrayList():positionData.get(currMonth).get("淘汰").keySet());
		model.addAttribute("saleProfit", saleProfit);
	}
	
	private void addFee(SaleProfit totalProfit, SaleProfit profit) {
		totalProfit.setSales(totalProfit.getSales() + profit.getSales());
		totalProfit.setProfits(totalProfit.getProfits() + profit.getProfits());
		totalProfit.setSalesNoTax(totalProfit.getSalesNoTax() + profit.getSalesNoTax());
		totalProfit.setTariff(totalProfit.getTariff() + profit.getTariff());
		totalProfit.setBuyCost(totalProfit.getBuyCost() + profit.getBuyCost());
		totalProfit.setAmazonFee(totalProfit.getAmazonFee() + profit.getAmazonFee());
		totalProfit.setTransportFee(totalProfit.getTransportFee() + profit.getTransportFee());
		totalProfit.setAdAmsFee(totalProfit.getAdAmsFee() + profit.getAdAmsFee());
		totalProfit.setAdInEventFee(totalProfit.getAdInEventFee() + profit.getAdInEventFee());
		totalProfit.setAdInProfitFee(totalProfit.getAdInProfitFee() + profit.getAdInProfitFee());
		totalProfit.setSupportAmazonFee(totalProfit.getSupportAmazonFee() + profit.getSupportAmazonFee());
		totalProfit.setSupportCost(totalProfit.getSupportCost() + profit.getSupportCost());
		totalProfit.setRefund(totalProfit.getRefund() + profit.getRefund());
		totalProfit.setRecallCost(totalProfit.getRecallCost() + profit.getRecallCost());
		totalProfit.setRecallFee(totalProfit.getRecallFee() + profit.getRecallFee());
		totalProfit.setStorageFee(totalProfit.getStorageFee() + profit.getStorageFee());
		totalProfit.setLongStorageFee(totalProfit.getLongStorageFee() + profit.getLongStorageFee());
	}

	public void marketingSalesData(SaleProfit saleProfit,Map<String, String> lineMap, Model model) throws ParseException{
		DateFormat formatMonth = new SimpleDateFormat("yyyyMM");
		Date startDate = null;
		Date lastDate = null;
		if (StringUtils.isNotEmpty(saleProfit.getEnd())) {//yyyyMM
			startDate = formatMonth.parse(saleProfit.getDay());
			lastDate = formatMonth.parse(saleProfit.getEnd());
		} else {
			Date date=DateUtils.getDateStart(new Date());
			startDate = DateUtils.addMonths(date, -5);
			lastDate = date;
		}
		model.addAttribute("startDate", startDate);
		model.addAttribute("lastDate", lastDate);
		Map<String,SaleReport> marketingMap=saleAnalysisReportService.findMarketingQuantity(saleProfit,lineMap,startDate,lastDate);
		model.addAttribute("marketingMap", marketingMap);
		Map<Integer,Integer> marketingTypeMap=Maps.newHashMap();
		List<String> legendList = Lists.newArrayList("自然","广告","页面折扣","闪促","替代","评测","免费");
		model.addAttribute("legendList",legendList);
		for (Map.Entry<String,SaleReport> entry: marketingMap.entrySet()) {
			SaleReport report = entry.getValue();
			
			Integer realOrder = 0;
			if(marketingTypeMap.get(1)!=null){
				realOrder = marketingTypeMap.get(1);
			}
			marketingTypeMap.put(1, report.getRealOrder() + realOrder);
			
			Integer adsOrder = 0;
			if(marketingTypeMap.get(2)!=null){
				adsOrder = marketingTypeMap.get(2);
			}
			marketingTypeMap.put(2, report.getAdsOrder() + adsOrder);
			
			
			Integer promotions = 0;
			if(marketingTypeMap.get(3)!=null){
				promotions = marketingTypeMap.get(3);
			}
			marketingTypeMap.put(3, report.getPromotionsOrder() + promotions);
			
			
			Integer flash = 0;
			if(marketingTypeMap.get(4)!=null){
				flash = marketingTypeMap.get(4);
			}
			marketingTypeMap.put(4, report.getFlashSalesOrder() + flash);
			
			
			Integer support = 0;
			if(marketingTypeMap.get(5)!=null){
				support = marketingTypeMap.get(5);
			}
			marketingTypeMap.put(5, report.getSupportVolume() + support);
			
			
			Integer review = 0;
			if(marketingTypeMap.get(6)!=null){
				review = marketingTypeMap.get(6);
			}
			marketingTypeMap.put(6, report.getReviewVolume() + review);
			
			
			Integer free = 0;
			if(marketingTypeMap.get(7)!=null){
				free = marketingTypeMap.get(7);
			}
			marketingTypeMap.put(7, report.getFreeOrder() + free);
		}
		model.addAttribute("marketingTypeMap", marketingTypeMap);
		
		Map<String,Map<String,Integer>> lightDealsMap=saleAnalysisReportService.find(saleProfit,lineMap,startDate,lastDate);
		model.addAttribute("lightDealsMap", lightDealsMap);
		
		Map<String,Map<String,Object[]>> adsMap=saleAnalysisReportService.findAdsDate(saleProfit,lineMap,startDate,lastDate);//月-产品
		model.addAttribute("adsMap", adsMap);
		Map<String,Map<String,Object>> adsTypeMap= Maps.newLinkedHashMap();
		for (Map.Entry<String,Map<String,Object[]>> entry : adsMap.entrySet()) {
			   String  month = entry.getKey();
			   Map<String,Object[]>  nameMap = entry.getValue();
			   
			   Map<String,Object> adsType = adsTypeMap.get(month);
			   if(adsType == null){
				   adsType = Maps.newLinkedHashMap();
				   adsTypeMap.put(month, adsType);
			   }
			   adsType.put("1", nameMap.keySet());
					   
			  
			   for (Map.Entry<String,Object[]> dtMap: nameMap.entrySet()) {
				   Object[] obj = dtMap.getValue();
				   String name = dtMap.getKey();
				   String ams_flag = obj[2].toString();
				   String spa_flag = obj[3].toString();
				   //投入SPA广告，未投AMS
				   if("0".equals(spa_flag)&&"1".equals(ams_flag)){
					   Set<String> nameSet = (Set<String>) adsType.get("2");
					   if(nameSet==null){
						   nameSet = Sets.newHashSet();
						   adsType.put("2", nameSet);
					   }
					   nameSet.add(name);
				   }
				   
				 //投入Ams广告，未投spa
				   if("1".equals(spa_flag)&&"0".equals(ams_flag)){
					   Set<String> nameSet = (Set<String>) adsType.get("3");
					   if(nameSet==null){
						   nameSet = Sets.newHashSet();
						   adsType.put("3", nameSet);
					   }
					   nameSet.add(name);
				   }
				   
				   if("0".equals(spa_flag)&&"0".equals(ams_flag)){
					   Set<String> nameSet = (Set<String>) adsType.get("4");
					   if(nameSet==null){
						   nameSet = Sets.newHashSet();
						   adsType.put("4", nameSet);
					   }
					   nameSet.add(name);
				   }
			   }
		} 
		model.addAttribute("adsTypeMap", adsTypeMap);
		
		Map<String,Map<Integer,Integer>>  rankMap=saleAnalysisReportService.findRankDate(saleProfit,lineMap,startDate,lastDate);
		model.addAttribute("rankMap", rankMap);
		
		Map<String,Map<String,Object[]>> bestSellerMap=saleAnalysisReportService.findBestSeller(saleProfit,lineMap,startDate,lastDate);
		model.addAttribute("bestSellerMap", bestSellerMap);
		List<String> countryList= Lists.newArrayList();
		
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
				&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry()) 
				&& !"nonEn".equals(saleProfit.getCountry()) && !"noUs".equals(saleProfit.getCountry())) {
			countryList.add(saleProfit.getCountry());
		} else if ("en".equals(saleProfit.getCountry())) {
			countryList.addAll(Sets.newHashSet("com","uk","ca"));
		} else if ("eu".equals(saleProfit.getCountry())) {
			countryList.addAll(Sets.newHashSet("de","uk","fr","it","es"));
		} else if ("nonEn".equals(saleProfit.getCountry())) {	//非英语国家
			countryList.addAll(Sets.newHashSet("de","fr","it","es","jp"));
		} else if ("noUs".equals(saleProfit.getCountry())) {	//非美国
			countryList.addAll(Sets.newHashSet("de","uk","fr","it","es","jp","ca","mx"));
		} else{
			countryList.addAll(Sets.newHashSet("de","uk","fr","it","es","jp","ca","mx","com"));
		}
		model.addAttribute("countryList", countryList);
	}
	
	
	public void newAfterSalesData(SaleProfit saleProfit,Map<String, String> lineMap, Model model) throws ParseException{
		DateFormat formatMonth = new SimpleDateFormat("yyyyMM");
		Date startDate = null;
		Date lastDate = null;
		if (StringUtils.isNotEmpty(saleProfit.getEnd())) {//yyyyMM
			startDate = formatMonth.parse(saleProfit.getDay());
			lastDate = formatMonth.parse(saleProfit.getEnd());
		} else {
			Date date=DateUtils.getDateStart(new Date());
			startDate = DateUtils.addMonths(date, -5);
			lastDate = date;
		}
		model.addAttribute("startDate", startDate);
		model.addAttribute("lastDate", lastDate);
	
		 Map<String,Map<String,Map<String,Object[]>>>  starMap=saleAnalysisReportService.findAfterSales(saleProfit,lineMap,startDate,lastDate);
		model.addAttribute("starMap", starMap);
		
        List<String> countryList= Lists.newArrayList();
		
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
				&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry()) 
				&& !"nonEn".equals(saleProfit.getCountry()) && !"noUs".equals(saleProfit.getCountry())) {
			countryList.add(saleProfit.getCountry());
		} else if ("en".equals(saleProfit.getCountry())) {
			countryList.addAll(Sets.newHashSet("com","uk","ca"));
		} else if ("eu".equals(saleProfit.getCountry())) {
			countryList.addAll(Sets.newHashSet("de","uk","fr","it","es"));
		} else if ("nonEn".equals(saleProfit.getCountry())) {	//非英语国家
			countryList.addAll(Sets.newHashSet("de","fr","it","es","jp"));
		} else if ("noUs".equals(saleProfit.getCountry())) {	//非美国
			countryList.addAll(Sets.newHashSet("de","uk","fr","it","es","jp","ca","mx"));
		}else{
			countryList.addAll(Sets.newHashSet("de","uk","fr","it","es","jp","ca","mx","com"));
		}
		model.addAttribute("countryList", countryList);
	}
			
	private static Map<String, String> getTypeMap() {
		if (typeMap == null) {
			typeMap = Maps.newLinkedHashMap();
			typeMap.put("t1", "整体销售额报告");
			typeMap.put("t2", "分品类销售额报告");
			typeMap.put("t3", "分产品销售额报告");
			typeMap.put("t4", "产品结构报告");
			typeMap.put("t5", "市场营销报告");
			typeMap.put("t6", "利润报告");
			typeMap.put("t7", "新品售后报告");
			//typeMap.put("t8", "市场占有率报告");
		}
		return typeMap;
	}
}
