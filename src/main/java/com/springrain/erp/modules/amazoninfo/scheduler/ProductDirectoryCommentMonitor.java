package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.HttpRequest;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectory;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectoryComment;
import com.springrain.erp.modules.amazoninfo.service.ProductDirectoryCommentDetailService;
import com.springrain.erp.modules.amazoninfo.service.ProductDirectoryCommentService;
import com.springrain.erp.modules.amazoninfo.service.ProductDirectoryService;
import com.springrain.erp.modules.psi.service.PsiLadingBillService;
import com.springrain.erp.modules.psi.service.lc.LcPsiLadingBillService;


public class ProductDirectoryCommentMonitor {
	@Autowired  
	private ProductDirectoryCommentService   		productDirectoryCommentService;
	@Autowired  
	private ProductDirectoryService			 		productDirectoryService;
	@Autowired
	private PsiLadingBillService  					ladingBillService;
	@Autowired
	private LcPsiLadingBillService 			 		lcLadingBillService;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private void scanDirectoryComments(){
		//自动冻结超过七天的目录开始
		this.productDirectoryService.updateLockStaAuto();
		//自动冻结超过七天的目录开始
		
		//每天算下供应商逾期率开始
		try{
			logger.info("每天算提单付款预期时间开始...");
			ladingBillService.updatePayDelayDays();
			lcLadingBillService.updatePayDelayDays();
			logger.info("每天算提单付款预期时间结束...");
		}catch(Exception ex){
			logger.error("每天算提单付款预期时间异常...");
		}
		
		
		logger.info("扫描亚马逊目录排名开始...");
		List<ProductDirectory>  list = this.productDirectoryService.find();
		List<ProductDirectoryComment> directoryComments = this.productDirectoryCommentService.findByDirectoryId(null);
		Map<Integer,Map<String,ProductDirectoryComment>> diectoryMap = Maps.newHashMap();
		for(ProductDirectoryComment comment: directoryComments){
			Integer directoryId = comment.getDirectoryId();
			String asin = comment.getAsin();
			Map<String,ProductDirectoryComment> innerMap = null;
			if(diectoryMap.get(directoryId)==null){
				innerMap=Maps.newHashMap();
			}else{
				innerMap=diectoryMap.get(directoryId);
			}
			innerMap.put(asin, comment);
			diectoryMap.put(directoryId, innerMap);
		}
		
		
		//查询所有目录的相应asin
		for(ProductDirectory directory:list){   
			//如果是锁定状态的，不扫描
			if("1".equals(directory.getLockSta())){
				continue;
			}
			try{
				String 	url     = directory.getUrl();
				Integer directoryId = directory.getId();
				String country =directory.getCountry();
				Map<String,ProductDirectoryComment> beforeAsinMap = diectoryMap.get(directoryId);
				//查询原来的top100的asin
				for(int i =1;i<6;i++){
					String pageUrl=url+"?pg="+i;
					Document doc = HttpRequest.reqUrl(pageUrl, null, false);
					if(doc!=null){
						//Elements itemDivs = doc.getElementsByClass("jp".equals(country)?"zg_item_normal":"zg_itemImmersion");
						Elements itemDivs = doc.getElementsByClass("jp".equals(country)?"zg_itemRow":"zg_itemImmersion");
						for(Element itemDiv:itemDivs){
							if (!"jp".equals(country)) {
								try {
									String text = itemDiv.getElementsByClass("zg_itemWrapper").get(0).text().trim();
									if(StringUtils.isEmpty(text)){
										continue;
									}
								} catch (Exception e) {
									continue;
								}
							}
							try {
								ProductDirectoryComment proComment =null;
								String  rank       = itemDiv.getElementsByClass("zg_rankNumber").get(0).text().replace(".","");
//								String  titleUrl   = itemDiv.getElementsByClass("zg_title").get(0).getElementsByTag("a").attr("href");
//								if(StringUtils.isEmpty(titleUrl)){
//									titleUrl=itemDiv.getElementsByClass("zg_image").get(0).getElementsByTag("a").attr("href");
//									titleUrl = this.stripControlChars(titleUrl);
//								}else{
//									titleUrl = this.stripControlChars(titleUrl);
//								}
//								String  asin=titleUrl.split("/")[5].replace("\n", "");
								String  titleUrl = "";
								String asin="";
								Elements eles =  itemDiv.getElementsByClass("zg_title");
								if("jp".equals(country)){
									if (eles != null && eles.size() > 0) {
										titleUrl = eles.get(0).getElementsByTag("a").attr("href");
										titleUrl = this.stripControlChars(titleUrl);
										try {
											asin = titleUrl.split("/")[5].replace("\n", "");
										} catch (Exception e) {
											logger.warn("解析asin失败：" + titleUrl, e);
											continue;
										}
									} else {
										try {
											eles = itemDiv.getElementsByClass("a-link-normal");
											for (Element element : eles) {
												if (StringUtils.isNotEmpty(element.text().trim())) {
													titleUrl = element.attr("href");
													titleUrl = this.stripControlChars(titleUrl);
													break;
												}
											}
											if (titleUrl.startsWith("/")) {
												titleUrl = titleUrl.substring(1);
											}
											asin = titleUrl.split("/")[3].replace("\n", "");
											if (asin.length() > 10) {
												asin = titleUrl.split("/")[2].replace("\n", "");
											}
											if (asin.length() > 10) {
												for (String str : titleUrl.split("/")) {
													if (str.replace("\n", "").length()==10) {
														asin = str.replace("\n", "");
													}
												}
											}
										} catch (Exception e) {
											logger.info("asin解析失败：" + pageUrl);
										}
									}
								} else {
									eles =  itemDiv.getElementsByClass("zg_itemWrapper");
									titleUrl = eles.get(0).getElementsByTag("a").get(0).attr("href");
									titleUrl = this.stripControlChars(titleUrl);
									try {
										if (titleUrl.startsWith("/")) {
											titleUrl = titleUrl.substring(1);
										}
										try {
											asin = titleUrl.split("/")[3].replace("\n", "");
										} catch (Exception e) {}
										if (asin == null || asin.length() != 10) {
											for (String str : titleUrl.split("/")) {
												str = str.replace("\n", "");
												if (str.contains("?")) {
													str = str.split("\\?")[0];
												}
												if (str.length()==10) {
													asin = str;
													break;
												}
											}
										}
									} catch (Exception e) {
										logger.warn(titleUrl, e);
										continue;
									}
								}
								if (StringUtils.isNotEmpty(asin) && asin.contains("?")) {
									asin = asin.split("\\?")[0];
								}
								if(beforeAsinMap!=null&&beforeAsinMap.get(asin)!=null){
									proComment  = beforeAsinMap.get(asin);
								}else{
									proComment = new ProductDirectoryComment();
									proComment.setUrl(url);
									proComment.setDirectoryId(directoryId);
									//String image = itemDiv.getElementsByClass("zg_image").get(0).getElementsByTag("img").attr("src");
									String  image = null;
									try {
										image  = itemDiv.getElementsByClass("a-spacing-mini").get(0).getElementsByTag("img").attr("src");
									} catch (Exception e) {
										image  = itemDiv.getElementsByTag("img").attr("src");
									}
									//品牌信息
									String brand = "";
									eles = itemDiv.getElementsByClass("zg_byline");
									if(eles!=null && eles.size()>0){
										brand = eles.get(0).text();
									} 
									if (StringUtils.isEmpty(brand)) {
										//抓取完整的title
										String productUrl = getLink(country, asin);
										Document productDetail = HttpRequest.reqUrl(productUrl, null, false);
										String  title ="" ;
										if(productDetail!=null&&productDetail.getElementById("productTitle")!=null){
											title=productDetail.getElementById("productTitle").text();
											if(productDetail.getElementById("brand")!=null){
												proComment.setBrand(productDetail.getElementById("brand").text());
											}
										}else{
											//title=itemDiv.getElementsByClass("zg_title").get(0).getElementsByTag("a").text();
											eles =  itemDiv.getElementsByClass("zg_title");
											if("jp".equals(country)){
												if (eles != null && eles.size() > 0) {
													title = eles.get(0).getElementsByTag("a").text();
												} else {
													try {
														eles = itemDiv.getElementsByClass("a-link-normal");
														for (Element element : eles) {
															if (StringUtils.isNotEmpty(element.text().trim())) {
																title = element.text();
																break;
															}
														}
													} catch (Exception e) {
														logger.info("主题解析失败：" + pageUrl);
													}
												}
											} else {
												try {
													title = itemDiv.getElementsByClass("zg_itemWrapper").get(0).getElementsByTag("a").get(0).getElementsByTag("span").get(0).attr("title");
												} catch (Exception e) {
													title = itemDiv.getElementsByClass("zg_itemWrapper").get(0).getElementsByTag("a").get(0).text().trim();
												}
												if (StringUtils.isNotEmpty(title)) {
													title = title.replace("\t", " ");
													title = title.replace("\n", " ");
												}
											}
										}
										proComment.setTitle(title);
									} else {
										proComment.setBrand(brand);
									}
									proComment.setImage(image);
									proComment.setAsin(asin);
									proComment.setCountry(country);
									proComment.setDataDate(new Date());
								}
								//每天抓取价格、品牌、排名
								float  price=0f;
								//有的没价格
								if(!"jp".equals(country)){
									String  priceStr = null;
									try {
										priceStr = itemDiv.getElementsByClass("a-row").get(0).getElementsByClass("a-color-price").get(0).getElementsByClass("p13n-sc-price").get(0).text();
									} catch (Exception e2) {
										try {
											priceStr  = itemDiv.getElementsByClass("zg_price").get(0).getElementsByClass("price").get(0).text();
										} catch (Exception e) {}
									}
									if(priceStr!=null){
										String priceS ="";
										if("com".equals(country)){
											priceS = priceStr.replace("$", "");
										}else if("uk".equals(country)){
											priceS = priceStr.replace("£", "");
										}else if ("de,es,it,fr,jp,ca,mx".contains(country)){
											priceS = priceStr.split(" ")[1];
										}
										//如果点号后面有两位，则点号就是小数点，否则为千分位分隔符
										if(priceS.split("\\.").length>0&&priceS.split("\\.")[priceS.split("\\.").length-1].length()==2){
											price=Float.parseFloat(priceS.replace(",", ""));
										}else if(priceS.split(",").length>0&&priceS.split(",")[priceS.split(",").length-1].length()==2){
											price=Float.parseFloat(priceS.replace(".", "").replace(",", "."));
										}else{
											priceS=priceS.replace(",", ""); 
											try{
												price=Float.parseFloat(priceS);
											}catch(Exception ex){
												//For input string: "25.99 - 31.99"
												price=Float.valueOf(priceS.split(" ")[0]);
											}
										}
									}
								}else{
									if(itemDiv.getElementsByClass("price").size()>0){
										String  priceStr  = itemDiv.getElementsByClass("price").get(0).text();
										String priceS = priceStr.split(" ")[1];
										priceS = priceS.replace(",", "");
										try{
											price=Float.parseFloat(priceS);
										}catch(Exception ex){}
									} else if(itemDiv.getElementsByClass("a-color-price").get(0).getElementsByClass("p13n-sc-price").size()>0){
										String  priceStr  = itemDiv.getElementsByClass("a-color-price").get(0).getElementsByClass("p13n-sc-price").get(0).text();
										try{
											String priceS = priceStr.replace("￥", "");
											priceS = priceS.replace(",", "");
											priceS = priceS.split("-")[0].trim();
											price=Float.parseFloat(priceS);
										}catch(Exception ex){
											logger.info(country + "免费产品"+asin);
										}
									}
								}
								
								proComment.setSalePrice(price);
								if(StringUtils.isEmpty(proComment.getBrand())){
									String productUrl = getLink(country, asin);
									//如果品牌上次没抓到，，
									Document productDetail = HttpRequest.reqUrl(productUrl, null, false);
									if(productDetail!=null&&productDetail.getElementById("productTitle")!=null){
										String title=productDetail.getElementById("productTitle").text();
										if(productDetail.getElementById("brand")!=null){
											proComment.setBrand(productDetail.getElementById("brand").text());
										}
										proComment.setTitle(title);
									}
								}
								proComment.setRanking(Integer.parseInt(rank));
								proComment.setUpdateDate(new Date());//更新的是100个
								this.reqStar(proComment);
							} catch (Exception e) {
								logger.error("目录扫描异常1："+e.getMessage(), e);
							}
						}
					}
				}
				logger.info("扫描目录序号："+directory.getId()+";"+url+"结束!");
			} catch (Exception e) {
				logger.error("目录扫描异常2："+e.getMessage(), e);
			}
		}
		logger.info("扫描亚马逊目录排名结束...");
	}

	
	public void reqStar(final ProductDirectoryComment directoryComment) {
		try{ 
			Thread thread1 = new Thread(){
				public void run() {
				new ProductDirectoryCommentMonitor().findReviews(directoryComment,"one_star");
				};
			};
			thread1.start();
			Thread.sleep(500);
			Thread thread2 = new Thread(){
				public void run() {
					new ProductDirectoryCommentMonitor().findReviews(directoryComment,"two_star");
				};
			};
			thread2.start();
			Thread.sleep(500);
			Thread thread3 = new Thread(){
				public void run() {
					new ProductDirectoryCommentMonitor().findReviews(directoryComment,"three_star");
				};
			};
			thread3.start();
			Thread.sleep(500);
			Thread thread4 = new Thread(){
				public void run() {
					new ProductDirectoryCommentMonitor().findReviews(directoryComment,"four_star");
				}
			};
			thread4.start();
			Thread.sleep(500);
			Thread thread5 = new Thread(){
				public void run() {
					new ProductDirectoryCommentMonitor().findReviews(directoryComment,"five_star");
				}
			};
			thread5.start();
			Thread.sleep(500);
			while(thread1.isAlive()||thread2.isAlive()||thread3.isAlive()||thread4.isAlive()||thread5.isAlive()){
				Thread.sleep(1000);
			}
			
			
			directoryComment.countStar();
			directoryComment.setIsShield("0");
			this.productDirectoryCommentService.save(directoryComment);//产品评论总信息保存
			
			
		}catch(Exception e){
			logger.error("目录扫描异常3："+e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	private  int parseTotal(String country,String content){
		String rs = content;
		try {
			if("com,ca,uk".contains(country)&&rs.contains("of ")){
				rs = rs.split("of ")[1];
				rs = rs.replaceAll("\\D","");
			}else if("jp".equals(country)){
				if(rs.contains("件")){
					rs = rs.split("件")[0];
				}else{
					return 0;
				}
			}else if("de".contains(country)&&rs.contains("von ")){
				rs = rs.split("von ")[1];
				rs = rs.replaceAll("\\D","");
			}else if("fr".contains(country)&&rs.contains("sur ")){
				rs = rs.split("sur ")[1];
				rs = rs.replaceAll("\\D","");
			}else if("es".contains(country)&&rs.contains("de ")){
				rs = rs.split("de ")[1];
				rs = rs.replaceAll("\\D","");
			}else if("it".contains(country)&&rs.contains("su ")){
				rs = rs.split("su ")[1];
				rs = rs.replaceAll("\\D","");
			}else if("mx".contains(country)&&rs.contains("de ")){
				rs = rs.split("de ")[1];
				rs = rs.replaceAll("\\D","");
			}
			rs=rs.replace(",", "").replace(".", "");
			if(isNumeric(rs)){
				return Integer.parseInt(rs);
			}else{
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	  
	public  boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	 } 
	
	private static Pattern pattern1 = Pattern.compile("\\[\"update\",\"#cm_cr-review_list\",\".[^\\]]+\"\\]");
	
	public  void findReviews(ProductDirectoryComment directoryComment,String starType){
		try{
			int i = 1;
			String temp = HttpRequest.reqUrlStr(directoryComment.getReviewsLinkByStar(i,starType), null, false);
			if(temp!=null){
				String htmlStr = temp.toString();
				Matcher matcher = pattern1.matcher(htmlStr);
				
				while(matcher.find()){
					String div = matcher.group();
					div = div.replace("[\"update\",\"#cm_cr-review_list\",\"","").replace("\"]","").replace("/\\\"/", "/").replace("\\","");
					Document doc = Jsoup.parse(div);
					if(i==1){
						Elements els = doc.getElementsByClass("a-spacing-medium");
						Integer star=0;//星星总数
						if(els!=null&&els.size()>0&&els.get(0).children().size()>0){
							String starStr = ((Element)els.get(0).childNode(0)).text();
							star = parseTotal(directoryComment.getCountry(),starStr);
						}
						if("one_star".equals(starType)){
							directoryComment.setStar1(star);
						}else if("two_star".equals(starType)){
							directoryComment.setStar2(star);
						}else if("three_star".equals(starType)){
							directoryComment.setStar3(star);
						}else if("four_star".equals(starType)){
							directoryComment.setStar4(star);
						}else if("five_star".equals(starType)){
							directoryComment.setStar5(star);
						}
					}
				}
			}
		}catch(Exception ex){
			logger.error("扫描星星异常："+ex.getMessage(), ex);
		}
	}
	
	
	public static String stringFilter(String str) throws PatternSyntaxException {
		String regEx = "[^\u0000-\uFFFF0-9\\s@#!\\?%\\+\\.!/\":]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
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

	public static String getLink(String country, String asin){
		String suff = country;
		if("uk,jp".contains(country)){
			 suff = "co."+suff;
		}else if("mx".equals(country)){
			suff = "com."+suff;
		}
		return "https://www.amazon."+suff+"/dp/"+asin;
	}
	
	public static void main(String [] arr){
			//ProductDirectoryCommentMonitor mon = new ProductDirectoryCommentMonitor();
			ProductDirectoryComment detail = new ProductDirectoryComment();
			detail.setCountry("de");
			detail.setAsin("B000UXHBKY");
//			mon.reqStar(detail);
//			String  titleUrl =" https://www.amazon.co.uk/AUKEY-Fisheye-iPhone-Smartphone-Tablets-3-1-Lens/dp/B014MC9V4C/" ;
//			Document productDetail = HttpRequest.reqUrl(titleUrl, null, false);
//			
//			if(productDetail!=null&&productDetail.getElementById("productTitle")!=null){
//				String title=productDetail.getElementById("productTitle").text();
//				String brand = productDetail.getElementById("brand").text();
//			}
			
			new ProductDirectoryCommentMonitor().findReviews(detail,"five_star");
			
	}

}
