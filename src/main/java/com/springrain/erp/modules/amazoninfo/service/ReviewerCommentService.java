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
import com.springrain.erp.modules.amazoninfo.dao.ReviewerCommentDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonReviewer;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerComment;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 联系记录Service
 */
@Component
@Transactional(readOnly = true)
public class ReviewerCommentService extends BaseService {

	@Autowired
	private ReviewerCommentDao reviewerCommentDao;
	
	public ReviewerComment get(Integer id) {
		return reviewerCommentDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(ReviewerComment reviewerComment) {
		reviewerCommentDao.save(reviewerComment);
	}
	
	@Transactional(readOnly = false)
	public void save(List<ReviewerComment> comments) {
		reviewerCommentDao.save(comments);
	}
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		reviewerCommentDao.deleteById(id);
	}
	
	public long findNewSysComment(AmazonReviewer amazonReviewer) {
		DetachedCriteria dc = reviewerCommentDao.createDetachedCriteria(
				Restrictions.eq("amazonReviewer", amazonReviewer),Restrictions.eq("createBy",UserUtils.getUserById("1")));
		Date today = new Date();
		if(today.getDay()==1){
			dc.add(Restrictions.gt("createDate", DateUtils.addHours(today, -72)));
		}else{
			dc.add(Restrictions.gt("createDate", DateUtils.addHours(today, -24)));
		}
		return reviewerCommentDao.count(dc);
	}
}
