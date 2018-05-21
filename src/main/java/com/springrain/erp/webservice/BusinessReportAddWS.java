package com.springrain.erp.webservice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.modules.amazoninfo.entity.BusinessReport;
import com.springrain.erp.modules.amazoninfo.service.BusinessReportService;

@WebService
public class BusinessReportAddWS {
	
	@Autowired
	private BusinessReportService businessReportService;
	
	private static String key = Global.getConfig("ws.key");
	
	private static DateFormat  format= new SimpleDateFormat("yyyy-MM-dd");
	
	
	public boolean addBusinessReport(String key,final String country,String dataDate,byte[]csvData){
		if(BusinessReportAddWS.key.equals(key)){
			try {
				CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(csvData),"utf-8"));
				final String country2 = country;
				Date dataDate2 = null;
				try {
					dataDate2 = format.parse(dataDate);
				} catch (ParseException e1) {
					e1.printStackTrace();
					try {
						reader.close();
					} catch (IOException e) {}
					return false;
				}
				final Date dataDate3 = dataDate2;
				try {
					List<String[]> data = reader.readAll();
					reader.close();
					if(data.size()>0){
						data.remove(0);
					}
					final Date today = new Date();
					businessReportService.saveAll(Collections2.transform(data, new Function<String[],BusinessReport>() {
						public BusinessReport apply(String[] input) {
							if(input.length >= 12){
								if("com".equals(country)||"de".equals(country)){
									Integer ordersPlaced = getNum(input[14]) ;
									Integer sessions = getNum(input[3]);
									Float conversion = 0f;
									if(ordersPlaced!=null && sessions!=null && sessions>0){
										conversion = (ordersPlaced/(float)sessions)*100;
										conversion = new BigDecimal(conversion).setScale(2,BigDecimal.ROUND_HALF_DOWN).floatValue();
									}
									return new BusinessReport(country2, dataDate3,today,input[0], input[1], 
											input[2],sessions,getFloatPercent(input[4]), getNum(input[5]), getFloatPercent(input[6]),
											getIntPercent(input[7]), getNum(input[8]), getFloatPercent(input[10]), getPrice(input[12],country2),ordersPlaced,conversion,null,null,null,null,null);
								}else{
									Integer ordersPlaced = getNum(input[11]) ;
									Integer sessions = getNum(input[3]);
									Float conversion = 0f;
									if(ordersPlaced!=null && sessions!=null && sessions>0){
										conversion = (ordersPlaced/(float)sessions)*100;
										conversion = new BigDecimal(conversion).setScale(2,BigDecimal.ROUND_HALF_DOWN).floatValue();
									}
									return new BusinessReport(country2, dataDate3,today,input[0], input[1], 
											input[2],sessions,getFloatPercent(input[4]), getNum(input[5]), getFloatPercent(input[6]),
											getIntPercent(input[7]), getNum(input[8]), getFloatPercent(input[9]), getPrice(input[10],country2),ordersPlaced,conversion,null,null,null,null,null);
								}
							}else{
								return null;
							}
						};
					}));
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			} catch (UnsupportedEncodingException e) {}
		}
		return false;
	}
	
	private Integer getNum(String numStr){
		if(numStr==null){
			return null;
		}else{
			String temp = numStr.replaceAll("[,| |\\.|\\s]", "");
			if(temp.length()==0){
				return null;
			}else{
				try {
					return Integer.parseInt(temp);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		}	
	}
	
	private Float getFloatPercent(String percent){
		if(percent==null){
			return null;
		}else{
			String temp = percent.replaceAll(",", ".").replaceAll("\\s", "").replaceAll("%", "");
			if(temp.length()==0){
				return null;
			}else{
				try {
					return Float.parseFloat(temp);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		}	
	}
	
	private Integer getIntPercent(String percent){
		if(percent==null){
			return null;
		}else{
			String temp = percent.replaceAll("\\s", "").replaceAll("%", "");
			if(temp.length()==0){
				return null;
			}else{
				try {
					return Integer.parseInt(temp);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		}	
	}
	
	private  Float getPrice(String price,String country){
		if(price==null){
			return null;
		}else{
			String temp = price.replaceAll("[¥|￥|$|€|\\s|,|\\.|£|Can$|MXN$]", "");
			if(temp.length()==0){
				return null;
			}else{
				if(temp.length()>=3){
					if(!"jp".equals(country)){
						temp = temp.substring(0, temp.length()-2)+"."+temp.substring(temp.length()-2);
					}
					try {
						return Float.parseFloat(temp);
					} catch (NumberFormatException e) {
						return null;
					}
				}else if("0".equals(temp)){
					return 0f;
				}else{
					return null;
				}
			}
		}	
	}
}
