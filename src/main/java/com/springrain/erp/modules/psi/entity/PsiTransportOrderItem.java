/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.util.Arrays;
import java.util.List;

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

import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.StringUtils;

/**
 * 运单明细表Entity
 * @author Michael
 * @version 2015-01-15
 */
@Entity
@Table(name = "psi_transport_order_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiTransportOrderItem implements Comparable<PsiTransportOrderItem>{
	
	private 	Integer  			id; 				// 编号
	private     PsiProduct     		product;  		    // 产品
	private     String      		productName;        // 产品名字,
	private     String      		colorCode;          // 颜色编号,
	private     String      		countryCode;        // 国家编号,
	private     Integer     		quantity;           // 数量,
	private     Integer             shippedQuantity;    // 发货数量
	private     Integer             receiveQuantity;    // 接收数量,
	private     Float       		itemPrice;          // 提高10%单价,
	private     String              currency;           // 货币类型，
	private     String      		delFlag="0";        // 删除标记,
	private     String      		remark;             // 备注, 
	private     Integer             packQuantity;       // 装箱数
	private     String              sku;                // sku
	private     String      		offlineSta="0";     // 0：线上,1：线下
	
	private     PsiTransportOrder   transportOrder;     //运单,
	private     Float  productPrice;
	private     Float  cnPrice;
	
	private String hsCode;
    private String cartonNo;//箱号
    private Float tempUsdPrice;
	private Integer chdQuantity; 	//拆单数量(非数据库字段)
	private String fbaFlag="0"; 	//是否已经创建FBA贴 0：否  1：是
	private String isFba="0"; 	//(非数据库字段),分开建贴时标记此次是否创建FBA贴 0：否  1：是
	private Integer fbaInboundId; 	//psi_fba_inbound ID,取消fba贴时用到
    
	public Float getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Float productPrice) {
		this.productPrice = productPrice;
	}


	@Transient   
    public Float getTempUsdPrice() {
		return tempUsdPrice;
	}

	public void setTempUsdPrice(Float tempUsdPrice) {
		this.tempUsdPrice = tempUsdPrice;
	}

	@Transient
    public String getCartonNo() {
		return cartonNo;
	}

	public void setCartonNo(String cartonNo) {
		this.cartonNo = cartonNo;
	}

	@Transient
	public String getHsCode() {
		return hsCode;
	}

	public void setHsCode(String hsCode) {
		this.hsCode = hsCode;
	}

	public PsiTransportOrderItem() {
		super();
	}

	public PsiTransportOrderItem(Integer id){
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

	@Transient
	public List<String> getColorList() {
		if(StringUtils.isNotBlank(product.getColor())){
			return Arrays.asList(product.getColor().split(","));
		}else{
			return Lists.newArrayList();
		}
	}
	
	@Transient
	public List<String> getCountryList() {
		return Arrays.asList(product.getPlatform().split(","));
	}
	
	public Integer getPackQuantity() {
		return packQuantity;
	}

	public void setPackQuantity(Integer packQuantity) {
		this.packQuantity = packQuantity;
	}

	public Integer getShippedQuantity() {
		return shippedQuantity;
	}

	public void setShippedQuantity(Integer shippedQuantity) {
		this.shippedQuantity = shippedQuantity;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@ManyToOne()
	@JoinColumn(name="transport_order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiTransportOrder getTransportOrder() {
		return transportOrder;
	}

	public void setTransportOrder(PsiTransportOrder transportOrder) {
		this.transportOrder = transportOrder;
	}

	public Integer getReceiveQuantity() {
		return receiveQuantity;
	}

	public void setReceiveQuantity(Integer receiveQuantity) {
		this.receiveQuantity = receiveQuantity;
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Float getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(Float itemPrice) {
		this.itemPrice = itemPrice;
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

	
	public String getOfflineSta() {
		return offlineSta;
	}

	public void setOfflineSta(String offlineSta) {
		this.offlineSta = offlineSta;
	}

	public Float getCnPrice() {
		return cnPrice;
	}

	public void setCnPrice(Float cnPrice) {
		this.cnPrice = cnPrice;
	}

	public String toString(){
		Integer boxNum = (int)Math.floor((this.receiveQuantity==null?this.quantity:this.receiveQuantity)/(float)this.packQuantity);
		return "{\"productId\":\""+this.product.getId()+"\",\"productName\":\""+this.productName+"\",\"remark\":\""+(this.remark!=null?this.remark:"")+"\",\"countryCode\":\""+this.countryCode+"\",\"colorCode\":\""+this.colorCode+"\",\"quantity\":\""+this.quantity+"\",\"sku\":\""+this.sku+"\",\"shippedQuantity\":\""+this.shippedQuantity+"\",\"receiveQuantity\":\""+this.receiveQuantity+"\",\"packQuantity\":\""+this.packQuantity+"\",\"offline\":\""+this.offlineSta+"\",\"boxNum\":\""+boxNum+"\"}";
	}

	@Override
	public int compareTo(PsiTransportOrderItem orderItem) {
		// TODO Auto-generated method stub
		return this.product.getChineseName().compareTo(orderItem.getProduct().getChineseName());
	}
	
	 @Override public boolean equals(Object obj) {
		   return super.equals(obj);
	   }

	   @Override public int hashCode() {
		   return super.hashCode();
	   }
	   
	@Transient
	public String getNameWithColor() {
		if(StringUtils.isNotBlank(colorCode)){
			return productName+"_"+colorCode;
		}
		return productName;
	}

	@Transient
	public Integer getChdQuantity() {
		return chdQuantity;
	}

	public void setChdQuantity(Integer chdQuantity) {
		this.chdQuantity = chdQuantity;
	}
	
	public String getFbaFlag() {
		return fbaFlag;
	}

	public void setFbaFlag(String fbaFlag) {
		this.fbaFlag = fbaFlag;
	}

	@Transient
	public String getIsFba() {
		return isFba;
	}

	public void setIsFba(String isFba) {
		this.isFba = isFba;
	}

	public Integer getFbaInboundId() {
		return fbaInboundId;
	}

	public void setFbaInboundId(Integer fbaInboundId) {
		this.fbaInboundId = fbaInboundId;
	}

	public PsiTransportOrderItem(PsiTransportOrder transportOrder,PsiProduct product,
			String productName, String colorCode, String countryCode,
			Integer quantity, Integer shippedQuantity, Integer receiveQuantity,
			Float itemPrice, String currency, String delFlag, String remark,
			Integer packQuantity, String sku, String offlineSta,
			Float productPrice,	Float cnPrice,String fbaFlag,Integer fbaInboundId) {
		super();
		this.product = product;
		this.productName = productName;
		this.colorCode = colorCode;
		this.countryCode = countryCode;
		this.quantity = quantity;
		this.shippedQuantity = shippedQuantity;
		this.receiveQuantity = receiveQuantity;
		this.itemPrice = itemPrice;
		this.currency = currency;
		this.delFlag = delFlag;
		this.remark = remark;
		this.packQuantity = packQuantity;
		this.sku = sku;
		this.offlineSta = offlineSta;
		this.transportOrder = transportOrder;
		this.productPrice = productPrice;
		this.cnPrice = cnPrice;
		this.fbaFlag=fbaFlag;
		this.fbaInboundId=fbaInboundId;
	}
	
	@Transient
	public String getProductColorCountry(){
		if(StringUtils.isEmpty(colorCode)){
			return productName+"_"+countryCode;
		}else{
			return productName+"_"+colorCode+"_"+countryCode;
		}
	}
	
	
}


