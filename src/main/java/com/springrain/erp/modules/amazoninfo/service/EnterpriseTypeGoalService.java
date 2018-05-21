package com.springrain.erp.modules.amazoninfo.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause.Entry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.EnterpriseTypeGoalDao;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseGoal;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseTotalGoal;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseTypeGoal;
import com.springrain.erp.modules.amazoninfo.entity.SaleReportMonthType;
import com.springrain.erp.modules.amazoninfo.web.EnterpriseGoalController;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Component
@Transactional(readOnly = true)
public class EnterpriseTypeGoalService extends BaseService {

	@Autowired
	private EnterpriseTypeGoalDao enterpriseTypeGoalDao;
	
	@Autowired
	private SaleReportMonthTypeService saleReportMonthTypeService;
	
	@Autowired
	private EnterpriseGoalService enterpriseGoalService;
	
	@Autowired
	private PsiProductTypeGroupDictService dictService;
	
	public EnterpriseTypeGoal get(Integer id) {
		return enterpriseTypeGoalDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(EnterpriseTypeGoal enterpriseTypeGoal) {
		enterpriseTypeGoalDao.save(enterpriseTypeGoal);
	}
	
	@Transactional(readOnly = false)
	public void save(List<EnterpriseTypeGoal> enterpriseTypeGoals) {
		enterpriseTypeGoalDao.save(enterpriseTypeGoals);
	}
	
	/**
	 * 
	 * @param month
	 * @return[type[country	goal]]
	 */
	public Map<String, Map<String, EnterpriseTypeGoal>> findGoalByMonth(String month){
		Map<String, Map<String, EnterpriseTypeGoal>> rs = Maps.newLinkedHashMap();
		DetachedCriteria dc = enterpriseTypeGoalDao.createDetachedCriteria();
		dc.add(Restrictions.eq("month", month));
		List<EnterpriseTypeGoal> list = enterpriseTypeGoalDao.find(dc);
		for (EnterpriseTypeGoal enterpriseTypeGoal : list) {
			String type = enterpriseTypeGoal.getProductType();
			String country = enterpriseTypeGoal.getCountry();
			Map<String, EnterpriseTypeGoal> typeMap = rs.get(type);
			if (typeMap == null) {
				typeMap = Maps.newLinkedHashMap();
				rs.put(type, typeMap);
			}
			typeMap.put(country, enterpriseTypeGoal);
		}
		return rs;
	}
	
	/**
	 * 
	 * @param month
	 * @return[type[country	goal]]
	 */
	public Map<String, Map<String, EnterpriseTypeGoal>> findTypeGoalByMonth(String month){
		Map<String, Map<String, EnterpriseTypeGoal>> rs = Maps.newLinkedHashMap();
		String sql = "SELECT t.`product_type`,t.`country`,t.`line`,t.`sales_goal`,t.`profit_goal` FROM `amazoninfo_enterprise_type_goal` t WHERE t.`month`=:p1";
		List<Object[]> list = enterpriseTypeGoalDao.findBySql(sql, new Parameter(month));
		for (Object[] obj : list) {
			String productType = obj[0].toString();
			String country = obj[1].toString();
			String line = obj[2].toString();
			float salesGoal = obj[3] == null ? 0 : Float.parseFloat(obj[3].toString());
			float profitGoal = obj[4] == null ? 0 : Float.parseFloat(obj[4].toString());
			Map<String, EnterpriseTypeGoal> typeMap = rs.get(productType);
			if (typeMap == null) {
				typeMap = Maps.newLinkedHashMap();
				rs.put(productType, typeMap);
			}
			EnterpriseTypeGoal enterpriseTypeGoal = new EnterpriseTypeGoal();
			enterpriseTypeGoal.setProductType(productType);
			enterpriseTypeGoal.setCountry(country);
			enterpriseTypeGoal.setLine(line);
			enterpriseTypeGoal.setSalesGoal(salesGoal);
			enterpriseTypeGoal.setProfitGoal(profitGoal);
			typeMap.put(country, enterpriseTypeGoal);
			
			EnterpriseTypeGoal totalGoal = typeMap.get("total");
			if (totalGoal == null) {
				totalGoal = new EnterpriseTypeGoal();
				typeMap.put("total", totalGoal);
				totalGoal.setProductType(productType);
				totalGoal.setLine(line);
				totalGoal.setSalesGoal(salesGoal);
				totalGoal.setProfitGoal(profitGoal);
			} else {
				totalGoal.setSalesGoal(totalGoal.getSalesGoal() + salesGoal);
				totalGoal.setProfitGoal(totalGoal.getProfitGoal() + profitGoal);
			}
		}
		return rs;
	}
	
	//根据条件查询唯一记录
	public EnterpriseTypeGoal getMonthTypeGoal(String country, String month, String type) {
		DetachedCriteria dc = enterpriseTypeGoalDao.createDetachedCriteria();
		dc.add(Restrictions.eq("month", month));
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.eq("productType", type));
		List<EnterpriseTypeGoal> list = enterpriseTypeGoalDao.find(dc);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据销售额计算动态目标,未完成的目标分摊到后面三个月
	 * @param date 当前月日期
	 * @throws ParseException 
	 */
	@Transactional(readOnly = false)
	public void updateDynamicGoal(Date date) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		String month = format.format(DateUtils.addMonths(date, -1));
		int monthNum = Integer.parseInt(month.substring(4));
		String year = month.substring(0, 4);
		// 获取上个月总销售额
		float sales = saleReportMonthTypeService.getTotalSalesByMonth(month);
		// 获取上个月总目标
		float goals = enterpriseGoalService.getSysTotalGoalByMonth(month);

		int length = 3;	//默认分摊到后三个月
		if (12 - monthNum < 3) {	//不足三个月
			length = 12 - monthNum;
		}
		if (sales >= goals) {	//目标已完成,不需要分摊
			if (length > 0) {	//按加权平均法分配月目标
				int goalMonthNum = monthNum + 1;
				String goalMonth = year+ (goalMonthNum < 10 ? "0" + goalMonthNum : goalMonthNum);
				Float totalGoal = enterpriseGoalService.getSysTotalGoalByMonth(goalMonth); // 获取指定月的总目标
				autoGoal(totalGoal, format.parse(goalMonth), date);
			}
			return;
		}
		//float sale = (goals - sales)/length;	//分摊到的目标
		for (int i = 1; i <= length; i++) {
			int goalMonthNum = monthNum + i;
			String goalMonth = year+ (goalMonthNum < 10 ? "0" + goalMonthNum : goalMonthNum);
//			Float totalGoal = enterpriseGoalService.getSysTotalGoalByMonth(goalMonth); // 获取指定月的总目标
//			autoGoal(totalGoal+sale, format.parse(goalMonth), date);
			//9月份开始不分摊,按最原始目标分配
			Float totalGoal = EnterpriseGoalController.rs.get(goalMonth).floatValue(); // 获取指定月的总目标
			autoGoal(totalGoal, format.parse(goalMonth), date);
		}
		for (int i = 1; i <= length; i++) {
			int goalMonthNum = monthNum + i;
			String goalMonth = year+ (goalMonthNum < 10 ? "0" + goalMonthNum : goalMonthNum);
			//更新系统动态计算后的总目标
			Float sysTotalGoal = enterpriseGoalService.getSysTotalGoalByMonth(goalMonth);
			if (sysTotalGoal != null) {
				EnterpriseTotalGoal countryTotalGoal = enterpriseGoalService.findByCountryAndMonth(goalMonth, "total");
				if(countryTotalGoal == null){
					countryTotalGoal = new EnterpriseTotalGoal();
					countryTotalGoal.setCountry("total");
					countryTotalGoal.setMonth(goalMonth);
					countryTotalGoal.setCreateDate(new Date());
					countryTotalGoal.setGoal(sysTotalGoal);
				} else {
					countryTotalGoal.setCreateDate(new Date());
					countryTotalGoal.setGoal(sysTotalGoal);
				}
				enterpriseGoalService.save(countryTotalGoal);
			}
		}
	}
	
	/**
	 * 根据前两个月数据用加权平均法计算各平台比例
	 * @param date 待计算目标月
	 * @return [类型[国家  占比]]  类型  1：销售额  2：利润
	 */
	public Map<String, Map<String, Float>> getCountryRatio(Date date) {
		Map<String, Map<String, Float>> rs = Maps.newHashMap();
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		String month1 = monthFormat.format(DateUtils.addMonths(date, -2));
		if ("201608".equals(month1)) {
			month1 = "201609";
		}
		String month2 = monthFormat.format(DateUtils.addMonths(date, -1));
		if ("201608".equals(month2)) {
			month2 = "201607";
		}
		Map<String,Map<String, SaleReportMonthType>> sales = saleReportMonthTypeService.getSalesAndProfitByMonth(month1, month2);
		
		Map<String, Float> salesMap = rs.get("1");
		if (salesMap == null) {
			salesMap = Maps.newHashMap();
			rs.put("1", salesMap);
		}
		float saleTotal1 = sales.get(month1).get("total").getSales();
		float saleTotal2 = sales.get(month2).get("total").getSales();
		float weight1 = saleTotal1/(saleTotal1 + saleTotal2);
		float weight2 = saleTotal2/(saleTotal1 + saleTotal2);
		List<String> countryList = Lists.newArrayList("en","de","fr","it","es","jp");
		for (String country : countryList) {
			float sale1 = 0f;
			float sale2 = 0f;
			sale1 = sales.get(month1).get(country).getSales();
			sale2 = sales.get(month2).get(country).getSales();
			float ratio = sale1 / saleTotal1 * weight1 + sale2 / saleTotal2 * weight2;
			//6月份
			if ("201606".equals(monthFormat.format(date))) {
				if ("de".equals(country)) {
					ratio = 0.283336f;
				}
				if ("fr".equals(country)) {
					ratio = 0.084621f;
				}
				if ("jp".equals(country)) {
					ratio = 0.086111f;
				}
				if ("it".equals(country)) {
					ratio = 0.044888f;
				}
				if ("es".equals(country)) {
					ratio = 0.015152f;
				}
				if ("en".equals(country)) {
					ratio = 0.485892f;
				}
			}
			salesMap.put(country, ratio);
		}
		
		Map<String, Float> profitMap = rs.get("2");
		if (profitMap == null) {
			profitMap = Maps.newHashMap();
			rs.put("2", profitMap);
		}
		//利润数据由于亚马逊结算报告的原因延期较长,往前推一个月计算,按产品类型计算利润 比例时不往前推,存在某产品类型数量不一致的情况
		month2 = monthFormat.format(DateUtils.addMonths(date, -3));
		if ("201608".equals(month2)) {
			month2 = "201609";
		}
		sales = saleReportMonthTypeService.getSalesAndProfitByMonth(month2, month1);
		float profitTotal1 = sales.get(month1).get("total").getProfits();
		float profitTotal2 = sales.get(month2).get("total").getProfits();
		weight1 = profitTotal1/(profitTotal1 + profitTotal2);
		weight2 = profitTotal2/(profitTotal1 + profitTotal2);
		for (String country : countryList) {
			float profit1 = sales.get(month1).get(country).getProfits();
			float profit2 = sales.get(month2).get(country).getProfits();
			float ratio = profit1 / profitTotal1 * weight1 + profit2 / profitTotal2 * weight2;
			profitMap.put(country, ratio);
		}
		return rs;
	}
	
	/**
	 * 根据前两个月数据计算各平台产品类型实际占比
	 * @param date 待计算目标月
	 * @return [类型[国家[产品类型  占比]]]  类型  1：销售额  2：利润
	 */
	public Map<String, Map<String, Map<String, Float>>> getProductTypeRatio(Date date) {
		Map<String, Map<String, Map<String, Float>>> rs = Maps.newHashMap();
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		String month1 = monthFormat.format(DateUtils.addMonths(date, -2));
		if ("201608".equals(month1)) {	//8月分de数据受8.7事件影响导致利润数据大多为负
			month1 = "201609";
		}
		String month2 = monthFormat.format(DateUtils.addMonths(date, -1));
		if ("201608".equals(month2)) {
			month2 = "201607";
		}
		Map<String,Map<String, SaleReportMonthType>> sales = saleReportMonthTypeService.getSalesAndProfitByType(month1, month2);
		
		Map<String, Map<String, Float>> salesMap = Maps.newHashMap();
		rs.put("1", salesMap);
		Map<String, Map<String, Float>> profitMap = Maps.newHashMap();
		rs.put("2", profitMap);
		
		for (Map.Entry<String,Map<String, SaleReportMonthType>> entrySales : sales.entrySet()) {  
			String country =entrySales.getKey();
			Map<String, SaleReportMonthType> countryMap = entrySales.getValue();
			float totalSale =  countryMap.get("total").getSales();
			float totalProfit =  countryMap.get("total").getProfits();
			Map<String, Float> saleRatio = Maps.newHashMap();
			salesMap.put(country, saleRatio);
			Map<String, Float> profitRatio = Maps.newHashMap();
			profitMap.put(country, profitRatio);
			for (Map.Entry<String,SaleReportMonthType> entry : countryMap.entrySet()) {  
			    String type =entry.getKey();
				if ("total".equals(type)) {
					continue;
				}
				SaleReportMonthType monthType =entry.getValue();
				saleRatio.put(type, monthType.getSales()/totalSale);
				profitRatio.put(type, monthType.getProfits()/totalProfit);
			}
		}
		return rs;
	}
	
	/**
	 * 
	 * @param goal 全平台月总目标
	 * @param date 调整月目标时间(分摊三个月)
	 * @param ratioDate 当前计算月目标时间(用于标记计算前两个月销售额比例)
	 */
	@Transactional(readOnly=false)
	public void autoGoal(float saleGoal, Date date, Date ratioDate){
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		String month = monthFormat.format(date);
		if ("201605".equals(month)) {	//五月份数据
			saleGoal = 2521228f;
		}
		float profitGoal = saleGoal * 0.27f;	//全平台总利润比例
		//分国家占比
		Map<String, Map<String, Float>> countryRatio = getCountryRatio(ratioDate);
		//各国家分产品类型占比
		Map<String, Map<String, Map<String, Float>>> typeRatio = getProductTypeRatio(ratioDate);
		//产品类型对应的产品线关系
		Map<String, String> typeLine = dictService.getTypeLine(null);
		
		List<EnterpriseTypeGoal> enterpriseTypeGoals = Lists.newArrayList();
		Date createDate = new Date();
		User sysUser = UserUtils.getUserById("1");
		Map<String, Float> tempMap=countryRatio.get("1");
		for (Map.Entry<String,Float> entry : tempMap.entrySet()) {  
			String country=entry.getKey();
			float countrySaleGoal = saleGoal * entry.getValue();
			if ("201605".equals(month)) {	//五月份目标已固定
				if ("de".equals(country)) {
					countrySaleGoal = 680863;
				}
				if ("fr".equals(country)) {
					countrySaleGoal = 261720;
				}
				if ("jp".equals(country)) {
					countrySaleGoal = 195458;
				}
				if ("it".equals(country)) {
					countrySaleGoal = 124269;
				}
				if ("es".equals(country)) {
					countrySaleGoal = 51105;
				}
				if ("en".equals(country)) {
					countrySaleGoal = 1207813;
				}
			}
			float countryProfitGoal = profitGoal * countryRatio.get("2").get(country);
			if ("en".equals(country)) {
				List<String> list = Lists.newArrayList("com","uk","ca");
				for (String enCountry : list) {
					EnterpriseTotalGoal totalGoal = enterpriseGoalService.findByCountryAndMonth(month, enCountry);
					if (totalGoal == null) {
						totalGoal = new EnterpriseTotalGoal();
						totalGoal.setCountry(enCountry);
						totalGoal.setCreateDate(createDate);
						totalGoal.setCreateUser(sysUser);
						totalGoal.setMonth(month);
						totalGoal.setGoal(countrySaleGoal * EnterpriseGoalService.countryRatio().get(enCountry));
						totalGoal.setProfitGoal(countryProfitGoal * EnterpriseGoalService.countryRatio().get(enCountry));
					} else {
						totalGoal.setGoal(countrySaleGoal * EnterpriseGoalService.countryRatio().get(enCountry));
						totalGoal.setProfitGoal(countryProfitGoal * EnterpriseGoalService.countryRatio().get(enCountry));
						totalGoal.setCreateDate(createDate);
						totalGoal.setCreateUser(sysUser);
					}
					enterpriseGoalService.save(totalGoal);
				}
			} else {
				EnterpriseTotalGoal totalGoal = enterpriseGoalService.findByCountryAndMonth(month, country);
				if (totalGoal == null) {
					totalGoal = new EnterpriseTotalGoal();
					totalGoal.setCountry(country);
					totalGoal.setCreateDate(createDate);
					totalGoal.setCreateUser(sysUser);
					totalGoal.setMonth(month);
					totalGoal.setGoal(countrySaleGoal);
					totalGoal.setProfitGoal(countryProfitGoal);
				} else {
					totalGoal.setGoal(countrySaleGoal);
					totalGoal.setProfitGoal(countryProfitGoal);
					totalGoal.setCreateDate(createDate);
					totalGoal.setCreateUser(sysUser);
				}
				enterpriseGoalService.save(totalGoal);
			}
			//System.out.println(country + "\t" + countrySaleGoal + "\t" + countryProfitGoal);
			Map<String, Float> tempCountryMap=typeRatio.get("1").get(country);
			for (Map.Entry<String,Float> entryRs : tempCountryMap.entrySet()) {  
				String type=entryRs.getKey();
				float typeSaleGoal = countrySaleGoal * entryRs.getValue();
				if (typeSaleGoal < 10) {	//10以下的不计算
					typeSaleGoal = 0;
				}
				float typeProfitGoal = countryProfitGoal * typeRatio.get("2").get(country).get(type);
				if (typeProfitGoal < 10) {	//10以下的不计算
					typeProfitGoal = 0;
				}
				EnterpriseTypeGoal enterpriseTypeSaleGoal = getMonthTypeGoal(country, month, type);
				if (enterpriseTypeSaleGoal == null) {
					enterpriseTypeSaleGoal = new EnterpriseTypeGoal();
				}
				enterpriseTypeSaleGoal.setCountry(country);
				enterpriseTypeSaleGoal.setCreateDate(createDate);
				enterpriseTypeSaleGoal.setCreateUser(sysUser);
				enterpriseTypeSaleGoal.setSalesGoal(typeSaleGoal);
				enterpriseTypeSaleGoal.setProfitGoal(typeProfitGoal);
				enterpriseTypeSaleGoal.setMonth(month);
				enterpriseTypeSaleGoal.setProductType(type);
				String line = typeLine.get(type.toLowerCase());
				if (StringUtils.isEmpty(line) && typeSaleGoal < 10) {
					continue;
				}
				if ("F".equals(line) && "201605".equals(month)) {
					line = "A";
				}
				enterpriseTypeSaleGoal.setLine(line);
				enterpriseTypeGoals.add(enterpriseTypeSaleGoal);
			}
		}
		if (enterpriseTypeGoals.size() > 0) {
			save(enterpriseTypeGoals);
		}
		
		//保存完之后分平台按产品线汇总
		autoLineGoal(month);
	}
	
	/**
	 * 根据产品类型目标汇总产品线目标
	 * @param month 汇总月份
	 */
	@Transactional(readOnly=false)
	public void autoLineGoal(String month) {
		Date createDate = new Date();
		User sysUser = UserUtils.getUserById("1");
		Map<String, PsiProductTypeGroupDict> lineMap = Maps.newHashMap();
		List<PsiProductTypeGroupDict> lineList = dictService.getAllList();
		for (PsiProductTypeGroupDict psiProductTypeGroupDict : lineList) {
			lineMap.put(psiProductTypeGroupDict.getName().substring(0, 1), psiProductTypeGroupDict);
		}
		String sql = "SELECT t.`country`,t.`line`,SUM(t.`sales_goal`),SUM(t.`profit_goal`) FROM `amazoninfo_enterprise_type_goal` t WHERE t.`month`=:p1 GROUP BY t.`country`,t.`line`";
		List<Object[]> list = enterpriseTypeGoalDao.findBySql(sql, new Parameter(month));
		List<EnterpriseGoal> enterpriseGoals = Lists.newArrayList();
		for (Object[] obj : list) {
			if (obj[1] == null) {
				continue;
			}
			String country = obj[0].toString();
			String line = obj[1].toString();
			float salesGoal = obj[2]==null?0f:Float.parseFloat(obj[2].toString());
			float profitsGoal = obj[3]==null?0f:Float.parseFloat(obj[3].toString());
			EnterpriseGoal enterpriseGoal = enterpriseGoalService.findByCountryAndMonthAndLine(month, country, lineMap.get(line).getId());
			if (enterpriseGoal == null) {
				enterpriseGoal = new EnterpriseGoal();
				enterpriseGoal.setMonth(month);
				enterpriseGoal.setCountry(country);
				enterpriseGoal.setGoal(salesGoal);
				enterpriseGoal.setProfitGoal(profitsGoal);
				enterpriseGoal.setProductLine(lineMap.get(line));
				enterpriseGoal.setCreateDate(createDate);
				enterpriseGoal.setCreateUser(sysUser);
			} else {
				enterpriseGoal.setGoal(salesGoal);
				enterpriseGoal.setProfitGoal(profitsGoal);
				enterpriseGoal.setCreateDate(createDate);
				enterpriseGoal.setCreateUser(sysUser);
			}
			enterpriseGoals.add(enterpriseGoal);
		}
		if (enterpriseGoals.size() > 0) {
			enterpriseGoalService.save(enterpriseGoals);
		}
	}
	
	@Transactional(readOnly = false)
	public void update(EnterpriseTypeGoal enterpriseTypeGoal) {
		String month = enterpriseTypeGoal.getMonth();
		String country = enterpriseTypeGoal.getCountry();
		String line = enterpriseTypeGoal.getLine();
		//汇总产品线目标
		String sql = "SELECT SUM(t.`sales_goal`),SUM(t.`profit_goal`) FROM `amazoninfo_enterprise_type_goal` t WHERE t.`month`=:p1 AND t.`country`=:p2 AND t.`line`=:p3 ";
		List<Object[]> list = enterpriseTypeGoalDao.findBySql(sql, new Parameter(month, country, line));
		Map<String, PsiProductTypeGroupDict> lineMap = Maps.newHashMap();
		List<PsiProductTypeGroupDict> lineList = dictService.getAllList();
		for (PsiProductTypeGroupDict psiProductTypeGroupDict : lineList) {
			lineMap.put(psiProductTypeGroupDict.getName().substring(0, 1), psiProductTypeGroupDict);
		}
		for (Object[] obj : list) {
			float salesGoal = obj[0]==null?0f:Float.parseFloat(obj[0].toString());
			float profitsGoal = obj[1]==null?0f:Float.parseFloat(obj[1].toString());
			EnterpriseGoal enterpriseGoal = enterpriseGoalService.findByCountryAndMonthAndLine(month, country, lineMap.get(line).getId());
			if (enterpriseGoal == null) {
				enterpriseGoal = new EnterpriseGoal();
				enterpriseGoal.setMonth(month);
				enterpriseGoal.setCountry(country);
				enterpriseGoal.setGoal(salesGoal);
				enterpriseGoal.setProfitGoal(profitsGoal);
				enterpriseGoal.setProductLine(lineMap.get(line));
				enterpriseGoal.setCreateDate(new Date());
				enterpriseGoal.setCreateUser(UserUtils.getUser());
			} else {
				enterpriseGoal.setGoal(salesGoal);
				enterpriseGoal.setProfitGoal(profitsGoal);
				enterpriseGoal.setCreateDate(new Date());
				enterpriseGoal.setCreateUser(UserUtils.getUser());
			}
			enterpriseGoalService.save(enterpriseGoal);
		}
		//汇总国家目标
		sql = "SELECT SUM(t.`sales_goal`),SUM(t.`profit_goal`) FROM `amazoninfo_enterprise_type_goal` t WHERE t.`month`=:p1 AND t.`country`=:p2 AND t.`line`!='E' ";
		list = enterpriseTypeGoalDao.findBySql(sql, new Parameter(month, country));
		for (Object[] obj : list) {
			float salesGoal = obj[0]==null?0f:Float.parseFloat(obj[0].toString());
			float profitsGoal = obj[1]==null?0f:Float.parseFloat(obj[1].toString());
			if ("en".equals(country)) {
				List<String> enCountrylist = Lists.newArrayList("com","uk","ca");
				for (String enCountry : enCountrylist) {
					EnterpriseTotalGoal enterpriseTotalGoal = enterpriseGoalService.findByCountryAndMonth(month, enCountry);
					if (enterpriseTotalGoal == null) {
						enterpriseTotalGoal = new EnterpriseTotalGoal();
						enterpriseTotalGoal.setCountry(enCountry);
						enterpriseTotalGoal.setCreateDate(new Date());
						enterpriseTotalGoal.setCreateUser(UserUtils.getUser());
						enterpriseTotalGoal.setMonth(month);
						enterpriseTotalGoal.setGoal(salesGoal * EnterpriseGoalService.countryRatio().get(enCountry));
						enterpriseTotalGoal.setProfitGoal(profitsGoal * EnterpriseGoalService.countryRatio().get(enCountry));
					} else {
						enterpriseTotalGoal.setGoal(salesGoal * EnterpriseGoalService.countryRatio().get(enCountry));
						enterpriseTotalGoal.setProfitGoal(profitsGoal * EnterpriseGoalService.countryRatio().get(enCountry));
						enterpriseTotalGoal.setCreateDate(new Date());
						enterpriseTotalGoal.setCreateUser(UserUtils.getUser());
					}
					enterpriseGoalService.save(enterpriseTotalGoal);
				}
			} else {
				EnterpriseTotalGoal enterpriseTotalGoal = enterpriseGoalService.findByCountryAndMonth(month, country);
				if (enterpriseTotalGoal == null) {
					enterpriseTotalGoal = new EnterpriseTotalGoal();
					enterpriseTotalGoal.setMonth(month);
					enterpriseTotalGoal.setCountry(country);
					enterpriseTotalGoal.setGoal(salesGoal);
					enterpriseTotalGoal.setProfitGoal(profitsGoal);
					enterpriseTotalGoal.setCreateDate(new Date());
					enterpriseTotalGoal.setCreateUser(UserUtils.getUser());
				} else {
					enterpriseTotalGoal.setGoal(salesGoal);
					enterpriseTotalGoal.setProfitGoal(profitsGoal);
					enterpriseTotalGoal.setCreateDate(new Date());
					enterpriseTotalGoal.setCreateUser(UserUtils.getUser());
				}
				enterpriseGoalService.save(enterpriseTotalGoal);
			}
		}
	}
	
	/**
	 *
	 * @param goal 全平台月总目标
	 * @param date 月目标时间
	 */
	@Transactional(readOnly=false)
	public void updateCountryGoal(float saleGoal, Date date){
		//TODO 更新国家目标,按国家比例分配到具体产品类型然后汇总产品线
		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
		String month = monthFormat.format(date);
		float profitGoal = saleGoal * 0.27f;	//全平台总利润比例
		//分国家占比
		Map<String, Map<String, Float>> countryRatio = getCountryRatio(date);
		//各国家分产品类型占比
		Map<String, Map<String, Map<String, Float>>> typeRatio = getProductTypeRatio(date);
		//产品类型对应的产品线关系
		Map<String, String> typeLine = dictService.getTypeLine(null);
		
		List<EnterpriseTypeGoal> enterpriseTypeGoals = Lists.newArrayList();
		Date createDate = new Date();
		User sysUser = UserUtils.getUserById("1");
		for (Map.Entry<String,Float> entry : countryRatio.get("1").entrySet()) {  
			String country =entry.getKey();
			float countrySaleGoal = saleGoal * entry.getValue();
			float countryProfitGoal = profitGoal * countryRatio.get("2").get(country);
			if ("en".equals(country)) {
				List<String> list = Lists.newArrayList("com","uk","ca");
				for (String enCountry : list) {
					EnterpriseTotalGoal totalGoal = enterpriseGoalService.findByCountryAndMonth(month, enCountry);
					if (totalGoal == null) {
						totalGoal = new EnterpriseTotalGoal();
						totalGoal.setCountry(enCountry);
						totalGoal.setCreateDate(createDate);
						totalGoal.setCreateUser(sysUser);
						totalGoal.setMonth(month);
						totalGoal.setGoal(countrySaleGoal * EnterpriseGoalService.countryRatio().get(enCountry));
						totalGoal.setProfitGoal(countryProfitGoal * EnterpriseGoalService.countryRatio().get(enCountry));
					} else {
						totalGoal.setGoal(countrySaleGoal * EnterpriseGoalService.countryRatio().get(enCountry));
						totalGoal.setProfitGoal(countryProfitGoal * EnterpriseGoalService.countryRatio().get(enCountry));
						totalGoal.setCreateDate(createDate);
						totalGoal.setCreateUser(sysUser);
					}
					enterpriseGoalService.save(totalGoal);
				}
			} else {
				EnterpriseTotalGoal totalGoal = enterpriseGoalService.findByCountryAndMonth(month, country);
				if (totalGoal == null) {
					totalGoal = new EnterpriseTotalGoal();
					totalGoal.setCountry(country);
					totalGoal.setCreateDate(createDate);
					totalGoal.setCreateUser(sysUser);
					totalGoal.setMonth(month);
					totalGoal.setGoal(countrySaleGoal);
					totalGoal.setProfitGoal(countryProfitGoal);
				} else {
					totalGoal.setGoal(countrySaleGoal);
					totalGoal.setProfitGoal(countryProfitGoal);
					totalGoal.setCreateDate(createDate);
					totalGoal.setCreateUser(sysUser);
				}
				enterpriseGoalService.save(totalGoal);
			}
			//System.out.println(country + "\t" + countrySaleGoal + "\t" + countryProfitGoal);
			for (Map.Entry<String,Float> entryTrype : typeRatio.get("1").get(country).entrySet()) {  
			    String type =entryTrype.getKey();
				float typeSaleGoal = countrySaleGoal * entryTrype.getValue();
				if (typeSaleGoal < 10) {	//10以下的不计算
					typeSaleGoal = 0;
				}
				float typeProfitGoal = countryProfitGoal * typeRatio.get("2").get(country).get(type);
				if (typeProfitGoal < 10) {	//10以下的不计算
					typeProfitGoal = 0;
				}
				EnterpriseTypeGoal enterpriseTypeSaleGoal = getMonthTypeGoal(country, month, type);
				if (enterpriseTypeSaleGoal == null) {
					enterpriseTypeSaleGoal = new EnterpriseTypeGoal();
				}
				enterpriseTypeSaleGoal.setCountry(country);
				enterpriseTypeSaleGoal.setCreateDate(createDate);
				enterpriseTypeSaleGoal.setCreateUser(sysUser);
				enterpriseTypeSaleGoal.setSalesGoal(typeSaleGoal);
				enterpriseTypeSaleGoal.setProfitGoal(typeProfitGoal);
				enterpriseTypeSaleGoal.setMonth(month);
				enterpriseTypeSaleGoal.setProductType(type);
				enterpriseTypeSaleGoal.setLine(typeLine.get(type.toLowerCase()));
				enterpriseTypeGoals.add(enterpriseTypeSaleGoal);
			}
		}
		if (enterpriseTypeGoals.size() > 0) {
			save(enterpriseTypeGoals);
		}
		
		//保存完之后分平台按产品线汇总
		autoLineGoal(month);
	}
	
	/**
	 * 按月分产品类型统计目标,不区分平台
	 * @param month
	 * @return[type	goal]
	 */
	public Map<String, EnterpriseTypeGoal> findAllTypeGoalByMonth(String month){
		Map<String, EnterpriseTypeGoal> rs = Maps.newLinkedHashMap();
		String sql = "SELECT t.`product_type`,t.`line`,SUM(t.`sales_goal`),SUM(t.`profit_goal`) FROM `amazoninfo_enterprise_type_goal` t WHERE t.`month`=:p1 GROUP BY t.`product_type`;";
		List<Object[]> list = enterpriseTypeGoalDao.findBySql(sql, new Parameter(month));
		for (Object[] obj : list) {
			String productType = obj[0].toString();
			String line = obj[1].toString();
			float salesGoal = obj[2] == null ? 0 : Float.parseFloat(obj[2].toString());
			float profitGoal = obj[3] == null ? 0 : Float.parseFloat(obj[3].toString());
			
			EnterpriseTypeGoal enterpriseTypeGoal = new EnterpriseTypeGoal();
			enterpriseTypeGoal.setProductType(productType);
			enterpriseTypeGoal.setLine(line);
			enterpriseTypeGoal.setSalesGoal(salesGoal);
			enterpriseTypeGoal.setProfitGoal(profitGoal);
			rs.put(productType, enterpriseTypeGoal);
			
			//if (!"E".equals(line)) {
				EnterpriseTypeGoal totalGoal = rs.get("total");
				if (totalGoal == null) {
					totalGoal = new EnterpriseTypeGoal();
					rs.put("total", totalGoal);
					totalGoal.setProductType(productType);
					totalGoal.setLine(line);
					totalGoal.setSalesGoal(salesGoal);
					totalGoal.setProfitGoal(profitGoal);
				} else {
					totalGoal.setSalesGoal(totalGoal.getSalesGoal() + salesGoal);
					totalGoal.setProfitGoal(totalGoal.getProfitGoal() + profitGoal);
				}
			//}
		}
		return rs;
	}
	
	//更改产品线关系时修改类型目标对应的关系
	@Transactional(readOnly = false)
	public void updateLine(String month, Map<String, String> typeLine){
		DetachedCriteria dc = enterpriseTypeGoalDao.createDetachedCriteria();
		dc.add(Restrictions.eq("month", month));
		List<EnterpriseTypeGoal> list = enterpriseTypeGoalDao.find(dc);
		if (list == null || list.size() == 0) {
			return;
		}
		for (EnterpriseTypeGoal enterpriseTypeGoal : list) {
			enterpriseTypeGoal.setLine(typeLine.get(enterpriseTypeGoal.getProductType().toLowerCase()));
		}
		save(list);
		enterpriseTypeGoalDao.flush();
		autoLineGoal(month);
	}

	public void setSaleReportMonthTypeService(SaleReportMonthTypeService saleReportMonthTypeService) {
		this.saleReportMonthTypeService = saleReportMonthTypeService;
	}
	
}
