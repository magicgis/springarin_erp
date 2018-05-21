package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "amazoninfo_facebook_report")
public class AmazonFacebookReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id; 
	private String market;
	private Date dateShipped;
	private String productLine;
    private String itemName;
	private String asin;
	private String seller;
	private String trackingId;
	private Double price;
	private Double advertisingFeeRate;
    private long itemsShipped;
    private Double revenue;
    private Double advertisingFees;
    private String deviceType;
	private String delFlag;
	
	private Date endDate;
    private Double profit;	//亚马逊返点+inateck产品销售利润(已去除增值税和亚马逊佣金)
	
	@Transient
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public AmazonFacebookReport() {
		super();
	}

	public AmazonFacebookReport(String market, Date dateShipped,
			String productLine, String itemName, String asin, String seller,
			String trackingId, Double price, Double advertisingFeeRate,
			long itemsShipped, Double revenue, Double advertisingFees,
			String deviceType, String delFlag) {
		super();
		this.market = market;
		this.dateShipped = dateShipped;
		this.productLine = productLine;
		this.itemName = itemName;
		this.asin = asin;
		this.seller = seller;
		this.trackingId = trackingId;
		this.price = price;
		this.advertisingFeeRate = advertisingFeeRate;
		this.itemsShipped = itemsShipped;
		this.revenue = revenue;
		this.advertisingFees = advertisingFees;
		this.deviceType = deviceType;
		this.delFlag = delFlag;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getDateShipped() {
		return dateShipped;
	}

	public void setDateShipped(Date dateShipped) {
		this.dateShipped = dateShipped;
	}
	
	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getProductLine() {
		return productLine;
	}

	public void setProductLine(String productLine) {
		this.productLine = productLine;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getAdvertisingFeeRate() {
		return advertisingFeeRate;
	}

	public void setAdvertisingFeeRate(Double advertisingFeeRate) {
		this.advertisingFeeRate = advertisingFeeRate;
	}

	public long getItemsShipped() {
		return itemsShipped;
	}

	public void setItemsShipped(long itemsShipped) {
		this.itemsShipped = itemsShipped;
	}

	public Double getRevenue() {
		return revenue;
	}

	public void setRevenue(Double revenue) {
		this.revenue = revenue;
	}

	public Double getAdvertisingFees() {
		return advertisingFees;
	}

	public void setAdvertisingFees(Double advertisingFees) {
		this.advertisingFees = advertisingFees;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public Double getProfit() {
		return profit;
	}

	public void setProfit(Double profit) {
		this.profit = profit;
	}

}


