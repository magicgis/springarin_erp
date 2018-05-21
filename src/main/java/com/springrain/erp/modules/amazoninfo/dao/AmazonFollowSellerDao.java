/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.dao;

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
import com.springrain.erp.modules.amazoninfo.entity.AmazonFollowSeller;

@Repository
public class AmazonFollowSellerDao extends BaseDao<AmazonFollowSeller> {
	
	/**
	 * 使用检索标准对象分页查询
	 * @param page
	 * @param detachedCriteria
	 * @param resultTransformer
	 * @return
	 */
	public List<AmazonFollowSeller> findGroupByProduct(DetachedCriteria detachedCriteria) {
		
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		
		ProjectionList projection =Projections.projectionList()
				.add(Projections.groupProperty("country"))
				.add(Projections.groupProperty("sellerName"))
				.add(Projections.groupProperty("productName"))
				.add(Projections.property("a"))
				.add(Projections.sum("quantity").as("quantity"))
				.add(Projections.property("asin"));
		criteria.setProjection(projection);
		List<Object[]> list = criteria.list();
		List<AmazonFollowSeller> listdata = Lists.newArrayList();
		for(Object[] objs:list){
			listdata.add(new AmazonFollowSeller(null, Integer.parseInt(objs[4].toString()),objs[1].toString(),objs[3].toString(), objs[0].toString(), null, objs[5].toString(), objs[2].toString()));
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
			criteria.setProjection(Projections.countDistinct("productName"));
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
}