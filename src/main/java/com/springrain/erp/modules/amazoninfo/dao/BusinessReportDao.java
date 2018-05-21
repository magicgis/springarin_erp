package com.springrain.erp.modules.amazoninfo.dao;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.type.FloatType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.BusinessReport;

/**
 * 亚马逊商业报表DAO接口
 * @author tim
 * @version 2014-05-28
 */
@Repository
public class BusinessReportDao extends BaseDao<BusinessReport> {
		
	@SuppressWarnings("unchecked")
	public Page<BusinessReport> findExt(Page<BusinessReport> page, DetachedCriteria detachedCriteria) {
		// get count
		if (!page.isDisabled() && !page.isNotCount()){
			page.setCount(countExt(detachedCriteria));
			if (page.getCount() < 1) {
				return page;
			}
		}
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());		
		
		ProjectionList projection = Projections.projectionList().add(Projections.sum("sessions").as("sessions")).add(Projections.sum("pageViews").as("pageViews"));
		criteria.setProjection(projection);
		List<Object[]> list = criteria.list();
		Object[] obj = list.get(0);
		float sessionsSum = Float.parseFloat(obj[0].toString());
		float pageViewsSum = Float.parseFloat(obj[1].toString());
		
		// set page
		if (!page.isDisabled()){
	        criteria.setFirstResult(page.getFirstResult());
	        criteria.setMaxResults(page.getMaxResults()); 
		}
		// order by
		if (StringUtils.isNotBlank(page.getOrderBy())){
			for (String order : StringUtils.split(page.getOrderBy(), ",")){
				String[] o = StringUtils.split(order, " ");
				if (o.length==1){
					if(o[0].equals("sessionPercentage")||o[0].equals("pageViewsPercentage")||o[0].equals("unitSessionPercentage")||o[0].equals("conversion")){
						criteria.addOrder(new MyOrder(o[0],true));
					}else{
						criteria.addOrder(Order.asc(o[0]));
					}
				}else if (o.length==2){
					if(o[0].equals("sessionPercentage")||o[0].equals("pageViewsPercentage")||o[0].equals("unitSessionPercentage")||o[0].equals("conversion")){
						if ("DESC".equals(o[1].toUpperCase())){
							criteria.addOrder(new MyOrder(o[0],false));
						}else{
							criteria.addOrder(new MyOrder(o[0],true));
						}
					}else{
						if ("DESC".equals(o[1].toUpperCase())){
							criteria.addOrder(Order.desc(o[0]));
						}else{
							criteria.addOrder(Order.asc(o[0]));
						}
					}
				}
			}
		}
		projection = Projections.projectionList().add(Projections.property("pAsin")).add(Projections.property("childAsin"))
				.add(Projections.property("country")).add(Projections.property("title")).add(Projections.sum("sessions").as("sessions"))
				.add(Projections.sum("pageViews").as("pageViews")).add(Projections.sum("unitsOrdered").as("unitsOrdered")).add(Projections.sum("grossProductSales").as("grossProductSales"))
				.add(Projections.sum("ordersPlaced").as("ordersPlaced")).add(Projections.avg("buyBoxPercentage").as("buyBoxPercentage"))
				.add(Projections.sqlProjection("sum(sessions)/"+sessionsSum+
				" as sessionPercentage , sum(page_views)/"+pageViewsSum+" as pageViewsPercentage, sum(units_ordered)/sum(sessions) as unitSessionPercentage, sum(orders_placed)/sum(sessions) as conversion",
				new String[]{"sessionPercentage","pageViewsPercentage","unitSessionPercentage","conversion"}, new Type[]{FloatType.INSTANCE,FloatType.INSTANCE,FloatType.INSTANCE,FloatType.INSTANCE}))
				.add(Projections.groupProperty("childAsin"));
		criteria.setProjection(projection);
		list = criteria.list();
		List<BusinessReport> listdata = Lists.newArrayList();
		for (Object[] objs : list) {
			if(objs[10]==null){
				objs[10] = "0";
			}
			if(objs[11]==null){
				objs[11] = "0";
			}
			listdata.add(new BusinessReport(objs[2].toString(), null, null, objs[0].toString(), objs[1].toString(), objs[3].toString(),
					Integer.parseInt(objs[4].toString()), 
					Float.parseFloat(objs[10].toString())*100f,
					Integer.parseInt(objs[5].toString()), 
					Float.parseFloat(objs[11].toString())*100f,
					Math.round(Float.parseFloat(objs[9].toString())), 
					Integer.parseInt(objs[6].toString()), 
					Float.parseFloat(objs[12]==null?"0":objs[12].toString())*100f,
					Float.parseFloat(objs[7]==null?"0":objs[7].toString()),
					Integer.parseInt(objs[8]==null?"0":objs[8].toString()), 
					Float.parseFloat(objs[13]==null?"0":objs[13].toString())*100f,null,null,null,null,null));
		}
		page.setList(listdata);
		return page;
	}
	
	/**
	 * 使用检索标准对象查询记录数
	 * @param detachedCriteria
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public long countExt(DetachedCriteria detachedCriteria) {
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		long totalCount = 0;
		try {
			// Get orders
			Field field = CriteriaImpl.class.getDeclaredField("orderEntries");
			field.setAccessible(true);
			List orderEntrys = (List)field.get(criteria);
			// Remove orders
			field.set(criteria, new ArrayList());
			// Get count
			criteria.setProjection(Projections.countDistinct("childAsin"));
			totalCount = Long.valueOf(criteria.uniqueResult().toString());
			// Clean count
			criteria.setProjection(null);
			// Restore orders
			field.set(criteria, orderEntrys);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return totalCount;
	}
	
	public List<BusinessReport> findCountData(String productName,Date startDate,Date endDate){
		DetachedCriteria detachedCriteria = createDetachedCriteria(Restrictions.eq("delFlag", "0"));
		detachedCriteria.add(Restrictions.between("dataDate", startDate,endDate));
		List<Object> temp = this.findBySql("SELECT DISTINCT CONCAT(country,ASIN) FROM psi_sku a WHERE del_flag='0' AND a.`asin` IS NOT NULL AND   CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END)=:p1", new Parameter(productName));
		String params = "";
		StringBuffer buf= new StringBuffer();
		for (Object object : temp) {
			buf.append("'"+object.toString()+"',");
		}
		if(StringUtils.isNotBlank(buf.toString())){
			params=buf.toString();
		}
		List<BusinessReport> rs = Lists.newArrayList();
		if(params.length()>0){
			detachedCriteria.add(Restrictions.sqlRestriction("Concat(country,child_asin) in ("+params.substring(0,params.length()-1)+")"));
					detachedCriteria.addOrder(Order.asc("dataDate"));
					detachedCriteria.addOrder(Order.asc("country"));
					Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());		
					ProjectionList projection = Projections.projectionList().add(Projections.property("sessions"))
							.add(Projections.property("conversion")).add(Projections.property("country")).add(Projections.property("dataDate"));
					criteria.setProjection(projection);
					List<Object[]> list = criteria.list();
					
					for (Object[] obj : list) {
						BusinessReport br = new BusinessReport();
						br.setSessions((Integer)obj[0]);
						br.setConversion((Float)obj[1]);
						br.setCountry(obj[2].toString());
						br.setDataDate((Date)obj[3]);
						rs.add(br);
					}
		}
		return rs;
	}
	
	public Map<String,Map<String,BusinessReport>> findCountProductsData(List<String> asins ,String country,Date startDate,Date endDate){
		DetachedCriteria detachedCriteria = createDetachedCriteria(Restrictions.eq("delFlag", "0"));
		detachedCriteria.add(Restrictions.and(Restrictions.between("dataDate", startDate,endDate),Restrictions.eq("country", country),Restrictions.in("childAsin", asins)));
		detachedCriteria.addOrder(Order.asc("dataDate"));
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());		
		ProjectionList projection = Projections.projectionList().add(Projections.property("sessions"))
							.add(Projections.property("ordersPlaced")).add(Projections.property("childAsin")).add(Projections.property("dataDate"));
		criteria.setProjection(projection);
		List<Object[]> list = criteria.list();
		Map<String,Map<String,BusinessReport>> rs = Maps.newHashMap();
		for (Object[] obj : list) {
			BusinessReport br = new BusinessReport();
			br.setSessions((Integer)obj[0]);
			br.setOrdersPlaced((Integer)obj[1]);
			br.setDataDate((Date)obj[3]);
			String asin = obj[2].toString();
			br.setChildAsin(asin);
			String date = DateUtils.getDate(br.getDataDate(), "yyyy-MM-dd");
			Map<String,BusinessReport> data = rs.get(date);
			if(data==null){
				data = Maps.newHashMap();
				rs.put(date,data);
			}
			data.put(asin,br);
		}
		return rs;
	}
	

	public Map<String,Map<String,BusinessReport>> findCountProductsData(Set<String> asins,Date startDate,Date endDate){
		DetachedCriteria detachedCriteria = createDetachedCriteria(Restrictions.eq("delFlag", "0"));
		detachedCriteria.add(Restrictions.and(Restrictions.between("dataDate", startDate,endDate),Restrictions.in("childAsin", asins)));
		detachedCriteria.addOrder(Order.asc("dataDate"));
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());		
		ProjectionList projection = Projections.projectionList().add(Projections.property("sessions"))
							.add(Projections.property("ordersPlaced")).add(Projections.property("childAsin")).add(Projections.property("dataDate")).add(Projections.property("country"));
		criteria.setProjection(projection);
		List<Object[]> list = criteria.list();
		Map<String,Map<String,BusinessReport>> rs = Maps.newHashMap();
		for (Object[] obj : list) {
			String asin = obj[2].toString();
			String date = DateUtils.getDate((Date)obj[3], "yyyyMMdd");
			String country = obj[4].toString();
			Integer sessions = (Integer)obj[0];
			Integer op = (Integer)obj[1];
			Map<String,BusinessReport> data = rs.get(country);
			if(data==null){
				data = Maps.newHashMap();
				rs.put(country,data);
			}
			BusinessReport br = data.get(date);
			if(br==null){
				br = new BusinessReport();
				br.setSessions(0);
				br.setOrdersPlaced(0);
				br.setDataDate((Date)obj[3]);
				br.setCountry(country);
				data.put(date, br);
			}
			if(sessions!=null){
				br.setSessions(sessions+br.getSessions());
			}
			if(op!=null){
				br.setOrdersPlaced(op+br.getOrdersPlaced());
			}
		}
		return rs;
	}
	
	
//	public List<BusinessReport> findCountDataByDate(String country,String searchFlag,Date startDate,Date endDate) throws ParseException{
//		DetachedCriteria detachedCriteria = createDetachedCriteria(Restrictions.eq("delFlag", "0"));
//		detachedCriteria.add(Restrictions.between("dataDate", startDate,endDate));
//		detachedCriteria.add(Restrictions.eq("country", country));
//		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());		
//		ProjectionList projection = null;
//		
//		if(searchFlag.equals("0")){
//			projection = Projections.projectionList()
//					.add(Projections.property("country"))
//					.add(Projections.sum("sessions").as("sessions"))
//					.add(Projections.sqlProjection("sum(orders_placed)/sum(sessions) as conversion ",
//					new String[]{"conversion"}, new Type[]{FloatType.INSTANCE}))
//					.add(Projections.groupProperty("dataDate"));
//		}else if(searchFlag.equals("1")){
//			projection = Projections.projectionList()
//					.add(Projections.property("country"))
//					.add(Projections.sum("sessions").as("sessions"))
//					.add(Projections.sqlProjection("sum(orders_placed)/sum(sessions) as conversion ",
//					new String[]{"conversion"}, new Type[]{FloatType.INSTANCE}))
//					.add(Projections.sqlGroupProjection("DATE_FORMAT(data_date,'%x %v') as weekGroup", "weekGroup",new String[]{"weekGroup"}, new Type[]{StringType.INSTANCE}));
//		}else if(searchFlag.equals("2")){
//			projection = Projections.projectionList()
//					.add(Projections.property("country"))
//					.add(Projections.sum("sessions").as("sessions"))
//					.add(Projections.sqlProjection("sum(orders_placed)/sum(sessions) as conversion ",
//					new String[]{"conversion"}, new Type[]{FloatType.INSTANCE}))
//					.add(Projections.sqlGroupProjection("DATE_FORMAT(data_date,'%X %m') as monthGroup", "monthGroup",new String[]{"monthGroup"}, new Type[]{StringType.INSTANCE}));
//		}
//		
//		criteria.setProjection(projection);
//		List<Object[]> list = criteria.list();
//		List<BusinessReport> rs = Lists.newArrayList();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		int year =0;
//		int week =0;
//		int month=0;
//		for (Object[] obj : list) {
//			BusinessReport br = new BusinessReport();
//		if(searchFlag.equals("0")){
//			br.setSessions(Integer.parseInt(obj[1].toString()));
//			br.setConversion((Float)obj[2]);
//			br.setCountry(obj[0].toString());
//			br.setDataDate((Date)obj[3]);	
//			
//		}else if(searchFlag.equals("1")){
//			String[] stra = obj[3].toString().split(" ");
//			year =Integer.parseInt(stra[0]);
//			week =Integer.parseInt(stra[1]);
//			
//			br.setSessions(Integer.parseInt(obj[1].toString()));
//			br.setConversion((Float)obj[2]);
//			br.setCountry(obj[0].toString());
//			br.setDataDate(sdf.parse(sdf.format(DateUtils.getLastDayOfWeek(year, week))));
//		}else if(searchFlag.equals("2")){
//			String[] stra = obj[3].toString().split(" ");
//			year =Integer.parseInt(stra[0]);
//			month =Integer.parseInt(stra[1]);
//			br.setSessions(Integer.parseInt(obj[1].toString()));
//			br.setConversion((Float)obj[2]);
//			br.setCountry(obj[0].toString());
//			br.setDataDate(sdf.parse(sdf.format(DateUtils.getFirstDayofMonth(year, month))));
//		}
//			rs.add(br);
//		}
//		return rs;
//	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public Page<BusinessReport> findExtByDay(Page<BusinessReport> page, DetachedCriteria detachedCriteria,String searchFlag) throws NumberFormatException, ParseException {
		// get count
		if (!page.isDisabled() && !page.isNotCount()){
			
			if(searchFlag.equals("0")){
				page.setCount(countExtByDay(detachedCriteria));
			}else if(searchFlag.equals("1")){
				page.setCount(countExtByWeek(detachedCriteria));
			}else if(searchFlag.equals("2")){
				page.setCount(countExtByMonth(detachedCriteria));
			}
			
			if (page.getCount() < 1) {
				return page;
			}
		}
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());	
		ProjectionList projection=null;
		List<Object[]> list=null;
//		ProjectionList projection = Projections.projectionList().add(Projections.sum("sessions").as("sessions")).add(Projections.sum("pageViews").as("pageViews"));
//		criteria.setProjection(projection);
//		List<Object[]> list = criteria.list();
//		Object[] obj = list.get(0);
//		float sessionsSum = Float.parseFloat(obj[0].toString());
//		float pageViewsSum = Float.parseFloat(obj[1].toString());
		
		// set page
		if (!page.isDisabled()){
	        criteria.setFirstResult(page.getFirstResult());
	        criteria.setMaxResults(page.getMaxResults()); 
		}
		// order by
		if (StringUtils.isNotBlank(page.getOrderBy())){
			for (String order : StringUtils.split(page.getOrderBy(), ",")){
				String[] o = StringUtils.split(order, " ");
				if (o.length==1){
					if(o[0].equals("unitSessionPercentage")||o[0].equals("conversion")||o[0].equals("weekGroup")||o[0].equals("monthGroup")){
						criteria.addOrder(new MyOrder(o[0],true));
					}else{
						criteria.addOrder(Order.asc(o[0]));
					}
				}else if (o.length==2){
					if(o[0].equals("unitSessionPercentage")||o[0].equals("conversion")||o[0].equals("weekGroup")||o[0].equals("monthGroup")){
						if ("DESC".equals(o[1].toUpperCase())){
							criteria.addOrder(new MyOrder(o[0],false));
						}else{
							criteria.addOrder(new MyOrder(o[0],true));
						}
					}else{
						if ("DESC".equals(o[1].toUpperCase())){
							criteria.addOrder(Order.desc(o[0]));
						}else{
							criteria.addOrder(Order.asc(o[0]));
						}
					}
				}
			}
		}
		if(searchFlag.equals("0")){
			projection = Projections.projectionList()
					.add(Projections.property("country"))
					.add(Projections.sum("sessions").as("sessions"))
					.add(Projections.sum("pageViews").as("pageViews"))
					.add(Projections.sum("unitsOrdered").as("unitsOrdered"))
					
					.add(Projections.sqlProjection("sum(units_ordered)/sum(sessions) as unitSessionPercentage, sum(orders_placed)/sum(sessions) as conversion ",
					new String[]{"unitSessionPercentage","conversion"}, new Type[]{FloatType.INSTANCE,FloatType.INSTANCE}))
					
					.add(Projections.sum("ordersPlaced").as("ordersPlaced"))
					.add(Projections.avg("buyBoxPercentage").as("buyBoxPercentage"))
					.add(Projections.sum("grossProductSales").as("grossProductSales"))
					.add(Projections.groupProperty("dataDate"))
					.add(Projections.sqlProjection("sum(gross_product_sales)/sum(orders_placed) as aveSalesPerItem ",new String[]{"aveSalesPerItem"}, new Type[]{FloatType.INSTANCE}));
		}else if(searchFlag.equals("1")){
			projection = Projections.projectionList()
					.add(Projections.property("country"))
					.add(Projections.sum("sessions").as("sessions"))
					.add(Projections.sum("pageViews").as("pageViews"))
					.add(Projections.sum("unitsOrdered").as("unitsOrdered"))

					.add(Projections.sqlProjection("sum(units_ordered)/sum(sessions) as unitSessionPercentage, sum(orders_placed)/sum(sessions) as conversion ",
					new String[]{"unitSessionPercentage","conversion"}, new Type[]{FloatType.INSTANCE,FloatType.INSTANCE}))
					
					.add(Projections.sum("ordersPlaced").as("ordersPlaced")).add(Projections.avg("buyBoxPercentage").as("buyBoxPercentage"))
					.add(Projections.sum("grossProductSales").as("grossProductSales"))
					.add(Projections.sqlGroupProjection("DATE_FORMAT(data_date,'%x %v') as weekGroup", "weekGroup",new String[]{"weekGroup"}, new Type[]{StringType.INSTANCE}))
					.add(Projections.sqlProjection("sum(gross_product_sales)/sum(orders_placed) as aveSalesPerItem ",new String[]{"aveSalesPerItem"}, new Type[]{FloatType.INSTANCE}));
		}else if(searchFlag.equals("2")){
			projection = Projections.projectionList()
					.add(Projections.property("country"))
					.add(Projections.sum("sessions").as("sessions"))
					.add(Projections.sum("pageViews").as("pageViews"))
					.add(Projections.sum("unitsOrdered").as("unitsOrdered"))
					
					.add(Projections.sqlProjection("sum(units_ordered)/sum(sessions) as unitSessionPercentage, sum(orders_placed)/sum(sessions) as conversion ",
					new String[]{"unitSessionPercentage","conversion"}, new Type[]{FloatType.INSTANCE,FloatType.INSTANCE}))
					
					.add(Projections.sum("ordersPlaced").as("ordersPlaced")).add(Projections.avg("buyBoxPercentage").as("buyBoxPercentage"))
					.add(Projections.sum("grossProductSales").as("grossProductSales"))
					.add(Projections.sqlGroupProjection("DATE_FORMAT(data_date,'%Y %m') as monthGroup", "monthGroup",new String[]{"monthGroup"}, new Type[]{StringType.INSTANCE}))
					.add(Projections.sqlProjection("sum(gross_product_sales)/sum(orders_placed) as aveSalesPerItem ",new String[]{"aveSalesPerItem"}, new Type[]{FloatType.INSTANCE}));
		}
	
		criteria.setProjection(projection);
		list = criteria.list();
		List<BusinessReport> listdata = Lists.newArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int year=0;
		int week=0;
		int month=0;
		for (Object[] objs : list) {
			
			if(searchFlag.equals("0")){
			listdata.add(new BusinessReport(objs[0].toString(), DateUtils.parseDate(objs[9].toString().substring(0, 10)), null, null, null, null,
					Integer.parseInt(objs[1].toString()), 
					null,
					Integer.parseInt(objs[2].toString()), 
					null,
					Integer.parseInt(new java.text.DecimalFormat("0").format(objs[7])), 
					Integer.parseInt(objs[3].toString()), 
					Float.parseFloat(objs[4]==null?"0":objs[4].toString())*100f,
					Float.parseFloat(objs[8]==null?"0":objs[8].toString())*100f,
					Integer.parseInt(objs[6]==null?"0":objs[6].toString()), 
					Float.parseFloat(objs[5]==null?"0":objs[5].toString())*100f,Float.parseFloat(objs[10]==null?"0":objs[10].toString()),null,null,null,null));
			
			}else if(searchFlag.equals("1")){
				String[] stra = objs[9].toString().split(" ");
				year =Integer.parseInt(stra[0]);
				week =Integer.parseInt(stra[1]);
				if(week==53){
					year = year + 1;
					week = 1;
				}else if (year == 2016){
					week = week + 1;
				}
				listdata.add(new BusinessReport(
						objs[0].toString(), 
						sdf.parse(sdf.format(DateUtils.getLastDayOfWeek(year, week)))
						, null, null, null, null,
						Integer.parseInt(objs[1].toString()), 
						null,
						Integer.parseInt(objs[2].toString()), 
						null,
						Integer.parseInt(new java.text.DecimalFormat("0").format(objs[7])), 
						Integer.parseInt(objs[3].toString()), 
						Float.parseFloat(objs[4]==null?"0":objs[4].toString())*100f,
						Float.parseFloat(objs[8]==null?"0":objs[8].toString())*100f,
						Integer.parseInt(objs[6]==null?"0":objs[6].toString()), 
						Float.parseFloat(objs[5]==null?"0":objs[5].toString())*100f,Float.parseFloat(objs[10]==null?"0":objs[10].toString()),null,null,null,null));
			}else if(searchFlag.equals("2")){
				String[] stra = objs[9].toString().split(" ");
				year =Integer.parseInt(stra[0]);
				month =Integer.parseInt(stra[1]);
				listdata.add(new BusinessReport(
						objs[0].toString(), 
						sdf.parse(sdf.format(DateUtils.getFirstDayOfMonth(year, month)))
						, null, null, null, null,
						Integer.parseInt(objs[1].toString()), 
						null,
						Integer.parseInt(objs[2].toString()), 
						null,
						Integer.parseInt(new java.text.DecimalFormat("0").format(objs[7])), 
						Integer.parseInt(objs[3].toString()), 
						Float.parseFloat(objs[4]==null?"0":objs[4].toString())*100f,
						Float.parseFloat(objs[8]==null?"0":objs[8].toString())*100f,
						Integer.parseInt(objs[6]==null?"0":objs[6].toString()), 
						Float.parseFloat(objs[5]==null?"0":objs[5].toString())*100f,Float.parseFloat(objs[10]==null?"0":objs[10].toString()),null,null,null,null));
			}
		}
		
		
		
		page.setList(listdata);
		return page;
	}
	
	/**
	 * 使用检索标准对象查询记录数
	 * @param detachedCriteria
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public long countExtByDay(DetachedCriteria detachedCriteria) {
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		long totalCount = 0;
		try {
			// Get orders
			Field field = CriteriaImpl.class.getDeclaredField("orderEntries");
			field.setAccessible(true);
			List orderEntrys = (List)field.get(criteria);
			// Remove orders
			field.set(criteria, new ArrayList());
			// Get count
			criteria.setProjection(Projections.countDistinct("dataDate"));
			totalCount = Long.valueOf(criteria.uniqueResult().toString());
			// Clean count
			criteria.setProjection(null);
			// Restore orders
			field.set(criteria, orderEntrys);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return totalCount;
	}
	
	
	
	
	/**
	 * 使用检索标准对象查询记录数
	 * @param detachedCriteria
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public long countExtByWeek(DetachedCriteria detachedCriteria) {
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		long totalCount = 0;
		try {
			// Get orders
			Field field = CriteriaImpl.class.getDeclaredField("orderEntries");
			field.setAccessible(true);
			List orderEntrys = (List)field.get(criteria);
			// Remove orders
			field.set(criteria, new ArrayList());
			// Get count
			
			criteria.setProjection(Projections.sqlProjection("COUNT(DISTINCT DATE_FORMAT(data_date,'%x %v')) as title ",
					new String[]{"title"}, new Type[]{IntegerType.INSTANCE}));
			totalCount = Long.valueOf(criteria.uniqueResult().toString());
			// Clean count
			criteria.setProjection(null);
			// Restore orders
			field.set(criteria, orderEntrys);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return totalCount;
	}
	
	
	/**
	 * 使用检索标准对象查询记录数
	 * @param detachedCriteria
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public long countExtByMonth(DetachedCriteria detachedCriteria) {
		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
		long totalCount = 0;
		try {
			// Get orders
			Field field = CriteriaImpl.class.getDeclaredField("orderEntries");
			field.setAccessible(true);
			List orderEntrys = (List)field.get(criteria);
			// Remove orders
			field.set(criteria, new ArrayList());
			// Get count
			
			criteria.setProjection(Projections.sqlProjection("COUNT(DISTINCT DATE_FORMAT(data_date,'%Y %m')) as title ",
					new String[]{"title"}, new Type[]{IntegerType.INSTANCE}));
			totalCount = Long.valueOf(criteria.uniqueResult().toString());
			// Clean count
			criteria.setProjection(null);
			// Restore orders
			field.set(criteria, orderEntrys);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return totalCount;
	}
	

}
