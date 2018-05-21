package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "amazoninfo_directory_comment_detail")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProductDirectoryCommentDetail implements Serializable{
	
	private static final long serialVersionUID = -3007854765751058999L;
	
	private     Integer     id;
	private     String      asin;
	private     String      country;
	private     String 		customerName;
	private     String 		customerId;
	private 	String 		reviewId;
	private     Date  		reviewDate;
	private     String 		content;
	private     String 		subject;
	private     String 		star;
	private     Date        createDate;
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public ProductDirectoryCommentDetail() {}
	

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}


	public String getContent() {
		return content;
	}

	public String getStar() {
		return star;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public String getSubject() {
		return subject;
	}
	

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Transient
	public String getCustomerLink(){
		String suff = country;
		if("uk,jp".contains(country)){
			 suff = "co."+suff;
		}else if("mx".equals(country)){
			suff = "com."+suff;
		}
		return "https://www.amazon."+suff+"/gp/pdp/profile/"+customerId;
	}
	
	@Transient
	public String getReviewLink(){
		String suff = country;
		if("uk,jp".contains(country)){
			 suff = "co."+suff;
		}else if("mx".equals(country)){
			suff = "com."+suff;
		}
		return "https://www.amazon."+suff+"/review/"+reviewId;
	}
}
