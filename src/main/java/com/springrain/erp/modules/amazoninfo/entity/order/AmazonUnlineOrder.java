package com.springrain.erp.modules.amazoninfo.entity.order;


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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "amazoninfo_unline_order")
@DynamicInsert
@DynamicUpdate
public class AmazonUnlineOrder{
	
	private String rateSn;//税号
	
	private String commentUrl;//评论Url

    private String amazonOrderId; 

    private String sellerOrderId;

    private Date purchaseDate;//创建订单的日期。

    private Date lastUpdateDate;

    private String orderStatus;

    private String fulfillmentChannel;//发货方式  亚马逊配送 (AFN) 或卖家自行配送 (MFN)。

    private   Stock    salesChannel;

    private String orderChannel;

    private String shipServiceLevel; //快递、平邮

    private AmazonUnlineAddress shippingAddress;
    
    private AmazonUnlineAddress invoiceAddress;
    
    private String invoiceFlag ;

    private Float orderTotal;

    private Integer numberOfItemsShipped;//已配送数量

    private Integer numberOfItemsUnshipped;//未配送数量

    private String paymentMethod;

    private String marketplaceId;

    private String buyerEmail;

    private String buyerName;

    private String shipmentServiceLevelCategory;

    private String cbaDisplayableShippingLabel; //卖家自定义的配送方式

    private String orderType;

    private Date earliestShipDate;//您承诺的订单发货时间范围的第一天

    private Date latestShipDate;//您承诺的订单最晚发货时间范围的第一天

    private Date earliestDeliveryDate;//最早到达时间

    private Date latestDeliveryDate;//最晚到达时间
    
    private Integer id;
    
    private Date createDate;
    
    private List<AmazonUnlineOrderItem> items = Lists.newArrayList();
    
    private String customId;
    
    private String outBound;
    
    private String billNo;
    private String supplier;
    
    private Date cancelDate;
    private User cancelUser;
    
    private Date deliveryDate;
    private String remark;
    
    private String origin;     //（中国仓）另外下单，还是库存转化     1：仓库                      0下单
    
    private String outBoundNo;
    
    private String invoiceNo;
    
    public String getOutBoundNo() {
		return outBoundNo;
	}

	public void setOutBoundNo(String outBoundNo) {
		this.outBoundNo = outBoundNo;
	}

	@Transient
    public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Transient
    @Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
    
    public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	@ManyToOne()
	@JoinColumn(name = "cancel_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCancelUser() {
		return cancelUser;
	}

	

	public void setCancelUser(User cancelUser) {
		this.cancelUser = cancelUser;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getOutBound() {
		return outBound;
	}

	public void setOutBound(String outBound) {
		this.outBound = outBound;
	}

	public String getAmazonOrderId() {
		return amazonOrderId;
	}
	public void setAmazonOrderId(String amazonOrderId) {
		this.amazonOrderId = amazonOrderId;
	}

	public String getSellerOrderId() {
		return sellerOrderId;
	}
	
	public void setSellerOrderId(String sellerOrderId) {
		this.sellerOrderId = sellerOrderId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getFulfillmentChannel() {
		return fulfillmentChannel;
	}

	public void setFulfillmentChannel(String fulfillmentChannel) {
		this.fulfillmentChannel = fulfillmentChannel;
	}

	@ManyToOne()
	@JoinColumn(name="sales_channel")
	@NotFound(action = NotFoundAction.IGNORE)
	public Stock getSalesChannel() {
		return salesChannel;
	}

	public void setSalesChannel(Stock salesChannel) {
		this.salesChannel = salesChannel;
	}

	public String getOrderChannel() {
		return orderChannel;
	}

	public void setOrderChannel(String orderChannel) {
		this.orderChannel = orderChannel;
	}

	public String getShipServiceLevel() {
		return shipServiceLevel;
	}

	public void setShipServiceLevel(String shipServiceLevel) {
		this.shipServiceLevel = shipServiceLevel;
	}

	public Float getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(Float orderTotal) {
		this.orderTotal = orderTotal;
	}

	public Integer getNumberOfItemsShipped() {
		return numberOfItemsShipped;
	}

	public void setNumberOfItemsShipped(Integer numberOfItemsShipped) {
		this.numberOfItemsShipped = numberOfItemsShipped;
	}

	public Integer getNumberOfItemsUnshipped() {
		return numberOfItemsUnshipped;
	}

	public void setNumberOfItemsUnshipped(Integer numberOfItemsUnshipped) {
		this.numberOfItemsUnshipped = numberOfItemsUnshipped;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getMarketplaceId() {
		return marketplaceId;
	}

	public void setMarketplaceId(String marketplaceId) {
		this.marketplaceId = marketplaceId;
	}

	public String getBuyerEmail() {
		return buyerEmail;
	}

	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getShipmentServiceLevelCategory() {
		return shipmentServiceLevelCategory;
	}

	public void setShipmentServiceLevelCategory(String shipmentServiceLevelCategory) {
		this.shipmentServiceLevelCategory = shipmentServiceLevelCategory;
	}

	public String getCbaDisplayableShippingLabel() {
		return cbaDisplayableShippingLabel;
	}

	public void setCbaDisplayableShippingLabel(String cbaDisplayableShippingLabel) {
		this.cbaDisplayableShippingLabel = cbaDisplayableShippingLabel;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public Date getEarliestShipDate() {
		return earliestShipDate;
	}

	public void setEarliestShipDate(Date earliestShipDate) {
		this.earliestShipDate = earliestShipDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getLatestShipDate() {
		return latestShipDate;
	}

	public void setLatestShipDate(Date latestShipDate) {
		this.latestShipDate = latestShipDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getEarliestDeliveryDate() {
		return earliestDeliveryDate;
	}

	public void setEarliestDeliveryDate(Date earliestDeliveryDate) {
		this.earliestDeliveryDate = earliestDeliveryDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getLatestDeliveryDate() {
		return latestDeliveryDate;
	}

	public void setLatestDeliveryDate(Date latestDeliveryDate) {
		this.latestDeliveryDate = latestDeliveryDate;
	}
	
	@OneToOne
	@JoinColumn(name="invoice_address")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public AmazonUnlineAddress getInvoiceAddress() {
		return invoiceAddress;
	}

	public void setInvoiceAddress(AmazonUnlineAddress invoiceAddress) {
		this.invoiceAddress = invoiceAddress;
	}

	public String getInvoiceFlag() {
		return invoiceFlag;
	}

	public void setInvoiceFlag(String invoiceFlag) {
		this.invoiceFlag = invoiceFlag;
	}
	
	public String getRateSn() {
		return rateSn;
	}

	public void setRateSn(String rateSn) {
		this.rateSn = rateSn;
	}
	
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getCommentUrl() {
		return commentUrl;
	}

	public void setCommentUrl(String commentUrl) {
		this.commentUrl = commentUrl;
	}

	@OneToOne
	@JoinColumn(name="shipping_address")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public AmazonUnlineAddress getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(AmazonUnlineAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	@OneToMany(mappedBy = "order",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@OrderBy(value="id")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<AmazonUnlineOrderItem> getItems() {
		return items;
	}

	public void setItems(List<AmazonUnlineOrderItem> items) {
		this.items = items;
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
	
	@PrePersist
	public void prePersist(){
		this.createDate = new Date();
	}
	
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public AmazonUnlineOrder() {}

	
}
