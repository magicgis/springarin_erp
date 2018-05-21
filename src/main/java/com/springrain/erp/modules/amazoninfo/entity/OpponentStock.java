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

@Entity
@Table(name = "amazoninfo_opponent_stock")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OpponentStock implements Serializable{
	 private static final long serialVersionUID = 8198464790839433398L;
	 private  	Integer 	id;           // id
	 private  	String  	asin;      	  // asin
	 private  	String  	country;      // country 
	 private  	Integer 	quantity;     // 库存数
	 private  	Date    	dataDate;     // 扫描时间
	 private    Integer     diffQuantity; // 差值数
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}

	public Integer getDiffQuantity() {
		return diffQuantity;
	}

	public void setDiffQuantity(Integer diffQuantity) {
		this.diffQuantity = diffQuantity;
	}
	
	public OpponentStock(){}
	
	
	
	public OpponentStock(String asin, String country, Integer quantity,	Date dataDate,Integer diffQuantity) {
		super();
		this.asin = asin;
		this.country = country;
		this.quantity = quantity;
		this.dataDate = dataDate;
		this.diffQuantity=diffQuantity;
	}

}
