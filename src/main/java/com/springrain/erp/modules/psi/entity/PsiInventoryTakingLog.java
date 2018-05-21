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
@Table(name = "psi_inventory_taking_log")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiInventoryTakingLog {  
	private	    Integer      		id; 		  	 // id
	private     Date         		takingDate;      // 盘点时间
	private     String         		result;          // 
	private     String         		remark;          // 
	private     String              filePath;        // 附件
	
	private     String              delFlag;         // 删除标记
	private     User         		createUser;      // 创建人
	private     Date         		createDate;      // 创建时间
	
	public PsiInventoryTakingLog() {
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

	public Date getTakingDate() {
		return takingDate;
	}

	public void setTakingDate(Date takingDate) {
		this.takingDate = takingDate;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

}


