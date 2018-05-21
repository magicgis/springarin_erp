/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;

/**
 * 产品积压
 * @author Tim
 * @version 2015-06-01
 */
public class OutOffStockDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private 	String  	sku;
	private 	Float       price;          // 目前售价
	private     String      outOffDaysStr;   // 断货日期字符串
	private     Float       delAmount;      // 损失营业额
	private     Integer     outOffDays;     // 断货天数
	private     String      country;        // 国家
	
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public String getOutOffDaysStr() {
		return outOffDaysStr;
	}
	public void setOutOffDaysStr(String outOffDaysStr) {
		this.outOffDaysStr = outOffDaysStr;
	}
	public Float getDelAmount() {
		return delAmount;
	}
	public void setDelAmount(Float delAmount) {
		this.delAmount = delAmount;
	}
	public Integer getOutOffDays() {
		return outOffDays;
	}
	public void setOutOffDays(Integer outOffDays) {
		this.outOffDays = outOffDays;
	}
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public OutOffStockDto(String sku, Float price, String outOffDaysStr,
			Float delAmount, Integer outOffDays,String country) {
		super();
		this.sku = sku;
		this.price = price;
		this.outOffDaysStr = outOffDaysStr;
		this.delAmount = delAmount;
		this.outOffDays = outOffDays;
		this.country=country;
	}
	
}


