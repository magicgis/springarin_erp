/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.io.Serializable;
import java.util.Date;

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

/**
 * 采购订单预计收货明细Entity
 * @author Michael
 * @version 2014-10-29
 */

@Entity
@Table(name = "lc_psi_purchase_order_delivery_date")
public class LcPurchaseOrderDeliveryDate  implements Serializable{
	private static final long serialVersionUID = 4895548349563129658L;
	private 	Integer           id; 		                     // id
	private 	Integer           productId;                     // productId
	private     String            productName;                   // productName
	private 	String            colorCode;                     // 颜色编号
	private 	String            countryCode;                   // 国家编号
	private 	Date              deliveryDate;                  // 交货日期
	private 	Integer           quantity;                      // 预计收货数量
	private 	Integer           quantityReceived;              // 已收货数量
	private     String            delFlag;                       // 删除状态
	private     Integer           purchaseOrderId;               // 采购订单id
	private     String            remark;                        // 备注
	private 	Integer           quantityOff;                   // 线下预计收货数量
	private 	Integer           quantityOffReceived;           // 线下已收货数量
	
	private     LcPurchaseOrderItem orderItem;                   // 采购订单项
	private 	Date              deliveryDateLog;               //交期变更记录日期
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	@ManyToOne()
	@JoinColumn(name="purchase_order_item_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPurchaseOrderItem getOrderItem() {
		return orderItem;
	}
	public void setOrderItem(LcPurchaseOrderItem orderItem) {
		this.orderItem = orderItem;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public String getColorCode() {
		return colorCode;
	}
	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	@Transient
	public Integer getUnReceived(){
		return this.quantity-this.quantityReceived;
	}
	
	@Transient
	public Integer getUnReceivedOff(){
		return this.quantityOff-this.quantityOffReceived;
	}
	
	public Date getDeliveryDateLog() {
		return deliveryDateLog;
	}
	public void setDeliveryDateLog(Date deliveryDateLog) {
		this.deliveryDateLog = deliveryDateLog;
	}
	public Integer getQuantityReceived() {
		return quantityReceived;
	}
	public void setQuantityReceived(Integer quantityReceived) {
		this.quantityReceived = quantityReceived;
	}
	
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getPurchaseOrderId() {
		return purchaseOrderId;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public void setPurchaseOrderId(Integer purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}
	public LcPurchaseOrderDeliveryDate(){};
	
	public LcPurchaseOrderDeliveryDate(Integer productId,String productName,String colorCode,String countryCode,Date deliveryDate,
			Integer quantity,Integer quantityReceived,LcPurchaseOrderItem orderItem,String delFlag,Integer purchaseOrderId,
			String remark,Integer quantityOff,Integer quantityOffReceived,Date deliveryDateLog) {
		super();
		this.productId = productId;
		this.productName=productName;
		this.colorCode = colorCode;
		this.countryCode = countryCode;
		this.deliveryDate = deliveryDate;
		this.quantity = quantity;
		this.quantityReceived = quantityReceived;
		this.orderItem = orderItem;
		this.delFlag=delFlag;
		this.purchaseOrderId=purchaseOrderId;
		this.remark=remark;
		this.quantityOff=quantityOff;
		this.quantityOffReceived=quantityOffReceived;
		this.deliveryDateLog=deliveryDateLog;
	}
	public Integer getQuantityOff() {
		return quantityOff;
	}
	public void setQuantityOff(Integer quantityOff) {
		this.quantityOff = quantityOff;
	}
	public Integer getQuantityOffReceived() {
		return quantityOffReceived;
	}
	public void setQuantityOffReceived(Integer quantityOffReceived) {
		this.quantityOffReceived = quantityOffReceived;
	}

	
	
}


