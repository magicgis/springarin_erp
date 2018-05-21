package com.springrain.erp.modules.amazoninfo.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.springrain.erp.common.utils.HttpRequest;
import com.springrain.erp.modules.psi.service.FbaInboundService;
import com.springrain.erp.modules.psi.service.PsiMarketingPlanService;

@Component
public class LogisticsInfoMonitor {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	private SimpleDateFormat sdff = new SimpleDateFormat("dd.MM.yy");
	@Autowired
	private FbaInboundService fbaService ;
	@Autowired PsiMarketingPlanService planService;
	private void trapEta() throws ParseException{
		logger.info("营销计划实际促销数量更新开始");
			this.planService.updateRealQuantity();
		logger.info("营销计划实际促销数量更新结束");
		
		logger.info("扫描港口到fba仓库eta开始");
		 Map<String,List<String>> supplierMap =fbaService.getTrackInfos();
		 int i =0;
		 for (Map.Entry<String,List<String>> entry : supplierMap.entrySet()) { 
		     String supplierType=entry.getKey();
		     List<String> list=entry.getValue();
			 for(String trackInfo:list){
				 String str="";
				 try{
					 String arr[] =trackInfo.split(",,");
					 Integer id = Integer.parseInt(arr[0]);
					 String tranNo = arr[1].toString();
					 String rsStr="";
					 if("DPD".equals(supplierType)){
						String url="https://tracking.dpd.de/cgi-bin/simpleTracking.cgi?parcelNr="+tranNo+"&locale=en_D2&type=1&jsoncallback=_jqjsp";
						String rs =HttpRequest.reqUrlStr(url, null,false);
						str=rs;
						//组成json
						if(StringUtils.isNotEmpty(rs)&&rs.contains("_jqjsp(")){ 
							String jsonStr =rs.trim().replace("_jqjsp(", "");
							jsonStr = jsonStr.substring(0, jsonStr.length()-1);
							JSONObject json =JSON.parseObject(jsonStr);
							if(json!=null&&json.getJSONObject("TrackingStatusJSON")!=null&&json.getJSONObject("TrackingStatusJSON").getJSONObject("shipmentInfo")!=null){
								String staStr=json.getJSONObject("TrackingStatusJSON").getJSONObject("shipmentInfo").getString("deliveryStatusMessage");
								if(StringUtils.isNotEmpty(staStr)){
									//Your parcel was delivered on 15.04.2016, at 10:29  Your parcel was delivered on 08.04.2016.
									rsStr = staStr.replace("Your parcel was delivered on ", "").substring(0, 10);
								}
							}
							if(StringUtils.isNotEmpty(rsStr)){
								i++;
								Date toAmaDate =sdf.parse(rsStr);
								this.fbaService.upToAmaDate(id, toAmaDate);
							}
						}
					 }else if("DHL-FREE".equals(supplierType)){
						String url ="https://nolp.dhl.de/nextt-online-public/set_identcodes.do?lang=en&rfn=&extendedSearch=true&idc="+tranNo;
						Document doc = HttpRequest.reqUrl(url, null, false);
						String cnt=doc.text();
						if(cnt.contains("Delivery successful")&&cnt.contains("has been delivered")){//Sat, 17.03.18 01:23 Germany
							Element ele=doc.getElementById("events-content-0");
							String dateStr = ele.getElementsByTag("dt").get(0).text();
							rsStr=dateStr.substring(dateStr.indexOf(",")+2).substring(0, 8);
						}
						/*Elements  eles = doc.getElementsByClass("well-status");
						if(eles!=null&&eles.size()==1){
							Element ele =eles.get(0);
							if(ele.text().contains("has been delivered")){
								String dateStr=ele.getElementsByTag("h2").text();
								rsStr=dateStr.substring(dateStr.indexOf(",")+2).substring(0, 8);
							}
						}*/
						if(StringUtils.isNotEmpty(rsStr)){
							i++;
							Date toAmaDate =sdff.parse(rsStr);
							this.fbaService.upToAmaDate(id, toAmaDate);
						}
					}
					 
					
				 }catch(Exception ex){
					 logger.error("更新德国发往fba时间失败："+ex.getMessage()+trackInfo+",返回字符串："+str,ex);
					 ex.printStackTrace();
				 }
			 }
		 }
		 logger.info("扫描港口到fba仓库eta结束,影响（"+i+"）个帖子");
	}

//	public static void main(String [] arr) throws ParseException, IOException{
//	    SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yy");
//		String rsStr="";
//		Date toAmaDate=null;
//		String tranNo="520159758158";
//		String url ="https://nolp.dhl.en/nextt-online-public/set_identcodes.do?lang=en&rfn=&extendedSearch=true&idc="+tranNo;
//		
//		Document doc = HttpRequest.reqUrl(url, null, false);
//		if(doc!=null){
//			Elements  eles = doc.getElementsByClass("well-status");
//			if(eles!=null&&eles.size()==1){
//				Element ele =eles.get(0);
//				if(ele.getElementsByTag("p").text().contains("has been delivered")){
//					//Status from Wed, 20.04.2016 02:58
//					//Status From Sat, 11.06.16 01:56
//					String dateStr=ele.getElementsByTag("h2").text();
//					rsStr=dateStr.substring(dateStr.indexOf(",")+2).substring(0, 8);
//				}
//			}
//			if(StringUtils.isNotEmpty(rsStr)){
//				toAmaDate =sdf1.parse(rsStr);
//			}
//		}
//		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//		String rs=FileUtils.readFileToString(new File("c:/xx.txt")); //  01045215909030, 2UCTWMCH
//			String url="https://tracking.dpd.de/cgi-bin/simpleTracking.cgi?parcelNr="+"01045215908499"+"&locale=en_D2&type=1&jsoncallback=_jqjsp";
//			String rs =HttpRequest.reqUrlStr(url, null,false);
			//组成json
//			String jsonStr =rs.replace("_jqjsp(", "");
//			jsonStr = jsonStr.substring(0, jsonStr.length()-1);
//			JSONObject json =JSON.parseObject(jsonStr);
//			if(json!=null&&json.getJSONObject("TrackingStatusJSON")!=null&&json.getJSONObject("TrackingStatusJSON").getJSONObject("shipmentInfo")!=null){
//				String staStr=json.getJSONObject("TrackingStatusJSON").getJSONObject("shipmentInfo").getString("deliveryStatusMessage");
//				String rsStr="";
//				if(StringUtils.isNotEmpty(staStr)){
//					//Your parcel was delivered on 15.04.2016, at 10:29  Your parcel was delivered on 08.04.2016.
//					rsStr = staStr.replace("Your parcel was delivered on ", "").substring(0, 10);
//				}
//				if(StringUtils.isNotEmpty(rsStr)){
//					Date toAmaDate =sdf.parse(rsStr);
//					System.out.println(toAmaDate);
//				}
//			}
//		
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring-context.xml");
//		LogisticsInfoMonitor info = context.getBean(LogisticsInfoMonitor.class);
//		info.trapEta();
		
//	}

}
