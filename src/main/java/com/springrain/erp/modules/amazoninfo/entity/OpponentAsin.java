package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.springrain.erp.modules.sys.entity.User;


@Entity
@Table(name = "amazoninfo_opponent_asin")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OpponentAsin implements Serializable {
	private     static final long serialVersionUID = -2433140471122314283L;
	
	private     Integer  id;
	private     String  productName;
	private     String  asin;           // 10位校验
	private     String  state;          // 0 取消  1监控
	private 	String 	country;        // 平台
	private     User    createUser;     // 创建人
	private     Date    createDate;     // 创建日期
	private     Date    endDate;        // 查询日期
	
	
	private  List<AmazonReview> reviews = Lists.newArrayList();

	public OpponentAsin() {
		super();
	}
	
	public OpponentAsin(String asinWithCountry, String productName,
			String asin, String state, String country, User createUser,
			Date createDate, List<AmazonReview> reviews) {
		super();
		this.productName = productName;
		this.asin = asin;
		this.state = state;
		this.country = country;
		this.createUser = createUser;
		this.createDate = createDate;
		this.reviews = reviews;
	}




	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}


	@ManyToOne()
	@JoinColumn(name="create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	
	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}
	

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	@Column(updatable=false)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public String getAsin() {
		return asin;
	}


	public void setAsin(String asin) {
		this.asin = asin;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}

	@OneToMany(mappedBy = "reviewMonitor",fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@OrderBy(value="lastUpdateDate DESC,reviewDate DESC")
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<AmazonReview> getReviews() {
		return reviews;
	}

	public void setReviews(List<AmazonReview> reviews) {
		this.reviews = reviews;
	}
	
	@Transient
	public String getBadReviewsLink(Integer num){
		String suffix = country;
		if("jp,uk".contains(suffix)){
			suffix = "co."+suffix;
		}else if ("mx".equals(suffix)){
			suffix = "com."+suffix;
		}
		String link = "http://www.amazon."
				+ suffix
				+ "/ss/customer-reviews/ajax/reviews/get/ref=cm_cr_pr_viewopt_sr?sortBy=recent&reviewerType=all_reviews&formatType=current_format&filterByStar=critical&pageNumber="
				+num+"&pageSize=50&asin=" + asin;
		return link;
	}
	
	@Transient
	public String getProductLink(){
		String suff = country;
		if("uk,jp".contains(country)){
			 suff = "co."+suff;
		}else if("mx".equals(country)){
			suff = "com."+suff;
		}
		return "http://www.amazon."+suff+"/dp/"+asin;
	}

	@Transient
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	
	
	
}


