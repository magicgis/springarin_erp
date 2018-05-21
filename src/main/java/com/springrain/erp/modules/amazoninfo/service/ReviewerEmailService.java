package com.springrain.erp.modules.amazoninfo.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.ReviewerEmailDao;
import com.springrain.erp.modules.amazoninfo.dao.ReviewerSendEmailDao;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerEmail;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerSendEmail;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Component
@Transactional(readOnly = true)
public class ReviewerEmailService extends BaseService{
	@Autowired
	private ReviewerEmailDao reviewerEmailDao;

	@Autowired
	private ReviewerSendEmailDao sendEmailDao;
	
	public ReviewerEmail get(Integer id) {
		return reviewerEmailDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(ReviewerEmail email) {
		reviewerEmailDao.save(email);
	}
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		reviewerEmailDao.deleteById(id);
	}
	
	public Page<ReviewerEmail> find(Page<ReviewerEmail> page, ReviewerEmail reviewerEmail, String aboutMe) {
		DetachedCriteria dc = reviewerEmailDao.createDetachedCriteria();
		String subject = reviewerEmail.getSubject();
		if (StringUtils.isNotEmpty(reviewerEmail.getSubject())){dc.add(Restrictions.or(Restrictions.like("subject", "%"+subject+"%"),Restrictions.like("revertEmail", "%"+subject+"%")
					,Restrictions.like("receiveContent", "%"+subject+"%")));
		}
		
		if(StringUtils.isNotEmpty(reviewerEmail.getAttchmentPath())){
			dc.add(Restrictions.or(Restrictions.isNotNull("attchmentPath"),Restrictions.isNotNull("inlineAttchmentPath")));
		}
		if(StringUtils.isNotEmpty(reviewerEmail.getCountry())){
			dc.createAlias("formReviewer", "formReviewer");
			dc.add(Restrictions.eq("formReviewer.country", reviewerEmail.getCountry()));
		}
		if (reviewerEmail.getCustomSendDate() != null){
			dc.add(Restrictions.ge("customSendDate", reviewerEmail.getCustomSendDate()));
		}
		if (reviewerEmail.getEndDate() != null){
			dc.add(Restrictions.le("customSendDate", DateUtils.addDays(reviewerEmail.getEndDate(), 1)));
		}
		
		if(StringUtils.isNotEmpty(reviewerEmail.getRemarks())){
			dc.add(Restrictions.eq("remarks", "%"+reviewerEmail.getRemarks()+"%"));
		}
		
		if (StringUtils.isNotEmpty(reviewerEmail.getState()) && "5".equals(reviewerEmail.getState())) {
			dc.add(Restrictions.in("state", Lists.newArrayList("0","1")));
		} else if (StringUtils.isNotEmpty(reviewerEmail.getState())){
			dc.add(Restrictions.eq("state", reviewerEmail.getState()));
		}
		
		dc.add(Restrictions.eq(ReviewerEmail.FIELD_DEL_FLAG, ReviewerEmail.DEL_FLAG_NORMAL));
		//TODO 增加与我相关
		User user = UserUtils.getUser();
		if(StringUtils.isNotEmpty(aboutMe) && "1".equals(aboutMe)){
			User masterBy = reviewerEmail.getMasterBy();
			if(null ==masterBy){
				reviewerEmail.setMasterBy(user);
				masterBy = user;
			}
			if (StringUtils.isNotEmpty(masterBy.getId())){
				//dc.add(Restrictions.eq("masterBy",masterBy));
				dc.add(Restrictions.or(Restrictions.eq("masterBy",masterBy),
						Restrictions.isNull("masterBy")));
			}	
		}	
		page =  reviewerEmailDao.find(page, dc);
		return page;
	}
	
	public boolean isProcessedEmail(String emailId){
		List<Object> list = reviewerEmailDao.findBySql("select count(1) from amazoninfo_reviewer_email_manager where email_id = :p1",new Parameter(emailId));
		if(list!=null&&list.size()==1){
			return Integer.parseInt(list.get(0).toString())>0;
		}
		return true;
	}

	@Transactional(readOnly = false)
	public void  saveProblem(String country,String productName,String  problemType,String problem,String id,String orderNos ){
		if(StringUtils.isNotEmpty(orderNos)){
			String sql ="UPDATE amazoninfo_reviewer_email_manager  SET country=:p2,product_name=:p3,problem_type=:p4,problem=:p5,order_nos=:p6 WHERE id=:p1  ";
			this.reviewerEmailDao.updateBySql(sql, new Parameter(id,country,productName,problemType,problem,orderNos));  
		}else{
			String sql ="UPDATE amazoninfo_reviewer_email_manager  SET country=:p2,product_name=:p3,problem_type=:p4,problem=:p5 WHERE id=:p1  ";
			this.reviewerEmailDao.updateBySql(sql, new Parameter(id,country,productName,problemType,problem));  
		}
	}
	
	public User findMastByFromEmail(List<String> emailList) {
		Page<ReviewerEmail> page = new Page<ReviewerEmail>();
		page.setPageSize(1);
		page.setPageNo(1);
		page = reviewerEmailDao.find(page,"from ReviewerEmail where delFlag = '0' and revertEmail in(:p1) and masterBy.delFlag = '0' Order by endDate Desc",new Parameter(emailList));
		List<ReviewerEmail> list  = page.getList();
		if(list.size() > 0){
			return list.get(0).getMasterBy();
		}
		
		Page<ReviewerSendEmail> page1 = new Page<ReviewerSendEmail>();
		page1.setPageSize(1);
		page1.setPageNo(1);
		page1 = sendEmailDao.find(page1,"from ReviewerSendEmail where delFlag = '0' and sendEmail in(:p1) and createBy.delFlag = '0' and sendFlag = '1' Order by sentDate Desc ",new Parameter(emailList));
		List<ReviewerSendEmail> lists  = page1.getList();
		if(lists.size() > 0){
			return lists.get(0).getCreateBy();
		}
		return null;
	}
	
	public List<ReviewerEmail> findByReviewerEmail(List<String> emailList) {
		DetachedCriteria dc = reviewerEmailDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.eq("state", "0"),Restrictions.eq("state", "1")));
		dc.add(Restrictions.eq(ReviewerEmail.FIELD_DEL_FLAG, ReviewerEmail.DEL_FLAG_NORMAL));
		User masterBy = UserUtils.getUser();
		if(!masterBy.isAdmin()){
			if (StringUtils.isNotEmpty(masterBy.getId())){
				dc.add(Restrictions.or(Restrictions.eq("masterBy",masterBy),
						Restrictions.isNull("masterBy")));
			}	
		}
		dc.add(Restrictions.in("revertEmail", emailList));
		dc.addOrder(Order.desc("state"));
		return reviewerEmailDao.find(dc);
	}
	
	/**
	 *保存备注 
	 */
	@Transactional(readOnly = false)
	public void  saveRemark(String remark,Integer reviewerEmailId){
		try{
			if(StringUtils.isNotEmpty(remark)){
				String sql ="UPDATE amazoninfo_reviewer_email_manager AS a SET a.`remarks`=:p2 WHERE a.id=:p1  ";
				this.reviewerEmailDao.updateBySql(sql, new Parameter(reviewerEmailId,remark));  
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
