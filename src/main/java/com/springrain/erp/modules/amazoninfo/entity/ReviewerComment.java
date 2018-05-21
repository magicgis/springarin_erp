package com.springrain.erp.modules.amazoninfo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.persistence.DataEntity;

@Entity
@Table(name = "amazoninfo_reviewer_comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ReviewerComment extends DataEntity<ReviewerComment> {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private AmazonReviewer amazonReviewer;
	private String comment;//'联系内容',
	private String type;//0：手动输入的 1:系统生成的
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="reviewer_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonReviewer getAmazonReviewer() {
		return amazonReviewer;
	}

	public void setAmazonReviewer(AmazonReviewer amazonReviewer) {
		this.amazonReviewer = amazonReviewer;
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


