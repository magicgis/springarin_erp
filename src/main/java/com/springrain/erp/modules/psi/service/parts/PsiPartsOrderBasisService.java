/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.parts;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;

import com.google.common.collect.Lists;
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
import com.springrain.erp.modules.psi.dao.parts.PsiPartsInventoryDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsOrderBasisDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsOrderDao;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.parts.PsiParts;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventory;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrder;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrderBasis;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrderBasisTotal;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrderItem;
import com.springrain.erp.modules.psi.scheduler.PoEmailManager;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 配件订单付款详情Service
 * @author Michael
 * @version 2015-06-29
 */
@Component
@Transactional(readOnly = true)
public class PsiPartsOrderBasisService extends BaseService {
	
	@Autowired
	private 	PsiPartsOrderBasisDao 		psiPartsOrderBasisDao;
	@Autowired
	private 	PsiPartsInventoryService 	psiPartsInventoryService;
	@Autowired
	private 	PsiPartsInventoryDao 		psiPartsInventoryDao;
	@Autowired
	private 	PsiPartsService 			psiPartsService;
	@Autowired
	private 	PsiPartsOrderDao 			partsOrderDao;
	@Autowired
	private 	PurchaseOrderDao 			purchaseOrderDao;
	@Autowired
	private 	PsiSupplierService 			psiSupplierService;
	@Autowired
	private 	PoEmailManager        		poMaillManager;  
	@Autowired
	private 	SendEmailService      		sendEmailService;
	
	
	
	public PsiPartsOrderBasis get(Integer id) {
		return psiPartsOrderBasisDao.get(id);
	}  
	
	public Page<PsiPartsOrderBasis> find(Page<PsiPartsOrderBasis> page, PsiPartsOrderBasis psiPartsOrderBasis) {
		DetachedCriteria dc = psiPartsOrderBasisDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiPartsOrderBasis.getPartsName())){
			dc.add(Restrictions.or(Restrictions.like("partsName", "%"+psiPartsOrderBasis.getPartsName()+"%"),Restrictions.like("purchaseOrderNo", "%"+psiPartsOrderBasis.getPartsName()+"%")));
		}
		return psiPartsOrderBasisDao.find(page, dc);
	}
	
	
	  
	@Transactional(readOnly = false)
	public String save(PsiPartsOrderBasisTotal psiPartsOrderBasisTotal) throws Exception {
		Map<Integer,List<PsiPartsOrderBasis>> supplierMap = Maps.newHashMap();
		Integer orderId=0;
		Set<Integer> partsIdSet=Sets.newHashSet();
		for(PsiPartsOrderBasis orderBasis:psiPartsOrderBasisTotal.getItems()){
			Integer partsId    =orderBasis.getPartsId();
			String  partsName  =orderBasis.getPartsName();
			Integer supplierId =orderBasis.getSupplierId();
			partsIdSet.add(partsId);
			List<PsiPartsOrderBasis>  orderBasisList =Lists.newArrayList();
			if(orderBasis.getOrderQuantity()==null){
				orderBasis.setOrderQuantity(0);
			}
			if(orderBasis.getPoFrozen()==null){
				orderBasis.setPoFrozen(0);
			}
			if(orderBasis.getStockFrozen()==null){
				orderBasis.setStockFrozen(0);
			}
			
			if(orderId.equals(0)){
				orderId=orderBasis.getPurchaseOrderId();
			}
			//需要下订单信息
			if(orderBasis.getOrderQuantity()>0){
				if(supplierMap.get(supplierId)!=null){
					orderBasisList = supplierMap.get(supplierId);
				}
				orderBasisList.add(orderBasis);
				supplierMap.put(supplierId, orderBasisList);
			}
			
			//更新库存信息
			PsiPartsInventory partsInventory=this.psiPartsInventoryService.getPsiPartsInventorys(partsId);
			if(partsInventory==null){
				partsInventory=new PsiPartsInventory();
				partsInventory.setPartsId(partsId);
				partsInventory.setPartsName(partsName);
				partsInventory.setPoFrozen(0);
				partsInventory.setPoNotFrozen(0);
				partsInventory.setStockFrozen(0);
				partsInventory.setStockNotFrozen(0);
			}
			//下单数一部分转化成po非，一部分转化成po：need-toStockFrozen-toPoFrozen为要转化成po的数量
			Integer changeQuantity =orderBasis.getNeedQuantity()-orderBasis.getStockFrozen()-orderBasis.getPoFrozen();
			//po非：订单数-其他使用的-po非转移
			Integer poNotChange =orderBasis.getOrderQuantity()-changeQuantity-orderBasis.getPoFrozen();
			partsInventory.setPoNotFrozen(partsInventory.getPoNotFrozen()+poNotChange);
			//po冻结数=需要数-库存冻结数
			Integer poChange =changeQuantity+orderBasis.getPoFrozen();
			partsInventory.setPoFrozen(partsInventory.getPoFrozen()+poChange);
			//最新库存未冻结数量
			partsInventory.setStockNotFrozen(partsInventory.getStockNotFrozen()-orderBasis.getStockFrozen());
			//最新库存冻结数量       库存冻结数不变
			partsInventory.setStockFrozen(partsInventory.getStockFrozen()+orderBasis.getStockFrozen());
			
			
			//保存整理后的数据
			orderBasis.setAfterPoFrozen(poChange);
			orderBasis.setAfterPoNotFrozen(poNotChange);
			orderBasis.setAfterStockFrozen(orderBasis.getStockFrozen());
			orderBasis.setAfterStockNotFrozen(-orderBasis.getStockFrozen());
			if(partsInventory.getStockNotFrozen()<0||partsInventory.getPoNotFrozen()<0){
				throw new RuntimeException("库存未冻结或者PO未冻结数，本次处理后为负数，同一时刻或许有其他人在操作，请重新操作，本次操作已取消！");
			}
			//添加日志
			if(!poNotChange.equals(0)){
				this.psiPartsInventoryService.saveLog(partsId, partsInventory.getPartsName(), poNotChange,"poNotFrozen", "", UserUtils.getUser(), new Date(), "Purchase By Order", "");
			}
			if(!poChange.equals(0)){
				this.psiPartsInventoryService.saveLog(partsId, partsInventory.getPartsName(), poChange,"poFrozen", "", UserUtils.getUser(), new Date(), "Purchase By Order", "");
			}
			if(!orderBasis.getStockFrozen().equals(0)){
				this.psiPartsInventoryService.saveLog(partsId, partsInventory.getPartsName(), 0-orderBasis.getStockFrozen(),"stockNotFrozen", "", UserUtils.getUser(), new Date(), "Purchase By Order", "");
				this.psiPartsInventoryService.saveLog(partsId, partsInventory.getPartsName(), orderBasis.getStockFrozen(),"stockFrozen", "", UserUtils.getUser(), new Date(), "Purchase By Order", "");
			}
			
			orderBasis.setCancelSta("0");
			psiPartsInventoryDao.save(partsInventory);
			psiPartsOrderBasisDao.save(orderBasis);
		}
		
		//更新采购订单状态
		this.purchaseOrderDao.updateOrderPartsOrderSta(orderId, "1");
		
		if(supplierMap.size()>0){
			return this.exeOrderInfo(supplierMap,partsIdSet,psiPartsOrderBasisTotal.getPurchaseDate());
		}else{
			return "1";
		}
		
		
	}
		
		
		private String exeOrderInfo(Map<Integer,List<PsiPartsOrderBasis>> supplierMap,Set<Integer> partsIdSet,Date purchaseDate) throws Exception{
			//查出配件信息map
			Map<Integer,PsiParts> partsMap=psiPartsService.getPartsByIds(partsIdSet);
			Integer purchaseOrderId=0;
			String  purchaseOrderNo ="";
			StringBuffer sb = new StringBuffer("");
			for(Map.Entry<Integer,List<PsiPartsOrderBasis>> entry:supplierMap.entrySet()){
				PsiSupplier    supplier =null;
				PsiPartsOrder  partsOrder = new PsiPartsOrder();
				List<PsiPartsOrderItem> items =Lists.newArrayList();
				String supplierName="";
				Float totalAmount = 0f;
				for(PsiPartsOrderBasis basis:entry.getValue()){
					PsiParts parts=partsMap.get(basis.getPartsId());
					Integer orderQuantity =basis.getOrderQuantity();
					if(StringUtils.isEmpty(supplierName)){
						supplierName=parts.getSupplier().getNikename();
						supplier =parts.getSupplier();   
					}
					PsiPartsOrderItem partsOrderItem = new PsiPartsOrderItem();
					partsOrderItem.setPsiParts(this.psiPartsService.get(parts.getId()));
					partsOrderItem.setPartsName(parts.getPartsName());
					partsOrderItem.setQuantityOrdered(orderQuantity);
					partsOrderItem.setQuantityReceived(0);
					partsOrderItem.setQuantityPreReceived(0);
					partsOrderItem.setQuantityPayment(0);
					partsOrderItem.setPaymentAmount(0f);
					partsOrderItem.setPartsOrder(partsOrder);
					partsOrderItem.setRemark(basis.getRemark());    
					partsOrderItem.setDeliveryDate(basis.getDeliveryDate());
					items.add(partsOrderItem);
					if(StringUtils.isEmpty(purchaseOrderNo)){
						purchaseOrderNo=basis.getPurchaseOrderNo();
						purchaseOrderId=basis.getPurchaseOrderId();
					}
				}
				String preOrderNo=purchaseOrderNo.substring(0,8);
				String afterOrderNo=purchaseOrderNo.substring(11);
				//配件订单号
				String orderNo =preOrderNo+"PJ_"+supplierName+afterOrderNo;
				sb.append(orderNo).append(",");
				partsOrder.setPartsOrderNo(orderNo);
				partsOrder.setCurrencyType(supplier.getCurrencyType());
				partsOrder.setDeposit(supplier.getDeposit());
				partsOrder.setItems(items);
				partsOrder.setPaymentAmount(0f);
				partsOrder.setPrePaymentAmount(0f);
				partsOrder.setDepositAmount(0f);
				partsOrder.setDepositPreAmount(0f);
				partsOrder.setPaymentSta("0");
				partsOrder.setOrderSta("0");
				partsOrder.setSupplier(supplier);
				partsOrder.setPurchaseOrderId(purchaseOrderId);
				partsOrder.setPurchaseOrderNo(purchaseOrderNo);
				partsOrder.setTotalAmount(totalAmount);
				partsOrder.setCreateDate(new Date());
				partsOrder.setCreateUser(UserUtils.getUser());
				partsOrder.setUpdateDate(new Date());
				partsOrder.setUpdateUser(UserUtils.getUser());
				partsOrder.setPurchaseDate(purchaseDate);
//				this.sendEmailToSupplier(partsOrder);
				this.partsOrderDao.save(partsOrder);
			}
			
			return sb.toString().substring(0, sb.length()-1);
		}
		
		
	/**
	 *查询该采购订单是否有配件单生成 
	 */
	public boolean isExistPurchaseOrder(Integer orderId){
		String sql ="SELECT id FROM psi_parts_order_basis AS a WHERE a.`purchase_order_id`=:p1 AND a.`cancel_sta`='0'  LIMIT 1";
		List<Integer> list=this.psiPartsOrderBasisDao.findBySql(sql,new Parameter(orderId));
		if(list!=null&&list.size()>0){
			return true;
		}else{
			return false;
		}
	}	
		
	
	public List<PsiPartsOrderBasis> findByPurchaseOrdeIdAndPartsId(Integer purchaseOrderId,String cancelSta) {
		DetachedCriteria dc = psiPartsOrderBasisDao.createDetachedCriteria();
		dc.add(Restrictions.eq("purchaseOrderId", purchaseOrderId));
		dc.add(Restrictions.eq("cancelSta", cancelSta));
		return  psiPartsOrderBasisDao.find(dc);
	}
		
		
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiPartsOrderBasisDao.deleteById(id);
	}
	
	
	/***
	 * 跟供应商发送邮件
	 */
	public void sendEmailToSupplier(PsiPartsOrder psiPartsOrder) throws Exception{
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
		String address =  "frank@inateck.com,sophie@inateck.com,bella@inateck.com,emma.chao@inateck.com,"+UserUtils.getUser().getEmail();  
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
