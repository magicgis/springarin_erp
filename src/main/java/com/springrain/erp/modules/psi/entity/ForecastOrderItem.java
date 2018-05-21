/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;

import javax.persistence.Column;
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
 * 预测订单明细Entity
 * @author Michael
 * @version 2016-02-26
 */
@Entity
@Table(name = "psi_forecast_order_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class ForecastOrderItem  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private 	Integer           id; 		                     // id
	private 	PsiProduct        product; 	                     // 产品
	private 	String            colorCode;                     // 颜色编号
	private 	String            countryCode;                   // 国家编号
	private		PsiSupplier       supplier;                      // 供应商
	private 	Integer           forecast1week;                 // 预测当前下单量
	private 	Integer           forecast2week;                 // 预测第二周下单量
	private 	Integer           forecast3week;                 // 预测第三周下单量
	private 	Integer           forecast4week;                 // 预测第四周下单量
	private 	Integer           quantity;                      // 审核下单量
	private 	Integer           promotionQuantity;             // 促销数量
	private 	Integer           promotionBossQuantity;         // 终极促销数量(2017-06-13改为广告下单数量)
	private 	String            remark;                        // 备注
	private 	String            reviewRemark;                  // 审核备注
	private     String            by31sales;                     // 是否依据31天的销量推测下单
	private     String            byWeek;                        // 用哪一周数据
	private     String            tips;                          // 提示字段
	private 	ForecastOrder     forecastOrder;                 // 订单
	private     String            productName;                   // 产品名称
	
	
	private     Integer           totalStock;                    // 总库存数
	private     Integer           safeStock;                     // 安全库存数
    private     Integer           period;                        // 生产运输缓冲周期
	private 	Integer           forecast1month;                // 第一月销售预测
	private 	Integer           forecast2month;                // 第二月销售预测
	private 	Integer           forecast3month;                // 第三月销售预测
	private 	Integer           forecast4month;                // 第四月销售预测
	private 	Integer           day31sales;                    // 31天销量
	private     String            lastOrderWeek;                 // 上次下单周
	
	private     Integer           saleQuantity;                  // 销售数量
	private     Integer           reviewQuantity;                // 审核数量
	private     Integer           bossQuantity;                  // 终极审核数量
	private 	String            bossRemark;                    // 终极审核备注
	
	private     Integer           purchaseQuantity;              // 采购改动后数量
	private     String            displaySta;                    // 预测显示为0  预测不显示为1   新增显示为2
	private     Integer           periodBuffer;                  // 缓冲周期
	
	private     Integer           maxStock;                      // 最大库存
	private 	Integer           realDay31sales;                // 去营销数后的31日销
	private     String            isMain;                    	 // 主力
	private     String            isNew;                    	 // 新品
	private     String            priceChange;                   // 改价超20%提示
	
	
	public ForecastOrderItem() {
		super();
	}

	public ForecastOrderItem(Integer id){
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
	public ForecastOrder getForecastOrder() {
		return forecastOrder;
	}

	public void setForecastOrder(ForecastOrder forecastOrder) {
		this.forecastOrder = forecastOrder;
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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

	public Integer getPromotionQuantity() {
		return promotionQuantity;
	}

	public void setPromotionQuantity(Integer promotionQuantity) {
		this.promotionQuantity = promotionQuantity;
	}

	public Integer getForecast1week() {
		return forecast1week;
	}

	public void setForecast1week(Integer forecast1week) {
		this.forecast1week = forecast1week;
	}

	public Integer getForecast2week() {
		return forecast2week;
	}

	public void setForecast2week(Integer forecast2week) {
		this.forecast2week = forecast2week;
	}

	public Integer getForecast3week() {
		return forecast3week;
	}

	public void setForecast3week(Integer forecast3week) {
		this.forecast3week = forecast3week;
	}

	public Integer getForecast4week() {
		return forecast4week;
	}

	public void setForecast4week(Integer forecast4week) {
		this.forecast4week = forecast4week;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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

	@Column(updatable=false)
	public String getBy31sales() {
		return by31sales;
	}

	public void setBy31sales(String by31sales) {
		this.by31sales = by31sales;
	}

	public String getProductName() {
		return productName;
	}

	@Transient
	public String getProductNameColor(){
		String productNameColor = productName;
		if(StringUtils.isNotEmpty(colorCode)){
			productNameColor=productNameColor+"_"+colorCode;
		}
		return productNameColor;
	}
	
	@Transient
	public String getConKey(){
		String productNameColor = productName;
		if(StringUtils.isNotEmpty(colorCode)){
			productNameColor=productNameColor+"_"+colorCode;
		}
		return productNameColor+"_"+countryCode;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	@Transient
	public String getProductNoBank(){
		String productName = getProductNameColor();
		Integer i = productName.split(" ")[0].length()+1;
		return productName.substring(i);
	}

	@Column(updatable=false)
	public String getByWeek() {
		return byWeek;
	}

	public void setByWeek(String byWeek) {
		this.byWeek = byWeek;
	}

	@Column(updatable=false)
	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public Integer getMaxStock() {
		return maxStock;
	}

	public void setMaxStock(Integer maxStock) {
		this.maxStock = maxStock;
	}

	public Integer getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(Integer totalStock) {
		this.totalStock = totalStock;
	}

	public Integer getSafeStock() {
		return safeStock;
	}

	public void setSafeStock(Integer safeStock) {
		this.safeStock = safeStock;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public Integer getForecast1month() {
		return forecast1month;
	}

	public void setForecast1month(Integer forecast1month) {
		this.forecast1month = forecast1month;
	}

	public Integer getForecast2month() {
		return forecast2month;
	}

	public void setForecast2month(Integer forecast2month) {
		this.forecast2month = forecast2month;
	}

	public Integer getForecast3month() {
		return forecast3month;
	}

	public void setForecast3month(Integer forecast3month) {
		this.forecast3month = forecast3month;
	}

	public Integer getForecast4month() {
		return forecast4month;
	}

	public void setForecast4month(Integer forecast4month) {
		this.forecast4month = forecast4month;
	}

	public Integer getDay31sales() {
		return day31sales;
	}

	public void setDay31sales(Integer day31sales) {
		this.day31sales = day31sales;
	}

	
	public String getLastOrderWeek() {
		return lastOrderWeek;
	}

	public void setLastOrderWeek(String lastOrderWeek) {
		this.lastOrderWeek = lastOrderWeek;
	}

	public Integer getSaleQuantity() {
		return saleQuantity;
	}

	public void setSaleQuantity(Integer saleQuantity) {
		this.saleQuantity = saleQuantity;
	}

	public Integer getReviewQuantity() {
		return reviewQuantity;
	}

	public void setReviewQuantity(Integer reviewQuantity) {
		this.reviewQuantity = reviewQuantity;
	}

	public Integer getBossQuantity() {
		return bossQuantity;
	}

	public void setBossQuantity(Integer bossQuantity) {
		this.bossQuantity = bossQuantity;
	}

	public String getBossRemark() {
		return bossRemark;
	}

	public void setBossRemark(String bossRemark) {
		this.bossRemark = bossRemark;
	}
	
	public Integer getPurchaseQuantity() {
		return purchaseQuantity;
	}

	public void setPurchaseQuantity(Integer purchaseQuantity) {
		this.purchaseQuantity = purchaseQuantity;
	}

	public String getDisplaySta() {
		return displaySta;
	}

	public void setDisplaySta(String displaySta) {
		this.displaySta = displaySta;
	}

	public Integer getPeriodBuffer() {
		return periodBuffer;
	}

	public void setPeriodBuffer(Integer periodBuffer) {
		this.periodBuffer = periodBuffer;
	}
	
	public Integer getRealDay31sales() {
		return realDay31sales;
	}

	public void setRealDay31sales(Integer realDay31sales) {
		this.realDay31sales = realDay31sales;
	}

	public String getIsMain() {
		return isMain;
	}

	public void setIsMain(String isMain) {
		this.isMain = isMain;
	}

	public String getIsNew() {
		return isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	public String getPriceChange() {
		return priceChange;
	}

	public void setPriceChange(String priceChange) {
		this.priceChange = priceChange;
	}

	@Transient
	public Integer getPackQuantity(){
		Integer pack =this.product.getPackQuantity();
		Integer productId = this.product.getId();
		if(productId.equals(217)){
			if("com,uk,jp,ca,mx,".contains(countryCode+",")){
				pack=60;
			}else{
				pack=44;
			}
		}else if(productId.equals(218)){
			if("com,jp,ca,mx,".contains(countryCode+",")){
				pack=32;
			}else{
				pack=24;
			}
		}
		return pack;
	}
	
	@Transient
	public String getProductIdColor(){
		if(StringUtils.isNotEmpty(product.getColor())){
			return product.getId()+"_"+product.getColor();
		}else{
			return product.getId()+"";
		}
	}
	
	@Transient
	public String getDay31SalesPercent(){
		String rs = "";
		if (day31sales > 0 && realDay31sales > 0) {
			double percent = (day31sales - realDay31sales)*100/day31sales.doubleValue();
			rs = String.format("%.2f", percent)+"%";
		}
		return rs;
	}

	public Integer getPromotionBossQuantity() {
		return promotionBossQuantity;
	}

	public void setPromotionBossQuantity(Integer promotionBossQuantity) {
		this.promotionBossQuantity = promotionBossQuantity;
	}

	public String toJson(){
		return "{\"forecast1week\":\""+forecast1week+"\",\"forecast2week\":\""+this.forecast2week+"\",\"forecast3week\":\""+this.forecast3week+"\",\"forecast4week\":\""+this.forecast4week+"\",\"quantity\":\""+this.quantity+"\",\"period\":\""+this.period+"\",\"day31sales\":\""+this.day31sales+"\"}";
	}
	
	
}


