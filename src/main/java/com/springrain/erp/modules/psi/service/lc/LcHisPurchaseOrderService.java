/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.lc;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.HisPurchaseOrderDao;
import com.springrain.erp.modules.psi.dao.lc.LcHisPurchaseOrderDao;
import com.springrain.erp.modules.psi.entity.lc.LcHisPurchaseOrder;

/**
 * 采购订单Service
 * @author Michael
 * @version 2014-10-29
 */
@Component
@Transactional(readOnly = true)
public class LcHisPurchaseOrderService extends BaseService {
	
	
	@Autowired
	private LcHisPurchaseOrderDao hisPurchaseOrderDao;
	
	public LcHisPurchaseOrder get(Integer id) {
		return hisPurchaseOrderDao.get(id);
	}
	
	public List<Object[]> getOrderVersion(String orderNo){
		String getProductLadingSql="SELECT id,version_no,modify_memo FROM lc_psi_his_purchase_order AS ho WHERE order_no=:p1 order by id ";
		return hisPurchaseOrderDao.findBySql(getProductLadingSql, new Parameter(orderNo));
	}		
	
	public Page<LcHisPurchaseOrder> find(Page<LcHisPurchaseOrder> page, LcHisPurchaseOrder purchaseOrder) {
		DetachedCriteria dc = hisPurchaseOrderDao.createDetachedCriteria();
		if (purchaseOrder.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",purchaseOrder.getCreateDate()));
		}
		if (purchaseOrder.getPurchaseDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(purchaseOrder.getPurchaseDate(),1)));
		}
		if(StringUtils.isNotEmpty(purchaseOrder.getOrderNo())){
			dc.add(Restrictions.like("orderNo", "%"+purchaseOrder.getOrderNo()+"%"));
		}
		if(StringUtils.isNotEmpty(purchaseOrder.getOrderSta())){
			dc.add(Restrictions.eq("orderSta", purchaseOrder.getOrderSta()));
		}else{
			dc.add(Restrictions.not(Restrictions.in("orderSta",new Object[]{"6","1"})));
		}
		
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", purchaseOrder.getSupplier()));
		}
		page.setOrderBy("id desc");
		return hisPurchaseOrderDao.find(page, dc);
	}
	
	
		
	@Transactional(readOnly = false)
	public void save(LcHisPurchaseOrder hisHisPurchaseOrder) {
		hisPurchaseOrderDao.save(hisHisPurchaseOrder);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		hisPurchaseOrderDao.deleteById(id);
	}
	
	
	
	
}
