package com.springrain.erp.modules.amazoninfo.entity;

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
@Table(name = "amazoninfo_posts_relationship_change")
public class AmazonPostsRelationshipChange{
	
	private Integer id;
	private String productName;
	private String size;
	private String color;
	private AmazonPostsRelationshipFeed amazonPostsRelationshipFeed;
    private String sku;
    private String parentSku;//原sku 0:无父 1：有父，erp找sku，（自己填） 
	
    private String catalogType1;
    
    private String catalogType2;
    
    
    @Transient
	public String getCatalogType1() {
		return catalogType1;
	}

	public void setCatalogType1(String catalogType1) {
		this.catalogType1 = catalogType1;
	}
	@Transient
	public String getCatalogType2() {
		return catalogType2;
	}

	public void setCatalogType2(String catalogType2) {
		this.catalogType2 = catalogType2;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}
	
	public String getParentSku() {
		return parentSku;
	}

	public void setParentSku(String parentSku) {
		this.parentSku = parentSku;
	}

	@ManyToOne()
	@JoinColumn(name="feed_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonPostsRelationshipFeed getAmazonPostsRelationshipFeed() {
		return amazonPostsRelationshipFeed;
	}

	public void setAmazonPostsRelationshipFeed(
			AmazonPostsRelationshipFeed amazonPostsRelationshipFeed) {
		this.amazonPostsRelationshipFeed = amazonPostsRelationshipFeed;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
