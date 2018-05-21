package com.springrain.erp.modules.amazoninfo.entity;


/**
 * 广告报表Entity
 * @author Tim
 * @version 2015-03-03
 */
public class AdvertisingCountItemDto {
	
	private String type;
	
	private String sku;
	
	private Float totalSpend;
    
	private Float totalOrderSales;
	
	private Integer saleV;
	
	private Integer impressions;
	
	private Integer clicks;
   
	private String dataData;
	
	private String keyword;
	
	public AdvertisingCountItemDto() {
		super();
	}

	public AdvertisingCountItemDto(String type, String sku, Float totalSpend,
			Float totalOrderSales, String dataData,Integer saleV,Integer impressions,Integer clicks,String keyword) {
		super();
		this.type = type;
		this.sku = sku;
		this.totalSpend = totalSpend;
		this.totalOrderSales = totalOrderSales;
		this.dataData = dataData;
		this.saleV = saleV;
		this.impressions = impressions;
		this.clicks = clicks;
		this.keyword = keyword;
	}




	public String getDataData() {
		return dataData;
	}

	public void setDataData(String dataData) {
		this.dataData = dataData;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
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

	public Integer getSaleV() {
		return saleV;
	}

	public void setSaleV(Integer saleV) {
		this.saleV = saleV;
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

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}


