package com.springrain.erp.modules.psi.entity;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

public class PurchasePaymentItemDto {
	
	private     Integer          	 delayDays;          		 //付款延迟天数，未延迟为1
	private     Long            	 balanceDateNums;    		 //当前日期-创建日期   毫秒数；
	private 	BigDecimal           	 totalAmount;                //提单总额
	private 	BigDecimal           	 totalPaymentPreAmount;      //已申请总额
	private 	BigDecimal           	 totalPaymentAmount;         //已支付总额
	private 	Integer 			 balanceRate1;       		 //尾款首次付款比例
	private 	Integer 			 balanceDelay1;      		 //第一次付款延迟几天
	private 	Integer 			 balanceRate2;       		 //尾款第二次付款比例
	private 	Integer 			 balanceDelay2;      		 //第二次付款延迟几天     
	private     Integer              ladingBillItemId;           //提单itemId
	private     String               productName;                //产品名
	private     String               country;                    //国家
	private     String               billNo;                     //提单号
	private     String               orderNo;                    //订单号
	private     Integer              rate;                       //当前使用哪个rate
	private     String               currency;                   //货币类型
	private     Integer              quantity;                   //数量
	private     Integer              ladingBillId;               //提单Id
	private     BigDecimal                itemPrice;                  //单价
	private     Integer              deposit;                    //定金比例 
	private     String               ladingSta;                  //提单状态
	
	public Integer getDelayDays() {
		return delayDays;
	}
	public void setDelayDays(Integer delayDays) {
		this.delayDays = delayDays;
	}
	
	public Long getBalanceDateNums() {
		return balanceDateNums;
	}
	public void setBalanceDateNums(Long balanceDateNums) {
		this.balanceDateNums = balanceDateNums;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getTotalPaymentPreAmount() {
		return totalPaymentPreAmount;
	}
	public void setTotalPaymentPreAmount(BigDecimal totalPaymentPreAmount) {
		this.totalPaymentPreAmount = totalPaymentPreAmount;
	}
	public BigDecimal getTotalPaymentAmount() {
		return totalPaymentAmount;
	}
	public void setTotalPaymentAmount(BigDecimal totalPaymentAmount) {
		this.totalPaymentAmount = totalPaymentAmount;
	}
	public Integer getBalanceRate1() {
		return balanceRate1;
	}
	public void setBalanceRate1(Integer balanceRate1) {
		this.balanceRate1 = balanceRate1;
	}
	public Integer getBalanceDelay1() {
		return balanceDelay1;
	}
	public void setBalanceDelay1(Integer balanceDelay1) {
		this.balanceDelay1 = balanceDelay1;
	}
	
	public BigDecimal getNeedPay(){
		return this.totalAmount.subtract(totalPaymentAmount).subtract(totalPaymentPreAmount);
	}
	
	
	public Integer getBalanceRate2() {
		return balanceRate2;
	}
	public void setBalanceRate2(Integer balanceRate2) {
		this.balanceRate2 = balanceRate2;
	}
	public Integer getBalanceDelay2() {
		return balanceDelay2;
	}
	public void setBalanceDelay2(Integer balanceDelay2) {
		this.balanceDelay2 = balanceDelay2;
	}
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	
	
	public Integer getDeposit() {
		return deposit;
	}
	public void setDeposit(Integer deposit) {
		this.deposit = deposit;
	}
	public Integer getLadingBillId() {
		return ladingBillId;
	}
	public void setLadingBillId(Integer ladingBillId) {
		this.ladingBillId = ladingBillId;
	}
	public Integer getLadingBillItemId() {
		return ladingBillItemId;
	}
	public void setLadingBillItemId(Integer ladingBillItemId) {
		this.ladingBillItemId = ladingBillItemId;
	}
	
	
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
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	
	public Integer getRate() {
		return rate;
	}
	public void setRate(Integer rate) {
		this.rate = rate;
	}
	
	public BigDecimal getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}
	
	public String getConKey(){
		return billNo+"_"+productName;
	}
	public BigDecimal getHasPay(){
		return totalPaymentAmount.add(totalPaymentPreAmount);
	}
	
	public String getLadingSta() {
		return ladingSta;
	}
	public void setLadingSta(String ladingSta) {
		this.ladingSta = ladingSta;
	}
	
	public PurchasePaymentItemDto(){}
	
	public PurchasePaymentItemDto(Integer delayDays, Long balanceDateNums,	BigDecimal totalAmount, BigDecimal totalPaymentPreAmount,	BigDecimal totalPaymentAmount, Integer balanceRate1,	Integer balanceDelay1, Integer balanceRate2, 
			Integer balanceDelay2,Integer ladingBillItemId,String productName,String country,String billNo,Integer rate,String orderNo,
			String currency,Integer quantity,Integer ladingBillId,BigDecimal itemPrice,Integer deposit,String ladingSta) {
		super();
		this.delayDays = delayDays;
		this.balanceDateNums = balanceDateNums;
		this.totalAmount = totalAmount;
		this.totalPaymentPreAmount = totalPaymentPreAmount;
		this.totalPaymentAmount = totalPaymentAmount;
		this.balanceRate1 = balanceRate1;
		this.balanceDelay1 = balanceDelay1;
		this.balanceRate2 = balanceRate2;
		this.balanceDelay2 = balanceDelay2;
		this.productName=productName;
		this.ladingBillItemId=ladingBillItemId;
		this.country=country;
		this.billNo =billNo;
		this.rate=rate;
		this.orderNo=orderNo;
		this.currency=currency;
		this.quantity=quantity;
		this.ladingBillId=ladingBillId;
		this.itemPrice=itemPrice;
		this.deposit=deposit;
		this.ladingSta=ladingSta;
	}

	
}


