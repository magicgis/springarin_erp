package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 销量预测修改记录Entity
 */
@Entity
@Table(name = "amazoninfo_sales_forecast_record")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SalesForecastRecord {
	
	private Integer id; 		// 编号
	private User createBy;
	private Date createDate;
	
	private String remark;
	private String state;
	
	private String country; 	// 国家
	private String productName;//产品名称(含颜色)
	private String month;	//月份yyyy-MM

	//提交日期开始的6个月预测销量
	private Integer forecast1;
	private Integer forecast2;
	private Integer forecast3;
	private Integer forecast4;
	private Integer forecast5;
	private Integer forecast6;
	
	public SalesForecastRecord() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateBy() {
		return createBy;
	}

	public void setCreateBy(User createBy) {
		this.createBy = createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Integer getForecast1() {
		return forecast1;
	}

	public void setForecast1(Integer forecast1) {
		this.forecast1 = forecast1;
	}

	public Integer getForecast2() {
		return forecast2;
	}

	public void setForecast2(Integer forecast2) {
		this.forecast2 = forecast2;
	}

	public Integer getForecast3() {
		return forecast3;
	}

	public void setForecast3(Integer forecast3) {
		this.forecast3 = forecast3;
	}

	public Integer getForecast4() {
		return forecast4;
	}

	public void setForecast4(Integer forecast4) {
		this.forecast4 = forecast4;
	}

	public Integer getForecast5() {
		return forecast5;
	}

	public void setForecast5(Integer forecast5) {
		this.forecast5 = forecast5;
	}

	public Integer getForecast6() {
		return forecast6;
	}

	public void setForecast6(Integer forecast6) {
		this.forecast6 = forecast6;
	}
	
}


