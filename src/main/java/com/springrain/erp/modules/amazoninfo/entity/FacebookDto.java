package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Date;

public class FacebookDto {
	private Date starts;
	private Date end;
	private String product;
	private String audience;
	private String age;
	private String gender;
	private String country;
	private String placement;
	private String adName;
	private Float  amountSpend;
	private Integer linkClicks;
	private Float ctr;
	private Float cpc;
	private Float cpm;
	private Integer allItemsShipped;
	private Float totalCr;
	private Float profit;
	private Float totalAdvertisingFees;
	private Float roi;
	private Float impressions;
	private Integer sameItemsShipped;
	private Float sameCr;
	private Float totalRevenue;
	private Float relativeRoi;
	private String productLine;
	private String adId;
	private String trackingId;
	private Float relevanceScore;
	private String negativeFeedback;
	private Float postComments;
	private Float costPerPostEngagement;
	private Float costPerPageLike;
	private Float costPerPostShare;
	private Float postEngagement;
	private Float postShares;
	private Float pageLikes;
	private String preView;
	private Float forecastCpc;
	private Float forecastRoi;
	
	private String createDate;
	private String endDate;
	private String adsDate;
	
	private Float  totalAffiliateFees;
	private String asinOnAd;
	private String productName;
	
	
	
	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public String getAsinOnAd() {
		return asinOnAd;
	}


	public void setAsinOnAd(String asinOnAd) {
		this.asinOnAd = asinOnAd;
	}


	public Float getTotalAffiliateFees() {
		return totalAffiliateFees;
	}


	public void setTotalAffiliateFees(Float totalAffiliateFees) {
		this.totalAffiliateFees = totalAffiliateFees;
	}


	public String getAdsDate() {
		return adsDate;
	}


	public void setAdsDate(String adsDate) {
		this.adsDate = adsDate;
	}


	public String getCreateDate() {
		return createDate;
	}


	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}


	public String getEndDate() {
		return endDate;
	}


	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


	public FacebookDto() {
		super();
	}
	
	
	public FacebookDto(Date starts, String product, String audience,
			String age, String gender, String country, String placement,
			String adName, Float amountSpend, Integer linkClicks, Float ctr,
			Float cpc, Float cpm, Integer allItemsShipped, Float totalCr,
			Float profit, Float totalAdvertisingFees, Float roi,
			Float impressions, Integer sameItemsShipped, Float sameCr,
			Float totalRevenue, Float relativeRoi, String productLine,
			String adId, String trackingId, Float relevanceScore,
			String negativeFeedback, Float postComments,
			Float costPerPostEngagement, Float costPerPageLike,
			Float costPerPostShare, Float postEngagement, Float postShares,
			Float pageLikes, String preView, Float forecastCpc,
			Float forecastRoi,Float totalAffiliateFees) {
		super();
		this.starts = starts;
		this.product = product;
		this.audience = audience;
		this.age = age;
		this.gender = gender;
		this.country = country;
		this.placement = placement;
		this.adName = adName;
		this.amountSpend = amountSpend;
		this.linkClicks = linkClicks;
		this.ctr = ctr;
		this.cpc = cpc;
		this.cpm = cpm;
		this.allItemsShipped = allItemsShipped;
		this.totalCr = totalCr;
		this.profit = profit;
		this.totalAdvertisingFees = totalAdvertisingFees;
		this.roi = roi;
		this.impressions = impressions;
		this.sameItemsShipped = sameItemsShipped;
		this.sameCr = sameCr;
		this.totalRevenue = totalRevenue;
		this.relativeRoi = relativeRoi;
		this.productLine = productLine;
		this.adId = adId;
		this.trackingId = trackingId;
		this.relevanceScore = relevanceScore;
		this.negativeFeedback = negativeFeedback;
		this.postComments = postComments;
		this.costPerPostEngagement = costPerPostEngagement;
		this.costPerPageLike = costPerPageLike;
		this.costPerPostShare = costPerPostShare;
		this.postEngagement = postEngagement;
		this.postShares = postShares;
		this.pageLikes = pageLikes;
		this.preView = preView;
		this.forecastCpc = forecastCpc;
		this.forecastRoi = forecastRoi;
		this.totalAffiliateFees=totalAffiliateFees;
	}
	
	public Date getStarts() {
		return starts;
	}
	public void setStarts(Date starts) {
		this.starts = starts;
	}
	
	
	public Date getEnd() {
		return end;
	}


	public void setEnd(Date end) {
		this.end = end;
	}


	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getAudience() {
		return audience;
	}
	public void setAudience(String audience) {
		this.audience = audience;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPlacement() {
		return placement;
	}
	public void setPlacement(String placement) {
		this.placement = placement;
	}
	public String getAdName() {
		return adName;
	}
	public void setAdName(String adName) {
		this.adName = adName;
	}
	public Float getAmountSpend() {
		return amountSpend;
	}
	public void setAmountSpend(Float amountSpend) {
		this.amountSpend = amountSpend;
	}
	public Integer getLinkClicks() {
		return linkClicks;
	}
	public void setLinkClicks(Integer linkClicks) {
		this.linkClicks = linkClicks;
	}
	public Float getCtr() {
		return ctr;
	}
	public void setCtr(Float ctr) {
		this.ctr = ctr;
	}
	public Float getCpc() {
		return cpc;
	}
	public void setCpc(Float cpc) {
		this.cpc = cpc;
	}
	public Float getCpm() {
		return cpm;
	}
	public void setCpm(Float cpm) {
		this.cpm = cpm;
	}
	public Integer getAllItemsShipped() {
		return allItemsShipped;
	}
	public void setAllItemsShipped(Integer allItemsShipped) {
		this.allItemsShipped = allItemsShipped;
	}
	public Float getTotalCr() {
		return totalCr;
	}
	public void setTotalCr(Float totalCr) {
		this.totalCr = totalCr;
	}
	public Float getProfit() {
		return profit;
	}
	public void setProfit(Float profit) {
		this.profit = profit;
	}
	public Float getTotalAdvertisingFees() {
		return totalAdvertisingFees;
	}
	public void setTotalAdvertisingFees(Float totalAdvertisingFees) {
		this.totalAdvertisingFees = totalAdvertisingFees;
	}
	public Float getRoi() {
		return roi;
	}
	public void setRoi(Float roi) {
		this.roi = roi;
	}
	public Float getImpressions() {
		return impressions;
	}
	public void setImpressions(Float impressions) {
		this.impressions = impressions;
	}
	public Integer getSameItemsShipped() {
		return sameItemsShipped;
	}
	public void setSameItemsShipped(Integer sameItemsShipped) {
		this.sameItemsShipped = sameItemsShipped;
	}
	public Float getSameCr() {
		return sameCr;
	}
	public void setSameCr(Float sameCr) {
		this.sameCr = sameCr;
	}
	public Float getTotalRevenue() {
		return totalRevenue;
	}
	public void setTotalRevenue(Float totalRevenue) {
		this.totalRevenue = totalRevenue;
	}
	public Float getRelativeRoi() {
		return relativeRoi;
	}
	public void setRelativeRoi(Float relativeRoi) {
		this.relativeRoi = relativeRoi;
	}
	public String getProductLine() {
		return productLine;
	}
	public void setProductLine(String productLine) {
		this.productLine = productLine;
	}
	public String getAdId() {
		return adId;
	}
	public void setAdId(String adId) {
		this.adId = adId;
	}
	public String getTrackingId() {
		return trackingId;
	}
	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}
	public Float getRelevanceScore() {
		return relevanceScore;
	}
	public void setRelevanceScore(Float relevanceScore) {
		this.relevanceScore = relevanceScore;
	}
	public String getNegativeFeedback() {
		return negativeFeedback;
	}
	public void setNegativeFeedback(String negativeFeedback) {
		this.negativeFeedback = negativeFeedback;
	}
	public Float getPostComments() {
		return postComments;
	}
	public void setPostComments(Float postComments) {
		this.postComments = postComments;
	}
	public Float getCostPerPostEngagement() {
		return costPerPostEngagement;
	}
	public void setCostPerPostEngagement(Float costPerPostEngagement) {
		this.costPerPostEngagement = costPerPostEngagement;
	}
	public Float getCostPerPageLike() {
		return costPerPageLike;
	}
	public void setCostPerPageLike(Float costPerPageLike) {
		this.costPerPageLike = costPerPageLike;
	}
	public Float getCostPerPostShare() {
		return costPerPostShare;
	}
	public void setCostPerPostShare(Float costPerPostShare) {
		this.costPerPostShare = costPerPostShare;
	}
	public Float getPostEngagement() {
		return postEngagement;
	}
	public void setPostEngagement(Float postEngagement) {
		this.postEngagement = postEngagement;
	}
	public Float getPostShares() {
		return postShares;
	}
	public void setPostShares(Float postShares) {
		this.postShares = postShares;
	}
	public Float getPageLikes() {
		return pageLikes;
	}
	public void setPageLikes(Float pageLikes) {
		this.pageLikes = pageLikes;
	}
	public String getPreView() {
		return preView;
	}
	public void setPreView(String preView) {
		this.preView = preView;
	}
	public Float getForecastCpc() {
		return forecastCpc;
	}
	public void setForecastCpc(Float forecastCpc) {
		this.forecastCpc = forecastCpc;
	}
	public Float getForecastRoi() {
		return forecastRoi;
	}
	public void setForecastRoi(Float forecastRoi) {
		this.forecastRoi = forecastRoi;
	}
	
	
}


