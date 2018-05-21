package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "amazoninfo_mfn_item")
public class MfnItem implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer id; // 编号
	
	private String sku;
	
	private Integer  quantity;
	
	private MfnInventoryFeed mfnInventory;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="mfn_inventory_id")
	public MfnInventoryFeed getMfnInventory() {
		return mfnInventory;
	}

	public void setMfnInventory(MfnInventoryFeed mfnInventory) {
		this.mfnInventory = mfnInventory;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
}
