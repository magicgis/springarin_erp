package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "psi_purchase_forecast_report")
public class PurchaseForecastDto implements Serializable {

	private static final long serialVersionUID = -4410870021018958702L;
	private      Integer        id;                	//id
	private  	 Integer 		supplierId;        	//供应商id
	private      String         productNameColor;  	//产品名
	private      String         month;             	//月份
	private      Float          orderAmount;       	//订单金额
	private      Float          depositAmount;    	//定金金额
	private      Float          ladingAmount;   	//尾款金额
	private      Float          balanceLadingAmount;//结余尾款
	private      Date           dataDate;          	//数据产生日期
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}
	public String getProductNameColor() {
		return productNameColor;
	}
	public void setProductNameColor(String productNameColor) {
		this.productNameColor = productNameColor;
	}
	public Float getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(Float orderAmount) {
		this.orderAmount = orderAmount;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public Float getDepositAmount() {
		return depositAmount;
	}
	public void setDepositAmount(Float depositAmount) {
		this.depositAmount = depositAmount;
	}
	public Float getLadingAmount() {
		return ladingAmount;
	}
	public void setLadingAmount(Float ladingAmount) {
		this.ladingAmount = ladingAmount;
	}
	public Float getBalanceLadingAmount() {
		return balanceLadingAmount;
	}
	public void setBalanceLadingAmount(Float balanceLadingAmount) {
		this.balanceLadingAmount = balanceLadingAmount;
	}
	public Date getDataDate() {
		return dataDate;
	}
	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}
	
	@Transient
	public Float getAllAmount(){
		return this.depositAmount+this.ladingAmount+this.balanceLadingAmount;
	}
	
	public PurchaseForecastDto(){};
	
	public PurchaseForecastDto(Integer supplierId,String productNameColor, String month, Float orderAmount,	Float depositAmount, Float ladingAmount, Float balanceLadingAmount,	Date dataDate) {
		super();
		this.supplierId = supplierId;
		this.productNameColor = productNameColor;
		this.month = month;
		this.orderAmount = orderAmount;
		this.depositAmount = depositAmount;
		this.ladingAmount = ladingAmount;
		this.balanceLadingAmount = balanceLadingAmount;
		this.dataDate = dataDate;
	}
	
	
	
	

}
