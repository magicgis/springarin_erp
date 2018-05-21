/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.Collections3;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.EntityComparator;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.psi.dao.PsiLadingBillDao;
import com.springrain.erp.modules.psi.dao.PsiLadingBillItemDao;
import com.springrain.erp.modules.psi.dao.PurchaseAmountAdjustDao;
import com.springrain.erp.modules.psi.dao.PurchaseOrderDao;
import com.springrain.erp.modules.psi.dao.PurchaseOrderItemDao;
import com.springrain.erp.modules.psi.dao.PurchasePaymentDao;
import com.springrain.erp.modules.psi.dao.PurchasePaymentItemDao;
import com.springrain.erp.modules.psi.entity.PsiLadingBill;
import com.springrain.erp.modules.psi.entity.PsiLadingBillItem;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PurchaseAmountAdjust;
import com.springrain.erp.modules.psi.entity.PurchaseOrder;
import com.springrain.erp.modules.psi.entity.PurchaseOrderItem;
import com.springrain.erp.modules.psi.entity.PurchasePayment;
import com.springrain.erp.modules.psi.entity.PurchasePaymentItem;
import com.springrain.erp.modules.psi.entity.PurchasePaymentItemDto;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 采购付款Service
 * @author Michael
 * @version 2014-11-21
 */
@Component
@Transactional(readOnly = true)
public class PurchasePaymentService extends BaseService {
	@Autowired
	private PurchasePaymentDao 			purchasePaymentDao;
	@Autowired
	private GenerateSequenceDao 		genSequenceDao;
	@Autowired
	private PurchaseOrderDao      		purchaseOrderDao;
	@Autowired
	private PurchaseOrderItemDao  		purchaseOrderItemDao;
	@Autowired
	private PsiLadingBillDao  	  		ladingBillDao;
	@Autowired
	private PsiLadingBillItemDao  	    ladingBillItemDao;
	@Autowired
	private PsiLadingBillService 		ladingService;
	@Autowired
	private PsiSupplierService    		psiSupplierService; 
	@Autowired
	private PurchasePaymentItemDao  	purchasePaymentItemDao;
	@Autowired
	private PurchaseAmountAdjustDao     adjustDao;
	@Autowired
	private SystemService systemService;
	
	public PurchasePayment get(Integer id) {
		return purchasePaymentDao.get(id);
	}
	
	public PurchasePayment get(String paymentId) {
		DetachedCriteria dc = purchasePaymentDao.createDetachedCriteria();
		dc.add(Restrictions.eq("paymentNo", paymentId));
		List<PurchasePayment> rs = purchasePaymentDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	public Page<PurchasePayment> find(Page<PurchasePayment> page, PurchasePayment purchasePayment) {
		DetachedCriteria dc = purchasePaymentDao.createDetachedCriteria();
		if(purchasePayment.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",purchasePayment.getCreateDate()));
		}
		
		if(purchasePayment.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(purchasePayment.getUpdateDate(),1)));
		}
		
		if(StringUtils.isNotEmpty(purchasePayment.getPaymentNo())){
			dc.add(Restrictions.like("paymentNo", "%"+purchasePayment.getPaymentNo()+"%"));
		}
		
		if(StringUtils.isNotEmpty(purchasePayment.getPaymentSta())){
			dc.add(Restrictions.eq("paymentSta", purchasePayment.getPaymentSta()));
		}else{
			dc.add(Restrictions.ne("paymentSta", "3"));
		}
		
		if(purchasePayment.getSupplier()!=null&&purchasePayment.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", purchasePayment.getSupplier()));
		}
		dc.addOrder(Order.desc("id"));
		return purchasePaymentDao.find(page, dc);
	}
	
	
	/***
	 * 获取未付款order信息 
	 *para 定金比>0    订单状态非草稿（1）和已付款完成（5）和取消（6）     申请定金金额为0
	 */
	public List<Object[]> getUnPaymentOrder(Integer supplierId,String currencyType){
		String getUnpaymentOrderSql="";
		if(StringUtils.isNotEmpty(currencyType)){
			getUnpaymentOrderSql="SELECT order_no,id,((order_total*deposit)/100-deposit_amount-deposit_pre_amount) AS upPay,deposit,currency_type,order_total FROM psi_purchase_order  WHERE deposit>0 AND order_sta NOT IN('0','1','5','6')  AND del_flag <>'1' AND pi_file_path IS NOT NULL AND ((order_total*deposit)/100-deposit_amount-deposit_pre_amount)>0 AND supplier_id=:p1 AND currency_type=:p2 ORDER BY id ";
			return this.purchasePaymentDao.findBySql(getUnpaymentOrderSql, new Parameter(supplierId,currencyType));
		}else{
			getUnpaymentOrderSql="SELECT order_no,id,((order_total*deposit)/100-deposit_amount-deposit_pre_amount) AS upPay,deposit,currency_type,order_total FROM psi_purchase_order  WHERE deposit>0 AND order_sta NOT IN('0','1','5','6')  AND del_flag <>'1' AND pi_file_path IS NOT NULL AND ((order_total*deposit)/100-deposit_amount-deposit_pre_amount)>0 AND supplier_id=:p1 ORDER BY id ";
			return this.purchasePaymentDao.findBySql(getUnpaymentOrderSql, new Parameter(supplierId));
		}
		
		
		
		
	}
	
	
	/***
	 *获取未付完款    提单信息 
	 * 
	 */
	public List<Object[]> getUnPaymentLading(Integer supplierId,String currencyType){
		String getUnPaymentLading="";
		if(StringUtils.isNotEmpty(currencyType)){
			getUnPaymentLading="SELECT bill_no,id,(total_amount - total_payment_amount - total_payment_pre_amount ) as aaa,(total_payment_amount + total_payment_pre_amount) as bbb,currency_type FROM psi_lading_bill WHERE (total_amount-total_payment_amount - total_payment_pre_amount)>0 AND del_flag <>'1' AND bill_sta <> '2' AND supplier_id=:p1 AND currency_type=:p2 ORDER BY id ";
			return this.purchasePaymentDao.findBySql(getUnPaymentLading,new Parameter(supplierId,currencyType));
		}else{
			getUnPaymentLading="SELECT bill_no,id,(total_amount - total_payment_amount - total_payment_pre_amount ) as aaa,(total_payment_amount + total_payment_pre_amount) as bbb,currency_type FROM psi_lading_bill WHERE (total_amount-total_payment_amount - total_payment_pre_amount)>0 AND del_flag <>'1' AND bill_sta <> '2' AND supplier_id=:p1 ORDER BY id ";
			return this.purchasePaymentDao.findBySql(getUnPaymentLading,new Parameter(supplierId));
		}
		
		
	}
	
	/***
	 *获取未付完款    提单信息 
	 * 
	 */
	public List<PurchasePaymentItemDto> getUnPaymentLadingItem(Integer supplierId,String currencyType,boolean flag){
		String getUnPaymentLading="";
		List<Object[]> list=null;
		if(StringUtils.isNotEmpty(currencyType)){
			getUnPaymentLading="SELECT a.`create_date`,b.`id`,b.`balance_delay1`,b.`balance_delay2`,b.`balance_rate1`,b.`balance_rate2`,b.`total_amount`,b.`total_payment_amount`,b.`total_payment_pre_amount`,b.`total_amount`*b.`balance_rate1`/100 AS firstShouldPay, " +
					" CASE WHEN b.color_code='' THEN b.product_name ELSE CONCAT( b.product_name,'_',b.color_code) END AS proName,b.country_code,a.bill_no,a.currency_type,b.quantity_lading,a.id as ladingId,b.item_price,b.purchase_order_item_id,a.bill_sta " +
					" FROM psi_lading_bill AS a ,psi_lading_bill_item AS b WHERE a.id=b.`lading_bill_id` AND a.`total_amount`>(a.`total_payment_amount`+a.`total_payment_pre_amount`) AND b.`total_amount`>(b.`total_payment_amount`+b.`total_payment_pre_amount`) " +
					" AND b.`del_flag`='0' AND a.`bill_sta`!='2' AND a.`supplier_id`=:p1 AND a.currency_type=:p2 ";
			list= this.purchasePaymentDao.findBySql(getUnPaymentLading,new Parameter(supplierId,currencyType));
		}else{
			getUnPaymentLading="SELECT a.`create_date`,b.`id`,b.`balance_delay1`,b.`balance_delay2`,b.`balance_rate1`,b.`balance_rate2`,b.`total_amount`,b.`total_payment_amount`,b.`total_payment_pre_amount`,b.`total_amount`*b.`balance_rate1`/100 AS firstShouldPay, " +
					" CASE WHEN b.color_code='' THEN b.product_name ELSE CONCAT( b.product_name,'_',b.color_code) END AS proName,b.country_code,a.bill_no,a.currency_type,b.quantity_lading,a.id as ladingId,b.item_price,b.purchase_order_item_id,a.bill_sta " +
					" FROM psi_lading_bill AS a ,psi_lading_bill_item AS b WHERE a.id=b.`lading_bill_id` AND a.`total_amount`>(a.`total_payment_amount`+a.`total_payment_pre_amount`) AND b.`total_amount`>(b.`total_payment_amount`+b.`total_payment_pre_amount`) " +
					" AND b.`del_flag`='0' AND a.`bill_sta`!='2' AND a.`supplier_id`=:p1 ";
			list= this.purchasePaymentDao.findBySql(getUnPaymentLading,new Parameter(supplierId));
		}
		long dateL = new Date().getTime();
		List<PurchasePaymentItemDto>  dtos = Lists.newArrayList(); 
		List<PurchasePaymentItemDto>  limitDtos = Lists.newArrayList(); 
		
		//先遍历一道，查询订单item的deposit
		Set<Integer> orderItems = Sets.newHashSet();
		if(list!=null){
			for(Object[] obj:list){
				orderItems.add(Integer.parseInt(obj[17].toString()));
			}
		}
		
		if(list!=null&&list.size()>0){
			Map<Integer,Integer> depositMap = Maps.newHashMap();   
			String orderSql="SELECT b.`id`,a.`deposit` FROM psi_purchase_order AS a,psi_purchase_order_item AS b WHERE a.id=b.`purchase_order_id`  AND b.id IN :p1 ";
			List<Object[]> orderItemInfos=purchasePaymentDao.findBySql(orderSql, new Parameter(orderItems));
			if(orderItemInfos!=null&&orderItemInfos.size()>0){
				for(Object[] obj: orderItemInfos) {
					depositMap.put(Integer.parseInt(obj[0].toString()), Integer.parseInt(obj[1].toString()));
				}
			}
			for(Object[] obj:list){
				Date createDate = (Date)obj[0];
				Integer ladingItemId=Integer.parseInt(obj[1].toString());
				Integer delay1=Integer.parseInt(obj[2].toString());
				Integer delay2=Integer.parseInt(obj[3].toString());
				Integer rate1=Integer.parseInt(obj[4].toString());
				Integer rate2=Integer.parseInt(obj[5].toString());
				BigDecimal   totalAmount = new BigDecimal(obj[6].toString());
				BigDecimal   payAmount = new BigDecimal(obj[7].toString());
				BigDecimal   prePayAmount = new BigDecimal(obj[8].toString());
				BigDecimal   firstAmount  = new BigDecimal(obj[9].toString());
				String  proName = obj[10].toString();
				String  country = obj[11].toString();
				String  billNo  = obj[12].toString();
				String  currency = obj[13].toString();
				Integer  quantity = Integer.parseInt(obj[14].toString());
				Integer  ladingId = Integer.parseInt(obj[15].toString());
				BigDecimal  itemPrice = new BigDecimal(obj[16].toString());
				Integer  orderItemId = Integer.parseInt(obj[17].toString());
				String billSta = obj[18].toString();
				Integer  deposit =depositMap.get(orderItemId);
				if(rate1.intValue()==100){
					PurchasePaymentItemDto dto = new PurchasePaymentItemDto(delay1, (dateL-createDate.getTime()), totalAmount, prePayAmount,payAmount, rate1, delay1, rate2, delay2,ladingItemId,proName,country,billNo,rate1,null,currency,quantity,ladingId,itemPrice,deposit,billSta);
					dtos.add(dto);
				}else if(rate2.intValue()==100){
					PurchasePaymentItemDto dto = new PurchasePaymentItemDto(delay2, (dateL-createDate.getTime()), totalAmount, prePayAmount,payAmount, rate1, delay1, rate2, delay2,ladingItemId,proName,country,billNo,rate2,null,currency,quantity,ladingId,itemPrice,deposit,billSta);
					dtos.add(dto);
				}else{
					if(payAmount.add(prePayAmount).compareTo(firstAmount)>=0){
						//如果付款金额大于分批第一次金额
						PurchasePaymentItemDto dto = new PurchasePaymentItemDto(delay2, (dateL-createDate.getTime()), totalAmount, prePayAmount,payAmount, rate1, delay1, rate2, delay2,ladingItemId,proName,country,billNo,rate2,null,currency,quantity,ladingId,itemPrice,deposit,billSta);
						dtos.add(dto);
					}else{
						BigDecimal tempRate1=new BigDecimal(rate1).divide(new BigDecimal(100));
						BigDecimal tempRate2=new BigDecimal(rate2).divide(new BigDecimal(100));
						PurchasePaymentItemDto dto1 = new PurchasePaymentItemDto(delay1, (dateL-createDate.getTime()), totalAmount.multiply(tempRate1), prePayAmount.multiply(tempRate1),payAmount.multiply(tempRate1), rate1, delay1, rate2, delay2,ladingItemId,proName,country,billNo,rate1,null,currency,quantity,ladingId,itemPrice,deposit,billSta);
						PurchasePaymentItemDto dto2 = new PurchasePaymentItemDto(delay2, (dateL-createDate.getTime()), totalAmount.multiply(tempRate2), prePayAmount.multiply(tempRate2),payAmount.multiply(tempRate2), rate1, delay1, rate2, delay2,ladingItemId,proName,country,billNo,rate2,null,currency,quantity,ladingId,itemPrice,deposit,billSta);
						dtos.add(dto1);
						dtos.add(dto2);
					}
				}
			}
			
			//按预期天数   和创建时间排序
			Collections.sort(dtos, EntityComparator.createComparator(-1, "delayDays","balanceDateNums"));   
			//数量太多前台最大只能256，
			if(flag){//如果不用限制就直接返回
				return dtos;
			}
			if(dtos.size()>200){
				for(int i =0;i<200;i++){
					limitDtos.add(dtos.get(i));
				}
			}else{
				return dtos;
			}
		}
		
		
		return limitDtos;
	}
	
	
	//取消
	@Transactional(readOnly = false)
	public boolean cancelPurchasePayment(PurchasePayment purchasePayment){
		boolean flag=true;
		Set<Integer>  ladingBillIds = Sets.newHashSet();
		Set<Integer>  ladingBillItemIds = Sets.newHashSet();
		for(PurchasePaymentItem item : purchasePayment.getItems()) {
			if("1".equals(item.getPaymentType())){
				ladingBillIds.add(item.getLadingBill().getId());
				ladingBillItemIds.add(item.getLadingBillItem().getId());
			}
			
		}
		Map<Integer,PsiLadingBill> ladingBillMap= Maps.newHashMap();
		if(ladingBillIds.size()>0){
			ladingBillMap=this.ladingService.findByIds(ladingBillIds);
		}
		
		Map<Integer,PsiLadingBillItem> ladingBillItemMap= Maps.newHashMap();
		if(ladingBillItemIds.size()>0){
			ladingBillItemMap=this.ladingService.findItemsByIds(ladingBillItemIds);
		}
		
		
		//追回提单预付款项        订单预付款项
		for(PurchasePaymentItem payItem:purchasePayment.getItems()){
			PsiLadingBill bill =payItem.getLadingBill();
			BigDecimal preAmonut = payItem.getPaymentAmount();//bill.getTotalPaymentPreAmount();
			if(bill!=null){
				//提单不为空说明是尾款       否则为定金
				PsiLadingBill oldLadingBill=ladingBillMap.get(bill.getId());
				oldLadingBill.setTotalPaymentPreAmount(oldLadingBill.getTotalPaymentPreAmount().subtract(preAmonut));
				
				PsiLadingBillItem oldLadingBillItem=ladingBillItemMap.get(payItem.getLadingBillItem().getId());
				oldLadingBillItem.setTotalPaymentPreAmount(oldLadingBillItem.getTotalPaymentPreAmount().subtract(preAmonut));
				//目前预付款只到定金这里，以后尽量到订单
			}else{
				PurchaseOrder order=payItem.getOrder();
				//定金只能付一次，直接清空预付定金即可
				order.setDepositPreAmount(order.getDepositPreAmount().subtract(preAmonut));
				this.purchaseOrderDao.save(order);
			}
		}
		
		if(ladingBillItemMap.size()>0){
			for(Map.Entry<Integer, PsiLadingBill> entry:ladingBillMap.entrySet()){
				this.ladingBillDao.save(entry.getValue());
			}
			
			for(Map.Entry<Integer, PsiLadingBillItem> entry:ladingBillItemMap.entrySet()){
				this.ladingBillItemDao.save(entry.getValue());
			}
		}
		
		
		
//		for(PurchasePaymentItem item : purchasePayment.getItems()) {
//			if("1".equals(item.getPaymentType())){
//				PsiLadingBill bill=ladingBillMap.get(item.getLadingBill().getId());
//				PsiLadingBillItem billItem = ladingBillItemMap.get(item.getLadingBillItem().getId());
//				//提单信息保存
////				item.setLadingBill(bill);
//				this.ladingBillDao.save(bill);
//				//提单item信息保存
////				item.setLadingBillItem(billItem);
//				this.ladingBillItemDao.save(billItem);
//			}
//		}
		
		
		if("1".equals(purchasePayment.getHasAdjust())){
			//把申请状态变为可付款状态
			this.adjustDao.updateStaByPaymentId(purchasePayment.getId(), "0");
		}
		
		purchasePayment.setPaymentSta("3"); 
		purchasePayment.setCancelDate(new Date());
		purchasePayment.setCancelUser(UserUtils.getUser());
		purchasePaymentDao.save(purchasePayment);
		
		return flag;
	}
	
	
	@Transactional(readOnly = false)
	public void save(PurchasePayment purchasePayment) {
		purchasePaymentDao.save(purchasePayment);
	}
	
	@Transactional(readOnly = false)
	public  boolean addSave(PurchasePayment purchasePayment) throws Exception {
		if(purchasePayment.getSupplier()!=null&&purchasePayment.getSupplier().getId()!=null){
			String paymentNo=this.genSequenceDao.genSequence(psiSupplierService.get(purchasePayment.getSupplier().getId()).getNikename()+"_FKH",2);
			purchasePayment.setPaymentNo(paymentNo);
		}
		boolean rs = false;
		Set<Integer>  ladingBillIds = Sets.newHashSet();
		Set<Integer>  ladingBillItemIds = Sets.newHashSet();
		for(PurchasePaymentItem item : purchasePayment.getItems()) {
			item.setPurchasePayment(purchasePayment);
			if(purchasePayment.getPaymentSta().equals("1")){
				if("1".equals(item.getPaymentType())){
					ladingBillIds.add(item.getLadingBill().getId());
					ladingBillItemIds.add(item.getLadingBillItem().getId());
				}
				item.setDelFlag("0");
			}
		}
		
		purchasePayment.setCreateDate(new Date());
		purchasePayment.setCreateUser(UserUtils.getUser());
		purchasePayment.setUpdateDate(new Date());
		purchasePayment.setUpdateUser(UserUtils.getUser());
		
		rs = replyEmailOrGetTotal(purchasePayment,ladingBillIds,ladingBillItemIds);
		
		
		purchasePayment.setDelFlag("0");
		purchasePayment.setRealPaymentAmount(0f);
		purchasePaymentDao.save(purchasePayment);
		
		if(purchasePayment.getPaymentSta().equals("1")&&purchasePayment.getAdjusts()!=null&&purchasePayment.getAdjusts().size()>0){
			for(PurchaseAmountAdjust adj :purchasePayment.getAdjusts()){
				//更新额外付款项为  申请状态
				adjustDao.updateStaPaymentIdById(adj.getId(),"1",purchasePayment.getId());
			}
			purchasePayment.setHasAdjust("1");
		}
		return rs;
	}
	

//	@Transactional(readOnly = false)
//	public boolean editSave(PurchasePayment purchasePayment) {
//		boolean rs = false;
//		try {
//			rs = replyEmailOrGetTotal(purchasePayment);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}	
//		if(!rs){
//			for(PurchasePaymentItem item : purchasePayment.getItems()) {
//				item.setPurchasePayment(purchasePayment);
//				if(purchasePayment.getPaymentSta().equals("1")){
//					if("0".equals(item.getPaymentType())){
//						item.getOrder().setDepositPreAmount(0f);
//					}else if("1".equals(item.getPaymentType())){
//						PsiLadingBill bill=item.getLadingBill();
//						float pre = bill.getTotalPaymentPreAmount();
//						bill.setTotalPaymentPreAmount(pre-item.getPaymentAmount());
//					}
//				}
//			}
//			purchasePayment.setPaymentSta("0");
//		}else{
//			for(PurchasePaymentItem item : purchasePayment.getItems()) {
//				item.setPurchasePayment(purchasePayment);
//				if("0".equals(item.getPaymentType())){
//					item.getOrder().setPayItem(item);
//				}
//			}
//		}
//		
//		Collection<String> setNewIds = Collections2.transform(purchasePayment.getItems(), new Function<PurchasePaymentItem, String>() {
//			public String apply(PurchasePaymentItem input) {
//				return input.getId().toString();
//			};
//		});
//		
//		Set<Integer>  delItemSet = Sets.newHashSet();
//		String oldItemIds=purchasePayment.getOldItemIds();
//		String [] oldIds = oldItemIds.split(",");
//		if(setNewIds!=null&&setNewIds.size()>0){
//			for(int j=0;j<oldIds.length;j++){
//				if(!setNewIds.contains(oldIds[j])){
//					//不包含就干掉
//					//this.purchasePaymentDao.deleteOrderItem(Integer.valueOf(oldIds[j]));
//					delItemSet.add(Integer.valueOf(oldIds[j]));
//				};
//			}
//		}else{
//			for(int j=0;j<oldIds.length;j++){
//				//this.purchasePaymentDao.deleteOrderItem(Integer.valueOf(oldIds[j]));
//				delItemSet.add(Integer.valueOf(oldIds[j]));
//			}
//		}
//		
//		
//		if(delItemSet.size()>0){
//			for(PurchasePaymentItem item:this.getPurchasePaymentItems(delItemSet)){
//				item.setDelFlag("1");
//				item.setPurchasePayment(purchasePayment);
//				purchasePayment.getItems().add(item);
//			};
//		}
//		
//		
//		purchasePayment.setUpdateDate(new Date());
//		purchasePayment.setUpdateUser(UserUtils.getUser());
//		purchasePaymentDao.getSession().merge(purchasePayment);
//		if(purchasePayment.getPaymentSta().equals("1")&&purchasePayment.getAdjusts()!=null&&purchasePayment.getAdjusts().size()>0){
//			for(PurchaseAmountAdjust adj :purchasePayment.getAdjusts()){
//				//更新额外付款项为  申请状态
//				adjustDao.updateStaPaymentIdById(adj.getId(),"1",purchasePayment.getId());
//			}
//			purchasePayment.setHasAdjust("1");
//		}
//		return rs;
//	}
	
	@Autowired
	private MailManager mailManager;
	
	private boolean replyEmailOrGetTotal(PurchasePayment purchasePayment,Set<Integer>  ladingBillIds,Set<Integer>  ladingBillItemIds) throws Exception{
		purchasePayment.setSupplier(psiSupplierService.get(purchasePayment.getSupplier().getId()));
		//如果是申请的时候    更新额外付款状态为申请付款
		BigDecimal adjustTotal=new BigDecimal(0);
		String orderNo="";
		if(purchasePayment.getPaymentSta().equals("1")&&purchasePayment.getAdjusts()!=null&&purchasePayment.getAdjusts().size()>0){
			for(PurchaseAmountAdjust adj :purchasePayment.getAdjusts()){
				//更新额外付款项为  申请状态
				adj.setPaymentId(purchasePayment.getId());
				adjustTotal=adjustTotal.add(new BigDecimal(adj.getAdjustAmount()));
			}
		}
		
		Map<Integer,PsiLadingBill> ladingBillMap= Maps.newHashMap();
		if(ladingBillIds.size()>0){
			ladingBillMap=this.ladingService.findByIds(ladingBillIds);
		}
		
		Map<Integer,PsiLadingBillItem> ladingBillItemMap= Maps.newHashMap();
		if(ladingBillItemIds.size()>0){
			ladingBillItemMap=this.ladingService.findItemsByIds(ladingBillItemIds);
		}
		
		BigDecimal totalAmount=new BigDecimal(0);
		for(PurchasePaymentItem item : purchasePayment.getItems()) {
			totalAmount=totalAmount.add(item.getPaymentAmount());
			if("0".equals(item.getPaymentType())){
				PurchaseOrder order = purchaseOrderDao.get(item.getOrder().getId());
				item.setOrder(order);
				order.setDepositPreAmount(order.getDepositPreAmount().add(item.getPaymentAmount()));
				if(!orderNo.contains(item.getBillNo()+",")){
					orderNo+=item.getBillNo()+",";
				}
			}else if("1".equals(item.getPaymentType())){
				PsiLadingBill bill=ladingBillMap.get(item.getLadingBill().getId());
				PsiLadingBillItem billItem = ladingBillItemMap.get(item.getLadingBillItem().getId());
				BigDecimal curPay= item.getPaymentAmount();
				//提单信息保存
				bill.setTotalPaymentPreAmount(bill.getTotalPaymentPreAmount().add(curPay));
				item.setLadingBill(bill);
				//提单item信息保存
				billItem.setTotalPaymentPreAmount(billItem.getTotalPaymentPreAmount().add(curPay));
				item.setLadingBillItem(billItem);
				String ladingOrderNoTemp=billItem.getPurchaseOrderItem().getPurchaseOrder().getOrderNo();
				if(!orderNo.contains(ladingOrderNoTemp+",")){
					orderNo+=ladingOrderNoTemp+",";
				}
			}
		}
		
		
		purchasePayment.setPaymentAmountTotal(totalAmount.floatValue());
		
		if(ladingBillItemMap.size()>0){
			for(Map.Entry<Integer, PsiLadingBill> entry:ladingBillMap.entrySet()){
				this.ladingBillDao.save(entry.getValue());
			}
			
			for(Map.Entry<Integer, PsiLadingBillItem> entry:ladingBillItemMap.entrySet()){
				this.ladingBillItemDao.save(entry.getValue());
			}
		}
		
		
		purchasePayment.setPaymentAmountTotal(new BigDecimal(purchasePayment.getPaymentAmountTotal()).add(adjustTotal).floatValue());
		purchasePayment.setApplyDate(new Date());
		purchasePayment.setApplyUser(UserUtils.getUser());
		Map<String, Object> prarms = Maps.newHashMap();
		prarms.put("payment",purchasePayment);
		prarms.put("orderNo",orderNo);
		List<User> userList = systemService.findUserByPermission("payment:operate:user");
		List<User> replys = Lists.newArrayList();
		if(userList!=null){
			replys.addAll(userList);
		}
		replys.add(UserUtils.getUser());
		String toAddress = Collections3.extractToString(replys,"email", ",");
		String content = PdfUtil.getPsiTemplate("applyPaymentEmail.ftl",prarms);
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			final MailInfo mailInfo = new MailInfo(toAddress,purchasePayment.getSupplier().getNikename()+"支付货款申请["+purchasePayment.getPaymentNo()+"]--长沙春雨"+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			new Thread(){
				public void run(){
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return true;
	}
	
	
	@Transactional(readOnly = false)
	public void sureSave(PurchasePayment purchasePayment) {
		//如果有额外付款
		if("1".equals(purchasePayment.getHasAdjust())){
			this.adjustDao.updateStaByPaymentId(purchasePayment.getId(), "2");
		}   
		Map<Integer ,List<String>>   orderMap= Maps.newHashMap();
		for(PurchasePaymentItem item:purchasePayment.getItems()){
			//区分是提单还是订单
			if(item.getPaymentType()!=null&&!"".equals(item.getPaymentType())){
				BigDecimal  curPay = item.getPaymentAmount();
				if("0".equals(item.getPaymentType())){
					//把订单        预付定金改为已付定金     预付变为0           
					PurchaseOrder order = item.getOrder();
					order.setDepositAmount(order.getDepositAmount().add(curPay));	  
					order.setDepositPreAmount(order.getDepositPreAmount().subtract(curPay));
					order.setPayItem(item);
					this.purchaseOrderDao.save(order);
				}else if("1".equals(item.getPaymentType())){
					PsiLadingBill bill = item.getLadingBill();
					bill.setTotalPaymentPreAmount(bill.getTotalPaymentPreAmount().subtract(curPay));
					bill.setTotalPaymentAmount(bill.getTotalPaymentAmount().add(curPay));
					this.ladingBillDao.save(bill);
					
					PsiLadingBillItem billItem = item.getLadingBillItem();
					billItem.setTotalPaymentPreAmount(billItem.getTotalPaymentPreAmount().subtract(curPay));
					billItem.setTotalPaymentAmount(billItem.getTotalPaymentAmount().add(curPay));
					this.ladingBillItemDao.save(billItem);
					
					PurchaseOrderItem orderItem =billItem.getPurchaseOrderItem();
					PurchaseOrder     order     = orderItem.getPurchaseOrder();
					Integer orderId = order.getId();
					String  productName=orderItem.getProductName()+":"+orderItem.getColorCode()+":"+orderItem.getCountryCode();
					Float ratioPay=(100-order.getDeposit())/100f;
					
					//算出提单item项的总值;   放入订单项的已付款属性里
//					Float totalAmount=billItem.getQuantityLading()*billItem.getItemPrice()*(ratioPay);
					orderItem.setPaymentAmount(orderItem.getPaymentAmount().add(curPay));
					//orderItem.setQuantityPayment(orderItem.getQuantityPayment()+billItem.getQuantityLading());//更新订单Item表支付数量
					int payQ =orderItem.getPaymentAmount().divide(orderItem.getItemPrice().multiply(new BigDecimal(ratioPay)),0, BigDecimal.ROUND_UP).intValue();
					orderItem.setQuantityPayment(payQ);
					
					this.purchaseOrderItemDao.save(orderItem);
					
					//判断是否是第一次付款，flag 货币类型
					if(order.getDepositAmount().intValue()==0&&order.getPaySta().equals("0")){
						order.setPaySta("1");
						order.setCurrencyType(purchasePayment.getSupplier().getCurrencyType());
					}
					//订单里面也维护已支付总额
					order.setPaymentAmount(order.getPaymentAmount().add(curPay));
					this.purchaseOrderDao.save(order);
					//判断订单Item项总值
				//	if(Float.floatToIntBits(orderItem.getPaymentAmount())==Float.floatToIntBits(orderItem.getItemPrice()*orderItem.getQuantityOrdered()*ratioPay)){
					BigDecimal shoudPay=new BigDecimal(orderItem.getItemPrice()+"").multiply(new BigDecimal(orderItem.getQuantityOrdered()).multiply(new BigDecimal(ratioPay+"")));
					if(shoudPay.subtract(new BigDecimal(orderItem.getPaymentAmount()+"")).abs().compareTo(new BigDecimal("0.1"))<=0){//一分钱以内的误差认为完成
						List<String> products = orderMap.get(orderId);
						if(products==null){
							products =Lists.newArrayList();
							orderMap.put(orderId, products);
						}
						products.add(productName);
						
					}
				}
			}
		}
		for (Map.Entry<Integer, List<String>> entry : orderMap.entrySet()) {
			Integer orderId = entry.getKey();
			boolean isFinal = true;
			for (PurchaseOrderItem item : this.purchaseOrderDao.get(orderId).getItems()) {
				String  productName=item.getProductName()+":"+item.getColorCode()+":"+item.getCountryCode();
				if(!entry.getValue().contains(productName)){
					Integer  orderNum  = item.getQuantityOrdered();
					Integer  recNum    = item.getQuantityReceived();
					if(!orderNum.equals(recNum)){ //订单数<>收到数，说明订单还没有收货完成
						isFinal = false;
						break;
					}
				}
				
			}
			//该订单所有项都付款完成，就更新order状态为已付款；
			if(isFinal){
				PurchaseOrder order = this.purchaseOrderDao.get(orderId);
				if("4".equals(order.getOrderSta())){
					order.setOrderSta("5");
				}
				this.purchaseOrderDao.save(order);
			}
		}
		purchasePayment.setSureDate(new Date());
		purchasePayment.setSureUser(UserUtils.getUser());
		purchasePayment.setPaymentSta("2");
		purchasePaymentDao.save(purchasePayment);
		try {
			this.sendToSupplier(purchasePayment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		purchasePaymentDao.deleteById(id);
	}
	
	
	/**
	 *根据itemId获取list信息 
	 */
	public List<PurchasePaymentItem> getPurchasePaymentItems(Set<Integer> ids){
		DetachedCriteria dc = this.purchasePaymentItemDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		return purchasePaymentItemDao.find(dc);
	}
	
	
	private void sendToSupplier(PurchasePayment purchasePayment) throws Exception{
		SendEmail  sendEmail = new SendEmail();
		PsiSupplier supplier = purchasePayment.getSupplier();
		Map<String,Object> params = Maps.newHashMap();
		String template = "";
		try {
			params.put("supplier", supplier);
			template = PdfUtil.getPsiTemplate("paySureEmail.ftl", params);
		} catch (Exception e) {}
		sendEmail.setSendContent(template);
		sendEmail.setSendEmail(supplier.getMail()); 
		sendEmail.setSendSubject("SpringRain支付货款"+"("+DateUtils.getDate()+")");
		sendEmail.setBccToEmail(UserUtils.getUser().getEmail());
		//发送邮件
		final MailInfo mailInfo = sendEmail.getMailInfo();
		String baseDir = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() ;
		File pdfFile = new File(baseDir,purchasePayment.getAttchmentPath());
		mailInfo.setFileName(pdfFile.getName());
		mailInfo.setFilePath(pdfFile.getAbsolutePath());
		new Thread(){
			@Override
			public void run() {
				mailManager.send(mailInfo);
			}
		}.start();
	}
	
	//查出付款部分支付的
	public Float getPartsPayment(Integer supplierId){
		return this.purchasePaymentDao.getPartsPayment(supplierId);
	}
	
	
	/**
	 * 付款升级后，把原来对提单付款，拆分成对提单item付款
	 *整理数据，把原来一个提单的付款信息，变为提单详细里的各个子项 
	 */
	@Transactional(readOnly = false)
	public void displitPayItem() {
		
		//1,查询所有的提单
		DetachedCriteria dc = purchasePaymentDao.createDetachedCriteria();
		//付款id<=744
		dc.add(Restrictions.le("id", 744));
//		dc.add(Restrictions.ne("paymentSta", "3"));
		List<PurchasePayment> list = this.purchasePaymentDao.find(dc);
		for(PurchasePayment pay:list){
			List<PurchasePaymentItem> addItems = Lists.newArrayList();
			for(PurchasePaymentItem item:pay.getItems()){
				if("1".equals(item.getPaymentType())){
					PsiLadingBill lading = item.getLadingBill();
					item.setDelFlag("1");
					BigDecimal payAmount = item.getPaymentAmount();
					BigDecimal noDepsitAmount = lading.getNoDepositAmount();
					for(PsiLadingBillItem  ladingItem:lading.getItems()){
						PurchasePaymentItem payItem = new PurchasePaymentItem();
						payItem.setLadingBill(lading);
						payItem.setDelFlag("0");
						payItem.setBillNo(lading.getBillNo());
						payItem.setLadingBillItem(ladingItem);
						payItem.setPaymentAmount(payAmount.multiply(ladingItem.getItemPrice()).multiply(new BigDecimal(ladingItem.getQuantityLading()).divide(noDepsitAmount, 2, BigDecimal.ROUND_HALF_UP)));
						payItem.setPaymentType("1");
						payItem.setPurchasePayment(pay);
						payItem.setRemark(lading.getRemark());
						addItems.add(payItem);//添加到付款里面
					}
				}
			}
			if(addItems.size()>0){
				pay.getItems().addAll(addItems);
				this.purchasePaymentDao.save(pay);
			}
		}
	}
	
	//sendemail
	public void sendNoticeEmail(String email,String content,String subject,String ccEmail,String bccEmail){
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			final MailInfo mailInfo = new MailInfo(email,subject+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			if(StringUtils.isNotEmpty(bccEmail)){
				mailInfo.setBccToAddress(bccEmail);
			}
			if(StringUtils.isNotEmpty(ccEmail)){
				mailInfo.setCcToAddress(ccEmail);
			}
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
	}
	
}
