package com.springrain.erp.modules.psi.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "psi_product_hscode_detail")
public class PsiProductHsCodeDetail {
	private	Integer id;
	private String productName;
	private String color;
	private Date updateDate;
	private String euHscode;
	private String caHscode;
	private String jpHscode;
	private String usHscode;
	private String hkHscode;
	private String cnHscode;
	private String formatDate;
	private PsiProduct psiProduct;
	
	@ManyToOne()
	@JoinColumn(name="product_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiProduct getPsiProduct() {
		return psiProduct;
	}
	public void setPsiProduct(PsiProduct psiProduct) {
		this.psiProduct = psiProduct;
	}
	
	
	@Transient
	public String getFormatDate() {
		return formatDate;
	}
	public void setFormatDate(String formatDate) {
		this.formatDate = formatDate;
	}
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getEuHscode() {
		return euHscode;
	}
	public void setEuHscode(String euHscode) {
		this.euHscode = euHscode;
	}
	public String getCaHscode() {
		return caHscode;
	}
	public void setCaHscode(String caHscode) {
		this.caHscode = caHscode;
	}
	public String getJpHscode() {
		return jpHscode;
	}
	public void setJpHscode(String jpHscode) {
		this.jpHscode = jpHscode;
	}
	public String getUsHscode() {
		return usHscode;
	}
	public void setUsHscode(String usHscode) {
		this.usHscode = usHscode;
	}
	public String getHkHscode() {
		return hkHscode;
	}
	public void setHkHscode(String hkHscode) {
		this.hkHscode = hkHscode;
	}
	public String getCnHscode() {
		return cnHscode;
	}
	public void setCnHscode(String cnHscode) {
		this.cnHscode = cnHscode;
	}
}
