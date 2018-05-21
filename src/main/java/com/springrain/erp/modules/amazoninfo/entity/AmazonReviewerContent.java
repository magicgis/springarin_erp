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

/**
 * 亚马逊top1W评论人评论详情
 * @author lee
 * @date 2015-11-30
 */
@Entity
@Table(name = "amazoninfo_reviewer_content")
public class AmazonReviewerContent implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private 	Integer 		id;
	private 	AmazonReviewer 	reviewer;
	private		String 			reviewId;	//评论id
	private 	String 			reviewTitle;//评论title
	private     String          productTitle;//产品title
	private 	Integer 		star;		//评分
	private 	String 			brandType;	//品牌类型
	private     String          productType;//产品类型
	private 	Date 			reviewDate;	//评论时间
	private     String          asin;       //asin
	private String stuffix;	//评论所属平台网站后缀
	
	public AmazonReviewerContent() {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="ama_reviewer_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonReviewer getReviewer() {
		return reviewer;
	}

	public void setReviewer(AmazonReviewer reviewer) {
		this.reviewer = reviewer;
	}

	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}

	public String getReviewTitle() {
		return reviewTitle;
	}

	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public Integer getStar() {
		return star;
	}

	public void setStar(Integer star) {
		this.star = star;
	}

	public String getBrandType() {
		return brandType;
	}

	public void setBrandType(String brandType) {
		this.brandType = brandType;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	
	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public AmazonReviewerContent(AmazonReviewer reviewer, String reviewId,String reviewTitle, String productTitle, Integer star,String brandType, String productType, Date reviewDate,String asin) {
		super();
		this.reviewer = reviewer;
		this.reviewId = reviewId;
		this.reviewTitle = reviewTitle;
		this.productTitle = productTitle;
		this.star = star;
		this.brandType = brandType;
		this.productType = productType;
		this.reviewDate = reviewDate;
		this.asin=asin;
	}

	@Transient
	public String getStuffix() {
		if (this.reviewer != null) {
			stuffix = this.reviewer.getCountry();
			if ("jp,uk".contains(stuffix)) {
				stuffix = "co." + stuffix;
			} else if ("mx".equals(stuffix)) {
				stuffix = "com." + stuffix;
			}
		}
		return stuffix;
	}
	
}
