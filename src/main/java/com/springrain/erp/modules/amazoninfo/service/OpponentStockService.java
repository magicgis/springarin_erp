/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xml.resolver.apps.resolver;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.OpponentStockDao;
import com.springrain.erp.modules.amazoninfo.entity.OpponentStock;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectoryComment;

/**
 * 对手库存追踪
 * @author Michael
 * @version 2015-03-03
 */
@Component
@Transactional(readOnly = true)
public class OpponentStockService extends BaseService {
	@Autowired
	private OpponentStockDao opponentStockDao;
	
	public List<OpponentStock> find(OpponentStock opponentStock,boolean diffFlag,Date startDay) {
		DetachedCriteria dc = opponentStockDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(opponentStock.getAsin())){
			dc.add(Restrictions.eq("asin", opponentStock.getAsin()));
		}
		if(StringUtils.isNotEmpty(opponentStock.getCountry())){
			dc.add(Restrictions.eq("country", opponentStock.getCountry()));
		}
		if(diffFlag){
			dc.add(Restrictions.isNotNull("diffQuantity"));
		}
		if(startDay!=null){
			dc.add(Restrictions.gt("dataDate", startDay));
		}
		dc.addOrder(Order.desc("dataDate"));
		return opponentStockDao.find(dc);
	}
	
	
   @Transactional(readOnly = false)
   public void save(OpponentStock opponentStock){
		this.opponentStockDao.save(opponentStock);
	}
	
   
	/**
	 * 查询昨天的库存
	 * 
	 */
	public Map<String,Integer> getYesterdayStock(Date date){
		Map<String,Integer> yesMap=Maps.newHashMap();
		DetachedCriteria dc = opponentStockDao.createDetachedCriteria();
		dc.add(Restrictions.eq("dataDate", date));
		List<OpponentStock> list = this.opponentStockDao.find(dc);
		if(list!=null&&list.size()>0){
			for(OpponentStock oppo:list){
				String key = oppo.getAsin()+","+oppo.getCountry();
				yesMap.put(key, oppo.getQuantity());
			}
		}
		return yesMap;
	}
	
	/**
	 * 查询昨天销量
	 * 
	 */
	public Map<String,Integer> getYesterdaySale(Set<String> asins,String country,Date yesterDay){
		String sql="SELECT a.`asin`,a.`diff_quantity` FROM amazoninfo_opponent_stock AS a WHERE a.`diff_quantity` IS NOT NULL AND a.`asin` IN :p3 AND a.`country`=:p1 AND a.`data_date`=:p2";
		Map<String,Integer> asinSaleMap = Maps.newHashMap();
		List<Object[]> list = this.opponentStockDao.findBySql(sql,new Parameter(country,yesterDay,asins));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				asinSaleMap.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
			}
		}
		return asinSaleMap;
	}
	
	
	
	/**
	 * 获得30天销量 asin,数量
	 * 
	 */
	public Map<String,Integer>  getAsin30Sale(Set<String> asins,String country,Date startDate){
		String sql="SELECT a.`asin`,AVG(a.`diff_quantity`) FROM amazoninfo_opponent_stock AS a WHERE a.`diff_quantity` IS NOT NULL AND a.`asin` IN :p3 AND a.`country`=:p1 AND a.`data_date`>=:p2 GROUP BY a.`asin`";
		Map<String,Integer> asinSaleMap = Maps.newHashMap();
		List<Object[]> list = this.opponentStockDao.findBySql(sql,new Parameter(country,startDate,asins));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				asinSaleMap.put(obj[0].toString(), new BigDecimal(obj[1].toString()).multiply(new BigDecimal(30)).intValue());
			}
		}
		return asinSaleMap;
	}
	
	/**
	 * 获得30天销量 asin,数量
	 * 
	 */
	public Map<String,Map<String,Integer>>  getAsin30Sale(Date startDate){
		String sql="SELECT a.`asin`,AVG(a.`diff_quantity`),a.`country` FROM amazoninfo_opponent_stock AS a WHERE a.`diff_quantity` IS NOT NULL AND a.`data_date`>=:p1 GROUP BY a.`asin`,a.`country` ";
		Map<String,Map<String,Integer>> resMap = Maps.newHashMap();
		List<Object[]> list = this.opponentStockDao.findBySql(sql,new Parameter(startDate));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String country = obj[2].toString();
				Map<String,Integer> inMap = null;
				if(resMap.get(country)==null){
					inMap = Maps.newHashMap();
				}else{
					inMap = resMap.get(country);
				}
				inMap.put(obj[0].toString(), new BigDecimal(obj[1].toString()).multiply(new BigDecimal(30)).intValue());
				resMap.put(country, inMap);
			}
		}
		return resMap;
	}
	
}
