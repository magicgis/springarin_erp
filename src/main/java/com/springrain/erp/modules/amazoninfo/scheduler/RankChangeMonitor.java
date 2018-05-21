package com.springrain.erp.modules.amazoninfo.scheduler;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.google.common.collect.Lists;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonCatalogRank;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsDetail;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.psi.service.PsiProductService;


public class RankChangeMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RankChangeMonitor.class);
	
	@Autowired
	private MailManager mailManager;
	@Autowired
	private AmazonPostsDetailService amazonPostsDetailService;

	@Autowired
	private PsiProductService		 psiProductService;
	@Autowired
	private AmazonCustomerService amazonCustomerService;
	
	
	public void sendEmailRank() {  
		LOGGER.info("开始查询排名变动产品...");
		//Map<String,List<AmazonCatalogRank>> map=amazonPostsDetailService.getChangeRankMap();
		Map<String,Map<String,List<AmazonCatalogRank>>> mapList=amazonPostsDetailService.getChangeRankMap2();
		Map<String,Map<String,AmazonPostsDetail>> starMap=amazonPostsDetailService.getChangeStar();
		Map<String,Map<String,List<String>>> reviewMap=amazonCustomerService.getReviews();
		Map<String,List<AmazonPostsDetail>> lowStarMap=amazonPostsDetailService.getLowStar();
		StringBuffer contents= new StringBuffer("");
		List<String> countryList=Lists.newArrayList("de","com","uk","fr","jp","it","es","ca","mx");
		for (String sortCountry : countryList) {
			Map<String,AmazonPostsDetail> data=starMap.get(sortCountry);
			if(data!=null&&data.size()>0){
				int flag=0;
				for (Map.Entry<String,AmazonPostsDetail> entry : data.entrySet()) {  
				    String asin =entry.getKey();
					AmazonPostsDetail post=entry.getValue();
					String[] starStr=post.getCompareStar().split(",");
					//Float compare=Float.parseFloat(starStr[0])-Float.parseFloat(starStr[1]);
					Float compare=(new BigDecimal(starStr[0])).subtract(new BigDecimal(starStr[1])).floatValue();
					String suffix=post.getCountry();
					if("jp,uk".contains(post.getCountry())){
						suffix="co."+post.getCountry();
					}else if("mx".equals(post.getCountry())){
						suffix="com."+post.getCountry();
					}
					String key=asin+"_"+sortCountry;
					Map<String,List<String>> review=reviewMap.get(key);
					if(review!=null&&review.size()>0){
						if(flag==0){
							contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
							contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#4EFEB3;color:#666;'><th colspan='9' style='border-left:1px solid;border-top:1px solid;color:#666;'>"+("com".equals(sortCountry)?"US":sortCountry.toUpperCase())+"星级评分变动情况表(因网络问题有可能导致抓取数据不完整性而引起的误差,请自行核实)</th>");
							contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#4EFEB3;color:#666;'><th style='border-left:1px solid;border-top:1px solid;color:#666;'>product_name</th>");
							contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>昨日评分</th>");
							contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>今日评分</th>");
							contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>5 star</th><th style='border-left:1px solid;border-top:1px solid;color:#666;'>4 star</th>");
							contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>3 star</th><th style='border-left:1px solid;border-top:1px solid;color:#666;'>2 star</th>");
							contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>1 star</th><th style='border-left:1px solid;border-top:1px solid;color:#666;'>总共评论</th>");
							contents.append("</tr>");
						}
						contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'><a href=http://www.amazon."+suffix+"/dp/"+post.getAsin()+" target='_blank'>"+post.getProductName()+"</a></td>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'><font color="+(compare>0?"green":"red")+">"+starStr[1]+"</font></td>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'><font color="+(compare>0?"green":"red")+">"+starStr[0]+"</font></td>");
							List<String> reviewList5=review.get("5");
							if(reviewList5!=null&&reviewList5.size()>0){
								contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>");
								for (String reviewAsin: reviewList5) {
									contents.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='green'>"+5+"</font></a>&nbsp;&nbsp;");
								}
								contents.append("</td>");
							}else{
								contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'></td>");
							}
							
							List<String> reviewList4=review.get("4");
							if(reviewList4!=null&&reviewList4.size()>0){
								contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>");
								for (String reviewAsin: reviewList4) {
									contents.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='green'>"+4+"</font></a>&nbsp;&nbsp;");
								}
								contents.append("</td>");
							}else{
								contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'></td>");
							}
							
							List<String> reviewList3=review.get("3");
							if(reviewList3!=null&&reviewList3.size()>0){
								contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>");
								for (String reviewAsin: reviewList3) {
									contents.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='red'>"+3+"</font></a>&nbsp;&nbsp;");
								}
								contents.append("</td>");
							}else{
								contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'></td>");
							}
							
							List<String> reviewList2=review.get("2");
							if(reviewList2!=null&&reviewList2.size()>0){
								contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>");
								for (String reviewAsin: reviewList2) {
									contents.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='red'>"+2+"</font></a>&nbsp;&nbsp;");
								}
								contents.append("</td>");
							}else{
								contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'></td>");
							}
							
							List<String> reviewList1=review.get("1");
							if(reviewList1!=null&&reviewList1.size()>0){
								contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>");
								for (String reviewAsin: reviewList1) {
									contents.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='red'>"+1+"</font></a>&nbsp;&nbsp;");
								}
								contents.append("</td>");
							}else{
								contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'></td>");
							}
							
							contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+post.getStar1()+"</td>");
							
						
						contents.append("</tr>");
						flag++;
					}
				}
				if(flag!=0){
					contents.append("</table>");
				}
			}
			
			
			List<AmazonPostsDetail> lowStar=lowStarMap.get(sortCountry);	
			if(lowStar!=null&&lowStar.size()>0){
				contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#FA8072;color:#666;'><th colspan='7' style='border-left:1px solid;border-top:1px solid;color:#666;'>"+("com".equals(sortCountry)?"US":sortCountry.toUpperCase())+"低分情况表(单帖评分,非组合帖评分)</th>");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#FA8072;color:#666;'><th style='border-left:1px solid;border-top:1px solid;color:#666;'>product_name</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>评分</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>5 star</th><th style='border-left:1px solid;border-top:1px solid;color:#666;'>4 star</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>3 star</th><th style='border-left:1px solid;border-top:1px solid;color:#666;'>2 star</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>1 star</th>");
				contents.append("</tr>");
				for (AmazonPostsDetail detail : lowStar) {
					String suffix=detail.getCountry();
					if("jp,uk".contains(detail.getCountry())){
						suffix="co."+detail.getCountry();
					}else if("mx".equals(detail.getCountry())){
						suffix="com."+detail.getCountry();
					}
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'><a href=http://www.amazon."+suffix+"/dp/"+detail.getAsin()+" target='_blank'>"+detail.getProductName()+"</a></td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+detail.getStar()+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+detail.getStar5()+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+detail.getStar4()+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+detail.getStar3()+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+detail.getStar2()+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+detail.getStar1()+"</td>");
					contents.append("</tr>");
					
				}
				contents.append("</table>");
			}
			
			
			Map<String,List<AmazonCatalogRank>> temp=mapList.get(sortCountry);
			if(temp!=null&&temp.size()>0){
				contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#9ACD32;color:#666;'><th colspan='5' style='border-left:1px solid;border-top:1px solid;color:#666;'>"+("com".equals(sortCountry)?"US":sortCountry.toUpperCase())+"排名变动情况表</th>");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#9ACD32;color:#666;'><th style='border-left:1px solid;border-top:1px solid;color:#666;'>product_name</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>catalog_name</th><th style='border-left:1px solid;border-top:1px solid;color:#666;'>昨日排名</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>今日排名</th><th style='border-left:1px solid;border-top:1px solid;color:#666;'>变动情况</th>");
				contents.append("</tr>");
				for (Map.Entry<String,List<AmazonCatalogRank>> entry : temp.entrySet()) { 
					int num=0;
					int length=entry.getValue().size();
					for (AmazonCatalogRank rank :entry.getValue()) {
						String[] rankStr=rank.getRankStr().split(",");
						String link=rank.getCountry();
						String suffix=rank.getCountry();
						Integer compare=Integer.parseInt(rankStr[0])-Integer.parseInt(rankStr[1]);
						if("jp,uk".contains(rank.getCountry())){
							suffix="co."+rank.getCountry();
							link = "co."+link;
						}else if("mx".equals(rank.getCountry())){
							suffix="com."+rank.getCountry();
							link = "com."+link;
						}
						String catalog="http://www.amazon."+link+"/gp/bestsellers/computers/"+rank.getCatalog();
						contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
						if(num==0){
							contents.append("<td rowspan="+length+"  style='border-left:1px solid;border-top:1px solid;color:#666;'><a href=http://www.amazon."+suffix+"/dp/"+rank.getAsin()+" target='_blank'>"+rank.getProductName()+"</a></td>");
							num++;
						}
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'><a href="+catalog+" target='_blank'>"+(StringUtils.isBlank(rank.getCatalogName())?"未找到目录名称":rank.getCatalogName())+"</a></td>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'><font color="+(compare>0?"red":"green")+">"+rankStr[1]+"</font></td>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'><font color="+(compare>0?"red":"green")+">"+rankStr[0]+"</font></td>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'><font color="+(compare>0?"red":"green")+">"+(compare>0?("↓ "+compare+"名"):("↑ "+compare.toString().replace("-", "")+"名"))+"</font></td>");
						contents.append("</tr>");
					}
				}
				contents.append("</table><br/><br/><br/>");
			}
			
		}
		
		
		if(StringUtils.isNotEmpty(contents)){
			Date date = new Date();
			//发信给产品创建人员：
			//String  toAddress="eileen@springrain.eu";
			String  toAddress="amazon-sales@inateck.com";
			final MailInfo mailInfo = new MailInfo(toAddress,"产品排名及星级变动幅度提醒("+DateUtils.getDate("yyyy/MM/dd")+")",date);
			mailInfo.setContent(contents.toString());
			//mailInfo.setCcToAddress("eileen@springrain.eu");
			mailInfo.setCcToAddress("frank@inateck.com,maik@inateck.com,after-sales@inateck.com,supply-chain@inateck.com,tim@inateck.com,eileen@inateck.com");
			new Thread(){
				public void run(){
					mailManager.send(mailInfo);
					//new MailManager().send(mailInfo);
				}
			}.start();
		}
	}


	public AmazonPostsDetailService getAmazonPostsDetailService() {
		return amazonPostsDetailService;
	}


	public void setAmazonPostsDetailService(
			AmazonPostsDetailService amazonPostsDetailService) {
		this.amazonPostsDetailService = amazonPostsDetailService;
	}

	
	public PsiProductService getPsiProductService() {
		return psiProductService;
	}


	public void setPsiProductService(PsiProductService psiProductService) {
		this.psiProductService = psiProductService;
	}


	public AmazonCustomerService getAmazonCustomerService() {
		return amazonCustomerService;
	}


	public void setAmazonCustomerService(AmazonCustomerService amazonCustomerService) {
		this.amazonCustomerService = amazonCustomerService;
	}


	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AmazonPostsDetailService amazonPostsDetailService = applicationContext.getBean(AmazonPostsDetailService.class);
		PsiProductService psiProductService = applicationContext.getBean(PsiProductService.class);
		AmazonCustomerService amazonCustomerService = applicationContext.getBean(AmazonCustomerService.class);
		RankChangeMonitor aarehouseDeProductMonitor = new RankChangeMonitor();
		aarehouseDeProductMonitor.setAmazonPostsDetailService(amazonPostsDetailService);
		aarehouseDeProductMonitor.setPsiProductService(psiProductService);
		aarehouseDeProductMonitor.setAmazonCustomerService(amazonCustomerService);
		aarehouseDeProductMonitor.sendEmailRank();
		applicationContext.close();
	}

}
