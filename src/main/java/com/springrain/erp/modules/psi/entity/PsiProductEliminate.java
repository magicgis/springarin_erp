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

/**
 * 进销存产品属性（淘汰、主力、新品、上架时间等）明细（分颜色分平台）
 */
@Entity
@Table(name = "psi_product_eliminate")
@DynamicInsert
@DynamicUpdate
public class PsiProductEliminate {
	
	private Integer id;
	private PsiProduct product; // 产品
	private String productName; // 产品名
	private String country; // 国家
	private String color; // 颜色
	private String isSale; // 1：在售  0：淘汰
	private String delFlag; // 删除标记
	private String isNew; // 1：新品  0：普通
	private String isMain; // 1：主力  0：普通
	private String addedMonth; //上架时间
	private String salesForecastScheme; //销售预测方案 1：A方案 2：B方案 3：C方案
	private Date eliminateTime; //淘汰时间
	private String offWebsite = "0";
	private Float  piPrice;
	private Float  cnpiPrice;
	
	private Integer transportType;// 1:海运 2：空运
	private Integer bufferPeriod=0;// 产品缓冲周期
	
	private Integer commissionPcent;
	private Float fbaFee;
	
	private Float fbaFeeEu;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne()
	@JoinColumn(name = "product_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiProduct getProduct() {
		return product;
	}

	public void setProduct(PsiProduct product) {
		this.product = product;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getIsSale() {
		return isSale;
	}

	public void setIsSale(String isSale) {
		this.isSale = isSale;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getIsNew() {
		return isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}
	
	@Transient
	public String getIsMain() {
		return isMain;
	}

	@Transient
	public void setIsMain(String isMain) {
		this.isMain = isMain;
	}

	public String getAddedMonth() {
		return addedMonth;
	}

	public void setAddedMonth(String addedMonth) {
		this.addedMonth = addedMonth;
	}

	public String getSalesForecastScheme() {
		return salesForecastScheme;
	}

	public void setSalesForecastScheme(String salesForecastScheme) {
		this.salesForecastScheme = salesForecastScheme;
	}
	
	public Date getEliminateTime() {
		return eliminateTime;
	}

	public void setEliminateTime(Date eliminateTime) {
		this.eliminateTime = eliminateTime;
	}
	
	public String getOffWebsite() {
		return offWebsite;
	}

	public void setOffWebsite(String offWebsite) {
		this.offWebsite = offWebsite;
	}

	@Transient
	public String getColorName() {
		String name = getProductName();
		if (StringUtils.isNotEmpty(color)) {
			name = name + "_" + color;
		}
		return name;
	}

	public Float getPiPrice() {
		return piPrice;
	}

	public void setPiPrice(Float piPrice) {
		this.piPrice = piPrice;
	}

	public Float getCnpiPrice() {
		return cnpiPrice;
	}

	public void setCnpiPrice(Float cnpiPrice) {
		this.cnpiPrice = cnpiPrice;
	}

	public Integer getTransportType() {
		return transportType;
	}

	public void setTransportType(Integer transportType) {
		this.transportType = transportType;
	}

	public Integer getBufferPeriod() {
		return bufferPeriod;
	}

	public void setBufferPeriod(Integer bufferPeriod) {
		this.bufferPeriod = bufferPeriod;
	}

	public Integer getCommissionPcent() {
		return commissionPcent;
	}

	public void setCommissionPcent(Integer commissionPcent) {
		this.commissionPcent = commissionPcent;
	}

	public Float getFbaFee() {
		return fbaFee;
	}

	public void setFbaFee(Float fbaFee) {
		this.fbaFee = fbaFee;
	}

	public Float getFbaFeeEu() {
		return fbaFeeEu;
	}

	public void setFbaFeeEu(Float fbaFeeEu) {
		this.fbaFeeEu = fbaFeeEu;
	}
	

}
