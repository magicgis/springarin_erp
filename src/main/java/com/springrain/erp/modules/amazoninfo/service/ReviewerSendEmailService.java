package com.springrain.erp.modules.amazoninfo.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.ReviewerSendEmailDao;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerSendEmail;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 发送邮件Service
 */
@Component
@Transactional(readOnly = true)
public class ReviewerSendEmailService extends BaseService {

	@Autowired
	private ReviewerSendEmailDao reviewerSendEmailDao;
	
	public ReviewerSendEmail get(Integer id) {
		return reviewerSendEmailDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(ReviewerSendEmail reviewerSendEmail) {
		//过滤Emoji等特殊符号
		reviewerSendEmail.setSendContent(Encodes.filterOffUtf8Mb4(reviewerSendEmail.getSendContent()));
		reviewerSendEmailDao.save(reviewerSendEmail);
	}
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		reviewerSendEmailDao.deleteById(id);
	}
	
	public Page<ReviewerSendEmail> find(Page<ReviewerSendEmail> page, ReviewerSendEmail sendEmail) {
		DetachedCriteria dc = reviewerSendEmailDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(sendEmail.getSendSubject())){
			dc.add(Restrictions.or(Restrictions.like("sendSubject", "%"+sendEmail.getSendSubject()+"%")
					,Restrictions.like("sendEmail", "%"+sendEmail.getSendSubject()+"%")));
		}
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			dc.add(Restrictions.eq("createBy",user));
		}	
		dc.add(Restrictions.eq(SendEmail.FIELD_DEL_FLAG, SendEmail.DEL_FLAG_NORMAL));
		if(StringUtils.isNotEmpty(sendEmail.getSendFlag())){
			dc.add(Restrictions.eq("sendFlag",sendEmail.getSendFlag()));
		}
		return reviewerSendEmailDao.find(page, dc);
	}
	
	public ReviewerSendEmail findBlankEmail(String email){
		DetachedCriteria dc = reviewerSendEmailDao.createDetachedCriteria();
		User user = UserUtils.getUser();
		dc.add(Restrictions.eq("createBy",user));
		if(email==null){
			dc.add(Restrictions.isNull("sendEmail"));
		} else {
			dc.add(Restrictions.eq("sendEmail",email));
		}
		dc.add(Restrictions.eq(SendEmail.FIELD_DEL_FLAG, SendEmail.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("type","0"));
		dc.add(Restrictions.eq("sendFlag","0"));
		List<ReviewerSendEmail> list = reviewerSendEmailDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
}
