package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.amazonservices.mws.orders._2013_09_01.MWSEndpoint;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;

public enum AmazonWSConfig {

	COM("com","","",MWSEndpoint.NA_PROD.toString(),"A2QGX098CVHYJ7","ATVPDKIKX0DER",TimeZone.getTimeZone("America/Los_Angeles"),Locale.US,"USD","Refund initiated for order ",Pattern.compile("\\$\\d+\\.?\\d{0,2}"),"US",null),
	DE("de","","",MWSEndpoint.DE_PROD.toString(),"A5JH7MGCI556L","A1PA6795UKMFR9",TimeZone.getTimeZone("Europe/Berlin"),Locale.GERMANY,"EUR","Gutschrift veranlasst zu Bestellung ",Pattern.compile("EUR \\d+\\,?\\d{0,2}"),"DE",null),
	JP("jp","","",MWSEndpoint.JP_PROD.toString(),"A2744L3VQVPUXF","A1VC38T7YXB528",TimeZone.getTimeZone("Asia/Tokyo"),Locale.JAPAN,"JPY","Amazon.co.jp - 返金処理開始のお知らせ：注文番号",Pattern.compile("￥ \\d+\\.?\\d{0,2}"),"JP",null),
	IT("it","","",MWSEndpoint.IT_PROD.toString(),"A5JH7MGCI556L","APJ6JRA9NG5V4",TimeZone.getTimeZone("Europe/Rome"),Locale.ITALY,"EUR","Rimborso emesso per l'ordine ",Pattern.compile("EUR \\d+\\,?\\d{0,2}"),"IT",null),
	ES("es","","",MWSEndpoint.ES_PROD.toString(),"A5JH7MGCI556L","A1RKKUPIHCS9HS",TimeZone.getTimeZone("Europe/Madrid"),new Locale("es","ES"),"EUR","Reembolso iniciado en pedido ",Pattern.compile("EUR\\d+\\.?\\d{0,2}"),"ES",null),
	UK("uk","","",MWSEndpoint.UK_PROD.toString(),"A5JH7MGCI556L","A1F83G8C2ARO7P",TimeZone.getTimeZone("Europe/London"),Locale.UK,"GBP","Refund initiated for order ",Pattern.compile("£\\d+\\.?\\d{0,2}"),"GB",null),
	FR("fr","","",MWSEndpoint.FR_PROD.toString(),"A5JH7MGCI556L","A13V1IB3VIYZZH",TimeZone.getTimeZone("Europe/Paris"),Locale.FRANCE,"EUR","Remboursement déclenché pour la commande ",Pattern.compile("EUR \\d+\\,?\\d{0,2}"),"FR",null),
	CA("ca","","","https://mws.amazonservices.ca","A2AB7Q6LDAB3U0","A2EUQ1WTGCTBG2",TimeZone.getTimeZone("Canada/Pacific"),Locale.CANADA,"CAD","Refund initiated for order ",Pattern.compile("CDN\\$ \\d+\\.?\\d{0,2}"),"CA",null),
	MX("mx","","","https://mws.amazonservices.com.mx","A2ANQDE1QGDL8X","A1AM78C64UM0Y8",TimeZone.getTimeZone("America/Mexico_City"),new Locale("es","MX"),"MXN","Reembolso iniciado en pedido ",Pattern.compile("MXN\\$ \\d+\\.?\\d{0,2}"),"MX",null);

	private String accessKey;
	private String secretKey;
	private String serviceURL;
	private String sellerId;
	private String marketplaceId;
	private String key;
	private TimeZone timeZone;
	private Locale locale;
	private String priceUnit;
	private String returnGoodsSubj;
	private Pattern pricePattern;
	private String countryCode;
	private String mwsAuthToken;	//开发者模式授权Token(非开发者模式配置时置空)
	
	
	private AmazonWSConfig(String key,String accessKey,String secretKey,String serviceURL,String sellerId,String marketplaceId,TimeZone timeZone,Locale locale,String priceUnit,String returnGoodsSubj,Pattern pricePattern,String countryCode,String mwsAuthToken){
		try {
			accessKey = AmazonProduct2Service.getAmazonAttr().get(key).get("accessKey");
		} catch (NullPointerException e) {}
		this.accessKey = accessKey;
		try {
			secretKey = AmazonProduct2Service.getAmazonAttr().get(key).get("secretKey");
		} catch (NullPointerException e) {}
		this.secretKey = secretKey;
		this.serviceURL = serviceURL;
		this.key = key;
		try {
			sellerId = AmazonProduct2Service.getAmazonAttr().get(key).get("sellerId");
		} catch (NullPointerException e) {}
		this.sellerId = sellerId;
		this.marketplaceId = marketplaceId;
		this.timeZone = timeZone;
		this.locale = locale;
		this.priceUnit = priceUnit;
		this.returnGoodsSubj = returnGoodsSubj;
		this.pricePattern = pricePattern;
		this.countryCode = countryCode;
		this.mwsAuthToken = mwsAuthToken;
	}
	
	public String getReturnGoodsSubj() {
		return returnGoodsSubj;
	}

	public void setReturnGoodsSubj(String returnGoodsSubj) {
		this.returnGoodsSubj = returnGoodsSubj;
	}

	public Pattern getPricePattern() {
		return pricePattern;
	}

	public void setPricePattern(Pattern pricePattern) {
		this.pricePattern = pricePattern;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getMarketplaceId() {
		return marketplaceId;
	}

	public void setMarketplaceId(String marketplaceId) {
		this.marketplaceId = marketplaceId;
	}
	
	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public String getPriceUnit() {
		return priceUnit;
	}

	public void setPriceUnit(String priceUnit) {
		this.priceUnit = priceUnit;
	}
	
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getMwsAuthToken() {
		return mwsAuthToken;
	}

	public void setMwsAuthToken(String mwsAuthToken) {
		this.mwsAuthToken = mwsAuthToken;
	}

	public static AmazonWSConfig get(String key) {
		if(StringUtils.isEmpty(key)){
			return null;
		}
		String temp =key;
		String serverId = Global.getConfig("server.id");
	    if("3".equals(serverId)){
	      if("com".equals(key)){
	        temp = "com2";
	      }
	    }
		try {
			return valueOf(temp.toUpperCase());
		} catch (Exception e) {
			
		}
		return null;
	}
	
}
