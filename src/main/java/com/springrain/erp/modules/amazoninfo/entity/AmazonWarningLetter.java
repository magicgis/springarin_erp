package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 亚马逊警告信件
 * @author lee
 */
@Entity
@Table(name = "amazoninfo_warning_letter")
public class AmazonWarningLetter implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String country; 		// 平台
	private String subject; 		// 信件主题
	private String letterId; 		// 信件ID
	private String letterContent; 	// 信件内容
	private Date letterDate; 		// 发信时间
	private Date createDate; 		// 创建时间
	private String type; 		//类型Policy Warning、Notifications、 Other Issues
	private String accountName; 		// 账号
	private String productName; 		// 产品名称,根据标题中解析出的sku统计
	
	
	public AmazonWarningLetter() {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getLetterId() {
		return letterId;
	}

	public void setLetterId(String letterId) {
		this.letterId = letterId;
	}

	public String getLetterContent() {
		return letterContent;
	}

	public void setLetterContent(String letterContent) {
		this.letterContent = letterContent;
	}

	public Date getLetterDate() {
		return letterDate;
	}

	public void setLetterDate(Date letterDate) {
		this.letterDate = letterDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Transient
	public String getType() {
		return type;
	}

	@Transient
	public void setType(String type) {
		this.type = type;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	
}
