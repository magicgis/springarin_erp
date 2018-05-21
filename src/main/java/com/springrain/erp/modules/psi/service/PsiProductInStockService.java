package com.springrain.erp.modules.psi.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.psi.dao.PsiProductInStockDao;
import com.springrain.erp.modules.psi.entity.ProductSalesInfo;
import com.springrain.erp.modules.psi.entity.PsiInventoryWarn;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiProductInStock;

/**
 *	产品历史库存Service
 */
@Component
@Transactional(readOnly = true)
public class PsiProductInStockService extends BaseService {

	@Autowired
	private PsiProductInStockDao psiProductInStockDao;
	@Autowired
	private PsiInventoryService psiInventoryService;
	
	@Autowired
	private PsiProductTieredPriceService psiTieredPriceService;
	
	@Autowired
	private ProductSalesInfoService productSalesInfoService;
	
	@Transactional(readOnly = false)
	public void save(PsiProductInStock psiProductInStock) {
		psiProductInStockDao.save(psiProductInStock);
	}
	
	@Transactional(readOnly = false)
	public void save(List<PsiProductInStock> psiProductInStocks) {
		psiProductInStockDao.save(psiProductInStocks);
	}
	
	public Map<String, Map<String, List<String>>> getSkuAndBarcode(){
		Map<String, Map<String, List<String>>> rs = Maps.newHashMap();
		String sql = "SELECT t.`sku`,b.`barcode`,t.`country`,t.`product_name` ,t.`color`,t.`update_user`"
					+ " FROM `psi_sku` t,`psi_barcode` b "
					+ " WHERE t.`del_flag`='0' AND b.`del_flag`='0' AND t.`use_barcode`='1' AND t.`barcode`=b.`id`";
		List<Object[]> list = psiProductInStockDao.findBySql(sql);
		for(Object[] obj : list){
			String sku = obj[0]==null?null : obj[0].toString();
			String barcode = obj[1]==null?null : obj[1].toString();
			String country = obj[2]==null?null : obj[2].toString();
			String product_name = obj[3]==null?null : obj[3].toString();
			String color = obj[4]==null?null : obj[4].toString();
			String userId = obj[5]==null?null : obj[5].toString();
			if (color != null && color.length() > 0) {
				product_name = product_name + "_" + color;
			}
			Map<String, List<String>> productMap = rs.get(product_name);
			if (productMap == null) {
				productMap = Maps.newHashMap();
				rs.put(product_name, productMap);
			}
			List<String> infoList = productMap.get(country);
			if (infoList == null) {
				infoList = Lists.newArrayList();
				productMap.put(country, infoList);
			}
			infoList.add(sku);
			infoList.add(barcode);
			infoList.add(userId);
		}
		return rs;
	}
	
	public Map<String, List<Float>> getProductPrice(){
		Map<String, List<Float>> rs = Maps.newHashMap();
//		String sql ="SELECT t.`product_id`,t.`price`,t.`rmb_price` FROM psi_product_supplier t WHERE t.`product_id` IS NOT NULL";
//		List<Object[]> list = psiProductInStockDao.findBySql(sql);
//		for(Object[] obj : list){
//			Integer productId = Integer.parseInt(obj[0].toString());
//			Float price = ((BigDecimal)(obj[1]==null?new BigDecimal(0):obj[1])).floatValue();
//			Float rmbRrice = ((BigDecimal)(obj[2]==null?new BigDecimal(0):obj[2])).floatValue();
//			
//			List<Float> priceList = rs.get(productId);
//			if (priceList == null) {
//				priceList = Lists.newArrayList();
//				priceList.add(price>0?price:null);
//				priceList.add(rmbRrice>0?rmbRrice:null);
//				rs.put(productId, priceList);
//			}
//		}
		//查看单价是否正确
		Map<String,Map<String,Float>> tieredMap = psiTieredPriceService.getPriceBaseMoqNoSupplier();
		for(Map.Entry<String,Map<String,Float>> entry:tieredMap.entrySet()){
			String proColor = entry.getKey();
			List<Float> priceList = Lists.newArrayList();
			priceList.add(entry.getValue().get("USD"));
			priceList.add(entry.getValue().get("CNY"));
			rs.put(proColor, priceList);
		}
		
		return rs;
	}
	
	public Map<String,PsiProductInStock> getHistoryInventory(String country){
		Map<String,PsiProductInStock> map=Maps.newHashMap();
        String sql="SELECT MAX(data_date) FROM psi_product_in_stock where country=:p1";
		List<Object> rs=psiProductInStockDao.findBySql(sql,new Parameter(country));
		if(rs.size()>0){
			Date date=(Date)rs.get(0);
			if(date!=null){
				DetachedCriteria dc = psiProductInStockDao.createDetachedCriteria();
				dc.add(Restrictions.eq("dataDate",date));
				dc.add(Restrictions.eq("country",country));
				List<PsiProductInStock> list =psiProductInStockDao.find(dc);
				for (PsiProductInStock psiProductInStock : list) {
					map.put(psiProductInStock.getProductName(), psiProductInStock);
				}
 			}
		}	
		return map;
	}
	
	public Map<String,Map<String,PsiProductInStock>> getHistoryInventory(){
		Map<String,Map<String,PsiProductInStock>> map=Maps.newHashMap();
		String sql="SELECT MAX(data_date) FROM psi_product_in_stock";
		List<Object> rs=psiProductInStockDao.findBySql(sql);
		if(rs.size()>0){
			Date date=(Date)rs.get(0);
			if(date!=null){
				DetachedCriteria dc = psiProductInStockDao.createDetachedCriteria();
				dc.add(Restrictions.eq("dataDate",date));
				List<PsiProductInStock> list =psiProductInStockDao.find(dc);
				for (PsiProductInStock psiProductInStock : list) {
					Map<String,PsiProductInStock> temp=map.get(psiProductInStock.getProductName());
					if(temp==null){
						temp=Maps.newHashMap();
						map.put(psiProductInStock.getProductName(),temp);
					}
					temp.put(psiProductInStock.getCountry(), psiProductInStock);
				}
 			}
		}	
		return map;
	}
	
	public PsiProductInStock getPsiProductInStock(String country,String productName){
		String sql="SELECT MAX(data_date) FROM psi_product_in_stock where product_name=:p1 and country=:p2 ";
		List<Object> rs=psiProductInStockDao.findBySql(sql,new Parameter(productName,country));
		if(rs.size()>0){
			Date date=(Date)rs.get(0);
			if(date!=null){
				DetachedCriteria dc = psiProductInStockDao.createDetachedCriteria();
				dc.add(Restrictions.eq("dataDate",date));
				dc.add(Restrictions.eq("productName",productName));
				dc.add(Restrictions.eq("country",country));
				List<PsiProductInStock> list =psiProductInStockDao.find(dc);
				if(list!=null&&list.size()>0){
					return list.get(0);
				}
 			}
		}	
		return null;
	}
	
	
	public Map<String,Map<String,PsiInventoryWarn>> getSafeInventory(){
		List<PsiInventoryWarn> list=psiInventoryService.getInventoryWarnList();
		Map<String,Map<String,PsiInventoryWarn>> map=Maps.newHashMap();
		/*String sql="select country,product_name,safe_inventory,total_stock from psi_product_in_stock WHERE  DATE_FORMAT(data_date,'%Y-%m-%d')=DATE_FORMAT((DATE_SUB(NOW(),INTERVAL 1 DAY)),'%Y-%m-%d') group by product_name,country";
		List<Object[]> list = psiProductInStockDao.findBySql(sql);
		for(Object[] obj : list){
			Map<String,Object[]> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(), temp);
			}
			temp.put(obj[1].toString(),obj);
		}*/
		for(PsiInventoryWarn warn: list){
			Map<String,PsiInventoryWarn> temp=map.get(warn.getCountry());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(warn.getCountry(), temp);
			}
			temp.put(warn.getProductName(),warn);
		}
		return map;
	}
	
	//查询需要下单的产品
	public List<String> findOrderQuantity(){
		String sql = "SELECT t.`product_name` FROM `psi_product_in_stock` t WHERE TO_DAYS(NOW()) - TO_DAYS(t.`data_date`) <= 1 AND  t.`order_quantity`>0 GROUP BY t.`product_name`";
		return psiProductInStockDao.findBySql(sql);
	}
	
	//库销比[产品名[国家 	库销比]] 国家包含eu、total
	public Map<String, Map<String, Double>> getInventorySalesMonth(){
		Map<String, Map<String, Double>> rs = Maps.newHashMap();
        String sql="SELECT MAX(data_date) FROM psi_product_in_stock";
		List<Object> list = psiProductInStockDao.findBySql(sql);
		if(list.size()>0){
			Date date=(Date)list.get(0);
			if(date!=null){
				DetachedCriteria dc = psiProductInStockDao.createDetachedCriteria();
				dc.add(Restrictions.eq("dataDate",date));
				List<PsiProductInStock> inStockList =psiProductInStockDao.find(dc);
				for (PsiProductInStock psiProductInStock : inStockList) {
					Map<String, Double> map = rs.get(psiProductInStock.getProductName());
					if (map == null) {
						map = Maps.newHashMap();
						rs.put(psiProductInStock.getProductName(), map);
					}
					map.put(psiProductInStock.getCountry(), psiProductInStock.getInventorySaleMonth());
				}
 			}
		}	
		return rs;
	}
	
	//[数据节点标记[品类 [产品名称 [国家[库存/31日销	 数量]]]]
	public Map<String, Map<String, Map<String, Map<String, Map<String, Integer>>>>> getInventoryTypeByDate(String date){
		Map<String, Map<String, Map<String, Map<String, Map<String, Integer>>>>> map = Maps.newHashMap();
		if (date != null) {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			DetachedCriteria dc = psiProductInStockDao.createDetachedCriteria();
			dc.add(Restrictions.sqlRestriction("data_date IN ("+date+")"));
			List<PsiProductInStock> list =psiProductInStockDao.find(dc);
			//产品名_国家
			Map<String,	ProductSalesInfo> fancha = productSalesInfoService.findAll();
			for (PsiProductInStock psiProductInStock : list) {
				String flag = format.format(psiProductInStock.getDataDate());
				Map<String, Map<String, Map<String, Map<String, Integer>>>> flagMap = map.get(flag);
				if (flagMap == null) {
					flagMap = Maps.newHashMap();
					map.put(flag, flagMap);
				}
				String type = "2";	//默认为普通品
				if ("0".equals(psiProductInStock.getIsSale())) {
					type = "4";	//淘汰
				} else if ("1".equals(psiProductInStock.getIsNew())) {
					type = "3";	//新产品
				} else if ("1".equals(psiProductInStock.getIsMain())) {
					type = "1";	//主力畅销品
				}
				Map<String, Map<String, Map<String, Integer>>> typeMap = flagMap.get(type);
				if (typeMap == null) {
					typeMap = Maps.newHashMap();
					flagMap.put(type, typeMap);
				}
				Map<String, Map<String, Integer>> productNameMap = typeMap.get(psiProductInStock.getProductName());
				if (productNameMap == null) {
					productNameMap = Maps.newHashMap();
					typeMap.put(psiProductInStock.getProductName(), productNameMap);
				}

				if (psiProductInStock.getCountry().startsWith("com")) {
					Map<String, Integer> comValueMap = productNameMap.get("com");
					if (comValueMap == null) {
						comValueMap = Maps.newHashMap();
						productNameMap.put("com", comValueMap);
					}
					comValueMap.put("inventory", (comValueMap.get("inventory")==null?0:comValueMap.get("inventory")) + psiProductInStock.getTotalStock());
					if (comValueMap.get("day31sale") == null) {
						int day31saleCom = 0;
						if (fancha.get(psiProductInStock.getProductName() + "_com2") != null) {
							day31saleCom = fancha.get(psiProductInStock.getProductName() + "_com2").getDay31Sales();
						}
						comValueMap.put("day31sale", day31saleCom);
					}
				} else {
					Map<String, Integer> valueMap = Maps.newHashMap();
					productNameMap.put(psiProductInStock.getCountry(), valueMap);
					valueMap.put("inventory", psiProductInStock.getTotalStock());
					valueMap.put("day31sale", psiProductInStock.getDay31Sales());
				}
				
				if (Integer.parseInt(flag.replaceAll("-", "")) < 20160301) {
					Map<String, Integer> totalValueMap = productNameMap.get("total");
					if (totalValueMap == null) {
						totalValueMap = Maps.newHashMap();
						productNameMap.put("total", totalValueMap);
					}
					totalValueMap.put("inventory", (totalValueMap.get("inventory")==null?0:totalValueMap.get("inventory")) + psiProductInStock.getTotalStock());
					
					if ("de,uk,fr,it,es".contains(psiProductInStock.getCountry())) {
						Map<String, Integer> euValueMap = productNameMap.get("eu");
						if (euValueMap == null) {
							euValueMap = Maps.newHashMap();
							productNameMap.put("eu", euValueMap);
						}
						euValueMap.put("inventory", (euValueMap.get("inventory")==null?0:euValueMap.get("inventory")) + psiProductInStock.getTotalStock());
						if (euValueMap.get("day31sale") == null) {
							int day31saleEu = 0;
							if (fancha.get(psiProductInStock.getProductName() + "_eu") != null) {
								day31saleEu = fancha.get(psiProductInStock.getProductName() + "_eu").getDay31Sales();
							}
							euValueMap.put("day31sale", day31saleEu);
						}
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 分国家查询各分类产品数量（total/de/com/jp）
	 * @param date
	 * @return[月份[数据节点标记[品类 [国家 数量]]]]
	 */
	public Map<String, Map<String, Map<String, Map<String, Integer>>>> getInventoryTypeNum(String date){
		Map<String, Map<String, Map<String, Map<String, Integer>>>> map = Maps.newHashMap();
		if (date != null) {
			String sql = "SELECT t.`data_date`,t.`country`,t.`is_main`,t.`is_new`,t.`is_sale` FROM `psi_product_in_stock` t WHERE "+
					" t.`country` IN('total','de','com','jp') AND t.`data_date` IN ("+date+")";
			List<Object[]> list =psiProductInStockDao.findBySql(sql);
			for (Object[] obj : list) {
				String flag = obj[0].toString();
				String month = flag.substring(0, 7);
				String country = obj[1].toString();
				String isMain = obj[2]==null?"0":obj[2].toString();
				String isNew = obj[3]==null?"0":obj[3].toString();
				String isSale = obj[4]==null?"1":obj[4].toString();
				Map<String, Map<String, Map<String, Integer>>> monthMap = map.get(month);
				if (monthMap == null) {
					monthMap = Maps.newHashMap();
					map.put(month, monthMap);
				}
				String type = "2";	//默认为普通品
				if ("0".equals(isSale)) {
					type = "4";	//淘汰
				} else if ("1".equals(isMain)) {
					type = "1";	//主力畅销品
				} else if ("1".equals(isNew)) {
					type = "3";	//新产品
				}
				String key = "1";//月底
				if (flag.endsWith("-15")) {
					key = "2";	//月中
				}
				Map<String, Map<String, Integer>> flagMap = monthMap.get(key);
				if (flagMap == null) {
					flagMap = Maps.newHashMap();
					monthMap.put(key, flagMap);
				}
				Map<String, Integer> typeNameMap = flagMap.get(type);
				if (typeNameMap == null) {
					typeNameMap = Maps.newHashMap();
					flagMap.put(type, typeNameMap);
				}
				Integer num = typeNameMap.get(country);
				if (num == null) {
					typeNameMap.put(country, 1);
				} else {
					typeNameMap.put(country, num +1);
				}
				//2016年3月之前没有汇总项,以美国数据为参考
				if (Integer.parseInt(flag.replaceAll("-", "").replaceAll("'", "")) < 20160301 && "com".equals(country)) {
					typeNameMap.put("total", typeNameMap.get(country));
				}
			}
		}
		return map;
	}

	public Map<String, Map<String, Integer>> findHistoryInventory(List<String> datesList) {
		Map<String, Map<String, Integer>> rs = Maps.newHashMap();
		if (datesList != null && datesList.size() > 0) {
			StringBuilder dates = new StringBuilder();
			for (String string : datesList) {
				dates.append("'").append(string).append("',");
			}
			String sql = "SELECT SUM(t.`total_stock`),DATE_FORMAT(t.`data_date`,'%Y-%m-%d') AS dates FROM `psi_product_in_stock` t "+
					" WHERE t.`country`!='total' AND t.`country`!='eu' AND t.`country`!='eunouk' AND DATE_FORMAT(t.`data_date`,'%Y-%m-%d') IN("+dates.substring(0,dates.length()-1)+") GROUP BY dates";
			List<Object[]> list = psiProductInStockDao.findBySql(sql);
			for (Object[] obj : list) {
				Integer inventory = Integer.parseInt(obj[0].toString());
				String date = obj[1].toString();
				String key = "1";//月底
				if (date.endsWith("-15")) {
					key = "2";	//月中
				}
				Map<String, Integer> map = rs.get(key);
				if(map == null){
					map = Maps.newHashMap();
					rs.put(key, map);
				}
				map.put(date.substring(0, 7), inventory);
			}
		}
		return rs;
	}

	/**
	 * 获取月末历史库存
	 * @param month 
	 * @return [产品 总库存] 国家分欧洲，日本，加拿大，美国
	 */
	public Map<String, Integer> findInventoryByMonth(String month, String country) {
		if ("us".equals(country)) {
			country = "com";
		}
		Map<String, Integer> rs = Maps.newLinkedHashMap();
		String temp = "";
		if ("eu".equals(country)) {
			temp = "AND country IN('de','fr','uk','it','es')";
		} else {
			temp = "AND country ='"+country+"' ";
		}
		String sql="SELECT product_name,SUM(total_stock-IFNULL(producting,0)) AS total FROM psi_product_in_stock WHERE data_date = (SELECT MAX(data_date) FROM psi_product_in_stock WHERE DATE_FORMAT(data_date, '%Y%m')=:p1)"+
				" AND (total_stock-IFNULL(producting,0))>0 "+temp+" GROUP BY product_name ORDER BY product_name";
		List<Object[]> list = psiProductInStockDao.findBySql(sql,new Parameter(month));
		for (Object[] obj : list) {
			String productName = obj[0].toString();
			Integer inventory = Integer.parseInt(obj[1].toString());
			rs.put(productName, inventory);
		}
		return rs;
	}

	/**
	 * 获取月初历史库存
	 * @param month 
	 * @return [产品 总库存] 国家分欧洲，日本，加拿大，美国
	 */
	public Map<String, Integer> findInventoryByMonthStart(String month, String country) {
		if ("us".equals(country)) {
			country = "com";
		}
		Map<String, Integer> rs = Maps.newLinkedHashMap();
		String temp = "";
		if ("eu".equals(country)) {
			temp = "AND country IN('de','fr','uk','it','es')";
		} else {
			temp = "AND country ='"+country+"' ";
		}
		String sql="SELECT product_name,SUM(total_stock-IFNULL(producting,0)) AS total FROM psi_product_in_stock WHERE data_date = (SELECT MIN(data_date) FROM psi_product_in_stock WHERE DATE_FORMAT(data_date, '%Y%m')=:p1)"+
				" AND (total_stock-IFNULL(producting,0))>0 "+temp+" GROUP BY product_name ORDER BY product_name";
		List<Object[]> list = psiProductInStockDao.findBySql(sql,new Parameter(month));
		for (Object[] obj : list) {
			String productName = obj[0].toString();
			Integer inventory = Integer.parseInt(obj[1].toString());
			rs.put(productName, inventory);
		}
		return rs;
	}

	/**
	 * 获取月末历史库存(包含库存为0的产品)
	 * @param month 
	 * @return [产品 总库存] 国家分欧洲，日本，加拿大，美国
	 */
	public Map<String, Integer> findInventoryByMonthEnd(String month, String country) {
		if ("us".equals(country)) {
			country = "com";
		}
		Map<String, Integer> rs = Maps.newLinkedHashMap();
		String temp = "";
		if ("eu".equals(country)) {
			temp = "AND country IN('de','fr','uk','it','es')";
		} else {
			temp = "AND country ='"+country+"' ";
		}
		String sql="SELECT product_name,SUM(total_stock-IFNULL(producting,0)) AS total FROM psi_product_in_stock WHERE data_date = (SELECT MAX(data_date) FROM psi_product_in_stock WHERE DATE_FORMAT(data_date, '%Y%m')=:p1)"+
				" "+temp+" GROUP BY product_name ORDER BY product_name";
		List<Object[]> list = psiProductInStockDao.findBySql(sql,new Parameter(month));
		for (Object[] obj : list) {
			String productName = obj[0].toString();
			Integer inventory = Integer.parseInt(obj[1].toString());
			rs.put(productName, inventory);
		}
		return rs;
	}

	/**
	 * 获取年度最早的库存数据
	 * @param year 
	 * @param country 
	 * @param productName 
	 * @return  总库存
	 */
	public Integer findEarlyHistoryInventory(String year, String country, String productName) {
		String sql="SELECT total_stock-IFNULL(producting,0) AS total FROM psi_product_in_stock"+ 
				" WHERE data_date LIKE :p1 AND total_stock-IFNULL(producting,0)>0 "+
				" AND country=:p2 AND product_name=:p3"+
				" GROUP BY product_name,data_date ORDER BY data_date LIMIT 1";
		List<Object> list = psiProductInStockDao.findBySql(sql,new Parameter(year+"%", country, productName));
		if (list != null && list.size() > 0) {
			return Integer.parseInt(list.get(0).toString());
		}
		return 0;
	}

	/**
	 * 获取月初&月末历史价格
	 * @param month 
	 * @param country 国家分欧洲，日本，加拿大，美国
	 * @return [类型[产品 价格map] 类型 0：月初价格   1:月末价格
	 */
	public Map<String, Map<String, Map<String, Float>>> findPriceByMonth(String month, String country) {
		List<String> countrys = Lists.newArrayList();
		if ("us".equals(country)) {
			countrys.add("com");
		} else if ("eu".equals(country)) {
			countrys.add("de");
			countrys.add("uk");
		} else {
			countrys.add(country);
		}
		Map<String, Map<String, Map<String, Float>>> rs = Maps.newLinkedHashMap();
		String sql="SELECT t.`product_name`,max(ifnull(t.`price`,0)),max(ifnull(t.`rmb_price`,0)) FROM `psi_product_in_stock` t WHERE t.`country`in :p1 "+
				" AND t.`data_date`=(SELECT MIN(data_date) FROM psi_product_in_stock WHERE DATE_FORMAT(data_date, '%Y%m')=:p2) group by t.`product_name`";
		List<Object[]> list = psiProductInStockDao.findBySql(sql,new Parameter(countrys, month));
		Map<String, Map<String, Float>> sMap = Maps.newHashMap();
		rs.put("0", sMap);
		for (Object[] obj : list) {
			String productName = obj[0].toString();
			float price = obj[1]==null?0:Float.parseFloat(obj[1].toString());
			float rmbPrice = obj[2]==null?0:Float.parseFloat(obj[2].toString());
			Map<String, Float> map = Maps.newHashMap();
			map.put("price", price);
			map.put("rmbPrice", rmbPrice);
			sMap.put(productName, map);
		}
		sql="SELECT t.`product_name`,max(ifnull(t.`price`,0)),max(ifnull(t.`rmb_price`,0)) FROM `psi_product_in_stock` t WHERE t.`country` in :p1 "+
				" AND t.`data_date`=(SELECT MAX(data_date) FROM psi_product_in_stock WHERE DATE_FORMAT(data_date, '%Y%m')=:p2) group by t.`product_name`";
		list = psiProductInStockDao.findBySql(sql,new Parameter(countrys, month));
		Map<String, Map<String, Float>> eMap = Maps.newHashMap();
		rs.put("1", eMap);
		for (Object[] obj : list) {
			String productName = obj[0].toString();
			float price = obj[1]==null?0:Float.parseFloat(obj[1].toString());
			float rmbPrice = obj[2]==null?0:Float.parseFloat(obj[2].toString());
			Map<String, Float> map = Maps.newHashMap();
			map.put("price", price);
			map.put("rmbPrice", rmbPrice);
			eMap.put(productName, map);
		}
		return rs;
	}
	
	/**
	 * 按天重新更新中国仓（Lc）数据
	 * 7.26
	 * @throws ParseException 
	 */
	@Transactional(readOnly = false)
	public void  initClData() throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = sdf.parse("2016-07-20");
		Date endDate =  sdf.parse("2016-10-31");
		while(startDate.before(endDate)){
			//获取当天各产品的库存
			Map<String,Integer> inventoryMap = this.getInventoryNumsByDate(startDate);
			//获取历史记录的产品信息
			if(inventoryMap!=null){
				List<PsiProductInStock> stocks=getHistoryInventoryByDate(startDate);
				for(PsiProductInStock inStock : stocks){
					String key=inStock.getProductName()+","+inStock.getCountry();
					if(inventoryMap.get(key)!=null){
						inStock.setCnLc(inventoryMap.get(key));//补录理诚库存    
						this.save(inStock);
					}   
				}
			}
			startDate=DateUtils.addDays(startDate, 1);
		}
	}
	
	@Transactional(readOnly = false)
	public void  saveAllData(List<PsiProductInStock> inStocks){
		this.psiProductInStockDao.save(inStocks);
	}
	
	
	public Map<String, Integer> getInventoryNumsByDate(Date date) {
		Map<String, Integer> rs = Maps.newHashMap();
		String sql="SELECT CASE WHEN a.`color_code`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END ,a.`country_code`,SUM(a.`quantity`) FROM psi_inventory_revision_log AS a WHERE a.`warehouse_id`='130'" +
				" AND a.`data_type`='new' AND a.`operatin_date`<=:p1 GROUP BY a.`product_name`,a.`color_code`,a.`country_code`";
		List<Object[]> list = psiProductInStockDao.findBySql(sql,new Parameter(date));
		for (Object[] obj : list) {
			String proNameColor=obj[0].toString();
			String country=obj[1].toString();
			Integer curQ = Integer.parseInt(obj[2].toString());
			Integer q = Integer.parseInt(obj[2].toString());
			String key=proNameColor+","+country;
			if(rs.get(key)!=null){
				q+=rs.get(key);
			}
			rs.put(key, q);
			
			if("fr,de,uk,it,es".contains(country)){
				key=proNameColor+","+"eu";
				if(rs.get(key)!=null){
					rs.put(key, rs.get(key)+curQ);
				}else{
					rs.put(key,curQ);
				}
			}
			
			key=proNameColor+","+"total";
			if(rs.get(key)!=null){
				rs.put(key, rs.get(key)+curQ);
			}else{
				rs.put(key,curQ);
			}
		}
		return rs;
	}
	
	
	public List<PsiProductInStock> getHistoryInventoryByDate(Date date){
		DetachedCriteria dc = psiProductInStockDao.createDetachedCriteria();
		dc.add(Restrictions.eq("dataDate",date));
		return psiProductInStockDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void updateTypeUserName(PsiProductInStock inStock){
		String sql="UPDATE psi_product_in_stock SET sale_user=:p1,cameraman=:p2,purchase_user=:p3,customer=:p4,merchandiser=:p5,product_manager=:p6 WHERE product_name=:p7 and country=:p8 ";
		psiProductInStockDao.updateBySql(sql,new Parameter(inStock.getSaleUser(),inStock.getCameraman(),inStock.getPurchaseUser(),inStock.getCustomer(),inStock.getMerchandiser(),inStock.getProductManager(),inStock.getProductName(),inStock.getCountry()));
	}

	public Map<String,Map<String,Float>> findYearTurnoverStarand(Date start,Date end,Map<String,PsiProductEliminate> eliminateMap){
		Map<String,Map<String,Float>> map=Maps.newHashMap();
		/*String sql="SELECT p.`product_name`,p.`country`,DATE_FORMAT(p.`data_date`,'%Y%m') dates,ROUND(AVG(365/(IFNULL(p.`safe_day`,0)+IFNULL(p.`period`,0)+IFNULL(a.`buffer_period`,0))),2) turnover FROM psi_product_in_stock p "+
		" join psi_product_eliminate a on p.`product_name`=(CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END) and a.del_flag='0'   "+
				 " AND CASE WHEN p.`country`='eu' THEN CASE WHEN p.`product_name` LIKE '%uk%' THEN 'uk' ELSE 'de' END ELSE p.`country` END =a.`country`"+
                 " WHERE p.`country` IN ('eu','com') AND p.`data_date`>=:p1 and p.`data_date`<=:p2 GROUP BY p.`product_name`,p.`country`,dates ";
		*/
		String sql="SELECT p.`product_name`,p.`country`,DATE_FORMAT(p.`data_date`,'%Y%m') dates,avg(IFNULL(p.`safe_day`,0)+IFNULL(p.`period`,0)) turnover FROM psi_product_in_stock p "+
		           " WHERE p.`data_date`>=:p1 and p.`data_date`<=:p2 and  p.`country` IN ('eu','com') GROUP BY p.`product_name`,p.`country`,dates ";
				
		List<Object[]> list = psiProductInStockDao.findBySql(sql,new Parameter(start,end));
		Calendar calendar = Calendar.getInstance();  
	    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMM");   
	        
		for (Object[] obj: list) {
			String name=obj[0].toString();
			String country=obj[1].toString();
			String date=obj[2].toString();
			if(obj[3]!=null){
				Float period=Float.parseFloat(obj[3].toString());
				if("eu".equals(country)){
					if(name.endsWith("UK")&&eliminateMap.get(name+"_uk")!=null){
						period+=eliminateMap.get(name+"_uk").getBufferPeriod();
					}else if(eliminateMap.get(name+"_de")!=null){
						period+=eliminateMap.get(name+"_de").getBufferPeriod();
					}
				}else if(eliminateMap.get(name+"_"+country)!=null){
					period+=eliminateMap.get(name+"_"+country).getBufferPeriod();
				}
				try {
					calendar.setTime(dateFormat.parse(date));
					
				} catch (ParseException e) {
					e.printStackTrace();
				}  
				Float turnover=365f/period;				
				
				Map<String,Float> temp=map.get(date);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(date, temp);
				}
				temp.put(name+"_"+country,turnover);
			}
		}
		return map;
	}

	/**
	 * 库存量前20
	 * @return
	 */
	public List<PsiProductInStock> findTopInventory(){
		List<PsiProductInStock> rs = Lists.newArrayList();
		String sql="SELECT t.`product_name`,t.`total_stock`,t.`day31_sales` FROM `psi_product_in_stock` t "+
				" WHERE t.`data_date`=(SELECT MAX(s.`data_date`) FROM `psi_product_in_stock` s) AND t.`country`='total' "+
				" ORDER BY t.`total_stock` DESC LIMIT 20";
		List<Object[]> list = psiProductInStockDao.findBySql(sql);
		
		for (Object[] obj: list) {
			String productName=obj[0].toString();
			Integer totalStock= Integer.parseInt(obj[1].toString());
			Integer day31Sales= Integer.parseInt(obj[2].toString());
			PsiProductInStock stock = new PsiProductInStock();
			stock.setProductName(productName);
			stock.setTotalStock(totalStock);
			stock.setDay31Sales(day31Sales);
			rs.add(stock);
		}
		return rs;
	}

	/**
	 * 库销比前20(不含新品及总库存低于500的产品)
	 * @return
	 */
	public List<PsiProductInStock> findTopRatio(){
		List<PsiProductInStock> rs = Lists.newArrayList();
		String sql="SELECT t.`product_name`,t.`total_stock`,t.`day31_sales`, "+
				" CASE WHEN t.`day31_sales`=0 THEN t.`total_stock`*10000 ELSE t.`total_stock`/t.`day31_sales` END AS rate "+
				" FROM `psi_product_in_stock` t "+
				" WHERE t.`data_date`=(SELECT MAX(s.`data_date`) FROM `psi_product_in_stock` s) AND t.`country`='total' AND t.`total_stock`>=500 AND t.`is_new`='0'"+
				" ORDER BY rate DESC LIMIT 20";
		List<Object[]> list = psiProductInStockDao.findBySql(sql);
		
		for (Object[] obj: list) {
			String productName=obj[0].toString();
			Integer totalStock= Integer.parseInt(obj[1].toString());
			Integer day31Sales= Integer.parseInt(obj[2].toString());
			Float rate= Float.parseFloat(obj[3].toString());
			PsiProductInStock stock = new PsiProductInStock();
			stock.setProductName(productName);
			stock.setTotalStock(totalStock);
			stock.setDay31Sales(day31Sales);
			stock.setInventorySaleMonth(rate);
			rs.add(stock);
		}
		return rs;
	}
	
	public static void main(String args []) throws ParseException{
		new PsiProductInStockService().initClData();
	}
}
