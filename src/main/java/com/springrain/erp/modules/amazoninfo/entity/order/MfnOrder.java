package com.springrain.erp.modules.amazoninfo.entity.order;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.IdGen;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "amazoninfo_ebay_order")
public class MfnOrder implements Comparable<MfnOrder>{
	private String id;
	private String orderId;
	private String status;
	private Date buyTime;
	private String buyerUser;
    private MfnAddress shippingAddress;
    private MfnAddress invoiceAddress;
    private Date lastModifiedTime;
    private String rateSn;
    private String country;
    private String buyerUserEmail;
    private Float orderTotal;
    private String paymentMethod;
    private Date paidTime;
    private String orderType;
    private Date shippedTime;
    private Float shippingServiceCost;
    private String channel;
    private String remark;
    private String supplier;
    private String trackNumber;
    private User createUser;
    
    private List<MfnOrderItem> items = Lists.newArrayList();
    
    private MfnPackage mfnPackage;
    
    private String eventId;
    
    private Integer billNo;
    
    private String showBillNo;
    
    private String isOld;
    
    private String accountName;
    
    private Date trackingDate;
    
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private Integer weight;
    private String trackingFlag;
    private String labelImage;
    private Float fee;
    
    private String pdfImage;
    private String trackingRemark;
    
    public String getTrackingRemark() {
		return trackingRemark;
	}

	public void setTrackingRemark(String trackingRemark) {
		this.trackingRemark = trackingRemark;
	}

	@Transient
    public String getPdfImage() {
		return pdfImage;
	}

	public void setPdfImage(String pdfImage) {
		this.pdfImage = pdfImage;
	}

	public String getLabelImage() {
		return labelImage;
	}

	public void setLabelImage(String labelImage) {
		this.labelImage = labelImage;
	}

	public Float getFee() {
		return fee;
	}

	public void setFee(Float fee) {
		this.fee = fee;
	}

	public String getTrackingFlag() {
		return trackingFlag;
	}

	public void setTrackingFlag(String trackingFlag) {
		this.trackingFlag = trackingFlag;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	
    
    public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Transient
	public String getIsOld() {
		return isOld;
	}
	public void setIsOld(String isOld) {
		this.isOld = isOld;
	}
	
	@Transient
	public String getShowBillNo() {
		return showBillNo;
	}
	public void setShowBillNo(String showBillNo) {
		this.showBillNo = showBillNo;
	}
	
	public Integer getBillNo() {
		return billNo;
	}
	public void setBillNo(Integer billNo) {
		this.billNo = billNo;
	}
	@Transient
    public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	
	
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	@ManyToOne()
	@JoinColumn(name="package_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public MfnPackage getMfnPackage() {
		return mfnPackage;
	}
	public void setMfnPackage(MfnPackage mfnPackage) {
		this.mfnPackage = mfnPackage;
	}
	@PrePersist
	public void prePersist(){
		this.id = IdGen.uuid();
	}
	@Id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
    @OneToMany(mappedBy = "order",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<MfnOrderItem> getItems() {
		return items;
	}
	
	public void setItems(List<MfnOrderItem> items) {
		this.items = items;
	}
	
	
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getShippedTime() {
		return shippedTime;
	}
	public void setShippedTime(Date shippedTime) {
		this.shippedTime = shippedTime;
	}
	public Float getShippingServiceCost() {
		return shippingServiceCost;
	}
	public void setShippingServiceCost(Float shippingServiceCost) {
		this.shippingServiceCost = shippingServiceCost;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getPaidTime() {
		return paidTime;
	}
	public void setPaidTime(Date paidTime) {
		this.paidTime = paidTime;
	}
	
	

	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getBuyTime() {
		return buyTime;
	}
	public void setBuyTime(Date buyTime) {
		this.buyTime = buyTime;
	}
	public String getBuyerUser() {
		return buyerUser;
	}
	public void setBuyerUser(String buyerUser) {
		this.buyerUser = buyerUser;
	}
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "shipping_address")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public MfnAddress getShippingAddress() {
		return shippingAddress;
	}
	public void setShippingAddress(MfnAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
	

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "invoice_address")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public MfnAddress getInvoiceAddress() {
		return invoiceAddress;
	}
	public void setInvoiceAddress(MfnAddress invoiceAddress) {
		this.invoiceAddress = invoiceAddress;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public String getRateSn() {
		return rateSn;
	}
	public void setRateSn(String rateSn) {
		this.rateSn = rateSn;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getBuyerUserEmail() {
		return buyerUserEmail;
	}
	public void setBuyerUserEmail(String buyerUserEmail) {
		this.buyerUserEmail = buyerUserEmail;
	}
	
	public Float getOrderTotal() {
		return orderTotal;
	}
	public void setOrderTotal(Float orderTotal) {
		this.orderTotal = orderTotal;
	}
	
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getTrackNumber() {
		return trackNumber;
	}
	public void setTrackNumber(String trackNumber) {
		this.trackNumber = trackNumber;
	}
	@Override
	public int compareTo(MfnOrder order) {
		return this.showBillNo.compareTo(order.getShowBillNo());
	}
	
	@Transient
	public String getGroupBillNo(){
		if(billNo!=null){
			if("0".equals(orderType)){
				if(billNo.toString().length()<8){
					String num="";
					StringBuffer buf= new StringBuffer();
					for(int m=0;m<8-billNo.toString().length();m++){
						buf.append("0");
					}
					if(StringUtils.isNotBlank(buf.toString())){
						num=buf.toString();
					}
					return num+billNo;
				}else{
					return billNo+"";
				}
			}else if("1".equals(orderType)){//test
				return "Test "+billNo;
			}else if("2".equals(orderType)||"5".equals(orderType)){//support
				return "Ersatz "+billNo;
			}else if("3".equals(orderType)){
				return "Mfn "+billNo;
			}
		}
		return null;
	}
	
	@ManyToOne()
	@JoinColumn(name = "create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}
	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	@Transient
	public Date getTrackingDate() {
		return trackingDate;
	}

	public void setTrackingDate(Date trackingDate) {
		this.trackingDate = trackingDate;
	}


	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

}
