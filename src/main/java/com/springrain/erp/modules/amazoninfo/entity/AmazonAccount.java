package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 亚马逊后台账号信息Entity
 * @author Tim
 * @version 2015-01-14
 */
@Entity
@Table(name = "amazoninfo_amazon_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AmazonAccount implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String id; 		// 编号
	private String country;
	private String accountEmail;
	private String accountId;
	private User lastUpdateUser;
	private User createUser;
	private Date lastUpdateDate;
	private String code;
	private String statu;//申请,等待校验码,账号已创建,授权中,已授权,正在删除
	private String rights;
	private String delFlag="0";
	private LinkedHashMap<String,String> rightMap;
	
	public AmazonAccount() {
		super();
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAccountEmail() {
		return accountEmail;
	}
	public void setAccountEmail(String accountEmail) {
		this.accountEmail = accountEmail;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getLastUpdateUser() {
		return lastUpdateUser;
	}
	public void setLastUpdateUser(User lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}
	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getStatu() {
		return statu;
	}
	public void setStatu(String statu) {
		this.statu = statu;
	}
	
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	
	@Transient
	public LinkedHashMap<String, String> getRightMap() {
		return rightMap;
	}
	public void setRightMap(LinkedHashMap<String, String> rightMap) {
		this.rightMap = rightMap;
	}
	public AmazonAccount(String country) {
		super();
		this.country = country;
	}
	
	
}


