/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.lc;

import java.io.File;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.custom.service.SendEmailService;
import com.springrain.erp.modules.psi.dao.PurchaseOrderDao;
import com.springrain.erp.modules.psi.dao.lc.LcPsiPartsInventoryDao;
import com.springrain.erp.modules.psi.dao.lc.LcPsiPartsOrderBasisDao;
import com.springrain.erp.modules.psi.dao.lc.LcPsiPartsOrderDao;
import com.springrain.erp.modules.psi.dao.lc.LcPsiPartsOrderItemDao;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsInventory;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsOrderBasis;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsOrderItem;
import com.springrain.erp.modules.psi.scheduler.PoEmailManager;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsService;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 产品配件Service
 * @author Michael
 * @version 2015-06-02
 */
@Component
@Transactional(readOnly = true)
public class LcPsiPartsOrderService extends BaseService {
	@Autowired
	private 	LcPsiPartsOrderDao 		 	psiPartsOrderDao;
	@Autowired
	private 	PsiPartsService 		 	psiPartsService;
	@Autowired
	private 	LcPsiPartsOrderItemDao 	 	psiPartsOrderItemDao;
	@Autowired
	private 	LcPsiPartsInventoryService 	psiPartsInventoryService;
	@Autowired
	private 	LcPsiPartsInventoryDao     	psiPartsInventoryDao;
	@Autowired
	private 	LcPsiPartsOrderBasisService 	psiPartsOrderBasisService;
	@Autowired 
	private     LcPsiPartsOrderBasisDao       partsOrderBasisDao;
	@Autowired
	private 	PurchaseOrderDao            purchaseOrderDao;
	@Autowired  
	private 	GenerateSequenceDao      	genSequenceDao;
	@Autowired
	private     PsiSupplierService          psiSupplierService;
	@Autowired
	private 	PoEmailManager        		poMaillManager;  
	@Autowired
	private 	SendEmailService      		sendEmailService;
	   
	public LcPsiPartsOrder get(Integer orderId){
		return this.psiPartsOrderDao.get(orderId);
	}  
	
	public Page<LcPsiPartsOrder> find(Page<LcPsiPartsOrder> page, LcPsiPartsOrder psiPartsOrder) {
		DetachedCriteria dc = psiPartsOrderDao.createDetachedCriteria();
		
		if (psiPartsOrder.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",psiPartsOrder.getCreateDate()));
		}
		
		if (psiPartsOrder.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(psiPartsOrder.getUpdateDate(),1)));
		}
		
		if(psiPartsOrder.getSupplier()!=null&&psiPartsOrder.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier.id", psiPartsOrder.getSupplier().getId()));
		}
		
		if(StringUtils.isNotEmpty(psiPartsOrder.getOrderSta())){
			dc.add(Restrictions.eq("orderSta", psiPartsOrder.getOrderSta()));
		}else{
			dc.add(Restrictions.ne("orderSta", "8"));
		}
		
		if(StringUtils.isNotEmpty(psiPartsOrder.getPartsOrderNo())){
			dc.add(Restrictions.or(Restrictions.like("partsOrderNo", "%"+psiPartsOrder.getPartsOrderNo()+"%"),Restrictions.like("purchaseOrderNo", "%"+psiPartsOrder.getPartsOrderNo()+"%")));
		}
		
		if(StringUtils.isNotEmpty(psiPartsOrder.getPurchaseOrderNo())){
			dc.createAlias("this.items", "item");  
			dc.add(Restrictions.like("item.partsName", "%"+psiPartsOrder.getPurchaseOrderNo()+"%"));
		}
		
		//dc.addOrder(Order.desc("id"));
		page.setOrderBy(" id desc");
		return psiPartsOrderDao.find2(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public void addSave(LcPsiPartsOrder psiPartsOrder, String nikeName) throws Exception {
		if(psiPartsOrder.getId()==null){
			//生成配件号
			String purchaseOrderNo=this.genSequenceDao.genSequence("ZJ_"+nikeName+"_LC",2);
			Integer quantity=0;
			for(LcPsiPartsOrderItem item:psiPartsOrder.getItems()){
				item.setPsiParts(this.psiPartsService.get(item.getPsiParts().getId()));//为了邮件里面有值
				item.setPartsOrder(psiPartsOrder);
				item.setQuantityReceived(0);
				item.setQuantityPreReceived(0);
				item.setQuantityPayment(0);          //已支付数量
				item.setPaymentAmount(0f);           //已支付金额
				quantity+=item.getQuantityOrdered();
			}
			
			psiPartsOrder.setPaymentAmount(0f);
			psiPartsOrder.setPrePaymentAmount(0f);
			psiPartsOrder.setDepositAmount(0f);
			psiPartsOrder.setDepositPreAmount(0f);
			psiPartsOrder.setPaymentSta("0");
			psiPartsOrder.setOrderSta("0");
			
			psiPartsOrder.setUpdateDate(new Date());
			psiPartsOrder.setUpdateUser(UserUtils.getUser());
			psiPartsOrder.setPartsOrderNo(purchaseOrderNo);
			psiPartsOrder.setCreateUser(UserUtils.getUser());
			psiPartsOrder.setCreateDate(new Date());
			sendEmailToSupplier(psiPartsOrder);
			psiPartsOrderDao.save(psiPartsOrder);
			
		}
		
	}
	
	@Transactional(readOnly = false)
	public void editSave(LcPsiPartsOrder psiPartsOrder) {
		if(psiPartsOrder.getId()!=null){
			Set<Integer>  delItemSet = Sets.newHashSet();
			Set<String> setNewIds = new HashSet<String>();
			String oldItemIds=psiPartsOrder.getOldItemIds();
			String [] oldIds = oldItemIds.split(",");
			Integer balanceQuantity=0;
			Integer orderQuantity=0;
			Float   totalAmount =0f;
			for(LcPsiPartsOrderItem item:psiPartsOrder.getItems()){
				item.setPartsOrder(psiPartsOrder);
				if(item.getId()!=null){
					setNewIds.add(item.getId().toString());
				}else{
					//如果id为空     说明是新增的
					item.setQuantityReceived(0);         //已接收数量0
					item.setQuantityPreReceived(0);
					item.setDelFlag("0");
				}
				balanceQuantity+=item.getQuantityOrdered()-item.getQuantityReceived();
				orderQuantity+=item.getQuantityOrdered();
				Float itemPrice=this.psiPartsService.getPartsPrice(item.getPsiParts().getId(), psiPartsOrder.getCurrencyType());
				if(itemPrice!=null){
					item.setItemPrice(itemPrice);
					totalAmount+=itemPrice*item.getQuantityOrdered();
				}
				item.setQuantityPayment(0);          //已支付数量
				item.setPaymentAmount(0f);           //已支付金额
			}
			
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
			
			
			if(delItemSet.size()>0){
				for(LcPsiPartsOrderItem item:this.getPartsOrderItems(delItemSet)){
					item.setDelFlag("1");
					item.setPartsOrder(psiPartsOrder);
					psiPartsOrder.getItems().add(item);
				};
			}
			
			psiPartsOrder.setTotalAmount(totalAmount);
			psiPartsOrder.setUpdateDate(new Date());
			psiPartsOrder.setUpdateUser(UserUtils.getUser());
			psiPartsOrderDao.getSession().merge(psiPartsOrder);
		}
		
	}
	
	
	@Transactional(readOnly = false)
	public void sureSave(LcPsiPartsOrder psiPartsOrder) {
		if(psiPartsOrder.getId()!=null){
			Float   totalAmount =0f;
			for(LcPsiPartsOrderItem item:psiPartsOrder.getItems()){
				item.setPartsOrder(psiPartsOrder);
				totalAmount+=item.getItemPrice()*item.getQuantityOrdered();
			}
			psiPartsOrder.setTotalAmount(totalAmount);
			psiPartsOrder.setOrderSta("1");
			psiPartsOrder.setSureDate(new Date());
			psiPartsOrder.setSureUser(UserUtils.getUser());
			psiPartsOrderDao.getSession().merge(psiPartsOrder);
			if(StringUtils.isEmpty(psiPartsOrder.getPurchaseOrderNo())){
				this.changePoInventory(psiPartsOrder);
			}
			
		}
		
	}
	
	//如果配件订单是自己建的，要更新po非的库存
	public void changePoInventory(LcPsiPartsOrder psiPartsOrder) {
		for(LcPsiPartsOrderItem item:psiPartsOrder.getItems()){
			LcPsiPartsInventory partsInventory= psiPartsInventoryService.getPsiPartsInventorys(item.getPsiParts().getId());
			if(partsInventory!=null){
				partsInventory.setPoNotFrozen(partsInventory.getPoNotFrozen()+item.getQuantityOrdered());
			}else{
				partsInventory=new LcPsiPartsInventory();
				partsInventory.setPartsId(item.getPsiParts().getId());
				partsInventory.setPartsName(item.getPartsName());
				partsInventory.setPoFrozen(0);
				partsInventory.setPoNotFrozen(item.getQuantityOrdered());
				partsInventory.setStockFrozen(0);
				partsInventory.setStockNotFrozen(0);
			}
			//添加库存日志
			this.psiPartsInventoryService.saveLog(item.getPsiParts().getId(), item.getPartsName(), item.getQuantityOrdered(),"poNotFrozen", "", UserUtils.getUser(), new Date(), "Purchase Self", item.getRemark());
			//保存库存
			this.psiPartsInventoryDao.save(partsInventory);
		}
	}
	
	//更新po库存
	@Transactional(readOnly = false)
	public void productReceive(LcPsiPartsOrder psiPartsOrder) {
		for(LcPsiPartsOrderItem item:psiPartsOrder.getItems()){
			LcPsiPartsInventory partsInventory= psiPartsInventoryService.getPsiPartsInventorys(item.getPsiParts().getId());
			partsInventory.setStockFrozen(partsInventory.getStockFrozen()-item.getQuantityOrdered());
			this.psiPartsInventoryDao.save(partsInventory);
		}
		psiPartsOrder.setIsProductReceive("1");
		this.psiPartsOrderDao.save(psiPartsOrder);
	}
	
	
	@Transactional(readOnly = false)
	public void save(LcPsiPartsOrder psiPartsOrder) {
		psiPartsOrderDao.save(psiPartsOrder);
	}
	
	
	
	@Transactional(readOnly = false)
	public void cancel(LcPsiPartsOrder psiPartsOrder) {
		if(StringUtils.isEmpty(psiPartsOrder.getPurchaseOrderNo())&&psiPartsOrder.getPurchaseOrderId()==null){
			//如果是申请状态取消   更新库存 （页面没放开）
			if("1".equals(psiPartsOrder.getOrderSta())){
				for (LcPsiPartsOrderItem item : psiPartsOrder.getItems()) {
					Integer partsId=item.getPsiParts().getId();
					LcPsiPartsInventory inventory=psiPartsInventoryDao.get(partsId);
					inventory.setPoNotFrozen(inventory.getPoNotFrozen()-item.getQuantityOrdered());
					if(inventory.getPoNotFrozen()<0){
						throw new RuntimeException(item.getPartsName()+"可用PO数为："+inventory.getPoNotFrozen()+";要转化数量："+item.getQuantityOrdered()+",此订单不能被取消，操作已取消");
					}
					this.psiPartsInventoryService.saveLog(partsId,item.getPartsName(), -item.getQuantityOrdered(),"poNotFrozen", "", UserUtils.getUser(), new Date(), "self parts order cancel", "");
					this.psiPartsInventoryDao.save(inventory);
				}
			}
		}else{  
			//不单个配件订单取消，一次取消涉及这个产品订单的所有配件信息都取消
			Integer purchaseOrderId = psiPartsOrder.getPurchaseOrderId();
			List<LcPsiPartsOrderBasis> basisList=this.psiPartsOrderBasisService.findByPurchaseOrdeIdAndPartsId(purchaseOrderId,"0");
			if(basisList!=null&&basisList.size()>0){
				for(LcPsiPartsOrderBasis basis:basisList){
					Integer partsId=basis.getPartsId();
					String partsName = basis.getPartsName();
					LcPsiPartsInventory inventory=psiPartsInventoryService.getPsiPartsInventorys(partsId);
					inventory.setPoFrozen(inventory.getPoFrozen()-basis.getAfterPoFrozen());
					inventory.setPoNotFrozen(inventory.getPoNotFrozen()-basis.getAfterPoNotFrozen());
					inventory.setStockFrozen(inventory.getStockFrozen()-basis.getAfterStockFrozen());
					inventory.setStockNotFrozen(inventory.getStockNotFrozen()-basis.getAfterStockNotFrozen());
					//添加库存日志
					if(!basis.getAfterPoNotFrozen().equals(0)){
						this.psiPartsInventoryService.saveLog(partsId,partsName, -basis.getAfterPoNotFrozen(),"poNotFrozen", "", UserUtils.getUser(), new Date(), "parts order cancel", "");
					}
					if(!basis.getAfterPoFrozen().equals(0)){
						this.psiPartsInventoryService.saveLog(partsId, partsName, -basis.getAfterPoFrozen(),"poFrozen", "", UserUtils.getUser(), new Date(), "parts order cancel", "");
					}
					if(!basis.getAfterStockFrozen().equals(0)){
						this.psiPartsInventoryService.saveLog(partsId, partsName, -basis.getAfterStockNotFrozen(),"stockNotFrozen", "", UserUtils.getUser(), new Date(), "parts order cancel", "");
						this.psiPartsInventoryService.saveLog(partsId, partsName, -basis.getAfterStockFrozen(),"stockFrozen", "", UserUtils.getUser(), new Date(), "parts order cancel", "");
					}
					this.psiPartsInventoryDao.save(inventory);
					//如果库存有小于0的情况，不能取消
					if(inventory.getPoFrozen()<0||inventory.getPoNotFrozen()<0||inventory.getStockFrozen()<0||inventory.getStockNotFrozen()<0){
						throw new RuntimeException("取消后，po冻结、po可用、库存可用、库存冻结，为负数，不能取消，操作已取消");
					}
					basis.setCancelSta("1");//变为已取消状态
					this.partsOrderBasisDao.save(basis);
				}
			}
			//更新产品订单为未下单状态
			this.purchaseOrderDao.updateOrderPartsOrderSta(purchaseOrderId, "0");
			//取消所有订单
			this.psiPartsOrderDao.updateCancelStaByOrderID(purchaseOrderId, "8");
		}
		psiPartsOrder.setCancelDate(new Date());
		psiPartsOrder.setCancelUser(UserUtils.getUser());
		psiPartsOrder.setOrderSta("8");
		psiPartsOrderDao.save(psiPartsOrder);
	}
	
	
	
	
	/**
	 *更新发送状态为已发送 
	 * 
	 */
	@Transactional(readOnly = false)
	public void updateSendSta(Integer partsId,String sendSta){
		String sql ="UPDATE lc_psi_parts_order AS a SET a.`send_eamil`=:p2 WHERE a.`id`=:p1";
		this.psiPartsOrderDao.updateBySql(sql, new Parameter(partsId,sendSta));
	}
	
	
	/**
	 *根据itemId获取list信息 
	 */
	public List<LcPsiPartsOrderItem> getPartsOrderItems(Set<Integer> ids){
		DetachedCriteria dc = this.psiPartsOrderItemDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		return psiPartsOrderItemDao.find(dc);
	}
	
	
	
	
	/***
	 *查询是否有
	 * 
	 */
	public boolean  getUnReceivedPartsOrder(Set<Integer> orderIdSet){
		String sql="SELECT b.purchase_order_id FROM lc_psi_parts_order_item AS a,lc_psi_parts_order AS b WHERE  a.parts_order_id=b.id AND a.del_flag='0' AND b.`order_sta`<>'8' AND b.purchase_order_id IN :p1 GROUP BY b.purchase_order_id LIMIT 1";
		List<Object[]> lists=this.psiPartsOrderDao.findBySql(sql, new Parameter(orderIdSet));
		if(lists.size()>0){
			return true;
		}
		return false;
	}
	
	
	/***
	 *查询未收货的配件产品数量
	 * 
	 */
	public Map<String,Integer>  getUnReceivedQuantity(Set<Integer> orderIdSet){
		Map<String,Integer> orderMap = Maps.newHashMap();
		String sql="SELECT b.purchase_order_id,a.product_id, a.quantity_ordered-a.quantity_received FROM lc_psi_parts_order_item AS a,lc_psi_parts_order AS b WHERE  a.parts_order_id=b.id AND a.del_flag='0' AND b.`order_sta`<>'8' AND b.purchase_order_id IN :p1 GROUP BY b.purchase_order_id,a.product_id";
		List<Object[]> lists=this.psiPartsOrderDao.findBySql(sql, new Parameter(orderIdSet));
		if(lists.size()>0){
			for(Object[] obs:lists){
				orderMap.put(obs[0].toString()+","+obs[1].toString(), ((BigInteger)obs[2]).intValue());
			}
		}
		return orderMap;
	}
	
	
	
	/**
	 *更新支付金额
	 * 
	 */
	public  void  updatePaymentAmountAndSta(Integer partsOrderId,Float preAmount){
		String sql ="UPDATE lc_psi_parts_order AS a SET a.`payment_amount`=(a.`payment_amount`+:p2), a.`pre_payment_amount`=(a.`pre_payment_amount`-:p2),CASE WHEN a.`total_amount`=(a.`payment_amount`+10080.00) THEN a.`payment_sta`='2'  ELSE a.`payment_sta`='1' END  WHERE a.`id`=:p1 ";
		this.psiPartsOrderDao.updateBySql(sql,new Parameter(partsOrderId,preAmount));
	}
	
	
	
	/***
	 * 跟供应商发送邮件
	 */
	public void sendEmailToSupplier(LcPsiPartsOrder psiPartsOrder) throws Exception{
		psiPartsOrder.setSendEamil("1");
		SendEmail  sendEmail = new SendEmail();
		//往采购供应商发信 获取供应商模板
		PsiSupplier supplier = psiSupplierService.get(psiPartsOrder.getSupplier().getId());
		psiPartsOrder.setSupplier(supplier);
		String orderNo=psiPartsOrder.getPartsOrderNo();
		Map<String,Object> params = Maps.newHashMap();
		String template = "";
		try {
			params.put("supplier", supplier);
			params.put("cuser", UserUtils.getUser());
			template = PdfUtil.getPsiTemplate("partsOrderEmail.ftl", params);
		} catch (Exception e) {}
		sendEmail.setSendContent(template);
		sendEmail.setSendEmail(supplier.getMail());    
		sendEmail.setSendSubject("新配件订单PN"+orderNo+"("+com.springrain.erp.common.utils.DateUtils.getDate()+")");
		sendEmail.setCreateBy(UserUtils.getUser());
		//向供应商发送邮件 加入附件和抄送人
		String address =  "sophie@inateck.com,bella@inateck.com,emma.chao@inateck.com,"+UserUtils.getUser().getEmail();  
		sendEmail.setBccToEmail(address);
		//发送邮件
		final MailInfo mailInfo = sendEmail.getMailInfo();
		String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiParts";
		String uuid = UUID.randomUUID().toString();
		File baseDir = new File(baseDirStr+"/"+uuid); 
		if(!baseDir.isDirectory())
			baseDir.mkdirs();
		File pdfFile = new File(baseDir,orderNo + ".pdf");
		PdfUtil.genPartsOrderPdf(pdfFile, psiPartsOrder);
		mailInfo.setFileName(orderNo + ".pdf");
		mailInfo.setFilePath(pdfFile.getAbsolutePath());
		sendEmail.setSendAttchmentPath(uuid+"/"+orderNo + ".pdf");
		new Thread(){
			@Override
			public void run() {
				poMaillManager.send(mailInfo);
			}
		}.start();
		
		sendEmail.setSentDate(new Date());
		sendEmail.setSendFlag("1");
		sendEmailService.save(sendEmail);
		
	}
	
}
