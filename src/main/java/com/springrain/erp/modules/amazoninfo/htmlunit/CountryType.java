package com.springrain.erp.modules.amazoninfo.htmlunit;

import java.util.Locale;

public enum CountryType {
	
	America("com","com.inateck",Locale.US,"$","$###,###,###.00","","111","ATVPDKIKX0DER","MM/dd/yyyy","US/Eastern","MM/dd/yy",0,""),//美国inateck产品;
	Germany("de", "de",Locale.GERMANY,"EUR","EUR ###,###,###.00","","111","A1PA6795UKMFR9", "dd.MM.yyyy","Europe/Berlin","dd/MM/yyyy",1,""),	
	France("de","fr",Locale.FRANCE,"EUR","EUR ###,###,###.00","","111","A13V1IB3VIYZZH","dd/MM/yyyy","Europe/London","dd/MM/yy",2,""),
	England("de","uk",Locale.UK,"£","£###,###,###.00","","111","A1F83G8C2ARO7P","dd/MM/yyyy","Europe/London","dd/MM/yyyy",3,""),
	Italy("de", "it",Locale.ITALY,"EUR","EUR ###,###,###.00","","111","APJ6JRA9NG5V4" ,"dd/MM/yyyy","Europe/Berlin","dd/MM/yy",4,""),	
	Spain("de","es",new Locale("es","ES"),"EUR","EUR ###,###,###.00","","111","A1RKKUPIHCS9HS","dd/MM/yyyy","Europe/London","dd/MM/yy",5,""),
	Japan("co.jp","jp",Locale.JAPAN,"￥","￥ ###,###,###.00","","111","A1VC38T7YXB528","MM/dd/yyyy","Japan","MM/dd/yy",6,""),
	Canada("ca","ca",Locale.CANADA,"CDN$","CDN$ ###,###,###.00","","111","A2EUQ1WTGCTBG2","MM/dd/yyyy","Canada/Pacific","MM/dd/yy",7,""),
	Mexico("com.mx","mx",new Locale("es","MX"),"MXN$","MXN$ ###,###,###.00","","111","A1AM78C64UM0Y8","MM/dd/yyyy","America/Mexico_City","MM/dd/yy",8,"");
	
	private String suffix; // 域名后缀
	private int index; // 下标
	private Locale locale;// 与知道创宇的type值对应;
	private String name; // 国家名字;
	private String flag; // 钱币的符号;
	private String pattern;// 商品售价的样式
	private String username; // 后台登陆用户名
	private String password;// 后台登陆密码;s
	private String marketPlaceID;
	private String timePattern;
	private String timeZone;
	private String accountTimePattern;// 账号差评列表时间类型;
	private String authCode;

	private CountryType(String suffix, String name, Locale locale, String flag,
			String pattern, String username, String password,
			String marketPlaceID, String timePattern, String timeZone,
			String accountTimePattern, int index,String authCode) {
		this.suffix = suffix;
		this.name = name;
		this.locale = locale;
		this.flag = flag;
		this.pattern = pattern;
		String cKey = name;
		if ("com.inateck".equals(name)) {
			cKey = "com";
		}
		
		/*try {
			username = AmazonProduct2Service.getAmazonAttr().get(cKey).get("email");
		} catch (NullPointerException e) {}*/
		
		this.username = username;
		
		/*try {
			password = AmazonProduct2Service.getAmazonAttr().get(cKey).get("password");
		} catch (NullPointerException e) {}*/
		this.password = password;
		this.marketPlaceID = marketPlaceID;
		this.timePattern = timePattern;
		this.timeZone = timeZone;
		this.accountTimePattern = accountTimePattern;
		this.index = index;
		this.authCode = authCode;
	}

	public static int size() {

		return CountryType.values().length;
	}

	public String getSuffix() {
		return suffix;
	}

	public int getIndex() {
		return index;
	}

	public Locale getLocale() {
		return locale;
	}

	public String getName() {
		return name;
	}

	public String getPattern() {
		return pattern;
	}

	public String getFlag() {
		return flag;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getMarketPlaceID() {
		return marketPlaceID;
	}

	public String getTimePattern() {
		return timePattern;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public String getAccountTimePattern() {
		return accountTimePattern;
	}
	

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	/**
	 * 通过域名的后缀名,获得国家的类型
	 * 
	 * @param index
	 * @return
	 */
	public static CountryType getCountryType(Integer index) {
		for (CountryType s : CountryType.values()) {
			if (s.getIndex() == index) {
				return s;
			}
		}
		return null;
	}

	/**
	 * 通过名称获得国家类型.以适配tim;
	 * 
	 * @param name
	 * @return
	 */
	public static CountryType getCountryTypeByEsayName(String name) {
		for (CountryType s : CountryType.values()) {
			if (s.getName().equals(name)) {
				return s;
			}
		}
		return null;
	}
    
}
 