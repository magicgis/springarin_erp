/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
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

import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * new to offline 转化清单Entity
 * @author Michael
 * @version 2015-05-25
 */
@Entity
@Table(name = "psi_quality_change_bill")
public class PsiQualityChangeBill{
	private 	Integer  		id; 				// id
    private     Integer         warehouseId;   	    // 仓库id
    private     String          batchNumber;        // 调换批次号
    private     String          changeType;         // 改变类型 :New_To_Offline
	private 	String 			sku; 			    // sku
    private     Integer         quantity;           // 数量
    private     String          remark;             // 备注
    private     String          changeSta;          // 状态
    
    private 	Date            applyDate;			// 申请日期
    private     User            applyUser;          // 申请人
    private 	Date            sureDate;           // 确认日期
    private 	User            sureUser;           // 确认人
    private 	Date            cancelDate;         // 取消日期
    private     User            cancelUser;         // 取消人
    
    
    private     String          unlineOrderNo;      // 线下订单号
    private     Integer         unlineOrderId;      // 线下订单id
    private 	Integer         productId;          // 产品id
    private 	String          productName;        // 产品名
    private 	String          productCountry;     // 产品国家
    private 	String          productColor;       // 产品颜色
    private     List<PsiQualityChangeBillItem>  items =Lists.newArrayList();  // 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
   public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getChangeSta() {
		return changeSta;
	}

	public void setChangeSta(String changeSta) {
		this.changeSta = changeSta;
	}


	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Transient
	public String getProductNameColor(){
		String proName=productName;
		if(StringUtils.isNotEmpty(this.productColor)){
			proName=proName+"_"+this.productColor;
		}
		return proName;
	}
	
	public String getUnlineOrderNo() {
		return unlineOrderNo;
	}

	public void setUnlineOrderNo(String unlineOrderNo) {
		this.unlineOrderNo = unlineOrderNo;
	}

	public Integer getUnlineOrderId() {
		return unlineOrderId;
	}

	public void setUnlineOrderId(Integer unlineOrderId) {
		this.unlineOrderId = unlineOrderId;
	}

	@ManyToOne()
	@JoinColumn(name="cancel_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCancelUser() {
		return cancelUser;
	}

	public void setCancelUser(User cancelUser) {
		this.cancelUser = cancelUser;
	}
	
	
	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
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

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	@ManyToOne()
	@JoinColumn(name="apply_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getApplyUser() {
		return applyUser;
	}

	public void setApplyUser(User applyUser) {
		this.applyUser = applyUser;
	}

	public Date getSureDate() {
		return sureDate;
	}

	public void setSureDate(Date sureDate) {
		this.sureDate = sureDate;
	}
	

	@ManyToOne()
	@JoinColumn(name="sure_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getSureUser() {
		return sureUser;
	}

	public void setSureUser(User sureUser) {
		this.sureUser = sureUser;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
	
	

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductCountry() {
		return productCountry;
	}

	@OneToMany(mappedBy = "qualityBill")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<PsiQualityChangeBillItem> getItems() {
		return items;
	}

	public void setItems(List<PsiQualityChangeBillItem> items) {
		this.items = items;
	}

	public void setProductCountry(String productCountry) {
		this.productCountry = productCountry;
	}

	public String getProductColor() {
		return productColor;
	}

	public void setProductColor(String productColor) {
		this.productColor = productColor;
	}

	public PsiQualityChangeBill() {}

	public PsiQualityChangeBill(Integer warehouseId,String batchNumber, String changeType, String sku,	Integer quantity, String remark, String changeSta, Date applyDate,User applyUser
			,String unlineOrderNo,Integer unlineOrderId,Integer productId,String productName,String productCountry,String productColor) {
		super();
		this.warehouseId = warehouseId;
		this.batchNumber = batchNumber;
		this.changeType = changeType;
		this.sku = sku;
		this.quantity = quantity;
		this.remark = remark;
		this.changeSta = changeSta;
		this.applyDate = applyDate;
		this.applyUser = applyUser;
		this.unlineOrderId=unlineOrderId;
		this.unlineOrderNo=unlineOrderNo;
		this.productId=productId;
		this.productName=productName;
		this.productColor=productColor;
		this.productCountry=productCountry;
	}

	
}


