package com.springrain.erp.modules.plan.dto;

import java.util.Map;

import com.springrain.erp.modules.plan.entity.Plan;

public class PlanMap {
	
	private Map<String,Plan> plansMap ;

	public Map<String, Plan> getPlansMap() {
		return plansMap;
	}

	public void setPlansMap(Map<String, Plan> plansMap) {
		this.plansMap = plansMap;
	}
	
}
