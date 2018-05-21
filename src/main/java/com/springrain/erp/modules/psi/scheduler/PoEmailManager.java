package com.springrain.erp.modules.psi.scheduler;

import java.io.File;
import java.net.URL;

import org.springframework.stereotype.Component;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailManager;


@Component
public class PoEmailManager extends MailManager{
	public PoEmailManager() {
		this.getManagerInfo().setUserName(Global.getConfig("po.email.username"));
		this.getManagerInfo().setPassword(Global.getConfig("po.email.password"));
//		this.getManagerInfo().getProperties().setProperty("mail.smtp.port", "587");
//		this.getManagerInfo().getProperties().setProperty("mail.pop3.host", "hwpop.exmail.qq.com");
//		this.getManagerInfo().getProperties().setProperty("mail.smtp.host", "hwsmtp.exmail.qq.com");
//		this.getManagerInfo().getProperties().setProperty("mail.imap.host", "imap.exmail.qq.com");
		
		URL url = getClass().getClassLoader().getResource("/");
		if(url!=null){
			File temp = new File(url.getPath());
			String attachmentDir =temp.getParentFile().getParentFile().getAbsolutePath()+"/"+Global.getCkBaseDir()+"/PoEmail";
			this.getManagerInfo().setAttachmentDir(attachmentDir);
		}
	}
	
}
