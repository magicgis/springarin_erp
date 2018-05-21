/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "amazoninfo_operational_report")
public class AmazonOperationalReport implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id; 		// 编号
	private Date createDate;
	private String country;
	private Integer session;
	private Integer salesVolume;
	private Integer returnVolume;
	private Integer orderVolume;

	private float sales;
	private Integer badReview;
	private Integer totalReview;
	private  Integer sessionOrder;
	
	private Date  endDate;
	
	private String searchType;
	
	private String date1;
	
	private String date2;
	
	private Integer maxOrder;
	private Integer promotionsOrder;
	private Integer flashSalesOrder;
	
	@Transient
	public Integer getMaxOrder() {
		return maxOrder;
	}

	public void setMaxOrder(Integer maxOrder) {
		this.maxOrder = maxOrder;
	}
	@Transient
	public Integer getPromotionsOrder() {
		return promotionsOrder;
	}

	public void setPromotionsOrder(Integer promotionsOrder) {
		this.promotionsOrder = promotionsOrder;
	}
	@Transient
	public Integer getFlashSalesOrder() {
		return flashSalesOrder;
	}

	public void setFlashSalesOrder(Integer flashSalesOrder) {
		this.flashSalesOrder = flashSalesOrder;
	}

	public Integer getSessionOrder() {
		return sessionOrder;
	}

	public void setSessionOrder(Integer sessionOrder) {
		this.sessionOrder = sessionOrder;
	}

	@Transient
	public String getDate1() {
		return date1;
	}

	public void setDate1(String date1) {
		this.date1 = date1;
	}
	
	@Transient
	public String getDate2() {
		return date2;
	}

	public void setDate2(String date2) {
		this.date2 = date2;
	}

	@Transient
	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	@Transient
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public AmazonOperationalReport() {
		super();
	}

	
	
	public AmazonOperationalReport(String country, Integer session,
			Integer salesVolume, Integer returnVolume, Integer orderVolume,
			float sales, Integer badReview, Integer totalReview,Integer sessionOrder) {
		super();
		this.country = country;
		this.session = session;
		this.salesVolume = salesVolume;
		this.returnVolume = returnVolume;
		this.orderVolume = orderVolume;
		this.sales = sales;
		this.badReview = badReview;
		this.totalReview = totalReview;
		this.sessionOrder=sessionOrder;
	}

	
	public AmazonOperationalReport(Integer maxOrder, Integer promotionsOrder,
			Integer flashSalesOrder) {
		super();
		this.maxOrder = maxOrder;
		this.promotionsOrder = promotionsOrder;
		this.flashSalesOrder = flashSalesOrder;
	}

	public AmazonOperationalReport(Integer id){
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
	
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
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

	public Integer getSession() {
		return session;
	}

	public void setSession(Integer session) {
		this.session = session;
	}

	public Integer getSalesVolume() {
		return salesVolume;
	}

	public void setSalesVolume(Integer salesVolume) {
		this.salesVolume = salesVolume;
	}

	public Integer getReturnVolume() {
		return returnVolume;
	}

	public void setReturnVolume(Integer returnVolume) {
		this.returnVolume = returnVolume;
	}

	public Integer getOrderVolume() {
		return orderVolume;
	}

	public void setOrderVolume(Integer orderVolume) {
		this.orderVolume = orderVolume;
	}


	public float getSales() {
		return sales;
	}

	public void setSales(float sales) {
		this.sales = sales;
	}

	public Integer getBadReview() {
		return badReview;
	}

	public void setBadReview(Integer badReview) {
		this.badReview = badReview;
	}

	public Integer getTotalReview() {
		return totalReview;
	}

	public void setTotalReview(Integer totalReview) {
		this.totalReview = totalReview;
	}

	
}


