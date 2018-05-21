package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.HttpRequest;
import com.springrain.erp.modules.amazoninfo.entity.AmazonReview;
import com.springrain.erp.modules.amazoninfo.entity.ProductReviewMonitor;
import com.springrain.erp.modules.amazoninfo.htmlunit.CountryType;
import com.springrain.erp.modules.amazoninfo.htmlunit.LocaleUtil;
import com.springrain.erp.modules.amazoninfo.service.ProductReviewMonitorService;

public class ProductOutReviewsMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ProductOutReviewsMonitor.class);
	
	@Autowired
	private ProductReviewMonitorService productReviewMonitorService;
	
	public void getRewiews() {
		LOGGER.info("开始扫描竞争对手差评");
		List<ProductReviewMonitor> list =  productReviewMonitorService.findAllByE();
		Date today = new Date();
		for(ProductReviewMonitor monitor : list) {
			Map<String,AmazonReview> rs = findReviews(monitor);
			List<AmazonReview> reviews = monitor.getReviews();
			Map<String,AmazonReview> oldMap = Maps.newHashMap();
			for (AmazonReview amazonReview : reviews) {
				String id = amazonReview.getReviewId();
				oldMap.put(id, amazonReview);
				AmazonReview newEst = rs.get(id);
				if(newEst==null){
					amazonReview.setState("0");
				}else{
					amazonReview.setContent1(newEst.getContent());
					amazonReview.setStar1(newEst.getStar());
					amazonReview.setSubject1(newEst.getSubject());
				}
				amazonReview.setLastUpdateDate(today);
			}
			for (AmazonReview amazonReview : rs.values()) {
				String id = amazonReview.getReviewId();
				AmazonReview old = oldMap.get(id);
				//新增
				if(old==null){
					amazonReview.setReviewMonitor(monitor);
					amazonReview.setSubjectShow(amazonReview.getSubject());
					amazonReview.setStarShow(amazonReview.getStar());
					amazonReview.setContentShow(amazonReview.getContent());
					reviews.add(amazonReview);
					amazonReview.setLastUpdateDate(today);
				}
			}
			productReviewMonitorService.save(reviews);
		};
		LOGGER.info("扫描竞争对手差评结束");
    }	
	
//	private static Pattern pattern = Pattern.compile("\\[\"append\",\"#cm_cr-review_list\",\".[^\\]]+\"\\]");
//	private static Pattern pattern1 = Pattern.compile("\\[\"update\",\"#cm_cr-review_list\",\".[^\\]]+\"\\]");
	private static Pattern pattern = Pattern.compile("\\[\"append\",\"#cm_cr-review_list\",\".+?\"\\]");
	private static Pattern pattern1 = Pattern.compile("\\[\"update\",\"#cm_cr-review_list\",\".+?\"\\]");
	
	public Map<String,AmazonReview> findReviews(ProductReviewMonitor monitor){
		int i = 1;
		Map<String,AmazonReview> rs = Maps.newHashMap();
		while(i>0){
			String temp = HttpRequest.reqUrlStr(monitor.getBadReviewsLink(i), null, true);
			String country = monitor.getCountry();
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
						AmazonReview comment = new AmazonReview();
						
						String url = doc.select("div.review-data a").attr("href");
						
						// 评论发表时间;
						String datetime = doc.select(
								"span.review-date").text();
						if (!StringUtils.isEmpty(datetime)) {
							try {
								Date create_date = LocaleUtil.formatDate(datetime, LocaleUtil.FORMAT_SHORT, CountryType
										.getCountryTypeByEsayName(suffix).getLocale());
								comment.setReviewDate(create_date);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						// 评论asin
						String id = doc.select(".review").get(0).attr("id");
						comment.setReviewId(id);
						// 评论等级
						String star = doc.select(
								"span.a-icon-alt").text();
						if (!StringUtils.isEmpty(star)) {
							String tempStar = star;
							if (tempStar != null) {
								// page.putField("star",
								// star.substring(0,star.indexOf(".")).trim());
								try {
									Integer starNum = Integer.parseInt(findStarByNew(tempStar));
									// 只扫好评;
									if (starNum > 3) {
										continue;
									}
									comment.setStar(starNum+"");
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						if(StringUtils.isNotEmpty(url)){
							if(!findRealAsin(url).equals(monitor.getAsin())){
								continue;
							}
						}
						// 评论主题：
						String title = doc.select(
											"a.review-title").text();
						String content = doc.select(
								"span.review-text").html();
	
						Elements selectable1 = doc.select("div.video-block");
						if (selectable1 != null && selectable1.html() != null
								&& content != null) {
							content = content.replace(selectable1.toString(), "");
						}
						if (!StringUtils.isEmpty(content)) {
							content = content.replaceAll(
									"<script[^>]*>[\\d\\D]*?</script>", "");
							comment.setContent(content);
						}
						// 评论人
						String commentator = doc.select(
								"a.author").text();
						if (!StringUtils.isEmpty(commentator)) {
							comment.setCustomerName(commentator);
						}
						String commentator_asin = doc.select(
								"a.author").attr("href");
	
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
	
						comment.setSubject(title);
						rs.put(comment.getReviewId(), comment);
					}
				}	
				// 匹配下一页;
				if(rs.size()==50*i){
					i++;
				}else{
					i=-1;
				}
			}
		}
		return rs;
	}
	
	private String findRealAsin(String url){
		return url.substring(
				url.indexOf("product-reviews/")+16 ,
				url.indexOf("/ref"));
	}
	
	private String findStarByNew(String str) {
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
	
	public static void main(String[] args) {
		ProductReviewMonitor m = new ProductReviewMonitor();
		m.setCountry("com");
		m.setAsin("B00JO6UGFU");
		Map<String, AmazonReview>  map = new ProductOutReviewsMonitor().findReviews(m);
		System.out.println(map);
	}
}
