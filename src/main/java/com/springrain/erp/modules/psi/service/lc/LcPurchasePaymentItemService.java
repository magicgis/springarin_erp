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
import com.springrain.erp.modules.psi.dao.lc.LcPurchasePaymentItemDao;
import com.springrain.erp.modules.psi.entity.lc.LcPurchasePaymentItem;

/**
 * 采购付款明细Service
 * @author Michael
 * @version 2014-11-21
 */
@Component
@Transactional(readOnly = true)
public class LcPurchasePaymentItemService extends BaseService {

	@Autowired
	private LcPurchasePaymentItemDao purchasePaymentItemDao;
	
	public LcPurchasePaymentItem get(Integer id) {
		return purchasePaymentItemDao.get(id);
	}
	
	public Page<LcPurchasePaymentItem> find(Page<LcPurchasePaymentItem> page, LcPurchasePaymentItem purchasePaymentItem) {
		DetachedCriteria dc = purchasePaymentItemDao.createDetachedCriteria();
		return purchasePaymentItemDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(LcPurchasePaymentItem purchasePaymentItem) {
		purchasePaymentItemDao.save(purchasePaymentItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		purchasePaymentItemDao.deleteById(id);
	}
	
}
