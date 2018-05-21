/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 回退测试记录
 * @author Michael
 * @version 2015-08-24
 */
@Entity
@Table(name = "amazoninfo_return_test")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class ReturnTest implements Serializable {
	private static final long serialVersionUID = -2544592454125249177L;
	private 		Integer 		id; 			// id
	private 		String 			productName; 	// 产品名
	private     	String      	sku;            // sku
	private 		Integer 	    quantity;       // 数量
	private     	String      	reason;         // 原因
	private     	String      	reasonDetail;   // 详细原因
	
	
	private         String          warehouseName;  //仓库名字
	private 		Integer 	    warehouseId;    // 库存id
	private 		Integer 	    newQuantity;    // new数量
	private 		Integer 	    renewQuantity;  // renew数量
	private 		Integer 	    oldQuantity;    // old数量
	private 		Integer 	    brokenQuantity; // broken数量
	private         String          stockInNo;      // 入库No
	
	private     	User        	createUser;     // 创建人
	private     	Date        	createDate;     // 创建日期
	private     	User        	updateUser;     // 修改人
	private     	Date        	updateDate;     // 修改日期
	private     	String      	testSta="0";    // 测试状态
	private 		String 			remark; 	    // 备注

	public ReturnTest() {
		super();
	}

	
	public ReturnTest(Integer id){
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


	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public String getSku() {
		return sku;
	}


	public void setSku(String sku) {
		this.sku = sku;
	}


	public Integer getQuantity() {
		return quantity;
	}


	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}


	public String getReason() {
		return reason;
	}


	public void setReason(String reason) {
		this.reason = reason;
	}


	public String getReasonDetail() {
		return reasonDetail;
	}


	public void setReasonDetail(String reasonDetail) {
		this.reasonDetail = reasonDetail;
	}


	public Integer getWarehouseId() {
		return warehouseId;
	}


	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}


	public Integer getNewQuantity() {
		return newQuantity;
	}


	public void setNewQuantity(Integer newQuantity) {
		this.newQuantity = newQuantity;
	}


	public Integer getRenewQuantity() {
		return renewQuantity;
	}


	public void setRenewQuantity(Integer renewQuantity) {
		this.renewQuantity = renewQuantity;
	}


	public Integer getOldQuantity() {
		return oldQuantity;
	}


	public void setOldQuantity(Integer oldQuantity) {
		this.oldQuantity = oldQuantity;
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


	public Integer getBrokenQuantity() {
		return brokenQuantity;
	}


	public void setBrokenQuantity(Integer brokenQuantity) {
		this.brokenQuantity = brokenQuantity;
	}


	public String getStockInNo() {
		return stockInNo;
	}


	public void setStockInNo(String stockInNo) {
		this.stockInNo = stockInNo;
	}


	public String getWarehouseName() {
		return warehouseName;
	}


	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
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


	public String getTestSta() {
		return testSta;
	}


	public void setTestSta(String testSta) {
		this.testSta = testSta;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}

	
}


