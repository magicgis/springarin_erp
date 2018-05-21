/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.io.Serializable;
import java.math.BigDecimal;

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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.service.BaseService;
/**
 * 采购付款明细Entity
 * @author Michael
 * @version 2014-11-21
 */
@Entity
@Table(name = "lc_psi_purchase_payment_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPurchasePaymentItem implements Serializable{
	private static final long serialVersionUID = 6741766231787971086L;
	private    Integer      	id; 		       
	private    LcPurchaseOrder 	order;
	private    LcPsiLadingBill 	ladingBill;
	private    String      		paymentType;
	private    BigDecimal       paymentAmount;
	private    String           delFlag="0";
	private    String           remark;
	private    LcPurchasePayment  purchasePayment;
	private    String           billNo;           //未知单号，根据类型判断是   订单编号  还是提单编号
	private    LcPsiLadingBillItem 	ladingBillItem;
	
	
	public LcPurchasePaymentItem() {
		super();
	}
	public LcPurchasePaymentItem(Integer id){
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

	@ManyToOne()
	@JoinColumn(name="payment_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPurchasePayment getPurchasePayment() {
		return purchasePayment;
	}

	public void setPurchasePayment(LcPurchasePayment purchasePayment) {
		this.purchasePayment =purchasePayment;
	}

	@OneToOne(optional=true)
	@JoinColumn(name="purchase_order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	//@Cascade(CascadeType.ALL)
	public LcPurchaseOrder getOrder() {
		return order;
	}

	public void setOrder(LcPurchaseOrder order) {
		this.order = order;
	}
	
	@ManyToOne(optional=true)
	@JoinColumn(name="lading_bill_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPsiLadingBill getLadingBill() {
		return ladingBill;
	}

	public void setLadingBill(LcPsiLadingBill ladingBill) {
		this.ladingBill = ladingBill;
	}
	
	@ManyToOne()
	@JoinColumn(name="lading_item_bill_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPsiLadingBillItem getLadingBillItem(){
		return ladingBillItem;
	}
	
	public void setLadingBillItem(LcPsiLadingBillItem ladingBillItem){
		this.ladingBillItem=ladingBillItem;
	}


	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}


	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}



	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
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
	public BigDecimal getNeedPaymentAmount(){
		if("0".equals(paymentType)){
			return order.getTotalAmount().subtract(order.getPaymentAmount());
		}else{
			return ladingBill.getTotalAmount().subtract(ladingBill.getTotalPaymentAmount());
		}
	}
	
	//提单item总价值
	@Transient
	public BigDecimal getPaymentAmountTotal() {
		if("0".equals(paymentType)){
			return paymentAmount;
		}else{
			return ladingBill.getTotalAmount();
		}
	}

	
	@Transient
	public String getUrl() {
		if("0".equals(paymentType)){
			return "<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPurchaseOrder/view?id="+order.getId()+"'>"+billNo+"</a>";
		}else{
			return "<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPsiLadingBill/view?id="+ladingBill.getId()+"'>"+billNo+"</a>";
		}
	}
	
	@Transient
	public BigDecimal getTotalAmount() {
		if("0".equals(paymentType)){
			return order.getTotalAmount();
		}else if("1".equals(paymentType)){
			return ladingBillItem.getNoDepositTotalAmount();
		}else{
			return BigDecimal.ZERO;
		}
	}
}


