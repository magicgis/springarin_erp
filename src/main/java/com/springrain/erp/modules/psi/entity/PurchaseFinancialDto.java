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
@Table(name = "psi_purchase_financial_report")
public class PurchaseFinancialDto implements Serializable {

	private static final long serialVersionUID = -4410870021018958702L;
	private      Integer        id;                //id
	private  	 Integer 		supplierId;        //供应商id
	private      String         productNameColor;  //产品名
	private      String         month;             //月份
	private      Float          orderAmount;       //订单金额
	private      Float          payOrderAmount;    //定金支付金额
	private      Float          payLadingAmount;   //尾款支付金额
	private      Float          upPayAmount;       //截止到月末未支付金额   (非字段)
	private      Date           dataDate;          //数据产生日期
	
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
	public Float getPayOrderAmount() {
		return payOrderAmount;
	}
	public void setPayOrderAmount(Float payOrderAmount) {
		this.payOrderAmount = payOrderAmount;
	}
	public Float getPayLadingAmount() {
		return payLadingAmount;
	}
	public void setPayLadingAmount(Float payLadingAmount) {
		this.payLadingAmount = payLadingAmount;
	}
	
	
	
	@Transient
	public Float getUpPayAmount() {
		return upPayAmount;
	}
	public void setUpPayAmount(Float upPayAmount) {
		this.upPayAmount = upPayAmount;
	}
	
	
	public Date getDataDate() {
		return dataDate;
	}
	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	
	@Transient
	public Float getAllPayment(){
		return this.payLadingAmount+this.payOrderAmount;
	}
	
	
	public PurchaseFinancialDto(){};
	
	public PurchaseFinancialDto(String month,Integer supplierId, String productNameColor,Float orderAmount, Float payOrderAmount, Float payLadingAmount,Date dataDate) {
		super();
		this.month= month;
		this.supplierId = supplierId;
		this.productNameColor = productNameColor;
		this.orderAmount = orderAmount;
		this.payOrderAmount = payOrderAmount;
		this.payLadingAmount = payLadingAmount;
		this.dataDate = dataDate;
	}
	
	
	
	
	
	
	

}
