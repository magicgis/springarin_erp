package com.springrain.erp.modules.psi.entity;

import java.util.Map;

import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;

public class PsiInventoryDtoByInStockWithWarehouseCode {
	
	private String productName;
	
	private String country;
	
	private String color;
	
	private String warehouseCode;
	
	private Map<String,Integer> skusNewQuantity= Maps.newHashMap();
	private Map<String,Integer> skusOldQuantity= Maps.newHashMap();
	private Map<String,Integer> skusBrokenQuantity= Maps.newHashMap();
	private Map<String,Integer> skusRenewQuantity= Maps.newHashMap();
	private Map<String,Integer> skusOfflineQuantity= Maps.newHashMap();
	
	
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
		
		for(Integer quantity:skusBrokenQuantity.values()){
			res+=quantity;
		}
		
		for(Integer quantity:skusOldQuantity.values()){
			res+=quantity;
		}
		
		for(Integer quantity:skusRenewQuantity.values()){
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
	
	public Integer getBrokenQuantity() {
		Integer res =0;
		for(Integer quantity:skusBrokenQuantity.values()){
			res+=quantity;
		}
		return res;
	}
	
	public Integer getOldQuantity() {
		Integer res =0;
		for(Integer quantity:skusOldQuantity.values()){
			res+=quantity;
		}
		return res;
	}
	
	public Integer getRenewQuantity() {
		Integer res =0;
		for(Integer quantity:skusRenewQuantity.values()){
			res+=quantity;
		}
		return res;
	}
	
	
	public Integer getOfflineQuantity() {
		Integer res =0;
		for(Integer quantity:skusOfflineQuantity.values()){
			res+=quantity;
		}
		return res;
	}
	
	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public Map<String, Integer> getSkusNewQuantity() {
		return skusNewQuantity;
	}

	public void setSkusNewQuantity(Map<String, Integer> skusNewQuantity) {
		this.skusNewQuantity = skusNewQuantity;
	}

	public Map<String, Integer> getSkusOldQuantity() {
		return skusOldQuantity;
	}

	public void setSkusOldQuantity(Map<String, Integer> skusOldQuantity) {
		this.skusOldQuantity = skusOldQuantity;
	}

	public Map<String, Integer> getSkusBrokenQuantity() {
		return skusBrokenQuantity;
	}

	public void setSkusBrokenQuantity(Map<String, Integer> skusBrokenQuantity) {
		this.skusBrokenQuantity = skusBrokenQuantity;
	}

	public Map<String, Integer> getSkusRenewQuantity() {
		return skusRenewQuantity;
	}

	public void setSkusRenewQuantity(Map<String, Integer> skusRenewQuantity) {
		this.skusRenewQuantity = skusRenewQuantity;
	}
	
	public Map<String, Integer> getSkusOfflineQuantity() {
		return skusOfflineQuantity;
	}

	public void setSkusOfflineQuantity(Map<String, Integer> skusOfflineQuantity) {
		this.skusOfflineQuantity = skusOfflineQuantity;
	}

	public String getProductNameWithColor() {
		if(StringUtils.isNotBlank(color)){
			return productName+"_"+color;
		}
		return productName;
	}

	public PsiInventoryDtoByInStockWithWarehouseCode(String productName, String country, String color,Map<String, Integer> skusNewQuantity,Map<String, Integer> skusOldQuantity,Map<String, Integer> skusBrokenQuantity,Map<String, Integer> skusRenewQuantity,Map<String, Integer> skusOfflineQuantity,String warehouseCode) {
		super();
		this.productName = productName;
		this.country = country;
		this.color = color;
		this.skusNewQuantity = skusNewQuantity;
		this.skusOldQuantity = skusOldQuantity;
		this.skusBrokenQuantity = skusBrokenQuantity;
		this.skusRenewQuantity = skusRenewQuantity;
		this.skusOfflineQuantity=skusOfflineQuantity;
		this.warehouseCode=warehouseCode;
	}

}
