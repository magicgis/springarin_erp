/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.StockAreaDao;
import com.springrain.erp.modules.psi.dao.StockDao;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.StockArea;

/**
 * 仓库区域Service
 */
@Component
@Transactional(readOnly = true)
public class StockAreaService extends BaseService {

	@Autowired
	private StockAreaDao stockAreaDao;
	@Autowired
	private StockDao stockDao;
	
	public StockArea get(Integer id) {
		return stockAreaDao.get(id);
	}
	
	public Page<StockArea> find(Page<StockArea> page, StockArea stockArea) {
		DetachedCriteria dc = stockAreaDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(stockArea.getName())){
		    DetachedCriteria dc1 = stockDao.createDetachedCriteria();
	        dc1.add(Restrictions.like("stockName", "%"+stockArea.getName()+"%"));
	        dc1.add(Restrictions.eq(Stock.FIELD_DEL_FLAG, Stock.DEL_FLAG_NORMAL));
	        List<Stock> rs = stockDao.find(dc1);
	        List<Integer> list = new ArrayList<Integer>();
	        for(int i=0;i<rs.size();i++){
	            list.add(rs.get(i).getId());
	        }
	        if(list.size()>0){
	            dc.add(Restrictions.in("stock.id", list));
	        }else{
	            dc.add(Restrictions.eq("stock.id",null));
	        }
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.asc("id"));
		return stockAreaDao.find(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public void save(StockArea stockArea) {
	    stockAreaDao.clear();
		stockAreaDao.save(stockArea);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		stockAreaDao.deleteById(id);
	}

    public List<StockArea> get(String officeId) {
        // TODO Auto-generated method stub
        List<StockArea> find = stockDao.find("from StockArea where stock.id="+officeId+"and delFlag='0'");
        return find;
    }

    public List<StockArea> findStockAreaByStockId(String stockId) {
        // TODO Auto-generated method stub
        List<StockArea> find = stockAreaDao.find("from StockArea where delFlag='0' and stock.id="+stockId);
        return find;
    }

    public StockArea findAreaById(String id) {
        // TODO Auto-generated method stub
        return (StockArea) stockAreaDao.find("from StockArea where delFlag='0' and id="+id).get(0);
    }

    public Integer findAll() {
        // TODO Auto-generated method stub
        return stockAreaDao.find("from StockArea").size();
    }
	
	
}
