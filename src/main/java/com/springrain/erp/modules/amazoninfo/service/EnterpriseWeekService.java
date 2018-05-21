/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.EnterpriseWeekDao;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseWeek;
import com.springrain.erp.modules.amazoninfo.entity.EnterpriseWeight;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.DictUtils;


@Component
@Transactional(readOnly = true)
public class EnterpriseWeekService extends BaseService {

	@Autowired
	private EnterpriseWeekDao enterpriseWeekDao;
	
	
	public EnterpriseWeek get(Integer id) {
		return enterpriseWeekDao.get(id);
	}
	
	
	@Transactional(readOnly = false)
	public String updateWeight(String country,Float updValue,String type){
		int i=0;
		if("1".equals(type)){
			String sql="update amazoninfo_enterprise_weight set monday=:p1 where country=:p2 and flag=1 ";
			i=enterpriseWeekDao.updateBySql(sql, new Parameter(updValue,country));
		}else if("2".equals(type)){
			String sql="update amazoninfo_enterprise_weight set tuesday=:p1 where country=:p2 and flag=1 ";
			i=enterpriseWeekDao.updateBySql(sql, new Parameter(updValue,country));
		}else if("3".equals(type)){
			String sql="update amazoninfo_enterprise_weight set wednesday=:p1 where country=:p2 and flag=1 ";
			i=enterpriseWeekDao.updateBySql(sql, new Parameter(updValue,country));
		}else if("4".equals(type)){
			String sql="update amazoninfo_enterprise_weight set thursday=:p1 where country=:p2 and flag=1 ";
			i=enterpriseWeekDao.updateBySql(sql, new Parameter(updValue,country));
		}else if("5".equals(type)){
			String sql="update amazoninfo_enterprise_weight set friday=:p1 where country=:p2 and flag=1 ";
			i=enterpriseWeekDao.updateBySql(sql, new Parameter(updValue,country));
		}else if("6".equals(type)){
			String sql="update amazoninfo_enterprise_weight set saturday=:p1 where country=:p2 and flag=1 ";
			i=enterpriseWeekDao.updateBySql(sql, new Parameter(updValue,country));
		}else if("7".equals(type)){
			String sql="update amazoninfo_enterprise_weight set sunday=:p1 where country=:p2 and flag=1 ";
			i=enterpriseWeekDao.updateBySql(sql, new Parameter(updValue,country));
		}
		if(i>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	
	@Transactional(readOnly = false)
	public void createEnterpriseWeek(EnterpriseWeek enterpriseWeek,String removeCountry){
		String delSql="DELETE FROM amazoninfo_enterprise_week";
		enterpriseWeekDao.updateBySql(delSql, null);
		String insertSql="INSERT INTO amazoninfo_enterprise_week(country,WEEK,monday,tuesday,wednesday,thursday,friday,saturday,sunday) " +
				" SELECT country,DATE_FORMAT(DATE,'%x%v') DATE,"+
				"TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(monday)*"
				+AmazonProduct2Service.getRateConfig().get("EUR/USD")+" WHEN a.`country`='uk' THEN SUM(monday)*"+AmazonProduct2Service.getRateConfig().get("GBP/USD")+" WHEN a.`country`='ca' THEN SUM(monday)*"
				+AmazonProduct2Service.getRateConfig().get("CAD/USD")+" WHEN a.`country`='jp' THEN SUM(monday)*"+
				AmazonProduct2Service.getRateConfig().get("JPY/USD")+" WHEN a.`country`='mx' THEN SUM(monday)*"+AmazonProduct2Service.getRateConfig().get("MXN/USD")+" ELSE SUM(monday) END ),2), "+
				"TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(tuesday)*"
				+AmazonProduct2Service.getRateConfig().get("EUR/USD")+" WHEN a.`country`='uk' THEN SUM(tuesday)*"+AmazonProduct2Service.getRateConfig().get("GBP/USD")+" WHEN a.`country`='ca' THEN SUM(tuesday)*"
				+AmazonProduct2Service.getRateConfig().get("CAD/USD")+" WHEN a.`country`='jp' THEN SUM(tuesday)*"+
				AmazonProduct2Service.getRateConfig().get("JPY/USD")+" WHEN a.`country`='mx' THEN SUM(tuesday)*"+AmazonProduct2Service.getRateConfig().get("MXN/USD")+" ELSE SUM(tuesday) END ),2), "+
				"TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(wednesday)*"
				+AmazonProduct2Service.getRateConfig().get("EUR/USD")+" WHEN a.`country`='uk' THEN SUM(wednesday)*"+AmazonProduct2Service.getRateConfig().get("GBP/USD")+" WHEN a.`country`='ca' THEN SUM(wednesday)*"
				+AmazonProduct2Service.getRateConfig().get("CAD/USD")+" WHEN a.`country`='jp' THEN SUM(wednesday)*"+
				AmazonProduct2Service.getRateConfig().get("JPY/USD")+" WHEN a.`country`='mx' THEN SUM(wednesday)*"+AmazonProduct2Service.getRateConfig().get("MXN/USD")+" ELSE SUM(wednesday) END ),2), "+
				"TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(thursday)*"
				+AmazonProduct2Service.getRateConfig().get("EUR/USD")+" WHEN a.`country`='uk' THEN SUM(thursday)*"+AmazonProduct2Service.getRateConfig().get("GBP/USD")+" WHEN a.`country`='ca' THEN SUM(thursday)*"
				+AmazonProduct2Service.getRateConfig().get("CAD/USD")+" WHEN a.`country`='jp' THEN SUM(thursday)*"+
				AmazonProduct2Service.getRateConfig().get("JPY/USD")+" WHEN a.`country`='mx' THEN SUM(thursday)*"+AmazonProduct2Service.getRateConfig().get("MXN/USD")+" ELSE SUM(thursday) END ),2), "+
				"TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(friday)*"
				+AmazonProduct2Service.getRateConfig().get("EUR/USD")+" WHEN a.`country`='uk' THEN SUM(friday)*"+AmazonProduct2Service.getRateConfig().get("GBP/USD")+" WHEN a.`country`='ca' THEN SUM(friday)*"
				+AmazonProduct2Service.getRateConfig().get("CAD/USD")+" WHEN a.`country`='jp' THEN SUM(friday)*"+
				AmazonProduct2Service.getRateConfig().get("JPY/USD")+" WHEN a.`country`='mx' THEN SUM(friday)*"+AmazonProduct2Service.getRateConfig().get("MXN/USD")+" ELSE SUM(friday) END ),2), "+
				"TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(saturday)*"
				+AmazonProduct2Service.getRateConfig().get("EUR/USD")+" WHEN a.`country`='uk' THEN SUM(saturday)*"+AmazonProduct2Service.getRateConfig().get("GBP/USD")+" WHEN a.`country`='ca' THEN SUM(saturday)*"
				+AmazonProduct2Service.getRateConfig().get("CAD/USD")+" WHEN a.`country`='jp' THEN SUM(saturday)*"+
				AmazonProduct2Service.getRateConfig().get("JPY/USD")+" WHEN a.`country`='mx' THEN SUM(saturday)*"+AmazonProduct2Service.getRateConfig().get("MXN/USD")+" ELSE SUM(saturday) END ),2), "+
				"TRUNCATE((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN SUM(sunday)*"
				+AmazonProduct2Service.getRateConfig().get("EUR/USD")+" WHEN a.`country`='uk' THEN SUM(sunday)*"+AmazonProduct2Service.getRateConfig().get("GBP/USD")+" WHEN a.`country`='ca' THEN SUM(sunday)*"
				+AmazonProduct2Service.getRateConfig().get("CAD/USD")+" WHEN a.`country`='jp' THEN SUM(sunday)*"+
				AmazonProduct2Service.getRateConfig().get("JPY/USD")+" WHEN a.`country`='mx' THEN SUM(sunday)*"+AmazonProduct2Service.getRateConfig().get("MXN/USD")+" ELSE SUM(sunday) END ),2) FROM ( "+
				//" SUM(monday),SUM(tuesday),SUM(wednesday),SUM(thursday),SUM(friday),SUM(saturday),SUM(sunday) FROM (" +
				" SELECT country,DATE_FORMAT(DATE,'%Y-%m-%d') DATE,(CASE WHEN DATE_FORMAT(DATE,'%w')=1 THEN SUM(sure_sales) ELSE 0 END ) monday" +
				",(CASE WHEN DATE_FORMAT(DATE,'%w')=2 THEN SUM(sure_sales) ELSE 0 END ) tuesday" +
				",(CASE WHEN DATE_FORMAT(DATE,'%w')=3 THEN SUM(sure_sales) ELSE 0 END ) wednesday" +
				",(CASE WHEN DATE_FORMAT(DATE,'%w')=4 THEN SUM(sure_sales) ELSE 0 END ) thursday" +
				",(CASE WHEN DATE_FORMAT(DATE,'%w')=5 THEN SUM(sure_sales) ELSE 0 END ) friday" +
				",(CASE WHEN DATE_FORMAT(DATE,'%w')=6 THEN SUM(sure_sales) ELSE 0 END ) saturday" +
				",(CASE WHEN DATE_FORMAT(DATE,'%w')=0 THEN SUM(sure_sales) ELSE 0 END ) sunday" +
				" FROM amazoninfo_sale_report a " +
				" WHERE  a.order_type='1' and DATE_FORMAT(DATE,'%Y-%m-%d')>=:p1 AND DATE_FORMAT(DATE,'%Y-%m-%d')<=:p2 and a.sure_sales!=0 " +
		     	" GROUP BY country,DATE_FORMAT(DATE,'%Y-%m-%d') ORDER BY DATE,country) a GROUP BY country,DATE_FORMAT(DATE,'%x%v') ";
		enterpriseWeekDao.updateBySql(insertSql, new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(enterpriseWeek.getStartDate()),new SimpleDateFormat("yyyy-MM-dd").format(enterpriseWeek.getEndDate())));
	    
		 List<EnterpriseWeight> weightList=createEnterpriseWeight(removeCountry);
		 String delSql2="DELETE FROM amazoninfo_enterprise_weight";
		 enterpriseWeekDao.updateBySql(delSql2, null);
		 for (EnterpriseWeight w : weightList) {
			 String insertSql2="insert into amazoninfo_enterprise_weight(flag,country,monday,tuesday,wednesday,thursday,friday,saturday,sunday) "+
		     " values("+w.getFlag()+",'"+w.getCountry()+"',"+w.getMonday()+","+w.getTuesday()+","+w.getWednesday()+","+w.getThursday()+","+w.getFriday()+","+w.getSaturday()+","+w.getSunday()+") ";
			 enterpriseWeekDao.updateBySql(insertSql2, null);
		}
	}
	
	
	public Map<String,Map<Integer,Float>> findByCountryWeight(){
		String sql="select country,monday,tuesday,wednesday,thursday,friday,saturday,sunday from amazoninfo_enterprise_weight where flag='1' ";
		List<Object[]> list=enterpriseWeekDao.findBySql(sql);
		Map<String,Map<Integer,Float>> map=new HashMap<String,Map<Integer,Float>>();
		for (Object[] obj : list) {
			Map<Integer,Float> weigth=new HashMap<Integer,Float>();
			weigth.put(1,((BigDecimal)obj[1]).floatValue());
			weigth.put(2,((BigDecimal)obj[2]).floatValue());
			weigth.put(3,((BigDecimal)obj[3]).floatValue());
			weigth.put(4,((BigDecimal)obj[4]).floatValue());
			weigth.put(5,((BigDecimal)obj[5]).floatValue());
			weigth.put(6,((BigDecimal)obj[6]).floatValue());
			weigth.put(7,((BigDecimal)obj[7]).floatValue());
			map.put(obj[0].toString(),weigth);
		}
		return map;
	}
	
	//查询某个月总权重值
	public Map<String,Float> findWeightByMonth(Date startDate,Date endDate, Map<String,Map<Integer,Float>> allWeight){
		   SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		   Map<String,Float> map=new HashMap<String,Float>();
		   if(allWeight.size()>0){
			   List<Dict> dictAll=DictUtils.getDictList("platform");
		       while(endDate.after(startDate)||endDate.equals(startDate)){
		    	   int week=dayForWeek(sdf.format(startDate));
		    	   for (Dict dict : dictAll) {
		   			  if(!"com.unitek".equals(dict.getValue())){
		   			     if(map.get(dict.getValue())==null){
		   			    	 Float w=1f;
		   			    	 if(allWeight.get(dict.getValue())!=null){
		   			    		 w=allWeight.get(dict.getValue()).get(week);
		   			    	 }
		   			    	map.put(dict.getValue(),w);
		   			     }else{
		   			    	 Float w=1f;
		   			    	 if(allWeight.get(dict.getValue())!=null){
		   			    		 w=allWeight.get(dict.getValue()).get(week);
		   			    	 }
		   			    	 float old=map.get(dict.getValue());
		   			    	 map.remove(dict.getValue());
		   			    	 map.put(dict.getValue(),old+w); 
		   			     }

		   			  }
		   		   }
		    	   if(map.get("totalAvg")==null){
		    		   Float w=allWeight.get("totalAvg").get(week);
		    		   map.put("totalAvg", w);
		    	   }else{
		    		   Float w=allWeight.get("totalAvg").get(week);
					   float old=map.get("totalAvg");
					   map.remove("totalAvg");
					   map.put("totalAvg",old+w); 
		    	   }
				   startDate = DateUtils.addDays(startDate, 1);
				}
		   }
		   return map;
		}
	
	public Map<String,Float> findWeightByMonth(Date startDate,Date endDate){
	   SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
	   Map<String,Float> map=new HashMap<String,Float>();
	   Map<String,Map<Integer,Float>> allWeight=findByCountryWeight();
	   if(allWeight.size()>0){
		   List<Dict> dictAll=DictUtils.getDictList("platform");
	       while(endDate.after(startDate)||endDate.equals(startDate)){
	    	   int week=dayForWeek(sdf.format(startDate));
	    	   for (Dict dict : dictAll) {
	   			  if(!"com.unitek".equals(dict.getValue())){
	   			     if(map.get(dict.getValue())==null){
	   			    	 Float w=1f;
	   			    	 if(allWeight.get(dict.getValue())!=null){
	   			    		 w=allWeight.get(dict.getValue()).get(week);
	   			    	 }
	   			    	map.put(dict.getValue(),w);
	   			     }else{
	   			    	 Float w=1f;
	   			    	 if(allWeight.get(dict.getValue())!=null){
	   			    		 w=allWeight.get(dict.getValue()).get(week);
	   			    	 }
	   			    	 float old=map.get(dict.getValue());
	   			    	 map.remove(dict.getValue());
	   			    	 map.put(dict.getValue(),old+w); 
	   			     }

	   			  }
	   		   }
	    	   if(map.get("totalAvg")==null){
	    		   Float w=allWeight.get("totalAvg").get(week);
	    		   map.put("totalAvg", w);
	    	   }else{
	    		   Float w=allWeight.get("totalAvg").get(week);
				   float old=map.get("totalAvg");
				   map.remove("totalAvg");
				   map.put("totalAvg",old+w); 
	    	   }
			   startDate = DateUtils.addDays(startDate, 1);
			}
	   }
	   return map;
	}
	
	public  int dayForWeek(String pTime){  
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
		 Calendar c = Calendar.getInstance();  
		 int dayForWeek = 0; 
		 try {
			 c.setTime(format.parse(pTime));
			 if(c.get(Calendar.DAY_OF_WEEK) == 1){  
			  dayForWeek = 7;  
			 }else{  
			  dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;  
			 }  
		} catch (ParseException e) {
			 e.printStackTrace();
		}  
		 return dayForWeek;  
	 }  
	
	public  List<EnterpriseWeight> findEnterpriseWeight(EnterpriseWeek enterpriseWeek){
		String sql="";
		List<Object[]> list=null;
		List<EnterpriseWeight> EnterpriseWeightList=new ArrayList<EnterpriseWeight>();
		if(StringUtils.isBlank(enterpriseWeek.getCountry())){
		   sql="select monday,tuesday,wednesday,thursday,friday,saturday,sunday,country,flag from amazoninfo_enterprise_weight where country='totalAvg' ";
		   list=enterpriseWeekDao.findBySql(sql);
		}else{
		   sql="select monday,tuesday,wednesday,thursday,friday,saturday,sunday,country,flag from amazoninfo_enterprise_weight where country=:p1 ";
		   list=enterpriseWeekDao.findBySql(sql,new Parameter(enterpriseWeek.getCountry()));
		}
		for (Object[] obj : list) {
			EnterpriseWeight week=new EnterpriseWeight();
			week.setMonday(((BigDecimal)obj[0]).floatValue());
			week.setTuesday(((BigDecimal)obj[1]).floatValue());
			week.setWednesday(((BigDecimal)obj[2]).floatValue());
			week.setThursday(((BigDecimal)obj[3]).floatValue());
			week.setFriday(((BigDecimal)obj[4]).floatValue());
			week.setSaturday(((BigDecimal)obj[5]).floatValue());
			week.setSunday(((BigDecimal)obj[6]).floatValue());
			week.setCountry(obj[7].toString());
			week.setFlag(obj[8].toString());
			EnterpriseWeightList.add(week);
		}
		return EnterpriseWeightList;
	}
	
	public  List<EnterpriseWeek> findEnterpriseWeek(EnterpriseWeek enterpriseWeek){
		String sqlString="";
		List<Object[]> list=null;
		List<EnterpriseWeek> EnterpriseWeekList=new ArrayList<EnterpriseWeek>();
		if(StringUtils.isBlank(enterpriseWeek.getCountry())){
			sqlString="SELECT WEEK,SUM(monday),SUM(tuesday),SUM(wednesday),SUM(thursday),SUM(friday),SUM(saturday),SUM(sunday) FROM amazoninfo_enterprise_week GROUP BY WEEK";
			list=enterpriseWeekDao.findBySql(sqlString);
		}else{
			sqlString="SELECT WEEK,SUM(monday),SUM(tuesday),SUM(wednesday),SUM(thursday),SUM(friday),SUM(saturday),SUM(sunday) FROM amazoninfo_enterprise_week where country=:p1 GROUP BY country,WEEK";
			list=enterpriseWeekDao.findBySql(sqlString,new Parameter(enterpriseWeek.getCountry()));
		}
		Float totalMonday=0f;
		Float totalTuesday=0f;
		Float totalWednesday=0f;
		Float totalThursday=0f;
		Float totalFriday=0f;
		Float totalSaturday=0f;
		Float totalSunday=0f;
		
		int totalMondayCount=0;
		int totalTuesdayCount=0;
		int totalWednesdayCount=0;
		int totalThursdayCount=0;
		int totalFridayCount=0;
		int totalSaturdayCount=0;
		int totalSundayCount=0;
		for (Object[] obj: list) {
			EnterpriseWeek week=new EnterpriseWeek();
			week.setWeek(obj[0].toString());
			week.setMonday(((BigDecimal)obj[1]).floatValue());
			week.setTuesday(((BigDecimal)obj[2]).floatValue());
			week.setWednesday(((BigDecimal)obj[3]).floatValue());
			week.setThursday(((BigDecimal)obj[4]).floatValue());
			week.setFriday(((BigDecimal)obj[5]).floatValue());
			week.setSaturday(((BigDecimal)obj[6]).floatValue());
			week.setSunday(((BigDecimal)obj[7]).floatValue());
			EnterpriseWeekList.add(week);
			
			totalMonday+=((BigDecimal)obj[1]).floatValue();
			totalTuesday+=((BigDecimal)obj[2]).floatValue();
			totalWednesday+=((BigDecimal)obj[3]).floatValue();
			totalThursday+=((BigDecimal)obj[4]).floatValue();
			totalFriday+=((BigDecimal)obj[5]).floatValue();
			totalSaturday+=((BigDecimal)obj[6]).floatValue();
			totalSunday+=((BigDecimal)obj[7]).floatValue();
			if(((BigDecimal)obj[1]).floatValue()!=0){totalMondayCount++;}
			if(((BigDecimal)obj[2]).floatValue()!=0){totalTuesdayCount++;}
			if(((BigDecimal)obj[3]).floatValue()!=0){totalWednesdayCount++;}
			if(((BigDecimal)obj[4]).floatValue()!=0){totalThursdayCount++;}
			if(((BigDecimal)obj[5]).floatValue()!=0){totalFridayCount++;}
			if(((BigDecimal)obj[6]).floatValue()!=0){totalSaturdayCount++;}
			if(((BigDecimal)obj[7]).floatValue()!=0){totalSundayCount++;}
		}
		if(list.size()>0){
			EnterpriseWeek total=new EnterpriseWeek();
			total.setWeek("avg");
			
			total.setMonday(new BigDecimal(totalMonday/totalMondayCount).setScale(2,4).floatValue());
			total.setTuesday(new BigDecimal(totalTuesday/totalTuesdayCount).setScale(2,4).floatValue());
			total.setWednesday(new BigDecimal(totalWednesday/totalWednesdayCount).setScale(2,4).floatValue());
			total.setThursday(new BigDecimal(totalThursday/totalThursdayCount).setScale(2,4).floatValue());
			total.setFriday(new BigDecimal(totalFriday/totalFridayCount).setScale(2,4).floatValue());
			total.setSaturday(new BigDecimal(totalSaturday/totalSaturdayCount).setScale(2,4).floatValue());
			total.setSunday(new BigDecimal(totalSunday/totalSundayCount).setScale(2,4).floatValue());
			EnterpriseWeekList.add(total);
		}
		
		return EnterpriseWeekList;
		
    }
	
	public List<EnterpriseWeight> createEnterpriseWeight(String removeCountry){
		List<EnterpriseWeight> EnterpriseWeightList=new ArrayList<EnterpriseWeight>();
		String sqlString="";
		if(StringUtils.isBlank(removeCountry)){
			/*sqlString="SELECT country,AVG(monday),AVG(tuesday),AVG(wednesday),AVG(thursday),AVG(friday),AVG(saturday),AVG(sunday) " +
					" FROM amazoninfo_enterprise_week GROUP BY country " +
					" UNION (SELECT 'totalAvg',AVG(monday),AVG(tuesday),AVG(wednesday),AVG(thursday),AVG(friday),AVG(saturday),AVG(sunday) FROM( " +
					" SELECT SUM(monday) monday,SUM(tuesday) tuesday,SUM(wednesday) wednesday,SUM(thursday) thursday,SUM(friday) friday,SUM(saturday) saturday,SUM(sunday) sunday " +
					" FROM amazoninfo_enterprise_week  GROUP BY WEEK)p) ";*/
			sqlString=" SELECT country,SUM(monday),SUM(tuesday),SUM(wednesday),SUM(thursday),SUM(friday),SUM(saturday),SUM(sunday) " +
			" FROM ( SELECT country,AVG(monday) monday,0 tuesday,0 wednesday,0 thursday,0 friday,0 saturday,0 sunday  FROM amazoninfo_enterprise_week WHERE monday!=0 GROUP BY country  " +
			"  UNION SELECT country,0 monday,AVG(tuesday) tuesday,0 wednesday,0 thursday,0 friday,0 saturday,0 sunday  FROM amazoninfo_enterprise_week WHERE tuesday!=0 GROUP BY country  " +
			"  UNION SELECT country,0 monday,0 tuesday,AVG(wednesday) wednesday,0 thursday,0 friday,0 saturday,0 sunday  FROM amazoninfo_enterprise_week WHERE wednesday!=0 GROUP BY country " + 
			"  UNION SELECT country,0 monday,0 tuesday,0 wednesday,AVG(thursday) thursday,0 friday,0 saturday,0 sunday  FROM amazoninfo_enterprise_week WHERE thursday!=0 GROUP BY country  " +
			"  UNION SELECT country,0 monday,0 tuesday,0 wednesday,0 thursday,AVG(friday) friday,0 saturday,0 sunday  FROM amazoninfo_enterprise_week WHERE friday!=0 GROUP BY country " +
			"  UNION SELECT country,0 monday,0 tuesday,0 wednesday,0 thursday,0 friday,AVG(saturday) saturday,0 sunday  FROM amazoninfo_enterprise_week WHERE saturday!=0 GROUP BY country " +
			"  UNION SELECT country,0 monday,0 tuesday,0 wednesday,0 thursday,0 friday,0 saturday,AVG(sunday) sunday  FROM amazoninfo_enterprise_week WHERE sunday!=0 GROUP BY country ) p GROUP BY country " +
			"  UNION " +
			"  SELECT 'totalAvg',SUM(monday),SUM(tuesday),SUM(wednesday),SUM(thursday),SUM(friday),SUM(saturday),SUM(sunday) " +
			"  FROM(SELECT AVG(monday) monday,0 tuesday,0 wednesday,0 thursday,0 friday,0 saturday,0 sunday FROM(SELECT SUM(monday) monday FROM amazoninfo_enterprise_week  GROUP BY WEEK)p WHERE p.monday!=0 " +
			"  UNION SELECT 0 monday,AVG(tuesday) tuesday,0 wednesday,0 thursday,0 friday,0 saturday,0 sunday FROM(SELECT SUM(tuesday) tuesday FROM amazoninfo_enterprise_week  GROUP BY WEEK)p WHERE p.tuesday!=0 " +
			"  UNION SELECT 0 monday,0 tuesday,AVG(wednesday) wednesday,0 thursday,0 friday,0 saturday,0 sunday FROM(SELECT SUM(wednesday) wednesday FROM amazoninfo_enterprise_week  GROUP BY WEEK)p WHERE p.wednesday!=0 " +
			"  UNION SELECT 0 monday,0 tuesday,0 wednesday,AVG(thursday) thursday,0 friday,0 saturday,0 sunday  FROM(SELECT SUM(thursday) thursday FROM amazoninfo_enterprise_week  GROUP BY WEEK)p WHERE p.thursday!=0 " +
			"  UNION SELECT 0 monday,0 tuesday,0 wednesday,0 thursday,AVG(friday) friday,0 saturday,0 sunday FROM(SELECT SUM(friday) friday FROM amazoninfo_enterprise_week  GROUP BY WEEK)p WHERE p.friday!=0 " +
			"  UNION SELECT 0 monday,0 tuesday,0 wednesday,0 thursday,0 friday,AVG(saturday) saturday,0 sunday FROM(SELECT SUM(saturday) saturday FROM amazoninfo_enterprise_week  GROUP BY WEEK)p WHERE p.saturday!=0 " +
			"  UNION SELECT 0 monday,0 tuesday,0 wednesday,0 thursday,0 friday,0 saturday,AVG(sunday) sunday FROM(SELECT SUM(sunday) sunday FROM amazoninfo_enterprise_week  GROUP BY WEEK)p WHERE p.sunday!=0) b ";
			   
			  
		}else{
			/*sqlString="SELECT country,AVG(monday),AVG(tuesday),AVG(wednesday),AVG(thursday),AVG(friday),AVG(saturday),AVG(sunday) " +
					" FROM amazoninfo_enterprise_week GROUP BY country " +
					" UNION (SELECT 'totalAvg',AVG(monday),AVG(tuesday),AVG(wednesday),AVG(thursday),AVG(friday),AVG(saturday),AVG(sunday) FROM( " +
					" SELECT SUM(monday) monday,SUM(tuesday) tuesday,SUM(wednesday) wednesday,SUM(thursday) thursday,SUM(friday) friday,SUM(saturday) saturday,SUM(sunday) sunday " +
					" FROM amazoninfo_enterprise_week where country!='"+removeCountry+"' GROUP BY WEEK)p) ";*/
			sqlString=" SELECT country,SUM(monday),SUM(tuesday),SUM(wednesday),SUM(thursday),SUM(friday),SUM(saturday),SUM(sunday) " +
					" FROM ( SELECT country,AVG(monday) monday,0 tuesday,0 wednesday,0 thursday,0 friday,0 saturday,0 sunday  FROM amazoninfo_enterprise_week WHERE monday!=0 GROUP BY country  " +
					"  UNION SELECT country,0 monday,AVG(tuesday) tuesday,0 wednesday,0 thursday,0 friday,0 saturday,0 sunday  FROM amazoninfo_enterprise_week WHERE tuesday!=0 GROUP BY country  " +
					"  UNION SELECT country,0 monday,0 tuesday,AVG(wednesday) wednesday,0 thursday,0 friday,0 saturday,0 sunday  FROM amazoninfo_enterprise_week WHERE wednesday!=0 GROUP BY country " + 
					"  UNION SELECT country,0 monday,0 tuesday,0 wednesday,AVG(thursday) thursday,0 friday,0 saturday,0 sunday  FROM amazoninfo_enterprise_week WHERE thursday!=0 GROUP BY country  " +
					"  UNION SELECT country,0 monday,0 tuesday,0 wednesday,0 thursday,AVG(friday) friday,0 saturday,0 sunday  FROM amazoninfo_enterprise_week WHERE friday!=0 GROUP BY country " +
					"  UNION SELECT country,0 monday,0 tuesday,0 wednesday,0 thursday,0 friday,AVG(saturday) saturday,0 sunday  FROM amazoninfo_enterprise_week WHERE saturday!=0 GROUP BY country " +
					"  UNION SELECT country,0 monday,0 tuesday,0 wednesday,0 thursday,0 friday,0 saturday,AVG(sunday) sunday  FROM amazoninfo_enterprise_week WHERE sunday!=0 GROUP BY country ) p GROUP BY country " +
					"  UNION " +
					"  SELECT 'totalAvg',SUM(monday),SUM(tuesday),SUM(wednesday),SUM(thursday),SUM(friday),SUM(saturday),SUM(sunday) " +
					"  FROM(SELECT AVG(monday) monday,0 tuesday,0 wednesday,0 thursday,0 friday,0 saturday,0 sunday FROM(SELECT SUM(monday) monday FROM amazoninfo_enterprise_week where country!='"+removeCountry+"' GROUP BY WEEK)p WHERE p.monday!=0 " +
					"  UNION SELECT 0 monday,AVG(tuesday) tuesday,0 wednesday,0 thursday,0 friday,0 saturday,0 sunday FROM(SELECT SUM(tuesday) tuesday FROM amazoninfo_enterprise_week where country!='"+removeCountry+"' GROUP BY WEEK)p WHERE p.tuesday!=0 " +
					"  UNION SELECT 0 monday,0 tuesday,AVG(wednesday) wednesday,0 thursday,0 friday,0 saturday,0 sunday FROM(SELECT SUM(wednesday) wednesday FROM amazoninfo_enterprise_week where country!='"+removeCountry+"' GROUP BY WEEK)p WHERE p.wednesday!=0 " +
					"  UNION SELECT 0 monday,0 tuesday,0 wednesday,AVG(thursday) thursday,0 friday,0 saturday,0 sunday  FROM(SELECT SUM(thursday) thursday FROM amazoninfo_enterprise_week where country!='"+removeCountry+"' GROUP BY WEEK)p WHERE p.thursday!=0 " +
					"  UNION SELECT 0 monday,0 tuesday,0 wednesday,0 thursday,AVG(friday) friday,0 saturday,0 sunday FROM(SELECT SUM(friday) friday FROM amazoninfo_enterprise_week where country!='"+removeCountry+"' GROUP BY WEEK)p WHERE p.friday!=0 " +
					"  UNION SELECT 0 monday,0 tuesday,0 wednesday,0 thursday,0 friday,AVG(saturday) saturday,0 sunday FROM(SELECT SUM(saturday) saturday FROM amazoninfo_enterprise_week where country!='"+removeCountry+"'  GROUP BY WEEK)p WHERE p.saturday!=0 " +
					"  UNION SELECT 0 monday,0 tuesday,0 wednesday,0 thursday,0 friday,0 saturday,AVG(sunday) sunday FROM(SELECT SUM(sunday) sunday FROM amazoninfo_enterprise_week   where country!='"+removeCountry+"' GROUP BY WEEK)p WHERE p.sunday!=0) b ";
					   
		}
		String delSql="DELETE FROM amazoninfo_enterprise_weight";
		enterpriseWeekDao.updateBySql(delSql, null);
		List<Object[]> list=enterpriseWeekDao.findBySql(sqlString);
		Map<String,EnterpriseWeek> map=new HashMap<String,EnterpriseWeek>();
		for (Object[] obj : list) {
			EnterpriseWeek week=new EnterpriseWeek();
			week.setCountry(obj[0].toString());
			week.setMonday(((BigDecimal)(obj[1])).floatValue());
			week.setTuesday(((BigDecimal)obj[2]).floatValue());
			week.setWednesday(((BigDecimal)obj[3]).floatValue());
			week.setThursday(((BigDecimal)obj[4]).floatValue());
			week.setFriday(((BigDecimal)obj[5]).floatValue());
			week.setSaturday(((BigDecimal)obj[6]).floatValue());
			week.setSunday(((BigDecimal)obj[7]).floatValue());
			map.put(obj[0].toString(),week);
			if("totalAvg".equals(obj[0].toString())){
				if(week.getMonday()<week.getTuesday()&&week.getMonday()<week.getWednesday()&&week.getMonday()<week.getThursday()&&week.getMonday()<week.getFriday()&&week.getMonday()<week.getSaturday()&&week.getMonday()<week.getSunday()){
					EnterpriseWeight w=new EnterpriseWeight();
					w.setCountry("totalAvg");
					w.setFlag("0");
					w.setMonday(1.00f);
					w.setTuesday(new BigDecimal(week.getTuesday()/week.getMonday()).setScale(2, 4).floatValue());
					w.setWednesday(new BigDecimal(week.getWednesday()/week.getMonday()).setScale(2, 4).floatValue());
					w.setThursday(new BigDecimal(week.getThursday()/week.getMonday()).setScale(2, 4).floatValue());
					w.setFriday(new BigDecimal(week.getFriday()/week.getMonday()).setScale(2, 4).floatValue());
					w.setSaturday(new BigDecimal(week.getSaturday()/week.getMonday()).setScale(2, 4).floatValue());
					w.setSunday(new BigDecimal(week.getSunday()/week.getMonday()).setScale(2, 4).floatValue());
					EnterpriseWeightList.add(w);
					EnterpriseWeight w1=new EnterpriseWeight();
					w1.setFlag("1");
					w1.setCountry("totalAvg");
					w1.setMonday(1.0f);
					w1.setTuesday(new BigDecimal(week.getTuesday()/week.getMonday()).setScale(1, 4).floatValue());
					w1.setWednesday(new BigDecimal(week.getWednesday()/week.getMonday()).setScale(1, 4).floatValue());
					w1.setThursday(new BigDecimal(week.getThursday()/week.getMonday()).setScale(1, 4).floatValue());
					w1.setFriday(new BigDecimal(week.getFriday()/week.getMonday()).setScale(1, 4).floatValue());
					w1.setSaturday(new BigDecimal(week.getSaturday()/week.getMonday()).setScale(1, 4).floatValue());
					w1.setSunday(new BigDecimal(week.getSunday()/week.getMonday()).setScale(1, 4).floatValue());
					EnterpriseWeightList.add(w1);
				}else if(week.getTuesday()<week.getMonday()&&week.getTuesday()<week.getWednesday()&&week.getTuesday()<week.getThursday()&&week.getTuesday()<week.getFriday()&&week.getTuesday()<week.getSaturday()&&week.getTuesday()<week.getSunday()){
					EnterpriseWeight w=new EnterpriseWeight();
					w.setCountry("totalAvg");
					w.setFlag("0");
					w.setTuesday(1.00f);
					w.setMonday(new BigDecimal(week.getMonday()/week.getTuesday()).setScale(2, 4).floatValue());
					w.setWednesday(new BigDecimal(week.getWednesday()/week.getTuesday()).setScale(2, 4).floatValue());
					w.setThursday(new BigDecimal(week.getThursday()/week.getTuesday()).setScale(2, 4).floatValue());
					w.setFriday(new BigDecimal(week.getFriday()/week.getTuesday()).setScale(2, 4).floatValue());
					w.setSaturday(new BigDecimal(week.getSaturday()/week.getTuesday()).setScale(2, 4).floatValue());
					w.setSunday(new BigDecimal(week.getSunday()/week.getTuesday()).setScale(2, 4).floatValue());
					EnterpriseWeightList.add(w);
					EnterpriseWeight w1=new EnterpriseWeight();
					w1.setFlag("1");
					w1.setCountry("totalAvg");
					w1.setTuesday(1.0f);
					w1.setMonday(new BigDecimal(week.getMonday()/week.getTuesday()).setScale(1, 4).floatValue());
					w1.setWednesday(new BigDecimal(week.getWednesday()/week.getTuesday()).setScale(1, 4).floatValue());
					w1.setThursday(new BigDecimal(week.getThursday()/week.getTuesday()).setScale(1, 4).floatValue());
					w1.setFriday(new BigDecimal(week.getFriday()/week.getTuesday()).setScale(1, 4).floatValue());
					w1.setSaturday(new BigDecimal(week.getSaturday()/week.getTuesday()).setScale(1, 4).floatValue());
					w1.setSunday(new BigDecimal(week.getSunday()/week.getTuesday()).setScale(1, 4).floatValue());
					EnterpriseWeightList.add(w1);
				}else if(week.getWednesday()<week.getMonday()&&week.getWednesday()<week.getTuesday()&&week.getWednesday()<week.getThursday()&&week.getWednesday()<week.getFriday()&&week.getWednesday()<week.getSaturday()&&week.getWednesday()<week.getSunday()){
					EnterpriseWeight w=new EnterpriseWeight();
					w.setFlag("0");
					w.setCountry("totalAvg");
					w.setWednesday(1.00f);
					w.setMonday(new BigDecimal(week.getMonday()/week.getWednesday()).setScale(2, 4).floatValue());
					w.setTuesday(new BigDecimal(week.getTuesday()/week.getWednesday()).setScale(2, 4).floatValue());
					w.setThursday(new BigDecimal(week.getThursday()/week.getWednesday()).setScale(2, 4).floatValue());
					w.setFriday(new BigDecimal(week.getFriday()/week.getWednesday()).setScale(2, 4).floatValue());
					w.setSaturday(new BigDecimal(week.getSaturday()/week.getWednesday()).setScale(2, 4).floatValue());
					w.setSunday(new BigDecimal(week.getSunday()/week.getWednesday()).setScale(2, 4).floatValue());
					EnterpriseWeightList.add(w);
					EnterpriseWeight w1=new EnterpriseWeight();
					w1.setFlag("1");
					w1.setCountry("totalAvg");
					w1.setWednesday(1.0f);
					w1.setMonday(new BigDecimal(week.getMonday()/week.getWednesday()).setScale(1, 4).floatValue());
					w1.setTuesday(new BigDecimal(week.getTuesday()/week.getWednesday()).setScale(1, 4).floatValue());
					w1.setThursday(new BigDecimal(week.getThursday()/week.getWednesday()).setScale(1, 4).floatValue());
					w1.setFriday(new BigDecimal(week.getFriday()/week.getWednesday()).setScale(1, 4).floatValue());
					w1.setSaturday(new BigDecimal(week.getSaturday()/week.getWednesday()).setScale(1, 4).floatValue());
					w1.setSunday(new BigDecimal(week.getSunday()/week.getWednesday()).setScale(1, 4).floatValue());
					EnterpriseWeightList.add(w1);
				 }else if(week.getThursday()<week.getMonday()&&week.getThursday()<week.getTuesday()&&week.getThursday()<week.getWednesday()&&week.getThursday()<week.getFriday()&&week.getThursday()<week.getSaturday()&&week.getThursday()<week.getSunday()){
					EnterpriseWeight w=new EnterpriseWeight();
					w.setFlag("0");
					w.setCountry("totalAvg");
					w.setThursday(1.00f);
					w.setMonday(new BigDecimal(week.getMonday()/week.getThursday()).setScale(2, 4).floatValue());
					w.setTuesday(new BigDecimal(week.getTuesday()/week.getThursday()).setScale(2, 4).floatValue());
					w.setWednesday(new BigDecimal(week.getWednesday()/week.getThursday()).setScale(2, 4).floatValue());
					w.setFriday(new BigDecimal(week.getFriday()/week.getThursday()).setScale(2, 4).floatValue());
					w.setSaturday(new BigDecimal(week.getSaturday()/week.getThursday()).setScale(2, 4).floatValue());
					w.setSunday(new BigDecimal(week.getSunday()/week.getThursday()).setScale(2, 4).floatValue());
					EnterpriseWeightList.add(w);
					EnterpriseWeight w1=new EnterpriseWeight();
					w1.setFlag("1");
					w1.setCountry("totalAvg");
					w1.setThursday(1.0f);
					w1.setMonday(new BigDecimal(week.getMonday()/week.getThursday()).setScale(1, 4).floatValue());
					w1.setTuesday(new BigDecimal(week.getTuesday()/week.getThursday()).setScale(1, 4).floatValue());
					w1.setWednesday(new BigDecimal(week.getWednesday()/week.getThursday()).setScale(1, 4).floatValue());
					w1.setFriday(new BigDecimal(week.getFriday()/week.getThursday()).setScale(1, 4).floatValue());
					w1.setSaturday(new BigDecimal(week.getSaturday()/week.getThursday()).setScale(1, 4).floatValue());
					w1.setSunday(new BigDecimal(week.getSunday()/week.getThursday()).setScale(1, 4).floatValue());
					EnterpriseWeightList.add(w1);
				}else if(week.getFriday()<week.getMonday()&&week.getFriday()<week.getTuesday()&&week.getFriday()<week.getWednesday()&&week.getFriday()<week.getThursday()&&week.getFriday()<week.getSaturday()&&week.getFriday()<week.getSunday()){
					EnterpriseWeight w=new EnterpriseWeight();
					w.setFlag("0");
					w.setFriday(1.00f);
					w.setCountry("totalAvg");
					w.setMonday(new BigDecimal(week.getMonday()/week.getFriday()).setScale(2, 4).floatValue());
					w.setTuesday(new BigDecimal(week.getTuesday()/week.getFriday()).setScale(2, 4).floatValue());
					w.setWednesday(new BigDecimal(week.getWednesday()/week.getFriday()).setScale(2, 4).floatValue());
					w.setThursday(new BigDecimal(week.getThursday()/week.getFriday()).setScale(2, 4).floatValue());
					w.setSaturday(new BigDecimal(week.getSaturday()/week.getFriday()).setScale(2, 4).floatValue());
					w.setSunday(new BigDecimal(week.getSunday()/week.getFriday()).setScale(2, 4).floatValue());
					EnterpriseWeightList.add(w);
					EnterpriseWeight w1=new EnterpriseWeight();
					w1.setFlag("1");
					w1.setCountry("totalAvg");
					w1.setFriday(1.0f);
					w1.setMonday(new BigDecimal(week.getMonday()/week.getFriday()).setScale(1, 4).floatValue());
					w1.setTuesday(new BigDecimal(week.getTuesday()/week.getFriday()).setScale(1, 4).floatValue());
					w1.setWednesday(new BigDecimal(week.getWednesday()/week.getFriday()).setScale(1, 4).floatValue());
					w1.setThursday(new BigDecimal(week.getThursday()/week.getFriday()).setScale(1, 4).floatValue());
					w1.setSaturday(new BigDecimal(week.getSaturday()/week.getFriday()).setScale(1, 4).floatValue());
					w1.setSunday(new BigDecimal(week.getSunday()/week.getFriday()).setScale(1, 4).floatValue());
					EnterpriseWeightList.add(w1);
				}else if(week.getSaturday()<week.getMonday()&&week.getSaturday()<week.getTuesday()&&week.getSaturday()<week.getWednesday()&&week.getSaturday()<week.getThursday()&&week.getSaturday()<week.getFriday()&&week.getSaturday()<week.getSunday()){
					EnterpriseWeight w=new EnterpriseWeight();
					w.setFlag("0");
					w.setSaturday(1.00f);
					w.setCountry("totalAvg");
					w.setMonday(new BigDecimal(week.getMonday()/week.getSaturday()).setScale(2, 4).floatValue());
					w.setTuesday(new BigDecimal(week.getTuesday()/week.getSaturday()).setScale(2, 4).floatValue());
					w.setWednesday(new BigDecimal(week.getWednesday()/week.getSaturday()).setScale(2, 4).floatValue());
					w.setThursday(new BigDecimal(week.getThursday()/week.getSaturday()).setScale(2, 4).floatValue());
					w.setFriday(new BigDecimal(week.getFriday()/week.getSaturday()).setScale(2, 4).floatValue());
					w.setSunday(new BigDecimal(week.getSunday()/week.getSaturday()).setScale(2, 4).floatValue());
					EnterpriseWeightList.add(w);
					EnterpriseWeight w1=new EnterpriseWeight();
					w1.setFlag("1");
					w1.setCountry("totalAvg");
					w1.setSaturday(1.0f);
					w1.setMonday(new BigDecimal(week.getMonday()/week.getSaturday()).setScale(1, 4).floatValue());
					w1.setTuesday(new BigDecimal(week.getTuesday()/week.getSaturday()).setScale(1, 4).floatValue());
					w1.setWednesday(new BigDecimal(week.getWednesday()/week.getSaturday()).setScale(1, 4).floatValue());
					w1.setThursday(new BigDecimal(week.getThursday()/week.getSaturday()).setScale(1, 4).floatValue());
					w1.setFriday(new BigDecimal(week.getFriday()/week.getSaturday()).setScale(1, 4).floatValue());
					w1.setSunday(new BigDecimal(week.getSunday()/week.getSaturday()).setScale(1, 4).floatValue());
					EnterpriseWeightList.add(w1);
				}else if(week.getSunday()<week.getMonday()&&week.getSunday()<week.getTuesday()&&week.getSunday()<week.getWednesday()&&week.getSunday()<week.getThursday()&&week.getSunday()<week.getFriday()&&week.getSunday()<week.getSaturday()){
					EnterpriseWeight w=new EnterpriseWeight();
					w.setFlag("0");
					w.setSunday(1.00f);
					w.setCountry("totalAvg");
					w.setMonday(new BigDecimal(week.getMonday()/week.getSunday()).setScale(2, 4).floatValue());
					w.setTuesday(new BigDecimal(week.getTuesday()/week.getSunday()).setScale(2, 4).floatValue());
					w.setWednesday(new BigDecimal(week.getWednesday()/week.getSunday()).setScale(2, 4).floatValue());
					w.setThursday(new BigDecimal(week.getThursday()/week.getSunday()).setScale(2, 4).floatValue());
					w.setFriday(new BigDecimal(week.getFriday()/week.getSunday()).setScale(2, 4).floatValue());
					w.setSaturday(new BigDecimal(week.getSaturday()/week.getSunday()).setScale(2, 4).floatValue());
					EnterpriseWeightList.add(w);
					EnterpriseWeight w1=new EnterpriseWeight();
					w1.setFlag("1");
					w1.setSunday(1.0f);
					w1.setCountry("totalAvg");
					w1.setMonday(new BigDecimal(week.getMonday()/week.getSunday()).setScale(1, 4).floatValue());
					w1.setTuesday(new BigDecimal(week.getTuesday()/week.getSunday()).setScale(1, 4).floatValue());
					w1.setWednesday(new BigDecimal(week.getWednesday()/week.getSunday()).setScale(1, 4).floatValue());
					w1.setThursday(new BigDecimal(week.getThursday()/week.getSunday()).setScale(1, 4).floatValue());
					w1.setFriday(new BigDecimal(week.getFriday()/week.getSunday()).setScale(1, 4).floatValue());
					w1.setSaturday(new BigDecimal(week.getSaturday()/week.getSunday()).setScale(1, 4).floatValue());
					EnterpriseWeightList.add(w1);
				}
			}
		}
		if(EnterpriseWeightList.size()>0){
			EnterpriseWeight t=EnterpriseWeightList.get(1);
			float real=t.getMonday()+t.getTuesday()+t.getWednesday()+t.getThursday()+t.getFriday()+t.getSaturday()+t.getSunday();
			for (Map.Entry<String,EnterpriseWeek> entry : map.entrySet()) {  
			    String country =entry.getKey();
				if(!"totalAvg".equals(country)){
					EnterpriseWeight w=new EnterpriseWeight();
					EnterpriseWeek week=entry.getValue();
					float totalWeek=week.getMonday()+week.getTuesday()+week.getWednesday()+week.getThursday()+week.getFriday()+week.getSaturday()+week.getSunday();
					w.setCountry(country);
					w.setFlag("0");
					w.setMonday(new BigDecimal(week.getMonday()/totalWeek*real).setScale(2, 4).floatValue());
					w.setTuesday(new BigDecimal(week.getTuesday()/totalWeek*real).setScale(2, 4).floatValue());
					w.setWednesday(new BigDecimal(week.getWednesday()/totalWeek*real).setScale(2, 4).floatValue());
					w.setThursday(new BigDecimal(week.getThursday()/totalWeek*real).setScale(2, 4).floatValue());
					w.setFriday(new BigDecimal(week.getFriday()/totalWeek*real).setScale(2, 4).floatValue());
					w.setSaturday(new BigDecimal(week.getSaturday()/totalWeek*real).setScale(2, 4).floatValue());
					w.setSunday(new BigDecimal(week.getSunday()/totalWeek*real).setScale(2, 4).floatValue());
					EnterpriseWeightList.add(w);
					EnterpriseWeight w1=new EnterpriseWeight();
					w1.setCountry(country);
					w1.setFlag("1");
					w1.setMonday(new BigDecimal(week.getMonday()/totalWeek*real).setScale(1, 4).floatValue());
					w1.setTuesday(new BigDecimal(week.getTuesday()/totalWeek*real).setScale(1, 4).floatValue());
					w1.setWednesday(new BigDecimal(week.getWednesday()/totalWeek*real).setScale(1, 4).floatValue());
					w1.setThursday(new BigDecimal(week.getThursday()/totalWeek*real).setScale(1, 4).floatValue());
					w1.setFriday(new BigDecimal(week.getFriday()/totalWeek*real).setScale(1, 4).floatValue());
					w1.setSaturday(new BigDecimal(week.getSaturday()/totalWeek*real).setScale(1, 4).floatValue());
					w1.setSunday(new BigDecimal(week.getSunday()/totalWeek*real).setScale(1, 4).floatValue());
					float child=w1.getMonday()+w1.getTuesday()+w1.getWednesday()+w1.getThursday()+w1.getFriday()+w1.getSaturday()+w1.getSunday();
					if(Math.abs(child-real) < 0.0000001){
						EnterpriseWeightList.add(w1);
					}else{
						if(child<real){
							float min=1;
							float minValue=w.getMonday();
							if(w.getTuesday()<=minValue){min=2;minValue=w.getTuesday();}
							if(w.getWednesday()<=minValue){min=3;;minValue=w.getWednesday();}
							if(w.getThursday()<=minValue){min=4;;minValue=w.getThursday();}
							if(w.getFriday()<=minValue){min=5;;minValue=w.getFriday();}
							if(w.getSaturday()<=minValue){min=6;;minValue=w.getSaturday();}
							if(w.getSunday()<=minValue){min=7;;minValue=w.getSunday();}
							if(min==1){
								w1.setMonday(real-(w1.getTuesday()+w1.getWednesday()+w1.getThursday()+w1.getFriday()+w1.getSaturday()+w1.getSunday()));
							}else if(min==2){
								w1.setTuesday(real-(w1.getMonday()+w1.getWednesday()+w1.getThursday()+w1.getFriday()+w1.getSaturday()+w1.getSunday()));
							}else if(min==3){
								w1.setWednesday(real-(w1.getMonday()+w1.getTuesday()+w1.getThursday()+w1.getFriday()+w1.getSaturday()+w1.getSunday()));
							}else if(min==4){
								w1.setThursday(real-(w1.getMonday()+w1.getWednesday()+w1.getTuesday()+w1.getFriday()+w1.getSaturday()+w1.getSunday()));
							}else if(min==5){
								w1.setFriday(real-(w1.getMonday()+w1.getWednesday()+w1.getThursday()+w1.getTuesday()+w1.getSaturday()+w1.getSunday()));
							}else if(min==6){
								w1.setSaturday(real-(w1.getMonday()+w1.getWednesday()+w1.getThursday()+w1.getFriday()+w1.getTuesday()+w1.getSunday()));
							}else if(min==7){
								w1.setSunday(real-(w1.getMonday()+w1.getWednesday()+w1.getThursday()+w1.getFriday()+w1.getSaturday()+w1.getTuesday()));
							}
						}else{
							float min=1;
							float minValue=w.getMonday();
							if(w.getTuesday().toString().length()>=4&&Float.parseFloat(w.getTuesday().toString().substring(3))>=5){
								if(w.getTuesday()<=minValue){
									min=2;
									minValue=w.getTuesday();}
							}
							if(w.getWednesday().toString().length()>=4&&Float.parseFloat(w.getWednesday().toString().substring(3))>=5){
								if(w.getWednesday()<=minValue){
									min=3;
									minValue=w.getWednesday();}
							}
							if(w.getThursday().toString().length()>=4&&Float.parseFloat(w.getThursday().toString().substring(3))>=5){
								if(w.getThursday()<=minValue){min=4;
								minValue=w.getThursday();}}
							if(w.getFriday().toString().length()>=4&&Float.parseFloat(w.getFriday().toString().substring(3))>=5){
								if(w.getFriday()<=minValue){
									min=5;
								    minValue=w.getFriday();}}
							if(w.getSaturday().toString().length()>=4&&Float.parseFloat(w.getSaturday().toString().substring(3))>=5){
								if(w.getSaturday()<=minValue){
									min=6;
									minValue=w.getSaturday();}}
							if(w.getSunday().toString().length()>=4&&Float.parseFloat(w.getSunday().toString().substring(3))>=5){
								if(w.getSunday()<=minValue){
									min=7;
									minValue=w.getSunday();}}
							if(min==1){
								w1.setMonday(real-(w1.getTuesday()+w1.getWednesday()+w1.getThursday()+w1.getFriday()+w1.getSaturday()+w1.getSunday()));
							}else if(min==2){
								w1.setTuesday(real-(w1.getMonday()+w1.getWednesday()+w1.getThursday()+w1.getFriday()+w1.getSaturday()+w1.getSunday()));
							}else if(min==3){
								w1.setWednesday(real-(w1.getMonday()+w1.getTuesday()+w1.getThursday()+w1.getFriday()+w1.getSaturday()+w1.getSunday()));
							}else if(min==4){
								w1.setThursday(real-(w1.getMonday()+w1.getWednesday()+w1.getTuesday()+w1.getFriday()+w1.getSaturday()+w1.getSunday()));
							}else if(min==5){
								w1.setFriday(real-(w1.getMonday()+w1.getWednesday()+w1.getThursday()+w1.getTuesday()+w1.getSaturday()+w1.getSunday()));
							}else if(min==6){
								w1.setSaturday(real-(w1.getMonday()+w1.getWednesday()+w1.getThursday()+w1.getFriday()+w1.getTuesday()+w1.getSunday()));
							}else if(min==7){
								w1.setSunday(real-(w1.getMonday()+w1.getWednesday()+w1.getThursday()+w1.getFriday()+w1.getSaturday()+w1.getTuesday()));
							}
						}
						EnterpriseWeightList.add(w1);
					}
				}
				
			}
		}
		return EnterpriseWeightList;
	}
}
