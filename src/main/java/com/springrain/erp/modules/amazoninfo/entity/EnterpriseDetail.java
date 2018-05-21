package com.springrain.erp.modules.amazoninfo.entity;

public class EnterpriseDetail {
    private Float addUpSales;
    private Float sales;
    private Float dayWeight;
    private Float totalWeight;
    private Float dayGoal;
    private Float addUpGoal;
    private Float rate;
    private Float addDayWeight;
    private Float autoDayGoal;
    
	public EnterpriseDetail() {
		super();
	}
	
	public EnterpriseDetail(Float addUpSales, Float sales, Float dayWeight,
			Float totalWeight, Float dayGoal, Float addUpGoal,Float rate) {
		super();
		this.addUpSales = addUpSales;
		this.sales = sales;
		this.dayWeight = dayWeight;
		this.totalWeight = totalWeight;
		this.dayGoal = dayGoal;
		this.addUpGoal = addUpGoal;
		this.rate=rate;
	}
	
	public EnterpriseDetail(Float addUpSales, Float sales, Float dayWeight,
			Float totalWeight, Float dayGoal, Float addUpGoal,Float rate,Float addDayWeight,Float autoDayGoal) {
		super();
		this.addUpSales = addUpSales;
		this.sales = sales;
		this.dayWeight = dayWeight;
		this.totalWeight = totalWeight;
		this.dayGoal = dayGoal;
		this.addUpGoal = addUpGoal;
		this.rate=rate;
		this.addDayWeight=addDayWeight;
		this.autoDayGoal=autoDayGoal;
	}
	
	public EnterpriseDetail(Float addUpSales, Float sales, Float dayWeight,
			Float totalWeight, Float dayGoal, Float addUpGoal,Float rate,Float autoDayGoal) {
		super();
		this.addUpSales = addUpSales;
		this.sales = sales;
		this.dayWeight = dayWeight;
		this.totalWeight = totalWeight;
		this.dayGoal = dayGoal;
		this.addUpGoal = addUpGoal;
		this.rate=rate;
		this.autoDayGoal=autoDayGoal;
	}
	
	public Float getAddDayWeight() {
		return addDayWeight;
	}

	public void setAddDayWeight(Float addDayWeight) {
		this.addDayWeight = addDayWeight;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public Float getAddUpSales() {
		return addUpSales;
	}
	public void setAddUpSales(Float addUpSales) {
		this.addUpSales = addUpSales;
	}
	public Float getSales() {
		return sales;
	}
	public void setSales(Float sales) {
		this.sales = sales;
	}
	public Float getDayWeight() {
		return dayWeight;
	}
	public void setDayWeight(Float dayWeight) {
		this.dayWeight = dayWeight;
	}
	public Float getTotalWeight() {
		return totalWeight;
	}
	public void setTotalWeight(Float totalWeight) {
		this.totalWeight = totalWeight;
	}
	public Float getDayGoal() {
		return dayGoal;
	}
	public void setDayGoal(Float dayGoal) {
		this.dayGoal = dayGoal;
	}
	public Float getAddUpGoal() {
		return addUpGoal;
	}
	public void setAddUpGoal(Float addUpGoal) {
		this.addUpGoal = addUpGoal;
	}

	public Float getAutoDayGoal() {
		return autoDayGoal;
	}

	public void setAutoDayGoal(Float autoDayGoal) {
		this.autoDayGoal = autoDayGoal;
	}
    
    
}
