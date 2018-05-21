package com.springrain.erp.modules.custom.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 邮件自动回复Entity
 * @author tim
 * @version 2014-09-24
 */
@Entity
@Table(name = "custom_unsubscribe_email")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UnsubscribeEmail {
	
	private Integer id ;
	private String customEmail;
	private Date   createDate;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCustomEmail() {
		return customEmail;
	}
	public void setCustomEmail(String customEmail) {
		this.customEmail = customEmail;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	
	public UnsubscribeEmail(String customEmail, Date createDate) {
		super();
		this.customEmail = customEmail;
		this.createDate = createDate;
	}
	
	
	public UnsubscribeEmail(){}
	

}
