/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.lc;

import java.io.File;
import java.io.IOException;
import java.util.Date;
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
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.Collections3;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.modules.psi.dao.lc.LcPsiTransportOrderDao;
import com.springrain.erp.modules.psi.dao.lc.LcPsiTransportReviseDao;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportRevise;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportReviseItem;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 运单付款修正表Service
 * @author Michael
 * @version 2015-01-29
 */
@Component
@Transactional(readOnly = true)
public class LcPsiTransportReviseService extends BaseService {  
	
	
	@Autowired
	private LcPsiTransportReviseDao psiTransportReviseDao;
	@Autowired
	private LcPsiTransportOrderDao psiTransportOrderDao;
	@Autowired
	private SystemService systemService;
	@Autowired   
	private PsiSupplierService psiSupplierService;
	@Autowired
	private GenerateSequenceDao genDao;	
	@Autowired
	private MailManager mailManager;
	
	
	public LcPsiTransportRevise get(Integer id) {
		return psiTransportReviseDao.get(id);
	}
	
	
	public Page<LcPsiTransportRevise> find(Page<LcPsiTransportRevise> page, LcPsiTransportRevise psiTransportRevise) {
		DetachedCriteria dc = psiTransportReviseDao.createDetachedCriteria();
		if(psiTransportRevise.getApplyDate()!=null){
			dc.add(Restrictions.ge("applyDate",psiTransportRevise.getApplyDate()));
		}
		
		if(psiTransportRevise.getSureDate()!=null){
			dc.add(Restrictions.le("applyDate",DateUtils.addDays(psiTransportRevise.getSureDate(),1)));
		}
		
		if(StringUtils.isNotEmpty(psiTransportRevise.getPaymentNo())){
			dc.add(Restrictions.like("paymentNo", "%"+psiTransportRevise.getPaymentNo()+"%"));
		}
		
		if(StringUtils.isNotEmpty(psiTransportRevise.getReviseSta())){
			dc.add(Restrictions.eq("reviseSta", psiTransportRevise.getReviseSta()));
		}else{
			dc.add(Restrictions.ne("reviseSta", "8"));
		}
		
		if(psiTransportRevise.getSupplier()!=null&&psiTransportRevise.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", psiTransportRevise.getSupplier()));
		}
		dc.addOrder(Order.desc("id"));
		return psiTransportReviseDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void addSave(LcPsiTransportRevise psiTransportRevise,MultipartFile[] filePaths) {
		for(LcPsiTransportReviseItem item:psiTransportRevise.getItems()){
			item.setTransportRevise(psiTransportRevise);
		}
		
		String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/lcPsiTranSportRevise";
		for (MultipartFile attchmentFile : filePaths) {
			if(attchmentFile.getSize()!=0){
				File baseDir = new File(baseDirStr); 
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
				String name=UUID.randomUUID().toString()+suffix;
				File dest = new File(baseDir,name);
				try {
					FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
					psiTransportRevise.setFilePathAppend("/psi/lcPsiTranSportRevise/"+name);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
		}
		psiTransportRevise.setApplyUser(UserUtils.getUser());
		psiTransportRevise.setApplyDate(new Date());
		psiTransportRevise.setReviseSta("0");
		//生成单号
		psiTransportRevise.setPaymentNo(genDao.genSequenceByMonth("_YDXZFK", 3));
		psiTransportReviseDao.save(psiTransportRevise);
		
		//如果是申请发信通知
		String toAddress="emma.chao@inateck.com";
		String content = "(理诚)运单付款修正编号：<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPsiTransportRevise/review?id="+psiTransportRevise.getId()+"'>"+psiTransportRevise.getPaymentNo()+"</a>已创建，请尽快登陆erp系统审批";
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			final MailInfo mailInfo = new MailInfo(toAddress,"运单付款修正单已创建待审批"+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			mailInfo.setCcToAddress(UserUtils.getUser().getEmail());
			if(StringUtils.isNotEmpty(psiTransportRevise.getAccountPath())){
				for(String filePath:psiTransportRevise.getAccountPath().split(",")){
					String path=ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir();
					String fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
					mailInfo.setFileName(fileName);
					mailInfo.setFilePath(path+"/"+filePath);
				}
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
	
	@Transactional(readOnly = false)
	public boolean reviewSave(LcPsiTransportRevise psiTransportRevise) {
		boolean rs = false;
		if(psiTransportRevise.getReviseAmount()>0){
			//如果修正总额为正，发送邮件给财务，付款
			try {
				rs = replyEmailOrGetTotal(psiTransportRevise);
			} catch (Exception e) {
				e.printStackTrace();
			}	
			if(rs){
				psiTransportRevise.setReviseSta("3");//已审核
				psiTransportReviseDao.save(psiTransportRevise);
			}
			return rs;
		}else{
			LcPsiTransportOrder transportOrder  = this.psiTransportOrderDao.get(psiTransportRevise.getTranOrderId());
			if(psiTransportRevise.getItems()!=null&&psiTransportRevise.getItems().size()>0){
				for(LcPsiTransportReviseItem item:psiTransportRevise.getItems()){
					if(item.getReviseType().equals("LocalAmount")){
						transportOrder.setLocalAmount(transportOrder.getLocalAmount()+item.getReviseAmount());
					}else if(item.getReviseType().equals("TranAmount")){
						transportOrder.setTranAmount(transportOrder.getTranAmount()+item.getReviseAmount());
					}else if(item.getReviseType().equals("DapAmount")){
						transportOrder.setDapAmount(transportOrder.getDapAmount()+item.getReviseAmount());
					}else if(item.getReviseType().equals("OtherAmount")){
						transportOrder.setOtherAmount(transportOrder.getOtherAmount()+item.getReviseAmount());
					}else if(item.getReviseType().equals("OtherAmount1")){
						transportOrder.setOtherAmount1(transportOrder.getOtherAmount1()+item.getReviseAmount());
					}else if(item.getReviseType().equals("InsuranceAmount")){
						transportOrder.setInsuranceAmount(transportOrder.getInsuranceAmount()+item.getReviseAmount());
					}else if(item.getReviseType().equals("DutyAmount")){
						transportOrder.setDutyTaxes(transportOrder.getDutyTaxes()+item.getReviseAmount());
					}else if(item.getReviseType().equals("TaxAmount")){
						transportOrder.setTaxTaxes(transportOrder.getTaxTaxes()+item.getReviseAmount());
					}else if(item.getReviseType().equals("OthderTaxAmount")){
						transportOrder.setOtherTaxes(transportOrder.getOtherTaxes()+item.getReviseAmount());
					}
				}
			}
			this.psiTransportOrderDao.save(transportOrder);  //更新运单的几项值
			psiTransportRevise.setReviseSta("5");//已完成
			psiTransportReviseDao.save(psiTransportRevise);
			return true;
		}
		
	}
	
	@Transactional(readOnly = false)
	public void sureSave(LcPsiTransportRevise psiTransportRevise,MultipartFile[] attchmentFiles) {
		
		if(psiTransportRevise.getId()!=null){
			psiTransportRevise=this.psiTransportReviseDao.get(psiTransportRevise.getId());
			Integer tranOrderId =psiTransportRevise.getTranOrderId();
			
			LcPsiTransportOrder transportOrder  = this.psiTransportOrderDao.get(tranOrderId);
			if(psiTransportRevise.getItems()!=null&&psiTransportRevise.getItems().size()>0){
				for(LcPsiTransportReviseItem item:psiTransportRevise.getItems()){
					if(item.getReviseType().equals("LocalAmount")){
						transportOrder.setLocalAmount(transportOrder.getLocalAmount()+item.getReviseAmount());
					}else if(item.getReviseType().equals("TranAmount")){
						transportOrder.setTranAmount(transportOrder.getTranAmount()+item.getReviseAmount());
					}else if(item.getReviseType().equals("DapAmount")){
						transportOrder.setDapAmount(transportOrder.getDapAmount()+item.getReviseAmount());
					}else if(item.getReviseType().equals("OtherAmount")){
						transportOrder.setOtherAmount(transportOrder.getOtherAmount()+item.getReviseAmount());
					}else if(item.getReviseType().equals("OtherAmount1")){
						transportOrder.setOtherAmount1(transportOrder.getOtherAmount1()+item.getReviseAmount());
					}else if(item.getReviseType().equals("InsuranceAmount")){
						transportOrder.setInsuranceAmount(transportOrder.getInsuranceAmount()+item.getReviseAmount());
					}else if(item.getReviseType().equals("DutyAmount")){
						transportOrder.setDutyTaxes(transportOrder.getDutyTaxes()+item.getReviseAmount());
					}else if(item.getReviseType().equals("TaxAmount")){
						transportOrder.setTaxTaxes(transportOrder.getTaxTaxes()+item.getReviseAmount());
					}else if(item.getReviseType().equals("OtherTaxAmount")){
						transportOrder.setOtherTaxes(transportOrder.getOtherTaxes()+item.getReviseAmount());
					}
				}
				this.psiTransportOrderDao.save(transportOrder);  //更新运单的几项值
			}
			
			
			for (MultipartFile attchmentFile : attchmentFiles) {
				if(attchmentFile.getSize()!=0){
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/lcPsiTranSportRevise";
					File baseDir = new File(baseDirStr+"/"+psiTransportRevise.getPaymentNo()); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
					String name=UUID.randomUUID().toString()+suffix;
					File dest = new File(baseDir,name);
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						psiTransportRevise.setAttchmentPath("/psi/lcPsiTranSportRevise/"+psiTransportRevise.getPaymentNo()+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
			
			psiTransportRevise.setReviseSta("5");   //确认状态状态   
			psiTransportRevise.setSureDate(new Date());
			psiTransportRevise.setSureUser(UserUtils.getUser());
			psiTransportReviseDao.save(psiTransportRevise);
		}
	}
	
	@Transactional(readOnly = false)
	public void save(LcPsiTransportRevise psiTransportRevise) {
		psiTransportReviseDao.save(psiTransportRevise);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiTransportReviseDao.deleteById(id);
	}
	
	/**
	 *查询 未完成运单付款修正单
	 */
	public List<LcPsiTransportRevise> findUpDoneRevisePay(Integer supplierId,Integer tranOrderId) {
		DetachedCriteria dc = this.psiTransportReviseDao.createDetachedCriteria();
		dc.add(Restrictions.eq("tranOrderId",tranOrderId));
		dc.add(Restrictions.eq("reviseSta","0"));
		dc.add(Restrictions.eq("supplier.id",supplierId));
		dc.addOrder(Order.desc("id"));
		return psiTransportReviseDao.find(dc);
	}
	
	private boolean replyEmailOrGetTotal(LcPsiTransportRevise psiTransportRevise) throws Exception{
		psiTransportRevise.setSupplier(psiSupplierService.get(psiTransportRevise.getSupplier().getId()));
		Map<String, Object> prarms = Maps.newHashMap();
		prarms.put("payment",psiTransportRevise);
		List<User> userList = systemService.findUserByPermission("payment:operate:user");
		List<User> replys = Lists.newArrayList();
		if(userList!=null){
			replys.addAll(userList);
		}
		replys.add(UserUtils.getUser());
		String toAddress = Collections3.extractToString(replys,"email", ",");
		String content = PdfUtil.getPsiTemplate("applyPaymentByReviseTransportEmail.ftl",prarms);
		Date date = new Date();
		final MailInfo mailInfo = new MailInfo(toAddress,psiTransportRevise.getSupplier().getNikename()+"支付(理诚)运单物流修正款申请"+DateUtils.getDate("-yyyy/M/dd"),date);
		mailInfo.setContent(content);
		String ccToAddress="maik@inateck.com,emma.chao@inateck.com,alisa@inateck.com";
		mailInfo.setCcToAddress(ccToAddress);
		if(StringUtils.isNotEmpty(psiTransportRevise.getAccountPath())){
			for(String filePath:psiTransportRevise.getAccountPath().split(",")){
				String path=ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir();
				String fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
				mailInfo.setFileName(fileName);
				mailInfo.setFilePath(path+"/"+filePath);
			}
		}
		new Thread(){
			public void run() {
				mailManager.send(mailInfo);
			};
		}.start();
		return true;
	}
}
