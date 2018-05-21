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
import org.springframework.transaction.annotation.Transactional;

/**
 * 跟帖Entity
 * @author Michael
 * @version 2017-06-26
 */
@Entity
@Table(name = "amazoninfo_follow_seller")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class AmazonFollowSeller implements Serializable{
	private static final long serialVersionUID = 2735546638775535745L;
	private     Integer     id;         	//id
	private 	Date 		dataDate;		//数据日期
	private     Integer		quantity;		//抓到次数
	private     String      sellerName; 	//卖家名字
	private     String      a;          	//超链接a的所有内容
	private     String      country;    	//国家
	private     String      productTitle;   //产品title
	private     String      asin;       
	private     String      productName;
	
	private 	Date 		updateDate;		//非数据库字段
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getDataDate() {
		return dataDate;
	}
	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getA() {
		return a;
	}
	public void setA(String a) {
		this.a = a;
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
	public String getProductTitle() {
		return productTitle;
	}
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
	
	@Transient
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public AmazonFollowSeller(){};
	
	public AmazonFollowSeller(Date dataDate, Integer quantity,String sellerName, String a, String country, String productTitle,	String asin, String productName) {
		super();
		this.dataDate = dataDate;
		this.quantity = quantity;
		this.sellerName = sellerName;
		this.a = a;
		this.country = country;
		this.productTitle = productTitle;
		this.asin = asin;
		this.productName = productName;
	}
	
	

}
