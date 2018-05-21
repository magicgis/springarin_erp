package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "amazoninfo_searchterms_report")
public class AdvertisingSearchTermReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id; 	
	private String country;
	private String accountName;
	private Date dataDate;
	private String currency;
	private String campaignName;
	private String adGroupName;
	private String customerSearchTerm;
	private String keyword;
	private String matchType;
	private Integer impressions;
	private Integer clicks;
	private Float ctr;
	private Float cpc;
	private Float spend;
	private Float dayTotalSales;
	private Float acos;
	private Float roas;
	private Integer dayTotalOrders;
	private Integer dayTotalUnits;
	private Float dayConversionRate;
	private Integer dayAdvertisedSkuUnits;
	private Integer dayOtherSkuUnits;
	private Float dayAdvertisedSkuSales;
	private Float dayOtherSkuSales;
	private Date updateTime;
	
	
	@Transient
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public AdvertisingSearchTermReport(String country, String accountName,
			Date dataDate, String currency, String campaignName,
			String adGroupName, String customerSearchTerm, String keyword,
			String matchType, Integer impressions, Integer clicks, Float ctr,
			Float cpc, Float spend, Float dayTotalSales, Float acos,
			Float roas, Integer dayTotalOrders, Integer dayTotalUnits,
			Float dayConversionRate, Integer dayAdvertisedSkuUnits,
			Integer dayOtherSkuUnits, Float dayAdvertisedSkuSales,
			Float dayOtherSkuSales) {
		super();
		this.country = country;
		this.accountName = accountName;
		this.dataDate = dataDate;
		this.currency = currency;
		this.campaignName = campaignName;
		this.adGroupName = adGroupName;
		this.customerSearchTerm = customerSearchTerm;
		this.keyword = keyword;
		this.matchType = matchType;
		this.impressions = impressions;
		this.clicks = clicks;
		this.ctr = ctr;
		this.cpc = cpc;
		this.spend = spend;
		this.dayTotalSales = dayTotalSales;
		this.acos = acos;
		this.roas = roas;
		this.dayTotalOrders = dayTotalOrders;
		this.dayTotalUnits = dayTotalUnits;
		this.dayConversionRate = dayConversionRate;
		this.dayAdvertisedSkuUnits = dayAdvertisedSkuUnits;
		this.dayOtherSkuUnits = dayOtherSkuUnits;
		this.dayAdvertisedSkuSales = dayAdvertisedSkuSales;
		this.dayOtherSkuSales = dayOtherSkuSales;
	}

	public AdvertisingSearchTermReport() {
		super();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public Float getCpc() {
		return cpc;
	}

	public void setCpc(Float cpc) {
		this.cpc = cpc;
	}

	public Float getSpend() {
		return spend;
	}

	public void setSpend(Float spend) {
		this.spend = spend;
	}

	public Float getDayTotalSales() {
		return dayTotalSales;
	}

	public void setDayTotalSales(Float dayTotalSales) {
		this.dayTotalSales = dayTotalSales;
	}

	public Float getAcos() {
		return acos;
	}

	public void setAcos(Float acos) {
		this.acos = acos;
	}

	public Float getRoas() {
		return roas;
	}

	public void setRoas(Float roas) {
		this.roas = roas;
	}

	public Integer getDayTotalOrders() {
		return dayTotalOrders;
	}

	public void setDayTotalOrders(Integer dayTotalOrders) {
		this.dayTotalOrders = dayTotalOrders;
	}

	public Integer getDayTotalUnits() {
		return dayTotalUnits;
	}

	public void setDayTotalUnits(Integer dayTotalUnits) {
		this.dayTotalUnits = dayTotalUnits;
	}

	public Float getDayConversionRate() {
		return dayConversionRate;
	}

	public void setDayConversionRate(Float dayConversionRate) {
		this.dayConversionRate = dayConversionRate;
	}

	public Integer getDayAdvertisedSkuUnits() {
		return dayAdvertisedSkuUnits;
	}

	public void setDayAdvertisedSkuUnits(Integer dayAdvertisedSkuUnits) {
		this.dayAdvertisedSkuUnits = dayAdvertisedSkuUnits;
	}

	public Integer getDayOtherSkuUnits() {
		return dayOtherSkuUnits;
	}

	public void setDayOtherSkuUnits(Integer dayOtherSkuUnits) {
		this.dayOtherSkuUnits = dayOtherSkuUnits;
	}

	public Float getDayAdvertisedSkuSales() {
		return dayAdvertisedSkuSales;
	}

	public void setDayAdvertisedSkuSales(Float dayAdvertisedSkuSales) {
		this.dayAdvertisedSkuSales = dayAdvertisedSkuSales;
	}

	public Float getDayOtherSkuSales() {
		return dayOtherSkuSales;
	}

	public void setDayOtherSkuSales(Float dayOtherSkuSales) {
		this.dayOtherSkuSales = dayOtherSkuSales;
	}
		
}


