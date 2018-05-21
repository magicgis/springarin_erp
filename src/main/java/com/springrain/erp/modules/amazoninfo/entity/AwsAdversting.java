package com.springrain.erp.modules.amazoninfo.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "amazoninfo_aws_adversting")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class AwsAdversting {  
	
	private Integer     id;
	private String 		status;         //状态
	private String 		country;        //平台
	private String 		campaignName;   //广告名
	private String 		productName;   //产品名
	private String 		campaignType;   //广告类型
	private String 		campaignId;     //广告Id
	private Date   		startDate ;
	private Date   		endDate ;
	private Date   		dataDate ;
	private Integer     impressions;
	private Integer     clicks;
	private BigDecimal  ctr;
	private Integer     dpv;
	private BigDecimal  spend;
	private BigDecimal  acpc;
	private Integer  	unitsSold;
	private BigDecimal  totalSales;
	private BigDecimal  acos;
	
	private String accountName;
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Integer getImpressions() {
		return impressions;
	}
	public void setImpressions(Integer impressions) {
		this.impressions = impressions;
	}
	public String getCampaignName() {
		return campaignName;
	}
	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
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
	public Date getDataDate() {
		return dataDate;
	}
	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}
	public Integer getClicks() {
		return clicks;
	}
	public void setClicks(Integer clicks) {
		this.clicks = clicks;
	}
	public BigDecimal getCtr() {
		return ctr;
	}
	public void setCtr(BigDecimal ctr) {
		this.ctr = ctr;
	}
	public Integer getDpv() {
		return dpv;
	}
	public void setDpv(Integer dpv) {
		this.dpv = dpv;
	}
	public BigDecimal getSpend() {
		return spend;
	}
	public void setSpend(BigDecimal spend) {
		this.spend = spend;
	}
	public BigDecimal getAcpc() {
		return acpc;
	}
	public void setAcpc(BigDecimal acpc) {
		this.acpc = acpc;
	}
	public Integer getUnitsSold() {
		return unitsSold;
	}
	public void setUnitsSold(Integer unitsSold) {
		this.unitsSold = unitsSold;
	}
	public BigDecimal getTotalSales() {
		return totalSales;
	}
	public void setTotalSales(BigDecimal totalSales) {
		this.totalSales = totalSales;
	}
	public BigDecimal getAcos() {
		return acos;
	}
	public void setAcos(BigDecimal acos) {
		this.acos = acos;
	}
	public String getCampaignType() {
		return campaignType;
	}
	public void setCampaignType(String campaignType) {
		this.campaignType = campaignType;
	}
	public String getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	public AwsAdversting(String status, String campaignName,
			String campaignType, String campaignId, Date startDate,
			Date endDate, Date dataDate, Integer impressions,Integer clicks, BigDecimal ctr,
			Integer dpv, BigDecimal spend, BigDecimal acpc, Integer unitsSold,
			BigDecimal totalSales, BigDecimal acos,String country) {
		super();
		this.status = status;
		this.campaignName = campaignName;
		this.campaignType = campaignType;
		this.campaignId = campaignId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.dataDate = dataDate;
		this.impressions=impressions;
		this.clicks = clicks;
		this.ctr = ctr;
		this.dpv = dpv;
		this.spend = spend;
		this.acpc = acpc;
		this.unitsSold = unitsSold;
		this.totalSales = totalSales;
		this.acos = acos;
		this.country=country;
	}
	public AwsAdversting() {
		super();
	}
	
}
