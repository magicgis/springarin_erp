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
import com.springrain.erp.modules.psi.entity.PsiInventoryInItem;
import com.springrain.erp.modules.psi.dao.PsiInventoryInItemDao;

/**
 * 入库明细管理Service
 * @author Michael
 * @version 2015-01-05
 */
@Component
@Transactional(readOnly = true)
public class PsiInventoryInItemService extends BaseService {

	@Autowired
	private PsiInventoryInItemDao psiInventoryInItemDao;
	
	public PsiInventoryInItem get(String id) {
		return psiInventoryInItemDao.get(id);
	}
	
	public Page<PsiInventoryInItem> find(Page<PsiInventoryInItem> page, PsiInventoryInItem psiInventoryInItem) {
		DetachedCriteria dc = psiInventoryInItemDao.createDetachedCriteria();
		return psiInventoryInItemDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiInventoryInItem psiInventoryInItem) {
		psiInventoryInItemDao.save(psiInventoryInItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiInventoryInItemDao.deleteById(id);
	}
	
}
