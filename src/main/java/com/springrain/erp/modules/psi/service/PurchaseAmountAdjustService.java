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
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.PurchaseAmountAdjustDao;
import com.springrain.erp.modules.psi.entity.PurchaseAmountAdjust;

/**
 * 采购金额调整Service
 * @author Michael
 * @version 2015-08-24
 */
@Component
@Transactional(readOnly = true)
public class PurchaseAmountAdjustService extends BaseService {
	@Autowired
	private PurchaseAmountAdjustDao purchaseAmountAdjustDao;
	
	public PurchaseAmountAdjust get(Integer id) {
		return purchaseAmountAdjustDao.get(id);
	}
	
	public Page<PurchaseAmountAdjust> find(Page<PurchaseAmountAdjust> page, PurchaseAmountAdjust purchaseAmountAdjust) {
		DetachedCriteria dc = purchaseAmountAdjustDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(purchaseAmountAdjust.getSubject())){
			dc.add(Restrictions.like("subject","%"+purchaseAmountAdjust.getSubject()+"%"));
		}
		
		if(purchaseAmountAdjust.getSupplier()!=null&&purchaseAmountAdjust.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier.id", purchaseAmountAdjust.getSupplier().getId()));
		}
		
		if(purchaseAmountAdjust.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate", purchaseAmountAdjust.getCreateDate()));
		}
		
		if (purchaseAmountAdjust.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(purchaseAmountAdjust.getUpdateDate(),1)));
		}
		
		page.setOrderBy("adjustSta,id desc");
		return purchaseAmountAdjustDao.find2(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public void save(PurchaseAmountAdjust purchaseAmountAdjust) {
		purchaseAmountAdjustDao.save(purchaseAmountAdjust);
	}
	
	public List<PurchaseAmountAdjust> findAdjustOrders(Set<Integer> orderIds) {
		DetachedCriteria dc = purchaseAmountAdjustDao.createDetachedCriteria();
		dc.add(Restrictions.in("orderId",orderIds));
		dc.add(Restrictions.eq("adjustSta", "0"));
		dc.addOrder(Order.desc("id"));
		return purchaseAmountAdjustDao.find(dc);
	}
	
	public List<PurchaseAmountAdjust> findAdjustOrders(Integer supplierId,Integer paymentId,String sta,String currency) {
		
		DetachedCriteria dc = purchaseAmountAdjustDao.createDetachedCriteria();
		dc.add(Restrictions.eq("supplier.id",supplierId));
		if(paymentId!=null){
			dc.add(Restrictions.eq("paymentId",paymentId));
		}
		if(StringUtils.isNotEmpty(sta)){
			dc.add(Restrictions.eq("adjustSta", sta));
		}
		if(StringUtils.isNotEmpty(currency)){
			dc.add(Restrictions.eq("currency", currency));
		}
		dc.addOrder(Order.desc("id"));
		return purchaseAmountAdjustDao.find(dc);
	}
}
