/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.entity.PsiInventoryOutItem;
import com.springrain.erp.modules.psi.dao.PsiInventoryOutItemDao;

/**
 * 出库明细管理Service
 * @author Michael
 * @version 2015-01-05
 */
@Component
@Transactional(readOnly = true)
public class PsiInventoryOutItemService extends BaseService {

	@Autowired
	private PsiInventoryOutItemDao psiInventoryOutItemDao;
	
	public PsiInventoryOutItem get(Integer id) {
		return psiInventoryOutItemDao.get(id);
	}
	
	public Page<PsiInventoryOutItem> find(Page<PsiInventoryOutItem> page, PsiInventoryOutItem psiInventoryOutItem) {
		DetachedCriteria dc = psiInventoryOutItemDao.createDetachedCriteria();
		return psiInventoryOutItemDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiInventoryOutItem psiInventoryOutItem) {
		psiInventoryOutItemDao.save(psiInventoryOutItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiInventoryOutItemDao.deleteById(id);
	}
	
}
