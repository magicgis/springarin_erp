/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

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
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 运单付款修正表Entity
 * @author Michael
 * @version 2015-01-29
 */
@Entity
@Table(name = "psi_transport_revise")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiTransportRevise {
	
	private      Integer       id; 			  // id
	private 	 Integer       tranOrderId;   // 运单id
	private      String        tranOrderNo;   // 运单No
	private		 String        paymentNo;  	  // 付款编号
	private      PsiSupplier   supplier;      // 供应商
	private		 String		   accountType;	  // 账号类型
	private		 String  	   attchmentPath; // 水单地址
	private      Float         reviseAmount;  // 修正金额
	private      Float         rate;          // 汇率
	private      String        currency;      // 货币类型
	private      String        reviseSta;     // 修正状态
	private      String        remark;        // 备注
	private      User          applyUser;     // 申请人
	private      Date          applyDate;     // 申请时间
	private      User          sureUser;      // 确认人
	private      Date          sureDate;      // 确认时间
	private      User          cancelUser;    // 确认人
	private      Date          cancelDate;    // 确认时间
	private		 String  	   accountPath;   // 账单地址
	private      List<PsiTransportReviseItem> items;
	
	
	private     String  payFlowNo;           // 付款流水号     yyyyMM0001
	private     String  applyTime;
	private     String  sureTime;
	private     String  moneyInfo;
	private     String  flowInfo;
	private     String  transportNoRemark;
	private     String  applyInfo;
	private     String  checkInfo;
	private     String  applyUserInfo;
	private     String  supplierName;
	private     String  supplierAccount;

	@Transient
	public String getPayFlowNo() {
		return payFlowNo;
	}

	public void setPayFlowNo(String payFlowNo) {
		this.payFlowNo = payFlowNo;
	}

	@Transient
	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	@Transient
	public String getSureTime() {
		return sureTime;
	}

	public void setSureTime(String sureTime) {
		this.sureTime = sureTime;
	}

	@Transient
	public String getMoneyInfo() {
		return moneyInfo;
	}

	public void setMoneyInfo(String moneyInfo) {
		this.moneyInfo = moneyInfo;
	}

	@Transient
	public String getFlowInfo() {
		return flowInfo;
	}

	public void setFlowInfo(String flowInfo) {
		this.flowInfo = flowInfo;
	}

	@Transient
	public String getTransportNoRemark() {
		return transportNoRemark;
	}

	public void setTransportNoRemark(String transportNoRemark) {
		this.transportNoRemark = transportNoRemark;
	}

	@Transient
	public String getApplyInfo() {
		return applyInfo;
	}

	public void setApplyInfo(String applyInfo) {
		this.applyInfo = applyInfo;
	}

	@Transient
	public String getCheckInfo() {
		return checkInfo;
	}

	public void setCheckInfo(String checkInfo) {
		this.checkInfo = checkInfo;
	}

	@Transient
	public String getApplyUserInfo() {
		return applyUserInfo;
	}

	public void setApplyUserInfo(String applyUserInfo) {
		this.applyUserInfo = applyUserInfo;
	}

	@Transient
	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	@Transient
	public String getSupplierAccount() {
		return supplierAccount;
	}

	public void setSupplierAccount(String supplierAccount) {
		this.supplierAccount = supplierAccount;
	}
	

	public PsiTransportRevise() {
		super();
	}

	public PsiTransportRevise(Integer id){
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
   

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public Integer getTranOrderId() {
		return tranOrderId;
	}
	
	public void setTranOrderId(Integer tranOrderId) {
		this.tranOrderId = tranOrderId;
	}
	
	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	@OneToMany(mappedBy = "transportRevise", fetch = FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<PsiTransportReviseItem> getItems() {
		return items;
	}

	public void setItems(List<PsiTransportReviseItem> items) {
		this.items = items;
	}

	
	public String getAccountPath() {
		return accountPath;
	}

	public void setAccountPath(String accountPath) {
		this.accountPath = accountPath;
	}

	public String getTranOrderNo() {
		return tranOrderNo;
	}

	public void setTranOrderNo(String tranOrderNo) {
		this.tranOrderNo = tranOrderNo;
	}

	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@ManyToOne()
	@JoinColumn(name = "supplier_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(PsiSupplier supplier) {
		this.supplier = supplier;
	}

	public Float getReviseAmount() {
		return reviseAmount;
	}

	public void setReviseAmount(Float reviseAmount) {
		this.reviseAmount = reviseAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAttchmentPath() {
		return attchmentPath;
	}

	public void setAttchmentPath(String attchmentPath) {
		this.attchmentPath = attchmentPath;
	}

	public String getReviseSta() {
		return reviseSta;
	}

	public void setReviseSta(String reviseSta) {
		this.reviseSta = reviseSta;
	}

	@ManyToOne()
	@JoinColumn(name = "apply_user")
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
	@JoinColumn(name = "sure_user")
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
	@JoinColumn(name = "cancel_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCancelUser() {
		return cancelUser;
	}

	public void setCancelUser(User cancelUser) {
		this.cancelUser = cancelUser;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	@Transient
	public  Float getTotalAmount(){
		Float totalAmount =0f;
		for(PsiTransportReviseItem item:this.items){
			if(item.getRate()!=null &&item.getReviseAmount()!=null){
				totalAmount+=item.getRate()*item.getReviseAmount();
			}
		}
		
		return totalAmount;
			
	}
	
	
	@Transient
	public String getAccount(){
		String rs =  supplier.getAccountByType(accountType);
		if(StringUtils.isNotBlank(rs)){
			return rs.replace(";", "<br/>");
		}
		return "";
	}
	
	
	public void setFilePathAppend(String accountPath) {
		if(StringUtils.isBlank(this.accountPath)){
			this.accountPath = accountPath;
		}else{
			this.accountPath = this.accountPath+","+accountPath;
		}
	}
	
	
}


