package com.springrain.erp.modules.amazoninfo.web;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExcelUtil;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.MapVo;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecast;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecastDto;
import com.springrain.erp.modules.amazoninfo.service.SalesForecastService;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.scheduler.PsiConfig;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 销量预测Controller
 * @author Tim
 * @version 2015-03-03
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/salesForecast")
public class SalesForecastController extends BaseController {

	@Autowired
	private SalesForecastService salesForecastService;
	
	@Autowired
	private PsiProductAttributeService psiProductAttributeService;
	
	private static DateFormat weekFormat = new SimpleDateFormat("yyyy-ww");
	
	@Autowired
	private PsiProductService psiProductService;
	
	@ModelAttribute
	public SalesForecast get(@RequestParam(required = false) Integer id) {
		if (id != null) {
			return salesForecastService.get(id);
		} else {
			return new SalesForecast();
		}
	}
	
	
	@RequestMapping(value = {"list", ""})
	public String list(SalesForecast salesForecast, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(salesForecast.getCountry())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					salesForecast.setCountry(dict.getValue());
					break;
				}
			}
		}
		if(StringUtils.isEmpty(salesForecast.getCountry())){
			salesForecast.setCountry("total");
		}
		if(null==salesForecast.getDataDate()){
			salesForecast.setDataDate(new Date());
		}
		Date start = DateUtils.addWeeks(salesForecast.getDataDate(), -3);
		
		Date end = DateUtils.addWeeks(salesForecast.getDataDate(), 19);
		
		start = DateUtils.getFirstDayOfWeek(start.getYear()+1900, DateUtils.getWeekOfYear(start));
		end = DateUtils.getLastDayOfWeek(end.getYear()+1900, DateUtils.getWeekOfYear(end));
		List<String> dataDates = Lists.newArrayList();
		List<String> dataDates1 = Lists.newArrayList();
		for (int i = 0; i < 23; i++) {
			Date temp = DateUtils.addWeeks(start, i);
			if(temp.getYear()!=DateUtils.addDays(temp, 5).getYear()){
				dataDates.add(weekFormat.format(DateUtils.addDays(temp, 5)));
			}else{
				dataDates.add(weekFormat.format(temp));
			}
			dataDates1.add(DateUtils.getDate(temp, "M-dd")+"-"+DateUtils.getDate(DateUtils.addDays(temp, 6), "M-dd"));
		}
		model.addAttribute("dataDates",dataDates);
		model.addAttribute("dataDates1",dataDates1);
		Map<String,SalesForecastDto> data = Maps.newLinkedHashMap();;
		
		//找产品
		List<Object> pNs = salesForecastService.getProducts(salesForecast);
		for (Object object : pNs) {
			SalesForecastDto dto = new SalesForecastDto(object.toString());
			data.put(object.toString(),dto);
		}
		//找填写的数据并放进dto
		List<SalesForecast> dataWithNative = salesForecastService.find(salesForecast, start, end);
		Collections.sort(dataWithNative);
		for (SalesForecast forecast: dataWithNative) {
			Date  date = forecast.getDataDate();
			String week =weekFormat.format(date);
			Map<String, Map<String, SalesForecast>> map = data.get(forecast.getProductName()).getData();
			Map<String, SalesForecast> weekMap = map.get(forecast.getCountry());
			if(weekMap==null){
				weekMap = new HashMap<String, SalesForecast>();
				map.put(forecast.getCountry(),weekMap);
			} 
			weekMap.put(week, forecast);
		}
		model.addAttribute("data",data);
		if("total".equals(salesForecast.getCountry())){
			Map<String,Map<String,Map<String,Integer>>>  realData  = null;
			Map<String,Map<String,Integer>> ebayRealData = null;
			Map<String,Map<String,List<String>>> skusMap = salesForecastService.getProductsSkuMap();
			Set<String> skus = Sets.newHashSet();
			Set<String> ebaySkus = Sets.newHashSet();
			
			for (Object name : pNs) {
				for (Map.Entry<String, Map<String, List<String>>>  entry : skusMap.entrySet()) { 
				    String country = entry.getKey();
					if("ebay".equals(country)){
						List<String> sl = entry.getValue().get(name.toString());
						if(sl!=null){
							ebaySkus.addAll(sl);
						}
					}else{
						List<String> sl = entry.getValue().get(name.toString());
						if(sl!=null){
							skus.addAll(sl);
						}
					}
				}
			}
			//找实时数据
			if(skus.size()>0){
				realData = salesForecastService.getRealSale(start, end, skus);
			}else{
				realData = Maps.newHashMap();
			}
			if(ebaySkus.size()>0){
				ebayRealData = salesForecastService.getRealSaleByEbay(start, end, ebaySkus);
			}else{
				ebayRealData = Maps.newHashMap();
			}
			
			for (Object name : pNs) {
				for (Map.Entry<String, Map<String, List<String>>> entry : skusMap.entrySet()) { 
				    String country = entry.getKey();
					List<String> sl = entry.getValue().get(name.toString());
					if(!"ebay".equals(country)){
						if(sl!=null){
							Map<String, Map<String, Integer>>  realMap = data.get(name.toString()).getRealData();
							for (Map.Entry<String, Map<String, Map<String, Integer>>> entryRs : realData.entrySet()) { 
							    String week = entryRs.getKey();
							    Map<String, Map<String, Integer>> realRs = entryRs.getValue();
								Integer num = null;
								for (String sku : sl) {
									if(null!=realRs && null!=realRs.get(country)){
										Integer num1 = realRs.get(country).get(sku);
										if(num1!=null){
											if(num==null){
												num = num1;
											}else{
												num = num+num1;
											}
										}
									}
								}
								if(num!=null){
									Map<String, Integer> weekData = realMap.get(country);
									if(weekData==null){
										weekData = Maps.newHashMap();
										realMap.put(country, weekData);
									}
									weekData.put(week, num);
								}
							}
						}
					}else{
						if(sl!=null){
							Map<String, Map<String, Integer>>  realMap = data.get(name.toString()).getRealData();
							for (String week : realData.keySet()) {
								Integer num = null;
								for (String sku : sl) {
									Integer num1 = null;
									if(ebayRealData.get(week)!=null){
										num1 = ebayRealData.get(week).get(sku);
									}
									if(num1!=null){
										if(num==null){
											num = num1;
										}else{
											num = num+num1;
										}
									}
								}
								if(num!=null){
									Map<String, Integer> weekData = realMap.get(country);
									if(weekData==null){
										weekData = Maps.newHashMap();
										realMap.put(country, weekData);
									}
									weekData.put(week, num);
								}
							}
						}
					}
				}
			}
			//合成欧洲数据
			for (SalesForecastDto dto : data.values()) {
				Map<String, SalesForecast> euro = Maps.newHashMap();
				for (Map.Entry<String, Map<String, SalesForecast>>  entry: dto.getData().entrySet()) { 
				    String country = entry.getKey();
					if("de,fr,ebay,it,es".contains(country)){
						Map<String, SalesForecast> weekData = entry.getValue();
						for (Map.Entry<String, SalesForecast>  entryRs:weekData.entrySet()) { 
						    String week = entryRs.getKey();
							SalesForecast euroData = euro.get(week);
							if(euroData==null){
								euroData = new SalesForecast();
								euroData.setQuantityForecast(0);
								euro.put(week, euroData);
							}
							Integer temp = entryRs.getValue().getQuantityForecast();
							if(temp==null){
								temp=0;
							}
							euroData.setQuantityForecast(euroData.getQuantityForecast()+ temp);
						}
					}
				}
				Map<String,Map<String, SalesForecast>> data1 = Maps.newLinkedHashMap();
				data1.put("eu", euro);
				data1.putAll(dto.getData());
				dto.setData(data1);
				//真实数据
				Map<String, Integer> euro1 = Maps.newHashMap();
				for (String country : dto.getData().keySet()) {
					if("de,fr,ebay,it,es".contains(country)){
						Map<String, Integer> weekData = dto.getRealData().get(country);
						if(weekData!=null){
							for (Map.Entry<String, Integer>  entryRs:weekData.entrySet()) { 
							    String week = entryRs.getKey();
								Integer euroData = euro1.get(week);
								if(euroData==null){
									euro1.put(week, 0);
								}
								euro1.put(week,euro1.get(week)+entryRs.getValue());
							}
						}
					}
				}
				Map<String,Map<String, Integer>> data2 = Maps.newLinkedHashMap();
				data2.put("eu", euro1);
				data2.putAll(dto.getRealData());
				dto.setRealData(data2);
			}
			return "modules/amazoninfo/salesForecastListTotalByMonth";
		}else{
			Map<String, Map<String, Integer>>  realData  = null;
			Map<String,List<String>> skusMap = salesForecastService.getProductsSkuMap(salesForecast.getCountry());
			Set<String> skus = Sets.newHashSet();
			for (Object name : pNs) {
				List<String> sl = skusMap.get(name.toString());
				if(sl!=null){
					skus.addAll(sl);
				}
			}
			//找实时数据
			if(skus.size()>0){
				if("ebay".equals(salesForecast.getCountry())){
					realData = salesForecastService.getRealSaleByEbay(start, end, skus);
				}else{
					realData = salesForecastService.getRealSale(salesForecast.getCountry(),start, end, skus);
				}
			}else{
				realData = Maps.newHashMap();
			}
			for (Object name : pNs) {
				List<String> sl = skusMap.get(name.toString());
				if(sl!=null){
					Map<String, Map<String, Integer>>  realMap = data.get(name.toString()).getRealData();
					for (Map.Entry<String, Map<String, Integer>> entry : realData.entrySet()) { 
					    String week = entry.getKey();
					    Map<String, Integer> realRs = entry.getValue();
						Integer num = null;
						for (String sku : sl) {
							if(null!=realRs){
								Integer num1 = realRs.get(sku);
								if(num1!=null){
									if(num==null){
										num = num1;
									}else{
										num = num+num1;
									}
								}
							}
						}
						if(num!=null){
							Map<String, Integer> weekData = realMap.get(salesForecast.getCountry());
							if(weekData==null){
								weekData = Maps.newHashMap();
								realMap.put(salesForecast.getCountry(), weekData);
							}
							weekData.put(week, num);
						}
					}
				}
			}
			Set<String>products =  skusMap.keySet();
			products.removeAll(pNs);
			request.getSession().setAttribute("addProducts",products);
			return "modules/amazoninfo/salesForecastListByMonth";
		}
	}
	
	@RequestMapping(value = {"export"})
	public String export(SalesForecast salesForecast, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date start = DateUtils.addWeeks(salesForecast.getDataDate(), -3);
		
		Date end = DateUtils.addWeeks(salesForecast.getDataDate(), 19);
		
		start = DateUtils.getFirstDayOfWeek(start.getYear()+1900, DateUtils.getWeekOfYear(start));
		end = DateUtils.getLastDayOfWeek(end.getYear()+1900, DateUtils.getWeekOfYear(end));
		List<String> dataDates = Lists.newArrayList();
		List<String> dataDates1 = Lists.newArrayList();
		for (int i = 0; i < 23; i++) {
			Date temp = DateUtils.addWeeks(start, i);
			if(temp.getYear()!=DateUtils.addDays(temp, 5).getYear()){
				dataDates.add(weekFormat.format(DateUtils.addDays(temp, 5)));
			}else{
				dataDates.add(weekFormat.format(temp));
			}
			dataDates1.add(DateUtils.getDate(temp, "MM-dd"));
		}
		Map<String,SalesForecastDto> data = Maps.newLinkedHashMap();;
		
		//找产品
		List<Object> pNs = salesForecastService.getProducts(salesForecast);
		for (Object object : pNs) {
			SalesForecastDto dto = new SalesForecastDto(object.toString());
			data.put(object.toString(),dto);
		}
		//找填写的数据并放进dto
		List<SalesForecast> dataWithNative = salesForecastService.find(salesForecast, start, end);
		Collections.sort(dataWithNative);
		for (SalesForecast forecast: dataWithNative) {
			Date  date = forecast.getDataDate();
			String week = weekFormat.format(date);
			Map<String, Map<String, SalesForecast>> map = data.get(forecast.getProductName()).getData();
			Map<String, SalesForecast> weekMap = map.get(forecast.getCountry());
			if(weekMap==null){
				weekMap = new HashMap<String, SalesForecast>();
				map.put(forecast.getCountry(),weekMap);
			} 
			weekMap.put(week, forecast);
		}
		Map<String,Map<String,Map<String,Integer>>>  realData  = null;
		Map<String,Map<String,Integer>> ebayRealData = null;
		Map<String,Map<String,List<String>>> skusMap = salesForecastService.getProductsSkuMap();
		Set<String> skus = Sets.newHashSet();
		Set<String> ebaySkus = Sets.newHashSet();
		
		for (Object name : pNs) {
			for (Map.Entry<String, Map<String, List<String>>> entry: skusMap.entrySet()) { 
			    String country = entry.getKey();
				if("ebay".equals(country)){
					List<String> sl = entry.getValue().get(name.toString());
					if(sl!=null){
						ebaySkus.addAll(sl);
					}
				}else{
					List<String> sl = entry.getValue().get(name.toString());
					if(sl!=null){
						skus.addAll(sl);
					}
				}
			}
		}
		//找实时数据
		if(skus.size()>0){
			realData = salesForecastService.getRealSale(start, end, skus);
		}else{
			realData = Maps.newHashMap();
		}
		if(ebaySkus.size()>0){
			ebayRealData = salesForecastService.getRealSaleByEbay(start, end, ebaySkus);
		}else{
			ebayRealData = Maps.newHashMap();
		}
		
		for (Object name : pNs) {
			for (Map.Entry<String, Map<String, List<String>>> entry: skusMap.entrySet()) { 
			    String country = entry.getKey();
				List<String> sl = entry.getValue().get(name.toString());
				if(!"ebay".equals(country)){
					if(sl!=null){
						Map<String, Map<String, Integer>>  realMap = data.get(name.toString()).getRealData();
						for (Map.Entry<String, Map<String,Map<String,Integer>>> entryRs: realData.entrySet()) { 
						    String week = entryRs.getKey();
						    Map<String,Map<String,Integer>> real = entryRs.getValue();
							Integer num = null;
							for (String sku : sl) {
								if(null!=real && null!=real.get(country)){
									Integer num1 = real.get(country).get(sku);
									if(num1!=null){
										if(num==null){
											num = num1;
										}else{
											num = num+num1;
										}
									}
								}
							}
							if(num!=null){
								Map<String, Integer> weekData = realMap.get(country);
								if(weekData==null){
									weekData = Maps.newHashMap();
									realMap.put(country, weekData);
								}
								weekData.put(week, num);
							}
						}
					}
				}else{
					if(sl!=null){
						Map<String, Map<String, Integer>>  realMap = data.get(name.toString()).getRealData();
						for (String week : realData.keySet()) {
							Integer num = null;
							for (String sku : sl) {
								Integer num1 = null;
								if(ebayRealData.get(week)!=null){
									num1 = ebayRealData.get(week).get(sku);
								}
								if(num1!=null){
									if(num==null){
										num = num1;
									}else{
										num = num+num1;
									}
								}
							}
							if(num!=null){
								Map<String, Integer> weekData = realMap.get(country);
								if(weekData==null){
									weekData = Maps.newHashMap();
									realMap.put(country, weekData);
								}
								weekData.put(week, num);
							}
						}
					}
				}
			}
		}
		//合成欧洲数据
		for (SalesForecastDto dto : data.values()) {
			Map<String, SalesForecast> euro = Maps.newHashMap();
			for (Map.Entry<String, Map<String, SalesForecast>>  entry : dto.getData().entrySet()) { 
			    String country = entry.getKey();
				if("de,fr,ebay,it,es".contains(country)){
					Map<String, SalesForecast> weekData = entry.getValue();
					for (Map.Entry<String, SalesForecast>  entryRs : weekData.entrySet()) { 
					    String week = entryRs.getKey();
						SalesForecast euroData = euro.get(week);
						if(euroData==null){
							euroData = new SalesForecast();
							euroData.setQuantityForecast(0);
							euro.put(week, euroData);
						}
						Integer temp = entryRs.getValue().getQuantityForecast();
						if(temp==null){
							temp=0;
						}
						euroData.setQuantityForecast(euroData.getQuantityForecast()+ temp);
					}
				}
			}
			Map<String,Map<String, SalesForecast>> data1 = Maps.newLinkedHashMap();
			data1.put("eu", euro);
			data1.putAll(dto.getData());
			dto.setData(data1);
			//真实数据
			Map<String, Integer> euro1 = Maps.newHashMap();
			for (String country : dto.getData().keySet()) {
				if("de,fr,ebay,it,es".contains(country)){
					Map<String, Integer> weekData = dto.getRealData().get(country);
					if(weekData!=null){
						for (Map.Entry<String, Integer>  entryRs : weekData.entrySet()) { 
						    String week = entryRs.getKey();
							Integer euroData = euro1.get(week);
							if(euroData==null){
								euro1.put(week, 0);
							}
							euro1.put(week,euro1.get(week)+entryRs.getValue());
						}
					}
				}
			}
			Map<String,Map<String, Integer>> data2 = Maps.newLinkedHashMap();
			data2.put("eu", euro1);
			data2.putAll(dto.getRealData());
			dto.setRealData(data2);
		}
		try {
            String fileName = "销售预测"+DateUtils.getDate("yyyyMMdd")+".xlsx";
            List<String> title = Lists.newArrayList("产品名","平台");
            for (String week : dataDates) {
            	title.add(week);
			}
            ExportExcel excel = new ExportExcel("产品销售预测汇总表",title);
            Row row = excel.addRow();
            excel.addCell(row,0,"");
            excel.addCell(row,1,"");
            excel.getSheet().setColumnWidth(0, 4000);
            excel.getSheet().setColumnWidth(1, 1000);
            int i = 2;
            for (String week : dataDates1) {
            	 excel.getSheet().setColumnWidth(i, 1700);
            	 excel.addCell(row,i++,week,2,String.class);
			}
        	for (SalesForecastDto dto : data.values()) {
        		int j = 0 ;
        		for (Map.Entry<String, Map<String, SalesForecast>>  entry : dto.getData().entrySet()) { 
        		    String country = entry.getKey();
        			row = excel.addRow();
        			if(j==0){
        				excel.addCell(row,0, dto.getProductName());
        			}else{
        				excel.addCell(row,0,null);
        			}
        			int col = 1;
        			String temp = country.toUpperCase();
        			if("COM".equals(temp)){
        				temp="US";
        			}
        			excel.addCell(row,col++,temp);
        			for (String week : dataDates) {
    					Integer num = null;
    					SalesForecast forecast = entry.getValue().get(week);
    					if(forecast!=null){
    						num = forecast.getQuantityForecast();
    					}
            			if(null==num&& dto.getRealData().get(country)!=null){
            				num = dto.getRealData().get(country).get(week);
            			}
        				excel.addCell(row,col++,num,2,Integer.class);
    				}
        			j++;
				}
			}
            excel.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/salesForecast?country="+salesForecast.getCountry();
	}
	
	
	
	@RequestMapping(value = {"plan"})
	public String procurementPlan(MapVo inventorys,HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Date date = new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		model.addAttribute("date",date);
		Date start = DateUtils.addWeeks(date, -8);
		Date end = DateUtils.addWeeks(date, 19);
		start = DateUtils.getFirstDayOfWeek(start.getYear()+1900, DateUtils.getWeekOfYear(start));
		end = DateUtils.getLastDayOfWeek(end.getYear()+1900, DateUtils.getWeekOfYear(end));
		
		List<String> dataDates = Lists.newArrayList();
		List<String> dataDates1 = Lists.newArrayList();
		for (int i = 0; i < 28; i++) {
			Date temp = DateUtils.addWeeks(start, i);
			if(temp.getYear()!=DateUtils.addDays(temp, 5).getYear()){
				dataDates.add(weekFormat.format(DateUtils.addDays(temp, 5)));
			}else{
				dataDates.add(weekFormat.format(temp));
			}
			dataDates1.add(DateUtils.getDate(temp, "M-dd")+"-"+DateUtils.getDate(DateUtils.addDays(temp, 6), "M-dd"));
		}
		model.addAttribute("dataDates",dataDates);
		model.addAttribute("dataDates1",dataDates1);
		Map<String,SalesForecastDto> data = Maps.newLinkedHashMap();;
		//找产品
		SalesForecast salesForecast = new SalesForecast();
		salesForecast.setCountry("total");
		List<Object> pNs = salesForecastService.getProducts(salesForecast);
		Map<String,Map<String, Integer>> tranTime = Maps.newHashMap(); 
		model.addAttribute("tranTime",tranTime);
		Map<String, Map<String, Integer>> skyOrSea = psiProductAttributeService.findtransportType();
		for (Object object : pNs) {
			String productName = object.toString();
			SalesForecastDto dto = new SalesForecastDto(productName);
			//找交期
			PsiProduct product = psiProductService.findProductByProductName(productName);
			int produce = 0;
			if(product!=null){
				produce = product.getProducePeriod()==null?0:product.getProducePeriod();
				Map<String, Integer> map = Maps.newHashMap();
				tranTime.put(productName, map);
				for (PsiConfig config : PsiConfig.values()) {
					if("fr,it,es".contains(config.getKey())){
						continue;
					}
					String key = config.getKey();
					if("de".equals(config.getKey())){
						key = "eu";
					}
					int day = 0 ;
					if(skyOrSea.get(productName)==null || 1==skyOrSea.get(productName).get(key)){
						day = config.getTransportBySea();
					}else{
						day = config.getTransportBySky();
					}
					Float temp =(produce+day)/(float)7;
					BigDecimal bg = new BigDecimal(temp);
				    map.put(key, bg.setScale(0, BigDecimal.ROUND_UP).intValue());
				}
			}
			data.put(productName,dto);
		}
		//找填写的数据并放进dto
		List<SalesForecast> dataWithNative = salesForecastService.find(salesForecast, start, end);
		Collections.sort(dataWithNative);
		for (SalesForecast forecast: dataWithNative) {
			Date  date1 = forecast.getDataDate();
			String week =weekFormat.format(date1);
			Map<String, Map<String, SalesForecast>> map = data.get(forecast.getProductName()).getData();
			Map<String, SalesForecast> weekMap = map.get(forecast.getCountry());
			if(weekMap==null){
				weekMap = new HashMap<String, SalesForecast>();
				map.put(forecast.getCountry(),weekMap);
			} 
			weekMap.put(week, forecast);
		}
		model.addAttribute("data",data);
		model.addAttribute("inventorys",inventorys);
		
		Map<String,Map<String,Map<String,Integer>>>  realData  = null;
		Map<String,Map<String,Integer>> ebayRealData = null;
		Map<String,Map<String,List<String>>> skusMap = salesForecastService.getProductsSkuMap();
		Set<String> skus = Sets.newHashSet();
		Set<String> ebaySkus = Sets.newHashSet();
		
		for (Object name : pNs) {
			for (Map.Entry<String, Map<String, List<String>>> entryRs : skusMap.entrySet()) { 
			    String country = entryRs.getKey();
				if("ebay".equals(country)){
					List<String> sl = entryRs.getValue().get(name.toString());
					if(sl!=null){
						ebaySkus.addAll(sl);
					}
				}else{
					List<String> sl = entryRs.getValue().get(name.toString());
					if(sl!=null){
						skus.addAll(sl);
					}
				}
			}
		}
		//找实时数据
		if(skus.size()>0){
			realData = salesForecastService.getRealSale(start, end, skus);
		}else{
			realData = Maps.newHashMap();
		}
		if(ebaySkus.size()>0){
			ebayRealData = salesForecastService.getRealSaleByEbay(start, end, ebaySkus);
		}else{
			ebayRealData = Maps.newHashMap();
		}
		for (Object name : pNs) {
			for (Map.Entry<String, Map<String, List<String>>> entry : skusMap.entrySet()) { 
			    String country = entry.getKey();
				List<String> sl = entry.getValue().get(name.toString());
				if(!"ebay".equals(country)){
					if(sl!=null){
						Map<String, Map<String, Integer>>  realMap = data.get(name.toString()).getRealData();
						for (Map.Entry<String,Map<String,Map<String,Integer>>> entryRs : realData.entrySet()) { 
						    String week = entryRs.getKey();
						    Map<String,Map<String,Integer>> realRs = entryRs.getValue();
							Integer num = null;
							for (String sku : sl) {
								if(null!=realRs && null!=realRs.get(country)){
									Integer num1 = realRs.get(country).get(sku);
									if(num1!=null){
										if(num==null){
											num = num1;
										}else{
											num = num+num1;
										}
									}
								}
							}
							if(num!=null){
								Map<String, Integer> weekData = realMap.get(country);
								if(weekData==null){
									weekData = Maps.newHashMap();
									realMap.put(country, weekData);
								}
								weekData.put(week, num);
							}
						}
					}
				}else{
					if(sl!=null){
						Map<String, Map<String, Integer>>  realMap = data.get(name.toString()).getRealData();
						for (String week : realData.keySet()) {
							Integer num = null;
							for (String sku : sl) {
								Integer num1 = null;
								if(ebayRealData.get(week)!=null){
									num1 = ebayRealData.get(week).get(sku);
								}
								if(num1!=null){
									if(num==null){
										num = num1;
									}else{
										num = num+num1;
									}
								}
							}
							if(num!=null){
								Map<String, Integer> weekData = realMap.get(country);
								if(weekData==null){
									weekData = Maps.newHashMap();
									realMap.put(country, weekData);
								}
								weekData.put(week, num);
							}
						}
					}
				}
			}
		}
		//合成欧洲数据
		for (SalesForecastDto dto : data.values()) {
			Map<String, SalesForecast> euro = Maps.newHashMap();
			for (Map.Entry<String, Map<String, SalesForecast>>  entry: dto.getData().entrySet()) { 
			    String country = entry.getKey();
				if("de,fr,ebay,it,es".contains(country)){
					Map<String, SalesForecast> weekData = entry.getValue();
					for (Map.Entry<String, SalesForecast>  entryRs: weekData.entrySet()) { 
					    String week = entryRs.getKey();
						SalesForecast euroData = euro.get(week);
						if(euroData==null){
							euroData = new SalesForecast();
							euroData.setQuantityForecast(0);
							euro.put(week, euroData);
						}
						Integer temp = entryRs.getValue().getQuantityForecast();
						if(temp==null){
							temp=0;
						}
						euroData.setQuantityForecast(euroData.getQuantityForecast()+ temp);
					}
				}
			}
			Map<String,Map<String, SalesForecast>> data1 = Maps.newLinkedHashMap();
			data1.put("eu", euro);
			data1.putAll(dto.getData());
			dto.setData(data1);
			//真实数据
			Map<String, Integer> euro1 = Maps.newHashMap();
			for (String country : dto.getData().keySet()) {
				if("de,fr,ebay,it,es".contains(country)){
					Map<String, Integer> weekData = dto.getRealData().get(country);
					if(weekData!=null){
						for (Map.Entry<String, Integer>  entryRs: weekData.entrySet()) { 
						    String week = entryRs.getKey();
							Integer euroData = euro1.get(week);
							if(euroData==null){
								euro1.put(week, 0);
							}
							euro1.put(week,euro1.get(week)+entryRs.getValue());
						}
					}
				}
			}
			Map<String,Map<String, Integer>> data2 = Maps.newLinkedHashMap();
			data2.put("eu", euro1);
			data2.putAll(dto.getRealData());
			dto.setRealData(data2);
			if(inventorys.getInventorys().size()>0){
				dto.initForecastData(tranTime, inventorys,dataDates);
			}
		}
		return "modules/amazoninfo/salesForecastPlanListTotal";
	}
	
	
	@RequestMapping(value = {"plan/export"})
	public String procurementPlanExport(MapVo inventorys,HttpServletRequest request, HttpServletResponse response) {
		
		Date date = new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		Date start = DateUtils.addWeeks(date, -8);
		Date end = DateUtils.addWeeks(date, 19);
		start = DateUtils.getFirstDayOfWeek(start.getYear()+1900, DateUtils.getWeekOfYear(start));
		end = DateUtils.getLastDayOfWeek(end.getYear()+1900, DateUtils.getWeekOfYear(end));
		
		List<String> dataDates = Lists.newArrayList();
		List<String> dataDates1 = Lists.newArrayList();
		for (int i = 0; i < 28; i++) {
			Date temp = DateUtils.addWeeks(start, i);
			if(temp.getYear()!=DateUtils.addDays(temp, 5).getYear()){
				dataDates.add(weekFormat.format(DateUtils.addDays(temp, 5)));
			}else{
				dataDates.add(weekFormat.format(temp));
			}
			dataDates1.add(DateUtils.getDate(temp, "M-dd")+"-"+DateUtils.getDate(DateUtils.addDays(temp, 6), "M-dd"));
		}
		Map<String,SalesForecastDto> data = Maps.newLinkedHashMap();;
		//找产品
		SalesForecast salesForecast = new SalesForecast();
		salesForecast.setCountry("total");
		List<Object> pNs = salesForecastService.getProducts(salesForecast);
		Map<String,Map<String,Integer>> skyOrSea = psiProductAttributeService.findtransportType();
		Map<String,Map<String, Integer>> tranTime = Maps.newHashMap(); 
		for (Object object : pNs) {
			String productName = object.toString();
			SalesForecastDto dto = new SalesForecastDto(productName);
			//找交期
			PsiProduct product = psiProductService.findProductByProductName(productName);
			int produce = 0;
			if(product!=null){
				produce = product.getProducePeriod()==null?0:product.getProducePeriod();
				Map<String, Integer> map = Maps.newHashMap();
				tranTime.put(productName, map);
				for (PsiConfig config : PsiConfig.values()) {
					if("fr,it,es".contains(config.getKey())){
						continue;
					}
					String key = config.getKey();
					if("de".equals(config.getKey())){
						key = "eu";
					}
					int day = 0 ;
					if(skyOrSea.get(productName)==null || 1==skyOrSea.get(productName).get(key)){
						day = config.getTransportBySea();
					}else{
						day = config.getTransportBySky();
					}
					Float temp =(produce+day)/(float)7;
					BigDecimal bg = new BigDecimal(temp);
				    map.put(key, bg.setScale(0, BigDecimal.ROUND_UP).intValue());
				}
			}
			data.put(productName,dto);
		}
		//找填写的数据并放进dto
		List<SalesForecast> dataWithNative = salesForecastService.find(salesForecast, start, end);
		Collections.sort(dataWithNative);
		for (SalesForecast forecast: dataWithNative) {
			Date  date1 = forecast.getDataDate();
			String week =weekFormat.format(date1);
			Map<String, Map<String, SalesForecast>> map = data.get(forecast.getProductName()).getData();
			Map<String, SalesForecast> weekMap = map.get(forecast.getCountry());
			if(weekMap==null){
				weekMap = new HashMap<String, SalesForecast>();
				map.put(forecast.getCountry(),weekMap);
			} 
			weekMap.put(week, forecast);
		}
		Map<String,Map<String,Map<String,Integer>>>  realData  = null;
		Map<String,Map<String,Integer>> ebayRealData = null;
		Map<String,Map<String,List<String>>> skusMap = salesForecastService.getProductsSkuMap();
		Set<String> skus = Sets.newHashSet();
		Set<String> ebaySkus = Sets.newHashSet();
		
		for (Object name : pNs) {
			for (Map.Entry<String, Map<String, List<String>>> entryRs : skusMap.entrySet()) { 
			    String country = entryRs.getKey();
				if("ebay".equals(country)){
					List<String> sl = entryRs.getValue().get(name.toString());
					if(sl!=null){
						ebaySkus.addAll(sl);
					}
				}else{
					List<String> sl = entryRs.getValue().get(name.toString());
					if(sl!=null){
						skus.addAll(sl);
					}
				}
			}
		}
		//找实时数据
		if(skus.size()>0){
			realData = salesForecastService.getRealSale(start, end, skus);
		}else{
			realData = Maps.newHashMap();
		}
		if(ebaySkus.size()>0){
			ebayRealData = salesForecastService.getRealSaleByEbay(start, end, ebaySkus);
		}else{
			ebayRealData = Maps.newHashMap();
		}
		for (Object name : pNs) {
			for (Map.Entry<String, Map<String, List<String>>> entry: skusMap.entrySet()) { 
		        String country = entry.getKey();
				List<String> sl = entry.getValue().get(name.toString());
				if(!"ebay".equals(country)){
					if(sl!=null){
						Map<String, Map<String, Integer>>  realMap = data.get(name.toString()).getRealData();
						for (Map.Entry<String, Map<String, Map<String, Integer>>>  entryRs: realData.entrySet()) { 
						    String week = entryRs.getKey();
						    Map<String, Map<String, Integer>> realRs = entryRs.getValue();
							Integer num = null;
							for (String sku : sl) {
								if(null!=realRs && null!=realRs.get(country)){
									Integer num1 = realRs.get(country).get(sku);
									if(num1!=null){
										if(num==null){
											num = num1;
										}else{
											num = num+num1;
										}
									}
								}
							}
							if(num!=null){
								Map<String, Integer> weekData = realMap.get(country);
								if(weekData==null){
									weekData = Maps.newHashMap();
									realMap.put(country, weekData);
								}
								weekData.put(week, num);
							}
						}
					}
				}else{
					if(sl!=null){
						Map<String, Map<String, Integer>>  realMap = data.get(name.toString()).getRealData();
						for (String week : realData.keySet()) {
							Integer num = null;
							for (String sku : sl) {
								Integer num1 = null;
								if(ebayRealData.get(week)!=null){
									num1 = ebayRealData.get(week).get(sku);
								}
								if(num1!=null){
									if(num==null){
										num = num1;
									}else{
										num = num+num1;
									}
								}
							}
							if(num!=null){
								Map<String, Integer> weekData = realMap.get(country);
								if(weekData==null){
									weekData = Maps.newHashMap();
									realMap.put(country, weekData);
								}
								weekData.put(week, num);
							}
						}
					}
				}
			}
		}
		//合成欧洲数据
		for (SalesForecastDto dto : data.values()) {
			Map<String, SalesForecast> euro = Maps.newHashMap();
			for (Map.Entry<String, Map<String, SalesForecast>> entry :  dto.getData().entrySet()) { 
			    String country = entry.getKey();
				if("de,fr,ebay,it,es".contains(country)){
					Map<String, SalesForecast> weekData = entry.getValue();
					for (Map.Entry<String,SalesForecast> entryRs : weekData.entrySet()) { 
					    String week = entryRs.getKey();
						SalesForecast euroData = euro.get(week);
						if(euroData==null){
							euroData = new SalesForecast();
							euroData.setQuantityForecast(0);
							euro.put(week, euroData);
						}
						Integer temp = entryRs.getValue().getQuantityForecast();
						if(temp==null){
							temp=0;
						}
						euroData.setQuantityForecast(euroData.getQuantityForecast()+ temp);
					}
				}
			}
			Map<String,Map<String, SalesForecast>> data1 = Maps.newLinkedHashMap();
			data1.put("eu", euro);
			data1.putAll(dto.getData());
			dto.setData(data1);
			//真实数据
			Map<String, Integer> euro1 = Maps.newHashMap();
			for (String country : dto.getData().keySet()) {
				if("de,fr,ebay,it,es".contains(country)){
					Map<String, Integer> weekData = dto.getRealData().get(country);
					if(weekData!=null){
						for (Map.Entry<String,Integer> entry: weekData.entrySet()) { 
					        String week = entry.getKey();
							Integer euroData = euro1.get(week);
							if(euroData==null){
								euro1.put(week, 0);
							}
							euro1.put(week,euro1.get(week)+entry.getValue());
						}
					}
				}
			}
			Map<String,Map<String, Integer>> data2 = Maps.newLinkedHashMap();
			data2.put("eu", euro1);
			data2.putAll(dto.getRealData());
			dto.setRealData(data2);
			if(inventorys.getInventorys().size()>0){
				dto.initForecastData(tranTime, inventorys,dataDates);
			}
		}
		
		///-----------
		try {
            String fileName = "采购计划"+DateUtils.getDate("yyyyMMdd")+".xlsx";
            List<String> title = Lists.newArrayList("产品名","平台");
            for (String week : dataDates) {
            	title.add(week);
			}
            ExportExcel excel = new ExportExcel("",
            		Lists.newArrayList("产品信息","","","","下单信息","","","","","","","","实际销售",
            				"","","","","","","","预测销量","","","","","","","","","","","","","","","","","","",""));
            Row row = excel.addRow();
            CellStyle  style = excel.getWb().createCellStyle();
    		style.setFillForegroundColor(IndexedColors.GOLD.getIndex());
    		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            excel.addCell(row,0,"产品名").setCellStyle(style);
            excel.addCell(row,1,"平台").setCellStyle(style);
            excel.addCell(row,2,"交期").setCellStyle(style);
            excel.addCell(row,3,"库存").setCellStyle(style);
            excel.getSheet().setColumnWidth(0, 4000);
            excel.getSheet().setColumnWidth(1, 1000);
            excel.getSheet().setColumnWidth(2, 1000);
            excel.getSheet().setColumnWidth(3, 1200);
            int i = 4;
            for (int j = 0; j < 8; j++) {
            	excel.getSheet().setColumnWidth(i, 1700);
           	 	excel.addCell(row,i++,dataDates.get(j+8),2,String.class).setCellStyle(style);
			}
            for (String week : dataDates) {
            	excel.getSheet().setColumnWidth(i, 1700);
           	 	excel.addCell(row,i++,week,2,String.class).setCellStyle(style);
			}
            List<SalesForecastDto> collect =  Lists.newArrayList();
            collect.addAll(data.values());
            Collections.sort(collect,new Comparator<SalesForecastDto>() {
            	public int compare(SalesForecastDto o1,SalesForecastDto o2) {
            		return o1.getProductName().compareToIgnoreCase(o2.getProductName());
            	};
			});
            List<String> list = Lists.newArrayList("eu","de","fr","it","es","uk","com","ca","jp");
        	for (SalesForecastDto dto : collect) {
        		int j = 0 ;
        		for (String country:list) {
        			row = excel.addRow();
        			if(j==0){
        				excel.addCell(row,0, dto.getProductName());
        			}else{
        				excel.addCell(row,0,dto.getProductName());
        			}
        			int col = 1;
        			String temp = country.toUpperCase();
        			if("COM".equals(temp)){
        				temp="US";
        			}
        			String name = dto.getProductName();
        			excel.addCell(row,col++,temp);
        			excel.addCell(row,col++,tranTime.get(name).get(country));
        			if(inventorys.getInventorys().get(name)!=null){
        				excel.addCell(row,col++,inventorys.getInventorys().get(name).get(country),2,Integer.class);
        			}else{
        				excel.addCell(row,col++,null);
        			}
        			for (int k = 0; k < 8; k++) {
        				if(dto.getForecastData().get(country)!=null){
        					excel.addCell(row,col++,dto.getForecastData().get(country).get(dataDates.get(k+8)),2,Integer.class);
        				}else{
            				excel.addCell(row,col++,null);
            			}
        			}
        			int ii = 0 ;
        			for (String week : dataDates) {
    					Integer num = null;
    					Map<String, SalesForecast>  tempMap = dto.getData().get(country);
    					if(tempMap!=null){
	    					SalesForecast forecast = dto.getData().get(country).get(week);
	    					if(ii<8){
	    						if(null==num&& dto.getRealData().get(country)!=null){
		            				num = dto.getRealData().get(country).get(week);
		            			}
	    						ii++;
	    					}else{
	    						if(forecast!=null){
		    						num = forecast.getQuantityForecast();
		    					}
	    					}
    					}
        				excel.addCell(row,col++,num,2,Integer.class);
    				}
        			row.getCell(4).setCellStyle(style);
        			row.getCell(12).setCellStyle(style);
        			row.getCell(20).setCellStyle(style);
        			j++;
				}
			}
            excel.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/salesForecastPlanListTotal/?repage";
	}
	
	
	@RequestMapping(value = "addProduct")
	public String addProduct(SalesForecast salesForecast, Model model) {
		model.addAttribute("salesForecast", salesForecast);
		return "modules/amazoninfo/saleForecastProductAdd";
	}
	
	@RequestMapping(value ="saveForecast")
	@ResponseBody
	public String save(@RequestParam(required=false)String week,SalesForecast salesForecast, Model model, RedirectAttributes redirectAttributes) {
		if(salesForecast.getId()==null){
			salesForecast.setCreateBy(UserUtils.getUser());
			String[] temp = week.split("-");
			salesForecast.setDataDate(DateUtils.getFirstDayOfWeek(Integer.parseInt(temp[0]),Integer.parseInt(temp[1])));
		}
		salesForecast.setLastUpdateDate(new Date());
		salesForecast.setLastUpdateBy(UserUtils.getUser());
		salesForecastService.save(salesForecast);
		return salesForecast.getId()+"";
	}

	@RequestMapping(value = "save")
	public String save(@RequestParam(value="productsName[]")String[] productsName,String country,Model model, RedirectAttributes redirectAttributes) {
		List<SalesForecast> list = Lists.newArrayList();
		User user = UserUtils.getUser();
		Date date = new Date();
		Date dataDate = DateUtils.getFirstDayOfWeek(date.getYear()+1900, DateUtils.getWeekOfYear(date));
		for (String product : productsName) {
			list.add(new SalesForecast(country,user,dataDate,date,user,product,null));
		}
		salesForecastService.save(list);
		addMessage(redirectAttributes, "新增销售预测产品成功!");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/salesForecast?country="+country;
	}
	
	@RequestMapping(value = "delete")
	public String delete(String product,String country,RedirectAttributes redirectAttributes) {
		salesForecastService.delete(country,product);
		addMessage(redirectAttributes, "删除销量预测产品成功!");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/salesForecast/?country="+country;
	}
	
	@RequestMapping(value = "saleReport")
	public String saleReport(String productName,String country,String id,Model model) {
		List<List<Object>> rs = salesForecastService.getProductSales(productName, country);
		model.addAttribute("data", rs.get(1));
		model.addAttribute("productName",productName);
		model.addAttribute("country",country);
		model.addAttribute("id",id);
		model.addAttribute("xAxis",rs.get(0));
		return "modules/amazoninfo/productSaleReport";
	}
	
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response,String country,RedirectAttributes redirectAttributes) {
		try {
            String fileName = "销售预测数据模板"+DateUtils.getDate("yyyyMMdd")+".xlsx";
            Date today = new Date();
    		Date start = today;
    		Date end = DateUtils.addWeeks(start, 19);
    		start = DateUtils.getFirstDayOfWeek(start.getYear()+1900, DateUtils.getWeekOfYear(start));
    		end = DateUtils.getLastDayOfWeek(end.getYear()+1900, DateUtils.getWeekOfYear(end));
    		List<String> dataDates = Lists.newArrayList();
    		for (int i = 0; i < 20; i++) {
    			Date temp = DateUtils.addWeeks(start, i);
    			if(temp.getYear()!=DateUtils.addDays(temp, 5).getYear()){
    				dataDates.add(weekFormat.format(DateUtils.addDays(temp, 5)));
    			}else{
    				dataDates.add(weekFormat.format(temp));
    			}
    		}
    		dataDates.add(0, "productName");
    		SalesForecast salesForecast = new SalesForecast();
    		salesForecast.setCountry(country);
    		List<SalesForecast> list = salesForecastService.find(salesForecast, start, end);
    		Map<String,Map<String,SalesForecast>> rs = Maps.newLinkedHashMap();
    		for (SalesForecast forecast : list) {
				String name = forecast.getProductName();
				Map<String,SalesForecast> temp = rs.get(name);
    			if(temp==null){
    				temp = Maps.newHashMap();
    				rs.put(name, temp);
    			}
    			Date temp1 = forecast.getDataDate();
    			String key = "";
    			if(temp1.getYear()!=DateUtils.addDays(temp1, 5).getYear()){
    				key = weekFormat.format(DateUtils.addDays(temp1, 5));
    			}else{
    				key = weekFormat.format(temp1);
    			}
    			temp.put(key, forecast);
			}
    		ExportExcel excel = new ExportExcel(null,dataDates);
    		for (Map.Entry<String, Map<String, SalesForecast>>  entry: rs.entrySet()) { 
    		    String name = entry.getKey();
    		    Map<String, SalesForecast> rsMap = entry.getValue();
    			Row row  = excel.addRow();
    			excel.addCell(row, 0, name);
    			int i = 0 ;
    			for (String date : dataDates) {
					if(i==0){
						i++;
						continue;
					}
					if( rsMap.get(date)!=null){
						excel.addCell(row, i,rsMap.get(date).getQuantityForecast());
					}else{
						excel.addCell(row, i, null);
					}
					i++;
				}
			}
            excel.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/salesForecast/?country="+country;
    }

    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file,String country, RedirectAttributes redirectAttributes) {
		try {
			List<String[]> datas = ExcelUtil.read(file.getInputStream());
			Date today = new Date();
    		Date start = today;
    		start = DateUtils.getFirstDayOfWeek(start.getYear()+1900, DateUtils.getWeekOfYear(start));
    		int ii = 0 ;
    		StringBuilder buf=new StringBuilder();
			for (String[] row : datas) {
				if(ii==0){
					ii++;
					continue;
				}
				String productName = row[0];
				if(salesForecastService.deleteByStartDate(country, productName, start)>0 || salesForecastService.findProducExsit(country, productName)){
					List<SalesForecast> list = Lists.newArrayList();
					for (int i = 1; i < row.length; i++) {
						String data = row[i];
						if(data!=null){
							String numberStr = data.replace(".0","");
							Integer number = null;
							if(numberStr!=null&&numberStr.trim().length()>0){
								number = Integer.parseInt(numberStr);
							}
							list.add(new SalesForecast(country, UserUtils.getUser(), DateUtils.addWeeks(start, i-1), new Date(), UserUtils.getUser(), productName,number ));
						}
					}
					salesForecastService.save(list);
				}else{
					if(productName!=null&&productName.length()>0){
						buf.append(productName+"产品不存在");
					}
				}
			}
			addMessage(redirectAttributes, "已成功导入数据!!"+buf.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			addMessage(redirectAttributes, "导入数据失败!!信息:"+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/salesForecast/?country="+country;
    }
}
