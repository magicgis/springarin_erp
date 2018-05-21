package com.springrain.erp.modules.psi.entity;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 进销存供应商
 */
@Entity
@Table(name = "psi_supplier_potential")
public class PsiSupplierPotential {
	private 	Integer 	id;
	private 	String 		name;
	private 	String 		nikename;
	private 	String 		shortName;	//中文简称
	private 	String 		address;
	private 	String 		site;
	private 	String 		dollarAccount1;
	private 	String 		dollarAccount2;
	private 	String 		rmbAccount1;
	private 	String 		rmbAccount2;
	private 	Integer 	deposit;
	private 	String 		contact;
	private 	String 		phone;
	private 	String 		mail;
	private 	String 		qq;
	private 	String 		memo;
	private 	String 		delFlag;
	private 	Integer 	addtime;
	private 	Integer 	uptime;
	private 	User 		createUser;
	private 	User 		updateUser;
	private 	String 		type;//0为供货商;1是物流供应商
	private 	String 		currencyType;
	private 	String 		suffixName;
	private 	String 		payMark;
	private     String      createRegularFlag;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSuffixName() {
		return suffixName;
	}

	public void setSuffixName(String suffixName) {
		this.suffixName = suffixName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNikename() {
		return nikename;
	}

	public void setNikename(String nikename) {
		this.nikename = nikename;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	@Column(name = "account1")
	public String getDollarAccount1() {
		return dollarAccount1;
	}

	public void setDollarAccount1(String dollarAccount1) {
		this.dollarAccount1 = dollarAccount1;
	}

	@Column(name = "account2")
	public String getDollarAccount2() {
		return dollarAccount2;
	}

	public void setDollarAccount2(String dollarAccount2) {
		this.dollarAccount2 = dollarAccount2;
	}

	@Column(name = "account3")
	public String getRmbAccount1() {
		return rmbAccount1;
	}

	public void setRmbAccount1(String rmbAccount1) {
		this.rmbAccount1 = rmbAccount1;
	}

	@Column(name = "account4")
	public String getRmbAccount2() {
		return rmbAccount2;
	}

	public void setRmbAccount2(String rmbAccount2) {
		this.rmbAccount2 = rmbAccount2;
	}

	public String getPayMark() {
		return payMark;
	}

	public void setPayMark(String payMark) {
		this.payMark = payMark;
	}

	public Integer getDeposit() {
		return deposit;
	}

	public void setDeposit(Integer deposit) {
		this.deposit = deposit;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public Integer getAddtime() {
		return addtime;
	}

	public void setAddtime(Integer addtime) {
		this.addtime = addtime;
	}

	public Integer getUptime() {
		return uptime;
	}

	public void setUptime(Integer uptime) {
		this.uptime = uptime;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	@Transient
	public String getTypeName() {
		if("0".equals(type)){
			return "产品供应商";
		}else if("1".equals(type)){
			return "物流服务供应商";
		}else if("2".equals(type)){
			return "包材供应商";
		}else{
			return "供应商类型未定";
		}
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userid")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "upuserid")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}
	

	@Transient
	public String getAccountByType(String type){
		if("1".equals(type)){
			return dollarAccount1;
		}else if("2".equals(type)){
			return dollarAccount2;
		}else if("3".equals(type)){
			return rmbAccount1;
		}else if("4".equals(type)){
			return rmbAccount2;
		}
		return "";
	}
	
	@Transient
	public Map<String,String> getAccountMap(){
		Map<String,String> rs = Maps.newLinkedHashMap();
		if(StringUtils.isNotEmpty(dollarAccount1)){
			rs.put("1", dollarAccount1);
		}
		if(StringUtils.isNotEmpty(dollarAccount2)){
			rs.put("2", dollarAccount2);
		}
		if(StringUtils.isNotEmpty(rmbAccount1)){
			rs.put("3", rmbAccount1);
		}
		if(StringUtils.isNotEmpty(rmbAccount2)){
			rs.put("4", rmbAccount2);
		}
		
		return rs;
	}
	
	public PsiSupplier copyToSupplier(){
		PsiSupplier supplier = new PsiSupplier();
		supplier.setAddress(this.getAddress());
		supplier.setAddtime(this.getAddtime());
		supplier.setContact(this.getContact());
		supplier.setCreateUser(this.getCreateUser());
		supplier.setCurrencyType(this.getCurrencyType());
		supplier.setDelFlag(this.getDelFlag());
		supplier.setDeposit(this.getDeposit());
		supplier.setDollarAccount1(this.getDollarAccount1());
		supplier.setDollarAccount2(this.getDollarAccount2());
		supplier.setMail(this.getMail());
		supplier.setMemo(this.getMemo());
		supplier.setName(this.getName());
		supplier.setNikename(this.getNikename());
		supplier.setPayMark(this.getPayMark());
		supplier.setPhone(this.getPhone());
		supplier.setQq(this.getQq());
		supplier.setRmbAccount1(this.getRmbAccount1());
		supplier.setRmbAccount2(this.getRmbAccount2());
		supplier.setShortName(this.getShortName());
		supplier.setSite(this.getSite());
		supplier.setSuffixName(this.getSuffixName());
		supplier.setType(this.getType());
		supplier.setUpdateUser(this.getUpdateUser());
		supplier.setUptime(this.getUptime());
		return supplier;
	}

	public String getCreateRegularFlag() {
		return createRegularFlag;
	}

	public void setCreateRegularFlag(String createRegularFlag) {
		this.createRegularFlag = createRegularFlag;
	}
	
	
}
