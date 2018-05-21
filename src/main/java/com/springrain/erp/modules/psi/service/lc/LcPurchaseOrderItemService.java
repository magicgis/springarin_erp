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

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.lc.LcPurchaseOrderItemDao;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrderItem;

/**
 * 采购订单明细Service
 * @author Michael
 * @version 2014-10-29
 */
@Component
@Transactional(readOnly = true)
public class LcPurchaseOrderItemService extends BaseService {

	@Autowired
	private LcPurchaseOrderItemDao purchaseOrderItemDao;
	
	public LcPurchaseOrderItem get(Integer id) {
		return purchaseOrderItemDao.get(id);
	}
	
	public Page<LcPurchaseOrderItem> find(Page<LcPurchaseOrderItem> page, LcPurchaseOrderItem purchaseOrderItem) {
		DetachedCriteria dc = purchaseOrderItemDao.createDetachedCriteria();
		return purchaseOrderItemDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(LcPurchaseOrderItem purchaseOrderItem) {
		purchaseOrderItemDao.save(purchaseOrderItem);
	}
	
	public String findUnitPrice(Integer productId,String countryCode,String colorCode){
		String sql ="SELECT CASE WHEN b.`country_code`='jp' OR b.`country_code`='com' OR b.`country_code`='ca' THEN 1.1*b.item_price ELSE 1.2*b.item_price END ,a.`currency_type` FROM lc_psi_purchase_order AS a,lc_psi_purchase_order_item AS b WHERE a.`id`=b.`purchase_order_id` AND b.del_flag='0' AND a.`order_sta` IN ('2','3','4') AND b.product_id=:p1 AND b.country_code=:p2 AND b.color_code=:p3 ORDER BY b.id DESC  LIMIT 1";
		List<Object[]> list=this.purchaseOrderItemDao.findBySql(sql, new Parameter(productId,countryCode,colorCode));
		for(Object[] object:list){
			return object[0]==null?"":object[0].toString()+","+object[1];
		}
		return null;
	}
	
	public List<LcPurchaseOrderItem> getOrderItems(String items) {
		DetachedCriteria dc = purchaseOrderItemDao.createDetachedCriteria();
		List<Integer> ids = Lists.newArrayList();
		for(String id:items.split(",")){
			ids.add(Integer.parseInt(id));
		}
		dc.add(Restrictions.in("id",ids));
		return purchaseOrderItemDao.find(dc);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		purchaseOrderItemDao.deleteById(id);
	}
	
}
