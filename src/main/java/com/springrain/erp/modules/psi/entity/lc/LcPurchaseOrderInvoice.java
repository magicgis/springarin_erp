/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.google.common.collect.Lists;

@Entity
@Table(name = "lc_psi_purchase_order_invoice")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPurchaseOrderInvoice implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private		 Integer   		 id; 		          	 // id
	private      LcPurchaseOrder order;
	private      String  productName;
	private      Integer  quantityOrdered;
	private      Integer  quantityMatched;
	private 	 List<LcPurchaseOrderInvoiceItem> items = Lists.newArrayList();
	
	
	public LcPurchaseOrderInvoice() {
		super();
	}

	public LcPurchaseOrderInvoice(Integer id){
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
	@JoinColumn(name="purchase_order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPurchaseOrder getOrder() {
		return order;
	}

	public void setOrder(LcPurchaseOrder order) {
		this.order = order;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getQuantityOrdered() {
		return quantityOrdered;
	}

	public void setQuantityOrdered(Integer quantityOrdered) {
		this.quantityOrdered = quantityOrdered;
	}

	public Integer getQuantityMatched() {
		return quantityMatched;
	}

	public void setQuantityMatched(Integer quantityMatched) {
		this.quantityMatched = quantityMatched;
	}

	@OneToMany(mappedBy = "invoice",fetch=FetchType.EAGER)
	@Where(clause="del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<LcPurchaseOrderInvoiceItem> getItems() {
		return items;
	}

	public void setItems(List<LcPurchaseOrderInvoiceItem> items) {
		this.items = items;
	}
	
}


