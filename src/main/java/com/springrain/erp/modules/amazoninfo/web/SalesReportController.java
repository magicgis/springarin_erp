package com.springrain.erp.modules.amazoninfo.web;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseDetail;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseGoal;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseTypeGoal;
import com.springrain.erp.modules.amazoninfo.entity.ProductPrice;
import com.springrain.erp.modules.amazoninfo.entity.SaleProfit;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.entity.SaleReportMonthType;
import com.springrain.erp.modules.amazoninfo.service.AdvertisingService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseGoalService;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseTypeGoalService;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseWeekService;
import com.springrain.erp.modules.amazoninfo.service.ProductPriceService;
import com.springrain.erp.modules.amazoninfo.service.ReturnGoodsService;
import com.springrain.erp.modules.amazoninfo.service.SaleProfitService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportMonthTypeService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 销量统计Controller
 * @author Tim
 * @version 2015-03-03
 */
@SuppressWarnings("all")
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/salesReprots")
public class SalesReportController extends BaseController {
	
	@Autowired
	private SaleReportService saleReportService;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private PsiProductTypeGroupDictService psiTypeGroupService;
	
	@Autowired
	private EnterpriseWeekService enterpriseWeekService;
	
	@Autowired
	private EnterpriseGoalService enterpriseGoalService;

	@Autowired
	private AmazonOrderService amazonOrderService;
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;

	@Autowired
	private AmazonProductService amazonProductService;
	@Autowired
	private AdvertisingService advertisingService;
	@Autowired
	private PsiProductTypeGroupDictService dictService;
	
	@Autowired
	private SaleReportMonthTypeService saleReportMonthTypeService;
	@Autowired
	private ProductPriceService productPriceService;
	
	@Autowired
	private SaleProfitService saleProfitService;
	
	@Autowired
	private EnterpriseTypeGoalService enterpriseTypeGoalService;

	@Autowired
	private ReturnGoodsService returnGoodsService;
	
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	
	private static DateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
	private static DateFormat formatWeek = new SimpleDateFormat("yyyyww");
	private static DateFormat formatMonth = new SimpleDateFormat("yyyyMM");
	private static DateFormat formatDay2 = new SimpleDateFormat("yyyyMMdd HH");
	
	@RequestMapping(value = "lineList")
	public String list(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		//得到当前月目标填报日期的前一天来获取当天汇率
		//String goalDateStr = enterpriseGoalService.findByCurrentMonth();
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String, Map<String, SaleReport>> data = saleReportService.getProductLineSales(saleReport, rateMap);
		model.addAttribute("data", data);
		Map<String,String> allLine=dictService.getProductLine();
		//allLine.put("unGrouped", "UnGrouped");
		model.addAttribute("allLine", allLine);
		
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		List<String> xAxis  = Lists.newArrayList();
		Map<String, String> tip = Maps.newHashMap();
		SimpleDateFormat enFormat=new java.text.SimpleDateFormat("E",Locale.US);
		SimpleDateFormat deFormat=new java.text.SimpleDateFormat("E",Locale.GERMANY);
		String language=LocaleContextHolder.getLocale().getLanguage();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				//tip.put(key,DateUtils.getDate(start,"E"));
				if("zh".equals(language)){
					tip.put(key,DateUtils.getDate(start,"E"));
				}else if("de".equals(language)){
					tip.put(key,deFormat.format(start));
				}else{
					tip.put(key,enFormat.format(start));
				}
				start = DateUtils.addDays(start, 1);
			}
		}
		model.addAttribute("xAxis", xAxis);
		model.addAttribute("tip", tip);
		model.addAttribute("currencySymbol", com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(saleReport.getCurrencyType()));
		
		return "modules/amazoninfo/sales/amazonProductLineSalesReportList";
	}
	
	
	public static Hashtable<String,Map<String,EnterpriseDetail>> realTable=new Hashtable<String,Map<String,EnterpriseDetail>>();
	public static Float realCountryGoal;
	public static Float realMonthCountryGoal;
	public static List<String> realKey=Lists.newArrayList();
	public static Set<String> resetFlag=Sets.newHashSet();
	public static Timestamp lastUpdateTime;
	@RequestMapping(value = {"list", ""})
	public String totalList(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		String currencyType = saleReport.getCurrencyType();
		String productTypeTemp=saleReport.getProductType();
		
		if(StringUtils.isBlank(saleReport.getGroupName())){
			saleReport.setProductType(null);
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isBlank(saleReport.getProductType())){
			List<Dict> dict=psiTypeGroupService.getProductType(saleReport.getGroupName());
			List<String> typeList=new ArrayList<String>();
			for (Dict dict2 : dict) {
				typeList.add("'"+dict2.getValue()+"'");
			}
			if(typeList!=null&&typeList.size()>0){
			    saleReport.setProductType(StringUtils.join(typeList.toArray(),",")); 
			}else{
				saleReport.setProductType("'no'");
			}  
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isNotBlank(saleReport.getProductType())){
			saleReport.setProductType("'"+saleReport.getProductType()+"'");
		}
		
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String, Map<String, SaleReport>>  data = saleReportService.getSales(saleReport, rateMap);
		Map<String,Map<String, SaleReport>>  otherData = saleReportService.getOtherSales(saleReport, rateMap);
		/*if(!"3".equals(saleReport.getSearchType())){
			model.addAttribute("orderNumMap",saleReportService.findOrderNum(saleReport));
		}*/
		
		//预测完成率
	  //  Calendar calendar = Calendar.getInstance();
		//Date endDate= calendar.getTime();
		Date endDate=saleReport.getEnd();
		Date startDate=DateUtils.getFirstDayOfMonth(endDate);
		String formatDate=new SimpleDateFormat("yyyyMM").format(startDate);//欧元版统计预测完成率和目标
	    model.addAttribute("day",new SimpleDateFormat("dd").format(endDate) );
	    model.addAttribute("formatDate", formatDate);
		if ("EUR".equals(currencyType)&&StringUtils.isBlank(saleReport.getGroupName())&&!resetFlag.contains(formatDay2.format(new Date()))) {
			resetFlag.add(formatDay2.format(new Date()));
	        Calendar calendar = Calendar.getInstance();
	        calendar.set(Calendar.HOUR_OF_DAY, 0);
	        calendar.set(Calendar.MINUTE, 0);
	        calendar.set(Calendar.SECOND, 0);
			Date endDate1= calendar.getTime();
			Date startDate1=DateUtils.getFirstDayOfMonth(endDate1);
			Date monthEnd1=DateUtils.getLastDayOfMonth(endDate1);
			Map<String,Float> totalWeight1=enterpriseWeekService.findWeightByMonth(startDate1, monthEnd1);
			String flag1="1";
			if(totalWeight1.size()>0){
				Map<String,Float> realMonthCountryGoal=enterpriseGoalService.findTotalGoalByMonth(new SimpleDateFormat("yyyyMM").format(endDate1), currencyType);
				Map<String,Map<Integer,Float>> countryWeight1= enterpriseWeekService.findByCountryWeight();
				Float totalSales1=0f;
				Map<String, Map<String,Float>> sales1=saleReportService.getSalesBydat(startDate1,endDate1, currencyType, rateMap,"total");//日期-国家-确认销量
				//Map<String,Map<String,EnterpriseDetail>> realList1=new HashMap<String,Map<String,EnterpriseDetail>>();
				Float dayGoal1=0f;
				Float addDayWeight=0f;
			    for (Dict dict : DictUtils.getDictList("platform")) {
						String country1 = dict.getValue();
						if(country1.contains("com")){
							continue;
						}
						if(realMonthCountryGoal.get(country1)==null&&!"mx".equals(country1)&&!country1.contains("com")){
							flag1="0";
							break;
						}
						Float countryMonthGoal1=(realMonthCountryGoal.get(country1)==null?0f:realMonthCountryGoal.get(country1));//国家月目标
						if(realMonthCountryGoal.get(country1)!=null){
							Float tatalMonthWeight1=totalWeight1.get(country1);//整个月权重和
							if(!"com.unitek".equals(country1)){
								while(endDate1.after(startDate1)||endDate1.equals(startDate1)){
									Map<String,Float> totalW=Maps.newHashMap();
									if(!DateUtils.getFirstDayOfMonth(endDate1).equals(startDate1)){
										totalW=enterpriseWeekService.findWeightByMonth(DateUtils.getFirstDayOfMonth(endDate1), DateUtils.addDays(startDate1,-1),countryWeight1);
									}
									
									Float day1=0f;
									if(sales1.get(formatDay.format(startDate1))!=null){
										day1=sales1.get(formatDay.format(startDate1)).get(country1)==null?0:sales1.get(formatDay.format(startDate1)).get(country1);
										totalSales1+=sales1.get(formatDay.format(startDate1)).get(country1)==null?0:sales1.get(formatDay.format(startDate1)).get(country1);//总计销售额
									}else{
										totalSales1+=0;
									}
									int week1=enterpriseWeekService.dayForWeek(new SimpleDateFormat("yyyy-MM-dd").format(startDate1));
									Float dayWeight1=0f;
									if(countryWeight1.get(country1)==null){
										dayWeight1=1f;
									}else{
										 dayWeight1=countryWeight1.get(country1).get(week1);
									}
									Float temp=(countryMonthGoal1-totalSales1+day1<0?0:(countryMonthGoal1-totalSales1+day1));
									Float singleDayGoal=temp*(dayWeight1/(tatalMonthWeight1-(totalW.size()==0?0:totalW.get(country1))));
									
									dayGoal1+=countryMonthGoal1*(dayWeight1/tatalMonthWeight1);//日目标
									addDayWeight+=dayWeight1;//累计日权重
									Map<String,EnterpriseDetail> countryRate1 =realTable.get(formatDay.format(startDate1));
									if(countryRate1==null){
										countryRate1 = Maps.newLinkedHashMap();
										realTable.put(formatDay.format(startDate1),countryRate1);
									}
									countryRate1.put(country1, new EnterpriseDetail(totalSales1,day1,
											dayWeight1,tatalMonthWeight1,countryMonthGoal1*(dayWeight1/tatalMonthWeight1),dayGoal1,totalSales1/dayGoal1*100,addDayWeight,singleDayGoal));
									//countryRate.put(country, totalSales/dayGoal*100);
									
									startDate1 = DateUtils.addDays(startDate1, 1);
							   }
						   }
						}
					  dayGoal1=0f;
					  totalSales1=0f;
					  addDayWeight=0f;
					  startDate1=DateUtils.getFirstDayOfMonth(endDate1);
				}
			    
			    if("1".equals(flag1)){
					    realCountryGoal=realMonthCountryGoal.get("totalAvg");//国家月目标
						model.addAttribute("countryMonthGoal1", realCountryGoal);
						
					    while(endDate1.after(startDate1)||endDate1.equals(startDate1)){
					    	float dayGoalCount1=0f;
					    	float total1=0f;
					    	float dayGoalUp=0f;
					    	if(sales1.get(formatDay.format(startDate1))!=null){
					    		 for (Dict dict : DictUtils.getDictList("platform")) {
					    			 String country1 = dict.getValue();
                                     if(country1.contains("com")){
					    				 continue;
					    			 }
					    			 if(!"com.unitek".equals(country1)){
					    	 			total1+=sales1.get(formatDay.format(startDate1)).get(country1)==null?0:sales1.get(formatDay.format(startDate1)).get(country1);
					    			 }
					    		 }	
								totalSales1+=total1;//总计销售额
							}else{
								totalSales1+=0;
							}
							//totalSales+=sales.get(formatDay.format(startDate)).get("totalAvg");//总计销售额
							
							if(realCountryGoal!=0){
								Float tatalMonthWeight1=totalWeight1.get("totalAvg");//整个月权重和
								int week1=enterpriseWeekService.dayForWeek(new SimpleDateFormat("yyyy-MM-dd").format(startDate1));
								Float dayWeight1=countryWeight1.get("totalAvg").get(week1);
								addDayWeight+=dayWeight1;
								for (Dict dict : DictUtils.getDictList("platform")) {
					    			 String country1 = dict.getValue();
					    			 if(country1.contains("com")){
					    				 continue;
					    			 }
					    			 if(!"com.unitek".equals(country1)){
					    				 if(realTable.get(formatDay.format(startDate1))!=null&&realTable.get(formatDay.format(startDate1)).get(country1)!=null){
					    					 dayGoal1+=realTable.get(formatDay.format(startDate1)).get(country1).getDayGoal();
					    					 dayGoalCount1+=realTable.get(formatDay.format(startDate1)).get(country1).getDayGoal();
					    					 dayGoalUp+=realTable.get(formatDay.format(startDate1)).get(country1).getAutoDayGoal();
					    				 }
					    			 }
					    		 }	
							//	dayGoal+=countryMonthGoal*(dayWeight/tatalMonthWeight);//日目标
								Map<String,EnterpriseDetail> countryRate1 =realTable.get(formatDay.format(startDate1));
								if(countryRate1==null){
									countryRate1 = Maps.newLinkedHashMap();
									realTable.put(formatDay.format(startDate1),countryRate1);
								}
								//countryRate.put("totalAvg", totalSales/dayGoal*100);
								countryRate1.put("totalAvg", new EnterpriseDetail(totalSales1,total1,
										dayWeight1,tatalMonthWeight1,dayGoalCount1,dayGoal1,totalSales1/dayGoal1*100,addDayWeight,dayGoalUp));
							}
							
							startDate1 = DateUtils.addDays(startDate1, 1);
					   }
			   
			    	 model.addAttribute("realList1",realTable);
					 model.addAttribute("monthGoal1", realMonthCountryGoal);
					 if(realTable.size()>0){
					   model.addAttribute("keyList1",Lists.newArrayList(realTable.keySet()));
					 }
			    }
			    
			}
		}else if("EUR".equals(currencyType)&&StringUtils.isBlank(saleReport.getGroupName())&&resetFlag.contains(formatDay2.format(new Date()))){
			 model.addAttribute("countryMonthGoal1", realCountryGoal);
			 model.addAttribute("realList1",realTable);
			 model.addAttribute("monthGoal1", realMonthCountryGoal);
			 if(realTable.size()>0){
			   model.addAttribute("keyList1",Lists.newArrayList(realTable.keySet()));
			 }
		}
	    
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		model.addAttribute("otherData",otherData);
		//构建x轴
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		SimpleDateFormat enFormat=new java.text.SimpleDateFormat("E",Locale.US);
		SimpleDateFormat deFormat=new java.text.SimpleDateFormat("E",Locale.GERMANY);
		String language=LocaleContextHolder.getLocale().getLanguage();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				//tip.put(key,DateUtils.getDate(start,"E"));
				if("zh".equals(language)){
					tip.put(key,DateUtils.getDate(start,"E"));
				}else if("de".equals(language)){
					tip.put(key,deFormat.format(start));
				}else{
					tip.put(key,enFormat.format(start));
				}
				start = DateUtils.addDays(start, 1);
			}
		}
		model.addAttribute("xAxis", xAxis);
		model.addAttribute("tip", tip);
		//取出3个区间值
		String sec = xAxis.get(xAxis.size()-2);
		List<SaleReport> list = Lists.newArrayList();
		for (Dict dict : DictUtils.getDictList("platform")) {
			String country = dict.getValue();
			if("com.unitek".equals(country)){
				continue;
			}
			SaleReport temp = null;
			if(data.get(country)!=null){
				temp= data.get(country).get(sec);
			}
			if(temp==null){
				temp = new SaleReport(0f,0f,0,0);
			}
			temp.setCountry(country);
			list.add(temp);
		}
		Collections.sort(list);
		model.addAttribute("sec",list);
		
		/*List<String> typesAll = Lists.newArrayList();
		for (Dict str : DictUtils.getDictList("product_type")) {
			String type = HtmlUtils.htmlUnescape(str.getValue());
			typesAll.add(type);
		}
		model.addAttribute("typesAll",typesAll);*/
		Map<String,Float> change=new HashMap<String,Float>();
//		change.put("cny", AmazonProduct2Service.getRateConfig().get("CNY/USD"));
//		change.put("usdToCny", AmazonProduct2Service.getRateConfig().get("USD/CNY"));
//		change.put("eur", AmazonProduct2Service.getRateConfig().get("EUR/USD"));
//		change.put("cad", AmazonProduct2Service.getRateConfig().get("CAD/USD"));
//		change.put("gbp", AmazonProduct2Service.getRateConfig().get("GBP/USD"));
//		change.put("jpy", AmazonProduct2Service.getRateConfig().get("JPY/USD"));
//		change.put("mxn", AmazonProduct2Service.getRateConfig().get("MXN/USD"));

		change.put("cny", MathUtils.getRate("CNY", currencyType, null));
		if ("USD".equals(currencyType)) {
			change.put("usdToCny", AmazonProduct2Service.getRateConfig().get("USD/CNY"));
		} else {
			change.put("usdToCny", AmazonProduct2Service.getRateConfig().get("USD/CNY")*
					AmazonProduct2Service.getRateConfig().get("EUR/USD"));
		}
		change.put("eur", MathUtils.getRate("EUR", currencyType, null));
		change.put("usd", MathUtils.getRate("USD", currencyType, null));
		change.put("cad", MathUtils.getRate("CAD", currencyType, null));
		change.put("gbp", MathUtils.getRate("GBP", currencyType, null));
		change.put("jpy", MathUtils.getRate("JPY", currencyType, null));
		change.put("mxn", MathUtils.getRate("MXN", currencyType, null));
		model.addAttribute("change", change);
		model.addAttribute("balance",advertisingService.accountBalance());
		model.addAttribute("groupType", psiTypeGroupService.getAllList());
		
		
		if(lastUpdateTime==null){
			lastUpdateTime= amazonOrderService.getMaxOrderDate(null);
			model.addAttribute("lastUpdateTime", lastUpdateTime);
		}else if(lastUpdateTime!=null){
			long  diff=DateUtils.parseDate(DateUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss")).getTime()-lastUpdateTime.getTime();
			long min=diff/(1000*60);
			if(min>30){
				lastUpdateTime= amazonOrderService.getMaxOrderDate(null);
				model.addAttribute("lastUpdateTime", lastUpdateTime);
			}else{
				model.addAttribute("lastUpdateTime", lastUpdateTime);
			}
		}
		
		model.addAttribute("currencySymbol", com.springrain.erp.common.utils.StringUtils.
					getCurrencySymbolByType(saleReport.getCurrencyType()));
		if ("1".equals(saleReport.getSearchType())) {	//星期和月不统计退货情况
			//统计上个月各国产品的退货率
	//		String dateStart = formatDay.format(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(new Date(), -1)));
	//		String dateEnd = formatDay.format(DateUtils.getLastDayOfMonth(DateUtils.addMonths(new Date(), -1)));
			//统计滚动31日退货情况
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String dateStart = format.format(DateUtils.addDays(new Date(), -31));
			String dateEnd = format.format(DateUtils.addDays(new Date(), -1));
			Map<String, Map<String, Map<String, Integer>>> retrunGoods = returnGoodsService.getRetrunGoods(dateStart , dateEnd);
			Map<String, Map<String, Integer>> salesVolume = returnGoodsService.getSalesVolume(dateStart, dateEnd);
			model.addAttribute("retrunQuantity", retrunGoods);
			model.addAttribute("sureQuantity", salesVolume);
			Map<String, Map<String, Map<String, Float>>> returnGoods = 
					returnGoodsService.getRetrunGoodsRate(retrunGoods, salesVolume);
			Map<String, List<Entry<String, Float>>> returnRankMap = Maps.newHashMap();
			Map<String, Integer> repeatProduct = Maps.newHashMap();
			for (Map.Entry<String,Map<String,Map<String,Float>>> entryGoods: returnGoods.entrySet()) { 
			    String country = entryGoods.getKey();
				Map<String, Map<String, Float>> countryMap = entryGoods.getValue();
				Map<String, Float> productRankMap = Maps.newTreeMap();//国家退货率排名前三的产品
				for (Map.Entry<String,Map<String,Float>> entry: countryMap.entrySet()) { 
				    String productName = entry.getKey();
					Float rate = entry.getValue().get("total");
					if (rate != null) {
						productRankMap.put(productName, rate);
					}
				}
				List<Entry<String, Float>> list_Data = new ArrayList<Entry<String, Float>>(productRankMap.entrySet());
				Collections.sort(list_Data, new Comparator<Map.Entry<String, Float>>() {
					@Override
					public int compare(Entry<String, Float> o1, Entry<String, Float> o2) {
						if (o1.getValue() < o2.getValue()) {
							return 1;
						} else {
							return -1;
						}
					}
				});
				returnRankMap.put(country, list_Data);
				int num = 0;
				for (Entry<String, Float> entry : list_Data) {
					if (num>6) {
						break;
					}
					String productName = entry.getKey();
					Integer total = repeatProduct.get(productName);
					if (total == null) {
						repeatProduct.put(productName, 1);
					} else {
						repeatProduct.put(productName, total + 1);
					}
					num++;
				}
			}
			model.addAttribute("repeatProduct", repeatProduct);
			model.addAttribute("returnGoods", returnGoods);
			model.addAttribute("returnRankMap", returnRankMap);
		}
		return "modules/amazoninfo/sales/amazonSalesReportList";
	}
	
	@RequestMapping(value = "getRateDetail")
	@ResponseBody
	public Map<String,Map<String,EnterpriseDetail>> getRateDetail(Date start,Date end){
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Date endDate=end;
		Date startDate=DateUtils.getFirstDayOfMonth(endDate);
	    Date monthEnd=DateUtils.getLastDayOfMonth(endDate);
	
		String flag="1";
		Map<String,Float> totalWeight=enterpriseWeekService.findWeightByMonth(startDate, monthEnd);
		if(totalWeight.size()>0){
			Map<String,Float> monthGoal=enterpriseGoalService.findTotalGoalByMonth(new SimpleDateFormat("yyyyMM").format(endDate), "EUR");
			Map<String,Map<Integer,Float>> countryWeight= enterpriseWeekService.findByCountryWeight();
			Float totalSales=0f;
			Map<String, Map<String,Float>> sales=saleReportService.getSalesBydat(startDate,endDate, "EUR", rateMap,"total");//日期-国家-确认销量
			Map<String,Map<String,EnterpriseDetail>> realList=Maps.newLinkedHashMap();
			Float dayGoal=0f;
		    for (Dict dict : DictUtils.getDictList("platform")) {
					String country = dict.getValue();
					Float countryMonthGoal=(monthGoal.get(country)==null?0f:monthGoal.get(country));//国家月目标
					if(monthGoal.get(country)==null&&!"mx".equals(country)&&!"com.unitek".equals(country)){
						flag="0";
						break;
					}
					if(monthGoal.get(country)!=null){
						Float tatalMonthWeight=totalWeight.get(country);//整个月权重和
						if(!"com.unitek".equals(country)){
							while(endDate.after(startDate)||endDate.equals(startDate)){
								Float day=0f;
								if(sales.get(formatDay.format(startDate))!=null){
									day=sales.get(formatDay.format(startDate)).get(country)==null?0:sales.get(formatDay.format(startDate)).get(country);
									totalSales+=sales.get(formatDay.format(startDate)).get(country)==null?0:sales.get(formatDay.format(startDate)).get(country);//总计销售额
								}else{
									totalSales+=0;
								}
								int week=enterpriseWeekService.dayForWeek(new SimpleDateFormat("yyyy-MM-dd").format(startDate));
								Float dayWeight=0f;
								if(countryWeight.get(country)==null){
									dayWeight=1f;
								}else{
									dayWeight=countryWeight.get(country).get(week);
								}
								dayGoal+=countryMonthGoal*(dayWeight/tatalMonthWeight);//日目标
								
								Map<String,Float> totalW=Maps.newHashMap();
								if(!DateUtils.getFirstDayOfMonth(endDate).equals(startDate)){
									totalW=enterpriseWeekService.findWeightByMonth(DateUtils.getFirstDayOfMonth(endDate), DateUtils.addDays(startDate,-1),countryWeight);
								}
								Float temp=(countryMonthGoal-totalSales+day<0?0:(countryMonthGoal-totalSales+day));
								Float singleDayGoal=temp*(dayWeight/(tatalMonthWeight-(totalW.size()==0?0:totalW.get(country))));
								
								Map<String,EnterpriseDetail> countryRate =realList.get(formatDay.format(startDate));
								if(countryRate==null){
									countryRate = Maps.newLinkedHashMap();
									realList.put(formatDay.format(startDate),countryRate);
								}
								countryRate.put(country, new EnterpriseDetail(totalSales,day,
										dayWeight,tatalMonthWeight,countryMonthGoal*(dayWeight/tatalMonthWeight),dayGoal,totalSales/dayGoal*100,singleDayGoal));
								
								startDate = DateUtils.addDays(startDate, 1);
						   }
					   }
					}
				  dayGoal=0f;
				  totalSales=0f;
				  startDate=DateUtils.getFirstDayOfMonth(endDate);
			}
		    
		    Float countryMonthGoal=monthGoal.get("totalAvg");//国家月目标
		    while(endDate.after(startDate)||endDate.equals(startDate)){
		    	float dayGoalCount=0f;
		    	float total=0f;
		    	float dayGoalUp=0f;
		    	if(sales.get(formatDay.format(startDate))!=null){
		    		 for (Dict dict : DictUtils.getDictList("platform")) {
		    			 String country = dict.getValue();
		    			 if(!"com.unitek".equals(country)){
		    	 			total+=sales.get(formatDay.format(startDate)).get(country)==null?0:sales.get(formatDay.format(startDate)).get(country);
		    			 }
		    		 }	
					totalSales+=total;//总计销售额
				}else{
					totalSales+=0;
				}
			
				
				if(countryMonthGoal!=0){
					Float tatalMonthWeight=totalWeight.get("totalAvg");//整个月权重和
					int week=enterpriseWeekService.dayForWeek(new SimpleDateFormat("yyyy-MM-dd").format(startDate));
					Float dayWeight=countryWeight.get("totalAvg").get(week);
					for (Dict dict : DictUtils.getDictList("platform")) {
		    			 String country = dict.getValue();
		    			 if(!"com.unitek".equals(country)){
		    				 if(realList.get(formatDay.format(startDate))!=null&&realList.get(formatDay.format(startDate)).get(country)!=null){
		    					 dayGoal+=realList.get(formatDay.format(startDate)).get(country).getDayGoal();
		    					 dayGoalCount+=realList.get(formatDay.format(startDate)).get(country).getDayGoal();
		    					 dayGoalUp+=realList.get(formatDay.format(startDate)).get(country).getAutoDayGoal();
		    				 }
		    			 }
		    		 }	
				
					Map<String,EnterpriseDetail> countryRate =realList.get(formatDay.format(startDate));
					if(countryRate==null){
						countryRate = Maps.newLinkedHashMap();
						realList.put(formatDay.format(startDate),countryRate);
					}
					
					countryRate.put("totalAvg", new EnterpriseDetail(totalSales,total,
							dayWeight,tatalMonthWeight,dayGoalCount,dayGoal,totalSales/dayGoal*100,dayGoalUp));
				}
				
				startDate = DateUtils.addDays(startDate, 1);
		    }
		    if("1".equals(flag)){
		    	return realList;
		    }
	    }
		return null;
	}
	
	
	@RequestMapping(value = "getRateDetail2")
	@ResponseBody
	public Map<String,Map<String,EnterpriseDetail>> getRateDetail2(Date start,Date end,String groupName){
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Date endDate=end;
		Date startDate=DateUtils.getFirstDayOfMonth(endDate);
	    Date monthEnd=DateUtils.getLastDayOfMonth(endDate);
	
		String flag="1";
		Map<String,Float> totalWeight=enterpriseWeekService.findWeightByMonth(startDate, monthEnd);
		Map<String,Map<String,Float>> goalMap=enterpriseGoalService.getGoalByMonth(new SimpleDateFormat("yyyyMM").format(endDate),"EUR");
		
		if(totalWeight.size()>0&&goalMap.size()>0&&goalMap.get(groupName)!=null&&(!"total".equals(groupName)&&goalMap.get(groupName).size()>=6)){
			Map<String,Map<String,EnterpriseDetail>> realList=Maps.newLinkedHashMap();
			Map<String,Float> monthGoal=enterpriseGoalService.findGoalByMonth(new SimpleDateFormat("yyyyMM").format(endDate), "EUR");
			Map<String,Map<Integer,Float>> countryWeight= enterpriseWeekService.findByCountryWeight();
			Float totalSales=0f;
			Map<String, Map<String,Float>> sales=saleReportService.getSalesBydat(startDate,endDate, "EUR", rateMap,groupName);//日期-国家-确认销量
			Float rate1=1f;
			if(!"total".equals(groupName)){
			   rate1=goalMap.get(groupName).get("total")/monthGoal.get("totalAvg");
			}
			Float dayGoal=0f;
		    for (Dict dict : DictUtils.getDictList("platform")) {
					String country = dict.getValue();
					if(monthGoal.get(country)==null&&!"mx".equals(country)&&!"com.unitek".equals(country)&&!"com".equals(country)&&!"uk".equals(country)&&!"ca".equals(country)){
						flag="0";
						break;
					}
					if(monthGoal.get(country)!=null&&!"mx".equals(country)&&!"com.unitek".equals(country)&&!"com".equals(country)&&!"uk".equals(country)&&!"ca".equals(country)){
						Float rate2=1f;
						if(!"total".equals(groupName)){
							rate2=goalMap.get(groupName).get(dict.getValue())/monthGoal.get(country);
						}
						Float countryMonthGoal=rate2*(monthGoal.get(country)==null?0f:monthGoal.get(country));//国家月目标
						Float tatalMonthWeight=totalWeight.get(country);//整个月权重和
						if(!"com.unitek".equals(country)){
							while(endDate.after(startDate)||endDate.equals(startDate)){
								Float day=0f;
								Map<String,Float> totalW=Maps.newHashMap();
								if(!DateUtils.getFirstDayOfMonth(endDate).equals(startDate)){
									totalW=enterpriseWeekService.findWeightByMonth(DateUtils.getFirstDayOfMonth(endDate), DateUtils.addDays(startDate,-1),countryWeight);
								}
								if(sales.get(formatDay.format(startDate))!=null){
									day=sales.get(formatDay.format(startDate)).get(country)==null?0:sales.get(formatDay.format(startDate)).get(country);
									totalSales+=sales.get(formatDay.format(startDate)).get(country)==null?0:sales.get(formatDay.format(startDate)).get(country);//总计销售额
								}else{
									totalSales+=0;
								}
								int week=enterpriseWeekService.dayForWeek(new SimpleDateFormat("yyyy-MM-dd").format(startDate));
								Float dayWeight=0f;
								if(countryWeight.get(country)==null){
									dayWeight=1f;
								}else{
									dayWeight=countryWeight.get(country).get(week);
								}
								Float temp=(countryMonthGoal-totalSales+day<0?0:(countryMonthGoal-totalSales+day));
								Float singleDayGoal=temp*(dayWeight/(tatalMonthWeight-(totalW.size()==0?0:totalW.get(country))));
								
								dayGoal+=countryMonthGoal*(dayWeight/tatalMonthWeight);//日目标
								Map<String,EnterpriseDetail> countryRate =realList.get(formatDay.format(startDate));
								if(countryRate==null){
									countryRate = Maps.newLinkedHashMap();
									realList.put(formatDay.format(startDate),countryRate);
								}
								countryRate.put(country, new EnterpriseDetail(totalSales,day,
										dayWeight,tatalMonthWeight,countryMonthGoal*(dayWeight/tatalMonthWeight),dayGoal,totalSales/dayGoal*100,singleDayGoal));
								
								startDate = DateUtils.addDays(startDate, 1);
						   }
					   }
					}else if("com".equals(country)){
						country="en";
						Float rate2=1f;
						if(!"total".equals(groupName)){
							rate2=goalMap.get(groupName).get(country)/monthGoal.get(country);
						}
						
						Float countryMonthGoal=rate2*(monthGoal.get(country)==null?0f:monthGoal.get(country));//国家月目标
						Float tatalMonthWeight=(totalWeight.get("com")+totalWeight.get("uk")+totalWeight.get("ca"))/3;//整个月权重和
						if(!"com.unitek".equals(country)){
							while(endDate.after(startDate)||endDate.equals(startDate)){
								Float day=0f;
								Map<String,Float> totalW=Maps.newHashMap();
								if(!DateUtils.getFirstDayOfMonth(endDate).equals(startDate)){
									totalW=enterpriseWeekService.findWeightByMonth(DateUtils.getFirstDayOfMonth(endDate), DateUtils.addDays(startDate,-1),countryWeight);
								}
								if(sales.get(formatDay.format(startDate))!=null){
									Float num1=sales.get(formatDay.format(startDate)).get("com")==null?0:sales.get(formatDay.format(startDate)).get("com");
									Float num2=sales.get(formatDay.format(startDate)).get("uk")==null?0:sales.get(formatDay.format(startDate)).get("uk");
									Float num3=sales.get(formatDay.format(startDate)).get("ca")==null?0:sales.get(formatDay.format(startDate)).get("ca");
									day=num1+num2+num3;
									totalSales+=day;//总计销售额
								}else{
									totalSales+=0;
								}
								int week=enterpriseWeekService.dayForWeek(new SimpleDateFormat("yyyy-MM-dd").format(startDate));
								Float w1=(countryWeight.get("com")==null?1f:countryWeight.get("com").get(week));
								Float w2=(countryWeight.get("ca")==null?1f:countryWeight.get("ca").get(week));
								Float w3=(countryWeight.get("uk")==null?1f:countryWeight.get("uk").get(week));
								Float dayWeight=(w1+w2+w3)/3;
								
								Float tatalThree=(totalW.size()==0?0:(totalW.get("com")+totalW.get("uk")+totalW.get("ca"))/3);
								Float temp=(countryMonthGoal-totalSales+day<0?0:(countryMonthGoal-totalSales+day));
								Float singleDayGoal=temp*(dayWeight/(tatalMonthWeight-tatalThree));
							
								dayGoal+=countryMonthGoal*(dayWeight/tatalMonthWeight);//日目标
								Map<String,EnterpriseDetail> countryRate =realList.get(formatDay.format(startDate));
								if(countryRate==null){
									countryRate = Maps.newLinkedHashMap();
									realList.put(formatDay.format(startDate),countryRate);
								}
								countryRate.put(country, new EnterpriseDetail(totalSales,day,
										dayWeight,tatalMonthWeight,countryMonthGoal*(dayWeight/tatalMonthWeight),dayGoal,totalSales/dayGoal*100,singleDayGoal));
								startDate = DateUtils.addDays(startDate, 1);
						   }
					   }
					}
				  dayGoal=0f;
				  totalSales=0f;
				  startDate=DateUtils.getFirstDayOfMonth(endDate);
			}
		    Float countryMonthGoal=rate1*monthGoal.get("totalAvg");//国家月目标
		    while(endDate.after(startDate)||endDate.equals(startDate)){
		    	float dayGoalCount=0f;
		    	float total=0f;
		    	float dayGoalUp=0f;
		    	if(sales.get(formatDay.format(startDate))!=null){
		    		 for (Dict dict : DictUtils.getDictList("platform")) {
		    			 String country = dict.getValue();
		    			 if(!"com.unitek".equals(country)){
		    	 			total+=sales.get(formatDay.format(startDate)).get(country)==null?0:sales.get(formatDay.format(startDate)).get(country);
		    			 }
		    		 }	
					totalSales+=total;//总计销售额
				}else{
					totalSales+=0;
				}
				if(countryMonthGoal!=0){
					Float tatalMonthWeight=totalWeight.get("totalAvg");//整个月权重和
					int week=enterpriseWeekService.dayForWeek(new SimpleDateFormat("yyyy-MM-dd").format(startDate));
					Float dayWeight=countryWeight.get("totalAvg").get(week);
					for (Dict dict : DictUtils.getDictList("platform")) {
		    			 String country = dict.getValue();
		    			 if(!"com.unitek".equals(country)&&!"ca".equals(country)&&!"uk".equals(country)){
		    				 if("com".equals(country)){
		    					 country="en";
		    				 }
		    				 if(realList.get(formatDay.format(startDate))!=null&&realList.get(formatDay.format(startDate)).get(country)!=null){
		    					 dayGoal+=realList.get(formatDay.format(startDate)).get(country).getDayGoal();
		    					 dayGoalCount+=realList.get(formatDay.format(startDate)).get(country).getDayGoal();
		    					 dayGoalUp+=realList.get(formatDay.format(startDate)).get(country).getAutoDayGoal();
		    				 }
		    			 }
		    		 }	
					Map<String,EnterpriseDetail> countryRate =realList.get(formatDay.format(startDate));
					if(countryRate==null){
						countryRate = Maps.newLinkedHashMap();
						realList.put(formatDay.format(startDate),countryRate);
					}
					countryRate.put("totalAvg", new EnterpriseDetail(totalSales,total,
							dayWeight,tatalMonthWeight,dayGoalCount,dayGoal,totalSales/dayGoal*100,dayGoalUp));
				}
				startDate = DateUtils.addDays(startDate, 1);
		   }
		    if("1".equals(flag)){
		    	return realList;
		    }
		} 
		
		return null;
	}
	
	@RequestMapping(value = "list2")
	public String list2(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		String currencyType = saleReport.getCurrencyType();
		String productTypeTemp=saleReport.getProductType();
		Map<String,String> allLine=dictService.getProductLine();
		allLine.put("unGrouped", "UnGrouped");
		model.addAttribute("allLine", allLine);
		if(StringUtils.isBlank(saleReport.getGroupName())||"total".equals(saleReport.getGroupName())){
			saleReport.setProductType(null);
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isBlank(saleReport.getProductType())){
			List<Dict> dict=psiTypeGroupService.getProductType(saleReport.getGroupName());
			List<String> typeList=new ArrayList<String>();
			for (Dict dict2 : dict) {
				typeList.add("'"+dict2.getValue()+"'");
			}
			if(typeList!=null&&typeList.size()>0){
			    saleReport.setProductType(StringUtils.join(typeList.toArray(),",")); 
			}else{
				saleReport.setProductType("'no'");
			}  
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isNotBlank(saleReport.getProductType())){
			saleReport.setProductType("'"+saleReport.getProductType()+"'");
		}
		
		
		
		//得到当前月目标填报日期的前一天来获取当天汇率
		//String goalDateStr = enterpriseGoalService.findByCurrentMonth();
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String, Map<String, SaleReport>>  data = saleReportService.getSales(saleReport, rateMap);
		Map<String,Map<String, SaleReport>>  otherData = saleReportService.getOtherSales(saleReport, rateMap);
		
		//预测完成率
	  //  Calendar calendar = Calendar.getInstance();
		//Date endDate= calendar.getTime();
		if ("EUR".equals(currencyType)) { //欧元版统计预测完成率和目标
		Date endDate=saleReport.getEnd();
		model.addAttribute("day",new SimpleDateFormat("dd").format(endDate) );
		Date startDate=DateUtils.getFirstDayOfMonth(endDate);
		Date monthEnd=DateUtils.getLastDayOfMonth(endDate);
		String formatDate=new SimpleDateFormat("yyyyMM").format(startDate);
		
		model.addAttribute("formatDate", formatDate);
		
	  
	    //up
	        Calendar calendar = Calendar.getInstance();
	        calendar.set(Calendar.HOUR_OF_DAY, 0);
	        calendar.set(Calendar.MINUTE, 0);
	        calendar.set(Calendar.SECOND, 0);
			Date endDate1= calendar.getTime();
			Date startDate1=DateUtils.getFirstDayOfMonth(endDate1);
			Date monthEnd1=DateUtils.getLastDayOfMonth(endDate1);
			Map<String,Float> totalWeight1=enterpriseWeekService.findWeightByMonth(startDate1, monthEnd1);
			String flag1="1";
			
			//当前月各个产品线目标
			Map<String,Map<String,Float>> goalByMonth=enterpriseGoalService.getGoalByMonth(new SimpleDateFormat("yyyyMM").format(endDate1),currencyType);
			if(StringUtils.isNotEmpty(saleReport.getProductType())&&!"unGrouped".equals(saleReport.getGroupName())&&goalByMonth.get(saleReport.getGroupName())!=null){
				//当前月各产品线销量
				Map<String,Float> curMonthSale = this.saleReportService.getSalesByLine(new SimpleDateFormat("yyyyMM").format(endDate1), saleReport.getProductType(), currencyType, rateMap);
				Float totalGoal = goalByMonth.get(saleReport.getGroupName()).get("total");
				Float enGoal = goalByMonth.get(saleReport.getGroupName()).get("en");
				Float unEnGoal = goalByMonth.get(saleReport.getGroupName()).get("unEn");
				Float totalSale=curMonthSale.get("total")==null?0f:curMonthSale.get("total");
				Float enSale=curMonthSale.get("en")==null?0f:curMonthSale.get("en");
				Float unEnSale=curMonthSale.get("unEn")==null?0f:curMonthSale.get("unEn");
				int curDay=calendar.get(Calendar.DATE);
				int monDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); 
				List<Float[]> tipList =Lists.newArrayList();
				DecimalFormat  nf = new DecimalFormat("0.##");
				try {
					tipList.add(new Float[]{totalGoal,totalSale*100/totalGoal,((totalSale*monDays*100)/(totalGoal*curDay))});
					tipList.add(new Float[]{enGoal,(enSale*100/enGoal),((enSale*monDays*100)/(enGoal*curDay))});
					tipList.add(new Float[]{unEnGoal,(unEnSale*100/unEnGoal),((unEnSale*monDays*100)/(unEnGoal*curDay))});
				} catch (Exception e) {}
				model.addAttribute("tipList", tipList);
			}
			
			
			
			if(totalWeight1.size()>0&&goalByMonth.size()>0&&goalByMonth.get(saleReport.getGroupName())!=null&&(!"total".equals(saleReport.getGroupName())&&goalByMonth.get(saleReport.getGroupName()).size()>=6)){
				Map<String,Float> monthGoal1=enterpriseGoalService.findGoalByMonth(new SimpleDateFormat("yyyyMM").format(endDate1), currencyType);
				Float rate1=1f;
				if(!"total".equals(saleReport.getGroupName())){
				   rate1=goalByMonth.get(saleReport.getGroupName()).get("total")/monthGoal1.get("totalAvg");
				}
				Map<String,Map<Integer,Float>> countryWeight1= enterpriseWeekService.findByCountryWeight();
				Float totalSales1=0f;
				Map<String, Map<String,Float>> sales1=saleReportService.getSalesBydat(startDate1,endDate1, currencyType, rateMap,saleReport.getGroupName());//日期-国家-确认销量
				Map<String,Map<String,EnterpriseDetail>> realList1=new HashMap<String,Map<String,EnterpriseDetail>>();
				Float dayGoal1=0f;
				Float addDayWeight=0f;
			    for (Dict dict : DictUtils.getDictList("platform")) {
						String country1 = dict.getValue();
						if(monthGoal1.get(country1)==null&&!"mx".equals(country1)&&!country1.contains("com")&&!"uk".equals(country1)&&!"ca".equals(country1)){
							flag1="0";
							break;
						}
						if(monthGoal1.get(country1)!=null&&!"mx".equals(country1)&&!country1.contains("com")&&!"uk".equals(country1)&&!"ca".equals(country1)){
							Float rate2=1f;
							if(!"total".equals(saleReport.getGroupName())){
								rate2=goalByMonth.get(saleReport.getGroupName()).get(dict.getValue())/monthGoal1.get(country1);
							}
							Float countryMonthGoal1=rate2*(monthGoal1.get(country1)==null?0f:monthGoal1.get(country1));//国家月目标
							Float tatalMonthWeight1=totalWeight1.get(country1);//整个月权重和
							if(!"com.unitek".equals(country1)){
								while(endDate1.after(startDate1)||endDate1.equals(startDate1)){
									Float day1=0f;
									Map<String,Float> totalW=Maps.newHashMap();
									if(!DateUtils.getFirstDayOfMonth(endDate).equals(startDate)){
										totalW=enterpriseWeekService.findWeightByMonth(DateUtils.getFirstDayOfMonth(endDate1), DateUtils.addDays(startDate1,-1),countryWeight1);
									}
									if(sales1.get(formatDay.format(startDate1))!=null){
										day1=sales1.get(formatDay.format(startDate1)).get(country1)==null?0:sales1.get(formatDay.format(startDate1)).get(country1);
										totalSales1+=sales1.get(formatDay.format(startDate1)).get(country1)==null?0:sales1.get(formatDay.format(startDate1)).get(country1);//总计销售额
									}else{
										totalSales1+=0;
									}
									int week1=enterpriseWeekService.dayForWeek(new SimpleDateFormat("yyyy-MM-dd").format(startDate1));
									Float dayWeight1=0f;
									if(countryWeight1.get(country1)==null){
										dayWeight1=1f;
									}else{
										 dayWeight1=countryWeight1.get(country1).get(week1);
									}
									Float temp=(countryMonthGoal1-totalSales1+day1<0?0:(countryMonthGoal1-totalSales1+day1));
									Float singleDayGoal=temp*(dayWeight1/(tatalMonthWeight1-(totalW.size()==0?0:totalW.get(country1))));
									dayGoal1+=countryMonthGoal1*(dayWeight1/tatalMonthWeight1);//日目标
									addDayWeight+=dayWeight1;//累计日权重
									Map<String,EnterpriseDetail> countryRate1 =realList1.get(formatDay.format(startDate1));
									if(countryRate1==null){
										countryRate1 = Maps.newLinkedHashMap();
										realList1.put(formatDay.format(startDate1),countryRate1);
									}
									countryRate1.put(country1, new EnterpriseDetail(totalSales1,day1,
											dayWeight1,tatalMonthWeight1,countryMonthGoal1*(dayWeight1/tatalMonthWeight1),dayGoal1,totalSales1/dayGoal1*100,addDayWeight,singleDayGoal));
									//countryRate.put(country, totalSales/dayGoal*100);
									
									startDate1 = DateUtils.addDays(startDate1, 1);
							   }
						   }
						}else if("com".equals(country1)){
							country1="en";
							Float rate2=1f;
							if(!"total".equals(saleReport.getGroupName())){
								rate2=goalByMonth.get(saleReport.getGroupName()).get(country1)/monthGoal1.get(country1);
							}
							Float countryMonthGoal1=rate2*(monthGoal1.get(country1)==null?0f:monthGoal1.get(country1));//国家月目标
							Float tatalMonthWeight1=(totalWeight1.get("com")+totalWeight1.get("uk")+totalWeight1.get("ca"))/3;//整个月权重和
							if(!"com.unitek".equals(country1)){
								while(endDate1.after(startDate1)||endDate1.equals(startDate1)){
									Float day1=0f;
									Map<String,Float> totalW=Maps.newHashMap();
									if(!DateUtils.getFirstDayOfMonth(endDate).equals(startDate)){
										totalW=enterpriseWeekService.findWeightByMonth(DateUtils.getFirstDayOfMonth(endDate1), DateUtils.addDays(startDate1,-1),countryWeight1);
									}
									if(sales1.get(formatDay.format(startDate1))!=null){
										Float num1=sales1.get(formatDay.format(startDate1)).get("com")==null?0:sales1.get(formatDay.format(startDate1)).get("com");
										Float num2=sales1.get(formatDay.format(startDate1)).get("uk")==null?0:sales1.get(formatDay.format(startDate1)).get("uk");
										Float num3=sales1.get(formatDay.format(startDate1)).get("ca")==null?0:sales1.get(formatDay.format(startDate1)).get("ca");
										day1=num1+num2+num3;
										totalSales1+=day1;//总计销售额
									}else{
										totalSales1+=0;
									}
									int week1=enterpriseWeekService.dayForWeek(new SimpleDateFormat("yyyy-MM-dd").format(startDate1));
									
									Float w1=(countryWeight1.get("com")==null?1f:countryWeight1.get("com").get(week1));
									Float w2=(countryWeight1.get("ca")==null?1f:countryWeight1.get("ca").get(week1));
									Float w3=(countryWeight1.get("uk")==null?1f:countryWeight1.get("uk").get(week1));
									Float dayWeight1=(w1+w2+w3)/3;
								
									Float tatalThree=(totalW.size()==0?0:(totalW.get("com")+totalW.get("uk")+totalW.get("ca"))/3);
									Float temp=(countryMonthGoal1-totalSales1+day1<0?0:(countryMonthGoal1-totalSales1+day1));
									Float singleDayGoal=temp*(dayWeight1/(tatalMonthWeight1-tatalThree));
									
									dayGoal1+=countryMonthGoal1*(dayWeight1/tatalMonthWeight1);//日目标
									addDayWeight+=dayWeight1;//累计日权重
									Map<String,EnterpriseDetail> countryRate1 =realList1.get(formatDay.format(startDate1));
									if(countryRate1==null){
										countryRate1 = Maps.newLinkedHashMap();
										realList1.put(formatDay.format(startDate1),countryRate1);
									}
									countryRate1.put(country1, new EnterpriseDetail(totalSales1,day1,
											dayWeight1,tatalMonthWeight1,countryMonthGoal1*(dayWeight1/tatalMonthWeight1),dayGoal1,totalSales1/dayGoal1*100,addDayWeight,singleDayGoal));
									//countryRate.put(country, totalSales/dayGoal*100);
									
									startDate1 = DateUtils.addDays(startDate1, 1);
							   }
						   }
							
						}else if("mx".equals(country1)){//非英语国家总计
							country1="unEn";
							Float rate2=1f;
							if(!"total".equals(saleReport.getGroupName())){
								rate2=goalByMonth.get(saleReport.getGroupName()).get(country1)/monthGoal1.get(country1);
							}
							Float countryMonthGoal1=rate2*(monthGoal1.get(country1)==null?0f:monthGoal1.get(country1));//国家月目标
							Float tatalMonthWeight1=(totalWeight1.get("de")+totalWeight1.get("fr")+totalWeight1.get("es")+totalWeight1.get("it")+totalWeight1.get("jp"))/5;//整个月权重和
							if(!"com.unitek".equals(country1)){
								while(endDate1.after(startDate1)||endDate1.equals(startDate1)){
									Float day1=0f;
									Map<String,Float> totalW=Maps.newHashMap();
									if(!DateUtils.getFirstDayOfMonth(endDate).equals(startDate)){
										totalW=enterpriseWeekService.findWeightByMonth(DateUtils.getFirstDayOfMonth(endDate1), DateUtils.addDays(startDate1,-1),countryWeight1);
									}
									if(sales1.get(formatDay.format(startDate1))!=null){
										Float num1=sales1.get(formatDay.format(startDate1)).get("de")==null?0:sales1.get(formatDay.format(startDate1)).get("de");
										Float num2=sales1.get(formatDay.format(startDate1)).get("fr")==null?0:sales1.get(formatDay.format(startDate1)).get("fr");
										Float num3=sales1.get(formatDay.format(startDate1)).get("es")==null?0:sales1.get(formatDay.format(startDate1)).get("es");
										Float num4=sales1.get(formatDay.format(startDate1)).get("it")==null?0:sales1.get(formatDay.format(startDate1)).get("it");
										Float num5=sales1.get(formatDay.format(startDate1)).get("jp")==null?0:sales1.get(formatDay.format(startDate1)).get("jp");
										day1=num1+num2+num3+num4+num5;
										totalSales1+=day1;//总计销售额
									}else{
										totalSales1+=0;
									}
									int week1=enterpriseWeekService.dayForWeek(new SimpleDateFormat("yyyy-MM-dd").format(startDate1));
									
									Float w1=(countryWeight1.get("de")==null?1f:countryWeight1.get("de").get(week1));
									Float w2=(countryWeight1.get("fr")==null?1f:countryWeight1.get("fr").get(week1));
									Float w3=(countryWeight1.get("es")==null?1f:countryWeight1.get("es").get(week1));
									Float w4=(countryWeight1.get("it")==null?1f:countryWeight1.get("it").get(week1));
									Float w5=(countryWeight1.get("jp")==null?1f:countryWeight1.get("jp").get(week1));
									Float dayWeight1=(w1+w2+w3+w4+w5)/5;
								
									Float tatalThree=(totalW.size()==0?0:(totalW.get("de")+totalW.get("fr")+totalW.get("es")+totalW.get("it")+totalW.get("jp"))/5);
									Float temp=(countryMonthGoal1-totalSales1+day1<0?0:(countryMonthGoal1-totalSales1+day1));
									Float singleDayGoal=temp*(dayWeight1/(tatalMonthWeight1-tatalThree));
									
									dayGoal1+=countryMonthGoal1*(dayWeight1/tatalMonthWeight1);//日目标
									addDayWeight+=dayWeight1;//累计日权重
									Map<String,EnterpriseDetail> countryRate1 =realList1.get(formatDay.format(startDate1));
									if(countryRate1==null){
										countryRate1 = Maps.newLinkedHashMap();
										realList1.put(formatDay.format(startDate1),countryRate1);
									}
									countryRate1.put(country1, new EnterpriseDetail(totalSales1,day1,
											dayWeight1,tatalMonthWeight1,countryMonthGoal1*(dayWeight1/tatalMonthWeight1),dayGoal1,totalSales1/dayGoal1*100,addDayWeight,singleDayGoal));
									//countryRate.put(country, totalSales/dayGoal*100);
									
									startDate1 = DateUtils.addDays(startDate1, 1);
							   }
						   }
							
						}
					  dayGoal1=0f;
					  totalSales1=0f;
					  addDayWeight=0f;
					  startDate1=DateUtils.getFirstDayOfMonth(endDate1);
				}
			    Float countryMonthGoal1=rate1*monthGoal1.get("totalAvg");//国家月目标
				model.addAttribute("countryMonthGoal1", countryMonthGoal1);
			    while(endDate1.after(startDate1)||endDate1.equals(startDate1)){
			    	float dayGoalCount1=0f;
			    	float total1=0f;
			    	float dayGoalUp=0f;
			    	if(sales1.get(formatDay.format(startDate1))!=null){
			    		 for (Dict dict : DictUtils.getDictList("platform")) {
			    			 String country1 = dict.getValue();
			    			 if(!"com.unitek".equals(country1)){
			    	 			total1+=sales1.get(formatDay.format(startDate1)).get(country1)==null?0:sales1.get(formatDay.format(startDate1)).get(country1);
			    			 }
			    		 }	
						totalSales1+=total1;//总计销售额
					}else{
						totalSales1+=0;
					}
					//totalSales+=sales.get(formatDay.format(startDate)).get("totalAvg");//总计销售额
					
					if(countryMonthGoal1!=0){
						Float tatalMonthWeight1=totalWeight1.get("totalAvg");//整个月权重和
						int week1=enterpriseWeekService.dayForWeek(new SimpleDateFormat("yyyy-MM-dd").format(startDate1));
						Float dayWeight1=countryWeight1.get("totalAvg").get(week1);
						addDayWeight+=dayWeight1;
						for (Dict dict : DictUtils.getDictList("platform")) {
			    			 String country1 = dict.getValue();
			    			 if(!"com.unitek".equals(country1)&&!"ca".equals(country1)&&!"uk".equals(country1)){
			    				 if("com".equals(country1)){
			    					 country1="en";
			    				 }
			    				 if("mx".equals(country1)){//非英语国家
			    					 country1="unEn";
			    				 }
			    				 if(realList1.get(formatDay.format(startDate1))!=null&&realList1.get(formatDay.format(startDate1)).get(country1)!=null){
			    					 dayGoal1+=realList1.get(formatDay.format(startDate1)).get(country1).getDayGoal();
			    					 dayGoalCount1+=realList1.get(formatDay.format(startDate1)).get(country1).getDayGoal();
			    					 dayGoalUp+=realList1.get(formatDay.format(startDate1)).get(country1).getAutoDayGoal();
			    				 }
			    			 }
			    		 }	
					//	dayGoal+=countryMonthGoal*(dayWeight/tatalMonthWeight);//日目标
						Map<String,EnterpriseDetail> countryRate1 =realList1.get(formatDay.format(startDate1));
						if(countryRate1==null){
							countryRate1 = Maps.newLinkedHashMap();
							realList1.put(formatDay.format(startDate1),countryRate1);
						}
						//countryRate.put("totalAvg", totalSales/dayGoal*100);
						countryRate1.put("totalAvg", new EnterpriseDetail(totalSales1,total1,
								dayWeight1,tatalMonthWeight1,dayGoalCount1,dayGoal1,totalSales1/dayGoal1*100,addDayWeight,dayGoalUp));
					}
					
					startDate1 = DateUtils.addDays(startDate1, 1);
			   }
			    if("1".equals(flag1)){
			    	 model.addAttribute("realList1",realList1);
					 model.addAttribute("monthGoal1", monthGoal1);
					 if(realList1.size()>0){
					   model.addAttribute("keyList1",Lists.newArrayList(realList1.keySet()));
					 }
			    }
			    
			}
		}
	    
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		model.addAttribute("otherData",otherData);
		//构建x轴
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		SimpleDateFormat enFormat=new java.text.SimpleDateFormat("E",Locale.US);
		SimpleDateFormat deFormat=new java.text.SimpleDateFormat("E",Locale.GERMANY);
		String language=LocaleContextHolder.getLocale().getLanguage();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
			//	tip.put(key,DateUtils.getDate(start,"E"));
				if("zh".equals(language)){
					tip.put(key,DateUtils.getDate(start,"E"));
				}else if("de".equals(language)){
					tip.put(key,deFormat.format(start));
				}else{
					tip.put(key,enFormat.format(start));
				}
				start = DateUtils.addDays(start, 1);
			}
		}
		model.addAttribute("xAxis", xAxis);
		model.addAttribute("tip", tip);
		//取出3个区间值
		String sec = xAxis.get(xAxis.size()-2);
		List<SaleReport> list = Lists.newArrayList();
		for (Dict dict : DictUtils.getDictList("platform")) {
			String country = dict.getValue();
			if("com.unitek".equals(country)){
				continue;
			}
			SaleReport temp = null;
			if(data.get(country)!=null){
				temp= data.get(country).get(sec);
			}
			if(temp==null){
				temp = new SaleReport(0f,0f,0,0);
			}
			temp.setCountry(country);
			list.add(temp);
		}
		Collections.sort(list);
		model.addAttribute("sec",list);
		
		/*List<String> typesAll = Lists.newArrayList();
		for (Dict str : DictUtils.getDictList("product_type")) {
			String type = HtmlUtils.htmlUnescape(str.getValue());
			typesAll.add(type);
		}
		model.addAttribute("typesAll",typesAll);*/
		Map<String,Float> change=new HashMap<String,Float>();
//		change.put("cny", AmazonProduct2Service.getRateConfig().get("CNY/USD"));
//		change.put("usdToCny", AmazonProduct2Service.getRateConfig().get("USD/CNY"));
//		change.put("eur", AmazonProduct2Service.getRateConfig().get("EUR/USD"));
//		change.put("cad", AmazonProduct2Service.getRateConfig().get("CAD/USD"));
//		change.put("gbp", AmazonProduct2Service.getRateConfig().get("GBP/USD"));
//		change.put("jpy", AmazonProduct2Service.getRateConfig().get("JPY/USD"));
//		change.put("mxn", AmazonProduct2Service.getRateConfig().get("MXN/USD"));

		change.put("cny", MathUtils.getRate("CNY", currencyType, null));
		if ("USD".equals(currencyType)) {
			change.put("usdToCny", AmazonProduct2Service.getRateConfig().get("USD/CNY"));
		} else {
			change.put("usdToCny", AmazonProduct2Service.getRateConfig().get("USD/CNY")*
					AmazonProduct2Service.getRateConfig().get("EUR/USD"));
		}
		change.put("eur", MathUtils.getRate("EUR", currencyType, null));
		change.put("usd", MathUtils.getRate("USD", currencyType, null));
		change.put("cad", MathUtils.getRate("CAD", currencyType, null));
		change.put("gbp", MathUtils.getRate("GBP", currencyType, null));
		change.put("jpy", MathUtils.getRate("JPY", currencyType, null));
		change.put("mxn", MathUtils.getRate("MXN", currencyType, null));
		model.addAttribute("change", change);
		model.addAttribute("balance",advertisingService.accountBalance());
		model.addAttribute("groupType", psiTypeGroupService.getAllList());
		model.addAttribute("lastUpdateTime", amazonOrderService.getMaxOrderDate(null));
		model.addAttribute("currencySymbol", com.springrain.erp.common.utils.StringUtils.
					getCurrencySymbolByType(saleReport.getCurrencyType()));
		return "modules/amazoninfo/sales/amazonSalesReportList2";
	}
	
	@RequestMapping(value = {"orderList"})
	public String orderList(@RequestParam(required=false)String productName,String country,String type, String time, String orderType,String currencyType,String lineType,Model model){
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		List<Object[]> ops = Lists.newArrayList();
		List<Object[]> reviewOps = Lists.newArrayList();
		if(productName!=null&&productName.endsWith("[GROUP]")){
			String tempPName=productName.substring(0,productName.length()-7);
			String name=tempPName;
			String color = "";
			if(name.indexOf("_")>0){
				name=tempPName.substring(0,tempPName.lastIndexOf("_"));
				color = tempPName.substring(tempPName.lastIndexOf("_")+1);
			}
			Map<String,Set<String>> nameList=psiProductService.findGroupName(name,color);
			Set<String> nameSet=nameList.get("1");
			 ops = this.saleReportService.findOrder(time,type,country,productName,currencyType, rateMap,orderType,lineType,nameSet);
			 if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
				 reviewOps=saleReportService.findReviewOrder(time,type,country,productName,currencyType, rateMap,orderType,lineType,nameSet);
			 }
		}else{
			 ops = this.saleReportService.findOrder(time,type,country,productName,currencyType, rateMap,orderType,lineType,null);
			 if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
				 reviewOps=saleReportService.findReviewOrder(time,type,country,productName,currencyType, rateMap,orderType,lineType,null);
			 }
		}
		if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
			List<String> orderIdList=saleReportService.findOrder(time,type,country,orderType);
			model.addAttribute("orderIdList", orderIdList);	
		}
		String defaultStr = getDefaultStr(orderType);
		model.addAttribute("defaultStr", defaultStr);
		model.addAttribute("reviewOps",reviewOps);	
		model.addAttribute("ops", ops);	
		model.addAttribute("country", country);
	    model.addAttribute("byTime", type);
	    model.addAttribute("dateStr", time);
	    model.addAttribute("orderType", orderType);
	    model.addAttribute("lineType", lineType);
	    model.addAttribute("productName", productName);
		model.addAttribute("currencySymbol", com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(currencyType));
		model.addAttribute("currencyType", currencyType);
		Map<String,String> allLine=dictService.getProductLine();
		allLine.put("unGrouped", "UnGrouped");
		model.addAttribute("allLine", allLine);
		model.addAttribute("nameTypeMap", psiProductService.findProductTypeMap());
		model.addAttribute("typeLineMap", groupDictService.getTypeLine(null));
		return "modules/amazoninfo/sales/amazonSalesReportView";
	}
	
	@RequestMapping(value = {"skuList"})
	public String skuList(String country,String type, String time, String currencyType,Model model,String orderType,String lineType){
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		List<Object[]> ops = this.saleReportService.findSKU(time,type,country,currencyType, rateMap,orderType,lineType);
		model.addAttribute("ops", ops);
        model.addAttribute("country", country);
        model.addAttribute("byTime", type);
        model.addAttribute("dateStr", time);
        model.addAttribute("orderType", orderType);
        model.addAttribute("lineType", lineType);
		model.addAttribute("currencySymbol", com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(currencyType));
		model.addAttribute("currencyType", currencyType);
		Map<String,String> allLine=dictService.getProductLine();
		allLine.put("unGrouped", "UnGrouped");
		model.addAttribute("allLine", allLine);
		model.addAttribute("nameTypeMap", psiProductService.findProductTypeMap());
		model.addAttribute("typeLineMap", groupDictService.getTypeLine(null));
		return "modules/amazoninfo/sales/amazonSalesSkuView";
		
	}
	
	
	@RequestMapping(value = {"productListByDate"})
	public String productListByDate(String country,String type, String time,String currencyType,Model model,String orderType,String lineType){
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Date start = null;
		Date end = null;
		int year = Integer.parseInt(time.substring(0,4));
		int month = Integer.parseInt(time.substring(4))-1;
		try {
			start = DateUtils.getFirstDayOfMonth(year, month);
			end = DateUtils.getLastDayOfMonth(year, month);
			end = DateUtils.addDays(end, 1);
		} catch (ParseException e) {}
		Map<String,List<Object[]>> ops = this.saleReportService.findProduct2(start,end,country,currencyType, rateMap,orderType,lineType);
		String defaultStr = getDefaultStr(orderType);
		model.addAttribute("defaultStr", defaultStr);
		model.addAttribute("ops", ops);
		model.addAttribute("country", country);
	    model.addAttribute("byTime", type);
	    model.addAttribute("dateStr", time);
	    model.addAttribute("orderType", orderType);
	    model.addAttribute("lineType", lineType);
		model.addAttribute("currencySymbol", com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(currencyType));
		model.addAttribute("currencyType", currencyType);
		return "modules/amazoninfo/sales/amazonSalesProductView2";
	}
	
	@RequestMapping(value = {"productList"})
	public String productList(String country,String type, String time,String currencyType,Model model,String orderType,String lineType){
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Date start = null;
		Date end = null;
		if("1".equals(type)){
			try {
				start = format.parse(time);
				end = DateUtils.addDays(start, 1);
			} catch (ParseException e) {}
		}else if("2".equals(type)){
			int year = Integer.parseInt(time.substring(0,4));
			int week = Integer.parseInt(time.substring(4));
			start = DateUtils.getFirstDayOfWeek(year, week);
			end = DateUtils.getLastDayOfWeek(year, week);
			end = DateUtils.addDays(end, 1);
		}else if("3".equals(type)){
			int year = Integer.parseInt(time.substring(0,4));
			int month = Integer.parseInt(time.substring(4))-1;
			try {
				start = DateUtils.getFirstDayOfMonth(year, month);
				end = DateUtils.getLastDayOfMonth(year, month);
				end = DateUtils.addDays(end, 1);
			} catch (ParseException e) {}
		}
		List<Object[]> ops = this.saleReportService.findProduct(start,end,country,currencyType, rateMap,orderType,lineType);
		String defaultStr = getDefaultStr(orderType);
		model.addAttribute("defaultStr", defaultStr);
		if((StringUtils.isBlank(orderType)||orderType.startsWith("1"))
				&& UserUtils.hasPermission("amazoninfo:profits:view")){//可查看利润
			Date date = saleReportService.getMaxDateFee(country);
			boolean flag = true;
			if(!"1".equals(type)){
				flag = date!=null&&(date.after(start) || date.equals(start)||date.equals(end));
			}
			model.addAttribute("flag",flag);
			if(flag){
				Map<String,String> allLine=dictService.getProductLine();
				for (Map.Entry<String,String> entry: allLine.entrySet()) { 
				    String key = entry.getKey();
					String value = entry.getValue();
					allLine.put(key, value.substring(0, 1));
				}
				if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
					// 从利润表统计数据,利润表只有亚马逊订单信息
					SaleProfit saleProfit = new SaleProfit();
					saleProfit.setDay(formatDay.format(start));
					saleProfit.setEnd(formatDay.format(DateUtils.addDays(end, -1)));
					saleProfit.setCountry(country);
					if (StringUtils.isNotEmpty(lineType) && StringUtils.isNotEmpty(allLine.get(lineType))) {
						saleProfit.setLine(allLine.get(lineType));
					}
					Map<String, SaleProfit> profitMap = Maps.newHashMap();
					List<SaleProfit> profitList = saleProfitService.getSalesProfitList(saleProfit, "2", "0").get("1");
					for (SaleProfit profit : profitList) {
						profitMap.put(profit.getProductName(), profit);
					}
					model.addAttribute("profitMap", profitMap);
				}
			}
		}else if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
			Map<String,Float> priceMap=productPriceService.findAllProducSalePrice(currencyType);
			model.addAttribute("priceMap",priceMap);
		}
		model.addAttribute("ops", ops);
		model.addAttribute("country", country);
	    model.addAttribute("byTime", type);
	    model.addAttribute("dateStr", time);
	    model.addAttribute("orderType", orderType);
	    model.addAttribute("lineType", lineType);
		model.addAttribute("currencySymbol", com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(currencyType));
		model.addAttribute("currencyType", currencyType);
	//	model.addAttribute("typeData",saleReportService.getSalesType(time, type, country));
		Map<String,String> allLine=dictService.getProductLine();
		allLine.put("unGrouped", "UnGrouped");
		model.addAttribute("allLine", allLine);
		//亚马逊用金比
		model.addAttribute("commission", productPriceService.findCommission());
		float vat = 0f;
		if(StringUtils.isNotEmpty(country) && "uk,it,es,fr,de,com,ca,jp,mx".contains(country)){
			String temp = country.toUpperCase();
			if("UK".equals(temp)){
				temp = "GB";
			}
			if("COM".equals(temp)){
				temp = "US";
			}
			CountryCode vatCode = CountryCode.valueOf(temp);
			if(vatCode!=null){
				vat = vatCode.getVat()/100f;
			}
			model.addAttribute("vat", vat);
		} 
		model.addAttribute("nameTypeMap", psiProductService.findProductTypeMap());
		model.addAttribute("typeLineMap", groupDictService.getTypeLine(null));
		return "modules/amazoninfo/sales/amazonSalesProductView";
	}
	
	private static DateFormat format = formatDay;
	
	@RequestMapping(value = {"contrastSaleView"})
	public String contrast(String startTime,String endTime,String country,String type,String currencyType,String lineType,Model model){
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		
		Date start = null;
		Date end = null;
		Date start1 = null;
		Date end1 = null;
		if("1".equals(type)){
			try {
				start1 = format.parse(endTime);
				end1 = start1;
				if(StringUtils.isEmpty(startTime)){
					end = start = DateUtils.addDays(start1, -1);
				}else{
					start = format.parse(startTime);
					end = start;
				}
				
			} catch (ParseException e) {}
		}else if("2".equals(type)){
			int year = Integer.parseInt(endTime.substring(0,4));
			int week = Integer.parseInt(endTime.substring(4));
			start1 = DateUtils.getFirstDayOfWeek(year, week);
			end1 = DateUtils.getLastDayOfWeek(year, week);
			
			if(StringUtils.isNotEmpty(startTime)){
				year = Integer.parseInt(startTime.substring(0,4));
				week = Integer.parseInt(startTime.substring(4));
				start = DateUtils.getFirstDayOfWeek(year, week);
				end = DateUtils.getLastDayOfWeek(year, week);
			}else{
				start = DateUtils.addWeeks(start1, -1);
				end = DateUtils.addWeeks(end1, -1);
			}
		}else if("3".equals(type)){
			int year = Integer.parseInt(endTime.substring(0,4));
			int month = Integer.parseInt(endTime.substring(4))-1;
			try {
				start1 = DateUtils.getFirstDayOfMonth(year, month);
				end1 = DateUtils.getLastDayOfMonth(year, month);
			} catch (ParseException e) {}
			
			if(StringUtils.isNotEmpty(startTime)){
				year = Integer.parseInt(startTime.substring(0,4));
				month = Integer.parseInt(startTime.substring(4))-1;
				try {
					start = DateUtils.getFirstDayOfMonth(year, month);
					end = DateUtils.getLastDayOfMonth(year, month);
				} catch (ParseException e) {}
			}else{
				start = DateUtils.addMonths(start1, -1);
				end = DateUtils.getLastDayOfMonth(start);
			}
		}
		model.addAttribute("start", start);	
		model.addAttribute("end", end);
		
		model.addAttribute("start1", start1);	
		model.addAttribute("end1", end1);
		
		Map<String,Object[]> data1 = this.saleReportService.findSales(start,end,country,currencyType, rateMap,lineType);
		Map<String,Object[]> data2 = this.saleReportService.findSales(start1,end1,country,currencyType, rateMap,lineType);
		
		model.addAttribute("data1", data1);	
		model.addAttribute("data2", data2);
		model.addAttribute("lineType", lineType);
		model.addAttribute("country", country);
	    model.addAttribute("byTime", type);
		model.addAttribute("currencySymbol", com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(currencyType));
		model.addAttribute("currencyType", currencyType);
		Map<String,String> allLine=dictService.getProductLine();
		allLine.put("unGrouped", "UnGrouped");
		model.addAttribute("allLine", allLine);
		model.addAttribute("nameTypeMap", psiProductService.findProductTypeMap());
		model.addAttribute("typeLineMap", groupDictService.getTypeLine(null));
		return "modules/amazoninfo/sales/amazonSalesProductContrastView";
	}
	
	@RequestMapping(value = {"contrast"})
	public String contrast(Date start,Date start1,Date end,Date end1,String country,String currencyType,String lineType,Model model){
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		model.addAttribute("start", start);	
		model.addAttribute("end", end);
		
		model.addAttribute("start1", start1);	
		model.addAttribute("end1", end1);
		
		Map<String,Object[]> data1 = this.saleReportService.findSales(start,end,country,currencyType, rateMap,lineType);
		Map<String,Object[]> data2 = this.saleReportService.findSales(start1,end1,country,currencyType, rateMap,lineType);
		
		model.addAttribute("data1", data1);	
		model.addAttribute("data2", data2);
		
		model.addAttribute("country", country);
	    model.addAttribute("byTime", "1");
		model.addAttribute("currencySymbol", com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(currencyType));
		model.addAttribute("currencyType", currencyType);
		 model.addAttribute("lineType", lineType);
		Map<String,String> allLine=dictService.getProductLine();
		allLine.put("unGrouped", "UnGrouped");
		model.addAttribute("allLine", allLine);
		return "modules/amazoninfo/sales/amazonSalesProductContrastView";
	}
	
	@RequestMapping(value = {"exportCompare"})
	public String exportCompare(Date start,Date start1,Date end,Date end1,String country,String currencyType,String lineType,Model model, HttpServletRequest request,HttpServletResponse response){
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCell cell = null;
		String currencySymbol = com.springrain.erp.common.utils.StringUtils.getCurrencySymbolByType(currencyType);
		List<String> title = Lists.newArrayList("NO.","产品名称","区间1","区间2","波动","","区间1","区间2","波动","");
		List<String> title2 = Lists.newArrayList("","","销量","销量","销量","幅度(%)","销售额("+currencySymbol+")","销售额("+currencySymbol+")","销售额("+currencySymbol+")","幅度(%)");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
	    }
		row=sheet.createRow(1);
		for (int i = 0; i < title2.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title2.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
	    }
		sheet.addMergedRegion(new CellRangeAddress(0, 1,0,0));
		sheet.addMergedRegion(new CellRangeAddress(0, 1,1,1));
		sheet.addMergedRegion(new CellRangeAddress(0, 0,4,5));
		sheet.addMergedRegion(new CellRangeAddress(0, 0,8,9));
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String,Object[]> data1 = this.saleReportService.findSales(start,end,country,currencyType,rateMap,lineType);
		Map<String,Object[]> data2 = this.saleReportService.findSales(start1,end1,country,currencyType, rateMap,lineType);
		PsiProduct psiProduct=new PsiProduct();
		List<String> productName=new ArrayList<String>();
		if(StringUtils.isBlank(country)){
			productName =psiProductService.findCountryProduct(psiProduct);
		}else{
			psiProduct.setPlatform(country);
			productName =psiProductService.findCountryProduct(psiProduct);
		}
		int rownum=2;
		for (String name : productName) {
				if(data1.get(name)!=null){
					row=sheet.createRow(rownum++);
					int j=0;
		    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(rownum-2);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
		    		int volume1=data1.get(name)[2]==null?0:Integer.parseInt(data1.get(name)[2].toString());
		    		int volume2=data2.get(name)==null?0:(data2.get(name)[2]==null?0:Integer.parseInt(data2.get(name)[2].toString()));
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(volume1);
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(volume2);
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(volume2-volume1);
				    if(volume1>0&&volume2>0){
				    	row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal((volume2-volume1)*100.0f/volume1).setScale(2,4).doubleValue());
				    }else if(volume1>0){
				    	row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(-100);
				    }else if(volume2>0){
				    	row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(100);
				    }else{
				    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				    }
				     
				    float sale1=data1.get(name)[2]==null?0:new BigDecimal(data1.get(name)[1].toString()).setScale(2,4).floatValue();
				    float sale2=data2.get(name)==null?0:(data2.get(name)[2]==null?0:new BigDecimal(data2.get(name)[1].toString()).setScale(2,4)).floatValue();
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(sale1);
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(sale2);
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(sale2-sale1);
					if(sale1>0&&sale2>0){
					   row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal((sale2-sale1)*100/sale1).setScale(2,4).doubleValue());
					}else if(sale1>0){
					   row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(-100);
					}else if(sale2>0){
					   row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(100);
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
				}
				if(data1.get(name)==null&&data2.get(name)!=null){
					row=sheet.createRow(rownum++);
					int j=0;
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(rownum-2);
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(data2.get(name)[2]==null?0:Integer.parseInt(data2.get(name)[2].toString()));
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(data2.get(name)[2]==null?0:Integer.parseInt(data2.get(name)[2].toString()));
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(100);
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(data2.get(name)[1]==null?0:new BigDecimal(data2.get(name)[1].toString()).setScale(2,4).doubleValue());
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(data2.get(name)[1]==null?0:new BigDecimal(data2.get(name)[1].toString()).setScale(2,4).doubleValue());
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(100);
				}
		}
		 for(int j=2;j<rownum;j++){
			 for(int i=0;i<title.size();i++){
		    	  if(i==5||i==6||i==7||i==8||i==9){
		    		 sheet.getRow(j).getCell(i).setCellStyle(cellStyle);
		    	  }else{
		    	     sheet.getRow(j).getCell(i).setCellStyle(contentStyle);
		    	  } 
			 }	  
	       }
			for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
		    }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = DictUtils.getDictLabel(country,"platform","eu".equals(country)?"欧洲合计":"各国合计")+"销售对比报告" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
			
	@RequestMapping(value = {"exportAll"})
	public String exportAll(@RequestParam(required=false)String productName,String country,String type, String time,String flag,String orderType,String lineType, String currencyType,Model model, HttpServletRequest request,HttpServletResponse response){
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCell cell = null;
		String currencySymbol = com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(currencyType);
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		String fileName="";
		if("0".equals(flag)){
			fileName=DictUtils.getDictLabel(country,"platform","eu".equals(country)?"欧洲合计":"各国合计")+"销售报告"+time+("1".equals(type)?"日":("2".equals(type)?"周":"月"))+"订单明细";
			//List<Object[]> order = this.saleReportService.findOrder(time,type,country,productName,currencyType, rateMap,orderType,lineType);
			List<Object[]> order = Lists.newArrayList();
			if(productName.endsWith("[GROUP]")){
				String tempPName=productName.substring(0,productName.length()-7);
				String name=tempPName;
				String color = "";
				if(name.indexOf("_")>0){
					name=tempPName.substring(0,tempPName.lastIndexOf("_"));
					color = tempPName.substring(tempPName.lastIndexOf("_")+1);
				}
				Map<String,Set<String>> nameList=psiProductService.findGroupName(name,color);
				Set<String> nameSet=nameList.get("1");
				order = this.saleReportService.findOrder(time,type,country,productName,currencyType, rateMap,orderType,lineType,nameSet);
			}else{
				order = this.saleReportService.findOrder(time,type,country,productName,currencyType, rateMap,orderType,lineType,null);
			}
			List<String> orderTitle = Lists.newArrayList("No.","AmazonOrderId","Country","Sku","产品名称","数量","订单状态","销售额("+currencySymbol+")","下单时间");
			if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
				orderTitle.add("StateOrRegion");
				orderTitle.add("CountryCode");
			}
			for (int i = 0; i < orderTitle.size(); i++) {
					cell = row.createCell(i);
					cell.setCellValue(orderTitle.get(i));
					cell.setCellStyle(style);
					sheet.autoSizeColumn((short) i);
			}
			if(order!=null){
				for(int i=0;i<order.size();i++){
					int j=0;
		    		row=sheet.createRow(i+1);
		    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(i+1);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(order.get(i)[0]==null?"":order.get(i)[0].toString());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(order.get(i)[1]==null?"":order.get(i)[1].toString());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(order.get(i)[2]==null?"":order.get(i)[2].toString());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(order.get(i)[3]==null?"":order.get(i)[3].toString());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(order.get(i)[4]==null?"":order.get(i)[4].toString());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(order.get(i)[5]==null?"":order.get(i)[5].toString());
		    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(order.get(i)[6]==null?0:((BigDecimal)(order.get(i)[6])).doubleValue());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(order.get(i)[7]==null?"":order.get(i)[7].toString());
		    		if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(order.get(i)[9]==null?"":order.get(i)[9].toString());
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(order.get(i)[10]==null?"":order.get(i)[10].toString());
					}
				}
				for (int i=0;i<order.size();i++) {
		        	 for (int j = 0; j < orderTitle.size(); j++) {
			        	  sheet.getRow(i+1).getCell(j).setCellStyle(contentStyle);
					 }
		         }
				 for (int i = 0; i < orderTitle.size(); i++) {
		        		 sheet.autoSizeColumn((short)i);
				  }
			}
		}else if("1".equals(flag)){
			fileName=DictUtils.getDictLabel(country,"platform","eu".equals(country)?"欧洲合计":"各国合计")+"销售报告"+time+("1".equals(type)?"日":("2".equals(type)?"周":"月"))+"Sku明细";
			List<Object[]> sku = this.saleReportService.findSKU(time,type,country,currencyType, rateMap,orderType,lineType);
			List<String> skuTitle = Lists.newArrayList("No.","Sku","产品名称","平台","数量","销售额("+currencySymbol+")");
			for (int i = 0; i < skuTitle.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(skuTitle.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
		     }
			if(sku!=null){
				for(int i=0;i<sku.size();i++){
					int j=0;
		    		row=sheet.createRow(i+1);
		    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(i+1);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sku.get(i)[0]==null?"":sku.get(i)[0].toString());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sku.get(i)[1]==null?"":sku.get(i)[1].toString());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sku.get(i)[2]==null?"":sku.get(i)[2].toString());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sku.get(i)[3]==null?"":sku.get(i)[3].toString());
		    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(sku.get(i)[4]==null?0:((BigDecimal)(sku.get(i)[4])).doubleValue());
				}	
				for (int i=0;i<sku.size();i++) {
		        	 for (int j = 0; j < skuTitle.size(); j++) {
			        	  sheet.getRow(i+1).getCell(j).setCellStyle(contentStyle);
					 }
		         }
				for (int i = 0; i < skuTitle.size(); i++) {
	        		 sheet.autoSizeColumn((short)i);
			    }
			}
		} else {
				fileName=DictUtils.getDictLabel(country,"platform","eu".equals(country)?"欧洲合计":"各国合计")+"销售报告"+time+("1".equals(type)?"日":("2".equals(type)?"周":"月"))+"产品明细";
				
				
				Date start = null;
				Date end = null;
				if("1".equals(type)){
					try {
						start = format.parse(time);
						end = DateUtils.addDays(start, 1);
					} catch (ParseException e) {}
				}else if("2".equals(type)){
					int year = Integer.parseInt(time.substring(0,4));
					int week = Integer.parseInt(time.substring(4));
					start = DateUtils.getFirstDayOfWeek(year, week);
					end = DateUtils.getLastDayOfWeek(year, week);
					end = DateUtils.addDays(end, 1);
				}else if("3".equals(type)){
					int year = Integer.parseInt(time.substring(0,4));
					int month = Integer.parseInt(time.substring(4))-1;
					try {
						start = DateUtils.getFirstDayOfMonth(year, month);
						end = DateUtils.getLastDayOfMonth(year, month);
						end = DateUtils.addDays(end, 1);
					} catch (ParseException e) {}
				}
				List<Object[]> product = this.saleReportService.findProduct(start,end,country,currencyType, rateMap,orderType,lineType);
				Map<String, Map<String, Float>> priceAndGw = null;
				if((StringUtils.isBlank(orderType)||orderType.startsWith("1"))
						&& UserUtils.hasPermission("amazoninfo:profits:view")){
					Date date = saleReportService.getMaxDateFee(country);
					if(date.after(start) || date.equals(start)||date.equals(end)){
						priceAndGw = saleReportService.getProductPriceAndTranGwNoTax(currencyType, rateMap);
					}
				}
				Map<String, Map<String, String>> map = saleReportService.getPorductsTypeAndLine();
				String line = "";
				String types = "";
				if(priceAndGw==null){
					List<String> productTitle = Lists.newArrayList("No.","产品名称","产品类型","产品线","数量","销售额("+currencySymbol+")");
					for (int i = 0; i < productTitle.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(productTitle.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
				     }
					if(product!=null){
						for(int i=0;i<product.size();i++){
							int j=0;
				    		row=sheet.createRow(i+1);
				    		String name = product.get(i)[0]==null?"":product.get(i)[0].toString();
				    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(i+1);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
				    		if(StringUtils.isNotEmpty(name)&&map.get(name)!=null){
				    			line = map.get(name).get("line");
				    			types = map.get(name).get("type");
				    			line = (line == null?"":line);
				    			types = (types == null?"":types);
				    		}
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(types);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(line);
				    		
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.get(i)[1]==null?"":product.get(i)[1].toString());
				    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.get(i)[2]==null?0:((BigDecimal)(product.get(i)[2])).doubleValue());
						}	
						for (int i=0;i<product.size();i++) {
				        	 for (int j = 0; j < productTitle.size(); j++) {
					        	  sheet.getRow(i+1).getCell(j).setCellStyle(contentStyle);
							 }
				         }
						for (int i = 0; i < productTitle.size(); i++) {
			        		 sheet.autoSizeColumn((short)i);
					    }
					}
				}else{
					List<String> productTitle = Lists.newArrayList("No.","产品名称","产品类型","产品线","数量","销售额("+currencySymbol+")","确认费用销量","税后收入","退款","亚马逊佣金","杂费","运输费","采购成本","利润");
					for (int i = 0; i < productTitle.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(productTitle.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
				     }
					if(product!=null){
						for(int i=0;i<product.size();i++){
							int j=0;
				    		row=sheet.createRow(i+1);
				    		String name = product.get(i)[0]==null?"":product.get(i)[0].toString();
				    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(i+1);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
				    		if(StringUtils.isNotEmpty(name)&&map.get(name)!=null){
				    			line = map.get(name).get("line");
				    			types = map.get(name).get("type");
				    			line = (line == null?"":line);
				    			types = (types == null?"":types);
				    		}
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(types);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(line);
				    		
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.get(i)[1]==null?"":product.get(i)[1].toString());
				    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.get(i)[2]==null?0:((BigDecimal)(product.get(i)[2])).doubleValue());
				    		/*int feeQ = product.get(i)[7]==null?0:((BigDecimal)(product.get(i)[7])).intValue();
				    		double refund =  product.get(i)[8]==null?0:((BigDecimal)(product.get(i)[8])).doubleValue();
				    		double salesNoTax = product.get(i)[3]==null?0:((BigDecimal)(product.get(i)[3])).doubleValue();
				    		double fee = product.get(i)[4]==null?0:((BigDecimal)(product.get(i)[4])).doubleValue();
				    		double other = product.get(i)[5]==null?0:((BigDecimal)(product.get(i)[5])).doubleValue();*/
				    		int feeQ = product.get(i)[10]==null?0:((BigDecimal)(product.get(i)[10])).intValue();
				    		double refund =  product.get(i)[11]==null?0:((BigDecimal)(product.get(i)[11])).doubleValue();
				    		double salesNoTax = product.get(i)[6]==null?0:((BigDecimal)(product.get(i)[6])).doubleValue();
				    		double fee = product.get(i)[7]==null?0:((BigDecimal)(product.get(i)[7])).doubleValue();
				    		double other = product.get(i)[8]==null?0:((BigDecimal)(product.get(i)[8])).doubleValue();
				    		double gw = 0;
				    		double cb = 0 ;
				    		if(priceAndGw.get(name)!=null){
				    			Float price =  priceAndGw.get(name).get("price");
				    			Float gwt =  priceAndGw.get(name).get("gw");
				    			price = price==null?0:price;
				    			gwt = gwt==null?0:gwt;
				    			//gw = gwt * (product.get(i)[6]==null?0:((BigDecimal)(product.get(i)[6])).doubleValue());
				    			gw = gwt * (product.get(i)[9]==null?0:((BigDecimal)(product.get(i)[9])).doubleValue());
				    			cb = price * feeQ;
				    		}
				    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(feeQ);
				    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(salesNoTax);
				    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(refund);
				    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(fee);
				    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(other);
				    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(-gw);
				    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(-cb);
				    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(salesNoTax+fee+other+refund-gw-cb);
						}	
						for (int i=0;i<product.size();i++) {
				        	 for (int j = 0; j < productTitle.size(); j++) {
					        	  sheet.getRow(i+1).getCell(j).setCellStyle(contentStyle);
							 }
				         }
						for (int i = 0; i < productTitle.size(); i++) {
			        		 sheet.autoSizeColumn((short)i);
					    }
					}
				}
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            fileName = fileName + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			fileName = fileName.replace("%7C", "|");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = {"exportByProductCountry"})
	public String exportByProductCountry(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		String productTypeTemp=saleReport.getProductType();
		PsiProduct psiProduct=new PsiProduct();
		List<String> page=new ArrayList<String>();
		if(StringUtils.isBlank(saleReport.getGroupName())){
			saleReport.setProductType(null);
			if(StringUtils.isBlank(saleReport.getCountry())){
				page =psiProductService.findCountryProduct2(psiProduct);
			}else{
				psiProduct.setPlatform(saleReport.getCountry());
				page =psiProductService.findCountryProduct2(psiProduct);
			}
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isBlank(saleReport.getProductType())){
			List<Dict> dict=psiTypeGroupService.getProductType(saleReport.getGroupName());
			List<String> typeList=new ArrayList<String>();
			Set<String> nameSet=new HashSet<String>();
			for (Dict dict2 : dict) {
				typeList.add("'"+dict2.getValue()+"'");
				nameSet.add(dict2.getValue());
			}
			if(typeList!=null&&typeList.size()>0){
			    saleReport.setProductType(StringUtils.join(typeList.toArray(),",")); 
			}else{
				saleReport.setProductType("'no'");
			}  
			List<PsiProduct> psiList=psiProductService.findAllByCountryByType2(saleReport.getCountry(),nameSet);
			for (PsiProduct psiProduct2 : psiList) {
				 page.addAll(psiProduct2.getProductNameWithColor());
			}
			
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isNotBlank(saleReport.getProductType())){
			saleReport.setProductType("'"+saleReport.getProductType()+"'");
			List<PsiProduct> psiList=psiProductService.findAllByCountry2(saleReport.getCountry(),productTypeTemp);
			for (PsiProduct psiProduct2 : psiList) {
				 page.addAll(psiProduct2.getProductNameWithColor());
			}
		}
		page.add("noProductName");
		Map<String,Map<String, Map<String,SaleReport>>>  data = saleReportService.getSalesByProduct(saleReport, rateMap);
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
				
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				tip.put(key,DateUtils.getDate(start,"E"));
				start = DateUtils.addDays(start, 1);
			}
		}
		
		
	
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCell cell = null;
		String currencySymbol = com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(saleReport.getCurrencyType());
		
        List<String> countryList=new ArrayList<String>();
        List<String> countryListTable=new ArrayList<String>();
        if(StringUtils.isBlank(saleReport.getCountry())){
        	List<Dict> dictAll=DictUtils.getDictList("platform");
        	for (Dict dict2 : dictAll) {
				if(!"com.unitek".equals(dict2.getValue())){
					countryList.add(dict2.getValue());
					countryListTable.add(dict2.getLabel());
				}
			}
        }else if("eu".equals(saleReport.getCountry())){
        	countryList.addAll(Lists.newArrayList("de","fr","it","es","uk"));
        	countryListTable.addAll(Lists.newArrayList("德国","法国","意大利","西班牙","英国"));
        }else if("en".equals(saleReport.getCountry())){
        	countryList.addAll(Lists.newArrayList("com","ca","uk"));
        	countryListTable.addAll(Lists.newArrayList("美国","加拿大","英国"));
        }else if("unEn".equals(saleReport.getCountry())){
        	countryList.addAll(Lists.newArrayList("de","fr","it","es","jp"));
        	countryListTable.addAll(Lists.newArrayList("德国","法国","意大利","西班牙","日本"));
        }else{
        	countryList.add(saleReport.getCountry());
        	countryListTable=Lists.newArrayList(DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
        }
		List<String> title = new ArrayList<String>();
		
		title.add(0, "产品");
		if(StringUtils.isBlank(saleReport.getCountry())){
			title.add(1, "总计");
		}else{
			if("eu".equals(saleReport.getCountry())){
        		title.add(1,"欧洲");
        	}else if("en".equals(saleReport.getCountry())){
        		title.add(1,"英语国家");
        	}else if("unEn".equals(saleReport.getCountry())){
        		title.add(1,"非英语国家");
        	}else{
        		title.add(1,DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
        	}
			
		}
		if("1".equals(saleReport.getSearchType())){
			title.add(2, "星期");
		}else{
			title.add(2, "区间");
		}
		
		title.add("销售额("+currencySymbol+")");
		title.add("确认销售额("+currencySymbol+")");
		title.add("销售");
		title.add("确认销量");
		if(StringUtils.isBlank(saleReport.getCountry())||"eu".equals(saleReport.getCountry())||"en".equals(saleReport.getCountry())||"unEn".equals(saleReport.getCountry())){
			for(int i=1;i<=countryList.size();i++){
				title.add("销售额("+currencySymbol+")");title.add("确认销售额("+currencySymbol+")");title.add("去促销后("+currencySymbol+")");title.add("促销比‰");
				title.add("销量");title.add("确认销量");title.add("去促销后");title.add("促销比‰");
			}
		}else{
			title.add("销售额("+currencySymbol+")");title.add("确认销售额("+currencySymbol+")");title.add("去促销后("+currencySymbol+")");title.add("促销比‰");
			title.add("销量");title.add("确认销量");title.add("去促销后");title.add("促销比‰");
		}
		
		int num=0;
		int startIndex=7;
		int endIndex=14;
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i==0||i==1||i==2||i==3||i==4||i==5||i==6){
			  cell.setCellValue(title.get(i));
			}else{
				if(StringUtils.isBlank(saleReport.getCountry())||"eu".equals(saleReport.getCountry())||"en".equals(saleReport.getCountry())||"unEn".equals(saleReport.getCountry())){
					if(i==startIndex){
						cell.setCellValue(countryListTable.get(num++));
						startIndex+=8;
					}
					if(i==endIndex){
						sheet.addMergedRegion(new CellRangeAddress(0, 0,i-7,i));
						endIndex+=8;
					}
				}else{
		        		cell.setCellValue(DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
					    sheet.addMergedRegion(new CellRangeAddress(0, 0,7,14));
				}
			}
			
		}
		row = sheet.createRow(1);
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i==0||i==1||i==2||i==3||i==4||i==5||i==6){
				sheet.addMergedRegion(new CellRangeAddress(0, 1,i,i));
			}else{
			   cell.setCellValue(title.get(i));
			}
		}
		int rownum=2;
		for (String dateKey : xAxis) {
		//	int c=0;
			int startRow=rownum;
		   for (String name : page) {
			   /*float passSale=0f;
			   for(int i=0;i<countryList.size();i++){
					String countryKey=countryList.get(i);
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSales()!=null&&data.get(countryKey).get(dateKey).get(name).getSales()>0){
						passSale+=data.get(countryKey).get(dateKey).get(name).getSales();
	        		}
			   }	
			   if(passSale>0){*/
				   row = sheet.createRow(rownum++);
				   row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(name);
					//if(c==0){
						row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(dateKey+("1".equals(saleReport.getSearchType())?"日":("2".equals(saleReport.getSearchType())?"周":"月")));
						row.getCell(1).setCellStyle(contentStyle);
						row.createCell(2,Cell.CELL_TYPE_STRING).setCellValue(tip.get(dateKey));
						row.getCell(2).setCellStyle(contentStyle);
					/*}else{
						row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("");
						row.getCell(0).setCellStyle(contentStyle);
						row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue("");
						row.getCell(1).setCellStyle(contentStyle);
					}*/
					//c++;
					
					row.getCell(2).setCellStyle(contentStyle);
					
					row.createCell(3,Cell.CELL_TYPE_NUMERIC);
					row.getCell(3).setCellStyle(cellStyle);
					row.createCell(4,Cell.CELL_TYPE_NUMERIC);
					row.getCell(4).setCellStyle(cellStyle);
					row.createCell(5,Cell.CELL_TYPE_NUMERIC);
					row.getCell(5).setCellStyle(contentStyle);
					row.createCell(6,Cell.CELL_TYPE_NUMERIC);
					row.getCell(6).setCellStyle(contentStyle);
					
					int cellnum=7;
					Float totalSales=0f;
					Float totalSureSales=0f;
					int totalVolume=0;
					int totalSureVolume=0;
					for(int i=0;i<countryList.size();i++){
						String countryKey=countryList.get(i);
						if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSales()!=null&&data.get(countryKey).get(dateKey).get(name).getSales()>0){
							totalSales+=data.get(countryKey).get(dateKey).get(name).getSales();
							row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(data.get(countryKey).get(dateKey).get(name).getSales()).setScale(2,4).floatValue());
		        		}else{
		        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue(0);
		        		}
						row.getCell(cellnum-1).setCellStyle(cellStyle);
						if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSureSales()!=null&&data.get(countryKey).get(dateKey).get(name).getSureSales()>0){
							totalSureSales+=data.get(countryKey).get(dateKey).get(name).getSureSales();
							row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(data.get(countryKey).get(dateKey).get(name).getSureSales()).setScale(2,4).floatValue());
		        		}else{
		        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue(0);
		        		}
						row.getCell(cellnum-1).setCellStyle(cellStyle);
						if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getRealSales()!=null&&data.get(countryKey).get(dateKey).get(name).getRealSales()>0){
							float realSales=new BigDecimal(data.get(countryKey).get(dateKey).get(name).getRealSales()).setScale(2,4).floatValue();
							row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(realSales);
		        		}else{
		        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue(0);
		        		}
						row.getCell(cellnum-1).setCellStyle(cellStyle);
						if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSureSales()!=null&&data.get(countryKey).get(dateKey).get(name).getSureSales()>0){
							if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getRealSales()!=null){
								float rate=(data.get(countryKey).get(dateKey).get(name).getSureSales()-data.get(countryKey).get(dateKey).get(name).getRealSales())*1000/data.get(countryKey).get(dateKey).get(name).getSureSales();
								row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(new BigDecimal(rate).setScale(2,4).doubleValue()));
							}else{
								row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
							}
		        		}else{
		        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue(0);
		        		}
						row.getCell(cellnum-1).setCellStyle(contentStyle);
						if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSalesVolume()!=null&&data.get(countryKey).get(dateKey).get(name).getSalesVolume()>0){
							totalVolume+=data.get(countryKey).get(dateKey).get(name).getSalesVolume();
							row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).get(name).getSalesVolume());
		        		}else{
		        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue(0);
		        		}
						row.getCell(cellnum-1).setCellStyle(contentStyle);
						if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSureSalesVolume()!=null&&data.get(countryKey).get(dateKey).get(name).getSureSalesVolume()>0){
							totalSureVolume+=data.get(countryKey).get(dateKey).get(name).getSureSalesVolume();
							row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).get(name).getSureSalesVolume());
		        		}else{
		        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue(0);
		        		}
						row.getCell(cellnum-1).setCellStyle(contentStyle);
						if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getRealSalesVolume()!=null&&data.get(countryKey).get(dateKey).get(name).getRealSalesVolume()>0){
							row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).get(name).getRealSalesVolume());
		        		}else{
		        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue(0);
		        		}
						row.getCell(cellnum-1).setCellStyle(contentStyle);
						if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSureSalesVolume()!=null&&data.get(countryKey).get(dateKey).get(name).getSureSalesVolume()>0){
							if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getRealSalesVolume()!=null){
								float rate=(data.get(countryKey).get(dateKey).get(name).getSureSalesVolume()-data.get(countryKey).get(dateKey).get(name).getRealSalesVolume())*1000.0f/data.get(countryKey).get(dateKey).get(name).getSureSalesVolume();
								row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(new BigDecimal(rate).setScale(2,4).doubleValue()));
							}else{
								row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
							}
		        		}else{
		        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue(0);
		        		}
						row.getCell(cellnum-1).setCellStyle(contentStyle);
					}
				  
					row.getCell(3).setCellValue(totalSales);
					row.getCell(4).setCellValue(totalSureSales);
					row.getCell(5).setCellValue(totalVolume);
					row.getCell(6).setCellValue(totalSureVolume);
					
			  // }
			}
		 /*  if(page.size()>0){
			   sheet.addMergedRegion(new CellRangeAddress(startRow, rownum-1,0,0));
			   sheet.addMergedRegion(new CellRangeAddress(startRow, rownum-1,1,1));
		   }*/
		
		}
		
		for (int i = 0; i < title.size(); i++) {
   		   sheet.autoSizeColumn((short)i,true);
	    }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "分商品名汇总统计_"+(saleReport.getCountry()==null?"":saleReport.getCountry()) + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	@RequestMapping(value = {"exportByDateCountry"})
	public String exportByDateCountry(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		String productTypeTemp=saleReport.getProductType();
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		if(StringUtils.isBlank(saleReport.getGroupName())){
			saleReport.setProductType(null);
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isBlank(saleReport.getProductType())){
			List<Dict> dict=psiTypeGroupService.getProductType(saleReport.getGroupName());
			List<String> typeList=new ArrayList<String>();
			for (Dict dict2 : dict) {
				typeList.add("'"+dict2.getValue()+"'");
			}
			
			if(typeList!=null&&typeList.size()>0){
			    saleReport.setProductType(StringUtils.join(typeList.toArray(),",")); 
			}else{
				saleReport.setProductType("'no'");
			}  
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isNotBlank(saleReport.getProductType())){
			saleReport.setProductType("'"+saleReport.getProductType()+"'");
		}
		Map<String, Map<String, SaleReport>>  data = saleReportService.getSales(saleReport, rateMap);
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
				
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				tip.put(key,DateUtils.getDate(start,"E"));
				start = DateUtils.addDays(start, 1);
			}
		}  
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCell cell = null;
        List<String> countryList=Lists.newArrayList("eu","en");
        List<String> countryListTable=Lists.newArrayList("欧洲","英语国家");
        
        String currencySymbol = com.springrain.erp.common.utils.StringUtils.
        		getCurrencySymbolByType(saleReport.getCurrencyType());

        List<Dict> dictAll=DictUtils.getDictList("platform");
    	for (Dict dict2 : dictAll) {
			if(!"com.unitek".equals(dict2.getValue())){
				countryList.add(dict2.getValue());
				countryListTable.add(dict2.getLabel());
			}
		}
		List<String> title = Lists.newArrayList("总计");
		
		if("1".equals(saleReport.getSearchType())){
			title.add("星期");
		}else{
			title.add("区间");
		}
		title.add("销售额("+currencySymbol+")");title.add("确认销售额("+currencySymbol+")");title.add("去促销后("+currencySymbol+")");
		title.add("销量");title.add("确认销量");title.add("去促销后");
		for(int i=1;i<=countryList.size();i++){
			title.add("销售额("+currencySymbol+")");title.add("确认销售额("+currencySymbol+")");title.add("去促销后("+currencySymbol+")");title.add("促销比‰");
			title.add("销量");title.add("确认销量");title.add("去促销后");title.add("促销比‰");
		}
		int num=0;
		int startIndex=8;
		int endIndex=15;
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i==0||i==1||i==2||i==3||i==4||i==5||i==6||i==7){
			  cell.setCellValue(title.get(i));
			}else if(i==startIndex){
				cell.setCellValue(countryListTable.get(num++));
				startIndex+=8;
			}else if(i==endIndex){
				sheet.addMergedRegion(new CellRangeAddress(0, 0,i-7,i));
				endIndex+=8;
			}
		}
		row = sheet.createRow(1);
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i==0||i==1||i==2||i==3||i==4||i==5||i==6||i==7){
				sheet.addMergedRegion(new CellRangeAddress(0, 1,i,i));
			}else{
			   cell.setCellValue(title.get(i));
			}
		}
		int rownum=2;
		for (String dateKey : xAxis) {
			row = sheet.createRow(rownum++);
			row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(dateKey+("1".equals(saleReport.getSearchType())?"日":("2".equals(saleReport.getSearchType())?"周":"月")));
			row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(tip.get(dateKey));
			row.createCell(2,Cell.CELL_TYPE_NUMERIC);
			row.createCell(3,Cell.CELL_TYPE_NUMERIC);
			row.createCell(4,Cell.CELL_TYPE_NUMERIC);
			row.createCell(5,Cell.CELL_TYPE_NUMERIC);
			row.createCell(6,Cell.CELL_TYPE_NUMERIC);
			row.createCell(7,Cell.CELL_TYPE_NUMERIC);
			Float totalSales=0f;
			Float sureTotalSales=0f;
			Float realTotalSales=0f;
			Integer volume=0;
			Integer sureVolume=0;
			Integer realVolume=0;
			int cellnum=8;
			for(int i=0;i<countryList.size();i++){
				String countryKey=countryList.get(i);
				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getSales()!=null&&data.get(countryKey).get(dateKey).getSales()>0){
					float saleValue=new BigDecimal(data.get(countryKey).get(dateKey).getSales()).setScale(2,4).floatValue();
					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(saleValue);
					if(!"eu".equals(countryKey)&&!"en".equals(countryKey)){
						totalSales+=data.get(countryKey).get(dateKey).getSales();
					}
        		}else{
        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
        		}
				row.getCell(cellnum-1).setCellStyle(cellStyle);
				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getSureSales()!=null&&data.get(countryKey).get(dateKey).getSureSales()>0){
					float sureSaleValue=new BigDecimal(data.get(countryKey).get(dateKey).getSureSales()).setScale(2,4).floatValue();
					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(sureSaleValue);
					if(!"eu".equals(countryKey)&&!"en".equals(countryKey)){
						sureTotalSales+=data.get(countryKey).get(dateKey).getSureSales();
					}
        		}else{
        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
        		}
				row.getCell(cellnum-1).setCellStyle(cellStyle);
				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getRealSales()!=null&&data.get(countryKey).get(dateKey).getRealSales()>0){
					float realSales=new BigDecimal(data.get(countryKey).get(dateKey).getRealSales()).setScale(2,4).floatValue();
					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(realSales);
					if(!"eu".equals(countryKey)&&!"en".equals(countryKey)){
						realTotalSales+=data.get(countryKey).get(dateKey).getRealSales();
					}
        		}else{
        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
        		}
				row.getCell(cellnum-1).setCellStyle(cellStyle);
				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getSureSales()!=null&&data.get(countryKey).get(dateKey).getSureSales()>0){
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getRealSales()!=null){
						float rate=(data.get(countryKey).get(dateKey).getSureSales()-data.get(countryKey).get(dateKey).getRealSales())*1000/data.get(countryKey).get(dateKey).getSureSales();
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(new BigDecimal(rate).setScale(2,4).doubleValue()));
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
        		}else{
        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
        		}
				row.getCell(cellnum-1).setCellStyle(cellStyle);
				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getSalesVolume()!=null&&data.get(countryKey).get(dateKey).getSalesVolume()>0){
					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).getSalesVolume());
					if(!"eu".equals(countryKey)&&!"en".equals(countryKey)){
						volume+=data.get(countryKey).get(dateKey).getSalesVolume();
					}
        		}else{
        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
        		}
				row.getCell(cellnum-1).setCellStyle(contentStyle);
				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getSureSalesVolume()!=null&&data.get(countryKey).get(dateKey).getSureSalesVolume()>0){
					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).getSureSalesVolume());
					if(!"eu".equals(countryKey)&&!"en".equals(countryKey)){
						sureVolume+=data.get(countryKey).get(dateKey).getSureSalesVolume();
					}
        		}else{
        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
        		}
				row.getCell(cellnum-1).setCellStyle(contentStyle);
				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getRealSalesVolume()!=null&&data.get(countryKey).get(dateKey).getRealSalesVolume()>0){
					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).getRealSalesVolume());
					if(!"eu".equals(countryKey)&&!"en".equals(countryKey)){
						realVolume+=data.get(countryKey).get(dateKey).getRealSalesVolume();
					}
        		}else{
        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
        		}
				row.getCell(cellnum-1).setCellStyle(contentStyle);
				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getSureSalesVolume()!=null&&data.get(countryKey).get(dateKey).getSureSalesVolume()>0){
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getRealSalesVolume()!=null){
						float rate=(data.get(countryKey).get(dateKey).getSureSalesVolume()-data.get(countryKey).get(dateKey).getRealSalesVolume())*1000.0f/data.get(countryKey).get(dateKey).getSureSalesVolume();
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(new BigDecimal(rate).setScale(2,4).doubleValue()));
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
        		}else{
        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
        		}
				row.getCell(cellnum-1).setCellStyle(contentStyle);
				
				row.getCell(2).setCellValue(totalSales);
				row.getCell(2).setCellStyle(cellStyle);
				row.getCell(3).setCellValue(sureTotalSales);
				row.getCell(3).setCellStyle(cellStyle);
				row.getCell(4).setCellValue(realTotalSales);
				row.getCell(4).setCellStyle(cellStyle);
				row.getCell(5).setCellValue(volume);
				row.getCell(5).setCellStyle(contentStyle);
				row.getCell(6).setCellValue(sureVolume);
				row.getCell(6).setCellStyle(contentStyle);
				row.getCell(7).setCellValue(realVolume);
				row.getCell(7).setCellStyle(contentStyle);
				
			}
		}
		
		for (int i = 0; i < title.size(); i++) {
   		   sheet.autoSizeColumn((short)i,true);
	    }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "页面数据分所有国家统计" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
		
	@RequestMapping(value = {"exportOriginal"})
	public String exportOriginal(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		String productTypeTemp=saleReport.getProductType();
		String line="";
		if(StringUtils.isBlank(saleReport.getGroupName())){
			saleReport.setProductType(null);
			line="ALL";
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isBlank(saleReport.getProductType())){
			List<Dict> dict=psiTypeGroupService.getProductType(saleReport.getGroupName());
			List<String> typeList=new ArrayList<String>();
			for (Dict dict2 : dict) {
				typeList.add("'"+dict2.getValue()+"'");
			}
			if(typeList!=null&&typeList.size()>0){
			    saleReport.setProductType(StringUtils.join(typeList.toArray(),",")); 
			}else{
				saleReport.setProductType("'no'");
			}  
			if("unGrouped".equals(saleReport.getGroupName())){
				line="unGrouped";
			}else{
				line=psiTypeGroupService.get(saleReport.getGroupName()).getName();
			}
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isNotBlank(saleReport.getProductType())){
			saleReport.setProductType("'"+saleReport.getProductType()+"'");
			line=psiTypeGroupService.get(saleReport.getGroupName()).getName()+saleReport.getProductType();
		}
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String, Map<String, SaleReport>>  data = saleReportService.getSales(saleReport, rateMap);
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
				
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				tip.put(key,DateUtils.getDate(start,"E"));
				start = DateUtils.addDays(start, 1);
			}
		}
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCell cell = null;
		String currencySymbol = com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(saleReport.getCurrencyType());
		
			List<String> title = Lists.newArrayList("销售额("+currencySymbol+")","确认销量("+currencySymbol+")","去促销后("+currencySymbol+")","促销比‰","销量","确认销量","去促销后","促销比‰");
			String countryKey="";
			if(StringUtils.isBlank(saleReport.getCountry())){
				title.add(0, "总计");
				countryKey="total";
			}else{
				title.add(0,DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
				countryKey=saleReport.getCountry();
			}
			if("1".equals(saleReport.getSearchType())){
				title.add(1, "星期");
			}else{
				title.add(1, "区间");
			}
			for(int i=0;i<title.size();i++){
				cell = row.createCell(i);
				cell.setCellStyle(style);
				cell.setCellValue(title.get(i));
			}
			int rownum=1;
		    for (String dateKey : xAxis) {
        		row = sheet.createRow(rownum++);
    			row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(dateKey+("1".equals(saleReport.getSearchType())?"日":("2".equals(saleReport.getSearchType())?"周":"月")));
    			row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(tip.get(dateKey));
    			int cellnum=2;
                    if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getSales()!=null&&data.get(countryKey).get(dateKey).getSales()>0){
    					float saleValue=new BigDecimal(data.get(countryKey).get(dateKey).getSales()).setScale(2,4).floatValue();
    					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(saleValue);
            		}else{
            			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
            		}
    				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getSureSales()!=null&&data.get(countryKey).get(dateKey).getSureSales()>0){
    					float sureSaleValue=new BigDecimal(data.get(countryKey).get(dateKey).getSureSales()).setScale(2,4).floatValue();
    					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(sureSaleValue);
            		}else{
            			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
            		}
    				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getRealSales()!=null&&data.get(countryKey).get(dateKey).getRealSales()>0){
    					float realSales=new BigDecimal(data.get(countryKey).get(dateKey).getRealSales()).setScale(2,4).floatValue();
    					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(realSales);
            		}else{
            			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
            		}
    				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getSureSales()!=null&&data.get(countryKey).get(dateKey).getSureSales()>0){
    					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getRealSales()!=null){
    						float rate=(data.get(countryKey).get(dateKey).getSureSales()-data.get(countryKey).get(dateKey).getRealSales())*1000/data.get(countryKey).get(dateKey).getSureSales();
    						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(new BigDecimal(rate).setScale(2,4).doubleValue()));
    					}else{
    						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
    					}
            		}else{
            			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
            		}
    				
    				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getSalesVolume()!=null&&data.get(countryKey).get(dateKey).getSalesVolume()>0){
    					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).getSalesVolume());
            		}else{
            			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
            		}
    				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getSureSalesVolume()!=null&&data.get(countryKey).get(dateKey).getSureSalesVolume()>0){
    					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).getSureSalesVolume());
            		}else{
            			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
            		}
    				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getRealSalesVolume()!=null&&data.get(countryKey).get(dateKey).getRealSalesVolume()>0){
    					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).getRealSalesVolume());
            		}else{
            			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
            		}
    				
    				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getSureSalesVolume()!=null&&data.get(countryKey).get(dateKey).getSureSalesVolume()>0){
    					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).getRealSalesVolume()!=null){
    						float rate=(data.get(countryKey).get(dateKey).getSureSalesVolume()-data.get(countryKey).get(dateKey).getRealSalesVolume())*1000.0f/data.get(countryKey).get(dateKey).getSureSalesVolume();
    						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(new BigDecimal(rate).setScale(2,4).doubleValue()));
    					}else{
    						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
    					}
            		}else{
            			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
            		}
			}
		    for (int i=0;i<xAxis.size();i++) {
	     	       for(int j=0;j<title.size();j++){
	     	    	  if(j==2||j==3||j==4){
	     	    		 sheet.getRow(i+1).getCell(j).setCellStyle(cellStyle);
	     	    	  }else{
	     	    	     sheet.getRow(i+1).getCell(j).setCellStyle(contentStyle);
	     	    	  } 
	     	       }
	        }
		    for(int i=0;i<title.size();i++){
		    	sheet.autoSizeColumn((short)i);
			}
		    try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	            String fileName =("1".equals(saleReport.getSearchType())?"日":("2".equals(saleReport.getSearchType())?"周":"月"))+line+"页面数据" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+countryKey+fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
			 
	}
	
	
	@RequestMapping(value = {"exportStatistics"})
	public String exportStatistics(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		String productTypeTemp=saleReport.getProductType();
		PsiProduct psiProduct=new PsiProduct();
		List<String> page=new ArrayList<String>();
		if(StringUtils.isBlank(saleReport.getGroupName())){
			saleReport.setProductType(null);
			if(StringUtils.isBlank(saleReport.getCountry())){
				page =psiProductService.findCountryProduct(psiProduct);
			}else{
				psiProduct.setPlatform(saleReport.getCountry());
				page =psiProductService.findCountryProduct(psiProduct);
			}
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isBlank(saleReport.getProductType())){
			List<Dict> dict=psiTypeGroupService.getProductType(saleReport.getGroupName());
			List<String> typeList=new ArrayList<String>();
			Set<String> nameSet=new HashSet<String>();
			for (Dict dict2 : dict) {
				typeList.add("'"+dict2.getValue()+"'");
				nameSet.add(dict2.getValue());
			}
			if(typeList!=null&&typeList.size()>0){
			    saleReport.setProductType(StringUtils.join(typeList.toArray(),",")); 
			}else{
				saleReport.setProductType("'no'");
			}
			List<PsiProduct> psiList=psiProductService.findAllByCountryByType(saleReport.getCountry(),nameSet);
			for (PsiProduct psiProduct2 : psiList) {
				 page.addAll(psiProduct2.getProductNameWithColor());
			}
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isNotBlank(saleReport.getProductType())){
			saleReport.setProductType("'"+saleReport.getProductType()+"'");
			List<PsiProduct> psiList=psiProductService.findAllByCountry(saleReport.getCountry(),productTypeTemp);
			for (PsiProduct psiProduct2 : psiList) {
				 page.addAll(psiProduct2.getProductNameWithColor());
			}
		}

		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String,Map<String, Map<String,SaleReport>>>  data = saleReportService.getSalesByProduct(saleReport, rateMap);
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
				
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				tip.put(key,DateUtils.getDate(start,"E"));
				start = DateUtils.addDays(start, 1);
			}
		}
	
			
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCell cell = null;
		String currencySymbol = com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(saleReport.getCurrencyType());

		List<String> title = new ArrayList<String>();
		title.add("产品名称");
		if(StringUtils.isBlank(saleReport.getCountry())){
			title.add("总计");
		}else if("eu".equals(saleReport.getCountry())){
			title.add( "欧洲");
		}else if("en".equals(saleReport.getCountry())){
			title.add( "英语国家");
		}else{
			title.add(DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
		}
		if("1".equals(saleReport.getSearchType())){
			title.add("星期");
		}else{
			title.add("区间");
		}
		title.add("已确认销售额("+currencySymbol+")");title.add("去促销后("+currencySymbol+")");title.add("已确认销量");title.add("去促销后");
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
		}
		int rownum=1;
		String countryKey=saleReport.getCountry();
		List<String> countryList=new ArrayList<String>();
		if(StringUtils.isBlank(saleReport.getCountry())){
			List<Dict> dictAll=DictUtils.getDictList("platform");
	    	for (Dict dict2 : dictAll) {
				if(!"com.unitek".equals(dict2.getValue())){
					countryList.add(dict2.getValue());
				}
			}
		}else if("eu".equals(saleReport.getCountry())){
			countryList.addAll(Lists.newArrayList("de","fr","it","es","uk"));
		}else if("en".equals(saleReport.getCountry())){
			countryList.addAll(Lists.newArrayList("com","ca","uk"));
		}else if("unEn".equals(saleReport.getCountry())){
			countryList.addAll(Lists.newArrayList("de","fr","it","es","jp"));
		}
        
		for (String name : page) {  
		     for (String dateKey : xAxis) {
				row = sheet.createRow(rownum++);
				row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(name);
				row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(dateKey+("1".equals(saleReport.getSearchType())?"日":("2".equals(saleReport.getSearchType())?"周":"月")));
				row.createCell(2,Cell.CELL_TYPE_STRING).setCellValue(tip.get(dateKey));
				
				Float sureSales=0f;
				Float realSales=0f;
				int sureSalesVolume=0;
				int realSalesVolume=0;
				if(StringUtils.isBlank(saleReport.getCountry())||"eu".equals(saleReport.getCountry())||"en".equals(saleReport.getCountry())||"unEn".equals(saleReport.getCountry())){
					for (String key : countryList) {
						if(data.get(key)!=null&&data.get(key).get(dateKey)!=null&&data.get(key).get(dateKey).get(name)!=null&&data.get(key).get(dateKey).get(name).getSureSales()!=null&&data.get(key).get(dateKey).get(name).getSureSales()>0){
							sureSales+=data.get(key).get(dateKey).get(name).getSureSales();
			    		}
						if(data.get(key)!=null&&data.get(key).get(dateKey)!=null&&data.get(key).get(dateKey).get(name)!=null&&data.get(key).get(dateKey).get(name).getSureSalesVolume()!=null&&data.get(key).get(dateKey).get(name).getSureSalesVolume()>0){
							sureSalesVolume+=data.get(key).get(dateKey).get(name).getSureSalesVolume();
			    		}
						if(data.get(key)!=null&&data.get(key).get(dateKey)!=null&&data.get(key).get(dateKey).get(name)!=null&&data.get(key).get(dateKey).get(name).getRealSales()!=null&&data.get(key).get(dateKey).get(name).getRealSales()>0){
							realSales+=data.get(key).get(dateKey).get(name).getRealSales();
			    		}
						if(data.get(key)!=null&&data.get(key).get(dateKey)!=null&&data.get(key).get(dateKey).get(name)!=null&&data.get(key).get(dateKey).get(name).getRealSalesVolume()!=null&&data.get(key).get(dateKey).get(name).getRealSalesVolume()>0){
							realSalesVolume+=data.get(key).get(dateKey).get(name).getRealSalesVolume();
			    		}
					}
					if(sureSales>0){
						row.createCell(3,Cell.CELL_TYPE_STRING).setCellValue(new BigDecimal(sureSales).setScale(2,4).floatValue());
					}else{
						row.createCell(3,Cell.CELL_TYPE_STRING).setCellValue(0);
					}
					if(realSales>0){
						row.createCell(4,Cell.CELL_TYPE_STRING).setCellValue(new BigDecimal(realSales).setScale(2,4).floatValue());
					}else{
						row.createCell(4,Cell.CELL_TYPE_STRING).setCellValue(0);
					}
				
					if(sureSalesVolume>0){
						row.createCell(5,Cell.CELL_TYPE_STRING).setCellValue(sureSalesVolume);
					}else{
						row.createCell(5,Cell.CELL_TYPE_STRING).setCellValue(0);
					}
					if(realSalesVolume>0){
						row.createCell(6,Cell.CELL_TYPE_STRING).setCellValue(realSalesVolume);
					}else{
						row.createCell(6,Cell.CELL_TYPE_STRING).setCellValue(0);
					}
					
				}else{
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSureSales()!=null&&data.get(countryKey).get(dateKey).get(name).getSureSales()>0){
						row.createCell(3,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(data.get(countryKey).get(dateKey).get(name).getSureSales()).setScale(2,4).floatValue());
		    		}else{
		    			row.createCell(3,Cell.CELL_TYPE_STRING).setCellValue(0);
		    		}
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getRealSales()!=null&&data.get(countryKey).get(dateKey).get(name).getRealSales()>0){
						row.createCell(4,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(data.get(countryKey).get(dateKey).get(name).getRealSales()).setScale(2,4).floatValue());
		    		}else{
		    			row.createCell(4,Cell.CELL_TYPE_STRING).setCellValue(0);
		    		}
					
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSureSalesVolume()!=null&&data.get(countryKey).get(dateKey).get(name).getSureSalesVolume()>0){
						row.createCell(5,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).get(name).getSureSalesVolume());
		    		}else{
		    			row.createCell(5,Cell.CELL_TYPE_STRING).setCellValue(0);
		    		}
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getRealSalesVolume()!=null&&data.get(countryKey).get(dateKey).get(name).getRealSalesVolume()>0){
						row.createCell(6,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).get(name).getRealSalesVolume());
		    		}else{
		    			row.createCell(6,Cell.CELL_TYPE_STRING).setCellValue(0);
		    		}
					
				}
				
		   }	
		}
		
		for (int i=0;i<rownum-1;i++) {
  	       for(int j=0;j<title.size();j++){
  	    	  if(j==3||j==4){
  	    		 sheet.getRow(i+1).getCell(j).setCellStyle(cellStyle);
  	    	  }else{
  	    	     sheet.getRow(i+1).getCell(j).setCellStyle(contentStyle);
  	    	  } 
  	       }
       }
		for (int i = 0; i < title.size(); i++) {
   		   sheet.autoSizeColumn((short)i);
	    }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "分产品详情统计_"+(saleReport.getCountry()==null?"":saleReport.getCountry()) + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
			 
	}
	
	
	
	@RequestMapping(value = {"exportProductLineProduct"})
	public String exportProductLineProduct(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		String productTypeTemp=saleReport.getProductType();
	
		if(StringUtils.isBlank(saleReport.getGroupName())){
			saleReport.setProductType(null);
		
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isBlank(saleReport.getProductType())){
			List<Dict> dict=psiTypeGroupService.getProductType(saleReport.getGroupName());
			List<String> typeList=new ArrayList<String>();
			Set<String> nameSet=new HashSet<String>();
			for (Dict dict2 : dict) {
				typeList.add("'"+dict2.getValue()+"'");
				nameSet.add(dict2.getValue());
			}
			if(typeList!=null&&typeList.size()>0){
			    saleReport.setProductType(StringUtils.join(typeList.toArray(),",")); 
			}else{
				saleReport.setProductType("'no'");
			}
			
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isNotBlank(saleReport.getProductType())){
			saleReport.setProductType("'"+saleReport.getProductType()+"'");
		}

		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String,Map<String, Map<String,SaleReport>>>  data = saleReportService.getSalesByProduct2(saleReport, rateMap);
		Map<String,String> tempMap=psiProductService.findAddMonth();
		
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
				
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				tip.put(key,DateUtils.getDate(start,"E"));
				start = DateUtils.addDays(start, 1);
			}
		}
	
			
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCell cell = null;
		String currencySymbol = com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(saleReport.getCurrencyType());

		List<String> title =Lists.newArrayList("产品","属性","日期","US","CA","UK","DE","FR","IT","ES","JP","MX","US SAlES","CA SAlES","UK SAlES","DE SAlES","FR SAlES","IT SAlES","ES SAlES","JP SAlES","MX SAlES");
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
		}
		int rownum=1;
		for (Map.Entry<String,Map<String,Map<String,SaleReport>>> dataTemp: data.entrySet()) {//country date name
			  String name=dataTemp.getKey();
			  for (Map.Entry<String,Map<String,SaleReport>> dateTemp: dataTemp.getValue().entrySet()) {
				  String date=dateTemp.getKey();
				  Map<String,SaleReport> temp=dateTemp.getValue();
				  row = sheet.createRow(rownum++);
				  int j=0;
				  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
				  row.getCell(j-1).setCellStyle(contentStyle);
				  String attr=tempMap.get(name);
				  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(attr==null?"":attr);
				  row.getCell(j-1).setCellStyle(contentStyle);
				  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
				  row.getCell(j-1).setCellStyle(contentStyle);
				  if(temp.get("com")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("com").getSureSalesVolume());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(contentStyle);
				  if(temp.get("ca")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("ca").getSureSalesVolume());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(contentStyle);
				  if(temp.get("uk")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("uk").getSureSalesVolume());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(contentStyle);
				  if(temp.get("de")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("de").getSureSalesVolume());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(contentStyle);
				  if(temp.get("fr")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("fr").getSureSalesVolume());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(contentStyle);
				  if(temp.get("it")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("it").getSureSalesVolume());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(contentStyle);
				  if(temp.get("es")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("es").getSureSalesVolume());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(contentStyle);
				  if(temp.get("jp")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("jp").getSureSalesVolume());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(contentStyle);
				  if(temp.get("mx")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("mx").getSureSalesVolume());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(contentStyle);
				  
				  
				  if(temp.get("com")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("com").getSureSales());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(cellStyle);
				  if(temp.get("ca")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("ca").getSureSales());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(cellStyle);
				  if(temp.get("uk")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("uk").getSureSales());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(cellStyle);
				  if(temp.get("de")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("de").getSureSales());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(cellStyle);
				  if(temp.get("fr")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("fr").getSureSales());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(cellStyle);
				  if(temp.get("it")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("it").getSureSales());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(cellStyle);
				  if(temp.get("es")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("es").getSureSales());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(cellStyle);
				  if(temp.get("jp")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("jp").getSureSales());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(cellStyle);
				  if(temp.get("mx")!=null){
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(temp.get("mx").getSureSales());
				  }else{
					  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.getCell(j-1).setCellStyle(cellStyle);
			  }
			
		}
		
		
		for (int i = 0; i < title.size(); i++) {
   		   sheet.autoSizeColumn((short)i);
	    }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "分产品线产品统计_"+(saleReport.getCountry()==null?"":saleReport.getCountry()) + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
			 
	}
	
	
	@RequestMapping(value = {"exportByProductTypeCountry"})
	public String exportByProductTypeCountry(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		String productTypeTemp=saleReport.getProductType();
		List<Dict> page =new ArrayList<Dict>();
		if(StringUtils.isBlank(saleReport.getGroupName())){
			saleReport.setProductType(null);
			page=DictUtils.getDictList("product_type");
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isBlank(saleReport.getProductType())){
			List<Dict> dict=psiTypeGroupService.getProductType(saleReport.getGroupName());
			List<String> typeList=new ArrayList<String>();
			for (Dict dict2 : dict) {
				typeList.add("'"+dict2.getValue()+"'");
			}
			page=psiTypeGroupService.getProductType(saleReport.getGroupName());
			if(typeList!=null&&typeList.size()>0){
			    saleReport.setProductType(StringUtils.join(typeList.toArray(),",")); 
			}else{
				saleReport.setProductType("'no'");
			}  
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isNotBlank(saleReport.getProductType())){
			saleReport.setProductType("'"+saleReport.getProductType()+"'");
			Dict d=new Dict();
			d.setValue(productTypeTemp);
			page.add(d);
		}
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String,Map<String, Map<String,SaleReport>>>  data = saleReportService.getSalesByProductType(saleReport, rateMap);
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
				
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				tip.put(key,DateUtils.getDate(start,"E"));
				start = DateUtils.addDays(start, 1);
			}
		}
	    
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCell cell = null;
		String currencySymbol = com.springrain.erp.common.utils.StringUtils.
							getCurrencySymbolByType(saleReport.getCurrencyType());
		
        List<String> countryList=new ArrayList<String>();
        List<String> countryListTable=new ArrayList<String>();
        if(StringUtils.isBlank(saleReport.getCountry())){
            List<Dict> dictAll=DictUtils.getDictList("platform");
        	for (Dict dict2 : dictAll) {
    			if(!"com.unitek".equals(dict2.getValue())){
    				countryList.add(dict2.getValue());
    			}
    		}
        	countryListTable=Lists.newArrayList("总计");
        }else if("eu".equals(saleReport.getCountry())){
        	countryList.addAll(Lists.newArrayList("de","fr","it","es","uk"));
        	countryListTable=Lists.newArrayList("欧洲");
        }else if("unEn".equals(saleReport.getCountry())){
        	countryList.addAll(Lists.newArrayList("de","fr","it","es","jp"));
        	countryListTable=Lists.newArrayList("非英语国家");
        }else if("en".equals(saleReport.getCountry())){
        	countryList.addAll(Lists.newArrayList("com","ca","uk"));
        	countryListTable=Lists.newArrayList("英语国家");
        }else{
        	countryList=Lists.newArrayList(saleReport.getCountry());
            countryListTable=Lists.newArrayList(DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
        }
		List<String> title = new ArrayList<String>();
		if(StringUtils.isBlank(saleReport.getCountry())){
			title.add(0, "总计");
		}else if("eu".equals(saleReport.getCountry())){
			title.add(0, "欧洲总计");
		}else if("en".equals(saleReport.getCountry())){
			title.add(0, "英语国家");
		}else if("unEn".equals(saleReport.getCountry())){
			title.add(0, "非英语国家");
		}else{
			title.add(0,DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
		}
		if("1".equals(saleReport.getSearchType())){
			title.add(1, "星期");
		}else{
			title.add(1, "区间");
		}
		title.add(2, "产品类型");
			title.add("销售额("+currencySymbol+")");title.add("销量");
		
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
		}
		int rownum=1;
		Collections.reverse(xAxis);
		for (String dateKey : xAxis) {
			int c=0;
			int startRow=rownum;
		   for (Dict dict : page) {
			String name=dict.getValue();
			row = sheet.createRow(rownum++);
			if(c==0){
				row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(dateKey+("1".equals(saleReport.getSearchType())?"日":("2".equals(saleReport.getSearchType())?"周":"月")));
				row.getCell(0).setCellStyle(contentStyle);
				row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(tip.get(dateKey));
				row.getCell(1).setCellStyle(contentStyle);
			}else{
				row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(0).setCellStyle(contentStyle);
				row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(1).setCellStyle(contentStyle);
			}
			c++;
			row.createCell(2,Cell.CELL_TYPE_STRING).setCellValue(name);
			row.getCell(2).setCellStyle(contentStyle);
			int cellnum=3;
			int salesVolume=0;
			float sales=0f;
			for(int i=0;i<countryList.size();i++){
				String countryKey=countryList.get(i);
				if(StringUtils.isBlank(saleReport.getCountry())||"eu".equals(saleReport.getCountry())||"en".equals(saleReport.getCountry())||"unEn".equals(saleReport.getCountry())){
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSales()!=null&&data.get(countryKey).get(dateKey).get(name).getSales()>0){
						sales+=data.get(countryKey).get(dateKey).get(name).getSales();
	        		}
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSalesVolume()!=null&&data.get(countryKey).get(dateKey).get(name).getSalesVolume()>0){
						salesVolume+=data.get(countryKey).get(dateKey).get(name).getSalesVolume();
	        		}
				}else{
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSales()!=null&&data.get(countryKey).get(dateKey).get(name).getSales()>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(data.get(countryKey).get(dateKey).get(name).getSales()).setScale(2,4).floatValue());
	        		}else{
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
					row.getCell(cellnum-1).setCellStyle(cellStyle);
					
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSalesVolume()!=null&&data.get(countryKey).get(dateKey).get(name).getSalesVolume()>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).get(name).getSalesVolume());
	        		}else{
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
					row.getCell(cellnum-1).setCellStyle(contentStyle);
				}
			}
			if(StringUtils.isBlank(saleReport.getCountry())||"eu".equals(saleReport.getCountry())||"en".equals(saleReport.getCountry())||"unEn".equals(saleReport.getCountry())){
                if(sales>0){
                	row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue(new BigDecimal(sales).setScale(2,4).floatValue());
				}else{
					row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
                row.getCell(cellnum-1).setCellStyle(cellStyle);
				if(salesVolume>0){
					row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);
				}else{
					row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				row.getCell(cellnum-1).setCellStyle(contentStyle);
			}
		  }
		   if(page.size()>0){
			   sheet.addMergedRegion(new CellRangeAddress(startRow, rownum-1,0,0));
			   sheet.addMergedRegion(new CellRangeAddress(startRow, rownum-1,1,1));
		   }
		}
		
		for (int i = 0; i < title.size(); i++) {
   		   sheet.autoSizeColumn((short)i);
	    }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "分产品类型统计_"+(saleReport.getCountry()==null?"":saleReport.getCountry()) + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	
	@RequestMapping(value = {"exportByProductTypeAllCountry"})
	public String exportByProductTypeAllCountry(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		String productTypeTemp=saleReport.getProductType();
		List<Dict> page =new ArrayList<Dict>();
		if(StringUtils.isBlank(saleReport.getGroupName())){
			saleReport.setProductType(null);
			page=DictUtils.getDictList("product_type");
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isBlank(saleReport.getProductType())){
			List<Dict> dict=psiTypeGroupService.getProductType(saleReport.getGroupName());
			List<String> typeList=new ArrayList<String>();
			for (Dict dict2 : dict) {
				typeList.add("'"+dict2.getValue()+"'");
			}
			page=psiTypeGroupService.getProductType(saleReport.getGroupName());
			if(typeList!=null&&typeList.size()>0){
			    saleReport.setProductType(StringUtils.join(typeList.toArray(),",")); 
			}else{
				saleReport.setProductType("'no'");
			}  
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isNotBlank(saleReport.getProductType())){
			saleReport.setProductType("'"+saleReport.getProductType()+"'");
			Dict d=new Dict();
			d.setValue(productTypeTemp);
			page.add(d);
		}
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String,Map<String, Map<String,SaleReport>>>  data = saleReportService.getSalesByProductType(saleReport, rateMap);
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
				
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				tip.put(key,DateUtils.getDate(start,"E"));
				start = DateUtils.addDays(start, 1);
			}
		}
	    
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCell cell = null;
		String currencySymbol = com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(saleReport.getCurrencyType());
		
        List<String> countryList=Lists.newArrayList("eu","en");
        List<String> countryListTable=Lists.newArrayList("欧洲","英语国家");
        if(StringUtils.isBlank(saleReport.getCountry())){
            List<Dict> dictAll=DictUtils.getDictList("platform");
        	for (Dict dict2 : dictAll) {
    			if(!"com.unitek".equals(dict2.getValue())){
    				countryList.add(dict2.getValue());
    				countryListTable.add(dict2.getLabel());
    			}
    		}
        }else{
        	countryList=Lists.newArrayList(saleReport.getCountry());
            countryListTable=Lists.newArrayList(DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
        }
		List<String> title = new ArrayList<String>();
		if(StringUtils.isBlank(saleReport.getCountry())){
			title.add(0, "总计");
		}else{
			title.add(0,DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
		}
		if("1".equals(saleReport.getSearchType())){
			title.add(1, "星期");
		}else{
			title.add(1, "区间");
		}
		title.add(2, "产品类型");
		if(StringUtils.isBlank(saleReport.getCountry())){
			for(int i=1;i<=countryList.size();i++){
				title.add("销售额("+currencySymbol+")");title.add("销量");
			}
		}else{
			title.add("销售额("+currencySymbol+")");title.add("销量");
		}
		int num=0;
		int startIndex=3;
		int endIndex=4;
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i==0||i==1||i==2){
			  cell.setCellValue(title.get(i));
			}else{
				if(StringUtils.isBlank(saleReport.getCountry())){
					if(i==startIndex){
						cell.setCellValue(countryListTable.get(num++));
						startIndex+=2;
					}
					if(i==endIndex){
						sheet.addMergedRegion(new CellRangeAddress(0, 0,i-1,i));
						endIndex+=2;
					}
				}else{
					cell.setCellValue(DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
					sheet.addMergedRegion(new CellRangeAddress(0, 0,3,4));
				}
			}	
		}
		row = sheet.createRow(1);
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i==0||i==1||i==2){
				sheet.addMergedRegion(new CellRangeAddress(0, 1,i,i));
			}else{
			   cell.setCellValue(title.get(i));
			}
		}
		int rownum=2;
		Collections.reverse(xAxis);
		for (String dateKey : xAxis) {
			int c=0;
			int startRow=rownum;
		   for (Dict dict : page) {
			String name=dict.getValue();
			row = sheet.createRow(rownum++);
			if(c==0){
				row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(dateKey+("1".equals(saleReport.getSearchType())?"日":("2".equals(saleReport.getSearchType())?"周":"月")));
				row.getCell(0).setCellStyle(contentStyle);
				row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(tip.get(dateKey));
				row.getCell(1).setCellStyle(contentStyle);
			}else{
				row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(0).setCellStyle(contentStyle);
				row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(1).setCellStyle(contentStyle);
			}
			c++;
			row.createCell(2,Cell.CELL_TYPE_STRING).setCellValue(name);
			row.getCell(2).setCellStyle(contentStyle);
			int cellnum=3;
			for(int i=0;i<countryList.size();i++){
				String countryKey=countryList.get(i);
				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSales()!=null&&data.get(countryKey).get(dateKey).get(name).getSales()>0){
					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(data.get(countryKey).get(dateKey).get(name).getSales()).setScale(2,4).floatValue());
        		}else{
        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
        		}
				row.getCell(cellnum-1).setCellStyle(cellStyle);
				
				if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSalesVolume()!=null&&data.get(countryKey).get(dateKey).get(name).getSalesVolume()>0){
					row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(countryKey).get(dateKey).get(name).getSalesVolume());
        		}else{
        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
        		}
				row.getCell(cellnum-1).setCellStyle(contentStyle);
			}
		  }
		   if(page.size()>0){
			   sheet.addMergedRegion(new CellRangeAddress(startRow, rownum-1,0,0));
			   sheet.addMergedRegion(new CellRangeAddress(startRow, rownum-1,1,1));
		   }
		}
		
		for (int i = 0; i < title.size(); i++) {
   		   sheet.autoSizeColumn((short)i,true);
	    }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "分产品类型统计所有国家_"+(saleReport.getCountry()==null?"":saleReport.getCountry()) + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	
	@RequestMapping(value = {"exportByGroupTypeAllCountry"})
	public String exportByGroupTypeAllCountry(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		String productTypeTemp=saleReport.getProductType();
		List<PsiProductTypeGroupDict> page= psiTypeGroupService.getAllList();
		PsiProductTypeGroupDict groupDict=new PsiProductTypeGroupDict();
		groupDict.setName("total");
		page.add(groupDict);
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String,Map<String, Map<String,SaleReport>>>  data = saleReportService.getSalesByProductGroupType(saleReport, rateMap);
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
				
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				tip.put(key,DateUtils.getDate(start,"E"));
				start = DateUtils.addDays(start, 1);
			}
		}
	    
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
        HSSFCellStyle cellStyle1 = wb.createCellStyle();
        cellStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
        cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCell cell = null;
		String currencySymbol =com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(saleReport.getCurrencyType());
		
        List<String> countryList=new ArrayList<String>();
        List<String> countryListTable=new ArrayList<String>();
        if(StringUtils.isBlank(saleReport.getCountry())){
         /*   List<Dict> dictAll=DictUtils.getDictList("platform");
        	for (Dict dict2 : dictAll) {
    			if(!"com.unitek".equals(dict2.getValue())){
    				countryList.add(dict2.getValue());
    				countryListTable.add(dict2.getLabel());
    			}
    		}*/
            countryList=Lists.newArrayList("de","fr","deAndFr","it","es","jp","mx","uk","com","ca","EnglishTotal","total");
            countryListTable=Lists.newArrayList("德国","法国","德国&法国","意大利","西班牙","日本","墨西哥","英国","美国","加拿大","英语国家","汇总");
        }else{
        	countryList=Lists.newArrayList(saleReport.getCountry());
            countryListTable=Lists.newArrayList(DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
        }
		List<String> title = new ArrayList<String>();
		if(StringUtils.isBlank(saleReport.getCountry())){
			title.add(0, "总计");
		}else{
			title.add(0,DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
		}
		if("1".equals(saleReport.getSearchType())){
			title.add(1, "星期");
		}else{
			title.add(1, "区间");
		}
		title.add(2, "产品线");
		if(StringUtils.isBlank(saleReport.getCountry())){
			for(int i=1;i<=countryList.size();i++){
				title.add("销售额("+currencySymbol+")");title.add("销售额占比(%)");
				title.add("去促销后("+currencySymbol+")");title.add("去促销后占比(%)");
				title.add("销量");title.add("去促销后");
			}
		}else{
			title.add("销售额("+currencySymbol+")");title.add("销售额占比(%)");
			title.add("去促销后("+currencySymbol+")");title.add("去促销后占比(%)");
			title.add("销量");title.add("去促销后");
		}
		int num=0;
		int startIndex=3;
		int endIndex=8;
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i==0||i==1||i==2){
			  cell.setCellValue(title.get(i));
			}else{
				if(StringUtils.isBlank(saleReport.getCountry())){
					if(i==startIndex){
						cell.setCellValue(countryListTable.get(num++));
						startIndex+=6;
					}
					if(i==endIndex){
						sheet.addMergedRegion(new CellRangeAddress(0, 0,i-5,i));
						endIndex+=6;
					}
				}else{
					cell.setCellValue(DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
					sheet.addMergedRegion(new CellRangeAddress(0, 0,3,8));
				}
			}	
		}
		row = sheet.createRow(1);
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i==0||i==1||i==2){
				sheet.addMergedRegion(new CellRangeAddress(0, 1,i,i));
			}else{
			   cell.setCellValue(title.get(i));
			}
		}
		int rownum=2;
		Collections.reverse(xAxis);
		for (String dateKey : xAxis) {
			int c=0;
			int startRow=rownum;
		   for (PsiProductTypeGroupDict dict : page) {
			String name=dict.getName();
			row = sheet.createRow(rownum++);
			if(c==0){
				row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(dateKey+("1".equals(saleReport.getSearchType())?"日":("2".equals(saleReport.getSearchType())?"周":"月")));
				row.getCell(0).setCellStyle(contentStyle);
				row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(tip.get(dateKey));
				row.getCell(1).setCellStyle(contentStyle);
			}else{
				row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(0).setCellStyle(contentStyle);
				row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(1).setCellStyle(contentStyle);
			}
			c++;
			row.createCell(2,Cell.CELL_TYPE_STRING).setCellValue(name);
			row.getCell(2).setCellStyle(contentStyle);
			int cellnum=3;
			float englishSales=0f;
			float englishTotalSales=0f;
			int englishSalesVolume=0;
			float englishRealSales=0f;
			float englishTotalRealSales=0f;
			int englishRealSalesVolume=0;
			
			float deAndFrSales=0f;
			float deAndFrTotalSales=0f;
			int deAndFrSalesVolume=0;
			float deAndFrRealSales=0f;
			float deAndFrTotalRealSales=0f;
			int deAndFrRealSalesVolume=0;
			
			float totalSales=0f;
			float allTotalSales=0f;
			int totalSalesVolume=0;
			float totalRealSales=0f;
			float allTotalRealSales=0f;
			int totalRealSalesVolume=0;
			
			for(int i=0;i<countryList.size();i++){
				if(i==countryList.size()-1){
					if(totalSales>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(totalSales).setScale(2,4).floatValue());
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(totalSales/allTotalSales).setScale(4,4).floatValue());
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					row.getCell(cellnum-2).setCellStyle(cellStyle);
					row.getCell(cellnum-1).setCellStyle(cellStyle1);
					
					if(totalRealSales>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(totalRealSales).setScale(2,4).floatValue());
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(totalRealSales/allTotalRealSales).setScale(4,4).floatValue());
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					row.getCell(cellnum-2).setCellStyle(cellStyle);
					row.getCell(cellnum-1).setCellStyle(cellStyle1);
					
					if(totalSalesVolume>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(totalSalesVolume);
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");	
					}
					row.getCell(cellnum-1).setCellStyle(contentStyle);
					
					if(totalRealSalesVolume>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(totalRealSalesVolume);
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");	
					}
					row.getCell(cellnum-1).setCellStyle(contentStyle);
				}
			    else if(i==countryList.size()-2){
					if(englishSales>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(englishSales).setScale(2,4).floatValue());
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(englishSales/englishTotalSales).setScale(4,4).floatValue());
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					row.getCell(cellnum-2).setCellStyle(cellStyle);
					row.getCell(cellnum-1).setCellStyle(cellStyle1);
					
					if(englishRealSales>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(englishRealSales).setScale(2,4).floatValue());
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(englishRealSales/englishTotalRealSales).setScale(4,4).floatValue());
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					row.getCell(cellnum-2).setCellStyle(cellStyle);
					row.getCell(cellnum-1).setCellStyle(cellStyle1);
					
					if(englishSalesVolume>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(englishSalesVolume);
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");	
					}
					row.getCell(cellnum-1).setCellStyle(contentStyle);
					
					if(englishRealSalesVolume>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(englishRealSalesVolume);
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");	
					}
					row.getCell(cellnum-1).setCellStyle(contentStyle);
				}else if(i==2){
					if(deAndFrSales>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(deAndFrSales).setScale(2,4).floatValue());
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(deAndFrSales/deAndFrTotalSales).setScale(4,4).floatValue());
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					row.getCell(cellnum-2).setCellStyle(cellStyle);
					row.getCell(cellnum-1).setCellStyle(cellStyle1);
					
					if(deAndFrRealSales>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(deAndFrRealSales).setScale(2,4).floatValue());
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(deAndFrRealSales/deAndFrTotalRealSales).setScale(4,4).floatValue());
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					row.getCell(cellnum-2).setCellStyle(cellStyle);
					row.getCell(cellnum-1).setCellStyle(cellStyle1);
					
					if(deAndFrSalesVolume>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(deAndFrSalesVolume);
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");	
					}
					row.getCell(cellnum-1).setCellStyle(contentStyle);
					
					if(deAndFrRealSalesVolume>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(deAndFrRealSalesVolume);
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");	
					}
					row.getCell(cellnum-1).setCellStyle(contentStyle);
				}else{
					String countryKey=countryList.get(i);
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSales()!=null&&data.get(countryKey).get(dateKey).get(name).getSales()>0){
						float sales=data.get(countryKey).get(dateKey).get(name).getSales();
						float total=data.get(countryKey).get(dateKey).get("total").getSales();
						if("ca".equals(countryKey)||"com".equals(countryKey)||"uk".equals(countryKey)){
							englishSales+=sales;
							englishTotalSales+=total;
						}
						if("de".equals(countryKey)||"fr".equals(countryKey)){
							deAndFrSales+=sales;
							deAndFrTotalSales+=total;
						}
						totalSales+=sales;
						allTotalSales+=total;
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(sales).setScale(2,4).floatValue());
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(sales/total).setScale(4,4).floatValue());
					}else{
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
					row.getCell(cellnum-2).setCellStyle(cellStyle);
					row.getCell(cellnum-1).setCellStyle(cellStyle1);
					
					
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getRealSales()!=null&&data.get(countryKey).get(dateKey).get(name).getRealSales()>0){
						float sales=data.get(countryKey).get(dateKey).get(name).getRealSales();
						float total=data.get(countryKey).get(dateKey).get("total").getRealSales();
						if("ca".equals(countryKey)||"com".equals(countryKey)||"uk".equals(countryKey)){
							englishRealSales+=sales;
							englishTotalRealSales+=total;
						}
						if("de".equals(countryKey)||"fr".equals(countryKey)){
							deAndFrRealSales+=sales;
							deAndFrTotalRealSales+=total;
						}
						totalRealSales+=sales;
						allTotalRealSales+=total;
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(sales).setScale(2,4).floatValue());
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(sales/total).setScale(4,4).floatValue());
					}else{
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
					row.getCell(cellnum-2).setCellStyle(cellStyle);
					row.getCell(cellnum-1).setCellStyle(cellStyle1);
					
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSalesVolume()!=null&&data.get(countryKey).get(dateKey).get(name).getSalesVolume()>0){
						int salesVolume=data.get(countryKey).get(dateKey).get(name).getSalesVolume();
						if("ca".equals(countryKey)||"com".equals(countryKey)||"uk".equals(countryKey)){
							englishSalesVolume+=salesVolume;
						}
						if("de".equals(countryKey)||"fr".equals(countryKey)){
							deAndFrSalesVolume+=salesVolume;
						}
						totalSalesVolume+=salesVolume;
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(salesVolume);
	        		}else{
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
					row.getCell(cellnum-1).setCellStyle(contentStyle);
					
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getRealSalesVolume()!=null&&data.get(countryKey).get(dateKey).get(name).getRealSalesVolume()>0){
						int salesVolume=data.get(countryKey).get(dateKey).get(name).getRealSalesVolume();
						if("ca".equals(countryKey)||"com".equals(countryKey)||"uk".equals(countryKey)){
							englishRealSalesVolume+=salesVolume;
						}
						if("de".equals(countryKey)||"fr".equals(countryKey)){
							deAndFrRealSalesVolume+=salesVolume;
						}
						totalRealSalesVolume+=salesVolume;
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(salesVolume);
	        		}else{
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
					row.getCell(cellnum-1).setCellStyle(contentStyle);
				}
			}
			
		  }
		   if(page.size()>0){
			   sheet.addMergedRegion(new CellRangeAddress(startRow, rownum-1,0,0));
			   sheet.addMergedRegion(new CellRangeAddress(startRow, rownum-1,1,1));
		   }
		}
		
		for (int i = 0; i < title.size(); i++) {
   		   sheet.autoSizeColumn((short)i,true);
	    }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "分产品线统计所有国家_"+(saleReport.getCountry()==null?"":saleReport.getCountry()) + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	@RequestMapping(value = {"exportByGroupTypePartCountry"})
	public String exportByGroupTypePartCountry(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		String productTypeTemp=saleReport.getProductType();
		List<PsiProductTypeGroupDict> page= psiTypeGroupService.getAllList();
		PsiProductTypeGroupDict groupDict=new PsiProductTypeGroupDict();
		groupDict.setName("total");
		page.add(groupDict);
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String,Map<String, Map<String,SaleReport>>>  data = saleReportService.getSalesByProductGroupType(saleReport, rateMap);
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
				
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				tip.put(key,DateUtils.getDate(start,"E"));
				start = DateUtils.addDays(start, 1);
			}
		}
	    
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
        HSSFCellStyle cellStyle1 = wb.createCellStyle();
        cellStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
        cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCell cell = null;
		String currencySymbol = com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(saleReport.getCurrencyType());
		
        List<String> countryList=new ArrayList<String>();
        List<String> countryListTable=new ArrayList<String>();
        if(StringUtils.isBlank(saleReport.getCountry())){
            List<Dict> dictAll=DictUtils.getDictList("platform");
        	for (Dict dict2 : dictAll) {
    			if("ca".equals(dict2.getValue())||"com".equals(dict2.getValue())||"uk".equals(dict2.getValue())){
    				countryList.add(dict2.getValue());
    				countryListTable.add(dict2.getLabel());
    			}
    		}
        	countryList.add("EnglishTotal");
			countryListTable.add("汇总");
        }else{
        	countryList=Lists.newArrayList(saleReport.getCountry());
            countryListTable=Lists.newArrayList(DictUtils.getDictLabel(saleReport.getCountry(),"platform",""));
        }
		List<String> title = new ArrayList<String>();
		if(StringUtils.isBlank(saleReport.getCountry())){
			title.add(0, "总计");
		}
		if("1".equals(saleReport.getSearchType())){
			title.add(1, "星期");
		}else{
			title.add(1, "区间");
		}
		title.add(2, "产品线");
		if(StringUtils.isBlank(saleReport.getCountry())){
			for(int i=1;i<=4;i++){
				title.add("销售额("+currencySymbol+")");title.add("销售额占比(%)");title.add("销量");
			}
		}
		int num=0;
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i==0||i==1||i==2){
			  cell.setCellValue(title.get(i));
			}else{
				if(StringUtils.isBlank(saleReport.getCountry())){
					if(i==3||i==6||i==9||i==12){
						cell.setCellValue(countryListTable.get(num++));
					}
					if(i==5||i==8||i==11||i==14){
						sheet.addMergedRegion(new CellRangeAddress(0, 0,i-2,i));
					}
				}
			}	
		}
		row = sheet.createRow(1);
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			if(i==0||i==1||i==2){
				sheet.addMergedRegion(new CellRangeAddress(0, 1,i,i));
			}else{
			   cell.setCellValue(title.get(i));
			}
		}
		int rownum=2;
		Collections.reverse(xAxis);
		for (String dateKey : xAxis) {
			int c=0;
			int startRow=rownum;
		   for (PsiProductTypeGroupDict dict : page) {
			String name=dict.getName();
			row = sheet.createRow(rownum++);
			if(c==0){
				row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(dateKey+("1".equals(saleReport.getSearchType())?"日":("2".equals(saleReport.getSearchType())?"周":"月")));
				row.getCell(0).setCellStyle(contentStyle);
				row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(tip.get(dateKey));
				row.getCell(1).setCellStyle(contentStyle);
			}else{
				row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(0).setCellStyle(contentStyle);
				row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(1).setCellStyle(contentStyle);
			}
			c++;
			row.createCell(2,Cell.CELL_TYPE_STRING).setCellValue(name);
			row.getCell(2).setCellStyle(contentStyle);
			int cellnum=3;
			float englishSales=0f;
			float englishTotalSales=0f;
			int englishSalesVolume=0;
			for(int i=0;i<countryList.size();i++){
				if(i==countryList.size()-1){
					if(englishSales>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(englishSales).setScale(2,4).floatValue());
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(englishSales/englishTotalSales).setScale(4,4).floatValue());
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					row.getCell(cellnum-2).setCellStyle(cellStyle);
					row.getCell(cellnum-1).setCellStyle(cellStyle1);
					
					if(englishSalesVolume>0){
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(englishSalesVolume);
					}else{
						row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");	
					}
					row.getCell(cellnum-1).setCellStyle(contentStyle);
				}else{
					String countryKey=countryList.get(i);
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSales()!=null&&data.get(countryKey).get(dateKey).get(name).getSales()>0){
						float sales=data.get(countryKey).get(dateKey).get(name).getSales();
						float total=data.get(countryKey).get(dateKey).get("total").getSales();
						englishSales+=sales;
						englishTotalSales+=total;
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(sales).setScale(2,4).floatValue());
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(new BigDecimal(sales/total).setScale(4,4).floatValue());
					}else{
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
					row.getCell(cellnum-2).setCellStyle(cellStyle);
					row.getCell(cellnum-1).setCellStyle(cellStyle1);
					
					
					
					if(data.get(countryKey)!=null&&data.get(countryKey).get(dateKey)!=null&&data.get(countryKey).get(dateKey).get(name)!=null&&data.get(countryKey).get(dateKey).get(name).getSalesVolume()!=null&&data.get(countryKey).get(dateKey).get(name).getSalesVolume()>0){
						int salesVolume=data.get(countryKey).get(dateKey).get(name).getSalesVolume();
						row.createCell(cellnum++,Cell.CELL_TYPE_NUMERIC).setCellValue(salesVolume);
						englishSalesVolume+=salesVolume;
	        		}else{
	        			row.createCell(cellnum++,Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
					row.getCell(cellnum-1).setCellStyle(contentStyle);
				}
				
			}
			
		  }
		   if(page.size()>0){
			   sheet.addMergedRegion(new CellRangeAddress(startRow, rownum-1,0,0));
			   sheet.addMergedRegion(new CellRangeAddress(startRow, rownum-1,1,1));
		   }
		}
		
		for (int i = 0; i < title.size(); i++) {
   		   sheet.autoSizeColumn((short)i,true);
	    }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "分产品线统计部分国家_"+(saleReport.getCountry()==null?"":saleReport.getCountry()) + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	@RequestMapping(value = {"exportTotalProfit"})
	public String exportTotalProfit(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model){
		String productTypeTemp=saleReport.getProductType();
		PsiProduct psiProduct=new PsiProduct();
		List<String> page=new ArrayList<String>();
		if(StringUtils.isBlank(saleReport.getGroupName())){
			saleReport.setProductType(null);
			if(StringUtils.isBlank(saleReport.getCountry())){
				page =psiProductService.findCountryProduct(psiProduct);
			}else{
				psiProduct.setPlatform(saleReport.getCountry());
				page =psiProductService.findCountryProduct(psiProduct);
			}
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isBlank(saleReport.getProductType())){
			List<Dict> dict=psiTypeGroupService.getProductType(saleReport.getGroupName());
			List<String> typeList=new ArrayList<String>();
			Set<String> nameSet=new HashSet<String>();
			for (Dict dict2 : dict) {
				typeList.add("'"+dict2.getValue()+"'");
				nameSet.add(dict2.getValue());
			}
			if(typeList!=null&&typeList.size()>0){
			    saleReport.setProductType(StringUtils.join(typeList.toArray(),",")); 
			}else{
				saleReport.setProductType("'no'");
			}  
			List<PsiProduct> psiList=psiProductService.findAllByCountryByType(saleReport.getCountry(),nameSet);
			for (PsiProduct psiProduct2 : psiList) {
				 page.addAll(psiProduct2.getProductNameWithColor());
			}
			
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isNotBlank(saleReport.getProductType())){
			saleReport.setProductType("'"+saleReport.getProductType()+"'");
			List<PsiProduct> psiList=psiProductService.findAllByCountry(saleReport.getCountry(),productTypeTemp);
			for (PsiProduct psiProduct2 : psiList) {
				 page.addAll(psiProduct2.getProductNameWithColor());
			}
		}
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String,Map<String, Map<String,SaleReport>>>  data = saleReportService.getSalesByProduct(saleReport, rateMap);
		Map<String,Map<String,Object>> productsMoqAndPrice = saleReportService.getProductsMoqAndPrice(saleReport, rateMap);
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
				
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				tip.put(key,DateUtils.getDate(start,"E"));
				start = DateUtils.addDays(start, 1);
			}
		}
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCell cell = null;
		String currencySymbol = com.springrain.erp.common.utils.StringUtils.
				getCurrencySymbolByType(saleReport.getCurrencyType());
		List<String> countryList=new ArrayList<String>();
	        if(StringUtils.isBlank(saleReport.getCountry())){
	            List<Dict> dictAll=DictUtils.getDictList("platform");
	        	for (Dict dict2 : dictAll) {
	    			if(!"com.unitek".equals(dict2.getValue())){
	    				countryList.add(dict2.getValue());
	    			}
	    		}
	        }else if("eu".equals(saleReport.getCountry())){
	        	countryList.addAll(Lists.newArrayList("de","fr","it","es","uk"));
	        }else if("en".equals(saleReport.getCountry())){
	        	countryList.addAll(Lists.newArrayList("com","ca","uk"));
	        }else if("unEn".equals(saleReport.getCountry())){
	        	countryList.addAll(Lists.newArrayList("de","fr","it","es","jp"));
	        }else{
	        	countryList=Lists.newArrayList(saleReport.getCountry());
	        }
	        
	        List<String> title = new ArrayList<String>();
			title.add("日期");
			for(String typeDate:xAxis){
                 title.add(typeDate);
            }
	       title.add("统计");
	        
			List<String> title2 = new ArrayList<String>();
			title2.add("产品");
			for(String typeDate:xAxis){
				title2.add("销售额("+currencySymbol+")");
				title2.add("销量");
			}
	        title2.add("销售额合计("+currencySymbol+")");
	        title2.add("销量合计");
	        title2.add("成本单价("+currencySymbol+")");
	        title2.add("毛利("+currencySymbol+")");
	        int k=0;
	        for (int i=0;i<title2.size();i++) {
	        	cell = row.createCell(i);
	        	cell.setCellStyle(style);
	        	if(i==0){
	        		cell.setCellValue(title.get(k++));
	        	}else if(i>0&&i<title2.size()-4){
	        	  if(i%2!=0){
	        		cell.setCellValue(title.get(k++));
	        	  }else{
	        		 sheet.addMergedRegion(new CellRangeAddress(0,0,i-1,i));
	        	  }
	        	}else if(i>=title2.size()-4){
	        		if(i==title2.size()-4){
	        			cell.setCellValue(title.get(k++));
	        		}else if(i==title2.size()-1){
	        			sheet.addMergedRegion(new CellRangeAddress(0,0,i-3,i));
	        		}
	        	}
			}
	        
	        row = sheet.createRow(1);
	        for (int i=0;i<title2.size();i++) {
	        	cell = row.createCell(i);
	        	cell.setCellStyle(style);
				cell.setCellValue(title2.get(i));
			}
	     int num=2;
	     //country date name sale
	     for(String name:page){
	    	 row = sheet.createRow(num++);
	    	 int col=0;
	    	 row.createCell(col++,Cell.CELL_TYPE_STRING).setCellValue(name);
	    	 row.getCell(col-1).setCellStyle(contentStyle);
	    	 float totalSales=0f;
	    	 int totalSalesVolume=0;
	    	 for(String typeDate:xAxis){
	    		 float sales=0f;
	    		 int  salesVolume=0;
	    		 for(String country:countryList){
	    			 if(data!=null&&data.get(country)!=null&&data.get(country).get(typeDate)!=null&&data.get(country).get(typeDate).get(name)!=null&&data.get(country).get(typeDate).get(name).getSureSales()!=null){
	    				 sales+=data.get(country).get(typeDate).get(name).getSureSales();
	    			 }
	    			 if(data!=null&&data.get(country)!=null&&data.get(country).get(typeDate)!=null&&data.get(country).get(typeDate).get(name)!=null&&data.get(country).get(typeDate).get(name).getSureSalesVolume()!=null){
	    				 salesVolume+=data.get(country).get(typeDate).get(name).getSureSalesVolume();
	    			 }
	    		 }
	    		 totalSales+=sales;
	    		 totalSalesVolume+=salesVolume;
	    		 cell = row.createCell(col++,Cell.CELL_TYPE_NUMERIC);
	    		 cell.setCellStyle(cellStyle);
	    		 if(sales>0){
	    			cell.setCellValue(sales);
	    		 }
	    		 cell = row.createCell(col++,Cell.CELL_TYPE_NUMERIC);
	    		 cell.setCellStyle(contentStyle);
	    		 if(salesVolume>0){
	    			cell.setCellValue(salesVolume);
	    		 }
			 }
	    	 cell = row.createCell(col++,Cell.CELL_TYPE_NUMERIC);
    		 cell.setCellStyle(cellStyle);
    		 if(totalSales>0){
    			cell.setCellValue(totalSales);
    		 }
    		 cell = row.createCell(col++,Cell.CELL_TYPE_NUMERIC);
    		 cell.setCellStyle(contentStyle);
    		 if(totalSalesVolume>0){
    			cell.setCellValue(totalSalesVolume);
    		 }
    		 //销售金额-（销售数量*成本单价）
    		if(productsMoqAndPrice.get(name)!=null&&productsMoqAndPrice.get(name).get("price")!=null){
     			row.createCell(col++, Cell.CELL_TYPE_NUMERIC).setCellValue(((BigDecimal) productsMoqAndPrice.get(name).get("price")).doubleValue());
     			if(totalSales>0){
     				double profit=totalSales-(totalSalesVolume*((BigDecimal) productsMoqAndPrice.get(name).get("price")).doubleValue());
     				row.createCell(col++, Cell.CELL_TYPE_NUMERIC).setCellValue(profit);
     			}else{
     				row.createCell(col++, Cell.CELL_TYPE_NUMERIC);
     			}
     		}else{
     			row.createCell(col++, Cell.CELL_TYPE_NUMERIC);
     			row.createCell(col++, Cell.CELL_TYPE_NUMERIC);
     		}
    		row.getCell(col-1).setCellStyle(cellStyle);
    		row.getCell(col-2).setCellStyle(cellStyle);
	     }
	    for (int i = 0; i < title2.size(); i++) {
    		 sheet.autoSizeColumn((short)i, true);
	    }   
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "分产品毛利统计";
			fileName = URLEncoder.encode(fileName, "UTF-8")+(saleReport.getCountry()==null?"":saleReport.getCountry())+"("+formatDay.format(saleReport.getStart())+"-"+formatDay.format(saleReport.getEnd())+")_" + sdf.format(new Date()) + ".xls";
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 手机版页面专用
	 * @return
	 */
	@RequestMapping(value = {"mobileList"})
	public String mobileList(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		String currencyType = saleReport.getCurrencyType();
		String productTypeTemp=saleReport.getProductType();
		
		if(StringUtils.isBlank(saleReport.getGroupName())){
			saleReport.setProductType(null);
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isBlank(saleReport.getProductType())){
			List<Dict> dict=psiTypeGroupService.getProductType(saleReport.getGroupName());
			List<String> typeList=new ArrayList<String>();
			for (Dict dict2 : dict) {
				typeList.add("'"+dict2.getValue()+"'");
			}
			if(typeList!=null&&typeList.size()>0){
			    saleReport.setProductType(StringUtils.join(typeList.toArray(),",")); 
			}else{
				saleReport.setProductType("'no'");
			}  
		}else if(StringUtils.isNotBlank(saleReport.getGroupName())&&StringUtils.isNotBlank(saleReport.getProductType())){
			saleReport.setProductType("'"+saleReport.getProductType()+"'");
		}
		
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String, Map<String, SaleReport>>  data = saleReportService.getSales(saleReport, rateMap);
		Map<String,Map<String, SaleReport>>  otherData = saleReportService.getOtherSales(saleReport, rateMap);
	    
		saleReport.setProductType(productTypeTemp);
		model.addAttribute("data", data);
		model.addAttribute("otherData",otherData);
		//构建x轴
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		String type = "日";
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
				type = "周";
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
				type = "月";
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				tip.put(key,DateUtils.getDate(start,"E"));
				start = DateUtils.addDays(start, 1);
			}
		}
		model.addAttribute("xAxis", xAxis);
		model.addAttribute("tip", tip);
		model.addAttribute("type", type);
		//取出3个区间值
		String sec = xAxis.get(xAxis.size()-2);
		List<SaleReport> list = Lists.newArrayList();
		for (Dict dict : DictUtils.getDictList("platform")) {
			String country = dict.getValue();
			if("com.unitek".equals(country)){
				continue;
			}
			SaleReport temp = null;
			if(data.get(country)!=null){
				temp= data.get(country).get(sec);
			}
			if(temp==null){
				temp = new SaleReport(0f,0f,0,0);
			}
			temp.setCountry(country);
			list.add(temp);
		}
		Collections.sort(list);
		model.addAttribute("sec",list);
		
		Map<String,Float> change=new HashMap<String,Float>();

		change.put("cny", MathUtils.getRate("CNY", currencyType, null));
		if ("USD".equals(currencyType)) {
			change.put("usdToCny", AmazonProduct2Service.getRateConfig().get("USD/CNY"));
		} else {
			change.put("usdToCny", AmazonProduct2Service.getRateConfig().get("USD/CNY")*
					AmazonProduct2Service.getRateConfig().get("EUR/USD"));
		}
		change.put("eur", MathUtils.getRate("EUR", currencyType, null));
		change.put("usd", MathUtils.getRate("USD", currencyType, null));
		change.put("cad", MathUtils.getRate("CAD", currencyType, null));
		change.put("gbp", MathUtils.getRate("GBP", currencyType, null));
		change.put("jpy", MathUtils.getRate("JPY", currencyType, null));
		change.put("mxn", MathUtils.getRate("MXN", currencyType, null));
		model.addAttribute("change", change);
		model.addAttribute("lastUpdateTime", amazonOrderService.getMaxOrderDate(null));
		model.addAttribute("currencySymbol", com.springrain.erp.common.utils.StringUtils.
					getCurrencySymbolByType(saleReport.getCurrencyType()));
		model.addAttribute("balance",advertisingService.accountBalance());
		
		return "modules/amazoninfo/sales/amazonSalesReportList";
	}
	
	private static String getDefaultStr(String orderType){
		String defaultStr = "汇总";
		if(StringUtils.isBlank(orderType)||orderType.startsWith("1")){
			defaultStr = "Amazon汇总";
		}else if(orderType.startsWith("2")){
			String str = orderType.split("-")[1].toUpperCase();
			defaultStr = "Vendor_" + ("COM".equals(str)?"US":str);
		}else if(orderType.startsWith("3")){
			String str = orderType.split("-")[1].toUpperCase();
			defaultStr = "Ebay_" + ("COM".equals(str)?"US":str);
		}else if(orderType.startsWith("4")){
			defaultStr = "Offline";
		}
		return defaultStr;
	}
	
	
	@RequestMapping(value = {"panEuList"})
	public String panEuList(Model model){
		model.addAttribute("panEuList", saleReportService.panEuList());
		return "modules/amazoninfo/sales/panEuList";
	}

	@RequestMapping(value = {"exportPanEu"})
	public String export(ProductPrice productPrice,HttpServletRequest request, HttpServletResponse response, Model model){
		List<Object[]> list=saleReportService.panEuList();
		
		HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		row.setHeight((short) 600);
		HSSFCell cell = null;	
		List<String>  title=Lists.newArrayList("ProductName","Asin","SKU","Fnsku","Pan_Eu","UK","DE","FR","IT","ES");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		int  rowIndex=1;
		for (Object[] obj: list) {
				row=sheet.createRow(rowIndex++);
				int m=0;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[3].toString());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[1].toString());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[2].toString());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[10]!=null?obj[10].toString():"");
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[4].toString());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[5].toString());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[6].toString());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[7].toString());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[8].toString());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[9].toString());
				
		}
		 for (int i=1;i<rowIndex;i++) {
       	      for (int j = 0; j < title.size(); j++) {
       	    	 if(j==0||j==1||j%2!=0){
       	    		sheet.getRow(i).getCell(j).setCellStyle(contentStyle);
       	    	 }else{
       	    		sheet.getRow(i).getCell(j).setCellStyle(cellStyle);
       	    	 }
	        	  
			 }
         }
		  for (int i = 0; i < title.size(); i++) {
       		 sheet.autoSizeColumn((short)i, true);
		  }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "PanEuProducts" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	@RequestMapping(value = {"getMaxOrder"})
	public String getMaxOrder(String[] productsName,SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isBlank(saleReport.getCountry())){
			saleReport.setCountry("total");
		}
		Map<String,Map<String, Map<String,Integer>>>  data = null;
		if(productsName!=null&&productsName.length>0){
			 data =saleReportService.getMaxOrder(saleReport,Lists.newArrayList(productsName));
		}else{
			 data =saleReportService.getMaxOrder(saleReport,null);
		}
		
		//构建x轴
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if (week == 53) {	//出现53周可能夸年了
					year = DateUtils.getMonday(start).getYear()+1900;
				}
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				tip.put(key,DateUtils.getDate(start,"E"));
				start = DateUtils.addDays(start, 1);
			}
		}
		model.addAttribute("xAxis", xAxis);
		model.addAttribute("tip", tip);
		model.addAttribute("data", data);
		List<String> productNames=amazonProductService.findAllProductName();
		if(productsName!=null&&productsName.length>0){
			productNames.removeAll(Lists.newArrayList(productsName));
			model.addAttribute("productsName", Lists.newArrayList(productsName));
		}
		model.addAttribute("productNames", productNames);
		return "modules/amazoninfo/amazonMaxOrderCountView";
	}
	
	
	@RequestMapping(value = {"maxOrderList"})
	public String maxOrderList(String productName,String country,String type, String time,Model model){
		List<Object[]> ops =saleReportService.findMaxOrder(time,type,country,productName);
		model.addAttribute("ops", ops);
		model.addAttribute("productName", productName);
		model.addAttribute("country", country);
		model.addAttribute("type", type);
		model.addAttribute("time", time);
		return "modules/amazoninfo/amazonMaxOrderListView";
	}
	
	@RequestMapping(value = {"genData"})
	public String genData(Model model){
		logger.info("开始生成数据");
		new Thread(){
			public void run() {
				Map<String,Map<String,Float>> allRate = amazonProduct2Service.getAllRateByDate();
				//先按天统计
				SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
				Date start ;
				try {
					start = dayFormat.parse("20150101");
					Date today = new Date();
					while(true){
						String dateStr = dayFormat.format(start);
						Map<String, Float> rateRs = allRate.get(dateStr);
						saleProfitService.saveOrUpdate(dateStr, rateRs);
						start = DateUtils.addDays(start, 1);
						if (start.after(today)) {
							break;
						}
					}
					//按天统计完毕后按月汇总
					String year = "2015";
					for (int i = 1; i < 13; i++) {
						String month = year + (i<10?"0"+i:i);
						saleReportMonthTypeService.saveOrUpdate(month);
					}
					year = "2016";
					for (int i = 1; i < 7; i++) {
						String month = year + (i<10?"0"+i:i);
						saleReportMonthTypeService.saveOrUpdate(month);
					}
					logger.info("生成数据完毕！！！");
				} catch (Exception e) {
					logger.error("生成数据异常", e);
				}
			}
		}.start();
		return "modules/amazoninfo/sales/amazonoperatingResults";
	}

	//运营业绩报告
	@RequestMapping(value = {"results"})
	public String results(String year, String dataType, String divFlag, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException{
		DateFormat format = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		if (StringUtils.isEmpty(year)) {
			year = format.format(DateUtils.addMonths(new Date(), -1));
		}
		model.addAttribute("year", year);
		//if (UserUtils.hasPermission("amazoninfo:profits:view")) {	//可查看利润
			model.addAttribute("flag", "1");
		//}
		if (StringUtils.isEmpty(dataType)) {
			dataType = "1";
		}
		model.addAttribute("dataType", dataType);
		
		//所有的产品线名称
		List<String> lineNameList = dictService.getAllLineShotrName();
		//产品类型对应的产品线关系
		Map<String, String> typeLine = dictService.getTypeLine(null);
		model.addAttribute("typeLine", typeLine);
		//按产品类型统计销售额		日期[类型	[国家 /销售数据]]
		Map<String,Map<String, Map<String, SaleReportMonthType>>> data = saleReportMonthTypeService.getSalesResult(year, false);
		//按产品类型统计销售额		日期[类型	[国家 /销售额]]
		//Map<String,Map<String, Map<String, Float>>> data = saleReportService.getSalesResult(year);
		//按产品线统计销售额		日期[产品线	[国家 /销售额]]
		//Map<String,Map<String, Map<String, Float>>> lineData = changeLineData(data, typeLine);
		//Map<String,Map<String, Map<String, SaleReportMonthType>>> lineData = changeLineData1(data, typeLine);
		Map<String,Map<String, Map<String, SaleReportMonthType>>> lineData = changeLineDataLine(data, typeLine);
		String startMonth = year + "01";
		String endMonth = year + "12";
		EnterpriseGoal enterpriseGoal = new EnterpriseGoal();
		enterpriseGoal.setStartMonth(monthFormat.parse(startMonth));
		enterpriseGoal.setEndMonth(monthFormat.parse(endMonth));
		//[月份 [国家   目标]]	
		Map<String,Map<String, EnterpriseGoal>> countryGoalMap = enterpriseGoalService.findCountryGoalWithTotalAndEn(enterpriseGoal);
		//分产品线目标	[月份 [产品线[国家   目标]]]
		Map<String,Map<String,Map<String, EnterpriseGoal>>> lineGoalMap = enterpriseGoalService.findLineGoalWithTotalAndEn(enterpriseGoal);
		Date now = new Date();
		String currMonthString = monthFormat.format(now);	//当前月
		String currYearString = format.format(now);			//当前年

		String currSeason = "q1";	//当前季度
		String monthNow = currMonthString.substring(4);
		if ("04".equals(monthNow) || "05".equals(monthNow) || "06".equals(monthNow)) {
			currSeason = "q2";
		} else if ("07".equals(monthNow) || "08".equals(monthNow) || "09".equals(monthNow)) {
			currSeason = "q3";
		} else if ("10".equals(monthNow) || "11".equals(monthNow) || "12".equals(monthNow)) {
			currSeason = "q4";
		}
		currSeason = currYearString + currSeason;
		//判断权限
		boolean viewAll = false;
		if (SecurityUtils.getSubject().isPermitted("amazoninfo:results:viewAll")) {
			viewAll = true;
		}
		model.addAttribute("viewAll", viewAll);	//查看所有数据
		//平台和产品线权限
		Map<String, String> countryLineMap = saleReportMonthTypeService.getCountryLineStr();
		model.addAttribute("viewCountry", countryLineMap.get("country"));	//平台集合
		model.addAttribute("viewLine", countryLineMap.get("line"));	//产品线集合
		
		model.addAttribute("data", data);	//按产品类型统计销售额数据
		model.addAttribute("lineData", lineData);	//按产品线统计销售额数据
		model.addAttribute("lineNameList", lineNameList);	//产品线集合
		model.addAttribute("countryGoalMap", countryGoalMap);	//分国家目标
		model.addAttribute("lineGoalMap", lineGoalMap);	//分国家分产品线目标
		model.addAttribute("currMonthString", currMonthString);
		model.addAttribute("currYearString", currYearString);
		model.addAttribute("currSeason", currSeason);
		//初始目标
		model.addAttribute("targetGoalMap", EnterpriseGoalService.monthGoalMap(year));
		//分平台毛利率指标
		model.addAttribute("rateMap", getAllProfitRate(year));
		//记住当前浏览的表格
		model.addAttribute("divFlag", divFlag);
		
		return "modules/amazoninfo/sales/amazonoperatingResults";
	}

	//导出运营业绩报告
	@RequestMapping(value = {"exportResults"})
	public String exportResults(String year, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException{
		HSSFWorkbook wb = new HSSFWorkbook();
		
		Map<String, HSSFCellStyle> styleMap = getAllStyle(wb);

		//year = "2016";	//目前数据只支持2016年
		DateFormat format = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		if (StringUtils.isEmpty(year)) {
			year = format.format(DateUtils.addMonths(new Date(), -1));
		}
		//所有的产品线名称,含UnGrouped
		List<String> lineNameList = dictService.getAllLineShotrName();
		//产品类型对应的产品线关系
		Map<String, String> typeLine = dictService.getTypeLine(null);
		//按产品类型统计销售额		日期[类型	[国家 /销售额]]
		//Map<String,Map<String, Map<String, Float>>> data = saleReportService.getSalesResult(year);
		// 按产品类型统计销售额		日期[类型	[国家 /销售数据]]
		Map<String,Map<String, Map<String, SaleReportMonthType>>> data = saleReportMonthTypeService.getSalesResult(year, true);
		//上年度数据统计
		int lastYear = Integer.parseInt(year)-1;
		Map<String,Map<String, Map<String, SaleReportMonthType>>> data1 = saleReportMonthTypeService.getSalesResultForYear(lastYear+"");
		//按产品线统计销售额		日期[产品线	[国家 /销售额]]
		//Map<String,Map<String, Map<String, SaleReportMonthType>>> lineData = changeLineData1(data, typeLine);
		Map<String,Map<String, Map<String, SaleReportMonthType>>> lineData = changeLineDataLine(data, typeLine);
		String startMonth = year + "01";
		String endMonth = year + "12";
		EnterpriseGoal enterpriseGoal = new EnterpriseGoal();
		enterpriseGoal.setStartMonth(monthFormat.parse(startMonth));
		enterpriseGoal.setEndMonth(monthFormat.parse(endMonth));
		//[月份 [国家   目标]]	
		Map<String,Map<String, EnterpriseGoal>> countryGoalMap = enterpriseGoalService.findCountryGoalWithTotalAndEn(enterpriseGoal);
		//分产品线目标	[月份 [产品线[国家   目标]]]
		Map<String,Map<String,Map<String, EnterpriseGoal>>> lineGoalMap = enterpriseGoalService.findLineGoalWithTotalAndEn(enterpriseGoal);
		Date now = new Date();
		String currMonthString = monthFormat.format(now);	//当前月
		String currYearString = format.format(now);			//当前年
		List<String> sheetNameList = Lists.newArrayList();	//sheet命名集合
		
		// 年度总表
		sheetNameList.add(year + "总表");
	    HSSFSheet yearSheet = wb.createSheet();
	    fillTotalSheet(yearSheet, data, data1, styleMap, year, currYearString, currMonthString, typeLine);

		//分产品线表
		sheetNameList.add(year + "分产品线");
	    HSSFSheet lineSheet = wb.createSheet();
	    fillByLineSheet(lineSheet, lineData, styleMap, year, currYearString, currMonthString);
	    
		//分市场销售额
		sheetNameList.add(year + "分市场销售额");
	    HSSFSheet salesSheet = wb.createSheet();
	    fillSalesSheet(salesSheet, data, styleMap, year, currYearString, currMonthString, typeLine);
	    
	    //if (UserUtils.hasPermission("amazoninfo:profits:view")) {
			//分市场利润
			sheetNameList.add("分市场利润");
		    HSSFSheet profitSheet = wb.createSheet();
		    fillProfitSheet(profitSheet, data, styleMap, year, currYearString, currMonthString, typeLine);
		    
			//分市场利润率
			sheetNameList.add("分市场利润率");
		    HSSFSheet profitRateSheet = wb.createSheet();
		    fillProfitRateSheet(profitRateSheet, data, styleMap, year, currYearString, currMonthString, typeLine);
		//}
		
	    //运营绩效
		sheetNameList.add("运营绩效");
	    HSSFSheet resultSheet = wb.createSheet();
	    fillResultSheet(resultSheet, lineData, styleMap, year, currYearString, currMonthString, typeLine,
	    		countryGoalMap,lineGoalMap,lineNameList);
	    
		/*
	    //总目标情况
		sheetNameList.add(year + "目标");
	    HSSFSheet targetSheet = wb.createSheet();
	    fillTargetSheet(targetSheet, data, lineData, styleMap, year, currYearString, currMonthString, typeLine,
	    		countryGoalMap,lineGoalMap,lineNameList);
	    */
		
		//命名sheet
		for (int i = 0; i < sheetNameList.size(); i++) {
			wb.setSheetName(i, sheetNameList.get(i));
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = year+"运营业绩报告" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//导出月度运营业绩报告
	@RequestMapping(value = {"expResultsByMonth"})
	public String expResultsByMonth(String month, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException{
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		
		Map<String, HSSFCellStyle> styleMap = getAllStyle(wb);
		
		DateFormat format = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		if (StringUtils.isEmpty(month)) {
			month = format.format(DateUtils.addMonths(new Date(), -1));
		}
		String year = month.substring(0, 4);
		//产品类型对应的产品线关系
		Map<String, String> typeLine = dictService.getTypeLine(month);
		// 按产品类型统计销售额		日期[类型	[国家 /销售数据]]
		Map<String,Map<String, Map<String, SaleReportMonthType>>> data = saleReportMonthTypeService.getSalesResult(year, true);
		//按产品线统计销售额		日期[产品线	[国家 /销售额]]
		Map<String,Map<String, Map<String, SaleReportMonthType>>> lineData = changeLineDataLine(data, typeLine);
		String startMonth = year + "01";
		String endMonth = year + "12";
		EnterpriseGoal enterpriseGoal = new EnterpriseGoal();
		enterpriseGoal.setStartMonth(monthFormat.parse(startMonth));
		enterpriseGoal.setEndMonth(monthFormat.parse(endMonth));
		//分产品线目标	[月份 [产品线[国家   目标]]]
		Map<String,Map<String,Map<String, EnterpriseGoal>>> lineGoalMap = enterpriseGoalService.findLineGoalWithTotalAndEn(enterpriseGoal);
		
		List<String> countryList = Lists.newArrayList("en","de","fr","it","es","jp","mx");
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 400);
		HSSFCell cell = null;
		List<String>  title = Lists.newArrayList("国家","产品线","销售额","税后销售额","销量","税后单价");
		boolean flag = true;//UserUtils.hasPermission("amazoninfo:profits:view");
		if (flag) {
			title.add("毛利润");
			title.add("税前毛利率");
			title.add("税后毛利率");
		}
		title.add("销售额目标");
		if (flag) {
			title.add("毛利率指标");
			title.add("毛利润目标");
		}
		title.add("销售额完成率");
		if (flag) {
			title.add("毛利润完成率");
		}
		title.add("销售额绩效");
		title.add("毛利绩效");
		title.add("总绩效");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
			
		Map<String, Map<String, SaleReportMonthType>> typeMap = lineData.get(month);
		int rowIndex = 1;

		float allSales = 0f;
		float allSalesNoTax = 0f;
		int allSaleVolume = 0;
		float allProfits = 0f;
		float allGoal = 0f;
		float allProfitGoal = 0f;
		
		float nonEnAllSales = 0f;
		float nonEnAllSalesNoTax = 0f;
		int nonEnAllSaleVolume = 0;
		float nonEnAllProfits = 0f;
		float nonEnAllGoal = 0f;
		float nonEnAllProfitGoal = 0f;
		List<Integer> indexList = Lists.newArrayList();
		//预留行,在后面填充非英语国家数据
		Map<String, HSSFRow> rowMap = Maps.newHashMap();
		for (String country : countryList) {
			float totalSales = 0f;
			float totalSalesNoTax = 0f;
			int totalSaleVolume = 0;
			float totalProfits = 0f;
			float totalGoal = 0f;
			float totalProfitGoal = 0f;
			for (Map.Entry<String, Map<String,SaleReportMonthType>> entry: typeMap.entrySet()) { 
			    String line = entry.getKey();
				if ("total".equals(line)) {
					continue;
				}
				int m = 0;
				Map<String, SaleReportMonthType> countryMap = entry.getValue();
				if (countryMap.get(country) == null) {
					continue;
				}
				row=sheet.createRow(rowIndex++);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(country.toUpperCase());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(line);
				//销售额
				float sales = countryMap.get(country).getSales();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales);
				//税后销售额
				float salesNoTax = countryMap.get(country).getSalesNoTax();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesNoTax);
				//销量
				float salesVolume = countryMap.get(country).getSalesVolume();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);
				//税后单价
				if (salesVolume > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesNoTax/salesVolume);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				float profits = countryMap.get(country).getProfits();
				if (flag) {
					//毛利润
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits);
					//税前毛利率
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(countryMap.get(country).getProfitRate());
					//税后毛利率
					if (salesNoTax > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/salesNoTax);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
				}
				//销售额目标
				float goal = 0f;
				try {
					goal = lineGoalMap.get(month).get(line).get(country).getGoal();
				} catch (NullPointerException e) {}
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(goal);
				if (flag) {
					//毛利率指标
					//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(getRate(year, country, line));
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				
				float profitGoal = 0f;
				try {
					profitGoal = lineGoalMap.get(month).get(line).get(country).getProfitGoal();
				} catch (NullPointerException e) {}
				
				if (profitGoal < 1) {
					profitGoal = goal * getRate(year, country, line);
				}
				if (flag) {
					//毛利目标
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitGoal);
				}
				//销售额完成率
				float salesRate = 0;
				if (goal > 0) {
					salesRate = sales/goal;
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesRate);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//毛利完成率
				float profitRate = 0;
				if (profitGoal > 0) {
					profitRate = profits/profitGoal;
				}
				if (flag) {
					if (profitGoal > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitRate);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
				}
				//销售额绩效
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
				//毛利绩效
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
				//总绩效
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesRate * 0.5 + profitRate * 0.5);
				//if (!"E".equals(line)) {
					totalSales += sales;
					totalSalesNoTax += salesNoTax;
					totalSaleVolume += salesVolume;
					totalProfits += profits;
					totalGoal += goal;
					totalProfitGoal += profitGoal;
				//}
			}
			//国家总计,E线除外
			if (!(totalSales >0)) {
				continue;
			}
			int m = 0;
			indexList.add(rowIndex);
			row=sheet.createRow(rowIndex++);
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(country.toUpperCase());
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("Total");
			//销售额
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalSales);
			//税后销售额
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalSalesNoTax);
			//销量
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalSaleVolume);
			//税后单价
			if (totalSaleVolume > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalSalesNoTax/totalSaleVolume);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			if (flag) {
				//毛利润
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalProfits);
				//税前毛利率
				if (totalSales > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalProfits/totalSales);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//税后毛利率
				if (totalSalesNoTax > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalProfits/totalSalesNoTax);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}
			//销售额目标
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalGoal);
			if (flag) {
				//毛利率指标
				//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(getRate(year, country, null));
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//毛利目标
			if (flag) {
				//毛利目标
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalProfitGoal);
			}
			//销售额完成率
			float salesRate = 0;
			if (totalGoal > 0) {
				salesRate = totalSales/totalGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			float profitRate = 0;
			if (totalProfitGoal > 0) {
				profitRate = totalProfits/totalProfitGoal;
			}
			if (flag) {
				//毛利完成率
				if (totalProfitGoal > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitRate);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}
			//销售额绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//毛利绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//总绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesRate * 0.5 + profitRate * 0.5);
			allSales += totalSales;
			allSalesNoTax += totalSalesNoTax;
			allSaleVolume += totalSaleVolume;
			allProfits += totalProfits;
			allGoal += totalGoal;
			allProfitGoal += totalProfitGoal;
			if (!"en".equals(country)) {
				nonEnAllSales += totalSales;
				nonEnAllSalesNoTax += totalSalesNoTax;
				nonEnAllSaleVolume += totalSaleVolume;
				nonEnAllProfits += totalProfits;
				nonEnAllGoal += totalGoal;
				nonEnAllProfitGoal += totalProfitGoal;
			}
			if ("en".equals(country)) {
				// 把非英语国家放到英语国家后面,英语国家统计完成了预留行
				for (String line : typeMap.keySet()) {
					if ("total".equals(line)) {
						continue;
					}
					HSSFRow hssfRow = sheet.createRow(rowIndex++);
					rowMap.put(line, hssfRow);
				}
				indexList.add(rowIndex);	//标记总几行
				HSSFRow hssfRow = sheet.createRow(rowIndex++);
				rowMap.put("total", hssfRow);
			}
		}
		//非英语国家产品线汇总,总计减去英语国家数据
		for (String line : typeMap.keySet()) {
			if ("total".equals(line)) {
				continue;
			}
			int m = 0;
			Map<String, SaleReportMonthType> countryMap = typeMap.get(line);
			if (countryMap.get("total") == null) {
				continue;
			}
			//row=sheet.createRow(rowIndex++);
			row = rowMap.get(line);
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("NON-EN");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(line);
			//销售额
			float sales = countryMap.get("total").getSales()-countryMap.get("en").getSales();;
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales);
			//税后销售额
			float salesNoTax = countryMap.get("total").getSalesNoTax()-countryMap.get("en").getSalesNoTax();
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesNoTax);
			//销量
			float salesVolume = countryMap.get("total").getSalesVolume()-countryMap.get("en").getSalesVolume();
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);
			//税后单价
			if (salesVolume > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesNoTax/salesVolume);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			float profits = countryMap.get("total").getProfits()-countryMap.get("en").getProfits();
			if (flag) {
				//毛利润
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits);
				//税前毛利率
				if (sales > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/sales);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//税后毛利率
				if (salesNoTax > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/salesNoTax);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}
			//销售额目标
			float goal = 0f;
			try {
				goal = lineGoalMap.get(month).get(line).get("total").getGoal()-lineGoalMap.get(month).get(line).get("en").getGoal();
			} catch (NullPointerException e) {}
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(goal);
			if (flag) {
				//毛利率指标,暂无
				//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(getRate("total", line));
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			float profitGoal = goal * getRate(year, "total", line);	//没有利润目标的按比例算,如E线
			try {
				profitGoal = lineGoalMap.get(month).get(line).get("total").getProfitGoal()-lineGoalMap.get(month).get(line).get("en").getProfitGoal();
			} catch (NullPointerException e) {}
			if (flag) {
				//毛利目标
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitGoal);
			}
			//销售额完成率
			float salesRate = 0;
			if (goal > 0) {
				salesRate = sales/goal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//毛利完成率
			float profitRate = 0;
			if (profitGoal > 0) {
				profitRate = profits/profitGoal;
			}
			if (flag) {
				if (profitGoal > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitRate);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}
			//销售额绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//毛利绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//总绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesRate * 0.5 + profitRate * 0.5);
		}
		//非英语国家总计ALL
		int m = 0;
		//indexList.add(rowIndex);
		//row=sheet.createRow(rowIndex++);
		row = rowMap.get("total");
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("NON-EN");
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("Total");
		//销售额
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllSales);
		//税后销售额
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllSalesNoTax);
		//销量
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllSaleVolume);
		//税后单价
		if (nonEnAllSaleVolume > 0) {
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllSalesNoTax/nonEnAllSaleVolume);
		} else {
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
		}
		if (flag) {
			//毛利润
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllProfits);
			//税前毛利率
			if (nonEnAllSales > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllProfits/nonEnAllSales);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//税后毛利率
			if (nonEnAllSalesNoTax > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllProfits/nonEnAllSalesNoTax);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
		}
		//销售额目标
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllGoal);
		if (flag) {
			//毛利率指标,暂无
			//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(getRate("total", null));
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
		}
		//毛利目标
		if (flag) {
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllProfitGoal);
		}
		//销售额完成率
		float salesRate = 0;
		if (nonEnAllGoal > 0) {
			salesRate = nonEnAllSales/nonEnAllGoal;
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesRate);
		} else {
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
		}
		float profitRate = 0;
		if (nonEnAllProfitGoal > 0) {
			profitRate = nonEnAllProfits/nonEnAllProfitGoal;
		}
		if (flag) {
			//毛利完成率
			if (nonEnAllProfitGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
		}
		//销售额绩效
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
		//毛利绩效
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
		//总绩效
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesRate * 0.5 + profitRate * 0.5);
		//非英语国家总计end
		//分产品线总计
		for (String line : typeMap.keySet()) {
			if ("total".equals(line)) {
				continue;
			}
			m = 0;
			Map<String, SaleReportMonthType> countryMap = typeMap.get(line);
			if (countryMap.get("total") == null) {
				continue;
			}
			row=sheet.createRow(rowIndex++);
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("TOTAL");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(line);
			//销售额
			float sales = countryMap.get("total").getSales();
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales);
			//税后销售额
			float salesNoTax = countryMap.get("total").getSalesNoTax();
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesNoTax);
			//销量
			float salesVolume = countryMap.get("total").getSalesVolume();
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);
			//税后单价
			if (salesVolume > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesNoTax/salesVolume);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			float profits = countryMap.get("total").getProfits();
			if (flag) {
				//毛利润
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits);
				//税前毛利率
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(countryMap.get("total").getProfitRate());
				//税后毛利率
				if (salesNoTax > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/salesNoTax);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}
			//销售额目标
			float goal = 0f;
			try {
				goal = lineGoalMap.get(month).get(line).get("total").getGoal();
			} catch (NullPointerException e) {}
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(goal);
			if (flag) {
				//毛利率指标
				//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(getRate(year, "total", line));
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			float profitGoal = goal * getRate(year, "total", line);	//没有利润目标的按比例算,如E线
			try {
				profitGoal = lineGoalMap.get(month).get(line).get("total").getProfitGoal();
			} catch (NullPointerException e) {}
			if (flag) {
				//毛利目标
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitGoal);
			}
			//销售额完成率
			salesRate = 0;
			if (goal > 0) {
				salesRate = sales/goal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//毛利完成率
			profitRate = 0;
			if (profitGoal > 0) {
				profitRate = profits/profitGoal;
			}
			if (flag) {
				if (profitGoal > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitRate);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}
			//销售额绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//毛利绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//总绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesRate * 0.5 + profitRate * 0.5);
		}
		//ALL
		m = 0;
		indexList.add(rowIndex);
		row=sheet.createRow(rowIndex++);
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("TOTAL");
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("Total");
		//销售额
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allSales);
		//税后销售额
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allSalesNoTax);
		//销量
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allSaleVolume);
		//税后单价
		if (allSaleVolume > 0) {
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allSalesNoTax/allSaleVolume);
		} else {
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
		}
		if (flag) {
			//毛利润
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits);
			//税前毛利率
			if (allSales > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits/allSales);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//税后毛利率
			if (allSalesNoTax > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits/allSalesNoTax);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
		}
		//销售额目标
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allGoal);
		if (flag) {
			//毛利率指标
			//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(getRate(year, "total", null));
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
		}
		//毛利目标
		if (flag) {
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfitGoal);
		}
		//销售额完成率
		salesRate = 0;
		if (allGoal > 0) {
			salesRate = allSales/allGoal;
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesRate);
		} else {
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
		}
		profitRate = 0;
		if (allProfitGoal > 0) {
			profitRate = allProfits/allProfitGoal;
		}
		if (flag) {
			//毛利完成率
			if (allProfitGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
		}
		//销售额绩效
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
		//毛利绩效
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
		//总绩效
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesRate * 0.5 + profitRate * 0.5);
		
		for (int i = 1; i < rowIndex; i++) {
			for (int j = 0; j < title.size(); j++) {
				if (flag) {
					try {
						if (indexList.contains(i)) {
							if (j < 2 || j == 14 || j == 15) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorStyle"));
							} else if (j == 2 || j == 3 || j == 4 || j == 6 || j == 9 || j == 11) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorIntStyle"));
							} else if (j == 5 || j == 16) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorDecStyle"));
							} else {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorPercentageInt"));
							}
						} else {
							if (j < 2 || j == 14 || j == 15) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
							} else if (j == 2 || j == 3 || j == 4 || j == 6 || j == 9 || j == 11) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("intStyle"));
							} else if (j == 5 || j == 16) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("decimalOneStyle"));
							} else {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("percentageInt"));
							}
						}
					} catch (Exception e) {}
				} else {
					try {
						if (indexList.contains(i)) {
							if (j < 2 || j == 8 || j == 9) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorStyle"));
							} else if (j == 2 || j == 3 || j == 4 || j == 6) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorIntStyle"));
							} else if (j == 5 || j == 10) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorDecStyle"));
							} else {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorPercentageInt"));
							}
						} else {
							if (j < 2 || j == 8 || j == 9) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
							} else if (j == 2 || j == 3 || j == 4 || j == 6) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("intStyle"));
							} else if (j == 5 || j == 10) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("decimalOneStyle"));
							} else {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("percentageInt"));
							}
						}
						
					} catch (Exception e) {}
				}
			}
	    }
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short)i, true);
		}
		
		//[type[country	goal]]
		Map<String, Map<String, EnterpriseTypeGoal>> typeGoalMap = enterpriseTypeGoalService.findTypeGoalByMonth(month);
		sheet = wb.createSheet();
		row = sheet.createRow(0);
		row.setHeight((short) 400);
		title = Lists.newArrayList("国家","产品类型","销售额","税后销售额","销量","税后单价");
		if (flag) {
			title.add("毛利润");
			title.add("税前毛利率");
			title.add("税后毛利率");
		}
		title.add("销售额目标");
		if (flag) {
			title.add("毛利润目标");
		}
		title.add("销售额完成率");
		if (flag) {
			title.add("毛利润完成率");
		}
		title.add("销售额绩效");
		title.add("毛利绩效");
		title.add("总绩效");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
			
		typeMap = data.get(month);
		rowIndex = 1;

		allSales = 0f;
		allSalesNoTax = 0f;
		allSaleVolume = 0;
		allProfits = 0f;
		allGoal = 0f;
		allProfitGoal = 0f;
		nonEnAllSales = 0f;
		nonEnAllSalesNoTax = 0f;
		nonEnAllSaleVolume = 0;
		nonEnAllProfits = 0f;
		nonEnAllGoal = 0f;
		nonEnAllProfitGoal = 0f;
		indexList = Lists.newArrayList();
		//所有在售的产品类型
		List<String> typeList = dictService.getAllProductTypeList();
		rowMap.clear();	//清空,重新计算
		for (String country : countryList) {
			float totalSales = 0f;
			float totalSalesNoTax = 0f;
			int totalSaleVolume = 0;
			float totalProfits = 0f;
			float totalSalesGoal = 0f;
			float totalProfitsGoal = 0f;
			//for (String type : typeMap.keySet()) {
			for (String type : typeList) {
				if ("total".equals(type)) {
					continue;
				}
				//没有销售额也没定目标则跳过
				if ((typeMap.get(type) == null || typeMap.get(type).get(country) == null) && 
						(typeGoalMap.get(type)==null || typeGoalMap.get(type).get(country) == null)) {
					continue;
				}
				m = 0;
				Map<String, SaleReportMonthType> countryMap = typeMap.get(type);
//				if (countryMap.get(country) == null) {
//					continue;
//				}
				row=sheet.createRow(rowIndex++);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(country.toUpperCase());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
				//销售额
				float sales = 0f;
				try {
					sales = countryMap.get(country).getSales();
				} catch (NullPointerException e) {}
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales);
				//税后销售额
				float salesNoTax = 0f;
				try {
					salesNoTax = countryMap.get(country).getSalesNoTax();
				} catch (NullPointerException e) {}
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesNoTax);
				//销量
				float salesVolume = 0f;
				try {
					salesVolume = countryMap.get(country).getSalesVolume();
				} catch (NullPointerException e) {}
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);
				//税后单价
				if (salesVolume > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesNoTax/salesVolume);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				float profits = 0f;
				try {
					profits = countryMap.get(country).getProfits();
				} catch (NullPointerException e) {}
				if (flag) {
					//毛利润
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits);
					//税前毛利率
					if (countryMap != null && countryMap.get(country) != null) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(countryMap.get(country).getProfitRate());
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					
					//税后毛利率
					if (salesNoTax > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/salesNoTax);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
				}
				//销售额目标
				float typeSalesGoal = 0f;
				try {
					typeSalesGoal = typeGoalMap.get(type).get(country).getSalesGoal();
				} catch (NullPointerException e) {}
				if (typeSalesGoal > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesGoal);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//利润目标
				float typeProfitsGoal = 0f;
				try {
					typeProfitsGoal = typeGoalMap.get(type).get(country).getProfitGoal();
				} catch (NullPointerException e) {}
				if (typeProfitsGoal > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfitsGoal);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				
				//销售额完成率
				float typeSalesRate = 0f;
				if (typeSalesGoal > 0) {
					typeSalesRate = sales/typeSalesGoal;
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesRate);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				
				//利润完成率
				float typeProfitsRate = 0f;
				if (typeProfitsGoal > 0) {
					typeProfitsRate = profits/typeProfitsGoal;
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfitsRate);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//销售额绩效
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
				//毛利绩效
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
				//总绩效
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesRate * 0.5 + typeProfitsRate * 0.5);
				
				totalSales += sales;
				totalSalesNoTax += salesNoTax;
				totalSaleVolume += salesVolume;
				totalProfits += profits;
				totalSalesGoal += typeSalesGoal;
				totalProfitsGoal += typeProfitsGoal;
			}
			//国家总计
			if (!(totalSales >0)) {
				continue;
			}
			m = 0;
			indexList.add(rowIndex);
			row=sheet.createRow(rowIndex++);
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(country.toUpperCase());
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("Total");
			//销售额
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalSales);
			//税后销售额
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalSalesNoTax);
			//销量
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalSaleVolume);
			//税后单价
			if (totalSaleVolume > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalSalesNoTax/totalSaleVolume);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			if (flag) {
				//毛利润
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalProfits);
				//税前毛利率
				if (totalSales > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalProfits/totalSales);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//税后毛利率
				if (totalSalesNoTax > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalProfits/totalSalesNoTax);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}
			//销售额目标
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalSalesGoal);
			//利润目标
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(totalProfitsGoal);
			
			//销售额完成率
			float typeSalesRate = 0f;
			if (totalSalesGoal > 0) {
				typeSalesRate = totalSales/totalSalesGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			//利润完成率
			float typeProfitsRate = 0f;
			if (totalProfitsGoal > 0) {
				typeProfitsRate = totalProfits/totalProfitsGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfitsRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//销售额绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//毛利绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//总绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesRate * 0.5 + typeProfitsRate * 0.5);
			
			allSales += totalSales;
			allSalesNoTax += totalSalesNoTax;
			allSaleVolume += totalSaleVolume;
			allProfits += totalProfits;
			allGoal += totalSalesGoal;
			allProfitGoal += totalProfitsGoal;
			if (!"en".equals(country)) {
				nonEnAllSales += totalSales;
				nonEnAllSalesNoTax += totalSalesNoTax;
				nonEnAllSaleVolume += totalSaleVolume;
				nonEnAllProfits += totalProfits;
				nonEnAllGoal += totalSalesGoal;
				nonEnAllProfitGoal += totalProfitsGoal;
			}
			if ("en".equals(country)) {
				//把非英语国家放到英语国家后面,英语国家统计完成了预留行
				for (String type : typeList) {
					if ("total".equals(type) || "E".equals(typeLine.get(type.toLowerCase()))) {
						continue;
					}
					//没有销售额也没定目标则跳过
					if ((typeMap.get(type) == null || typeMap.get(type).get(country) == null) && 
							(typeGoalMap.get(type)==null || typeGoalMap.get(type).get(country) == null)) {
						continue;
					}
					HSSFRow hssfRow = sheet.createRow(rowIndex++);
					rowMap.put(type, hssfRow);
				}
				indexList.add(rowIndex);	//标记总几行
				HSSFRow hssfRow = sheet.createRow(rowIndex++);
				rowMap.put("total", hssfRow);
			}
		}
		//非英语国家分产品类型
		for (String type : typeList) {
			if ("total".equals(type) || "E".equals(typeLine.get(type.toLowerCase()))) {
				continue;
			}
			m = 0;
			Map<String, SaleReportMonthType> countryMap = typeMap.get(type);
			if (countryMap==null || countryMap.get("total") == null) {
				continue;
			}
			//row=sheet.createRow(rowIndex++);
			row=rowMap.get(type);
			if (row == null) {
				continue;
			}
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("NON-EN");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
			//销售额
			float sales = countryMap.get("total").getSales();
			try {
				sales = sales -countryMap.get("en").getSales(); 
			} catch (NullPointerException e) {}
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales);
			//税后销售额
			float salesNoTax = countryMap.get("total").getSalesNoTax();
			try {
				salesNoTax = salesNoTax -countryMap.get("en").getSalesNoTax(); 
			} catch (NullPointerException e) {}
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesNoTax);
			//销量
			float salesVolume = countryMap.get("total").getSalesVolume();
			try {
				salesVolume = salesVolume -countryMap.get("en").getSalesVolume(); 
			} catch (NullPointerException e) {}
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);
			//税后单价
			if (salesVolume > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesNoTax/salesVolume);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			float profits = countryMap.get("total").getProfits();
			try {
				profits = profits - countryMap.get("en").getProfits();
			} catch (NullPointerException e) {}
			if (flag) {
				//毛利润
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits);
				//税前毛利率
				if (sales > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/sales);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				
				//税后毛利率
				if (salesNoTax > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/salesNoTax);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}
			//销售额目标
			float typeSalesGoal = 0f;
			try {
				typeSalesGoal = typeGoalMap.get(type).get("total").getSalesGoal();
			} catch (NullPointerException e) {}
			try {
				typeSalesGoal = typeSalesGoal - typeGoalMap.get(type).get("en").getSalesGoal();
			} catch (NullPointerException e) {}
			if (typeSalesGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesGoal);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//利润目标
			float typeProfitsGoal = 0f;
			try {
				typeProfitsGoal = typeGoalMap.get(type).get("total").getProfitGoal();
			} catch (NullPointerException e) {}
			try {
				typeProfitsGoal = typeProfitsGoal - typeGoalMap.get(type).get("en").getProfitGoal();
			} catch (NullPointerException e) {}
			if (typeProfitsGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfitsGoal);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			//销售额完成率
			float typeSalesRate = 0f;
			if (typeSalesGoal > 0) {
				typeSalesRate = sales/typeSalesGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			//利润完成率
			float typeProfitsRate = 0f;
			if (typeProfitsGoal > 0) {
				typeProfitsRate = profits/typeProfitsGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfitsRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//销售额绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//毛利绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//总绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesRate * 0.5 + typeProfitsRate * 0.5);
			
		}
		//非英语国家总计ALL
		m = 0;
		//indexList.add(rowIndex);
		//row=sheet.createRow(rowIndex++);
		row=rowMap.get("total");
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("NON-EN");
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("Total");
		//销售额
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllSales);
		//税后销售额
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllSalesNoTax);
		//销量
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllSaleVolume);
		//税后单价
		if (nonEnAllSaleVolume > 0) {
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllSalesNoTax/nonEnAllSaleVolume);
		} else {
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
		}
		if (flag) {
			//毛利润
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllProfits);
			//税前毛利率
			if (nonEnAllSales > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllProfits/nonEnAllSales);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//税后毛利率
			if (nonEnAllSalesNoTax > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllProfits/nonEnAllSalesNoTax);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//销售额目标
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllGoal);
			//利润目标
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nonEnAllProfitGoal);
			
			//销售额完成率
			float typeSalesRate = 0f;
			if (nonEnAllGoal > 0) {
				typeSalesRate = nonEnAllSales/nonEnAllGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			//利润完成率
			float typeProfitsRate = 0f;
			if (nonEnAllProfitGoal > 0) {
				typeProfitsRate = nonEnAllProfits/nonEnAllProfitGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfitsRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//销售额绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//毛利绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//总绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesRate * 0.5 + typeProfitsRate * 0.5);
		}
		//非英语国家end
		//分产品类型总计
		for (String type : typeList) {
			if ("total".equals(type) || "E".equals(typeLine.get(type.toLowerCase()))) {
				continue;
			}
			m = 0;
			Map<String, SaleReportMonthType> countryMap = typeMap.get(type);
			if (countryMap==null || countryMap.get("total") == null) {
				continue;
			}
			row=sheet.createRow(rowIndex++);
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("TOTAL");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
			//销售额
			float sales = countryMap.get("total").getSales();
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales);
			//税后销售额
			float salesNoTax = countryMap.get("total").getSalesNoTax();
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesNoTax);
			//销量
			float salesVolume = countryMap.get("total").getSalesVolume();
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);
			//税后单价
			if (salesVolume > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesNoTax/salesVolume);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			float profits = countryMap.get("total").getProfits();
			if (flag) {
				//毛利润
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits);
				//税前毛利率
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(countryMap.get("total").getProfitRate());
				//税后毛利率
				if (salesNoTax > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/salesNoTax);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}
			//销售额目标
			float typeSalesGoal = 0f;
			try {
				typeSalesGoal = typeGoalMap.get(type).get("total").getSalesGoal();
			} catch (NullPointerException e) {}
			if (typeSalesGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesGoal);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//利润目标
			float typeProfitsGoal = 0f;
			try {
				typeProfitsGoal = typeGoalMap.get(type).get("total").getProfitGoal();
			} catch (NullPointerException e) {}
			if (typeProfitsGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfitsGoal);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			//销售额完成率
			float typeSalesRate = 0f;
			if (typeSalesGoal > 0) {
				typeSalesRate = sales/typeSalesGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			//利润完成率
			float typeProfitsRate = 0f;
			if (typeProfitsGoal > 0) {
				typeProfitsRate = profits/typeProfitsGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfitsRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//销售额绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//毛利绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//总绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesRate * 0.5 + typeProfitsRate * 0.5);
			
		}
		//ALL
		m = 0;
		indexList.add(rowIndex);
		row=sheet.createRow(rowIndex++);
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("TOTAL");
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("Total");
		//销售额
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allSales);
		//税后销售额
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allSalesNoTax);
		//销量
		row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allSaleVolume);
		//税后单价
		if (allSaleVolume > 0) {
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allSalesNoTax/allSaleVolume);
		} else {
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
		}
		if (flag) {
			//毛利润
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits);
			//税前毛利率
			if (allSales > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits/allSales);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//税后毛利率
			if (allSalesNoTax > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits/allSalesNoTax);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//销售额目标
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allGoal);
			//利润目标
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfitGoal);
			
			//销售额完成率
			float typeSalesRate = 0f;
			if (allGoal > 0) {
				typeSalesRate = allSales/allGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			//利润完成率
			float typeProfitsRate = 0f;
			if (allProfitGoal > 0) {
				typeProfitsRate = allProfits/allProfitGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfitsRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//销售额绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//毛利绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(0.5);
			//总绩效
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSalesRate * 0.5 + typeProfitsRate * 0.5);
		}
		
		for (int i = 1; i < rowIndex; i++) {
			for (int j = 0; j < title.size(); j++) {
				if (flag) {
					try {
						if (indexList.contains(i)) {
							if (j < 2 || j == 13 || j == 14) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorStyle"));
							} else if (j == 2 || j == 3 || j == 4 || j == 6 || j == 9 || j == 10) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorIntStyle"));
							} else if (j == 5 || j == 15) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorDecStyle"));
							} else {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorPercentageInt"));
							}
						} else {
							if (j < 2 || j == 13 || j == 14) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
							} else if (j == 2 || j == 3 || j == 4 || j == 6 || j == 9 || j == 10) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("intStyle"));
							} else if (j == 5 || j == 15) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("decimalOneStyle"));
							} else {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("percentageInt"));
							}
						}
					} catch (Exception e) {}
				} else {
					try {
						if (indexList.contains(i)) {
							if (j < 2 || j == 8 || j == 9) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorStyle"));
							} else if (j == 2 || j == 3 || j == 4 || j == 6) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorIntStyle"));
							} else if (j == 5 || j == 10) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorDecStyle"));
							} else {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorPercentageInt"));
							}
						} else {
							if (j < 2 || j == 8 || j == 9) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
							} else if (j == 2 || j == 3 || j == 4 || j == 6) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("intStyle"));
							} else if (j == 5 || j == 10) {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("decimalOneStyle"));
							} else {
								sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("percentageInt"));
							}
						}
						
					} catch (Exception e) {}
				}
			}
	    }
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short)i, true);
		}
		
		Map<String, SaleReportMonthType> allTypeData = saleReportMonthTypeService.getAllTypeSalesByMonth(month);
		Map<String, SaleReportMonthType> allLineData = saleReportMonthTypeService.getAllLineSalesByMonth(month);
		Map<String, EnterpriseTypeGoal> allTypeGoal = enterpriseTypeGoalService.findAllTypeGoalByMonth(month);
		Map<String, EnterpriseGoal> allLineGoal = enterpriseGoalService.getAllLineGoalByMonth(month);
	    //产品经理绩效
	    HSSFSheet productManagerSheet = wb.createSheet();
	    fillProductManagerSheet(productManagerSheet, styleMap, month, allTypeData, allLineData, allTypeGoal, allLineGoal);
			
		//命名sheet
		wb.setSheetName(0, month + "月分产品线绩效报表");
		wb.setSheetName(1, month + "月分产品类型绩效报表");
		wb.setSheetName(2, month + "月产品经理绩效报表");
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = month+"月绩效报表" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//产品经理绩效sheet
	private void fillProductManagerSheet(HSSFSheet sheet, Map<String, HSSFCellStyle> styleMap, String month,
			Map<String, SaleReportMonthType> allTypeData, Map<String, SaleReportMonthType> allLineData,
			Map<String, EnterpriseTypeGoal> allTypeGoal, Map<String, EnterpriseGoal> allLineGoal) {
		
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 400);
		HSSFCell cell = null;
		//按产品类型统计
		List<String>  title = Lists.newArrayList("日期","产品类型","产品线","销售额目标","实际销售额","销售额完成率","销售额占比",
				"利润目标","实际利润","利润完成率","利润占比","利润率"," 绩效 ");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		
		int  rowIndex=1;
		List<Integer> indexList = Lists.newArrayList();
		//汇总数据
		SaleReportMonthType totalMonthType = allTypeData.get("total");
		for (Map.Entry<String,SaleReportMonthType> entry : allTypeData.entrySet()) { 
		    String productType = entry.getKey();
			if (productType.equals("total")) {
				continue;
			}
			int m=0;
			row=sheet.createRow(rowIndex++);
			//日期
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month);
			//产品类型
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(productType);
			
			SaleReportMonthType monthType = entry.getValue();
			EnterpriseTypeGoal typeGoal = allTypeGoal.get(productType);
			//产品线
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(monthType.getLine());
			float typeSaleGoal = 0f;	//销售目标
			float typeProfitGoal = 0f;	//利润目标
			if (typeGoal != null) {
				typeSaleGoal = typeGoal.getSalesGoal();
				typeProfitGoal = typeGoal.getProfitGoal();
			}
			//销售目标
			if (typeSaleGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSaleGoal);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			float typeSale = monthType.getSales();	//销售额
			float typeProfit = monthType.getProfits();	//利润
			//销售额
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSale);
			//销售额完成率
			float saleRate = 0f;
			if (typeSaleGoal > 0) {
				saleRate = typeSale/typeSaleGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(saleRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//销售额占比
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSale/totalMonthType.getSales());
			//利润目标
			if (typeProfitGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfitGoal);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			//实际利润
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfit);
			//利润完成率
			float profitRate = 0f;
			if (typeProfitGoal > 0) {
				profitRate = typeProfit/typeProfitGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//利润占比
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfit/totalMonthType.getProfits());
			//利润率
			if (typeSale > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfit/typeSale);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//绩效
			if (saleRate > 0 || profitRate > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(saleRate * 0.5 + profitRate * 0.5);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
		}
		
		if (totalMonthType != null) {
			int m=0;
			indexList.add(rowIndex);
			row=sheet.createRow(rowIndex++);
			//日期
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month);
			//产品类型
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("Total");
			
			SaleReportMonthType monthType = allTypeData.get("total");
			EnterpriseTypeGoal typeGoal = allTypeGoal.get("total");
			//产品线
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("total");
			float typeSaleGoal = 0f;	//销售目标
			float typeProfitGoal = 0f;	//利润目标
			if (typeGoal != null) {
				typeSaleGoal = typeGoal.getSalesGoal();
				typeProfitGoal = typeGoal.getProfitGoal();
			}
			//销售目标
			if (typeSaleGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSaleGoal);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			float typeSale = monthType.getSales();	//销售额
			float typeProfit = monthType.getProfits();	//利润
			//销售额
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSale);
			//销售额完成率
			float saleRate = 0f;
			if (typeSaleGoal > 0) {
				saleRate = typeSale/typeSaleGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(saleRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//销售额占比
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeSale/totalMonthType.getSales());
			//利润目标
			if (typeProfitGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfitGoal);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			//实际利润
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfit);
			//利润完成率
			float profitRate = 0f;
			if (typeProfitGoal > 0) {
				profitRate = typeProfit/typeProfitGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//利润占比
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfit/totalMonthType.getProfits());
			//利润率
			if (typeSale > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeProfit/typeSale);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//绩效
			if (saleRate > 0 || profitRate > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(saleRate * 0.5 + profitRate * 0.5);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
		}
		
		for (int i = 1; i < rowIndex; i++) {
			for (int j = 0; j < title.size(); j++) {
				try {
					if (indexList.contains(i)) {
						if (j < 3) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorStyle"));
						} else if (j == 3 || j == 4 || j == 7 || j == 8) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorIntStyle"));
						}else if (j == 5 || j == 6 || j == 9 || j == 10 || j == 11) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorPercentageInt"));
						} else {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorDecStyle"));
						}
					} else {
						if (j < 3) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
						} else if (j == 3 || j == 4 || j == 7 || j == 8) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("intStyle"));
						}else if (j == 5 || j == 6 || j == 9 || j == 10 || j == 11) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("percentageInt"));
						} else {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("decimalOneStyle"));
						}
					}
					
				} catch (Exception e) {}
			}
	    }

		rowIndex++;	//中间隔一行
		//按产品线统计
		title = Lists.newArrayList("日期","产品线","销售额目标","实际销售额","销售额完成率","销售额占比",
				"利润目标","实际利润","利润完成率","利润占比","利润率"," 绩效 ");
		row=sheet.createRow(rowIndex++);
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		
		int flag = rowIndex;
		//汇总数据
		SaleReportMonthType totalLineMonthType = allLineData.get("total");
		for (Map.Entry<String, SaleReportMonthType>  entry: allLineData.entrySet()) { 
		    String line = entry.getKey();
			if (line.equals("total")) {
				continue;
			}
			int m=0;
			row=sheet.createRow(rowIndex++);
			SaleReportMonthType monthType = entry.getValue();
			EnterpriseGoal lineGoal = allLineGoal.get(line);
			//日期
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month);
			//产品线
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(line);
			float lineSaleGoal = 0f;	//销售目标
			float lineProfitGoal = 0f;	//利润目标
			if (lineGoal != null) {
				lineSaleGoal = lineGoal.getGoal();
				lineProfitGoal = lineGoal.getProfitGoal();
			}
			//销售目标
			if (lineSaleGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineSaleGoal);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			float lineSale = monthType.getSales();	//销售额
			float lineProfit = monthType.getProfits();	//利润
			//销售额
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineSale);
			//销售额完成率
			float saleRate = 0f;
			if (lineSaleGoal > 0) {
				saleRate = lineSale/lineSaleGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(saleRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//销售额占比
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineSale/totalLineMonthType.getSales());
			//利润目标
			if (lineProfitGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineProfitGoal);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			//实际利润
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineProfit);
			//利润完成率
			float profitRate = 0f;
			if (lineProfitGoal > 0) {
				profitRate = lineProfit/lineProfitGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//利润占比
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineProfit/totalLineMonthType.getProfits());
			//利润率
			if (lineSale > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineProfit/lineSale);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//绩效
			if (saleRate > 0 || profitRate > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(saleRate * 0.5 + profitRate * 0.5);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
		}
		
		if (totalLineMonthType != null) {
			int m = 0;
			indexList.add(rowIndex);
			row = sheet.createRow(rowIndex++);
			//日期
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month);
			//产品线
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("Total");
			
			SaleReportMonthType monthType = allLineData.get("total");
			EnterpriseGoal lineGoal = allLineGoal.get("total");
			float lineSaleGoal = 0f;	//销售目标
			float lineProfitGoal = 0f;	//利润目标
			if (lineGoal != null) {
				lineSaleGoal = lineGoal.getGoal();
				lineProfitGoal = lineGoal.getProfitGoal();
			}
			//销售目标
			if (lineSaleGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineSaleGoal);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			float lineSale = monthType.getSales();	//销售额
			float lineProfit = monthType.getProfits();	//利润
			//销售额
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineSale);
			//销售额完成率
			float saleRate = 0f;
			if (lineSaleGoal > 0) {
				saleRate = lineSale/lineSaleGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(saleRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//销售额占比
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineSale/totalLineMonthType.getSales());
			//利润目标
			if (lineProfitGoal > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineProfitGoal);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			//实际利润
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineProfit);
			//利润完成率
			float profitRate = 0f;
			if (lineProfitGoal > 0) {
				profitRate = lineProfit/lineProfitGoal;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profitRate);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//利润占比
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineProfit/totalLineMonthType.getProfits());
			//利润率
			if (lineSale > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(lineProfit/lineSale);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//绩效
			if (saleRate > 0 || profitRate > 0) {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(saleRate * 0.5 + profitRate * 0.5);
			} else {
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
		}
		
		for (int i = flag; i < rowIndex; i++) {
			for (int j = 0; j < title.size(); j++) {
				try {
					if (indexList.contains(i)) {
						if (j == 0 || j == 1) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorStyle"));
						} else if (j == 2 || j == 3 || j == 6 || j == 7) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorIntStyle"));
						}else if (j == 4 || j == 5 || j == 8 || j == 9 || j == 10) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorPercentageInt"));
						} else {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorDecStyle"));
						}
					} else {
						if (j == 0 || j == 1) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
						} else if (j == 2 || j == 3 || j == 6 || j == 7) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("intStyle"));
						}else if (j == 4 || j == 5 || j == 8 || j == 9 || j == 10) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("percentageInt"));
						} else {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("decimalOneStyle"));
						}
					}
				} catch (Exception e) {}
			}
	    }
		
		for (int i = 0; i < title.size(); i++) {
			try {
				sheet.autoSizeColumn((short)i, true);
			} catch (Exception e) {}
			
		}
		
	}

	private void fillResultSheet(HSSFSheet sheet, Map<String, Map<String, Map<String, SaleReportMonthType>>> data,
			Map<String, HSSFCellStyle> styleMap, String year,
			String currYearString, String currMonthString, Map<String, String> typeLine, Map<String, Map<String, EnterpriseGoal>> countryGoalMap,
			Map<String, Map<String, Map<String, EnterpriseGoal>>> lineGoalMap, List<String> lineNameList) {
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 400);
		HSSFCell cell = null;
		List<String>  title = Lists.newArrayList("日期","产品线","EN","DE","FR","IT","ES","JP","Total");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		String monthNow = currMonthString.substring(4);
		int season = 1;	//当前时间所属季度数
		if ("04".equals(monthNow) || "05".equals(monthNow) || "06".equals(monthNow)) {
			season = 2;
		} else if ("07".equals(monthNow) || "08".equals(monthNow) || "09".equals(monthNow)) {
			season = 3;
		} else if ("10".equals(monthNow) || "11".equals(monthNow) || "12".equals(monthNow)) {
			season = 4;
		}
		List<String> countryList = Lists.newArrayList("en","de","fr","it","es","jp","total");
		int  rowIndex=1;
		List<Integer> indexList = Lists.newArrayList();
		for (Map.Entry<String, Map<String, Map<String, SaleReportMonthType>>> entryData: data.entrySet()) { 
		    String month = entryData.getKey();
//			if (month.contains("q")) {
//				continue;
//			}
			if (!month.contains("q") && year.equals(currYearString) && Integer.parseInt(month)>=Integer.parseInt(currMonthString)) {
				continue;
			}
			if (month.contains("q") && year.equals(currYearString) && Integer.parseInt(month.substring(1, 2))>=season) {
				continue;
			}
			Map<String, Map<String, SaleReportMonthType>> typeMap = entryData.getValue();
			Map<String, SaleReportMonthType> countryTotalMap = typeMap.get("total");
			float enTotal = countryTotalMap.get("en").getSales();
			float deTotal = countryTotalMap.get("de").getSales();
			float frTotal = countryTotalMap.get("fr").getSales();
			float itTotal = countryTotalMap.get("it").getSales();
			float esTotal = countryTotalMap.get("es").getSales();
			float jpTotal = countryTotalMap.get("jp").getSales();
			float totalSale = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
			
			float enTotalProfit = countryTotalMap.get("en").getProfits();
			float deTotalProfit = countryTotalMap.get("de").getProfits();
			float frTotalProfit = countryTotalMap.get("fr").getProfits();
			float itTotalProfit = countryTotalMap.get("it").getProfits();
			float esTotalProfit = countryTotalMap.get("es").getProfits();
			float jpTotalProfit = countryTotalMap.get("jp").getProfits();
			float totalProfit = enTotalProfit + deTotalProfit + frTotalProfit + itTotalProfit + esTotalProfit + jpTotalProfit;
			
			float totalGoal = 0f;
			for (Map.Entry<String, Map<String, SaleReportMonthType>> entry: typeMap.entrySet()) { 
			    String line = entry.getKey();
				if ("total".equals(line)) {
					continue;
				}
				int m = 0;
				Map<String, SaleReportMonthType> countryMap = entry.getValue();
				row=sheet.createRow(rowIndex++);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month.toUpperCase());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(line);
				
				for (String country : countryList) {
					float lineGoal = 0f;
					try {
						lineGoal = lineGoalMap.get(month).get(line).get(country).getGoal();
					} catch (NullPointerException e) {}
					if (lineGoal == 0) {
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					} else {
						totalGoal += lineGoal;
						float lineSale = countryMap.get(country)==null?0:countryMap.get(country).getSales();
						float lineProfits = countryMap.get(country)==null?0:countryMap.get(country).getProfits();
						float profitGoal = lineGoal * getRate(year, country, line);
						try {
							profitGoal = lineGoalMap.get(month).get(line).get(country).getProfitGoal();
						} catch (NullPointerException e) {}
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue((lineSale/lineGoal+lineProfits/profitGoal)*0.5 );
					}
				}
			}
			//总计
			int m = 0;
			indexList.add(rowIndex);
			row=sheet.createRow(rowIndex++);
			row.createCell(m++).setCellValue(month.toUpperCase());
			row.createCell(m++).setCellValue("Total");
			float enLineGoal = 0f;
			try {
				enLineGoal = countryGoalMap.get(month).get("en").getGoal();
			} catch (NullPointerException e) {}
			if (enLineGoal == 0) {
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
			} else {
				float profitGoal = enLineGoal * getRate(year, "en", null);
				try {
					profitGoal = countryGoalMap.get(month).get("en").getProfitGoal();
				} catch (NullPointerException e) {}
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue((enTotal/enLineGoal+enTotalProfit/profitGoal)*0.5 );
			}
			
			float deLineGoal = 0f;
			try {
				deLineGoal = countryGoalMap.get(month).get("de").getGoal();
			} catch (NullPointerException e) {}
			if (deLineGoal == 0) {
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
			} else {
				float profitGoal = deLineGoal * getRate(year, "de", null);
				try {
					profitGoal = countryGoalMap.get(month).get("de").getProfitGoal();
				} catch (NullPointerException e) {}
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue((deTotal/deLineGoal+deTotalProfit/profitGoal)*0.5 );
			}
			
			float frLineGoal = 0f;
			try {
				frLineGoal = countryGoalMap.get(month).get("fr").getGoal();
			} catch (NullPointerException e) {}
			if (frLineGoal == 0) {
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
			} else {
				float profitGoal = frLineGoal * getRate(year, "fr", null);
				try {
					profitGoal = countryGoalMap.get(month).get("fr").getProfitGoal();
				} catch (NullPointerException e) {}
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue((frTotal/frLineGoal+frTotalProfit/profitGoal)*0.5 );
			}
			
			float itLineGoal = 0f;
			try {
				itLineGoal = countryGoalMap.get(month).get("it").getGoal();
			} catch (NullPointerException e) {}
			if (itLineGoal == 0) {
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
			} else {
				float profitGoal = itLineGoal * getRate(year, "it", null);
				try {
					profitGoal = countryGoalMap.get(month).get("it").getProfitGoal();
				} catch (NullPointerException e) {}
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue((itTotal/itLineGoal+itTotalProfit/profitGoal)*0.5 );
			}
			
			float esLineGoal = 0f;
			try {
				esLineGoal = countryGoalMap.get(month).get("es").getGoal();
			} catch (NullPointerException e) {}
			if (esLineGoal == 0) {
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
			} else {
				float profitGoal = esLineGoal * getRate(year, "es", null);
				try {
					profitGoal = countryGoalMap.get(month).get("es").getProfitGoal();
				} catch (NullPointerException e) {}
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue((esTotal/esLineGoal+esTotalProfit/profitGoal)*0.5 );
			}
			
			float jpLineGoal = 0f;
			try {
				jpLineGoal = countryGoalMap.get(month).get("jp").getGoal();
			} catch (NullPointerException e) {}
			if (jpLineGoal == 0) {
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
			} else {
				float profitGoal = jpLineGoal * getRate(year, "jp", null);
				try {
					profitGoal = countryGoalMap.get(month).get("jp").getProfitGoal();
				} catch (NullPointerException e) {}
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue((jpTotal/jpLineGoal+jpTotalProfit/profitGoal)*0.5 );
			}
			
			float totalLineGoal = 0f;
			try {
				totalLineGoal = countryGoalMap.get(month).get("total").getGoal();
			} catch (NullPointerException e) {}
			if (totalLineGoal == 0) {
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
			} else {
				float profitGoal = totalLineGoal * getRate(year, "total", null);
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue((totalSale/totalLineGoal+totalProfit/profitGoal)*0.5 );
			}

		}
		
		for (int i = 1; i < rowIndex; i++) {
			for (int j = 0; j < title.size(); j++) {
				try {
					if (indexList.contains(i)) {
						if (j < 2) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorStyle"));
						} else {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorDecStyle"));
						}
					} else {
						if (j < 2) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
						} else {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("decimalOneStyle"));
						}
					}
					
				} catch (Exception e) {}
			}
	    }
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short)i, true);
		}
		
	}


	private void fillTargetSheet(HSSFSheet sheet, Map<String, Map<String, Map<String, SaleReportMonthType>>> data, Map<String, Map<String, Map<String, SaleReportMonthType>>> lineData,
			Map<String, HSSFCellStyle> styleMap, String year, String currYearString, String currMonthString, Map<String, String> typeLine,
			Map<String, Map<String, EnterpriseGoal>> countryGoalMap, Map<String, Map<String, Map<String, EnterpriseGoal>>> lineGoalMap,
			List<String> lineNameList) {

		HSSFRow row = null;
		HSSFCell cell = null;
		int rowIndex=1;
		List<String>  title = Lists.newArrayList("日期","产品类型","产品线","EN","EN%","DE","DE%","FR","FR%","IT","IT%","ES","ES%","JP","JP%","Total","TT%");
		
		//季度计算
		String monthNow = currMonthString.substring(4);
		int season = 1;	//当前时间所属季度数
		if ("04".equals(monthNow) || "05".equals(monthNow) || "06".equals(monthNow)) {
			season = 2;
		} else if ("07".equals(monthNow) || "08".equals(monthNow) || "09".equals(monthNow)) {
			season = 3;
		} else if ("10".equals(monthNow) || "11".equals(monthNow) || "12".equals(monthNow)) {
			season = 4;
		}
		
		//年初始目标()
		Map<String, Integer> goal = EnterpriseGoalService.monthGoalMap(year);
		//目标sheet
		row = sheet.createRow(0);
		title = Lists.newArrayList("月份",year + "原定目标/万欧",year + "实际目标/万欧","月销售/万欧","盈亏/万欧","完成比例");
		Map<String, String> monthChangeMap = monthStringMap(year);
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		rowIndex = 1;
		float totalGoal1 = 0f;
		float totalGoal = 0f;
		float totalSales = 0f;
		for (Map.Entry<String, Map<String, EnterpriseGoal>> entryData: countryGoalMap.entrySet()) { 
		    String key = entryData.getKey();
		    Map<String, EnterpriseGoal> tempMap=entryData.getValue();
			if (key.contains("q")) {
				continue;
			}
			if (year.equals(currYearString) && Integer.parseInt(key)>=Integer.parseInt(currMonthString)) {
				continue;
			}
			int m = 0;
			row = sheet.createRow(rowIndex++);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("contentStyle"));
			cell.setCellValue(monthChangeMap.get(key));
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			float monthGoal1 = goal.get(key) != null?goal.get(key):0f;
			if (monthGoal1 > 0) {
				cell.setCellValue(goal.get(key)/10000f);
			} else {
				cell.setCellValue("");
			}
			float monthGoal = tempMap.get("total").getGoal()==null?0:tempMap.get("total").getGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(monthGoal/10000);
			if (year.equals(currYearString) && Integer.parseInt(key)==Integer.parseInt(currMonthString)) {
				for (int i = 0; i < 3; i++) {
					cell = row.createCell(m++);
					cell.setCellStyle(styleMap.get("contentStyle"));
					cell.setCellValue("");
				}
			} else {
				if (data.get(key) == null || data.get(key).get("total")==null) {
					continue;
				}
				Map<String, SaleReportMonthType> countryTotalMap = data.get(key).get("total");
				float enTotal = countryTotalMap.get("en").getSales();
				float deTotal = countryTotalMap.get("de").getSales();
				float frTotal = countryTotalMap.get("fr").getSales();
				float itTotal = countryTotalMap.get("it").getSales();
				float esTotal = countryTotalMap.get("es").getSales();
				float jpTotal = countryTotalMap.get("jp").getSales();
				float monthSales = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
				totalSales += monthSales;
				totalGoal += monthGoal;
				totalGoal1 += monthGoal1;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(monthSales/10000);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue((monthSales-monthGoal)/10000);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				cell.setCellValue(monthSales/monthGoal);
			}
		}
		//总计
		int m = 0;
		row = sheet.createRow(rowIndex++);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("contentStyle"));
		cell.setCellValue("总计");
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		if (totalGoal1 > 0) {
			cell.setCellValue(totalGoal1/10000);
		} else {
			cell.setCellValue("");
		}
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(totalGoal/10000);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(totalSales/10000);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue((totalSales-totalGoal)/10000);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(totalSales/totalGoal);
		
		rowIndex++;	//表格之间空一行
		title = Lists.newArrayList("月份",year+"目标","EN目标","EN完成","完成比","DE目标","DE完成","完成比",
				"FR目标","FR完成","完成比","IT目标","IT完成","完成比","ES目标","ES完成","完成比","JP目标","JP完成","完成比");
		row = sheet.createRow(rowIndex++);
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
		}
		
		totalGoal = 0f;
		totalSales = 0f;
		float enTotalGoal = 0f;
		float deTotalGoal = 0f;
		float frTotalGoal = 0f;
		float itTotalGoal = 0f;
		float esTotalGoal = 0f;
		float jpTotalGoal = 0f;
		float enTotalSales = 0f;
		float deTotalSales = 0f;
		float frTotalSales = 0f;
		float itTotalSales = 0f;
		float esTotalSales = 0f;
		float jpTotalSales = 0f;
		for (Map.Entry<String, Map<String, EnterpriseGoal>> entry: countryGoalMap.entrySet()) { 
		    String key = entry.getKey();
		    Map<String, EnterpriseGoal> valueMap=entry.getValue();
			if (key.contains("q")) {
				continue;
			}
			if (year.equals(currYearString) && Integer.parseInt(key)>=Integer.parseInt(currMonthString)) {
				continue;
			}
			m = 0;
			Map<String, SaleReportMonthType> countryTotalMap = data.get(key).get("total");
			row = sheet.createRow(rowIndex++);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("contentStyle"));
			cell.setCellValue(monthChangeMap.get(key));
			//总目标
			float monthGoal = valueMap.get("total").getGoal()==null?0:valueMap.get("total").getGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(monthGoal);
			//英语国家
			float enGoal = valueMap.get("en").getGoal()==null?0:valueMap.get("en").getGoal();
			enTotalGoal += enGoal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(enGoal);
			float enTotal = countryTotalMap.get("en").getSales();
			enTotalSales += enTotal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(enTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(enTotal/enGoal);
			//德国
			float deGoal = valueMap.get("de").getGoal()==null?0:valueMap.get("de").getGoal();
			deTotalGoal += deGoal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(deGoal);
			float deTotal = countryTotalMap.get("de").getSales();
			deTotalSales += deTotal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(deTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(deTotal/deGoal);
			//法国
			float frGoal = valueMap.get("fr").getGoal()==null?0:valueMap.get("fr").getGoal();
			frTotalGoal += frGoal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(frGoal);
			float frTotal = countryTotalMap.get("fr").getSales();
			frTotalSales += frTotal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(frTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(frTotal/frGoal);
			//意大利
			float itGoal = valueMap.get("it").getGoal()==null?0:valueMap.get("it").getGoal();
			itTotalGoal += itGoal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(itGoal);
			float itTotal = countryTotalMap.get("it").getSales();
			itTotalSales += itTotal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(itTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(itTotal/itGoal);
			//西班牙
			float esGoal = valueMap.get("es").getGoal()==null?0:valueMap.get("es").getGoal();
			esTotalGoal += esGoal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(esGoal);
			float esTotal = countryTotalMap.get("es").getSales();
			esTotalSales += esTotal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(esTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(esTotal/esGoal);
			//日本
			float jpGoal = valueMap.get("jp").getGoal()==null?0:valueMap.get("jp").getGoal();
			jpTotalGoal += jpGoal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(jpGoal);
			float jpTotal = countryTotalMap.get("jp").getSales();
			jpTotalSales += jpTotal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(jpTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(jpTotal/jpGoal);
			
			float monthSales = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
			totalSales += monthSales;
			totalGoal += monthGoal;
		}
		//总计
		m = 0;
		row = sheet.createRow(rowIndex++);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("contentStyle"));
		cell.setCellValue("总计");
		//总目标
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(totalGoal);
		//英语国家
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(enTotalGoal);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(enTotalSales);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(enTotalSales/enTotalGoal);
		//德国
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(deTotalGoal);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(deTotalSales);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(deTotalSales/deTotalGoal);
		//法国
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(frTotalGoal);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(frTotalSales);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(frTotalSales/frTotalGoal);
		//意大利
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(itTotalGoal);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(itTotalSales);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(itTotalSales/itTotalGoal);
		//西班牙
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(esTotalGoal);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(esTotalSales);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(esTotalSales/esTotalGoal);
		//日本
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(jpTotalGoal);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(jpTotalSales);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(jpTotalSales/jpTotalGoal);
		//季度统计
		for (Map.Entry<String, Map<String, EnterpriseGoal>> entry: countryGoalMap.entrySet()) { 
		    String key =entry.getKey();
		    Map<String, EnterpriseGoal> tempMap=entry.getValue();
			if (!key.contains("q")) {
				continue;
			}
			if (year.equals(currYearString) && Integer.parseInt(key.substring(1, 2))>=season) {
				continue;
			}
			m = 0;
			Map<String, SaleReportMonthType> countryTotalMap = data.get(key).get("total");
			row = sheet.createRow(rowIndex++);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(key.toUpperCase());
			//总目标
			float monthGoal = tempMap.get("total").getGoal()==null?0:tempMap.get("total").getGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(monthGoal);
			//英语国家
			float enGoal =tempMap.get("en").getGoal()==null?0:tempMap.get("en").getGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(enGoal);
			float enTotal = countryTotalMap.get("en").getSales();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(enTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(enTotal/enGoal);
			//德国
			float deGoal = tempMap.get("de").getGoal()==null?0:tempMap.get("de").getGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(deGoal);
			float deTotal = countryTotalMap.get("de").getSales();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(deTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(deTotal/deGoal);
			//法国
			float frGoal =tempMap.get("fr").getGoal()==null?0:tempMap.get("fr").getGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(frGoal);
			float frTotal = countryTotalMap.get("fr").getSales();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(frTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(frTotal/frGoal);
			//意大利
			float itGoal = tempMap.get("it").getGoal()==null?0:tempMap.get("it").getGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(itGoal);
			float itTotal = countryTotalMap.get("it").getSales();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(itTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(itTotal/itGoal);
			//西班牙
			float esGoal = tempMap.get("es").getGoal()==null?0:tempMap.get("es").getGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(esGoal);
			float esTotal = countryTotalMap.get("es").getSales();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(esTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(esTotal/esGoal);
			//日本
			float jpGoal = tempMap.get("jp").getGoal()==null?0:tempMap.get("jp").getGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(jpGoal);
			float jpTotal = countryTotalMap.get("jp").getSales();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(jpTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(jpTotal/jpGoal);
		}
		// 分产品线目标完成比
		rowIndex++;	//表格之间空一行
		title = Lists.newArrayList("月份","产品线","EN目标","EN完成","完成比","DE目标","DE完成","完成比",
				"FR目标","FR完成","完成比","IT目标","IT完成","完成比","ES目标","ES完成","完成比","JP目标","JP完成","完成比","TT目标","TT完成","完成比");
		row = sheet.createRow(rowIndex++);
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
		}
		
		totalGoal = 0f;
		totalSales = 0f;
		for (String key : countryGoalMap.keySet()) {	//以国家目标月份迭代，产品线目标大多未填报
			if (key.contains("q")) {
				continue;
			}
			if (year.equals(currYearString) && Integer.parseInt(key)>=Integer.parseInt(currMonthString)) {
				continue;
			}
			for (String lineName : lineNameList) {
				m = 0;
				Map<String, SaleReportMonthType> countryTotalMap = lineData.get(key).get(lineName);
				row = sheet.createRow(rowIndex++);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("contentStyle"));
				cell.setCellValue(monthChangeMap.get(key));
				//产品线
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("contentStyle"));
				cell.setCellValue(lineName);
				
				//英语国家
				float enGoal = 0;
				try {
					enGoal = lineGoalMap.get(key).get(lineName).get("en").getGoal()==null?0:lineGoalMap.get(key).get(lineName).get("en").getGoal();
				} catch (NullPointerException e) {}
				enTotalGoal += enGoal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (enGoal > 1) {
					cell.setCellValue(enGoal);
				} else {
					cell.setCellValue("");
				}
				float enTotal = 0;
				try {
					enTotal = countryTotalMap.get("en").getSales();
				} catch (NullPointerException e) {}
				enTotalSales += enTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(enTotal);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (enGoal > 1) {
					cell.setCellValue(enTotal/enGoal);
				} else {
					cell.setCellValue("");
				}
				
				//德国
				float deGoal = 0;
				try {
					deGoal = lineGoalMap.get(key).get(lineName).get("de").getGoal()==null?0:lineGoalMap.get(key).get(lineName).get("de").getGoal();
				} catch (NullPointerException e) {}
				deTotalGoal += deGoal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (deGoal > 1) {
					cell.setCellValue(deGoal);
				} else {
					cell.setCellValue("");
				}
				float deTotal = 0;
				try {
					deTotal = countryTotalMap.get("de").getSales();
				} catch (NullPointerException e) {}
				deTotalSales += deTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(deTotal);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (deGoal > 1) {
					cell.setCellValue(deTotal/deGoal);
				} else {
					cell.setCellValue("");
				}
				
				//法国
				float frGoal = 0;
				try {
					frGoal = lineGoalMap.get(key).get(lineName).get("fr").getGoal()==null?0:lineGoalMap.get(key).get(lineName).get("fr").getGoal();
				} catch (NullPointerException e) {}
				frTotalGoal += frGoal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (frGoal > 1) {
					cell.setCellValue(frGoal);
				} else {
					cell.setCellValue("");
				}
				float frTotal = 0;
				try {
					frTotal = countryTotalMap.get("fr").getSales();
				} catch (NullPointerException e) {}
				frTotalSales += frTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(frTotal);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (frGoal > 1) {
					cell.setCellValue(frTotal/frGoal);
				} else {
					cell.setCellValue("");
				}
				
				//意大利
				float itGoal = 0;
				try {
					itGoal = lineGoalMap.get(key).get(lineName).get("it").getGoal()==null?0:lineGoalMap.get(key).get(lineName).get("it").getGoal();
				} catch (NullPointerException e) {}
				itTotalGoal += itGoal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (itGoal > 1) {
					cell.setCellValue(itGoal);
				} else {
					cell.setCellValue("");
				}
				float itTotal = 0;
				try {
					itTotal = countryTotalMap.get("it").getSales();
				} catch (NullPointerException e) {}
				itTotalSales += itTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(itTotal);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (itGoal > 1) {
					cell.setCellValue(itTotal/itGoal);
				} else {
					cell.setCellValue("");
				}
				
				//西班牙
				float esGoal = 0;
				try {
					esGoal = lineGoalMap.get(key).get(lineName).get("es").getGoal()==null?0:lineGoalMap.get(key).get(lineName).get("es").getGoal();
				} catch (NullPointerException e) {}
				esTotalGoal += esGoal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (esGoal > 1) {
					cell.setCellValue(esGoal);
				} else {
					cell.setCellValue("");
				}
				float esTotal = 0;
				try {
					esTotal = countryTotalMap.get("es").getSales();
				} catch (NullPointerException e) {}
				esTotalSales += esTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(esTotal);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (esGoal > 1) {
					cell.setCellValue(esTotal/esGoal);
				} else {
					cell.setCellValue("");
				}
				
				//日本
				float jpGoal = 0;
				try {
					jpGoal = lineGoalMap.get(key).get(lineName).get("jp").getGoal()==null?0:lineGoalMap.get(key).get(lineName).get("jp").getGoal();
				} catch (NullPointerException e) {}
				jpTotalGoal += jpGoal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (jpGoal > 1) {
					cell.setCellValue(jpGoal);
				} else {
					cell.setCellValue("");
				}
				float jpTotal = 0;
				try {
					jpTotal = countryTotalMap.get("jp").getSales();
				} catch (NullPointerException e) {}
				jpTotalSales += jpTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(jpTotal);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (jpGoal > 1) {
					cell.setCellValue(jpTotal/jpGoal);
				} else {
					cell.setCellValue("");
				}
				
				//总目标
				float monthGoal = 0;
				try {
					monthGoal = lineGoalMap.get(key).get(lineName).get("total").getGoal()==null?0:lineGoalMap.get(key).get(lineName).get("total").getGoal();
				} catch (NullPointerException e) {}
				
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (monthGoal>1) {
					cell.setCellValue(monthGoal);
				} else {
					cell.setCellValue("");
				}
				
				float monthSales = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (monthSales > 1) {
					cell.setCellValue(monthSales);
				} else {
					cell.setCellValue("");
				}
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (monthGoal > 1) {
					cell.setCellValue(monthSales/monthGoal);
				} else {
					cell.setCellValue("");
				}
			}
		}
		
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short)i, true);
		}
	}

	private void fillProfitTargetSheet(HSSFSheet sheet, Map<String, Map<String, Map<String, SaleReportMonthType>>> data, Map<String, Map<String, Map<String, SaleReportMonthType>>> lineData,
			Map<String, HSSFCellStyle> styleMap, String year, String currYearString, String currMonthString, Map<String, String> typeLine,
			Map<String, Map<String, EnterpriseGoal>> countryGoalMap, Map<String, Map<String, Map<String, EnterpriseGoal>>> lineGoalMap,
			List<String> lineNameList) {

		HSSFRow row = null;
		HSSFCell cell = null;
		int rowIndex=1;
		List<String>  title = Lists.newArrayList("日期","产品类型","产品线","EN","EN%","DE","DE%","FR","FR%","IT","IT%","ES","ES%","JP","JP%","Total","TT%");
		
		//季度计算
		String monthNow = currMonthString.substring(4);
		int season = 1;	//当前时间所属季度数
		if ("04".equals(monthNow) || "05".equals(monthNow) || "06".equals(monthNow)) {
			season = 2;
		} else if ("07".equals(monthNow) || "08".equals(monthNow) || "09".equals(monthNow)) {
			season = 3;
		} else if ("10".equals(monthNow) || "11".equals(monthNow) || "12".equals(monthNow)) {
			season = 4;
		}
		
		//年初始目标()
		Map<String, Integer> goal = EnterpriseGoalService.monthGoalMap(year);
		//目标sheet
		row = sheet.createRow(0);
		title = Lists.newArrayList("月份",year + "原定目标/万欧",year + "实际目标/万欧","月销售/万欧","盈亏/万欧","完成比例");
		Map<String, String> monthChangeMap = monthStringMap(year);
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		rowIndex = 1;
		float totalGoal1 = 0f;
		float totalGoal = 0f;
		float totalSales = 0f;
		for (Map.Entry<String,Map<String,EnterpriseGoal>> entry : countryGoalMap.entrySet()) { 
		    String key = entry.getKey();
		    Map<String,EnterpriseGoal> entryGoal =entry.getValue();
			if (key.contains("q")) {
				continue;
			}
			if (year.equals(currYearString) && Integer.parseInt(key)>=Integer.parseInt(currMonthString)) {
				continue;
			}
			int m = 0;
			row = sheet.createRow(rowIndex++);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("contentStyle"));
			cell.setCellValue(monthChangeMap.get(key));
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			float monthGoal1 = goal.get(key) != null?goal.get(key) * getRate(currYearString, "total", null):0f;
			if (monthGoal1 > 0) {
				cell.setCellValue(monthGoal1/10000);
			} else {
				cell.setCellValue("");
			}
			float monthGoal = entryGoal.get("total").getProfitGoal()==null?0:entryGoal.get("total").getProfitGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(monthGoal/10000);
			if (year.equals(currYearString) && Integer.parseInt(key)==Integer.parseInt(currMonthString)) {
				for (int i = 0; i < 3; i++) {
					cell = row.createCell(m++);
					cell.setCellStyle(styleMap.get("contentStyle"));
					cell.setCellValue("");
				}
			} else {
				if (data.get(key) == null || data.get(key).get("total")==null) {
					continue;
				}
				Map<String, SaleReportMonthType> countryTotalMap = data.get(key).get("total");
				float enTotal = countryTotalMap.get("en").getProfits();
				float deTotal = countryTotalMap.get("de").getProfits();
				float frTotal = countryTotalMap.get("fr").getProfits();
				float itTotal = countryTotalMap.get("it").getProfits();
				float esTotal = countryTotalMap.get("es").getProfits();
				float jpTotal = countryTotalMap.get("jp").getProfits();
				float monthSales = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
				totalSales += monthSales;
				totalGoal += monthGoal;
				totalGoal1 += monthGoal1;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(monthSales/10000);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue((monthSales-monthGoal)/10000);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				cell.setCellValue(monthSales/monthGoal);
			}
		}
		//总计
		int m = 0;
		row = sheet.createRow(rowIndex++);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("contentStyle"));
		cell.setCellValue("总计");
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		if (totalGoal1 > 0) {
			cell.setCellValue(totalGoal1/10000);
		} else {
			cell.setCellValue("");
		}
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(totalGoal/10000);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(totalSales/10000);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue((totalSales-totalGoal1)/10000);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(totalSales/totalGoal1);
		
		rowIndex++;	//表格之间空一行
		title = Lists.newArrayList("月份",year+"目标","EN目标","EN完成","完成比","DE目标","DE完成","完成比",
				"FR目标","FR完成","完成比","IT目标","IT完成","完成比","ES目标","ES完成","完成比","JP目标","JP完成","完成比");
		row = sheet.createRow(rowIndex++);
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
		}
		
		totalGoal = 0f;
		totalSales = 0f;
		float enTotalGoal = 0f;
		float deTotalGoal = 0f;
		float frTotalGoal = 0f;
		float itTotalGoal = 0f;
		float esTotalGoal = 0f;
		float jpTotalGoal = 0f;
		float enTotalSales = 0f;
		float deTotalSales = 0f;
		float frTotalSales = 0f;
		float itTotalSales = 0f;
		float esTotalSales = 0f;
		float jpTotalSales = 0f;
		for (Map.Entry<String, Map<String, EnterpriseGoal>> entry: countryGoalMap.entrySet()) { 
		    String key = entry.getKey();
		    Map<String, EnterpriseGoal> entryGoal = entry.getValue();
			if (key.contains("q")) {
				continue;
			}
			if (year.equals(currYearString) && Integer.parseInt(key)>=Integer.parseInt(currMonthString)) {
				continue;
			}
			m = 0;
			Map<String, SaleReportMonthType> countryTotalMap = data.get(key).get("total");
			row = sheet.createRow(rowIndex++);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("contentStyle"));
			cell.setCellValue(monthChangeMap.get(key));
			//总目标
			float monthGoal = entryGoal.get("total").getProfitGoal()==null?0:entryGoal.get("total").getProfitGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(monthGoal);
			//英语国家
			float enGoal = entryGoal.get("en").getProfitGoal()==null?0:entryGoal.get("en").getProfitGoal();
			enTotalGoal += enGoal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(enGoal);
			float enTotal = countryTotalMap.get("en").getProfits();
			enTotalSales += enTotal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(enTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(enTotal/enGoal);
			//德国
			float deGoal = entryGoal.get("de").getProfitGoal()==null?0:entryGoal.get("de").getProfitGoal();
			deTotalGoal += deGoal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(deGoal);
			float deTotal = countryTotalMap.get("de").getProfits();
			deTotalSales += deTotal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(deTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(deTotal/deGoal);
			//法国
			float frGoal = entryGoal.get("fr").getProfitGoal()==null?0:entryGoal.get("fr").getProfitGoal();
			frTotalGoal += frGoal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(frGoal);
			float frTotal = countryTotalMap.get("fr").getProfits();
			frTotalSales += frTotal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(frTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(frTotal/frGoal);
			//意大利
			float itGoal = entryGoal.get("it").getProfitGoal()==null?0:entryGoal.get("it").getProfitGoal();
			itTotalGoal += itGoal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(itGoal);
			float itTotal = countryTotalMap.get("it").getProfits();
			itTotalSales += itTotal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(itTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(itTotal/itGoal);
			//西班牙
			float esGoal = entryGoal.get("es").getProfitGoal()==null?0:entryGoal.get("es").getProfitGoal();
			esTotalGoal += esGoal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(esGoal);
			float esTotal = countryTotalMap.get("es").getProfits();
			esTotalSales += esTotal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(esTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(esTotal/esGoal);
			//日本
			float jpGoal = entryGoal.get("jp").getProfitGoal()==null?0:entryGoal.get("jp").getProfitGoal();
			jpTotalGoal += jpGoal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(jpGoal);
			float jpTotal = countryTotalMap.get("jp").getProfits();
			jpTotalSales += jpTotal;
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(jpTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(jpTotal/jpGoal);
			
			float monthSales = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
			totalSales += monthSales;
			totalGoal += monthGoal;
		}
		//总计
		m = 0;
		row = sheet.createRow(rowIndex++);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("contentStyle"));
		cell.setCellValue("总计");
		//总目标
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(totalGoal);
		//英语国家
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(enTotalGoal);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(enTotalSales);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(enTotalSales/enTotalGoal);
		//德国
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(deTotalGoal);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(deTotalSales);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(deTotalSales/deTotalGoal);
		//法国
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(frTotalGoal);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(frTotalSales);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(frTotalSales/frTotalGoal);
		//意大利
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(itTotalGoal);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(itTotalSales);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(itTotalSales/itTotalGoal);
		//西班牙
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(esTotalGoal);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(esTotalSales);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(esTotalSales/esTotalGoal);
		//日本
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(jpTotalGoal);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("intStyle"));
		cell.setCellValue(jpTotalSales);
		cell = row.createCell(m++);
		cell.setCellStyle(styleMap.get("percentageInt"));
		cell.setCellValue(jpTotalSales/jpTotalGoal);
		//季度统计
		for (Map.Entry<String, Map<String, EnterpriseGoal>> entry: countryGoalMap.entrySet()) { 
	        String key = entry.getKey();
	        Map<String, EnterpriseGoal> tempMap=entry.getValue();
			if (!key.contains("q")) {
				continue;
			}
			if (year.equals(currYearString) && Integer.parseInt(key.substring(1, 2))>=season) {
				continue;
			}
			m = 0;
			Map<String, SaleReportMonthType> countryTotalMap = data.get(key).get("total");
			row = sheet.createRow(rowIndex++);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(key.toUpperCase());
			//总目标
			float monthGoal = tempMap.get("total").getProfitGoal()==null?0:tempMap.get("total").getProfitGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(monthGoal);
			//英语国家
			float enGoal = tempMap.get("en").getProfitGoal()==null?0:tempMap.get("en").getProfitGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(enGoal);
			float enTotal = countryTotalMap.get("en").getProfits();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(enTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(enTotal/enGoal);
			//德国
			float deGoal = tempMap.get("de").getProfitGoal()==null?0:tempMap.get("de").getProfitGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(deGoal);
			float deTotal = countryTotalMap.get("de").getProfits();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(deTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(deTotal/deGoal);
			//法国
			float frGoal = tempMap.get("fr").getProfitGoal()==null?0:tempMap.get("fr").getProfitGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(frGoal);
			float frTotal = countryTotalMap.get("fr").getProfits();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(frTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(frTotal/frGoal);
			//意大利
			float itGoal = tempMap.get("it").getProfitGoal()==null?0:tempMap.get("it").getProfitGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(itGoal);
			float itTotal = countryTotalMap.get("it").getProfits();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(itTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(itTotal/itGoal);
			//西班牙
			float esGoal = tempMap.get("es").getProfitGoal()==null?0:tempMap.get("es").getProfitGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(esGoal);
			float esTotal = countryTotalMap.get("es").getProfits();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(esTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(esTotal/esGoal);
			//日本
			float jpGoal = tempMap.get("jp").getProfitGoal()==null?0:tempMap.get("jp").getProfitGoal();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(jpGoal);
			float jpTotal = countryTotalMap.get("jp").getProfits();
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("intStyle"));
			cell.setCellValue(jpTotal);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("percentageInt"));
			cell.setCellValue(jpTotal/jpGoal);
		}
		// 分产品线目标完成比
		rowIndex++;	//表格之间空一行
		title = Lists.newArrayList("月份","产品线","EN目标/万欧","EN完成","完成比","DE目标/万欧","DE完成","完成比",
				"FR目标/万欧","FR完成","完成比","IT目标/万欧","IT完成","完成比","ES目标/万欧","ES完成","完成比","JP目标/万欧","JP完成","完成比","TT目标","TT完成","完成比");
		row = sheet.createRow(rowIndex++);
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
		}
		
		totalGoal = 0f;
		totalSales = 0f;
		for (String key : countryGoalMap.keySet()) {	//以国家目标月份迭代，产品线目标大多未填报
			if (key.contains("q")) {
				continue;
			}
			if (year.equals(currYearString) && Integer.parseInt(key)>=Integer.parseInt(currMonthString)) {
				continue;
			}
			for (String lineName : lineNameList) {
				m = 0;
				Map<String, SaleReportMonthType> countryTotalMap = lineData.get(key).get(lineName);
				row = sheet.createRow(rowIndex++);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("contentStyle"));
				cell.setCellValue(monthChangeMap.get(key));
				//产品线
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("contentStyle"));
				cell.setCellValue(lineName);
				
				//英语国家
				float enGoal = 0;
				try {
					enGoal = lineGoalMap.get(key).get(lineName).get("en").getProfitGoal()==null?0:lineGoalMap.get(key).get(lineName).get("en").getProfitGoal();
				} catch (NullPointerException e) {}
				enTotalGoal += enGoal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (enGoal > 1) {
					cell.setCellValue(enGoal);
				} else {
					cell.setCellValue("");
				}
				float enTotal = 0;
				try {
					enTotal = countryTotalMap.get("en").getProfits();
				} catch (NullPointerException e) {}
				enTotalSales += enTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(enTotal);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (enGoal > 1) {
					cell.setCellValue(enTotal/enGoal);
				} else {
					cell.setCellValue("");
				}
				
				//德国
				float deGoal = 0;
				try {
					deGoal = lineGoalMap.get(key).get(lineName).get("de").getProfitGoal()==null?0:lineGoalMap.get(key).get(lineName).get("de").getProfitGoal();
				} catch (NullPointerException e) {}
				deTotalGoal += deGoal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (deGoal > 1) {
					cell.setCellValue(deGoal);
				} else {
					cell.setCellValue("");
				}
				float deTotal = 0;
				try {
					deTotal = countryTotalMap.get("de").getProfits();
				} catch (NullPointerException e) {}
				deTotalSales += deTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(deTotal);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (deGoal > 1) {
					cell.setCellValue(deTotal/deGoal);
				} else {
					cell.setCellValue("");
				}
				
				//法国
				float frGoal = 0;
				try {
					frGoal = lineGoalMap.get(key).get(lineName).get("fr").getProfitGoal()==null?0:lineGoalMap.get(key).get(lineName).get("fr").getProfitGoal();
				} catch (NullPointerException e) {}
				frTotalGoal += frGoal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (frGoal > 1) {
					cell.setCellValue(frGoal);
				} else {
					cell.setCellValue("");
				}
				float frTotal = 0;
				try {
					frTotal = countryTotalMap.get("fr").getProfits();
				} catch (NullPointerException e) {}
				frTotalSales += frTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(frTotal);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (frGoal > 1) {
					cell.setCellValue(frTotal/frGoal);
				} else {
					cell.setCellValue("");
				}
				
				//意大利
				float itGoal = 0;
				try {
					itGoal = lineGoalMap.get(key).get(lineName).get("it").getProfitGoal()==null?0:lineGoalMap.get(key).get(lineName).get("it").getProfitGoal();
				} catch (NullPointerException e) {}
				itTotalGoal += itGoal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (itGoal > 1) {
					cell.setCellValue(itGoal);
				} else {
					cell.setCellValue("");
				}
				float itTotal = 0;
				try {
					itTotal = countryTotalMap.get("it").getProfits();
				} catch (NullPointerException e) {}
				itTotalSales += itTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(itTotal);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (itGoal > 1) {
					cell.setCellValue(itTotal/itGoal);
				} else {
					cell.setCellValue("");
				}
				
				//西班牙
				float esGoal = 0;
				try {
					esGoal = lineGoalMap.get(key).get(lineName).get("es").getProfitGoal()==null?0:lineGoalMap.get(key).get(lineName).get("es").getProfitGoal();
				} catch (NullPointerException e) {}
				esTotalGoal += esGoal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (esGoal > 1) {
					cell.setCellValue(esGoal);
				} else {
					cell.setCellValue("");
				}
				float esTotal = 0;
				try {
					esTotal = countryTotalMap.get("es").getProfits();
				} catch (NullPointerException e) {}
				esTotalSales += esTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(esTotal);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (esGoal > 1) {
					cell.setCellValue(esTotal/esGoal);
				} else {
					cell.setCellValue("");
				}
				
				//日本
				float jpGoal = 0;
				try {
					jpGoal = lineGoalMap.get(key).get(lineName).get("jp").getProfitGoal()==null?0:lineGoalMap.get(key).get(lineName).get("jp").getProfitGoal();
				} catch (NullPointerException e) {}
				jpTotalGoal += jpGoal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (jpGoal > 1) {
					cell.setCellValue(jpGoal);
				} else {
					cell.setCellValue("");
				}
				float jpTotal = 0;
				try {
					jpTotal = countryTotalMap.get("jp").getProfits();
				} catch (NullPointerException e) {}
				jpTotalSales += jpTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				cell.setCellValue(jpTotal);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (jpGoal > 1) {
					cell.setCellValue(jpTotal/jpGoal);
				} else {
					cell.setCellValue("");
				}
				
				//总目标
				float monthGoal = 0;
				try {
					monthGoal = lineGoalMap.get(key).get(lineName).get("total").getProfitGoal()==null?0:lineGoalMap.get(key).get(lineName).get("total").getProfitGoal();
				} catch (NullPointerException e) {}
				
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (monthGoal>1) {
					cell.setCellValue(monthGoal);
				} else {
					cell.setCellValue("");
				}
				
				float monthSales = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("intStyle"));
				if (monthSales > 1) {
					cell.setCellValue(monthSales);
				} else {
					cell.setCellValue("");
				}
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("percentageInt"));
				if (monthGoal > 1) {
					cell.setCellValue(monthSales/monthGoal);
				} else {
					cell.setCellValue("");
				}
			}
		}
		
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short)i, true);
		}
	}


	private void fillTotalSheet(HSSFSheet sheet, Map<String,Map<String, Map<String, SaleReportMonthType>>> data, Map<String,Map<String, Map<String, SaleReportMonthType>>> data1, 
			Map<String, HSSFCellStyle> styleMap, String year, String currYearString, String currMonthString, Map<String, String> typeLine) {
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 400);
		HSSFCell cell = null;
		List<String>  title = Lists.newArrayList("月份","产品类型","产品线","销售额","销量","均价","销售额占比");
		//if (UserUtils.hasPermission("amazoninfo:profits:view")) {
			title.add("利润");
			title.add("利润率");
			title.add("单利");
			title.add("利润占比");
		//}
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		List<Integer> indexList = Lists.newArrayList();
		int  rowIndex=1;
		//上年度总计
		for (Map.Entry<String, Map<String, Map<String, SaleReportMonthType>>> entryData: data1.entrySet()) { 
		    String years = entryData.getKey();
			Map<String, Map<String, SaleReportMonthType>> typeMap = entryData.getValue();
			Map<String, SaleReportMonthType> countryTotalMap = typeMap.get("total");
			float enTotal = countryTotalMap.get("en").getSales();
			float deTotal = countryTotalMap.get("de").getSales();
			float frTotal = countryTotalMap.get("fr").getSales();
			float itTotal = countryTotalMap.get("it").getSales();
			float esTotal = countryTotalMap.get("es").getSales();
			float jpTotal = countryTotalMap.get("jp").getSales();
			//计算出时间点内总销售额
			float allTotal = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;	
			
			float enProfits = countryTotalMap.get("en").getProfits();
			float deProfits = countryTotalMap.get("de").getProfits();
			float frProfits = countryTotalMap.get("fr").getProfits();
			float itProfits = countryTotalMap.get("it").getProfits();
			float esProfits = countryTotalMap.get("es").getProfits();
			float jpProfits = countryTotalMap.get("jp").getProfits();
			//计算出时间点内总销售额
			float allProfits = enProfits + deProfits + frProfits + itProfits + esProfits + jpProfits;
			Integer allSalesVolume = 0;
			for (Map.Entry<String, Map<String, SaleReportMonthType>> entryType: typeMap.entrySet()) { 
			    String type = entryType.getKey();
				if ("total".equals(type)) {
					continue;
				}
				int m = 0;
				Map<String, SaleReportMonthType> countryMap = entryType.getValue();
				row=sheet.createRow(rowIndex++);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(years);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
				//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeLine.get(type.toLowerCase())==null?"UnGrouped":typeLine.get(type.toLowerCase()));
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(countryMap.get("total").getLine());
				
				//销售额
				float sales = countryMap.get("total")==null?0:countryMap.get("total").getSales();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales);
				//销量
				Integer salesVolume = countryMap.get("total")==null?0:countryMap.get("total").getSalesVolume();
				allSalesVolume += salesVolume;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);
				//均价
				if (salesVolume > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales/salesVolume);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//销售额占比
				if (salesVolume > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales/allTotal);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//if (UserUtils.hasPermission("amazoninfo:profits:view")) {
					//利润
					float profits = countryMap.get("total")==null?0:countryMap.get("total").getProfits();
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits);
					//利润率
					if (sales > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/sales);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					//单利
					if (sales > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/salesVolume);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					//利润占比
					if (allProfits > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/allProfits);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
				//}
			}
			//总计
			int m = 0;
			row=sheet.createRow(rowIndex++);
			indexList.add(rowIndex - 1);
			//时间
			row.createCell(m++).setCellValue(years);
			//类型
			row.createCell(m++).setCellValue("Total");
			//产品线
			row.createCell(m++).setCellValue("");
			//销售额
			row.createCell(m++).setCellValue(allTotal);
			//销量
			row.createCell(m++).setCellValue(allSalesVolume);
			//均价
			cell = row.createCell(m++);
			if (allSalesVolume > 0) {
				cell.setCellValue(allTotal/allSalesVolume);
			} else {
				cell.setCellValue("");
			}
			//销售额占比
			row.createCell(m++).setCellValue(1);
			//if (UserUtils.hasPermission("amazoninfo:profits:view")) {
				//利润
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits);
				//利润率
				if (allTotal > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits/allTotal);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//单利
				if (allTotal > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits/allSalesVolume);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//利润占比
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(1);
			//}
		}
		for (Map.Entry<String,Map<String,Map<String,SaleReportMonthType>>> entryData: data.entrySet()) { 
		    String month = entryData.getKey();
			if (month.contains("q")) {
				continue;	//季度数据显示到第二个sheet
			}
			if (year.equals(currYearString) && Integer.parseInt(month)>=Integer.parseInt(currMonthString)) {
				continue;
			}
			Map<String, Map<String, SaleReportMonthType>> typeMap = entryData.getValue();
			Map<String, SaleReportMonthType> countryTotalMap = typeMap.get("total");
			float enTotal = countryTotalMap.get("en").getSales();
			float deTotal = countryTotalMap.get("de").getSales();
			float frTotal = countryTotalMap.get("fr").getSales();
			float itTotal = countryTotalMap.get("it").getSales();
			float esTotal = countryTotalMap.get("es").getSales();
			float jpTotal = countryTotalMap.get("jp").getSales();
			//计算出时间点内总销售额
			float allTotal = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;	
			
			float enProfits = countryTotalMap.get("en").getProfits();
			float deProfits = countryTotalMap.get("de").getProfits();
			float frProfits = countryTotalMap.get("fr").getProfits();
			float itProfits = countryTotalMap.get("it").getProfits();
			float esProfits = countryTotalMap.get("es").getProfits();
			float jpProfits = countryTotalMap.get("jp").getProfits();
			//计算出时间点内总销售额
			float allProfits = enProfits + deProfits + frProfits + itProfits + esProfits + jpProfits;
			Integer allSalesVolume = 0;
			for (Map.Entry<String, Map<String, SaleReportMonthType>> entry: typeMap.entrySet()) { 
			    String type = entry.getKey();
				if ("total".equals(type)) {
					continue;
				}
				int m = 0;
				Map<String, SaleReportMonthType> countryMap = entry.getValue();
				row=sheet.createRow(rowIndex++);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
				//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeLine.get(type.toLowerCase())==null?"UnGrouped":typeLine.get(type.toLowerCase()));
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(countryMap.get("total").getLine());

				//销售额
				float sales = countryMap.get("total")==null?0:countryMap.get("total").getSales();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales);
				//销量
				Integer salesVolume = countryMap.get("total")==null?0:countryMap.get("total").getSalesVolume();
				allSalesVolume += salesVolume;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);
				//均价
				if (salesVolume > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales/salesVolume);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//销售额占比
				if (salesVolume > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales/allTotal);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//if (UserUtils.hasPermission("amazoninfo:profits:view")) {
					//利润
					float profits = countryMap.get("total")==null?0:countryMap.get("total").getProfits();
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits);
					//利润率
					if (sales > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/sales);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					//单利
					if (sales > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/salesVolume);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					//利润占比
					if (allProfits > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/allProfits);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
				//}
			}
			//月度总计
			int m = 0;
			row=sheet.createRow(rowIndex++);
			indexList.add(rowIndex - 1);
			//时间
			row.createCell(m++).setCellValue(month);
			//类型
			row.createCell(m++).setCellValue("Total");
			//产品线
			row.createCell(m++).setCellValue("");
			//销售额
			row.createCell(m++).setCellValue(allTotal);
			//销量
			row.createCell(m++).setCellValue(allSalesVolume);
			//均价
			cell = row.createCell(m++);
			if (allSalesVolume > 0) {
				cell.setCellValue(allTotal/allSalesVolume);
			} else {
				cell.setCellValue("");
			}
			//销售额占比
			row.createCell(m++).setCellValue(1);
			//if (UserUtils.hasPermission("amazoninfo:profits:view")) {
				//利润
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits);
				//利润率
				if (allTotal > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits/allTotal);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//单利
				if (allTotal > 0) {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits/allSalesVolume);
				} else {
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				//利润占比
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(1);
			//}
		}
		
		Calendar calendar = Calendar.getInstance();
		int currMonth = calendar.get(Calendar.MONTH) + 1;
		int currYear = calendar.get(Calendar.YEAR);
		String monthNow = currMonthString.substring(4);
		int season = 1;	//当前时间所属季度数
		if ("04".equals(monthNow) || "05".equals(monthNow) || "06".equals(monthNow)) {
			season = 2;
		} else if ("07".equals(monthNow) || "08".equals(monthNow) || "09".equals(monthNow)) {
			season = 3;
		} else if ("10".equals(monthNow) || "11".equals(monthNow) || "12".equals(monthNow)) {
			season = 4;
		}
		if (currYear > Integer.parseInt(year) || currMonth > 3) {	//季度过完后才出季度报告
			for (Map.Entry<String, Map<String, Map<String, SaleReportMonthType>>> entryData: data.entrySet()) { 
			    String month = entryData.getKey();
				if (!month.contains("q")) {
					continue;	//只处理季度数据
				}
				if (year.equals(currYearString) && Integer.parseInt(month.substring(1, 2))>=season) {
					continue;
				}
				Map<String, Map<String, SaleReportMonthType>> typeMap = entryData.getValue();
				Map<String, SaleReportMonthType> countryTotalMap = typeMap.get("total");
				float enTotal = countryTotalMap.get("en").getSales();
				float deTotal = countryTotalMap.get("de").getSales();
				float frTotal = countryTotalMap.get("fr").getSales();
				float itTotal = countryTotalMap.get("it").getSales();
				float esTotal = countryTotalMap.get("es").getSales();
				float jpTotal = countryTotalMap.get("jp").getSales();
				float allTotal = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
				
				float enProfits = countryTotalMap.get("en").getProfits();
				float deProfits = countryTotalMap.get("de").getProfits();
				float frProfits = countryTotalMap.get("fr").getProfits();
				float itProfits = countryTotalMap.get("it").getProfits();
				float esProfits = countryTotalMap.get("es").getProfits();
				float jpProfits = countryTotalMap.get("jp").getProfits();
				//计算出时间点内总销售额
				float allProfits = enProfits + deProfits + frProfits + itProfits + esProfits + jpProfits;
				Integer allSalesVolume = 0;
				for (Map.Entry<String, Map<String, SaleReportMonthType>> entry: typeMap.entrySet()) { 
				    String type = entry.getKey();
					if ("total".equals(type)) {
						continue;
					}
					int m = 0;
					Map<String, SaleReportMonthType> countryMap = entry.getValue();
					row = sheet.createRow(rowIndex++);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month.toUpperCase());
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
					//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeLine.get(type.toLowerCase())==null?"UnGrouped":typeLine.get(type.toLowerCase()));
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(countryMap.get("total").getLine());
					//销售额
					float sales = countryMap.get("total")==null?0:countryMap.get("total").getSales();
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales);
					//销量
					Integer salesVolume = countryMap.get("total")==null?0:countryMap.get("total").getSalesVolume();
					allSalesVolume += salesVolume;
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(salesVolume);
					//均价
					if (salesVolume > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales/salesVolume);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					//销售额占比
					if (salesVolume > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(sales/allTotal);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					//if (UserUtils.hasPermission("amazoninfo:profits:view")) {
						//利润
						float profits = countryMap.get("total")==null?0:countryMap.get("total").getProfits();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits);
						//利润率
						if (sales > 0) {
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/sales);
						} else {
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						//单利
						if (sales > 0) {
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/salesVolume);
						} else {
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						//利润占比
						if (allProfits > 0) {
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(profits/allProfits);
						} else {
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
					//}
				}
				//季度总计
				int m = 0;
				row=sheet.createRow(rowIndex++);
				indexList.add(rowIndex - 1);
				//时间
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("colorStyle"));
				cell.setCellValue(month.toUpperCase());
				//类型
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("colorStyle"));
				cell.setCellValue("Total");
				//产品线
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("colorStyle"));
				cell.setCellValue("");
				//销售额
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("colorStyle"));
				cell.setCellValue(allTotal);
				//销量
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("colorStyle"));
				cell.setCellValue(allSalesVolume);
				//均价
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("colorStyle"));
				if (allSalesVolume > 0) {
					cell.setCellValue(allTotal/allSalesVolume);
				} else {
					cell.setCellValue("");
				}
				//销售额占比
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("colorStyle"));
				cell.setCellValue(1);
				//if (UserUtils.hasPermission("amazoninfo:profits:view")) {
					//利润
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits);
					//利润率
					if (allTotal > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits/allTotal);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					//单利
					if (allTotal > 0) {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(allProfits/allSalesVolume);
					} else {
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					//利润占比
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(1);
				//}
			}
			
		}
		
		for (int i = 1; i < rowIndex; i++) {
			if (indexList.contains(i)) {
				for (int j = 0; j < title.size(); j++) {
					if (j == 5 || j == 9) {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorDecStyle"));
					} else if (j == 3 || j == 4 || j == 7) {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorIntStyle"));
					}else if (j == 6 || j == 8 || j == 10) {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorPercentageOne"));
					} else {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorStyle"));
					}
				}
			} else {
				for (int j = 0; j < title.size(); j++) {
					if (j == 5 || j == 9) {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("decimalOneStyle"));
					} else if (j == 3 || j == 4 || j == 7) {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("intStyle"));
					}else if (j == 6 || j == 8 || j == 10) {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("percentageOne"));
					} else {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
					}
				}
			}
	    }
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short)i, true);
		}
	}
	
	//分产品线统计
	private void fillByLineSheet(HSSFSheet sheet, Map<String,Map<String, Map<String, SaleReportMonthType>>> data, 
			Map<String, HSSFCellStyle> styleMap, String year, String currYearString, String currMonthString) {
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 400);
		HSSFCell cell = null;
		List<String>  title = Lists.newArrayList("","日期","产品线","EN","EN%","DE","DE%","FR","FR%","IT","IT%","ES","ES%","JP","JP%","Total","TT%");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		List<String>  rowTitle = Lists.newArrayList("销售额","销量","平均售价");
		//if (UserUtils.hasPermission("amazoninfo:profits:view")) {
			rowTitle.add("利润");
			rowTitle.add("利润率");
		//}
		String monthNow = currMonthString.substring(4);
		int season = 1;	//当前时间所属季度数
		if ("04".equals(monthNow) || "05".equals(monthNow) || "06".equals(monthNow)) {
			season = 2;
		} else if ("07".equals(monthNow) || "08".equals(monthNow) || "09".equals(monthNow)) {
			season = 3;
		} else if ("10".equals(monthNow) || "11".equals(monthNow) || "12".equals(monthNow)) {
			season = 4;
		}
		Map<Integer, Integer> styleFlagMap = Maps.newHashMap();
		int  rowIndex=1;
		for (Map.Entry<String, Map<String, Map<String, SaleReportMonthType>>> entryData: data.entrySet()) { 
		    String month = entryData.getKey();
//			if (month.contains("q")) {
//				continue;	//季度数据显示到月度结尾
//			}
			if (!month.contains("q") && year.equals(currYearString) && Integer.parseInt(month)>=Integer.parseInt(currMonthString)) {
				continue;
			}
			if (month.contains("q") && year.equals(currYearString) && Integer.parseInt(month.substring(1, 2))>=season) {
				continue;
			}
			for (String rowName : rowTitle) {
				Map<String, Map<String, SaleReportMonthType>> typeMap = entryData.getValue();
				Map<String, SaleReportMonthType> countryTotalMap = typeMap.get("total");
				if ("销售额".equals(rowName)) {
					float enTotal = countryTotalMap.get("en").getSales();
					float deTotal = countryTotalMap.get("de").getSales();
					float frTotal = countryTotalMap.get("fr").getSales();
					float itTotal = countryTotalMap.get("it").getSales();
					float esTotal = countryTotalMap.get("es").getSales();
					float jpTotal = countryTotalMap.get("jp").getSales();
					float allTotal = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
					for (Map.Entry<String, Map<String, SaleReportMonthType>> entryType: typeMap.entrySet()) { 
					    String line = entryType.getKey();
						if ("total".equals(line)) {
							continue;
						}
						int m = 0;
						Map<String, SaleReportMonthType> countryMap = entryType.getValue();
						styleFlagMap.put(rowIndex, 1);
						row=sheet.createRow(rowIndex++);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(rowName);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month.toUpperCase());
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(line);
						float en = countryMap.get("en")==null?0:countryMap.get("en").getSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(en);
						//row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(en/(double)enTotal*100) + "%");
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(en/enTotal);
						float de = countryMap.get("de")==null?0:countryMap.get("de").getSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(de);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(de/deTotal);
						float fr = countryMap.get("fr")==null?0:countryMap.get("fr").getSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fr);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fr/frTotal);
						float it = countryMap.get("it")==null?0:countryMap.get("it").getSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it/itTotal);
						float es = countryMap.get("es")==null?0:countryMap.get("es").getSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es/esTotal);
						float jp = countryMap.get("jp")==null?0:countryMap.get("jp").getSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp/jpTotal);
						float total = countryMap.get("total")==null?0:countryMap.get("total").getSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total/allTotal);
					}
					//月度总计
					int m = 0;
					styleFlagMap.put(rowIndex, 1);
					row=sheet.createRow(rowIndex++);
					row.createCell(m++).setCellValue(rowName);
					row.createCell(m++).setCellValue(month.toUpperCase());
					row.createCell(m++).setCellValue("Total");
					List<Float> list = Lists.newArrayList(enTotal,deTotal,frTotal,itTotal,esTotal,jpTotal,allTotal);
					for (Float f : list) {
						row.createCell(m++).setCellValue(f);
						row.createCell(m++).setCellValue(f/allTotal);
					}
				} else if ("销量".equals(rowName)) {
					float enTotal = countryTotalMap.get("en").getSalesVolume();
					float deTotal = countryTotalMap.get("de").getSalesVolume();
					float frTotal = countryTotalMap.get("fr").getSalesVolume();
					float itTotal = countryTotalMap.get("it").getSalesVolume();
					float esTotal = countryTotalMap.get("es").getSalesVolume();
					float jpTotal = countryTotalMap.get("jp").getSalesVolume();
					float allTotal = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
					for (Map.Entry<String, Map<String, SaleReportMonthType>> entryType: typeMap.entrySet()) { 
					    String type = entryType.getKey();
					
						if ("total".equals(type)) {
							continue;
						}
						int m = 0;
						Map<String, SaleReportMonthType> countryMap = entryType.getValue();
						styleFlagMap.put(rowIndex, 2);
						row=sheet.createRow(rowIndex++);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(rowName);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month.toUpperCase());
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
						float en = countryMap.get("en")==null?0:countryMap.get("en").getSalesVolume();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(en);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(en/enTotal);
						float de = countryMap.get("de")==null?0:countryMap.get("de").getSalesVolume();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(de);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(de/deTotal);
						float fr = countryMap.get("fr")==null?0:countryMap.get("fr").getSalesVolume();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fr);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fr/frTotal);
						float it = countryMap.get("it")==null?0:countryMap.get("it").getSalesVolume();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it/itTotal);
						float es = countryMap.get("es")==null?0:countryMap.get("es").getSalesVolume();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es/esTotal);
						float jp = countryMap.get("jp")==null?0:countryMap.get("jp").getSalesVolume();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp/jpTotal);
						float total = countryMap.get("total")==null?0:countryMap.get("total").getSalesVolume();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total/allTotal);
					}
					//月度总计
					int m = 0;
					styleFlagMap.put(rowIndex, 2);
					row=sheet.createRow(rowIndex++);
					row.createCell(m++).setCellValue(rowName);
					row.createCell(m++).setCellValue(month.toUpperCase());
					row.createCell(m++).setCellValue("Total");
					List<Float> list = Lists.newArrayList(enTotal,deTotal,frTotal,itTotal,esTotal,jpTotal,allTotal);
					for (Float f : list) {
						row.createCell(m++).setCellValue(f);
						row.createCell(m++).setCellValue(f/allTotal);
					}
				} else if ("平均售价".equals(rowName)) {
					float enTotal = countryTotalMap.get("en").getAvgSales();
					float deTotal = countryTotalMap.get("de").getAvgSales();
					float frTotal = countryTotalMap.get("fr").getAvgSales();
					float itTotal = countryTotalMap.get("it").getAvgSales();
					float esTotal = countryTotalMap.get("es").getAvgSales();
					float jpTotal = countryTotalMap.get("jp").getAvgSales();
					float allTotal = countryTotalMap.get("total").getAvgSales();
					for (Map.Entry<String, Map<String, SaleReportMonthType>> entryType: typeMap.entrySet()) { 
					    String type = entryType.getKey();
						if ("total".equals(type)) {
							continue;
						}
						int m = 0;
						Map<String, SaleReportMonthType> countryMap = entryType.getValue();
						styleFlagMap.put(rowIndex, 3);
						row=sheet.createRow(rowIndex++);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(rowName);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month.toUpperCase());
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
						float en = countryMap.get("en")==null?0:countryMap.get("en").getAvgSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(en);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						float de = countryMap.get("de")==null?0:countryMap.get("de").getAvgSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(de);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						float fr = countryMap.get("fr")==null?0:countryMap.get("fr").getAvgSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fr);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						float it = countryMap.get("it")==null?0:countryMap.get("it").getAvgSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						float es = countryMap.get("es")==null?0:countryMap.get("es").getAvgSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						float jp = countryMap.get("jp")==null?0:countryMap.get("jp").getAvgSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						float total = countryMap.get("total")==null?0:countryMap.get("total").getAvgSales();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
					//月度总计
					int m = 0;
					styleFlagMap.put(rowIndex, 3);
					row=sheet.createRow(rowIndex++);
					row.createCell(m++).setCellValue(rowName);
					row.createCell(m++).setCellValue(month.toUpperCase());
					row.createCell(m++).setCellValue("Total");
					List<Float> list = Lists.newArrayList(enTotal,deTotal,frTotal,itTotal,esTotal,jpTotal,allTotal);
					for (Float f : list) {
						row.createCell(m++).setCellValue(f);
						row.createCell(m++).setCellValue("");
					}
				} else if ("利润".equals(rowName)) {
					float enTotal = countryTotalMap.get("en").getProfits();
					float deTotal = countryTotalMap.get("de").getProfits();
					float frTotal = countryTotalMap.get("fr").getProfits();
					float itTotal = countryTotalMap.get("it").getProfits();
					float esTotal = countryTotalMap.get("es").getProfits();
					float jpTotal = countryTotalMap.get("jp").getProfits();
					float allTotal = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
					for (Map.Entry<String, Map<String, SaleReportMonthType>> entryType: typeMap.entrySet()) { 
					    String type = entryType.getKey();
						if ("total".equals(type)) {
							continue;
						}
						int m = 0;
						Map<String, SaleReportMonthType> countryMap = entryType.getValue();
						styleFlagMap.put(rowIndex, 4);
						row=sheet.createRow(rowIndex++);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(rowName);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month.toUpperCase());
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
						float en = countryMap.get("en")==null?0:countryMap.get("en").getProfits();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(en);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(en/enTotal);
						float de = countryMap.get("de")==null?0:countryMap.get("de").getProfits();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(de);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(de/deTotal);
						float fr = countryMap.get("fr")==null?0:countryMap.get("fr").getProfits();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fr);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fr/frTotal);
						float it = countryMap.get("it")==null?0:countryMap.get("it").getProfits();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it/itTotal);
						float es = countryMap.get("es")==null?0:countryMap.get("es").getProfits();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es/esTotal);
						float jp = countryMap.get("jp")==null?0:countryMap.get("jp").getProfits();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp/jpTotal);
						float total = countryMap.get("total")==null?0:countryMap.get("total").getProfits();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total/allTotal);
					}
					//月度总计
					int m = 0;
					styleFlagMap.put(rowIndex, 4);
					row=sheet.createRow(rowIndex++);
					row.createCell(m++).setCellValue(rowName);
					row.createCell(m++).setCellValue(month.toUpperCase());
					row.createCell(m++).setCellValue("Total");
					List<Float> list = Lists.newArrayList(enTotal,deTotal,frTotal,itTotal,esTotal,jpTotal,allTotal);
					for (Float f : list) {
						row.createCell(m++).setCellValue(f);
						row.createCell(m++).setCellValue(f/allTotal);
					}
				} else if ("利润率".equals(rowName)) {
					float enTotal = countryTotalMap.get("en").getProfitRate();
					float deTotal = countryTotalMap.get("de").getProfitRate();
					float frTotal = countryTotalMap.get("fr").getProfitRate();
					float itTotal = countryTotalMap.get("it").getProfitRate();
					float esTotal = countryTotalMap.get("es").getProfitRate();
					float jpTotal = countryTotalMap.get("jp").getProfitRate();
					float allTotal = countryTotalMap.get("total").getProfitRate();
					for (Map.Entry<String, Map<String,SaleReportMonthType>> entry: typeMap.entrySet()) { 
					    String type  = entry.getKey();
						if ("total".equals(type)) {
							continue;
						}
						int m = 0;
						Map<String, SaleReportMonthType> countryMap = entry.getValue();
						styleFlagMap.put(rowIndex, 5);
						row=sheet.createRow(rowIndex++);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(rowName);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month.toUpperCase());
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
						float en = countryMap.get("en")==null?0:countryMap.get("en").getProfitRate();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(en);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						float de = countryMap.get("de")==null?0:countryMap.get("de").getProfitRate();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(de);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						float fr = countryMap.get("fr")==null?0:countryMap.get("fr").getProfitRate();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fr);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						float it = countryMap.get("it")==null?0:countryMap.get("it").getProfitRate();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						float es = countryMap.get("es")==null?0:countryMap.get("es").getProfitRate();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						float jp = countryMap.get("jp")==null?0:countryMap.get("jp").getProfitRate();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						float total = countryMap.get("total")==null?0:countryMap.get("total").getProfitRate();
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total);
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
					//月度总计
					int m = 0;
					styleFlagMap.put(rowIndex, 5);
					row=sheet.createRow(rowIndex++);
					row.createCell(m++).setCellValue(rowName);
					row.createCell(m++).setCellValue(month.toUpperCase());
					row.createCell(m++).setCellValue("Total");
					List<Float> list = Lists.newArrayList(enTotal,deTotal,frTotal,itTotal,esTotal,jpTotal,allTotal);
					for (Float f : list) {
						row.createCell(m++).setCellValue(f);
						row.createCell(m++).setCellValue("");
					}
				}
			}
		}
		
		for (int i = 1; i < rowIndex; i++) {
			for (int j = 0; j < title.size(); j++) {
				if (j < 3) {
					sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
				} else {
					if (styleFlagMap.get(i) == 1 || styleFlagMap.get(i) == 2 || styleFlagMap.get(i) == 4) {	//销售额&销量&利润
						if (j % 2 == 1) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("intStyle"));
						} else {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("percentageInt"));
						}
					} else if (styleFlagMap.get(i) == 3) {	//平均售价
						if (j % 2 == 1) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("decimalOneStyle"));
						} else {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
						}
					} else if (styleFlagMap.get(i) == 5) {	//利润率
						if (j % 2 == 1) {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("percentageOne"));
						} else {
							sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
						}
					}
				}
			}
	    }
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short)i, true);
		}
	}

	//分市场销售额
	private void fillSalesSheet(HSSFSheet sheet, Map<String,Map<String, Map<String, SaleReportMonthType>>> data, 
			Map<String, HSSFCellStyle> styleMap, String year, String currYearString, String currMonthString, Map<String, String> typeLine) {
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 400);
		HSSFCell cell = null;
		List<String>  title = Lists.newArrayList("日期","产品类型","产品线","EN","EN%","DE","DE%","FR","FR%","IT","IT%","ES","ES%","JP","JP%","Total","TT%");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		List<Integer> indexList = Lists.newArrayList();
		int  rowIndex=1;
		for (Map.Entry<String, Map<String, Map<String, SaleReportMonthType>>> entryData: data.entrySet()) { 
		    String month = entryData.getKey();
			if (month.contains("q")) {
				continue;	//季度数据显示到月度结尾
			}
			if (year.equals(currYearString) && Integer.parseInt(month)>=Integer.parseInt(currMonthString)) {
				continue;
			}
			Map<String, Map<String, SaleReportMonthType>> typeMap = entryData.getValue();
			Map<String, SaleReportMonthType> countryTotalMap = typeMap.get("total");
			float enTotal = countryTotalMap.get("en").getSales();
			float deTotal = countryTotalMap.get("de").getSales();
			float frTotal = countryTotalMap.get("fr").getSales();
			float itTotal = countryTotalMap.get("it").getSales();
			float esTotal = countryTotalMap.get("es").getSales();
			float jpTotal = countryTotalMap.get("jp").getSales();
			float allTotal = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
			for (Map.Entry<String,Map<String, SaleReportMonthType>> entryType: typeMap.entrySet()) { 
			    String type =entryType.getKey();
				if ("total".equals(type)) {
					continue;
				}
				int m = 0;
				Map<String, SaleReportMonthType> countryMap = entryType.getValue();
				row=sheet.createRow(rowIndex++);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
				//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeLine.get(type.toLowerCase())==null?"UnGrouped":typeLine.get(type.toLowerCase()));
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(countryMap.get("total").getLine());
				float en = countryMap.get("en")==null?0:countryMap.get("en").getSales();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(en);
				//row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(en/(double)enTotal*100) + "%");
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(en/enTotal);
				float de = countryMap.get("de")==null?0:countryMap.get("de").getSales();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(de);
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(de/deTotal);
				float fr = countryMap.get("fr")==null?0:countryMap.get("fr").getSales();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fr);
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fr/frTotal);
				float it = countryMap.get("it")==null?0:countryMap.get("it").getSales();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it/itTotal);
				float es = countryMap.get("es")==null?0:countryMap.get("es").getSales();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es/esTotal);
				float jp = countryMap.get("jp")==null?0:countryMap.get("jp").getSales();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp/jpTotal);
				float total = countryMap.get("total")==null?0:countryMap.get("total").getSales();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total/allTotal);
			}
			//月度总计
			int m = 0;
			row=sheet.createRow(rowIndex++);
			indexList.add(rowIndex - 1);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(month);
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue("Total");
			cell = row.createCell(m++);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue("");
			List<Float> list = Lists.newArrayList(enTotal,deTotal,frTotal,itTotal,esTotal,jpTotal,allTotal);
			for (Float f : list) {
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("colorStyle"));
				cell.setCellValue(Math.round(f));
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("colorStyle"));
				cell.setCellValue(Math.round(f/allTotal*100) + "%");
			}
		}
		
		Calendar calendar = Calendar.getInstance();
		int currMonth = calendar.get(Calendar.MONTH) + 1;
		int currYear = calendar.get(Calendar.YEAR);
		String monthNow = currMonthString.substring(4);
		int season = 1;	//当前时间所属季度数
		if ("04".equals(monthNow) || "05".equals(monthNow) || "06".equals(monthNow)) {
			season = 2;
		} else if ("07".equals(monthNow) || "08".equals(monthNow) || "09".equals(monthNow)) {
			season = 3;
		} else if ("10".equals(monthNow) || "11".equals(monthNow) || "12".equals(monthNow)) {
			season = 4;
		}
		if (currYear > Integer.parseInt(year) || currMonth > 3) {	//季度过完后才出季度报告
			for (Map.Entry<String, Map<String, Map<String, SaleReportMonthType>>> entryData: data.entrySet()) { 
			    String month = entryData.getKey();
				if (!month.contains("q")) {
					continue;	//只处理季度数据
				}
				if (year.equals(currYearString) && Integer.parseInt(month.substring(1, 2))>=season) {
					continue;
				}
				Map<String, Map<String, SaleReportMonthType>> typeMap = entryData.getValue();
				Map<String, SaleReportMonthType> countryTotalMap = typeMap.get("total");
				float enTotal = countryTotalMap.get("en").getSales();
				float deTotal = countryTotalMap.get("de").getSales();
				float frTotal = countryTotalMap.get("fr").getSales();
				float itTotal = countryTotalMap.get("it").getSales();
				float esTotal = countryTotalMap.get("es").getSales();
				float jpTotal = countryTotalMap.get("jp").getSales();
				float allTotal = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
				for (Map.Entry<String, Map<String, SaleReportMonthType>> entry: typeMap.entrySet()) { 
				    String type = entry.getKey();
					if ("total".equals(type)) {
						continue;
					}
					int m = 0;
					Map<String, SaleReportMonthType> countryMap = entry.getValue();
					row = sheet.createRow(rowIndex++);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month.toUpperCase());
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
					//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeLine.get(type.toLowerCase())==null?"UnGrouped":typeLine.get(type.toLowerCase()));
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(countryMap.get("total").getLine());
					float en = countryMap.get("en")==null?0:countryMap.get("en").getSales();
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(en);
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(en/enTotal);
					float de = countryMap.get("de")==null?0:countryMap.get("de").getSales();
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(de);
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(de/deTotal);
					float fr = countryMap.get("fr")==null?0:countryMap.get("fr").getSales();
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fr);
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fr/frTotal);
					float it = countryMap.get("it")==null?0:countryMap.get("it").getSales();
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it/itTotal);
					float es = countryMap.get("es")==null?0:countryMap.get("es").getSales();
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es/esTotal);
					float jp = countryMap.get("jp")==null?0:countryMap.get("jp").getSales();
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp/jpTotal);
					float total = countryMap.get("total")==null?0:countryMap.get("total").getSales();
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total/allTotal);
				}
				//季度总计
				int m = 0;
				row = sheet.createRow(rowIndex++);
				indexList.add(rowIndex - 1);
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("colorStyle"));
				cell.setCellValue(month.toUpperCase());
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("colorStyle"));
				cell.setCellValue("Total");
				cell = row.createCell(m++);
				cell.setCellStyle(styleMap.get("colorStyle"));
				cell.setCellValue("");
				List<Float> list = Lists.newArrayList(enTotal,deTotal,frTotal,itTotal,esTotal,jpTotal,allTotal);
				for (Float f : list) {
					cell = row.createCell(m++);
					cell.setCellStyle(styleMap.get("colorStyle"));
					cell.setCellValue(Math.round(f));
					cell = row.createCell(m++);
					cell.setCellStyle(styleMap.get("colorStyle"));
					cell.setCellValue(Math.round(f/allTotal*100) + "%");
				}
			}
			
		}
		
		for (int i = 1; i < rowIndex; i++) {
			if (indexList.contains(i)) {
				continue;
			}
			for (int j = 0; j < title.size(); j++) {
				if (j >3 && j % 2 == 0) {
					sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("percentageInt"));
				} else if (j >2 && j % 2 == 1) {
					sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("intStyle"));
				} else {
					sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
				}
			}
	    }
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short)i, true);
		}
	}

	//分市场利润
	private void fillProfitSheet(HSSFSheet sheet, Map<String,Map<String, Map<String, SaleReportMonthType>>> data, 
			Map<String, HSSFCellStyle> styleMap, String year, String currYearString, String currMonthString, Map<String, String> typeLine) {
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 400);
		HSSFCell cell = null;
		List<String>  title = Lists.newArrayList("日期","产品类型","产品线","EN","EN%","DE","DE%","FR","FR%","IT","IT%","ES","ES%","JP","JP%","Total","TT%");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		
		String monthNow = currMonthString.substring(4);
		int season = 1;	//当前时间所属季度数
		if ("04".equals(monthNow) || "05".equals(monthNow) || "06".equals(monthNow)) {
			season = 2;
		} else if ("07".equals(monthNow) || "08".equals(monthNow) || "09".equals(monthNow)) {
			season = 3;
		} else if ("10".equals(monthNow) || "11".equals(monthNow) || "12".equals(monthNow)) {
			season = 4;
		}
		List<Integer> indexList = Lists.newArrayList();
		int  rowIndex=1;
		for (Map.Entry<String, Map<String, Map<String, SaleReportMonthType>>> entryData: data.entrySet()) { 
		    String month = entryData.getKey();
			if (!month.contains("q") && year.equals(currYearString) && Integer.parseInt(month)>=Integer.parseInt(currMonthString)) {
				continue;
			}
			if (month.contains("q") && year.equals(currYearString) && Integer.parseInt(month.substring(1, 2))>=season) {
				continue;
			}
			Map<String, Map<String, SaleReportMonthType>> typeMap = entryData.getValue();
			Map<String, SaleReportMonthType> countryTotalMap = typeMap.get("total");
			float enTotal = countryTotalMap.get("en").getProfits();
			float deTotal = countryTotalMap.get("de").getProfits();
			float frTotal = countryTotalMap.get("fr").getProfits();
			float itTotal = countryTotalMap.get("it").getProfits();
			float esTotal = countryTotalMap.get("es").getProfits();
			float jpTotal = countryTotalMap.get("jp").getProfits();
			float allTotal = enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal;
			for (Map.Entry<String, Map<String, SaleReportMonthType>> entry: typeMap.entrySet()) { 
			    String type = entry.getKey();
				if ("total".equals(type)) {
					continue;
				}
				int m = 0;
				Map<String, SaleReportMonthType> countryMap = entry.getValue();
				row=sheet.createRow(rowIndex++);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month.toUpperCase());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
				//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeLine.get(type.toLowerCase())==null?"UnGrouped":typeLine.get(type.toLowerCase()));
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(countryMap.get("total").getLine());
				float en = countryMap.get("en")==null?0:countryMap.get("en").getProfits();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(en);
				//row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(en/(double)enTotal*100) + "%");
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(en/enTotal);
				float de = countryMap.get("de")==null?0:countryMap.get("de").getProfits();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(de);
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(de/deTotal);
				float fr = countryMap.get("fr")==null?0:countryMap.get("fr").getProfits();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fr);
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fr/frTotal);
				float it = countryMap.get("it")==null?0:countryMap.get("it").getProfits();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it/itTotal);
				float es = countryMap.get("es")==null?0:countryMap.get("es").getProfits();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es/esTotal);
				float jp = countryMap.get("jp")==null?0:countryMap.get("jp").getProfits();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp/jpTotal);
				float total = countryMap.get("total")==null?0:countryMap.get("total").getProfits();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total/allTotal);
			}
			//月度总计
			int m = 0;
			row=sheet.createRow(rowIndex++);
			indexList.add(rowIndex - 1);
			row.createCell(m++).setCellValue(month.toUpperCase());
			row.createCell(m++).setCellValue("Total");
			row.createCell(m++).setCellValue("");
			List<Float> list = Lists.newArrayList(enTotal,deTotal,frTotal,itTotal,esTotal,jpTotal,allTotal);
			for (Float f : list) {
				row.createCell(m++).setCellValue(f);
				row.createCell(m++).setCellValue(f/allTotal);
			}
		}
		
		for (int i = 1; i < rowIndex; i++) {
			if (indexList.contains(i)) {
				for (int j = 0; j < title.size(); j++) {
					if (j < 3) {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorStyle"));
					} else if (j % 2 == 1) {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorIntStyle"));
					} else {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorPercentageInt"));
					}
				}
			} else {
				for (int j = 0; j < title.size(); j++) {
					if (j < 3) {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
					} else if (j % 2 == 1) {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("intStyle"));
					} else {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("percentageInt"));
					}
				}
			}
	    }
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short)i, true);
		}
	}

	//分市场利润率
	private void fillProfitRateSheet(HSSFSheet sheet, Map<String,Map<String, Map<String, SaleReportMonthType>>> data, 
			Map<String, HSSFCellStyle> styleMap, String year, String currYearString, String currMonthString, Map<String, String> typeLine) {
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 400);
		HSSFCell cell = null;
		List<String>  title = Lists.newArrayList("日期","产品类型","产品线","EN","DE","FR","IT","ES","JP","Total");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(styleMap.get("colorStyle"));
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		
		String monthNow = currMonthString.substring(4);
		int season = 1;	//当前时间所属季度数
		if ("04".equals(monthNow) || "05".equals(monthNow) || "06".equals(monthNow)) {
			season = 2;
		} else if ("07".equals(monthNow) || "08".equals(monthNow) || "09".equals(monthNow)) {
			season = 3;
		} else if ("10".equals(monthNow) || "11".equals(monthNow) || "12".equals(monthNow)) {
			season = 4;
		}
		List<Integer> indexList = Lists.newArrayList();
		int  rowIndex=1;
		for (Map.Entry<String, Map<String, Map<String, SaleReportMonthType>>> entryData: data.entrySet()) { 
		    String month =entryData.getKey();
			if (!month.contains("q") && year.equals(currYearString) && Integer.parseInt(month)>=Integer.parseInt(currMonthString)) {
				continue;
			}
			if (month.contains("q") && year.equals(currYearString) && Integer.parseInt(month.substring(1, 2))>=season) {
				continue;
			}
			Map<String, Map<String, SaleReportMonthType>> typeMap = entryData.getValue();
			Map<String, SaleReportMonthType> countryTotalMap = typeMap.get("total");
			float enTotal = countryTotalMap.get("en").getProfitRate();
			float deTotal = countryTotalMap.get("de").getProfitRate();
			float frTotal = countryTotalMap.get("fr").getProfitRate();
			float itTotal = countryTotalMap.get("it").getProfitRate();
			float esTotal = countryTotalMap.get("es").getProfitRate();
			float jpTotal = countryTotalMap.get("jp").getProfitRate();
			float allTotal = countryTotalMap.get("total").getProfitRate();
			for (Map.Entry<String, Map<String, SaleReportMonthType>> entry: typeMap.entrySet()) { 
		        String type = entry.getKey();
				if ("total".equals(type)) {
					continue;
				}
				int m = 0;
				Map<String, SaleReportMonthType> countryMap = entry.getValue();
				row=sheet.createRow(rowIndex++);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(month.toUpperCase());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(type);
				//row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(typeLine.get(type.toLowerCase())==null?"UnGrouped":typeLine.get(type.toLowerCase()));
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(countryMap.get("total").getLine());
				float en = countryMap.get("en")==null?0:countryMap.get("en").getProfitRate();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(en);
				float de = countryMap.get("de")==null?0:countryMap.get("de").getProfitRate();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(de);
				float fr = countryMap.get("fr")==null?0:countryMap.get("fr").getProfitRate();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fr);
				float it = countryMap.get("it")==null?0:countryMap.get("it").getProfitRate();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(it);
				float es = countryMap.get("es")==null?0:countryMap.get("es").getProfitRate();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(es);
				float jp = countryMap.get("jp")==null?0:countryMap.get("jp").getProfitRate();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(jp);
				float total = countryMap.get("total")==null?0:countryMap.get("total").getProfitRate();
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(total);
			}
			//总计
			int m = 0;
			row=sheet.createRow(rowIndex++);
			indexList.add(rowIndex - 1);
			row.createCell(m++).setCellValue(month.toUpperCase());
			row.createCell(m++).setCellValue("Total");
			row.createCell(m++).setCellValue("");
			List<Float> list = Lists.newArrayList(enTotal,deTotal,frTotal,itTotal,esTotal,jpTotal,allTotal);
			for (Float f : list) {
				row.createCell(m++).setCellValue(f);
			}
		}
		
		for (int i = 1; i < rowIndex; i++) {
			if (indexList.contains(i)) {
				for (int j = 0; j < title.size(); j++) {
					if (j < 3) {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorStyle"));
					} else {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("colorPercentageInt"));
					}
				}
			} else {
				for (int j = 0; j < title.size(); j++) {
					if (j < 3) {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("contentStyle"));
					} else {
						sheet.getRow(i).getCell(j).setCellStyle(styleMap.get("percentageInt"));
					}
				}
			}
	    }
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short)i, true);
		}
	}
	
	private static Map<String, String> monthStringMap(String year) {
		Map<String, String> monthMap = Maps.newLinkedHashMap();
		monthMap.put(year + "01", "Jan");
		monthMap.put(year + "02", "Feb");
		monthMap.put(year + "03", "Mar");
		monthMap.put(year + "04", "Apr");
		monthMap.put(year + "05", "Mar");
		monthMap.put(year + "06", "Jun");
		monthMap.put(year + "07", "Jul");
		monthMap.put(year + "08", "Aug");
		monthMap.put(year + "09", "Sep");
		monthMap.put(year + "10", "Oct");
		monthMap.put(year + "11", "Nov");
		monthMap.put(year + "12", "Dec");
		return monthMap;
	}
	
	
	/**
	 * 销售数据换算为分产品线map
	 * @param data	[月份[类型[国家	销售额]]]
	 * @param typeLine	[类型	 产品线]不含UnGrouped
	 * @return [月份[产品线[国家	销售额]]]含UnGrouped 不含季度总计
	 */
	private static Map<String,Map<String, Map<String, Float>>> changeLineData(
			Map<String,Map<String, Map<String, Float>>> data, Map<String, String> typeLine) {
		Map<String,Map<String, Map<String, Float>>> rs = Maps.newHashMap();
		for (Map.Entry<String,Map<String, Map<String, Float>>> entryData: data.entrySet()) { 
		    String month = entryData.getKey();
			if (month.contains("q")) {
				continue;	//跳过季度数据
			}
			Map<String, Map<String, Float>> typeMap = entryData.getValue();
			Map<String, Map<String, Float>> lineTypeMap = rs.get(month);
			if (lineTypeMap == null) {
				lineTypeMap = Maps.newHashMap();
				rs.put(month, lineTypeMap);
			}
			for (Map.Entry<String, Map<String, Float>> entryType: typeMap.entrySet()) { 
			    String type = entryType.getKey();
				if ("total".equals(type)) {
					continue;
				}
				Map<String, Float> countryMap = entryType.getValue();
				String lineName = typeLine.get(type.toLowerCase());
				if (StringUtils.isEmpty(lineName)) {
					lineName = "UnGrouped";
				}
				Map<String, Float> lineCountryMap = lineTypeMap.get(lineName);
				if (lineCountryMap == null) {
					lineCountryMap = Maps.newHashMap();
					lineTypeMap.put(lineName, lineCountryMap);
				}
				for (Map.Entry<String,Float> entry: countryMap.entrySet()) { 
				    String country = entry.getKey();
					Float sales = entry.getValue();
					Float lineSales = lineCountryMap.get(country);
					if (lineSales == null) {
						lineCountryMap.put(country, sales);
					} else {
						lineCountryMap.put(country, lineSales + sales);
					}
				}
			}
		}
		return rs;
	}
	
	/**
	 * 销售数据换算为分产品线map,SaleReportMonthType对象中属性一起转换
	 * @param data	[月份[类型[国家	销售额]]]
	 * @param typeLine	[类型	 产品线]不含UnGrouped
	 * @return [月份[产品线[国家	销售额]]]含UnGrouped 不含季度总计
	 */
	private static Map<String,Map<String, Map<String, SaleReportMonthType>>> changeLineData1(
			Map<String,Map<String, Map<String, SaleReportMonthType>>> data, Map<String, String> typeLine) {
		Map<String, Map<String, Map<String, SaleReportMonthType>>> rs = Maps.newTreeMap();
		for (Map.Entry<String,Map<String, Map<String, SaleReportMonthType>>> entry: data.entrySet()) { 
		    String month = entry.getKey();
			Map<String, Map<String, SaleReportMonthType>> typeMap = entry.getValue();
			Map<String, Map<String, SaleReportMonthType>> lineTypeMap = rs.get(month);
			if (lineTypeMap == null) {
				lineTypeMap = Maps.newTreeMap();
				rs.put(month, lineTypeMap);
			}
			for (Map.Entry<String, Map<String, SaleReportMonthType>> entryType: typeMap.entrySet()) { 
			    String type =entryType.getKey();
				if ("total".equals(type)) {
					continue;
				}
				Map<String, SaleReportMonthType> countryMap = entryType.getValue();
				String lineName = typeLine.get(type.toLowerCase());
				if (StringUtils.isEmpty(lineName)) {
					lineName = "UnGrouped";
				}
				//月、产品线
				Map<String, SaleReportMonthType> lineCountryMap = lineTypeMap.get(lineName);
				if (lineCountryMap == null) {
					lineCountryMap = Maps.newHashMap();
					lineTypeMap.put(lineName, lineCountryMap);
				}
				//总产品线
				Map<String, SaleReportMonthType> lineTotalMap = lineTypeMap.get("total");
				if (lineTotalMap == null) {
					lineTotalMap = Maps.newHashMap();
					lineTypeMap.put("total", lineTotalMap);
				}
				for (Map.Entry<String,SaleReportMonthType> entryRs: countryMap.entrySet()) { 
				    String country = entryRs.getKey();
					SaleReportMonthType monthType = entryRs.getValue();
					float sales = monthType==null?0f:monthType.getSales();
					Integer salesVolume = monthType==null?0:monthType.getSalesVolume();
					float profits = monthType==null?0f:monthType.getProfits();
					
					SaleReportMonthType lineMonthType = lineCountryMap.get(country);
					if (lineMonthType == null) {
						lineMonthType = new SaleReportMonthType();
						lineMonthType.setSales(sales);
						lineMonthType.setSalesVolume(salesVolume);
						lineMonthType.setProfits(profits);
						lineCountryMap.put(country, lineMonthType);
					} else {
						lineMonthType.setSales(lineMonthType.getSales() + sales);
						lineMonthType.setSalesVolume(lineMonthType.getSalesVolume() + salesVolume);
						lineMonthType.setProfits(lineMonthType.getProfits() + profits);
						lineCountryMap.put(country, lineMonthType);
					}
				
					//total产品线分国家统计
					SaleReportMonthType lineMonthTotal = lineTotalMap.get(country);
					if (lineMonthTotal == null) {
						lineMonthTotal = new SaleReportMonthType();
						lineMonthTotal.setSales(sales);
						lineMonthTotal.setSalesVolume(salesVolume);
						lineMonthTotal.setProfits(profits);
						lineTotalMap.put(country, lineMonthTotal);
					} else {
						lineMonthTotal.setSales(lineMonthTotal.getSales() + sales);
						lineMonthTotal.setSalesVolume(lineMonthTotal.getSalesVolume() + salesVolume);
						lineMonthTotal.setProfits(lineMonthTotal.getProfits() + profits);
						lineTotalMap.put(country, lineMonthTotal);
					}
					
				}
			}
		}
		return rs;
	}
	
	/**
	 * 销售数据换算为分产品线map,直接按统计表中的产品线统计(情景：新增F线从A线分出产品类型,按实时产品线关系统计会把原来A线的销售额统计到F线中)
	 * @param data	[月份[类型[国家	销售额]]]
	 * @param typeLine	[类型	 产品线]不含UnGrouped
	 * @return [月份[产品线[国家	销售额]]]含UnGrouped 不含季度总计
	 */
	private static Map<String,Map<String, Map<String, SaleReportMonthType>>> changeLineDataLine(
			Map<String,Map<String, Map<String, SaleReportMonthType>>> data, Map<String, String> typeLine) {
		Map<String, Map<String, Map<String, SaleReportMonthType>>> rs = Maps.newTreeMap();
		for (Map.Entry<String,Map<String, Map<String, SaleReportMonthType>>> entry: data.entrySet()) { 
		    String month = entry.getKey();
			Map<String, Map<String, SaleReportMonthType>> typeMap = entry.getValue();
			Map<String, Map<String, SaleReportMonthType>> lineTypeMap = rs.get(month);
			if (lineTypeMap == null) {
				lineTypeMap = Maps.newTreeMap();
				rs.put(month, lineTypeMap);
			}
			for (Map.Entry<String, Map<String, SaleReportMonthType>> entryType: typeMap.entrySet()) { 
			    String type = entryType.getKey();
				if ("total".equals(type)) {
					continue;
				}
				Map<String, SaleReportMonthType> countryMap = entryType.getValue();
				for (Map.Entry<String, SaleReportMonthType> entryRs: countryMap.entrySet()) { 
				     String country = entryRs.getKey();
					SaleReportMonthType monthType = entryRs.getValue();
					float sales = monthType==null?0f:monthType.getSales();
					Integer salesVolume = monthType==null?0:monthType.getSalesVolume();
					float profits = monthType==null?0f:monthType.getProfits();
					String lineName = monthType==null?null:monthType.getLine();
					float salesNoTax = monthType==null?0f:monthType.getSalesNoTax();
					float adInEventFee = monthType==null?0f:monthType.getAdInEventFee(); 
					
					if (StringUtils.isEmpty(lineName)) {
						lineName = typeLine.get(type.toLowerCase());
					}
					if (StringUtils.isEmpty(lineName)) {
						lineName = "UnGrouped";
					}
					//月、产品线
					Map<String, SaleReportMonthType> lineCountryMap = lineTypeMap.get(lineName);
					if (lineCountryMap == null) {
						lineCountryMap = Maps.newHashMap();
						lineTypeMap.put(lineName, lineCountryMap);
					}
					//总产品线
					Map<String, SaleReportMonthType> lineTotalMap = lineTypeMap.get("total");
					if (lineTotalMap == null) {
						lineTotalMap = Maps.newHashMap();
						lineTypeMap.put("total", lineTotalMap);
					}
					
					SaleReportMonthType lineMonthType = lineCountryMap.get(country);
					if (lineMonthType == null) {
						lineMonthType = new SaleReportMonthType();
						lineMonthType.setSales(sales);
						lineMonthType.setSalesVolume(salesVolume);
						lineMonthType.setProfits(profits);
						lineMonthType.setSalesNoTax(salesNoTax);
						lineMonthType.setAdInEventFee(adInEventFee);
						lineCountryMap.put(country, lineMonthType);
					} else {
						lineMonthType.setSales(lineMonthType.getSales() + sales);
						lineMonthType.setSalesVolume(lineMonthType.getSalesVolume() + salesVolume);
						lineMonthType.setProfits(lineMonthType.getProfits() + profits);
						lineMonthType.setSalesNoTax(lineMonthType.getSalesNoTax() + salesNoTax);
						lineMonthType.setAdInEventFee(lineMonthType.getAdInEventFee() + adInEventFee);
						lineCountryMap.put(country, lineMonthType);
					}
				
					//if(!"E".equals(lineName)){
						//total产品线分国家统计
						SaleReportMonthType lineMonthTotal = lineTotalMap.get(country);
						if (lineMonthTotal == null) {
							lineMonthTotal = new SaleReportMonthType();
							lineMonthTotal.setSales(sales);
							lineMonthTotal.setSalesVolume(salesVolume);
							lineMonthTotal.setProfits(profits);
							lineMonthTotal.setSalesNoTax(salesNoTax);
							lineMonthTotal.setAdInEventFee(adInEventFee);
							lineTotalMap.put(country, lineMonthTotal);
						} else {
							lineMonthTotal.setSales(lineMonthTotal.getSales() + sales);
							lineMonthTotal.setSalesVolume(lineMonthTotal.getSalesVolume() + salesVolume);
							lineMonthTotal.setProfits(lineMonthTotal.getProfits() + profits);
							lineMonthTotal.setSalesNoTax(lineMonthTotal.getSalesNoTax() + salesNoTax);
							lineMonthTotal.setAdInEventFee(lineMonthTotal.getAdInEventFee() + adInEventFee);
							lineTotalMap.put(country, lineMonthTotal);
						}
					//}
				}
			}
		}
		return rs;
	}
	
	
	@RequestMapping(value = {"exportMaxOrder"})
	public String exportMaxOrder(String[] productsName,SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String,List<Object[]>>  map=amazonOrderService.findMaxOrderInfo(saleReport.getStart(),saleReport.getEnd(),saleReport.getCountry(),productsName!=null?Sets.newHashSet(productsName):null);
		Map<String,Integer> returnMap=returnGoodsService.findReturnQuantity(saleReport.getStart(),saleReport.getEnd(),saleReport.getCountry());
		
		HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		
		row.setHeight((short) 600);
		HSSFCell cell = null;	
		List<String>  title=Lists.newArrayList("产品","国家","订单号","时间","邮箱","CustomId","数量","退货数量","地址");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		int  rowIndex=1;
		for (Map.Entry<String, List<Object[]>>  entry : map.entrySet()) { 
		    String orderIdAndName = entry.getKey();
			List<Object[]> objList=entry.getValue();
			for (Object[] obj: objList) {
				row=sheet.createRow(rowIndex++);
				int m=0;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[5].toString());
				row.getCell(m-1).setCellStyle(contentStyle);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(obj[0].toString())?"us":obj[0].toString());
				row.getCell(m-1).setCellStyle(contentStyle);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[1].toString());
				row.getCell(m-1).setCellStyle(contentStyle);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(formatDay.format(obj[2]));
				row.getCell(m-1).setCellStyle(contentStyle);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[3]==null?"":obj[3].toString());
				row.getCell(m-1).setCellStyle(contentStyle);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[4]==null?"":obj[4].toString());
				row.getCell(m-1).setCellStyle(contentStyle);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[6].toString());
				row.getCell(m-1).setCellStyle(contentStyle);
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(returnMap.get(orderIdAndName)==null?0:returnMap.get(orderIdAndName));
				row.getCell(m-1).setCellStyle(contentStyle);
				String info="";
				if(obj[7]!=null){
					info+=obj[7].toString()+"/";
				}
				if(obj[8]!=null){
					info+=obj[8].toString()+"/";
				}
				if(obj[9]!=null){
					info+=obj[9].toString()+"/";
				}
				if(obj[10]!=null){
					info+=obj[10].toString()+"/";
				}
				if(obj[11]!=null){
					info+=obj[11].toString()+"/";
				}
				if(obj[12]!=null){
					info+=obj[12].toString()+"/";
				}
				if(obj[13]!=null){
					info+=obj[13].toString()+"/";
				}
				if(obj[14]!=null){
					info+=obj[14].toString()+"/";
				}
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(info);
				row.getCell(m-1).setCellStyle(contentStyle);
			}
		}
		 
		for (int i = 0; i < title.size(); i++) {
       		 sheet.autoSizeColumn((short)i, true);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "MaxOrder" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	
	/**
	 * mark 格式化数字保留一位小数不生效,必须保留两位小数,原因不明
	 * @param wb
	 * @return key/value 
	 * title: 标题,黄色	
	 * contentStyle:普通字符串		
	 * percentageInt:百分比取整	
	 * percentageOne:百分比保留小数
	 * intStyle：整数	
	 * decimalOneStyle：保留小数
	 * colorStyle：普通格式带黄色背景
	 * colorIntStyle：黄色背景整数		
	 * colorDecStyle:黄色背景保留小数
	 * colorPercentageInt：黄色背景百分比取整 	
	 * colorPercentageOne：黄色背景百分比保留小数
	 * 
	 */
	private static Map<String, HSSFCellStyle> getAllStyle(HSSFWorkbook wb){
		Map<String, HSSFCellStyle> styleMap = Maps.newHashMap();
		//表头
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.YELLOW.index);
		style.setFillForegroundColor(HSSFColor.YELLOW.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		styleMap.put("title", style);	//标题格式

		//普通字符串
		HSSFCellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		styleMap.put("contentStyle", contentStyle);	//字符串格式
		
		//取整百分比显示
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		styleMap.put("percentageInt", cellStyle);	//不带小数百分比
		
		//取整数显示
		HSSFCellStyle intStyle = wb.createCellStyle();
		intStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		intStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		intStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		intStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		intStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		intStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		intStyle.setRightBorderColor(HSSFColor.BLACK.index);
		intStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		intStyle.setTopBorderColor(HSSFColor.BLACK.index);
		styleMap.put("intStyle", intStyle);	//整数
		
		//两位小数显示
		HSSFCellStyle decimalStyle = wb.createCellStyle();
		decimalStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		decimalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setRightBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setTopBorderColor(HSSFColor.BLACK.index);
		styleMap.put("decimalOneStyle", decimalStyle);	//两位小数
		
		//两位小数显示
		HSSFCellStyle percentageOne = wb.createCellStyle();
		percentageOne.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
		percentageOne.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		percentageOne.setBottomBorderColor(HSSFColor.BLACK.index);
		percentageOne.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		percentageOne.setLeftBorderColor(HSSFColor.BLACK.index);
		percentageOne.setBorderRight(HSSFCellStyle.BORDER_THIN);
		percentageOne.setRightBorderColor(HSSFColor.BLACK.index);
		percentageOne.setBorderTop(HSSFCellStyle.BORDER_THIN);
		percentageOne.setTopBorderColor(HSSFColor.BLACK.index);
		styleMap.put("percentageOne", percentageOne);	//一位小数
		
		//高亮显示
		HSSFCellStyle colorStyle = wb.createCellStyle();
		colorStyle.setFillBackgroundColor(HSSFColor.YELLOW.index);
		colorStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		colorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		colorStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		colorStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		colorStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		colorStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		colorStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		colorStyle.setRightBorderColor(HSSFColor.BLACK.index);
		colorStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		colorStyle.setTopBorderColor(HSSFColor.BLACK.index);
		styleMap.put("colorStyle", colorStyle);	//普通带颜色格式
		
		//高亮显示取整
		HSSFCellStyle colorIntStyle = wb.createCellStyle();
		colorIntStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		colorIntStyle.setFillBackgroundColor(HSSFColor.YELLOW.index);
		colorIntStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		colorIntStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		colorIntStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		colorIntStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		colorIntStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		colorIntStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		colorIntStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		colorIntStyle.setRightBorderColor(HSSFColor.BLACK.index);
		colorIntStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		colorIntStyle.setTopBorderColor(HSSFColor.BLACK.index);
		styleMap.put("colorIntStyle", colorIntStyle);
		
		//高亮显示取两位位小数
		HSSFCellStyle colordecStyle = wb.createCellStyle();
		colordecStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		colordecStyle.setFillBackgroundColor(HSSFColor.YELLOW.index);
		colordecStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		colordecStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		colordecStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		colordecStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		colordecStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		colordecStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		colordecStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		colordecStyle.setRightBorderColor(HSSFColor.BLACK.index);
		colordecStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		colordecStyle.setTopBorderColor(HSSFColor.BLACK.index);
		styleMap.put("colorDecStyle", colordecStyle);
		
		//高亮显示取整百分比
		HSSFCellStyle colorPercentageInt = wb.createCellStyle();
		colorPercentageInt.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));
		colorPercentageInt.setFillBackgroundColor(HSSFColor.YELLOW.index);
		colorPercentageInt.setFillForegroundColor(HSSFColor.YELLOW.index);
		colorPercentageInt.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		colorPercentageInt.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		colorPercentageInt.setBottomBorderColor(HSSFColor.BLACK.index);
		colorPercentageInt.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		colorPercentageInt.setLeftBorderColor(HSSFColor.BLACK.index);
		colorPercentageInt.setBorderRight(HSSFCellStyle.BORDER_THIN);
		colorPercentageInt.setRightBorderColor(HSSFColor.BLACK.index);
		colorPercentageInt.setBorderTop(HSSFCellStyle.BORDER_THIN);
		colorPercentageInt.setTopBorderColor(HSSFColor.BLACK.index);
		styleMap.put("colorPercentageInt", colorPercentageInt);
		
		//高亮显示取两位位小数百分比
		HSSFCellStyle colorPercentageOne = wb.createCellStyle();
		colorPercentageOne.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
		colorPercentageOne.setFillBackgroundColor(HSSFColor.YELLOW.index);
		colorPercentageOne.setFillForegroundColor(HSSFColor.YELLOW.index);
		colorPercentageOne.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		colorPercentageOne.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		colorPercentageOne.setBottomBorderColor(HSSFColor.BLACK.index);
		colorPercentageOne.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		colorPercentageOne.setLeftBorderColor(HSSFColor.BLACK.index);
		colorPercentageOne.setBorderRight(HSSFCellStyle.BORDER_THIN);
		colorPercentageOne.setRightBorderColor(HSSFColor.BLACK.index);
		colorPercentageOne.setBorderTop(HSSFCellStyle.BORDER_THIN);
		colorPercentageOne.setTopBorderColor(HSSFColor.BLACK.index);
		styleMap.put("colorPercentageOne", colorPercentageOne);
		
		return styleMap;
	}
	
	private static float getRate(String year, String country, String line) {
		float rate = 0;
		try {
			rate = getAllProfitRate(year).get(country);
		} catch (Exception e) {}
		try {
			if (!"2016".equals(year) && !"en".equals(country)) {
				country = "de"; //非英语国家一致
			}
			rate = getLineProfitRate(year).get(country).get(line);
		} catch (Exception e) {}
		return rate;
	}
	
	/**
	 * 平台毛利率目标
	 * @return
	 */
	private static Map<String, Float> getAllProfitRate(String year){
		Map<String, Float> rateMap = Maps.newHashMap();
		if ("2016".equals(year)) {
			rateMap.put("total", 0.27f);
			rateMap.put("en", 0.31f);
			rateMap.put("de", 0.22f);
			rateMap.put("fr", 0.22f);
			rateMap.put("it", 0.19f);
			rateMap.put("es", 0.24f);
			rateMap.put("jp", 0.32f);
		} else if ("2017".equals(year)) {
			rateMap.put("total", 0.2317f);
			rateMap.put("en", 0.2506f);
			rateMap.put("de", 0.2137f);
			rateMap.put("fr", 0.2137f);
			rateMap.put("it", 0.2137f);
			rateMap.put("es", 0.2137f);
			rateMap.put("jp", 0.2137f);
		}
		return rateMap;
	}

	/**
	 * 毛利率指标
	 * @return
	 */
	public static Map<String, Map<String, Float>> getLineProfitRate(String year) {

		if ("2018".equals(year)) {
			return null;
		}
		Map<String, Map<String, Float>> rs = Maps.newHashMap();
		//英语国家
		Map<String, Float> enMap = Maps.newHashMap();
		rs.put("en", enMap);
		enMap.put("A", 0.29f);
		enMap.put("B", 0.31f);
		enMap.put("C", 0.23f);
		enMap.put("D", 0.37f);
		if ("2017".equals(year)) {
			enMap.put("A", 0.243f);
			enMap.put("B", 0.3092f);
			enMap.put("C", 0.1685f);
			enMap.put("D", 0.3278f);
			enMap.put("E", 0.225f);
			enMap.put("F", 0.1456f);
		}
		
		//德国
		Map<String, Float> deMap = Maps.newHashMap();
		rs.put("de", deMap);
		deMap.put("A", 0.21f);
		deMap.put("B", 0.23f);
		deMap.put("C", 0.23f);
		deMap.put("D", 0.21f);
		if ("2017".equals(year)) {
			deMap.put("A", 0.1847f);
			deMap.put("B", 0.2602f);
			deMap.put("C", 0.1596f);
			deMap.put("D", 0.2544f);
			deMap.put("E", 0.215f);
			deMap.put("F", 0.2458f);
		}
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
		//total
		Map<String, Float> totalMap = Maps.newHashMap();
		rs.put("total", totalMap);
		totalMap.put("A", 0.25f);
		totalMap.put("B", 0.28f);
		totalMap.put("C", 0.23f);
		totalMap.put("D", 0.32f);
		if ("2017".equals(year)) {
			totalMap.put("A", 0.2101f);
			totalMap.put("B", 0.2845f);
			totalMap.put("C", 0.1640f);
			totalMap.put("D", 0.2911f);
			totalMap.put("E", 0.22f);
			totalMap.put("F", 0.1825f);
		}
		return rs;
	}
	
	
	@RequestMapping(value = {"exportYearSales"})
	public String exportYearSales(SaleReport saleReport,HttpServletRequest request, HttpServletResponse response, Model model,String type) throws ParseException {
		DateFormat formatYear= new SimpleDateFormat("yyyy");
		HSSFWorkbook wb = new HSSFWorkbook();
		Map<String, HSSFCellStyle> formatMap=getAllStyle(wb);
		Map<String,String> lineMap=dictService.getLineNameByName();
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		
		
		Date today=new Date();
		if("0".equals(type)){
			
			Date start =DateUtils.addYears(today,-2);
			Date end =DateUtils.addYears(today,-1);
			saleReport.setStart(formatDay.parse(formatYear.format(start)+"0101"));
			saleReport.setEnd(formatDay.parse(formatYear.format(end)+"1231"));
			
		}else{
			Date start = saleReport.getStart();
			Date end = saleReport.getEnd();
			if(start==null){
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			saleReport.setStart(DateUtils.getFirstDayOfMonth(start));
			saleReport.setEnd(DateUtils.getLastDayOfMonth(end));
		}
		Map<String,Map<String, Map<String,SaleReport>>> map=saleReportService.getSalesByProductByYear(saleReport,rateMap,type);
	
			
		List<String> countryList=Lists.newArrayList("com","uk","ca","de","fr","it","es","jp","mx");
		
		if("1".equals(type)){
			  HSSFSheet sheet= wb.createSheet();
        	  HSSFRow row = sheet.createRow(0);
			  row.setHeight((short) 400);
			  HSSFCell cell = null;	
			  List<String>  title=Lists.newArrayList("型号","时间","产品线","US销量","US销售额(€)","UK销量","UK销售额(€)","CA销量","CA销售额(€)","DE销量","DE销售额(€)","FR销量","FR销售额(€)","IT销量","IT销售额(€)","ES销量","ES销售额(€)","JP销量","JP销售额(€)","MX销量","MX销售额(€)");
			  for(int i = 0; i < title.size(); i++){
					cell = row.createCell(i);
					cell.setCellStyle(formatMap.get("title"));
					cell.setCellValue(title.get(i));
					sheet.autoSizeColumn((short)i);
			  }
			  int rowIndex=1;
			  for (Map.Entry<String,Map<String,Map<String,SaleReport>>> tempMap: map.entrySet()) {
	        	  String year=tempMap.getKey();
				  Map<String, Map<String,SaleReport>> yearMap=tempMap.getValue();
				  for (Map.Entry<String, Map<String,SaleReport>> entry: yearMap.entrySet()) { 
				      String name = entry.getKey();
					  Map<String,SaleReport> nameMap=entry.getValue();
					  int j=0;
					  row = sheet.createRow(rowIndex++);
					  row.createCell(j++).setCellValue(name);
					  row.getCell(j-1).setCellStyle(formatMap.get("contentStyle"));
					  row.createCell(j++).setCellValue(year);
					  row.getCell(j-1).setCellStyle(formatMap.get("contentStyle"));
					  row.createCell(j++).setCellValue(StringUtils.isBlank(lineMap.get(name))?"":lineMap.get(name));
					  row.getCell(j-1).setCellStyle(formatMap.get("contentStyle"));
					  for (String country: countryList) {
						   if(nameMap.get(country)!=null){
							   row.createCell(j++).setCellValue(nameMap.get(country).getRealSalesVolume());
							   row.createCell(j++).setCellValue(nameMap.get(country).getRealSales());
						   }else{//decimalOneStyle
							   row.createCell(j++).setCellValue(0);
							   row.createCell(j++).setCellValue(0);
						   }
						   row.getCell(j-2).setCellStyle(formatMap.get("contentStyle"));
						   row.getCell(j-1).setCellStyle(formatMap.get("decimalOneStyle"));
					  }
				  }
				  
				  for (int i = 0; i < title.size(); i++) {
			       		 sheet.autoSizeColumn((short)i, true);
				  }
			}
		}else{
			List<String>  title=Lists.newArrayList("型号","产品线","US销量","US销售额(€)","UK销量","UK销售额(€)","CA销量","CA销售额(€)","DE销量","DE销售额(€)","FR销量","FR销售额(€)","IT销量","IT销售额(€)","ES销量","ES销售额(€)","JP销量","JP销售额(€)","MX销量","MX销售额(€)");
			for (Map.Entry<String,Map<String,Map<String,SaleReport>>> tempMap: map.entrySet()) {
	        	  String year=tempMap.getKey();
	        	  int rowIndex=1;
	        	  HSSFSheet sheet= wb.createSheet(year);
	        	  HSSFRow row = sheet.createRow(0);
				  row.setHeight((short) 400);
				  HSSFCell cell = null;		
				  for(int i = 0; i < title.size(); i++){
						cell = row.createCell(i);
						cell.setCellStyle(formatMap.get("title"));
						cell.setCellValue(title.get(i));
						sheet.autoSizeColumn((short)i);
				  }
				  Map<String, Map<String,SaleReport>> yearMap=tempMap.getValue();
				  for (Map.Entry<String, Map<String,SaleReport>> entry: yearMap.entrySet()) { 
				      String name = entry.getKey();
					  Map<String,SaleReport> nameMap=entry.getValue();
					  int j=0;
					  row = sheet.createRow(rowIndex++);
					  row.createCell(j++).setCellValue(name);
					  row.getCell(j-1).setCellStyle(formatMap.get("contentStyle"));
					  row.createCell(j++).setCellValue(StringUtils.isBlank(lineMap.get(name))?"":lineMap.get(name));
					  row.getCell(j-1).setCellStyle(formatMap.get("contentStyle"));
					  for (String country: countryList) {
						   if(nameMap.get(country)!=null){
							   row.createCell(j++).setCellValue(nameMap.get(country).getRealSalesVolume());
							   row.createCell(j++).setCellValue(nameMap.get(country).getRealSales());
						   }else{//decimalOneStyle
							   row.createCell(j++).setCellValue(0);
							   row.createCell(j++).setCellValue(0);
						   }
						   row.getCell(j-2).setCellStyle(formatMap.get("contentStyle"));
						   row.getCell(j-1).setCellStyle(formatMap.get("decimalOneStyle"));
					  }
				  }
				  
				  for (int i = 0; i < title.size(); i++) {
			       		 sheet.autoSizeColumn((short)i, true);
				  }
			}
		}
		
        
		
	
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "Sales" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	
	@RequestMapping(value = {"exportSalesByUs"})
	public String exportSalesByUs(SaleReport saleReport,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		saleReport.setStart(DateUtils.getFirstDayOfMonth(saleReport.getStart()));
		saleReport.setEnd(DateUtils.getLastDayOfMonth(saleReport.getEnd()));
		HSSFWorkbook wb = new HSSFWorkbook();
		Map<String, HSSFCellStyle> formatMap=getAllStyle(wb);
		Map<String,Object[]>  map=saleReportService.findUsSales(saleReport);
			
		List<String>  title=Lists.newArrayList("月份","阿拉巴马","阿拉斯加州","阿肯色州","特拉华州","夏威夷州","爱达荷州","爱荷华州","路易斯安那州","缅因州","密西西比州",
				"密苏里州","蒙大拿州","内布拉斯加州","新罕布什尔州","新墨西哥州","俄克拉荷马州","俄勒冈州","罗德岛州","南达科他州","犹他州",
				"佛蒙特州","怀俄明州","哥伦比亚特区","阿利桑那州","加利福尼亚州","科罗拉多州","康涅狄格州","佛罗里达州","乔治亚州","伊利诺斯州",
				"印第安纳州","堪萨斯州","肯塔基州","马里兰州","马萨诸塞州","密歇根州","明尼苏达州","内华达州","新泽西州","纽约州",
				"北卡罗来纳州","北达科他州","俄亥俄州","宾夕法尼亚州","南卡罗来纳州","田纳西州","得克萨斯州","弗吉尼亚州","华盛顿州","西弗吉尼亚州",
				"威斯康辛州");

        	  int rowIndex=1;
        	  HSSFSheet sheet= wb.createSheet();
        	  HSSFRow row = sheet.createRow(0);
			  row.setHeight((short) 400);
			  HSSFCell cell = null;		
			  for(int i = 0; i < title.size(); i++){
					cell = row.createCell(i);
					cell.setCellStyle(formatMap.get("title"));
					cell.setCellValue(title.get(i));
					sheet.autoSizeColumn((short)i);
			  }
			  for (Map.Entry<String,Object[]> entry: map.entrySet()) { 
			      String month = entry.getKey();
				  Object[] obj= entry.getValue();
				  int j=0;
				  row = sheet.createRow(rowIndex++);
				  row.createCell(j++).setCellValue(month);
				  row.getCell(j-1).setCellStyle(formatMap.get("contentStyle"));
				  
				  for(int i=1;i<=51;i++){
					  row.createCell(j++).setCellValue(obj[i]==null?0:((BigDecimal)obj[i]).floatValue());
					  row.getCell(j-1).setCellStyle(formatMap.get("decimalOneStyle"));
				  }
			  }
			  for (int i = 0; i < title.size(); i++) {
		       		 sheet.autoSizeColumn((short)i, true);
			  }
		
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "SalesByUSStates" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
}
