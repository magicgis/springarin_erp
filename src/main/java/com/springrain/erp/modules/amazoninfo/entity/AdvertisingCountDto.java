package com.springrain.erp.modules.amazoninfo.entity;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 广告报表Entity
 * @author Tim
 * @version 2015-03-03
 */
public class AdvertisingCountDto {
	
	private String type;
	
	private Float totalSpend;
    
	private Float totalOrderSales;
	
	private String dataDate;
	
	private  Integer clicks;
	private  Integer impressions;
	private  Integer quantity;
	
	
	
	
	public Integer getClicks() {
		return clicks;
	}

	public void setClicks(Integer clicks) {
		this.clicks = clicks;
	}

	public Integer getImpressions() {
		return impressions;
	}

	public void setImpressions(Integer impressions) {
		this.impressions = impressions;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	private List<AdvertisingCountItemDto> items = Lists.newArrayList();
	
	public AdvertisingCountDto() {
		super();
	}

	public AdvertisingCountDto(String type, Float totalSpend,
			Float totalOrderSales, String dataDate) {
		super();
		this.type = type;
		this.totalSpend = totalSpend;
		this.totalOrderSales = totalOrderSales;
		this.dataDate = dataDate;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Float getTotalSpend() {
		return totalSpend;
	}

	public void setTotalSpend(Float totalSpend) {
		this.totalSpend = totalSpend;
	}

	public Float getTotalOrderSales() {
		return totalOrderSales;
	}

	public void setTotalOrderSales(Float totalOrderSales) {
		this.totalOrderSales = totalOrderSales;
	}

	public List<AdvertisingCountItemDto> getItems() {
		return items;
	}

	public void setItems(List<AdvertisingCountItemDto> items) {
		this.items = items;
	}
	
}


