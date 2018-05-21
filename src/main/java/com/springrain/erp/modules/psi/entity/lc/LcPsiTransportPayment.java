/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 运单付款表Entity
 * @author Michael
 * @version 2015-01-21
 */
@Entity
@Table(name = "lc_psi_transport_payment")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiTransportPayment {
	private 	Integer 			id; 			// id
	private		String              paymentNo;  	// 付款编号
	private		PsiSupplier         supplier; 		// 承运商
	private		String              paymentSta; 	// 付款状态
	private		String				accountType;	// 账号类型
	private		String  		    attchmentPath;  // 凭证地址
	private		String  		    supplierAttchmentPath;  // 凭证地址
	private		Float               paymentAmount;  // 付款总金额
	private     String              currency;   	// 货币类型
	private		String              remark;     	// 备注
	private		Date				sureDate;   	// 确认时间
	private		User				sureUser;		// 确认人
	private		Date				updateDate; 	// 更新时间
	private		User				updateUser; 	// 更新人
	private		User                applyUser;  	// 申请人
	private		Date				applyDate;  	// 申请时间
	private		User                cancelUser; 	// 取消人
	private		Date				cancelDate; 	// 取消时间
	private		Date				createDate; 	// 创建日期
	private		User				createUser; 	// 创建人
	private		Float               beforeAmount;   // 转换前总金额
	private     Float               rate;           // 汇率 
	private     String              oldItemIds;     // 老itemid字符串
	private     String              supplierCostPath;   //供应商费用明细
	
	private     String              payFlowNo;           // 付款流水号     yyyyMM0001
	
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

	@Transient
	public String getApplyUserInfo() {
		return applyUserInfo;
	}

	public void setApplyUserInfo(String applyUserInfo) {
		this.applyUserInfo = applyUserInfo;
	}

	@Transient
	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	@Transient
	public String getMoneyInfo() {
		return moneyInfo;
	}

	public void setMoneyInfo(String moneyInfo) {
		this.moneyInfo = moneyInfo;
	}
	@Transient
	public String getTransportNoRemark() {
		return transportNoRemark;
	}

	@Transient
	public String getFlowInfo() {
		return flowInfo;
	}

	public void setFlowInfo(String flowInfo) {
		this.flowInfo = flowInfo;
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

	private     List<LcPsiTransportPaymentItem> items;
	
	
	public LcPsiTransportPayment() {
		super();
	}

	public LcPsiTransportPayment(Integer id){
		this();
		this.id = id;
	}
	
	@Transient
	public String getSureTime() {
		return sureTime;
	}

	public void setSureTime(String sureTime) {
		this.sureTime = sureTime;
	}

	@Transient
	public String getOldItemIds() {
		return oldItemIds;
	}

	public void setOldItemIds(String oldItemIds) {
		this.oldItemIds = oldItemIds;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getPayFlowNo() {
		return payFlowNo;
	}

	public void setPayFlowNo(String payFlowNo) {
		this.payFlowNo = payFlowNo;
	}

	@OneToMany(mappedBy = "transportPayment", fetch = FetchType.LAZY)
	@OrderBy(value="tranOrderId")
	@Where(clause = "del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<LcPsiTransportPaymentItem> getItems() {
		return items;
	}

	public void setItems(List<LcPsiTransportPaymentItem> items) {
		this.items = items;
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

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
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

	
	@Transient
	public String getSupplierCostPath() {
		return supplierCostPath;
	}

	public void setSupplierCostPath(String supplierCostPath) {
		this.supplierCostPath = supplierCostPath;
	}
	
	public void setSupplierCostPathAppend(String supplierCostPath){
		if(StringUtils.isNotEmpty(this.supplierCostPath)){
			this.supplierCostPath=this.supplierCostPath+","+supplierCostPath;
		}else{
			this.supplierCostPath = supplierCostPath;
		}
		
	}

	public String getPaymentSta() {
		return paymentSta;
	}

	public void setPaymentSta(String paymentSta) {
		this.paymentSta = paymentSta;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@ManyToOne()
	@JoinColumn(name = "create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}


	public String getAttchmentPath() {
		return attchmentPath;
	}

	public void setAttchmentPath(String attchmentPath) {
		if(StringUtils.isBlank(this.attchmentPath)){
			this.attchmentPath = attchmentPath;
		}else{
			this.attchmentPath=this.attchmentPath+","+attchmentPath;
		}
	}
	
	public String getSupplierAttchmentPath() {
		return supplierAttchmentPath;
	}

	public void setSupplierAttchmentPath(String supplierAttchmentPath) {
		if(StringUtils.isBlank(this.supplierAttchmentPath)){
			this.supplierAttchmentPath = supplierAttchmentPath;
		}else{
			this.supplierAttchmentPath=this.supplierAttchmentPath+","+supplierAttchmentPath;
		}
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getSureDate() {
		return sureDate;
	}

	public Float getBeforeAmount() {
		return beforeAmount;
	}

	public void setBeforeAmount(Float beforeAmount) {
		this.beforeAmount = beforeAmount;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public void setSureDate(Date sureDate) {
		this.sureDate = sureDate;
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

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@ManyToOne()
	@JoinColumn(name = "update_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
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

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	@Transient
	public String getAccount(){
		String rs =  supplier.getAccountByType(accountType);
		if(StringUtils.isNotBlank(rs)){
			return rs.replace(";", "<br/>");
		}
		return "";
	}
	
	@Transient
	public String getAccountNoBr(){
		return supplier.getAccountByType(accountType);
	}
}


