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
 * 供应商税率调整Entity
 * @author Michael
 * @version 2015-08-24
 */
@Entity
@Table(name = "psi_supplier_tax_adjust")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiSupplierTaxAdjust implements Serializable {
	
	private static final long serialVersionUID = 7103844763449810099L;
	private 		Integer 		id; 			// id
	private         PsiSupplier     supplier;       // 供应商
	private         Integer         oldTax;         // 原税率
	private         Integer         tax;            // 改后税率
	private     	String      	remark;         // 备注
    private         String          filePath;       // 凭证     
	private     	String      	adjustSta="0";  // 调整状态
	
	private     	User        	createUser;     // 创建人
	private     	Date        	createDate;     // 创建日期
	private         User            cancelUser;     // 取消人
	private     	Date        	cancelDate;     // 取消日期
	private         User            reviewUser;     // 审核人
	private     	Date        	reviewDate;     // 审核日期
	
	public PsiSupplierTaxAdjust() {
		super();
	}
	
	public PsiSupplierTaxAdjust(Integer id){
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

	public Integer getOldTax() {
		return oldTax;
	}

	public void setOldTax(Integer oldTax) {
		this.oldTax = oldTax;
	}

	public Integer getTax() {
		return tax;
	}

	public void setTax(Integer tax) {
		this.tax = tax;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@ManyToOne()
	@JoinColumn(name="review_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getReviewUser() {
		return reviewUser;
	}

	public void setReviewUser(User reviewUser) {
		this.reviewUser = reviewUser;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}


	
}


