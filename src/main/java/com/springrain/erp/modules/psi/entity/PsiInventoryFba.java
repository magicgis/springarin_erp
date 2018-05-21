package com.springrain.erp.modules.psi.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "psi_inventory_fba")
public class PsiInventoryFba {

	private Integer id;
	private String sku;
	private String asin;
	private String fnsku;
	private Integer fulfillableQuantity;
	private Integer unsellableQuantity;
	private Integer reservedQuantity;
	private Integer warehouseQuantity;
	private Integer transitQuantity;
	private Integer totalQuantity;
	private String country;
	private Date dataDate;
	private Date lastUpdateDate;
	private Integer orrectQuantity;
	private String accountName;
	
	@Id
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getAsin() {
		return asin;
	}
	public void setAsin(String asin) {
		this.asin = asin;
	}
	public String getFnsku() {
		return fnsku;
	}
	public void setFnsku(String fnsku) {
		this.fnsku = fnsku;
	}
	public Integer getFulfillableQuantity() {
		return fulfillableQuantity;
	}
	public void setFulfillableQuantity(Integer fulfillableQuantity) {
		this.fulfillableQuantity = fulfillableQuantity;
	}
	public Integer getUnsellableQuantity() {
		return unsellableQuantity;
	}
	public void setUnsellableQuantity(Integer unsellableQuantity) {
		this.unsellableQuantity = unsellableQuantity;
	}
	public Integer getReservedQuantity() {
		return reservedQuantity;
	}
	public void setReservedQuantity(Integer reservedQuantity) {
		this.reservedQuantity = reservedQuantity;
	}
	public Integer getWarehouseQuantity() {
		return warehouseQuantity;
	}
	public void setWarehouseQuantity(Integer warehouseQuantity) {
		this.warehouseQuantity = warehouseQuantity;
	}
	public Integer getTransitQuantity() {
		return transitQuantity;
	}
	public void setTransitQuantity(Integer transitQuantity) {
		this.transitQuantity = transitQuantity;
	}
	public Integer getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	public Date getDataDate() {
		return dataDate;
	}
	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
	public Integer getOrrectQuantity() {
		return orrectQuantity;
	}
	public void setOrrectQuantity(Integer orrectQuantity) {
		this.orrectQuantity = orrectQuantity;
	}
	public PsiInventoryFba() {}
	
	public PsiInventoryFba(String sku, String asin, String fnsku,
			Integer fulfillableQuantity, Integer unsellableQuantity,
			Integer reservedQuantity, Integer warehouseQuantity,
			Integer transitQuantity, Integer totalQuantity, String country,Date dataDate,Date lastUpdateDate,Integer orrectQuantity,String accountName) {
		super();
		this.sku = sku;
		this.asin = asin;
		this.fnsku = fnsku;
		this.fulfillableQuantity = fulfillableQuantity;
		this.unsellableQuantity = unsellableQuantity;
		this.reservedQuantity = reservedQuantity;
		this.warehouseQuantity = warehouseQuantity;
		this.transitQuantity = transitQuantity;
		this.totalQuantity = totalQuantity;
		this.country = country;
		this.dataDate = dataDate;
		this.lastUpdateDate = lastUpdateDate;
		this.orrectQuantity = orrectQuantity;
		this.accountName=accountName;
	}
	
	public PsiInventoryFba(String sku, String asin, String fnsku,
			Integer fulfillableQuantity, Integer unsellableQuantity,
			Integer reservedQuantity, Integer warehouseQuantity,
			Integer transitQuantity, Integer totalQuantity, String country,Date dataDate,Date lastUpdateDate,Integer orrectQuantity) {
		super();
		this.sku = sku;
		this.asin = asin;
		this.fnsku = fnsku;
		this.fulfillableQuantity = fulfillableQuantity;
		this.unsellableQuantity = unsellableQuantity;
		this.reservedQuantity = reservedQuantity;
		this.warehouseQuantity = warehouseQuantity;
		this.transitQuantity = transitQuantity;
		this.totalQuantity = totalQuantity;
		this.country = country;
		this.dataDate = dataDate;
		this.lastUpdateDate = lastUpdateDate;
		this.orrectQuantity = orrectQuantity;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PsiInventoryFba other = (PsiInventoryFba) obj;
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		return true;
	}
	
	@Transient
	public int getTotal(){
		fulfillableQuantity = (fulfillableQuantity==null?0:fulfillableQuantity);
		reservedQuantity = (reservedQuantity==null?0:reservedQuantity);
		transitQuantity = (transitQuantity==null?0:transitQuantity);
		if(orrectQuantity==null){
			return fulfillableQuantity+transitQuantity;
		}else{
			return fulfillableQuantity+orrectQuantity+transitQuantity;
		}
	}
	
	
	@Transient
	public int getRealTotal(){
		fulfillableQuantity = (fulfillableQuantity==null?0:fulfillableQuantity);
		if(orrectQuantity==null){
			return fulfillableQuantity;
		}else{
			return fulfillableQuantity+orrectQuantity;
		}
	}
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
}
