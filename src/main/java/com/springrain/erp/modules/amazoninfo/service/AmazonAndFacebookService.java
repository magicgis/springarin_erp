package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonFacebookRelationshipDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonFacebookReportDao;
import com.springrain.erp.modules.amazoninfo.dao.FacebookReportDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonFacebookRelationship;
import com.springrain.erp.modules.amazoninfo.entity.AmazonFacebookReport;
import com.springrain.erp.modules.amazoninfo.entity.FacebookDto;
import com.springrain.erp.modules.amazoninfo.entity.FacebookReport;


@Component
@Transactional(readOnly = true)
public class AmazonAndFacebookService extends BaseService {

	@Autowired
	private AmazonFacebookRelationshipDao amazonFacebookRelationshipDao;
	
	@Autowired
	private AmazonFacebookReportDao amazonFacebookReportDao;
	
	@Autowired
	private FacebookReportDao facebookReportDao;
	
	@Autowired
	private ProductPriceService productPriceService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private AdvertisingService advertisingService;
	
	@Autowired
	private SaleProfitService saleProfitService;
	
	public Page<AmazonFacebookReport> find(Page<AmazonFacebookReport> page, AmazonFacebookReport amazonFacebookReport) {
		DetachedCriteria dc = amazonFacebookReportDao.createDetachedCriteria();
		dc.add(Restrictions.ge("dateShipped",amazonFacebookReport.getDateShipped()));
		dc.add(Restrictions.le("dateShipped",amazonFacebookReport.getEndDate()));
		
		dc.add(Restrictions.eq("delFlag","0"));
		dc.addOrder(Order.desc("dateShipped"));
		return amazonFacebookReportDao.find(page, dc);
	}
	
	public Page<FacebookReport> find(Page<FacebookReport> page, FacebookReport facebookReport) {
		DetachedCriteria dc = facebookReportDao.createDetachedCriteria();
	    
		dc.add(Restrictions.ge("start",facebookReport.getStart()));
		dc.add(Restrictions.le("start",facebookReport.getEnd()));
		
		dc.add(Restrictions.eq("delFlag","0"));
		dc.addOrder(Order.desc("start"));
		return facebookReportDao.find(page, dc);
	}
	
	
	public Page<AmazonFacebookRelationship> find(Page<AmazonFacebookRelationship> page, AmazonFacebookRelationship amazonFacebookRelationship) {
		DetachedCriteria dc = amazonFacebookRelationshipDao.createDetachedCriteria();
	    
		dc.add(Restrictions.ge("date",amazonFacebookRelationship.getDate()));
		dc.add(Restrictions.le("date",amazonFacebookRelationship.getEndDate()));
		
		dc.add(Restrictions.eq("delFlag","0"));
		dc.addOrder(Order.desc("date"));
		return amazonFacebookRelationshipDao.find(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public void saveRelationship(List<AmazonFacebookRelationship> amazonFacebookRelationship) {
		amazonFacebookRelationshipDao.save(amazonFacebookRelationship);
	}
	
	public AmazonFacebookRelationship findRelation(Integer id){
		return amazonFacebookRelationshipDao.get(id);
	}
	
	public Integer isExist(String adId){
		String sql="select id from amazoninfo_facebook_relationship where ad_id=:p1 and del_flag='0'";
		List<Integer> list=amazonFacebookRelationshipDao.findBySql(sql,new Parameter(adId));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void deleteRelation(Integer id) {
		String sql="update amazoninfo_facebook_relationship set del_flag='1' where id=:p1 ";
		amazonFacebookRelationshipDao.updateBySql(sql,new Parameter(id));
	}
	
	@Transactional(readOnly = false)
	public void deleteRelation(Set<String> id) {
		String sql="update amazoninfo_facebook_relationship set del_flag='1' where id in :p1 ";
		amazonFacebookRelationshipDao.updateBySql(sql,new Parameter(id));
	}
	
	@Transactional(readOnly = false)
	public void saveAmazonFacebookReport(List<AmazonFacebookReport> amazonFacebookReport) {
		amazonFacebookReportDao.save(amazonFacebookReport);
	}
	
	@Transactional(readOnly = false)
	public void deleteAmazonFacebook(Integer id) {
		String sql="update amazoninfo_facebook_report set del_flag='1' where id=:p1 ";
		amazonFacebookReportDao.updateBySql(sql,new Parameter(id));
	}
	
	@Transactional(readOnly = false)
	public void deleteAmazonFacebook(Set<String> id) {
		String sql="update amazoninfo_facebook_report set del_flag='1' where id in :p1 ";
		amazonFacebookReportDao.updateBySql(sql,new Parameter(id));
	}
	
	@Transactional(readOnly = false)
	public void saveFacebookReport(List<FacebookReport> facebookReport) {
		facebookReportDao.save(facebookReport);
	}
	
	@Transactional(readOnly = false)
	public void deleteFacebook(Integer id) {
		String sql="update facebook_report set del_flag='1' where id=:p1 ";
		facebookReportDao.updateBySql(sql,new Parameter(id));
	}
	
	@Transactional(readOnly = false)
	public void deleteFacebook(Set<String> id) {
		String sql="update facebook_report set del_flag='1' where id in :p1 ";
		facebookReportDao.updateBySql(sql,new Parameter(id));
	}
	
	
	public Map<String,FacebookDto> findAmountSpend2(FacebookDto facebookDto){
		Map<String,FacebookDto> map=Maps.newHashMap();
		Map<String,Float> priceMap=productPriceService.findAllProducSalePrice("USD");//name_country
		Map<String,Map<String, Float>> rateMap=amazonProduct2Service.getRate(facebookDto.getStarts(), facebookDto.getEnd());//date-name-rate
		String sql="SELECT f.`start`,r.`product`,r.`audience`,r.`age`,r.`gender`,r.`market`,r.`placement`,f.`ad_name`,f.`amount_spent`,f.`link_clicks`, "+
			" round(f.`link_clicks`/f.`impressions`,2) ctr,round(f.`amount_spent`/f.`link_clicks`,2) cpc,round(f.`amount_spent`/f.`impressions`*1000,2) cpm, "+
		    " f.impressions,r.product_line,r.tracking_id,r.ad_id,f.relevance_score,f.negative_feedback,f.post_comments, "+
		    " f.`amount_spent`/f.post_engagement  cost_per_post_engagement,f.`amount_spent`/f.page_likes cost_per_page_like,f.`amount_spent`/f.post_shares cost_per_post_share, "+
			" f.post_shares,f.page_likes,f.post_engagement,r.pre_view,	"+
			"  SUM(CASE WHEN p.product_name LIKE 'Inateck%' OR p.product_name LIKE 'Tomons%' THEN t.items_shipped ELSE 0 END) all_items_shipped, "+
			" SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.items_shipped ELSE 0 END) same_items_shipped,round(SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.items_shipped ELSE 0 END)/f.`link_clicks`,2) sameCr,round(SUM(t.items_shipped)/f.`link_clicks`,2) totalCr,SUM(t.advertising_fees) total_advertising_fees, "+
			" SUM(CASE WHEN p.product_name LIKE 'Inateck%' OR p.product_name LIKE 'Tomons%' THEN t.revenue ELSE 0 END) total_revenue, "+
			" GROUP_CONCAT(CONCAT(CONCAT(p.`product_name`,CASE WHEN p.`color`!='' THEN '_' ELSE '' END,p.`color`),'_',t.items_shipped)) nameAndQuantity, "+
			" SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.advertising_fees ELSE 0 END) totalAffiliateFees "+
			" FROM facebook_report f "+
			" JOIN amazoninfo_facebook_relationship r ON f.`ad_id`=r.`ad_id`  "+ 
			" JOIN amazoninfo_facebook_report t ON t.tracking_id=r.tracking_id AND t.market=r.market "+
			" LEFT JOIN (select distinct country,asin,product_name,color from  psi_sku where p.del_flag='0')  p ON p.country=t.market AND p.asin=t.asin  "+
			" WHERE f.`del_flag`='0' AND r.`del_flag`='0' AND t.del_flag='0' and f.`start`>=:p1 and f.`start`<=:p2 and f.ad_id=:p3 "+
			" GROUP BY f.`start`,r.`product`,r.`audience`,r.`age`,r.`gender`,r.`market`,r.`placement`,f.`ad_name`,f.`amount_spent`,f.`link_clicks`,ctr,cpc,cpm,"+
			" f.impressions,r.product_line,r.tracking_id,r.ad_id,f.relevance_score,f.negative_feedback,f.post_comments,cost_per_post_engagement,cost_per_page_like,cost_per_post_share,"+
			" f.post_shares,f.page_likes,f.post_engagement,r.pre_view	";
			
		List<Object[]> list=facebookReportDao.findBySql(sql,new Parameter(facebookDto.getStarts(),facebookDto.getEnd(),facebookDto.getAdId()));
		for (Object[] obj: list) {
			Date start=(Date)obj[0];
			String product=obj[1].toString();
			String audience=obj[2].toString();
			String age=obj[3].toString();
			String gender=obj[4].toString();
			String market=obj[5].toString();
			float rate=1f;
			if(rateMap.get(DateUtils.getDate((Date)start,"yyyy-MM-dd"))!=null){
				if("de,fr,it,es".contains(market)){
			       rate=rateMap.get(DateUtils.getDate((Date)start,"yyyy-MM-dd")).get("EUR/USD");
				}else if("uk".equals(market)){
					rate=rateMap.get(DateUtils.getDate((Date)start,"yyyy-MM-dd")).get("GBP/USD");	
				}else if("jp".equals(market)){
					rate=rateMap.get(DateUtils.getDate((Date)start,"yyyy-MM-dd")).get("JPY/USD");
				}
			}else{
				if("de,fr,it,es".contains(market)){
				    rate=MathUtils.getRate("EUR","USD",null);
				}else if("uk".equals(market)){
					rate=MathUtils.getRate("GBP","USD",null);
				}else if("jp".equals(market)){
					rate=MathUtils.getRate("JPY","USD",null);
				}
			}
			
			String placement=obj[6].toString();
			String adName=obj[7].toString();
			float amountSpend=((BigDecimal)obj[8]).floatValue();
			Integer linkClicks=Integer.parseInt(obj[9].toString());
			float ctr=Float.parseFloat(obj[10].toString());
			float cpc=Float.parseFloat(obj[11].toString());
			float cpm=Float.parseFloat(obj[12].toString());
			Float impressions=Float.parseFloat(obj[13].toString());
			String productLine=obj[14].toString(); 
			String trackingId=obj[15].toString();
			String adId=obj[16].toString();
			float relevanceScore=Float.parseFloat(obj[17].toString());
			String negativeFeedback=obj[18].toString();
			float postComments=Float.parseFloat(obj[19].toString());
			float costPerPostEngagement=Float.parseFloat(obj[20]==null?"0":obj[20].toString())*rate;
			float costPerPageLike=Float.parseFloat(obj[21]==null?"0":obj[21].toString())*rate;
			float costPerPostShare=Float.parseFloat(obj[22]==null?"0":obj[22].toString())*rate;
			float postEngagement=Float.parseFloat(obj[23].toString());
			float postShares=Float.parseFloat(obj[24].toString());
			float pageLikes=Float.parseFloat(obj[25].toString());
			String preView=obj[26].toString();
			Integer allItemsShipped=Integer.parseInt(obj[27].toString());
			Integer sameItemsShipped=Integer.parseInt(obj[28].toString());
			float sameCr=Float.parseFloat(obj[29].toString());
			float totalCr=Float.parseFloat(obj[30].toString());
			float totalAdvertisingFees=Float.parseFloat(obj[31].toString())*rate;
			float totalRevenue=Float.parseFloat(obj[32].toString())*rate;
			String nameAndQuantity=obj[33].toString();
			Float totalAffiliateFees=Float.parseFloat(obj[34].toString())*rate;
			Float totalCost=0f;
			if(StringUtils.isNotBlank(nameAndQuantity)){
				if(nameAndQuantity.contains(",")){
					String[] name_quantitys=nameAndQuantity.split(",");
					for (String name_quantity: name_quantitys) {
						String name=name_quantity.substring(0, name_quantity.lastIndexOf("_"));
						Integer quantity=Integer.parseInt(name_quantity.substring(name_quantity.lastIndexOf("_")+1));
						if((name.contains("Inateck")||name.contains("Tomons"))&&priceMap.get(name+"_"+market)!=null){
							totalCost+=priceMap.get(name+"_"+market)*quantity;
						}
					}
				}else{
					String name=nameAndQuantity.substring(0, nameAndQuantity.lastIndexOf("_"));
					Integer quantity=Integer.parseInt(nameAndQuantity.substring(nameAndQuantity.lastIndexOf("_")+1));
					if((name.contains("Inateck")||name.contains("Tomons"))&&priceMap.get(name+"_"+market)!=null){
						totalCost+=priceMap.get(name+"_"+market)*quantity;
					}
				}
			}
			Float profit=totalRevenue-totalCost;
			Float roi=(totalAdvertisingFees+profit*1.5f)/amountSpend;// (Total Advertising Fees+Profit*1.5)/Amount Spent
			Float relativeRoi=(totalAdvertisingFees+profit)/amountSpend;//(Total Advertising Fees+Profit)/Amount Spent
			Float forecastCpc=1f;
			//(Profit/该Ad ID的inateck或tomons产品Items Shipped总量*500/预估CPC*Total CR*1.5+该Ad ID的Total Affiliate Fees/Link Clicks*500/预估CPC)/500
			//Float forecastRoi=(profit/sameItemsShipped/forecastCpc*totalCr*1.5f+totalAffiliateFees/linkClicks*500f/forecastCpc)/500f;
			
			Float forecastRoi=1f;
			map.put(DateUtils.getDate((Date)start,"yyyy-MM-dd"),new FacebookDto(start,product,audience,age,gender,market,placement,adName,amountSpend,linkClicks,ctr,cpc,cpm, allItemsShipped, totalCr,
			profit,totalAdvertisingFees,roi,impressions,sameItemsShipped,sameCr,totalRevenue, relativeRoi,productLine,adId, trackingId,relevanceScore,
			negativeFeedback,postComments,costPerPostEngagement, costPerPageLike,costPerPostShare,postEngagement,postShares,pageLikes, preView,forecastCpc,forecastRoi,totalAffiliateFees));
		}
		return map;
	}
	
	
	public Map<String,FacebookDto> findAmountSpend(FacebookDto facebookDto){
		Map<String,FacebookDto> map=Maps.newHashMap();
		Map<String,Map<String, Float>> rateMap=amazonProduct2Service.getRate(facebookDto.getStarts(), facebookDto.getEnd());//date-name-rate
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		Map<String,FacebookDto> amazonMap=Maps.newHashMap();
		//ROUND(SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.items_shipped ELSE 0 END)/f.`link_clicks`,2) sameCr,ROUND(SUM(t.items_shipped)/f.`link_clicks`,2) totalCr,
		String amazonSql="select  t.`date_shipped`,r.ad_id,SUM(CASE WHEN p.product_name LIKE 'Inateck%' OR p.product_name LIKE 'Tomons%' THEN t.items_shipped ELSE 0 END) all_items_shipped, " +
				" SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.items_shipped ELSE 0 END) same_items_shipped,SUM(t.advertising_fees) total_advertising_fees, " + 
				" SUM(CASE WHEN p.product_name LIKE 'Inateck%' OR p.product_name LIKE 'Tomons%' THEN t.revenue ELSE 0 END) total_revenue,  " +
				" GROUP_CONCAT(CONCAT(CONCAT(p.`product_name`,CASE WHEN p.`color`!='' THEN '_' ELSE '' END,p.`color`),'_',t.items_shipped)) nameAndQuantity, " +
				" SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.advertising_fees ELSE 0 END) totalAffiliateFees, " +
				" SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.items_shipped ELSE 0 END),SUM(t.items_shipped),sum(t.profit) profit "+
				" from  amazoninfo_facebook_relationship r  JOIN amazoninfo_facebook_report t ON t.tracking_id=r.tracking_id AND t.market=r.market  "+
				" LEFT JOIN (select distinct country,asin,product_name,color from  psi_sku where del_flag='0')  p ON p.country=t.market AND p.asin=t.asin  "+
				" WHERE r.`del_flag`='0' and t.del_flag='0' and t.`date_shipped`>=:p1 and t.`date_shipped`<=:p2 and r.ad_id=:p3 ";
				
				amazonSql+=" GROUP BY t.`date_shipped`,r.ad_id ";
				List<Object[]> list2=facebookReportDao.findBySql(amazonSql,new Parameter(facebookDto.getStarts(),facebookDto.getEnd(),facebookDto.getAdId()));
				for (Object[] obj: list2) {
					Date start=(Date)obj[0];
					String adId=obj[1].toString();
					Integer allItemsShipped=Integer.parseInt(obj[2].toString());
					Integer sameItemsShipped=Integer.parseInt(obj[3].toString());
				//	float sameCr=Float.parseFloat(obj[29]==null?"0":obj[29].toString());
				//	float totalCr=Float.parseFloat(obj[30]==null?"0":obj[30].toString());
					float totalAdvertisingFees=Float.parseFloat(obj[4]==null?"0":obj[4].toString());
					float totalRevenue=Float.parseFloat(obj[5]==null?"0":obj[5].toString());
					String nameAndQuantity=(obj[6]==null?"":obj[6].toString());
					Float totalAffiliateFees=Float.parseFloat(obj[7].toString());
					float sameCr=Float.parseFloat(obj[8]==null?"0":obj[8].toString());
					float totalCr=Float.parseFloat(obj[2]==null?"0":obj[2].toString());
					float profit=Float.parseFloat(obj[10]==null?"0":obj[10].toString());
					FacebookDto dto=new FacebookDto();
					dto.setAdId(adId);
					dto.setStarts(start);
					dto.setTotalAdvertisingFees(totalAdvertisingFees);
					dto.setTotalRevenue(totalRevenue);
					dto.setProductName(nameAndQuantity);
					dto.setTotalAffiliateFees(totalAffiliateFees);
					dto.setSameCr(sameCr);
					dto.setTotalCr(totalCr);
					dto.setAllItemsShipped(allItemsShipped);
					dto.setSameItemsShipped(sameItemsShipped);
					dto.setProfit(profit);
					amazonMap.put(dateFormat.format(start)+adId, dto);
				}
				
				
		
		String sql="SELECT f.`start`,r.`product`,r.`audience`,r.`age`,r.`gender`,r.`market`,r.`placement`,f.`ad_name`,sum(f.`amount_spent`),sum(f.`link_clicks`), "+
			" round(sum(f.`link_clicks`)*100/sum(f.`impressions`),2) ctr,round(sum(f.`amount_spent`)/sum(f.`link_clicks`),2) cpc,round(sum(f.`amount_spent`)/sum(f.`impressions`)*1000,2) cpm, "+
		    " sum(f.impressions),r.product_line,r.tracking_id,r.ad_id,sum(f.relevance_score),GROUP_CONCAT(distinct f.negative_feedback),sum(f.post_comments), "+
		    " sum(f.`amount_spent`)/sum(f.post_engagement)  cost_per_post_engagement,sum(f.`amount_spent`)/sum(f.page_likes) cost_per_page_like,sum(f.`amount_spent`)/sum(f.post_shares) cost_per_post_share, "+
			" sum(f.post_shares),sum(f.page_likes),sum(f.post_engagement),r.pre_view,r.asin_on_ad	"+
			" FROM amazoninfo_facebook_relationship r "+
			" JOIN facebook_report f ON f.`ad_id`=r.`ad_id`  "+ 
			" WHERE f.`del_flag`='0' AND r.`del_flag`='0'  and f.`start`>=:p1 and f.`start`<=:p2  and  f.`ad_id`=:p3 ";
		   
			sql+=" GROUP BY f.`start`,r.`product`,r.`audience`,r.`age`,r.`gender`,r.`market`,r.`placement`,f.`ad_name`,r.pre_view,r.product_line,r.tracking_id,r.ad_id,r.asin_on_ad ";
			
		List<Object[]> list=facebookReportDao.findBySql(sql, new Parameter(facebookDto.getStarts(),facebookDto.getEnd(),facebookDto.getAdId()));
		for (Object[] obj: list) {
			Date start=(Date)obj[0];
			String product=obj[1].toString();
			String audience=obj[2].toString();
			String age=obj[3].toString();
			String gender=obj[4].toString();
			String market=obj[5].toString();
			float rate=1f;
			if(rateMap.get(DateUtils.getDate((Date)start,"yyyy-MM-dd"))!=null){
				if("de,fr,it,es".contains(market)){
			       rate=rateMap.get(DateUtils.getDate((Date)start,"yyyy-MM-dd")).get("EUR/USD");
				}else if("uk".equals(market)){
					rate=rateMap.get(DateUtils.getDate((Date)start,"yyyy-MM-dd")).get("GBP/USD");	
				}else if("jp".equals(market)){
					rate=rateMap.get(DateUtils.getDate((Date)start,"yyyy-MM-dd")).get("JPY/USD");
				}
			}else{
				if("de,fr,it,es".contains(market)){
				    rate=MathUtils.getRate("EUR","USD",null);
				}else if("uk".equals(market)){
					rate=MathUtils.getRate("GBP","USD",null);
				}else if("jp".equals(market)){
					rate=MathUtils.getRate("JPY","USD",null);
				}
			}
			
			String placement=obj[6].toString();
			String adName=obj[7].toString();
			float amountSpend=((BigDecimal)obj[8]).floatValue();
			Integer linkClicks=Integer.parseInt(obj[9].toString());
			float ctr=Float.parseFloat(obj[10]==null?"0":obj[10].toString());
			float cpc=Float.parseFloat(obj[11]==null?"0":obj[11].toString());
			float cpm=Float.parseFloat(obj[12]==null?"0":obj[12].toString());
			Float impressions=Float.parseFloat(obj[13]==null?"0":obj[13].toString());
			String productLine=obj[14].toString(); 
			String trackingId=obj[15].toString();
			String adId=obj[16].toString();
			float relevanceScore=Float.parseFloat(obj[17]==null?"0":obj[17].toString());
			String negativeFeedback=obj[18].toString();
			float postComments=Float.parseFloat(obj[19].toString());
			float costPerPostEngagement=Float.parseFloat(obj[20]==null?"0":obj[20].toString())*rate;
			float costPerPageLike=Float.parseFloat(obj[21]==null?"0":obj[21].toString())*rate;
			float costPerPostShare=Float.parseFloat(obj[22]==null?"0":obj[22].toString())*rate;
			float postEngagement=Float.parseFloat(obj[23]==null?"0":obj[23].toString());
			float postShares=Float.parseFloat(obj[24].toString());
			float pageLikes=Float.parseFloat(obj[25].toString());
			String preView=obj[26].toString();
			String asinOnAd=obj[27].toString();
			
			
			Integer allItemsShipped=0;
			Integer sameItemsShipped=0;
			float sameCr=0f;
			float totalCr=0f;
			float totalAdvertisingFees=0f;
			float totalRevenue=0f;
			Float totalAffiliateFees=0f;
			Float profit=0f;
			if(amazonMap!=null&&amazonMap.get(dateFormat.format(start)+adId)!=null){
				FacebookDto dto=amazonMap.get(dateFormat.format(start)+adId);
				allItemsShipped=dto.getAllItemsShipped();
				sameItemsShipped=dto.getSameItemsShipped();
				//ROUND(SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.items_shipped ELSE 0 END)/f.`link_clicks`,2) sameCr,ROUND(SUM(t.items_shipped)/f.`link_clicks`,2) totalCr,
				if(linkClicks>0){
					sameCr=dto.getSameCr()*100/linkClicks;
					totalCr=dto.getTotalCr()*100/linkClicks;
				}
				totalAdvertisingFees=dto.getTotalAdvertisingFees()*rate;
				totalRevenue=dto.getTotalRevenue()*rate;
				profit=dto.getProfit()*rate;
				totalAffiliateFees=dto.getTotalAdvertisingFees()*rate;
			}
			
			/*Float totalCost=0f;
			if(StringUtils.isNotBlank(nameAndQuantity)){
				if(nameAndQuantity.contains(",")){
					String[] name_quantitys=nameAndQuantity.split(",");
					for (String name_quantity: name_quantitys) {
						String name=name_quantity.substring(0, name_quantity.lastIndexOf("_"));
						Integer quantity=Integer.parseInt(name_quantity.substring(name_quantity.lastIndexOf("_")+1));
						if((name.contains("Inateck")||name.contains("Tomons"))&&priceMap.get(name+"_"+market)!=null){
							totalCost+=priceMap.get(name+"_"+market)*quantity;
						}
					}
				}else{
					String name=nameAndQuantity.substring(0, nameAndQuantity.lastIndexOf("_"));
					Integer quantity=Integer.parseInt(nameAndQuantity.substring(nameAndQuantity.lastIndexOf("_")+1));
					if((name.contains("Inateck")||name.contains("Tomons"))&&priceMap.get(name+"_"+market)!=null){
						totalCost+=priceMap.get(name+"_"+market)*quantity;
					}
				}
			}*/
			//Float profit=totalRevenue-totalCost;
			Float roi=(totalAdvertisingFees+profit*1.5f)/amountSpend;// (Total Advertising Fees+Profit*1.5)/Amount Spent
			Float relativeRoi=(totalAdvertisingFees+profit)/amountSpend;//(Total Advertising Fees+Profit)/Amount Spent
			Float forecastCpc=1f;
			//(Profit/该Ad ID的inateck或tomons产品Items Shipped总量*500/预估CPC*Total CR*1.5+该Ad ID的Total Affiliate Fees/Link Clicks*500/预估CPC)/500
			//Float forecastRoi=(profit/sameItemsShipped/forecastCpc*totalCr*1.5f+totalAffiliateFees/linkClicks*500f/forecastCpc)/500f;
			Float forecastRoi=1f;
			FacebookDto tempDto=new FacebookDto(start,product,audience,age,gender,market,placement,adName,amountSpend,linkClicks,ctr,cpc,cpm, allItemsShipped, totalCr,
					profit,totalAdvertisingFees,roi,impressions,sameItemsShipped,sameCr,totalRevenue, relativeRoi,productLine,adId, trackingId,relevanceScore,
					negativeFeedback,postComments,costPerPostEngagement, costPerPageLike,costPerPostShare,postEngagement,postShares,pageLikes, preView,forecastCpc,forecastRoi,totalAffiliateFees);
			tempDto.setAsinOnAd(asinOnAd);
			map.put(DateUtils.getDate((Date)start,"yyyy-MM-dd"),tempDto);
		}
		return map;
	}
	
	
	public List<FacebookDto> findFacebook(FacebookDto facebookDto){
		Map<String,Map<String, Float>> rateMap=amazonProduct2Service.getRate(facebookDto.getStarts(), facebookDto.getEnd());//date-name-rate
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		Map<String,FacebookDto> amazonMap=Maps.newHashMap();
		//ROUND(SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.items_shipped ELSE 0 END)/f.`link_clicks`,2) sameCr,ROUND(SUM(t.items_shipped)/f.`link_clicks`,2) totalCr,
		String amazonSql="select  r.date,r.ad_id,SUM(CASE WHEN p.product_name LIKE 'Inateck%' OR p.product_name LIKE 'Tomons%' THEN t.items_shipped ELSE 0 END) all_items_shipped, " +
				" SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.items_shipped ELSE 0 END) same_items_shipped,SUM(t.advertising_fees) total_advertising_fees, " + 
				" SUM(CASE WHEN p.product_name LIKE 'Inateck%' OR p.product_name LIKE 'Tomons%' THEN t.revenue ELSE 0 END) total_revenue,  " +
				" GROUP_CONCAT(CONCAT(CONCAT(p.`product_name`,CASE WHEN p.`color`!='' THEN '_' ELSE '' END,p.`color`),'_',t.items_shipped)) nameAndQuantity, " +
				" SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.advertising_fees ELSE 0 END) totalAffiliateFees, " +
				" SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.items_shipped ELSE 0 END),SUM(t.items_shipped),sum(t.profit) profit "+
				" from  amazoninfo_facebook_relationship r  JOIN amazoninfo_facebook_report t ON t.tracking_id=r.tracking_id AND t.market=r.market  "+
				" LEFT JOIN (select distinct country,asin,product_name,color from  psi_sku where del_flag='0') p ON p.country=t.market AND p.asin=t.asin "+
				" WHERE r.`del_flag`='0' and t.del_flag='0' and t.`date_shipped`>=:p1 and t.`date_shipped`<=:p2 ";;
				List<Object> amazonParam=Lists.newArrayList();
				amazonParam.add(facebookDto.getStarts());
				amazonParam.add(facebookDto.getEnd());
			    int m=3;
				if(StringUtils.isNotBlank(facebookDto.getCountry())){
					amazonSql+=" and  r.market=:p"+(m++);
					amazonParam.add(facebookDto.getCountry());
				}
				
				if(StringUtils.isNotBlank(facebookDto.getGender())){
					amazonSql+=" and r.`gender`=:p"+(m++);
					amazonParam.add(facebookDto.getGender());
				}
				
				if(StringUtils.isNotBlank(facebookDto.getPlacement())){
					amazonSql+=" and r.`placement`=:p"+(m++);
					amazonParam.add(facebookDto.getPlacement());
				}
				
				if(StringUtils.isNotBlank(facebookDto.getAge())){
					amazonSql+=" and r.`age`=:p"+(m++);
					amazonParam.add(facebookDto.getAge());
				}
				
				if(StringUtils.isNotBlank(facebookDto.getAsinOnAd())){
					amazonSql+=" and r.`asin_on_ad`=:p"+(m++);
					amazonParam.add(facebookDto.getAsinOnAd());
				}
				
				if(StringUtils.isNotBlank(facebookDto.getProduct())){
					amazonSql+=" and r.`product` like :p"+(m++);
					amazonParam.add("%"+facebookDto.getProduct()+"%");
				}
				
				if(StringUtils.isNotBlank(facebookDto.getProductLine())){
					amazonSql+=" and r.`product_line` like :p"+(m++);
					amazonParam.add("%"+facebookDto.getProductLine()+"%");
				}
				
				if(StringUtils.isNotBlank(facebookDto.getAudience())){
					amazonSql+=" and r.`audience` like :p"+(m++);
					amazonParam.add("%"+facebookDto.getAudience()+"%");
				}
				
			
				if(StringUtils.isNotBlank(facebookDto.getTrackingId())){
					amazonSql+=" and r.`tracking_id` like :p"+(m++);
					amazonParam.add("%"+facebookDto.getTrackingId()+"%");
				}
				
				amazonSql+=" GROUP BY r.date,r.ad_id ";
				List<Object[]> list2=facebookReportDao.findBySql(amazonSql, new Parameter(amazonParam.toArray(new Object[amazonParam.size()])));
				for (Object[] obj: list2) {
					Date start=(Date)obj[0];
					String adId=obj[1].toString();
					Integer allItemsShipped=Integer.parseInt(obj[2].toString());
					Integer sameItemsShipped=Integer.parseInt(obj[3].toString());
				//	float sameCr=Float.parseFloat(obj[29]==null?"0":obj[29].toString());
				//	float totalCr=Float.parseFloat(obj[30]==null?"0":obj[30].toString());
					float totalAdvertisingFees=Float.parseFloat(obj[4]==null?"0":obj[4].toString());
					float totalRevenue=Float.parseFloat(obj[5]==null?"0":obj[5].toString());
					String nameAndQuantity=(obj[6]==null?"":obj[6].toString());
					Float totalAffiliateFees=Float.parseFloat(obj[7].toString());
					float sameCr=Float.parseFloat(obj[8]==null?"0":obj[8].toString());
					float totalCr=Float.parseFloat(obj[2]==null?"0":obj[2].toString());
					float profit=Float.parseFloat(obj[10]==null?"0":obj[10].toString());
					FacebookDto dto=new FacebookDto();
					dto.setAdId(adId);
					dto.setStarts(start);
					dto.setTotalAdvertisingFees(totalAdvertisingFees);
					dto.setTotalRevenue(totalRevenue);
					dto.setProductName(nameAndQuantity);
					dto.setTotalAffiliateFees(totalAffiliateFees);
					dto.setSameCr(sameCr);
					dto.setTotalCr(totalCr);
					dto.setAllItemsShipped(allItemsShipped);
					dto.setSameItemsShipped(sameItemsShipped);
					dto.setProfit(profit);
					amazonMap.put(dateFormat.format(start)+adId, dto);
				}
				
				
		
		List<FacebookDto> facebookList=Lists.newArrayList();
		String sql="SELECT r.date,r.`product`,r.`audience`,r.`age`,r.`gender`,r.`market`,r.`placement`,f.`ad_name`,sum(f.`amount_spent`),sum(f.`link_clicks`), "+
			" round(sum(f.`link_clicks`)*100/sum(f.`impressions`),2) ctr,round(sum(f.`amount_spent`)/sum(f.`link_clicks`),2) cpc,round(sum(f.`amount_spent`)/sum(f.`impressions`)*1000,2) cpm, "+
		    " sum(f.impressions),r.product_line,r.tracking_id,r.ad_id,sum(f.relevance_score),GROUP_CONCAT(distinct f.negative_feedback),sum(f.post_comments), "+
		    " sum(f.`amount_spent`)/sum(f.post_engagement)  cost_per_post_engagement,sum(f.`amount_spent`)/sum(f.page_likes) cost_per_page_like,sum(f.`amount_spent`)/sum(f.post_shares) cost_per_post_share, "+
			" sum(f.post_shares),sum(f.page_likes),sum(f.post_engagement),r.pre_view,r.asin_on_ad	"+
			//"  SUM(CASE WHEN p.product_name LIKE 'Inateck%' OR p.product_name LIKE 'Tomons%' THEN t.items_shipped ELSE 0 END) all_items_shipped, "+
			//" SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.items_shipped ELSE 0 END) same_items_shipped,round(SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.items_shipped ELSE 0 END)/f.`link_clicks`,2) sameCr,round(SUM(t.items_shipped)/f.`link_clicks`,2) totalCr,SUM(t.advertising_fees) total_advertising_fees, "+
			//" SUM(CASE WHEN p.product_name LIKE 'Inateck%' OR p.product_name LIKE 'Tomons%' THEN t.revenue ELSE 0 END) total_revenue, "+
			//" GROUP_CONCAT(CONCAT(CONCAT(p.`product_name`,CASE WHEN p.`color`!='' THEN '_' ELSE '' END,p.`color`),'_',t.items_shipped)) nameAndQuantity, "+
			//" SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.advertising_fees ELSE 0 END) totalAffiliateFees "+
			" FROM amazoninfo_facebook_relationship r "+
			" JOIN facebook_report f ON f.`ad_id`=r.`ad_id`  "+ 
			//" JOIN amazoninfo_facebook_report t ON t.tracking_id=r.tracking_id AND t.market=r.market "+
			//" LEFT JOIN psi_sku p ON p.country=t.market AND p.asin=t.asin AND p.del_flag='0' AND p.use_barcode='1' "+
			" WHERE f.`del_flag`='0' AND r.`del_flag`='0'  and f.`start`>=:p1 and f.`start`<=:p2 ";
		    List<Object> paramList=Lists.newArrayList();
		    paramList.add(facebookDto.getStarts());
		    paramList.add(facebookDto.getEnd());
		    int i=3;
			if(StringUtils.isNotBlank(facebookDto.getCountry())){
				sql+=" and  r.market=:p"+(i++);
				paramList.add(facebookDto.getCountry());
			}
			
			if(StringUtils.isNotBlank(facebookDto.getGender())){
				sql+=" and r.`gender`=:p"+(i++);
				paramList.add(facebookDto.getGender());
			}
			
			if(StringUtils.isNotBlank(facebookDto.getPlacement())){
				sql+=" and r.`placement`=:p"+(i++);
				paramList.add(facebookDto.getPlacement());
			}
			
			if(StringUtils.isNotBlank(facebookDto.getAge())){
				sql+=" and r.`age`=:p"+(i++);
				paramList.add(facebookDto.getAge());
			}
			
			if(StringUtils.isNotBlank(facebookDto.getAsinOnAd())){
				sql+=" and r.`asin_on_ad`=:p"+(i++);
				paramList.add(facebookDto.getAsinOnAd());
			}
			
			if(StringUtils.isNotBlank(facebookDto.getProduct())){
				sql+=" and r.`product` like :p"+(i++);
				paramList.add("%"+facebookDto.getProduct()+"%");
			}
			
			if(StringUtils.isNotBlank(facebookDto.getProductLine())){
				sql+=" and r.`product_line` like :p"+(i++);
				paramList.add("%"+facebookDto.getProductLine()+"%");
			}
			
			if(StringUtils.isNotBlank(facebookDto.getAudience())){
				sql+=" and r.`audience` like :p"+(i++);
				paramList.add("%"+facebookDto.getAudience()+"%");
			}
			
			
			if(StringUtils.isNotBlank(facebookDto.getAdName())){
				sql+=" and f.`ad_name` like :p"+(i++);
				paramList.add("%"+facebookDto.getAdName()+"%");
			}
			
			
			if(StringUtils.isNotBlank(facebookDto.getTrackingId())){
				sql+=" and r.`tracking_id` like :p"+(i++);
				paramList.add("%"+facebookDto.getTrackingId()+"%");
			}
			
			sql+=" GROUP BY r.date,r.`product`,r.`audience`,r.`age`,r.`gender`,r.`market`,r.`placement`,f.`ad_name`,r.pre_view,r.product_line,r.tracking_id,r.ad_id,r.asin_on_ad ";
			
		List<Object[]> list=facebookReportDao.findBySql(sql, new Parameter(paramList.toArray(new Object[paramList.size()])));
		for (Object[] obj: list) {
			Date start=(Date)obj[0];
			String product=obj[1].toString();
			String audience=obj[2].toString();
			String age=obj[3].toString();
			String gender=obj[4].toString();
			String market=obj[5].toString();
			float rate=1f;
			if(rateMap.get(DateUtils.getDate((Date)start,"yyyy-MM-dd"))!=null){
				if("de,fr,it,es".contains(market)){
			       rate=rateMap.get(DateUtils.getDate((Date)start,"yyyy-MM-dd")).get("EUR/USD");
				}else if("uk".equals(market)){
					rate=rateMap.get(DateUtils.getDate((Date)start,"yyyy-MM-dd")).get("GBP/USD");	
				}else if("jp".equals(market)){
					rate=rateMap.get(DateUtils.getDate((Date)start,"yyyy-MM-dd")).get("JPY/USD");
				}
			}else{
				if("de,fr,it,es".contains(market)){
				    rate=MathUtils.getRate("EUR","USD",null);
				}else if("uk".equals(market)){
					rate=MathUtils.getRate("GBP","USD",null);
				}else if("jp".equals(market)){
					rate=MathUtils.getRate("JPY","USD",null);
				}
			}
			
			String placement=obj[6].toString();
			String adName=obj[7].toString();
			float amountSpend=((BigDecimal)obj[8]).floatValue();
			Integer linkClicks=Integer.parseInt(obj[9].toString());
			float ctr=Float.parseFloat(obj[10]==null?"0":obj[10].toString());
			float cpc=Float.parseFloat(obj[11]==null?"0":obj[11].toString());
			float cpm=Float.parseFloat(obj[12]==null?"0":obj[12].toString());
			Float impressions=Float.parseFloat(obj[13]==null?"0":obj[13].toString());
			String productLine=obj[14].toString(); 
			String trackingId=obj[15].toString();
			String adId=obj[16].toString();
			float relevanceScore=Float.parseFloat(obj[17]==null?"0":obj[17].toString());
			String negativeFeedback=obj[18].toString();
			float postComments=Float.parseFloat(obj[19].toString());
			float costPerPostEngagement=Float.parseFloat(obj[20]==null?"0":obj[20].toString())*rate;
			float costPerPageLike=Float.parseFloat(obj[21]==null?"0":obj[21].toString())*rate;
			float costPerPostShare=Float.parseFloat(obj[22]==null?"0":obj[22].toString())*rate;
			float postEngagement=Float.parseFloat(obj[23]==null?"0":obj[23].toString());
			float postShares=Float.parseFloat(obj[24].toString());
			float pageLikes=Float.parseFloat(obj[25].toString());
			String preView=obj[26].toString();
			String asinOnAd=obj[27].toString();
			
			Integer allItemsShipped=0;
			Integer sameItemsShipped=0;
			float sameCr=0f;
			float totalCr=0f;
			float totalAdvertisingFees=0f;
			float totalRevenue=0f;
			Float totalAffiliateFees=0f;
			Float profit=0f;
			if(amazonMap!=null&&amazonMap.get(dateFormat.format(start)+adId)!=null){
				FacebookDto dto=amazonMap.get(dateFormat.format(start)+adId);
				allItemsShipped=dto.getAllItemsShipped();
				sameItemsShipped=dto.getSameItemsShipped();
				//ROUND(SUM(CASE WHEN r.asin_on_ad=t.asin THEN t.items_shipped ELSE 0 END)/f.`link_clicks`,2) sameCr,ROUND(SUM(t.items_shipped)/f.`link_clicks`,2) totalCr,
				if(linkClicks>0){
					sameCr=dto.getSameCr()*100/linkClicks;
					totalCr=dto.getTotalCr()*100/linkClicks;
				}
				totalAdvertisingFees=dto.getTotalAdvertisingFees()*rate;
				totalRevenue=dto.getTotalRevenue()*rate;
				totalAffiliateFees=dto.getTotalAdvertisingFees()*rate;
				profit=dto.getProfit()*rate;
			}
			
			Float roi=(totalAdvertisingFees+profit*1.5f)/amountSpend;// (Total Advertising Fees+Profit*1.5)/Amount Spent
			Float relativeRoi=(totalAdvertisingFees+profit)/amountSpend;//(Total Advertising Fees+Profit)/Amount Spent
			Float forecastCpc=1f;
			//(Profit/该Ad ID的inateck或tomons产品Items Shipped总量*500/预估CPC*Total CR*1.5+该Ad ID的Total Affiliate Fees/Link Clicks*500/预估CPC)/500
			//Float forecastRoi=(profit/sameItemsShipped/forecastCpc*totalCr*1.5f+totalAffiliateFees/linkClicks*500f/forecastCpc)/500f;
			Float forecastRoi=1f;
			
			FacebookDto tempDto=new FacebookDto(start,product,audience,age,gender,market,placement,adName,amountSpend,linkClicks,ctr,cpc,cpm, allItemsShipped, totalCr,
					profit,totalAdvertisingFees,roi,impressions,sameItemsShipped,sameCr,totalRevenue, relativeRoi,productLine,adId, trackingId,relevanceScore,
					negativeFeedback,postComments,costPerPostEngagement, costPerPageLike,costPerPostShare,postEngagement,postShares,pageLikes, preView,forecastCpc,forecastRoi,totalAffiliateFees);
			tempDto.setAsinOnAd(asinOnAd);
			facebookList.add(tempDto);
		}
		return facebookList;
	}
	
	//统计利润
	@Transactional(readOnly = false)
	public void updateProfit() {
		//[国家[asin price]]
		Map<String, Map<String, Float>> priceMap = advertisingService.getProductPrice();
		//asin对应的产品名
		Map<String,String> asinNameMap = saleProfitService.getProductNameByAsin();
		//佣金比例[产品名称_国家   佣金比]
		Map<String, Integer> commissionMap = productPriceService.findCommission();
		DetachedCriteria dc = amazonFacebookReportDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag","0"));
		dc.add(Restrictions.isNull("profit"));
		List<AmazonFacebookReport> list = amazonFacebookReportDao.find(dc);
		for (AmazonFacebookReport report : list) {
			String country = report.getMarket();
			String asin = report.getAsin();
			//返利
			Double adFee = report.getAdvertisingFees();
			//销售额
			Double sales = report.getRevenue();
			//销量
			long salesVolume = report.getItemsShipped();
			try {
				//保本价
				Float price = priceMap.get(country).get(asin);
				float vat = 0f;	//增值税
				String temp = country.toUpperCase();
				if("UK".equals(temp)){
					temp = "GB";
				}
				if("COM".equals(temp)){
					temp = "US";
				}
				CountryCode vatCode = CountryCode.valueOf(temp);
				if (vatCode != null) {
					vat = vatCode.getVat()/100f;
				}
				int commission = 0;	//佣金比
				try {
					commission = commissionMap.get(asinNameMap.get(asin) + "_" + country);
				} catch (NullPointerException e) {}
				//利润为销售利润加返点金额
				float cbPrice = price*salesVolume;
				double lirun = (sales-cbPrice)/(1+vat)-(sales-cbPrice)*commission/100d;
				report.setProfit(lirun + adFee);
			} catch (NullPointerException e) {
				//保本价不存在为非inateck产品,利润为返点金额
				report.setProfit(adFee);
			}
		}
		saveAmazonFacebookReport(list);
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AmazonAndFacebookService  service= applicationContext.getBean(AmazonAndFacebookService.class);
		SaleProfitService  saleProfitService= applicationContext.getBean(SaleProfitService.class);
		ProductPriceService productPriceService = applicationContext.getBean(ProductPriceService.class);
		AdvertisingService advertisingService = applicationContext.getBean(AdvertisingService.class);
		service.setAdvertisingService(advertisingService);
		service.setProductPriceService(productPriceService);
		service.setSaleProfitService(saleProfitService);
		service.updateProfit();
		applicationContext.close();
	}

	public AdvertisingService getAdvertisingService() {
		return advertisingService;
	}

	public void setAdvertisingService(AdvertisingService advertisingService) {
		this.advertisingService = advertisingService;
	}

	public ProductPriceService getProductPriceService() {
		return productPriceService;
	}

	public void setProductPriceService(ProductPriceService productPriceService) {
		this.productPriceService = productPriceService;
	}

	public SaleProfitService getSaleProfitService() {
		return saleProfitService;
	}

	public void setSaleProfitService(SaleProfitService saleProfitService) {
		this.saleProfitService = saleProfitService;
	}
	
}
