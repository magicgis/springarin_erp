package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "facebook_report")
public class FacebookReport implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id; 
	private Date start;
	private Date end;
	private String accountId;
	private String campaignName;
	private String campaignId;
	private String adSetName;
	private String adSetId;
	private String adName;
	private String adId;
	private String delivery;
	private Double amountSpent;
	private Double impressions;
	private Double linkClicks;
	private Double frequency;
	private Double relevanceScore;
	private String negativeFeedback;
	private Double postShares;
	private Double postComments;
	private Double pageLikes;
	private Double postEngagement;
	private String delFlag;
	
	
	

	public FacebookReport() {
		super();
	}

	public FacebookReport(Date start, Date end, String accountId,
			String campaignName, String campaignId, String adSetName,
			String adSetId, String adName, String adId, String delivery,
			Double amountSpent, Double impressions, Double linkClicks,
			Double frequency, Double relevanceScore, String negativeFeedback,
			Double postShares, Double postComments, Double pageLikes,
			Double postEngagement, String delFlag) {
		super();
		this.start = start;
		this.end = end;
		this.accountId = accountId;
		this.campaignName = campaignName;
		this.campaignId = campaignId;
		this.adSetName = adSetName;
		this.adSetId = adSetId;
		this.adName = adName;
		this.adId = adId;
		this.delivery = delivery;
		this.amountSpent = amountSpent;
		this.impressions = impressions;
		this.linkClicks = linkClicks;
		this.frequency = frequency;
		this.relevanceScore = relevanceScore;
		this.negativeFeedback = negativeFeedback;
		this.postShares = postShares;
		this.postComments = postComments;
		this.pageLikes = pageLikes;
		this.postEngagement = postEngagement;
		this.delFlag = delFlag;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}
	

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getAdSetName() {
		return adSetName;
	}

	public void setAdSetName(String adSetName) {
		this.adSetName = adSetName;
	}

	public String getAdSetId() {
		return adSetId;
	}

	public void setAdSetId(String adSetId) {
		this.adSetId = adSetId;
	}

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public String getAdId() {
		return adId;
	}

	public void setAdId(String adId) {
		this.adId = adId;
	}

	public String getDelivery() {
		return delivery;
	}

	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}

	public Double getAmountSpent() {
		return amountSpent;
	}

	public void setAmountSpent(Double amountSpent) {
		this.amountSpent = amountSpent;
	}

	public Double getImpressions() {
		return impressions;
	}

	public void setImpressions(Double impressions) {
		this.impressions = impressions;
	}

	public Double getLinkClicks() {
		return linkClicks;
	}

	public void setLinkClicks(Double linkClicks) {
		this.linkClicks = linkClicks;
	}

	public Double getFrequency() {
		return frequency;
	}

	public void setFrequency(Double frequency) {
		this.frequency = frequency;
	}

	public Double getRelevanceScore() {
		return relevanceScore;
	}

	public void setRelevanceScore(Double relevanceScore) {
		this.relevanceScore = relevanceScore;
	}

	public String getNegativeFeedback() {
		return negativeFeedback;
	}

	public void setNegativeFeedback(String negativeFeedback) {
		this.negativeFeedback = negativeFeedback;
	}

	public Double getPostShares() {
		return postShares;
	}

	public void setPostShares(Double postShares) {
		this.postShares = postShares;
	}

	public Double getPostComments() {
		return postComments;
	}

	public void setPostComments(Double postComments) {
		this.postComments = postComments;
	}

	public Double getPageLikes() {
		return pageLikes;
	}

	public void setPageLikes(Double pageLikes) {
		this.pageLikes = pageLikes;
	}

	public Double getPostEngagement() {
		return postEngagement;
	}

	public void setPostEngagement(Double postEngagement) {
		this.postEngagement = postEngagement;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

}


