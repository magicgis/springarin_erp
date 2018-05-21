package com.springrain.erp.modules.solr.entity;

import org.apache.solr.client.solrj.beans.Field;

import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;

public class Index {

	@Field
	private String id;
	
	@Field
	private String subject;
	
	@Field
	private String describe;
	
	@Field
	private String asin;
	
	@Field
	private String country;
	
	@Field
	private String dataDate;
	
	@Field
	private String link;
	
	@Field
	private String type;
	
	@Field
	private String orderNo;
	
	@Field
	private String userName;
	
	@Field
	private String productName;
	
	@Field
	private String email;
	
	@Field
	private String productType;
	
	@Field
	private String customId;
	
	@Field
	private String image;
	
	@Field
	private Integer goodReview;
	
	@Field
	private Integer badReview;
	
	@Field
	private Integer buyTimes;
	
	@Field
	private Float salePrice;
	
	@Field
	private String status;	//订单状态

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescribe() {
		if (StringUtils.isNotEmpty(describe) && describe.length() > 200) {
			String temp = describe.replace("<font color='red'>", "");
			temp = temp.replace("</font>", "");
			if (temp.length() > 200) {
				describe = describe.substring(0, 200) + "...";
			}
		}
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getCountry() {
		try {
			if (StringUtils.isEmpty(country) && StringUtils.isNotEmpty(email)) {
				String[] str = email.split("\\.");
				return str[str.length-1];
			}
		} catch (Exception e) {}
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getGoodReview() {
		return goodReview;
	}

	public void setGoodReview(Integer goodReview) {
		this.goodReview = goodReview;
	}

	public Integer getBadReview() {
		return badReview;
	}

	public void setBadReview(Integer badReview) {
		this.badReview = badReview;
	}

	public Integer getBuyTimes() {
		return buyTimes;
	}

	public void setBuyTimes(Integer buyTimes) {
		this.buyTimes = buyTimes;
	}

	public Float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Float salePrice) {
		this.salePrice = salePrice;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTypeDetail() {
		try {
			if (type.toLowerCase().contains("email")) {
				String detail = id.split("_")[1];
				if (detail.toLowerCase().contains("send")) {
					return "SendEmail";
				} else {
					return "ReceiveEmail";
				}
			}
		} catch (Exception e) {}
		return "";
	}
	
}

