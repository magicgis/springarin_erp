package com.springrain.erp.modules.psi.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.modules.psi.dao.PsiProductMoldFeeDao;
import com.springrain.erp.modules.psi.entity.PsiProductMoldFee;

/**
 * 供应商产品模具费Service
 */
@Component
@Transactional(readOnly = true)
public class PsiProductMoldFeeService extends BaseService {
	
	@Autowired
	private PsiProductMoldFeeDao psiProductMoldFeeDao;

	public PsiProductMoldFee get(Integer id) {
		return psiProductMoldFeeDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiProductMoldFee psiProductMoldFee) {
		psiProductMoldFeeDao.save(psiProductMoldFee);
	}

	public List<PsiProductMoldFee> find() {
		DetachedCriteria dc = psiProductMoldFeeDao.createDetachedCriteria();
		dc.addOrder(Order.desc("id"));
		return psiProductMoldFeeDao.find(dc);
	}

	/**
	 * 需要算入成本的产品模具费(有模具费且销量未达到5000)
	 * @return Map<colorName, 单个分摊的模具费>
	 */
	public Map<String, Float> findMoldFeeForBuyCost() {
		Map<String, Float> rs = Maps.newHashMap();
		String sql = "SELECT t.`product_name`,t.`mold_fee` FROM `psi_product_mold_fee` t WHERE t.`sale_flag`='0'";
		List<Object[]> list = psiProductMoldFeeDao.findBySql(sql);
		for (Object[] obj : list) {
			String productNames = obj[0].toString();
			Float moldFee = Float.parseFloat(obj[1].toString());
			for (String productName : productNames.split(",")) {
				rs.put(productName, moldFee/5000);
			}
		}
		return rs;
	}
	
	/**
	 * 根据供应商统计未返还的模具费(下单数达到返还数量但是未返还完毕)
	 * @return List<Float>
	 */
	public List<Float> findForReturn(Integer supplierId) {
		List<Float> rs = Lists.newArrayList();
		String sql = "SELECT SUM(t.`mold_fee`), SUM(CASE WHEN t.`purchase_flag`='2' AND t.`return_flag`='0' THEN t.`mold_fee` ELSE 0 END)"+
				" FROM `psi_product_mold_fee` t WHERE t.`supplier_id`=:p1";
		List<Object[]> list = psiProductMoldFeeDao.findBySql(sql, new Parameter(supplierId));
		for (Object[] obj : list) {
			Float totalMoldFee = obj[0]==null?0:Float.parseFloat(obj[0].toString());
			Float returnMoldFee = obj[1]==null?0:Float.parseFloat(obj[1].toString());
			rs.add(totalMoldFee);	//总的模具费
			rs.add(returnMoldFee);	//达到返还条件的模具费
		}
		return rs;
	}

	/**
	 * 更新销量和下单量状态标记(用于定时更新)
	 * @return 
	 */
	@Transactional(readOnly = false)
	public void updateFlag() {
		//统计销量标记
		String sql = "SELECT t.`id`,t.`product_name` FROM `psi_product_mold_fee` t WHERE t.`sale_flag`='0'";
		String salesSql = "SELECT SUM(t.`sales_volume`) FROM `amazoninfo_report_month_type` t WHERE t.`product_name` IN :p1";
		List<Object[]> list = psiProductMoldFeeDao.findBySql(sql);
		for (Object[] obj : list) {
			List<String> nameList = Lists.newArrayList();
			String productNames = obj[1].toString();
			for (String productName : productNames.split(",")) {
				nameList.add(productName);
			}
			List<Object> rs = psiProductMoldFeeDao.findBySql(salesSql, new Parameter(nameList));
			if (rs != null && rs.size() > 0 && rs.get(0) != null) {
				Integer salesVolume = Integer.parseInt(rs.get(0).toString());
				if (salesVolume >= 5000) {
					Integer id = Integer.parseInt(obj[0].toString());
					String updateSql = "UPDATE `psi_product_mold_fee` t SET t.`sale_flag`='1' WHERE t.`id`=:p1";
					psiProductMoldFeeDao.updateBySql(updateSql, new Parameter(id));
				}
			}
		}
		//统计下单量标记
		sql = "SELECT t.`id`,t.`product_name`,t.`return_flag`,t.`return_num` FROM `psi_product_mold_fee` t WHERE t.`purchase_flag` IN('0','1')";
		list = psiProductMoldFeeDao.findBySql(sql);
		String orderSql = "SELECT SUM(i.`quantity_ordered`) FROM `lc_psi_purchase_order` t, `lc_psi_purchase_order_item` i"+
				" WHERE i.`purchase_order_id`=t.`id` AND t.`order_sta` IN ('1','2','3','4','5') AND i.`del_flag`='0' "+
				" AND CASE WHEN i.`color_code`='' THEN i.`product_name` ELSE CONCAT(i.`product_name`,'_',i.`color_code`) END IN :p1";	//统计下单量
		for (Object[] obj : list) {
			List<String> nameList = Lists.newArrayList();
			Integer id = Integer.parseInt(obj[0].toString());
			String productNames = obj[1].toString();
			String returnFlag = obj[2].toString();	//0:返还  1：不返还
			Integer returnNum = obj[3]==null?0:Integer.parseInt(obj[3].toString());
			for (String productName : productNames.split(",")) {
				nameList.add(productName);
			}
			List<Object> rs = psiProductMoldFeeDao.findBySql(orderSql, new Parameter(nameList));
			if (rs != null && rs.size() > 0 && rs.get(0) != null) {
				String newFlag = "0";
				Integer orderVolume = Integer.parseInt(rs.get(0).toString());
				if ("1".equals(returnFlag) && orderVolume >= 5000) {
					newFlag = "2";	//不返还的达到5000直接完结
				} else if ("0".equals(returnFlag) && orderVolume >= returnNum) {
					newFlag = "2";	//可以返还的达到返还数直接完结
				} else if ("0".equals(returnFlag) && orderVolume >= 5000) {
					newFlag = "1";	//可以返还的达到5000更新标记
				}
				if (!"0".equals(newFlag)) {
					String updateSql = "UPDATE `psi_product_mold_fee` t SET t.`purchase_flag`=:p1 WHERE t.`id`=:p2";
					psiProductMoldFeeDao.updateBySql(updateSql, new Parameter(newFlag, id));
				}
			}
		}
	}

	public PsiProductMoldFee findMoldFeeByModel(String name) {
		DetachedCriteria dc = psiProductMoldFeeDao.createDetachedCriteria();
		dc.add(Restrictions.like("productName", "%" + name + "%"));
		List<PsiProductMoldFee> list = psiProductMoldFeeDao.find(dc);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 产品淘汰时利润统计模具费(销量达到5000的忽略,同一条模具费记录中有产品未淘汰的忽略)
	 * @param productName
	 */
	@Transactional(readOnly = false)
	public void updateProfit(String productName) {
		logger.info(productName+"淘汰，模具费统计");
		//统计销量标记
		String sql = "SELECT t.`id`,t.`product_name`,t.`mold_fee` FROM `psi_product_mold_fee` t WHERE t.`product_name` LIKE :p1 AND t.`sale_flag`='0'";
		List<Object[]> list = psiProductMoldFeeDao.findBySql(sql, new Parameter("%"+productName+"%"));
		if (list == null || list.size() == 0) {
			return;	//没有模具费或者销量已达到5000,忽略
		}
		List<String> nameList = Lists.newArrayList();
		Integer id = 0;
		Float moldFee = 0f;
		for (Object[] obj : list) {
			id = Integer.parseInt(obj[0].toString());
			String productNames = obj[1].toString();
			moldFee = Float.parseFloat(obj[2].toString());
			for (String name : productNames.split(",")) {
				nameList.add(name);
			}
		}
		//看模具费记录中是否所有产品都淘汰了
		sql = "SELECT * FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' AND t.`is_sale`!='4'"+
				" AND CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END IN :p1";
		list = psiProductMoldFeeDao.findBySql(sql, new Parameter(nameList));
		if (list != null && list.size() > 0) {
			return;	//还有在售的,忽略
		}
		//剩余未统计利润的模具费算到当月的记录中
		DateFormat format = new SimpleDateFormat("yyyyMM");
		String currMonth = format.format(new Date());	//当前月
		//查询当月之前所有扣除的模具费
		sql = "SELECT SUM(IFNULL(t.`mold_fee`,0)) FROM `amazoninfo_report_month_type` t WHERE t.`month`<:p1 AND t.`product_name` IN :p2";
		List<Object> saleList = psiProductMoldFeeDao.findBySql(sql, new Parameter(currMonth, nameList));
		float profitMoldFee = 0f;//已经扣除的模具费(欧元)
		if (saleList != null && saleList.size()>0 && saleList.get(0)!=null) {
			profitMoldFee = Float.parseFloat(saleList.get(0).toString());
		}
		//算出剩余未扣除的模具费(换成欧元)
		moldFee = moldFee * MathUtils.getRate("CNY", "EUR", null) - profitMoldFee;
		//统计当月销量
		sql = "SELECT SUM(IFNULL(t.`sales_volume`,0)) FROM `amazoninfo_report_month_type` t WHERE t.`month`=:p1 AND t.`product_name` IN :p2";
		saleList = psiProductMoldFeeDao.findBySql(sql, new Parameter(currMonth, nameList));
		Integer salesVolume = 0;//当月销量
		if (saleList != null && saleList.size()>0 && saleList.get(0)!=null) {
			salesVolume = Integer.parseInt(saleList.get(0).toString());
		}
		if (salesVolume > 0) {
			String updateSql = "UPDATE `amazoninfo_sale_profit` t SET t.`mold_fee`=t.`sales_volume`*:p1 WHERE t.`day` LIKE :p2 AND t.`product_name` IN :p3";
			psiProductMoldFeeDao.updateBySql(updateSql, new Parameter(moldFee/salesVolume, currMonth+"%", nameList));
		}
		//更新模具费销量标记
		String updateSql = "UPDATE `psi_product_mold_fee` t SET t.`sale_flag`='1' WHERE t.`id`=:p1";
		psiProductMoldFeeDao.updateBySql(updateSql, new Parameter(id));
	}
	
}
