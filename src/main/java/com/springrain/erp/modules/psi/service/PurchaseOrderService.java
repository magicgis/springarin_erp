/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.psi.dao.HisPurchaseOrderDao;
import com.springrain.erp.modules.psi.dao.PsiLadingBillDao;
import com.springrain.erp.modules.psi.dao.PurchaseOrderDao;
import com.springrain.erp.modules.psi.dao.PurchaseOrderDeliveryDateDao;
import com.springrain.erp.modules.psi.dao.PurchaseOrderItemDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsOrderDao;
import com.springrain.erp.modules.psi.entity.HisPurchaseOrder;
import com.springrain.erp.modules.psi.entity.PsiLadingBill;
import com.springrain.erp.modules.psi.entity.PsiLadingBillItem;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PurchaseOrder;
import com.springrain.erp.modules.psi.entity.PurchaseOrderDeliveryDate;
import com.springrain.erp.modules.psi.entity.PurchaseOrderItem;
import com.springrain.erp.modules.psi.scheduler.PoEmailManager;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 采购订单Service
 * @author Michael  
 * @version 2014-10-29
 */
@Component
@Transactional(readOnly = true)
public class PurchaseOrderService extends BaseService {
	
	@Autowired
	private PurchaseOrderDao    		purchaseOrderDao;
	@Autowired
	private PurchaseOrderItemDao  		purchaseOrderItemDao;
	@Autowired
	private PsiLadingBillDao    		psiLadingBillDao;
	@Autowired
	private HisPurchaseOrderDao 		hisPurchaseOrderDao;
	@Autowired
	private GenerateSequenceDao 		genSequenceDao;
	@Autowired
	private PsiProductService 			psiProductService;    
	@Autowired
	private MailManager					mailManager;  
	@Autowired
	private PsiPartsOrderDao 			partsOrderDao;
	@Autowired
	private PoEmailManager 				poMaillManager;
	@Autowired
	private PsiSupplierService 		    supplierService;
	@Autowired
	private PurchaseOrderDeliveryDateDao deliveryDateDao;
	@Autowired
	private  PsiProductEliminateService psiProductEliminateService;
	@Autowired
	private SystemService systemService;
	
	public PurchaseOrder get(Integer id) {
		return purchaseOrderDao.get(id);
	}
	
	
	public PurchaseOrder get(String orderNo) {
		DetachedCriteria dc = purchaseOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("orderNo", orderNo));
		List<PurchaseOrder> rs = purchaseOrderDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	
	
	/**
	 * 生成随机序列号
	 */
	@Transactional(readOnly = false)
	public String  createSequenceNumber(String seqCodeName){
		return this.genSequenceDao.genSequence(seqCodeName,2);
	}
	
	/**
	 *获取产品最近一次下单时间 
	 */
	public Map<String,String> getLastOrderDateByColor(){
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		Map<String,String> resMap = Maps.newHashMap();   
		//String sql="SELECT CASE WHEN b.`color_code` ='' THEN b.`product_name`  ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END AS productName,MAX(a.`create_date`) FROM psi_purchase_order AS a ,psi_purchase_order_item AS b WHERE a.`id`=b.`purchase_order_id` AND b.`del_flag`='0' AND  a.`order_sta` <>'6'  GROUP BY b.`product_name`,b.`color_code`";
		String sql = "SELECT productName,MAX(dates) FROM("+
				" SELECT CASE WHEN b.`color_code` ='' THEN b.`product_name`  ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END AS productName,MAX(a.`create_date`) AS dates FROM psi_purchase_order AS a ,psi_purchase_order_item AS b "+
				" WHERE a.`id`=b.`purchase_order_id` AND b.`del_flag`='0' AND  a.`order_sta` <>'6'  GROUP BY b.`product_name`,b.`color_code`"+
				" UNION ALL"+
				" SELECT CASE WHEN b.`color_code` ='' THEN b.`product_name`  ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END AS productName,MAX(a.`create_date`) AS dates FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b "+
				" WHERE a.`id`=b.`purchase_order_id` AND b.`del_flag`='0' AND  a.`order_sta` <>'6'  GROUP BY b.`product_name`,b.`color_code`) AS t GROUP BY productName";
		List<Object[]>  list=  this.purchaseOrderDao.findBySql(sql);
		for(Object[] obj:list){
			String productName = obj[0].toString();
			String week=DateUtils.getWeekStr((Date)obj[1], formatWeek, 5, "-");
			resMap.put(productName, week.substring(0,5)+"WK"+week.substring(5));
		}
		return resMap;
	}
	
	
	/**
	 *获取产品最近一次下单时间 
	 */
	public Map<Integer,String> getLastOrderDate(){
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		Map<Integer,String> resMap = Maps.newHashMap();   
		//String sql="SELECT b.`product_id`,MAX(a.`create_date`) FROM psi_purchase_order AS a ,psi_purchase_order_item AS b WHERE a.`id`=b.`purchase_order_id` AND b.`del_flag`='0' AND  a.`order_sta` <>'6'  GROUP BY b.`product_id`";
		String sql = "SELECT product_id,MAX(dates) FROM("+
				" SELECT b.`product_id`,MAX(a.`create_date`) AS dates FROM psi_purchase_order AS a ,psi_purchase_order_item AS b "+
				" WHERE a.`id`=b.`purchase_order_id` AND b.`del_flag`='0' AND  a.`order_sta` <>'6'  GROUP BY b.`product_id`"+
				" UNION ALL"+
				" SELECT b.`product_id`,MAX(a.`create_date`) AS dates FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b "+
				" WHERE a.`id`=b.`purchase_order_id` AND b.`del_flag`='0' AND  a.`order_sta` <>'6'  GROUP BY b.`product_id`) AS t GROUP BY product_id";
		List<Object[]>  list=  this.purchaseOrderDao.findBySql(sql);
		for(Object[] obj:list){
			Integer productId = Integer.parseInt(obj[0].toString());
			String week=DateUtils.getWeekStr((Date)obj[1], formatWeek, 5, "-");
			resMap.put(productId, week.substring(0,5)+"WK"+week.substring(5));
		}
		return resMap;
	}
	
	@Transactional(readOnly = false)
	public void  updatePayItemId(Integer itemId,Integer orderId){
		Parameter parameter =new Parameter(itemId,orderId);
		purchaseOrderDao.updateBySql("update psi_purchase_order set pay_item_id=:p1 where id =:p2) ", parameter);
	}
	
	@Transactional(readOnly = false)
	public void  updateDeliveryDate(Integer purchaseOrderId,Integer itemId,Integer deliveryDateId,Integer quantity,Integer quantityReceived,Integer quantityOff,Integer quantityOffReceived,Date deliveryDate,String delIds,String remark) throws ParseException{
		//如果deliveryDateId为空   说明是增加
		if(deliveryDateId==null){
			PurchaseOrderItem orderItem = this.purchaseOrderItemDao.get(itemId);
			//如果是分配收货方2010-01-01
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date logDate=sdf.parse("2010-01-01");
			PurchaseOrderDeliveryDate   deliveryPo = new PurchaseOrderDeliveryDate(orderItem.getProduct().getId(),orderItem.getProductName(),orderItem.getColorCode(),orderItem.getCountryCode(), deliveryDate, quantity, quantityReceived, orderItem,"0",purchaseOrderId,remark,quantityOff,quantityOffReceived,logDate);
			this.deliveryDateDao.save(deliveryPo);
		}else{
			//编辑原来的数
			PurchaseOrderDeliveryDate deliveryPo=this.deliveryDateDao.get(deliveryDateId);
			deliveryPo.setQuantity(quantity);					//更新预计收货数
			deliveryPo.setQuantityOff(quantityOff);             //更新线下预计收货
			if(deliveryDate.compareTo(deliveryPo.getDeliveryDate())!=0){
				deliveryPo.setDeliveryDateLog(deliveryPo.getDeliveryDate()); //保存原收货日期
			}
			deliveryPo.setDeliveryDate(deliveryDate);			//更新预计收货时间
			deliveryPo.setRemark(remark);                       //更新备注
			this.deliveryDateDao.save(deliveryPo);
		}
		if(StringUtils.isNotEmpty(delIds)){
			for(String delId:delIds.split(",")){
				PurchaseOrderDeliveryDate   deliveryPo=this.deliveryDateDao.get(Integer.parseInt(delId));
				deliveryPo.setDelFlag("1");		
				this.deliveryDateDao.save(deliveryPo);
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void  deleteDeliveryDateByOrderId(Integer orderId){
		purchaseOrderDao.updateBySql("UPDATE psi_purchase_order_delivery_date AS a SET a.del_flag='1'  WHERE a.`purchase_order_id`= :p1 ", new Parameter(orderId));
	}
	
	public void  deleteDeliveryDateByOrderItemId(Set<Integer> orderItemId){
		purchaseOrderDao.updateBySql("UPDATE psi_purchase_order_delivery_date AS a SET a.del_flag='1'  WHERE a.`purchase_order_item_id` IN :p1 ", new Parameter(orderItemId));
	}
	
	/***
	 * 查询该供应商的某产品的装箱数是否可以改
	 * 
	 */
	public boolean  canEditPackingQuantity(Integer supplierId,Integer productId){
		Parameter parameter =new Parameter(supplierId,productId);
		List<BigInteger> list=purchaseOrderDao.findBySql("SELECT COUNT(1) FROM psi_purchase_order_item AS ppoi WHERE ppoi.purchase_order_id IN (SELECT ppo.id FROM psi_purchase_order AS ppo WHERE ppo.order_sta IN ('2','3') AND ppo.supplier_id=:p1) AND ppoi.product_id=:p2 AND ppoi.del_flag='0'  ", parameter);
		return (!(list.get(0).intValue()>0));
	}
			
			
	/**
	 *查询是否收货完成 
	 */
	public int  isReceivedFinal(Integer itemId){
		Parameter parameter =new Parameter(itemId);
		List<BigDecimal> list=purchaseOrderDao.findBySql("SELECT SUM(quantity_ordered -quantity_received) as aa FROM psi_purchase_order_item poi WHERE poi.purchase_order_id =(SELECT ppoi.purchase_order_id FROM psi_purchase_order_item as ppoi WHERE ppoi.id=:p1 AND ppoi.del_flag='0' ) AND poi.del_flag='0'  ", parameter);
		return list.get(0).intValue();
	}
	
	/***
	 * 查询该供应商是否存在未付款订单
	 * 
	 */
	public boolean  hasUnDoneOrder(Integer supplierId){
		Parameter parameter =new Parameter(supplierId);
		List<BigInteger> list=purchaseOrderDao.findBySql("SELECT COUNT(1) FROM psi_purchase_order WHERE  order_sta <>'5' and pay_sta='1' and supplier_id=:p1  ", parameter);
		return list.get(0).intValue()>0;
	}
	
	/**
	 *查询是否付款完成 
	 */
	@Transactional(readOnly = true)
	public int  isPaymentFinal(Integer itemId){
		Parameter parameter =new Parameter(itemId);
		List<BigDecimal> list=purchaseOrderDao.findBySql("SELECT SUM(quantity_ordered -quantity_payment) as aa FROM psi_purchase_order_item poi WHERE poi.del_flag='0' AND  poi.purchase_order_id =(SELECT ppoi.purchase_order_id FROM psi_purchase_order_item as ppoi WHERE ppoi.id=:p1 AND ppoi.del_flag='0'  )  ", parameter);
		return list.get(0).intValue();
	}
	
	/**
	 *根据订单item算出付款比例
	 */
	@Transactional(readOnly = true)
	public int  getDeposit(Integer itemId){
		Parameter parameter =new Parameter(itemId);
		List<Integer> list=purchaseOrderDao.findBySql("SELECT deposit FROM psi_purchase_order  WHERE id=(SELECT ppoi.purchase_order_id FROM psi_purchase_order_item as ppoi WHERE ppoi.id=:p1 AND ppoi.del_flag='0' )  ", parameter);
		return list.get(0).intValue();
	}
	
	
	public Page<PurchaseOrder> findByProduct(Page<PurchaseOrder> page, PurchaseOrder purchaseOrder,String isCheck,String productIdColor) {
		DetachedCriteria dc = purchaseOrderDao.createDetachedCriteria();
		dc.createAlias("this.items", "item");
		if(StringUtils.isNotEmpty(purchaseOrder.getVersionNo())){
			dc.add(Restrictions.eq("item.countryCode",purchaseOrder.getVersionNo()));
		}
		
		if(StringUtils.isNotEmpty(productIdColor)){
			 String[]  arr=productIdColor.split(",");
		        Integer productId = Integer.parseInt(arr[0]);
		        String colorCode ="";
		        if(arr.length>1){
		        	colorCode = arr[1];
		        }
			dc.add(Restrictions.eq("item.product.id",productId));
			dc.add(Restrictions.eq("item.colorCode",colorCode));
		}
		
		if (purchaseOrder.getCreateDate()!=null){
			dc.add(Restrictions.or(Restrictions.ge("item.deliveryDate",purchaseOrder.getCreateDate()),Restrictions.ge("item.actualDeliveryDate",purchaseOrder.getCreateDate())));
		}
		if (purchaseOrder.getPurchaseDate()!=null){
			dc.add(Restrictions.or(Restrictions.le("item.deliveryDate",DateUtils.addDays(purchaseOrder.getPurchaseDate(),1)),Restrictions.le("item.deliveryDate",DateUtils.addDays(purchaseOrder.getPurchaseDate(),1))));
		}
		if("1".equals(isCheck)){
			dc.add(Restrictions.eq("merchandiser.id",UserUtils.getUser().getId()));
		}
		if(StringUtils.isNotEmpty(purchaseOrder.getOrderNo())){
			dc.add(Restrictions.like("orderNo", "%"+purchaseOrder.getOrderNo()+"%"));
		}
		if(StringUtils.isNotEmpty(purchaseOrder.getOrderSta())){
			dc.add(Restrictions.eq("orderSta", purchaseOrder.getOrderSta()));
		}else if("9".equals(purchaseOrder.getOrderSta())){
			dc.add(Restrictions.in("orderSta", new String[]{"1","2","3"}));
		}else{
			dc.add(Restrictions.ne("orderSta","6"));
		}
		
		if(StringUtils.isNotEmpty(purchaseOrder.getIsOverInventory())){
			dc.add(Restrictions.eq("isOverInventory", purchaseOrder.getIsOverInventory()));
		}
		
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", purchaseOrder.getSupplier()));
		}
		
		if(!StringUtils.isEmpty(purchaseOrder.getVersionNo())){
			dc.add(Restrictions.eq("item.countryCode",purchaseOrder.getVersionNo()));
		}
		
		page.setOrderBy("id desc");
		return purchaseOrderDao.find2(page, dc);
	}
	
	
	public Page<PurchaseOrder> findByProduct(Page<PurchaseOrder> page, Integer productId,String color,String countryCode, String orderNo) {
		DetachedCriteria dc = purchaseOrderDao.createDetachedCriteria();
		dc.createAlias("this.items", "item");
		dc.add(Restrictions.eq("item.product.id",productId));
		dc.add(Restrictions.eq("item.colorCode",color));
		if(StringUtils.isNotEmpty(countryCode)){
			dc.add(Restrictions.eq("item.countryCode",countryCode));
		}
		if(StringUtils.isNotEmpty(orderNo)){
			dc.add(Restrictions.like("orderNo","%"+orderNo+"%"));
		}
		dc.add(Restrictions.ne("orderSta","6"));
		page.setOrderBy("id desc");
		return purchaseOrderDao.find2(page, dc);
	}
	
	public List<Object[]> getProductAmountByByCountry(PurchaseOrder purchaseOrder){
		String sql="SELECT product_name, "+
		"SUM(CASE WHEN t.country_code='de' THEN quantity_ordered-quantity_received  ELSE 0 END  ) de,"+
		"SUM(CASE WHEN t.country_code='uk' THEN quantity_ordered-quantity_received  ELSE 0 END  ) uk,"+
		"SUM(CASE WHEN t.country_code='fr' THEN quantity_ordered-quantity_received  ELSE 0 END  ) fr,"+
		"SUM(CASE WHEN t.country_code='it' THEN quantity_ordered-quantity_received  ELSE 0 END  ) it,"+
		"SUM(CASE WHEN t.country_code='es' THEN quantity_ordered-quantity_received  ELSE 0 END  ) es,"+
		"SUM(CASE WHEN t.country_code='com' THEN quantity_ordered-quantity_received  ELSE 0 END  ) us,"+
		"SUM(CASE WHEN t.country_code='jp' THEN quantity_ordered-quantity_received  ELSE 0 END  ) jp,"+
		"SUM(CASE WHEN t.country_code='ca' THEN quantity_ordered-quantity_received  ELSE 0 END  ) ca,r.purchase_date,IFNULL(t.actual_delivery_date,t.delivery_date), "+
		"(CASE WHEN t.`actual_delivery_date` IS NOT NULL AND  t.`actual_delivery_date`>t.`delivery_date` THEN '逾期' "+
		"		WHEN NOW()>t.`delivery_date` AND t.quantity_received<t.`quantity_ordered` THEN '逾期' "+
		"		ELSE '正常' END)  statu "+
		" FROM psi_purchase_order r  JOIN psi_purchase_order_item t ON r.id=t.`purchase_order_id` where quantity_ordered!=quantity_received ";
		if (purchaseOrder.getCreateDate()!=null){
			sql+=" and t.delivery_date>= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getCreateDate())+"'";
		}
		if (purchaseOrder.getPurchaseDate()!=null){
			sql+=" and t.delivery_date<= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"'";
		}
		if(StringUtils.isNotEmpty(purchaseOrder.getOrderNo())){
			sql+=" and (r.order_no like '%"+purchaseOrder.getOrderNo()+"%' or t.product_name like '%"+purchaseOrder.getOrderNo()+"%')";
		}
		if(StringUtils.isNotEmpty(purchaseOrder.getOrderSta())){
			sql+=" and r.order_sta= '"+purchaseOrder.getOrderSta()+"'";
		}else{
			sql+=" and r.order_sta!='6' ";
		}
		
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			sql+=" and r.supplier_id= '"+purchaseOrder.getSupplier().getId()+"'";
		}
		
		if(!StringUtils.isEmpty(purchaseOrder.getVersionNo())){
			sql+=" and t.country_code= '"+purchaseOrder.getVersionNo()+"'";
		}
		
		sql+=" GROUP BY r.purchase_date,t.product_name,IFNULL(t.actual_delivery_date,t.delivery_date), "+
				"(CASE WHEN t.`actual_delivery_date` IS NOT NULL AND  t.`actual_delivery_date`>t.`delivery_date` THEN '逾期' "+
				"	WHEN NOW()>t.`delivery_date` AND t.quantity_received<t.`quantity_ordered` THEN '逾期' "+
				"	ELSE '正常' END)   ";
		return purchaseOrderDao.findBySql(sql);
	}
	
	
	
	
	public List<PurchaseOrder> exp(PurchaseOrder purchaseOrder) {
		DetachedCriteria dc = purchaseOrderDao.createDetachedCriteria();
		dc.createAlias("this.items", "item");
		if (purchaseOrder.getCreateDate()!=null){
			dc.add(Restrictions.ge("item.deliveryDate",purchaseOrder.getCreateDate()));
		}
		if (purchaseOrder.getPurchaseDate()!=null){
			dc.add(Restrictions.le("item.deliveryDate",DateUtils.addDays(purchaseOrder.getPurchaseDate(),1)));
		}
		if(StringUtils.isNotEmpty(purchaseOrder.getOrderNo())){
			dc.add(Restrictions.or(Restrictions.like("orderNo", "%"+purchaseOrder.getOrderNo()+"%"),Restrictions.like("item.productName", "%"+purchaseOrder.getOrderNo()+"%")));
		}
		if(StringUtils.isNotEmpty(purchaseOrder.getOrderSta())){
			dc.add(Restrictions.eq("orderSta", purchaseOrder.getOrderSta()));
		}else{
			dc.add(Restrictions.ne("orderSta","6"));
		}
		
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", purchaseOrder.getSupplier()));
		}
		
		//dc.add(Restrictions.sqlRestriction(" group by"));
		
		return purchaseOrderDao.exp(dc, Criteria.DISTINCT_ROOT_ENTITY);
	}
	
	
	
	public List<PurchaseOrderItem> findRate2(PurchaseOrder purchaseOrder){
		List<PurchaseOrderItem> list=Lists.newArrayList();
		String sql="SELECT o.order_no,s.nikename,CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.`color_code`) ELSE '' END) NAME, "+
			" GROUP_CONCAT(t.id),MIN(delivery_date),GROUP_CONCAT(DISTINCT remark),o.order_sta,o.id orderId,SUM(t.quantity_ordered),SUM(t.quantity_received)   "+
			" FROM  lc_psi_purchase_order o join lc_psi_purchase_order_item t ON t.purchase_order_id=o.id AND t.del_flag='0'   "+
			" JOIN psi_supplier s ON s.id=o.supplier_id   "+
			" WHERE o.order_sta!='6' AND o.order_sta!='0'   ";
		
		if (purchaseOrder.getCreateDate()!=null){
			sql+=" and t.delivery_date>='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getCreateDate())+"'";
		}
		if (purchaseOrder.getPurchaseDate()!=null){
			sql+=" and t.delivery_date<'"+new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(purchaseOrder.getPurchaseDate(),1))+"'";
		}
		if(StringUtils.isNotBlank(purchaseOrder.getModifyMemo())){
			if(purchaseOrder.getModifyMemo().contains("_")){
				String[] arr=purchaseOrder.getModifyMemo().split("_");
				sql+=" and t.product_name='"+arr[0]+"'";
				sql+=" and t.color_code='"+arr[1]+"'";
			}else{
				sql+=" and t.product_name='"+purchaseOrder.getModifyMemo()+"'";
			}
		}
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			sql+=" and o.supplier_id="+purchaseOrder.getSupplier().getId();
		}
		sql+=" GROUP BY o.order_no,s.nikename,CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.`color_code`) ELSE '' END),o.id,o.order_sta ";
		List<Object[]> objList=purchaseOrderDao.findBySql(sql);
		for (Object[] obj: objList) {
			PurchaseOrderItem tempItem=new PurchaseOrderItem();
			tempItem.setForecastRemark(obj[0].toString());//订单号
			tempItem.setColorCode(obj[1].toString());//供应商
			tempItem.setProductName(obj[2].toString());//产品
			tempItem.setItemIdStr(obj[3].toString());//itemid
			tempItem.setDeliveryDate((Date)obj[4]);
			tempItem.setRemark(obj[5]==null?"":obj[5].toString());
			tempItem.setOrderSta(obj[6].toString());
			tempItem.setForecastItemId(Integer.parseInt(obj[7].toString()));//orderid
			
			Integer quantityOrder=Integer.parseInt(obj[8]==null?"0":obj[8].toString());
			Integer quantityReceiver=Integer.parseInt(obj[9]==null?"0":obj[9].toString());
			if(quantityOrder.intValue()==quantityReceiver.intValue()){
				tempItem.setDelFlag("已收货");
			}else if(quantityReceiver.intValue()==0){
				tempItem.setDelFlag("未收货");
			}else if(quantityReceiver.intValue()>0&&quantityOrder.intValue()!=quantityReceiver.intValue()){
				tempItem.setDelFlag("部分收货");
			}
			
			
			list.add(tempItem);
		}
		return list;
	}
	
	
	public List<PurchaseOrderItem> findRate(PurchaseOrder purchaseOrder){
		List<PurchaseOrderItem> list=Lists.newArrayList();
		String sql="SELECT o.order_no,s.nikename,CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.`color_code`) ELSE '' END) NAME, "+
			" GROUP_CONCAT(t.id),MIN(delivery_date),GROUP_CONCAT(DISTINCT remark),o.order_sta,o.id orderId,SUM(t.quantity_ordered),SUM(t.quantity_received)   "+
			" FROM psi_purchase_order o JOIN psi_purchase_order_item t  ON t.purchase_order_id=o.id AND t.del_flag='0'   "+
			" JOIN psi_supplier s ON s.id=o.supplier_id   "+
			" WHERE o.order_sta!='6' AND o.order_sta!='0'   ";
		
		if (purchaseOrder.getCreateDate()!=null){
			sql+=" and t.delivery_date>='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getCreateDate())+"'";
		}
		if (purchaseOrder.getPurchaseDate()!=null){
			sql+=" and t.delivery_date<'"+new SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(purchaseOrder.getPurchaseDate(),1))+"'";
		}
		if(StringUtils.isNotBlank(purchaseOrder.getModifyMemo())){
			if(purchaseOrder.getModifyMemo().contains("_")){
				String[] arr=purchaseOrder.getModifyMemo().split("_");
				sql+=" and t.product_name='"+arr[0]+"'";
				sql+=" and t.color_code='"+arr[1]+"'";
			}else{
				sql+=" and t.product_name='"+purchaseOrder.getModifyMemo()+"'";
			}
		}
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			sql+=" and o.supplier_id="+purchaseOrder.getSupplier().getId();
		}
		sql+=" GROUP BY o.order_no,s.nikename,CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.`color_code`) ELSE '' END),o.id,o.order_sta ";
		List<Object[]> objList=purchaseOrderDao.findBySql(sql);
		for (Object[] obj: objList) {
			PurchaseOrderItem tempItem=new PurchaseOrderItem();
			tempItem.setForecastRemark(obj[0].toString());//订单号
			tempItem.setColorCode(obj[1].toString());//供应商
			tempItem.setProductName(obj[2].toString());//产品
			tempItem.setItemIdStr(obj[3].toString());//itemid
			tempItem.setDeliveryDate((Date)obj[4]);
			tempItem.setRemark(obj[5]==null?"":obj[5].toString());
			tempItem.setOrderSta(obj[6].toString());
			tempItem.setForecastItemId(Integer.parseInt(obj[7].toString()));//orderid
			
			Integer quantityOrder=Integer.parseInt(obj[8]==null?"0":obj[8].toString());
			Integer quantityReceiver=Integer.parseInt(obj[9]==null?"0":obj[9].toString());
			if(quantityOrder.intValue()==quantityReceiver.intValue()){
				tempItem.setDelFlag("已收货");
			}else if(quantityReceiver.intValue()==0){
				tempItem.setDelFlag("未收货");
			}else if(quantityReceiver.intValue()>0&&quantityOrder.intValue()!=quantityReceiver.intValue()){
				tempItem.setDelFlag("部分收货");
			}
			
			
			list.add(tempItem);
		}
		return list;
	}
	
	
	
	
	public List<PurchaseOrder> exp1(PurchaseOrder purchaseOrder) {
		DetachedCriteria dc = purchaseOrderDao.createDetachedCriteria();
		dc.createAlias("this.items", "item");
		if (purchaseOrder.getCreateDate()!=null){
			dc.add(Restrictions.ge("item.deliveryDate",purchaseOrder.getCreateDate()));
		}
		if (purchaseOrder.getPurchaseDate()!=null){
			dc.add(Restrictions.le("item.deliveryDate",DateUtils.addDays(purchaseOrder.getPurchaseDate(),1)));
		}
		if(StringUtils.isNotEmpty(purchaseOrder.getOrderNo())){
			dc.add(Restrictions.or(Restrictions.like("orderNo", "%"+purchaseOrder.getOrderNo()+"%"),Restrictions.like("item.productName", "%"+purchaseOrder.getOrderNo()+"%")));
		}
		if(StringUtils.isNotEmpty(purchaseOrder.getOrderSta())){
			dc.add(Restrictions.eq("orderSta", purchaseOrder.getOrderSta()));
		}else{
			dc.add(Restrictions.ne("orderSta","6"));
		}
		
		if(StringUtils.isNotBlank(purchaseOrder.getModifyMemo())){
			if(purchaseOrder.getModifyMemo().contains("_")){
				String[] arr=purchaseOrder.getModifyMemo().split("_");
				dc.add(Restrictions.eq("item.productName",arr[0]));
				dc.add(Restrictions.eq("item.colorCode",arr[1]));
			}else{
				dc.add(Restrictions.eq("item.productName", purchaseOrder.getModifyMemo()));
			}
		}
		
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", purchaseOrder.getSupplier()));
		}
		
		//dc.add(Restrictions.sqlRestriction(" group by"));
	//	return purchaseOrderDao.exp(dc, Criteria.DISTINCT_ROOT_ENTITY);
		return purchaseOrderDao.find(dc);
	}
	
	//查找未收完货的订单             过滤：  包含某产品
	public List<String> findUnReceivedDone(Integer productId,String country,String color){
		String sql ="SELECT DISTINCT a.`order_no` FROM psi_purchase_order  AS a ,psi_purchase_order_item AS b WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta`<>'6'  AND b.`quantity_ordered`<>b.`quantity_received` AND b.`product_id`=:p1 AND b.`country_code`=:p2 AND b.`color_code`=:p3";
		List<String> objects=purchaseOrderDao.findBySql(sql,new Parameter(new Object[]{productId,country,color}));
		return objects;
	}
	
	
	public Page<PurchaseOrder> find(Page<PurchaseOrder> page, PurchaseOrder purchaseOrder) {
		DetachedCriteria dc = purchaseOrderDao.createDetachedCriteria();
		dc.createAlias("this.items", "item");
		if (purchaseOrder.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",purchaseOrder.getCreateDate()));
		}
		if (purchaseOrder.getPurchaseDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(purchaseOrder.getPurchaseDate(),1)));
		}
		if(StringUtils.isNotEmpty(purchaseOrder.getOrderNo())){
			dc.add(Restrictions.or(Restrictions.like("orderNo", "%"+purchaseOrder.getOrderNo()+"%"),Restrictions.like("item.productName", "%"+purchaseOrder.getOrderNo()+"%")));
		}
		if(StringUtils.isNotEmpty(purchaseOrder.getOrderSta())){
			dc.add(Restrictions.eq("orderSta", purchaseOrder.getOrderSta()));
		}else{
			dc.add(Restrictions.not(Restrictions.in("orderSta",new Object[]{"6","0","1"})));
		}
		
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", purchaseOrder.getSupplier()));
		}
		page.setOrderBy("id desc");
		return purchaseOrderDao.find2(page,dc);
	}
	
	
	public Page<PurchaseOrder> findReconciliation(Page<PurchaseOrder> page, PurchaseOrder purchaseOrder) {
		DetachedCriteria dc = purchaseOrderDao.createDetachedCriteria();
		dc.add(Restrictions.in("orderSta",new Object[]{"2","3","4","5"}));
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", purchaseOrder.getSupplier()));
		}
		
		if (purchaseOrder.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",purchaseOrder.getCreateDate()));
		}
		if (purchaseOrder.getPurchaseDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(purchaseOrder.getPurchaseDate(),1)));
		}
		
		page.setOrderBy("supplier desc");
		return purchaseOrderDao.findReconciliation(page, dc, purchaseOrder);
	}
	
	public Page<Object[]>  findReconciliation2(Page<Object[]> page, PurchaseOrder purchaseOrder) {
		return purchaseOrderDao.findReconciliation2(page,purchaseOrder);
	}
	
	
	public List<PurchaseOrder> findPurchaseOrder(PurchaseOrder purchaseOrder) {
		String sqlString="select order_no,deposit_amount,order_total,d.deposit,d.currency_type from psi_purchase_order d  " +
				" where  d.del_flag='0' and d.supplier_id="+purchaseOrder.getSupplier().getId()+" and order_total*deposit/100<>deposit_amount ";
		List<Object[]> list=purchaseOrderDao.findBySql(sqlString);
		List<PurchaseOrder> orderList=new ArrayList<PurchaseOrder>();
		for (Object[] object : list) {
			PurchaseOrder order=new PurchaseOrder();
			order.setOrderNo(object[0].toString());
			order.setDepositAmount((BigDecimal) object[1]);
			order.setTotalAmount((BigDecimal) object[2]);
			order.setDeposit(Integer.parseInt(object[3].toString()));
			order.setCurrencyType(object[4].toString());
			orderList.add(order);
		}
		return orderList;

	}
	
	
	public List<Object[]> findAccountBalance() {
		String sqlString="SELECT a.`country`,a.`balance`,a.`remark`,a.`update_time`,  Round((CASE WHEN a.`country`='de'||a.`country`='fr'||a.`country`='it'||a.`country`='es' THEN a.`balance`*"
				+MathUtils.getRate("EUR", "USD", null)+" WHEN a.`country`='uk' THEN a.`balance`*"+MathUtils.getRate("GBP", "USD", null)+" WHEN a.`country`='ca' THEN a.`balance`*"
				+MathUtils.getRate("CAD", "USD", null)+" WHEN a.`country`='jp' THEN a.`balance`*"+
				MathUtils.getRate("JPY", "USD", null)+" WHEN a.`country`='mx' THEN a.`balance`*"+MathUtils.getRate("MXN", "USD", null)+" ELSE a.`balance` END ),2) FROM account_balance AS a ";
		return purchaseOrderDao.findBySql(sqlString);
	}
	
	public List<PurchaseOrderItem> findPurchaseOrderItem(PurchaseOrder purchaseOrder) {
		String sqlString2="";
		if(purchaseOrder.getSupplier().getId()!=1&&purchaseOrder.getSupplier().getId()!=12){
			sqlString2="select IFNULL(t.actual_delivery_date,t.`delivery_date`),quantity_ordered,quantity_received,item_price,d.order_no,d.currency_type,d.deposit,t.product_name " +
					" from psi_purchase_order_item t join psi_purchase_order d on d.id=t.purchase_order_id where order_sta in ('1','2','3') and d.supplier_id="+purchaseOrder.getSupplier().getId()+
					"  and t.del_flag='0' and d.del_flag='0'  and d.supplier_id="+purchaseOrder.getSupplier().getId();
					//and IFNULL(t.actual_delivery_date,t.`delivery_date`)<= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"' and IFNULL(t.actual_delivery_date,t.`delivery_date`)>='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getReceiveFinishedDate()) +"'"+
			 if(purchaseOrder.getPurchaseDate()!=null){
				 sqlString2+=" AND t.`delivery_date` <= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"'";
			 }
			 if(purchaseOrder.getReceiveFinishedDate()!=null){
				 sqlString2+=" and t.`delivery_date`>='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getReceiveFinishedDate())+"'";
			 }

		}else if(purchaseOrder.getSupplier().getId()==1){
			sqlString2="select IFNULL(t.actual_delivery_date,t.`delivery_date`),quantity_ordered,quantity_received,item_price,d.order_no,d.currency_type,d.deposit,t.product_name " +
					" from psi_purchase_order_item t join psi_purchase_order d on d.id=t.purchase_order_id where order_sta in ('1','2','3') and d.supplier_id=1 "+
					" and t.del_flag='0' and d.del_flag='0' and d.supplier_id=1";
			//and CONCAT(DATE_FORMAT(DATE_ADD(LAST_DAY(IFNULL(t.actual_delivery_date,t.`delivery_date`)), INTERVAL 2 MONTH), '%Y-%m'),'-10')<= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"' and CONCAT(DATE_FORMAT(DATE_ADD(LAST_DAY(IFNULL(t.actual_delivery_date,t.`delivery_date`)), INTERVAL 2 MONTH), '%Y-%m'),'-10')>='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getReceiveFinishedDate()) +"'"+
			 if(purchaseOrder.getPurchaseDate()!=null){
				 sqlString2+=" AND CONCAT(DATE_FORMAT(DATE_ADD(LAST_DAY(t.`delivery_date`), INTERVAL 2 MONTH), '%Y-%m'),'-10') <= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"'";
			 }
			 if(purchaseOrder.getReceiveFinishedDate()!=null){
				 sqlString2+=" and CONCAT(DATE_FORMAT(DATE_ADD(LAST_DAY(t.`delivery_date`), INTERVAL 2 MONTH), '%Y-%m'),'-10') >='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getReceiveFinishedDate())+"'";
			 }

		}else if(purchaseOrder.getSupplier().getId()==12){
			sqlString2="select IFNULL(t.actual_delivery_date,t.`delivery_date`),quantity_ordered,quantity_received,item_price,d.order_no,d.currency_type,d.deposit,t.product_name " +
					" from psi_purchase_order_item t join psi_purchase_order d on d.id=t.purchase_order_id where order_sta in ('1','2','3') and d.supplier_id=12 "+
					" and t.del_flag='0' and d.del_flag='0' and d.supplier_id=12 ";
			//and IFNULL(t.actual_delivery_date,t.`delivery_date`)<= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"' and IFNULL(t.actual_delivery_date,t.`delivery_date`)>='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getReceiveFinishedDate()) +"'"+
				//	"  AND LAST_DAY(IFNULL(t.actual_delivery_date,t.`delivery_date`))<='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"'  ";
			 if(purchaseOrder.getPurchaseDate()!=null){
				 sqlString2+=" AND  t.`delivery_date`<= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"' AND LAST_DAY(t.`delivery_date`) <= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"' ";
			 }
			 if(purchaseOrder.getReceiveFinishedDate()!=null){
				 sqlString2+=" and  t.`delivery_date`>='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getReceiveFinishedDate())+"'";
			 }

		}
			List<PurchaseOrderItem> items=new ArrayList<PurchaseOrderItem>();
			List<Object[]> list2=purchaseOrderDao.findBySql(sqlString2);
			for (Object[] object2 : list2) { 
				PurchaseOrderItem item=new PurchaseOrderItem();
				item.setDeliveryDate((Date) object2[0]);
				item.setQuantityOrdered(Integer.parseInt(object2[1].toString()));
				item.setQuantityReceived(Integer.parseInt(object2[2].toString()));
				if(object2[3]!=null){
					item.setItemPrice((BigDecimal) object2[3]);
				}else{
					item.setItemPrice(BigDecimal.ZERO);
				}
				item.setProductName(object2[7].toString());
				PurchaseOrder order2=new PurchaseOrder();
				order2.setOrderNo(object2[4].toString());
				order2.setCurrencyType(object2[5].toString());
				order2.setDeposit(Integer.parseInt(object2[6].toString()));
				item.setPurchaseOrder(order2);
				items.add(item);
			}
		return items;

	}
	
	public Page<PurchaseOrder> findLessCargoList(Page<PurchaseOrder> page, PurchaseOrder purchaseOrder) {
		DetachedCriteria dc = purchaseOrderDao.createDetachedCriteria();
		//订单状态为   生产或部分收货
		dc.add(Restrictions.in("orderSta",new String[]{"1","2","3"}));
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", purchaseOrder.getSupplier()));
		}   
		page.setOrderBy("supplier desc");
		return purchaseOrderDao.find(page,dc);
	}
	
	
	@Transactional(readOnly = false)
	public void save(PurchaseOrder purchaseOrder) {
		purchaseOrderDao.save(purchaseOrder);
	}
	
	
	@Transactional(readOnly = false)
	public boolean editSave(PurchaseOrder purchaseOrder) throws Exception {
		Map<Integer,Integer>  itemQuantityMap = Maps.newHashMap();
		PurchaseOrder  tempPurchaseOrder = this.purchaseOrderDao.get(purchaseOrder.getId());
		for(PurchaseOrderItem item:tempPurchaseOrder.getItems()){
			itemQuantityMap.put(item.getId(), item.getQuantityOrdered());
		}
		Set<Integer>  delDeliveryDateItemIds = Sets.newHashSet();
		
		
		Map<Integer,Map<Integer,BigDecimal>> ladingBillMap = Maps.newHashMap();
		BigDecimal  totalAmount = BigDecimal.ZERO;
		Set<Integer>  delItemSet = Sets.newHashSet();
		Set<String> setNewIds = new HashSet<String>();
		String oldItemIds=purchaseOrder.getOldItemIds();
		String [] oldIds = oldItemIds.split(",");
		//组成产品名+颜色   数量Map
//		Map<String,Integer> orderProColorMap = Maps.newHashMap();
		//说明是草稿状态     
		if("1".equals(purchaseOrder.getOrderSta())||"0".equals(purchaseOrder.getOrderSta())){
//			boolean flag=false;
//			for(PurchaseOrderItem item: purchaseOrder.getItems()){
//				if(item.getForecastItemId()!=null){
//					flag=true;
//					break;
//				}
//			}
			
//			if(flag){
//				//如果是预测生成出来的订单、并且数量有变动
//				PurchaseOrder  oldOrder = this.purchaseOrderDao.get(purchaseOrder.getId());
//				Map<String,Integer> oldMap = Maps.newHashMap();
//				for(PurchaseOrderItem item:oldOrder.getItems()){
//					String key =item.getProductNameColor()+","+item.getCountry();
//					oldMap.put(key, item.getQuantityOrdered());
//				}
//				boolean sendFlag=false;
//				Map<String,Integer> curMap = Maps.newHashMap();
//				for(PurchaseOrderItem item:purchaseOrder.getItems()){
//					String key =item.getProductNameColor()+","+item.getCountry();
//					curMap.put(key, item.getQuantityOrdered());
//					if(!oldMap.containsKey(key)||(!item.getQuantityOrdered().equals(oldMap.get(key)))){
//						sendFlag=true;
//					}
//				}
//				for(String key:oldMap.keySet()){
//					//老的里面有新的里面没有，，删除
//					if(!curMap.containsKey(key)){
//						sendFlag=true;
//					}
//				}
//				if(sendFlag){
//					this.sendEmail(curMap,oldMap,purchaseOrder.getOrderNo());
//				}
//			}
		
			for(PurchaseOrderItem item : purchaseOrder.getItems()){
				item.setPurchaseOrder(purchaseOrder);
				if(item.getItemPrice()!=null){
					totalAmount=totalAmount.add(item.getItemPrice().multiply(new BigDecimal(item.getQuantityOrdered())));
				}
				if(item.getId()!=null&&!"".equals(item.getId())){
					setNewIds.add(item.getId().toString());
				}else{
					//如果id为空     说明是新增的
					item.setQuantityPreReceived(0);      //预接收数量0
					item.setQuantityReceived(0);         //已接收数量0
					item.setQuantityOffPreReceived(0);      //预接收数量0
					item.setQuantityOffReceived(0);         //已接收数量0
					item.setQuantityPayment(0);          //已付款数量0
					item.setPaymentAmount(BigDecimal.ZERO);           //已支付金额
					item.setDelFlag("0");
				}
				
				//star 20151214
//				String proColor = item.getProductName();
//				if(StringUtils.isNotEmpty(item.getColorCode())){
//					proColor=proColor+"_"+item.getColorCode();
//				}
//				
//				Integer orderQuantity = item.getQuantityOrdered();
//				if(orderProColorMap.get(proColor)!=null){
//					orderQuantity+=orderProColorMap.get(proColor);
//				}
//				orderProColorMap.put(proColor, orderQuantity);
				//end 20151214
			}
			purchaseOrder.setTotalAmount(totalAmount);
			
			if(setNewIds!=null&&setNewIds.size()>0){
				for(int j=0;j<oldIds.length;j++){
					if(!setNewIds.contains(oldIds[j])){
						//不包含就干掉
						delItemSet.add(Integer.valueOf(oldIds[j]));
					};
				}
			}else{
				//说明原来的都删除了
				for(int j=0;j<oldIds.length;j++){
					delItemSet.add(Integer.valueOf(oldIds[j]));
				}
			}
			
			//star 20151214
//			String isOverFlag="0";
//			String remark="";
//			Map<String,Integer> maxMap = this.getMaxInventory();
//			Map<String,Integer> canSaleMap = this.getCanSaleMap();
//			for(String proColor:orderProColorMap.keySet()){
//				if(maxMap.get(proColor)!=null){
//					Integer canSaleQuantity=0;
//					if(canSaleMap.get(proColor)!=null){
//						canSaleQuantity=canSaleMap.get(proColor);
//					}
//					if(maxMap.get(proColor)-canSaleQuantity-orderProColorMap.get(proColor)<0){
//						isOverFlag="1";
//						remark+="产品:"+proColor+",最大允许库存数:"+maxMap.get(proColor)+",下单后库存数为:"+(canSaleQuantity+orderProColorMap.get(proColor)+";");
//					}
//				}
//			}
//			if("1".equals(isOverFlag)){
//				purchaseOrder.setIsOverInventory(isOverFlag);
//				purchaseOrder.setOverRemark(remark);
//			}else{
				purchaseOrder.setIsOverInventory("0");
//			}
			//end 20151214
			
			
			if("1".equals(purchaseOrder.getToReview())){
//				if("1".equals(purchaseOrder.getIsOverInventory())){
//					//查询角色为：采购超标审核员
//					String emailAddress="";  
//					for(Role role:UserUtils.getRoleList()){
//						if("采购审核员(超标)".equals(role.getName())){
//							for(User user:role.getUserList()){
//								emailAddress+=user.getEmail()+",";
//							}
//							break;    
//						}
//					}
//					
//					if(emailAddress.length()>0){
//						emailAddress=emailAddress.substring(0, emailAddress.length()-1);
//						String content = "(超标)采购订单编号：<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/purchaseOrder/review?id="+purchaseOrder.getId()+"'>"+purchaseOrder.getOrderNo()+"</a>已创建，请尽快登陆erp系统审批";
//						if(StringUtils.isNotBlank(content)){
//							Date date = new Date();
//							final MailInfo mailInfo = new MailInfo(emailAddress,"(超标)采购订单已创建待审批"+DateUtils.getDate("-yyyy/M/dd"),date);
//							mailInfo.setContent(content+",原因:"+purchaseOrder.getOverRemark());
//							//发送成功不成功都能保存
//							new Thread(){
//								@Override
//								public void run(){
//									poMaillManager.send(mailInfo);
//								}
//							}.start();
//						}
//					}
//				}else{
					//查询角色为：采购审核员
					StringBuilder emailAddress=new StringBuilder();
					List<User> users=systemService.findUserByPermission("psi:order:review");
					for(User user:users){
						emailAddress.append(user.getEmail()).append(",");
					}
					
					if(emailAddress.length()>0){
						emailAddress=new StringBuilder(emailAddress.substring(0, emailAddress.length()-1));
					}
					String content = "采购订单编号：<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/purchaseOrder/review?id="+purchaseOrder.getId()+"'>"+purchaseOrder.getOrderNo()+"</a>已创建，请尽快登陆erp系统审批";
					if(StringUtils.isNotBlank(content)){
						Date date = new Date();
						final MailInfo mailInfo = new MailInfo(emailAddress.toString(),"采购订单已创建待审批"+DateUtils.getDate("-yyyy/M/dd"),date);
						mailInfo.setContent(content);
						//发送成功不成功都能保存
						new Thread(){
							@Override
							public void run(){
								poMaillManager.send(mailInfo);
							}
						}.start();
						
					}
				}
//			}
		}else{
			//非草稿状态编辑
			//判断   预接收数     已接收数    (远程也要加)
			//判断相应的提单，已提单数量要小于订单数量（一人操作，后台不做判断），如果价格改动，查出相应的提单，改变单价和总价
			//编辑一次删除一次批量预收货
			//删除分批收货的
			for(PurchaseOrderItem item : purchaseOrder.getItems()){
				item.setPurchaseOrder(purchaseOrder);
				if(item.getId()!=null&&!"".equals(item.getId())){
					setNewIds.add(item.getId().toString());
					//判断价格是否变动,或者定金比例变动，都重新算提单总额
					//if(Float.floatToIntBits(item.getOldItemPrice())!=Float.floatToIntBits(item.getItemPrice())||purchaseOrder.getOldDeposit()!=purchaseOrder.getDeposit()){
					if(purchaseOrder.getOldDeposit()!=purchaseOrder.getDeposit()){	
						List<PsiLadingBillItem> listItem = this.purchaseOrderItemDao.get(item.getId()).getBillItemList();
						for(PsiLadingBillItem billItem:listItem){
							Map<Integer,BigDecimal> mapLaidngItem = Maps.newHashMap();
							if(ladingBillMap.get(billItem.getLadingBill().getId())!=null){
								mapLaidngItem =ladingBillMap.get(billItem.getLadingBill().getId());
							}
							mapLaidngItem.put(billItem.getId(), item.getItemPrice());
							ladingBillMap.put(billItem.getLadingBill().getId(), mapLaidngItem);
						}
					}
					//2015-08-13 
					item.setQuantityOrdered(item.getQuantityReceived()+item.getQuantityPreReceived()+item.getQuantityBalance());
					item.setQuantityOffOrdered(item.getQuantityOffReceived()+item.getQuantityOffPreReceived()+item.getQuantityOffBalance());
					//end
					//如果订单数变更，删除其分批交期
					if(itemQuantityMap.get(item.getId())!=null&&itemQuantityMap.get(item.getId()).intValue()!=item.getQuantityOrdered()){
						delDeliveryDateItemIds.add(item.getId());
					}
				}else{
					//如果id为空     说明是新增的
					item.setQuantityPreReceived(0);      //预接收数量0
					item.setQuantityReceived(0);         //已接收数量0
					item.setQuantityOffPreReceived(0);   //预接收数量0
					item.setQuantityOffReceived(0);      //已接收数量0
					item.setQuantityPayment(0);          //已付款数量0
					item.setPaymentAmount(BigDecimal.ZERO);           //已支付金额
					//2015-08-13 
					item.setQuantityOrdered(item.getQuantityBalance());
					item.setQuantityOffOrdered(item.getQuantityOffBalance());
					//end
				}
				//算总金额
				if(item.getItemPrice()!=null){
					totalAmount=totalAmount.add(item.getItemPrice().multiply(new BigDecimal(item.getQuantityOrdered())));
				}
			}
			
			if(setNewIds!=null&&setNewIds.size()>0){
				for(int j=0;j<oldIds.length;j++){
					if(!setNewIds.contains(oldIds[j])){
						delItemSet.add(Integer.valueOf(oldIds[j]));
						delDeliveryDateItemIds.add(Integer.valueOf(oldIds[j]));
					};
				}
			}else{
				//说明原来的都删除了
				for(int j=0;j<oldIds.length;j++){
					delItemSet.add(Integer.valueOf(oldIds[j]));
					delDeliveryDateItemIds.add(Integer.valueOf(oldIds[j]));
				}
			}
			
		}
		
		purchaseOrder.setTotalAmount(totalAmount);
		purchaseOrder.setUpdateDate(new Date());
		purchaseOrder.setUpdateUser(UserUtils.getUser());
		
		//处理要改变的提单
		if(ladingBillMap.size()>0){
			for(Integer key :ladingBillMap.keySet()){
				BigDecimal totalBillAmount = new BigDecimal("0");
				Map<Integer,BigDecimal> mapLadingItemNow = ladingBillMap.get(key);
				PsiLadingBill ladingBill = this.psiLadingBillDao.get(key);
				for(PsiLadingBillItem item:ladingBill.getItems()){
					Float  ratio =(100-purchaseOrder.getDeposit())/100f;
					//算出总金额   乘上支付比例
					BigDecimal  itemPrice = BigDecimal.ZERO;
					if(mapLadingItemNow.containsKey(item.getId())){
						itemPrice=mapLadingItemNow.get(item.getId());
						//item单价改变
						item.setItemPrice(itemPrice);
					}else{
						itemPrice=item.getItemPrice();
					}
					item.setItemPrice(itemPrice);
					totalBillAmount=totalBillAmount.add(new BigDecimal(item.getQuantityLading()).multiply(itemPrice).multiply(new BigDecimal(ratio+"")));
				}
				ladingBill.setTotalAmount(totalBillAmount);
				this.psiLadingBillDao.save(ladingBill);
			}
		}
		
		if(delItemSet.size()>0){
			for(PurchaseOrderItem item:this.getPurchaseOrderItems(delItemSet)){
				item.setDelFlag("1");
				item.setPurchaseOrder(purchaseOrder);
				purchaseOrder.getItems().add(item);
				//如果被删除了，就更新预测单数据，purchaseQuantity 
				if(item.getForecastItemId()!=null){
					updateFroecastPurchaseQuantity(item.getForecastItemId(), 0);
				}
			};
		}
		
		if(delDeliveryDateItemIds!=null&&delDeliveryDateItemIds.size()>0){
			this.deleteDeliveryDateByOrderItemId(delDeliveryDateItemIds);
		}
		
		//发信
//		Map<Integer,Date>  oldDeliveryDateMap=this.getOldDeliveryDate(purchaseOrder.getId());
//		this.sendEmailDeliveryReceived(purchaseOrder, oldDeliveryDateMap);
		
		purchaseOrderDao.getSession().merge(purchaseOrder);
		if(!"1".equals(purchaseOrder.getOrderSta())&&!"0".equals(purchaseOrder.getOrderSta())){
			//保存快照
			SimpleDateFormat  sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String versionNo = sdf.format(new Date());
			purchaseOrder.setVersionNo(versionNo);
			hisPurchaseOrderDao.save(new HisPurchaseOrder(purchaseOrder));
		}
		return true;
	}
	
	

	@Transactional(readOnly = false)
	public boolean deliveryDateSave(PurchaseOrder purchaseOrder) throws Exception {
		Map<Integer,Date> curDateMap = Maps.newHashMap();   //明细id:日期
//		Map<Integer,Date> oldDateMap = Maps.newHashMap();   //明细id:日期
		Map<Integer,String> remarkMap = Maps.newHashMap();
		for(PurchaseOrderItem item:purchaseOrder.getItems()){
			curDateMap.put(item.getId(), item.getActualDeliveryDate());
			remarkMap.put(item.getId(), item.getRemark());
		}
		purchaseOrder=this.purchaseOrderDao.get(purchaseOrder.getId());
		for(PurchaseOrderItem item:purchaseOrder.getItems()){
//			oldDateMap.put(item.getId(), item.getActualDeliveryDate());
			Date curDate = curDateMap.get(item.getId());
			//如果有改变就记录改前时间
			if(curDate.compareTo(item.getActualDeliveryDate())!=0){
				item.setDeliveryDateLog(item.getActualDeliveryDate());
			}
			item.setActualDeliveryDate(curDate);
			item.setRemark(remarkMap.get(item.getId()));
		}
		
		this.purchaseOrderDao.save(purchaseOrder);
		//发信给销售
//		this.sendEmailDeliveryReceived(purchaseOrder, oldDateMap);
		return true;
	}
	@Transactional(readOnly = false)
	public void updateReviewStaEmail(Integer orderId,String orderSta,String sendEmailSta){
		String sql="UPDATE psi_purchase_order AS a SET a.`order_sta`=:p2 , a.`send_email_flag`=:p3 WHERE a.`id`=:p1";
		this.purchaseOrderDao.updateBySql(sql, new Parameter(orderId,orderSta,sendEmailSta));
	}
	   
	@Transactional(readOnly = false)
	public void updateReviewStaEmailRemark(Integer orderId,String orderSta,String sendEmailSta,String overRemark){
		String sql="UPDATE psi_purchase_order AS a SET a.`order_sta`=:p2 , a.`send_email_flag`=:p3,a.`over_remark`=:p4 WHERE a.`id`=:p1";
		this.purchaseOrderDao.updateBySql(sql, new Parameter(orderId,orderSta,sendEmailSta,overRemark));
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		purchaseOrderDao.deleteById(id);
	}
	
	@Transactional(readOnly = false)
	public boolean sureSave(MultipartFile piFile,PurchaseOrder purchaseOrder,String filePath,RedirectAttributes redirectAttributes) throws IOException {
		BigDecimal totalAmount =new BigDecimal(0);
			if(piFile!=null&&piFile.getSize()!=0){
				if (filePath == null) {
					filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/purchaseOrders";
				}
				String suffix = piFile.getOriginalFilename().substring(piFile.getOriginalFilename().lastIndexOf("."));  
				String uuid = UUID.randomUUID().toString();
				File file1 = new File(filePath, purchaseOrder.getOrderNo());
				if (!file1.exists()) {
					file1.mkdirs();
				}
				File piFilePdf = new File(file1, "pi_"+uuid+suffix);
				FileUtils.copyInputStreamToFile(piFile.getInputStream(),piFilePdf);
				purchaseOrder.setPiFilePath(Global.getCkBaseDir() + "/psi/purchaseOrders/"+purchaseOrder.getOrderNo()+"/pi_"+uuid+suffix);
			}
			
			//改为生产状态
			purchaseOrder.setOrderSta("2");
			purchaseOrder.setSureDate(new Date());
			purchaseOrder.setSureUser(UserUtils.getUser());
			
			//保存快照
			SimpleDateFormat  sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String versionNo = sdf.format(new Date());
			purchaseOrder.setVersionNo(versionNo);
			for(PurchaseOrderItem item : purchaseOrder.getItems()){
				item.setPurchaseOrder(purchaseOrder);  
				totalAmount=totalAmount.add(item.getItemPrice().multiply(new BigDecimal(item.getQuantityOrdered())));
				if(item.getForecastItemId()!=null){
					this.updateFroecastPurchaseQuantity(item.getForecastItemId(), item.getQuantityOrdered());
				}
				
			}
			purchaseOrder.setTotalAmount(totalAmount);
			purchaseOrderDao.save(purchaseOrder);
			hisPurchaseOrderDao.save(new HisPurchaseOrder(purchaseOrder));
			try {
				sendPiToSupplier(purchaseOrder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		return true;
	}
	
	
	/**
	 * 根据状态查出订单
	 * 
	 */
	public Map<Integer,String> getOrdersByStas(Map<Integer,String> refMap){
		Map<Integer,String> orderMap=Maps.newHashMap();
		String sql ="SELECT a.`id`,a.`order_no`,a.`currency_type` FROM psi_purchase_order AS a WHERE a.`order_sta` IN ('2','3','4')";
		List<Object[]> objects=this.purchaseOrderDao.findBySql(sql);
		for(Object[] object:objects){
			orderMap.put(Integer.parseInt(object[0].toString()), object[1].toString());
			refMap.put(Integer.parseInt(object[0].toString()), object[2].toString());
		}
		return orderMap;
	}
	
	/**
	 * 查询目前在产的订单,即：“生产状态”和“部分收货状态”  所有sku  数量
	 */
	
	public Map<String,Integer> getProductInMarking(){
		Map<String,Integer> skuMap = Maps.newHashMap();
		DetachedCriteria dc = purchaseOrderDao.createDetachedCriteria();
		dc.add(Restrictions.in("orderSta",new Object[]{"1","2","3"}));
		//查询所有sku及对应的产品信息
		List<PsiSku> skuList=this.psiProductService.getSkus(null);
		Map<String,String> productMap = Maps.newHashMap();
		
		for(PsiSku psiSku :skuList){
			String key = psiSku.getProductId()+","+psiSku.getCountry()+","+psiSku.getColor();
			if(!productMap.containsKey(key)){
				productMap.put(key, psiSku.getSku());
			}
		}
		
		List<PurchaseOrder> purchaseOrders= purchaseOrderDao.find(dc);
		for(PurchaseOrder order:purchaseOrders){
			for(PurchaseOrderItem item:order.getItems()){
				Integer  quantity=item.getQuantityUnReceived();
				String productKey = item.getProduct().getId()+","+item.getCountryCode()+","+item.getColorCode();
				String sku = productMap.get(productKey);
				if(sku!=null){
					if(skuMap.containsKey(sku)){
						quantity+=skuMap.get(sku);
					}
					skuMap.put(sku,quantity);
				}
			}
		}
		return skuMap;
	}
	
	/**
	 * 单个sku的在产 数量
	 */
	
	public Integer getProductInMarkingQuantity(String sku){
		DetachedCriteria dc = purchaseOrderDao.createDetachedCriteria();
		dc.add(Restrictions.in("orderSta",new Object[]{"1","2","3"}));
		PsiSku psiSku =this.psiProductService.getSkuBySku(sku,"1");
		if(psiSku!=null){
			Integer productId=psiSku.getProductId();
			String country =psiSku.getCountry();
			String color =psiSku.getColor();
			dc.add(Restrictions.eq("item.product.id", productId));
			dc.add(Restrictions.eq("countryCode", country));
			dc.add(Restrictions.eq("colorCode", color));
			List<PurchaseOrder> purchaseOrders= purchaseOrderDao.find(dc);
			Integer  quantity=0;
			for(PurchaseOrder order:purchaseOrders){
				for(PurchaseOrderItem item:order.getItems()){
					if(productId.equals(item.getProduct().getId())&&country.equals(item.getCountryCode())&&color.equals(item.getColorCode())){
						quantity += item.getQuantityUnReceived();
					}
				}
			}
			return quantity;
		}else{
			return null;
		}
	}
	

	
	/***
	 *查询sku匹配的条码 
	 * 
	 */
	
	public Map<String,String> getSkuAndFnsku(){
		Map<String,String> skuFnskuMap=Maps.newHashMap();
		String sql ="SELECT c.sku,p.`barcode` FROM psi_sku AS c , psi_barcode AS p WHERE p.`psi_product`=c.`product_id`AND p.`product_platform`=c.`country` AND p.`product_color`=c.`color` AND c.`del_flag`='0' AND c.`use_barcode`='1' AND p.`barcode` IS NOT NULL  AND c.`product_name`  NOT LIKE '%other%' AND c.`product_name`  NOT LIKE '%Old%'  ";
		List<Object[]> objects=this.purchaseOrderDao.findBySql(sql);
		for(Object[] object:objects){
			skuFnskuMap.put(object[1].toString(), object[0].toString());
		}
		return skuFnskuMap;
	}
	
	
	
//	public boolean sendEmail(Map<String,Integer> curMap,Map<String,Integer> oldMap,String orderNo){
//		StringBuffer contents= new StringBuffer("");
//		contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;width:100%' cellpadding='0' cellspacing='0' >");
//		contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
//		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:20%'>产品名</th>");
//		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:5%'>国家</th>");
//		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:15%'>改前数量</th>");
//		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:15%'>改后数量</th>");
//		contents.append("</tr>");
////		for(String key:curMap.keySet()){
//		for(Map.Entry<String, Integer> entry:curMap.entrySet()){
//			String key = entry.getKey(); 
//			Integer quantity=entry.getValue();
//			String arr[]=key.split(",");
//			contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
//			contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+arr[0]+"</td>");
//			contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(("com".equals(arr[1])?"us":arr[1]).toUpperCase())+"</td>");
//			contents.append("<td style='border-left:1px solid;border-top:1px solid;color:"+(quantity.equals(oldMap.get(key))?"#666;":"red")+";'>"+(oldMap.get(key)!=null?(oldMap.get(key)+""):("新增"))+"</td>");
//			contents.append("<td style='border-left:1px solid;border-top:1px solid;color:"+(quantity.equals(oldMap.get(key))?"#666;":"red")+"'>"+quantity+"</td></tr>");
//		}
//		
////		for(String key:oldMap.keySet()){
//		for(Map.Entry<String, Integer> entry:oldMap.entrySet()){
//			String key = entry.getKey(); 
//			Integer quantity=entry.getValue();
//			String arr[]=key.split(",");
//			if(!curMap.containsKey(key)){
//				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
//				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+arr[0]+"</td>");
//				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(("com".equals(arr[1])?"us":arr[1]).toUpperCase())+"</td>");
//				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:"+(quantity.equals(curMap.get(key))?"#666;":"red")+";'>"+quantity+"</td>");
//				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:red;'>"+"删除"+"</td></tr>");
//			}
//		}
//		contents.append("</table><br/>");
//		String toAddress ="amazon-sales@inateck.com";
//		Date date = new Date();   
//		final MailInfo mailInfo = new MailInfo(toAddress,"采购订单信息有变动["+orderNo+"]"+DateUtils.getDate("-yyyy/M/dd"),date);
//		mailInfo.setContent(contents.toString());
//		new Thread(){
//			public void run(){
//				 mailManager.send(mailInfo);
//			}
//		}.start();
//		
//		return true;
//	}
	
	/**
	 *获得po里产品的剩余数量 
	 * map  key：productName+country
	 */
	public Map<String,Integer> getPoBalance(){
		Map<String,Integer> map=Maps.newHashMap();
		String sql="SELECT b.`product_name`,b.`country_code` ,b.`color_code`,SUM(b.`quantity_ordered`-b.`quantity_received`) FROM psi_purchase_order AS a ,psi_purchase_order_item AS b WHERE a.`id`=b.`purchase_order_id`  AND b.del_flag='0'  AND a.`order_sta`IN ('1','2','3') GROUP BY b.`product_name`,b.`country_code`,b.`color_code`";
		List<Object[]> objects=this.purchaseOrderDao.findBySql(sql);
		for(Object[] object:objects){
			String key=object[0].toString()+","+object[1].toString()+","+object[2].toString();
			map.put(key, object[3]==null?0:Integer.parseInt(object[3].toString()));
		}
		return map;
	}
	
	
	/**
	 *获得在途产品数量
	 * map  key：productName+country
	 */
	public Map<String,Integer> getTranQuantity(){
		String stockSql="SELECT a.`id` FROM psi_stock AS a WHERE a.`type`='0' AND a.`del_flag`='0'";
		List<Integer> stockIds=this.purchaseOrderDao.findBySql(stockSql);
		Map<String,Integer> map=Maps.newHashMap();
		String sql="SELECT b.`product_name`,b.`country_code`,b.`color_code`,SUM(b.`shipped_quantity`-CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END ) AS a FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.del_flag='0' AND a.`transport_sta` IN ('1','2','3','4')  AND a.`to_store` IN :p1 AND a.`from_store` IN :p1 GROUP BY b.`product_name`,b.`country_code`,b.`color_code`";
		List<Object[]> objects=this.purchaseOrderDao.findBySql(sql,new Parameter(stockIds));
		for(Object[] object:objects){
			String key=object[0].toString()+","+object[1].toString()+","+object[2].toString();
			map.put(key, object[3]==null?0:Integer.parseInt(object[3].toString()));
		}
		return map;
	}
	
	/**
	 *获得本地仓产品数量
	 * map  key：productName+country
	 */
	public Map<String,Map<String,Integer>> getLocalStock(){
		Map<String,Map<String,Integer>> map=Maps.newHashMap();
		String sql="SELECT a.`product_name`,a.`country_code`,a.`color_code`,SUM(a.`new_quantity`),a.`warehouse_id` FROM psi_inventory AS a GROUP BY a.`warehouse_id`,a.`product_name`,a.`country_code`,a.`color_code`";
		List<Object[]> objects=this.purchaseOrderDao.findBySql(sql);
		for(Object[] object:objects){
			String key=object[0].toString()+","+object[1].toString()+","+object[2].toString();
			Map<String,Integer> innerMap =Maps.newHashMap();
			if(map.get(object[4].toString())!=null){
				innerMap=map.get(object[4].toString());
			}
			innerMap.put(key, object[3]==null?0:Integer.parseInt(object[3].toString()));
			map.put(object[4].toString(), innerMap);
		}
		return map;
	}
	
	/**
	 *获得po里产品的剩余数量 
	 * map  key：productName+country
	 */
	public Map<String,Integer> getFbaStock(){
		Map<String,Integer> map=Maps.newHashMap();
		String sql="SELECT b.`product_name`,b.`country`,b.`color`, CASE WHEN a.orrect_quantity IS NULL  THEN (a.`fulfillable_quantity`+a.`transit_quantity`) ELSE (a.orrect_quantity+a.fulfillable_quantity)  END AS quantity FROM psi_inventory_fba AS a,psi_sku AS b WHERE a.`sku`=b.`sku` AND b.`use_barcode`='1' AND a.`data_date`= DATE_FORMAT(SYSDATE(), '%Y-%m-%d') GROUP BY b.`product_name`,b.`country`,b.`color`";
		List<Object[]> objects=this.purchaseOrderDao.findBySql(sql);
		for(Object[] object:objects){
			String key=object[0].toString()+","+object[1].toString()+","+object[2].toString();
			map.put(key, object[3]==null?0:Integer.parseInt(object[3].toString()));
		}
		return map;
	}
	
	/**
	 *获取barcode里的产品信息    
	 * 
	 * 
	 */
	public List<Object[]> getProductInfos(){
		String sql="SELECT  a.`product_name`,a.`product_platform`,a.`product_color` FROM psi_barcode  AS a WHERE a.`del_flag`='0' GROUP BY a.`product_name`,a.`product_platform`,a.`product_color` ORDER BY a.`product_name`,a.`product_color`,field(a.`product_platform`,'de','fr','it','es','uk','com','ca','jp'); ";
		List<Object[]> objects=this.purchaseOrderDao.findBySql(sql);
		return objects;
	}
	
	public Map<Integer,Double> getUnReceiving(PurchaseOrder purchaseOrder){
		String sql="SELECT SUM((b.`quantity_ordered` - b.`quantity_received`) * (CASE WHEN a.currency_type = 'CNY' THEN b.`item_price`/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+" ELSE b.`item_price` END)) * ((100- a.`deposit`) / 100) payment, " +
				" a.supplier_id supplier_id FROM psi_purchase_order AS a,psi_purchase_order_item AS b,psi_supplier AS c " +
				" WHERE a.`id` = b.`purchase_order_id` AND a.`supplier_id` = c.`id` AND a.`order_sta` IN ('1','2', '3') and a.`supplier_id` <>1 and a.`supplier_id` <>12 and a.del_flag='0' and b.del_flag='0'  " ;
				 if(purchaseOrder.getPurchaseDate()!=null){
					 sql+=" AND b.`delivery_date` <= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"'";
				 }
				 if(purchaseOrder.getReceiveFinishedDate()!=null){
					 sql+=" and b.`delivery_date`>='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getReceiveFinishedDate())+"'";
				 }
				sql+=" GROUP BY a.supplier_id "+
				" union SELECT SUM((b.`quantity_ordered` - b.`quantity_received`) * (CASE WHEN a.currency_type = 'CNY' THEN b.`item_price`/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+" ELSE b.`item_price` END)) * ((100- a.`deposit`) / 100) payment, " +
				" a.supplier_id supplier_id FROM psi_purchase_order AS a,psi_purchase_order_item AS b,psi_supplier AS c " +
				" WHERE a.`id` = b.`purchase_order_id` AND a.`supplier_id` = c.`id` AND a.`order_sta` IN ('1','2', '3') and a.`supplier_id`=1 and a.del_flag='0' and b.del_flag='0' " ;
				 if(purchaseOrder.getPurchaseDate()!=null){
					 sql+=" AND CONCAT(DATE_FORMAT(DATE_ADD(LAST_DAY(b.`delivery_date`), INTERVAL 2 MONTH), '%Y-%m'),'-10')<= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"'";
				 }
				 if(purchaseOrder.getReceiveFinishedDate()!=null){
					 sql+=" and CONCAT(DATE_FORMAT(DATE_ADD(LAST_DAY(b.`delivery_date`), INTERVAL 2 MONTH), '%Y-%m'),'-10') >='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getReceiveFinishedDate())+"' ";
				 }
				sql+=" union SELECT SUM((b.`quantity_ordered` - b.`quantity_received`) * (CASE WHEN a.currency_type = 'CNY' THEN b.`item_price`/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+" ELSE b.`item_price` END)) * ((100- a.`deposit`) / 100) payment, " +
				" a.supplier_id supplier_id FROM psi_purchase_order AS a,psi_purchase_order_item AS b,psi_supplier AS c " +
				" WHERE a.`id` = b.`purchase_order_id` AND a.`supplier_id` = c.`id` AND a.`order_sta` IN ('1','2', '3') and a.`supplier_id` =12 " +
				"  and a.del_flag='0' and b.del_flag='0' ";
				 if(purchaseOrder.getPurchaseDate()!=null){
					 sql+=" AND LAST_DAY(b.`delivery_date`)<='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"' and b.`delivery_date` <= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"' ";
				 }
				 if(purchaseOrder.getReceiveFinishedDate()!=null){
					 sql+="  and  b.`delivery_date` >='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getReceiveFinishedDate())+"' ";
				 }
		
		List<Object[]> list=this.purchaseOrderDao.findBySql(sql);
		Map<Integer,Double> map=new HashMap<Integer,Double>();
		for (Object[] object : list) {
			if(object[1]!=null&&object[0]!=null){
				map.put(Integer.parseInt(object[1].toString()), ((BigDecimal) object[0]).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			
		}
		return map;
	}
	
	public Map<Integer,Double> getUnPayFinal(PurchaseOrder purchaseOrder){
		String sql="SELECT SUM(CASE WHEN b.currency_type = 'CNY' THEN (b.total_amount - b.total_payment_amount)/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+" else (b.total_amount - b.total_payment_amount) end) unPayAmount,b.`supplier_id` " +
				" FROM psi_lading_bill b " +
				" WHERE b.bill_sta = '1'  AND b.total_amount > b.total_payment_amount and b.`supplier_id` <>1 and b.`supplier_id` <>12 and b.del_flag='0' " ;
		 if(purchaseOrder.getPurchaseDate()!=null){
			 sql+=" AND b.`sure_date` <= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"'";
		 }
		 if(purchaseOrder.getReceiveFinishedDate()!=null){
			 sql+="  and  b.`sure_date` >='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getReceiveFinishedDate())+"'  ";
		 }	
		sql+=" GROUP BY b.`supplier_id` "+
				" union SELECT SUM(CASE WHEN b.currency_type = 'CNY' THEN (b.total_amount - b.total_payment_amount)/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+" else (b.total_amount - b.total_payment_amount) end) unPayAmount,b.`supplier_id` " +
				" FROM psi_lading_bill b  " +
				" WHERE b.bill_sta = '1' AND b.total_amount > b.total_payment_amount and b.`supplier_id` =1 and b.del_flag='0' ";
		 if(purchaseOrder.getPurchaseDate()!=null){
			 sql+=" AND CONCAT(DATE_FORMAT(DATE_ADD(LAST_DAY(b.sure_date), INTERVAL 2 MONTH), '%Y-%m'),'-10') <= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"'";
		 }
		 if(purchaseOrder.getReceiveFinishedDate()!=null){
			 sql+=" and  CONCAT(DATE_FORMAT(DATE_ADD(LAST_DAY(b.sure_date), INTERVAL 2 MONTH), '%Y-%m'),'-10') >='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getReceiveFinishedDate())+"'  ";
		 }	
		sql+=	" union SELECT SUM(CASE WHEN b.currency_type = 'CNY' THEN (b.total_amount - b.total_payment_amount)/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+" else (b.total_amount - b.total_payment_amount) end) unPayAmount,b.`supplier_id` " +
				" FROM psi_lading_bill b  " +
				" WHERE b.bill_sta = '1' AND b.total_amount > b.total_payment_amount and b.`supplier_id` =12 "+
				" and b.del_flag='0'  ";
		if(purchaseOrder.getPurchaseDate()!=null){
			 sql+=" AND b.`sure_date` <= '"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"'  AND LAST_DAY(b.sure_date)<='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getPurchaseDate())+"'  ";
		}
		if(purchaseOrder.getReceiveFinishedDate()!=null){
			sql+=" and b.`sure_date` >='"+new SimpleDateFormat("yyyy-MM-dd").format(purchaseOrder.getReceiveFinishedDate())+"'  ";
		}			
		List<Object[]> list=this.purchaseOrderDao.findBySql(sql);
		Map<Integer,Double> map=new HashMap<Integer,Double>();
		for (Object[] object : list) {
			if(object[1]!=null&&object[0]!=null){
				map.put(Integer.parseInt(object[1].toString()), ((BigDecimal) object[0]).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			
		}
		return map;
	}
	
	public Map<Integer,Double> getUnPayDeposit(PurchaseOrder purchaseOrder){
		String sql="SELECT  SUM(CASE WHEN b.currency_type = 'CNY' THEN (b.order_total * b.deposit / 100- b.deposit_amount) /"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+" " +
				" ELSE (b.order_total * b.deposit / 100- b.deposit_amount) END) AS depositAmount, b.`supplier_id`" +
				" FROM psi_purchase_order b where  b.del_flag='0' GROUP BY b.`supplier_id` "+
				" having SUM(CASE WHEN b.currency_type = 'CNY' THEN (b.order_total * b.deposit / 100- b.deposit_amount) /"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+" ELSE (b.order_total * b.deposit / 100- b.deposit_amount) END)<>0 ";
		List<Object[]> list=this.purchaseOrderDao.findBySql(sql);
		Map<Integer,Double> map=new HashMap<Integer,Double>();
		for (Object[] object : list) {
			if(object[1]!=null&&object[0]!=null){
				map.put(Integer.parseInt(object[1].toString()), ((BigDecimal) object[0]).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
			}
		
		}
		return map;
	}
	
	/**
	 *根据itemId获取list信息 
	 */
	public List<PurchaseOrderItem> getPurchaseOrderItems(Set<Integer> ids){
		DetachedCriteria dc = this.purchaseOrderItemDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		return purchaseOrderItemDao.find(dc);
	}
	
	public List<PurchaseOrder> findHasPartsOfPurchaseOrder(String[] orderStas,boolean isParts) {
		DetachedCriteria dc = purchaseOrderDao.createDetachedCriteria();
		dc.add(Restrictions.in("orderSta",orderStas));
		if(isParts){
			dc.add(Restrictions.eq("toPartsOrder","1"));	
		}
		return purchaseOrderDao.find(dc);
	}
	

		private void sendPiToSupplier(PurchaseOrder purchaseOrder) throws Exception{
			SendEmail  sendEmail = new SendEmail();
			//往采购供应商发信 获取供应商模板
			PsiSupplier supplier = this.supplierService.get(purchaseOrder.getSupplier().getId());
			String orderNo=purchaseOrder.getOrderNo();
			Map<String,Object> params = Maps.newHashMap();
			String template = "";
			try {
				params.put("supplier", supplier);
				template = PdfUtil.getPsiTemplate("piSureEmail.ftl", params);
			} catch (Exception e) {}
			sendEmail.setSendContent(template);
			sendEmail.setSendEmail(supplier.getMail()); 
			sendEmail.setSendSubject("采购订单："+orderNo+"的PI已确认"+"("+DateUtils.getDate()+")");
			sendEmail.setBccToEmail(UserUtils.getUser().getEmail());
			//发送邮件
			final MailInfo mailInfo = sendEmail.getMailInfo();
			String baseDir = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/");
			File pdfFile = new File(baseDir,purchaseOrder.getPiFilePath());
			mailInfo.setFileName(pdfFile.getName());
			mailInfo.setFilePath(pdfFile.getAbsolutePath());
			new Thread(){
				@Override
				public void run() {
					poMaillManager.send(mailInfo);
				}
			}.start();
		}
		
		//运单试算
		public List<PurchaseOrder> findWayBillList(PsiLadingBill ladingBill) {
			DetachedCriteria dc = purchaseOrderDao.createDetachedCriteria();
			dc.createAlias("this.items", "item");
			
			dc.add(Restrictions.not(Restrictions.in("orderSta",new String []{"4","5","6"})));//收货完成和已付款已取消的不要
			if(ladingBill.getSupplier()!=null&&ladingBill.getSupplier().getId()!=null){
				dc.add(Restrictions.eq("supplier", ladingBill.getSupplier()));
			}
			
			if(ladingBill.getCreateDate()!=null){
				dc.add(Restrictions.or(Restrictions.ge("item.deliveryDate",ladingBill.getCreateDate()),Restrictions.ge("item.deliveryDate",ladingBill.getCreateDate())));
				dc.add(Restrictions.or(Restrictions.le("item.actualDeliveryDate",DateUtils.addDays(ladingBill.getSureDate(),1)),Restrictions.le("item.actualDeliveryDate",DateUtils.addDays(ladingBill.getSureDate(),1))));
			}
			if(ladingBill.getBillNo()!=null&&!"".equals(ladingBill.getBillNo())){
				String[] countryArr=null;
				if("eu".equals(ladingBill.getBillNo())){
					countryArr=new String[]{"de","fr","it","es","uk"};
				}else{
					countryArr=new String[]{ladingBill.getBillNo()};
				}
				dc.add(Restrictions.in("item.countryCode", countryArr));
			}
			return purchaseOrderDao.find(dc);
		}
		
		/**
		 * 获取最近一天产品数
		 */
		public Map<String,Integer> getCanSaleMap(){
			String sql="SELECT a.`data_date` FROM psi_product_in_stock AS a ORDER BY a.`data_date` DESC LIMIT 1";
			List<Date> dataDates = this.partsOrderDao.findBySql(sql);
			Map<String,Integer> rs = Maps.newHashMap();
			if(dataDates!=null&&dataDates.size()>0){
				sql="SELECT a.`product_name`,a.`total_stock` FROM psi_product_in_stock AS a WHERE a.`data_date`=:p1 AND a.country='total'";
				List<Object[]> objs=this.purchaseOrderDao.findBySql(sql, new Parameter(dataDates.get(0)));
				if(objs!=null&&objs.size()>0){
					for(Object[] obj:objs){
						String proColor=obj[0].toString();
						Integer quantity=Integer.parseInt(obj[1].toString());
						rs.put(proColor, quantity);
					}
				}
			}
			return rs;
		}
		
		/**
		 *获取所有产品的最大库存数 
		 * 
		 */
		public Map<String,Integer> getMaxInventory(){
			Map<String,Integer>  rs = Maps.newHashMap();
			String sql="SELECT CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'',a.`color`) END  AS proColor,a.`quantity` FROM  psi_product_attribute AS a";
			List<Object[]> objs=this.purchaseOrderDao.findBySql(sql);
			if(objs!=null&&objs.size()>0){
				for(Object[] obj:objs){
					String proColor=obj[0].toString();
					if(obj[1]!=null){
						Integer quantity=Integer.parseInt(obj[1].toString());
						rs.put(proColor, quantity);
					}
				}
			}
			return rs;
		}
		
		/**
		 *获取带颜色的产品
		 * 
		 */
		public Map<String,String> getAllProductColors(){
			Map<String,String>  rs = Maps.newHashMap();
			String sql="SELECT DISTINCT CONCAT(a.`product_name`,CASE  WHEN a.`product_color`='' THEN '' ELSE CONCAT('_',a.`product_color`) END) AS productName,a.`psi_product`,a.`product_color` FROM psi_barcode a WHERE a.`del_flag`='0'";
			List<Object[]> objs=this.purchaseOrderDao.findBySql(sql);
			if(objs!=null&&objs.size()>0){
				for(Object[] obj:objs){
					String proColor=obj[0].toString();
					rs.put(proColor, obj[1]+","+obj[2]);
				}
			}
			return rs;
		}
		

		
		/**
		 *获取原来的预计后货日期
		 * 
		 */
		public Map<Integer,Date> getOldDeliveryDate(Integer orderId){
			Map<Integer,Date>  rs = Maps.newHashMap();
			String sql="SELECT a.id,a.`actual_delivery_date` FROM psi_purchase_order_item AS a WHERE a.`purchase_order_id`=:p1 AND a.`del_flag`='0' AND a.`actual_delivery_date` IS NOT NULL";
			List<Object[]> objs=this.purchaseOrderDao.findBySql(sql,new Parameter(orderId));
			if(objs!=null&&objs.size()>0){
				for(Object[] obj:objs){
					Integer itemId=(Integer)obj[0];
					Date date=(Date)obj[1];
					rs.put(itemId, date);
				}
			}
			return rs;
		}
		
		
		/**
		 *超期邮件发信 
		 * 
		 */
		
//		public void sendEmailDeliveryReceived(PurchaseOrder order,Map<Integer,Date> oldDeliveryDateMap) { 
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			StringBuffer contents= new StringBuffer("");
//			String supplierName = supplierService.get(order.getSupplier().getId()).getNikename();
//			String flowUser     = sysService.getUser(order.getMerchandiser().getId()).getLoginName();
//			String orderNo      = order.getOrderNo();
//			Integer orderId     = order.getId();
//			List<PurchaseOrderItem> items = Lists.newArrayList();
//			if(order.getItems()!=null&&order.getItems().size()>0){
//				//编辑的时候要比较原时间
//				for(PurchaseOrderItem item:order.getItems()){
//					if(item.getDeliveryDateList()!=null&&item.getDeliveryDateList().size()>0){
//						//对分批收货进行处理
//						for(PurchaseOrderDeliveryDate delivery:item.getDeliveryDateList()){
//							//如果里面有部分收货日期大于PO交期
//							if(delivery.getDeliveryDate().after(item.getDeliveryDate())){
//								PurchaseOrderItem singleItem = new PurchaseOrderItem(item.getProductName(), item.getColorCode(), item.getCountryCode(),
//										delivery.getRemark(), delivery.getQuantity(),delivery.getQuantityReceived(),item.getDeliveryDate(),delivery.getDeliveryDate());
//								items.add(singleItem);
//							}
//						}
//					}else{
//						if(item.getActualDeliveryDate().after(item.getDeliveryDate())&&item.getActualDeliveryDate().compareTo(oldDeliveryDateMap.get(item.getId()))!=0){
//							items.add(item);
//						}
//					}
//					
//				}
//			}
//			
//			if(items.size()>0){
//				contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
//				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
//				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>采购员</th>");
//				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>供应商名称</th>");
//				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>订单编号</th>");
//				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>国家</th>");
//				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>产品型号</th>");
//				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>未收货数</th>");
//				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>订单交期</th>");
//				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>预计交期</th>");
//				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>超期天数</th>");
//				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>备注</th>");
//				contents.append("</tr>");
//			    for (PurchaseOrderItem item: items) {
//			    	String productName = item.getProductName();
//			    	if(StringUtils.isNotEmpty(item.getColorCode())){
//			    		productName=productName+"_"+item.getColorCode();
//			    	}
//					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
//					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+flowUser+"</td>");
//					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+supplierName+"</td>");
//					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/purchaseOrder/view?id="+orderId+"'>"+orderNo+"</a></td>");
//					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(("com".equals(item.getCountryCode())?"us":item.getCountryCode()).toString().toUpperCase())+"</td>");
//					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+productName+"</td>");
//					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getQuantityUnReceived()+"</td>");
//					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+sdf.format(item.getDeliveryDate())+"</td>");
//					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+sdf.format(item.getActualDeliveryDate())+"</td>");
//					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+DateUtils.spaceDays(item.getDeliveryDate(), item.getActualDeliveryDate())+"</td>");
//					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getRemark()+"</td>");
//					contents.append("</tr>"); 
//			   }
//			  contents.append("</table><br/>");
//			if(StringUtils.isNotEmpty(contents)){
//				Date date = new Date();
//				String  toAddress="amazon-sales@inateck.com,"+UserUtils.getUser().getEmail();
//				final MailInfo mailInfo = new MailInfo(toAddress,"采购订单收货延迟通知["+orderNo+"]",date);
//				mailInfo.setContent(contents.toString());
//				new Thread(){
//					public void run(){
//						mailManager.send(mailInfo);
//					}
//				}.start();
//			}
//		}
//	}
//		
	//更新预测单采购确认数量
	@Transactional(readOnly = false)
	public void updateFroecastPurchaseQuantity(Integer forecastItemId,Integer quantity){
			String sql ="UPDATE psi_forecast_order_item AS a SET purchase_quantity=:p2 WHERE a.`id`=:p1";
			this.purchaseOrderDao.updateBySql(sql, new Parameter(forecastItemId,quantity));
	}
	
	

	/**
	 * 9-15天之内要收货的产品
	 */
	public List<Object[]> getLastDeliveryProducts(Date startDate){
		String sql="SELECT (CASE WHEN b.`color_code` ='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END)  AS proName, b.`country_code`," +
				" CASE WHEN  c.`delivery_date` IS NOT NULL THEN SUM(c.quantity-c.quantity_received) ELSE SUM(b.`quantity_ordered`-b.`quantity_received`-b.`quantity_pre_received`) END AS quantity," +
				" MIN(CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`delivery_date` ELSE b.`actual_delivery_date` END) AS deliveryDate, a.merchandiser " +
				" FROM psi_purchase_order AS a," +
				" psi_purchase_order_item AS b LEFT JOIN psi_purchase_order_delivery_date AS c ON  b.`id`=c.`purchase_order_item_id` AND c.`del_flag`='0' AND (c.quantity-c.quantity_received) >0 " +
				" WHERE a.id=b.`purchase_order_id` AND b.del_flag='0' AND a.`order_sta` IN ('2','3') AND" +
				" (CASE WHEN c.`delivery_date` IS NOT NULL THEN  c.`delivery_date` ELSE b.`actual_delivery_date` END ) " +
				" BETWEEN :p1 AND :p2 GROUP BY b.`product_name`,b.`color_code`,b.`country_code` HAVING quantity>0 ";
		List<Object[]> objs = this.psiLadingBillDao.findBySql(sql, new Parameter(startDate,DateUtils.addDays(startDate, 6)));
		return objs;
	}
	
	
	/**
	 * 获得今天往后的所有产品
	 */
	public List<Object[]> getDeliveryProducts(Date startDate,Integer week,Set<Integer> newProductIds,String firstOnce,String moreOnce){
		String append="";
		Date endDate= null;
		if(week!=null){
			startDate=DateUtils.addDays(startDate,(week-1)*7);
			endDate=DateUtils.addDays(startDate,6);
		}else{
			endDate=DateUtils.addYears(startDate, 1);
		}
		List<String>  proNames = this.getFirstOrderProducts();
		List<Object[]> objs =null;
		Parameter para=null;
		if(newProductIds!=null&&newProductIds.size()>0){
			if("1".equals(firstOnce)&&"1".equals(moreOnce)){
				para= new Parameter(startDate,endDate,newProductIds);
				append="and b.product_id in :p3 ";
			}else if("1".equals(firstOnce)){
				para= new Parameter(startDate,endDate,newProductIds,proNames);
				append="and b.product_id in :p3 and (CASE WHEN b.`color_code` ='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END) in :p4 ";
			}else if("1".equals(moreOnce)){
				para= new Parameter(startDate,endDate,newProductIds,proNames);
				append="and b.product_id in :p3 and (CASE WHEN b.`color_code` ='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END) not in :p4 ";
			}
			
		}else{
			para= new Parameter(startDate,endDate);
		}
		String sql="SELECT (CASE WHEN b.`color_code` ='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END)  AS proName, " +
				" b.`country_code`,CASE WHEN  c.`delivery_date` IS NOT NULL THEN (c.quantity-c.quantity_received) ELSE (b.`quantity_ordered`-b.`quantity_received`-b.`quantity_pre_received`) END AS quantity, " +
				" MIN(CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`delivery_date` ELSE b.`actual_delivery_date` END) AS deliveryDate FROM psi_purchase_order AS a, " +
				" psi_purchase_order_item AS b LEFT JOIN psi_purchase_order_delivery_date AS c ON  b.`id`=c.`purchase_order_item_id` AND c.`del_flag`='0' AND (c.quantity-c.quantity_received) >0 " +
				" WHERE a.id=b.`purchase_order_id` AND b.del_flag='0' AND a.`order_sta` IN ('2','3') AND " +
				" (CASE WHEN c.`delivery_date` IS NOT NULL THEN  c.`delivery_date` ELSE b.`actual_delivery_date` END )  between :p1 and :p2 "+append+"" +
				" GROUP BY b.`product_name`,b.`color_code`,b.`country_code` HAVING quantity>0 ; ";
		objs=this.psiLadingBillDao.findBySql(sql,para);
		return objs;
	}
	
	/**
	 * 查询所有首单的产品  分颜色
	 */
	public List<String> getFirstOrderProducts(){
		String sql="SELECT (CASE WHEN aa.color_code='' THEN aa.product_name ELSE CONCAT(aa.product_name,'_',aa.color_code) END) AS productName" +
				" FROM (SELECT a.id,b.`product_name`,b.`color_code`FROM psi_purchase_order AS a,psi_purchase_order_item AS b WHERE a.id=b.`purchase_order_id`" +
				" AND b.`del_flag`='0' AND a.`order_sta`<>'6'  GROUP BY a.id,b.`product_name`,b.`color_code`) AS aa GROUP BY aa.product_name,aa.color_code " +
				"HAVING COUNT(*)=1";
		List<String> objs=this.psiLadingBillDao.findBySql(sql);
		return objs;
	}
	
	
	/**
	 *查找分批收货的订单项
	 */
	public List<Integer> getOrderItemFromDelivery(String productName,String country){
		String sql="SELECT a.`purchase_order_item_id` FROM psi_purchase_order_delivery_date AS a WHERE a.`del_flag`='0'" ;
		if(StringUtils.isNotEmpty(country)){
			sql+=" AND CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p1 AND a.country_code =:p2";
		}
		sql+="  GROUP BY a.purchase_order_item_id";
		List<Integer> objs=null;
		if(StringUtils.isNotEmpty(country)){
			objs=this.purchaseOrderDao.findBySql(sql,new Parameter(productName,country));
		}else{
			objs=this.purchaseOrderDao.findBySql(sql);
		}
		return objs;
	}
	 
	/**
	 *查找分批收货的订单项
	 */
	public List<Integer> getOrderItemFromDelivery2(String productName,String country){
		String sql="SELECT a.`purchase_order_item_id` FROM lc_psi_purchase_order_delivery_date AS a WHERE a.`del_flag`='0'" ;
		if(StringUtils.isNotEmpty(country)){
			sql+=" AND CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p1 AND a.country_code =:p2";
		}
		sql+="  GROUP BY a.purchase_order_item_id";
		List<Integer> objs=null;
		if(StringUtils.isNotEmpty(country)){
			objs=this.purchaseOrderDao.findBySql(sql,new Parameter(productName,country));
		}else{
			objs=this.purchaseOrderDao.findBySql(sql);
		}
		return objs;
	}
	 
	
	/**
	 *查找分批收货信息       产品名：国家：sku：数量
	 */
	public Map<String,Map<String,Map<String,Integer>>> getDeliveryInfos(Map<String,Map<String,Map<String,Integer>>> map,Map<String,String> skuMap){
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		Map<String, String> newMap=psiProductEliminateService.findIsNewMap();
		String sql="SELECT CASE WHEN a.color_code ='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END proName,a.country_code " +
				" ,SUM(a.quantity-a.quantity_received-(a.quantity_off-a.quantity_off_received)) AS unReceived FROM psi_purchase_order_delivery_date AS a WHERE a.`del_flag`='0'" +
				" AND a.`delivery_date`>=:p1 "+
				" AND a.`delivery_date`<=:p2 "+
				"  GROUP BY a.country_code, proName HAVING unReceived>0";
			List<Object[]> list=this.purchaseOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end)));
			for(Object[] obj:list){
				String name=obj[0].toString();
				String country=obj[1].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				String sku=skuMap.get(name+"_"+country);
				//没绑定sku的不算
				if(StringUtils.isEmpty(sku)&&"1".equals(newMap.get(name+"_"+country))){//产品名_颜色_国   1新品
					sku=name+"_"+country;
				}
				if(StringUtils.isEmpty(sku)){
					continue;
				}
				Map<String,Map<String,Integer>> temp=map.get(name);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name, temp);
				}
				Map<String,Integer> countryTemp=temp.get(country);
				if(countryTemp==null){
					countryTemp=Maps.newLinkedHashMap();
					temp.put(country, countryTemp);
				}
				Integer skuQuantity=countryTemp.get(sku);
				countryTemp.put(sku, quantity+(skuQuantity==null?0:skuQuantity));
				//countryTemp.put(sku, quantity);
				Integer tempQuantity=countryTemp.get("total");
				countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		   }
		return map;
	}
	
	/**
	 *查找分批收货信息       产品名：国家：sku：数量
	 */
	public Map<String,Map<String,Map<String,Integer>>> getDeliveryInfos2(Map<String,Map<String,Map<String,Integer>>> map,Map<String,String> skuMap){
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		Map<String, String> newMap=psiProductEliminateService.findIsNewMap();
		String sql="SELECT CASE WHEN a.color_code ='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END proName,a.country_code " +
				" ,SUM(a.quantity-a.quantity_received-(a.quantity_off-a.quantity_off_received)) AS unReceived FROM lc_psi_purchase_order_delivery_date AS a WHERE a.`del_flag`='0'" +
				" AND a.`delivery_date`>=:p1 "+
				" AND a.`delivery_date`<=:p2 "+
				"  GROUP BY a.country_code, proName HAVING unReceived>0";
			List<Object[]> list=this.purchaseOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end)));
			for(Object[] obj:list){
				String name=obj[0].toString();
				String country=obj[1].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				String sku=skuMap.get(name+"_"+country);
				//没绑定sku的不算
				if(StringUtils.isEmpty(sku)&&"1".equals(newMap.get(name+"_"+country))){//产品名_颜色_国   1新品
					sku=name+"_"+country;
				}
				if(StringUtils.isEmpty(sku)){
					continue;
				}
				Map<String,Map<String,Integer>> temp=map.get(name);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name, temp);
				}
				Map<String,Integer> countryTemp=temp.get(country);
				if(countryTemp==null){
					countryTemp=Maps.newLinkedHashMap();
					temp.put(country, countryTemp);
				}
				Integer skuQuantity=countryTemp.get(sku);
				countryTemp.put(sku, quantity+(skuQuantity==null?0:skuQuantity));
				//countryTemp.put(sku, quantity);
				Integer tempQuantity=countryTemp.get("total");
				countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		   }
		return map;
	}
	
	public Map<String,Integer> getDeliveryInfosByName(Map<String,Integer>  map,Set<String> name,Date date){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		String sql="SELECT CASE WHEN a.color_code ='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END proName " +
				" ,SUM(a.quantity-a.quantity_received-(a.quantity_off-a.quantity_off_received)) AS unReceived FROM psi_purchase_order_delivery_date AS a WHERE a.`del_flag`='0'" +
				" AND a.`delivery_date`>=:p1 "+
				" AND a.`delivery_date`<=:p2 and (CASE WHEN a.color_code ='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END) in :p3"+
				"  GROUP BY proName HAVING unReceived>0";
			List<Object[]> list=this.purchaseOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end),name));
			for(Object[] obj:list){
				String pname=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[1].toString());
				if(map.get(pname)==null){
					map.put(pname, quantity);
				}else{
					Integer tempQuantity=map.get(pname);
					map.put(pname, tempQuantity+quantity);
				}
		   }
		return map;
	}
	
	public Map<String,Integer> getDeliveryInfosByName2(Map<String,Integer>  map,Set<String> name,Date date){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		String sql="SELECT CASE WHEN a.color_code ='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END proName " +
				" ,SUM(a.quantity-a.quantity_received-(a.quantity_off-a.quantity_off_received)) AS unReceived FROM lc_psi_purchase_order_delivery_date AS a WHERE a.`del_flag`='0'" +
				" AND a.`delivery_date`>=:p1 "+
				" AND a.`delivery_date`<=:p2 and (CASE WHEN a.color_code ='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END) in :p3"+
				"  GROUP BY proName HAVING unReceived>0";
			List<Object[]> list=this.purchaseOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end),name));
			for(Object[] obj:list){
				String pname=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[1].toString());
				if(map.get(pname)==null){
					map.put(pname, quantity);
				}else{
					Integer tempQuantity=map.get(pname);
					map.put(pname, tempQuantity+quantity);
				}
		   }
		return map;
	}
	
	
	/**
	 *查找分批收货信息       产品名：国家：sku：数量
	 */
	public Map<String,Map<String,Integer>> getDeliveryInfos(Map<String,Map<String,Integer>> map,Map<String,String> skuMap,String proName,String country){
		String sql="SELECT a.country_code,CASE WHEN a.color_code ='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END proName " +
				" ,SUM(a.quantity-a.quantity_received-(a.quantity_off-a.quantity_off_received)) AS unReceived FROM psi_purchase_order_delivery_date AS a WHERE a.`del_flag`='0'" +
				" AND a.`delivery_date`>=SUBDATE(CURDATE(),DATE_FORMAT(CURDATE(),'%w')-1) "+
				" AND a.`delivery_date`<SUBDATE(DATE_ADD(CURDATE(),INTERVAL 7 DAY),DATE_FORMAT(DATE_ADD(CURDATE(),INTERVAL 7 DAY),'%w')-1) "+
				" AND CONCAT(a.`product_name`,CASE WHEN a.`color_code`='' THEN '' ELSE  CONCAT('_',a.`color_code`) END)=:p1 and a.`country_code`=:p2 "+
				"  GROUP BY a.country_code, proName HAVING unReceived>0";
			List<Object[]> list=this.purchaseOrderDao.findBySql(sql,new Parameter(proName,country));
			for(Object[] obj:list){
				String name=obj[1].toString();
				String countryCode=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				String sku=skuMap.get(name+"_"+countryCode);
				Map<String,Integer> temp=map.get(name);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name, temp);
				}
				
				Integer skuQuantity=temp.get(sku);
				temp.put(sku, quantity+(skuQuantity==null?0:skuQuantity));
				//countryTemp.put(sku, quantity);
				Integer tempQuantity=temp.get("total");
				temp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		   }
		return map;
	}
	
	public Map<String,Map<String,Integer>> getDeliveryInfos2(Map<String,Map<String,Integer>> map,Map<String,String> skuMap,String proName,String country){
		String sql="SELECT a.country_code,CASE WHEN a.color_code ='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END proName " +
				" ,SUM(a.quantity-a.quantity_received-(a.quantity_off-a.quantity_off_received)) AS unReceived FROM lc_psi_purchase_order_delivery_date AS a WHERE a.`del_flag`='0'" +
				" AND a.`delivery_date`>=SUBDATE(CURDATE(),DATE_FORMAT(CURDATE(),'%w')-1) "+
				" AND a.`delivery_date`<SUBDATE(DATE_ADD(CURDATE(),INTERVAL 7 DAY),DATE_FORMAT(DATE_ADD(CURDATE(),INTERVAL 7 DAY),'%w')-1) "+
				" AND CONCAT(a.`product_name`,CASE WHEN a.`color_code`='' THEN '' ELSE  CONCAT('_',a.`color_code`) END)=:p1 and a.`country_code`=:p2 "+
				"  GROUP BY a.country_code, proName HAVING unReceived>0";
			List<Object[]> list=this.purchaseOrderDao.findBySql(sql,new Parameter(proName,country));
			for(Object[] obj:list){
				String name=obj[1].toString();
				String countryCode=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				String sku=skuMap.get(name+"_"+countryCode);
				Map<String,Integer> temp=map.get(name);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name, temp);
				}
				
				Integer skuQuantity=temp.get(sku);
				temp.put(sku, quantity+(skuQuantity==null?0:skuQuantity));
				//countryTemp.put(sku, quantity);
				Integer tempQuantity=temp.get("total");
				temp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		   }
		return map;
	}
		
	@Transactional(readOnly = false)
	public String updateDeliveryDate(Integer orderItemId,Date deliveryDate){
		String sql ="UPDATE  psi_purchase_order_item AS a SET a.`actual_delivery_date`=:p1 WHERE a.id=:p2 ";
		int i =this.purchaseOrderDao.updateBySql(sql, new Parameter(deliveryDate,orderItemId));
		if(i>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	
	public void saveAll(List<PurchaseOrder> purchaseOrders) {
		purchaseOrderDao.save(purchaseOrders);
	}
	
	/**
	 * 统计所有产品的采购数量
	 * @param type 1:总数(默认)  2：在产
	 * @return Map<String,Integer> [productName qty]
	 */
	public Map<String,Integer> getAllQty(String type){
		Map<String,Integer> rs = Maps.newHashMap();
		String temp = "('1','2','3','4','5')";
		if ("2".equals(type)) {
			temp = "('1','2','3')";
		}
		String sql="SELECT product_name,color_code,SUM(orderNum) FROM ("+
				" SELECT b.`product_name`,b.`color_code`,"+
				" SUM(b.`quantity_ordered`) AS orderNum"+
				" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b "+
				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN "+temp+" AND b.`del_flag`='0' "+
				" GROUP BY b.product_name,b.color_code  "+
				" UNION ALL SELECT b.`product_name`,b.`color_code`,"+
				" SUM(b.`quantity_ordered`) AS orderNum"+
				" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b "+
				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN "+temp+" AND b.`del_flag`='0' "+
				" GROUP BY b.product_name,b.color_code ) AS t GROUP BY product_name,color_code";
		List<Object[]> list=this.purchaseOrderDao.findBySql(sql);
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
	
	/**
	 * 统计所有产品的最小交付时间
	 * @return Map<String, String> [productName yyyy-MM-dd]
	 */
	public Map<String, String> getAllDeliveryDate(){
		Map<String, String> rs = Maps.newHashMap();
		String sql="SELECT product_name,color_code,MIN(dates) FROM("+
				" SELECT t.`product_name`,t.`color_code`,MIN(t.`delivery_date`) AS dates"+
				" FROM `psi_purchase_order_item` t,`psi_purchase_order` o "+
				" WHERE t.`purchase_order_id`=o.`id` AND o.`order_sta` IN ('1','2','3','4','5')"+
				" GROUP BY t.`product_name`,t.`color_code`"+
				" UNION ALL"+
				" SELECT t.`product_name`,t.`color_code`,MIN(t.`delivery_date`)  AS dates"+
				" FROM `lc_psi_purchase_order_item` t,`lc_psi_purchase_order` o "+
				" WHERE t.`purchase_order_id`=o.`id` AND o.`order_sta` IN ('1','2','3','4','5')"+
				" GROUP BY t.`product_name`,t.`color_code`"+
				" ) AS aa GROUP BY product_name,color_code";
		List<Object[]> list=this.purchaseOrderDao.findBySql(sql);
		for(Object[] obj:list){
			String productName = obj[0].toString();
			String color = obj[1]==null?"":obj[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			String date = obj[2]==null?"":obj[2].toString();
			rs.put(productName, date);
	    }
		return rs;
	}
	
	/**
	 *查找分批收货的订单项
	 */
	public List<Integer> getOrderItemFromDelivery(String productName,String country,Date date){
		String sql="SELECT a.`purchase_order_item_id` FROM lc_psi_purchase_order_delivery_date AS a WHERE delivery_date>=:p1 and a.`del_flag`='0'" ;
		if(StringUtils.isNotEmpty(country)){
			sql+=" AND CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p2 AND a.country_code =:p3";
		}
		sql+="  GROUP BY a.purchase_order_item_id";
		List<Integer> objs=null;
		if(StringUtils.isNotEmpty(country)){
			objs=this.purchaseOrderDao.findBySql(sql,new Parameter(date,productName,country));
		}else{
			objs=this.purchaseOrderDao.findBySql(sql,new Parameter(date));
		}
		return objs;
	}
	
	
	public Map<String,Map<Date,Map<String,Integer>>> getPODeliveryInfos(Map<String,Map<Date,Map<String,Integer>>> map,Map<String,String> skuMap,Map<String,PsiProductEliminate> attrMap){
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(new Date());
		String sql="SELECT CASE WHEN a.color_code ='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END proName,a.country_code " +
				" ,SUM(a.quantity-a.quantity_received-(a.quantity_off-a.quantity_off_received)) AS unReceived,a.`delivery_date` FROM lc_psi_purchase_order_delivery_date AS a WHERE a.`del_flag`='0'" +
				" AND a.`delivery_date`>=:p1 GROUP BY a.country_code, proName HAVING unReceived>0";
			List<Object[]> list=this.purchaseOrderDao.findBySql(sql,new Parameter(start));
			for(Object[] obj:list){
				String name=obj[0].toString();
				String country=obj[1].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				String sku=skuMap.get(name+"_"+country);
				Date date=(Date)obj[3];
				//没绑定sku的不算
				if(StringUtils.isEmpty(sku)&&"新品".equals(attrMap.get(name+"_"+country).getIsSale())){//产品名_颜色_国   1新品
					sku=name+"_"+country;
				}
				if(StringUtils.isEmpty(sku)){
					continue;
				}
				Map<Date,Map<String,Integer>> temp=map.get(name+"_"+country);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name+"_"+country, temp);
				}
				Map<String,Integer> countryTemp=temp.get(date);
				if(countryTemp==null){
					countryTemp=Maps.newLinkedHashMap();
					temp.put(date, countryTemp);
				}
				Integer skuQuantity=countryTemp.get(sku);
				countryTemp.put(sku, quantity+(skuQuantity==null?0:skuQuantity));
				Integer tempQuantity=countryTemp.get("total");
				countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		   }
		return map;
	}
	
//	public static void main(String[] args) {
//		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
//		PurchaseOrderService  service = applicationContext.getBean(PurchaseOrderService.class);
//		Map<String,Integer> rs = service.getAllQty();
//		for (String productName : rs.keySet()) {
//			System.out.println(productName + "\t" + rs.get(productName));
//		}
//		SaleReportService reportService = applicationContext.getBean(SaleReportService.class);
//		Map<String, Integer> rs = reportService.getAllSalesVolume();
//		for (String productName : rs.keySet()) {
//			System.out.println(productName + "\t" + rs.get(productName));
//		}
//		applicationContext.close();
//	}
}
