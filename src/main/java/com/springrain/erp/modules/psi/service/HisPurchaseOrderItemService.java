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
import com.springrain.erp.modules.psi.dao.PurchaseOrderItemDao;
import com.springrain.erp.modules.psi.entity.PurchaseOrderItem;

/**
 * 采购订单明细Service
 * @author Michael
 * @version 2014-10-29
 */
@Component
@Transactional(readOnly = true)
public class HisPurchaseOrderItemService extends BaseService {

	@Autowired
	private PurchaseOrderItemDao purchaseOrderItemDao;
	
	public PurchaseOrderItem get(Integer id) {
		return purchaseOrderItemDao.get(id);
	}
	
	public Page<PurchaseOrderItem> find(Page<PurchaseOrderItem> page, PurchaseOrderItem purchaseOrderItem) {
		DetachedCriteria dc = purchaseOrderItemDao.createDetachedCriteria();
		return purchaseOrderItemDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PurchaseOrderItem purchaseOrderItem) {
		purchaseOrderItemDao.save(purchaseOrderItem);
	}
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		purchaseOrderItemDao.deleteById(id);
	}
	
}
