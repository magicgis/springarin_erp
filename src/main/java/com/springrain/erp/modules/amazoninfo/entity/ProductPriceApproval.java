package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 产品特殊定价管理Entity
 */
@Entity
@Table(name = "amazoninfo_product_price_approval")
public class ProductPriceApproval implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String productName;	//产品名称
	private String sku;			//sku
	private String country;		//国家
	private Float price;		//价格
	private Date saleStartDate;	//起售时间
	private Date saleEndDate;	//截止时间
	private String reason;	//定价原因
	private String state;	//状态0：未审批 1：审批通过 2：审批未通过
	private String isActive;	//是否有效 0：无效 1：有效(定时任务判断)
	
	private User createBy;	//申请人
	private Date createDate;//申请时间
	private User reviewUser;	//审核人
	
	private Date reviewDate;//审核时间
	private Integer warnQty;	//预警销量
	private Integer changeQty;	//自动改价销量
	private Float changePrice;		//自动改价价格
	private String isMonitor;	//是否监控标记 0：否 1：是
	private String noticeFlag;	//是否已发邮件通知  0：否 1：是
	private String type;	//1：降价  2：涨价
	
	private String accountName;
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public ProductPriceApproval() {}

	public ProductPriceApproval(Integer id, String productName,
			String sku, String country, Float price, Date saleStartDate,
			Date saleEndDate, String reason, String state) {
		super();
		this.id = id;
		this.productName = productName;
		this.sku = sku;
		this.country = country;
		this.price = price;
		this.saleStartDate = saleStartDate;
		this.saleEndDate = saleEndDate;
		this.reason = reason;
		this.state = state;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Date getSaleStartDate() {
		return saleStartDate;
	}

	public void setSaleStartDate(Date saleStartDate) {
		this.saleStartDate = saleStartDate;
	}

	public Date getSaleEndDate() {
		return saleEndDate;
	}

	public void setSaleEndDate(Date saleEndDate) {
		this.saleEndDate = saleEndDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public Integer getWarnQty() {
		return warnQty;
	}

	public void setWarnQty(Integer warnQty) {
		this.warnQty = warnQty;
	}

	public Integer getChangeQty() {
		return changeQty;
	}

	public void setChangeQty(Integer changeQty) {
		this.changeQty = changeQty;
	}

	public Float getChangePrice() {
		return changePrice;
	}

	public void setChangePrice(Float changePrice) {
		this.changePrice = changePrice;
	}

	public String getIsMonitor() {
		return isMonitor;
	}

	public void setIsMonitor(String isMonitor) {
		this.isMonitor = isMonitor;
	}

	public String getNoticeFlag() {
		return noticeFlag;
	}

	public void setNoticeFlag(String noticeFlag) {
		this.noticeFlag = noticeFlag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
