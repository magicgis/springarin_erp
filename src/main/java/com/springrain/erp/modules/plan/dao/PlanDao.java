package com.springrain.erp.modules.plan.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Repository;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.plan.dto.Month.Week;
import com.springrain.erp.modules.plan.entity.Plan;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 日常工作计划DAO接口
 * @author tim
 * @version 2014-03-25
 */
@Repository
public class PlanDao extends BaseDao<Plan> {
	
	public List<Plan> findMonthPlan(String year ,String month , String userId , String dep){
		String p1 = year+"/"+month;
		String p2 = p1+"/"+dep;
		String p3 = userId ;
		return find("from Plan where (flag =:p2 and type = 3) or (createBy.id=:p3 and ((flag = :p1 and type = 2) or (type = 1 and flag like CONCAT(:p1,'%'))))", new Parameter(p1,p2,p3));
	}
	
	public List<Plan> findWeekAndLogPlans(Week weeks,String year,String month,String weekNum,String userId){
		String p1 = year+"/"+month+"/"+weekNum;
		Collection<String> temp = Collections2.transform(Arrays.asList(weeks.getDayData()), new Function<Date, String>() {
			public String apply(Date date) {
				if(null!=date){
					return format.format(date);
				}
				return null;
			};
		});            
		List<String> p2 = Lists.newArrayList(temp.iterator());
		p2.remove(null);
		String p3 = userId ;
		return find("from Plan where createBy.id=:p3 and ((type = 1 and flag =:p1) or (flag in (:p2)))", new Parameter(p1,p2,p3));
	}
	
	private final static DateFormat format = new SimpleDateFormat("yyyy/MM/dd",Locale.CHINESE); 
	
	public List<Plan> viewPlans(Date date,String cweek,Office office,User user){
		String year = (date.getYear()+1900)+"";
		String month = (date.getMonth()+1)+"";
		String p1 = year+"/"+month;
		String p2 = p1+"/"+office.getId();
		List<User> p3 = Lists.newArrayList();
		if(user!=null)
			p3.add(user);
		else
			p3.addAll(office.getUserList());
		String p4 = p1+"/"+cweek;
		List<String> p5 = Lists.newArrayList();
		int i = date.getDay();
		if(i>5){
			i=5;
			date = DateUtils.addDays(date, -1);
		}else if(i==0){
			i= 5 ;
			date = DateUtils.addDays(date, -2);
		}
		for (int j = 0; j < i; j++) {
			p5.add(format.format(DateUtils.addDays(date, -j)));
		}
		return find("from Plan where (flag =:p2 and type = 3) or (createBy in (:p3) and ((flag = :p1 and type = 2) or (type = 1 and flag = :p4) or (type = 0 and flag in (:p5)))) order by flag", new Parameter(p1,p2,p3,p4,p5));
	}
	
	public List<Plan> viewMonthPlan(String month,Office office,User user){
		String year = DateUtils.getYear();
		String p1 = year+"/"+month;
		String p2 = p1+"/"+office.getId();
		String p4 = p1+"/%";
		if(month.length()==1){
			month = "0"+month;
		}
		String p5 = year+"/"+month+"/%";
		List<User> p3 = Lists.newArrayList();
		if(user!=null)
			p3.add(user);
		else
			p3.addAll(office.getUserList());
		return find("from Plan where (flag =:p2 and type = 3) or (createBy in (:p3) and ((flag = :p1 and type = 2) or (type = 1 and flag like :p4) or (type = 0 and flag like :p5))) order by flag", new Parameter(p1,p2,p3,p4,p5));
	}
}
