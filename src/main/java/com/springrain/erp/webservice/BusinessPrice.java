package com.springrain.erp.webservice;

import java.io.Serializable;

import javax.persistence.Transient;

import com.springrain.erp.common.utils.excel.annotation.ExcelField;

/**
 * 亚马逊产品B2B价格Entity
 * 
 */
public class BusinessPrice implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id; // 编号
	
	private String asin;
	private String sku;
	
	private Float businessPrice;	//B2B价格
	//5档阶梯价格和数量
	private Float price1;
	private Float price2;
	private Float price3;
	private Float price4;
	private Float price5;
	private Integer quantity1;
	private Integer quantity2;
	private Integer quantity3;
	private Integer quantity4;
	private Integer quantity5;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}
	
	@ExcelField(title="asin", align=2, sort=40)
	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public BusinessPrice() {}

	public Float getBusinessPrice() {
		return businessPrice;
	}

	public void setBusinessPrice(Float businessPrice) {
		this.businessPrice = businessPrice;
	}

	public Float getPrice1() {
		return price1;
	}

	public void setPrice1(Float price1) {
		this.price1 = price1;
	}

	public Float getPrice2() {
		return price2;
	}

	public void setPrice2(Float price2) {
		this.price2 = price2;
	}

	public Float getPrice3() {
		return price3;
	}

	public void setPrice3(Float price3) {
		this.price3 = price3;
	}

	public Float getPrice4() {
		return price4;
	}

	public void setPrice4(Float price4) {
		this.price4 = price4;
	}

	public Float getPrice5() {
		return price5;
	}

	public void setPrice5(Float price5) {
		this.price5 = price5;
	}

	public Integer getQuantity1() {
		return quantity1;
	}

	public void setQuantity1(Integer quantity1) {
		this.quantity1 = quantity1;
	}

	public Integer getQuantity2() {
		return quantity2;
	}

	public void setQuantity2(Integer quantity2) {
		this.quantity2 = quantity2;
	}

	public Integer getQuantity3() {
		return quantity3;
	}

	public void setQuantity3(Integer quantity3) {
		this.quantity3 = quantity3;
	}

	public Integer getQuantity4() {
		return quantity4;
	}

	public void setQuantity4(Integer quantity4) {
		this.quantity4 = quantity4;
	}

	public Integer getQuantity5() {
		return quantity5;
	}

	public void setQuantity5(Integer quantity5) {
		this.quantity5 = quantity5;
	}
	
	/**
	 * 获取最高一级的阶梯价(即B2B最低价)
	 * @return
	 */
	@Transient
	public Float getLowPrice(){
		if (price5 != null) {
			return price5;
		} else if (price4 != null) {
			return price4;
		} else if (price3 != null) {
			return price3;
		} else if (price2 != null) {
			return price2;
		} else if (price1 != null) {
			return price1;
		}else {
			return businessPrice;
		}
	}
	
}
