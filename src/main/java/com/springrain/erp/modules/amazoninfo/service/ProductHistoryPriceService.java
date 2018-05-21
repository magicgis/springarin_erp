/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.amazoninfo.dao.ProductHistoryPriceDao;
import com.springrain.erp.modules.amazoninfo.entity.ProductHistoryPrice;

/**
 * 产品历史价格Service
 * @author Tim
 * @version 2015-04-10
 */
@Component
@Transactional(readOnly = true)
public class ProductHistoryPriceService extends BaseService {

	@Autowired
	private ProductHistoryPriceDao productHistoryPriceDao;
	
	public ProductHistoryPrice get(String id) {
		return productHistoryPriceDao.get(id);
	}
	
	private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	//国家[时间 数据]
	public Map<String,Map<String,ProductHistoryPrice>> find(Set<String> skus,Date start,Date end,String cty) {
		String sql = "SELECT a.`country`, a.`data_date`, MIN(CASE WHEN a.`sku` LIKE '%local%' THEN a.`sale_price`+3.99   ELSE a.`sale_price` END ) ,GROUP_CONCAT(CONCAT(a.`sku`,':',a.sale_price,'<br/>') ORDER BY a.`sale_price`) FROM amazoninfo_product_history_price a WHERE country=:p1 AND a.`data_date`>=:p2 AND a.`data_date` <= :p3 AND a.`sku` IN :p4 and a.`sale_price`>0    "+ 
					 "	GROUP BY a.`country`, a.`data_date` order by a.country,a.data_date";
		List<Object[]> list = productHistoryPriceDao.findBySql(sql,new Parameter(cty,start,end,skus));
		Map<String,Map<String,ProductHistoryPrice>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String country = objs[0].toString();
			Map<String,ProductHistoryPrice> map = rs.get(country);
			if(map==null){
				map = Maps.newHashMap();
				rs.put(country, map);
			}
			String date = dateFormat.format((Date)objs[1]);
			map.put(date, new ProductHistoryPrice((Date)objs[1], objs[3].toString(), Float.parseFloat(objs[2].toString()), country));
		}
		return rs;
	}
	
	
	
	/**
	 * 
	 * 产品[时间 数据]
	 * 
	 */
	public Map<String,Map<String,Float>> getMulProductPrice(String[] productNames,String start,String end,String country,String searchFlag) {
		String typeSql = "'%Y-%m-%d'";
		//查询2个时间节点
		if("1".equals(searchFlag)){
			typeSql="'%x-%v'";//按周查询
			if ("2016-01".equals(start)) {
				start = "2015-53";
			}
			if ("2016-01".equals(end)) {
				end = "2015-53";
			}
			start = getWeekStr(start);
		}else if("2".equals(searchFlag)){
			typeSql="'%Y-%m'";//按月查询
		}
		
		
		String sql = "SELECT DATE_FORMAT(a.`data_date`,"+typeSql+") dates,b.proName,MIN(CASE WHEN a.`sku` LIKE '%local%' THEN a.`sale_price`+3.99   ELSE a.`sale_price` END ) AS minPrice FROM amazoninfo_product_history_price AS a  INNER JOIN (SELECT DISTINCT a.sku,(CASE WHEN a.color='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END) AS proName " +
				" FROM psi_sku AS a WHERE a.`del_flag`='0' AND a.product_name !='Inateck other' AND a.product_name !='Inateck Old' AND  a.`del_flag`='0' ) AS b " +
				" ON a.`sku`=b.sku WHERE  DATE_FORMAT(a.`data_date`,"+typeSql+")>= :p2 AND DATE_FORMAT(a.`data_date`,"+typeSql+") <= :p3 " +
				" AND b.proName IN :p1 AND a.country=:p4  " +
				" AND a.`sale_price`>0 GROUP BY dates,b.proName ORDER BY a.`data_date` ";
		List<Object[]> list = productHistoryPriceDao.findBySql(sql,new Parameter(productNames,start,end,country));
		Map<String,Map<String,Float>> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			String date = objs[0].toString();
			String name = objs[1].toString();
			Float price = Float.parseFloat(objs[2].toString());
			Map<String,Float> inMap = null;
			if(rs.get(name)==null){
				inMap = Maps.newHashMap();
			}else{
				inMap=rs.get(name);
			}
			inMap.put(date, price);
			rs.put(name, inMap);
		}
		
		return rs;
	}
	
	
	private static String getWeekStr(String date){
		if ("2016-01".equals(date)) {
			date = "2015-53";
		} else if(date.startsWith("2016")){
			Integer i = Integer.parseInt(date.split("-")[1]);
			i = i-1;
			date = "2016-"+(i<10?("0"+i):i);
		} 
		return date;
	}
	
	
	@Transactional(readOnly = false)
	public void save(List<ProductHistoryPrice> productHistoryPrices) {
		productHistoryPriceDao.save(productHistoryPrices);
	}
	
}
