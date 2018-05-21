/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.StringUtils;

/**
 * 采购订单明细Entity
 * @author Michael
 * @version 2014-10-29
 */
@Entity
@Table(name = "psi_his_purchase_order_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class HisPurchaseOrderItem  implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer           id; 		                     // id
	private BigDecimal             itemPrice;                     //单价
	private String            colorCode;                     //颜色编号
	private String            countryCode;                   //国家编号
	private Date              deliveryDate;                  //交货日期
	private String            remark;                        //备注
	private String            delFlag;                       //删除标记
	private PsiProduct        product; 	                     //产品
	private Integer           quantityOrdered;               //订单数量
	private Integer           quantityReceived;              //已接收数量
	private Integer           quantityPreReceived;           //预接收数量
	private String            productName;                   //产品名字
	private Date              updateDate;                    //更新日期      
	private BigDecimal             paymentAmount ;                //已付款金额
	private Integer           quantityPayment ;              //已付款数量
	
	private HisPurchaseOrder  hisPurchaseOrder;              //订单
	private Date             actualDeliveryDate;
	
	@Transient
	public List<String> getColorList() {
		if(StringUtils.isNotBlank(product.getColor())){
			return Arrays.asList(product.getColor().split(","));
		}else{
			return Lists.newArrayList();
		}
	}

	@Transient
	public Date getActualDeliveryDate() {
		return actualDeliveryDate;
	}


	public void setActualDeliveryDate(Date actualDeliveryDate) {
		this.actualDeliveryDate = actualDeliveryDate;
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


	@Transient
	public List<String> getCountryList() {
		return Arrays.asList(product.getPlatform().split(","));
	}


	public HisPurchaseOrderItem() {
		super();
	}

	public HisPurchaseOrderItem(Integer id){
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

	@ManyToOne(optional=true)
	@JoinColumn(name="purchase_order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public HisPurchaseOrder getPurchaseOrder() {
		return hisPurchaseOrder;
	}

	public void setPurchaseOrder(HisPurchaseOrder hisPurchaseOrder) {
		this.hisPurchaseOrder = hisPurchaseOrder;
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
		Float productV=0f;
		if(this.product.getHeight()!=null&&this.product.getWidth()!=null&&this.product.getLength()!=null){
			productV = this.product.getHeight().floatValue()*this.product.getWidth().floatValue()*this.product.getLength().floatValue();
			productV = productV*this.quantityOrdered/1000000f;
		}
		return productV;
	}
	
	@Transient
	public Float  getWeight(){
		Float productW=0f;
		if(this.product.getWeight()!=null){
			productW=this.product.getWeight().floatValue()*this.quantityOrdered/1000f;
		}
		return productW;
	}
	
	
	/**
	 *欠货体积 
	 * 
	 */
	@Transient
	public Float  getLessCargoVolume(){
		Float productV=0f;
		if(this.product.getHeight()!=null&&this.product.getWidth()!=null&&this.product.getLength()!=null){
			productV = this.product.getHeight().floatValue()*this.product.getWidth().floatValue()*this.product.getLength().floatValue();
			productV = productV*(this.quantityOrdered-this.quantityReceived)/1000000f;
		}
		return productV;
	}
	
	@Transient
	public Float  getLessCargoWeight(){
		Float productW=0f;
		if(this.product.getWeight()!=null){
			productW=this.product.getWeight().floatValue()*(this.quantityOrdered-this.quantityReceived)/1000f;
		}
		return productW;
	}

	public HisPurchaseOrderItem(PurchaseOrderItem orderItem,HisPurchaseOrder order){
			this.colorCode      	 = orderItem.getColorCode();
			this.countryCode    	 = orderItem.getCountryCode();
			this.delFlag       		 = orderItem.getDelFlag();
			this.deliveryDate  		 = orderItem.getDeliveryDate();
			this.itemPrice      	 = orderItem.getItemPrice();
			this.paymentAmount  	 = orderItem.getPaymentAmount();
			this.product       		 = orderItem.getProduct();
			this.productName   		 = orderItem.getProductName();
			this.quantityOrdered 	 = orderItem.getQuantityOrdered();
			this.quantityPayment 	 = orderItem.getQuantityPayment();
			this.quantityPreReceived = orderItem.getQuantityPreReceived();
			this.quantityReceived    = orderItem.getQuantityReceived();
			this.remark              = orderItem.getRemark();
			this.updateDate          = orderItem.getUpdateDate();
			this.actualDeliveryDate  = orderItem.getActualDeliveryDate();
			this.hisPurchaseOrder    = order;
		}
	
	public HisPurchaseOrderItem(String productName,String colorCode, String countryCode,
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
	
	@Transient
	public Integer getQuantityCanReceived() {
		return this.quantityOrdered-this.quantityReceived-this.quantityPreReceived;
	}
	
}


