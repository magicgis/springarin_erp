package com.springrain.erp.modules.amazoninfo.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecastByMonth;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.SalesForecastServiceByMonth;
import com.springrain.erp.modules.psi.entity.ProductSalesInfo;
import com.springrain.erp.modules.psi.entity.PsiInventoryWarn;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiProductInStock;
import com.springrain.erp.modules.psi.service.ProductSalesInfoService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductInStockService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 销量预测Controller
 * @author Tim
 * @version 2015-03-03
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/salesForecastByMonth")
public class SalesForecastControllerByMonth extends BaseController {
	@Autowired
	private SalesForecastServiceByMonth salesForecastService;
	
	private static DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
	
	@Autowired
	private PsiProductService psiProductService;
	@Autowired
	private PsiProductAttributeService psiProductAttributeService;
	@Autowired
	private PsiProductInStockService psiProductInStockService;
	
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;

	@Autowired
	private PsiProductAttributeService productAttributeService;
	
	@Autowired
	private PsiProductTypeGroupDictService groupDictService;
	
	@Autowired
	private ProductSalesInfoService salesInfoService;
	@Autowired
	private PsiProductEliminateService 		productEliminateService;
	@Autowired
	private SaleReportService      saleReportService;
	
	
	@ModelAttribute
	public SalesForecastByMonth get(@RequestParam(required = false) Integer id) {
		if (id != null) {
			return salesForecastService.get(id);
		} else {
			return new SalesForecastByMonth();
		}
	}
	
	
	@RequestMapping(value = {"list", ""})
	public String list(SalesForecastByMonth salesForecast, HttpServletRequest request, HttpServletResponse response, Model model) {
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
		Date today = DateUtils.getLastDayOfMonth(DateUtils.getDateStart(new Date()));
		salesForecast.setDataDate(today);
		Date start = DateUtils.addMonths(salesForecast.getDataDate(), -3);
		Date end = DateUtils.addMonths(salesForecast.getDataDate(), 6);
		String country = salesForecast.getCountry();
		model.addAttribute("salesForecast",salesForecast);
		
		if("total".equals(country)){
			//产品 [国家[月  数]]
			Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAllWithType(start,end);
			model.addAttribute("data",data);
			Page<Object[]> page = new Page<Object[]>(request,response);
			page=psiProductService.findOrderBySaleProduct(page,salesForecast.getProductName());
			model.addAttribute("page",page);
			Map<String,Map<String,Map<String,Integer>>> saleData = salesForecastService.getRealSale(start, end);
			model.addAttribute("saleData",saleData);
			List<String> dates = Lists.newArrayList();
			List<String> dates1 = Lists.newArrayList();
			for (int i = 0; i < 9; i++) {
				if(i<3){
					dates.add(monthFormat.format(DateUtils.addMonths(start,i)));
				}else{
					dates1.add(monthFormat.format(DateUtils.addMonths(start,i)));
				}
			}
			model.addAttribute("countrys",Lists.newArrayList("total","de","fr","it","es","eu","uk","com","jp","ca","mx"));
			model.addAttribute("dates",dates);
			model.addAttribute("dates1",dates1);
			//model.addAttribute("hasPower", psiProductService.getHasPower());
			model.addAttribute("hasPower", productEliminateService.findProductFanOuFlag());
			return "modules/amazoninfo/salesForecastTotalList";
		}else{
			//产品 [月  数]
			Map<String, Map<String, SalesForecastByMonth>>  data = salesForecastService.findByCountryType(country,start,end);
			model.addAttribute("data",data);
			List<PsiProductEliminate> list = psiProductEliminateService.findOnSaleNotNew(country);
			model.addAttribute("products",list);
			Map<String, Map<String, Integer>> saleData = salesForecastService.getRealSale(country, start, end);
			model.addAttribute("saleData",saleData);
			if ("de".equals(country)) {	//德国有泛欧产品,预测的数据包含其他欧洲国家
				Map<String,Map<String,Map<String,Integer>>> totalSaleData = salesForecastService.getRealSale(start, end);
				model.addAttribute("totalSaleData",totalSaleData);
			}
			List<String> dates = Lists.newArrayList();
			List<String> dates1 = Lists.newArrayList();
			for (int i = 0; i < 9; i++) {
				if(i<3){
					dates.add(monthFormat.format(DateUtils.addMonths(start,i)));
				}else{
					dates1.add(monthFormat.format(DateUtils.addMonths(start,i)));
				}
			}
			model.addAttribute("dates",dates);
			model.addAttribute("dates1",dates1);
			model.addAttribute("hasPower", productEliminateService.findProductFanOuFlag());
			return "modules/amazoninfo/salesForecastListByMonth";
		}
	}
	
	@RequestMapping(value ="save")
	@ResponseBody
	public String save(@RequestParam(required=false)String month,SalesForecastByMonth salesForecast, Model model, RedirectAttributes redirectAttributes) {
		if(salesForecast.getId()==null){
			salesForecast.setCreateBy(UserUtils.getUser());
			String[] temp = month.split("-");
			try {
				salesForecast.setDataDate(DateUtils.getLastDayOfMonth(Integer.parseInt(temp[0]),Integer.parseInt(temp[1])-1));
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		salesForecast.setLastUpdateDate(new Date());
		salesForecast.setLastUpdateBy(UserUtils.getUser());
		salesForecastService.save(salesForecast);
		return salesForecast.getId()+"";
	}
	
	@RequestMapping(value ="exportSalesForecastByMonth2")
	public String exportSalesForecastByMonth2(SalesForecastByMonth salesForecast, HttpServletRequest request, HttpServletResponse response){
		Date today = DateUtils.getLastDayOfMonth(DateUtils.getDateStart(new Date()));
		salesForecast.setDataDate(today);
		Date start = DateUtils.addMonths(salesForecast.getDataDate(), -6);
		Date end = DateUtils.addMonths(salesForecast.getDataDate(), 12);
		Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAllWithType(start,end);
		Page<PsiProduct> page = new Page<PsiProduct>(request,response);
		page.setPageSize(800);
		page = psiProductService.findAllOnSaleNotNewByPage(page,"",salesForecast.getProductName());
		Map<String,Map<String,Map<String,Integer>>> saleData = salesForecastService.getRealSale(start, end);
		Map<String,Integer> maxQuantity=psiProductAttributeService.getAllMaxInventory();
		Map<String,Map<String,PsiInventoryWarn>> safeInventory=psiProductInStockService.getSafeInventory();
		List<String> dates = Lists.newArrayList();
		List<String> dates1 = Lists.newArrayList();
		List<String> dates2 = Lists.newArrayList();
		for (int i = 0; i <18; i++) {
			if(i<6){
				dates.add(monthFormat.format(DateUtils.addMonths(start,i)));
			}else{
				dates1.add(monthFormat.format(DateUtils.addMonths(start,i)));
				if(i>6&&i<10){
					dates2.add(monthFormat.format(DateUtils.addMonths(start,i)));
				}
			}
		}
		List<String> countrys=Lists.newArrayList("de","fr","it","es","uk","com","ca","jp");
		 
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
        cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setTopBorderColor(HSSFColor.BLACK.index);
        HSSFFont font1=wb.createFont();
        font1.setColor(HSSFColor.RED.index);//HSSFColor.VIOLET.index //字体颜色
        cellStyle1.setFont(font1);
		
		//高亮显示
		HSSFCellStyle colorStyle = wb.createCellStyle();
		colorStyle.setFillBackgroundColor(HSSFColor.YELLOW.index);
		colorStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		colorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		//colorStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		colorStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		colorStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		colorStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		colorStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		colorStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		colorStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		colorStyle.setRightBorderColor(HSSFColor.BLACK.index);
		colorStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		colorStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCell cell = null;
	    List<String> title=Lists.newArrayList("产品名称","MOQ","MPQ","L/T","普通类别","平台");
	    for(String time:dates){
	    	title.add(time);
	    }
	    for(String time1:dates1){
	    	title.add(time1);
	    }
	    title.add("库存上限");
	    title.add("分国家单独缺口");
	    title.add("累计总缺口");
	    title.add("总体积");
	    title.add("总库存");
	    title.add("安全库存");
	    
	    for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
		}
	    int num=1;
	    Map<String, String> isNewMap = psiProductEliminateService.findIsNewMap();
	    Map<String, String> productPositionMap = psiProductEliminateService.findProductPositionByCountry(null);
	    //符合预测条件的产品集合
	    List<String> nameList = psiProductEliminateService.findForecastName();
	    Map<String, String> findProductFanOuFlag = saleReportService.findProductFanOuFlag();
	    
	    for(PsiProduct product:page.getList()){
	    	for(String name:product.getProductNameWithColor()){
	    		if (!nameList.contains(name)) {
					continue;
				}
	    		int tempTotal=0;
	    		int inventoryTotal = 0;//总库存
	    		int safeInventoryTotal = 0;//总安全库存
	    		for(String country:countrys){
	    		    String fanOu = findProductFanOuFlag.get(name);
	    			if(product.getPlatform().contains(country)){
	    				if ("0".equals(fanOu) && "fr,it,es,uk".contains(country)) {
							continue;//泛欧产品欧洲平台统一汇总到德国
						}
    				   if ("1".equals(fanOu) && "fr,it,es".contains(country)) {
                            continue;//部分泛欧产品欧洲平台统一汇总到德国
                        }
	    				String key = name + "_" + country;
	    				 int j=0;
	    				 row = sheet.createRow(num++);
    					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
    					 row.getCell(j-1).setCellStyle(contentStyle);
	    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getMinOrderPlaced());	
	    				 row.getCell(j-1).setCellStyle(contentStyle);
	    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getPackQuantity());
	    				 row.getCell(j-1).setCellStyle(contentStyle);
	    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getProducePeriod());
	    				 row.getCell(j-1).setCellStyle(contentStyle);
	    				 
	    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("0".equals(isNewMap.get(key))?"普通":"新品");
	    				 row.getCell(j-1).setCellStyle(contentStyle);
	    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
	    				 row.getCell(j-1).setCellStyle(contentStyle);
	    				 for(String time:dates){
	    					 int forecast=0;
	    					 int realData=0;
	    					 if ("0".equals(fanOu) && "de".equals(country)) {	//泛欧产品欧洲销量统计计算到德国
								try {
									realData+=saleData.get(name).get("uk").get(time);
								} catch (Exception e) {}
								try {
									realData+=saleData.get(name).get("eu").get(time);
								} catch (Exception e) {}
	    					 }if ("1".equals(fanOu) && "de".equals(country)) {    //泛欧产品欧洲销量统计计算到德国
                                try {
                                    realData+=saleData.get(name).get("eu").get(time);
                                } catch (Exception e) {}
                             } else {
    	    					 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(time)!=null&&data.get(name).get(country).get(time).getQuantityForecast()!=null){
    	    						 forecast=data.get(name).get(country).get(time).getQuantityForecast();
    							 }
    	    					 if(saleData!=null&&saleData.get(name)!=null&&saleData.get(name).get(country)!=null&&saleData.get(name).get(country).get(time)!=null){
    	    						 realData=saleData.get(name).get(country).get(time);
    	    					 }
	    					 }
	    					 if(realData > 0){
	    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(realData);
	    					 }else{
	    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(forecast);
	    					 }
	    					 row.getCell(j-1).setCellStyle(contentStyle);
	    				 }
	    				 for(String time:dates1){
	    					 int forecast=0;
	    					 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(time)!=null&&data.get(name).get(country).get(time).getQuantityForecast()!=null){
	    						 forecast=data.get(name).get(country).get(time).getQuantityForecast();
	    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(forecast);
							 }else{
	    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
	    					 }
	    					 row.getCell(j-1).setCellStyle(contentStyle);
	    					
	    				 }
	    				 //库存上限
	    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(maxQuantity==null?0:(maxQuantity.get(name)==null?0:maxQuantity.get(name)));
	    				 row.getCell(j-1).setCellStyle(contentStyle);
	    				 
	    				 int breach=0;
	    				 PsiInventoryWarn obj=null;
	    				 if(safeInventory!=null&&safeInventory.get(country)!=null&&safeInventory.get(country).get(name)!=null){
	    					 obj=safeInventory.get(country).get(name);
	    				 }
	    				 if(obj!=null){
    						 if( "0".equals(isNewMap.get(key))){
		    					 int forecast=0;
		    					 for(String time:dates2){
			    					 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(time)!=null&&data.get(name).get(country).get(time).getQuantityForecast()!=null){
			    						 forecast+=data.get(name).get(country).get(time).getQuantityForecast();
									 }
			    				 }
		    					 int curForecast=0;
		    					 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(monthFormat.format(today))!=null&&data.get(name).get(country).get(monthFormat.format(today)).getQuantityForecast()!=null){
		    						 curForecast=data.get(name).get(country).get(monthFormat.format(today)).getQuantityForecast();
								 }
		    					 float day=(DateUtils.spaceDays(new Date(), DateUtils.getLastDayOfMonth(new Date()))+1)*1f/DateUtils.getDayOfMonth()*curForecast;
		    					 breach=Math.round(obj.getTotal()-obj.getSafeSales()-forecast-day);
		    				 }else{
		    					 int forecast=0;
		    					 for(String time:dates2){
			    					 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(time)!=null&&data.get(name).get(country).get(time).getQuantityForecast()!=null){
			    						 forecast+=data.get(name).get(country).get(time).getQuantityForecast();
									 }
			    				 }
		    					 int curForecast=0;
		    					 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(monthFormat.format(today))!=null&&data.get(name).get(country).get(monthFormat.format(today)).getQuantityForecast()!=null){
		    						 curForecast+=data.get(name).get(country).get(monthFormat.format(today)).getQuantityForecast();
								 }
		    					 float day=(DateUtils.spaceDays(new Date(), DateUtils.getLastDayOfMonth(new Date()))+1)*1f/DateUtils.getDayOfMonth()*curForecast;
		    					 breach=Math.round(obj.getTotal()-forecast-day);
		    				 }
	    				 }
	    				 int inventory = 0;
	    				 int safeSalses = 0;
	    				 if (obj != null) {
	    					 inventory = obj.getTotal();
	    					 safeSalses = obj.getSafeSales();
						 }
	    				 if ("0".equals(fanOu) && "de".equals(country)) {	//泛欧产品统计到德国平台
		    				 PsiInventoryWarn fr = safeInventory.get("fr").get(name);
		    				 if (fr != null) {
		    					 inventory += fr.getTotal();
		    					 safeSalses += fr.getSafeSales();
	    						 if( "0".equals(isNewMap.get(key))){
			    					 breach+=(fr.getTotal()-fr.getSafeSales());
			    				 }else{
			    					 breach+=(fr.getTotal());
			    				 }
							 }
		    				 PsiInventoryWarn it = safeInventory.get("it").get(name);
		    				 if (it != null) {
		    					 inventory += it.getTotal();
		    					 safeSalses += it.getSafeSales();
	    						 if( "0".equals(isNewMap.get(key))){
			    					 breach+=(it.getTotal()-it.getSafeSales());
			    				 }else{
			    					 breach+=it.getTotal();
			    				 }
							 }
		    				 PsiInventoryWarn es = safeInventory.get("es").get(name);
		    				 if (es != null) {
		    					 inventory += es.getTotal();
		    					 safeSalses += es.getSafeSales();
	    						 if( "0".equals(isNewMap.get(key))){
			    					 breach+=(es.getTotal()-es.getSafeSales());
			    				 }else{
			    					 breach+=es.getTotal();
			    				 }
							 }
		    				 PsiInventoryWarn uk = safeInventory.get("uk").get(name);
		    				 if (uk != null) {
		    					 inventory += uk.getTotal();
		    					 safeSalses += uk.getSafeSales();
	    						 if( "0".equals(isNewMap.get(key))){
			    					 breach+=(uk.getTotal()-uk.getSafeSales());
			    				 }else{
			    					 breach+=uk.getTotal().intValue();
			    				 }
							 }
						 }
	    				 
	    				 if ("1".equals(fanOu) && "de".equals(country)) {  //部分泛欧产品统计到德国平台
                             PsiInventoryWarn fr = safeInventory.get("fr").get(name);
                             if (fr != null) {
                                 inventory += fr.getTotal();
                                 safeSalses += fr.getSafeSales();
                                 if( "0".equals(isNewMap.get(key))){
                                     breach+=(fr.getTotal()-fr.getSafeSales());
                                 }else{
                                     breach+=(fr.getTotal());
                                 }
                             }
                             PsiInventoryWarn it = safeInventory.get("it").get(name);
                             if (it != null) {
                                 inventory += it.getTotal();
                                 safeSalses += it.getSafeSales();
                                 if( "0".equals(isNewMap.get(key))){
                                     breach+=(it.getTotal()-it.getSafeSales());
                                 }else{
                                     breach+=it.getTotal();
                                 }
                             }
                             PsiInventoryWarn es = safeInventory.get("es").get(name);
                             if (es != null) {
                                 inventory += es.getTotal();
                                 safeSalses += es.getSafeSales();
                                 if( "0".equals(isNewMap.get(key))){
                                     breach+=(es.getTotal()-es.getSafeSales());
                                 }else{
                                     breach+=es.getTotal();
                                 }
                             }
	    				 }
	    				 //分国家单独缺口
	    				 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(breach);
	    				 if(breach>0){
	    					 row.getCell(j-1).setCellStyle(contentStyle);
	    				 }else{
	    					 row.getCell(j-1).setCellStyle(cellStyle1);
	    				 }
	    				 //累计总缺口
	    				 tempTotal=tempTotal+breach;
	    				 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(tempTotal);
	    				 if(tempTotal>0){
	    					 row.getCell(j-1).setCellStyle(contentStyle);
	    				 }else{
	    					 row.getCell(j-1).setCellStyle(cellStyle1);
	    				 }
	    				 //总体积
	    				 if(breach<0){
	    					 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.abs(breach*product.getTranVolume()));
		    				 row.getCell(j-1).setCellStyle(cellStyle);
	    				 }else{
	    					 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
		    				 row.getCell(j-1).setCellStyle(contentStyle);
	    				 }
	    				 //总库存&安全库存
	    				 inventoryTotal += inventory;
	    				 safeInventoryTotal += safeSalses;
    					 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventory);
	    				 row.getCell(j-1).setCellStyle(contentStyle);
	    				 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(safeSalses);
	    				 row.getCell(j-1).setCellStyle(contentStyle);
	    			}
	    			
	    		}
	    		//单产品增加总计行
    			if(!"4".equals(productPositionMap.get(name))){
    				String country = "total";
    				 int j=0;
    				 row = sheet.createRow(num++);
					 
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
					 row.getCell(j-1).setCellStyle(colorStyle);
    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");	
    				 row.getCell(j-1).setCellStyle(colorStyle);
    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
    				 row.getCell(j-1).setCellStyle(colorStyle);
    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
    				 row.getCell(j-1).setCellStyle(colorStyle);
    				 
    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
    				 row.getCell(j-1).setCellStyle(colorStyle);
    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
    				 row.getCell(j-1).setCellStyle(colorStyle);
    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(country);
    				 row.getCell(j-1).setCellStyle(colorStyle);
    				 for(String time:dates){
    					 int forecast=0;
    					 int realData=0;
    					 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(time)!=null&&data.get(name).get(country).get(time).getQuantityForecast()!=null){
    						 forecast=data.get(name).get(country).get(time).getQuantityForecast();
						 }
    					 if(saleData!=null&&saleData.get(name)!=null&&saleData.get(name).get(country)!=null&&saleData.get(name).get(country).get(time)!=null){
    						 realData=saleData.get(name).get(country).get(time);
    					 }
    					 if(saleData==null||saleData.get(name)==null||saleData.get(name).get(country)==null||saleData.get(name).get(country).get(time)==null){
    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(forecast);
    					 }else if(saleData!=null&&saleData.get(name)!=null&&saleData.get(name).get(country)!=null&&saleData.get(name).get(country).get(time)!=null){
    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(realData);
    					 }else{
    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
    					 }
    					 row.getCell(j-1).setCellStyle(colorStyle);
    				 }
    				 for(String time:dates1){
    					 int forecast=0;
    					 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(time)!=null&&data.get(name).get(country).get(time).getQuantityForecast()!=null){
    						 forecast=data.get(name).get(country).get(time).getQuantityForecast();
    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(forecast);
						 }else{
    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
    					 }
    					 row.getCell(j-1).setCellStyle(colorStyle);
    					
    				 }
    				 //库存上限
    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(maxQuantity==null?0:(maxQuantity.get(name)==null?0:maxQuantity.get(name)));
    				 row.getCell(j-1).setCellStyle(colorStyle);
    				 
    				 //分国家单独缺口
    				 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
    				 row.getCell(j-1).setCellStyle(colorStyle);
    				 //累计总缺口
    				 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(tempTotal);
    				 if(tempTotal>0){
    					 row.getCell(j-1).setCellStyle(colorStyle);
    				 }else{
    					 row.getCell(j-1).setCellStyle(colorStyle);
    				 }
    				 //总体积
					 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
    				 row.getCell(j-1).setCellStyle(colorStyle);
    				 
    				//总库存
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventoryTotal);
    				row.getCell(j-1).setCellStyle(colorStyle);
    				//安全库存
    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(safeInventoryTotal);
    				row.getCell(j-1).setCellStyle(colorStyle);
    			}
    			//单产品总计行结束
	    	}
	    }
	    for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i);
	    }
	    try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
            String fileName = "月销售预测_供应链_"+sdf.format(new Date()) + ".xls";
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
	
	@RequestMapping(value ="exportSalesForecastByMonth")
	public String exportSalesForecastByMonth(SalesForecastByMonth salesForecast, HttpServletRequest request, HttpServletResponse response){
		Date today = DateUtils.getLastDayOfMonth(DateUtils.getDateStart(new Date()));
		salesForecast.setDataDate(today);
		Date start = DateUtils.addMonths(salesForecast.getDataDate(), -3);
		Date end = DateUtils.addMonths(salesForecast.getDataDate(), 12);
		Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAllWithType(start, end);
		Page<PsiProduct> page = new Page<PsiProduct>(request,response);
		page.setPageSize(800);
		page = psiProductService.findAllOnSaleNotNewByPage(page,"",salesForecast.getProductName());
		Map<String,Map<String,Map<String,Integer>>> saleData = salesForecastService.getRealSale(start, end);
		List<String> dates = Lists.newArrayList();
		List<String> dates1 = Lists.newArrayList();
		for (int i = 0; i < 15; i++) {
			if(i<3){
				dates.add(monthFormat.format(DateUtils.addMonths(start,i)));
			}else{
				dates1.add(monthFormat.format(DateUtils.addMonths(start,i)));
			}
		}
		List<String> countrys=Lists.newArrayList("eu","de","fr","it","es","uk","com","jp","ca");
		 
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
		
		//高亮显示
		HSSFCellStyle colorStyle = wb.createCellStyle();
		colorStyle.setFillBackgroundColor(HSSFColor.YELLOW.index);
		colorStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		colorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		colorStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		colorStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		colorStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		colorStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		colorStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		colorStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		colorStyle.setRightBorderColor(HSSFColor.BLACK.index);
		colorStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		colorStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCell cell = null;
	    List<String> title=Lists.newArrayList("序号","产品名称","平台");
	    for(String time:dates){
	    	title.add(time);
	    }
	    for(String time1:dates1){
	    	title.add(time1);
	    }
	    for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
		}
	    int index=0;
	    int num=1;
	    //符合预测条件的产品集合
	    List<String> nameList = psiProductEliminateService.findForecastName();
	    Map<String, String> findProductFanOuFlag = saleReportService.findProductFanOuFlag();
	    for(PsiProduct product:page.getList()){
	    	for(String name:product.getProductNameWithColor()){
	    		if (!nameList.contains(name)) {
					continue;
				}
	    		++index;
	    		int flag=0;
	    		for(String country:countrys){
	    		    String fanOu = findProductFanOuFlag.get(name);
	    			if(product.getPlatform().contains(country)||"eu".equals(country)){
	    				if ("0".equals(fanOu) && "fr,it,es,uk,eu".contains(country)) {
							continue;//泛欧产品欧洲平台统一汇总到德国
						}
	    				if("1".equals(fanOu) && "fr,it,es,eu".contains(country)){
	    				    continue;
	    				}
	    				 int j=2;
	    				 row = sheet.createRow(num++);
	    				 if(flag==0){
	    					 row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(index);
	    					 row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(name);
	    				 }else{
	    					 row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("");
	    					 row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue("");
	    				 }
	    				 row.getCell(0).setCellStyle(contentStyle);
	    				 row.getCell(1).setCellStyle(contentStyle);
	    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(country);
	    				 row.getCell(j-1).setCellStyle(contentStyle);
	    				 for(String time:dates){
	    					 int forecast=0;
	    					 int realData=0;
	    					 if ("0".equals(fanOu) && "de".equals(country)) {	//泛欧产品欧洲销量统计计算到德国
								try {
									realData+=saleData.get(name).get("uk").get(time);
								} catch (Exception e) {}
								try {
									realData+=saleData.get(name).get("eu").get(time);
								} catch (Exception e) {}
	    					 } else if("1".equals(fanOu) && "de".equals(country)){
	    					        try {
	                                    realData+=saleData.get(name).get("eu").get(time);
	                                } catch (Exception e) {}
	    					 }else {
		    					 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(time)!=null&&data.get(name).get(country).get(time).getQuantityForecast()!=null){
		    						 forecast=data.get(name).get(country).get(time).getQuantityForecast();
								 }
		    					 if(saleData!=null&&saleData.get(name)!=null&&saleData.get(name).get(country)!=null&&saleData.get(name).get(country).get(time)!=null){
		    						 realData=saleData.get(name).get(country).get(time);
		    					 }
	    					 }
	    					 if(realData > 0){
	    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(realData);
	    					 }else{
	    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(forecast);
	    					 }
	    					 row.getCell(j-1).setCellStyle(contentStyle);
	    				 }
                         int k=0;
                         String beforeTime="";
	    				 for(String time:dates1){
	    					 int forecast=0;
	    					 int realData=0;
	    					 int oldforecast=0;
	    					 if(k>0){
	    						 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(beforeTime)!=null&&data.get(name).get(country).get(beforeTime).getQuantityForecast()!=null){
	    							 oldforecast=data.get(name).get(country).get(beforeTime).getQuantityForecast();
								 }
	    					 }
	    					 beforeTime=time;
	    					 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(time)!=null&&data.get(name).get(country).get(time).getQuantityForecast()!=null){
	    						 forecast=data.get(name).get(country).get(time).getQuantityForecast();
							 }
	    					 if(saleData!=null&&saleData.get(name)!=null&&saleData.get(name).get(country)!=null&&saleData.get(name).get(country).get(time)!=null){
	    						 realData=saleData.get(name).get(country).get(time);
	    					 }
	    					
	    					 if(realData==0){
	    						 String con="";
	    						 if(k==0&&forecast>0&&saleData!=null&&saleData.get(name)!=null&&saleData.get(name).get(country)!=null&&saleData.get(name).get(country).get(dates.get(2))!=null){
	    							 if(saleData.get(name).get(country).get(dates.get(2)) > 0 && forecast*1.0/saleData.get(name).get(country).get(dates.get(2))>1.15){
	    								 con="  预测增幅"+MathUtils.roundUp(((forecast*1.0/saleData.get(name).get(country).get(dates.get(2)))-1)*100)+"%";
	    							 }
	    						 }else if(k>0&&forecast!=0&&oldforecast>0){
	    							 if(forecast*1.0/oldforecast>1.15){
	    								 con="  预测增幅 "+MathUtils.roundUp((forecast*1.0/oldforecast-1)*100)+"%";
	    							 }
	    						 }
	    						 HSSFCell cell2 = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
	    						 if (StringUtils.isNotEmpty(con)) {
		    						 cell2.setCellStyle(colorStyle);
								 } else {
									 cell2.setCellStyle(contentStyle); 
								 }
	    						 cell2.setCellValue(forecast);
	    						 //row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(forecast+con);
	    					 }else if(realData!=0&&forecast!=0){
	    						 String con="";
	    						 if(k==0&&forecast>0&&saleData!=null&&saleData.get(name)!=null&&saleData.get(name).get(country)!=null&&saleData.get(name).get(country).get(dates.get(2))!=null){
	    							 if(forecast*1.0/saleData.get(name).get(country).get(dates.get(2))>1.15){
	    								 con="  预测增幅 "+MathUtils.roundUp(((forecast*1.0/saleData.get(name).get(country).get(dates.get(2)))-1)*100)+"%";
	    							 }
	    						 }else if(k>0&&forecast!=0&&oldforecast>0){
	    							 if(forecast*1.0/oldforecast>1.15){
	    								 con="  预测增幅 "+MathUtils.roundUp((forecast*1.0/oldforecast-1)*100)+"%";
	    							 }
	    						 }
	    						 HSSFCell cell2 = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
	    						 if (StringUtils.isNotEmpty(con)) {
		    						 cell2.setCellStyle(colorStyle);
								 } else {
									 cell2.setCellStyle(contentStyle); 
								 }
	    						 cell2.setCellValue(forecast);
	    						 //row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(forecast+con);
	    					 }else{
	    						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	    						 row.getCell(j-1).setCellStyle(contentStyle);
	    					 }
	    					 k++;
	    					 //row.getCell(j-1).setCellStyle(contentStyle);
	    					
	    				 }
	    				 flag++;
	    			}
	    			
	    		}
	    		sheet.addMergedRegion(new CellRangeAddress(num-flag,num-1,0,0));
    			sheet.addMergedRegion(new CellRangeAddress(num-flag,num-1,1,1));
	    	}
	    }
	    for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i);
	    }
	    try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
            String fileName = "月销售预测_"+sdf.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value ="exportSalesForecastByMonthIsNew")
	public String exportSalesForecastByMonthIsNew(String type,SalesForecastByMonth salesForecast, HttpServletRequest request, HttpServletResponse response){
			Date today = DateUtils.getLastDayOfMonth(DateUtils.getDateStart(new Date()));
			salesForecast.setDataDate(today);
			Date start = DateUtils.addMonths(salesForecast.getDataDate(), -3);
			Date end = DateUtils.addMonths(salesForecast.getDataDate(), 12);
			//Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAll(salesForecast,start,end);
			Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAllWithType(start, end);
			Page<PsiProduct> page = new Page<PsiProduct>(request,response);
			page.setPageSize(800);
			page = psiProductService.findAllOnSaleNotNewByPage(page,"",salesForecast.getProductName());
			Map<String,Map<String,Map<String,Integer>>> saleData = salesForecastService.getRealSale(start, end);
			List<String> dates = Lists.newArrayList();
			List<String> dates1 = Lists.newArrayList();
			for (int i = 0; i < 15; i++) {
				if(i<3){
					dates.add(monthFormat.format(DateUtils.addMonths(start,i)));
				}else{
					dates1.add(monthFormat.format(DateUtils.addMonths(start,i)));
				}
			}
			List<String> countrys=Lists.newArrayList("eu","de","fr","it","es","uk","com","jp","ca","mx");
			 
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
			
			//高亮显示
			HSSFCellStyle colorStyle = wb.createCellStyle();
			colorStyle.setFillBackgroundColor(HSSFColor.YELLOW.index);
			colorStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
			colorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			colorStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			colorStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			colorStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			colorStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			colorStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			colorStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			colorStyle.setRightBorderColor(HSSFColor.BLACK.index);
			colorStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			colorStyle.setTopBorderColor(HSSFColor.BLACK.index);
			
			HSSFCell cell = null;
		    List<String> title=Lists.newArrayList("序号","产品名称","平台");
		    for(String time:dates){
		    	title.add(time);
		    }
		    for(String time1:dates1){
		    	title.add(time1);
		    }
		    for(int i=0;i<title.size();i++){
				cell = row.createCell(i);
				cell.setCellStyle(style);
				cell.setCellValue(title.get(i));
			}
		    int index=0;
		    int num=1;
		    List<String> isNewList = psiProductEliminateService.findIsNewProductName();
		    Map<String, String> productPositionMap = psiProductEliminateService.findProductPositionByCountry(null);
		    for(PsiProduct product:page.getList()){
		    	//if(type.equals(product.getIsNew())){
			    	for(String name:product.getProductNameWithColor()){
			    		if (!"4".equals(productPositionMap.get(name))) {
							continue;
						}
			    		if ("1".equals(type) && !isNewList.contains(name)) {
							continue;
						} else if ("0".equals(type) && isNewList.contains(name)) {
							continue;
						}
			    		++index;
			    		int flag=0;
			    		for(String country:countrys){
			    			if(product.getPlatform().contains(country)||"eu".equals(country)){
			    				 int j=2;
			    				 row = sheet.createRow(num++);
			    				 if(flag==0){
			    					 row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(index);
			    					 row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(name);
			    				 }else{
			    					 row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("");
			    					 row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue("");
			    				 }
			    				 row.getCell(0).setCellStyle(contentStyle);
			    				 row.getCell(1).setCellStyle(contentStyle);
			    				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(country);
			    				 row.getCell(j-1).setCellStyle(contentStyle);
			    				 for(String time:dates){
			    					 int forecast=0;
			    					 int realData=0;
			    					 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(time)!=null&&data.get(name).get(country).get(time).getQuantityForecast()!=null){
			    						 forecast=data.get(name).get(country).get(time).getQuantityForecast();
									 }
			    					 if(saleData!=null&&saleData.get(name)!=null&&saleData.get(name).get(country)!=null&&saleData.get(name).get(country).get(time)!=null){
			    						 realData=saleData.get(name).get(country).get(time);
			    					 }
			    					
			    					 if(saleData==null||saleData.get(name)==null||saleData.get(name).get(country)==null||saleData.get(name).get(country).get(time)==null){
			    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(forecast);
			    					 }else if(saleData!=null&&saleData.get(name)!=null&&saleData.get(name).get(country)!=null&&saleData.get(name).get(country).get(time)!=null){
			    						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(realData);
			    					 }else{
			    						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    					 }
			    					 row.getCell(j-1).setCellStyle(contentStyle);
			    				 }
	                             int k=0;
	                             String beforeTime="";
			    				 for(String time:dates1){
			    					 int forecast=0;
			    					 int realData=0;
			    					 int oldforecast=0;
			    					 if(k>0){
			    						 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(beforeTime)!=null&&data.get(name).get(country).get(beforeTime).getQuantityForecast()!=null){
			    							 oldforecast=data.get(name).get(country).get(beforeTime).getQuantityForecast();
										 }
			    					 }
			    					 beforeTime=time;
			    					 if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(time)!=null&&data.get(name).get(country).get(time).getQuantityForecast()!=null){
			    						 forecast=data.get(name).get(country).get(time).getQuantityForecast();
									 }
			    					 if(saleData!=null&&saleData.get(name)!=null&&saleData.get(name).get(country)!=null&&saleData.get(name).get(country).get(time)!=null){
			    						 realData=saleData.get(name).get(country).get(time);
			    					 }
			    					
			    					 if(realData==0){
			    						 String con="";
			    						 if(k==0&&forecast>0&&saleData!=null&&saleData.get(name)!=null&&saleData.get(name).get(country)!=null&&saleData.get(name).get(country).get(dates.get(2))!=null){
			    							 if(saleData.get(name).get(country).get(dates.get(2)) > 0 && forecast*1.0/saleData.get(name).get(country).get(dates.get(2))>1.15){
			    								con="  预测增幅"+MathUtils.roundUp(((forecast*1.0/saleData.get(name).get(country).get(dates.get(2)))-1)*100)+"%";
			    							 }
			    						 }else if(k>0&&forecast!=0&&oldforecast>0){
			    							 if(forecast*1.0/oldforecast>1.15){
			    								 con="  预测增幅 "+MathUtils.roundUp((forecast*1.0/oldforecast-1)*100)+"%";
			    							 }
			    						 }
			    						 HSSFCell cell2 = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
			    						 if (StringUtils.isNotEmpty(con)) {
				    						 cell2.setCellStyle(colorStyle);
										 } else {
											 cell2.setCellStyle(contentStyle); 
										 }
			    						 cell2.setCellValue(forecast);
			    					 }else if(realData!=0&&forecast!=0){
			    						 String con="";
			    						 if(k==0&&forecast>0&&saleData!=null&&saleData.get(name)!=null&&saleData.get(name).get(country)!=null&&saleData.get(name).get(country).get(dates.get(2))!=null){
			    							 if(forecast*1.0/saleData.get(name).get(country).get(dates.get(2))>1.15){
			    								 con="  预测增幅 "+MathUtils.roundUp(((forecast*1.0/saleData.get(name).get(country).get(dates.get(2)))-1)*100)+"%";
			    							 }
			    						 }else if(k>0&&forecast!=0&&oldforecast>0){
			    							 if(forecast*1.0/oldforecast>1.15){
			    								 con="  预测增幅 "+MathUtils.roundUp((forecast*1.0/oldforecast-1)*100)+"%";
			    							 }
			    						 }
			    						 HSSFCell cell2 = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
			    						 if (StringUtils.isNotEmpty(con)) {
				    						 cell2.setCellStyle(colorStyle);
										 } else {
											 cell2.setCellStyle(contentStyle); 
										 }
			    						 cell2.setCellValue(forecast);
			    						 //cell2.setCellStyle(colorStyle);
			    						// row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(forecast+con);
			    					 }else{
			    						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    						 row.getCell(j-1).setCellStyle(contentStyle);
			    					 }
			    					 k++;
			    					 //row.getCell(j-1).setCellStyle(contentStyle);
			    					
			    				 }
			    				 flag++;
			    			}
			    			
			    		}
			    		sheet.addMergedRegion(new CellRangeAddress(num-flag,num-1,0,0));
		    			sheet.addMergedRegion(new CellRangeAddress(num-flag,num-1,1,1));
			    	}
		    	//}
		    	
		    }
		    for (int i = 0; i < title.size(); i++) {
		   		   sheet.autoSizeColumn((short)i);
		    }
		    try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	            String fileName = ("0".equals(type)?"普通":"新品")+"月销售预测_"+sdf.format(new Date()) + ".xls";
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
	
	@RequestMapping(value ="export")
	public String export(SalesForecastByMonth salesForecast, HttpServletRequest request, HttpServletResponse response) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = format.parse("2017-07-01");
		Date today = DateUtils.getLastDayOfMonth(DateUtils.getDateStart(date));
		salesForecast.setDataDate(today);
		Date start = today;
		Date end = DateUtils.addMonths(salesForecast.getDataDate(), 6);
		
		Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAllWithType(start,end);
		Page<PsiProduct> page = new Page<PsiProduct>(request,response);
		page.setPageSize(800);
		page = psiProductService.findAllOnSaleNotNewByPage(page,"",salesForecast.getProductName());
		
		List<String> dates = Lists.newArrayList();
		for (int i = 0; i <6; i++) {
			dates.add(monthFormat.format(DateUtils.addMonths(start,i)));
		}
		List<String> countrys=Lists.newArrayList("de","fr","it","es","uk","com","ca","jp");
		 
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 600);
		
		HSSFCell cell = null;
	    List<String> title=Lists.newArrayList("产品名称","产品线","国家","总库存","31日销","31日库销比");
	    for(String time:dates){
	    	title.add(time);
	    }
	    
	    for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
		}
	    int num=1;
	    Map<String, String> typeLineMap = groupDictService.getTypeLine(null);
	    Map<String,ProductSalesInfo> fangchaMap = salesInfoService.findAll();
	    Map<String,Map<String,PsiProductInStock>> inStockMap = psiProductInStockService.getHistoryInventory();
	    for(PsiProduct product:page.getList()){
	    	boolean fanOu = "1".equals(product.getHasPower())?false:true;
	    	for(String name:product.getProductNameWithColor()){
	    		for(String country:countrys){
	    			if(product.getPlatform().contains(country)){
	    				String key = name + "_" + country;
	    				String countryTemp = country;
	    				if (fanOu && "fr,it,es,uk".contains(country)) {
							continue;//泛欧产品欧洲平台统一汇总到德国
						}
	    				if (fanOu && "de".equals(country)) {
		    				key = name + "_eu";
		    				countryTemp = "eu";
						}
	    				int j=0;
	    				row = sheet.createRow(num++);  
    					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(typeLineMap.get(product.getType().toLowerCase()));	
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
	    				int total = 0;
	    				if (inStockMap.get(name)!=null && inStockMap.get(name).get(countryTemp)!=null) {
	    					total = inStockMap.get(name).get(countryTemp).getTotalStock();
						}
	    				int day31sales = 0;
	    				if (fangchaMap.get(key) != null) {
	    					day31sales = fangchaMap.get(key).getDay31Sales();
						}
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total);
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(day31sales);
	    				if (day31sales > 0) {
	    					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total/(double)day31sales);
						} else {
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						for (String time : dates) {
	    					int forecast=0;
	    					if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null&&data.get(name).get(country).get(time)!=null&&data.get(name).get(country).get(time).getQuantityForecast()!=null){
	    						forecast=data.get(name).get(country).get(time).getQuantityForecast();
							}
	    					if (forecast > 0) {
	    						row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(forecast);
							} else {
								row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
							}
	    				}
	    			}
	    		}
	    	}
	    }
	    try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
            String fileName = "旺季备货导出表格.xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("旺季备货导出表格异常", e);
		}
		return null;
	}
	
}
