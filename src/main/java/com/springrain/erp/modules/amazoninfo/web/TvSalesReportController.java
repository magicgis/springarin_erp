package com.springrain.erp.modules.amazoninfo.web;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseDetail;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseGoal;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseTypeGoal;
import com.springrain.erp.modules.amazoninfo.entity.ProductPrice;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.entity.SaleReportMonthType;
import com.springrain.erp.modules.amazoninfo.service.AdvertisingService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseGoalService;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseTypeGoalService;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseWeekService;
import com.springrain.erp.modules.amazoninfo.service.ProductPriceService;
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
 */
@SuppressWarnings("all")
@Controller
@RequestMapping(value = "amazoninfo/tvSalesReprots")
public class TvSalesReportController extends BaseController {

	@Autowired
	private SaleReportService saleReportService;
	
	@Autowired
	private EnterpriseGoalService enterpriseGoalService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;

	@Autowired
	private AmazonOrderService amazonOrderService;
	
	private static DateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
	
	
	public static Timestamp lastUpdateTime;
	@RequestMapping(value = {"list", ""})
	public String list(SaleReport saleReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		String currencyType = saleReport.getCurrencyType();
		saleReport.setProductType(null);
		//得到当前月目标填报日期的前一天来获取当天汇率
		String goalDateStr = enterpriseGoalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		Map<String, Map<String, SaleReport>>  data = saleReportService.getSales(saleReport, rateMap);
		Map<String,Map<String, SaleReport>>  otherData = saleReportService.getOtherSales(saleReport, rateMap);
		
		
		model.addAttribute("data", data);
		model.addAttribute("otherData",otherData);
		List<String> xAxis  = Lists.newArrayList();
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		while(end.after(start)||end.equals(start)){
			String key = formatDay.format(start);
			xAxis.add(key);
			tip.put(key,DateUtils.getDate(start,"E"));
			start = DateUtils.addDays(start, 1);
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
		return "modules/amazoninfo/sales/amazonTvSalesReportList";
	}

	
	@RequestMapping(value = "getConnect")
	@ResponseBody
	public String getConnect(){
		/*
		Random random = new Random();
		int s = random.nextInt(10);
		if (s < 9) {
			return "0";
		}*/
		return "1";
	}
}
