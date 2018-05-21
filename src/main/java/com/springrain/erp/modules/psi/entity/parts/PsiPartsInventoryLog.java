/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.parts;

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

import com.springrain.erp.modules.sys.entity.User;

/**
 * 配件库存日志Entity
 * @author Michael
 * @version 2014-12-24
 */
@Entity
@Table(name = "psi_parts_inventory_log")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiPartsInventoryLog implements Serializable{
	private static final long serialVersionUID = -5289441950608920642L;
	private 	Integer			 id; 		   			  // id
	private     Integer          partsId;                 // 配件id
	private     String           partsName;   		      // 配件名称
	private     Integer          quantity;     			  // 操作数量
	private     String           dataType;                // 数据类型    poFrozen  poNotFrozen  stockFrozen   stockNotFrozen
	private     String           relativeNumber;          // 相关单号
	private     User             createUser;              // 操作人
	private     Date             createDate;              // 操作时间
	private     String           operateType;             // 操作类型
	private     String           remark;                  // 备注
	
	public PsiPartsInventoryLog() {
		super();
	}

	public PsiPartsInventoryLog(Integer id){
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

	public Integer getPartsId() {
		return partsId;
	}

	public void setPartsId(Integer partsId) {
		this.partsId = partsId;
	}

	public String getPartsName() {
		return partsName;
	}

	public void setPartsName(String partsName) {
		this.partsName = partsName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getRelativeNumber() {
		return relativeNumber;
	}

	public void setRelativeNumber(String relativeNumber) {
		this.relativeNumber = relativeNumber;
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

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Transient
	public Integer getAbsQuantity() {
		return Math.abs(quantity);
	}


	public PsiPartsInventoryLog(Integer partsId, String partsName,Integer quantity, String dataType, String relativeNumber,	User createUser, Date createDate, String operateType, String remark) {
		super();
		this.partsId = partsId;
		this.partsName = partsName;
		this.quantity = quantity;
		this.dataType = dataType;
		this.relativeNumber = relativeNumber;
		this.createUser = createUser;
		this.createDate = createDate;
		this.operateType = operateType;
		this.remark = remark;
	}
	
	
	

}


