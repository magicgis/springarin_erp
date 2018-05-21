package com.springrain.erp.modules.amazoninfo.service;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonAccountDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccount;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 亚马逊后台账号信息Service
 * @author Tim
 * @version 2015-01-14
 */
@Component
@Transactional(readOnly = true)
public class AmazonAccountService extends BaseService {

	@Autowired
	private AmazonAccountDao amazonAccountDao;
	
	public AmazonAccount get(String id) {
		return amazonAccountDao.get(id);
	}
	
	public Page<AmazonAccount> find(Page<AmazonAccount> page, AmazonAccount amazonAccount) {
		DetachedCriteria dc = amazonAccountDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(amazonAccount.getAccountEmail())){
			dc.add(Restrictions.like("accountEmail", "%"+amazonAccount.getAccountEmail()+"%"));
		}
		amazonAccount.setCountry(StringUtils.isNotEmpty(amazonAccount.getCountry())?amazonAccount.getCountry():"com");
		dc.add(Restrictions.eq("country",amazonAccount.getCountry()));
		dc.add(Restrictions.eq("delFlag","0"));
		dc.addOrder(Order.desc("lastUpdateDate"));
		return amazonAccountDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(AmazonAccount amazonAccount) {
		amazonAccountDao.save(amazonAccount);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		amazonAccountDao.deleteById(id);
	}
	
	public boolean findExistAaccount(AmazonAccount amazonAccount) {
		DetachedCriteria dc = amazonAccountDao.createDetachedCriteria();
		dc.add(Restrictions.eq("createUser",UserUtils.getUser()));
		dc.add(Restrictions.eq("country",amazonAccount.getCountry()));
		dc.add(Restrictions.eq("delFlag","0"));
		return amazonAccountDao.count(dc)>0;
	}
	
	public boolean findExistAaccount(String userId,String country) {
		DetachedCriteria dc = amazonAccountDao.createDetachedCriteria();
		dc.add(Restrictions.eq("accountId",userId));
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.eq("delFlag","0"));
		return amazonAccountDao.count(dc)>0;
	}
}
