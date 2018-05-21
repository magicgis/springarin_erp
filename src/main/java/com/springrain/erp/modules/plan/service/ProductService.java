/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.plan.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.plan.entity.Product;
import com.springrain.erp.modules.plan.entity.ProductFlow;
import com.springrain.erp.modules.plan.dao.ProductDao;
import com.springrain.erp.modules.plan.dao.ProductFlowDao;

/**
 * 产品管理Service
 * @author tim
 * @version 2014-04-02
 */
@Component
@Transactional(readOnly = true)
public class ProductService extends BaseService {

	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ProductFlowDao productFlowDao;
	
	public Product get(String id) {
		return productDao.get(id);
	}
	
	public Page<Product> find(Page<Product> page, Product product) {
		DetachedCriteria dc = productDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(product.getName())){
			dc.add(Restrictions.like("name", "%"+product.getName()+"%"));
		}
		if("7".equals(product.getFinish())){
			dc.add(Restrictions.eq("finish", "7"));
		}else{
			product.setFinish("0");
			dc.add(Restrictions.ne("finish", "7"));
		}
		dc.add(Restrictions.eq(Product.FIELD_DEL_FLAG, Product.DEL_FLAG_NORMAL));
		//dc.addOrder(Order.desc("id"));
		return productDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(Product product) {
		List<ProductFlow> list =  product.getListFlow();
		int index = list.size();
		if(index>0)
			product.setStartDate(list.get(0).getStartDate());
		productDao.save(product);
		for(ProductFlow pf : list){
			pf.setProduct(product);
			productFlowDao.save(pf);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		productDao.deleteById(id);
	}
	
}
