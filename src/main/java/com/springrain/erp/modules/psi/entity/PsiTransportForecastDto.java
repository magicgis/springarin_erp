package com.springrain.erp.modules.psi.entity;

import java.util.Date;

public class PsiTransportForecastDto {
	
	private String sku;
	
	private Date forecastDate;
	
	private Integer quantity;

	private long separateDay;
	
	
	public PsiTransportForecastDto(String sku, Date forecastDate,
			Integer quantity,long separateDay) {
		super();
		this.sku = sku;
		this.forecastDate = forecastDate;
		this.quantity = quantity;
		this.separateDay=separateDay;
	}

	
	
	public PsiTransportForecastDto() {
		super();
	}



	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Date getForecastDate() {
		return forecastDate;
	}

	public void setForecastDate(Date forecastDate) {
		this.forecastDate = forecastDate;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}



	public long getSeparateDay() {
		return separateDay;
	}



	public void setSeparateDay(long separateDay) {
		this.separateDay = separateDay;
	}
	
}
