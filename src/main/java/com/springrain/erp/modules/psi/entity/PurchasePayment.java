/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 采购付款Entity
 * @author Michael
 * @version 2014-11-21
 */
@Entity
@Table(name = "psi_purchase_payment")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PurchasePayment implements Serializable  {
	
	private static final long serialVersionUID = 4889722457919195609L;
	private     Integer      	id; 	
	private     String       	paymentNo;
	private     PsiSupplier  	supplier;
	private     Float        	paymentAmountTotal;
	private     String       	paymentSta;
	private     Date         	createDate;
	private     User         	createUser;
	private     String       	delFlag;
	private     String       	attchmentPath;
	private     String       	remark;
	private     User         	sureUser;
	private     Date         	sureDate;
	private 	User         	updateUser;
	private 	Date         	updateDate;
	private     User         	applyUser;
	private     Date         	applyDate;
    private     String       	accountType;
	private     String       	currencyType;
	private     String       	oldItemIds;
	private     Date         	cancelDate;          // 取消日期
	private     User         	cancelUser;          // 取消人
	private     String       	hasAdjust="0";       // 有调整项
	private     Float        	realPaymentAmount;   // 真实确认金额
	private     Float        	curPaymentAmount;    // 本次确认金额(非字段)
	private     List<PurchasePaymentItem> items=Lists.newArrayList();
	private     List<PurchaseAmountAdjust> adjusts;
	
	public PurchasePayment() {
		super();
	}

	public PurchasePayment(Integer id){
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

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	@Transient
	public String getOldItemIds() {
		return oldItemIds;
	}

	public void setOldItemIds(String oldItemIds) {
		this.oldItemIds = oldItemIds;
	}

	@OneToMany(mappedBy = "purchasePayment",fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Where(clause="del_flag=0")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<PurchasePaymentItem> getItems() {
		return items;
	}

	public void setItems(List<PurchasePaymentItem> items) {
		this.items = items;
	}
	
	@Transient
	public List<PurchaseAmountAdjust> getAdjusts() {
		return adjusts;
	}

	public void setAdjusts(List<PurchaseAmountAdjust> adjusts) {
		this.adjusts = adjusts;
	}
	
	

	@ManyToOne()
	@JoinColumn(name="supplier_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(PsiSupplier supplier) {
		this.supplier = supplier;
	}

	public Float getPaymentAmountTotal() {
		return paymentAmountTotal;
	}
	
	@Transient
	public Float getAmountTotal(){
		//如果是人民币除上6.18
		if(this.currencyType.equals("CNY")){
			return paymentAmountTotal/AmazonProduct2Service.getRateConfig().get("USD/CNY");
		}else{
			return paymentAmountTotal;
		}
	}
	
	@Transient
	public Float getRealAmountTotal(){
		//如果是人民币除上6.18
		if(this.currencyType.equals("CNY")){
			return realPaymentAmount/AmazonProduct2Service.getRateConfig().get("USD/CNY");
		}else{
			return realPaymentAmount;
		}
	}

	public void setPaymentAmountTotal(Float paymentAmountTotal) {
		this.paymentAmountTotal = paymentAmountTotal;
	}

	public String getPaymentSta() {
		return paymentSta;
	}

	public void setPaymentSta(String paymentSta) {
		this.paymentSta = paymentSta;
	}
	
	
	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	@Column(updatable=false)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@ManyToOne()
	@JoinColumn(name="update_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
	
	@ManyToOne()
	@JoinColumn(name="cancel_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCancelUser() {
		return cancelUser;
	}

	public void setCancelUser(User cancelUser) {
		this.cancelUser = cancelUser;
	}

	@ManyToOne()
	@JoinColumn(name="apply_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getApplyUser() {
		return applyUser;
	}

	public void setApplyUser(User applyUser) {
		this.applyUser = applyUser;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	@ManyToOne()
	@JoinColumn(name="sure_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getSureUser() {
		return sureUser;
	}

	public void setSureUser(User sureUser) {
		this.sureUser = sureUser;
	}

	public Date getSureDate() {
		return sureDate;
	}

	public void setSureDate(Date sureDate) {
		this.sureDate = sureDate;
	}
	
	@ManyToOne()
	@JoinColumn(name="create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}


	public String getAttchmentPath() {
		return attchmentPath;
	}

	public void setAttchmentPath(String attchmentPath) {
		if(StringUtils.isNotEmpty(attchmentPath)){
			if(StringUtils.isBlank(this.attchmentPath)){
				this.attchmentPath = attchmentPath;
			}else{
				this.attchmentPath=this.attchmentPath+","+attchmentPath;
			}
		}
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Transient
	public String getAccount(){
		String rs =  supplier.getAccountByType(accountType);
		if(StringUtils.isNotBlank(rs)){
			return rs.replace(";", "<br/>");
		}
		return "";
	}

	public String getHasAdjust() {
		return hasAdjust;
	}

	public void setHasAdjust(String hasAdjust) {
		this.hasAdjust = hasAdjust;
	}

	public Float getRealPaymentAmount() {
		return realPaymentAmount;
	}

	public void setRealPaymentAmount(Float realPaymentAmount) {
		this.realPaymentAmount = realPaymentAmount;
	}

	@Transient
	public Float getCanPaymentAmount(){
		return new BigDecimal(paymentAmountTotal).subtract(new BigDecimal(realPaymentAmount)).floatValue();
	}
	
	@Transient
	public Float getCurPaymentAmount() {
		return curPaymentAmount;
	}

	public void setCurPaymentAmount(Float curPaymentAmount) {
		this.curPaymentAmount = curPaymentAmount;
	}
	
	
	@Transient
	public Set<PurchaseOrder> getOrders(){
		Set<PurchaseOrder> set = Sets.newHashSet();
		for(PurchasePaymentItem item:items){
			if("0".equals(item.getPaymentType())){
				set.add(item.getOrder());
			}
		}
		return set;
	}
	
	
	@Transient
	public Set<PsiLadingBill> getLadings(){
		Set<PsiLadingBill> set = Sets.newHashSet();
		for(PurchasePaymentItem item:items){
			if("1".equals(item.getPaymentType())){
				set.add(item.getLadingBill());
			}
		}
		return set;
	}
	
}


