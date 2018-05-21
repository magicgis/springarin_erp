package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Date;

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

import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "psi_product_post_mail_info")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiProductPostMailInfo implements Serializable{  
	
	private static final long serialVersionUID = 7456597852186296980L;
	private Integer     id;
	private String 		type;           //类型   0主力  1新品
	private String 		country;        //平台
	private String 		productName;    //产品名
	private String 		status;         //状态   已发送、不发送、待发送
	private String 		remark;         //
	
	private Date   		addDate ;
	private Date   		updateDate ;
	private User        updateUser ;
	
	public PsiProductPostMailInfo(){};
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	@ManyToOne()
	@JoinColumn(name="update_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	public PsiProductPostMailInfo(String type, String country, String productName,	String status, String remark, Date addDate, Date updateDate,User updateUser) {
		super();
		this.type = type;
		this.country = country;
		this.productName = productName;
		this.status = status;
		this.remark = remark;
		this.addDate = addDate;
		this.updateDate = updateDate;
		this.updateUser = updateUser;
	}
	
}
