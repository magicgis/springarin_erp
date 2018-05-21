/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 配件订单付款详情Entity
 * @author Michael
 * @version 2015-06-29
 */
@Entity
@Table(name = "lc_psi_parts_inventory")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiPartsInventory{
	
	private 	Integer    id;  
	private 	Integer    partsId; 			// 配件id
	private 	String     partsName; 			// 配件名称
	private     Integer    poFrozen;       		// po冻结
	private     Integer    poNotFrozen;   		// po非冻结
	private     Integer    stockFrozen;    		// stock冻结
	private     Integer    stockNotFrozen;		// stock非冻结
	private     String     operateType;         // 操作类型 (非数据库字段)
	
	
	private     List<LcPsiPartsInventoryLog> partsLogs ; // 日志list (非数据库字段)

	public LcPsiPartsInventory() {
		super();
	}

	public LcPsiPartsInventory(Integer id){
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

	@Transient
	public List<LcPsiPartsInventoryLog> getPartsLogs() {
		return partsLogs;
	}

	public void setPartsLogs(List<LcPsiPartsInventoryLog> partsLogs) {
		this.partsLogs = partsLogs;
	}

	
	@Transient
	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	
}


