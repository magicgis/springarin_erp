package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "amazoninfo_price")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Price implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Integer id; // 编号
	
	private String sku;
	
	private Float price;
	
	private Float salePrice;
	
	private Date saleStartDate;
	
	private Date saleEndDate;
	
	private Float businessPrice;
	
	private Float quantityPrice1;
	
	private Integer quantityLowerBound1;
	
	private Float quantityPrice2;
	
	private Integer quantityLowerBound2;
	
	private Float quantityPrice3;
	
	private Integer quantityLowerBound3;
	
	private Float quantityPrice4;
	
	private Integer quantityLowerBound4;
	
	private Float quantityPrice5;
	
	private Integer quantityLowerBound5;
	
	private String deleteB2b;
	
	
	private PriceFeed priceFeed;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="feed_price_feed_id")
	public PriceFeed getPriceFeed() {
		return priceFeed;
	}

	public void setPriceFeed(PriceFeed priceFeed) {
		this.priceFeed = priceFeed;
	}

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

	public Float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Float salePrice) {
		this.salePrice = salePrice;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getSaleStartDate() {
		return saleStartDate;
	}

	public void setSaleStartDate(Date saleStartDate) {
		this.saleStartDate = saleStartDate;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getSaleEndDate() {
		return saleEndDate;
	}

	public void setSaleEndDate(Date saleEndDate) {
		this.saleEndDate = saleEndDate;
	}
	
	public Float getBusinessPrice() {
		return businessPrice;
	}

	public void setBusinessPrice(Float businessPrice) {
		this.businessPrice = businessPrice;
	}

	public Float getQuantityPrice1() {
		return quantityPrice1;
	}

	public void setQuantityPrice1(Float quantityPrice1) {
		this.quantityPrice1 = quantityPrice1;
	}

	public Integer getQuantityLowerBound1() {
		return quantityLowerBound1;
	}

	public void setQuantityLowerBound1(Integer quantityLowerBound1) {
		this.quantityLowerBound1 = quantityLowerBound1;
	}

	public Float getQuantityPrice2() {
		return quantityPrice2;
	}

	public void setQuantityPrice2(Float quantityPrice2) {
		this.quantityPrice2 = quantityPrice2;
	}

	public Integer getQuantityLowerBound2() {
		return quantityLowerBound2;
	}

	public void setQuantityLowerBound2(Integer quantityLowerBound2) {
		this.quantityLowerBound2 = quantityLowerBound2;
	}

	public Float getQuantityPrice3() {
		return quantityPrice3;
	}

	public void setQuantityPrice3(Float quantityPrice3) {
		this.quantityPrice3 = quantityPrice3;
	}

	public Integer getQuantityLowerBound3() {
		return quantityLowerBound3;
	}

	public void setQuantityLowerBound3(Integer quantityLowerBound3) {
		this.quantityLowerBound3 = quantityLowerBound3;
	}

	public Float getQuantityPrice4() {
		return quantityPrice4;
	}

	public void setQuantityPrice4(Float quantityPrice4) {
		this.quantityPrice4 = quantityPrice4;
	}

	public Integer getQuantityLowerBound4() {
		return quantityLowerBound4;
	}

	public void setQuantityLowerBound4(Integer quantityLowerBound4) {
		this.quantityLowerBound4 = quantityLowerBound4;
	}

	public Float getQuantityPrice5() {
		return quantityPrice5;
	}

	public void setQuantityPrice5(Float quantityPrice5) {
		this.quantityPrice5 = quantityPrice5;
	}

	public Integer getQuantityLowerBound5() {
		return quantityLowerBound5;
	}

	public void setQuantityLowerBound5(Integer quantityLowerBound5) {
		this.quantityLowerBound5 = quantityLowerBound5;
	}

	@Column(name="delete_b2b")
	public String getDeleteB2b() {
		return deleteB2b;
	}

	public void setDeleteB2b(String deleteB2b) {
		this.deleteB2b = deleteB2b;
	}

	@Transient
	public String getCountryTips(){
		String country="";
		if(sku.contains("@")){
			int i= sku.indexOf("@");
			country=sku.substring(0, i).toUpperCase();
		}
		return country;
	}
}
