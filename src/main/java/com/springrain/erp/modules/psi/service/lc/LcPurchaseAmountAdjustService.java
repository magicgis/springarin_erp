/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.lc;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
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

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.lc.LcPurchaseAmountAdjustDao;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseAmountAdjust;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 采购金额调整Service
 * @author Michael
 * @version 2015-08-24
 */
@Component
@Transactional(readOnly = true)
public class LcPurchaseAmountAdjustService extends BaseService {
	@Autowired
	private LcPurchaseAmountAdjustDao purchaseAmountAdjustDao;
	@Autowired
	private MailManager				  mailManager;
	@Autowired
	private SystemService             systemService;
	
	public LcPurchaseAmountAdjust get(Integer id) {
		return purchaseAmountAdjustDao.get(id);
	}
	
	public Page<LcPurchaseAmountAdjust> find(Page<LcPurchaseAmountAdjust> page, LcPurchaseAmountAdjust purchaseAmountAdjust) {
		DetachedCriteria dc = purchaseAmountAdjustDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(purchaseAmountAdjust.getSubject())){
			dc.add(Restrictions.like("subject","%"+purchaseAmountAdjust.getSubject()+"%"));
		}
		
		if(purchaseAmountAdjust.getSupplier()!=null&&purchaseAmountAdjust.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier.id", purchaseAmountAdjust.getSupplier().getId()));
		}
		
		if(purchaseAmountAdjust.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate", purchaseAmountAdjust.getCreateDate()));
		}
		
		if (purchaseAmountAdjust.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(purchaseAmountAdjust.getUpdateDate(),1)));
		}
		
		page.setOrderBy("adjustSta,id desc");
		return purchaseAmountAdjustDao.find2(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public void save(LcPurchaseAmountAdjust purchaseAmountAdjust,MultipartFile[] attchmentFiles) {
		if(purchaseAmountAdjust.getId()==null){
			purchaseAmountAdjust.setCreateDate(new Date());
			purchaseAmountAdjust.setCreateUser(UserUtils.getUser());
		}else{
			purchaseAmountAdjust.setUpdateDate(new Date());
			purchaseAmountAdjust.setUpdateUser(UserUtils.getUser());
		}
		
		for (MultipartFile attchmentFile : attchmentFiles) {
			if(attchmentFile.getSize()!=0){
				String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/purchaseAmountAdjust";
				File baseDir = new File(baseDirStr); 
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				String name=UUID.randomUUID().toString();
				String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf(".")); 
				File dest = new File(baseDir,name+suffix);
				try {
					FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
					purchaseAmountAdjust.setFilePathAppend("/psi/purchaseAmountAdjust/"+name+suffix);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
		}
		purchaseAmountAdjustDao.save(purchaseAmountAdjust);
		
		//如果为申请审核状态，给审核人发信
		if("a".equals(purchaseAmountAdjust.getAdjustSta())){
			List<User> users=systemService.findUserByPermission("psi:purchaseAdjust:review");
			String email="";
			for(User user:users){
				email+=user.getEmail()+",";
			}
			if(StringUtils.isNotEmpty(email)){
				email = email.substring(0, email.length()-1);
				String subject="采购金额调整已申请，请尽快审核！";
				String content = "Hi,<br/>采购金额调整单：<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPurchaseAmountAdjust/review?id="+purchaseAmountAdjust.getId()+"'>"+purchaseAmountAdjust.getOrderNo()+"</a>已申请审核调整价格，请及时审核!";
				this.sendNoticeEmail(email, content, subject, UserUtils.getUser().getEmail(), null);
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void save(LcPurchaseAmountAdjust purchaseAmountAdjust) {
		purchaseAmountAdjustDao.save(purchaseAmountAdjust);
	}
	
	public List<LcPurchaseAmountAdjust> findAdjustOrders(Set<Integer> orderIds) {
		DetachedCriteria dc = purchaseAmountAdjustDao.createDetachedCriteria();
		dc.add(Restrictions.in("orderId",orderIds));
		dc.add(Restrictions.eq("adjustSta", "r"));
		dc.addOrder(Order.desc("id"));
		return purchaseAmountAdjustDao.find(dc);
	}
	
	public List<LcPurchaseAmountAdjust> findAdjustOrders(Integer supplierId,Integer paymentId,String sta,String currency) {
		
		DetachedCriteria dc = purchaseAmountAdjustDao.createDetachedCriteria();
		dc.add(Restrictions.eq("supplier.id",supplierId));
		if(paymentId!=null){
			dc.add(Restrictions.eq("paymentId",paymentId));
		}
		if(StringUtils.isNotEmpty(sta)){
			dc.add(Restrictions.eq("adjustSta", sta));
		}
		if(StringUtils.isNotEmpty(currency)){
			dc.add(Restrictions.eq("currency", currency));
		}
		dc.addOrder(Order.desc("id"));
		return purchaseAmountAdjustDao.find(dc);
	}
	
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
