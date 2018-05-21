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
 * 亚马逊产品目录
 * @author Michael
 * @version 2015-08-24
 */

@Entity
@Table(name = "amazoninfo_directory")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class ProductDirectory implements Serializable {
	private static final long serialVersionUID = -930949557326791372L;
	private 		Integer 		id; 				// id
	private         String          subject;            // subject
	private 		String 			url;		 		// url
	private         String          country;            // country
	private     	User        	createUser;     	// 创建人
	private     	Date        	createDate;     	// 创建日期
	private     	User        	updateUser;     	// 修改人
	private     	Date        	updateDate;     	// 修改日期
	private     	String      	directorySta="0"; 	// 目录状态
	private 		String 			remark; 	    	// 备注
	private         String          lockSta="0";        // 是否锁定
	private     	Date        	activeDate;     	// 激活日期
	public ProductDirectory() {
		super();
	}
	
	public ProductDirectory(Integer id){
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


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
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


	public String getDirectorySta() {
		return directorySta;
	}


	public void setDirectorySta(String directorySta) {
		this.directorySta = directorySta;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country= country;
	}


	public String getLockSta() {
		return lockSta;
	}


	public void setLockSta(String lockSta) {
		this.lockSta = lockSta;
	}

	public Date getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}
	

}


