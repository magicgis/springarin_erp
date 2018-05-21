/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.PsiTransportReviseItemDao;
import com.springrain.erp.modules.psi.entity.PsiTransportReviseItem;

/**
 * 运单付款修正item表Service
 * @author Michael
 * @version 2015-01-29
 */
@Component
@Transactional(readOnly = true)
public class PsiTransportReviseItemService extends BaseService {

	@Autowired
	private PsiTransportReviseItemDao psiTransportReviseItemDao;
	
	public PsiTransportReviseItem get(Integer id) {
		return psiTransportReviseItemDao.get(id);
	}
	
	public Page<PsiTransportReviseItem> find(Page<PsiTransportReviseItem> page, PsiTransportReviseItem psiTransportReviseItem) {
		DetachedCriteria dc = psiTransportReviseItemDao.createDetachedCriteria();
		return psiTransportReviseItemDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiTransportReviseItem psiTransportReviseItem) {
		psiTransportReviseItemDao.save(psiTransportReviseItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiTransportReviseItemDao.deleteById(id);
	}
	
}
