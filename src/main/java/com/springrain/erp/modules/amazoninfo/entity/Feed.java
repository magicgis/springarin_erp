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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 亚马逊帖子上架Entity
 * @author tim
 * @version 2014-08-06
 */
@Entity
@Table(name = "amazoninfo_feed")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Feed implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer id; // 编号
	
	private FeedSubmission feedSubmission;
	
	private String sku;
	
	private String ean;
	
	private Float price;
	
	private Float salePrice;
	
	private Date saleStartDate;
	
	private Date saleEndDate;
	
	private String parentChild;
	
	private String relationshipType;
	
	private String parentSku;
	
	private String subject;
	
	private String description;
	
	private String bulletPoint1;
	
	private String bulletPoint2;
	
	private String bulletPoint3;
	
	private String bulletPoint4;
	
	private String bulletPoint5;
	
	private String genericKeywords1;
	
	private String genericKeywords2;
	
	private String genericKeywords3;
	
	private String genericKeywords4;
	
	private String genericKeywords5;
	

	public Feed() {}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="feed_submission_id")
	public FeedSubmission getFeedSubmission() {
		return feedSubmission;
	}

	public void setFeedSubmission(FeedSubmission feedSubmission) {
		this.feedSubmission = feedSubmission;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Float salePrice) {
		this.salePrice = salePrice;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getSaleStartDate() {
		return saleStartDate;
	}

	public void setSaleStartDate(Date saleStartDate) {
		this.saleStartDate = saleStartDate;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getSaleEndDate() {
		return saleEndDate;
	}

	public void setSaleEndDate(Date saleEndDate) {
		this.saleEndDate = saleEndDate;
	}

	public String getParentChild() {
		return parentChild;
	}

	public void setParentChild(String parentChild) {
		this.parentChild = parentChild;
	}

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	public String getParentSku() {
		return parentSku;
	}

	public void setParentSku(String parentSku) {
		this.parentSku = parentSku;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBulletPoint1() {
		return bulletPoint1;
	}

	public void setBulletPoint1(String bulletPoint1) {
		this.bulletPoint1 = bulletPoint1;
	}

	public String getBulletPoint2() {
		return bulletPoint2;
	}

	public void setBulletPoint2(String bulletPoint2) {
		this.bulletPoint2 = bulletPoint2;
	}

	public String getBulletPoint3() {
		return bulletPoint3;
	}

	public void setBulletPoint3(String bulletPoint3) {
		this.bulletPoint3 = bulletPoint3;
	}

	public String getBulletPoint4() {
		return bulletPoint4;
	}

	public void setBulletPoint4(String bulletPoint4) {
		this.bulletPoint4 = bulletPoint4;
	}

	public String getBulletPoint5() {
		return bulletPoint5;
	}

	public void setBulletPoint5(String bulletPoint5) {
		this.bulletPoint5 = bulletPoint5;
	}

	public String getGenericKeywords1() {
		return genericKeywords1;
	}

	public void setGenericKeywords1(String genericKeywords1) {
		this.genericKeywords1 = genericKeywords1;
	}

	public String getGenericKeywords2() {
		return genericKeywords2;
	}

	public void setGenericKeywords2(String genericKeywords2) {
		this.genericKeywords2 = genericKeywords2;
	}

	public String getGenericKeywords3() {
		return genericKeywords3;
	}

	public void setGenericKeywords3(String genericKeywords3) {
		this.genericKeywords3 = genericKeywords3;
	}

	public String getGenericKeywords4() {
		return genericKeywords4;
	}

	public void setGenericKeywords4(String genericKeywords4) {
		this.genericKeywords4 = genericKeywords4;
	}

	public String getGenericKeywords5() {
		return genericKeywords5;
	}

	public void setGenericKeywords5(String genericKeywords5) {
		this.genericKeywords5 = genericKeywords5;
	}
}


