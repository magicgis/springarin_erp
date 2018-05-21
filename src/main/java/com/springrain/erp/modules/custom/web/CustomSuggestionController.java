package com.springrain.erp.modules.custom.web;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.custom.entity.CustomProductProblem;
import com.springrain.erp.modules.custom.entity.CustomSuggestion;
import com.springrain.erp.modules.custom.scheduler.CustomEmailMonitor;
import com.springrain.erp.modules.custom.service.CustomEmailService;
import com.springrain.erp.modules.custom.service.CustomSuggestionService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 客户建议Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/custom/suggestion/")
public class CustomSuggestionController extends BaseController {

	@Autowired
	private CustomSuggestionService customSuggestionService;
	
	@Autowired
	private CustomEmailService customEmailService;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private PsiProductTypeGroupDictService dictService;

	
	@RequestMapping(value = {"list", ""})
	public String list(CustomSuggestion suggestion, HttpServletRequest request, HttpServletResponse response, Model model) {
        if (suggestion.getCreateDate() == null) {
        	Date date = new Date();
        	date.setHours(0);
        	date.setMinutes(0);
        	date.setSeconds(0);
        	suggestion.setCreateDate(DateUtils.addMonths(date, -1));
        	suggestion.setEndDate(date);
		}
		List<CustomSuggestion> list = customSuggestionService.find(suggestion); 
        model.addAttribute("list", list);
        model.addAttribute("suggestion", suggestion);
        //查询产品经理[产品类型  name]
		model.addAttribute("mangerMap", this.psiProductService.findManagerProductTypeMap());
		return "modules/custom/customSuggestionList";
	}
	
	//保存客户建议
	@ResponseBody
	@RequestMapping(value = {"ajaxSave"})
	public String ajaxSave(String country, String productName, String content, String emailId, Integer id){
		try{
			Map<String, String> productTypeMap = psiProductService.findProductTypeMap();
			content = URLDecoder.decode(content, "utf-8");
			CustomEmail customEmail = customEmailService.get(emailId);
			CustomSuggestion suggestion = null;
			if (id != null) {
				suggestion = customSuggestionService.getSuggestionById(id);
			}
			if (suggestion == null) {
				suggestion = customSuggestionService.getByEmailId(emailId);
			}
			if (suggestion == null) {
				suggestion = new CustomSuggestion();
				suggestion.setCreateDate(new Date());
			}
			suggestion.setCountry(country);
			suggestion.setProductName(productName);
			suggestion.setCustomEmail(customEmail);
			suggestion.setProductType(productTypeMap.get(suggestion.getProductName()));
			suggestion.setContent(content);
			customSuggestionService.saveSuggestion(suggestion);
		}catch(Exception e){
			logger.error("保存客户建议失败", e);
			return "保存客户建议失败" + e.getMessage();
		}
		return "0";
	}
	
	@RequestMapping(value = "expSuggestion")
	public String expSuggestion(CustomSuggestion suggestion, HttpServletRequest request,HttpServletResponse response, Model model) {
        List<CustomSuggestion> suggestions = customSuggestionService.find(suggestion); 
	    Map<String,String> mangerMap  = this.psiProductService.findManagerProductTypeMap();//查询产品经理
	    //产品类型对应的产品线关系
	    Map<String, String> typeLine = dictService.getTypeLine(null);
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
		contentStyle.setWrapText(true);

		HSSFCell cell = null;		
		List<String> title=Lists.newArrayList("Country","Product Line","Product Type","Product Name", 
				"Product Manager", "Create Date", "Order Nos", "Email", "Content");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		 int rowIndex=1;
		 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
         if(suggestions!=null){
		    	for (int i=0;i<suggestions.size();i++) {
		    		CustomSuggestion customSuggestion = suggestions.get(i);
	    			int j=0;
		    		row=sheet.createRow(rowIndex++);
		    		row.setHeight((short) 400);
		    		//Country
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(customSuggestion.getCountry())?"us":customSuggestion.getCountry());  
		    		String type = customSuggestion.getProductType();
					//Product Line
		    		if (typeLine.get(type.toLowerCase())!=null) {
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(typeLine.get(type.toLowerCase())+"线");
					} else {
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
		    		//Product Type
		    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(type);
		    		//Product Name
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(customSuggestion.getProductName());
		    		//Product Manager
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(mangerMap.get(type));
		    		//Create Date
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(format.format(customSuggestion.getCreateDate()));
		    		//Order Nos
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(customSuggestion.getCustomEmail().getOrderNos());
		    		//Email
		    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(customSuggestion.getCustomEmail().getRevertEmail());
		    		//Content
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(customSuggestion.getContent());
		    	}
		    	for (int i = 0; i < rowIndex - 1; i++) {
					for (int j = 0; j < title.size(); j++) {
						sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
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
		
				String fileName = "CustomerSuggestion" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				logger.error("导出客户建议内容异常！", e);
			}
         }
		return null;
	}
	
}
