package com.springrain.erp.modules.psi.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.springrain.erp.modules.psi.entity.parts.PsiParts;

@Entity
@Table(name = "psi_product_parts")
public class ProductParts {
	private		Integer  	 id;
	private 	PsiProduct   product;
	private 	PsiParts 	 parts;
	private 	String 		 color;
	private     Integer      mixtureRatio;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "product_id", unique = true)
	public PsiProduct getProduct() {
		return product;
	}

	public void setProduct(PsiProduct product) {
		this.product = product;
	}
	
	@ManyToOne
	@JoinColumn(name = "parts_id", unique = true)
	public PsiParts getParts() {
		return parts;
	}

	public void setParts(PsiParts parts) {
		this.parts = parts;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	public Integer getMixtureRatio() {
		return mixtureRatio;
	}

	public void setMixtureRatio(Integer mixtureRatio) {
		this.mixtureRatio = mixtureRatio;
	}
	

	public ProductParts() {}

	public ProductParts(PsiProduct product, PsiParts parts,String color,Integer mixtureRatio) {
		super();
		this.product = product;
		this.parts = parts;
		this.color =color;
		this.mixtureRatio =mixtureRatio;
	}

	
}
