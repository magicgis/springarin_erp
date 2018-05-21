package com.springrain.erp.modules.psi.entity;

import java.util.Map;

import com.google.common.collect.Maps;

public class MapVo {
	
	private Map<Integer, Float> prices = Maps.newHashMap();
	
	private Map<Integer, String> remarks = Maps.newHashMap();
	
	private Map<Integer, Float> rmbPrices = Maps.newHashMap();

	public Map<Integer, Float> getPrices() {
		return prices;
	}

	public void setPrices(Map<Integer, Float> prices) {
		this.prices = prices;
	}

	public Map<Integer, String> getRemarks() {
		return remarks;
	}

	public void setRemarks(Map<Integer, String> remarks) {
		this.remarks = remarks;
	}

	public Map<Integer, Float> getRmbPrices() {
		return rmbPrices;
	}

	public void setRmbPrices(Map<Integer, Float> rmbPrices) {
		this.rmbPrices = rmbPrices;
	}
	
}
