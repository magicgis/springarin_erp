package com.springrain.erp.modules.amazoninfo.entity;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 广告报表Entity
 * @author Tim
 * @version 2015-03-03
 */
public class AdvertisingTotalDto {
	
	private Integer impressions;
	private Integer clicks;
   
	private Float totalSpend;
    
	private Integer sameSkuOrdersPlaced;
	private Float sameSkuOrderSales;
	
	private Integer otherSkuOrdersPlaced;
	private Float otherSkuOrderSales;
	private List<AdvertisingDto> dtos = Lists.newArrayList();
	
	
	public AdvertisingTotalDto() {
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
	
	
	public List<AdvertisingDto> getDtos() {
		return dtos;
	}

	public void setDtos(List<AdvertisingDto> dtos) {
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
		for (AdvertisingDto dto : dtos) {
			dto.initData();
			impressions=dto.getImpressions()+impressions;
			clicks=dto.getClicks()+clicks;
			totalSpend=dto.getTotalSpend()+totalSpend;
			sameSkuOrdersPlaced=dto.getSameSkuOrdersPlaced()+sameSkuOrdersPlaced;
			sameSkuOrderSales=dto.getSameSkuOrderSales()+sameSkuOrderSales;
			otherSkuOrdersPlaced=dto.getOtherSkuOrdersPlaced()+otherSkuOrdersPlaced;
			otherSkuOrderSales=dto.getOtherSkuOrderSales()+otherSkuOrderSales;
		}
	}
}


