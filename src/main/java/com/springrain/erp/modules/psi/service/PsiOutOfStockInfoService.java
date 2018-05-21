package com.springrain.erp.modules.psi.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.psi.dao.PsiInventoryGapDao;
import com.springrain.erp.modules.psi.dao.PsiOutOfStockInfoDao;
import com.springrain.erp.modules.psi.entity.PsiInventoryGap;
import com.springrain.erp.modules.psi.entity.PsiOutOfStockInfo;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.scheduler.PsiConfig;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

@Component
@Transactional(readOnly = true)
public class PsiOutOfStockInfoService extends BaseService{

	@Autowired
	private PsiOutOfStockInfoDao       psiOutOfStockInfoDao;
	
	@Autowired
	private PsiInventoryGapDao         psiInventoryGapDao;
	
	@Autowired
	private PsiInventoryService        psiInventoryService;
	@Autowired
	private PsiProductService psiProductService;
	
	@Transactional(readOnly = false)
	public void save(PsiOutOfStockInfo psiOutOfStockInfo) {
		psiOutOfStockInfoDao.save(psiOutOfStockInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(List<PsiOutOfStockInfo> psiOutOfStockInfos) {
		psiOutOfStockInfoDao.save(psiOutOfStockInfos);
	}
	
	@Transactional(readOnly = false)
	public void saveGaps(List<PsiInventoryGap> psiInventoryGaps) {
		psiInventoryGapDao.save(psiInventoryGaps);
	}
	
	public Page<PsiOutOfStockInfo> findAllInfo(Page<PsiOutOfStockInfo> page, PsiOutOfStockInfo psiOutOfStockInfo) {
		DetachedCriteria dc = psiOutOfStockInfoDao.createDetachedCriteria();
		if(StringUtils.isNotBlank(psiOutOfStockInfo.getCountry())){
			dc.add(Restrictions.eq("country", psiOutOfStockInfo.getCountry()));
		}
		if(psiOutOfStockInfo.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",psiOutOfStockInfo.getCreateDate()));
		}
		if(psiOutOfStockInfo.getActualDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(psiOutOfStockInfo.getActualDate(),1)));
		}
		page.setOrderBy("createDate desc");
		return psiOutOfStockInfoDao.find(page,dc);
	}
	
	
	public List<Object[]> getOutOfStockChangePrice(){
		String sql="SELECT f.`country`,p.sku,IFNULL(p.sale_price,p.price),s.`product_name`,s.`color` FROM amazoninfo_price_feed f JOIN amazoninfo_price p ON f.id=p.`feed_price_feed_id`"+
          " JOIN psi_sku s ON f.`country`=s.`country` AND p.`sku`=s.`sku` AND s.`del_flag`='0' "+
          " WHERE f.`state`='3' AND reason='断货升价'  AND DATE_FORMAT(f.`request_date`,'%Y-%m-%d')=DATE_FORMAT(NOW(),'%Y-%m-%d') ";// 
		List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql);
		return list;
	}
	
	
	
	public Map<String,Integer> findFbaTrans(){
		 //在途  FBA运输	
		 Map<String,Integer> fbaTransMap=Maps.newHashMap();
		 String sql1="SELECT CONCAT(i.`product_name`,CASE  WHEN i.`color_code`='' THEN '' ELSE CONCAT('_',i.`color_code`) END),i.country_code,SUM(i.`quantity`) AS quantity "+
				" FROM psi_transport_order AS a ,psi_transport_order_item AS i "+
				" WHERE a.`id`=i.`transport_order_id` and i.del_flag='0' AND a.transport_sta IN ('1','2','3') AND a.`transport_type`='1' GROUP BY CONCAT(i.`product_name`,CASE  WHEN i.`color_code`='' THEN '' ELSE CONCAT('_',i.`color_code`) END),i.country_code ";
			List<Object[]> list1=psiOutOfStockInfoDao.findBySql(sql1);
			for (Object[] obj : list1) {
				String key=obj[0].toString()+"_"+obj[1].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				fbaTransMap.put(key, quantity);
				if("fr,uk,es,it,de".contains(obj[1].toString())){
					Integer euQuantity=fbaTransMap.get(obj[0].toString()+"_eu");
					fbaTransMap.put(obj[0].toString()+"_eu", quantity+(euQuantity==null?0:euQuantity));
				}
			}
		return 	fbaTransMap;
	}
	
	
	public Map<String,Map<String,Map<String,PsiInventoryGap>>> findAllCountryGap(String forecastType,String weekNum){
		Map<String,Map<String,Map<String,PsiInventoryGap>>> map=Maps.newHashMap();
	//	String sql="SELECT MAX(create_date) FROM psi_inventory_gap where forecast_type=:p1  AND TYPE IN ('8','9','10','11','12','13')";
		String sql="SELECT create_date FROM psi_inventory_gap WHERE forecast_type=:p1  AND TYPE IN ('8', '9', '10', '11', '12', '13') ORDER BY create_date DESC LIMIT 1";
		List<Object> rs=psiOutOfStockInfoDao.findBySql(sql,new Parameter(forecastType));
		if(rs.size()>0){
			Date date=(Timestamp)rs.get(0);
			if(date!=null){
				Date createDate=new Date();
				Date start = new Date();
				start.setHours(0);
				start.setMinutes(0);
				start.setSeconds(0);
				Date end = DateUtils.addMonths(start,5);
				DateFormat formatWeek = new SimpleDateFormat("yyyyww");
				DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
				if(start.getDay()==0){
					start = DateUtils.addDays(start, -1);
				}
				Map<String,Integer> weekList=Maps.newLinkedHashMap();
				Map<Integer, String> tip = Maps.newHashMap();
				int num=1;
				while(end.after(start)||end.equals(start)){
						String key = formatWeek.format(start);
						int year =DateUtils.getSunday(start).getYear()+1900;
						int week =  Integer.parseInt(key.substring(4));
						if(week==53){
			                year =DateUtils.getMonday(start).getYear()+1900;
					    }
						if(week<10){
							key = year+"0"+week;
						}else{
							key =year+""+week;
						}
						
						if(weekList.size()>16){
							break;
						}else{
							weekList.put(key,num++);
							Date first = DateUtils.getFirstDayOfWeek(year, week);
							tip.put(num-1,DateUtils.getDate(first,"yyyy-MM-dd")+","+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
							start = DateUtils.addWeeks(start, 1);
						}
				}
				
				
				String sql1="SELECT name_color,country,TYPE,week1,week2,week3,week4,week5,week6,week7,week8,week9,week10,week11,week12,week13,week14,week15,week16 FROM psi_inventory_gap where create_date=:p1 and forecast_type=:p2  AND TYPE IN ('8','9','10','11','12','13') ";
				if("0".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0) ";
				}
				if("1".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0 or week5<0 or week6<0 or week7<0 or week8<0) ";
				}
				if("2".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0 or week5<0 or week6<0 or week7<0 or week8<0 or week9<0 or week10<0 or week11<0 or week12<0) ";
				}
				if("3".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0 or week5<0 or week6<0 or week7<0 or week8<0 or week9<0 or week10<0 or week11<0 or week12<0 or week13<0 or week14<0 or week15<0 or week16<0) ";
				}
				List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql1,new Parameter(date,forecastType));
				for (Object[] obj: list) {
					PsiInventoryGap gap=new PsiInventoryGap();
					String name=obj[0].toString();
					String country=obj[1].toString();
					String type=obj[2].toString();
					gap.setNameColor(name);
					gap.setForecastType(forecastType);
					gap.setType(type);
					gap.setWeek1(obj[3]==null?0:Integer.parseInt(obj[3].toString()));
					gap.setWeek2(obj[4]==null?0:Integer.parseInt(obj[4].toString()));
					gap.setWeek3(obj[5]==null?0:Integer.parseInt(obj[5].toString()));
					gap.setWeek4(obj[6]==null?0:Integer.parseInt(obj[6].toString()));
					gap.setWeek5(obj[7]==null?0:Integer.parseInt(obj[7].toString()));
					gap.setWeek6(obj[8]==null?0:Integer.parseInt(obj[8].toString()));
					gap.setWeek7(obj[9]==null?0:Integer.parseInt(obj[9].toString()));
					gap.setWeek8(obj[10]==null?0:Integer.parseInt(obj[10].toString()));
					gap.setWeek9(obj[11]==null?0:Integer.parseInt(obj[11].toString()));
					gap.setWeek10(obj[12]==null?0:Integer.parseInt(obj[12].toString()));
					gap.setWeek11(obj[13]==null?0:Integer.parseInt(obj[13].toString()));
					gap.setWeek12(obj[14]==null?0:Integer.parseInt(obj[14].toString()));
					gap.setWeek13(obj[15]==null?0:Integer.parseInt(obj[15].toString()));
					gap.setWeek14(obj[16]==null?0:Integer.parseInt(obj[16].toString()));
					gap.setWeek15(obj[17]==null?0:Integer.parseInt(obj[17].toString()));
					gap.setWeek16(obj[18]==null?0:Integer.parseInt(obj[18].toString()));
					
					List<Date> dateList=Lists.newArrayList();
					Integer day=0;
					if("0".equals(weekNum)||"1".equals(weekNum)||"2".equals(weekNum)||"3".equals(weekNum)){
						if(gap.getWeek1()<0){
							String[] arr=tip.get(1).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek1());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek2()<0){
							String[] arr=tip.get(2).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek2());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek3()<0){
							String[] arr=tip.get(3).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek3());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek4()<0){
							String[] arr=tip.get(4).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek4());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if("1".equals(weekNum)||"2".equals(weekNum)||"3".equals(weekNum)){
						if(gap.getWeek5()<0){
							String[] arr=tip.get(5).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek5());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek6()<0){
							String[] arr=tip.get(6).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek6());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek7()<0){
							String[] arr=tip.get(7).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek7());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek8()<0){
							String[] arr=tip.get(8).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek8());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if("2".equals(weekNum)||"3".equals(weekNum)){
						if(gap.getWeek9()<0){
							String[] arr=tip.get(9).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek9());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek10()<0){
							String[] arr=tip.get(10).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek10());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek11()<0){
							String[] arr=tip.get(11).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek11());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek12()<0){
							String[] arr=tip.get(12).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek12());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if("3".equals(weekNum)){
						if(gap.getWeek13()<0){
							String[] arr=tip.get(13).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek13());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek14()<0){
							String[] arr=tip.get(14).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek14());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek15()<0){
							String[] arr=tip.get(15).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek15());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek16()<0){
							String[] arr=tip.get(16).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek16());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						
					}
					gap.setDay(day);
					StringBuilder time= new StringBuilder("");
					
						List<Date> removeList=Lists.newArrayList();
						for(int i=2;i<dateList.size();i=i+2){
							Date beforeDate=dateList.get(i-1);
							Date afterDate=dateList.get(i);
							if(DateUtils.addDays(beforeDate, 1).equals(afterDate)){
								removeList.add(beforeDate);
								removeList.add(afterDate);
							}
						}
						dateList.removeAll(removeList);
						for(int i=0;i<dateList.size()-1;i=i+2){
							time.append(formatDay.format(dateList.get(i))).append("~").append(formatDay.format(dateList.get(i+1))).append(";");
						}
					    gap.setTime(time.toString());
						Map<String,Map<String,PsiInventoryGap>> temp=map.get(name);
						if(temp==null){
							temp=Maps.newHashMap();
							map.put(name,temp);
						}
						Map<String,PsiInventoryGap> temp2=temp.get(country);
						if(temp2!=null){
							String oldKey="";
							for(String oldType:temp2.keySet()){
								if(Integer.parseInt(type)>Integer.parseInt(oldType)){
									oldKey=oldType;
									temp2.put(type,gap);
									break;
								}
							}
							if(StringUtils.isNotBlank(oldKey)){
								temp2.remove(oldKey);
							}
							continue;
						}
						if(temp2==null){
							temp2=Maps.newHashMap();
							temp.put(country,temp2);
						}
						temp2.put(type,gap);
					
				}
			}
		}	
		return map;
	}	
	
	

	
	public Map<String,Map<String,PsiInventoryGap>> findGap(String country,String forecastType,String weekNum){
		Map<String,Map<String,PsiInventoryGap>> map=Maps.newHashMap();
		//String sql="SELECT MAX(create_date) FROM psi_inventory_gap where country=:p1 and forecast_type=:p2  AND TYPE IN ('8','9','10','11','12','13')";
		String sql="SELECT create_date FROM psi_inventory_gap WHERE country=:p1 and forecast_type=:p2  AND TYPE IN ('8', '9', '10', '11', '12', '13') ORDER BY create_date DESC LIMIT 1";
		List<Object> rs=psiOutOfStockInfoDao.findBySql(sql,new Parameter(country,forecastType));
		if(rs.size()>0){
			Date date=(Timestamp)rs.get(0);
			if(date!=null){
				Date createDate=new Date();
				Date start = new Date();
				start.setHours(0);
				start.setMinutes(0);
				start.setSeconds(0);
				Date end = DateUtils.addMonths(start,5);
				DateFormat formatWeek = new SimpleDateFormat("yyyyww");
				DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
				if(start.getDay()==0){
					start = DateUtils.addDays(start, -1);
				}
				Map<String,Integer> weekList=Maps.newLinkedHashMap();
				Map<Integer, String> tip = Maps.newHashMap();
				int num=1;
				while(end.after(start)||end.equals(start)){
						String key = formatWeek.format(start);
						int year =DateUtils.getSunday(start).getYear()+1900;
						int week =  Integer.parseInt(key.substring(4));
						if(week==53){
			                year =DateUtils.getMonday(start).getYear()+1900;
					    }
						if(week<10){
							key = year+"0"+week;
						}else{
							key =year+""+week;
						}
						
						if(weekList.size()>16){
							break;
						}else{
							weekList.put(key,num++);
							Date first = DateUtils.getFirstDayOfWeek(year, week);
							tip.put(num-1,DateUtils.getDate(first,"yyyy-MM-dd")+","+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
							start = DateUtils.addWeeks(start, 1);
						}
				}
				
				
				String sql1="SELECT name_color,forecast_type,TYPE,week1,week2,week3,week4,week5,week6,week7,week8,week9,week10,week11,week12,week13,week14,week15,week16 FROM psi_inventory_gap where create_date=:p1 and country=:p2 and forecast_type=:p3  AND TYPE IN ('8','9','10','11','12','13') ";
				if("0".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0) ";
				}
				if("1".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0 or week5<0 or week6<0 or week7<0 or week8<0) ";
				}
				if("2".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0 or week5<0 or week6<0 or week7<0 or week8<0 or week9<0 or week10<0 or week11<0 or week12<0) ";
				}
				if("3".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0 or week5<0 or week6<0 or week7<0 or week8<0 or week9<0 or week10<0 or week11<0 or week12<0 or week13<0 or week14<0 or week15<0 or week16<0) ";
				}
				List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql1,new Parameter(date,country,forecastType));
				for (Object[] obj: list) {
					PsiInventoryGap gap=new PsiInventoryGap();
					String name=obj[0].toString();
					String type=obj[2].toString();
					gap.setNameColor(name);
					gap.setForecastType(forecastType);
					gap.setType(type);
					gap.setWeek1(obj[3]==null?0:Integer.parseInt(obj[3].toString()));
					gap.setWeek2(obj[4]==null?0:Integer.parseInt(obj[4].toString()));
					gap.setWeek3(obj[5]==null?0:Integer.parseInt(obj[5].toString()));
					gap.setWeek4(obj[6]==null?0:Integer.parseInt(obj[6].toString()));
					gap.setWeek5(obj[7]==null?0:Integer.parseInt(obj[7].toString()));
					gap.setWeek6(obj[8]==null?0:Integer.parseInt(obj[8].toString()));
					gap.setWeek7(obj[9]==null?0:Integer.parseInt(obj[9].toString()));
					gap.setWeek8(obj[10]==null?0:Integer.parseInt(obj[10].toString()));
					gap.setWeek9(obj[11]==null?0:Integer.parseInt(obj[11].toString()));
					gap.setWeek10(obj[12]==null?0:Integer.parseInt(obj[12].toString()));
					gap.setWeek11(obj[13]==null?0:Integer.parseInt(obj[13].toString()));
					gap.setWeek12(obj[14]==null?0:Integer.parseInt(obj[14].toString()));
					gap.setWeek13(obj[15]==null?0:Integer.parseInt(obj[15].toString()));
					gap.setWeek14(obj[16]==null?0:Integer.parseInt(obj[16].toString()));
					gap.setWeek15(obj[17]==null?0:Integer.parseInt(obj[17].toString()));
					gap.setWeek16(obj[18]==null?0:Integer.parseInt(obj[18].toString()));
					
					List<Date> dateList=Lists.newArrayList();
					Integer day=0;
					if("0".equals(weekNum)||"1".equals(weekNum)||"2".equals(weekNum)||"3".equals(weekNum)){
						if(gap.getWeek1()<0){
							String[] arr=tip.get(1).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek1());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek2()<0){
							String[] arr=tip.get(2).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek2());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek3()<0){
							String[] arr=tip.get(3).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek3());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek4()<0){
							String[] arr=tip.get(4).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek4());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if("1".equals(weekNum)||"2".equals(weekNum)||"3".equals(weekNum)){
						if(gap.getWeek5()<0){
							String[] arr=tip.get(5).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek5());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek6()<0){
							String[] arr=tip.get(6).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek6());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek7()<0){
							String[] arr=tip.get(7).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek7());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek8()<0){
							String[] arr=tip.get(8).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek8());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if("2".equals(weekNum)||"3".equals(weekNum)){
						if(gap.getWeek9()<0){
							String[] arr=tip.get(9).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek9());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek10()<0){
							String[] arr=tip.get(10).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek10());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek11()<0){
							String[] arr=tip.get(11).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek11());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek12()<0){
							String[] arr=tip.get(12).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek12());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if("3".equals(weekNum)){
						if(gap.getWeek13()<0){
							String[] arr=tip.get(13).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek13());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek14()<0){
							String[] arr=tip.get(14).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek14());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek15()<0){
							String[] arr=tip.get(15).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek15());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek16()<0){
							String[] arr=tip.get(16).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=7;
								gap.setGap(gap.getWeek16());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						
					}
					gap.setDay(day);
					StringBuilder time= new StringBuilder();
					
						List<Date> removeList=Lists.newArrayList();
						for(int i=2;i<dateList.size();i=i+2){
							Date beforeDate=dateList.get(i-1);
							Date afterDate=dateList.get(i);
							if(DateUtils.addDays(beforeDate, 1).equals(afterDate)){
								removeList.add(beforeDate);
								removeList.add(afterDate);
							}
						}
						dateList.removeAll(removeList);
						for(int i=0;i<dateList.size()-1;i=i+2){
							time.append(formatDay.format(dateList.get(i))).append("~").append(formatDay.format(dateList.get(i+1))).append(";");
						}
					    gap.setTime(time.toString());
						Map<String,PsiInventoryGap> temp=map.get(name);
						if(temp!=null){
							String oldKey="";
							for(String oldType:temp.keySet()){
								if(Integer.parseInt(type)>Integer.parseInt(oldType)){
									oldKey=oldType;
									temp.put(type,gap);
									break;
								}
							}
							if(StringUtils.isNotBlank(oldKey)){
								temp.remove(oldKey);
							}
							continue;
						}
						if(temp==null){
							temp=Maps.newHashMap();
							map.put(name,temp);
						}
						temp.put(type,gap);
					
				}
			}
		}	
		return map;
	}	
	
	public Map<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>> findAllCountryGap(String weekNum){
		Map<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>> map=Maps.newHashMap();
		//String sql="SELECT MAX(create_date) FROM psi_inventory_gap where TYPE IN ('8','9','10','11','12','13')";
		String sql="SELECT create_date FROM psi_inventory_gap WHERE  TYPE IN ('8', '9', '10', '11', '12', '13') ORDER BY create_date DESC LIMIT 1";
		
		List<Object> rs=psiOutOfStockInfoDao.findBySql(sql);
		if(rs.size()>0){
			Date date=(Timestamp)rs.get(0);
			if(date!=null){
				Date createDate=new Date();
				Date start = new Date();
				start.setHours(0);
				start.setMinutes(0);
				start.setSeconds(0);
				Date end = DateUtils.addMonths(start,5);
				DateFormat formatWeek = new SimpleDateFormat("yyyyww");
				DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
				if(start.getDay()==0){
					start = DateUtils.addDays(start, -1);
				}
				Map<String,Integer> weekList=Maps.newLinkedHashMap();
				Map<Integer, String> tip = Maps.newHashMap();
				int num=1;
				while(end.after(start)||end.equals(start)){
						String key = formatWeek.format(start);
						int year =DateUtils.getSunday(start).getYear()+1900;
						int week =  Integer.parseInt(key.substring(4));
						if(week==53){
			                year =DateUtils.getMonday(start).getYear()+1900;
					    }
						if(week<10){
							key = year+"0"+week;
						}else{
							key =year+""+week;
						}
						
						if(weekList.size()>16){
							break;
						}else{
							weekList.put(key,num++);
							Date first = DateUtils.getFirstDayOfWeek(year, week);
							tip.put(num-1,DateUtils.getDate(first,"yyyy-MM-dd")+","+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
							start = DateUtils.addWeeks(start, 1);
						}
				}
				
				
				String sql1="SELECT name_color,country,TYPE,week1,week2,week3,week4,week5,week6,week7,week8,week9,week10,week11,week12,week13,week14,week15,week16,forecast_type FROM psi_inventory_gap where create_date=:p1  AND TYPE IN ('8','9','10','11','12','13') ";
				if("0".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0) ";
				}
				if("1".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0 or week5<0 or week6<0 or week7<0 or week8<0) ";
				}
				if("2".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0 or week5<0 or week6<0 or week7<0 or week8<0 or week9<0 or week10<0 or week11<0 or week12<0) ";
				}
				if("3".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0 or week5<0 or week6<0 or week7<0 or week8<0 or week9<0 or week10<0 or week11<0 or week12<0 or week13<0 or week14<0 or week15<0 or week16<0) ";
				}
				sql1+=" union SELECT name_color,country,TYPE,week1,week2,week3,week4,week5,week6,week7,week8,week9,week10,week11,week12,week13,week14,week15,week16,forecast_type FROM psi_inventory_gap where create_date=:p1  AND TYPE IN ('1','2','3','4','5','6','7') ";
				
				List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql1,new Parameter(date));
				for (Object[] obj: list) {
					PsiInventoryGap gap=new PsiInventoryGap();
					String name=obj[0].toString();
					String country=obj[1].toString();
					String type=obj[2].toString();
					String forecastType=obj[19].toString();
					gap.setNameColor(name);
					gap.setForecastType(forecastType);
					gap.setType(type);
					gap.setWeek1(obj[3]==null?0:Integer.parseInt(obj[3].toString()));
					gap.setWeek2(obj[4]==null?0:Integer.parseInt(obj[4].toString()));
					gap.setWeek3(obj[5]==null?0:Integer.parseInt(obj[5].toString()));
					gap.setWeek4(obj[6]==null?0:Integer.parseInt(obj[6].toString()));
					gap.setWeek5(obj[7]==null?0:Integer.parseInt(obj[7].toString()));
					gap.setWeek6(obj[8]==null?0:Integer.parseInt(obj[8].toString()));
					gap.setWeek7(obj[9]==null?0:Integer.parseInt(obj[9].toString()));
					gap.setWeek8(obj[10]==null?0:Integer.parseInt(obj[10].toString()));
					gap.setWeek9(obj[11]==null?0:Integer.parseInt(obj[11].toString()));
					gap.setWeek10(obj[12]==null?0:Integer.parseInt(obj[12].toString()));
					gap.setWeek11(obj[13]==null?0:Integer.parseInt(obj[13].toString()));
					gap.setWeek12(obj[14]==null?0:Integer.parseInt(obj[14].toString()));
					gap.setWeek13(obj[15]==null?0:Integer.parseInt(obj[15].toString()));
					gap.setWeek14(obj[16]==null?0:Integer.parseInt(obj[16].toString()));
					gap.setWeek15(obj[17]==null?0:Integer.parseInt(obj[17].toString()));
					gap.setWeek16(obj[18]==null?0:Integer.parseInt(obj[18].toString()));
					
					List<Date> dateList=Lists.newArrayList();
					Integer day=0;
					if("0".equals(weekNum)||"1".equals(weekNum)||"2".equals(weekNum)||"3".equals(weekNum)){
						if(gap.getWeek1()<0){
							String[] arr=tip.get(1).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek1());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek2()<0){
							String[] arr=tip.get(2).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek2());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek3()<0){
							String[] arr=tip.get(3).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek3());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek4()<0){
							String[] arr=tip.get(4).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek4());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if("1".equals(weekNum)||"2".equals(weekNum)||"3".equals(weekNum)){
						if(gap.getWeek5()<0){
							String[] arr=tip.get(5).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek5());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek6()<0){
							String[] arr=tip.get(6).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek6());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek7()<0){
							String[] arr=tip.get(7).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek7());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek8()<0){
							String[] arr=tip.get(8).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek8());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if("2".equals(weekNum)||"3".equals(weekNum)){
						if(gap.getWeek9()<0){
							String[] arr=tip.get(9).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek9());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek10()<0){
							String[] arr=tip.get(10).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek10());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek11()<0){
							String[] arr=tip.get(11).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek11());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek12()<0){
							String[] arr=tip.get(12).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek12());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if("3".equals(weekNum)){
						if(gap.getWeek13()<0){
							String[] arr=tip.get(13).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek13());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek14()<0){
							String[] arr=tip.get(14).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek14());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek15()<0){
							String[] arr=tip.get(15).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek15());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek16()<0){
							String[] arr=tip.get(16).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek16());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						
					}
					gap.setDay(day);
					StringBuilder time=new StringBuilder();
					
						List<Date> removeList=Lists.newArrayList();
						for(int i=2;i<dateList.size();i=i+2){
							Date beforeDate=dateList.get(i-1);
							Date afterDate=dateList.get(i);
							if(DateUtils.addDays(beforeDate, 1).equals(afterDate)){
								removeList.add(beforeDate);
								removeList.add(afterDate);
							}
						}
						dateList.removeAll(removeList);
						for(int i=0;i<dateList.size()-1;i=i+2){
							time.append(formatDay.format(dateList.get(i))).append("~").append(formatDay.format(dateList.get(i+1))).append(";");
						}
					    gap.setTime(time.toString());
					    Map<String,Map<String,Map<String,PsiInventoryGap>>> foreMap=map.get(country); 
					    if(foreMap==null){
					    	foreMap=Maps.newHashMap();
					    	map.put(country, foreMap);
					    }
					    
						Map<String,Map<String,PsiInventoryGap>> temp=foreMap.get(forecastType);
						if(temp==null){
							temp=Maps.newHashMap();
							foreMap.put(forecastType,temp);
						}
						Map<String,PsiInventoryGap> temp2=temp.get(name);
						if(temp2==null){
							temp2=Maps.newHashMap();
							temp.put(name,temp2);
						}
						temp2.put(type,gap);
				  }
			}
		}	
		
		
		
		Map<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>> newMap=Maps.newHashMap();
         Map<String,Map<String,Integer>> poMap=getPoStock();
         if(map!=null&&map.size()>0){
			   for (Map.Entry<String, Map<String, Map<String, Map<String, PsiInventoryGap>>>> countryEntry:map.entrySet()) {
				   		 String country = countryEntry.getKey();
						 Map<String,Map<String,Map<String,PsiInventoryGap>>> countryMap=countryEntry.getValue();
						 for(Map.Entry<String,Map<String,Map<String,PsiInventoryGap>>> entry3:countryMap.entrySet()){
							 String foreCastType = entry3.getKey();
							 Map<String,Map<String,PsiInventoryGap>> forecastMap=entry3.getValue();
							 for (Map.Entry<String,Map<String,PsiInventoryGap>> entry: forecastMap.entrySet()) {
								 String name = entry.getKey();
								 Map<String,PsiInventoryGap> typeMap=entry.getValue();
								 Set<String> set=typeMap.keySet();
								 Integer type=0;
								 for(String max:set){
									 if(Integer.parseInt(max)>=8&&Integer.parseInt(max)>=type){
										 type=Integer.parseInt(max);
									 }
								 }
								 if(type<8){
									 continue;
								 }
								 PsiInventoryGap newGap=typeMap.get(type+"");
								 PsiInventoryGap gap=typeMap.get((type-5)+"");
								 if(gap==null){
									 if(type==13){
										 newGap.setDesc("紧急增加PO");
									 }
								 }else{
									 if(type==13){
										    Integer num=0;
										    gap=typeMap.get((type-6)+"");
										    if(gap==null){
												 if(type==13){
													 newGap.setDesc("紧急增加PO");
												 }
											}else{
												Integer poQuantity=0;
												
												if("de".equals(country)){
													PsiProduct product=psiProductService.findProductByProductName(name);
													if(product!=null&&"0".equals(product.getHasPower())){
														if(poMap!=null&&poMap.get("eu")!=null&&poMap.get("eu").get(name)!=null){
															poQuantity=poMap.get("eu").get(name);
														}
													}else{
														if(poMap!=null&&poMap.get(country)!=null&&poMap.get(country).get(name)!=null){
															poQuantity=poMap.get(country).get(name);
														}
													}
												}else{
													if(poMap!=null&&poMap.get(country)!=null&&poMap.get(country).get(name)!=null){
														poQuantity=poMap.get(country).get(name);
													}
												}
												
												poMap.get(country).get(name);
												   String weekType="4周后";
												   if("0".equals(weekNum)){
												    	num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4();
												    	weekType="4周后";
													}
													if("1".equals(weekNum)){
														num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4()
															+gap.getWeek5()+gap.getWeek6()+gap.getWeek7()+gap.getWeek8();
														weekType="8周后";
													}
													if("2".equals(weekNum)){
														num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4()+
																+gap.getWeek5()+gap.getWeek6()+gap.getWeek7()+gap.getWeek8()
																+gap.getWeek9()+gap.getWeek10()+gap.getWeek11()+gap.getWeek12();
														weekType="12周后";
													}
													if("3".equals(weekNum)){
														num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4()+
																+gap.getWeek5()+gap.getWeek6()+gap.getWeek7()+gap.getWeek8()
																+gap.getWeek9()+gap.getWeek10()+gap.getWeek11()+gap.getWeek12()
																+gap.getWeek13()+gap.getWeek14()+gap.getWeek15()+gap.getWeek16();
														weekType="16周后";
													}
												if(poQuantity>num){
													newGap.setDesc(weekType+"可到货"+(poQuantity-num));
												}else{
													newGap.setDesc("紧急增加PO");
												}
										    }
										    
									 }else{
										    Integer num=0;
										    if("0".equals(weekNum)){
										    	num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4();
											}
											if("1".equals(weekNum)){
												num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4()
													+gap.getWeek5()+gap.getWeek6()+gap.getWeek7()+gap.getWeek8();
											}
											if("2".equals(weekNum)){
												num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4()+
														+gap.getWeek5()+gap.getWeek6()+gap.getWeek7()+gap.getWeek8()
														+gap.getWeek9()+gap.getWeek10()+gap.getWeek11()+gap.getWeek12();
											}
											if("3".equals(weekNum)){
												num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4()+
														+gap.getWeek5()+gap.getWeek6()+gap.getWeek7()+gap.getWeek8()
														+gap.getWeek9()+gap.getWeek10()+gap.getWeek11()+gap.getWeek12()
														+gap.getWeek13()+gap.getWeek14()+gap.getWeek15()+gap.getWeek16();
											}
											newGap.setDesc(newGap.getNextGapType()+num);
									 }
								 }
								 Map<String,Map<String,Map<String,PsiInventoryGap>>> newAllMap=newMap.get(country);
								 if(newAllMap==null){
									 newAllMap=Maps.newHashMap();
									 newMap.put(country, newAllMap);
								 }
								 
								 Map<String,Map<String,PsiInventoryGap>> newTemp=newAllMap.get(foreCastType);
								 if(newTemp==null){
									 newTemp=Maps.newHashMap();
									 newAllMap.put(foreCastType, newTemp);
								 }
								 Map<String,PsiInventoryGap> newNameTemp=newTemp.get(name);
								 if(newNameTemp==null){
									 newNameTemp=Maps.newHashMap();
									 newTemp.put(name, newNameTemp);
								 }
								 newNameTemp.put(type+"", newGap);
							 }
							 
							 
						 }
						 
			   }	
         } 
		
		
		if(newMap!=null&&newMap.size()>0){
			   for (Dict dict : DictUtils.getDictList("platform")) {
				    String country= dict.getValue();
					if(!"mx".equals(country)&&!"com.unitek".equals(country)){
						 Map<String,Map<String,Map<String,PsiInventoryGap>>> countryMap=newMap.get(country);
						 if(countryMap!=null&&countryMap.size()>0){
							    Map<String,Map<String,PsiInventoryGap>> forecastMap=countryMap.get("0");
								//1.周日销
								 Map<String,Map<String,PsiInventoryGap>> weekMap=countryMap.get("1");
								 Set<String> weekRemoveName=Sets.newHashSet();
								 if(weekMap!=null&&weekMap.size()>0){
									 for (Map.Entry<String,Map<String,PsiInventoryGap>> entry: weekMap.entrySet()) {
										 String name = entry.getKey();
										 Map<String,PsiInventoryGap> weekTypeMap=entry.getValue();
										 for(String type:weekTypeMap.keySet()){
											 if(forecastMap!=null&&forecastMap.get(name)!=null&&forecastMap.get(name).get(type)!=null){
												 weekRemoveName.add(name);
											 }
										 }
										 
									}
								 }
								 if(weekRemoveName!=null&&weekRemoveName.size()>0){
									 for(String key:weekRemoveName){
										 weekMap.remove(key);
									 }
								 }
								 
								 Map<String,Map<String,PsiInventoryGap>> forecastMap2=countryMap.get("3");
								 Set<String> weekRemoveName2=Sets.newHashSet();
								 if(forecastMap2!=null&&forecastMap2.size()>0){
//									 for (String name: forecastMap2.keySet()) {
									 for (Map.Entry<String,Map<String,PsiInventoryGap>> entry: forecastMap2.entrySet()) {
										 String name = entry.getKey();
										 Map<String,PsiInventoryGap> weekTypeMap=entry.getValue();
										 for(String type:weekTypeMap.keySet()){
											 if(forecastMap!=null&&forecastMap.get(name)!=null&&forecastMap.get(name).get(type)!=null){
												 weekRemoveName2.add(name);
												 break;
											 }
											 if(weekMap!=null&&weekMap.get(name)!=null&&weekMap.get(name).get(type)!=null){
												 weekRemoveName2.add(name);
											 }
										 }
										 
									}
								 }
								 if(weekRemoveName2!=null&&weekRemoveName2.size()>0){
									 for(String key:weekRemoveName2){
										 forecastMap2.remove(key);
									 }
								 }
								 
								 
								 Map<String,Map<String,PsiInventoryGap>> weekMap2=countryMap.get("2");//week-safe
								 Set<String> weekRemoveName3=Sets.newHashSet();
								 if(weekMap2!=null&&weekMap2.size()>0){
//									 for (String name: weekMap2.keySet()) {
									 for (Map.Entry<String,Map<String,PsiInventoryGap>> entry: weekMap2.entrySet()) {
										 String name = entry.getKey();
										 Map<String,PsiInventoryGap> weekTypeMap=entry.getValue();
										 for(String type:weekTypeMap.keySet()){
											 if(forecastMap!=null&&forecastMap.get(name)!=null&&forecastMap.get(name).get(type)!=null){
												 weekRemoveName3.add(name);
												 break;
											 }
											 if(weekMap!=null&&weekMap.get(name)!=null&&weekMap.get(name).get(type)!=null){
												 weekRemoveName3.add(name);
											 }
											 if(forecastMap2!=null&&forecastMap2.get(name)!=null&&forecastMap2.get(name).get(type)!=null){
												 weekRemoveName3.add(name);
											 }
										 }
									 }
								 }
								 if(weekRemoveName3!=null&&weekRemoveName3.size()>0){
									 for(String key:weekRemoveName3){
										 weekMap2.remove(key);
									 }
								 }
							 
						 }
					}
			   }
			 
		}
		return newMap;
	}	
	
	public Map<String,Map<String,Map<String,PsiInventoryGap>>> findGapAllForecastType(String country,String weekNum){
		Map<String,Map<String,Integer>> poMap=getPoStock();
		
		Map<String,Map<String,Map<String,PsiInventoryGap>>> map=Maps.newHashMap();
		//String sql="SELECT MAX(create_date) FROM psi_inventory_gap where country=:p1";
		String sql="SELECT create_date FROM psi_inventory_gap WHERE country=:p1  ORDER BY create_date DESC LIMIT 1";
		
		List<Object> rs=psiOutOfStockInfoDao.findBySql(sql,new Parameter(country));
		if(rs.size()>0){
			Date date=(Timestamp)rs.get(0);
			if(date!=null){
				Date createDate=new Date();
				Date start = new Date();
				start.setHours(0);
				start.setMinutes(0);
				start.setSeconds(0);
				Date end = DateUtils.addMonths(start,5);
				DateFormat formatWeek = new SimpleDateFormat("yyyyww");
				DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
				if(start.getDay()==0){
					start = DateUtils.addDays(start, -1);
				}
				Map<String,Integer> weekList=Maps.newLinkedHashMap();
				Map<Integer, String> tip = Maps.newHashMap();
				int num=1;
				while(end.after(start)||end.equals(start)){
						String key = formatWeek.format(start);
						int year =DateUtils.getSunday(start).getYear()+1900;
						int week =  Integer.parseInt(key.substring(4));
						if(week==53){
			                year =DateUtils.getMonday(start).getYear()+1900;
					    }
						if(week<10){
							key = year+"0"+week;
						}else{
							key =year+""+week;
						}
						
						if(weekList.size()>16){
							break;
						}else{
							weekList.put(key,num++);
							Date first = DateUtils.getFirstDayOfWeek(year, week);
							tip.put(num-1,DateUtils.getDate(first,"yyyy-MM-dd")+","+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
							start = DateUtils.addWeeks(start, 1);
						}
				}
				
				
				String sql1="SELECT name_color,forecast_type,TYPE,week1,week2,week3,week4,week5,week6,week7,week8,week9,week10,week11,week12,week13,week14,week15,week16 FROM psi_inventory_gap where create_date=:p1 and country=:p2  AND TYPE IN ('8','9','10','11','12','13') ";
				if("0".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0) ";
				}
				if("1".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0 or week5<0 or week6<0 or week7<0 or week8<0) ";
				}
				if("2".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0 or week5<0 or week6<0 or week7<0 or week8<0 or week9<0 or week10<0 or week11<0 or week12<0) ";
				}
				if("3".equals(weekNum)){
					 sql1+=" and (week1<0 or week2<0 or week3<0 or week4<0 or week5<0 or week6<0 or week7<0 or week8<0 or week9<0 or week10<0 or week11<0 or week12<0 or week13<0 or week14<0 or week15<0 or week16<0) ";
				}
				sql1+=" union SELECT name_color,forecast_type,TYPE,week1,week2,week3,week4,week5,week6,week7,week8,week9,week10,week11,week12,week13,week14,week15,week16 FROM psi_inventory_gap where create_date=:p1 and country=:p2  AND TYPE IN ('1','2','3','4','5','6','7') ";
				List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql1,new Parameter(date,country));
				for (Object[] obj: list) {
					PsiInventoryGap gap=new PsiInventoryGap();
					String name=obj[0].toString();
					String type=obj[2].toString();
					String forecastType=obj[1].toString();
					gap.setNameColor(name);
					gap.setForecastType(forecastType);
					gap.setType(type);
					gap.setWeek1(obj[3]==null?0:Integer.parseInt(obj[3].toString()));
					gap.setWeek2(obj[4]==null?0:Integer.parseInt(obj[4].toString()));
					gap.setWeek3(obj[5]==null?0:Integer.parseInt(obj[5].toString()));
					gap.setWeek4(obj[6]==null?0:Integer.parseInt(obj[6].toString()));
					gap.setWeek5(obj[7]==null?0:Integer.parseInt(obj[7].toString()));
					gap.setWeek6(obj[8]==null?0:Integer.parseInt(obj[8].toString()));
					gap.setWeek7(obj[9]==null?0:Integer.parseInt(obj[9].toString()));
					gap.setWeek8(obj[10]==null?0:Integer.parseInt(obj[10].toString()));
					gap.setWeek9(obj[11]==null?0:Integer.parseInt(obj[11].toString()));
					gap.setWeek10(obj[12]==null?0:Integer.parseInt(obj[12].toString()));
					gap.setWeek11(obj[13]==null?0:Integer.parseInt(obj[13].toString()));
					gap.setWeek12(obj[14]==null?0:Integer.parseInt(obj[14].toString()));
					gap.setWeek13(obj[15]==null?0:Integer.parseInt(obj[15].toString()));
					gap.setWeek14(obj[16]==null?0:Integer.parseInt(obj[16].toString()));
					gap.setWeek15(obj[17]==null?0:Integer.parseInt(obj[17].toString()));
					gap.setWeek16(obj[18]==null?0:Integer.parseInt(obj[18].toString()));
					
					List<Date> dateList=Lists.newArrayList();
					Integer day=0;
					if("0".equals(weekNum)||"1".equals(weekNum)||"2".equals(weekNum)||"3".equals(weekNum)){
						if(gap.getWeek1()<0){
							String[] arr=tip.get(1).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek1());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek2()<0){
							String[] arr=tip.get(2).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek2());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek3()<0){
							String[] arr=tip.get(3).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek3());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek4()<0){
							String[] arr=tip.get(4).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek4());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if("1".equals(weekNum)||"2".equals(weekNum)||"3".equals(weekNum)){
						if(gap.getWeek5()<0){
							String[] arr=tip.get(5).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek5());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek6()<0){
							String[] arr=tip.get(6).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek6());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek7()<0){
							String[] arr=tip.get(7).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek7());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek8()<0){
							String[] arr=tip.get(8).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek8());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if("2".equals(weekNum)||"3".equals(weekNum)){
						if(gap.getWeek9()<0){
							String[] arr=tip.get(9).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek9());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek10()<0){
							String[] arr=tip.get(10).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek10());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek11()<0){
							String[] arr=tip.get(11).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek11());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek12()<0){
							String[] arr=tip.get(12).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek12());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if("3".equals(weekNum)){
						if(gap.getWeek13()<0){
							String[] arr=tip.get(13).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek13());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek14()<0){
							String[] arr=tip.get(14).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek14());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek15()<0){
							String[] arr=tip.get(15).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek15());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek16()<0){
							String[] arr=tip.get(16).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek16());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						
					}
					gap.setDay(day);
					StringBuilder time= new StringBuilder();
					
						List<Date> removeList=Lists.newArrayList();
						for(int i=2;i<dateList.size();i=i+2){
							Date beforeDate=dateList.get(i-1);
							Date afterDate=dateList.get(i);
							if(DateUtils.addDays(beforeDate, 1).equals(afterDate)){
								removeList.add(beforeDate);
								removeList.add(afterDate);
							}
						}
						dateList.removeAll(removeList);
						for(int i=0;i<dateList.size()-1;i=i+2){
							time.append(formatDay.format(dateList.get(i))).append("~").append(formatDay.format(dateList.get(i+1))).append(";");
						}
					    gap.setTime(time.toString());
					    
					    Map<String,Map<String,PsiInventoryGap>> typeMap=map.get(forecastType);
					    if(typeMap==null){
					    	typeMap=Maps.newHashMap();
					    	map.put(forecastType, typeMap);
					    }
						Map<String,PsiInventoryGap> temp=typeMap.get(name);
						/*if(temp!=null){
							String oldKey="";
							for(String oldType:temp.keySet()){
								if(Integer.parseInt(type)>Integer.parseInt(oldType)){
									oldKey=oldType;
									temp.put(type,gap);
									break;
								}
							}
							if(StringUtils.isNotBlank(oldKey)){
								temp.remove(oldKey);
							}
							continue;
						}*/
						if(temp==null){
							temp=Maps.newHashMap();
							typeMap.put(name,temp);
						}
						temp.put(type,gap);
					
				}
			}
		}
		
		Map<String,Map<String,Map<String,PsiInventoryGap>>> newMap=Maps.newHashMap();
		
		if(map!=null&&map.size()>0){
			for(Map.Entry<String, Map<String, Map<String, PsiInventoryGap>>>  entry3:map.entrySet()){
				String foreCastType = entry3.getKey();
				 Map<String,Map<String,PsiInventoryGap>> forecastMap=entry3.getValue();
				 for (Map.Entry<String,Map<String,PsiInventoryGap>> entry: forecastMap.entrySet()) {
					 String name = entry.getKey();
					 Map<String,PsiInventoryGap> typeMap=entry.getValue();
					 Set<String> set=typeMap.keySet();
					 Integer type=0;
					 for(String max:set){
						 if(Integer.parseInt(max)>=8&&Integer.parseInt(max)>=type){
							 type=Integer.parseInt(max);
						 }
					 }
					 if(type<8){
						 continue;
					 }
					 PsiInventoryGap newGap=typeMap.get(type+"");
					 PsiInventoryGap gap=typeMap.get((type-5)+"");
					 if(gap==null){
						 if(type==13){
							 newGap.setDesc("紧急增加PO");
						 }
					 }else{
						 if(type==13){
							    Integer num=0;
							    gap=typeMap.get((type-6)+"");
							    if(gap==null){
									 if(type==13){
										 newGap.setDesc("紧急增加PO");
									 }
								}else{
									Integer poQuantity=0;
									
									if("de".equals(country)){
										PsiProduct product=psiProductService.findProductByProductName(name);
										if(product!=null&&"0".equals(product.getHasPower())){
											if(poMap!=null&&poMap.get("eu")!=null&&poMap.get("eu").get(name)!=null){
												poQuantity=poMap.get("eu").get(name);
											}
										}else{
											if(poMap!=null&&poMap.get(country)!=null&&poMap.get(country).get(name)!=null){
												poQuantity=poMap.get(country).get(name);
											}
										}
									}else{
										if(poMap!=null&&poMap.get(country)!=null&&poMap.get(country).get(name)!=null){
											poQuantity=poMap.get(country).get(name);
										}
									}
									
									poMap.get(country).get(name);
									   String weekType="4周后";
									   if("0".equals(weekNum)){
									    	num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4();
									    	weekType="4周后";
										}
										if("1".equals(weekNum)){
											num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4()
												+gap.getWeek5()+gap.getWeek6()+gap.getWeek7()+gap.getWeek8();
											weekType="8周后";
										}
										if("2".equals(weekNum)){
											num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4()+
													+gap.getWeek5()+gap.getWeek6()+gap.getWeek7()+gap.getWeek8()
													+gap.getWeek9()+gap.getWeek10()+gap.getWeek11()+gap.getWeek12();
											weekType="12周后";
										}
										if("3".equals(weekNum)){
											num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4()+
													+gap.getWeek5()+gap.getWeek6()+gap.getWeek7()+gap.getWeek8()
													+gap.getWeek9()+gap.getWeek10()+gap.getWeek11()+gap.getWeek12()
													+gap.getWeek13()+gap.getWeek14()+gap.getWeek15()+gap.getWeek16();
											weekType="16周后";
										}
									if(poQuantity>num){
										newGap.setDesc(weekType+"可到货"+(poQuantity-num));
									}else{
										newGap.setDesc("紧急增加PO");
									}
							    }
							    
						 }else{
							    Integer num=0;
							    if("0".equals(weekNum)){
							    	num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4();
								}
								if("1".equals(weekNum)){
									num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4()
										+gap.getWeek5()+gap.getWeek6()+gap.getWeek7()+gap.getWeek8();
								}
								if("2".equals(weekNum)){
									num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4()+
											+gap.getWeek5()+gap.getWeek6()+gap.getWeek7()+gap.getWeek8()
											+gap.getWeek9()+gap.getWeek10()+gap.getWeek11()+gap.getWeek12();
								}
								if("3".equals(weekNum)){
									num=gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+gap.getWeek4()+
											+gap.getWeek5()+gap.getWeek6()+gap.getWeek7()+gap.getWeek8()
											+gap.getWeek9()+gap.getWeek10()+gap.getWeek11()+gap.getWeek12()
											+gap.getWeek13()+gap.getWeek14()+gap.getWeek15()+gap.getWeek16();
								}
								newGap.setDesc(newGap.getNextGapType()+num);
						 }
					 }
					 
					 Map<String,Map<String,PsiInventoryGap>> newTemp=newMap.get(foreCastType);
					 if(newTemp==null){
						 newTemp=Maps.newHashMap();
						 newMap.put(foreCastType, newTemp);
					 }
					 Map<String,PsiInventoryGap> newNameTemp=newTemp.get(name);
					 if(newNameTemp==null){
						 newNameTemp=Maps.newHashMap();
						 newTemp.put(name, newNameTemp);
					 }
					 newNameTemp.put(type+"", newGap);
				 }
			}
			
		}
		
		
		if(newMap!=null&&newMap.size()>0){
			 Map<String,Map<String,PsiInventoryGap>> forecastMap=newMap.get("0");
			//1.周日销
			 Map<String,Map<String,PsiInventoryGap>> weekMap=newMap.get("1");
			 Set<String> weekRemoveName=Sets.newHashSet();
			 if(weekMap!=null&&weekMap.size()>0){
//				 for (String name: weekMap.keySet()) {
				 for (Map.Entry<String,Map<String,PsiInventoryGap>> entry: weekMap.entrySet()) {
					 String name = entry.getKey();
					 Map<String,PsiInventoryGap> weekTypeMap=entry.getValue();
					 for(String type:weekTypeMap.keySet()){
						 if(forecastMap!=null&&forecastMap.get(name)!=null&&forecastMap.get(name).get(type)!=null){
							 weekRemoveName.add(name);
						 }
					 }
					 
				}
			 }
			 if(weekRemoveName!=null&&weekRemoveName.size()>0){
				 for(String key:weekRemoveName){
					 weekMap.remove(key);
				 }
			 }
			 
			 Map<String,Map<String,PsiInventoryGap>> forecastMap2=newMap.get("3");
			 Set<String> weekRemoveName2=Sets.newHashSet();
			 if(forecastMap2!=null&&forecastMap2.size()>0){
				 for (Map.Entry<String,Map<String,PsiInventoryGap>> entry: forecastMap2.entrySet()) {
					 String name = entry.getKey();
					 Map<String,PsiInventoryGap> weekTypeMap=entry.getValue();
					 for(String type:weekTypeMap.keySet()){
						 if(forecastMap!=null&&forecastMap.get(name)!=null&&forecastMap.get(name).get(type)!=null){
							 weekRemoveName2.add(name);
							 break;
						 }
						 if(weekMap!=null&&weekMap.get(name)!=null&&weekMap.get(name).get(type)!=null){
							 weekRemoveName2.add(name);
						 }
					 }
					 
				}
			 }
			 if(weekRemoveName2!=null&&weekRemoveName2.size()>0){
				 for(String key:weekRemoveName2){
					 forecastMap2.remove(key);
				 }
			 }
			 
			 
			 Map<String,Map<String,PsiInventoryGap>> weekMap2=newMap.get("2");//week-safe
			 Set<String> weekRemoveName3=Sets.newHashSet();
			 if(weekMap2!=null&&weekMap2.size()>0){
//				 for (String name: weekMap2.keySet()) {
				 for (Map.Entry<String,Map<String,PsiInventoryGap>> entry: weekMap2.entrySet()) {
					 String name = entry.getKey();
					 Map<String,PsiInventoryGap> weekTypeMap=entry.getValue();
					 for(String type:weekTypeMap.keySet()){
						 if(forecastMap!=null&&forecastMap.get(name)!=null&&forecastMap.get(name).get(type)!=null){
							 weekRemoveName3.add(name);
							 break;
						 }
						 if(weekMap!=null&&weekMap.get(name)!=null&&weekMap.get(name).get(type)!=null){
							 weekRemoveName3.add(name);
						 }
						 if(forecastMap2!=null&&forecastMap2.get(name)!=null&&forecastMap2.get(name).get(type)!=null){
							 weekRemoveName3.add(name);
						 }
					 }
				 }
			 }
			 if(weekRemoveName3!=null&&weekRemoveName3.size()>0){
				 for(String key:weekRemoveName3){
					 weekMap2.remove(key);
				 }
			 }
		}
		return newMap;
	}	
	
	public Map<String,Map<String,PsiInventoryGap>> findByCountryName(){
		Map<String,Map<String,PsiInventoryGap>>  map=Maps.newHashMap();
		//String sql="SELECT MAX(create_date) FROM psi_inventory_gap";
		String sql="SELECT create_date FROM psi_inventory_gap ORDER BY create_date DESC LIMIT 1";
		List<Object> rs=psiOutOfStockInfoDao.findBySql(sql);
		if(rs.size()>0){
			Date date=(Timestamp)rs.get(0);
			if(date!=null){
				String sql1="SELECT name_color,forecast_type,country,week1,week2,week3,week4,week5,week6,week7,week8,week9,week10,week11,week12,week13,week14,week15,week16 FROM psi_inventory_gap where create_date=:p1 and forecast_type in ('0','1') and type='1' ";
				List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql1,new Parameter(date));
				for (Object[] obj: list) {
					PsiInventoryGap gap=new PsiInventoryGap();
					String name=obj[0].toString();
					String country=obj[2].toString();
					String forecastType=obj[1].toString();
					gap.setNameColor(name);
					gap.setForecastType(forecastType);
					gap.setCountry(country);
					gap.setWeek1(obj[3]==null?0:Integer.parseInt(obj[3].toString()));
					gap.setWeek2(obj[4]==null?0:Integer.parseInt(obj[4].toString()));
					gap.setWeek3(obj[5]==null?0:Integer.parseInt(obj[5].toString()));
					gap.setWeek4(obj[6]==null?0:Integer.parseInt(obj[6].toString()));
					gap.setWeek5(obj[7]==null?0:Integer.parseInt(obj[7].toString()));
					gap.setWeek6(obj[8]==null?0:Integer.parseInt(obj[8].toString()));
					gap.setWeek7(obj[9]==null?0:Integer.parseInt(obj[9].toString()));
					gap.setWeek8(obj[10]==null?0:Integer.parseInt(obj[10].toString()));
					gap.setWeek9(obj[11]==null?0:Integer.parseInt(obj[11].toString()));
					gap.setWeek10(obj[12]==null?0:Integer.parseInt(obj[12].toString()));
					gap.setWeek11(obj[13]==null?0:Integer.parseInt(obj[13].toString()));
					gap.setWeek12(obj[14]==null?0:Integer.parseInt(obj[14].toString()));
					gap.setWeek13(obj[15]==null?0:Integer.parseInt(obj[15].toString()));
					gap.setWeek14(obj[16]==null?0:Integer.parseInt(obj[16].toString()));
					gap.setWeek15(obj[17]==null?0:Integer.parseInt(obj[17].toString()));
					gap.setWeek16(obj[18]==null?0:Integer.parseInt(obj[18].toString()));
					Map<String,PsiInventoryGap> temp=map.get(forecastType);
					if(temp==null){
						temp=Maps.newHashMap();
						map.put(forecastType,temp);
					}
					temp.put(name+"_"+country,gap);
				}
			}
		}	
		return map;
	}
	
	
	public Map<String,Map<String,PsiInventoryGap>> findAllGap(String country,String forecastType){
		Map<String,Map<String,PsiInventoryGap>> map=Maps.newHashMap();
	//	String sql="SELECT MAX(create_date) FROM psi_inventory_gap where country=:p1 and forecast_type=:p2 ";
		String sql="SELECT create_date FROM psi_inventory_gap where country=:p1 and forecast_type=:p2  ORDER BY create_date DESC LIMIT 1";
		List<Object> rs=psiOutOfStockInfoDao.findBySql(sql,new Parameter(country,forecastType));
		if(rs.size()>0){
			Date date=(Timestamp)rs.get(0);
			if(date!=null){
				String sql1="SELECT name_color,forecast_type,TYPE,week1,week2,week3,week4,week5,week6,week7,week8,week9,week10,week11,week12,week13,week14,week15,week16 FROM psi_inventory_gap where create_date=:p1 and country=:p2 and forecast_type=:p3 ";
				List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql1,new Parameter(date,country,forecastType));
				for (Object[] obj: list) {
					PsiInventoryGap gap=new PsiInventoryGap();
					String name=obj[0].toString();
					String type=obj[2].toString();
					gap.setNameColor(name);
					gap.setForecastType(forecastType);
					gap.setType(type);
					gap.setWeek1(obj[3]==null?0:Integer.parseInt(obj[3].toString()));
					gap.setWeek2(obj[4]==null?0:Integer.parseInt(obj[4].toString()));
					gap.setWeek3(obj[5]==null?0:Integer.parseInt(obj[5].toString()));
					gap.setWeek4(obj[6]==null?0:Integer.parseInt(obj[6].toString()));
					gap.setWeek5(obj[7]==null?0:Integer.parseInt(obj[7].toString()));
					gap.setWeek6(obj[8]==null?0:Integer.parseInt(obj[8].toString()));
					gap.setWeek7(obj[9]==null?0:Integer.parseInt(obj[9].toString()));
					gap.setWeek8(obj[10]==null?0:Integer.parseInt(obj[10].toString()));
					gap.setWeek9(obj[11]==null?0:Integer.parseInt(obj[11].toString()));
					gap.setWeek10(obj[12]==null?0:Integer.parseInt(obj[12].toString()));
					gap.setWeek11(obj[13]==null?0:Integer.parseInt(obj[13].toString()));
					gap.setWeek12(obj[14]==null?0:Integer.parseInt(obj[14].toString()));
					gap.setWeek13(obj[15]==null?0:Integer.parseInt(obj[15].toString()));
					gap.setWeek14(obj[16]==null?0:Integer.parseInt(obj[16].toString()));
					gap.setWeek15(obj[17]==null?0:Integer.parseInt(obj[17].toString()));
					gap.setWeek16(obj[18]==null?0:Integer.parseInt(obj[18].toString()));
					Map<String,PsiInventoryGap> temp=map.get(name);
					if(temp==null){
						temp=Maps.newHashMap();
						map.put(name,temp);
					}
					temp.put(type,gap);
				}
			}
		}	
		return map;
	}
	
	//FBA运输
	public Map<String,Map<String,Integer>> getFbaTrans(){
		Map<String,Map<String,Integer>> map=Maps.newHashMap();
		String sql="SELECT CONCAT(i.`product_name`,CASE  WHEN i.`color_code`='' THEN '' ELSE CONCAT('_',i.`color_code`) END),i.country_code,SUM(i.`quantity`) AS quantity "+
				" FROM psi_transport_order AS a ,psi_transport_order_item AS i "+
				" WHERE a.`id`=i.`transport_order_id` and i.del_flag='0' AND a.transport_sta IN ('1','2','3') AND a.`transport_type`='1' GROUP BY CONCAT(i.`product_name`,CASE  WHEN i.`color_code`='' THEN '' ELSE CONCAT('_',i.`color_code`) END),i.country_code ";
		List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql);
		for (Object[] obj: list) {
			String name=obj[0].toString();
			String country=obj[1].toString();
			Integer quantity=Integer.parseInt(obj[2].toString());
			Map<String,Integer> temp=map.get(country);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(country, temp);
			}
			temp.put(name,quantity);
			if("fr,uk,es,it,de".contains(country)){
				Map<String,Integer> euTemp=map.get("eu");
				if(euTemp==null){
					euTemp=Maps.newHashMap();
					map.put("eu",euTemp);
				}
				euTemp.put(name,(euTemp.get(name)==null?0:euTemp.get(name))+quantity);
			}
		}
		return map;
	}
	
	    //本地运输
		public Map<String,Map<String,Integer>> getLocalTrans(){
			Map<String,Map<String,Integer>> map=Maps.newHashMap();
			String sql="SELECT CONCAT(i.`product_name`,CASE  WHEN i.`color_code`='' THEN '' ELSE CONCAT('_',i.`color_code`) END),i.country_code,SUM(i.`quantity`) AS quantity "+
					" FROM psi_transport_order AS a ,psi_transport_order_item AS i "+
					" WHERE a.`id`=i.`transport_order_id` and i.del_flag='0' AND a.transport_sta IN ('1','2','3') AND a.`transport_type`='0' GROUP BY CONCAT(i.`product_name`,CASE  WHEN i.`color_code`='' THEN '' ELSE CONCAT('_',i.`color_code`) END),i.country_code ";
			List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql);
			for (Object[] obj: list) {
				String name=obj[0].toString();
				String country=obj[1].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				Map<String,Integer> temp=map.get(country);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(country, temp);
				}
				temp.put(name,quantity);
				if("fr,uk,es,it,de".contains(country)){
					Map<String,Integer> euTemp=map.get("eu");
					if(euTemp==null){
						euTemp=Maps.newHashMap();
						map.put("eu",euTemp);
					}
					euTemp.put(name,(euTemp.get(name)==null?0:euTemp.get(name))+quantity);
				}
			}
			return map;
		}
		
		public Map<String,List<String>> getLoclaTransDetail(){
			SimpleDateFormat formatDay=new SimpleDateFormat("yyyy-MM-dd");
			//1.在途  本地运输0
			Map<String,List<String>> localTrans=Maps.newHashMap();
			String sql4="SELECT CONCAT(i.`product_name`,CASE  WHEN i.`color_code`='' THEN '' ELSE CONCAT('_',i.`color_code`) END),i.country_code,a.model,SUM(i.`quantity`) AS quantity,IFNULL(a.`pre_eta_date`,a.`pick_up_date`),(case when a.`pre_eta_date` is null then '0' else '1' end) "+
					" FROM psi_transport_order AS a ,psi_transport_order_item AS i "+
					" WHERE a.`id`=i.`transport_order_id` and i.del_flag='0' AND a.transport_sta IN ('1','2','3') AND a.`transport_type`='0' GROUP BY CONCAT(i.`product_name`,CASE  WHEN i.`color_code`='' THEN '' ELSE CONCAT('_',i.`color_code`) END),i.country_code,a.model,IFNULL(a.`pre_eta_date`,a.`pick_up_date`),(case when a.`pre_eta_date` is null then '0' else '1' end) ";
		
			List<Object[]> list4=psiOutOfStockInfoDao.findBySql(sql4);
			for (Object[] obj : list4) {
				String name=obj[0].toString();
				String country=obj[1].toString();
				String model=obj[2].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String type=obj[5].toString();
				Date forecast=null;
				if("0".equals(type)){//预计到达时间为空
					if("0".equals(model)){//Air
						forecast=DateUtils.addDays((Date)obj[4],PsiConfig.get(country).getTransportBySky());
					}else if("1".equals(model)){//Sea
						forecast=DateUtils.addDays((Date)obj[4],PsiConfig.get(country).getTransportBySea());
					}else{
						forecast=DateUtils.addDays((Date)obj[4],7);
					}
				}else{
					if("0".equals(model)){//Air
						forecast=DateUtils.addDays((Timestamp)obj[4],PsiConfig.get(country).getWareHouseBySky());
					}else if("1".equals(model)){//Sea
						forecast=DateUtils.addDays((Timestamp)obj[4],PsiConfig.get(country).getWareHouseBySea());
					}else{
						forecast=DateUtils.addDays((Timestamp)obj[4],3);
					}
				}
				if(forecast.after(DateUtils.addDays(new Date(),21))){
					continue;
				}
				if(forecast.before(new Date())){
					forecast=new Date();
				}
				List<String> temp=localTrans.get(name+"_"+country);
				if(temp==null){
					temp=Lists.newArrayList();
					localTrans.put(name+"_"+country,temp);
				}
				temp.add(formatDay.format(forecast)+","+country+","+("0".equals(model)?"空运":("1".equals(model)?"海运":"快递"))+",数量："+quantity);
			}
			return localTrans;
		}
	
		//德国仓
		public Map<String,Map<String,Integer>> getDeStock(){
					Map<String,Map<String,Integer>> map=Maps.newHashMap();
					String sql="SELECT CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END),t.country_code,SUM(t.new_quantity) "+
							" FROM psi_inventory t  JOIN psi_product_attribute p ON CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)= CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END) AND p.del_flag='0' "+
							" WHERE new_quantity>0 AND t.warehouse_id=19 GROUP BY   CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END),t.country_code  ";
				   
                    List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql);
					for (Object[] obj: list) {
						String name=obj[0].toString();
						String country=obj[1].toString();
						Integer quantity=Integer.parseInt(obj[2].toString());
						Map<String,Integer> temp=map.get(country);
						if(temp==null){
							temp=Maps.newHashMap();
							map.put(country, temp);
						}
						temp.put(name,quantity);
						if("fr,uk,es,it,de".contains(country)){
							Map<String,Integer> euTemp=map.get("eu");
							if(euTemp==null){
								euTemp=Maps.newHashMap();
								map.put("eu",euTemp);
							}
							euTemp.put(name,(euTemp.get(name)==null?0:euTemp.get(name))+quantity);
						}
					}
					return map;
		}
		
		//US仓
				public Map<String,Map<String,Integer>> getUsStock(){
							Map<String,Map<String,Integer>> map=Maps.newHashMap();
							String sql="SELECT CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END),t.country_code,SUM(t.new_quantity) "+
									" FROM psi_inventory t  JOIN psi_product_attribute p ON CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)= CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END) AND p.del_flag='0' "+
									" WHERE new_quantity>0 AND t.warehouse_id=120 GROUP BY   CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END),t.country_code  ";
						   
		                    List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql);
							for (Object[] obj: list) {
								String name=obj[0].toString();
								String country=obj[1].toString();
								Integer quantity=Integer.parseInt(obj[2].toString());
								Map<String,Integer> temp=map.get(country);
								if(temp==null){
									temp=Maps.newHashMap();
									map.put(country, temp);
								}
								temp.put(name,quantity);
							}
							return map;
				}
		
		//中国仓
				public Map<String,Map<String,Integer>> getCnStock(){
							Map<String,Map<String,Integer>> map=Maps.newHashMap();
							  String sql="SELECT CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END),t.country_code,SUM(t.new_quantity) "+
										" FROM psi_inventory t  JOIN psi_product_attribute p ON CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)= CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END) AND p.del_flag='0' "+
										" WHERE new_quantity>0 AND t.warehouse_id=21 GROUP BY   CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END),t.country_code ";
							    
		                    List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql);
							for (Object[] obj: list) {
								String name=obj[0].toString();
								String country=obj[1].toString();
								Integer quantity=Integer.parseInt(obj[2].toString());
								Map<String,Integer> temp=map.get(country);
								if(temp==null){
									temp=Maps.newHashMap();
									map.put(country, temp);
								}
								temp.put(name,quantity);
								if("fr,uk,es,it,de".contains(country)){
									Map<String,Integer> euTemp=map.get("eu");
									if(euTemp==null){
										euTemp=Maps.newHashMap();
										map.put("eu",euTemp);
									}
									euTemp.put(name,(euTemp.get(name)==null?0:euTemp.get(name))+quantity);
								}
							}
							return map;
				}	
	
				//采购仓
				public Map<String,Map<String,Integer>> getPoStock(){
							Map<String,Map<String,Integer>> map=Maps.newHashMap();
							//String sql="SELECT CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END),t.`country_code`,SUM(t.quantity_ordered-t.quantity_received) "+
							 //          " FROM psi_purchase_order r JOIN psi_purchase_order_item t  ON r.id=t.`purchase_order_id` and t.del_flag='0' "+
				            //           " JOIN psi_product_attribute p ON CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)=CONCAT(t.product_name,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.color_code) ELSE '' END)  AND p.del_flag='0' "+
							//           " WHERE r.order_sta='2' OR (r.order_sta='3' AND t.quantity_ordered!=t.quantity_received) GROUP BY CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END),t.`country_code` ";
							 
							String sql ="SELECT name,country_code,SUM(orderNum) FROM (" +
									"SELECT  CONCAT(b.`product_name`,CASE  WHEN b.`color_code`='' THEN '' ELSE CONCAT('_',b.`color_code`) END) name,b.`country_code`," +
									" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum" +
									" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b " +
									" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('2','3')  and b.`del_flag`='0' " +
									" GROUP BY name,b.country_code  "+
									" UNION ALL SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color_code`='' THEN '' ELSE CONCAT('_',b.`color_code`) END) name,b.`country_code`," +
									" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum" +
									" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b " +
									" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('2','3')  and b.`del_flag`='0' " +
									" GROUP BY name,b.country_code) AS t GROUP BY name,country_code ";	
		                    List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql);
							for (Object[] obj: list) {
								String name=obj[0].toString();
								String country=obj[1].toString();
								Integer quantity=Integer.parseInt(obj[2].toString());
								Map<String,Integer> temp=map.get(country);
								if(temp==null){
									temp=Maps.newHashMap();
									map.put(country, temp);
								}
								temp.put(name,quantity);
								if("fr,uk,es,it,de".contains(country)){
									Map<String,Integer> euTemp=map.get("eu");
									if(euTemp==null){
										euTemp=Maps.newHashMap();
										map.put("eu",euTemp);
									}
									euTemp.put(name,(euTemp.get(name)==null?0:euTemp.get(name))+quantity);
								}
							}
							return map;
				}	
				
	public Map<String,Map<String,Map<String,Integer>>> findGapInfo(){
		   Map<String,Map<String,Map<String,Integer>>> totalMap=Maps.newHashMap();
		   DateFormat formatWeek = new SimpleDateFormat("yyyyww");
			 //亚马逊仓   
			// Map<String,PsiInventoryFba> amazonStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
			 //Map<String,PsiInventoryFba>  getProductFbaInfo(String productName);//productName+"_"+eu
			

           //亚马逊在途  FBA运输 CN
			Map<String,Map<String,Integer>> fbaTransMap2=Maps.newHashMap();
			String sql2="SELECT CONCAT(i.`product_name`,CASE  WHEN i.`color_code`='' THEN '' ELSE CONCAT('_',i.`color_code`) END),i.country_code,a.model,SUM(i.`quantity`) AS quantity,IFNULL(a.`pre_eta_date`,a.`pick_up_date`),(case when a.`pre_eta_date` is null then '0' else '1' end) "+
						" FROM psi_transport_order AS a ,psi_transport_order_item AS i "+
						" WHERE a.`id`=i.`transport_order_id` and i.del_flag='0' AND a.transport_sta IN ('1','2','3') AND a.`transport_type`='1' GROUP BY CONCAT(i.`product_name`,CASE  WHEN i.`color_code`='' THEN '' ELSE CONCAT('_',i.`color_code`) END),i.country_code,a.model,IFNULL(a.`pre_eta_date`,a.`pick_up_date`),(case when a.`pre_eta_date` is null then '0' else '1' end) ";
					
		    List<Object[]> list2=psiOutOfStockInfoDao.findBySql(sql2);
			for (Object[] obj : list2) {
				String name=obj[0].toString();
				String country=obj[1].toString();
				String model=obj[2].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String type=obj[5].toString();
				Date forecast=null;
				if("0".equals(type)){//预计到达时间为空
					if("0".equals(model)){//Air
						forecast=DateUtils.addDays((Date)obj[4],PsiConfig.get(country).getTransportBySky());
					}else if("1".equals(model)){//Sea
						forecast=DateUtils.addDays((Date)obj[4],PsiConfig.get(country).getTransportBySea());
					}else{
						forecast=DateUtils.addDays((Date)obj[4],7);
					}
				}else{
					forecast=(Timestamp)obj[4];
				}
				Map<String,Integer> temp=fbaTransMap2.get(name+"_"+country);
				if(temp==null){
					temp=Maps.newHashMap();
					fbaTransMap2.put(name+"_"+country,temp);
				}
				if(forecast.before(new Date())){
					forecast=new Date();
				}
				if(forecast.getDay()==0){
					forecast = DateUtils.addDays(forecast, -1);
				}
				String key = formatWeek.format(forecast);
				int year =DateUtils.getSunday(forecast).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(forecast).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				Integer quantityBydate=temp.get(key);
				temp.put(key, quantity+(quantityBydate==null?0:quantityBydate));
				
				if("fr,uk,es,it,de".contains(obj[1].toString())){
					Map<String,Integer> euTemp=fbaTransMap2.get(name+"_eu");
					if(euTemp==null){
						euTemp=Maps.newHashMap();
						fbaTransMap2.put(name+"_eu",euTemp);
					}
					Integer euQuantityBydate=euTemp.get(key);
					euTemp.put(key, quantity+(euQuantityBydate==null?0:euQuantityBydate));
					
				}
			}	
			
			//亚马逊在途  FBA运输 DE US
			String fbaTran="SELECT c.productName,c.country,SUM(CASE  WHEN a.country IN ('de','fr','uk') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -7 DAY) THEN 0 ELSE (b.`quantity_shipped`-b.`quantity_received`) END ) AS tranFba FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0')c "+  
			  " WHERE a.`id` = b.`fba_inbound_id` AND ship_from_address IN ('US','DE') AND a.shipped_date IS NOT NULL  AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` GROUP BY c.productName,c.country  HAVING tranFba >0  ";
			List<Object[]> fabList=psiOutOfStockInfoDao.findBySql(fbaTran);
			for (Object[] obj: fabList) {
				String name=obj[0].toString();
				String country=obj[1].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				Date forecast= DateUtils.addDays(new Date(),5);
				Map<String,Integer> temp=fbaTransMap2.get(name+"_"+country);
				if(temp==null){
					temp=Maps.newHashMap();
					fbaTransMap2.put(name+"_"+country,temp);
				}
				
				if(forecast.getDay()==0){
					forecast = DateUtils.addDays(forecast, -1);
				}
				String key = formatWeek.format(forecast);
				int year =DateUtils.getSunday(forecast).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(forecast).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				Integer quantityBydate=temp.get(key);
				temp.put(key, quantity+(quantityBydate==null?0:quantityBydate));
				
				if("fr,uk,es,it,de".contains(obj[1].toString())){
					Map<String,Integer> euTemp=fbaTransMap2.get(name+"_eu");
					if(euTemp==null){
						euTemp=Maps.newHashMap();
						fbaTransMap2.put(name+"_eu",euTemp);
					}
					Integer euQuantityBydate=euTemp.get(key);
					euTemp.put(key, quantity+(euQuantityBydate==null?0:euQuantityBydate));
				}
			}
			
			totalMap.put("0", fbaTransMap2);
			
			
			//本地仓 计算德国美国仓
			Map<String,Map<String,Integer>> localStock=Maps.newHashMap();
			String sql3="SELECT CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END),t.country_code,SUM(t.new_quantity),p.`transport_type` "+
					" FROM psi_inventory t  JOIN psi_product_eliminate p ON CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)= CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END) AND p.del_flag='0' AND t.`country_code`=p.`country` "+
					" WHERE new_quantity>0 AND t.warehouse_id in (19,120) GROUP BY   CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END),t.country_code,p.`transport_type`  ";
		    List<Object[]> list3=psiOutOfStockInfoDao.findBySql(sql3);   
			for (Object[] obj : list3) {
				String name=obj[0].toString();
				String country=obj[1].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
			    String type=(obj[3]==null?"1":obj[3].toString());
			    Date forecast=new Date();
			    if("1".equals(type)){
			    	 forecast=DateUtils.addDays((Date)forecast,8);
			    }else{
			         forecast=DateUtils.addDays((Date)forecast,8);
			    }
			    Map<String,Integer> temp=localStock.get(name+"_"+country);
				if(temp==null){
					temp=Maps.newHashMap();
					localStock.put(name+"_"+country,temp);
				}
				if(forecast.before(new Date())){
					forecast=new Date();
				}
				if(forecast.getDay()==0){
					forecast = DateUtils.addDays(forecast, -1);
				}
				String key = formatWeek.format(forecast);
				int year =DateUtils.getSunday(forecast).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(forecast).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				Integer quantityBydate=temp.get(key);
				temp.put(key, quantity+(quantityBydate==null?0:quantityBydate));
				
				if("fr,uk,es,it,de".contains(country)){
					Map<String,Integer> euTemp=localStock.get(name+"_eu");
					if(euTemp==null){
						euTemp=Maps.newHashMap();
						localStock.put(name+"_eu",euTemp);
					}
					Integer euQuantityBydate=euTemp.get(key);
					euTemp.put(key, quantity+(euQuantityBydate==null?0:euQuantityBydate));
					
				}
			}
			totalMap.put("1", localStock);
			//1.在途  本地运输0
			Map<String,Map<String,Integer>> localTrans=Maps.newHashMap();
			String sql4="SELECT CONCAT(i.`product_name`,CASE  WHEN i.`color_code`='' THEN '' ELSE CONCAT('_',i.`color_code`) END),i.country_code,a.model,SUM(i.`quantity`) AS quantity,IFNULL(a.`pre_eta_date`,a.`pick_up_date`),(case when a.`pre_eta_date` is null then '0' else '1' end) "+
					" FROM psi_transport_order AS a ,psi_transport_order_item AS i "+
					" WHERE a.`id`=i.`transport_order_id` and i.del_flag='0' AND a.transport_sta IN ('1','2','3') AND a.`transport_type`='0' GROUP BY CONCAT(i.`product_name`,CASE  WHEN i.`color_code`='' THEN '' ELSE CONCAT('_',i.`color_code`) END),i.country_code,a.model,IFNULL(a.`pre_eta_date`,a.`pick_up_date`),(case when a.`pre_eta_date` is null then '0' else '1' end) ";
		
			List<Object[]> list4=psiOutOfStockInfoDao.findBySql(sql4);
			for (Object[] obj : list4) {
				String name=obj[0].toString();
				String country=obj[1].toString();
				String model=obj[2].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String type=obj[5].toString();
				Date forecast=null;
				if("0".equals(type)){//预计到达时间为空
					if("0".equals(model)){//Air
						forecast=DateUtils.addDays((Date)obj[4],PsiConfig.get(country).getTransportBySky());
					}else if("1".equals(model)){//Sea
						forecast=DateUtils.addDays((Date)obj[4],PsiConfig.get(country).getTransportBySea());
					}else{
						forecast=DateUtils.addDays((Date)obj[4],7);
					}
				}else{
					if("0".equals(model)){//Air
						forecast=DateUtils.addDays((Timestamp)obj[4],PsiConfig.get(country).getWareHouseBySky());
					}else if("1".equals(model)){//Sea
						forecast=DateUtils.addDays((Timestamp)obj[4],PsiConfig.get(country).getWareHouseBySea());
					}else{
						forecast=DateUtils.addDays((Timestamp)obj[4],3);
					}
				}
				Map<String,Integer> temp=localTrans.get(name+"_"+country);
				if(temp==null){
					temp=Maps.newHashMap();
					localTrans.put(name+"_"+country,temp);
				}
				if(forecast.before(new Date())){
					forecast=new Date();
				}
				if(forecast.getDay()==0){
					forecast = DateUtils.addDays(forecast, -1);
				}
				String key = formatWeek.format(forecast);
				int year =DateUtils.getSunday(forecast).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(forecast).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				Integer quantityBydate=temp.get(key);
				temp.put(key, quantity+(quantityBydate==null?0:quantityBydate));
				
				if("fr,uk,es,it,de".contains(country)){
					Map<String,Integer> euTemp=localTrans.get(name+"_eu");
					if(euTemp==null){
						euTemp=Maps.newHashMap();
						localTrans.put(name+"_eu",euTemp);
					}
					Integer euQuantityBydate=euTemp.get(key);
					euTemp.put(key, quantity+(euQuantityBydate==null?0:euQuantityBydate));
				}
				
			}
			totalMap.put("2", localTrans);
			//CN仓
			Map<String,Map<String,Integer>> cnTrans=Maps.newHashMap();
		   // String sql5="SELECT CONCAT(y.`product_name`,CASE  WHEN y.`color_code`='' THEN '' ELSE CONCAT('_',y.`color_code`) END),y.country_code,SUM(y.new_quantity),p.`transport_type` "+
		   //         " FROM psi_inventory Y  JOIN psi_product_attribute p ON CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)=CONCAT(y.`product_name`,CASE  WHEN y.`color_code`='' THEN '' ELSE CONCAT('_',y.`color_code`) END) AND p.del_flag='0' "+
		   //         " WHERE new_quantity>0 AND y.warehouse_id=21 GROUP BY  CONCAT(y.`product_name`,CASE  WHEN y.`color_code`='' THEN '' ELSE CONCAT('_',y.`color_code`) END),y.country_code,p.`transport_type` ";
		    String sql5="SELECT CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END),t.country_code,SUM(t.new_quantity),p.`transport_type` "+
					" FROM psi_inventory t  JOIN psi_product_eliminate p ON CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)= CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END) AND p.del_flag='0' AND t.`country_code`=p.`country`  "+
					" WHERE new_quantity>0 AND t.warehouse_id=21 GROUP BY   CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END),t.country_code,p.`transport_type`  ";
		  
		    
			List<Object[]> list5=psiOutOfStockInfoDao.findBySql(sql5);   
			for (Object[] obj : list5) {
				String name=obj[0].toString();
				String country=obj[1].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
			    String type=(obj[3]==null?"1":obj[3].toString());
			    Date forecast=new Date();
			    if("1".equals(type)){
			    	forecast=DateUtils.addDays((Date)forecast,PsiConfig.get(country).getTransportBySea());
			    }else{
			    	forecast=DateUtils.addDays((Date)forecast,PsiConfig.get(country).getTransportBySky());
			    }
			    Map<String,Integer> temp=cnTrans.get(name+"_"+country);
				if(temp==null){
					temp=Maps.newHashMap();
					cnTrans.put(name+"_"+country,temp);
				}
				if(forecast.before(new Date())){
					forecast=new Date();
				}
				if(forecast.getDay()==0){
					forecast = DateUtils.addDays(forecast, -1);
				}
				String key = formatWeek.format(forecast);
				int year =DateUtils.getSunday(forecast).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(forecast).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				Integer quantityBydate=temp.get(key);
				temp.put(key, quantity+(quantityBydate==null?0:quantityBydate));
				
				if("fr,uk,es,it,de".contains(country)){
					Map<String,Integer> euTemp=cnTrans.get(name+"_eu");
					if(euTemp==null){
						euTemp=Maps.newHashMap();
						cnTrans.put(name+"_eu",euTemp);
					}
					Integer euQuantityBydate=euTemp.get(key);
					euTemp.put(key, quantity+(euQuantityBydate==null?0:euQuantityBydate));
				} 
			}
			totalMap.put("3", cnTrans);
			  //在产
			  Map<String,Map<String,Integer>> productingMap=Maps.newHashMap();
			 /* String sql6="SELECT CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END),t.`country_code`,SUM(t.quantity_ordered-t.quantity_received),t.`delivery_date`,p.`transport_type` "+
			           " FROM psi_purchase_order r JOIN psi_purchase_order_item t  ON r.id=t.`purchase_order_id` and t.del_flag='0' "+
                       " JOIN psi_product_attribute p ON CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)=CONCAT(t.product_name,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.color_code) ELSE '' END)  AND p.del_flag='0' "+
			           " WHERE r.order_sta='2' OR (r.order_sta='3' AND t.quantity_ordered!=t.quantity_received) GROUP BY CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END),t.`country_code`,t.`delivery_date`,p.`transport_type` "*/;
			 String sql6 ="SELECT name,country_code,SUM(orderNum),`delivery_date`,`transport_type` FROM (" +
								"SELECT  CONCAT(b.`product_name`,CASE  WHEN b.`color_code`='' THEN '' ELSE CONCAT('_',b.`color_code`) END) name,b.`country_code`," +
								" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum,b.`delivery_date`,p.`transport_type` " +
								" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b,psi_product_eliminate p " +
								" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('2','3')  and b.`del_flag`='0' " +
								" and CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)=CONCAT(b.product_name,CASE WHEN b.`color_code`!='' THEN CONCAT ('_',b.color_code) ELSE '' END)  AND p.del_flag='0' AND b.`country_code`=p.`country`  "+
								" GROUP BY name,b.country_code,b.`delivery_date`,p.`transport_type`  "+
								" UNION ALL SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color_code`='' THEN '' ELSE CONCAT('_',b.`color_code`) END) name,b.`country_code`," +
								" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum,b.`delivery_date`,p.`transport_type` " +
								" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b,psi_product_eliminate p " +
								" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('2','3')  and b.`del_flag`='0' " +
								" and CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)=CONCAT(b.product_name,CASE WHEN b.`color_code`!='' THEN CONCAT ('_',b.color_code) ELSE '' END)  AND p.del_flag='0' AND b.`country_code`=p.`country` "+
								" GROUP BY name,b.country_code,b.`delivery_date`,p.`transport_type`) AS t GROUP BY name,country_code,`delivery_date`,`transport_type` ";	
			  List<Object[]> list6=psiOutOfStockInfoDao.findBySql(sql6);   
			  for (Object[] obj : list6) {
				    String name=obj[0].toString();
					String country=obj[1].toString();
					Integer quantity=Integer.parseInt(obj[2].toString());
				    String type=(obj[4]==null?"1":obj[4].toString());
				    Date forecast=null;
				    //1海运  其他空运
				    if("1".equals(type)){
				    	forecast=DateUtils.addDays((Date)obj[3],PsiConfig.get(country).getTransportBySea());
				    }else{
				    	forecast=DateUtils.addDays((Date)obj[3],PsiConfig.get(country).getTransportBySky());
				    }	
				    Map<String,Integer> temp=productingMap.get(name+"_"+country);
					if(temp==null){
						temp=Maps.newHashMap();
						productingMap.put(name+"_"+country,temp);
					}
					if(forecast.before(new Date())){
						forecast=new Date();
					}
					if(forecast.getDay()==0){
						forecast = DateUtils.addDays(forecast, -1);
					}
					String key = formatWeek.format(forecast);
					int year =DateUtils.getSunday(forecast).getYear()+1900;
					int week =  Integer.parseInt(key.substring(4));
					if(week==53){
		                year =DateUtils.getMonday(forecast).getYear()+1900;
				    }
					if(week<10){
						key = year+"0"+week;
					}else{
						key =year+""+week;
					}
					Integer quantityBydate=temp.get(key);
					temp.put(key, quantity+(quantityBydate==null?0:quantityBydate));
					
					if("fr,uk,es,it,de".contains(country)){
						Map<String,Integer> euTemp=productingMap.get(name+"_eu");
						if(euTemp==null){
							euTemp=Maps.newHashMap();
							productingMap.put(name+"_eu",euTemp);
						}
						Integer euQuantityBydate=euTemp.get(key);
						euTemp.put(key, quantity+(euQuantityBydate==null?0:euQuantityBydate));
					} 	   
		      }
			  totalMap.put("4", productingMap);
			  return totalMap;
	    }
	
	//三周欧洲FBA在途balance低于安全库存的型号
	public Map<String,String> getEuCountryThreeWeekGap(){
		Map<String,String> map=Maps.newHashMap();
       // String sql="SELECT MAX(create_date) FROM psi_inventory_gap where TYPE IN ('8','9','10','11','12','13')";
		 String sql="SELECT create_date FROM psi_inventory_gap where TYPE IN ('8','9','10','11','12','13')  ORDER BY create_date DESC LIMIT 1 ";
		List<Object> rs=psiOutOfStockInfoDao.findBySql(sql);
		if(rs.size()>0){
			Date date=(Timestamp)rs.get(0);
			if(date!=null){
				String sql1="SELECT distinct name_color,country FROM psi_inventory_gap where create_date=:p1  AND `forecast_type` IN ('2','3') AND TYPE='9' "
				          +" and (week1<0 or week2<0 or week3<0) and country in ('de','fr','uk','it','es')";
				List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql1,new Parameter(date));
				for (Object[] obj: list) {
					map.put(obj[0].toString()+"_"+obj[1].toString(),"1");
				}
			}
		}	
		return map;
	}
	
	public Map<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>> findEuCountryGap(){
		Map<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>> map=Maps.newHashMap();
	//	String sql="SELECT MAX(create_date) FROM psi_inventory_gap where TYPE IN ('8','9','10','11','12','13')";
		 String sql="SELECT create_date FROM psi_inventory_gap where TYPE IN ('8','9','10','11','12','13')  ORDER BY create_date DESC LIMIT 1 ";
		List<Object> rs=psiOutOfStockInfoDao.findBySql(sql);
		if(rs.size()>0){
			Date date=(Timestamp)rs.get(0);
			if(date!=null){
				Date createDate=new Date();
				Date start = new Date();
				start.setHours(0);
				start.setMinutes(0);
				start.setSeconds(0);
				Date end = DateUtils.addMonths(start,5);
				DateFormat formatWeek = new SimpleDateFormat("yyyyww");
				DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
				if(start.getDay()==0){
					start = DateUtils.addDays(start, -1);
				}
				Map<String,Integer> weekList=Maps.newLinkedHashMap();
				Map<Integer, String> tip = Maps.newHashMap();
				int num=1;
				while(end.after(start)||end.equals(start)){
						String key = formatWeek.format(start);
						int year =DateUtils.getSunday(start).getYear()+1900;
						int week =  Integer.parseInt(key.substring(4));
						if(week==53){
			                year =DateUtils.getMonday(start).getYear()+1900;
					    }
						if(week<10){
							key = year+"0"+week;
						}else{
							key =year+""+week;
						}
						
						if(weekList.size()>16){
							break;
						}else{
							weekList.put(key,num++);
							Date first = DateUtils.getFirstDayOfWeek(year, week);
							tip.put(num-1,DateUtils.getDate(first,"yyyy-MM-dd")+","+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
							start = DateUtils.addWeeks(start, 1);
						}
				}
				
				
				String sql1="SELECT name_color,country,TYPE,week1,week2,week3,week4,week5,week6,week7,week8,week9,week10,week11,week12,week13,week14,week15,week16,forecast_type FROM psi_inventory_gap where create_date=:p1  AND `forecast_type` IN ('2','3') AND TYPE='9' ";
				sql1+=" and (week1<0 or week2<0 or week3<0) and country in ('de','fr','uk','it','es')";
				sql1+=" union SELECT name_color,country,TYPE,week1,week2,week3,week4,week5,week6,week7,week8,week9,week10,week11,week12,week13,week14,week15,week16,forecast_type FROM psi_inventory_gap where create_date=:p1 AND `forecast_type` IN ('2','3') AND  TYPE='3'  and country in ('de','fr','uk','it','es') ";
				
				List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql1,new Parameter(date));
				for (Object[] obj: list) {
					PsiInventoryGap gap=new PsiInventoryGap();
					String name=obj[0].toString();
					String country=obj[1].toString();
					String type=obj[2].toString();
					String forecastType=obj[19].toString();
					gap.setNameColor(name);
					gap.setForecastType(forecastType);
					gap.setType(type);
					gap.setWeek1(obj[3]==null?0:Integer.parseInt(obj[3].toString()));
					gap.setWeek2(obj[4]==null?0:Integer.parseInt(obj[4].toString()));
					gap.setWeek3(obj[5]==null?0:Integer.parseInt(obj[5].toString()));
					gap.setWeek4(obj[6]==null?0:Integer.parseInt(obj[6].toString()));
					gap.setWeek5(obj[7]==null?0:Integer.parseInt(obj[7].toString()));
					gap.setWeek6(obj[8]==null?0:Integer.parseInt(obj[8].toString()));
					gap.setWeek7(obj[9]==null?0:Integer.parseInt(obj[9].toString()));
					gap.setWeek8(obj[10]==null?0:Integer.parseInt(obj[10].toString()));
					gap.setWeek9(obj[11]==null?0:Integer.parseInt(obj[11].toString()));
					gap.setWeek10(obj[12]==null?0:Integer.parseInt(obj[12].toString()));
					gap.setWeek11(obj[13]==null?0:Integer.parseInt(obj[13].toString()));
					gap.setWeek12(obj[14]==null?0:Integer.parseInt(obj[14].toString()));
					gap.setWeek13(obj[15]==null?0:Integer.parseInt(obj[15].toString()));
					gap.setWeek14(obj[16]==null?0:Integer.parseInt(obj[16].toString()));
					gap.setWeek15(obj[17]==null?0:Integer.parseInt(obj[17].toString()));
					gap.setWeek16(obj[18]==null?0:Integer.parseInt(obj[18].toString()));
					
					List<Date> dateList=Lists.newArrayList();
					Integer day=0;
						if(gap.getWeek1()<0){
							String[] arr=tip.get(1).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek1());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek2()<0){
							String[] arr=tip.get(2).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek2());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek3()<0){
							String[] arr=tip.get(3).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek3());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						
					gap.setDay(day);
					StringBuilder time= new StringBuilder();
					
						List<Date> removeList=Lists.newArrayList();
						for(int i=2;i<dateList.size();i=i+2){
							Date beforeDate=dateList.get(i-1);
							Date afterDate=dateList.get(i);
							if(DateUtils.addDays(beforeDate, 1).equals(afterDate)){
								removeList.add(beforeDate);
								removeList.add(afterDate);
							}
						}
						dateList.removeAll(removeList);
						for(int i=0;i<dateList.size()-1;i=i+2){
							time.append(formatDay.format(dateList.get(i))).append("~").append(formatDay.format(dateList.get(i+1))).append(";");
						}
					    gap.setTime(time.toString());
					    Map<String,Map<String,Map<String,PsiInventoryGap>>> foreMap=map.get(country); 
					    if(foreMap==null){
					    	foreMap=Maps.newHashMap();
					    	map.put(country, foreMap);
					    }
					    
						Map<String,Map<String,PsiInventoryGap>> temp=foreMap.get(forecastType);
						if(temp==null){
							temp=Maps.newHashMap();
							foreMap.put(forecastType,temp);
						}
						Map<String,PsiInventoryGap> temp2=temp.get(name);
						if(temp2==null){
							temp2=Maps.newHashMap();
							temp.put(name,temp2);
						}
						temp2.put(type,gap);
				  }
			}
		}	
		
		
		
		Map<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>> newMap=Maps.newHashMap();
        if(map!=null&&map.size()>0){
        	 	for (Map.Entry<String, Map<String, Map<String, Map<String, PsiInventoryGap>>>> countryEntry:map.entrySet()) {
		   		 		String country = countryEntry.getKey();
		   		 		Map<String,Map<String,Map<String,PsiInventoryGap>>> countryMap=countryEntry.getValue();
						 for(Map.Entry<String,Map<String,Map<String,PsiInventoryGap>>> countryEntry1:countryMap.entrySet()){
							 String foreCastType = countryEntry1.getKey();
							 Map<String,Map<String,PsiInventoryGap>> forecastMap=countryEntry1.getValue();
							 for (Map.Entry<String,Map<String,PsiInventoryGap>> entry: forecastMap.entrySet()) {
								 String name = entry.getKey();
								 Map<String,PsiInventoryGap> typeMap=entry.getValue();
								 Set<String> set=typeMap.keySet();
								 Integer type=0;
								 for(String max:set){
									 if(Integer.parseInt(max)>=8&&Integer.parseInt(max)>=type){
										 type=Integer.parseInt(max);
									 }
								 }
								 if(type<8){
									 continue;
								 }
								 PsiInventoryGap newGap=typeMap.get(type+"");
								 PsiInventoryGap gap=typeMap.get("3");
								 if(gap==null){
									newGap.setDesc("");
								 }else{
									 newGap.setDesc(gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+""); 
								 }
								 Map<String,Map<String,Map<String,PsiInventoryGap>>> newAllMap=newMap.get(country);
								 if(newAllMap==null){
									 newAllMap=Maps.newHashMap();
									 newMap.put(country, newAllMap);
								 }
								 
								 Map<String,Map<String,PsiInventoryGap>> newTemp=newAllMap.get(foreCastType);
								 if(newTemp==null){
									 newTemp=Maps.newHashMap();
									 newAllMap.put(foreCastType, newTemp);
								 }
								 Map<String,PsiInventoryGap> newNameTemp=newTemp.get(name);
								 if(newNameTemp==null){
									 newNameTemp=Maps.newHashMap();
									 newTemp.put(name, newNameTemp);
								 }
								 newNameTemp.put(type+"",newGap);
							 }
							 
							 
						 }
						 
			   }	
         } 
		
		
		   if(newMap!=null&&newMap.size()>0){
			   for (Dict dict : DictUtils.getDictList("platform")) {
				    String country= dict.getValue();
					if(!"mx".equals(country)&&!"com.unitek".equals(country)){
						 Map<String,Map<String,Map<String,PsiInventoryGap>>> countryMap=newMap.get(country);
						 if(countryMap!=null&&countryMap.size()>0){
							    
								 Map<String,Map<String,PsiInventoryGap>> forecastMap2=countryMap.get("3");
								
								 Map<String,Map<String,PsiInventoryGap>> weekMap2=countryMap.get("2");//week-safe
								 Set<String> weekRemoveName3=Sets.newHashSet();
								 if(weekMap2!=null&&weekMap2.size()>0){
//									 for (String name: weekMap2.keySet()) {
									 for (Map.Entry<String,Map<String,PsiInventoryGap>> entry: weekMap2.entrySet()) {
										 String name = entry.getKey();
										 Map<String,PsiInventoryGap> weekTypeMap=entry.getValue();
										 for(String type:weekTypeMap.keySet()){
											 if(forecastMap2!=null&&forecastMap2.get(name)!=null&&forecastMap2.get(name).get(type)!=null){
												 weekRemoveName3.add(name);
											 }
										 }
									 }
								 }
								 if(weekRemoveName3!=null&&weekRemoveName3.size()>0){
									 for(String key:weekRemoveName3){
										 weekMap2.remove(key);
									 }
								 }
							 
						 }
					}
			   }
			 
		}
		return newMap;
	}
	
	
	public Map<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>> findAllCountryGapByTwoWeek(){
		Map<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>> map=Maps.newHashMap();
	//	String sql="SELECT MAX(create_date) FROM psi_inventory_gap where TYPE IN ('8','9','10','11','12','13')";
		 String sql="SELECT create_date FROM psi_inventory_gap where TYPE IN ('8','9','10','11','12','13')  ORDER BY create_date DESC LIMIT 1 ";
		List<Object> rs=psiOutOfStockInfoDao.findBySql(sql);
		if(rs.size()>0){
			Date date=(Timestamp)rs.get(0);
			if(date!=null){
				Date createDate=new Date();
				Date start = new Date();
				start.setHours(0);
				start.setMinutes(0);
				start.setSeconds(0);
				Date end = DateUtils.addMonths(start,5);
				DateFormat formatWeek = new SimpleDateFormat("yyyyww");
				DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
				if(start.getDay()==0){
					start = DateUtils.addDays(start, -1);
				}
				Map<String,Integer> weekList=Maps.newLinkedHashMap();
				Map<Integer, String> tip = Maps.newHashMap();
				int num=1;
				while(end.after(start)||end.equals(start)){
						String key = formatWeek.format(start);
						int year =DateUtils.getSunday(start).getYear()+1900;
						int week =  Integer.parseInt(key.substring(4));
						if(week==53){
			                year =DateUtils.getMonday(start).getYear()+1900;
					    }
						if(week<10){
							key = year+"0"+week;
						}else{
							key =year+""+week;
						}
						
						if(weekList.size()>16){
							break;
						}else{
							weekList.put(key,num++);
							Date first = DateUtils.getFirstDayOfWeek(year, week);
							tip.put(num-1,DateUtils.getDate(first,"yyyy-MM-dd")+","+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
							start = DateUtils.addWeeks(start, 1);
						}
				}
				
				
				String sql1="SELECT name_color,country,TYPE,week1,week2,week3,week4,week5,week6,week7,week8,week9,week10,week11,week12,week13,week14,week15,week16,forecast_type FROM psi_inventory_gap where create_date=:p1  AND `forecast_type` IN ('0','1') AND TYPE='9' ";
				sql1+=" and (week1<0 or week2<0  or week3<0)";
				sql1+=" union SELECT name_color,country,TYPE,week1,week2,week3,week4,week5,week6,week7,week8,week9,week10,week11,week12,week13,week14,week15,week16,forecast_type FROM psi_inventory_gap where create_date=:p1 AND `forecast_type` IN ('0','1') AND  TYPE='3' ";
				
				List<Object[]> list=psiOutOfStockInfoDao.findBySql(sql1,new Parameter(date));
				for (Object[] obj: list) {
					PsiInventoryGap gap=new PsiInventoryGap();
					String name=obj[0].toString();
					String country=obj[1].toString();
					String type=obj[2].toString();
					String forecastType=obj[19].toString();
					gap.setNameColor(name);
					gap.setForecastType(forecastType);
					gap.setType(type);
					gap.setWeek1(obj[3]==null?0:Integer.parseInt(obj[3].toString()));
					gap.setWeek2(obj[4]==null?0:Integer.parseInt(obj[4].toString()));
					gap.setWeek3(obj[5]==null?0:Integer.parseInt(obj[5].toString()));
					gap.setWeek4(obj[6]==null?0:Integer.parseInt(obj[6].toString()));
					gap.setWeek5(obj[7]==null?0:Integer.parseInt(obj[7].toString()));
					gap.setWeek6(obj[8]==null?0:Integer.parseInt(obj[8].toString()));
					gap.setWeek7(obj[9]==null?0:Integer.parseInt(obj[9].toString()));
					gap.setWeek8(obj[10]==null?0:Integer.parseInt(obj[10].toString()));
					gap.setWeek9(obj[11]==null?0:Integer.parseInt(obj[11].toString()));
					gap.setWeek10(obj[12]==null?0:Integer.parseInt(obj[12].toString()));
					gap.setWeek11(obj[13]==null?0:Integer.parseInt(obj[13].toString()));
					gap.setWeek12(obj[14]==null?0:Integer.parseInt(obj[14].toString()));
					gap.setWeek13(obj[15]==null?0:Integer.parseInt(obj[15].toString()));
					gap.setWeek14(obj[16]==null?0:Integer.parseInt(obj[16].toString()));
					gap.setWeek15(obj[17]==null?0:Integer.parseInt(obj[17].toString()));
					gap.setWeek16(obj[18]==null?0:Integer.parseInt(obj[18].toString()));
					
					List<Date> dateList=Lists.newArrayList();
					Integer day=0;
						if(gap.getWeek1()<0){
							String[] arr=tip.get(1).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek1());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						if(gap.getWeek2()<0){
							String[] arr=tip.get(2).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek2());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						
						if(gap.getWeek3()<0){
							String[] arr=tip.get(3).split(",");
							try {
								dateList.add(formatDay.parse(arr[0]));
								dateList.add(formatDay.parse(arr[1]));
								day+=1;
								gap.setGap(gap.getWeek3());
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}

					gap.setDay(day);
					StringBuilder time= new StringBuilder();;
					
						List<Date> removeList=Lists.newArrayList();
						for(int i=2;i<dateList.size();i=i+2){
							Date beforeDate=dateList.get(i-1);
							Date afterDate=dateList.get(i);
							if(DateUtils.addDays(beforeDate, 1).equals(afterDate)){
								removeList.add(beforeDate);
								removeList.add(afterDate);
							}
						}
						dateList.removeAll(removeList);
						for(int i=0;i<dateList.size()-1;i=i+2){
							time.append(formatDay.format(dateList.get(i))).append("~").append(formatDay.format(dateList.get(i+1))).append(";");
						}
					    gap.setTime(time.toString());
					    Map<String,Map<String,Map<String,PsiInventoryGap>>> foreMap=map.get(country); 
					    if(foreMap==null){
					    	foreMap=Maps.newHashMap();
					    	map.put(country, foreMap);
					    }
					    
						Map<String,Map<String,PsiInventoryGap>> temp=foreMap.get(forecastType);
						if(temp==null){
							temp=Maps.newHashMap();
							foreMap.put(forecastType,temp);
						}
						Map<String,PsiInventoryGap> temp2=temp.get(name);
						if(temp2==null){
							temp2=Maps.newHashMap();
							temp.put(name,temp2);
						}
						temp2.put(type,gap);
				  }
			}
		}	
		
		
		
		Map<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>> newMap=Maps.newHashMap();
        if(map!=null&&map.size()>0){
//			   for (String country:map.keySet()) {
	        	 for (Map.Entry<String, Map<String, Map<String, Map<String, PsiInventoryGap>>>> countryEntry:map.entrySet()) {
			   		 	String country = countryEntry.getKey();
			   		 	Map<String,Map<String,Map<String,PsiInventoryGap>>> countryMap=countryEntry.getValue();
						 for(Map.Entry<String,Map<String,Map<String,PsiInventoryGap>>> entry3:countryMap.entrySet()){
							 String foreCastType = entry3.getKey();
							 Map<String,Map<String,PsiInventoryGap>> forecastMap=entry3.getValue();
							 for (Map.Entry<String,Map<String,PsiInventoryGap>> entry: forecastMap.entrySet()) {
								 String name = entry.getKey();
								 Map<String,PsiInventoryGap> typeMap=entry.getValue();
								 Set<String> set=typeMap.keySet();
								 Integer type=0;
								 for(String max:set){
									 if(Integer.parseInt(max)>=8&&Integer.parseInt(max)>=type){
										 type=Integer.parseInt(max);
									 }
								 }
								 if(type<8){
									 continue;
								 }
								 PsiInventoryGap newGap=typeMap.get(type+"");
								 PsiInventoryGap gap=typeMap.get("3");
								 if(gap==null){
									newGap.setDesc("");
								 }else{
									 newGap.setDesc(gap.getWeek1()+gap.getWeek2()+gap.getWeek3()+""); 
								 }
								 Map<String,Map<String,Map<String,PsiInventoryGap>>> newAllMap=newMap.get(country);
								 if(newAllMap==null){
									 newAllMap=Maps.newHashMap();
									 newMap.put(country, newAllMap);
								 }
								 
								 Map<String,Map<String,PsiInventoryGap>> newTemp=newAllMap.get(foreCastType);
								 if(newTemp==null){
									 newTemp=Maps.newHashMap();
									 newAllMap.put(foreCastType, newTemp);
								 }
								 Map<String,PsiInventoryGap> newNameTemp=newTemp.get(name);
								 if(newNameTemp==null){
									 newNameTemp=Maps.newHashMap();
									 newTemp.put(name, newNameTemp);
								 }
								 newNameTemp.put(type+"",newGap);
							 }
							 
							 
						 }
						 
			   }	
         } 
		
		
		   if(newMap!=null&&newMap.size()>0){
			   for (Dict dict : DictUtils.getDictList("platform")) {
				    String country= dict.getValue();
					if(!"mx".equals(country)&&!"com.unitek".equals(country)){
						 Map<String,Map<String,Map<String,PsiInventoryGap>>> countryMap=newMap.get(country);
						 if(countryMap!=null&&countryMap.size()>0){
							    
							     Map<String,Map<String,PsiInventoryGap>> forecastMap=countryMap.get("0");
								//1.周日销
								 Map<String,Map<String,PsiInventoryGap>> weekMap=countryMap.get("1");
								 Set<String> weekRemoveName=Sets.newHashSet();
								 if(weekMap!=null&&weekMap.size()>0){
//									 for (String name: weekMap.keySet()) {
									 for (Map.Entry<String,Map<String,PsiInventoryGap>> entry: weekMap.entrySet()) {
										 String name = entry.getKey();
										 Map<String,PsiInventoryGap> weekTypeMap=entry.getValue();
										 for(String type:weekTypeMap.keySet()){
											 if(forecastMap!=null&&forecastMap.get(name)!=null&&forecastMap.get(name).get(type)!=null){
												 weekRemoveName.add(name);
											 }
										 }
										 
									}
								 }
								 if(weekRemoveName!=null&&weekRemoveName.size()>0){
									 for(String key:weekRemoveName){
										 weekMap.remove(key);
									 }
								 }
							 
						 }
					}
			   }
			 
		}
		return newMap;
	}
}
