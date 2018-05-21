package com.springrain.erp.modules.amazoninfo.entity.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.datatype.XMLGregorianCalendar;

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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.springrain.erp.modules.amazoninfo.scheduler.AmazonWSConfig;
import com.springrain.erp.modules.sys.utils.DictUtils;

@Entity
@Table(name = "amazoninfo_order")
@DynamicInsert
@DynamicUpdate
public class AmazonOrder{
	
	private String rateSn;//税号
	
	private String commentUrl;//评论Url

    private String amazonOrderId; 

    private String sellerOrderId;

    private Date purchaseDate;//创建订单的日期。

    private Date lastUpdateDate;

    private String orderStatus;

    private String fulfillmentChannel;//发货方式  亚马逊配送 (AFN) 或卖家自行配送 (MFN)。

    private String salesChannel;

    private String orderChannel;

    private String shipServiceLevel; //快递、平邮

    private AmazonAddress shippingAddress;
    
    private AmazonAddress invoiceAddress;
    
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
    
    private List<AmazonOrderItem> items = Lists.newArrayList();
    
    private String customId;
    
    
    private List<AmazonRefund> amazonRefunds;
    
    private Date deliveryDate;
    private String remark;
    
    
    private String  replacedOrderId;
    private String  buyerTaxInfo; //CompanyLegalName-TaxingRegion-(tax-tax identifier)
    private String purchaseOrderNumber; 
    
    private String isBusinessOrder;//0:false 1:true
    private String isReplacementOrder;
    private String isPrime;
    private String isPremiumOrder;
    private String paymentMethodDetail;
    
    private String invoiceNo;
    
    private String accountName;
    
    private Date printDate;
    
    
    public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	
	@Transient
	public Date getPrintDate() {
		return printDate;
	}

	public void setPrintDate(Date printDate) {
		this.printDate = printDate;
	}

	@Transient
    public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
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

	@Transient
    public List<AmazonRefund> getAmazonRefunds() {
		return amazonRefunds;
	}

	public void setAmazonRefunds(List<AmazonRefund> amazonRefunds) {
		this.amazonRefunds = amazonRefunds;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getSalesChannel() {
		return salesChannel;
	}

	public void setSalesChannel(String salesChannel) {
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
	public AmazonAddress getInvoiceAddress() {
		return invoiceAddress;
	}

	public void setInvoiceAddress(AmazonAddress invoiceAddress) {
		this.invoiceAddress = invoiceAddress;
	}

	@Transient
	public String getInvoiceFlag() {
		return invoiceFlag;
	}

	public void setInvoiceFlag(String invoiceFlag) {
		this.invoiceFlag = invoiceFlag;
	}
	
	@Transient
	public String getRateSn() {
		return rateSn;
	}

	public void setRateSn(String rateSn) {
		this.rateSn = rateSn;
	}
	
	@Transient
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	@Transient
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
	public AmazonAddress getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(AmazonAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	@OneToMany(mappedBy = "order",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<AmazonOrderItem> getItems() {
		return items;
	}

	public void setItems(List<AmazonOrderItem> items) {
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
	
	public AmazonOrder() {}
	
	@Transient
	@JsonIgnore
	public Date xmlGregorianToDate(XMLGregorianCalendar calendar) {
		if(calendar!=null){
			return calendar.toGregorianCalendar().getTime();
		}
		return null;
	}
	
	private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	public static DateFormat getFormat(){
		return threadLocal.get();
	}
	
	@Transient
	@JsonIgnore
	public Date xmlGregorianToLocalDate(XMLGregorianCalendar calendar,String country) {
		DateFormat sdf = getFormat();
		if(calendar!=null){
			Date date =  calendar.toGregorianCalendar().getTime();
			if("de,it,es,fr".contains(country)){
				sdf.setTimeZone(AmazonWSConfig.get("de").getTimeZone());
			}else{
				try {
					sdf.setTimeZone(AmazonWSConfig.get(country).getTimeZone());
				} catch (Exception e) {
					sdf.setTimeZone(AmazonWSConfig.get(country.contains("com")?"com":country).getTimeZone());
				}
				
			}
			String time = sdf.format(date);
			sdf.setTimeZone(TimeZone.getDefault());
			try {
				return sdf.parse(time);
			} catch (ParseException e) {}
		}
		return null;
	}
	
	@Transient
	@JsonIgnore
	public String getCountry(){
		return DictUtils.getDictLabel(salesChannel.substring(salesChannel.lastIndexOf(".")+1),"platform","");
	}
	
	@Transient
	@JsonIgnore
	public String getCountryChar(){
		return salesChannel.substring(salesChannel.lastIndexOf(".")+1);
	}
	
	@Override
	public String toString() {
		return amazonOrderId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((amazonOrderId == null) ? 0 : amazonOrderId.hashCode());
		result = prime * result
				+ ((salesChannel == null) ? 0 : salesChannel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AmazonOrder other = (AmazonOrder) obj;
		if (amazonOrderId == null) {
			if (other.amazonOrderId != null)
				return false;
		} else if (!amazonOrderId.equals(other.amazonOrderId))
			return false;
		if (salesChannel == null) {
			if (other.salesChannel != null)
				return false;
		} else if (!salesChannel.equals(other.salesChannel))
			return false;
		return true;
	}

	@Transient
	public String getLink(){
		String suf = salesChannel;
		suf = suf.substring(suf.indexOf(".")+1);
		return "http://www.amazon."+suf+"/gp/pdp/profile/";
	}

	public String getIsReplacementOrder() {
		return isReplacementOrder;
	}

	public void setIsReplacementOrder(String isReplacementOrder) {
		this.isReplacementOrder = isReplacementOrder;
	}

	public String getReplacedOrderId() {
		return replacedOrderId;
	}

	public void setReplacedOrderId(String replacedOrderId) {
		this.replacedOrderId = replacedOrderId;
	}

	public String getBuyerTaxInfo() {
		return buyerTaxInfo;
	}

	public void setBuyerTaxInfo(String buyerTaxInfo) {
		this.buyerTaxInfo = buyerTaxInfo;
	}

	public String getIsBusinessOrder() {
		return isBusinessOrder;
	}

	public void setIsBusinessOrder(String isBusinessOrder) {
		this.isBusinessOrder = isBusinessOrder;
	}

	public String getPurchaseOrderNumber() {
		return purchaseOrderNumber;
	}

	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
	}

	public String getIsPrime() {
		return isPrime;
	}

	public void setIsPrime(String isPrime) {
		this.isPrime = isPrime;
	}

	public String getIsPremiumOrder() {
		return isPremiumOrder;
	}

	public void setIsPremiumOrder(String isPremiumOrder) {
		this.isPremiumOrder = isPremiumOrder;
	}

	public String getPaymentMethodDetail() {
		return paymentMethodDetail;
	}

	public void setPaymentMethodDetail(String paymentMethodDetail) {
		this.paymentMethodDetail = paymentMethodDetail;
	}

	
	/*public static void main(String[] args) {
		DateFormat sdf = getFormat();
		Date date =  new Date();
			sdf.setTimeZone(AmazonWSConfig.get("uk").getTimeZone());
		String time = sdf.format(date);
		sdf.setTimeZone(TimeZone.getDefault());
			try {
				System.out.println(sdf.parse(time));
			} catch (ParseException e) {
				e.printStackTrace();
			}
	}*/
}
