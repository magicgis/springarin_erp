package com.springrain.erp.common.email;

import com.springrain.erp.common.config.Global;
import java.io.File;
import java.util.Properties;

public class MailManagerInfo {
	private String mailServerHost;
	private String mailSmtpHost;
	private String mailImapHost;
	private String mailServerPort = "110";
	private String protocal = "pop3";
	private int timout = 60;
	private String userName;
	private String password;
	private String attachmentDir;
	private String emailDir;
	private String emailFileSuffix = ".eml";

	private boolean validate = true;

	public MailManagerInfo() {
		this.mailServerHost = Global.getConfig("email.pophost");
		this.mailSmtpHost = Global.getConfig("email.stmphost");
		this.mailImapHost = Global.getConfig("email.imaphost");
		//根据邮箱区分gmail和qq邮箱,再设置对应的服务器
		this.setUserName(Global.getConfig("email.username"));
		//this.userName = Global.getConfig("email.username");
		this.password = Global.getConfig("email.password");
		try {
			this.timout = Integer.parseInt(Global
					.getConfig("email.smtp.timeout"));
		} catch (NumberFormatException localNumberFormatException) {
		}
	}

	private Properties properties;

	public Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			properties.put("mail.pop3.host", this.mailServerHost);
			properties.put("mail.pop3.port", this.mailServerPort);
			properties.put("mail.pop3.auth", this.validate ? "true" : "false");
			properties.put("mail.imap.host", this.mailImapHost);
			properties.put("mail.smtp.host", this.mailSmtpHost);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.transport.protocol", "smtp");
			properties.put("mail.debug", false);
			properties.put("mail.smtp.timeout",this.timout * 1000);
			properties.put("mail.smtp.ssl.enable","true");
			properties.put("mail.smtp.starttls.enable","true");
		}
		return properties;
	}

	public String getProtocal() {
		return this.protocal;
	}

	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}

	public String getAttachmentDir() {
		return this.attachmentDir;
	}

	public void setAttachmentDir(String attachmentDir) {
		if (!attachmentDir.endsWith(File.separator)) {
			attachmentDir = attachmentDir + File.separator;
		}
		this.attachmentDir = attachmentDir;
	}

	public String getEmailDir() {
		return this.emailDir;
	}

	public void setEmailDir(String emailDir) {
		if (!emailDir.endsWith(File.separator)) {
			emailDir = emailDir + File.separator;
		}
		this.emailDir = emailDir;
	}

	public String getEmailFileSuffix() {
		return this.emailFileSuffix;
	}

	public void setEmailFileSuffix(String emailFileSuffix) {
		if (!emailFileSuffix.startsWith(".")) {
			emailFileSuffix = "." + emailFileSuffix;
		}
		this.emailFileSuffix = emailFileSuffix;
	}

	public String getMailServerHost() {
		return this.mailServerHost;
	}

	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	public String getMailServerPort() {
		return this.mailServerPort;
	}

	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		if (this.userName.endsWith("licheng-tech.com")) {	//支持qq邮箱
			this.getProperties().setProperty("mail.pop3.host", Global.getConfig("qq.email.pophost"));
			this.getProperties().setProperty("mail.smtp.host", Global.getConfig("qq.email.stmphost"));
			this.getProperties().setProperty("mail.imap.host", Global.getConfig("qq.email.imaphost"));
			this.mailServerHost = Global.getConfig("qq.email.pophost");
			this.mailSmtpHost = Global.getConfig("qq.email.stmphost");
			this.mailImapHost = Global.getConfig("qq.email.imaphost");
		} else if(!"amazon@mail1.inateck.com".equals(userName)){
			this.getProperties().setProperty("mail.pop3.host", Global.getConfig("email.pophost"));
			this.getProperties().setProperty("mail.smtp.host", Global.getConfig("email.stmphost"));
			this.getProperties().setProperty("mail.imap.host", Global.getConfig("email.imaphost"));
			this.mailServerHost = Global.getConfig("email.pophost");
			this.mailSmtpHost = Global.getConfig("email.stmphost");
			this.mailImapHost = Global.getConfig("email.imaphost");
		}
	}

	public boolean isValidate() {
		return this.validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String getMailSmtpHost() {
		return this.mailSmtpHost;
	}

	public void setMailSmtpHost(String mailSmtpHost) {
		this.mailSmtpHost = mailSmtpHost;
	}

	public int getTimout() {
		return this.timout;
	}

	public String getMailImapHost() {
		return this.mailImapHost;
	}

	public void setMailImapHost(String mailImapHost) {
		this.mailImapHost = mailImapHost;
	}

	public void setTimout(int timout) {
		this.timout = timout;
	}
}