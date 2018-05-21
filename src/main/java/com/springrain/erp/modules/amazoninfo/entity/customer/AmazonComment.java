package com.springrain.erp.modules.amazoninfo.entity.customer;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.custom.entity.CustomEmailTemplate;

/**
 * 售后邮件发送记录Entity
 */
@Entity
@Table(name = "amazoninfo_comment")
public class AmazonComment{

	private Integer id ;
	
    private Date createDate;
    
    private String content;
    
    private AmazonCustomer customer;
    
    private AmazonCustomFilter task;	//归属任务
    private Date sentDate;				//发送日期
    private String sendFlag;		//发送标记 0：未发送 1：已发送
    private String sendEmail;		//发送邮箱
    private String sendSubject;		//主题

    private CustomEmailTemplate template;	//邮件模板
    
    private CustomEmail customEmail;	//客户回复邮件
    private AmazonReviewComment reviewComment;	//客户追加的评论
    private String orderId;	//亚马逊订单号
    private String star;	//非数据库字段 0:差评  1：好评
    
    public AmazonComment() {}

    @Id
   	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@ManyToOne()
	@JoinColumn(name="customer_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(AmazonCustomer customer) {
		this.customer = customer;
	}

	@ManyToOne()
	@JoinColumn(name="task_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonCustomFilter getTask() {
		return task;
	}

	public void setTask(AmazonCustomFilter task) {
		this.task = task;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public String getSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(String sendFlag) {
		this.sendFlag = sendFlag;
	}
	
	@ManyToOne()
	@JoinColumn(name="template_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public CustomEmailTemplate getTemplate() {
		return template;
	}

	public void setTemplate(CustomEmailTemplate template) {
		this.template = template;
	}

	public String getSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(String sendEmail) {
		this.sendEmail = sendEmail;
	}

	public String getSendSubject() {
		return sendSubject;
	}

	public void setSendSubject(String sendSubject) {
		this.sendSubject = sendSubject;
	}

	@ManyToOne()
	@JoinColumn(name="custom_email")
	@NotFound(action = NotFoundAction.IGNORE)
	public CustomEmail getCustomEmail() {
		return customEmail;
	}

	public void setCustomEmail(CustomEmail customEmail) {
		this.customEmail = customEmail;
	}

	@ManyToOne()
	@JoinColumn(name="reivew_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonReviewComment getReviewComment() {
		return reviewComment;
	}

	public void setReviewComment(AmazonReviewComment reviewComment) {
		this.reviewComment = reviewComment;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Transient
	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}
	
}
