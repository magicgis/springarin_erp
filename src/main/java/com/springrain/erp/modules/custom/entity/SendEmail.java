package com.springrain.erp.modules.custom.entity;

import java.io.Serializable;
import java.net.IDN;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.IdGen;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 发送邮件Entity
 * @author tim
 * @version 2014-05-13
 */
@Entity
@Table(name = "custom_send_email")
public class SendEmail implements Serializable{
	
	private static final String[] delHtmlTag = { "html", "body", "head"};
	
	private static final long serialVersionUID = 1L;

	public SendEmail() {
	}

	public SendEmail(String id){
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
	
	protected String id;		// 编号
	
	private String sendSubject ; //'回件主题',
	
	private String sendContent ;// '发送内容',
	
	private String sendEmail;//回复邮箱
	
	private String sendAttchmentPath;//回复附件
	
	private String ccToEmail;
	
	private String bccToEmail;
	
	private Date sentDate;
	
	private CustomEmail customEmail;
	
	private String type = "0";//0：发送的 1：回复的
	
	private User createBy;
	
	private String delFlag = DEL_FLAG_NORMAL; // 删除标记（0：正常；1：删除；2：审核）
	
	private String sendFlag = "0";//0:未发送  1:已发送   2：Undelivered message
	
	private String remark;   //客服自己的备注（非数据库字段）
	
	private User checkUser;
	
	private String checkState;//0:未审核 1:审核通过  2：审核未通过 3:不需审核
	
	private String country;
	
	private String reason;
	
	private Date endDate;
	
	private String serverEmail;
	
	private String accountName;
	
	private String taxInvoice;
	
	private String orderId;
	
	@Transient
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Transient
	public String getTaxInvoice() {
		return taxInvoice;
	}

	public void setTaxInvoice(String taxInvoice) {
		this.taxInvoice = taxInvoice;
	}

	@Transient
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getServerEmail() {
		return serverEmail;
	}

	public void setServerEmail(String serverEmail) {
		this.serverEmail = serverEmail;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "check_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCheckUser() {
		return checkUser;
	}

	public void setCheckUser(User checkUser) {
		this.checkUser = checkUser;
	}

	public String getSendContent() {
		if(StringUtils.isNotEmpty(sendContent)){
			sendContent = sendContent.replaceAll("(<style>[\\s\\S]*?</style>)|(<STYLE>[\\s\\S]*?</STYLE>)","");
			for (String tag : delHtmlTag) {
				this.sendContent = this.sendContent.replaceAll("</?" + tag
						+ "[^>]*>", "");
				this.sendContent = this.sendContent.replaceAll(
						"</?" + tag.toUpperCase() + "[^>]*>", "");
			}
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
	public CustomEmail getCustomEmail() {
		return customEmail;
	}

	public void setCustomEmail(CustomEmail customEmail) {
		this.customEmail = customEmail;
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
	
	@PrePersist
	public void prePersist(){
		this.id = IdGen.uuid();
	}

	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public SendEmail(String sendSubject, String sendContent, String sendEmail,String type) {
		super();
		this.sendSubject = sendSubject;
		this.sendContent = sendContent;
		this.sendEmail = sendEmail;
		this.type = type;
	}
	

	@Transient
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCheckState() {
		return checkState;
	}

	public void setCheckState(String checkState) {
		this.checkState = checkState;
	}

	@Transient
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}	
	
	@Transient
	public String getEncryptionEmail() {
		String rs= sendEmail;
		if(sendEmail!=null&&sendEmail.contains(",")){
			 String sendArr="";
			 String[] mailArr= sendEmail.split(",");
			 for (String mail: mailArr) {
				 if(!mail.contains("@amazon")&&!mail.contains("@inateck")&&!mail.contains("@marketplace.amazon")&&mail.contains("@")){
						String[] arr = mail.split("@");
						String tmpEmail=Encodes.encodeBase64(arr[0].getBytes())+"@"+arr[1];
						sendArr+="erp"+tmpEmail+",";
				 }
			 }
			 if(StringUtils.isNotBlank(sendArr)){
				 return sendArr.substring(0,sendArr.length()-1);
			 }
			 return rs;
		}else if(sendEmail!=null){
			if(!sendEmail.contains("@amazon")&&!sendEmail.contains("@inateck")&&!sendEmail.contains("@marketplace.amazon")&&sendEmail.contains("@")){
				String[] arr = sendEmail.split("@");
				String tmpEmail=Encodes.encodeBase64(arr[0].getBytes())+"@"+arr[1];
				rs="erp"+tmpEmail;
			}
		}
		return rs;
	}
	
	
}


