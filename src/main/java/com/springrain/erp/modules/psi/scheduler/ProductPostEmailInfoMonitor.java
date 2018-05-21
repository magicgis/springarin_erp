package com.springrain.erp.modules.psi.scheduler;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductPostMailInfoService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;

@Component
public class ProductPostEmailInfoMonitor {
	
	@Autowired
	private AmazonProduct2Service      		product2Service;
	@Autowired
	private MailManager 					mailManager;
	@Autowired
	private PsiProductTypeGroupDictService  typeGroupService;
	@Autowired
	private PsiProductGroupUserService      groupService;
	@Autowired
	private PsiProductPostMailInfoService   postService;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	public void exeWarnMonitor() throws IOException, ParseException{
		try{
			logger.info("主力、新品发送售后邮件监控 start");
			postService.addProductPostMailInfos();
			//获取所有待发信产品  
			Map<String,Set<String>> unSendMap =this.postService.getUnSendProducts();
			this.sendEmai(unSendMap);
			
			logger.info("主力、新品发送售后邮件监控 end");
		}catch(Exception ex){
			logger.error("主力、新品发送售后邮件监控 "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	private void sendEmai(Map<String,Set<String>> rsMap){
		try{
			Map<String,String>  prodouctLineMap = this.typeGroupService.getLineNameByName();
			Map<String,String>  userLineMap=groupService.getEmailProductLine();
			if(rsMap!=null&&rsMap.size()>0){
				Map<String,Map<String,Set<String>>> lineCountryMap = Maps.newHashMap();
				for (Map.Entry<String,Set<String>> entry : rsMap.entrySet()) { 
				    String country=entry.getKey();
					Set<String> productNames = entry.getValue();
					if(productNames==null||productNames.size()==0){
						continue;
					}
					for(String proInfo:productNames){
						String arr[] = proInfo.split(",");
						String productName = arr[0];
						if(StringUtils.isEmpty(prodouctLineMap.get(productName))){
							continue;
						}
						String lineName=prodouctLineMap.get(productName);
						Map<String,Set<String>> countryMap = null;
						if(lineCountryMap.get(lineName)==null){
							countryMap=Maps.newHashMap();
						}else{
							countryMap=lineCountryMap.get(lineName);
						}
						Set<String> proSets = null;
						if(countryMap.get(country)==null){
							proSets=Sets.newHashSet();
						}else{
							proSets=countryMap.get(country);
						}
						proSets.add(proInfo);
						countryMap.put(country, proSets);
						lineCountryMap.put(lineName, countryMap);
					}
				}
				
				Date date = new Date();
				for (Map.Entry<String,Map<String,Set<String>>> entry : lineCountryMap.entrySet()) { 
					String lineName = entry.getKey();
					Map<String,Set<String>> countryMap = entry.getValue();
					
					StringBuffer contents= new StringBuffer("");
					contents.append("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;以下是主力产品4星以上、新品累计销量大于100的未发售后邮件的产品("+lineName+")：<br/><table width='90%' style='border:1px solid #cad9ea;color:#666; '>" +
							"<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>国家</th><th>产品名</th><th>新品/主力</th></tr>");
					for (Map.Entry<String,Set<String>> entryCountry : countryMap.entrySet()) { 
					    String country=entryCountry.getKey();
					    int i=0;
						for(String proInfo:entryCountry.getValue()){
							String arr[] = proInfo.split(",,,,");   
							String productName = arr[0];
							String color="#f5fafe";
							if(i==0){
								color="#99CCFF";
							}
							String tips = "0".equals(arr[1])?"主力":"新品";
							contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:"+color+"; '><td>"+("com".equals(country)?"us":country)+
									"</td><td>"+productName+"</td><td>"+tips+"</td></tr>");
							i++;
						}
					}
					
					contents.append("</table>");
					//发信：
					String toAddress=userLineMap.get(lineName)+",ethan@inateck.com";
					final MailInfo mailInfo = new MailInfo(toAddress,"满足条件未发送售后邮件的产品提醒("+lineName+")"+DateUtils.getDate("-yyyy/M/dd"),date);
					mailInfo.setContent(contents.toString());
					new Thread(){
						public void run(){
							 mailManager.send(mailInfo);
						}
					}.start();
				}
					
				
			}
				
		}catch(Exception e){
			logger.error("首页含中差评预警异常"+e);
			e.printStackTrace();
		}
	}	
	
//	public static void main(String[] args) throws Exception {
//	ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
//	ProductPostEmailInfoMonitor advertisingService = applicationContext.getBean(ProductPostEmailInfoMonitor.class);
//	advertisingService.exeWarnMonitor();
//	
//}
}
