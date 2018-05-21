package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.amazonservices.mws.orders._2013_09_01.MWSEndpoint;

@Entity
@Table(name = "amazoninfo_account_config")
public class AmazonAccountConfig {
	
	private Integer id; 	
	private String country;
	private String accountName;
	private String accessKey;
	private String secretKey;
    private String sellerId;
	private String mwsAuthToken;
	private String userName;
	private String password;
	private String accountSecretKey;
	private String serverIp;
	private String serverId;
	private String state;
	private String delFlag;
	
	
	private String marketplaceId;
	private String serviceURL;
	private String priceUnit;
	private String returnGoodsSubj; 
	private String countryCode;
	private TimeZone timeZone;
	private Locale locale;
	private Pattern pricePattern;
	
	private String suffix; // 域名后缀
	private String flag; // 钱币的符号;
	private String pattern;// 商品售价的样式
	private String timePattern;
	
	private String invoiceType;
	
	//配套的vendor账号和ams账号
	private String vendorName;
	private String vendorPwd;
	private String amsName;
	private String amsPwd;
	private String amsEntityId;	//ams EntityId
	private String amsCode;	//ams二次校验码
	
	private String fbaAddr;
	
	private String customerEmail;
	private String customerEmailPassword;
	private String emailType;
	
	private String skuIndex;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFbaAddr() {
		return fbaAddr;
	}

	public void setFbaAddr(String fbaAddr) {
		this.fbaAddr = fbaAddr;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
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

	public String getAccountSecretKey() {
		return accountSecretKey;
	}

	public void setAccountSecretKey(String accountSecretKey) {
		this.accountSecretKey = accountSecretKey;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getMwsAuthToken() {
		return mwsAuthToken;
	}

	public void setMwsAuthToken(String mwsAuthToken) {
		this.mwsAuthToken = mwsAuthToken;
	}

	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	
	@Transient
	public String getMarketplaceId() {
			if("de".equals(country)){
				marketplaceId="A1PA6795UKMFR9";
			}else if("fr".equals(country)){
				marketplaceId="A13V1IB3VIYZZH";
			}else if("it".equals(country)){
				marketplaceId="APJ6JRA9NG5V4";
			}else if("es".equals(country)){
				marketplaceId="A1RKKUPIHCS9HS";
			}else if("uk".equals(country)){
				marketplaceId="A1F83G8C2ARO7P";
			}else if(country.startsWith("com")){
				marketplaceId="ATVPDKIKX0DER";
			}else if("ca".equals(country)){
				marketplaceId="A2EUQ1WTGCTBG2";
			}else if("mx".equals(country)){
				marketplaceId="A1AM78C64UM0Y8";
			}else if("jp".equals(country)){
				marketplaceId="A1VC38T7YXB528";
			}
			return marketplaceId;
	}

	@Transient
	public String getServiceURL() {
		if("de".equals(country)){
			serviceURL=MWSEndpoint.DE_PROD.toString();
		}else if("fr".equals(country)){
			serviceURL=MWSEndpoint.FR_PROD.toString();
		}else if("it".equals(country)){
			serviceURL=MWSEndpoint.IT_PROD.toString();
		}else if("es".equals(country)){
			serviceURL=MWSEndpoint.ES_PROD.toString();
		}else if("uk".equals(country)){
			serviceURL=MWSEndpoint.UK_PROD.toString();
		}else if(country.startsWith("com")){
			serviceURL=MWSEndpoint.NA_PROD.toString();
		}else if("ca".equals(country)){
			serviceURL="https://mws.amazonservices.ca";
		}else if("mx".equals(country)){
			serviceURL="https://mws.amazonservices.com.mx";
		}else if("jp".equals(country)){
			serviceURL=MWSEndpoint.JP_PROD.toString();
		}
		return serviceURL;
	}
	
	@Transient
	public String getPriceUnit() {
		if("de".equals(country)){
			priceUnit="EUR";
		}else if("fr".equals(country)){
			priceUnit="EUR";
		}else if("it".equals(country)){
			priceUnit="EUR";
		}else if("es".equals(country)){
			priceUnit="EUR";
		}else if("uk".equals(country)){
			priceUnit="GBP";
		}else if(country.startsWith("com")){
			priceUnit="USD";
		}else if("ca".equals(country)){
			priceUnit="CAD";
		}else if("mx".equals(country)){
			priceUnit="MXN";
		}else if("jp".equals(country)){
			priceUnit="JPY";
		}
		return priceUnit;
	}
	
	@Transient
	public String getReturnGoodsSubj() {
		if("de".equals(country)){
			returnGoodsSubj="Gutschrift veranlasst zu Bestellung ";
		}else if("fr".equals(country)){
			returnGoodsSubj="Remboursement déclenché pour la commande ";
		}else if("it".equals(country)){
			returnGoodsSubj="Rimborso emesso per l'ordine ";
		}else if("es".equals(country)){
			returnGoodsSubj="Reembolso iniciado en pedido ";
		}else if("uk".equals(country)){
			returnGoodsSubj="Refund initiated for order ";
		}else if(country.startsWith("com")){
			returnGoodsSubj="Refund initiated for order ";
		}else if("ca".equals(country)){
			returnGoodsSubj="Refund initiated for order ";
		}else if("mx".equals(country)){
			returnGoodsSubj="Reembolso iniciado en pedido ";
		}else if("jp".equals(country)){
			returnGoodsSubj="Amazon.co.jp - 返金処理開始のお知らせ：注文番号";
		}
		return returnGoodsSubj;
	}

	@Transient
	public String getCountryCode() {
		if("de".equals(country)){
			countryCode="DE";
		}else if("fr".equals(country)){
			countryCode="FR";
		}else if("it".equals(country)){
			countryCode="IT";
		}else if("es".equals(country)){
			countryCode="ES";
		}else if("uk".equals(country)){
			countryCode="GB";
		}else if(country.startsWith("com")){
			countryCode="US";
		}else if("ca".equals(country)){
			countryCode="CA";
		}else if("mx".equals(country)){
			countryCode="MX";
		}else if("jp".equals(country)){
			countryCode="JP";
		}
		return countryCode;
	}
	
	@Transient
	public TimeZone getTimeZone() {
		if("de".equals(country)){
			timeZone = TimeZone.getTimeZone("Europe/Berlin");
		}else if("fr".equals(country)){
			timeZone = TimeZone.getTimeZone("Europe/Paris");
		}else if("it".equals(country)){
			timeZone = TimeZone.getTimeZone("Europe/Rome");
		}else if("es".equals(country)){
			timeZone = TimeZone.getTimeZone("Europe/Madrid");
		}else if("uk".equals(country)){
			timeZone = TimeZone.getTimeZone("Europe/London");
		}else if(country.startsWith("com")){
			timeZone = TimeZone.getTimeZone("America/Los_Angeles");
		}else if("ca".equals(country)){
			timeZone = TimeZone.getTimeZone("Canada/Pacific");
		}else if("mx".equals(country)){
			timeZone = TimeZone.getTimeZone("America/Mexico_City");
		}else if("jp".equals(country)){
			timeZone = TimeZone.getTimeZone("Asia/Tokyo");
		}
		return timeZone;
	}
	
	@Transient
	public Locale getLocale() {
		if("de".equals(country)){
			locale = Locale.GERMANY;
		}else if("fr".equals(country)){
			locale =Locale.FRANCE;
		}else if("it".equals(country)){
			locale = Locale.ITALY;
		}else if("es".equals(country)){
			locale =new Locale("es","ES");
		}else if("uk".equals(country)){
			locale = Locale.UK;
		}else if(country.startsWith("com")){
			locale = Locale.US;
		}else if("ca".equals(country)){
			locale =Locale.CANADA;
		}else if("mx".equals(country)){
			locale =new Locale("es","MX");
		}else if("jp".equals(country)){
			locale = Locale.JAPAN;
		}
		return locale;
	}
	
	@Transient
	public Pattern getPricePattern() {
		if("de".equals(country)){
			pricePattern = Pattern.compile("EUR \\d+\\,?\\d{0,2}");
		}else if("fr".equals(country)){
			pricePattern = Pattern.compile("EUR \\d+\\,?\\d{0,2}");
		}else if("it".equals(country)){
			pricePattern = Pattern.compile("EUR \\d+\\,?\\d{0,2}");
		}else if("es".equals(country)){
			pricePattern = Pattern.compile("EUR\\d+\\.?\\d{0,2}");
		}else if("uk".equals(country)){
			pricePattern = Pattern.compile("£\\d+\\.?\\d{0,2}");
		}else if(country.startsWith("com")){
			pricePattern = Pattern.compile("\\$\\d+\\.?\\d{0,2}");
		}else if("ca".equals(country)){
			pricePattern = Pattern.compile("CDN\\$ \\d+\\.?\\d{0,2}");
		}else if("mx".equals(country)){
			pricePattern = Pattern.compile("MXN\\$ \\d+\\.?\\d{0,2}");
		}else if("jp".equals(country)){
			pricePattern = Pattern.compile("￥ \\d+\\.?\\d{0,2}");
		}
		return pricePattern;
	}
	
	@Transient
	public String getSuffix() {
		if("de".equals(country)){
			suffix="de";
		}else if("fr".equals(country)){
			suffix="fr";
		}else if("it".equals(country)){
			suffix="it";
		}else if("es".equals(country)){
			suffix="es";
		}else if("uk".equals(country)){
			suffix="co.uk";
		}else if(country.startsWith("com")){
			suffix="com";
		}else if("ca".equals(country)){
			suffix="ca";
		}else if("mx".equals(country)){
			suffix="com.mx";
		}else if("jp".equals(country)){
			suffix="co.jp";
		}
		return suffix;
	}
	
	@Transient
	public String getPattern() {
		if("de".equals(country)){
			pattern="EUR ###,###,###.00";
		}else if("fr".equals(country)){
			pattern="EUR ###,###,###.00";
		}else if("it".equals(country)){
			pattern="EUR ###,###,###.00";
		}else if("es".equals(country)){
			pattern="EUR ###,###,###.00";
		}else if("uk".equals(country)){
			pattern="£###,###,###.00";
		}else if(country.startsWith("com")){
			pattern="$###,###,###.00";
		}else if("ca".equals(country)){
			pattern="CDN$ ###,###,###.00";
		}else if("mx".equals(country)){
			pattern="MXN$ ###,###,###.00";
		}else if("jp".equals(country)){
			pattern="￥ ###,###,###.00";
		}
		return pattern;
	}

	@Transient
	public String getFlag() {
		if("de".equals(country)){
			flag="EUR";
		}else if("fr".equals(country)){
			flag="EUR";
		}else if("it".equals(country)){
			flag="EUR";
		}else if("es".equals(country)){
			flag="EUR";
		}else if("uk".equals(country)){
			flag="£";
		}else if(country.startsWith("com")){
			flag="$";
		}else if("ca".equals(country)){
			flag="CDN$";
		}else if("mx".equals(country)){
			flag="MXN$";
		}else if("jp".equals(country)){
			flag="￥";
		}
		return flag;
	}
	
	@Transient
	public String getTimePattern() {
		if("de".equals(country)){
			timePattern="dd.MM.yyyy";
		}else if("fr".equals(country)){
			timePattern="dd/MM/yyyy";
		}else if("it".equals(country)){
			timePattern="dd/MM/yyyy";
		}else if("es".equals(country)){
			timePattern="dd/MM/yyyy";
		}else if("uk".equals(country)){
			timePattern="dd/MM/yyyy";
		}else if(country.startsWith("com")){
			timePattern="MM/dd/yyyy";
		}else if("ca".equals(country)){
			timePattern="MM/dd/yyyy";
		}else if("mx".equals(country)){
			timePattern="dd/MM/yyyy";
		}else if("jp".equals(country)){
			timePattern="MM/dd/yyyy";
		}
		return timePattern;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerEmailPassword() {
		return customerEmailPassword;
	}

	public void setCustomerEmailPassword(String customerEmailPassword) {
		this.customerEmailPassword = customerEmailPassword;
	}

	public String getEmailType() {
		return emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorPwd() {
		return vendorPwd;
	}

	public void setVendorPwd(String vendorPwd) {
		this.vendorPwd = vendorPwd;
	}

	public String getAmsName() {
		return amsName;
	}

	public void setAmsName(String amsName) {
		this.amsName = amsName;
	}

	public String getAmsPwd() {
		return amsPwd;
	}

	public void setAmsPwd(String amsPwd) {
		this.amsPwd = amsPwd;
	}

	public String getAmsEntityId() {
		return amsEntityId;
	}

	public void setAmsEntityId(String amsEntityId) {
		this.amsEntityId = amsEntityId;
	}

	public String getAmsCode() {
		return amsCode;
	}

	public void setAmsCode(String amsCode) {
		this.amsCode = amsCode;
	}

	public String getSkuIndex() {
		return skuIndex;
	}

	public void setSkuIndex(String skuIndex) {
		this.skuIndex = skuIndex;
	}
	
}


