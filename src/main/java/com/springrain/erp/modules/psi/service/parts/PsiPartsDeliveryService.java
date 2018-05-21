/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.parts;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.custom.service.SendEmailService;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsDeliveryDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsInventoryDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsOrderDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsOrderItemDao;
import com.springrain.erp.modules.psi.entity.PsiLadingBill;
import com.springrain.erp.modules.psi.entity.PsiLadingBillItem;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsDelivery;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsDeliveryDto;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsDeliveryItem;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventory;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrder;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrderItem;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 配件收货付款详情Service
 * @author Michael
 * @version 2015-07-03
 */
@Component
@Transactional(readOnly = true)
public class PsiPartsDeliveryService extends BaseService {
	@Autowired
	private PsiPartsDeliveryDao  		psiPartsDeliveryDao;
	@Autowired
	private PsiPartsOrderItemDao 		psiPartsOrderItemDao;
	@Autowired
	private PsiPartsOrderDao 	 		psiPartsOrderDao;
	@Autowired
	private PsiPartsInventoryService    psiPartsInventoryService;
	@Autowired
	private PsiPartsInventoryDao        psiPartsInventoryDao;
	@Autowired
	private GenerateSequenceDao         genSequenceDao;
	@Autowired
	private PsiSupplierService          supplierService;
	@Autowired
	private MailManager				    mailManager;
	@Autowired
	private SendEmailService            sendEmailService;
	
	  
	public PsiPartsDelivery get(Integer id) {
		return psiPartsDeliveryDao.get(id);
	}
	
	public Page<PsiPartsDelivery> find(Page<PsiPartsDelivery> page, PsiPartsDelivery psiPartsDelivery) {
		DetachedCriteria dc = psiPartsDeliveryDao.createDetachedCriteria();
		
		if (psiPartsDelivery.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",psiPartsDelivery.getCreateDate()));
		}
		
		if (psiPartsDelivery.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(psiPartsDelivery.getUpdateDate(),1)));
		}
		
		if(psiPartsDelivery.getSupplier()!=null&&psiPartsDelivery.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier.id", psiPartsDelivery.getSupplier().getId()));
		}
		
		if(StringUtils.isNotEmpty(psiPartsDelivery.getBillSta())){
			dc.add(Restrictions.eq("billSta", psiPartsDelivery.getBillSta()));
		}else{
			dc.add(Restrictions.ne("billSta", "2"));
		}
		
		if(StringUtils.isNotEmpty(psiPartsDelivery.getBillNo())){
			dc.add(Restrictions.like("billNo", "%"+psiPartsDelivery.getBillNo()+"%"));
		}
		
		if(StringUtils.isNotEmpty(psiPartsDelivery.getRemark())){
			dc.createAlias("this.items", "item");  
			dc.add(Restrictions.like("item.partsName", "%"+psiPartsDelivery.getRemark()+"%"));
		}
		
		//dc.addOrder(Order.desc("id"));
		page.setOrderBy(" id desc");
		return psiPartsDeliveryDao.find2(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiPartsDelivery psiPartsDelivery) {
		psiPartsDeliveryDao.save(psiPartsDelivery);
	}
	
	
	@Transactional(readOnly = false)
	public void batchSave(PsiPartsDelivery psiPartsDelivery) {
		
		Integer total = 0;
		Float   totalAmount =0f ;
		
		for (Iterator<PsiPartsDeliveryItem> iterator = psiPartsDelivery.getItems().iterator(); iterator.hasNext();) {
			PsiPartsDeliveryItem item = (PsiPartsDeliveryItem) iterator.next();
			if(item.getQuantityLading()==null||item.getQuantityLading()==0){
				iterator.remove();
			}else{
				item.setPartsDelivery(psiPartsDelivery);
				PsiPartsOrderItem orderItem = psiPartsOrderItemDao.get(item.getPartsOrderItem().getId());
				if(item.getQuantityLading()!=null){
					total+=item.getQuantityLading();
					//把提单数量    关联到    订单item里的      预收货数量
					Integer  curPreReceived =orderItem.getQuantityPreReceived()+item.getQuantityLading();
					orderItem.setQuantityPreReceived(curPreReceived);
					orderItem.setActualDeliveryDate(psiPartsDelivery.getDeliveryDate());
					this.psiPartsOrderItemDao.save(orderItem);
				}
				Float  ratio =(100-orderItem.getPartsOrder().getDeposit())/100f;
				//算出总金额   乘上支付比例
				totalAmount+=item.getQuantityLading()*item.getItemPrice()*ratio;
			}
		}
		psiPartsDelivery.setCreateDate(new Date());
		psiPartsDelivery.setCreateUser(UserUtils.getUser());
		psiPartsDelivery.setUpdateDate(new Date());
		psiPartsDelivery.setUpdateUser(UserUtils.getUser());
		psiPartsDelivery.setBillSta("0");
		psiPartsDelivery.setTotalAmount(totalAmount);       //总金额
		psiPartsDelivery.setTotalPaymentAmount(0f);         //已付款金额
		psiPartsDelivery.setTotalPaymentPreAmount(0f);      //已申请付款金额
		psiPartsDeliveryDao.save(psiPartsDelivery);       
		
		//发信给收货商
		try {
			sendEmailToVendor(psiPartsDelivery);
		} catch (Exception e) {
		}   
	}
	
	private void sendEmailToVendor(PsiPartsDelivery deliveryBill) throws Exception{
		SendEmail  sendEmail = new SendEmail();
		String toEmail = "lucas@inateck.com";
		String billNo =deliveryBill.getBillNo();
		
		if(deliveryBill.getTranSupplier()!=null&&deliveryBill.getTranSupplier().getId()!=null){
			deliveryBill.setTranSupplier(this.supplierService.get(deliveryBill.getTranSupplier().getId()));
			toEmail=deliveryBill.getTranSupplier().getMail();
		}else{
			deliveryBill.setTranSupplier(null);
		}
		
		deliveryBill.setSupplier(this.supplierService.get(deliveryBill.getSupplier().getId()));
		//往采购供应商发信 获取供应商模板
		Map<String,Object> params = Maps.newHashMap();
		String template = "";
		try {
			params.put("Cname", UserUtils.getUser().getName());
			params.put("Cemail", UserUtils.getUser().getEmail());
			template = PdfUtil.getPsiTemplate("partsDeliveryEmail.ftl", params);
		} catch (Exception e) {}
		sendEmail.setSendContent(template);
		sendEmail.setSendEmail(UserUtils.getUser().getEmail());   //自建人
		sendEmail.setSendSubject("新建配件收货单LN:"+billNo+"("+DateUtils.getDate()+")");
		sendEmail.setCreateBy(UserUtils.getUser());
		//向供应商发送邮件 加入附件和抄送人
		sendEmail.setBccToEmail(toEmail+",supply-chain@inateck.com");//抄送给收货供应商及供应链部
		  
		//发送邮件
		final MailInfo mailInfo = sendEmail.getMailInfo();
		String	filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/partsDelivery";
		File file = new File(filePath, deliveryBill.getBillNo());
		if (!file.exists()) {
			file.mkdirs();
		}
		File pdfFile = new File(file, deliveryBill.getBillNo() + ".pdf");
		PdfUtil.genPsiPartsDeliveryPdf(pdfFile,deliveryBill);
		
		
		mailInfo.setFileName(billNo + ".pdf");
		mailInfo.setFilePath(pdfFile.getAbsolutePath());
		sendEmail.setSendAttchmentPath(billNo + ".pdf");
		new Thread(){
			@Override
			public void run() {
				mailManager.send(mailInfo);
			}
		}.start();
		
		sendEmail.setSentDate(new Date());
		sendEmail.setSendFlag("1");
		sendEmailService.save(sendEmail);
	}

	
	@Transactional( readOnly = false)
	public void sureSave(PsiPartsDelivery psiPartsDeliery,MultipartFile[] attchmentFiles) {
		//如果不为申请状态不能确认
		if(!"0".equals(psiPartsDeliery.getBillSta())){
			return;
		}
		
		boolean flag =true;
		Map<Integer ,List<String>>   orderMap= Maps.newHashMap();
		Map<Integer ,String>   orderStatus= Maps.newHashMap();
		Map<Integer,Integer> partsMap = Maps.newHashMap();//key:配件id value：
		for(PsiPartsDeliveryItem billItem: psiPartsDeliery.getItems()){
			
			PsiPartsOrderItem  orderItem = billItem.getPartsOrderItem();
			String   partsName   = billItem.getPartsName();
			Integer  partsId     = billItem.getPartsId();
			Integer  orderId   	 = orderItem.getPartsOrder().getId();
			Integer  orderNum  	 = orderItem.getQuantityOrdered();
			Integer  perRecNum   = orderItem.getQuantityPreReceived();
			Integer  recNum      = orderItem.getQuantityReceived();
			
			Integer  ladingQuantity =billItem.getQuantityLading();
			//组成库存处理map
			if(partsMap.get(partsId)!=null){
				ladingQuantity+=partsMap.get(partsId);
			}
			partsMap.put(partsId, ladingQuantity);
			
			if(orderNum<perRecNum+recNum){
				flag = false;
				break;
			}else{
				orderItem.setQuantityReceived(recNum+billItem.getQuantityLading());
				orderItem.setQuantityPreReceived(perRecNum-billItem.getQuantityLading());
				
				//如果预收货数为0；说明多次点击确认
				if(orderItem.getQuantityPreReceived()<0){
					throw new RuntimeException("提单进行了多次确认，请核实，操作已取消");
				}
				
				if(orderItem.getQuantityReceived()>orderItem.getQuantityOrdered()){
					throw new RuntimeException("该提单关联的订单项已收货数大于订单数，请核实，操作已取消");
				}
				
				if(orderNum.equals(recNum+billItem.getQuantityLading())){
					List<String> products = orderMap.get(orderId);
					if(products==null){
						products =Lists.newArrayList();
						orderMap.put(orderId, products);
					}
					
					products.add(partsName);
					
				}
				orderStatus.put(orderId, "3");
				this.psiPartsOrderItemDao.save(orderItem);
			}
		}
		
		//处理配件库存变更信息 start
		changePartsInventory(partsMap,psiPartsDeliery.getBillNo());
		//处理配件库存变更信息 end
		
		for (Map.Entry<Integer, List<String>> entry : orderMap.entrySet()) {
			Integer orderId = entry.getKey();
			boolean isFinal = true;
			PsiPartsOrder order = this.psiPartsOrderDao.get(orderId);
			for (PsiPartsOrderItem item : order.getItems()) {
				String  productName=item.getPartsName();
				if(!entry.getValue().contains(productName)){
					Integer  orderNum  = item.getQuantityOrdered();
					Integer  recNum    = item.getQuantityReceived();
					if(orderNum!=null&&!orderNum.equals(recNum)){
						isFinal = false;
						break;
					}
				}
			}
			if(isFinal){
				if(order.getTotalAmount().equals(order.getPaymentAmount()+order.getDepositAmount())){
					orderStatus.put(orderId, "7");
				}else{
					orderStatus.put(orderId, "5");
				}
				
			}	
		}
		
		for (Map.Entry<Integer, String> entry : orderStatus.entrySet()) {
			Integer id = entry.getKey();
			String status = entry.getValue();
			if("5".equals(status)){
				//如果是已收货      获取收货时间  
				this.psiPartsOrderDao.updateOrderStaAndFinishedDate(id, status,new Date());
			}else{
				this.psiPartsOrderDao.updateOrderSta(id, status);
			}
		}
		
		if(!flag){
			//收货数量大于订单数量，请编辑后保存
			throw new RuntimeException("收货数量大于配件订单数量，请取消该单，操作已取消");
		}
		
		
		for (MultipartFile attchmentFile : attchmentFiles) {
			if(attchmentFile.getSize()!=0){
				String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/partsDelivery";
				File baseDir = new File(baseDirStr+"/"+psiPartsDeliery.getBillNo()); 
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
				String name=UUID.randomUUID().toString()+suffix;
				File dest = new File(baseDir,name);
				try {
					FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
					psiPartsDeliery.setAttchmentPathAppend("/psi/partsDelivery/"+psiPartsDeliery.getBillNo()+"/"+name);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
		}
		
		psiPartsDeliery.setSureUser(UserUtils.getUser());
		psiPartsDeliery.setSureDate(new Date());
		psiPartsDeliery.setBillSta("1");
		this.psiPartsDeliveryDao.save(psiPartsDeliery);
	}
	
	public  void changePartsInventory(Map<Integer,Integer> partsMap,String billNo){
		for(Map.Entry<Integer,Integer> entry:partsMap.entrySet()){
			Integer partsId = entry.getKey();
			Integer quantity =entry.getValue();
			PsiPartsInventory partsInventory= psiPartsInventoryService.getPsiPartsInventorys(partsId);
			//先收货冻结po的，如果够用就不收po非里面的    
			Integer poFrozen =partsInventory.getPoFrozen();
			if(partsInventory.getPoFrozen()-quantity>=0){
				partsInventory.setPoFrozen(partsInventory.getPoFrozen()-quantity);
				partsInventory.setStockFrozen(partsInventory.getStockFrozen()+quantity);
				//添加日志
				this.psiPartsInventoryService.saveLog(partsId, partsInventory.getPartsName(), 0-quantity,"poFrozen", billNo, UserUtils.getUser(), new Date(), "Purchase Storing", "");
				this.psiPartsInventoryService.saveLog(partsId, partsInventory.getPartsName(), quantity,"stockFrozen", billNo, UserUtils.getUser(), new Date(), "Purchase Storing", "");
			}else{
				Integer notFrozenQuantity =quantity-poFrozen;
				Integer oldPoFrozen = partsInventory.getPoFrozen();
				partsInventory.setPoFrozen(0);
				partsInventory.setStockFrozen(partsInventory.getStockFrozen()+poFrozen);
				partsInventory.setPoNotFrozen(partsInventory.getPoNotFrozen()-notFrozenQuantity);
				if(partsInventory.getPoNotFrozen()<0){
					throw new RuntimeException("收货后配件:"+partsInventory.getPartsName()+"库存：po非冻结为负数，请检查，操作已取消");
				}
				partsInventory.setStockNotFrozen(partsInventory.getStockNotFrozen()+notFrozenQuantity);
				
				//添加日志
				if(!poFrozen.equals(0)){
					this.psiPartsInventoryService.saveLog(partsId, partsInventory.getPartsName(), 0-oldPoFrozen,"poFrozen", billNo, UserUtils.getUser(), new Date(), "Purchase Storing", "");
					this.psiPartsInventoryService.saveLog(partsId, partsInventory.getPartsName(), poFrozen,"stockFrozen", billNo, UserUtils.getUser(), new Date(), "Purchase Storing", "");
				}
				
				if(!notFrozenQuantity.equals(0)){
					this.psiPartsInventoryService.saveLog(partsId, partsInventory.getPartsName(), notFrozenQuantity,"stockNotFrozen", billNo, UserUtils.getUser(), new Date(), "Purchase Storing", "");
					this.psiPartsInventoryService.saveLog(partsId, partsInventory.getPartsName(), 0-notFrozenQuantity,"poNotFrozen", billNo, UserUtils.getUser(), new Date(), "Purchase Storing", "");
				}
			}
			psiPartsInventoryDao.save(partsInventory);
		}
	}
	
	
	public List<PsiPartsDeliveryDto> getProductLading(Integer supplierId,String currencyType){
		String getProductLadingSql="SELECT poi.`parts_id`,poi.`parts_name`,(poi.quantity_ordered-poi.quantity_received-poi.quantity_pre_received) AS aaa,po.id AS order_id,po.parts_order_no,poi.id,poi.item_price FROM psi_parts_order_item  AS poi  ,psi_parts_order AS po  WHERE poi.`parts_order_id` =po.`id` AND poi.del_flag='0' AND po.supplier_id=:p1  AND po.currency_type=:p2 AND po.order_sta IN ('1','3') AND (poi.quantity_ordered-poi.quantity_received-poi.quantity_pre_received)>0";
		List<Object[]> objs =psiPartsDeliveryDao.findBySql(getProductLadingSql, new Parameter(supplierId,currencyType));
		List<PsiPartsDeliveryDto> dtos= Lists.newArrayList();
		for(Object[] obj:objs){
			dtos.add(new PsiPartsDeliveryDto((Integer)obj[0], (String)obj[1], Integer.parseInt(obj[2].toString()), (Integer)obj[3], (String)obj[4], (Integer)obj[5],Float.parseFloat(obj[6].toString())));
		}
		return dtos;
	}
	
	public List<PsiPartsDeliveryDto> getProductLadingForEdit(Integer supplierId,String currencyType){
		String getProductLadingSql="SELECT poi.`parts_id`,poi.`parts_name`,(poi.quantity_ordered-poi.quantity_received-poi.quantity_pre_received) AS aaa,po.id AS order_id ,po.parts_order_no,poi.id,poi.item_price FROM psi_parts_order_item  AS poi  ,psi_parts_order AS po  WHERE poi.`parts_order_id` =po.`id` AND poi.del_flag='0' AND po.supplier_id=:p1  AND po.currency_type=:p2 AND po.order_sta IN ('1','3') AND (poi.quantity_ordered-poi.quantity_received-poi.quantity_pre_received)>=0 ";
		List<Object[]> objs =psiPartsDeliveryDao.findBySql(getProductLadingSql, new Parameter(supplierId,currencyType));
		List<PsiPartsDeliveryDto> dtos= Lists.newArrayList();
		for(Object[] obj:objs){
			dtos.add(new PsiPartsDeliveryDto((Integer)obj[0], (String)obj[1], Integer.parseInt(obj[2].toString()), (Integer)obj[3], (String)obj[4], (Integer)obj[5],Float.parseFloat(obj[6].toString())));
		}
		return dtos;
	}
	
	
	@Transactional(readOnly = false)
	public boolean cancelBill(PsiPartsDelivery psiPartsDelivery) {
		boolean flag=true;
		try{
		for(PsiPartsDeliveryItem item:psiPartsDelivery.getItems()){
			PsiPartsOrderItem orderItem=item.getPartsOrderItem();
			//获取预提单数量 
			int preQuantity=orderItem.getQuantityPreReceived();
			orderItem.setQuantityPreReceived(preQuantity-item.getQuantityLading());
			psiPartsOrderItemDao.save(orderItem);
		}
		}catch(Exception  ex){
			return false;
		}
		return flag;
	}
	
	@Transactional(readOnly = false)
	public String  createSequenceNumber(String seqCodeName){
		return this.genSequenceDao.genSequence(seqCodeName,2);
	}
	
}
