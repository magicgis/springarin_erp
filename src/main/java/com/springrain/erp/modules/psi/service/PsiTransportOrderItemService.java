/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.PsiTransportOrderItemDao;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderItem;

/**
 * 运单明细表Service
 * @author Michael
 * @version 2015-01-15
 */
@Component
@Transactional(readOnly = true)
public class PsiTransportOrderItemService extends BaseService {

	@Autowired
	private PsiTransportOrderItemDao psiTransportOrderItemDao;
	
	public PsiTransportOrderItem get(Integer id) {
		return psiTransportOrderItemDao.get(id);
	}
	
	public Page<PsiTransportOrderItem> find(Page<PsiTransportOrderItem> page, PsiTransportOrderItem psiTransportOrderItem) {
		DetachedCriteria dc = psiTransportOrderItemDao.createDetachedCriteria();
		return psiTransportOrderItemDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiTransportOrderItem psiTransportOrderItem) {
		psiTransportOrderItemDao.save(psiTransportOrderItem);
	}
	
	
	/**
	 *根据itemId获取list信息 
	 */
	public List<PsiTransportOrderItem> getTransportOrderItems(Set<Integer> ids){
		DetachedCriteria dc = psiTransportOrderItemDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		return psiTransportOrderItemDao.find(dc);
	}
	
	/**
	 *根据itemId获取list信息 
	 */
	public List<PsiTransportOrderItem> getTransportOrderItems(Integer transportOrderId){
		DetachedCriteria dc = psiTransportOrderItemDao.createDetachedCriteria();
		dc.add(Restrictions.eq("transportOrder.id", transportOrderId));
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.asc("id"));
		return psiTransportOrderItemDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiTransportOrderItemDao.deleteById(id);
	}
	
}
