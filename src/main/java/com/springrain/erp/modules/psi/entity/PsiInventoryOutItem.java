/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 出库明细管理Entity
 * @author Michael
 * @version 2015-01-05
 */
@Entity
@Table(name = "psi_inventory_out_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiInventoryOutItem implements Serializable{
	
	private static final long serialVersionUID = 3915363942416331874L;
	private   Integer  id; 		
	private   Integer  productId;
	private   String   productName;
	private   String   colorCode;
	private   String   countryCode;
	private   Integer  quantity;
	private   String   qualityType="new";
	private   String   remark;
	private   PsiInventoryOut inventoryOut;  
	private   Integer  inventoryId;    
	private   String   sku;
	private   Integer  timelyQuantity;
	private   Float    avgPrice;        //出库时当天的均价

	
	public PsiInventoryOutItem() {
		super();
	}

	public PsiInventoryOutItem(Integer id){
		this();
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

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	@ManyToOne()
	@JoinColumn(name="inventory_out_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiInventoryOut getInventoryOut() {
		return inventoryOut;
	}

	public void setInventoryOut(PsiInventoryOut inventoryOut) {
		this.inventoryOut = inventoryOut;
	}

	@Transient
	public Integer getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Integer inventoryId) {
		this.inventoryId = inventoryId;
	}

	public String getProductName() {
		return productName;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getQualityType() {
		return qualityType;
	}

	public void setQualityType(String qualityType) {
		this.qualityType = qualityType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getTimelyQuantity() {
		return timelyQuantity;
	}

	public void setTimelyQuantity(Integer timelyQuantity) {
		this.timelyQuantity = timelyQuantity;
	}
	
	@Transient
	public String getProductNameColor(){
		String productName =this.productName ;
		if(StringUtils.isNotEmpty(this.colorCode)){
			productName+=productName+"_"+this.colorCode;
		}
		return productName;
	}

	public Float getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(Float avgPrice) {
		this.avgPrice = avgPrice;
	}

	
}


