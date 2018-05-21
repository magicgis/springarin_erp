/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 亚马逊产品目录子项
 * @author tim
 * @version 2016-03-29
 */

@Entity
@Table(name = "amazoninfo_type_catelog_item")
public class ProductCatelogItem {
	
	private 		Integer 		id; 				// id
	private         ProductCatelog productCatelog;
	private         String          country;            // country
	private         String  me;
	private         String  productName;
	private         String  asin;
	private         String  imageUrl;
	private         String  brand;
	private         Float price;
	private         Integer rank;
	private         Float     sales;
	private         Integer   salesVolume;
	private         String firstTo20;
	
	
	public ProductCatelogItem() {
		super();
	}
	
	public ProductCatelogItem(Integer id){
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

	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country= country;
	}

	@ManyToOne
	@JoinColumn(name="catelog_id")
	public ProductCatelog getProductCatelog() {
		return productCatelog;
	}

	public void setProductCatelog(ProductCatelog productCatelog) {
		this.productCatelog = productCatelog;
	}

	public String getMe() {
		return me;
	}

	public void setMe(String me) {
		this.me = me;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getFirstTo20() {
		return firstTo20;
	}

	public void setFirstTo20(String firstTo20) {
		this.firstTo20 = firstTo20;
	}

	public Float getSales() {
		return sales;
	}

	public void setSales(Float sales) {
		this.sales = sales;
	}

	public Integer getSalesVolume() {
		return salesVolume;
	}

	public void setSalesVolume(Integer salesVolume) {
		this.salesVolume = salesVolume;
	}
	
	@Transient
	public String getLink(){
		String suff = country;
		if("uk,jp".contains(country)){
			 suff = "co."+suff;
		}else if("mx".equals(country)){
			suff = "com."+suff;
		}
		return "http://www.amazon."+suff+"/dp/"+asin;
	}

}


