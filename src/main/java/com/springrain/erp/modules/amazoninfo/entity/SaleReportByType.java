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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 产品销量销售额Entity
 * @author Tim
 * @version 2015-06-01
 */
@Entity
@Table(name = "amazoninfo_sale_report_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SaleReportByType implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id; 		// 编号
	private Float sales;
	private Float sureSales;
	private Float realSales;
	private Integer salesVolume;
	private Integer sureSalesVolume;
	private Integer realSalesVolume;
	private String type;
	private String country;
	private Date date;
	

	private String orderType;
	
    private String accountName;
	
	

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
	public SaleReportByType() {
		super();
	}

	public SaleReportByType(Integer id){
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

	public Float getSales() {
		return sales;
	}

	public void setSales(Float sales) {
		this.sales = sales;
	}

	public Integer getSalesVolume() {
		return salesVolume;
	}

	public void setSalesVolume(Integer salesVolume) {
		this.salesVolume = salesVolume;
	}
	
	public Float getSureSales() {
		return sureSales;
	}

	public void setSureSales(Float sureSales) {
		this.sureSales = sureSales;
	}

	public Integer getSureSalesVolume() {
		return sureSalesVolume;
	}

	public void setSureSalesVolume(Integer sureSalesVolume) {
		this.sureSalesVolume = sureSalesVolume;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Column(updatable=false)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Float getRealSales() {
		return realSales;
	}

	public void setRealSales(Float realSales) {
		this.realSales = realSales;
	}

	public Integer getRealSalesVolume() {
		return realSalesVolume;
	}

	public void setRealSalesVolume(Integer realSalesVolume) {
		this.realSalesVolume = realSalesVolume;
	}
	
	

}


