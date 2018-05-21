/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.oa.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import com.springrain.erp.common.persistence.IdEntity;
import com.springrain.erp.modules.sys.utils.DictUtils;

/**
 * 采购
 */
@Entity
@Table(name = "oa_buydevice")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BuyDevice extends IdEntity<BuyDevice> {
	
	private static final long serialVersionUID = 1L;

	private String deviceType;	// 设备类型
	private String reason; 	// 理由
	private String name;
	private Float price;
	
	private String processInstanceId; // 流程实例编号
	private String processStatus; //流程状态
	
	private boolean pass;
	private boolean audit;
	private String auditRemarks; 

	public BuyDevice() {
		super();
	}

	public BuyDevice(String id){
		this();
		this.id = id;
	}
	
	@Length(min=1, max=255)
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	@Transient
	public String getDeviceTypeDictLabel() {
		return DictUtils.getDictLabel(deviceType, "officeDeviceType", "");
	}
	
}


