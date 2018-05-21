package com.springrain.erp.modules.amazoninfo.entity.order;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.common.utils.IdGen;

@Entity
@Table(name = "amazoninfo_ebay_orderitem")
public class MfnOrderItem{

	private String id;
    private String title;
    private String sku;
    private Integer quantityPurchased;
    private Integer quantityShipped;
    private Float itemTax;
    private Float  itemPrice;
    private Float codFee;
    private String asin;
    private MfnOrder order;
    private String orderId;
    private String productName;
    private Date printTime;
    private String billNo;
    
    
    @Transient
    public Date getPrintTime() {
		return printTime;
	}
	public void setPrintTime(Date printTime) {
		this.printTime = printTime;
	}
	@Transient
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	@Transient
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
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
	
	
	@Transient
    public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
    public Integer getQuantityShipped() {
		return quantityShipped;
	}
	public void setQuantityShipped(Integer quantityShipped) {
		this.quantityShipped = quantityShipped;
	}
	public Float getItemTax() {
		return itemTax;
	}
	public void setItemTax(Float itemTax) {
		this.itemTax = itemTax;
	}
	public Float getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(Float itemPrice) {
		this.itemPrice = itemPrice;
	}
	public Float getCodFee() {
		return codFee;
	}
	public void setCodFee(Float codFee) {
		this.codFee = codFee;
	}
	@ManyToOne()
	@JoinColumn(name="order_id")
	@NotFound(action = NotFoundAction.IGNORE)
    public MfnOrder getOrder() {
		return order;
	}
	public void setOrder(MfnOrder order) {
		this.order = order;
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
	public Integer getQuantityPurchased() {
		return quantityPurchased;
	}
	public void setQuantityPurchased(Integer quantityPurchased) {
		this.quantityPurchased = quantityPurchased;
	}

	public String getAsin() {
		return asin;
	}
	public void setAsin(String asin) {
		this.asin = asin;
	}
    
   
}
