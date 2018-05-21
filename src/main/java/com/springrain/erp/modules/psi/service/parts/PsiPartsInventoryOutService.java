/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.parts;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsInventoryDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsInventoryOutDao;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventory;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventoryOut;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventoryOutItem;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventoryOutOrder;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 配件出库Service
 * @author Michael
 * @version 2015-07-16
 */
@Component
@Transactional(readOnly = true)
public class PsiPartsInventoryOutService extends BaseService {
	@Autowired
	private PsiPartsInventoryOutDao		    psiPartsInventoryOutDao;
	@Autowired
	private PsiPartsInventoryService		psiPartsInventoryService;
	@Autowired
	private PsiPartsInventoryDao 			psiPartsInventoryDao;
	
	public PsiPartsInventoryOut get(String id) {
		return psiPartsInventoryOutDao.get(id);
	}
	
	public Page<PsiPartsInventoryOut> find(Page<PsiPartsInventoryOut> page, PsiPartsInventoryOut psiPartsInventoryOut){
		
		DetachedCriteria dc = psiPartsInventoryOutDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiPartsInventoryOut.getBillNo())){
			dc.createAlias("this.orders", "order");
			dc.add(Restrictions.or(Restrictions.like("order.purchaseOrderNo", "%"+psiPartsInventoryOut.getBillNo()+"%")));
		}
		
		if(StringUtils.isNotEmpty(psiPartsInventoryOut.getProductName())){
			dc.add(Restrictions.or(Restrictions.like("productName", "%"+psiPartsInventoryOut.getProductName()+"%")));
		}
		
		if(StringUtils.isNotEmpty(psiPartsInventoryOut.getRemark())){
			dc.createAlias("this.items", "item");
			dc.add(Restrictions.or(Restrictions.like("item.partsName", "%"+psiPartsInventoryOut.getRemark()+"%")));
		}
		//dc.addOrder(Order.desc("id"));
		page.setOrderBy(" id desc");
		return psiPartsInventoryOutDao.find2(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public void addSave(PsiPartsInventoryOut psiPartsInventoryOut) {
		this.exePartsInventoryOut(psiPartsInventoryOut);
		for(PsiPartsInventoryOutOrder order:psiPartsInventoryOut.getOrders()){
			order.setPartsInventoryOut(psiPartsInventoryOut);
			Integer quantity = order.getQuantity();
			if(psiPartsInventoryOutDao.getCanDeliveryQuantity(order.getPurchaseOrderId(), psiPartsInventoryOut.getProductId(),psiPartsInventoryOut.getColor())!=null){
				//增加数量
				psiPartsInventoryOutDao.addProductCanDeliveryQuantity(order.getPurchaseOrderId(), psiPartsInventoryOut.getProductId(), quantity,psiPartsInventoryOut.getColor());
			}else{
				//增加一条校验信息
				psiPartsInventoryOutDao.insertProductCanDeliveryCheck(order.getPurchaseOrderId(), psiPartsInventoryOut.getProductId(), quantity,psiPartsInventoryOut.getColor());
			}
			
		}
		psiPartsInventoryOut.setCreateDate(new Date());
		psiPartsInventoryOut.setCreateUser(UserUtils.getUser());
		psiPartsInventoryOutDao.save(psiPartsInventoryOut);
	}      
	
	/**
	 *配件出库更新配件库存 
	 * 
	 */
	private void exePartsInventoryOut(PsiPartsInventoryOut psiPartsInventoryOut){
		for(PsiPartsInventoryOutItem item:psiPartsInventoryOut.getItems()){
			item.setPartsInventoryOut(psiPartsInventoryOut);
			Integer partsId = item.getPartsId();
			Integer quantity = item.getQuantity();
			PsiPartsInventory partsInventory=this.psiPartsInventoryService.getPsiPartsInventorys(partsId);
			//订单后货只会收库存锁定的数量，如果不足就报异常
			if(partsInventory.getStockFrozen()-quantity>=0){
				partsInventory.setStockFrozen(partsInventory.getStockFrozen()-quantity);
				this.psiPartsInventoryDao.save(partsInventory);
			}else{
				throw new RuntimeException("产品订单收货，配件库存锁定数:"+partsInventory.getStockFrozen()+"小于要收货数:"+quantity+"，操作已取消");
			}
			
			//添加日志
			this.psiPartsInventoryService.saveLog(partsId, partsInventory.getPartsName(), 0-quantity,"stockFrozen", "", UserUtils.getUser(), new Date(), "Inventory Out", "");
		}
	}
	
	@Transactional(readOnly = false)
	public void save(PsiPartsInventoryOut psiPartsInventoryOut) {
		psiPartsInventoryOutDao.save(psiPartsInventoryOut);
	}
	
	
	
	public Integer getCanDeliveryQuantity(Integer purchaseOrderId, Integer productId,String color){
		return this.psiPartsInventoryOutDao.getCanDeliveryQuantity(purchaseOrderId, productId,color);
	}
	
	
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiPartsInventoryOutDao.deleteById(id);
	}
	
	/***
	 *根据订单id 获得可提货数量 
	 * key:productId,
	 */
	public Map<String,Integer> getCanLadingQuantity(Integer purchaseOrderId){
		Map<String,Integer> tempMap=Maps.newHashMap();
		String sql="SELECT a.`product_id`,a.color,a.`can_lading_quantity` FROM  psi_parts_delivery_check AS a WHERE a.`can_lading_quantity`>0 AND a.`purchase_order_id`=:p1 ";
		List<Object[]> list=this.psiPartsInventoryOutDao.findBySql(sql, new Parameter(purchaseOrderId));
		if(list!=null){
			for(Object[] obj:list){
				tempMap.put(obj[0].toString()+","+obj[1].toString(), Integer.parseInt(obj[2].toString()));
			}
		}
		return tempMap;
	}
	
	/***
	 *根据订单ids 获得可提货数量 
	 * 
	 */
	public Map<Integer,Map<String,Integer>> getCanLadingQuantitys(Set<Integer> purchaseOrderIds){
		Map<Integer,Map<String,Integer>> tempMap=Maps.newHashMap();
		String sql="SELECT a.`purchase_order_id`,a.`product_id`,a.color,a.`can_lading_quantity` FROM  psi_parts_delivery_check AS a WHERE a.`can_lading_quantity`>0 AND a.`purchase_order_id` in :p1 ";
		List<Object[]> list=this.psiPartsInventoryOutDao.findBySql(sql, new Parameter(purchaseOrderIds));
		if(list!=null){
			for(Object[] obj:list){
				Integer orderId = Integer.parseInt(obj[0].toString());
				Map<String,Integer> innerMap=null;
				if(tempMap.get(orderId)==null){
					 innerMap=Maps.newHashMap();
				}else{
					innerMap = tempMap.get(orderId);
				}
				innerMap.put(obj[1].toString()+","+obj[2].toString(), Integer.parseInt(obj[3].toString()));
				tempMap.put(orderId, innerMap);
			}
		}
		return tempMap;
	}
	
	public List<PsiPartsInventoryOut> findByOrderId(Integer purchaseOrderId){
			Page<PsiPartsInventoryOut> page  = new Page<PsiPartsInventoryOut>();
			page.setPageSize(10000);
			DetachedCriteria dc = psiPartsInventoryOutDao.createDetachedCriteria();
			dc.createAlias("this.orders", "order");
			dc.add(Restrictions.or(Restrictions.eq("order.purchaseOrderId",purchaseOrderId)));
			page.setOrderBy(" id desc");
		return psiPartsInventoryOutDao.find2(page, dc).getList();
	}
	
	
}
