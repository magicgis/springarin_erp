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
@Table(name = "psi_product_avg_price")
public class PsiProductAvgPrice {
	private Integer id;
	
	private String productName;
	
	private String country;
	
	private Float avgPrice;
	
	private Date updateDate;
	
	private Integer stock;
	
	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}



	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
	}

	public PsiProductAvgPrice() {
		super();
	}

	public Float getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(Float avgPrice) {
		this.avgPrice = avgPrice;
	}

	public PsiProductAvgPrice(String productName, String country,
			Float avgPrice, Date updateDate) {
		super();
		this.productName = productName;
		this.country = country;
		this.avgPrice = avgPrice;
		this.updateDate = updateDate;
	}
	
	public PsiProductAvgPrice(String productName, String country,
			Float avgPrice, Date updateDate,Integer stock) {
		super();
		this.productName = productName;
		this.country = country;
		this.avgPrice = avgPrice;
		this.updateDate = updateDate;
		this.stock = stock;
	}

}
