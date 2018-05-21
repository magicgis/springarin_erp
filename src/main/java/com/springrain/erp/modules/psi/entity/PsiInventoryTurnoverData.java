package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 库存周转率基础数据Entity
 */
@Entity
@Table(name = "psi_inventory_turnover_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PsiInventoryTurnoverData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String month;	//月份
	private String dataType;	//0:月 1：年
	private String country;	//国家
	private String productName;	//产品名
	private String productType;	//产品类型
	private String line;	//产品线
	private Integer salesVolume;	//销量
	private Integer sQuantity;	//月初库存
	private Integer eQuantity;	//月末库存
	private Float sPrice;	//月初价格(CNY),价格存在货币单位变化的情况,统一转为CNY
	private Float ePrice;	//月末价格(CNY)
	private Float rate;	//当月库存周转率
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public Integer getsQuantity() {
		return sQuantity;
	}

	public void setsQuantity(Integer sQuantity) {
		this.sQuantity = sQuantity;
	}

	public Integer geteQuantity() {
		return eQuantity;
	}

	public void seteQuantity(Integer eQuantity) {
		this.eQuantity = eQuantity;
	}

	public Float getsPrice() {
		return sPrice;
	}

	public void setsPrice(Float sPrice) {
		this.sPrice = sPrice;
	}

	public Float getePrice() {
		return ePrice;
	}

	public void setePrice(Float ePrice) {
		this.ePrice = ePrice;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public Integer getSalesVolume() {
		return salesVolume;
	}

	public void setSalesVolume(Integer salesVolume) {
		this.salesVolume = salesVolume;
	}

	
}


