/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "amazoninfo_spread_report")
public class AmazonSpreadReport implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id; 		// 编号
	private Date createDate;
	private String country;
	private String productName;
	private Integer session;
	private Integer salesVolume;
	private float sales;
    private String sku;
    private String asin;
    private float price;
    private float cost;
    private int order;
    private float conversion;

	private Date endDate;
	private Float profit;
	
	
	private String start;
	private String end;
	
	@Transient
	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	@Transient
	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public AmazonSpreadReport() {
		super();
	}

	public AmazonSpreadReport(String country, String productName,
			Integer session, Integer salesVolume, float sales, String sku,
			String asin, float price, float cost, int order, float conversion,
			Float profit) {
		super();
		this.country = country;
		this.productName = productName;
		this.session = session;
		this.salesVolume = salesVolume;
		this.sales = sales;
		this.sku = sku;
		this.asin = asin;
		this.price = price;
		this.cost = cost;
		this.order = order;
		this.conversion = conversion;
		this.profit = profit;
	}

	@Transient
	public Float getProfit() {
		return profit;
	}

	public void setProfit(Float profit) {
		this.profit = profit;
	}

	@Transient
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Column(updatable=false)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getSession() {
		return session;
	}

	public void setSession(Integer session) {
		this.session = session;
	}

	public Integer getSalesVolume() {
		return salesVolume;
	}

	public void setSalesVolume(Integer salesVolume) {
		this.salesVolume = salesVolume;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public float getSales() {
		return sales;
	}

	public void setSales(float sales) {
		this.sales = sales;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public float getConversion() {
		return conversion;
	}

	public void setConversion(float conversion) {
		this.conversion = conversion;
	}

	
	
}


