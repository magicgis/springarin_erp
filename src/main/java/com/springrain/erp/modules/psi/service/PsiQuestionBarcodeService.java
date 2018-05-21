/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.PsiQuestionBarcodeDao;
import com.springrain.erp.modules.psi.entity.PsiQuestionBarcode;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * Service
 * @author Michael
 * @version 2015-06-01
 */
@Component
@Transactional(readOnly = true)
public class PsiQuestionBarcodeService extends BaseService {
	@Autowired
	private PsiQuestionBarcodeDao 			psiQuestionBarcodeDao;
	
	public PsiQuestionBarcode get(Integer id) {
		return psiQuestionBarcodeDao.get(id);
	} 
	    
	public Page<PsiQuestionBarcode> find(Page<PsiQuestionBarcode> page, PsiQuestionBarcode psiQuestionBarcode) {
		DetachedCriteria dc = psiQuestionBarcodeDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiQuestionBarcode.getProductName())){
			dc.add(Restrictions.or(Restrictions.like("productName", "%"+psiQuestionBarcode.getProductName()+"%"),Restrictions.like("productName", "%"+psiQuestionBarcode.getProductName()+"%")));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("id"));
		return psiQuestionBarcodeDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiQuestionBarcode psiQuestionBarcode) {
		if(StringUtils.isNotEmpty(psiQuestionBarcode.getProductNameTemp())){
			String arr[] = psiQuestionBarcode.getProductNameTemp().split(",");
			psiQuestionBarcode.setProductId(Integer.parseInt(arr[0]));
			psiQuestionBarcode.setProductName(arr[1]);
		}
		if(psiQuestionBarcode.getId()==null){
			psiQuestionBarcode.setCreateDate(new Date());
			psiQuestionBarcode.setCreateUser(UserUtils.getUser());
			psiQuestionBarcode.setDelFlag("0");
		}else{
			psiQuestionBarcode.setUpdateDate(new Date());
			psiQuestionBarcode.setUpdateUser(UserUtils.getUser());
		}
		psiQuestionBarcodeDao.save(psiQuestionBarcode);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		PsiQuestionBarcode psiQuestionBarcode=this.get(id);
		psiQuestionBarcode.setDelFlag("1");
		psiQuestionBarcode.setUpdateDate(new Date());
		psiQuestionBarcode.setUpdateUser(UserUtils.getUser());
		psiQuestionBarcodeDao.save(psiQuestionBarcode);
	}
	
	  
	
			
		/**
		 * 获取所有产品
		 * 
		 */
		public Map<String,String> getMapByProAttr(){
			Map<String,String> res=Maps.newHashMap();
			String sql="SELECT CASE WHEN a.color='' THEN a.product_name ELSE CONCAT(a.product_name,'_',a.color) END AS proColor,a.product_id  FROM psi_product_attribute AS a WHERE a.`del_flag`='0' GROUP BY a.`product_id`";
			List<Object[]> objs=this.psiQuestionBarcodeDao.findBySql(sql);
			if(objs!=null&&objs.size()>0){
				for(Object[] obj:objs){
					res.put((obj[1]+","+obj[0]), obj[0].toString());
				}
			}
			return res;
		}
			
}
