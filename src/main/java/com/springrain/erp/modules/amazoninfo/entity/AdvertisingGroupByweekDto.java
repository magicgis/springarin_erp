package com.springrain.erp.modules.amazoninfo.entity;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 广告报表Entity
 * @author Tim
 * @version 2015-03-03
 */
public class AdvertisingGroupByweekDto {
	
	private String name;
	private String groupName;
	
	private Integer impressions;
	private Integer clicks;
   
	private Float totalSpend;
    
	private Integer weekSameSkuUnitsOrdered;
	private Float weekSameSkuUnitsLirun;
	
	private Integer weekOtherSkuUnitsOrdered;
	
	private Integer weekParentSkuUnitsOrdered;
	private Float weekParentSkuUnitsLirun;
	
	
	private List<AdvertisingByWeek> advertisings = Lists.newArrayList();
	
	public AdvertisingGroupByweekDto() {
		super();
	}
	
	public AdvertisingGroupByweekDto(String name,String groupName) {
		super();
		this.groupName = groupName;
		this.name = name;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public Integer getWeekParentSkuUnitsOrdered() {
		return weekParentSkuUnitsOrdered;
	}

	public void setWeekParentSkuUnitsOrdered(Integer weekParentSkuUnitsOrdered) {
		this.weekParentSkuUnitsOrdered = weekParentSkuUnitsOrdered;
	}

	public Float getWeekParentSkuUnitsLirun() {
		return weekParentSkuUnitsLirun;
	}

	public void setWeekParentSkuUnitsLirun(Float weekParentSkuUnitsLirun) {
		this.weekParentSkuUnitsLirun = weekParentSkuUnitsLirun;
	}

	public List<AdvertisingByWeek> getAdvertisings() {
		return advertisings;
	}

	public void setAdvertisings(List<AdvertisingByWeek> advertisings) {
		this.advertisings = advertisings;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getConversion() {
		if(clicks>0){
			Float temp = (float)getWeekSameSkuUnitsOrdered()*100/(float)clicks;
			BigDecimal bg = new BigDecimal(temp);
		    temp = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		    return temp;
		}
		return 0f;
	}  

	public Float getRoi() {
		if(getTotalSpend()>0){
			Float temp = (getWeekSameSkuUnitsLirun()+getWeekParentSkuUnitsLirun())/getTotalSpend();
			BigDecimal bg = new BigDecimal(temp);
		    temp = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		    return temp;
		}
		return 0f;
	}
}


