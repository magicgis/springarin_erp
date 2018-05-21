package com.springrain.erp.modules.psi.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.SaleProfit;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.dao.PsiInventoryTurnoverDataDao;
import com.springrain.erp.modules.psi.entity.PsiInventoryTurnoverData;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;

/**
 * 库存周转率Service
 */
@Component
@Transactional(readOnly = true)
public class PsiInventoryTurnoverDataService extends BaseService {
	
	@Autowired
	private PsiInventoryTurnoverDataDao turnoverDataDao;
	
	@Autowired
	private PsiProductInStockService productInStockService;
	
	@Autowired
	private PsiProductTieredPriceService psiTieredPriceService;
	@Autowired
	private PsiProductEliminateService 		psiProductEliminateService;

	@Transactional(readOnly = false)
	public void saveTurnoverData(PsiInventoryTurnoverData data) {
		turnoverDataDao.save(data);
	}
	
	@Transactional(readOnly = false)
	public void saveTurnoverDataList(List<PsiInventoryTurnoverData> dataList) {
		turnoverDataDao.save(dataList);
	}
	
	/**
	 * 保存指定月份的产品库存等信息,用于统计存货周转率(月存货周转率 = 月销售成本/月均存货)2、月均存货 = （月初存货金额(上月底) + 月末存货金额）/ 2 
	 * @param month1 上月时间 yyyyMM
	 * @param month2 本月时间 (即计算周转率的月份)yyyyMM
	 */
	@Transactional(readOnly = false)
	public void saveTurnoverDataByMonth(String month1, String month2) {
		List<String> countryList = Lists.newArrayList("com","de","fr","uk","it","es","ca","jp","mx","com2");
		//产品当前月销量[国家[产品  销量]]
		Map<String, Map<String, SaleProfit>> salesMap = getSalseByMonth(month2, "0");
		List<PsiInventoryTurnoverData> list = Lists.newArrayList();
		//月初价格(不含税CNY)直接取上个月数据
		Map<String, Float> hisPriceMap = getHisPrice(month1);
		//月末价格(不含税CNY)当前采购价
		Map<String, Float> priceMap = psiTieredPriceService.getNoTaxCnyPriceNoSupplier();
		for (String country : countryList) {
			//月初历史库存(即上月末库存排除在产)
			Map<String, Integer> startInventory = productInStockService.findInventoryByMonthEnd(month1, country);
			//月末历史库存(排除在产)
			Map<String, Integer> endInventory = productInStockService.findInventoryByMonthEnd(month2, country);
			Map<String, SaleProfit> countryMap = salesMap.get(country);
			if (countryMap == null) { //没有销量
				continue;
			}
			for (Map.Entry<String, SaleProfit> entry: countryMap.entrySet()) {
				String productName =entry.getKey();
				PsiInventoryTurnoverData data = new PsiInventoryTurnoverData();
				SaleProfit profit = entry.getValue();
				String type = profit.getType();
				String line = profit.getLine();
				data.setProductName(productName);
				data.setCountry(country);
				data.setProductType(type);
				data.setLine(line);
				data.setMonth(month2);
				data.setDataType("0");	//月度数据
				Integer salesVolume = profit.getSalesVolume();
				data.setSalesVolume(salesVolume);
				//泛欧产品只有de有库存
				Integer sQuantity = startInventory.get(productName)==null?0:startInventory.get(productName);
				Integer eQuantity = endInventory.get(productName)==null?0:endInventory.get(productName);
				data.setsQuantity(sQuantity);
				data.seteQuantity(eQuantity);
				Float ePrice = priceMap.get(productName);
				if (ePrice == null) {	//没有价格不考虑
					continue;
				}
				data.setePrice(ePrice);
				Float sPrice = hisPriceMap.get(productName);
				if (sPrice == null) {
					sPrice = ePrice;	//上个月没有价格,直接用当前价
				}
				data.setsPrice(sPrice);
				//计算月存货周转率（存货周转率=该产品当月销售成本/[（月初余额+月末余额）/2]）
				float avgPrice = (sPrice + ePrice) /2;
				if (sQuantity>0 && eQuantity>0) {
					float turnRate = salesVolume * avgPrice * 2 /(sQuantity * sPrice + eQuantity * ePrice);
					data.setRate(turnRate);
				}
				list.add(data);
			}
		}
		saveTurnoverDataList(list);
	}
	
	/**
	 * 保存指定年的产品库存等信息,用于统计存货周转率(月存货周转率 = 月销售成本/月均存货)2、月均存货 = （月初存货金额(上月底) + 月末存货金额）/ 2 
	 * @param year1 上年时间 yyyy
	 * @param year2 本年时间 (即计算周转率的月份)yyyy
	 */
	@Transactional(readOnly = false)
	public void saveTurnoverDataByYear(String year1, String year2) {
		List<String> countryList = Lists.newArrayList("com","de","fr","uk","it","es","ca","jp");
		//产品当前年销量[国家[产品  销量]]
		Map<String, Map<String, SaleProfit>> salesMap = getSalseByMonth(year2, "1");
		List<PsiInventoryTurnoverData> list = Lists.newArrayList();
		//年初价格(不含税CNY)直接取1月份数据
		Map<String, Float> hisPriceMap = getHisPrice(year2+"01");
		//年末价格(不含税CNY)当前采购价
		Map<String, Float> priceMap = psiTieredPriceService.getNoTaxCnyPriceNoSupplier();
		for (String country : countryList) {
			//年初历史库存(即上年末库存排除在产),为空则找最早的库存数
			Map<String, Integer> startInventory = productInStockService.findInventoryByMonthEnd(year1+"12", country);
			//年末历史库存(排除在产)
			Map<String, Integer> endInventory = productInStockService.findInventoryByMonthEnd(year2+"12", country);
			Map<String, SaleProfit> countryMap = salesMap.get(country);
			for (Map.Entry<String, SaleProfit> entry: countryMap.entrySet()) {
				String productName =entry.getKey();
				PsiInventoryTurnoverData data = new PsiInventoryTurnoverData();
				SaleProfit profit = entry.getValue();
				String type = profit.getType();
				String line = profit.getLine();
				data.setProductName(productName);
				data.setCountry(country);
				data.setProductType(type);
				data.setLine(line);
				data.setMonth(year2);
				data.setDataType("1");	//年度数据
				Integer salesVolume = profit.getSalesVolume();
				//泛欧产品只有de有库存
				Integer sQuantity = startInventory.get(productName);
				Integer eQuantity = endInventory.get(productName);
				if (eQuantity == null) {
					eQuantity = 0;
				}
				if (sQuantity == null) {	//年初没有改产品,查询该产品最早的库存
					//logger.info(productName);
					sQuantity = productInStockService.findEarlyHistoryInventory(productName, year2, country);
				}
				data.setSalesVolume(salesVolume);
				data.setsQuantity(sQuantity);
				data.seteQuantity(eQuantity);
				Float ePrice = priceMap.get(productName);
				if (ePrice == null) {	//没有价格不考虑
					continue;
				}
				data.setePrice(ePrice);
				Float sPrice = hisPriceMap.get(productName);
				if (sPrice == null) {
					sPrice = ePrice;	//上个月没有价格,直接用当前价
				}
				data.setsPrice(sPrice);
				//计算月存货周转率（存货周转率=该产品当月销售成本/[（月初余额+月末余额）/2]）
				float avgPrice = (sPrice + ePrice) /2;
				if (sQuantity>0 && eQuantity>0) {
					float turnRate = salesVolume * avgPrice * 2 /(sQuantity * sPrice + eQuantity * ePrice);
					data.setRate(turnRate);
				}
				list.add(data);
			}
		}
		saveTurnoverDataList(list);
	}

	/**
	 * 获取指定时间的销量
	 * @param dateStr 月-yyyyMM 年-yyyy
	 * @param flag 默认：月销量  1：年销量
	 * @return[国家[产品  销量]]
	 */
	public Map<String, Map<String, SaleProfit>> getSalseByMonth(String dateStr, String flag) {
		Map<String, Map<String, SaleProfit>> rs = Maps.newHashMap();
		String sql = "SELECT t.`product_name`,t.`type`,t.`line`,t.`sales_volume`,t.`country`"+ 
				" FROM `amazoninfo_report_month_type` t WHERE t.`month`=:p1 AND t.`sales_volume` > 0";
		if ("1".equals(flag)) {	//年销量
			dateStr = dateStr + "%";
			sql = "SELECT t.`product_name`,t.`type`,t.`line`,SUM(t.`sales_volume`),t.`country`"+ 
					" FROM `amazoninfo_report_month_type` t WHERE t.`month` LIKE :p1 AND t.`sales_volume` > 0"+ 
					" GROUP BY t.`product_name`,t.`country`";
		}
		List<Object[]> list = turnoverDataDao.findBySql(sql, new Parameter(dateStr));
		for (Object[] obj : list) {
			SaleProfit profit = new SaleProfit();
			String productName = obj[0].toString();
			String type = obj[1].toString();
			String line = obj[2].toString();
			Integer salesVolume = Integer.parseInt(obj[3].toString());
			String country = obj[4].toString();
			profit.setCountry(country);
			profit.setProductName(productName);
			profit.setType(type);
			profit.setLine(line);
			profit.setSalesVolume(salesVolume);
			Map<String, SaleProfit> countryMap = rs.get(country);
			if (countryMap == null) {
				countryMap = Maps.newHashMap();
				rs.put(country, countryMap);
			}
			countryMap.put(productName, profit);
		}
		return rs;
	}

	/**
	 * 获取产品的采购价
	 * @param month
	 * @return
	 */
	public Map<String, Float> getHisPrice(String month) {
		Map<String, Float> rs = Maps.newHashMap();
		String sql = "SELECT t.`product_name`,t.`e_price` FROM psi_inventory_turnover_data t WHERE t.`month`=:p1";
		List<Object[]> list = turnoverDataDao.findBySql(sql, new Parameter(month));
		for (Object[] obj : list) {
			String productName = obj[0].toString();
			Float price = Float.parseFloat(obj[1].toString());
			rs.put(productName, price);
		}
		return rs;
	}
	
	/**
	 * 查询周转率
	 * @param saleProfit
	 * @param flag	1:按月  3:按年
	 * @param groupType	默认分产品   1:分产品类型  2：分产品线
	 * @return map [时间 [产品/类型/线  周转率]]
	 */
	public Map<String, Map<String, Float>> getTurnoverRateList(SaleProfit saleProfit, String flag, String groupType){
		Map<String, Map<String, Float>> map = Maps.newHashMap();
		String temp = "";
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
				&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry()) && !"nonEn".equals(saleProfit.getCountry()) && !"noUs".equals(saleProfit.getCountry())) {
			temp = " AND t.`country`='"+saleProfit.getCountry()+"' ";
		} else if ("en".equals(saleProfit.getCountry())) {
			temp = " AND t.`country` in ('com','uk','ca') ";
		} else if ("eu".equals(saleProfit.getCountry())) {
			temp = " AND t.`country` in ('de','uk','fr','it','es') ";
		} else if ("nonEn".equals(saleProfit.getCountry())) {	//非英语国家
			temp = " AND t.`country` in ('de','fr','it','es','jp') ";
		} else if ("noUs".equals(saleProfit.getCountry())) {	//非美国
			temp = " AND t.`country` in ('de','uk','fr','it','es','jp','ca','mx') ";
		}
		String temp1 = " t.`product_name` ";
		if ("1".equals(groupType)) {
			temp1 = " t.`product_type` ";
		} else if ("2".equals(groupType)) {
			temp1 = " t.`line` ";
		}
		String sql = "";
		Parameter parameter = new Parameter(saleProfit.getDay(), saleProfit.getEnd());
		if (StringUtils.isNotEmpty(flag) && "1".equals(flag)) {	//按月
			sql ="SELECT t.`month`,t.`product_name`,t.`product_type`,t.`line`,SUM(t.`sales_volume`*(t.`s_price`+t.`e_price`)) AS sales, "+
				" SUM(t.s_quantity*t.s_price+t.e_quantity*t.e_price) AS inventory"+
				" FROM `psi_inventory_turnover_data` t WHERE t.`data_type`='0' AND t.month>=:p1 AND t.`month`<=:p2 " +temp+
				" GROUP BY t.month,"+temp1;
		} else {	//按年
			sql ="SELECT t.`month`,t.`product_name`,t.`product_type`,t.`line`,SUM(t.`sales_volume`*(t.`s_price`+t.`e_price`)) AS sales, "+
					" SUM(t.s_quantity*t.s_price+t.e_quantity*t.e_price) AS inventory"+
					" FROM `psi_inventory_turnover_data` t WHERE t.`data_type`='1' AND t.month>=:p1 AND t.`month`<=:p2 " +temp+
					" GROUP BY t.month,"+temp1;
		}
		float totalSales = 0f;
		float totalInventorys = 0f;
		List<Object[]> list = turnoverDataDao.findBySql(sql, parameter);
		for (Object[] obj : list) {
			String month = obj[0].toString();
			String productName = obj[1].toString();
			String productType = obj[2].toString();
			String line = obj[3].toString();
			if ("1".equals(groupType)) {	//按类型
				productName = productType;
			} else if ("2".equals(groupType)) {	//按产品线
				productName = line;
			}
			Float sales = obj[4]==null?0:Float.parseFloat(obj[4].toString());
			Float inventory = obj[5]==null?0:Float.parseFloat(obj[5].toString());
			totalSales += sales;
			totalInventorys += inventory;
			Map<String, Float> monthMap = map.get(month);
			if (monthMap == null) {
				monthMap = Maps.newHashMap();
				map.put(month, monthMap);
			}
			if (inventory > 0) {
				monthMap.put(productName, sales/inventory);
			}
		}
		if (totalInventorys > 0) {
			Map<String, Float> totalMap = Maps.newHashMap();
			totalMap.put("total", totalSales/totalInventorys);
			map.put("total", totalMap);
		}
		return map;
	}
	
	/**
	 * 计算库存周转率
	 * @param start	起始时间	按月 :yyyyMM 按年:yyyy 
	 * @param end	截止时间	按月 :yyyyMM 按年:yyyy
	 * @param flag	1:按月  2：按年
	 * @param groupType	默认分产品   1:分产品类型  2：分产品线
	 * @return map [时间 [国家[产品/类型/线  PsiInventoryTurnoverData]]] 周转率data.getsPrice()/data.getePrice()
	 */
	public Map<String, Map<String, Map<String, PsiInventoryTurnoverData>>> getTurnoverRate(String start, String end, String flag, String groupType){
		Map<String, Map<String, Map<String, PsiInventoryTurnoverData>>> map = Maps.newHashMap();
		String temp1 = " t.`product_name` ";
		if ("1".equals(groupType)) {
			temp1 = " t.`product_type` ";
		} else if ("2".equals(groupType)) {
			temp1 = " t.`line` ";
		}
		String sql = "";
		Parameter parameter = new Parameter(start, end);
		if (StringUtils.isNotEmpty(flag) && "1".equals(flag)) {	//按月
			sql ="SELECT t.`month`,t.`product_name`,t.`product_type`,t.`line`,SUM(t.`sales_volume`*(t.`s_price`+t.`e_price`)) AS sales, "+
				" SUM(t.s_quantity*t.s_price+t.e_quantity*t.e_price) AS inventory,t.`country`"+
				" FROM `psi_inventory_turnover_data` t WHERE t.`data_type`='0' AND t.month>=:p1 AND t.`month`<=:p2 and t.country not in ('fr','it','es','uk') "+
				" GROUP BY t.month,t.`country`,"+temp1;
		} else {	//按年
			sql ="SELECT t.`month`,t.`product_name`,t.`product_type`,t.`line`,SUM(t.`sales_volume`*(t.`s_price`+t.`e_price`)) AS sales, "+
					" SUM(t.s_quantity*t.s_price+t.e_quantity*t.e_price) AS inventory,t.`country`"+
					" FROM `psi_inventory_turnover_data` t WHERE t.`data_type`='1' AND t.month>=:p1 AND t.`month`<=:p2 and t.country not in ('fr','it','es','uk') "+
					" GROUP BY t.month,t.`country`,"+temp1;
		}
		List<Object[]> list = turnoverDataDao.findBySql(sql, parameter);
		for (Object[] obj : list) {
			String month = obj[0].toString();
			String productName = obj[1].toString();
			String productType = obj[2].toString();
			String line = obj[3].toString();
			if ("1".equals(groupType)) {	//按类型
				productName = productType;
			} else if ("2".equals(groupType)) {	//按产品线
				productName = line;
			}
			Float sales = obj[4]==null?0:Float.parseFloat(obj[4].toString());
			Float inventory = obj[5]==null?0:Float.parseFloat(obj[5].toString());
			String country = obj[6].toString();
			
			Map<String, Map<String, PsiInventoryTurnoverData>> monthMap = map.get(month);
			if (monthMap == null) {
				monthMap = Maps.newHashMap();
				map.put(month, monthMap);
			}
			
			Map<String, PsiInventoryTurnoverData> countryMap = monthMap.get(country);
			if (countryMap == null) {
				countryMap = Maps.newHashMap();
				monthMap.put(country, countryMap);
			}
			PsiInventoryTurnoverData data = new PsiInventoryTurnoverData();
			data.setsPrice(sales);
			data.setePrice(inventory);
			countryMap.put(productName, data);
			//英语国家
			if ("com,uk,ca".contains(country)) {
				Map<String, PsiInventoryTurnoverData> enMap = monthMap.get("en");
				if (enMap == null) {
					enMap = Maps.newHashMap();
					monthMap.put("en", enMap);
				}
				PsiInventoryTurnoverData enData = enMap.get(productName);
				if (enData == null) {
					enData = new PsiInventoryTurnoverData();
					enData.setsPrice(sales);
					enData.setePrice(inventory);
					enMap.put(productName, enData);
				} else {
					enData.setsPrice(enData.getsPrice() + sales);
					enData.setePrice(enData.getePrice() + inventory);
				}
			}
			
			//非英语国家

			if ("de,fr,it,es,jp".contains(country)) {
				Map<String, PsiInventoryTurnoverData> nonEnMap = monthMap.get("nonEn");
				if (nonEnMap == null) {
					nonEnMap = Maps.newHashMap();
					monthMap.put("nonEn", nonEnMap);
				}
				PsiInventoryTurnoverData nonEnData = nonEnMap.get(productName);
				if (nonEnData == null) {
					nonEnData = new PsiInventoryTurnoverData();
					nonEnData.setsPrice(sales);
					nonEnData.setePrice(inventory);
					nonEnMap.put(productName, nonEnData);
				} else {
					nonEnData.setsPrice(nonEnData.getsPrice() + sales);
					nonEnData.setePrice(nonEnData.getePrice() + inventory);
				}
			}
			
			if ("de,fr,it,es,uk".contains(country)) {
				Map<String, PsiInventoryTurnoverData> nonEnMap = monthMap.get("eu");
				if (nonEnMap == null) {
					nonEnMap = Maps.newHashMap();
					monthMap.put("eu", nonEnMap);
				}
				PsiInventoryTurnoverData nonEnData = nonEnMap.get(productName);
				if (nonEnData == null) {
					nonEnData = new PsiInventoryTurnoverData();
					nonEnData.setsPrice(sales);
					nonEnData.setePrice(inventory);
					nonEnMap.put(productName, nonEnData);
				} else {
					nonEnData.setsPrice(nonEnData.getsPrice() + sales);
					nonEnData.setePrice(nonEnData.getePrice() + inventory);
				}
			}
			
			Map<String, PsiInventoryTurnoverData> totalMap = monthMap.get("total");
			if (totalMap == null) {
				totalMap = Maps.newHashMap();
				monthMap.put("total", totalMap);
			}
			PsiInventoryTurnoverData totalData = totalMap.get(productName);
			if (totalData == null) {
				totalData = new PsiInventoryTurnoverData();
				totalData.setsPrice(sales);
				totalData.setePrice(inventory);
				totalMap.put(productName, totalData);
			} else {
				totalData.setsPrice(totalData.getsPrice() + sales);
				totalData.setePrice(totalData.getePrice() + inventory);
			}
		}
		return map;
	}
	
	public Map<String, Map<String, Map<String, PsiInventoryTurnoverData>>> getTurnoverRate2(String start, String end, String flag, String groupType,Map<String,PsiProductEliminate> eliminateMap) throws ParseException{
		DateFormat monthFormatData= new SimpleDateFormat("yyyyMM");
  		DateFormat dayFormatData= new SimpleDateFormat("yyyyMMdd");
		
		Map<String, Map<String, Map<String, PsiInventoryTurnoverData>>> map = Maps.newHashMap();
		String temp1 = " t.`product_name` ";
		if ("1".equals(groupType)) {
			temp1 = " t.`product_type` ";
		} else if ("2".equals(groupType)) {
			temp1 = " t.`line` ";
		}
		String sql = "";
		Parameter parameter = new Parameter(start, end);
		if (StringUtils.isNotEmpty(flag) && "1".equals(flag)) {	//按月
			sql ="SELECT t.`month`,t.`product_name`,t.`product_type`,t.`line`,SUM(t.`sales_volume`*(t.`s_price`+t.`e_price`)) AS sales, "+
				" SUM(t.s_quantity*t.s_price+t.e_quantity*t.e_price) AS inventory,t.`country`"+
				" FROM `psi_inventory_turnover_data` t WHERE t.`data_type`='0' AND t.month>=:p1 AND t.`month`<=:p2 "+
				" GROUP BY t.month,t.`country`,"+temp1;
		} else {	//按年
			sql ="SELECT t.`month`,t.`product_name`,t.`product_type`,t.`line`,SUM(t.`sales_volume`*(t.`s_price`+t.`e_price`)) AS sales, "+
					" SUM(t.s_quantity*t.s_price+t.e_quantity*t.e_price) AS inventory,t.`country`"+
					" FROM `psi_inventory_turnover_data` t WHERE t.`data_type`='1' AND t.month>=:p1 AND t.`month`<=:p2 "+
					" GROUP BY t.month,t.`country`,"+temp1;
		}
		List<Object[]> list = turnoverDataDao.findBySql(sql, parameter);
		for (Object[] obj : list) {
			String month = obj[0].toString();
			String productName = obj[1].toString();
			String country = obj[6].toString();
			PsiProductEliminate ate=eliminateMap.get(productName+"_"+country);
			if(ate==null){
				continue;
			}
			if("淘汰".equals(ate.getIsSale())&&ate.getEliminateTime()==null){
				continue;
			}
			if("淘汰".equals(ate.getIsSale())&&ate.getEliminateTime().before(monthFormatData.parse(month))){
				continue;
			}
			if("新品".equals(ate.getIsNew())&&monthFormatData.parse(month).after(DateUtils.addMonths(dayFormatData.parse(ate.getAddedMonth()),2))){
				continue;
			}
			String productType = obj[2].toString();
			String line = obj[3].toString();
			if ("1".equals(groupType)) {	//按类型
				productName = productType;
			} else if ("2".equals(groupType)) {	//按产品线
				productName = line;
			}
			Float sales = obj[4]==null?0:Float.parseFloat(obj[4].toString());
			Float inventory = obj[5]==null?0:Float.parseFloat(obj[5].toString());
			
			
			Map<String, Map<String, PsiInventoryTurnoverData>> monthMap = map.get(month);
			if (monthMap == null) {
				monthMap = Maps.newHashMap();
				map.put(month, monthMap);
			}
			
			Map<String, PsiInventoryTurnoverData> countryMap = monthMap.get(country);
			if (countryMap == null) {
				countryMap = Maps.newHashMap();
				monthMap.put(country, countryMap);
			}
			PsiInventoryTurnoverData data = new PsiInventoryTurnoverData();
			data.setsPrice(sales);
			data.setePrice(inventory);
			countryMap.put(productName, data);
			//英语国家
			if ("com,uk,ca".contains(country)) {
				Map<String, PsiInventoryTurnoverData> enMap = monthMap.get("en");
				if (enMap == null) {
					enMap = Maps.newHashMap();
					monthMap.put("en", enMap);
				}
				PsiInventoryTurnoverData enData = enMap.get(productName);
				if (enData == null) {
					enData = new PsiInventoryTurnoverData();
					enData.setsPrice(sales);
					enData.setePrice(inventory);
					enMap.put(productName, enData);
				} else {
					enData.setsPrice(enData.getsPrice() + sales);
					enData.setePrice(enData.getePrice() + inventory);
				}
			}
			
			//非英语国家

			if ("de,fr,it,es,jp".contains(country)) {
				Map<String, PsiInventoryTurnoverData> nonEnMap = monthMap.get("nonEn");
				if (nonEnMap == null) {
					nonEnMap = Maps.newHashMap();
					monthMap.put("nonEn", nonEnMap);
				}
				PsiInventoryTurnoverData nonEnData = nonEnMap.get(productName);
				if (nonEnData == null) {
					nonEnData = new PsiInventoryTurnoverData();
					nonEnData.setsPrice(sales);
					nonEnData.setePrice(inventory);
					nonEnMap.put(productName, nonEnData);
				} else {
					nonEnData.setsPrice(nonEnData.getsPrice() + sales);
					nonEnData.setePrice(nonEnData.getePrice() + inventory);
				}
			}
			
			if ("de,fr,it,es,uk".contains(country)) {
				Map<String, PsiInventoryTurnoverData> nonEnMap = monthMap.get("eu");
				if (nonEnMap == null) {
					nonEnMap = Maps.newHashMap();
					monthMap.put("eu", nonEnMap);
				}
				PsiInventoryTurnoverData nonEnData = nonEnMap.get(productName);
				if (nonEnData == null) {
					nonEnData = new PsiInventoryTurnoverData();
					nonEnData.setsPrice(sales);
					nonEnData.setePrice(inventory);
					nonEnMap.put(productName, nonEnData);
				} else {
					nonEnData.setsPrice(nonEnData.getsPrice() + sales);
					nonEnData.setePrice(nonEnData.getePrice() + inventory);
				}
			}
			
			Map<String, PsiInventoryTurnoverData> totalMap = monthMap.get("total");
			if (totalMap == null) {
				totalMap = Maps.newHashMap();
				monthMap.put("total", totalMap);
			}
			PsiInventoryTurnoverData totalData = totalMap.get(productName);
			if (totalData == null) {
				totalData = new PsiInventoryTurnoverData();
				totalData.setsPrice(sales);
				totalData.setePrice(inventory);
				totalMap.put(productName, totalData);
			} else {
				totalData.setsPrice(totalData.getsPrice() + sales);
				totalData.setePrice(totalData.getePrice() + inventory);
			}
		}
		return map;
	}
	
//	public static void main(String[] args) {
//		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
//		PsiInventoryTurnoverDataService  a= applicationContext.getBean(PsiInventoryTurnoverDataService.class);
//		Map<String, Map<String, Map<String, PsiInventoryTurnoverData>>> map = a.getTurnoverRate("201610", "201610", "1", "2");
//		Map<String, Map<String, PsiInventoryTurnoverData>> rsMap = 	map.get("201610");
//			Map<String, PsiInventoryTurnoverData> countryMap = rsMap.get("en");
//			for (String productName : countryMap.keySet()) {
//				PsiInventoryTurnoverData data = countryMap.get(productName);
//				if (data.getePrice() > 0) {
//					System.out.println(productName + "\t" + data.getsPrice()/data.getePrice());
//				} else {
//					System.out.println(productName);
//				}
//			}
//		applicationContext.close();
//	}

}
