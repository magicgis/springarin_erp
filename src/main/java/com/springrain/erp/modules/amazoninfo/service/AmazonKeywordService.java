/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonKeywordSearchDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonKeywordSearch;

@Component
@Transactional(readOnly = true)
public class AmazonKeywordService extends BaseService {

	@Autowired
	private AmazonKeywordSearchDao amazonKeywordSearchDao;

	public Page<AmazonKeywordSearch> find(Page<AmazonKeywordSearch> page, AmazonKeywordSearch amazonKeywordSearch) {
		DetachedCriteria dc = amazonKeywordSearchDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(amazonKeywordSearch.getKeyword())){
			dc.add(Restrictions.like("keyword","%"+amazonKeywordSearch.getKeyword()+"%"));
		}
		if(StringUtils.isNotEmpty(amazonKeywordSearch.getCountry())){
			dc.add(Restrictions.eq("country",amazonKeywordSearch.getCountry()));
		}
		if(StringUtils.isNotEmpty(amazonKeywordSearch.getState())){
			dc.add(Restrictions.eq("state",amazonKeywordSearch.getState()));
		}
		dc.add(Restrictions.ge("createDate",amazonKeywordSearch.getCreateDate()));
		dc.add(Restrictions.lt("createDate",DateUtils.addDays(amazonKeywordSearch.getUpdateDate(),1)));
		dc.addOrder(Order.desc("createDate"));
		return amazonKeywordSearchDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void saveSearch(AmazonKeywordSearch search){
		amazonKeywordSearchDao.save(search);
	}
	
	public AmazonKeywordSearch get(Integer id) {
		return amazonKeywordSearchDao.get(id);
	}
	
	public List<AmazonKeywordSearch> findKeywordByCountry(){
		DetachedCriteria dc = amazonKeywordSearchDao.createDetachedCriteria();
		dc.add(Restrictions.eq("state","0"));
		List<AmazonKeywordSearch> list=amazonKeywordSearchDao.find(dc);
		for(AmazonKeywordSearch keywordSearch:list){
			Hibernate.initialize(keywordSearch.getItems());
		}
		return list;
	}
	
	@Transactional(readOnly = false)
	public void saveSearchList(List<AmazonKeywordSearch> amazonKeywordSearchList) {
		for (AmazonKeywordSearch search: amazonKeywordSearchList) {
			if(search.getId()!=null&&search.getId()>0){
				amazonKeywordSearchDao.getSession().merge(search);
			}else{
				amazonKeywordSearchDao.save(search);
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void cancelKey(){
		Date date=new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		String sql="select id from amazoninfo_keyword_search where update_date<:p1 ";
		List<Integer> idList=amazonKeywordSearchDao.findBySql(sql,new Parameter(DateUtils.addDays(date,-7)));
		if(idList!=null&&idList.size()>0){
			String updateSql="update amazoninfo_keyword_search set state='1' where id in :p1";
			amazonKeywordSearchDao.updateBySql(updateSql, new Parameter(idList));
		}
	}
}
