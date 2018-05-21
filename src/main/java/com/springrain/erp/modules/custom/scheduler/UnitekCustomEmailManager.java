package com.springrain.erp.modules.custom.scheduler;

import org.springframework.stereotype.Component;

import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.email.MailManagerInfo;

@Component
public class UnitekCustomEmailManager extends MailManager {
	public UnitekCustomEmailManager() {
		this.setManagerInfo(new MailManagerInfo());
		getManagerInfo().setUserName("support_us@unitek-products.com");
		getManagerInfo().setPassword("Sbpi231Y");
		getManagerInfo().setMailServerHost("pop.unitek-products.com");
		getManagerInfo().setMailSmtpHost("smtp.unitek-products.com");
		getManagerInfo().setTimout(100000);
	}
}