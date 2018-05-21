package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
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
import com.springrain.erp.modules.amazoninfo.dao.customer.AmazonCustomerDao;
import com.springrain.erp.modules.amazoninfo.dao.order.AmazonOrderDao;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonBuyComment;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomFilter;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomer;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonReviewComment;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;

/**
 * 亚马逊产品Service
 * @author tim
 * @version 2014-06-04
 */
@Component
@Transactional(readOnly = true)
public class AmazonCustomerService extends BaseService {

	@Autowired
	private AmazonCustomerDao customerDao;
	
	@Autowired
	private AmazonOrderDao amazonOrdertDao;
	
	public AmazonCustomer get(String id) {
		return customerDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public long  countCustomersData(Date date,int page){
		DetachedCriteria dc = amazonOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.eq("orderStatus", "shipped"));
		dc.add(Restrictions.isNotNull("buyerEmail"));
		dc.addOrder(Order.asc("lastUpdateDate"));
		dc.add(Restrictions.gt("lastUpdateDate", date));
		Page<AmazonOrder> pageOrder=amazonOrdertDao.find(new Page<AmazonOrder>(page,100), dc);
	    for (AmazonOrder order : pageOrder.getList()) {
	    	countCustomerData(order);
		}
	    return pageOrder.getCount();
	}
	
	
	
	@Transactional(readOnly = false)
	public void  countCustomerData(AmazonOrder order){
		Date today = new Date();
		String orderId = order.getAmazonOrderId();
    	AmazonCustomer customer = null;
		if(StringUtils.isNotBlank(order.getBuyerEmail())){
			 customer = getByEg(order.getBuyerEmail());
		}
		List<AmazonBuyComment> list = null;
		if(customer==null){
			customer = new AmazonCustomer("", order.getBuyerName(), order.getBuyerEmail(), order.getNumberOfItemsShipped(), order.getPurchaseDate());	
			list = Lists.newArrayList();
			customer.setBuyComments(list);
		}else{
			if(customer.getLastBuyDate().before(order.getLastUpdateDate())){
				customer.setLastBuyDate(order.getLastUpdateDate());
				customer.setBuyTimes(1+customer.getBuyTimes());
				customer.setBuyQuantity(order.getNumberOfItemsShipped()+customer.getBuyQuantity());
				list = customer.getBuyComments();
			}else{
				return;
			}
		}
		for (AmazonOrderItem orderItem : order.getItems()) {
			list.add(new AmazonBuyComment(today, order.getPurchaseDate(), "1", orderId, orderItem.getAsin(), orderItem.getSellersku(), orderItem.getProductName()+(StringUtils.isEmpty(orderItem.getColor())?"":"_"+orderItem.getColor()), orderItem.getQuantityOrdered(), orderItem.getId(),null,customer));
		}
		save(customer);
	}
	
	public AmazonCustomer getByEg(String id) {
		AmazonCustomer rs =  customerDao.get(id);
		if(rs!=null){
			Hibernate.initialize(rs.getBuyComments());
		}
		return rs;
	}
	
	public Timestamp getMaxBuyDate() {
		List<Object> rs=null;
		String sql = "SELECT MAX(a.`last_buy_date`) FROM amazoninfo_customer a ";
		rs = customerDao.findBySql(sql);
		if(rs.size()>0){
			return (Timestamp)rs.get(0);
		}else{
			return null;
		}
	}
	
	
	@Transactional(readOnly = false)
	public  void save(AmazonCustomer amazonCustomer) {
		customerDao.save(amazonCustomer);
	}
	
	@Transactional(readOnly = false)
	public  void save(List<AmazonCustomer> amazonCustomers) {
		customerDao.save(amazonCustomers);
	}
	
	@Transactional(readOnly = false)
	public void saveEmail(AmazonCustomer amazonCustomer){
		customerDao.save(amazonCustomer);
		updateEmail3(amazonCustomer.getEmail(),amazonCustomer.getAmzEmail());
	}
	
	@Transactional(readOnly = false)
	public  void save(AmazonReviewComment reviewComment) {
		String sql = "INSERT INTO amazoninfo_review_comment(review_asin,create_date,ASIN,country,review_date,star,SUBJECT,customer_id) values(:p1,:p2,:p3,:p4,:p5,:p6,:p7,:p8)";
		customerDao.updateBySql(sql, new Parameter(reviewComment.getReviewAsin(),new Date(),reviewComment.getAsin(),reviewComment.getCountry(),reviewComment.getReviewDate(),reviewComment.getStar(),reviewComment.getSubject(),reviewComment.getCustomer().getCustomerId()));
	}
	
	public Map<String,List<Object[]>> getBlackCustomers() {
		
		String sql = "SELECT DISTINCT a.`customer_id`,a.`country`,a.`buy_times`,a.`last_buy_date`,a.`amz_email`,a.`return_quantity`, a.`buy_quantity`, TRUNCATE((a.`return_quantity`*100/a.`buy_quantity`),0) AS retrunG , GROUP_CONCAT(DISTINCT b.`product_name` ) FROM amazoninfo_customer a,amazoninfo_buy_comment b WHERE" 

					 +"  a.`customer_id` = b.`customer_id` AND  a.`return_quantity`/a.`buy_quantity` > 0.5  AND a.`buy_times` > 1 AND a.`last_buy_date`> DATE_ADD(CURDATE(), INTERVAL -3 MONTH) GROUP BY a.`customer_id`"

					 +"  ORDER BY a.`country`,retrunG DESC";
		
		List<Object[]> list =  customerDao.findBySql(sql);
		Map<String,List<Object[]>>  rs = Maps.newHashMap();
		for (Object[] objects : list) {
			String county = objects[1].toString();
			List<Object[]> data = rs.get(county);
			if(data==null){
				data = Lists.newArrayList();
				rs.put(county, data);
			}
			data.add(objects);
		}
		return rs;
	}
	
    public List<Object[]> countCustomers() {
    	//String sql = "SELECT  a.`buy_times`,COUNT(1),TRUNCATE(AVG(TIMESTAMPDIFF(DAY,a.`first_buy_date`,a.`last_buy_date`)/a.`buy_times`),0),a.`customer_id`  FROM amazoninfo_customer a  WHERE  a.`last_buy_date`> DATE_ADD(CURDATE(), INTERVAL -3 MONTH) GROUP BY a.`buy_times`";			
    	String sql = "SELECT  a.`buy_times`,COUNT(1),TRUNCATE(AVG(a.`days`/a.`buy_times`),0),a.`customer_id`  FROM amazoninfo_customer a  WHERE  a.`last_buy_date`> DATE_ADD(CURDATE(), INTERVAL -3 MONTH) GROUP BY a.`buy_times`";			
		return customerDao.findBySql(sql);
	}
    
    public List<Object[]> findCustomers1() {
    	String sql = "SELECT a.`country`,a.`custom_id` FROM custom_event_manager a WHERE a.`custom_id` LIKE '%account\\.%' AND a.`type` = '1'";			
		return customerDao.findBySql(sql);
	}
    
    public List<Object[]> findCustomers2() {
    	String sql = "SELECT b.`country`,b.`customer_id` FROM amazoninfo_review_comment b WHERE  b.`customer_id` LIKE '%account\\.%'";			
		return customerDao.findBySql(sql);
	}
    
   
	@Transactional(readOnly = false)
	public void updateCid(String cid,String newCid) {
		String sql = "UPDATE amazoninfo_review_comment SET customer_id = :p1 WHERE customer_id = :p2";
		customerDao.updateBySql(sql, new Parameter(newCid,cid));
		
		sql = "UPDATE custom_event_manager SET custom_id =:p1 WHERE custom_id = :p2";
		customerDao.updateBySql(sql, new Parameter(newCid,cid));
	}
    
	@Transactional(readOnly = false)
	public void sycnCid() {
		String sql = "	UPDATE  amazoninfo_order_extract a,amazoninfo_order b ,amazoninfo_customer c SET c.`customer_id` = a.`custom_id` WHERE a.`amazon_order_id` = b.`amazon_order_id` AND b.`buyer_email` = c.`amz_email` AND c.`customer_id` ='' AND a.`custom_id` !=''";
		customerDao.updateBySql(sql, null);
		
		sql = "DELETE bbb FROM (SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(aa.ids,',',b.help_topic_id+1),',',-1) AS id FROM (SELECT SUBSTRING_INDEX(GROUP_CONCAT(a.`id` ORDER BY a.`create_date` DESC),',',(1-COUNT(1))) AS ids FROM `amazoninfo_buy_comment` a WHERE a.`create_date`>'2018-2-1' AND a.`type` = '1' GROUP BY a.`order_id`,a.`sku`,a.`type` HAVING COUNT(1)>1)aa "+
			  "	JOIN mysql.help_topic b  ON b.help_topic_id < (LENGTH(aa.ids) - LENGTH(REPLACE(aa.ids,',',''))+1)) aaa  ,amazoninfo_buy_comment bbb  WHERE aaa.id = bbb.`id`   ";
		customerDao.updateBySql(sql, null);
	}
	
	
	
    public Map<String,List<Object[]>> countCustomersByCountry() {
    	//String sql = "SELECT  a.`buy_times`,COUNT(1),TRUNCATE(AVG(TIMESTAMPDIFF(DAY,a.`first_buy_date`,a.`last_buy_date`)/a.`buy_times`),0),a.`customer_id`,a.`country`  FROM amazoninfo_customer a  WHERE  a.`last_buy_date`> DATE_ADD(CURDATE(), INTERVAL -3 MONTH) GROUP BY country,a.`buy_times`";		
    	String sql = "SELECT  a.`buy_times`,COUNT(1),TRUNCATE(AVG(a.`days`/a.`buy_times`),0),a.`customer_id`,a.`country`  FROM amazoninfo_customer a  WHERE  a.`last_buy_date`> DATE_ADD(CURDATE(), INTERVAL -3 MONTH) GROUP BY country,a.`buy_times`";	
    	List<Object[]> list = customerDao.findBySql(sql);
    	Map<String,List<Object[]>>  rs = Maps.newHashMap();
		for (Object[] objects : list) {
			if (objects[4] == null) {
				continue;
			}
			String county = objects[4].toString();
			List<Object[]> data = rs.get(county);
			if(data==null){
				data = Lists.newArrayList();
				rs.put(county, data);
			}
			data.add(objects);
		}
		return rs;
	}
    
    public boolean reviewIsExist (String customerId,String reviewAsin,int star){
    	String sql = "SELECT COUNT(1) FROM amazoninfo_review_comment a WHERE a.`customer_id` = :p1 AND a.`review_asin` =:p2 AND a.`star` = :p3";
    	List<Object> rs = customerDao.findBySql(sql,new Parameter(customerId,reviewAsin,star));
    	return ((BigInteger)rs.get(0)).intValue()>0;
    }
    
    
	
    public List<Object[]> query(boolean byExport, AmazonCustomFilter customFilter){
    	String search = "";
    	String name = customFilter.getName();
    	if(StringUtils.isNotEmpty(name)){
    		search +=" and a.`name` LIKE '%"+name+"%'";
    	}
    	String email = customFilter.getEmail();
    	if(StringUtils.isNotEmpty(email)){
    		search +=" and (a.`email` LIKE '%"+email+"%' or a.`amz_email` LIKE '%"+email+"%')";
    	}
    	if (customFilter.getCustomer() != null) {
        	String customerId = customFilter.getCustomer().getCustomerId();
        	if(StringUtils.isNotEmpty(customerId)){
        		search +=" and a.`customer_id` LIKE '%"+customerId+"%'";
        	}
		}
    	String country = customFilter.getCountry();
    	if(StringUtils.isNotEmpty(country)){
    		search +=" and a.`country`='"+country+"'";
    	}
    	String buyTimes = customFilter.getBuyTimes();
    	if(StringUtils.isNotEmpty(buyTimes)){
    		if("1".equals(buyTimes)){
    			search +=" and a.`buy_times`=1";
    		}else{
    			search +=" and a.`buy_times`>1";
    		}
    	}
    	String returnQ = customFilter.getReturnFlag();
    	if(StringUtils.isNotEmpty(returnQ)){
    		search +=" and a.`return_quantity` "+("1".equals(returnQ)?">":"=")+" 0";
    	}
    	String bigOrder = customFilter.getBigOrder();
    	if (StringUtils.isNotEmpty(bigOrder)) {
    		if("1".equals(bigOrder)){
    			search +=" and b.`quantity`>=10";
    		}else{
    			search +=" and b.`quantity`<10";
    		}
		}
    	String search1 = "";
    	
    	String productName1 = customFilter.getPn1();
    	String productName2 = customFilter.getPn2();
    	String productName3 = customFilter.getPn3();
    	String and = customFilter.getPnAnd();
    	if(StringUtils.isNotEmpty(productName1)||StringUtils.isNotEmpty(productName2)||StringUtils.isNotEmpty(productName3)){
    		search1 += " and (";
			String flag = "0";
			if(StringUtils.isNotEmpty(productName1)){
				search1 += "GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+productName1+"%'";
				flag = "1";
			}
			if(StringUtils.isNotEmpty(productName2)){
				if("0".equals(flag)){
					search1 += "GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+productName2+"%'";
					flag = "1";
				}else{
					search1 += (("1".equals(and)?"and":"or")+" GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+productName2+"%'");
				}
			}
			if(StringUtils.isNotEmpty(productName3)){
				if("0".equals(flag)){
					search1 += "GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+productName3+"%'";
				}else{
					search1 +=  (("1".equals(and)?"and":"or")+" GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+productName3+"%'");
				}
			}
    		search1 += ")";
    	}
    	
    	String pn1 = customFilter.getPn11();
    	String pn2 = customFilter.getPn22();
    	String pn3 = customFilter.getPn33();
    	
    	and = customFilter.getPn1And();
    	if(StringUtils.isNotEmpty(pn1)||StringUtils.isNotEmpty(pn2)||StringUtils.isNotEmpty(pn3)){
    		search1 += " and (";
			String flag = "0";
			if(StringUtils.isNotEmpty(pn1)){
				search1 += "GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+pn1+"%'";
				flag = "1";
			}
			if(StringUtils.isNotEmpty(pn2)){
				if("0".equals(flag)){
					search1 += "GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+pn2+"%'";
					flag = "1";
				}else{
					search1 += (("1".equals(and)?"and":"or")+" GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+pn2+"%'");
				}
			}
			if(StringUtils.isNotEmpty(pn3)){
				if("0".equals(flag)){
					search1 += "GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+pn3+"%'";
				}else{
					search1 +=  (("1".equals(and)?"and":"or")+" GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+pn3+"%'");
				}
			}
    		search1 += ")";
    	}
    	
    	String good = customFilter.getGood();
    	String error = customFilter.getError();
    	if("0".equals(good)&&"0".equals(error)){
    		search1 += "and ( GROUP_CONCAT(DISTINCT c.`star`) is null)";
    	}else{
	    	if(StringUtils.isNotEmpty(good)){
	    		if("1".equals(good)){
	    			search1 += "and ( GROUP_CONCAT(DISTINCT c.`star`)  LIKE '%4%' or  GROUP_CONCAT(DISTINCT c.`star`) LIKE '%5%' )";
	    		}else{
	    			search1 += "and (( GROUP_CONCAT(DISTINCT c.`star`)  not  LIKE '%4%' and GROUP_CONCAT(DISTINCT c.`star`) not LIKE '%5%' ) or GROUP_CONCAT(DISTINCT c.`star`) is null )";
	    		}
	    	}
	    	if(StringUtils.isNotEmpty(error)){
	    		if("1".equals(error)){
	    			search1 += "and ( GROUP_CONCAT(DISTINCT c.`star`)  LIKE '%1%' or  GROUP_CONCAT(DISTINCT c.`star`) LIKE '%2%'  or  GROUP_CONCAT(DISTINCT c.`star`)  LIKE '%3%' )";
	    		}else{
	    			search1 += "and (( GROUP_CONCAT(DISTINCT c.`star`) not  LIKE '%1%'  and  GROUP_CONCAT(DISTINCT c.`star`)  not  LIKE '%2%'  and  GROUP_CONCAT(DISTINCT c.`star`) not  LIKE '%3%' ) or GROUP_CONCAT(DISTINCT c.`star`) is null)";
	    		}
	    	}
    	}
    	String pl = customFilter.getPl();
    	if(StringUtils.isNotEmpty(pl)){
    		search1 +=" and  TRUNCATE(AVG(TIMESTAMPDIFF(DAY,a.`first_buy_date`,a.`last_buy_date`)/a.`buy_times`),0)<="+pl;
    	}
    	//String sql = "SELECT DISTINCT a.`customer_id`,a.`country`,a.`amz_email`,a.`email`,GROUP_CONCAT(DISTINCT b.`product_name` order by b.type_date ) FROM amazoninfo_customer a JOIN amazoninfo_buy_comment b ON a.`customer_id` = b.`customer_id` LEFT JOIN  amazoninfo_review_comment c ON a.`customer_id` = c.`customer_id`  WHERE  a.`last_buy_date` >:p1 AND a.`last_buy_date`<:p2  "+search+" GROUP BY a.`customer_id` having 1=1  "+search1+" ORDER BY a.`country`"+(byExport?"":"LIMIT 20");		
    	String sql = "SELECT DISTINCT a.`customer_id`,a.`country`,a.`amz_email`,a.`email`,GROUP_CONCAT(DISTINCT b.`product_name` order by b.type_date ) FROM amazoninfo_customer a JOIN amazoninfo_buy_comment b ON a.`customer_id` = b.`customer_id` LEFT JOIN  amazoninfo_review_comment c ON a.`customer_id` = c.`customer_id`  WHERE  b.`type_date` >:p1 AND b.`type_date`<:p2  "+search+" GROUP BY a.`customer_id` having 1=1  "+search1+" ORDER BY a.`country`"+(byExport?"":"LIMIT 20");			
		return customerDao.findBySql(sql,new Parameter(customFilter.getStartDate(), DateUtils.addDays(customFilter.getEndDate(),1)));
    }
    
    public Integer queryCount(AmazonCustomFilter customFilter){
    	String search = "";
    	String name = customFilter.getName();
    	if(StringUtils.isNotEmpty(name)){
    		search +=" and a.`name` LIKE '%"+name+"%'";
    	}
    	String email = customFilter.getEmail();
    	if(StringUtils.isNotEmpty(email)){
    		search +=" and (a.`email` LIKE '%"+email+"%' or a.`amz_email` LIKE '%"+email+"%')";
    	}
    	if (customFilter.getCustomer() != null) {
        	String customerId = customFilter.getCustomer().getCustomerId();
        	if(StringUtils.isNotEmpty(customerId)){
        		search +=" and a.`customer_id` LIKE '%"+customerId+"%'";
        	}
		}
    	String country = customFilter.getCountry();
    	if(StringUtils.isNotEmpty(country)){
    		search +=" and a.`country`='"+country+"'";
    	}
    	String buyTimes = customFilter.getBuyTimes();
    	if(StringUtils.isNotEmpty(buyTimes)){
    		if("1".equals(buyTimes)){
    			search +=" and a.`buy_times`=1";
    		}else{
    			search +=" and a.`buy_times`>1";
    		}
    	}
    	String returnQ = customFilter.getReturnFlag();
    	if(StringUtils.isNotEmpty(returnQ)){
    		search +=" and a.`return_quantity` "+("1".equals(returnQ)?">":"=")+" 0";
    	}
    	String bigOrder = customFilter.getBigOrder();
    	if (StringUtils.isNotEmpty(bigOrder)) {
    		if("1".equals(bigOrder)){
    			search +=" and b.`quantity`>=10";
    		}else{
    			search +=" and b.`quantity`<10";
    		}
		}
    	String search1 = "";
    	
    	String productName1 = customFilter.getPn1();
    	String productName2 = customFilter.getPn2();
    	String productName3 = customFilter.getPn3();
    	String and = customFilter.getPnAnd();
    	if(StringUtils.isNotEmpty(productName1)||StringUtils.isNotEmpty(productName2)||StringUtils.isNotEmpty(productName3)){
    		search1 += " and (";
			String flag = "0";
			if(StringUtils.isNotEmpty(productName1)){
				search1 += "GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+productName1+"%'";
				flag = "1";
			}
			if(StringUtils.isNotEmpty(productName2)){
				if("0".equals(flag)){
					search1 += "GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+productName2+"%'";
					flag = "1";
				}else{
					search1 += (("1".equals(and)?"and":"or")+" GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+productName2+"%'");
				}
			}
			if(StringUtils.isNotEmpty(productName3)){
				if("0".equals(flag)){
					search1 += "GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+productName3+"%'";
				}else{
					search1 +=  (("1".equals(and)?"and":"or")+" GROUP_CONCAT(DISTINCT b.`product_name`) LIKE '%"+productName3+"%'");
				}
			}
    		search1 += ")";
    	}
    	
    	String pn1 = customFilter.getPn11();
    	String pn2 = customFilter.getPn22();
    	String pn3 = customFilter.getPn33();
    	
    	and = customFilter.getPn1And();
    	if(StringUtils.isNotEmpty(pn1)||StringUtils.isNotEmpty(pn2)||StringUtils.isNotEmpty(pn3)){
    		search1 += " and (";
			String flag = "0";
			if(StringUtils.isNotEmpty(pn1)){
				search1 += "GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+pn1+"%'";
				flag = "1";
			}
			if(StringUtils.isNotEmpty(pn2)){
				if("0".equals(flag)){
					search1 += "GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+pn2+"%'";
					flag = "1";
				}else{
					search1 += (("1".equals(and)?"and":"or")+" GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+pn2+"%'");
				}
			}
			if(StringUtils.isNotEmpty(pn3)){
				if("0".equals(flag)){
					search1 += "GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+pn3+"%'";
				}else{
					search1 +=  (("1".equals(and)?"and":"or")+" GROUP_CONCAT(DISTINCT b.`product_name`) not LIKE '%"+pn3+"%'");
				}
			}
    		search1 += ")";
    	}
    	
    	String good = customFilter.getGood();
    	String error = customFilter.getError();
    	if("0".equals(good)&&"0".equals(error)){
    		search1 += "and ( GROUP_CONCAT(DISTINCT c.`star`) is null)";
    	}else{
	    	if(StringUtils.isNotEmpty(good)){
	    		if("1".equals(good)){
	    			search1 += "and ( GROUP_CONCAT(DISTINCT c.`star`)  LIKE '%4%' or  GROUP_CONCAT(DISTINCT c.`star`) LIKE '%5%' )";
	    		}else{
	    			search1 += "and (( GROUP_CONCAT(DISTINCT c.`star`)  not  LIKE '%4%' and GROUP_CONCAT(DISTINCT c.`star`) not LIKE '%5%' ) or GROUP_CONCAT(DISTINCT c.`star`) is null )";
	    		}
	    	}
	    	if(StringUtils.isNotEmpty(error)){
	    		if("1".equals(error)){
	    			search1 += "and ( GROUP_CONCAT(DISTINCT c.`star`)  LIKE '%1%' or  GROUP_CONCAT(DISTINCT c.`star`) LIKE '%2%'  or  GROUP_CONCAT(DISTINCT c.`star`)  LIKE '%3%' )";
	    		}else{
	    			search1 += "and (( GROUP_CONCAT(DISTINCT c.`star`) not  LIKE '%1%'  and  GROUP_CONCAT(DISTINCT c.`star`)  not  LIKE '%2%'  and  GROUP_CONCAT(DISTINCT c.`star`) not  LIKE '%3%' ) or GROUP_CONCAT(DISTINCT c.`star`) is null)";
	    		}
	    	}
    	}
    	
    	String pl = customFilter.getPl();
    	if(StringUtils.isNotEmpty(pl)){
    		search1 +=" and  TRUNCATE(AVG(TIMESTAMPDIFF(DAY,a.`first_buy_date`,a.`last_buy_date`)/a.`buy_times`),0)<="+pl;
    	}
    	
    	//String sql = "select count(1) from (SELECT DISTINCT a.`customer_id`,a.`country`,a.`amz_email`,a.`email` FROM amazoninfo_customer a JOIN amazoninfo_buy_comment b ON a.`customer_id` = b.`customer_id` LEFT JOIN  amazoninfo_review_comment c ON a.`customer_id` = c.`customer_id`  WHERE  a.`last_buy_date` >:p1 AND a.`last_buy_date`<:p2  "+search+" GROUP BY a.`customer_id` having 1=1  "+search1+" ORDER BY a.`country`) as cc";
    	String sql = "select count(1) from (SELECT DISTINCT a.`customer_id`,a.`country`,a.`amz_email`,a.`email` FROM amazoninfo_customer a JOIN amazoninfo_buy_comment b ON a.`customer_id` = b.`customer_id` LEFT JOIN  amazoninfo_review_comment c ON a.`customer_id` = c.`customer_id`  WHERE  b.`type_date` >:p1 AND b.`type_date`<:p2  "+search+" GROUP BY a.`customer_id` having 1=1  "+search1+" ORDER BY a.`country`) as cc";			
    	List<Object> rs =  customerDao.findBySql(sql,new Parameter(customFilter.getStartDate(), DateUtils.addDays(customFilter.getEndDate(),1)));
		return ((BigInteger)rs.get(0)).intValue();
    }
    
    @Transactional(readOnly = false)
	public  void deleteRepeatReviews() {
		String sql = "SELECT GROUP_CONCAT(a.`id` ORDER BY a.`id`) FROM amazoninfo_review_comment a GROUP BY a.`customer_id`,a.`review_asin`,a.`star`  HAVING COUNT(1) >1";
		List<String> list = customerDao.findBySql(sql);
		List<String> del = Lists.newArrayList();
		for (String ids : list) {
			List<String> temp = Lists.newArrayList(ids.split(","));
			temp.remove(0);
			del.addAll(temp);
		}
		sql = "DELETE FROM amazoninfo_review_comment  WHERE id IN :p1";
		customerDao.updateBySql(sql, new Parameter(del));
    }
    
    
    public Map<String,Map<String,List<String>>> getReviews(){
    	String sql="SELECT ASIN,country,star,review_asin FROM amazoninfo_review_comment WHERE create_date>=DATE_FORMAT((DATE_SUB(NOW(),INTERVAL 1 DAY)),'%Y-%m-%d 09:00:00') ";
    	List<Object[]> list=customerDao.findBySql(sql);
    	Map<String,Map<String,List<String>>> map=Maps.newHashMap();
    	for (Object[] obj : list) {
			String key=obj[0].toString()+"_"+obj[1].toString();
			Map<String,List<String>> data=map.get(key);
			if(data==null){
				data=Maps.newHashMap();
				map.put(key, data);
			}
		    List<String> reviewAsinList=data.get(obj[2].toString());
		    if(reviewAsinList==null){
		    	reviewAsinList=Lists.newArrayList();
		    	data.put(obj[2].toString(), reviewAsinList);
		    }
		    reviewAsinList.add(obj[3].toString());
		}
    	return map;
    }
	
    /**
     * 更新
     */
	@Transactional(readOnly = false)
	public void updateDays() {
		String sql = "UPDATE amazoninfo_customer a SET a.`days`=TIMESTAMPDIFF(DAY,a.`first_buy_date`,a.`last_buy_date`)"+ 
				" WHERE a.`last_buy_date`>DATE_ADD(CURRENT_DATE(),INTERVAL -5 DAY) OR a.`days` IS NULL ";
		customerDao.updateBySql(sql, null);
	}
    
	public String findCustomId(String email){
		String sql=" SELECT r.`customer_id` FROM amazoninfo_customer r WHERE r.`amz_email`=:p1";
		List<String> list=customerDao.findBySql(sql, new Parameter(email));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public String findProductName(String customerId){
		String sql="SELECT t.`product_name` FROM amazoninfo_buy_comment t WHERE t.`customer_id`=:p1 ORDER BY create_date DESC LIMIT 1";
		List<String> list=customerDao.findBySql(sql, new Parameter(customerId));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void updateEmail(String customerId,String email,String amzEmail) {
		AmazonCustomer amazonCustomer=get(amzEmail); 
		if(amazonCustomer!=null){
			
			String tempEmail="";
			String sql = "UPDATE amazoninfo_customer a SET a.email=:p1 where customer_id=:p2 ";
			if(StringUtils.isBlank(amazonCustomer.getEmail())){
				tempEmail=email;
				if(tempEmail.length()<150){
					customerDao.updateBySql(sql,new Parameter(tempEmail,customerId));
				}
			}else{
				String[] arr=amazonCustomer.getEmail().split(",");
				boolean flag=true;
				for (String emailArr : arr) {
					if(emailArr.equals(email)){
						flag=false;
						break;
					}
				}
				if(flag){
					tempEmail=amazonCustomer.getEmail()+","+email;
					  if(tempEmail.length()<150){
						  customerDao.updateBySql(sql,new Parameter(tempEmail,customerId));
					  }
					
				}else{
					tempEmail=amazonCustomer.getEmail();
				}
			}
			
			String sql2="select email from amazoninfo_match_private_email where amz_email=:p1";
			List<String> amzList=customerDao.findBySql(sql2,new Parameter(amzEmail));
			if(amzList!=null&&amzList.size()>0){
				 if(tempEmail.length()<500){
					 String updateSql="update amazoninfo_match_private_email set email=:p1 where amz_email=:p2 ";
					 customerDao.updateBySql(updateSql,new Parameter(tempEmail,amzEmail));
				 }
			}else if(StringUtils.isNotBlank(amzEmail)&&StringUtils.isNotBlank(email)&&tempEmail.length()<500){
				String insertSql="insert into amazoninfo_match_private_email(amz_email,email) value(:p1,:p2) ";
				customerDao.updateBySql(insertSql,new Parameter(amzEmail,tempEmail));
			}
			
			/*String sql2="select amz_email from amazoninfo_match_private_email where email=:p1";
			List<String> amzList=customerDao.findBySql(sql2,new Parameter(email));
			if(amzList!=null&&amzList.size()>0){
				String updateSql="update amazoninfo_match_private_email set amz_email=:p1 where email=:p2 ";
				customerDao.updateBySql(updateSql,new Parameter(amzEmail,email));
			}else{
				String insertSql="insert into amazoninfo_match_private_email(amz_email,email) value(:p1,:p2) ";
				customerDao.updateBySql(insertSql,new Parameter(amzEmail,email));
			}*/
		}
	}
	
	@Transactional(readOnly = false)
	public void updateEmail3(String email,String amzEmail) {
		AmazonCustomer amazonCustomer=get(amzEmail); 
		if(amazonCustomer!=null){
			String sql = "UPDATE amazoninfo_customer a SET a.email=:p1 where amz_email=:p2 ";
			customerDao.updateBySql(sql,new Parameter(email,amzEmail));
			
			String sql2="select email from amazoninfo_match_private_email where amz_email=:p1";
			List<String> amzList=customerDao.findBySql(sql2,new Parameter(amzEmail));
			if(amzList!=null&&amzList.size()>0){
				String updateSql="update amazoninfo_match_private_email set email=:p1 where amz_email=:p2 ";
				customerDao.updateBySql(updateSql,new Parameter(email,amzEmail));
			}else{
				String insertSql="insert into amazoninfo_match_private_email(amz_email,email) value(:p1,:p2) ";
				customerDao.updateBySql(insertSql,new Parameter(amzEmail,email));
			}
			
		}
	}
	
	
	@Transactional(readOnly = false)
	public void updateEmail2(String email,String amzEmail) {
			String sql2="select email from amazoninfo_match_private_email where amz_email=:p1";
			List<String> amzList=customerDao.findBySql(sql2,new Parameter(amzEmail));
			if(amzList!=null&&amzList.size()>0){
				String[] arr=amzList.get(0).split(",");
				boolean flag=true;
				for (String emailArr : arr) {
					if(emailArr.equals(email)){
						flag=false;
						break;
					}
				}
				if(flag){
					
					String tempEmail=amzList.get(0)+","+email;
                    if(tempEmail.length()<500){
                    	String updateSql="update amazoninfo_match_private_email set email=:p1 where amz_email=:p2 ";
    					customerDao.updateBySql(updateSql,new Parameter(tempEmail,amzEmail));
					}  
				}
				
			}else if(StringUtils.isNotBlank(amzEmail)&&StringUtils.isNotBlank(email)){
				String insertSql="insert into amazoninfo_match_private_email(amz_email,email) value(:p1,:p2) ";
				customerDao.updateBySql(insertSql,new Parameter(amzEmail,email));
			}
	}
	
	public String findAmzEmail(String email){
		String sql="select amz_email from amazoninfo_match_private_email where email like :p1 or email like :p2 or email like :p3 or email like :p4 ";
		List<String> amzList=customerDao.findBySql(sql,new Parameter(email,email+",%","%,"+email+",%","%,"+email));
		if(amzList!=null&&amzList.size()>0){
			return amzList.get(0);
		}
		return null;
	}
	
	
	public List<AmazonReviewComment> findReview(String country,String customerId,Date date,String asin){
		List<AmazonReviewComment>  commentList=Lists.newArrayList();
		String sql="SELECT review_date,country,review_asin,ASIN,star,SUBJECT FROM amazoninfo_review_comment t WHERE t.`customer_id`=:p1 AND create_date>=:p2 and asin=:p3 and country=:p4 ";
		List<Object[]> list=customerDao.findBySql(sql,new Parameter(customerId,date,asin,country));
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				Date reviewDate=(Timestamp)obj[0];
				String buyCountry=obj[1].toString();
				String reviewAsin=obj[2].toString();
				String buyAsin=obj[3].toString();
				String star=obj[4].toString();
				String subject=obj[5].toString();
				
				AmazonReviewComment comment=new AmazonReviewComment();
				comment.setReviewDate(reviewDate);
				comment.setCountry(buyCountry);
				comment.setReviewAsin(reviewAsin);
				comment.setAsin(buyAsin);
				comment.setStar(star);
				comment.setSubject(subject);
				commentList.add(comment);
			}
		}
		return commentList;
	}
	
	public AmazonCustomer getByCustomId(String customerId) {
		 DetachedCriteria dc = customerDao.createDetachedCriteria();
		 dc.add(Restrictions.eq("customerId", customerId));
		 List<AmazonCustomer> list=customerDao.find(dc);
		 if(list!=null&&list.size()>0){
			 return list.get(0);
		 }
		 return null;
	}
}
