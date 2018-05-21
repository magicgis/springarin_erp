/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 采购付款明细Entity
 * @author Michael
 * @version 2014-12-24
 */
@Entity
@Table(name = "psi_inventory")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiInventory  {
	
	private    Integer 			id; 		        
	private    Integer  		productId;              //产品id
	private    String   		productName;            //产品名
	private	   String   		colorCode;				//颜色
	private    String   		countryCode;            //国家
	private    Integer  		newQuantity;            //新品数量
	private    Integer  		oldQuantity;            //外包转损坏数量
	private    Integer  		brokenQuantity;         //残次品数量
	private    Integer  		renewQuantity;          //翻新品数量
	private    Stock      		warehouse;              //仓库
	private    String   		warehouseName;          //仓库名字
	private    Date             updateDate;             //最近更新日期
	private    String           sku;                    //sku
	private    List<PsiInventory> inventoryList ;       //list页面统计用到
	private    List<PsiInventoryRevisionLog> changeItems; //内部调控项  
	private    Integer  		sparesQuantity;          //备品数量
	private    Integer  		offlineQuantity;         //线下数量
	private    Float            avgPrice;
	private    Float            volume;					 //(非数据库字段)
	private    Integer  		usableQuantity;          //可用数量(非数据库字段)
	private    String 			asin;                    //(非数据库字段)
	private    String  		    active;                  //(非数据库字段)
	
	private    String           remark;
	
	@Transient
	public Float getVolume() {
		return volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}
	
	public Float getAvgPrice() {
		return avgPrice;
	}
	
	public void setAvgPrice(Float avgPrice) {
		this.avgPrice = avgPrice;
	}

	public PsiInventory() {
		super();
	}

	public PsiInventory(Integer id){
		this();
		this.id = id;
	}
	
	public PsiInventory(Integer newQuantity, String sku) {
		super();
		this.newQuantity = newQuantity;
		this.sku = sku;
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
	
	public Integer getSparesQuantity() {
		return sparesQuantity;
	}

	public void setSparesQuantity(Integer sparesQuantity) {
		this.sparesQuantity = sparesQuantity;
	}

	public String getSku(){
		return this.sku;
	}
	public void setSku(String sku){
		this.sku=sku;
	}
	
	@Transient
	public String getProductNameColor(){
		String proNameColor =this.productName;
		if(StringUtils.isNotEmpty(this.colorCode)){
			proNameColor+="_"+this.colorCode;
		}
		return proNameColor;
	}
	
	@Transient
	public Integer getUsableQuantity() {
		return usableQuantity;
	}

	public void setUsableQuantity(Integer usableQuantity) {
		this.usableQuantity = usableQuantity;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getColorCode() {
		return colorCode;
	}

	@Transient
	@JsonIgnore
	public List<PsiInventory> getInventoryList() {
		return inventoryList;
	}

	public void setInventoryList(List<PsiInventory> inventoryList) {
		this.inventoryList = inventoryList;
	}

	@Transient
	@JsonIgnore
	public List<PsiInventoryRevisionLog> getChangeItems() {
		return changeItems;
	}

	public void setChangeItems(List<PsiInventoryRevisionLog> changeItems) {
		this.changeItems = changeItems;
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

	public Integer getNewQuantity() {
		return newQuantity;
	}

	public void setNewQuantity(Integer newQuantity) {
		this.newQuantity = newQuantity;
	}

	public Integer getOldQuantity() {
		return oldQuantity;
	}

	public void setOldQuantity(Integer oldQuantity) {
		this.oldQuantity = oldQuantity;
	}

	public Integer getBrokenQuantity() {
		return brokenQuantity;
	}

	public void setBrokenQuantity(Integer brokenQuantity) {
		this.brokenQuantity = brokenQuantity;
	}

	public Integer getRenewQuantity() {
		return renewQuantity;
	}

	public void setRenewQuantity(Integer renewQuantity) {
		this.renewQuantity = renewQuantity;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="warehouse_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public Stock getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Stock warehouse) {
		this.warehouse = warehouse;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	
	public PsiInventory(Integer productId,String productName,Integer newQuantity,Integer oldQuantity,Integer brokenQuantity,Integer renewQuantity,Stock house,String countryCode,String colorCode,Integer sparesQuantity,Integer offlineQuantity){
		this.productId=productId;
		this.productName=productName;
		this.newQuantity = newQuantity;
		this.oldQuantity = oldQuantity;
		this.brokenQuantity = brokenQuantity;
		this.renewQuantity  = renewQuantity;
		this.warehouse=house ;
		this.countryCode=countryCode;
		this.colorCode=colorCode;
		this.sparesQuantity=sparesQuantity;
		this.offlineQuantity=offlineQuantity;
	}
	@Transient  
	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}
	
	@Transient  
	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	@Transient
	public Integer getTotalQuantity(){
		return this.newQuantity+this.oldQuantity+this.brokenQuantity+this.renewQuantity+this.sparesQuantity+this.offlineQuantity;
	}

	public Integer getOfflineQuantity() {
		return offlineQuantity;
	}

	public void setOfflineQuantity(Integer offlineQuantity) {
		this.offlineQuantity = offlineQuantity;
	}
	
	@Transient
	public String getProductColorCountry(){
		if(StringUtils.isEmpty(colorCode)){
			return productName+"_"+countryCode;
		}else{
			return productName+"_"+colorCode+"_"+countryCode;
		}
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}


