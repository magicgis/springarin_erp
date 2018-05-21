/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.parts;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

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
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsDeliveryDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsOrderDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsOrderItemDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsPaymentDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsPaymentItemDao;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsDelivery;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsDeliveryItem;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrder;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrderItem;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsPayment;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsPaymentItem;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 配件订单付款详情Service
 * @author Michael
 * @version 2015-06-15
 */
@Component
@Transactional(readOnly = true)
public class PsiPartsPaymentService extends BaseService {
	@Autowired
	private PsiPartsPaymentDao   		psiPartsPaymentDao;
	@Autowired
	private PsiPartsOrderDao	 		psiPartsOrderDao;
	@Autowired
	private PsiPartsDeliveryDao	 		psiPartsDeliveryDao;
	@Autowired
	private GenerateSequenceDao  		genSequenceDao;
	@Autowired  
	private PsiSupplierService   		psiSupplierService;
	@Autowired
	private SystemService  				systemService;
	@Autowired
	private MailManager 				mailManager;  
	@Autowired
	private PsiPartsPaymentItemDao 		psiPartsPaymentItemDao;
	@Autowired
	private PsiPartsOrderItemDao        psiPartsOrderItemDao;
	       
	private static String filePath;
	
	public PsiPartsPayment get(Integer id) {
		return psiPartsPaymentDao.get(id);
	}
	
	public Page<PsiPartsPayment> find(Page<PsiPartsPayment> page, PsiPartsPayment psiPartsPayment) {
		DetachedCriteria dc = psiPartsPaymentDao.createDetachedCriteria();
		if(psiPartsPayment.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",psiPartsPayment.getCreateDate()));
		}
		
		if(psiPartsPayment.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(psiPartsPayment.getUpdateDate(),1)));
		}
		
		if(StringUtils.isNotEmpty(psiPartsPayment.getPaymentNo())){
			dc.add(Restrictions.like("paymentNo", "%"+psiPartsPayment.getPaymentNo()+"%"));
		}
		
		if(StringUtils.isNotEmpty(psiPartsPayment.getPaymentSta())){
			dc.add(Restrictions.eq("paymentSta", psiPartsPayment.getPaymentSta()));
		}else{
			dc.add(Restrictions.ne("paymentSta", "3"));
		}
		
		if(psiPartsPayment.getSupplier()!=null&&psiPartsPayment.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", psiPartsPayment.getSupplier()));
		}
		dc.addOrder(Order.desc("id"));
		return psiPartsPaymentDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void addSave(PsiPartsPayment psiPartsPayment) throws Exception {
		psiPartsPayment.setCreateUser(UserUtils.getUser());
		psiPartsPayment.setCreateDate(new Date());
	
		
		PsiSupplier supplier = new PsiSupplier();
		if(psiPartsPayment.getSupplier()!=null&&psiPartsPayment.getSupplier().getId()!=null){
			supplier=psiSupplierService.get(psiPartsPayment.getSupplier().getId());
			String paymentNo=this.genSequenceDao.genSequence(supplier.getNikename()+"_PJFK",2);
			psiPartsPayment.setPaymentNo(paymentNo);
		}
		boolean rs = false;
		try {
			rs = replyEmailOrGetTotal(psiPartsPayment);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		if(!rs){
			for(PsiPartsPaymentItem item : psiPartsPayment.getItems()) {
				item.setPsiPartsPayment(psiPartsPayment);
				if(psiPartsPayment.getPaymentSta().equals("1")){
					if("0".equals(item.getPaymentType())){
						item.getOrder().setDepositPreAmount(0f);
					}else if("1".equals(item.getPaymentType())){
						PsiPartsDelivery bill=item.getLadingBill();
						float pre = bill.getTotalPaymentPreAmount();
						bill.setTotalPaymentPreAmount(pre-item.getPaymentAmount());
					}
					item.setDelFlag("0");
				}
			}
			psiPartsPayment.setPaymentSta("0");
		}else{
			for(PsiPartsPaymentItem item : psiPartsPayment.getItems()) {
				item.setPsiPartsPayment(psiPartsPayment);
				if("0".equals(item.getPaymentType())){
					item.getOrder().setPayItem(item);
				}
			}
		}
		
		
		//psiPartsPayment.setCurrencyType(supplier.getCurrencyType());
		psiPartsPayment.setSupplier(supplier);
		//发信通知财务付款
		//this.sendEmailToSupplier(psiPartsPayment);
		
		psiPartsPaymentDao.save(psiPartsPayment);
	}
	
	
	@Transactional(readOnly = false)
	public void cancel(PsiPartsPayment psiPartsPayment) {
//		//把原来预交货的数量改回来
//		for(PsiPartsPaymentItem item :psiPartsPayment.getItems()){
//			this.psiPartsOrderDao.updatePrePaymentAmount(item.getPartsOrderId(), item.getPaymentAmount(),"cancel");
//			
//		}
		//追回提单预付款项        订单预付款项
		for(PsiPartsPaymentItem payItem:psiPartsPayment.getItems()){
			PsiPartsDelivery bill =payItem.getLadingBill();
			if(bill!=null){
				//提单不为空说明是尾款       否则为定金
				Float preAmonut = bill.getTotalPaymentPreAmount();
				PsiPartsDelivery oldLadingBill=this.psiPartsDeliveryDao.get(bill.getId());
				oldLadingBill.setTotalPaymentPreAmount(oldLadingBill.getTotalPaymentPreAmount()-preAmonut);
				this.psiPartsDeliveryDao.save(oldLadingBill);
				//目前预付款只到定金这里，以后尽量到订单
			}else{
				PsiPartsOrder order=payItem.getOrder();
				//定金只能付一次，直接清空预付定金即可
				order.setDepositPreAmount(0f);
				this.psiPartsOrderDao.save(order);
			}
		}
		psiPartsPayment.setCancelDate(new Date());
		psiPartsPayment.setCancelUser(UserUtils.getUser());
		this.psiPartsPaymentDao.save(psiPartsPayment);
	}
	
	
	@Transactional(readOnly = false)
	public void sureSave(MultipartFile memoFile,PsiPartsPayment psiPartsPayment) {
			if(memoFile!=null&&memoFile.getSize()!=0){
				if (filePath == null) {
					filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/partsPayment";
				}
				File baseDir = new File(filePath+"/"+psiPartsPayment.getPaymentNo()); 
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				String suffix = memoFile.getOriginalFilename().substring(memoFile.getOriginalFilename().lastIndexOf("."));     
				String name=UUID.randomUUID().toString()+suffix;
				File dest = new File(baseDir,name);
				try {
					FileUtils.copyInputStreamToFile(memoFile.getInputStream(),dest);
					psiPartsPayment.setAttchmentPath("/psi/partsPayment/"+psiPartsPayment.getPaymentNo()+"/"+name);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
			
			Map<Integer ,List<String>>   orderMap= Maps.newHashMap();
			for(PsiPartsPaymentItem item:psiPartsPayment.getItems()){
				//区分是提单还是订单
				if(item.getPaymentType()!=null&&!"".equals(item.getPaymentType())){
					if("0".equals(item.getPaymentType())){
						//把订单        预付定金改为已付定金     预付变为0           
						PsiPartsOrder order = item.getOrder();
						order.setDepositAmount(order.getDepositAmount()+item.getPaymentAmount());	  //保险起见这样写    变为已付定金数
						order.setDepositPreAmount(order.getDepositPreAmount()-item.getPaymentAmount());   //保险起见这样写    其实可以直接赋0f
						this.psiPartsOrderDao.save(order);
					}else if("1".equals(item.getPaymentType())){
						PsiPartsDelivery bill = item.getLadingBill();
						bill.setTotalPaymentPreAmount(bill.getTotalPaymentPreAmount()-item.getPaymentAmount());
						bill.setTotalPaymentAmount(bill.getTotalPaymentAmount()+item.getPaymentAmount());
						this.psiPartsDeliveryDao.save(bill);
						// 如果提单金额已支付完            再看订单item  和订单     得出订单付款状态；
						if(Float.floatToIntBits(bill.getTotalAmount())==Float.floatToIntBits(bill.getTotalPaymentAmount())){
							for(PsiPartsDeliveryItem billItem: bill.getItems()){
								PsiPartsOrderItem orderItem =billItem.getPartsOrderItem();
								PsiPartsOrder     order     = orderItem.getPartsOrder();
								Integer orderId = orderItem.getPartsOrder().getId();
								String partsName =billItem.getPartsName();
								Float ratioPay=(100-orderItem.getPartsOrder().getDeposit())/100f;
								orderItem.setQuantityPayment(orderItem.getQuantityPayment()+billItem.getQuantityLading());//更新订单Item表支付数量
								//算出提单item项的总值;   放入订单项的已付款属性里
								Float totalAmount=billItem.getQuantityLading()*billItem.getItemPrice()*(ratioPay);
								orderItem.setPaymentAmount(orderItem.getPaymentAmount()+totalAmount);
								this.psiPartsOrderItemDao.save(orderItem);
								
//								//判断是否是第一次付款，flag 货币类型
//								if(order.getDepositAmount()==0&&order.getPaymentSta().equals("0")){
//									order.setPaymentSta("1");
//									order.setCurrencyType(psiPartsPayment.getSupplier().getCurrencyType());
//								}
								
								//订单里面也维护已支付总额
								order.setPaymentAmount(order.getPaymentAmount()+totalAmount);
								this.psiPartsOrderDao.save(order);
								
								
								//判断订单Item项总值
								if(Float.floatToIntBits(orderItem.getPaymentAmount())==Float.floatToIntBits(orderItem.getItemPrice()*orderItem.getQuantityOrdered()*ratioPay)){
									List<String> products = orderMap.get(orderId);
									if(products==null){
										products =Lists.newArrayList();
										orderMap.put(orderId, products);
									}
									products.add(partsName);
									
								}
								
							}
							
						}
					}
				}
			}
			for (Map.Entry<Integer, List<String>> entry : orderMap.entrySet()) {
				Integer orderId = entry.getKey();
				boolean isFinal = true;
				for (PsiPartsOrderItem item : this.psiPartsOrderDao.get(orderId).getItems()) {
					String  partsName=item.getPartsName();
					if(!entry.getValue().contains(partsName)){
						Integer  orderNum  = item.getQuantityOrdered();
						Integer  recNum    = item.getQuantityReceived();
						if(!orderNum.equals(recNum)){
							isFinal = false;
							break;
						}
					}
					
				}
				//该订单所有项都付款完成，就更新order状态为已付款；
				if(isFinal){
					PsiPartsOrder order = this.psiPartsOrderDao.get(orderId);
					if("5".equals(order.getOrderSta())){
						order.setOrderSta("7");
					}
					this.psiPartsOrderDao.save(order);
				}
			}
			
			psiPartsPayment.setSureDate(new Date());
			psiPartsPayment.setSureUser(UserUtils.getUser());
			psiPartsPayment.setPaymentSta("2");
			this.psiPartsPaymentDao.save(psiPartsPayment);
			try {
				this.sendToSupplier(psiPartsPayment);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	@Transactional(readOnly = false)
	public boolean editSave(PsiPartsPayment partsPayment) {
		partsPayment.setCreateUser(UserUtils.getUser());
		boolean rs = false;
		try {
			rs = replyEmailOrGetTotal(partsPayment);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		if(!rs){
			for(PsiPartsPaymentItem item : partsPayment.getItems()) {
				item.setPsiPartsPayment(partsPayment);
				if(partsPayment.getPaymentSta().equals("1")){
					if("0".equals(item.getPaymentType())){
						item.getOrder().setDepositPreAmount(0f);
					}else if("1".equals(item.getPaymentType())){
						PsiPartsDelivery bill=item.getLadingBill();
						float pre = bill.getTotalPaymentPreAmount();
						bill.setTotalPaymentPreAmount(pre-item.getPaymentAmount());
					}
				}
			}
			partsPayment.setPaymentSta("0");
		}else{
			for(PsiPartsPaymentItem item : partsPayment.getItems()) {
				item.setPsiPartsPayment(partsPayment);
				if("0".equals(item.getPaymentType())){
					item.getOrder().setPayItem(item);
				}
			}
		}
		
		Collection<String> setNewIds = Collections2.transform(partsPayment.getItems(), new Function<PsiPartsPaymentItem, String>() {
			public String apply(PsiPartsPaymentItem input) {
				return input.getId().toString();
			};
		});
		
		Set<Integer>  delItemSet = Sets.newHashSet();
		String oldItemIds=partsPayment.getOldItemIds();
		String [] oldIds = oldItemIds.split(",");
		if(setNewIds!=null&&setNewIds.size()>0){
			for(int j=0;j<oldIds.length;j++){
				if(!setNewIds.contains(oldIds[j])){
					//不包含就干掉
					delItemSet.add(Integer.valueOf(oldIds[j]));
				};
			}
		}else{
			for(int j=0;j<oldIds.length;j++){
				delItemSet.add(Integer.valueOf(oldIds[j]));
			}
		}
		
		
		if(delItemSet.size()>0){
			for(PsiPartsPaymentItem item:this.getPartsPaymentItems(delItemSet)){
				item.setDelFlag("1");
				item.setPsiPartsPayment(partsPayment);
				partsPayment.getItems().add(item);
			};
		}
		
		
		partsPayment.setUpdateDate(new Date());
		partsPayment.setUpdateUser(UserUtils.getUser());
		this.psiPartsPaymentDao.getSession().merge(partsPayment);
		return rs;
	}
	
	
	
	private boolean replyEmailOrGetTotal(PsiPartsPayment partsPayment) throws Exception{
		partsPayment.setSupplier(psiSupplierService.get(partsPayment.getSupplier().getId()));
		Float totalAmount=0f;
		for(PsiPartsPaymentItem item : partsPayment.getItems()) {
			totalAmount+=item.getPaymentAmount();
			if("0".equals(item.getPaymentType())){
				PsiPartsOrder order = this.psiPartsOrderDao.get(item.getUnknowId());
				item.setOrder(order);
				if(partsPayment.getPaymentSta().equals("1")){
					order.setDepositPreAmount(item.getPaymentAmount());
				}
			}else if("1".equals(item.getPaymentType())){
				PsiPartsDelivery bill=this.psiPartsDeliveryDao.get(item.getUnknowId());
				item.setLadingBill(bill);
				if(partsPayment.getPaymentSta().equals("1")){
					Float totalPrePayLading = bill.getTotalPaymentPreAmount();
					bill.setTotalPaymentPreAmount(totalPrePayLading+item.getPaymentAmount());
				}
			}
		}
		partsPayment.setPaymentAmountTotal(totalAmount);
		if(partsPayment.getPaymentSta().equals("1")){
			partsPayment.setApplyDate(new Date());
			partsPayment.setApplyUser(UserUtils.getUser());
			Map<String, Object> prarms = Maps.newHashMap();
			prarms.put("payment",partsPayment);
			List<User> userList = systemService.findUserByPermission("payment:operate:user");
			List<User> replys = Lists.newArrayList();
			if(userList!=null){
				replys.addAll(userList);
			}
			replys.add(UserUtils.getUser());
			String toAddress = Collections3.extractToString(replys,"email", ",");
			String content = PdfUtil.getPsiTemplate("applyPaymentByPartsEmail.ftl",prarms);
			if(StringUtils.isNotBlank(content)){
				Date date = new Date();
				final MailInfo mailInfo = new MailInfo(toAddress,partsPayment.getSupplier().getNikename()+"支付配件款申请"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent(content);
				new Thread(){
					public void run(){
						 mailManager.send(mailInfo);
					}
				}.start();
			}
		}
		return true;
	}
	
	
	public void updateSta(Integer paymentId,String sta) {
		String sql ="UPDATE psi_parts_payment AS a SET a.`payment_sta`=:p2 WHERE a.`id`=:p1";
		psiPartsPaymentDao.updateBySql(sql,new Parameter(paymentId,sta));
	}
	
	
	@Transactional(readOnly = false)
	public void save(PsiPartsPayment psiPartsPayment) {
		psiPartsPaymentDao.save(psiPartsPayment);
	}
	
	/**
	 *查询出未付款的配件订单信息 
	 * 
	 */
	public  Map<String,PsiPartsOrder>   getUnPaymentDoneOrder(Integer supplierId,String currency){
		 Map<String,PsiPartsOrder> orderMap =Maps.newHashMap();
		String sql ="SELECT id,parts_order_no,(total_amount*deposit)/100,payment_amount,pre_payment_amount,deposit,currency_type,deposit_amount FROM psi_parts_order  WHERE deposit>0 AND order_sta NOT IN('0','7','8') AND  deposit_pre_amount =0  AND  deposit_amount =0  AND supplier_id=:p1  ORDER BY id ";
		List<Object[]> list=null;
		if(StringUtils.isNotEmpty(currency)){
			sql ="SELECT id,parts_order_no,(total_amount*deposit)/100,payment_amount,pre_payment_amount,deposit,currency_type,deposit_amount FROM psi_parts_order  WHERE deposit>0 AND order_sta NOT IN('0','7','8') AND  deposit_pre_amount =0  AND  deposit_amount =0  AND supplier_id=:p1 AND currency_type=:p2 ORDER BY id ";
			list=this.psiPartsOrderDao.findBySql(sql,new Parameter(supplierId,currency));
		 }else{
			 list=this.psiPartsOrderDao.findBySql(sql,new Parameter(supplierId));
		 }
		
		for(Object[] obj :list){
			orderMap.put((String)obj[1],new PsiPartsOrder((Integer)obj[0], (String)obj[1], ((BigDecimal)obj[2]).floatValue(), ((BigDecimal)obj[3]).floatValue(), ((BigDecimal)obj[4]).floatValue(), (Integer)obj[5], (String)obj[6],((BigDecimal)obj[7]).floatValue()));
		}
		return orderMap;
		
	}
	
	/**
	 *查询出未付款的配件订单信息 
	 * 
	 */
	public  Map<String,PsiPartsDelivery>   getUnPaymentDoneLading(Integer supplierId,String currency){
		 Map<String,PsiPartsDelivery> ladingMap =Maps.newHashMap();
		String sql ="SELECT id,bill_no,(total_amount - total_payment_amount - total_payment_pre_amount ) as aaa,(total_payment_amount + total_payment_pre_amount) as bbb,currency_type FROM psi_parts_delivery WHERE (total_amount-total_payment_amount - total_payment_pre_amount)>0  AND bill_sta ='1' AND supplier_id=:p1 ORDER BY id ";;
		List<Object[]> list=null;
		if(StringUtils.isNotEmpty(currency)){
			sql="SELECT id,bill_no,(total_amount - total_payment_amount - total_payment_pre_amount ) as aaa,(total_payment_amount + total_payment_pre_amount) as bbb,currency_type FROM psi_parts_delivery WHERE (total_amount-total_payment_amount - total_payment_pre_amount)>0  AND bill_sta ='1' AND supplier_id=:p1 AND currency_type=:p2 ORDER BY id ";
			list=this.psiPartsDeliveryDao.findBySql(sql,new Parameter(supplierId,currency));
		 }else{
			list=this.psiPartsDeliveryDao.findBySql(sql,new Parameter(supplierId));
		 }
		
		for(Object[] obj :list){
			ladingMap.put((String)obj[1],new PsiPartsDelivery((Integer)obj[0], (String)obj[1], ((BigDecimal)obj[2]).floatValue(), ((BigDecimal)obj[3]).floatValue(), (String)obj[4]));
		}
		return ladingMap;
	}
	
	
	/**
	 *根据itemId获取list信息 
	 */
	public List<PsiPartsPaymentItem> getPartsPaymentItems(Set<Integer> ids){
		DetachedCriteria dc = this.psiPartsPaymentItemDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		return psiPartsPaymentItemDao.find(dc);
	}
	
	
	private void sendToSupplier(PsiPartsPayment partsPayment) throws Exception{
		SendEmail  sendEmail = new SendEmail();
		PsiSupplier supplier = partsPayment.getSupplier();
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
		File pdfFile = new File(baseDir,partsPayment.getAttchmentPath());
		mailInfo.setFileName(pdfFile.getName());
		mailInfo.setFilePath(pdfFile.getAbsolutePath());
		new Thread(){
			@Override
			public void run() {
				mailManager.send(mailInfo);
			}
		}.start();
	}
	
	
}
