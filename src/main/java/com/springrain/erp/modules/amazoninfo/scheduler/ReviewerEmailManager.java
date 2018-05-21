package com.springrain.erp.modules.amazoninfo.scheduler;

import java.io.File;
import java.net.URL;

import org.springframework.stereotype.Component;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailManager;


@Component
public class ReviewerEmailManager extends MailManager{

	public ReviewerEmailManager() {
		this.getManagerInfo().setUserName(Global.getConfig("reviewer.email.username"));
		this.getManagerInfo().setPassword(Global.getConfig("reviewer.email.password"));
		URL url = getClass().getClassLoader().getResource("/");
		if(url!=null){
			File temp = new File(url.getPath());
			String attachmentDir =temp.getParentFile().getParentFile().getAbsolutePath()+"/"+Global.getCkBaseDir()+"/customEmail";
			this.getManagerInfo().setAttachmentDir(attachmentDir);
		}
	}
	
}
