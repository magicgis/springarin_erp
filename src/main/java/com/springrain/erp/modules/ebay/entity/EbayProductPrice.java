package com.springrain.erp.modules.ebay.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ebay_product_price")
public class EbayProductPrice {
	private Integer id;
	private String productName;
	private String country;
	private Float tranFee;
	private Float tranGw;
	private Double safePrice;
	private Date updateDate;
	private Double skyFee;
	private Double seaFee;
	private Float purchasePrice;
	private Float tranPrice;
	
	public EbayProductPrice() {
		super();
	}
	public EbayProductPrice(String productName, String country, Float tranFee,
			Float tranGw, Double safePrice, Date updateDate,Double skyFee,Double seaFee) {
		super();
		this.productName = productName;
		this.country = country;
		this.tranFee = tranFee;
		this.tranGw = tranGw;
		this.safePrice = safePrice;
		this.updateDate = updateDate;
		this.skyFee=skyFee;
		this.seaFee=seaFee;
	}
	
	public EbayProductPrice(String productName, String country, Float tranFee,
			Float tranGw, Double safePrice, Date updateDate,Double skyFee,Double seaFee,Float purchasePrice,Float tranPrice) {
		super();
		this.productName = productName;
		this.country = country;
		this.tranFee = tranFee;
		this.tranGw = tranGw;
		this.safePrice = safePrice;
		this.updateDate = updateDate;
		this.skyFee=skyFee;
		this.seaFee=seaFee;
		this.purchasePrice=purchasePrice;
		this.tranPrice=tranPrice;
	}
	
	public Float getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(Float purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public Float getTranPrice() {
		return tranPrice;
	}
	public void setTranPrice(Float tranPrice) {
		this.tranPrice = tranPrice;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	
	public Double getSkyFee() {
		return skyFee;
	}
	public void setSkyFee(Double skyFee) {
		this.skyFee = skyFee;
	}
	public Double getSeaFee() {
		return seaFee;
	}
	public void setSeaFee(Double seaFee) {
		this.seaFee = seaFee;
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
	public Float getTranFee() {
		return tranFee;
	}
	public void setTranFee(Float tranFee) {
		this.tranFee = tranFee;
	}
	public Float getTranGw() {
		return tranGw;
	}
	public void setTranGw(Float tranGw) {
		this.tranGw = tranGw;
	}
	public Double getSafePrice() {
		return safePrice;
	}
	public void setSafePrice(Double safePrice) {
		this.safePrice = safePrice;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
}
