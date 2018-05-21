/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 配件订单付款
 * @author Michael
 * @version 2014-11-21
 */
@Entity
@Table(name = "lc_psi_parts_payment")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiPartsPayment  implements Serializable {
	private static final long serialVersionUID = -768906042720469579L;
	private     Integer      id; 	
	private     String       paymentNo;
	private     Float        paymentAmountTotal;
	private     String       paymentSta;
	private     String       attchmentPath;
	private     String       remark;
	private     String       accountType;
	private     String       currencyType;
	private     String       oldItemIds;
	private     User         sureUser;
	private     Date         sureDate;
	private 	User         updateUser;
	private 	Date         updateDate;
	private     User         applyUser;
	private     Date         applyDate;
	private     Date         createDate;
	private     User         createUser;
	private     Date         cancelDate;          //取消日期
	private     User         cancelUser;          //取消人
	private     PsiSupplier  supplier;
	private    List<LcPsiPartsPaymentItem> items;
	 
	private     String          payFlowNo;        // 付款流水号     yyyyMM0001
	
	public LcPsiPartsPayment() {
		super();
	}

	public LcPsiPartsPayment(Integer id){
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


	public Float getPaymentAmountTotal() {
		return paymentAmountTotal;
	}
	
	
	public String getPayFlowNo() {
		return payFlowNo;
	}

	public void setPayFlowNo(String payFlowNo) {
		this.payFlowNo = payFlowNo;
	}
	

	@OneToMany(mappedBy = "psiPartsPayment",fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Where(clause="del_flag=0")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<LcPsiPartsPaymentItem> getItems() {
		return items;
	}

	public void setItems(List<LcPsiPartsPaymentItem> items) {
		this.items = items;
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
	

	public String getAttchmentPath() {
		return attchmentPath;
	}

	public void setAttchmentPath(String attchmentPath) {
		this.attchmentPath = attchmentPath;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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


