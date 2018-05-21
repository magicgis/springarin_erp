/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.ForecastOrderItemDao;
import com.springrain.erp.modules.psi.entity.ForecastOrderItem;
/**
 * 预测订单Service
 * @author Michael  
 * @version 2016-2-26
 */
@Component
@Transactional(readOnly = true)
public class ForecastOrderItemService extends BaseService {
	@Autowired
	private ForecastOrderItemDao    	forecastOrderItemDao;
	
	public List<ForecastOrderItem> find(Integer forecastOrderId) {
			DetachedCriteria dc = forecastOrderItemDao.createDetachedCriteria();
			dc.add(Restrictions.eq("forecastOrder.id",forecastOrderId));
			dc.addOrder(Order.asc("productName"));
		return forecastOrderItemDao.find(dc);
	}
	
	public ForecastOrderItem getItemInfo(Integer itemId){
		return this.forecastOrderItemDao.get(itemId);
	}
	
	
}
