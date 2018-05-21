/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "psi_forecast_transport_analyse_order")
public class PsiTransportForecastAnalyseOrder  implements Serializable{
	private static final long serialVersionUID = 1L;
	private 	Integer           id; 		                     // id
	private     String            productName;
	private     String            countryCode;
	private     Integer           peirod;
	private  Integer fbaStock;
	private  Integer oversea;
	private  Integer safeInventory;
	private  String tip;
	private  String fillUpTip;
	private  Date updateDate;
	private  String poInfo;
	private  Integer cnStock;
	private  String salesInfo;
	private  String transInfo;
	private  String promotions;
	
	private  Integer airGap;
	private  Integer seaGap;
	private  Integer poGap;
	
	
	public String getPoInfo() {
		return poInfo;
	}

	public void setPoInfo(String poInfo) {
		this.poInfo = poInfo;
	}

	public String getSalesInfo() {
		return salesInfo;
	}

	public void setSalesInfo(String salesInfo) {
		this.salesInfo = salesInfo;
	}

	public Integer getCnStock() {
		return cnStock;
	}

	public void setCnStock(Integer cnStock) {
		this.cnStock = cnStock;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getFillUpTip() {
		return fillUpTip;
	}

	public void setFillUpTip(String fillUpTip) {
		this.fillUpTip = fillUpTip;
	}

	public Integer getFbaStock() {
		return fbaStock;
	}

	public void setFbaStock(Integer fbaStock) {
		this.fbaStock = fbaStock;
	}

	public Integer getOversea() {
		return oversea;
	}

	public void setOversea(Integer oversea) {
		this.oversea = oversea;
	}

	public Integer getSafeInventory() {
		return safeInventory;
	}

	public void setSafeInventory(Integer safeInventory) {
		this.safeInventory = safeInventory;
	}

	public Integer getPeirod() {
		return peirod;
	}

	public void setPeirod(Integer peirod) {
		this.peirod = peirod;
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getTransInfo() {
		return transInfo;
	}

	public void setTransInfo(String transInfo) {
		this.transInfo = transInfo;
	}

	public String getPromotions() {
		return promotions;
	}

	public void setPromotions(String promotions) {
		this.promotions = promotions;
	}

	public Integer getAirGap() {
		return airGap;
	}

	public void setAirGap(Integer airGap) {
		this.airGap = airGap;
	}

	public Integer getSeaGap() {
		return seaGap;
	}

	public void setSeaGap(Integer seaGap) {
		this.seaGap = seaGap;
	}

	public Integer getPoGap() {
		return poGap;
	}

	public void setPoGap(Integer poGap) {
		this.poGap = poGap;
	}

}


