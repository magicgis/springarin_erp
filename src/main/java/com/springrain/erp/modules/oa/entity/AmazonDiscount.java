package com.springrain.erp.modules.oa.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import com.springrain.erp.common.persistence.IdEntity;

/**
 * 打折
 */
@Entity
@Table(name = "oa_amazondiscount")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AmazonDiscount extends IdEntity<AmazonDiscount> {
	
	private static final long serialVersionUID = 1L;

	private Date startDate;	
	
	private Date endDate;	
	
	private String discountScope;
	
	private Float price;
	private String reason; 	// 理由
	
	private String processInstanceId; // 流程实例编号
	private String processStatus; //流程状态
	
	private boolean pass;
	private boolean audit;
	private String auditRemarks; 

	public AmazonDiscount() {
		super();
	}

	public AmazonDiscount(String id){
		this();
		this.id = id;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getDiscountScope() {
		return discountScope;
	}

	public void setDiscountScope(String discountScope) {
		this.discountScope = discountScope;
	}

	@Length(min=1, max=255)
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	@Transient
	public boolean isPass() {
		return pass;
	}

	@Transient
	public void setPass(boolean pass) {
		this.pass = pass;
	}

	@Transient
	public String getAuditRemarks() {
		return auditRemarks;
	}

	@Transient
	public void setAuditRemarks(String auditRemarks) {
		this.auditRemarks = auditRemarks;
	}

	@Transient
	public boolean isAudit() {
		return audit;
	}

	@Transient
	public void setAudit(boolean audit) {
		this.audit = audit;
	}
	
}


