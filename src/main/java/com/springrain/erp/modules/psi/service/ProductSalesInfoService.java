/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.ProductSalesInfoDao;
import com.springrain.erp.modules.psi.entity.ProductSalesInfo;

/**
 * 采购订单Service
 * @author Michael
 * @version 2014-10-29
 */
@Component
@Transactional(readOnly = true)
public class ProductSalesInfoService extends BaseService {

	@Autowired
	private ProductSalesInfoDao productSalesInfoDao;
	
	public Map<String,ProductSalesInfo> findAll(){
		List<ProductSalesInfo> list =  productSalesInfoDao.findAll();
		Map<String,ProductSalesInfo> rs = Maps.newLinkedHashMap();
		for (ProductSalesInfo productSalesInfo : list) {
			String country = productSalesInfo.getCountry();
			String productName = productSalesInfo.getProductName();
			rs.put(productName+"_"+country, productSalesInfo);
			if(!"eu".equals(country) && !"eunouk".equals(country)){
				ProductSalesInfo total = rs.get(productName);
				if(total==null){
					total = new ProductSalesInfo();
					total.setDay31Sales(0);
					rs.put(productName, total);
				}
				total.setDay31Sales(total.getDay31Sales()+productSalesInfo.getDay31Sales());
			}
		}
		return rs;
	}
	
	public Map<String,ProductSalesInfo> find(String productName){
		DetachedCriteria dc = productSalesInfoDao.createDetachedCriteria();
		dc.add(Restrictions.eq("productName",productName));
		List<ProductSalesInfo> list =  productSalesInfoDao.find(dc);
		Map<String,ProductSalesInfo> rs = Maps.newLinkedHashMap();
		for (ProductSalesInfo productSalesInfo : list) {
			String country = productSalesInfo.getCountry();
			rs.put(productName+"_"+country, productSalesInfo);
			if(!"eu".equals(country) && !"eunouk".equals(country)){
				ProductSalesInfo total = rs.get(productName);
				if(total==null){
					total = new ProductSalesInfo();
					total.setDay31Sales(0);
					rs.put(productName, total);
				}
				total.setDay31Sales(total.getDay31Sales()+productSalesInfo.getDay31Sales());
			}
		}
		return rs;
	}
	
	public Map<String,ProductSalesInfo> find(Set<String> nameSet){
		DetachedCriteria dc = productSalesInfoDao.createDetachedCriteria();
		dc.add(Restrictions.in("productName",nameSet));
		List<ProductSalesInfo> list =  productSalesInfoDao.find(dc);
		Map<String,ProductSalesInfo> rs = Maps.newLinkedHashMap();
		for (ProductSalesInfo productSalesInfo : list) {
			String country = productSalesInfo.getCountry();
			String productName=productSalesInfo.getProductName();
			rs.put(productSalesInfo.getProductName()+"_"+country, productSalesInfo);
			if(!"eu".equals(country) && !"eunouk".equals(country)){
				ProductSalesInfo total = rs.get(productName);
				if(total==null){
					total = new ProductSalesInfo();
					total.setDay31Sales(0);
					rs.put(productName, total);
				}
				total.setDay31Sales(total.getDay31Sales()+productSalesInfo.getDay31Sales());
			}
		}
		return rs;
	}
	
	
	public List<ProductSalesInfo> findByCountry(String country){
		DetachedCriteria dc = productSalesInfoDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country",country));
		List<ProductSalesInfo> list =  productSalesInfoDao.find(dc);
		return list;
	}
		
	@Transactional(readOnly = false)
	public void save(List<ProductSalesInfo> productSalesInfos) {
		String sql = "truncate table psi_product_variance";
		productSalesInfoDao.updateBySql(sql, null);
		productSalesInfoDao.save(productSalesInfos);
	}
}
