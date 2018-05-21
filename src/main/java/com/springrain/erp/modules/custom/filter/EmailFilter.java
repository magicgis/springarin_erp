package com.springrain.erp.modules.custom.filter;

import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.search.SearchTerm;

import org.springframework.stereotype.Component;

import com.springrain.erp.common.email.MailManager;

@Component
public class EmailFilter extends SearchTerm{

	private static final long serialVersionUID = 1L;
	
	private static final String[] filter = new String[]{"konto-aktualisierung@amazon.+","fba-ship-confirm@amazon.+","fba-ca-noreply@amazon\\.ca",
			"auto-communication@amazon.+","amazon-eu-webinars@amazon\\.com","fba-noreply@amazon\\.com","noreply@youtube\\.com","store-news@amazon\\.com","no-reply@amazon\\.com"
			,"amazon-tutor@amazon\\.it","tutor-ventas-amazon@amazon\\.es","aide-ventes-amazon@amazon\\.fr","10000@qq\\.com","mailer-daemon@googlemail\\.com","member@paypal\\.com","ebay@ebay\\.com","service@paypal\\.com","do\\_not\\_reply@dpd\\.com"};
	@Override
	public boolean match(Message msg) {
		try {
			String from = MailManager.getFrom(msg);
			for (String rule : filter) {
				if(Pattern.matches(rule, from)){
					return false;
				}
			}
		} catch (Exception e) {
		}
		return true;
	}
	
	
	public static void main(String[] args) {
		String from ="do_not_reply@dpd.com";
		for (String rule : filter) {
			if(Pattern.matches(rule, from)){
				 System.out.println("111");
			}
		}

	}
	
}
