package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 广告报表Entity
 * @author Tim
 * @version 2015-03-03
 */
@Entity
@Table(name = "amazoninfo_advertising_week")
public class AdvertisingByWeek implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id; 	
	
	private String country; 	
	private String name;
	private String groupName;
	private String sku;
	private String keyword;
	private String type;
	private String week;
	
	private Date updateDate;
	
	private Integer impressions;
	private Integer clicks;
  
	private Float totalSpend;
    
	private Integer weekSameSkuUnitsOrdered;
	private Float weekSameSkuUnitsSales;
	private Float weekSameSkuUnitsLirun;
	
	private Integer weekOtherSkuUnitsOrdered;
	private Float weekOtherSkuUnitsSales;
	private Float weekOtherSkuUnitsLirun;
	
	private Integer weekParentSkuUnitsOrdered;
	private Float weekParentSkuUnitsSales;
	private Float weekParentSkuUnitsLirun;
	
	private String startWeek;
	private String endWeek;
	
    private String accountName;
	
	
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	
	@Transient
	public String getStartWeek() {
		return startWeek;
	}

	public void setStartWeek(String startWeek) {
		this.startWeek = startWeek;
	}

	@Transient
	public String getEndWeek() {
		return endWeek;
	}

	public void setEndWeek(String endWeek) {
		this.endWeek = endWeek;
	}

	public AdvertisingByWeek() {
		super();
	}
	
	public AdvertisingByWeek(String name, String groupName,
			String sku, String keyword, String type,
			Integer impressions, Integer clicks,
			Float totalSpend, Integer weekSameSkuUnitsOrdered,
			Float weekSameSkuUnitsSales, Float weekSameSkuUnitsLirun,
			Integer weekOtherSkuUnitsOrdered, Float weekOtherSkuUnitsSales,
			Float weekOtherSkuUnitsLirun, Integer weekParentSkuUnitsOrdered,
			Float weekParentSkuUnitsSales, Float weekParentSkuUnitsLirun) {
		super();
		this.name = name;
		this.groupName = groupName;
		this.sku = sku;
		this.keyword = keyword;
		this.type = type;
		this.impressions = impressions;
		this.clicks = clicks;
		this.totalSpend = totalSpend;
		this.weekSameSkuUnitsOrdered = weekSameSkuUnitsOrdered;
		this.weekSameSkuUnitsSales = weekSameSkuUnitsSales;
		this.weekSameSkuUnitsLirun = weekSameSkuUnitsLirun;
		this.weekOtherSkuUnitsOrdered = weekOtherSkuUnitsOrdered;
		this.weekOtherSkuUnitsSales = weekOtherSkuUnitsSales;
		this.weekOtherSkuUnitsLirun = weekOtherSkuUnitsLirun;
		this.weekParentSkuUnitsOrdered = weekParentSkuUnitsOrdered;
		this.weekParentSkuUnitsSales = weekParentSkuUnitsSales;
		this.weekParentSkuUnitsLirun = weekParentSkuUnitsLirun;
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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getGroupName() {
		return groupName;
	}


	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public String getSku() {
		return sku;
	}


	public void setSku(String sku) {
		this.sku = sku;
	}


	public String getKeyword() {
		return keyword;
	}


	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getWeek() {
		return week;
	}


	public void setWeek(String week) {
		this.week = week;
	}


	public Date getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
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


	public Integer getWeekSameSkuUnitsOrdered() {
		return weekSameSkuUnitsOrdered;
	}


	public void setWeekSameSkuUnitsOrdered(Integer weekSameSkuUnitsOrdered) {
		this.weekSameSkuUnitsOrdered = weekSameSkuUnitsOrdered;
	}


	public Float getWeekSameSkuUnitsSales() {
		return weekSameSkuUnitsSales;
	}


	public void setWeekSameSkuUnitsSales(Float weekSameSkuUnitsSales) {
		this.weekSameSkuUnitsSales = weekSameSkuUnitsSales;
	}


	public Float getWeekSameSkuUnitsLirun() {
		return weekSameSkuUnitsLirun;
	}


	public void setWeekSameSkuUnitsLirun(Float weekSameSkuUnitsLirun) {
		this.weekSameSkuUnitsLirun = weekSameSkuUnitsLirun;
	}


	public Integer getWeekOtherSkuUnitsOrdered() {
		return weekOtherSkuUnitsOrdered;
	}


	public void setWeekOtherSkuUnitsOrdered(Integer weekOtherSkuUnitsOrdered) {
		this.weekOtherSkuUnitsOrdered = weekOtherSkuUnitsOrdered;
	}


	public Float getWeekOtherSkuUnitsSales() {
		return weekOtherSkuUnitsSales;
	}


	public void setWeekOtherSkuUnitsSales(Float weekOtherSkuUnitsSales) {
		this.weekOtherSkuUnitsSales = weekOtherSkuUnitsSales;
	}


	public Float getWeekOtherSkuUnitsLirun() {
		return weekOtherSkuUnitsLirun;
	}


	public void setWeekOtherSkuUnitsLirun(Float weekOtherSkuUnitsLirun) {
		this.weekOtherSkuUnitsLirun = weekOtherSkuUnitsLirun;
	}


	public Integer getWeekParentSkuUnitsOrdered() {
		return weekParentSkuUnitsOrdered;
	}


	public void setWeekParentSkuUnitsOrdered(Integer weekParentSkuUnitsOrdered) {
		this.weekParentSkuUnitsOrdered = weekParentSkuUnitsOrdered;
	}


	public Float getWeekParentSkuUnitsSales() {
		return weekParentSkuUnitsSales;
	}


	public void setWeekParentSkuUnitsSales(Float weekParentSkuUnitsSales) {
		this.weekParentSkuUnitsSales = weekParentSkuUnitsSales;
	}


	public Float getWeekParentSkuUnitsLirun() {
		return weekParentSkuUnitsLirun;
	}


	public void setWeekParentSkuUnitsLirun(Float weekParentSkuUnitsLirun) {
		this.weekParentSkuUnitsLirun = weekParentSkuUnitsLirun;
	}
	
	@Transient
	public Float getRoi() {
		if(getTotalSpend()>0){
			Float temp = (getWeekSameSkuUnitsLirun()+getWeekParentSkuUnitsLirun())/getTotalSpend();
			BigDecimal bg = new BigDecimal(temp);
		    temp = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		    return temp;
		}
		return 0f;
	}
	
	@Transient
	public Float getConversion() {
		if(clicks>0){
			Float temp = (float)getWeekSameSkuUnitsOrdered()*100/(float)clicks;
			BigDecimal bg = new BigDecimal(temp);
		    temp = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		    return temp;
		}
		return 0f;
	}  
	
}


