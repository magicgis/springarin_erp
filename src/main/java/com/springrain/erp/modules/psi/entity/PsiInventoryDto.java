package com.springrain.erp.modules.psi.entity;

import java.util.Map;

import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;

public class PsiInventoryDto {
	
	private String productName;
	
	private String country;
	
	private String color;
	
	
	private Map<String,Integer> skusNewQuantity= Maps.newHashMap();
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getQuantity() {
		Integer res =0;
		
		for(Integer quantity:skusNewQuantity.values()){
			res+=quantity;
		}
		
		return res;
	}

	public Integer getNewQuantity() {
		Integer res =0;
		for(Integer quantity:skusNewQuantity.values()){
			res+=quantity;
		}
		return res;
	}
	

	public Map<String, Integer> getSkusNewQuantity() {
		return skusNewQuantity;
	}

	public void setSkusNewQuantity(Map<String, Integer> skusNewQuantity) {
		this.skusNewQuantity = skusNewQuantity;
	}


	public String getProductNameWithColor() {
		if(StringUtils.isNotBlank(color)){
			return productName+"_"+color;
		}
		return productName;
	}

	public PsiInventoryDto(String productName, String country, String color,Map<String, Integer> skusNewQuantity) {
		super();
		this.productName = productName;
		this.country = country;
		this.color = color;
		this.skusNewQuantity = skusNewQuantity;
	}

}
