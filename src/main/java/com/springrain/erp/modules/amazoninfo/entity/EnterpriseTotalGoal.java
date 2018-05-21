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

@Entity
@Table(name = "amazoninfo_enterprise_total_goal")
public class EnterpriseTotalGoal {
	private Integer id;
	private String month;
	private String country; // country为total表示系统计算出的当月总目标,只做参考不做具体计算
	private Float goal;
	private Float profitGoal; // 利润目标

	private Date startMonth;
	private Date endMonth;

	private Date createDate;
	private User createUser;

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

	@Transient
	public Date getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(Date startMonth) {
		this.startMonth = startMonth;
	}

	@Transient
	public Date getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(Date endMonth) {
		this.endMonth = endMonth;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Float getGoal() {
		return goal;
	}

	public void setGoal(Float goal) {
		this.goal = goal;
	}

	public Float getProfitGoal() {
		return profitGoal;
	}

	public void setProfitGoal(Float profitGoal) {
		this.profitGoal = profitGoal;
	}

}
