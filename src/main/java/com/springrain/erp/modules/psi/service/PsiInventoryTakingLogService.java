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
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.modules.psi.dao.PsiInventoryTakingLogDao;
import com.springrain.erp.modules.psi.entity.PsiInventoryTakingLog;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * Service
 * @author Michael
 * @version 2015-06-01
 */
@Component
@Transactional(readOnly = true)
public class PsiInventoryTakingLogService extends BaseService {
	@Autowired
	private PsiInventoryTakingLogDao 			psiInventoryTakingLogDao;
	
	public PsiInventoryTakingLog get(Integer id) {
		return psiInventoryTakingLogDao.get(id);
	} 
	    
	public Page<PsiInventoryTakingLog> find(Page<PsiInventoryTakingLog> page, PsiInventoryTakingLog psiInventoryTakingLog) {
		DetachedCriteria dc = psiInventoryTakingLogDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiInventoryTakingLog.getResult())){
			dc.add(Restrictions.or(Restrictions.like("result", "%"+psiInventoryTakingLog.getResult()+"%"),Restrictions.like("remark", "%"+psiInventoryTakingLog.getRemark()+"%")));
		}
		
		if (psiInventoryTakingLog.getCreateDate()!=null){
			dc.add(Restrictions.or(Restrictions.ge("createDate",psiInventoryTakingLog.getCreateDate()),Restrictions.ge("createDate",psiInventoryTakingLog.getCreateDate())));
		}
		if (psiInventoryTakingLog.getTakingDate()!=null){
			dc.add(Restrictions.or(Restrictions.le("createDate",DateUtils.addDays(psiInventoryTakingLog.getTakingDate(),1)),Restrictions.le("createDate",DateUtils.addDays(psiInventoryTakingLog.getTakingDate(),1))));
		}
		
		dc.add(Restrictions.eq("delFlag", "0"));
		if(StringUtils.isEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("id"));
		}
		return psiInventoryTakingLogDao.find(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public void save(PsiInventoryTakingLog psiInventoryTakingLog,MultipartFile[] attchmentFiles) {
		
		for (MultipartFile attchmentFile : attchmentFiles) {
			if(attchmentFile.getSize()!=0){
				String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiInventoryTakingLog";
				File baseDir = new File(baseDirStr); 
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				String name=UUID.randomUUID().toString();
				String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf(".")); 
				File dest = new File(baseDir,name+suffix);
				try {
					FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
					psiInventoryTakingLog.setFilePathAppend("/psi/psiInventoryTakingLog/"+name+suffix);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
		}
		
		if(psiInventoryTakingLog.getId()==null){
			psiInventoryTakingLog.setCreateDate(new Date());
			psiInventoryTakingLog.setCreateUser(UserUtils.getUser());
			psiInventoryTakingLog.setDelFlag("0");
		}
		psiInventoryTakingLogDao.save(psiInventoryTakingLog);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		PsiInventoryTakingLog psiInventoryTakingLog=this.get(id);
		psiInventoryTakingLog.setDelFlag("1");
		psiInventoryTakingLogDao.save(psiInventoryTakingLog);
	}

			
}
