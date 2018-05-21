/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.parts;

import java.io.Serializable;
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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.google.common.collect.Lists;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 配件出库Entity
 * @author Michael
 * @version 2015-07-16
 */
@Entity
@Table(name = "psi_parts_inventory_out")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiPartsInventoryOut implements Serializable {
	private static final long serialVersionUID = 4896916279052483224L;
	private 	Integer		 id; 			  	// id
	private     String       billNo;          	// 配件出库单号
	private     Integer      productId;         // 产品id
	private     String       productName;     	// 产品名
	private     String       color;             // 颜色
	private     String       remark;          	// 备注
	private     User         createUser;      	// 创建人
	private     Date         createDate;        // 创建时间
	private     String       productIdColor;    // 产品id和颜色     （非数据库字段）
	private     List<PsiPartsInventoryOutItem> items =Lists.newArrayList();
	private     List<PsiPartsInventoryOutOrder> orders =Lists.newArrayList();
	
	
	public PsiPartsInventoryOut() {
		super();
	}

	public PsiPartsInventoryOut(Integer id){
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

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	@OneToMany(mappedBy = "partsInventoryOut")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<PsiPartsInventoryOutItem> getItems() {
		return items;
	}

	public void setItems(List<PsiPartsInventoryOutItem> items) {
		this.items = items;
	}

	
	
	@OneToMany(mappedBy = "partsInventoryOut",fetch=FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<PsiPartsInventoryOutOrder> getOrders() {
		return orders;
	}

	public void setOrders(List<PsiPartsInventoryOutOrder> orders) {
		this.orders = orders;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	@Transient
	public String getProductNameColor(){
		String productName=this.productName;
		if(StringUtils.isNotEmpty(this.color)){
			productName=productName+"_"+this.color;
		}
		return productName;
	}

	
	@Transient
	public String getProductIdColor() {
		return productIdColor;
	}

	public void setProductIdColor(String productIdColor) {
		this.productIdColor = productIdColor;
	}
	
	
	@Transient
	public Integer getQuantity(){
		Integer total =0;
		for(PsiPartsInventoryOutOrder order : orders){
			total+=order.getQuantity();
		}
		return total;
	}
}


