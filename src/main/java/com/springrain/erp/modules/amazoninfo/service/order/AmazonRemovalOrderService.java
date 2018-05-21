package com.springrain.erp.modules.amazoninfo.service.order;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.order.AmazonRemovalOrderDao;
import com.springrain.erp.modules.amazoninfo.dao.order.AmazonReturnOrderShipmentDao;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonRemovalOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonReturnOrderShipment;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.SaleProfitService;

/**
 * 亚马逊产品Service
 * @author tim
 * @version 2014-06-26
 */
@Component
@Transactional(readOnly = true)
public class AmazonRemovalOrderService extends BaseService {

	@Autowired
	private AmazonRemovalOrderDao amazonRemovalOrderDao;
	
	@Autowired
	private SaleProfitService saleProfitService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private AmazonReturnOrderShipmentDao amazonReturnOrderShipmentDao;
	
	public Page<AmazonRemovalOrder> find(Page<AmazonRemovalOrder> page, AmazonRemovalOrder amazonRemovalOrder, String inStorage) {
		DetachedCriteria dc = amazonRemovalOrderDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(amazonRemovalOrder.getAmazonOrderId())){
			dc.add(Restrictions.eq("amazonOrderId", amazonRemovalOrder.getAmazonOrderId()));
		}
		if (StringUtils.isNotEmpty(amazonRemovalOrder.getCountry())){
			dc.add(Restrictions.eq("country", amazonRemovalOrder.getCountry()));
		}
		if (StringUtils.isNotEmpty(amazonRemovalOrder.getOrderStatus())){
			dc.add(Restrictions.eq("orderStatus", amazonRemovalOrder.getOrderStatus()));
		}
		if (StringUtils.isNotEmpty(amazonRemovalOrder.getOrderType())){
			dc.add(Restrictions.eq("orderType", amazonRemovalOrder.getOrderType()));
		}
		if (StringUtils.isNotEmpty(amazonRemovalOrder.getSource())){
			String sql = "SELECT DISTINCT t.id FROM `amazoninfo_removal_order` t,`amazoninfo_removal_orderitem` o"+
					" WHERE t.`id`=o.`order_id` AND (o.`sellersku` LIKE :p1 OR o.`product_name` LIKE :p1)";
			List<String> lists = amazonRemovalOrderDao.findBySql(sql, new Parameter("%" +amazonRemovalOrder.getSource()+ "%"));
			if(lists!=null && lists.size()>0){
				dc.add(Restrictions.in("id", lists));
			}
		}
		if (StringUtils.isNotEmpty(inStorage) && "1".equals(inStorage)){
			String sql = "SELECT DISTINCT t.id FROM `amazoninfo_removal_order` t,`amazoninfo_removal_orderitem` o " +
					" WHERE t.`id`=o.`order_id` AND t.`order_type`='Return' AND o.`stored_qty`<o.`completed_qty` " +
					" AND (o.`product_name` IS NOT NULL OR o.`sellersku` LIKE '%;%' OR o.`sellersku` LIKE '%\\_%')";
			List<String> lists = amazonRemovalOrderDao.findBySql(sql);
			if(lists!=null && lists.size()>0){
				dc.add(Restrictions.in("id", lists));
			}
		}
		if (!StringUtils.isNotEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("lastUpdateDate"));
		}
		return amazonRemovalOrderDao.find(page, dc);
	}
	
	public Timestamp getMaxDate(String accountName){
		String sql = "select min(purchase_date) from amazoninfo_removal_order where account_name = :p1 and order_status='Pending'  ";
		List<Object> rs = amazonRemovalOrderDao.findBySql(sql, new Parameter(accountName));
		if(rs.size()>=1){
			return (Timestamp)rs.get(0);
		}else{
			 sql = "select max(purchase_date) from amazoninfo_removal_order where account_name = :p1 ";
			 rs = amazonRemovalOrderDao.findBySql(sql, new Parameter(accountName));
			 if(rs.size()>=1){
				return (Timestamp)rs.get(0);
			 }else{
				 return null;
			 }
		}
	}
	
	public Map<String, String> skuAndNameMap(){
		String sql = "SELECT DISTINCT a.`sku` , CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN '_' ELSE '' END,a.`color`) FROM psi_sku a WHERE a.`del_flag` = '0' AND a.`country` != 'ebay' AND a.`product_name` != 'inateck other' AND a.`product_name` != 'inateck old'";
		List<Object[]> list =  amazonRemovalOrderDao.findBySql(sql);
		Map<String, String> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(), objects[1].toString());
		}
		return rs;
	}
	
	public AmazonRemovalOrder get(Integer id) {
		return amazonRemovalOrderDao.get(id);
	}
	
	
	public AmazonRemovalOrder findByEg(String orderId,String country){
		DetachedCriteria dc = amazonRemovalOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("amazonOrderId", orderId));
		dc.add(Restrictions.eq("country", country));
		List<AmazonRemovalOrder> rs = amazonRemovalOrderDao.find(dc);
		if(rs.size()==1){
			AmazonRemovalOrder order =  rs.get(0);
			Hibernate.initialize(order.getItems());
			return order;
		}
		return null;
	}
	

	@Transactional(readOnly = false)
	public  void save(List<AmazonRemovalOrder> amazonOrders) {
		for (AmazonRemovalOrder amazonOrder : amazonOrders) {
			amazonRemovalOrderDao.save(amazonOrder);
		}
	}

	@Transactional(readOnly = false)
	public void updateStoredQty(Map<Integer, Integer> itemQty) {
		String sql = "UPDATE `amazoninfo_removal_orderitem` t SET t.`stored_qty`=t.`stored_qty`+:p1 WHERE t.`id`=:p2";
		for (Map.Entry<Integer, Integer> entry : itemQty.entrySet()) { 
		    Integer id =entry.getKey();
			amazonRemovalOrderDao.updateBySql(sql, new Parameter(entry.getValue(), id));
		}
	}

	/**
	 * 查询可以入库的召回订单
	 */
	public Map<Integer, String> findForStore() {
		String sql = "SELECT DISTINCT t.id,t.`amazon_order_id` FROM `amazoninfo_removal_order` t,`amazoninfo_removal_orderitem` o " +
				" WHERE t.`id`=o.`order_id` AND t.`order_type`='Return' AND o.`stored_qty`<o.`completed_qty`" +
				" AND (o.`product_name` IS NOT NULL OR o.`sellersku` LIKE '%;%' OR o.`sellersku` LIKE '%\\_%')";
		List<Object[]> list =  amazonRemovalOrderDao.findBySql(sql);
		Map<Integer, String> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(Integer.parseInt(objects[0].toString()), objects[1].toString());
		}
		return rs;
	}

	/**
	 * 召回订单成本计算,按实时成本计算
	 */
	@Transactional(readOnly = false)
	public void updateOrderFee() {
		//SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		//String dateStr = format.format(DateUtils.addDays(new Date(), -1));
		Map<String,Map<String,Float>> allRate = amazonProduct2Service.getAllRateByDate();
		Map<String, Map<String, Float>> allFreight = saleProfitService.findAllFreight(allRate);	//所有产品运费信息(转换成欧元)
		Map<String, String> minDateMap = saleProfitService.findAllFreightMinDate();
		//分日期产品保本价[日期[sku_国家 本地贴保本价]]
		Map<String, Map<String, Float>> allPriceMap = Maps.newHashMap();
		String sql = "SELECT t.`id`,t.`product_name`,o.`country`,t.`sellersku`,DATE_FORMAT(o.`purchase_date`,'%Y%m%d') " +
				" FROM `amazoninfo_removal_orderitem` t,`amazoninfo_removal_order` o "+
				" WHERE t.`order_id`=o.`id` AND t.`buy_cost` IS NULL";
		List<Object[]> list =  amazonRemovalOrderDao.findBySql(sql);
		Map<Integer, List<Float>> map = Maps.newHashMap();
		for (Object[] obj : list) {
			Integer id = Integer.parseInt(obj[0].toString());
			List<Float> feeList = Lists.newArrayList();
			map.put(id, feeList);
			if (obj[1] == null) {
				feeList.add(0f);
				feeList.add(0f);
				continue;
			}
			String productName = obj[1].toString();
			String country = obj[2].toString();
			String sku = obj[3].toString();
			String dateStr = obj[4].toString();
			String key = productName + "_" + country.toUpperCase();
			if ("de,uk,fr,it,es".contains(country)) {
				key = productName + "_EU";
			} else if ("com,ca".contains(country)) {
				key = productName + "_US";
			}
			float avgFreight = saleProfitService.getFreight(key, dateStr, minDateMap, allFreight);
			if (avgFreight == -1) {
				avgFreight = 0f;
			}
			feeList.add(avgFreight);
			String temp = country;
			if ("de".equals(country)) {
				if (sku.toLowerCase().contains("uk")) {
					temp = "uk";
				} else if (sku.toLowerCase().contains("fr")) {
					temp = "fr";
				} else if (sku.toLowerCase().contains("it")) {
					temp = "it";
				} else if (sku.toLowerCase().contains("es")) {
					temp = "es";
				}
			}
			Map<String, Float> priceMap = allPriceMap.get(dateStr);
			if (priceMap == null) {
				priceMap = findLocalPriceMapByDay(dateStr, allRate.get(dateStr));
				allPriceMap.put(dateStr, priceMap);
			}
			Float buyCost = priceMap.get(sku + "_" + temp);
			if (buyCost == null) {
				//找到有记录的最近一条
				buyCost = saleProfitService.findRecentBuyCost(productName, temp, allRate.get(dateStr));
			}
			feeList.add(buyCost);
		}
		String updateSql = "UPDATE `amazoninfo_removal_orderitem` t SET t.`avg_freight`=:p1,t.`buy_cost`=:p2 WHERE t.`id`=:p3";
		for (Map.Entry<Integer,List<Float>> entry : map.entrySet()) { 
		    Integer id =entry.getKey();
			List<Float> feelList = entry.getValue();
			amazonRemovalOrderDao.updateBySql(updateSql, new Parameter(feelList.get(0), feelList.get(1), id));
		}
	}

	/**
	 * 统计所有产品的销量
	 * @return Map<String,Integer> [productName qty]
	 */
	public Map<String, Integer> getAllSalesVolume() {
		Map<String,Integer> rs = Maps.newLinkedHashMap();
		String sql="SELECT t.`product_name`,SUM(t.`completed_qty`) FROM `amazoninfo_removal_orderitem` t,`amazoninfo_removal_order` o"+ 
				" WHERE t.`order_id`=o.`id` AND o.`order_type` IN('Disposal','Liquidate') AND t.`product_name` IS NOT NULL GROUP BY t.`product_name`";
		List<Object[]> list = amazonRemovalOrderDao.findBySql(sql);
		for(Object[] obj:list){
			String productName = obj[0].toString();
			Integer quantity = obj[1]==null?0:Integer.parseInt(obj[1].toString());
			rs.put(productName, quantity);
	    }
		return rs;
	}
	
	
	/**
	 * 查询某产品召回中的数量
	 * @return Map<String,Integer> [productName qty]
	 */
	public Map<String,Integer> getReturningByProductName(String productName) {
		Map<String,Integer> rs = Maps.newLinkedHashMap();
		String sql="SELECT a.`country`,SUM(b.`in_process_qty`) FROM amazoninfo_removal_order AS a ,amazoninfo_removal_orderitem AS b WHERE a.id = b.`order_id` AND a.`order_status`='Pending' AND a.`order_type`='Return' AND b.`in_process_qty`>0  AND b.`disposition`='Sellable' AND b.`product_name`=:p1 GROUP BY a.`country`";
		List<Object[]> list = amazonRemovalOrderDao.findBySql(sql,new Parameter(productName));
		for(Object[] obj:list){
			String country = obj[0].toString();
			Integer quantity = obj[1]==null?0:Integer.parseInt(obj[1].toString());
			
			if("de,fr,uk,it,es,".contains(country+",")){
				if(rs.get("eu")!=null){
					rs.put("eu", rs.get("eu")+quantity);
				}else{
					rs.put("eu", quantity);
				}
			}
			
			if(rs.get("total")!=null){
				rs.put("total", rs.get("total")+quantity);
			}else{
				rs.put("total", quantity);
			}
			
			rs.put(country, quantity);
	    }
		return rs;
	}
	
	public Map<String,Map<String,Integer>> getReturningByProductName(Set<String> productName) {
		Map<String,Map<String,Integer>> rs = Maps.newLinkedHashMap();
		String sql="SELECT a.`country`,SUM(b.`in_process_qty`),b.`product_name` FROM amazoninfo_removal_order AS a ,amazoninfo_removal_orderitem AS b WHERE a.id = b.`order_id` AND a.`order_status`='Pending' AND a.`order_type`='Return' AND b.`in_process_qty`>0  AND b.`disposition`='Sellable' AND b.`product_name` in :p1 GROUP BY a.`country`,b.`product_name`";
		List<Object[]> list = amazonRemovalOrderDao.findBySql(sql,new Parameter(productName));
		for(Object[] obj:list){
			String country = obj[0].toString();
			Integer quantity = obj[1]==null?0:Integer.parseInt(obj[1].toString());
			String name=obj[2].toString();
			Map<String,Integer> temp=rs.get(name);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				rs.put(name, temp);
			}
			if("de,fr,uk,it,es,".contains(country+",")){
				if(temp.get("eu")!=null){
					temp.put("eu", temp.get("eu")+quantity);
				}else{
					temp.put("eu", quantity);
				}
			}
			
			if(temp.get("total")!=null){
				temp.put("total", temp.get("total")+quantity);
			}else{
				temp.put("total", quantity);
			}
			
			temp.put(country, quantity);
	    }
		return rs;
	}

	//获取产品本地贴价格(含采购价、关税、运费)[sku_国家 价格]
	public Map<String, Float> findLocalPriceMapByDay(String day, Map<String,Float> rate) {
		if (Integer.parseInt(day) < 20160106) {	//最早的时间为20160106
			day = "20160106";
		}
		Map<String, Float> rs = Maps.newHashMap();
		String sql = "SELECT t.`product_name`,t.`country`,t.`local_price`,t.`sku` FROM `amazoninfo_product_price` t WHERE DATE_FORMAT(t.date,'%Y%m%d')=:p1";
        List<Object[]>  list = amazonRemovalOrderDao.findBySql(sql, new Parameter(day));
        if(list == null || list.size() == 0){
        	sql = "SELECT MAX(a.`date`) FROM amazoninfo_product_price a ";
    		List<Object> dateList =  amazonRemovalOrderDao.findBySql(sql);
    		if(dateList.size() > 0){
    			sql = "SELECT t.`product_name`,t.`country`,t.`local_price`,t.`sku` FROM `amazoninfo_product_price` t  WHERE t.`date`=:p1 ";
    			list =  amazonRemovalOrderDao.findBySql(sql,new Parameter(dateList.get(0)));
    		}
        }
        float crate = MathUtils.getRate("USD", "EUR", rate);
        for (Object[] obj : list) {
			String country = obj[1].toString();
			float amzPrice = Float.parseFloat(obj[2].toString());
			String sku = obj[3].toString();
			rs.put(sku + "_" + country, amzPrice * crate);
		}
        return rs;
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AmazonRemovalOrderService service = applicationContext.getBean(AmazonRemovalOrderService.class);
		SaleProfitService saleProfitService = applicationContext.getBean(SaleProfitService.class);
		AmazonProduct2Service amazonProduct2Service = applicationContext.getBean(AmazonProduct2Service.class);
		/*Map<Integer, Integer> map = Maps.newHashMap();
		map.put(8478, 11);
		map.put(8479, 12);
		service.updateStoredQty(map);
		Map<Integer, String> rs = service.findForStore();
		for (Integer id : service.findForStore().keySet()) {
			System.out.println(id + "\t" + rs.get(id));
		}*/
		service.setSaleProfitService(saleProfitService);
		service.setAmazonProduct2Service(amazonProduct2Service);
		service.updateOrderFee();
		applicationContext.close();
	}

	public SaleProfitService getSaleProfitService() {
		return saleProfitService;
	}

	public void setSaleProfitService(SaleProfitService saleProfitService) {
		this.saleProfitService = saleProfitService;
	}

	public AmazonProduct2Service getAmazonProduct2Service() {
		return amazonProduct2Service;
	}

	public void setAmazonProduct2Service(AmazonProduct2Service amazonProduct2Service) {
		this.amazonProduct2Service = amazonProduct2Service;
	}
	
	
	public List<String> findReturnOrder(){
		String sql="SELECT amazon_order_id FROM amazoninfo_removal_order r WHERE r.`purchase_date`>='2017-10-25' AND order_type='Return' AND country='com'";
		return amazonRemovalOrderDao.findBySql(sql);
	}
	
	@Transactional(readOnly = false)
	public  void saveList(List<AmazonReturnOrderShipment> shipmentOrder) {
		amazonReturnOrderShipmentDao.save(shipmentOrder);
	}
	
	@Transactional(readOnly = false)
	public  void saveList(AmazonReturnOrderShipment shipmentOrder) {
		amazonReturnOrderShipmentDao.save(shipmentOrder);
	}
	
	public AmazonReturnOrderShipment find(String orderId,String shipmentId){
		DetachedCriteria dc = amazonReturnOrderShipmentDao.createDetachedCriteria();
		dc.add(Restrictions.eq("orderId", orderId));
		dc.add(Restrictions.eq("shipmentId", shipmentId));
		List<AmazonReturnOrderShipment> rs = amazonReturnOrderShipmentDao.find(dc);
		if(rs.size()==1){
			AmazonReturnOrderShipment order=rs.get(0);
			Hibernate.initialize(order.getItems());
			return order;
		}
		return null;
	}
	
	public Map<Integer,String> findTrack(){
		Map<Integer,String> map=Maps.newHashMap();
		String sql="SELECT id,tracking_number FROM amazoninfo_return_order_shipment t WHERE t.`tracking_state` IS NULL";
		List<Object[]> list=amazonReturnOrderShipmentDao.findBySql(sql);
		for (Object[] obj: list) {
			map.put(Integer.parseInt(obj[0].toString()),obj[1].toString());
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public  void updateState(Set<Integer> idSet) {
		String sql="update amazoninfo_return_order_shipment set tracking_state='1' where id in :p1";
		amazonReturnOrderShipmentDao.updateBySql(sql, new Parameter(idSet));
	}

	//统计美国召回订单已到货数量
	@Transactional(readOnly = false)
	public void updateDeliveredQty() {
		String sql = "UPDATE `amazoninfo_removal_orderitem` t SET t.`delivered_qty`= "+
				" (SELECT SUM(i.`quantity_shipped`) FROM amazoninfo_return_order_shipment s, amazoninfo_return_order_shipment_item i,amazoninfo_removal_order o "+
				" WHERE t.`order_id`=o.`id` AND s.`id`=i.`shipment` AND o.`amazon_order_id`=s.`order_id`  "+
				" AND i.`sku`=t.`sellersku` AND i.`disposition`=t.`disposition` AND s.`tracking_state`='1' AND o.`country`='com' AND o.`purchase_date`>'2017-10-21 00:03:02')";
		amazonReturnOrderShipmentDao.updateBySql(sql, null);
	}
	
	@Transactional(readOnly = false)
	public  void updateTrackState(Set<String> idSet) {
		String sql="update amazoninfo_return_order_shipment set tracking_state='1' where id in :p1";
		amazonReturnOrderShipmentDao.updateBySql(sql, new Parameter(idSet));
	}
	
	public Page<AmazonReturnOrderShipment> find(Page<AmazonReturnOrderShipment> page, AmazonReturnOrderShipment amazonReturnOrderShipment) {
		DetachedCriteria dc = amazonReturnOrderShipmentDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(amazonReturnOrderShipment.getOrderId())){
			dc.add(Restrictions.eq("orderId", amazonReturnOrderShipment.getOrderId()));
		}
		if (StringUtils.isNotEmpty(amazonReturnOrderShipment.getShipmentId())){
			dc.add(Restrictions.eq("shipmentId", amazonReturnOrderShipment.getShipmentId()));
		}
		if(StringUtils.isNotBlank(amazonReturnOrderShipment.getTrackingState())){
			if("1".equals(amazonReturnOrderShipment.getTrackingState())){
				dc.add(Restrictions.eq("trackingState","1"));
			}else{
				dc.add(Restrictions.isNull("trackingState"));
			}
		}
		if(StringUtils.isNotBlank(amazonReturnOrderShipment.getShippedDate())){
			dc.createAlias("this.items", "item");
			dc.add(Restrictions.like("item.sku","%"+amazonReturnOrderShipment.getShippedDate()+"%"));
		}
		
		if(StringUtils.isNotBlank(amazonReturnOrderShipment.getTrackingNumber())&&"1".equals(amazonReturnOrderShipment.getTrackingNumber())){
			dc.add(Restrictions.not(Restrictions.like("trackingNumber","http://wwwapps.ups.com%")));
			dc.add(Restrictions.not(Restrictions.like("trackingNumber","%tools.usps.com%")));
			dc.add(Restrictions.not(Restrictions.like("trackingNumber","%UPS Freight%")));
			dc.add(Restrictions.not(Restrictions.like("trackingNumber","%UPGF%")));
			dc.add(Restrictions.not(Restrictions.like("trackingNumber","%UPS Ground%")));
		}
		dc.addOrder(Order.desc("orderId"));
		return amazonReturnOrderShipmentDao.find(page, dc);
	}

    public Map<String, List<Integer>> export() {
        String sql = "SELECT i.`product_name`,SUM(IFNULL(i.`requested_qty`,0)),SUM(IFNULL(i.`completed_qty`,0)),SUM(IFNULL(i.`delivered_qty`,0))"
                +" FROM `amazoninfo_removal_order` o,`amazoninfo_removal_orderitem` i"
                +" WHERE o.`id`=i.`order_id` AND o.`country`='com' AND "
                +" o.`purchase_date`>='2017-10-26 06:41:25' AND o.`order_type`='Return' AND o.`order_status`!='Cancelled' GROUP BY i.`product_name`";
        List<Object[]> findBySql = amazonRemovalOrderDao.findBySql(sql);
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        for(Object[] obj : findBySql){
            List<Integer> list = Lists.newArrayList();
            list.add(Integer.parseInt(obj[1].toString()));
            list.add(Integer.parseInt(obj[2].toString()));
            list.add(Integer.parseInt(obj[3].toString()));
            map.put(obj[0].toString(), list);
        }
        return map;
    }

    //召回途中的数量（召回未完成的可售数）
	public Map<String, Map<String, Integer>> getInProcessQuantity() {
		Map<String, Map<String, Integer>> rs = Maps.newHashMap();
		String sql = "SELECT o.`country`,i.`product_name`,SUM(i.`in_process_qty`) "+
				" FROM `amazoninfo_removal_order` o, `amazoninfo_removal_orderitem` i "+
				" WHERE o.`id`=i.`order_id` AND o.`order_status`='Pending' AND o.`order_type`='Return' AND i.`disposition`='Sellable' "+
				" AND i.`in_process_qty`>0 AND i.`product_name` IS NOT NULL "+
				" GROUP BY i.`product_name`,o.`country`";
		List<Object[]> list = amazonRemovalOrderDao.findBySql(sql);
		for (Object[] obj : list) {
			String country = obj[0].toString();
			String productName = obj[1].toString();
			Integer qty = Integer.parseInt(obj[2].toString());
			Map<String, Integer> productMap = rs.get(productName);
			if (productMap == null) {
				productMap = Maps.newHashMap();
				rs.put(productName, productMap);
			}
			productMap.put(country, qty);
		}
		return rs;
	}
}
