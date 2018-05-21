/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.PriceFeedDao;
import com.springrain.erp.modules.amazoninfo.entity.Price;
import com.springrain.erp.modules.amazoninfo.entity.PriceFeed;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 亚马逊帖子上架Service
 * @author tim
 * @version 2014-08-06
 */
@Component
@Transactional(readOnly = true)
public class PriceFeedService extends BaseService {

	@Autowired
	private PriceFeedDao priceFeedDao;
	
	public PriceFeed get(Integer id) {
		return priceFeedDao.get(id);
	}
	
	public Page<PriceFeed> find(Page<PriceFeed> page, PriceFeed priceFeed) {
		DetachedCriteria dc = priceFeedDao.createDetachedCriteria();
		Date date =  priceFeed.getEndDate();
		date.setHours(23);
		date.setMinutes(59);
		dc.add(Restrictions.and(Restrictions.ge("requestDate",priceFeed.getRequestDate()),Restrictions.le("requestDate",date)));
		
		String country = priceFeed.getCountry();
		if (StringUtils.isNotEmpty(country)){
			dc.add(Restrictions.eq("country", country));
		}
		if (StringUtils.isNotEmpty(priceFeed.getReason())){
			dc.add(Restrictions.eq("reason", priceFeed.getReason()));
		}
		if (priceFeed.getCreateBy()!=null && StringUtils.isNotEmpty(priceFeed.getCreateBy().getId())){
			dc.add(Restrictions.or(Restrictions.eq("createBy", priceFeed.getCreateBy()),Restrictions.eq("createBy",UserUtils.getUserById("1"))));
		}
		if(StringUtils.isNotEmpty(priceFeed.getResult())){
			dc.createAlias("this.prices", "price");
			dc.add(Restrictions.like("price.sku", "%"+priceFeed.getResult()+"%"));
		}
		return priceFeedDao.find(page, dc);
	}
	
	public List<PriceFeed> findUnfinished(){
		DetachedCriteria dc = priceFeedDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.eq("state", "1"),Restrictions.eq("state", "2")));
		List<PriceFeed> rs =  priceFeedDao.find(dc);
		for (PriceFeed priceFeed : rs) {
			Hibernate.initialize(priceFeed.getPrices());
		}
		return rs;
	}
	
	
	@Transactional(readOnly = false)
	public void save(PriceFeed priceFeed) {
		priceFeedDao.save(priceFeed);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		priceFeedDao.deleteById(id);
	}
	
	//查询最近(一小时内)系统提交的预警价格修改结果[country_sku result]
	public Map<String, String> findSysSubmited(String reason){
		Map<String, String> rs = Maps.newHashMap();
		DetachedCriteria dc = priceFeedDao.createDetachedCriteria();
		dc.add(Restrictions.ge("requestDate", DateUtils.addHours(new Date(), -1)));
		dc.add(Restrictions.eq("reason", reason));
		List<PriceFeed> list =  priceFeedDao.find(dc);
		for (PriceFeed priceFeed : list) {
			String country = priceFeed.getCountry();
			List<Price> prices = priceFeed.getPrices();
			for (Price price : prices) {
				String key = country + "_" + price.getSku();
				rs.put(key, priceFeed.getStateStr());
			}
		}
		return rs;
	}
}
