package com.springrain.erp.modules.custom.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.persistence.IdEntity;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 邮件自动回复Entity
 * @author tim
 * @version 2014-09-24
 */
@Entity
@Table(name = "custom_auto_reply")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AutoReply extends IdEntity<AutoReply> {
	
	private static final long serialVersionUID = 1L;
	private String subject; 	// 主题
	private String content;
	private String used = "0";
	private String type;
	private String usedForward ="0";
	private User forwardTo;

	public AutoReply() {
		super();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getUsedForward() {
		return usedForward;
	}

	public void setUsedForward(String usedForward) {
		this.usedForward = usedForward;
	}
	
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getForwardTo() {
		return forwardTo;
	}

	public void setForwardTo(User forwardTo) {
		this.forwardTo = forwardTo;
	}
}


