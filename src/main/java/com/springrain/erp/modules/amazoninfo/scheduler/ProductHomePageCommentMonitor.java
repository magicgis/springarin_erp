package com.springrain.erp.modules.amazoninfo.scheduler;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.HttpRequest;
import com.springrain.erp.modules.amazoninfo.service.AmazonFollowSellerService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.scheduler.ProductPostEmailInfoMonitor;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;

@Component
public class ProductHomePageCommentMonitor {
	@Autowired
	private AmazonProduct2Service      		product2Service;
	@Autowired
	private MailManager 					mailManager;
	@Autowired
	private PsiProductTypeGroupDictService  typeGroupService;
	@Autowired
	private ProductPostEmailInfoMonitor      postMonitor;
	@Autowired
	private PsiInventoryService 			 inventoryService;
	@Autowired
	private AmazonFollowSellerService	     followSellerService;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private void scanHomePageComment() throws ParseException{
		try{
			logger.info("库容预警开始...");
			inventoryService.getOverCapacityInfo();
			logger.info("库容预警结束...");
		}catch(Exception ex){
			logger.error("库容预警异常..."+ex.getMessage(),ex);
		}
		
		new Thread(){
			@Override
			public void run(){
				logger.info("跟卖提醒开始...");
				String content = followSellerService.renderData();
				if(StringUtils.isNotEmpty(content)){
					sendNoticeEmail("amazon-sales@inateck.com,erp_development@inateck.com", content, "重点产品跟卖提醒", "", "");
				}
				logger.info("跟卖提醒结束...");
			}
		}.start();
		
		
		logger.info("扫描产品主页差评开始...");  
		try{
			Map<String,Set<String>> countryAsinNameMap=product2Service.findCountryAsinName();
			Map<String,Set<String>> rsMap =Maps.newHashMap();
			Calendar  cal = Calendar.getInstance();
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			for (Map.Entry<String,Set<String>> entry : countryAsinNameMap.entrySet()) { 
			    String country=entry.getKey();
				Set<String> asinNames =entry.getValue();
				for(String asinName :asinNames){
					String arr[]=asinName.split(",");
					String asin = arr[0];
					List<Integer> starList = this.findStar(asin, country);
					String result = "";
					if(starList.size()>0){
						//如果是大于8点，只提示没评论的
						if(hour<12){
							if(starList.contains(3)&&(starList.contains(1)||starList.contains(2))){
								result="中差评";
							}else if(!starList.contains(3)&&(starList.contains(1)||starList.contains(2))){
								result="差评";
							}else if(starList.contains(3)){
								result="中评";
							}
						}
					}else{
						result="评论丢失";
					}
					
					if(StringUtils.isNotEmpty(result)){
						Set<String> infoSet = null; 
						if(rsMap.get(country)==null){
							infoSet=Sets.newHashSet();
						}else{
							infoSet=rsMap.get(country);
						}
						infoSet.add(asinName+","+result);
						rsMap.put(country, infoSet);
					}
				}
			}
			//组合信件内容
			sendEmai(rsMap);
		}catch(Exception ex){
			logger.error("扫描产品主页差评异常1..."+ex.getMessage(),ex);
		}
		logger.info("扫描产品主页差评结束...");  
		
		//暂停发送售后邮件提醒
//		try {
//			this.postMonitor.exeWarnMonitor();  //扫描售后邮件发送情况
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}
	
	
	
	
	private void sendEmai(Map<String,Set<String>> rsMap){
		try{
			Map<String,String>  prodouctLineMap = this.typeGroupService.getLineNameByName();
			StringBuffer contents= new StringBuffer("");
			if(rsMap!=null&&rsMap.size()>0){
				contents.append("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;以下是首页含有中差评或者没有评论消失的产品：<br/><table width='90%' style='border:1px solid #cad9ea;color:#666; '>" +
						"<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>国家</th><th>产品线</th><th>产品名</th><th>提示</th></tr>");
				for (Map.Entry<String,Set<String>> entry : rsMap.entrySet()) { 
				    String country=entry.getKey();
					Set<String> productNames = entry.getValue();
					if(productNames==null||productNames.size()==0){
						continue;
					}
					int i=0;
					//对产品进行分产品线处理
					Map<String,Set<String>> lineMap = Maps.newHashMap();
					for(String proInfo:productNames){
						String arr[] = proInfo.split(",");
						String productName = arr[1];
						String lineName="无";
						if(prodouctLineMap.get(productName)!=null){
							lineName=prodouctLineMap.get(productName);
						}
						Set<String> proSets = null;
						if(lineMap.get(lineName)==null){
							proSets=Sets.newHashSet();
						}else{
							proSets=lineMap.get(lineName);
						}
						proSets.add(proInfo);
						lineMap.put(lineName, proSets);
					}
					for (Map.Entry<String,Set<String>> entryLine : lineMap.entrySet()) { 
					    String line=entryLine.getKey();
						for(String proInfo:entryLine.getValue()){
							String arr[] = proInfo.split(",");
							String asin = arr[0];
							String productName = arr[1];
							String tips = arr[2];
							if(!"评论丢失".equals(tips)){//上面显示中差评
								String color="#f5fafe";
								String url = getHomeA(asin, country,productName);
								if(i==0){
									color="#99CCFF";
								}
								contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:"+color+"; '><td>"+("com".equals(country)?"us":country)+
										"</td><td>"+line+"</td><td>"+url+"</td><td>"+tips+"</td></tr>");
								i++;
							}
						}
					}
				}
				
				//显示评论丢失的
				for (Map.Entry<String,Set<String>> entry : rsMap.entrySet()) { 
				    String country=entry.getKey();
					Set<String> productNames = entry.getValue();
					if(productNames==null||productNames.size()==0){
						continue;
					}
					int i=0;
					//对产品进行分产品线处理
					Map<String,Set<String>> lineMap = Maps.newHashMap();
					for(String proInfo:productNames){
						String arr[] = proInfo.split(",");
						String productName = arr[1];
						String lineName="无";
						if(prodouctLineMap.get(productName)!=null){
							lineName=prodouctLineMap.get(productName);
						}
						Set<String> proSets = null;
						if(lineMap.get(lineName)==null){
							proSets=Sets.newHashSet();
						}else{
							proSets=lineMap.get(lineName);
						}
						proSets.add(proInfo);
						lineMap.put(lineName, proSets);
					}
					for (Map.Entry<String,Set<String>> entryLine : lineMap.entrySet()) { 
					    String line=entryLine.getKey();
						for(String proInfo:entryLine.getValue()){
							String arr[] = proInfo.split(",");
							String asin = arr[0];
							String productName = arr[1];
							String tips = arr[2];
							if("评论丢失".equals(tips)){//这里只显示“评论丢失”的
								String color="#f5fafe";
								String url = getHomeA(asin, country,productName);
								if(i==0){
									color="#99CCFF";
								}
								contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:"+color+"; '><td>"+("com".equals(country)?"us":country)+
										"</td><td>"+line+"</td><td>"+url+"</td><td>"+tips+"</td></tr>");
								i++;
							}
						}
					}
				}
				contents.append("</table>");
				Date date = new Date();
				//发信：
				String toAddress="amazon-sales@inateck.com,tim@inateck.com";
				final MailInfo mailInfo = new MailInfo(toAddress,"产品首页含中差评或者评论消失预警"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent(contents.toString());
				new Thread(){
					public void run(){
						 mailManager.send(mailInfo);
					}
				}.start();
			}
		}catch(Exception e){
			logger.error("首页含中差评预警异常"+e);
		}
	}	
	
	public  boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	 } 
	
//	private static Pattern pattern = Pattern.compile("\\[\"append\",\"#cm_cr-review_list\",\".[^\\]]+\"\\]");
//	private static Pattern pattern1 = Pattern.compile("\\[\"update\",\"#cm_cr-review_list\",\".[^\\]]+\"\\]");

	private static Pattern pattern = Pattern.compile("\\[\"append\",\"#cm_cr-review_list\",\".+?\"\\]");
	private static Pattern pattern1 = Pattern.compile("\\[\"update\",\"#cm_cr-review_list\",\".+?\"\\]");
	

	/**
	 *返回首页评论得分，以逗号分隔
	 */
	public  List<Integer> findStar(String asin,String country){
		List<Integer> starList = Lists.newArrayList();
		try{
			String temp = HttpRequest.reqUrlStr(this.getReviewsLinkByStar(asin,country), null, true);
			int s = temp.indexOf("[\""+"script"+"\",");
			if(StringUtils.isNotEmpty(temp)){
				//不包含script说明遇到防机器人，返回的是验证码页面
				if(s>=0){
					String htmlStr = temp.toString();
					List<Document> divs = Lists.newArrayList();
					Matcher matcher = pattern1.matcher(htmlStr);
					
					while(matcher.find()){
						String div = matcher.group();
						div = div.replace("[\"update\",\"#cm_cr-review_list\",\"","").replace("\"]","").replace("/\\\"/", "/").replace("\\","");
						Document doc = Jsoup.parse(div);
						Elements eles = doc.select(".review");
						if(eles.size()>0){
							divs.add(doc);
						}
					}
					
					matcher = pattern.matcher(htmlStr);
					while(matcher.find()){
						String div = matcher.group();
						div = div.replace("[\"append\",\"#cm_cr-review_list\",\"","").replace("\"]","").replace("/\\\"/", "/").replace("\\","");
						Document doc = Jsoup.parse(div);
						Elements eles = doc.select(".review");
						if(eles.size()>0){
							divs.add(doc);
						}
					}
					if (divs!= null&&divs.size()>0) {
						int totalLength =8000; 
						for(Document doc : divs) {
							// 评论等级
							String star = doc.select("span.a-icon-alt").text();
							String content = doc.select("span.review-text").html();
							Elements selectable1 = doc.select("div.video-block");
							if (selectable1 != null && selectable1.html() != null && content != null) {
								content = content.replace(selectable1.toString(), "");
							}
							if (!StringUtils.isEmpty(content)) {
								content = content.replaceAll("<script[^>]*>[\\d\\D]*?</script>", "");
								if(StringUtils.isNotEmpty(content)){
									content=stringFilter(stripControlChars(content));
								}
							}
							
							if (StringUtils.isNotEmpty(star)) {
								try {
									Integer starNum = Integer.parseInt(findStarByNew(star));
									starList.add(starNum);
								} catch (Exception e) {	}
								//查出一定长度的  首页不显示(大概是6000)
								int length=content.length();
								if(length>2000){
									length=2000;
								}else if(length<500){
									length=500;
								}
								totalLength=totalLength-length;
								if(totalLength<0){
									break;
								}
							}
						}
					}
				}else{
					//如果返回的是放机器人页面，返回一个0星的，结果那里排除0星的
					starList.add(0);
				}
			}else{
				//如果请求为空，返回一个0星的，结果那里排除0星的
				starList.add(0);
			}
//			if(starList.size()==0){
//				String fileName1 ="/opt/apache-tomcat-7.0.53/webapps/springrain-erp/data/site/test/"+country+"_"+asin+".txt";
//				FileUtils.createFile(fileName1);
//				FileOutputStream ops1 = new FileOutputStream(fileName1);
//				ops1.write(temp.getBytes("UTF-8"));
//				ops1.close();
//			}
			
		}catch(Exception ex){
			logger.error("扫描产品主页评论异常2："+ex.getMessage());
			ex.printStackTrace();
			starList.add(0);
		}
		return starList;
	}
	
	
	
	private static String findStarByNew(String str) {
		str = str.replace(",",".");
		String result = null;
		String regex = "\\d{1}\\.0";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(str);
		if (match.find()) {
			result = match.group().replace(".0","");
		}
		return result;
	}
	
	public static String stringFilter(String str) throws PatternSyntaxException {
		String regEx = "[^\u0000-\uFFFF0-9\\s@#!\\?%\\+\\.!/\":]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	public static String getReviewsLinkByStar(String asin,String country){
		String suffix = country;
		if("jp,uk".contains(suffix)){
			suffix = "co."+suffix;
		}else if ("mx".equals(suffix)){
			suffix = "com."+suffix;
		}
		String link = "https://www.amazon."	+ suffix
				+ "/ss/customer-reviews/ajax/reviews/get/ref=cm_cr_pr_viewopt_sr?sortBy=helpful&reviewerType=all_reviews&" +
				"deviceType=desktop&pageNumber=1&pageSize=8&asin=" + asin;
		return link;
	}
	
	public static String getHomeA(String asin,String country,String productName){
		String suffix = country;
		if("jp,uk".contains(suffix)){
			suffix = "co."+suffix;
		}else if ("mx".equals(suffix)){
			suffix = "com."+suffix;
		}
		String link ="<a href='https://www.amazon."+suffix+"/dp/"+asin+"'>"+productName+"</a>";
		return link;
	}
	
	
	protected String stripControlChars(String iString) {
		StringBuffer result = new StringBuffer(iString);
		int idx = result.length();
		while (idx-- > 0) {
			if ((result.charAt(idx) >= ' ') || (result.charAt(idx) == '\t')
					|| (result.charAt(idx) == '\n')
					|| (result.charAt(idx) == '\r'))
				continue;
			result.deleteCharAt(idx);
		}
		String rs = result.toString().replaceAll("\\p{Cc}", "");
		return rs;
	}
	
	/**
	 * sendemail
	 */
	public void sendNoticeEmail(String email,String content,String subject,String ccEmail,String bccEmail){
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			final MailInfo mailInfo = new MailInfo(email,subject+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			if(StringUtils.isNotEmpty(bccEmail)){
				mailInfo.setBccToAddress(bccEmail);
			}
			if(StringUtils.isNotEmpty(ccEmail)){
				mailInfo.setCcToAddress(ccEmail);
			}
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
	}
	
//	public static void main(String [] arr) throws ParseException{
//			ProductDirectoryCommentDetailMonitor mon = new ProductDirectoryCommentDetailMonitor();
//			ProductDirectoryComment detail = new ProductDirectoryComment();
//			detail.setCountry("com");
//			detail.setAsin("B00LE5VV1C");
//			mon.reqStar(detail);
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring-context.xml");
//		ProductHomePageCommentMonitor homePage = context.getBean(ProductHomePageCommentMonitor.class);
//		homePage.scanHomePageComment();
//		while(true){
//			new ProductHomePageCommentMonitor().findStar("B00QJV6J90", "com");
//		}
//	}

	
}
