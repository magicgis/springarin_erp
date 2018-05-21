package com.springrain.erp.modules.psi.scheduler;

import com.springrain.erp.common.utils.StringUtils;
/***
 *这里是到各个国家天数及到了本地仓后到各个fba仓库的天数 
 * 
 */
public enum PsiConfig {
	COM("com",14,35,2,3,20,40,9),
	COM2("com2",14,35,2,3,20,40,9),
	COM3("com3",14,35,2,3,20,40,9),
	DE("de",19,56,9,9,12,52,10),
	JP("jp",11,26,7,7,9,22,10),
	IT("it",19,56,9,9,12,52,10),
	ES("es",28,65,18,18,12,52,10),
	UK("uk",20,57,10,10,12,52,10),
	FR("fr",19,58,11,11,12,52,10),
	CA("ca",12,12,7,7,20,35,12),
	MX("mx",12,12,7,7,12,35,9);
	
	private String key;
	private int transportBySky;
	private int transportBySea;
	private int wareHouseBySky;
	private int wareHouseBySea;
	
	private int fbaBySky;
	private int fbaBySea;
	private int fbaByExpress;
	
	
	private PsiConfig(String key, int transportBySky, int transportBySea,int wareHouseBySky,int wareHouseBySea,int fbaBySky,int fbaBySea,int fbaByExpress) {
		this.key = key;
		this.transportBySky = transportBySky;
		this.transportBySea = transportBySea;
		this.wareHouseBySky = wareHouseBySky;
		this.wareHouseBySea = wareHouseBySea;
		this.fbaBySky=fbaBySky;
		this.fbaBySea=fbaBySea;
		this.fbaByExpress=fbaByExpress;
	}


	
	public int getFbaBySky() {
		return fbaBySky;
	}

	public void setFbaBySky(int fbaBySky) {
		this.fbaBySky = fbaBySky;
	}




	public int getFbaBySea() {
		return fbaBySea;
	}




	public void setFbaBySea(int fbaBySea) {
		this.fbaBySea = fbaBySea;
	}




	public int getFbaByExpress() {
		return fbaByExpress;
	}




	public void setFbaByExpress(int fbaByExpress) {
		this.fbaByExpress = fbaByExpress;
	}




	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getTransportBySky() {
		return transportBySky;
	}

	public void setTransportBySky(int transportBySky) {
		this.transportBySky = transportBySky;
	}

	public int getTransportBySea() {
		return transportBySea;
	}

	public void setTransportBySea(int transportBySea) {
		this.transportBySea = transportBySea;
	}

	
	
	public int getWareHouseBySky() {
		return wareHouseBySky;
	}

	public void setWareHouseBySky(int wareHouseBySky) {
		this.wareHouseBySky = wareHouseBySky;
	}

	public int getWareHouseBySea() {
		return wareHouseBySea;
	}

	public void setWareHouseBySea(int wareHouseBySea) {
		this.wareHouseBySea = wareHouseBySea;
	}

	public static PsiConfig get(String key) {
		if(StringUtils.isEmpty(key)){
			return null;
		}
		String temp =key;
		return valueOf(temp.toUpperCase());
	}
}
