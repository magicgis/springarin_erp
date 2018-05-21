package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 评测人信息
 * @author lee
 * @date 2015-11-30
 */
@Entity
@Table(name = "amazoninfo_reviewer")
public class AmazonReviewer implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private 	Integer 	id;
	private 	String 		name;
	private 	String 		reviewEmail;    //评论邮箱
	private 	String 		country;		//平台
	private 	String 		address;
	private 	String 		reviewerType;	//0:站内、1:站外
	private 	String 		amaUrl;			//ama个人网址
	
	private 	String 		reviewerId;		//客户id
	private 	String 		star;			//给评论人评分
	private 	Integer 	rank;			//amazon排名
	private 	Date    	updateDate; 	//更新时间
	private     String      email1;         //私人邮箱1
	private     String      email2;         //私人邮箱2
	private     String      sourcePlatform; //评测人来源平台（Facebook、Youtube等,针对站外评测人）
	private 	User 		createBy;		//创建人
	private 	User 		contactBy;		//最后联系人
	private     String      isVineVoice;    //是不是vineVoice顶尖品论群体 0为不是 1为是
	
	
	private 	String 		youtubeUrl;		//youtobe个人网址
	private 	String 		twitterUrl;		//twitter个人网址
	private 	String 		sitefbUrl;		//site Fb个人网址
	private 	String 		otherUrl;		//其他平台个人网址
	private     String      facebookUrl;    //同上 
	private     String      instagramUrl;   //同上
	
	
	public String getFacebookUrl() {
		return facebookUrl;
	}

	public void setFacebookUrl(String facebookUrl) {
		this.facebookUrl = facebookUrl;
	}

	public String getInstagramUrl() {
		return instagramUrl;
	}

	public void setInstagramUrl(String instagramUrl) {
		this.instagramUrl = instagramUrl;
	}

	private 	List<AmazonReviewerContent> content = Lists.newArrayList();
	private List<ReviewerComment> comments;
	
	public AmazonReviewer() {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getReviewerType() {
		return reviewerType;
	}

	public void setReviewerType(String reviewerType) {
		this.reviewerType = reviewerType;
	}

	public String getAmaUrl() {
		return amaUrl;
	}

	public void setAmaUrl(String amaUrl) {
		this.amaUrl = amaUrl;
	}

	public String getYoutubeUrl() {
		return youtubeUrl;
	}

	public void setYoutubeUrl(String youtubeUrl) {
		this.youtubeUrl = youtubeUrl;
	}

	public String getTwitterUrl() {
		return twitterUrl;
	}

	public void setTwitterUrl(String twitterUrl) {
		this.twitterUrl = twitterUrl;
	}

	public String getSitefbUrl() {
		return sitefbUrl;
	}

	public void setSitefbUrl(String sitefbUrl) {
		this.sitefbUrl = sitefbUrl;
	}

	public String getOtherUrl() {
		return otherUrl;
	}

	public void setOtherUrl(String otherUrl) {
		this.otherUrl = otherUrl;
	}

	public String getReviewerId() {
		return reviewerId;
	}

	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}
	

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@OneToMany(mappedBy = "reviewer",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL})
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<AmazonReviewerContent> getContent() {
		return content;
	}

	public void setContent(List<AmazonReviewerContent> content) {
		this.content = content;
	}
	
	@OneToMany(mappedBy = "amazonReviewer", fetch=FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("createDate")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<ReviewerComment> getComments() {
		return comments;
	}

	public void setComments(List<ReviewerComment> comments) {
		this.comments = comments;
	}

	public String getReviewEmail() {
		return reviewEmail;
	}

	public void setReviewEmail(String reviewEmail) {
		this.reviewEmail = reviewEmail;
	}

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getSourcePlatform() {
		return sourcePlatform;
	}

	public void setSourcePlatform(String sourcePlatform) {
		this.sourcePlatform = sourcePlatform;
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

	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getContactBy() {
		return contactBy;
	}

	public void setContactBy(User contactBy) {
		this.contactBy = contactBy;
	}

	public String getIsVineVoice() {
		return isVineVoice;
	}

	public void setIsVineVoice(String isVineVoice) {
		this.isVineVoice = isVineVoice;
	}
	
	
	
	
}
