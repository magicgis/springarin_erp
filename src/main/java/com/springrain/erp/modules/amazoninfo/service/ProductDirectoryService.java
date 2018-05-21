/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.ProductDirectoryDao;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectory;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 测试检测Service
 * @author Michael
 * @version 2015-08-24
 */

@Component
@Transactional(readOnly = true)
public class ProductDirectoryService extends BaseService {

	@Autowired
	private ProductDirectoryDao productDirectoryDao;
	public ProductDirectory get(Integer id) {
		return productDirectoryDao.get(id);
	}
	
	
	public Page<ProductDirectory> find(Page<ProductDirectory> page, ProductDirectory productDirectory,String isCheck) {
		DetachedCriteria dc = productDirectoryDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(productDirectory.getDirectorySta())){
			dc.add(Restrictions.eq("directorySta", productDirectory.getDirectorySta()));
		}
		if(StringUtils.isNotEmpty(productDirectory.getCountry())){
			dc.add(Restrictions.eq("country", productDirectory.getCountry()));
		}
		if(StringUtils.isNotEmpty(productDirectory.getUrl())){
			dc.add(Restrictions.or(Restrictions.like("url", "%"+productDirectory.getUrl()+"%"),Restrictions.like("subject", "%"+productDirectory.getUrl()+"%")));
		}
		if("1".equals(isCheck)){
			dc.add(Restrictions.eq("createUser.id",UserUtils.getUser().getId()));
		}
		page.setOrderBy("id desc");
		return productDirectoryDao.find(page, dc);
	}
	
	public List<ProductDirectory> find() {
		DetachedCriteria dc = productDirectoryDao.createDetachedCriteria();
		dc.add(Restrictions.eq("directorySta","0"));
		dc.addOrder(Order.desc("id"));
		return productDirectoryDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void save(ProductDirectory productDirectory) {
		if(productDirectory.getId()==null){
			productDirectory.setDirectorySta("0");
			productDirectory.setCreateDate(new Date());
			productDirectory.setCreateUser(UserUtils.getUser());
		}else{
			productDirectory.setUpdateDate(new Date());
			productDirectory.setCreateUser(UserUtils.getUser());
		}
		String country ="";
		String suffix = productDirectory.getUrl().split("/")[2].replace("www.amazon.", "").replace("\n", "");
		if("co.uk,co.jp".contains(suffix)){
			country = suffix.replace("co.", "");
		}else if("com.mx".equals(suffix)){
			country = "mx";
		}else{
			country=suffix;
		}
		productDirectory.setCountry(country);
		productDirectoryDao.save(productDirectory);
	}
	
	//更新状态
	@Transactional(readOnly = false)
	public void updateSta(Integer id,String sta){
		String sql="UPDATE amazoninfo_directory AS a SET a.`directory_sta`=:p2 Where a.`id`=:p1";
		this.productDirectoryDao.updateBySql(sql, new Parameter(id,sta));
	}
	
	//解冻
	@Transactional(readOnly = false)
	public void unLock(Integer id){
		String sql="UPDATE amazoninfo_directory AS a Set a.`lock_sta`='0',a.`active_date`=CURDATE() Where a.`id`=:p1";
		this.productDirectoryDao.updateBySql(sql, new Parameter(id));
	}
	
	//系统自动更新目录冻结状态
	@Transactional(readOnly = false)
	public void updateLockStaAuto(){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String sql="UPDATE amazoninfo_directory AS a SET a.`lock_sta`='1' WHERE  a.`active_date`<=:p1 AND a.`lock_sta`='0'";
			this.productDirectoryDao.updateBySql(sql, new Parameter(DateUtils.addDays(sdf.parse(sdf.format(new Date())), -7)));
		} catch (Exception e) {}
	}
	
	public boolean isExistUrl(String url ,Integer id){
		String sql="SELECT a.`id` FROM amazoninfo_directory AS a WHERE a.`url`=:p1";
		List<Integer> list = null;
		if(id==null){
			list=this.productDirectoryDao.findBySql(sql, new Parameter(url));
		}else{
			 sql="SELECT a.`id` FROM amazoninfo_directory AS a WHERE a.`url`=:p1 AND id <>:p2";
			list=this.productDirectoryDao.findBySql(sql, new Parameter(url,id));
		}
		if(list!=null&&list.size()>0){
			return true;
		}else{
			return false;
		}
	}
	
}
