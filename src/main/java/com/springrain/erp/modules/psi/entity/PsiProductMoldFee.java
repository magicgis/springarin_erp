package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 产品模具费用Entity
 */
@Entity
@Table(name = "psi_product_mold_fee")
public class PsiProductMoldFee implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private PsiSupplier supplier;	//供应商
	private String productName;	//产品名称(带颜色,多个以逗号连接,多规格多颜色共用模具)
	private Float moldFee;		//模具费
	private String returnFlag;	//费用是否返还 0:返还 1:不返还
	private Integer returnNum;	//返还数量,达到多少下单量时返还模具费
	private Date createDate;	//创建时间
	private User createBy;		//创建时间
	private String saleFlag = "0";	//销量标记(前5000个需要将模具费分摊计算保本价) 0:未达到(默认) 1:已达到
	private String purchaseFlag = "0";//下单量标记 0:(默认) 1:已达到5000(不需要均摊) 2:已到达返还数(模具费可返还时)
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Float getMoldFee() {
		return moldFee;
	}

	public void setMoldFee(Float moldFee) {
		this.moldFee = moldFee;
	}

	public String getReturnFlag() {
		return returnFlag;
	}

	public void setReturnFlag(String returnFlag) {
		this.returnFlag = returnFlag;
	}

	public Integer getReturnNum() {
		return returnNum;
	}

	public void setReturnNum(Integer returnNum) {
		this.returnNum = returnNum;
	}

	@ManyToOne()
	@JoinColumn(name="create_by")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateBy() {
		return createBy;
	}

	public void setCreateBy(User createBy) {
		this.createBy = createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getSaleFlag() {
		return saleFlag;
	}

	public void setSaleFlag(String saleFlag) {
		this.saleFlag = saleFlag;
	}

	public String getPurchaseFlag() {
		return purchaseFlag;
	}

	public void setPurchaseFlag(String purchaseFlag) {
		this.purchaseFlag = purchaseFlag;
	}

	@Transient
	public List<String> getProductNames() {
		List<String> list = Lists.newArrayList();
		if (StringUtils.isNotEmpty(productName)) {
			for (String name : productName.split(",")) {
				list.add(name);
			}
		}
		return list;
	}
	
}
