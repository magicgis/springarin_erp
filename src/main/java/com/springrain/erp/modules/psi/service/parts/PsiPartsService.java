/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.parts;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsDao;
import com.springrain.erp.modules.psi.entity.parts.PsiParts;
import com.springrain.erp.modules.psi.service.PsiProductPartsService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 产品配件Service
 * @author Michael
 * @version 2015-06-01
 */
@Component
@Transactional(readOnly = true)
public class PsiPartsService extends BaseService {
	@Autowired
	private PsiPartsDao 			psiPartsDao;
	@Autowired
	private PsiProductPartsService  productPartsService;
	
	public PsiParts get(Integer id) {
		return psiPartsDao.get(id);
	} 
	    
	public Page<PsiParts> find(Page<PsiParts> page, PsiParts psiParts) {
		DetachedCriteria dc = psiPartsDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiParts.getPartsName())){
			dc.add(Restrictions.or(Restrictions.like("partsName", "%"+psiParts.getPartsName()+"%")));
		}
		if(StringUtils.isNotEmpty(psiParts.getPartsType())){
			dc.add(Restrictions.eq("partsType", psiParts.getPartsType()));
		}
		if(psiParts.getSupplier()!=null&&psiParts.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier.id", psiParts.getSupplier().getId()));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("id"));
		return psiPartsDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiParts psiParts,MultipartFile[] attchmentFiles,MultipartFile imagePeview) {
		if (imagePeview!=null&&imagePeview.getSize() != 0) {
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir()+"psiparts";
			File baseDir = new File(baseDirStr);
			if (!baseDir.isDirectory()){
				baseDir.mkdirs();
			}
			String name = imagePeview.getOriginalFilename();
			String suffix = name.substring(name.lastIndexOf("."));
			name = psiParts.getPartsName().replace("amp;", "") + suffix;
			try {
				File imageFile = new File(baseDir, name);
				FileUtils.copyInputStreamToFile(imagePeview.getInputStream(),imageFile);
				logger.info("图片保存成功:" + imageFile.getAbsolutePath());
				psiParts.setImage(Global.getCkBaseDir()+"psiparts/"+ name);
			} catch (IOException e) {
				logger.warn(name + "文件保存失败", e);
			}
		}
		
		for (MultipartFile attchmentFile : attchmentFiles) {
			if(attchmentFile.getSize()!=0){
				String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiParts";
				File baseDir = new File(baseDirStr+"/"+psiParts.getPartsName()); 
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				String name=attchmentFile.getOriginalFilename();
				File dest = new File(baseDir,name);
				try {
					FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
					psiParts.setAttchmentPathAppend("/psi/psiParts/"+psiParts.getPartsName()+"/"+name);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
		}
		
		//如果价格变动，保存备注
		String changeLog="";
		if(psiParts.getOldPrice()!=null&&Float.floatToIntBits(psiParts.getOldPrice())!=Float.floatToIntBits(psiParts.getPrice())){
			changeLog+="美元改前价格："+psiParts.getOldPrice()+",改后："+psiParts.getPrice();
		}
		
		if(psiParts.getOldRmbPrice()!=null&&Float.floatToIntBits(psiParts.getOldRmbPrice())!=Float.floatToIntBits(psiParts.getRmbPrice())){
			changeLog+="人民币改前价格："+psiParts.getOldRmbPrice()+",改后："+psiParts.getRmbPrice();
		}
		
		if(StringUtils.isNotEmpty(changeLog)){
			changeLog+="<br/>";
			psiParts.setPriceChangeLog(UserUtils.getUser().getLoginName()+"修改价格，"+changeLog+psiParts.getPriceChangeLog().replace("&gt;", ">").replace("&lt;", "<"));
		}
		if(psiParts.getId()==null){
			psiParts.setCreateDate(new Date());
			psiParts.setCreateUser(UserUtils.getUser());
		}else{
			psiParts.setUpdateDate(new Date());
			psiParts.setUpdateUser(UserUtils.getUser());
		}
		psiPartsDao.save(psiParts);
	}
	
	public Map<Integer,PsiParts> getPartsBySupplierId(Integer supplierId){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<Integer,PsiParts> partsMap = Maps.newHashMap();
		String sql ="SELECT a.id,a.`parts_name`,a.produce_period FROM psi_parts AS a WHERE a.`supplier_id`=:p1 AND a.`del_flag`='0'";
		List<Object[]> list=this.psiPartsDao.findBySql(sql,new Parameter(supplierId));
		if(list.size()>0){
			for(Object[] obj:list){   
				partsMap.put((Integer)obj[0],new PsiParts((Integer)obj[0],(String)obj[1],sdf.format(DateUtils.addDays(new Date(),obj[2]!=null?(Integer)obj[2]:0))));
			}
		}
		return partsMap;
	}
	
	
	/**
	 *查询产品交货日期 
	 */
	public Map<Integer,String> getAllReceivedDate(){
		String skuSql ="SELECT a.id,a.`produce_period` FROM psi_parts AS a WHERE a.`del_flag`='0'";
		List<Object[]> periods=this.psiPartsDao.findBySql(skuSql); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<Integer,String> periodMap = Maps.newHashMap();
		for(Object[] period:periods){
			Integer id = Integer.parseInt(period[0].toString());
			Integer p =period[1]!=null?Integer.parseInt(period[1].toString()):0;
			
			periodMap.put(id, sdf.format(DateUtils.addDays(new Date(), p)));
		}
		return periodMap;
	}
	
	public Map<Integer,PsiParts> getPartsByIds(Set<Integer> partsId){
		Map<Integer,PsiParts> partsMap =Maps.newHashMap();
		DetachedCriteria dc = psiPartsDao.createDetachedCriteria();
		if(partsId!=null){
			dc.add(Restrictions.in("id", partsId));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiParts> list= psiPartsDao.find(dc);
		if(list.size()>0){
			for(PsiParts parts:list){
				partsMap.put(parts.getId(),parts);
			}
		}
		return partsMap;
	}
	
	
	public Map<Integer,PsiParts> getPartsByIdsJson(){
		Map<Integer,PsiParts> partsMap =Maps.newHashMap();
		DetachedCriteria dc = psiPartsDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiParts> list= psiPartsDao.find(dc);
		if(list.size()>0){
			for(PsiParts parts:list){
				partsMap.put(parts.getId(),new PsiParts(parts.getId(), parts.getPartsName(), null));
			}
		}
		return partsMap;
	}
	
	public Float getPartsPrice(Integer partsId,String currency){
		String sql ="SELECT b.`price`,b.`rmb_price` FROM psi_parts AS b WHERE  b.id=:p1 ";
		List<Object[]> list=this.psiPartsDao.findBySql(sql,new Parameter(partsId));
		Float price =null;   
		if(list.size()==1){   
			Object[] obj=list.get(0);
			if("USD".equals(currency)&&obj[0]!=null){
				price=Float.parseFloat(obj[0].toString());
			}else if("CNY".equals(currency)&&obj[1]!=null){
				price=Float.parseFloat(obj[1].toString());
			}
		}
		return price;
	}
	
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		//根据配件id 删除配件与产品的关系
		this.productPartsService.deleteByPartsId(id);
		psiPartsDao.deleteById(id);
	}
	
	public List<PsiParts> findAllParts() {
		DetachedCriteria dc = psiPartsDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("id"));
		return psiPartsDao.find(dc);
	}
	public String getPartsData(){
		List<PsiParts> parts= this.findAllParts();
		StringBuilder res= new StringBuilder("[");
		if(parts.size()>0){
			for(PsiParts part:parts){
				res.append("{\"partsId\":\"").append(part.getId()).append("\",\"partsName\":\"").append(part.getPartsName()).append("\"},");
			}
			res=new StringBuilder(res.substring(0, res.length()-1)).append("]");
		}else{
			res.append("]");
		}
		return res.toString();
	}
	
	public boolean isExistName(String partsName ,Integer id){
		String sql="SELECT a.`id` FROM psi_parts AS a WHERE a.`parts_name`=:p1";
		List<Integer> list = null;
		if(id==null){
			list=this.psiPartsDao.findBySql(sql, new Parameter(partsName));
		}else{
			 sql="SELECT a.`id` FROM psi_parts AS a WHERE a.`parts_name`=:p1 AND id <>:p2";
			list=this.psiPartsDao.findBySql(sql, new Parameter(partsName,id));
		}
		if(list!=null&&list.size()>0){
			return true;
		}else{
			return false;
		}
	}
}
