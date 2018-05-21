package com.springrain.erp.modules.amazoninfo.entity.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.google.common.collect.Lists;

@Entity
@Table(name = "amazoninfo_vendor_order")
public class VendorOrder implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String status;
	
	private String country;
	
	private String orderId;
	
	private String shipToLocation;//收货地址
	
	private Date orderedDate; 
	
	private String deliveryWindow;
	
	private String freightTerms;
	
	private String paymentMethod;
	
	private String paymentTerms;
	
	private String purchasingEntity;
	
	private BigDecimal submittedTotalCost;
	
	private BigDecimal acceptedTotalCost;
	
	private BigDecimal cancelledTotalCost;
	
	private BigDecimal receivedTotalCost;
	
	private List<VendorOrderItem> items = Lists.newArrayList();
	
	private VendorShipment shipment;
	
	private String qtyFlag;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@ManyToOne()
	@JoinColumn(name="shipment_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public VendorShipment getShipment() {
		return shipment;
	}

	public void setShipment(VendorShipment shipment) {
		this.shipment = shipment;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getShipToLocation() {
		return shipToLocation;
	}

	public void setShipToLocation(String shipToLocation) {
		this.shipToLocation = shipToLocation;
	}

	public Date getOrderedDate() {
		return orderedDate;
	}

	public void setOrderedDate(Date orderedDate) {
		this.orderedDate = orderedDate;
	}

	public String getDeliveryWindow() {
		return deliveryWindow;
	}

	public void setDeliveryWindow(String deliveryWindow) {
		this.deliveryWindow = deliveryWindow;
	}

	public String getFreightTerms() {
		return freightTerms;
	}

	public void setFreightTerms(String freightTerms) {
		this.freightTerms = freightTerms;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	public String getPurchasingEntity() {
		return purchasingEntity;
	}

	public void setPurchasingEntity(String purchasingEntity) {
		this.purchasingEntity = purchasingEntity;
	}

	public BigDecimal getSubmittedTotalCost() {
		return submittedTotalCost;
	}

	public void setSubmittedTotalCost(BigDecimal submittedTotalCost) {
		this.submittedTotalCost = submittedTotalCost;
	}

	public BigDecimal getAcceptedTotalCost() {
		return acceptedTotalCost;
	}

	public void setAcceptedTotalCost(BigDecimal acceptedTotalCost) {
		this.acceptedTotalCost = acceptedTotalCost;
	}

	public BigDecimal getCancelledTotalCost() {
		return cancelledTotalCost;
	}

	public void setCancelledTotalCost(BigDecimal cancelledTotalCost) {
		this.cancelledTotalCost = cancelledTotalCost;
	}

	public BigDecimal getReceivedTotalCost() {
		return receivedTotalCost;
	}

	public void setReceivedTotalCost(BigDecimal receivedTotalCost) {
		this.receivedTotalCost = receivedTotalCost;
	}

	@OneToMany(mappedBy = "order",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<VendorOrderItem> getItems() {
		return items;
	}

	public void setItems(List<VendorOrderItem> items) {
		this.items = items;
	}

	public VendorOrder() {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Transient
	public String getQtyFlag() {
		return qtyFlag;
	}

	public void setQtyFlag(String qtyFlag) {
		this.qtyFlag = qtyFlag;
	}
	
	
}
