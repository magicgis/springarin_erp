/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.sys.entity.User;

/**
 * 折扣预警Entity
 * @author Michael
 * @version 2015-08-24
 */
@Entity
@Table(name = "psi_purchase_amount_adjust")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PurchaseAmountAdjust implements Serializable {
	private      static final long serialVersionUID = -2433140471122314283L;
	
	private 		Integer 		id; 			// id
	private         Float           adjustAmount;   // 调整数量
	private     	String      	subject;        // 额外付款主题
	private     	String      	adjustSta="0";  // 调整状态
	private     	User        	createUser;     // 创建人
	private     	Date        	createDate;     // 创建日期
	private         User            updateUser;     // 修改人
	private     	Date        	updateDate;     // 修改日期
	private         User            cancelUser;     // 取消人
	private     	Date        	cancelDate;     // 取消日期
	private         PsiSupplier     supplier;       // 供应商
	private         Integer         paymentId;      // 付款单id
	private         String          currency;       // 货币类型
	private     	String      	remark;         // 备注

	public PurchaseAmountAdjust() {
		super();
	}
	
	public PurchaseAmountAdjust(Integer id){
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


	public Float getAdjustAmount() {
		return adjustAmount;
	}


	public void setAdjustAmount(Float adjustAmount) {
		this.adjustAmount = adjustAmount;
	}


	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getAdjustSta() {
		return adjustSta;
	}


	public void setAdjustSta(String adjustSta) {
		this.adjustSta = adjustSta;
	}


	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	@ManyToOne()
	@JoinColumn(name="cancel_user")
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




	public Integer getPaymentId() {
		return paymentId;
	}


	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}
	
}


