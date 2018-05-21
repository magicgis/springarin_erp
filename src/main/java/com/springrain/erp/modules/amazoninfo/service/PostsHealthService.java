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

import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.PostsHealthDao;
import com.springrain.erp.modules.amazoninfo.entity.PostsHealth;

/**
 * 帖子健康列表Service
 * @author Tim
 * @version 2015-07-08
 */
@Component
@Transactional(readOnly = true)
public class PostsHealthService extends BaseService {

	@Autowired
	private PostsHealthDao postsHealthDao;
	
	public List<PostsHealth> find(PostsHealth postsHealth) {
		DetachedCriteria dc = postsHealthDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(postsHealth.getSku())){
			dc.add(Restrictions.like("sku", "%"+postsHealth.getSku()+"%"));
		}
		if(postsHealth.getDate()==null){
			postsHealth.setDate(DateUtils.getDateStart(new Date()));
		}
		dc.add(Restrictions.eq("date", postsHealth.getDate()));
		dc.add(Restrictions.eq("country", postsHealth.getCountry()));
		return postsHealthDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void save(List<PostsHealth> postsHealths) {
		postsHealthDao.save(postsHealths);
	}
	
}
