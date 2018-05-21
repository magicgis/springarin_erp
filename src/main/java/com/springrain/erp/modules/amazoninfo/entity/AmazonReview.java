package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;

@Entity
@Table(name = "amazoninfo_reviews_out")
public class AmazonReview implements Serializable{
	
	private static final long serialVersionUID = -2433140471122314283L;
	
	private 	Integer id; 
	
	private     String customerName;
	
	private     String customerId;
	
	private     ProductReviewMonitor reviewMonitor;
	
	private 	String reviewId;
	
	private     Date  reviewDate;
	
	private     Date lastUpdateDate;
	
	private     String state="1";
	
	//累计变动
	private     String contentShow;
	
	private     String subjectShow;
	
	private     String starShow;
	
	private     String content;
	
	private     String subject;
	
	private     String star;
	
	public AmazonReview() {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	@ManyToOne()
	@JoinColumn(name="monitor_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public ProductReviewMonitor getReviewMonitor() {
		return reviewMonitor;
	}

	public void setReviewMonitor(ProductReviewMonitor reviewMonitor) {
		this.reviewMonitor = reviewMonitor;
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

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getContentShow() {
		return contentShow;
	}

	public void setContentShow1(String contentShow) {
		if(StringUtils.isNotBlank(this.contentShow)){
			this.contentShow += ("<br/> Update "+DateUtils.getDate()+"<br/>"+contentShow);
			if(this.contentShow.length()>9800){
				this.contentShow = this.contentShow.substring(0,9800)+"...超出限制长度...";
			}
			this.lastUpdateDate = new Date();
		}else{
			this.contentShow = contentShow;
		}
	}

	public String getSubjectShow() {
		return subjectShow;
	}

	public void setSubjectShow1(String subjectShow) {
		if(StringUtils.isNotBlank(this.subjectShow)){
			this.subjectShow += ("<br/> Update "+DateUtils.getDate()+"<br/>"+subjectShow);
			this.lastUpdateDate = new Date();
		}else{
			this.subjectShow = subjectShow;
		}
	}

	public String getStarShow() {
		return starShow;
	}

	public void setStarShow1(String starShow) {
		if(StringUtils.isNotBlank(this.starShow)){
			this.starShow += ("|"+starShow);
			this.lastUpdateDate = new Date();
		}else{
			this.starShow = starShow;
		}
	}

	public String getContent() {
		return content;
	}

	public void setContent1(String content) {
		if(!content.equals(this.content)){
			this.content = content;
			setContentShow1(content);
		}
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject1(String subject) {
		if(!subject.equals(this.subject)){
			this.subject = subject;
			setSubjectShow1(subject);
		}
	}

	public String getStar() {
		return star;
	}

	public void setStar1(String star) {
		if(!star.equals(this.star)){
			this.star = star;
			setStarShow1(star);
		}
	}

	public void setContentShow(String contentShow) {
		this.contentShow = contentShow;
	}

	public void setSubjectShow(String subjectShow) {
		this.subjectShow = subjectShow;
	}

	public void setStarShow(String starShow) {
		this.starShow = starShow;
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
	
	@Transient
	public String getCustomerLink(){
		String country =  reviewMonitor.getCountry();
		String suff = country;
		if("uk,jp".contains(country)){
			 suff = "co."+suff;
		}else if("mx".equals(country)){
			suff = "com."+suff;
		}
		return "http://www.amazon."+suff+"/gp/pdp/profile/"+customerId;
	}
	
	@Transient
	public String getReviewLink(){
		String country =  reviewMonitor.getCountry();
		String suff = country;
		if("uk,jp".contains(country)){
			 suff = "co."+suff;
		}else if("mx".equals(country)){
			suff = "com."+suff;
		}
		return "http://www.amazon."+suff+"/review/"+reviewId;
	}
}
