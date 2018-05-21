package com.springrain.erp.modules.ebay.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.ebay.dao.EbayOrderDao;
import com.springrain.erp.modules.ebay.dao.EbayOrderItemDao;
import com.springrain.erp.modules.ebay.dao.EbayProductPriceDao;
import com.springrain.erp.modules.ebay.dao.EbayProductProfitDao;
import com.springrain.erp.modules.ebay.entity.EbayOrder;
import com.springrain.erp.modules.ebay.entity.EbayProductPrice;
import com.springrain.erp.modules.ebay.entity.EbayProductProfit;

@Component
@Transactional(readOnly = true)
public class EbayOrderService extends BaseService {

	@Autowired
	private EbayOrderDao ebayOrderDao;
	
	@Autowired
	private EbayOrderItemDao ebayOrderItemDao;
	
	@Autowired
	private EbayProductPriceDao ebayProductPriceDao;

	@Autowired
	private EbayProductProfitDao ebayProductProfitDao;
	
	@Transactional(readOnly = false)
	public void savePrice(List<EbayProductPrice> priceList) {
		ebayProductPriceDao.save(priceList);
	}
	
	@Transactional(readOnly = false)
	public void saveProfit(List<EbayProductProfit> profitList) {
		//ebayProductProfitDao.getSession().clear();
		for (EbayProductProfit order : profitList) {
			if(order.getId()!=null&&order.getId()>0){
				ebayProductProfitDao.getSession().merge(order);
			}else{
				ebayProductProfitDao.save(order);
			}
		}
		//ebayProductProfitDao.save(profitList);
	}
	
	
	public List<EbayProductProfit> find(EbayProductProfit profitEty){
		 List<EbayProductProfit> profitList=Lists.newArrayList();
		 String typeSql = "'%Y%m%d'";
		 if("1".equals(profitEty.getType())){
			 typeSql = "'%Y%m'";
			// profitEty.setStart(DateUtils.getFirstDayOfMonth(profitEty.getStart()));
			// profitEty.setEnd(DateUtils.getLastDayOfMonth(profitEty.getEnd()));
		 }
		
		 String sql="";
		 if("1".equals(profitEty.getType())){
			 sql="SELECT t.product_name,t.`country`,DATE_FORMAT(t.`day`,"+typeSql+") dates,sum(t.`sales_volume`),sum(t.`sales`),sum(t.`sales_no_tax`),sum(t.`transport_fee`),sum(t.`buy_cost`),sum(t.`ebay_fee`),sum(t.`profits`),sum(t.price)  FROM ebay_product_profit t "+
		              " WHERE DATE_FORMAT(t.`day`,'%Y-%m')>=:p1 and DATE_FORMAT(t.`day`,'%Y-%m')<=:p2 and t.country=:p3 group by t.product_name,t.`country`,dates ";
		 }else{
			 sql="SELECT t.product_name,t.`country`,concat("+profitEty.getStart()+",'~',"+profitEty.getEnd()+") dates,sum(t.`sales_volume`),sum(t.`sales`),sum(t.`sales_no_tax`),sum(t.`transport_fee`),sum(t.`buy_cost`),sum(t.`ebay_fee`),sum(t.`profits`),sum(t.price)   FROM ebay_product_profit t "+
		              " WHERE t.`day`>=:p1 and t.`day`<=:p2 and t.country=:p3 group by t.product_name,t.`country` "; 
		 }
		// sql="SELECT t.product_name,t.`country`,DATE_FORMAT(t.`day`,"+typeSql+") dates,sum(t.`sales_volume`),sum(t.`sales`),sum(t.`sales_no_tax`),sum(t.`transport_fee`),sum(t.`buy_cost`),sum(t.`ebay_fee`),sum(t.`profits`)  FROM ebay_product_profit t "+
         //     " WHERE t.`day`>=:p1 and t.`day`<=:p2 and t.country=:p3 group by t.product_name,t.`country`,dates ";
		 List<Object[]> list=ebayProductProfitDao.findBySql(sql,new Parameter(profitEty.getStart(),profitEty.getEnd(),profitEty.getCountry()));
		 if(list!=null&&list.size()>0){
			 for (Object[] obj: list) {
				String name=obj[0].toString();
				String country=obj[1].toString();
				String date=obj[2].toString();
				if(!"1".equals(profitEty.getType())){
					date=profitEty.getStart()+"~"+profitEty.getEnd();
				}
				
		        Integer quantity=Integer.parseInt(obj[3].toString());
		        Float sales=Float.parseFloat(obj[4].toString());
				Float salesNoTax=Float.parseFloat(obj[5].toString());
				Float tranFee=Float.parseFloat(obj[6].toString());
				Float buyCosts=Float.parseFloat(obj[7].toString());
				Float ebayFee=Float.parseFloat(obj[8].toString());
				Float profit=Float.parseFloat(obj[9].toString());
				Float price=Float.parseFloat(obj[10].toString());
				
				EbayProductProfit ety=new EbayProductProfit();
				ety.setProductName(name);
				ety.setCountry(country);
				ety.setDate(date);
				ety.setSalesVolume(quantity);
				ety.setSales(sales);
				ety.setSalesNoTax(salesNoTax);
				ety.setTransportFee(tranFee);
				ety.setBuyCost(buyCosts);
				ety.setEbayFee(ebayFee);
				ety.setProfits(profit);
				ety.setPrice(price);
				
				profitList.add(ety);
			 }
		 }
		 return profitList;
	}
	
	public Map<String,Map<String,EbayProductPrice>> findEbayList(){
		Map<String,Map<String,EbayProductPrice>> map=Maps.newHashMap();
		DetachedCriteria dc = ebayProductPriceDao.createDetachedCriteria();
		Date date=new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
    	dc.add(Restrictions.ge("updateDate",date));
		dc.add(Restrictions.lt("updateDate",DateUtils.addDays(date,1)));
		List<EbayProductPrice> list=ebayProductPriceDao.find(dc);
		if(list==null||list.size()==0){
			String sqlString="select max(update_date) from ebay_product_price ";
			List<Object> rs=ebayProductPriceDao.findBySql(sqlString);
			if(rs.size()>0){
				Date maxDate=(Date)rs.get(0);
				dc = ebayProductPriceDao.createDetachedCriteria();
		    	dc.add(Restrictions.eq("updateDate",maxDate));
				list=ebayProductPriceDao.find(dc);
				if(list!=null&&list.size()>0){
					for (EbayProductPrice ebayProductPrice : list) {
						Map<String,EbayProductPrice> temp=map.get(ebayProductPrice.getProductName());
						if(temp==null){
							temp=Maps.newHashMap();
							map.put(ebayProductPrice.getProductName(), temp);
						}
						temp.put(ebayProductPrice.getCountry(), ebayProductPrice);
					}
				}
			}	
		}else{
			for (EbayProductPrice ebayProductPrice : list) {
				Map<String,EbayProductPrice> temp=map.get(ebayProductPrice.getProductName());
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(ebayProductPrice.getProductName(), temp);
				}
				temp.put(ebayProductPrice.getCountry(), ebayProductPrice);
			}
		}
		return map;
	}
	
	
	@Transactional(readOnly = false)
	public void addOrUpdate(EbayOrder order) {
		//ebayOrderDao.getSession().clear();
		//ebayOrderDao.save(order);
		if(order.getId()!=null&&order.getId()>0){
			ebayOrderDao.getSession().merge(order);
		}else{
			ebayOrderDao.save(order);
		}
	}

	public boolean isNotExist(String orderId,String country) {
		DetachedCriteria dc = ebayOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("orderId", orderId));
		dc.add(Restrictions.eq("country", country));
		return ebayOrderDao.count(dc) == 0;
	}

	public Page<EbayOrder> find(Page<EbayOrder> page, EbayOrder ebayorder) {
		DetachedCriteria dc = ebayOrderDao.createDetachedCriteria();
		String search = ebayorder.getOrderId();
		if (StringUtils.isNotEmpty(search)) {
			try {
				int id = Integer.parseInt(search);
				Integer idStr = id;
				if (idStr.toString().length() == 10) {
					dc.createAlias("this.items", "item");
					dc.add(Restrictions.like("item.sku", "%" + search + "%"));
				} else {
					dc.add(Restrictions.eq("id", id));
				}
			} catch (NumberFormatException e) {
				dc.createAlias("this.shippingAddress", "shippingAddress");
				dc.createAlias("this.items", "item");
				String rs=search;
				if(search.contains("@")&&search.startsWith("erp")){
					 String[] temp=search.split("@");
					 rs=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
				}
				dc.add(Restrictions.or(Restrictions.eq("invoiceNo",search),
						Restrictions.like("orderId", "%" + search + "%"),
						Restrictions.like("buyerUserId", "%" + search + "%"),
						Restrictions.like("item.email", "%" + search + "%"),
						Restrictions.like("item.email","%" + rs +"%"),
						Restrictions.like("shippingAddress.name", "%" + search + "%"),
						Restrictions.like("item.sku", "%" + search + "%")));
			}
		}
		if (StringUtils.isNotEmpty(ebayorder.getStatus())) {
			dc.add(Restrictions.like("status", "%" + ebayorder.getStatus()));
		}
		if (StringUtils.isNotEmpty(ebayorder.getCountry())) {
			dc.add(Restrictions.eq("country", ebayorder.getCountry()));
		}
		if (ebayorder.getCreatedTime() != null) {
			dc.add(Restrictions.ge("createdTime", ebayorder.getCreatedTime()));
		}
		if (ebayorder.getShippedTime() != null) {
			dc.add(Restrictions.le("createdTime", DateUtils.addDays(ebayorder.getShippedTime(),1)));
		}
		return ebayOrderDao.find(page, dc);
	}
	
	public Page<EbayOrder> ordersManager(Page<EbayOrder> page, EbayOrder ebayorder) {
		DetachedCriteria dc = ebayOrderDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(ebayorder.getOrderId())) {
			dc.add(Restrictions.or(Restrictions.like("buyerUserId", "%" + ebayorder.getOrderId() + "%"),
					Restrictions.like("orderId", "%" + ebayorder.getOrderId() + "%")));
		}
		if (StringUtils.isNotEmpty(ebayorder.getStatus())) {
			dc.add(Restrictions.like("status", "%" + ebayorder.getStatus()));
		}
		if (ebayorder.getCreatedTime() != null) {
			dc.add(Restrictions.ge("createdTime", ebayorder.getCreatedTime()));
		}
		if (ebayorder.getShippedTime() != null) {
			dc.add(Restrictions.le("createdTime", DateUtils.addDays(ebayorder.getShippedTime(),1)));
		}
		return ebayOrderDao.find(page, dc);
	}

	public EbayOrder get(Integer id) {
		return ebayOrderDao.get(id);
	}

	public EbayOrder getOrder(String orderId,String country) {
		EbayOrder order = ebayOrderDao.getByHql(
				"from EbayOrder as e where e.orderId = :p1 and e.country = :p2 ", new Parameter(
						orderId,country));
		return order;
	}

	@Transactional(readOnly = false)
	public void save(EbayOrder ebayOrder) {
		ebayOrderDao.save(ebayOrder);
	}
	
	
	public List<EbayOrder> inquiryOrder(EbayOrder ebayorder){
		DetachedCriteria dc = ebayOrderDao.createDetachedCriteria();
		if (ebayorder.getCreatedTime() != null) {
			dc.add(Restrictions.ge("createdTime", ebayorder.getCreatedTime()));
		}
		if (ebayorder.getShippedTime() != null) {
			dc.add(Restrictions.le("createdTime", DateUtils.addDays(ebayorder.getShippedTime(),1)));
		}
		if (StringUtils.isNotEmpty(ebayorder.getStatus())) {
			dc.add(Restrictions.like("status", "%" + ebayorder.getStatus()));
		}
		return ebayOrderDao.find(dc);
	}
	
	public List<EbayOrder> findNoInvoice(int startId,String country,int index){
		DetachedCriteria dc = ebayOrderDao.createDetachedCriteria();
		dc.add(Restrictions.le("id",startId));
		StringBuilder buf=new StringBuilder();
		for (int i = 0; i < index; i++) {
			buf.append("_");
		}
		String suff = buf.toString(); 
		dc.add(Restrictions.like("invoiceFlag",suff+"0%"));
		dc.add(Restrictions.gt("total",new BigDecimal(0)));
		dc.add(Restrictions.gt("amountPaid",new BigDecimal(0)));
		dc.addOrder(Order.asc("id"));
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.in("status",new String[]{"2","4"}));
		List<EbayOrder> orders =  ebayOrderDao.find(dc);
		for (EbayOrder ebayOrder : orders) {
			Hibernate.initialize(ebayOrder.getShippingAddress());
			Hibernate.initialize(ebayOrder.getItems());
		}
		return orders;
	}
	
	
	public List<EbayOrder> find(int startId,Integer endHours,String country,int index){
		DetachedCriteria dc = ebayOrderDao.createDetachedCriteria();
		dc.add(Restrictions.in("status",new String[]{"2","4"}));
		StringBuilder buf=new StringBuilder();
		for (int i = 0; i < index; i++) {
			buf.append("_");
		}
		String suff =buf.toString(); 
		dc.add(Restrictions.not(Restrictions.like("invoiceFlag",suff+"1%")));
		if(endHours!=null && endHours>0){
			Date date = new Date();
			date = DateUtils.addHours(date, -endHours);
			dc.add(Restrictions.lt("lastModifiedTime", date));
		}
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.gt("total",new BigDecimal(0)));
		dc.add(Restrictions.gt("amountPaid",new BigDecimal(0)));
		dc.add(Restrictions.gt("id",startId));
		dc.addOrder(Order.asc("id"));
		List<EbayOrder> orders =  ebayOrderDao.find(dc);
		for (EbayOrder ebayOrder : orders) {
			Hibernate.initialize(ebayOrder.getShippingAddress());
			Hibernate.initialize(ebayOrder.getItems());
		}
		return orders;
	}
	
	@Transactional(readOnly = false)
	public void  updateOrderInvoiceFlag(String invoiceFlag) {
		Object[] str = {invoiceFlag};
		Parameter parameter =new Parameter(str);
		ebayOrderDao.updateBySql("update ebay_order  set invoice_flag=:p1 WHERE status <> '2' and status <> '4' and country='de' ", parameter);
	}
	
	@Transactional(readOnly = false)
	public void  updateOrderInvoiceFlagById(String invoiceFlag,Integer id) {
		ebayOrderDao.updateBySql("update ebay_order set invoice_flag=:p1 WHERE id=:p2", new Parameter(invoiceFlag,id));
	}
	
	@Transactional(readOnly = false)
	public void  updateInvoiceNoById(String invoiceNo,Integer id) {
		ebayOrderDao.updateBySql("update ebay_order set invoice_no=:p1 WHERE id=:p2", new Parameter(invoiceNo,id));
	}
	
	
	public Map<String,Set<Integer>> findNoInvoiceOrder(){
		String sql="SELECT country,id FROM ebay_order WHERE last_modified_time>='2017-10-24' and status in ('2','4') AND invoice_no IS NULL";
		List<Object[]> list=ebayOrderDao.findBySql(sql);
		Map<String,Set<Integer>> map=Maps.newHashMap();
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				String country=obj[0].toString();
				Integer orderId=Integer.parseInt(obj[1].toString());
				Set<Integer> temp=map.get(country);
				if(temp==null){
					temp=Sets.newHashSet();
					map.put(country, temp);
				}
				temp.add(orderId);
			}
		}
		return map;
	}
	
	public boolean getisEbayEmail(String email){
		DetachedCriteria dc = ebayOrderItemDao.createDetachedCriteria();
		dc.add(Restrictions.eq("email",email));
		return ebayOrderItemDao.count(dc)>0;
	}
	
	public List<Object> getAllEbaySkus(){
		String sql = "SELECT DISTINCT a.`sku` FROM ebay_orderitem a WHERE a.`sku` !=''";
		List<Object> list = ebayOrderDao.findBySql(sql);
		return list;
	}
	
	public List<Object> getAllEbaySkus(String country){
		String sql = "SELECT DISTINCT a.`sku` FROM ebay_order b join ebay_orderitem a on b.id=a.order_id WHERE b.country=:p1 and a.`sku` !=''";
		List<Object> list = ebayOrderDao.findBySql(sql,new Parameter(country));
		return list;
	}
	
	public Timestamp getLastUpdateTime(String country){
		String sql = "SELECT MAX(a.`last_modified_time`) FROM ebay_order a where country = :p1";
		List<Object> list = ebayOrderDao.findBySql(sql,new Parameter(country));
		if(list!=null&&list.size()>=1){
			Timestamp time  = (Timestamp)list.get(0);
			if(time!=null){
				return time;
			}
		}
		return new Timestamp(DateUtils.addDays(new Date(),-29).getTime());
	}
	
	public List<EbayOrder> findForExp(EbayOrder ebayOrder){
		DetachedCriteria dc = ebayOrderDao.createDetachedCriteria();

		if (ebayOrder.getCreatedTime() != null) {
			dc.add(Restrictions.ge("createdTime", ebayOrder.getCreatedTime()));
		}
		if (ebayOrder.getShippedTime() != null) {
			dc.add(Restrictions.le("createdTime", DateUtils.addDays(ebayOrder.getShippedTime(),1)));
		}
		if (ebayOrder.getStatus() != null && ebayOrder.getStatus().length() > 0) {
			dc.add(Restrictions.eq("status", ebayOrder.getStatus()));
		}
		if(StringUtils.isNotBlank(ebayOrder.getCountry())){
			dc.add(Restrictions.eq("country", ebayOrder.getCountry()));
		}
		dc.add(Restrictions.gt("total",new BigDecimal(0)));
		dc.addOrder(Order.asc("id"));
		List<EbayOrder> orders =  ebayOrderDao.find(dc);
		for (EbayOrder order : orders) {
			Hibernate.initialize(order.getShippingAddress());
			Hibernate.initialize(order.getItems());
		}
		return orders;
	}
	
	public Map<String,EbayProductPrice> findPrice(){
		Map<String,EbayProductPrice>  map=Maps.newHashMap();
		String sql="SELECT country,DATE_FORMAT(r.`update_date`,'%Y%m%d') dates,r.product_name,purchase_price,tran_price from ebay_product_price r where r.`update_date`>=:p1 and r.update_date<=:p2 ";
		List<Object[]> list=ebayProductPriceDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(), -20),new Date()));
		for (Object[] obj: list) {
			String country=obj[0].toString();
			String date=obj[1].toString();
			String name=obj[2].toString();
			float purchasePrice=Float.parseFloat(obj[3]==null?"0":obj[3].toString());
			float tranFee=Float.parseFloat(obj[4]==null?"0":obj[4].toString());
			EbayProductPrice price=new EbayProductPrice();
			price.setPurchasePrice(purchasePrice);
            price.setTranFee(tranFee);
            map.put(date+"_"+name+"_"+country,price);
		}
		return map;
	}
	
	public  EbayProductProfit findProfit(Date date,String productName,String country){
		DetachedCriteria dc = ebayProductProfitDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.eq("day",date));
		dc.add(Restrictions.eq("productName",productName));
		List<EbayProductProfit> orders = ebayProductProfitDao.find(dc);
		if(orders!=null&&orders.size()>0){
			return orders.get(0);
		}
		return null;
	}
	
	
	public List<Object[]> findSalesByDate(){
		String sql="SELECT country,STR_TO_DATE(DATE_FORMAT(r.`created_time`,'%Y%m%d'),'%Y%m%d') AS dates,t.sku,SUM(t.`quantity_purchased`)*t.`transaction_price` total,SUM(t.`quantity_purchased`),t.`transaction_price` FROM ebay_order r "+
                  " JOIN ebay_orderitem t ON r.`id`=t.`order_id` "+
                  " WHERE r.`created_time`>=:p1 AND r.`created_time`<=:p2 and sku is not null GROUP BY r.`country`,t.sku,dates,t.`transaction_price` ";
		return ebayOrderDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-15),new Date()));
	}
	
	public Map<String,Float> findFbaShipmentFee(){
		String sql="SELECT d.`order_id`,SUM(r.`fba_per_order_fulfillment_fee`+r.`fba_per_unit_fulfillment_fee`+r.`fba_transportation_fee`) fee FROM amazoninfo_outbound_order r "+
			" JOIN ebay_order d ON r.`old_order_id`=d.`order_id` "+
			" WHERE d.`created_time`>=:p1 AND d.`created_time`<=:p2 and r.`order_status`='COMPLETE' AND r.`order_type`='Ebay' GROUP BY r.`id` ";
		
		String sql2="SELECT d.`order_id`,GROUP_CONCAT(CONCAT(CONCAT(t.`product_name`,CASE  WHEN t.`color`='' THEN '' ELSE CONCAT('_',t.`color`) END),';',quantity_ordered)) NAME,SUM(quantity_ordered) quantity,DATE_FORMAT(d.`created_time`,'%Y%m%d') dates "+
             " FROM amazoninfo_outbound_order r JOIN ebay_order d ON r.`old_order_id`=d.`order_id` JOIN amazoninfo_outbound_orderitem t ON r.id=t.`order_id` WHERE d.`created_time`>=:p1 AND d.`created_time`<=:p2 and r.`order_status`='COMPLETE' AND r.`order_type`='Ebay' GROUP BY d.`order_id`,dates ";
		List<Object[]> list=ebayOrderDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(), -15),new Date()));
		Map<String,Float>  map=Maps.newHashMap();
		Map<String,Float>  temp=Maps.newHashMap();
		if(list!=null&&list.size()>0){
			for (Object[] obj: list){
				if(obj[1]==null){
					continue;
				}
				temp.put(obj[0].toString(), Float.parseFloat(obj[1].toString()));
			}
		}	
		List<Object[]> list2=ebayOrderDao.findBySql(sql2,new Parameter(DateUtils.addDays(new Date(), -15),new Date()));
		if(list2!=null&&list2.size()>0){
			for (Object[] obj: list2){
				String orderId=obj[0].toString();
				String name=obj[1].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				String date=obj[3].toString();
				if(temp.get(orderId)!=null){
					float fee=temp.get(orderId)/quantity;
					String[] arr=name.split(",");
					for (String nameAndQuantity: arr) {
						String[] temp1=nameAndQuantity.split(";");
						Float tranFee=map.get(temp1[0]+"_"+date);
						map.put(temp1[0]+"_"+date, Integer.parseInt(temp1[1])*fee+(tranFee==null?0:tranFee));
					}
				}
			}
		}
		
		
		/*if(list!=null&&list.size()>0){
			for (Object[] obj: list){
				String name=obj[0].toString();
				float fee=Float.parseFloat(obj[1].toString());
				String date=obj[2].toString();
				String[] arr=name.split(",");
				for (String nameAndQuantity: arr) {
					String[] temp=nameAndQuantity.split(";");
					Float tranFee=map.get(temp[0]+"_"+date);
					map.put(temp[0]+"_"+date, Integer.parseInt(temp[1])*fee+(tranFee==null?0:tranFee));
				}
			}
		}*/
		return map;
	}
	
	public Map<String,Set<String>> findExceptionEbayOrder(){
		Map<String,Set<String>> map=Maps.newHashMap();
		String sql="SELECT country,order_id FROM ebay_order e WHERE e.`created_time`>='2017-09-01' and e.`checkout_status`='Incomplete' AND e.`paid_time` IS NOT NULL AND e.`shipped_time` IS  NULL";
		List<Object[]> list=ebayOrderDao.findBySql(sql);
		for (Object[] obj: list) {
			Set<String> sets=map.get(obj[0].toString());
			if(sets==null){
				sets=Sets.newHashSet();
				map.put(obj[0].toString(), sets);
			}
			sets.add(obj[1].toString());
		}
		return map;
	}
}
