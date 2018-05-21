/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.dao;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.NullPrecedence;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.FloatType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PurchaseOrder;

/**
 * 采购订单DAO接口
 * @author Michael
 * @version 2014-10-29
 */
@Repository
public class PurchaseOrderDao extends BaseDao<PurchaseOrder> {
	@Autowired
	private PurchasePaymentDao payDao;
	
	public void  updateOrderSta(Integer orderId,String sta){
		Parameter parameter =new Parameter(orderId,sta);
		this.updateBySql("update psi_purchase_order set order_sta=:p2 where id = :p1 ", parameter);
	}
	
	public void  updateOrderStaAndFinishedDate(Integer orderId,String sta,Date finishedDate){
		Parameter parameter =new Parameter(orderId,sta,finishedDate);
		this.updateBySql("update psi_purchase_order set order_sta=:p2 , receive_finished_date=:p3 where id = :p1 ", parameter);
	}
	
	public void  updateOrderPartsOrderSta(Integer orderId,String sta){
		Parameter parameter =new Parameter(orderId,sta);
		this.updateBySql("update psi_purchase_order set to_parts_order=:p2  where id = :p1 ", parameter);
	}
	
	public void deleteOrderItem(Integer itemId) {
		Parameter parameter =new Parameter(itemId);
		this.updateBySql("update psi_purchase_order_item set del_flag='1' where id =:p1", parameter);
	}

	
	/**
	 * 使用检索标准对象分页查询
	 * @param page
	 * @param detachedCriteria
	 * @param resultTransformer
	 * @return
	 */
	public List<PurchaseOrder> exp(DetachedCriteria detachedCriteria, ResultTransformer resultTransformer ) {
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		//分组拍重复
		criteria.setProjection(Projections.distinct(Projections.property("id")));
		List<Integer> ids = criteria.list();
		DetachedCriteria dc = this.createDetachedCriteria();
		dc.add(Restrictions.in("id",ids));
		criteria = dc.getExecutableCriteria(getSession());
		return criteria.list();
	}
	
	
	@SuppressWarnings("rawtypes")
	public long countReconciliation(DetachedCriteria detachedCriteria) {
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		long totalCount = 0;
		try {
			Field field = CriteriaImpl.class.getDeclaredField("orderEntries");
			field.setAccessible(true);
			List orderEntrys = (List)field.get(criteria);
			field.set(criteria, new ArrayList());
			criteria.setProjection(Projections.countDistinct("supplier"));
			totalCount = Long.valueOf(criteria.uniqueResult().toString());
			criteria.setProjection(null);
			field.set(criteria, orderEntrys);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return totalCount;
	}
	
	
	
	public Page<PurchaseOrder> findReconciliation(Page<PurchaseOrder> page,DetachedCriteria detachedCriteria, PurchaseOrder purchaseOrder) {
		Map<Integer,BigDecimal> supplierMap = payDao.getPartsPayment();
		ProjectionList projection=null;
		List<Object[]> list=null;
		if (!page.isDisabled() && !page.isNotCount()){
			page.setCount(countReconciliation(detachedCriteria));
			if (page.getCount() < 1) {
				return page;
			}
		}
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		if (!page.isDisabled()){
	        criteria.setFirstResult(page.getFirstResult());
	        criteria.setMaxResults(page.getMaxResults()); 
		}
		if (StringUtils.isNotBlank(page.getOrderBy())){
			for (String order : StringUtils.split(page.getOrderBy(), ",")){
				String[] o = StringUtils.split(order, " ");
				if (o.length==1){
					criteria.addOrder(Order.asc(o[0]).nulls(NullPrecedence.FIRST));
				}else if (o.length==2){
					if ("DESC".equals(o[1].toUpperCase())){
						criteria.addOrder(Order.desc(o[0]).nulls(NullPrecedence.FIRST));
					}else{
						criteria.addOrder(Order.asc(o[0]).nulls(NullPrecedence.FIRST));
					}
				}
			}
		}
		projection =Projections.projectionList().add(Projections.property("supplier"))
				.add(Projections.sqlProjection(" SUM( CASE WHEN currency_type='CNY' THEN order_total/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE order_total END  ) AS orderTotal," +
						"SUM( CASE WHEN currency_type='CNY' THEN payment_amount/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE payment_amount END  ) AS paymentAmount ,"+
						"SUM( CASE WHEN currency_type='CNY' THEN deposit_amount/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE deposit_amount END  ) AS depositAmount "+
						"", new String[]{"orderTotal","paymentAmount","depositAmount"}, new Type[]{FloatType.INSTANCE,FloatType.INSTANCE,FloatType.INSTANCE}))
				.add(Projections.groupProperty("supplier"));
		criteria.setProjection(projection);
		list = criteria.list();
		List<PurchaseOrder> orders = Lists.newArrayList();
		for(Object[] objs:list){
			PurchaseOrder order = new PurchaseOrder();
			PsiSupplier supplier=(PsiSupplier)objs[0];
			order.setSupplier(supplier);
			order.setTotalAmount(new BigDecimal(objs[1].toString()));
			//order.setPaymentAmount(Float.valueOf(objs[2].toString())+psiLadingBillDao.getFinalPayment(supplier.getId())+payDao.getPartsPayment(supplier.getId()));
			order.setPaymentAmount(new BigDecimal(objs[2].toString()).add(supplierMap.get(supplier.getId())!=null?supplierMap.get(supplier.getId()):BigDecimal.ZERO));
			order.setDepositAmount(new BigDecimal(objs[3].toString()));
			orders.add(order);
		}
		page.setList(orders);
		return page;
	}
	
	  public Page<Object[]> findReconciliation2(Page<Object[]> page,PurchaseOrder purchaseOrder) {
		String sqlString="SELECT r.supplier_id,p.nikename,SUM( CASE WHEN r.currency_type='CNY' THEN order_total/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE order_total END  ) AS totalAmount,"+
	            " SUM( CASE WHEN r.currency_type='CNY' THEN payment_amount/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE payment_amount END  ) AS paymentAmount , "+
	            " SUM( CASE WHEN r.currency_type='CNY' THEN c.payment/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE c.payment END  ) AS monthAmount, "+
	            " SUM( CASE WHEN r.currency_type='CNY' THEN (order_total*p.deposit/100-deposit_amount)/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE (order_total*p.deposit/100-deposit_amount) END ) As depositAmount "+
	            " ,SUM( CASE WHEN r.currency_type='CNY' THEN b.unPayAmount/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE b.unPayAmount END  ) As unPayAmount"+
				" FROM psi_purchase_order r JOIN psi_supplier p ON r.`supplier_id`=p.id  " +
	           // " LEFT JOIN psi_lading_bill b ON b.`supplier_id`=r.`supplier_id` AND b.bill_sta='1' AND b.total_amount>b.total_payment_amount "+
				" LEFT JOIN (select b.`supplier_id`,sum(b.total_amount-b.total_payment_amount) unPayAmount from psi_lading_bill b where  b.bill_sta='1' AND b.total_amount>b.total_payment_amount group by b.`supplier_id`) b ON b.`supplier_id`=r.`supplier_id` "+
				" LEFT JOIN  (SELECT SUM(( b.`quantity_ordered` - b.`quantity_received`) * b.`item_price`) * ((100- c.`deposit`) / 100) payment ,a.supplier_id supplier_id " +
				" FROM psi_purchase_order AS a,psi_purchase_order_item AS b,psi_supplier AS c " +
				" WHERE a.`id` = b.`purchase_order_id` AND a.`supplier_id` = c.`id`  AND a.`order_sta` IN ('2', '3') AND b.`delivery_date` <= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+
				"' GROUP BY a.supplier_id) c  ON r.`supplier_id`=c.supplier_id GROUP BY r.supplier_id,p.nikename";
		  return findBySql(page,sqlString);
		
	 }
}
