package com.springrain.erp.modules.amazoninfo.entity.customer;

import java.util.Date;

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
@Table(name = "amazoninfo_buy_comment")
public class AmazonBuyComment{

	private Integer id ;
	
    private Date createDate;
    
    private Date typeDate;
    
    private String type; 
    
    private String orderId;
    
    private String asin;
    
    private String sku;
    
    private String productName;
    
    private int quantity;
    
    private int itemId;
    
    private String remark;
    
    private float money;
    
    private String customerId;
    
    private AmazonCustomer customer;
    
    public AmazonBuyComment() {}
    
    public AmazonBuyComment(Date createDate, Date typeDate, String type,
			String orderId, String asin, String sku, String productName,
			int quantity, int itemId,String remark, AmazonCustomer customer) {
		super();
		this.createDate = createDate;
		this.typeDate = typeDate;
		this.type = type;
		this.orderId = orderId;
		this.asin = asin;
		this.sku = sku;
		this.productName = productName;
		this.quantity = quantity;
		this.itemId = itemId;
		this.customer = customer;
		this.remark = remark;
		this.customerId = customer.getCustomerId();
	}

    public AmazonBuyComment(Date createDate, Date typeDate, String type,
			String orderId, String asin, String sku, String productName,
			float money, int itemId,String remark, AmazonCustomer customer) {
		super();
		this.createDate = createDate;
		this.typeDate = typeDate;
		this.type = type;
		this.orderId = orderId;
		this.asin = asin;
		this.sku = sku;
		this.productName = productName;
		this.money = money;
		this.itemId = itemId;
		this.customer = customer;
		this.remark = remark;
		this.customerId = customer.getCustomerId();
	}
    
	@Id
   	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getTypeDate() {
		return typeDate;
	}

	public void setTypeDate(Date typeDate) {
		this.typeDate = typeDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@ManyToOne()
	@JoinColumn(name="amz_email")
	@NotFound(action = NotFoundAction.IGNORE)	
	public AmazonCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(AmazonCustomer customer) {
		this.customer = customer;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Float getMoney() {
		return money;
	}

	public void setMoney(Float money) {
		this.money = money;
	}
	
	
}
