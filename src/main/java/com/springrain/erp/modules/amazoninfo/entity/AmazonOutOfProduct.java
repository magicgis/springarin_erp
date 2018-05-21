package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "amazoninfo_out_of_product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AmazonOutOfProduct implements Serializable{
	
	private 	static final long serialVersionUID = 1L;
	private 	Integer id; 			 
	private 	String 	productNameColor;
	private 	String 	country;
	private 	Date 	dataDate;
	//以下非字段
    private     Integer daySpace;
    private     String  dayStr;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductNameColor() {
		return productNameColor;
	}

	public void setProductNameColor(String productNameColor) {
		this.productNameColor = productNameColor;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}
	
	@Transient
	public Integer getDaySpace() {
		return daySpace;
	}

	public void setDaySpace(Integer daySpace) {
		this.daySpace = daySpace;
	}

	
	@Transient
	public String getDayStr() {
		return dayStr;
	}

	public void setDayStr(String dayStr) {
		this.dayStr = dayStr;
	}

	public AmazonOutOfProduct(String productNameColor, String country,Date dataDate) {
		super();
		this.productNameColor = productNameColor;
		this.country = country;
		this.dataDate = dataDate;
	}
	
	public AmazonOutOfProduct(String productNameColor, String country,Integer daySpace,String dayStr) {
		super();
		this.productNameColor = productNameColor;
		this.country = country;
		this.daySpace = daySpace;
		this.dayStr=dayStr;
	}
	
	
}
