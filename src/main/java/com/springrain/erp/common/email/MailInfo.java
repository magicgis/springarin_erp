package com.springrain.erp.common.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MailInfo {
	
	private String toAddress;
	
	private String ccToAddress;
	
	private String bccToAddress;
	
	private Date sentdate;
	
	private String subject;
	
	private String content;
	
	//support别称发送，为了开case亚马逊认证
	private String fromServer;
	
	private List<String> filePath = new ArrayList<String>();
	
	private List<String> fileName = new ArrayList<String>();
	
	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getSentdate() {
		return sentdate;
	}

	public void setSentdate(Date sentdate) {
		this.sentdate = sentdate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public List<String> getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath.add(filePath);
	}

	public List<String> getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName.add(fileName);
	}

	public String getCcToAddress() {
		return ccToAddress;
	}

	public void setCcToAddress(String ccToAddress) {
		this.ccToAddress = ccToAddress;
	}

	public String getBccToAddress() {
		return bccToAddress;
	}

	public void setBccToAddress(String bccToAddress) {
		this.bccToAddress = bccToAddress;
	}
	
	public String getFromServer() {
		return fromServer;
	}

	public void setFromServer(String fromServer) {
		this.fromServer = fromServer;
	}

	public MailInfo() {}

	public MailInfo(String toAddress, String subject,
			Date sentdate) {
		super();
		this.toAddress = toAddress;
		this.subject = subject;
		this.sentdate = sentdate;
	}	
}
