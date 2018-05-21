/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 运单付款表Entity
 * @author Michael
 * @version 2015-01-21
 */
@Entity
@Table(name = "lc_psi_transport_payment_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiTransportPaymentItem{
	
	private 	Integer 			id; 		      // id
	private		Integer             tranOrderId;      // 运单id
	private     String              transportNo;      // 运单编号
	private     String              paymentType;      // 付款种类
	private     Float               paymentAmount;    // 支付金额
	private     String              currency;         // 货币种类
	private     String              delFlag="0";      // 删除标记
	private     String              remark;           // 备注
	private     Float               rate;             // 当时汇率
	private     String              isChecked;        // 是否选中           
	private		Float               afterAmount;      // 转换后金额
	
	private     LcPsiTransportPayment transportPayment; // 运单付款总表
	
	
	public LcPsiTransportPaymentItem() {
		super();
	}

	public LcPsiTransportPaymentItem(Integer id){
		this();
		this.id = id;
	}
	
	
	@Transient
	public String getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(String isChecked) {
		this.isChecked = isChecked;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Float getAfterAmount() {
		return afterAmount;
	}

	public void setAfterAmount(Float afterAmount) {
		this.afterAmount = afterAmount;
	}

	@ManyToOne()
	@JoinColumn(name="payment_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPsiTransportPayment getTransportPayment() {
		return transportPayment;
	}

	public void setTransportPayment(LcPsiTransportPayment transportPayment) {
		this.transportPayment = transportPayment;
	}

	public Integer getTranOrderId() {
		return tranOrderId;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public void setTranOrderId(Integer tranOrderId) {
		this.tranOrderId = tranOrderId;
	}

	public String getTransportNo() {
		return transportNo;
	}

	public void setTransportNo(String transportNo) {
		this.transportNo = transportNo;
	}

	public Float getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Float paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	
	
	
	
}


