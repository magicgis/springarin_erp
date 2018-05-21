/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.lc;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.lc.LcPsiLadingBillItemDao;
import com.springrain.erp.modules.psi.entity.lc.LcPsiLadingBillItem;

/**
 * 提单明细Service
 * @author Michael
 * @version 2014-11-11
 */
@Component
@Transactional(readOnly = true)
public class LcPsiLadingBillItemService extends BaseService {

	@Autowired
	private LcPsiLadingBillItemDao psiLadingBillItemDao;
	
	public LcPsiLadingBillItem get(Integer id) {
		return psiLadingBillItemDao.get(id);
	}
	
	public Page<LcPsiLadingBillItem> find(Page<LcPsiLadingBillItem> page, LcPsiLadingBillItem psiLadingBillItem) {
		DetachedCriteria dc = psiLadingBillItemDao.createDetachedCriteria();
		return psiLadingBillItemDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(LcPsiLadingBillItem psiLadingBillItem) {
		psiLadingBillItemDao.save(psiLadingBillItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiLadingBillItemDao.deleteById(id);
	}
	
}
