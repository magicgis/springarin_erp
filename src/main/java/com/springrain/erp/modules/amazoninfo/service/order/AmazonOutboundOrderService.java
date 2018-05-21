package com.springrain.erp.modules.amazoninfo.service.order;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cxf.endpoint.Client;
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
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.order.AmazonOutboundOrderDao;
import com.springrain.erp.modules.amazoninfo.dao.order.AmazonUnlineOrderDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOutboundAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOutboundOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOutboundOrderItem;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.custom.entity.Comment;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.service.CommentService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.webservice.OutboundOrder;

/**
 * 亚马逊FBA发货订单
 * @author Tim
 * @version 2016-08-24
 */


@Component
@Transactional(readOnly = true)
public class AmazonOutboundOrderService extends BaseService {

	@Autowired
	private AmazonOutboundOrderDao amazonOutboundOrderDao;
	@Autowired
	private EventService eventService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private MfnOrderService mfnOrderService;
	@Autowired
	private AmazonUnlineOrderDao unlineOrderDao;
	@Autowired
	private AmazonOrderService amazonOrderService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	private final static Logger logger = LoggerFactory.getLogger(AmazonOutboundOrderService.class);
	@Transactional(readOnly = false)
    public  void save(List<AmazonOutboundOrder> amazonOutboundOrders){
		amazonOutboundOrderDao.save(amazonOutboundOrders);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getOrderIds(List<String> countrySuffix){
		Object[] params = {countrySuffix};
		Parameter parameter =new Parameter(params);
		return amazonOutboundOrderDao.createSqlQuery("select seller_order_id from amazoninfo_outbound_order  where order_status not in ('Canceled','Draft','CANCELLED') and seller_order_id not like 'MFN-%' and amazon_order_id is null  and country in :p1 ", parameter).list();
	}
	
	
	public List<Object[]> countOrder(AmazonOutboundOrder amazonOutboundOrder){
		String sql="SELECT o.`country`,CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END) AS NAME,o.`order_type`,SUM(t.`quantity_ordered`) "+
		"	FROM amazoninfo_outbound_order o JOIN amazoninfo_outbound_orderitem t ON o.id=t.`order_id` "+
		"	where o.`create_date`>=:p1 and o.`create_date`<:p2 "+
		"	GROUP BY o.`country`,NAME,o.`order_type` ";	
		return amazonOutboundOrderDao.findBySql(sql,new Parameter(amazonOutboundOrder.getCreateDate(),DateUtils.addDays(amazonOutboundOrder.getLastUpdateDate(), 1)));
	}
	
	public boolean isExistOrder(String country,String amazonOrderId){
		if("de,fr,it,es,uk".contains(country)){
			String sql="select 1 from amazoninfo_outbound_order where country in :p1 and seller_order_id=:p2 ";
			List<String> list=amazonOutboundOrderDao.findBySql(sql,new Parameter(Sets.newHashSet("de","fr","it","es","uk"),amazonOrderId));
			if(list!=null&&list.size()>0){
				return false;
			}
			return true;
		}else{
			String sql="select 1 from amazoninfo_outbound_order where country=:p1 and seller_order_id=:p2 ";
			List<String> list=amazonOutboundOrderDao.findBySql(sql,new Parameter(country,amazonOrderId));
			if(list!=null&&list.size()>0){
				return false;
			}
			return true;
		}
		
	}
	
	public List<AmazonOutboundOrder> findAllCompleteOrders(){
		DetachedCriteria dc = amazonOutboundOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("orderStatus","Complete"));
		dc.add(Restrictions.eq("orderType","Support"));
		dc.add(Restrictions.isNotNull("customId"));
		Date date=new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		dc.add(Restrictions.ge("createDate",DateUtils.addDays(date,-30)));
		List<AmazonOutboundOrder> rs = amazonOutboundOrderDao.find(dc);
		for (AmazonOutboundOrder amazonOutboundOrder : rs) {
			Hibernate.initialize(amazonOutboundOrder.getItems());
		}
		return rs;
	}
	
	
	public boolean isExistCommentOrder(String orderId,String type){
		String sql="SELECT count(1) FROM amazoninfo_buy_comment t WHERE t.`type`=:p1 AND t.`order_id`=:p2";
		int num=((BigInteger)amazonOutboundOrderDao.findBySql(sql, new Parameter(type,orderId)).get(0)).intValue();
		if(num>0){
			return false;
		}
		return true;
	}
	
	public Map<String,List<AmazonOutboundOrder>> findAllOrders(){
		Map<String,List<AmazonOutboundOrder>> map=Maps.newHashMap();
		DetachedCriteria dc = amazonOutboundOrderDao.createDetachedCriteria();
		dc.add(Restrictions.not(Restrictions.in("orderStatus",Sets.newHashSet("Canceled","Complete","Draft","CANCELLED"))));
		dc.add(Restrictions.in("accountName", amazonAccountConfigService.findAccountName()));
		List<AmazonOutboundOrder> rs = amazonOutboundOrderDao.find(dc);
		for (AmazonOutboundOrder amazonOutboundOrder : rs) {
			Hibernate.initialize(amazonOutboundOrder.getItems());
			Hibernate.initialize(amazonOutboundOrder.getShippingAddress());
			Hibernate.initialize(amazonOutboundOrder.getCreateUser());
			Hibernate.initialize(amazonOutboundOrder.getShipmentItems());
			List<AmazonOutboundOrder> tempList=map.get(amazonOutboundOrder.getCountry());
			if(tempList==null){
				tempList=Lists.newArrayList();
				map.put(amazonOutboundOrder.getCountry(), tempList);
			}
			tempList.add(amazonOutboundOrder);
		}
		return map;
	}
	
	
	public List<AmazonOutboundOrder> findAllDraftMFNOrders(){
		DetachedCriteria dc = amazonOutboundOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("orderStatus","Draft"));
		dc.add(Restrictions.like("sellerOrderId","MFN-%"));
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2016-11-02");
			dc.add(Restrictions.ge("createDate",date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<AmazonOutboundOrder> rs = amazonOutboundOrderDao.find(dc);
		for (AmazonOutboundOrder amazonOutboundOrder : rs) {
			Hibernate.initialize(amazonOutboundOrder.getItems());
			Hibernate.initialize(amazonOutboundOrder.getShippingAddress());
		}
		return rs;
	}
	
	
	@Transactional(readOnly = false)
    public  void save(AmazonOutboundOrder amazonOutboundOrder){
		//amazonOutboundOrderDao.save(amazonOutboundOrder);
		if(amazonOutboundOrder.getId()!=null&&amazonOutboundOrder.getId()>0){
			amazonOutboundOrderDao.getSession().merge(amazonOutboundOrder); //用库里查出来的保存
		}else{
			amazonOutboundOrderDao.save(amazonOutboundOrder);
		}
	}
	
	@Transactional(readOnly = false)
    public  void cancelOrderAndEvent(AmazonOutboundOrder amazonOutboundOrder){
		amazonOutboundOrder.setOrderStatus("CANCELLED");
		amazonOutboundOrder.setCancelDate(new Date());
		amazonOutboundOrder.setCancelUser(UserUtils.getUser());
		amazonOutboundOrder.setLastUpdateDate(new Date());
		if(amazonOutboundOrder.getId()!=null&&amazonOutboundOrder.getId()>0){
			amazonOutboundOrderDao.getSession().merge(amazonOutboundOrder); //用库里查出来的保存
		}else{
			amazonOutboundOrderDao.save(amazonOutboundOrder);
		}
        if(amazonOutboundOrder.getSellerOrderId().startsWith("MFN-")&&StringUtils.isNotBlank(amazonOutboundOrder.getEventId())){
        	//eventService.delete(amazonOutboundOrder.getEvent().getId());
        	String[] idArr=amazonOutboundOrder.getEventId().split(",");
        	List<Event> eventList=Lists.newArrayList();
        	for (String eventId : idArr) {
        		Event event=eventService.get(Integer.parseInt(eventId));
    			event.setEndDate(new Date());
    			event.setState("4");
    			
			}
        	if(eventList!=null&&eventList.size()>0){
        		eventService.save(eventList);
        	}
        	try{
        		String sql="update amazoninfo_ebay_order set status='9' WHERE order_id=:p1 ";
        		amazonOutboundOrderDao.updateBySql(sql, new Parameter(amazonOutboundOrder.getSellerOrderId()));
        		//mfnOrderService.cancelOrder(amazonOutboundOrder.getSellerOrderId());
        	}catch(Exception e){
        		 logger.error("取消MFN Order异常",e.getMessage());
        	}
        	
		}
	}
	
	@Transactional(readOnly = false)
    public  void save2(AmazonOutboundOrder amazonOutboundOrder,List<Event> eventList,String ratingEventId){
		if(amazonOutboundOrder.getId()!=null&&amazonOutboundOrder.getId()>0){
			amazonOutboundOrderDao.getSession().merge(amazonOutboundOrder); //用库里查出来的保存
		}else{
			if(eventList!=null&&eventList.size()>0){
				eventService.save(eventList);
				String eventId="";
				List<Comment> commentList=Lists.newArrayList();
				for (Event event: eventList) {
					eventId+=event.getId()+",";
					
					Event tempEvent=new Event();
					tempEvent.setId(event.getId());
					Comment comm = new Comment();
					comm.setComment(UserUtils.getUser().getName()+"添加本地自发货订单(Add MFN Order)<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazonAndEbay/mfnOrder/form2?orderId="+amazonOutboundOrder.getSellerOrderId()+"'>"+amazonOutboundOrder.getSellerOrderId()+"</a>");
					comm.setType("1");
					comm.setEvent(tempEvent);
					comm.setCreateBy(UserUtils.getUser());
					comm.setCreateDate(new Date());
					comm.setUpdateBy(UserUtils.getUser());
					comm.setUpdateDate(new Date());
					commentList.add(comm);
					
				}
				if(commentList!=null&&commentList.size()>0){
					commentService.save(commentList);
				}
				amazonOutboundOrder.setEventId(eventId.substring(0,eventId.lastIndexOf(",")));
				if(StringUtils.isNotBlank(ratingEventId)){
					try{
						amazonOutboundOrder.setRemark(amazonOutboundOrder.getRemark()+"<div style='display:none'>"+eventId.substring(0,eventId.lastIndexOf(","))+"</div>");
						Comment comm = new Comment();
						String common = "Replacement sent by "+UserUtils.getUser().getName()+" <a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/event/form?id="+eventList.get(0).getId()+"' target='blank'>SPR-"+eventList.get(0).getId()+"</a>";
						comm.setComment(common);
						comm.setType("0");
						comm.setCreateBy(UserUtils.getUser());
						comm.setEvent(eventService.get(Integer.parseInt(ratingEventId)));
						commentService.save(comm);
					}catch(Exception e){}
				}
			}
			
			amazonOutboundOrderDao.save(amazonOutboundOrder);
		}
	}
	
	@Transactional(readOnly = false)
    public String createSave(AmazonOutboundOrder amazonOutboundOrder,List<Event> eventList,String ratingEventId,final AmazonAccountConfig config){
		if("Draft".equals(amazonOutboundOrder.getOrderStatus())){
			amazonOutboundOrder.setOrderStatus("PENGING");
		}
		String rs = "";
		
		try {//orderJson
			String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"),toJson(amazonOutboundOrder),amazonOutboundOrder.getAccountName()};
			Object[] res= client.invoke("createFbaOutBoundOrder", str);
			rs = (String)res[0];
		} catch (Exception e1) {
			rs = e1.getMessage();
		}
		
		if(StringUtils.isBlank(rs)){
			String eventId="";
			StringBuffer buf= new StringBuffer();
			if(eventList!=null&&eventList.size()>0){
				eventService.save(eventList);
				for (Event event: eventList) {
					buf.append(event.getId()+",");
				}
				eventId = buf.toString();
				amazonOutboundOrder.setEventId(eventId.substring(0,eventId.lastIndexOf(",")));
			}
			if(StringUtils.isNotBlank(ratingEventId)){
				try{
					amazonOutboundOrder.setRemark(amazonOutboundOrder.getRemark()+"<div style='display:none'>"+eventId.substring(0,eventId.lastIndexOf(","))+"</div>");
					Comment comm = new Comment();
					String common = "Replacement sent by "+UserUtils.getUser().getName()+" <a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/event/form?id="+eventList.get(0).getId()+"' target='blank'>SPR-"+eventList.get(0).getId()+"</a>";
					comm.setComment(common);
					comm.setType("0");
					comm.setCreateBy(UserUtils.getUser());
					comm.setEvent(eventService.get(Integer.parseInt(ratingEventId)));
					commentService.save(comm);
				}catch(Exception e){}
			}
			save(amazonOutboundOrder);
			
			if(StringUtils.isNotBlank(amazonOutboundOrder.getOldOrderId())){
				try{
					final Set<String> orderIdSet=Sets.newHashSet(amazonOutboundOrder.getOldOrderId());
					final String country=amazonOutboundOrder.getCountry();
					final String orderType=amazonOutboundOrder.getOrderType();
					final String sellerOrderId=amazonOutboundOrder.getSellerOrderId();
					final String orderId=amazonOutboundOrder.getOldOrderId();
					new Thread(){
						public void run() {
							mfnOrderService.updateStatus(orderIdSet);
							if("AmzMfn".equals(orderType)){
								mfnOrderService.updateShippedByOrders(orderIdSet, country,config);
							}
						}
			       }.start();
			       
			       if(sellerOrderId.startsWith("Ebay-")||sellerOrderId.startsWith("DZW-")){
						new Thread(){
							public void run() {
								mfnOrderService.updateOrderShippedInEbay2(orderId,country) ;
							}
				       }.start();
					}
			       
				}catch(Exception e){logger.error("更新Amazon订单发货状态异常",e.getMessage());}
			}
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void updateWebsiteStatu(String oldOrderId){
		try{//
			String sql="UPDATE amazoninfo_unline_order r SET r.`order_status`='Shipped' WHERE r.`amazon_order_id`=:p1 ";
			unlineOrderDao.updateBySql(sql, new Parameter(oldOrderId));
		}catch(Exception e){logger.error("更新官网订单发货状态异常",e.getMessage());}
	}
	
	@Transactional(readOnly = false)
	public void updateStatu(String oldOrderId){
		try{//
			String sql="UPDATE amazoninfo_ebay_order r SET r.`status`='1' WHERE r.`order_id`=:p1 ";
			unlineOrderDao.updateBySql(sql, new Parameter(oldOrderId));
		}catch(Exception e){logger.error("更新发货状态异常",e.getMessage());}
	}
	
	@Transactional(readOnly = false)
    public  String cancelSave(AmazonOutboundOrder amazonOutboundOrder,AmazonAccountConfig config){
		String rs=""; 
		
		try {
			String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"),amazonOutboundOrder.getSellerOrderId(),amazonOutboundOrder.getAccountName()};
			Object[] res= client.invoke("cancelFbaOutBoundOrder", str);
			rs = (String)res[0];
		} catch (Exception e1) {
			rs = e1.getMessage();
		}
		
		
		if(StringUtils.isBlank(rs)){
			amazonOutboundOrder.setOrderStatus("CANCELLED");
			amazonOutboundOrder.setCancelDate(new Date());
			amazonOutboundOrder.setCancelUser(UserUtils.getUser());
			amazonOutboundOrder.setLastUpdateDate(new Date());
		    if(StringUtils.isNotBlank(amazonOutboundOrder.getEventId())){
		    	String[] idArr=amazonOutboundOrder.getEventId().split(",");
	        	List<Event> eventList=Lists.newArrayList();
	        	for (String eventId : idArr) {
	        		Event event=eventService.get(Integer.parseInt(eventId));
	    			event.setEndDate(new Date());
	    			event.setState("4");
	    			
				}
	        	if(eventList!=null&&eventList.size()>0){
	        		eventService.save(eventList);
	        	}
		    }
			

			save(amazonOutboundOrder);
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
    public  String updateSave(AmazonOutboundOrder amazonOutboundOrder,AmazonAccountConfig config){
		amazonOutboundOrder.setFulfillmentAction("Ship");
        String rs=""; 
		try {
			String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"),amazonOutboundOrder.getId(),amazonOutboundOrder.getAccountName()};
			Object[] res= client.invoke("updateFbaOutBoundOrder", str);
			rs = (String)res[0];
		} catch (Exception e1) {
			rs = e1.getMessage();
		}
		
		if(StringUtils.isBlank(rs)){
			amazonOutboundOrder.setCheckDate(new Date());
			amazonOutboundOrder.setCheckUser(UserUtils.getUser());
			amazonOutboundOrder.setLastUpdateDate(new Date());
			save(amazonOutboundOrder);
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void  updateAmazonOrderId(String amazonOrderId,String sellerOrderId) {
		Object[] str = {amazonOrderId,sellerOrderId};
		Parameter parameter =new Parameter(str);
		amazonOutboundOrderDao.updateBySql("update amazoninfo_outbound_order set amazon_order_id=:p1 WHERE seller_order_id = :p2", parameter);
	}
	
	
	public AmazonOutboundOrder get(Integer id) {
		return amazonOutboundOrderDao.get(id);
	}
	
	public AmazonOutboundOrder getOrderByOrderId(String sellerOrderId) {
		DetachedCriteria dc = amazonOutboundOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("sellerOrderId", sellerOrderId));
		dc.addOrder(Order.desc("createDate"));
		List<AmazonOutboundOrder> rs = amazonOutboundOrderDao.find(dc);
		if(rs.size()>0){
			AmazonOutboundOrder order =  rs.get(0);
			Hibernate.initialize(order.getItems());
			return order;
		}
		return null;
	}
	
	public AmazonOutboundOrder getOrderByOldOrderId(String sellerOrderId) {
		DetachedCriteria dc = amazonOutboundOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("oldOrderId",sellerOrderId.substring(sellerOrderId.indexOf("-")+1)));
		dc.addOrder(Order.desc("createDate"));
		List<AmazonOutboundOrder> rs = amazonOutboundOrderDao.find(dc);
		if(rs.size()>0){
			AmazonOutboundOrder order =  rs.get(0);
			Hibernate.initialize(order.getItems());
			return order;
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void delete(Set<Integer> id){
	  String sql="delete from amazoninfo_outbound_orderitem where id in :p1";
	  amazonOutboundOrderDao.updateBySql(sql, new Parameter(id));
	}
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonOutboundOrderService.class);

	public Integer findSupport(String amazonOrderId){
		String sql="select count(*) from amazoninfo_outbound_order r where old_order_id=:p1";
		List<Object> list=amazonOutboundOrderDao.findBySql(sql,new Parameter(amazonOrderId));
		if(list!=null&&list.size()>0&&list.get(0)!=null){
			return Integer.parseInt(list.get(0).toString());
		}
		return 0;
	}
	
	public Page<AmazonOutboundOrder> find(Page<AmazonOutboundOrder> page,AmazonOutboundOrder amazonOutboundOrder) {
		DetachedCriteria dc = amazonOutboundOrderDao.createDetachedCriteria();
		dc.createAlias("this.items", "item");
		
		if (amazonOutboundOrder.getCreateDate() != null) {
				dc.add(Restrictions.ge("createDate", amazonOutboundOrder.getCreateDate()));
		}
		if (amazonOutboundOrder.getLastUpdateDate() != null) {
				dc.add(Restrictions.le("createDate", DateUtils.addDays(amazonOutboundOrder.getLastUpdateDate(),1)));
		}
		if(StringUtils.isNotEmpty(amazonOutboundOrder.getCountry())){
				dc.add(Restrictions.eq("country",amazonOutboundOrder.getCountry()));
		}
		if(StringUtils.isNotBlank(amazonOutboundOrder.getOrderStatus())){
				dc.add(Restrictions.eq("orderStatus", amazonOutboundOrder.getOrderStatus()));
		}
		if(StringUtils.isNotBlank(amazonOutboundOrder.getSellerOrderId())){
			dc.add(Restrictions.or(Restrictions.like("item.productName","%"+amazonOutboundOrder.getSellerOrderId()+"%"),
					Restrictions.like("item.sellersku","%"+amazonOutboundOrder.getSellerOrderId()+"%"),
					Restrictions.eq("buyerEmail", amazonOutboundOrder.getSellerOrderId()),Restrictions.eq("buyerName", amazonOutboundOrder.getSellerOrderId()),
					Restrictions.like("sellerOrderId", "%"+amazonOutboundOrder.getSellerOrderId()+"%") )); 
		}
		if(StringUtils.isNotBlank(amazonOutboundOrder.getFulfillmentAction())){
			dc.add(Restrictions.eq("fulfillmentAction", amazonOutboundOrder.getFulfillmentAction()));
		}
		if (amazonOutboundOrder.getCreateUser()!=null && StringUtils.isNotEmpty(amazonOutboundOrder.getCreateUser().getId())){
			dc.add(Restrictions.eq("createUser", amazonOutboundOrder.getCreateUser()));
		}
		if(StringUtils.isNotBlank(amazonOutboundOrder.getOrderType())){
			dc.createAlias("this.createUser", "createUser");
			if("0".equals(amazonOutboundOrder.getOrderType())){//多渠道
				dc.add(Restrictions.or(Restrictions.not(Restrictions.like("sellerOrderId","MFN-%")),Restrictions.eq("createUser.name","管理员")));
			}else{
				dc.add(Restrictions.and(Restrictions.like("sellerOrderId","MFN-%"),Restrictions.ne("createUser.name","管理员")));
			}
		}
		if(StringUtils.isNotBlank(amazonOutboundOrder.getFlag())){
			dc.add(Restrictions.eq("orderType",amazonOutboundOrder.getFlag()));
		}
	/*	if(StringUtils.isBlank(amazonOutboundOrder.getOrderStatus())){
			dc.add(Restrictions.ne("orderStatus","Canceled"));
		}*/
		dc.add(Restrictions.ne("orderType","Paypal_Refund"));
		dc.addOrder(Order.desc("createDate"));
		return amazonOutboundOrderDao.find(page, dc);
	}
	
	
	public AmazonOutboundAddress findByName(String name){
		String sql="SELECT s.NAME,address_line1,address_line2,address_line3,s.city,s.country,district,state_or_region,postal_code,country_code,phone,r.`custom_id`,r.buyer_email,r.buyer_name  FROM amazoninfo_outbound_address s JOIN amazoninfo_outbound_order r ON s.id=r.`shipping_address` WHERE NAME LIKE :p1 ORDER BY s.id ASC  ";
		List<Object[]> list=amazonOutboundOrderDao.findBySql(sql,new Parameter("%"+name+"%"));
		if(list!=null&&list.size()>0){
			Object[] obj=list.get(0);
			AmazonOutboundAddress addr=new AmazonOutboundAddress();
			addr.setName(obj[0].toString());
			addr.setAddressLine1(obj[1]==null?"":obj[1].toString());
			addr.setAddressLine2(obj[2]==null?"":obj[2].toString());
			addr.setAddressLine3(obj[3]==null?"":obj[3].toString());
			addr.setCity(obj[4]==null?"":obj[4].toString());
			addr.setCountryCode(obj[5]==null?"":obj[5].toString());
			addr.setDistrict(obj[6]==null?"":obj[6].toString());
			addr.setStateOrRegion(obj[7]==null?"":obj[7].toString());
			addr.setPostalCode(obj[8]==null?"":obj[8].toString());
			addr.setCountryCode(obj[9]==null?"":obj[9].toString());
			addr.setPhone(obj[10]==null?"":obj[10].toString());
			addr.setCustomId(obj[11]==null?"":obj[11].toString());
			addr.setBuyerEmail(obj[12]==null?"":obj[12].toString());
			addr.setBuyerName(obj[13]==null?"":obj[13].toString());
			return addr;
		}
		return null;
	}
	
	
	
		/**
		 * 统计所有产品的销量
		 * @return Map<String,Integer> [productName qty]
		 */
		public Map<String, Integer> getAllSalesVolume(){
			Map<String,Integer> rs = Maps.newLinkedHashMap();
			String sql="SELECT i.`product_name`,i.`color`,SUM(i.`quantity_ordered`)"+
					" FROM `amazoninfo_outbound_order` t,`amazoninfo_outbound_orderitem` i "+
					" WHERE i.`order_id`=t.`id` AND t.`order_status`='COMPLETE' GROUP BY i.`product_name`,i.`color`";
			List<Object[]> list = amazonOutboundOrderDao.findBySql(sql);
			for(Object[] obj:list){
				String productName = obj[0].toString();
				String color = obj[1]==null?"":obj[1].toString();
				if (StringUtils.isNotEmpty(color)) {
					productName = productName + "_" + color;
				}
				Integer quantity = obj[2]==null?0:Integer.parseInt(obj[2].toString());
				rs.put(productName, quantity);
		    }
			return rs;
		}
	 
	  public Integer findSkuQuantity(Date date,String sku){
		  String sql="SELECT SUM(t.`quantity_ordered`) FROM amazoninfo_outbound_order r JOIN amazoninfo_outbound_orderitem t ON r.`id`=t.`order_id` "+
               " WHERE (seller_order_id NOT LIKE 'MFN-%' OR (r.`create_user`=1)) AND r.order_status ='COMPLETE' AND t.`sellersku`=:p1 AND r.`create_date`>=:p2 ";
		  List<Object> list = amazonOutboundOrderDao.findBySql(sql,new Parameter(sku,date));
		  if(list!=null&&list.size()>0&&list.get(0)!=null){
				return Integer.parseInt(list.get(0).toString());
		  }
		  return 0;
	  }
	  
	  @Transactional(readOnly = false)
	  public  void updateFlag(Set<String> orderId){
			String sql="UPDATE amazoninfo_outbound_order SET flag='1' where old_order_id in :p1";
			amazonOutboundOrderDao.updateBySql(sql, new Parameter(orderId));
	  }
	  
	  @Transactional(readOnly = false)
	  public  void updateOrderFlag(Set<String> orderId){
			String sql="UPDATE amazoninfo_outbound_order SET flag='1' where seller_order_id in :p1";
			amazonOutboundOrderDao.updateBySql(sql, new Parameter(orderId));
	  }
	  
	  public boolean isExistOrder(String orderId){
			String sql="select count(1) from amazoninfo_outbound_order where seller_order_id = :p1 ";
			List<Object> count=amazonOutboundOrderDao.findBySql(sql,new Parameter("AmzMfn-"+orderId));
			return ((BigInteger)count.get(0)).intValue()>0;
		}
	  
	  @Transactional(readOnly = false)
	    public synchronized void updateAndSaveAmazon(List<AmazonOrder> mfn){
			 for (AmazonOrder amazonOrder : mfn) {
				String orderStatu="Draft";
				if("Unshipped".equals(amazonOrder.getOrderStatus())){//UnShipped,Shipped,Canceled
					orderStatu="Draft";
				}else{
					orderStatu="CANCELLED";
				}
				if(!isExistOrder(amazonOrder.getAmazonOrderId())){//不存在
					//if("0".equals(orderStatu)){
						AmazonOutboundAddress shippingAddress=new AmazonOutboundAddress();
						shippingAddress.setName(amazonOrder.getShippingAddress().getName());
						shippingAddress.setAddressLine1(amazonOrder.getShippingAddress().getAddressLine1());
						shippingAddress.setAddressLine2(amazonOrder.getShippingAddress().getAddressLine2());
						shippingAddress.setAddressLine3(amazonOrder.getShippingAddress().getAddressLine3());
						shippingAddress.setCity(amazonOrder.getShippingAddress().getCity());
						shippingAddress.setCountry(amazonOrder.getShippingAddress().getCounty());
						shippingAddress.setStateOrRegion(amazonOrder.getShippingAddress().getStateOrRegion());
						shippingAddress.setCountryCode(amazonOrder.getShippingAddress().getCountryCode());
						shippingAddress.setPostalCode(amazonOrder.getShippingAddress().getPostalCode());
						shippingAddress.setPhone(amazonOrder.getShippingAddress().getPhone());
						
						AmazonOutboundOrder mfnOrder =new AmazonOutboundOrder();
						mfnOrder.setSellerOrderId("AmzMfn-"+amazonOrder.getAmazonOrderId());
						mfnOrder.setLastUpdateDate(amazonOrder.getLastUpdateDate());
						mfnOrder.setOrderStatus(orderStatu);
						mfnOrder.setShippingSpeedCategory("Standard");
						mfnOrder.setOrderType("AmzMfn");
						mfnOrder.setDisplayableOrderComment(AmazonOutboundOrder.displayableOrderCommentMap.get(amazonOrder.getCountryChar()));
						mfnOrder.setShippingAddress(shippingAddress);
						mfnOrder.setCountry(amazonOrder.getCountryChar());
						mfnOrder.setBuyerEmail(amazonOrder.getBuyerEmail());
						mfnOrder.setBuyerName(amazonOrder.getBuyerName());
						mfnOrder.setLatestShipDate(amazonOrder.getLatestShipDate());
						mfnOrder.setEarliestShipDate(amazonOrder.getEarliestShipDate());
						mfnOrder.setLatestDeliveryDate(mfnOrder.getLatestDeliveryDate());
						mfnOrder.setEarliestDeliveryDate(mfnOrder.getEarliestDeliveryDate());
						mfnOrder.setCustomId(amazonOrderService.getCustomIdByOrderId(amazonOrder.getAmazonOrderId()));
						mfnOrder.setCreateDate(amazonOrder.getPurchaseDate());
						mfnOrder.setCreateUser(new User("1"));
						mfnOrder.setRemark("Amazon Order");
						mfnOrder.setOldOrderId(amazonOrder.getAmazonOrderId());
						mfnOrder.setFulfillmentAction("Ship");
						
						List<AmazonOutboundOrderItem> items = Lists.newArrayList();
						for (AmazonOrderItem item : amazonOrder.getItems()) {
							AmazonOutboundOrderItem orderItem=new AmazonOutboundOrderItem();
							orderItem.setSellersku(item.getSellersku());
							orderItem.setProductName(item.getProductName());
							orderItem.setColor(item.getColor());
							orderItem.setOrder(mfnOrder);
							orderItem.setAsin(item.getAsin());
							orderItem.setQuantityOrdered(item.getQuantityOrdered());
							items.add(orderItem);
						}
						mfnOrder.setItems(items);
						amazonOutboundOrderDao.save(mfnOrder);
					//}
				}
			}
		}
	  
	  public Float findReviewRefund(String orderId){
		  String sql="select amazon_fee from amazoninfo_outbound_order where amazon_order_id=:p1 ";
		  List<Object> list=amazonOutboundOrderDao.findBySql(sql,new Parameter(orderId));
		  if(list!=null&&list.size()>0){
			return ((BigDecimal)(list.get(0)==null?new BigDecimal(0):list.get(0))).floatValue();
		  }
		  return 0f;
	  }
	  
	  public List<String> findReviewRefund(Set<String> orderId){
		  String sql="select amazon_order_id from amazoninfo_outbound_order where amazon_order_id in :p1 ";
		  return amazonOutboundOrderDao.findBySql(sql,new Parameter(orderId));
	  }
	  
	    @Transactional(readOnly = false)
		public void updateDate(Map<String,AmazonOrder> orderMap){
			String updateSql="update amazoninfo_outbound_order set create_date=:p1 where amazon_order_id=:p2";
			for (Map.Entry<String,AmazonOrder>  entry: orderMap.entrySet()) {
				amazonOutboundOrderDao.updateBySql(updateSql,new Parameter(entry.getValue().getPurchaseDate(),entry.getKey()));
			}
		}
	  
	    @Transactional(readOnly = false)
		public void updateDate(Set<String> orderId){
	    	Date date=DateUtils.getLastDayOfMonth(DateUtils.addMonths(new Date(),-1));
			String updateSql="update amazoninfo_outbound_order set create_date=:p1 where amazon_order_id in :p2";
			amazonOutboundOrderDao.updateBySql(updateSql,new Parameter(date,orderId));
		}
	    

	    private String toJson(AmazonOutboundOrder order){
			//String strObject="{\"first\":{\"address\":\"中国上海\",\"age\":\"23\",\"name\":\"JSON\"}}";
			String orderRs = "\"sellerOrderId\":\""+order.getSellerOrderId()+"\",\"displayableOrderComment\":\""+order.getDisplayableOrderComment()+"\",\"shippingSpeedCategory\":\""+order.getShippingSpeedCategory()+"\",\"fulfillmentAction\":\""+order.getFulfillmentAction()+"\",\"buyerEmail\":\""+order.getBuyerEmail()+"\"";
			String items = "";
			for (AmazonOutboundOrderItem item : order.getItems()) {
				items +="{\"sku\":\""+item.getSellersku()+"\",\"quantity\":\""+item.getQuantityOrdered()+"\"},";
			}
			items = items.substring(0,items.length()-1);
			AmazonOutboundAddress orderAddr = order.getShippingAddress();
			String address = "{\"name\":\""+orderAddr.getName()+"\",\"addressLine1\":\""+orderAddr.getAddressLine1()+"\",\"stateOrRegion\":\""+orderAddr.getStateOrRegion()+"\",\"countryCode\":\""+orderAddr.getCountryCode()+"\",\"postalCode\":\""+orderAddr.getPostalCode()+"\",\"city\":\""+orderAddr.getCity()+"\",\"district\":\""+orderAddr.getDistrict()+"\",\"addressLine2\":\""+orderAddr.getAddressLine2()+"\",\"addressLine3\":\""+orderAddr.getAddressLine3()+"\",\"phone\":\""+orderAddr.getPhone()+"\"}";
			String rs="{"+orderRs+",\"orderItems\":["+items+"],\"shippingAddress\":"+address+"}";
			return rs;
		}
		
		
		
}
