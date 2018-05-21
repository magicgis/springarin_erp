package com.springrain.erp.modules.psi.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
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
import com.springrain.erp.modules.psi.dao.PsiSupplierDao;
import com.springrain.erp.modules.psi.dao.PsiSupplierTaxAdjustDao;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PsiSupplierTaxAdjust;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 *	供应商产品Service
 */
@Component
@Transactional(readOnly = true)
public class PsiSupplierTaxAdjustService extends BaseService {
	@Autowired
	private PsiSupplierTaxAdjustDao psiSupplierTaxAdjustDao;
	@Autowired
	private PsiSupplierDao          psiSupplierDao;
	@Autowired
	private MailManager 			mailManager;
	
	
	public Page<PsiSupplierTaxAdjust> find(Page<PsiSupplierTaxAdjust> page,PsiSupplierTaxAdjust tax) {
		DetachedCriteria dc = psiSupplierTaxAdjustDao.createDetachedCriteria();
		dc.addOrder(Order.desc("id"));
		if(tax.getSupplier()!=null&&tax.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier.id", tax.getSupplier().getId()));
		}
		
		if (tax.getCreateDate()!=null){
			dc.add(Restrictions.or(Restrictions.ge("createDate",tax.getCreateDate()),Restrictions.ge("createDate",tax.getCreateDate())));
		}
		if (tax.getReviewDate()!=null){
			dc.add(Restrictions.or(Restrictions.le("createDate",DateUtils.addDays(tax.getReviewDate(),1)),Restrictions.le("createDate",DateUtils.addDays(tax.getReviewDate(),1))));
		}
		return psiSupplierTaxAdjustDao.find(page,dc);
	}  
	
	public PsiSupplierTaxAdjust get(Integer taxId){
		return this.psiSupplierTaxAdjustDao.get(taxId);
	}
	
	/**
	 * 添加保存
	 * @throws IOException 
	 */
	@Transactional(readOnly = false)
	public void save(PsiSupplierTaxAdjust tax,MultipartFile supplierFile) throws IOException{
		
		if(supplierFile!=null&&supplierFile.getSize()!=0){
			String	filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/supplierTax";
			String suffix = supplierFile.getOriginalFilename().substring(supplierFile.getOriginalFilename().lastIndexOf("."));  
			String uuid = UUID.randomUUID().toString();
			File file1 = new File(filePath);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			File piFilePdf = new File(file1, uuid+suffix);
			FileUtils.copyInputStreamToFile(supplierFile.getInputStream(),piFilePdf);
			tax.setFilePath(Global.getCkBaseDir() + "/psi/supplierTax/"+uuid+suffix);
		}
		tax.setAdjustSta("1");
		tax.setCreateDate(new Date());
		tax.setCreateUser(UserUtils.getUser());
		this.psiSupplierTaxAdjustDao.save(tax);
		String subject="供应商"+psiSupplierDao.get(tax.getSupplier().getId()).getNikename()+"税点调整已申请";
		String content="Hi,<br/>"+subject+",<br/>请点<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/psiSupplierTaxAdjust/review?id="+tax.getId()+"'>此处</a>进行审核";
		this.sendNoticeEmail("emma.chao@inateck.com", content, subject, UserUtils.getUser().getEmail(), "");
	}
	
	/**
	 * 审核保存
	 */
	@Transactional(readOnly = false)
	public void reviewSave(PsiSupplierTaxAdjust tax){
		tax = this.psiSupplierTaxAdjustDao.get(tax.getId());
		tax.setReviewDate(new Date());
		tax.setReviewUser(UserUtils.getUser());
		tax.setAdjustSta("2");
		this.psiSupplierTaxAdjustDao.save(tax);
		
		//把供应商的税率变了
		PsiSupplier  supplier = tax.getSupplier();
		supplier.setTaxRate(tax.getTax());
		psiSupplierDao.save(supplier);
		String subject="供应商"+supplier.getNikename()+"税点调整已审核";
		String content="Hi,"+tax.getCreateUser().getName()+"<br/>"+subject+",请核实!";
		this.sendNoticeEmail(tax.getCreateUser().getEmail(), content, subject, "", "");
	}
	
	
	
	/**
	 * 取消保存
	 */
	@Transactional(readOnly = false)
	public void cancelSave(PsiSupplierTaxAdjust tax){
		tax = this.psiSupplierTaxAdjustDao.get(tax.getId());
		tax.setCancelDate(new Date());
		tax.setCancelUser(UserUtils.getUser());
		tax.setAdjustSta("8");
		this.psiSupplierTaxAdjustDao.save(tax);
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
