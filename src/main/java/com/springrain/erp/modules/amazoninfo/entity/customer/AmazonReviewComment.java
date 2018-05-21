package com.springrain.erp.modules.amazoninfo.entity.customer;

import java.net.URLDecoder;
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
import org.springframework.web.util.HtmlUtils;

@Entity
@Table(name = "amazoninfo_review_comment")
public class AmazonReviewComment{

	private Integer id ;
	
    private Date createDate;
    
    private String reviewAsin;

    private String asin;
    
    private String country;
    
    private Date reviewDate;
    
    private String star;
    
    private String subject;
    
    private AmazonCustomer customer;
    
    public AmazonReviewComment() {}
    
    public AmazonReviewComment(Date createDate, String reviewAsin, String asin,
			String country, Date reviewDate, String star, String subject,
			String customerId) {
		super();
		this.createDate = createDate;
		this.reviewAsin = reviewAsin;
		this.asin = asin;
		this.country = country;
		this.reviewDate = reviewDate;
		this.star = star;
		this.subject = subject;
		this.customer = new AmazonCustomer();
		this.customer.setCustomerId(customerId);
	}



	@Id
   	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	
	public String getReviewAsin() {
		return reviewAsin;
	}

	public void setReviewAsin(String reviewAsin) {
		this.reviewAsin = reviewAsin;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	@SuppressWarnings("deprecation")
	public String getSubject() {
		String rs = subject;
		try {
			 rs = URLDecoder.decode(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return HtmlUtils.htmlUnescape(rs).split("@#")[0];
		
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@ManyToOne()
	@JoinColumn(name="customer_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(AmazonCustomer customer) {
		this.customer = customer;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}
	
	@Transient
	public String getLink(){
		String suf = country;
		if("jp,uk".contains(suf)){
			suf = "co."+suf;
		}else if("mx".contains(suf)){
			suf = "com."+suf;
		}
		return "http://www.amazon."+suf+"/review/"+reviewAsin;
	}
}
