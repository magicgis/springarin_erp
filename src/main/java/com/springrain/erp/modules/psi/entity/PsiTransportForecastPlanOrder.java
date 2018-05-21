/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
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
@Table(name = "psi_forecast_transport_plan_order")
public class PsiTransportForecastPlanOrder  implements Serializable{
	private static final long serialVersionUID = 1L;
	private 	Integer           id; 		                     // id
	private     String            productName;
	private     String            countryCode;
	private     String            sku;
	private     String            model;
	private     String            transportType;
	private     Integer            quantity;
	private     Integer            boxNum;
	private     String             remark;
	private     String             delFlag;
	private     String             type;
	private     String             state;
	private     String             otherDesc;
	private     Date               updateDate;
	private     PsiTransportForecastAnalyseOrder  order;
	private     PsiSupplier   supplier;
	private     PsiProduct  product;
	
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


	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getBoxNum() {
		return boxNum;
	}

	public void setBoxNum(Integer boxNum) {
		this.boxNum = boxNum;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getOtherDesc() {
		return otherDesc;
	}

	public void setOtherDesc(String otherDesc) {
		this.otherDesc = otherDesc;
	}

	
	@ManyToOne()
	@JoinColumn(name="order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiTransportForecastAnalyseOrder getOrder() {
		return order;
	}

	public void setOrder(PsiTransportForecastAnalyseOrder order) {
		this.order = order;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	@Transient
	public String getTransportTypeName(){
		if("0".equals(transportType)){
			return "本地运输";
		}else{
			return "FBA运输";
		}
	}
	
	@Transient
	public String getModelName(){
		if("0".equals(model)){
			return "空运";
		}else if("1".equals(model)){
			return "海运";
		}else if("3".equals(model)){
			return "铁路";
		}else{
			return "快递";
		}
	}

	@Transient
	public PsiSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(PsiSupplier supplier) {
		this.supplier = supplier;
	}

	
	@Transient
	public PsiProduct getProduct() {
		return product;
	}

	public void setProduct(PsiProduct product) {
		this.product = product;
	}

	
}


