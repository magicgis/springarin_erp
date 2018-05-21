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
import com.springrain.erp.modules.sys.entity.User;

/**
 * 折扣预警Entity
 * @author Michael
 * @version 2015-08-24
 */
@Entity
@Table(name = "amazoninfo_evaluate_warning_log")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class EvaluateWarningLog implements Serializable {
	
	private static final long serialVersionUID = -8732837634138973840L;
	private 	Integer 		id; 				// id
	private 	String 			promotionCode; 	// 旧促销码
	private     String          relativeOrderId;    // 相关订单id
	private     Date            createDate;         // 创建时间
	
	private 	EvaluateWarning evaluateWarning; 	// 主表

	public EvaluateWarningLog() {
		super();
	}

	public EvaluateWarningLog(Integer id){
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

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getRelativeOrderId() {
		return relativeOrderId;
	}

	public void setRelativeOrderId(String relativeOrderId) {
		this.relativeOrderId = relativeOrderId;
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	
	

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@ManyToOne()
	@JoinColumn(name="evaluate_warning_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public EvaluateWarning getEvaluateWarning() {
		return evaluateWarning;
	}

	public void setEvaluateWarning(EvaluateWarning evaluateWarning) {
		this.evaluateWarning = evaluateWarning;
	}

	
}


