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
 * 营销计划Entity
 * @author Michael
 * @version 2017-06-12
 */
@Entity
@Table(name = "his_psi_marketing_plan_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class HisPsiMarketingPlanItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private 	Integer 	id; 				// 编号
	private 	String 		productName; 		// 产品名称
	private 	PsiProduct	product;            // 产品
	private 	String      colorCode;          // 颜色编号
	private     Integer     promoQuantity;      // 促销数(促销里面是总数，广告里面是日均数)
	private     Integer     realQuantity;       // 实际数
	
	private     String      	 delFlag="0";           
	private     HisPsiMarketingPlan marketingPlan ;

	public HisPsiMarketingPlanItem() {
		super();
	}

	
	public HisPsiMarketingPlanItem(Integer id){
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

	public Integer getRealQuantity() {
		return realQuantity;
	}

	public void setRealQuantity(Integer realQuantity) {
		this.realQuantity = realQuantity;
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

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public Integer getPromoQuantity() {
		return promoQuantity;
	}

	public void setPromoQuantity(Integer promoQuantity) {
		this.promoQuantity = promoQuantity;
	}

	
	public String getDelFlag() {
		return delFlag;
	}


	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}


	@ManyToOne()
	@JoinColumn(name="marketing_plan_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public HisPsiMarketingPlan getMarketingPlan() {
		return marketingPlan;
	}


	public void setMarketingPlan(HisPsiMarketingPlan marketingPlan) {
		this.marketingPlan = marketingPlan;
	}


	public HisPsiMarketingPlanItem(String productName, PsiProduct product,
			String colorCode, Integer promoQuantity, String delFlag,HisPsiMarketingPlan plan) {
		super();
		this.productName = productName;
		this.product = product;
		this.colorCode = colorCode;
		this.promoQuantity = promoQuantity;
		this.delFlag =delFlag;
		this.marketingPlan=plan;
	}

	@Transient
	public String getNameWithColor() {
		if(StringUtils.isNotBlank(colorCode)){
			return productName+"_"+colorCode;
		}
		return productName;
	}
	
}


