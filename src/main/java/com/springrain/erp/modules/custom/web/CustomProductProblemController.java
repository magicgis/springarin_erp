package com.springrain.erp.modules.custom.web;

import java.io.OutputStream;
import java.net.URLEncoder;
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
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.ReturnGoods;
import com.springrain.erp.modules.amazoninfo.service.ReturnGoodsService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.custom.entity.CustomProductProblem;
import com.springrain.erp.modules.custom.entity.CustomProductTypeProblems;
import com.springrain.erp.modules.custom.service.CustomProductProblemService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;

/**
 * 邮件Controller
 * @author Michael
 * @version 2014-04-30
 */
@Controller
@RequestMapping(value = "${adminPath}/custom/productProblem")
public class CustomProductProblemController extends BaseController {
	
	@Autowired
	private CustomProductProblemService	 customProductProblemService;
	@Autowired
	private PsiProductService psiProductService;
	@Autowired
	private SaleReportService  saleService;
	@Autowired
	private ReturnGoodsService returnGoodsService;
	@Autowired
	private PsiProductTypeGroupDictService dictService;
	
	@RequestMapping(value = {"list", ""})
	public String list(CustomProductProblem problem, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(problem.getCreateDate()==null){
			problem.setCreateDate(sdf.parse(sdf.format(new Date())));
		}
		if(problem.getDataDate()==null){
			problem.setDataDate(DateUtils.addMonths(problem.getCreateDate(), -1));
		}
		Map<String,Integer>  problemNumMap = Maps.newHashMap(); 
		Map<String,Map<String,String>>  problemMap = customProductProblemService.getProblems(problem,problemNumMap);
		
		model.addAttribute("productMap", this.psiProductService.findProductTypeMap());
		model.addAttribute("mangerMap", this.psiProductService.findManagerProductTypeMap());//查询产品经理
		//查询销量
		model.addAttribute("saleMap",this.saleService.getSalesByDate(problem.getDataDate(), DateUtils.addDays(problem.getCreateDate(),1), problem.getCountry()));
		model.addAttribute("productProblem", problem);
		 model.addAttribute("problemNumMap", problemNumMap);
        model.addAttribute("problemMap", problemMap);
        ReturnGoods returnGoods=new ReturnGoods();
        returnGoods.setStartDate(problem.getDataDate());
        returnGoods.setReturnDate(problem.getCreateDate());
        returnGoods.setCountry(problem.getCountry());
		Map<String,Object[]> returnMap=returnGoodsService.findAllReturnCommentInfo(returnGoods);
		model.addAttribute("returnMap", returnMap);
		return "modules/custom/customMasterProblemList";
	}

	
	@RequestMapping(value = {"detail"})
	public String detail(CustomProductProblem productProblem, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		List<CustomProductProblem>  problems = customProductProblemService.getProblemDetails(productProblem);
		Map<String, String> masterMap = customProductProblemService.findMaster(problems);
		model.addAttribute("productProblem", productProblem);
		model.addAttribute("problems", problems);
		model.addAttribute("masterMap", masterMap);
		return "modules/custom/customDetailProblemList";
	}
	
	
	@RequestMapping(value = "expProblem")
	public String expProblem(CustomProductProblem productProblem,HttpServletRequest request,HttpServletResponse response, Model model) {
        List<CustomProductProblem>  customEmails = customProductProblemService.getProblemDetails(productProblem); 
       Map<String,String> productMap = this.psiProductService.findProductTypeMap();
	   Map<String,String> mangerMap  = this.psiProductService.findManagerProductTypeMap();//查询产品经理
	   //产品类型对应的产品线关系
	   Map<String, String> typeLine = dictService.getTypeLine(null);
	   SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		
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
		  
		  CellStyle titleStyle = wb.createCellStyle();
		  titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		  HSSFFont font1 = wb.createFont();
		  font1.setFontHeightInPoints((short) 15); // 字体高度
		  font1.setFontName(" 黑体 "); // 字体
		  font1.setBoldweight((short) 15);
		  titleStyle.setFont(font1);
	
		  HSSFCell cell = null;		
		  List<String> title=Lists.newArrayList("Product Name","Data Sourse","Product Line","Product Type","Country","Create Date","Problem Type", "Problem Detail",  "Product Manager","Order Nos","Id Or Email","Problem Editor");
		  for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
		 int rowIndex=1;
         if(customEmails!=null){
        	 Map<String, String> masterMap = customProductProblemService.findMaster(customEmails);
		    	for (int i=0;i<customEmails.size();i++) {
		    		CustomProductProblem  custom=customEmails.get(i);
	    			int j=0;
		    		row=sheet.createRow(rowIndex++);
		    		row.setHeight((short) 400);
		    		String type =productMap.get(custom.getProductName());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(custom.getProductName());
		    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("1".equals(custom.getpKey().getDataType())?"邮件":"事件");
		    		if (StringUtils.isNotEmpty(type)) {
						if (typeLine.get(type.toLowerCase())!=null) {
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(typeLine.get(type.toLowerCase())+"线");
						} else {
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
					} else {
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
		    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(type);
		    		
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(custom.getCountry())?"us":custom.getCountry());  
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sdf1.format(custom.getCreateDate()));
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(custom.getProblemType());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(custom.getProblem());
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(mangerMap.get(type));
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(custom.getOrderNos());
		    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("1".equals(custom.getpKey().getDataType())?custom.getRevertEmail():custom.getpKey().getDataId());
		    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(masterMap.get(custom.getpKey().getDataId())==null?"":masterMap.get(custom.getpKey().getDataId()));
		    		}
	          
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
		
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		
				String fileName = "productProblem" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
         }
		return null;
	}
	
	@RequestMapping(value = "problems")
	public String problems(CustomProductTypeProblems problems, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<CustomProductTypeProblems> list = customProductProblemService.findAllProblemType(problems);
		model.addAttribute("list", list);
		model.addAttribute("problems", problems);
		return "modules/custom/customProblemTypeList";
	}
	
	@RequestMapping(value = "form")
	public String form(CustomProductTypeProblems problems, HttpServletRequest request, HttpServletResponse response, Model model) {
		if (problems.getId() != null) {
			problems = customProductProblemService.getProblemTypeById(problems.getId());
		}
		model.addAttribute("problems", problems);
		return "modules/custom/customProblemTypeForm";
	}
	
	@RequestMapping(value = "save")
	public String save(CustomProductTypeProblems problems, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		problems.setProductType(Encodes.unescapeHtml(problems.getProductType()));
		if (problems.getId() == null && 
				customProductProblemService.getProblemTypeByTypeAndProblem(
						problems.getProductType(), problems.getProblemType())!=null) {	//新建判重复
			addMessage(redirectAttributes, "操作失败,该问题类型已存在,不能重复添加！");
			return "redirect:"+Global.getAdminPath()+"/custom/productProblem/problems/?repage";
		}
		problems.setDelFlag("0");
		customProductProblemService.saveProblemType(problems);
		addMessage(redirectAttributes, "操作成功");
		return "redirect:"+Global.getAdminPath()+"/custom/productProblem/problems/?repage";
	}
	
	@RequestMapping(value = "delete")
	public String delete(CustomProductTypeProblems problems, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		if (problems.getId() != null) {
			problems = customProductProblemService.getProblemTypeById(problems.getId());
			problems.setDelFlag("1");
			customProductProblemService.saveProblemType(problems);
		}
		addMessage(redirectAttributes, "操作成功");
		return "redirect:"+Global.getAdminPath()+"/custom/productProblem/problems/?repage";
	}

}
