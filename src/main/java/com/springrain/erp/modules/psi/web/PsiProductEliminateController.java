package com.springrain.erp.modules.psi.web;

import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.SalesForecastServiceByMonth;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductMoldFeeService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/psi/productEliminate")
public class PsiProductEliminateController extends BaseController {
	private final static Logger LOGGER = LoggerFactory.getLogger(PsiProductEliminateController.class);
	@Autowired
	private PsiProductEliminateService productEliminateService;
	
	@Autowired
	private SalesForecastServiceByMonth salesForecastService;
	
	@Autowired
	private MailManager mailManager;

	@Autowired
	private PsiProductAttributeService productAttributeService;
	
	@Autowired
	private  LcPsiTransportOrderService  psiTransportOrderService;
	
	@Autowired
	private PsiProductMoldFeeService psiProductMoldFeeService;
	//符合C销售预测方案的产品类型,暂时有四种
//	private static List<String> cTypeList = 
//			Lists.newArrayList("Express card","HDD enclosures","Kindle cover","Tablet PC bag");

	@ModelAttribute
	public PsiProductEliminate get(@RequestParam(required = false) Integer id) {
		if (id != null) {
			return productEliminateService.get(id);
		} else {
			return new PsiProductEliminate();
		}
	}
	
	@RequestMapping(value = { "list", "" })
	public String list(PsiProductEliminate productEliminate, HttpServletRequest request,HttpServletResponse response, Model model) {
		Map<String, Map<String, List<String>>> rs = productEliminateService.findAll();
		List<String> list = Lists.newArrayList();
		for (String string : rs.keySet()) {
			list.add(string);
		}
		model.addAttribute("list", list);
		model.addAttribute("products", rs);
		return "modules/psi/psiProductEliminateList";
	}
	
	//新品明细
	@RequestMapping(value = { "isNewlist"})
	public String isNewlist(PsiProductEliminate productEliminate, HttpServletRequest request,HttpServletResponse response, Model model) {
		Map<String, Map<String, List<String>>> rs = productEliminateService.findIsNewAll();
		List<String> list = Lists.newArrayList();
		for (String string : rs.keySet()) {
			list.add(string);
		}
		model.addAttribute("list", list);
		model.addAttribute("products", rs);
		return "modules/psi/psiProductIsNewList";
	}
	
	//上架日期
	@RequestMapping(value = { "addedMonthlist"})
	public String addedMonthlist(PsiProductEliminate productEliminate, HttpServletRequest request,HttpServletResponse response, Model model) {
		Map<String, Map<String, List<String>>> rs = productEliminateService.findAddedMonthAll();
		List<String> list = Lists.newArrayList();
		for (String string : rs.keySet()) {
			list.add(string);
		}
		model.addAttribute("list", list);
		model.addAttribute("products", rs);
		return "modules/psi/psiProductAddedMonthList";
	}
	
	//销售预测方案明细
	@RequestMapping(value = { "forecastlist"})
	public String forecastlist(PsiProductEliminate productEliminate, HttpServletRequest request,HttpServletResponse response, Model model) {
		Map<String, Map<String, List<String>>> rs = productEliminateService.findForecastSchemeAll();
		List<String> list = Lists.newArrayList();
		for (String string : rs.keySet()) {
			list.add(string);
		}
		model.addAttribute("list", list);
		model.addAttribute("products", rs);
		return "modules/psi/psiProductForecastList";
	}
	
	//产品定位
	@RequestMapping(value = { "setPosition"})
	public String setPosition(String productName, String flag, HttpServletRequest request,HttpServletResponse response, Model model) {
		if (StringUtils.isEmpty(flag)) {
			flag = "1";
		}
		List<PsiProductEliminate> list = productEliminateService.findProductEliminateList(productName, null, null, null, null);
		boolean hasMulColor = false;
		if (list.size() > 0) {
			PsiProduct product = list.get(0).getProduct();
			if (StringUtils.isNotEmpty(product.getColor()) && product.getColor().split(",").length>1) {
				hasMulColor = true;
			}
		}
		List<String> productNames = Lists.newArrayList();
		Map<String, Map<String, List<String>>> rs = productEliminateService.findAll();
		for (String string : rs.keySet()) {
			productNames.add(string);
		}
		model.addAttribute("productNames", productNames);
		model.addAttribute("hasMulColor", hasMulColor);
		model.addAttribute("flag", flag);	//1设置淘汰属性  2 设置主力属性 3：设置上架时间 4:设置销售预测方案
		model.addAttribute("productName", productName);
		model.addAttribute("list", list);
		return "modules/psi/psiProductPositionEdit";
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateIsSale"})
	public String updateIsSale(String productName, @RequestParam(required=false)String platform, String isSale, String colorSync) {
		try {
			productEliminateService.updateIsSale(productName, platform, isSale, colorSync);
			String  temp = "";
        	String name = productName;
        	String platformStr = "";
        	if (StringUtils.isEmpty(platform)) {
        		platformStr = "全球";
			} else {
				platformStr = SystemService.countryNameMap.get(platform);
			}
        	if ("1".equals(colorSync)) {	//同步颜色
        		temp = "(所有颜色)";
        		name = name.split("_")[0];
			}
			if ("4".equals(isSale)) {	//淘汰时发邮件
				try {
					psiProductMoldFeeService.updateProfit(productName);
				} catch (Exception e) {
					logger.error(productName+"淘汰产品利润统计模具费用异常", e);
				}
	        	logger.info("产品"+name+temp+"已淘汰,淘汰范围："+platformStr+",操作人:"+UserUtils.getUser().getName());
				//邮件通知相关人员
	        	StringBuffer contents= new StringBuffer("");
	        	contents.append("<p><span style='font-size:20px'>Hi all,<br/>&nbsp;&nbsp;&nbsp;&nbsp;产品"+name+temp+"已淘汰,淘汰范围："+platformStr+"。<br/>&nbsp;&nbsp;&nbsp;&nbsp;以上请知悉。</span></p>");
				String toAddress="supply-chain@inateck.com,amazon-sales@inateck.com,after-sales@inateck.com,pmg@inateck.com,frank@inateck.com,maik@inateck.com,marketing_dept@inateck.com";
				final MailInfo mailInfo = new MailInfo(toAddress,name + "产品淘汰提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
				mailInfo.setContent(contents.toString());
				mailInfo.setCcToAddress("erp_development@inateck.com");
				new Thread(){
				    public void run(){
						mailManager.send(mailInfo);
					}
				}.start();
			} else {
	        	logger.info("产品"+name+temp+"更新产品定位为"+isSale+",范围："+platformStr+",操作人:"+UserUtils.getUser().getName());
			}
			return "true";
		} catch (Exception e) {
			logger.error(productName + "状态修改失败！", e);
			return "false";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = {"batchUpdateIsSale"})
	public String batchUpdateIsSale(@RequestParam(required=true)String productName, @RequestParam(required=true)String isSaleStr, String colorSync) {
		try {
			//查询该产品的淘汰情况
			List<String> countryList = Lists.newArrayList();
			List<String> saleCountryList = Lists.newArrayList();
			Map<String, String> isSaleMap = productEliminateService.findProductPositionByName(productName);
			for (String countrySale : isSaleStr.split(",")) {
				String platform = countrySale.split("_")[0];
				String isSale = countrySale.split("_")[1];
				if (!isSale.equals(isSaleMap.get(platform))) {	//与数据库现在的在售属性对比
					productEliminateService.updateIsSale(productName, platform, isSale, colorSync);
					if ("4".equals(isSale)) {	//记住改为淘汰的平台
						countryList.add(platform);
					} else {
						saleCountryList.add(platform);
					}
				}
			}
        	String  temp = "";
        	String name = productName;
        	if ("1".equals(colorSync)) {	//同步颜色
        		temp = "(所有颜色)";
        		name = name.split("_")[0];
			}
			if (countryList.size() > 0) {
				try {
					psiProductMoldFeeService.updateProfit(productName);
				} catch (Exception e) {
					logger.error(productName+"淘汰产品利润统计模具费用异常", e);
				}
				StringBuilder platformStr = new StringBuilder();
	        	if (countryList.size() == isSaleMap.keySet().size()) {
	        		platformStr.append("全球");
				} else {
					for (String platform : countryList) {
						platformStr.append(SystemService.countryNameMap.get(platform)).append(",");
					}
					platformStr = new StringBuilder(platformStr.substring(0, platformStr.length()-1));
				}
	        	logger.info("产品"+name+temp+"已淘汰,淘汰范围："+platformStr+",操作人:"+UserUtils.getUser().getName());
				//邮件通知相关人员
	        	StringBuffer contents= new StringBuffer("");
	        	contents.append("<p><span style='font-size:20px'>Hi all,<br/>&nbsp;&nbsp;&nbsp;&nbsp;产品"+name+temp+"已淘汰,淘汰范围："+platformStr+"。<br/>&nbsp;&nbsp;&nbsp;&nbsp;以上请知悉。</span></p>");
				String toAddress="supply-chain@inateck.com,amazon-sales@inateck.com,after-sales@inateck.com,pmg@inateck.com,frank@inateck.com,maik@inateck.com,marketing_dept@inateck.com";
				final MailInfo mailInfo = new MailInfo(toAddress,name + "产品淘汰提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
				mailInfo.setContent(contents.toString());
				mailInfo.setCcToAddress("erp_development@inateck.com");
				new Thread(){
				    public void run(){
						mailManager.send(mailInfo);
					}
				}.start();
			} else if(saleCountryList.size() > 0) {
	        	StringBuilder platformStr = new StringBuilder();
	        	if (saleCountryList.size() == isSaleMap.keySet().size()) {
	        		platformStr.append("全球");
				} else {
					for (String platform : saleCountryList) {
						platformStr.append(SystemService.countryNameMap.get(platform)).append(",");
					}
					platformStr = new StringBuilder(platformStr.substring(0, platformStr.length()-1));
				}
	        	logger.info("产品"+name+temp+"更新产品定位"+isSaleStr+",范围："+platformStr+",操作人:"+UserUtils.getUser().getName());
			}
			return "true";
		} catch (Exception e) {
			logger.error(productName + "状态修改失败！", e);
			return "false";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateForecast"})
	public String updateForecast(String productName, @RequestParam(required=false)String platform, String forecast, String colorSync, String isC) {
		try {
			productEliminateService.updateForecast(productName, platform, forecast, colorSync, isC);
			return "true";
		} catch (Exception e) {
			logger.error(productName + "状态修改失败！", e);
			return "false";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "addedMonth")
	public String addedMonth(String productName, String addedMonth, String colorSync) {
		if(StringUtils.isNotEmpty(addedMonth)){
			addedMonth =  DateUtils.getDate(new Date(addedMonth),"yyyy-MM-dd");
		}
		productEliminateService.updateAddedMonth(productName, addedMonth, colorSync);
		return "1";
	}
	
	//分平台属性导出
	@RequestMapping(value = "exportProductDetail")
	public String exportProductDetail(HttpServletRequest request,HttpServletResponse response, Model model) {
		List<PsiProductEliminate> list = productEliminateService.findProductEliminateList(null, null, null, null, null);
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

		CellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		contentStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		contentStyle1.setWrapText(true);
		HSSFCell cell = null;
		
		List<String> title = Lists.newArrayList("产品名称", "国家", "产品类型", "是否在售", "是否新品", "上架时间", "运输方式");
		Map<String, Map<String, Integer>> transportTypeMap = productAttributeService.findtransportType();
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		List<Integer> index = new ArrayList<Integer>();
		int rowIndex = 1;
		for (int i = 0; i < list.size(); i++) {
			PsiProductEliminate eliminate = list.get(i);
			int j = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			 if("0".equals(eliminate.getIsSale())){
				index.add(rowIndex - 1);
			}
			String productName = eliminate.getProductName();
			if (StringUtils.isNotEmpty(eliminate.getColor())) {
				productName = productName + "_" + eliminate.getColor();
			}
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(productName);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(eliminate.getCountry());
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(eliminate.getProduct().getType());
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("4".equals(eliminate.getIsSale()) ? "不可售" : "可销售");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("1".equals(eliminate.getIsNew()) ? "新品" : "非新品");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(eliminate.getAddedMonth());
			String transportType = "";
			try {//Inateck Old & Inateck Other无运输方式
    			transportType = transportTypeMap.get(productName).get(eliminate.getCountry())==1?"海运":"空运";
			} catch (Exception e) {}
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(transportType);
		}

		for (int i = 0; i < rowIndex - 1; i++) {
			for (int j = 0; j < title.size(); j++) {
				if (index.contains(i + 1)) {
					sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle1);
				} else {
					sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
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

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");

			String fileName = "产品分平台属性导出" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("产品分平台属性导出异常", e);
		}
		return null;
	}
	
	@RequestMapping(value = { "piPrice"})
	public String piPrice(PsiProductEliminate productEliminate, HttpServletRequest request,HttpServletResponse response, Model model) {
		Map<String,Map<String,PsiProductEliminate>> rs = productEliminateService.findAllByNameAndCountry();
		model.addAttribute("products", rs);
		return "modules/psi/psiProductPiPriceList";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"updatePiPrice"})
	public String updatePiPrice(String name,String type,String country,Float price) {
		try {
			productEliminateService.updatePiPrice(name,type,country,price);
			return "true";
		} catch (Exception e) {
			logger.error(name + "状态修改失败！", e);
			return "false";
		}
	}
	
	
	@RequestMapping(value = "uploadFile")
	@ResponseBody
	public String uploadFile(@RequestParam("excel")MultipartFile excelFile,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		LOGGER.info("update pi start");
		try {
			    
				try {
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/piPrice/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					File baseDir = new File(baseDirStr); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String name = excelFile.getOriginalFilename();
					File dest = new File(baseDir,name);
					if(dest.exists()){
						dest.delete();
					}
					FileUtils.copyInputStreamToFile(excelFile.getInputStream(),dest);
				}catch(Exception e){}	
			
				Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				// 循环行Row
				Map<String,Double> cnpiMap=Maps.newHashMap();
				Map<String,Double> piMap=Maps.newHashMap();
				
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null||row.getCell(0)==null) {
						continue;
					}
				    String name=StringUtils.trim(row.getCell(0).getStringCellValue());
				    try{
				    	 double cnPi=row.getCell(1).getNumericCellValue();
				    	 if(cnPi>=0){
				    		 //productEliminateService.updateCNPiPrice(name,cnPi);
				    		 cnpiMap.put(name,cnPi);
				    	 }
				    }catch(Exception e){
				    }
				    try{
				    	 double usPrice=row.getCell(2).getNumericCellValue();
				    	 if(usPrice>=0){
				    		// productEliminateService.updatePiPrice(name,usPrice,"com");
				    		 piMap.put(name+"_com", usPrice);
				    	 }
				    }catch(Exception e){
				    }
				    try{
				    	 double euPrice=row.getCell(3).getNumericCellValue();
				    	 if(euPrice>=0){
				    		// productEliminateService.updatePiPrice(name,euPrice,"de");
				    		 piMap.put(name+"_de", euPrice);
				    	 }
				    }catch(Exception e){
				    }
				    try{
				    	 double jpPrice=row.getCell(4).getNumericCellValue();
				    	 if(jpPrice>=0){
				    		 //productEliminateService.updatePiPrice(name,jpPrice,"jp");
				    		 piMap.put(name+"_jp", jpPrice);
				    	 }
				    }catch(Exception e){
				    }
				    try{
				    	double caPrice=row.getCell(5).getNumericCellValue();
				    	 if(caPrice>=0){
				    		// productEliminateService.updatePiPrice(name,caPrice,"ca");
				    		 piMap.put(name+"_ca", caPrice);
				    	 }
				    }catch(Exception e){
				    }
				    try{
				    	 double mxPrice=row.getCell(6).getNumericCellValue();
				    	 if(mxPrice>=0){
				    		// productEliminateService.updatePiPrice(name,mxPrice,"mx");
				    		 piMap.put(name+"_mx", mxPrice);
				    	 }
				    }catch(Exception e){
				    }
				}
				
				if(cnpiMap.size()>0){
					productEliminateService.updateCNPiPrice(cnpiMap);
				}
				if(piMap.size()>0){
					productEliminateService.updatePiPrice(piMap);
				}
		} catch (Exception e) {
			LOGGER.error("文件上传失败",e);
			return "1";
		}
		
		LOGGER.info("update pi end");
		return "0";
	}
	
	
		@RequestMapping(value = "exportPIDetail")
		public String exportPIDetail(HttpServletRequest request,HttpServletResponse response, Model model) {
			Map<String,Map<String,PsiProductEliminate>> map=productEliminateService.findAllByNameAndCountry();
			Map<String,String> priceMap=this.psiTransportOrderService.getProPriceByProductId2();
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
			
			HSSFCellStyle cellStyle = wb.createCellStyle();
		    cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		    
			List<String> title = Lists.newArrayList("产品名称","实时CNPI","CNPI","US PI", "DE  PI", "JP PI", "CA PI", "MX PI");
			for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
			int rowIndex = 1;
			List<String> allList=Lists.newArrayList("de","fr","it","es","uk","com","jp","ca","mx");
			List<String> euList=Lists.newArrayList("de","fr","it","es","uk");
			for (Map.Entry<String,Map<String,PsiProductEliminate>> nameMap:map.entrySet()) {
				int j = 0;
				row = sheet.createRow(rowIndex++);
				row.setHeight((short) 400);
				String name=nameMap.getKey();
				Map<String,PsiProductEliminate> temp=nameMap.getValue();
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(name);
				 //含税价 / （1+退税率）  / 6.9*1.15  退税价    除以6.9是美金   乘以1.15
				Double cnPi=0d;
			    if(priceMap.get(name)!=null){
			    	String rs=priceMap.get(name);
			    	//含税价-货币单位-装箱数-是否带电-毛重-退税率
			    	String[] arr=rs.split("_");
			    	if(arr[0]!=null){
			    		Float price=Float.parseFloat(arr[0]);
			    		String currency=arr[1];
			    		//Integer taxRefund=Integer.parseInt(arr[5]);
			    		if("USD".equals(currency)){
			    			price=price*AmazonProduct2Service.getRateConfig().get("USD/CNY");
			    		}
			    		//cnPi=price/6.9*1.18;//不含税价
			    		cnPi=price*1.3/1.17;
			    	}
			    }
			    row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(cnPi);
				row.getCell(j-1).setCellStyle(cellStyle);
				
			    float actualPiPrice=0f;
			    for(String country:allList){
					if(temp.get(country)!=null&&temp.get(country).getCnpiPrice()!=null){
						actualPiPrice=temp.get(country).getCnpiPrice();
						break;
					}
				}
				if(actualPiPrice>0){
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(actualPiPrice);
					row.getCell(j-1).setCellStyle(cellStyle);
				}else{
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				
				if(temp.get("com")!=null&&temp.get("com").getPiPrice()!=null){
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(temp.get("com").getPiPrice());
					row.getCell(j-1).setCellStyle(cellStyle);
				}else{
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				float dePrice=0f;
				for(String country:euList){
					if(temp.get(country)!=null&&temp.get(country).getPiPrice()!=null){
						dePrice=temp.get(country).getPiPrice();
						break;
					}
				}
				if(dePrice>0){
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(dePrice);
					row.getCell(j-1).setCellStyle(cellStyle);
				}else{
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				
				if(temp.get("jp")!=null&&temp.get("jp").getPiPrice()!=null){
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(temp.get("jp").getPiPrice());
					row.getCell(j-1).setCellStyle(cellStyle);
				}else{
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				if(temp.get("ca")!=null&&temp.get("ca").getPiPrice()!=null){
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(temp.get("ca").getPiPrice());
					row.getCell(j-1).setCellStyle(cellStyle);
				}else{
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				if(temp.get("mx")!=null&&temp.get("mx").getPiPrice()!=null){
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(temp.get("mx").getPiPrice());
					row.getCell(j-1).setCellStyle(cellStyle);
				}else{
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				}
			}

			for (int i = 0; i < title.size(); i++) {
				sheet.autoSizeColumn((short) i);
			}

			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");

				String fileName = "PI" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				logger.error("PI", e);
			}
			return null;
		}
		
		@RequestMapping(value = "updateCnpiIsNull")
		public String updateCnpiIsNull(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
			List<String> list=productEliminateService.getCnPiIsNull();
			Map<String,String> priceMap=this.psiTransportOrderService.getProPriceByProductId2();
			if(list!=null&&list.size()>0){
				for(String name:list){
					if(priceMap.get(name)!=null){
					    	String rs=priceMap.get(name);
					    	//含税价-货币单位-装箱数-是否带电-毛重-退税率
					    	String[] arr=rs.split("_");
					    	if(arr[0]!=null){
					    		/*Float price=Float.parseFloat(arr[0]);
					    		String currency=arr[1];
					    		Integer taxRefund=Integer.parseInt(arr[5]);
					    		if("USD".equals(currency)){
					    			price=price*AmazonProduct2Service.getRateConfig().get("USD/CNY");
					    		}
					    		Double cnPi=price/(1+taxRefund/100f)/6.9*1.15;
					    		*/
					    		Float price=Float.parseFloat(arr[0]);
					    		String currency=arr[1];
					    		//Integer taxRefund=Integer.parseInt(arr[5]);
					    		if("USD".equals(currency)){
					    			price=price*AmazonProduct2Service.getRateConfig().get("USD/CNY");
					    		}
					    		//cnPi=price/6.9*1.18;//不含税价
					    		Double cnPi=price*1.3/1.17;//含税价
					    		productEliminateService.updateCNPiPrice(name,cnPi);
					    	}
					    }
				}
			}
			return "redirect:"+Global.getAdminPath()+"/psi/productEliminate/piPrice?repage";
		}

		@ResponseBody
		@RequestMapping(value = {"updateAttrByCountry"})
		public String updateAttrByCountry(PsiProductEliminate psiProductEliminate,String flag) {
			productEliminateService.save(psiProductEliminate);
			if ("de".equals(psiProductEliminate.getCountry()) && psiProductEliminate.getProduct()!=null && psiProductEliminate.getProduct().getFanOu()) {
				//可泛欧产品,德国数据同步更新至欧洲
				productEliminateService.updateAttrByEu(psiProductEliminate);
			}
			return "1";
		}
		
		@ResponseBody
		@RequestMapping(value = {"updateCommissionPcent"})
		public String updateCommissionPcent(PsiProductEliminate psiProductEliminate) {
			productEliminateService.updateCommissionPcent(psiProductEliminate);
			return "1";
		}
	
	
}
