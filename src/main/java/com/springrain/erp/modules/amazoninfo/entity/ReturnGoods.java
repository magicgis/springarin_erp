/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springrain.erp.common.utils.excel.annotation.ExcelField;

/**
 * 退货信息Entity
 * @author Tim
 * @version 2014-12-29
 */
@Entity
@Table(name = "amazoninfo_return_goods")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ReturnGoods{
	private Integer id;
	private String sku;
	private String asin;
	private String fnsku;
	private String country;
	private Date returnDate;
	private String orderId;
	private Integer quantity;
	private String fulfillmentCenterId;
	private String reason;
	private String disposition;
	private String customerComment;
	
	private Date startDate;
	private String productName;
	
	private String prouctNameColor;
	private String accountName;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Transient
	@ExcelField(title="Email", align=2, sort=11)
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	@Transient
	@ExcelField(title="ProuctName", align=2, sort=0)
	public String getProuctNameColor() {
		return prouctNameColor;
	}
	public void setProuctNameColor(String prouctNameColor) {
		this.prouctNameColor = prouctNameColor;
	}
	
	@Transient
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@ExcelField(title="Sku", align=2, sort=3)
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	
	@ExcelField(title="Asin", align=2, sort=5)
	public String getAsin() {
		return asin;
	}
	public void setAsin(String asin) {
		this.asin = asin;
	}
	@ExcelField(title="Fnsku", align=2, sort=4)
	public String getFnsku() {
		return fnsku;
	}
	public void setFnsku(String fnsku) {
		this.fnsku = fnsku;
	}
	
	@ExcelField(title="country", align=2, sort=10)
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@ExcelField(title="RetrunDate", align=2, sort=1)
	@Column(updatable=false)
	public Date getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}
	
	@ExcelField(title="OrderId", align=2, sort=2)
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@ExcelField(title="Quantity", align=2, sort=6)
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	@ExcelField(title="FC", align=2, sort=7)
	public String getFulfillmentCenterId() {
		return fulfillmentCenterId;
	}
	public void setFulfillmentCenterId(String fulfillmentCenterId) {
		this.fulfillmentCenterId = fulfillmentCenterId;
	}
	
	@ExcelField(title="Reason", align=2, sort=9)
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@ExcelField(title="Disposition", align=2, sort=8)
	public String getDisposition() {
		return disposition;
	}
	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
	
	@ExcelField(title="CustomerComment", align=2, sort=12)
	public String getCustomerComment() {
		return customerComment;
	}
	public void setCustomerComment(String customerComment) {
		this.customerComment = customerComment;
	}
	public ReturnGoods() {}
	
	public ReturnGoods(String sku, String asin, String fnsku, String country,
			Date returnDate, String orderId, Integer quantity,
			String fulfillmentCenterId, String reason, String disposition,String customerComment) {
		super();
		this.sku = sku;
		this.asin = asin;
		this.fnsku = fnsku;
		this.country = country;
		this.returnDate = returnDate;
		this.orderId = orderId;
		this.quantity = quantity;
		this.fulfillmentCenterId = fulfillmentCenterId;
		this.reason = reason;
		this.disposition = disposition;
		this.customerComment = customerComment;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
}


