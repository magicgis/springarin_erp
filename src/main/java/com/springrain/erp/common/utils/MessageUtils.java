package com.springrain.erp.common.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

public class MessageUtils {

	public static String format(String def,String key , Object[] obj){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder
				.getRequestAttributes()).getRequest();
		
		Cookie cookie = WebUtils.getCookie(request, "locale");
		Locale locale = null;
		if(cookie==null){
			locale = Locale.getDefault();
		}else{
			locale = StringUtils.parseLocaleString(cookie.getValue());
		}
		ResourceBundle rb = ResourceBundle.getBundle("/i18n/message_info", locale);
		
		String value = rb.getString(key);
		
		if("".equals(value) || null==value){
			return def;
		}
		
		if(obj!=null){
			return MessageFormat.format(value, obj);
		}else{
			return value;
		}
	}
	
	public static String format(String key , Object[] obj){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder
				.getRequestAttributes()).getRequest();
		
		Cookie cookie = WebUtils.getCookie(request, "locale");
		Locale locale = null;
		if(cookie==null){
			locale = Locale.getDefault();
		}else{
			locale = StringUtils.parseLocaleString(cookie.getValue());
		}
		ResourceBundle rb = ResourceBundle.getBundle("/i18n/message_info", locale);
		
		String value = rb.getString(key);
		
		if("".equals(value) || null==value){
			return "";
		}
		if(obj!=null){
			return MessageFormat.format(value, obj);
		}else{
			return value;
		}
	}
	
	public static String format(String key){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder
				.getRequestAttributes()).getRequest();
		
		Cookie cookie = WebUtils.getCookie(request, "locale");
		Locale locale = null;
		if(cookie==null){
			locale = Locale.getDefault();
		}else{
			locale = StringUtils.parseLocaleString(cookie.getValue());
		}
		ResourceBundle rb = ResourceBundle.getBundle("/i18n/message_info", locale);
		
		String value = rb.getString(key);
		
		if("".equals(value) || null==value){
			return "";
		}
		return value;
	}
	
	public static String format(String def,String key){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder
				.getRequestAttributes()).getRequest();
		
		Cookie cookie = WebUtils.getCookie(request, "locale");
		Locale locale = null;
		if(cookie==null){
			locale = Locale.getDefault();
		}else{
			locale = StringUtils.parseLocaleString(cookie.getValue());
		}
		ResourceBundle rb = ResourceBundle.getBundle("/i18n/message_info", locale);
		String value = rb.getString(key);
		if("".equals(value) || null==value){
			return def;
		}
		return value;
	}
}
