package com.springrain.erp.modules.amazoninfo.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.HttpRequest;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectory;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectoryComment;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectoryCommentDetail;
import com.springrain.erp.modules.amazoninfo.htmlunit.CountryType;
import com.springrain.erp.modules.amazoninfo.htmlunit.LocaleUtil;
import com.springrain.erp.modules.amazoninfo.service.ProductDirectoryCommentDetailService;
import com.springrain.erp.modules.amazoninfo.service.ProductDirectoryCommentService;
import com.springrain.erp.modules.amazoninfo.service.ProductDirectoryService;
import com.sun.istack.FinalArrayList;


public class ProductDirectoryCommentDetailMonitor {
	@Autowired  
	private ProductDirectoryCommentService   productDirectoryCommentService;
	@Autowired  
	private ProductDirectoryCommentDetailService   productDirectoryCommentDetailService;

	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private void scanComments() throws ParseException{
		logger.info("扫描亚马逊目录评论开始...");   //每周五开始扫描评论
		try{
			Set<String> asinCountrySet = this.productDirectoryCommentService.getAllAsinAndCountry();
			for(String asinCountry:asinCountrySet){  
				String arr[]=asinCountry.split(",");
				this.reqStar(arr[0],arr[1]);
			}
			//更新该产品的上架时间
			Integer i =productDirectoryCommentService.getUpDate();
			logger.info("目录扫描更新上架时间影响："+i+"行！");
		}catch(Exception ex){
			logger.error("扫描亚马逊目录评论扫描异常..."+ex.getMessage(),ex);
		}
		logger.info("扫描亚马逊目录评论结束...");
	}
	
	
	public void reqStar(final String asin,final String country) {
		//根据国家和asin查出所有的星星及asin
		final Set<String> asinSet =this.productDirectoryCommentService.getStarLevelMap(asin, country);
		final List<ProductDirectoryCommentDetail>  list = Lists.newArrayList();
		try{ 
			Thread thread1 = new Thread(){
				public void run() {
				   Map<String,ProductDirectoryCommentDetail> resMaps= new ProductDirectoryCommentDetailMonitor().findReviews(asin, country,"one_star",asinSet);
				   list.addAll(resMaps.values());
				};
			};
			thread1.start();
			Thread.sleep(1000);
			Thread thread2 = new Thread(){
				public void run() {
					 Map<String,ProductDirectoryCommentDetail> resMaps= new ProductDirectoryCommentDetailMonitor().findReviews(asin, country,"two_star",asinSet);
					 list.addAll(resMaps.values());
				};
			};
			thread2.start();
			Thread.sleep(1000);
			Thread thread3 = new Thread(){
				public void run() {
					 Map<String,ProductDirectoryCommentDetail> resMaps= new ProductDirectoryCommentDetailMonitor().findReviews(asin, country,"three_star",asinSet);
					 list.addAll(resMaps.values());
				};
			};
			thread3.start();
			Thread.sleep(1000);
			Thread thread4 = new Thread(){
				public void run() {
					 Map<String,ProductDirectoryCommentDetail> resMaps= new ProductDirectoryCommentDetailMonitor().findReviews(asin, country,"four_star",asinSet);
					 list.addAll(resMaps.values());
				}
			};
			thread4.start();
			Thread.sleep(1000);
			Thread thread5 = new Thread(){
				public void run() {
					 Map<String,ProductDirectoryCommentDetail> resMaps= new ProductDirectoryCommentDetailMonitor().findReviews(asin, country,"five_star",asinSet);
					 list.addAll(resMaps.values());
				}
			};
			thread5.start();
			Thread.sleep(500);
			while(thread1.isAlive()||thread2.isAlive()||thread3.isAlive()||thread4.isAlive()||thread5.isAlive()){
				Thread.sleep(1000);
			}
//			只扫描评论详细不保存目录内容
//			directoryComment.countStar();
//			this.productDirectoryCommentService.save(directoryComment);//产品评论总信息保存
			if(list.size()>0){
				productDirectoryCommentDetailService.saveList(list);
			}
		}catch(Exception e){
			logger.error("目录评论扫描异常3："+e.getMessage());
			e.printStackTrace();
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

	
	public  Map<String,ProductDirectoryCommentDetail> findReviews(String asin,String country,String starType,Set<String> reviewIds){
		Map<String,ProductDirectoryCommentDetail> rs = Maps.newHashMap();
		try{
			int i = 1;
			while(i>0){
				String temp = HttpRequest.reqUrlStr(ProductDirectoryCommentDetailMonitor.getReviewsLinkByStar(i,starType,asin,country), null, true);
				String suffix = country;
				if("com".equals(country)){
					suffix = "com.inateck";
				}
				if(temp!=null){
					
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
					if (divs != null) {
						for (Document doc : divs) {
							ProductDirectoryCommentDetail comment = new ProductDirectoryCommentDetail();
							String url = doc.select("div.review-data a").attr("href");
							// 评论发表时间;
							String datetime = doc.select("span.review-date").text();
							if (!StringUtils.isEmpty(datetime)) {
								try {
									Date create_date = LocaleUtil.formatDate(datetime, LocaleUtil.FORMAT_SHORT, CountryType.getCountryTypeByEsayName(suffix).getLocale());
									comment.setReviewDate(create_date);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							// 评论asin
							String id = doc.select(".review").get(0).attr("id");
							if(reviewIds!=null&&reviewIds.size()>0&&reviewIds.contains(id)){//如果该评论存在，则推出
								break;
							}
							comment.setReviewId(id);
							// 评论等级
							String star = doc.select("span.a-icon-alt").text();
							if (!StringUtils.isEmpty(star)) {
								String tempStar = star;
								if (tempStar != null) {
									try {
										Integer starNum = Integer.parseInt(findStarByNew(tempStar));
										comment.setStar(starNum+"");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							
							if(StringUtils.isNotEmpty(url)){
								String tempAsin = findRealAsin(url);
								if(tempAsin!=null&&!tempAsin.equals(asin)){
									continue;
								}
							}
							
							//如果请求的是几星 ，不为几星
							if("one_star".equals(starType)&&!"1".equals(comment.getStar())){
								continue;
							}else if("two_star".equals(starType)&&!"2".equals(comment.getStar())){
								continue;
							}else if("three_star".equals(starType)&&!"3".equals(comment.getStar())){
								continue;
							}else if("four_star".equals(starType)&&!"4".equals(comment.getStar())){
								continue;
							}else if("five_star".equals(starType)&&!"5".equals(comment.getStar())){
								continue;
							}
							// 评论主题：
							String title = doc.select("a.review-title").text();
							String content = doc.select("span.review-text").html();
		
							Elements selectable1 = doc.select("div.video-block");
							if (selectable1 != null && selectable1.html() != null && content != null) {
								content = content.replace(selectable1.toString(), "");
							}
							if (!StringUtils.isEmpty(content)) {
								content = content.replaceAll("<script[^>]*>[\\d\\D]*?</script>", "");
								if(StringUtils.isNotEmpty(content)){
									comment.setContent(stringFilter(stripControlChars(content)));
								}
							}
							// 评论人
							String commentator = doc.select("a.author").text();
							if (!StringUtils.isEmpty(commentator)) {
								comment.setCustomerName(stringFilter(stripControlChars(commentator)));
							}
							String commentator_asin = doc.select("a.author").attr("href");
		
							// 即是评论人的asin码
							if (!StringUtils.isEmpty(commentator_asin)) {
								try {
									commentator_asin = commentator_asin.split("profile/")[1];
									commentator_asin = commentator_asin.substring(0,commentator_asin.indexOf("/"));
								} catch (Exception e) {
									e.printStackTrace();
								}
								comment.setCustomerId(commentator_asin);
							}
							
							comment.setCountry(country);
							comment.setAsin(asin);
							if(StringUtils.isNotEmpty(title)){
								comment.setSubject(stringFilter(stripControlChars(title)));
							}
							rs.put(comment.getReviewId(), comment);
							
							comment.setCreateDate(new Date());
						}
					}	
					// 匹配下一页;
					if(rs.size()==20*i){
						i++;
					}else{
						i=-1;
					}
				}
			}
		}catch(Exception ex){
			logger.error("扫描评论星星异常："+ex.getMessage(), ex);
		}
		return rs;
	}
	
	private static String findRealAsin(String url){
		try {
			return url.substring(url.indexOf("product-reviews/")+16 ,url.indexOf("/ref"));
		} catch (Exception e) {}
		return null;
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
	
	public static String getReviewsLinkByStar(Integer num,String starType,String asin,String country){
		String suffix = country;
		if("jp,uk".contains(suffix)){
			suffix = "co."+suffix;
		}else if ("mx".equals(suffix)){
			suffix = "com."+suffix;
		}
		String link = "https://www.amazon."
				+ suffix
				+ "/ss/customer-reviews/ajax/reviews/get/ref=cm_cr_pr_viewopt_sr?sortBy=recent&reviewerType=all_reviews&formatType=current_format&filterByStar="+starType+"&pageNumber="
				+num+"&pageSize=20&asin=" + asin;
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
	
//	public static void main(String [] arr){
//			ProductDirectoryCommentDetailMonitor mon = new ProductDirectoryCommentDetailMonitor();
//			ProductDirectoryComment detail = new ProductDirectoryComment();
//			detail.setCountry("com");
//			detail.setAsin("B00LE5VV1C");
//			mon.reqStar(detail);
//	}

}
