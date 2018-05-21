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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 配件收货详情Entity
 * @author Michael
 * @version 2015-07-03
 */
@Entity
@Table(name = "lc_psi_parts_delivery_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiPartsDeliveryItem implements Serializable{
	
	private static final long serialVersionUID = 2414853678210074538L;
	private 	Integer              id;
	private     Integer              partsId;
	private 	String 				 partsName;
	private 	Integer 			 quantityLading;
	private     Float                itemPrice;           //单价    
	private 	String     			 delFlag="0";
	private 	String     			 remark;
	private 	Integer              canLadingTotal;
	private 	Integer              oldQuantityLading;
	private 	LcPsiPartsDelivery	 partsDelivery;
	private 	LcPsiPartsOrderItem    partsOrderItem;      
	
	public LcPsiPartsDeliveryItem() {
		super();
	}

	public LcPsiPartsDeliveryItem(Integer id){
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
	public String getPartsName() {
		return partsName;
	}
	public void setPartsName(String partsName) {
		this.partsName = partsName;
	}
	public Integer getQuantityLading() {
		return quantityLading;
	}
	public void setQuantityLading(Integer quantityLading) {
		this.quantityLading = quantityLading;
	}
	
	@ManyToOne()
	@JoinColumn(name="parts_delivery_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPsiPartsDelivery getPartsDelivery() {
		return partsDelivery;
	}
	public void setPartsDelivery(LcPsiPartsDelivery partsDelivery) {
		this.partsDelivery = partsDelivery;
	}
	
	@ManyToOne()
	@JoinColumn(name="parts_order_item_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPsiPartsOrderItem getPartsOrderItem() {
		return partsOrderItem;
	}
	public void setPartsOrderItem(LcPsiPartsOrderItem partsOrderItem) {
		this.partsOrderItem = partsOrderItem;
	}
	
	@Transient
	public Integer getCanLadingTotal() {
		return canLadingTotal;
	}
	public void setCanLadingTotal(Integer canLadingTotal) {
		this.canLadingTotal = canLadingTotal;
	}
	
	@Transient
	public Integer getOldQuantityLading() {
		return oldQuantityLading;
	}
	public void setOldQuantityLading(Integer oldQuantityLading) {
		this.oldQuantityLading = oldQuantityLading;
	}
	public Float getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(Float itemPrice) {
		this.itemPrice = itemPrice;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getPartsId() {
		return partsId;
	}

	public void setPartsId(Integer partsId) {
		this.partsId = partsId;
	}
	
}


