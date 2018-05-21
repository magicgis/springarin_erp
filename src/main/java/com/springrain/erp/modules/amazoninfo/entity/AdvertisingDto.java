package com.springrain.erp.modules.amazoninfo.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Transient;

import com.google.common.collect.Lists;

/**
 * 广告报表Entity
 * @author Tim
 * @version 2015-03-03
 */
public class AdvertisingDto {
	
	private String name;
	
	private Integer impressions;
	private Integer clicks;
   
	private Float totalSpend;
    
	private Integer sameSkuOrdersPlaced;
	private Float sameSkuOrderSales;
	
	private Integer otherSkuOrdersPlaced;
	private Float otherSkuOrderSales;
	private List<AdvertisingGroupDto> groups = Lists.newArrayList();
	
	
	public AdvertisingDto() {
		super();
	}

	public AdvertisingDto(String name) {
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

	public Integer getOrdersPlaced() {
		return otherSkuOrdersPlaced+sameSkuOrdersPlaced;
	}

	public Float getOrderSales() {
		return otherSkuOrderSales+sameSkuOrderSales;
	}
	
	public List<AdvertisingGroupDto> getGroups() {
		return groups;
	}

	public void setGroups(List<AdvertisingGroupDto> groups) {
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
			Float temp = (float)getOrdersPlaced()*100/(float)clicks;
			BigDecimal bg = new BigDecimal(temp);
		    temp = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		    return temp;
		}
		return 0f;
	}  
	
	public Float getAcos() {
		if(getOrderSales()>0){
			Float temp = (float)getTotalSpend()*100/getOrderSales();
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
		sameSkuOrdersPlaced=0;
		sameSkuOrderSales=0f;
		otherSkuOrdersPlaced=0;
		otherSkuOrderSales=0f;
		for (AdvertisingGroupDto group : groups) {
			int impressions1=0;
			int clicks1=0;
			float totalSpend1=0f;
			int sameSkuOrdersPlaced1=0;
			float sameSkuOrderSales1=0f;
			int otherSkuOrdersPlaced1=0;
			float otherSkuOrderSales1=0f;
			for (Advertising advertising : group.getAdvertisings()) {
				impressions1=impressions1+advertising.getImpressions();
				clicks1=clicks1+advertising.getClicks();
				totalSpend1=totalSpend1+advertising.getTotalSpend();
				sameSkuOrdersPlaced1=sameSkuOrdersPlaced1+advertising.getSameSkuOrdersPlaced();
				sameSkuOrderSales1=sameSkuOrderSales1+advertising.getSameSkuOrderSales();
				otherSkuOrdersPlaced1=otherSkuOrdersPlaced1+advertising.getOtherSkuOrdersPlaced();
				otherSkuOrderSales1=otherSkuOrderSales1+advertising.getOtherSkuOrderSales();
			}
			group.setClicks(clicks1);
			group.setImpressions(impressions1);
			group.setTotalSpend(totalSpend1);
			group.setOtherSkuOrderSales(otherSkuOrderSales1);
			group.setOtherSkuOrdersPlaced(otherSkuOrdersPlaced1);
			group.setSameSkuOrderSales(sameSkuOrderSales1);
			group.setSameSkuOrdersPlaced(sameSkuOrdersPlaced1);
			impressions=impressions1+impressions;
			clicks=clicks1+clicks;
			totalSpend=totalSpend1+totalSpend;
			sameSkuOrdersPlaced=sameSkuOrdersPlaced1+sameSkuOrdersPlaced;
			sameSkuOrderSales=sameSkuOrderSales1+sameSkuOrderSales;
			otherSkuOrdersPlaced=otherSkuOrdersPlaced1+otherSkuOrdersPlaced;
			otherSkuOrderSales=otherSkuOrderSales1+otherSkuOrderSales;
		}
		this.setClicks(clicks);
		this.setImpressions(impressions);
		this.setTotalSpend(totalSpend);
		this.setOtherSkuOrderSales(otherSkuOrderSales);
		this.setOtherSkuOrdersPlaced(otherSkuOrdersPlaced);
		this.setSameSkuOrderSales(sameSkuOrderSales);
		this.setSameSkuOrdersPlaced(sameSkuOrdersPlaced);
	}
}


