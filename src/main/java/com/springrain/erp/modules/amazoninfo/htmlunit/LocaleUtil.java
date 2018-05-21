package com.springrain.erp.modules.amazoninfo.htmlunit;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.springrain.erp.common.utils.DateUtils;

/**
 * 国际化转换类；
 * 
 * @author bob
 * 
 */
public class LocaleUtil {
	
	/**
	 * 英文简写（默认）如：2010-12-01
	 */
	public static String FORMAT_SHORT = "yyyy-MM-dd";

	/**
	 * 英文全称 如：2010-12-01 23:15:06
	 */
	public static String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";


	// 欧元区的价格模式；
	/*
	 * public static final String EUR_PRICE = "EUR ###,###.00"; public static
	 * final String uk = "￥ ###,###,###.00";
	 */
	public static final String NUM_PATTERN = "###,###,###.00";

	/**
	 * 根据不同国家的时间差别获的给定的样式的的时间值； 该方法待改进；
	 * 
	 * @param str
	 * @param pattern
	 *            时间样式如"YYYY
	 * @param 地区
	 *            ；
	 */
	public static Date formatDate(String str, String pattern, Locale locale) {
		DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, locale);
		str = str.replace("September","Sep").replace("Sept","Sep");
		if(locale!=Locale.JAPAN&&str.length()>2){
			str = str.substring(2).trim();
		}
		if(locale==Locale.CANADA){
			format = new SimpleDateFormat("MMM dd yyyy",locale);
			str = str.replace(".", "").replace(",", "");
		}else if(locale == Locale.UK){
			str = str.replace(".", "");
		}else if(locale==Locale.ITALY){
			format = new SimpleDateFormat("dd MMM yyyy",locale);
		}
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			str = str.replace(".", "");
			if(locale == Locale.JAPAN){
				format = new SimpleDateFormat("yyyy年MM月dd日",locale);
			}else{
				format = new SimpleDateFormat("MMM dd yyyy",locale);
			}
			try {
				date = format.parse(str);
			} catch (ParseException e1) {
				format = new SimpleDateFormat("dd MMM yyyy",locale);
				try {
					date = format.parse(str);
				} catch (ParseException e2) {
				}
			}
		}
		return date;
	}
	
	
	/**
	 * 根据不同国家的时间差别获的给定的样式的的时间值； 该方法待改进； 评论人的评论
	 * 
	 * @param str
	 * @param pattern
	 *            时间样式如"YYYY
	 * @param 地区
	 *            ；
	 */
	public static Date formatDateReviewers(String str, String pattern, Locale locale) {
		DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, locale);
		str = str.replace("September","Sep").replace("Sept","Sep");
		if(locale==Locale.CANADA){
			format = new SimpleDateFormat("MMM dd yyyy",locale);
			str = str.replace(".", "").replace(",", "");
		}else if(locale == Locale.UK){
			str = str.replace(".", "");
		}else if(locale == Locale.US){
			str = str.replace(",", "");
		}else if(locale==Locale.ITALY){
			format = new SimpleDateFormat("dd MMM yyyy",locale);
		}
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			str = str.replace(".", "");
			if(locale == Locale.JAPAN){
				format = new SimpleDateFormat("yyyy年MM月dd日",locale);
			}else{
				format = new SimpleDateFormat("MMM dd yyyy",locale);
			}
			try {
				date = format.parse(str);
			} catch (ParseException e1) {
				format = new SimpleDateFormat("dd MMM yyyy",locale);
				try {
					date = format.parse(str);
				} catch (ParseException e2) {
				}
			}
		}
		return date;
	}
	/**
	 * 根据不同国家的时间差别获的给定的样式的的时间值； 该方法待改进；
	 * 
	 * @param str
	 * @param pattern
	 *            时间样式如"YYYY
	 * @param 地区
	 *            ；
	 */
	public static String formatDateByFaq(String str, String pattern, Locale locale) {
		String result = null;
		DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, locale);
		str = str.replace("September","Sep").replace("Sept","Sep");
		if(locale==Locale.CANADA){
			format = new SimpleDateFormat("MMM dd yyyy",locale);
			str = str.replace(".", "");
		}else if(locale == Locale.UK){
			str = str.replace(".", "");
		}else if(locale == Locale.GERMANY){
			str = str.replace("am", "").trim();
		}
		// DateFormat.getDateInstance
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (date != null) {
			result = DateUtils.getDate(date,pattern);
		}

		return result;
	}
	

	/**
	 * 转换价格;
	 * 
	 * @param str
	 * @param 匹配模式
	 * @param 地区
	 * @return
	 */
	public static Double parsePrice(String str, String pattern, Locale locale) {
		Double price = null;
		NumberFormat format = new DecimalFormat(pattern,
				new DecimalFormatSymbols(locale));
		try {
			price = format.parse(str).doubleValue();

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return price;
	}

	public static Integer parseNum(String str, String pattern, Locale locale) {
		Integer price = null;
		NumberFormat format = new DecimalFormat(pattern,
				new DecimalFormatSymbols(locale));
		try {
			price = format.parse(str).intValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return price;
	}
	
	public static void main(String[] args) {
		Date a = formatDate("am 10. Oktober 2015", "yyyy-mm-dd", Locale.GERMANY);
		System.out.println(a);
	}
}
