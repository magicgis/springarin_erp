package com.springrain.erp.modules.psi.entity;

import java.util.Date;

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

/**
 * 产品库存库位明细Entity
 */
@Entity
@Table(name = "psi_inventory_location")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiInventoryLocation  {
	
	private    Integer 			id; 		      
	private    String           sku;                    //sku  
	private    Integer  		productId;              //产品id
	private    String   		productName;            //产品名
	private	   String   		colorCode;				//颜色
	private    String   		countryCode;            //国家
	private    String   		snCode;            	    //批次号
	private    Integer  		newQuantity=0;          //新品数量
	private    Integer  		oldQuantity=0;          //外包转损坏数量
	private    Integer  		brokenQuantity=0;       //残次品数量
	private    Integer  		renewQuantity=0;        //翻新品数量
	private    StockLocation    stockLocation;          //仓库库位
	private    Date             createDate;             //入库日期
	private    Date             updateDate;             //最近更新日期
	private    Integer  		sparesQuantity=0;       //备品数量
	private    Integer  		offlineQuantity=0;      //线下数量
	private    Integer  		fbaLockQuantity=0;      //FBA锁定数量
	private    Integer  		offlineLockQuantity=0;  //线下锁定数量
	private    String           remark;
	
	public PsiInventoryLocation() {
		super();
	}

	public PsiInventoryLocation(Integer id){
		this();
		this.id = id;
	}
	
	public PsiInventoryLocation(Integer newQuantity, String sku) {
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
	@JoinColumn(name="location_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public StockLocation getStockLocation() {
		return stockLocation;
	}

	public void setStockLocation(StockLocation stockLocation) {
		this.stockLocation = stockLocation;
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

	public String getSnCode() {
		return snCode;
	}

	public void setSnCode(String snCode) {
		this.snCode = snCode;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getFbaLockQuantity() {
		return fbaLockQuantity;
	}

	public void setFbaLockQuantity(Integer fbaLockQuantity) {
		this.fbaLockQuantity = fbaLockQuantity;
	}

	public Integer getOfflineLockQuantity() {
		return offlineLockQuantity;
	}

	public void setOfflineLockQuantity(Integer offlineLockQuantity) {
		this.offlineLockQuantity = offlineLockQuantity;
	}
	
	
}


