/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiInventoryGap;
import com.springrain.erp.modules.psi.entity.PsiOutOfStockInfo;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductInStock;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiOutOfStockInfoService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductInStockService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.DictUtils;


@Controller
@RequestMapping(value = "${adminPath}/psi/psiOutOfStockInfo")
public class PsiOutOfStockInfoController extends BaseController {
	@Autowired
	private PsiOutOfStockInfoService psiOutOfStockInfoService;
	
	@Autowired
	private PsiInventoryService psiInventoryService ;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private PsiProductInStockService  psiProductInStockService;
	
	@Autowired
	private PsiProductAttributeService psiProductAttributeService;
	
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiOutOfStockInfo psiOutOfStockInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PsiOutOfStockInfo> page = new Page<PsiOutOfStockInfo>(request,response);
		
		if(psiOutOfStockInfo.getCreateDate()==null){
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			psiOutOfStockInfo.setCreateDate(DateUtils.addDays(today,-5));
			psiOutOfStockInfo.setActualDate(today);
		}
		page=psiOutOfStockInfoService.findAllInfo(page, psiOutOfStockInfo);
		model.addAttribute("page",page);
		return "modules/psi/psiOutOfStockInfoList";
	}

	
	@RequestMapping(value = "findGapInfo")
	public String findGapInfo(String weekNum,String country,String forecastType,HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isBlank(country)){
			country="de";
		}
		if(StringUtils.isBlank(weekNum)){
			weekNum="0";
		}
		//Map<String,Map<String,PsiInventoryGap>> gapMap=psiOutOfStockInfoService.findGap(country,forecastType,weekNum);
		Map<String,Map<String,Map<String,PsiInventoryGap>>> gapMap=psiOutOfStockInfoService.findGapAllForecastType(country,weekNum);
		model.addAttribute("gapMap",gapMap);
		model.addAttribute("country",country);
		model.addAttribute("weekNum",weekNum);
		//model.addAttribute("forecastType",forecastType);
		Map<String,PsiInventoryFba> fbaStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
		model.addAttribute("fbaStock",fbaStock);
		if("de".equals(country)){
			model.addAttribute("powerMap",psiProductService.getHasPowerByName());
		}
		Map<String, String> positionMap = psiProductEliminateService.findProductPositionByCountry(null);
		model.addAttribute("positionMap", positionMap);//产品定位 3：主力
		model.addAttribute("tranMap",psiProductAttributeService.findtransportType());// 1 海运  2空运
		return "modules/psi/psiInventoryGapList";
	}
	
	@RequestMapping(value = "findEuGapInfo")
	public String findEuGapInfo(String weekNum,String country,String forecastType,HttpServletRequest request, HttpServletResponse response, Model model) {
		
		if(StringUtils.isBlank(country)){
			country="de";
		}
		if(StringUtils.isBlank(weekNum)){
			weekNum="0";
		}
		//Map<String,Map<String,PsiInventoryGap>> gapMap=psiOutOfStockInfoService.findGap(country,forecastType,weekNum);
		Map<String,Map<String,Map<String,PsiInventoryGap>>> gapMap=psiOutOfStockInfoService.findGapAllForecastType(country,weekNum);
		model.addAttribute("gapMap",gapMap);
		model.addAttribute("country",country);
		model.addAttribute("weekNum",weekNum);
		//model.addAttribute("forecastType",forecastType);
		Map<String,PsiInventoryFba> fbaStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
		model.addAttribute("fbaStock",fbaStock);
		if("de".equals(country)){
			model.addAttribute("powerMap",psiProductService.getHasPowerByName());
		}
		Map<String, String> positionMap = psiProductEliminateService.findProductPositionByCountry(null);
		model.addAttribute("positionMap", positionMap);
		model.addAttribute("tranMap",psiProductAttributeService.findtransportType());// 1 海运  2空运
		return "modules/psi/psiInventoryEuGapList";
	}
	
	@RequestMapping(value = "findAllGapInfo")
	public String findAllGapInfo(String weekNum,String forecastType,HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isBlank(forecastType)){
			forecastType="0";
		}
		if(StringUtils.isBlank(weekNum)){
			weekNum="0";
		}
		//Map<String,Map<String,Map<String,PsiInventoryGap>>>  gapMap=psiOutOfStockInfoService.findAllCountryGap(forecastType,weekNum);
		Map<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>>  gapMap=psiOutOfStockInfoService.findAllCountryGap(weekNum);
		model.addAttribute("gapMap",gapMap);
		model.addAttribute("weekNum",weekNum);
		Map<String,PsiInventoryFba> fbaStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
		model.addAttribute("fbaStock",fbaStock);
		model.addAttribute("powerMap",psiProductService.getHasPowerByName());
		Map<String, String> positionMap = psiProductEliminateService.findProductPositionByCountry(null);
		model.addAttribute("positionMap", positionMap);
		model.addAttribute("tranMap",psiProductAttributeService.findtransportType());// 1 海运  2空运
		return "modules/psi/psiInventoryAllGapList";
	}
	
	

	@RequestMapping(value = "exportAllDetail")
	public String exportAllDetail(String forecastType,HttpServletRequest request,HttpServletResponse response, Model model) {
		
		Map<String, String> positionMap = psiProductEliminateService.findProductPositionByCountry(null);
		Map<String,PsiInventoryFba> fbaStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
		Map<String,Map<String,Integer>> fabMap=psiOutOfStockInfoService.getFbaTrans();
		Map<String,Map<String,Integer>> localMap=psiOutOfStockInfoService.getLocalTrans();
		Map<String,Map<String,Integer>> deMap=psiOutOfStockInfoService.getDeStock();
		Map<String,Map<String,Integer>> cnMap=psiOutOfStockInfoService.getCnStock();
		Map<String,Map<String,Integer>> poMap=psiOutOfStockInfoService.getPoStock();
		
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
		
		HSSFCellStyle contentStyle = wb.createCellStyle();
		contentStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
		contentStyle.setFillBackgroundColor(HSSFColor.GREEN.index);
		contentStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		
		HSSFCellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
		contentStyle1.setFillBackgroundColor(HSSFColor.LIME.index);
		contentStyle1.setFillForegroundColor(HSSFColor.LIME.index);
		
		
		HSSFCellStyle contentStyle2 = wb.createCellStyle();
		HSSFFont font1 = wb.createFont();
		font1.setColor(HSSFColor.RED.index);
		contentStyle2.setFont(font1);
		
		row.setHeight((short) 600);
		HSSFCell cell = null;	
		List<String>  title=Lists.newArrayList("产品","国家","类型","分类","生产周期","运输周期","安全天数","安全库存","最低起订","FBA在途","本地在途","德国仓","中国仓","PO","总计","周次");
		Date createDate=new Date();
		Date start = new Date();
		start.setHours(0);
		start.setMinutes(0);
		start.setSeconds(0);
		Date end = DateUtils.addMonths(start,5);
		DateFormat formatWeek = new SimpleDateFormat("yyyyww");
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		if(start.getDay()==0){
			start = DateUtils.addDays(start, -1);
		}
		int count=0;
		while(end.after(start)||end.equals(start)){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(start).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				if(count>=16){
					break;
				}else{
					title.add(key);
					start = DateUtils.addWeeks(start, 1);
				}
				count++;
		 }
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		
		int  rowIndex=1;
		
		for (Dict dict : DictUtils.getDictList("platform")) {
			String country = dict.getValue();
			if(!"mx".equals(country)&&!"com.unitek".equals(country)){
				Map<String,Map<String,PsiInventoryGap>> map=psiOutOfStockInfoService.findAllGap(country,forecastType);
				Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory(country);
				Map<String,PsiProductInStock> euStockMap=psiProductInStockService.getHistoryInventory("eu");
				for (Map.Entry<String, Map<String, PsiInventoryGap>> entry: map.entrySet()) {
					String name = entry.getKey();
					Map<String,PsiInventoryGap> temp=entry.getValue();
					PsiProduct product=psiProductService.findProductByProductName(name);
					for(int i=0;i<=12;i++){
						int m=0;
						row=sheet.createRow(rowIndex++);
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(name);
						if("de".equals(country)&&"0".equals(product.getHasPower())){//不带电源
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("eu");
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(country);
						}
					
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(product.getType());
						
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(DictUtils.getDictLabel(positionMap.get(name), "product_position", ""));
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(product.getProducePeriod());
						Integer safe=0;
						if("de".equals(country)&&"0".equals(product.getHasPower())){//不带电源
							if(euStockMap!=null&&euStockMap.get(name)!=null&&euStockMap.get(name).getPeriod()!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(euStockMap.get(name).getPeriod()-product.getProducePeriod());
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							if(euStockMap!=null&&euStockMap.get(name)!=null&&euStockMap.get(name).getSafeDay()!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(euStockMap.get(name).getSafeDay());
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							if(euStockMap!=null&&euStockMap.get(name)!=null&&euStockMap.get(name).getSafeDay()!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(MathUtils.roundUp(euStockMap.get(name).getSafeInventory()));
								safe=MathUtils.roundUp(euStockMap.get(name).getSafeInventory());
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
						}else{
							if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getPeriod()!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(stockMap.get(name).getPeriod()-product.getProducePeriod());
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getSafeDay()!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(stockMap.get(name).getSafeDay());
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getSafeDay()!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(MathUtils.roundUp(stockMap.get(name).getSafeInventory()));
								safe=MathUtils.roundUp(stockMap.get(name).getSafeInventory());
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
						}
						
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(product.getMinOrderPlaced());
						
						
						if("de".equals(country)&&product!=null&&"0".equals(product.getHasPower())){//不带电源 泛欧   hasPower 0：不带电源 1：带电源
							if(fabMap!=null&&fabMap.get("eu")!=null&&fabMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fabMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							if(localMap!=null&&localMap.get("eu")!=null&&localMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(localMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							if(deMap!=null&&deMap.get("eu")!=null&&deMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							if(cnMap!=null&&cnMap.get("eu")!=null&&cnMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(cnMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							if(poMap!=null&&poMap.get("eu")!=null&&poMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(poMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							if(i==0){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("销量预测");
								setAllValue(m,"1",row,temp);
							}else if(i==1){
								if(fbaStock!=null&&fbaStock.get(name+"_eu")!=null&&fbaStock.get(name+"_eu").getFulfillableQuantity()!=null){
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fbaStock.get(name+"_eu").getFulfillableQuantity());
								}else{
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊仓");
								setAllValue(m,"2",row,temp);
							}else if(i==2){
								if(fabMap!=null&&fabMap.get("eu")!=null&&fabMap.get("eu").get(name)!=null){
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fabMap.get("eu").get(name));
								}else{
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊在途");
								setAllValue(m,"3",row,temp);
							}else if(i==3){
								if(deMap!=null&&deMap.get("eu")!=null&&deMap.get("eu").get(name)!=null){
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deMap.get("eu").get(name));
								}else{
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("本地仓");
								setAllValue(m,"4",row,temp);
							}else if(i==4){
								if(localMap!=null&&localMap.get("eu")!=null&&localMap.get("eu").get(name)!=null){
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(localMap.get("eu").get(name));
								}else{
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("在途");
								setAllValue(m,"5",row,temp);
							}else if(i==5){
								if(cnMap!=null&&cnMap.get("eu")!=null&&cnMap.get("eu").get(name)!=null){
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(cnMap.get("eu").get(name));
								}else{
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("中国仓");
								setAllValue(m,"6",row,temp);
							}else if(i==6){
								if(poMap!=null&&poMap.get("eu")!=null&&poMap.get("eu").get(name)!=null){
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(poMap.get("eu").get(name));
								}else{
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("PO");
								setAllValue(m,"7",row,temp);
							}else if(i==7){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊仓balance");
								setAllValue(m,"8",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
							}else if(i==8){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("FBA在途balance");
								setAllValue(m,"9",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
							}else if(i==9){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("本地仓balance");
								setAllValue(m,"10",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
							}else if(i==10){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("在途balance");
								setAllValue(m,"11",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
							}else if(i==11){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("中国仓balance");
								setAllValue(m,"12",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
							}else if(i==12){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("PO balance");
								setAllValue(m,"13",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
							}
						}else{
							if(fabMap!=null&&fabMap.get(country)!=null&&fabMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fabMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							if(localMap!=null&&localMap.get(country)!=null&&localMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(localMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							if(deMap!=null&&deMap.get(country)!=null&&deMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							if(cnMap!=null&&cnMap.get(country)!=null&&cnMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(cnMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							if(poMap!=null&&poMap.get(country)!=null&&poMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(poMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							if(i==0){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("销量预测");
								setAllValue(m,"1",row,temp);
							}else if(i==1){
								if(fbaStock!=null&&fbaStock.get(name+"_"+country)!=null&&fbaStock.get(name+"_"+country).getFulfillableQuantity()!=null){
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fbaStock.get(name+"_"+country).getFulfillableQuantity());
								}else{
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊仓");
								setAllValue(m,"2",row,temp);
							}else if(i==2){
								if(fabMap!=null&&fabMap.get(country)!=null&&fabMap.get(country).get(name)!=null){
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fabMap.get(country).get(name));
								}else{
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
								
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊在途");
								setAllValue(m,"3",row,temp);
							}else if(i==3){
								if(deMap!=null&&deMap.get(country)!=null&&deMap.get(country).get(name)!=null){
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deMap.get(country).get(name));
								}else{
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
								
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("本地仓");
								setAllValue(m,"4",row,temp);
							}else if(i==4){
								if(localMap!=null&&localMap.get(country)!=null&&localMap.get(country).get(name)!=null){
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(localMap.get(country).get(name));
								}else{
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("在途");
								setAllValue(m,"5",row,temp);
							}else if(i==5){
								if(cnMap!=null&&cnMap.get(country)!=null&&cnMap.get(country).get(name)!=null){
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(cnMap.get(country).get(name));
								}else{
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("中国仓");
								setAllValue(m,"6",row,temp);
							}else if(i==6){
								if(poMap!=null&&poMap.get(country)!=null&&poMap.get(country).get(name)!=null){
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(poMap.get(country).get(name));
								}else{
									row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("PO");
								setAllValue(m,"7",row,temp);
							}else if(i==7){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊仓balance");
								setAllValue(m,"8",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
							}else if(i==8){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("FBA在途balance");
								setAllValue(m,"9",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
							}else if(i==9){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("本地仓balance");
								setAllValue(m,"10",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
							}else if(i==10){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("在途balance");
								setAllValue(m,"11",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
							}else if(i==11){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("中国仓balance");
								setAllValue(m,"12",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
							}else if(i==12){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("PO balance");
								setAllValue(m,"13",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
							}
						}
						
					}
			     }
			}
		 }		
		
		
		
		/* for (int i=1;i<rowIndex;i++) {
       	      for (int j = 0; j < title.size(); j++) {
       	    		sheet.getRow(i).getCell(j).setCellStyle(contentStyle);
			 }
         }*/
		  for (int i = 0; i < title.size(); i++) {
       		 sheet.autoSizeColumn((short)i, true);
		  }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			//String fileName =("0".equals(forecastType)?"销售预测方式":"周日销方式");
			String fileName = "库存缺口"+("0".equals(forecastType)?"销售预测方式":"周日销方式")+new SimpleDateFormat("yyyyMMdd").format(new Date());
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName+".xls");
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = "exportDetail")
	public String exportDetail(String country,String forecastType,HttpServletRequest request,HttpServletResponse response, Model model) {
		Map<String,Map<String,PsiInventoryGap>> map=psiOutOfStockInfoService.findAllGap(country,forecastType);
		Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory(country);
		Map<String, String> positionMap = psiProductEliminateService.findProductPositionByCountry(null);
		
		Map<String,PsiInventoryFba> fbaStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
		Map<String,Map<String,Integer>> fabMap=psiOutOfStockInfoService.getFbaTrans();
		Map<String,Map<String,Integer>> localMap=psiOutOfStockInfoService.getLocalTrans();
		Map<String,Map<String,Integer>> deMap=psiOutOfStockInfoService.getDeStock();
		Map<String,Map<String,Integer>> cnMap=psiOutOfStockInfoService.getCnStock();
		Map<String,Map<String,Integer>> poMap=psiOutOfStockInfoService.getPoStock();
		
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
		HSSFCellStyle contentStyle = wb.createCellStyle();
		contentStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
		contentStyle.setFillBackgroundColor(HSSFColor.GREEN.index);
		contentStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		
		HSSFCellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
		contentStyle1.setFillBackgroundColor(HSSFColor.LIME.index);
		contentStyle1.setFillForegroundColor(HSSFColor.LIME.index);
		
		HSSFCellStyle contentStyle2 = wb.createCellStyle();
		HSSFFont font1 = wb.createFont();
		font1.setColor(HSSFColor.RED.index);
		contentStyle2.setFont(font1);
		
		row.setHeight((short) 600);
		HSSFCell cell = null;	
		List<String>  title=Lists.newArrayList("产品","国家","类型","分类","生产周期","运输周期","安全天数","安全库存","最低起订","FBA在途","本地在途","德国仓","中国仓","PO","总计","周次");
		
		Date createDate=new Date();
		Date start = new Date();
		start.setHours(0);
		start.setMinutes(0);
		start.setSeconds(0);
		Date end = DateUtils.addMonths(start,5);
		DateFormat formatWeek = new SimpleDateFormat("yyyyww");
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		if(start.getDay()==0){
			start = DateUtils.addDays(start, -1);
		}
		int count=0;
		while(end.after(start)||end.equals(start)){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(start).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				if(count>=16){
					break;
				}else{
					title.add(key);
					start = DateUtils.addWeeks(start, 1);
				}
				count++;
		 }
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		
		int  rowIndex=1;
		for (Map.Entry<String, Map<String, PsiInventoryGap>> entry: map.entrySet()) {
				String name = entry.getKey();
				Map<String,PsiInventoryGap> temp=entry.getValue();
				PsiProduct product=psiProductService.findProductByProductName(name);
				if("de".equals(country)&&"0".equals(product.getHasPower())){//不带电源
					continue;
				}
				for(int i=0;i<=12;i++){
					int m=0;
					row=sheet.createRow(rowIndex++);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(name);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(country);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(product.getType());
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(DictUtils.getDictLabel(positionMap.get(name), "product_position", ""));
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(product.getProducePeriod());
					if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getPeriod()!=null){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(stockMap.get(name).getPeriod()-product.getProducePeriod());
					}else{
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getSafeDay()!=null){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(stockMap.get(name).getSafeDay());
					}else{
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					Integer safe=0;
					if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getSafeDay()!=null){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(MathUtils.roundUp(stockMap.get(name).getSafeInventory()));
						safe=MathUtils.roundUp(stockMap.get(name).getSafeInventory());
					}else{
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(product.getMinOrderPlaced());
					
					if("de".equals(country)&&product!=null&&"0".equals(product.getHasPower())){//不带电源 泛欧   hasPower 0：不带电源 1：带电源
						if(fabMap!=null&&fabMap.get("eu")!=null&&fabMap.get("eu").get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fabMap.get("eu").get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(localMap!=null&&localMap.get("eu")!=null&&localMap.get("eu").get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(localMap.get("eu").get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(deMap!=null&&deMap.get("eu")!=null&&deMap.get("eu").get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deMap.get("eu").get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(cnMap!=null&&cnMap.get("eu")!=null&&cnMap.get("eu").get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(cnMap.get("eu").get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(poMap!=null&&poMap.get("eu")!=null&&poMap.get("eu").get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(poMap.get("eu").get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						if(i==0){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("销量预测");
							setAllValue(m,"1",row,temp);
						}else if(i==1){
							if(fbaStock!=null&&fbaStock.get(name+"_eu")!=null&&fbaStock.get(name+"_eu").getFulfillableQuantity()!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fbaStock.get(name+"_eu").getFulfillableQuantity());
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊仓");
							setAllValue(m,"2",row,temp);
						}else if(i==2){
							if(fabMap!=null&&fabMap.get("eu")!=null&&fabMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fabMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊在途");
							setAllValue(m,"3",row,temp);
						}else if(i==3){
							if(deMap!=null&&deMap.get("eu")!=null&&deMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("本地仓");
							setAllValue(m,"4",row,temp);
						}else if(i==4){
							if(localMap!=null&&localMap.get("eu")!=null&&localMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(localMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("在途");
							setAllValue(m,"5",row,temp);
						}else if(i==5){
							if(cnMap!=null&&cnMap.get("eu")!=null&&cnMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(cnMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("中国仓");
							setAllValue(m,"6",row,temp);
						}else if(i==6){
							if(poMap!=null&&poMap.get("eu")!=null&&poMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(poMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("PO");
							setAllValue(m,"7",row,temp);
						}else if(i==7){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊仓balance");
							setAllValue(m,"8",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==8){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("FBA在途balance");
							setAllValue(m,"9",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==9){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("本地仓balance");
							setAllValue(m,"10",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==10){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("在途balance");
							setAllValue(m,"11",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==11){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("中国仓balance");
							setAllValue(m,"12",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==12){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("PO balance");
							setAllValue(m,"13",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}
					}else{
						if(fabMap!=null&&fabMap.get(country)!=null&&fabMap.get(country).get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fabMap.get(country).get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(localMap!=null&&localMap.get(country)!=null&&localMap.get(country).get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(localMap.get(country).get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(deMap!=null&&deMap.get(country)!=null&&deMap.get(country).get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deMap.get(country).get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(cnMap!=null&&cnMap.get(country)!=null&&cnMap.get(country).get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(cnMap.get(country).get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(poMap!=null&&poMap.get(country)!=null&&poMap.get(country).get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(poMap.get(country).get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						if(i==0){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("销量预测");
							setAllValue(m,"1",row,temp);
						}else if(i==1){
							if(fbaStock!=null&&fbaStock.get(name+"_"+country)!=null&&fbaStock.get(name+"_"+country).getFulfillableQuantity()!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fbaStock.get(name+"_"+country).getFulfillableQuantity());
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊仓");
							setAllValue(m,"2",row,temp);
						}else if(i==2){
							if(fabMap!=null&&fabMap.get(country)!=null&&fabMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fabMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊在途");
							setAllValue(m,"3",row,temp);
						}else if(i==3){
							if(deMap!=null&&deMap.get(country)!=null&&deMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("本地仓");
							setAllValue(m,"4",row,temp);
						}else if(i==4){
							if(localMap!=null&&localMap.get(country)!=null&&localMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(localMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("在途");
							setAllValue(m,"5",row,temp);
						}else if(i==5){
							if(cnMap!=null&&cnMap.get(country)!=null&&cnMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(cnMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("中国仓");
							setAllValue(m,"6",row,temp);
						}else if(i==6){
							if(poMap!=null&&poMap.get(country)!=null&&poMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(poMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("PO");
							setAllValue(m,"7",row,temp);
						}else if(i==7){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊仓balance");
							setAllValue(m,"8",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==8){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("FBA在途balance");
							setAllValue(m,"9",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==9){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("本地仓balance");
							setAllValue(m,"10",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==10){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("在途balance");
							setAllValue(m,"11",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==11){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("中国仓balance");
							setAllValue(m,"12",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==12){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("PO balance");
							setAllValue(m,"13",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}
					}
					
					
					
				}
		}
		
		/* for (int i=1;i<rowIndex;i++) {
       	      for (int j = 0; j < title.size(); j++) {
       	    		sheet.getRow(i).getCell(j).setCellStyle(contentStyle);
			 }
         }*/
		  for (int i = 0; i < title.size(); i++) {
       		 sheet.autoSizeColumn((short)i, true);
		  }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			//String fileName = country+("0".equals(forecastType)?"销售预测方式":"周日销方式");
			String fileName = "库存缺口_"+country+("0".equals(forecastType)?"销售预测方式":"周日销方式")+new SimpleDateFormat("yyyyMMdd").format(new Date());
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName+".xls");
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@RequestMapping(value = "exportEuDetail")
	public String exportEuDetail(String country,String forecastType,HttpServletRequest request,HttpServletResponse response, Model model) {
		Map<String,Map<String,PsiInventoryGap>> map=psiOutOfStockInfoService.findAllGap(country,forecastType);
		Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory("eu");
		Map<String, String> positionMap = psiProductEliminateService.findProductPositionByCountry(null);
		Map<String,PsiInventoryFba> fbaStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
		Map<String,Map<String,Integer>> fabMap=psiOutOfStockInfoService.getFbaTrans();
		Map<String,Map<String,Integer>> localMap=psiOutOfStockInfoService.getLocalTrans();
		Map<String,Map<String,Integer>> deMap=psiOutOfStockInfoService.getDeStock();
		Map<String,Map<String,Integer>> cnMap=psiOutOfStockInfoService.getCnStock();
		Map<String,Map<String,Integer>> poMap=psiOutOfStockInfoService.getPoStock();
		
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
		HSSFCellStyle contentStyle = wb.createCellStyle();
		contentStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
		contentStyle.setFillBackgroundColor(HSSFColor.GREEN.index);
		contentStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		
		HSSFCellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
		contentStyle1.setFillBackgroundColor(HSSFColor.LIME.index);
		contentStyle1.setFillForegroundColor(HSSFColor.LIME.index);
		
		HSSFCellStyle contentStyle2 = wb.createCellStyle();
		HSSFFont font1 = wb.createFont();
		font1.setColor(HSSFColor.RED.index);
		contentStyle2.setFont(font1);
		
		row.setHeight((short) 600);
		HSSFCell cell = null;	
		List<String>  title=Lists.newArrayList("产品","国家","类型","分类","生产周期","运输周期","安全天数","安全库存","最低起订","FBA在途","本地在途","德国仓","中国仓","PO","周次");
		
		Date createDate=new Date();
		Date start = new Date();
		start.setHours(0);
		start.setMinutes(0);
		start.setSeconds(0);
		Date end = DateUtils.addMonths(start,5);
		DateFormat formatWeek = new SimpleDateFormat("yyyyww");
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		if(start.getDay()==0){
			start = DateUtils.addDays(start, -1);
		}
		int count=0;
		while(end.after(start)||end.equals(start)){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(start).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				if(count>=16){
					break;
				}else{
					title.add(key);
					start = DateUtils.addWeeks(start, 1);
				}
				count++;
		 }
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		
		int  rowIndex=1;
		for (Map.Entry<String, Map<String, PsiInventoryGap>> entry: map.entrySet()) {
				String name = entry.getKey();
				Map<String,PsiInventoryGap> temp=entry.getValue();
				PsiProduct product=psiProductService.findProductByProductName(name);
				if("de".equals(country)&&"1".equals(product.getHasPower())){//不带电源
					continue;
				}
				for(int i=0;i<=12;i++){
					int m=0;
					row=sheet.createRow(rowIndex++);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(name);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("EU");
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(product.getType());
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(DictUtils.getDictLabel(positionMap.get(name), "product_position", ""));
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(product.getProducePeriod());
					Integer safe=0;
					if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getPeriod()!=null){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(stockMap.get(name).getPeriod()-product.getProducePeriod());
					}else{
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getSafeDay()!=null){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(stockMap.get(name).getSafeDay());
					}else{
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getSafeDay()!=null){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(MathUtils.roundUp(stockMap.get(name).getSafeInventory()));
						safe=MathUtils.roundUp(stockMap.get(name).getSafeInventory());
					}else{
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(product.getMinOrderPlaced());
					
					if("de".equals(country)&&product!=null&&"0".equals(product.getHasPower())){//不带电源 泛欧   hasPower 0：不带电源 1：带电源
						if(fabMap!=null&&fabMap.get("eu")!=null&&fabMap.get("eu").get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fabMap.get("eu").get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(localMap!=null&&localMap.get("eu")!=null&&localMap.get("eu").get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(localMap.get("eu").get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(deMap!=null&&deMap.get("eu")!=null&&deMap.get("eu").get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deMap.get("eu").get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(cnMap!=null&&cnMap.get("eu")!=null&&cnMap.get("eu").get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(cnMap.get("eu").get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(poMap!=null&&poMap.get("eu")!=null&&poMap.get("eu").get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(poMap.get("eu").get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						if(i==0){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("销量预测");
							setAllValue(m,"1",row,temp);
						}else if(i==1){
							if(fbaStock!=null&&fbaStock.get(name+"_eu")!=null&&fbaStock.get(name+"_eu").getFulfillableQuantity()!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fbaStock.get(name+"_eu").getFulfillableQuantity());
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊仓");
							setAllValue(m,"2",row,temp);
						}else if(i==2){
							if(fabMap!=null&&fabMap.get("eu")!=null&&fabMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fabMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊在途");
							setAllValue(m,"3",row,temp);
						}else if(i==3){
							if(deMap!=null&&deMap.get("eu")!=null&&deMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("本地仓");
							setAllValue(m,"4",row,temp);
						}else if(i==4){
							if(localMap!=null&&localMap.get("eu")!=null&&localMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(localMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("在途");
							setAllValue(m,"5",row,temp);
						}else if(i==5){
							if(cnMap!=null&&cnMap.get("eu")!=null&&cnMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(cnMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("中国仓");
							setAllValue(m,"6",row,temp);
						}else if(i==6){
							if(poMap!=null&&poMap.get("eu")!=null&&poMap.get("eu").get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(poMap.get("eu").get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("PO");
							setAllValue(m,"7",row,temp);
						}else if(i==7){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊仓balance");
							setAllValue(m,"8",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==8){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("FBA在途balance");
							setAllValue(m,"9",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==9){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("本地仓balance");
							setAllValue(m,"10",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==10){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("在途balance");
							setAllValue(m,"11",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==11){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("中国仓balance");
							setAllValue(m,"12",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==12){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("PO balance");
							setAllValue(m,"13",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}
					}else{
						if(fabMap!=null&&fabMap.get(country)!=null&&fabMap.get(country).get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fabMap.get(country).get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(localMap!=null&&localMap.get(country)!=null&&localMap.get(country).get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(localMap.get(country).get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(deMap!=null&&deMap.get(country)!=null&&deMap.get(country).get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deMap.get(country).get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(cnMap!=null&&cnMap.get(country)!=null&&cnMap.get(country).get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(cnMap.get(country).get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						
						if(poMap!=null&&poMap.get(country)!=null&&poMap.get(country).get(name)!=null){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(poMap.get(country).get(name));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						if(i==0){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("销量预测");
							setAllValue(m,"1",row,temp);
						}else if(i==1){
							if(fbaStock!=null&&fbaStock.get(name+"_"+country)!=null&&fbaStock.get(name+"_"+country).getFulfillableQuantity()!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fbaStock.get(name+"_"+country).getFulfillableQuantity());
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊仓");
							setAllValue(m,"2",row,temp);
						}else if(i==2){
							if(fabMap!=null&&fabMap.get(country)!=null&&fabMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fabMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊在途");
							setAllValue(m,"3",row,temp);
						}else if(i==3){
							if(deMap!=null&&deMap.get(country)!=null&&deMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("本地仓");
							setAllValue(m,"4",row,temp);
						}else if(i==4){
							if(localMap!=null&&localMap.get(country)!=null&&localMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(localMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("在途");
							setAllValue(m,"5",row,temp);
						}else if(i==5){
							if(cnMap!=null&&cnMap.get(country)!=null&&cnMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(cnMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("中国仓");
							setAllValue(m,"6",row,temp);
						}else if(i==6){
							if(poMap!=null&&poMap.get(country)!=null&&poMap.get(country).get(name)!=null){
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(poMap.get(country).get(name));
							}else{
								row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("PO");
							setAllValue(m,"7",row,temp);
						}else if(i==7){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("亚马逊仓balance");
							setAllValue(m,"8",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==8){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("FBA在途balance");
							setAllValue(m,"9",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==9){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("本地仓balance");
							setAllValue(m,"10",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==10){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("在途balance");
							setAllValue(m,"11",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==11){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("中国仓balance");
							setAllValue(m,"12",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}else if(i==12){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("PO balance");
							setAllValue(m,"13",row,temp,contentStyle,contentStyle1,contentStyle2,safe);
						}
					}
				}
		}
		
		/* for (int i=1;i<rowIndex;i++) {
       	      for (int j = 0; j < title.size(); j++) {
       	    		sheet.getRow(i).getCell(j).setCellStyle(contentStyle);
			 }
         }*/
		  for (int i = 0; i < title.size(); i++) {
       		 sheet.autoSizeColumn((short)i, true);
		  }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			String fileName = "库存缺口_EU"+("0".equals(forecastType)?"销售预测方式":"周日销方式")+new SimpleDateFormat("yyyyMMdd").format(new Date());
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName+".xls");
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void setAllValue(int m,String type,HSSFRow row,Map<String,PsiInventoryGap> temp){
		if(temp.get(type)!=null){
			Integer week1= temp.get(type).getWeek1();
			if(week1!=null){
				if(week1==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week1);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week2= temp.get(type).getWeek2();
			if(week2!=null){
				if(week2==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week2);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week3= temp.get(type).getWeek3();
			if(week3!=null){
				if(week3==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week3);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week4= temp.get(type).getWeek4();
			if(week4!=null){
				if(week4==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week4);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week5= temp.get(type).getWeek5();
			if(week5!=null){
				if(week5==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week5);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week6= temp.get(type).getWeek6();
			if(week6!=null){
				if(week6==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week6);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week7= temp.get(type).getWeek7();
			if(week7!=null){
				if(week7==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week7);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week8= temp.get(type).getWeek8();
			if(week8!=null){
				if(week8==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week8);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week9= temp.get(type).getWeek9();
			if(week9!=null){
				if(week9==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week9);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week10= temp.get(type).getWeek10();
			if(week10!=null){
				if(week10==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week10);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week11= temp.get(type).getWeek11();
			if(week11!=null){
				if(week11==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week11);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week12= temp.get(type).getWeek12();
			if(week12!=null){
				if(week12==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week12);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week13= temp.get(type).getWeek13();
			if(week13!=null){
				if(week13==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week13);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week14= temp.get(type).getWeek14();
			if(week14!=null){
				if(week14==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week14);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week15= temp.get(type).getWeek15();
			if(week15!=null){
				if(week15==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week15);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			Integer week16= temp.get(type).getWeek16();
			if(week16!=null){
				if(week16==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week16);
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
		}else{
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
		}
	}
	
	
	public void setAllValue(int m,String type,HSSFRow row,Map<String,PsiInventoryGap> temp,HSSFCellStyle contentStyle,HSSFCellStyle contentStyle1,HSSFCellStyle contentStyle2,Integer safe){
		if(temp.get(type)!=null){
			Integer week1= temp.get(type).getWeek1();
			if(week1!=null){
				if(week1==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week1);
					if(week1<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week1<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week1>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			
			Integer week2= temp.get(type).getWeek2();
			if(week2!=null){
				if(week2==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week2);
					if(week2<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week2<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week2>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			
			Integer week3= temp.get(type).getWeek3();
			if(week3!=null){
				if(week3==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week3);
					if(week3<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week3<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week3>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			
			Integer week4= temp.get(type).getWeek4();
			if(week4!=null){
				if(week4==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week4);
					if(week4<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week4<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week4>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			

			Integer week5= temp.get(type).getWeek5();
			if(week5!=null){
				if(week5==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week5);
					if(week5<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week5<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week5>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			
			Integer week6= temp.get(type).getWeek6();
			if(week6!=null){
				if(week6==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week6);
					if(week6<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week6<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week6>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			
			Integer week7= temp.get(type).getWeek7();
			if(week7!=null){
				if(week7==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week7);
					if(week7<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week7<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week7>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			
			Integer week8= temp.get(type).getWeek8();
			if(week8!=null){
				if(week8==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week8);
					if(week8<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week8<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week8>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			
			Integer week9= temp.get(type).getWeek9();
			if(week9!=null){
				if(week9==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week9);
					if(week9<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week9<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week9>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			
			Integer week10= temp.get(type).getWeek10();
			if(week10!=null){
				if(week10==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week10);
					if(week10<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week10<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week10>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			
			Integer week11= temp.get(type).getWeek11();
			if(week11!=null){
				if(week11==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week11);
					if(week11<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week11<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week11>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			

			Integer week12= temp.get(type).getWeek12();
			if(week12!=null){
				if(week12==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week12);
					if(week12<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week12<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week12>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			
			Integer week13= temp.get(type).getWeek13();
			if(week13!=null){
				if(week13==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week13);
					if(week13<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week13<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week13>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			
			Integer week14= temp.get(type).getWeek14();
			if(week14!=null){
				if(week14==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week14);
					if(week14<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week14<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week14>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			

			Integer week15= temp.get(type).getWeek15();
			if(week15!=null){
				if(week15==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week15);
					if(week15<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week15<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week15>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
			

			Integer week16= temp.get(type).getWeek16();
			if(week16!=null){
				if(week16==0){
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(m-1).setCellStyle(contentStyle1);//浅
				}else{
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(week16);
					if(week16<0){
						row.getCell(m-1).setCellStyle(contentStyle2);//红
					}else if(week16<safe){
						row.getCell(m-1).setCellStyle(contentStyle1);//浅
					}else if(week16>=safe){
						row.getCell(m-1).setCellStyle(contentStyle);//深
					}
				}
			}else{
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
				row.getCell(m-1).setCellStyle(contentStyle1);//浅
			}
		}else{
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
		}
	}
}
