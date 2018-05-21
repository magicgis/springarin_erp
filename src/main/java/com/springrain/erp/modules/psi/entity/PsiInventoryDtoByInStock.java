package com.springrain.erp.modules.psi.entity;

import java.util.Map;

import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;

public class PsiInventoryDtoByInStock {
	
	private String productName;
	
	private String country;
	
	private String color;
	
	//key 为仓库国家码
	private Map<String,PsiInventoryDtoByInStockWithWarehouseCode> quantityInventory= Maps.newHashMap();
	
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

	public Integer getNewQuantity() {
		Integer res =0;
		for (PsiInventoryDtoByInStockWithWarehouseCode psiInventoryDtoByInStockWithWarehouseCode : quantityInventory.values()) {
			res+=psiInventoryDtoByInStockWithWarehouseCode.getNewQuantity();
		}
		return res;
	}
	
	public String getProductNameWithColor() {
		if(StringUtils.isNotBlank(color)){
			return productName+"_"+color;
		}
		return productName;
	}

	public Map<String, PsiInventoryDtoByInStockWithWarehouseCode> getQuantityInventory() {
		return quantityInventory;
	}

	public void setQuantityInventory(
			Map<String, PsiInventoryDtoByInStockWithWarehouseCode> quantityInventory) {
		this.quantityInventory = quantityInventory;
	}

	public PsiInventoryDtoByInStock(String productName,	String country,	String color, Map<String, PsiInventoryDtoByInStockWithWarehouseCode> quantityInventory) {
		super();
		this.productName = productName;
		this.country = country;
		this.color = color;
		this.quantityInventory = quantityInventory;
	}
}
