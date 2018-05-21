/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

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
import com.google.common.collect.Lists;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 折扣预警Entity
 * @author Michael
 * @version 2015-08-24
 */
@Entity
@Table(name = "amazoninfo_evaluate_warning")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class EvaluateWarning implements Serializable {
	private      static final long serialVersionUID = -2433140471122314283L;
	
	private 		Integer 		id; 			// id
	private 		String 			promotionId; 	// 促销码
	private     	String      	remark;         // 备注
	private 		String 			country;        // 平台
	private     	User        	createUser;     // 创建人
	private     	Date        	createDate;     // 创建日期
	private     	Date        	updateDate;     // 编辑日期
	private     	String      	warningSta="0"; // 折扣预警状态
	private 		String 			promotionCode; 	// 促销码
	private 		String 			result;      	// end结果
	
	private  List<EvaluateWarningLog> logs = Lists.newArrayList();

	public EvaluateWarning() {
		super();
	}

	
	public EvaluateWarning(Integer id){
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

	public String getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	@Column(updatable=false)
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getWarningSta() {
		return warningSta;
	}

	public void setWarningSta(String warningSta) {
		this.warningSta = warningSta;
	}

	
	@OneToMany(mappedBy = "evaluateWarning",fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<EvaluateWarningLog> getLogs() {
		return logs;
	}

	public void setLogs(List<EvaluateWarningLog> logs) {
		this.logs = logs;
	}
	
	
}


