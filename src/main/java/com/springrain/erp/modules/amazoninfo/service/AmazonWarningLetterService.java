package com.springrain.erp.modules.amazoninfo.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonWarningLetterDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonWarningLetter;

/**
 * 亚马逊警告信件Service
 */
@Component
@Transactional(readOnly = true)
public class AmazonWarningLetterService extends BaseService {

	@Autowired
	private AmazonWarningLetterDao warningLetterDao;
	
	public AmazonWarningLetter get(Integer id) {
		return warningLetterDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(AmazonWarningLetter letter) {
		warningLetterDao.save(letter);
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonWarningLetter> letters) {
		warningLetterDao.save(letters);
	}
	
	public AmazonWarningLetter findByCountryAndLetterId(String country, String letterId) {
		DetachedCriteria dc = warningLetterDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(country)) {
			dc.add(Restrictions.eq("country", country));
		}
		dc.add(Restrictions.eq("letterId", letterId));
		List<AmazonWarningLetter> list = warningLetterDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public Page<AmazonWarningLetter> find(Page<AmazonWarningLetter> page, AmazonWarningLetter warningLetter) {
		DetachedCriteria dc = warningLetterDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(warningLetter.getCountry())){
			dc.add(Restrictions.eq("country", warningLetter.getCountry()));
		}
		if (StringUtils.isNotEmpty(warningLetter.getSubject())){
			dc.add(Restrictions.like("subject", "%" + warningLetter.getSubject() + "%"));
		}
		if (StringUtils.isNotEmpty(warningLetter.getProductName())){
			dc.add(Restrictions.like("productName", "%" + warningLetter.getProductName() + "%"));
		}
		return warningLetterDao.find(page, dc);
	}
	
	//统计产品收到警告信的次数
	public int countByProductName(String productName) {
		DetachedCriteria dc = warningLetterDao.createDetachedCriteria();
		dc.add(Restrictions.eq("productName", productName));
		List<AmazonWarningLetter> list = warningLetterDao.find(dc);
		return list.size();
	}
	
}
