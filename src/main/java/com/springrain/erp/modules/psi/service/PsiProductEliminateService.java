package com.springrain.erp.modules.psi.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.PsiProductDao;
import com.springrain.erp.modules.psi.dao.PsiProductEliminateDao;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.sys.utils.DictUtils;

@Component
@Transactional(readOnly = true)
public class PsiProductEliminateService extends BaseService{
	
	@Autowired
	private PsiProductEliminateDao productEliminateDao;
	
	@Autowired
	private PsiProductDao productDao;
	
	public PsiProductEliminate get(Integer id){
		return productEliminateDao.get(id);
	}

	@Transactional(readOnly = false)
	public void save(List<PsiProductEliminate> eliminates) {
		productEliminateDao.save(eliminates);
	}

	@Transactional(readOnly = false)
	public void save(PsiProductEliminate productEliminate) {
		productEliminateDao.save(productEliminate);
	} 
	
	@Transactional(readOnly = false)
	public void updateCommissionPcent(PsiProductEliminate productEliminate) {
		String sql="update psi_product_eliminate set commission_pcent=:p1 where id=:p2";
		productEliminateDao.updateBySql(sql, new Parameter(productEliminate.getId(),productEliminate.getCommissionPcent()));
	} 
	
	public Map<String, Map<String, List<String>>> findAll() {
		//productName country isSale
		Map<String, Map<String, List<String>>> rs = Maps.newLinkedHashMap();
		String sql = "SELECT CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END AS productName," +
				" GROUP_CONCAT(CONCAT(t.`country`,'_',t.`is_sale`)),p.`create_user`"+
				" FROM `psi_product_eliminate` t,`psi_product` p WHERE t.`product_id`=p.`id` AND t.`del_flag`='0' GROUP BY t.`product_name`,t.color ORDER BY t.`product_id` desc";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			String productName = objects[0].toString();
			Map<String, List<String>> country = rs.get(productName);
			if (country == null) {
				country = Maps.newHashMap();
				rs.put(productName, country);
			}
			String[] strs = objects[1].toString().split(",");
			String createUserId = objects[2].toString();
			String totalSale = "0";	//产品在售,默认否
			for (String str : strs) {
				String[] string = str.split("_");
				if (!"4".equals(string[1])) {
					totalSale = string[1];	//只要还有平台没淘汰,则不算淘汰
				}
				List<String> list2= country.get(string[0]);
				if (list2 == null) {
					list2 = Lists.newArrayList();
					country.put(string[0], list2);
				}
				list2.add(string[1]);
				list2.add(createUserId);
			}
			List<String> totalList = Lists.newArrayList();
			totalList.add(totalSale);
			totalList.add(createUserId);
			country.put("total", totalList);
		}
		return rs;
	}
	
	
	public Map<String, Map<String, String>> findAll(List<String> productNames) {
		List<Object[]> list =null;
		Map<String, Map<String, String>> rs = Maps.newLinkedHashMap();
		String sql = "SELECT CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END AS productName, GROUP_CONCAT(CONCAT(t.`country`,'_',t.`is_sale`))"+
				" FROM `psi_product_eliminate` t,`psi_product` p WHERE t.`product_id`=p.`id` AND t.`del_flag`='0' GROUP BY t.`product_name`,t.color ORDER BY t.`product_id` desc";
		if(productNames!=null&&productNames.size()>0){
			sql="SELECT CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END AS productName, GROUP_CONCAT(CONCAT(t.`country`,'_',t.`is_sale`))"+
					" FROM `psi_product_eliminate` t,`psi_product` p WHERE t.`product_id`=p.`id` AND t.`del_flag`='0' and (CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END) in :p1 GROUP BY t.`product_name`,t.color ORDER BY t.`product_id` desc";
			list= productEliminateDao.findBySql(sql,new Parameter(productNames));
		}else{
			list= productEliminateDao.findBySql(sql);
		}
		
		for (Object[] objects : list) {
			String productName = objects[0].toString();
			Map<String, String> country = rs.get(productName);
			if (country == null) {
				country = Maps.newHashMap();
				rs.put(productName, country);
			}
			String[] strs = objects[1].toString().split(",");
			String totalSale = "0";	//产品在售,默认否
			for (String str : strs) {
				String[] string = str.split("_");
				if ("1".equals(string[1])) {
					totalSale = "1";	//只要还有平台在售产品,则改成在售
				}
				country.put(string[0], string[1]);
			}
			country.put("total", totalSale);
		}
		return rs;
	}
	
	public Map<String, Map<String, List<String>>> findIsNewAll() {
		Map<String, Map<String, List<String>>> rs = Maps.newLinkedHashMap();
		String sql = "SELECT CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END AS productName," +
				" GROUP_CONCAT(CONCAT(t.`country`,'_',t.`is_new`)),p.`create_user`"+
				" FROM `psi_product_eliminate` t,`psi_product` p WHERE t.`product_id`=p.`id` AND t.`del_flag`='0' GROUP BY t.`product_name`,t.color ORDER BY t.`product_id` desc";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			String productName = objects[0].toString();
			Map<String, List<String>> country = rs.get(productName);
			if (country == null) {
				country = Maps.newHashMap();
				rs.put(productName, country);
			}
			String[] strs = objects[1].toString().split(",");
			String createUserId = objects[2].toString();
			String totalIsNew = "0";	//新品,默认否
			for (String str : strs) {
				String[] string = str.split("_");
				if ("1".equals(string[1])) {
					totalIsNew = "1";	//只要还有平台为新品,则改成新品
				}
				List<String> list2= country.get(string[0]);
				if (list2 == null) {
					list2 = Lists.newArrayList();
					country.put(string[0], list2);
				}
				list2.add(string[1]);
				list2.add(createUserId);
			}
			List<String> totalList = Lists.newArrayList();
			totalList.add(totalIsNew);
			totalList.add(createUserId);
			country.put("total", totalList);
		}
		return rs;
	}
	
	//上架日期 
	public Map<String, Map<String, List<String>>> findAddedMonthAll() {
		Map<String, Map<String, List<String>>> rs = Maps.newLinkedHashMap();
		String sql = "SELECT CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END AS productName," +
				" GROUP_CONCAT(CONCAT(t.`country`,'_',CASE WHEN t.`added_month` IS NOT NULL THEN  t.`added_month` ELSE '' END)),p.`create_user`"+
				" FROM `psi_product_eliminate` t,`psi_product` p WHERE t.`product_id`=p.`id` AND t.`del_flag`='0' GROUP BY t.`product_name`,t.color ORDER BY t.`product_id` desc";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			String productName = objects[0].toString();
			Map<String, List<String>> country = rs.get(productName);
			if (country == null) {
				country = Maps.newHashMap();
				rs.put(productName, country);
			}
			String[] strs = objects[1].toString().split(",");
			String createUserId = objects[2].toString();
			for (String str : strs) {
				String[] string = str.split("_");
				List<String> list2= country.get(string[0]);
				if (list2 == null) {
					list2 = Lists.newArrayList();
					country.put(string[0], list2);
				}
				list2.add(string.length==2?string[1]:"");
				list2.add(createUserId);
			}
		}
		return rs;
	}
	
	//销售预测方案
	public Map<String, Map<String, List<String>>> findForecastSchemeAll() {
		Map<String, Map<String, List<String>>> rs = Maps.newLinkedHashMap();
		String sql = "SELECT CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END AS productName, "+
				 " GROUP_CONCAT(CONCAT(t.`country`,'_',CASE WHEN t.`sales_forecast_scheme` IS NOT NULL THEN  t.`sales_forecast_scheme` ELSE '' END)),p.`create_user`,p.`type` "+
				 " FROM `psi_product_eliminate` t,`psi_product` p WHERE t.`product_id`=p.`id` AND t.`del_flag`='0' AND t.`is_sale`!='4' AND t.`is_new`='0' "+
				 " GROUP BY t.`product_name`,t.color ORDER BY t.`product_id` DESC";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			String productName = objects[0].toString();
			Map<String, List<String>> country = rs.get(productName);
			if (country == null) {
				country = Maps.newHashMap();
				rs.put(productName, country);
			}
			String[] strs = objects[1].toString().split(",");
			String createUserId = objects[2].toString();
			String type = objects[3].toString();
			for (String str : strs) {
				String[] string = str.split("_");
				List<String> list2= country.get(string[0]);
				if (list2 == null) {
					list2 = Lists.newArrayList();
					country.put(string[0], list2);
				}
				list2.add(string.length==2?string[1]:"");
				list2.add(createUserId);
			}
			List<String> totalList = Lists.newArrayList();
			totalList.add(createUserId);
			totalList.add(type);
			country.put("total", totalList);
		}
		return rs;
	}

	@Transactional(readOnly = false)
	public void updateProductEliminate(PsiProduct product) {
		String productName = product.getBrand() + " " + product.getModel();
		
		//先把该产品的淘汰明细记录更新为删除状态
		String delSql = "UPDATE `psi_product_eliminate` t SET t.`del_flag`='1' WHERE t.`product_id`=:p1";
		productEliminateDao.updateBySql(delSql, new Parameter(product.getId()));
		
		//按照修改后的产品参数（平台和颜色）更新记录（新插入或更新删除状态为未删除）
		String[] newColors = product.getColor().split(",");
		String[] newPlatfroms = product.getPlatform().split(",");
		for (String color : newColors) {
			color = StringUtils.isNotEmpty(color)?color:"";
			for (String country : newPlatfroms) {
				String delFlag = "0";
				if ("mx".equals(country)) {
					delFlag = "1";
				}
				String sql = "INSERT INTO `psi_product_eliminate`(product_id,product_name,country,color,is_sale,del_flag,is_new,transport_type)"+
						" VALUES('"+product.getId()+"','"+productName+"','"+country+"','"+color+"','0','"+delFlag+"','1','"+(product.getTransportType()==null?1:product.getTransportType())+"') "+
						" ON DUPLICATE KEY UPDATE del_flag=VALUES(del_flag)";	//is_sale=VALUES(is_sale)产品定位后续调整
				productEliminateDao.updateBySql(sql, null);
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void updateIsSale(String productName, String country, String isSale, String colorSync){
		String temp = "";
		if ("4".equals(isSale)) {
			temp = ",is_new='0',eliminate_time=NOW() ";
		} else {
			temp = ",eliminate_time=null ";
		}
		String[] arr = productName.split("_");
		if (StringUtils.isEmpty(country)) {
			if ("1".equals(colorSync)) {
				String sql ="UPDATE `psi_product_eliminate` AS a SET is_sale=:p1 " + temp + " WHERE a.`product_name`=:p2";
				productEliminateDao.updateBySql(sql, new Parameter(isSale, arr[0]));
			} else {
				String sql ="UPDATE `psi_product_eliminate` AS a SET is_sale=:p1 " + temp + " WHERE a.`product_name`=:p2 AND a.`color`=:p3";
				productEliminateDao.updateBySql(sql, new Parameter(isSale, arr[0], arr.length==2?arr[1]:""));
			}
		} else {
			if ("1".equals(colorSync)) {
				String sql ="UPDATE `psi_product_eliminate` AS a SET is_sale=:p1 " + temp + " WHERE a.`product_name`=:p2 AND a.`country`=:p3";
				productEliminateDao.updateBySql(sql, new Parameter(isSale, arr[0], country));
			} else {
				String sql ="UPDATE `psi_product_eliminate` AS a SET is_sale=:p1 " + temp + " WHERE a.`product_name`=:p2 AND a.`country`=:p3 AND a.`color`=:p4";
				productEliminateDao.updateBySql(sql, new Parameter(isSale, arr[0], country, arr.length==2?arr[1]:""));
			}
		}
		//更新产品表is_sale
		String sql = "SELECT t.`product_id`,t.`is_sale` FROM `psi_product_eliminate` t WHERE t.`product_name` =:p1 AND t.`del_flag`='0' ORDER BY t.`is_sale`,FIELD(t.`country`,'de','uk','fr','jp','it','es','ca','mx','com','com2','com3')";
		List<Object[]> list = productEliminateDao.findBySql(sql, new Parameter(arr[0]));
		String productIsSale = "4";	//是否在售标记
		String id = "";
		for (Object[] obj : list) {
			id = obj[0].toString();
			productIsSale = obj[1].toString();
			break;
		}
		if (StringUtils.isNotEmpty(id)) {
			PsiProduct product = productDao.get(Integer.parseInt(id));
			if (product != null && !productIsSale.equals(product.getIsSale())) {
				product.setIsSale(productIsSale);
				if ("4".equals(productIsSale)) {//淘汰品改普通品
					product.setIsNew("0");
				}
				productDao.save(product);
			}
		}
		if ("4".equals(isSale)) {
			updateIsNew();
		}
	}
	
	@Transactional(readOnly = false)
	public void updateAddedMonth(String productName, String addedMonth, String colorSync){
		String[] arr = productName.split("_");
		if ("1".equals(colorSync)) {
			String sql ="UPDATE `psi_product_eliminate` AS a SET added_month=:p1 WHERE a.`product_name`=:p2 AND a.`country`=:p3";
			productEliminateDao.updateBySql(sql, new Parameter(addedMonth, arr[0], arr[arr.length-1]));
		} else {
			String sql ="UPDATE `psi_product_eliminate` AS a SET added_month=:p1 WHERE a.`product_name`=:p2 AND a.`country`=:p3 AND a.`color`=:p4";
			productEliminateDao.updateBySql(sql, new Parameter(addedMonth, arr[0], arr[arr.length-1], arr.length==3?arr[1]:""));
		}
		//更新产品表上架时间
		String sql = "SELECT t.`product_id` FROM `psi_product_eliminate` t WHERE t.`product_name` =:p1 AND t.`del_flag`='0' GROUP BY t.`product_id`";
		List<Object> list = productEliminateDao.findBySql(sql, new Parameter(arr[0]));
		String id = "";
		for (Object obj : list) {
			id = obj.toString();
			if (StringUtils.isNotEmpty(id)) {
				PsiProduct product = productDao.get(Integer.parseInt(id));
				if (product != null && StringUtils.isEmpty(product.getAddedMonth())) {
					product.setAddedMonth(addedMonth);
					productDao.save(product);
				}
			}
		}
	}

	/**
	 * 
	 * @param productName
	 * @param country
	 * @param forecast	方案为C时同步更新整个类型的产品 1：A方案  2：B方案  3：C方案
	 * @param colorSync	是否同步更新同产品其他颜色 1：同步
	 * @param isC	原方案是否为C方案 0：不是  1：是  原方案为C时更改整个类型的产品
	 */
	@Transactional(readOnly = false)
	public void updateForecast(String productName, String country, String forecast, String colorSync, String isC) {
		String[] arr = productName.split("_");
		if ("3".equals(forecast) || "1".equals(isC)) {	//按类型
			String sql = "SELECT t.`TYPE` FROM psi_product t,psi_product_eliminate e WHERE t.id=e.product_id AND e.`product_name`=:p1";
			List<String> list = productEliminateDao.findBySql(sql,new Parameter(arr[0]));
			if (list == null || list.size() == 0) {
				return;
			}
			sql = "UPDATE `psi_product_eliminate` t INNER JOIN `psi_product` p ON t.`product_id`=p.`id` SET t.`sales_forecast_scheme`=:p1 WHERE p.`TYPE`=:p2";
			productEliminateDao.updateBySql(sql,new Parameter(forecast, list.get(0)));
		}else {
			if ("1".equals(colorSync)) {
				String sql ="UPDATE `psi_product_eliminate` AS a SET a.`sales_forecast_scheme`=:p1 WHERE a.`product_name`=:p2 AND a.`country`=:p3";
				productEliminateDao.updateBySql(sql, new Parameter(forecast, arr[0], country));
			} else {
				String sql ="UPDATE `psi_product_eliminate` AS a SET a.`sales_forecast_scheme`=:p1 WHERE a.`product_name`=:p2 AND a.`country`=:p3 AND a.`color`=:p4";
				productEliminateDao.updateBySql(sql, new Parameter(forecast, arr[0], country, arr.length==2?arr[1]:""));
			}
		}
	}
	
	/**
	 * 根据条件获取产品淘汰信息(分产品、颜色、平台),条件需要能查询到唯一记录,否则返回空
	 * @param productName 产品名称,忽略颜色
	 * @param color	产品颜色,可为空,为空时从产品名称中截取
	 * @param country 国家
	 * @return
	 */
	public PsiProductEliminate findProductEliminateByProductName(String productName, String color, String country){
		String temp = productName;
		if(productName.indexOf("_")>0){
			temp = productName.substring(0,productName.lastIndexOf("_"));
			if (StringUtils.isEmpty(color)) {
				color = productName.substring(productName.lastIndexOf("_")+1,productName.length());
			}
		}
		DetachedCriteria dc = productEliminateDao.createDetachedCriteria();
		dc.add(Restrictions.eq("productName", temp));
		if (StringUtils.isNotEmpty(country)) {
			dc.add(Restrictions.eq("country", country));
		}
		if (StringUtils.isNotEmpty(color)) {
			dc.add(Restrictions.eq("color", color));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiProductEliminate> list = productEliminateDao.find(dc);
		if(list.size()==1){
			return list.get(0);
		}
		return null;
	}
	
	public List<PsiProductEliminate> findProductEliminateList(String productName, String color, String country, List<String> isSale, String isNew){
		String temp = productName;
		if(StringUtils.isNotEmpty(productName) && productName.indexOf("_")>0){
			temp = productName.substring(0,productName.lastIndexOf("_"));
			if (StringUtils.isEmpty(color)) {
				color = productName.substring(productName.lastIndexOf("_")+1,productName.length());
			}
		}
		DetachedCriteria dc = productEliminateDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(temp)) {
			dc.add(Restrictions.eq("productName", temp));
		}
		if (StringUtils.isNotEmpty(country)) {
			dc.add(Restrictions.eq("country", country));
		}
		if (StringUtils.isNotEmpty(color)) {
			dc.add(Restrictions.eq("color", color));
		}
		if (isSale != null && isSale.size() > 0) {
			dc.add(Restrictions.in("isSale", isSale));
		}
		if (StringUtils.isNotEmpty(isNew)) {
			dc.add(Restrictions.eq("isNew", isNew));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("id"));
		return productEliminateDao.find(dc);
	}
	
	
	public Page<PsiProductEliminate> findByPage(Page<PsiProductEliminate> page, PsiProductEliminate eliminate, List<String> isSaleList) {
		DetachedCriteria dc = productEliminateDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(eliminate.getProductName())) {
			dc.add(Restrictions.sqlRestriction("CASE WHEN {alias}.color='' THEN product_name ELSE CONCAT(product_name,'_',{alias}.color) END like '%"+eliminate.getProductName().trim()+"%'"));
		}
		if (StringUtils.isNotEmpty(eliminate.getCountry())) {
			dc.add(Restrictions.eq("country", eliminate.getCountry()));
		}
		if (isSaleList != null && isSaleList.size() > 0) {
			dc.add(Restrictions.in("isSale", isSaleList));
		}
		if (eliminate.getTransportType()!=null) {
			dc.add(Restrictions.eq("transportType", eliminate.getTransportType()));
		}
		dc.createAlias("product", "product");
		List<String> fanouList = Lists.newArrayList("de","com","ca","jp","mx");
		dc.add(Restrictions.or(
				Restrictions.and(Restrictions.eq("product.hasPower", "0"), Restrictions.in("country", fanouList)),
				Restrictions.eq("product.type", "keyboard"),
				Restrictions.eq("product.hasPower", "1")
				));
		dc.add(Restrictions.eq("delFlag", "0"));
		if (!StringUtils.isNotEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("bufferPeriod"));
		}
		return productEliminateDao.find(page, dc);
	}
	
	
	/**
	 * 查询所有产品定位,区分平台和颜色
	 * @return map<产品名_颜色_国家, isSale>
	 */
	public Map<String, String> findAllProductPosition() {
		Map<String, String> rs = Maps.newLinkedHashMap();
		String sql = "SELECT CONCAT(CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END,'_',t.`country`)," +
				" t.`is_sale` FROM `psi_product_eliminate` t WHERE t.`del_flag`='0'";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			String key = objects[0].toString();
			String value = objects[1].toString();
			rs.put(key, value);
		}
		return rs;
	}
	
	/**
	 * 查询所有产品新品状态,区分平台和颜色
	 * @return map<产品名_颜色_国家, isNew>
	 */
	public Map<String, String> findIsNewMap() {
		Map<String, String> rs = Maps.newLinkedHashMap();
		String sql = "SELECT CONCAT(CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END,'_',t.`country`)," +
				" t.`is_new` FROM `psi_product_eliminate` t WHERE t.`del_flag`='0'";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			String key = objects[0].toString();
			String value = objects[1].toString();
			rs.put(key, value);
		}
		return rs;
	}
	
	public Map<String, String> findIsNewMap(String country) {
		Map<String, String> rs = Maps.newLinkedHashMap();
		String sql = "SELECT CONCAT(CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END) name," +
				" t.`is_new` FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' and t.country=:p1 ";
		List<Object[]> list = productEliminateDao.findBySql(sql,new Parameter(country));
		for (Object[] objects : list) {
			String key = objects[0].toString();
			String value = objects[1].toString();
			rs.put(key, value);
		}
		return rs;
	}
	
	/**
	 * 查询产品定位,区分颜色,不区分平台
	 * @param countryList 为空时表示所有平台
	 * @return Map<产品名_颜色, 产品定位>
	 */
	public Map<String, String> findProductPositionByCountry(List<String> countryList) {
		Map<String, String> rs = Maps.newHashMap();
		List<Object[]> list = null;
		if (countryList == null || countryList.size() == 0) {
			String sql = "SELECT CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END AS NAME, "+
					" SUBSTRING_INDEX(GROUP_CONCAT(t.`is_sale` ORDER BY FIELD(t.`country`,'de','uk','fr','jp','it','es','ca','mx','com','com2','com3')),',',1) AS isSale"+
					" FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' GROUP BY t.`product_name`,t.`color`";
			list = productEliminateDao.findBySql(sql);
		} else {
			String sql = "SELECT CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END AS NAME, "+
					" SUBSTRING_INDEX(GROUP_CONCAT(t.`is_sale` ORDER BY FIELD(t.`country`,'de','uk','fr','jp','it','es','ca','mx','com','com2','com3')),',',1) AS isSale"+
					" FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' AND t.`country` IN :p1 GROUP BY t.`product_name`,t.`color`";
			list = productEliminateDao.findBySql(sql,new Parameter(countryList));
		}
		for (Object[] objects : list) {
			String key = objects[0].toString();
			String value = objects[1].toString();
			rs.put(key, value);
		}
		return rs;
	}
	
	
	/**
	 * 查询所有新品列表,区分颜色,不区分平台
	 * @return list<产品名_颜色>
	 */
	public List<String> findIsNewProductName() {
		String sql = "SELECT bb.pname FROM("+
				" SELECT aa.pname ,SUM(CASE WHEN aa.is_new='1' AND added_month != '' AND NOT(LOCATE(',',added_month))  "+
				" THEN (CASE WHEN added_month>=DATE_ADD(CURDATE(),INTERVAL -6 MONTH) THEN '1' ELSE '0' END ) ELSE aa.is_new END) AS isnew "+
				" FROM (SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS pname,"+
				" GROUP_CONCAT(a.`added_month`) AS  added_month ,SUM(a.`is_new`) AS  is_new FROM psi_product_eliminate a "+
				" WHERE CASE WHEN a.`product_name` LIKE '%uk%' THEN a.`country` IN ('uk') "+
				" WHEN a.`product_name` LIKE '%jp%' THEN a.`country` IN ('jp')  "+
				" WHEN a.`product_name` LIKE '%eu%' THEN a.`country` IN ('de')  "+
				" WHEN a.`product_name` LIKE '%us%' THEN a.`country` IN ('com')  "+
				" ELSE a.`country` IN ('de','com') END AND a.`del_flag`='0' GROUP BY a.`product_name`,a.`color`)aa GROUP BY aa.pname)AS bb WHERE bb.isnew>=1";
		return productEliminateDao.findBySql(sql);
	}
	
	
	/**
	 * 区分颜色,不区分小平台，含欧洲和合计
	 * @return Map<产品名_颜色, list<全平台新品属性, 欧洲新品属性, 全平台产品定位>
	 */
	public List<Object[]> findIsNewProductNameWithTotalAndEu() {
		String sql = "SELECT CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END,SUM(CASE WHEN t.country= 'de'  || t.country= 'com' THEN t.is_new ELSE 0 END),SUM(CASE WHEN t.country= 'de' THEN t.is_new ELSE 0 END),SUBSTRING_INDEX(GROUP_CONCAT(t.`is_sale` ORDER BY FIELD(t.`country`,'de','uk','fr','jp','it','es','ca','mx','com','com2','com3')),',',1) AS isSale FROM `psi_product_eliminate` t WHERE t.`del_flag`='0'  GROUP BY t.`product_name`,t.`color`";
		return productEliminateDao.findBySql(sql);
	}

	/**
	 * 查询所有产品的产品定位和新品属性,区分平台和颜色
	 * @return list<产品名_颜色_国家, isSale, isNew>
	 */
	public List<Object[]> findPositionAndIsNew() {
		String sql = "SELECT CONCAT(CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END,'_',t.`country`)," +
				" t.`is_sale` , t.is_new FROM `psi_product_eliminate` t WHERE t.`del_flag`='0'";
		return productEliminateDao.findBySql(sql);
	}
	
	/**
	 * 查询在售(定位为非淘汰)产品不区分平台和颜色
	 * @param productName 产品名称,忽略颜色
	 * @return List<产品id>
	 */
	public List<Integer> findOnSaleList(String productName) {
		String sql = "SELECT t.`product_id` FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' AND t.`is_sale`!='4' ";
		if (StringUtils.isNotEmpty(productName)) {
			if(productName.indexOf("_")>0){
				productName = productName.substring(0,productName.lastIndexOf("_"));
			}
			sql += " AND t.`product_name` = :p1";
			sql += " GROUP BY t.`product_id`";
			return productEliminateDao.findBySql(sql, new Parameter(productName));
		} else {
			sql += " GROUP BY t.`product_id`";
			return productEliminateDao.findBySql(sql);
		}
		
	}
	
	/**
	 * 产品在售颜色和平台汇总
	 * @return
	 */
	public Map<Integer, String> getOnSaleColorPlatform() {
		Map<Integer, String> rs = Maps.newHashMap();
		String sql ="SELECT t.`product_id`,CONCAT(t.`color`,'_',GROUP_CONCAT(t.`country`)) FROM `psi_product_eliminate` t"+ 
				" WHERE t.`del_flag`='0' AND t.`is_sale`!='4' AND t.`color` !='' GROUP BY t.`product_id`,t.`color`";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			Integer id = Integer.parseInt(objects[0].toString());
			String colorPlatform = objects[1].toString();
			String str = rs.get(id);
			if (StringUtils.isEmpty(str)) {
				rs.put(id, colorPlatform);
			} else {
				rs.put(id, rs.get(id) + ";" +colorPlatform);
			}
		}
		return rs;
	}
	
	/**
	 * 产品在售(非淘汰)颜色
	 * @return Map<产品ID, 在售的颜色(多个以逗号分隔)>
	 */
	public Map<Integer, String> getOnSaleColor() {
		Map<Integer, String> rs = Maps.newHashMap();
		String sql ="SELECT t.`product_id`,t.`color` FROM `psi_product_eliminate` t"+ 
				" WHERE t.`del_flag`='0' AND t.`is_sale`!='4' AND t.`color` !='' GROUP BY t.`product_id`,t.`color`";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			Integer id = Integer.parseInt(objects[0].toString());
			String color = objects[1].toString();
			String str = rs.get(id);
			if (StringUtils.isEmpty(str)) {
				rs.put(id, color);
			} else {
				rs.put(id, rs.get(id) + "," +color);
			}
		}
		return rs;
	}
	
	/**
	 * 产品在售平台
	 * @return
	 */
	public Map<Integer, String> getOnSalePlatform() {
		Map<Integer, String> rs = Maps.newHashMap();
		String sql ="SELECT t.`product_id`,GROUP_CONCAT(DISTINCT t.`country`) FROM `psi_product_eliminate` t"+ 
				" WHERE t.`del_flag`='0' AND t.`is_sale`!='4'  GROUP BY t.`product_id`";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			Integer id = Integer.parseInt(objects[0].toString());
			String color = objects[1].toString();
			rs.put(id, color);
		}
		return rs;
	}
	
	/**
	 *获取带国家所有淘汰或新品的产品
	 */
	public Map<String, Set<String>> getNotSaleOrNewProduct() {
		Map<String, Set<String>> rs = Maps.newHashMap();
		String sql ="SELECT  (CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.color) END) AS proColor ,a.`country`FROM psi_product_eliminate AS a WHERE a.`del_flag`='0' AND (a.`is_sale`='4' or a.is_new='1')";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			String proColor = objects[0].toString();
			String country = objects[1].toString();
			Set<String> proColorSet = Sets.newHashSet();
			if(rs.get(country)==null){
				proColorSet=Sets.newHashSet();
			}else{
				proColorSet=rs.get(country);
			}
			proColorSet.add(proColor);
			rs.put(country, proColorSet);
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public Integer updateIsNew(){
		String sql="UPDATE psi_product_eliminate SET is_new='0' WHERE added_month IS NOT NULL AND is_new='1' AND TO_DAYS(NOW()) - TO_DAYS(added_month)>=180 ";
		Integer count = productEliminateDao.updateBySql(sql, null);
		//更新产品表新品属性(规则,de&com平台所有颜色都为普通品时整个产品才算普通品)
		//sql = "SELECT t.`product_id`,t.`color`,t.`is_new` FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' AND t.`country` IN('de','com')";
		sql = "UPDATE psi_product p ,(SELECT aa.product_id ,SUM(CASE WHEN aa.is_new='1' AND added_month != '' AND NOT(LOCATE(',',added_month))  " +
				" THEN (CASE WHEN added_month>=DATE_ADD(CURDATE(),INTERVAL -6 MONTH) THEN '1' ELSE '0' END ) ELSE aa.is_new END) AS isnew  FROM " +
				" (SELECT a.`product_id`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) AS pname," +
				" GROUP_CONCAT(a.`added_month`) AS  added_month ,SUM(a.`is_new`) AS  is_new FROM psi_product_eliminate a WHERE " +
				" CASE WHEN a.`product_name` LIKE '%uk%' THEN a.`country` IN ('uk') "+
				" WHEN a.`product_name` LIKE '%jp%' THEN a.`country` IN ('jp')  "+
				" WHEN a.`product_name` LIKE '%eu%' THEN a.`country` IN ('de')  "+
				" WHEN a.`product_name` LIKE '%us%' THEN a.`country` IN ('com')  "+
				" ELSE a.`country` IN ('de','com') END" +
				" AND a.`del_flag`='0' GROUP BY a.`product_name`,a.`color`)aa GROUP BY aa.product_id) aaa  " +
				" SET p.is_new=(CASE WHEN aaa.isnew>1 THEN '1' ELSE aaa.isnew END )  "+
				" WHERE p.`id` = aaa.product_id AND (CASE WHEN aaa.isnew>1 THEN '1' ELSE aaa.isnew END ) != p.`is_new`"	;
		productEliminateDao.updateBySql(sql, null);
		return count;
	}
	
	public List<PsiProductEliminate> findNoAddedMonth(){
		DetachedCriteria dc = productEliminateDao.createDetachedCriteria();
		dc.add(Restrictions.isNull("addedMonth"));
		dc.add(Restrictions.eq("delFlag", "0"));
		return productEliminateDao.find(dc);
	}

	/**
	 * @param country
	 * @return
	 */
	public List<PsiProductEliminate> findOnSaleNotNew(String country){
		DetachedCriteria dc = productEliminateDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(country)) {
			dc.add(Restrictions.eq("country",country));
		}
		dc.add(Restrictions.ne("isSale","4"));
		dc.add(Restrictions.eq("isNew","0"));
		dc.add(Restrictions.isNotNull("addedMonth"));
		dc.add(Restrictions.eq("delFlag", "0"));
		
		List<PsiProductEliminate> list = productEliminateDao.find(dc);
		Map<String, String> map = findProductFanOuFlag();
		if ("uk".equals(country)) {
			for (Iterator<PsiProductEliminate> iterator = list.iterator(); iterator.hasNext();) {
				PsiProductEliminate eliminate = iterator.next();
				String colorName = eliminate.getColorName();
				if("0".equals(map.get(colorName))){
					iterator.remove();
				}
			}
		} else if ("fr,uk,it,es".contains(country)) {
			for (Iterator<PsiProductEliminate> iterator = list.iterator(); iterator.hasNext();) {
				PsiProductEliminate eliminate = iterator.next();
				String colorName = eliminate.getColorName();
				if("0".equals(map.get(colorName)) || "1".equals(map.get(colorName))){
					iterator.remove();
				}
			}
		}
		return list;
	}

	/**
	 * 查询产品的定位、新品、上架时间属性
	 * @param productName
	 * @return Map<平台,PsiProductEliminate>
	 */
	public Map<String,PsiProductEliminate> findAllInfoByNameWithColor(String productName){
		String sql = "SELECT country,t.`is_sale`,t.is_new,t.added_month FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' "+
				" AND (CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END)=:p1  ORDER BY t.`added_month`";
		List<Object[]> list=productEliminateDao.findBySql(sql, new Parameter(productName));
		Map<String,PsiProductEliminate> map=Maps.newLinkedHashMap();
		for (Object[] obj: list) {
			PsiProductEliminate product=new PsiProductEliminate();
			product.setIsSale(obj[1].toString());
			product.setIsNew(obj[2].toString());
			product.setAddedMonth(obj[3]==null?"":obj[3].toString());
			map.put(obj[0].toString(), product);
		}
		return map;
	}
	
	public Map<String,String> findAddedMonth(){
		String sql = "SELECT country,t.added_month,(CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END) name "+
				" FROM `psi_product_eliminate` t WHERE  t.`del_flag`='0' and added_month is not null ";
		List<Object[]> list=productEliminateDao.findBySql(sql);
		Map<String,String> map=Maps.newLinkedHashMap();
		for (Object[] obj: list) {
			map.put(obj[2].toString()+"_"+obj[0].toString(),obj[1].toString());
		}
		return map;
	}
	
	/**
	 * 根据产品名称获取所有平台的在售属性
	 * @param productName 产品名称，带颜色
	 * @return
	 */
	public Map<String, String> findProductPositionByName(String productName) {
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT t.`country`,t.`is_sale` FROM `psi_product_eliminate` t WHERE (CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END)=:p1 AND t.`del_flag`='0' ";
		List<Object[]> list = productEliminateDao.findBySql(sql, new Parameter(productName));
		for (Object[] objects : list) {
			rs.put(objects[0].toString(), objects[1].toString());
		}
		return rs;
	}

	//查询符合预测条件的产品
	public List<String> findForecastName(){
		String sql = "SELECT DISTINCT CONCAT(e.`product_name`, CASE WHEN e.`color`='' THEN '' ELSE CONCAT('_', e.`color`)END) FROM `psi_product_eliminate` e"+ 
				" WHERE e.`is_new`='0' AND e.`is_sale`!='4' AND e.`del_flag`='0' AND e.`added_month` IS NOT NULL AND e.`product_name` NOT IN('Inateck Old','Inateck other') ";
		return productEliminateDao.findBySql(sql);
	}
	
	/**
	 * 查询各平台在售非新品产品集合
	 * @return Map<平台, 产品名集合>
	 */
	public Map<String,List<String>> findCountryOnSaleNotNewProduct() {
		Map<String, List<String>> rs = Maps.newLinkedHashMap();
		String sql = "SELECT t.country,(CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END) " +
				"  FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' and t.`is_sale`!='4' and t.is_new='0' ";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			List<String> temp=rs.get(objects[0].toString());
			if(temp==null){
				temp=Lists.newArrayList();
				rs.put(objects[0].toString(), temp);
			}
			temp.add(objects[1].toString());
		}
		return rs;
	}
	
	//新品上架天数(用于计算31日销日均销)[产品名称_颜色_国家  上架天数]
	public Map<String, Integer> findOnSaleDays() {
		Map<String, Integer> rs = Maps.newLinkedHashMap();
		String sql = "SELECT CONCAT(t.product_name,CASE WHEN t.color='' THEN '' ELSE CONCAT('_', t.color) END,'_', t.country),TO_DAYS(NOW()) - TO_DAYS(t.added_month) FROM psi_product_eliminate t "+
				" WHERE added_month IS NOT NULL AND is_new='1' AND del_flag='0' AND TO_DAYS(NOW()) - TO_DAYS(added_month)<=31 AND TO_DAYS(NOW()) - TO_DAYS(added_month)>0 ";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] obj : list) {
			rs.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
		}
		return rs;
	}
	
	/**
	 * 不分平台查询所有产品定位、新品、上架时间(最早的时间)
	 * @return 
	 */
	public Map<String, PsiProductEliminate> findProductAllAttr() {
		Map<String, PsiProductEliminate> rs = Maps.newLinkedHashMap();
		String sql = "SELECT t.`product_name`,t.`color`, SUBSTRING_INDEX(GROUP_CONCAT(t.`is_sale` ORDER BY FIELD(t.`country`,'de','uk','fr','jp','it','es','ca','mx','com','com2','com3')),',',1) AS isSale,SUM(t.`is_new`),MIN(t.`added_month`) "+
				" FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' AND t.`product_name` IS NOT NULL GROUP BY t.`product_name`,t.`color`";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			String productName = objects[0].toString();
			String color = objects[1]==null?"":objects[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			String isSale = objects[2].toString();
			Float isNew = Float.parseFloat(objects[3].toString());
			String addedMonth = objects[4]==null?"":objects[4].toString();
			PsiProductEliminate eliminate = new PsiProductEliminate();
			eliminate.setIsSale(DictUtils.getDictLabel(isSale, "product_position", ""));
			eliminate.setIsNew(isNew>0?"新品":"普通品");
			eliminate.setAddedMonth(addedMonth);
			rs.put(productName, eliminate);
		}
		return rs;
	}
	
	public List<Object[]> findOffWebsiteProduct(){
		String sql = "SELECT  CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END,b.`color`) AS pname, b.`country`,CASE WHEN p.type='Keyboard' THEN '1' ELSE p.`has_power` END has_power,GROUP_CONCAT(DISTINCT s.`asin`) FROM psi_product_eliminate b,psi_product p,psi_sku s  WHERE s.`country` = b.`country` AND s.`product_name` = b.`product_name` AND s.`color` = b.`color` AND  s.`del_flag`= '0' AND  b.`product_id` = p.`id` AND  b.`del_flag` = '0'  AND s.`product_name`!= 'inateck other' AND s.`product_name`!= 'inateck old'  AND b.`added_month` IS NOT NULL AND b.`is_sale` = '4' AND b.country !='ca' AND b.country !='es' AND off_website = '0' AND s.asin IS NOT NULL GROUP BY pname, b.`country`";
		return  productEliminateDao.findBySql(sql);
	}
	
	public Map<String,Integer> findStockProduct(){
		String sql = "SELECT CONCAT(a.`product_name`,'_',a.`country`) AS pkey ,SUM(a.`total_stock`) FROM psi_product_in_stock a WHERE  a.`data_date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND a.`country` IN('uk','com','jp','eu') GROUP BY pkey ";
		Map<String,Integer> rs = Maps.newHashMap();
		List<Object[]> list =   productEliminateDao.findBySql(sql);
		for (Object[] objs : list) {
			rs.put(objs[0].toString(), Integer.parseInt(objs[1].toString()));
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void updateOffWebsite(String country,Set<String> products){
		String sql = "UPDATE psi_product_eliminate a SET a.off_website = '1' WHERE  a.`country` = :p1 AND CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`) IN :p2 ";
		productEliminateDao.updateBySql(sql, new Parameter(country,products));
	}
	
	
	public List<String> findNewAfterTwoMonth(){
		String sql="SELECT e.`country`,CONCAT(e.`product_name`,CASE WHEN e.`color`!='' THEN CONCAT('_',e.`color`) ELSE '' END) NAME "+
                   " FROM psi_product_eliminate e WHERE e.`is_new`='1' AND e.`country` IN ('de','com','jp') AND e.`del_flag`='0' AND e.`added_month` IS NOT NULL and e.`added_month`>=:p1 ";
		List<Object[]> list=productEliminateDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addMonths(new Date(),-2))));
		List<String> resList=Lists.newArrayList();
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				resList.add(obj[1].toString()+"_"+obj[0].toString());
			}
		}
		return resList;
	}

	/**
	 * 分平台查询所有产品定位、新品、上架时间等属性
	 * @return Map<colorName_country, PsiProductEliminate>
	 */
	public Map<String,PsiProductEliminate> findProductCountryAttr(){
		Map<String,PsiProductEliminate> map=Maps.newHashMap();
		String sql="SELECT e.`country`,CONCAT(e.`product_name`,CASE WHEN e.`color`!='' THEN CONCAT('_',e.`color`) ELSE '' END) NAME,is_sale,eliminate_time,is_new,e.`added_month`,e.`buffer_period`,e.transport_type "+
                " FROM psi_product_eliminate e WHERE e.`del_flag`='0' ";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] objects : list) {
			String country=objects[0].toString();
			String productName = objects[1].toString();
			String isSale = objects[2].toString();
			Date eliminateTime=null;
			if(objects[3]!=null){
				eliminateTime=(Date)objects[3];
			}
			Float isNew = Float.parseFloat(objects[4].toString());
			String addedMonth = objects[5]==null?"":objects[5].toString();
			PsiProductEliminate eliminate = new PsiProductEliminate();
			//爆款、利润款、主力、淘汰
			eliminate.setIsSale(DictUtils.getDictLabel(isSale, "product_position", ""));
			eliminate.setIsNew(isNew>0?"新品":"普通品");
			eliminate.setAddedMonth(addedMonth);
			eliminate.setEliminateTime(eliminateTime);
			eliminate.setBufferPeriod(Integer.parseInt(objects[6]==null?"0":objects[6].toString()));
			eliminate.setTransportType(Integer.parseInt(objects[7].toString()));//1:海运 2：空运
			map.put(productName+"_"+country, eliminate);
		}
		return map;
	}
	
	
	/***
	 *查出新品拼了国家
	 */
	public List<String>  getNewProductsCountry(){
		String sql="SELECT CASE WHEN a.`color` ='' THEN CONCAT(a.`product_name`,'_',a.`country`) ELSE CONCAT(a.`product_name`,'_',a.`color`,'_',a.`country`) END  FROM psi_product_eliminate AS a WHERE a.`del_flag`='0' AND a.`is_new`='1'";
		return this.productDao.findBySql(sql);
	}
	
	//产品名_颜色 、国家
	public Map<String,Map<String,PsiProductEliminate>> findAllByNameAndCountry(){
		Map<String,Map<String,PsiProductEliminate>>  map=Maps.newHashMap();
		DetachedCriteria dc = productEliminateDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiProductEliminate> list=productEliminateDao.find(dc);
		for (PsiProductEliminate psiProductEliminate : list) {
			Map<String,PsiProductEliminate> temp=map.get(psiProductEliminate.getColorName());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(psiProductEliminate.getColorName(), temp);
			}
			temp.put(psiProductEliminate.getCountry(),psiProductEliminate);
		}
		return map;
	}
	
	//产品ID_颜色 、国家
	public Map<String,Map<String,PsiProductEliminate>> findAllByNameIdAndCountry(){
		Map<String,Map<String,PsiProductEliminate>>  map=Maps.newHashMap();
		DetachedCriteria dc = productEliminateDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiProductEliminate> list=productEliminateDao.find(dc);
		for (PsiProductEliminate psiProductEliminate : list) {
			String key=psiProductEliminate.getProduct().getId()+"";
			if(StringUtils.isNotBlank(psiProductEliminate.getColor())){
				key=psiProductEliminate.getProduct().getId()+"_"+psiProductEliminate.getColor();
			}
			Map<String,PsiProductEliminate> temp=map.get(key);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(key, temp);
			}
			temp.put(psiProductEliminate.getCountry(),psiProductEliminate);
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void updatePiPrice(String name,String type,String country,Float price){
		if("1".equals(type)){//cnpi_price
			String sql = "UPDATE psi_product_eliminate a SET a.cnpi_price =:p1 WHERE CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`)=:p2 ";
			productEliminateDao.updateBySql(sql, new Parameter(price,name));
		}else{
			String sql = "UPDATE psi_product_eliminate a SET a.pi_price =:p1  WHERE  a.`country` in :p2 AND CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`)=:p3 ";
			Set<String> countrySet=Sets.newHashSet();
			if("de".equals(country)){
				countrySet=Sets.newHashSet("de","fr","it","es","uk");
			}else{
				countrySet=Sets.newHashSet(country);
			}
			productEliminateDao.updateBySql(sql, new Parameter(price,countrySet,name));
		}
	}
	
	@Transactional(readOnly = false)
	public void updateCNPiPrice(String name,Double price){
		String sql = "UPDATE psi_product_eliminate a SET a.cnpi_price =:p1 WHERE CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`)=:p2 ";
		productEliminateDao.updateBySql(sql, new Parameter(price,name));
	}
	
	@Transactional(readOnly = false)
	public void updateCNPiPrice(Map<String,Double> priceMap){
		String sql = "UPDATE psi_product_eliminate a SET a.cnpi_price =:p1 WHERE CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`)=:p2 ";
		for (Map.Entry<String,Double> e: priceMap.entrySet()) {
			String name=e.getKey();
			Double price=e.getValue();
			productEliminateDao.updateBySql(sql, new Parameter(price,name));
		}
	}
	
	@Transactional(readOnly = false)
	public void updatePiPrice(String name,Double price,String country){
		String sql = "UPDATE psi_product_eliminate a SET a.pi_price =:p1  WHERE  a.`country` in :p2 AND a.`product_name`=:p3 and a.`color`=:p4 ";
		String color = "";
		String productName=name;
		if(name.indexOf("_")>0){
			productName=name.substring(0,name.lastIndexOf("_"));
			color = name.substring(name.lastIndexOf("_")+1);
		}
		Set<String> countrySet=Sets.newHashSet();
		if("de".equals(country)){
			countrySet=Sets.newHashSet("de","fr","it","es","uk");
		}else{
			countrySet=Sets.newHashSet(country);
		}
		productEliminateDao.updateBySql(sql, new Parameter(price,countrySet,productName,color));
	}
	
	@Transactional(readOnly = false)
	public void updatePiPrice(Map<String,Double> priceMap){
		String sql = "UPDATE psi_product_eliminate a SET a.pi_price =:p1  WHERE  a.`country` in :p2 AND a.`product_name`=:p3 and a.`color`=:p4 ";
		for (Map.Entry<String,Double> e: priceMap.entrySet()) {
			String tempName=e.getKey();
			Double price=e.getValue();
			String country=tempName.substring(tempName.lastIndexOf("_")+1);
			String name=tempName.substring(0,tempName.lastIndexOf("_"));
			
			String color = "";
			String productName=name;
			if(name.indexOf("_")>0){
				productName=name.substring(0,name.lastIndexOf("_"));
				color = name.substring(name.lastIndexOf("_")+1);
			}
			Set<String> countrySet=Sets.newHashSet();
			if("de".equals(country)){
				countrySet=Sets.newHashSet("de","fr","it","es","uk");
			}else{
				countrySet=Sets.newHashSet(country);
			}
			productEliminateDao.updateBySql(sql, new Parameter(price,countrySet,productName,color));
		}
		
	}
	
	public Map<String,Map<String,Float>> findModelPiPrice(){
		Map<String,Map<String,Float>>  map=Maps.newHashMap();
		DetachedCriteria dc = productEliminateDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiProductEliminate> list=productEliminateDao.find(dc);
		for (PsiProductEliminate eliminate : list) {
			if(eliminate.getPiPrice()!=null){
				String name=eliminate.getProductName().substring(eliminate.getProductName().indexOf(" ")+1);
				Map<String,Float> temp=map.get(eliminate.getCountry());
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(eliminate.getCountry(), temp);
				}
				temp.put(name, eliminate.getPiPrice());
			}
		}
		return map;
	}
	
	public Map<String,Map<String,Float>> findAmazonPiPrice(){
		Map<String,Map<String,Float>> map=Maps.newHashMap();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM");
		String date=dateFormat.format(DateUtils.addMonths(new Date(),-2));
		String sql="select country,model,price from amazoninfo_pi_price where datadate=:p1 ";
		List<Object[]> list=productEliminateDao.findBySql(sql,new Parameter(date));
		for (Object[] obj: list) {
			if(obj[2]!=null){
				    Float price=((BigDecimal)obj[2]).floatValue();
					Map<String,Float> temp=map.get(obj[0].toString());
					if(temp==null){
						temp=Maps.newHashMap();
						map.put(obj[0].toString(), temp);
					}
					temp.put(obj[1].toString(),price);
			}
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void savePiPrice(String country,String model,String date,Float price){
		String sql="insert into amazoninfo_pi_price(country,model,datadate,create_date,price) VALUES (:p1,:p2,:p3,:p4,:p5) ";
		productEliminateDao.updateBySql(sql, new Parameter(country,model,date,new Date(),price));
	}
	
	
	/**
	 *查找产品运输方式
	 * 
	 */
	public Map<String,String> findTransportType(){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT (CASE WHEN a.color='' THEN CONCAT(a.product_name,'_',a.country) ELSE CONCAT(a.product_name,'_',a.color,'_',a.country) END) AS comKey,a.`transport_type`   FROM psi_product_eliminate AS a WHERE a.`product_name` IS NOT NULL AND a.`del_flag`='0' ";
		List<Object[]> list=productEliminateDao.findBySql(sql);
		for (Object[] obj: list) {
			map.put(obj[0].toString(), obj[1].toString());
		}
		return map;
	}
	 
	
	@Transactional(readOnly = false)
	public void updatePiPrice(String model,String country,Float price){
		String sql = "UPDATE amazoninfo_pi_price a SET price=:p1 where country=:p2 and model=:p3 ";
		productEliminateDao.updateBySql(sql, new Parameter(price,country,model));
	}
	
	public List<String> getCnPiIsNull(){
		String sql="SELECT distinct CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END ,a.`color`) FROM psi_product_eliminate a WHERE a.`del_flag`='0' AND (a.`cnpi_price` IS NULL or a.`cnpi_price`=0)";
		return productEliminateDao.findBySql(sql);
	}

	//分颜色组装产品运输方式,map[产品名称, transportType] transportType： 1 海运  2空运 3 空、海运
	public Map<String, Integer> findProductTransportType(){
		Map<String, Integer> rs = Maps.newHashMap();
		String sql ="SELECT t.`product_name`,t.`color`,t.`transport_type` FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' AND t.`transport_type` IS NOT NULL GROUP BY t.`product_name`,t.`color`,t.`transport_type`";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] obj : list) {
			String name = obj[0].toString();
			String color = obj[1]==null?"":obj[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				name = name + "_" + color;
			}
			Integer transportType = Integer.parseInt(obj[2].toString());
			if (rs.get(name) != null) {
				transportType = transportType + rs.get(name);
			}
			rs.put(name, transportType);
		}
		return rs;
	}

	//缓冲周期,map[产品名称, [国家	缓冲周期]] 
	public Map<String, Map<String, Integer>> findBufferPeriod() {
		Map<String, Map<String, Integer>> rs = Maps.newHashMap();
		String sql ="SELECT t.`product_name`,t.`color`,t.`buffer_period`,t.`country` FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' ";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] obj : list) {
			String name = obj[0].toString();
			String color = obj[1]==null?"":obj[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				name = name + "_" + color;
			}
			Integer bufferPeriod = obj[2]==null?0:Integer.parseInt(obj[2].toString());
			String country = obj[3].toString();
			Map<String, Integer> productMap = rs.get(name);
			if (productMap == null) {
				productMap = Maps.newHashMap();
				rs.put(name, productMap);
			}
			productMap.put(country, bufferPeriod);
		}
		return rs;
	}

	//缓冲周期,map[产品名称, [国家	缓冲周期]] 
	public Map<String, Integer> findBufferPeriod(String productName) {
		Map<String, Integer> rs = Maps.newHashMap();
		String sql ="SELECT t.`buffer_period`,t.`country` FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' " +
				" AND CASE WHEN t.`color`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color`) END=:p1 ";
		List<Object[]> list = productEliminateDao.findBySql(sql, new Parameter(productName));
		for (Object[] obj : list) {
			Integer bufferPeriod = Integer.parseInt(obj[0]==null?"0":obj[0].toString());
			String country = obj[1].toString();
			rs.put(country, bufferPeriod);
		}
		return rs;
	}

	/*
	 * map[产品名称, [国家	PsiProductEliminate]] 
	 * PsiProductEliminate含缓冲周期和运输类型属性(运输类型3为混合类型，即空海运都有)
	 * 欧洲以德国为准
	 */
	public Map<String, Map<String, PsiProductEliminate>> findProductAttr() {
		Map<String, Map<String, PsiProductEliminate>> rs = Maps.newHashMap();
		String sql ="SELECT t.`product_name`,t.`color`,t.`buffer_period`,t.`country`,t.`transport_type` FROM `psi_product_eliminate` t WHERE t.`del_flag`='0' ";
		List<Object[]> list = productEliminateDao.findBySql(sql);
		for (Object[] obj : list) {
			String name = obj[0].toString();
			String color = obj[1]==null?"":obj[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				name = name + "_" + color;
			}
			Integer bufferPeriod = Integer.parseInt(obj[2].toString());
			String country = obj[3].toString();
			//默认没填的为空运
			Integer transportType = obj[4]==null?2:Integer.parseInt(obj[4].toString());
			Map<String, PsiProductEliminate> productMap = rs.get(name);
			if (productMap == null) {
				productMap = Maps.newHashMap();
				rs.put(name, productMap);
			}
			PsiProductEliminate eliminate = new PsiProductEliminate();
			eliminate.setBufferPeriod(bufferPeriod);
			eliminate.setTransportType(transportType);
			productMap.put(country, eliminate);
			//欧洲
			if ("de,fr,uk,it,es".contains(country)) {
				PsiProductEliminate euEliminate = productMap.get("eu");
				if (euEliminate == null) {
					euEliminate = new PsiProductEliminate();
					productMap.put("eu", euEliminate);
				}
				if (euEliminate.getBufferPeriod()==0 && "uk".equals(country)) {
					euEliminate.setBufferPeriod(bufferPeriod);
				}
				if ("de".equals(country)) {
					euEliminate.setBufferPeriod(bufferPeriod);
				}
				if (euEliminate.getTransportType() == null) {
					euEliminate.setTransportType(transportType);
				} else if (euEliminate.getTransportType()!=transportType && euEliminate.getTransportType()<3) {
					euEliminate.setTransportType(euEliminate.getTransportType() + transportType);
				}
			}

			PsiProductEliminate totalEliminate = productMap.get("total");
			if (totalEliminate == null) {
				totalEliminate = new PsiProductEliminate();
				productMap.put("total", totalEliminate);
			}
			if (totalEliminate.getBufferPeriod()==0 && "uk".equals(country)) {
				totalEliminate.setBufferPeriod(bufferPeriod);
			}
			if ("de".equals(country)) {
				totalEliminate.setBufferPeriod(bufferPeriod);
			}
			if (totalEliminate.getTransportType() == null) {
				totalEliminate.setTransportType(transportType);
			} else if (totalEliminate.getTransportType()!=transportType && totalEliminate.getTransportType()<3) {
				totalEliminate.setTransportType(totalEliminate.getTransportType() + transportType);
			}
		}
		return rs;
	}
	
	//更新欧洲国家缓冲周期和运输方式
	@Transactional(readOnly = false)
	public void updateAttrByEu(PsiProductEliminate psiProductEliminate){
		String sql = "UPDATE `psi_product_eliminate` t SET t.`buffer_period`=:p1,t.`transport_type`=:p2 WHERE t.`product_name`=:p3 AND t.`color`=:p4 AND t.`country` IN('fr','uk','it','es') AND t.`del_flag`='0' ";
		productEliminateDao.updateBySql(sql, new Parameter(psiProductEliminate.getBufferPeriod(), psiProductEliminate.getTransportType(), psiProductEliminate.getProductName(), psiProductEliminate.getColor()));
	}
	
	@Transactional(readOnly = false)
	public void updateCommission(List<Integer> idList,String country,Integer commissionPcent){
		String sql = "UPDATE `psi_product_eliminate` t SET commission_pcent=:p1 WHERE product_id in :p2 and country=:p3 AND t.`del_flag`='0' ";
		productEliminateDao.updateBySql(sql, new Parameter(commissionPcent,idList,country));
	}
	
	
	/**
	 *查找未淘汰的新品 ：国家
	 * Map<proName,Set<country>>
	 */
	public Map<String,Set<String>>  getSaleNewProduct(){
		Map<String,Set<String>> rs = Maps.newHashMap();
		String sql="SELECT CASE WHEN a.`color`='' THEN a.product_name ELSE CONCAT(a.product_name,'_',a.color) END AS pro,a.country  FROM psi_product_eliminate AS a WHERE  a.`del_flag`='0'  AND a.is_sale!='4' AND a.`is_new`='1' ";
		List<Object[]> list=productEliminateDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				String proName = obj[0].toString();
				String country = obj[1].toString();
				Set<String> countrys = null;
				if(rs.get(country)!=null){
					countrys=rs.get(country);
				}else{
					countrys=Sets.newHashSet();
				}
				countrys.add(country);
				rs.put(proName, countrys);
			}
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void updateFbaFee(Map<Integer,Float> fbaMap){
		String sql = "UPDATE `psi_product_eliminate` t SET fba_fee=:p1 WHERE id=:p2 AND t.`del_flag`='0' ";
		for (Map.Entry<Integer,Float> entry: fbaMap.entrySet()) {
			productEliminateDao.updateBySql(sql, new Parameter(entry.getValue(),entry.getKey()));
		}
	}
	

	@Transactional(readOnly = false)
	public void updateFbaEuFee(Map<Integer,Float> fbaEuMap){
		String sql = "UPDATE `psi_product_eliminate` t SET fba_fee_eu=:p1 WHERE id=:p2 AND t.`del_flag`='0' ";
		for (Map.Entry<Integer,Float> entry: fbaEuMap.entrySet()) {
			productEliminateDao.updateBySql(sql, new Parameter(entry.getValue(),entry.getKey()));
		}
	}
	
	public Map<String,List<Object[]>> findExceptionFbaFee(){
	/*	String sql="SELECT p.`country`,d.`product_name`,d.`fba`,p.fba_fee,(d.`fba`-p.fba_fee) tempFee,d.sku FROM psi_product_eliminate p "+
			" JOIN amazoninfo_product_price d ON p.`country`=d.`country` AND d.`product_name`=CONCAT(p.`product_name`,CASE WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)  "+
			" WHERE d.`date`=DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND d.`type`='0' and  p.is_sale='1'  AND p.`del_flag`='0' AND p.`fba_fee` IS NOT NULL and (d.`fba`-p.fba_fee>0.5 or p.fba_fee-d.`fba`>0.5 ) order by country,tempFee desc ";
	*/	
		
		String sql="SELECT p.`country`,d.`product_name`,d.`fba`,(CASE WHEN (p.`country` IN ('fr','it','es')&&p.`fba_fee_eu` IS NOT NULL&&d.sku LIKE '%-de%')   "+
			"	THEN p.`fba_fee_eu` ELSE  p.fba_fee END   "+
				"		),(d.`fba`-(CASE WHEN (p.`country` IN ('fr','it','es')&&p.`fba_fee_eu` IS NOT NULL&&d.sku LIKE '%-de%')      "+
				"		THEN p.`fba_fee_eu` ELSE  p.fba_fee END   "+
				"		)) tempFee,d.sku   "+
				
				"		FROM psi_product_eliminate p   "+
				"		JOIN amazoninfo_product_price d ON p.`country`=d.`country` AND d.`product_name`=CONCAT(p.`product_name`,CASE WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)    "+ 
				"		WHERE d.`date`=DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND d.`type`='0' AND  p.is_sale!='4'    "+
				"		 AND p.`del_flag`='0' AND p.`fba_fee` IS NOT NULL AND (d.`fba`-(CASE WHEN (p.`country` IN ('fr','it','es')&&p.`fba_fee_eu` IS NOT NULL&&d.sku LIKE '%-de%')      "+
				"		THEN p.`fba_fee_eu` ELSE  p.fba_fee END )>0.5 )    "+
				"		ORDER BY country,tempFee DESC ";
		List<Object[]> list=productEliminateDao.findBySql(sql);
		 Map<String,List<Object[]>> map=Maps.newLinkedHashMap();
		for (Object[] obj: list) {
			List<Object[]> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Lists.newArrayList();
				map.put(obj[0].toString(),temp);
			}
			temp.add(obj);
		}
		return map;
	}
	
	/**
	 * @return Map<colorName, flag> flag 0：完全泛欧  1：uk以外4国泛欧  2：不能泛欧
	 */
	public Map<String, String> findProductFanOuFlag(){
		List<String> samePartNoList = Lists.newArrayList();
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT a.`TYPE`,a.`has_power`,CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName  "+
				 " FROM psi_product a JOIN mysql.help_topic b "+
				 " ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`del_flag` = '0' ";
		List<Object[]> list=productEliminateDao.findBySql(sql);
		for (Object[] obj: list) {
			String flag = "0";
			String type = obj[0].toString();
			String hasPower = obj[1].toString();
			String productName = obj[2].toString();
			//Keyboard类型和同partNo的不能泛欧
			if ("Keyboard".equals(type) || samePartNoList.contains(productName)) {
				flag = "2";
			} else if ("1".equals(hasPower)) {
				flag = "1";	//其它带电源类型的只能再UK以外的4国泛欧
			}
			rs.put(productName, flag);
		}
		return rs;
	}
	
	public List<String> findNoCnPI(){
		String sql="SELECT DISTINCT CONCAT(t.`product_name`,CASE WHEN t.`color`='' THEN '' ELSE CONCAT('_',t.`color`) END) NAME FROM psi_product_eliminate t  "+
				  "	JOIN psi_product p ON t.`product_id`=p.id AND p.`del_flag`='0' AND p.`is_new`='1'   "+
				  "	WHERE (t.`cnpi_price` IS NULL OR t.`cnpi_price`=0 OR t.`pi_price` IS NULL OR t.`pi_price`=0) AND t.is_sale!='4' AND t.`is_new`='1'  AND t.`del_flag`='0'  AND t.`country` IN ('de','uk','com','jp')   "+
				  "	AND EXISTS (SELECT 1 FROM psi_product_tiered_price p WHERE p.`product_id`=t.`product_id` AND p.`price` IS NOT NULL AND p.`del_flag`='0') ";
		return productEliminateDao.findBySql(sql);
	}
}
