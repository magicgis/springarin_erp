/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.modules.amazoninfo.entity.AdvertisingByWeek;

/**
 * 广告报表DAO接口
 * @author Tim
 * @version 2016-04-27
 */
@Repository
public class AdvertisingByWeekDao extends BaseDao<AdvertisingByWeek> {
	
	
	public List<AdvertisingByWeek> findCount(DetachedCriteria detachedCriteria) {
		List<AdvertisingByWeek> rs = Lists.newArrayList();
		if(count(detachedCriteria)>0){
			Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
			ProjectionList projection = Projections.projectionList().add(
						Projections.sum("impressions").as("impressions"))
						.add(Projections.sum("clicks").as("clicks"))
						.add(Projections.sum("totalSpend").as("totalSpend"))
						.add(Projections.sum("weekSameSkuUnitsOrdered").as("weekSameSkuUnitsOrdered"))
						.add(Projections.sum("weekSameSkuUnitsSales").as("weekSameSkuUnitsSales"))
						.add(Projections.sum("weekSameSkuUnitsLirun").as("weekSameSkuUnitsLirun"))
						
						.add(Projections.sum("weekOtherSkuUnitsOrdered").as("weekOtherSkuUnitsOrdered"))
						.add(Projections.sum("weekOtherSkuUnitsSales").as("weekOtherSkuUnitsSales"))
						.add(Projections.sum("weekOtherSkuUnitsLirun").as("weekOtherSkuUnitsLirun"))
						
						.add(Projections.sum("weekParentSkuUnitsOrdered").as("weekParentSkuUnitsOrdered"))
						.add(Projections.sum("weekParentSkuUnitsSales").as("weekParentSkuUnitsSales"))
						.add(Projections.sum("weekParentSkuUnitsLirun").as("weekParentSkuUnitsLirun"))
						
						.add(Projections.property("name"))
						.add(Projections.property("groupName"))
						.add(Projections.property("sku"))
						.add(Projections.property("keyword"))
						.add(Projections.property("type"))
						
						.add(Projections.groupProperty("name"))
						.add(Projections.groupProperty("groupName"))
						.add(Projections.groupProperty("sku"))
						.add(Projections.groupProperty("keyword"))
						.add(Projections.groupProperty("type"));
			criteria.setProjection(projection);
			List<Object[]> list = criteria.list(); 
			for (Object[] objs : list) {
				rs.add(new AdvertisingByWeek(objs[12].toString(),objs[13].toString(),objs[14].toString(),
						objs[15].toString(),objs[16].toString(),Integer.parseInt(objs[0].toString()),
						Integer.parseInt(objs[1].toString()),Float.parseFloat(objs[2].toString()),
						Integer.parseInt(objs[3].toString()),Float.parseFloat(objs[4].toString()),Float.parseFloat(objs[5].toString()),
						Integer.parseInt(objs[6].toString()),Float.parseFloat(objs[7].toString()),Float.parseFloat(objs[8].toString()),
						Integer.parseInt(objs[9].toString()),Float.parseFloat(objs[10].toString()),Float.parseFloat(objs[11].toString())));
			}
		}
		return rs;
	}
}
