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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "amazoninfo_promotions_warning_item")
public class AmazonPromotionsWarningItem implements Serializable {
	private static final long serialVersionUID = -2433140471122314283L;
	private 	Integer 		id; 			   // id
	private 	String 			asin; 			   // asin
	private     String      	productNameColor;  // 产品名
	private     String          remark;            // 备注
	private     String          delFlag="0";       // 删除标致
	private     Integer         halfHourQuantity;  // 半个小时峰值   
	private     Integer         cumulativeQuantity;// 累计销量
	private     AmazonPromotionsWarning warning;      	   // 折扣预警
	
	private     String          isMain;
	
	
	public String getIsMain() {
		return isMain;
	}

	public void setIsMain(String isMain) {
		this.isMain = isMain;
	}

	public AmazonPromotionsWarningItem() {
		super();
	}

	public AmazonPromotionsWarningItem(Integer id){
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

	
	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
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
	public AmazonPromotionsWarning getWarning() {
		return warning;
	}

	public void setWarning(AmazonPromotionsWarning warning) {
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


