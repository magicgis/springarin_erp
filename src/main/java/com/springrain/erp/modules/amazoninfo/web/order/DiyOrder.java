package com.springrain.erp.modules.amazoninfo.web.order;

import java.io.File;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.springrain.erp.common.utils.FreeMarkers;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.scheduler.AmazonWSConfig;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class DiyOrder {
	
	public static void main(String[] args) {
		AmazonOrder order = new AmazonOrder();
		AmazonAddress address = new AmazonAddress();
		
		address.setName("Invoice address :");
		
		address.setAddressLine1("UEFA");
		address.setCity("Invoice processing");
		address.setStateOrRegion("Route de Genève 46");
		address.setCountryCode("1260 Nyon 2");
		order.setShippingAddress(address);
		order.setId(2014081901);
		order.setBuyerEmail("itpurchase@uefa.ch");
		order.setPurchaseDate(new Date());
		order.setOrderTotal(690f);
		order.setPaymentMethod("100%TT before delivery");
		List<AmazonOrderItem> items = Lists.newArrayList();
		AmazonOrderItem item = new AmazonOrderItem();
		item.setId(2014081901);
		item.setSellersku("Inateck HB5001");
		item.setItemPrice(324.00f);
		item.setQuantityShipped(12);
		items.add(item);
		item = new AmazonOrderItem();
		item.setId(2014081902);
		item.setSellersku("Inateck UC4001");
		item.setItemPrice(130.00f);
		item.setQuantityShipped(10);
		items.add(item);
		item = new AmazonOrderItem();
		item.setId(2014081903);
		item.setSellersku("Inateck FD1006");
		item.setItemPrice(44.00f);
		item.setQuantityShipped(2);
		items.add(item);
		item = new AmazonOrderItem();
		item.setId(2014081904);
		item.setSellersku("Inateck UA2001");
		item.setItemPrice(162.00f);
		item.setQuantityShipped(6);
		item.setShippingPrice(30f);
		items.add(item);
		order.setItems(items);
		genPdf("de",order,false);
		
		
	}
	private static String path = "d:/111";

	public static File genPdf(String country, AmazonOrder order,boolean hasTax){
		try {
			Map<String, Object> params = Maps.newHashMap();
			AmazonAddress address = order.getInvoiceAddress();
			if(address==null){
				address = order.getShippingAddress();
			}
			params.put("customer_name", address.getName());
			String shippAd = address.getAddressLine1() == null ? "" : address
					.getAddressLine1();
			shippAd += (address.getAddressLine2() == null ? "" : " "
					+ address.getAddressLine2());
			shippAd += (address.getAddressLine3() == null ? "" : " "
					+ address.getAddressLine3());
			params.put("reciever_address", shippAd);
			params.put("reciever_city", address.getCity()==null?"":address.getCity());
			params.put("reciever_state", address.getStateOrRegion()==null?"":address.getStateOrRegion());
			params.put("reciever_postcode", address.getPostalCode());

			params.put("reciever_country", address.getCountryCode());

			String billcur = "";
			if ("uk".equals(country)) {
				billcur = "GBP";
			} else if ("com".equals(country)) {
				billcur = "$";
			} else if ("jp".equals(country)) {
				billcur = "JPY";
			} else {
				billcur = "EUR";
			}
			params.put("BILLCUR", billcur);

			params.put("paymethod", order.getPaymentMethod());
			params.put("rate_sn",order.getRateSn()==null?"":order.getRateSn());
			params.put("invoiceno", order.getId()+"");
			params.put("customerno", order.getId()+"");

			params.put("customer_email", order.getBuyerEmail());
			// 以下需要计算
			int rate = 0;
			float vat = 0f;
			if (hasTax) {
				params.put("rate", rate);
			} else {
				params.put("rate", 0);
			}
			List<Map<String, Object>> items = Lists.newArrayList();
			Float orderitemShipprice = 0f;
			Float orderitem_totalprice = 0f;
			int quantity = 0 ;
			for (AmazonOrderItem item : order.getItems()) {
				if(item.getQuantityShipped()==null||item.getQuantityShipped()==0){
					continue;
				}
				quantity+= item.getQuantityShipped();
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", item.getId()+"");
				itemMap.put("num", item.getQuantityShipped()+"");
				itemMap.put("rate", "("+rate+"%)");
				itemMap.put("name",item.getSellersku());
				float shipping = (item.getShippingPrice()==null?0f:item.getShippingPrice())-(item.getShippingDiscount()==null?0f:item.getShippingDiscount());
				float price = item.getItemPrice();
				price = price - (item.getPromotionDiscount()==null?0f:item.getPromotionDiscount());
				float totlePrice = (price) * 100
						/ (100 + rate);
				itemMap.put("totlePrice", getNumberStr(totlePrice,country));
				Float singlePrice = (price)
						/ item.getQuantityShipped() * 100 / (100 + rate);
				String singlePriceStr = getNumberStr(singlePrice,country);
				itemMap.put("singlePrice", singlePriceStr);
				orderitemShipprice += shipping;
				orderitem_totalprice += totlePrice;
				items.add(itemMap);
			}
			if(quantity<=0){
				return null;
			}
			if(orderitemShipprice>0){
				orderitemShipprice = orderitemShipprice*100/(100+rate);
			}
			params.put("orderitem_shipprice", getNumberStr(orderitemShipprice,country));
			
			params.put("items", items);
			params.put("orderitem_totalprice",getNumberStr(orderitem_totalprice,country));
			params.put("order_totalprice",getNumberStr(orderitemShipprice+orderitem_totalprice,country));
			if(hasTax){
				vat = (orderitemShipprice+orderitem_totalprice) * rate/100f ;
				params.put("totalprice", getNumberStr(order.getOrderTotal(),country));
			}else{
				params.put("totalprice", getNumberStr(orderitem_totalprice+orderitemShipprice,country));
			}
			params.put("vat", getNumberStr(vat,country));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			params.put("buydate", getDate(country, order.getPurchaseDate()));
			/*params.put("buytime", getDateTime(country, order.getPurchaseDate()));
			params.put("paytime",  getDateTime(country, order.getLastUpdateDate()));*/

			// 生成账单附件
			String invoiceTemp = getInvoiceTemplate("_" + country, params);
			if(invoiceTemp==null||invoiceTemp.length()==0){
				return null;
			}
			String baseDirStr = path+"/invoice";
			File baseDir = new File(baseDirStr);
			if(!baseDir.exists()){
				baseDir.mkdirs();
			}
			File htmlFile = null ;
			File pdfFile = null;
			if(hasTax){
				htmlFile = new File(baseDir,"Amazon_"+order.getAmazonOrderId()+"_bill.html");
				pdfFile = new File(baseDir,"Amazon_"+order.getAmazonOrderId()+"_bill.pdf");
			}else{
				htmlFile = new File(baseDir,"Amazon_"+order.getAmazonOrderId()+"_nbill.html");
				pdfFile = new File(baseDir,"Amazon_"+order.getAmazonOrderId()+"_nbill.pdf");
			}
			invoiceTemp = invoiceTemp.replaceAll("(&(?!amp;))", "&amp;");
			Files.write(invoiceTemp.subSequence(0, invoiceTemp.length()), htmlFile, Charset.forName("utf-8"));
			PdfUtil.htmlToPdf(htmlFile, pdfFile,new File(path,"invoice"));
			htmlFile.delete();
			return pdfFile;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static String getNumberStr(Float num,String country) {
		NumberFormat format = NumberFormat.getInstance(AmazonWSConfig.get(country).getLocale());
		String rs = "0.00";
		if (null != num) {
			DecimalFormat df = new DecimalFormat("0.00");
			String singlePriceStr = df.format(num);
			rs = singlePriceStr;
		}
		return format.format(Double.parseDouble(rs));
	}
	
	private static String getDate(String country, Date date) {
		AmazonWSConfig config = AmazonWSConfig.get(country);
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, config.getLocale());
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
	
	private static String getInvoiceTemplate(String country, Map<String, Object> params) {
		if (country == null) {
			return "";
		}
		try {
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(path,"invoice"));
			Template template = cfg.getTemplate("invoice" + country + ".ftl");
			if (params != null && params.size() > 0) {
				return FreeMarkers.renderTemplate(template, params);
			} else {
				return FreeMarkers.renderTemplate(template, null);
			}
		} catch (Exception e) {
			if (country.length() > 0) {
				return getInvoiceTemplate("", params);
			} else {
				return getInvoiceTemplate(null, params);
			}
		}
	}
}
