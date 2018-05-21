/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.psi.dao.StockDao;
import com.springrain.erp.modules.psi.entity.Stock;

/**
 * 仓库Service
 * @author tim
 * @version 2014-11-17
 */
@Component
@Transactional(readOnly = true)
public class StockService extends BaseService {

	@Autowired
	private StockDao stockDao;
	
	public Stock get(Integer id) {
		return stockDao.get(id);
	}
	
	public Page<Stock> find(Page<Stock> page, Stock stock) {
		DetachedCriteria dc = stockDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(stock.getStockSign())){
			dc.add(Restrictions.like("name", "%"+stock.getStockSign()+"%"));
		}
		dc.addOrder(Order.asc("type"));
		dc.add(Restrictions.eq(Stock.FIELD_DEL_FLAG, Stock.DEL_FLAG_NORMAL));
		return stockDao.find(page, dc);
	}
	
	
	/**
	 * 查询本地仓库
	 * 0:本地仓库；1FBA仓库；2所有仓库
	 */
	public List<Stock> findStocks(String type) {
		DetachedCriteria dc = stockDao.createDetachedCriteria();
		dc.add(Restrictions.eq(Stock.FIELD_DEL_FLAG, Stock.DEL_FLAG_NORMAL));
		if(!"2".equals(type)){
			dc.add(Restrictions.eq("type", type));
		}
		dc.addOrder(Order.asc("id"));
		return stockDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void save(Stock stock) {
		stockDao.save(stock);
	}
	
	public Stock findByName(String name){
		DetachedCriteria dc = stockDao.createDetachedCriteria();
		dc.add(Restrictions.like("stockName", "%"+name+"%"));
		dc.add(Restrictions.eq(Stock.FIELD_DEL_FLAG, Stock.DEL_FLAG_NORMAL));
		List<Stock> rs = stockDao.find(dc);
		if(rs.size()>=1){
			return rs.get(0);
		}
		return null;
	}
	
	public Stock findBySign(String sign){
		DetachedCriteria dc = stockDao.createDetachedCriteria();
		dc.add(Restrictions.eq("stockSign", sign));
		dc.add(Restrictions.eq(Stock.FIELD_DEL_FLAG, Stock.DEL_FLAG_NORMAL));
		List<Stock> rs = stockDao.find(dc);
		if(rs.size()>=1){
			return rs.get(0);
		}
		return null;
	}
	
	public boolean findBySignAndCountryIsExt(String sign,String country){
		DetachedCriteria dc = stockDao.createDetachedCriteria();
		dc.add(Restrictions.eq("stockSign", sign));
		dc.add(Restrictions.eq("platform", country));
		dc.add(Restrictions.eq(Stock.FIELD_DEL_FLAG, Stock.DEL_FLAG_NORMAL));
		return stockDao.count(dc)>0;
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		stockDao.deleteById(id);
	}
/*	TNTonline
	3760 W CENTURY BLVD
	INGLEWOOD, CA 90303
	US
	
	TDKRFSEB
	3542A Meeker Ave
	EL Monte, CA 91731
	US*/
	
	//根据国家代码（大写：CN）     查询本地仓
	public List<Stock> findByCountryCode(String countryCode,String country,AmazonAccountConfig config){
		
		if("com2".equals(country)){
			List<Stock> list=Lists.newArrayList();
			list.add(new Stock("3542A Meeker Ave","","EL Monte","","CA","US","91731","US","","美国本地A","0","TDKRFSEB"));
			return list;
		}else if("com3".equals(country)){
			List<Stock> list=Lists.newArrayList();
			list.add(new Stock("3760 W CENTURY BLVD","","INGLEWOOD","","CA","US","90303","US","","美国本地A","0","TNTonline"));
			return list;
		}else{
			if(config!=null&&StringUtils.isNotBlank(config.getFbaAddr())){
				String[] addr=config.getFbaAddr().split(";;");
				List<Stock> list=Lists.newArrayList();
				list.add(new Stock(addr[0],"",addr[1],"",addr[2],addr[3],addr[4],addr[5],"",addr[6],"0",addr[7]));
				return list;		
			}else{
				DetachedCriteria dc = stockDao.createDetachedCriteria();
				dc.add(Restrictions.eq("type", "0"));
				dc.add(Restrictions.eq("countrycode", countryCode));
				dc.add(Restrictions.eq(Stock.FIELD_DEL_FLAG, Stock.DEL_FLAG_NORMAL));
				return stockDao.find(dc);
			}
		}
	}
	
	//查询本地仓库  国家简码
	public Map<Integer,String> findSelfStockCountryCode(){
		Map<Integer,String> map =Maps.newHashMap();
		String sql="SELECT a.`id`,a.`countrycode` FROM psi_stock AS a WHERE a.`del_flag`='0' AND a.`type`='0'";
		List<Object[]> objects =stockDao.findBySql(sql);
		if(objects.size()>0){
			for(Object[] obj:objects){
				map.put(Integer.parseInt(obj[0].toString()), obj[1].toString());
			}
		}
		return map;
	}
	
	
	public Map<Integer,Stock> findStock(){
		Map<Integer,Stock> rs = Maps.newHashMap();
		DetachedCriteria dc = stockDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", "0"));
		dc.add(Restrictions.eq(Stock.FIELD_DEL_FLAG, Stock.DEL_FLAG_NORMAL));
		List<Stock> list=stockDao.find(dc);
		if(list!=null&&list.size()>0){
			for(Stock stock:list){
				rs.put(stock.getId(), stock);
			}
		}
		return rs;
	}
	
}
