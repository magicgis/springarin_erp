/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.modules.psi.dao.PsiTransportOrderReplaceDao;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderReplace;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 运单表Service
 * @author Michael
 * @version 2015-01-15
 */
@Component
@Transactional(readOnly = true)
public class PsiTransportOrderReplaceService extends BaseService {
	@Autowired
	private PsiTransportOrderReplaceDao 		psiTransportOrderReplaceDao;
	
	
	public PsiTransportOrderReplace get(Integer id) {
		return psiTransportOrderReplaceDao.get(id);
	}
	
	public Page<PsiTransportOrderReplace> find(Page<PsiTransportOrderReplace> page, PsiTransportOrderReplace psiTransportOrderReplace) {
		DetachedCriteria dc = psiTransportOrderReplaceDao.createDetachedCriteria();
		
		if(psiTransportOrderReplace.getBoxNumber()!=null&&psiTransportOrderReplace.getBoxNumber()>0){
			dc.add(Restrictions.eq("boxNumber", psiTransportOrderReplace.getBoxNumber()));
		}
		
		if(psiTransportOrderReplace.getVendor1()!=null&&psiTransportOrderReplace.getVendor1().getId()!=null){
			Integer vendorId=psiTransportOrderReplace.getVendor1().getId();
			dc.add(Restrictions.or(Restrictions.eq("vendor1.id", vendorId),Restrictions.eq("vendor2.id", vendorId),Restrictions.eq("vendor3.id", vendorId),Restrictions.eq("vendor4.id", vendorId)));
		}
		
		if(psiTransportOrderReplace.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",psiTransportOrderReplace.getCreateDate()));
		}
		
		if(psiTransportOrderReplace.getEtdDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(psiTransportOrderReplace.getEtdDate(),1)));
		}
		dc.add(Restrictions.eq("replaceSta", "1"));
		return psiTransportOrderReplaceDao.find(page, dc);
	}
	
	public List<PsiTransportOrderReplace> findList(PsiTransportOrderReplace psiTransportOrderReplace) {
		DetachedCriteria dc = psiTransportOrderReplaceDao.createDetachedCriteria();
		
		if(psiTransportOrderReplace.getBoxNumber()!=null&&psiTransportOrderReplace.getBoxNumber()>0){
			dc.add(Restrictions.eq("boxNumber", psiTransportOrderReplace.getBoxNumber()));
		}
		
		if(psiTransportOrderReplace.getVendor1()!=null&&psiTransportOrderReplace.getVendor1().getId()!=null){
			Integer vendorId=psiTransportOrderReplace.getVendor1().getId();
			dc.add(Restrictions.or(Restrictions.eq("vendor1.id", vendorId),Restrictions.eq("vendor2.id", vendorId),Restrictions.eq("vendor3.id", vendorId),Restrictions.eq("vendor4.id", vendorId)));
		}
		
		if(psiTransportOrderReplace.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",psiTransportOrderReplace.getCreateDate()));
		}
		
		if(psiTransportOrderReplace.getEtdDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(psiTransportOrderReplace.getEtdDate(),1)));
		}
		dc.add(Restrictions.eq("replaceSta", "1"));
		return psiTransportOrderReplaceDao.find(dc);
	}
	
	
	@Transactional(readOnly = false)
	public void editSaveData(PsiTransportOrderReplace psiTransportOrderReplace,MultipartFile[] localFile,MultipartFile[] tranFile,MultipartFile[] dapFile,MultipartFile[] otherFile,MultipartFile[] insuranceFile,MultipartFile[] taxFile) throws IOException {
		this.clearSupplierData(psiTransportOrderReplace);
		this.saveAttachment(psiTransportOrderReplace, localFile, tranFile, dapFile, otherFile, insuranceFile, taxFile);
		if(psiTransportOrderReplace.getId()==null){
			psiTransportOrderReplace.setCreateDate(new Date());
			psiTransportOrderReplace.setCreateUser(UserUtils.getUser());
			psiTransportOrderReplace.setReplaceSta("1");
		}
		this.psiTransportOrderReplaceDao.save(psiTransportOrderReplace);
	}
		
	
	@Transactional(readOnly = false)
	public void updateSuffixName(Integer id,String suffixName){
		this.psiTransportOrderReplaceDao.updateBySql("update psi_transport_order_replace set suffix_name=:p1 where id =:p2", new Parameter(suffixName,id));
		
	}
	
	
	public void clearSupplierData(PsiTransportOrderReplace psiTransportOrder){
		if(psiTransportOrder.getVendor1()!=null&&psiTransportOrder.getVendor1().getId()==null){
			psiTransportOrder.setVendor1(null);
		}
		if(psiTransportOrder.getVendor2()!=null&&psiTransportOrder.getVendor2().getId()==null){
			psiTransportOrder.setVendor2(null);
		}
		if(psiTransportOrder.getVendor3()!=null&&psiTransportOrder.getVendor3().getId()==null){
			psiTransportOrder.setVendor3(null);
		}
		if(psiTransportOrder.getVendor4()!=null&&psiTransportOrder.getVendor4().getId()==null){
			psiTransportOrder.setVendor4(null);
		}
		if(psiTransportOrder.getVendor5()!=null&&psiTransportOrder.getVendor5().getId()==null){
			psiTransportOrder.setVendor5(null);
		}
		if(psiTransportOrder.getVendor6()!=null&&psiTransportOrder.getVendor6().getId()==null){
			psiTransportOrder.setVendor6(null);
		}
	}
	
		//更新费用附件
		public void saveAttachment(PsiTransportOrderReplace psiTransportOrderReplace,MultipartFile[] localFile,MultipartFile[] tranFile,MultipartFile[] dapFile,MultipartFile[] otherFile,MultipartFile[] insuranceFile,MultipartFile[] taxFile){
			//判断附件
			if(localFile[0].getSize()>0){
				psiTransportOrderReplace.setLocalPath(null);//如果编辑上传了附件就把原来的清空
				for (MultipartFile attchmentFile : localFile) {
					if(attchmentFile.getSize()!=0){
						String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
						File baseDir = new File(baseDirStr+"/"+psiTransportOrderReplace.getId()); 
						if(!baseDir.isDirectory())
							baseDir.mkdirs();
						String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
						String name=UUID.randomUUID().toString()+suffix;
						File dest = new File(baseDir,name);
						try {
							FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
							psiTransportOrderReplace.setLocalPathAppend(psiTransportOrderReplace.getTransportNo()+"/"+name);
						} catch (IOException e) {
							logger.warn(name+"文件保存失败",e);
						}
					}
				}
			}
			
			if(tranFile[0].getSize()>0){
				psiTransportOrderReplace.setTranPath(null);//如果编辑上传了附件就把原来的清空
				for (MultipartFile attchmentFile : tranFile) {
					if(attchmentFile.getSize()!=0){
						String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
						File baseDir = new File(baseDirStr+"/"+psiTransportOrderReplace.getId()); 
						if(!baseDir.isDirectory())
							baseDir.mkdirs();
						String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
						String name=UUID.randomUUID().toString()+suffix;
						File dest = new File(baseDir,name);
						try {
							FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
							psiTransportOrderReplace.setTranPathAppend(psiTransportOrderReplace.getId()+"/"+name);
						} catch (IOException e) {
							logger.warn(name+"文件保存失败",e);
						}
					}
				}
			}
			
			if(dapFile[0].getSize()>0){
				psiTransportOrderReplace.setDapPath(null);//如果编辑上传了附件就把原来的清空
				for (MultipartFile attchmentFile : dapFile) {
					if(attchmentFile.getSize()!=0){
						String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
						File baseDir = new File(baseDirStr+"/"+psiTransportOrderReplace.getId()); 
						if(!baseDir.isDirectory())
							baseDir.mkdirs();
						String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
						String name=UUID.randomUUID().toString()+suffix;
						File dest = new File(baseDir,name);
						try {
							FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
							psiTransportOrderReplace.setDapPathAppend(psiTransportOrderReplace.getId()+"/"+name);
						} catch (IOException e) {
							logger.warn(name+"文件保存失败",e);
						}
					}
				}
			}
			
			
			if(otherFile[0].getSize()>0){
				psiTransportOrderReplace.setOtherPath(null);//如果编辑上传了附件就把原来的清空
				for (MultipartFile attchmentFile : otherFile) {
					if(attchmentFile.getSize()!=0){
						String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
						File baseDir = new File(baseDirStr+"/"+psiTransportOrderReplace.getId()); 
						if(!baseDir.isDirectory())
							baseDir.mkdirs();
						String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
						String name=UUID.randomUUID().toString()+suffix;
						File dest = new File(baseDir,name);
						try {
							FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
							psiTransportOrderReplace.setOtherPathAppend(psiTransportOrderReplace.getId()+"/"+name);
						} catch (IOException e) {
							logger.warn(name+"文件保存失败",e);
						}
					}
				}
			}
			
			
			if(insuranceFile[0].getSize()>0){
				psiTransportOrderReplace.setInsurancePath(null);//如果编辑上传了附件就把原来的清空
				for (MultipartFile attchmentFile : insuranceFile) {
					if(attchmentFile.getSize()!=0){
						String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
						File baseDir = new File(baseDirStr+"/"+psiTransportOrderReplace.getId()); 
						if(!baseDir.isDirectory())
							baseDir.mkdirs();
						String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
						String name=UUID.randomUUID().toString()+suffix;
						File dest = new File(baseDir,name);
						try {
							FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
							psiTransportOrderReplace.setInsurancePathAppend(psiTransportOrderReplace.getId()+"/"+name);
						} catch (IOException e) {
							logger.warn(name+"文件保存失败",e);
						}
					}
				}
			}
			
			
			if(taxFile[0].getSize()>0){
				psiTransportOrderReplace.setTaxPath(null);//如果编辑上传了附件就把原来的清空
				for (MultipartFile attchmentFile : taxFile) {
					if(attchmentFile.getSize()!=0){
						String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
						File baseDir = new File(baseDirStr+"/"+psiTransportOrderReplace.getId()); 
						if(!baseDir.isDirectory())
							baseDir.mkdirs();
						String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
						String name=UUID.randomUUID().toString()+suffix;
						File dest = new File(baseDir,name);
						try {
							FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
							psiTransportOrderReplace.setTaxPathAppend(psiTransportOrderReplace.getId()+"/"+name);
						} catch (IOException e) {
							logger.warn(name+"文件保存失败",e);
						}
					}
				}
			}
		}
} 
