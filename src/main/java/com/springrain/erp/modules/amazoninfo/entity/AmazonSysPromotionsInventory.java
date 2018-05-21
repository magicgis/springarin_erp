/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "amazoninfo_sys_promotions_inventory")
public class AmazonSysPromotionsInventory implements Serializable {
	private      static final long serialVersionUID = -2433140471122314283L;
	private 	Integer 	id; 			// id
	private     Date        createDate;     
	private     Date        useDate;    
	private     String      promotionsId;      
	private     String      promotionsCode;    
	private 	String 		country;    
	private 	String 		isActive;  
	
	
	public AmazonSysPromotionsInventory() {
		super();
	}

	public AmazonSysPromotionsInventory(Integer id){
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	@Column(updatable=false)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	@Column(updatable=false)
	public Date getUseDate() {
		return useDate;
	}

	public void setUseDate(Date useDate) {
		this.useDate = useDate;
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

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
}


