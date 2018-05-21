package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Map;

import com.google.common.collect.Maps;

public class MapVo {
	
	private Map<String,Map<String,Integer>> inventorys = Maps.newHashMap();

	public Map<String, Map<String, Integer>> getInventorys() {
		return inventorys;
	}

	public void setInventorys(Map<String, Map<String, Integer>> inventorys) {
		this.inventorys = inventorys;
	} 
	
}
