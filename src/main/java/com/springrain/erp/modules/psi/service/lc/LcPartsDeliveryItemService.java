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
import com.springrain.erp.modules.psi.dao.lc.LcPsiPartsDeliveryItemDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsDeliveryItemDao;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsDeliveryItem;

/**
 * 配件收货详情Service
 * @author Michael
 * @version 2015-07-03
 */
@Component
@Transactional(readOnly = true)
public class LcPartsDeliveryItemService extends BaseService {
	@Autowired  
	private LcPsiPartsDeliveryItemDao psiPartsDeliveryItemDao;
	
	public LcPsiPartsDeliveryItem get(String id) {
		return psiPartsDeliveryItemDao.get(id);
	}
	
	public Page<LcPsiPartsDeliveryItem> find(Page<LcPsiPartsDeliveryItem> page, LcPsiPartsDeliveryItem LcPsiPartsDeliveryItem) {
		DetachedCriteria dc = psiPartsDeliveryItemDao.createDetachedCriteria();
		return psiPartsDeliveryItemDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(LcPsiPartsDeliveryItem LcPsiPartsDeliveryItem) {
		psiPartsDeliveryItemDao.save(LcPsiPartsDeliveryItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiPartsDeliveryItemDao.deleteById(id);
	}
	
	
}
