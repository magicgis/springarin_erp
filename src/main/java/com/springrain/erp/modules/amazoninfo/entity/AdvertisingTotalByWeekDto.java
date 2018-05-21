package com.springrain.erp.modules.amazoninfo.entity;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 广告报表Entity
 * @author Tim
 * @version 2016-04-28
 */
public class AdvertisingTotalByWeekDto {
	
	private Integer impressions;
	private Integer clicks;
   
	private Float totalSpend;
    
	private Integer weekSameSkuUnitsOrdered;
	private Float weekSameSkuUnitsLirun;
	
	private Integer weekOtherSkuUnitsOrdered;
	
	private Integer weekParentSkuUnitsOrdered;
	private Float weekParentSkuUnitsLirun;
	
	private List<AdvertisingByWeekDto> dtos = Lists.newArrayList();
	
	public AdvertisingTotalByWeekDto() {
		super();
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

	public List<AdvertisingByWeekDto> getDtos() {
		return dtos;
	}

	public void setDtos(List<AdvertisingByWeekDto> dtos) {
		this.dtos = dtos;
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
		weekSameSkuUnitsOrdered = 0;
		weekSameSkuUnitsLirun = 0f;
		weekOtherSkuUnitsOrdered = 0;
		weekParentSkuUnitsOrdered = 0;
		weekParentSkuUnitsLirun = 0f;
		for (AdvertisingByWeekDto dto : dtos) {
			dto.initData();
			impressions=dto.getImpressions()+impressions;
			clicks=dto.getClicks()+clicks;
			totalSpend=dto.getTotalSpend()+totalSpend;
			weekSameSkuUnitsOrdered = weekSameSkuUnitsOrdered+dto.getWeekSameSkuUnitsOrdered();
			weekSameSkuUnitsLirun= weekSameSkuUnitsLirun+dto.getWeekSameSkuUnitsLirun();
			weekOtherSkuUnitsOrdered = weekOtherSkuUnitsOrdered+dto.getWeekOtherSkuUnitsOrdered();
			weekParentSkuUnitsOrdered = weekParentSkuUnitsOrdered+dto.getWeekParentSkuUnitsOrdered();
			weekParentSkuUnitsLirun = weekParentSkuUnitsLirun+dto.getWeekParentSkuUnitsLirun();
		}
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


