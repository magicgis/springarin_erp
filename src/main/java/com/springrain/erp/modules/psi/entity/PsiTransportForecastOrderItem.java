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

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.common.utils.StringUtils;


@Entity
@Table(name = "psi_forecast_transport_order_item")
public class PsiTransportForecastOrderItem  implements Serializable{
	private static final long serialVersionUID = 1L;
	private 	Integer           id; 		                     // id
	private 	String            colorCode;                     // 颜色编号
	private 	String            countryCode;                   // 国家编号
	private 	String            remark;                        // 备注
	private 	String            reviewRemark;                  // 审核备注
	private     Integer           safeStock;
	private     Integer           amazonStock;
	private     Integer           day31sales;
	private     Integer           quantity;
	private     Integer           checkQuantity;
	private     String            model;
	private     String            transportType;
	private 	PsiTransportForecastOrder     psiTransportForecastOrder;                 // 订单
	private     String            displaySta;                    // 预测显示为0  预测不显示为1   新增显示为2
    private     Integer           boxNum;
	private     String   productName;
	private     String   detail;
	private     String   sku;
	private     Integer  totalStock;
	private     Integer  salesDay;
	private     Integer  gap;
	private     Integer  poStock;
	private     Integer  transStock;
	private     Integer  overseaStock;
	
	private     Integer  boxQuantity;
	private     Float    volume;
	private     Float    weight;
	
	private     Integer  airQuantity;
	private     Integer  expQuantity;
	private     Integer  totalAir;
	private     Integer  totalExp;
	
	private     Integer  actualQuantity;
	private     String   transSta;//0:springrain 1:lc运单
	
	
	public String getTransSta() {
		return transSta;
	}

	public void setTransSta(String transSta) {
		this.transSta = transSta;
	}

	@Transient
	public Integer getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(Integer actualQuantity) {
		this.actualQuantity = actualQuantity;
	}

	public Integer getTotalAir() {
		return totalAir;
	}

	public void setTotalAir(Integer totalAir) {
		this.totalAir = totalAir;
	}

	public Integer getTotalExp() {
		return totalExp;
	}

	public void setTotalExp(Integer totalExp) {
		this.totalExp = totalExp;
	}

	public Integer getAirQuantity() {
		return airQuantity;
	}

	public void setAirQuantity(Integer airQuantity) {
		this.airQuantity = airQuantity;
	}

	public Integer getExpQuantity() {
		return expQuantity;
	}

	public void setExpQuantity(Integer expQuantity) {
		this.expQuantity = expQuantity;
	}

	@Transient
	public Integer getBoxQuantity() {
		return boxQuantity;
	}

	public void setBoxQuantity(Integer boxQuantity) {
		this.boxQuantity = boxQuantity;
	}

	@Transient
	public Float getVolume() {
		return volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}

	@Transient
	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Integer getOverseaStock() {
		return overseaStock;
	}

	public void setOverseaStock(Integer overseaStock) {
		this.overseaStock = overseaStock;
	}

	public Integer getTransStock() {
		return transStock;
	}

	public void setTransStock(Integer transStock) {
		this.transStock = transStock;
	}

	public Integer getGap() {
		return gap;
	}

	public void setGap(Integer gap) {
		this.gap = gap;
	}

	public Integer getPoStock() {
		return poStock;
	}

	public void setPoStock(Integer poStock) {
		this.poStock = poStock;
	}

	public Integer getSalesDay() {
		return salesDay;
	}

	public void setSalesDay(Integer salesDay) {
		this.salesDay = salesDay;
	}

	
	public Integer getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(Integer totalStock) {
		this.totalStock = totalStock;
	}

	public PsiTransportForecastOrderItem(Integer id, String colorCode,
			String countryCode, String remark, String reviewRemark,
			Integer safeStock, Integer amazonStock, Integer day31sales,
			Integer quantity, Integer checkQuantity, String model,
			String transportType, String displaySta, Integer boxNum,
			String productName,String sku) {
		super();
		this.id = id;
		this.colorCode = colorCode;
		this.countryCode = countryCode;
		this.remark = remark;
		this.reviewRemark = reviewRemark;
		this.safeStock = safeStock;
		this.amazonStock = amazonStock;
		this.day31sales = day31sales;
		this.quantity = quantity;
		this.checkQuantity = checkQuantity;
		this.model = model;
		this.transportType = transportType;
		this.displaySta = displaySta;
		this.boxNum = boxNum;
		this.productName = productName;
		this.sku = sku;
	}

	@Transient
	public String getTransportTypeName(){
		if("0".equals(transportType)){
			return "本地运输";
		}else{
			return "FBA运输";
		}
	}
	
	@Transient
	public String getModelName(){
		if("0".equals(model)){
			return "空运";
		}else if("1".equals(model)){
			return "海运";
		}else if("3".equals(model)){
			return "铁路";
		}else{
			return "快递";
		}
	}
	
	
	@Transient
	public String getProductNameColor(){
		String productNameColor = productName;
		if(StringUtils.isNotEmpty(colorCode)){
			productNameColor=productNameColor+"_"+colorCode;
		}
		return productNameColor;
	}
	
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getBoxNum() {
		return boxNum;
	}

	public void setBoxNum(Integer boxNum) {
		this.boxNum = boxNum;
	}

	public Integer getSafeStock() {
		return safeStock;
	}

	public void setSafeStock(Integer safeStock) {
		this.safeStock = safeStock;
	}

	public Integer getAmazonStock() {
		return amazonStock;
	}

	public void setAmazonStock(Integer amazonStock) {
		this.amazonStock = amazonStock;
	}

	public Integer getDay31sales() {
		return day31sales;
	}

	public void setDay31sales(Integer day31sales) {
		this.day31sales = day31sales;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getCheckQuantity() {
		return checkQuantity;
	}

	public void setCheckQuantity(Integer checkQuantity) {
		this.checkQuantity = checkQuantity;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}

	public PsiTransportForecastOrderItem() {
		super();
	}

	public PsiTransportForecastOrderItem(Integer id){
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

	@ManyToOne()
	@JoinColumn(name="forecast_order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiTransportForecastOrder getPsiTransportForecastOrder() {
		return psiTransportForecastOrder;
	}

	public void setPsiTransportForecastOrder(PsiTransportForecastOrder psiTransportForecastOrder) {
		this.psiTransportForecastOrder = psiTransportForecastOrder;
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

	public String getReviewRemark() {
		return reviewRemark;
	}

	public void setReviewRemark(String reviewRemark) {
		this.reviewRemark = reviewRemark;
	}

	
	public String getDisplaySta() {
		return displaySta;
	}

	public void setDisplaySta(String displaySta) {
		this.displaySta = displaySta;
	}

	
	
}


