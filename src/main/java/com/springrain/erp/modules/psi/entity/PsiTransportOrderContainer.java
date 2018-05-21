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
 * 运单集装箱明细表Entity
 * @author Michael
 * @version 2015-01-15
 */
@Entity
@Table(name = "psi_transport_order_container")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiTransportOrderContainer {
	
	private		 Integer 		    id; 	
	private      String     	    containerType;    		 // 集装箱类型
	private      Integer    	    quantity;         		 // 数量
	private      Float              itemPrice;				 // 单价
	private      String      	    remark;           		 // 备注
	private      String             delFlag="0";             // 删除状态
	
	private      PsiTransportOrder  transportOrder;  		 // 运单

	public PsiTransportOrderContainer() {
		super();
	}

	public PsiTransportOrderContainer(Integer id){
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

	@ManyToOne()
	@JoinColumn(name="transport_order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiTransportOrder getTransportOrder() {
		return transportOrder;
	}

	public void setTransportOrder(PsiTransportOrder transportOrder) {
		this.transportOrder = transportOrder;
	}

	public String getContainerType() {
		return containerType;
	}

	public Float getItemPrice() {
		return itemPrice;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public void setItemPrice(Float itemPrice) {
		this.itemPrice = itemPrice;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}


