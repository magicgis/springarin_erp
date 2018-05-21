package com.springrain.erp.modules.amazoninfo.entity.order;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "amazoninfo_refund_item")
public class AmazoninfoRefundItem {
   
	private Integer id;
	private String productName;
	private String asin;
	private String sku;
	private String orderItemId;
	private String refundReason;
	private String remark;
	private String refundType;
	private Float money;
	private AmazonRefund amazonRefund;
	private Float shippingMoney;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getAsin() {
		return asin;
	}
	public void setAsin(String asin) {
		this.asin = asin;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	
	public String getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}
	public String getRefundReason() {
		return refundReason;
	}
	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getRefundType() {
		return refundType;
	}
	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}
	public Float getMoney() {
		return money;
	}
	public void setMoney(Float money) {
		this.money = money;
	}
	@ManyToOne()
	@JoinColumn(name="refund_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonRefund getAmazonRefund() {
		return amazonRefund;
	}
	public void setAmazonRefund(AmazonRefund amazonRefund) {
		this.amazonRefund = amazonRefund;
	}
	@Transient
	public Float getShippingMoney() {
		return shippingMoney;
	}
	public void setShippingMoney(Float shippingMoney) {
		this.shippingMoney = shippingMoney;
	}

	
}
