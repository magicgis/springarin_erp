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
@Table(name = "amazoninfo_fba_health_report")
public class AmazonFbaHealthReport implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String id; 	
	private Date snapshotDate;
	private String sku;
	private String fnsku;
	private String asin;
	private String productName;
	private String condition;
	private Integer salesRank;
	private String productGroup;
	private Integer totalQuantity;
	private Integer sellableQuantity;
	private Integer unsellableQuantity;
	private Integer ageDays90;
	private Integer ageDays180;
	private Integer ageDays270;
	private Integer ageDays365;
	private Integer agePlusDays365;
	private Integer shippedHrs24;
	private Integer shippedDays7;
	private Integer shippedDays30;
	private Integer shippedDays90;
	private Integer shippedDays180;
	private Integer shippedDays365;
	private Float weeksCover7;
	private Float weeksCover30;
	private Float weeksCover90;
	private Float weeksCover180;
	private Float weeksCover365;
	private String afnNewSellers;
	private String afnUsedSellers;
	private String country;
	private Float yourPrice;
	private Float salesPrice;
	private Float afnNewPrice;
	private Float afnUsedPrice;
	private Float mfnNewPrice;
	private Float mfnUsedPrice;
	private Integer qtyCharged;
	private Integer qtyLongTermStorage;
	private Integer qtyWithRemovals;
	private Float projectedMo12;
	private Float perUnitVolume;
	private String isHazmat;
	private Integer inBoundQuantity;
	private String asinLimit;
	private Integer inboundRecommendQuantity;
	private Date createTime;
	private String accountName;
	
	
	
	public String getAccountName() {
		return accountName;
	}



	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}



	public AmazonFbaHealthReport() {
		super();
	}
	
	
	
	public AmazonFbaHealthReport(Integer ageDays365, Integer agePlusDays365) {
		super();
		this.ageDays365 = ageDays365;
		this.agePlusDays365 = agePlusDays365;
	}



	public AmazonFbaHealthReport(Date snapshotDate, String sku, String fnsku,
			String asin, String productName, String condition,
			Integer salesRank, String productGroup, Integer totalQuantity,
			Integer sellableQuantity, Integer unsellableQuantity,
			Integer ageDays90, Integer ageDays180, Integer ageDays270,
			Integer ageDays365, Integer agePlusDays365, Integer shippedHrs24,
			Integer shippedDays7, Integer shippedDays30, Integer shippedDays90,
			Integer shippedDays180, Integer shippedDays365, Float weeksCover7,
			Float weeksCover30, Float weeksCover90, Float weeksCover180,
			Float weeksCover365, String afnNewSellers, String afnUsedSellers,
			String country, Float yourPrice, Float salesPrice,
			Float afnNewPrice, Float afnUsedPrice, Float mfnNewPrice,
			Float mfnUsedPrice, Integer qtyCharged, Integer qtyLongTermStorage,
			Integer qtyWithRemovals, Float projectedMo12, Float perUnitVolume,
			String isHazmat, Integer inBoundQuantity, String asinLimit,
			Integer inboundRecommendQuantity, Date createTime) {
		super();
		this.snapshotDate = snapshotDate;
		this.sku = sku;
		this.fnsku = fnsku;
		this.asin = asin;
		this.productName = productName;
		this.condition = condition;
		this.salesRank = salesRank;
		this.productGroup = productGroup;
		this.totalQuantity = totalQuantity;
		this.sellableQuantity = sellableQuantity;
		this.unsellableQuantity = unsellableQuantity;
		this.ageDays90 = ageDays90;
		this.ageDays180 = ageDays180;
		this.ageDays270 = ageDays270;
		this.ageDays365 = ageDays365;
		this.agePlusDays365 = agePlusDays365;
		this.shippedHrs24 = shippedHrs24;
		this.shippedDays7 = shippedDays7;
		this.shippedDays30 = shippedDays30;
		this.shippedDays90 = shippedDays90;
		this.shippedDays180 = shippedDays180;
		this.shippedDays365 = shippedDays365;
		this.weeksCover7 = weeksCover7;
		this.weeksCover30 = weeksCover30;
		this.weeksCover90 = weeksCover90;
		this.weeksCover180 = weeksCover180;
		this.weeksCover365 = weeksCover365;
		this.afnNewSellers = afnNewSellers;
		this.afnUsedSellers = afnUsedSellers;
		this.country = country;
		this.yourPrice = yourPrice;
		this.salesPrice = salesPrice;
		this.afnNewPrice = afnNewPrice;
		this.afnUsedPrice = afnUsedPrice;
		this.mfnNewPrice = mfnNewPrice;
		this.mfnUsedPrice = mfnUsedPrice;
		this.qtyCharged = qtyCharged;
		this.qtyLongTermStorage = qtyLongTermStorage;
		this.qtyWithRemovals = qtyWithRemovals;
		this.projectedMo12 = projectedMo12;
		this.perUnitVolume = perUnitVolume;
		this.isHazmat = isHazmat;
		this.inBoundQuantity = inBoundQuantity;
		this.asinLimit = asinLimit;
		this.inboundRecommendQuantity = inboundRecommendQuantity;
		this.createTime = createTime;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
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

	public String getFnsku() {
		return fnsku;
	}

	public void setFnsku(String fnsku) {
		this.fnsku = fnsku;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	@Column(name="condition1")
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Integer getSalesRank() {
		return salesRank;
	}

	public void setSalesRank(Integer salesRank) {
		this.salesRank = salesRank;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public Integer getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Integer getSellableQuantity() {
		return sellableQuantity;
	}

	public void setSellableQuantity(Integer sellableQuantity) {
		this.sellableQuantity = sellableQuantity;
	}

	public Integer getUnsellableQuantity() {
		return unsellableQuantity;
	}

	public void setUnsellableQuantity(Integer unsellableQuantity) {
		this.unsellableQuantity = unsellableQuantity;
	}

	public Integer getAgeDays90() {
		return ageDays90;
	}

	public void setAgeDays90(Integer ageDays90) {
		this.ageDays90 = ageDays90;
	}

	public Integer getAgeDays180() {
		return ageDays180;
	}

	public void setAgeDays180(Integer ageDays180) {
		this.ageDays180 = ageDays180;
	}

	public Integer getAgeDays270() {
		return ageDays270;
	}

	public void setAgeDays270(Integer ageDays270) {
		this.ageDays270 = ageDays270;
	}

	public Integer getAgeDays365() {
		return ageDays365;
	}

	public void setAgeDays365(Integer ageDays365) {
		this.ageDays365 = ageDays365;
	}

	public Integer getAgePlusDays365() {
		return agePlusDays365;
	}

	public void setAgePlusDays365(Integer agePlusDays365) {
		this.agePlusDays365 = agePlusDays365;
	}

	public Integer getShippedHrs24() {
		return shippedHrs24;
	}

	public void setShippedHrs24(Integer shippedHrs24) {
		this.shippedHrs24 = shippedHrs24;
	}

	public Integer getShippedDays7() {
		return shippedDays7;
	}

	public void setShippedDays7(Integer shippedDays7) {
		this.shippedDays7 = shippedDays7;
	}

	public Integer getShippedDays30() {
		return shippedDays30;
	}

	public void setShippedDays30(Integer shippedDays30) {
		this.shippedDays30 = shippedDays30;
	}

	public Integer getShippedDays90() {
		return shippedDays90;
	}

	public void setShippedDays90(Integer shippedDays90) {
		this.shippedDays90 = shippedDays90;
	}

	public Integer getShippedDays180() {
		return shippedDays180;
	}

	public void setShippedDays180(Integer shippedDays180) {
		this.shippedDays180 = shippedDays180;
	}

	public Integer getShippedDays365() {
		return shippedDays365;
	}

	public void setShippedDays365(Integer shippedDays365) {
		this.shippedDays365 = shippedDays365;
	}



	public Float getWeeksCover7() {
		return weeksCover7;
	}

	public void setWeeksCover7(Float weeksCover7) {
		this.weeksCover7 = weeksCover7;
	}

	public Float getWeeksCover30() {
		return weeksCover30;
	}

	public void setWeeksCover30(Float weeksCover30) {
		this.weeksCover30 = weeksCover30;
	}

	public Float getWeeksCover90() {
		return weeksCover90;
	}

	public void setWeeksCover90(Float weeksCover90) {
		this.weeksCover90 = weeksCover90;
	}

	public Float getWeeksCover180() {
		return weeksCover180;
	}

	public void setWeeksCover180(Float weeksCover180) {
		this.weeksCover180 = weeksCover180;
	}

	public Float getWeeksCover365() {
		return weeksCover365;
	}

	public void setWeeksCover365(Float weeksCover365) {
		this.weeksCover365 = weeksCover365;
	}

	public String getAfnNewSellers() {
		return afnNewSellers;
	}

	public void setAfnNewSellers(String afnNewSellers) {
		this.afnNewSellers = afnNewSellers;
	}

	public String getAfnUsedSellers() {
		return afnUsedSellers;
	}

	public void setAfnUsedSellers(String afnUsedSellers) {
		this.afnUsedSellers = afnUsedSellers;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Float getYourPrice() {
		return yourPrice;
	}

	public void setYourPrice(Float yourPrice) {
		this.yourPrice = yourPrice;
	}

	public Float getSalesPrice() {
		return salesPrice;
	}

	public void setSalesPrice(Float salesPrice) {
		this.salesPrice = salesPrice;
	}

	public Float getAfnNewPrice() {
		return afnNewPrice;
	}

	public void setAfnNewPrice(Float afnNewPrice) {
		this.afnNewPrice = afnNewPrice;
	}

	public Float getAfnUsedPrice() {
		return afnUsedPrice;
	}

	public void setAfnUsedPrice(Float afnUsedPrice) {
		this.afnUsedPrice = afnUsedPrice;
	}

	public Float getMfnNewPrice() {
		return mfnNewPrice;
	}

	public void setMfnNewPrice(Float mfnNewPrice) {
		this.mfnNewPrice = mfnNewPrice;
	}

	public Float getMfnUsedPrice() {
		return mfnUsedPrice;
	}

	public void setMfnUsedPrice(Float mfnUsedPrice) {
		this.mfnUsedPrice = mfnUsedPrice;
	}

	public Integer getQtyCharged() {
		return qtyCharged;
	}

	public void setQtyCharged(Integer qtyCharged) {
		this.qtyCharged = qtyCharged;
	}

	public Integer getQtyLongTermStorage() {
		return qtyLongTermStorage;
	}

	public void setQtyLongTermStorage(Integer qtyLongTermStorage) {
		this.qtyLongTermStorage = qtyLongTermStorage;
	}

	public Integer getQtyWithRemovals() {
		return qtyWithRemovals;
	}

	public void setQtyWithRemovals(Integer qtyWithRemovals) {
		this.qtyWithRemovals = qtyWithRemovals;
	}


	public Float getProjectedMo12() {
		return projectedMo12;
	}

	public void setProjectedMo12(Float projectedMo12) {
		this.projectedMo12 = projectedMo12;
	}

	public Float getPerUnitVolume() {
		return perUnitVolume;
	}

	public void setPerUnitVolume(Float perUnitVolume) {
		this.perUnitVolume = perUnitVolume;
	}

	public String getIsHazmat() {
		return isHazmat;
	}

	public void setIsHazmat(String isHazmat) {
		this.isHazmat = isHazmat;
	}

	public Integer getInBoundQuantity() {
		return inBoundQuantity;
	}

	public void setInBoundQuantity(Integer inBoundQuantity) {
		this.inBoundQuantity = inBoundQuantity;
	}

	public String getAsinLimit() {
		return asinLimit;
	}

	public void setAsinLimit(String asinLimit) {
		this.asinLimit = asinLimit;
	}

	public Integer getInboundRecommendQuantity() {
		return inboundRecommendQuantity;
	}

	public void setInboundRecommendQuantity(Integer inboundRecommendQuantity) {
		this.inboundRecommendQuantity = inboundRecommendQuantity;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}


