package com.springrain.erp.modules.amazoninfo.web;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomFilter;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomer;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.custom.service.CustomEmailService;

/**
 * 亚马逊后台账号信息Controller
 * @author Tim
 * @version 2015-01-14
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/customers")
public class AmazonCustomerController extends BaseController {

	@Autowired
	private AmazonCustomerService amazonCustomerService;
	
	@Autowired
	private CustomEmailService customEmailService;
	
	@ModelAttribute
	public AmazonCustomer get(@RequestParam(required=false) String amzEmail,@RequestParam(required=false) String customId ) {
		if (!StringUtils.isBlank(amzEmail)){
			AmazonCustomer customer=amazonCustomerService.get(amzEmail);
			if(customer==null){
				return new AmazonCustomer();
			}
			return customer;
		}else if(StringUtils.isNotBlank(customId)){
            return amazonCustomerService.getByCustomId(customId); 
		}else{
			return new AmazonCustomer();
		}
	}
	
	@RequiresPermissions("amazoninfo:amazonCustomers:view")
	@RequestMapping(value = {"count"})
	public String count(AmazonCustomer amazonCustomer, HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("dataTotal", amazonCustomerService.countCustomers());
		model.addAttribute("dataByCountry", amazonCustomerService.countCustomersByCountry());
		model.addAttribute("returnBlack", amazonCustomerService.getBlackCustomers());
		return "modules/amazoninfo/amazonCustomerCount";
	}
	
	@RequiresPermissions("amazoninfo:amazonCustomers:view")
	@RequestMapping(value = {"view"})
	public String view(AmazonCustomer amazonCustomer, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(amazonCustomer==null){
			throw new RuntimeException("Amazon customer does not exist");
		}
		
		model.addAttribute("customer",amazonCustomer);
		String amzEmail=amazonCustomer.getAmzEmail();
		String email=amazonCustomer.getEmail();
		Set<String> emaliSet=Sets.newHashSet();
		if(StringUtils.isNotBlank(amzEmail)){
			emaliSet.add(amzEmail);
		}
		if(StringUtils.isNotBlank(email)){
			String[] emailArr=email.split(",");
			for (String arr: emailArr) {
				if(StringUtils.isNotBlank(arr)){
					emaliSet.add(arr);
				}
			}
		}
		if(emaliSet!=null&&emaliSet.size()>0){
			Map<String,CustomEmail> emailMap=customEmailService.findAllTypeEmail(emaliSet);
			model.addAttribute("emailMap",emailMap);
			Map<String,SendEmail> sendEmailMap=customEmailService.findSendEmail(emaliSet);
			model.addAttribute("sendEmailMap",sendEmailMap);
		}
		return "modules/amazoninfo/amazonCustomer";
	}
	
	@ResponseBody
	@RequiresPermissions("amazoninfo:amazonCustomers:view")
	@RequestMapping(value = {"save"})
	public String save(AmazonCustomer amazonCustomer, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isNotBlank(amazonCustomer.getEmail())){
			if(amazonCustomer.getEmail().contains(",")){
				String[] emailArr=amazonCustomer.getEmail().split(",");
				String tempEmail="";
				for(String arr:emailArr){
					 if(arr.startsWith("erp")){
						 String[] temp=arr.split("@");
						 arr=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
					 }
					 tempEmail+=arr+",";
				}
				amazonCustomer.setEmail(tempEmail.substring(0,tempEmail.length()-1));
			}else{
				if(amazonCustomer.getEmail().startsWith("erp")){
					 String mail=amazonCustomer.getEmail();
					 String[] temp=mail.split("@");
					 mail=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
					 amazonCustomer.setEmail(mail);
				}
			}
		}
		amazonCustomerService.saveEmail(amazonCustomer);
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateEmail"})
	public String updateEmail(String customerId,String email,String amzEmail, HttpServletRequest request, HttpServletResponse response, Model model) {
		amazonCustomerService.updateEmail(customerId, email,amzEmail);
		return "1";
	}
	
	
	@RequiresPermissions("amazoninfo:amazonCustomers:view")
	@RequestMapping(value = {"query"})
	public String query(AmazonCustomFilter customFilter, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(customFilter.getEndDate()==null){
			Date end = DateUtils.getDateStart(new Date());
			customFilter.setEndDate(end);
			customFilter.setStartDate(DateUtils.addMonths(end, -3));
		}else{
			model.addAttribute("data", amazonCustomerService.query(false, customFilter));
			model.addAttribute("total", amazonCustomerService.queryCount(customFilter));
		}
		model.addAttribute("customFilter", customFilter);
		return "modules/amazoninfo/amazonCustomerQuery";
	}
	
	@RequiresPermissions("amazoninfo:amazonCustomers:view")
	@RequestMapping(value = {"export"})
	public String export(AmazonCustomFilter customFilter, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Object[]> list = amazonCustomerService.query(true, customFilter);
		if(list.size()>65535){
		    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
		}
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		String[] title = { "CustomerId","Country", "Ama_Email", "Email","Purchase History"};

		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
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
		// 设置字体
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);
		HSSFCell cell = null;
		for (int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}

		HSSFCell cell1 = null;
		String productName1 = customFilter.getPn1();
		// 输出亚马逊excel订单
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(i + 1);
			Object[] objs = list.get(i);
			row.createCell((short) 0).setCellValue(
					objs[0].toString());
			String country = objs[1].toString().toUpperCase();
			if("COM".equals(country)){
				country = "US";
			}
			row.createCell((short) 1).setCellValue(country);
			row.createCell((short) 2).setCellValue(
					objs[2] == null ? " " : objs[2].toString());
			row.createCell((short) 3).setCellValue(objs[3] == null ? " " : objs[3].toString());
			//String ph = objs[4].toString();
			StringBuilder buf=new StringBuilder(objs[4].toString());
			if(StringUtils.isNotEmpty(productName1)){
				List<String> tempList = Lists.newArrayList(buf.toString().split(","));
				String nn = "";
				for (String tn : tempList) {
					if(tn.toLowerCase().contains(productName1.toLowerCase())){
						nn = tn;
						break;
					}
				}
				if(nn.length()>0){
					//ph = nn;
					buf = new StringBuilder(nn);
					tempList.remove(nn);
					for (String tn : tempList) {
						//ph+=(","+tn);
						buf.append(","+tn);
					}
				}
			}
			row.createCell((short) 4).setCellValue(buf.toString());
		}
		try {
		// 自动调节列宽
			sheet.autoSizeColumn((short) 0);
			sheet.autoSizeColumn((short) 1);
			sheet.autoSizeColumn((short) 2);
			sheet.autoSizeColumn((short) 3);
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
	
			SimpleDateFormat sdf = new SimpleDateFormat("MMddHmm");
	
			String fileName = "CustomerInfo" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@RequestMapping(value = {"viewDetail"})
	public String viewDetail(AmazonCustomer amazonCustomer, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(amazonCustomer!=null&&StringUtils.isNotBlank(amazonCustomer.getCountry())){//传亚马逊邮箱
			model.addAttribute("customer",amazonCustomer);
			String amzEmail=amazonCustomer.getAmzEmail();
			String email=amazonCustomer.getEmail();
			Set<String> emaliSet=Sets.newHashSet();
			if(StringUtils.isNotBlank(amzEmail)){
				emaliSet.add(amzEmail);
			}
			if(StringUtils.isNotBlank(email)){
				String[] emailArr=email.split(",");
				for (String arr: emailArr) {
					if(StringUtils.isNotBlank(arr)){
						emaliSet.add(arr);
					}
				}
			}
			if(emaliSet!=null&&emaliSet.size()>0){
				Map<String,CustomEmail> emailMap=customEmailService.findAllTypeEmail(emaliSet);
				model.addAttribute("emailMap",emailMap);
			}
			return "modules/amazoninfo/amazonCustomer";
		}else{
			String tempEmail=amazonCustomerService.findAmzEmail(amazonCustomer.getAmzEmail());//传私人邮箱
			if(StringUtils.isNotBlank(tempEmail)){
				amazonCustomer=amazonCustomerService.get(tempEmail);
				model.addAttribute("customer",amazonCustomer);
				String amzEmail=amazonCustomer.getAmzEmail();
				String email=amazonCustomer.getEmail();
				Set<String> emaliSet=Sets.newHashSet();
				if(StringUtils.isNotBlank(amzEmail)){
					emaliSet.add(amzEmail);
				}
				if(StringUtils.isNotBlank(email)){
					String[] emailArr=email.split(",");
					for (String arr: emailArr) {
						if(StringUtils.isNotBlank(arr)){
							emaliSet.add(arr);
						}
					}
				}
				if(emaliSet!=null&&emaliSet.size()>0){
					Map<String,CustomEmail> emailMap=customEmailService.findAllTypeEmail(emaliSet);
					model.addAttribute("emailMap",emailMap);
				}
				return "modules/amazoninfo/amazonCustomer";
			}else{
				if(StringUtils.isNotBlank(amazonCustomer.getCustomerId())){
					amazonCustomer=amazonCustomerService.getByCustomId(amazonCustomer.getCustomerId()); 
				}
				Set<String> emaliSet=Sets.newHashSet();
				if(StringUtils.isNotBlank(amazonCustomer.getAmzEmail())){
					emaliSet.add(amazonCustomer.getAmzEmail());
				}
				if(emaliSet!=null&&emaliSet.size()>0){
					Map<String,CustomEmail> emailMap=customEmailService.findAllTypeEmail(emaliSet);
					model.addAttribute("emailMap",emailMap);
				}
				if(StringUtils.isNotBlank(amazonCustomer.getCountry())){
					return "modules/amazoninfo/amazonCustomer";
				}
				return "modules/amazoninfo/amazonEmailDetail";
			}
		}

	}
}
