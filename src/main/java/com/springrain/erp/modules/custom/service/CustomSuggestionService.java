package com.springrain.erp.modules.custom.service;

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
import com.springrain.erp.modules.custom.dao.CustomSuggestionDao;
import com.springrain.erp.modules.custom.entity.CustomSuggestion;

/**
 * 客户建议Service
 */
@Component
@Transactional(readOnly = true)
public class CustomSuggestionService extends BaseService{

	@Autowired
	private CustomSuggestionDao customSuggestionDao;
	
	public CustomSuggestion getByEmailId(String id) {
		DetachedCriteria dc = customSuggestionDao.createDetachedCriteria();
		dc.add(Restrictions.eq("customEmail.id", id));
		List<CustomSuggestion> list = customSuggestionDao.find(dc);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void saveSuggestion(CustomSuggestion customSuggestion) {
		customSuggestionDao.save(customSuggestion);
	}
	
	public CustomSuggestion getSuggestionById(Integer id) {
		return customSuggestionDao.get(id);
	}
	
	public List<CustomSuggestion> find(CustomSuggestion suggestion) {
		DetachedCriteria dc = customSuggestionDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(suggestion.getProductName())){
			dc.add(Restrictions.like("productName", "%" + suggestion.getProductName() + "%"));
		}
		if (StringUtils.isNotEmpty(suggestion.getCountry())){
			dc.add(Restrictions.eq("country", suggestion.getCountry()));
		}
		if (suggestion.getCreateDate() != null){
			dc.add(Restrictions.ge("createDate", suggestion.getCreateDate()));
		}
		if (suggestion.getEndDate() != null){
			dc.add(Restrictions.le("createDate", DateUtils.addDays(suggestion.getEndDate(), 1)));
		}
		return customSuggestionDao.find(dc);
	}
	
}
