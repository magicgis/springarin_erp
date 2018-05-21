/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.parts;

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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.validator.constraints.Length;

import com.springrain.erp.common.persistence.DataEntity;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 配件出库Entity
 * @author Michael
 * @version 2015-07-16
 */
@Entity
@Table(name = "psi_parts_inventory_out_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiPartsInventoryOutItem implements Serializable{
	private static final long serialVersionUID = 1348226342160518909L;
	private 	  Integer			   id; 
	private       Integer 			   partsId;             // 配件id
	private       String         	   partsName;           // 配件名
	private       Integer        	   quantity;            // 数量
	private       Integer              stockQuantity;       // 库存锁定数量（非字段）
	private       Integer              mixtureRatio;        // 配比（非字段）
	private       Integer              maxCanQuantity;      // 最大可提数（非字段）
	 
	private       PsiPartsInventoryOut partsInventoryOut;

	public PsiPartsInventoryOutItem() {
		super();
	}

	public PsiPartsInventoryOutItem(Integer id){
		this();
		this.id = id;
	}
	
	public PsiPartsInventoryOutItem(Integer partsId, String partsName,Integer mixtureRatio,Integer stockQuantity,Integer maxCanQuantity) {
		super();
		this.partsId = partsId;
		this.partsName = partsName;
		this.stockQuantity = stockQuantity;
		this.mixtureRatio=mixtureRatio;
		this.maxCanQuantity=maxCanQuantity;
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@ManyToOne()
	@JoinColumn(name="parts_inventory_out_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiPartsInventoryOut getPartsInventoryOut() {
		return partsInventoryOut;
	}

	public void setPartsInventoryOut(PsiPartsInventoryOut partsInventoryOut) {
		this.partsInventoryOut = partsInventoryOut;
	}

	
	@Transient
	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	@Transient
	public Integer getMixtureRatio() {
		return mixtureRatio;
	}

	public void setMixtureRatio(Integer mixtureRatio) {
		this.mixtureRatio = mixtureRatio;
	}

	@Transient
	public Integer getMaxCanQuantity() {
		return maxCanQuantity;
	}

	public void setMaxCanQuantity(Integer maxCanQuantity) {
		this.maxCanQuantity = maxCanQuantity;
	}
	
}


