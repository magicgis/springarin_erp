package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.OutsidePromotionDao;
import com.springrain.erp.modules.amazoninfo.dao.OutsidePromotionWebsiteDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPromotionsWarning;
import com.springrain.erp.modules.amazoninfo.entity.OutsidePromotion;
import com.springrain.erp.modules.amazoninfo.entity.OutsidePromotionWebsite;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 *	进销存供应商Service
 */
@Component
@Transactional(readOnly = true)
public class OutsidePromotionService extends BaseService {
	
	@Autowired
	private OutsidePromotionDao outsidePromotionDao;
	@Autowired
	private OutsidePromotionWebsiteDao  websiteDao;
	
	public Page<OutsidePromotion> find(Page<OutsidePromotion> page, OutsidePromotion outside,String isCheck) {
		DetachedCriteria dc = outsidePromotionDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(outside.getPromotionCode())){
			dc.add(Restrictions.eq("promotionCode", outside.getPromotionCode()));
		}
		if(StringUtils.isNotEmpty(outside.getAsin())){
			dc.add(Restrictions.eq("asin", outside.getAsin()));
		}
		if(StringUtils.isNotEmpty(outside.getProductName())){
			dc.add(Restrictions.like("productName", "%"+outside.getProductName()+"%"));
		}
		if("1".equals(isCheck)){
			dc.add(Restrictions.eq("createUser.id",UserUtils.getUser().getId()));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("id"));
		return outsidePromotionDao.find(page, dc);
	}
	
	public List<OutsidePromotion> findAll() {
		DetachedCriteria dc = outsidePromotionDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		return outsidePromotionDao.find(dc);
	}
	
	public List<OutsidePromotion> findByProId(Integer promoId) {
		DetachedCriteria dc = outsidePromotionDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("promoWarning.id", promoId));
		return outsidePromotionDao.find(dc);
	}
	
	@Transactional(readOnly=false)
	public void refresh(Integer promoId){//更新开始结束时间
		List<OutsidePromotion> promos = this.findByProId(promoId);
		for(OutsidePromotion promo:promos){
			AmazonPromotionsWarning warning= promo.getPromoWarning();
			promo.setStartDate(warning.getStartDate());
			promo.setEndDate(warning.getEndDate());
			this.save(promo);
		}
	}
	
	/**
	 *获取所有站外信息 
	 */
	public List<OutsidePromotionWebsite> findWebsite(String trackId) {
		DetachedCriteria dc = websiteDao.createDetachedCriteria();
		dc.add(Restrictions.eq("trackId", trackId));
		dc.add(Restrictions.eq("delFlag", "0"));
		return websiteDao.find(dc);
	}
	
	
	public OutsidePromotion get(Integer id){
		return outsidePromotionDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void addSave(OutsidePromotion pagePromo){
		String productStr=pagePromo.getProductName();
		List<OutsidePromotion> outsides = Lists.newArrayList();
		String [] productArr=productStr.split(",");
		String [] asinArr=pagePromo.getAsin().split(",");
		List<OutsidePromotionWebsite> websites = pagePromo.getPromoWebsites();//站点跟trackId相对应
		if(websites!=null&&websites.size()>0){
			for(OutsidePromotionWebsite site:websites){
				site.setTrackId(pagePromo.getTrackId());
				site.setDelFlag("0");
				websiteDao.save(site);
			}
		}
		for(int i =0;i<productArr.length;i++){
			OutsidePromotion promo = new OutsidePromotion();
			promo.setProductName(productArr[i]);
			promo.setAsin(asinArr[i]);
			promo.setCountry(pagePromo.getCountry());
			promo.setStartDate(pagePromo.getStartDate());
			promo.setEndDate(pagePromo.getEndDate());
			promo.setTrackId(pagePromo.getTrackId());
			promo.setPromotionCode(pagePromo.getPromotionCode());
			promo.setBuyerGets(pagePromo.getBuyerGets());
			promo.setDelFlag("0"); //逻辑删除标志，0代表正常
			promo.setCreateUser(UserUtils.getUser());
			promo.setCreateDate(new Date());
			promo.setSampleProvided(pagePromo.getSampleProvided());
			promo.setPlatformFunds(pagePromo.getPlatformFunds());
			promo.setPromoWarning(pagePromo.getPromoWarning());
			outsides.add(promo);
		}
		outsidePromotionDao.save(outsides);
	}
	

	
	@Transactional(readOnly = false)
	public void editSave(OutsidePromotion pagePromo){
		List<OutsidePromotionWebsite>  curWedsites = pagePromo.getPromoWebsites();
		Set<Integer> oldSetIds = Sets.newHashSet();
		List<OutsidePromotionWebsite> oldWebSites=this.findWebsite(pagePromo.getTrackId());
		Set<Integer> curPromoIds =Sets.newHashSet();
		
		for(OutsidePromotionWebsite site:oldWebSites){
			oldSetIds.add(site.getId());
		}
		
		for(OutsidePromotionWebsite site:curWedsites){
			if(site.getId()==null){
				site.setDelFlag("0");  //新增
				site.setTrackId(pagePromo.getTrackId());
			}else{
				curPromoIds.add(site.getId());
			}
		}
		
		if((curPromoIds==null||curPromoIds.size()==0)){
			for(OutsidePromotionWebsite site:oldWebSites){
				site.setDelFlag("1");
				curWedsites.add(site);
			}
		}else{
			for(OutsidePromotionWebsite site:oldWebSites){
				if(!curPromoIds.contains(site.getId())){
					site.setDelFlag("1");
					curWedsites.add(site);
				}
			}
		}
		
		for(OutsidePromotionWebsite webSite:curWedsites){
			websiteDao.getSession().merge(webSite);
		}
		
	}
	

	
	@Transactional(readOnly = false)
	public void save(OutsidePromotion outsidePromotion){
		outsidePromotionDao.save(outsidePromotion);
	}
	
	




	
/**
 *获取一段时间内的销量,,销售额
 */
public Map<Date,Map<String,String>> getSalesByAsinCountry(Set<String> productNames,String country,Date startDate,Date endDate){
	String sql = "SELECT a.`date`,SUM(a.`sales_volume`),SUM(a.`sales`),(CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END) AS proName" +
			" FROM amazoninfo_sale_report AS a WHERE  (CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END) " +
			" in :p1 AND a.`country`=:p2 AND  a.`date` between :p3 and :p4 GROUP BY proName ,a.`date` ";
	List<Object[]> list = outsidePromotionDao.findBySql(sql,new Parameter(productNames,country,startDate,endDate));
	Map<Date,Map<String,String>> rsMap = Maps.newHashMap();
	for(Object[] obj:list){
		Date dateDate=(Date)obj[0];
		Integer quantity = Integer.parseInt(obj[1].toString());
		Map<String,String> inMap =null;
		if(rsMap.get(dateDate)==null){
			inMap = Maps.newHashMap();
		}else{
			inMap =rsMap.get(dateDate);
		}
		
		inMap.put(obj[3].toString(), quantity+",,"+obj[2].toString());
		rsMap.put(dateDate, inMap);
	}
	return rsMap;
}


/**
 *获取一段时间内的促销销量,,销售额,,总优惠
 */
public Map<Date,Map<String,String>> getSalesByAsinCountryPro(Set<String> asins,String country,Date startDate,Date endDate,String trackId){
	String sql = "SELECT  a.`purchase_date`,SUM(a.`quantity`),SUM(IFNULL(a.`sales`,0)),SUM(a.`discount`),a.asin FROM amazoninfo_promotions_report AS a "+
			" WHERE  a.`asin`in :p1 AND a.`country`=:p2 AND a.`promotion_ids` LIKE :p5 AND a.`purchase_date` between :p3 and :p4  AND a.`discount`>0 GROUP BY a.`country`,a.`purchase_date`,a.`asin`  ";
	List<Object[]> list = outsidePromotionDao.findBySql(sql,new Parameter(asins,country,startDate,endDate,"%"+trackId+"%"));
	 Map<Date,Map<String,String>> rsMap = Maps.newHashMap();
	for(Object[] obj:list){
		Date dateDate=(Date)obj[0];
		Integer quantity = Integer.parseInt(obj[1].toString());
		String asin = obj[4].toString();
		Map<String,String> inMap = null;
		if(rsMap.get(dateDate)==null){
			inMap = Maps.newHashMap();
		}else{
			inMap=rsMap.get(dateDate);
		}
		inMap.put(asin, quantity+",,"+obj[2].toString()+",,"+obj[3].toString());
		rsMap.put(dateDate, inMap);
	}
	return rsMap;
}


	/**
	 *获取一段时间内的fba库存值 
	 */
	public Map<Date,Map<String,String>> getSessionByAsinCountry(String country,Set<String> asins,Date startDate,Date endDate){
		String sql = "SELECT a.`data_date`,a.`sessions`,a.`orders_placed`,a.`child_asin` FROM amazoninfo_business_report AS a WHERE  " +
				" a.child_asin in :p1 AND a.`country`=:p2 AND a.`data_date` between :p3 and :p4 ";
		List<Object[]> list = outsidePromotionDao.findBySql(sql,new Parameter(asins,country,startDate,endDate));
		Map<Date,Map<String,String>> rsMap = Maps.newHashMap();
		for(Object[] obj:list){
			Date dateDate=(Date)obj[0];
			Integer quantity = Integer.parseInt(obj[1].toString());
			Integer order = Integer.parseInt(obj[2].toString());
			String asin = (obj[3].toString());
			Map<String,String> inMap = null;
			if(rsMap.get(dateDate)==null){
				inMap = Maps.newHashMap();
			}else{
				inMap = rsMap.get(dateDate);
			}
			inMap.put(asin, quantity+","+order);
			rsMap.put(dateDate,inMap);
		}
		return rsMap;
	}
	
	
	
	/***
	 *获取该trackId的所有产品 
	 */
	public List<Object[]> getProductInfoByTrackId(String trackId){
		String sql = "SELECT a.`asin`,a.`product_name` FROM amazoninfo_outside_promotion AS a WHERE a.`track_id`=:p1 AND a.`del_flag`='0' ";
		return  outsidePromotionDao.findBySql(sql,new Parameter(trackId));
	}
	
	
	
}
