package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 广告报表Entity
 * @author Tim
 * @version 2015-03-03
 */
@Entity
@Table(name = "amazoninfo_advertising")
public class Advertising implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id; 		
	private String country; 	
	private String name;
	private String groupName;
	private String sku;
	private String keyword;
	private String type;
	
	private Date dataDate;
	private Date createDate;
	
	private Integer impressions;
	private Integer clicks;
   
	private Float totalSpend;
	private Float conversion;
    
	private Integer sameSkuOrdersPlaced;
	private Float sameSkuOrderSales;
	
	private Integer weekSameSkuUnitsOrdered;
	private Float weekSameSkuUnitsSales;
	private Integer monthSameSkuUnitsOrdered;
	
	private Integer otherSkuOrdersPlaced;
	private Float otherSkuOrderSales;
	
	private Float maxCpcBid;
	
	private Float onePageBid;

	private String searchFlag ="0";   //0：按日期       1： 按星期       2： 按月份统计
	
	
	private String nameStatus;
	private String groupNameStatus;
	private String keywordStatus;
	
	
    private String accountName;
	
	
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	
	public String getNameStatus() {
		return nameStatus;
	}

	public void setNameStatus(String nameStatus) {
		this.nameStatus = nameStatus;
	}

	public String getGroupNameStatus() {
		return groupNameStatus;
	}

	public void setGroupNameStatus(String groupNameStatus) {
		this.groupNameStatus = groupNameStatus;
	}

	public String getKeywordStatus() {
		return keywordStatus;
	}

	public void setKeywordStatus(String keywordStatus) {
		this.keywordStatus = keywordStatus;
	}

	public Advertising() {
		super();
	}
	
	public Advertising(String country, String name, String groupName,
			String sku, String keyword, Date dataDate, Date createDate,
			Integer impressions, Integer clicks, Float totalSpend,
			Float conversion, Integer sameSkuOrdersPlaced,
			Float sameSkuOrderSales, Integer otherSkuOrdersPlaced,
			Float otherSkuOrderSales,Float maxCpcBid,Float onePageBid,String type,Integer weekSameSkuUnitsOrdered,Integer monthSameSkuUnitsOrdered,Float weekSameSkuUnitsSales) {
		super();
		this.country = country;
		this.name = name;
		this.groupName = groupName;
		this.sku = sku;
		this.keyword = keyword;
		this.dataDate = dataDate;
		this.createDate = createDate;
		this.impressions = impressions;
		this.clicks = clicks;
		this.totalSpend = totalSpend;
		this.conversion = conversion;
		this.sameSkuOrdersPlaced = sameSkuOrdersPlaced;
		this.sameSkuOrderSales = sameSkuOrderSales;
		this.otherSkuOrdersPlaced = otherSkuOrdersPlaced;
		this.otherSkuOrderSales = otherSkuOrderSales;
		this.maxCpcBid = maxCpcBid;
		this.onePageBid = onePageBid;
		this.type = type;
		this.weekSameSkuUnitsOrdered = weekSameSkuUnitsOrdered;
		this.monthSameSkuUnitsOrdered = monthSameSkuUnitsOrdered;
		this.weekSameSkuUnitsSales = weekSameSkuUnitsSales;
	}
	
	public Advertising(String name, String groupName,
			String sku, String keyword,
			Integer impressions, Integer clicks, Float totalSpend,
			Float conversion, Integer sameSkuOrdersPlaced,
			Float sameSkuOrderSales, Integer otherSkuOrdersPlaced,
			Float otherSkuOrderSales,Float maxCpcBid,Float onePageBid,String type,Integer weekSameSkuUnitsOrdered,Integer monthSameSkuUnitsOrdered,Float weekSameSkuUnitsSales) {
		super();
		this.name = name;
		this.groupName = groupName;
		this.sku = sku;
		this.keyword = keyword;
		this.impressions = impressions;
		this.clicks = clicks;
		this.totalSpend = totalSpend;
		this.conversion = conversion;
		this.sameSkuOrdersPlaced = sameSkuOrdersPlaced;
		this.sameSkuOrderSales = sameSkuOrderSales;
		this.otherSkuOrdersPlaced = otherSkuOrdersPlaced;
		this.otherSkuOrderSales = otherSkuOrderSales;
		this.maxCpcBid = maxCpcBid;
		this.onePageBid = onePageBid;
		this.type = type;
		this.weekSameSkuUnitsOrdered = weekSameSkuUnitsOrdered;
		this.monthSameSkuUnitsOrdered = monthSameSkuUnitsOrdered;
		this.weekSameSkuUnitsSales = weekSameSkuUnitsSales;
	}
	
	public Float getWeekSameSkuUnitsSales() {
		return weekSameSkuUnitsSales;
	}

	public void setWeekSameSkuUnitsSales(Float weekSameSkuUnitsSales) {
		this.weekSameSkuUnitsSales = weekSameSkuUnitsSales;
	}

	public Integer getWeekSameSkuUnitsOrdered() {
		return weekSameSkuUnitsOrdered;
	}

	public void setWeekSameSkuUnitsOrdered(Integer weekSameSkuUnitsOrdered) {
		this.weekSameSkuUnitsOrdered = weekSameSkuUnitsOrdered;
	}

	public Integer getMonthSameSkuUnitsOrdered() {
		return monthSameSkuUnitsOrdered;
	}

	public void setMonthSameSkuUnitsOrdered(Integer monthSameSkuUnitsOrdered) {
		this.monthSameSkuUnitsOrdered = monthSameSkuUnitsOrdered;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Column(updatable=false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(updatable=false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(updatable=false)
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(updatable=false)
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Column(updatable=false)
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	@Column(updatable=false)
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Column(updatable=false)
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public Float getTotalSpend() {
		return totalSpend;
	}

	public void setTotalSpend(Float totalSpend) {
		this.totalSpend = totalSpend;
	}

	public Float getConversion() {
		return conversion;
	}

	public void setConversion(Float conversion) {
		this.conversion = conversion;
	}

	public Integer getSameSkuOrdersPlaced() {
		return sameSkuOrdersPlaced;
	}

	public void setSameSkuOrdersPlaced(Integer sameSkuOrdersPlaced) {
		this.sameSkuOrdersPlaced = sameSkuOrdersPlaced;
	}

	public Float getSameSkuOrderSales() {
		return sameSkuOrderSales;
	}

	public void setSameSkuOrderSales(Float sameSkuOrderSales) {
		this.sameSkuOrderSales = sameSkuOrderSales;
	}

	public Integer getOtherSkuOrdersPlaced() {
		return otherSkuOrdersPlaced;
	}

	public void setOtherSkuOrdersPlaced(Integer otherSkuOrdersPlaced) {
		this.otherSkuOrdersPlaced = otherSkuOrdersPlaced;
	}

	
	public Float getOtherSkuOrderSales() {
		return otherSkuOrderSales;
	}

	public void setOtherSkuOrderSales(Float otherSkuOrderSales) {
		this.otherSkuOrderSales = otherSkuOrderSales;
	}

	@Column(updatable=false)
	public Float getMaxCpcBid() {
		return maxCpcBid;
	}

	public void setMaxCpcBid(Float maxCpcBid) {
		this.maxCpcBid = maxCpcBid;
	}

	@Column(updatable=false)
	public Float getOnePageBid() {
		return onePageBid;
	}

	public void setOnePageBid(Float onePageBid) {
		this.onePageBid = onePageBid;
	}
	
	
	@Transient
	public Float getAverageCPC() {
		if(clicks>0){
			Float temp = getTotalSpend()/clicks;
			BigDecimal bg = new BigDecimal(temp);
		    temp = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			return temp;
		}
		return 0f;
	}

	@Transient
	public Integer getOrdersPlaced() {
		return otherSkuOrdersPlaced+sameSkuOrdersPlaced;
	}

	@Transient
	public Float getOrderSales() {
		return otherSkuOrderSales+sameSkuOrderSales;
	}

	@Transient
	public Float getCtr() {
		if(impressions>0){
			Float temp = (float)clicks*100/(float)impressions;
			BigDecimal bg = new BigDecimal(temp);
		    temp = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		    return temp;
		}
		return 0f;
	}
	
	@Transient
	public Float getAcos() {
		if(getOrderSales()>0){
			Float temp = (float)getTotalSpend()*100/getOrderSales();
			BigDecimal bg = new BigDecimal(temp);
		    temp = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		    return temp;
		}
		return 0f;
	}

	@Transient
	public String getSearchFlag() {
		return searchFlag;
	}
	
	public void setSearchFlag(String searchFlag) {
		this.searchFlag = searchFlag;
	}
}


