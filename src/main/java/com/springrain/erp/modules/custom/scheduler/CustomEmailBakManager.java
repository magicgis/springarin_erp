package com.springrain.erp.modules.custom.scheduler;

import java.io.File;
import java.net.URL;

import org.springframework.stereotype.Component;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailManager;


@Component
public class CustomEmailBakManager extends MailManager{

	public CustomEmailBakManager() {
		this.getManagerInfo().setUserName("support1@inateck.com");
		this.getManagerInfo().setPassword("=2he9SMr1");
		URL url = getClass().getClassLoader().getResource("/");
		if(url!=null){
			File temp = new File(url.getPath());
			String attachmentDir =temp.getParentFile().getParentFile().getAbsolutePath()+"/"+Global.getCkBaseDir()+"/customEmail";
			this.getManagerInfo().setAttachmentDir(attachmentDir);
		}
	}
	
}
