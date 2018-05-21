/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonFbaHealthReport;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsDetail;
import com.springrain.erp.modules.amazoninfo.service.AmazonFbaHealthReportService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonStorageMonitorService;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;

@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/amazonFbaHealthReport")
public class AmazonFbaHealthReportController extends BaseController {
	@Autowired
	private AmazonFbaHealthReportService amazonFbaHealthReportService;
	@Autowired
	private AmazonPostsDetailService amazonPostsDetailService;
	@Autowired
	private PsiProductService psiProductService;
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	@Autowired
	private PsiProductGroupUserService 		groupUserService;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	@Autowired
	private PsiProductAttributeService psiProductAttributeService;
	@Autowired
	private PsiInventoryFbaService psiInventoryFbaService;
	@Autowired
    private PsiInventoryService psiInventoryService;
	@Autowired
	private AmazonStorageMonitorService amazonStorageMonitorService;
    @Autowired
	private PsiProductTypeGroupDictService psiProductTypeGroupDictService;
	
	@RequestMapping(value = {"list", ""})
	public String list(AmazonFbaHealthReport amazonFbaHealthReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AmazonFbaHealthReport> page = new Page<AmazonFbaHealthReport>(request, response,-1);
//		if(StringUtils.isBlank(amazonFbaHealthReport.getCountry())){
//			amazonFbaHealthReport.setCountry("de");
//		}
		Date date = amazonFbaHealthReport.getCreateTime();
		if (date==null){
			date = new Date();
			Calendar cal = Calendar.getInstance();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			cal.setTime(date);       
			/*if("com,ca,mx,uk,de,it,es,fr".contains(amazonFbaHealthReport.getCountry())){
				cal.add (cal.DAY_OF_MONTH, -3); 
			}else if("jp".equals(amazonFbaHealthReport.getCountry())){
				cal.add (cal.DAY_OF_MONTH, -2); 
			}*/
			amazonFbaHealthReport.setCreateTime(cal.getTime());
		}
		page = amazonFbaHealthReportService.find(page, amazonFbaHealthReport); 
		model.addAttribute("page", page);
		return "modules/amazoninfo/amazonFbaHealthReportList";
	}
	
	
	

	@RequestMapping(value = {"export"})
	public String exports(AmazonFbaHealthReport amazonFbaHealthReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AmazonFbaHealthReport> page = new Page<AmazonFbaHealthReport>(request, response,-1);
//		if(StringUtils.isBlank(amazonFbaHealthReport.getCountry())){
//			amazonFbaHealthReport.setCountry("de");
//		}
		Date date = amazonFbaHealthReport.getCreateTime();
		if (date==null){
			date = new Date();
			Calendar cal = Calendar.getInstance();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			cal.setTime(date);       
			/*if("com,ca,mx,uk,de,it,es,fr".contains(amazonFbaHealthReport.getCountry())){
				cal.add (cal.DAY_OF_MONTH, -3); 
			}else if("jp".equals(amazonFbaHealthReport.getCountry())){
				cal.add (cal.DAY_OF_MONTH, -2); 
			}*/
			amazonFbaHealthReport.setCreateTime(cal.getTime());
		}
		page.setOrderBy("country asc");
		page = amazonFbaHealthReportService.find(page, amazonFbaHealthReport);
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		
		//组合根据sku找到对应产品线(产品type属性)的map
		Map<String, String> skuNameMap = psiProductService.getProductNameBySku();//amazonFbaHealthReportService.findSkuProductName(format.format(amazonFbaHealthReport.getCreateTime()));
		Map<String, String> typeMap = Maps.newHashMap();
		List<PsiProduct> list = psiProductService.find();
		for (PsiProduct psiProduct : list) {
			typeMap.put(psiProduct.getName(), psiProduct.getType());
		}
		List<Dict> dictList = DictUtils.getDictList("platform");
		Map<String, String> platformMap = Maps.newHashMap();
		for (Dict dict : dictList) {
			platformMap.put(dict.getValue(), dict.getLabel());
		}
		
		List<String> title=Lists.newArrayList("product model","platform","createTime","sku","fnsku","asin","production line", "product_name","condition", "sales_rank", "productGroup","total_quantity","sellable_quantity", "unsellable_quantity",
		  "age_days(0-90)","age_days(91-180)", "age_days(181-270)", "age_days(271-365)","age_days(365+)","shipped(last 24Hrs)","shipped(last 7days)","shipped(last 30days)",
		  "shipped(last 90days)","shipped(last 180days)","shipped(last 365days)","cover_week(t7)","cover_week(t30)","cover_week(t90)","cover_week(t180)","cover_week(t365)",
		  "afn_new_sellers","afn_used_sellers","country","your_price","sales_price", "afn_new_price","afn_used_price", "mfn_new_price","mfn_used_price",
		  "qty_charged","qty_long_term_storage","qty_with_removals","projected_ltsf_12_mo", "per_unit_volume","is_hazmat","in_bound_quantity", "asin_limit","inbound_recommend_quantity");
		try {
        	SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
        	Map<String,String> countryNameMap = SystemService.countryNameMap;
        	String platform = countryNameMap.get(amazonFbaHealthReport.getCountry())==null?"总计":countryNameMap.get(amazonFbaHealthReport.getCountry());
            String fileName = new SimpleDateFormat("yyyyMMdd").format(amazonFbaHealthReport.getCreateTime())+"FBA健康报表_"+platform+sdf.format(new Date()) +".xlsx";
            ExportExcel excel = new ExportExcel("FBA健康报表",title);
        	for (AmazonFbaHealthReport fba : page.getList()) {
        	    Row row = excel.addRow();
        	    int j = 0;	
        	    String productModel = "";
	            try {
	            	productModel = skuNameMap.get(fba.getSku());
				} catch (NullPointerException e) {}
	            excel.addCell(row,j++, productModel);
	            excel.addCell(row,j++,platformMap.get(fba.getCountry()));
	            excel.addCell(row,j++,fba.getCreateTime());
	            excel.addCell(row,j++,fba.getSku());
	            excel.addCell(row,j++,fba.getFnsku());
	            excel.addCell(row,j++,fba.getAsin());
	            String type = "";
	            try {
	            	type = typeMap.get(skuNameMap.get(fba.getSku()));
				} catch (NullPointerException e) {}
	            excel.addCell(row,j++,type);
	            excel.addCell(row,j++,fba.getProductName());
	            excel.addCell(row,j++,fba.getCondition());
	            excel.addCell(row,j++,fba.getSalesRank());
	            excel.addCell(row,j++,fba.getProductGroup());
	            excel.addCell(row,j++,fba.getTotalQuantity());
	            excel.addCell(row,j++,fba.getSellableQuantity());
	            excel.addCell(row,j++,fba.getUnsellableQuantity());
	            excel.addCell(row,j++,fba.getAgeDays90());
	            excel.addCell(row,j++,fba.getAgeDays180());
	            excel.addCell(row,j++,fba.getAgeDays270());
	            excel.addCell(row,j++,fba.getAgeDays365());
	            excel.addCell(row,j++,fba.getAgePlusDays365());
	            excel.addCell(row,j++,fba.getShippedHrs24());
	            excel.addCell(row,j++,fba.getShippedDays7());
	            excel.addCell(row,j++,fba.getShippedDays30());
	            excel.addCell(row,j++,fba.getShippedDays90());
	            excel.addCell(row,j++,fba.getShippedDays180());
	            excel.addCell(row,j++,fba.getShippedDays365());
	            excel.addCell(row,j++,fba.getWeeksCover7());
	            excel.addCell(row,j++,fba.getWeeksCover30());
	            excel.addCell(row,j++,fba.getWeeksCover90());
	            excel.addCell(row,j++,fba.getWeeksCover180());
	            excel.addCell(row,j++,fba.getWeeksCover365());
	            excel.addCell(row,j++,fba.getAfnNewSellers());
	            excel.addCell(row,j++,fba.getAfnUsedSellers());
	            excel.addCell(row,j++,fba.getCountry());
	            excel.addCell(row,j++,fba.getYourPrice());
	            excel.addCell(row,j++,fba.getSalesPrice());
	            excel.addCell(row,j++,fba.getAfnNewPrice());
	            excel.addCell(row,j++,fba.getAfnUsedPrice());
	            excel.addCell(row,j++,fba.getMfnNewPrice());
	            excel.addCell(row,j++,fba.getMfnUsedPrice());
	            excel.addCell(row,j++,fba.getQtyCharged());
	            excel.addCell(row,j++,fba.getQtyLongTermStorage());
	            excel.addCell(row,j++,fba.getQtyWithRemovals());
	            excel.addCell(row,j++,fba.getProjectedMo12());
	            excel.addCell(row,j++,fba.getPerUnitVolume());
	            excel.addCell(row,j++,fba.getIsHazmat());
	            excel.addCell(row,j++,fba.getInBoundQuantity());
	            excel.addCell(row,j++,fba.getAsinLimit());
	            excel.addCell(row,j++,fba.getInboundRecommendQuantity());
			}
            excel.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	@RequestMapping(value = {"export2"})
	public String exports2(AmazonFbaHealthReport amazonFbaHealthReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AmazonFbaHealthReport> page = new Page<AmazonFbaHealthReport>(request, response,-1);
		Date date = amazonFbaHealthReport.getCreateTime();
		if (date==null){
			date = new Date();
			Calendar cal = Calendar.getInstance();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			cal.setTime(date);       
			amazonFbaHealthReport.setCreateTime(cal.getTime());
		}
		page.setOrderBy("country asc");
		page = amazonFbaHealthReportService.find(page, amazonFbaHealthReport);
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		Map<String,AmazonPostsDetail> postsMap=amazonPostsDetailService.getPostsByCountry(amazonFbaHealthReport.getCountry());
		//组合根据sku找到对应产品线(产品type属性)的map
		Map<String, String> skuNameMap = amazonFbaHealthReportService.findSkuProductName(format.format(amazonFbaHealthReport.getCreateTime()));
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
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0000000"));
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
		String currency="($)";
		String unit="(立方英尺)";
		 if("de,fr,it,es".contains(amazonFbaHealthReport.getCountry())){//每立方米500欧  >12 每立方米1000欧
			 currency="(€)";
			 unit="(立方米)";
       	}else if("uk".equals(amazonFbaHealthReport.getCountry())){//>12 每体积（立方米） * 882.50英镑
	       	 currency="(￡)";
			 unit="(立方米)";
       	}else if("jp".equals(amazonFbaHealthReport.getCountry())){//>12 每体积（立方米）x 174857 日元 
	       	 currency="(￥)";
			 unit="(立方米)";
       	}
		List<String> title=Lists.newArrayList("市场","型号","sku","asin","体积"+unit,"超期(181-270)","超期(271-365)","超期(365+)","6-12月费用"+currency,"超过12月费用"+currency);
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		int  rowIndex=1;
        for (AmazonFbaHealthReport fba : page.getList()) {
        	        row=sheet.createRow(rowIndex++);
             	    int j = 0;	
             	    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(fba.getCountry())?"US":fba.getCountry().toUpperCase());
     	            String productModel = "";
     	            try {
     	            	productModel = skuNameMap.get(fba.getSku());
     				} catch (NullPointerException e) {}
     	           row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(productModel);
     	           row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(fba.getSku());
     	           row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(fba.getAsin());
     	           
     	            Double volume=0d;
     	            Float  price1=0f;
     	            Float  price2=0f;
     	            if(fba.getPerUnitVolume()>0){
     	            	if("com".equals(amazonFbaHealthReport.getCountry())){//每体积（立方英尺）*11.25 美元    >12 每体积（立方英尺）*22.50 美元
      	            		volume=fba.getPerUnitVolume()*1.0d;
      	            		price1=11.25f;
      	            		price2=22.50f;
      	            	}else if("de,fr,it,es".contains(amazonFbaHealthReport.getCountry())){//每立方米500欧  >12 每立方米1000欧
      	            		volume=fba.getPerUnitVolume()/1000000d;
      	            		price1=500f;
      	            		price2=1000f;
      	            	}else if("uk".equals(amazonFbaHealthReport.getCountry())){//>12 每体积（立方米） * 882.50英镑
      	            		volume=fba.getPerUnitVolume()/1000000d;
      	            		price2=882.50f;
      	            	}else if("jp".equals(amazonFbaHealthReport.getCountry())){//>12 每体积（立方米）x 174857 日元 
      	            		volume=fba.getPerUnitVolume()/1000000d;
      	            		price2=174857f;
      	            	}
     	            }else{
     	            	 if(postsMap!=null&&postsMap.get(fba.getAsin())!=null){
          	            	AmazonPostsDetail detail=postsMap.get(fba.getAsin());
          	            	volume=detail.getPackageHeight()*detail.getPackageWidth()*detail.getPackageLength()*1d;
          	            	if("com".equals(amazonFbaHealthReport.getCountry())){//每体积（立方英尺）*11.25 美元    >12 每体积（立方英尺）*22.50 美元
          	            		volume=0.0005787*volume;
          	            		price1=11.25f;
          	            		price2=22.50f;
          	            	}else if("de,fr,it,es".contains(amazonFbaHealthReport.getCountry())){//每立方米500欧  >12 每立方米1000欧
          	            		volume=0.0000164*volume;
          	            		price1=500f;
          	            		price2=1000f;
          	            	}else if("uk".equals(amazonFbaHealthReport.getCountry())){//>12 每体积（立方米） * 882.50英镑
          	            		volume=0.0000164*volume;
          	            		price2=882.50f;
          	            	}else if("jp".equals(amazonFbaHealthReport.getCountry())){//>12 每体积（立方米）x 174857 日元 
          	            		volume=0.0000164*volume;
          	            		price2=174857f;
          	            	}
          	            }
     	            }
     	           
     	           row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(volume);
     	           row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(fba.getAgeDays270());
     	           row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(fba.getAgeDays365());
     	           row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(fba.getAgePlusDays365());
     	            
     	            if("de,fr,it,es,com".contains(amazonFbaHealthReport.getCountry())){
     	            	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(MathUtils.roundUp((fba.getAgeDays270()+fba.getAgeDays365())*volume*price1));
     	            	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(MathUtils.roundUp(fba.getAgePlusDays365()*volume*price2));
     	            }else{
     	            	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
     	            	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(MathUtils.roundUp(fba.getAgePlusDays365()*volume*price2));
     	            }
			}
        
        for (int i=1;i<rowIndex;i++) {
     	      for (int j = 0; j < title.size(); j++) {
     	    	 if(j==5){
     	    		sheet.getRow(i).getCell(j).setCellStyle(cellStyle);
     	    	 }else{
     	    		sheet.getRow(i).getCell(j).setCellStyle(contentStyle);
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
			String fileName = "仓储费用报表" + sdf.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value = "findOverFbaProduct")
	public String findOverFbaProduct(AmazonFbaHealthReport amazonFbaHealthReport,Model model){
		if(amazonFbaHealthReport.getCreateTime()==null){
			Date date=DateUtils.addMonths(new Date(),-1);
			amazonFbaHealthReport.setCreateTime(DateUtils.getLastDayOfMonth(date));
		}
		Map<String,Map<String,AmazonFbaHealthReport>> map=amazonFbaHealthReportService.findWarnningInventory(amazonFbaHealthReport); 
		Map<String,String> nameAndLineMap=groupDictService.getLineByName();//产品名-产品线ID
		model.addAttribute("nameAndLineMap", nameAndLineMap);
		List<PsiProductTypeGroupDict> lineList=groupDictService.getAllList();
		model.addAttribute("lineList", lineList);
		model.addAttribute("amazonFbaHealthReport", amazonFbaHealthReport);
		Map<String,Map<String,String>> groupMap=groupUserService.getSingleGroupUser();
		Map<String,Map<String,AmazonFbaHealthReport>> inventoryMap=Maps.newHashMap();
		model.addAttribute("groupMap", groupMap);
		Map<String,Map<String,PsiProductEliminate>> isNewMap=psiProductEliminateService.findAllByNameAndCountry();
		//产品名_颜色 、国家   isSale>0?"在售":"淘汰"  isNew>0?"新品":"普通品" 
		model.addAttribute("isNewMap", isNewMap);
		
		if(map!=null&&map.size()>0){
			 for (Map.Entry<String, Map<String, AmazonFbaHealthReport>> entry : map.entrySet()) { 
			    String country=entry.getKey();
				if("en,nonEn".contains(country)){
					Map<String,AmazonFbaHealthReport> temp=inventoryMap.get(country);
					if(temp==null){
						temp=Maps.newHashMap();
						inventoryMap.put(country,temp);
					}
					
					Map<String,AmazonFbaHealthReport> totalTemp=inventoryMap.get("total");
					if(totalTemp==null){
						totalTemp=Maps.newHashMap();
						inventoryMap.put("total",totalTemp);
					}
					
					
					Map<String,AmazonFbaHealthReport> countryMap=entry.getValue();
					
					
				    for (Map.Entry<String, AmazonFbaHealthReport> entryRs : countryMap.entrySet()) { 
					     String name=entryRs.getKey();
						 if(nameAndLineMap.get(name)!=null){
							 String lineId=nameAndLineMap.get(name);
							 AmazonFbaHealthReport curReport=entryRs.getValue();
							 
							 AmazonFbaHealthReport report=temp.get(lineId);
							 if(report==null){
								 report = new AmazonFbaHealthReport(0,0);
								 temp.put(lineId,report);
							 }
							 report.setAgeDays365(report.getAgeDays365()+curReport.getAgeDays365());
							 report.setAgePlusDays365(report.getAgePlusDays365()+curReport.getAgePlusDays365());
							 
							 AmazonFbaHealthReport totalReport=temp.get("total");
							 if(totalReport==null){
								 totalReport = new AmazonFbaHealthReport(0,0);
								 temp.put("total",totalReport);
							 }
							 totalReport.setAgeDays365(totalReport.getAgeDays365()+curReport.getAgeDays365());
							 totalReport.setAgePlusDays365(totalReport.getAgePlusDays365()+curReport.getAgePlusDays365());
							 
							 
							 
							 AmazonFbaHealthReport report1=totalTemp.get(lineId);
							 if(report1==null){
								 report1 = new AmazonFbaHealthReport(0,0);
								 totalTemp.put(lineId,report1);
							 }
							 report1.setAgeDays365(report1.getAgeDays365()+curReport.getAgeDays365());
						     report1.setAgePlusDays365(report1.getAgePlusDays365()+curReport.getAgePlusDays365());
							 
							 
							 AmazonFbaHealthReport totalReport2=totalTemp.get("total");
							 if(totalReport2==null){
								 totalReport2 = new AmazonFbaHealthReport(0,0);
								 totalTemp.put("total",totalReport2);
							 }
							 totalReport2.setAgeDays365(totalReport2.getAgeDays365()+curReport.getAgeDays365());
						     totalReport2.setAgePlusDays365(totalReport2.getAgePlusDays365()+curReport.getAgePlusDays365());
							   
							 
							 
						 }
					}
					
				}
			}
		}
		model.addAttribute("lineMap", inventoryMap);
		model.addAttribute("detailMap", map);
		return "modules/psi/countOverFbaProduct";
	}
	
	
	@RequestMapping(value = "expOverFbaProduct")
	public String expOverFbaProduct(AmazonFbaHealthReport amazonFbaHealthReport,Model model, HttpServletRequest request, HttpServletResponse response){
		Map<String,Map<String,AmazonFbaHealthReport>> map=amazonFbaHealthReportService.findWarnningInventory(amazonFbaHealthReport); 
		Map<String,PsiProductTypeGroupDict> nameAndLineMap=groupDictService.getLineByProductName();
		Map<String,Map<String,String>> groupMap=groupUserService.getSingleGroupUser();
		Map<String,Map<String,PsiProductEliminate>> isNewMap=psiProductEliminateService.findAllByNameAndCountry();
		//产品名_颜色 、国家   isSale>0?"在售":"淘汰"  isNew>0?"新品":"普通品" 
		
		List<String> title=Lists.newArrayList("产品","产品品线","产品定位","新品","日期","市场","运营负责人","6-12库存","12月以上库存");
        HSSFWorkbook wb = new HSSFWorkbook();
  		HSSFSheet sheet = wb.createSheet();
		
		int  rowIndex=0;
		HSSFRow row = sheet.createRow(rowIndex++);
  		HSSFCell cell = null;
  		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short) i);
		}
  		String date=new SimpleDateFormat("yyyyMMdd").format(amazonFbaHealthReport.getCreateTime());
        if(map!=null&&map.size()>0){
        	for (Map.Entry<String, Map<String, AmazonFbaHealthReport>> entry : map.entrySet()) { 
        	    String country=entry.getKey();
        		if("en,nonEn".contains(country)){
        			continue;
        		}
        		Map<String,AmazonFbaHealthReport> nameMap=entry.getValue();
        		for (Map.Entry<String, AmazonFbaHealthReport> entryMap : nameMap.entrySet()) { 
        		        String name=entryMap.getKey();
        			    AmazonFbaHealthReport report=entryMap.getValue();
        				row = sheet.createRow(rowIndex++);
        				int j=0;
            			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
            			if(nameAndLineMap.get(name)!=null){
        					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(nameAndLineMap.get(name).getName());
        				}else{
        					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
        				}
            			if(isNewMap.get(name)!=null&&isNewMap.get(name).get(country)!=null){
            				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("0".equals(isNewMap.get(name).get(country).getIsSale())?"淘汰":"在售");
            				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("0".equals(isNewMap.get(name).get(country).getIsNew())?"普通品":"新品");
            			}else{
            				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
            				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
            			}
            			
            			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
            			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
            			if(nameAndLineMap.get(name)!=null){
        					 String lineId=nameAndLineMap.get(name).getId();
        					 if(groupMap.get(lineId)!=null&&groupMap.get(lineId).get(country)!=null){
        						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(groupMap.get(lineId).get(country).split(",")[1]);
        					 }else{
        						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
        					 }
        				}else{
        					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
        				}
            			
            			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAgeDays365());
            			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAgePlusDays365());
				}
        		
        		
			}
        }
        for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i);
		}

        try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "分产品FBA库存" + sdf.format(new Date()) + ".xls";
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
	
	
	
	@RequestMapping(value = {"exportAllStorage"})
    public String priceExport(HttpServletRequest request, HttpServletResponse response,
            Model model) throws UnsupportedEncodingException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet1 = wb.createSheet();
        HSSFSheet sheet2 = wb.createSheet();
        HSSFSheet sheet3 = wb.createSheet();
        HSSFSheet sheet4 = wb.createSheet();
        wb.setSheetName(0, "库存365天以上");
        wb.setSheetName(1, "库存270天到365天");
        wb.setSheetName(2, "库存180天到270天");
        wb.setSheetName(3, "库存90天到180天");
        HSSFRow row1 = null;
        HSSFRow row2 = null;
        HSSFRow row3 = null;
        HSSFRow row4 = null;
        
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        sheet1.autoSizeColumn((short)0); //调整第一列宽度
        sheet1.autoSizeColumn((short)1); //调整第二列宽度
        sheet1.autoSizeColumn((short)2); //调整第三列宽度
        sheet1.autoSizeColumn((short)10);
        String[] title = {"产品","COM","DE","UK","CA","JP","FR", "IT","ES","总计","产品线","类型"};

        style.setFillBackgroundColor(HSSFColor.ORANGE.index);
        style.setFillForegroundColor(HSSFColor.ORANGE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // 设置Excel中的边框(表头的边框)
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
        style.setBottomBorderColor(HSSFColor.BLACK.index);

        style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
        style.setLeftBorderColor(HSSFColor.BLACK.index);

        style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
        style.setRightBorderColor(HSSFColor.BLACK.index);

        style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
        style.setTopBorderColor(HSSFColor.BLACK.index);
        style.setFillBackgroundColor(HSSFColor.BLACK.index);
        
        HSSFCellStyle style1 = wb.createCellStyle();
        style1.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        // 设置字体
        HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 16); // 字体高度
        font.setFontName(" 黑体 "); // 字体
        font.setBoldweight((short) 16);
        style.setFont(font);
        HSSFCell cell = null;
       /* for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
            sheet.autoSizeColumn((short) i);
        }*/
        
        Map<String, Double> volume = amazonStorageMonitorService.getVolume();
        Map<String, String> lineNameByName = psiProductTypeGroupDictService.getLineNameByName();
        LinkedHashMap<String, LinkedHashMap<String, List<String>>> productStorag = amazonStorageMonitorService.getProductStorag();
        List<PsiProduct> findAllOnSale = psiProductService.findAllOnSale();

        List<String> onSaleProduct = new ArrayList<String>();
        for(int k=0;k<findAllOnSale.size();k++){
             PsiProduct psiProduct = findAllOnSale.get(k);
             if(psiProduct.getColor().contains(",")){
                    String arr[] = psiProduct.getColor().toString().split(",");
                    for(int i=0;i<arr.length;i++){
                        onSaleProduct.add(psiProduct.getBrand()+" "+psiProduct.getModel()+"_"+arr[i]);
                    }
                }else if("".equals(psiProduct.getColor())){
                    onSaleProduct.add(psiProduct.getBrand()+" "+psiProduct.getModel());
                }else{
                    onSaleProduct.add(psiProduct.getBrand()+" "+psiProduct.getModel()+"_"+psiProduct.getColor());
                }
        }
        for(int i=3;i>-1;i--){
            Cell cell4 = null ;
            Cell cell3 = null;;
            Cell cell2 = null ;
            Cell cell1 = null ;
            int excelNo = 0;
            if(i==0){
                row4 = sheet4.createRow(excelNo++);
                cell4 = row4.createCell(0, Cell.CELL_TYPE_STRING);
                cell4.setCellStyle(style);
            }
            if(i==1){
                row3 = sheet3.createRow(excelNo++);
                cell3 = row3.createCell(0, Cell.CELL_TYPE_STRING);
                cell3.setCellStyle(style);
            }
            if(i==2){
                row2 = sheet2.createRow(excelNo++);
                cell2 = row2.createCell(0, Cell.CELL_TYPE_STRING);
                cell2.setCellStyle(style);
            }
            if(i==3){
                row1 = sheet1.createRow(excelNo++);
                cell1 = row1.createCell(0, Cell.CELL_TYPE_STRING);
                cell1.setCellStyle(style); 
            }

            if(i==0){
                cell4.setCellValue("库存90天到180天");
            }else if(i==1){
                cell3.setCellValue("库存180天到270天");
                
            }else if(i==2){
                cell2.setCellValue("库存270天到365天");
            }else{
                cell1.setCellValue("库存365天以上");
            }
            
            if(i==0){
                row4 = sheet4.createRow(excelNo++);
                for (int j = 0; j < title.length; j++) {
                    cell = row4.createCell(j);
                    cell.setCellValue(title[j]);
                    cell.setCellStyle(style);          
                    //sheet1.autoSizeColumn((short) j);
                }
            }else if(i==1){
                row3 = sheet3.createRow(excelNo++);
                for (int j = 0; j < title.length; j++) {
                    cell = row3.createCell(j);
                    cell.setCellValue(title[j]);
                    cell.setCellStyle(style);          
                   // sheet1.autoSizeColumn((short) j);
                }
            }else if(i==2){
                row2 = sheet2.createRow(excelNo++);
                for (int j = 0; j < title.length; j++) {
                    cell = row2.createCell(j);
                    cell.setCellValue(title[j]);
                    cell.setCellStyle(style);          
                    //sheet1.autoSizeColumn((short) j);
                }
            }else{
                row1 = sheet1.createRow(excelNo++);
                for (int j = 0; j < title.length; j++) {
                    cell = row1.createCell(j);
                    cell.setCellValue(title[j]);
                    cell.setCellStyle(style);          
                    //sheet1.autoSizeColumn((short) j);
                }
            }
            
            int com=0,de=0,uk=0,ca=0,jp=0,fr=0,it=0,es=0;
            double comV=0,deV=0,ukV=0,caV=0,jpV=0,frV=0,itV=0,esV=0;
            LinkedHashMap<String,List<String>> map = new LinkedHashMap<String, List<String>>();
            for (Entry<String, LinkedHashMap<String, List<String>>> entry : productStorag.entrySet()) { 

                LinkedHashMap<String, List<String>> entry1 = entry.getValue();
                StringBuilder sb1 = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                StringBuilder sb3 = new StringBuilder();
                StringBuilder sb4 = new StringBuilder();
                StringBuilder sb5 = new StringBuilder();
                StringBuilder sb6 = new StringBuilder();
                StringBuilder sb7 = new StringBuilder();
                StringBuilder sb8 = new StringBuilder();
                Integer total = 0;
                boolean contentNotNull = false;
                for (String key : entry1.keySet()) {
                    String key2 = entry.getKey();
                    Double v=0d;
                    if( volume.get(key2)!=null){
                        v = volume.get(key2);
                    }
                    
                    
                    if(!"0".equals(entry1.get(key).get(i))){
                        total +=  Integer.parseInt(entry1.get(key).get(i));
                        contentNotNull = true;
                        if(key.equals("com")){
                            sb1.append(entry1.get(key).get(i));
                            com += Integer.parseInt(entry1.get(key).get(i));
                            comV+=v*Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("de")){
                            sb2.append(entry1.get(key).get(i));
                            deV+=v*Integer.parseInt(entry1.get(key).get(i));
                            de += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("uk")){
                            sb3.append(entry1.get(key).get(i));
                            ukV+=v*Integer.parseInt(entry1.get(key).get(i));
                            uk += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("ca")){
                            sb4.append(entry1.get(key).get(i));
                            caV+=v*Integer.parseInt(entry1.get(key).get(i));
                            ca += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("jp")){
                            sb5.append(entry1.get(key).get(i));
                            jpV+=v*Integer.parseInt(entry1.get(key).get(i));
                            jp += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("fr")){
                            sb6.append(entry1.get(key).get(i));
                            frV+=v*Integer.parseInt(entry1.get(key).get(i));
                            fr += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("it")){
                            sb7.append(entry1.get(key).get(i));
                            itV+=v*Integer.parseInt(entry1.get(key).get(i));
                            it += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("es")){
                            sb8.append(entry1.get(key).get(i));
                            esV+=v*Integer.parseInt(entry1.get(key).get(i));
                            es += Integer.parseInt(entry1.get(key).get(i));
                        }
                    }
                } 
                if(contentNotNull){
                    String lineName= lineNameByName.get(entry.getKey())!=null ? lineNameByName.get(entry.getKey()):"";

                    if(onSaleProduct.contains(entry.getKey())){
                        if(i==0){
                            row4 = sheet4.createRow(excelNo++);
                            row4.setRowStyle(style1);
                            
                            row4.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(entry.getKey());
                            if(sb1 != null && !"".equals(sb1.toString())){
                                row4.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb1.toString()));
                            }else{
                                row4.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(sb1.toString());
                            }
                            if(sb2 != null && !"".equals(sb2.toString())){
                                row4.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb2.toString()));
                            }else{
                                row4.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(sb2.toString());
                            }
                            if(sb3 != null && !"".equals(sb3.toString())){
                                row4.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb3.toString()));
                            }else{
                                row4.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(sb3.toString());
                            }
                            if(sb4 != null && !"".equals(sb4.toString())){
                                row4.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb4.toString()));
                            }else{
                                row4.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(sb4.toString());
                            }
                            if(sb5 != null && !"".equals(sb5.toString())){
                                row4.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb5.toString()));
                            }else{
                                row4.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(sb5.toString());
                            }
                            if(sb6 != null && !"".equals(sb6.toString())){
                                row4.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb6.toString()));
                            }else{
                                row4.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(sb6.toString());
                            }
                            if(sb7 != null && !"".equals(sb7.toString())){
                                row4.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb7.toString()));
                            }else{
                                row4.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(sb7.toString());
                            }
                            if(sb8 != null && !"".equals(sb8.toString())){
                                row4.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb8.toString()));
                            }else{
                                row4.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(sb8.toString());
                            }
                            
                            row4.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(total);
                            row4.createCell(10, Cell.CELL_TYPE_STRING).setCellValue(lineName);
                            row4.createCell(11, Cell.CELL_TYPE_STRING).setCellValue("非淘汰" );
                        }
                        if(i==1){
                            row3 = sheet3.createRow(excelNo++);
                            row3.setRowStyle(style1);
                            row3.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(entry.getKey());
                            if(sb1 != null && !"".equals(sb1.toString())){
                                row3.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb1.toString()));
                            }else{
                                row3.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(sb1.toString());
                            }
                            if(sb2 != null && !"".equals(sb2.toString())){
                                row3.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb2.toString()));
                            }else{
                                row3.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(sb2.toString());
                            }
                            if(sb3 != null && !"".equals(sb3.toString())){
                                row3.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb3.toString()));
                            }else{
                                row3.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(sb3.toString());
                            }
                            if(sb4 != null && !"".equals(sb4.toString())){
                                row3.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb4.toString()));
                            }else{
                                row3.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(sb4.toString());
                            }
                            if(sb5 != null && !"".equals(sb5.toString())){
                                row3.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb5.toString()));
                            }else{
                                row3.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(sb5.toString());
                            }
                            if(sb6 != null && !"".equals(sb6.toString())){
                                row3.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb6.toString()));
                            }else{
                                row3.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(sb6.toString());
                            }
                            if(sb7 != null && !"".equals(sb7.toString())){
                                row3.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb7.toString()));
                            }else{
                                row3.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(sb7.toString());
                            }
                            if(sb8 != null && !"".equals(sb8.toString())){
                                row3.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb8.toString()));
                            }else{
                                row3.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(sb8.toString());
                            }
                            row3.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(total);
                            row3.createCell(10, Cell.CELL_TYPE_STRING).setCellValue(lineName);
                            row3.createCell(11, Cell.CELL_TYPE_STRING).setCellValue("非淘汰" );
                        }
                        if(i==2){
                            row2 = sheet2.createRow(excelNo++);
                            row2.setRowStyle(style1);
                            row2.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(entry.getKey());
                            if(sb1 != null && !"".equals(sb1.toString())){
                                row2.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb1.toString()));
                            }else{
                                row2.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(sb1.toString());
                            }
                            if(sb2 != null && !"".equals(sb2.toString())){
                                row2.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb2.toString()));
                            }else{
                                row2.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(sb2.toString());
                            }
                            if(sb3 != null && !"".equals(sb3.toString())){
                                row2.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb3.toString()));
                            }else{
                                row2.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(sb3.toString());
                            }
                            if(sb4 != null && !"".equals(sb4.toString())){
                                row2.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb4.toString()));
                            }else{
                                row2.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(sb4.toString());
                            }
                            if(sb5 != null && !"".equals(sb5.toString())){
                                row2.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb5.toString()));
                            }else{
                                row2.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(sb5.toString());
                            }
                            if(sb6 != null && !"".equals(sb6.toString())){
                                row2.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb6.toString()));
                            }else{
                                row2.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(sb6.toString());
                            }
                            if(sb7 != null && !"".equals(sb7.toString())){
                                row2.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb7.toString()));
                            }else{
                                row2.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(sb7.toString());
                            }
                            if(sb8 != null && !"".equals(sb8.toString())){
                                row2.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb8.toString()));
                            }else{
                                row2.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(sb8.toString());
                            }
                            row2.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(total);
                            row2.createCell(10, Cell.CELL_TYPE_STRING).setCellValue(lineName);
                            row2.createCell(11, Cell.CELL_TYPE_STRING).setCellValue("非淘汰" );
                        }
                        if(i==3){
                            row1 = sheet1.createRow(excelNo++);
                            row1.setRowStyle(style1);
                            row1.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(entry.getKey());
                            if(sb1 != null && !"".equals(sb1.toString())){
                                row1.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb1.toString()));
                            }else{
                                row1.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(sb1.toString());
                            }
                            if(sb2 != null && !"".equals(sb2.toString())){
                                row1.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb2.toString()));
                            }else{
                                row1.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(sb2.toString());
                            }
                            if(sb3 != null && !"".equals(sb3.toString())){
                                row1.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb3.toString()));
                            }else{
                                row1.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(sb3.toString());
                            }
                            if(sb4 != null && !"".equals(sb4.toString())){
                                row1.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb4.toString()));
                            }else{
                                row1.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(sb4.toString());
                            }
                            if(sb5 != null && !"".equals(sb5.toString())){
                                row1.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb5.toString()));
                            }else{
                                row1.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(sb5.toString());
                            }
                            if(sb6 != null && !"".equals(sb6.toString())){
                                row1.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb6.toString()));
                            }else{
                                row1.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(sb6.toString());
                            }
                            if(sb7 != null && !"".equals(sb7.toString())){
                                row1.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb7.toString()));
                            }else{
                                row1.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(sb7.toString());
                            }
                            if(sb8 != null && !"".equals(sb8.toString())){
                                row1.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(sb8.toString()));
                            }else{
                                row1.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(sb8.toString());
                            }
                            row1.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(total);
                            row1.createCell(10, Cell.CELL_TYPE_STRING).setCellValue(lineName);
                            row1.createCell(11, Cell.CELL_TYPE_STRING).setCellValue("非淘汰" );
                        }
                        
                    }else{
                        List<String> list = new ArrayList<String>();
                        list.add(sb1!=null ? sb1.toString():"" );
                        list.add(sb2!=null ? sb2.toString():"" );
                        list.add(sb3!=null ? sb3.toString():"" );
                        list.add(sb4!=null ? sb4.toString():"" );
                        list.add(sb5!=null ? sb5.toString():"" );
                        list.add(sb6!=null ? sb6.toString():"" );
                        list.add(sb7!=null ? sb7.toString():"" );
                        list.add(sb8!=null ? sb8.toString():"");
                        list.add(String.valueOf(total));
                        list.add(lineName );
                        list.add("淘汰" );
                        map.put(entry.getKey(), list);
                    }
                }
            }
            if(map.size()>0){
                for(Entry<String, List<String>> entry : map.entrySet()){
                    List<String> value = entry.getValue();
                   
                    if(i==0){
                        row4 = sheet4.createRow(excelNo++);
                        row4.setRowStyle(style1);
                        row4.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(entry.getKey());
                        if(!"".equals(value.get(0))){
                            row4.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(0)));
                        }else{
                            row4.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(0));
                        }
                        if(!"".equals(value.get(1))){
                            row4.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(1)));
                        }else{
                            row4.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(1));
                        }
                        if(!"".equals(value.get(2))){
                            row4.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(2)));
                        }else{
                            row4.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(2));
                        }
                        if(!"".equals(value.get(3))){
                            row4.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(3)));
                        }else{
                            row4.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(3));
                        }
                        if(!"".equals(value.get(4))){
                            row4.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(4)));
                        }else{
                            row4.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(4));
                        }
                        if(!"".equals(value.get(5))){
                            row4.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(5)));
                        }else{
                            row4.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(5));
                        }
                        if(!"".equals(value.get(6))){
                            row4.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(6)));
                        }else{
                            row4.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(6));
                        }
                        if(!"".equals(value.get(7))){
                            row4.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(7)));
                        }else{
                            row4.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(7));
                        }
                        if(!"".equals(value.get(8))){
                            row4.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(8)));
                        }else{
                            row4.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(8));
                        }
                        
                        row4.createCell(10, Cell.CELL_TYPE_STRING).setCellValue(value.get(9));
                        row4.createCell(11, Cell.CELL_TYPE_STRING).setCellValue(value.get(10));
                    }
                    if(i==1){
                        row3 = sheet3.createRow(excelNo++);
                        row3.setRowStyle(style1);
                        row3.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(entry.getKey());
                        if(!"".equals(value.get(0))){
                            row3.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(0)));
                        }else{
                            row3.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(0));
                        }
                        if(!"".equals(value.get(1))){
                            row3.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(1)));
                        }else{
                            row3.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(1));
                        }
                        if(!"".equals(value.get(2))){
                            row3.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(2)));
                        }else{
                            row3.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(2));
                        }
                        if(!"".equals(value.get(3))){
                            row3.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(3)));
                        }else{
                            row3.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(3));
                        }
                        if(!"".equals(value.get(4))){
                            row3.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(4)));
                        }else{
                            row3.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(4));
                        }
                        if(!"".equals(value.get(5))){
                            row3.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(5)));
                        }else{
                            row3.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(5));
                        }
                        if(!"".equals(value.get(6))){
                            row3.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(6)));
                        }else{
                            row3.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(6));
                        }
                        if(!"".equals(value.get(7))){
                            row3.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(7)));
                        }else{
                            row3.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(7));
                        }
                        if(!"".equals(value.get(8))){
                            row3.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(8)));
                        }else{
                            row3.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(8));
                        }
                        row3.createCell(10, Cell.CELL_TYPE_STRING).setCellValue(value.get(9));
                        row3.createCell(11, Cell.CELL_TYPE_STRING).setCellValue(value.get(10));
                    }
                    if(i==2){
                        row2 = sheet2.createRow(excelNo++);
                        row2.setRowStyle(style1);
                        row2.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(entry.getKey());
                        if(!"".equals(value.get(0))){
                            row2.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(0)));
                        }else{
                            row2.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(0));
                        }
                        if(!"".equals(value.get(1))){
                            row2.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(1)));
                        }else{
                            row2.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(1));
                        }
                        if(!"".equals(value.get(2))){
                            row2.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(2)));
                        }else{
                            row2.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(2));
                        }
                        if(!"".equals(value.get(3))){
                            row2.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(3)));
                        }else{
                            row2.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(3));
                        }
                        if(!"".equals(value.get(4))){
                            row2.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(4)));
                        }else{
                            row2.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(4));
                        }
                        if(!"".equals(value.get(5))){
                            row2.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(5)));
                        }else{
                            row2.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(5));
                        }
                        if(!"".equals(value.get(6))){
                            row2.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(6)));
                        }else{
                            row2.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(6));
                        }
                        if(!"".equals(value.get(7))){
                            row2.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(7)));
                        }else{
                            row2.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(7));
                        }
                        if(!"".equals(value.get(8))){
                            row2.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(8)));
                        }else{
                            row2.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(8));
                        }
                        row2.createCell(10, Cell.CELL_TYPE_STRING).setCellValue(value.get(9));
                        row2.createCell(11, Cell.CELL_TYPE_STRING).setCellValue(value.get(10));
                    }
                    if(i==3){
                        row1 = sheet1.createRow(excelNo++);
                        row1.setRowStyle(style1);
                        row1.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(entry.getKey());
                        if(!"".equals(value.get(0))){
                            row1.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(0)));
                        }else{
                            row1.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(0));
                        }
                        if(!"".equals(value.get(1))){
                            row1.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(1)));
                        }else{
                            row1.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(1));
                        }
                        if(!"".equals(value.get(2))){
                            row1.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(2)));
                        }else{
                            row1.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(2));
                        }
                        if(!"".equals(value.get(3))){
                            row1.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(3)));
                        }else{
                            row1.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(3));
                        }
                        if(!"".equals(value.get(4))){
                            row1.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(4)));
                        }else{
                            row1.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(4));
                        }
                        if(!"".equals(value.get(5))){
                            row1.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(5)));
                        }else{
                            row1.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(5));
                        }
                        if(!"".equals(value.get(6))){
                            row1.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(6)));
                        }else{
                            row1.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(6));
                        }
                        if(!"".equals(value.get(7))){
                            row1.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(7)));
                        }else{
                            row1.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(7));
                        }
                        if(!"".equals(value.get(8))){
                            row1.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(value.get(8)));
                        }else{
                            row1.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(value.get(8));
                        }
                        row1.createCell(10, Cell.CELL_TYPE_STRING).setCellValue(value.get(9));
                        row1.createCell(11, Cell.CELL_TYPE_STRING).setCellValue(value.get(10));
                    }
                   
                }
            }
            DecimalFormat   df   =new DecimalFormat("#0.0000"); 
            if(i==0){
                row4 = sheet4.createRow(excelNo++);
                row4.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("库存总数");
                row4.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(com);
                row4.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(de);
                row4.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(uk);
                row4.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(ca);
                row4.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(jp );
                row4.createCell(6, Cell.CELL_TYPE_STRING).setCellValue(fr);
                row4.createCell(7, Cell.CELL_TYPE_STRING).setCellValue(it);
                row4.createCell(8, Cell.CELL_TYPE_STRING).setCellValue(es );
                row4.createCell(9, Cell.CELL_TYPE_STRING).setCellValue(com+de+uk+ca+jp+fr+it+es );
                
                row4 = sheet4.createRow(excelNo++);
                row4.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue("库存总体积(m³)");
                row4.createCell(1, Cell.CELL_TYPE_NUMERIC).setCellValue(Double.parseDouble(df.format(comV) ));
                row4.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(Double.parseDouble(df.format(deV) ));
                row4.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(Double.parseDouble(df.format(ukV) ));
                row4.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(Double.parseDouble(df.format(caV) ));
                row4.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(Double.parseDouble(df.format(jpV) ) );
                row4.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(Double.parseDouble(df.format(frV) ));
                row4.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(Double.parseDouble(df.format(itV) ));
                row4.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(Double.parseDouble(df.format(esV) ));
                row4.createCell(9, Cell.CELL_TYPE_NUMERIC).setCellValue(Double.parseDouble(df.format(comV+deV+ukV+caV+jpV+frV+itV+esV) ));
            }
            if(i==1){
                row3 = sheet3.createRow(excelNo++);
                row3.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("库存总数");
                row3.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(com);
                row3.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(de);
                row3.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(uk);
                row3.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(ca);
                row3.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(jp );
                row3.createCell(6, Cell.CELL_TYPE_STRING).setCellValue(fr);
                row3.createCell(7, Cell.CELL_TYPE_STRING).setCellValue(it);
                row3.createCell(8, Cell.CELL_TYPE_STRING).setCellValue(es );
                row3.createCell(9, Cell.CELL_TYPE_STRING).setCellValue(com+de+uk+ca+jp+fr+it+es );
                
                row3 = sheet3.createRow(excelNo++);
                row3.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("库存总体积(m³)");
                row3.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(comV) ));
                row3.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(deV) ));
                row3.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(ukV) ));
                row3.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(caV) ));
                row3.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(jpV) ) );
                row3.createCell(6, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(frV) ));
                row3.createCell(7, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(itV) ));
                row3.createCell(8, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(esV) ) );
                row3.createCell(9, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(comV+deV+ukV+caV+jpV+frV+itV+esV) ) );
            }
            if(i==2){
                row2 = sheet2.createRow(excelNo++);
                row2.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("库存总数");
                row2.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(com);
                row2.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(de);
                row2.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(uk);
                row2.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(ca);
                row2.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(jp );
                row2.createCell(6, Cell.CELL_TYPE_STRING).setCellValue(fr);
                row2.createCell(7, Cell.CELL_TYPE_STRING).setCellValue(it);
                row2.createCell(8, Cell.CELL_TYPE_STRING).setCellValue(es );
                row2.createCell(9, Cell.CELL_TYPE_STRING).setCellValue(com+de+uk+ca+jp+fr+it+es );
                
                row2 = sheet2.createRow(excelNo++);
                row2.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("库存总体积(m³)");
                row2.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(comV) ));
                row2.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(deV) ));
                row2.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(ukV) ));
                row2.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(caV) ));
                row2.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(jpV ) ));
                row2.createCell(6, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(frV) ));
                row2.createCell(7, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(itV) ));
                row2.createCell(8, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(esV) ) );
                row2.createCell(9, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(comV+deV+ukV+caV+jpV+frV+itV+esV ) ));
            }
            if(i==3){
                row1 = sheet1.createRow(excelNo++);
                row1.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("库存总数");
                row1.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(com);
                row1.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(de);
                row1.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(uk);
                row1.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(ca);
                row1.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(jp );
                row1.createCell(6, Cell.CELL_TYPE_STRING).setCellValue(fr);
                row1.createCell(7, Cell.CELL_TYPE_STRING).setCellValue(it);
                row1.createCell(8, Cell.CELL_TYPE_STRING).setCellValue(es );
                row1.createCell(9, Cell.CELL_TYPE_STRING).setCellValue(com+de+uk+ca+jp+fr+it+es );
                
                row1 = sheet1.createRow(excelNo++);
                row1.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("库存总体积(m³)");
                row1.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(comV) ));
                row1.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(deV) ));
                row1.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(ukV) ));
                row1.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(caV) ));
                row1.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(jpV ) ));
                row1.createCell(6, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(frV) ));
                row1.createCell(7, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(itV) ));
                row1.createCell(8, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(esV ) ));
                row1.createCell(9, Cell.CELL_TYPE_STRING).setCellValue(Double.parseDouble(df.format(comV+deV+ukV+caV+jpV+frV+itV+esV ) ));
            }
            
        }

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");

        SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
        String fileName = "FBA在库天数汇总" + sdf.format(new Date()) + ".xls";
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename="
                + fileName);
        try {
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	/*@RequestMapping(value = {"exportAllStorage1"})
    public void exportAllStorage(HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
	    ExportExcel export = new ExportExcel("库存汇总", Lists.newArrayList("产品","COM","DE","UK",
                "CA","JP","FR", "IT","ES","产品线"));
	    export.getSheet().setColumnWidth(0, 6000);
	    Map<String, Double> volume = amazonStorageMonitorService.getVolume();
        Map<String, String> lineNameByName = psiProductTypeGroupDictService.getLineNameByName();
        LinkedHashMap<String, LinkedHashMap<String, List<String>>> productStorag = amazonStorageMonitorService.getProductStorag();
        List<PsiProduct> findAllOnSale = psiProductService.findAllOnSale();

        List<String> onSaleProduct = new ArrayList<String>();
        for(int k=0;k<findAllOnSale.size();k++){
             PsiProduct psiProduct = findAllOnSale.get(k);
             if(psiProduct.getColor().contains(",")){
                    String arr[] = psiProduct.getColor().toString().split(",");
                    for(int i=0;i<arr.length;i++){
                        onSaleProduct.add(psiProduct.getBrand()+" "+psiProduct.getModel()+"_"+arr[i]);
                    }
                }else if("".equals(psiProduct.getColor())){
                    onSaleProduct.add(psiProduct.getBrand()+" "+psiProduct.getModel());
                }else{
                    onSaleProduct.add(psiProduct.getBrand()+" "+psiProduct.getModel()+"_"+psiProduct.getColor());
                }
        }
        int flag = 2;
        for(int i=3;i>-1;i--){
            if(i==0){
                Row addRow = export.addRow();
                addRow.setHeight((short)700);
                addRow.getSheet().addMergedRegion(new CellRangeAddress(flag, flag, 0, 8));
                export.addCell(addRow, 0,"库存90天到180天" );
                Row addRow1 = export.addRow();
                export.addCell(addRow1, 0,"产品" );
                export.addCell(addRow1, 1,"COM" );
                export.addCell(addRow1, 2,"DE" );
                export.addCell(addRow1, 3,"UK" );
                export.addCell(addRow1, 4,"CA" );
                export.addCell(addRow1, 5,"JP" );
                export.addCell(addRow1, 6,"FR" );
                export.addCell(addRow1, 7,"IT" );
                export.addCell(addRow1, 8,"ES" );
                export.addCell(addRow1, 9,"产品线" );
                flag+=2;
            }else if(i==1){
                Row addRow = export.addRow();
                addRow.setHeight((short)700);
                addRow.getSheet().addMergedRegion(new CellRangeAddress(flag, flag, 0, 8));
                export.addCell(addRow, 0,"库存180天到270天" );
                Row addRow1 = export.addRow();
                export.addCell(addRow1, 0,"产品" );
                export.addCell(addRow1, 1,"COM" );
                export.addCell(addRow1, 2,"DE" );
                export.addCell(addRow1, 3,"UK" );
                export.addCell(addRow1, 4,"CA" );
                export.addCell(addRow1, 5,"JP" );
                export.addCell(addRow1, 6,"FR" );
                export.addCell(addRow1, 7,"IT" );
                export.addCell(addRow1, 8,"ES" );
                export.addCell(addRow1, 9,"产品线" );
                flag+=2;
            }else if(i==2){
                Row addRow = export.addRow();
                addRow.setHeight((short)700);
                addRow.getSheet().addMergedRegion(new CellRangeAddress(flag, flag, 0, 8));
                export.addCell(addRow, 0,"库存270天到365天" );
                Row addRow1 = export.addRow();
                export.addCell(addRow1, 0,"产品" );
                export.addCell(addRow1, 1,"COM" );
                export.addCell(addRow1, 2,"DE" );
                export.addCell(addRow1, 3,"UK" );
                export.addCell(addRow1, 4,"CA" );
                export.addCell(addRow1, 5,"JP" );
                export.addCell(addRow1, 6,"FR" );
                export.addCell(addRow1, 7,"IT" );
                export.addCell(addRow1, 8,"ES" );
                export.addCell(addRow1, 9,"产品线" );
                flag+=2;
            }else{
                Row addRow = export.addRow();
                addRow.setHeight((short)700);
                addRow.getSheet().addMergedRegion(new CellRangeAddress(flag, flag, 0, 8));
                export.addCell(addRow, 0,"库存365天以上" );
                Row addRow1 = export.addRow();
                export.addCell(addRow1, 0,"产品" );
                export.addCell(addRow1, 1,"COM" );
                export.addCell(addRow1, 2,"DE" );
                export.addCell(addRow1, 3,"UK" );
                export.addCell(addRow1, 4,"CA" );
                export.addCell(addRow1, 5,"JP" );
                export.addCell(addRow1, 6,"FR" );
                export.addCell(addRow1, 7,"IT" );
                export.addCell(addRow1, 8,"ES" );
                export.addCell(addRow1, 9,"产品线" );
                flag+=2;
            }
            int com=0,de=0,uk=0,ca=0,jp=0,fr=0,it=0,es=0;
            double comV=0,deV=0,ukV=0,caV=0,jpV=0,frV=0,itV=0,esV=0;
            LinkedHashMap<String,List<String>> map = new LinkedHashMap<String, List<String>>();
            for (Entry<String, LinkedHashMap<String, List<String>>> entry : productStorag.entrySet()) { 

                LinkedHashMap<String, List<String>> entry1 = entry.getValue();
                StringBuilder sb1 = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                StringBuilder sb3 = new StringBuilder();
                StringBuilder sb4 = new StringBuilder();
                StringBuilder sb5 = new StringBuilder();
                StringBuilder sb6 = new StringBuilder();
                StringBuilder sb7 = new StringBuilder();
                StringBuilder sb8 = new StringBuilder();
                boolean contentNotNull = false;
                for (String key : entry1.keySet()) {
                    String key2 = entry.getKey();
                    Double v=0d;
                    if( volume.get(key2)!=null){
                        v = volume.get(key2);
                    }
                    if(!"0".equals(entry1.get(key).get(i))){
                        contentNotNull = true;
                        if(key.equals("com")){
                            sb1.append(entry1.get(key).get(i));
                            com += Integer.parseInt(entry1.get(key).get(i));
                            comV+=v*Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("de")){
                            sb2.append(entry1.get(key).get(i));
                            deV+=v*Integer.parseInt(entry1.get(key).get(i));
                            de += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("uk")){
                            sb3.append(entry1.get(key).get(i));
                            ukV+=v*Integer.parseInt(entry1.get(key).get(i));
                            uk += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("ca")){
                            sb4.append(entry1.get(key).get(i));
                            caV+=v*Integer.parseInt(entry1.get(key).get(i));
                            ca += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("jp")){
                            sb5.append(entry1.get(key).get(i));
                            jpV+=v*Integer.parseInt(entry1.get(key).get(i));
                            jp += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("fr")){
                            sb6.append(entry1.get(key).get(i));
                            frV+=v*Integer.parseInt(entry1.get(key).get(i));
                            fr += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("it")){
                            sb7.append(entry1.get(key).get(i));
                            itV+=v*Integer.parseInt(entry1.get(key).get(i));
                            it += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("es")){
                            sb8.append(entry1.get(key).get(i));
                            esV+=v*Integer.parseInt(entry1.get(key).get(i));
                            es += Integer.parseInt(entry1.get(key).get(i));
                        }
                    }
                } 
                if(contentNotNull){
                    String lineName= lineNameByName.get(entry.getKey())!=null ? lineNameByName.get(entry.getKey()):"";

                    if(onSaleProduct.contains(entry.getKey())){
                        Row addRow = export.addRow();
                        export.addCell(addRow, 0,entry.getKey() );
                        
                        export.addCell(addRow, 1,sb1.toString() );
                        export.addCell(addRow, 2,sb2.toString() );
                        export.addCell(addRow, 3,sb3.toString() );
                        export.addCell(addRow, 4,sb4.toString() );
                        export.addCell(addRow, 5,sb5.toString() );
                        export.addCell(addRow, 6,sb6.toString() );
                        export.addCell(addRow, 7,sb7.toString() );
                        export.addCell(addRow, 8,sb8.toString() );
                        
                        export.addCell(addRow, 1,  StringUtils.isBlank(sb1.toString())?"":Integer.parseInt(sb1.toString()));
                        export.addCell(addRow, 2,  StringUtils.isBlank(sb2.toString())?"":Integer.parseInt(sb2.toString()));
                        export.addCell(addRow, 3,  StringUtils.isBlank(sb3.toString())?"":Integer.parseInt(sb3.toString()));
                        export.addCell(addRow, 4,  StringUtils.isBlank(sb4.toString())?"":Integer.parseInt(sb4.toString()));
                        export.addCell(addRow, 5,  StringUtils.isBlank(sb5.toString())?"":Integer.parseInt(sb5.toString()));
                        export.addCell(addRow, 6,  StringUtils.isBlank(sb6.toString())?"":Integer.parseInt(sb6.toString()));
                        export.addCell(addRow, 7,  StringUtils.isBlank(sb7.toString())?"":Integer.parseInt(sb7.toString()));
                        export.addCell(addRow, 8,  StringUtils.isBlank(sb8.toString())?"":Integer.parseInt(sb8.toString()));
                        
                        
                        export.addCell(addRow, 9,lineName );
                        flag++;
                    }else{
                        List<String> list = new ArrayList<String>();
                        list.add(sb1.toString() );
                        list.add(sb2.toString() );
                        list.add(sb3.toString() );
                        list.add(sb4.toString() );
                        list.add(sb5.toString() );
                        list.add(sb6.toString() );
                        list.add(sb7.toString() );
                        list.add(sb8.toString() );
                        list.add(lineName );
                        map.put(entry.getKey(), list);
                    }
                }
            }
            if(map.size()>0){
                Row addRow = export.addRow();
                addRow.getSheet().addMergedRegion(new CellRangeAddress(flag, flag,0, 9));
                export.addCell(addRow, 0, "淘汰的产品");
                flag++;
                for(Entry<String, List<String>> entry : map.entrySet()){
                    List<String> value = entry.getValue();
                    Row addRow1 = export.addRow();
                    export.addCell(addRow1, 0, entry.getKey());
                    export.addCell(addRow1, 1, StringUtils.isBlank(value.get(0))?"":Integer.parseInt(value.get(0)));
                    export.addCell(addRow1, 2, StringUtils.isBlank(value.get(1))?"":Integer.parseInt(value.get(1)));
                    export.addCell(addRow1, 3, StringUtils.isBlank(value.get(2))?"":Integer.parseInt(value.get(2)));
                    export.addCell(addRow1, 4, StringUtils.isBlank(value.get(3))?"":Integer.parseInt(value.get(3)));
                    export.addCell(addRow1, 5, StringUtils.isBlank(value.get(4))?"":Integer.parseInt(value.get(4)));
                    export.addCell(addRow1, 6, StringUtils.isBlank(value.get(5))?"":Integer.parseInt(value.get(5)));
                    export.addCell(addRow1, 7, StringUtils.isBlank(value.get(6))?"":Integer.parseInt(value.get(6)));
                    export.addCell(addRow1, 8, StringUtils.isBlank(value.get(7))?"":Integer.parseInt(value.get(7)));
                    export.addCell(addRow1, 9, StringUtils.isBlank(value.get(8))?"":value.get(8));
                    flag++;
                }
            }
            DecimalFormat   df   =new DecimalFormat("#0.0000"); 
            Row addRow = export.addRow();
            export.addCell(addRow, 0,"库存总数");
            export.addCell(addRow, 1,com );
            export.addCell(addRow, 2,de );
            export.addCell(addRow, 3,uk );
            export.addCell(addRow, 4,ca );
            export.addCell(addRow, 5,jp );
            export.addCell(addRow, 6,fr );
            export.addCell(addRow, 7,it );
            export.addCell(addRow, 8,es );
            Row addRow1 = export.addRow();
            export.addCell(addRow1, 0,"库存总体积(m³)");
            export.addCell(addRow1, 1,df.format(comV) );
            export.addCell(addRow1, 2,df.format(deV) );
            export.addCell(addRow1, 3,df.format(ukV) );
            export.addCell(addRow1, 4,df.format(caV) );
            export.addCell(addRow1, 5,df.format(jpV) );
            export.addCell(addRow1, 6,df.format(frV) );
            export.addCell(addRow1, 7,df.format(itV) );
            export.addCell(addRow1, 8,df.format(esV) );
            flag+=2;
        }
        try {
            export.write(response, "FBA在库天数汇总" + DateUtils.getDate() + ".xlsx").dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
	
	@RequestMapping(value = {"exportBySome"})
    public void exportBySome(HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
        ExportExcel export = new ExportExcel("库存预警", Lists.newArrayList(" ","","","","","","","","",""));
        export.getSheet().setColumnWidth(0, 6000);
        export.getSheet().setColumnWidth(1, 6000);
        Map<String, String> productPositionMap = psiProductEliminateService.findAllProductPosition();
        Map<String, String> lineNameByName = psiProductTypeGroupDictService.getLineNameByName();
        LinkedHashMap<String, List<String>> productAndCountryByDaySale = amazonStorageMonitorService.getProductAndCountryByDaySale();
        LinkedHashMap<String, LinkedHashMap<String, List<String>>> productStorag = amazonStorageMonitorService.getProductStorag();
        LinkedHashMap<String, LinkedHashMap<String, Integer>> storageByDay = amazonStorageMonitorService.getStorageByDay();
        Map<String, Map<String, List<String>>> allStockAndSale = amazonStorageMonitorService.getAllStockAndSale();
        Map<String, Map<String, Integer>> get31SalesMap = psiInventoryFbaService.get31SalesMap(null);
        Row addRow0 = export.addRow();
        export.addCell(addRow0, 0, "以下是产品超过3个月库存总数 /日销量>120天，其他市场FBA库存可售天<90天详情");
        addRow0.getSheet().addMergedRegion(new CellRangeAddress(2, 2, 0, 8));
        Row addRow01 = export.addRow();
        addRow01.getSheet().addMergedRegion(new CellRangeAddress(3, 3, 3, 5));
        addRow01.getSheet().addMergedRegion(new CellRangeAddress(3, 3, 6, 8));
        export.addCell(addRow01, 0, "产品(US)");
        export.addCell(addRow01, 1, "FBA总库存在库天数大于90天");
        export.addCell(addRow01, 2, "日销量");
        export.addCell(addRow01, 3, "JP");
        export.addCell(addRow01, 6, "DE");
        Row exportMessage = export.addRow();
        exportMessage.getSheet().addMergedRegion(new CellRangeAddress(3, 4, 0, 0));
        exportMessage.getSheet().addMergedRegion(new CellRangeAddress(3, 4, 1, 1));
        exportMessage.getSheet().addMergedRegion(new CellRangeAddress(3, 4, 2, 2));
        export.addCell(exportMessage, 3, "总库存不含在产");
        export.addCell(exportMessage, 4, "日销量");
        export.addCell(exportMessage, 5, "可售天");
        export.addCell(exportMessage, 6, "总库存不含在产");
        export.addCell(exportMessage, 7, "日销量");
        export.addCell(exportMessage, 8, "可售天");
        export.addCell(exportMessage, 9, "产品线");
        boolean comFlag=false,jpFlag=false,deFlag=false;
        int rownum=4;
        for(int j=1;j<4;j++){
            HashMap<String,List<String>> map = new HashMap<String, List<String>>();
            for(Entry<String, List<String>> entry : productAndCountryByDaySale.entrySet()){
                String pName = entry.getKey();
                for(int i=0;i<entry.getValue().size();i++){
                    String country = entry.getValue().get(i);
                    Map<String, Integer> get31SalesMap1 = get31SalesMap.get(country);
                    Integer sales31=0;
                    if(get31SalesMap1.get(pName)!=null){
                         if(get31SalesMap1.get(pName)!=null)
                            sales31 = get31SalesMap1.get(pName);
                    }
                    if(allStockAndSale.get(pName) != null){
                        String lineName= lineNameByName.get(pName)!=null ? lineNameByName.get(pName):"";

                        Map<String, List<String>> countryAndStockAndSale = allStockAndSale.get(pName);
                        List<String> listJp = countryAndStockAndSale.get("jp")!=null?countryAndStockAndSale.get("jp"):null;
                        List<String> listCom = countryAndStockAndSale.get("com")!=null?countryAndStockAndSale.get("com"):null;
                        List<String> listDe = countryAndStockAndSale.get("eu")!=null?countryAndStockAndSale.get("eu"):null;
                        int totalCom = listCom != null ? Integer.parseInt(listCom.get(0)):0;
                        double saleMountOneDayCom = listCom != null ? Math.ceil(Double.parseDouble(listCom.get(1))/31):0;
                        int totalDe = listDe != null ? Integer.parseInt(listDe.get(0)):0;
                        double saleMountOneDayDe = listDe != null ? Math.ceil( Double.parseDouble(listDe.get(1))/31):0;
                        int totalJp = listJp != null ? Integer.parseInt(listJp.get(0)):0;
                        double saleMountOneDayJp = listJp != null ?Math.ceil(Double.parseDouble(listJp.get(1))/31):0;
                        boolean flag1 = false,flag2 = false;
                        if(j==1){
                            if("com".equals(country)){
                                   if(totalJp != 0 && saleMountOneDayJp != 0 && totalJp/saleMountOneDayJp<90){
                                      flag1=true;
                                   }
                                   if(totalDe != 0 && saleMountOneDayDe != 0 && totalDe/saleMountOneDayDe<90){
                                      flag2=true;
                                   }
                                   if(flag1 || flag2){
                                       if(!"4".equals(productPositionMap.get(pName+"_"+country))){
                                           rownum=rownum+1;
                                           comFlag=true;
                                           Row addRow2 = export.addRow();
                                           export.addCell(addRow2, 0, pName);
                                           export.addCell(addRow2, 1, storageByDay.get(pName).get(country));
                                           export.addCell(addRow2, 2, sales31!=0?(int)Math.ceil(sales31/31):"");
                                           if(flag1){
                                               export.addCell(addRow2, 3, totalJp);
                                               export.addCell(addRow2, 4, (int)Math.ceil(saleMountOneDayJp));
                                               export.addCell(addRow2, 5, (int)Math.ceil(totalJp/saleMountOneDayJp));
                                           }
                                           if(flag2){
                                               export.addCell(addRow2, 6, totalDe);
                                               export.addCell(addRow2, 7, (int)Math.ceil(saleMountOneDayDe));
                                               export.addCell(addRow2, 8, (int)Math.ceil(totalDe/saleMountOneDayDe));
                                           }
                                           export.addCell(addRow2, 9, lineName);
                                       }else{
                                           List<String> list = new ArrayList<String>();
                                           list.add(storageByDay.get(pName).get(country).toString());
                                           list.add(sales31!=0?String.valueOf((int)Math.ceil(sales31/31)):"");
                                           if(flag1){
                                               list.add(Integer.toString(totalJp));
                                               list.add(Integer.toString((int)Math.ceil(saleMountOneDayJp)));
                                               list.add(Integer.toString((int)Math.ceil(totalJp/saleMountOneDayJp)));
                                           }else{
                                               list.add("");
                                               list.add("");
                                               list.add("");
                                           }
                                           if(flag2){
                                               list.add(Integer.toString(totalDe));
                                               list.add(Integer.toString((int)Math.ceil(saleMountOneDayDe)));
                                               list.add( Integer.toString((int)Math.ceil(totalDe/saleMountOneDayDe))); 
                                           }else{
                                               list.add("");
                                               list.add("");
                                               list.add("");
                                           }
                                           
                                           list.add(lineName);
                                           map.put(pName, list);
                                       }
                                    }
                             }
                        }
                        if(j==2){
                             if("jp".equals(country)){
                                   if(totalCom != 0 && saleMountOneDayCom != 0 && totalCom/saleMountOneDayCom<90){
                                        flag1=true;
                                    }
                                    if(totalDe != 0 && saleMountOneDayDe != 0 && totalDe/saleMountOneDayDe<90){
                                        flag2=true;
                                    }
                                    if(flag1 || flag2){
                                        jpFlag=true;
                                        if(!"4".equals(productPositionMap.get(pName+"_"+country))){
                                            rownum++;
                                            Row addRow2 = export.addRow();
                                            export.addCell(addRow2, 0, pName);
                                            export.addCell(addRow2, 1, storageByDay.get(pName).get(country));
                                            export.addCell(addRow2, 2, sales31!=0?(int)Math.ceil(sales31/31):"");
                                            if(flag1){
                                                export.addCell(addRow2, 3, totalCom);
                                                export.addCell(addRow2, 4, (int)Math.ceil(saleMountOneDayCom));
                                                export.addCell(addRow2, 5, (int)Math.ceil(totalCom/saleMountOneDayCom));
                                            }
                                            if(flag2){
                                                export.addCell(addRow2, 6, totalDe);
                                                export.addCell(addRow2, 7, (int)Math.ceil(saleMountOneDayDe));
                                                export.addCell(addRow2, 8, (int)Math.ceil(totalDe/saleMountOneDayDe));
                                            }
                                            export.addCell(addRow2, 9, lineName);
                                        }else{
                                            List<String> list = new ArrayList<String>();
                                            list.add(storageByDay.get(pName).get(country).toString());
                                            list.add(sales31!=0?String.valueOf((int)Math.ceil(sales31/31)):"");
                                            if(flag1){
                                                list.add(Integer.toString(totalCom));
                                                list.add(Integer.toString( (int)Math.ceil(saleMountOneDayCom)));
                                                list.add(Integer.toString((int)Math.ceil(totalCom/saleMountOneDayCom)));
                                            }else{
                                                list.add("");
                                                list.add("");
                                                list.add("");
                                            }
                                            if(flag2){
                                                list.add(Integer.toString(totalDe));
                                                list.add(Integer.toString((int)Math.ceil(saleMountOneDayDe)));
                                                list.add( Integer.toString((int)Math.ceil(totalDe/saleMountOneDayDe))); 
                                            }else{
                                                list.add("");
                                                list.add("");
                                                list.add("");
                                            }
                                            
                                            list.add(lineName);
                                            map.put(pName, list);
                                        }
                                        
                                     }
                               }
                        }
                        if(j==3){
                            if("de".equals(country)){
                                    if(totalCom != 0 && saleMountOneDayCom != 0 && totalCom/saleMountOneDayCom<90){
                                        flag1=true;
                                    }
                                    if(totalJp != 0 && saleMountOneDayJp != 0 && totalJp/saleMountOneDayJp<90){
                                        flag2=true;
                                    }
                                    if(flag1 || flag2){
                                        deFlag=true;
                                        if(!"4".equals(productPositionMap.get(pName+"_"+country))){
                                            Row addRow2 = export.addRow();
                                            rownum++;
                                            export.addCell(addRow2, 0, pName);
                                            export.addCell(addRow2, 1, storageByDay.get(pName).get(country));
                                            export.addCell(addRow2, 2, sales31!=0?(int)Math.ceil(sales31/31):"");
                                            if(flag1){
                                                export.addCell(addRow2, 3, totalCom);
                                                export.addCell(addRow2, 4, (int)Math.ceil(saleMountOneDayCom));
                                                export.addCell(addRow2, 5, (int)Math.ceil(totalCom/saleMountOneDayCom));
                                            }
                                            if(flag2){
                                                export.addCell(addRow2, 6, totalJp);
                                                export.addCell(addRow2, 7, (int)Math.ceil(saleMountOneDayJp));
                                                export.addCell(addRow2, 8, (int)Math.ceil(totalJp/saleMountOneDayJp));
                                            }
                                            export.addCell(addRow2, 9, lineName);
                                        }else{
                                            List<String> list = new ArrayList<String>();
                                            list.add(storageByDay.get(pName).get(country).toString());
                                            list.add(sales31!=0?String.valueOf((int)Math.ceil(sales31/31)):"");
                                            if(flag1){
                                                list.add(Integer.toString(totalCom));
                                                list.add(Integer.toString( (int)Math.ceil(saleMountOneDayCom)));
                                                list.add(Integer.toString((int)Math.ceil(totalCom/saleMountOneDayCom)));
                                            }else{
                                                list.add("");
                                                list.add("");
                                                list.add("");
                                            }
                                            if(flag2){
                                                list.add(Integer.toString(totalJp));
                                                list.add(Integer.toString((int)Math.ceil(saleMountOneDayJp)));
                                                list.add(Integer.toString((int)Math.ceil(totalJp/saleMountOneDayJp)));
                                            }else{
                                                list.add("");
                                                list.add("");
                                                list.add("");
                                            }
                                            
                                            list.add(lineName);
                                            map.put(pName, list);
                                        }
                                        
                                     }
                                }
                        }
                       
                        }
                   }
                }
            if(map.size()>0 && j==1){
                Row addRow = export.addRow();
                addRow.getSheet().addMergedRegion(new CellRangeAddress(rownum+1, rownum+1,0, 9));
                export.addCell(addRow, 0, "淘汰的产品");
                rownum++;
            }
            if(map.size()>0 && (j==2 || j==3)){
                Row addRow = export.addRow();
                addRow.getSheet().addMergedRegion(new CellRangeAddress(rownum, rownum,0, 9));
                export.addCell(addRow, 0, "淘汰的产品");
                rownum++;
            }
            for(Entry<String, List<String>> entry : map.entrySet()){
                List<String> value = entry.getValue();
                Row addRow1 = export.addRow();
                export.addCell(addRow1, 0, entry.getKey());
                export.addCell(addRow1, 1, value.get(0));
                export.addCell(addRow1, 2, value.get(1));
                export.addCell(addRow1, 3,value.get(2));
                export.addCell(addRow1, 4, value.get(3));
                export.addCell(addRow1, 5, value.get(4));
                export.addCell(addRow1, 6, value.get(5));
                export.addCell(addRow1, 7, value.get(6));
                export.addCell(addRow1, 8, value.get(7));
                export.addCell(addRow1, 9, value.get(8));
                rownum++;
            }
            if(j==1){
                Row addRow02 = export.addRow();
                addRow02.getSheet().addMergedRegion(new CellRangeAddress(rownum+1, rownum+1, 3, 5));
                addRow02.getSheet().addMergedRegion(new CellRangeAddress(rownum+1, rownum+1, 6, 8));
                export.addCell(addRow02, 0, "产品(JP)");
                export.addCell(addRow02, 1, "FBA总库存在库天数大于90天");
                export.addCell(addRow02, 2, "日销量");
                export.addCell(addRow02, 3, "US");
                export.addCell(addRow02, 6, "DE");
                rownum=rownum+1;
                Row exportMessage2 = export.addRow();
                exportMessage2.getSheet().addMergedRegion(new CellRangeAddress(rownum, rownum+1, 0, 0));
                exportMessage2.getSheet().addMergedRegion(new CellRangeAddress(rownum, rownum+1, 1, 1));
                exportMessage2.getSheet().addMergedRegion(new CellRangeAddress(rownum, rownum+1, 2, 2));
                export.addCell(exportMessage2, 3, "总库存不含在产");
                export.addCell(exportMessage2, 4, "日销量");
                export.addCell(exportMessage2, 5, "可售天");
                export.addCell(exportMessage2, 6, "总库存不含在产");
                export.addCell(exportMessage2, 7, "日销量");
                export.addCell(exportMessage2, 8, "可售天");
                export.addCell(exportMessage2, 9, "产品线");
                rownum=rownum+2;
            }
            if(j==2){
                Row addRow02 = export.addRow();
                addRow02.getSheet().addMergedRegion(new CellRangeAddress(rownum, rownum, 3, 5));
                addRow02.getSheet().addMergedRegion(new CellRangeAddress(rownum, rownum, 6, 8));
                export.addCell(addRow02, 0, "产品(DE)");
                export.addCell(addRow02, 1, "FBA总库存在库天数大于90天");
                export.addCell(addRow02, 2, "日销量");
                export.addCell(addRow02, 3, "US");
                export.addCell(addRow02, 6, "JP");
                Row exportMessage2 = export.addRow();
                exportMessage2.getSheet().addMergedRegion(new CellRangeAddress(rownum, rownum+1, 0, 0));
                exportMessage2.getSheet().addMergedRegion(new CellRangeAddress(rownum, rownum+1, 1, 1));
                exportMessage2.getSheet().addMergedRegion(new CellRangeAddress(rownum, rownum+1, 2, 2));
                export.addCell(exportMessage2, 3, "总库存不含在产");
                export.addCell(exportMessage2, 4, "日销量");
                export.addCell(exportMessage2, 5, "可售天");
                export.addCell(exportMessage2, 6, "总库存不含在产");
                export.addCell(exportMessage2, 7, "日销量");
                export.addCell(exportMessage2, 8, "可售天");
                export.addCell(exportMessage2, 9, "产品线");
                rownum=rownum+2;
            }
         }
        try {
            export.write(response, "库存预警" + DateUtils.getDate() + ".xlsx").dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
     }
}
