package com.springrain.erp.modules.amazoninfo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "amazoninfo_product_type_charge")
public class AmazonProductTypeCharge{

	private Integer id; // 编号
	private String productType;
	private String country;
	private Integer commissionPcent;
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}


	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getCommissionPcent() {
		return commissionPcent;
	}

	public void setCommissionPcent(Integer commissionPcent) {
		this.commissionPcent = commissionPcent;
	}
	
	
}
