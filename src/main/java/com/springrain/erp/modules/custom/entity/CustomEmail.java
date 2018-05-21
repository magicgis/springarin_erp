package com.springrain.erp.modules.custom.entity;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.persistence.IdEntity;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.MessageUtils;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "custom_email_manager")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class CustomEmail extends IdEntity<CustomEmail> {
	private static final long serialVersionUID = 1L;
	private static final String[] delHtmlTag = { "html", "body", "head"};
	private String emailId;
	private String subject;
	private Date customSendDate;
	private Date endDate;
	private Date answerDate;
	private String receiveContent = "";
	private String state;
	private User masterBy;
	private String customId;
	private String revertEmail;
	private String revertServerEmail;
	private String attchmentPath;
	private String inlineAttchmentPath;
	private String transmit;
	private String urgent = "0";
	private String result;
	private String flag;
	
	private 	String 		country;
	private     String      productName;
	private     String      problemType;
	private     String      problem;
	private     String      orderNos;
	
	private     String      tempContent;
	
	private     String      checkState;
	
	private     Date      followDate;
	private     String    followState;
	
	private     String    remindFlag;
	
	private List<SendEmail> sendEmails;
	
	
	@Transient
	public String getEncryptionEmail() {
		String rs= revertEmail;
		if(!revertEmail.contains("@amazon")&&!revertEmail.contains("@inateck")&&!revertEmail.contains("@marketplace.amazon")&&revertEmail.contains("@")){
			String[] arr = revertEmail.split("@");
			String tmpEmail=Encodes.encodeBase64(arr[0].getBytes())+"@"+arr[1];
			rs="erp"+tmpEmail;
		}
		return rs;
	}

	
	public CustomEmail() {
		
	}

	public CustomEmail(String id) {
		this();
		this.id = id;
	}

	@Column(updatable = false)
	public String getEmailId() {
		return this.emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	
	

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProblemType() {
		return problemType;
	}

	public void setProblemType(String problemType) {
		this.problemType = problemType;
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	@Column(updatable = false)
	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCustomSendDate() {
		return this.customSendDate;
	}

	public void setCustomSendDate(Date customSendDate) {
		this.customSendDate = customSendDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getAnswerDate() {
		return this.answerDate;
	}

	public void setAnswerDate(Date answerDate) {
		this.answerDate = answerDate;
	}

	@Column(updatable = false)
	public String getReceiveContent() {
		for (String tag : delHtmlTag) {
			this.receiveContent = this.receiveContent.replaceAll("</?" + tag
					+ "[^>]*>", "");
			this.receiveContent = this.receiveContent.replaceAll(
					"</?" + tag.toUpperCase() + "[^>]*>", "");
		}
		return this.receiveContent;
	}

	public void setReceiveContent(String receiveContent) {
		if (this.receiveContent != null || this.receiveContent.length() == 0)
			this.receiveContent = receiveContent;
		else
			this.receiveContent = (this.receiveContent + "<br/>" + receiveContent);
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getMasterBy() {
		return this.masterBy;
	}

	public void setMasterBy(User masterBy) {
		this.masterBy = masterBy;
	}

	public String getCustomId() {
		return this.customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	@Column(updatable = false)
	public String getRevertEmail() {
		return this.revertEmail;
	}

	public void setRevertEmail(String revertEmail) {
		this.revertEmail = revertEmail;
	}

	@Column(updatable = false)
	public String getAttchmentPath() {
		return this.attchmentPath;
	}

	public void setAttchmentPath(String attchmentPath) {
		if (this.attchmentPath == null || this.attchmentPath.length() == 0)
			this.attchmentPath = attchmentPath;
		else
			this.attchmentPath = (this.attchmentPath + "," + attchmentPath);
	}

	@Transient
	public String getStateCls() {
		String rs = "Rh";
		if ("0".equals(this.state))
			rs = "Ru";
		else if ("1".equals(this.state))
			rs = "Rr";
		else if ("2".equals(this.state))
			rs = "Rh";
		else if ("4".equals(this.state)) {
			rs = "Rn";
		}
		return rs;
	}

	@Transient
	public int getSendedEmails() {
		int rs = this.sendEmails.size();
		if ("0".equals(((SendEmail) this.sendEmails.get(0)).getSendFlag())) {
			return rs - 1;
		}
		return rs;
	}
	
	@Transient
	public String getSendedCheckEmails() {
		int rs = this.sendEmails.size();
		if (rs>0) {
			for (SendEmail email: sendEmails) {
				if("2".equals(email.getCheckState())){//0:未审核 1:审核通过  2：审核未通过
					return "<span style='color:#ff0033;'>Audit failure</span>";
				}else if("0".equals(email.getCheckState())){
					return "<span style='color:#ff0033;'>Unaudited</span>";
				}
			}
		}
		return "";
	}

	@Transient
	public String getStateStr() {
		String rs = MessageUtils.format("custom_email_state1");
		if ("1".equals(this.state))
			rs = MessageUtils.format("custom_email_state2");
		else if ("2".equals(this.state))
			rs = MessageUtils.format("custom_email_state3");
		else if ("4".equals(this.state)) {
			rs = MessageUtils.format("custom_email_state4");
		}
		return rs;
	}

	@Transient
	public int getMasterTime() {
		if ((this.endDate != null) && (this.answerDate != null)) {
			long time = this.endDate.getTime() - this.answerDate.getTime();
			return Math.round((float) time / 60000.0F);
		}
		return -1;
	}

	@Transient
	public boolean getShake() {
		return new Date().getTime() - getUpdateDate().getTime() < 30000L;
	}

	@Column(updatable = false)
	public String getInlineAttchmentPath() {
		return this.inlineAttchmentPath;
	}

	public void setInlineAttchmentPath(String inlineAttchmentPath) {
		if (this.inlineAttchmentPath == null
				|| this.inlineAttchmentPath.length() == 0)
			this.inlineAttchmentPath = inlineAttchmentPath;
		else
			this.inlineAttchmentPath = (this.inlineAttchmentPath + "," + inlineAttchmentPath);
	}

	public String getTransmit() {
		return this.transmit;
	}

	public void setTransmit(String transmit) {
		if (this.transmit == null || this.transmit.length() == 0)
			this.transmit = transmit;
		else
			this.transmit = (this.transmit + "," + transmit);
	}

	@Column(updatable = false)
	public String getUrgent() {
		return this.urgent;
	}

	public void setUrgent(String urgent) {
		this.urgent = urgent;
	}

	@Column(updatable = false)
	public String getRevertServerEmail() {
		return this.revertServerEmail;
	}

	public void setRevertServerEmail(String revertServerEmail) {
		this.revertServerEmail = revertServerEmail;
	}

	@OneToMany(mappedBy = "customEmail", fetch = FetchType.LAZY)
	@Where(clause = "del_flag='0'")
	@OrderBy("sentDate")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SUBSELECT)
	@Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	public List<SendEmail> getSendEmails() {
		return this.sendEmails;
	}

	public void setSendEmails(List<SendEmail> sendEmails) {
		this.sendEmails = sendEmails;
	}

	public String getResult() {
		return this.result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getOrderNos() {
		return orderNos;
	}

	public void setOrderNos(String orderNos) {
		this.orderNos = orderNos;
	}

	public CustomEmail(String emailId, String subject, Date customSendDate,
			String state, User masterBy, String revertEmail) {
		this.emailId = emailId;
		this.subject = subject;
		this.customSendDate = customSendDate;
		this.state = state;
		this.masterBy = masterBy;
		this.revertEmail = revertEmail;
	}

	@Transient
	public String getTempContent() {
		return tempContent;
	}

	public void setTempContent(String tempContent) {
		this.tempContent = tempContent;
	}

	@Transient
	public String getCheckState() {
		return checkState;
	}

	public void setCheckState(String checkState) {
		this.checkState = checkState;
	}

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getFollowDate() {
		return followDate;
	}

	public void setFollowDate(Date followDate) {
		this.followDate = followDate;
	}

	public String getFollowState() {
		return followState;
	}

	public void setFollowState(String followState) {
		this.followState = followState;
	}

	public String getRemindFlag() {
		return remindFlag;
	}

	public void setRemindFlag(String remindFlag) {
		this.remindFlag = remindFlag;
	}
	
	
	@Transient
	public String getSenderIp() {
		String rs= emailId;
		if(StringUtils.isNotBlank(emailId)&&emailId.contains("@")&&emailId.contains(">!")){
			rs=emailId.split("@")[1].split(">!")[0];
			String strPattern = "[0-9.]+";
	        Pattern p = Pattern.compile(strPattern);
	        Matcher m = p.matcher(rs);
	        if(m.matches()||(revertEmail.contains("@amazon.")&&rs.contains("google."))){
	        	rs="<b style='color:red;'>"+rs+" suspicious email</b>";
	        }
		}
		return rs;
	}
	
	
	@Transient
	public String getSenderCountry() {
		return revertEmail.substring(revertEmail.lastIndexOf(".") + 1);
	}
	
}