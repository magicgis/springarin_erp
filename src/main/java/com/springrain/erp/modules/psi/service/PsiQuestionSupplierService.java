/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.modules.psi.dao.PsiQuestionSupplierDao;
import com.springrain.erp.modules.psi.entity.PsiQuestionSupplier;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * Service
 * @author Michael
 * @version 2015-06-01
 */
@Component
@Transactional(readOnly = true)
public class PsiQuestionSupplierService extends BaseService {
	@Autowired
	private PsiQuestionSupplierDao 			psiQuestionSupplierDao;
	
	public PsiQuestionSupplier get(Integer id) {
		return psiQuestionSupplierDao.get(id);
	} 
	    
	public Page<PsiQuestionSupplier> find(Page<PsiQuestionSupplier> page, PsiQuestionSupplier psiQuestionSupplier) {
		DetachedCriteria dc = psiQuestionSupplierDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiQuestionSupplier.getEvent())){
			dc.add(Restrictions.or(Restrictions.like("event", "%"+psiQuestionSupplier.getEvent()+"%"),
					Restrictions.like("consequence", "%"+psiQuestionSupplier.getConsequence()+"%"),
					Restrictions.like("deal", "%"+psiQuestionSupplier.getDeal()+"%"),
					Restrictions.like("punishment", "%"+psiQuestionSupplier.getPunishment()+"%")));
		}
		if(psiQuestionSupplier.getSupplier()!=null&&psiQuestionSupplier.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier.id", psiQuestionSupplier.getSupplier().getId()));
		}
		if(StringUtils.isNotEmpty(psiQuestionSupplier.getOrderNo())){
			dc.add(Restrictions.eq("orderNo", psiQuestionSupplier.getOrderNo()));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		if(StringUtils.isEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("id"));
		}
		return psiQuestionSupplierDao.find(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public void save(PsiQuestionSupplier psiQuestionSupplier,MultipartFile[] attchmentFiles) {
		
		for (MultipartFile attchmentFile : attchmentFiles) {
			if(attchmentFile.getSize()!=0){
				String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiQuestionSupplier";
				File baseDir = new File(baseDirStr); 
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				//String name=attchmentFile.getOriginalFilename();
				String name=UUID.randomUUID().toString();
				String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf(".")); 
				File dest = new File(baseDir,name+suffix);
				try {
					FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
					psiQuestionSupplier.setFilePathAppend("/psi/psiQuestionSupplier/"+name+suffix);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
		}
		
		
		if(psiQuestionSupplier.getId()==null){
			psiQuestionSupplier.setCreateDate(new Date());
			if(psiQuestionSupplier.getCreateUser() == null){
		         psiQuestionSupplier.setCreateUser(UserUtils.getUser());
			}
			psiQuestionSupplier.setDelFlag("0");
		}else{
			psiQuestionSupplier.setUpdateDate(new Date());
			psiQuestionSupplier.setUpdateUser(UserUtils.getUser());
		}
//		if(psiQuestionSupplier.getProduct()==null||psiQuestionSupplier.getProduct().getId()==null){
//			psiQuestionSupplier.setProduct(null);
//		}
		
		if(psiQuestionSupplier.getSupplier()==null||psiQuestionSupplier.getSupplier().getId()==null){
			psiQuestionSupplier.setSupplier(null);
		}
		psiQuestionSupplierDao.save(psiQuestionSupplier);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		PsiQuestionSupplier psiQuestionSupplier=this.get(id);
		psiQuestionSupplier.setDelFlag("1");
		psiQuestionSupplier.setUpdateDate(new Date());
		psiQuestionSupplier.setUpdateUser(UserUtils.getUser());
		psiQuestionSupplierDao.save(psiQuestionSupplier);
	}

	public int getCountByOrderNo(String orderNo) {
		DetachedCriteria dc = psiQuestionSupplierDao.createDetachedCriteria();
		dc.add(Restrictions.eq("orderNo", orderNo));
		return psiQuestionSupplierDao.find(dc).size();
	}
	
			
}
