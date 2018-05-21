package com.springrain.erp.modules.amazoninfo.entity.order;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "amazoninfo_outbound_shipment")
public class AmazonOutboundShipment{

	private Integer id;
	
    private String sellersku;

    private String productName;
    
    private String color;
    
    private Integer quantity;
    
    private String asin;
    
    private String trackNumber;
    
    private String trackSupplier;
    
    private Integer shipmentId;
    
    private AmazonOutboundOrder order;
    
    private Date estimatedArrivalDate;
    
    
	public Date getEstimatedArrivalDate() {
		return estimatedArrivalDate;
	}

	public void setEstimatedArrivalDate(Date estimatedArrivalDate) {
		this.estimatedArrivalDate = estimatedArrivalDate;
	}

	public Integer getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(Integer shipmentId) {
		this.shipmentId = shipmentId;
	}

	public String getSellersku() {
		return sellersku;
	}

	public void setSellersku(String sellersku) {
		this.sellersku = sellersku;
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(String trackNumber) {
		this.trackNumber = trackNumber;
	}

	public String getTrackSupplier() {
		return trackSupplier;
	}

	public void setTrackSupplier(String trackSupplier) {
		this.trackSupplier = trackSupplier;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@OneToOne(fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonOutboundOrder getOrder() {
		return order;
	}

	public void setOrder(AmazonOutboundOrder order) {
		this.order = order;
	}
	
	@Transient
	public String  getName(){
		if(StringUtils.isNotBlank(color)){
			return productName+"_"+color;
		}
		return productName;
	}
	
}
