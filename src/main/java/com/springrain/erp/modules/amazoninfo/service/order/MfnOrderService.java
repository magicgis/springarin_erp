package com.springrain.erp.modules.amazoninfo.service.order;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.cxf.endpoint.Client;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.ApiException;
import com.ebay.sdk.ApiLogging;
import com.ebay.sdk.SdkException;
import com.ebay.sdk.call.CompleteSaleCall;
import com.ebay.soap.eBLBaseComponents.ShipmentTrackingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShipmentType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.IdGen;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.order.AmazonEmailMoneyCompareDao;
import com.springrain.erp.modules.amazoninfo.dao.order.AmazonMfnReplaceDao;
import com.springrain.erp.modules.amazoninfo.dao.order.MfnOrderDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonMfnReplace;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOutboundOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOutboundOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnPackage;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.custom.entity.Comment;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.service.CommentService;
import com.springrain.erp.modules.ebay.scheduler.EbayConstants;
import com.springrain.erp.modules.psi.service.PsiInventoryOutService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Component
@Transactional(readOnly = true)
public class MfnOrderService extends BaseService {

	@Autowired
	private MfnOrderDao mfnOrderDao;
	@Autowired
	private CommentService commentService;
	@Autowired
	private MfnPackageService mfnPackageService;
	@Autowired
	private AmazonProductService amazonProductService;
	@Autowired
	private AmazonMfnReplaceDao amazonMfnReplaceDao;
	@Autowired
	private AmazonUnlineOrderService amazonUnlineOrderService;
	
	@Autowired
	private AmazonEmailMoneyCompareDao amazonEmailMoneyCompareDao;
	
	@Autowired
	private SystemService systemService;
	@Autowired
	private PsiInventoryOutService   psiInventoryOutService;
	
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	
    public static Map<String,Float> emailMoney=Maps.newHashMap();
	


	public List<String> findNoEmail(){
		String sql=" SELECT email FROM temp_email_money_compare WHERE rate>90 ";
		return amazonEmailMoneyCompareDao.findBySql(sql);
	}
	
	@Transactional(readOnly = false)
    public  void save(List<AmazonMfnReplace> amazonMfnReplaces){
		amazonMfnReplaceDao.save(amazonMfnReplaces);
	}
	
	
	public MfnOrder get(String id) {
		return mfnOrderDao.get(id);
	}
	
	public MfnOrder getByOrderId(String orderId) {
		DetachedCriteria dc = mfnOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("orderId",orderId)); 
		List<MfnOrder> mfnOrder=mfnOrderDao.find(dc);
		if(mfnOrder!=null&&mfnOrder.size()>0){
			return mfnOrder.get(0);
		}else{
			return new MfnOrder();
		}
	}
	
	public MfnOrder getByOrderId2(String orderId) {
		DetachedCriteria dc = mfnOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("orderId",orderId)); 
		List<MfnOrder> mfnOrder=mfnOrderDao.find(dc);
		if(mfnOrder!=null&&mfnOrder.size()>0){
			return mfnOrder.get(0);
		}
		return null;
	}


	@Transactional(readOnly = false)
    public boolean synchronizeOrderStatus(AmazonUnlineOrder amazonUnlineOrder){
		amazonUnlineOrder.setOrderStatus("Waiting for delivery");
		amazonUnlineOrder.setEarliestShipDate(new Date());
		amazonUnlineOrder.setOutBound("1");
		amazonUnlineOrder.setCommentUrl(amazonUnlineOrder.getOrderStatus()+"-"+UserUtils.getUser().getName()+"-"+new Date());
		
		for(AmazonUnlineOrderItem item : amazonUnlineOrder.getItems()){
			 item.setQuantityOut(item.getQuantityOrdered());
		}
		amazonUnlineOrderService.save(amazonUnlineOrder);
		return true;
	}	
	
	
	@Transactional(readOnly = false)
    public boolean synchronizeOrder(AmazonUnlineOrder amazonUnlineOrder){
		if(amazonUnlineOrder.getSalesChannel()==null||amazonUnlineOrder.getSalesChannel().getId()==130||amazonUnlineOrder.getSalesChannel().getId()==21||amazonUnlineOrder.getItems()==null||amazonUnlineOrder.getItems().size()==0){
			return false;
		}
		amazonUnlineOrder.setOrderStatus("Shipped");
		amazonUnlineOrder.setEarliestShipDate(new Date());
		/*for(AmazonUnlineOrderItem item : amazonUnlineOrder.getItems()){
			 item.setQuantityOut(item.getQuantityOrdered());
		}*/
		amazonUnlineOrderService.save(amazonUnlineOrder);
		MfnOrder tempOrder=getByOrderId2(amazonUnlineOrder.getAmazonOrderId());
		if(tempOrder!=null){
			return false;
		}
		MfnAddress shippingAddress=new MfnAddress();
		shippingAddress.setId(amazonUnlineOrder.getShippingAddress().getId()+"_mfn");
		shippingAddress.setName(amazonUnlineOrder.getShippingAddress().getName());
		shippingAddress.setStreet(amazonUnlineOrder.getShippingAddress().getAddressLine1());
		shippingAddress.setStreet1(amazonUnlineOrder.getShippingAddress().getAddressLine2());
		shippingAddress.setStreet2(amazonUnlineOrder.getShippingAddress().getAddressLine3());
		shippingAddress.setCityName(amazonUnlineOrder.getShippingAddress().getCity());
		shippingAddress.setStateOrProvince(amazonUnlineOrder.getShippingAddress().getStateOrRegion());
		shippingAddress.setCountryCode(amazonUnlineOrder.getShippingAddress().getCountryCode());
		shippingAddress.setPostalCode(amazonUnlineOrder.getShippingAddress().getPostalCode());
		shippingAddress.setPhone(amazonUnlineOrder.getShippingAddress().getPhone());
		
		MfnOrder mfnOrder =new MfnOrder();
		mfnOrder.setId(amazonUnlineOrder.getId()+"_mfn");
		mfnOrder.setOrderId(amazonUnlineOrder.getAmazonOrderId());
		mfnOrder.setStatus("0");
		mfnOrder.setBuyTime(new Date());
		mfnOrder.setBuyerUser(amazonUnlineOrder.getBuyerName());
		mfnOrder.setShippingAddress(shippingAddress);
		mfnOrder.setLastModifiedTime(amazonUnlineOrder.getLastUpdateDate());
		if(!"CHECK24".equals(amazonUnlineOrder.getOrderChannel())&&!"管理员".equals(amazonUnlineOrder.getOrderChannel())){
			amazonUnlineOrder.setRateSn("Offline");
		}
		mfnOrder.setRateSn(amazonUnlineOrder.getRateSn());
		if(StringUtils.isNotBlank(amazonUnlineOrder.getCbaDisplayableShippingLabel())){
			mfnOrder.setRemark(amazonUnlineOrder.getCbaDisplayableShippingLabel());
		}
        
		if(amazonUnlineOrder.getSalesChannel().getId()==19){
			mfnOrder.setCountry("de");
		}else if(amazonUnlineOrder.getSalesChannel().getId()==120){
			mfnOrder.setCountry("com");
		}else if(amazonUnlineOrder.getSalesChannel().getId()==130||amazonUnlineOrder.getSalesChannel().getId()==21){
			mfnOrder.setCountry("cn");
		}

		mfnOrder.setBuyerUserEmail(amazonUnlineOrder.getBuyerEmail());
		mfnOrder.setOrderTotal(amazonUnlineOrder.getOrderTotal());
		mfnOrder.setPaymentMethod(amazonUnlineOrder.getPaymentMethod());
		mfnOrder.setPaidTime(amazonUnlineOrder.getPurchaseDate());
		mfnOrder.setOrderType("3");
		mfnOrder.setShippedTime(amazonUnlineOrder.getEarliestShipDate());
		List<MfnOrderItem> items = Lists.newArrayList();
		for (AmazonUnlineOrderItem item : amazonUnlineOrder.getItems()) {
			MfnOrderItem orderItem=new MfnOrderItem();
			orderItem.setId(item.getId()+"_mfn");
			orderItem.setSku(item.getSellersku());
			orderItem.setTitle(item.getProductName()+(StringUtils.isNotBlank(item.getColor())?("_"+item.getColor()):""));
			orderItem.setOrder(mfnOrder);
			orderItem.setQuantityPurchased(item.getQuantityOrdered());
			orderItem.setQuantityShipped(item.getQuantityOrdered());
			orderItem.setItemTax(item.getItemTax());
			orderItem.setItemPrice(item.getItemPrice());
			orderItem.setCodFee(0f);
			orderItem.setAsin(item.getAsin());
			items.add(orderItem);
		}
		mfnOrder.setItems(items);
		
		mfnOrderDao.save(mfnOrder);
		return true;
	}
	
	@Transactional(readOnly = false)
    public boolean synchronizeMfnOrder(AmazonOutboundOrder amazonOutboundOrder){
		String sql="update amazoninfo_outbound_order set fulfillment_action='Ship',order_status='COMPLETE',earliest_ship_date=now() where id=:p1 ";
		mfnOrderDao.updateBySql(sql, new Parameter(amazonOutboundOrder.getId()));

		
		MfnAddress shippingAddress=new MfnAddress();
		shippingAddress.setId(IdGen.uuid());
		shippingAddress.setName(amazonOutboundOrder.getShippingAddress().getName());
		shippingAddress.setStreet(amazonOutboundOrder.getShippingAddress().getAddressLine1());
		shippingAddress.setStreet1(amazonOutboundOrder.getShippingAddress().getAddressLine2());
		shippingAddress.setStreet2(amazonOutboundOrder.getShippingAddress().getAddressLine3());
		shippingAddress.setCityName(amazonOutboundOrder.getShippingAddress().getCity());
		shippingAddress.setStateOrProvince(amazonOutboundOrder.getShippingAddress().getStateOrRegion());
		shippingAddress.setCountryCode(amazonOutboundOrder.getShippingAddress().getCountryCode());
		shippingAddress.setPostalCode(amazonOutboundOrder.getShippingAddress().getPostalCode());
		shippingAddress.setPhone(amazonOutboundOrder.getShippingAddress().getPhone());
		
		MfnOrder mfnOrder =new MfnOrder();
		mfnOrder.setEventId(amazonOutboundOrder.getEventId());
		mfnOrder.setId(IdGen.uuid());
		mfnOrder.setOrderId(amazonOutboundOrder.getSellerOrderId());
		mfnOrder.setStatus("0");
		mfnOrder.setBuyTime(new Date());
		mfnOrder.setBuyerUser(amazonOutboundOrder.getBuyerName());
		mfnOrder.setShippingAddress(shippingAddress);
		mfnOrder.setLastModifiedTime(amazonOutboundOrder.getLastUpdateDate());
		mfnOrder.setRemark(amazonOutboundOrder.getRemark());
        if(amazonOutboundOrder.getCountry().startsWith("com")){
        	mfnOrder.setCountry("com");
        }else if("jp".equals(amazonOutboundOrder.getCountry())){
        	mfnOrder.setCountry("jp");
        }else{
        	mfnOrder.setCountry("de");
        }
		
		mfnOrder.setBuyerUserEmail(amazonOutboundOrder.getBuyerEmail());
		mfnOrder.setOrderTotal(0f);
		mfnOrder.setPaymentMethod("Other");
		mfnOrder.setPaidTime(amazonOutboundOrder.getCreateDate());
		mfnOrder.setCreateUser(amazonOutboundOrder.getCreateUser());
		mfnOrder.setAccountName(amazonOutboundOrder.getAccountName());
		if("Review".equals(amazonOutboundOrder.getOrderType())){
			mfnOrder.setOrderType("1");
		}else if("Support".equals(amazonOutboundOrder.getOrderType())){
			mfnOrder.setOrderType("2");
		}else{
			mfnOrder.setOrderType("3");
		}
	
		mfnOrder.setShippedTime(amazonOutboundOrder.getEarliestShipDate());
		List<MfnOrderItem> items = Lists.newArrayList();
		for (AmazonOutboundOrderItem item : amazonOutboundOrder.getItems()) {
			MfnOrderItem orderItem=new MfnOrderItem();
			orderItem.setId(IdGen.uuid());
			orderItem.setSku(item.getSellersku());
			orderItem.setTitle(item.getProductName()+(StringUtils.isNotBlank(item.getColor())?("_"+item.getColor()):""));
			orderItem.setOrder(mfnOrder);
			orderItem.setQuantityPurchased(item.getQuantityOrdered());
			orderItem.setQuantityShipped(item.getQuantityOrdered());
			orderItem.setItemTax(0f);
			orderItem.setItemPrice(0f);
			orderItem.setCodFee(0f);
			orderItem.setAsin(item.getAsin());
			items.add(orderItem);
		}
		mfnOrder.setItems(items);
		
		mfnOrderDao.save(mfnOrder);
		return true;
	}
	
	
	
	@Transactional(readOnly = false)
    public synchronized void updateAndSaveAmazon(List<AmazonOrder> mfn){
		 String updateSql="update amazoninfo_ebay_order set STATUS=:p1,last_modified_time=:p2 where id=:p3 ";
		 for (AmazonOrder amazonOrder : mfn) {
			String orderStatu="";
			if("Unshipped".equals(amazonOrder.getOrderStatus())){//UnShipped,Shipped,Canceled
				orderStatu="0";
			}else if("Shipped".equals(amazonOrder.getOrderStatus())){
				orderStatu="1";
			}else{
				orderStatu="9";
			}
			if(isExistOrder(amazonOrder.getId())){//存在
				if("1".equals(orderStatu)||"9".equals(orderStatu)){
					mfnOrderDao.updateBySql(updateSql, new Parameter(orderStatu,amazonOrder.getLastUpdateDate(),amazonOrder.getId()+"_amazon"));
				}
			}else{
				if("0".equals(orderStatu)||"1".equals(orderStatu)){
					MfnAddress shippingAddress=new MfnAddress();
					shippingAddress.setId(amazonOrder.getShippingAddress().getId()+"_amazon");
					shippingAddress.setName(amazonOrder.getShippingAddress().getName());
					shippingAddress.setStreet(amazonOrder.getShippingAddress().getAddressLine1());
					shippingAddress.setStreet1(amazonOrder.getShippingAddress().getAddressLine2());
					shippingAddress.setStreet2(amazonOrder.getShippingAddress().getAddressLine3());
					shippingAddress.setCityName(amazonOrder.getShippingAddress().getCity());
					shippingAddress.setStateOrProvince(amazonOrder.getShippingAddress().getStateOrRegion());
					shippingAddress.setCountryCode(amazonOrder.getShippingAddress().getCountryCode());
					shippingAddress.setPostalCode(amazonOrder.getShippingAddress().getPostalCode());
					shippingAddress.setPhone(amazonOrder.getShippingAddress().getPhone());
					
					MfnOrder mfnOrder =new MfnOrder();
					mfnOrder.setId(amazonOrder.getId()+"_amazon");
					mfnOrder.setOrderId(amazonOrder.getAmazonOrderId());
					mfnOrder.setStatus(orderStatu);
					mfnOrder.setBuyTime(amazonOrder.getPurchaseDate());
					mfnOrder.setBuyerUser(amazonOrder.getBuyerName());
					mfnOrder.setShippingAddress(shippingAddress);
					mfnOrder.setLastModifiedTime(amazonOrder.getLatestShipDate());
					mfnOrder.setRateSn(amazonOrder.getRateSn());
					mfnOrder.setCountry(amazonOrder.getSalesChannel().substring(amazonOrder.getSalesChannel().lastIndexOf(".")+1));
					mfnOrder.setBuyerUserEmail(amazonOrder.getBuyerEmail());
					mfnOrder.setOrderTotal(amazonOrder.getOrderTotal());
					mfnOrder.setPaymentMethod(amazonOrder.getPaymentMethod());
					mfnOrder.setPaidTime(amazonOrder.getPurchaseDate());
					mfnOrder.setAccountName(amazonOrder.getAccountName());
					
					mfnOrder.setOrderType("0");
					mfnOrder.setShippedTime(amazonOrder.getEarliestShipDate());
					mfnOrder.setAccountName(amazonOrder.getAccountName());
					List<MfnOrderItem> items = Lists.newArrayList();
					for (AmazonOrderItem item : amazonOrder.getItems()) {
						MfnOrderItem orderItem=new MfnOrderItem();
						orderItem.setId(item.getId()+"_amazon");
						orderItem.setSku(item.getSellersku());
						if(item.getSellersku().toLowerCase().contains("_pack")){
							try{
								int quantity=item.getQuantityOrdered()*Integer.parseInt(item.getSellersku().split("_pack")[1]);
								orderItem.setQuantityPurchased(quantity);
								orderItem.setQuantityShipped(quantity);
							}catch(Exception e){
								orderItem.setQuantityPurchased(item.getQuantityOrdered());
								orderItem.setQuantityShipped(item.getQuantityOrdered());
							}
						}else{
							orderItem.setQuantityPurchased(item.getQuantityOrdered());
							orderItem.setQuantityShipped(item.getQuantityOrdered());
						}
						
						orderItem.setTitle(item.getProductName()+(StringUtils.isNotBlank(item.getColor())?("_"+item.getColor()):""));
						//orderItem.setOrderId(item.getOrder().getId()+"_amazon");
						orderItem.setOrder(mfnOrder);
						
						orderItem.setItemTax(0f);
						orderItem.setItemPrice(item.getItemPrice());
						orderItem.setCodFee(0f);
						orderItem.setAsin(item.getAsin());
						items.add(orderItem);
					}
					mfnOrder.setItems(items);
					
					mfnOrderDao.save(mfnOrder);
					
				}
			}
		}
	}
	//不存在 false
	public boolean isExistOrder(Integer id){
		String sql="select count(1) from amazoninfo_ebay_order where id = :p1 ";
		List<Object> count=mfnOrderDao.findBySql(sql,new Parameter(id+"_amazon"));
		return ((BigInteger)count.get(0)).intValue()>0;
	}
	
	public boolean isEqual(){
		String sql1="SELECT COUNT(*) FROM amazoninfo_ebay_order WHERE STATUS='0' AND id LIKE '%_amazon' "+
                 // " AND (DATE_FORMAT(buy_time,'%Y-%m-%d')=DATE_FORMAT((DATE_SUB(NOW(),INTERVAL 1 DAY)),'%Y-%m-%d') OR DATE_FORMAT(buy_time,'%Y-%m-%d')=DATE_FORMAT(NOW(),'%Y-%m-%d')) ";
				" AND DATE_FORMAT(buy_time,'%Y-%m-%d')>=DATE_FORMAT((DATE_SUB(NOW(),INTERVAL 7 DAY)),'%Y-%m-%d') "; 
		
		String sql2="SELECT COUNT(*) FROM amazoninfo_order r WHERE r.`fulfillment_channel`='MFN'  AND order_status='UnShipped'  "+
                   //" AND (DATE_FORMAT(purchase_date,'%Y-%m-%d')=DATE_FORMAT((DATE_SUB(NOW(),INTERVAL 1 DAY)),'%Y-%m-%d') OR DATE_FORMAT(purchase_date,'%Y-%m-%d')=DATE_FORMAT(NOW(),'%Y-%m-%d'))";
		       " AND DATE_FORMAT(purchase_date,'%Y-%m-%d')>=DATE_FORMAT((DATE_SUB(NOW(),INTERVAL 7 DAY)),'%Y-%m-%d') ";
		List<Object> count1=mfnOrderDao.findBySql(sql1);
		List<Object> count2=mfnOrderDao.findBySql(sql2);
		return ((BigInteger)count1.get(0)).intValue()==((BigInteger)count2.get(0)).intValue();
		
	}
	
	
	
	@Transactional(readOnly = false)
    public void updateAndSaveEbay(){
		String sql="INSERT INTO amazoninfo_ebay_order (id,order_id,STATUS,buy_time,buyer_user,shipping_address,invoice_address,last_modified_time,rate_sn,country,buyer_user_email,order_total,payment_method,paid_time,order_type,shipped_time,shipping_service_cost) "+
		" SELECT CONCAT(id,'_','ebay') id,order_id,'0' STATUS,created_time,buyer_user_id, "+
		" CONCAT(shipping_address,'_','ebay') shipping_address,NULL invoice_address, "+
		" last_modified_time,rate_sn,r.country,(SELECT t.email FROM ebay_orderitem t WHERE t.order_id=r.id LIMIT 1 ) buyer_email,total,payment_methods,paid_time,'0',shipped_time,shipping_service_cost "+
		" FROM ebay_order r WHERE STATUS='1' AND r.created_time>=DATE_ADD(CURRENT_DATE(),INTERVAL -15 day)  "+
		" ON DUPLICATE KEY UPDATE `last_modified_time` = VALUES(last_modified_time),`paid_time` =VALUES(paid_time),`shipped_time` = VALUES(shipped_time),shipping_service_cost= VALUES(shipping_service_cost) ";
		mfnOrderDao.updateBySql(sql, null);	
		
		String temp="SELECT id FROM amazoninfo_ebay_order WHERE buy_time>=DATE_ADD(CURRENT_DATE(),INTERVAL -15 day)  and SUBSTRING_INDEX(id,'_',-1)='ebay'  ";
		List<String> tempIdList=mfnOrderDao.findBySql(temp);
		
		String sql1="INSERT INTO amazoninfo_ebay_orderitem(id,sku,title,order_id,quantity_purchased,quantity_shipped,item_tax,item_price,cod_fee) "+
		" SELECT p.id,p.sku,(CASE WHEN (NAME='Inateck other' OR NAME='Inateck Old') THEN p.title ELSE p.name END),p.order_id,p.quantity_purchased,p.quantity_purchased,0,transaction_price,0 "+
		" FROM (SELECT CONCAT(id,'_','ebay') id,(CASE WHEN t.`sku` LIKE '%\\_q%' THEN SUBSTRING_INDEX(t.`sku`,'_q',1) ELSE sku END) sku, "+
		" IFNULL((SELECT DISTINCT CONCAT(product_name,CASE  WHEN color='' THEN '' ELSE CONCAT('_',color) END) NAME FROM psi_sku s WHERE s.sku=t.sku  "+
		" AND s.del_flag='0' AND product_name NOT LIKE '%Inateck other%' AND product_name NOT LIKE '%Inateck Old%' LIMIT 1),title) NAME,title, "+
		" CONCAT(order_id,'_','ebay') order_id,(quantity_purchased*(CASE WHEN t.`sku` LIKE '%\\_q%' THEN SUBSTRING_INDEX(t.`sku`,'_q',-1) ELSE 1 END))quantity_purchased,transaction_price "+
		" FROM ebay_orderitem t) p WHERE p.order_id IN :p1 "+
		" ON DUPLICATE KEY UPDATE title=VALUES(title),`quantity_purchased` = VALUES(quantity_purchased),`quantity_shipped` = VALUES(quantity_shipped) ";
		mfnOrderDao.updateBySql(sql1,new Parameter(tempIdList));	
		
		
		String tempSql="SELECT shipping_address FROM amazoninfo_ebay_order WHERE buy_time>=DATE_ADD(CURRENT_DATE(),INTERVAL -15 day)  and SUBSTRING_INDEX(id,'_',-1)='ebay'  ";
		List<String> tempList=mfnOrderDao.findBySql(tempSql);
		
		String sql2="INSERT INTO amazoninfo_ebay_address(id,NAME,street,street1,street2,city_name,country,state_or_province,country_code,postal_code,phone,order_id)  "+
		" SELECT CONCAT(id,'_','ebay') id,NAME,street,street1,street2,city_name,county,state_or_province,country_code,postal_code,phone,CONCAT(order_id,'_','ebay') order_id  "+
		" FROM ebay_address WHERE CONCAT(id,'_','ebay') IN :p1   "+
		" ON DUPLICATE KEY UPDATE `country` = VALUES(country)";
		mfnOrderDao.updateBySql(sql2,new Parameter(tempList));	
		
		String updateSql="SELECT SUBSTRING_INDEX(o.`id`,'_',1) FROM amazoninfo_ebay_order o WHERE  buy_time>=DATE_ADD(CURRENT_DATE(),INTERVAL -20 day) and o.`status`='0' AND SUBSTRING_INDEX(o.`id`,'_',-1)='ebay' ";
		List<String> updateList=mfnOrderDao.findBySql(updateSql);
		
		String sql3=" INSERT INTO amazoninfo_ebay_order (id,STATUS,last_modified_time,shipped_time) "+
		" SELECT CONCAT(r.id,'_','ebay') id,(CASE WHEN r.status='2' THEN '1' ELSE '9' END) STATUS,r.last_modified_time,r.shipped_time "+
		" FROM ebay_order r WHERE r.id IN :p1 AND r.status !='1' "+
		" ON DUPLICATE KEY UPDATE `last_modified_time` = VALUES(last_modified_time),`shipped_time` = VALUES(shipped_time),STATUS= VALUES(STATUS) ";
		mfnOrderDao.updateBySql(sql3,new Parameter(updateList));	
	}
	
	@Transactional(readOnly = false)
    public void updateAndSaveCheck24(){

		String sql="INSERT INTO amazoninfo_ebay_order (id,order_id,STATUS,buy_time,buyer_user,shipping_address,invoice_address,last_modified_time,rate_sn,country,buyer_user_email,order_total,payment_method,paid_time,order_type,shipping_service_cost,remark) "+
				" SELECT CONCAT(id,'_','mfn') id,r.`amazon_order_id` order_id,'0' STATUS,r.`purchase_date` buy_time,r.`buyer_name` buyer_user, "+
				" CONCAT(shipping_address,'_','mfn') shipping_address,NULL invoice_address, "+
				" r.`last_update_date` last_modified_time,rate_sn,'de',r.`buyer_email` buyer_user_email,r.`order_total`,r.`payment_method`,r.`purchase_date` paid_time,'3',2.99,'check24' "+
				" FROM amazoninfo_unline_order r WHERE order_status='Unshipped' AND r.`purchase_date`>=DATE_ADD(CURRENT_DATE(),INTERVAL -15 DAY) AND r.`order_channel`='check24' "+
				" ON DUPLICATE KEY UPDATE `order_total` = VALUES(order_total) ";
		       mfnOrderDao.updateBySql(sql, null);	
				
		        String idSql="SELECT id FROM amazoninfo_ebay_order WHERE SUBSTRING_INDEX(id,'_',-1)='mfn'  and buy_time>=DATE_ADD(CURRENT_DATE(),INTERVAL -15 day) and country='de' and remark='check24'";
			   	List<String> idTempList=mfnOrderDao.findBySql(idSql);
				if(idTempList!=null&&idTempList.size()>0){
					String sql1="INSERT INTO amazoninfo_ebay_orderitem(id,sku,title,order_id,quantity_purchased,quantity_shipped,item_tax,item_price,cod_fee) "+
							" SELECT CONCAT(id,'_','mfn') id,sellersku,CONCAT(product_name,CASE  WHEN color='' THEN '' ELSE CONCAT('_',color) END) title, "+
							" CONCAT(order_id,'_','mfn') order_id,quantity_ordered quantity_shipped,quantity_ordered quantity_purchased,0,item_price,0 "+
							" FROM amazoninfo_unline_orderitem p WHERE  CONCAT(p.order_id,'_','mfn')  IN (:p1 ) "+
							" ON DUPLICATE KEY UPDATE title=VALUES(title),`quantity_purchased` = VALUES(quantity_purchased),`quantity_shipped` = VALUES(quantity_purchased) ";
					mfnOrderDao.updateBySql(sql1, new Parameter(idTempList));
				}
		       
				String addressSql="SELECT shipping_address FROM amazoninfo_ebay_order WHERE SUBSTRING_INDEX(id,'_',-1)='mfn'  and buy_time>=DATE_ADD(CURRENT_DATE(),INTERVAL -15 day) and country='de' and remark='check24'";
				List<String> addressList=mfnOrderDao.findBySql(addressSql);
				if(addressList!=null&&addressList.size()>0){
					String sql2="INSERT INTO amazoninfo_ebay_address(id,NAME,street,street1,street2,city_name,country,state_or_province,country_code,postal_code,phone)  "+
							" SELECT CONCAT(id,'_','mfn') id,NAME,address_line1 street,address_line2 street1,address_line3 street2,city,county,state_or_region,country_code,postal_code,phone  "+
							" FROM amazoninfo_unline_address WHERE CONCAT(id,'_','mfn') IN (:p1)   "+
							" ON DUPLICATE KEY UPDATE `phone` = VALUES(phone)  ";
					mfnOrderDao.updateBySql(sql2, new Parameter(addressList));	
					
				}
				
				
				String tempSql="SELECT SUBSTRING_INDEX(o.`id`,'_',1) FROM amazoninfo_ebay_order o  "+
						" WHERE o.`status`='0' AND SUBSTRING_INDEX(o.`id`,'_',-1)='mfn' and buy_time>=DATE_ADD(CURRENT_DATE(),INTERVAL -20 day) and country='de'  and remark='check24' ";
				List<String> idList=mfnOrderDao.findBySql(tempSql);
				if(idList!=null&&idList.size()>0){
					String sql3=" INSERT INTO amazoninfo_ebay_order (id,STATUS) "+
							" SELECT CONCAT(r.id,'_','mfn') id,(CASE WHEN r.order_status='Shipped' THEN '1' ELSE '9' END) STATUS "+
							" FROM amazoninfo_unline_order r WHERE r.id IN (:p1) AND r.order_status !='Unshipped' "+
							" ON DUPLICATE KEY UPDATE STATUS= VALUES(STATUS) ";
					mfnOrderDao.updateBySql(sql3, new Parameter(idList));	
				}
				
	}	
	
	public Map<String,MfnOrder> findAllOrder(String[] idArr){
		 Map<String,MfnOrder> map=Maps.newHashMap();
		 DetachedCriteria dc = mfnOrderDao.createDetachedCriteria();
		 dc.add(Restrictions.in("id", idArr));
		 List<MfnOrder> mfnList=mfnOrderDao.find(dc);
		 for (MfnOrder mfnOrder : mfnList) {
			map.put(mfnOrder.getId(), mfnOrder);
		 }
		 return map;
	}
	
	public Page<MfnOrder> ordersManager(Page<MfnOrder> page, MfnOrder mfnOrder) {
		DetachedCriteria dc = mfnOrderDao.createDetachedCriteria();
		dc.createAlias("this.items", "item");
		
		if (StringUtils.isNotEmpty(mfnOrder.getOrderId())) {
			dc.createAlias("this.shippingAddress", "shippingAddress");
			Pattern pattern = Pattern.compile("[0-9]*"); 
		 try{	
			   if(pattern.matcher(mfnOrder.getOrderId()).matches()){
				   char strs[] = mfnOrder.getOrderId().toCharArray();
				   int index = 0;
				   int len=mfnOrder.getOrderId().length();
				   for(int i=0; i<len; i++){
				     if('0'!=strs[i]){
				       index=i;
				       break;
				     }
				   }
				   String strLast = mfnOrder.getOrderId().substring(index, len);// 截取字符串
				   dc.add(Restrictions.or(Restrictions.eq("billNo",Integer.parseInt(strLast)),Restrictions.eq("item.sku",mfnOrder.getOrderId()),Restrictions.like("orderId", "%" + mfnOrder.getOrderId() + "%")));
			   }else if(mfnOrder.getOrderId().contains("Test")){
			    	dc.add(Restrictions.eq("billNo",Integer.parseInt(mfnOrder.getOrderId().replaceAll("Test","").trim())));
			    	dc.add(Restrictions.eq("orderType","1"));
			   }else if(mfnOrder.getOrderId().contains("Ersatz")){
			    	dc.add(Restrictions.eq("billNo",Integer.parseInt(mfnOrder.getOrderId().replaceAll("Ersatz","").trim())));
			    	dc.add(Restrictions.or(Restrictions.eq("orderType","2"),Restrictions.eq("orderType","5")));
			   }else if(mfnOrder.getOrderId().contains("Mfn")){
			    	dc.add(Restrictions.eq("billNo",Integer.parseInt(mfnOrder.getOrderId().replaceAll("Mfn","").trim())));
			    	dc.add(Restrictions.eq("orderType","3"));
			   }else{
			    	dc.add(Restrictions.or(Restrictions.like("buyerUser", "%" + mfnOrder.getOrderId() + "%"),Restrictions.like("buyerUserEmail", "%" + mfnOrder.getOrderId() + "%"),
			    	Restrictions.like("shippingAddress.name", "%" + mfnOrder.getOrderId() + "%"),Restrictions.like("orderId", "%" + mfnOrder.getOrderId() + "%"),Restrictions.eq("item.sku",mfnOrder.getOrderId()),Restrictions.like("item.title","%" + mfnOrder.getOrderId() + "%")));
			    }
		   }catch(Exception e){
			   dc.add(Restrictions.or(Restrictions.like("buyerUser", "%" + mfnOrder.getOrderId() + "%"),Restrictions.like("buyerUserEmail", "%" + mfnOrder.getOrderId() + "%"),
					   Restrictions.like("shippingAddress.name", "%" + mfnOrder.getOrderId() + "%"),Restrictions.like("orderId", "%" + mfnOrder.getOrderId() + "%"),Restrictions.eq("item.sku",mfnOrder.getOrderId()),Restrictions.like("item.title","%" + mfnOrder.getOrderId() + "%")));
		   }
		}else{
			if (mfnOrder.getBuyTime() != null) {
				dc.add(Restrictions.ge("buyTime", mfnOrder.getBuyTime()));
			}
			if (mfnOrder.getLastModifiedTime() != null) {
				dc.add(Restrictions.le("buyTime", DateUtils.addDays(mfnOrder.getLastModifiedTime(),1)));
			}
			if(StringUtils.isNotEmpty(mfnOrder.getChannel())){
				if("0".equals(mfnOrder.getChannel())){//amazon
					dc.add(Restrictions.like("id", "%amazon%"));
				}else if("2".equals(mfnOrder.getChannel())){
					dc.add(Restrictions.eq("orderType","3"));
				}else if("3".equals(mfnOrder.getChannel())){
					dc.add(Restrictions.like("id", "%ebay%"));
				}else{
					dc.add(Restrictions.in("orderType",Sets.newHashSet("1","2","5")));
				}
			}
			if(!"11".equals(mfnOrder.getStatus())){
				dc.add(Restrictions.eq("status", mfnOrder.getStatus()));
			}
		}
		
		if(StringUtils.isNotEmpty(mfnOrder.getCountry())){
			if("com".equals(mfnOrder.getCountry())){
				dc.add(Restrictions.like("country","com%"));
			}else{
				dc.add(Restrictions.eq("country",mfnOrder.getCountry()));
			}
		}
		if(StringUtils.isNotBlank(mfnOrder.getShowBillNo())){
			if("0".equals(mfnOrder.getShowBillNo())){//已打印
				dc.add(Restrictions.isNotNull("billNo"));
			}else{
				dc.add(Restrictions.isNull("billNo"));
			}
		}
		String orderBy = page.getOrderBy();
		if (StringUtils.isBlank(orderBy)) {
			dc.addOrder(Order.desc("item.sku"));
		}
		return mfnOrderDao.find(page, dc);
	}

	
	
	@Transactional(readOnly = false)
	public void updatePrintTime(String[] arr,String name){
		Date date=new Date();
		String sql="update amazoninfo_ebay_order set print_time=:p1,file_name=:p2 WHERE id=:p3 ";
		String sql1="update amazoninfo_ebay_order set print_time=:p1,file_name=:p2,status='1' WHERE id=:p3 ";
		for (String id : arr) {
			if(id.endsWith("amazon")||id.endsWith("ebay")){
				mfnOrderDao.updateBySql(sql,new Parameter(date,name,id));
			}else{
				mfnOrderDao.updateBySql(sql1,new Parameter(date,name,id));
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void updateStatus(Set<String> orderId){
		Date date=new Date();
		String sql="update amazoninfo_ebay_order set status=:p1 WHERE order_id in :p2 ";
		mfnOrderDao.updateBySql(sql,new Parameter(date,"1",orderId));
	}	

	@Transactional(readOnly = false)
	public String updateQuantity(String id, Integer quantity) {
		String sql = "update  amazoninfo_ebay_orderitem set quantity_shipped=:p1 where id=:p2";
		int i = this.mfnOrderDao.updateBySql(sql, new Parameter(quantity, id));
		if (i > 0) {
			return "true";
		} else {
			return "false";
		}
	}

	@Transactional(readOnly = false)
	public void save(MfnOrder mfnOrder) {
		mfnOrderDao.clear();
		String mfnId=mfnOrder.getId();
		mfnOrderDao.save(mfnOrder);
		if(StringUtils.isNotBlank(mfnOrder.getEventId())){
			String[] eventIdArr=mfnOrder.getEventId().split(",");
			for (String eventId : eventIdArr) {
				String sql="update custom_event_manager set state='1' where id=:p1 ";
				mfnOrderDao.updateBySql(sql, new Parameter(eventId));
				if(StringUtils.isBlank(mfnId)){
					Event event=new Event();
					event.setId(Integer.parseInt(eventId));
					Comment comm = new Comment();
					comm.setComment(UserUtils.getUser().getName()+"添加本地自发货订单(Add MFN Order)<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazonAndEbay/mfnOrder/form?id="+mfnOrder.getId()+"'>"+mfnOrder.getOrderId()+"</a>");
					comm.setType("1");
					comm.setEvent(event);
					comm.setCreateBy(UserUtils.getUser());
					comm.setCreateDate(new Date());
					comm.setUpdateBy(UserUtils.getUser());
					comm.setUpdateDate(new Date());
					commentService.save(comm);
				}
			}
		}
	}
	
	public Map<String,List<String>> getDownloadFileList(MfnOrder mfnOrder){
		String sql=" SELECT DATE_FORMAT(o.`print_time`,'%Y-%m-%d'),file_name FROM amazoninfo_ebay_order o "+
				" WHERE o.`print_time` IS NOT NULL and DATE_FORMAT(o.`print_time`,'%Y-%m-%d')>=:p1 and DATE_FORMAT(o.`print_time`,'%Y-%m-%d')<=:p2 "+
				" GROUP BY DATE_FORMAT(o.`print_time`,'%Y-%m-%d'),file_name ";
		Map<String,List<String>> map=new HashMap<String,List<String>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> list=this.mfnOrderDao.findBySql(sql, new Parameter(sdf.format(mfnOrder.getBuyTime()),sdf.format(mfnOrder.getLastModifiedTime())));
		for (Object[] obj : list) {
			List<String> nameList=map.get(obj[0].toString());
			if(nameList==null){
				nameList =new ArrayList<String>();
				map.put(obj[0].toString(), nameList);
			}
			nameList.add(obj[1].toString());
		}
		return map;
	}
	
	public Map<String,Integer> getCurrentDateTotal(MfnOrder mfnOrder){
		Map<String,Integer> map=Maps.newLinkedHashMap();
		String sql="";
		if("0".equals(mfnOrder.getIsOld())){//包含old
			sql="SELECT t.title,SUM(t.`quantity_shipped`) quantity_total FROM amazoninfo_ebay_order o JOIN amazoninfo_ebay_orderitem t ON o.`id`=t.`order_id` "+
		               " join amazoninfo_ebay_package p on p.id=o.package_id "+
		               " WHERE DATE_FORMAT(p.`print_time`,'%Y-%m-%d')>=:p1  and DATE_FORMAT(p.`print_time`,'%Y-%m-%d')<=:p2 and o.country=:p3 "+
		               " and (lower(t.title)  like '%unitek%'  or lower(t.title)  like '%orico%' or sku='unknown' or lower(t.`sku`)  like '%-old%' or lower(t.`sku`)  like '%_old%' or lower(t.`sku`)  like '%old_%' or lower(t.`sku`)  like '%old-%') " ;
		           if(StringUtils.isNotBlank(mfnOrder.getOrderId())){
		        	   sql+=" and (t.title like '%"+mfnOrder.getOrderId()+"%' or t.sku like '%"+mfnOrder.getOrderId()+"%') ";
		           }
		           sql+=" GROUP BY t.title order by quantity_total desc ";
		}else{
			sql="SELECT t.title,SUM(t.`quantity_shipped`) quantity_total FROM amazoninfo_ebay_order o JOIN amazoninfo_ebay_orderitem t ON o.`id`=t.`order_id` "+
		               " join amazoninfo_ebay_package p on p.id=o.package_id "+
		               " WHERE DATE_FORMAT(p.`print_time`,'%Y-%m-%d')>=:p1  and lower(t.title) not like '%unitek%'  and lower(t.title) not like '%orico%'  and DATE_FORMAT(p.`print_time`,'%Y-%m-%d')<=:p2 and o.country=:p3 and sku!='unknown' and lower(t.`sku`) not like '%-old%' and lower(t.`sku`) not like '%_old%' and lower(t.`sku`) not like '%old_%' and lower(t.`sku`) not like '%old-%' " ;
		           if(StringUtils.isNotBlank(mfnOrder.getOrderId())){
		        	   sql+=" and (t.title like '%"+mfnOrder.getOrderId()+"%' or t.sku like '%"+mfnOrder.getOrderId()+"%') ";
		           }
		           sql+=" GROUP BY t.title order by quantity_total desc ";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> list=this.mfnOrderDao.findBySql(sql,new Parameter(sdf.format(mfnOrder.getBuyTime()),sdf.format(mfnOrder.getLastModifiedTime()),mfnOrder.getCountry()));
		for (Object[] obj : list) {
			map.put(obj[0]==null?"":obj[0].toString(),Integer.parseInt(obj[1].toString()));
		}
		return map;
	}
	
	public Map<String,Integer> getTotalByProductName2(MfnOrder mfnOrder){
		String sql="SELECT t.title,t.asin,SUM(t.`quantity_shipped`) quantity_total FROM amazoninfo_ebay_order o JOIN amazoninfo_ebay_orderitem t ON o.`id`=t.`order_id` "+
	               " join amazoninfo_ebay_package p on p.id=o.package_id "+
	             //  " WHERE DATE_FORMAT(p.`print_time`,'%Y-%m-%d %H:%i:%s')>=:p1 and lower(t.title) not like '%unitek%'  and lower(t.title) not like '%orico%'  and DATE_FORMAT(p.`print_time`,'%Y-%m-%d %H:%i:%s')<:p2 and o.country=:p3 and sku!='unknown' and lower(t.`sku`) not like '%-old%' and lower(t.`sku`) not like '%_old%' and lower(t.`sku`) not like '%old_%' and lower(t.`sku`) not like '%old-%' "+
	             " WHERE DATE_FORMAT(p.`print_time`,'%Y-%m-%d %H:%i:%s')>=:p1 and lower(t.title) not like '%unitek%'  and lower(t.title) not like '%orico%'  and DATE_FORMAT(p.`print_time`,'%Y-%m-%d %H:%i:%s')<:p2 and o.country=:p3 and sku!='unknown'  "+
	             " group by t.title,t.asin ";
		Map<String,Integer> map=new HashMap<String,Integer>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Object[]> list=this.mfnOrderDao.findBySql(sql,new Parameter(sdf.format(mfnOrder.getBuyTime()),sdf.format(mfnOrder.getLastModifiedTime()),mfnOrder.getCountry()));
		for (Object[] obj : list) {
			String productName="";
			String asin=(obj[1]==null?"":obj[1].toString());
			if(StringUtils.isBlank(obj[0].toString())||"null".equals(obj[0].toString())){
				if(StringUtils.isNotBlank(asin)){
					productName=amazonProductService.findProductName(asin,mfnOrder.getCountry());
				}else{
					productName=obj[0].toString();
				}
			}else{
				productName=obj[0].toString();
			}
			if(map.get(productName)==null){
				map.put(productName,Integer.parseInt(obj[2].toString()));
			}else{
				Integer quantity=map.get(productName);
				map.put(productName,Integer.parseInt(obj[2].toString())+quantity);
			}
			
		}
		return map;
	}
	
	public Map<String,Integer> getTotalByProductName(MfnOrder mfnOrder){
		String sql="SELECT t.title,t.asin,SUM(t.`quantity_shipped`) quantity_total FROM amazoninfo_ebay_order o JOIN amazoninfo_ebay_orderitem t ON o.`id`=t.`order_id` "+
	               " join amazoninfo_ebay_package p on p.id=o.package_id "+
	               " WHERE DATE_FORMAT(p.`print_time`,'%Y-%m-%d')>=:p1 and lower(t.title) not like '%unitek%'  and lower(t.title) not like '%orico%' and DATE_FORMAT(p.`print_time`,'%Y-%m-%d')<=:p2 and o.country=:p3 and sku!='unknown' and lower(t.`sku`) not like '%-old%' and lower(t.`sku`) not like '%_old%' and lower(t.`sku`) not like '%old_%' and lower(t.`sku`) not like '%old-%' "+
	               " group by t.title,t.asin ";
		Map<String,Integer> map=new HashMap<String,Integer>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> list=this.mfnOrderDao.findBySql(sql,new Parameter(sdf.format(mfnOrder.getBuyTime()),sdf.format(mfnOrder.getLastModifiedTime()),mfnOrder.getCountry()));
		for (Object[] obj : list) {
			String productName="";
			String asin=(obj[1]==null?"":obj[1].toString());
			if(StringUtils.isBlank(obj[0].toString())||"null".equals(obj[0].toString())){
				if(StringUtils.isNotBlank(asin)){
					productName=amazonProductService.findProductName(asin,mfnOrder.getCountry());
				}else{
					productName=obj[0].toString();
				}
			}else{
				productName=obj[0].toString();
			}
			if(map.get(productName)==null){
				map.put(productName,Integer.parseInt(obj[2].toString()));
			}else{
				Integer quantity=map.get(productName);
				map.remove(productName);
				map.put(productName,Integer.parseInt(obj[2].toString())+quantity);
			}
			
		}
		return map;
	}
	
	
	public Map<String,List<MfnOrderItem>> getCurrentDateList(MfnOrder mfnOrder){
		String sql="";
		if("0".equals(mfnOrder.getIsOld())){//包含old
			sql="SELECT t.sku,t.`id`,t.title,o.order_id,t.`quantity_shipped`,t.asin,p.print_time,o.bill_no,o.order_type FROM amazoninfo_ebay_order o JOIN amazoninfo_ebay_orderitem t ON o.`id`=t.`order_id` "+
					  " join amazoninfo_ebay_package p on p.id=o.package_id "+
		               " WHERE DATE_FORMAT(p.`print_time`,'%Y-%m-%d')>=:p1 and DATE_FORMAT(p.`print_time`,'%Y-%m-%d')<=:p2 and o.country=:p3  "+
					  " and (lower(t.title) like '%unitek%' or lower(t.title) like '%orico%'  or sku='unknown' or lower(t.`sku`)  like '%-old%' or lower(t.`sku`)  like '%_old%' or lower(t.`sku`)  like '%old_%' or lower(t.`sku`)  like '%old-%') ";
		               if(StringUtils.isNotBlank(mfnOrder.getOrderId())){
			        	   sql+=" and (t.title like '%"+mfnOrder.getOrderId()+"%' or t.sku like '%"+mfnOrder.getOrderId()+"%') ";
			           }
		               sql+= " ORDER BY t.sku ";
		}else{
			sql="SELECT t.sku,t.`id`,t.title,o.order_id,t.`quantity_shipped`,t.asin,p.print_time,o.bill_no,o.order_type FROM amazoninfo_ebay_order o JOIN amazoninfo_ebay_orderitem t ON o.`id`=t.`order_id` "+
					  " join amazoninfo_ebay_package p on p.id=o.package_id "+
		               " WHERE DATE_FORMAT(p.`print_time`,'%Y-%m-%d')>=:p1  and lower(t.title) not like '%unitek%'  and lower(t.title) not like '%orico%' and DATE_FORMAT(p.`print_time`,'%Y-%m-%d')<=:p2 and o.country=:p3 and sku!='unknown' and lower(t.`sku`) not like '%-old%' and lower(t.`sku`) not like '%_old%' and lower(t.`sku`) not like '%old_%' and lower(t.`sku`) not like '%old-%' ";
		               if(StringUtils.isNotBlank(mfnOrder.getOrderId())){
			        	   sql+=" and (t.title like '%"+mfnOrder.getOrderId()+"%' or t.sku like '%"+mfnOrder.getOrderId()+"%') ";
			           }
		               sql+= " ORDER BY t.sku ";
		}
		
		
		Map<String,List<MfnOrderItem>> map=new HashMap<String,List<MfnOrderItem>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> list=this.mfnOrderDao.findBySql(sql, new Parameter(sdf.format(mfnOrder.getBuyTime()),sdf.format(mfnOrder.getLastModifiedTime()),mfnOrder.getCountry()));
		for (Object[] obj : list) {
			String sku=obj[0].toString();
			String id=obj[1].toString();
			String title=(obj[2]==null?"":obj[2].toString());
			String orderId=obj[3].toString();
			Integer quantity=Integer.parseInt(obj[4].toString());
			String asin=(obj[5]==null?"":obj[5].toString());
			Timestamp time=(Timestamp)obj[6];
			String billNo=(obj[7]==null?"0":obj[7].toString());
			String orderType=obj[8].toString();
			
			MfnOrderItem item=new MfnOrderItem();
			item.setId(id);
			item.setQuantityShipped(quantity);
			item.setPrintTime(new Date(time.getTime()));
			
			if("0".equals(orderType)){
				if(billNo.length()<8){
					String num="";
					StringBuffer buf= new StringBuffer();
					for(int m=0;m<8-billNo.length();m++){
						buf.append("0");
					}
					num=buf.toString();
					item.setBillNo(num+billNo);
				}else{
					item.setBillNo(billNo);
				}
			}else if("1".equals(orderType)){//test
				item.setBillNo("Test "+billNo);
			}else if("2".equals(orderType)||"5".equals(orderType)){//support
				item.setBillNo("Ersatz "+billNo);
			}else if("3".equals(orderType)){
				item.setBillNo("Mfn "+billNo);
			}
			
			if(StringUtils.isBlank(title)||"null".equals(title)){
				if(StringUtils.isNotBlank(asin)){
					item.setProductName(amazonProductService.findProductName(item.getAsin(),mfnOrder.getCountry()));
				}else{
					item.setProductName(title);
				}
			}else{
				item.setProductName(title);
			}
			
			item.setSku(sku);
		    item.setOrderId(orderId);
			List<MfnOrderItem> mfnOrderList=map.get(item.getProductName());
			if(mfnOrderList==null){
				mfnOrderList =new ArrayList<MfnOrderItem>();
				map.put(item.getProductName(), mfnOrderList);
			}
			mfnOrderList.add(item);
		}
		return map;
	}

	public String getIsExistPackage(String[] arr){
		String sql="SELECT 1 FROM amazoninfo_ebay_order WHERE package_id IS NULL AND id=:p1 ";
		for (String arrId : arr) {
			List<String> list=mfnOrderDao.findBySql(sql,new Parameter(arrId));
			if(list.size()==0){
				return "0";
			}
		}
		return "1";
	}
	
	@Transactional(readOnly = false)
	public void cancelOrder(String orderId){
		String sql="update amazoninfo_ebay_order set status='9' WHERE order_id=:p1 ";
		mfnOrderDao.updateBySql(sql, new Parameter(orderId));
	}
	
	@Transactional(readOnly = false)
	public Map<Integer,Map<String,Integer>> savePackage(String[] arr,String packageNo,String country){
		Map<Integer,Map<String,Integer>> map=new HashMap<Integer,Map<String,Integer>>();
		
		Date date=new Date();
		String sql="update amazoninfo_ebay_order set status='1',package_id=:p1,bill_no=:p2 WHERE id=:p3 ";
		String sql2="update amazoninfo_ebay_order set package_id=:p1,bill_no=:p2  WHERE id=:p3 ";
		String updateEvent="update custom_event_manager set state='2',end_date=now() where id=:p1 ";
		MfnPackage mfnPackage=new MfnPackage();
		mfnPackage.setPrintTime(date);
		mfnPackage.setPackageNo(packageNo);
		mfnPackage.setStatus("1");
		mfnPackage.setCountry(country);
		mfnPackage.setPrintUser(UserUtils.getUser());
		mfnPackageService.save(mfnPackage);
		
		Map<String,Integer> orderBill=map.get(mfnPackage.getId());
		if(orderBill==null){
			orderBill=Maps.newHashMap();
			map.put(mfnPackage.getId(), orderBill);
		}
		
		///Integer billNO=getMaxBillNo();
		Integer billNO=getMaxBillNo("0",country);//amazon or ebay
		Integer billNO1=getMaxBillNo("1",country);//test
		Integer billNO2=getMaxBillNo("2",country);//support
		Integer billNO3=getMaxBillNo("3",country);//mfn
		List<MfnOrder> mfnOrderList=new ArrayList<MfnOrder>();
		for (String id : arr) {
			MfnOrder mfnOrder=mfnOrderDao.get(id);
			if(!id.endsWith("amazon")&&!id.endsWith("ebay")){
				if("1".equals(mfnOrder.getOrderType())){
					mfnOrderDao.updateBySql(sql,new Parameter(mfnPackage.getId(),++billNO1,id));
					orderBill.put(id, billNO1);
					/*if(StringUtils.isNotBlank(mfnOrder.getEventId())){
						String[] eventArr=mfnOrder.getEventId().split(",");
						for (String evnetId: eventArr) {
							mfnOrderDao.updateBySql(updateEvent,new Parameter(evnetId));
						}
					}*/
				}else if("2".equals(mfnOrder.getOrderType())){
					mfnOrderDao.updateBySql(sql,new Parameter(mfnPackage.getId(),++billNO2,id));
					orderBill.put(id, billNO2);
					if(StringUtils.isNotBlank(mfnOrder.getEventId())){
						String[] eventArr=mfnOrder.getEventId().split(",");
						if(StringUtils.isBlank(mfnOrder.getRemark())||(StringUtils.isNotBlank(mfnOrder.getRemark())&&!mfnOrder.getRemark().contains("display:none") ) ){
							for (String evnetId: eventArr) {
								mfnOrderDao.updateBySql(updateEvent,new Parameter(evnetId));
							}
						}
					
					}
				}else if("5".equals(mfnOrder.getOrderType())){
					mfnOrderDao.updateBySql(sql,new Parameter(mfnPackage.getId(),++billNO2,id));
					orderBill.put(id, billNO2);
				}else if("3".equals(mfnOrder.getOrderType())){
					mfnOrderDao.updateBySql(sql,new Parameter(mfnPackage.getId(),++billNO3,id));
					orderBill.put(id, billNO3);
				}
				
				
			}else{
				mfnOrderList.add(mfnOrder);
				mfnOrderDao.updateBySql(sql2,new Parameter(mfnPackage.getId(),++billNO,id));
				orderBill.put(id, billNO);
			}
			
		}
		final Integer packageId=mfnPackage.getId();
		if(mfnOrderList.size()==0){
			String sql4="update amazoninfo_ebay_package set status='2' where id=:p1";
			mfnOrderDao.updateBySql(sql4,new Parameter(packageId));
		}
		return map;
	}
	
	private Integer getMaxBillNo(String type,String country){
		String sql="SELECT (case when MAX(bill_no) is null  then 0 else MAX(bill_no) end) FROM amazoninfo_ebay_order where country like :p1 and ";
		if("2".equals(type)){
			sql+=" order_type in ('2','5') ";
		}else{
			sql+=" order_type="+type;
		}
		
		List<BigInteger> rs=mfnOrderDao.findBySql(sql,new Parameter(country+"%"));
		if(rs.size()>0){
			return rs.get(0).intValue();
		}else{
			return 0;
		}
	}
	
	@Transactional(readOnly = false)
	public void updatePackageStatu(Integer packageId){
		String sql="update amazoninfo_ebay_package set status='1' where id=:p1";
		mfnOrderDao.updateBySql(sql,new Parameter(packageId));
	}
	
	@Transactional(readOnly = false)
	public void updateAmazonAndEbayStatu(boolean flag,Integer packageId){
		String sql3="update amazoninfo_ebay_package set status=:p1 where id=:p2";
		if(flag){
			mfnOrderDao.updateBySql(sql3,new Parameter("2",packageId));
		}else{
			mfnOrderDao.updateBySql(sql3,new Parameter("3",packageId));
		}
	}

	//多渠道订单
	public  boolean  updateShippedByOrders(Set<String> orders,String country,AmazonAccountConfig config) {
		try {
			String interfaceUrl = BaseService.AMAZONLOGIN_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"),country,Lists.newArrayList(orders),config.getAccountName()};
			client.invoke("updateShippedState", str);
		} catch (Exception e) {
			logger.error(config.getAccountName()+"更新发货状态错误："+e.getMessage(), e);
		}
		return true;
	}

	public void updateOrderShippedInEbay(Map<String,Map<String, String>> orderTrackMap,String country) {
		ApiContext apiContext = new ApiContext();
		ApiCredential cred = apiContext.getApiCredential();
		
		if("de".equals(country)){
			cred.seteBayToken(EbayConstants.EBAYTOKEN);
			ApiAccount account = cred.getApiAccount();
			account.setDeveloper(EbayConstants.DEVID);
			account.setApplication(EbayConstants.APPID);
			account.setCertificate(EbayConstants.CERTID);
			apiContext.setApiServerUrl(EbayConstants.APISERVERURL);
			apiContext.setSite(SiteCodeType.GERMANY);
		}else{
			cred.seteBayToken(EbayConstants.EBAYTOKEN_US);
			ApiAccount account = cred.getApiAccount();
			account.setDeveloper(EbayConstants.DEVID_US);
			account.setApplication(EbayConstants.APPID_US);
			account.setCertificate(EbayConstants.CERTID_US);
			apiContext.setApiServerUrl(EbayConstants.APISERVERURL);
			apiContext.setSite(SiteCodeType.US);
		}

		ApiLogging apiLog = apiContext.getApiLogging();
		apiLog.setLogSOAPMessages(false);
		apiLog.setLogHTTPHeaders(false);
		apiLog.setLogExceptions(false);
		apiContext.setApiLogging(apiLog);
		CompleteSaleCall call = new CompleteSaleCall(apiContext);
		call.setEnableCompression(false);
		call.setShipped(true);
		 for (Map.Entry<String,Map<String,String>> entry: orderTrackMap.entrySet()) { 
		    String orderId =entry.getKey();
			try {
				call.setOrderID(orderId);
				ShipmentType shipmentType = new ShipmentType();
				List<ShipmentTrackingDetailsType> detailsTypeList = Lists.newArrayList(); 
				 for (Map.Entry<String,String> entryRs : entry.getValue().entrySet()) { 
				    String carrier =entryRs.getKey();
					ShipmentTrackingDetailsType detailsType = new ShipmentTrackingDetailsType();
					detailsType.setShippingCarrierUsed(carrier);
					detailsType.setShipmentTrackingNumber(entryRs.getValue());
					detailsTypeList.add(detailsType);
				}
				shipmentType.setShipmentTrackingDetails(detailsTypeList.toArray(new ShipmentTrackingDetailsType[detailsTypeList.size()]));
				call.setShipment(shipmentType);
				call.completeSale();
			} catch (ApiException e) {
				logger.warn("更改ebay订单状态api出错：" + e.getMessage(), e);
			} catch (SdkException e) {
				// logger.warn("更改ebay订单状态sdk出错："+e.getMessage(),e);
			} catch (Exception e) {
				logger.warn("更改ebay订单状态出错：" + e.getMessage(), e);
			}
		}	
	}
	
	public void updateOrderShippedInEbay2(String orderId,String country) {
		ApiContext apiContext = new ApiContext();
		ApiCredential cred = apiContext.getApiCredential();
		
		if("de".equals(country)){
			cred.seteBayToken(EbayConstants.EBAYTOKEN);
			ApiAccount account = cred.getApiAccount();
			account.setDeveloper(EbayConstants.DEVID);
			account.setApplication(EbayConstants.APPID);
			account.setCertificate(EbayConstants.CERTID);
			apiContext.setApiServerUrl(EbayConstants.APISERVERURL);
			apiContext.setSite(SiteCodeType.GERMANY);
		}else{
			cred.seteBayToken(EbayConstants.EBAYTOKEN_US);
			ApiAccount account = cred.getApiAccount();
			account.setDeveloper(EbayConstants.DEVID_US);
			account.setApplication(EbayConstants.APPID_US);
			account.setCertificate(EbayConstants.CERTID_US);
			apiContext.setApiServerUrl(EbayConstants.APISERVERURL);
			apiContext.setSite(SiteCodeType.US);
		}

		ApiLogging apiLog = apiContext.getApiLogging();
		apiLog.setLogSOAPMessages(false);
		apiLog.setLogHTTPHeaders(false);
		apiLog.setLogExceptions(false);
		apiContext.setApiLogging(apiLog);
		
		CompleteSaleCall call = new CompleteSaleCall(apiContext);
		call.setOrderID(orderId);
		call.setEnableCompression(false);
		call.setShipped(true);
		try {
			call.completeSale();
		}catch (Exception e) {
			logger.warn(orderId+"更改ebay订单状态出错：" + e.getMessage(), e);
		}
	}
	
	public static void main(String[] args) {
		/*List<MfnOrder> orders = Lists.newArrayList();
		
		List<String> orderList=Lists.newArrayList(
				"106-8842561-8603437",
				"113-0689025-3094655",
				"111-4463846-7613867");
        List<String> numberList=Lists.newArrayList("LK849992623CN","LK849992624CN","LK849992625CN");
		int i=0;
		for (String order: orderList) {
			MfnOrder a = new MfnOrder();
			a.setSupplier("ePacket");
			a.setTrackNumber(numberList.get(i++));
			a.setOrderId(order);
			orders.add(a);
		}

		new MfnOrderService().submitTrackCode("com",orders,new File("d:/1111"));
		
		*/
		//applicationContext.close();
	}
	
	public Map<String,Map<String,Integer>> findDPDAmount(MfnOrder mfnOrder){
		String sql="SELECT '1',country,SUM(s.`packages`) FROM amazoninfo_vendor_shipment s WHERE s.`carrierSCAC`='DPD' AND s.`shipped_date`>=:p1 and s.`shipped_date`<:p2 GROUP BY s.country "+
				" UNION ALL "+
				" SELECT '2',d.country,SUM(d.tray) FROM psi_fba_inbound d WHERE d.supplier='DPD' AND d.tray IS NOT NULL AND d.shipped_date>=:p1 and d.shipped_date<:p2 GROUP BY d.country "+
				" UNION ALL "+
				" SELECT '3',r.country,COUNT(*) FROM amazoninfo_ebay_order r JOIN amazoninfo_ebay_address e ON r.shipping_address=e.id "+
				" JOIN amazoninfo_ebay_package p ON r.package_id=p.id "+
				" WHERE r.package_id IS NOT NULL  AND (street IS NULL OR  (LOWER(street) NOT LIKE '%packstation%'  AND LOWER(street) NOT LIKE '%paketshop%'  AND LOWER(street) NOT LIKE '%postfiliale%')) "+ 
				" AND (street1 IS NULL OR  (LOWER(street1) NOT LIKE '%packstation%'  AND LOWER(street1) NOT LIKE '%paketshop%'  AND LOWER(street1) NOT LIKE '%postfiliale%'))  "+
				" AND (street2 IS NULL OR  (LOWER(street2) NOT LIKE '%packstation%'  AND LOWER(street2) NOT LIKE '%paketshop%'  AND LOWER(street2) NOT LIKE '%postfiliale%'))  "+
				" AND p.print_time>=:p1 and p.print_time<:p2 GROUP BY r.country ";
		Map<String,Map<String,Integer>> map=Maps.newHashMap();
		List<Object[]> list=mfnOrderDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(mfnOrder.getBuyTime()),new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(mfnOrder.getPaidTime(),1))));
		for (Object[] obj: list) {
			Map<String,Integer> temp=map.get(obj[1].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[1].toString(), temp);
			}
			temp.put(obj[0].toString(),((BigDecimal)obj[2]).intValue());
		}
		return map;
	}
	
	
	public Map<String,MfnOrder> findOrderByBillNo(Map<String,Set<String>> billNoMap,String country){
		Map<String,MfnOrder> map=Maps.newHashMap();
		String sql="SELECT r.id,r.order_id,r.`buyer_user`,r.`buyer_user_email`,r.`shipped_time`,r.bill_no,r.`track_number`,s.email,t.`title`,t.`quantity_purchased`,r.last_modified_time,r.country,r.account_name "+
			" FROM amazoninfo_ebay_order r  JOIN amazoninfo_ebay_orderitem t ON r.`id`=t.`order_id` "+
			" LEFT JOIN sys_user s ON r.create_user=s.id AND s.del_flag='0' WHERE r.order_type IN :p1 AND r.`bill_no` IN :p2 and r.country like :p3 AND t.title NOT LIKE '%Old' AND t.title NOT LIKE '%Other' ";
		
		for (Map.Entry<String,Set<String>> entity: billNoMap.entrySet()) {
			Set<String> typeSet=Sets.newHashSet();
			Set<String> billSet=entity.getValue();
			String type=entity.getKey();
			if(type.contains("2")){
				typeSet.add("2");
				typeSet.add("5");
			}else if(type.contains("1")){
				typeSet.add("1");
			}else if(type.contains("3")){
				typeSet.add("3");
			}else{
				typeSet.add("0");
			}
			List<Object[]> list=mfnOrderDao.findBySql(sql, new Parameter(typeSet,billSet,country+"%"));
			for (Object[] obj: list) {
				MfnOrder order=new MfnOrder();
				order.setBillNo(Integer.parseInt(obj[5].toString()));
				order.setOrderType(type);
				MfnOrder temp=map.get(order.getGroupBillNo());
				if(temp==null){
					order.setId(obj[0].toString());
					order.setOrderId(obj[1].toString());
					order.setBuyerUser(obj[2].toString());
					if(obj[3]!=null&&StringUtils.isNotBlank(obj[3].toString())){
						order.setBuyerUserEmail(obj[3].toString());
					}
					if(obj[4]!=null){
						order.setShippedTime((Timestamp)obj[4]);
					}
					
					if(obj[6]!=null&&StringUtils.isNotBlank(obj[6].toString())){
						order.setTrackNumber(obj[6].toString());
					}
					if(obj[7]!=null&&StringUtils.isNotBlank(obj[7].toString())){
						order.setRemark(obj[7].toString());
					}
					List<MfnOrderItem>  items=Lists.newArrayList();
					MfnOrderItem item=new MfnOrderItem();
					if(obj[8]!=null&&StringUtils.isNotBlank(obj[8].toString())){
						item.setTitle(obj[8].toString());
					}
					item.setQuantityPurchased(Integer.parseInt(obj[9].toString()));
					if(obj[10]!=null){
						order.setLastModifiedTime((Timestamp)obj[10]);
					}
					order.setCountry(obj[11].toString());
					if(obj[12]!=null){
						order.setAccountName(obj[12].toString());
					}
					item.setOrder(order);
					items.add(item);
					order.setItems(items);
					map.put(order.getGroupBillNo(), order);
				}else{
					List<MfnOrderItem>  items=temp.getItems();
					MfnOrderItem item=new MfnOrderItem();
					if(obj[8]!=null&&StringUtils.isNotBlank(obj[8].toString())){
						item.setTitle(obj[8].toString());
					}
					item.setQuantityPurchased(Integer.parseInt(obj[9].toString()));
					item.setOrder(order);
					items.add(item);
				}
			}
		}			
		return map;
	}
	
	@Transactional(readOnly = false)
	public Map<String,List<MfnOrder>> updateTrackNumber(String type,Map<String,String> trackMap,String country){
		
		Map<String,List<MfnOrder>> map=Maps.newHashMap();
		List<MfnOrder> mfnOrderList0=Lists.newArrayList();
		List<MfnOrder> mfnOrderList1=Lists.newArrayList();
		List<MfnOrder> mfnOrderList2=Lists.newArrayList();
		List<MfnOrder> mfnOrderList3=Lists.newArrayList();
		Map<String,String> updateTrackMap=Maps.newHashMap();
		for (Map.Entry<String,String> entry : trackMap.entrySet()) { 
			try{
				String billNo=entry.getKey();
				MfnOrder order=new MfnOrder();
				order.setSupplier(type);
				order.setTrackNumber(entry.getValue());
				order.setCountry(country);
				if(billNo.contains("Ersatz")){//2 5
					Integer bill=Integer.parseInt(billNo.replace("Ersatz", "").trim());
					Object[] obj=findOrderNoByBillNo2(bill,country);
					if(obj!=null&&obj.length>0){
						updateTrackMap.put(obj[0].toString(),entry.getValue());
						order.setId(obj[0].toString());
						order.setOrderId(obj[1].toString());
						order.setBuyerUser(obj[2].toString());
						if(obj[5]!=null){
							User user=systemService.getUser(obj[5].toString());
							if(user!=null){
								order.setRemark(user.getEmail());
							}
						}
						if(obj[3]!=null&&StringUtils.isNotBlank(obj[3].toString())&&(obj[6]==null||!entry.getValue().equals(obj[6].toString()))){
						//if(obj[3]!=null&&StringUtils.isNotBlank(obj[3].toString())){
							order.setBuyerUserEmail(obj[3].toString());
							mfnOrderList1.add(order);
						}
					}
				}else if(billNo.contains("Test")){//"1".equals(order.getOrderType())
					Integer bill=Integer.parseInt(billNo.replace("Test", "").trim());
					Object[] obj=findOrderNoByBillNo("1",bill,country);
	                if(obj!=null&&obj.length>0){
	                	updateTrackMap.put(obj[0].toString(),entry.getValue());
	                	order.setId(obj[0].toString());
	    				order.setOrderId(obj[1].toString());
	    				order.setBuyerUser(obj[2].toString());
	    				if(obj[5]!=null){
							User user=systemService.getUser(obj[5].toString());
							if(user!=null){
								order.setRemark(user.getEmail());
							}
						}
	    				//if(obj[3]!=null&&StringUtils.isNotBlank(obj[3].toString())){
	    				if(obj[3]!=null&&StringUtils.isNotBlank(obj[3].toString())&&(obj[6]==null||!entry.getValue().equals(obj[6].toString()))){
	    					order.setBuyerUserEmail(obj[3].toString());
	    					mfnOrderList1.add(order);
	    				}
					}
					
				}else if(billNo.contains("Mfn")){//3
					Integer bill=Integer.parseInt(billNo.replace("Mfn", "").trim());
					Object[] obj=findOrderNoByBillNo("3",bill,country);
	                if(obj!=null&&obj.length>0){
	                	updateTrackMap.put(obj[0].toString(),entry.getValue());
	                	order.setId(obj[0].toString());
	    				order.setOrderId(obj[1].toString());
	    				order.setBuyerUser(obj[2].toString());
	    				if(obj[5]!=null){
							User user=systemService.getUser(obj[5].toString());
							if(user!=null){
								order.setRemark(user.getEmail());
							}
						}
	    				if(obj[3]!=null&&StringUtils.isNotBlank(obj[3].toString())&&(obj[6]==null||!entry.getValue().equals(obj[6].toString()))){
	    				//if(obj[3]!=null&&StringUtils.isNotBlank(obj[3].toString())){
	    					order.setBuyerUserEmail(obj[3].toString());
	    					mfnOrderList1.add(order);
	    					mfnOrderList2.add(order);
	    				}
					}
					
				}else{//0
					char strs[] = billNo.toCharArray();
					int index = 0;
					int len=billNo.length();
					for(int i=0; i<len; i++){
					   if('0'!=strs[i]){
					       index=i;
					       break;
					   }
					}
					Integer bill=Integer.parseInt(billNo.substring(index, len));
					Object[] obj=findOrderNoByBillNo("0",bill,country);
	                if(obj!=null&&obj.length>0){
	                	updateTrackMap.put(obj[0].toString(),entry.getValue());   
	                	order.setId(obj[0].toString());
	                	order.setOrderId(obj[1].toString());
						order.setBuyerUser(obj[2].toString());
						if(obj[5]!=null){
							User user=systemService.getUser(obj[5].toString());
							if(user!=null){
								order.setRemark(user.getEmail());
							}
						}
	    				if(obj[0].toString().endsWith("_amazon")){
	    					if(obj[3]!=null&&StringUtils.isNotBlank(obj[3].toString())&&(obj[6]==null||!entry.getValue().equals(obj[6].toString()))){
	    					//if(obj[3]!=null&&StringUtils.isNotBlank(obj[3].toString())){
	    						order.setBuyerUserEmail(obj[3].toString());
		    					order.setShippedTime((Timestamp)obj[4]);
								mfnOrderList0.add(order);
	    						if(obj[7]!=null){
	    							order.setAccountName(obj[7].toString());
	    						}
	    					}
	    				}else{
	    					//if(obj[3]!=null&&StringUtils.isNotBlank(obj[3].toString())){
	    					if(obj[3]!=null&&StringUtils.isNotBlank(obj[3].toString())&&(obj[6]==null||!entry.getValue().equals(obj[6].toString()))){
	    						order.setBuyerUserEmail(obj[3].toString());
	    						mfnOrderList1.add(order);
	    						mfnOrderList3.add(order);
	    					}
	    				}
					}
					
				}
			}catch(Exception e){
			}
		}
		map.put("0", mfnOrderList0);
		map.put("1", mfnOrderList1);
		map.put("2", mfnOrderList2);
		map.put("3", mfnOrderList3);
		if(updateTrackMap!=null&&updateTrackMap.size()>0){
			updateTrackNumber(updateTrackMap,type);
		}
		return map;
	}
	
	public Object[] findOrderNoByBillNo(String type,Integer billNo,String country){
		String sql="SELECT r.id,order_id,r.`buyer_user`,r.`buyer_user_email`,r.`shipped_time`,r.create_user,r.track_number,r.account_name FROM amazoninfo_ebay_order r  WHERE r.order_type=:p1 AND r.`bill_no`=:p2 and r.country=:p3 ";
		List<Object[]> list=mfnOrderDao.findBySql(sql, new Parameter(type,billNo,country));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public Object[] findOrderNoByBillNo(String type,String orderId,String country){
		String sql="SELECT r.id,order_id,r.`buyer_user`,r.`buyer_user_email`,r.`shipped_time`,r.create_user,r.track_number FROM amazoninfo_ebay_order r  WHERE r.order_type=:p1 AND r.`order_id`=:p2 and r.country=:p3 ";
		List<Object[]> list=mfnOrderDao.findBySql(sql, new Parameter(type,orderId,country));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	
	public Object[] findOrderNoByBillNo2(Integer billNo,String country){
		String sql="SELECT r.id,order_id,r.`buyer_user`,r.`buyer_user_email`,r.`shipped_time`,r.create_user,r.track_number FROM amazoninfo_ebay_order r WHERE r.order_type in ('2','5') AND r.`bill_no`=:p1 and r.country=:p2 ";
		List<Object[]> list=mfnOrderDao.findBySql(sql, new Parameter(billNo,country));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public String updateTrackNumber(Map<String,String> trackMap,String type,Map<String,Integer> quantityMap,Integer wareHouseId){
		if(quantityMap!=null&&quantityMap.size()>0){
         	String info=psiInventoryOutService.outBoundByProduct(wareHouseId,quantityMap);
         	if(StringUtils.isNotBlank(info)){
 			    return info;
         	}
        }
		if(StringUtils.isNotBlank(type)){
			String sql="update  amazoninfo_ebay_order set supplier=:p1,track_number=:p2,tracking_flag='2' where id=:p3  ";
			for (Map.Entry<String,String> entry : trackMap.entrySet()) { 
			    String id=entry.getKey();
				mfnOrderDao.updateBySql(sql, new Parameter(type,entry.getValue(),id));
			}
		}else{
			String sql="update  amazoninfo_ebay_order set tracking_flag='1' where id in :p1  ";
			mfnOrderDao.updateBySql(sql, new Parameter(trackMap.keySet()));
		}
		return "";
	}
	
	@Transactional(readOnly = false)
	public String updateTrackNumber(Map<String,String> trackMap,String type,Map<String,Integer> quantityMap,Map<String,Integer> offlineQuantityMap,Integer wareHouseId){
		if(quantityMap!=null&&quantityMap.size()>0){
         	String info=psiInventoryOutService.outBoundByProduct(wareHouseId,quantityMap,offlineQuantityMap);
         	if(StringUtils.isNotBlank(info)){
 			    return info;
         	}
        }
		if(StringUtils.isNotBlank(type)){
			String sql="update  amazoninfo_ebay_order set supplier=:p1,track_number=:p2,tracking_flag='2' where id=:p3  ";
			for (Map.Entry<String,String> entry : trackMap.entrySet()) { 
			    String id=entry.getKey();
				mfnOrderDao.updateBySql(sql, new Parameter(type,entry.getValue(),id));
			}
		}else{
			String sql="update  amazoninfo_ebay_order set tracking_flag='1' where id in :p1  ";
			mfnOrderDao.updateBySql(sql, new Parameter(trackMap.keySet()));
		}
		return "";
	}
	
	@Transactional(readOnly = false)
	public void updateTrackNumber(Map<String,String> trackMap,String type){
		String sql="update  amazoninfo_ebay_order set supplier=:p1,track_number=:p2 where id=:p3  ";
		for (Map.Entry<String,String> entry : trackMap.entrySet()) { 
		    String id=entry.getKey();
			mfnOrderDao.updateBySql(sql, new Parameter(type,entry.getValue(),id));
		}
		
	}
	
	@Transactional(readOnly = false)
	public void updateTrackNumber(List<MfnOrder> orderList){
		String sql="update  amazoninfo_ebay_order set supplier=:p1,track_number=:p2 where order_id=:p3";
		for (MfnOrder order: orderList) {
			mfnOrderDao.updateBySql(sql, new Parameter(order.getSupplier(),order.getTrackNumber(),order.getOrderId()));
		}
	}
	
	//true已发货 不能删
	public boolean isShippedOrder(String orderId){
		String sql="SELECT count(1) FROM amazoninfo_ebay_order r WHERE r.`order_id`=:p1 and r.bill_no is not null";//
		List<Object> count=mfnOrderDao.findBySql(sql,new Parameter(orderId));
		return ((BigInteger)count.get(0)).intValue()>0;
	}
	
	/**
	 * 统计所有产品的销量
	 * @return Map<String,Integer> [productName qty]
	 */
	public Map<String, Integer> getAllSalesVolume(){
		Map<String,Integer> rs = Maps.newLinkedHashMap();
		String sql="SELECT t.`title`,SUM(t.`quantity_shipped`) FROM `amazoninfo_ebay_orderitem` t,`amazoninfo_ebay_order` o"+ 
				" WHERE t.`order_id`=o.`id` AND o.`status`='1' AND t.`title` IS NOT NULL GROUP BY t.`title`";
		List<Object[]> list= mfnOrderDao.findBySql(sql);
		for(Object[] obj:list){
			String productName = obj[0].toString();
			Integer quantity = obj[1]==null?0:Integer.parseInt(obj[1].toString());
			rs.put(productName, quantity);
	    }
		return rs;
	}
	
	 @Transactional(readOnly = false)
	 public  void updateShipFlag(String id){
		String sql="update amazoninfo_ebay_order set remark=CONCAT(ifnull(remark,''),' ',:p2),status='9' where id=:p1 ";
		String remark = UserUtils.getUser().getName()+" cancel";
		mfnOrderDao.updateBySql(sql, new Parameter(id,remark));
	}
	 
	 public List<MfnOrder> findAllEbayOrder(){
			String sql="SELECT id,order_id,r.`buyer_user`,r.`buyer_user_email`,r.`shipped_time`,r.create_user,r.track_number,r.country,supplier FROM amazoninfo_ebay_order r WHERE r.order_type=0 and country='com' and track_number is not null AND r.id like '%amazon' ";
			List<Object[]> list=mfnOrderDao.findBySql(sql);
			List<MfnOrder> tempList=Lists.newArrayList();
			for (Object[] obj: list) {
				MfnOrder order=new MfnOrder();
				order.setId(obj[0].toString());
	        	order.setOrderId(obj[1].toString());
				order.setBuyerUser(obj[2].toString());
				order.setCountry(obj[7].toString());
				order.setBuyerUserEmail(obj[3].toString());
				order.setTrackNumber(obj[6].toString());
				order.setSupplier(obj[8].toString());
				tempList.add(order);
			}
			return tempList;
		}
	 
	 public List<String> findTrackingWarnOrder(String country){
		 /*String sql="";
		 if("de".equals(country)){//24点
			 sql="SELECT r.`order_id` FROM amazoninfo_ebay_order r  "+
						" WHERE r.`last_modified_time`>=DATE_ADD(NOW(), INTERVAL -2 HOUR) AND r.`last_modified_time`<DATE_ADD(NOW(), INTERVAL 1 HOUR) "+
						" and r.country='de' AND r.id LIKE '%_amazon' AND r.`status`='1'";
		 }else if("com".equals(country)){//9点
			 sql="SELECT r.`order_id` FROM amazoninfo_ebay_order r "+
						" WHERE r.`last_modified_time`>=DATE_ADD(NOW(), INTERVAL -12 HOUR) AND r.`last_modified_time`<NOW() "+
						" and r.country='com' AND r.id LIKE '%_amazon' AND r.`status`='1'";
		 }*/
		 String sql="SELECT r.`order_id` FROM amazoninfo_ebay_order r join amazoninfo_ebay_package p on r.`package_id`=p.id  where p.`print_time`>=:p1 and r.country=:p2 AND r.`status`='1' AND r.id LIKE '%_amazon'  ";
		 return mfnOrderDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-3),country));
	 }
	 
	 public Map<String,Integer> findUnshippedQuantity(String country){
		 Map<String,Integer> map=Maps.newHashMap();
		 String sql="SELECT t.`title`,SUM(t.`quantity_purchased`) FROM amazoninfo_ebay_order r "+
				 " JOIN amazoninfo_ebay_orderitem t ON r.id=t.`order_id` "+
				 " WHERE r.buy_time>=:p1 and r.`country`=:p2  AND r.`status`!='9' AND r.`supplier` IS NULL group by t.title";
		 List<Object[]> list=mfnOrderDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-30),country));
		 if(list!=null&&list.size()>0){
			 for (Object[] obj: list) {
				 if(obj[0]!=null&&obj[1]!=null){
					 map.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
				 }
			}
		 }
		 return map;
	 }
	 
	 public Map<String,MfnOrder> findOrder(Set<String> orderIds,String country){
		 Map<String,MfnOrder> map=Maps.newHashMap();
		 for (String  billNo : orderIds) {
			 try{
				    Object[] obj=null;
				    if(billNo.contains("-")){
						obj=findOrderNoByBillNo("0",billNo,country);
				    }else{
				    	char strs[] = billNo.toCharArray();
						int index = 0;
						int len=billNo.length();
						for(int i=0; i<len; i++){
						   if('0'!=strs[i]){
						       index=i;
						       break;
						   }
						}
						Integer bill=Integer.parseInt(billNo.substring(index, len));
						obj=findOrderNoByBillNo("0",bill,country);
				    }
					
		            if(obj!=null&&obj.length>0&&obj[6]==null){
		            	 //id,order_id,r.`buyer_user`,r.`buyer_user_email`,r.`shipped_time`,r.create_user,r.track_number
		 				 if(obj[0].toString().endsWith("_amazon")){
		 					 MfnOrder order=new MfnOrder();
		 	             	 order.setId(obj[0].toString());
		 	             	 order.setOrderId(obj[1].toString());
		 	             	 order.setShippedTime((Timestamp)obj[4]);
		 	             	 order.setCountry(country);
		 	             	 map.put(billNo, order);
		 				 }
					 }
			 }catch(Exception e){}
			   
		 }
		 return map;
	 }
	 
	 public Map<String,Integer> findPrintQuantity(String country){
		 Map<String,Integer> map=Maps.newHashMap();
		 String sql="SELECT t.`title`,SUM(t.`quantity_purchased`) FROM amazoninfo_ebay_package p "+
					" JOIN amazoninfo_ebay_order r ON p.id=r.`package_id` "+
					" JOIN amazoninfo_ebay_orderitem t ON t.`order_id`=r.`id` "+
					" WHERE p.`print_time`>=:p1 AND p.`country`=:p2 and t.`title` is not null GROUP BY t.`title` ";
		 List<Object[]> list=mfnOrderDao.findBySql(sql,new Parameter(DateUtils.addHours(new Date(),-2),country));
		 for (Object[] obj: list) {
			map.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
		 }
		 return map;
	 }
	
	 @Transactional(readOnly = false)
	 public void updateTrackingInfo(String supplier,String trackNumber,String image,String trackingFlag,Float fee,String id){
		 String sql="update amazoninfo_ebay_order set supplier=:p1,track_number=:p2,label_image=:p3,tracking_flag=:p4,fee=:p5 where id=:p6";
		 mfnOrderDao.updateBySql(sql, new Parameter(supplier,trackNumber,image,trackingFlag,fee,id));
	 }
	 
	 @Transactional(readOnly = false)
	 public void updateTrackingInfo(Set<String> orderId){
		 String sql="update amazoninfo_ebay_order set tracking_flag='1' where order_id in :p1";
		 mfnOrderDao.updateBySql(sql, new Parameter(orderId));
	 }
	 
	 @Transactional(readOnly = false)
	 public void updateTrackingRemark(String id,String remark){
		 String sql="update amazoninfo_ebay_order set tracking_remark=:p2 where id = :p1";
		 mfnOrderDao.updateBySql(sql, new Parameter(id,remark));
	 }
	 
	 
	 @Transactional(readOnly = false)
	 public void updatePackageRemark(Integer id,String remark){
		 String sql="update amazoninfo_ebay_package set remark=:p2 where id = :p1";
		 mfnOrderDao.updateBySql(sql, new Parameter(id,remark));
	 }
	 
	 public Map<String,List<MfnOrder>> updateTracking(){
		  Map<String,List<MfnOrder>> map=Maps.newHashMap();
		  String sql="SELECT r.id,r.`country`,r.`account_name`,r.`order_id`,r.`supplier`,r.`track_number`,p.`print_time` "+
					" FROM amazoninfo_ebay_order r JOIN amazoninfo_ebay_package p ON r.`package_id`= p.id "+
					" WHERE p.`print_time`>=:p1 AND r.`order_type`='0' AND r.`tracking_flag`='0' and r.supplier is not null and r.track_number is not null and r.country like 'com%'";
		  List<Object[]> list=mfnOrderDao.findBySql(sql,new Parameter(DateUtils.addHours(new Date(),-3)));
		  for (Object[] obj : list) {
			  String id = obj[0].toString();
			  String country = obj[1].toString();
			  String accountName="";
			  String orderId = obj[3].toString();
			  String supplier= obj[4].toString();
			  String trackNumber= obj[5].toString();
			  Timestamp time=(Timestamp)obj[6];
			  if(id.endsWith("ebay")){
				  accountName="Ebay";
			  }else{
				  accountName=obj[2].toString();
			  }
			    MfnOrder order=new MfnOrder();
			    order.setOrderId(orderId);
			    order.setSupplier(supplier);
			    order.setTrackNumber(trackNumber);
			    if(!"Ebay".equals(accountName)){
				    Date date=new Date(time.getTime());
				    order.setTrackingDate(DateUtils.addHours(date,1));
			    }
			    order.setCountry(country);
			    List<MfnOrder> tempList=map.get(accountName);
			    if(tempList==null){
			    	list=Lists.newArrayList();
			    	map.put(accountName,tempList);
			    }
			    tempList.add(order);
		  }
		  return map;
	 }
	 
	 public List<Object[]> findOrderNoFee(MfnOrder mfnOrder){
			String sql="select order_id,supplier,track_number from amazoninfo_ebay_order where buy_time>=:p1 and buy_time<:p2 and track_number is not null and fee is null ";
			return mfnOrderDao.findBySql(sql,new Parameter(mfnOrder.getBuyTime(),DateUtils.addDays(mfnOrder.getLastModifiedTime(), 1)));
	}
	 
	 @Transactional(readOnly = false)
	 public void updateFee(Map<String,Float> feeMap){
			String sql="update amazoninfo_ebay_order set fee=:p1 where order_id=:p2 ";
			for ( Map.Entry<String,Float> entry: feeMap.entrySet()) {
				mfnOrderDao.updateBySql(sql,new Parameter(entry.getValue(),entry.getKey()));
			}
	} 

}
