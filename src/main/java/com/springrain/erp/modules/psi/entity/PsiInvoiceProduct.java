/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "psi_invoice_product")
public class PsiInvoiceProduct {  
	
	private	    Integer      		id; 		  	 // id
	private     String              productCode;
	private     String              name;
	private     Float               taxRate;
	private     String              delFlag;
	
	public PsiInvoiceProduct() {
		super();
	}
	
	public PsiInvoiceProduct(Integer id) {
		super();
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

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Float taxRate) {
		this.taxRate = taxRate;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public PsiInvoiceProduct(String productCode, String name, Float taxRate,
			String delFlag) {
		super();
		this.productCode = productCode;
		this.name = name;
		this.taxRate = taxRate;
		this.delFlag = delFlag;
	}

}


