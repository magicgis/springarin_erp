/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.internal.CriteriaImpl;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.Stock;

/**
 * 采购付款明细DAO接口
 * @author Michael
 * @version 2014-12-24
 */
@Repository
public class PsiInventoryDao extends BaseDao<PsiInventory> {
	/**
	 * 使用检索标准对象分页查询
	 * @param page
	 * @param detachedCriteria
	 * @param resultTransformer
	 * @return
	 */
	public Page<PsiInventory> findGroupByProduct(Page<PsiInventory> page, DetachedCriteria detachedCriteria) {
		// get count
		if (!page.isDisabled() && !page.isNotCount()){
			page.setCount(countByProduct(detachedCriteria));
			if (page.getCount() < 1) {
				return page;
			}
		}
		
		
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		
		// set page
		if (!page.isDisabled()){
	        criteria.setFirstResult(page.getFirstResult());
	        criteria.setMaxResults(page.getMaxResults()); 
		}
		
		// order by
		if (StringUtils.isNotBlank(page.getOrderBy())){
			for (String order : StringUtils.split(page.getOrderBy(), ",")){
				String[] o = StringUtils.split(order, " ");
				if (o.length==1){
					criteria.addOrder(Order.asc(o[0]));
				}else if (o.length==2){
					if ("DESC".equals(o[1].toUpperCase())){
						criteria.addOrder(Order.desc(o[0]));
					}else{
						criteria.addOrder(Order.asc(o[0]));
					}
				}
			}
		}
		ProjectionList projection =Projections.projectionList()
				.add(Projections.groupProperty("productId"))
				.add(Projections.groupProperty("colorCode"))
				.add(Projections.property("productName"))
				.add(Projections.alias(Projections.sum("newQuantity"),"newQuantity"))
				.add(Projections.sum("oldQuantity").as("oldQuantity"))
				.add(Projections.sum("brokenQuantity").as("brokenQuantity"))
				.add(Projections.sum("renewQuantity").as("renewQuantity"))
				.add(Projections.sum("sparesQuantity").as("sparesQuantity"))
				.add(Projections.sum("offlineQuantity").as("offlineQuantity"))
				.add(Projections.property("warehouse.id"));
		if (StringUtils.isEmpty(page.getOrderBy())){
			projection.add(Projections.max("updateDate").as("updateDate"));
		}
		criteria.setProjection(projection);
		List<Object[]> list = criteria.list();
		List<PsiInventory> listdata = Lists.newArrayList();
		for(Object[] objs:list){
			//Stock stock=(Stock)objs[7];
			listdata.add(new PsiInventory(Integer.parseInt(objs[0].toString()), objs[2].toString(), Integer.parseInt(objs[3].toString()),
					Integer.parseInt(objs[4].toString()), Integer.parseInt(objs[5].toString()), Integer.parseInt(objs[6].toString()),
					new Stock((Integer)objs[9]),null,objs[1].toString(),Integer.parseInt(objs[7].toString()),Integer.parseInt(objs[8].toString())));
		}
		page.setList(listdata);
		return page;
	}
	
	
	/***
	 *
	 */
	public List<PsiInventory> findGroupByCountryAndColor(DetachedCriteria detachedCriteria) {
		
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		ProjectionList projection =Projections.projectionList()
				.add(Projections.groupProperty("countryCode"))
				//.add(Projections.groupProperty("colorCode"))
				.add(Projections.property("colorCode"))
				.add(Projections.property("productId"))
				.add(Projections.property("productName"))
				.add(Projections.sum("newQuantity").as("newQuantity"))
				.add(Projections.sum("oldQuantity").as("oldQuantity"))
				.add(Projections.sum("brokenQuantity").as("brokenQuantity"))
				.add(Projections.sum("renewQuantity").as("renewQuantity"))
				.add(Projections.sum("sparesQuantity").as("sparesQuantity"))
				.add(Projections.sum("offlineQuantity").as("offlineQuantity"))
				.add(Projections.property("warehouse.id"))
				.add(Projections.max("updateDate").as("updateDate"))
		        .add(Projections.property("remark"));
		criteria.setProjection(projection);
		List<Object[]> list = criteria.list();
		List<PsiInventory> listdata = Lists.newArrayList();
		for(Object[] objs:list){
			//Stock stock=(Stock)objs[8];
			PsiInventory inventory=new PsiInventory(Integer.parseInt(objs[2].toString()), objs[3].toString(), Integer.parseInt(objs[4].toString()),
					Integer.parseInt(objs[5].toString()), Integer.parseInt(objs[6].toString()), Integer.parseInt(objs[7].toString()),
					new Stock((Integer)objs[10]),objs[0].toString(),objs[1].toString(),Integer.parseInt(objs[8].toString()),Integer.parseInt(objs[9].toString()));
			inventory.setRemark(objs[12]==null?"":objs[12].toString());
			listdata.add(inventory);
		}
		return listdata;
	}
	
	
	@SuppressWarnings("rawtypes")
	public long countByProduct(DetachedCriteria detachedCriteria) {
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		long totalCount = 0;
		try {
			// Get orders
			Field field = CriteriaImpl.class.getDeclaredField("orderEntries");
			field.setAccessible(true);
			List orderEntrys = (List)field.get(criteria);
			// Remove orders
			field.set(criteria, new ArrayList());
			// Get count
			criteria.setProjection(Projections.countDistinct("productId"));
			totalCount = Long.valueOf(criteria.uniqueResult().toString());
			// Clean count
			criteria.setProjection(null);
			// Restore orders
			field.set(criteria, orderEntrys);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return totalCount;
	}
	
	/**
	 * 使用检索标准对象分页查询
	 * @param page
	 * @param detachedCriteria
	 * @param resultTransformer
	 * @return
	 */
	public PsiInventory findInventorySum(DetachedCriteria detachedCriteria) {
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		ProjectionList projection =Projections.projectionList()
				.add(Projections.property("productId"))
				.add(Projections.property("productName"))
				.add(Projections.sum("newQuantity").as("newQuantity"))
				.add(Projections.sum("oldQuantity").as("oldQuantity"))
				.add(Projections.sum("brokenQuantity").as("brokenQuantity"))
				.add(Projections.sum("renewQuantity").as("renewQuantity"))
				.add(Projections.sum("sparesQuantity").as("sparesQuantity"))
				.add(Projections.sum("offlineQuantity").as("offlineQuantity"))
				.add(Projections.property("warehouse"));
		criteria.setProjection(projection);
		List<Object[]> list = criteria.list();
		List<PsiInventory> listdata = Lists.newArrayList();
		for(Object[] objs:list){
			if(objs[0]==null){
				return null;
			}
			Stock stock=(Stock)objs[8];
			listdata.add(new PsiInventory(Integer.parseInt(objs[0].toString()), objs[1].toString(), Integer.parseInt(objs[2].toString()),
					Integer.parseInt(objs[3].toString()), Integer.parseInt(objs[4].toString()), Integer.parseInt(objs[5].toString()),
					stock,null,null,Integer.parseInt(objs[6].toString()),Integer.parseInt(objs[7].toString())));
		}
		if(listdata.size()==1){
			return listdata.get(0);
		}else{
			return null;
		}
		
	}
	
	
}
