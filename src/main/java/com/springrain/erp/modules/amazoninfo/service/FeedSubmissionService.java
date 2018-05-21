/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.FeedSubmissionDao;
import com.springrain.erp.modules.amazoninfo.entity.FeedSubmission;

/**
 * 亚马逊帖子上架Service
 * @author tim
 * @version 2014-08-06
 */
@Component
@Transactional(readOnly = true)
public class FeedSubmissionService extends BaseService {

	@Autowired
	private FeedSubmissionDao feedSubmissionDao;
	
	public FeedSubmission get(Integer id) {
		return feedSubmissionDao.get(id);
	}
	
	public Page<FeedSubmission> find(Page<FeedSubmission> page, FeedSubmission feedSubmission) {
		DetachedCriteria dc = feedSubmissionDao.createDetachedCriteria();
		Date date =  feedSubmission.getEndDate();
		date.setHours(23);
		date.setMinutes(59);
		dc.add(Restrictions.and(Restrictions.ge("createDate",feedSubmission.getCreateDate()),Restrictions.le("createDate",date)));
		
		String country = feedSubmission.getCountry();
		if (StringUtils.isNotEmpty(country)){
			dc.add(Restrictions.eq("country", country));
		}
		if (feedSubmission.getCreateBy()!=null && StringUtils.isNotEmpty(feedSubmission.getCreateBy().getId())){
			dc.add(Restrictions.eq("createBy", feedSubmission.getCreateBy()));
		}
		if(StringUtils.isNotEmpty(feedSubmission.getResult())){
			dc.createAlias("this.feeds", "feed");
			dc.add(Restrictions.like("feed.sku", "%"+feedSubmission.getResult()+"%"));
		}
		
		dc.add(Restrictions.ne("delFlag", "1"));
		
		return feedSubmissionDao.find(page, dc);
	}
	
	public List<FeedSubmission> findUnfinished(){
		DetachedCriteria dc = feedSubmissionDao.createDetachedCriteria();
		dc.add(Restrictions.ne("delFlag", "1"));
		dc.add(Restrictions.or(Restrictions.eq("state", "1"),Restrictions.eq("state", "2")));
		return feedSubmissionDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void save(FeedSubmission feedSubmission) {
		feedSubmissionDao.save(feedSubmission);
	}
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		feedSubmissionDao.deleteById(id);
	}
}
