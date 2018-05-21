/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.parts;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsPaymentItem;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsPaymentItemDao;

/**
 * 配件订单付款详情Service
 * @author Michael
 * @version 2015-06-15
 */
@Component
@Transactional(readOnly = true)
public class PsiPartsPaymentItemService extends BaseService {

	@Autowired
	private PsiPartsPaymentItemDao psiPartsPaymentItemDao;
	public Page<PsiPartsPaymentItem> find(Page<PsiPartsPaymentItem> page, PsiPartsPaymentItem psiPartsPaymentItem) {
		//return psiPartsPaymentItemDao.find(page, dc);
		return null;
	}
	    
	@Transactional(readOnly = false)
	public void save(PsiPartsPaymentItem psiPartsPaymentItem) {
		psiPartsPaymentItemDao.save(psiPartsPaymentItem);
	}  
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiPartsPaymentItemDao.deleteById(id);
	}
	
}
