package com.springrain.erp.modules.psi.entity;

  public class PsiTransportDto {
	  
	private Float weight;
	private Float money;
	private Integer quantity;
	
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Float getWeight() {
		return weight;
	}
	public void setWeight(Float weight) {
		this.weight = weight;
	}
	public Float getMoney() {
		return money;
	}
	public void setMoney(Float money) {
		this.money = money;
	}
	
	public PsiTransportDto() {
		super();
	}
	
	public PsiTransportDto(Float weight, Float money, Integer quantity) {
		super();
		this.weight = weight;
		this.money = money;
		this.quantity = quantity;
	}
	
	public PsiTransportDto(Float weight, Float money) {
		super();
		this.weight = weight;
		this.money = money;
	}
	
	public float getUnitPrice(){
		if(weight!=0){
			return money/weight;
		}
		return 0;
	}
	
}
