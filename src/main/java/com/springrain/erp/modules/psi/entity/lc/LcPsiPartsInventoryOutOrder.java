/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.io.Serializable;

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

/**
 * 配件出库Entity
 * @author Michael
 * @version 2015-07-16
 */
@Entity
@Table(name = "lc_psi_parts_inventory_out_order")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiPartsInventoryOutOrder implements Serializable{
	
	private static final long serialVersionUID = -3563889016553027657L;
	private 		Integer		 id; 			  	// id
	private    		Integer      purchaseOrderId; 	// 采购订单id
	private     	String       purchaseOrderNo; 	// 采购订单No
	private     	Integer      quantity;        	// 数量 
	
	private LcPsiPartsInventoryOut partsInventoryOut;

	public LcPsiPartsInventoryOutOrder() {
		super();
	}

	public LcPsiPartsInventoryOutOrder(Integer id){
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@ManyToOne()
	@JoinColumn(name="parts_inventory_out_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPsiPartsInventoryOut getPartsInventoryOut() {
		return partsInventoryOut;
	}

	public void setPartsInventoryOut(LcPsiPartsInventoryOut partsInventoryOut) {
		this.partsInventoryOut = partsInventoryOut;
	}
	
	
	public Integer getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Integer purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	

}


