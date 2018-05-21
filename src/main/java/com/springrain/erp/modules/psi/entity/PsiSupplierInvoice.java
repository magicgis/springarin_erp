/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "psi_supplier_invoice")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiSupplierInvoice {  
	
	private	    Integer      		id; 		  	 // id
	private     Date         		invoiceDate;     // 开票日期
	private     String              invoiceCode;
	private     String              invoiceNo;
	private     String              companyName;
	private     String              taxpayerNo;
	private     String              productName;
	private     String              model;
	private     String              unit;
	private     Integer             quantity;
	private     Float               price;
	private     Float               totalPrice;
	private     Float               rate;
	private     String              state;//0:未认证 1:已认证
	private     String              delFlag;
	private     Date         		useDate;    
	private     Date         		returnDate;   
	private     Integer             remainingQuantity; //剩余数量
	private     User         		createUser;      // 创建人
	private     Date         		createDate;      // 创建时间
	private     Integer             useQuantity;
	private     Float               taxRate;       
	
	public PsiSupplierInvoice() {
		super();
	}
	
	public PsiSupplierInvoice(Integer id) {
		super();
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	
	public Integer getRemainingQuantity() {
		return remainingQuantity;
	}

	public void setRemainingQuantity(Integer remainingQuantity) {
		this.remainingQuantity = remainingQuantity;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getTaxpayerNo() {
		return taxpayerNo;
	}

	public void setTaxpayerNo(String taxpayerNo) {
		this.taxpayerNo = taxpayerNo;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public Date getUseDate() {
		return useDate;
	}

	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}
	
	@ManyToOne()
	@JoinColumn(name="create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	
	public Integer getUseQuantity() {
		return useQuantity;
	}

	public void setUseQuantity(Integer useQuantity) {
		this.useQuantity = useQuantity;
	}

	public PsiSupplierInvoice(Date invoiceDate, String invoiceCode,
			String invoiceNo, String companyName, String taxpayerNo,
			String productName, String model, String unit, Integer quantity,
			Float price, Float totalPrice, Float rate, String state,
			Integer remainingQuantity, Float taxRate) {
		super();
		this.invoiceDate = invoiceDate;
		this.invoiceCode = invoiceCode;
		this.invoiceNo = invoiceNo;
		this.companyName = companyName;
		this.taxpayerNo = taxpayerNo;
		this.productName = productName;
		this.model = model;
		this.unit = unit;
		this.quantity = quantity;
		this.price = price;
		this.totalPrice = totalPrice;
		this.rate = rate;
		this.state = state;
		this.remainingQuantity = remainingQuantity;
		this.taxRate=taxRate;
	}

	public Float getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Float taxRate) {
		this.taxRate = taxRate;
	}

}


