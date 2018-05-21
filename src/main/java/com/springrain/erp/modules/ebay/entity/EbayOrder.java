package com.springrain.erp.modules.ebay.entity;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.ebay.soap.eBLBaseComponents.AddressType;
import com.ebay.soap.eBLBaseComponents.ExternalTransactionType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.PaymentTransactionType;
import com.ebay.soap.eBLBaseComponents.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.MessageUtils;
import com.springrain.erp.modules.amazoninfo.scheduler.AmazonWSConfig;
import com.springrain.erp.modules.ebay.scheduler.EbayConstants;

@Entity
@Table(name = "ebay_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EbayOrder {
	private Integer id;
	private String rateSn;// 税号
	private String orderId;
	private String orderStatus;  //订单状态
	private String checkoutStatus; //支付状态
	private Date createdTime;  //创建时间
	private String paymentMethods; //支付方式
	private BigDecimal total; //订单总额
	private BigDecimal shippingServiceCost; //运输金额
	private String buyerUserId; //购买人ID
	private Date paidTime; //支付时间
	private Date shippedTime; //付款时间
	private Date lastModifiedTime; //最后修改时间
	private String status="";
	private String sellerEmail="";

	private EbayAddress shippingAddress; // 送货地址
	private EbayAddress invoiceAddress; // 订单地址
	private List<EbayOrderItem> items = Lists.newArrayList();
	
	private String country;

	// 新增字段
	private BigDecimal adjustmentAmount; //调整金额
	private BigDecimal amountPaid;
	private BigDecimal amountSaved;
	private String paymentStatus; //支付状态
	private BigDecimal subtotal; //小计
	private BigDecimal shippinginsuranceCost;
	private String shippingService; //运输服务商
	private String externaltransactionId;
	private Date externaltransactionTime;
	private BigDecimal feeorcreditAmount;
	private BigDecimal paymentorrefundAmount;
	private String eiasToken;
	private String invoiceFlag="0";
	
	private String buyerEmail;  //购买人邮箱
	private String invoiceNo;

	@Transient
	public String getBuyerEmail() {
		Set<String> set = Sets.newHashSet();
		buyerEmail = "";
		for (EbayOrderItem item : items) {
			set.add(item.getEmail());
		}
		set.remove("Invalid Request");
		if (set.size() == 1) {
			buyerEmail = set.iterator().next();
		} else if (set.size() > 1) {
			buyerEmail = set.toString().replaceAll("[\\[|\\]]", "");
		}
		return buyerEmail;
	}
	
	@Transient
	public String getEncryptionBuyerEmail() {
		String tempEmail=getBuyerEmail();
		String rs="";
		 if(StringUtils.isNotBlank(tempEmail)&&tempEmail.contains("@")){
			String[] arr = tempEmail.split("@");
			rs="erp"+Encodes.encodeBase64(arr[0].getBytes())+"@"+arr[1];
		 }
		return rs;
	}

	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getCheckoutStatus() {
		return checkoutStatus;
	}

	public void setCheckoutStatus(String checkoutStatus) {
		this.checkoutStatus = checkoutStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(String paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getBuyerUserId() {
		return buyerUserId;
	}

	public void setBuyerUserId(String buyerUserId) {
		this.buyerUserId = buyerUserId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getPaidTime() {
		return paidTime;
	}

	public void setPaidTime(Date paidTime) {
		this.paidTime = paidTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getShippedTime() {
		return shippedTime;
	}

	public void setShippedTime(Date shippedTime) {
		this.shippedTime = shippedTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Transient
	public String getStatusW() {
		if (StringUtils.isNotEmpty(status)) {
			if ("0".equals(status)) {
				return MessageUtils.format("ebay_order_status_nopay");
			} else if ("1".equals(status)) {
				return MessageUtils.format("ebay_order_status_ispay");
			} else {
				return MessageUtils.format("ebay_order_status_shipped");
			}
		}
		return "";
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "shipping_address")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public EbayAddress getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(EbayAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "invoice_address")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public EbayAddress getInvoiceAddress() {
		return invoiceAddress;
	}

	public void setInvoiceAddress(EbayAddress invoiceAddress) {
		this.invoiceAddress = invoiceAddress;
	}

	@OneToMany(mappedBy = "order",fetch=FetchType.EAGER,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@OrderBy(value = "id")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<EbayOrderItem> getItems() {
		return items;
	}

	public void setItems(List<EbayOrderItem> items) {
		this.items = items;
	}

	public BigDecimal getShippingServiceCost() {
		return shippingServiceCost;
	}

	public void setShippingServiceCost(BigDecimal shippingServiceCost) {
		this.shippingServiceCost = shippingServiceCost;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public BigDecimal getAdjustmentAmount() {
		return adjustmentAmount;
	}

	public void setAdjustmentAmount(BigDecimal adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public BigDecimal getAmountSaved() {
		return amountSaved;
	}

	public void setAmountSaved(BigDecimal amountSaved) {
		this.amountSaved = amountSaved;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getShippinginsuranceCost() {
		return shippinginsuranceCost;
	}

	public void setShippinginsuranceCost(BigDecimal shippinginsuranceCost) {
		this.shippinginsuranceCost = shippinginsuranceCost;
	}

	public String getShippingService() {
		return shippingService;
	}

	public void setShippingService(String shippingService) {
		this.shippingService = shippingService;
	}

	public String getExternaltransactionId() {
		return externaltransactionId;
	}

	public void setExternaltransactionId(String externaltransactionId) {
		this.externaltransactionId = externaltransactionId;
	}

	public Date getExternaltransactionTime() {
		return externaltransactionTime;
	}

	public void setExternaltransactionTime(Date externaltransactionTime) {
		this.externaltransactionTime = externaltransactionTime;
	}

	public BigDecimal getFeeorcreditAmount() {
		return feeorcreditAmount;
	}

	public void setFeeorcreditAmount(BigDecimal feeorcreditAmount) {
		this.feeorcreditAmount = feeorcreditAmount;
	}

	public BigDecimal getPaymentorrefundAmount() {
		return paymentorrefundAmount;
	}

	public void setPaymentorrefundAmount(BigDecimal paymentorrefundAmount) {
		this.paymentorrefundAmount = paymentorrefundAmount;
	}

	public String getEiasToken() {
		return eiasToken;
	}

	public void setEiasToken(String eiasToken) {
		this.eiasToken = eiasToken;
	}

	@Transient
	public String getSellerEmail() {
		return sellerEmail;
	}

	public void setSellerEmail(String sellerEmail) {
		this.sellerEmail = sellerEmail;
	}
	

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public EbayOrder() {
	}
	
	
	@Transient
	@JsonIgnore
	public Date xmlGregorianToLocalDate(Calendar calendar,String country) {
		DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(calendar!=null){
			Date date =  calendar.getTime();
			if("de,it,es,fr".contains(country)){
				sdf.setTimeZone(AmazonWSConfig.get("de").getTimeZone());
			}else{
				sdf.setTimeZone(AmazonWSConfig.get(country).getTimeZone());
			}
			String time = sdf.format(date);
			sdf.setTimeZone(TimeZone.getDefault());
			try {
				return sdf.parse(time);
			} catch (ParseException e) {}
		}
		return null;
	}
	

	public EbayOrder(OrderType order,String country) {
		this.orderId = order.getOrderID();
		this.orderStatus = order.getOrderStatus().value();
		this.checkoutStatus = order.getCheckoutStatus().getStatus().value();
		this.createdTime = xmlGregorianToLocalDate(order.getCreatedTime(),country);

		if (order.getCheckoutStatus().getPaymentMethod().value() != null) {
			this.paymentMethods = order.getCheckoutStatus().getPaymentMethod()
					.value();
		}

		this.total = new BigDecimal(order.getTotal().getValue());
		this.buyerUserId = order.getBuyerUserID();

		if (order.getPaidTime() != null) {
			this.paidTime = xmlGregorianToLocalDate(order.getPaidTime(),country);
			if ("Complete".equals(checkoutStatus)
					&& "NoPaymentFailure".equals(order.getCheckoutStatus()
							.getEBayPaymentStatus().value())) {
				if (order.getShippedTime() != null) {
					this.shippedTime = xmlGregorianToLocalDate(order.getShippedTime(),country);
					this.status = EbayConstants.SHIPPED;
				}else{
					this.status = EbayConstants.PAY;
				}
			}else if("Incomplete".equals(checkoutStatus)
					&& "NoPaymentFailure".equals(order.getCheckoutStatus()
							.getEBayPaymentStatus().value())){
				if (order.getShippedTime() != null) {
					this.shippedTime = xmlGregorianToLocalDate(order.getShippedTime(),country);
					this.status = EbayConstants.SHIPPED;
				}else{
					this.status = EbayConstants.PAY;
				}
				
			}else{
				if (order.getShippedTime() != null) {
					this.shippedTime = xmlGregorianToLocalDate(order.getShippedTime(),country);
					this.status = EbayConstants.CASHONDELIVERY;
				}else{
					this.status = EbayConstants.NOPAY;
				}
			}
		} else {
			if (order.getShippedTime() != null) {
				this.shippedTime = xmlGregorianToLocalDate(order.getShippedTime(),country);
				this.status = EbayConstants.CASHONDELIVERY;
			}else{
				this.status = EbayConstants.NOPAY;
			}
		}

		if (order.getCheckoutStatus().getLastModifiedTime() != null) {
			this.lastModifiedTime = xmlGregorianToLocalDate(order.getCheckoutStatus()
					.getLastModifiedTime(),country);
		}

		if (order.getShippingServiceSelected().getShippingServiceCost() != null) {
			this.shippingServiceCost = new BigDecimal(order
					.getShippingServiceSelected().getShippingServiceCost()
					.getValue());
		}

		if (order.getAdjustmentAmount() != null) {
			this.adjustmentAmount = new BigDecimal(order.getAdjustmentAmount()
					.getValue());
		}
		if (order.getAmountPaid() != null) {
			this.amountPaid = new BigDecimal(order.getAmountPaid().getValue());
		}
		if (order.getAmountSaved() != null) {
			this.amountSaved = new BigDecimal(order.getAmountSaved().getValue());
		}

		if (order.getCheckoutStatus() != null
				&& order.getCheckoutStatus().getEBayPaymentStatus() != null) {
			this.paymentStatus = order.getCheckoutStatus()
					.getEBayPaymentStatus().value();
		}
		if (order.getSubtotal() != null) {
			this.subtotal = new BigDecimal(order.getSubtotal().getValue());
		}
		if (order.getShippingServiceSelected() != null
				&& order.getShippingServiceSelected()
						.getShippingInsuranceCost() != null) {
			this.shippinginsuranceCost = new BigDecimal(order
					.getShippingServiceSelected().getShippingInsuranceCost()
					.getValue());
		}
		if (order.getShippingServiceSelected() != null) {
			this.shippingService = order.getShippingServiceSelected()
					.getShippingService();
		}
		this.eiasToken = order.getEIASToken();

		ExternalTransactionType[] externalTransaction1 = null;
		if (order.getExternalTransaction() != null) {
			externalTransaction1 = order.getExternalTransaction();
		}
		if (externalTransaction1 != null) {
			for (int i = 0; i < externalTransaction1.length; i++) {
				this.externaltransactionId = externalTransaction1[i]
						.getExternalTransactionID();
				this.externaltransactionTime = externalTransaction1[i]
						.getExternalTransactionTime().getTime();
			}
		}

		PaymentTransactionType[] pt = null;
		if (order.getMonetaryDetails() != null
				&& order.getMonetaryDetails().getPayments() != null
				&& order.getMonetaryDetails().getPayments().getPayment() != null) {
			pt = order.getMonetaryDetails().getPayments().getPayment();
		}
		if (pt != null) {
			for (int i = 0; i < pt.length; i++) {
				if(pt[i].getPaymentAmount()!=null){
					this.paymentorrefundAmount = new BigDecimal(pt[i]
							.getPaymentAmount().getValue());
				}
				if(pt[i].getFeeOrCreditAmount()!=null){
					this.feeorcreditAmount = new BigDecimal(pt[i]
							.getFeeOrCreditAmount().getValue());
				}
			}
		}

		AddressType address = order.getShippingAddress();
		if (address != null) {
			this.shippingAddress = new EbayAddress(address, this);
		}

		TransactionType[] items = order.getTransactionArray().getTransaction();
		if (items != null && items.length > 0) {
			List<EbayOrderItem> ls = Lists.newArrayList();
			for (TransactionType transactionType : items) {
				ls.add(new EbayOrderItem(transactionType, this));
			}
			this.items = ls;
		}
	}
}
