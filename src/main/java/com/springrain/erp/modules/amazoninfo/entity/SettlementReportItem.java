package com.springrain.erp.modules.amazoninfo.entity;

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

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "settlementreport_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SettlementReportItem {
	private Integer id;
	private String amazonOrderItemCode;
	private String merchantAdjustmentItemId;
	private String sku;
	private Integer quantity;
	private BigDecimal principal;
	private BigDecimal shipping;
	
	private BigDecimal shippingHb;
	private BigDecimal shipmentFee;
	
	private BigDecimal  giftWrap;
	
	private BigDecimal  goodwill;
	
	
	private BigDecimal crossBorderFulfillmentFee;
	private BigDecimal fbaPerUnitFulfillmentFee;
	private BigDecimal FbaPerOrderFulfillmentFee;
	
	private BigDecimal fbaWeightBasedFee;
	private BigDecimal commission;
	private BigDecimal shippingChargeback;
	private BigDecimal giftwrapChargeback;
	private BigDecimal refundCommission;
	
	private BigDecimal restockingFee;
	private BigDecimal promotion;
	private BigDecimal cod;
	private BigDecimal codFee;
	private BigDecimal otherFee;
	
	private Date addTime;
	private BigDecimal tax; //美国州税
	private BigDecimal salesTaxServiceFee; //州税服务费
	private BigDecimal shippingTax;
	

	private SettlementReportOrder order;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public BigDecimal getGiftWrap() {
		return giftWrap;
	}

	public void setGiftWrap(BigDecimal giftWrap) {
		this.giftWrap = giftWrap;
	}
	
	public BigDecimal getGoodwill() {
		return goodwill;
	}

	public void setGoodwill(BigDecimal goodwill) {
		this.goodwill = goodwill;
	}

	public BigDecimal getShippingHb() {
		return shippingHb;
	}

	public void setShippingHb(BigDecimal shippingHb) {
		this.shippingHb = shippingHb;
	}
	
	public BigDecimal getFbaPerOrderFulfillmentFee() {
		return FbaPerOrderFulfillmentFee;
	}

	public void setFbaPerOrderFulfillmentFee(BigDecimal fbaPerOrderFulfillmentFee) {
		FbaPerOrderFulfillmentFee = fbaPerOrderFulfillmentFee;
	}

	public BigDecimal getShipmentFee() {
		return shipmentFee;
	}

	public void setShipmentFee(BigDecimal shipmentFee) {
		this.shipmentFee = shipmentFee;
	}

	public BigDecimal getCodFee() {
		return codFee;
	}

	public void setCodFee(BigDecimal codFee) {
		this.codFee = codFee;
	}
	
	public BigDecimal getOtherFee() {
		return otherFee;
	}

	public void setOtherFee(BigDecimal otherFee) {
		this.otherFee = otherFee;
	}

	public BigDecimal getRestockingFee() {
		return restockingFee;
	}

	public void setRestockingFee(BigDecimal restockingFee) {
		this.restockingFee = restockingFee;
	}

	public String getAmazonOrderItemCode() {
		return amazonOrderItemCode;
	}

	public void setAmazonOrderItemCode(String amazonOrderItemCode) {
		this.amazonOrderItemCode = amazonOrderItemCode;
	}

	public String getMerchantAdjustmentItemId() {
		return merchantAdjustmentItemId;
	}

	public void setMerchantAdjustmentItemId(String merchantAdjustmentItemId) {
		this.merchantAdjustmentItemId = merchantAdjustmentItemId;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrincipal() {
		return principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}
	
	

	public BigDecimal getPromotion() {
		return promotion;
	}

	public void setPromotion(BigDecimal promotion) {
		this.promotion = promotion;
	}

	public BigDecimal getShipping() {
		return shipping;
	}

	public void setShipping(BigDecimal shipping) {
		this.shipping = shipping;
	}

	public BigDecimal getCrossBorderFulfillmentFee() {
		return crossBorderFulfillmentFee;
	}

	public void setCrossBorderFulfillmentFee(
			BigDecimal crossBorderFulfillmentFee) {
		this.crossBorderFulfillmentFee = crossBorderFulfillmentFee;
	}

	public BigDecimal getFbaPerUnitFulfillmentFee() {
		return fbaPerUnitFulfillmentFee;
	}

	public void setFbaPerUnitFulfillmentFee(BigDecimal fbaPerUnitFulfillmentFee) {
		this.fbaPerUnitFulfillmentFee = fbaPerUnitFulfillmentFee;
	}

	public BigDecimal getFbaWeightBasedFee() {
		return fbaWeightBasedFee;
	}

	public void setFbaWeightBasedFee(BigDecimal fbaWeightBasedFee) {
		this.fbaWeightBasedFee = fbaWeightBasedFee;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getShippingChargeback() {
		return shippingChargeback;
	}

	public void setShippingChargeback(BigDecimal shippingChargeback) {
		this.shippingChargeback = shippingChargeback;
	}

	public BigDecimal getGiftwrapChargeback() {
		return giftwrapChargeback;
	}

	public void setGiftwrapChargeback(BigDecimal giftwrapChargeback) {
		this.giftwrapChargeback = giftwrapChargeback;
	}

	public BigDecimal getRefundCommission() {
		return refundCommission;
	}

	public void setRefundCommission(BigDecimal refundCommission) {
		this.refundCommission = refundCommission;
	}
	
	public BigDecimal getCod() {
		return cod;
	}

	public void setCod(BigDecimal cod) {
		this.cod = cod;
	}

	@ManyToOne
	@JoinColumn(name = "order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public SettlementReportOrder getOrder() {
		return order;
	}

	public void setOrder(SettlementReportOrder order) {
		this.order = order;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public BigDecimal getSalesTaxServiceFee() {
		return salesTaxServiceFee;
	}

	public void setSalesTaxServiceFee(BigDecimal salesTaxServiceFee) {
		this.salesTaxServiceFee = salesTaxServiceFee;
	}

	public BigDecimal getShippingTax() {
		return shippingTax;
	}

	public void setShippingTax(BigDecimal shippingTax) {
		this.shippingTax = shippingTax;
	}
}
