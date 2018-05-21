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

import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * sku调换清单Entity
 * @author Michael
 * @version 2015-05-25
 */
@Entity
@Table(name = "psi_sku_change_bill")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PsiSkuChangeBill{
	private 	Integer  		id; 				// id
	private 	String 			evenName; 			// 事件名字
    private     Integer         warehouseId;   	    // 仓库id
    private     String          warehouseName; 	    // 仓库名
    private 	Integer         productId;          // 产品id
    private 	String          productName;        // 产品名
    private 	String          productCountry;     // 产品国家
    private 	String          productColor;       // 产品颜色
    private 	String          fromSku;            // 从sku
    private 	String          toSku;              // 到sku
    private     String          batchNumber;        // 调换批次号
    private     Integer         quantity;           // 数量
    private 	Date            applyDate;			// 申请日期
    private     User            applyUser;          // 申请人
    private 	Date            sureDate;           // 确认日期
    private 	User            sureUser;           // 确认人
    private 	Date            cancelDate;         // 取消日期
    private     User            cancelUser;         // 取消人
    private     String          changeSta;          // 状态
    private     String          remark;             // 备注
    
    private     String          shippmentId;         //shippmentId
    
	private     List<PsiSkuChangeBillItem>     items;

    
    
    @OneToMany(mappedBy = "skuChangeBill")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<PsiSkuChangeBillItem> getItems() {
		return items;
	}

	public void setItems(List<PsiSkuChangeBillItem> items) {
		this.items = items;
	}

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

	public String getEvenName() {
		return evenName;
	}

	public void setEvenName(String evenName) {
		this.evenName = evenName;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
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

	public void setProductCountry(String productCountry) {
		this.productCountry = productCountry;
	}

	public String getProductColor() {
		return productColor;
	}

	public void setProductColor(String productColor) {
		this.productColor = productColor;
	}

	
	@Transient
	public String getShippmentId() {
		return shippmentId;
	}

	public void setShippmentId(String shippmentId) {
		this.shippmentId = shippmentId;
	}

	public String getFromSku() {
		return fromSku;
	}

	public void setFromSku(String fromSku) {
		this.fromSku = fromSku;
	}

	public String getToSku() {
		return toSku;
	}

	public void setToSku(String toSku) {
		this.toSku = toSku;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
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

	
	@ManyToOne()
	@JoinColumn(name="cancel_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCancelUser() {
		return cancelUser;
	}

	public void setCancelUser(User cancelUser) {
		this.cancelUser = cancelUser;
	}
	
	
	@Transient
	public String getProductColorCountry(){
		String name = productName;
		if(StringUtils.isNotEmpty(productColor)){
			name=name+"_"+productColor;
		}
		return name+","+productCountry;
	}
	
	public PsiSkuChangeBill() {}

	public PsiSkuChangeBill(String evenName, Integer warehouseId,String warehouseName, Integer productId, String productName,String productCountry,
			String productColor, String fromSku,String toSku, String batchNumber, Integer quantity, Date applyDate,	User applyUser, String changeSta) {
		super();
		this.evenName = evenName;
		this.warehouseId = warehouseId;
		this.warehouseName = warehouseName;
		this.productId = productId;
		this.productName = productName;
		this.productCountry = productCountry;
		this.productColor = productColor;
		this.fromSku = fromSku;
		this.toSku = toSku;
		this.batchNumber = batchNumber;
		this.quantity = quantity;
		this.applyDate = applyDate;
		this.applyUser = applyUser;
		this.changeSta = changeSta;
	}

	
}


