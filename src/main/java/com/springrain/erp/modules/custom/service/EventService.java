package com.springrain.erp.modules.custom.service;

import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.custom.dao.EventDao;
import com.springrain.erp.modules.custom.entity.AutoReply;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.psi.entity.PsiProductGroupCustomer;
import com.springrain.erp.modules.psi.entity.PsiProductGroupCustomerEmail;
import com.springrain.erp.modules.sys.dao.RoleDao;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 事件Service
 * @author tim
 * @version 2014-05-21
 */
@Component
@Transactional(readOnly = true)
public class EventService extends BaseService {
	@Autowired
	private EventDao eventDao;
	
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private AutoReplyService autoReplyService;
	
	@Autowired
	private SystemService systemService;
		
	public Event get(Integer id) {
		return eventDao.get(id);
	}
	
	public Map<String,Integer> getUnDealEventMap(){
		String sql="SELECT s.name,COUNT(*) FROM custom_event_manager m JOIN sys_user  s ON m.`master_by`=s.`id` "+
				" WHERE state='0' AND m.del_flag='0' and m.create_by='1' and transmit is null and reason is null AND  HOUR(TIMEDIFF(NOW(),m.create_date))>24 and s.name is not null GROUP BY s.`name`  ";
		Map<String,Integer> map=Maps.newHashMap();
		List<Object[]> list=eventDao.findBySql(sql);
		for (Object[] obj : list) {
			map.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
		}
		return map;
	}
	
	public Map<String,Map<String,Object[]>> getUnDealTypeEventMap(){
		String sql="SELECT s.name,m.type,COUNT(*) total,MIN(m.create_date) FROM custom_event_manager m JOIN sys_user  s ON m.`master_by`=s.`id` "+
				" WHERE state='0' AND m.del_flag='0' AND s.del_flag='0' and m.create_by='1' and transmit is null and (reason is null or reason='') AND  HOUR(TIMEDIFF(NOW(),m.create_date))>24 and s.name is not null GROUP BY s.`name`,m.type order by total desc  ";
		Map<String,Map<String,Object[]>> map=Maps.newLinkedHashMap();
		List<Object[]> list=eventDao.findBySql(sql);
		for (Object[] obj : list) {
			Map<String,Object[]> typeMap=map.get(obj[0].toString());
			if(typeMap==null){
				typeMap=Maps.newLinkedHashMap();
				map.put(obj[0].toString(),typeMap);
			}
			Object[] temp=new Object[2];
			temp[0]=obj[2];
			temp[1]=obj[3];
			typeMap.put(obj[1].toString(),temp);	
		}
		return map;
	}
	
    public Event getById(Integer id,String type){
		String sql="select r.id,r.country,r.custom_name,r.custom_email,s.sku,r.invoice_number from custom_event_manager r " +
	            "   JOIN psi_sku s ON s.asin=r.remarks AND s.country=r.country  WHERE s.del_flag='0' and s.product_name not like '%Inateck Old%' and s.product_name not like '%Inateck Other%' and r.id=:p1 LIMIT 1  ";
		List<Object[]> list=eventDao.findBySql(sql,new Parameter(id));
		Event e=new Event();
		for (Object[] obj : list) {
			e.setId(Integer.parseInt(obj[0].toString()));
			e.setCountry(obj[1].toString());
			e.setCustomName(obj[2].toString());
			e.setCustomEmail(obj[3].toString());
			e.setRemarks(obj[4].toString());
			e.setInvoiceNumber(obj[5]==null?"":obj[5].toString());
			if(StringUtils.isNotBlank(e.getInvoiceNumber())&&("2".equals(type)||"5".equals(type))){
				int len=e.getInvoiceNumber().split("-").length;
				String sqlString="select NAME,street,street1,street2,city_name,county,state_or_province,country_code,postal_code,phone from ebay_order r join ebay_address s on r.shipping_address=s.id where r.order_id=:p1 LIMIT 1  ";
				if(len==2){
				   sqlString="select NAME,street,street1,street2,city_name,county,state_or_province,country_code,postal_code,phone from ebay_order r join ebay_address s on r.shipping_address=s.id where r.order_id=:p1 LIMIT 1  ";
				}else{
				   sqlString="select NAME,address_line1,address_line2,address_line3,city,county,state_or_region,country_code,postal_code,phone from amazoninfo_order r join amazoninfo_address s on r.shipping_address=s.id where r.amazon_order_id=:p1 LIMIT 1  ";
				}
				
				List<Object[]> orderInfo=eventDao.findBySql(sqlString,new Parameter(e.getInvoiceNumber()));
				for (Object[] obj2 : orderInfo) {
					e.setName(obj2[0]==null?"":obj2[0].toString());
					e.setStreet(obj2[1]==null?"":obj2[1].toString());
					e.setStreet1(obj2[2]==null?"":obj2[2].toString());
					e.setStreet2(obj2[3]==null?"":obj2[3].toString());
					e.setCityName(obj2[4]==null?"":obj2[4].toString());
					e.setCountry(obj2[5]==null?"":obj2[5].toString());
					e.setStateOrProvince(obj2[6]==null?"":obj2[6].toString());
					e.setCountryCode(obj2[7]==null?"":obj2[7].toString());
					e.setPostalCode(obj2[8]==null?"":obj2[8].toString());
					e.setPhone(obj2[9]==null?"":obj2[9].toString());
				}
			}
		}
		return e;
	}
    
	public Page<Event> find(Page<Event> page, Event event) {
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		
		String country = event.getCountry();
		String countrySql = null;
		if (StringUtils.isNotEmpty(country)){
			if("other".equals(event.getCountry())){
				dc.add(Restrictions.eq("country", ""));
				countrySql = "";
			}else{
				dc.add(Restrictions.eq("country", country));
				countrySql = country;
			}	
		}
		String subject = event.getSubject();
		if (StringUtils.isNotEmpty(event.getSubject())){
			String temp = "";
			if(StringUtils.isNotEmpty(countrySql)){
				temp  = "AND a.`country` ='"+countrySql+"'";
			}
			String sql = "SELECT DISTINCT a.`asin` FROM psi_sku a WHERE a.`del_flag`='0'  AND CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END)  LIKE :p1  " +temp;
			List<Object> list = eventDao.findBySql(sql, new Parameter("%"+event.getSubject()+"%"));
			
			int id = 0 ;
			try {
				id = Integer.parseInt(event.getSubject());
			} catch (NumberFormatException e) {}
			if(list.size()>0){//邮箱 地址 姓名 customer ID
				if(id>0){
					dc.add(Restrictions.or(Restrictions.eq("id", id),Restrictions.like("subject", "%"+subject+"%"),Restrictions.like("customEmail", "%"+subject+"%"),Restrictions.like("customName", "%"+subject+"%"),Restrictions.like("customId", "%"+subject+"%")
							,Restrictions.like("invoiceNumber", "%"+subject+"%"),Restrictions.like("description", "%"+subject+"%"),Restrictions.in("remarks",list)));
				}else{
					dc.add(Restrictions.or(Restrictions.like("subject", "%"+subject+"%"),Restrictions.like("customEmail", "%"+subject+"%"),Restrictions.like("customName", "%"+subject+"%"),Restrictions.like("customId", "%"+subject+"%")
							,Restrictions.like("invoiceNumber", "%"+subject+"%"),Restrictions.like("description", "%"+subject+"%"),Restrictions.in("remarks", list)));
				}
			}else{
				if(id>0){
					dc.add(Restrictions.or(Restrictions.eq("id", id),Restrictions.like("subject", "%"+subject+"%"),Restrictions.like("description", "%"+subject+"%"),Restrictions.like("customEmail", "%"+subject+"%"),Restrictions.like("customName", "%"+subject+"%"),Restrictions.like("customId", "%"+subject+"%"),Restrictions.like("invoiceNumber", "%"+subject+"%")));
				}else{
					dc.add(Restrictions.or(Restrictions.like("subject", "%"+subject+"%"),Restrictions.like("description", "%"+subject+"%"),Restrictions.like("customEmail", "%"+subject+"%"),Restrictions.like("customName", "%"+subject+"%"),Restrictions.like("customId", "%"+subject+"%"),Restrictions.like("invoiceNumber", "%"+subject+"%")));
				}
			}
			if (StringUtils.isNotEmpty(event.getState())){
				dc.add(Restrictions.eq("state", event.getState()));
			}
		}else{
			Date date =  event.getEndDate();
			date.setHours(23);
			date.setMinutes(59);
			if (StringUtils.isNotEmpty(event.getState())){
				dc.add(Restrictions.eq("state", event.getState()));
				if("2,4".contains(event.getState())){
					dc.add(Restrictions.and(Restrictions.ge("endDate",event.getCreateDate()),Restrictions.le("endDate", date)));
				}else{
					dc.add(Restrictions.or(Restrictions.and(Restrictions.ge("createDate",event.getCreateDate()),Restrictions.le("createDate",date))
							  ,Restrictions.and(Restrictions.ge("updateDate",event.getCreateDate()),Restrictions.le("updateDate", date))));
				}
			}else{
				dc.add(Restrictions.or(Restrictions.and(Restrictions.ge("createDate",event.getCreateDate()),Restrictions.le("createDate",date))
						  ,Restrictions.and(Restrictions.ge("endDate",event.getCreateDate()),Restrictions.le("endDate", date))
						  ,Restrictions.and(Restrictions.ge("updateDate",event.getCreateDate()),Restrictions.le("updateDate", date))));
			}
			dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		}
		String remark = event.getRemarks();
		if (StringUtils.isNotEmpty(remark) && remark.indexOf(":")>0){
			String[] temp = remark.trim().split(":");
			Set<String> set = Sets.newHashSet();
			if(temp.length>0){
				set.add(temp[0]);
				dc.add(Restrictions.in("remarks",set));
			}
		}
		if (event.getMasterBy()!=null && StringUtils.isNotEmpty(event.getMasterBy().getId())){
			User masterBy = UserUtils.getUserById(event.getMasterBy().getId());
			if("1".equals(event.getPriority())){
				dc.add(Restrictions.or(Restrictions.eq("masterBy", masterBy)
						,Restrictions.eq("createBy", masterBy),Restrictions.like("transmit",masterBy.getName()+"(%"),Restrictions.like("transmit","%,"+masterBy.getName()+"(%")));
			}else{
				dc.add(Restrictions.or(Restrictions.eq("masterBy", masterBy)
						,Restrictions.eq("createBy", masterBy)));
			}
		}
		if (event.getCreateBy()!=null && StringUtils.isNotEmpty(event.getCreateBy().getId())){
			dc.add(Restrictions.eq("masterBy", event.getCreateBy()));
		}
		if (StringUtils.isNotEmpty(event.getType())){
			if("-1".equals(event.getType())){
				dc.add(Restrictions.or(Restrictions.eq("type", "1"),Restrictions.eq("type", "2")));
			}else{
				dc.add(Restrictions.eq("type", event.getType()));
			}
			if("1".equals(event.getType())&&StringUtils.isNotBlank(event.getProductAttribute())){
				dc.add(Restrictions.eq("productAttribute",event.getProductAttribute()));
			}
		}
		if(StringUtils.isNotBlank(event.getIsEvil())){
			if("0".equals(event.getIsEvil())){
				dc.add(Restrictions.eq("isEvil", event.getIsEvil()));
			}else{
				dc.add(Restrictions.or(Restrictions.eq("isEvil", event.getIsEvil()),Restrictions.isNull("isEvil")));
			}
			
		}
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		
		if(StringUtils.isNotBlank(event.getProductLine())){
			String sql="SELECT DISTINCT s.asin FROM psi_product d "+
			" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type'  "+
			"JOIN psi_product_type_group g ON t.id=g.`dict_id`  "+
			" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0'  "+
			" JOIN psi_sku s ON s.`product_name`=CONCAT(d.brand,' ',d.model) AND s.del_flag='0' "+
			" WHERE p.id=:p1 AND ASIN IS NOT NULL  ";
			List<String> list = eventDao.findBySql(sql, new Parameter(event.getProductLine()));
			if(list!=null&&list.size()>0){
				dc.add(Restrictions.in("remarks",list));
			}else{
				dc.add(Restrictions.eq("remarks",event.getProductLine()));
			}
		}
		return eventDao.find(page, dc);
	}
	
	
	public Page<Event> findProblems(Page<Event> page, Event event) {
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		if(event.getAnswerDate()!=null){
			Restrictions.ge("answerDate", event.getAnswerDate());
		}
		
		if(event.getEndDate()!=null){
			Restrictions.ge("endDate", event.getEndDate());
		}
		
		if(StringUtils.isNotEmpty(event.getProductName())){
			Restrictions.like("productName", "%"+event.getProductName()+"%");
		}
		
		dc.add(Restrictions.isNotNull("problemType"));
		dc.add(Restrictions.isNotNull("answerDate"));
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		return eventDao.find(page, dc);
	}
	
	public Map<String,Map<String, String>> count(Event event) {
		String country = event.getCountry();
		List<SimpleExpression> ses = Lists.newArrayList();
		if (StringUtils.isNotEmpty(country)){
			if("other".equals(event.getCountry())){
				ses.add(Restrictions.eq("country", ""));
			}else{
				ses.add(Restrictions.eq("country", country));
			}	
		}
		if (StringUtils.isNotEmpty(event.getType())){
			ses.add(Restrictions.eq("type", event.getType()));
		}
		ses.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		return eventDao.findCount(ses,event.getCreateDate(),event.getEndDate());
	}
	
	public Event findEvent(Event event){
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("type", event.getType()));
		if("6".equals(event.getType())){
			dc.add(Restrictions.or(Restrictions.eq("reviewLink", event.getReviewLink()),Restrictions.like("reviewLink", event.getReviewLink()+"%"),Restrictions.eq("reviewLink", event.getReviewLink().replace("https:", "http:")),Restrictions.like("reviewLink", event.getReviewLink().replace("https:", "http:")+"%")));
		}else{
			dc.add(Restrictions.or(Restrictions.eq("reviewLink", event.getReviewLink()),Restrictions.eq("reviewLink", event.getReviewLink().replace("https:", "http:"))));
		}
		List<Event> list = eventDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public boolean isExistByorderId(Event event){
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("type", event.getType()));
		dc.add(Restrictions.eq("invoiceNumber", event.getInvoiceNumber()));
		dc.add(Restrictions.eq("priority", event.getPriority()));
		return eventDao.count(dc)>0;
	}
	
	public Event findEvent(String email,User master){
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("state", "1"));
		dc.add(Restrictions.or(Restrictions.eq("type", "1"),Restrictions.eq("type", "2")));
		dc.add(Restrictions.eq("customEmail",email));
		dc.add(Restrictions.eq("masterBy", master));
		List<Event> list = eventDao.find(dc);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	
	@Transactional(readOnly = false)
	public void save(Event event) {
		if("1,2".contains(event.getType())){
			if(findEvent(event)!=null){
				return;
			}
		}
		User master = event.getMasterBy();
		AutoReply autoReply = new AutoReply();
		autoReply.setType("2");
		autoReply.setCreateBy(master);
		autoReply = autoReplyService.findByUser(autoReply);
		if(autoReply!=null&&"1".equals(autoReply.getUsedForward())&&autoReply.getForwardTo()!=null){
			event.setMasterBy(autoReply.getForwardTo());
			event.setTransmit(master.getName()+" auto forwardTo "+event.getMasterBy().getName());
		}
		if(event.getId()==null&&"1,2".contains(event.getType())){
			event.setDescription(Encodes.filterOffUtf8Mb4(event.getDescription()));
		}
		//过滤掉<body>标签
		event.setDescription(StringUtils.getDelHtmlTags(event.getDescription(), Lists.newArrayList("html", "body", "head")));
		eventDao.save(event);
	}
	
	@Transactional(readOnly = false)
	public void save(List<Event> events) {
		for (Event event : events) {
			save(event);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		eventDao.deleteById(id);
	}
	
	@Transactional(readOnly = false)
	public void updatePrice(Integer id,Float reviewPrice) {
		String sql="update custom_event_manager  set review_price=:p1 where id=:p2 ";
		eventDao.updateBySql(sql, new Parameter(reviewPrice,id));
	}
	
	public User getMaster(String key,String eventType,String orderId) {
		if(("1".equals(eventType)||"2".equals(eventType))&&!"not find oderID".equals(orderId)){
			DetachedCriteria dc = eventDao.createDetachedCriteria();
			dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
			dc.add(Restrictions.in("type", new String[]{"1","2"}));
			dc.add(Restrictions.eq("invoiceNumber",orderId));
			List<Event> list = eventDao.find(dc);
			if(list.size()>0){
				User user = list.get(0).getMasterBy();
				 if("0".equals(user.getDelFlag())){
					 return user;
				 };
			}
		}
//		DetachedCriteria criteria = roleDao.createDetachedCriteria();
//		criteria.add(Restrictions.eq("remarks", "event:"+key));
//		List<Role> roles =  roleDao.find(criteria);
//		List<User> users = null;
//		if(roles.size()==1){
//			Role role = roles.get(0);
//			Hibernate.isInitialized(role.getUserList());
//			users = role.getUserList();
//		}
//		if(users==null || users.size()==0){
//			criteria = roleDao.createDetachedCriteria();
//			criteria.add(Restrictions.eq("remarks", "event:com"));
//			roles =  roleDao.find(criteria);
//			if(roles.size()==1){
//				Role role = roles.get(0);
//				Hibernate.isInitialized(role.getUserList());
//				users = role.getUserList();
//			}else{
//				throw new RuntimeException("美国事件处理角色必须存在！");
//			}
//		}
		//--------------------用权限表示替代角色开始
		String perssion = "event:service:"+key;
		List<User> users = this.systemService.hasPerssion(perssion);
		if(users==null || users.size()==0){
			perssion = "event:service:com";
			users = this.systemService.hasPerssion(perssion);
		}
		//--------------------用权限表示替代角色结束
		int size = users.size();
		if(size==0){
			throw new RuntimeException("美国事件处理人必须存在！");
		}else if(size == 1){
			return users.get(0);
		}else{
			Map<Integer,User> ids = Maps.newHashMap();
			for (User user : users) {
				List<Object> list = eventDao.findBySql("select max(id) from custom_event_manager where type = :p1 and master_by =:p2",new Parameter(eventType,user.getId()));
				if(list!=null&&list.size()==1&&list.get(0)!=null&&list.get(0).toString()!=null){
					try {
						ids.put(Integer.parseInt(list.get(0).toString()),user);
					} catch (NumberFormatException e) {
						return user;
					}
				}else{
					return user;
				}
			}
			int temp = 0 ;
			for (Integer id : ids.keySet()) {
				if(temp>0){
					temp = temp >= id ? id:temp;
				}else{
					temp = id;
				}
			}
			return ids.get(temp);
		}
	}
	
	public User getMaster(String key,String eventType,List<PsiProductGroupCustomer> customerList) {
		Map<Integer,User> ids = Maps.newHashMap();
		for (PsiProductGroupCustomer customer : customerList) {
			List<Object> list = eventDao.findBySql("select max(id) from custom_event_manager where type = :p1 and master_by =:p2",new Parameter(eventType,customer.getUserId()));
			if(list!=null&&list.size()==1&&list.get(0)!=null&&list.get(0).toString()!=null){
				try {
					ids.put(Integer.parseInt(list.get(0).toString()),systemService.getUser(customer.getUserId()));
				} catch (NumberFormatException e) {
					return systemService.getUser(customer.getUserId());
				}
			}else{
				return systemService.getUser(customer.getUserId());
			}
		}
		int temp = 0 ;
		for (Integer id : ids.keySet()) {
			if(temp>0){
				temp = temp >= id ? id:temp;
			}else{
				temp = id;
			}
		}
		return ids.get(temp);
	}
	
	public User getMaster(String key,List<PsiProductGroupCustomerEmail> customerList) {
		Map<Timestamp,User> ids = Maps.newHashMap();
		for (PsiProductGroupCustomerEmail customer : customerList) {
			List<Object> list = eventDao.findBySql("select max(create_date) from custom_email_manager where master_by =:p1 and  del_flag='0' ",new Parameter(customer.getUserId()));
			if(list!=null&&list.size()==1&&list.get(0)!=null){
				try {
					ids.put((Timestamp)list.get(0),systemService.getUser(customer.getUserId()));
				} catch (NumberFormatException e) {
					return systemService.getUser(customer.getUserId());
				}
			}else{
				return systemService.getUser(customer.getUserId());
			}
		}
		Timestamp temp =null;
		for (Timestamp date: ids.keySet()) {
			if(temp!=null){
				temp = (temp.after(date)? date:temp);
			}else{
				temp = date;
			}
		}
		return ids.get(temp);
	}
	
	public boolean getEventIsExistByOrder(String orderId){
		if(StringUtils.isEmpty(orderId)){
			return false;
		}
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		dc.add(Restrictions.or(Restrictions.eq("type", "5"),Restrictions.eq("type", "7"),Restrictions.eq("type", "8")));
		dc.add(Restrictions.like("invoiceNumber","%"+orderId+"%"));
		return eventDao.count(dc)>0;
	}
	
	public Date getAccentEventMaxDate(String country){
		String sql = "SELECT MAX(a.`create_date`) FROM custom_event_manager a WHERE a.`type` = '2' AND a.`country` = :p1";
		List<Object> date =  eventDao.findBySql(sql, new Parameter(country));
		if(date!=null&&date.size()==1){
			return (Date)date.get(0);
		}
		return null;
	}
	
	public String getLinksMasterName(String link){
		//String sql = "SELECT a.`master_by` FROM custom_event_manager a  WHERE  :p1 REGEXP  CONCAT('^',REPLACE(REPLACE(REPLACE(TRIM(TRAILING ',' FROM a.`review_link`),',','|^'),'?','\\\\?'),'+','\\\\+')) and a.`state`='2' and  a.`type`='8' AND a.`review_link`!=''";
		String sql = "SELECT a.`master_by`,a.`review_link` FROM custom_event_manager a  WHERE a.`review_link` like :p1 and a.`state`='2' and  a.`type`='8' AND a.`review_link`!='' ORDER BY a.`update_date` desc ";
		String temp = HtmlUtils.htmlUnescape(link);
		List<Object> objs = eventDao.findBySql(sql, new Parameter("%"+temp.replace("%", "\\%").replace("_", "\\_")+"%"));
		if(objs.size()>=1){
			User user = systemService.getUser(((Object[])objs.get(0))[0].toString());
			if(user!=null){
				return user.getName();
			}
		}else{
			objs = eventDao.findBySql(sql, new Parameter("%"+link.replace("%", "\\%").replace("_", "\\_")+"%"));
			if(objs.size()>=1){
				User user = systemService.getUser(((Object[])objs.get(0))[0].toString());
				if(user!=null){
					return user.getName();
				}
			}else{
				if(link.contains("?")){
					objs = eventDao.findBySql(sql, new Parameter("%"+link.split("\\?")[0]+"%"));
					if(objs.size()>=1){
						for (Object obj: objs) {
							String linkStr = ((Object[])obj)[1].toString();
							try {
								linkStr =  URLDecoder.decode(linkStr,"utf-8");
							} catch (Exception e) {
								logger.error(linkStr, e);
							}
							linkStr=HtmlUtils.htmlUnescape(linkStr);
							if(linkStr.contains(temp)){
								String userStr = ((Object[])obj)[0].toString();
								User user = systemService.getUser(userStr);
								if(user!=null){
									return user.getName();
								}
							}
						}
					}	
				}
			}
		}
		return null;
	}
	
	public Map<String,String> getEventMap(String type,String country){
		String sql="select id,concat('SPR','-',id) from custom_event_manager where  del_flag='0' and state!='2' and state!='4'  and master_by=:p1 and type=:p2  ";
		if("de".equals(country)){
			sql+=" and country in ('de','fr','it','es','uk') ";
		}else if("com".equals(country)){
			sql+=" and country in ('com') ";
		}
		List<Object[]> list=eventDao.findBySql(sql,new Parameter(UserUtils.getUser().getId(),type));
		Map<String,String> map=new HashMap<String,String>();
		for (Object[] obj : list) { 
			map.put(obj[0].toString(),obj[1].toString());
		}
		return map;
	}
	
	public List<Event> getEventList(String type,String country){
		String sql="select id,concat('SPR','-',id) from custom_event_manager where del_flag='0' and state!='2'  and state!='4'  and master_by=:p1 and type=:p2 ";//
		
		if("de".equals(country)){
			sql+=" and country in ('de','fr','it','es','uk') ";
		}else if("com".equals(country)){
			sql+=" and country in ('com') ";
		}
			
		List<Object[]> list=eventDao.findBySql(sql,new Parameter(UserUtils.getUser().getId(),type));
		List<Event> eventList=new ArrayList<Event>();
		for (Object[] obj : list) {
			Event e=new Event();
			e.setId(Integer.parseInt(obj[0].toString()));
			e.setSubject(obj[1].toString());
			eventList.add(e);
		}
		return eventList;
	}
	
	public Map<String,List<Event>> findScanEvent(){
		Map<String,List<Event>> map=Maps.newHashMap();
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("type", "1"));
		dc.add(Restrictions.or(Restrictions.eq("state", "0"),Restrictions.eq("state", "1")));
		//dc.add(Restrictions.ne("id",746));
		//ca it es没有跟帖情况
		//dc.add(Restrictions.in("country",Lists.newArrayList(new String[] {"com","jp","uk","de"})));
		dc.add(Restrictions.isNotNull("country"));
		List<Event> list=eventDao.find(dc);
		for (Event event : list) {
			Hibernate.initialize(event.getComments());
		}
		list.remove(eventDao.get(746));
		for(Event e:list){
			List<Event> eventList=map.get(e.getCountry());
			if(eventList==null){
				eventList=Lists.newArrayList();
				map.put(e.getCountry(), eventList);
			}
			eventList.add(e);
		}
		
		return map;
	}
	
	public Map<String,List<Event>> findScanFinishedEvent(){
		Map<String,List<Event>> map=Maps.newHashMap();
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("type", "1"));
		dc.add(Restrictions.eq("state", "2"));
		dc.add(Restrictions.like("result","%Unchangeable%"));
		dc.add(Restrictions.ge("endDate",DateUtils.addMonths(new Date(),-6)));
		dc.add(Restrictions.isNotNull("country"));
		List<Event> list=eventDao.find(dc);
		for (Event event : list) {
			Hibernate.initialize(event.getComments());
		}
		list.remove(eventDao.get(746));
		for(Event e:list){
			List<Event> eventList=map.get(e.getCountry());
			if(eventList==null){
				eventList=Lists.newArrayList();
				map.put(e.getCountry(), eventList);
			}
			eventList.add(e);
		}
		return map;
	}
	
	public Map<String,List<Event>> findScanEvent2(){
		Map<String,List<Event>> map=Maps.newHashMap();
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("type", "1"));
		dc.add(Restrictions.or(Restrictions.eq("state", "0"),Restrictions.eq("state", "1")));
		//dc.add(Restrictions.ne("id",746));
		//ca it es没有跟帖情况
		dc.add(Restrictions.in("country",Lists.newArrayList(new String[] {"com","jp","uk","de"})));
		List<Event> list=eventDao.find(dc);
		for (Event event : list) {
			Hibernate.initialize(event.getComments());
			Hibernate.initialize(event.getMasterBy());
		}
		list.remove(eventDao.get(746));
		for(Event e:list){
			List<Event> eventList=map.get(e.getCountry());
			if(eventList==null){
				eventList=Lists.newArrayList();
				map.put(e.getCountry(), eventList);
			}
			eventList.add(e);
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public Map<String,Map<String,String>> findEventWarn(){//
		
		String sql="SELECT DISTINCT g.`country`,g.id,(CASE  WHEN (t.`comment` LIKE '%差评跟帖%') THEN '2' ELSE '3' END),r.del_flag,t.`comment`  FROM custom_event_manager g JOIN custom_event_comment t ON g.id=t.`event` JOIN sys_user r ON g.`master_by`=r.id   "+
         " WHERE t.`create_by`='1' AND t.`del_flag`='0' AND (t.`comment` LIKE '%差评跟帖%'  OR t.comment LIKE '%事件发生了改变%')  "+
         " AND (DATE_FORMAT(t.`create_date`,'%Y-%m-%d')>=:p1 or (t.comment LIKE '%事件发生了改变%' and t.comment LIKE '%页面:3分%'  AND g.`state` IN ('0','1') )) AND g.`country` IS NOT NULL  "+
         " UNION  SELECT DISTINCT g.`country`,g.id,'1',r.del_flag,t.`comment`  FROM custom_event_manager g JOIN custom_event_comment t ON g.id=t.`event` JOIN sys_user r ON g.`master_by`=r.id  "+
         " WHERE t.`create_by`='1' AND t.`del_flag`='0' AND (g.`state` IN ('0','1') or  ( DATE_FORMAT(t.`create_date`,'%Y-%m-%d')>=:p2 and g.`state`='2' and g.`result` LIKE '%Unchangeable%'))  AND (t.`comment` LIKE '差评事件已改成好评%'  OR t.`comment` LIKE '%差评帖已删除%')  "+
         " AND g.`country` IS NOT NULL ";
        List<Object[]> list=eventDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-2),DateUtils.addDays(new Date(),-2)));
        Map<String,Map<String,String>>  map=Maps.newHashMap();
        Set<String> idSet=Sets.newHashSet();
        Set<String> idSet1=Sets.newHashSet();
        Set<String> idSet2=Sets.newHashSet();
        for (Object[] obj : list) {
        	
        	Map<String,String> temp=map.get(obj[0].toString());
        	if(temp==null){
        		temp=Maps.newHashMap();
        		map.put(obj[0].toString(),temp);
        	}
        	String eventStr=temp.get(obj[2].toString());
        	String eventLink=("<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/event/form?id="+obj[1].toString()+"'>SPR_"+obj[1].toString()+"</a>");
        	temp.put(obj[2].toString(),(eventStr==null?"":eventStr+",")+eventLink);
        	
        	if("1".equals(obj[3].toString())){
        		if(StringUtils.isNotBlank(obj[4].toString())){
        			 if(obj[4].toString().contains("已删除")){
        				 idSet2.add(obj[1].toString());
        			 }else  if(obj[4].toString().contains("已改成好评")){
        				 idSet.add(obj[1].toString());
        			 }else if(obj[4].toString().contains("页面:3分")){
        				 idSet1.add(obj[1].toString());
        			 }
        		}
        	}
		}
        try{
           if(idSet!=null&&idSet.size()>0){
           	     updateEndDate(idSet,"To positive");
           }
           if(idSet1!=null&&idSet1.size()>0){
             	 updateEndDate(idSet1,"To neutral");
            }
           if(idSet2!=null&&idSet2.size()>0){
             	 updateEndDate(idSet2,"Deleted");
            }
        }catch(Exception e){}
        
		return map;
	}
	
	public List<Event> findReviewOrderEvent(Event event, String reviewType){
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			User createBy = event.getCreateBy();
			if(null ==createBy){
				event.setCreateBy(user);
				createBy = user;
			}
			if (StringUtils.isNotEmpty(createBy.getId())){
				dc.add(Restrictions.eq("createBy", createBy));
			}	
		}
		dc.add(Restrictions.eq("type", event.getType()));
		if (StringUtils.isNotEmpty(reviewType) && "1".equals(reviewType)) {
			dc.add(Restrictions.in("state", new String[] {"2","4"}));
		} else {
			dc.add(Restrictions.in("state", new String[] {"0","1"}));
		}
		
		if (event.getCreateDate() != null) {
			dc.add(Restrictions.ge("createDate",event.getCreateDate()));
		}
		if (event.getEndDate() != null) {
			dc.add(Restrictions.le("createDate",DateUtils.addDays(event.getEndDate(),1)));
		}
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		List<Event> list = eventDao.find(dc);
		return list;
	}
	
	/**
	 * 根据客户邮箱查找客户评测记录
	 * @param customEmailList(评测者邮箱list,匹配评测者所有的邮箱1~3个)
	 * @return
	 */
	public List<Event> findReviewOrderEventHis(List<String> customEmailList){
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			if (StringUtils.isNotEmpty(user.getId())){
				dc.add(Restrictions.eq("createBy", user));
			}	
		}
		dc.add(Restrictions.eq("type", "8"));
		dc.add(Restrictions.in("customEmail", customEmailList));
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		List<Event> list = eventDao.find(dc);
		return list;
	}
	
	public Event isExistEventByOrder(String orderId){
		if(StringUtils.isEmpty(orderId)){
			return null;
		}
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("type","4"));
		dc.add(Restrictions.ne("state","4"));
		dc.add(Restrictions.eq("delFlag","0"));
		dc.add(Restrictions.eq("invoiceNumber",orderId));
		List<Event> list = eventDao.find(dc);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public List<Object[]> findAllTaxOrder(Date start,Date end,String country){
		String sql="SELECT m.`country`,m.`invoice_number`,d.`refund_total`,m.`attchment_path`,m.tax_id FROM custom_event_manager m "+
				" JOIN amazoninfo_order r ON m.`invoice_number`=r.`amazon_order_id` "+
				" JOIN amazoninfo_refund d ON d.`amazon_order_id`=r.`amazon_order_id` AND d.`is_tax`='0' AND d.`refund_state`!='2' "+
				" WHERE m.del_flag='0' AND m.type='4' AND m.`invoice_number` IS NOT NULL "+// AND m.`attchment_path` LIKE '%/TP%' 
				" and m.`end_date`>=:p1 and m.`end_date`<:p2 ";
		if(StringUtils.isNotBlank(country)){
			sql+=" and m.country=:p3 ";
			return eventDao.findBySql(sql,new Parameter(start,DateUtils.addDays(end,1),country));
		}else{
			return eventDao.findBySql(sql,new Parameter(start,DateUtils.addDays(end,1)));
		}
	}
	
	/**
	 * 查询未获取到客户信息的Review Refund事件
	 * 订单号不为空但是客户邮箱为空
	 * @return
	 */
	public List<Event> findReviewRefundEvent(){
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", "11"));
		dc.add(Restrictions.and(Restrictions.isNotNull("invoiceNumber"), Restrictions.ne("invoiceNumber","")));
		dc.add(Restrictions.or(Restrictions.isNull("customEmail"), Restrictions.isNull("customName"), Restrictions.isNull("customId"), Restrictions.eq("customId","")));
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		List<Event> list = eventDao.find(dc);
		return list;
	}
	
	/**
	 * Review Refund事件同一产品上限是否达到15个，不限平台，不限颜色
	 * @param productName
	 * @return 0 ：达到15个  1：未达到
	 */
	public String isBeyond(String productName){
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		dc.add(Restrictions.eq("type","11"));
		if (productName.contains("_")) {	//带颜色
			dc.add(Restrictions.like("productName", productName.split("_")[0] + "_%"));
		} else {
			dc.add(Restrictions.eq("productName", productName));
		}
		List<Event> list = eventDao.find(dc);
		if (list != null && list.size() > 14) {
			return "0";
		}
		return "1";
	}
	
	public Map<String,String> getEventByCustomId(){
		Map<String,String>  map=Maps.newHashMap();
		String sql="SELECT m.`custom_email`,GROUP_CONCAT(id) FROM custom_event_manager m WHERE m.create_date>=:p1 and m.`del_flag`='0' AND m.`state` IN ('1','2','4') AND m.`custom_id`!='' AND m.`custom_id` IS NOT NULL  GROUP BY m.`custom_id`";
		List<Object[]> list = eventDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(), -20)));
        if(list!=null&&list.size()>0){
        	for (Object[] obj: list) {
        		map.put(obj[0].toString(), obj[1].toString());
			}
        }
        return map;
	}

	@Transactional(readOnly = false)
	public void updateEndDate(Set<String> eventId,String result){
		String sql="update custom_event_manager set end_date=now(),state='2',result=:p2  where id in :p1";
		eventDao.updateBySql(sql, new Parameter(eventId,result));
	}
	
	
	public Map<String,List<Event>> findReviewOrderEvent(){
		Map<String,List<Event>> map=Maps.newHashMap();
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", "8"));
		//dc.add(Restrictions.isNotEmpty("customId"));
		dc.add(Restrictions.or(Restrictions.ne("customId",null), Property.forName("customId").isNotNull()));
		dc.add(Restrictions.in("state", new String[] {"0","1"}));
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		List<Event> list = eventDao.find(dc);
		if(list!=null&&list.size()>0){
			for(Event e:list){
				Hibernate.initialize(e.getComments());
				List<Event> eventList=map.get(e.getCountry());
				if(eventList==null){
					eventList=Lists.newArrayList();
					map.put(e.getCountry(), eventList);
				}
				eventList.add(e);
			}
		}
		return map;
	}
	
	public Map<String,String> findReviewWarn(){//
		//String sql="SELECT DISTINCT g.`country`,g.id,(CASE WHEN (t.`comment` LIKE '差评事件已改成好评%' OR t.`comment` LIKE '%差评帖已删除%') THEN '1' WHEN (t.`comment` LIKE '%差评跟帖%') THEN '2' ELSE '3' END)  FROM custom_event_manager g JOIN custom_event_comment t ON g.id=t.`event` "+ 
	//			" WHERE t.`create_by`='1' AND t.`del_flag`='0' AND (t.`comment` LIKE '差评事件已改成好评%' OR t.`comment` LIKE '%差评跟帖%' OR t.`comment` LIKE '%差评帖已删除%' OR t.comment LIKE '%事件发生了改变%') "+
		//		" AND DATE_FORMAT(t.`create_date`,'%Y-%m-%d')=DATE_FORMAT(NOW(),'%Y-%m-%d') AND g.`country` IS NOT NULL ";
		String sql="SELECT distinct g.`country`,g.id FROM custom_event_manager g "+
			" WHERE g.`create_date`>=:p1 and g.`create_date`<=:p2 AND g.type='8' AND g.`state` IN ('0','1') "+
			" AND NOT EXISTS (SELECT 1 FROM custom_event_comment t WHERE g.id=t.`event` AND t.`create_by`='1' AND t.`del_flag`='0' AND t.`comment` LIKE '%Review Order add comment%'  )  ";
        List<Object[]> list=eventDao.findBySql(sql,new Parameter(DateUtils.addMonths(new Date(), -3),DateUtils.addMonths(new Date(), -1)));
        Map<String,String>  map=Maps.newHashMap();
        for (Object[] obj : list) {
        	String eventStr=map.get(obj[0].toString());
        	String eventLink=("<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/event/form?id="+obj[1].toString()+"'>SPR_"+obj[1].toString()+"</a>");
        	map.put(obj[0].toString(),(eventStr==null?"":eventStr+",")+eventLink);
		}
		return map;
	}
	
	public List<String> findReviewLink(String asin,String country){
		String sql="SELECT distinct review_link FROM custom_event_manager r WHERE r.`type`='8' AND r.`remarks`=:p1 and r.country=:p2 AND r.`review_link` LIKE '%amazon.%review%' AND del_flag='0' ";
		return eventDao.findBySql(sql,new Parameter(asin,country));
	}
	
	public Map<Integer,String> findEventId(String customId){
		Map<Integer,String> map=Maps.newHashMap();
		String sql="SELECT r.id,u.email FROM custom_event_manager r join sys_user u on r.master_by=u.id and u.del_flag='0' WHERE r.custom_id=:p1 AND r.invoice_number='not find oderID' ";
		List<Object[]> list=eventDao.findBySql(sql,new Parameter(customId));
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				map.put(Integer.parseInt(obj[0].toString()),(obj[1]==null?"":obj[1].toString()));
			}
		}
		return map;
	}
	
	public Map<Integer,String> updateEventOrder(){
		Map<Integer,String> map=Maps.newHashMap();
		String sql="SELECT a.id,u.email,m.`buyer_email`,m.`buyer_name`,GROUP_CONCAT(b.`amazon_order_id`) FROM custom_event_manager a "+
				"	JOIN amazoninfo_order_extract b ON a.`custom_id` = b.`custom_id` "+
				"	JOIN sys_user u ON a.master_by=u.id AND u.del_flag='0'  "+
				"	JOIN amazoninfo_order m ON b.`amazon_order_id`=m.`amazon_order_id` "+
				"	WHERE  a.`type` = '1' AND a.`invoice_number` = 'not find oderID' and m.`buyer_email` is not null "+
				"	AND a.`create_date`>=:p1 GROUP BY a.`id`,u.email,m.`buyer_email`,m.`buyer_name`";
		List<Object[]> list=eventDao.findBySql(sql,new Parameter(DateUtils.addMonths(new Date(),-6)));					
		for (Object[] obj: list) {
			map.put(Integer.parseInt(obj[0].toString()),(obj[1]==null?"":obj[1].toString())+";;"+obj[2].toString()+";;"+obj[3].toString()+";;"+obj[4].toString());
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void updateEndDate(Integer id,String email,String name,String orderId){
		String sql="update custom_event_manager set custom_name=:p1,custom_email=:p2,invoice_number=:p3  where id = :p4";
		eventDao.updateBySql(sql, new Parameter(name,email,orderId,id));
	}
	
	public Map<String,List<Event>> findScanFAQEvent(){
		Map<String,List<Event>> map=Maps.newHashMap();
		DetachedCriteria dc = eventDao.createDetachedCriteria();
		dc.add(Restrictions.and(Restrictions.ge("createDate",DateUtils.addDays(new Date(),-30))));
		dc.add(Restrictions.eq("type", "6"));
		dc.add(Restrictions.or(Restrictions.eq("state", "1"),Restrictions.eq("state", "2")));
		dc.add(Restrictions.eq(Event.FIELD_DEL_FLAG, Event.DEL_FLAG_NORMAL));
		dc.add(Restrictions.isNotNull("country"));
		List<Event> list=eventDao.find(dc);
		for (Event event : list) {
		   Hibernate.initialize(event.getComments());
		}
		for(Event e:list){
			List<Event> eventList=map.get(e.getCountry());
			if(eventList==null){
				eventList=Lists.newArrayList();
				map.put(e.getCountry(), eventList);
			}
			eventList.add(e);
		}
		return map;
	}
	
	public List<Event> findOutTimeEvent(Event event){
		String sql="SELECT r.id,r.`type`,r.`subject`,t.`name`,r.`create_date`,r.`answer_date`,ROUND(TIMESTAMPDIFF(SECOND,r.create_date,IFNULL(r.answer_date,NOW()))/3600,2)dates,r.`state`,(CASE WHEN r.`account_name` IS NULL OR r.`account_name`='' THEN r.`country`  ELSE r.`account_name` END) account "+
				" FROM custom_event_manager r  JOIN sys_user t ON r.`master_by`=t.id AND t.`del_flag`='0' "+
				" WHERE r.`create_date`>=:p1 and r.`create_date`<:p2  AND r.`type` IN ('1','2','6') AND ROUND(TIMESTAMPDIFF(SECOND,r.create_date,IFNULL(r.answer_date,NOW()))/3600,2)>24 ";
		List<Object[]> list = eventDao.findBySql(sql,new Parameter(event.getCreateDate(),DateUtils.addDays(event.getEndDate(),1)));
		List<Event> rs=Lists.newArrayList();
		for (Object[] obj: list) {
			Event e = new Event();
			if(obj[5]==null&&"2".equals(obj[7].toString())){
				continue;
			}
			e.setId(Integer.parseInt(obj[0].toString()));
			e.setType(obj[1].toString());
			e.setSubject(obj[2].toString());
			e.setCustomName(obj[3].toString());
			e.setCreateDate((Date) obj[4]);
			e.setAnswerDate((Date) obj[5]);
			e.setPriority(obj[6].toString());
			e.setState(obj[7].toString());
			e.setAccountName(obj[8].toString());
			rs.add(e);
		}
		return rs;
	}
}
