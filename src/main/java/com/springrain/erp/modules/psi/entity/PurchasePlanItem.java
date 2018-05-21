/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;

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

import com.springrain.erp.common.utils.StringUtils;

/**
 * 新品采购计划明细
 * @author Michael
 */
@Entity
@Table(name = "psi_purchase_plan_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PurchasePlanItem  implements Serializable{
	private static final long serialVersionUID = 7002795970350580095L;
	private 	Integer           id; 		                     // id
	private 	PurchasePlan      plan;          		         //订单
	private 	PsiProduct        product; 	                     //产品
	private 	String            productName;                   //产品名字
	private 	String            colorCode;                     //颜色编号
	private 	String            countryCode;                   //国家编号
	private 	Integer           quantity;               		 //订单数量
	private 	Integer           quantityReview;              
	private 	Integer           quantityBossReview;          
	private 	String            remark;                        //备注
	private 	String            remarkReview;                  //初级审核备注
	private 	String            remarkBossReview;              //终极审核备注
	private 	String            delFlag="0";                   //删除标记
	private 	String            createSta;                     //生成订单标记
	
	
	public PurchasePlanItem() {
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
	@JoinColumn(name="plan_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PurchasePlan getPlan() {
		return plan;
	}


	public void setPlan(PurchasePlan plan) {
		this.plan = plan;
	}

	
	public String getColorCode() {
		return colorCode;
	}


	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}


	public String getCountryCode() {
		return countryCode;
	}


	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getDelFlag() {
		return delFlag;
	}


	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	

	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public Integer getQuantity() {
		return quantity;
	}


	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}


	public Integer getQuantityReview() {
		return quantityReview;
	}


	public void setQuantityReview(Integer quantityReview) {
		this.quantityReview = quantityReview;
	}


	public Integer getQuantityBossReview() {
		return quantityBossReview;
	}


	public void setQuantityBossReview(Integer quantityBossReview) {
		this.quantityBossReview = quantityBossReview;
	}


	
	public String getRemarkReview() {
		return remarkReview;
	}


	public void setRemarkReview(String remarkReview) {
		this.remarkReview = remarkReview;
	}


	public String getRemarkBossReview() {
		return remarkBossReview;
	}


	public void setRemarkBossReview(String remarkBossReview) {
		this.remarkBossReview = remarkBossReview;
	}


	@Transient
	public String getProductNameColor() {
		if(StringUtils.isNotBlank(colorCode)){
			return productName+"_"+colorCode;
		}
		return productName;
	}

	public String getCreateSta() {
		return createSta;
	}


	public void setCreateSta(String createSta) {
		this.createSta = createSta;
	}


	public PurchasePlanItem(PurchasePlan plan, PsiProduct product,String productName, String colorCode, String countryCode,	Integer quantity, String delFlag) {
		super();
		this.plan = plan;
		this.product = product;
		this.productName = productName;
		this.colorCode = colorCode;
		this.countryCode = countryCode;
		this.quantity = quantity;
		this.delFlag = delFlag;
	}
	
}


