package com.springrain.erp.modules.amazoninfo.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.modules.amazoninfo.dao.ProductCatelogDao;
import com.springrain.erp.modules.amazoninfo.entity.CategoryDto;

@Component
@Transactional(readOnly = true)
public class CategoryCountService {

	@Autowired
	private ProductCatelogDao productCatelogDao;
	
	/*
		 INSERT INTO `category_merchant_name`( merchant_customer_id,
	  marketplace,NAME )  SELECT  merchant_customer_id,
	  marketplace ,SUBSTRING_INDEX(GROUP_CONCAT(aa.`merchant_brand_name` ORDER BY num DESC ),',',1) FROM (SELECT 
	  merchant_customer_id,
	  marketplace,
	  a.`merchant_brand_name`,
	  SUM(a.`units_net_ordered_3p_cw`) AS num 
	FROM
	  category_product_info_09 a 
	WHERE data_type = '1' 
	  AND a.`merchant_brand_name` IS NOT NULL 
	GROUP BY merchant_customer_id,
	  marketplace,
	  a.`merchant_brand_name` ) aa  GROUP BY  merchant_customer_id,
	  marketplace 
	 */
	
	public List<Object[]> findMerchantsInfoByCountry(String country){
		
		String sql = "SELECT b.name,SUM(a.asin_total_glance_view_count_cw) AS sessions , SUM(units_net_ordered_3p_cw) AS cw ,round((SUM(units_net_ordered_3p_cw)*100/SUM(a.asin_total_glance_view_count_cw)),2) AS con ,ROUND(SUM(a.net_shipped_gms_3p_cw),2) AS sales, ROUND(SUM(a.net_shipped_gms_3p_cw)/SUM(units_net_ordered_3p_cw),2) "+
				   " ,a.merchant_customer_id FROM category_product_info_09 a ,category_merchant_name b WHERE a.merchant_customer_id = b.merchant_customer_id  AND a.marketplace = b.marketplace AND  data_type = 1 "+
                   " AND a.marketplace =:p1 GROUP BY a.merchant_customer_id ORDER BY cw DESC";
		
		return productCatelogDao.findBySql(sql,new Parameter(country));
    }
	
	
	public List<Object[]> findAccountInfo(Long merchantCustomerId,String country){
		String sql = "SELECT week_ending_day,SUM(a.asin_total_glance_view_count_cw) AS sessions , SUM(units_net_ordered_3p_cw),ROUND(SUM(units_net_ordered_3p_cw)/7,0) AS avg_net,SUM(units_net_ordered_3p_cw)*100/SUM(a.asin_total_glance_view_count_cw) AS con ,ROUND(SUM(a.net_shipped_gms_3p_cw),2) AS sales, ROUND(SUM(a.net_shipped_gms_3p_cw)/SUM(units_net_ordered_3p_cw),2)"+ 
				  "  FROM category_product_info_09 a WHERE data_type = 1 "+
                  "  AND marketplace = :p2 AND merchant_customer_id = :p1 GROUP BY week_ending_day ORDER BY week_ending_day";
		return productCatelogDao.findBySql(sql,new Parameter(merchantCustomerId,country));
    }
	
	public List<Object[]> findAccountCategoryInfo(Long merchantCustomerId,String country){
		String sql = "SELECT product_group,SUM(a.asin_total_glance_view_count_cw) AS sessions , SUM(units_net_ordered_3p_cw) AS cw,SUM(units_net_ordered_3p_cw)*100/SUM(a.asin_total_glance_view_count_cw) AS con ,ROUND(SUM(a.net_shipped_gms_3p_cw),2) AS sales, ROUND(SUM(a.net_shipped_gms_3p_cw)/SUM(units_net_ordered_3p_cw),2) "+
					 "	FROM category_product_info_09 a WHERE data_type = 1 "+
					 "	AND marketplace = :p2 AND merchant_customer_id = :p1 and net_shipped_gms_3p_cw>0 GROUP BY product_group ORDER BY cw DESC ";
		return productCatelogDao.findBySql(sql,new Parameter(merchantCustomerId,country));
    }
	
	
	public List<Object[]> findAccountSingleCategoryInfo(Long merchantCustomerId,String country,String categoryName){
		String sql = "SELECT a.asin_name,a.asin,SUM(a.asin_total_glance_view_count_cw) AS sessions , SUM(units_net_ordered_3p_cw) AS cw,ROUND(SUM(units_net_ordered_3p_cw)*100/SUM(a.asin_total_glance_view_count_cw),2) AS con ,ROUND(SUM(a.net_shipped_gms_3p_cw),2) AS sales, ROUND(SUM(a.net_shipped_gms_3p_cw)/SUM(units_net_ordered_3p_cw),2)  FROM category_product_info_09 a WHERE data_type = 1"+ 
					 "	AND marketplace = :p2 AND merchant_customer_id = :p1 AND product_group=:p3 AND a.net_shipped_gms_3p_cw>0 AND units_net_ordered_3p_cw>0 GROUP BY a.asin   ORDER BY cw DESC";
		return productCatelogDao.findBySql(sql,new Parameter(merchantCustomerId,country,categoryName));
    }
	
	public List<Object[]> findAccountProductInfo(Long merchantCustomerId,String country,String categoryName,String asin){
		String sql = "SELECT a.week_ending_day ,a.asin_total_glance_view_count_cw AS sessions , units_net_ordered_3p_cw AS cw,ROUND((units_net_ordered_3p_cw/7),0) AS avg_net,ROUND((units_net_ordered_3p_cw*100/a.asin_total_glance_view_count_cw),2) AS con ,a.net_shipped_gms_3p_cw AS sales, ROUND((a.net_shipped_gms_3p_cw/units_net_ordered_3p_cw),2)  FROM category_product_info_09 a WHERE data_type = 1 "+
					"	AND marketplace = :p2 AND merchant_customer_id = :p1 AND product_group=:p3 AND a.asin = :p4 AND a.net_shipped_gms_3p_cw>0 AND units_net_ordered_3p_cw>0    ORDER BY a.week_ending_day DESC ";
		return productCatelogDao.findBySql(sql,new Parameter(merchantCustomerId,country,categoryName,asin));
    }
	
	/*
	 
	 INSERT INTO  category_num SELECT a.cataloglinkid,COUNT(1) AS num FROM category_asin_rank a,(SELECT a.`asin` FROM category_product_info_09 a WHERE a.`data_type` = '0'  GROUP BY a.`asin`) b WHERE  a.`asin` = b.`asin` GROUP BY a.`cataloglinkid` 
		
		SELECT SUM(net_shipped_gms_3p_cw) AS net_cw,
		SUM(a.`units_net_ordered_3p_cw`) AS units_cw, 
		a.`asin`,a.`week_ending_day`,b.`rank`
		FROM category_product_info_09 a  , category_asin_rank b  WHERE a.`asin` = b.`asin` AND b.`cataloglinkid` = '430172031'
		AND week_ending_day >= '2018-02-09' AND week_ending_day<= '2018-02-24' 
		AND data_type = 0 AND net_shipped_gms_3p_cw >0 AND units_net_ordered_3p_cw>0  GROUP BY a.`asin`,a.`week_ending_day`	
		
	*/
	
	public List<Object[]> countCategoryInfo(String country,String categoryId){
		String sql = "SELECT a.`merchant_brand_name`, SUM(a.`units_net_ordered_3p_cw`) AS units_cw,SUM(net_shipped_gms_3p_cw) AS net_cw,round((SUM(net_shipped_gms_3p_cw) /SUM(a.`units_net_ordered_3p_cw`)),2),"+
			"	GROUP_CONCAT(DISTINCT b.`rank` ORDER BY b.`rank`)"+
			"	FROM category_product_info_09 a  , category_asin_rank b  WHERE a.`asin` = b.`asin` AND b.`cataloglinkid` = :p1 "+
			"	AND week_ending_day >= '2018-01-26' AND week_ending_day<= '2018-02-24' AND a.`marketplace` =:p2 "+
			"	AND data_type = 0 AND net_shipped_gms_3p_cw >0 AND units_net_ordered_3p_cw>0  GROUP BY a.`merchant_brand_name` ";
		return productCatelogDao.findBySql(sql,new Parameter(categoryId,country));
    }
	
	public List<Map<String, Object>> findCategoryTree(String country){
		List<Map<String, Object>> rs = Lists.newArrayList();
		if("us".equalsIgnoreCase(country)){
			country = "com";
		}
		String sql ="SELECT a.`id`,a.`parent_id`,a.`catalog_name`,a.`path_id`,b.`num` FROM amazoninfo_product_catalogues a,category_num b WHERE a.`country` = :p1 AND a.`path_id` = b.`category_linkid`  GROUP BY b.`category_linkid`";
		List<Object[]> list = productCatelogDao.findBySql(sql,new Parameter(country));
		List<Object> pids = Lists.newArrayList();
		Set<Object> ids = Sets.newHashSet();
		for (Object[] objects : list) {
			Map<String,Object> map = Maps.newHashMap();
			rs.add(map);
			ids.add(objects[0]);
			map.put("id", objects[0]);
			Object pid = objects[1];
			if(pid!=null){
				pids.add(pid);
			}
			map.put("pId", pid);
			map.put("name",objects[2]+"(采样数"+objects[4]+")");
			map.put("title",objects[3]);
			map.put("falg",true);
		}
		
		sql = "SELECT a.`id`,a.`parent_id`,a.`catalog_name`,a.`path_id` FROM amazoninfo_product_catalogues a WHERE a.`country` = :p1 and id in :p2";
		while(pids.size()>0){
			list = productCatelogDao.findBySql(sql,new Parameter(country,pids));
			pids = Lists.newArrayList();
			for (Object[] objects : list) {
				Map<String,Object> map = Maps.newHashMap();
				rs.add(map);
				map.put("id", objects[0]);
				Object pid = objects[1];
				if(pid!=null){
					if(!ids.contains(pid)){
						pids.add(pid);
						ids.add(pid);
					}
				}
				map.put("pId", pid);
				map.put("name",objects[2]);
				map.put("title",objects[3]);
				map.put("falg",false);
			}
		}
		return rs;
	}
	
	
	public List<CategoryDto> findBrand(String catalog,String country,String startDate) throws ParseException{
		String sql="SELECT ROUND(SUM(r.`sales`),2) sales,SUM(r.quantity) qty,(CASE WHEN r.sales/r.quantity<25 THEN '1' WHEN r.sales/r.quantity<50 THEN '2'  WHEN r.sales/r.quantity<100 THEN '3' ELSE '4' END) priceType,count(*),COUNT(DISTINCT r.`brand`) "+
				" FROM category_product_report r WHERE country=:p1 and data_date=:p2 and (r.`catalog`=:p3 or r.`catalog` like :p4 or r.`catalog` like :p5 or r.`catalog` like :p6)   GROUP BY priceType ORDER BY priceType ";
	    List<Object[]> list = productCatelogDao.findBySql(sql,new Parameter(country,new SimpleDateFormat("yyyy-MM-dd").parse(startDate),catalog,catalog+",%","%,"+catalog+",%","%,"+catalog));
	    List<CategoryDto> dtoList=Lists.newArrayList();
	    Integer totalQty=0;
		Float totalSales=0f;
		Integer num=0;
		Integer brandNum=0;
	    for (Object[] obj : list) {
	    	CategoryDto dto=new CategoryDto();
			dto.setBrand(obj[2].toString());
			dto.setSales(Float.parseFloat(obj[0].toString()));
			dto.setQuantity(Integer.parseInt(obj[1].toString()));
			dto.setNum(Integer.parseInt(obj[3].toString()));
			dto.setBrandNum(Integer.parseInt(obj[4].toString()));
			dtoList.add(dto);
			totalQty+=Integer.parseInt(obj[1].toString());
			totalSales+=Float.parseFloat(obj[0].toString());
			num +=Integer.parseInt(obj[3].toString());
			brandNum +=Integer.parseInt(obj[4].toString());
		}
	    CategoryDto dto=new CategoryDto();
		dto.setBrand("<b>Total</b>");
		dto.setSales(totalSales);
		dto.setQuantity(totalQty);
		dto.setNum(num);
		dto.setBrandNum(brandNum);
		dtoList.add(dto);
		return dtoList;
	}
	
	public List<CategoryDto> findBrand(String catalog,String country,String startDate,String price) throws ParseException{
		String temp =" ";
		if(StringUtils.isNotBlank(price)){
			if(price.contains("+")){
				Float lowerPrice = Float.parseFloat(price.replace("+",""));
				temp += " and r.`sales`/r.quantity>="+lowerPrice;
			}else{
				String[] arr = price.split("~");
				Float lowerPrice = Float.parseFloat(arr[0]);
				Float higherPrice = Float.parseFloat(arr[1]);
				temp += " and r.`sales`/r.quantity>="+lowerPrice+" and r.`sales`/r.quantity<"+higherPrice+" ";
			
			  }
		}
		String sql="SELECT r.`brand`,ROUND(SUM(r.`sales`),2) sales,SUM(r.quantity) qty,GROUP_CONCAT(concat(concat(concat(r.`asin`,'_',r.quantity),'_',r.sales)),'_',r.brand) ASIN,ROUND(SUM(r.sales)/SUM(r.quantity),2) "+
					" FROM category_product_report r WHERE country=:p1 and data_date=:p2 and (r.`catalog`=:p3 or r.`catalog` like :p4 or r.`catalog` like :p5 or r.`catalog` like :p6)  "+temp+" GROUP BY r.brand ";
		List<Object[]> list = productCatelogDao.findBySql(sql,new Parameter(country,new SimpleDateFormat("yyyy-MM-dd").parse(startDate),catalog,catalog+",%","%,"+catalog+",%","%,"+catalog));
		List<CategoryDto> dtoList=Lists.newArrayList();
		if(list!=null&&list.size()>0){
			Integer totalQty=0;
			Float totalSales=0f;
			String asin="";
			for (int i=0;i<list.size();i++) {
				Object[] obj=list.get(i);
				totalSales += Float.parseFloat(obj[1].toString());
				totalQty+= Integer.parseInt(obj[2].toString());
				if(i==list.size()-1){
					asin = asin + obj[3].toString() ;
				}else{
					asin = asin + obj[3].toString() +",";
				}
				
			}
			CategoryDto total=new CategoryDto();
			total.setBrand("<b>Total</b>");
			total.setSales(totalSales);
			total.setQuantity(totalQty);
			total.setPrice(totalSales/totalQty);
			total.setAsin(asin);
			dtoList.add(total);
			for (Object[] obj : list) {
				CategoryDto dto=new CategoryDto();
				dto.setBrand(obj[0].toString());
				dto.setSales(Float.parseFloat(obj[1].toString()));
				dto.setQuantity(Integer.parseInt(obj[2].toString()));
				dto.setPrice(Float.parseFloat(obj[4].toString()));
				dto.setSalesRate(dto.getSales()*100/totalSales);
				dto.setQtyRate(dto.getQuantity()*100f/totalQty);
				String asins = obj[3].toString();
				dto.setAsin(asins);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}
	
	public List<CategoryDto> findSiblings(List<String> catalogs,String country,String startDate,String price) throws ParseException{
		String temp =" ";
		if(StringUtils.isNotBlank(price)){
			if(price.contains("+")){
				Float lowerPrice = Float.parseFloat(price.replace("+",""));
				temp += " and r.`sales`/r.quantity>="+lowerPrice;
			}else{
				String[] arr = price.split("~");
				Float lowerPrice = Float.parseFloat(arr[0]);
				Float higherPrice = Float.parseFloat(arr[1]);
				temp += " and r.`sales`/r.quantity>="+lowerPrice+" and r.`sales`/r.quantity<"+higherPrice+" ";
			}
		}
		String sql="SELECT ROUND(SUM(r.sales),2) sales,SUM(r.quantity) qty,ROUND(SUM(r.sales)/SUM(r.quantity),2) "+
					" FROM category_product_report r WHERE country=:p1 and data_date=:p2 and (r.`catalog`=:p3 or r.`catalog` like :p4 or r.`catalog` like :p5 or r.`catalog` like :p6)  "+temp;
		List<CategoryDto> dtoList=Lists.newArrayList();
		Integer totalQty=0;
		Float totalSales=0f;
		for (String catalog: catalogs) {
			List<Object[]> list = productCatelogDao.findBySql(sql,new Parameter(country,new SimpleDateFormat("yyyy-MM-dd").parse(startDate),catalog,catalog+",%","%,"+catalog+",%","%,"+catalog));
			if(list!=null&&list.size()>0){
				Object[] obj=list.get(0);
				if(obj!=null&&obj[0]!=null){
					CategoryDto dto=new CategoryDto();
					dto.setCatalog(catalog);
					dto.setSales(Float.parseFloat(obj[0].toString()));
					dto.setQuantity(Integer.parseInt(obj[1].toString()));
					dto.setPrice(Float.parseFloat(obj[2].toString()));
					dtoList.add(dto);
					totalSales += Float.parseFloat(obj[0].toString());
					totalQty+= Integer.parseInt(obj[1].toString());
				}
			}
		}
		CategoryDto total=new CategoryDto();
		total.setCatalog("<b>Total</b>");
		total.setSales(totalSales);
		total.setQuantity(totalQty);
		total.setPrice(totalSales/totalQty);
		dtoList.add(total);
		return dtoList;
	}	
	
	
	/*INSERT INTO category_product_report(country,ASIN,`catalog`,`data_date`,brand,sales,quantity)
	SELECT country,ASIN,`catalog`,`data_date`,SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT merchant_friendly_name ORDER BY qty DESC),',',1) NAME,SUM(sales),SUM(qty) FROM (
	SELECT country,ASIN,t.`catalog`,t.`data_date`,t.`merchant_friendly_name`,SUM(t.`gms_shipped_cw`) sales,SUM(t.`units_new_shipped_3p_cw`+t.`units_new_shipped_retail_cw`) qty
	FROM category_product_info_report t  WHERE t.`asp` IS NOT NULL AND t.`asp`>0 AND merchant_friendly_name IS NOT NULL AND t.`catalog` IS NOT NULL AND t.`catalog`!=''
	GROUP BY t.`country`,t.`asin`,t.`catalog`,t.`data_date`,t.`merchant_friendly_name`) a GROUP BY country,ASIN,`catalog`,`data_date`;


	INSERT INTO category_report_num(category_linkid,num,avg_price,lower_price,higher_price)
	SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.catalog,',',b.help_topic_id+1),',',-1) catalog,COUNT(*),AVG(a.sales/a.quantity),MIN(a.sales/a.quantity),MAX(a.sales/a.quantity)
	FROM category_product_report a JOIN mysql.help_topic b 
	ON b.help_topic_id < (LENGTH(a.catalog) - LENGTH(REPLACE(a.catalog,',',''))+1) 
	GROUP BY SUBSTRING_INDEX(SUBSTRING_INDEX(a.catalog,',',b.help_topic_id+1),',',-1);
*/
}
