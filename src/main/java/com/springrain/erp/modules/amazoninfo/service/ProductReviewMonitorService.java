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
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonReviewDao;
import com.springrain.erp.modules.amazoninfo.dao.ProductReviewMonitorDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonReview;
import com.springrain.erp.modules.amazoninfo.entity.EvaluateWarning;
import com.springrain.erp.modules.amazoninfo.entity.ProductReviewMonitor;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Component
@Transactional(readOnly = true)
public class ProductReviewMonitorService extends BaseService {

	@Autowired
	private AmazonReviewDao amazonReviewDao;
	
	@Autowired
	private ProductReviewMonitorDao productReviewMonitorDao;
	

	public Page<ProductReviewMonitor> find(Page<ProductReviewMonitor> page, ProductReviewMonitor productReviewMonitor) {
		DetachedCriteria dc = productReviewMonitorDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(productReviewMonitor.getProductName())){
			dc.add(Restrictions.eq("productName", productReviewMonitor.getProductName()));
		}
		if(StringUtils.isNotEmpty(productReviewMonitor.getCountry())){
			dc.add(Restrictions.eq("country", productReviewMonitor.getCountry()));
		}
		if(productReviewMonitor.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate", productReviewMonitor.getCreateDate()));
		}
		if (productReviewMonitor.getEndDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(productReviewMonitor.getEndDate(),1)));
		}
		if(StringUtils.isNotEmpty(productReviewMonitor.getState())){
			dc.add(Restrictions.eq("state", productReviewMonitor.getState()));
		}
		page.setOrderBy("id desc");
		return productReviewMonitorDao.find(page, dc);
	}
	
	
	public ProductReviewMonitor get(Integer id) {
		return productReviewMonitorDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(ProductReviewMonitor productReviewMonitor) {
		if(productReviewMonitor.getId()==null){
			productReviewMonitor.setCreateDate(new Date());
			productReviewMonitor.setCreateUser(UserUtils.getUser());
			productReviewMonitor.setState("1");
		}
		productReviewMonitorDao.save(productReviewMonitor);
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonReview> reviews) {
		amazonReviewDao.save(reviews);
	}
	
	public List<ProductReviewMonitor> findAllByE() {
		DetachedCriteria dc = productReviewMonitorDao.createDetachedCriteria();
		dc.add(Restrictions.eq("state","1"));
		List<ProductReviewMonitor> rs =  productReviewMonitorDao.find(dc);
		for (ProductReviewMonitor productReviewMonitor : rs) {
			Hibernate.initialize(productReviewMonitor.getReviews());
		}
		return rs;
	}
	
	
}
