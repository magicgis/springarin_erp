package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.MfnInventoryFeedDao;
import com.springrain.erp.modules.amazoninfo.entity.MfnInventoryFeed;
import com.springrain.erp.modules.amazoninfo.entity.MfnItem;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 自发货库存管理Service
 * @author Tim
 * @version 2015-07-09
 */
@Component
@Transactional(readOnly = true)
public class MfnInventoryFeedService extends BaseService {

	@Autowired
	private MfnInventoryFeedDao mfnInventoryDao;
	
	public MfnInventoryFeed get(String id) {
		return mfnInventoryDao.get(id);
	}
	
	public Page<MfnInventoryFeed> find(Page<MfnInventoryFeed> page, MfnInventoryFeed mfnInventory) {
		DetachedCriteria dc = mfnInventoryDao.createDetachedCriteria();
		Date date =  mfnInventory.getEndDate();
		date.setHours(23);
		date.setMinutes(59);
		dc.add(Restrictions.and(Restrictions.ge("requestDate",mfnInventory.getRequestDate()),Restrictions.le("requestDate",date)));
		
		String country = mfnInventory.getCountry();
		if (StringUtils.isNotEmpty(country)){
			dc.add(Restrictions.eq("country", country));
		}
		if (mfnInventory.getCreateBy()!=null && StringUtils.isNotEmpty(mfnInventory.getCreateBy().getId())){
			dc.add(Restrictions.or(Restrictions.eq("createBy", mfnInventory.getCreateBy()),Restrictions.eq("createBy",UserUtils.getUserById("1"))));
		}
		if(StringUtils.isNotEmpty(mfnInventory.getResult())){
			dc.createAlias("this.items", "item");
			dc.add(Restrictions.like("item.sku", "%"+mfnInventory.getResult()+"%"));
		}
		return mfnInventoryDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(MfnInventoryFeed mfnInventory) {
		mfnInventoryDao.save(mfnInventory);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		mfnInventoryDao.deleteById(id);
	}
	
	@Transactional(readOnly = false)
	public void updateProductMfn(MfnInventoryFeed mfnInventory) {
		String sql = "UPDATE amazoninfo_product2 a  SET a.`quantity` = :p1  WHERE a.`country` = :p2 AND a.`sku` = :p3";
		for (MfnItem mfnItem : mfnInventory.getItems()) {
			mfnInventoryDao.updateBySql(sql, new Parameter(mfnItem.getQuantity(),mfnInventory.getCountry(),mfnItem.getSku()));
		}
	}
	
}
