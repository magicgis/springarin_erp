package com.springrain.erp.modules.amazoninfo.entity.order;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


@Entity
@Table(name = "amazoninfo_outbound_orderitem")
public class AmazonOutboundOrderItem{

	private Integer id ;
	
    private String asin;

    private String sellersku;
    
    private String productName;
    
    private String color;
    
    private Integer quantityOrdered; //订单数量

    private AmazonOutboundOrder order;
    
    
    
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getSellersku() {
		return sellersku;
	}

	public void setSellersku(String sellersku) {
		this.sellersku = sellersku;
	}


	public Integer getQuantityOrdered() {
		return quantityOrdered;
	}

	public void setQuantityOrdered(Integer quantityOrdered) {
		this.quantityOrdered = quantityOrdered;
	}

	//@ManyToOne(optional=true,cascade={javax.persistence.CascadeType.ALL})
	@ManyToOne()
	@JoinColumn(name="order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonOutboundOrder getOrder() {
		return order;
	}

	public void setOrder(AmazonOutboundOrder order) {
		this.order = order;
	}
	
	@Transient
	public String  getName(){
		if(StringUtils.isNotBlank(color)){
			return productName+"_"+color;
		}
		return productName;
	}
	public AmazonOutboundOrderItem(){
		
	}
	
}
