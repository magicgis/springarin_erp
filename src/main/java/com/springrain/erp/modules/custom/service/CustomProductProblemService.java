package com.springrain.erp.modules.custom.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
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
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.modules.custom.dao.CustomProductProblemDao;
import com.springrain.erp.modules.custom.dao.CustomProductTypeProblemsDao;
import com.springrain.erp.modules.custom.entity.CustomProductProblem;
import com.springrain.erp.modules.custom.entity.CustomProductTypeProblems;
import com.springrain.erp.modules.custom.entity.CustomSuggestion;
import com.springrain.erp.modules.psi.entity.ProductTypeUtils;

@Component
@Transactional(readOnly = true)
public class CustomProductProblemService extends BaseService{
	
	@Autowired
	private CustomProductProblemDao customProductProblemDao;

	@Autowired
	private CustomProductTypeProblemsDao customProductTypeProblemsDao;
	
	
	public Map<String,Map<String,String>>  getProblems(CustomProductProblem problem,Map<String,Integer> refMap){
		Map<String,Map<String,String>> tempMap = Maps.newHashMap();
		String sql ="";
		Parameter para = null;
		if(StringUtils.isNotEmpty(problem.getCountry())){
			sql="SELECT a.`product_name`,a.`problem_type`,COUNT(*) FROM custom_product_problem AS a WHERE a.`data_date` BETWEEN :p2   AND :p3 AND a.`country`=:p1 GROUP BY a.`product_name`,a.`problem_type`";
			para=new Parameter(problem.getCountry(),problem.getDataDate(),problem.getCreateDate());
		}else{
			sql ="SELECT a.`product_name`,a.`problem_type`,COUNT(*) FROM custom_product_problem AS a where a.`data_date` BETWEEN :p1  AND :p2  GROUP BY a.`product_name`,a.`problem_type`";
			para=new Parameter(problem.getDataDate(),problem.getCreateDate());
		}
		
		List<Object[]> problems=this.customProductProblemDao.findBySql(sql,para);
		if(problems!=null&&problems.size()>0){
			for(Object[] obj:problems){
				String productName = obj[0].toString();
				Map<String,String> proMap = null;
				Integer totalQuantity = Integer.parseInt(obj[2].toString());
				if(tempMap.get(productName)==null){
					proMap = Maps.newHashMap();
				}else{
					proMap = tempMap.get(productName);
				}
				
				if(refMap.get(productName)!=null){
					totalQuantity+=refMap.get(productName);
				}
				
				proMap.put(obj[1].toString(), obj[2].toString());
				tempMap.put(productName, proMap);
				refMap.put(productName, totalQuantity);
			}
		}
		return tempMap;
	}
	
	public List<CustomProductProblem>  getProblemDetails(CustomProductProblem problem){
		DetachedCriteria dc = customProductProblemDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(problem.getCountry())){
			dc.add(Restrictions.eq("country",problem.getCountry()));
		}
		if(StringUtils.isNotEmpty(problem.getProductName())){
			dc.add(Restrictions.eq("productName",problem.getProductName()));
		}
		
		if(StringUtils.isNotEmpty(problem.getProblemType())){
			dc.add(Restrictions.eq("problemType",problem.getProblemType()));
		}
		if(problem.getCreateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(problem.getCreateDate(),1)));
		}
		dc.add(Restrictions.ge("createDate",problem.getDataDate()));
		return this.customProductProblemDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void saveOrUpdateEmailProblem() {
		String sql = "INSERT INTO custom_product_problem (data_id,country,product_name,problem_type,problem,order_nos,create_date,data_date,data_type,revert_email)"
				+ " SELECT a.`id`,a.`country`,a.`product_name`,a.problem_type,a.`problem`,a.`order_nos`,NOW(),a.`create_date`,'1',a.`revert_email`"
				+ " FROM custom_email_manager a "
				+ " WHERE a.`del_flag`='0' AND a.problem_type IS NOT NULL AND a.problem_type !='' and a.state != '4' AND a.`create_date`>DATE_ADD(CURRENT_DATE(),INTERVAL -5 DAY)"
				+ " ON DUPLICATE KEY "
				+ " UPDATE `country`=VALUES(country),`product_name`=VALUES(product_name),`problem_type`=VALUES(problem_type), "
				+ " `problem`=VALUES(problem),`order_nos`=VALUES(order_nos) ";
		customProductProblemDao.updateBySql(sql, null);
	}
	
	@Transactional(readOnly = false)
	public void saveOrUpdateEventProblem() {
		String sql = "INSERT INTO custom_product_problem (data_id,country,product_name,problem_type,problem,order_nos,create_date,data_date,data_type) "
				+ " SELECT a.`id`,a.`country`,a.`product_name`,a.problem_type,a.`reason` AS problem,a.`invoice_number` AS order_nos,NOW(),a.`create_date`,'2' "
				+ " FROM custom_event_manager a  "
				+ " WHERE a.`del_flag`='0' AND a.problem_type IS NOT NULL AND a.problem_type !='' and a.type in ('1','2')  AND a.`create_date`>DATE_ADD(CURRENT_DATE(),INTERVAL -5 DAY)"
				+ " ON DUPLICATE KEY "
				+ " UPDATE `country`=VALUES(country),`product_name`=VALUES(product_name),`problem_type`=VALUES(problem_type), "
				+ " `problem`=VALUES(problem),`order_nos`=VALUES(order_nos) ";
		customProductProblemDao.updateBySql(sql, null);
	}
	
	@Transactional(readOnly = false)
	public void duplicateEmailProblem() {
		String sql = "DELETE FROM a USING custom_product_problem AS a,custom_product_problem AS b "
					+ "WHERE a.`data_date`>b.`data_date` AND a.`revert_email` = b.`revert_email` AND a.`product_name`=b.`product_name` AND a.`data_type`='1' ";
		customProductProblemDao.updateBySql(sql, null);
	}
	
	@Transactional(readOnly = false)
	public void saveProblemType(CustomProductTypeProblems problems) {
		customProductTypeProblemsDao.save(problems);
	}
	
	public List<CustomProductTypeProblems> findAllProblemType(CustomProductTypeProblems problems) {
		DetachedCriteria dc = customProductTypeProblemsDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(problems.getProductType())) {
			problems.setProductType(Encodes.unescapeHtml(problems.getProductType()));
			dc.add(Restrictions.eq("productType", problems.getProductType()));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		return customProductTypeProblemsDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void init(Map<String, List<String>> typeproblemmap) {
		for (Entry<String, List<String>> entry: typeproblemmap.entrySet()) {
			String type = entry.getKey();
			List<String> list = entry.getValue();
			for (String problemType : list) {
				CustomProductTypeProblems problems = new CustomProductTypeProblems();
				problems.setProductType(type);
				problems.setProblemType(problemType);
				problems.setDelFlag("0");
				saveProblemType(problems);
			}
		}
	}
	
	/**
	 * 组合问题类型
	 * @return Map<String, List<String>> [产品类型	 问题类型list]
	 */
	public Map<String, List<String>> findProblemType() {
		Map<String, List<String>> rs = Maps.newHashMap();
		String sql = "SELECT t.`product_type`,t.`problem_type` FROM `custom_product_type_problems` t WHERE t.`del_flag`='0'";
		List<Object[]> list = customProductTypeProblemsDao.findBySql(sql);
		for (Object[] objects : list) {
			String productType = objects[0].toString();
			String problemType = objects[1].toString();
			List<String> typeList = rs.get(productType);
			if (typeList == null) {
				typeList = Lists.newArrayList();
				rs.put(productType, typeList);
			}
			typeList.add(problemType);
		}
		return rs;
	}

	public CustomProductTypeProblems getProblemTypeById(Integer id) {
		return customProductTypeProblemsDao.get(id);
	}

	public CustomProductTypeProblems getProblemTypeByTypeAndProblem(String productType, String problemType) {
		DetachedCriteria dc = customProductTypeProblemsDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("productType", productType));
		dc.add(Restrictions.eq("problemType", problemType));
		List<CustomProductTypeProblems> list = customProductTypeProblemsDao.find(dc);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		CustomProductProblemService  a= applicationContext.getBean(CustomProductProblemService.class);
		a.init(ProductTypeUtils.typeProblemMap);
		applicationContext.close();
	}

	public Map<String, String> findMaster(List<CustomProductProblem> customEmails) {
		Map<String, String> rsMap = Maps.newHashMap();
		List<Integer> eventIds = Lists.newArrayList();
		List<String> emailIds = Lists.newArrayList();
		for (CustomProductProblem problem : customEmails) {
			if ("1".equals(problem.getpKey().getDataType())) {
				emailIds.add(problem.getpKey().getDataId());
			} else {
				eventIds.add(Integer.parseInt(problem.getpKey().getDataId()));
			}
		}
		if (eventIds.size()>0) {
			String sql = "SELECT t.`id`,u.`name` FROM custom_event_manager t,sys_user u "+
					" WHERE t.`master_by`=u.`id` AND t.`id` IN :p1";
			List<Object[]> list = customProductTypeProblemsDao.findBySql(sql, new Parameter(eventIds));
			for (Object[] obj : list) {
				if (obj[0] == null || obj[1] == null) {
					continue;
				}
				rsMap.put(obj[0].toString(), obj[1].toString());
			}
		}
		if (emailIds.size()>0) {
			String sql = "SELECT t.`id`,u.`name` FROM custom_email_manager t,sys_user u "+
					" WHERE t.`master_by`=u.`id` AND t.`id` IN :p1";
			List<Object[]> list = customProductTypeProblemsDao.findBySql(sql, new Parameter(emailIds));
			for (Object[] obj : list) {
				if (obj[0] == null || obj[1] == null) {
					continue;
				}
				rsMap.put(obj[0].toString(), obj[1].toString());
			}
		}
		return rsMap;
	}
}
