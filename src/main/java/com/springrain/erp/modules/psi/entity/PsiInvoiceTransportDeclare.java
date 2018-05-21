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

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "psi_transport_declare")
public class PsiInvoiceTransportDeclare {  
	
	private	    Integer      		id; 		  	 // id
	private     Date                declareDate;
	private     String              declareNo;
	private     String              declareNum;
	private     String              declareCode;
	private     String              transportNo;
	private     String              productNo;
	private     String              productName;
	private     String              productModel;
	private     Integer             quantity;
	private     Float               price;
	private     String              delFlag;         // 删除标记
	private     User         		createUser;      // 创建人
	private     Date         		createDate;      // 创建时间
	private     Date         		arrangeDate;      // 创建时间
	private     PsiSupplierInvoice   invoice;
	private     User         		arrangeUser;
	private     String              state;//1：不能编辑
	private     String              legalUnit;
	private     float             legalQuantity;
	private     String              unit;
	
	private     Float               totalPrice;//成交总价
	private     Float               usdPrice;
	private     Float               usdRate;
	private     Float               cnyPrice;
	private     Float               taxRate;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@ManyToOne()
	@JoinColumn(name="arrange_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getArrangeUser() {
		return arrangeUser;
	}

	public void setArrangeUser(User arrangeUser) {
		this.arrangeUser = arrangeUser;
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
	

	@ManyToOne()
	@JoinColumn(name="invoice_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplierInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(PsiSupplierInvoice invoice) {
		this.invoice = invoice;
	}

	public String getDelFlag() {
		return delFlag;
	}

	

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public Date getDeclareDate() {
		return declareDate;
	}

	public void setDeclareDate(Date declareDate) {
		this.declareDate = declareDate;
	}

	public String getDeclareNo() {
		return declareNo;
	}

	public void setDeclareNo(String declareNo) {
		this.declareNo = declareNo;
	}

	

	public String getDeclareNum() {
		return declareNum;
	}

	public void setDeclareNum(String declareNum) {
		this.declareNum = declareNum;
	}

	public String getDeclareCode() {
		return declareCode;
	}

	public void setDeclareCode(String declareCode) {
		this.declareCode = declareCode;
	}

	public String getTransportNo() {
		return transportNo;
	}

	public void setTransportNo(String transportNo) {
		this.transportNo = transportNo;
	}

	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductModel() {
		return productModel;
	}

	public void setProductModel(String productModel) {
		this.productModel = productModel;
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

	public Date getArrangeDate() {
		return arrangeDate;
	}

	public void setArrangeDate(Date arrangeDate) {
		this.arrangeDate = arrangeDate;
	}

	

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public PsiInvoiceTransportDeclare(Date declareDate, String declareNo,
			String declareNum, String declareCode, String transportNo,
			String productNo, String productName, String productModel,
			Integer quantity, Float price, String delFlag, User createUser,Date createDate) {
		super();
		this.declareDate = declareDate;
		this.declareNo = declareNo;
		this.declareNum = declareNum;
		this.declareCode = declareCode;
		this.transportNo = transportNo;
		this.productNo = productNo;
		this.productName = productName;
		this.productModel = productModel;
		this.quantity = quantity;
		this.price = price;
		this.delFlag = delFlag;
		this.createUser = createUser;
		this.createDate=createDate;
	}
	
	
	
	

	public PsiInvoiceTransportDeclare(Date declareDate, String declareNo,
			String declareNum, String declareCode, String transportNo,
			String productNo, String productName, String productModel,
			Integer quantity, Float price, String delFlag, User createUser,
			Date createDate,String legalUnit,
			float legalQuantity, String unit, Float totalPrice,
			Float usdPrice, Float usdRate, Float cnyPrice,Float taxRate) {
		super();
		this.declareDate = declareDate;
		this.declareNo = declareNo;
		this.declareNum = declareNum;
		this.declareCode = declareCode;
		this.transportNo = transportNo;
		this.productNo = productNo;
		this.productName = productName;
		this.productModel = productModel;
		this.quantity = quantity;
		this.price = price;
		this.delFlag = delFlag;
		this.createUser = createUser;
		this.createDate = createDate;
		this.legalUnit = legalUnit;
		this.legalQuantity = legalQuantity;
		this.unit = unit;
		this.totalPrice = totalPrice;
		this.usdPrice = usdPrice;
		this.usdRate = usdRate;
		this.cnyPrice = cnyPrice;
		this.taxRate=taxRate;
	}

	public PsiInvoiceTransportDeclare() {
		super();
	}

	public String getLegalUnit() {
		return legalUnit;
	}

	public void setLegalUnit(String legalUnit) {
		this.legalUnit = legalUnit;
	}

	
	
	public float getLegalQuantity() {
		return legalQuantity;
	}

	public void setLegalQuantity(float legalQuantity) {
		this.legalQuantity = legalQuantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Float getUsdPrice() {
		return usdPrice;
	}

	public void setUsdPrice(Float usdPrice) {
		this.usdPrice = usdPrice;
	}

	public Float getUsdRate() {
		return usdRate;
	}

	public void setUsdRate(Float usdRate) {
		this.usdRate = usdRate;
	}

	public Float getCnyPrice() {
		return cnyPrice;
	}

	public void setCnyPrice(Float cnyPrice) {
		this.cnyPrice = cnyPrice;
	}

	public Float getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Float taxRate) {
		this.taxRate = taxRate;
	}
	
	
	

}

