package com.springrain.erp.modules.psi.service;


import java.util.List;
import java.util.Map;

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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.PsiInventoryLocationDao;
import com.springrain.erp.modules.psi.entity.PsiInventoryLocation;

/**
 * 库存库位管理Service
 */
@Component
@Transactional(readOnly = true)
public class PsiInventoryLocationService extends BaseService {
	
	@Autowired
	private PsiInventoryLocationDao 		psiInventoryLocationDao;
	
	public PsiInventoryLocation get(Integer id) {
		return psiInventoryLocationDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiInventoryLocation psiInventoryLocation) {
		psiInventoryLocationDao.save(psiInventoryLocation);
	}
	
	@Transactional(readOnly = false)
	public void save(List<PsiInventoryLocation> psiInventoryLocationList) {
		psiInventoryLocationDao.save(psiInventoryLocationList);
	}

	/**
	 * 根据sku、库位和sn码获取唯一对象
	 * @param sku
	 * @param locationId
	 * @param snCode
	 * @return
	 */
	public PsiInventoryLocation getByUnique(String sku, Integer locationId, String snCode) {
		DetachedCriteria dc = psiInventoryLocationDao.createDetachedCriteria();
		dc.add(Restrictions.eq("sku", sku));
		dc.add(Restrictions.eq("snCode", snCode));
		dc.createAlias("stockLocation", "location");
		dc.add(Restrictions.eq("location.id", locationId));
		List<PsiInventoryLocation> list = psiInventoryLocationDao.find(dc);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public Page<PsiInventoryLocation> find(Page<PsiInventoryLocation> page, PsiInventoryLocation psiInventoryLocation, Integer stockId) {
		if (stockId == null) {
			return page;
		}
		DetachedCriteria dc = psiInventoryLocationDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiInventoryLocation.getProductName())){
			dc.add(Restrictions.sqlRestriction(" (CONCAT(product_name,'_',color_code) like '%"+psiInventoryLocation.getProductName()+"%' " +
					" OR sku like '%"+psiInventoryLocation.getProductName()+"%')"));
		}
		if(StringUtils.isNotEmpty(psiInventoryLocation.getCountryCode())){
			dc.add(Restrictions.eq("countryCode",psiInventoryLocation.getCountryCode()));
		}
		dc.createAlias("stockLocation", "location");
		dc.createAlias("location.stockArea", "area");
		dc.createAlias("area.stock", "stock");
		dc.add(Restrictions.eq("stock.id", stockId));
		if(psiInventoryLocation.getStockLocation() != null && psiInventoryLocation.getStockLocation().getId()!=null){
			dc.add(Restrictions.eq("location.id", psiInventoryLocation.getStockLocation().getId()));
		}
		dc.add(Restrictions.or(Restrictions.gt("newQuantity", 0),
				Restrictions.gt("oldQuantity", 0),
				Restrictions.gt("brokenQuantity", 0),
				Restrictions.gt("sparesQuantity", 0),
				Restrictions.gt("offlineQuantity", 0),
				Restrictions.gt("renewQuantity", 0)));
		if(StringUtils.isEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("updateDate"));
		}
		return psiInventoryLocationDao.find(page, dc);
	}

    public Integer findByLocation(Integer id) {
        return psiInventoryLocationDao.findBySql("select *from psi_inventory_location where location_id=:p1",new Parameter(id)).size();
    }

	public Map<Integer, String> findAllLocation(Integer stockId) {
		Map<Integer, String> rsMap = Maps.newHashMap();
		String sql = "SELECT l.`id`,l.`name` FROM `psi_stock_location` l,`psi_stock_area` a, `psi_stock` s"+
				" WHERE l.`area_id`=a.`id` AND a.`stock_id`=s.`id` AND s.`id`=:p1";
		List<Object[]> list = psiInventoryLocationDao.findBySql(sql, new Parameter(stockId));
		for (Object[] obj : list) {
			rsMap.put(Integer.parseInt(obj[0].toString()), obj[1].toString());
		}
		return rsMap;
	}
	
}
