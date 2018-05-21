package com.springrain.erp.modules.ebay.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "ebay_product_profit")
public class EbayProductProfit {
	private Integer id;
	private String productName;
	private String country;
	private Date day;
	private Integer salesVolume;
	private Float sales;
	private Float salesNoTax;
	private Float transportFee;
	private Float buyCost;
	private Float ebayFee;
	private Float profits;
	private Float price;
	
	private String start;
	private String end;
	private String type;//0 日 1:年
	private String date;
	
	
	@Transient
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Transient
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Transient
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	
	@Transient
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	
	
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	

	public EbayProductProfit() {
		super();
	}
	
	public EbayProductProfit(String productName, String country, Date day,
			Integer salesVolume, Float sales, Float salesNoTax,
			Float transportFee, Float buyCost, Float ebayFee, Float profits,
			Float price) {
		super();
		this.productName = productName;
		this.country = country;
		this.day = day;
		this.salesVolume = salesVolume;
		this.sales = sales;
		this.salesNoTax = salesNoTax;
		this.transportFee = transportFee;
		this.buyCost = buyCost;
		this.ebayFee = ebayFee;
		this.profits = profits;
		this.price = price;
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
	public Date getDay() {
		return day;
	}
	public void setDay(Date day) {
		this.day = day;
	}
	public Integer getSalesVolume() {
		return salesVolume;
	}
	public void setSalesVolume(Integer salesVolume) {
		this.salesVolume = salesVolume;
	}
	public Float getSales() {
		return sales;
	}
	public void setSales(Float sales) {
		this.sales = sales;
	}
	public Float getSalesNoTax() {
		return salesNoTax;
	}
	public void setSalesNoTax(Float salesNoTax) {
		this.salesNoTax = salesNoTax;
	}
	public Float getTransportFee() {
		return transportFee;
	}
	public void setTransportFee(Float transportFee) {
		this.transportFee = transportFee;
	}
	public Float getBuyCost() {
		return buyCost;
	}
	public void setBuyCost(Float buyCost) {
		this.buyCost = buyCost;
	}
	public Float getEbayFee() {
		return ebayFee;
	}
	public void setEbayFee(Float ebayFee) {
		this.ebayFee = ebayFee;
	}
	public Float getProfits() {
		return profits;
	}
	public void setProfits(Float profits) {
		this.profits = profits;
	}
}
