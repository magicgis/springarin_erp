package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 帖子健康列表Entity
 * @author Tim
 * @version 2015-07-08
 */
@Entity
@Table(name = "amazoninfo_posts_health")
public class PostsHealth implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id; 		// 编号
	
	private String country;
	private String sku; 
	private String asin;
	private String productName;
	private String fieldName;
	private String alertType;
	private String currentValue;
	private String lastUpdated;
	private String explanation;
	private Date date;
    private String accountName;
    

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public PostsHealth() {
		super();
	}

	public PostsHealth(String id){
		this();
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	@Transient
	public String getLink() {
		String temp = country;
		if("jp,uk".contains(country)){
			temp = "co."+temp;
		}
		return "http://www.amazon."+temp+"/dp/"+asin;
	}

	public PostsHealth(String country, String sku, String asin,
			String productName, String fieldName, String alertType,
			String currentValue, String lastUpdated, String explanation,
			Date date) {
		super();
		this.country = country;
		this.sku = sku;
		this.asin = asin;
		this.productName = productName;
		this.fieldName = fieldName;
		this.alertType = alertType;
		this.currentValue = currentValue;
		this.lastUpdated = lastUpdated;
		this.explanation = explanation;
		this.date = date;
	}
	
}


