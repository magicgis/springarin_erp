/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExcelUtil;
import com.springrain.erp.modules.amazoninfo.dao.EnterpriseGoalDao;
import com.springrain.erp.modules.amazoninfo.dao.EnterpriseTotalGoalDao;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseGoal;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseTotalGoal;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Component
@Transactional(readOnly = true)
public class EnterpriseGoalService extends BaseService {

	@Autowired
	private EnterpriseGoalDao enterpriseGoalDao;
	
	@Autowired
	private EnterpriseTotalGoalDao enterpriseTotalGoalDao;
	
	@Autowired
	private PsiProductTypeGroupDictService dictService;
	
	public EnterpriseGoal get(Integer id) {
		return enterpriseGoalDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(EnterpriseGoal enterpriseGoal) {
		enterpriseGoalDao.save(enterpriseGoal);
	}
	
	@Transactional(readOnly = false)
	public void save(EnterpriseTotalGoal enterpriseTotalGoal) {
		enterpriseTotalGoalDao.save(enterpriseTotalGoal);
	}
	
	public Map<String,Map<String, EnterpriseTotalGoal>> findMonthTotalGoal(EnterpriseGoal enterpriseGoal){
		String sql="select id,month,country,goal from amazoninfo_enterprise_total_goal where month>=:p1 and month<=:p2 and country !='total' order by month";
		Map<String,Map<String, EnterpriseTotalGoal>>  map=Maps.newHashMap();
		List<Object[]> list=enterpriseGoalDao.findBySql(sql, new Parameter(new SimpleDateFormat("yyyyMM").format(enterpriseGoal.getStartMonth()),new SimpleDateFormat("yyyyMM").format(enterpriseGoal.getEndMonth())));
		for (Object[] obj : list) {
			String month = obj[1].toString();
			String country = obj[2].toString();
			float goal = obj[3]==null?0f:Float.parseFloat(obj[3].toString());
			EnterpriseTotalGoal enterpriseTotalGoal = new EnterpriseTotalGoal();
			if (obj[0] != null) {
				enterpriseTotalGoal.setId(Integer.parseInt(obj[0].toString()));
			}
			enterpriseTotalGoal.setMonth(month);
			enterpriseTotalGoal.setCountry(country);
			enterpriseTotalGoal.setGoal(goal);
			
			Map<String,EnterpriseTotalGoal> temp = map.get(month);
			if (temp == null) {
				temp = Maps.newHashMap();
				map.put(month, temp);
			}
			temp.put(country,enterpriseTotalGoal);
			
			EnterpriseTotalGoal totalGoal = temp.get("total");
			if (totalGoal == null) {
				totalGoal = new EnterpriseTotalGoal();
				totalGoal.setCountry("total");
				totalGoal.setMonth(month);
				totalGoal.setGoal(goal);
			} else {
				totalGoal.setGoal(totalGoal.getGoal() + goal);
			}
			temp.put("total", totalGoal);
		}
		return map;
	}
	
	//[月份 [国家   目标]] 月份包含季度(q1/q2/q3/q4)包含总计和英语国家总计
	public Map<String,Map<String, EnterpriseGoal>> findCountryGoalWithTotalAndEn(EnterpriseGoal enterpriseGoal){
		String sql="select id,month,country,goal,profit_goal from amazoninfo_enterprise_total_goal where month>=:p1 and month<=:p2 and country !='total' order by month";
		Map<String,Map<String, EnterpriseGoal>>  map=Maps.newLinkedHashMap();
		List<Object[]> list=enterpriseGoalDao.findBySql(sql, new Parameter(new SimpleDateFormat("yyyyMM").format(enterpriseGoal.getStartMonth()),new SimpleDateFormat("yyyyMM").format(enterpriseGoal.getEndMonth())));
		for (Object[] obj : list) {
			String month = obj[1].toString();
			String country = obj[2].toString();
			String obj3 = obj[3] == null?"0":obj[3].toString();
			Float goal = obj[3] == null?0:Float.parseFloat(obj3);
			Float profitGoal = obj[4] == null?0:Float.parseFloat(obj[4].toString());
			Map<String, EnterpriseGoal> temp=map.get(month);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(month,temp);
			}
			EnterpriseGoal eGoal = new EnterpriseGoal();
			eGoal.setGoal(goal);
			eGoal.setProfitGoal(profitGoal);
			temp.put(country, eGoal);
			//总计
			EnterpriseGoal totalGoal  = temp.get("total");
			if (totalGoal == null) {
				totalGoal = new EnterpriseGoal();
				temp.put("total", totalGoal);
				totalGoal.setGoal(goal);
				totalGoal.setProfitGoal(profitGoal);
			} else {
				totalGoal.setGoal(totalGoal.getGoal() + goal);
				totalGoal.setProfitGoal(totalGoal.getProfitGoal() + profitGoal);
			}
			//英语国家
			if ("com,uk,ca".contains(country)) {
				EnterpriseGoal enTotal = temp.get("en");
				if (enTotal == null) {
					enTotal = new EnterpriseGoal();
					temp.put("en", enTotal);
					enTotal.setGoal(goal);
					enTotal.setProfitGoal(profitGoal);
				} else {
					enTotal.setGoal(enTotal.getGoal() + goal);
					enTotal.setProfitGoal(enTotal.getProfitGoal() + profitGoal);
				}
			}
			//分季节统计
			String q = "q1";	//一季度
			if (month.endsWith("04") || month.endsWith("05") || month.endsWith("06")) {	//二季度
				q = "q2";
			} else if (month.endsWith("07") || month.endsWith("08") || month.endsWith("09")) {	//三季度
				q = "q3";
			} else if (month.endsWith("10") || month.endsWith("11") || month.endsWith("12")) {	//四季度
				q = "q4";
			}
			Map<String, EnterpriseGoal> seasonTemp = map.get(q);
			if (seasonTemp == null) {
				seasonTemp = Maps.newLinkedHashMap();
				map.put(q, seasonTemp);
			}
			EnterpriseGoal seasonGoal = seasonTemp.get(country);
			if (seasonGoal == null) {
				seasonGoal = new EnterpriseGoal();
				seasonTemp.put(country, seasonGoal);
				seasonGoal.setGoal(goal);
				seasonGoal.setProfitGoal(profitGoal);
			} else {
				seasonGoal.setGoal(seasonGoal.getGoal() + goal);
				seasonGoal.setProfitGoal(seasonGoal.getProfitGoal() + profitGoal);
			}
			if ("com,uk,ca".contains(country)) {
				EnterpriseGoal enSeasonGoal = seasonTemp.get("en");
				if (enSeasonGoal == null) {
					enSeasonGoal = new EnterpriseGoal();
					seasonTemp.put("en", enSeasonGoal);
					enSeasonGoal.setGoal(goal);
					enSeasonGoal.setProfitGoal(profitGoal);
				} else {
					enSeasonGoal.setGoal(enSeasonGoal.getGoal() + goal);
					enSeasonGoal.setProfitGoal(enSeasonGoal.getProfitGoal() + profitGoal);
				}
			}
			EnterpriseGoal totalSeasonGoal = seasonTemp.get("total");
			if (totalSeasonGoal == null) {
				totalSeasonGoal = new EnterpriseGoal();
				seasonTemp.put("total", totalSeasonGoal);
				totalSeasonGoal.setGoal(goal);
				totalSeasonGoal.setProfitGoal(profitGoal);
			} else {
				totalSeasonGoal.setGoal(totalSeasonGoal.getGoal() + goal);
				totalSeasonGoal.setProfitGoal(totalSeasonGoal.getProfitGoal() + profitGoal);
			}
		}
		return map;
	}
	
	//[月份 [产品线[国家   目标]]] 英语国家在上报目标时已经合并为EN
	public Map<String,Map<String,Map<String, EnterpriseGoal>>> findLineGoalWithTotalAndEn(EnterpriseGoal enterpriseGoal){
		String sql="SELECT g.month,g.`country`,IFNULL(d.name,'UnGrouped'),g.goal,g.`profit_goal` "+
				" FROM amazoninfo_enterprise_goal g LEFT JOIN psi_product_type_dict d ON g.product_line=d.id  "+
				" WHERE MONTH>=:p1 AND MONTH<=:p2 and country !='total' ORDER BY g.month";
		Map<String,Map<String,Map<String, EnterpriseGoal>>>  map=Maps.newLinkedHashMap();
		List<Object[]> list=enterpriseGoalDao.findBySql(sql, new Parameter(new SimpleDateFormat("yyyyMM").format(enterpriseGoal.getStartMonth()),new SimpleDateFormat("yyyyMM").format(enterpriseGoal.getEndMonth())));
		for (Object[] obj : list) {
			String month = obj[0].toString();
			String country = obj[1].toString();
			String line = obj[2].toString();
			if (line != null && line.contains(" ")) {
				line = line.split(" ")[0];
			}
			String obj3 = obj[3] == null?"0":obj[3].toString();
			Float goal = obj[3] == null?0:Float.parseFloat(obj3);
			Float profitGoal = obj[4] == null?0:Float.parseFloat(obj[4].toString());
			Map<String, Map<String, EnterpriseGoal>> temp = map.get(month);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(month, temp);
			}
			Map<String, EnterpriseGoal> lineMap = temp.get(line);
			if (lineMap == null) {
				lineMap = Maps.newLinkedHashMap();
				temp.put(line, lineMap);
			}
			if ("com,ca,uk".contains(country)) {
				country = "en";
			}
			EnterpriseGoal eGoal = lineMap.get(country);
			if (eGoal == null) {
				eGoal = new EnterpriseGoal();
				eGoal.setGoal(goal);
				eGoal.setProfitGoal(profitGoal);
				lineMap.put(country, eGoal);
			} else {
				eGoal.setGoal(eGoal.getGoal() + goal);
				eGoal.setProfitGoal(eGoal.getProfitGoal() + profitGoal);
			}
			
			EnterpriseGoal total = lineMap.get("total");
			if (total == null) {
				total = new EnterpriseGoal();
				lineMap.put("total", total);
				total.setGoal(goal);
				total.setProfitGoal(profitGoal);
			} else {
				total.setGoal(total.getGoal() + goal);
				total.setProfitGoal(total.getProfitGoal() + profitGoal);
			}
			//分季节统计
			String q = "q1";	//一季度
			if (month.endsWith("04") || month.endsWith("05") || month.endsWith("06")) {	//二季度
				q = "q2";
			} else if (month.endsWith("07") || month.endsWith("08") || month.endsWith("09")) {	//三季度
				q = "q3";
			} else if (month.endsWith("10") || month.endsWith("11") || month.endsWith("12")) {	//四季度
				q = "q4";
			}
			Map<String,Map<String, EnterpriseGoal>> seasonTemp = map.get(q);
			if(seasonTemp==null){
				seasonTemp=Maps.newLinkedHashMap();
				map.put(q,seasonTemp);
			}
			Map<String, EnterpriseGoal> seasonLineMap = seasonTemp.get(line);
			if (seasonLineMap == null) {
				seasonLineMap = Maps.newLinkedHashMap();
				seasonTemp.put(line, seasonLineMap);
			}
			EnterpriseGoal seasonGoal = seasonLineMap.get(country);
			if (seasonGoal == null) {
				seasonGoal = new EnterpriseGoal();
				seasonLineMap.put(country, seasonGoal);
				seasonGoal.setGoal(goal);
				seasonGoal.setProfitGoal(profitGoal);
			} else {
				seasonGoal.setGoal(seasonGoal.getGoal() + goal);
				seasonGoal.setProfitGoal(seasonGoal.getProfitGoal() + profitGoal);
			}
			EnterpriseGoal seasonTotal = seasonLineMap.get("total");
			if (seasonTotal == null) {
				seasonTotal = new EnterpriseGoal();
				seasonLineMap.put("total", seasonTotal);
				seasonTotal.setGoal(goal);
				seasonTotal.setProfitGoal(profitGoal);
			} else {
				seasonTotal.setGoal(seasonTotal.getGoal() + goal);
				seasonTotal.setProfitGoal(seasonTotal.getProfitGoal() + profitGoal);
			}
		}
		return map;
	}
	
	//[月份 [产品线[国家   目标]]] 英语国家部分数据上报目标时已经合并为EN
	public Map<String,Map<String,Map<String, EnterpriseGoal>>> findLineGoalWithEnAndNonEn(EnterpriseGoal enterpriseGoal){
		String sql="SELECT g.month,g.`country`,IFNULL(d.name,'UnGrouped'),g.goal,g.`profit_goal` "+
				" FROM amazoninfo_enterprise_goal g LEFT JOIN psi_product_type_dict d ON g.product_line=d.id  "+
				" WHERE MONTH>=:p1 AND MONTH<=:p2 and country !='total' ORDER BY g.month,d.name";
		Map<String,Map<String,Map<String, EnterpriseGoal>>>  map=Maps.newLinkedHashMap();
		List<Object[]> list=enterpriseGoalDao.findBySql(sql, new Parameter(new SimpleDateFormat("yyyyMM").format(enterpriseGoal.getStartMonth()),new SimpleDateFormat("yyyyMM").format(enterpriseGoal.getEndMonth())));
		for (Object[] obj : list) {
			String month = obj[0].toString();
			String country = obj[1].toString();
			String line = obj[2].toString();
			if (line != null && line.contains(" ")) {
				line = line.split(" ")[0];
			}
			String obj3 = obj[3] == null?"0":obj[3].toString();
			Float goal = obj[3] == null?0:Float.parseFloat(obj3);
			Float profitGoal = obj[4] == null?0:Float.parseFloat(obj[4].toString());
			Map<String, Map<String, EnterpriseGoal>> temp = map.get(month);
			if(temp==null){
				temp=Maps.newTreeMap();
				map.put(month, temp);
			}
			Map<String, EnterpriseGoal> lineMap = temp.get(line);
			if (lineMap == null) {
				lineMap = Maps.newLinkedHashMap();
				temp.put(line, lineMap);
			}
			
			EnterpriseGoal countryGoal = lineMap.get(country);
			if (countryGoal == null) {
				countryGoal = new EnterpriseGoal();
				countryGoal.setGoal(goal);
				countryGoal.setProfitGoal(profitGoal);
				lineMap.put(country, countryGoal);
			} else {
				countryGoal.setGoal(countryGoal.getGoal() + goal);
				countryGoal.setProfitGoal(countryGoal.getProfitGoal() + profitGoal);
			}
			String tempCountry = "";
			if ("com,ca,uk,en".contains(country)) {
				tempCountry = "en";
			} else {
				tempCountry = "nonEn";
			}
			EnterpriseGoal eGoal = lineMap.get(tempCountry);
			if (eGoal == null) {
				eGoal = new EnterpriseGoal();
				eGoal.setGoal(goal);
				eGoal.setProfitGoal(profitGoal);
				lineMap.put(tempCountry, eGoal);
			} else {
				eGoal.setGoal(eGoal.getGoal() + goal);
				eGoal.setProfitGoal(eGoal.getProfitGoal() + profitGoal);
			}

			EnterpriseGoal total = lineMap.get("total");
			if (total == null) {
				total = new EnterpriseGoal();
				lineMap.put("total", total);
				total.setGoal(goal);
				total.setProfitGoal(profitGoal);
			} else {
				total.setGoal(total.getGoal() + goal);
				total.setProfitGoal(total.getProfitGoal() + profitGoal);
			}
			//每条线汇总
			Map<String, EnterpriseGoal> totalLineMap = temp.get("total");
			if (totalLineMap == null) {
				totalLineMap = Maps.newLinkedHashMap();
				temp.put("total", totalLineMap);
			}
			EnterpriseGoal totalLineGoal = totalLineMap.get(country);
			if (totalLineGoal == null) {
				totalLineGoal = new EnterpriseGoal();
				totalLineGoal.setGoal(goal);
				totalLineGoal.setProfitGoal(profitGoal);
				totalLineMap.put(country, totalLineGoal);
			} else {
				totalLineGoal.setGoal(totalLineGoal.getGoal() + goal);
				totalLineGoal.setProfitGoal(totalLineGoal.getProfitGoal() + profitGoal);
			}
			EnterpriseGoal enOrNonEnLineGoal = totalLineMap.get(tempCountry);
			if (enOrNonEnLineGoal == null) {
				enOrNonEnLineGoal = new EnterpriseGoal();
				enOrNonEnLineGoal.setGoal(goal);
				enOrNonEnLineGoal.setProfitGoal(profitGoal);
				totalLineMap.put(tempCountry, enOrNonEnLineGoal);
			} else {
				enOrNonEnLineGoal.setGoal(enOrNonEnLineGoal.getGoal() + goal);
				enOrNonEnLineGoal.setProfitGoal(enOrNonEnLineGoal.getProfitGoal() + profitGoal);
			}

			EnterpriseGoal totalGoal = totalLineMap.get("total");
			if (totalGoal == null) {
				totalGoal = new EnterpriseGoal();
				totalLineMap.put("total", totalGoal);
				totalGoal.setGoal(goal);
				totalGoal.setProfitGoal(profitGoal);
			} else {
				totalGoal.setGoal(totalGoal.getGoal() + goal);
				totalGoal.setProfitGoal(totalGoal.getProfitGoal() + profitGoal);
			}
		}
		return map;
	}
	
	//[产品线[国家   目标]] 英语国家部分数据上报目标时已经合并为EN
	public Map<String,Map<String, EnterpriseGoal>> findCountryLineGoal(String month){
		String sql="SELECT g.month,g.`country`,IFNULL(d.name,'UnGrouped'),g.goal,g.`profit_goal` "+
				" FROM amazoninfo_enterprise_goal g LEFT JOIN psi_product_type_dict d ON g.product_line=d.id  "+
				" WHERE MONTH=:p1 and country !='total' ORDER BY g.month,d.name";
		Map<String,Map<String, EnterpriseGoal>>  map=Maps.newLinkedHashMap();
		List<Object[]> list=enterpriseGoalDao.findBySql(sql, new Parameter(month));
		for (Object[] obj : list) {
			String country = obj[1].toString();
			String line = obj[2].toString();
			if (line != null && line.contains(" ")) {
				line = line.split(" ")[0];
			}
			Float goal = obj[3] == null?0:Float.parseFloat(obj[3].toString());
			Float profitGoal = obj[4] == null?0:Float.parseFloat(obj[4].toString());
			Map<String, EnterpriseGoal> temp = map.get(line);
			if(temp==null){
				temp=Maps.newTreeMap();
				map.put(line, temp);
			}
			
			EnterpriseGoal countryGoal = temp.get(country);
			if (countryGoal == null) {
				countryGoal = new EnterpriseGoal();
				countryGoal.setGoal(goal);
				countryGoal.setProfitGoal(profitGoal);
				temp.put(country, countryGoal);
			} else {
				countryGoal.setGoal(countryGoal.getGoal() + goal);
				countryGoal.setProfitGoal(countryGoal.getProfitGoal() + profitGoal);
			}
		}
		return map;
	}
	
	
	public Map<String,Float> findTotalGoalByMonth(String month, String currencyType){
		String sql="SELECT country,goal FROM amazoninfo_enterprise_total_goal WHERE MONTH=:p1 and country !='total'";
		List<Object[]> list=enterpriseGoalDao.findBySql(sql,new Parameter(month));
		Map<String,Float> map=new HashMap<String,Float>();
		float total=0f;
		float euTotal=0f;
		for (Object[] obj : list) {
			total+=((BigDecimal)obj[1]).floatValue();
			map.put(obj[0].toString(),((BigDecimal)obj[1]).floatValue());
			if ("de,fr,it,es,uk".contains(obj[0].toString())) {
				euTotal+=((BigDecimal)obj[1]).floatValue();
			}
		}
		map.put("totalAvg", new BigDecimal(total).setScale(2,4).floatValue());
		map.put("euAvg", new BigDecimal(euTotal).setScale(2,4).floatValue());
		return map;
	}
	
	
	public Map<String,Map<String,Object[]>> findMonthGoal(EnterpriseGoal enterpriseGoal){
		String sql="select g.id,g.month,ifnull(g.product_line,'total') line,IFNULL(d.name,'UnGrouped'),g.goal from amazoninfo_enterprise_goal g left join psi_product_type_dict d on g.product_line=d.id where month>=:p1 and month<=:p2 and g.country=:p3 order by g.month";
		Map<String,Map<String,Object[]>> map=Maps.newHashMap();
		List<Object[]> list=enterpriseGoalDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(enterpriseGoal.getStartMonth()),new SimpleDateFormat("yyyyMM").format(enterpriseGoal.getEndMonth()),enterpriseGoal.getCountry()));
		for (Object[] obj : list) {
			Map<String,Object[]> temp=map.get(obj[1]);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[1].toString(),temp);
			}
			temp.put(obj[2].toString(),obj);
		}
		return map;
	}
	
	public Map<String,Map<String,Float>> findMonthLineGoal(EnterpriseGoal enterpriseGoal){
		String str = "";
		if ("notEn".equals(enterpriseGoal.getCountry()) || "nonEn".equals(enterpriseGoal.getCountry())) {	//小语种国家统计(非英语国家)
			str = " AND g.`country` IN('de','fr','it','es','jp')";
		} else if ("en".equals(enterpriseGoal.getCountry())) {
			str = " AND g.`country` IN('en','com','uk','ca')";
		} else if ("notUs".equals(enterpriseGoal.getCountry()) || "noUs".equals(enterpriseGoal.getCountry())) {
			str = " AND g.`country` IN('de','fr','it','es','jp','uk','mx','ca')";
		} else if ("eu".equals(enterpriseGoal.getCountry())) {
			str = " AND g.`country` IN('de','fr','it','es','uk')";
		} else if (StringUtils.isNotEmpty(enterpriseGoal.getCountry())) {
			str = " AND g.`country` IN('"+enterpriseGoal.getCountry()+"')";
		}
		String sql="SELECT g.month,IFNULL(g.product_line,'total') line,SUM(g.goal) FROM amazoninfo_enterprise_goal g WHERE MONTH>=:p1 AND MONTH<=:p2 "+str+" GROUP BY g.month,IFNULL(g.product_line,'total') ORDER BY g.month";
		Map<String,Map<String,Float>> map=Maps.newHashMap();
		List<Object[]> list=enterpriseGoalDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(enterpriseGoal.getStartMonth()),new SimpleDateFormat("yyyyMM").format(enterpriseGoal.getEndMonth())));
		for (Object[] obj : list) {
			Map<String,Float> temp=map.get(obj[0]);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(),temp);
			}
			temp.put(obj[1].toString(),((BigDecimal)obj[2]).floatValue());
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void save(List<EnterpriseGoal> enterpriseGoals) {
		enterpriseGoalDao.save(enterpriseGoals);
	}
	
	@Transactional(readOnly = false)
	public void saveTotalGoals(List<EnterpriseTotalGoal> enterpriseTotalGoals) {
		enterpriseTotalGoalDao.save(enterpriseTotalGoals);
	}
	
	@Transactional(readOnly = false)
	public void updateGoal(EnterpriseGoal enterpriseGoal) {
	   String sql="update amazoninfo_enterprise_goal set goal=:p1 where id=:p2";
	   enterpriseGoalDao.updateBySql(sql,new Parameter(enterpriseGoal.getGoal(),enterpriseGoal.getId()));
	}
	
	public String findByCountryMonth(EnterpriseGoal enterpriseGoal){
		String sql="select 1 from amazoninfo_enterprise_goal where month=:p1 ";
		List<Object> list=enterpriseGoalDao.findBySql(sql,new Parameter(enterpriseGoal.getMonth()));
		if(list.size()>0){
			return "1";
		}else{
			return "0";
		}
	}
	
	public Map<String,Float> findGoalByMonth(String month, String currencyType){
		String sql="SELECT country,sum(goal) FROM amazoninfo_enterprise_goal WHERE MONTH=:p1  GROUP BY country";
		List<Object[]> list=enterpriseGoalDao.findBySql(sql,new Parameter(month));
		Map<String,Float> map=new HashMap<String,Float>();
		float total=0f;
		BigDecimal unEnAmount = BigDecimal.ZERO;
		BigDecimal enAmount=BigDecimal.ZERO;
		for (Object[] obj : list) {
			total+=((BigDecimal)obj[1]).floatValue();
			String country = obj[0].toString();
			BigDecimal amount =(BigDecimal)obj[1];
			map.put(country,(amount).floatValue());
			if("de,fr,es,it,jp,".contains(country+",")){//算出非英语国家的统计
				unEnAmount=unEnAmount.add(amount);
			}
			if("uk,com,ca,".contains(country+",")){//算出英语国家的统计
				enAmount=enAmount.add(amount);
			}
		}
		map.put("totalAvg", new BigDecimal(total).setScale(2,4).floatValue());
		map.put("unEn", unEnAmount.setScale(2,4).floatValue());
		map.put("en", enAmount.setScale(2,4).floatValue());
		return map;
	}
	
	//产品线-国家-目标
	public Map<String,Map<String,Float>> getGoalByMonth(String month, String currencyType){
		Map<String,Map<String,Float>> map=Maps.newHashMap();
		String sql="SELECT country,goal,product_line FROM amazoninfo_enterprise_goal WHERE MONTH=:p1 and product_line is not null ";
		List<Object[]> list=enterpriseGoalDao.findBySql(sql,new Parameter(month));
		for (Object[] obj : list) {
			Map<String,Float> temp=map.get(obj[2].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[2].toString(),temp);
			}
			String country = obj[0].toString();
			temp.put(country,((BigDecimal)obj[1]).floatValue());
			
			if("de,fr,it,es,jp,".contains(country+",")){//添加非英语平台
				String tempCountry="unEn";
				Float unEnAmount = ((BigDecimal)obj[1]).floatValue();
				if(temp.get(tempCountry)!=null){
					unEnAmount+=temp.get(tempCountry);
				}
				temp.put(tempCountry,unEnAmount);
				map.put(obj[2].toString(), temp);
			}
			
			if("ca,com,uk,".contains(country+",")){//添加英语平台
				String tempCountry="en";
				Float enAmount = ((BigDecimal)obj[1]).floatValue();
				if(temp.get(tempCountry)!=null){
					enAmount+=temp.get(tempCountry);
				}
				temp.put(tempCountry,enAmount);
				map.put(obj[2].toString(), temp);
			}
		}
		
		
		if(map!=null&&map.size()>0){
			for (Map.Entry<String, Map<String, Float>> entry : map.entrySet()) {  
				Map<String,Float> temp=entry.getValue();
				Float goal=0f;
				for (Map.Entry<String, Float> entryRs : temp.entrySet()) {  
				    String country=entryRs.getKey();
					if(!"unEn".equals(country)&&!"en".equals(country)){
						goal+=entryRs.getValue();
					}
				}
				temp.put("total", goal);
			}
			List<Dict> dictAll=DictUtils.getDictList("platform");
			Float totalGoal=0f;
	      	for (Dict dict : dictAll) {
	  			if(!"com.unitek".equals(dict.getValue())){
	  				Float goal=0f;
	  				for (Map.Entry<String, Map<String, Float>> entry : map.entrySet()) {  
	  					Map<String,Float> temp=entry.getValue();
	  					goal+=(temp.get(dict.getValue())==null?0:temp.get(dict.getValue()));
	  					totalGoal+=(temp.get(dict.getValue())==null?0:temp.get(dict.getValue()));
	  				}
	  				Map<String,Float> temp=map.get("total");
	  				if(temp==null){
	  					temp=Maps.newHashMap();
	  					map.put("total",temp);
	  				}
	  				temp.put(dict.getValue(), goal);
	  			}
	  		}
	      	Map<String,Float> temp=map.get("total");
				if(temp==null){
					temp=Maps.newHashMap();
					map.put("total",temp);
				}
				temp.put("total", totalGoal);
		}
		
		return map;
	}
	
	/**
	 * 获取当前月份的月目标填报时间的前一天字符串（yyyyMMdd）
	 * @return
	 */
	public String findByCurrentMonth(){
		String sql="SELECT DATE_FORMAT(a.`create_date`, '%Y%m%d') dates FROM amazoninfo_enterprise_goal a WHERE a.`month`=:p1 ORDER BY a.`create_date` DESC";
		List<Object> list=enterpriseGoalDao.findBySql(sql,new Parameter(DateUtils.getDate("yyyyMM")));
		if(list.size()>0){
			Object obj = list.get(0);
			if (obj == null) {
				return null;
			}
			String goalDateStr = obj.toString();
			DateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
			try {
				Date goalDate = formatDay.parse(goalDateStr);
				return formatDay.format(DateUtils.addDays(goalDate, -1));
			} catch (ParseException e) {
				return null;
			}
		}else{
			return null;
		}
	}
	

	/**
	 * 获取当前月份的月目标填报时间的前一天字符串（yyyyMMdd）
	 * @return
	 */
	public String findTotalByCurrentMonth(){
		String sql="SELECT DATE_FORMAT(a.`create_date`, '%Y%m%d') dates FROM amazoninfo_enterprise_total_goal a WHERE a.`month`=:p1 and country !='total' ORDER BY a.`create_date` DESC";
		List<Object> list=enterpriseGoalDao.findBySql(sql,new Parameter(DateUtils.getDate("yyyyMM")));
		if(list.size()>0){
			Object obj = list.get(0);
			if (obj == null) {
				return null;
			}
			String goalDateStr = obj.toString();
			DateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
			try {
				Date goalDate = formatDay.parse(goalDateStr);
				return formatDay.format(DateUtils.addDays(goalDate, -1));
			} catch (ParseException e) {
				return null;
			}
		}else{
			return null;
		}
	}
	
	public EnterpriseTotalGoal findByCountryAndMonth(String month, String country){
		DetachedCriteria dc = enterpriseTotalGoalDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(month)) {
			dc.add(Restrictions.eq("month", month));
		}
		if (StringUtils.isNotEmpty(country)) {
			dc.add(Restrictions.eq("country", country));
		}
		List<EnterpriseTotalGoal> list = enterpriseTotalGoalDao.find(dc);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public EnterpriseGoal findByCountryAndMonthAndLine(String month, String country, String id){
		DetachedCriteria dc = enterpriseGoalDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(month)) {
			dc.add(Restrictions.eq("month", month));
		}
		if (StringUtils.isNotEmpty(country)) {
			dc.add(Restrictions.eq("country", country));
		}
		dc.createAlias("productLine", "productLine");
		if (StringUtils.isNotEmpty(id)) {
			dc.add(Restrictions.eq("productLine.id", id));
		}
		List<EnterpriseGoal> list = enterpriseGoalDao.find(dc);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		PsiProductTypeGroupDictService dictService = applicationContext.getBean(PsiProductTypeGroupDictService.class);
		EnterpriseGoalService service = applicationContext.getBean(EnterpriseGoalService.class);
		SaleReportMonthTypeService typeService = applicationContext.getBean(SaleReportMonthTypeService.class);
		service.setDictService(dictService);
		String month = "201605";
		Map<String,Map<String, Float>> monthSales = typeService.getLineSalesByMonth(month);
		service.updateDynamicGoal(monthSales, month);
		applicationContext.close();
	}
	
	/**
	 * 获取系统计算出的月度总目标,该目标作为以后修改目标时的参考值,月度实际目标不能低于该值
	 * @return
	 */
	public Float findTargetGoalByMonth(String month){
		String sql="SELECT goal FROM `amazoninfo_enterprise_total_goal` t WHERE t.`month`=:p1 AND t.`country`='total'";
		List<Object> list = enterpriseGoalDao.findBySql(sql,new Parameter(month));
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
	 * 获取当前月度排除某一国家后的总目标
	 * @return
	 */
	public Float getTotalGoalByMonth(String month, String country){
		String sql="SELECT SUM(goal) FROM `amazoninfo_enterprise_total_goal` t WHERE t.`month`=:p1 AND t.`country`!='total' AND t.`country` !=:p2";
		List<Object> list = enterpriseGoalDao.findBySql(sql,new Parameter(month, country));
		if(list.size()>0){
			Object obj = list.get(0);
			if (obj == null) {
				return null;
			}
			return Float.parseFloat(obj.toString());
		}else{
			return null;
		}
	}
	
	/**
	 * 获取当前月度总目标
	 * @return
	 */
	public Float getSysTotalGoalByMonth(String month){
		String sql="SELECT SUM(goal) FROM `amazoninfo_enterprise_total_goal` t WHERE t.`month`=:p1 AND t.`country`!='total'";
		List<Object> list = enterpriseGoalDao.findBySql(sql,new Parameter(month));
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
	 * 获取当前月度排除某一国家某条产品线后的总目标
	 * @return
	 */
	public Float getTotalLineGoalByMonth(String month, String country, String lineId){
		String sql="SELECT SUM(t.goal) FROM `amazoninfo_enterprise_goal` t WHERE t.`month`=:p1 "+
				" AND t.`id`!=(SELECT g.id FROM amazoninfo_enterprise_goal g WHERE g.`month`=:p1 AND g.`country`=:p2 AND g.`product_line`=:p3)";
		List<Object> list = enterpriseGoalDao.findBySql(sql,new Parameter(month, country, lineId));
		if(list.size()>0){
			Object obj = list.get(0);
			if (obj == null) {
				return null;
			}
			return Float.parseFloat(obj.toString());
		}else{
			return null;
		}
	}
	
	/**
	 * 获取当前月度某一国家排除某条产品线后的总目标
	 * @return
	 */
	public Float getMonthTotalLineGoal(String month, String country, String lineId){
		String sql="SELECT SUM(t.goal) FROM `amazoninfo_enterprise_goal` t WHERE t.`month`=:p1 AND t.`country`=:p2 "+
				" AND t.`id`!=(SELECT g.id FROM amazoninfo_enterprise_goal g WHERE g.`month`=:p1 AND g.`country`=:p2 AND g.`product_line`=:p3)";
		List<Object> list = enterpriseGoalDao.findBySql(sql,new Parameter(month, country, lineId));
		if(list.size()>0){
			Object obj = list.get(0);
			if (obj == null) {
				return null;
			}
			return Float.parseFloat(obj.toString());
		}else{
			return 0f;
		}
	}
	
	@Transactional(readOnly = false)
	public void updateAllGoal(String month, Integer monthTotal) {
		Date now = new Date();
		//产品线简称对应的产品线
		Map<String, PsiProductTypeGroupDict> typeGroupMap = Maps.newHashMap();
		List<PsiProductTypeGroupDict> list = dictService.getAllList();
		for (PsiProductTypeGroupDict psiProductTypeGroupDict : list) {
			typeGroupMap.put(psiProductTypeGroupDict.getName().substring(0,1), psiProductTypeGroupDict);
		}
		//目标
		List<EnterpriseTotalGoal> totalGoals = Lists.newArrayList();
		List<EnterpriseGoal> lineGoals = Lists.newArrayList();
		//国家比率
		Map<String, Float> countryRatio = countryRatio();
		//国家产品线比率
		Map<String, Map<String, Float>> countryLineRatio = countryLineRatio();
		
		List<String> countryList = Lists.newArrayList("de","fr","it","es","jp","com","ca","uk");
		List<String> lineList = Lists.newArrayList("A","B","C","D");
		for (String country : countryList) {
			//国家目标
			float countryGoal = 0;
			if ("com,ca,uk".contains(country)) {
				countryGoal = monthTotal * countryRatio.get("en") * countryRatio.get(country);
			} else {
				countryGoal = monthTotal * countryRatio.get(country);
			}
			EnterpriseTotalGoal totalGoal = findByCountryAndMonth(month, country);
			if (totalGoal == null) {
				totalGoal = new EnterpriseTotalGoal();
				totalGoal.setCountry(country);
				totalGoal.setCreateDate(now);
				totalGoal.setMonth(month);
				totalGoal.setGoal(countryGoal);
			} else {
				totalGoal.setGoal(countryGoal);
				totalGoal.setCreateDate(now);
			}
			totalGoals.add(totalGoal);
			for (String line : lineList) {
				String key = country;
				if ("ca,uk".contains(country)) {	//英语国家产品线视为同一平台
					continue;
				} else if ("com".equals(country)) {	//英语国家
					key = "en";
				}
				float totalMonthGoal = monthTotal * countryRatio.get(key);
				PsiProductTypeGroupDict dict = typeGroupMap.get(line);
				if (dict == null) {
					continue;
				}
				float lineGoal = totalMonthGoal * countryLineRatio.get(key).get(line);
				EnterpriseGoal enterpriseGoal = findByCountryAndMonthAndLine(month, key, dict.getId());
				if (enterpriseGoal == null) {
					enterpriseGoal = new EnterpriseGoal();
					enterpriseGoal.setCountry(key);
					enterpriseGoal.setMonth(month);
					enterpriseGoal.setGoal(lineGoal);
					enterpriseGoal.setCreateDate(now);
					enterpriseGoal.setProductLine(dict);
				} else {
					enterpriseGoal.setGoal(lineGoal);
					enterpriseGoal.setCreateDate(now);
				}
				lineGoals.add(enterpriseGoal);
			}
		}
		if (totalGoals.size() > 0) {
			saveTotalGoals(totalGoals);
		}
		if (lineGoals.size() > 0) {
			save(lineGoals);
		}
	}
	
	//获取系统计算的总目标阀值
	public Map<String,Float> findSysTotalGoal(){
		String sql = " SELECT t.`month`,t.`goal` FROM amazoninfo_enterprise_total_goal t WHERE t.`country`='total' AND t.`month` IS NOT NULL AND t.`goal` IS NOT NULL";
		List<Object[]> list = enterpriseGoalDao.findBySql(sql);
		Map<String, Float> map = new HashMap<String, Float>();
		for (Object[] obj : list) {
			map.put(obj[0].toString(), Float.parseFloat(obj[1].toString()));
		}
		return map;
	}

	/**
	 * 根据销售额计算动态目标,整个平台销售目标实现的话不往后分摊,平台目标未实现的分产品线往后分摊
	 * @param monthSales	月产品线销量
	 * @param month
	 */
	@Transactional(readOnly = false)
	public void updateDynamicGoal(Map<String,Map<String,Float>> monthSales, String month) {
		int monthNum = Integer.parseInt(month.substring(4));
		String year = month.substring(0, 4);
		Map<String,Map<String,Float>> monthGoal = getLineGoalByMonth(month);
		//2017各国家产品线利润率
		Map<String, Map<String, Float>> countryProfitRatio = countryLineProfitRatio();
		//产品线简称对应的产品线ID
		Map<String, String> allGroup = dictService.getProductLine();
		Map<String, String> allLine = Maps.newHashMap();
		for (Map.Entry<String,String> entry : allGroup.entrySet()) {  
		    String key =entry.getKey();
			String value = entry.getValue();
			allLine.put(value.substring(0, 1), key);
		}
		int length = 3;	//默认分摊到后三个月
		if (12 - monthNum < 3) {	//不足三个月
			length = 12 - monthNum;
		}
		for (Map.Entry<String, Map<String, Float>> entryMonth : monthGoal.entrySet()) {  
		    String country = entryMonth.getKey();
		    Map<String, Float> lineGoals=entryMonth.getValue();
			if ("total".equals(country)) {
				continue;
			}
			//如果这个国家的目标完成了,就不往后分摊
			if ("en".equals(country)) {
				float enSales = 0;
				try {
					enSales += monthSales.get("com").get("total");
				} catch (NullPointerException e) {}
				try {
					enSales += monthSales.get("ca").get("total");
				} catch (NullPointerException e) {}
				try {
					enSales += monthSales.get("uk").get("total");
				} catch (NullPointerException e) {}
				if (enSales > monthGoal.get(country).get("total")) {
					continue;
				}
			} else {
				if (monthSales.get(country).get("total") > lineGoals.get("total")) {
					continue;
				}
			}
			
			for (Map.Entry<String,Float> entry : lineGoals.entrySet()) {  
			    String lineName =entry.getKey();
				String lineId = allLine.get(lineName);
				if (StringUtils.isEmpty(lineId)) {
					continue;
				}
				float lineSales = 0f;
				if ("en".equals(country)) {
					try {
						lineSales += monthSales.get("com").get(lineName);
					} catch (NullPointerException e) {}
					try {
						lineSales += monthSales.get("ca").get(lineName);
					} catch (NullPointerException e) {}
					try {
						lineSales += monthSales.get("uk").get(lineName);
					} catch (NullPointerException e) {}
				} else {
					try {
						lineSales = monthSales.get(country).get(lineName);
					} catch (NullPointerException e) {}
				}
				float lineGoal = entry.getValue();
				if (lineGoal <= lineSales) {
					continue;	//目标完成了不需要往后分摊
				}
				float sales = (lineGoal - lineSales)/length;	//分摊到的目标

				float profitRatio = 0;
				if ("com,ca,uk".contains(country)) {
					profitRatio = countryProfitRatio.get("en").get(lineName);
				} else {
					profitRatio = countryProfitRatio.get("nonEn").get(lineName);
				}
				for (int i = 1; i <= length; i++) {
					int goalMonthNum = monthNum + i;
					String goalMonth = year+ (goalMonthNum < 10 ? "0" + goalMonthNum : goalMonthNum);
					Float totalGoal = getMonthTotalLineGoal(goalMonth, country, lineId); // 国家总目标,不计算当前产品线
					if (totalGoal == null) {
						continue;
					}
					EnterpriseGoal enterpriseGoal = findByCountryAndMonthAndLine(goalMonth, country, lineId);
					if (enterpriseGoal == null) {
						enterpriseGoal = new EnterpriseGoal();
						enterpriseGoal.setCountry(country);
						enterpriseGoal.setMonth(month);
						enterpriseGoal.setCreateDate(new Date());
						enterpriseGoal.setGoal((float)MathUtils.roundUp((double)sales));	//取整
						enterpriseGoal.setProfitGoal(enterpriseGoal.getGoal()*profitRatio);
					} else {
						enterpriseGoal.setCreateDate(new Date());
						enterpriseGoal.setGoal(enterpriseGoal.getGoal() + MathUtils.roundUp((double)sales));
						enterpriseGoal.setProfitGoal(enterpriseGoal.getGoal()*profitRatio);
					}
					save(enterpriseGoal);
					
					totalGoal += enterpriseGoal.getGoal();	//国家新的总目标
					if ("en".equals(country)) {
						//更新三个国家
						Map<String, Float> rate = countryRatio();
						List<String> list = Lists.newArrayList("com","uk","ca");
						for (String key : list) {
							float goal = MathUtils.roundUp((double)totalGoal * rate.get(key));
							EnterpriseTotalGoal countryGoal = findByCountryAndMonth(goalMonth, key);
							if(countryGoal == null){
								countryGoal = new EnterpriseTotalGoal();
								countryGoal.setCountry(key);
								countryGoal.setMonth(goalMonth);
								countryGoal.setCreateDate(new Date());
								countryGoal.setGoal(goal);
							} else {
								countryGoal.setCreateDate(new Date());
								countryGoal.setGoal(goal);
							}
							save(countryGoal);
						}
					} else {
						EnterpriseTotalGoal countryGoal = findByCountryAndMonth(goalMonth, country);
						if(countryGoal == null){
							countryGoal = new EnterpriseTotalGoal();
							countryGoal.setCountry(country);
							countryGoal.setMonth(goalMonth);
							countryGoal.setCreateDate(new Date());
							countryGoal.setGoal(totalGoal);
						} else {
							countryGoal.setCreateDate(new Date());
							countryGoal.setGoal(totalGoal);
						}
						save(countryGoal);
					}
				}
			}
		}
		for (int i = 1; i <= length; i++) {
			int goalMonthNum = monthNum + i;
			String goalMonth = year+ (goalMonthNum < 10 ? "0" + goalMonthNum : goalMonthNum);
			//更新系统动态计算后的总目标
			Float sysTotalGoal = getSysTotalGoalByMonth(goalMonth);
			if (sysTotalGoal != null) {
				EnterpriseTotalGoal countryTotalGoal = findByCountryAndMonth(goalMonth, "total");
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
				save(countryTotalGoal);
			}
		}
	}

	/**
	 * 获取指定月份的分产品线销售目标
	 * @param month
	 * @return[国家[产品线  目标]]
	 */
	public Map<String,Map<String,Float>> getLineGoalByMonth(String month){
		String sql ="SELECT country,product_line,goal FROM amazoninfo_enterprise_goal WHERE MONTH=:p1 AND product_line IS NOT NULL AND goal IS NOT NULL";
		List<Object[]> list = enterpriseGoalDao.findBySql(sql, new Parameter(month));
		//把产品线ID转换成简称,方便对接
		Map<String, String> allLine = dictService.getProductLine();
		for (Map.Entry<String,String> entry : allLine.entrySet()) {  
		    String key =entry.getKey();
			String value = entry.getValue();
			allLine.put(key, value.substring(0, 1));
		}
		Map<String,Map<String, Float>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString();
			String line = objs[1].toString();
			Float goal = objs[2]==null?0:Float.parseFloat(objs[2].toString());

			Map<String, Float> countryTemp=rs.get(country);
			if (countryTemp == null) {
				countryTemp = Maps.newHashMap();
				rs.put(country, countryTemp);
			}
			countryTemp.put(allLine.get(line), goal);
			//if (!"E".equals(allLine.get(line))) {
				Float totalGoal = countryTemp.get("total"); //总计,不计算E线
				if (totalGoal == null) {
					countryTemp.put("total", goal);
				} else {
					countryTemp.put("total", totalGoal + goal);
				}
				
			//}
		}
		return rs;
	}
	
	/**
	 * 更新国家目标,按比例更新下属产品线目标
	 * @param enterpriseTotalGoal
	 */
	@Transactional(readOnly = false)
	public void updateCountryGoal(EnterpriseTotalGoal enterpriseTotalGoal) {
		//产品线简称对应的产品线
		Map<String, PsiProductTypeGroupDict> typeGroupMap = Maps.newHashMap();
		List<PsiProductTypeGroupDict> list = dictService.getAllList();
		for (PsiProductTypeGroupDict psiProductTypeGroupDict : list) {
			typeGroupMap.put(psiProductTypeGroupDict.getName().substring(0,1), psiProductTypeGroupDict);
		}
		//目标
		List<EnterpriseGoal> lineGoals = Lists.newArrayList();
		//国家产品线比率
		Map<String, Map<String, Float>> countryLineRatio = countryLineRatio();
		
		List<String> lineList = Lists.newArrayList("A","B","C","D");
		String country = enterpriseTotalGoal.getCountry();
		float totalMonthGoal = enterpriseTotalGoal.getGoal();
		String month = enterpriseTotalGoal.getMonth();

		String key = country;
		if ("ca,uk,com".contains(country)) {	//英语国家产品线视为同一平台
			key = "en";
			//分配产品线目标时需要加上其他两个英语国家的目标
			if ("ca".equals(country)) {
				EnterpriseTotalGoal usGoal = findByCountryAndMonth(month, "com");
				if (usGoal != null) {
					totalMonthGoal += usGoal.getGoal();
				}
				EnterpriseTotalGoal ukGoal = findByCountryAndMonth(month, "uk");
				if (ukGoal != null) {
					totalMonthGoal += ukGoal.getGoal();
				}
			} else if ("uk".equals(country)) {
				EnterpriseTotalGoal usGoal = findByCountryAndMonth(month, "com");
				if (usGoal != null) {
					totalMonthGoal += usGoal.getGoal();
				}
				EnterpriseTotalGoal ukGoal = findByCountryAndMonth(month, "ca");
				if (ukGoal != null) {
					totalMonthGoal += ukGoal.getGoal();
				}
			} else {
				EnterpriseTotalGoal caGoal = findByCountryAndMonth(month, "ca");
				if (caGoal != null) {
					totalMonthGoal += caGoal.getGoal();
				}
				EnterpriseTotalGoal ukGoal = findByCountryAndMonth(month, "uk");
				if (ukGoal != null) {
					totalMonthGoal += ukGoal.getGoal();
				}
			}
		}
		Date now = new Date();
		User user = UserUtils.getUser();
		for (String line : lineList) {
			PsiProductTypeGroupDict dict = typeGroupMap.get(line);
			if (dict == null) {
				continue;
			}
			float lineGoal = totalMonthGoal * countryLineRatio.get(key).get(line);
			EnterpriseGoal enterpriseGoal = findByCountryAndMonthAndLine(month, key, dict.getId());
			if (enterpriseGoal == null) {
				enterpriseGoal = new EnterpriseGoal();
				enterpriseGoal.setCountry(key);
				enterpriseGoal.setMonth(month);
				enterpriseGoal.setGoal(lineGoal);
				enterpriseGoal.setCreateDate(now);
				enterpriseGoal.setProductLine(dict);
				enterpriseGoal.setCreateUser(user);
			} else {
				enterpriseGoal.setGoal(lineGoal);
				enterpriseGoal.setCreateDate(now);
				enterpriseGoal.setCreateUser(user);
			}
			lineGoals.add(enterpriseGoal);
		}
		save(enterpriseTotalGoal);
		if (lineGoals.size() > 0) {
			save(lineGoals);
		}
	}

	/**
	 * 根据产品线更新后的目标修改整个国家的总目标
	 * @param enterpriseGoal
	 */
	@Transactional(readOnly = false)
	public void updateCountryGoalByLine(EnterpriseGoal enterpriseGoal) {
		String month = enterpriseGoal.getMonth();
		String country = enterpriseGoal.getCountry();
		float totalGoal = getMonthTotalLineGoal(month,country,enterpriseGoal.getProductLine().getId());	//新的国家总目标
		totalGoal += enterpriseGoal.getGoal();
		User user = UserUtils.getUser();
		if ("en".equals(country)) {
			//更新三个国家
			Map<String, Float> rate = countryRatio();
			List<String> list = Lists.newArrayList("com","uk","ca");
			for (String key : list) {
				float goal = MathUtils.roundUp((double)totalGoal * rate.get(key));
				EnterpriseTotalGoal countryGoal = findByCountryAndMonth(month, key);
				if(countryGoal == null){
					countryGoal = new EnterpriseTotalGoal();
					countryGoal.setCountry(key);
					countryGoal.setMonth(month);
					countryGoal.setCreateDate(new Date());
					countryGoal.setCreateUser(user);
					countryGoal.setGoal(goal);
				} else {
					countryGoal.setCreateDate(new Date());
					countryGoal.setCreateUser(user);
					countryGoal.setGoal(goal);
				}
				save(countryGoal);
			}
		} else {
			EnterpriseTotalGoal countryGoal = findByCountryAndMonth(month, country);
			if(countryGoal == null){
				countryGoal = new EnterpriseTotalGoal();
				countryGoal.setCountry(country);
				countryGoal.setMonth(month);
				countryGoal.setCreateDate(new Date());
				countryGoal.setCreateUser(user);
				countryGoal.setGoal(totalGoal);
			} else {
				countryGoal.setCreateDate(new Date());
				countryGoal.setCreateUser(user);
				countryGoal.setGoal(totalGoal);
			}
			save(countryGoal);
		}
	}

	/**
	 * 根据产品线更新后的目标修改整个国家的总目标
	 * @param enterpriseGoal
	 */
	@Transactional(readOnly = false)
	public void updateCountryTotalGoal(EnterpriseGoal enterpriseGoal) {
		String month = enterpriseGoal.getMonth();
		String country = enterpriseGoal.getCountry();
		String sql = "SELECT SUM(t.`goal`),SUM(t.`profit_goal`) FROM `amazoninfo_enterprise_goal` t WHERE t.`month`=:p1 AND t.`country`=:p2  ";
		List<Object[]> rsList = enterpriseGoalDao.findBySql(sql, new Parameter(month, country));
		for (Object[] obj : rsList) {
			float salesGoal = obj[0]==null?0f:Float.parseFloat(obj[0].toString());
			float profitsGoal = obj[1]==null?0f:Float.parseFloat(obj[1].toString());
			EnterpriseTotalGoal enterpriseTotalGoal = findByCountryAndMonth(month, country);
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
			save(enterpriseTotalGoal);
		}
	}
	
	/**
	 * 自动分配各平台及各产品线(A/B/C/D)目标
	 * 
	 */
	@Transactional(readOnly = false)
	public void autoGoal(Date date) {
		DateFormat format = new SimpleDateFormat("yyyyMM");
		Date firstDayOfMonth = DateUtils.getFirstDayOfMonth(date);
		String month = format.format(date);
		Map<String, Integer> monthGoal = monthGoalMap(month.substring(0, 4));
		Integer monthTotal = monthGoal.get(month);
		if (monthTotal != null) {
			//产品线简称对应的产品线
			Map<String, PsiProductTypeGroupDict> typeGroupMap = Maps.newHashMap();
			List<PsiProductTypeGroupDict> list = dictService.getAllList();
			for (PsiProductTypeGroupDict psiProductTypeGroupDict : list) {
				typeGroupMap.put(psiProductTypeGroupDict.getName().substring(0,1), psiProductTypeGroupDict);
			}
			//目标
			List<EnterpriseTotalGoal> totalGoals = Lists.newArrayList();
			List<EnterpriseGoal> lineGoals = Lists.newArrayList();
			//国家比率
			Map<String, Float> countryRatio = countryRatio();
			//国家产品线比率
			Map<String, Map<String, Float>> countryLineRatio = countryLineRatio();
			
			List<String> countryList = Lists.newArrayList("de","fr","it","es","jp","com","ca","uk");
			List<String> lineList = Lists.newArrayList("A","B","C","D");
			for (String country : countryList) {
				//国家目标
				float countryGoal = 0;
				if ("com,ca,uk".contains(country)) {
					countryGoal = monthTotal * countryRatio.get("en") * countryRatio.get(country);
				} else {
					countryGoal = monthTotal * countryRatio.get(country);
				}
				EnterpriseTotalGoal totalGoal = findByCountryAndMonth(month, country);
				if (totalGoal == null) {
					totalGoal = new EnterpriseTotalGoal();
					totalGoal.setCountry(country);
					totalGoal.setCreateDate(firstDayOfMonth);
					totalGoal.setMonth(month);
					totalGoal.setGoal(countryGoal);
				} else {
					totalGoal.setGoal(countryGoal);
					totalGoal.setCreateDate(firstDayOfMonth);
				}
				totalGoals.add(totalGoal);
				for (String line : lineList) {
					String key = country;
					if ("ca,uk".contains(country)) {	//英语国家产品线视为同一平台
						continue;
					} else if ("com".equals(country)) {	//英语国家
						key = "en";
					}
					float totalMonthGoal = monthTotal * countryRatio.get(key);
					PsiProductTypeGroupDict dict = typeGroupMap.get(line);
					if (dict == null) {
						continue;
					}
					float lineGoal = totalMonthGoal * countryLineRatio.get(key).get(line);
					EnterpriseGoal enterpriseGoal = findByCountryAndMonthAndLine(month, key, dict.getId());
					if (enterpriseGoal == null) {
						enterpriseGoal = new EnterpriseGoal();
						enterpriseGoal.setCountry(key);
						enterpriseGoal.setMonth(month);
						enterpriseGoal.setGoal(lineGoal);
						enterpriseGoal.setCreateDate(firstDayOfMonth);
						enterpriseGoal.setProductLine(dict);
					} else {
						enterpriseGoal.setGoal(lineGoal);
						enterpriseGoal.setCreateDate(firstDayOfMonth);
					}
					lineGoals.add(enterpriseGoal);
				}
			}
			if (totalGoals.size() > 0) {
				saveTotalGoals(totalGoals);
			}
			if (lineGoals.size() > 0) {
				save(lineGoals);
			}
		}
	}

	/**
	 * 获取指定月份的分产品线销售目标,不区分平台
	 * @param month
	 * @return[产品线  目标]
	 */
	public Map<String, EnterpriseGoal> getAllLineGoalByMonth(String month){
		String sql ="SELECT t.`product_line`,SUM(IFNULL(t.`goal`,0)),SUM(IFNULL(t.`profit_goal`,0)) FROM amazoninfo_enterprise_goal t "+
				" WHERE t.`month`=:p1 AND product_line IS NOT NULL AND goal IS NOT NULL GROUP BY t.`product_line`";
		List<Object[]> list = enterpriseGoalDao.findBySql(sql, new Parameter(month));
		//把产品线ID转换成简称,方便对接
		Map<String, String> allLine = dictService.getProductLine();
		for (Map.Entry<String,String> entry : allLine.entrySet()) {  
		    String key =entry.getKey();
			String value = entry.getValue();
			allLine.put(key, value.substring(0, 1));
		}
		Map<String, EnterpriseGoal> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String line = objs[0].toString();
			line = allLine.get(line);
			Float goal = objs[1] == null ? 0 : Float.parseFloat(objs[1].toString());
			Float profitGoal = objs[2] == null ? 0 : Float.parseFloat(objs[2].toString());

			EnterpriseGoal enterpriseGoal = new EnterpriseGoal();
			enterpriseGoal.setGoal(goal);
			enterpriseGoal.setProfitGoal(profitGoal);
			rs.put(line, enterpriseGoal);
			
			//if (!"E".equals(line)) {
				EnterpriseGoal totalGoal = rs.get("total");
				if (totalGoal == null) {
					totalGoal = new EnterpriseGoal();
					rs.put("total", totalGoal);
					totalGoal.setGoal(goal);
					totalGoal.setProfitGoal(profitGoal);
				} else {
					totalGoal.setGoal(totalGoal.getGoal() + goal);
					totalGoal.setProfitGoal(totalGoal.getProfitGoal() + profitGoal);
				}
			//}
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void updateLineProfitGoal(String month, String lineId, String country, float profitGoal) {
	   String sql="UPDATE `amazoninfo_enterprise_goal` t SET t.`profit_goal`=:p1 WHERE t.`month`=:p2 AND t.`product_line`=:p3 AND t.`country`=:p4";
	   enterpriseGoalDao.updateBySql(sql,new Parameter(profitGoal, month, lineId, country));
	}
	
	//2016年度各月总目标
	public static Map<String, Integer> monthGoalMap(String year) {
		Map<String, Integer> rs = Maps.newHashMap();
		if ("2016".equals(year)) {
			rs.put("201601", 2660000);
			rs.put("201602", 2230000);
			rs.put("201603", 2550000);
			rs.put("201604", 2420000);
			rs.put("201605", 2230000);
			rs.put("201606", 2460000);
			rs.put("201607", 2930000);
			rs.put("201608", 3100000);
			rs.put("201609", 3170000);
			rs.put("201610", 3240000);
			rs.put("201611", 3940000);
			rs.put("201612", 5190000);
			rs.put("q1", 7440000);
			rs.put("q2", 7110000);
			rs.put("q3", 9200000);
			rs.put("q4", 12370000);
		} else if ("2017".equals(year)) {
			//2017年度各月总目标
			rs.put("201701", 2970750);
			rs.put("201702", 2470750);
			rs.put("201703", 2568750);
			rs.put("201704", 2840595);
			rs.put("201705", 3112076);
			rs.put("201706", 3599902);
			rs.put("201707", 4859809);
			rs.put("201708", 5083587);
			rs.put("201709", 5824482);
			rs.put("201710", 7043035);
			rs.put("201711", 9662924);
			rs.put("201712", 12673466);
			rs.put("q1", 8010250);
			rs.put("q2", 9552573);
			rs.put("q3", 15767878);
			rs.put("q4", 29379425);
		}
		return rs;
	}
	
	//2016年度各国家占比,其中英语国家占比为EN总目标下的比率
	public static Map<String, Float> countryRatio() {
		Map<String, Float> rs = Maps.newHashMap();
		rs.put("de", 0.27f);
		rs.put("fr", 0.1f);
		rs.put("it", 0.05f);
		rs.put("es", 0.02f);
		rs.put("jp", 0.08f);
		rs.put("en", 0.48f);
		rs.put("com", 0.73f);
		rs.put("ca", 0.05f);
		rs.put("uk", 0.22f);
		return rs;
	}
	
	//2016年度各国家产品线占比,英语国家统一 [国家[产品线  比率]]
	public static Map<String, Map<String, Float>> countryLineRatio() {
		Map<String, Map<String, Float>> rs = Maps.newHashMap();
		//英语国家
		Map<String, Float> nonEnMap = Maps.newHashMap();
		rs.put("en", nonEnMap);
		nonEnMap.put("A", 0.385f);
		nonEnMap.put("B", 0.36f);
		nonEnMap.put("C", 0.055f);
		nonEnMap.put("D", 0.2f);
		//德国
		Map<String, Float> deMap = Maps.newHashMap();
		rs.put("de", deMap);
		deMap.put("A", 0.4f);
		deMap.put("B", 0.35f);
		deMap.put("C", 0.1f);
		deMap.put("D", 0.15f);
		//法国
		Map<String, Float> frMap = Maps.newHashMap();
		rs.put("fr", frMap);
		frMap.put("A", 0.38f);
		frMap.put("B", 0.39f);
		frMap.put("C", 0.06f);
		frMap.put("D", 0.17f);
		//意大利
		Map<String, Float> itMap = Maps.newHashMap();
		rs.put("it", itMap);
		itMap.put("A", 0.37f);
		itMap.put("B", 0.27f);
		itMap.put("C", 0.07f);
		itMap.put("D", 0.29f);
		//西班牙
		Map<String, Float> esMap = Maps.newHashMap();
		rs.put("es", esMap);
		esMap.put("A", 0.33f);
		esMap.put("B", 0.44f);
		esMap.put("C", 0.06f);
		esMap.put("D", 0.17f);
		//日本
		Map<String, Float> jpMap = Maps.newHashMap();
		rs.put("jp", jpMap);
		jpMap.put("A", 0.305f);
		jpMap.put("B", 0.49f);
		jpMap.put("C", 0.13f);
		jpMap.put("D", 0.075f);
		
		return rs;
	}

	public PsiProductTypeGroupDictService getDictService() {
		return dictService;
	}

	public void setDictService(PsiProductTypeGroupDictService dictService) {
		this.dictService = dictService;
	}
	
	/**
	 * 2017自动分配各平台及各产品线(A/B/C/D)目标
	 * @throws ParseException 
	 */
	@Transactional(readOnly = false)
	public void autoGoal2017() throws ParseException {
		//产品线简称对应的产品线
		Map<String, PsiProductTypeGroupDict> typeGroupMap = Maps.newHashMap();
		List<PsiProductTypeGroupDict> typeGroupList = dictService.getAllList();
		for (PsiProductTypeGroupDict psiProductTypeGroupDict : typeGroupList) {
			typeGroupMap.put(psiProductTypeGroupDict.getName().substring(0,1), psiProductTypeGroupDict);
		}
		Map<String, Map<String, Float>> totalLineGoal = totalLineGoal2017();
		Date today = new SimpleDateFormat("yyyyMMdd").parse("20170101");	//一月
		Date firstDayOfMonth = DateUtils.getFirstDayOfMonth(today);
		List<String> list = Lists.newArrayList("A","B","C","D","E","F");
		List<String> countryList = Lists.newArrayList("de","fr","it","es","jp","com","ca","uk");
		//2017各国家比率
		Map<String, Map<String, Float>> countryRatio = countryLineRatio2017();
		//2017各国家产品线利润率
		Map<String, Map<String, Float>> countryProfitRatio = countryLineProfitRatio();
		List<EnterpriseGoal> lineGoals = Lists.newArrayList();
		for (int i = 0; i < 12; i++) {
			Date date = DateUtils.addMonths(today, i);
			for (String line : list) {
				DateFormat format = new SimpleDateFormat("yyyyMM");
				String month = format.format(date);
				float enGoal = totalLineGoal.get("en").get(line+month);
				float nonEnGoal = totalLineGoal.get("nonEn").get(line+month);
				//英语国家分配到com/uk/ca
				for (String country : countryList) {
					//国家目标
					float countryLineGoal = 0;
					float profitRatio = 0;
					if ("com,ca,uk".contains(country)) {
						countryLineGoal = enGoal * countryRatio.get("en").get(country);
						profitRatio = countryProfitRatio.get("en").get(line);
					} else {
						countryLineGoal = nonEnGoal * countryRatio.get("nonEn").get(country);
						profitRatio = countryProfitRatio.get("nonEn").get(line);
					}
					PsiProductTypeGroupDict dict = typeGroupMap.get(line);
					if (dict == null) {
						continue;
					}
					EnterpriseGoal enterpriseGoal = findByCountryAndMonthAndLine(month, country, dict.getId());
					if (enterpriseGoal == null) {
						enterpriseGoal = new EnterpriseGoal();
						enterpriseGoal.setCountry(country);
						enterpriseGoal.setMonth(month);
						enterpriseGoal.setGoal(countryLineGoal);
						enterpriseGoal.setCreateDate(firstDayOfMonth);
						enterpriseGoal.setProductLine(dict);
						enterpriseGoal.setProfitGoal(countryLineGoal*profitRatio);
					} else {
						enterpriseGoal.setGoal(countryLineGoal);
						enterpriseGoal.setCreateDate(firstDayOfMonth);
						enterpriseGoal.setProfitGoal(countryLineGoal*profitRatio);
					}
					lineGoals.add(enterpriseGoal);
				}
			}
		}
		if (lineGoals.size() > 0) {
			save(lineGoals);
		}
		for (int i = 0; i < 12; i++) {
			Date date2 = DateUtils.addMonths(today, i);
			DateFormat format = new SimpleDateFormat("yyyyMM");
			String month = format.format(date2);
			for (String country : countryList) {
				String sql = "SELECT SUM(t.`goal`),SUM(t.`profit_goal`) FROM `amazoninfo_enterprise_goal` t WHERE t.`month`=:p1 AND t.`country`=:p2  ";
				List<Object[]> rsList = enterpriseGoalDao.findBySql(sql, new Parameter(month, country));
				for (Object[] obj : rsList) {
					float salesGoal = obj[0]==null?0f:Float.parseFloat(obj[0].toString());
					float profitsGoal = obj[1]==null?0f:Float.parseFloat(obj[1].toString());
					EnterpriseTotalGoal enterpriseTotalGoal = findByCountryAndMonth(month, country);
					if (enterpriseTotalGoal == null) {
						enterpriseTotalGoal = new EnterpriseTotalGoal();
						enterpriseTotalGoal.setMonth(month);
						enterpriseTotalGoal.setCountry(country);
						enterpriseTotalGoal.setGoal(salesGoal);
						enterpriseTotalGoal.setProfitGoal(profitsGoal);
						enterpriseTotalGoal.setCreateDate(firstDayOfMonth);
					} else {
						enterpriseTotalGoal.setGoal(salesGoal);
						enterpriseTotalGoal.setProfitGoal(profitsGoal);
						enterpriseTotalGoal.setCreateDate(firstDayOfMonth);
					}
					save(enterpriseTotalGoal);
				}
			}
		}
	}
	
	
	//2017年度各月度目标国家比例
	public static Map<String, Map<String, Float>> countryLineRatio2017() {
		Map<String, Map<String, Float>> rs = Maps.newHashMap();
		//英语国家
		Map<String, Float> enMap = Maps.newHashMap();
		rs.put("en", enMap);
		enMap.put("com", 0.73f);
		enMap.put("ca", 0.05f);
		enMap.put("uk", 0.22f);
		//非英语国家
		Map<String, Float> nonEnMap = Maps.newHashMap();
		rs.put("nonEn", nonEnMap);
		nonEnMap.put("de", 0.52f);
		nonEnMap.put("fr", 0.19f);
		nonEnMap.put("it", 0.10f);
		nonEnMap.put("es", 0.04f);
		nonEnMap.put("jp", 0.15f);
		return rs;
	}
	
	//2017年度各月度产品线目标利润率
	public static Map<String, Map<String, Float>> countryLineProfitRatio() {
		Map<String, Map<String, Float>> rs = Maps.newHashMap();
		//英语国家
		Map<String, Float> enMap = Maps.newHashMap();
		rs.put("en", enMap);
		enMap.put("A", 0.243f);
		enMap.put("B", 0.3092f);
		enMap.put("C", 0.1685f);
		enMap.put("D", 0.3278f);
		enMap.put("E", 0.225f);
		enMap.put("F", 0.1456f);
		//非英语国家
		Map<String, Float> nonEnMap = Maps.newHashMap();
		rs.put("nonEn", nonEnMap);
		nonEnMap.put("A", 0.1847f);
		nonEnMap.put("B", 0.2602f);
		nonEnMap.put("C", 0.1596f);
		nonEnMap.put("D", 0.2544f);
		nonEnMap.put("E", 0.215f);
		nonEnMap.put("F", 0.2458f);
		return rs;
	}
	
	//2017年度各月度产品线目标
	public static Map<String, Map<String, Float>> totalLineGoal2017() {
		Map<String, Map<String, Float>> rs = Maps.newHashMap();
		//英语国家
		Map<String, Float> enMap = Maps.newHashMap();
		rs.put("en", enMap);
		//key:线月份
		enMap.put("A201701", 360000f);
		enMap.put("A201702", 291000f);
		enMap.put("A201703", 295000f);
		enMap.put("A201704", 300000f);
		enMap.put("A201705", 310000f);
		enMap.put("A201706", 325000f);
		enMap.put("A201707", 390000f);
		enMap.put("A201708", 410000f);
		enMap.put("A201709", 430000f);
		enMap.put("A201710", 450000f);
		enMap.put("A201711", 550000f);
		enMap.put("A201712", 640000f);
		
		enMap.put("B201701", 480000f);
		enMap.put("B201702", 310000f);
		enMap.put("B201703", 330000f);
		enMap.put("B201704", 335000f);
		enMap.put("B201705", 355000f);
		enMap.put("B201706", 380000f);
		enMap.put("B201707", 420000f);
		enMap.put("B201708", 600000f);
		enMap.put("B201709", 560000f);
		enMap.put("B201710", 620000f);
		enMap.put("B201711", 890000f);
		enMap.put("B201712", 1050000f);
		
		enMap.put("C201701", 120000f);
		enMap.put("C201702", 90000f);
		enMap.put("C201703", 110000f);
		enMap.put("C201704", 130000f);
		enMap.put("C201705", 160000f);
		enMap.put("C201706", 210000f);
		enMap.put("C201707", 350000f);
		enMap.put("C201708", 320000f);
		enMap.put("C201709", 380000f);
		enMap.put("C201710", 500000f);
		enMap.put("C201711", 950000f);
		enMap.put("C201712", 1280000f);
		
		enMap.put("D201701", 280000f);
		enMap.put("D201702", 270000f);
		enMap.put("D201703", 280000f);
		enMap.put("D201704", 330000f);
		enMap.put("D201705", 350000f);
		enMap.put("D201706", 410000f);
		enMap.put("D201707", 450000f);
		enMap.put("D201708", 500000f);
		enMap.put("D201709", 520000f);
		enMap.put("D201710", 580000f);
		enMap.put("D201711", 717000f);
		enMap.put("D201712", 772500f);
		
		enMap.put("E201701", 90000f);
		enMap.put("E201702", 106000f);
		enMap.put("E201703", 120000f);
		enMap.put("E201704", 130000f);
		enMap.put("E201705", 150000f);
		enMap.put("E201706", 190000f);
		enMap.put("E201707", 320000f);
		enMap.put("E201708", 400000f);
		enMap.put("E201709", 520000f);
		enMap.put("E201710", 810000f);
		enMap.put("E201711", 1300000f);
		enMap.put("E201712", 2150000f);
		
		enMap.put("F201701", 170000f);
		enMap.put("F201702", 150000f);
		enMap.put("F201703", 170000f);
		enMap.put("F201704", 190000f);
		enMap.put("F201705", 210000f);
		enMap.put("F201706", 250000f);
		enMap.put("F201707", 340000f);
		enMap.put("F201708", 340000f);
		enMap.put("F201709", 400000f);
		enMap.put("F201710", 480000f);
		enMap.put("F201711", 580000f);
		enMap.put("F201712", 690000f);
		
		//非英语国家国
		Map<String, Float> nonEnMap = Maps.newHashMap();
		rs.put("nonEn", nonEnMap);
		//key:线月份
		nonEnMap.put("A201701", 450750f);
		nonEnMap.put("A201702", 377750f);
		nonEnMap.put("A201703", 347750f);
		nonEnMap.put("A201704", 393595f);
		nonEnMap.put("A201705", 405076f);
		nonEnMap.put("A201706", 438902f);
		nonEnMap.put("A201707", 508809f);
		nonEnMap.put("A201708", 517587f);
		nonEnMap.put("A201709", 558482f);
		nonEnMap.put("A201710", 607035f);
		nonEnMap.put("A201711", 688923f);
		nonEnMap.put("A201712", 825466f);
		
		nonEnMap.put("B201701", 500000f);
		nonEnMap.put("B201702", 360000f);
		nonEnMap.put("B201703", 350000f);
		nonEnMap.put("B201704", 355000f);
		nonEnMap.put("B201705", 360000f);
		nonEnMap.put("B201706", 385000f);
		nonEnMap.put("B201707", 520000f);
		nonEnMap.put("B201708", 510000f);
		nonEnMap.put("B201709", 610000f);
		nonEnMap.put("B201710", 660000f);
		nonEnMap.put("B201711", 840000f);
		nonEnMap.put("B201712", 1010000f);
		
		nonEnMap.put("C201701", 60000f);
		nonEnMap.put("C201702", 70000f);
		nonEnMap.put("C201703", 80000f);
		nonEnMap.put("C201704", 100000f);
		nonEnMap.put("C201705", 130000f);
		nonEnMap.put("C201706", 180000f);
		nonEnMap.put("C201707", 350000f);
		nonEnMap.put("C201708", 330000f);
		nonEnMap.put("C201709", 500000f);
		nonEnMap.put("C201710", 680000f);
		nonEnMap.put("C201711", 920000f);
		nonEnMap.put("C201712", 1300000f);
		
		nonEnMap.put("D201701", 240000f);
		nonEnMap.put("D201702", 246000f);
		nonEnMap.put("D201703", 276000f);
		nonEnMap.put("D201704", 328000f);
		nonEnMap.put("D201705", 367000f);
		nonEnMap.put("D201706", 406000f);
		nonEnMap.put("D201707", 546000f);
		nonEnMap.put("D201708", 506000f);
		nonEnMap.put("D201709", 506000f);
		nonEnMap.put("D201710", 546000f);
		nonEnMap.put("D201711", 677000f);
		nonEnMap.put("D201712", 785500f);
		
		nonEnMap.put("E201701", 110000f);
		nonEnMap.put("E201702", 100000f);
		nonEnMap.put("E201703", 110000f);
		nonEnMap.put("E201704", 145000f);
		nonEnMap.put("E201705", 200000f);
		nonEnMap.put("E201706", 290000f);
		nonEnMap.put("E201707", 490000f);
		nonEnMap.put("E201708", 460000f);
		nonEnMap.put("E201709", 620000f);
		nonEnMap.put("E201710", 840000f);
		nonEnMap.put("E201711", 1200000f);
		nonEnMap.put("E201712", 1710000f);
		
		nonEnMap.put("F201701", 110000f);
		nonEnMap.put("F201702", 100000f);
		nonEnMap.put("F201703", 100000f);
		nonEnMap.put("F201704", 104000f);
		nonEnMap.put("F201705", 115000f);
		nonEnMap.put("F201706", 135000f);
		nonEnMap.put("F201707", 175000f);
		nonEnMap.put("F201708", 190000f);
		nonEnMap.put("F201709", 220000f);
		nonEnMap.put("F201710", 270000f);
		nonEnMap.put("F201711", 350000f);
		nonEnMap.put("F201712", 460000f);
		return rs;
	}
	
	/**
	 * 2017下半年分配各平台及各产品线(A/B/C/D/E/F)目标
	 * @throws ParseException 
	 */
	@Transactional(readOnly = false)
	public void autoGoal2017Half() throws ParseException {
		//产品线简称对应的产品线
		Map<String, PsiProductTypeGroupDict> typeGroupMap = Maps.newHashMap();
		List<PsiProductTypeGroupDict> typeGroupList = dictService.getAllList();
		for (PsiProductTypeGroupDict psiProductTypeGroupDict : typeGroupList) {
			typeGroupMap.put(psiProductTypeGroupDict.getName().substring(0,1), psiProductTypeGroupDict);
		}
		Date today = new SimpleDateFormat("yyyyMMdd").parse("20170701");	//一月
		Date firstDayOfMonth = DateUtils.getFirstDayOfMonth(today);
		List<String> list = Lists.newArrayList("A","B","C","D","E","F");
		List<String> countryList = Lists.newArrayList("de","fr","it","es","jp","com","ca","uk");
		//2017各产品线平台销售额占比
		Map<String, Map<String, Float>> countrySalesRatio = Maps.newHashMap();
		Map<String, Float> aLine = Maps.newHashMap();
		countrySalesRatio.put("A", aLine);
		aLine.put("com", 0.3363f);
		aLine.put("ca", 0.0165f);
		aLine.put("uk", 0.1097f);
		aLine.put("de", 0.2601f);
		aLine.put("fr", 0.1153f);
		aLine.put("it", 0.0546f);
		aLine.put("es", 0.0197f);
		aLine.put("jp", 0.0878f);
		Map<String, Float> bLine = Maps.newHashMap();
		countrySalesRatio.put("B", bLine);
		bLine.put("com", 0.2708f);
		bLine.put("ca", 0.0218f);
		bLine.put("uk", 0.1742f);
		bLine.put("de", 0.2688f);
		bLine.put("fr", 0.0871f);
		bLine.put("it", 0.0340f);
		bLine.put("es", 0.0295f);
		bLine.put("jp", 0.1138f);
		Map<String, Float> cLine = Maps.newHashMap();
		countrySalesRatio.put("C", cLine);
		cLine.put("com", 0.4527f);
		cLine.put("ca", 0.0027f);
		cLine.put("uk", 0.1748f);
		cLine.put("de", 0.2432f);
		cLine.put("fr", 0.0395f);
		cLine.put("it", 0.0426f);
		cLine.put("es", 0.0087f);
		cLine.put("jp", 0.0358f);
		Map<String, Float> dLine = Maps.newHashMap();
		countrySalesRatio.put("D", dLine);
		dLine.put("com", 0.4584f);
		dLine.put("ca", 0.0203f);
		dLine.put("uk", 0.0751f);
		dLine.put("de", 0.2256f);
		dLine.put("fr", 0.0507f);
		dLine.put("it", 0.0565f);
		dLine.put("es", 0.0107f);
		dLine.put("jp", 0.1027f);
		Map<String, Float> eLine = Maps.newHashMap();
		countrySalesRatio.put("E", eLine);
		eLine.put("com", 0.3831f);
		eLine.put("ca", 0f);
		eLine.put("uk", 0.1339f);
		eLine.put("de", 0.2520f);
		eLine.put("fr", 0.0746f);
		eLine.put("it", 0.0733f);
		eLine.put("es", 0.0244f);
		eLine.put("jp", 0.0587f);
		Map<String, Float> fLine = Maps.newHashMap();
		countrySalesRatio.put("F", fLine);
		fLine.put("com", 0.5227f);
		fLine.put("ca", 0.0268f);
		fLine.put("uk", 0.0917f);
		fLine.put("de", 0.2167f);
		fLine.put("fr", 0.0377f);
		fLine.put("it", 0.0216f);
		fLine.put("es", 0.0116f);
		fLine.put("jp", 0.0712f);
		//2017各国家各产品线利润率
		Map<String, Map<String, Float>> countryProfitRatio = Maps.newHashMap();
		Map<String, Float> enMap = Maps.newHashMap();
		countryProfitRatio.put("en", enMap);
		enMap.put("A", 0.2430f);
		enMap.put("B", 0.3092f);
		enMap.put("C", 0.1685f);
		enMap.put("D", 0.3278f);
		enMap.put("E", 0.225f);
		enMap.put("F", 0.185f);
		Map<String, Float> nonEnMap = Maps.newHashMap();
		countryProfitRatio.put("nonEn", nonEnMap);
		nonEnMap.put("A", 0.1847f);
		nonEnMap.put("B", 0.2602f);
		nonEnMap.put("C", 0.1596f);
		nonEnMap.put("D", 0.2544f);
		nonEnMap.put("E", 0.215f);
		nonEnMap.put("F", 0.18f);

		Map<String, Map<String, Float>> goalMap = Maps.newLinkedHashMap();
		Map<String, Float> month07 = Maps.newHashMap();
		goalMap.put("201707", month07);
		month07.put("A", 720122f);
		month07.put("B", 900000f);
		month07.put("C", 150000f);
		month07.put("D", 500000f);
		month07.put("E", 350000f);
		month07.put("F", 311024f);
		Map<String, Float> month08 = Maps.newHashMap();
		goalMap.put("201708", month08);
		month08.put("A", 784223f);
		month08.put("B", 950000f);
		month08.put("C", 140000f);
		month08.put("D", 550000f);
		month08.put("E", 364000f);
		month08.put("F", 353335f);
		Map<String, Float> month09 = Maps.newHashMap();
		goalMap.put("201709", month09);
		month09.put("A", 899799f);
		month09.put("B", 1100000f);
		month09.put("C", 150000f);
		month09.put("D", 600000f);
		month09.put("E", 494000f);
		month09.put("F", 380172f);
		Map<String, Float> month10 = Maps.newHashMap();
		goalMap.put("201710", month10);
		month10.put("A", 1027627f);
		month10.put("B", 1000000f);
		month10.put("C", 180000f);
		month10.put("D", 650000f);
		month10.put("E", 525000f);
		month10.put("F", 422207f);
		Map<String, Float> month11 = Maps.newHashMap();
		goalMap.put("201711", month11);
		month11.put("A", 1104779f);
		month11.put("B", 1350000f);
		month11.put("C", 230000f);
		month11.put("D", 900000f);
		month11.put("E", 804000f);
		month11.put("F", 480009f);
		Map<String, Float> month12 = Maps.newHashMap();
		goalMap.put("201712", month12);
		month12.put("A", 1163450f);
		month12.put("B", 1700000f);
		month12.put("C", 450000f);
		month12.put("D", 1100000f);
		month12.put("E", 963000f);
		month12.put("F", 553253f);
		
		Map<String, Float> profitRatio = Maps.newHashMap();
		profitRatio.put("A", 0.2101f);
		profitRatio.put("B", 0.2845f);
		profitRatio.put("C", 0.1640f);
		profitRatio.put("D", 0.2911f);
		profitRatio.put("E", 0.22f);
		profitRatio.put("F", 0.1825f);
		List<EnterpriseGoal> lineGoals = Lists.newArrayList();
		for (Entry<String, Map<String, Float>> entry : goalMap.entrySet()) {
			String month = entry.getKey();
			Map<String, Float> lineGoal = entry.getValue();
			for (String line : list) {
				//当月产品线的总目标,需要拆分到每个国家
				Float monthLineGoal = lineGoal.get(line);
				//Float monthProfitGoal = monthLineGoal * profitRatio.get(line);
				for (String country : countryList) {
					//国家目标
					float countryLineGoal = monthLineGoal * countrySalesRatio.get(line).get(country);
					String key = "nonEn";
					if ("com,ca,uk".contains(country)) {
						key = "en";
					}
					float countryProfitGoal = countryLineGoal * countryProfitRatio.get(key).get(line);
					PsiProductTypeGroupDict dict = typeGroupMap.get(line);
					if (dict == null) {
						continue;
					}
					EnterpriseGoal enterpriseGoal = findByCountryAndMonthAndLine(month, country, dict.getId());
					if (enterpriseGoal == null) {
						enterpriseGoal = new EnterpriseGoal();
						enterpriseGoal.setCountry(country);
						enterpriseGoal.setMonth(month);
						enterpriseGoal.setGoal(countryLineGoal);
						enterpriseGoal.setCreateDate(firstDayOfMonth);
						enterpriseGoal.setProductLine(dict);
						enterpriseGoal.setProfitGoal(countryProfitGoal);
					} else {
						enterpriseGoal.setCreateDate(firstDayOfMonth);
						enterpriseGoal.setGoal(countryLineGoal);
						enterpriseGoal.setCreateDate(firstDayOfMonth);
						enterpriseGoal.setProfitGoal(countryProfitGoal);
					}
					lineGoals.add(enterpriseGoal);
				}
			}
		}
		if (lineGoals.size() > 0) {
			save(lineGoals);
		}
		List<String> monthList = Lists.newArrayList("201707","201708","201709","201710","201711","201712");
		for (String month : monthList) {
			for (String country : countryList) {
				String sql = "SELECT SUM(t.`goal`),SUM(t.`profit_goal`) FROM `amazoninfo_enterprise_goal` t WHERE t.`month`=:p1 AND t.`country`=:p2  ";
				List<Object[]> rsList = enterpriseGoalDao.findBySql(sql, new Parameter(month, country));
				for (Object[] obj : rsList) {
					float salesGoal = obj[0]==null?0f:Float.parseFloat(obj[0].toString());
					float profitsGoal = obj[1]==null?0f:Float.parseFloat(obj[1].toString());
					EnterpriseTotalGoal enterpriseTotalGoal = findByCountryAndMonth(month, country);
					if (enterpriseTotalGoal == null) {
						enterpriseTotalGoal = new EnterpriseTotalGoal();
						enterpriseTotalGoal.setMonth(month);
						enterpriseTotalGoal.setCountry(country);
						enterpriseTotalGoal.setGoal(salesGoal);
						enterpriseTotalGoal.setProfitGoal(profitsGoal);
						enterpriseTotalGoal.setCreateDate(firstDayOfMonth);
					} else {
						enterpriseTotalGoal.setGoal(salesGoal);
						enterpriseTotalGoal.setProfitGoal(profitsGoal);
						enterpriseTotalGoal.setCreateDate(firstDayOfMonth);
					}
					save(enterpriseTotalGoal);
				}
			}
		}
	}

	@Transactional(readOnly = false)
	public void importGoal() {
		try {
			//时间	数量	编号	orderid	退换标志	次品	店铺
			String path = this.getClass().getResource("").getPath();
			if (path.contains(":")) {
				path = path.substring(1, path.lastIndexOf("classes"));
			} else {
				path = path.substring(0, path.lastIndexOf("classes"));
			}
			path = path.replace("%20", " ");
			path = path + "classes/salesgoal.xlsx";
			//File file = new File("E:/salesgoal.xlsx");
			File file = new File(path);
			if (!file.exists()) {
				logger.warn("销售目标文件不存在" + file.getAbsolutePath());
			}
			Map<String, Map<String, Float>> profitRateMap = importProfitRateMap();
			InputStream inputStream = new FileInputStream(file);
			// A/B/C/D/E
			List<String> lineList = Lists.newArrayList("52aa512544ed405c884bd6d266cd5b0b","1e6235091b0b43fb9f310512c6924427","be2ed665694e4fd19dcda7e839bafd03","058a7c4ff7d443628d1bb0e35f1d3f8c","e5fb0544516740beaee4670355597bc1");
			List<String> countryList = Lists.newArrayList("de","fr","es","it","uk","jp","ca","mx");
			
			Workbook wb = new XSSFWorkbook(inputStream);
			Date today = new SimpleDateFormat("yyyyMMdd").parse("20180101");	//一月
			Date firstDayOfMonth = DateUtils.getFirstDayOfMonth(today);
			List<EnterpriseGoal> lineGoals = Lists.newArrayList();
			for (int i = 0; i < lineList.size(); i++) {
				String line = lineList.get(i);
				Sheet sheet = wb.getSheetAt(i);
				for (int row = 1; row < 13; row++) {
					String month = 2018 + (row<10?("0"+row):(row+""));
					for (int cell = 2; cell < 10; cell++) {
						String country = countryList.get(cell-2);
						double goal = sheet.getRow(row).getCell(cell).getNumericCellValue();
						EnterpriseGoal enterpriseGoal = findByCountryAndMonthAndLine(month, country, line);
						if (enterpriseGoal == null) {
							enterpriseGoal = new EnterpriseGoal();
							enterpriseGoal.setCountry(country);
							enterpriseGoal.setMonth(month);
							enterpriseGoal.setGoal((float)goal);
							enterpriseGoal.setProfitGoal((float)goal*profitRateMap.get(month).get(line));
							enterpriseGoal.setCreateDate(firstDayOfMonth);
							PsiProductTypeGroupDict dict = new PsiProductTypeGroupDict();
							dict.setId(line);
							enterpriseGoal.setProductLine(dict);
						} else {
							enterpriseGoal.setGoal((float)goal);
							enterpriseGoal.setProfitGoal((float)goal*profitRateMap.get(month).get(line));
							enterpriseGoal.setCreateDate(firstDayOfMonth);
						}
						lineGoals.add(enterpriseGoal);
					}
				}
			}
			if (lineGoals.size() > 0) {
				save(lineGoals);
			}
			for (int i = 0; i < 12; i++) {
				Date date2 = DateUtils.addMonths(today, i);
				DateFormat format = new SimpleDateFormat("yyyyMM");
				String month = format.format(date2);
				for (String country : countryList) {
					String sql = "SELECT SUM(t.`goal`),SUM(t.`profit_goal`) FROM `amazoninfo_enterprise_goal` t WHERE t.`month`=:p1 AND t.`country`=:p2  ";
					List<Object[]> rsList = enterpriseGoalDao.findBySql(sql, new Parameter(month, country));
					for (Object[] obj : rsList) {
						float salesGoal = obj[0]==null?0f:Float.parseFloat(obj[0].toString());
						float profitsGoal = obj[1]==null?0f:Float.parseFloat(obj[1].toString());
						EnterpriseTotalGoal enterpriseTotalGoal = findByCountryAndMonth(month, country);
						if (enterpriseTotalGoal == null) {
							enterpriseTotalGoal = new EnterpriseTotalGoal();
							enterpriseTotalGoal.setMonth(month);
							enterpriseTotalGoal.setCountry(country);
							enterpriseTotalGoal.setGoal(salesGoal);
							enterpriseTotalGoal.setProfitGoal(profitsGoal);
							enterpriseTotalGoal.setCreateDate(firstDayOfMonth);
						} else {
							enterpriseTotalGoal.setGoal(salesGoal);
							enterpriseTotalGoal.setProfitGoal(profitsGoal);
							enterpriseTotalGoal.setCreateDate(firstDayOfMonth);
						}
						save(enterpriseTotalGoal);
					}
				}
			}
		} catch (Exception e) {
			logger.error("导入目标失败", e);
		}
	}
	
	//Map<月份, Map<产品线, 利润率>>
	public Map<String, Map<String, Float>> importProfitRateMap(){
		Map<String, Map<String, Float>> map = Maps.newHashMap();
		try {
			//时间	数量	编号	orderid	退换标志	次品	店铺
			String path = this.getClass().getResource("").getPath();
			if (path.contains(":")) {
				path = path.substring(1, path.lastIndexOf("classes"));
			} else {
				path = path.substring(0, path.lastIndexOf("classes"));
			}
			path = path.replace("%20", " ");
			path = path + "classes/profitrate.xlsx";
			//File file = new File("E:/profitrate.xlsx");
			File file = new File(path);
			if (!file.exists()) {
				logger.warn("利润率目标文件不存在" + file.getAbsolutePath());
			}
			InputStream inputStream = new FileInputStream(file);
			// A/B/C/D/E
			List<String> lineList = Lists.newArrayList("52aa512544ed405c884bd6d266cd5b0b","1e6235091b0b43fb9f310512c6924427","be2ed665694e4fd19dcda7e839bafd03","058a7c4ff7d443628d1bb0e35f1d3f8c","e5fb0544516740beaee4670355597bc1");
			List<String[]> dataList = ExcelUtil.read(inputStream);
			for (int i = 2; i < dataList.size(); i++) {
				String[] datas = dataList.get(i);
				String month = 2018 + (i-1<10?("0"+(i-1)):((i-1)+""));
				Map<String, Float> monthMap = map.get(month);
				if (monthMap == null) {
					monthMap = Maps.newHashMap();
					map.put(month, monthMap);
				}
				for (int j = 0; j < lineList.size(); j++) {
					String line = lineList.get(j);
					String rateStr = datas[j+1];
					float rate = 0f;
					if (StringUtils.isNotBlank(rateStr)) {
						rate = Float.parseFloat(rateStr);
					}
					monthMap.put(line, rate);
				}
			}
		} catch (Exception e) {
			logger.error("导入目标失败", e);
		}
		return map;
	}
	
}
