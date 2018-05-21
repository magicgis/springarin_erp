/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.Advertising;

/**
 * 广告报表DAO接口
 * @author Tim
 * @version 2015-03-03
 */
@Repository
public class AdvertisingDao extends BaseDao<Advertising> {
	
	
	public List<Advertising> findCount(DetachedCriteria detachedCriteria) {
		List<Advertising> rs = Lists.newArrayList();
		if(count(detachedCriteria)>0){
			Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
			ProjectionList projection = Projections.projectionList().add(
						Projections.sum("impressions").as("impressions"))
						.add(Projections.sum("clicks").as("clicks"))
						.add(Projections.sum("totalSpend").as("totalSpend"))
						.add(Projections.sum("sameSkuOrdersPlaced").as("sameSkuOrdersPlaced"))
						.add(Projections.sum("sameSkuOrderSales").as("sameSkuOrderSales"))
						.add(Projections.sum("otherSkuOrdersPlaced").as("otherSkuOrdersPlaced"))
						.add(Projections.sum("otherSkuOrderSales").as("otherSkuOrderSales"))
						.add(Projections.max("maxCpcBid").as("maxCpcBid"))
						.add(Projections.max("onePageBid").as("onePageBid"))
						.add(Projections.avg("conversion").as("conversion"))
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
				rs.add(new Advertising(objs[10].toString(),objs[11].toString(),objs[12].toString(),objs[13].toString(),
						Integer.parseInt(objs[0].toString()), Integer.parseInt(objs[1].toString()), Float.parseFloat(objs[2]==null?"0":objs[2].toString()), Float.parseFloat(objs[9]==null?"0":objs[9].toString())
						, Integer.parseInt(objs[3].toString()), Float.parseFloat(objs[4]==null?"0":objs[4].toString()), Integer.parseInt(objs[5].toString()), Float.parseFloat(objs[6]==null?"0":objs[6].toString()),
							Float.parseFloat(objs[7]==null?"0":objs[7].toString()), Float.parseFloat(objs[8]==null?"0":objs[8].toString()),objs[14].toString(),null,null,null));
			}
		}
		return rs;
	}
	
	public List<Advertising> findByDate(Advertising advertising,DetachedCriteria detachedCriteria) {
		List<Advertising> rs = Lists.newArrayList();
		if(count(detachedCriteria)>0){
			Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
			ProjectionList projection = Projections.projectionList().add(
						Projections.sum("impressions").as("impressions"))
						.add(Projections.sum("clicks").as("clicks"))
						.add(Projections.sum("totalSpend").as("totalSpend"))
						.add(Projections.sum("sameSkuOrdersPlaced").as("sameSkuOrdersPlaced"))
						.add(Projections.sum("sameSkuOrderSales").as("sameSkuOrderSales"))
						.add(Projections.sum("otherSkuOrdersPlaced").as("otherSkuOrdersPlaced"))
						.add(Projections.sum("otherSkuOrderSales").as("otherSkuOrderSales"))
						.add(Projections.max("maxCpcBid").as("maxCpcBid"))
						.add(Projections.max("onePageBid").as("onePageBid"))
						.add(Projections.avg("conversion").as("conversion"));
			if(StringUtils.isNotBlank(advertising.getKeyword())){
				projection.add(Projections.property("name"))
				.add(Projections.property("groupName"))
				.add(Projections.property("sku"))
				.add(Projections.property("keyword"))
				.add(Projections.property("dataDate"))
				.add(Projections.property("type"))
				.add(Projections.groupProperty("name"))
				.add(Projections.groupProperty("groupName"))
				.add(Projections.groupProperty("sku"))
				.add(Projections.groupProperty("keyword"))
				.add(Projections.groupProperty("dataDate"))
				.add(Projections.groupProperty("type"));
			}else if(StringUtils.isNotBlank(advertising.getGroupName())){
				projection.add(Projections.property("name"))
				.add(Projections.property("groupName"))
				.add(Projections.property("dataDate"))
				
				.add(Projections.groupProperty("name"))
				.add(Projections.groupProperty("groupName"))
				.add(Projections.groupProperty("dataDate"));
			}else if(StringUtils.isNotBlank(advertising.getName())){
				projection.add(Projections.property("name"))
				.add(Projections.property("dataDate"))
				.add(Projections.groupProperty("name"))
				.add(Projections.groupProperty("dataDate"));
			}
			criteria.addOrder(Order.desc("dataDate"));
			criteria.setProjection(projection);
			List<Object[]> list = criteria.list(); 
			for (Object[] objs : list) {
				if(StringUtils.isNotBlank(advertising.getKeyword())){
					Advertising advertising2 = new Advertising(objs[10].toString(),objs[11].toString(),objs[12].toString(),objs[13].toString(),
						Integer.parseInt(objs[0].toString()), Integer.parseInt(objs[1].toString()), Float.parseFloat(objs[2]==null?"0":objs[2].toString()), Float.parseFloat(objs[9]==null?"0":objs[9].toString())
						, Integer.parseInt(objs[3].toString()), Float.parseFloat(objs[4]==null?"0":objs[4].toString()), Integer.parseInt(objs[5].toString()), Float.parseFloat(objs[6]==null?"0":objs[6].toString()),
							Float.parseFloat(objs[7]==null?"0":objs[7].toString()), Float.parseFloat(objs[8]==null?"0":objs[8].toString()),objs[14].toString(),null,null,null);
					advertising2.setDataDate((Date)objs[14]);
					rs.add(advertising2);
				}else if(StringUtils.isNotBlank(advertising.getGroupName())){
					Advertising advertising2 = new Advertising(objs[10].toString(),objs[11].toString(),null,null,
							Integer.parseInt(objs[0].toString()), Integer.parseInt(objs[1].toString()), Float.parseFloat(objs[2]==null?"0":objs[2].toString()), Float.parseFloat(objs[9]==null?"0":objs[9].toString())
							, Integer.parseInt(objs[3].toString()), Float.parseFloat(objs[4]==null?"0":objs[4].toString()), Integer.parseInt(objs[5].toString()), Float.parseFloat(objs[6]==null?"0":objs[6].toString()),
								Float.parseFloat(objs[7]==null?"0":objs[7].toString()), Float.parseFloat(objs[8]==null?"0":objs[8].toString()),null,null,null,null);
					advertising2.setDataDate((Date)objs[12]);
					rs.add(advertising2);
				}else if(StringUtils.isNotBlank(advertising.getName())){
					Advertising advertising2 = new Advertising(objs[10].toString(),null,null,null,
							Integer.parseInt(objs[0].toString()), Integer.parseInt(objs[1].toString()), Float.parseFloat(objs[2]==null?"0":objs[2].toString()), Float.parseFloat(objs[9]==null?"0":objs[9].toString())
							, Integer.parseInt(objs[3].toString()), Float.parseFloat(objs[4]==null?"0":objs[4].toString()), Integer.parseInt(objs[5].toString()), Float.parseFloat(objs[6]==null?"0":objs[6].toString()),
								Float.parseFloat(objs[7]==null?"0":objs[7].toString()), Float.parseFloat(objs[8]==null?"0":objs[8].toString()),null,null,null,null);
					advertising2.setDataDate((Date)objs[11]);
					rs.add(advertising2);
				}
			}
		}
		return rs;
	}
	
	
	
}
