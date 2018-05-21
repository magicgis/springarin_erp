package com.springrain.erp.modules.amazoninfo.entity.order;

import java.math.BigDecimal;

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

@Entity
@Table(name = "amazoninfo_removal_orderitem")
public class AmazonRemovalOrderItem{

	private Integer id ;
	
    private String sellersku;
    
    private String fnsku;
    
    private String productName;
    
    private Integer requestedQty;
    
    private Integer completedQty;
    
    private Integer cancelledQty;
    
    private Integer inProcessQty;

    private String disposition;
    
    private BigDecimal removalFee;
    
    private AmazonRemovalOrder order;
    
    private Integer storedQty = 0;
    
    private Float avgFreight;	//运费(欧元)
    
    private Float buyCost;	//采购价(欧元)
    
    private String currency;
    
    private String accountName;
    /**
     * 已到货数量,针对美国大批量召回做特殊处理,到货后方可入库
     * 系统从物流信息确认是否已到货,ups外的物流需手动确认
     */
    private Integer deliveredQty = 0;
    
    
  //非数据库字段,仅做前台数据传输
    private String qualityType;
    private String colorCode;
    private String countryCode;

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

	
	public String getSellersku() {
		return sellersku;
	}

	public void setSellersku(String sellersku) {
		this.sellersku = sellersku;
	}

	public String getFnsku() {
		return fnsku;
	}

	public void setFnsku(String fnsku) {
		this.fnsku = fnsku;
	}

	public Integer getRequestedQty() {
		return requestedQty;
	}

	public void setRequestedQty(Integer requestedQty) {
		this.requestedQty = requestedQty;
	}

	public Integer getCompletedQty() {
		return completedQty;
	}

	public void setCompletedQty(Integer completedQty) {
		this.completedQty = completedQty;
	}

	public Integer getCancelledQty() {
		return cancelledQty;
	}

	public void setCancelledQty(Integer cancelledQty) {
		this.cancelledQty = cancelledQty;
	}

	public Integer getInProcessQty() {
		return inProcessQty;
	}

	public void setInProcessQty(Integer inProcessQty) {
		this.inProcessQty = inProcessQty;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
	
	public BigDecimal getRemovalFee() {
		return removalFee;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setRemovalFee(BigDecimal removalFee) {
		this.removalFee = removalFee;
	}

	@ManyToOne()
	@JoinColumn(name="order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonRemovalOrder getOrder() {
		return order;
	}
	
	
	

	public void setOrder(AmazonRemovalOrder order) {
		this.order = order;
	}
	
	public AmazonRemovalOrderItem(){
		
	}

	public AmazonRemovalOrderItem(String sellersku, String fnsku,
			String productName, Integer requestedQty, Integer completedQty,
			Integer cancelledQty, Integer inProcessQty, String disposition,
			BigDecimal removalFee, String currency,AmazonRemovalOrder order) {
		super();
		this.sellersku = sellersku;
		this.fnsku = fnsku;
		this.productName = productName;
		this.requestedQty = requestedQty;
		this.completedQty = completedQty;
		this.cancelledQty = cancelledQty;
		this.inProcessQty = inProcessQty;
		this.disposition = disposition;
		this.removalFee = removalFee;
		this.currency = currency;
		this.order = order;
		this.order.getItems().add(this);
	}

	public Integer getStoredQty() {
		return storedQty;
	}

	public void setStoredQty(Integer storedQty) {
		this.storedQty = storedQty;
	}

	public Float getAvgFreight() {
		return avgFreight;
	}

	public void setAvgFreight(Float avgFreight) {
		this.avgFreight = avgFreight;
	}

	public Float getBuyCost() {
		return buyCost;
	}

	public void setBuyCost(Float buyCost) {
		this.buyCost = buyCost;
	}

	@Transient
	public String getQualityType() {
		return qualityType;
	}

	public void setQualityType(String qualityType) {
		this.qualityType = qualityType;
	}

	@Transient
	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	@Transient
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getDeliveredQty() {
		return deliveredQty;
	}

	public void setDeliveredQty(Integer deliveredQty) {
		this.deliveredQty = deliveredQty;
	}

	@Transient
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	
}
