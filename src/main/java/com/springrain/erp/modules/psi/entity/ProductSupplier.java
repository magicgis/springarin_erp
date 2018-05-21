package com.springrain.erp.modules.psi.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "psi_product_supplier")
public class ProductSupplier {
	
	private	Integer  id;
	
	private PsiProduct product;
	
	private PsiSupplier supplier;
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "product_id", unique = true)
	public PsiProduct getProduct() {
		return product;
	}

	public void setProduct(PsiProduct product) {
		this.product = product;
	}
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "supplier_id", unique = true)
	public PsiSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(PsiSupplier supplier) {
		this.supplier = supplier;
	}


	public ProductSupplier() {}

	public ProductSupplier(PsiProduct product, PsiSupplier supplier) {
		super();
		this.product = product;
		this.supplier = supplier;
	}
	
}
