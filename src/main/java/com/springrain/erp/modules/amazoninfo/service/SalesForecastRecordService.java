package com.springrain.erp.modules.amazoninfo.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.SalesForecastRecordDao;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecastRecord;

/**
 * 销量预测修改记录Service
 */
@Component
@Transactional(readOnly = true)
public class SalesForecastRecordService extends BaseService {

	@Autowired
	private SalesForecastRecordDao salesForecastRecordDao;
	
	public SalesForecastRecord get(Integer id) {
		return salesForecastRecordDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(SalesForecastRecord salesForecastRecord) {
		salesForecastRecordDao.save(salesForecastRecord);
	}
	
	@Transactional(readOnly = false)
	public void save(List<SalesForecastRecord> salesForecastRecords) {
		salesForecastRecordDao.save(salesForecastRecords);
	}

	public Page<SalesForecastRecord> find(Page<SalesForecastRecord> page,
			SalesForecastRecord salesForecastRecord, String month, String flag, List<String> colorNameList) {
		DetachedCriteria dc = salesForecastRecordDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(salesForecastRecord.getState())){
			dc.add(Restrictions.eq("state", salesForecastRecord.getState()));
		}
		if (StringUtils.isNotEmpty(salesForecastRecord.getCountry())){
			dc.add(Restrictions.eq("country", salesForecastRecord.getCountry()));
		}
		if (StringUtils.isNotEmpty(salesForecastRecord.getProductName())){
			dc.add(Restrictions.like("productName", "%" + salesForecastRecord.getProductName() + "%"));
		}
		if ("1".equals(flag) && "0".equals(salesForecastRecord.getState())) {
			if(colorNameList.size() == 0){
				dc.add(Restrictions.eq("state", "-1"));	//没有急需审批的产品
			} else{
				dc.add(Restrictions.in("productName", colorNameList));
			}
		}
		if (StringUtils.isNotEmpty(month)) {
			dc.add(Restrictions.eq("month", salesForecastRecord.getMonth()));
		}
		return salesForecastRecordDao.find(page, dc);
	}
	
	public SalesForecastRecord findSalesForecastRecord(String country, String productName, String month){
		DetachedCriteria dc = salesForecastRecordDao.createDetachedCriteria();
		dc.add(Restrictions.eq("state", "0"));
		if (StringUtils.isNotEmpty(country)) {
			dc.add(Restrictions.eq("country", country));
		}
		if (StringUtils.isNotEmpty(productName)) {
			dc.add(Restrictions.eq("productName", productName));
		}
		if (StringUtils.isNotEmpty(month)) {
			dc.add(Restrictions.eq("month", month));
		}
		
		List<SalesForecastRecord> list = salesForecastRecordDao.find(dc);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	//查询待审批数量
	public Integer countQuantity(String month){
		String sql ="SELECT * FROM `amazoninfo_sales_forecast_record` t WHERE t.`state`='0'AND t.`month`=:p1 ";
		List<Object[]> list = salesForecastRecordDao.findBySql(sql, new Parameter(month));
		if (list != null) {
			return list.size();
		}
		return 0;
	}
	
}
