package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springrain.erp.common.utils.excel.annotation.ExcelField;

/**
 * 产品历史价格Entity
 * @author Tim
 * @version 2015-04-10
 */
@Entity
@Table(name = "amazoninfo_product_history_price")
public class ProductHistoryPrice implements Serializable{ 
	
	private static final long serialVersionUID = 1L;
	private Integer id; // 编号
	
	private Date dataDate;
	private String sku;
	private Float salePrice; 
	private String country;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}

	@ExcelField(title="country", align=2, sort=50)
	@Column(updatable=false)
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public Float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Float salePrice) {
		this.salePrice = salePrice;
	}
	
	public ProductHistoryPrice() {
	}

	public ProductHistoryPrice(Date dataDate, String sku, Float salePrice,
			String country) {
		super();
		this.dataDate = dataDate;
		this.sku = sku;
		this.salePrice = salePrice;
		this.country = country;
	}
	
}


