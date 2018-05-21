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

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "temp_email_money_compare")
public class AmazonEmailMoneyCompare implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String email;
	
	private Float total;
	
	private Integer id;
	
	private Float mfnTotal;
	
	private Float rate;
	
	private Date date;
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	public Float getMfnTotal() {
		return mfnTotal;
	}

	public void setMfnTotal(Float mfnTotal) {
		this.mfnTotal = mfnTotal;
	}

	
    
	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public AmazonEmailMoneyCompare() {}
	
}
