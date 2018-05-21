/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 采购付款明细Entity
 * @author Michael
 * @version 2014-11-21
 */
@Entity
@Table(name = "lc_psi_parts_payment_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiPartsPaymentItem implements Serializable {
	private static final long serialVersionUID = 2283195904006129657L;
	private   	 Integer      	  id; 		       
	private   	 Float            paymentAmount;
	private   	 String           delFlag="0";
	private   	 String           remark;
	private      String      	  paymentType;
	private      String           billNo;           //未知单号，根据类型判断是   订单编号  还是提单编号
	private    	 LcPsiPartsOrder 	  order;
	private      LcPsiPartsDelivery ladingBill;
	private      Integer          unknowId;         //未知id（非表字段）
	private   	 LcPsiPartsPayment  psiPartsPayment;
  
	public LcPsiPartsPaymentItem() {
		super();
	}  

	public LcPsiPartsPaymentItem(Integer id){
		this();  
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public Float getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Float paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	

	@ManyToOne()
	@JoinColumn(name="payment_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPsiPartsPayment getPsiPartsPayment() {
		return psiPartsPayment;
	}

	public void setPsiPartsPayment(LcPsiPartsPayment psiPartsPayment) {
		this.psiPartsPayment = psiPartsPayment;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	@Transient
	public Integer getUnknowId() {
		return unknowId;
	}

	public void setUnknowId(Integer unknowId) {
		this.unknowId = unknowId;
	}

	@OneToOne(optional=true)
	@JoinColumn(name="parts_order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public LcPsiPartsOrder getOrder() {
		return order;
	}

	public void setOrder(LcPsiPartsOrder order) {
		this.order = order;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="parts_delivery_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public LcPsiPartsDelivery getLadingBill() {
		return ladingBill;
	}

	public void setLadingBill(LcPsiPartsDelivery ladingBill) {
		this.ladingBill = ladingBill;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	@Transient
	public String getTypeName(){
		if("0".equals(paymentType)){
			return "定金";
		}else if("1".equals(paymentType)){
			return "尾款";
		}
		return "";
	}
	
	@Transient
	public Float getNeedPaymentAmount() {
		if("0".equals(paymentType)){
			return order.getTotalAmount()*order.getDeposit()/100f;
		}else if("1".equals(paymentType)){
			return ladingBill.getTotalAmount() -ladingBill.getTotalPaymentAmount();
		}
		return -1f;
	}
	
	//提单item总价值
	@Transient
	public Float getPaymentAmountTotal() {
		if("0".equals(paymentType)){
			return order.getTotalAmount()*order.getDeposit()/100f;
		}else if("1".equals(paymentType)){
			return ladingBill.getTotalAmount();
		}
		return -1f;
	}


	
}


