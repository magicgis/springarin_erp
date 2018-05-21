package com.springrain.erp.modules.ebay.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.MessageUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.ZipUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.scheduler.SendEmailByOrderMonitor;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.custom.scheduler.CustomEmailManager;
import com.springrain.erp.modules.custom.service.UnsubscribeEmailService;
import com.springrain.erp.modules.ebay.entity.EbayAddress;
import com.springrain.erp.modules.ebay.entity.EbayOrder;
import com.springrain.erp.modules.ebay.entity.EbayOrderItem;
import com.springrain.erp.modules.ebay.scheduler.EbayConstants;
import com.springrain.erp.modules.ebay.service.EbayOrderService;
import com.springrain.erp.modules.psi.service.PsiProductService;

/**
 * @author Sam
 * 
 */
@Controller
@RequestMapping(value = "${adminPath}/ebay/order")
public class EbayOrderController extends BaseController {

	@Autowired
	private EbayOrderService ebayOrderService;

	@Autowired
	private AmazonOrderService amazonOrderService;

	@Autowired
	private CustomEmailManager sendCustomEmailManager;

	@Autowired
	private PsiProductService psiProductService;
	
	/**
	 * @param id
	 * @return
	 */
	@ModelAttribute
	public EbayOrder get(@RequestParam(required = false) Integer id) {
		if (id != null) {
			return ebayOrderService.get(id);
		} else {
			return new EbayOrder();
		}
	}

	// @RequiresPermissions("amazoninfo:order:view")
	/**
	 * @param ebayOrder
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "list", "" })
	public String list(EbayOrder ebayOrder, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<EbayOrder> page = new Page<EbayOrder>(request, response);
		if (ebayOrder.getCreatedTime() == null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			ebayOrder.setCreatedTime(DateUtils.addMonths(today, -1));
			ebayOrder.setShippedTime(today);
		}
		if(StringUtils.isBlank(ebayOrder.getCountry())){
			ebayOrder.setCountry("de");
		}
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id desc");
		} else {
			page.setOrderBy(orderBy + ",id desc");
		}
		page = ebayOrderService.find(page, ebayOrder);
		page.setOrderBy(orderBy);

		model.addAttribute("page", page);
		return "modules/ebay/order/ebayOrderList";
	}
	
	@RequestMapping(value =  "expEbayOrderByCsv" )
	public String expEbayOrderByCsv(EbayOrder ebayOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (ebayOrder.getCreatedTime() == null) {
			ebayOrder.setCreatedTime(DateUtils.addMonths(today, -1));
			ebayOrder.setShippedTime(today);
		}
			
	    List<EbayOrder> list = ebayOrderService.findForExp(ebayOrder);
	    if(list.size()>65535){
	    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
	    }
	    response.setCharacterEncoding("UTF-8");
		response.setContentType("application/download;charset=UTF-8");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "EbayOrderData" + sdf.format(new Date()) + ".csv";
		response.setHeader("Content-disposition", "attachment;filename=\""
				+fileName);
		OutputStream o;
		try {
			o = response.getOutputStream();
			OutputStreamWriter os = new OutputStreamWriter(o, "utf-8");

			Float gbpRate = AmazonProduct2Service.getRateConfig().get("GBP/EUR");
			BigDecimal bd = new BigDecimal(gbpRate);
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			gbpRate = bd.floatValue();
			for (EbayOrder order : list) {
				EbayAddress address = order.getShippingAddress();
				String countryCode = null;
				if (address != null) {
					countryCode = address.getCountryCode();
				}
				/**
				CountryCode code  = null;
				try {
					if("UK".equals(countryCode)){
						code = CountryCode.valueOf("GB");
					} else {
						code = CountryCode.valueOf(countryCode);
					}
				} catch (Exception e) {}*/
				
				double afterTax = order.getTotal()==null?0:order.getTotal().doubleValue();
				if (afterTax <= 0) {
					continue;
				}
				float rate = 1f;
				if("UK".equals(countryCode)){
					rate = gbpRate;
					afterTax = afterTax*rate;
					bd = new BigDecimal(afterTax);
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					afterTax =bd.doubleValue() ;
				}
				
				StringBuffer content = new StringBuffer();
				content.append(order.getId()).append("\t");
				content.append(order.getOrderId()).append("\t");
				content.append(sdf1.format(order.getCreatedTime())).append("\t");
				content.append(afterTax).append("\t");
				/** 部分国家没有收集到信息,暂时不导出
				if (code != null) {
					content.append(code.getNumberCode()).append("\t");
				}*/
				content.append(rate).append("\t").append("100000");
				content.append("\n");
				os.write(content.toString());
			}
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value =  "expZipEbayOrderByCsv" )
	public String expZipEbayOrderByCsv(EbayOrder ebayOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		Date today = new Date();
    	final SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
		final String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/ebayOrderTotal";
    	final String fileName =  sdf1.format(today);
    	final File zipFile = new File (baseDirStr+"/Ebay_OrderTotal_"+fileName+".zip");
    	final File baseDir = new File(baseDirStr+"/"+fileName);
		if(!baseDir.exists()){
			baseDir.mkdirs();
		}
		
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (ebayOrder.getCreatedTime() == null) {
			ebayOrder.setCreatedTime(DateUtils.addMonths(today, -1));
			ebayOrder.setShippedTime(today);
		}
			
	    List<EbayOrder> list = ebayOrderService.findForExp(ebayOrder);
	    if(list.size()>65535){
	    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
	    }
		OutputStreamWriter osw = null;
		OutputStreamWriter totalOsw = null;
		try {
			Map<String,List<EbayOrder>> dataMap = Maps.newHashMap();
			for (EbayOrder order : list) {
				if(order.getShippingAddress()!=null){
					String key = order.getShippingAddress().getCountryCode();
					List<EbayOrder> temp = dataMap.get(key);
					if(temp==null){
						temp = Lists.newArrayList();
						dataMap.put(key, temp);
					}
					temp.add(order);
				}
			}
			File totalFile = new File(baseDir,"total.csv");
			FileOutputStream totalFos =new FileOutputStream(totalFile);
			totalOsw = new OutputStreamWriter(totalFos, "utf-8");
			Float gbpRate = AmazonProduct2Service.getRateConfig().get("GBP/EUR");
			BigDecimal bd = new BigDecimal(gbpRate);
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			gbpRate = bd.floatValue();
			for (String name : dataMap.keySet()) {
				if (name == null || name.length() == 0) {
					continue;
				}
				File cvsFile = new File(baseDir,name+".csv");
				FileOutputStream fos =new FileOutputStream(cvsFile);
				osw = new OutputStreamWriter(fos, "utf-8");
				for (EbayOrder order : dataMap.get(name)) {
					CountryCode code  = null;
					try {
						if("UK".equals(name)){
							code = CountryCode.valueOf("GB");
						} else {
							code = CountryCode.valueOf(name);
						}
					} catch (Exception e) {}
				
				double afterTax = order.getTotal()==null?0:order.getTotal().doubleValue();
				if (afterTax <= 0) {
					continue;
				}

				float rate = 1f;
				if("UK".equals(name)){
					rate = gbpRate;
					afterTax = afterTax*rate;
					bd = new BigDecimal(afterTax);
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					afterTax =bd.doubleValue() ;
				}
			
				StringBuffer content = new StringBuffer();
				content.append(order.getId()).append("\t");
				content.append(order.getOrderId()).append("\t");
				content.append(sdf1.format(order.getCreatedTime())).append("\t");
				content.append(afterTax).append("\t");
				if (code!=null) {
					content.append(code.getNumberCode()).append("\t");
				}
				content.append(name).append("\t");
				content.append("100000");
				content.append("\n"); //结束 换行
				osw.write(content.toString());
				totalOsw.write(content.toString());
			}
				osw.flush();
				osw.close();
			}
			totalOsw.flush();
			totalOsw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(osw!=null){
				try {
					osw.close();
				} catch (IOException e) {}
			}
			if(totalOsw!=null){
				try {
					totalOsw.close();
				} catch (IOException e) {}
			}
		}

		try {
			//压缩打包
			ZipUtil.zip(zipFile.getAbsolutePath(),"",baseDirStr+"/"+fileName);
			FileUtils.deleteDirectory(baseDir);
			//下载打包后的文件
			response.addHeader("Content-Disposition", "attachment;filename=" +zipFile.getName());
			OutputStream out = response.getOutputStream();
			out.write(FileUtils.readFileToByteArray(zipFile));
			out.flush();
			out.close();
			//完成之后删除临时文件
			FileUtils.deleteFile(baseDirStr+File.separator+zipFile.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value =  "exportEbayOrder" )
	public String exportEbayOrder(EbayOrder ebayOrder, HttpServletRequest request,HttpServletResponse response, Model model) {
		SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (ebayOrder.getCreatedTime() == null) {
			ebayOrder.setCreatedTime(DateUtils.addMonths(today, -1));
			ebayOrder.setShippedTime(today);
		}
			
	    List<EbayOrder> list = ebayOrderService.findForExp(ebayOrder);
	    if(list.size()>65535){
	    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
	    }
	    Map<String,String> nameMap=psiProductService.getProductNameByCountrySku();
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		String[] title = { "Invoice Id", "EbayOrderId", "Receive Address","Zip Codes", "Created Date", "After-Tax",
				"Per-Tax", "Tax Rate", "User Email","Product Name","sku"};
		
		  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		  //设置Excel中的边框(表头的边框)
		  style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		  
		  style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		  style.setBottomBorderColor(HSSFColor.BLACK.index);
		  
		  style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		  style.setLeftBorderColor(HSSFColor.BLACK.index);
		  
		  style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		  style.setRightBorderColor(HSSFColor.BLACK.index);
		  
		  style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		  style.setTopBorderColor(HSSFColor.BLACK.index);
		  //设置字体
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
		// 输出Ebay excel订单
		int index=1;
		for (int i =0;i<list.size();i++ ) {
			EbayOrder order = list.get(i);
			for (EbayOrderItem item : order.getItems()) {
				short no = 0;
				row = sheet.createRow(index++);
				row.createCell(no++).setCellValue(order.getId());
				row.createCell(no++).setCellValue(order.getOrderId());
				EbayAddress address = order.getShippingAddress();
				StringBuffer sb = new StringBuffer();
				String countryCode = null;
				if (address != null) {
					sb.append(address.getName());
					if (StringUtils.isNotBlank(address.getStreet())) {
						sb.append(",").append(address.getStreet());
					}
					if (StringUtils.isNotBlank(address.getStreet1())) {
						sb.append(",").append(address.getStreet1());
					}
					if (StringUtils.isNotBlank(address.getStreet2())) {
						sb.append(",").append(address.getStreet2());
					}
					if (StringUtils.isNotBlank(address.getCounty())) {
						sb.append(",").append(address.getCounty());
					}
					if (StringUtils.isNotBlank(address.getCityName())) {
						sb.append(",").append(address.getCityName());
					}
					if (StringUtils.isNotBlank(address.getCountryCode())) {
						sb.append(",").append(address.getCountryCode());
					}
					if (StringUtils.isNotBlank(address.getPostalCode())) {
						sb.append(",").append(address.getPostalCode());
					}
					countryCode = address.getCountryCode();
				}
				row.createCell(no++).setCellValue(sb.toString());
				String zipCode = (order.getShippingAddress()==null?" ":order.getShippingAddress().getPostalCode());
				row.createCell(no++).setCellValue(zipCode);
				row.createCell(no++).setCellValue(sdf1.format(order.getCreatedTime()));

				float vat = 0;
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
					vat = code.getVat();
				} catch (Exception e) {}
				
				double afterTax = order.getTotal()==null?0:order.getTotal().doubleValue();
				
				cell1=row.createCell(no++);
				cell1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell1.setCellValue(afterTax);
				if (vat != 0 && afterTax > 0) {
					cell1=row.createCell(no++);
					cell1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					double prePrice = afterTax*100/(100+vat);
					BigDecimal bd = new BigDecimal(prePrice);
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					cell1.setCellValue(bd.doubleValue());
					
					row.createCell(no++).setCellValue(vat+"%");
				} else {
					row.createCell(no++).setCellValue("");
					row.createCell(no++).setCellValue("");
				}
				 String email=item.getEmail();
				 if(StringUtils.isNotBlank(email)&&email.contains("@")){
					    String[] arr = email.split("@");
						String tmpEmail=Encodes.encodeBase64(arr[0].getBytes())+"@"+arr[1];
						email="erp"+tmpEmail;
				 }
				 
				row.createCell(no++).setCellValue(email);
				String key=(("de".equals(order.getCountry())?"ebay":"ebay_com")+"_"+item.getSku());
				row.createCell(no++).setCellValue(nameMap.get(key)==null?"":nameMap.get(key));
				row.createCell(no++).setCellValue(item.getSku());
			}
			
		}

		// 自动调节列宽
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);
		sheet.autoSizeColumn((short) 8);
		sheet.autoSizeColumn((short) 9);
		sheet.autoSizeColumn((short) 10);
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");

			String fileName = "EbayOrderData" + sdf.format(new Date()) + ".xls";
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

	/**
	 * @param ebayOrder
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "form")
	public String form(EbayOrder ebayOrder, Model model) {
		model.addAttribute("ebayOrder", ebayOrder);

		int count = 0;
		List<EbayOrderItem> list = ebayOrder.getItems();
		for (EbayOrderItem item : list) {
			count += item.getQuantityPurchased();
		}
		//货品数量
		ebayOrder.setSellerEmail(count + "");
		return "modules/ebay/order/ebayOrderForm";
	}

	/**
	 * @param ebayOrder
	 * @return
	 */
	@RequestMapping(value = "save")
	@ResponseBody
	public String save(EbayOrder ebayOrder) {
		ebayOrderService.save(ebayOrder);
		return ebayOrder.getInvoiceAddress().getId() + "";
	}

	/**
	 * @param hasTax
	 * @param ebayOrder
	 * @return
	 */
	@RequestMapping(value = "invoice")
	@ResponseBody
	public String invoice(String hasTax, EbayOrder ebayOrder,String quantitys,String itemIds) {
		String country = "de";
		String flag="INVOICE_EU";
		String suffix="E";
		if(ebayOrder!=null){
			country=ebayOrder.getCountry();
			if(!"de".equals(country)){
				flag="INVOICE_US";
				suffix="U";
			}
			if(StringUtils.isBlank(ebayOrder.getInvoiceNo())){
				if("0".equals(ebayOrder.getInvoiceFlag())){//未发送账单
					String invoiceNo=amazonOrderService.createFlowNo(flag,8,suffix);
					ebayOrder.setInvoiceNo(invoiceNo);
					ebayOrderService.updateInvoiceNoById(ebayOrder.getInvoiceNo(),ebayOrder.getId()) ;
				}else{
					ebayOrder.setInvoiceNo(ebayOrder.getId()+"");
				}
			}
		}
		
		File file = null;
		if(itemIds!=null&&quantitys!=null){
			//说明是退款单   itemid  数量
			if("4".equals(hasTax)){
				file= SendEmailByOrderMonitor.genEbayPdfRefund1(country,ebayOrder,hasTax,itemIds,quantitys);
			}else{
				file = SendEmailByOrderMonitor.genEbayPdfRefund2(country,ebayOrder,hasTax,itemIds,quantitys);
			}
		}else{
			file = SendEmailByOrderMonitor.genEbayPdf(ebayOrder, country,hasTax);
		}
		if (file != null) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * @param bcc
	 * @param mail
	 * @param hasTax
	 * @param ebayOrder
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "send")
	public String send(String bcc, String mail, String hasTax,
			EbayOrder ebayOrder, Model model,String quantitys,String itemIds,
			RedirectAttributes redirectAttributes) {
		String country = "de";
		String flag="INVOICE_EU";
		String suffix="E";
		if(ebayOrder!=null){
			country=ebayOrder.getCountry();
			if(!"de".equals(country)){
				flag="INVOICE_US";
				suffix="U";
			}
			if(StringUtils.isBlank(ebayOrder.getInvoiceNo())){
				if("0".equals(ebayOrder.getInvoiceFlag())){//未发送账单
					String invoiceNo=amazonOrderService.createFlowNo(flag,8,suffix);
					ebayOrder.setInvoiceNo(invoiceNo);
					ebayOrderService.updateInvoiceNoById(ebayOrder.getInvoiceNo(),ebayOrder.getId()) ;
				}else{
					ebayOrder.setInvoiceNo(ebayOrder.getId()+"");
				}
			}
		}
		File file = null;
	    if(itemIds!=null&&quantitys!=null){
					//说明是退款单   itemid  数量
			if("4".equals(hasTax)){
				file= SendEmailByOrderMonitor.genEbayPdfRefund1(country,ebayOrder,hasTax,itemIds,quantitys);
			}else{
				file = SendEmailByOrderMonitor.genEbayPdfRefund2(country,ebayOrder,hasTax,itemIds,quantitys);
			}
		}else{
			file = SendEmailByOrderMonitor.genEbayPdf(ebayOrder, country,hasTax);
		}
		if (file != null) {
			Map<String, String> params = Maps.newHashMap();
			String toEmail = mail;
			String toAddress="";
			if(toEmail.indexOf(",")>0){
				toEmail = mail.substring(0,mail.indexOf(","));
				String[] emailArr=mail.split(",");
				
				for (String arr: emailArr) {
					if(arr.startsWith("erp")){
						 String[] temp=mail.split("@");
						 arr=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
					 }
					toAddress+=arr+",";
				}
			}else{
				if(mail.startsWith("erp")){
					String[] arr=mail.split("@");
					mail=new String(Encodes.decodeBase64(arr[0].substring(3)))+"@"+arr[1];
				 }
				toAddress+=mail+",";
			}
			toAddress = toAddress.substring(0,toAddress.length()-1);
			//不管怎样都放入token
			params.put("token",UnsubscribeEmailService.getUnsubscribeEmaliHref(toEmail));
			String template = SendEmailByOrderMonitor.getTemplate("invoice",
					"_" + country, params);
			String subject = "invoice";
			if ("de".equals(country)) {
				subject = "Kaufbeleg";
			} else if ("fr".equals(country)) {
				subject = "La facture de votre commande";
			}
			
			MailInfo mailInfo = new MailInfo(toAddress, subject + " "
					+ ebayOrder.getOrderId(), new Date());
			mailInfo.setContent(HtmlUtils.htmlUnescape(template));
			mailInfo.setFileName(file.getName());
			mailInfo.setFilePath(file.getAbsolutePath());
			if (StringUtils.isNotEmpty(bcc)) {
				mailInfo.setBccToAddress(bcc);
			}
			if (sendCustomEmailManager.send(mailInfo)) {
				addMessage(redirectAttributes,
						MessageUtils.format("amazon_order_tips19"));
			} else {
				addMessage(redirectAttributes,
						MessageUtils.format("amazon_order_tips20"));
			}
		}
		return "redirect:" + Global.getAdminPath() + "/ebay/order/form?id="+(ebayOrder==null?0:ebayOrder.getId());
	}

	@RequestMapping(value = "toExport")
	public String toExport(EbayOrder ebayOrder, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<EbayOrder> page = new Page<EbayOrder>(request, response);
		if (ebayOrder.getCreatedTime() == null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			ebayOrder.setCreatedTime(DateUtils.addDays(today, -1));
			ebayOrder.setShippedTime(today);
			ebayOrder.setSellerEmail("Amazon_MFN");
		}
		
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id desc");
		} else {
			page.setOrderBy(orderBy + ",id desc");
		}
		
		if("Ebay".equals(ebayOrder.getSellerEmail())){
			page = ebayOrderService.ordersManager(page, ebayOrder);
		}else if("Amazon_MFN".equals(ebayOrder.getSellerEmail())){
			if(!"4".equals(ebayOrder.getStatus()))
				page = amazonOrderService.findMFNOrder(page, ebayOrder);
		}else{
			page.setPageSize(Integer.MAX_VALUE);			
			page = ebayOrderService.ordersManager(page, ebayOrder);
			List<EbayOrder> temp = page.getList();
			if(!"4".equals(ebayOrder.getStatus())){
				page = amazonOrderService.findMFNOrder(page, ebayOrder);
				temp.addAll(page.getList());
			}
			page.setList(temp);
		}
		page.setOrderBy(orderBy);
		model.addAttribute("page", page);
		return "/modules/ebay/order/shipmentManager";
	}

	/**
	 * @param ebayOrder
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "exportExcel")
	public String exportExcel(EbayOrder ebayOrder, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		String[] title = { "invoice", "order", "transaction", "item", "title",
				"quantity", "address", "platform" };

		HSSFCell cell = null;
		for (int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}

		if (ebayOrder.getCreatedTime() == null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			ebayOrder.setCreatedTime(DateUtils.addDays(today, -1));
			ebayOrder.setShippedTime(today);
		}
		
		ebayOrder.setStatus(EbayConstants.PAY);
		List<EbayOrder> EbayList = ebayOrderService.inquiryOrder(ebayOrder);
		
		List<EbayOrder> EbaOutList = SplitL(EbayList);

		List<AmazonOrder> AmaList = amazonOrderService.findExAma(
				ebayOrder.getCreatedTime(), ebayOrder.getShippedTime());
		
		// 将ama转换为eba，并按交易拆分
		List<EbayOrder> AmaList2 = AmaToEba(AmaList);
		List<EbayOrder> AmaOutList = SplitL(AmaList2);

		int rowIndex = 0;

		// 输出Ebay订单
		for (int i = 0; i < EbaOutList.size(); i++) {
			EbayOrder ebOrder = EbaOutList.get(i);
			row = sheet.createRow(i + 1);
			row.createCell((short) 0).setCellValue(ebOrder.getId() + "");
			row.createCell((short) 1).setCellValue(ebOrder.getOrderId());

			EbayOrderItem ei = ebOrder.getItems().get(0);
			row.createCell((short) 2).setCellValue(ei.getTransactionId());
			row.createCell((short) 3).setCellValue(ei.getItemId());
			row.createCell((short) 4).setCellValue(ei.getTitle());
			row.createCell((short) 5).setCellValue(ei.getQuantityPurchased());

			EbayAddress address = ebOrder.getShippingAddress();
			if (address != null) {
				String st0 = address.getStreet() == null ? "" : address
						.getStreet();
				String st1 = address.getStreet1() == null ? "" : address
						.getStreet1();
				String st2 = address.getStreet2() == null ? "" : address
						.getStreet2();
				row.createCell((short) 6).setCellValue(
						st0 + " " + st1 + " " + st2 + " "
								+ address.getCityName() + " "
								+ address.getPostalCode());
			} else {
				row.createCell((short) 6).setCellValue("");
			}
			row.createCell((short) 7).setCellValue("Ebay");
			rowIndex++;
		}

		int ro = rowIndex;
		// 空3行
		for (int i = ro; i < ro + 3; i++) {
			row = sheet.createRow(i + 1);
			row.createCell((short) 0).setCellValue("");
			rowIndex++;
		}

		// 输出Amaz订单
		for (int i = 0; i < AmaOutList.size(); i++) {
			EbayOrder ebOrder = AmaOutList.get(i);
			row = sheet.createRow(rowIndex + 1);
			row.createCell((short) 0).setCellValue(ebOrder.getId() + "");
			row.createCell((short) 1).setCellValue(ebOrder.getOrderId());

			EbayOrderItem ei = ebOrder.getItems().get(0);
			row.createCell((short) 2).setCellValue(ei.getTransactionId());
			row.createCell((short) 3).setCellValue(ei.getItemId());
			row.createCell((short) 4).setCellValue(ei.getTitle());
			row.createCell((short) 5).setCellValue(ei.getQuantityPurchased());

			EbayAddress address = ebOrder.getShippingAddress();
			if (address != null) {
				String st0 = address.getStreet() == null ? "" : address
						.getStreet();
				String st1 = address.getStreet1() == null ? "" : address
						.getStreet1();
				String st2 = address.getStreet2() == null ? "" : address
						.getStreet2();
				row.createCell((short) 6).setCellValue(
						st0 + " " + st1 + " " + st2 + ""
								+ address.getCityName() + " "
								+ address.getPostalCode());
			} else {
				row.createCell((short) 6).setCellValue("");
			}
			row.createCell((short) 7).setCellValue(ebOrder.getSellerEmail());
			rowIndex++;
		}

		// 自动调节列宽
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
		sheet.autoSizeColumn((short) 4);
		sheet.autoSizeColumn((short) 5);
		sheet.autoSizeColumn((short) 6);
		sheet.autoSizeColumn((short) 7);

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		String fileName = "未发货订单" + sdf.format(new Date()) + ".xls";
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

	/**
	 * @param AmaList
	 * @return
	 */
	private List<EbayOrder> AmaToEba(List<AmazonOrder> AmaList) {
		if (AmaList == null) {
			return null;
		}
		List<EbayOrder> EbaList = Lists.newArrayList();

		for (AmazonOrder amazonOrder : AmaList) {
			EbayOrder ebOrder = new EbayOrder();
			ebOrder.setId(amazonOrder.getId());
			ebOrder.setOrderId(amazonOrder.getAmazonOrderId());
			//销售渠道
			ebOrder.setSellerEmail(amazonOrder.getSalesChannel());

			AmazonAddress add = amazonOrder.getShippingAddress();
			EbayAddress are = new EbayAddress();
			are.setStreet(add.getAddressLine1());
			are.setStreet1(add.getAddressLine2());
			are.setStreet2(add.getAddressLine3());
			are.setCityName(add.getCity());
			are.setPostalCode(add.getPostalCode());
			are.setStateOrProvince(add.getStateOrRegion());
			ebOrder.setShippingAddress(are);

			List<EbayOrderItem> list = Lists.newArrayList();

			for (int i = 0; i < amazonOrder.getItems().size(); i++) {
				AmazonOrderItem amait = amazonOrder.getItems().get(i);
				EbayOrderItem ebait = new EbayOrderItem();
				ebait.setTransactionId("");
				ebait.setItemId(amait.getOrderItemId());
				ebait.setTitle(amait.getTitle());
				ebait.setQuantityPurchased(amait.getQuantityOrdered()
						- amait.getQuantityShipped());
				
				//如果订单数量减去已发货数量为0，不添加这条交易
				if(amait.getQuantityOrdered()
						- amait.getQuantityShipped() > 0){
					list.add(ebait);
				}
			}
			ebOrder.setItems(list);
			EbaList.add(ebOrder);
		}

		return EbaList;
	}

	/**
	 * @param EbayList
	 * @return
	 */
	private List<EbayOrder> SplitL(List<EbayOrder> EbayList) {
		if (EbayList == null) {
			return null;
		}
		List<EbayOrderItem> ls = Lists.newArrayList();
		List<EbayOrder> outList = Lists.newArrayList();

		for (int i = 0; i < EbayList.size(); i++) {
			EbayOrder ebOrder = EbayList.get(i);
			ls = ebOrder.getItems();
			if (ls != null && ls.size() > 0) {
				if (ls.size() == 1) {
					outList.add(ebOrder);
				} else {
					for (int j = 0; j < ls.size(); j++) {
						EbayOrderItem ei = ls.get(j);
						EbayOrder ebOrder2 = new EbayOrder();
						ebOrder2.setId(ebOrder.getId());
						ebOrder2.setOrderId(ebOrder.getOrderId());
						ebOrder2.setShippingAddress(ebOrder
								.getShippingAddress());
						List<EbayOrderItem> item = Lists.newArrayList();
						item.add(ei);
						ebOrder2.setItems(item);
						outList.add(ebOrder2);
					}
				}
			}
		}
		return outList;
	}

	/**
	 * @param ebayOrder
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "exportCsv")
	public String exportCsv(EbayOrder ebayOrder, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		if (ebayOrder.getCreatedTime() == null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			ebayOrder.setCreatedTime(DateUtils.addDays(today, -1));
			ebayOrder.setShippedTime(today);
		}
		ebayOrder.setStatus(EbayConstants.PAY);
		List<EbayOrder> Ebalist = ebayOrderService.inquiryOrder(ebayOrder);

		List<AmazonOrder> AmaList = amazonOrderService.findExAma(
				ebayOrder.getCreatedTime(), ebayOrder.getShippedTime());
		List<EbayOrder> AEList = AmaToEba(AmaList);

		List<String[]> outList = new ArrayList<String[]>();

		for (int i = 0; i < Ebalist.size(); i++) {
			EbayOrder order = Ebalist.get(i);
			EbayAddress address = order.getShippingAddress();
			String[] st = { order.getId() + "", address.getName(),
					address.getStreet() == null ? "" : address.getStreet(),
					address.getStreet1() == null ? "" : address.getStreet1(),
					address.getStreet2() == null ? "" : address.getStreet2(),
					address.getCityName(), address.getPostalCode(), "Ebay" };
			outList.add(st);
		}
		for (int i = 0; i < AEList.size(); i++) {
			EbayOrder order = AEList.get(i);
			EbayAddress address = order.getShippingAddress();
			String[] st = { order.getId() + "", address.getName(),
					address.getStreet() == null ? "" : address.getStreet(),
					address.getStreet1() == null ? "" : address.getStreet1(),
					address.getStreet2() == null ? "" : address.getStreet2(),
					address.getCityName(), address.getPostalCode(), "Amazon" };
			outList.add(st);
		}

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/download;charset=UTF-8");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		response.setHeader("Content-disposition", "attachment;filename=\""
				+ "未发货订单" + sdf.format(new Date()) + ".csv\"");
		OutputStream o;
		try {
			o = response.getOutputStream();
			OutputStreamWriter os = new OutputStreamWriter(o, "utf-8");
			for (String[] str : outList) {
				//String str2 = "";
				StringBuilder  buf=new StringBuilder();
				for (int i = 0; i < str.length; i++) {
					if (i > 0)
						buf.append(",");
					buf.append(str[i]);
				}
				buf.append("\n");
				os.write(buf.toString());
			}
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
