package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;

/**
 * session和转化率监控Entity DTO
 * @author Tim
 * @version 2015-02-09
 */
public class SessionMonitorResultDto implements Serializable,Comparable<SessionMonitorResultDto> {
	
	private static final long serialVersionUID = 1L;
	
	private String productName;
	
	private Integer sessions;
	private Integer sessionsByDate;
	private Float conver;
	private Map<String,String> links;
	private String country;
	
	private Float price;
	
	private String searchFlag;
	private Date month;
	
	
	private Map<String,SessionMonitor> asins = Maps.newHashMap();
	
	public SessionMonitorResultDto() {
		super();
	}

	public SessionMonitorResultDto(String country,String productName, Integer sessions,
			Integer sessionsByDate, Float conver) {
		super();
		this.country = country;
		this.productName = productName;
		this.sessions = sessions;
		this.sessionsByDate = sessionsByDate;
		this.conver = conver;
	}

	public SessionMonitorResultDto(Integer sessions,Float conver) {
		super();
		this.sessions = sessions;
		this.conver = conver;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getSessions() {
		return sessions;
	}

	public void setSessions(Integer sessions) {
		this.sessions = sessions;
	}

	public Integer getSessionsByDate() {
		if(sessionsByDate!=null){
			int sessionsByDate1 = 0;
			for (String asin : asins.keySet()) {
				if(asins.get(asin)!=null){
					sessionsByDate1 +=(asins.get(asin).getProductId()==null?0:asins.get(asin).getProductId());
				}
			}
			int i = getMonthMaxDays(month)-month.getDate();
			if(i!=0){
				BigDecimal  temp =   new  BigDecimal((float)(sessions-sessionsByDate1)/i);
				sessionsByDate1 =  temp.setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
			}else{
				sessionsByDate1 = sessions-sessionsByDate1;
			}
			return sessionsByDate1;
		}
		return sessionsByDate;
	}

	public void setSessionsByDate(Integer sessionsByDate) {
		this.sessionsByDate = sessionsByDate;
	}

	public Float getConver() {
		return conver;
	}

	public void setConver(Float conver) {
		this.conver = conver;
	}

	public Map<String, SessionMonitor> getAsins() {
		return asins;
	}

	public void setAsins(Map<String, SessionMonitor> asins) {
		this.asins = asins;
	}

	public Map<String, String> getLinks() {
		if(links==null&&asins.size()>0){
			links = Maps.newHashMap();
			String suff = country;
			if("uk,jp".contains(country)){
				 suff = "co."+suff;
			}else if("mx".equals(country)){
				suff = "com."+suff;
			}
			suff = "http://www.amazon."+suff+"/dp/";
			for (String asin : asins.keySet()) {
				links.put(asin,suff+asin);
			}
		}
		return links;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	private Integer realSessions;
	
	public Integer getRealSessions(){
		if(realSessions==null){
			realSessions = 0;
			for (String asin : asins.keySet()) {
				if(asins.get(asin)!=null){
					realSessions +=asins.get(asin).getSessions();
				}
			}
		}
		return realSessions;
	}
	
	public Double getRealConver(){
		realSessions = getRealSessions();
		if(realSessions>0){
			int orders = 0;
			for (String asin : asins.keySet()) {
				if(asins.get(asin)!=null){
					orders +=asins.get(asin).getSessionsByDate();
				}
			}
			double conver = orders*100/(double)realSessions;
			BigDecimal  temp =   new  BigDecimal(conver);
			conver =  temp.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
			return conver;
		}
		return 0d;
	}
	
	public Boolean getIsPass(){
		Boolean rs = null;
		if("2".equals(searchFlag)&&sessions!=null){
			rs = getRealSessions()>=sessions;
		}else if("0".equals(searchFlag)&&sessionsByDate!=null){
			rs = getRealSessions()>=getSessionsByDate();
		}
		if(conver!=null&&((rs!=null&&rs)||rs==null)){
			rs = getRealConver()>=conver.doubleValue();
		}
		return rs;
	}
	
	
	public double getProductsPrice(){
		if(price==null){
			return 0d;
		}
		int session = 0;
		if(sessions==null&&sessionsByDate==null){
			session = getRealSessions();
			if("2".equals(searchFlag)&&session>0){
				Date date = new Date();
				if("com,ca".contains(country)){
					if(date.getDate()>=4&&date.getYear()==month.getYear()&&date.getMonth()==month.getMonth()){
						int i = date.getDate()-3;
						session = Math.round(session/(float)i)*getMonthMaxDays(date);
					}
				}else{
					if(date.getDate()>=3&&date.getYear()==month.getYear()&&date.getMonth()==month.getMonth()){
						int i = date.getDate()-2;
						session = Math.round(session/(float)i)*getMonthMaxDays(date);
					}
				}
			}
		}else{
			if("2".equals(searchFlag)&&sessions>0){
				session = (sessions==null?0:sessions);
			}else{
				session = (sessionsByDate==null?0:sessionsByDate);
			}
		}
		Double conver1 = 0d;
		if(conver==null){
			conver1 = getRealConver();
		}else{
			conver1 = conver+conver1;
		}
		double rs = 0d;
		if("ca".equals(country)){
			rs = session*conver1*price*AmazonProduct2Service.getRateConfig().get("CAD/EUR")/100; 
		}else if("uk".equals(country)){
			rs = session*conver1*price*AmazonProduct2Service.getRateConfig().get("GBP/EUR")/100;
		}else if("com".equals(country)){
			rs = session*conver1*price/AmazonProduct2Service.getRateConfig().get("EUR/USD")/100;
		}else if("jp".equals(country)){
			rs = session*conver1*price*AmazonProduct2Service.getRateConfig().get("JPY/EUR")/100;
		}else{
			rs = session*conver1*price/100;
		}
		BigDecimal  temp = new  BigDecimal(rs);
		rs =  temp.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		return rs;
	}
	
	@Override
	public int compareTo(SessionMonitorResultDto o) {
		if(o==null){
			return 1;
		}else{
			if ((o.getIsPass() && getIsPass()) || (!o.getIsPass() && !getIsPass())){
				return productName.compareToIgnoreCase(o.getProductName());
			}else{
				return getIsPass().compareTo(o.getIsPass());
			}
		}
	}

	public void setSearchFlag(String searchFlag) {
		this.searchFlag = searchFlag;
	}

	public void setMonth(Date month) {
		this.month = month;
	}
	
	private int getMonthMaxDays(Date date){
		Calendar   calendar   =   Calendar.getInstance();   
	    calendar.set(1900+date.getYear(),date.getMonth(),1);   
	    calendar.roll(Calendar.DATE,   false);   
	    return calendar.get(Calendar.DATE); 
	}
}


