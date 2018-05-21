package com.springrain.erp.modules.amazoninfo.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.SaleReportMonthTypeDao;
import com.springrain.erp.modules.amazoninfo.entity.SaleReportMonthType;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 产品按月按类型统计销量销售额Service
 */
@Component
@Transactional(readOnly = true)
public class SaleReportMonthTypeService extends BaseService {
	
	@Autowired
	private SaleReportMonthTypeDao saleReportMonthTypeDao;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private SaleReportService saleReportService;
	
	@Autowired
	private PsiProductTypeGroupDictService dictService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
		
	@Transactional(readOnly = false)
	public void save(SaleReportMonthType saleReportMonthType) {
		saleReportMonthTypeDao.save(saleReportMonthType);
	}

	@Transactional(readOnly = false)
	public void saveList(List<SaleReportMonthType> saleReportMonthTypes) {
		saleReportMonthTypeDao.save(saleReportMonthTypes);
	}
	
	/**
	 * 组合运营业绩报告数据
	 * @param year
	 * @return//日期[类型	[国家 /销售额]]
	 */
	@Transactional(readOnly = false)
	public Map<String,Map<String, Map<String, SaleReportMonthType>>> getSalesResult(String year, boolean export){
		Map<String,Map<String, Map<String, SaleReportMonthType>>> rs = Maps.newTreeMap();
		//TODO 加入产品线权限判断
		//String temp = "";
		StringBuilder buff=new StringBuilder();
		boolean eFlag = true;	//标记总计时是否算E线
		if(!export && !SecurityUtils.getSubject().isPermitted("amazoninfo:results:viewAll")) {
			Map<String, List<String>> permissions = getLineAndCountry();
			if (permissions.size() == 0) {	//没有相应的产品线权限
				return rs;
			} else {
				buff.append(" AND (");
				int flag = 0;
				 for (Map.Entry<String, List<String>> entry: permissions.entrySet()) { 
				    String country =entry.getKey();
				    List<String> permissionsList=entry.getValue();
					if (flag > 0) {
						buff.append(" OR ");
					}
					String temp1 = "";
					if (permissionsList.size() > 0) {
						String lines = "";
						StringBuffer buf= new StringBuffer();
						for (String line : permissionsList) {
							if (!"E".equals(line)) {
								//eFlag = false;
							}
							buf.append("'" + line + "',");
						}
						lines = buf.toString();
						lines = lines.substring(0, lines.length()-1);
						temp1 = " AND t.`line` IN ("+lines+")";
					}
					buff.append("(t.`country`='"+country+"'" + temp1 + ")");
					flag++;
				}
				 buff.append(")");
			}
		} else {
			//eFlag = false;
		}
		String temp = "";
		if ("2018".equals(year)) {
			temp = " AND t.`country` NOT LIKE 'com%' ";
		}
		String sql ="SELECT t.`country`,t.`type`,t.`month`,SUM(t.`sales`),SUM(t.`sales_volume`),SUM(t.`profits`),t.`line`,SUM(t.`sales_no_tax`),SUM(t.`ad_in_event_fee`)," +
				" SUM(t.`market_sales`),SUM(t.`market_num`),SUM(t.`market_profit`),SUM(IFNULL(t.`storage_fee`,0)),SUM(t.`long_storage_fee`),SUM(t.`deal_fee`),SUM(IFNULL(t.`recall_cost`,0)),SUM(IFNULL(t.`recall_fee`,0)), " +
				
				" SUM(t.`ad_in_profit_fee`),SUM(t.`ad_out_event_fee`),SUM(t.`ad_out_profit_fee`),"+
				" SUM(t.`support_amazon_fee`),SUM(t.`support_cost`),SUM(t.`review_amazon_fee`),SUM(t.`review_cost`)," +
				" SUM(IFNULL(t.`tariff`,0)),SUM(IFNULL(t.`ad_ams_fee`,0))"+
				
				" FROM `amazoninfo_report_month_type` t WHERE t.`month` LIKE :p1 "+buff.toString() + temp + " GROUP BY t.`country`,t.`month`,t.`type` ORDER BY t.`month`,t.`line`";

		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(year + "%"));
		//2017年不忽略E线
		if (!"2016".equals(year)) {
			eFlag = true;
		}
		for (Object[] objs : list) {
			String country = objs[0].toString();
			String type = objs[1].toString();
			String date = objs[2].toString();
			Float sales = objs[3]==null?0:Float.parseFloat(objs[3].toString());
			Integer salesVolume = objs[4]==null?0:Integer.parseInt(objs[4].toString());
			Float profits = objs[5]==null?0:Float.parseFloat(objs[5].toString());
			String line = objs[6].toString();
			Float salesNoTax = objs[7]==null?0:Float.parseFloat(objs[7].toString());	//税后销售额
			Float adInEventFee = objs[8]==null?0:Float.parseFloat(objs[8].toString());	//站内sales广告费
			Float marketSales = objs[9]==null?0:Float.parseFloat(objs[9].toString());	//B2B销售额
			Integer marketNum = objs[10]==null?0:Integer.parseInt(objs[10].toString());	//B2B销量
			Float marketProfit = objs[11]==null?0:Float.parseFloat(objs[11].toString());	//B2B利润
			
			Float storageFee = objs[12]==null?0:Float.parseFloat(objs[12].toString());	//月度仓储费
			Float longStorageFee = objs[13]==null?0:Float.parseFloat(objs[13].toString());	//长期仓储费
			Float dealFee = objs[14]==null?0:Float.parseFloat(objs[14].toString());	//闪促费
			Float recallCost = objs[15]==null?0:Float.parseFloat(objs[15].toString());	//召回成本(排除退回且可用的)
			Float recallFee = objs[16]==null?0:Float.parseFloat(objs[16].toString());	//召回费

			Float adInProfitFee = objs[17]==null?0:-Float.parseFloat(objs[17].toString());
			Float adOutEventFee = objs[18]==null?0:Float.parseFloat(objs[18].toString());
			Float adOutProfitFee = objs[19]==null?0:Float.parseFloat(objs[19].toString());
			//站内站外广告销售额
			//利润去除广告费用
			profits = profits + adInProfitFee + adOutEventFee + adOutProfitFee;
			//替代货和评测单
			//Float supportAmazonFee = objs[20]==null?0:Float.parseFloat(objs[20].toString());
			//Float supportCost = objs[21]==null?0:Float.parseFloat(objs[21].toString());
			Float reviewAmazonFee = objs[22]==null?0:Float.parseFloat(objs[22].toString());
			Float reviewCost = objs[23]==null?0:Float.parseFloat(objs[23].toString());
			profits = profits + reviewAmazonFee - reviewCost;
			//关税
			Float tariff = objs[24]==null?0:Float.parseFloat(objs[24].toString());
			Float adAmsFee = objs[25]==null?0:-Float.parseFloat(objs[25].toString());
			profits = profits + adAmsFee - tariff;
			
			//销量销售额除去B2B数据--2017-4-10
			sales = sales - marketSales;
			salesVolume = salesVolume - marketNum;
			//利润除去站内sales广告费 和B2B利润
			profits = profits - adInEventFee - marketProfit;
			//利润除去仓储费和闪促费
			profits = profits + storageFee + longStorageFee + dealFee;
			//利润除去召回产品的成本和召回费用--2017-9-7
			profits = profits - recallCost - recallFee;
			Map<String, Map<String, SaleReportMonthType>> typeTemp=rs.get(date);
			if(typeTemp==null){
				typeTemp = Maps.newLinkedHashMap();
				rs.put(date, typeTemp);
			}
			Map<String, SaleReportMonthType> countryTemp = typeTemp.get(type);
			if (countryTemp == null) {
				countryTemp = Maps.newLinkedHashMap();
				typeTemp.put(type, countryTemp);
			}
			SaleReportMonthType saleReportMonthType = new SaleReportMonthType();
			saleReportMonthType.setSales(sales);
			saleReportMonthType.setSalesVolume(salesVolume);
			saleReportMonthType.setProfits(profits);
			saleReportMonthType.setLine(line);
			saleReportMonthType.setSalesNoTax(salesNoTax);
			saleReportMonthType.setAdInEventFee(adInEventFee);
			countryTemp.put(country, saleReportMonthType);
			
			Map<String, SaleReportMonthType> countryTotalTemp = typeTemp.get("total");	//分国家总计
			if (countryTotalTemp == null) {
				countryTotalTemp = Maps.newLinkedHashMap();
				typeTemp.put("total", countryTotalTemp);
			}
			String totalCountry = country;
			if ("ca,com,uk".contains(country)) {
				totalCountry = "en";
			}
			if (eFlag || !"E".equals(line)) {	//分国家总计不计算E线,用户只有E线权限时统计E线
				//英语国家都算在一起
				SaleReportMonthType monthType = countryTotalTemp.get(totalCountry);
				if (monthType == null) {
					monthType = new SaleReportMonthType();
					monthType.setSales(sales);
					monthType.setSalesVolume(salesVolume);
					monthType.setProfits(profits);
					monthType.setLine(line);
					monthType.setSalesNoTax(salesNoTax);
					monthType.setAdInEventFee(adInEventFee);
					countryTotalTemp.put(totalCountry, monthType);
				} else {
					monthType.setSales(monthType.getSales() + sales);
					monthType.setSalesVolume(monthType.getSalesVolume() + salesVolume);
					monthType.setProfits(monthType.getProfits() + profits);
					monthType.setSalesNoTax(monthType.getSalesNoTax() + salesNoTax);
					monthType.setAdInEventFee(monthType.getAdInEventFee() + adInEventFee);
					countryTotalTemp.put(totalCountry, monthType);
				}
				//所有国家总计
				SaleReportMonthType totalCountryType = countryTotalTemp.get("total");
				if (totalCountryType == null) {
					totalCountryType = new SaleReportMonthType();
					totalCountryType.setSales(sales);
					totalCountryType.setSalesVolume(salesVolume);
					totalCountryType.setProfits(profits);
					totalCountryType.setLine(line);
					totalCountryType.setSalesNoTax(salesNoTax);
					totalCountryType.setAdInEventFee(adInEventFee);
					countryTotalTemp.put("total", totalCountryType);
				} else {
					totalCountryType.setSales(totalCountryType.getSales() + sales);
					totalCountryType.setSalesVolume(totalCountryType.getSalesVolume() + salesVolume);
					totalCountryType.setProfits(totalCountryType.getProfits() + profits);
					totalCountryType.setSalesNoTax(totalCountryType.getSalesNoTax() + salesNoTax);
					totalCountryType.setAdInEventFee(totalCountryType.getAdInEventFee() + adInEventFee);
					countryTotalTemp.put("total", totalCountryType);
				}
			}

			SaleReportMonthType monthType1 = countryTemp.get("total");	//分类型总计
			if(monthType1==null){
				monthType1 = new SaleReportMonthType();
				monthType1.setSales(sales);
				monthType1.setSalesVolume(salesVolume);
				monthType1.setProfits(profits);
				monthType1.setLine(line);
				monthType1.setSalesNoTax(salesNoTax);
				monthType1.setAdInEventFee(adInEventFee);
				countryTemp.put("total", monthType1);
			} else {
				monthType1.setSales(monthType1.getSales() + sales);
				monthType1.setSalesVolume(monthType1.getSalesVolume() + salesVolume);
				monthType1.setProfits(monthType1.getProfits() + profits);
				monthType1.setSalesNoTax(monthType1.getSalesNoTax() + salesNoTax);
				monthType1.setAdInEventFee(monthType1.getAdInEventFee() + adInEventFee);
				countryTemp.put("total", monthType1);
			}
			//英语国家汇总
			if ("ca,com,uk".contains(country)) {
				SaleReportMonthType monthTypeEn = countryTemp.get("en");	//分类型总计
				if(monthTypeEn==null){
					monthTypeEn = new SaleReportMonthType();
					monthTypeEn.setSales(sales);
					monthTypeEn.setSalesVolume(salesVolume);
					monthTypeEn.setProfits(profits);
					monthTypeEn.setLine(line);
					monthTypeEn.setSalesNoTax(salesNoTax);
					monthTypeEn.setAdInEventFee(adInEventFee);
					countryTemp.put("en", monthTypeEn);
				} else {
					monthTypeEn.setSales(monthTypeEn.getSales() + sales);
					monthTypeEn.setSalesVolume(monthTypeEn.getSalesVolume() + salesVolume);
					monthTypeEn.setProfits(monthTypeEn.getProfits() + profits);
					monthTypeEn.setSalesNoTax(monthTypeEn.getSalesNoTax() + salesNoTax);
					monthTypeEn.setAdInEventFee(monthTypeEn.getAdInEventFee() + adInEventFee);
					countryTemp.put("en", monthTypeEn);
				}
			}
			//分季节统计
			String q = "q1";	//一季度
			if (date.endsWith("04") || date.endsWith("05") || date.endsWith("06")) {	//二季度
				q = "q2";
			} else if (date.endsWith("07") || date.endsWith("08") || date.endsWith("09")) {	//三季度
				q = "q3";
			} else if (date.endsWith("10") || date.endsWith("11") || date.endsWith("12")) {	//四季度
				q = "q4";
			}
			Map<String, Map<String, SaleReportMonthType>> typeSeasonTemp = rs.get(q);
			if (typeSeasonTemp == null) {
				typeSeasonTemp = Maps.newLinkedHashMap();
				rs.put(q, typeSeasonTemp);
			}
			Map<String, SaleReportMonthType> countrySeasonTemp = typeSeasonTemp.get(type);
			if (countrySeasonTemp == null) {
				countrySeasonTemp = Maps.newLinkedHashMap();
				typeSeasonTemp.put(type, countrySeasonTemp);
			}
			SaleReportMonthType monthType2 = countrySeasonTemp.get(country);
			if (monthType2 == null) {
				monthType2 = new SaleReportMonthType();
				monthType2.setSales(sales);
				monthType2.setSalesVolume(salesVolume);
				monthType2.setProfits(profits);
				monthType2.setLine(line);
				monthType2.setSalesNoTax(salesNoTax);
				monthType2.setAdInEventFee(adInEventFee);
				countrySeasonTemp.put(country, monthType2);
			} else {
				monthType2.setSales(monthType2.getSales() + sales);
				monthType2.setSalesVolume(monthType2.getSalesVolume() + salesVolume);
				monthType2.setProfits(monthType2.getProfits() + profits);
				monthType2.setSalesNoTax(monthType2.getSalesNoTax() + salesNoTax);
				monthType2.setAdInEventFee(monthType2.getAdInEventFee() + adInEventFee);
				countrySeasonTemp.put(country, monthType2);
			}
			
			SaleReportMonthType monthType3 = countrySeasonTemp.get("total");
			if (monthType3 == null) {
				monthType3 = new SaleReportMonthType();
				monthType3.setSales(sales);
				monthType3.setSalesVolume(salesVolume);
				monthType3.setProfits(profits);
				monthType3.setLine(line);
				monthType3.setSalesNoTax(salesNoTax);
				monthType3.setAdInEventFee(adInEventFee);
				countrySeasonTemp.put("total", monthType3);
			} else {
				monthType3.setSales(monthType3.getSales() + sales);
				monthType3.setSalesVolume(monthType3.getSalesVolume() + salesVolume);
				monthType3.setProfits(monthType3.getProfits() + profits);
				monthType3.setSalesNoTax(monthType3.getSalesNoTax() + salesNoTax);
				monthType3.setAdInEventFee(monthType3.getAdInEventFee() + adInEventFee);
				countrySeasonTemp.put("total", monthType3);
			}
			
			//英语国家汇总
			if ("ca,com,uk".contains(country)) {
				SaleReportMonthType monthType4 = countrySeasonTemp.get("en");
				if(monthType4==null){
					monthType4 = new SaleReportMonthType();
					monthType4.setSales(sales);
					monthType4.setSalesVolume(salesVolume);
					monthType4.setProfits(profits);
					monthType4.setLine(line);
					monthType4.setSalesNoTax(salesNoTax);
					monthType4.setAdInEventFee(adInEventFee);
					countrySeasonTemp.put("en", monthType4);
				} else {
					monthType4.setSales(monthType4.getSales() + sales);
					monthType4.setSalesVolume(monthType4.getSalesVolume() + salesVolume);
					monthType4.setProfits(monthType4.getProfits() + profits);
					monthType4.setSalesNoTax(monthType4.getSalesNoTax() + salesNoTax);
					monthType4.setAdInEventFee(monthType4.getAdInEventFee() + adInEventFee);
					countrySeasonTemp.put("en", monthType4);
				}
			}
			if (eFlag || !"E".equals(line)) {	//分国家总计不计算E线,用户只有E线权限时统计E线
				Map<String, SaleReportMonthType> countrySeasonTotal = typeSeasonTemp.get("total");	//分国家季节总计
				if (countrySeasonTotal == null) {
					countrySeasonTotal = Maps.newLinkedHashMap();
					typeSeasonTemp.put("total", countrySeasonTotal);
				}
				SaleReportMonthType monthType5 = countrySeasonTotal.get(totalCountry);
				if (monthType5 == null) {
					monthType5 = new SaleReportMonthType();
					monthType5.setSales(sales);
					monthType5.setSalesVolume(salesVolume);
					monthType5.setProfits(profits);
					monthType5.setLine(line);
					monthType5.setSalesNoTax(salesNoTax);
					monthType5.setAdInEventFee(adInEventFee);
					countrySeasonTotal.put(totalCountry, monthType5);
				} else {
					monthType5.setSales(monthType5.getSales() + sales);
					monthType5.setSalesVolume(monthType5.getSalesVolume() + salesVolume);
					monthType5.setProfits(monthType5.getProfits() + profits);
					monthType5.setSalesNoTax(monthType5.getSalesNoTax() + salesNoTax);
					monthType5.setAdInEventFee(monthType5.getAdInEventFee() + adInEventFee);
					countrySeasonTotal.put(totalCountry, monthType5);
				}
				
				SaleReportMonthType monthType6 = countrySeasonTotal.get("total");
				if (monthType6 == null) {
					monthType6 = new SaleReportMonthType();
					monthType6.setSales(sales);
					monthType6.setSalesVolume(salesVolume);
					monthType6.setProfits(profits);
					monthType6.setLine(line);
					monthType6.setSalesNoTax(salesNoTax);
					monthType6.setAdInEventFee(adInEventFee);
					countrySeasonTotal.put("total", monthType6);
				} else {
					monthType6.setSales(monthType6.getSales() + sales);
					monthType6.setSalesVolume(monthType6.getSalesVolume() + salesVolume);
					monthType6.setProfits(monthType6.getProfits() + profits);
					monthType6.setSalesNoTax(monthType6.getSalesNoTax() + salesNoTax);
					monthType6.setAdInEventFee(monthType6.getAdInEventFee() + adInEventFee);
					countrySeasonTotal.put("total", monthType6);
				}
			}
		}
		return rs;
	}
	
	/**
	 * 按年统计数据
	 * @param year
	 * @return//日期[类型	[国家 /销售额]]
	 */
	public Map<String,Map<String, Map<String, SaleReportMonthType>>> getSalesResultForYear(String year){
		String sql ="SELECT t.`country`,t.`type`,"+year+",SUM(t.`sales`),SUM(t.`sales_volume`),SUM(t.`profits`),t.`line` " +
				" FROM `amazoninfo_report_month_type` t WHERE t.`month` LIKE :p1 GROUP BY t.`country`,t.`type`";
		
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(year + "%"));
		
		Map<String,Map<String, Map<String, SaleReportMonthType>>> rs = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString();
			String type = objs[1].toString();
			String date = objs[2].toString();
			Float sales = objs[3]==null?0:Float.parseFloat(objs[3].toString());
			Integer salesVolume = objs[4]==null?0:Integer.parseInt(objs[4].toString());
			Float profits = objs[5]==null?0:Float.parseFloat(objs[5].toString());
			String line = objs[6].toString();
			
			Map<String, Map<String, SaleReportMonthType>> typeTemp=rs.get(date);
			if(typeTemp==null){
				typeTemp = Maps.newHashMap();
				rs.put(date, typeTemp);
			}
			Map<String, SaleReportMonthType> countryTemp = typeTemp.get(type);
			if (countryTemp == null) {
				countryTemp = Maps.newHashMap();
				typeTemp.put(type, countryTemp);
			}
			SaleReportMonthType saleReportMonthType = new SaleReportMonthType();
			saleReportMonthType.setSales(sales);
			saleReportMonthType.setSalesVolume(salesVolume);
			saleReportMonthType.setProfits(profits);
			saleReportMonthType.setLine(line);
			countryTemp.put(country, saleReportMonthType);
			
			Map<String, SaleReportMonthType> countryTotalTemp = typeTemp.get("total");	//分国家总计
			if (countryTotalTemp == null) {
				countryTotalTemp = Maps.newHashMap();
				typeTemp.put("total", countryTotalTemp);
			}
			String totalCountry = country;
			if ("ca,com,uk".contains(country)) {
				totalCountry = "en";
			}
			//英语国家都算在一起
			SaleReportMonthType monthType = countryTotalTemp.get(totalCountry);
			if (monthType == null) {
				monthType = new SaleReportMonthType();
				monthType.setSales(sales);
				monthType.setSalesVolume(salesVolume);
				monthType.setProfits(profits);
				monthType.setLine(line);
				countryTotalTemp.put(totalCountry, monthType);
			} else {
				monthType.setSales(monthType.getSales() + sales);
				monthType.setSalesVolume(monthType.getSalesVolume() + salesVolume);
				monthType.setProfits(monthType.getProfits() + profits);
				countryTotalTemp.put(totalCountry, monthType);
			}

			SaleReportMonthType monthType1 = countryTemp.get("total");	//分类型总计
			if(monthType1==null){
				monthType1 = new SaleReportMonthType();
				monthType1.setSales(sales);
				monthType1.setSalesVolume(salesVolume);
				monthType1.setProfits(profits);
				monthType1.setLine(line);
				countryTemp.put("total", monthType1);
			} else {
				monthType1.setSales(monthType1.getSales() + sales);
				monthType1.setSalesVolume(monthType1.getSalesVolume() + salesVolume);
				monthType1.setProfits(monthType1.getProfits() + profits);
				countryTemp.put("total", monthType1);
			}
			//英语国家汇总
			if ("ca,com,uk".contains(country)) {
				SaleReportMonthType monthTypeEn = countryTemp.get("en");	//分类型总计
				if(monthTypeEn==null){
					monthTypeEn = new SaleReportMonthType();
					monthTypeEn.setSales(sales);
					monthTypeEn.setSalesVolume(salesVolume);
					monthTypeEn.setProfits(profits);
					monthTypeEn.setLine(line);
					countryTemp.put("en", monthTypeEn);
				} else {
					monthTypeEn.setSales(monthTypeEn.getSales() + sales);
					monthTypeEn.setSalesVolume(monthTypeEn.getSalesVolume() + salesVolume);
					monthTypeEn.setProfits(monthTypeEn.getProfits() + profits);
					countryTemp.put("en", monthTypeEn);
				}
			}
		}
		return rs;
	}
	
	/**
	 * 根据月份分平台、产品类型统计销售额、销量以及费用利润等信息保存到中间表
	 * @param month	月份
	 * @param rateRs 汇率
	 * @return
	 * 
	 */
	@Transactional(readOnly=false)
	public void saveOrUpdate(String month){
		String temp1 = " ,TRUNCATE(SUM(a.sales_no_tax),2)" +
	        		" ,TRUNCATE(SUM(a.amazon_fee),2)" +
	        		" ,TRUNCATE(SUM(a.other_fee),2)" +
	        		" ,TRUNCATE(SUM(a.transport_fee),2)" +
	        		" ,TRUNCATE(SUM(a.buy_cost*a.fee_quantity),2)" +
	        		" ,TRUNCATE(SUM(a.refund),2)" +
	        		" ,TRUNCATE(SUM(a.profits),2)" ;
		
        String sql = "SELECT a.`product_name`,SUM(a.`sales_volume`) as sales,TRUNCATE(SUM( a.sales),2)" +
        		temp1+",a.`country`,a.`type`,a.`line`,SUM(a.return_num),SUM(a.`support_num`),SUM(a.`review_num`)," +
        				" SUM(a.`support_cost`),SUM(a.`support_amazon_fee`),SUM(a.`review_cost`),SUM(a.`review_amazon_fee`), " +
        				" SUM(a.`ad_in_event_sales`),SUM(a.`ad_in_event_sales_volume`),SUM(a.`ad_in_event_fee`), " +
        				" SUM(a.`ad_in_profit_sales`),SUM(a.`ad_in_profit_sales_volume`),SUM(a.`ad_in_profit_fee`), " +
        				" SUM(a.`ad_out_event_sales`),SUM(a.`ad_out_event_sales_volume`),SUM(a.`ad_out_event_fee`), " +
        				" SUM(a.`ad_out_profit_sales`),SUM(a.`ad_out_profit_sales_volume`),SUM(a.`ad_out_profit_fee`), " +
        				" SUM(a.`recall_num`),SUM(a.`recall_cost`),SUM(a.`recall_fee`),SUM(a.`fee_quantity`)," +
        				" SUM(a.`market_num`),SUM(a.`market_sales`),TRUNCATE(SUM(a.tariff*a.fee_quantity),2),SUM(IFNULL(a.`storage_fee`,0)), " +
        				" SUM(IFNULL(a.`ad_ams_sales`,0)),SUM(IFNULL(a.`ad_ams_sales_volume`,0)),SUM(IFNULL(a.`ad_ams_fee`,0)),SUM(IFNULL(a.`market_profit`,0)), " +
        				" SUM(IFNULL(a.`deal_fee`,0)),SUM(IFNULL(a.`deal_sales_volume`,0)),SUM(IFNULL(a.`deal_profit`,0)),group_concat(distinct a.product_attr), " +
        				" SUM(IFNULL(a.`mold_fee`,0)),a.`account_name`,SUM(IFNULL(a.`express_fee`,0)),SUM(IFNULL(a.`vine_fee`,0)),SUM(IFNULL(a.`vine_num`,0)),SUM(IFNULL(a.`vine_cost`,0)) " +
        				" FROM amazoninfo_sale_profit a ";
        sql+=" WHERE a.`type` is not null and a.`day` like '"+month+"%' ";
        sql+=" GROUP BY a.`product_name`,a.account_name order by sales desc";
        
        List<Object[]>  list = saleReportMonthTypeDao.findBySql(sql);
		List<SaleReportMonthType> lists = Lists.newArrayList();
		
		for (Object[] obj : list) {
			if (obj[0] == null) {
				continue;
			}
			String productName = obj[0].toString();
			Integer salesVolume = obj[1]==null?0:Integer.parseInt(obj[1].toString());
			Float sales = obj[2]==null?0:Float.parseFloat(obj[2].toString());
			Float salesNoTax = obj[3]==null?0:Float.parseFloat(obj[3].toString());
			Float amazonFee = obj[4]==null?0:Float.parseFloat(obj[4].toString());
			Float otherFee = obj[5]==null?0:Float.parseFloat(obj[5].toString());
			Float transportFee = obj[6]==null?0:Float.parseFloat(obj[6].toString());
			float buyCost = obj[7]==null?0:Float.parseFloat(obj[7].toString());//采购成本
			Float refund = obj[8]==null?0:Float.parseFloat(obj[8].toString());
			Float profits = obj[9]==null?0:Float.parseFloat(obj[9].toString()); //利润
			String country = obj[10].toString();
			String type =  obj[11].toString();
			String line =  obj[12].toString();
			Integer returnNum = obj[13]==null?0:Integer.parseInt(obj[13].toString()); //退货数量
			Integer supportNum = obj[14]==null?0:Integer.parseInt(obj[14].toString()); //替代货数量
			Integer reviewNum = obj[15]==null?0:Integer.parseInt(obj[15].toString()); //评测货数量
			Float supportCost = obj[16]==null?0:Float.parseFloat(obj[16].toString());//替代货成本(运费+采购成本)
			Float supportAmazonFee = obj[17]==null?0:Float.parseFloat(obj[17].toString());//替代货亚马逊费用
			Float reviewCost = obj[18]==null?0:Float.parseFloat(obj[18].toString());//评测单成本(运费+采购成本)
			Float reviewAmazonFee = obj[19]==null?0:Float.parseFloat(obj[19].toString());//评测单亚马逊费用
			
			Float adInEventSales = obj[20]==null?0:Float.parseFloat(obj[20].toString()); //站内event广告销售额
			Integer adInEventSalesVolume = obj[21]==null?0:Integer.parseInt(obj[21].toString()); //站内event广告销量
			Float adInEventFee = obj[22]==null?0:Float.parseFloat(obj[22].toString()); //站内event广告费用
			Float adInProfitSales = obj[23]==null?0:Float.parseFloat(obj[23].toString()); //站内Profit广告销售额
			Integer adInProfitSalesVolume = obj[24]==null?0:Integer.parseInt(obj[24].toString()); //站内Profit广告销量
			Float adInProfitFee = obj[25]==null?0:Float.parseFloat(obj[25].toString()); //站内Profit广告费用
			
			Float adOutEventSales = obj[26]==null?0:Float.parseFloat(obj[26].toString()); //站外event广告销售额
			Integer adOutEventSalesVolume = obj[27]==null?0:Integer.parseInt(obj[27].toString()); //站外event广告销量
			Float adOutEventFee = obj[28]==null?0:Float.parseFloat(obj[28].toString()); //站外event广告费用
			Float adOutProfitSales = obj[29]==null?0:Float.parseFloat(obj[29].toString()); //站外Profit广告销售额
			Integer adOutProfitSalesVolume = obj[30]==null?0:Integer.parseInt(obj[30].toString()); //站外Profit广告销量
			Float adOutProfitFee = obj[31]==null?0:Float.parseFloat(obj[31].toString()); //站外Profit广告费用
			
			//召回数量&召回成本
			Integer recallNum = obj[32]==null?0:Integer.parseInt(obj[32].toString());
			Float recallCost = obj[33]==null?0:Float.parseFloat(obj[33].toString());
			Float recallFee = obj[34]==null?0:Float.parseFloat(obj[34].toString());
			Integer feeQuantity = obj[35]==null?0:Integer.parseInt(obj[35].toString());
			Integer marketNum = obj[36]==null?0:Integer.parseInt(obj[36].toString());
			Float marketSales = obj[37]==null?0:Float.parseFloat(obj[37].toString());
			Float tariff = obj[38]==null?0:Float.parseFloat(obj[38].toString());
			//Float storageFee = obj[39]==null?0:Float.parseFloat(obj[39].toString());
			
			Float adAmsSales = obj[40]==null?0:Float.parseFloat(obj[40].toString()); //AMS广告销售额
			Integer adAmsSalesVolume = obj[41]==null?0:Integer.parseInt(obj[41].toString()); //AMS广告销量
			Float adAmsFee = obj[42]==null?0:Float.parseFloat(obj[42].toString()); //AMS广告费用
			Float marketProfit = obj[43]==null?0:Float.parseFloat(obj[43].toString()); //B2B利润

			Float dealFee = obj[44]==null?0:Float.parseFloat(obj[44].toString()); //闪促费用
			Integer dealSalesVolume = obj[45]==null?0:Integer.parseInt(obj[45].toString()); //闪促销量
			Float dealProfit = obj[46]==null?0:Float.parseFloat(obj[46].toString()); //闪促利润
			String productAttr=(obj[47]==null?"":obj[47].toString());
			Float moldFee = obj[48]==null?0:Float.parseFloat(obj[48].toString()); //闪促利润
			String accountName = obj[49].toString();
			Float expressFee = obj[50]==null?0:Float.parseFloat(obj[50].toString()); //自发货快递费
			Float vineFee = obj[51]==null?0:Float.parseFloat(obj[51].toString()); //vine项目费用
			Integer vineNum = obj[52]==null?0:Integer.parseInt(obj[52].toString());
			Float vineCost = obj[53]==null?0:Float.parseFloat(obj[53].toString());
			
			if ("F".equals(line) && Integer.parseInt(month) < 201606) {
				line = "A";	//F线产品从A线中分出去的,5月份还是算在A线中
			}
			SaleReportMonthType saleReportMonthType = getByUnique(month, country, productName, accountName);
			if (saleReportMonthType == null) {
				saleReportMonthType = new SaleReportMonthType();
			}
			saleReportMonthType.setSales(sales);
			saleReportMonthType.setSalesVolume(salesVolume);
			saleReportMonthType.setFeeQuantity(feeQuantity);
			saleReportMonthType.setSalesNoTax(salesNoTax);
			saleReportMonthType.setAmazonFee(amazonFee);
			saleReportMonthType.setOtherFee(otherFee);
			saleReportMonthType.setTransportFee(transportFee);
			saleReportMonthType.setBuyCost(buyCost);
			saleReportMonthType.setRefund(refund);
			saleReportMonthType.setProfits(profits);
			saleReportMonthType.setCountry(country);
			saleReportMonthType.setAccountName(accountName);
			saleReportMonthType.setType(type);
			saleReportMonthType.setMonth(month);
			saleReportMonthType.setLine(line);
			saleReportMonthType.setProductName(productName);
			saleReportMonthType.setReturnNum(returnNum);
			saleReportMonthType.setSupportNum(supportNum);
			saleReportMonthType.setSupportCost(supportCost);
			saleReportMonthType.setSupportAmazonFee(supportAmazonFee);
			saleReportMonthType.setReviewNum(reviewNum);
			saleReportMonthType.setReviewCost(reviewCost);
			saleReportMonthType.setReviewAmazonFee(reviewAmazonFee);
			saleReportMonthType.setAdInEventSales(adInEventSales);
			saleReportMonthType.setAdInEventSalesVolume(adInEventSalesVolume);
			saleReportMonthType.setAdInEventFee(adInEventFee);
			saleReportMonthType.setAdInProfitSales(adInProfitSales);
			saleReportMonthType.setAdInProfitSalesVolume(adInProfitSalesVolume);
			saleReportMonthType.setAdInProfitFee(adInProfitFee);
			saleReportMonthType.setAdOutEventSales(adOutEventSales);
			saleReportMonthType.setAdOutEventSalesVolume(adOutEventSalesVolume);
			saleReportMonthType.setAdOutEventFee(adOutEventFee);
			saleReportMonthType.setAdOutProfitSales(adOutProfitSales);
			saleReportMonthType.setAdOutProfitSalesVolume(adOutProfitSalesVolume);
			saleReportMonthType.setAdOutProfitFee(adOutProfitFee);
			saleReportMonthType.setAdAmsSales(adAmsSales);
			saleReportMonthType.setAdAmsSalesVolume(adAmsSalesVolume);
			saleReportMonthType.setAdAmsFee(adAmsFee);
			saleReportMonthType.setRecallNum(recallNum);
			saleReportMonthType.setRecallCost(recallCost);
			saleReportMonthType.setRecallFee(recallFee);
			saleReportMonthType.setMarketNum(marketNum);
			saleReportMonthType.setMarketSales(marketSales);
			saleReportMonthType.setMarketProfit(marketProfit);
			saleReportMonthType.setTariff(tariff);
			//saleReportMonthType.setStorageFee(storageFee);
			
			saleReportMonthType.setDealFee(dealFee);
			saleReportMonthType.setDealProfit(dealProfit);
			saleReportMonthType.setDealSalesVolume(dealSalesVolume);
			saleReportMonthType.setMoldFee(moldFee);
			saleReportMonthType.setExpressFee(expressFee);
			saleReportMonthType.setVineFee(vineFee);
			saleReportMonthType.setVineNum(vineNum);
			saleReportMonthType.setVineCost(vineCost);
			
			String attr="";

			if (productAttr.contains("淘汰")) {
				attr = "淘汰";
			} else if (productAttr.contains("新品")) {
				attr = "新品";
			} else if (productAttr.contains("爆款")) {
				attr = "爆款";
			} else if (productAttr.contains("利润款")) {
				attr = "利润款";
			} else if (productAttr.contains("主力")) {
				attr = "主力";
			} else {
				attr = "普通";
			}
			saleReportMonthType.setProductAttr(attr);
			lists.add(saleReportMonthType);
		}
		if (lists.size() > 0) {
			saveList(lists);
		}
	}
	
	//根据条件查询唯一记录
	public SaleReportMonthType getByUnique(String month, String country, String productName, String accountName) {
		DetachedCriteria dc = saleReportMonthTypeDao.createDetachedCriteria();
		dc.add(Restrictions.eq("month", month));
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.eq("productName", productName));
		dc.add(Restrictions.eq("accountName", accountName));
		List<SaleReportMonthType> list = saleReportMonthTypeDao.find(dc);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 获取指定月份的分产品线销售额
	 * @param month
	 * @return [国家[产品线  销售额]]
	 */
	public Map<String,Map<String, Float>> getLineSalesByMonth(String month){
		String sql ="SELECT t.`country`,t.`line`,SUM(t.`sales`) FROM `amazoninfo_report_month_type` t WHERE t.`month`=:p1 GROUP BY t.`line`,t.`country`";
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(month));
		Map<String,Map<String, Float>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString();
			String line = objs[1].toString();
			Float sales = objs[2]==null?0:Float.parseFloat(objs[2].toString());

			Map<String, Float> countryTemp = rs.get(country);
			if (countryTemp == null) {
				countryTemp = Maps.newHashMap();
				rs.put(country, countryTemp);
			}
			countryTemp.put(line, sales);
			if (!"E".equals(line)) {
				Float totalSales = countryTemp.get("total");//不计算E线
				if (totalSales == null) {
					countryTemp.put("total", sales);
				} else {
					countryTemp.put("total", totalSales + sales);
				}
			}
		}
		return rs;
	}
	
	/**
	 * 分月获取国家销售额,不计算E线
	 * @param month
	 * @return [月份[国家  销售额]]
	 */
	public Map<String,Map<String, Float>> getSalesByMonth(String date1, String date2){
		String sql ="SELECT t.`month`,t.`country`,SUM(t.`sales`) FROM `amazoninfo_report_month_type` t WHERE t.`month`>=:p1 AND t.`month`<=:p2 GROUP BY t.`country`,t.`month`";
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(date1, date2));
		Map<String,Map<String, Float>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String month = objs[0].toString();
			String country = objs[1].toString();
			Float sales = objs[2]==null?0:Float.parseFloat(objs[2].toString());

			Map<String, Float> dateTemp = rs.get(month);
			if (dateTemp == null) {
				dateTemp = Maps.newHashMap();
				rs.put(month, dateTemp);
			}
			dateTemp.put(country, sales);
			
			Float totalSales = dateTemp.get("total");
			if (totalSales == null) {
				dateTemp.put("total", sales);
			} else {
				dateTemp.put("total", totalSales + sales);
			}
		}
		return rs;
	}
	
	/**
	 * 获取月度总销售额(不含E线)
	 * @return
	 */
	public Float getTotalSalesByMonth(String month){
		String sql="SELECT SUM(t.`sales`) FROM `amazoninfo_report_month_type` t WHERE t.`line`!='E' AND t.`month`=:p1";
		List<Object> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(month));
		if(list.size()>0){
			Object obj = list.get(0);
			if (obj == null) {
				return 0f;
			}
			return Float.parseFloat(obj.toString());
		}else{
			return 0f;
		}
	}
	
	/**
	 * 分月获取国家销售额和利润,不计算E线
	 * @param month
	 * @return [月份[国家  销售额]]
	 */
	public Map<String,Map<String, SaleReportMonthType>> getSalesAndProfitByMonth(String date1, String date2){
		String sql ="SELECT t.`month`,t.`country`,SUM(t.`sales`),SUM(t.`profits`) FROM `amazoninfo_report_month_type` t WHERE t.`line`!='E' AND t.`month`>=:p1 AND t.`month`<=:p2 GROUP BY t.`country`,t.`month`";
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(date1, date2));
		Map<String,Map<String, SaleReportMonthType>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String month = objs[0].toString();
			String country = objs[1].toString();
			Float sales = objs[2]==null?0:Float.parseFloat(objs[2].toString());
			Float profits = objs[3]==null?0:Float.parseFloat(objs[3].toString());

			Map<String, SaleReportMonthType> dateTemp = rs.get(month);
			if (dateTemp == null) {
				dateTemp = Maps.newHashMap();
				rs.put(month, dateTemp);
			}
			SaleReportMonthType monthType = new SaleReportMonthType();
			monthType.setSales(sales);
			monthType.setProfits(profits);
			dateTemp.put(country, monthType);
			
			if ("com,uk,ca".contains(country)) {
				SaleReportMonthType enMonthType = dateTemp.get("en");
				if (enMonthType == null) {
					enMonthType = new SaleReportMonthType();
					dateTemp.put("en", enMonthType);
					enMonthType.setSales(sales);
					enMonthType.setProfits(profits);
				} else {
					enMonthType.setSales(enMonthType.getSales() + sales);
					enMonthType.setProfits(enMonthType.getProfits() + profits);
				}
			}
			
			SaleReportMonthType totalMonthType = dateTemp.get("total");
			if (totalMonthType == null) {
				totalMonthType = new SaleReportMonthType();
				dateTemp.put("total", totalMonthType);
				totalMonthType.setSales(sales);
				totalMonthType.setProfits(profits);
			} else {
				totalMonthType.setSales(totalMonthType.getSales() + sales);
				totalMonthType.setProfits(totalMonthType.getProfits() + profits);
			}
		}
		return rs;
	}
	
	/**
	 * 分产品类型获取国家销售额和利润,不计算E线
	 * @param month
	 * @return [国家[类型  销售额]]
	 */
	public Map<String,Map<String, SaleReportMonthType>> getSalesAndProfitByType(String date1, String date2){
		String sql ="SELECT t.`type`,t.`country`,SUM(t.`sales`),SUM(t.`profits`) FROM `amazoninfo_report_month_type` t WHERE t.`line`!='E' AND t.`month`>=:p1 AND t.`month`<=:p2 GROUP BY t.`type`,t.`country`";
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(date1, date2));
		Map<String,Map<String, SaleReportMonthType>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String type = objs[0].toString();
			String country = objs[1].toString();
			Float sales = objs[2]==null?0:Float.parseFloat(objs[2].toString());
			Float profits = objs[3]==null?0:Float.parseFloat(objs[3].toString());

			if ("com,uk,ca".contains(country)) {
				country = "en";
			}
			Map<String, SaleReportMonthType> countryTemp = rs.get(country);
			if (countryTemp == null) {
				countryTemp = Maps.newHashMap();
				rs.put(country, countryTemp);
			}
			SaleReportMonthType monthType = countryTemp.get(type);
			if (monthType == null) {
				monthType = new SaleReportMonthType();
				countryTemp.put(type, monthType);
				monthType.setSales(sales);
				monthType.setProfits(profits);
			} else {
				monthType.setSales(monthType.getSales() + sales);
				monthType.setProfits(monthType.getProfits() + profits);
			}
			
			SaleReportMonthType totalMonthType = countryTemp.get("total");
			if (totalMonthType == null) {
				totalMonthType = new SaleReportMonthType();
				countryTemp.put("total", totalMonthType);
				totalMonthType.setSales(sales);
				totalMonthType.setProfits(profits);
			} else {
				totalMonthType.setSales(totalMonthType.getSales() + sales);
				totalMonthType.setProfits(totalMonthType.getProfits() + profits);
			}
		}
		return rs;
	}
	
	/**
	 * 分月按产品类型统计销售额,不区分国家,总计不统计E线(利润去除广告费用)
	 * @param month
	 * @return [产品类型  销售额]]
	 */
	public Map<String, SaleReportMonthType> getAllTypeSalesByMonth(String month){
		String sql ="SELECT t.`type`,t.`line`,SUM(t.`sales`),SUM(t.`profits`-IFNULL(t.`ad_in_event_fee`,0)) FROM `amazoninfo_report_month_type` t WHERE  t.`month`=:p1 GROUP BY t.`type` ORDER BY t.`line`";
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(month));
		Map<String, SaleReportMonthType> rs = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			String type = objs[0].toString();
			String line = objs[1].toString();
			Float sales = objs[2]==null?0:Float.parseFloat(objs[2].toString());
			Float profits = objs[3]==null?0:Float.parseFloat(objs[3].toString());

			SaleReportMonthType monthType = new SaleReportMonthType();
			monthType.setType(type);
			monthType.setLine(line);
			monthType.setSales(sales);
			monthType.setProfits(profits);
			rs.put(type, monthType);
			
			//if (!"E".equals(line)) {
				SaleReportMonthType totalType = rs.get("total");
				if (totalType == null) {
					totalType = new SaleReportMonthType();
					rs.put("total", totalType);
					totalType.setSales(sales);
					totalType.setProfits(profits);
				} else {
					totalType.setSales(totalType.getSales() + sales);
					totalType.setProfits(totalType.getProfits() + profits);
				}
			//}
		}
		return rs;
	}
	
	/**
	 * 分月按产品线统计销售额,不区分国家,总计不统计E线(利润去除广告费用)
	 * @param month
	 * @return [产品线  销售额]]
	 */
	public Map<String, SaleReportMonthType> getAllLineSalesByMonth(String month){
		String sql ="SELECT t.`line`,SUM(t.`sales`),SUM(t.`profits`-IFNULL(t.`ad_in_event_fee`,0)) FROM `amazoninfo_report_month_type` t WHERE  t.`month`=:p1 GROUP BY t.`line` ORDER BY t.`line`;";
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(month));
		Map<String, SaleReportMonthType> rs = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			String line = objs[0].toString();
			Float sales = objs[1]==null?0:Float.parseFloat(objs[1].toString());
			Float profits = objs[2]==null?0:Float.parseFloat(objs[2].toString());

			SaleReportMonthType monthType = new SaleReportMonthType();
			monthType.setLine(line);
			monthType.setSales(sales);
			monthType.setProfits(profits);
			rs.put(line, monthType);
			
			//if (!"E".equals(line)) {
				SaleReportMonthType totalType = rs.get("total");
				if (totalType == null) {
					totalType = new SaleReportMonthType();
					rs.put("total", totalType);
					totalType.setLine(line);
					totalType.setSales(sales);
					totalType.setProfits(profits);
				} else {
					totalType.setSales(totalType.getSales() + sales);
					totalType.setProfits(totalType.getProfits() + profits);
				}
			//}
		}
		return rs;
	}

	/**
	 * 按月获取产品销量
	 * @param month 
	 * @return [产品 销量] 国家分欧洲，日本，加拿大，美国
	 */
	public Map<String, Integer> findSalesVolumeByMonth(String month, String country) {
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
		String sql="SELECT product_name,SUM(sales_volume) FROM amazoninfo_report_month_type WHERE month = :p1 "+ temp+" GROUP BY product_name";
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql,new Parameter(month));
		for (Object[] obj : list) {
			String productName = obj[0].toString();
			Integer salesVolume = Integer.parseInt(obj[1].toString());
			rs.put(productName, salesVolume);
		}
		return rs;
	}
	
	//Map<country,List<line>>
	public Map<String, List<String>> getLineAndCountry(){
		Map<String, List<String>> rs = Maps.newHashMap();
		Map<String, String> allLine = dictService.getProductLine();
		for (Map.Entry<String,String> entryRs : allLine.entrySet()) { 
		    String key =entryRs.getKey();
			String value = entryRs.getValue();
			allLine.put(key, value.substring(0, 1));
		}
		String sql = "SELECT t.`product_group_id`,t.`country` FROM psi_product_group_user t WHERE t.`responsible` like :p1 AND t.`del_flag`='0' AND LENGTH(t.`responsible`)>10 ";
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql,new Parameter("%" +UserUtils.getUser().getId() + "%"));
		List<String> platMag = Lists.newArrayList();
		for (Object[] obj : list) {
			if ("0".equals(obj[0])) {
				platMag.add(obj[1].toString());
			} else {
				List<String> lineList = rs.get(obj[1].toString());
				if (lineList == null) {
					lineList = Lists.newArrayList();
					rs.put(obj[1].toString(), lineList);
				}
				lineList.add(allLine.get(obj[0].toString()));
			}
		}
		for (String country : platMag) {
			List<String> temp = Lists.newArrayList();
			rs.put(country, temp);//置空表示整个平台负责人
		}
		return rs;
	}
	
	/**
	 * 查询当前用户对应的国家和产品线集合
	 * @return
	 */
	public Map<String, String> getCountryLineStr(){
		Map<String, String> rs = Maps.newHashMap();
		Map<String, String> allLine = dictService.getProductLine();
		String alLineStr = "";
		StringBuffer buf= new StringBuffer();
		for (Map.Entry<String,String> entry: allLine.entrySet()) { 
		    String key =entry.getKey();
			String value = entry.getValue();
			allLine.put(key, value.substring(0, 1));
			buf.append(value.substring(0, 1) + ",");
		}
		alLineStr=buf.toString();
		String sql = "SELECT t.`country` FROM `psi_product_group_user` t WHERE t.`responsible` like :p1 AND t.`del_flag`='0' AND LENGTH(t.`responsible`)>10 AND t.`country` IS NOT NULL  GROUP BY t.`country`";
		List<Object> list = saleReportMonthTypeDao.findBySql(sql,new Parameter("%" + UserUtils.getUser().getId() + "%"));
		Set<String> countrySet = Sets.newHashSet();
		for (Object obj : list) {
			String country = obj.toString();
			if ("com,uk,ca".contains(country)) {
				countrySet.add("en");
			} else {
				countrySet.add(country);
			}
		}
		rs.put("country", countrySet.toString());
		sql = "SELECT t.`product_group_id` FROM `psi_product_group_user` t WHERE t.`responsible` LIKE :p1 AND t.`del_flag`='0' AND LENGTH(t.`responsible`)>10 AND t.`product_group_id` IS NOT NULL GROUP BY t.`product_group_id`";
		list = saleReportMonthTypeDao.findBySql(sql,new Parameter("%" +UserUtils.getUser().getId()+"%"));
		String lines = "";
		for (Object obj : list) {
			if ("0".equals(obj.toString())) {
				lines = alLineStr;
				break;
			} else {
				String line = allLine.get(obj.toString());
				lines += (line + ",");
			}
		}
		rs.put("line", lines);
		return rs;
	}
	
	/**
	 * 分月按产品线统计销售额,区分英语国家和非英语国家
	 * @param month
	 * @return  	[月份 [产品线[国家   SaleReportMonthType]]]
	 */
	public Map<String,Map<String,Map<String, SaleReportMonthType>>> getAllLineSalesWithEnAndNonEn(String start, String end){
		String sql ="SELECT t.`month`,t.`country`,t.`line`,SUM(t.`sales`),SUM(t.`profits`-IFNULL(t.`ad_in_event_fee`,0)) "+
				" FROM `amazoninfo_report_month_type` t WHERE  t.`month`>=:p1 AND t.`month`<=:p2 "+
				" GROUP BY t.`line`,t.`country`,t.`month`";
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(start, end));
		Map<String,Map<String,Map<String, SaleReportMonthType>>> map = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			String month = objs[0].toString();
			String country = objs[1].toString();
			String line = objs[2].toString();
			Float sales = objs[3]==null?0:Float.parseFloat(objs[3].toString());
			Float profits = objs[4]==null?0:Float.parseFloat(objs[4].toString());

			Map<String, Map<String, SaleReportMonthType>> temp = map.get(month);
			if(temp==null){
				temp=Maps.newTreeMap();
				map.put(month, temp);
			}
			Map<String, SaleReportMonthType> lineMap = temp.get(line);
			if (lineMap == null) {
				lineMap = Maps.newLinkedHashMap();
				temp.put(line, lineMap);
			}
			SaleReportMonthType monthType = lineMap.get(country);
			if (monthType == null) {
				monthType = new SaleReportMonthType();
				monthType.setSales(sales);
				monthType.setProfits(profits);
				lineMap.put(country, monthType);
			} else {
				monthType.setSales(monthType.getSales() + sales);
				monthType.setProfits(monthType.getProfits() + profits);
			}
			String tempCountry = "";
			if ("com,ca,uk".contains(country)) {
				tempCountry = "en";
			} else {
				tempCountry = "nonEn";
			}
			SaleReportMonthType countryMonthType = lineMap.get(tempCountry);
			if (countryMonthType == null) {
				countryMonthType = new SaleReportMonthType();
				countryMonthType.setSales(sales);
				countryMonthType.setProfits(profits);
				lineMap.put(tempCountry, countryMonthType);
			} else {
				countryMonthType.setSales(countryMonthType.getSales() + sales);
				countryMonthType.setProfits(countryMonthType.getProfits() + profits);
			}

			SaleReportMonthType total = lineMap.get("total");
			if (total == null) {
				total = new SaleReportMonthType();
				lineMap.put("total", total);
				total.setSales(sales);
				total.setProfits(profits);
			} else {
				total.setSales(total.getSales() + sales);
				total.setProfits(total.getProfits() + profits);
			}
			//每条线汇总
			Map<String, SaleReportMonthType> totalLineMap = temp.get("total");
			if (totalLineMap == null) {
				totalLineMap = Maps.newLinkedHashMap();
				temp.put("total", totalLineMap);
			}
			SaleReportMonthType totalLineMonthType = totalLineMap.get(country);
			if (totalLineMonthType == null) {
				totalLineMonthType = new SaleReportMonthType();
				totalLineMonthType.setSales(sales);
				totalLineMonthType.setProfits(profits);
				totalLineMap.put(country, totalLineMonthType);
			} else {
				totalLineMonthType.setSales(totalLineMonthType.getSales() + sales);
				totalLineMonthType.setProfits(totalLineMonthType.getProfits() + profits);
			}
			SaleReportMonthType tempCountryLineMonthType = totalLineMap.get(tempCountry);
			if (tempCountryLineMonthType == null) {
				tempCountryLineMonthType = new SaleReportMonthType();
				tempCountryLineMonthType.setSales(sales);
				tempCountryLineMonthType.setProfits(profits);
				totalLineMap.put(tempCountry, tempCountryLineMonthType);
			} else {
				tempCountryLineMonthType.setSales(tempCountryLineMonthType.getSales() + sales);
				tempCountryLineMonthType.setProfits(tempCountryLineMonthType.getProfits() + profits);
			}

			SaleReportMonthType totalGoal = totalLineMap.get("total");
			if (totalGoal == null) {
				totalGoal = new SaleReportMonthType();
				totalLineMap.put("total", totalGoal);
				totalGoal.setSales(sales);
				totalGoal.setProfits(profits);
			} else {
				totalGoal.setSales(totalGoal.getSales() + sales);
				totalGoal.setProfits(totalGoal.getProfits() + profits);
			}
		}
		return map;
	}
	
	/**
	 * 按产品线查询运营负责人,区分英语国家和非英语国家
	 * @param month
	 * @return  [产品线[国家   salesName]]
	 */
	public Map<String,Map<String, String>> getAllLineSales(){
		String sql ="SELECT DISTINCT(b.`name`) as user,c.`name`,a.`country` FROM psi_product_group_user AS a,sys_user AS b,`psi_product_type_dict` c "+
				" WHERE a.`responsible` like CONCAT('%',b.`id` ,'%') AND c.`id`=a.`product_group_id` AND LENGTH(b.`id`)>10"+
				" AND a.`del_flag`='0' AND c.`del_flag`='0' AND a.`product_group_id`!='0' GROUP BY b.`name`,a.`country`,a.`product_group_id`";
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql);
		Map<String, Map<String, String>> map = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			String name = objs[0].toString();
			String line = objs[1].toString();
			if (line != null && line.contains(" ")) {
				line = line.split(" ")[0];
			}
			String country = objs[2].toString();
			Map<String, String> lineMap = map.get(line);
			if (lineMap == null) {
				lineMap = Maps.newHashMap();
				map.put(line, lineMap);
			}
			lineMap.put(country, name);
		}
		return map;
	}

	/**
	 * 统计月度仓储费用
	 * @param month 月份(yyyyMM)
	 * @param avgRateMap 月度平均汇率
	 * @return Map<country, Map<productName, fee>> fee>0.005
	 */
	@Transactional(readOnly = false)
	public void updateStorageFee(String month, Map<String, Float> avgRateMap) {
		if (getStorageFeeByMonth(month, "1")) {
			return;	//统计过了不需要再统计
		}
		String sql = "SELECT t.`country`,t.`product_name`,SUM(t.`estimated_monthly_storage_fee`),t.`currency`,t.`account_name`"+
				" FROM `amazoninfo_monthly_storage_fees` t WHERE t.`month`=:p1 AND t.`estimated_monthly_storage_fee`>0"+
				" GROUP BY t.`country`,t.`account_name`,t.`product_name`";
		StringBuilder sb = new StringBuilder(month);
		sb.insert(4, "-");
		String temp = sb.toString();
		//产品类型对应的产品线关系
		Map<String, String> typeLine = dictService.getTypeLine(month);
		Map<String, String> newTypeLine = dictService.getTypeLine(null);
		Map<String, String> nameType = psiProductService.findProductTypeMap();
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(temp));
		List<SaleReportMonthType> reportMonthTypes = Lists.newArrayList();
		for (Object[] obj : list) {
			String country = obj[0].toString();
			String productName = obj[1] == null?"":obj[1].toString();
			if (StringUtils.isEmpty(productName)) {
				continue;
			}
			float fee = obj[2]==null?0:Float.parseFloat(obj[2].toString());
			String currency = obj[3].toString();
			String accountName = obj[4]==null?SaleProfitService.accountMap.get(country):obj[4].toString();
			//按月均汇率转换成欧元
			fee = fee * MathUtils.getRate(currency, "EUR", avgRateMap);
			if (fee<0.005f) {
				continue;	//费用太少，忽略
			}
			//更新费用到月度报表中
			SaleReportMonthType saleReportMonthType = getByUnique(month, country, productName, accountName);
			if (saleReportMonthType == null) {
				//没有销售记录,插入一条数据
				saleReportMonthType = new SaleReportMonthType();
				saleReportMonthType.setProductName(productName);
				saleReportMonthType.setCountry(country);
				saleReportMonthType.setAccountName(accountName);
				saleReportMonthType.setMonth(month);
				String type = nameType.get(productName);
				if (StringUtils.isEmpty(type)) {
					continue;
				}
				String line = typeLine.get(type.toLowerCase());
				if (StringUtils.isEmpty(line)) {//可能有新增加的品类,之前没有录入产品线关系
					line = newTypeLine.get(type.toLowerCase());
				}
				saleReportMonthType.setType(type);
				saleReportMonthType.setLine(line);
			}
			saleReportMonthType.setStorageFee(-fee);
			reportMonthTypes.add(saleReportMonthType);
		}
		if (reportMonthTypes.size() > 0) {
			saveList(reportMonthTypes);
		}
	}

	/**
	 * 统计长期仓储费用（长期费用每年收两次，插入收费当月）
	 * @param month 月份(yyyyMM)
	 * @param avgRateMap 月度平均汇率
	 * @return Map<country, Map<productName, fee>> fee>0.005
	 */
	@Transactional(readOnly = false)
	public void updateLongStorageFee(String month, Map<String, Float> avgRateMap) {
		if (getStorageFeeByMonth(month, "2")) {
			return;	//统计过了不需要再统计
		}
		String sql = "SELECT t.`country`,t.`product_name`,t.`currency`,"+
				" SUM(t.`twelfth_mo_long_terms_storage_fee`+t.`six_mo_long_terms_storage_fee`),t.`account_name`"+
				" FROM `amazoninfo_long_term_storage_fees` t WHERE DATE_FORMAT(t.`snapshot_date`,'%Y%m')=:p1 "+
				" GROUP BY t.`account_name`,t.`product_name`";
		//产品类型对应的产品线关系
		Map<String, String> typeLine = dictService.getTypeLine(month);
		Map<String, String> newTypeLine = dictService.getTypeLine(null);
		Map<String, String> nameType = psiProductService.findProductTypeMap();
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(month));
		List<SaleReportMonthType> reportMonthTypes = Lists.newArrayList();
		for (Object[] obj : list) {
			String country = obj[0].toString();
			String productName = obj[1] == null?"":obj[1].toString();
			if (StringUtils.isEmpty(productName)) {
				continue;
			}
			String currency = obj[2].toString();
			float fee = obj[3]==null?0:Float.parseFloat(obj[3].toString());
			String accountName = obj[4].toString();
			//按月均汇率转换成欧元
			fee = fee * MathUtils.getRate(currency, "EUR", avgRateMap);
			if (fee<0.005f) {
				continue;	//费用太少，忽略
			}
			//更新费用到月度报表中
			SaleReportMonthType saleReportMonthType = getByUnique(month, country, productName, accountName);
			if (saleReportMonthType == null) {
				//没有销售记录,插入一条数据
				saleReportMonthType = new SaleReportMonthType();
				saleReportMonthType.setProductName(productName);
				saleReportMonthType.setCountry(country);
				saleReportMonthType.setAccountName(accountName);
				saleReportMonthType.setMonth(month);
				String type = nameType.get(productName);
				if (StringUtils.isEmpty(type)) {
					continue;
				}
				String line = typeLine.get(type.toLowerCase());
				if (StringUtils.isEmpty(line)) {//可能有新增加的品类,之前没有录入产品线关系
					line = newTypeLine.get(type.toLowerCase());
				}
				saleReportMonthType.setType(type);
				saleReportMonthType.setLine(line);
			}
			saleReportMonthType.setLongStorageFee(-fee);
			reportMonthTypes.add(saleReportMonthType);
		}
		if (reportMonthTypes.size() > 0) {
			saveList(reportMonthTypes);
		}
	}
	
	/**
	 * 判断某个月份是否已经统计过仓储费
	 * @param month	yyyyMM
	 * @param type	1：月度的  2：长期的
	 * @return true 已统计  false 未统计
	 */
	public boolean getStorageFeeByMonth(String month, String type){
		String sql = "SELECT t.country,SUM(IFNULL(t.`storage_fee`,0)),SUM(IFNULL(t.`long_storage_fee`,0)) " +
				" FROM `amazoninfo_report_month_type` t WHERE t.`month`=:p1 group by t.country";
		List<Object[]> list = saleReportMonthTypeDao.findBySql(sql, new Parameter(month));
		boolean flag = true;
		for (Object[] obj : list) {
			if (obj[0] == null) {
				continue;
			}
			float fee = obj[1]==null?0:Float.parseFloat(obj[1].toString());
			if ("2".equals(type)) {	//长期仓储费
				fee = obj[2]==null?0:Float.parseFloat(obj[2].toString());
			}
			if (Math.abs(fee) < 1) {	//只要有国家没有统计的，就说明没有统计完
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * 补齐产品分类属性
	 */
	@Transactional(readOnly=false)
	public void updateProductAttr(){
		DetachedCriteria dc = saleReportMonthTypeDao.createDetachedCriteria();
		dc.add(Restrictions.ge("month", "201801"));
		dc.add(Restrictions.isNull("productAttr"));
		List<SaleReportMonthType> list = saleReportMonthTypeDao.find(dc);
		//总的定位
		Map<String, String> positionMap = psiProductEliminateService.findProductPositionByCountry(null);
		Map<String, PsiProductEliminate> countryPositionMap = psiProductEliminateService.findProductCountryAttr();
		for (SaleReportMonthType saleReportMonthType : list) {
			String country = saleReportMonthType.getCountry();
			String productName = saleReportMonthType.getProductName();
			String key = productName+ "_" + country;
			PsiProductEliminate eliminate = countryPositionMap.get(key);
			if (eliminate != null ) {
				if ("新品".equals(eliminate.getIsNew())) {
					saleReportMonthType.setProductAttr("新品");
				} else if (StringUtils.isNotEmpty(eliminate.getIsSale())) {
					saleReportMonthType.setProductAttr(eliminate.getIsSale());
				}
			}
			//上一步未匹配国家产品定位,按整个产品规则算定位
			if (StringUtils.isEmpty(saleReportMonthType.getProductAttr())) {
				String isSale = positionMap.get(productName);
				if (StringUtils.isNotEmpty(isSale)) {
					String attr = DictUtils.getDictLabel(isSale, "product_position", "");
					if (StringUtils.isNotEmpty(attr)) {
						saleReportMonthType.setProductAttr(attr);
					}
				}
			}
		}
		saveList(list);
	}

	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		SaleReportMonthTypeService  service= applicationContext.getBean(SaleReportMonthTypeService.class);
		String year = "2015";
		for (int i = 1; i < 13; i++) {
			String month = year + (i<10?"0"+i:i);
			service.saveOrUpdate(month);
		}
		year = "2016";
		for (int i = 1; i < 7; i++) {
			String month = year + (i<10?"0"+i:i);
			service.saveOrUpdate(month);
		}
		
		applicationContext.close();
	}
}
