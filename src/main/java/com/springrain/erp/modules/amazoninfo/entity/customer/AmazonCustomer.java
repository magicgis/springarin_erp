package com.springrain.erp.modules.amazoninfo.entity.customer;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.StringUtils;

@Entity
@Table(name = "amazoninfo_customer")
@DynamicInsert
@DynamicUpdate
public class AmazonCustomer{

    private String customerId;

    private String name;
    
    private String country;
    
    private String amzEmail;
    
    private String email;
    
    private String star;
    
    private int buyTimes = 1;
    
    private int buyQuantity;
    
    private int returnQuantity = 0;
    
    private float refundMoney = 0f;
    
    private int supportQuantity = 0;
    
    private Date firstBuyDate;
    
    private Date lastBuyDate;
    
    private String eventId;
    
    private List<AmazonBuyComment> buyComments;
    
    private List<AmazonComment> comments;
    
    private List<AmazonReviewComment> reviewComments;
    
    private List<AmazonBuyComment> buyCommentTemp = Lists.newArrayList();
    
    private List<AmazonBuyComment> returnCommentTemp = Lists.newArrayList();
    
    private List<AmazonBuyComment> supportCommentTemp = Lists.newArrayList();
    
    private List<AmazonBuyComment> refundCommentTemp = Lists.newArrayList();

    private String messageState;//1：Undelivered message
    
    
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Id
	public String getAmzEmail() {
		return amzEmail;
	}

	public void setAmzEmail(String amzEmail) {
		this.amzEmail = amzEmail;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	@OneToMany(mappedBy = "customer",fetch=FetchType.EAGER,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@OrderBy(value="createDate DESC")
	public List<AmazonBuyComment> getBuyComments() {
		return buyComments;
	}

	public void setBuyComments(List<AmazonBuyComment> buyComments) {
		this.buyComments = buyComments;
	}

	@OneToMany(mappedBy = "customer",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<AmazonComment> getComments() {
		return comments;
	}

	public void setComments(List<AmazonComment> comments) {
		this.comments = comments;
	}

	@OneToMany(mappedBy = "customer",fetch=FetchType.EAGER,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@OrderBy(value="createDate DESC")
	public List<AmazonReviewComment> getReviewComments() {
		return reviewComments;
	}

	public void setReviewComments(List<AmazonReviewComment> reviewComments) {
		this.reviewComments = reviewComments;
	}

	public int getBuyTimes() {
		return buyTimes;
	}

	public void setBuyTimes(int buyTimes) {
		this.buyTimes = buyTimes;
	}

	public int getBuyQuantity() {
		return buyQuantity;
	}

	public void setBuyQuantity(int buyQuantity) {
		this.buyQuantity = buyQuantity;
	}

	public int getReturnQuantity() {
		return returnQuantity;
	}

	public void setReturnQuantity(int returnQuantity) {
		this.returnQuantity = returnQuantity;
	}

	public Date getFirstBuyDate() {
		return firstBuyDate;
	}

	public void setFirstBuyDate(Date firstBuyDate) {
		this.firstBuyDate = firstBuyDate;
	}

	public Date getLastBuyDate() {
		return lastBuyDate;
	}

	public void setLastBuyDate(Date lastBuyDate) {
		this.lastBuyDate = lastBuyDate;
	}
	
	public AmazonCustomer() {}

	public AmazonCustomer(String customerId, String name,
			String amzEmail, int buyQuantity, Date firstBuyDate) {
		super();
		this.customerId = customerId;
		this.name = name;
		this.amzEmail = amzEmail;
		if(StringUtils.isNotEmpty(amzEmail)){
			this.country = amzEmail.substring(amzEmail.lastIndexOf(".")+1);
		}
		this.buyQuantity = buyQuantity;
		this.firstBuyDate = firstBuyDate;
		this.lastBuyDate = firstBuyDate;
	}
	
	@Transient
	public String getLink(){
		String suf = country;
		if(suf!=null){
			if("jp,uk".contains(suf)){
				suf = "co."+suf;
			}else if("mx".contains(suf)){
				suf = "com."+suf;
			}
			return "http://www.amazon."+suf+"/gp/pdp/profile/"+customerId;
		}else{
			return "";
		}
	}
	
	@Transient
	public List<AmazonBuyComment> getBuyCommentTemp() {
		return buyCommentTemp;
	}

	@Transient
	public List<AmazonBuyComment> getReturnCommentTemp() {
		return returnCommentTemp;
	}
	
	
	@Transient
	public List<AmazonBuyComment> getSupportCommentTemp() {
		return supportCommentTemp;
	}



	@Transient
	public List<AmazonBuyComment> getRefundCommentTemp() {
		return refundCommentTemp;
	}


	private Map<String, String> productName;
	
	@Transient
	public Map<String, String> getReviewProductName() {
		if(productName==null){
			productName = Maps.newHashMap();
			if (buyComments != null && buyComments.size() > 0) {
				for (AmazonBuyComment comment : buyComments) {
					productName.put(comment.getAsin(),comment.getProductName());
					if("1".equals(comment.getType())){
						buyCommentTemp.add(comment);
					}else if("2".equals(comment.getType())){
						returnCommentTemp.add(comment);
					}else if("3".equals(comment.getType())){
						supportCommentTemp.add(comment);
					}else if("4".equals(comment.getType())){
						refundCommentTemp.add(comment);
					}
				}
			}
		}
		return productName;
	}


	public float getRefundMoney() {
		return refundMoney;
	}

	public void setRefundMoney(float refundMoney) {
		this.refundMoney = refundMoney;
	}

	public int getSupportQuantity() {
		return supportQuantity;
	}

	public void setSupportQuantity(int supportQuantity) {
		this.supportQuantity = supportQuantity;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getMessageState() {
		return messageState;
	}

	public void setMessageState(String messageState) {
		this.messageState = messageState;
	}
	
	@Transient
	public String getEncryptionEmail() {
		String rs= email;
		if(email!=null&&email.contains(",")){
			 String sendArr="";
			 String[] mailArr= email.split(",");
			 for (String mail: mailArr) {
				 if(!mail.contains("@amazon")&&!mail.contains("@marketplace.amazon")){
						String[] arr = mail.split("@");
						String tmpEmail=Encodes.encodeBase64(arr[0].getBytes())+"@"+arr[1];
						sendArr+="erp"+tmpEmail+",";
				 }
			 }
			 return sendArr.substring(0,sendArr.length()-1);
		}else if(email!=null){
			if(!email.contains("@amazon")&&!email.contains("@marketplace.amazon")){
				String[] arr = email.split("@");
				String tmpEmail=Encodes.encodeBase64(arr[0].getBytes())+"@"+arr[1];
				rs="erp"+tmpEmail;
			}
		}
		return rs;
	}

}
