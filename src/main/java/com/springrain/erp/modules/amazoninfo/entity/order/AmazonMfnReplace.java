package com.springrain.erp.modules.amazoninfo.entity.order;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "amazoninfo_mfn_replace")
public class AmazonMfnReplace implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String email;
	
	private String nameQuantity;
	
	private String orderId;
	
	private Date date; 
	
	private Float total;
	
	private Integer id;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNameQuantity() {
		return nameQuantity;
	}

	public void setNameQuantity(String nameQuantity) {
		this.nameQuantity = nameQuantity;
	}

    @Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	public AmazonMfnReplace() {}
	
}
