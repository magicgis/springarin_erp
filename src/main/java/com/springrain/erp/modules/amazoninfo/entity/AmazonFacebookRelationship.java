package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "amazoninfo_facebook_relationship")
public class AmazonFacebookRelationship implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id; 
	private String market;
	private Date date;
	private String productLine;
	private String gender;
	private String audience;
	private String age;
	private String placement;
	private String asinOnAd;
	private String adId;
	private String trackingId;
	private String preView;
	private String delFlag;
	private String product;
	
	private Date endDate;
	
    @Transient
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public AmazonFacebookRelationship() {
		super();
	}

	public AmazonFacebookRelationship(String market, Date date,
			String productLine,String product, String gender, String audience, String age,
			String placement, String asinOnAd, String adId, String trackingId,
			String preView, String delFlag) {
		super();
		this.market = market;
		this.date = date;
		this.productLine = productLine;
		this.product=product;
		this.gender = gender;
		this.audience = audience;
		this.age = age;
		this.placement = placement;
		this.asinOnAd = asinOnAd;
		this.adId = adId;
		this.trackingId = trackingId;
		this.preView = preView;
		this.delFlag = delFlag;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	
	
	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getProductLine() {
		return productLine;
	}

	public void setProductLine(String productLine) {
		this.productLine = productLine;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAudience() {
		return audience;
	}

	public void setAudience(String audience) {
		this.audience = audience;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getPlacement() {
		return placement;
	}

	public void setPlacement(String placement) {
		this.placement = placement;
	}

	public String getAsinOnAd() {
		return asinOnAd;
	}

	public void setAsinOnAd(String asinOnAd) {
		this.asinOnAd = asinOnAd;
	}

	public String getAdId() {
		return adId;
	}

	public void setAdId(String adId) {
		this.adId = adId;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public String getPreView() {
		return preView;
	}

	public void setPreView(String preView) {
		this.preView = preView;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

}


