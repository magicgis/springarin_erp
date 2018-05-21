/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;

/**
 * 采购订单明细Entity
 * @author Michael
 * @version 2014-10-29
 */
@Entity
@Table(name = "psi_purchase_order_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PurchaseOrderItem  implements Serializable,Comparable<PurchaseOrderItem>{
	private static final long serialVersionUID = 1L;
	private 	Integer           id; 		                     // id
	private 	BigDecimal        itemPrice;                     //单价
	private 	String            colorCode;                     //颜色编号
	private 	String            countryCode;                   //国家编号
	private 	Date              deliveryDate;                  //交货日期
	private 	String            remark;                        //备注
	private 	String            delFlag="0";                   //删除标记
	private 	PurchaseOrder     purchaseOrder;                 //订单
	private 	PsiProduct        product; 	                     //产品
	private 	Integer           quantityOrdered;               //订单数量
	private 	Integer           quantityReceived;              //已接收数量
	private 	Integer           quantityPreReceived;           //预接收数量
	private 	String            productName;                   //产品名字
	private 	Date              updateDate;                    //更新日期      
	private 	Date              actualDeliveryDate;            //实际交货日期
	private 	BigDecimal        paymentAmount ;                //已付款金额
	private 	Integer           quantityPayment ;              //已付款数量
	private 	BigDecimal        oldItemPrice;                  //老itemPrice（生产状态编辑，判断改没改价格）
	private 	Integer           quantityBalance;           	 //可收货数（非字段）
	private     Integer           forecastItemId;                //预测订单itemId
	private     String            forecastRemark;                //预测备注
	
	private 	Integer           quantityOffOrdered;            //线下订单数量
	private 	Integer           quantityOffReceived;           //线下已接收数量
	private 	Integer           quantityOffPreReceived;        //线下预接收数量
	private 	Integer           quantityOffBalance;            //线下可收货数（非字段）
	private     String            orderSta;
	private     String            itemIdStr;
	

	private 	Date              deliveryDateLog;               // 交期变更记录日期
	
	@Transient
	public String getItemIdStr() {
		return itemIdStr;
	}


	public void setItemIdStr(String itemIdStr) {
		this.itemIdStr = itemIdStr;
	}


	@Transient
	public String getOrderSta() {
		return orderSta;
	}


	public void setOrderSta(String orderSta) {
		this.orderSta = orderSta;
	}

	private List<PsiLadingBillItem>  billItemList = Lists.newArrayList();
	
	private List<PurchaseOrderDeliveryDate>  deliveryDateList = Lists.newArrayList();
	
	@Transient
	public List<String> getColorList() {
		if(StringUtils.isNotBlank(product.getColor())){
			return Arrays.asList(product.getColor().split(","));
		}else{
			return Lists.newArrayList();
		}
	}


	public Integer getQuantityPayment() {
		return quantityPayment;
	}

	public void setQuantityPayment(Integer quantityPayment) {
		this.quantityPayment = quantityPayment;
	}


	@Transient
	public BigDecimal getTotalAmount() {
		BigDecimal totalAmount = new BigDecimal("0");
		if(this.itemPrice!=null){
			totalAmount=this.itemPrice.multiply(new BigDecimal(this.quantityOrdered)).multiply(
					new BigDecimal((100-this.getPurchaseOrder().getDeposit())/100f));
		}
		return totalAmount;
	}

	//@OneToMany(mappedBy = "purchaseOrderItem",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@OneToMany(mappedBy = "purchaseOrderItem",fetch=FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<PsiLadingBillItem> getBillItemList() {
		return billItemList;
	}

	public void setBillItemList(List<PsiLadingBillItem> billItemList) {
		this.billItemList = billItemList;
	}

	@OneToMany(mappedBy = "orderItem",fetch=FetchType.EAGER)
	@Where(clause="del_flag=0")
	@OrderBy(value="deliveryDate")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<PurchaseOrderDeliveryDate> getDeliveryDateList() {
		return deliveryDateList;
	}

	public void setDeliveryDateList(List<PurchaseOrderDeliveryDate> deliveryDateList) {
		this.deliveryDateList = deliveryDateList;
	}


	@Transient
	public List<String> getCountryList() {
		return Arrays.asList(product.getPlatform().split(","));
	}

	
	
	@Transient
	public BigDecimal getOldItemPrice() {
		return oldItemPrice;
	}


	public void setOldItemPrice(BigDecimal oldItemPrice) {
		this.oldItemPrice = oldItemPrice;
	}


	public PurchaseOrderItem() {
		super();
	}

	public PurchaseOrderItem(String productName,String colorCode, String countryCode,
			 String remark,Integer quantityOrdered,Integer quantityReceived,Integer quantityPreReceived, Date deliveryDate, Date actualDeliveryDate) {
		super();
		this.colorCode = colorCode;
		this.countryCode = countryCode;
		this.deliveryDate = deliveryDate;
		this.remark = remark;
		this.quantityOrdered = quantityOrdered;
		this.quantityReceived = quantityReceived;
		this.productName = productName;
		this.actualDeliveryDate = actualDeliveryDate;
		this.quantityPreReceived=quantityPreReceived;
	}


	public PurchaseOrderItem(Integer id){
		this();
		this.id = id;
	}
	
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
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
	@JoinColumn(name="purchase_order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
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

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Integer getQuantityPreReceived() {
		return quantityPreReceived;
	}

	public void setQuantityPreReceived(Integer quantityPreReceived) {
		this.quantityPreReceived = quantityPreReceived;
	}



	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getQuantityOrdered() {
		return quantityOrdered;
	}

	public void setQuantityOrdered(Integer quantityOrdered) {
		this.quantityOrdered = quantityOrdered;
	}

	public Integer getQuantityReceived() {
		return quantityReceived;
	}

	public void setQuantityReceived(Integer quantityReceived) {
		this.quantityReceived = quantityReceived;
	}

	@Transient
	public Integer getQuantityUnReceived(){
		return this.quantityOrdered-this.quantityReceived;
	}
	
	@Transient
	public Integer getQuantityOffUnReceived(){
		return this.quantityOffOrdered-this.quantityOffReceived;
	}
	
	@Transient
	public Integer getQuantityCanReceived() {
		return this.quantityOrdered-this.quantityReceived-this.quantityPreReceived;
	}
	
	
	@Transient
	public Integer getQuantityOffCanReceived() {
		return this.quantityOffOrdered-this.quantityOffReceived-this.quantityOffPreReceived;
	}
	
	@Transient
	public Integer getQuantityBalance() {
		return quantityBalance;
	}


	public void setQuantityBalance(Integer quantityBalance) {
		this.quantityBalance = quantityBalance;
	}

	@Transient
	public Integer getQuantityOffBalance() {
		return quantityOffBalance;
	}


	public void setQuantityOffBalance(Integer quantityOffBalance) {
		this.quantityOffBalance = quantityOffBalance;
	}


	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
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
	
	@Transient
	public BigDecimal getDepositAmount() {
		BigDecimal totalAmount = new BigDecimal("0");
		if(this.itemPrice!=null){
			totalAmount=this.itemPrice.multiply(new BigDecimal(this.quantityOrdered)).multiply(new BigDecimal(this.getPurchaseOrder().getDeposit()/100f));
		}
		return totalAmount;
	}
	
	@Transient
	public BigDecimal getDepositPaymentAmount(){
		BigDecimal depositAmount=this.purchaseOrder.getDepositAmount();
		BigDecimal totalAmount = this.purchaseOrder.getTotalAmount();
		if(depositAmount.compareTo(BigDecimal.ZERO)>0){
			return itemPrice.multiply(new BigDecimal(quantityOrdered)).multiply(depositAmount.divide(totalAmount, 2, BigDecimal.ROUND_HALF_UP));
		}else{
			return BigDecimal.ZERO;
		}
	}
	
	@Transient
	public BigDecimal getAllPaymentAmount(){
			return  this.paymentAmount.add(getDepositPaymentAmount());
	}

	@Transient
	public String getCountry() {
		String rs = countryCode;
		if("com".equals(countryCode)){
			rs = "us";
		}
		return rs.toUpperCase();
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
	
	@Transient
	public String getBarcode() {
		for (PsiBarcode barcode : product.getBarcodes()) {
			if(colorCode.equals(barcode.getProductColor())&&countryCode.equals(barcode.getProductPlatform())){
				String bcode = barcode.getBarcode();
				bcode = null==bcode?"":bcode;
				return getCountry()+" "+bcode; 
			}
		}
		return ""; 
	}
	
	@Transient
	public String getBarcodeNoCountry() {
		for (PsiBarcode barcode : product.getBarcodes()) {
			if(colorCode.equals(barcode.getProductColor())&&countryCode.equals(barcode.getProductPlatform())){
				String bcode = barcode.getBarcode();
				bcode = null==bcode?"":bcode;
				return bcode; 
			}
		}
		return ""; 
	}
	
	@Transient
	public PsiBarcode getBarcodeInstans() {
		for (PsiBarcode barcode : product.getBarcodes()) {
			if(colorCode.equals(barcode.getProductColor())&&countryCode.equals(barcode.getProductPlatform())){
				return barcode;
			}
		}
		return null; 
	}
	
	@Transient
	public Float  getVolume(){
		if(product.getPackQuantity()>0){
			return this.product.getBoxVolume().floatValue()*(this.quantityOrdered/(float)product.getPackQuantity());
		}else{
			return 0f;
		}	
	}
	
	@Transient
	public Float  getWeight(){
		if(product.getPackQuantity()>0){
			return this.product.getGw().floatValue()*(this.quantityOrdered/(float)product.getPackQuantity());
		}else{
			return 0f;
		}	
	}
	
	
	/**
	 *欠货体积 
	 * 
	 */
	@Transient
	public Float  getLessCargoVolume(){
		if(product.getPackQuantity()>0){
			return this.product.getBoxVolume().floatValue()*((this.quantityOrdered-this.quantityReceived)/(float)product.getPackQuantity());
		}else{
			return 0f;
		}	
	}
	
	@Transient
	public Float  getLessCargoWeight(){
		if(product.getPackQuantity()>0){
			return this.product.getGw().floatValue()*((this.quantityOrdered-this.quantityReceived)/(float)product.getPackQuantity());
		}else{
			return 0f;
		}	
	}
	
	@Transient
	public Integer getPreAndReceivedQuantity(){
		return this.quantityReceived+this.quantityPreReceived;
	}

	@Transient
	public String getProductNameColor(){
		if(StringUtils.isNotEmpty(colorCode)){
			return productName+"_"+colorCode;
		}else{
			return productName;
		}
	}
	
	@Transient
	public String getProductIdColor(){
		if(StringUtils.isNotEmpty(colorCode)){
			return productName+","+colorCode;
		}else{
			return productName;
		}
	}

	public Date getActualDeliveryDate() {
		return actualDeliveryDate;
	}


	public void setActualDeliveryDate(Date actualDeliveryDate) {
		this.actualDeliveryDate = actualDeliveryDate;
	}
	
	private static Map<String,Integer> indexs ;
	
	static{
		indexs = Maps.newHashMap();
		indexs.put("de", 1);
		indexs.put("fr", 2);
		indexs.put("it", 3);
		indexs.put("es", 4);
		indexs.put("uk", 5);
		indexs.put("com", 6);
		indexs.put("com2", 7);
		indexs.put("com3", 8);
		indexs.put("ca", 9);
		indexs.put("jp", 10);
		indexs.put("mx", 11);
	}

	@Override
	public int compareTo(PurchaseOrderItem o) {
		if(o.getColorCode().compareTo(colorCode)==0&&o.getProductName().compareTo(productName)==0){
				return -indexs.get(o.getCountryCode()).compareTo(indexs.get(countryCode));
		}
		return 0;
	}

   @Override public boolean equals(Object obj) {
	   return super.equals(obj);
   }

   @Override public int hashCode() {
	   return super.hashCode();
   }


	@Column(updatable=false)
	public Integer getForecastItemId() {
		return forecastItemId;
	}


	public void setForecastItemId(Integer forecastItemId) {
		this.forecastItemId = forecastItemId;
	}


	public String getForecastRemark() {
		return forecastRemark;
	}


	public void setForecastRemark(String forecastRemark) {
		this.forecastRemark = forecastRemark;
	}


	public Integer getQuantityOffOrdered() {
		return quantityOffOrdered;
	}


	public void setQuantityOffOrdered(Integer quantityOffOrdered) {
		this.quantityOffOrdered = quantityOffOrdered;
	}


	public Integer getQuantityOffReceived() {
		return quantityOffReceived;
	}


	public void setQuantityOffReceived(Integer quantityOffReceived) {
		this.quantityOffReceived = quantityOffReceived;
	}


	//可收货线下数量
	@Transient
	public Integer getCanLadingOffQuantity(){
		return this.quantityOffOrdered-this.quantityOffPreReceived-this.quantityOffReceived;
	}
	
	public Integer getQuantityOffPreReceived() {
		return quantityOffPreReceived;
	}


	public void setQuantityOffPreReceived(Integer quantityOffPreReceived) {
		this.quantityOffPreReceived = quantityOffPreReceived;
	}


	public Date getDeliveryDateLog() {
		return deliveryDateLog;
	}


	public void setDeliveryDateLog(Date deliveryDateLog) {
		this.deliveryDateLog = deliveryDateLog;
	}
	
	
}


