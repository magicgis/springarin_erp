package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Lists;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.weixin.utils.ParamesAPI;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

public class ProductPostDetailMonitor {
	
	private final static Logger logger = LoggerFactory.getLogger(ProductPostDetailMonitor.class);
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private AmazonPostsDetailService amazonPortsDetailService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private MailManager mailManager;
	
	/**
	 * 监控组合帖子有没有被其他产品恶意捆绑
	 */
	public void monitor() {
		logger.info("开始监控组合帖子捆绑情况");
		try {
			//查询最新时间的组合贴
			Map<String, Set<String>> map = amazonPortsDetailService.findCombination();
			//查询现有的asin集合
			Set<String> asinSet = amazonProduct2Service.getAllAsin();
			for (Map.Entry<String,Set<String>> entry : map.entrySet()) { 
			    String country =entry.getKey();
				List<String> countryAsinList = Lists.newArrayList();
				String suffix = country;
				if ("jp,uk".contains(suffix)) {
					suffix = "co." + suffix;
				} else if ("mx".equals(suffix)) {
					suffix = "com." + suffix;
				}
				for (String asin : entry.getValue()) {
					try {
						String url = "http://www.amazon." + suffix + "/dp/" + asin;
						WebClient client = getClient();
						HtmlPage page = getPage(client, url, 1);
						DomElement div = page.getElementById("variation_color_name");
						if (div == null) {
							continue;
						}
						DomNodeList<HtmlElement>  eles = div.getElementsByTagName("li");
						boolean flag = false;	//标记帖子下面是否有自己的商品
						boolean other = false;	//标记帖子下面是否有别人的asin
						for (HtmlElement htmlElement : eles) {
							String sonAsin = htmlElement.getAttribute("data-defaultasin");
							if (StringUtils.isEmpty(sonAsin)) {
								String sonUrl = htmlElement.getAttribute("data-dp-url");
								if (StringUtils.isNotEmpty(sonUrl)) {
									sonAsin = sonUrl.split("/").length>2?sonUrl.split("/")[2]:"";
								}
							}
							//抓取到asin但是不在系统记录的asin中,则视为其他商家恶意捆绑的asin
							if (StringUtils.isNotEmpty(sonAsin) && !asinSet.contains(sonAsin)) {
								logger.info(country + "监控到组合贴异常asin:" + sonAsin + ",母贴asin:" + asin);
								DomElement a = page.getElementById("brand");
								String brand = a.asText();
								if (brand.toLowerCase().contains("orico") || brand.toLowerCase().contains("unitek")) {
									logger.info("监控到异常组合贴品牌为:" + brand + ",不进行消息通知");
								} else {
									other = true;	//有别人的asin
								}
							} else if (StringUtils.isNotEmpty(sonAsin) && asinSet.contains(sonAsin)) {
								flag = true;	//组合贴存在自己的产品
							}
						}
						if (flag && other) {	//既有自己的asin又有别人的asin才预警
							countryAsinList.add(asin);
						}
						client.closeAllWindows();
					}catch(Exception e){
						logger.warn(country + "监控组合贴" + asin + "异常", e);
					}
				}
				if (countryAsinList.size() > 0) {
					String countryStr = SystemService.countryNameMap.get(country);
					Map<String, String> emailAndLoginNameMap = systemService.findLoginNameByEmail();
					List<String> countryList=Lists.newArrayList(country);
					List<String> roleNameList=Lists.newArrayList("amazoninfo:feedSubmission:");
				    Map<String,Set<String>> rs = systemService.getEmailMap(countryList,roleNameList);
				    String toEmail="";
				    String userStr = "";
					StringBuffer buf1= new StringBuffer();	
					StringBuffer buf2= new StringBuffer();
				    for (Map.Entry<String,Set<String>> entryRs : rs.entrySet()) { 
						Set<String> set = entryRs.getValue();
						for (String str : set) {
							String loginName = emailAndLoginNameMap.get(str);
							if (loginName != null) {
								buf1.append(loginName + "|");
								buf2.append(str + ",");
							}
						}
					}
				    buf1.append("tim");
				    buf2.append("tim@inateck.com");
				    userStr = buf1.toString();
					toEmail = buf2.toString();
					
					logger.info(countryStr + "组合贴捆绑异常通知人员(微信)：" + userStr + "\t" + countryAsinList.toString());
					logger.info("组合贴捆绑异常通知人员(Email)：" + toEmail);

					String url = "http://www.amazon." + suffix + "/dp/";
					try {
						//微信通知
						StringBuffer buf= new StringBuffer("Hi All,Amazon("+countryStr+")组合贴监控到异常捆绑信息,请及时处理.\n消息时间：\n" + DateUtils.getDateTime());
						for (String asin : countryAsinList) {
							buf.append("\n"+asin+":<a href='"+url+asin+"'>点击处理</a>");
						}
								
						WeixinSendMsgUtil.sendTextMsg(WeixinSendMsgUtil.getToken(), userStr, null, null, ParamesAPI.appId, buf.toString(), "0");
					} catch (Exception e) {
						logger.error(countryStr + "组合贴监控到异常捆绑信息,微信通知发送失败！应通知人员"+ userStr, e);
						WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, countryStr + "组合贴监控到异常捆绑信息,微信通知发送失败！应通知人员"+ userStr);
					}
					try {
						//邮件通知
						StringBuffer contents= new StringBuffer("");
						contents.append("<p><span style='font-size:20px'>Hi all,<br/>&nbsp;&nbsp;&nbsp;&nbsp;Amazon("+countryStr+")组合贴监控到异常捆绑信息,请及时处理.");
						for (String asin : countryAsinList) {
							contents.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+asin+":<a href='"+url+asin+"'>点击处理</a></span></p>");
						}
						final MailInfo mailInfo = new MailInfo(toEmail, "组合贴异常捆绑监控提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
						mailInfo.setContent(contents.toString());
		    			new Thread(){
		    			    public void run(){
		    					mailManager.send(mailInfo);
		    				}
		    			}.start();
					} catch (Exception e) {
						logger.error(countryStr + "组合贴监控到异常捆绑信息,邮件通知发送失败！应通知人员"+ toEmail, e);
						WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, countryStr + "组合贴监控到异常捆绑信息,邮件通知发送失败！应通知人员"+ toEmail);
					}
				}
			}
		} catch (Exception e) {
			logger.error("监控组合贴捆绑情况异常", e);
			WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "监控组合贴捆绑情况异常");
		}
		logger.info("监控组合帖子捆绑情况完毕");
	}
	
	private static WebClient getClient() {
		WebClient client = new WebClient();
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
		return client;
	}
	
	//重复10次获取页面失败则放弃
	private HtmlPage getPage(WebClient client,String url,int num){
		if(num>10){
			return null;
		}
		try {
			HtmlPage page =  client.getPage(url);
			return page;
		} catch (Exception e) {
			if (num==10) {
				logger.error(url + "第"+ num + "次请求失败", e);
			}
			num = num +1;
			return getPage(client,url,num);
		}
	}
	
	public static void main(String[] args) {
		/*try {
			String url = "http://www.amazon.es/dp/B01I1IT6HU";
			WebClient client = getClient();
	
			HtmlPage page = client.getPage(url);
			DomElement a = page.getElementById("brand");
			String brand = a.asText();
			System.out.println("品牌：" + brand);
		} catch (Exception e) {
			logger.error("监控组合贴B01I1IT6HU异常" , e);
		}*/
		/*
		try {
			String url = "http://www.amazon.de/dp/B00XHPJB1O";
			WebClient client = getClient();

			HtmlPage page = client.getPage(url);
			DomElement div = page.getElementById("variation_color_name");
			DomNodeList<HtmlElement>  eles = div.getElementsByTagName("li");
			for (HtmlElement htmlElement : eles) {
				//System.out.println(htmlElement.asXml());
				String sonAsin = htmlElement.getAttribute("data-defaultasin");
				if (StringUtils.isEmpty(sonAsin)) {
					String sonUrl = htmlElement.getAttribute("data-dp-url");
					if (StringUtils.isNotEmpty(sonUrl)) {
						sonAsin = sonUrl.split("/").length>2?sonUrl.split("/")[2]:"";
					}
				}
				System.out.println(sonAsin);
			}
		} catch (Exception e) {
			logger.error("de监控组合贴B00XHPJB1O异常" , e);
		}
		
		
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AmazonPostsDetailService  service = applicationContext.getBean(AmazonPostsDetailService.class);
		AmazonProduct2Service  amazonProduct2Service = applicationContext.getBean(AmazonProduct2Service.class);
		try {
			//查询最新时间的组合贴
			Map<String, Set<String>> map = service.findCombination();
			//查询现有的asin集合
			Set<String> asinSet = amazonProduct2Service.getAllAsin();
			for (String country : map.keySet()) {
				Set<String> countrySet = Sets.newHashSet();
				String suffix = country;
				if ("jp,uk".contains(suffix)) {
					suffix = "co." + suffix;
				} else if ("mx".equals(suffix)) {
					suffix = "com." + suffix;
				}
				for (String asin : map.get(country)) {
					try {
						String url = "http://www.amazon." + suffix + "/dp/" + asin;
						WebClient client = getClient();
						HtmlPage page = client.getPage(url);
						DomElement div = page.getElementById("variation_color_name");
						if (div == null) {
							continue;
						}
						DomNodeList<HtmlElement>  eles = div.getElementsByTagName("li");
						for (HtmlElement htmlElement : eles) {
							String sonAsin = htmlElement.getAttribute("data-defaultasin");
							if (StringUtils.isEmpty(sonAsin)) {
								String sonUrl = htmlElement.getAttribute("data-dp-url");
								if (StringUtils.isNotEmpty(sonUrl)) {
									sonAsin = sonUrl.split("/").length>2?sonUrl.split("/")[2]:"";
								}
							}
							//抓取到asin但是不在
							if (StringUtils.isNotEmpty(sonAsin) && !asinSet.contains(sonAsin)) {
								countrySet.add(asin);
							}
						}
					}catch(Exception e){
						logger.warn(country + "监控组合贴" + asin + "异常", e);
					}
				}
				if (countrySet.size() > 0) {
					System.out.println(country + "\t" + countrySet);
				}
			}
		} catch (Exception e) {
			logger.error("监控组合贴捆绑情况异常", e);
		}
		applicationContext.close();*/
	}
}
