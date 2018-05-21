/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 运单付款修正item表Entity
 * @author Michael
 * @version 2015-01-29
 */
@Entity
@Table(name = "psi_transport_revise_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiTransportReviseItem {
	
	private 	Integer		  id; 			// id
	private     String        reviseType;   // 修正类型
	private     Float         reviseAmount; // 修正金额
	private     String        currency;     // 货币类型
	private     Float         rate;         // 汇率
	private     Float         oldAmount;    // 原来付款额
	private     String        remark;       // 备注
	private     PsiTransportRevise transportRevise; 
	

	public PsiTransportReviseItem() {
		super();
	}

	public PsiTransportReviseItem(Integer id){
		this();
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	

	public Float getOldAmount() {
		return oldAmount;
	}

	public void setOldAmount(Float oldAmount) {
		this.oldAmount = oldAmount;
	}

	@ManyToOne()
	@JoinColumn(name="transport_revise_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiTransportRevise getTransportRevise() {
		return transportRevise;
	}

	public void setTransportRevise(PsiTransportRevise transportRevise) {
		this.transportRevise = transportRevise;
	}

	public String getReviseType() {   
		return reviseType;
	}

	public void setReviseType(String reviseType) {
		this.reviseType = reviseType;
	}

	public Float getReviseAmount() {
		return reviseAmount;
	}

	public void setReviseAmount(Float reviseAmount) {
		this.reviseAmount = reviseAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	
}


