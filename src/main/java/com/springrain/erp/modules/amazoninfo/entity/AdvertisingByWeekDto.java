package com.springrain.erp.modules.amazoninfo.entity;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 广告报表Entity
 * @author Tim
 * @version 2015-03-03
 */
public class AdvertisingByWeekDto {
	
	private String name;
	
	private Integer impressions;
	private Integer clicks;
   
	private Float totalSpend;
 
	private Integer weekSameSkuUnitsOrdered;
	private Float weekSameSkuUnitsLirun;
	
	private Integer weekOtherSkuUnitsOrdered;
	
	private Integer weekParentSkuUnitsOrdered;
	private Float weekParentSkuUnitsLirun;
	
	private List<AdvertisingGroupByweekDto> groups = Lists.newArrayList();
	
	
	public AdvertisingByWeekDto() {
		super();
	}

	public AdvertisingByWeekDto(String name) {
		super();
		this.name = name;
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

	public List<AdvertisingGroupByweekDto> getGroups() {
		return groups;
	}

	public void setGroups(List<AdvertisingGroupByweekDto> groups) {
		this.groups = groups;
	}

	public Float getAverageCPC() {
		if(clicks>0){
			Float temp = getTotalSpend()/clicks;
			BigDecimal bg = new BigDecimal(temp);
		    temp = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			return temp;
		}
		return 0f;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getCtr() {
		if(impressions>0){
			Float temp = (float)clicks*100/(float)impressions;
			BigDecimal bg = new BigDecimal(temp);
		    temp = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		    return temp;
		}
		return 0f;
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
	
	
	public void initData(){
		impressions=0;
		clicks=0;
		totalSpend=0f;
		Integer weekSameSkuUnitsOrdered = 0;
		Float weekSameSkuUnitsLirun = 0f;
		Integer weekOtherSkuUnitsOrdered = 0;
		Integer weekParentSkuUnitsOrdered = 0;
		Float weekParentSkuUnitsLirun = 0f;
		for (AdvertisingGroupByweekDto group : groups) {
			int impressions1=0;
			int clicks1=0;
			float totalSpend1=0f;
			Integer weekSameSkuUnitsOrdered1 = 0;
			Float weekSameSkuUnitsLirun1 = 0f;
			Integer weekOtherSkuUnitsOrdered1 = 0;
			Integer weekParentSkuUnitsOrdered1 = 0;
			Float weekParentSkuUnitsLirun1 = 0f;
			for (AdvertisingByWeek advertising : group.getAdvertisings()) {
				impressions1=impressions1+advertising.getImpressions();
				clicks1=clicks1+advertising.getClicks();
				totalSpend1=totalSpend1+advertising.getTotalSpend();
				weekSameSkuUnitsOrdered1 = weekSameSkuUnitsOrdered1+advertising.getWeekSameSkuUnitsOrdered();
				weekSameSkuUnitsLirun1 = weekSameSkuUnitsLirun1+advertising.getWeekSameSkuUnitsLirun();
				weekOtherSkuUnitsOrdered1 = weekOtherSkuUnitsOrdered1+advertising.getWeekOtherSkuUnitsOrdered();
				weekParentSkuUnitsOrdered1 = weekParentSkuUnitsOrdered1+advertising.getWeekParentSkuUnitsOrdered();
				weekParentSkuUnitsLirun1 = weekParentSkuUnitsLirun1+advertising.getWeekParentSkuUnitsLirun();
			}
			group.setClicks(clicks1);
			group.setImpressions(impressions1);
			group.setTotalSpend(totalSpend1);
			group.setWeekOtherSkuUnitsOrdered(weekOtherSkuUnitsOrdered1);
			group.setWeekParentSkuUnitsLirun(weekParentSkuUnitsLirun1);
			group.setWeekParentSkuUnitsOrdered(weekParentSkuUnitsOrdered1);
			group.setWeekSameSkuUnitsLirun(weekSameSkuUnitsLirun1);
			group.setWeekSameSkuUnitsOrdered(weekSameSkuUnitsOrdered1);
			
			impressions=impressions1+impressions;
			clicks=clicks1+clicks;
			totalSpend=totalSpend1+totalSpend;
			weekSameSkuUnitsOrdered = weekSameSkuUnitsOrdered1+weekSameSkuUnitsOrdered;
			weekSameSkuUnitsLirun = weekSameSkuUnitsLirun1+weekSameSkuUnitsLirun;
			weekOtherSkuUnitsOrdered = weekOtherSkuUnitsOrdered1+weekOtherSkuUnitsOrdered;
			weekParentSkuUnitsOrdered = weekParentSkuUnitsOrdered1+weekParentSkuUnitsOrdered;
			weekParentSkuUnitsLirun = weekParentSkuUnitsLirun1+weekParentSkuUnitsLirun;
		}
		this.setClicks(clicks);
		this.setImpressions(impressions);
		this.setTotalSpend(totalSpend);
		this.setWeekOtherSkuUnitsOrdered(weekOtherSkuUnitsOrdered);
		this.setWeekParentSkuUnitsLirun(weekParentSkuUnitsLirun);
		this.setWeekParentSkuUnitsOrdered(weekParentSkuUnitsOrdered);
		this.setWeekSameSkuUnitsLirun(weekSameSkuUnitsLirun);
		this.setWeekSameSkuUnitsOrdered(weekSameSkuUnitsOrdered);
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


