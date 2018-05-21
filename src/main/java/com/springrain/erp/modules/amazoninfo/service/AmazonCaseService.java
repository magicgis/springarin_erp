package com.springrain.erp.modules.amazoninfo.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonCaseDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonCase;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrder;


@Component
@Transactional(readOnly = true)
public class AmazonCaseService extends BaseService {

	@Autowired
	private AmazonCaseDao amazonCaseDao;
	
	public AmazonCase get(Integer id){
		return amazonCaseDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonCase> amazonCases) {
		amazonCaseDao.save(amazonCases);
	}
	
	@Transactional(readOnly = false)
	public void save(AmazonCase amazonCase) {
		amazonCaseDao.save(amazonCase);
	}

	public Page<AmazonCase> find(Page<AmazonCase> page, AmazonCase amazonCase) {
		DetachedCriteria dc = amazonCaseDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(amazonCase.getCountry())){
			dc.add(Restrictions.eq("country", amazonCase.getCountry()));
		}
		if (StringUtils.isNotEmpty(amazonCase.getCaseId())){
			dc.add(Restrictions.or(Restrictions.eq("caseId", amazonCase.getCaseId()),Restrictions.eq("asin", amazonCase.getCaseId())));
		}
		if (amazonCase.getCreateBy()!=null &&  StringUtils.isNotEmpty(amazonCase.getCreateBy().getId())){
			dc.add(Restrictions.eq("createBy.id", amazonCase.getCreateBy().getId()));
		}
		return amazonCaseDao.find(page, dc);
	}
	
}
