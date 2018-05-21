package com.springrain.erp.modules.amazoninfo.service.order;


import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
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
import com.springrain.erp.modules.amazoninfo.dao.order.VendorOrderDao;
import com.springrain.erp.modules.amazoninfo.dao.order.VendorReturnsDao;
import com.springrain.erp.modules.amazoninfo.dao.order.VendorShipmentDao;
import com.springrain.erp.modules.amazoninfo.entity.order.VendorOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.VendorReturns;
import com.springrain.erp.modules.amazoninfo.entity.order.VendorShipment;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Component
@Transactional(readOnly = true)
public class VendorShipmentService extends BaseService {

	@Autowired
	private VendorShipmentDao shipmentDao;
	
	@Autowired
	private VendorOrderDao vendorOrderDao;
	
	@Autowired
	private VendorReturnsDao vendorReturnsDao;
	
	
	public Page<VendorOrder> findVendorOrders(Page<VendorOrder> page,VendorOrder vendorOrder) {
		DetachedCriteria dc = vendorOrderDao.createDetachedCriteria();	
		dc.add(Restrictions.eq("status","Unconfirmed"));
		dc.add(Restrictions.eq("country",vendorOrder.getCountry()));
		if(StringUtils.isNotBlank(vendorOrder.getStatus())){
			dc.createAlias("items","item");
			dc.add(Restrictions.or(Restrictions.like("item.sku","%"+vendorOrder.getStatus()+"%"),Restrictions.like("item.asin","%"+vendorOrder.getStatus()+"%")));
		}
		if(StringUtils.isNotBlank(vendorOrder.getOrderId())){
			dc.add(Restrictions.eq("orderId",vendorOrder.getOrderId()));
		}
		return vendorOrderDao.find(page, dc);
	}
	
	public VendorOrder findVendorOrder(String orderId) {
		DetachedCriteria dc = vendorOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("orderId",orderId));
		List<VendorOrder> rs = vendorOrderDao.find(dc);
		if(rs!=null&&rs.size()>0){
			VendorOrder order=rs.get(0);
			Hibernate.initialize(order.getItems());
			return order;
		}
		return null;
	}
	
	public Map<String,VendorOrder> findAllVendorOrder(String country) {
		Map<String,VendorOrder> map=Maps.newHashMap();
		DetachedCriteria dc = vendorOrderDao.createDetachedCriteria();
		dc.add(Restrictions.in("status",Sets.newHashSet("Unconfirmed","Confirmed")));
		dc.add(Restrictions.eq("country",country));
		List<VendorOrder> rs = vendorOrderDao.find(dc);
		if(rs!=null&&rs.size()>0){
			for (VendorOrder order : rs) {
				Hibernate.initialize(order.getItems());
				map.put(order.getOrderId(), order);
			}
		}
		return map;
	}
	
	public VendorShipment getShipment(Integer id) {
		return shipmentDao.get(id);
	}

	public VendorOrder getOrder(Integer id) {
		return vendorOrderDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(VendorShipment shipment) {
		shipmentDao.save(shipment);
	}
	
	@Transactional(readOnly = false)
	public void saveOrder(VendorOrder order) {
		vendorOrderDao.save(order);
	}
	
	@Transactional(readOnly = false)
	public void save(List<VendorShipment> shipments) {
		shipmentDao.save(shipments);
	}
	
	
	@Transactional(readOnly = false)
	public void savevendorReturns(List<VendorReturns> vendorReturns) {
		vendorReturnsDao.save(vendorReturns);
	}
	
	public boolean isExistRequestId(String requestId){
		String sql="SELECT 1 FROM amazoninfo_vendor_returns s WHERE s.`request_id`=:p1 ";
		List<Object> list=vendorReturnsDao.findBySql(sql, new Parameter(requestId));
		if(list!=null&&list.size()>0){
			return true;
		}
		return false;
	}
	
	public  boolean findAsn(VendorShipment shipment){
		String sql="select count(1) from amazoninfo_vendor_shipment where asn=:p1";
		List<Object> list=shipmentDao.findBySql(sql,new Parameter(shipment.getAsn()));
		if(list!=null&&list.size()>0){
			return ((BigInteger)list.get(0)).intValue()>0;
		}
		return false;
	}
	
	
	public Page<VendorReturns> findVendorReturns(Page<VendorReturns> page,VendorReturns vendorReturns) {
		DetachedCriteria dc = vendorReturnsDao.createDetachedCriteria();	
		if(vendorReturns.getCreateTime()!=null){
			dc.add(Restrictions.ge("queryTime",vendorReturns.getCreateTime()));
		}
		if(vendorReturns.getQueryTime()!= null) {
			dc.add(Restrictions.le("queryTime",vendorReturns.getQueryTime()));
		}
		dc.add(Restrictions.eq("returnReason","Overstock"));
		dc.addOrder(Order.desc("queryTime"));
		return vendorReturnsDao.find(page, dc);
	}
	
	public Map<String,Map<String,Integer>> countReturns(Date createTime,Date queryTime){
		Map<String,Map<String,Integer>> map=Maps.newHashMap();
		String sql="SELECT DATE_FORMAT(s.`query_time`,'%Y%m') qdate,ifnull(t.`product_name`,'未匹配') name,SUM(t.`approved_quantity`) FROM amazoninfo_vendor_returns s "+ 
           " JOIN amazoninfo_vendor_returns_item t ON s.id=t.`return_id` "+ 
           "  where s.`query_time`>=:p1 and s.`query_time`<=:p2 and s.return_reason='Overstock'  "+
           " GROUP BY qdate,name ";
		List<Object[]> list=vendorReturnsDao.findBySql(sql,new Parameter(createTime,queryTime));
		for (Object[] obj: list) {
			Map<String,Integer> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(), temp);
			}
			temp.put(obj[1].toString(),Integer.parseInt(obj[2].toString()));
		}
	    return map;	
	}
	
	
	public Page<VendorShipment> findVendorShipment(Page<VendorShipment> page,VendorShipment vendorShipment) {
		DetachedCriteria dc = shipmentDao.createDetachedCriteria();	
		if(vendorShipment.getShipDate()!=null){
			dc.add(Restrictions.ge("shipDate",vendorShipment.getShipDate()));
		}
		if(vendorShipment.getDeliveryDate()!= null) {
			dc.add(Restrictions.le("shipDate",vendorShipment.getDeliveryDate()));
		}
		if(StringUtils.isNotBlank(vendorShipment.getCountry())){
			dc.add(Restrictions.eq("country", vendorShipment.getCountry()));
		}
		if(StringUtils.isNotBlank(vendorShipment.getAsn())){
			dc.createAlias("this.orders", "order");
			dc.add(Restrictions.or(Restrictions.like("order.orderId", "%" +vendorShipment.getAsn()+ "%"),Restrictions.like("asn", "%" +vendorShipment.getAsn()+ "%")));
		}
		dc.addOrder(Order.desc("shipDate"));
		return shipmentDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public boolean updateVendor(String ids) {
		String[] idArr=ids.split(",");
		Date date=new Date();
		String sql="update amazoninfo_vendor_shipment set shipped_date=:p1,status='Shipped',delivery_user=:p2 where id=:p3 ";
		for (String id : idArr) {
			shipmentDao.updateBySql(sql, new Parameter(date,UserUtils.getUser().getId(),Integer.parseInt(id)));
		}
		return true;
	}
	
	
	public Map<String,Integer> getTotalByProductName(VendorShipment vendorShipment){
		String sql="SELECT t.product_name,SUM(t.`accepted_quantity`) quantity_total FROM amazoninfo_vendor_shipment p JOIN amazoninfo_vendor_order o ON o.shipment_id=p.id "+
	               " join amazoninfo_vendor_orderitem t on t.order_id=o.id "+
	             //  " WHERE DATE_FORMAT(p.`shipped_date`,'%Y-%m-%d %H:%i:%s')>=:p1 and DATE_FORMAT(p.`shipped_date`,'%Y-%m-%d %H:%i:%s')<:p2 and lower(t.product_name) not like '%unitek%'  and lower(t.product_name) not like '%orico%'  and o.country=:p3 and lower(t.`sku`) not like '%-old%' and lower(t.`sku`) not like '%_old%' and lower(t.`sku`) not like '%old_%' and lower(t.`sku`) not like '%old-%' "+
	             " WHERE DATE_FORMAT(p.`shipped_date`,'%Y-%m-%d %H:%i:%s')>=:p1 and DATE_FORMAT(p.`shipped_date`,'%Y-%m-%d %H:%i:%s')<:p2 and lower(t.product_name) not like '%unitek%'  and lower(t.product_name) not like '%orico%'  and o.country=:p3  "+
	               " group by t.product_name HAVING quantity_total>0 ";
		Map<String,Integer> map=new HashMap<String,Integer>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Object[]> list=this.shipmentDao.findBySql(sql,new Parameter(sdf.format(vendorShipment.getShipDate()),sdf.format(vendorShipment.getDeliveryDate()),vendorShipment.getCountry()));
		for (Object[] obj : list) {
			String productName=(obj[0]==null?"":obj[0].toString());
			map.put(productName, Integer.parseInt(obj[1].toString()));
		}
		return map;
	}
	
	public Map<String,Integer> getTotal(VendorShipment vendorShipment){
		String sql="SELECT t.product_name,SUM(t.`accepted_quantity`) quantity_total FROM amazoninfo_vendor_shipment p JOIN amazoninfo_vendor_order o ON o.shipment_id=p.id "+
	               " join amazoninfo_vendor_orderitem t on t.order_id=o.id "+
	               " WHERE DATE_FORMAT(p.`shipped_date`,'%Y-%m-%d')>=:p1 and DATE_FORMAT(p.`shipped_date`,'%Y-%m-%d')<=:p2 and lower(t.product_name) not like '%unitek%'  and lower(t.product_name) not like '%orico%'  and o.country=:p3 and lower(t.`sku`) not like '%-old%' and lower(t.`sku`) not like '%_old%' and lower(t.`sku`) not like '%old_%' and lower(t.`sku`) not like '%old-%' ";
		        if(StringUtils.isNotBlank(vendorShipment.getAsn())){
     	             sql+=" and (t.product_name like '%"+vendorShipment.getAsn()+"%') ";
                }
	             sql+=  " group by t.product_name HAVING quantity_total>0 order by quantity_total desc ";
		Map<String,Integer> map=Maps.newLinkedHashMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> list=this.shipmentDao.findBySql(sql,new Parameter(sdf.format(vendorShipment.getShipDate()),sdf.format(vendorShipment.getDeliveryDate()),vendorShipment.getCountry()));
		for (Object[] obj : list) {
			String productName=(obj[0]==null?"":obj[0].toString());
			map.put(productName, Integer.parseInt(obj[1].toString()));
		}
		return map;
	}
	
	public Map<String,List<Object[]>> getTotalDetail(VendorShipment vendorShipment){
		String sql="SELECT t.product_name,p.asn,o.order_id,t.`accepted_quantity`,p.`shipped_date`,p.id shipmentId,o.id vendorId,t.received_quantity,o.status FROM amazoninfo_vendor_shipment p JOIN amazoninfo_vendor_order o ON o.shipment_id=p.id "+
	               " join amazoninfo_vendor_orderitem t on t.order_id=o.id "+
	               " WHERE DATE_FORMAT(p.`shipped_date`,'%Y-%m-%d')>=:p1 and DATE_FORMAT(p.`shipped_date`,'%Y-%m-%d')<=:p2 and lower(t.product_name) not like '%unitek%'  and lower(t.product_name) not like '%orico%'  and o.country=:p3 and lower(t.`sku`) not like '%-old%' and lower(t.`sku`) not like '%_old%' and lower(t.`sku`) not like '%old_%' and lower(t.`sku`) not like '%old-%' ";
		 if(StringUtils.isNotBlank(vendorShipment.getAsn())){
	             sql+=" and (t.product_name like '%"+vendorShipment.getAsn()+"%') ";
         }   
		Map<String,List<Object[]>> map=Maps.newHashMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> list=this.shipmentDao.findBySql(sql,new Parameter(sdf.format(vendorShipment.getShipDate()),sdf.format(vendorShipment.getDeliveryDate()),vendorShipment.getCountry()));
		for (Object[] obj : list) {
			String productName=(obj[0]==null?"":obj[0].toString());
			List<Object[]> temp=map.get(productName);
			if(temp==null){
				temp=Lists.newArrayList();
				map.put(productName, temp);
			}
			temp.add(obj);
		}
		return map;
	}

	public List<VendorShipment> findAllByUpdate(List<String> ids,String country) {
		DetachedCriteria dc = shipmentDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.in("asn", ids));
		List<VendorShipment> rs = shipmentDao.find(dc);
		for (VendorShipment vendorShipment : rs) {
			String billS = vendorShipment.getBillStatu();
			if(billS==null || "1".equals(billS)){
				for (VendorOrder order : vendorShipment.getOrders()) {
					Hibernate.initialize(order.getItems());
				}
			}
		}
		return rs;
	}
	
	public List<String> getIdsNotExist(List<String> ids,String country) {
		String sql = "SELECT a.`asn` FROM amazoninfo_vendor_shipment a WHERE a.`asn` IN :p1 AND a.`country` = :p2";
		List<String> list = shipmentDao.findBySql(sql, new Parameter(ids,country));
		List<String> rs = Lists.newArrayList(ids);
		rs.removeAll(list);
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void updateTrackFee(VendorShipment vendorShipment){
		String sql="update amazoninfo_vendor_shipment set fee=:p1 where id=:p2";
		shipmentDao.updateBySql(sql, new Parameter(vendorShipment.getFee(),vendorShipment.getId()));
	}
	
	@Transactional(readOnly = false)
	public void updateCheckFlag(VendorShipment vendorShipment){
		String sql="update amazoninfo_vendor_shipment set check_user=:p1,check_statu='0' where id=:p2";
		shipmentDao.updateBySql(sql, new Parameter(UserUtils.getUser().getId(),vendorShipment.getId()));
	}
	
	@Transactional(readOnly = false)
	public void updateExceptionCheckFlag(VendorShipment vendorShipment){
		String sql="update amazoninfo_vendor_shipment set check_statu='1' where id=:p1";
		shipmentDao.updateBySql(sql, new Parameter(vendorShipment.getId()));
	}
    
	@Transactional(readOnly = false)
	public void updateBillStatu(){
		String sql="SELECT s.id,SUM(t.`accepted_quantity`) accepted_quantity,SUM(t.`submitted_quantity`) submitted_quantity FROM amazoninfo_vendor_shipment s  "+
	    " JOIN amazoninfo_vendor_order o ON s.id=o.`shipment_id` JOIN amazoninfo_vendor_orderitem t ON o.id=t.order_id   "+
	    " WHERE NOT EXISTS (SELECT 1 FROM amazoninfo_vendor_order r WHERE r.`status`='Confirmed' AND s.id=r.`shipment_id`) "+
	    " AND (bill_statu IS NULL OR bill_statu='1') AND t.`accepted_quantity`!=0 "+
	    " GROUP BY s.id HAVING  accepted_quantity=submitted_quantity  "+
        " UNION SELECT id,1,1  FROM amazoninfo_vendor_shipment WHERE check_user IS NOT NULL AND (bill_statu IS NULL OR bill_statu='1') ";
		List<Object[]> list=shipmentDao.findBySql(sql);
		Set<Integer> set=Sets.newHashSet();
		for (Object[] obj : list) {
			set.add(Integer.parseInt(obj[0].toString()));
		}
		if(set!=null&&set.size()>0){
			String updateSql="update amazoninfo_vendor_shipment set bill_statu='2' where id in :p1 ";
			shipmentDao.updateBySql(updateSql, new Parameter(set));
		}
		
		Set<Integer> exceptionSet=compareShipment();
		if(exceptionSet!=null&&exceptionSet.size()>0){
			String updateSql="update amazoninfo_vendor_shipment set check_statu='1' where id in :p1 ";
			shipmentDao.updateBySql(updateSql, new Parameter(exceptionSet));
		}
	}
	
	public List<Object[]> findExceptionAsn(VendorShipment shipment){
		String sql="SELECT s.`asn`,t.`product_name`,SUM(t.`accepted_quantity`-t.`submitted_quantity`) quantity "+
				 " FROM amazoninfo_vendor_shipment s JOIN amazoninfo_vendor_order d ON s.id=d.`shipment_id` "+
				" JOIN amazoninfo_vendor_orderitem t ON d.id=t.`order_id` "+
				" WHERE s.`check_statu`='1' AND t.`accepted_quantity`!=0 GROUP BY s.asn,t.`product_name` HAVING quantity<>0 ";
		return shipmentDao.findBySql(sql);
	}
	
	public Set<Integer> compareShipment(){
		String sql="SELECT s.id,SUM(t.`accepted_quantity`) accepted_quantity,SUM(t.`submitted_quantity`) submitted_quantity FROM amazoninfo_vendor_shipment s "+
				"  JOIN amazoninfo_vendor_order o ON s.id=o.`shipment_id` JOIN amazoninfo_vendor_orderitem t ON o.id=t.order_id  "+
				"  WHERE NOT EXISTS (SELECT 1 FROM amazoninfo_vendor_order r WHERE r.`status`='Confirmed' AND s.id=r.`shipment_id`) AND s.`check_user` IS NULL  AND t.`accepted_quantity`!=0 GROUP BY s.id HAVING  accepted_quantity!=submitted_quantity ";
		
		List<Object[]> list=shipmentDao.findBySql(sql);
		Set<Integer> set=Sets.newHashSet();
		for (Object[] obj : list) {
			set.add(Integer.parseInt(obj[0].toString()));
		}
		return set;
	}
	
	public List<VendorOrder> findForExp(VendorShipment shipment){
		DetachedCriteria dc = vendorOrderDao.createDetachedCriteria();

		if (shipment.getShipDate() != null) {
			dc.add(Restrictions.ge("orderedDate", shipment.getShipDate()));
		}
		if (shipment.getDeliveryDate() != null) {
			dc.add(Restrictions.le("orderedDate", DateUtils.addDays(shipment.getDeliveryDate(),1)));
		}
		if(StringUtils.isNotBlank(shipment.getCountry())){
			dc.add(Restrictions.eq("country", shipment.getCountry()));
		}
		dc.add(Restrictions.eq("status", "Closed"));
		dc.addOrder(Order.desc("orderedDate"));
		List<VendorOrder> orders =  vendorOrderDao.find(dc);
		for (VendorOrder order : orders) {
			Hibernate.initialize(order.getItems());
		}
		return orders;
	}
	
	@Transactional(readOnly = false)
	public void updateBillStatu(String shipmentId,String statu,String country){
		String sql="update amazoninfo_vendor_shipment set bill_statu=:p1 where asn=:p2 and country = :p3";
		shipmentDao.updateBySql(sql, new Parameter(statu,shipmentId,country));
	}
	
	public Map<String,Map<String,String>> findBarcode(){
		Map<String,Map<String,String>> map=Maps.newHashMap();
		String sql="SELECT DISTINCT r.`country`,t.`product_name`,t.`sku_in_vendor` FROM amazoninfo_vendor_order r  "+
                   " JOIN amazoninfo_vendor_orderitem t ON r.id=t.`order_id`";
		List<Object[]> list=shipmentDao.findBySql(sql);
		for (Object[] obj: list) {
			Map<String,String> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(), temp);
			}
			temp.put(obj[1].toString(), obj[2].toString());
		}
		return map;
	}
	
}
