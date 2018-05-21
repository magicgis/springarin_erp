package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.SessionMonitorDao;
import com.springrain.erp.modules.amazoninfo.entity.BusinessReport;
import com.springrain.erp.modules.amazoninfo.entity.SessionMonitor;
import com.springrain.erp.modules.amazoninfo.entity.SessionMonitorResultDto;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * session和转化率监控Service
 * @author Tim
 * @version 2015-02-09
 */
@Component
@Transactional(readOnly = true)
public class SessionMonitorService extends BaseService {

	@Autowired
	private SessionMonitorDao sessionMonitorDao;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Transactional(readOnly = false)
	public SessionMonitor get(Integer id) {
		return sessionMonitorDao.get(id);
	}
	
	public List<SessionMonitor> find(SessionMonitor sessionMonitor) {
		DetachedCriteria dc = sessionMonitorDao.createDetachedCriteria();
		
		if(StringUtils.isEmpty(sessionMonitor.getCountry())){
			sessionMonitor.setCountry("de");
		}
		if (null==sessionMonitor.getMonth()){
			Date today = new Date();
			if("de,it,es,fr,uk,jp".contains(sessionMonitor.getCountry())){
				today = DateUtils.addDays(today, -2);
			}else{
				today = DateUtils.addDays(today, -3);
			}
			today.setDate(1);
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			sessionMonitor.setMonth(today);
		}
		dc.add(Restrictions.eq("month",sessionMonitor.getMonth()));
		if(!"total".equals(sessionMonitor.getCountry())){
			dc.add(Restrictions.eq("country",sessionMonitor.getCountry()));
		}
		if(StringUtils.isNotEmpty(sessionMonitor.getProductName())){
			dc.add(Restrictions.like("productName","%"+sessionMonitor.getProductName()+"%"));
		}
		return sessionMonitorDao.find(dc);
	}
	
	public List<SessionMonitor> findNotHasNullData(SessionMonitor sessionMonitor) {
		DetachedCriteria dc = sessionMonitorDao.createDetachedCriteria();
		
		if(StringUtils.isEmpty(sessionMonitor.getCountry())){
			sessionMonitor.setCountry("de");
		}
		if (null==sessionMonitor.getMonth()){
			Date today = new Date();
			if("de,it,es,fr,uk,jp".contains(sessionMonitor.getCountry())){
				today = DateUtils.addDays(today, -2);
			}else{
				today = DateUtils.addDays(today, -3);
			}
			today.setDate(1);
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			sessionMonitor.setMonth(today);
		}
		dc.add(Restrictions.eq("month",sessionMonitor.getMonth()));
		dc.add(Restrictions.or(Restrictions.isNotNull("sessions"),Restrictions.isNotNull("conver")));
		if(!"total".equals(sessionMonitor.getCountry())){
			dc.add(Restrictions.eq("country",sessionMonitor.getCountry()));
		}
		if(StringUtils.isNotEmpty(sessionMonitor.getProductName())){
			dc.add(Restrictions.like("productName","%"+sessionMonitor.getProductName()+"%"));
		}
		return sessionMonitorDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void synSessions(SessionMonitor sessionMonitor){
		sessionMonitor.setProductName(null);
		Date date = DateUtils.addMonths(sessionMonitor.getMonth(),-1);
		Date month = sessionMonitor.getMonth();
		sessionMonitor.setMonth(date);
		List<SessionMonitor> list = findNotHasNullData(sessionMonitor);
		sessionMonitor.setMonth(month);
		if(list.size()>0){
			String sql = "delete from amazoninfo_session_monitor where  month =:p1 and country=:p2";
			sessionMonitorDao.createSqlQuery(sql, new Parameter(month,sessionMonitor.getCountry())).executeUpdate();
			List<SessionMonitor> list2 = Lists.newArrayList();
			for (SessionMonitor sessionMonitor2 : list) {
				SessionMonitor temp = new SessionMonitor(sessionMonitor2);
				temp.setMonth(month);
				temp.setCreateDate(new Date());
				temp.setLastUpdateDate(new Date());
				temp.setCreateUser(UserUtils.getUser());
				temp.setLastUpdateUser(UserUtils.getUser());
				list2.add(temp);
			}
			sessionMonitorDao.save(list2);
		}
	}
	
	
	@Transactional(readOnly = false)
	public void save(List<SessionMonitor> sessionMonitors) {
		sessionMonitorDao.save(sessionMonitors);
	}
	
	@Transactional(readOnly = false)
	public void save(SessionMonitor sessionMonitor) {
		sessionMonitorDao.save(sessionMonitor);
	}
	
	public List<SessionMonitorResultDto> findSessionAsin(SessionMonitor sessionMonitor,Set<String> asins){
		Date firstMonth =new Date(sessionMonitor.getMonth().getYear(), sessionMonitor.getMonth().getMonth(),1,0,0);
		String filter = "";
		if(StringUtils.isNotEmpty(sessionMonitor.getProductName())){
			filter = "and b.`product_name` like :p3";
		}
		String sql = "SELECT CASE  WHEN LENGTH(b.`color`)>0  THEN CONCAT(b.`product_name`,'_',b.`color`) ELSE b.`product_name` END AS productName,b.`asin`,a.`conver`,a.`sessions`,a.`sessions_by_date` FROM amazoninfo_session_monitor a,psi_sku b " +
				"WHERE a.`color` = b.`color` AND (a.`conver` is not null  OR  a.`sessions` is not null)  AND a.`product_id` = b.`product_id` AND a.`country` = b.`country`  AND  a.`country` = :p1 AND a.`month`=:p2 and b.del_flag='0' and b.`asin` is not null  "+filter+"  GROUP BY b.`asin`";
		List<Object> list = null;
		if(filter.length()>0){
			list = sessionMonitorDao.findBySql(sql, new Parameter(sessionMonitor.getCountry(),firstMonth,"%"+sessionMonitor.getProductName()+"%"));
		}else{
			list = sessionMonitorDao.findBySql(sql, new Parameter(sessionMonitor.getCountry(),firstMonth));
		}	
		Map<String, SessionMonitorResultDto> result = Maps.newHashMap();
		if(asins==null){
			asins = Sets.newHashSet();
		}
		for (Object object : list) {
			Object[] objs = (Object[])object;
			String productName = objs[0].toString();
			String asin = objs[1].toString();
			asins.add(asin);
			Integer sessions = null;
			try {
				if(objs[3]!=null){
					sessions = Integer.parseInt(objs[3].toString());
				}
			} catch (Exception e) {}
			
			Integer sessionsByDay = null;
			try {
				if(objs[4]!=null){
					sessionsByDay = Integer.parseInt(objs[4].toString());
				}
			} catch (Exception e) {}
			
			Float conver = null;
			try {
				if(objs[2]!=null){
					conver = Float.parseFloat(objs[2].toString());
				}
			} catch (Exception e) {}
			SessionMonitorResultDto dto = result.get(productName);
			if(dto==null){
				if("0".equals(sessionMonitor.getSearchFlag())){
					dto = new SessionMonitorResultDto(sessionMonitor.getCountry(),productName,sessions,sessionsByDay, conver);
				}else{
					dto = new SessionMonitorResultDto(sessionMonitor.getCountry(),productName,sessions,null, conver);
				}
				dto.setSearchFlag(sessionMonitor.getSearchFlag());
				dto.setMonth(sessionMonitor.getMonth());
				result.put(productName, dto);
			}
			dto.getAsins().put(asin,null);
		}
		return Lists.newArrayList(result.values());
	}
	
	public Map<String,List<String>> findProductAsins(SessionMonitor sessionMonitor) {
		String sql = "SELECT CASE  WHEN LENGTH(b.`color`)>0  THEN CONCAT(b.`product_name`,'_',b.`color`) ELSE b.`product_name` END AS productName,b.`asin` FROM psi_sku b WHERE  b.`color`=:p1 AND b.`product_id`=:p2 AND  b.`country` =:p3";
		List<Object> list = sessionMonitorDao.findBySql(sql, new Parameter(sessionMonitor.getColor()==null?"":sessionMonitor.getColor(),sessionMonitor.getProductId(),sessionMonitor.getCountry()));
		Map<String,List<String>> rs = Maps.newHashMap();
		for (Object object : list) {
			Object[] objs = (Object[])object; 
			String productName = objs[0].toString();
			List<String> asins = rs.get(productName);
			if(asins==null){
				asins = Lists.newArrayList();
				rs.put(productName, asins);
			}
			asins.add(objs[1].toString());
		}
		return rs;
	}
	
	private static DateFormat format = new SimpleDateFormat("yyyy-MM");
	
	public Map<String,SessionMonitor> getProduct(BusinessReport businessReport){
		String sql = "SELECT b.`month`,b.`sessions_by_date`,b.`conver`FROM psi_sku a,amazoninfo_session_monitor b WHERE a.`color` = b.`color` AND a.`country` = b.`country` AND a.`product_id` = b.`product_id`  AND a.`asin` =:p1 AND a.`country`=:p2 AND b.`month`>=:p3 AND b.`month`<=:p4";
		List<Object> list = sessionMonitorDao.findBySql(sql, new Parameter(businessReport.getChildAsin(),businessReport.getCountry(),businessReport.getCreateDate(),businessReport.getDataDate()));
		Map<String,SessionMonitor> result = Maps.newHashMap();
		for (Object object : list) {
			Object[] objs =(Object[])object;
			BigDecimal decimal = (BigDecimal)objs[2];
			Float conver = decimal==null?null:decimal.floatValue();
			result.put(format.format((Date)objs[0]),new SessionMonitor(null,(Integer)objs[1],conver));
		}
		return result;
	}
	
	public List<SessionMonitorResultDto> getData(SessionMonitor sessionMonitor){
		if(StringUtils.isEmpty(sessionMonitor.getCountry())){
			sessionMonitor.setCountry("de");
		}
		if(sessionMonitor.getMonth()==null){
			Date today = new Date();
			if("de,it,es,fr,uk,jp".contains(sessionMonitor.getCountry())){
				today = DateUtils.addDays(today, -2);
			}else{
				today = DateUtils.addDays(today, -3);
			}
			sessionMonitor.setMonth(new Date(today.getYear(), today.getMonth(),today.getDate(),0,0));
		}
		Date monthFirst,p2,p3;
		monthFirst = new Date(sessionMonitor.getMonth().getYear(),sessionMonitor.getMonth().getMonth(),1,0,0);
		if("2".equals(sessionMonitor.getSearchFlag())){
			p2 = monthFirst;
			p3 =  new Date(sessionMonitor.getMonth().getYear(),sessionMonitor.getMonth().getMonth(),getMonthMaxDays(monthFirst),0,0);
		}else{
			p2=p3=sessionMonitor.getMonth();
		}
		//sessionMonitor.setLastUpdateDate(sessionMonitor.getMonth());
		//sessionMonitor.setMonth(month);
		Set<String> asins = Sets.newHashSet();
		List<SessionMonitorResultDto> products = findSessionAsin(sessionMonitor,asins);
		String sql = "SELECT this_.child_asin AS y1_,SUM(orders_placed) AS orders_placed,SUM(this_.sessions) AS y4_ FROM amazoninfo_business_report this_ WHERE this_.country=:p1 AND this_.data_date>=:p2 AND this_.data_date<=:p3 AND this_.del_flag='0' AND this_.child_asin in :p4 GROUP BY this_.child_asin ORDER BY y4_";
		if(products.size()>0){
			List<Object> data = sessionMonitorDao.findBySql(sql, new Parameter(sessionMonitor.getCountry(),p2,p3,asins));
			Map<String,SessionMonitor> rs = Maps.newHashMap();
			for (Object object : data) {
				Object[] objs = (Object[])object; 
				Integer sessions = null;
				try {
					if(objs[2]!=null){
						sessions = Integer.parseInt(objs[2].toString());
					}
				} catch (Exception e) {}
				
				Integer orders = null;
				try {
					if(objs[1]!=null){
						orders = Integer.parseInt(objs[1].toString());
					}
				} catch (Exception e) {}
				rs.put(objs[0].toString(),new SessionMonitor(sessions,orders));
			}
			//获取目标对应的产品
			Map<String,Float> prices = amazonProduct2Service.getPriceByAsin(asins, sessionMonitor.getCountry());
			for (SessionMonitorResultDto product : products) {
				Map<String, SessionMonitor> productMap = product.getAsins();
				Float price = null;
			    for (Map.Entry<String,SessionMonitor> entryRs : productMap.entrySet()) { 
				    String asin=entryRs.getKey();
				    SessionMonitor sessionValue=entryRs.getValue();
					if(sessionValue!=null){
						productMap.put(asin, sessionValue);
					}else{
						productMap.put(asin, new SessionMonitor(0,0));
					}
					Float temp = prices.get(asin);
					if(temp!=null){
						if(price==null){
							price = temp;
						}else{
							price = price>temp?temp:price;
						}
					}
				}
				product.setPrice(price);
			}
		}
		//sessionMonitor.setMonth(sessionMonitor.getLastUpdateDate());
		if("0".equals(sessionMonitor.getSearchFlag())){
			Map<String, SessionMonitor> map = Maps.newHashMap();
			for (SessionMonitorResultDto dto : products) {
				map.putAll(dto.getAsins());
			}
			//校准每天目标值
			if(products.size()>0){
				List<Object> data = sessionMonitorDao.findBySql(sql, new Parameter(sessionMonitor.getCountry(),monthFirst,p3,asins));
				for (Object object : data) {
					Object[] objs = (Object[])object; 
					String asin = objs[0].toString();
					Integer sessions = 0;
					try {
						if(objs[2]!=null){
							sessions = Integer.parseInt(objs[2].toString());
						}
					} catch (Exception e) {}
					if(map.get(asin)!=null){
						//将已经发生的值记录在产品id里
						map.get(asin).setProductId(sessions);
					}
				}
			}
		}
		return products;
	}
	
	private int getMonthMaxDays(Date date){
		Calendar   calendar   =   Calendar.getInstance();   
	    calendar.set(1900+date.getYear(),date.getMonth(),1);   
	    calendar.roll(Calendar.DATE,false);   
	    return calendar.get(Calendar.DATE); 
	}
}
