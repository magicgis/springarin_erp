package com.springrain.erp.modules.amazoninfo.htmlunit;

import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public class LoginUtil {


	private static Logger logger = LoggerFactory.getLogger(LoginUtil.class);


	public static WebClient frontRegister(String country, boolean needJs) {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(
				Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient")
				.setLevel(Level.OFF);
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
		return frontRegister(country, needJs, 0);
	}

	private static WebClient frontRegister(String country, boolean needJs,
			int num) {
		if (num == 10) {
			logger.error("final login error:" + "country:" + country);
			return null;
		}
		WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_9);
		HtmlPage page = null;
		String loginURL = null;
		WebClientOptions options = webClient.getOptions();
		options.setTimeout(30000);
		options.setActiveXNative(false);
		options.setCssEnabled(false);
		options.setPopupBlockerEnabled(false);
		options.setThrowExceptionOnFailingStatusCode(false);
		options.setThrowExceptionOnScriptError(false);
		options.setPrintContentOnFailingStatusCode(false);
		webClient.waitForBackgroundJavaScript(30000);

		options.setJavaScriptEnabled(false);

		try {
			String flex = country;
			if ("com".equals(country)) {
				flex = "us";
			} else if ("uk".equals(country)) {
				flex = "gb";
			}
			String stuffix = country;
			if ("jp".equals(country) || "uk".equals(country)) {
				stuffix = "co." + country;
			} else if ("mx".equals(country)) {
				stuffix = "com." + country;
			}
			loginURL = "https://www.amazon."
					+ stuffix
					+ "/ap/signin?_encoding=UTF8&openid.assoc_handle="
					+ flex
					+ "flex&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.mode=checkid_setup&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0";
			page = webClient.getPage(loginURL);
			String buttonId = "signInSubmit";
			/*
			 * if("com".equals(country)){ buttonId="signInSubmit"; }else{
			 * buttonId="signInSubmit-input"; }
			 */

			final HtmlForm form = page.getFormByName("signIn");
			HtmlSubmitInput button = (HtmlSubmitInput) page
					.getElementById(buttonId);
			form.getInputByName("email").setValueAttribute("PO@springrain.eu");// Global.getConfig("email.username"));
			form.getInputByName("password").setValueAttribute("springrain2015");// Global.getConfig("email.password"));
			button.click();
			logger.info(country + " login success");
			return webClient;
		} catch (Exception e) {
			logger.error("error:" + loginURL + "country:" + country, e);
			webClient.closeAllWindows();
			e.printStackTrace();
			num = num + 1;
			return frontRegister(country, needJs, num);
		}

	}

	public static WebClient frontRegister(String country, boolean needJs,
			String loginName, String password) {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(
				Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient")
				.setLevel(Level.OFF);
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
		return frontRegister(country, needJs, 0, loginName, password);
	}

	private static WebClient frontRegister(String country, boolean needJs,
			int num, String loginName, String password) {
		if (num == 10) {
			logger.error("final login error:" + "country:" + country);
			return null;
		}
		WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_9);
		HtmlPage page = null;
		String loginURL = null;
		WebClientOptions options = webClient.getOptions();
		options.setTimeout(30000);
		options.setActiveXNative(false);
		options.setCssEnabled(false);
		options.setPopupBlockerEnabled(false);
		options.setThrowExceptionOnFailingStatusCode(false);
		options.setThrowExceptionOnScriptError(false);
		options.setPrintContentOnFailingStatusCode(false);
		webClient.waitForBackgroundJavaScript(30000);

		options.setJavaScriptEnabled(false);
		// webClient.setAjaxController(new
		// NicelyResynchronizingAjaxController());
		// webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		// webClient.getOptions().setThrowExceptionOnScriptError(false);
		try {
			String flex = country;
			if ("com".equals(country)) {
				flex = "us";
			} else if ("uk".equals(country)) {
				flex = "gb";
			}
			String stuffix = country;
			if ("jp".equals(country) || "uk".equals(country)) {
				stuffix = "co." + country;
			} else if ("mx".equals(country)) {
				stuffix = "com." + country;
			}
			loginURL = "https://www.amazon."
					+ stuffix
					+ "/ap/signin?_encoding=UTF8&openid.assoc_handle="
					+ flex
					+ "flex&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.mode=checkid_setup&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0";
			page = webClient.getPage(loginURL);
			if (page == null) {
				webClient = null;
			}
			String buttonId = "signInSubmit";

			final HtmlForm form = page.getFormByName("signIn");
			HtmlSubmitInput button = (HtmlSubmitInput) page
					.getElementById(buttonId);
			form.getInputByName("email").setValueAttribute(loginName);// Global.getConfig("email.username"));
			form.getInputByName("password").setValueAttribute(password);// Global.getConfig("email.password"));
			button.click();
			logger.info(country + " login success");
			return webClient;
		} catch (Exception e) {
			logger.error("error:" + loginURL + "country:" + country, e);
			webClient.closeAllWindows();
			e.printStackTrace();
			num = num + 1;
			return frontRegister(country,needJs,num,loginName,password);
		}

	}

}
