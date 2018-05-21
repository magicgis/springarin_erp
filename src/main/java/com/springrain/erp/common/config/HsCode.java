package com.springrain.erp.common.config;

import com.springrain.erp.common.utils.StringUtils;

public enum HsCode {
	
	//euHscode,jpHscode,caHscode,usHscode,hkHscode, cnHscode,
	HDD_enclosures("HDD enclosures","84719000000","8473.30.019","8517620049","8517.62.0050","85176290","8517629900",0f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	Docking_station("Docking station","84719000000","8473.30.019","8517620049","8517.62.0050","85176290","8517629900",0f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	Express_card("Express card","84719000000","8517.70.000","8517620049","8517.62.0050","85176290","8517629900",0f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	Hub("Hub","84714900900","8517.62.000","8517620049","8517.62.0050","85176290","8517623500",0f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	Cable("Cable","85444290100","8517.62.000","8517620049","8517.62.0050","85176290","8517623500",3.3f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	Adaptor("Adaptor","84714900900","8517.62.000","8517620049","8517.62.0050","85176290","8517629900",0f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	Speaker("Speaker","85182100900","8518.22.000","8518210000","8518.21.0000","85182100","8518210000",4.5f,0f,6.5f,4.9f,19f,8f,14.975f,0.47f,15f,0f,""),
	Keyboard("Keyboard","84716060900","847160000","8471600050","8471.60.2000","84716099","8471607100",0f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	Scanner("Scanner","84719000000","8517.90.000","8471900000","8471.90.0000","84719000","8471900090",0f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	HDD_case("HDD case","39239000000","4202.99.090","4202929090","8517.62.0050","42050090","4202920000",6.5f,4.6f,7f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	Tablet_PC_bag("Tablet PC bag","39239000000","6307.90.0292","4202929090","4205.00.8000","42050090","420212900",6.5f,5f,7f,5.7f,19f,8f,14.975f,0.47f,15f,0f,""),
	Kindle_cover("Kindle cover","39239000000","3926.90.0294","4202929090","4205.00.8000","42050090","4202920000",6.5f,3.9f,7f,20f,19f,8f,14.975f,0.47f,15f,0f,""),
	USB_Charger("USB Charger","84714900900","8504.90.000","8504409039","8504.40.8500","85044099","8504409999",0f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	Car_Charger("Car Charger","84714900900","8504.40.011","8504401000","8504.40.8500","85044099","8504409999",0f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	Phone_case("Phone case","39239000000","3926.90.0294","4202929090","4205.00.8000","42050090","4202920000",6.5f,4f,7f,20f,19f,8f,14.975f,0.47f,15f,0f,""),
	Earphone("Earphone","85183020000","8518.30.000","8518301000","8517.62.0050","85183010","8518300000",2f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	HDD_Adapter("HDD Adapter","84714900900","8517.62.000","8517620049","8517.62.0050","85176290","8517629900",0f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,""),
	Tomons_Lamps("Tomons Lamps","9405209990","9405.20.000","9405.20.00 00","9405.20.80.10","94052000","9405200090",2.7f,0f,7.0f,3.9f,19f,8f,14.975f,0.47f,15f,0f,""),
	Wireless_presenter("Wireless presenter","85176200900","9013.20.090","9013200000","8471.60.1050","84716099","9013200090",0f,0f,0f,0f,19f,8f,14.975f,0.47f,15f,0f,"");

	private          String             euHscode;
	private          String             jpHscode;
	private          String             caHscode;
	private          String             usHscode;
	private          String             hkHscode;
	private          String             cnHscode;
	private          String             mxHscode;
	private          Float              euCustomDuty;
	private          Float              euImportDuty;
	private          Float              jpCustomDuty;
	private          Float              jpImportDuty;
	private          Float              caCustomDuty;
	private          Float              caImportDuty;
	private          Float              usCustomDuty;
	private          Float              usImportDuty;
	private          Float              mxCustomDuty;
	private          Float              mxImportDuty;
	private          String             productType;
	
	private HsCode(String productType,String euHscode, String jpHscode, String caHscode,
			String usHscode, String hkHscode, String cnHscode,
			Float euCustomDuty,  Float jpCustomDuty,Float caCustomDuty,Float usCustomDuty,
			Float euImportDuty,Float jpImportDuty,  Float caImportDuty,Float usImportDuty,  Float mxImportDuty,Float mxCustomDuty,String mxHscode) {
		this.productType=productType;
		this.euHscode = euHscode;
		this.jpHscode = jpHscode;
		this.caHscode = caHscode;
		this.usHscode = usHscode;
		this.hkHscode = hkHscode;
		this.cnHscode = cnHscode;
		this.euCustomDuty = euCustomDuty;
		this.euImportDuty = euImportDuty;
		this.jpCustomDuty = jpCustomDuty;
		this.jpImportDuty = jpImportDuty;
		this.caCustomDuty = caCustomDuty;
		this.caImportDuty = caImportDuty;
		this.usCustomDuty = usCustomDuty;
		this.usImportDuty = usImportDuty;
		this.mxCustomDuty = mxCustomDuty;
		this.mxImportDuty = mxImportDuty;
		this.mxHscode = mxHscode;
	}
	

	public String getMxHscode() {
		return mxHscode;
	}



	public void setMxHscode(String mxHscode) {
		this.mxHscode = mxHscode;
	}




	public Float getMxCustomDuty() {
		return mxCustomDuty;
	}




	public void setMxCustomDuty(Float mxCustomDuty) {
		this.mxCustomDuty = mxCustomDuty;
	}




	public Float getMxImportDuty() {
		return mxImportDuty;
	}




	public void setMxImportDuty(Float mxImportDuty) {
		this.mxImportDuty = mxImportDuty;
	}




	public String getEuHscode() {
		return euHscode;
	}
	public void setEuHscode(String euHscode) {
		this.euHscode = euHscode;
	}
	public String getJpHscode() {
		return jpHscode;
	}
	public void setJpHscode(String jpHscode) {
		this.jpHscode = jpHscode;
	}
	public String getCaHscode() {
		return caHscode;
	}
	public void setCaHscode(String caHscode) {
		this.caHscode = caHscode;
	}
	public String getUsHscode() {
		return usHscode;
	}
	public void setUsHscode(String usHscode) {
		this.usHscode = usHscode;
	}
	public String getHkHscode() {
		return hkHscode;
	}
	public void setHkHscode(String hkHscode) {
		this.hkHscode = hkHscode;
	}
	public String getCnHscode() {
		return cnHscode;
	}
	public void setCnHscode(String cnHscode) {
		this.cnHscode = cnHscode;
	}
	public Float getEuCustomDuty() {
		return euCustomDuty;
	}
	public void setEuCustomDuty(Float euCustomDuty) {
		this.euCustomDuty = euCustomDuty;
	}
	public Float getEuImportDuty() {
		return euImportDuty;
	}
	public void setEuImportDuty(Float euImportDuty) {
		this.euImportDuty = euImportDuty;
	}
	public Float getJpCustomDuty() {
		return jpCustomDuty;
	}
	public void setJpCustomDuty(Float jpCustomDuty) {
		this.jpCustomDuty = jpCustomDuty;
	}
	public Float getJpImportDuty() {
		return jpImportDuty;
	}
	public void setJpImportDuty(Float jpImportDuty) {
		this.jpImportDuty = jpImportDuty;
	}
	public Float getCaCustomDuty() {
		return caCustomDuty;
	}
	public void setCaCustomDuty(Float caCustomDuty) {
		this.caCustomDuty = caCustomDuty;
	}
	public Float getCaImportDuty() {
		return caImportDuty;
	}
	public void setCaImportDuty(Float caImportDuty) {
		this.caImportDuty = caImportDuty;
	}
	public Float getUsCustomDuty() {
		return usCustomDuty;
	}
	public void setUsCustomDuty(Float usCustomDuty) {
		this.usCustomDuty = usCustomDuty;
	}
	public Float getUsImportDuty() {
		return usImportDuty;
	}
	public void setUsImportDuty(Float usImportDuty) {
		this.usImportDuty = usImportDuty;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	
	public static HsCode get(String key) {
		if(StringUtils.isEmpty(key)){
			return null;
		}
		String temp =key;
		return valueOf(temp.replaceAll(" ","_"));
	}
	
}
 