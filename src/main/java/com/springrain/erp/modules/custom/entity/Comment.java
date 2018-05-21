package com.springrain.erp.modules.custom.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.persistence.IdEntity;

/**
 * 评论Entity
 * @author tim
 * @version 2014-05-21
 */
@Entity
@Table(name = "custom_event_comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Comment extends IdEntity<Comment> {
	
	private static final long serialVersionUID = 1L;
	
	private Event event;
	
	private String comment;//'事件记录内容',
	
	private String type;//0：手动输入的 1:系统生成的
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="event")
	@NotFound(action = NotFoundAction.IGNORE)
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
		if(event!=null){
			event.setUpdateDate(new Date());
		}
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = HtmlUtils.htmlUnescape(comment);;
	}
	
	@Column(updatable=false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}


