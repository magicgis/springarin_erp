/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 营销计划Entity
 * @author Michael
 * @version 2017-06-12
 */
@Entity
@Table(name = "his_psi_marketing_plan")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class HisPsiMarketingPlan implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private 	Integer 	id; 				// 编号
	private 	String      countryCode;        // 国家编号
	private     String      startWeek;          // 开始周
	private     String      endWeek;	        // 结束周
	private     String      remark;             // 备注
	private     String      sta;                // 状态     0:新建   3：已审核   8：取消
	private     String      type;               // 类型     0:促销   1：广告
	
	private     Integer     marketingPlanId;    // 计划id 
	private		Date      	updateDate;         // 最后更新日期
	private		User     	updateUser;         // 最后更新人
	
	private     List<HisPsiMarketingPlanItem>  items ;
	

	public HisPsiMarketingPlan() {
		super();
	}

	public HisPsiMarketingPlan(Integer id){
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

	public Integer getMarketingPlanId() {
		return marketingPlanId;
	}

	public void setMarketingPlanId(Integer marketingPlanId) {
		this.marketingPlanId = marketingPlanId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getStartWeek() {
		return startWeek;
	}

	public void setStartWeek(String startWeek) {
		this.startWeek = startWeek;
	}

	
	@OneToMany(mappedBy = "marketingPlan", fetch = FetchType.EAGER)
	@Where(clause = "del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<HisPsiMarketingPlanItem> getItems() {
		return items;
	}

	public void setItems(List<HisPsiMarketingPlanItem> items) {
		this.items = items;
	}
	
	public String getEndWeek() {
		return endWeek;
	}

	public void setEndWeek(String endWeek) {
		this.endWeek = endWeek;
	}



	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@ManyToOne()
	@JoinColumn(name="update_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}

	
	public String getSta() {
		return sta;
	}

	public void setSta(String sta) {
		this.sta = sta;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public HisPsiMarketingPlan(String countryCode, String startWeek,String endWeek, String remark, String sta, String type,	Date updateDate, User updateUser
			,Integer marketingPlanId) {
		super();
		this.countryCode = countryCode;
		this.startWeek = startWeek;
		this.endWeek = endWeek;
		this.remark = remark;
		this.sta = sta;
		this.type = type;
		this.updateDate = updateDate;
		this.updateUser = updateUser;
		this.marketingPlanId =marketingPlanId;
	}
	
	
	
}


