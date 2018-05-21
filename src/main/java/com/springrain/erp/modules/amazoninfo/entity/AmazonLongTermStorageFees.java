package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "amazoninfo_long_term_storage_fees")
public class AmazonLongTermStorageFees implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id; 		
	private String country; 	
	private Date snapshotDate;
	private String asin;
	private String fnsku;
	private String productName;
	private String sku;
	private String conditionType;
	private Integer qtyChargedTwelfthMoLongTermStorageFee;
	private Float perUnitVolume;
	private String currency;
	private Float twelfthMoLongTermsStorageFee;
	private Integer qtyChargedSixMoLongTermStorageFee;
	private Float sixMoLongTermsStorageFee;
	private Date updateDate;
	private String accountName;
	
	public AmazonLongTermStorageFees() {
		super();
	}
	
	
	
	public AmazonLongTermStorageFees(String country, Date snapshotDate,
			String asin, String fnsku, String productName, String sku,
			String conditionType, Integer qtyChargedTwelfthMoLongTermStorageFee,
			Float perUnitVolume, String currency,
			Float twelfthMoLongTermsStorageFee,
			Integer qtyChargedSixMoLongTermStorageFee,
			Float sixMoLongTermsStorageFee, Date updateDate) {
		super();
		this.country = country;
		this.snapshotDate = snapshotDate;
		this.asin = asin;
		this.fnsku = fnsku;
		this.productName = productName;
		this.sku = sku;
		this.conditionType = conditionType;
		this.qtyChargedTwelfthMoLongTermStorageFee = qtyChargedTwelfthMoLongTermStorageFee;
		this.perUnitVolume = perUnitVolume;
		this.currency = currency;
		this.twelfthMoLongTermsStorageFee = twelfthMoLongTermsStorageFee;
		this.qtyChargedSixMoLongTermStorageFee = qtyChargedSixMoLongTermStorageFee;
		this.sixMoLongTermsStorageFee = sixMoLongTermsStorageFee;
		this.updateDate = updateDate;
	}



	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getAsin() {
		return asin;
	}


	public void setAsin(String asin) {
		this.asin = asin;
	}


	public String getFnsku() {
		return fnsku;
	}


	public void setFnsku(String fnsku) {
		this.fnsku = fnsku;
	}


	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Date getSnapshotDate() {
		return snapshotDate;
	}

	public void setSnapshotDate(Date snapshotDate) {
		this.snapshotDate = snapshotDate;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}


	public String getConditionType() {
		return conditionType;
	}


	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}



	public Float getPerUnitVolume() {
		return perUnitVolume;
	}

	public void setPerUnitVolume(Float perUnitVolume) {
		this.perUnitVolume = perUnitVolume;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}



	public Integer getQtyChargedTwelfthMoLongTermStorageFee() {
		return qtyChargedTwelfthMoLongTermStorageFee;
	}



	public void setQtyChargedTwelfthMoLongTermStorageFee(
			Integer qtyChargedTwelfthMoLongTermStorageFee) {
		this.qtyChargedTwelfthMoLongTermStorageFee = qtyChargedTwelfthMoLongTermStorageFee;
	}



	public Float getTwelfthMoLongTermsStorageFee() {
		return twelfthMoLongTermsStorageFee;
	}



	public void setTwelfthMoLongTermsStorageFee(Float twelfthMoLongTermsStorageFee) {
		this.twelfthMoLongTermsStorageFee = twelfthMoLongTermsStorageFee;
	}



	public Integer getQtyChargedSixMoLongTermStorageFee() {
		return qtyChargedSixMoLongTermStorageFee;
	}



	public void setQtyChargedSixMoLongTermStorageFee(
			Integer qtyChargedSixMoLongTermStorageFee) {
		this.qtyChargedSixMoLongTermStorageFee = qtyChargedSixMoLongTermStorageFee;
	}



	public Float getSixMoLongTermsStorageFee() {
		return sixMoLongTermsStorageFee;
	}



	public void setSixMoLongTermsStorageFee(Float sixMoLongTermsStorageFee) {
		this.sixMoLongTermsStorageFee = sixMoLongTermsStorageFee;
	}



	public String getAccountName() {
		return accountName;
	}



	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	
	
}


