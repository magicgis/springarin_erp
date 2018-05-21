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
import com.springrain.erp.modules.psi.dao.StockLocationDao;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.StockArea;
import com.springrain.erp.modules.psi.entity.StockLocation;

/**
 * 仓库库位Service
 */
@Component
@Transactional(readOnly = true)
public class StockLocationService extends BaseService {
    @Autowired
    private StockAreaDao stockAreaDao;
    @Autowired
    private StockDao stockDao;
	@Autowired
	private StockLocationDao stockLocationDao;
	
	public StockLocation get(Integer id) {
		return stockLocationDao.get(id);
	}
	
	public Page<StockLocation> find(Page<StockLocation> page, StockLocation stockLocation) {
		DetachedCriteria dc = stockLocationDao.createDetachedCriteria();
		if(stockLocation!=null){
    		if (StringUtils.isNotEmpty(stockLocation.getName())){
    		    dc.add(Restrictions.like("name", "%"+stockLocation.getName()+"%"));
    		}
    		if(stockLocation.getStockArea()!=null){
    		    if (StringUtils.isNotEmpty(stockLocation.getStockArea().getName())){
                    DetachedCriteria dc1 = stockAreaDao.createDetachedCriteria();
                    dc1.add(Restrictions.like("name", "%"+stockLocation.getStockArea().getName()+"%"));
                    dc1.add(Restrictions.eq("delFlag", "0"));
                    List<StockArea> find = stockAreaDao.find(dc1);
                    List<Integer> list = new ArrayList<Integer>();
                    for(int i=0;i<find.size();i++){
                        list.add(find.get(i).getId());
                    }
                    if(list.size()>0){
                        dc.add(Restrictions.in("stockArea.id", list));
                    }else{
                        dc.add(Restrictions.eq("stockArea.id",null));
                    }
                }
    		    if (StringUtils.isNotEmpty(stockLocation.getStockArea().getStock().getName())){
                    dc.add(Restrictions.like("name", "%"+stockLocation.getName()+"%"));
                    DetachedCriteria dc1 = stockDao.createDetachedCriteria();
                    DetachedCriteria dc2 = stockAreaDao.createDetachedCriteria();
                    dc1.add(Restrictions.like("stockName", "%"+stockLocation.getStockArea().getStock().getName()+"%"));
                    dc1.add(Restrictions.eq(Stock.FIELD_DEL_FLAG, Stock.DEL_FLAG_NORMAL));
                    List<Stock> rs = stockDao.find(dc1);
                    List<Integer> list = new ArrayList<Integer>();
                    for(int i=0;i<rs.size();i++){
                        list.add(rs.get(i).getId());
                    }
                    if(list.size()>0){
                        dc2.add(Restrictions.in("stock.id", list));
                    }else{
                        dc2.add(Restrictions.eq("stock.id",null));
                    }
                    dc2.add(Restrictions.eq("delFlag", "0"));
                    List<StockArea> find = stockAreaDao.find(dc2);
                    List<Integer> list2 = new ArrayList<Integer>();
                    for(int i=0;i<find.size();i++){
                        list2.add(find.get(i).getId());
                    }
                    if(list2.size()>0){
                        dc.add(Restrictions.in("stockArea.id", list2));
                    }else{
                        dc.add(Restrictions.eq("stockArea.id",null));
                    }
                }
    		    
    		}
    		
    		
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.asc("id"));
		return stockLocationDao.find(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public void save(StockLocation stockLocation) {
	    stockLocationDao.clear();
		stockLocationDao.save(stockLocation);
	}
	
	

    public List<StockLocation> get(String areaId) {
        // TODO Auto-generated method stub
        List<StockLocation> find = stockLocationDao.find("from StockLocation where stockArea.id="+areaId+"and delFlag='0'");
        return find;
    }

    public StockLocation findById(int parseInt) {
        // TODO Auto-generated method stub
        List<Object> find = stockLocationDao.find("from StockLocation where delFlag='0' and id="+parseInt);
        return (StockLocation) find.get(0);
    }

    public Integer getCount() {
        // TODO Auto-generated method stub
        return stockLocationDao.find("from StockLocation").size();
    }

	
}
