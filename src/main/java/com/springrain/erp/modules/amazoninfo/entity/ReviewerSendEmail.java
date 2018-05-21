package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.net.IDN;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 评测发送邮件Entity
 */
@Entity
@Table(name = "amazoninfo_reviewer_send_email")
public class ReviewerSendEmail implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public ReviewerSendEmail() {
	}

	public ReviewerSendEmail(Integer id){
		this();
		this.id = id;
	}
	
	@Transient
	public MailInfo getMailInfo(){
		MailInfo info = new MailInfo();
		info.setSubject(sendSubject);
		info.setBccToAddress(bccToEmail);
		info.setCcToAddress(ccToEmail);
		info.setContent(sendContent);
		info.setSentdate(new Date());
		info.setToAddress(sendEmail);
		return info;
	}
	
	//多个收件人的情况下拆分发送,避免评测邮件群发是客户能互相看到
	@Transient
	public List<MailInfo> getMailInfoList(){
		List<MailInfo> list = Lists.newArrayList();
		if (StringUtils.isNotEmpty(sendEmail) && sendEmail.contains(",")) {
			for (String email : sendEmail.split(",")) {
				MailInfo info = new MailInfo();
				info.setSubject(sendSubject);
				info.setBccToAddress(bccToEmail);
				info.setCcToAddress(ccToEmail);
				info.setContent(sendContent);
				info.setSentdate(new Date());
				info.setToAddress(email);
				list.add(info);
			}
		} else {
			MailInfo info = new MailInfo();
			info.setSubject(sendSubject);
			info.setBccToAddress(bccToEmail);
			info.setCcToAddress(ccToEmail);
			info.setContent(sendContent);
			info.setSentdate(new Date());
			info.setToAddress(sendEmail);
			list.add(info);
		}
		return list;
	}
	
	protected Integer id;		// 编号
	
	private String sendSubject ; //'回件主题',
	
	private String sendContent ;// '发送内容',
	
	private String sendEmail;//回复邮箱
	
	private String sendAttchmentPath;//回复附件
	
	private String ccToEmail;
	
	private String bccToEmail;
	
	private Date sentDate;
	
	private ReviewerEmail reviewerEmail;
	
	private String type = "0";//0：发送的 1：回复的
	
	private User createBy;
	
	private String delFlag = DEL_FLAG_NORMAL; // 删除标记（0：正常；1：删除；2：审核）
	
	private String sendFlag = "0";
	
	private String remark; //备注
	
	public String getSendContent() {
		if(StringUtils.isNotEmpty(sendContent)){
			sendContent = sendContent.replaceAll("(<style>[\\s\\S]*?</style>)|(<STYLE>[\\s\\S]*?</STYLE>)","");
		}
		return sendContent;
	}

	public void setSendContent(String sendContent) {
		this.sendContent = sendContent;
	}
	
	public String getSendSubject() {
		return this.sendSubject;
	}	

	public void setSendSubject(String sendSubject) {
		this.sendSubject = sendSubject;
	}
	
	public String getSendEmail() {
		if(sendEmail!=null){
			sendEmail =  IDN.toASCII(sendEmail);
		}
		return sendEmail;
	}

	public void setSendEmail(String sendEmail) {
		this.sendEmail = sendEmail;
	}
	
	public String getSendAttchmentPath() {
		return sendAttchmentPath;
	}

	public void setSendAttchmentPath(String sendAttchmentPath) {
		if(StringUtils.isBlank(this.sendAttchmentPath)){
			this.sendAttchmentPath = sendAttchmentPath;
		}else{
			this.sendAttchmentPath = this.sendAttchmentPath+","+sendAttchmentPath;
		}	
	}
	
	public String getCcToEmail() {
		if(ccToEmail!=null){
			ccToEmail =  IDN.toASCII(ccToEmail);
		}
		return ccToEmail;
	}

	public void setCcToEmail(String ccToEmail) {
		this.ccToEmail = ccToEmail;
	}
	
	public String getBccToEmail() {
		if(bccToEmail!=null){
			bccToEmail =  IDN.toASCII(bccToEmail);
		}
		return bccToEmail;
	}

	public void setBccToEmail(String bccToEmail) {
		this.bccToEmail = bccToEmail;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getSentDate() {
		return sentDate;
	}
	
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public ReviewerEmail getReviewerEmail() {
		return reviewerEmail;
	}

	public void setReviewerEmail(ReviewerEmail reviewerEmail) {
		this.reviewerEmail = reviewerEmail;
	}

	@Column(updatable=false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateBy() {
		return createBy;
	}

	public void setCreateBy(User createBy) {
		this.createBy = createBy;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getDelFlag() {
		return delFlag;
	}
	
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	
	public String getSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(String sendFlag) {
		this.sendFlag = sendFlag;
	}
	
	@Transient
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	// 删除标记（0：正常；1：删除；2：审核；）
	public static final String FIELD_DEL_FLAG = "delFlag";
	public static final String DEL_FLAG_NORMAL = "0";
	public static final String DEL_FLAG_DELETE = "1";
	public static final String DEL_FLAG_AUDIT = "2";
	
	@Transient
	public boolean getShake(){
		if(getSentDate()!=null)
			return new Date().getTime() - getSentDate().getTime() < 30000;
		else
			return false;
	}

	public ReviewerSendEmail(String sendSubject, String sendContent, String sendEmail,String type) {
		super();
		this.sendSubject = sendSubject;
		this.sendContent = sendContent;
		this.sendEmail = sendEmail;
		this.type = type;
	}	
	
	
}


