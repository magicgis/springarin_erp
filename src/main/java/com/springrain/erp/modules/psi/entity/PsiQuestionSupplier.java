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
@Table(name = "psi_question_supplier")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiQuestionSupplier {  
	private	    Integer      		id; 		  	 // id
	private     String              productName;     // 产品名
	private	    PsiSupplier      	supplier; 	     // 供应商
	private 	String		 		orderNo;         // 采购批次
	private 	String		 		event;  	     // 事件
	private 	String		 		consequence;  	 // 后果
	private 	String		 		deal;  	         // 处理
	private 	String		 		punishment;  	 // 惩罚
	private 	String		 		questionType;    // 类型
	private     Date         		questionDate;    // 问题时间
	private     String       		delFlag="0";     // 删除类型
	private     String              filePath;        // 附件
	private     User         		createUser;      // 创建人
	private     Date         		createDate;      // 创建时间
	private     User        		updateUser;      // 编辑人
	private     Date         		updateDate;      // 编辑时间
	private     String         		result;      // 质检结果 显示（OK/NG）(N次)。NG时，（N次）每填写一次重验时数字自动加一，（n）数字2开始，低于2时，括号数字不显示，
	
	public PsiQuestionSupplier() {
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

	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public void setFilePathAppend(String filePath){
		if(StringUtils.isNotEmpty(this.filePath)){
			this.filePath=this.filePath+","+filePath;
		}else{
			this.filePath = filePath;
		}
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
	@JoinColumn(name="supplier_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(PsiSupplier supplier) {
		this.supplier = supplier;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getDeal() {
		return deal;
	}

	public void setDeal(String deal) {
		this.deal = deal;
	}

	public String getPunishment() {
		return punishment;
	}

	public void setPunishment(String punishment) {
		this.punishment = punishment;
	}

	public String getConsequence() {
		return consequence;
	}

	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	
}


