package com.springrain.erp.modules.amazoninfo.scheduler;

import java.io.File;
import java.net.URL;

import org.springframework.stereotype.Component;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.email.MailManagerInfo;

@Component
public class SendCustomEmail1Manager extends MailManager {
	public SendCustomEmail1Manager() {
		this.setManagerInfo(new MailManagerInfo());
		getManagerInfo().setUserName("amazon@mail1.inateck.com");
		getManagerInfo().setPassword("r7Pch9kZ");
		getManagerInfo().setMailServerHost("mail1.inateck.com");
		getManagerInfo().setMailSmtpHost("mail1.inateck.com");
		
		getManagerInfo().getProperties().setProperty("mail.pop3.host", "mail1.inateck.com");
		getManagerInfo().getProperties().setProperty("mail.smtp.host", "mail1.inateck.com");
		
		getManagerInfo().getProperties().remove("mail.pop3.socketFactory.port");
		getManagerInfo().getProperties().setProperty("mail.smtp.port", "25");
		getManagerInfo().getProperties().remove("mail.smtp.socketFactory.port");
		getManagerInfo().getProperties().setProperty("mail.smtp.ssl.enable","false");
		getManagerInfo().getProperties().setProperty("mail.smtp.ssl.trust","mail1.inateck.com");
		URL url = getClass().getClassLoader().getResource("/");
		if (url != null) {
			File temp = new File(url.getPath());
			String attachmentDir = temp.getParentFile().getParentFile()
					.getAbsolutePath()
					+ "/" + Global.getCkBaseDir() + "/customEmail/invoice";
			getManagerInfo().setAttachmentDir(attachmentDir);
		}
	}
}