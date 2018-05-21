package com.springrain.erp.modules.psi.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 进销存产品
 */
@Entity
@Table(name = "psi_product_tiered_price_log")
@DynamicInsert
@DynamicUpdate
public class PsiProductTieredPriceLog {
	
	private			 Integer 			id;
	private			 PsiProduct			product;            //产品
	private			 String			    productNameColor;   //产品+颜色
	private			 String			    color;              //颜色
	private			 PsiSupplier		supplier;           //供应商
	private          String             content;            //价格改动日志内容   
	private          String             remark;             //备注
	private 		 User 				createUser;
	private			 Date				createTime;
	private          String             tieredType;          //类型
	private          String             currencyType;        //货币类型
	private          Float              oldPrice;            //原价格
	private          Float              price;               //现在价格
	private			 Date				updateTime;          //(非字段)
	private          String             productIdColor;      //(非字段)
	
	private 		 User 				sureUser;
	private			 Date				sureTime;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public String getProductNameColor() {
		return productNameColor;
	}
	public void setProductNameColor(String productNameColor) {
		this.productNameColor = productNameColor;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
	@ManyToOne()
	@JoinColumn(name="product_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiProduct getProduct() {
		return product;
	}
	public void setProduct(PsiProduct product) {
		this.product = product;
	}
	
	@ManyToOne()
	@JoinColumn(name="supplier_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiSupplier getSupplier() {
		return supplier;
	}
	public void setSupplier(PsiSupplier supplier) {
		this.supplier = supplier;
	}
	
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
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
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@ManyToOne()
	@JoinColumn(name="sure_user")
	public User getSureUser() {
		return sureUser;
	}
	public void setSureUser(User sureUser) {
		this.sureUser = sureUser;
	}
	public Date getSureTime() {
		return sureTime;
	}
	public void setSureTime(Date sureTime) {
		this.sureTime = sureTime;
	}
	@Transient
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	@Transient
	public String getProductIdColor() {
		return productIdColor;
	}
	public void setProductIdColor(String productIdColor) {
		this.productIdColor = productIdColor;
	}
	public String getTieredType() {
		return tieredType;
	}
	public void setTieredType(String tieredType) {
		this.tieredType = tieredType;
	}
	public Float getOldPrice() {
		return oldPrice;
	}
	public void setOldPrice(Float oldPrice) {
		this.oldPrice = oldPrice;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public String getCurrencyType() {
		return currencyType;
	}
	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}
	
}
