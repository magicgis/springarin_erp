/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "amazoninfo_sys_promotions")
public class AmazonSysPromotions implements Serializable {
	private      static final long serialVersionUID = -2433140471122314283L;
	private 	Integer 	id; 			// id
	private 	String 		type; 	
	private     User        createUser;   
	private     Date        createDate;  
	private     User        reviewUser;    
	private     Date        reviewDate;    
	private     String      promotionsType;      
	private 	String 		country;    
	private 	String 		status;  
	private     Integer     num;
	private     String      remarks;
	
	private     List<AmazonSysPromotionsItem>        items;

	public AmazonSysPromotions() {
		super();
	}

	public AmazonSysPromotions(Integer id){
		this();
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
	
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	
	@OneToMany(mappedBy = "amazonSysPromotions",fetch=FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<AmazonSysPromotionsItem> getItems() {
		return items;
	}

	public void setItems(List<AmazonSysPromotionsItem> items) {
		this.items = items;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@ManyToOne()
	@JoinColumn(name="review_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getReviewUser() {
		return reviewUser;
	}

	
	public void setReviewUser(User reviewUser) {
		this.reviewUser = reviewUser;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getPromotionsType() {
		return promotionsType;
	}

	public void setPromotionsType(String promotionsType) {
		this.promotionsType = promotionsType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	
	
}


