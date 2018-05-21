package com.springrain.erp.modules.psi.entity;

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
@Table(name = "psi_product_transport_rate")
public class PsiProductTransportRate {
	private Integer id;
	
	private String productName;
	
	private String country;
	
	private Float seaRate;
	
	private Float airRate;
	
	private Float expressRate;
	
	private Float seaPrice;
	
	private Float airPrice;
	
	private Float expressPrice;
	
	private Date updateDate;
	
	private Float avgPrice;

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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Float getSeaRate() {
		return seaRate;
	}

	public void setSeaRate(Float seaRate) {
		this.seaRate = seaRate;
	}

	public Float getAirRate() {
		return airRate;
	}

	public void setAirRate(Float airRate) {
		this.airRate = airRate;
	}

	public Float getExpressRate() {
		return expressRate;
	}

	public void setExpressRate(Float expressRate) {
		this.expressRate = expressRate;
	}

	public Float getSeaPrice() {
		return seaPrice;
	}

	public void setSeaPrice(Float seaPrice) {
		this.seaPrice = seaPrice;
	}

	public Float getAirPrice() {
		return airPrice;
	}

	public void setAirPrice(Float airPrice) {
		this.airPrice = airPrice;
	}

	public Float getExpressPrice() {
		return expressPrice;
	}

	public void setExpressPrice(Float expressPrice) {
		this.expressPrice = expressPrice;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public PsiProductTransportRate() {
		super();
	}

	public PsiProductTransportRate(Integer id,String productName, String country,
			Float seaRate, Float airRate, Float expressRate,
			Float seaPrice, Float airPrice, Float expressPrice) {
		super();
		this.id=id;
		this.productName = productName;
		this.country = country;
		this.seaRate = seaRate;
		this.airRate = airRate;
		this.expressRate = expressRate;
		this.seaPrice = seaPrice;
		this.airPrice = airPrice;
		this.expressPrice = expressPrice;
	}
	
	
	
	public PsiProductTransportRate(String productName, String country,
			Float seaRate, Float airRate, Float expressRate, Float seaPrice,
			Float airPrice, Float expressPrice, Float avgPrice) {
		super();
		this.productName = productName;
		this.country = country;
		this.seaRate = seaRate;
		this.airRate = airRate;
		this.expressRate = expressRate;
		this.seaPrice = seaPrice;
		this.airPrice = airPrice;
		this.expressPrice = expressPrice;
		this.avgPrice = avgPrice;
	}

	public PsiProductTransportRate( String country,
			Float seaRate, Float airRate, Float expressRate,
			Float seaPrice, Float airPrice, Float expressPrice) {
		super();
		this.seaRate = seaRate;
		this.airRate = airRate;
		this.expressRate = expressRate;
		this.seaPrice = seaPrice;
		this.airPrice = airPrice;
		this.expressPrice = expressPrice;
	}
	
	
	@Transient
	public Float getAvgPrice(){
		/*float avgPrice=0f;
		if(seaRate!=null&&seaPrice!=null){
			avgPrice+=seaRate*seaPrice;
		}
		if(airRate!=null&&airPrice!=null){
			avgPrice+=airRate*airPrice;
		}
		if(expressRate!=null&&expressPrice!=null){
			avgPrice+=expressRate*expressPrice;
		}*/
		return avgPrice;
	}

	public void setAvgPrice(Float avgPrice) {
		this.avgPrice = avgPrice;
	}
	
	
}
