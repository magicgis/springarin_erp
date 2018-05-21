package com.springrain.erp.modules.ebay.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.ebay.soap.eBLBaseComponents.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "ebay_orderitem")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EbayOrderItem {

	private Integer id;
	private String sku;
	private String title;
	private String itemId;
	private BigDecimal taxes;  //税费
	private String transactionId;  //交易ID
	private Integer quantityPurchased; // 货物数量
	private BigDecimal transactionPrice;  //订单金额
	private BigDecimal vatPercent;// 增值税
	private Date paidTime; 
	private Date shippedTime;
	private String paypalEmailAddress;
	private String paisapayId;
	private Date invoiceSentTime; //账单发送时间
	private EbayOrder order;

	// 新增客户评价
	private String commentText;  //内容
	private String commentType; //评论类型
	private String targetUser; //目标用户

	private BigDecimal finalValueFee;
	private String email;
	private Integer sellingmanagersalesrecordNumber;
	private String orderlineitemId;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public BigDecimal getTaxes() {
		return taxes;
	}

	public void setTaxes(BigDecimal taxes) {
		this.taxes = taxes;
	}

	public Integer getQuantityPurchased() {
		return quantityPurchased;
	}

	public void setQuantityPurchased(Integer quantityPurchased) {
		this.quantityPurchased = quantityPurchased;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public BigDecimal getTransactionPrice() {
		return transactionPrice;
	}

	public void setTransactionPrice(BigDecimal transactionPrice) {
		this.transactionPrice = transactionPrice;
	}

	public BigDecimal getVatPercent() {
		return vatPercent;
	}

	public void setVatPercent(BigDecimal vatPercent) {
		this.vatPercent = vatPercent;
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

	public String getPaypalEmailAddress() {
		return paypalEmailAddress;
	}

	public void setPaypalEmailAddress(String paypalEmailAddress) {
		this.paypalEmailAddress = paypalEmailAddress;
	}

	public String getPaisapayId() {
		return paisapayId;
	}

	public void setPaisapayId(String paisapayId) {
		this.paisapayId = paisapayId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getInvoiceSentTime() {
		return invoiceSentTime;
	}

	public void setInvoiceSentTime(Date invoiceSentTime) {
		this.invoiceSentTime = invoiceSentTime;
	}

	@ManyToOne
	@JoinColumn(name = "order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public EbayOrder getOrder() {
		return order;
	}

	public void setOrder(EbayOrder order) {
		this.order = order;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public String getCommentType() {
		return commentType;
	}

	public void setCommentType(String commentType) {
		this.commentType = commentType;
	}

	public String getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}

	public BigDecimal getFinalValueFee() {
		return finalValueFee;
	}

	public void setFinalValueFee(BigDecimal finalValueFee) {
		this.finalValueFee = finalValueFee;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getSellingmanagersalesrecordNumber() {
		return sellingmanagersalesrecordNumber;
	}

	public void setSellingmanagersalesrecordNumber(
			Integer sellingmanagersalesrecordNumber) {
		this.sellingmanagersalesrecordNumber = sellingmanagersalesrecordNumber;
	}

	public String getOrderlineitemId() {
		return orderlineitemId;
	}

	public void setOrderlineitemId(String orderlineitemId) {
		this.orderlineitemId = orderlineitemId;
	}

	public EbayOrderItem() {
	}

	public EbayOrderItem(TransactionType transaction, EbayOrder order) {
		this.sku = transaction.getItem().getSKU();
		if(transaction.getVariation()!=null&&transaction.getVariation().getSKU()!=null){
			this.sku = transaction.getVariation().getSKU();
		}
		
		this.title = transaction.getItem().getTitle();
		this.itemId = transaction.getItem().getItemID();
		this.taxes = new BigDecimal(transaction.getTaxes().getTotalTaxAmount()
				.getValue());
		this.transactionId = transaction.getTransactionID();
		this.transactionPrice = new BigDecimal(transaction
				.getTransactionPrice().getValue());

		if (transaction.getVATPercent() != null) {
			this.vatPercent = transaction.getVATPercent();
		}
		if (transaction.getPaidTime() != null) {
			this.paidTime = transaction.getPaidTime().getTime();
		}

		if (transaction.getShippedTime() != null) {
			this.shippedTime = transaction.getShippedTime().getTime();
		}
		if (transaction.getPayPalEmailAddress() != null) {
			this.paypalEmailAddress = transaction.getPayPalEmailAddress();
		}
		if (transaction.getPaisaPayID() != null) {
			this.paisapayId = transaction.getPaisaPayID();
		}
		if (transaction.getInvoiceSentTime() != null) {
			this.invoiceSentTime = transaction.getInvoiceSentTime().getTime();
		}
		this.quantityPurchased = transaction.getQuantityPurchased();
		this.order = order;

		if (transaction.getFeedbackLeft() != null) {
			this.commentText = transaction.getFeedbackLeft().getCommentText();
		}
		if (transaction.getFeedbackLeft() != null
				&& transaction.getFeedbackLeft().getCommentType() != null) {
			this.commentType = transaction.getFeedbackLeft().getCommentType()
					.value();
		}
		if (transaction.getFeedbackLeft() != null) {
			this.targetUser = transaction.getFeedbackLeft().getTargetUser();
		}
		if (transaction.getFinalValueFee() != null) {
			this.finalValueFee = new BigDecimal(transaction.getFinalValueFee()
					.getValue());
		}
		if (transaction.getBuyer() != null) {
			this.email = transaction.getBuyer().getEmail();
		}
		if (transaction.getShippingDetails() != null) {
			this.sellingmanagersalesrecordNumber = transaction
					.getShippingDetails().getSellingManagerSalesRecordNumber();
		}
		this.orderlineitemId = transaction.getOrderLineItemID();
	}
}