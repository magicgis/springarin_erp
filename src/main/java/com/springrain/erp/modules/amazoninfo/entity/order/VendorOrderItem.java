package com.springrain.erp.modules.amazoninfo.entity.order;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "amazoninfo_vendor_orderitem")
public class VendorOrderItem{

	private Integer id ;
	
    private String asin;

    private String sku;
    
    private String skuInVendor;
    
    private String productName;
    
    private String title;

    private Date expectedDeliveryDate;
    
    
    private Integer submittedQuantity; //订单数量

    private Integer acceptedQuantity;//接受的数量

    private Integer receivedQuantity; //收到数量

    private Integer outstandingQuantity;//已配送的数量

    private BigDecimal itemPrice;
    
    private BigDecimal unitPrice;
    
    private Integer stockQty;
    
    
    private VendorOrder order;
    
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


	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getSkuInVendor() {
		return skuInVendor;
	}

	public void setSkuInVendor(String skuInVendor) {
		this.skuInVendor = skuInVendor;
	}

	public Date getExpectedDeliveryDate() {
		return expectedDeliveryDate;
	}

	public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
		this.expectedDeliveryDate = expectedDeliveryDate;
	}

	public Integer getSubmittedQuantity() {
		return submittedQuantity;
	}

	public void setSubmittedQuantity(Integer submittedQuantity) {
		this.submittedQuantity = submittedQuantity;
	}

	public Integer getAcceptedQuantity() {
		return acceptedQuantity;
	}

	public void setAcceptedQuantity(Integer acceptedQuantity) {
		this.acceptedQuantity = acceptedQuantity;
	}

	public Integer getReceivedQuantity() {
		return receivedQuantity;
	}

	public void setReceivedQuantity(Integer receivedQuantity) {
		this.receivedQuantity = receivedQuantity;
	}

	public Integer getOutstandingQuantity() {
		return outstandingQuantity;
	}

	public void setOutstandingQuantity(Integer outstandingQuantity) {
		this.outstandingQuantity = outstandingQuantity;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	@ManyToOne()
	@JoinColumn(name="order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public VendorOrder getOrder() {
		return order;
	}

	public void setOrder(VendorOrder order) {
		this.order = order;
	}
	
	public VendorOrderItem(){
		
	}

	@Transient
	public Integer getStockQty() {
		return stockQty;
	}

	public void setStockQty(Integer stockQty) {
		this.stockQty = stockQty;
	}
	
	
}
