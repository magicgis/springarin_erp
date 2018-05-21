/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.springrain.erp.common.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Lists;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * 
 * @author ThinkGem
 * @version 2013-3-15
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(DateUtils.class);

	private static String[] parsePatterns = { "yyyy-MM-dd",
			"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd",
			"yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" };

	private static long CONST_WEEK = 3600 * 1000 * 24 * 7;

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}

	public static String getBeforeDate(String pattern){
		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTime(new Date());//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
		return DateFormatUtils.format(calendar.getTime(), pattern);
	}
	
	public static Date getDate(Integer millLong) {
		return new Date(millLong*1000L);
	}
	
	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}

	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}

	/**
	 * 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null) {
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * 
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		long t = new Date().getTime() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}
	
	/**
	 * 获取过去的天数
	 * 
	 * @param date
	 * @return
	 * @throws ParseException 
	 */
	public static long pastDaysByStr(String dateStr) throws ParseException {
		SimpleDateFormat sdf=new SimpleDateFormat(parsePatterns[0]);
		Date date = sdf.parse(dateStr);
		long t = date.getTime()-sdf.parse(sdf.format(new Date())).getTime();
		return t / (24 * 60 * 60 * 1000);
	}
	
	/**
	 * 两个时间的差值
	 * @param date
	 * @return
	 */
	public static long spaceDays(Date before,Date after) {
		long t = after.getTime() - before.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	//由月份  获取月份英文简称
	public static String getShortMonth(Integer i){
		String monthName ="";
		if(i==1){
			monthName="Jan";	
		}else if(i==2){
			monthName="Feb";
		}else if(i==3){
			monthName="Mar";
		}else if(i==4){
			monthName="Apr";
		}else if(i==5){
			monthName="May";
		}else if(i==6){
			monthName="Jun";
		}else if(i==7){
			monthName="Jul";
		}else if(i==8){
			monthName="Aug";
		}else if(i==9){
			monthName="Sept";
		}else if(i==10){
			monthName="Oct";
		}else if(i==11){
			monthName="Nov";
		}else if(i==12){
			monthName="Dec";
		}
		return monthName;
	}
	
	
	public static Date getDateStart(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date getDateEnd(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(Date date, String pattern) {
		return DateFormatUtils.format(date, pattern);
	}

	public static boolean isFuture(String date) {
		try {
			return new SimpleDateFormat("yyyy/MM/dd").parse(date).after(
					new Date());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

	public static int subtraction(String firstString, String secondString) {
		Date firstDate = null;
		Date secondDate = null;
		try {
			firstDate = df.parse(firstString);
			secondDate = df.parse(secondString);
		} catch (Exception e) {
			return -1;
			// 日期型字符串格式错误
		}
		int nDay = (int) ((firstDate.getTime() - secondDate.getTime()) / (24 * 60 * 60 * 1000));
		return nDay;
	}

	public static Date ISO8601ToDate(String ISO8601) {
		if (StringUtils.isNotEmpty(ISO8601)) {
			return new Date(new DateTime(ISO8601).getMillis());
		}
		return null;
	}

	/**
	 * 取得当前日期是多少周
	 * 
	 * @param date
	 * @return
	 */
	public static int getWeekOfYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		/**
		 * 设置一年中第一个星期所需的最少天数，例如，如果定义第一个星期包含一年第一个月的第一天，则使用值 1 调用此方法。
		 * 如果最少天数必须是一整个星期，则使用值 7 调用此方法。
		 **/
		c.setMinimalDaysInFirstWeek(7);
		c.setTime(date);

		return c.get(Calendar.WEEK_OF_YEAR);
	}
	
	

	/**
	 * 得到某年某周的第一天
	 * 
	 * @param year
	 * @param week
	 * @return
	 */
	public static Date getFirstDayOfWeek(int year, int week) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		if(year==2017){
			week+=1;
		}
		c.set(Calendar.WEEK_OF_YEAR, week);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		c.setFirstDayOfWeek(Calendar.MONDAY); // 设置周一

		return c.getTime();
	}

	/**
	 * 得到某年某周的最后一天
	 * 
	 * @param year
	 * @param week
	 * @return
	 */
	public static Date getLastDayOfWeek(int year, int week) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		if(year==2017){
			week+=1;
		}
		c.set(Calendar.WEEK_OF_YEAR, week);
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
		return c.getTime();
	}

	/**
	 * 获取当前日期 星期一的时间
	 * 
	 */
	public static Date getMonday(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return c.getTime();
	}

	/**
	 * 获取当前日期 星期天的时间
	 * 
	 */
	public static Date getSunday(Date date) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		//c.add(Calendar.DAY_OF_MONTH, 7);
		return c.getTime();
	}
	
	/**
	 *获取当前周的开始和结束时间 
	 * week:201709
	 */
	public static String getWeekStartEnd(String yearWeek){
		if(StringUtils.isNotEmpty(yearWeek)){
			int year = Integer.parseInt(yearWeek.substring(0, 4));
			int week = Integer.parseInt(yearWeek.substring(4));
			Date firstDay = DateUtils.getFirstDayOfWeek(year, week);
			return DateUtils.getDate(firstDay,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(firstDay),"yyyy-MM-dd");
		}else{
			return "";
		}
		
	}
	

	/**
	 * 获取某年某月第一一天日期
	 * 
	 * @param year
	 *            年份
	 * @return Date
	 */
	public static Date getLastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		return cal.getTime();
	}

	/**
	 * 获取某年某月最后一天日期
	 * 
	 * @param year
	 *            年份
	 * @return Date
	 */
	public static Date getFirstDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		return cal.getTime();
	}

	

	/**
	 * 获取某年第一天日期
	 * 
	 * @param year
	 *            年份
	 * @return Date
	 */
	public static Date getYearFirst(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date currYearFirst = calendar.getTime();
		return currYearFirst;
	}

	/**
	 * 获取某年最后一天日期
	 * 
	 * @param year
	 *            年份
	 * @return Date
	 */
	public static Date getYearLast(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		Date currYearLast = calendar.getTime();

		return currYearLast;
	}

	
	
	/**
	 * 获取指定月第一天
	 * 
	 */
	public static Date getFirstDayOfMonth(int year, int month)
			throws ParseException {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		int value = c.getActualMinimum(Calendar.DAY_OF_MONTH);
		c.set(Calendar.DAY_OF_MONTH, value);
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		return c.getTime();
	}
	
	/**
	 * 获取指定月第一天
	 * 
	 */
	public static Date getLastDayOfMonth(int year, int month)
			throws ParseException {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		int value = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		c.set(Calendar.DAY_OF_MONTH, value);
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		return c.getTime();
	}

	/**
	 * 获取指定月第一天
	 * 
	 */
	public static List<String> toReverseInterval(Date start,Date end,String[] dates){
		List<String> datesList =  Lists.newArrayList(dates);
		List<String> rs = Lists.newArrayList();
		String interval = "";
		while(!start.after(end)){
			String dateStr =  getDate(start, "yyyy/MM/dd");
			if(!datesList.contains(dateStr)){
				if(interval.length()==0){
					interval = dateStr;
				}
			}else{
				if(interval.length()>0){
					String endStr = getDate(DateUtils.addDays(start, -1), "yyyy/MM/dd");
					if(!interval.equals(endStr)){
						rs.add(interval+"-"+endStr);
					}else{
						rs.add(endStr);
					}	
					interval = "";
				}
			}
			start = DateUtils.addDays(start, 1);
		}
		if(interval.length()>0){
			String dateStr=getDate(end, "yyyy/MM/dd");
			if(!dateStr.equals(interval)){
				rs.add(interval+"-"+dateStr);
			}else{
				rs.add(interval);
			}
		}
		
		
		return rs;
	}
	
	
	/***
	 * 获取连续月份
	 * start：201508
	 */
	public static List<String> getContineMonthList(String start,String end) throws ParseException{
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMM");
		Date startDate=sdf.parse(start);
		Date endDate=sdf.parse(end);
		List<String> rs =Lists.newArrayList();
		rs.add(start);
		while(startDate.before(endDate)){
			startDate = DateUtils.addMonths(startDate, 1);
			rs.add(sdf.format(startDate));
		}
		return rs;
	}
	
	public static int getDayOfMonth(){
		Calendar c = Calendar.getInstance();
		return c.getActualMaximum(Calendar.DATE);
	}
	
	/**
	 * 
	 * @param date
	 * @param formatWeek
	 * @param start	分割起始位置 如yyyy-ww起始位置为5 yyyyww起始位置为4
	 * @param connect	连接符号如yyyy-ww连接符号为‘-’ yyyyww连接符号为空字符串
	 * @return
	 */
	public static String getWeekStr(Date date, DateFormat formatWeek, int start, String connect){
		String key = formatWeek.format(date);
		if (StringUtils.isNotEmpty(connect)) {
			String[] dateStrs = key.split(connect);
			if (date.getMonth()==11 && "01".equals(dateStrs[1])) {
				Integer year = date.getYear() + 1901;
				key = year + connect + dateStrs[1];
			}
		} else {
			int year =DateUtils.getSunday(date).getYear()+1900;
			int week =  Integer.parseInt(key.substring(start));
			if(week<10){
				key = year+connect+"0"+week;
			}else{
				key =year+connect+""+week;
			}
		}
		return key;
	}
	
	/**
	 * 
	 * @param date 2015-52 201552
	 * @param start	分割起始位置 如yyyy-ww起始位置为5 yyyyww起始位置为4
	 * @param connect	连接符号如yyyy-ww连接符号为‘-’ yyyyww连接符号为空字符串
	 * @return
	 */
	public static String getWeekStr(String date, int start, String connect, boolean add){
		Integer i = Integer.parseInt(date.substring(start));
		if(i==53){
			Integer year = Integer.parseInt(date.substring(0,4));
			date =  (year+1)+connect+"01";
		}else if (add && date.contains("2016")){
			i = i+1;
			date = "2016"+connect+(i<10?("0"+i):i);
		}
		return date;
	}
	
	/**
	 * true 周末
	 * @return
	 */
	public static Boolean isHoliday(){
		return isHoliday(new Date());
	}
	
	//手动指定中秋国庆时段的非周末假日
	private static List<String> list = Lists.newArrayList("20170123", "20170124", "20170125", "20170126");
	
	/**
	 * 查询指定时间是否为节假日true 周末
	 * @return
	 */
	public static Boolean isHoliday(Date date){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		if (list.contains(format.format(date))) {
			return true;
		}
		/*WebClient client = new WebClient();
		WebClientOptions options = client.getOptions();
		options.setTimeout(30000);
		options.setJavaScriptEnabled(false);
		options.setActiveXNative(false);
		options.setCssEnabled(false);
		options.setPopupBlockerEnabled(false);
		options.setThrowExceptionOnFailingStatusCode(false);
		options.setThrowExceptionOnScriptError(false);
		options.setPrintContentOnFailingStatusCode(false);
		client.waitForBackgroundJavaScript(30000);
		try {
			HtmlPage page = client.getPage("http://tool.bitefu.net/jiari/?d="+format.format(date));
			if (page == null) {
				return (date.getDay()==0 || date.getDay()==6);
			} else if ("0".equals(page.asText().trim())) {
				return false;
			} else if ("1".equals(page.asText().trim()) || "2".equals(page.asText().trim())) {
				return true;
			} else {
				return (date.getDay()==0 || date.getDay()==6);
			}
		} catch (FailingHttpStatusCodeException e) {
			logger.error("节假日接口调用错误", e);
		} catch (MalformedURLException e) {
			logger.error("节假日接口调用错误", e);
		} catch (IOException e) {
			logger.error("节假日接口调用错误", e);
		}*/
		return (date.getDay()==0 || date.getDay()==6);
	}

	/**
	 * 取得制定日期是多少周，按特定格式返回字符串
	 * @param date
	 * @return
	 */
	public static String getWeekOfYearStr(Date date) {
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setMinimalDaysInFirstWeek(1);
		c.setTime(date);
		return c.get(Calendar.YEAR) + "-WK" + c.get(Calendar.WEEK_OF_YEAR);

	}

	/**
	 * 取得0点0分0秒的时间,默认为当天0点时间
	 * @param date
	 * @return
	 */
	public static Date getZeroTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		//System.out.println(getLastDayOfWeek(2015,10).toLocaleString());
		// System.out.println(formatDate(parseDate("2010/3/6")));
		// System.out.println(getDate("yyyy年MM月dd日 E"));
		// long time = new Date().getTime()-parseDate("2012-11-19").getTime();
		// System.out.println(time/(24*60*60*1000));
		// System.out.println(getWeek());
		// System.out.println(getMonth());

		// System.out.println(isFuture("2014/3/30"));
		//System.out.println(toReverseInterval(DateUtils.addDays(getDateStart(new Date()),-30),getDateStart(new Date()),"2015/08/02,2015/08/03,2015/08/05,2015/08/06".split(",")));
		//System.out.println(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(new Date(), 2)));
		System.out.println(isHoliday());
	}
}
