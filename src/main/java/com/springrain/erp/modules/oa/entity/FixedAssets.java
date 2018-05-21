/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.oa.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 招聘Entity
 * 2016-11-21 michael
 */
@Entity
@Table(name = "oa_fixed_assets")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FixedAssets  implements Serializable {
	private static final long serialVersionUID = 5140527382119404091L;
	private		 Integer   		 id; 		          	 // id
	private      User            ownerUser;              // 使用人
	private      Office          ownerOffice;            // 使用部门
	private      String          name;                   // 资产名字
	private      String          model;                  // 型号
	private      String          billNo;                 // 编号
	private      String          fixedSta;               // 状态        报废、使用、权限
	private      String          place;                  // 存放地点
	private      String          remark;                 // 备注
	private      Date            buyDate;                // 购买日期
	
	private       User           createUser;             // 创建人
	private       Date           createDate;             // 创建时间
	private       User           updateUser;             // 编辑人
	private       Date           updateDate;             // 编辑时间
	
	private      String          delFlag;                // 删除状态
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public FixedAssets() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getFixedSta() {
		return fixedSta;
	}

	public void setFixedSta(String fixedSta) {
		this.fixedSta = fixedSta;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Date getBuyDate() {
		return buyDate;
	}

	public void setBuyDate(Date buyDate) {
		this.buyDate = buyDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@ManyToOne()
	@JoinColumn(name="owner_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(User ownerUser) {
		this.ownerUser = ownerUser;
	}

	@ManyToOne()
	@JoinColumn(name="create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	
	@ManyToOne()
	@JoinColumn(name="owner_office")
	@NotFound(action = NotFoundAction.IGNORE)
	public Office getOwnerOffice() {
		return ownerOffice;
	}

	public void setOwnerOffice(Office ownerOffice) {
		this.ownerOffice = ownerOffice;
	}

	
	@Transient
	public String  getOwner(){
		if(ownerUser==null&&ownerOffice==null){
			return "0";
		}else if(ownerUser!=null){
			return "1";
		}else{
			return "2";
		}
	}
	
	
	
	
	
	
	
}


