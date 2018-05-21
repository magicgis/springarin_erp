package com.springrain.erp.modules.amazoninfo.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
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
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.TokenizerByLucene;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonKeyword;
import com.springrain.erp.modules.amazoninfo.entity.AmazonKeywordSearch;
import com.springrain.erp.modules.amazoninfo.service.AmazonKeywordService;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/amazonKeyword")
public class AmazonKeywordController extends BaseController {
	@Autowired
	private AmazonKeywordService amazonKeywordService;

	
	@RequestMapping(value = "list")
	public String form(AmazonKeywordSearch amazonKeywordSearch, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AmazonKeywordSearch> page = new Page<AmazonKeywordSearch>(request, response);
		if(amazonKeywordSearch.getCreateDate()==null){
			Date date=new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			amazonKeywordSearch.setCreateDate(DateUtils.addDays(date,-60));
			amazonKeywordSearch.setUpdateDate(date);
		}
		page=amazonKeywordService.find(page,amazonKeywordSearch);
		model.addAttribute("page", page);
		model.addAttribute("amazonKeywordSearch", amazonKeywordSearch);
		return "modules/amazoninfo/amazonKeywordSearchList";
	}

	@RequestMapping(value = "syscKeyword")
	public String syscKeyword(AmazonKeywordSearch amazonKeywordSearch, Model model){
		AmazonKeywordSearch search=amazonKeywordService.get(amazonKeywordSearch.getId()) ;
		Map<String,Integer> sysnMap=Maps.newLinkedHashMap();
		if(search.getItems()!=null&&search.getItems().size()>0){
		   Set<String> titleSet=Sets.newHashSet();
		   for (AmazonKeyword item: search.getItems()) {
			   titleSet.add(item.getTitle());
		   }
			Map<String, Integer> rs = TokenizerByLucene.titleWordsRate(titleSet);
			List<Map.Entry<String, Integer>> listData = new ArrayList<Map.Entry<String,Integer>>(rs.entrySet());  
	        Collections.sort(listData,new Comparator<Map.Entry<String, Integer>>() {  
	            @Override  
	            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {  
	                return (o2.getValue()-o1.getValue());  
	            }  
	        }); 
	        Integer total=0;
	        for(Map.Entry<String,Integer> key:listData){ 
	        	String word=key.getKey();
				Integer num=key.getValue();
				sysnMap.put(word, num);
				total+=num; 
	        } 
			sysnMap.put("totalK", total);
		}
		model.addAttribute("sysnMap", sysnMap);
		return "modules/amazoninfo/amazonKeywordSearchAnalyse";
	}
	
	@RequestMapping(value = "add")
	public String add(AmazonKeywordSearch amazonKeywordSearch, HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("amazonKeywordSearch", amazonKeywordSearch);
		return "modules/amazoninfo/amazonKeywordSearchAdd";
	}

	
	@RequestMapping(value = "save")
	public String save(AmazonKeywordSearch amazonKeywordSearch, HttpServletRequest request, HttpServletResponse response, Model model) {
		amazonKeywordSearch.setCreateDate(new Date());
		amazonKeywordSearch.setCreateUser(UserUtils.getUser());
		amazonKeywordSearch.setState("0");
		amazonKeywordSearch.setUpdateDate(new Date());
		amazonKeywordService.saveSearch(amazonKeywordSearch);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonKeyword/list?country="+amazonKeywordSearch.getCountry();
	}
	
	@RequestMapping(value = "cancel")
	public String cancel(AmazonKeywordSearch amazonKeywordSearch, HttpServletRequest request, HttpServletResponse response, Model model) {
		amazonKeywordSearch=amazonKeywordService.get(amazonKeywordSearch.getId()) ;
		amazonKeywordSearch.setState("1");
		amazonKeywordService.saveSearch(amazonKeywordSearch);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonKeyword/list?country="+amazonKeywordSearch.getCountry();
	}
	
	@RequestMapping(value = "searchKey")
	public String searchKey(AmazonKeywordSearch amazonKeywordSearch, HttpServletRequest request, HttpServletResponse response, Model model) {
		amazonKeywordSearch=amazonKeywordService.get(amazonKeywordSearch.getId()) ;
		amazonKeywordSearch.setState("0");
		amazonKeywordSearch.setUpdateDate(new Date());
		amazonKeywordService.saveSearch(amazonKeywordSearch);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonKeyword/list?country="+amazonKeywordSearch.getCountry();
	}
	
	
	@RequestMapping(value = "export")
	public String export(AmazonKeywordSearch amazonKeywordSearch, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AmazonKeywordSearch> page = new Page<AmazonKeywordSearch>(request, response,-1);
		page.setPageSize(60000);
		page=amazonKeywordService.find(page,amazonKeywordSearch);
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
			HSSFCell cell = null;
			List<String> title = Lists.newArrayList("国家","关键词","词","出现次数");
			for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
		    }
			int rownum=1;
		  for (AmazonKeywordSearch search: page.getList()) {
			if(search.getItems()!=null&&search.getItems().size()>0){
				   Set<String> titleSet=Sets.newHashSet();
				   for (AmazonKeyword item: search.getItems()) {
					   titleSet.add(item.getTitle());
				   }
					Map<String, Integer> rs = TokenizerByLucene.titleWordsRate(titleSet);
					List<Map.Entry<String, Integer>> listData = new ArrayList<Map.Entry<String,Integer>>(rs.entrySet());  
			        Collections.sort(listData,new Comparator<Map.Entry<String, Integer>>() {  
			            @Override  
			            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {  
			                return (o2.getValue()-o1.getValue());  
			            }  
			        }); 
			
			        for(Map.Entry<String,Integer> key:listData){ 
			        	String word=key.getKey();
						Integer num=key.getValue();
						row=sheet.createRow(rownum++);
						int j=0;
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(("com".equals(search.getCountry())?"us":search.getCountry()).toUpperCase());
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(search.getKeyword());
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(word);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(num);
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
         String fileName ="keyword" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";
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
