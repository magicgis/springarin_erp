package com.springrain.erp.modules.amazoninfo.entity.order;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "amazoninfo_vendor_returns_item")
public class VendorReturnsItem implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String requestItemId;
	
	private String asin;
	
	private String ean;
	
	private String productName;
	
	private Integer requestedQuantity;
	
	private Float requestedRefund;
	
    private Integer approvedQuantity;
	
	private Float approvedRefund;
	
	private Float totalCost;
	
	private VendorReturns vendorReturns;
	
	
	@ManyToOne()
	@JoinColumn(name="return_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public VendorReturns getVendorReturns() {
		return vendorReturns;
	}

	public void setVendorReturns(VendorReturns vendorReturns) {
		this.vendorReturns = vendorReturns;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRequestItemId() {
		return requestItemId;
	}

	public void setRequestItemId(String requestItemId) {
		this.requestItemId = requestItemId;
	}


	
	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getRequestedQuantity() {
		return requestedQuantity;
	}

	public void setRequestedQuantity(Integer requestedQuantity) {
		this.requestedQuantity = requestedQuantity;
	}

	public Float getRequestedRefund() {
		return requestedRefund;
	}

	public void setRequestedRefund(Float requestedRefund) {
		this.requestedRefund = requestedRefund;
	}

	public Integer getApprovedQuantity() {
		return approvedQuantity;
	}

	public void setApprovedQuantity(Integer approvedQuantity) {
		this.approvedQuantity = approvedQuantity;
	}

	public Float getApprovedRefund() {
		return approvedRefund;
	}

	public void setApprovedRefund(Float approvedRefund) {
		this.approvedRefund = approvedRefund;
	}

	public Float getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Float totalCost) {
		this.totalCost = totalCost;
	}

	
}
