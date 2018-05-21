/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

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
 * sku确认清单Entity
 * @author Michael
 * @version 2015-05-25
 */
@Entity
@Table(name = "psiSkuChangeBillItem")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PsiSkuChangeBillItem{
	private 		Integer			 id; 		  // id
	private 		String 			 sku; 	      // sku
	private         Integer  	     quantity;    // 数量
	private     	String           remark;      // 备注
	private         PsiSkuChangeBill skuChangeBill; 
	
	    
	@ManyToOne()
	@JoinColumn(name="sku_change_bill_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSkuChangeBill getSkuChangeBill() {
		return skuChangeBill;
	}
	public void setSkuChangeBill(PsiSkuChangeBill skuChangeBill) {
		this.skuChangeBill = skuChangeBill;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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


