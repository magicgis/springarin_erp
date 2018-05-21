package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "psi_sku")
public class PsiSku implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private 	Integer 		id;
	private 	PsiBarcode 		barcode;
	private 	String 			useBarcode;
	private 	String 			sku;
	private 	String 			asin;
	private 	Integer 		productId;
	private 	String 			country;
	private 	String 			color;
	private 	String 			productName;
	private 	String 			delFlag="0";
	private     User            updateUser;
	private     Integer         quantity;
	private     String          barcodeType;
	private     String          accountName;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="barcode")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiBarcode getBarcode() {
		return barcode;
	}

	public void setBarcode(PsiBarcode barcode) {
		this.barcode = barcode;
	}

	public String getUseBarcode() {
		return useBarcode;
	}

	public void setUseBarcode(String useBarcode) {
		this.useBarcode = useBarcode;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getAsin() {
		return asin;
	}
	public void setAsin(String asin) {
		this.asin = asin;
	}
	
	
	@ManyToOne()
	@JoinColumn(name="update_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
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
	
	public PsiSku(){};
	public PsiSku(String sku,Integer productId, String country,	String color, String productName) {
		super();
		this.sku = sku;
		this.productId = productId;
		this.country = country;
		this.color = color;
		this.productName = productName;
	}
	
	
	
	public PsiSku(String sku, String asin, String country, String color,
			String productName, Integer quantity) {
		super();
		this.sku = sku;
		this.asin = asin;
		this.country = country;
		this.color = color;
		this.productName = productName;
		this.quantity = quantity;
	}

	@Transient
	public String getNameWithColor(){
		if(StringUtils.isNotBlank(color)){
			return productName+"_"+color;
		}
		return productName;
	}

	@Transient
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	
	@Transient
	public String getProductColorCountry(){
		if(StringUtils.isEmpty(color)){
			return productName+"_"+country;
		}else{
			return productName+"_"+color+"_"+country;
		}
	}

	@Transient
	public String getBarcodeType() {
		return barcodeType;
	}

	public void setBarcodeType(String barcodeType) {
		this.barcodeType = barcodeType;
	}
	
	
	
}
