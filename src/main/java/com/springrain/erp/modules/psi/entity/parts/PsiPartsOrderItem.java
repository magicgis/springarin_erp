/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.parts;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.google.common.collect.Lists;

/**
 * 配件订单详情Entity
 * @author Michael
 * @version 2015-06-02
 */
@Entity
@Table(name = "psiPartsOrderItem")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PsiPartsOrderItem implements Serializable{
	private static final long serialVersionUID = 2033786214047188780L;
	private 	Integer			id; 			 	// 编号
	private     String     		partsName;       	// 配件名字
	private     Integer         quantityOrdered; 	// 订单数量
	private     Integer         quantityReceived;	// 已接收数量
	private     Integer         quantityPreReceived;// 预接收数量
	private     Float           itemPrice;       	// 单价
	private     String          delFlag="0";        // 删除标记
	private     String          remark;          	// 备注
	private 	Date            deliveryDate;       // 系统交期
	private 	Date            actualDeliveryDate; // 大概交期
    private     PsiParts        psiParts;           // 配件
	private     PsiPartsOrder   partsOrder;  	 	// 配件订单
	private 	Float           paymentAmount ;     //已付款金额
	public Float getPaymentAmount() {
		return paymentAmount;
	}
	public void setPaymentAmount(Float paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	public Integer getQuantityPayment() {
		return quantityPayment;
	}
	public void setQuantityPayment(Integer quantityPayment) {
		this.quantityPayment = quantityPayment;
	}

	private 	Integer         quantityPayment ;   //已付款数量
	private List<PsiPartsDeliveryItem>  deliveryItemList = Lists.newArrayList();
	  
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne()
	@JoinColumn(name="parts_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiParts getPsiParts() {
		return psiParts;
	}
	public void setPsiParts(PsiParts psiParts) {
		this.psiParts = psiParts;
	}
	
	public String getPartsName() {
		return partsName;
	}
	
	public void setPartsName(String partsName) {
		this.partsName = partsName;
	}
	public Integer getQuantityOrdered() {
		return quantityOrdered;
	}
	public void setQuantityOrdered(Integer quantityOrdered) {
		this.quantityOrdered = quantityOrdered;
	}
	public Integer getQuantityReceived() {
		return quantityReceived;
	}
	
	@OneToMany(mappedBy = "partsOrderItem",fetch=FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<PsiPartsDeliveryItem> getDeliveryItemList() {
		return deliveryItemList;
	}
	public void setDeliveryItemList(List<PsiPartsDeliveryItem> deliveryItemList) {
		this.deliveryItemList = deliveryItemList;
	}
	
	
	public void setQuantityReceived(Integer quantityReceived) {
		this.quantityReceived = quantityReceived;
	}
	public Float getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(Float itemPrice) {
		this.itemPrice = itemPrice;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	
	@ManyToOne()
	@JoinColumn(name="parts_order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiPartsOrder getPartsOrder() {
		return partsOrder;
	}
	public void setPartsOrder(PsiPartsOrder partsOrder) {
		this.partsOrder = partsOrder;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public Integer getQuantityPreReceived() {
		return quantityPreReceived;
	}
	public void setQuantityPreReceived(Integer quantityPreReceived) {
		this.quantityPreReceived = quantityPreReceived;
	}
	
	public Date getActualDeliveryDate() {
		return actualDeliveryDate;
	}
	public void setActualDeliveryDate(Date actualDeliveryDate) {
		this.actualDeliveryDate = actualDeliveryDate;
	}
	@Transient
	public Integer getQuantityUnReceived(){
		return this.quantityOrdered-this.quantityReceived;
	}
	
	@Transient
	public Integer getQuantityCanReceived(){
		return this.quantityOrdered-this.quantityReceived-this.quantityPreReceived;
	}
	
	
}


