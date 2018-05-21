package com.springrain.erp.modules.amazoninfo.entity.order;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "amazoninfo_return_order_shipment_item")
@DynamicInsert
@DynamicUpdate
public class AmazonReturnOrderShipmentItem{
	
    private Integer id;
    private String sku;
    private Integer quantityShipped;
    private AmazonReturnOrderShipment order;
    private String disposition;
   
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getQuantityShipped() {
		return quantityShipped;
	}

	public void setQuantityShipped(Integer quantityShipped) {
		this.quantityShipped = quantityShipped;
	}
	
	
	@ManyToOne()
	@JoinColumn(name="shipment")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonReturnOrderShipment getOrder() {
		return order;
	}

	public void setOrder(AmazonReturnOrderShipment order) {
		this.order = order;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
	
}
