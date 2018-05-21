package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.net.IDN;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "amazoninfo_case")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class AmazonCase implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String country;
	private String asin;
	private String subject;
	private String sendContent;
	
	private String merchantName;
	private String merchantId;
	private String marketplaceId;
	private String language;
	private String callbackCountry;
	private String caseId;
	
	private User createBy;
	private String ccToEmail;
	private Date sentDate;
	
	private String predictionId;
	private String itemId;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSendContent() {
		if(StringUtils.isNotEmpty(sendContent)){
			sendContent = sendContent.replaceAll("(<style>[\\s\\S]*?</style>)|(<STYLE>[\\s\\S]*?</STYLE>)","");
		}
		return sendContent;
	}

	public void setSendContent(String sendContent) {
		this.sendContent = sendContent;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMarketplaceId() {
		return marketplaceId;
	}

	public void setMarketplaceId(String marketplaceId) {
		this.marketplaceId = marketplaceId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCallbackCountry() {
		return callbackCountry;
	}

	public void setCallbackCountry(String callbackCountry) {
		this.callbackCountry = callbackCountry;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateBy() {
		return createBy;
	}

	public void setCreateBy(User createBy) {
		this.createBy = createBy;
	}
	
	public String getCcToEmail() {
		if(ccToEmail!=null){
			ccToEmail =  IDN.toASCII(ccToEmail);
		}
		return ccToEmail;
	}

	public void setCcToEmail(String ccToEmail) {
		this.ccToEmail = ccToEmail;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getSentDate() {
		return sentDate;
	}
	
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	@Transient
	public String getPredictionId() {
		return predictionId;
	}

	@Transient
	public void setPredictionId(String predictionId) {
		this.predictionId = predictionId;
	}

	@Transient
	public String getItemId() {
		return itemId;
	}

	@Transient
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

}


