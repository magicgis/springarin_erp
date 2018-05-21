/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.plan.service;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.plan.dao.PlanDao;
import com.springrain.erp.modules.plan.dto.Month.Week;
import com.springrain.erp.modules.plan.entity.Plan;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 日常工作计划Service
 * @author tim
 * @version 2014-03-25
 */
@Component
@Transactional(readOnly = true)
public class PlanService extends BaseService {

	@Autowired
	private PlanDao planDao;
	
	public Plan get(String id) {
		return planDao.get(id);
	}
	
	public List<Plan> findMonthPlan(String year ,String month , String userId , String dep) {
		return planDao.findMonthPlan(year, month, userId, dep);
	}
	
	public List<Plan> findLogPlan(Week weeks,String year ,String month ,String week,String userId) {
		return planDao.findWeekAndLogPlans(weeks,year,month,week,userId);
	}
	
	public Plan findCurrentDayLogPlan() {
		DetachedCriteria dc = planDao.createDetachedCriteria();
		dc.add(Restrictions.eq(Plan.FIELD_DEL_FLAG, Plan.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("type","0"));
		dc.add(Restrictions.eq("flag",DateUtils.getDate("yyyy/MM/dd")));
		dc.add(Restrictions.eq("createBy",UserUtils.getUser()));
		List<Plan> list =  planDao.find(dc);
		if(list.size()==1){
			return list.get(0);
		}
		return null;
	}
	
	public List<Plan> viewPlan(Date date,String cweek,Office office,User user) {
		return planDao.viewPlans(date,cweek, office,user);
	}
	
	public List<Plan> viewMonthPlan(String month,Office office,User user) {
		return planDao.viewMonthPlan(month,office,user);
	}
	
	@Transactional(readOnly = false)
	public void save(Plan plan) {
		DetachedCriteria dc = planDao.createDetachedCriteria();
		if(!"3".equals(plan.getType())){
			dc.add(Restrictions.eq("createBy", plan.getCreateBy()));
		}
		dc.add(Restrictions.eq("flag", plan.getFlag()));
		dc.add(Restrictions.eq("type", plan.getType()));
		if(planDao.count(dc)>0){
			Plan plan1 = planDao.find(dc).get(0);
			if(plan.getContent()!=null){
				plan1.setContent(plan.getContent());
				plan1.setUpdateBy(plan.getUpdateBy());
				plan1.setUpdateDate(new Date());
				planDao.save(plan1);
			}
		}else{
			planDao.save(plan);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		planDao.deleteById(id);
	}
	
	@Transactional(readOnly = false)
	public void save(List<Plan> plans) {
		for (Plan plan : plans) {
			save(plan);
		}
	}
	
}
