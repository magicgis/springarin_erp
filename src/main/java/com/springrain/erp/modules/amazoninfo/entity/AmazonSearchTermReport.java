package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "amazoninfo_search_term_report")
public class AmazonSearchTermReport {

	private Integer id;
	private String country;
	private String campaignName;
	private String adGroupName;
	private String customerSearchTerm;
	private String keyword;
	private String matchType;
	private Date startDate;
	private Date endDate;
	private Integer impressions;
	private Integer clicks;
	private Float ctr;
	private Float totalSpend;
	private Float averageCpc;
	private Float acos;
	private Integer ordersPlaced;
	private Float productSales;
	private Float conversionRate;
	private Integer sameSku;
	private Integer otherSku;
	private Float sameSkuSale;
	private Float otherSkuSale;
	private Date updateTime;
	
    private String accountName;
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public AmazonSearchTermReport() {
		super();
	}
	
	public AmazonSearchTermReport(String country, String campaignName,
			String adGroupName, String customerSearchTerm, String keyword,
			String matchType, Date startDate, Date endDate,
			Integer impressions, Integer clicks, Float ctr, Float totalSpend,
			Float averageCpc, Float acos, Integer ordersPlaced,
			Float productSales, Float conversionRate, Integer sameSku,
			Integer otherSku, Float sameSkuSale, Float otherSkuSale,
			Date updateTime) {
		super();
		this.country = country;
		this.campaignName = campaignName;
		this.adGroupName = adGroupName;
		this.customerSearchTerm = customerSearchTerm;
		this.keyword = keyword;
		this.matchType = matchType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.impressions = impressions;
		this.clicks = clicks;
		this.ctr = ctr;
		this.totalSpend = totalSpend;
		this.averageCpc = averageCpc;
		this.acos = acos;
		this.ordersPlaced = ordersPlaced;
		this.productSales = productSales;
		this.conversionRate = conversionRate;
		this.sameSku = sameSku;
		this.otherSku = otherSku;
		this.sameSkuSale = sameSkuSale;
		this.otherSkuSale = otherSkuSale;
		this.updateTime = updateTime;
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCampaignName() {
		return campaignName;
	}
	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}
	public String getAdGroupName() {
		return adGroupName;
	}
	public void setAdGroupName(String adGroupName) {
		this.adGroupName = adGroupName;
	}
	public String getCustomerSearchTerm() {
		return customerSearchTerm;
	}
	public void setCustomerSearchTerm(String customerSearchTerm) {
		this.customerSearchTerm = customerSearchTerm;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getMatchType() {
		return matchType;
	}
	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Integer getImpressions() {
		return impressions;
	}
	public void setImpressions(Integer impressions) {
		this.impressions = impressions;
	}
	public Integer getClicks() {
		return clicks;
	}
	public void setClicks(Integer clicks) {
		this.clicks = clicks;
	}
	public Float getCtr() {
		return ctr;
	}
	public void setCtr(Float ctr) {
		this.ctr = ctr;
	}
	public Float getTotalSpend() {
		return totalSpend;
	}
	public void setTotalSpend(Float totalSpend) {
		this.totalSpend = totalSpend;
	}
	public Float getAverageCpc() {
		return averageCpc;
	}
	public void setAverageCpc(Float averageCpc) {
		this.averageCpc = averageCpc;
	}
	public Float getAcos() {
		return acos;
	}
	public void setAcos(Float acos) {
		this.acos = acos;
	}
	public Integer getOrdersPlaced() {
		return ordersPlaced;
	}
	public void setOrdersPlaced(Integer ordersPlaced) {
		this.ordersPlaced = ordersPlaced;
	}
	public Float getProductSales() {
		return productSales;
	}
	public void setProductSales(Float productSales) {
		this.productSales = productSales;
	}
	public Float getConversionRate() {
		return conversionRate;
	}
	public void setConversionRate(Float conversionRate) {
		this.conversionRate = conversionRate;
	}
	public Integer getSameSku() {
		return sameSku;
	}
	public void setSameSku(Integer sameSku) {
		this.sameSku = sameSku;
	}
	public Integer getOtherSku() {
		return otherSku;
	}
	public void setOtherSku(Integer otherSku) {
		this.otherSku = otherSku;
	}
	public Float getSameSkuSale() {
		return sameSkuSale;
	}
	public void setSameSkuSale(Float sameSkuSale) {
		this.sameSkuSale = sameSkuSale;
	}
	public Float getOtherSkuSale() {
		return otherSkuSale;
	}
	public void setOtherSkuSale(Float otherSkuSale) {
		this.otherSkuSale = otherSkuSale;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	
	
	
}
