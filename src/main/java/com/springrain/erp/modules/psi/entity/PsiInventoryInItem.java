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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.common.utils.StringUtils;

/**
 * 入库明细管理Entity
 * @author Michael
 * @version 2015-01-05
 */
@Entity
@Table(name = "psi_inventory_in_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiInventoryInItem implements Serializable{
	private static final long serialVersionUID = 3851064631425860000L;
	private   Integer  id; 		
	private   Integer  productId;
	private   String   productName;
	private   String   colorCode;
	private   String   countryCode;
	private   Integer  quantity;
	private   String   qualityType;
	private   String   remark;
	private   String    sku;
	private   PsiInventoryIn inventoryIn;
	private   Integer  timelyQuantity;

	private   String   tranQuantity;
	private   Integer  billItemId;   //提单itemId
	private   Float    price;
	
	private   Integer  packQuantity; //装箱数
	private   Integer  boxNumber;    //箱数
	private   Float    gw;           //毛重
	private   Float    boxVolume;    //大箱体积
	
	public PsiInventoryInItem() {
		super();
	}

	public PsiInventoryInItem(Integer id){
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

	@Transient
	public String getTranQuantity() {
		return tranQuantity;
	}

	public void setTranQuantity(String tranQuantity) {
		this.tranQuantity = tranQuantity;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	@Transient
	public String getProductNameColor(){
		String name=productName;
		if(StringUtils.isNotEmpty(colorCode)){
			name=name+"_"+colorCode;
		}
		return name;
	}

	@ManyToOne()
	@JoinColumn(name="inventory_in_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiInventoryIn getInventoryIn() {
		return inventoryIn;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public void setInventoryIn(PsiInventoryIn inventoryIn) {
		this.inventoryIn = inventoryIn;
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
	
	public Integer getBillItemId() {
		return billItemId;
	}

	public void setBillItemId(Integer billItemId) {
		this.billItemId = billItemId;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}
	
	@Transient
	public Integer getPackQuantity() {
		return packQuantity;
	}

	public void setPackQuantity(Integer packQuantity) {
		this.packQuantity = packQuantity;
	}
	
	@Transient
	public Integer getBoxNumber() {
		return boxNumber;
	}

	public void setBoxNumber(Integer boxNumber) {
		this.boxNumber = boxNumber;
	}
	
	
	@Transient
	public Float getGw() {
		return gw;
	}

	public void setGw(Float gw) {
		this.gw = gw;
	}

	@Transient
	public Float getBoxVolume() {
		return boxVolume;
	}

	public void setBoxVolume(Float boxVolume) {
		this.boxVolume = boxVolume;
	}
	
	@Transient
	public boolean getIsNew(){
		boolean flag=false;
		String conSku =this.productName;
		if(StringUtils.isNotEmpty(this.colorCode)){
			conSku=this.productName+"_"+this.colorCode+"_"+this.countryCode;
		}else{
			conSku=this.productName+"_"+this.countryCode;
		}
		if(this.sku.equals(conSku)){
			flag=true;
		}
		return flag;
	}

	public PsiInventoryInItem(Integer productId, String productName,String colorCode, String countryCode, Integer quantity,String qualityType, String sku,
			PsiInventoryIn inventoryIn,Integer timelyQuantity,Integer billItemId,Float price) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.colorCode = colorCode;
		this.countryCode = countryCode;
		this.quantity = quantity;
		this.qualityType = qualityType;
		this.sku = sku;
		this.inventoryIn = inventoryIn;
		this.timelyQuantity = timelyQuantity;
		this.billItemId=billItemId;
		this.price=price;
	}

	
	public PsiInventoryInItem(Integer productId, String productName,String colorCode, String countryCode, Integer quantity,String qualityType, String sku,
			PsiInventoryIn inventoryIn,Integer timelyQuantity,Integer billItemId,Float price,Integer packQuantity,Integer boxNumber,Float gw,Float boxVolume) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.colorCode = colorCode;
		this.countryCode = countryCode;
		this.quantity = quantity;
		this.qualityType = qualityType;
		this.sku = sku;
		this.inventoryIn = inventoryIn;
		this.timelyQuantity = timelyQuantity;
		this.billItemId=billItemId;
		this.price=price;
		this.packQuantity=packQuantity;
		this.boxNumber=boxNumber;
		this.gw=gw;
		this.boxVolume=boxVolume;
	}
	
	
	
}


