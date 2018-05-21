/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import com.springrain.erp.modules.sys.entity.User;

/**
 * barcode 问题
 * @author Michael
 * @version 2015-06-01
 */  


@Entity
@Table(name = "psi_question_barcode")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiQuestionBarcode {  
	private	    Integer      		id; 		  	 // id
	private	    Integer      		productId; 		 // 产品id
	private 	String		 		productName;	 // 产品名
	private	    Integer      		quantity; 		 // 数量
	private 	String		 		wrongSide;  	 // 错误方
	private 	String		 		transportOrderNo;// 运单号号
	private 	String		 		reason;  	     // 原因
	private     Date         		questionDate;   // 问题时间
	private     String       		delFlag="0";     // 删除类型
	private     User         		createUser;      // 创建人
	private     Date         		createDate;      // 创建时间
	private     User        		updateUser;      // 编辑人
	private     Date         		updateDate;      // 编辑时间
	
	private 	String		 		productNameTemp;	 // 非数据库字段
	public PsiQuestionBarcode() {
		super();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getWrongSide() {
		return wrongSide;
	}

	public void setWrongSide(String wrongSide) {
		this.wrongSide = wrongSide;
	}

	public String getTransportOrderNo() {
		return transportOrderNo;
	}

	public void setTransportOrderNo(String transportOrderNo) {
		this.transportOrderNo = transportOrderNo;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getQuestionDate() {
		return questionDate;
	}

	public void setQuestionDate(Date questionDate) {
		this.questionDate = questionDate;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	@ManyToOne()
	@JoinColumn(name="createUser")
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
	@JoinColumn(name="updateUser")
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

	@Transient
	public String getProductNameTemp() {
		return productNameTemp;
	}

	public void setProductNameTemp(String productNameTemp) {
		this.productNameTemp = productNameTemp;
	}

	
	
}


