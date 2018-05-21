package com.springrain.erp.modules.amazoninfo.service.order;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonOrderExtractDao;
import com.springrain.erp.modules.amazoninfo.dao.order.AmazonOrderDao;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderExtract;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.scheduler.AmazonWSConfig;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.ebay.entity.EbayOrder;
import com.springrain.erp.modules.ebay.entity.EbayOrderItem;
import com.springrain.erp.modules.sys.dao.GenerateSequenceInvoiceDao;

/**
 * 亚马逊产品Service
 * @author tim
 * @version 2014-06-26
 */
@Component
@Transactional(readOnly = true)
public class AmazonOrderService extends BaseService {

	@Autowired
	private AmazonOrderDao amazonOrdertDao;
	@Autowired
	private AmazonOrderExtractDao amazonOrderExtractDao;
	@Autowired
	private GenerateSequenceInvoiceDao 	 genDao;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AmazonOrderService.class);
	public AmazonOrder get(Integer id) {
		AmazonOrder order =  amazonOrdertDao.get(id);
		Hibernate.initialize(order.getItems());
		return order;
	}
	
	
	public Page<AmazonOrder> find(Page<AmazonOrder> page, AmazonOrder amazonOrder) {
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(amazonOrder.getAmazonOrderId())){
			dc.add(Restrictions.like("amazonOrderId", amazonOrder.getAmazonOrderId()+"%"));
		}else if(StringUtils.isNotEmpty(amazonOrder.getBuyerEmail())){
			dc.add(Restrictions.like("buyerEmail","%"+amazonOrder.getBuyerEmail()+"%"));
		}else if(StringUtils.isNotEmpty(amazonOrder.getInvoiceNo())){
			if(amazonOrder.getInvoiceNo().toUpperCase().startsWith("E")||amazonOrder.getInvoiceNo().toUpperCase().startsWith("U")||amazonOrder.getInvoiceNo().toUpperCase().startsWith("J")){
				String sql="SELECT amazon_order_id FROM amazoninfo_order_extract WHERE invoice_no=:p1";
				List<String> list = amazonOrdertDao.findBySql(sql,new Parameter(amazonOrder.getInvoiceNo()));
				if(list.size()>0&&list.get(0)!=null){
					dc.add(Restrictions.eq("amazonOrderId",list.get(0)));
				}else{
					dc.add(Restrictions.eq("amazonOrderId",amazonOrder.getInvoiceNo()));
				}
			}else{
				try{
					dc.add(Restrictions.eq("id",Integer.parseInt(amazonOrder.getInvoiceNo())));
				}catch(Exception e){
					dc.add(Restrictions.eq("amazonOrderId",amazonOrder.getInvoiceNo()));
				}
			}
		}else{
			if (amazonOrder.getPurchaseDate()!=null){
				dc.add(Restrictions.ge("purchaseDate",amazonOrder.getPurchaseDate()));
			}
			if (amazonOrder.getLastUpdateDate()!=null){
				dc.add(Restrictions.le("purchaseDate",DateUtils.addDays(amazonOrder.getLastUpdateDate(),1)));
			}
		}
		if(StringUtils.isNotEmpty(amazonOrder.getSellerOrderId())){
			dc.createAlias("this.items", "item");
			dc.add(Restrictions.like("item.sellersku","%"+amazonOrder.getSellerOrderId()+"%"));
		}
		int flag = 0 ;
		if (StringUtils.isNotEmpty(amazonOrder.getSalesChannel())){
			dc.add(Restrictions.eq("salesChannel","amazon."+("jp,uk".contains(amazonOrder.getSalesChannel())?"co.":("mx".equals(amazonOrder.getSalesChannel())?"com.":""))+amazonOrder.getSalesChannel()));
			if(StringUtils.isNotEmpty(amazonOrder.getInvoiceFlag())){
				flag = 1;
				dc.createAlias("this.shippingAddress", "address");
				String country = amazonOrder.getSalesChannel().toUpperCase();
				if("COM".equals(country)){
					country = "US";
				}else if("UK".equals(country)){
					country = "GB";
				}
				dc.add(Restrictions.ne("address.countryCode", country));
			}
		}
		if (StringUtils.isNotEmpty(amazonOrder.getShipServiceLevel())){
			if(flag==0){
				dc.createAlias("this.shippingAddress", "address");
			}
			dc.add(Restrictions.eq("address.countryCode", amazonOrder.getShipServiceLevel()));
		}
		if(StringUtils.isNotEmpty(amazonOrder.getOrderStatus())){
			dc.add(Restrictions.eq("orderStatus", amazonOrder.getOrderStatus()));
		}
		if(StringUtils.isNotEmpty(amazonOrder.getFulfillmentChannel())){
			dc.add(Restrictions.eq("fulfillmentChannel", amazonOrder.getFulfillmentChannel()));
		}
		if(StringUtils.isNotEmpty(amazonOrder.getOrderChannel())){
			dc.add(Restrictions.isNotNull("orderChannel"));
		}
		if(StringUtils.isNotBlank(amazonOrder.getIsBusinessOrder())&&"1".equals(amazonOrder.getIsBusinessOrder())){
			dc.add(Restrictions.eq("isBusinessOrder","1"));
		}
		page=amazonOrdertDao.find(page, dc);
		String sql="SELECT invoice_no FROM amazoninfo_order_extract WHERE amazon_order_id=:p1";
		for (AmazonOrder order : page.getList()) {
			List<String> list = amazonOrdertDao.findBySql(sql,new Parameter(order.getAmazonOrderId()));
			if(list.size()>0&&list.get(0)!=null){
				order.setInvoiceNo(list.get(0).toString());
			}
		}
		return page;
	}
	
	
	public Map<String,Map<String,AmazonOrder>> findPendingOrdersByAccount() {
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.and(Restrictions.in("orderStatus", Lists.newArrayList("Pending","Unshipped")),Restrictions.le("purchaseDate", DateUtils.addDays(new Date(),-4))),Restrictions.and(Restrictions.eq("orderStatus","Shipped"),Restrictions.isNull("buyerEmail"))));
		List<AmazonOrder> list =  amazonOrdertDao.find(dc);
		Map<String,Map<String,AmazonOrder>>  rs = Maps.newHashMap();
		for (AmazonOrder amazonOrder : list) {
			Map<String,AmazonOrder> temp = rs.get(amazonOrder.getAccountName());
			if(temp==null){
				temp = Maps.newHashMap();
				rs.put(amazonOrder.getAccountName(), temp);
			}
			Hibernate.initialize(amazonOrder.getItems());
			Hibernate.initialize(amazonOrder.getShippingAddress());
			temp.put(amazonOrder.getAmazonOrderId(),amazonOrder);
		}
		return rs;
	}
	
	public boolean  isNotExist(String orderId, String accountName){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.eq("amazonOrderId", orderId));
		dc.add(Restrictions.eq("accountName", accountName));
		return amazonOrdertDao.count(dc)==0;
	}
	
	public AmazonOrder findByEg(String orderId){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.eq("amazonOrderId", orderId));
		List<AmazonOrder> rs = amazonOrdertDao.find(dc);
		if(rs.size()==1){
			AmazonOrder order =  rs.get(0);
			Hibernate.initialize(order.getItems());
			Hibernate.initialize(order.getShippingAddress());
			Hibernate.initialize(order.getInvoiceAddress());
			return order;
		}
		return null;
	}
	
	public AmazonOrder findByEg(String orderId, String accountName){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.eq("amazonOrderId", orderId));
		dc.add(Restrictions.eq("accountName", accountName));
		List<AmazonOrder> rs = amazonOrdertDao.find(dc);
		if(rs.size()==1){
			AmazonOrder order =  rs.get(0);
			Hibernate.initialize(order.getItems());
			Hibernate.initialize(order.getShippingAddress());
			Hibernate.initialize(order.getInvoiceAddress());
			return order;
		}
		return null;
	}
	
	public AmazonOrder findByEgNoAdress(String orderId){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.eq("amazonOrderId", orderId));
		List<AmazonOrder> rs = amazonOrdertDao.find(dc);
		if(rs.size()==1){
			AmazonOrder order =  rs.get(0);
			Hibernate.initialize(order.getItems());
			return order;
		}
		return null;
	}
	
	public Map<String,AmazonOrder> findOrdersByEgNoAdress(Set<String> ordersId){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.in("amazonOrderId", ordersId));
		List<AmazonOrder> rs = amazonOrdertDao.find(dc);
		Map<String,AmazonOrder> result = Maps.newHashMap();
		for (AmazonOrder order : rs) {
			Hibernate.initialize(order.getItems());
			Hibernate.initialize(order.getShippingAddress());
			result.put(order.getAmazonOrderId(), order);
		}
		return result;
	}
	
	public List<AmazonOrder> findOrdersByEg(Set<String> ordersId){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.in("amazonOrderId", ordersId));
		List<AmazonOrder> rs = amazonOrdertDao.find(dc);
		for (AmazonOrder order : rs) {
			Hibernate.initialize(order.getItems());
			Hibernate.initialize(order.getShippingAddress());
		}
		return rs;
	}
	
	
	public AmazonOrder findByLazy(String orderId){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.eq("amazonOrderId", orderId));
		List<AmazonOrder> rs = amazonOrdertDao.find(dc);
		if(rs.size()==1){
			AmazonOrder order =  rs.get(0);
			return order;
		}
		return null;
	}
	
	public List<AmazonOrder> findByCustomId(String customId){
		List<String> orderList = findOrderIdByCustomId(customId);
		if (orderList == null ||orderList.size() == 0) {
			return Lists.newArrayList();
		}
 		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.in("amazonOrderId", orderList));
		dc.add(Restrictions.eq("orderStatus","Shipped"));
		List<AmazonOrder> rs = amazonOrdertDao.find(dc);
		return rs;
	}

	public List<AmazonOrder> findEgByCustomId(String customId){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		List<String> orderList = findOrderIdByCustomId(customId);
		if (orderList == null ||orderList.size() == 0) {
			return Lists.newArrayList();
		}
		dc.add(Restrictions.in("amazonOrderId", orderList));
		dc.add(Restrictions.eq("orderStatus","Shipped"));
		dc.addOrder(Order.desc("purchaseDate"));
		List<AmazonOrder> rs = amazonOrdertDao.find(dc);
		if(rs!=null){
			for (AmazonOrder amazonOrder : rs) {
				Hibernate.initialize(amazonOrder.getItems());
			}
		}
		return rs;
	}
	
	private List<String> findOrderIdByCustomId(String customId) {
		String sql = "SELECT t.`amazon_order_id` FROM `amazoninfo_order_extract` t WHERE t.`custom_id`=:p1";
		return amazonOrdertDao.findBySql(sql, new Parameter(customId));
	}
	
	public AmazonOrder findByEgIvoiceAddress(String orderId){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.eq("amazonOrderId", orderId));
		List<AmazonOrder> rs = amazonOrdertDao.find(dc);
		if(rs.size()==1){
			AmazonOrder order =  rs.get(0);
			Hibernate.initialize(order.getInvoiceAddress());
			return order;
		}
		return null;
	}
	
	public List<AmazonOrder> findExAma(Date beginDate, Date endDate){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.ge("purchaseDate", beginDate));
		dc.add(Restrictions.le("purchaseDate", endDate));
		dc.add(Restrictions.eq("fulfillmentChannel","MFN"));
		dc.add(Restrictions.in("orderStatus",new String[]{"PartiallyShipped","Unshipped"}));
		List<AmazonOrder> orders =  amazonOrdertDao.find(dc);
		for (AmazonOrder amazonOrder : orders) {
			Hibernate.initialize(amazonOrder.getItems());
			Hibernate.initialize(amazonOrder.getShippingAddress());
		}
		return orders;
	}
	
	public List<String> getShippedCountrys(AmazonOrder order){
		String sql  = "SELECT DISTINCT a.`country_code` FROM amazoninfo_address a, amazoninfo_order b WHERE a.`id` = b.`shipping_address` AND  " +
				"	b.`order_status`='Shipped' AND b.`sales_channel` LIKE :p1 AND " +
				"	a.`country_code` IS NOT NULL  AND b.`order_channel` IS NULL  and b.`purchase_date` > :p2 and b.`purchase_date` < :p3 ";
		String country = order.getSalesChannel();
		if(StringUtils.isEmpty(country)){
			country="%%";
		}else{
			country="%"+country+"%";
		}
		return amazonOrdertDao.findBySql(sql, new Parameter(country,order.getPurchaseDate(),DateUtils.addDays(order.getLastUpdateDate(),1)));
	}
	
	
	public Map<String,String> getOrderEmailMap(Set<String> orders){
		String sql  = "SELECT a.`amazon_order_id`,a.`buyer_email` FROM amazoninfo_order a WHERE a.`amazon_order_id` IN :p1 and a.`buyer_email` is not null";
		List<Object[]> list =  amazonOrdertDao.findBySql(sql, new Parameter(orders));
		Map<String,String> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(), objects[1].toString());
		}
		return rs;
	}
	
	public Page<AmazonOrder> findAmaInterface(Page<AmazonOrder>page,Date beginDate, Date endDate,String isMFN,String orderStatus){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.ge("purchaseDate", beginDate));
		dc.add(Restrictions.le("purchaseDate", endDate));
		if(StringUtils.isNotEmpty(isMFN)){
			if("MFN".equals(isMFN)){
				dc.add(Restrictions.eq("fulfillmentChannel","MFN"));
			}else if ("AFN".equals(isMFN)){
				dc.add(Restrictions.eq("fulfillmentChannel","AFN"));
			}
		}
		
		if(StringUtils.isNotEmpty(orderStatus)){
			dc.add(Restrictions.eq("orderStatus",orderStatus));
		}
		
		List<AmazonOrder> orders =  amazonOrdertDao.find(page,dc).getList();
		for (AmazonOrder amazonOrder : orders) {
			for (AmazonOrderItem item : amazonOrder.getItems()) {
				item.setOrder(null);
			}
			if(amazonOrder.getShippingAddress()!=null){
				amazonOrder.getShippingAddress().setOrder(null);
			}
			if(amazonOrder.getInvoiceAddress()!=null){
				amazonOrder.getInvoiceAddress().setOrder(null);
			}
		}
		return page;
	}

	public List<AmazonOrder> findMfnOrder(){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		Date date=new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		//dc.add(Restrictions.ge("purchaseDate",DateUtils.addDays(date,-1)));
		dc.add(Restrictions.ge("purchaseDate",DateUtils.addDays(date,-7)));
		dc.add(Restrictions.le("purchaseDate",new Date()));
		dc.add(Restrictions.eq("fulfillmentChannel","MFN"));
		List<String> status = Lists.newArrayList(); 
		status.add("Unshipped");
		status.add("Shipped");
		status.add("Canceled");
		dc.add(Restrictions.in("orderStatus",status));
		List<AmazonOrder> rs = amazonOrdertDao.find(dc);
		for (AmazonOrder amazonOrder : rs) {
			for (AmazonOrderItem item : amazonOrder.getItems()) {
				Hibernate.initialize(item);
			}
			Hibernate.initialize(amazonOrder.getInvoiceAddress());
			Hibernate.initialize(amazonOrder.getShippingAddress());	
		}
		return rs;
	}
	
	
	public Page<EbayOrder> findMFNOrder(Page<EbayOrder> page, EbayOrder order){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.ge("purchaseDate", order.getCreatedTime()));
		dc.add(Restrictions.le("purchaseDate", order.getShippedTime()));
		dc.add(Restrictions.eq("fulfillmentChannel","MFN"));
		String statu = order.getStatus();
		List<String> status = Lists.newArrayList(); 
		//new String[]{"PartiallyShipped","Unshipped"}
		if(statu.length()!=0){
			if("0".equals(statu)){
				status.add("PendingAvailability");
				status.add("Pending");
				status.add("Canceled");
				status.add("Unfulfillable");
			}else if("1".equals(statu)){
				status.add("Unshipped");
			}else if("2".equals(statu)){
				status.add("Shipped");
			}else if("3".equals(statu)){
				status.add("PartiallyShipped");
			}
			dc.add(Restrictions.in("orderStatus",status));
		}
		if(StringUtils.isNotEmpty(order.getOrderId())){
			dc.add(Restrictions.or(Restrictions.like("amazonOrderId","%"+order.getOrderId()+"%"),Restrictions.like("buyerName","%"+order.getOrderId()+"%")));
		}
		
		Page<AmazonOrder> pageAmazon = new Page<AmazonOrder>(page.getPageNo(), page.getPageSize(),page.getCount());
		
		pageAmazon = amazonOrdertDao.find(pageAmazon,dc);
		List<EbayOrder> list = Lists.newArrayList();
		for (AmazonOrder amazonOrder : pageAmazon.getList()) {
			EbayOrder ebayOrder = new EbayOrder();
			ebayOrder.setId(amazonOrder.getId());
			ebayOrder.setBuyerUserId(amazonOrder.getBuyerName());
			ebayOrder.setSellerEmail(amazonOrder.getSalesChannel());
			ebayOrder.setOrderId(amazonOrder.getAmazonOrderId());
			ebayOrder.setCreatedTime(amazonOrder.getPurchaseDate());
			ebayOrder.setTotal(new BigDecimal(amazonOrder.getOrderTotal()==null ?0:amazonOrder.getOrderTotal()));
		
			if(StringUtils.isEmpty(statu)){
				String statuss=amazonOrder.getOrderStatus();
				
				if("PendingAvailability,Pending,Canceled,Unfulfillable".contains(statuss)){
					ebayOrder.setStatus("0");
				}else if("Unshipped".equals(statuss)){
					ebayOrder.setStatus("1");
				}else if("Shipped".equals(statuss)){
					ebayOrder.setStatus("2");
				}else if("PartiallyShipped".equals(statuss)){
					ebayOrder.setStatus("3");
				}
			}else{
				ebayOrder.setStatus(statu);
			}
			
			
			EbayOrderItem  eabyItem =null;
			List<EbayOrderItem>  eabyItemList =Lists.newArrayList();
			for(AmazonOrderItem  item : amazonOrder.getItems()){
			     eabyItem = new EbayOrderItem();
			     eabyItem.setSku(item.getSellersku());
			     eabyItem.setTransactionId(item.getOrderItemId());
			     eabyItem.setQuantityPurchased(item.getQuantityOrdered());
			     eabyItemList.add(eabyItem);
			}
			ebayOrder.setItems(eabyItemList);
			list.add(ebayOrder);
		}
		page.setList(list);
		page.setCount(pageAmazon.getCount());
		return page;
	}

	
	public List<Object[]> findExp(AmazonOrder amazonOrder) throws ParseException {
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		String search = amazonOrder.getSellerOrderId();
		if (StringUtils.isNotEmpty(search)){
			try {
				int id = Integer.parseInt(search);
				dc.add(Restrictions.eq("id",id));
			} catch (NumberFormatException e) {
				dc.createAlias("this.items", "item");
				dc.add(Restrictions.or(Restrictions.like("amazonOrderId", "%"+search+"%"),
						Restrictions.like("buyerEmail","%"+search+"%"),Restrictions.like("item.sellersku","%"+search+"%")));
			}
		}
		int flag = 0 ;
		if (StringUtils.isNotEmpty(amazonOrder.getSalesChannel())){
			dc.add(Restrictions.eq("salesChannel", "amazon."+("jp,uk".contains(amazonOrder.getSalesChannel())?"co.":"")+amazonOrder.getSalesChannel()));
			if(StringUtils.isNotEmpty(amazonOrder.getInvoiceFlag())){
				String country = amazonOrder.getSalesChannel().toUpperCase();
				if("COM".equals(country)){
					country = "US";
				}else if("UK".equals(country)){
					country = "GB";
				}
				dc.createAlias("this.shippingAddress", "address");
				flag = 1;
				dc.add(Restrictions.ne("address.countryCode", country));
			}
		}
		if (StringUtils.isNotEmpty(amazonOrder.getShipServiceLevel())){
			if(flag==0){
				dc.createAlias("this.shippingAddress", "address");
				flag = 1;
			}
			dc.add(Restrictions.eq("address.countryCode", amazonOrder.getShipServiceLevel()));
		}
		if (amazonOrder.getPurchaseDate()!=null){
			dc.add(Restrictions.ge("purchaseDate",amazonOrder.getPurchaseDate()));
		}
		if (amazonOrder.getLastUpdateDate()!=null){
			dc.add(Restrictions.le("purchaseDate",DateUtils.addDays(amazonOrder.getLastUpdateDate(),1)));
		}
			dc.add(Restrictions.eq("orderStatus", "Shipped"));
		
		if(StringUtils.isNotEmpty(amazonOrder.getOrderChannel())){
			dc.add(Restrictions.isNotNull("orderChannel"));
		}
		
		dc.addOrder(Order.desc("purchaseDate"));
		return amazonOrdertDao.findExp(dc);
	}
	
	
	
	public List<Object[]> findExp(String month,String country) throws ParseException {
		Date monthDate = DateUtils.parseDate(month+"-1",new String[]{"yyyy-MM-dd"});
		Date start = DateUtils.getFirstDayOfMonth(monthDate);
		Date end = DateUtils.getLastDayOfMonth(monthDate);
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		List<String> countrys = null;
		if("eu".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.co.uk","Amazon.de","Amazon.fr","Amazon.es","Amazon.it") ;
		}else if (country.equalsIgnoreCase("EU-FilterRefundVat")){
			countrys = Lists.newArrayList("Amazon.co.uk","Amazon.de","Amazon.fr","Amazon.es","Amazon.it") ;
			String sql="SELECT DISTINCT d.`amazon_order_id` FROM amazoninfo_refund d JOIN amazoninfo_refund_item t ON d.id=t.`refund_id` "+
		              " WHERE d.`refund_state`='1' AND (t.`remark` LIKE '%退税%' OR t.`remark` LIKE '%tax%') and d.country in :p1 ";
			List<Object> list = amazonOrdertDao.findBySql(sql, new Parameter(Lists.newArrayList("de","fr","it","es","uk")));
			if(list.size()>0){
				dc.add(Restrictions.not(Restrictions.in("amazonOrderId",list)));
			}
		}else if("ca".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.ca") ;
		}else if("us".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.com") ;
		}else if("jp".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.co.jp") ;
		}
		dc.add(Restrictions.in("salesChannel",countrys));
		dc.add(Restrictions.ge("purchaseDate",start));
		dc.add(Restrictions.le("purchaseDate",DateUtils.addDays(end,1)));
		dc.add(Restrictions.eq("orderStatus", "Shipped"));
		dc.add(Restrictions.isNull("orderChannel"));
		return amazonOrdertDao.findExp(dc);
	}
	
	public Integer findMaxId(String country,int index){
		String suff = ""; 
		StringBuffer buf= new StringBuffer();
		for (int i = 0; i < index; i++) {
			buf.append("_");
		}
		suff = buf.toString();
		String sql="select max(id)  from amazoninfo_order_extract where country=:p1 and invoice_flag not like :p2 ";
		List<Integer> list=amazonOrderExtractDao.findBySql(sql,new Parameter(country,suff+"1%"));
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public List<AmazonOrder> find(int startId,Integer endHours,String country,int index,Set<String>asins){
		String suff = ""; 
		StringBuffer buf= new StringBuffer();
		for (int i = 0; i < index; i++) {
			buf.append("_");
		}
		suff = buf.toString();
		List<AmazonOrder> orders = Lists.newArrayList();
		
		String sql="select amazon_order_id,invoice_flag,invoice_no,print_date from amazoninfo_order_extract where country=:p1 and id>:p2 and order_total>0 and invoice_flag not like :p3 and account_name like 'Inateck%' ";
		List<Object[]> orderList=Lists.newArrayList();
		if(endHours!=null && endHours>0){
			Date date = new Date();
			date = DateUtils.addHours(date, -endHours);
			sql+=" and last_update_date < :p4 ";
			orderList=amazonOrderExtractDao.findBySql(sql, new Parameter(country,startId,suff+"1%",date));
		}else{
			orderList=amazonOrderExtractDao.findBySql(sql, new Parameter(country,startId,suff+"1%"));
		}
		Map<String,Object[]> map=Maps.newHashMap();
		if(orderList!=null&&orderList.size()>0){
			for (Object[] obj: orderList) {
				String orderId=obj[0].toString();
				//String invoiceFlag=obj[1].toString();
				map.put(orderId, obj);
			}
			DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
			dc.add(Restrictions.in("amazonOrderId", map.keySet()));
			dc.add(Restrictions.eq("salesChannel", "amazon."+("jp,uk".contains(country)?"co.":"")+country));
			dc.add(Restrictions.isNotNull("buyerEmail"));
			if(asins.size()>0){
				dc.createAlias("this.items", "item");
				dc.add(Restrictions.in("item.asin",asins));
			}
			
			List<AmazonOrder> tempOrders = amazonOrdertDao.find(dc);
			if(tempOrders.size()>0){
				for (AmazonOrder amazonOrder : tempOrders) {
					Hibernate.initialize(amazonOrder.getShippingAddress());
					Hibernate.initialize(amazonOrder.getItems());
					Object[] obj=map.get(amazonOrder.getAmazonOrderId());
					amazonOrder.setInvoiceFlag(obj[1].toString());
					amazonOrder.setInvoiceNo(obj[2]==null?"":obj[2].toString());
					amazonOrder.setPrintDate(obj[3]==null?null:(Date)obj[3]);
					orders.add(amazonOrder);
				}
			}
		}
		return orders;
	}
	
	public List<AmazonOrder> findNoInvoice(int startId,String country,int index,Set<String>asins){
		String suff = ""; 
		StringBuffer buf= new StringBuffer();
		for (int i = 0; i < index; i++) {
			buf.append("_");
		}
		suff = buf.toString();
		String sql="select amazon_order_id,invoice_flag,invoice_no,print_date from amazoninfo_order_extract where last_update_date>=:p1 and country=:p2 and id<:p3 and order_total>0 and invoice_flag like :p4 and account_name like 'Inateck%' ";
		List<Object[]> orderList=amazonOrderExtractDao.findBySql(sql, new Parameter(DateUtils.addDays(new Date(),-30),country,startId,suff+"0%"));
		Map<String,Object[]> map=Maps.newHashMap();
		List<AmazonOrder> orders = Lists.newArrayList();
		if(orderList!=null&&orderList.size()>0){
			for (Object[] obj: orderList) {
				String orderId=obj[0].toString();
				//String invoiceFlag=obj[1].toString();
				map.put(orderId, obj);
			}
			DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
			dc.add(Restrictions.in("amazonOrderId", map.keySet()));
			dc.add(Restrictions.eq("salesChannel", "amazon."+("jp,uk".contains(country)?"co.":"")+country));
			dc.add(Restrictions.isNotNull("buyerEmail"));
			if(asins.size()>0){
				dc.createAlias("this.items", "item");
				dc.add(Restrictions.in("item.asin",asins));
			}
			List<AmazonOrder> tempOrders =  amazonOrdertDao.find(dc);
			if(tempOrders.size()>0){
				for (AmazonOrder amazonOrder : tempOrders) {
					Hibernate.initialize(amazonOrder.getShippingAddress());
					Hibernate.initialize(amazonOrder.getItems());
					Object[] obj=map.get(amazonOrder.getAmazonOrderId());
					amazonOrder.setInvoiceFlag(obj[1].toString());
					amazonOrder.setInvoiceNo(obj[2]==null?"":obj[2].toString());
					amazonOrder.setPrintDate(obj[3]==null?null:(Date)obj[3]);
					orders.add(amazonOrder);
				}
			}
		}	
		return orders;
	}
	
	@Transactional(readOnly = false)
	public  synchronized void save(AmazonOrder amazonOrder) {
		if(amazonOrder.getId()!=null&&amazonOrder.getId()>0){
			amazonOrdertDao.getSession().merge(amazonOrder); //用库里查出来的保存
		}else{
			amazonOrdertDao.save(amazonOrder);
		}
	}
	
	
	@Transactional(readOnly = false)
	public void save2(AmazonOrder amazonOrder) {
		amazonOrdertDao.save(amazonOrder);
	}
	
	@Transactional(readOnly = false)
	public  void save(List<AmazonOrder> amazonOrders) {
		for (AmazonOrder amazonOrder : amazonOrders) {
			amazonOrdertDao.save(amazonOrder);
		}
	}
	
	
	public Float findPriceByOrder(String accountName,String sku){
		//实时查不到则查历史订单
		String sql = "SELECT DISTINCT b.`item_price` FROM amazoninfo_order a ,amazoninfo_orderitem b WHERE a.`id` = b.`order_id` AND a.`order_status` IN ('Shipped','UnShipped') AND b.`quantity_ordered` = 1 AND b.`item_price`>0 and (b.`promotion_discount` IS NULL OR b.`promotion_discount` = 0) AND b.`sellersku` = :p1 AND a.`account_name` = :p2 ORDER BY a.`purchase_date` DESC";
	    List<Object> list = amazonOrdertDao.findBySql(sql, new Parameter(sku, accountName));
	    if(list.size()>0){
	    	 return Float.parseFloat(list.get(0).toString());
	    }
	    return null;
	}
	
	
	public Float findPrice(String country,String sku){
		String sql = "SELECT a.`sale_price` FROM amazoninfo_product_history_price a WHERE a.`country` = :p1 AND a.`sku` = :p2 AND a.`sale_price`>0 AND a.`sale_price` IS NOT NULL ORDER BY a.`data_date` DESC";
		List<Object> list = amazonOrdertDao.findBySql(sql, new Parameter(country,sku));
		if(list.size()>0){
			return ((BigDecimal)list.get(0)).floatValue();
		}
		return null;
	}
	
	public Map<String,Float> findPriceMapByAccount(String account){
		String sql = "SELECT a.`sku`,a.`sale_price` FROM amazoninfo_product2 a WHERE a.`active` = '1' and a.`sale_price`> 0 AND a.`account_name` = :p1";
		List<Object[]> list = amazonOrdertDao.findBySql(sql, new Parameter(account));
		Map<String,Float> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			float price = Float.parseFloat(objs[1].toString());
			rs.put(objs[0].toString(),price);
		}
		if(rs.size()==0){
			sql = "SELECT DISTINCT b.`sellersku`,b.`item_price` FROM amazoninfo_order a, amazoninfo_orderitem b WHERE a.`id` = b.`order_id` AND a.`account_name` = :p1 AND a.`order_status` = 'Shipped' and b.`item_price` > 0 AND b.`quantity_ordered` = 1 AND a.`purchase_date` > DATE_ADD(CURDATE(),INTERVAL -10 DAY) ORDER BY a.`purchase_date` DESC";
			list = amazonOrdertDao.findBySql(sql, new Parameter(account));
			for (Object[] objs : list) {
				String sku = objs[0].toString();
				if(rs.get(sku)==null){
					float price = Float.parseFloat(objs[1].toString());
					rs.put(sku, price);
				}
			}
		}
		return rs;
	}
	
	public Map<String,Float> findPriceMap(String country){
		String sql = "SELECT a.`sku`,a.`sale_price` FROM amazoninfo_product2 a WHERE a.`active` = '1' and a.`sale_price`> 0 AND a.`country` = :p1";
		List<Object[]> list = amazonOrdertDao.findBySql(sql, new Parameter(country));
		Map<String,Float> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			float price = Float.parseFloat(objs[1].toString());
			rs.put(objs[0].toString(),price);
		}
		if(rs.size()==0){
			sql = "SELECT DISTINCT b.`sellersku`,b.`item_price` FROM amazoninfo_order a, amazoninfo_orderitem b WHERE a.`id` = b.`order_id` AND a.`sales_channel` LIKE :p1 AND a.`order_status` = 'Shipped' and b.`item_price` > 0 AND b.`quantity_ordered` = 1 AND a.`purchase_date` > DATE_ADD(CURDATE(),INTERVAL -10 DAY) ORDER BY a.`purchase_date` DESC";
			list = amazonOrdertDao.findBySql(sql, new Parameter("%"+country+"%"));
			for (Object[] objs : list) {
				String sku = objs[0].toString();
				if(rs.get(sku)==null){
					float price = Float.parseFloat(objs[1].toString());
					rs.put(sku, price);
				}
			}
		}
		return rs;
	}
	
	//删除orderItem
	@Transactional(readOnly = false)
	public void deleteOrderItem(String itemId) {
		Parameter parameter =new Parameter(itemId);
		amazonOrdertDao.updateBySql("delete from amazoninfo_orderitem where id =:p1", parameter);
	}
	
	@Transactional(readOnly = false)
	public void  updateOrderInvoiceFlagById(String invoiceFlag,String orderId) {
		amazonOrdertDao.updateBySql("update amazoninfo_order_extract set invoice_flag=:p1 WHERE amazon_order_id=:p2", new Parameter(invoiceFlag,orderId));
	}
	
	//客户号设置相关方法
	@SuppressWarnings("unchecked")
	public Set<String> getOrderIds(String country){
		Set<String> idSet=Sets.newHashSet();
		Set<String> noInvoiceEmail=Sets.newHashSet();
		String sql = "SELECT t.`amazon_order_id`,e.invoice_flag,t.buyer_email  FROM amazoninfo_order t,`amazoninfo_order_extract` e "+
				" WHERE t.`amazon_order_id`=e.`amazon_order_id` AND t.last_update_date>DATE_ADD(NOW(),INTERVAL -90 DAY) "+
				" AND e.`custom_id` IS NULL AND t.`order_channel` IS NULL  AND e.country= :p1";
		
		List<Object[]> orderList=amazonOrdertDao.createSqlQuery(sql, new Parameter(country)).list();
		
		//已发过账单或邀评邮件
		if(orderList!=null&&orderList.size()>0){
			for (Object[] obj: orderList) {
				if(obj[1]!=null&&StringUtils.isNotBlank(obj[1].toString())&&obj[1].toString().contains("1")){
					idSet.add(obj[0].toString());
				}else if(obj[2]!=null&&StringUtils.isNotBlank(obj[2].toString())&&!obj[1].toString().contains("1")){
					noInvoiceEmail.add(obj[2].toString());
				}
			}
		}
		
		if(noInvoiceEmail!=null&&noInvoiceEmail.size()>0){
			String comment="SELECT t.`amazon_order_id`,t.buyer_email  FROM amazoninfo_order t "+
					" JOIN `amazoninfo_order_extract` e ON t.`amazon_order_id`=e.`amazon_order_id`  "+
				    "  JOIN amazoninfo_comment m ON m.`send_email`=t.`buyer_email` "+
				    "  WHERE t.last_update_date>DATE_ADD(NOW(),INTERVAL -30 DAY) AND e.country=:p1 and e.invoice_flag not like '%1%' AND m.`send_flag`='1' "+
					" AND e.`custom_id` IS NULL AND t.`order_channel` IS NULL ";
			List<Object[]> commentList=amazonOrdertDao.findBySql(comment,new Parameter(country));
			if(commentList!=null&&commentList.size()>0){
				for (Object[] obj: commentList) {
					if(obj[1]!=null&&StringUtils.isNotBlank(obj[1].toString())){
						noInvoiceEmail.remove(obj[1].toString());
						idSet.add(obj[0].toString());
					}
				}
			}
			
			if(noInvoiceEmail!=null&&noInvoiceEmail.size()>0){
				String emailSql="SELECT t.`amazon_order_id`,t.buyer_email  FROM amazoninfo_order t "+
							" JOIN `amazoninfo_order_extract` e ON t.`amazon_order_id`=e.`amazon_order_id`  "+
						    "  JOIN custom_email_manager m ON m.`revert_email`=t.`buyer_email` "+
						    "  WHERE t.last_update_date>DATE_ADD(NOW(),INTERVAL -30 DAY) AND e.country=:p1 and e.invoice_flag not like '%1%' "+
							" AND e.`custom_id` IS NULL AND t.`order_channel` IS NULL ";
				List<Object[]> emailList=amazonOrdertDao.findBySql(emailSql,new Parameter(country));
				if(emailList!=null&&emailList.size()>0){
					for (Object[] obj: emailList) {
						if(obj[1]!=null&&StringUtils.isNotBlank(obj[1].toString())){
							noInvoiceEmail.remove(obj[1].toString());
							idSet.add(obj[0].toString());
						}
					}
				}
			}
			

			if(noInvoiceEmail!=null&&noInvoiceEmail.size()>0){
				String emailSql="SELECT t.`amazon_order_id`,t.buyer_email  FROM amazoninfo_order t "+
						" JOIN `amazoninfo_order_extract` e ON t.`amazon_order_id`=e.`amazon_order_id`  "+
					    "  JOIN custom_send_email m ON m.`send_email`=t.`buyer_email` "+
					    "  WHERE t.last_update_date>DATE_ADD(NOW(),INTERVAL -30 DAY) AND e.country=:p1 and e.invoice_flag not like '%1%' "+
						" AND e.`custom_id` IS NULL AND t.`order_channel` IS NULL ";
			List<Object[]> emailList=amazonOrdertDao.findBySql(emailSql,new Parameter(country));
			if(emailList!=null&&emailList.size()>0){
				for (Object[] obj: emailList) {
					idSet.add(obj[0].toString());
				}
			}
		   }
		}
		return idSet;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String> getOrderIdsAndEmail(List<String> countrySuffix){
		Map<String,String>  map=Maps.newHashMap();
		Object[] params = {countrySuffix};
		Parameter parameter =new Parameter(params);
		//String sql = "select amazon_order_id from amazoninfo_order  where order_status = 'Shipped' and custom_id is Null  and order_channel is null and sales_channel in :p1 ";
		String sql = "SELECT t.`amazon_order_id`,t.buyer_email FROM amazoninfo_order t,`amazoninfo_order_extract` e "+
				" WHERE t.`amazon_order_id`=e.`amazon_order_id` AND t.`order_status` = 'Shipped' and e.invoice_flag  like '%1%' "+
				" AND e.`custom_id` IS NULL AND t.`order_channel` IS NULL and t.buyer_email is not null AND t.last_update_date>DATE_ADD(NOW(),INTERVAL -10 DAY) AND t.`sales_channel` IN :p1";
		List<Object[]> list=amazonOrdertDao.createSqlQuery(sql, parameter).list();
		for (Object[] obj: list) {
			map.put(obj[0].toString(), obj[1].toString());
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void  updateCustomId(String orderId,String customId) {
		Object[] str = {customId,orderId};
		Parameter parameter =new Parameter(str);
		//amazonOrdertDao.updateBySql("update amazoninfo_order set custom_id=:p1 WHERE amazon_order_id = :p2", parameter);
		//客户ID分离到amazoninfo_order_extract表
		amazonOrdertDao.updateBySql("update amazoninfo_order_extract set custom_id=:p1 WHERE amazon_order_id = :p2", parameter);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String> getOrderIdsMap(List<String> countrySuffix){
		Object[] params = {countrySuffix};
		Parameter parameter =new Parameter(params);
		List<Object[]> a = amazonOrdertDao.createSqlQuery("select amazon_order_id,buyer_email from amazoninfo_order  where order_status = 'Shipped' and custom_id is Null and sales_channel in :p1 ", parameter).list();
		Map<String,String> rs = Maps.newHashMap();
		for (Object[] objects : a) {
			rs.put(objects[0].toString(), objects[1].toString());
		}
		return rs;
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getMap(List<String>countrys){
		Object[] params = {countrys};
		Parameter parameter =new Parameter(params);
		List<Object> a = amazonOrdertDao.createSqlQuery("SELECT DISTINCT custom_email FROM custom_event_manager WHERE TYPE IN ('1','2') AND custom_email IS NOT NULL AND custom_email!='' AND country IN :p1",parameter).list();
		Set<String> rs =Sets.newHashSet();
		for (Object objects : a) {
			rs.add(objects.toString());
		}
		return rs;
	}
	
	public List<Map<String,Object>> countAllPromotions(Date startDate,Date endDate,String country){
		endDate = DateUtils.addDays(endDate, 1);
		String sql = "SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) AS NAME,a.asin,a.promotion_ids,a.promotion_discount,SUM(quantity_ordered) as sum ,SUM(a.item_price-ifnull(a.promotion_discount,0)) as sumPrice, b.`sales_channel`,t.name groupName " +
				" FROM amazoninfo_orderitem a " +
				" LEFT JOIN amazoninfo_order b ON a.order_id=b.id"+
				" left join psi_product p on a.`product_name`=concat(p.brand,' ',p.model)   and p.del_flag='0' "+
				" left join sys_dict d on d.value=p.type and d.type='product_type' and d.del_flag='0' "+
				" left join psi_product_type_group g on g.dict_id=d.id "+
				" left join psi_product_type_dict t on g.id=t.id and t.del_flag='0' "+
				" WHERE a.`product_name` is not null  AND b.`purchase_date`>=:p1 And b.`purchase_date`<:p2 AND b.`order_status` in ('Shipped','Pending','Unshipped')  "+ 
				" AND ((a.promotion_ids!='B2B Free Shipping' AND a.promotion_ids NOT LIKE '%Core Free Shipping%'   AND a.`promotion_ids` IS NOT NULL) OR (a.`promotion_ids` IS NULL AND a.`promotion_discount` >0) or (a.promotion_ids LIKE '%Core Free Shipping%' AND a.promotion_ids LIKE '%,%') )"+
				" AND b.`sales_channel` LIKE :p3  GROUP BY NAME,a.asin,t.name,b.`sales_channel`,a.`promotion_ids` ,a.`promotion_discount` order by name desc";
		List<Object[]> list = amazonOrdertDao.findBySql(sql, new Parameter(startDate,endDate,"%"+country+"%"));
		List<Map<String,Object>> rs = Lists.newArrayList();
		for (Object[] objs : list) {
			Map<String,Object> map = Maps.newLinkedHashMap();
			map.put("name", objs[0]);
			map.put("asin", objs[1]);
			map.put("promotionIds", objs[2]==null?"闪购":objs[2]);
			map.put("promotionDiscount",objs[3]==null?"未付款":((BigDecimal)objs[3]).floatValue()+"");
			map.put("sum", ((BigDecimal)objs[4]).intValue()+"");
			map.put("sales", objs[5]==null?"":objs[5]);
			String countryStr = objs[6].toString();
			map.put("country",countryStr.substring(countryStr.lastIndexOf(".")+1));
			map.put("groupName", objs[7]==null?"":objs[7].toString());
			rs.add(map);
		}
		return rs;
	}
	
	public List<Map<String,Object>> countPromotions(Date startDate,Date endDate,String country,String lineType){
		endDate = DateUtils.addDays(endDate, 1);
		String sql="SELECT a.product_name,a.ASIN,a.promotion_ids,a.discount,SUM(a.quantity) sum, " +
				  " TRUNCATE((CASE WHEN a.country='uk' THEN SUM(a.sales)*"+AmazonProduct2Service.getRateConfig().get("GBP/EUR")+" WHEN a.country='ca' THEN SUM(a.sales)*"
					+AmazonProduct2Service.getRateConfig().get("CAD/EUR")+" WHEN a.country='jp' THEN SUM(a.sales)*"+
					AmazonProduct2Service.getRateConfig().get("JPY/EUR")+" WHEN a.country='com' THEN sum(a.sales)*"+AmazonProduct2Service.getRateConfig().get("USD/EUR")+" WHEN a.country='mx' THEN sum(a.sales)*"+AmazonProduct2Service.getRateConfig().get("MXN/EUR")+" ELSE SUM(a.sales) END ),2) sumPrice,a.country,t.name groupName,a.code "+
		  " FROM amazoninfo_promotions_report a "+
		  " left join psi_product p on SUBSTRING_INDEX(a.`product_name`,'_',1)=concat(p.brand,' ',p.model)   and p.del_flag='0' "+
			" left join sys_dict d on d.value=p.type and d.type='product_type' and d.del_flag='0' "+
			" left join psi_product_type_group g on g.dict_id=d.id "+
			" left join psi_product_type_dict t on g.id=t.id and t.del_flag='0' "+
			" WHERE a.`product_name` is not null  AND a.`purchase_date`>=:p1 And a.`purchase_date`<:p2  ";
			 if(StringUtils.isNotBlank(lineType)&&!"total".equals(lineType)){
		        	sql+=" and IFNULL(t.`id`,'unGrouped')='"+lineType+"'  ";
		        }
		        String temp = "";
		        if (StringUtils.isNotEmpty(country) && "eu".equals(country)) {
					temp = " and  a.country in ('de','uk','es','fr','it') ";
		        }else   if (StringUtils.isNotEmpty(country) && "unEn".equals(country)) {
					temp = " and  a.country in ('de','jp','es','fr','it') ";
				}else if (StringUtils.isNotEmpty(country) && "en".equals(country)) {
					temp = " and  a.country in ('uk','com','ca') ";
				}  else if(StringUtils.isNotBlank(country)){
		        	temp = " and  a.country = '"+country+"'";
				}
				sql+= temp+" GROUP BY a.product_name,a.asin,t.name,a.country,BINARY(a.`promotion_ids`) ,a.`discount` order by t.name,sum desc";
		List<Object[]> list = amazonOrdertDao.findBySql(sql, new Parameter(startDate,endDate));
		List<Map<String,Object>> rs = Lists.newArrayList();
		for (Object[] objs : list) {
			
			Map<String,Object> map = Maps.newHashMap();
			map.put("name", objs[0]);
			map.put("asin", objs[1]);
			map.put("promotionIds", objs[2]==null?"闪购":objs[2]);
			map.put("promotionDiscount", objs[3]==null?"":((BigDecimal)objs[3]).floatValue()+"");
			map.put("sum", ((BigDecimal)objs[4]).intValue()+"");
			map.put("sales", objs[5]==null?"":objs[5]);
			String countryStr = objs[6].toString();
			//map.put("country",countryStr.substring(countryStr.lastIndexOf(".")+1));
			map.put("country",countryStr);
			map.put("groupName", objs[7]==null?"":objs[7].toString());
			map.put("code", objs[8]==null?"":objs[8].toString());
			rs.add(map);
		}
		return rs;
	}
	
	public List<Map<String,Object>> countPromotions(Date startDate,Date endDate){
		endDate = DateUtils.addDays(endDate, 1);
		String sql = "SELECT CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) AS NAME,a.asin,a.promotion_ids,a.promotion_discount,SUM(quantity_ordered) as sum,"+
		        //"SUM(a.item_price-a.promotion_discount) as sumPrice,"+
		        " TRUNCATE((CASE WHEN REPLACE(REPLACE(REPLACE(b.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','')='uk' THEN SUM(a.item_price-a.promotion_discount)*"+AmazonProduct2Service.getRateConfig().get("GBP/EUR")+" WHEN REPLACE(REPLACE(REPLACE(b.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','')='ca' THEN SUM(a.item_price-a.promotion_discount)*"
		    				+AmazonProduct2Service.getRateConfig().get("CAD/EUR")+" WHEN REPLACE(REPLACE(REPLACE(b.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','')='jp' THEN SUM(a.item_price-a.promotion_discount)*"+
		    				AmazonProduct2Service.getRateConfig().get("JPY/EUR")+" WHEN REPLACE(REPLACE(REPLACE(b.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','')='com' THEN sum(a.item_price-a.promotion_discount)*"+AmazonProduct2Service.getRateConfig().get("USD/EUR")+" WHEN REPLACE(REPLACE(REPLACE(b.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','')='mx' THEN sum(a.item_price-a.promotion_discount)*"+AmazonProduct2Service.getRateConfig().get("MXN/EUR")+" ELSE SUM(a.item_price-a.promotion_discount) END ),2) sumPrice, "+
		    				
		        " SUBSTRING_INDEX(b.`sales_channel`,'.',-1) as country,t.name groupName " +
				" FROM amazoninfo_orderitem a LEFT JOIN amazoninfo_order b ON a.order_id=b.id "+
				" left join psi_product p on a.`product_name`=concat(p.brand,' ',p.model)   and p.del_flag='0' "+
				" left join sys_dict d on d.value=p.type and d.type='product_type' and d.del_flag='0' "+
				" left join psi_product_type_group g on g.dict_id=d.id "+
				" left join psi_product_type_dict t on g.id=t.id and t.del_flag='0' "+
				" WHERE a.`product_name` is not null  AND b.`purchase_date`>=:p1 And b.`purchase_date`<:p2 AND b.`order_status` in ('Shipped','Pending','Unshipped') "+ 
				" AND ( (a.promotion_ids!='B2B Free Shipping' AND a.promotion_ids not like '%Core Free Shipping%' and a.`promotion_discount`>0 and a.`promotion_ids` IS NOT NULL) or  "+
				" (a.`promotion_ids` IS NULL AND a.`promotion_discount` >0)  OR (a.promotion_ids LIKE '%Core Free Shipping%' AND a.promotion_ids LIKE '%,%' and a.`promotion_discount`>0   ))"+
				"  GROUP BY NAME,a.asin,t.name,a.`promotion_ids` ,a.`promotion_discount`,SUBSTRING_INDEX(b.`sales_channel`,'.',-1) order by t.name,country,sum desc";
		List<Object[]> list = amazonOrdertDao.findBySql(sql, new Parameter(startDate,endDate));
		List<Map<String,Object>> rs = Lists.newArrayList();
		for (Object[] objs : list) {
			Map<String,Object> map = Maps.newHashMap();
			map.put("name", objs[0]);
			map.put("asin", objs[1]);
			map.put("promotionIds",objs[2]==null?"闪购":objs[2]);
			map.put("promotionDiscount", ((BigDecimal)objs[3]).floatValue()+"");
			map.put("sum", ((BigDecimal)objs[4]).intValue()+"");
			map.put("sales", objs[5]);
			map.put("country",objs[6]==null?"":"com".equals(objs[6].toString())?"us":objs[6].toString());
			map.put("groupName", objs[7]==null?"":objs[7].toString());
			rs.add(map);
		}
		return rs;
	}
	public List<Object[]> findPromotionOrder(Date startDate,Date endDate,String country,String promotionIds,String asin,String price){
		endDate = DateUtils.addDays(endDate, 1);
		String sql = "";
        String temp = "";
        if (StringUtils.isNotEmpty(country) && "eu".equals(country)) {
			temp = " and  a.`sales_channel` in ('Amazon.de','Amazon.co.uk','Amazon.es','Amazon.fr','Amazon.it') ";
		}else if (StringUtils.isNotEmpty(country) && "en".equals(country)) {
			temp = " and  a.`sales_channel` in ('Amazon.com','Amazon.co.uk','Amazon.ca') ";
		}else {
        	temp = " and  a.`sales_channel` = '"+"amazon."+("jp,uk".contains(country)?"co.":"")+country+"'";
		}
		if(StringUtils.isNotBlank(promotionIds)){
			if(StringUtils.isNotBlank(price)&&!"0.0".equals(price)){
				sql = "SELECT DATE_FORMAT(a.`purchase_date`,'%Y%m%d') days,COUNT(1),GROUP_CONCAT(a.`amazon_order_id`) FROM amazoninfo_order a , amazoninfo_orderitem b " +
						"WHERE a.`id`=b.`order_id` "+temp+" AND b.`promotion_ids` =:p1 and  a.`purchase_date`>:p2 And a.`purchase_date`<:p3 and b.asin=:p4 and b.promotion_discount = :p5 GROUP BY days  order by a.`purchase_date`";
				return  amazonOrdertDao.findBySql(sql, new Parameter(promotionIds,startDate,endDate,asin,price));
			}else{
				sql = "SELECT DATE_FORMAT(a.`purchase_date`,'%Y%m%d') days,COUNT(1),GROUP_CONCAT(a.`amazon_order_id`) FROM amazoninfo_order a , amazoninfo_orderitem b " +
						"WHERE a.`id`=b.`order_id` "+temp+" AND b.`promotion_ids` =:p1 and  a.`purchase_date`>:p2 And a.`purchase_date`<:p3 and b.asin=:p4 and (b.promotion_discount  is null or b.promotion_discount=0)  GROUP BY days  order by a.`purchase_date`";
				return  amazonOrdertDao.findBySql(sql, new Parameter(promotionIds,startDate,endDate,asin));
			}
			
		}else{
			if(StringUtils.isNotBlank(price)&&!"0.0".equals(price)){
				sql = "SELECT DATE_FORMAT(a.`purchase_date`,'%Y%m%d') days,COUNT(1),GROUP_CONCAT(a.`amazon_order_id`) FROM amazoninfo_order a , amazoninfo_orderitem b " +
						"WHERE a.`id`=b.`order_id` "+temp+" AND b.`promotion_ids` is null and  a.`purchase_date`>:p1 And a.`purchase_date`<:p2 and b.asin=:p3 and b.promotion_discount = :p4 GROUP BY days  order by a.`purchase_date`";
				return  amazonOrdertDao.findBySql(sql, new Parameter(startDate,endDate,asin,price));
			}else{
				sql = "SELECT DATE_FORMAT(a.`purchase_date`,'%Y%m%d') days,COUNT(1),GROUP_CONCAT(a.`amazon_order_id`) FROM amazoninfo_order a , amazoninfo_orderitem b " +
						"WHERE a.`id`=b.`order_id` "+temp+" AND b.`promotion_ids` is null and  a.`purchase_date`>:p1 And a.`purchase_date`<:p2 and b.asin=:p3 and (b.promotion_discount  is null or b.promotion_discount=0) GROUP BY days  order by a.`purchase_date`";
				return  amazonOrdertDao.findBySql(sql, new Parameter(startDate,endDate,asin));
			}
			
		}
		
	}
	
	public Map<String,String> findPromotionReview(List<String> orderIds){
		Map<String,String> rs = Maps.newHashMap();
		if(orderIds!=null&&orderIds.size()>0){
			String sql = "SELECT aa.amazon_order_id,aa.comment_url,bb.`star` FROM (SELECT a.`amazon_order_id`,a.`comment_url` FROM amazoninfo_order a WHERE a.`amazon_order_id` IN (:p1) AND a.`comment_url` IS NOT NULL) aa ,amazoninfo_review_comment bb WHERE  FIND_IN_SET(bb.`review_asin`,REPLACE(aa.comment_url,'/',','))";
			List<Object[]> list = amazonOrdertDao.findBySql(sql, new Parameter(orderIds));
			for (Object[] objs : list) {
				rs.put(objs[0].toString(),objs[2].toString());
			}
		}
		return rs;
	}
	
	public Timestamp getMaxOrderDateByAccount(String account){
		String sql = "";
		List<Object> rs=null;
		if(StringUtils.isBlank(account)){
			sql="select last_update_date from  amazoninfo_order  order by last_update_date desc limit 1";
			rs = amazonOrdertDao.findBySql(sql);
		}else{
			sql="select last_update_date from  amazoninfo_order where account_name =:p1  order by last_update_date desc limit 1";
			rs = amazonOrdertDao.findBySql(sql, new Parameter(account));
		}
		if(rs.size()>0){
			return (Timestamp)rs.get(0);
		}else{
			return null;
		}
	}
	
	public Timestamp getMaxOrderDate(String country){
		String sql = "";
		List<Object> rs=null;
		if(StringUtils.isBlank(country)){
			sql="select last_update_date from  amazoninfo_order  order by last_update_date desc limit 1";
			//sql = "select max(last_update_date) from  amazoninfo_order where order_channel is null";
			rs = amazonOrdertDao.findBySql(sql);
		}else{
			//sql = "select max(last_update_date) from  amazoninfo_order where sales_channel like :p1 and order_channel is null";
			if("jp,uk".contains(country)){
				country="Amazon.co."+country;
			}else if("mx".equals(country)){
				country="Amazon.com."+country;
			}else{
				country="Amazon."+country;
			}
			sql="select last_update_date from  amazoninfo_order where sales_channel =:p1  order by last_update_date desc limit 1";
			
			rs = amazonOrdertDao.findBySql(sql, new Parameter(country));
		}
		if(rs.size()>0){
			return (Timestamp)rs.get(0);
		}else{
			return null;
		}
	}
	
	public Timestamp getMaxInvoiceDate(){
		    Date date = DateUtils.addDays(new Date(),-30);
			String sql = "SELECT MIN(r.last_update_date) FROM amazoninfo_order r  "+
				"	JOIN amazoninfo_order_extract t ON r.`amazon_order_id`=t.`amazon_order_id`   "+
				"	WHERE r.invoice_address IS NULL AND r.sales_channel = 'amazon.de'    "+
				"	AND r.order_status = 'Shipped' AND r.last_update_date >:p1  AND r.order_channel IS NULL AND t.`invoice_flag` LIKE '0%'";
			List<Object> rs = amazonOrdertDao.findBySql(sql, new Parameter(date));
			if(rs.size()>0&&rs.get(0)!=null){
				return (Timestamp)rs.get(0);
			}else{
				return null;
			}
		
	}
	
	public Map<String, Float> findSales(String month,String country) throws ParseException {
		Date monthDate = DateUtils.parseDate(month+"-1",new String[]{"yyyy-MM-dd"});
		Date start = DateUtils.getFirstDayOfMonth(monthDate);
		Date end = DateUtils.addDays(DateUtils.getLastDayOfMonth(monthDate),1);
		List<String> countrys = null;
		if("eu".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.co.uk","Amazon.de","Amazon.fr","Amazon.es","Amazon.it") ;
		}else if("ca".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.ca") ;
		}else if("us".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.com") ;
		}else if("jp".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.co.jp") ;
		}
		String sql = "SELECT c.`country_code`,TRUNCATE(SUM(CASE WHEN a.`sales_channel`='Amazon.co.uk' THEN a.`order_total`*"+AmazonProduct2Service.getRateConfig().get("GBP/EUR")+" ELSE a.`order_total` END),2) AS sales FROM amazoninfo_order a,amazoninfo_address c  " +
				"WHERE  c.`id` = a.`shipping_address` AND a.`purchase_date` >:p1 AND a.`purchase_date` < :p2    AND a.`order_status`  = 'Shipped' AND  a.`order_channel` IS NULL  AND a.`sales_channel` IN :p3 GROUP BY c.`country_code` ORDER BY sales DESC" ;
		List<Object[]> list = amazonOrdertDao.findBySql(sql, new Parameter(start,end,countrys));
		Map<String, Float> rs = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			rs.put(objs[0].toString(), ((BigDecimal)objs[1]).floatValue());
		}
		return rs;
	}
	
	public Map<String,Object[]> getReturnOrder(String month,String country) throws Exception{
		Date monthDate = DateUtils.parseDate(month+"-1",new String[]{"yyyy-MM-dd"});
		Date start = DateUtils.getFirstDayOfMonth(monthDate);
		Date end = DateUtils.getLastDayOfMonth(monthDate);
		List<String> countrys = null;
		List<String> countrys1 = null;
		if("eu".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("uk","de","fr","es","it") ;
			countrys1 = Lists.newArrayList("Amazon.co.uk","Amazon.de","Amazon.fr","Amazon.es","Amazon.it") ;
		}else if("ca".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("ca") ;
			countrys1 = Lists.newArrayList("Amazon.ca") ;
		}else if("us".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("com") ;
			countrys1 = Lists.newArrayList("Amazon.com") ;
		}else if("jp".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("jp") ;
			countrys1 = Lists.newArrayList("Amazon.co.jp") ;
		}
		String sql = "SELECT CONCAT(a.`order_id`,',',a.`sku`)  FROM amazoninfo_return_goods a WHERE a.`return_date` >= :p1 AND a.`country` in :p2 ";
		List<Object> temp = amazonOrdertDao.findBySql(sql, new Parameter(start,countrys));
		Map<String,Object[]> rs = Maps.newHashMap();
		if(temp.size()>0){
			sql ="SELECT CONCAT(c.`amazon_order_id`,',',b.`sellersku`) AS key1," +
						" c.`id`,c.`amazon_order_id`,b.`sellersku`,TRUNCATE(b.`item_price`/b.`quantity_shipped`,2) AS price," +
						" b.`quantity_shipped`,c.`sales_channel` FROM amazoninfo_orderitem b , amazoninfo_order c " +
						" WHERE  b.`order_id` = c.`id`  AND c.`purchase_date` >= :p1 AND c.`purchase_date` <= :p2 AND c.`order_status` IN ('Shipped','UnShipped') AND  c.`sales_channel` IN :p4 AND b.`quantity_shipped`>0 " +
						" HAVING key1 IN (:p3)";
			List<Object[]> list = amazonOrdertDao.findBySql(sql, new Parameter(start,end,temp,countrys1));
			for (Object[] objects : list) {
				rs.put(objects[2].toString(), objects);
			}
		}
		return rs;
	}
	
	public int getReturnGoodsQuantity(String orderId,String sku){
		String sql = "SELECT COUNT(1) FROM amazoninfo_return_goods a WHERE a.`sku` = :p1 AND a.`order_id` = :p2";
		return ((BigInteger)amazonOrdertDao.findBySql(sql, new Parameter(sku,orderId)).get(0)).intValue();
	}
	
	@Transactional(readOnly = false)
	public void updateAllOrderDate(){
		DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sqlString="SELECT id,purchase_date,SUBSTRING_INDEX(`sales_channel`,'.',-1) FROM amazoninfo_order_copy WHERE sales_channel<>'Non-Amazon' AND  `purchase_date`<'2014-01-01' ";
		String updateSql="update amazoninfo_order_copy set purchase_date=:p1 where id=:p2 ";
		List<Object[]> list=amazonOrdertDao.findBySql(sqlString);
		for (Object[] objects : list) {
			int id=Integer.parseInt(objects[0].toString());
			try {
				Date date=sdf.parse(objects[1].toString());
				String country=objects[2].toString();
				if("de,it,es,fr".contains(country)){
					sdf.setTimeZone(AmazonWSConfig.get("de").getTimeZone());
				}else{
					sdf.setTimeZone(AmazonWSConfig.get(country).getTimeZone());
				}
				String time = sdf.format(date);
				sdf.setTimeZone(TimeZone.getDefault());
				Date parseDate=sdf.parse(time);
				amazonOrdertDao.updateBySql(updateSql, new Parameter(parseDate,id));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	//查询最近一次销售时间
	public  Date getMaxTime(String sku,String promId){
		String sql ="SELECT MAX(a.`purchase_date`) FROM amazoninfo_order a ,amazoninfo_orderitem b WHERE a.`id` = b.`order_id` AND b.`sellersku`=:p1 AND b.`promotion_ids` LIKE :p2 AND a.`order_status`<>'Canceled' ";
		List<Object> list =this.amazonOrdertDao.findBySql(sql, new Parameter(sku,"%"+promId+"%"));
		if(list!=null&&list.size()>0&&list.get(0)!=null){
			return (Date)list.get(0);
		}
		return null;
	}
	
	//查询最近一次销售时间
		public  Date getMaxTimeByAsin(String country,String asin,String promId){
			String sql ="SELECT MAX(b.`purchase_date`) FROM amazoninfo_promotions_report b WHERE  b.`asin`=:p1 "+
		   " AND (BINARY(b.`promotion_ids`) = :p2 or BINARY(b.`promotion_ids`) like :p3 or BINARY(b.`promotion_ids`) like :p4  or BINARY(b.`promotion_ids`) like :p5  ) and b.country=:p6  ";
			List<Object> list =this.amazonOrdertDao.findBySql(sql, new Parameter(asin,promId,promId+",%","%,"+promId+",%","%,"+promId,country));
			if(list!=null&&list.size()>0&&list.get(0)!=null){
				return (Date)list.get(0);
			}
			return null;
		}
		
		
		public  Date getMaxTimeByAsin2(String country,String asin,String promId){
		/*	String sql ="SELECT MAX(b.`purchase_date`) FROM amazoninfo_promotions_report b WHERE  b.`asin`=:p1 "+
		   " AND (BINARY(b.`promotion_ids`) = :p2 or BINARY(b.`promotion_ids`) like :p3 or BINARY(b.`promotion_ids`) like :p4  or BINARY(b.`promotion_ids`) like :p5  ) and b.country=:p6  ";
			*/
			String sql ="SELECT MAX(a.`purchase_date`) FROM amazoninfo_order a ,amazoninfo_orderitem b WHERE a.`id` = b.`order_id` AND b.`asin`=:p1 "+
					" AND ((BINARY(b.`promotion_ids`) = :p2 or BINARY(b.`promotion_ids`) like :p3 or BINARY(b.`promotion_ids`) like :p4  or BINARY(b.`promotion_ids`) like :p5  )) and a.sales_channel = :p6  AND a.`order_status`<>'Canceled' ";
					
			List<Object> list =this.amazonOrdertDao.findBySql(sql, new Parameter(asin,promId,promId+",%","%,"+promId+",%","%,"+promId,"amazon."+("jp,uk".contains(country)?"co.":"")+country));
			
			
			if(list!=null&&list.size()>0&&list.get(0)!=null){
				return (Date)list.get(0);
			}
			return null;
		}
	
	//查询该sku 某促销码的销售数量  所有
	public  Integer getCumulativeQuantity(String sku,String promId){
		String sql ="SELECT SUM(b.`quantity_ordered`) FROM amazoninfo_order a ,amazoninfo_orderitem b WHERE a.`id` = b.`order_id` AND b.`sellersku`=:p1 AND b.`promotion_ids` LIKE :p2 AND a.`order_status`<>'Canceled' ";
		List<Object> list =this.amazonOrdertDao.findBySql(sql, new Parameter(sku,"%"+promId+"%"));
		if(list!=null&&list.size()>0&&list.get(0)!=null){
			return Integer.parseInt(list.get(0).toString());
		}
		return null;
	}
	
	//查询该sku 某促销码的销售数量  所有
		public  Integer getCumulativeQuantityByAsin(String country,String asin,String promId){
			String sql ="SELECT SUM(b.`quantity`) FROM amazoninfo_promotions_report b WHERE b.`asin`=:p1 AND (BINARY(b.`promotion_ids`) = :p2 or BINARY(b.`promotion_ids`) like :p3 or BINARY(b.`promotion_ids`) like :p4  or BINARY(b.`promotion_ids`) like :p5  ) AND b.country=:p6 ";
			List<Object> list =this.amazonOrdertDao.findBySql(sql, new Parameter(asin,promId,promId+",%","%,"+promId+",%","%,"+promId,country));
			if(list!=null&&list.size()>0&&list.get(0)!=null){
				return Integer.parseInt(list.get(0).toString());
			}
			return null;
		}
		
		
		public  Integer getHalfHourQuantityByAsin(String country,String asin,String promId,Date maxDate){
			String sql ="SELECT SUM(CASE WHEN b.`purchase_date`>=  DATE_ADD(:p3,INTERVAL -30 MINUTE) AND b.`purchase_date`<=:p3 THEN b.`quantity` ELSE 0 END ) FROM " +
					" amazoninfo_promotions_report b WHERE  b.`asin`=:p1  and (BINARY(b.`promotion_ids`) = :p2 or BINARY(b.`promotion_ids`) like :p4 or BINARY(b.`promotion_ids`) like :p5  or BINARY(b.`promotion_ids`) like :p6  )  AND b.`purchase_date` BETWEEN DATE_ADD(:p3,INTERVAL -60 MINUTE) AND :p3  and b.country=:p7 ";
			List<Object> list =this.amazonOrdertDao.findBySql(sql, new Parameter(asin,promId,maxDate,promId+",%","%,"+promId+",%","%,"+promId,country));
			if(list!=null&&list.size()>0&&list.get(0)!=null){
				return Integer.parseInt(list.get(0).toString());
				//return Integer.parseInt(objs[0]==null?"0":objs[0].toString())>=warningQuantity||Integer.parseInt(objs[1]==null?"0":objs[1].toString())>=warningQuantity||Integer.parseInt(objs[2]==null?"0":objs[2].toString())>=warningQuantity;
			}
			return null;
		}
	  
	//查询该sku 某促销码的销售数量  所有
	public  boolean getHalfHourQuantity(String sku,String promId,Date maxDate,Integer warningQuantity){
		String sql ="SELECT SUM(CASE WHEN a.`purchase_date`>=  DATE_ADD(:p3,INTERVAL -90 MINUTE) AND a.`purchase_date`<= DATE_ADD(:p3,INTERVAL -60 MINUTE) THEN b.`quantity_ordered` ELSE 0 END ) ,SUM(CASE WHEN a.`purchase_date`>=  DATE_ADD(:p3,INTERVAL -60 MINUTE)" +
				" AND a.`purchase_date`<= DATE_ADD(:p3,INTERVAL -30 MINUTE) THEN b.`quantity_ordered` ELSE 0 END ),SUM(CASE WHEN a.`purchase_date`>=  DATE_ADD(:p3,INTERVAL -30 MINUTE)AND a.`purchase_date`<=:p3 THEN b.`quantity_ordered` ELSE 0 END )FROM amazoninfo_order a" +
				" ,amazoninfo_orderitem b WHERE a.`id` = b.`order_id` AND b.`sellersku`=:p1 AND b.`promotion_ids` LIKE :p2  AND a.`purchase_date` BETWEEN DATE_ADD(:p3,INTERVAL -90 MINUTE) AND :p3 AND a.`order_status`<>'Canceled'";
		List<Object[]> list =this.amazonOrdertDao.findBySql(sql, new Parameter(sku,"%"+promId+"%",maxDate));
		if(list!=null&&list.size()>0){
			Object[] objs = list.get(0);
			return Integer.parseInt(objs[0].toString())>=warningQuantity||Integer.parseInt(objs[1].toString())>=warningQuantity||Integer.parseInt(objs[2].toString())>=warningQuantity;
		}
		return false;
	}
	
	//查询该sku 某促销码的销售数量  所有
		public  boolean getHalfHourQuantityByAsin(String country,String asin,String promId,Date maxDate,Integer warningQuantity){
			String sql ="SELECT SUM(CASE WHEN b.`purchase_date`>=  DATE_ADD(:p3,INTERVAL -90 MINUTE) AND b.`purchase_date`<= DATE_ADD(:p3,INTERVAL -60 MINUTE) THEN b.`quantity` ELSE 0 END ) ,SUM(CASE WHEN b.`purchase_date`>=  DATE_ADD(:p3,INTERVAL -60 MINUTE)" +
					" AND b.`purchase_date`<= DATE_ADD(:p3,INTERVAL -30 MINUTE) THEN b.`quantity` ELSE 0 END ),SUM(CASE WHEN b.`purchase_date`>=  DATE_ADD(:p3,INTERVAL -30 MINUTE) AND b.`purchase_date`<=:p3 THEN b.`quantity` ELSE 0 END ) FROM " +
					" amazoninfo_promotions_report b WHERE  b.`asin`=:p1  and (BINARY(b.`promotion_ids`) = :p2 or BINARY(b.`promotion_ids`) like :p4 or BINARY(b.`promotion_ids`) like :p5  or BINARY(b.`promotion_ids`) like :p6  )  AND b.`purchase_date` BETWEEN DATE_ADD(:p3,INTERVAL -90 MINUTE) AND :p3  and b.country=:p7 ";
			List<Object[]> list =this.amazonOrdertDao.findBySql(sql, new Parameter(asin,promId,maxDate,promId+",%","%,"+promId+",%","%,"+promId,country));
			if(list!=null&&list.size()>0){
				Object[] objs = list.get(0);
				return Integer.parseInt(objs[0]==null?"0":objs[0].toString())>=warningQuantity||Integer.parseInt(objs[1]==null?"0":objs[1].toString())>=warningQuantity||Integer.parseInt(objs[2]==null?"0":objs[2].toString())>=warningQuantity;
			}
			return false;
		}
		
		public  boolean getFreeOverHourQuantityByAsin(String country,String asin,String promId){
			String sql ="SELECT SUM(b.`quantity_ordered`) FROM amazoninfo_order a,amazoninfo_orderitem b WHERE a.`id` = b.`order_id`  " +
					" AND b.`asin`=:p1 AND b.`promotion_ids`=:p2  and REPLACE(REPLACE(REPLACE(a.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','')=:p3 " +
					" AND a.`purchase_date`>=DATE_ADD(NOW(),INTERVAL -24 HOUR) AND a.`order_status`<>'Canceled'";
			List<Object> list =this.amazonOrdertDao.findBySql(sql, new Parameter(asin,promId,country));
			if(list!=null&&list.size()>0&&list.get(0)!=null){
				return Integer.parseInt(list.get(0).toString())>5?true:false;
			}
			return false;
		}
		
		public  boolean getFreeOverQuantity(String country,String promId){
			
				String sql1 ="SELECT MAX(b.`purchase_date`) FROM amazoninfo_promotions_report b WHERE  "+
			    "  (BINARY(b.`promotion_ids`) = :p1 or BINARY(b.`promotion_ids`) like :p2 or BINARY(b.`promotion_ids`) like :p3  or BINARY(b.`promotion_ids`) like :p4  ) and b.country=:p5  ";
				List<Object> list1 =this.amazonOrdertDao.findBySql(sql1, new Parameter(promId,promId+",%","%,"+promId+",%","%,"+promId,country));
				if(list1!=null&&list1.size()>0&&list1.get(0)!=null){
					Date maxDate=(Date)list1.get(0);
					String sql ="SELECT SUM(b.`quantity`) FROM amazoninfo_promotions_report b WHERE  " +
							"   (BINARY(b.`promotion_ids`) = :p1 or BINARY(b.`promotion_ids`) like :p2 or BINARY(b.`promotion_ids`) like :p3  or BINARY(b.`promotion_ids`) like :p4  )   and b.country=:p5 " +
							" AND b.`purchase_date`>=DATE_ADD(:p6,INTERVAL -24 HOUR) ";
					List<Object> list =this.amazonOrdertDao.findBySql(sql, new Parameter(promId,promId+",%","%,"+promId+",%","%,"+promId,country,maxDate));
					if(list!=null&&list.size()>0&&list.get(0)!=null){
						return Integer.parseInt(list.get(0).toString())>30?true:false;
					}
				}
			   return false;
		}
		
		public  Integer getEntireCatlogueQuantity(String country,String promId){
			String sql ="SELECT SUM(b.`quantity_ordered`) FROM amazoninfo_order a,amazoninfo_orderitem b WHERE a.`id` = b.`order_id`  " +
		            " and (BINARY(b.`promotion_ids`) = :p1 or BINARY(b.`promotion_ids`) like :p2 or BINARY(b.`promotion_ids`) like :p3  or BINARY(b.`promotion_ids`) like :p4  ) "+
					" and REPLACE(REPLACE(REPLACE(a.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','')=:p5 " +
					" AND a.`purchase_date`>=DATE_ADD(NOW(),INTERVAL -24 HOUR) AND a.`order_status`<>'Canceled'";
			List<Object> list =this.amazonOrdertDao.findBySql(sql, new Parameter(promId,promId+",%","%,"+promId+",%","%,"+promId,country));
			if(list!=null&&list.size()>0&&list.get(0)!=null){
				return Integer.parseInt(list.get(0).toString());
			}
		    return 0;
	    }
		
		
		public  boolean getFreeOverHourQuantity(String country,String promId){
			String sql ="SELECT SUM(b.`quantity_ordered`) FROM amazoninfo_order a,amazoninfo_orderitem b WHERE a.`id` = b.`order_id`  " +
					" AND b.`promotion_ids`= :p1 and REPLACE(REPLACE(REPLACE(a.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','')=:p2 " +
					" AND a.`purchase_date`>=DATE_ADD(NOW(),INTERVAL -30 minute) AND a.`order_status`<>'Canceled'";
			List<Object> list =this.amazonOrdertDao.findBySql(sql, new Parameter(promId,country));
			if(list!=null&&list.size()>0&&list.get(0)!=null){
				return Integer.parseInt(list.get(0).toString())>5?true:false;
			}
			return false;
		}
	
	//查询 某促销码的销售数量  
	public  String getEvaluateQuantity(String promId,Date renDate){
		String sql ="SELECT a.`amazon_order_id`  FROM amazoninfo_order a ,amazoninfo_orderitem b WHERE a.`id` = b.`order_id`  AND b.`promotion_ids` LIKE :p1 AND a.`purchase_date`>=:p2 AND a.`order_status`<>'Canceled' ";
		List<String> list =this.amazonOrdertDao.findBySql(sql, new Parameter("%"+promId+"%",renDate));
		if(list!=null&&list.size()>0){
			String orderIds = "";
			StringBuffer buf= new StringBuffer();
			for(String orderId:list){
				buf.append(orderId+",");
			}
			orderIds= buf.toString();
			if(StringUtils.isNotEmpty(orderIds)){
				return orderIds.substring(0, orderIds.length()-1);
			}
		}
		return null;
	}
	
	public Map<String,List<Object[]>> getMaxOrder(){
		String sql="SELECT SUBSTRING_INDEX(`sales_channel`,'.',-1),amazon_order_id,CONCAT(product_name,CASE WHEN color!='' THEN CONCAT ('_',color) ELSE '' END),color,quantity_ordered,r.`order_status` FROM  amazoninfo_order r JOIN amazoninfo_orderitem t ON r.id=t.`order_id` "+
				"WHERE t.quantity_ordered>=10 AND TO_DAYS(NOW( )) - TO_DAYS(r.`purchase_date`) <= 1 AND r.`order_status`!='Canceled' ORDER BY quantity_ordered DESC ";
		List<Object[]> list=amazonOrdertDao.findBySql(sql);
		Map<String,List<Object[]>> map=Maps.newLinkedHashMap();
		for (Object[] obj : list) {
			List<Object[]> objList=map.get(obj[0].toString());
			if(objList==null){
				objList=Lists.newArrayList();
				map.put(obj[0].toString(),objList);
			}
			objList.add(obj);
		}
		return map;
	}
	
	
	//调整亚马逊数据
	@Transactional(readOnly = false)
	public void adjustEmailDiscount() throws Exception{
		//1571079  SELECT b.id,a.`create_date` FROM amazoninfo_order AS a ,amazoninfo_orderitem AS b WHERE a.`id`=b.`order_id` AND a.`create_date`<'2015-11-06 11:30:00'  ORDER BY b.`id` DESC
		String sql="UPDATE amazoninfo_orderitem AS a SET a.`shipping_discount`=NULL WHERE a.`order_item_id` NOT IN :p1 AND a.`shipping_discount`IS NOT NULL AND a.`id`<1571079 ";
		this.amazonOrdertDao.updateBySql(sql, new Parameter(this.resolveExcelFile()));
	}

	
	public Set<Long> resolveExcelFile() throws Exception{
	 	Set<Long> set = Sets.newHashSet();
		String fileName = "/opt/apache-tomcat-7.0.53/temp/111.xlsx";
		Workbook workBook = WorkbookFactory.create(new FileInputStream(fileName));
		Sheet sheet = workBook.getSheetAt(0);
		sheet.setForceFormulaRecalculation(true);
		int rows = sheet.getPhysicalNumberOfRows();
		if(rows <= 0){
			throw new RuntimeException("Excel file no data，Operation has been canceled!");
		}
		// 循环行Row
		for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
			Row row = sheet.getRow(rowNum);
			if (row == null) {
				continue;
			}
			Cell cell = row.getCell(0);
			if (cell == null) {
				continue;
			}
			BigDecimal db = new BigDecimal(cell.getNumericCellValue());
			set.add(Long.valueOf(db.toPlainString()));
		}
	return set;
   }
	
	
  public Map<String,Object[]> findDealFee(String country,Set<String> skuSet){
	  Map<String,Object[]> map=Maps.newHashMap();
	  String temp="(CASE WHEN (a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es') then "+AmazonProduct2Service.getRateConfig().get("USD/EUR")+" when a.country='uk' then "+AmazonProduct2Service.getRateConfig().get("USD/GBP")+" when a.country='jp' then "+AmazonProduct2Service.getRateConfig().get("USD/JPY")+"  when a.country='ca' then "+AmazonProduct2Service.getRateConfig().get("USD/CAD")+"  when a.country='mx' then "+AmazonProduct2Service.getRateConfig().get("USD/MXN")+" else 1 end)";
	  String sql="SELECT a.`sku`,a.product_name,a.`cost`*"+temp+",a.`fba`*"+temp+",a.`commission_pcent`,a.`tariff_pcent` FROM amazoninfo_product_price a "+
	  " WHERE a.`date` = DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND a.`country` =:p1 AND a.`sku` in :p2 ";
	  List<Object[]> rs=amazonOrdertDao.findBySql(sql,new Parameter(country,skuSet));
	  if(rs!=null&&rs.size()>0){
		  for (Object[] obj: rs) {
			  map.put(obj[0].toString(), obj);
		  }
	  }
	  return map;
  }
  
   public List<Object[]> getSettlementByOrderId(String amazonOrderId,String country){
		   Float vat=0f;
		   if("uk,it,es,fr,de,com,ca,jp,mx".contains(country)){
				String temp = country.toUpperCase();
				if("UK".equals(temp)){
					temp = "GB";
				}
				if("COM".equals(temp)){
					temp = "US";
				}
				CountryCode vatCode = CountryCode.valueOf(temp);
				if(vatCode!=null){
					vat = vatCode.getVat()/100f;
				}
			}
		   String sql="SELECT TRUNCATE(SUM(IFNULL(t.principal,0)+IFNULL(t.shipping,0)+IFNULL(t.`promotion`,0)),2) totalPrice,sum(case when r.type='order' then IFNULL(t.`quantity`,0) else 0 end),SUM(IFNULL(t.`cross_border_fulfillment_fee`,0)) fee1,SUM(IFNULL(t.`fba_per_unit_fulfillment_fee`,0)) fee2,SUM(IFNULL(t.`fba_weight_based_fee`,0)) fee3, "+
			          " SUM(IFNULL(t.`commission`,0)+ IFNULL(t.`refund_commission`,0)) fee4,CONCAT(p.`product_name`,CASE WHEN p.`color`!='' THEN CONCAT ('_',p.`color`) ELSE '' END) NAME,  "+
				   
		              " SUM(IFNULL(t.`cod`,0) + IFNULL(t.`gift_wrap`,0) + IFNULL(t.`goodwill`,0)+ "+
					  " IFNULL(t.`cross_border_fulfillment_fee`,0) + IFNULL(t.`shipping_chargeback`,0) + IFNULL(t.`giftwrap_chargeback`,0) + "+
					  " IFNULL(t.`restocking_fee`,0)  + IFNULL(t.`cod_fee`,0) + IFNULL(t.`other_fee`,0) + IFNULL(t.`shipping_hb`,0)+ "+
					  " IFNULL(t.`shipment_fee`,0) + IFNULL(t.`fba_per_order_fulfillment_fee`,0) ) AS order_other_fee,TRUNCATE(SUM(IFNULL(t.principal,0)+IFNULL(t.shipping,0)+IFNULL(t.shipping_chargeback,0)+IFNULL(t.`promotion`,0))/:p3,2) AS sales_no_tax, "+
					  " SUM(IFNULL(t.`fba_per_unit_fulfillment_fee`,0) + IFNULL(t.`fba_weight_based_fee`,0) + IFNULL(t.`commission`,0)) AS amazon_fee, "+
					  " sum(case when IFNULL(t.principal,0)>0 then  IFNULL(t.principal,0) else 0 end),sum(case when IFNULL(t.principal,0)<0 then  IFNULL(t.principal,0) else 0 end), "+
					  " sum(case when IFNULL(t.principal,0)>0 then  (IFNULL(t.principal,0)+IFNULL(t.shipping,0)+IFNULL(t.`promotion`,0)) else 0 end),sum(case when IFNULL(t.principal,0)<0 then  (IFNULL(t.`commission`,0)+ IFNULL(t.`refund_commission`,0)) else 0 end),sum(case when IFNULL(t.principal,0)>0 then  IFNULL(t.`commission`,0) else 0 end) "+
			          " FROM amazoninfo_financial r JOIN amazoninfo_financial_item t ON r.id=t.`order_id` "+
			          " JOIN psi_sku p ON t.sku=p.sku AND p.country=:p1 and p.del_flag='0' "+
			          " WHERE r.`amazon_order_id` = :p2   GROUP BY name HAVING fee1+fee2+fee3+fee4<>0";
		   List<Object[]> list=amazonOrdertDao.findBySql(sql,new Parameter(country,amazonOrderId,1+vat));
		   return list;
   }
   
   public List<Object[]> getRefundOrders(String month , String country){
	   List<String> countrys = Lists.newArrayList(country);
	   if("eu".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("uk","de","fr","es","it") ;
		}
	   String sql="SELECT CONCAT('g_',a.`id`), a.`amazon_order_id`,DATE_FORMAT(a.`posted_date`,'%Y-%m-%d'),SUM(b.`principal`),a.country FROM settlementreport_order a ,settlementreport_item b WHERE a.`type` = 'Refund' AND a.`id` = b.`order_id` AND b.`principal`<0 AND DATE_FORMAT(a.`posted_date`,'%Y-%m')=:p1 and  a.`country` in :p2 GROUP BY  a.`amazon_order_id";
	   List<Object[]> list=amazonOrdertDao.findBySql(sql,new Parameter(month,countrys));
	   return list;
   } 
   
   public Map<String,List<Object[]>> getFreePromotions(){
	   Map<String,List<Object[]>> map=Maps.newHashMap();
	   String sql="SELECT REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.',''),t.`promotion_ids`,r.`purchase_date`,r.`amazon_order_id`,t.`item_price`,m.`type`, "+
          " CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END) "+
          "  FROM amazoninfo_order r JOIN amazoninfo_orderitem t ON r.id=t.`order_id` "+
          "  LEFT JOIN custom_event_manager m ON m.`invoice_number`=r.`amazon_order_id` "+
          "  WHERE (t.`promotion_ids` LIKE 'Free-%' or t.`promotion_ids` LIKE 'R-%') AND  r.`order_status`!='Canceled' AND DATE_FORMAT(r.`purchase_date`,'%Y-%m-%d')=DATE_FORMAT(DATE_SUB(NOW(),INTERVAL 1 DAY),'%Y-%m-%d')  ";
	   List<Object[]> list=amazonOrdertDao.findBySql(sql);
	   for (Object[] obj: list) {
		   List<Object[]> temp=map.get(obj[0].toString());
		   if(temp==null){
			   temp=Lists.newArrayList();
			   map.put(obj[0].toString(), temp);
		   }
		   temp.add(obj);
	   }
	   return map;
   }
   
   @Transactional(readOnly = false)
   public void setRateSn(String rateSn,String orderId){
	   String sql="UPDATE amazoninfo_order_extract SET rate_sn=:p1 WHERE amazon_order_id=:p2";
	   amazonOrdertDao.updateBySql(sql,new Parameter(rateSn,orderId));
   }
   
   public Map<String,String> getTaxIdByOrderId(){
	   Map<String,String> map=Maps.newHashMap();
	   String sql="SELECT r.`invoice_number`,tax_id FROM custom_event_manager r WHERE r.`tax_id` IS NOT NULL AND r.`tax_id`!=''";
	   List<Object[]> list=amazonOrdertDao.findBySql(sql);
	   for (Object[] obj: list) {
		  map.put(obj[0].toString(),obj[1].toString());
	   }
	   return map;
   }
  
   
   public Map<String,Map<String,Object[]>> findCustomId(){
	   Map<String,Map<String,Object[]>> map=Maps.newHashMap();
	   String sql="SELECT t.`promotion_ids`,REPLACE(REPLACE(REPLACE(o.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') country,o.`custom_id`,o.`amazon_order_id`, "+
			" GROUP_CONCAT(DISTINCT CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END)) NAME "+
			" FROM amazoninfo_order o JOIN amazoninfo_orderitem t ON t.`order_id`=o.id "+
			" WHERE t.`promotion_ids` LIKE '%F-SysAuto-%'  AND o.`purchase_date`>=:p1 "+
			" GROUP BY  t.`promotion_ids`,o.`custom_id`,country,o.`amazon_order_id` ";
					List<Object[]> list=amazonOrdertDao.findBySql(sql, new Parameter(DateUtils.addDays(new Date(),-20)));
		for (Object[] obj: list) {
			Map<String,Object[]> temp=map.get(obj[1].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[1].toString(),temp);
			}
			temp.put(obj[0].toString(), obj);
		}
		return map;
	}
   
   @Transactional(readOnly = false)
   public void saveReturnsAndBadReview(){
	  String sql1="INSERT INTO `amazoninfo_return_comment` (country,purchase_date,product_name,color,return_amount)  "+
	    " SELECT g.`country` country,DATE_FORMAT(o.purchase_date,'%Y-%m-%d') purchase_date, s.product_name product_name,s.`color` color, SUM(g.quantity) return_amount "+
		" FROM amazoninfo_return_goods g JOIN amazoninfo_order o ON o.`amazon_order_id` = g.`order_id`  "+
		"JOIN (SELECT DISTINCT product_name,color,country,sku FROM psi_sku WHERE `product_name` NOT LIKE '%other%' AND `product_name` NOT LIKE '%Old%' AND del_flag='0' ) s ON s.`sku`=g.`sku` AND  g.`country`=s.`country`  "+
		" WHERE  o.`order_status`='Shipped' AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -6 MONTH) "+
		" AND s.`product_name` NOT LIKE '%other%' AND s.`product_name` NOT LIKE '%Old%' "+
		" GROUP BY g.`country`,s.`product_name` ,s.`color`,DATE_FORMAT(o.purchase_date,'%Y-%m-%d') "+
		" ON DUPLICATE KEY UPDATE `return_amount` = VALUES(return_amount) ";
	  amazonOrdertDao.updateBySql(sql1, null);
	  
	  String sql2="INSERT INTO `amazoninfo_return_comment` (country,purchase_date,product_name,color,comment_amount) "+
			" SELECT c.`country` country,DATE_FORMAT(o.purchase_date,'%Y-%m-%d') purchase_date,t.`product_name` product_name,t.`color` color,COUNT(*) comment_amount "+
			" FROM (SELECT a.ID,a.`remarks`,a.country,SUBSTRING_INDEX(SUBSTRING_INDEX(a.`invoice_number`,',',b.help_topic_id+1),',',-1) order_id "+
			" FROM custom_event_manager a JOIN mysql.help_topic b "+
			" ON b.help_topic_id < (LENGTH(a.`invoice_number`) - LENGTH(REPLACE(a.`invoice_number`,',',''))+1) "+
			" WHERE a.type='1' AND a.`invoice_number`!='not find oderID' AND a.`invoice_number` IS NOT NULL) c  "+
			" JOIN  (SELECT DISTINCT product_name,color,country,ASIN FROM psi_sku WHERE `product_name` NOT LIKE '%other%' AND `product_name` NOT LIKE '%Old%' AND del_flag='0' ) t ON t.`asin`=c.`remarks` AND t.`country`=c.country "+
			" JOIN amazoninfo_order o ON o.`amazon_order_id` = c.`order_id` "+
			" JOIN amazoninfo_orderitem r ON o.id=r.order_id AND r.asin=c.remarks "+
			" WHERE  o.`order_status`='Shipped' AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -6 MONTH) "+
			" GROUP BY c.`country`,t.`product_name` ,t.`color`,o.`amazon_order_id`,DATE_FORMAT(o.purchase_date,'%Y-%m-%d') "+
			" ON DUPLICATE KEY UPDATE `comment_amount` =VALUES(comment_amount) ";
	  amazonOrdertDao.updateBySql(sql2, null);	  
	  
	  String sql3="INSERT INTO `amazoninfo_return_comment` (country,purchase_date,product_name,color,order_amount) "+
	     " SELECT SUBSTRING_INDEX(o.`sales_channel`,'.',-1) country,DATE_FORMAT(o.purchase_date,'%Y-%m-%d') purchase_date,t.`product_name` product_name,t.`color` color, COUNT(*) order_amount "+
		 " FROM amazoninfo_order o JOIN  amazoninfo_orderitem t ON o.id=t.`order_id` "+
		 " WHERE o.`order_status`='Shipped' AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -6 MONTH) AND t.`product_name` NOT LIKE '%other%' AND t.`product_name` NOT LIKE '%Old%' "+
		 " GROUP BY DATE_FORMAT(o.purchase_date,'%Y-%m-%d'),t.`product_name`,t.`color`,SUBSTRING_INDEX(o.`sales_channel`,'.',-1)"+
		 " ON DUPLICATE KEY UPDATE `order_amount` = VALUES(order_amount) ";
	  amazonOrdertDao.updateBySql(sql3, null);	    
   }
   
   
   
   @Transactional(readOnly = false)
   public void savePromotionsOrder(){
	   try{
		   String tempSql="DELETE FROM  amazoninfo_promotions_report WHERE  purchase_date>=:p1 or discount=0";
		   amazonOrdertDao.updateBySql(tempSql,new Parameter(DateUtils.addDays(new Date(),-15)));
	   }catch(Exception e){LOGGER.error("删除折扣为0异常", e);}
	   String sql="INSERT INTO `amazoninfo_promotions_report` (country,purchase_date,product_name,ASIN,promotion_ids,discount,quantity,sales,account_name) "+
		" SELECT REPLACE(REPLACE(REPLACE(b.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') country,STR_TO_DATE(DATE_FORMAT(b.`purchase_date`,'%Y%m%d'),'%Y%m%d') date,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) product_name,a.asin,(CASE WHEN (a.`promotion_ids` IS NULL  or  (a.`promotion_ids` is not null and a.`promotion_ids` not like '%,%' and a.`promotion_ids` like '%Core Free Shipping%' and a.`promotion_ids` like 'Free Delivery%'  AND a.`promotion_discount` >0 ) ) THEN '闪购' ELSE a.promotion_ids END) promotionIds,IFNULL(a.promotion_discount,0) discount, "+ 
		" SUM(CASE WHEN b.`order_status`='Canceled'  THEN 0 ELSE a.`quantity_ordered` END) quantity, "+
		" SUM(CASE WHEN b.`order_status`='Canceled'  THEN 0 ELSE (a.item_price-a.promotion_discount) END) sales,b.account_name "+
		" FROM amazoninfo_order b JOIN amazoninfo_orderitem a ON a.order_id=b.id   "+
		" WHERE b.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -3 MONTH)  AND b.`order_status` IN ('Shipped','Pending','Unshipped','Canceled')  "+ 
		" AND  a.`product_name` IS NOT NULL  "+
		" AND ((a.`promotion_ids` IS NOT NULL AND a.promotion_ids!='B2B Free Shipping'   AND ((a.promotion_ids LIKE '%Core Free Shipping%'  AND a.promotion_ids  LIKE '%,%') OR a.promotion_ids NOT LIKE '%Core Free Shipping%' ) ) OR   "+
		" (a.`promotion_ids` IS NULL AND a.`promotion_discount` >0) or  (a.`promotion_ids` is not null and a.`promotion_ids` not like '%,%' and a.`promotion_ids` like '%Core Free Shipping%' and a.`promotion_ids` like 'Free Delivery%'  AND a.`promotion_discount` >0 )  )  "+
		" GROUP BY product_name,a.asin,country,promotionIds,discount,date,account_name "+
		" ON DUPLICATE KEY UPDATE `quantity` = VALUES(quantity),`sales` = VALUES(sales)  ";
	   amazonOrdertDao.updateBySql(sql, null);
   }
   
   @Transactional(readOnly = false)
   public void updatePromotionsCode(Map<String,Map<String,String>> map){
	   if(map!=null&&map.size()>0){
		   String sql="UPDATE amazoninfo_promotions_report  t "+
			       " SET t.code=:p1 WHERE  t.country=:p2 "+
				   " AND (BINARY(t.`promotion_ids`)=:p3 or BINARY(t.`promotion_ids`) like :p4 or BINARY(t.`promotion_ids`) like :p5 or BINARY(t.`promotion_ids`) like :p6) ";
		   for (Map.Entry<String,Map<String,String>> entry : map.entrySet()) { 
		        String country=entry.getKey();
				Map<String,String> countryMap=entry.getValue();
				 for (Map.Entry<String,String> entryRs : countryMap.entrySet()) { 
				    String trackId=entryRs.getKey();
					amazonOrdertDao.updateBySql(sql, new Parameter(entryRs.getValue(),country,trackId,"%,"+trackId,trackId+",%","%,"+trackId+",%"));
				}
			}
		}
    }
   
   
   public Map<String,Map<String,Map<String,Integer>>> findPromotionsNum(){
	   Map<String,Map<String,Map<String,Integer>>> map=Maps.newHashMap();
	  String sql=" SELECT country,ASIN,promotion_ids,SUM(quantity) FROM amazoninfo_promotions_report WHERE purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -1 DAY) "+
			"	AND promotion_ids LIKE 'F-' and promotion_ids is not null and quantity>0 GROUP BY country,ASIN,promotion_ids ";
	  List<Object[]> list=amazonOrdertDao.findBySql(sql);
	  if(list!=null&&list.size()>0){
		  for (Object[] obj: list) {
			  Map<String,Map<String,Integer>> temp=map.get(obj[0].toString());
			  if(temp==null){
				  temp=Maps.newHashMap();
				  map.put(obj[0].toString(), temp);
			  }
			  Map<String,Integer> countryMap=temp.get(obj[1].toString());
			  if(countryMap==null){
				  countryMap=Maps.newHashMap();
				  temp.put(obj[1].toString(), countryMap);
			  }
			  countryMap.put(obj[2].toString(),Integer.parseInt(obj[3].toString()));
		  } 
	  }
	  return map;
   }
   
   public Map<String,String> findPromotionsRate(){
	   Map<String,String> map=Maps.newHashMap();
	   String sql=" SELECT REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.',''),SUM(CASE WHEN ((t.`promotion_ids` IS NOT NULL AND t.promotion_ids!='B2B Free Shipping'   AND ((t.promotion_ids LIKE '%Core Free Shipping%'  AND t.promotion_ids  LIKE '%,%') OR t.promotion_ids NOT LIKE '%Core Free Shipping%' ) )  ) THEN t.`quantity_ordered` ELSE 0 END) proQuantity,SUM(t.`quantity_ordered`) quantity, "+
		" ROUND(SUM(CASE WHEN ((t.`promotion_ids` IS NOT NULL AND t.promotion_ids!='B2B Free Shipping'   AND ((t.promotion_ids LIKE '%Core Free Shipping%'  AND t.promotion_ids  LIKE '%,%') OR t.promotion_ids NOT LIKE '%Core Free Shipping%' ) )  ) THEN t.`quantity_ordered` ELSE 0 END)*100/SUM(t.`quantity_ordered`),0) rate FROM amazoninfo_order r JOIN amazoninfo_orderitem t ON r.id=t.`order_id`  "+
		" WHERE r.`purchase_date`>=:p1 AND r.`order_status`!='Canceled' GROUP BY r.`sales_channel` HAVING rate>10 ";
	   List<Object[]> list=amazonOrdertDao.findBySql(sql,new Parameter(DateUtils.addHours(new Date(),-24)));
	   if(list!=null&&list.size()>0){
		   for (Object[] obj:list) {
			   String country=obj[0].toString();
			   Integer rate=Integer.parseInt(obj[3].toString());
			   Integer proQuantity=Integer.parseInt(obj[1].toString());
			   Integer quantity=Integer.parseInt(obj[2].toString());
			   map.put(country,("com".equals(country)?"US":country.toUpperCase())+",折扣数:"+proQuantity+",总数:"+quantity+",累计24小时折扣率:"+rate+"%");
		   }
	   }
	   return map;
   }
   
   public Map<String,Map<String,Integer>> findRankPromotions(){
	   Map<String,Map<String,Integer>> map=Maps.newLinkedHashMap();
	   String sql="SELECT REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') country,t.`promotion_ids`,SUM(t.`quantity_ordered`) quantity "+
		"	FROM amazoninfo_order r JOIN amazoninfo_orderitem t ON r.id=t.`order_id`  "+
		"	WHERE r.`purchase_date`>=:p1 AND r.`order_status`!='Canceled' "+
		"	AND (t.`promotion_ids` IS NOT NULL AND t.promotion_ids!='B2B Free Shipping'   AND ((t.promotion_ids LIKE '%Core Free Shipping%'  AND t.promotion_ids  LIKE '%,%') OR t.promotion_ids NOT LIKE '%Core Free Shipping%' ) ) "+
		"	GROUP BY r.`sales_channel` ,t.`promotion_ids` "+
		"	ORDER BY country DESC,quantity DESC ";
	   List<Object[]> list=amazonOrdertDao.findBySql(sql,new Parameter(DateUtils.addHours(new Date(),-24)));
	   for (Object[] obj:list) {
		   Map<String,Integer> temp=map.get(obj[0].toString());
		   if(temp==null){
			   temp=Maps.newLinkedHashMap();
			   map.put(obj[0].toString(),temp);
		   }
		   temp.put(obj[1].toString(), Integer.parseInt(obj[1].toString()));
	   }
	   return map;
   }
   
   public Map<String,Integer>  findPromotions(){
	   Map<String,Integer>  map=Maps.newHashMap();
	   String sql="SELECT p.`country`,SUM(p.`quantity`) FROM amazoninfo_promotions_report p WHERE p.`purchase_date`=:p1 "+
         " AND (p.`promotion_ids`!='闪购' AND p.promotion_ids!='B2B Free Shipping'   AND ((p.promotion_ids LIKE '%Core Free Shipping%'  AND p.promotion_ids  LIKE '%,%') OR p.promotion_ids NOT LIKE '%Core Free Shipping%' ) ) "+
         " GROUP BY p.country ";
	   List<Object[]> list=amazonOrdertDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(new Date(), -1))));
	   for (Object[] obj: list) {
		  map.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
	   }
	   return map;
   }
   
   public Map<String,Map<String,Integer>> findPromotionsId(){
	   Map<String,Map<String,Integer>> map=Maps.newLinkedHashMap();
	   String sql="SELECT p.`country`,p.`promotion_ids`,SUM(p.`quantity`) quantity FROM amazoninfo_promotions_report p WHERE p.`purchase_date`=:p1  "+
        " AND (p.`promotion_ids`!='闪购' AND p.promotion_ids!='B2B Free Shipping'   AND ((p.promotion_ids LIKE '%Core Free Shipping%'  AND p.promotion_ids  LIKE '%,%') OR p.promotion_ids NOT LIKE '%Core Free Shipping%' ) ) "+
        " GROUP BY p.country ,p.`promotion_ids` ORDER BY country DESC,quantity DESC ";
	   List<Object[]> list=amazonOrdertDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(new Date(), -1))));
	   for (Object[] obj:list) {
		   Map<String,Integer> temp=map.get(obj[0].toString());
		   if(temp==null){
			   temp=Maps.newLinkedHashMap();
			   map.put(obj[0].toString(),temp);
		   }
		   temp.put(obj[1].toString(), Integer.parseInt(obj[2].toString()));
	   }
	   return map;
   }
   
   public List<String> findAllOrderId(String amzEmail){
	   String sql="SELECT r.`amazon_order_id` FROM amazoninfo_order  r WHERE r.`buyer_email`=:p1";
	   return amazonOrdertDao.findBySql(sql,new Parameter(amzEmail));
   }
   
   
   public Map<String,List<Object[]>> findMaxOrderInfo(Date start,Date end,String country,Set<String> nameSet){
	   String sql="SELECT REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') country,r.`amazon_order_id`,r.`purchase_date`,r.`buyer_email`,r.`custom_id`, "+
		"	CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END) NAME,t.`quantity_ordered`, "+
		"	d.`address_line1`,d.`address_line2`,d.`address_line3`,d.`city`,d.`state_or_region`,d.`postal_code`,d.`country_code`,d.`phone` "+
		"	FROM amazoninfo_order r "+
		"	JOIN amazoninfo_orderitem t ON r.`id`=t.`order_id` "+
		"	JOIN amazoninfo_address d ON r.`shipping_address`=d.`id` "+
		"	WHERE r.`purchase_date`>=:p1 AND r.`purchase_date`<=:p2 AND r.`order_status`!='Canceled' AND t.`quantity_ordered`>=10 and t.`product_name` is not null ";
	   Map<String,List<Object[]>> map=Maps.newLinkedHashMap();
	   List<Object> list=Lists.newArrayList();
	   list.add(start);
	   list.add(end);
	   int i=3;
	   if(StringUtils.isNotBlank(country)&&!"total".equals(country)){
		 
		   if("eu".equals(country)){
			   sql+=" and REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') in  ('de','fr','it','es','uk') ";
		   }else{
			   list.add(country);
			   sql+=" and REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','')=:p"+(i++)+" ";
		   }
		   
	   }
	   if(nameSet!=null&&nameSet.size()>0){
		   list.add(nameSet);
		   sql+=" and CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END)  in :p"+(i++)+" ";
	   }
	   sql+=" order by country ";
	   List<Object[]> objList=amazonOrdertDao.findBySql(sql, new Parameter(list.toArray(new Object[list.size()])));
	   if(objList!=null&&objList.size()>0){
		   for (Object[] obj: objList) {
			   String orderId=obj[1].toString();
			   String name=obj[5].toString();
			   List<Object[]> temp=map.get(orderId+","+name);
			   if(temp==null){
				   temp=Lists.newArrayList();
				   map.put(orderId+","+name,temp);
			   }
			   temp.add(obj);
		   }
	   }
	   return map;
   }
   
   

	public Map<String,List<Object[]>> findAllPlatform(AmazonOrder amazonOrder){
		Map<String,List<Object[]>> map=Maps.newLinkedHashMap();
		String sql2="SELECT DISTINCT t.email,d.`phone`,ifnull(d.`country_code`,''),d.name,d.postal_code,d.city_name,d.state_or_province FROM ebay_order r "+
				" JOIN ebay_address d ON r.`shipping_address`=d.id  "+
				" JOIN ebay_orderitem t ON r.id=t.`order_id` "+
				" WHERE r.`created_time`>=:p1 AND r.`created_time`<=:p2 AND t.email!='Invalid Request' AND d.`phone`!='Invalid Request' AND d.`phone`!='' AND d.`phone` IS NOT NULL ";
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			sql2+=" and r.`country`=:p3 ";
		}
		sql2+=" order by r.`country`";
		List<Object[]> ebayList =Lists.newArrayList();
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			ebayList =amazonOrdertDao.findBySql(sql2, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1),amazonOrder.getSalesChannel()));
		}else{
			ebayList =amazonOrdertDao.findBySql(sql2, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1)));
		}
		
		if(ebayList!=null&&ebayList.size()>0){
			map.put("3",ebayList);
		}
		
		String sql1="SELECT DISTINCT r.`buyer_email`,s.`phone`,ifnull(s.country_code,''),s.name,s.postal_code,s.city,s.state_or_region,r.order_channel FROM amazoninfo_unline_order r JOIN amazoninfo_unline_address s ON r.`shipping_address`=s.`id` "+
				" WHERE r.`purchase_date`>=:p1 AND r.`purchase_date`<:p2 and s.`phone` IS NOT NULL and r.sales_channel not in ('21','130') AND s.`phone`!='' and r.`buyer_email` not like '%marketplace.amazon%' ";
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			sql1+=" and (case when r.sales_channel='19' then 'de' else 'com' end)=:p3 ";
		}
		sql1+="order by s.country_code";
		List<Object[]> offlineList =Lists.newArrayList();
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			offlineList =amazonOrdertDao.findBySql(sql1, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1),amazonOrder.getSalesChannel()));
		}else{
			offlineList =amazonOrdertDao.findBySql(sql1, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1)));
		}
		if(offlineList!=null&&offlineList.size()>0){
			for (Object[] obj: offlineList) {
				String createUser=obj[7].toString();
				String type="";
				if("管理员".equals(createUser)){
					type="0";
				}else if("CHECK24".equals(createUser)){
					type="1";
				}else{
					type="2";
				}
				List<Object[]>  list=map.get(type);
				if(list==null){
					list=Lists.newArrayList();
					map.put(type, list);
				}
				list.add(obj);
			}
		}
		return map;
	}
	
	public Map<String,List<Object[]>> findAllPlatform2(AmazonOrder amazonOrder){
		Map<String,List<Object[]>> map=Maps.newLinkedHashMap();
		
		String sql3="SELECT DISTINCT buyer_email,d.name,d.postal_code,d.city,REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') country "+
				" from amazoninfo_order r  "+
				" JOIN amazoninfo_address d ON r.`shipping_address`=d.`id` "+
				" join amazoninfo_orderitem t on t.order_id=r.id "+
				" WHERE r.`purchase_date`>=:p1 AND r.`purchase_date`<=:p2  AND e.`email` IS NOT NULL  ";
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			sql3+=" and REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','')=:p3 ";
		}
		sql3+=" order by country  ";
		List<Object[]> amazonList =Lists.newArrayList();
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			amazonList=amazonOrdertDao.findBySql(sql3, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1),amazonOrder.getSalesChannel()));
		}else{
			amazonList=amazonOrdertDao.findBySql(sql3, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1)));
		}
		if(amazonList!=null&&amazonList.size()>0){
			map.put("4", amazonList);
		}
	
		String sql2="SELECT DISTINCT t.email,d.name,d.postal_code,d.city_name,r.`country` FROM ebay_order r "+
				" JOIN ebay_address d ON r.`shipping_address`=d.id  "+
				" JOIN ebay_orderitem t ON r.id=t.`order_id` "+
				" JOIN psi_sku p on p.sku=t.sku and p.country=r.country and p.del_flag='0' "+
				" WHERE r.`created_time`>=:p1 AND r.`created_time`<=:p2 AND t.email!='Invalid Request'  ";
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			sql2+=" and r.`country`=:p3 ";
		}
		sql2+=" order by r.`country`";
		List<Object[]> ebayList =Lists.newArrayList();
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			ebayList =amazonOrdertDao.findBySql(sql2, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1),amazonOrder.getSalesChannel()));
		}else{
			ebayList =amazonOrdertDao.findBySql(sql2, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1)));
		}
		
		if(ebayList!=null&&ebayList.size()>0){
			map.put("3",ebayList);
		}
		return map;
	}
	
	public Map<String,List<Object[]>> findAllPlatform2(AmazonOrder amazonOrder,String name){
		Map<String,List<Object[]>> map=Maps.newLinkedHashMap();
		
		String sql3="SELECT DISTINCT buyer_email,d.name,d.postal_code,d.city,REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') country "+
				" from amazoninfo_order r  "+
				" JOIN amazoninfo_address d ON r.`shipping_address`=d.`id` "+
				" join amazoninfo_orderitem t on t.order_id=r.id "+
				" WHERE r.`purchase_date`>=:p1 AND r.`purchase_date`<=:p2 and t.product_name=:p3 AND buyer_email IS NOT NULL  ";
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			sql3+=" and REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','')=:p4 ";
		}
		sql3+=" order by country  ";
		List<Object[]> amazonList =Lists.newArrayList();
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			amazonList=amazonOrdertDao.findBySql(sql3, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1),name,amazonOrder.getSalesChannel()));
		}else{
			amazonList=amazonOrdertDao.findBySql(sql3, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1),name));
		}
		if(amazonList!=null&&amazonList.size()>0){
			map.put("4", amazonList);
		}
	
		String sql2="SELECT DISTINCT t.email,d.name,d.postal_code,d.city_name,r.`country` FROM ebay_order r "+
				" JOIN ebay_address d ON r.`shipping_address`=d.id  "+
				" JOIN ebay_orderitem t ON r.id=t.`order_id` "+
				" JOIN psi_sku p on p.sku=t.sku and p.country=r.country and p.del_flag='0' and p.product_name =:p3 "+
				" WHERE r.`created_time`>=:p1 AND r.`created_time`<=:p2 AND t.email!='Invalid Request'  ";
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			sql2+=" and r.`country`=:p4 ";
		}
		sql2+=" order by r.`country`";
		List<Object[]> ebayList =Lists.newArrayList();
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			ebayList =amazonOrdertDao.findBySql(sql2, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1),name,amazonOrder.getSalesChannel()));
		}else{
			ebayList =amazonOrdertDao.findBySql(sql2, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1),name));
		}
		
		if(ebayList!=null&&ebayList.size()>0){
			map.put("3",ebayList);
		}
		return map;
	}
	
	
	public Map<String,List<Object[]>> findAmazonPlatform(AmazonOrder amazonOrder){
		Map<String,List<Object[]>> map=Maps.newLinkedHashMap();
		// t.email,d.`phone`,ifnull(d.`country_code`,''),d.name,d.postal_code,d.city_name,d.state_or_province
		String sql3="SELECT DISTINCT buyer_email,d.`phone`,ifnull(d.country_code,''),d.name,d.postal_code,d.city,d.state_or_region "+
				" from amazoninfo_order r  "+
				" JOIN amazoninfo_address d ON r.`shipping_address`=d.`id` "+
				" WHERE r.`purchase_date`>=:p1 AND r.`purchase_date`<=:p2 ";
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			sql3+=" and REPLACE(REPLACE(REPLACE(r.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','')=:p3 ";
		}
		sql3+=" order by d.country_code  ";
		List<Object[]> amazonList =Lists.newArrayList();
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			amazonList=amazonOrdertDao.findBySql(sql3, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1),amazonOrder.getSalesChannel()));
		}else{
			amazonList=amazonOrdertDao.findBySql(sql3, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1)));
		}
		if(amazonList!=null&&amazonList.size()>0){
			map.put("4", amazonList);
		}
		return map;
	}
	
	public Map<String,List<Object[]>> findEbayPlatform(AmazonOrder amazonOrder){
		Map<String,List<Object[]>> map=Maps.newLinkedHashMap();
		//Firstname, Lastname, Postalcode, Town, Country 
		String sql2="SELECT DISTINCT t.email,d.`phone`,ifnull(d.`country_code`,''),d.name,d.postal_code,d.city_name,d.state_or_province FROM ebay_order r "+
				" JOIN ebay_address d ON r.`shipping_address`=d.id  "+
				" JOIN ebay_orderitem t ON r.id=t.`order_id` "+
				" WHERE r.`created_time`>=:p1 AND r.`created_time`<=:p2 AND t.email!='Invalid Request'  ";
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			sql2+=" and r.`country`=:p3 ";
		}
		sql2+=" order by r.`country`";
		
		
		List<Object[]> amazonList =Lists.newArrayList();
		if(StringUtils.isNotBlank(amazonOrder.getSalesChannel())){
			amazonList=amazonOrdertDao.findBySql(sql2, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1),amazonOrder.getSalesChannel()));
		}else{
			amazonList=amazonOrdertDao.findBySql(sql2, new Parameter(amazonOrder.getPurchaseDate(),DateUtils.addDays(amazonOrder.getLastUpdateDate(),1)));
		}
		if(amazonList!=null&&amazonList.size()>0){
			map.put("4", amazonList);
		}
		return map;
	}
	
	public Map<String,Map<String, Map<String,SaleReport>>> getPromotions(SaleReport saleReport){
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		String typeSql = "'%Y%m%d'";
		if("2".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addWeeks(today, -19);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addWeeks(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getMonday(start);
			end = DateUtils.getSunday(end);
			typeSql="'%x%v'";
		}else if("3".equals(saleReport.getSearchType())){
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -18);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addMonths(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
			start = DateUtils.getFirstDayOfMonth(start);
			end = DateUtils.getLastDayOfMonth(end);
			typeSql="'%Y%m'";
		}else{
			if(start==null){
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				start = DateUtils.addMonths(today, -1);
				end = today;
				saleReport.setStart(start);
				saleReport.setEnd(end);
			}else{
				Date end1 = DateUtils.addDays(start, 3);
				if(end.before(end1)){
					end = end1;
					saleReport.setEnd(end1);
				}
			}
		}
		
		String sql="SELECT a.`product_name`,country,DATE_FORMAT(purchase_date,"+typeSql+")  dates,SUM(quantity), " +
				  " TRUNCATE((CASE WHEN a.country='uk' THEN SUM(a.sales)*"+AmazonProduct2Service.getRateConfig().get("GBP/EUR")+" WHEN a.country='ca' THEN SUM(a.sales)*"
					+AmazonProduct2Service.getRateConfig().get("CAD/EUR")+" WHEN a.country='jp' THEN SUM(a.sales)*"+
					AmazonProduct2Service.getRateConfig().get("JPY/EUR")+" WHEN a.country='com' THEN sum(a.sales)*"+AmazonProduct2Service.getRateConfig().get("USD/EUR")+" WHEN a.country='mx' THEN sum(a.sales)*"+AmazonProduct2Service.getRateConfig().get("MXN/EUR")+" ELSE SUM(a.sales) END ),2), "+
										
					" TRUNCATE((CASE WHEN a.country='uk' THEN SUM(discount*quantity)*"+AmazonProduct2Service.getRateConfig().get("GBP/EUR")+" WHEN a.country='ca' THEN SUM(discount*quantity)*"
					+AmazonProduct2Service.getRateConfig().get("CAD/EUR")+" WHEN a.country='jp' THEN SUM(discount*quantity)*"+
					AmazonProduct2Service.getRateConfig().get("JPY/EUR")+" WHEN a.country='com' THEN sum(discount*quantity)*"+AmazonProduct2Service.getRateConfig().get("USD/EUR")+" WHEN a.country='mx' THEN sum(discount*quantity)*"+AmazonProduct2Service.getRateConfig().get("MXN/EUR")+" ELSE SUM(discount*quantity) END ),2) "+
					
					" FROM amazoninfo_promotions_report a where purchase_date>=:p1 and purchase_date<=:p2 ";
					if(StringUtils.isNotBlank(saleReport.getCountry())){
						sql+=" and country=:p3 ";
					}
					sql+=" and promotion_ids not like '%F-SysAuto-%' and (promotion_ids like 'F-%' or promotion_ids like '%,F-%') GROUP BY a.`product_name`,dates,country ";
		List<Object[]> list = null;
		if(StringUtils.isNotBlank(saleReport.getCountry())){
			list = amazonOrdertDao.findBySql(sql, new Parameter(start,end,saleReport.getCountry()));
		}else{
			list = amazonOrdertDao.findBySql(sql, new Parameter(start,end));
		}
		Map<String,Map<String, Map<String,SaleReport>>> rs = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			String country = objs[1].toString(); 
			String productName = (objs[0]==null)?"":objs[0].toString(); 
			String date = objs[2].toString(); 
			Integer quantity = ((BigDecimal)(objs[3]==null?new BigDecimal(0):objs[3])).intValue();
			Float sales = ((BigDecimal)(objs[4]==null?new BigDecimal(0):objs[4])).floatValue();
			Float promotionSales = ((BigDecimal)(objs[5]==null?new BigDecimal(0):objs[5])).floatValue();
		
			if("2".equals(saleReport.getSearchType())){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			
			Map<String, Map<String,SaleReport>> datas = rs.get(country);
			if(datas==null){
				datas = Maps.newLinkedHashMap();
				rs.put(country, datas);
			}
			Map<String, SaleReport> sale=datas.get(date);
			if(sale==null){
				sale=Maps.newLinkedHashMap();
				datas.put(date, sale);
			}
			SaleReport report=new SaleReport();
			report.setSalesVolume(quantity);
			report.setSales(sales);
			report.setSureSales(promotionSales);
			sale.put(productName, report);
		}
		return rs;
	}
	
	
	public Integer getQuantityByTime(String country,Date start,Date end,String asin){
		String sql="SELECT SUM(t.`quantity_ordered`) FROM amazoninfo_order r "+
		   " JOIN amazoninfo_orderitem t ON r.id=t.`order_id`  "+
		   " WHERE r.`sales_channel`=:p1 AND r.`purchase_date`>=:p2 AND r.`purchase_date`<=:p3 AND r.`order_status`!='Canceled' AND t.`asin`=:p4 ";
		String suffix="";
		if("jp.uk".contains(country)){
			suffix="Amazon.co."+country;
		}else if("mx".equals(country)){
			suffix="Amazon.com."+country;
		}else{
			suffix="Amazon."+country;
		}
		List<BigDecimal> list=amazonOrdertDao.findBySql(sql,new Parameter(suffix,start,end,asin));
		if(list!=null&&list.size()>0&&list.get(0)!=null){
			return list.get(0).intValue();
		}
		return 0;
	}
	
	
	public List<Object[]> findRateSn(){
		String sql="SELECT o.amazon_order_id,r.`tax_id` FROM custom_event_manager r  "+
		          " JOIN  amazoninfo_order_extract o ON r.`invoice_number`=o.`amazon_order_id` AND (o.`rate_sn`='' OR o.`rate_sn` IS NULL)   "+
		          "  WHERE r.type='4' AND r.`create_date`>=:p1  AND r.`tax_id` IS NOT NULL AND r.`tax_id`!=''  ";
		return amazonOrdertDao.findBySql(sql,new Parameter(DateUtils.addMonths(new Date(),-2)));
	}
	
	
	public AmazonOrderExtract  findRateSnByOrderId(String orderId){
		DetachedCriteria dc = amazonOrderExtractDao.createDetachedCriteria();
		dc.add(Restrictions.eq("amazonOrderId",orderId));
		List<AmazonOrderExtract> orderList=amazonOrderExtractDao.find(dc);
		if(orderList!=null&&orderList.size()>0){
			return orderList.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void updateRateSn(String rateSn,String orderId){
		String sql="update amazoninfo_order_extract set rate_sn=:p1 where amazon_order_id=:p2";
		amazonOrderExtractDao.updateBySql(sql, new Parameter(rateSn,orderId));
	}
	
	@Transactional(readOnly = false)
	public void updateInvoiceFlag(Set<String> orderId){
		String sql="update amazoninfo_order_extract set invoice_flag='1' where amazon_order_id in :p1";
		amazonOrderExtractDao.updateBySql(sql, new Parameter(orderId));
	}
	
	//SELECT amazon_order_id,rate_sn,comment_url,custom_id,invoice_flag
	

	public String getCustomIdByOrderId(String amazonOrderId) {
		String sql = "SELECT t.`custom_id` FROM `amazoninfo_order_extract` t WHERE t.`amazon_order_id`=:p1";
		List<Object> list = amazonOrdertDao.findBySql(sql,new Parameter(amazonOrderId));
		if (list != null && list.size() > 0 && list.get(0)!=null) {
			return list.get(0).toString();
		}
		return null;
	}

	//统计5小时内更新的订单
		@Transactional(readOnly = false)
		public void updateOrderExtract() {
			String sql = "INSERT INTO `amazoninfo_order_extract`(amazon_order_id,country,purchase_date,last_update_date,order_total,account_name) "+
					" SELECT t.amazon_order_id,SUBSTRING_INDEX(t.sales_channel,'.',-1),purchase_date,last_update_date,order_total,t.account_name  "+
					" FROM `amazoninfo_order` t WHERE t.last_update_date>=(SELECT MAX(e.last_update_date) FROM `amazoninfo_order_extract` e) AND t.order_status='Shipped' "+
					" ON DUPLICATE KEY UPDATE `last_update_date` = VALUES(last_update_date),`order_total` = VALUES(order_total)";
			amazonOrdertDao.updateBySql(sql, null);
			//按更新时间统计出现遗漏的情况,补漏最近一个月的
			sql = "INSERT INTO `amazoninfo_order_extract`(amazon_order_id,country,purchase_date,last_update_date,order_total,account_name) "+
					" SELECT a.amazon_order_id,SUBSTRING_INDEX(a.sales_channel,'.',-1),a.purchase_date,a.last_update_date,a.order_total ,a.account_name "+
					" FROM amazoninfo_order a LEFT JOIN amazoninfo_order_extract b ON a.`amazon_order_id` = b.`amazon_order_id`  "+
					" WHERE a.`order_status` = 'shipped' AND b.`amazon_order_id` IS NULL AND a.`last_update_date`>=:p1";
			amazonOrdertDao.updateBySql(sql, new Parameter(DateUtils.addMonths(new Date(), -1)));
			
			
		}

	
	public String getRateSn(String amazonOrderId) {
		String sql = "SELECT t.`rate_sn` FROM `amazoninfo_order_extract` t WHERE t.`amazon_order_id`=:p1";
		List<Object> list = amazonOrdertDao.findBySql(sql,new Parameter(amazonOrderId));
		if (list != null && list.size() > 0 && list.get(0)!=null) {
			return list.get(0).toString();
		}
		return null;
	}
	
	public Map<String,AmazonOrder> findOrders(Set<String> orderId){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.in("amazonOrderId", orderId));
		List<AmazonOrder> rs = amazonOrdertDao.find(dc);
		Map<String,AmazonOrder> map = Maps.newHashMap();
		for (AmazonOrder order : rs) {
			map.put(order.getAmazonOrderId(), order);
		}
		return map;
	}
	
	
	@Transactional(readOnly = false)
	public String createFlowNo(String type,Integer seqLength,String suffix){
		synchronized (AmazonOrderService.class){
			return this.genDao.genInvoiceSequence(type,seqLength,suffix);
		}
	}
	
	@Transactional(readOnly = false)
	public void  updateInvoiceNoById(String invoiceNo,String orderId) {
		amazonOrdertDao.updateBySql("update amazoninfo_order_extract set invoice_no=:p1,print_date=NOW() WHERE amazon_order_id=:p2", new Parameter(invoiceNo,orderId));
	}
	
	public Map<String,Set<String>> findNoInvoiceOrder(){
		String sql="SELECT country,amazon_order_id FROM amazoninfo_order_extract WHERE last_update_date>='2017-10-24' AND invoice_no IS NULL";
		List<Object[]> list=amazonOrdertDao.findBySql(sql);
		Map<String,Set<String>> map=Maps.newHashMap();
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				String country=obj[0].toString();
				String orderId=obj[1].toString();
				Set<String> temp=map.get(country);
				if(temp==null){
					temp=Sets.newHashSet();
					map.put(country, temp);
				}
				temp.add(orderId);
			}
		}
		return map;
	}
	
	public List<String> findServerByEmail(String email){
		String sql="SELECT distinct g.customer_email FROM amazoninfo_order r "+
		 " JOIN amazoninfo_account_config g ON r.`account_name`=g.`account_name`  "+
		 " WHERE r.`buyer_email` = :p1 and g.customer_email is not null order by r.id desc ";
		return amazonOrdertDao.findBySql(sql,new Parameter(email));
	}
}
