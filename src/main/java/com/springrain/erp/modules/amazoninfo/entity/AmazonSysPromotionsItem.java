/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;

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

@Entity
@Table(name = "amazoninfo_sys_promotions_item")
public class AmazonSysPromotionsItem implements Serializable {
	private      static final long serialVersionUID = -2433140471122314283L;
	private 	Integer 	id; 			// id
	private 	String 		promotionsId; 	
	private 	String 		promotionsCode; 	
	private     String      email;
	private     String      customId;
	private     String      country;
	private     AmazonSysPromotions amazonSysPromotions;
	private     String      amazonOrderId;
	private     String      productName;
	
	private     String        createDate;
	
	@Transient
	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getAmazonOrderId() {
		return amazonOrderId;
	}

	public void setAmazonOrderId(String amazonOrderId) {
		this.amazonOrderId = amazonOrderId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@ManyToOne()
	@JoinColumn(name="track_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonSysPromotions getAmazonSysPromotions() {
		return amazonSysPromotions;
	}

	public void setAmazonSysPromotions(
			AmazonSysPromotions amazonSysPromotions) {
		this.amazonSysPromotions = amazonSysPromotions;
	}

	public AmazonSysPromotionsItem() {
		super();
	}

	public AmazonSysPromotionsItem(Integer id){
		this();
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPromotionsId() {
		return promotionsId;
	}

	public void setPromotionsId(String promotionsId) {
		this.promotionsId = promotionsId;
	}

	public String getPromotionsCode() {
		return promotionsCode;
	}

	public void setPromotionsCode(String promotionsCode) {
		this.promotionsCode = promotionsCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}
	
	
}


