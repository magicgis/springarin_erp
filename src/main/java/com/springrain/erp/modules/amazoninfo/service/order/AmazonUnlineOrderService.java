package com.springrain.erp.modules.amazoninfo.service.order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.order.AmazonUnlineOrderDao;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnOrder;
import com.springrain.erp.modules.psi.dao.PsiInventoryDao;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiQualityChangeBill;
import com.springrain.erp.modules.psi.entity.PsiQualityChangeBillItem;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiQualityChangeBillService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 亚马逊线下订单
 * @author Eileen
 * @version 2015-06-29
 */
@Component
@Transactional(readOnly = true)
public class AmazonUnlineOrderService extends BaseService {

	@Autowired
	private AmazonUnlineOrderDao          amazonUnlineOrdertDao;
	@Autowired
	private PsiQualityChangeBillService   qualityService;
	@Autowired
	private PsiInventoryService           psiInventoryService;
	@Autowired
	private PsiInventoryDao               psiInventoryDao;
	@Autowired
	private MailManager						mailManager;
	@Autowired
	private PsiProductGroupUserService      groupUserService;
	
	public AmazonUnlineOrder get(Integer id) {
		return amazonUnlineOrdertDao.get(id);
	}
	
	public List<AmazonUnlineOrder> findWaitSysnOrder(){
		DetachedCriteria dc = amazonUnlineOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.eq("orderStatus","Waiting for delivery"));
		List<AmazonUnlineOrder> list=amazonUnlineOrdertDao.find(dc);
		for (AmazonUnlineOrder amazonUnlineOrder : list) {
			Hibernate.initialize(amazonUnlineOrder.getItems());
			Hibernate.initialize(amazonUnlineOrder.getShippingAddress());
			Hibernate.initialize(amazonUnlineOrder.getInvoiceAddress());
		}
		return list;
	}
	
	
	public Map<String,AmazonUnlineOrder> isNotExist(Set<String> orderIdSet) {
		Map<String,AmazonUnlineOrder> map=Maps.newHashMap();
		DetachedCriteria dc = amazonUnlineOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.in("amazonOrderId", orderIdSet));
		List<AmazonUnlineOrder> list=amazonUnlineOrdertDao.find(dc);
		if(list!=null){
			for (AmazonUnlineOrder amazonUnlineOrder : list) {
				Hibernate.initialize(amazonUnlineOrder.getItems());
				Hibernate.initialize(amazonUnlineOrder.getShippingAddress());
				Hibernate.initialize(amazonUnlineOrder.getInvoiceAddress());
				map.put(amazonUnlineOrder.getAmazonOrderId(), amazonUnlineOrder);
			}
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void delete(Integer id){
	  String sql="delete from amazoninfo_unline_orderitem where id=:p1";
	  amazonUnlineOrdertDao.updateBySql(sql, new Parameter(id));
	}
	
	@Transactional(readOnly = false)
	public void delete(Set<Integer> id){
	  String sql="delete from amazoninfo_unline_orderitem where id in :p1";
	  amazonUnlineOrdertDao.updateBySql(sql, new Parameter(id));
	}
	
	
	public AmazonUnlineOrder getByOrderId(String orderId){
		DetachedCriteria dc = amazonUnlineOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.eq("amazonOrderId",orderId));
		List<AmazonUnlineOrder> list=amazonUnlineOrdertDao.find(dc);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public List<AmazonUnlineOrder> getByOrderIds(Set<String> orderId){
		DetachedCriteria dc = amazonUnlineOrdertDao.createDetachedCriteria();
		dc.add(Restrictions.in("amazonOrderId",orderId));
		return amazonUnlineOrdertDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public String updateLadingBillNo(Integer tranId,String ladingBillNo){
		String sql ="update  amazoninfo_unline_order set bill_no=:p1 where id=:p2";
		int i =this.amazonUnlineOrdertDao.updateBySql(sql, new Parameter(ladingBillNo,tranId));
		if(i>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	@Transactional(readOnly = false)
	public void updateTrackNumber(List<MfnOrder> mfnOrders){
		String sql="update  amazoninfo_unline_order set bill_no=:p1,supplier=:p2 where amazon_order_id=:p3";
		for (MfnOrder mfnOrder: mfnOrders) {
			amazonUnlineOrdertDao.updateBySql(sql, new Parameter(mfnOrder.getTrackNumber(),mfnOrder.getSupplier(),mfnOrder.getOrderId()));
		}
	}
	
	@Transactional(readOnly = false)
	public void updateTrackNumber(String trackNumber,String supplier,String orderId){
		String sql="update  amazoninfo_unline_order set bill_no=:p1,supplier=:p2 where amazon_order_id=:p3";
	    amazonUnlineOrdertDao.updateBySql(sql, new Parameter(trackNumber,supplier,orderId));
	}
	
	public Page<AmazonUnlineOrder> find(Page<AmazonUnlineOrder> page, AmazonUnlineOrder amazonUnlineOrder) {
		DetachedCriteria dc = amazonUnlineOrdertDao.createDetachedCriteria();
		String search = amazonUnlineOrder.getSellerOrderId();
		/*if (StringUtils.isNotEmpty(search)){
			try {
				int id = Integer.parseInt(search);
				dc.add(Restrictions.or(Restrictions.eq("id",id),Restrictions.eq("amazonOrderId",search)));
			} catch (NumberFormatException e) {
				dc.createAlias("this.items", "item");
				dc.add(Restrictions.or(Restrictions.like("amazonOrderId", "%"+search+"%"),
						Restrictions.like("buyerEmail","%"+search+"%"),Restrictions.like("item.sellersku","%"+search+"%"),Restrictions.like("customId","%"+search+"%")));
			}
		}else{*/
			if (amazonUnlineOrder.getSalesChannel()!=null&&amazonUnlineOrder.getSalesChannel().getId()!=null){
				dc.createAlias("this.salesChannel", "salesChannel");
				dc.add(Restrictions.eq("salesChannel.id",amazonUnlineOrder.getSalesChannel().getId()));
			}
			
			if (amazonUnlineOrder.getPurchaseDate()!=null){
				dc.add(Restrictions.ge("purchaseDate",amazonUnlineOrder.getPurchaseDate()));
			}
			if (amazonUnlineOrder.getLastUpdateDate()!=null){
				dc.add(Restrictions.le("purchaseDate",DateUtils.addDays(amazonUnlineOrder.getLastUpdateDate(),1)));
			}
			if(StringUtils.isNotEmpty(amazonUnlineOrder.getOrderStatus())){
				dc.add(Restrictions.eq("orderStatus", amazonUnlineOrder.getOrderStatus()));
			}else{
				dc.add(Restrictions.ne("orderStatus","Canceled"));
			}
			
			if(StringUtils.isNotBlank(amazonUnlineOrder.getOrderChannel())){
				if("2".equals(amazonUnlineOrder.getOrderChannel())){
					dc.add(Restrictions.eq("orderChannel","check24"));
				}else if("0".equals(amazonUnlineOrder.getOrderChannel())){//非管理员
					dc.add(Restrictions.and(Restrictions.ne("orderChannel","管理员"),Restrictions.ne("orderChannel","check24")));
					dc.add(Restrictions.not(Restrictions.like("orderChannel","%-OTHER-%")));
				}else if("3".equals(amazonUnlineOrder.getOrderChannel())){
					dc.add(Restrictions.like("orderChannel","%-OTHER-%"));
				}else{
					dc.add(Restrictions.eq("orderChannel","管理员"));
				}
			}
			
			if (StringUtils.isNotEmpty(search)){
				try {
					int id = Integer.parseInt(search);
					dc.add(Restrictions.or(Restrictions.eq("id",id),Restrictions.eq("amazonOrderId",search)));
				} catch (NumberFormatException e) {
					dc.createAlias("this.items", "item");
					dc.add(Restrictions.or(Restrictions.eq("invoiceNo",search),Restrictions.like("amazonOrderId", "%"+search+"%"),
							Restrictions.like("buyerEmail","%"+search+"%"),Restrictions.like("item.sellersku","%"+search+"%"),Restrictions.like("customId","%"+search+"%")));
				}
			}
		/*}
		*/
		dc.add(Restrictions.isNull("cancelDate"));
		return amazonUnlineOrdertDao.find(page, dc);
	}
	
	public List<AmazonUnlineOrder> find(AmazonUnlineOrder amazonUnlineOrder) {
		DetachedCriteria dc = amazonUnlineOrdertDao.createDetachedCriteria();
		
		if (amazonUnlineOrder.getSalesChannel()!=null&&amazonUnlineOrder.getSalesChannel().getId()!=null){
			dc.createAlias("this.salesChannel", "salesChannel");
			dc.add(Restrictions.eq("salesChannel.id",amazonUnlineOrder.getSalesChannel().getId()));
		}
		
		if (amazonUnlineOrder.getPurchaseDate()!=null){
			dc.add(Restrictions.ge("purchaseDate",amazonUnlineOrder.getPurchaseDate()));
		}
		if (amazonUnlineOrder.getLastUpdateDate()!=null){
			dc.add(Restrictions.le("purchaseDate",DateUtils.addDays(amazonUnlineOrder.getLastUpdateDate(),1)));
		}
		Set<String> status=Sets.newHashSet("Shipped","Unshipped","Pending");
		dc.add(Restrictions.in("orderStatus",status));
		dc.add(Restrictions.isNull("cancelDate"));
		return amazonUnlineOrdertDao.find(dc);
	}
	
	public List<AmazonUnlineOrder> findNoInvoiceFlag() {
		DetachedCriteria dc = amazonUnlineOrdertDao.createDetachedCriteria();
		dc.createAlias("this.salesChannel", "salesChannel");
		dc.add(Restrictions.eq("salesChannel.id",19));
		dc.add(Restrictions.eq("orderStatus","Pending"));
		dc.add(Restrictions.eq("invoiceFlag","000"));
		dc.add(Restrictions.eq("orderChannel","管理员"));
		dc.add(Restrictions.isNull("cancelDate"));
		List<AmazonUnlineOrder> list=amazonUnlineOrdertDao.find(dc);
		if(list!=null&&list.size()>0){
			for (AmazonUnlineOrder order : list) {
				Hibernate.initialize(order.getItems());
				Hibernate.initialize(order.getShippingAddress());
				Hibernate.initialize(order.getInvoiceAddress());
			}
		}
		return list;
	}
	
	@Transactional(readOnly = false)
	public void updateInvoiceFlag(Set<String> orderIdSet){
		String sql="update  amazoninfo_unline_order set invoice_flag='001' where amazon_order_id in :p1";
	    amazonUnlineOrdertDao.updateBySql(sql, new Parameter(orderIdSet));
	}
	
	@Transactional(readOnly = false)
	public void  updateInvoiceNoById(String invoiceNo,Integer id) {
		amazonUnlineOrdertDao.updateBySql("update amazoninfo_unline_order set invoice_no=:p1 WHERE id=:p2", new Parameter(invoiceNo,id));
	}
	
	
	public List<Object[]> count(AmazonUnlineOrder amazonUnlineOrder) {
		String sql="SELECT CONCAT(t.`product_name`,IF(t.`color`='','',CONCAT('_',t.`color`))),r.`sales_channel`,t.`country`,SUM(t.`quantity_ordered`) "+
          " FROM amazoninfo_unline_order r JOIN amazoninfo_unline_orderitem  t ON r.id=t.`order_id`  "+
          " WHERE r.cancel_date IS NULL AND r.`order_status` IN ('Shipped','Unshipped','Pending')  "+
          "  AND r.`purchase_date`>=:p1 AND r.`purchase_date`<:p2 ";
          if(StringUtils.isNotBlank(amazonUnlineOrder.getOrderChannel())){
        	  if("0".equals(amazonUnlineOrder.getOrderChannel())){
        		  sql+=" and r.order_channel!='管理员' and r.order_channel!='check24' ";
        	  }else if("2".equals(amazonUnlineOrder.getOrderChannel())){
        		  sql+=" and r.order_channel='check24' ";
        	  }else{
        		  sql+=" and r.order_channel='管理员' ";
        	  }
          }
        sql+= " GROUP BY CONCAT(t.`product_name`,IF(t.`color`='','',CONCAT('_',t.`color`))),r.`sales_channel`,t.`country` ";
		return amazonUnlineOrdertDao.findBySql(sql, new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(amazonUnlineOrder.getPurchaseDate()),new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(amazonUnlineOrder.getLastUpdateDate(),1))));
	}
	
	@Transactional(readOnly = false)
	public void updateOutBound(AmazonUnlineOrder amazonUnlineOrder,String outBoundNo) {
		String sql="update amazoninfo_unline_order set out_bound=:p1,order_status='Shipped',earliest_ship_date=now() where id=:p2 ";
		amazonUnlineOrdertDao.updateBySql(sql, new Parameter(outBoundNo,amazonUnlineOrder.getId()));
	}
	
	
	@Transactional(readOnly = false)
	public void updateCancelInfo(Integer id) {
		String sql="update amazoninfo_unline_order set cancel_date= now(),cancel_user=:p1 where id=:p2 ";
		amazonUnlineOrdertDao.updateBySql(sql, new Parameter(UserUtils.getUser().getId(),id));
		Integer warehouseId=19;
		StringBuffer buf= new StringBuffer();
		//查询未取消的new_to_offline转换
		List<PsiQualityChangeBill> qualityBills=this.qualityService.findNoCancelInfos(warehouseId, id);
		for(PsiQualityChangeBill qualityBill:qualityBills){
			buf.append("线下订单["+id+"]取消！");   
			if("0".equals(qualityBill.getChangeSta())){
				//如果是申请状态，直接调用取消
				buf.append("&nbsp;&nbsp;未确认转码[id:"+qualityBill.getId()+"]：sku["+qualityBill.getSku()+"]数量("+qualityBill.getQuantity()+")，已追回转换数量，");
				this.qualityService.cancel(qualityBill.getId(),null);
			}else{
				//如果已经确认的，追回来
				if(qualityBill.getItems()!=null&&qualityBill.getItems().size()>0){
                    //申请sku和确认sku不是相同的sku
					for(PsiQualityChangeBillItem item:qualityBill.getItems()){
						buf.append("&nbsp;&nbsp;已确认转码[id:"+qualityBill.getId()+"]：sku["+item.getSku()+"]数量("+item.getQuantity()+")，已追回转换数量，");
						this.cancelQualityType(item.getSku(), warehouseId, item.getQuantity());
					}
				}else{
					//相同sku没有子表
					buf.append("&nbsp;&nbsp;已确认转码[id:"+qualityBill.getId()+"]：sku["+qualityBill.getSku()+"]数量("+qualityBill.getQuantity()+")，已追回转换数量，");
					this.cancelQualityType(qualityBill.getSku(), warehouseId, qualityBill.getQuantity());
				}
			}
		}
		
		//发信提醒销售
		Map<String,String> userInfo =this.groupUserService.getResponsibleByCountry("de");
		if(userInfo.size()>0&&StringUtils.isNotBlank(buf.toString())){
			String name="";
			String email ="";
			StringBuffer buf1= new StringBuffer();
			StringBuffer buf2= new StringBuffer();
			for (Map.Entry<String,String> entry : userInfo.entrySet()) { 
			    String userName=entry.getKey();
				buf1.append(userName+",");
				buf2.append(entry.getValue()+",");
			}
			name=buf1.toString();
			email =buf2.toString();
			String content="Hi,"+name.substring(0, name.length()-1)+"<br/><br/>"+buf.toString()+"，请及时跟德国仓同事联系，【<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/psiQualityChangeBill/list'>点击此处</a>】查看详细!<br/><br/><br/>best regards<br/>Erp System";
			String ccEmail="george@inateck.com,amazon-sales@inateck.com,supply-chain@inateck.com";
			sendEmail(content, buf.toString(), email.substring(0, email.length()-1),ccEmail);
		}
	}
	
	@Transactional(readOnly = false)
	public void updateCancelStatu(Set<String> orderId) {
		String sql="update amazoninfo_unline_order set cancel_date= now() ,cancel_user=:p1,order_status='Canceled' where amazon_order_id in :p2 and order_channel='管理员' ";
		amazonUnlineOrdertDao.updateBySql(sql, new Parameter(new User("1"),orderId));
	}
	
	
	@Transactional(readOnly = false)
	public void save(AmazonUnlineOrder amazonUnlineOrder) {
		if(amazonUnlineOrder.getId()!=null&&amazonUnlineOrder.getId()>0){
			amazonUnlineOrdertDao.getSession().merge(amazonUnlineOrder); //用库里查出来的保存
		}else{
			amazonUnlineOrdertDao.save(amazonUnlineOrder);
		}
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonUnlineOrder> amazonUnlineOrders) {
		amazonUnlineOrdertDao.save(amazonUnlineOrders);
	}
	
	public Map<String,String> getUnUseUnlineOrder(){
		//String sqlString="SELECT a.id,CONCAT(DATE_FORMAT(a.`create_date`,'%Y-%m-%d'),'[',a.amazon_order_id,']') FROM amazoninfo_unline_order AS a WHERE a.id NOT IN(SELECT unline_order FROM psi_transport_order WHERE unline_order IS NOT NULL AND transport_sta!='8') ORDER BY a.`create_date` DESC";
		//取最近100个，不过滤是否绑定过
		String sqlString="SELECT a.id,CONCAT(DATE_FORMAT(a.`create_date`,'%Y-%m-%d'),'[',a.amazon_order_id,']') FROM amazoninfo_unline_order AS a  ORDER BY a.`create_date` DESC limit 100";
		List<Object[]> list=amazonUnlineOrdertDao.findBySql(sqlString);
		Map<String,String> map=Maps.newLinkedHashMap();
		for (Object[] obj : list) {
			map.put(obj[0].toString(),obj[1].toString());
		}
		return map;
	}
	
	
	public Map<String,String> getUnOuntBoundUnlineOrder(){
		String sqlString="select id,amazon_order_id from amazoninfo_unline_order where out_bound='0' ";
		List<Object[]> list=amazonUnlineOrdertDao.findBySql(sqlString);
		Map<String,String> map=new HashMap<String,String>();
		for (Object[] obj : list) {
			map.put(obj[0].toString(),obj[1].toString());
		}
		return map;
	}
	
	public AmazonUnlineAddress findByName(String name){
		String sql="SELECT NAME,address_line1,address_line2,address_line3,city,county,district,state_or_region,postal_code,country_code,phone FROM amazoninfo_unline_address where name like :p1 order by id asc ";
		List<Object[]> list=amazonUnlineOrdertDao.findBySql(sql,new Parameter("%"+name+"%"));
		if(list!=null&&list.size()>0){
			Object[] obj=list.get(0);
			AmazonUnlineAddress addr=new AmazonUnlineAddress();
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
			return addr;
		}
		return null;
	}
	
	 public void sendEmail(String content,String title,String sendEmail,String ccEmail){
			Date date = new Date();
			final MailInfo mailInfo1 = new MailInfo(sendEmail,title,date);
			mailInfo1.setContent(content);
			mailInfo1.setCcToAddress(ccEmail);
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo1);
				}
			}.start();
		}
	
	 @Transactional(readOnly = false)
	 public void cancelQualityTypeInfos(String sku,Integer warehouseId,Integer quantity){
		 this.cancelQualityType(sku, warehouseId, quantity);
	 }
	 
	 public void cancelQualityType(String sku,Integer warehouseId,Integer quantity){
			PsiInventory inventory = this.psiInventoryService.findBySku(sku,warehouseId);
			inventory.setNewQuantity(inventory.getNewQuantity()+quantity);
			inventory.setOfflineQuantity(inventory.getOfflineQuantity()-quantity);
			if(inventory.getNewQuantity()<0||inventory.getOfflineQuantity()<0){
				throw new RuntimeException("New_To_Offline确认库存数后有为负值；new:"+inventory.getNewQuantity()+"offline:"+inventory.getOfflineQuantity()+",请核查,操作已取消");
			}
			this.psiInventoryDao.save(inventory);
			//添加日志
			this.psiInventoryService.savelog("Offline_To_New", "offline", -quantity, "线下订单取消", inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,inventory.getOfflineQuantity());
			this.psiInventoryService.savelog("Offline_To_New", "new", quantity, "线下订单取消", inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,inventory.getNewQuantity());

	 }

	 
	 @Transactional(readOnly = false)
	 public void updateInventory(String sku,Integer warehouseId,Integer quantity){
		 	PsiInventory inventory = this.psiInventoryService.findBySku(sku, warehouseId);
			if((inventory.getNewQuantity()-quantity)<0){
				return ;
			}
			inventory.setNewQuantity(inventory.getNewQuantity()-quantity);
			inventory.setOfflineQuantity(inventory.getOfflineQuantity()+quantity);
			this.psiInventoryDao.save(inventory);
			//添加日志
			this.psiInventoryService.savelog("New_To_Offline", "new", -quantity, null, inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,inventory.getNewQuantity());
			this.psiInventoryService.savelog("New_To_Offline", "offline", quantity, null, inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,inventory.getOfflineQuantity());
	 }
	 
	 /**
	 * 统计所有产品的销量
	 * @return Map<String,Integer> [productName qty]
	 */
	public Map<String, Integer> getAllSalesVolume() {
		Map<String,Integer> rs = Maps.newLinkedHashMap();
		String sql="SELECT t.`product_name`,t.`color`,SUM(t.`quantity_shipped`) FROM `amazoninfo_unline_orderitem` t,`amazoninfo_unline_order` o "+
				" WHERE t.`order_id`=o.`id` AND o.`order_status`='Shipped' AND t.`quantity_shipped`>0 GROUP BY t.`product_name`,t.`color`";
		List<Object[]> list= amazonUnlineOrdertDao.findBySql(sql);
		for(Object[] obj:list){
			String productName = obj[0].toString();
			String color = obj[1] == null ? "" : obj[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			Integer quantity = obj[2]==null?0:Integer.parseInt(obj[2].toString());
			rs.put(productName, quantity);
	    }
		return rs;
	}
	
	public Map<String,Set<Integer>> findNoInvoiceOrder(){//and order_channel in ('管理员','CHECK24')
		String sql="SELECT (CASE WHEN sales_channel=19 THEN 'de' WHEN sales_channel=120 THEN 'com'  ELSE 'jp' END ) country,id FROM amazoninfo_unline_order "+ 
                   " WHERE purchase_date>='2017-12-16' AND `order_status`='Shipped' AND invoice_no IS NULL  AND sales_channel IN (19,120,147)";
		List<Object[]> list=amazonUnlineOrdertDao.findBySql(sql);
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
	
}
