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
 * 配件库存盘点Entity
 * @author Michael
 * @version 2015-07-31
 */
@Entity
@Table(name = "lc_psi_parts_inventory_taking_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiPartsInventoryTakingItem implements Serializable{
	
	private static final long serialVersionUID = 3515318312896469370L;
	private 		Integer 		id; 			// id
	private     	Integer         partsId;    	// 配件id
	private     	String          partsName;  	// 配件名称
	private     	Integer         stockType;  	// 库存类型
	private     	Integer         poFrozen;   	// 数量
	private     	Integer         poNotFrozen;   	// 数量
	private     	Integer         stockFrozen;   	// 数量
	private     	Integer         stockNotFrozen;	// 数量
	private     	String          remark;     	// 备注
	private   LcPsiPartsInventoryTaking partsTaking;  //主表

	public LcPsiPartsInventoryTakingItem() {
		super();
	}

	public LcPsiPartsInventoryTakingItem(Integer id){
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

	public Integer getPartsId() {
		return partsId;
	}

	public void setPartsId(Integer partsId) {
		this.partsId = partsId;
	}

	public String getPartsName() {
		return partsName;
	}

	public void setPartsName(String partsName) {
		this.partsName = partsName;
	}

	public Integer getStockType() {
		return stockType;
	}

	public void setStockType(Integer stockType) {
		this.stockType = stockType;
	}

	

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@ManyToOne()
	@JoinColumn(name="taking_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPsiPartsInventoryTaking getPartsTaking() {
		return partsTaking;
	}

	public void setPartsTaking(LcPsiPartsInventoryTaking partsTaking) {
		this.partsTaking = partsTaking;
	}

	public Integer getPoFrozen() {
		return poFrozen;
	}

	public void setPoFrozen(Integer poFrozen) {
		this.poFrozen = poFrozen;
	}

	public Integer getPoNotFrozen() {
		return poNotFrozen;
	}

	public void setPoNotFrozen(Integer poNotFrozen) {
		this.poNotFrozen = poNotFrozen;
	}

	public Integer getStockFrozen() {
		return stockFrozen;
	}

	public void setStockFrozen(Integer stockFrozen) {
		this.stockFrozen = stockFrozen;
	}

	public Integer getStockNotFrozen() {
		return stockNotFrozen;
	}

	public void setStockNotFrozen(Integer stockNotFrozen) {
		this.stockNotFrozen = stockNotFrozen;
	}
	

}


