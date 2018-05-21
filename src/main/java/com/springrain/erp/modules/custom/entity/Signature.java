package com.springrain.erp.modules.custom.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户签名Entity
 * @author tim
 * @version 2014-05-16
 */
@Entity
@Table(name = "custom_user_signature")
public class Signature implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String userId;
	
	private String signatureContent;
	
	@Id
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSignatureContent() {
		return signatureContent;
	}

	public void setSignatureContent(String signatureContent) {
		this.signatureContent = signatureContent;
	}
	
}


