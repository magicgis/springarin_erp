package com.springrain.erp.modules.amazoninfo.entity.order;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.amazonservices.mws.orders._2013_09_01.model.Money;
import com.amazonservices.mws.orders._2013_09_01.model.OrderItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;

@Entity
@Table(name = "amazoninfo_orderitem")
public class AmazonOrderItem{

	private Integer id ;
	
    private String asin;

    private String sellersku;
    
    private String productName;
    
    private String color;
    
    private String orderItemId;//亚马逊所定义的订单商品编码。

    private String title;

    private Integer quantityOrdered; //订单数量

    private Integer quantityShipped;//已配送的数量

    private Float itemPrice;
    
    private Float itemTax;

    private Float shippingPrice;

    private Float giftWrapPrice;

    private Float shippingTax;

    private Float giftWrapTax;

    private Float shippingDiscount;

    private Float promotionDiscount; //报价中的总促销折扣。

    private String promotionIds; //商品所使用的促销编码 多个逗号隔开

    private Float codFee; //货到付款服务收取的费用。

    private Float codFeeDiscount;

    private String giftMessageText;

    private String giftWrapLevel;

    private String conditionNote;//卖家描述的商品状况。

    private String conditionId; //商品的状况。

    private String conditionSubtypeId; //商品的子状况。

    private Date scheduledDeliveryStartDate; //订单预约送货上门的开始日期

    private Date scheduledDeliveryEndDate;//订单预约送货上门的结束日期
    
    private AmazonOrder order;
    
    
    private String buyerCustomizedInfo;//CustomizedURL
    private String priceDesignation; 
    
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getSellersku() {
		return sellersku;
	}

	public void setSellersku(String sellersku) {
		this.sellersku = sellersku;
	}

	public String getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getQuantityOrdered() {
		return quantityOrdered;
	}

	public void setQuantityOrdered(Integer quantityOrdered) {
		this.quantityOrdered = quantityOrdered;
	}

	public Integer getQuantityShipped() {
		return quantityShipped;
	}

	public void setQuantityShipped(Integer quantityShipped) {
		this.quantityShipped = quantityShipped;
	}

	public Float getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(Float itemPrice) {
		this.itemPrice = itemPrice;
	}

	public Float getShippingPrice() {
		return shippingPrice;
	}

	public void setShippingPrice(Float shippingPrice) {
		this.shippingPrice = shippingPrice;
	}

	public Float getGiftWrapPrice() {
		return giftWrapPrice;
	}

	public void setGiftWrapPrice(Float giftWrapPrice) {
		this.giftWrapPrice = giftWrapPrice;
	}

	public Float getItemTax() {
		return itemTax;
	}

	public void setItemTax(Float itemTax) {
		this.itemTax = itemTax;
	}

	public Float getShippingTax() {
		return shippingTax;
	}

	public void setShippingTax(Float shippingTax) {
		this.shippingTax = shippingTax;
	}

	public Float getGiftWrapTax() {
		return giftWrapTax;
	}

	public void setGiftWrapTax(Float giftWrapTax) {
		this.giftWrapTax = giftWrapTax;
	}

	public Float getShippingDiscount() {
		return shippingDiscount;
	}

	public void setShippingDiscount(Float shippingDiscount) {
		this.shippingDiscount = shippingDiscount;
	}

	public Float getPromotionDiscount() {
		return promotionDiscount;
	}

	public void setPromotionDiscount(Float promotionDiscount) {
		this.promotionDiscount = promotionDiscount;
	}

	public String getPromotionIds() {
		return promotionIds;
	}

	public void setPromotionIds(String promotionIds) {
		this.promotionIds = promotionIds;
	}

	public Float getCodFee() {
		return codFee;
	}

	public void setCodFee(Float codFee) {
		this.codFee = codFee;
	}

	public Float getCodFeeDiscount() {
		return codFeeDiscount;
	}

	public void setCodFeeDiscount(Float codFeeDiscount) {
		this.codFeeDiscount = codFeeDiscount;
	}

	public String getGiftMessageText() {
		return giftMessageText;
	}

	public void setGiftMessageText(String giftMessageText) {
		this.giftMessageText = giftMessageText;
	}

	public String getGiftWrapLevel() {
		return giftWrapLevel;
	}

	public void setGiftWrapLevel(String giftWrapLevel) {
		this.giftWrapLevel = giftWrapLevel;
	}

	public String getConditionNote() {
		return conditionNote;
	}

	public void setConditionNote(String conditionNote) {
		this.conditionNote = conditionNote;
	}

	public String getConditionId() {
		return conditionId;
	}

	public void setConditionId(String conditionId) {
		this.conditionId = conditionId;
	}

	public String getConditionSubtypeId() {
		return conditionSubtypeId;
	}

	public void setConditionSubtypeId(String conditionSubtypeId) {
		this.conditionSubtypeId = conditionSubtypeId;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getScheduledDeliveryStartDate() {
		return scheduledDeliveryStartDate;
	}

	public void setScheduledDeliveryStartDate(Date scheduledDeliveryStartDate) {
		this.scheduledDeliveryStartDate = scheduledDeliveryStartDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getScheduledDeliveryEndDate() {
		return scheduledDeliveryEndDate;
	}

	public void setScheduledDeliveryEndDate(Date scheduledDeliveryEndDate) {
		this.scheduledDeliveryEndDate = scheduledDeliveryEndDate;
	}
	
	//@ManyToOne(optional=true,cascade={javax.persistence.CascadeType.ALL})
	@ManyToOne()
	@JoinColumn(name="order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonOrder getOrder() {
		return order;
	}

	public void setOrder(AmazonOrder order) {
		this.order = order;
	}
	
	public AmazonOrderItem(){
		
	}
	
	@Transient
	public String getName(){
		if(StringUtils.isNotEmpty(color)){
			return productName+"_"+color;
		}
		return productName;
	}

	public String getBuyerCustomizedInfo() {
		return buyerCustomizedInfo;
	}

	public void setBuyerCustomizedInfo(String buyerCustomizedInfo) {
		this.buyerCustomizedInfo = buyerCustomizedInfo;
	}

	public String getPriceDesignation() {
		return priceDesignation;
	}

	public void setPriceDesignation(String priceDesignation) {
		this.priceDesignation = priceDesignation;
	}
	
}
