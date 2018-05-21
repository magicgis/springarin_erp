package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
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
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.ProductPriceApprovalDao;
import com.springrain.erp.modules.amazoninfo.entity.ProductPriceApproval;

/**
 * 产品特殊定价管理Service
 */
@Component
@Transactional(readOnly = true)
public class ProductPriceApprovalService extends BaseService {

	@Autowired
	private ProductPriceApprovalDao productPriceApprovalDao;
	
	public ProductPriceApproval get(Integer id) {
		return productPriceApprovalDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(ProductPriceApproval productPriceApproval) {
		productPriceApprovalDao.save(productPriceApproval);
	}
	
	@Transactional(readOnly = false)
	public void save(List<ProductPriceApproval> productPriceApprovals) {
		for (ProductPriceApproval productPriceApproval : productPriceApprovals) {
			productPriceApprovalDao.save(productPriceApproval);
		}
	}

	public Page<ProductPriceApproval> find(Page<ProductPriceApproval> page,
			ProductPriceApproval productPriceApproval) {
		DetachedCriteria dc = productPriceApprovalDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(productPriceApproval.getState())){
			dc.add(Restrictions.eq("state", productPriceApproval.getState()));
		}
		if (StringUtils.isNotEmpty(productPriceApproval.getCountry())){
			dc.add(Restrictions.eq("country", productPriceApproval.getCountry()));
		}
		if (StringUtils.isNotEmpty(productPriceApproval.getProductName())){
			dc.add(Restrictions.like("productName", "%" + productPriceApproval.getProductName() + "%"));
		}
		if (StringUtils.isEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("createDate"));
		}
		return productPriceApprovalDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void activePrice(Date today) {
		List<ProductPriceApproval> list = Lists.newArrayList();
		DetachedCriteria dc = productPriceApprovalDao.createDetachedCriteria();
		dc.add(Restrictions.eq("state", "1"));
		Page<ProductPriceApproval> page = productPriceApprovalDao.find(new Page<ProductPriceApproval>(), dc);
		list = page.getList();
		if (today == null) {
			today = new Date();
		}
		for (ProductPriceApproval price : list) {
			if (price.getSaleStartDate() == null || price.getSaleEndDate() == null) {
				continue;
			}
			if (price.getSaleStartDate().before(today)
					&& DateUtils.addDays(price.getSaleEndDate(), 1).after(today) 
					&& "0".equals(price.getIsActive())) {
				price.setIsActive("1");
				productPriceApprovalDao.save(price);
			} else if((price.getSaleStartDate().after(today) || DateUtils.addDays(price.getSaleEndDate(), 1).before(today))
					&& "1".equals(price.getIsActive())){
				price.setIsActive("0");
				productPriceApprovalDao.save(price);
			}
		}
	}

	//查询当前生效的审批价格map[sku_country,价格]
	public Map<String, Float> findSkuPrice() {
		Map<String, Float> rs = Maps.newHashMap();
		DetachedCriteria dc = productPriceApprovalDao.createDetachedCriteria();
		dc.add(Restrictions.eq("state", "1"));	//审批通过的
		//当前时间在生效期范围内的
		Date date = new Date();
		dc.add(Restrictions.le("saleStartDate", date));
		dc.add(Restrictions.ge("saleEndDate", DateUtils.addDays(date, -1)));
		dc.addOrder(Order.asc("createDate"));
		List<ProductPriceApproval> list = productPriceApprovalDao.find(dc);
		for (ProductPriceApproval productPriceApproval : list) {
			rs.put(productPriceApproval.getSku() + "_" + productPriceApproval.getCountry(), productPriceApproval.getPrice());
		}
		return rs;
	}

	//根据sku和国家获取审批价格,没有则返回null
	public Float findPriceBySkuAndCountry(String sku, String country, String type) {
		DetachedCriteria dc = productPriceApprovalDao.createDetachedCriteria();
		dc.add(Restrictions.eq("state", "1"));	//审批通过的
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.eq("sku", sku));
		dc.add(Restrictions.eq("type", type));
		//当前时间在生效期范围内的
		Date date = new Date();
		dc.add(Restrictions.le("saleStartDate", date));
		dc.add(Restrictions.ge("saleEndDate", DateUtils.addDays(date, -1)));
		dc.addOrder(Order.desc("createDate"));
		List<ProductPriceApproval> list = productPriceApprovalDao.find(dc);
		if (list != null && list.size() > 0) {
			return list.get(0).getPrice();
		}
		return null;
	}
	
	//查询当前需要监控的信息
	public List<ProductPriceApproval> findForMonitor() {
		DetachedCriteria dc = productPriceApprovalDao.createDetachedCriteria();
		dc.add(Restrictions.eq("isMonitor", "1"));
		return productPriceApprovalDao.find(dc);
	}
	
	/**
	 * //查询指定时间后的销量
	 * @param sku
	 * @param startDate yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public Integer findSalesVolume(String sku, String startDate, String country) {
		String sql="SELECT SUM(i.`quantity_ordered`) FROM `amazoninfo_order` t ,`amazoninfo_orderitem` i "+
				" WHERE t.`id`=i.`order_id` AND DATE_FORMAT(t.`purchase_date`,'%Y-%m-%d HH:mm:ss')>:p1  "+
				" AND t.`order_status` !='Canceled' AND t.`sales_channel` LIKE '%"+country+"' AND i.`sellersku`=:p2";
		try {
			int num = Integer.parseInt(productPriceApprovalDao.findBySql(sql, new Parameter(startDate, sku)).get(0).toString());
			return num;
		} catch (Exception e) {
			return 0;
		}
	}
}
