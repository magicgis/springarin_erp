package com.springrain.erp.modules.amazoninfo.scheduler;

import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.HttpRequest;
import com.springrain.erp.modules.amazoninfo.entity.AmazonReviewer;
import com.springrain.erp.modules.amazoninfo.entity.AmazonReviewerContent;
import com.springrain.erp.modules.amazoninfo.htmlunit.CountryType;
import com.springrain.erp.modules.amazoninfo.htmlunit.LocaleUtil;
import com.springrain.erp.modules.amazoninfo.htmlunit.LoginUtil;
import com.springrain.erp.modules.amazoninfo.service.AmazonReviewerService;
import com.springrain.erp.modules.sys.service.DictService;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

public class TopReviewerMonitor {
	@Autowired  
	private AmazonReviewerService   reviewerService; 
	@Autowired
	private DictService             dictService;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private void scanReview(){
		logger.info("扫描top reviewer开始...");    
		try{
			List<String> countrys = dictService.findByType("platform");
			List<Thread> threads = Lists.newArrayList(); 
			for(final String country:countrys){
				Thread tempThread = new Thread(){
					public void run() {
						logger.info(country+"top1w扫描开始!!!");
						if(!"uk".equals(country)){
							new TopReviewerMonitor().scanByCountry(country,reviewerService);
						}
						 logger.info(country+"top1w扫描完成!!!");
					}
				};
				tempThread.start();
				threads.add(tempThread);
			}
			long st = System.currentTimeMillis();
			long proess = 0;
			while(threads.size()>0){
				proess = Math.round((float) (System.currentTimeMillis() - st) / 1000.0F + 0.5F);
				if(proess>108000L){
					logger.warn("top1w线程超时了30小时,准备打断中...");
					for (Iterator<Thread> iterator = threads.iterator(); iterator.hasNext();) {
						Thread thread = iterator.next();
						if(thread.isAlive()){
							try {
								thread.stop();
							} catch (Exception e) {}
						}
					}
					break;
				}
				for (Iterator<Thread> iterator = threads.iterator(); iterator.hasNext();) {
					Thread thread = iterator.next();
					if(!thread.isAlive()){
						iterator.remove();
					}
				}
				try {
					Thread.sleep(60000*30);
				} catch (InterruptedException e) {}
			}
			
			
		}catch(Exception e){
			logger.error("扫描top reviewer："+e.getMessage());
			e.printStackTrace();
		}
		try {
			reviewerService.updateAllReviewer();
			logger.info("统计评测人评论信息完毕");
		} catch (Exception e) {
			logger.error("统计评测人评论信息异常："+e.getMessage(), e);
		}
		logger.info("扫描top reviewer结束...");
	}
	


	public void scanByCountry(String country,AmazonReviewerService reviewerService1){
		String stuffix =country;
		if("jp".equals(country)||"uk".equals(country)){
			stuffix="co."+country;
		}else if("mx".equals(country)){
			stuffix="com."+country;
		}
		WebClient client = LoginUtil.frontRegister(country, false);
		//异常报出次数；
		int exNum=0;
		if(client!=null){
			for(int i=1;i<1001;i++){
				try{
					String pageUrl="https://www.amazon."+stuffix+"/review/top-reviewers?page="+i;
					HtmlPage masterPage  = getPage(client,pageUrl,0);
					for(int j=1;j<11;j++){
						//每页10条数据
						if(masterPage!=null){
							try{
								String trId = String.valueOf(j+(i-1)*10);
								HtmlTableRow trElement =  (HtmlTableRow)masterPage.getElementById("reviewer"+trId);
								if(trElement==null){
									continue;
								}
								HtmlTableCell  tdElement =(HtmlTableCell)trElement.getElementsByTagName("td").get(1);
								String reviewerId=tdElement.getElementsByTagName("a").get(1).getAttribute("name");
								
								String emailUrl ="https://www.amazon."+stuffix+"/gp/pdp/profile/"+reviewerId+"/customer_email";
								WebRequest req = new WebRequest(new URL(emailUrl),HttpMethod.GET);
								req.setAdditionalHeader("X-Requested-With", "XMLHttpRequest");
								UnexpectedPage jsonPage = jsonPage(client, req, 0);
								String json ="";
								if(jsonPage!=null&&jsonPage.getWebResponse()!=null){
									json =jsonPage.getWebResponse().getContentAsString();
								}
								
								String email =""; //解析邮箱
								if(StringUtils.isNotEmpty(json)){
									Map<String,JSONObject> map = JSON.parseObject(json,Map.class);
									if(map.get("data").get("email")!=null){  // {"status":"ok","data":{"email":"jjceoreviews@gmail.com"}}
										email=map.get("data").get("email").toString().trim();
									}
								}
								
								List<String> listUrls = Lists.newArrayList();
								HtmlPage personPage = getPage(client, "https://www.amazon."+stuffix+"/gp/pdp/profile/"+reviewerId, 0);//进入主页面获取联系网站
								//查看有没有https://www.facebook.com  https://www.youtube.com/  https://twitter.com/  https://www.instagram.com
								if(personPage!=null){//解析社交网站
									Document doc=Jsoup.parse(personPage.asXml());
									Elements eles=doc.getElementsByClass("social-link");
									for(Element es: eles){
										String url=es.getElementsByTag("a").attr("href");
										listUrls.add(url);
									}
								}
								//如果邮箱不为空，就继续扫描评论
								if(StringUtils.isNotEmpty(email)||listUrls.size()>0){
									String rank = trElement.getFirstElementChild().getTextContent().replace("#", "").replace(",", "").replace(".", "").trim();
									//如果不是第一页的第一个，但排名为1，说明乱了
									if(i!=1&&j!=1&&"1".equals(rank)){
										break;
									}
									Integer remarkTotal = Integer.parseInt(trElement.getElementsByTagName("td").get(3).getTextContent().replace(".", "").replace(",", "").trim());
									String name=tdElement.getElementsByTagName("b").get(0).asText();
									
									//根据reviwerId,确定是存在
								    AmazonReviewer amaReviewer = reviewerService1.getReviewerByReviewId(reviewerId,country);
								    Set<String>  reviewIdSet  = Sets.newHashSet();
								    if(amaReviewer.getId()==null){
								    	amaReviewer.setRank(Integer.parseInt(rank));
								    	amaReviewer.setName(name);
								    	amaReviewer.setCountry(country);
								    	amaReviewer.setReviewerId(reviewerId);
								    	amaReviewer.setReviewerType("0");
								    }else{
								    	reviewIdSet=reviewerService1.getAllComment(amaReviewer.getId());
								    }
								    
								    if(listUrls.size()>0){
								    	StringBuffer otherUrls=new StringBuffer("");
										for(String url:listUrls){
											if(url.indexOf("facebook.com")>0){
												if(StringUtils.isEmpty(amaReviewer.getFacebookUrl())){
													amaReviewer.setFacebookUrl(url);
												}
											}else if(url.indexOf("youtube.com")>0){
												if(StringUtils.isEmpty(amaReviewer.getYoutubeUrl())){
													amaReviewer.setYoutubeUrl(url);
												}
											}else if(url.indexOf("twitter.com")>0){
												if(StringUtils.isEmpty(amaReviewer.getTwitterUrl())){
													amaReviewer.setTwitterUrl(url);
												}
											}else if(url.indexOf("instagram.com")>0){
												if(StringUtils.isEmpty(amaReviewer.getInstagramUrl())){
													amaReviewer.setInstagramUrl(url);
												}
											}else{
												if(StringUtils.isEmpty(amaReviewer.getOtherUrl())){
													otherUrls.append(url+",");
												}
											}
										}
										if(StringUtils.isEmpty(amaReviewer.getOtherUrl())&&StringUtils.isNotEmpty(otherUrls.toString())){
											amaReviewer.setOtherUrl(otherUrls.toString());
										}
									}
									
								    
									int pageSize=1;
									List<AmazonReviewerContent> tempReviews = Lists.newArrayList();
									Integer pageTotal =0;
									if(remarkTotal%10==0){
										pageTotal=remarkTotal/10;
									}else{
										pageTotal=remarkTotal/10+1;
									}
									while(pageSize<=pageTotal){
										String reviewUrl ="https://www.amazon."+stuffix+"/gp/cdp/member-reviews/"+reviewerId+"?page="+pageSize+"&sort_by=MostRecentReview";
										Document doc = HttpRequest.reqUrl(reviewUrl, null, true);
										if(doc==null){
											continue;
										}
										Elements reviewIdAs =doc.select("a[name]");
										if(reviewIdAs==null||reviewIdAs.size()==0){
											continue;
										}
										for(Element reviewA:reviewIdAs){
											try{
												String reviewId    = reviewA.attr("name");
												Element content = reviewA.nextElementSibling().nextElementSibling();
												Element reviewTextEle = content.select("div[class=reviewText]").get(0);
												if(reviewIdSet!=null&&reviewIdSet.contains(reviewId)){
													break;
												}
												String starStr  ="";    
												if("com".equals(country)){
													starStr= content.select("img").get(0).attr("alt");
												}else{
													starStr=content.select("span.swSprite").attr("title");
												}
												String reviewTitle  = content.getElementsByTag("b").get(0).text();
												String reviewDateStr   = content.getElementsByTag("nobr").get(0).text();
												Date   reviewDate = LocaleUtil.formatDateReviewers(reviewDateStr, LocaleUtil.FORMAT_SHORT, CountryType.getCountryTypeByEsayName("com".equals(country)?"com.inateck":country).getLocale());
												String asin="";
												String productTitle="";
												String brandType="";
												String productType  ="";
												Elements asinA=reviewTextEle.previousElementSibling().getElementsByTag("a");
												//有些产品没有产品title
												if(asinA==null||asinA.attr("href")==null){
													asin =asinA.attr("href").split("/")[5];
													productTitle = asinA.text();
													if(productTitle.lastIndexOf("(")!=-1&&productTitle.lastIndexOf(")")!=-1){
														productType  =productTitle.substring(productTitle.lastIndexOf("(") + 1, productTitle.lastIndexOf(")"));
													}
													
													//处理品牌名
													for(String bank:this.getMonitorBanks()){
														if(productTitle.contains(bank)){
															brandType=bank;
															break;
														}
													}
												}
												
												Integer star = Integer.parseInt(findStarByNew(starStr));
											
												tempReviews.add(new AmazonReviewerContent(amaReviewer, reviewId, reviewTitle, productTitle, star, brandType, productType, reviewDate,asin));
												
											}catch(Exception ex){
												if(exNum<5){
													logger.error("top reviewer单个评论扫描异常："+ex.getMessage(),ex);
													exNum++;
												}
											}
										}
										pageSize++;
									}
									amaReviewer.setReviewEmail(email);
									amaReviewer.setUpdateDate(new Date());
									
									if(amaReviewer.getIsVineVoice()==null||"0".equals(amaReviewer.getIsVineVoice())){//如果不为vine，重新看是否为vine
										String isVineVoice ="0"; //是否为vineVoice成员
										//进入用户主页，获取VINE VOICE  开始
										try{
											if(personPage!=null){
												List<DomNode> spans = personPage.querySelectorAll("span.pr-c7y-badge");
												for(DomNode span:spans){
													if("VINE VOICE".equals(span.getTextContent())){
														isVineVoice="1";
														break;
													};
												}
											}
										}catch(Exception ex){
											if(exNum<5){
												logger.error("top reviewer VINE VOICE扫描异常："+ex.getMessage());
												exNum++;
											}
										}
										//进入用户主页，获取VINE VOICE  结束
										amaReviewer.setIsVineVoice(isVineVoice);
									}
									
									if(tempReviews.size()>0){
										if(amaReviewer.getId()!=null){
											//逐条更新子项
											
											reviewerService1.updateAllReviewerBySql(amaReviewer,tempReviews);
										}else{
											amaReviewer.getContent().addAll(tempReviews);
											reviewerService1.save(amaReviewer);
										}
									}else{
										//如果没有新增的进来，只更新主表
										if(amaReviewer.getId()!=null){
											reviewerService1.updateAmazonReviewerBySql(amaReviewer);
										}else{
											amaReviewer.getContent().addAll(tempReviews);
											reviewerService1.save(amaReviewer);
										}
										
									}
									
									
								}
							}catch(Exception ex){
								if(exNum<5){
									logger.error("top reviewer单个评论人异常"+ex.getMessage(),ex);
									exNum++;
								}
							}
						}
					}
				}catch(Exception ex){
					if(exNum<5){
						logger.error("top reviewer评论人页面异常"+ex.getMessage());
						exNum++;
					}
				}
			}
			//扫描top10000完成后继续扫描手动录入的站内评测人评论信息
			AmazonReviewer reviewer = new AmazonReviewer();
			reviewer.setReviewerType("0"); //站内
			reviewer.setCountry(country);	//分国家扫描
			List<AmazonReviewer> list = reviewerService1.findListForScanner(reviewer);
			for (AmazonReviewer amazonReviewer : list) {
				try {
					String reviewerId = amazonReviewer.getReviewerId();
					// 根据reviwerId,找到现有的评论信息
					Set<String> reviewIdSet = reviewerService1.getAllComment(amazonReviewer.getId());
					int pageSize = 1;
					List<AmazonReviewerContent> tempReviews = Lists.newArrayList();
					Integer pageTotal = 20; // 不知道用户评论总数,默认最多扫描页数,评论为空或异常时停止
					Set<String> idSet = Sets.newHashSet();
					while (pageSize <= pageTotal) {
						String reviewUrl = "https://www.amazon." + stuffix + "/gp/cdp/member-reviews/" + reviewerId + "?page=" + pageSize + "&sort_by=MostRecentReview";
						Elements reviewIdAs = null;
						try {
							Document doc = HttpRequest.reqUrl(reviewUrl, null, true);
							reviewIdAs = doc.select("a[name]");
						} catch (Exception e) {
							break;
						}
						if (reviewIdAs == null || reviewIdAs.size() == 0) {
							break;
						}
						if (reviewIdAs.size() < 10) {	//表示已不足一页,即已到最后一页
							pageSize = pageTotal;
						}
						for (Element reviewA : reviewIdAs) {
							try {
								String reviewId = reviewA.attr("name"); // 评论ID
								if (idSet.contains(reviewId)) {
									continue;
								}
								idSet.add(reviewId);
								Element content = reviewA.nextElementSibling().nextElementSibling();
								Element reviewTextEle = content.select("div[class=reviewText]").get(0);
								if (reviewIdSet != null && reviewIdSet.contains(reviewId)) {
									break;
								}
								String starStr = "";
								if ("com".equals(country)) {
									starStr = content.select("img").get(0).attr("alt");
								} else {
									starStr = content.select("span.swSprite").attr("title");
								}
								String reviewTitle = content.getElementsByTag("b").get(0).text();
								String reviewDateStr = content.getElementsByTag("nobr").get(0).text();
								Date reviewDate = LocaleUtil.formatDateReviewers(reviewDateStr, LocaleUtil.FORMAT_SHORT,
												CountryType.getCountryTypeByEsayName("com".equals(country) ? "com.inateck" : country).getLocale());
								String asin = "";
								String productTitle = "";
								String brandType = "";
								String productType = "";
								Elements asinA = reviewTextEle.previousElementSibling().getElementsByTag("a");
								// 有些产品没有产品title
								if (asinA != null && asinA.attr("href") != null) {
									try {
										asin = asinA.attr("href").split("/")[5];
									} catch (Exception e) {}
									productTitle = asinA.text();
									if (productTitle.lastIndexOf("(") != -1
											&& productTitle.lastIndexOf(")") != -1) {
										productType = productTitle.substring(productTitle.lastIndexOf("(") + 1,
														productTitle.lastIndexOf(")"));
									}

									// 处理品牌名
									for (String bank : getMonitorBanks()) {
										if (productTitle.contains(bank)) {
											brandType = bank;
											break;
										}
									}
								}

								Integer star = Integer.parseInt(findStarByNew(starStr));

								tempReviews.add(new AmazonReviewerContent(amazonReviewer, reviewId, reviewTitle, productTitle, star,
										brandType, productType, reviewDate, asin));

							} catch (Exception ex) {
								if(exNum<5){
								logger.error("reviewer单个评论扫描异常：" + ex.getMessage(),ex);
								exNum++;
								}
							}
						}
						pageSize++;
					}
					amazonReviewer.setUpdateDate(new Date());
					if (tempReviews.size() > 0) {
						if (amazonReviewer.getId() != null) {
							// 逐条更新子项
							reviewerService1.updateAllReviewerBySql(amazonReviewer, tempReviews);
						} else {
							amazonReviewer.getContent().addAll(tempReviews);
							reviewerService1.save(amazonReviewer);
						}
					} else {
						// 如果没有新增的进来，只更新主表
						if (amazonReviewer.getId() != null) {
							reviewerService1.updateAmazonReviewerBySql(amazonReviewer);
						} else {
							amazonReviewer.getContent().addAll(tempReviews);
							reviewerService1.save(amazonReviewer);
						}
					}
				} catch (Exception ex) {
					if(exNum<5){
					logger.error("reviewer单个评论人异常" + ex.getMessage(),ex);
					exNum++;
					}
				}
			}
			//扫描手动录入的站内评测人评论信息结束
		}
	}
	
	
	public static Set<String>  getMonitorBanks(){
		Set<String> set = Sets.newHashSet();
		set.add("Inateck"); 
		set.add("Anker");       
		set.add("Aukey");
		set.add("TaoTronics");
		set.add("EasyAcc");
		set.add("Mpow");
		set.add("RAVPower");
		set.add("CSL");
		return set;
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
	
	private HtmlPage getPage(WebClient client,String url,int num){
		if(num>10){
			return null;
		}
		try {
			HtmlPage page =  client.getPage(url);
			return page;
		} catch (Exception e) {
			e.printStackTrace();
			num = num +1;
			return getPage(client,url,num);
		}
	}
	
	public UnexpectedPage jsonPage(WebClient client,WebRequest req,int num){
		if(num>5){
			return null;
		}
		try {
			UnexpectedPage page =  client.getPage(req);
			return page;
		} catch (Exception e) {
			num = num +1;
			client.closeAllWindows();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {}
			return jsonPage(client,req,num);
		}
	}
	
	
	public void test (){
//		WebClient client = LoginUtil.frontRegister("com", false);
//		String reviewerId = "A2LXX47A0KMJVX";
//		//进入用户主页，获取VINE VOICE  开始
//		HtmlPage personPage = getPage(client, "https://www.amazon.com/gp/pdp/profile/"+reviewerId, 0);
//		List<DomNode> spans = personPage.querySelectorAll("span.pr-c7y-badge");
//		for(DomNode span:spans){
//			if("VINE VOICE".equals(span.getTextContent())){
//				System.out.println("hhhha");
//				break;
//			};
//		}
//		
//		
//		WebClient client = LoginUtil.frontRegister("de", false);
//		String reviewerId = "A1U2U4IY024BVU";
////		//进入用户主页，获取VINE VOICE  开始
//		HtmlPage personPage = getPage(client, "https://www.amazon.de/gp/pdp/profile/"+reviewerId, 0);
//		System.out.println(personPage.getUrl());
//		FileUtils.writeFile(personPage.asXml(), "d://xxx.html");
////		String aa="<div><div class=\"a-fixed-right-grid-col social-link\" style=\"float: left;\"><a href=\"https://www.facebook.com/MagicMasi\" >xx</a></div><div class=\"a-fixed-right-grid-col social-link\" style=\"float: left;\"><a href=\"https://www.facebook.com/MagicMasi\" >yy</a></div></div>";
//		String aa = personPage.asXml();
//		Document doc=Jsoup.parse(aa);
//		Elements eles=doc.getElementsByClass("social-link");
//		for(Element es: eles){
//			String str=es.getElementsByTag("a").attr("href");
//			System.out.println();
//		}
//		List<DomNode> spans = personPage.querySelectorAll("span.pr-c7y-badge");
//		List<DomNode> divs = personPage.querySelectorAll("div.social-link");
//		if(divs!=null&&divs.size()>0){
//			for(DomNode node:divs){
//				String url=doc.getElementsByAttribute("href").text();
//				System.out.println(url);
//			}
//		}
	}
//	public static void main(String [] arr){
//		new TopReviewerMonitor().test();
//	}

}
