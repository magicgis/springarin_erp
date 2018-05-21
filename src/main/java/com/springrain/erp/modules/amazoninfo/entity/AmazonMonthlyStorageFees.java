package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "amazoninfo_monthly_storage_fees")
public class AmazonMonthlyStorageFees implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id; 		
	private String country; 	
	private String asin;
	private String fnsku;
	private String productName;
	private String fulfillmentCenter;
	private String countryCode;
	private Float longestSide;
	private Float medianSide;
	private Float shortestSide;
	private String measurementUnits;
	private Float weight;
	private String weightUnits;
	private Float itemVolume;
	private String volumeUnits;
	private Float averageQuantityOnHand;
	private Float averageQuantityPendingRemoval;
	private Float estimatedTotalItemVolume;
	private String month;
	private Float storageRate;
	private String currency;
	private Float estimatedMonthlyStorageFee;
	private Date updateDate;
	private String productSizeTier;
	
	private String totalFee;
    private String shortestSideNew;
    private String medianSideNew;
    private String longestSideNew;
    private String totalLongFee;
    private String totalMonthFee;
	private String accountName;
    
	public AmazonMonthlyStorageFees() {
		super();
	}
	
	public AmazonMonthlyStorageFees(String country, String asin, String fnsku,
			String productName, String fulfillmentCenter, String countryCode,
			Float longestSide, Float medianSide, Float shortestSide,
			String measurementUnits, Float weight, String weightUnits,
			Float itemVolume, String volumeUnits, Float averageQuantityOnHand,
			Float averageQuantityPendingRemoval,
			Float estimatedTotalItemVolume, String month, Float storageRate,
			String currency, Float estimatedMonthlyStorageFee, Date updateDate,String productSizeTier) {
		super();
		this.country = country;
		this.asin = asin;
		this.fnsku = fnsku;
		this.productName = productName;
		this.fulfillmentCenter = fulfillmentCenter;
		this.countryCode = countryCode;
		this.longestSide = longestSide;
		this.medianSide = medianSide;
		this.shortestSide = shortestSide;
		this.measurementUnits = measurementUnits;
		this.weight = weight;
		this.weightUnits = weightUnits;
		this.itemVolume = itemVolume;
		this.volumeUnits = volumeUnits;
		this.averageQuantityOnHand = averageQuantityOnHand;
		this.averageQuantityPendingRemoval = averageQuantityPendingRemoval;
		this.estimatedTotalItemVolume = estimatedTotalItemVolume;
		this.month = month;
		this.storageRate = storageRate;
		this.currency = currency;
		this.estimatedMonthlyStorageFee = estimatedMonthlyStorageFee;
		this.updateDate = updateDate;
		this.productSizeTier=productSizeTier;
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


	public String getFulfillmentCenter() {
		return fulfillmentCenter;
	}


	public void setFulfillmentCenter(String fulfillmentCenter) {
		this.fulfillmentCenter = fulfillmentCenter;
	}


	public String getCountryCode() {
		return countryCode;
	}


	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}


	public Float getLongestSide() {
		return longestSide;
	}


	public void setLongestSide(Float longestSide) {
		this.longestSide = longestSide;
	}


	public Float getMedianSide() {
		return medianSide;
	}


	public void setMedianSide(Float medianSide) {
		this.medianSide = medianSide;
	}


	public Float getShortestSide() {
		return shortestSide;
	}


	public void setShortestSide(Float shortestSide) {
		this.shortestSide = shortestSide;
	}


	public String getMeasurementUnits() {
		return measurementUnits;
	}


	public void setMeasurementUnits(String measurementUnits) {
		this.measurementUnits = measurementUnits;
	}


	public Float getWeight() {
		return weight;
	}


	public void setWeight(Float weight) {
		this.weight = weight;
	}


	public String getWeightUnits() {
		return weightUnits;
	}


	public void setWeightUnits(String weightUnits) {
		this.weightUnits = weightUnits;
	}


	public Float getItemVolume() {
		return itemVolume;
	}


	public void setItemVolume(Float itemVolume) {
		this.itemVolume = itemVolume;
	}


	public String getVolumeUnits() {
		return volumeUnits;
	}


	public void setVolumeUnits(String volumeUnits) {
		this.volumeUnits = volumeUnits;
	}


	public Float getAverageQuantityOnHand() {
		return averageQuantityOnHand;
	}


	public void setAverageQuantityOnHand(Float averageQuantityOnHand) {
		this.averageQuantityOnHand = averageQuantityOnHand;
	}


	public Float getAverageQuantityPendingRemoval() {
		return averageQuantityPendingRemoval;
	}


	public void setAverageQuantityPendingRemoval(Float averageQuantityPendingRemoval) {
		this.averageQuantityPendingRemoval = averageQuantityPendingRemoval;
	}


	public Float getEstimatedTotalItemVolume() {
		return estimatedTotalItemVolume;
	}


	public void setEstimatedTotalItemVolume(Float estimatedTotalItemVolume) {
		this.estimatedTotalItemVolume = estimatedTotalItemVolume;
	}


	public String getMonth() {
		return month;
	}


	public void setMonth(String month) {
		this.month = month;
	}


	public Float getStorageRate() {
		return storageRate;
	}


	public void setStorageRate(Float storageRate) {
		this.storageRate = storageRate;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public Float getEstimatedMonthlyStorageFee() {
		return estimatedMonthlyStorageFee;
	}


	public void setEstimatedMonthlyStorageFee(Float estimatedMonthlyStorageFee) {
		this.estimatedMonthlyStorageFee = estimatedMonthlyStorageFee;
	}


	public Date getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getProductSizeTier() {
		return productSizeTier;
	}

	public void setProductSizeTier(String productSizeTier) {
		this.productSizeTier = productSizeTier;
	}
	
	@Transient
    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    @Transient
    public String getShortestSideNew() {
        return shortestSideNew;
    }

    public void setShortestSideNew(String shortestSideNew) {
        this.shortestSideNew = shortestSideNew;
    }

    @Transient
    public String getMedianSideNew() {
        return medianSideNew;
    }

    public void setMedianSideNew(String medianSideNew) {
        this.medianSideNew = medianSideNew;
    }

    @Transient
    public String getLongestSideNew() {
        return longestSideNew;
    }

    public void setLongestSideNew(String longestSideNew) {
        this.longestSideNew = longestSideNew;
    }

    @Transient
    public String getTotalLongFee() {
        return totalLongFee;
    }

    public void setTotalLongFee(String totalLongFee) {
        this.totalLongFee = totalLongFee;
    }

    @Transient
    public String getTotalMonthFee() {
        return totalMonthFee;
    }

    public void setTotalMonthFee(String totalMonthFee) {
        this.totalMonthFee = totalMonthFee;
    }

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
}


