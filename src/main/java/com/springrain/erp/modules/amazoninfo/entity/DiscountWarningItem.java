/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 折扣预警Entity
 * @author Michael
 * @version 2015-08-24
 */
@Entity
@Table(name = "amazoninfo_discount_warning_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DiscountWarningItem implements Serializable {
	private static final long serialVersionUID = -2433140471122314283L;
	private 	Integer 		id; 			   // id
	private 	String 			sku; 			   // sku
	private     String      	productNameColor;  // 产品名
	private     String          remark;            // 备注
	private     String          delFlag="0";       // 删除标致
	private     Integer         halfHourQuantity;  // 半个小时峰值   
	private     Integer         cumulativeQuantity;// 累计销量
	private     DiscountWarning warning;      	   // 折扣预警
	public DiscountWarningItem() {
		super();
	}

	public DiscountWarningItem(Integer id){
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

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getProductNameColor() {
		return productNameColor;
	}

	public void setProductNameColor(String productNameColor) {
		this.productNameColor = productNameColor;
	}

	@ManyToOne()
	@JoinColumn(name="warning_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public DiscountWarning getWarning() {
		return warning;
	}

	public void setWarning(DiscountWarning warning) {
		this.warning = warning;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getHalfHourQuantity() {
		return halfHourQuantity;
	}

	public void setHalfHourQuantity(Integer halfHourQuantity) {
		this.halfHourQuantity = halfHourQuantity;
	}

	public Integer getCumulativeQuantity() {
		return cumulativeQuantity;
	}

	public void setCumulativeQuantity(Integer cumulativeQuantity) {
		this.cumulativeQuantity = cumulativeQuantity;
	}
	
	
	
	
}


