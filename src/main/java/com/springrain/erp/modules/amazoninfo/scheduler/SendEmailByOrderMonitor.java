package com.springrain.erp.modules.amazoninfo.scheduler;

import java.io.File;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.FreeMarkers;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnOrderItem;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonUnlineOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.MfnOrderService;
import com.springrain.erp.modules.custom.scheduler.CustomEmailManager;
import com.springrain.erp.modules.custom.service.UnsubscribeEmailService;
import com.springrain.erp.modules.ebay.entity.EbayAddress;
import com.springrain.erp.modules.ebay.entity.EbayOrder;
import com.springrain.erp.modules.ebay.entity.EbayOrderItem;
import com.springrain.erp.modules.ebay.service.EbayOrderService;
import com.springrain.erp.modules.psi.service.PsiProductService;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class SendEmailByOrderMonitor {

	@Autowired
	private AmazonOrderService orderService;

	@Autowired
	private EbayOrderService ebayOrderService;

	@Autowired
	private SendCustomEmail1Manager sendCustomEmailManager;

	@Autowired
	private CustomEmailManager customEmailManager;

	@Autowired
	private AmazonProductService amazonProductService;
	
	@Autowired
	private PsiProductService productService;

	@Autowired
	private UnsubscribeEmailService unsubscribeEmailService;
	@Autowired
	private MfnOrderService 		mfnOrderService;
	
	@Autowired
	private AmazonUnlineOrderService unlineOrderService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	private final static Logger LOGGER = LoggerFactory
			.getLogger(SendEmailByOrderMonitor.class);

	private static String path;
	
	private static String head = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd \">    <html xmlns=\"http://www.w3.org/1999/xhtml\">  <head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/><meta http-equiv=\"cache-control\" content=\"no-cache\"/><meta http-equiv=\"pragma\" content=\"no-cache\"/><title></title></head><body style=\"margin:0;\">"; 
	
	private static String foot ="</body></html>";
	

	public static String getTemplate(String name, String country,
			Map<String, String> params) {
		if (country == null) {
			return "";
		}
		if (path == null) {
			path = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ "WEB-INF/classes/templates/email";
		}
		try {
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(path));
			cfg.setDefaultEncoding("utf-8");
			Template template = cfg.getTemplate(name + country + ".ftl");
			if (params != null && params.size() > 0) {
				return FreeMarkers.renderTemplate(template, params);
			} else {
				return FreeMarkers.renderTemplate(template, null);
			}
		} catch (Exception e) {
			return "";
		}
	}

	
	public static String getTemplateByName(String country,
			Map<String, Object> params) {
		if (country == null) {
			return "";
		}
		try {
			if (path == null) {
				path = ContextLoader.getCurrentWebApplicationContext()
						.getServletContext().getRealPath("/")
						+ "WEB-INF/classes/templates/email";
			}
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(new File(path).getParentFile(), "invoice"));
			Template template = cfg.getTemplate("package" + country + ".ftl");
			if (params != null && params.size() > 0) {
				return FreeMarkers.renderTemplate(template, params);
			} else {
				return FreeMarkers.renderTemplate(template, null);
			}
		} catch (Exception e) {
			return "";
		}
	}

	
	private static String getInvoiceTemplate(String country,
			Map<String, Object> params) {
		if (country == null) {
			return "";
		}
		try {
			if (path == null) {
				path = ContextLoader.getCurrentWebApplicationContext()
						.getServletContext().getRealPath("/")
						+ "WEB-INF/classes/templates/email";
			}
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(new File(path)
					.getParentFile(), "invoice"));
			Template template = cfg.getTemplate("invoice" + country + ".ftl");
			if (params != null && params.size() > 0) {
				return FreeMarkers.renderTemplate(template, params);
			} else {
				return FreeMarkers.renderTemplate(template, null);
			}
		} catch (Exception e) {
			return "";
		}
	}
	
	private static String getInvoiceTemplateByEur(String country,
			Map<String, Object> params) {
		if (country == null) {
			return "";
		}
		try {
			if (path == null) {
				path = ContextLoader.getCurrentWebApplicationContext()
						.getServletContext().getRealPath("/")
						+ "WEB-INF/classes/templates/email";
			}
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(new File(path)
					.getParentFile(), "invoice"));
			Template template = cfg.getTemplate("eur_invoice" + country + ".ftl");
			if (params != null && params.size() > 0) {
				return FreeMarkers.renderTemplate(template, params);
			} else {
				return FreeMarkers.renderTemplate(template, null);
			}
		} catch (Exception e) {
			return "";
		}
	}
	
	private static String getRefundTemplate(String country,
			Map<String, Object> params) {
		if (country == null) {
			return "";
		}
		try {
			if (path == null) {
				path = ContextLoader.getCurrentWebApplicationContext()
						.getServletContext().getRealPath("/")
						+ "WEB-INF/classes/templates/email";
			}
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(path));
			Template template = cfg.getTemplate("refund" + country + ".ftl");
			if (params != null && params.size() > 0) {
				return FreeMarkers.renderTemplate(template, params);
			} else {
				return FreeMarkers.renderTemplate(template, null);
			}
		} catch (Exception e) {
			return "";
		}
	}

	public void setOrderService(AmazonOrderService orderService) {
		this.orderService = orderService;
	}

	public static File genPackagePdf(List<MfnOrder> orderList,String packageNo,String country) {
		//String country="de";
		if(!country.startsWith("de")&&!country.startsWith("com")&&!country.startsWith("jp")){
			country="com";
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("orderList", orderList);
		params.put("currentDate", getDate(country,new Date()));
		try {
			String invoiceTemp = getTemplateByName("_" + country, params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error("package模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ Global.getCkBaseDir() + "/package";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}

			File htmlFile = new File(baseDir, packageNo+".html");
			File pdfFile = new File(baseDir, packageNo+".pdf");
		
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static File genTaxRefundPdf(AmazonOrder order,String country,String nameId,String titleId) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("order", order);
		params.put("sendDate",getDateTime(country,new Date()));
		params.put("lastUpdateDate",getDateTime(country,order.getLastUpdateDate()));
		
		Map<String,String> titleMap = Maps.newHashMap();
		for(int i =0;i<nameId.split(",").length;i++){
			titleMap.put(nameId.split(",")[i],titleId.split(",")[i]);
		}
		params.put("titleMap", titleMap);
		
		try {
			String invoiceTemp = getRefundTemplate("_" + order.getAccountName(), params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error("TaxRefundPdf模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ Global.getCkBaseDir() + "/taxRefund";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}

			File htmlFile = new File(baseDir, order.getAmazonOrderId() + ".html");
			File pdfFile = new File(baseDir, order.getAmazonOrderId() + ".pdf");
			
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,new File(new File(path).getParentFile(), "taxRefund"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static File genPackagePdf(MfnOrder order) {
		String country="de";
		try {
			Map<String, Object> params = Maps.newHashMap();
			MfnAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			params.put("title", "Rechnung");
			params.put("orderId", order.getOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getStreet()!=null){
				params.put("reciever_address1", address.getStreet().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getStreet1()!=null){
				params.put("reciever_address2", address.getStreet1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getStreet2()!=null){
				params.put("reciever_address3", address.getStreet2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCityName() == null ? "": address.getCityName());
			params.put("reciever_state",address.getStateOrProvince() == null ? "" : address.getStateOrProvince());
			params.put("reciever_postcode", address.getPostalCode());
			params.put("reciever_country", address.getCountryCode());

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", order.getId() + "");
			params.put("customerno", order.getId() + "");

			params.put("customer_email", order.getBuyerUserEmail());
			
			List<Map<String, Object>> items = Lists.newArrayList();
			int quantity = 0;
			for (MfnOrderItem item : order.getItems()) {
				if (item.getQuantityPurchased()== null|| item.getQuantityPurchased() == 0) {
					continue;
				}
				quantity += item.getQuantityPurchased();
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				itemMap.put("num", item.getQuantityPurchased() + "");
				String name =item.getTitle();
				if(StringUtils.isBlank(name)){
					name = HtmlUtils.htmlUnescape(item.getTitle());
				}
				itemMap.put("name", name + "[" + item.getSku() + "]");
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getOrderId()+ "--该订单无货品");
				return null;
			}
			

			params.put("items", items);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getBuyTime()));
			params.put("buytime", getDateTime(country, order.getBuyTime()));
			params.put("paytime",getDateTime(country, order.getPaidTime()));
			
			

			String invoiceTemp = getTemplateByName("_" + country, params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getOrderId()+"-----package模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = new File(baseDir, "Mfn.html");
			File pdfFile = new File(baseDir, "Mfn.pdf");
		
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	public static File genPdfByRefund(AmazonProductService amazonProductService,String country, AmazonUnlineOrder order, String type,String itemIds,String quantitys,String orderNo,String invoice) {
		try {
			Map<Integer,Integer> refundMap = Maps.newHashMap();
			for(int i =0;i<itemIds.split(",").length;i++){
				refundMap.put(Integer.parseInt(itemIds.split(",")[i]), Integer.parseInt(quantitys.split(",")[i]));
			}
			
			Map<String, Object> params = Maps.newHashMap();
			AmazonUnlineAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			if (type.equals("2")) {
				params.put("title", "Gutschrift");
			}
			params.put("type", type);
			params.put("orderId", StringUtils.isNotEmpty(orderNo)?orderNo:order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCity() == null ? "": address.getCity());
			params.put("reciever_state",address.getStateOrRegion() == null ? "" : address.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());
			params.put("reciever_country", address.getCountryCode());
			if(StringUtils.isNotBlank(order.getRemark())){
				   params.put("remark", order.getRemark());	
			}

			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			} else if ("mx".equals(country)) {
				billcur = "MXN";
			} else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno",StringUtils.isNotEmpty(invoice)?invoice:order.getInvoiceNo());
			params.put("customerno",(StringUtils.isNotEmpty(invoice)?Integer.parseInt(invoice):order.getId()) * 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			Float rate = 0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				rate = code.getVat();
			}
			float vat = 0f;
			params.put("rate", rate);
			
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			Float orderTotal  =0f;
			int quantity = 0;
			
			for (AmazonUnlineOrderItem item : order.getItems()) {
				//如果不包含在退款单的map里面就跳过
				if (item.getQuantityOrdered() == null|| item.getQuantityOrdered() == 0||!refundMap.containsKey(item.getId())) {
					continue;
				}
				//rate+=item.getItemTax();
				quantity +=refundMap.get(item.getId());
				
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				//itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("num", refundMap.get(item.getId()) + "");
			//	itemMap.put("rate", "(" + rate + "%)");
				itemMap.put("rate", "(" + item.getItemTax() + "%)");
				itemMap.put("name", (item.getProductName()==null?"":item.getProductName()) + "[" + item.getSellersku() + "]");

				float shipping = (item.getShippingPrice() == null ? 0f : item.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice())-(item.getShippingDiscount()==null?0f:item.getShippingDiscount());
				float price = item.getItemPrice();
				price = price- (item.getPromotionDiscount() == null ? 0f : item.getPromotionDiscount());
				//Float singlePrice = (price) / refundMap.get(item.getId()) * 100/ (100 + rate);
				//Float singlePrice = price / item.getQuantityOrdered() * 100/ (100 + rate);
				String singlePriceStr = getNumberStr(price, country);
				itemMap.put("singlePrice", singlePriceStr);
				//float totlePrice =price* refundMap.get(item.getId())+price*refundMap.get(item.getId())*item.getItemTax()/100;
				float totlePrice =price* refundMap.get(item.getId());
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				orderitemShipprice += shipping;
				orderitem_totalprice += totlePrice;
				//总价=单价加邮费
				orderTotal+=totlePrice+shipping;
				items.add(itemMap);
			}
			//rate=rate/order.getItems().size();
			//params.put("rate", rate);
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			params.put("orderitem_shipprice",getNumberStr(orderitemShipprice, country));
			params.put("items", items);
			params.put("orderitem_totalprice",getNumberStr(orderitem_totalprice, country));
			params.put("order_totalprice",getNumberStr(orderitemShipprice + orderitem_totalprice,country));
			vat = (orderitemShipprice + orderitem_totalprice) * rate / 100f;
			//订单总价：
			//params.put("totalprice",getNumberStr(order.getOrderTotal(), country));
			params.put("totalprice",getNumberStr(orderTotal+vat, country));
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getPurchaseDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			//if(!"com".equals(country)&&!"de".equals(country)){
				params.put("paytime", getDateTime(country, order.getLastUpdateDate()));
			//}
			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + country, params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			htmlFile = new File(baseDir, "Unline_"+ order.getAmazonOrderId() + "_bill.html");
			pdfFile = new File(baseDir, "Unline_"+ order.getAmazonOrderId() + "_bill.pdf");
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static File genPdfsByRefund(String month,AmazonProductService amazonProductService,String country, List<AmazonOrder> orders,Map<String, Map<String, Integer>> returnGoods) {
		String invoiceTemp ="";
		Map<String,String> namesMap = amazonProductService.findProductNameMap();
		for (AmazonOrder order : orders) {
			Map<String, Object> params = Maps.newHashMap();
			AmazonAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			params.put("title", "Gutschrift");
			params.put("type", "2");
			params.put("orderId", order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCity() == null ? "": address.getCity());
			params.put("reciever_state",address.getStateOrRegion() == null ? "" : address.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());
			params.put("reciever_country", address.getCountryCode());

			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			}else if ("mx".equals(country)) {
				billcur = "MXN";
			} else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", order.getId() + "");
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			float rate = 0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				rate = code.getVat();
			}
			
			float vat = 0f;
			params.put("rate", rate);
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			Float orderTotal  =0f;
			int quantity = 0;
			String orderId = order.getAmazonOrderId();
			for (AmazonOrderItem item : order.getItems()) {
				String sku = item.getSellersku();
				//如果不包含在退款单的map里面就跳过
				if (item.getQuantityOrdered() == null|| item.getQuantityOrdered() == 0||returnGoods.get(orderId).get(sku)==null) {
					continue;
				}
				//quantity += item.getQuantityOrdered();
				quantity +=returnGoods.get(orderId).get(sku);
				
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				//itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("num",returnGoods.get(orderId).get(sku) + "");
				itemMap.put("rate", "(" + rate + "%)");
				String name = namesMap.get(item.getAsin());
				if(StringUtils.isBlank(name)){
					name = HtmlUtils.htmlUnescape(item.getTitle());
				}
				itemMap.put("name", name + "[" + item.getSellersku() + "]");

				float shipping = (item.getShippingPrice() == null ? 0f : item.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice())-(item.getShippingDiscount()==null?0f:item.getShippingDiscount());
				float price = item.getItemPrice();
				price = price- (item.getPromotionDiscount() == null ? 0f : item.getPromotionDiscount());
				//Float singlePrice = (price) / refundMap.get(item.getId()) * 100/ (100 + rate);
				Float singlePrice = price / item.getQuantityOrdered() * 100/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				float totlePrice =singlePrice* returnGoods.get(orderId).get(sku);
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				orderitemShipprice += shipping;
				orderitem_totalprice += totlePrice;
				//总价=单价加邮费
				orderTotal+=price / item.getQuantityOrdered()* returnGoods.get(orderId).get(sku) +shipping;
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",getNumberStr(orderitem_totalprice, country));
			params.put("order_totalprice",getNumberStr(orderitemShipprice + orderitem_totalprice,country));
			vat = (orderitemShipprice + orderitem_totalprice) * rate / 100f;
			//订单总价：
			//params.put("totalprice",getNumberStr(order.getOrderTotal(), country));
			params.put("totalprice",getNumberStr(orderTotal, country));
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			params.put("buydate", getDate(country, order.getPurchaseDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			params.put("paytime", getDateTime(country, order.getLastUpdateDate()));
			// 生成账单附件
			String invoiceTemp1 = getInvoiceTemplate("_" + order.getAccountName(), params);
			if (invoiceTemp1 == null || invoiceTemp1.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				continue;
			}
			invoiceTemp += (invoiceTemp1+"<div style=\"page-break-after: always;\"></div>");
		}
		try {
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/invoiceTotal/"+country+"/"+month;
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			htmlFile = new File(baseDir, "Amazon_RetrunGoodsTotal_bill.html");
			pdfFile = new File(baseDir, "Amazon_RetrunGoodsTotal_bill.pdf");
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	
	public static File genTaxRefundPdf(String rateSn,Float totalTax,AmazonProductService amazonProductService,String country, AmazonOrder order, String type) {
		boolean hasTax = !type.equals("0");
		try {
			Map<String, Object> params = Maps.newHashMap();
			AmazonAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			
			params.put("title", "Rechnung");
			
			params.put("type", type);
			params.put("orderId", order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCity() == null ? ""
					: address.getCity());
			params.put(
					"reciever_state",
					address.getStateOrRegion() == null ? "" : address
							.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());

			params.put("reciever_country", address.getCountryCode());

			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			} else if ("mx".equals(country)) {
				billcur = "MXN";
			}else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);
			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",StringUtils.isBlank(rateSn) ? "":rateSn);
			params.put("invoiceno", order.getInvoiceNo());
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			float rate = 0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				rate = code.getVat();
			}
			float vat = 0f;
			if (hasTax) {
				params.put("rate", rate);
			} else {
				params.put("rate", 0);
			}
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			int quantity = 0;
			for (AmazonOrderItem item : order.getItems()) {
				if (item.getQuantityOrdered() == null
						|| item.getQuantityOrdered() == 0) {
					continue;
				}
				quantity += item.getQuantityOrdered();
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("rate", "(" + rate + "%)");
				String name = amazonProductService.findProductName(
						item.getAsin(), country);
				if(StringUtils.isBlank(name)){
					name = HtmlUtils.htmlUnescape(item.getTitle());
				}
				itemMap.put("name", name + "[" + item.getSellersku() + "]");

				float shipping = (item.getShippingPrice() == null ? 0f : item
						.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice())-(item.getShippingDiscount()==null?0f:item.getShippingDiscount());
				float price = item.getItemPrice();
				price = price
						- (item.getPromotionDiscount() == null ? 0f : item
								.getPromotionDiscount());
				float totlePrice = (price) * 100 / (100 + rate);
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				Float singlePrice = (price) / item.getQuantityOrdered() * 100
						/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				orderitemShipprice += shipping;
				orderitem_totalprice += totlePrice;
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",
					getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",
					getNumberStr(orderitem_totalprice, country));
			params.put(
					"order_totalprice",
					getNumberStr(orderitemShipprice + orderitem_totalprice,
							country));
			if (hasTax) {
				vat = (orderitemShipprice + orderitem_totalprice) * rate / 100f;
				params.put("totalprice",
						getNumberStr(order.getOrderTotal(), country));
			} else {
				params.put(
						"totalprice",
						getNumberStr(orderitem_totalprice + orderitemShipprice,
								country));
			}
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getPurchaseDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			params.put("paytime",
					getDateTime(country, order.getLastUpdateDate()));
			
			if(order.getDeliveryDate()!=null){
				params.put("deliveryDate",
						getDateTime(country, order.getDeliveryDate()));
			}
			
			if(StringUtils.isNotBlank(order.getRemark())){
			   params.put("remark", order.getRemark());	
			}

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + order.getAccountName(), params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String uuid = UUID.randomUUID().toString();
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ Global.getCkBaseDir() + "/event/"+uuid;
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			
			if (hasTax) {
				htmlFile = new File(baseDir,order.getAmazonOrderId() +"_bill.html");
				pdfFile = new File(baseDir,order.getAmazonOrderId() +"_bill.pdf");
			} else {
				htmlFile = new File(baseDir,order.getAmazonOrderId() +"_nbill.html");
				pdfFile = new File(baseDir,order.getAmazonOrderId() +"_nbill.pdf");
			}
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),
					htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,
					new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static File genEuTaxRefundPdf(String rateSn,String imgPath,Float avgRate,Float totalTax,AmazonProductService amazonProductService,String country, AmazonOrder order, String type) {
		boolean hasTax = !type.equals("0");
		try {
			Map<String, Object> params = Maps.newHashMap();
			AmazonAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			params.put("attchmentPath",imgPath);
			params.put("title", "Rechnung");
			
			params.put("type", type);
			params.put("orderId", order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCity() == null ? ""
					: address.getCity());
			params.put(
					"reciever_state",
					address.getStateOrRegion() == null ? "" : address
							.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());

			params.put("reciever_country", address.getCountryCode());

			String billcur = "";
			
			if ("uk".equals(country)) {
				billcur = "EUR";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			}else if ("mx".equals(country)) {
				billcur = "MXN";
			} else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",StringUtils.isBlank(rateSn)? "" :rateSn);
			params.put("invoiceno", order.getInvoiceNo());
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			float rate = 0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				rate = code.getVat();
			}
			float vat = 0f;
			if (hasTax) {
				params.put("rate", rate);
			} else {
				params.put("rate", 0);
			}
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			int quantity = 0;
			for (AmazonOrderItem item : order.getItems()) {
				if (item.getQuantityOrdered() == null
						|| item.getQuantityOrdered() == 0) {
					continue;
				}
				quantity += item.getQuantityOrdered();
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("rate", "(" + rate + "%)");
				String name = amazonProductService.findProductName(
						item.getAsin(), country);
				if(StringUtils.isBlank(name)){
					name = HtmlUtils.htmlUnescape(item.getTitle());
				}
				itemMap.put("name", name + "[" + item.getSellersku() + "]");

				float shipping = ((item.getShippingPrice() == null ? 0f : item
						.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice())-(item.getShippingDiscount()==null?0f:item.getShippingDiscount()))*avgRate;
				float price = item.getItemPrice()*avgRate;
				price = price
						- (item.getPromotionDiscount() == null ? 0f : item
								.getPromotionDiscount())*avgRate;
				float totlePrice = (price) * 100 / (100 + rate);
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				Float singlePrice = (price) / item.getQuantityOrdered() * 100
						/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				orderitemShipprice += shipping;
				orderitem_totalprice += totlePrice;
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",
					getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",
					getNumberStr(orderitem_totalprice, country));
			params.put(
					"order_totalprice",
					getNumberStr(orderitemShipprice + orderitem_totalprice,
							country));
			if (hasTax) {
				vat = (orderitemShipprice + orderitem_totalprice) * rate / 100f;
				params.put("totalprice",
						getNumberStr(order.getOrderTotal(), country));
			} else {
				params.put(
						"totalprice",
						getNumberStr(orderitem_totalprice + orderitemShipprice,
								country));
			}
			params.put("vat", getNumberStr(totalTax*avgRate, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getPurchaseDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			params.put("paytime",
					getDateTime(country, order.getLastUpdateDate()));
			
			if(order.getDeliveryDate()!=null){
				params.put("deliveryDate",
						getDateTime(country, order.getDeliveryDate()));
			}
			
			if(StringUtils.isNotBlank(order.getRemark())){
			   params.put("remark", order.getRemark());	
			}

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplateByEur("_" + order.getAccountName(), params);//getInvoiceTemplateByEur
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String uuid = UUID.randomUUID().toString();
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ Global.getCkBaseDir() + "/event/"+uuid;
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
		
			if (hasTax) {
				htmlFile = new File(baseDir,order.getAmazonOrderId() +"_billByEUR.html");
				pdfFile = new File(baseDir, order.getAmazonOrderId() +"_billByEUR.pdf");
			} else {
				htmlFile = new File(baseDir,order.getAmazonOrderId() + "_nbillByEUR.html");
				pdfFile = new File(baseDir, order.getAmazonOrderId() +"_nbillByEUR.pdf");
			}
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),
					htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,
					new File(new File(baseDirStr).getParentFile().getParentFile(), "event"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static File genEuTaxRefundPdf2(String rateSn,String month,String imgPath,Float avgRate,Float totalTax,AmazonProductService amazonProductService,String country, AmazonOrder order, String type,String tempCountry) {
		boolean hasTax = !type.equals("0");
		try {
			Map<String, Object> params = Maps.newHashMap();
			AmazonAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			params.put("attchmentPath",imgPath);
			params.put("title", "Rechnung");
			
			params.put("type", type);
			params.put("orderId", order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCity() == null ? ""
					: address.getCity());
			params.put(
					"reciever_state",
					address.getStateOrRegion() == null ? "" : address
							.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());

			params.put("reciever_country", address.getCountryCode());

			String billcur = "";
			
			if ("uk".equals(country)) {
				billcur = "EUR";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			} else if ("mx".equals(country)) {
				billcur = "MXN";
			}else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",StringUtils.isBlank(rateSn) ? "" : rateSn);
			params.put("invoiceno", order.getInvoiceNo());
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			float rate = 0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				rate = code.getVat();
			}
			float vat = 0f;
			if (hasTax) {
				params.put("rate", rate);
			} else {
				params.put("rate", 0);
			}
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			int quantity = 0;
			for (AmazonOrderItem item : order.getItems()) {
				if (item.getQuantityOrdered() == null
						|| item.getQuantityOrdered() == 0) {
					continue;
				}
				quantity += item.getQuantityOrdered();
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("rate", "(" + rate + "%)");
				String name = amazonProductService.findProductName(
						item.getAsin(), country);
				if(StringUtils.isBlank(name)){
					name = HtmlUtils.htmlUnescape(item.getTitle());
				}
				itemMap.put("name", name + "[" + item.getSellersku() + "]");

				float shipping = ((item.getShippingPrice() == null ? 0f : item
						.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice())-(item.getShippingDiscount()==null?0f:item.getShippingDiscount()))*avgRate;
				float price = item.getItemPrice()*avgRate;
				price = price
						- (item.getPromotionDiscount() == null ? 0f : item
								.getPromotionDiscount())*avgRate;
				float totlePrice = (price) * 100 / (100 + rate);
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				Float singlePrice = (price) / item.getQuantityOrdered() * 100
						/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				orderitemShipprice += shipping;
				orderitem_totalprice += totlePrice;
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",
					getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",
					getNumberStr(orderitem_totalprice, country));
			params.put(
					"order_totalprice",
					getNumberStr(orderitemShipprice + orderitem_totalprice,
							country));
			if (hasTax) {
				vat = (orderitemShipprice + orderitem_totalprice) * rate / 100f;
				params.put("totalprice",
						getNumberStr(order.getOrderTotal(), country));
			} else {
				params.put(
						"totalprice",
						getNumberStr(orderitem_totalprice + orderitemShipprice,
								country));
			}
			params.put("vat", getNumberStr(totalTax*avgRate, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getPurchaseDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			params.put("paytime",
					getDateTime(country, order.getLastUpdateDate()));
			
			if(order.getDeliveryDate()!=null){
				params.put("deliveryDate",
						getDateTime(country, order.getDeliveryDate()));
			}
			
			if(StringUtils.isNotBlank(order.getRemark())){
			   params.put("remark", order.getRemark());	
			}

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplateByEur("_" + order.getAccountName(), params);//getInvoiceTemplateByEur
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				return null;
			}
			//String uuid = UUID.randomUUID().toString();
			//String baseDirStr = ContextLoader.getCurrentWebApplicationContext()
			//		.getServletContext().getRealPath("/")
			//		+ Global.getCkBaseDir() + "/event/"+uuid;
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ 
					Global.getCkBaseDir() + "/eventTax/"+tempCountry+month;
	    	
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
		
			if (hasTax) {
				htmlFile = new File(baseDir,order.getAmazonOrderId() +"_EURBILL.html");
				pdfFile = new File(baseDir, order.getAmazonOrderId() +"_EURBILL.pdf");
			} else {
				htmlFile = new File(baseDir,order.getAmazonOrderId() + "_NEURBILL.html");
				pdfFile = new File(baseDir, order.getAmazonOrderId() +"_NEURBILL.pdf");
			}
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),
					htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,
					new File(new File(baseDirStr).getParentFile().getParentFile(), "event"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	public static File genPdf(AmazonProductService amazonProductService,String country, AmazonOrder order, String type,String itemStr,String quantityStr) {
		boolean hasTax = !type.equals("0");
		try {
			Map<Integer,Integer> itemSelectMap = Maps.newHashMap();
			for(int i =0;i<itemStr.split(",").length;i++){
				itemSelectMap.put(Integer.parseInt(itemStr.split(",")[i]), Integer.parseInt(quantityStr.split(",")[i]));
			}
			
			Map<String, Object> params = Maps.newHashMap();
			AmazonAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			if(type.equals("2")) {
				params.put("title", "Gutschrift");
			} else {
				params.put("title", "Rechnung");
			}
			params.put("type", type);
			params.put("orderId", order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCity() == null ? ""
					: address.getCity());
			params.put(
					"reciever_state",
					address.getStateOrRegion() == null ? "" : address
							.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());

			params.put("reciever_country", address.getCountryCode());

			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			}else if ("mx".equals(country)) {
				billcur = "MXN";
			} else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",
					order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", order.getInvoiceNo());
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			float rate = 0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				rate = code.getVat();
			}
			float vat = 0f;
			if (hasTax) {
				params.put("rate", rate);
			} else {
				params.put("rate", 0);
			}
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			int quantity = 0;
			for (AmazonOrderItem item : order.getItems()) {
				
				if (item.getQuantityOrdered() == null
						|| item.getQuantityOrdered() == 0||itemSelectMap.get(item.getId())==null||itemSelectMap.get(item.getId())==0) {
					continue;
				}
				quantity += itemSelectMap.get(item.getId());
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				itemMap.put("num", itemSelectMap.get(item.getId()) + "");
				itemMap.put("rate", "(" + rate + "%)");
				String name = amazonProductService.findProductName(
						item.getAsin(), country);
				if(StringUtils.isBlank(name)){
					name = HtmlUtils.htmlUnescape(item.getTitle());
				}
				itemMap.put("name", name + "[" + item.getSellersku() + "]");

				float shipping = (item.getShippingPrice() == null ? 0f : item
						.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice())-(item.getShippingDiscount()==null?0f:item.getShippingDiscount());
				float price = item.getItemPrice();
				price = price
						- (item.getPromotionDiscount() == null ? 0f : item
								.getPromotionDiscount());
				float totlePrice = ((price) * 100 / (100 + rate))/item.getQuantityOrdered()*itemSelectMap.get(item.getId());
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				Float singlePrice = (price) / item.getQuantityOrdered() * 100
						/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				orderitemShipprice += shipping;
				orderitem_totalprice += totlePrice;
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",
					getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",
					getNumberStr(orderitem_totalprice, country));
			params.put(
					"order_totalprice",
					getNumberStr(orderitemShipprice + orderitem_totalprice,
							country));
			if (hasTax) {
				vat = (orderitemShipprice + orderitem_totalprice) * rate / 100f;
				/*params.put("totalprice",
						getNumberStr(order.getOrderTotal(), country));*/
				params.put(
						"totalprice",
						getNumberStr(orderitem_totalprice + orderitemShipprice+vat,
								country));
			} else {
				params.put(
						"totalprice",
						getNumberStr(orderitem_totalprice + orderitemShipprice,
								country));
			}
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getPrintDate()==null?order.getPurchaseDate():order.getPrintDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			params.put("paytime",
					getDateTime(country, order.getLastUpdateDate()));
			
			if(order.getDeliveryDate()!=null){
				params.put("deliveryDate",
						getDateTime(country, order.getDeliveryDate()));
			}
			
			if(StringUtils.isNotBlank(order.getRemark())){
			   params.put("remark", order.getRemark());	
			}

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + order.getAccountName(), params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			if (hasTax) {
				htmlFile = new File(baseDir, "Amazon_"
						+ order.getAmazonOrderId() + "_bill.html");
				pdfFile = new File(baseDir, "Amazon_"
						+ order.getAmazonOrderId() + "_bill.pdf");
			} else {
				htmlFile = new File(baseDir, "Amazon_"
						+ order.getAmazonOrderId() + "_nbill.html");
				pdfFile = new File(baseDir, "Amazon_"
						+ order.getAmazonOrderId() + "_nbill.pdf");
			}
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),
					htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,
					new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static File genPdf(AmazonProductService amazonProductService,String country, AmazonOrder order, String type) {
		boolean hasTax = !type.equals("0");
		try {
			Map<String, Object> params = Maps.newHashMap();
			AmazonAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			if(type.equals("2")) {
				params.put("title", "Gutschrift");
			} else {
				params.put("title", "Rechnung");
			}
			params.put("type", type);
			params.put("orderId", order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCity() == null ? ""
					: address.getCity());
			params.put(
					"reciever_state",
					address.getStateOrRegion() == null ? "" : address
							.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());

			params.put("reciever_country", address.getCountryCode());

			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			}else if ("mx".equals(country)) {
				billcur = "MXN";
			} else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",
					order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", order.getInvoiceNo());
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			float rate = 0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				rate = code.getVat();
			}
			float vat = 0f;
			if (hasTax) {
				params.put("rate", rate);
			} else {
				params.put("rate", 0);
			}
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			int quantity = 0;
			for (AmazonOrderItem item : order.getItems()) {
				if (item.getQuantityOrdered() == null
						|| item.getQuantityOrdered() == 0) {
					continue;
				}
				quantity += item.getQuantityOrdered();
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("rate", "(" + rate + "%)");
				String name = amazonProductService.findProductName(
						item.getAsin(), country);
				if(StringUtils.isBlank(name)){
					name = HtmlUtils.htmlUnescape(item.getTitle());
				}
				itemMap.put("name", name + "[" + item.getSellersku() + "]");

				float shipping = (item.getShippingPrice() == null ? 0f : item
						.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice())-(item.getShippingDiscount()==null?0f:item.getShippingDiscount());
				float price = item.getItemPrice();
				price = price
						- (item.getPromotionDiscount() == null ? 0f : item
								.getPromotionDiscount());
				float totlePrice = (price) * 100 / (100 + rate);
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				Float singlePrice = (price) / item.getQuantityOrdered() * 100
						/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				orderitemShipprice += shipping;
				orderitem_totalprice += totlePrice;
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",
					getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",
					getNumberStr(orderitem_totalprice, country));
			params.put(
					"order_totalprice",
					getNumberStr(orderitemShipprice + orderitem_totalprice,
							country));
			if (hasTax) {
				vat = (orderitemShipprice + orderitem_totalprice) * rate / 100f;
				params.put("totalprice",
						getNumberStr(order.getOrderTotal(), country));
			} else {
				params.put(
						"totalprice",
						getNumberStr(orderitem_totalprice + orderitemShipprice,
								country));
			}
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country,order.getPrintDate()==null?order.getPurchaseDate():order.getPrintDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			params.put("paytime",
					getDateTime(country, order.getLastUpdateDate()));
			
			if(order.getDeliveryDate()!=null){
				params.put("deliveryDate",
						getDateTime(country, order.getDeliveryDate()));
			}
			
			if(StringUtils.isNotBlank(order.getRemark())){
			   params.put("remark", order.getRemark());	
			}

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + order.getAccountName(), params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			if (hasTax) {
				htmlFile = new File(baseDir, "Amazon_"
						+ order.getAmazonOrderId() + "_bill.html");
				pdfFile = new File(baseDir, "Amazon_"
						+ order.getAmazonOrderId() + "_bill.pdf");
			} else {
				htmlFile = new File(baseDir, "Amazon_"
						+ order.getAmazonOrderId() + "_nbill.html");
				pdfFile = new File(baseDir, "Amazon_"
						+ order.getAmazonOrderId() + "_nbill.pdf");
			}
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),
					htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,
					new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static File genPdfByRefund(AmazonProductService amazonProductService,String country, AmazonOrder order, String type,String itemIds,String quantitys) {
		try {
			
			Map<Integer,Integer> refundMap = Maps.newHashMap();
			for(int i =0;i<itemIds.split(",").length;i++){
				refundMap.put(Integer.parseInt(itemIds.split(",")[i]), Integer.parseInt(quantitys.split(",")[i]));
			}
			
			Map<String, Object> params = Maps.newHashMap();
			AmazonAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			if (type.equals("2")) {
				params.put("title", "Gutschrift");
			}else if(type.equals("3")){
				params.put("title", "Refund/Gutschrift");
			}
			params.put("type", type);
			params.put("orderId", order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCity() == null ? "": address.getCity());
			params.put("reciever_state",address.getStateOrRegion() == null ? "" : address.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());
			params.put("reciever_country", address.getCountryCode());
			
			if(StringUtils.isNotBlank(order.getRemark())){
				   params.put("remark", order.getRemark());	
			}

			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			}else if ("mx".equals(country)) {
				billcur = "MXN";
			} else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", order.getInvoiceNo());
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			float rate = 0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				rate = code.getVat();
			}
			float vat = 0f;
			params.put("rate", rate);
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			Float orderTotal  =0f;
			int quantity = 0;
			for (AmazonOrderItem item : order.getItems()) {
				//如果不包含在退款单的map里面就跳过
				if (item.getQuantityOrdered() == null|| item.getQuantityOrdered() == 0||!refundMap.containsKey(item.getId())) {
					continue;
				}
				//quantity += item.getQuantityOrdered();
				quantity +=refundMap.get(item.getId());
				
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				//itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("num", refundMap.get(item.getId()) + "");
				itemMap.put("rate", "(" + rate + "%)");
				String name = amazonProductService.findProductName(
						item.getAsin(), country);
				if(StringUtils.isBlank(name)){
					name = HtmlUtils.htmlUnescape(item.getTitle());
				}
				itemMap.put("name", name + "[" + item.getSellersku() + "]");

				float shipping = (item.getShippingPrice() == null ? 0f : item.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice())-(item.getShippingDiscount()==null?0f:item.getShippingDiscount());
				float price = item.getItemPrice();
				price = price- (item.getPromotionDiscount() == null ? 0f : item.getPromotionDiscount());
				//Float singlePrice = (price) / refundMap.get(item.getId()) * 100/ (100 + rate);
				Float singlePrice = price / item.getQuantityOrdered() * 100/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				float totlePrice =singlePrice* refundMap.get(item.getId());
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				orderitemShipprice += shipping;
				orderitem_totalprice += totlePrice;
				//总价=单价加邮费
				orderTotal+=price / item.getQuantityOrdered()* refundMap.get(item.getId()) +shipping;
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",getNumberStr(orderitem_totalprice, country));
			params.put("order_totalprice",getNumberStr(orderitemShipprice + orderitem_totalprice,country));
			vat = (orderitemShipprice + orderitem_totalprice) * rate / 100f;
			//订单总价：
			//params.put("totalprice",getNumberStr(order.getOrderTotal(), country));
			params.put("totalprice",getNumberStr(orderTotal, country));
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getPrintDate()==null?order.getPurchaseDate():order.getPrintDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			params.put("paytime", getDateTime(country, order.getLastUpdateDate()));

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + order.getAccountName(), params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			htmlFile = new File(baseDir, "Amazon_"+ order.getAmazonOrderId() + "_bill.html");
			pdfFile = new File(baseDir, "Amazon_"+ order.getAmazonOrderId() + "_bill.pdf");
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static File genPartPdfByRefund(AmazonProductService amazonProductService,String country, AmazonOrder order, String type,String itemIds,String quantitys,String orderId) {
		try {
			
			Map<Integer,Float> refundMap = Maps.newHashMap();
			for(int i =0;i<itemIds.split(",").length;i++){
				refundMap.put(Integer.parseInt(itemIds.split(",")[i]), Float.parseFloat(quantitys.split(",")[i]));
			}
			
			Map<String, Object> params = Maps.newHashMap();
			AmazonAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			if (type.equals("2")||"4".equals(type)) {
				params.put("title", "Gutschrift");
			}else if(type.equals("3")){
				params.put("title", "Refund/Gutschrift");
			}
			params.put("type", type);
			params.put("orderId", order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCity() == null ? "": address.getCity());
			params.put("reciever_state",address.getStateOrRegion() == null ? "" : address.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());
			params.put("reciever_country", address.getCountryCode());
			
			if(StringUtils.isNotBlank(order.getRemark())){
				   params.put("remark", order.getRemark());	
			}

			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			} else if ("mx".equals(country)) {
				billcur = "MXN";
			}else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", StringUtils.isNotBlank(orderId)?orderId:(order.getInvoiceNo()));
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			float rate = 0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				rate = code.getVat();
			}
			float vat = 0f;
			params.put("rate", rate);
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			Float orderTotal  =0f;
			int quantity = 0;
			for (AmazonOrderItem item : order.getItems()) {
				//如果不包含在退款单的map里面就跳过
				if (item.getQuantityOrdered() == null|| item.getQuantityOrdered() == 0||!refundMap.containsKey(item.getId())) {
					continue;
				}
				//quantity += item.getQuantityOrdered();
				quantity +=refundMap.get(item.getId());
				
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				//itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("rate", "(" + rate + "%)");
				String name = amazonProductService.findProductName(
						item.getAsin(), country);
				if(StringUtils.isBlank(name)){
					name = HtmlUtils.htmlUnescape(item.getTitle());
				}
				itemMap.put("name", name + "[" + item.getSellersku() + "]");

				//float shipping = (item.getShippingPrice() == null ? 0f : item.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice());
				float price = item.getItemPrice();
				price = price- (item.getPromotionDiscount() == null ? 0f : item.getPromotionDiscount());
				//Float singlePrice = (price) / refundMap.get(item.getId()) * 100/ (100 + rate);
				Float singlePrice = price / item.getQuantityOrdered() * 100/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				//float totlePrice =singlePrice* refundMap.get(item.getId());
				float totlePrice =refundMap.get(item.getId())*100/ (100 + rate);
				
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				orderitemShipprice += 0;
				orderitem_totalprice += totlePrice;
				//总价=单价加邮费
				orderTotal+=refundMap.get(item.getId());
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",getNumberStr(orderitem_totalprice, country));
			params.put("order_totalprice",getNumberStr(orderitem_totalprice,country));
			vat = (orderitem_totalprice) * rate / 100f;
			//订单总价：
			//params.put("totalprice",getNumberStr(order.getOrderTotal(), country));
			params.put("totalprice",getNumberStr(orderTotal, country));
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getPrintDate()==null?order.getPurchaseDate():order.getPrintDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			params.put("paytime", getDateTime(country, order.getLastUpdateDate()));

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + order.getAccountName(), params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			htmlFile = new File(baseDir, "Amazon_"+ order.getAmazonOrderId() + "_bill.html");
			pdfFile = new File(baseDir, "Amazon_"+ order.getAmazonOrderId() + "_bill.pdf");
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static File genTaxRefundPdfByRefund(AmazonProductService amazonProductService,String country, AmazonOrder order, String type,String itemIds,String quantitys) {
		try {
			
			Map<String,Float> refundMap = Maps.newHashMap();
			for(int i =0;i<itemIds.split(",").length;i++){
				refundMap.put(itemIds.split(",")[i], Float.parseFloat(quantitys.split(",")[i]));
			}
			
			Map<String, Object> params = Maps.newHashMap();
			AmazonAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
		
			params.put("title", "Gutschrift");
			
			params.put("type","4");
			params.put("orderId", order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}
			params.put("reciever_city", address.getCity() == null ? "": address.getCity());
			params.put("reciever_state",address.getStateOrRegion() == null ? "" : address.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());
			params.put("reciever_country", address.getCountryCode());
			
			if(StringUtils.isNotBlank(order.getRemark())){
				   params.put("remark", order.getRemark());	
			}

			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			} else if ("mx".equals(country)) {
				billcur = "MXN";
			}else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", order.getId() + "");
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			float rate = 0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				rate = code.getVat();
			}
			float vat = 0f;
			params.put("rate", rate);
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			Float orderTotal  =0f;
			int quantity = 0;
			for (AmazonOrderItem item : order.getItems()) {
				//如果不包含在退款单的map里面就跳过
				if (item.getQuantityOrdered() == null|| item.getQuantityOrdered() == 0||!refundMap.containsKey(item.getId())) {
					continue;
				}
				//quantity += item.getQuantityOrdered();
				quantity +=refundMap.get(item.getId());
				
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				//itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("rate", "(" + rate + "%)");
				String name = amazonProductService.findProductName(
						item.getAsin(), country);
				if(StringUtils.isBlank(name)){
					name = HtmlUtils.htmlUnescape(item.getTitle());
				}
				itemMap.put("name", name + "[" + item.getSellersku() + "]");

				//float shipping = (item.getShippingPrice() == null ? 0f : item.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice());
				float price = item.getItemPrice();
				price = price- (item.getPromotionDiscount() == null ? 0f : item.getPromotionDiscount());
				//Float singlePrice = (price) / refundMap.get(item.getId()) * 100/ (100 + rate);
				Float singlePrice = price / item.getQuantityOrdered() * 100/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				//float totlePrice =singlePrice* refundMap.get(item.getId());
				float totlePrice =refundMap.get(item.getId());
				
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				orderitemShipprice += 0;
				orderitem_totalprice += totlePrice;
				//总价=单价加邮费
				orderTotal+=totlePrice;
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",getNumberStr(orderitem_totalprice, country));
			params.put("order_totalprice",getNumberStr(orderitem_totalprice,country));
			//vat = (orderitem_totalprice) * rate / 100f;
			vat=0;
			//订单总价：
			//params.put("totalprice",getNumberStr(order.getOrderTotal(), country));
			params.put("totalprice",getNumberStr(orderTotal, country));
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getPurchaseDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			params.put("paytime", getDateTime(country, order.getLastUpdateDate()));

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + order.getAccountName(), params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			htmlFile = new File(baseDir, "Amazon_"+ order.getAmazonOrderId() + "_bill.html");
			pdfFile = new File(baseDir, "Amazon_"+ order.getAmazonOrderId() + "_bill.pdf");
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static File genPartPdfByRefund(AmazonProductService amazonProductService,String country, AmazonUnlineOrder order, String type,String itemIds,String quantitys) {
		try {
			
			Map<Integer,Float> refundMap = Maps.newHashMap();
			for(int i =0;i<itemIds.split(",").length;i++){
				refundMap.put(Integer.parseInt(itemIds.split(",")[i]), Float.parseFloat(quantitys.split(",")[i]));
			}
			
			Map<String, Object> params = Maps.newHashMap();
			AmazonUnlineAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			if (type.equals("2")||"4".equals(type)) {
				params.put("title", "Gutschrift");
			}else if(type.equals("3")){
				params.put("title", "Refund/Gutschrift");
			}
			params.put("type", type);
			params.put("orderId", order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCity() == null ? "": address.getCity());
			params.put("reciever_state",address.getStateOrRegion() == null ? "" : address.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());
			params.put("reciever_country", address.getCountryCode());
			
			if(StringUtils.isNotBlank(order.getRemark())){
				   params.put("remark", order.getRemark());	
			}

			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			}else if ("mx".equals(country)) {
				billcur = "MXN";
			} else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", order.getInvoiceNo());
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			float rate = 0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				rate = code.getVat();
			}
			float vat = 0f;
			params.put("rate", rate);
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			Float orderTotal  =0f;
			int quantity = 0;
			for (AmazonUnlineOrderItem item : order.getItems()) {
				//如果不包含在退款单的map里面就跳过
				if (item.getQuantityOrdered() == null|| item.getQuantityOrdered() == 0||!refundMap.containsKey(item.getId())) {
					continue;
				}
				//quantity += item.getQuantityOrdered();
				quantity +=refundMap.get(item.getId());
				
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				//itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("rate", "(" + rate + "%)");
				String name = amazonProductService.findProductName(
						item.getAsin(), country);
				if(StringUtils.isBlank(name)){
					name = HtmlUtils.htmlUnescape(item.getTitle());
				}
				itemMap.put("name", name + "[" + item.getSellersku() + "]");

				//float shipping = (item.getShippingPrice() == null ? 0f : item.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice());
				float price = item.getItemPrice();
				price = price- (item.getPromotionDiscount() == null ? 0f : item.getPromotionDiscount());
				//Float singlePrice = (price) / refundMap.get(item.getId()) * 100/ (100 + rate);
				Float singlePrice = price;
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				//float totlePrice =singlePrice* refundMap.get(item.getId());
				float totlePrice =refundMap.get(item.getId());
				
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				orderitemShipprice += 0;
				orderitem_totalprice += totlePrice;
				//总价=单价加邮费
				orderTotal+=totlePrice;
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",getNumberStr(orderitem_totalprice, country));
			params.put("order_totalprice",getNumberStr(orderitem_totalprice,country));
			//vat = (orderitem_totalprice) * rate / 100f;
			vat=0;
			//订单总价：
			//params.put("totalprice",getNumberStr(order.getOrderTotal(), country));
			params.put("totalprice",getNumberStr(orderTotal, country));
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getPurchaseDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			params.put("paytime", getDateTime(country, order.getLastUpdateDate()));

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + country, params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			
			htmlFile = new File(baseDir, "Unline_"+ order.getAmazonOrderId() + "_bill.html");
			pdfFile = new File(baseDir, "Unline_"+ order.getAmazonOrderId() + "_bill.pdf");
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	public static File genPdf(AmazonProductService amazonProductService,String country, AmazonUnlineOrder order, String type,String orderNo,String invoice) {
		boolean hasTax = !type.equals("0");
		try {
			Map<String, Object> params = Maps.newHashMap();
			AmazonUnlineAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			if(type.equals("2")) {
				params.put("title", "Gutschrift");
			} else {
				params.put("title", "Rechnung");
			}
			params.put("type", type);
			params.put("orderId", StringUtils.isNotEmpty(orderNo)?orderNo:order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCity() == null ? ""
					: address.getCity());
			params.put(
					"reciever_state",
					address.getStateOrRegion() == null ? "" : address
							.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());

			params.put("reciever_country", address.getCountryCode());

			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			}else if ("mx".equals(country)) {
				billcur = "MXN";
			} else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",
					order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", (StringUtils.isNotEmpty(invoice)?Integer.parseInt(invoice):order.getInvoiceNo()) + "");
			params.put("customerno", (StringUtils.isNotEmpty(invoice)?Integer.parseInt(invoice):order.getId())* 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			float rate =0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				rate = code.getVat();
			}
			float vat = 0f;
			params.put("rate", rate);
			
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			int quantity = 0;
			for (AmazonUnlineOrderItem item : order.getItems()) {
				if (item.getQuantityOrdered() == null
						|| item.getQuantityOrdered() == 0) {
					continue;
				}
				//rate+=item.getItemTax();
				quantity += item.getQuantityOrdered();
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				itemMap.put("num", item.getQuantityOrdered() + "");
				if (hasTax) {
					itemMap.put("rate", "(" + item.getItemTax() + "%)");
				}
				itemMap.put("name", (item.getProductName()==null?"":item.getProductName()) + "[" + item.getSellersku() + "]");

				float shipping = (item.getShippingPrice() == null ? 0f : item.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice())-(item.getShippingDiscount()==null?0f:item.getShippingDiscount());
				float price = item.getItemPrice();
				price = price
						- (item.getPromotionDiscount() == null ? 0f : item
								.getPromotionDiscount());
				float totlePrice = (price) * item.getQuantityOrdered();
				/*if (hasTax) {
					totlePrice=totlePrice+price* item.getQuantityOrdered()*item.getItemTax()/100;
				}*/
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				String singlePriceStr = getNumberStr2(price, country);
				itemMap.put("singlePrice", singlePriceStr);
				orderitemShipprice += shipping;
				orderitem_totalprice += totlePrice;
				items.add(itemMap);
			}
			//rate=rate/order.getItems().size();
			if (hasTax) {
				params.put("rate", rate);
			} else {
				params.put("rate", 0);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			params.put("orderitem_shipprice",getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",getNumberStr(orderitem_totalprice, country));
			params.put("order_totalprice",getNumberStr(orderitemShipprice + orderitem_totalprice,country));
			if (hasTax) {
				vat = (orderitemShipprice + orderitem_totalprice) * rate / 100f;
				params.put("totalprice",getNumberStr(orderitem_totalprice + orderitemShipprice+vat, country));
			} else {
				params.put("totalprice",getNumberStr(orderitem_totalprice + orderitemShipprice,country));
			}
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getPurchaseDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			//params.put("paytime",getDateTime(country, order.getLastUpdateDate()));
			if("Shipped".equals(order.getOrderStatus())||"Unshipped".equals(order.getOrderStatus())){
				params.put("paytime", getDateTime(country, order.getLastUpdateDate()));
			}
			if(order.getDeliveryDate()!=null){
				params.put("deliveryDate",
						getDateTime(country, order.getDeliveryDate()));
			}
			
			if(StringUtils.isNotBlank(order.getRemark())){
			   params.put("remark", order.getRemark());	
			}
			
			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + country, params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			if (hasTax) {
				htmlFile = new File(baseDir, "Unline_"
						+ order.getAmazonOrderId() + "_bill.html");
				pdfFile = new File(baseDir, "Unline_"
						+ order.getAmazonOrderId() + "_bill.pdf");
			} else {
				htmlFile = new File(baseDir, "Unline_"
						+ order.getAmazonOrderId() + "_nbill.html");
				pdfFile = new File(baseDir, "Unline_"
						+ order.getAmazonOrderId() + "_nbill.pdf");
			}
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),
					htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,
					new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	
	public static File genPdf2(AmazonProductService amazonProductService,String country, AmazonUnlineOrder order, String type,String orderNo,String invoice) {
		boolean hasTax = !type.equals("0");
		try {
			Map<String, Object> params = Maps.newHashMap();
			AmazonUnlineAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			if(type.equals("2")) {
				params.put("title", "Gutschrift");
			} else {
				params.put("title", "Rechnung");
			}
			params.put("type", type);
			params.put("orderId", StringUtils.isNotEmpty(orderNo)?orderNo:order.getAmazonOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getAddressLine1()!=null){
				params.put("reciever_address1", address.getAddressLine1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getAddressLine2()!=null){
				params.put("reciever_address2", address.getAddressLine2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getAddressLine3()!=null){
				params.put("reciever_address3", address.getAddressLine3().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCity() == null ? ""
					: address.getCity());
			params.put(
					"reciever_state",
					address.getStateOrRegion() == null ? "" : address
							.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());

			params.put("reciever_country", address.getCountryCode());

			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			}else if ("mx".equals(country)) {
				billcur = "MXN";
			} else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",
					order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", (StringUtils.isNotEmpty(invoice)?Integer.parseInt(invoice):order.getInvoiceNo()));
			params.put("customerno", (StringUtils.isNotEmpty(invoice)?Integer.parseInt(invoice):order.getId())* 16 + "");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			float rate =0f;
			String countryCode = address.getCountryCode();
			if(countryCode!=null){
				countryCode = countryCode.toUpperCase();
			}else{
				countryCode = "";
			}
			if("co.uk,de,fr,es,it".contains(country)){
				/*CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country;
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}*/
				///rate = code.getVat();
				rate=19f;
			}
			float vat = 0f;
			params.put("rate", rate);
			
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			int quantity = 0;
			for (AmazonUnlineOrderItem item : order.getItems()) {
				if (item.getQuantityOrdered() == null
						|| item.getQuantityOrdered() == 0) {
					continue;
				}
				//rate+=item.getItemTax();
				quantity += item.getQuantityOrdered();
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				itemMap.put("num", item.getQuantityOrdered() + "");
				if (hasTax) {
					itemMap.put("rate", "(" + item.getItemTax() + "%)");
				}
				itemMap.put("name", (item.getProductName()==null?"":item.getProductName()) + "[" + item.getSellersku() + "]");

				float shipping = (item.getShippingPrice() == null ? 0f : item.getShippingPrice())+(item.getGiftWrapPrice()==null?0f:item.getGiftWrapPrice())-(item.getShippingDiscount()==null?0f:item.getShippingDiscount());
				float price = item.getItemPrice();
				price = price
						- (item.getPromotionDiscount() == null ? 0f : item
								.getPromotionDiscount());
				float totlePrice = (price) * item.getQuantityOrdered();
				/*if (hasTax) {
					totlePrice=totlePrice+price* item.getQuantityOrdered()*item.getItemTax()/100;
				}*/
				totlePrice=totlePrice * 100 / (100 + rate);
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				float singlePrice = price*100/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				orderitemShipprice += shipping;
				orderitem_totalprice += totlePrice;
				items.add(itemMap);
			}
			//rate=rate/order.getItems().size();
			if (hasTax) {
				params.put("rate", rate);
			} else {
				params.put("rate", 0);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getAmazonOrderId() + "--该订单无货品");
				return null;
			}
			if(orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			
			params.put("orderitem_shipprice",getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",getNumberStr(orderitem_totalprice, country));
			params.put("order_totalprice",getNumberStr(orderitemShipprice + orderitem_totalprice,country));
			if (hasTax) {
				vat = (orderitemShipprice + orderitem_totalprice) * rate / 100f;
				params.put("totalprice",getNumberStr(order.getOrderTotal(), country));
				//vat = (orderitem_totalprice) * rate / 100f;
			//	params.put("totalprice",getNumberStr(order.getOrderTotal(), country));
			} else {
				params.put("totalprice",getNumberStr(orderitem_totalprice + orderitemShipprice,country));
			}
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getPurchaseDate()));
			params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			//params.put("paytime",getDateTime(country, order.getLastUpdateDate()));
			if("Shipped".equals(order.getOrderStatus())||"Unshipped".equals(order.getOrderStatus())){
				params.put("paytime", getDateTime(country, order.getLastUpdateDate()));
			}
			if(order.getDeliveryDate()!=null){
				params.put("deliveryDate",
						getDateTime(country, order.getDeliveryDate()));
			}
			
			if(StringUtils.isNotBlank(order.getRemark())){
			   params.put("remark", order.getRemark());	
			}
			
			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + country, params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getAmazonOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			if (hasTax) {
				htmlFile = new File(baseDir, "Unline_"
						+ order.getAmazonOrderId() + "_bill.html");
				pdfFile = new File(baseDir, "Unline_"
						+ order.getAmazonOrderId() + "_bill.pdf");
			} else {
				htmlFile = new File(baseDir, "Unline_"
						+ order.getAmazonOrderId() + "_nbill.html");
				pdfFile = new File(baseDir, "Unline_"
						+ order.getAmazonOrderId() + "_nbill.pdf");
			}
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),
					htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,
					new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	public static String getNumberStr(Float num, String country) {
		if(country.startsWith("com")){
			country="com";
		}
		NumberFormat format = NumberFormat.getInstance(AmazonWSConfig.get(
				country).getLocale());
		format.setMinimumFractionDigits(2);
		String rs = "0.00";
		if (null != num) {
			DecimalFormat df = new DecimalFormat("0.00");
			String singlePriceStr = df.format(num);
			rs = singlePriceStr;
		}
		return format.format(Double.parseDouble(rs));
	}
	
	public static String getNumberStr2(Float num, String country) {
		NumberFormat format = NumberFormat.getInstance(AmazonWSConfig.get(
				country).getLocale());
		format.setMinimumFractionDigits(2);
		String rs = "0.000";
		if (null != num) {
			DecimalFormat df = new DecimalFormat("0.000");
			String singlePriceStr = df.format(num);
			rs = singlePriceStr;
		}
		return format.format(Double.parseDouble(rs));
	}

	private static String getDate(String country, Date date) {
		AmazonWSConfig config = AmazonWSConfig.get(country);
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,
				config.getLocale());
		df.setTimeZone(config.getTimeZone());
		return df.format(date);
	}

	private static String getDateTime(String country, Date date) {
		AmazonWSConfig config = AmazonWSConfig.get(country);
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT, config.getLocale());
		df.setTimeZone(config.getTimeZone());
		return df.format(date);
	}

	public static File genEbayPdfRefund2(String country,EbayOrder order, String type,String itemIds,String quantitys) {
		try {
			
			Map<Integer,Integer> refundMap = Maps.newHashMap();
			for(int i =0;i<itemIds.split(",").length;i++){
				refundMap.put(Integer.parseInt(itemIds.split(",")[i]), Integer.parseInt(quantitys.split(",")[i]));
			}
			
			Map<String, Object> params = Maps.newHashMap();
			EbayAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			if (type.equals("2")||"4".equals(type)) {
				params.put("title", "Gutschrift");
			}else if(type.equals("3")){
				params.put("title", "Refund/Gutschrift");
			}
			params.put("type", type);
			params.put("orderId", order.getOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getStreet()!=null){
				params.put("reciever_address1", address.getStreet().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getStreet1()!=null){
				params.put("reciever_address2", address.getStreet1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getStreet2()!=null){
				params.put("reciever_address3", address.getStreet2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCityName() == null ? ""
					: address.getCityName());
			params.put(
					"reciever_state",
					address.getStateOrProvince() == null ? "" : address
							.getStateOrProvince());
			params.put("reciever_postcode", address.getPostalCode());

			params.put("reciever_country", address.getCountryCode());


			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			}else if ("mx".equals(country)) {
				billcur = "MXN";
			} else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethods());
			params.put("rate_sn",
					order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", order.getInvoiceNo());
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerUserId() + "@ebay.com");
			// 以下需要计算
			int rate = 19;
			if("com".equals(country)){
				rate=0;
			}
			boolean hasTax = !type.equals("0");
			float vat = 0f;
			if (hasTax) {
				params.put("rate", rate);
			} else {
				params.put("rate", 0);
			}
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			Float orderTotal  =0f;
			int quantity = 0;
			for (EbayOrderItem item : order.getItems()) {
				//如果不包含在退款单的map里面就跳过
				if (item.getQuantityPurchased() == null|| item.getQuantityPurchased() == 0||!refundMap.containsKey(item.getId())) {
					continue;
				}
				//quantity += item.getQuantityOrdered();
				quantity +=refundMap.get(item.getId());
				
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				//itemMap.put("num", item.getQuantityOrdered() + "");
				itemMap.put("num", refundMap.get(item.getId()) + "");
				itemMap.put("rate", "(" + rate + "%)");
				itemMap.put("name", item.getTitle());

				float price = item.getTransactionPrice().floatValue();
				float totlePrice = (price) * refundMap.get(item.getId())*100 / (100 + rate);
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				Float singlePrice = (price) * 100/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				
				orderitem_totalprice += totlePrice;
				orderTotal+=price* refundMap.get(item.getId());
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getOrderId() + "--该订单无货品");
				return null;
			}
			
			float shipping;
			if (order.getShippingServiceCost().floatValue() == 0)
				shipping = 0f;
			else
				shipping = order.getShippingServiceCost().floatValue();
			
			orderitemShipprice += shipping;
			
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",getNumberStr(orderitemShipprice, country));


			params.put("items", items);
			params.put("orderitem_totalprice",getNumberStr(orderitem_totalprice, country));
			params.put("order_totalprice",getNumberStr(orderitemShipprice + orderitem_totalprice,country));
			vat = (orderitemShipprice + orderitem_totalprice) * rate / 100f;
			//订单总价：
			//params.put("totalprice",getNumberStr(order.getOrderTotal(), country));
			params.put("totalprice",getNumberStr(orderTotal, country));
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getCreatedTime()));
			params.put("buytime",getDateTime(country, order.getLastModifiedTime()));
			params.put("paytime",getDateTime(country, order.getLastModifiedTime()));

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + country, params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			htmlFile = new File(baseDir, "Ebay_"+ order.getOrderId() + "_bill.html");
			pdfFile = new File(baseDir, "Ebay_"+ order.getOrderId() + "_bill.pdf");
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static File genEbayPdfRefund1(String country,EbayOrder order, String type,String itemIds,String quantitys) {
		try {
			
			Map<Integer,Float> refundMap = Maps.newHashMap();
			for(int i =0;i<itemIds.split(",").length;i++){
				refundMap.put(Integer.parseInt(itemIds.split(",")[i]), Float.parseFloat(quantitys.split(",")[i]));
			}
			
			Map<String, Object> params = Maps.newHashMap();
			EbayAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			if (type.equals("2")||"4".equals(type)) {
				params.put("title", "Gutschrift");
			}else if(type.equals("3")){
				params.put("title", "Refund/Gutschrift");
			}
			params.put("type", type);
			params.put("orderId", order.getOrderId());
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getStreet()!=null){
				params.put("reciever_address1", address.getStreet().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getStreet1()!=null){
				params.put("reciever_address2", address.getStreet1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getStreet2()!=null){
				params.put("reciever_address3", address.getStreet2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCityName() == null ? ""
					: address.getCityName());
			params.put(
					"reciever_state",
					address.getStateOrProvince() == null ? "" : address
							.getStateOrProvince());
			params.put("reciever_postcode", address.getPostalCode());

			params.put("reciever_country", address.getCountryCode());


			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			}else if ("mx".equals(country)) {
				billcur = "MXN";
			} else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethods());
			params.put("rate_sn",
					order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", order.getInvoiceNo());
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerUserId() + "@ebay.com");
			// 以下需要计算
			int rate = 19;
			if("com".equals(country)){
				rate=0;
			}
			boolean hasTax = !type.equals("0");
			float vat = 0f;
			if (hasTax) {
				params.put("rate", rate);
			} else {
				params.put("rate", 0);
			}
			
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			Float orderTotal  =0f;
			int quantity = 0;
			for (EbayOrderItem item : order.getItems()) {
				//如果不包含在退款单的map里面就跳过
				if (item.getQuantityPurchased() == null|| item.getQuantityPurchased() == 0||!refundMap.containsKey(item.getId())) {
					continue;
				}
				quantity +=refundMap.get(item.getId());
				
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				itemMap.put("num", item.getQuantityPurchased() + "");
				itemMap.put("rate", "(" + rate + "%)");
				itemMap.put("name", item.getTitle());
				
				float price =refundMap.get(item.getId());
				float totlePrice = (price) *100 / (100 + rate);
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				Float singlePrice = (price) * 100/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				
				orderitem_totalprice += totlePrice;
				orderTotal+=refundMap.get(item.getId());
				items.add(itemMap);
			}
			if (quantity <= 0) {
				LOGGER.error(order.getOrderId() + "--该订单无货品");
				return null;
			}
			
			float shipping;
			if (order.getShippingServiceCost().floatValue() == 0)
				shipping = 0f;
			else
				shipping = order.getShippingServiceCost().floatValue();
			
			orderitemShipprice += shipping;
			
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",getNumberStr(orderitemShipprice, country));

			
			
			params.put("items", items);
			params.put("orderitem_totalprice",getNumberStr(orderitem_totalprice, country));
			params.put("order_totalprice",getNumberStr(orderitem_totalprice,country));
			vat = (orderitem_totalprice) * rate / 100f;
			//订单总价：
			//params.put("totalprice",getNumberStr(order.getOrderTotal(), country));
			params.put("totalprice",getNumberStr(orderTotal, country));
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getCreatedTime()));
			params.put("buytime",getDateTime(country, order.getLastModifiedTime()));
			params.put("paytime",getDateTime(country, order.getLastModifiedTime()));

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + country, params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error(order.getOrderId()+"-----invoice模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			htmlFile = new File(baseDir, "Ebay_"+ order.getOrderId() + "_bill.html");
			pdfFile = new File(baseDir, "Ebay_"+ order.getOrderId() + "_bill.pdf");
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static File genEbayPdf(EbayOrder order, String country, String type) {
		boolean hasTax = !type.equals("0");
		try {
			Map<String, Object> params = Maps.newHashMap();
			EbayAddress address = order.getInvoiceAddress();
			if (address == null) {
				address = order.getShippingAddress();
			}
			if (type.equals("2")) {
				params.put("title", "Gutschrift");
			} else {
				params.put("title", "Rechnung");
			}
			params.put("type", type);
			params.put("orderId", order.getOrderId());
			
			
			params.put("customer_name", address.getName().replace("<", "").replace(">", ""));

			if(address.getStreet()!=null){
				params.put("reciever_address1", address.getStreet().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address1", "");
			}
			if(address.getStreet1()!=null){
				params.put("reciever_address2", address.getStreet1().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address2", "");
			}
			if(address.getStreet2()!=null){
				params.put("reciever_address3", address.getStreet2().replace("<", "").replace(">", ""));
			}else{
				params.put("reciever_address3", "");
			}

			params.put("reciever_city", address.getCityName() == null ? ""
					: address.getCityName());
			params.put(
					"reciever_state",
					address.getStateOrProvince() == null ? "" : address
							.getStateOrProvince());
			params.put("reciever_postcode", address.getPostalCode());

			params.put("reciever_country", address.getCountryCode());

			String billcur = "EUR";
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethods());
			params.put("rate_sn",
					order.getRateSn() == null ? "" : order.getRateSn());
			params.put("invoiceno", order.getInvoiceNo());
			params.put("customerno", order.getId() * 16 + "");

			params.put("customer_email", order.getBuyerUserId() + "@ebay.com");
			// 以下需要计算

			int rate = 19;
			if("com".equals(country)){
				rate=0;
			}
			float vat = 0f;
			if (hasTax) {
				params.put("rate", rate);
			} else {
				params.put("rate", 0);
			}

			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			int quantity = 0;
			for (EbayOrderItem item : order.getItems()) {
				if (item.getQuantityPurchased() == null
						|| item.getQuantityPurchased() == 0) {
					continue;
				}
				quantity += item.getQuantityPurchased();
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId() + "");
				itemMap.put("num", item.getQuantityPurchased() + "");
				itemMap.put("rate", "(" + rate + "%)");
				itemMap.put("name", item.getTitle());
				

				float price = item.getTransactionPrice().floatValue();
				float totlePrice = (price) * item.getQuantityPurchased()*100 / (100 + rate);
				itemMap.put("totlePrice", getNumberStr(totlePrice, country));
				Float singlePrice = (price) * 100/ (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice, country);
				itemMap.put("singlePrice", singlePriceStr);
				
				orderitem_totalprice += totlePrice;
				items.add(itemMap);
			}
			float shipping;
			if (order.getShippingServiceCost().floatValue() == 0)
				shipping = 0f;
			else
				shipping = order.getShippingServiceCost().floatValue();
			
			orderitemShipprice += shipping;
			
			if (quantity <= 0) {
				LOGGER.error(order.getOrderId() + "--该订单无货品");
				return null;
			}
			if (orderitemShipprice > 0) {
				orderitemShipprice = orderitemShipprice * 100 / (100 + rate);
			}
			params.put("orderitem_shipprice",
					getNumberStr(orderitemShipprice, country));

			params.put("items", items);
			params.put("orderitem_totalprice",
					getNumberStr(orderitem_totalprice, country));
			params.put(
					"order_totalprice",
					getNumberStr(orderitemShipprice + orderitem_totalprice,
							country));
			if (hasTax) {
				vat = (orderitemShipprice + orderitem_totalprice) * rate / 100f;
				/*float adjust=0f;
				if(order.getAdjustmentAmount()!=null){
					adjust=order.getAdjustmentAmount().floatValue();
				}
				params.put("totalprice",
						getNumberStr(order.getSubtotal().floatValue()+order.getShippingServiceCost().floatValue()-adjust, country));*/
				params.put("totalprice",
						getNumberStr(order.getSubtotal().floatValue()+order.getShippingServiceCost().floatValue(), country));
			} else {
				params.put(
						"totalprice",
						getNumberStr(orderitem_totalprice + orderitemShipprice,
								country));
			}
			params.put("vat", getNumberStr(vat, country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getCreatedTime()));
			params.put("buytime",
					getDateTime(country, order.getLastModifiedTime()));
			
			if (!"0".equals(order.getStatus())) {
				params.put("paytime",
						getDateTime(country, order.getLastModifiedTime()));
			}

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + country, params);
			if (invoiceTemp == null || invoiceTemp.length() == 0) {
				LOGGER.error("invoice模板加载失败");
				return null;
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ Global.getCkBaseDir() + "/invoice";
			File baseDir = new File(baseDirStr);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			File htmlFile = null;
			File pdfFile = null;
			if (hasTax) {
				htmlFile = new File(baseDir, "Ebay_" + order.getOrderId()
						+ "_bill.html");
				pdfFile = new File(baseDir, "Ebay_" + order.getOrderId()
						+ "_bill.pdf");
			} else {
				htmlFile = new File(baseDir, "Ebay_" + order.getOrderId()
						+ "_nbill.html");
				pdfFile = new File(baseDir, "Ebay_" + order.getOrderId()
						+ "_nbill.pdf");
			}
			invoiceTemp = head+invoiceTemp.replaceAll("(&(?!amp;))", "&amp;")+foot;
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()),
					htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,
					new File(new File(path).getParentFile(), "invoice"));
			htmlFile.delete();
			return pdfFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	
}
