/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "lc_psi_parts_order_basis")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiPartsOrderBasis implements Serializable{
	private static final long serialVersionUID = 2679531007848723237L;
	private 	Integer    id;  
	private 	Integer    partsId; 			// 配件id
	private 	String     partsName; 			// 配件名称
	private 	Integer    purchaseOrderId; 	// 配件id
	private 	String     purchaseOrderNo; 	// 配件名称
	private     Integer    needQuantity;        // 需要数
	private     Integer    orderQuantity;		// 下订单数量
	private     Integer    poFrozen;       		// po冻结
	private     Integer    poNotFrozen;   		// po非冻结
	private     Integer    stockFrozen;    		// stock冻结
	private     Integer    stockNotFrozen;		// stock非冻结  I
	private     Integer    supplierId;          // 供应商id
	private     String     remark;              // 备注
	private     Integer    moq;                 // 最小下单数
	private     Date       deliveryDate;        // 收货日期
	private     Integer    mixtureRatio;        // 产品配比
	
	private     Integer    afterPoFrozen;       // po冻结         (计算后得出增加或减少的数量)
	private     Integer    afterPoNotFrozen;   	// po非冻结     (计算后得出增加或减少的数量)
	private     Integer    afterStockFrozen;    // stock冻结 (计算后得出增加或减少的数量)
	private     Integer    afterStockNotFrozen; // stock非冻结(计算后得出增加或减少的数量)
	private     String     cancelSta="0";       // 取消状态 1 已取消
	
	
	public LcPsiPartsOrderBasis() {
		super();
	}

	public LcPsiPartsOrderBasis(Integer id){
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

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
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

	public Integer getMoq() {
		return moq;
	}

	public void setMoq(Integer moq) {
		this.moq = moq;
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

	public Integer getNeedQuantity() {
		return needQuantity;
	}

	public void setNeedQuantity(Integer needQuantity) {
		this.needQuantity = needQuantity;
	}

	public Integer getOrderQuantity() {
		return orderQuantity;
	}

	public void setOrderQuantity(Integer orderQuantity) {
		this.orderQuantity = orderQuantity;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	@Transient
	public Integer getMixtureRatio() {
		return mixtureRatio;
	}

	public void setMixtureRatio(Integer mixtureRatio) {
		this.mixtureRatio = mixtureRatio;
	}

	public Integer getAfterPoFrozen() {
		return afterPoFrozen;
	}

	public void setAfterPoFrozen(Integer afterPoFrozen) {
		this.afterPoFrozen = afterPoFrozen;
	}

	public Integer getAfterPoNotFrozen() {
		return afterPoNotFrozen;
	}

	public void setAfterPoNotFrozen(Integer afterPoNotFrozen) {
		this.afterPoNotFrozen = afterPoNotFrozen;
	}

	public Integer getAfterStockFrozen() {
		return afterStockFrozen;
	}

	public void setAfterStockFrozen(Integer afterStockFrozen) {
		this.afterStockFrozen = afterStockFrozen;
	}

	public Integer getAfterStockNotFrozen() {
		return afterStockNotFrozen;
	}

	public void setAfterStockNotFrozen(Integer afterStockNotFrozen) {
		this.afterStockNotFrozen = afterStockNotFrozen;
	}

	public String getCancelSta() {
		return cancelSta;
	}

	public void setCancelSta(String cancelSta) {
		this.cancelSta = cancelSta;
	}
	
}


