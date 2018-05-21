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

import org.apache.commons.lang.time.DateUtils;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonFacebookRelationship;
import com.springrain.erp.modules.amazoninfo.entity.AmazonFacebookReport;
import com.springrain.erp.modules.amazoninfo.entity.FacebookDto;
import com.springrain.erp.modules.amazoninfo.entity.FacebookReport;
import com.springrain.erp.modules.amazoninfo.service.AmazonAndFacebookService;

@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/amazonAndFacebook")
public class AmazonAndFacebookController extends BaseController {
	@Autowired
	private AmazonAndFacebookService amazonAndFacebookService;
	private static DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
	
	@RequestMapping(value = "totalList")
	public String totalList(FacebookDto facebookDto, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		if(facebookDto.getStarts()==null){
			Date date=new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			facebookDto.setStarts(DateUtils.addDays(date, -7));
			facebookDto.setEnd(new Date());
		}
		List<FacebookDto> faceBookList=amazonAndFacebookService.findFacebook(facebookDto);
		model.addAttribute("faceBookList", faceBookList);
		return "modules/amazoninfo/facebookPostList";
	}
	
	@RequestMapping(value = "amountSpentCharts")
	public String amountSpentCharts(FacebookDto facebookDto, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		try {
			facebookDto.setStarts(formatDay.parse(facebookDto.getCreateDate()));
			facebookDto.setEnd(formatDay.parse(facebookDto.getEndDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date start = facebookDto.getStarts();
		Date end = facebookDto.getEnd();
		List<String> xAxis  = Lists.newArrayList();
		while(end.after(start)||end.equals(start)){
			 String key = formatDay.format(start);
		     xAxis.add(key);
			 start = DateUtils.addDays(start, 1);
		}
		Map<String,FacebookDto> amaouSpentMap=amazonAndFacebookService.findAmountSpend(facebookDto);
		model.addAttribute("xAxis",xAxis);
		model.addAttribute("amaouSpentMap", amaouSpentMap);
		
		model.addAttribute("start",facebookDto.getCreateDate());
		model.addAttribute("end",facebookDto.getEndDate());
		model.addAttribute("adId",facebookDto.getAdId());
		model.addAttribute("adName",facebookDto.getAdName());
		model.addAttribute("facebookDto", facebookDto);
		return "modules/amazoninfo/facebookAmountSpentList";
	}
	
	
	
	@RequestMapping(value = "list")
	public String list(FacebookReport facebookReport, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Page<FacebookReport> page = new Page<FacebookReport>(request, response,-1);
		if(facebookReport.getStart()==null){
			Date date=new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			facebookReport.setStart(DateUtils.addDays(date, -7));
			facebookReport.setEnd(new Date());
		}
		String orderBy = page.getOrderBy();
		if (StringUtils.isEmpty(orderBy)) {
			page.setOrderBy("start desc");
		} else {
			page.setOrderBy(orderBy);
		}
		page = amazonAndFacebookService.find(page, facebookReport);
		page.setOrderBy(orderBy);
		model.addAttribute("page", page);
		return "modules/amazoninfo/facebookList";
	}
	
	
	@RequestMapping(value = "exportFacebookList")
	public String exportFacebookList(FacebookReport facebookReport, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
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
		Page<FacebookReport> page = new Page<FacebookReport>(request, response,-1);
		if(facebookReport.getStart()==null){
			facebookReport.setStart(DateUtils.addDays(new Date(), -7));
			facebookReport.setEnd(new Date());
		}
		List<String> titleList=Lists.newArrayList("Reporting Starts","Reporting Ends","Account ID","Campaign Name","Campaign ID","Ad Set Name","Ad Set ID","Ad Name","Ad ID","Delivery","Amount Spent","Impressions","Link Clicks","Frequency","Relevance Score","Negative Feedback",
				"Post Shares","Post Comments","Page Likes","Post Engagement");
		page = amazonAndFacebookService.find(page, facebookReport);
		for(int i=0;i<titleList.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(titleList.get(i));
		}
		row = sheet.createRow(1);
		int rownum=1;
		for (FacebookReport report : page.getList()) {
			int j=0;
			row = sheet.createRow(rownum++);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(formatDay.format(report.getStart()));
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(formatDay.format(report.getEnd()));
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAccountId());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getCampaignName());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getCampaignId());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAdSetName());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAdSetId());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAdName());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAdId());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getDelivery());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAmountSpent());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getImpressions());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getLinkClicks());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getFrequency());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getRelevanceScore());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getNegativeFeedback());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    //"Post Shares","Post Comments","Page Likes","Post Engagement"
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getPostShares());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getPostComments());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getPageLikes());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getPostEngagement());
		    row.getCell(j-1).setCellStyle(cellStyle);
		}
		for (int i = 0; i < titleList.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
	    }
		try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
	            String fileName = "facebook.xls";
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
	
	
	@RequestMapping(value = "amazonFacebookList")
	public String amazonFacebookList(AmazonFacebookReport amazonFacebookReport, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Page<AmazonFacebookReport> page = new Page<AmazonFacebookReport>(request, response,-1);
		if(amazonFacebookReport.getDateShipped()==null){
			Date date=new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			amazonFacebookReport.setDateShipped(DateUtils.addDays(date, -7));
			amazonFacebookReport.setEndDate(new Date());
		}
		String orderBy = page.getOrderBy();
		if (StringUtils.isEmpty(orderBy)) {
			page.setOrderBy("dateShipped desc");
		} else {
			page.setOrderBy(orderBy);
		}
		page = amazonAndFacebookService.find(page, amazonFacebookReport);
		page.setOrderBy(orderBy);
		model.addAttribute("page", page);
		return "modules/amazoninfo/amazonFacebookList";
	}
	
	
	@RequestMapping(value = "exportAmazonFacebookList")
	public String exportAmazonFacebookList(AmazonFacebookReport amazonFacebookReport, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
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
        Page<AmazonFacebookReport> page = new Page<AmazonFacebookReport>(request, response,-1);
		if(amazonFacebookReport.getDateShipped()==null){
			amazonFacebookReport.setDateShipped(DateUtils.addDays(new Date(), -7));
			amazonFacebookReport.setEndDate(new Date());
		}
		List<String> titleList=Lists.newArrayList("Market","Product Line","Items Name","Asin","Seller","Tracking Id","Date Shipped","Price","Advertising-Fee Rate","Items Shipped","Revenue","Advertising Fees","Device Type");
		page = amazonAndFacebookService.find(page, amazonFacebookReport);
		for(int i=0;i<titleList.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(titleList.get(i));
		}
		row = sheet.createRow(1);
		int rownum=1;
		for (AmazonFacebookReport report : page.getList()) {
			int j=0;
			row = sheet.createRow(rownum++);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getMarket());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getProductLine());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getItemName());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAsin());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSeller());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getTrackingId());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(formatDay.format(report.getDateShipped()));
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getPrice());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAdvertisingFeeRate());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getItemsShipped());
		    row.getCell(j-1).setCellStyle(contentStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getRevenue());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getAdvertisingFees());
		    row.getCell(j-1).setCellStyle(cellStyle);
		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getDeviceType());
		    row.getCell(j-1).setCellStyle(contentStyle);
		}
		for (int i = 0; i < titleList.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
	    }
		try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
	            String fileName = "amazon.xls";
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
	
	
	@RequestMapping(value = "amazonFacebookRelationship")
	public String amazonFacebookRelationship(AmazonFacebookRelationship amazonFacebookRelationship, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Page<AmazonFacebookRelationship> page = new Page<AmazonFacebookRelationship>(request, response,-1);
		if(amazonFacebookRelationship.getDate()==null){
			Date date=new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			amazonFacebookRelationship.setDate(DateUtils.addDays(date, -7));
			amazonFacebookRelationship.setEndDate(new Date());
		}
		String orderBy = page.getOrderBy();
		if (StringUtils.isEmpty(orderBy)) {
			page.setOrderBy("date desc");
		} else {
			page.setOrderBy(orderBy);
		}
		page = amazonAndFacebookService.find(page, amazonFacebookRelationship);
		page.setOrderBy(orderBy);
		model.addAttribute("page", page);
		model.addAttribute("amazonFacebookRelationship",amazonFacebookRelationship);
		return "modules/amazoninfo/amazonFacebookRelationshipList";
	}

	@RequestMapping(value = "updateProfit")
	public String updateProfit(FacebookReport facebookReport, HttpServletRequest request, HttpServletResponse response, Model model){
		amazonAndFacebookService.updateProfit();
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/list?repage";
	}
	
	@RequestMapping(value = "deleteFacebook")
	public String deleteFacebook(FacebookReport facebookReport, HttpServletRequest request, HttpServletResponse response, Model model){
		amazonAndFacebookService.deleteFacebook(facebookReport.getId());
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/list?repage";
	}
	
	@RequestMapping(value = "deleteFacebookArr")
	public String deleteFacebookArr(String ids, HttpServletRequest request, HttpServletResponse response, Model model){
		String[] idArr=ids.split(",");
		amazonAndFacebookService.deleteFacebook(Sets.newHashSet(idArr));
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/list?repage";
	}
	
	
	@RequestMapping(value = "deleteAmazonFacebook")
	public String deleteAmazonFacebook(AmazonFacebookReport amazonFacebookReport, HttpServletRequest request, HttpServletResponse response, Model model){
		amazonAndFacebookService.deleteAmazonFacebook(amazonFacebookReport.getId());
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/amazonFacebookList?repage";
	}
	
	@RequestMapping(value = "deleteAmazonFacebookArr")
	public String deleteAmazonFacebookArr(String ids,HttpServletRequest request, HttpServletResponse response, Model model){
		String[] idArr=ids.split(",");
		amazonAndFacebookService.deleteAmazonFacebook(Sets.newHashSet(idArr));
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/amazonFacebookList?repage";
	}
	
	
	@RequestMapping(value = "deleteRelation")
	public String deleteRelation(AmazonFacebookRelationship amazonFacebookRelationship, HttpServletRequest request, HttpServletResponse response, Model model){
		amazonAndFacebookService.deleteRelation(amazonFacebookRelationship.getId());
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/amazonFacebookRelationship?repage";
	}
	
	@RequestMapping(value = "deleteRelationArr")
	public String deleteRelationArr(String ids, HttpServletRequest request, HttpServletResponse response, Model model){
		String[] idArr=ids.split(",");
		amazonAndFacebookService.deleteRelation(Sets.newHashSet(idArr));
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/amazonFacebookRelationship?repage";
	}
	
	
	@RequestMapping(value = "uploadFile")
	public String uploadFile(@RequestParam("excel")MultipartFile excelFile,String type,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		String returnRes="redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/list?repage";
		try {
			Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
			Sheet sheet = workBook.getSheetAt(0);
			sheet.setForceFormulaRecalculation(true);
			if("0".equals(type)){//facebook
				// 循环行Row
				List<FacebookReport> facebookReportList=Lists.newArrayList();
			//[26-七月-2016, 26-七月-2016, 1.0728935610282E14, US_SoundCore sport XL, 6.051402557826E12, NF_desktop_target27 _13M, 6.051402558026E12, soundcore xl waterproof , 6.051405865226E12, active, 7.9, 450.0, 4.0, 1.008969, , , , , null, null, null, null, null, null, null]
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						continue;
					}
					Date start=row.getCell(0).getDateCellValue();
					Date end=row.getCell(1).getDateCellValue();
				    String accountId=row.getCell(2).getStringCellValue();
				    String campaignName=row.getCell(3).getStringCellValue();
				    String campaignId=row.getCell(4).getStringCellValue();
				    String adSetName=row.getCell(5).getStringCellValue();
				    String adSetId=row.getCell(6).getStringCellValue();
				    String adName=row.getCell(7).getStringCellValue();
				    String adId= row.getCell(8).getStringCellValue();
				    String delivery=row.getCell(9).getStringCellValue();
				    double amountSpent=row.getCell(10).getNumericCellValue();
				    double impressions=row.getCell(11).getNumericCellValue();
				  
				    double linkClicks=0;
				    try{linkClicks=row.getCell(12).getNumericCellValue();}catch(Exception e){}
				    double frequency=0;
				    try{frequency=row.getCell(13).getNumericCellValue();}catch(Exception e){}
				    double relevanceScore=0;
				    try{relevanceScore=row.getCell(14).getNumericCellValue();}catch(Exception e){}
				    String negativeFeedback="";
				    try{negativeFeedback=row.getCell(15).getStringCellValue();}catch(Exception e){}
				    double postShares=0;
				    try{postShares=row.getCell(16).getNumericCellValue();}catch(Exception e){}
				    double postComments=0;
				    try{postComments=row.getCell(17).getNumericCellValue();}catch(Exception e){}
				    double pageLikes=0;
				    try{pageLikes=row.getCell(18).getNumericCellValue();}catch(Exception e){}
				    double postEngagement=0;
				    try{postEngagement=row.getCell(19).getNumericCellValue();}catch(Exception e){}
				    facebookReportList.add(new FacebookReport(start,end,accountId,campaignName,campaignId,adSetName,adSetId,adName,adId,delivery,
							amountSpent,impressions,linkClicks,frequency,relevanceScore,negativeFeedback,postShares,postComments,pageLikes,postEngagement,"0"));
				}
				if(facebookReportList!=null&&facebookReportList.size()>0){
					amazonAndFacebookService.saveFacebookReport(facebookReportList);
				}
				addMessage(redirectAttributes,"文件上传成功");
			}else if("1".equals(type)){//amazon
				returnRes="redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/amazonFacebookList?repage";
				List<AmazonFacebookReport> reportList=Lists.newArrayList();
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						continue;
					}
					String market=("us".equals(row.getCell(0).getStringCellValue().toLowerCase())?"com":row.getCell(0).getStringCellValue().toLowerCase());
					String productLine=row.getCell(1).getStringCellValue();
					String itemName=row.getCell(2).getStringCellValue();
					String asin="";
					try{
						asin=row.getCell(3).getStringCellValue();
					}catch(Exception e){
						asin=row.getCell(3).getNumericCellValue()+"";
					}
					
					String seller=row.getCell(4).getStringCellValue();
					String trackingId=row.getCell(5).getStringCellValue();
					Date dateShipped=row.getCell(6).getDateCellValue();
					
				    double price=0;
				    try{price=row.getCell(7).getNumericCellValue();}catch(Exception e){}
				    double advertisingFeeRate=0;
				    try{advertisingFeeRate=row.getCell(8).getNumericCellValue();}catch(Exception e){}
				    long itemsShipped=(long) row.getCell(9).getNumericCellValue();
					double revenue=0;
					try{revenue=row.getCell(10).getNumericCellValue();}catch(Exception e){}
					double advertisingFees=0;
					try{advertisingFees=row.getCell(11).getNumericCellValue();}catch(Exception e){}
					String deviceType=row.getCell(12).getStringCellValue();
					
					reportList.add(new AmazonFacebookReport(market,dateShipped,productLine,itemName,asin,seller,trackingId,price, advertisingFeeRate,
								 itemsShipped, revenue, advertisingFees,deviceType,"0")); 
				}
				if(reportList!=null&&reportList.size()>0){
					amazonAndFacebookService.saveAmazonFacebookReport(reportList);
				}
				try{
					amazonAndFacebookService.updateProfit(); 
				}catch(Exception e){}
				addMessage(redirectAttributes,"文件上传成功");
				return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/amazonFacebookList?repage";
			}else if("2".equals(type)){//relationship
				List<AmazonFacebookRelationship> reportList=Lists.newArrayList();
				returnRes="redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/amazonFacebookRelationship?repage";
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						continue;
					}
					Date date=row.getCell(0).getDateCellValue();
					String market=("us".equals(row.getCell(1).getStringCellValue().toLowerCase())?"com":row.getCell(1).getStringCellValue().toLowerCase());
					String productLine=row.getCell(2).getStringCellValue();
					String product=row.getCell(3).getStringCellValue();
					String gender=row.getCell(4).getStringCellValue();
					String audience=row.getCell(5).getStringCellValue();
					String age=row.getCell(6).getStringCellValue();
					String placement=row.getCell(7).getStringCellValue();
					String asinOnAd=row.getCell(8).getStringCellValue();
					//String adId=row.getCell(9).getStringCellValue();
					String adId=row.getCell(9).getStringCellValue();
					String trackingId=row.getCell(10).getStringCellValue();
					String preView=row.getCell(11).getStringCellValue();
					Integer isExistId=amazonAndFacebookService.isExist(adId);
					if(isExistId==null){
						reportList.add(new  AmazonFacebookRelationship(market,date,productLine,product,gender,audience,age,
								placement,asinOnAd,adId,trackingId,preView,"0"));
					}else{
						AmazonFacebookRelationship ship=amazonAndFacebookService.findRelation(isExistId);
						ship.setAge(age);
						ship.setAsinOnAd(asinOnAd);
						ship.setDate(date);
						ship.setDelFlag("0");
						ship.setGender(gender);
						ship.setPreView(preView);
						ship.setMarket(market);
						ship.setProductLine(productLine);
						ship.setProduct(product);
						ship.setAudience(audience);
						ship.setPlacement(placement);
						ship.setTrackingId(trackingId);
						reportList.add(ship);
					}
				}	
				
				if(reportList!=null&&reportList.size()>0){
					amazonAndFacebookService.saveRelationship(reportList);
				}
				addMessage(redirectAttributes,"文件上传成功");
				return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/amazonFacebookRelationship?repage";
			}
			
		} catch (Exception e) {
			addMessage(redirectAttributes,"文件上传失败"+e.getMessage());
			return returnRes;
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonAndFacebook/list?repage";
	}
	
	
	@RequestMapping(value = "exportAll")
	public String exportAll(FacebookDto facebookDto, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
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
       /* cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);*/
        HSSFCell cell = null;
		
		
		List<FacebookDto> faceBookList=amazonAndFacebookService.findFacebook(facebookDto);
		List<String> titleList=Lists.newArrayList("Product","Starts","Audience","Age","Gender","Country","Placement","Ad ID","Ad Name","Tracking ID","Amount Spend","Link Clicks","CTR(%)","CPC","CPM","Total CR(%)","Same CR(%)","Profit"
				,"Same Items Shipped","All Items Shipped","Total Advertising","系数ROI","绝对ROI","Page Likes","CostPerPageLike","Impressions","Total Revenue","Post Shares","Cost Per Post Share","PostEngagement","CostPerPostEngagement","ProductLine",
				"Post Comments","Pre-View");
		
		for(int i=0;i<titleList.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(titleList.get(i));
		}
		row = sheet.createRow(1);
		int rownum=1;
		Float totalAmountSpend=0f;
		Integer totalLinkClick=0;
		Integer totalAllItemShipped=0;
		Float totalProfit=0f;
		Float totalAdvertisingFee=0f;
		
		Float totalImpressions=0f;
		Float totalCpc=0f;
		Float totalCr=0f;
		Float totalRoi=0f;
		
		for (FacebookDto dto:faceBookList) {
			int j=0;
			row = sheet.createRow(rownum++);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getProduct());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getStarts());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getAudience());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getAge());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getGender());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getCountry());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getPlacement());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getAdId());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getAdName());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getTrackingId());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getAmountSpend());
			totalAmountSpend+=dto.getAmountSpend();
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getLinkClicks());
			totalLinkClick+=dto.getLinkClicks();
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getCtr());
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getCpc());
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getCpm());
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getTotalCr());
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getSameCr());
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getProfit());
			totalProfit+=dto.getProfit();
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getSameItemsShipped());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getAllItemsShipped());
			totalAllItemShipped+=dto.getAllItemsShipped();
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getTotalAdvertisingFees());
			totalAdvertisingFee+=dto.getTotalAdvertisingFees();
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getRoi());
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getRelativeRoi());
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getPageLikes());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getCostPerPageLike());
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getImpressions());
			totalImpressions+=dto.getImpressions();
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getTotalRevenue());
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getPostShares());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getCostPerPostShare());
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getPostEngagement());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getCostPerPostEngagement());
			row.getCell(j-1).setCellStyle(cellStyle);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getProductLine());
		
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getPostComments());
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getPreView());
		}
		Float totalCtr=0f;
		if(totalImpressions>0){
			totalCtr=totalLinkClick*100/totalImpressions;
		}
		if(totalLinkClick>0){
			totalCpc=totalAmountSpend/totalLinkClick;
			totalCr=totalAllItemShipped*100f/totalLinkClick;
		}
		if(totalAmountSpend>0){
			totalRoi=(totalProfit*1.5f+totalAdvertisingFee)/totalAmountSpend;
		}
		
		row = sheet.createRow(rownum++);
		int j=0;
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("TOTAL");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalAmountSpend);
		row.getCell(j-1).setCellStyle(cellStyle);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalLinkClick);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalCtr);
		row.getCell(j-1).setCellStyle(cellStyle);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalCpc);
		row.getCell(j-1).setCellStyle(cellStyle);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalCr);
		row.getCell(j-1).setCellStyle(cellStyle);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalProfit);
		row.getCell(j-1).setCellStyle(cellStyle);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalAllItemShipped);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalAdvertisingFee);
		row.getCell(j-1).setCellStyle(cellStyle);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalRoi);
		row.getCell(j-1).setCellStyle(cellStyle);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(j-1).setCellStyle(cellStyle);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(j-1).setCellStyle(cellStyle);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalImpressions);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(j-1).setCellStyle(cellStyle);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(j-1).setCellStyle(cellStyle);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.getCell(j-1).setCellStyle(cellStyle);
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		
		for (int i = 0; i < titleList.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
	    }
		try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
	            String fileName = "data.xls";
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
