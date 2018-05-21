package com.springrain.erp.modules.amazoninfo.entity.customer;

import java.util.Map;

import com.google.common.collect.Maps;

public class MapVo {
	
	private Map<String,Object> vo = Maps.newHashMap();

	public Map<String, Object> getVo() {
		return vo;
	}

	public void setVo(Map<String, Object> vo) {
		this.vo = vo;
	}
	
}
