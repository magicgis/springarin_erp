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
import com.springrain.erp.modules.psi.dao.PsiLadingBillItemDao;
import com.springrain.erp.modules.psi.entity.PsiLadingBillItem;

/**
 * 提单明细Service
 * @author Michael
 * @version 2014-11-11
 */
@Component
@Transactional(readOnly = true)
public class PsiLadingBillItemService extends BaseService {

	@Autowired
	private PsiLadingBillItemDao psiLadingBillItemDao;
	
	public PsiLadingBillItem get(Integer id) {
		return psiLadingBillItemDao.get(id);
	}
	
	public Page<PsiLadingBillItem> find(Page<PsiLadingBillItem> page, PsiLadingBillItem psiLadingBillItem) {
		DetachedCriteria dc = psiLadingBillItemDao.createDetachedCriteria();
		return psiLadingBillItemDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiLadingBillItem psiLadingBillItem) {
		psiLadingBillItemDao.save(psiLadingBillItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiLadingBillItemDao.deleteById(id);
	}
	
}
