/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.sys.entity.User;

/**
 * improve
 * @author Michael
 * @version 2015-06-01
 */  


@Entity
@Table(name = "psi_product_improve")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiProductImprove {  
	private	    Integer      		id; 		  	 		// id
	private 	String		 		orderNo;         		// 采购批次
	private     String              productNameColor;	    // 产品名
	private     Date         		improveDate;      		// 优化时间
	private     String              improveContent;         // 优化情况
	private     String              isChangeSku;            // 是否更换sku
	private     String       		delFlag="0";     		// 删除类型
	private     User         		createUser;      		// 创建人
	private     Date         		createDate;      		// 创建时间
	private     User         		deleteUser;      		// 删除人
	private     Date         		deleteDate;      		// 删除时间
	
	private     String              productName;            // 产品名（非数据库字段）
	private     String              color;                  // 颜色（非数据库字段）
	
	
	public PsiProductImprove() {
		super();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getProductNameColor() {
		return productNameColor;
	}

	public void setProductNameColor(String productNameColor) {
		this.productNameColor = productNameColor;
	}

	public Date getImproveDate() {
		return improveDate;
	}

	public void setImproveDate(Date improveDate) {
		this.improveDate = improveDate;
	}

	public String getImproveContent() {
		return improveContent;
	}

	public void setImproveContent(String improveContent) {
		this.improveContent = improveContent;
	}

	public String getIsChangeSku() {
		return isChangeSku;
	}

	public void setIsChangeSku(String isChangeSku) {
		this.isChangeSku = isChangeSku;
	}


	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
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
	@JoinColumn(name="delete_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getDeleteUser() {
		return deleteUser;
	}

	public void setDeleteUser(User deleteUser) {
		this.deleteUser = deleteUser;
	}

	public Date getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
	}

	@Transient
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Transient
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	
}


