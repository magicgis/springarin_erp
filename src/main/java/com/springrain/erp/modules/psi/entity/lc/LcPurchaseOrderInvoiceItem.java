/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.io.Serializable;
import java.util.List;

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

import com.google.common.collect.Lists;

@Entity
@Table(name = "lc_psi_purchase_order_invoice_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPurchaseOrderInvoiceItem implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private		 Integer   		 id; 		          	 // id
	private      String  invoiceNo;
	private      Integer  invoiceQuantity;
	private 	 LcPurchaseOrderInvoice invoice;
	private      String  delFlag;
	
	public LcPurchaseOrderInvoiceItem() {
		super();
	}

	public LcPurchaseOrderInvoiceItem(Integer id){
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

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Integer getInvoiceQuantity() {
		return invoiceQuantity;
	}

	public void setInvoiceQuantity(Integer invoiceQuantity) {
		this.invoiceQuantity = invoiceQuantity;
	}

	@ManyToOne()
	@JoinColumn(name="invoice_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPurchaseOrderInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(LcPurchaseOrderInvoice invoice) {
		this.invoice = invoice;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	
}


