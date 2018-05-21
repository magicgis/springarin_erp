/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonLongTermStorageFeeDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonMonthlyStorageFeeDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonLongTermStorageFees;
import com.springrain.erp.modules.amazoninfo.entity.AmazonMonthlyStorageFees;



@Component
@Transactional(readOnly = true)
public class AmazonStorageFeeService extends BaseService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AmazonStorageFeeService.class);

    @Autowired
    private AmazonMonthlyStorageFeeDao amazonStorageFeeDao;
    
    @Autowired
    private AmazonLongTermStorageFeeDao amazonLongTermStorageFeeDao;
    
    public Map<String,List<AmazonMonthlyStorageFees>> find(String month){
        Map<String,List<AmazonMonthlyStorageFees>> map=Maps.newHashMap();
        DetachedCriteria dc = amazonStorageFeeDao.createDetachedCriteria();
        dc.add(Restrictions.eq("month",month));
        List<AmazonMonthlyStorageFees> list=amazonStorageFeeDao.find(dc);
        for (AmazonMonthlyStorageFees fees : list) {
            List<AmazonMonthlyStorageFees> temp=map.get(fees.getCountry());
            if(temp==null){
                temp=Lists.newArrayList();
                map.put(fees.getCountry(),temp);
            }
            temp.add(fees);
        }
        return map;
    }
    
    @Transactional(readOnly = false)
    public void save(List<AmazonMonthlyStorageFees> report){
        amazonStorageFeeDao.save(report);
    }
    
    @Transactional(readOnly = false)
    public void saveLongTerm(List<AmazonLongTermStorageFees> report){
        amazonLongTermStorageFeeDao.save(report);
    }
    
    public AmazonLongTermStorageFees findLongTerm(Date date,String sku,String country){
        DetachedCriteria dc = amazonLongTermStorageFeeDao.createDetachedCriteria();
        dc.add(Restrictions.eq("snapshotDate",date));
        dc.add(Restrictions.eq("sku",sku));
        dc.add(Restrictions.eq("country",country));
        List<AmazonLongTermStorageFees> list=amazonLongTermStorageFeeDao.find(dc);
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }
    
    public boolean findLongTerm(Date date,String country){
        DetachedCriteria dc = amazonLongTermStorageFeeDao.createDetachedCriteria();
        dc.add(Restrictions.ge("snapshotDate",date));
        dc.add(Restrictions.lt("snapshotDate",DateUtils.addDays(date,1)));
        dc.add(Restrictions.eq("country",country));
        List<AmazonLongTermStorageFees> list=amazonLongTermStorageFeeDao.find(dc);
        if(list!=null&&list.size()>0){
            return false;
        }
        return true;
    }
    
    
    public boolean findMonth(String month,String country){
        DetachedCriteria dc = amazonStorageFeeDao.createDetachedCriteria();
        dc.add(Restrictions.eq("month",month));
        dc.add(Restrictions.eq("country",country));
        List<AmazonMonthlyStorageFees> list=amazonStorageFeeDao.find(dc);
        if(list!=null&&list.size()>0){
            return false;
        }
        return true;
    }
    
    public AmazonMonthlyStorageFees findMonthly(String month,String asin,String country,String fulfillmentCenter){
        DetachedCriteria dc = amazonStorageFeeDao.createDetachedCriteria();
        dc.add(Restrictions.eq("month",month));
        dc.add(Restrictions.eq("asin",asin));
        dc.add(Restrictions.eq("country",country));
        dc.add(Restrictions.eq("fulfillmentCenter",fulfillmentCenter));
        List<AmazonMonthlyStorageFees> list=amazonStorageFeeDao.find(dc);
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }
    
    /**
     * 仓储费用汇总
     * @param longStart
     * @param longEnd
     * @param monthStart
     * @param monthEnd
     * @param country
     * @return
     */
    public List<AmazonMonthlyStorageFees> findStorageFees(String longStart, String longEnd, String monthStart, String monthEnd, String country){
        String currencyType = "EUR"; // 统计的货币类型（EUR/USD
        Map<String, Float> rateRs = null;   // 置空,采用实时汇率    
        String sql = null;
        List<Object[]> findBySql = null;
        String sql1 = "SELECT A.total_long_fee,B.*,ROUND(SUM((CASE WHEN A.total_long_fee IS NULL THEN 0 " 
                + " ELSE A.total_long_fee END)+(CASE WHEN B.totol_monthly_fee IS NULL THEN 0 ELSE B.totol_monthly_fee END)),2)"
                + " total_fee FROM (SELECT country long_country,product_name,snapshot_date, ROUND(SUM(" 
                + " CASE currency WHEN 'USD' THEN (twelfth_mo_long_terms_storage_fee+" 
                + " six_mo_long_terms_storage_fee)*" + MathUtils.getRate("USD", currencyType, rateRs)
                + "                  WHEN 'EUR' THEN (twelfth_mo_long_terms_storage_fee+" 
                + " six_mo_long_terms_storage_fee)*" + MathUtils.getRate("EUR", currencyType, rateRs)
                + "                  WHEN 'JPY' THEN (twelfth_mo_long_terms_storage_fee+" 
                + " six_mo_long_terms_storage_fee)*" + MathUtils.getRate("JPY", currencyType, rateRs)
                + "                  WHEN 'GBP' THEN (twelfth_mo_long_terms_storage_fee+" 
                + " six_mo_long_terms_storage_fee)*" + MathUtils.getRate("GBP", currencyType, rateRs)
                + "               WHEN 'CAD' THEN (twelfth_mo_long_terms_storage_fee+" 
                + " six_mo_long_terms_storage_fee)*" + MathUtils.getRate("CAD", currencyType, rateRs) 
                + " END),2) total_long_fee";
        String sql2 =  " (SELECT m.product_name,m.longest_side,m.median_side,m.shortest_side,m.measurement_units,ROUND(SUM(" 
                + "  CASE currency WHEN 'USD' THEN (estimated_monthly_storage_fee)*" + MathUtils.getRate("USD", currencyType, rateRs)
                + "              WHEN 'EUR' THEN (estimated_monthly_storage_fee)*" + MathUtils.getRate("EUR", currencyType, rateRs)
                + "              WHEN 'JPY' THEN (estimated_monthly_storage_fee)*" + MathUtils.getRate("JPY", currencyType, rateRs)
                + "               WHEN 'GBP' THEN (estimated_monthly_storage_fee)*" + MathUtils.getRate("GBP", currencyType, rateRs)
                + "               WHEN 'CAD' THEN (estimated_monthly_storage_fee)*" + MathUtils.getRate("CAD", currencyType, rateRs)
                + " END),2) totol_monthly_fee";
        String sql3 = " ON A.product_name = B.product_name GROUP BY B.product_name";
        if (StringUtils.isNotBlank(country)){
            sql = sql1 + "  FROM amazoninfo_long_term_storage_fees where snapshot_date>=:p1 and snapshot_date<=:p2 and country=:p3 GROUP BY product_name) A" 
                  + " LEFT JOIN " + sql2 + "  FROM amazoninfo_monthly_storage_fees m where m.month>=:p4 and m.month<=:p5 and country=:p6 GROUP BY product_name) B" + sql3
                  +" UNION "
                  + sql1 + "  FROM amazoninfo_long_term_storage_fees where DATE_FORMAT(snapshot_date,'%Y-%m')>=:p7 and DATE_FORMAT(snapshot_date,'%Y-%m')<=:p8 and country=:p9 GROUP BY product_name) A" 
                  + " Right JOIN " + sql2 + "  FROM amazoninfo_monthly_storage_fees m where m.month>='"+monthStart+"' and m.month<='"+monthEnd+"' and country='"+country+"' GROUP BY product_name) B" + sql3;
            findBySql = amazonStorageFeeDao.findBySql(sql, new Parameter(monthStart,monthEnd, country,monthStart,monthEnd,country,monthStart,monthEnd, country));
        } else {
            sql = sql1 + "  FROM amazoninfo_long_term_storage_fees where snapshot_date>=:p1 and snapshot_date<=:p2 GROUP BY product_name) A" 
                      + " RIGHT JOIN " + sql2 + "  FROM amazoninfo_monthly_storage_fees m where m.month>=:p3 and m.month<=:p4  GROUP BY product_name) B" + sql3
                      +" UNION "
                      + sql1 + "  FROM amazoninfo_long_term_storage_fees where DATE_FORMAT(snapshot_date,'%Y-%m')>=:p5 and DATE_FORMAT(snapshot_date,'%Y-%m')<=:p6  GROUP BY product_name) A" 
                      + " LEFT JOIN " + sql2 + "  FROM amazoninfo_monthly_storage_fees m where m.month>=:p7 and m.month<=:p8 GROUP BY product_name) B" + sql3;
            findBySql = amazonStorageFeeDao.findBySql(sql, new Parameter(monthStart,monthEnd,monthStart,monthEnd,monthStart,monthEnd,monthStart,monthEnd));     
        }
        List<AmazonMonthlyStorageFees> list = new ArrayList<AmazonMonthlyStorageFees>();
        for (Object[] obj : findBySql) {
            AmazonMonthlyStorageFees storageFee = new AmazonMonthlyStorageFees();
            if(!"0.00".equals(obj[7].toString()) && obj[1] != null){
                String totalLongFee = (obj[0] != null) ? obj[0].toString() : "0.00";
                String productName = obj[1].toString();
                String longestSide = (obj[2] != null) ? obj[2].toString() : " " ;
                String medianSide = (obj[3] != null) ?  obj[3].toString() : " " ; 
                String shortestSide = (obj[4] != null) ? obj[4].toString() : " " ;
                String measurementUnits = (obj[5] != null)? obj[5].toString() : " " ;
                String totolMonthlyFee = (obj[6] != null) ? obj[6].toString() : "0.00";
                String totalFee = obj[7].toString();
                storageFee.setTotalFee(totalFee);
                storageFee.setShortestSideNew(shortestSide);
                storageFee.setLongestSideNew(longestSide);
                storageFee.setProductName(productName);
                storageFee.setMeasurementUnits(measurementUnits);
                storageFee.setTotalLongFee(totalLongFee);
                storageFee.setTotalMonthFee(totolMonthlyFee);
                storageFee.setMedianSideNew(medianSide);
                list.add(storageFee);
            }
        }
        return list;
    }
    
    /**
     * 获取产品长期仓储、短期仓储的详细各月费用（按月显示）
     * @param start
     * @param end
     * @param productName
     * @param country
     * @param type
     * @return
     * @throws ParseException
     */
    public String findStorageByMonthAndLong(String start,String end,String productName,String country,String type) throws ParseException{
        String currencyType = "EUR"; // 统计的货币类型（EUR/USD
        Map<String, Float> rateRs = null;   // 置空,采用实时汇率
        List<Object[]> findBySql = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf= new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        String startBegin = dateFormat.format(sdf.parse(start));
        String endBegin = dateFormat.format(sdf.parse(end));
        String sqlMonth = "SELECT m.month,ROUND(SUM("
                        + " CASE currency WHEN 'USD' THEN (estimated_monthly_storage_fee)*" + MathUtils.getRate("USD", currencyType, rateRs)
                        + "  WHEN 'EUR' THEN (estimated_monthly_storage_fee)*" + MathUtils.getRate("EUR", currencyType, rateRs)
                        + "  WHEN 'JPY' THEN (estimated_monthly_storage_fee)*" + MathUtils.getRate("JPY", currencyType, rateRs)
                        + "  WHEN 'GBP' THEN (estimated_monthly_storage_fee)*" + MathUtils.getRate("GBP", currencyType, rateRs)
                        + "  WHEN 'CAD' THEN (estimated_monthly_storage_fee)*" + MathUtils.getRate("CAD", currencyType, rateRs)
                        + " END),2) totalOneMothFee  FROM amazoninfo_monthly_storage_fees m  ";
        String sqlLong = "SELECT snapshot_date,ROUND(SUM("
                        + " CASE currency WHEN 'USD' THEN (twelfth_mo_long_terms_storage_fee+" 
                        + " six_mo_long_terms_storage_fee)*" + MathUtils.getRate("USD", currencyType, rateRs)
                        + "                  WHEN 'EUR' THEN (twelfth_mo_long_terms_storage_fee+" 
                        + " six_mo_long_terms_storage_fee)*" + MathUtils.getRate("EUR", currencyType, rateRs)
                        + "                  WHEN 'JPY' THEN (twelfth_mo_long_terms_storage_fee+" 
                        + " six_mo_long_terms_storage_fee)*" + MathUtils.getRate("JPY", currencyType, rateRs)
                        + "                  WHEN 'GBP' THEN (twelfth_mo_long_terms_storage_fee+" 
                        + " six_mo_long_terms_storage_fee)*" + MathUtils.getRate("GBP", currencyType, rateRs)
                        + "               WHEN 'CAD' THEN (twelfth_mo_long_terms_storage_fee+" 
                        + " six_mo_long_terms_storage_fee)*" + MathUtils.getRate("CAD", currencyType, rateRs)
                        + " END),2) totalLongFee,qty_charged_six_mo_long_term_storage_fee,qty_charged_twelfth_mo_long_term_storage_fee FROM amazoninfo_long_term_storage_fees";
        if ("monthly".equals(type)) {//从前台传入"monthly"、"longly"值来判断显示月仓储、长期仓储的各月详情
            if (StringUtils.isNotBlank(country)){
                   sqlMonth += " WHERE m.product_name=:p1 AND m.month>=:p2 AND m.month<=:p3 AND country =:p4 GROUP BY m.MONTH";
                   findBySql = amazonStorageFeeDao.findBySql(sqlMonth, new Parameter(productName,startBegin,endBegin,country));
            } else {
                   sqlMonth += " WHERE m.product_name=:p1 AND m.month>=:p2 AND m.month<=:p3 GROUP BY m.MONTH";
                   findBySql = amazonStorageFeeDao.findBySql(sqlMonth, new Parameter(productName,startBegin,endBegin));
            }
        } else {
            if (StringUtils.isBlank(country)){
                   sqlLong += " WHERE DATE_FORMAT(snapshot_date,'%Y-%m')>=:p1 AND DATE_FORMAT(snapshot_date,'%Y-%m')<=:p2 AND product_name=:p3 GROUP BY snapshot_date";
                   findBySql = amazonStorageFeeDao.findBySql(sqlLong, new Parameter(startBegin,endBegin,productName));
            } else {
                  sqlLong += " WHERE DATE_FORMAT(snapshot_date,'%Y-%m')>=:p1 AND DATE_FORMAT(snapshot_date,'%Y-%m')<=:p2 AND product_name=:p3 AND country=:p4 GROUP BY snapshot_date";
                  findBySql = amazonStorageFeeDao.findBySql(sqlLong, new Parameter(startBegin,endBegin,productName,country)); 
            }
        }
        
        StringBuilder content = new StringBuilder();
        String month = "";
        for (Object[] objs : findBySql) {
            month = objs[0].toString();
            if(objs.length>2){
                content.append(month).append(" : ").append(objs[1].toString()+"</br>mount : "
                       +objs[2].toString()+"(超过6个月) "+objs[3].toString()+"(超过12个月)").append("<br/>") ; 
            }else {
                content.append(month).append(" : ").append(objs[1].toString()).append("<br/>") ;  
            }
            
        }
        return content.toString();
    }
    
    
	private void saveData(String data,String country,Map<String,String>  nameMap){
		String[] rows = data.split("\r\n");
		List<AmazonMonthlyStorageFees> reportList=Lists.newArrayList();
		Date date=new Date();
		boolean flag=false;
		for (int i = rows.length-1; i>0 ; i--) {
		
			String[] rowData = rows[i].split("\t");
			String asin=rowData[0];
			String fnsku=rowData[1];
			String fulfillmentCenter=rowData[3];
			String countryCode=rowData[4];
			if("FR".equals(countryCode)){
				country="fr";
			}else if("IT".equals(countryCode)){
				country="it";
			}else if("ES".equals(countryCode)){
				country="es";
			}else if("GB".equals(countryCode)){
				country="uk";
			}else if("CA".equals(countryCode)){
				country="ca";
			}else if("DE".equals(countryCode)){
				country="de";
			}
			String productName=nameMap.get(asin+"_"+country);
			float longestSide=Float.parseFloat(rowData[5]);
			float medianSide=Float.parseFloat(rowData[6]);
			float shortestSide=Float.parseFloat(rowData[7]);
			
			String measurementUnits=rowData[8];
			float weight=Float.parseFloat(rowData[9]);
			String weightUnits=rowData[10];
			float itemVolume=Float.parseFloat(rowData[11]);
			String volumeUnits=rowData[12];
			if("com".equals(country)){
				String productSizeTier=rowData[13];
                float averageQuantityOnHand=Float.parseFloat(rowData[14]);
				
				float averageQuantityPendingRemoval=Float.parseFloat(rowData[15]);
				float estimatedTotalItemVolume=0f;
				if(rowData[16].contains("E")){
					BigDecimal bd = new BigDecimal(rowData[16]);  
					estimatedTotalItemVolume=Float.parseFloat(bd.toPlainString());
				}else{
					estimatedTotalItemVolume=Float.parseFloat(rowData[16]);
				}
				String month=rowData[17];
				float storageRate=Float.parseFloat(rowData[18]);
				String currency=rowData[19];
				float estimatedMonthlyStorageFee=0f;
				if(rowData[20].contains("E")){
					BigDecimal bd = new BigDecimal(rowData[20]);  
					estimatedMonthlyStorageFee=Float.parseFloat(bd.toPlainString());
				}else{
					estimatedMonthlyStorageFee=Float.parseFloat(rowData[20]);
				}
				if(!flag&&findMonthly(month,asin,country,fulfillmentCenter)==null){
					flag=true;
				}
				if(flag){
					reportList.add(new AmazonMonthlyStorageFees(country,asin,fnsku,productName,fulfillmentCenter,countryCode,
							longestSide,medianSide,shortestSide,measurementUnits,weight,weightUnits,itemVolume,volumeUnits,averageQuantityOnHand,
							averageQuantityPendingRemoval,estimatedTotalItemVolume,month,storageRate,currency,estimatedMonthlyStorageFee,date,productSizeTier));
			
				}
				
			}else{
				float averageQuantityOnHand=Float.parseFloat(rowData[13]);
				
				float averageQuantityPendingRemoval=Float.parseFloat(rowData[14]);
				float estimatedTotalItemVolume=0f;
				if(rowData[15].contains("E")){
					BigDecimal bd = new BigDecimal(rowData[15]);  
					estimatedTotalItemVolume=Float.parseFloat(bd.toPlainString());
				}else{
					estimatedTotalItemVolume=Float.parseFloat(rowData[15]);
				}
				String month=rowData[16];
				float storageRate=Float.parseFloat(rowData[17]);
				String currency=rowData[18];
				float estimatedMonthlyStorageFee=0f;
				if(rowData[19].contains("E")){
					BigDecimal bd = new BigDecimal(rowData[19]);  
					estimatedMonthlyStorageFee=Float.parseFloat(bd.toPlainString());
				}else{
					estimatedMonthlyStorageFee=Float.parseFloat(rowData[19]);
				}
				
				if(!flag&&findMonthly(month,asin,country,fulfillmentCenter)==null){
					flag=true;
				}
				if(flag){
					reportList.add(new AmazonMonthlyStorageFees(country,asin,fnsku,productName,fulfillmentCenter,countryCode,
							longestSide,medianSide,shortestSide,measurementUnits,weight,weightUnits,itemVolume,volumeUnits,averageQuantityOnHand,
							averageQuantityPendingRemoval,estimatedTotalItemVolume,month,storageRate,currency,estimatedMonthlyStorageFee,date,null));
			
				}
			}
			
			
		}
		if(reportList!=null&&reportList.size()>0){
			save(reportList);
		}
	}
	
	private void saveData2(String data,String country,Map<String,String>  nameMap) throws ParseException{
		String[] rows = data.split("\r\n");
		List<AmazonLongTermStorageFees> reportList=Lists.newArrayList();
		Date today=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		for (int i = rows.length-1; i>0 ; i--) {
			String[] rowData = rows[i].split("\t");
			String date=rowData[0];
			Date snapshotDate=dateFormat.parse(date.split("T")[0]);
			String sku=rowData[1];
			String fnsku=rowData[2];
			String asin=rowData[3];
			String productName=nameMap.get(asin);
			String condition=rowData[5];
			Integer qtyCharged12MoLongTermStorageFee=Integer.parseInt(rowData[6]);
			Float perUnitVolume=Float.parseFloat(rowData[7]);
			String currency=rowData[8];
			Float mo12LongTermsStorageFee=Float.parseFloat(rowData[9]);
			Integer qtyCharged6MoLongTermStorageFee=Integer.parseInt(rowData[10]);
			Float mo6LongTermsStorageFee=Float.parseFloat(rowData[11]);
			AmazonLongTermStorageFees fee=findLongTerm(snapshotDate,sku,country);
			if(fee==null){
				AmazonLongTermStorageFees fees=new AmazonLongTermStorageFees(country,snapshotDate,asin,fnsku,productName,sku,condition,qtyCharged12MoLongTermStorageFee,
						perUnitVolume,currency,mo12LongTermsStorageFee,qtyCharged6MoLongTermStorageFee,mo6LongTermsStorageFee,today);
				reportList.add(fees);
			}
		}
		if(reportList!=null&&reportList.size()>0){
			saveLongTerm(reportList);
		}
	}
	
	public static String getDownloadData(WebClient webClient,String url){
		WebRequest request = null;
		try {
			request = new WebRequest(new URL(url),HttpMethod.POST);
			request.setAdditionalHeader("Content-Type",
				"application/x-www-form-urlencoded");
			Page page = webClient.getPage(request);
			if (page != null) {
				String data = "";
				if(url.contains("co.jp")){
					data = page.getWebResponse().getContentAsString("Shift_JIS");
				}else{
					data = page.getWebResponse().getContentAsString();
				}
				return data;
			} 
		}catch(Exception e){
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
    
}
