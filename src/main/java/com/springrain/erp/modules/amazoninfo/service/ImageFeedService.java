/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.ImageFeedDao;
import com.springrain.erp.modules.amazoninfo.entity.ImageFeed;

/**
 * 图片修改Service
 * @author tim
 * @version 2014-11-11
 */
@Component
@Transactional(readOnly = true)
public class ImageFeedService extends BaseService {

	@Autowired
	private ImageFeedDao imageFeedDao;
	
	public ImageFeed get(Integer id) {
		return imageFeedDao.get(id);
	}
	
	public Page<ImageFeed> find(Page<ImageFeed> page, ImageFeed imageFeed) {
		DetachedCriteria dc = imageFeedDao.createDetachedCriteria();
		Date date =  imageFeed.getEndDate();
		date.setHours(23);
		date.setMinutes(59);
		dc.add(Restrictions.and(Restrictions.ge("requestDate",imageFeed.getRequestDate()),Restrictions.le("requestDate",date)));
		
		String country = imageFeed.getCountry();
		if (StringUtils.isNotEmpty(country)){
			dc.add(Restrictions.eq("country", country));
		}
		if (imageFeed.getCreateBy()!=null && StringUtils.isNotEmpty(imageFeed.getCreateBy().getId())){
			dc.add(Restrictions.eq("createBy", imageFeed.getCreateBy()));
		}
		if(StringUtils.isNotEmpty(imageFeed.getSku())){
			dc.add(Restrictions.like("sku", "%"+imageFeed.getSku()+"%"));
		}
		return imageFeedDao.find(page, dc);
	}
	
	public List<ImageFeed> findUnfinished(){
		DetachedCriteria dc = imageFeedDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.eq("state", "1"),Restrictions.eq("state", "2")));
		List<ImageFeed> rs =  imageFeedDao.find(dc);
		for (ImageFeed imageFeed : rs) {
			Hibernate.initialize(imageFeed.getImages());
		}
		return rs;
	}
	
	
	@Transactional(readOnly = false)
	public void save(ImageFeed imageFeed) {
		imageFeedDao.save(imageFeed);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		imageFeedDao.deleteById(id);
	}
	
}
