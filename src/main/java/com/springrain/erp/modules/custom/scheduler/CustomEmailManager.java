package com.springrain.erp.modules.custom.scheduler;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.email.MailManagerInfo;


@Component
public class CustomEmailManager extends MailManager{

	public CustomEmailManager() {
		this.getManagerInfo().setUserName(Global.getConfig("custom.email.username"));
		this.getManagerInfo().setPassword(Global.getConfig("custom.email.password"));
		URL url = getClass().getClassLoader().getResource("/");
		if(url!=null){
			File temp = new File(url.getPath());
			String attachmentDir =temp.getParentFile().getParentFile().getAbsolutePath()+"/"+Global.getCkBaseDir()+"/customEmail";
			this.getManagerInfo().setAttachmentDir(attachmentDir);
		}
	}

	
	public MailManagerInfo setCustomEmailManager(String type,String userName,String password){
		MailManagerInfo info=this.getManagerInfo();
		info.setUserName(userName);
		info.setPassword(password);
		if("1".equals(type)){
			Properties  p=info.getProperties();
			p.setProperty("mail.pop3.host", Global.getConfig("qq.email.pophost"));
			p.setProperty("mail.smtp.host", Global.getConfig("qq.email.stmphost"));
			p.setProperty("mail.imap.host", Global.getConfig("qq.email.imaphost"));
			info.setMailServerHost(Global.getConfig("qq.email.pophost"));
			info.setMailSmtpHost(Global.getConfig("qq.email.stmphost"));
			info.setMailImapHost(Global.getConfig("qq.email.imaphost"));
		}else{
			Properties  p=info.getProperties();
			p.setProperty("mail.pop3.host", Global.getConfig("email.pophost"));
			p.setProperty("mail.smtp.host", Global.getConfig("email.stmphost"));
			p.setProperty("mail.imap.host", Global.getConfig("email.imaphost"));
			info.setMailServerHost(Global.getConfig("email.pophost"));
			info.setMailSmtpHost(Global.getConfig("email.stmphost"));
			info.setMailImapHost(Global.getConfig("email.imaphost"));
		}
		URL url = getClass().getClassLoader().getResource("/");
		if(url!=null){
			File temp = new File(url.getPath());
			String attachmentDir =temp.getParentFile().getParentFile().getAbsolutePath()+"/"+Global.getCkBaseDir()+"/customEmail";
			info.setAttachmentDir(attachmentDir);
		}
		return info;
	}
}
