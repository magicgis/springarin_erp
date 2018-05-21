/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.dao.order;

import java.text.ParseException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.type.FloatType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;

/**
 * 亚马逊产品DAO接口
 * @author tim
 * @version 2014-06-26
 */
@Repository
public class AmazonOrderDao extends BaseDao<AmazonOrder> {
	
	
	public List<Object[]> findExp(DetachedCriteria detachedCriteria) throws ParseException{
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("amazonOrderId"))
				.add(Projections.sqlProjection("(SELECT  CONCAT_WS(',',NAME,address_line1,address_line2,address_line3,county,city,country_code,postal_code)  FROM amazoninfo_address   WHERE id = shipping_address) AS address", new String[]{"address"}, new Type[]{StringType.INSTANCE}))
				.add(Projections.property("purchaseDate"))
				.add(Projections.property("orderTotal"))
				.add(Projections.sqlProjection("(SELECT  country_code  FROM amazoninfo_address   WHERE id = shipping_address) AS address1", new String[]{"address1"}, new Type[]{StringType.INSTANCE}))
				.add(Projections.sqlProjection("IF (order_channel IS NULL ,'yes' , 'no') as orderChannel ",  new String[]{"orderChannel"}, new Type[]{StringType.INSTANCE}))
				.add(Projections.property("salesChannel"));	
		criteria.setProjection(projection);
		List<Object[]> list  = criteria.list();
		return list;
	}
	
	
}
