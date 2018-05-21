package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "psi_supplier")
public class PsiSupplier implements Serializable{
	private static final long serialVersionUID = 6807450137257444350L;
	private 	Integer 		id;
	private 	String		 	name;
	private 	String 			nikename;
	private 	String 			shortName;	//中文简称
	private 	String 			address;
	private 	String 			site;
	private 	String 			dollarAccount1;
	private 	String 			dollarAccount2;
	private 	String 			rmbAccount1;
	private 	String 			rmbAccount2;
	private 	String 			publicAccount;
	private 	Integer 		deposit;
	private 	String 			contact;
	private 	String 			phone;
	private 	String 			mail;
	private 	String 			qq;
	private 	String 			memo;
	private 	String 			delFlag;
	private 	Integer 		addtime;
	private 	Integer 		uptime;
	private 	User 			createUser;
	private 	User 			updateUser;
	
	private List<ProductSupplier> products;
	
	private 	String 			type;//0为供货商;1是物流供应商
	private 	String 			currencyType;
	private 	String 			suffixName;
	private 	String 			payMark;
	
	//尾款支付比例
	private 	Integer 		balanceRate1;       //尾款首次付款比例
	private 	Integer 		balanceDelay1;      //建了提单几天之后算延时
	private 	Integer 		balanceRate2;       //尾款第二次付款比例
	private 	Integer 		balanceDelay2;      //建了提单几天之后算延时

	private 	Integer   		taxRate;            //含税价税率
	
	private 	String 			eliminate="1";
	
	private   	String        	contractNo; 		//合同号
	private   	String        	attchmentPath; 		//合同附件
	private   	String        	reviewPath;     	//供应商考核文件
	private   	String        	payRemark;			//付款条款备注
	private     String          payType;            //付款类型 1：次月1号        2：次月5号          3：次月10号        4：次月15号        5：次月20号         6：次月25号  
												    //     11：次次月1号 12：次次月5号  13：次次月10号 14：次次月15号 15：次次月20号 16：次次月25号     
	
	public String getEliminate() {
		return eliminate;
	}

	public void setEliminate(String eliminate) {
		this.eliminate = eliminate;
	}

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

	public Integer getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Integer taxRate) {
		this.taxRate = taxRate;
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

	
	public void setAttchmentPathAppend(String attchmentPath) {
		if(StringUtils.isBlank(this.attchmentPath)){
			this.attchmentPath = attchmentPath;
		}else{
			this.attchmentPath = this.attchmentPath+","+attchmentPath;
		}
	}
	
	public void setReviewPathAppend(String reviewPath) {
		if(StringUtils.isBlank(this.reviewPath)){
			this.reviewPath = reviewPath;
		}else{
			this.reviewPath = this.reviewPath+","+reviewPath;
		}
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
	
	
	@JsonIgnore
	@OneToMany(mappedBy = "supplier",cascade=javax.persistence.CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<ProductSupplier> getProducts() {
		return products;
	}

	public void setProducts(List<ProductSupplier> products) {
		this.products = products;
	}

	@Transient
	public String getAccountByType(String type){
		if("0".equals(type)){
			return publicAccount;
		}else if("1".equals(type)){
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
		if(StringUtils.isNotEmpty(publicAccount)){
			rs.put("0", publicAccount);
		}
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

	public Integer getBalanceRate1() {
		return balanceRate1;
	}

	public void setBalanceRate1(Integer balanceRate1) {
		this.balanceRate1 = balanceRate1;
	}

	public Integer getBalanceDelay1() {
		return balanceDelay1;
	}

	public void setBalanceDelay1(Integer balanceDelay1) {
		this.balanceDelay1 = balanceDelay1;
	}

	public Integer getBalanceRate2() {
		return balanceRate2;
	}

	public void setBalanceRate2(Integer balanceRate2) {
		this.balanceRate2 = balanceRate2;
	}

	public Integer getBalanceDelay2() {
		return balanceDelay2;
	}

	public void setBalanceDelay2(Integer balanceDelay2) {
		this.balanceDelay2 = balanceDelay2;
	}

	public String getPublicAccount() {
		return publicAccount;
	}

	public void setPublicAccount(String publicAccount) {
		this.publicAccount = publicAccount;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getAttchmentPath() {
		return attchmentPath;
	}

	public void setAttchmentPath(String attchmentPath) {
		this.attchmentPath = attchmentPath;
	}

	public String getReviewPath() {
		return reviewPath;
	}

	public void setReviewPath(String reviewPath) {
		this.reviewPath = reviewPath;
	}

	public String getPayRemark() {
		return payRemark;
	}

	public void setPayRemark(String payRemark) {
		this.payRemark = payRemark;
	}
	
	public PsiSupplier(){};
	
	public PsiSupplier(Integer id){
		this.id=id;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}
	
}
