/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.common.util.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.amazoninfo.dao.AdvertisingDao;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;

/**
 * 库存预警Service
 *
 */
@Component
@Transactional(readOnly = true)
public class AmazonStorageMonitorService extends BaseService {

	@Autowired
	private AdvertisingDao advertisingDao;
	@Autowired
	private PsiInventoryFbaService psiInventoryFbaService;
	@Autowired
	private PsiInventoryService psiInventoryService;
	@Autowired
    private PsiProductEliminateService psiProductEliminateService;
	@Autowired
	private PsiProductTypeGroupDictService psiProductTypeGroupDictService;
	@Autowired
	private PsiProductService psiProductService;
	
	/**
	 * 获取所有产品库存
	 * @return
	 */
	public  LinkedHashMap<String, LinkedHashMap<String, List<String>>> getProductStorag() {
	    String sql = "SELECT (CASE WHEN color ='' THEN s.`product_name` ELSE CONCAT(s.`product_name`,'_',s.`color`) END) pro_name," 
	    		    +" hr.`country`,SUM(hr.age_days180),SUM(hr.age_days270),SUM(hr.age_days365),SUM(hr.age_plus_days365) FROM psi_sku s,amazoninfo_fba_health_report hr " 
	    		    +" WHERE s.sku=hr.sku AND s.`country`=hr.`country`  AND hr.create_time=CURDATE()  AND s.`del_flag`='0' GROUP BY s.`product_name`,hr.`country`,s.color "
	                +" ORDER BY s.`product_name` ASC,SUM(hr.age_days180) DESC, SUM(hr.age_days270) DESC,SUM(hr.age_days365) DESC,SUM(hr.age_days365) DESC";
        List<Object[]> findBySql = advertisingDao.findBySql(sql);
        LinkedHashMap<String, LinkedHashMap<String, List<String>>> map = new LinkedHashMap<String, LinkedHashMap<String,List<String>>>();
        for(Object[] obj : findBySql){
            List<String> list = new ArrayList<String>();
            list.add(obj[2].toString());
            list.add(obj[3].toString());
            list.add(obj[4].toString());
            list.add(obj[5].toString());
            if(map.containsKey(obj[0].toString())){//之前有存储该产品
                LinkedHashMap<String, List<String>> mapOrigin = map.get(obj[0].toString());
                mapOrigin.put(obj[1].toString(), list);
                map.put(obj[0].toString(), mapOrigin);
            }else{
                LinkedHashMap<String, List<String>> mapList = new LinkedHashMap<String, List<String>>();
                mapList.put(obj[1].toString(), list);
                map.put(obj[0].toString(), mapList);
            }
        }
	    return map;
    }
	
	/**
	 * 查出了不带电、和键盘的产品的总库存
	 */
	public LinkedHashMap<String, LinkedHashMap<String, Integer>> getStorageByDay(){
	    String sql = "SELECT (CASE WHEN s.`color`='' THEN s.`product_name` ELSE CONCAT(s.`product_name`,'_',s.`color`) END) pname,"
                   +" SUM(hr.age_days180+hr.age_days270+hr.age_days365+hr.age_plus_days365) all_storage,hr.`country` FROM psi_sku s,amazoninfo_fba_health_report hr  "
                   +" WHERE s.sku=hr.sku AND s.`country`=hr.`country` AND s.`del_flag`='0'  AND hr.create_time=CURDATE() GROUP BY s.`product_name`,hr.`country`,s.color ORDER BY pname ASC ";
	    List<Object[]> findBySql = advertisingDao.findBySql(sql);
	    List<String> list = this.getProudutExpectKeyBoardAndElectric();
	    LinkedHashMap<String, LinkedHashMap<String, Integer>> map = new LinkedHashMap<String, LinkedHashMap<String,Integer>>();
	    for(Object[] obj:findBySql){
	        if(list.contains(obj[0].toString())){
	            String country="";
    	        if("jp".equals(obj[2].toString())){
    	            country="jp";
    	        }else if("com".equals(obj[2].toString())){
    	            country="com";
    	        }else{
    	            country="de";
    	        }
	            if(map.containsKey(obj[0].toString())){//之前有存储该产品
    	            LinkedHashMap<String, Integer> mapOrigin = map.get(obj[0].toString());
    	            if(mapOrigin.containsKey(country)){//之前有存储过该国家
    	                Integer stock = Integer.parseInt(obj[1].toString()) + mapOrigin.get(country);
    	                mapOrigin.put(country, stock);
    	            }else{
    	                mapOrigin.put(country,  Integer.parseInt(obj[1].toString()));
    	            }
                    map.put(obj[0].toString(), mapOrigin);
                }else{
                    LinkedHashMap<String, Integer> map1 = new LinkedHashMap<String, Integer>();
                    map1.put(country, Integer.parseInt(obj[1].toString()));
                    map.put(obj[0].toString(), map1);
                }
	        }
	    }
	    System.out.println(map);
	    return map;
	 }
	
	/**
	 * 排除键盘和电源后的所有产品名
	 * @return
	 */
	public List<String> getProudutExpectKeyBoardAndElectric(){
        String sql = "  SELECT brand,model,color FROM psi_product p WHERE TYPE <> 'Keyboard' AND del_flag='0' " +
        		     " AND has_power <>'1' GROUP BY brand,model,color";
        List<Object[]> findBySql = advertisingDao.findBySql(sql);
        List<String> list = new ArrayList<String>();
        for(Object[] obj:findBySql){
            if(obj[2].toString().contains(",")){
                String arr[] = obj[2].toString().split(",");
                for(int i=0;i<arr.length;i++){
                    list.add(obj[0].toString()+" "+obj[1].toString()+"_"+arr[i]);
                }
            }else if("".equals(obj[2].toString())){
                list.add(obj[0].toString()+" "+obj[1].toString());
            }else{
                list.add(obj[0].toString()+" "+obj[1].toString()+"_"+obj[2].toString());
            }
        }
        return list;
     }
	
	public String contentOfStorage(){
	    LinkedHashMap<String, LinkedHashMap<String, List<String>>> productStorag = this.getProductStorag();
	    Map<String, String> lineNameByName = psiProductTypeGroupDictService.getLineNameByName();
	    List<PsiProduct> findAllOnSale = psiProductService.findAllOnSale();
	    List<String> onSaleProduct = new ArrayList<String>();
	    for(int k=0;k<findAllOnSale.size();k++){
	         PsiProduct psiProduct = findAllOnSale.get(k);
	         if(psiProduct.getColor().contains(",")){
	                String arr[] = psiProduct.getColor().toString().split(",");
	                for(int i=0;i<arr.length;i++){
	                    onSaleProduct.add(psiProduct.getBrand()+" "+psiProduct.getModel()+"_"+arr[i]);
	                }
	            }else if("".equals(psiProduct.getColor())){
	                onSaleProduct.add(psiProduct.getBrand()+" "+psiProduct.getModel());
	            }else{
	                onSaleProduct.add(psiProduct.getBrand()+" "+psiProduct.getModel()+"_"+psiProduct.getColor());
	            }
	    }
	    Map<String, Double> volume = this.getVolume();
	    StringBuilder sbf = new StringBuilder();
	    for(int i=3;i>-1;i--){
	        StringBuilder sb = new StringBuilder();
	        if(i==0){
	            sb.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;'>"
                        +"<td colspan='9'>库存大于90天小于180天详情</td></tr><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; " +
                "padding:0 1em 0;background-color:#4EFEB3; '><th>产品</th><th>COM</th><th>DE</th><th>UK</th><th>CA</th><th>JP</th><th>FR</th><th>IT</th><th>ES</th></tr>");
	        }if(i==1){
                sb.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;'>"
                        +"<td colspan='9'>库存大于180天小于270详情</td></tr><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; " +
                "padding:0 1em 0;background-color:#4EFEB3; '><th>产品</th><th>COM</th><th>DE</th><th>UK</th><th>CA</th><th>JP</th><th>FR</th><th>IT</th><th>ES</th></tr>");
            }if(i==2){
                sb.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;'>"
                        +"<td colspan='9'>库存大于270天小于365详情</td></tr><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; " +
                "padding:0 1em 0;background-color:#4EFEB3; '><th>产品</th><th>COM</th><th>DE</th><th>UK</th><th>CA</th><th>JP</th><th>FR</th><th>IT</th><th>ES</th></tr>");
            }if(i==3){
                sb.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;'>"
                        +"<td colspan='9'>库存360天以上详情</td></tr><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; " +
                "padding:0 1em 0;background-color:#4EFEB3; '><th>产品</th><th>COM</th><th>DE</th><th>UK</th><th>CA</th><th>JP</th><th>FR</th><th>IT</th><th>ES</th></tr>");
            }
	        int com=0,de=0,uk=0,ca=0,jp=0,fr=0,it=0,es=0;
	        double comV=0,deV=0,ukV=0,caV=0,jpV=0,frV=0,itV=0,esV=0;
	        StringBuilder sbNotSale = new StringBuilder();
	        StringBuilder sbOnSale = new StringBuilder();
    	    for (Entry<String, LinkedHashMap<String, List<String>>> entry : productStorag.entrySet()) {  
    	        LinkedHashMap<String, List<String>> entry1 = entry.getValue();
                StringBuilder sb1 = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                StringBuilder sb3 = new StringBuilder();
                StringBuilder sb4 = new StringBuilder();
                StringBuilder sb5 = new StringBuilder();
                StringBuilder sb6 = new StringBuilder();
                StringBuilder sb7 = new StringBuilder();
                StringBuilder sb8 = new StringBuilder();
                boolean contentNotNull = false;
                for (String key : entry1.keySet()) {
                    String key2 = entry.getKey();
                    Double v=0d;
                    if( volume.get(key2)!=null){
                        v = volume.get(key2);
                    }
                    
                    if(!"0".equals(entry1.get(key).get(i))){
                        contentNotNull = true;
                        if(key.equals("com")){
                            sb1.append(entry1.get(key).get(i));
                            com += Integer.parseInt(entry1.get(key).get(i));
                            comV+=v*Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("de")){
                            sb2.append(entry1.get(key).get(i));
                            deV+=v*Integer.parseInt(entry1.get(key).get(i));
                            de += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("uk")){
                            sb3.append(entry1.get(key).get(i));
                            ukV+=v*Integer.parseInt(entry1.get(key).get(i));
                            uk += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("ca")){
                            sb4.append(entry1.get(key).get(i));
                            caV+=v*Integer.parseInt(entry1.get(key).get(i));
                            ca += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("jp")){
                            sb5.append(entry1.get(key).get(i));
                            jpV+=v*Integer.parseInt(entry1.get(key).get(i));
                            jp += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("fr")){
                            sb6.append(entry1.get(key).get(i));
                            frV+=v*Integer.parseInt(entry1.get(key).get(i));
                            fr += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("it")){
                            sb7.append(entry1.get(key).get(i));
                            itV+=v*Integer.parseInt(entry1.get(key).get(i));
                            it += Integer.parseInt(entry1.get(key).get(i));
                        }else if(key.equals("es")){
                            sb8.append(entry1.get(key).get(i));
                            esV+=v*Integer.parseInt(entry1.get(key).get(i));
                            es += Integer.parseInt(entry1.get(key).get(i));
                        }
                    }
                } 
                if(contentNotNull){
                    String lineName= lineNameByName.get(entry.getKey())!=null ? "("+lineNameByName.get(entry.getKey())+")":"";
                    if(onSaleProduct.contains(entry.getKey())){
                        sbOnSale.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#DBEEEE;'>"
                                +"<td>"+entry.getKey()+lineName+"</td><td>"+sb1.toString()+"</td><td>"+sb2.toString()+"</td><td>"+sb3.toString()+"</td><td>"+sb4.toString()+"</td><td>"+sb5.toString()+"</td><td>"+sb6.toString()+"</td><td>"+sb7.toString()+"</td><td>"+sb8.toString()+"</td></tr>");
                    }else{
                        sbNotSale.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#FF8080;'>"
                                +"<td>"+entry.getKey()+lineName+"</td><td>"+sb1.toString()+"</td><td>"+sb2.toString()+"</td><td>"+sb3.toString()+"</td><td>"+sb4.toString()+"</td><td>"+sb5.toString()+"</td><td>"+sb6.toString()+"</td><td>"+sb7.toString()+"</td><td>"+sb8.toString()+"</td></tr>");
                    }
                }
    	    }
    	    DecimalFormat   df   =new DecimalFormat("#0.0000");  
    	    if(com!=0 || de!=0 || uk!=0 || ca!=0 || jp!=0 || fr!=0 || it!=0 || es!=0){
        	    sb.append(sbOnSale.toString()+sbNotSale.toString()+"<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#DBEEEE;'>"
                            +"<td>库存总数</td><td>"+com+"</td><td>"+de+"</td><td>"+uk+"</td><td>"+ca+"</td><td>"+jp+"</td><td>"+fr+"</td><td>"+it+"</td><td>"+es+"</td></tr>");
        	    sb.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#DBEEEE;'>"
                        +"<td>库存总体积</td><td>"+df.format(comV)+"</td><td>"+df.format(deV)+"</td><td>"+df.format(ukV)+"</td><td>"+df.format(caV)+"</td><td>"+df.format(jpV)+"</td><td>"+df.format(frV)+"</td><td>"+df.format(itV)+"</td><td>"+df.format(esV)+"</td></tr></table>");
    	    }else{
    	        sb.append(sbOnSale.toString()+sbNotSale.toString()+"</table>");
    	    }
    	    sbf.append(sb.toString());
	    }
	    return sbf.toString();
	}
	
	/**
	 * 获取满足产品大于90天的总库存/日销量>120的情况
	 * @return
	 */
	public LinkedHashMap<String,List<String>> getProductAndCountryByDaySale(){
	    Map<String, Map<String, Integer>> get31SalesMap = psiInventoryFbaService.get31SalesMap(null);
	    LinkedHashMap<String, LinkedHashMap<String, Integer>> storageByDay = this.getStorageByDay();
	    LinkedHashMap<String,List<String>> map = new LinkedHashMap<String, List<String>>();
	    for (Entry<String, LinkedHashMap<String, Integer>> entry : storageByDay.entrySet()) {  
	        String productName = entry.getKey();
	        Map<String, Integer> countryAndStorage = entry.getValue();
	        for(String country : countryAndStorage.keySet()){
	            Integer storage = countryAndStorage.get(country);
    	        Map<String, Integer> saleMap = get31SalesMap.get(country);
    	        if(saleMap==null){
    	        	continue;
    	        }
    	        Integer sale = saleMap.get(productName)==null?0:saleMap.get(productName);
    	        if(sale>0 && sale!=null){
        	        if(storage/((double)sale/31) > 120){
        	            if(map.containsKey(productName)){
        	                List<String> list = map.get(productName);
        	                list.add(country);
        	                map.put(productName, list);
        	            }else{
        	                ArrayList<String> countryList = new ArrayList<String>();
        	                countryList.add(country);
        	                map.put(productName, countryList);
        	            }
        	        }
    	        }
    	        if(sale==0){
    	            if(map.containsKey(productName)){
                        List<String> list = map.get(productName);
                        list.add(country);
                        map.put(productName, list);
                    }else{
                        ArrayList<String> countryList = new ArrayList<String>();
                        countryList.add(country);
                        map.put(productName, countryList);
                    }
    	        }
	        }
	    }
	    return map;
	}
	
	/**
	 * 查出所有产品的总库存和日销量
	 * @return
	 */
	public Map<String, Map<String, List<String>>> getAllStockAndSale(){
	    String sql = " SELECT product_name,country,SUM(total_stock-producting) stock,day31_sales day_sales FROM psi_product_in_stock WHERE data_date=CURDATE()-1 AND day31_sales IS NOT NULL AND (total_stock-producting)>0 AND (country='com' OR country='eu' OR country='jp') GROUP BY country,product_name";
	    List<Object[]> findBySql = advertisingDao.findBySql(sql);
	    Map<String, Map<String, List<String>>> map = new HashMap<String, Map<String,List<String>>>();
	    for(Object[] obj : findBySql){
	        Map<String, List<String>> mapTemp = new HashMap<String, List<String>>();
	        List<String> list = new ArrayList<String>();
	        list.add(obj[2].toString());
	        list.add(obj[3].toString());
	        if(map.containsKey(obj[0].toString())){
	            mapTemp = map.get(obj[0].toString());
	        }
            mapTemp.put(obj[1].toString(), list);
            map.put(obj[0].toString(), mapTemp);
	    }
	    return map;
	}
	
	public String contentOfStorageBySale(){
		Map<String, String> productPositionMap = psiProductEliminateService.findAllProductPosition();
	    LinkedHashMap<String, List<String>> productAndCountryByDaySale = this.getProductAndCountryByDaySale();
	    Map<String, String> lineNameByName = psiProductTypeGroupDictService.getLineNameByName();
	    LinkedHashMap<String, LinkedHashMap<String, List<String>>> productStorag = this.getProductStorag();
	    LinkedHashMap<String, LinkedHashMap<String, Integer>> storageByDay = this.getStorageByDay();
	    Map<String, Map<String, List<String>>> allStockAndSale = this.getAllStockAndSale();
	    Map<String, Map<String, Integer>> get31SalesMap = psiInventoryFbaService.get31SalesMap(null);
	    StringBuilder sb = new StringBuilder();
	    sb.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr  style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>"
	            +" <th rowspan='2'>产品(US)</th><th  rowspan='2'>FBA总库存<br/>在库天数大于90天</th><th  rowspan='2'>日销量</th><th colspan='3'>JP</th>"
	            +" <th colspan='3'>DE</th></tr><tr  style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>总库存不含在产</th><th>日销量</th><th>可售天</th><th>总库存不含在产</th><th>日销量</th><th>可售天</th></tr>");
	    StringBuilder sb1 = new StringBuilder();
	    StringBuilder sb2 = new StringBuilder();
	    StringBuilder sb3 = new StringBuilder();
	    StringBuilder sb10 = new StringBuilder();
        StringBuilder sb20 = new StringBuilder();
        StringBuilder sb30 = new StringBuilder();
	    StringBuilder sbOther = new StringBuilder();
	    for(Entry<String, List<String>> entry : productAndCountryByDaySale.entrySet()){
	        String pName = entry.getKey();
	        for(int i=0;i<entry.getValue().size();i++){
	            StringBuilder sb01 = new StringBuilder();
	            StringBuilder sb02 = new StringBuilder();
	            StringBuilder sb03 = new StringBuilder();
                StringBuilder sb04 = new StringBuilder();
                StringBuilder sb05 = new StringBuilder();
                StringBuilder sb06 = new StringBuilder();
	            String country = entry.getValue().get(i);
	            if(allStockAndSale.get(pName) != null){
                    String lineName= lineNameByName.get(pName)!=null ? "("+lineNameByName.get(pName)+")":"";

	                Map<String, List<String>> countryAndStockAndSale = allStockAndSale.get(pName);
	                List<String> listJp = countryAndStockAndSale.get("jp")!=null?countryAndStockAndSale.get("jp"):null;
	                List<String> listCom = countryAndStockAndSale.get("com")!=null?countryAndStockAndSale.get("com"):null;
	                List<String> listDe = countryAndStockAndSale.get("eu")!=null?countryAndStockAndSale.get("eu"):null;
                    int totalCom = listCom != null ? Integer.parseInt(listCom.get(0)):0;
                    double saleMountOneDayCom = listCom != null ? Math.ceil(Double.parseDouble(listCom.get(1))/31):0;
                    int totalDe = listDe != null ? Integer.parseInt(listDe.get(0)):0;
                    double saleMountOneDayDe = listDe != null ? Math.ceil( Double.parseDouble(listDe.get(1))/31):0;
                    int totalJp = listJp != null ? Integer.parseInt(listJp.get(0)):0;
                    double saleMountOneDayJp = listJp != null ?Math.ceil(Double.parseDouble(listJp.get(1))/31):0;
    	            if("com".equals(country)){
                           if(totalJp != 0 && saleMountOneDayJp != 0 && totalJp/saleMountOneDayJp<90){
        	                  sb01.append((int)Math.ceil(totalJp/saleMountOneDayJp));
        	                  sb03.append(totalJp);
        	                  sb04.append((int)Math.ceil(saleMountOneDayJp));
        	               }
        	               if(totalDe != 0 && saleMountOneDayDe != 0 && totalDe/saleMountOneDayDe<90){
        	                  sb02.append((int)Math.ceil(totalDe/saleMountOneDayDe));
        	                  sb05.append(totalDe);
                              sb06.append((int)Math.ceil(saleMountOneDayDe));
        	               }
        	               if(!"".equals(sb01.toString()) || !"".equals(sb02.toString())){
        	                  if(!"4".equals(productPositionMap.get(pName+"_"+country))){
        	                      sb1.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#DBEEEE;'>"
                                          +"<td>"+pName+lineName+"</td><td>"+storageByDay.get(pName).get(country)+"</td><td>"+(int)Math.ceil((double)(get31SalesMap.get(country).get(pName))/31)+"</td><td>"+sb03.toString()+"</td><td>"+sb04.toString()+"</td><td>"+sb01.toString()+"</td><td>"+sb05.toString()+"</td><td>"+sb06.toString()+"</td><td>"+sb02.toString()+"</td></tr>");
        	                  }else{
        	                      sb10.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#FF8080;'>"
                                          +"<td>"+pName+lineName+"</td><td>"+storageByDay.get(pName).get(country)+"</td><td>"+(int)Math.ceil((double)(get31SalesMap.get(country).get(pName))/31)+"</td><td>"+sb03.toString()+"</td><td>"+sb04.toString()+"</td><td>"+sb01.toString()+"</td><td>"+sb05.toString()+"</td><td>"+sb06.toString()+"</td><td>"+sb02.toString()+"</td></tr>");
        	                  }
        	               }
    	             }
	                 if("jp".equals(country)){
                           if(totalCom != 0 && saleMountOneDayCom != 0 && totalCom/saleMountOneDayCom<90){
	                            sb01.append((int)Math.ceil(totalCom/saleMountOneDayCom));
	                            sb03.append(totalCom);
	                            sb04.append((int)Math.ceil(saleMountOneDayCom));
	                        }
	                        if(totalDe != 0 && saleMountOneDayDe != 0 && totalDe/saleMountOneDayDe<90){
	                            sb02.append((int)Math.ceil(totalDe/saleMountOneDayDe));
	                            sb05.append(totalDe);
	                            sb06.append((int)Math.ceil(saleMountOneDayDe));
	                        }
	                        if(!"".equals(sb01.toString()) || !"".equals(sb02.toString())){
	                            if(!"4".equals(productPositionMap.get(pName+"_"+country))){
	                                sb2.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#DBEEEE;'>"
	                                        +"<td>"+pName+lineName+"</td><td>"+storageByDay.get(pName).get(country)+"</td><td>"+(int)Math.ceil((double)(get31SalesMap.get(country).get(pName))/31)+"</td><td>"+sb03.toString()+"</td><td>"+sb04.toString()+"</td><td>"+sb01.toString()+"</td><td>"+sb05.toString()+"</td><td>"+sb06.toString()+"</td><td>"+sb02.toString()+"</td></tr>");
	                            }else{
	                                sb20.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#FF8080;'>"
	                                        +"<td>"+pName+lineName+"</td><td>"+storageByDay.get(pName).get(country)+"</td><td>"+(int)Math.ceil((double)(get31SalesMap.get(country).get(pName))/31)+"</td><td>"+sb03.toString()+"</td><td>"+sb04.toString()+"</td><td>"+sb01.toString()+"</td><td>"+sb05.toString()+"</td><td>"+sb06.toString()+"</td><td>"+sb02.toString()+"</td></tr>");
	                            }
	                        }
	                   }
                    if("de".equals(country)){
                            if(totalCom != 0 && saleMountOneDayCom != 0 && totalCom/saleMountOneDayCom<90){
                                sb01.append((int)Math.ceil(totalCom/saleMountOneDayCom));
                                sb03.append(totalCom);
                                sb04.append((int)Math.ceil(saleMountOneDayCom));
                            }
                            if(totalJp != 0 && saleMountOneDayJp != 0 && totalJp/saleMountOneDayJp<90){
                                sb02.append((int)Math.ceil(totalJp/saleMountOneDayJp));
                                sb05.append(totalJp);
                                sb06.append((int)Math.ceil(saleMountOneDayJp));
                                
                            }
                            if(!"".equals(sb01.toString()) || !"".equals(sb02.toString())){
                                if(!"4".equals(productPositionMap.get(pName+"_"+country))){
                                    sb3.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#DBEEEE;'>"
                                          +"<td>"+pName+lineName+"</td><td>"+storageByDay.get(pName).get(country)+"</td><td>"+(int)Math.ceil((double)(get31SalesMap.get(country).get(pName))/31)+"</td><td>"+sb03.toString()+"</td><td>"+sb04.toString()+"</td><td>"+sb01.toString()+"</td><td>"+sb05.toString()+"</td><td>"+sb06.toString()+"</td><td>"+sb02.toString()+"</td></tr>");
                                }else{
                                    sb30.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#FF8080;'>"
                                          +"<td>"+pName+lineName+"</td><td>"+storageByDay.get(pName).get(country)+"</td><td>"+(int)Math.ceil((double)(get31SalesMap.get(country).get(pName))/31)+"</td><td>"+sb03.toString()+"</td><td>"+sb04.toString()+"</td><td>"+sb01.toString()+"</td><td>"+sb05.toString()+"</td><td>"+sb06.toString()+"</td><td>"+sb02.toString()+"</td></tr>");
                                }
                            }
                        }
                    }
	           }
	        }
	        if(!"".equals(sb1.toString())){
	            sb.append(sb1.toString());
	        }
	        if(!"".equals(sb10.toString())){
                sb.append(sb10.toString());
            }
	        if(!"".equals(sb2.toString()) || !"".equals(sb20.toString())){
	            sb.append("<tr  style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>"
                +" <th rowspan='2'>产品(JP)</th><th  rowspan='2'>FBA总库存<br/>在库天数大于90天</th><th  rowspan='2'>日销量</th><th colspan='3'>US</th>"
                +" <th colspan='3'>DE</th></tr><tr  style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>总库存不含在产</th><th>日销量</th><th>可售天</th><th>总库存不含在产</th><th>日销量</th><th>可售天</th></tr>"+sb2.toString());
	        }
	        if(!"".equals(sb20.toString())){
                sb.append(sb20.toString());
            }
	        if(!"".equals(sb3.toString()) || !"".equals(sb30.toString())){
	            sb.append("<tr  style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>"
                +" <th rowspan='2'>产品(DE)</th><th  rowspan='2'>FBA总库存<br/>在库天数大于90天</th><th  rowspan='2'>日销量</th><th colspan='3'>US</th>"
                +" <th colspan='3'>JP</th></tr><tr  style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>总库存不含在产</th><th>日销量</th><th>可售天</th><th>总库存不含在产</th><th>日销量</th><th>可售天</th></tr>"+sb3.toString());
	        }
	        if(!"".equals(sb30.toString())){
                sb.append(sb30.toString());
            }
	        sb.append("</table>");
	        StringBuilder sbFinal = new StringBuilder();
	        if("".equals(sb1.toString()) && "".equals(sb2.toString()) && "".equals(sb3.toString())){
	            sbFinal.append("");  
	        }else{
	            sbFinal.append(sb.toString());
	        }
	    return sbFinal.toString();
	}

	public List<Map.Entry<String, Integer>> sortByValue(List<Map.Entry<String, Integer>> list){
	    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() { 
            public int compare(Map.Entry<String, Integer> o1, 
              Map.Entry<String, Integer> o2) { 
              return (o2.getValue() - o1.getValue()); 
            } 
        }); 
	    return list;
	}
	
	public String getCountryAndStockContent(List<Map.Entry<String,Integer>> list){
	    StringBuilder sb = new StringBuilder();
	    for(Map.Entry<String,Integer> mapping:list){ 
            sb.append(mapping.getKey()+":"+mapping.getValue()+" "); 
	    }
	    return sb.toString();
	}
	
	/**
	 * 获取单个产品的体积
	 * @return
	 */
	public Map<String, Double> getVolume(){
	   String sql =  "SELECT brand,model,color,box_volume/pack_quantity FROM psi_product";
	   List<Object[]> findBySql = advertisingDao.findBySql(sql);
	   Map<String, Double> map = new HashMap<String, Double>();
	   for(Object[] obj:findBySql){
           if(obj[2].toString().contains(",")){
               String arr[] = obj[2].toString().split(",");
               for(int i=0;i<arr.length;i++){
                   map.put(obj[0].toString()+" "+obj[1].toString()+"_"+arr[i], Double.parseDouble(obj[3].toString()));
               }
           }else if("".equals(obj[2].toString())){
               map.put(obj[0].toString()+" "+obj[1].toString(), Double.parseDouble(obj[3].toString()));
           }else{
               map.put(obj[0].toString()+" "+obj[1].toString()+"_"+obj[2].toString(), Double.parseDouble(obj[3].toString()));
           }
       }
	   System.out.println(map);
	   return map;
	}
}
