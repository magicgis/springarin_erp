package com.springrain.erp.modules.psi.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "psi_out_of_stock_info")
public class PsiOutOfStockInfo {
	private		Integer  	 id;
	private     String       productName;
	private     String       color;
	private     String       country;
	private     Integer      fbaQuantity;
	private     Integer      quantityDay31;
	private     Float        beforePrice;
	private     Float        afterPrice;
	private     Date         createDate;
	private     Date         actualDate;
	private     String       sku;
	private     String       info1;//在途
	private     String       info2;//在产
	private     String       info3;//在库（CN）
	private     String       info4;//在库（海外）
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getFbaQuantity() {
		return fbaQuantity;
	}

	public void setFbaQuantity(Integer fbaQuantity) {
		this.fbaQuantity = fbaQuantity;
	}

	

	public Integer getQuantityDay31() {
		return quantityDay31;
	}

	public void setQuantityDay31(Integer quantityDay31) {
		this.quantityDay31 = quantityDay31;
	}

	public Float getBeforePrice() {
		return beforePrice;
	}

	public void setBeforePrice(Float beforePrice) {
		this.beforePrice = beforePrice;
	}

	public Float getAfterPrice() {
		return afterPrice;
	}

	public void setAfterPrice(Float afterPrice) {
		this.afterPrice = afterPrice;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getActualDate() {
		return actualDate;
	}

	public void setActualDate(Date actualDate) {
		this.actualDate = actualDate;
	}

	
	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}
	
	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}
	
	public String getInfo3() {
		return info3;
	}

	public void setInfo3(String info3) {
		this.info3 = info3;
	}

	public String getInfo4() {
		return info4;
	}

	public void setInfo4(String info4) {
		this.info4 = info4;
	}

	
	
}
