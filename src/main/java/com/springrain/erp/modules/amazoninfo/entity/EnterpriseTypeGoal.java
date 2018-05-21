package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 分产品类型月目标
 * @author lee
 * @date 2016-6-6
 */
@Entity
@Table(name = "amazoninfo_enterprise_type_goal")
public class EnterpriseTypeGoal {

	private Integer id;
	private String month;
	private String productType;	//产品类型
	private String line;	//销售线
	private String country;
	private Date createDate;
	private User createUser;
	private Float salesGoal;	//销售额目标
	private Float profitGoal;	//利润目标
	
    private Date goalMonth;

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

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@ManyToOne()
	@JoinColumn(name = "create_user")
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Float getSalesGoal() {
		return salesGoal;
	}

	public void setSalesGoal(Float salesGoal) {
		this.salesGoal = salesGoal;
	}

	public Float getProfitGoal() {
		return profitGoal;
	}

	public void setProfitGoal(Float profitGoal) {
		this.profitGoal = profitGoal;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	@Transient
	public Date getGoalMonth() {
		return goalMonth;
	}

	public void setGoalMonth(Date goalMonth) {
		this.goalMonth = goalMonth;
	}

}
