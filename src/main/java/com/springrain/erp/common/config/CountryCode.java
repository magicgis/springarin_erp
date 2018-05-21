package com.springrain.erp.common.config;

public enum CountryCode {
	
	DE("DE","德国","Germany",19f,"44001"),
	GB("GB","英国","United Kingdom",20f,"43210"),
	FR("FR","法国","France",20f,"43220"),
	IT("IT","意大利","Italy",22f,"43240"),
	AT("AT","奥地利","Austria",20f,"43250"),
	ES("ES","西班牙","Spain",21f,"43230"),
	BE("BE","比利时","Belgium",21f,"43260"),
	IE("IE","爱尔兰","Ireland",23f,"43270"),
	NL("NL","荷兰","Netherlands",19f,"44004"),
	LU("LU","卢森堡","Luxembourg",19f,"44005"),
	DK("DK","丹麦","Denmark",19f,"44006"),
	PT("PT","葡萄牙","Portugal",19f,"44014"),
	GR("GR","希腊","Greece",19f,"44007"),
	FI("FI","芬兰","Finland",19f,"44008"),
	SE("SE","瑞典","Sweden",19f,"44009"),
	CY("CY","塞浦路斯","Cyprus",19f,"44019"),
	RO("RO","罗马尼亚","Romania",19f,"44011"),
	MT("MT","马耳他","Malta",19f,"44024"),
	MC("MC","摩纳哥","Monaco",19f,"44018"),
	PL("PL","波兰","Poland",19f,"44015"),
	SI("SI","斯洛文尼亚","Slovenia",19f,"44016"),
	EE("EE","爱沙尼亚","Estonia",19f,"44010"),
	BG("BG","保加利亚","Bulgaria",19f,"44017"),
	LV("LV","拉脱维亚","Latvia",19f,"44020"),
	HU("HU","匈牙利","Hungary",19f,"44013"),
	SM("SM","圣马力诺","San Marino",19f,"44031"),
	CZ("CZ","捷克","CzechRepublic",19f,"44022"),
	LT("LT","立陶宛","Lithuania",19f,"44021"),
	SK("SK","斯洛伐克","Slovakia",19f,"44023"),
	GG("GG","根西岛","GG",19f,"44030"),
	JE("JE","泽西岛","JE",19f,"44027"),
	IM("IM","马恩岛","IM",19f,"44028"),
	CH("CH","瑞士","Switzerland",19f,"44012"),
	HR("HR","克罗地亚","Croatia",19f,"44029"),
	VA("VA","梵蒂冈","Vatican",19f,"44025"),
	AD("AD","安道尔","AD",19f,"44026"),
	US("US","美国","America",6.47f,"45026"),
	CA("CA","加拿大","Canada",15f,"45027"),
	JP("JP","日本","Japan",8f,"45028"),
	MX("MX","墨西哥","Mexico",16f,"45580"),;

	
	private String code; // 国家名字;
	private String cnName;
	private String name;
	private Float vat;
	private String numberCode;
	
	private CountryCode(String code, String cnName,String name,Float vat,String numberCode) {
		this.code = code;
		this.cnName = cnName;
		this.name = name;
		this.vat = vat;
		this.numberCode = numberCode;
	}
	
	public String getNumberCode() {
		return numberCode;
	}

	public void setNumberCode(String numberCode) {
		this.numberCode = numberCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public Float getVat() {
		return vat;
	}

	public void setVat(Float vat) {
		this.vat = vat;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static int size() {
		return CountryCode.values().length;
	}

}
 