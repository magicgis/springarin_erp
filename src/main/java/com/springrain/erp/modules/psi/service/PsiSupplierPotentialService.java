package com.springrain.erp.modules.psi.service;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.PsiSupplierDao;
import com.springrain.erp.modules.psi.dao.PsiSupplierPotentialDao;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PsiSupplierPotential;
import com.springrain.erp.modules.psi.entity.PurchaseOrder;

/**
 *	进销存供应商Service
 */
@Component
@Transactional(readOnly = true)
public class PsiSupplierPotentialService extends BaseService {
	
	@Autowired
	private PsiSupplierPotentialDao psiSupplierPotentialDao;
	@Autowired
	private PsiSupplierDao          psiSupplierDao;
	
	public Page<PsiSupplierPotential> find(Page<PsiSupplierPotential> page, PsiSupplierPotential supplier) {
		DetachedCriteria dc = psiSupplierPotentialDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(supplier.getName())) {
			dc.add(Restrictions.or(Restrictions.like("name", "%" + supplier.getName() + "%"),Restrictions.like("nikename", "%" + supplier.getName() + "%")));
		}
		if(StringUtils.isNotEmpty(supplier.getType())){
			dc.add(Restrictions.eq("type", supplier.getType()));
		}
		if(StringUtils.isNotEmpty(supplier.getCurrencyType())){
			dc.add(Restrictions.eq("currencyType", supplier.getCurrencyType()));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiSupplierPotentialDao.find(page, dc);
	}
	
	
	/**
	 * 获取产品供应商
	 * @return
	 */
	public List<PsiSupplierPotential> findAll() {
		DetachedCriteria dc = psiSupplierPotentialDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", "0"));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiSupplierPotentialDao.find(dc);
	}
	
	public List<PsiSupplierPotential> findAll(PurchaseOrder purchaseOrder) {
		DetachedCriteria dc = psiSupplierPotentialDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", "0"));
		dc.add(Restrictions.eq("delFlag", "0"));
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("id", purchaseOrder.getSupplier().getId()));
		}
		return psiSupplierPotentialDao.find(dc);
	}
	
	public List<PsiSupplierPotential> findAllTransporter() {
		DetachedCriteria dc = psiSupplierPotentialDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", "1"));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiSupplierPotentialDao.find(dc);
	}
	
	
	/**
	 *根据供应商类型获取供应商列表 
	 * 
	 */
	public List<PsiSupplierPotential> findSupplierByType(String[] types) {
		DetachedCriteria dc = psiSupplierPotentialDao.createDetachedCriteria();
		dc.add(Restrictions.in("type", types));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiSupplierPotentialDao.find(dc);
	}
	
	public PsiSupplierPotential get(Integer id){
		return psiSupplierPotentialDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiSupplierPotential psiSupplierPotential){
		psiSupplierPotentialDao.save(psiSupplierPotential);
	}
	@Transactional(readOnly = false)
	public void gen(PsiSupplierPotential psiSupplierPotential){
		PsiSupplier supplier = psiSupplierPotential.copyToSupplier();
		psiSupplierDao.save(supplier);
		//已生成正式供应商
		psiSupplierPotential.setCreateRegularFlag("1");
		psiSupplierPotentialDao.save(psiSupplierPotential);
	}
	
	/**
	 * 逻辑删除
	 */
	@Transactional(readOnly = false)
	public int delete(Integer id){
		return psiSupplierPotentialDao.deleteById(id);
	}
	
	public boolean nameIsExsit(String name){
		long i=0;
		DetachedCriteria dc = psiSupplierPotentialDao.createDetachedCriteria();
		dc.add(Restrictions.eq("nikename",name));
		dc.add(Restrictions.eq("delFlag", "0"));
		i+=psiSupplierPotentialDao.count(dc);
		//简称也不能与正式供应商简称相同
		DetachedCriteria dc1 = psiSupplierDao.createDetachedCriteria();
		dc1.add(Restrictions.eq("nikename",name));
		dc1.add(Restrictions.eq("delFlag", "0"));
		i+=psiSupplierPotentialDao.count(dc1);
		return i>0;
	}
	@Transactional(readOnly = false)
	public void updateSuffixName(Integer id,String suffixName) {
		Parameter parameter =new Parameter(suffixName,id);
		this.psiSupplierPotentialDao.updateBySql("update psi_supplier_potential set suffix_name=:p1 where id =:p2",parameter);
	}
	
	
	/**
	 * 根据状态查出订单
	 * 
	 */
	public Map<Integer,String> getIdNameCurrency(Map<Integer,String> refMap){
		Map<Integer,String> supplierMap=Maps.newHashMap();
		String sql ="SELECT a.`id`,a.`nikename`,a.`currency_type` FROM psi_supplier_potential AS a  WHERE (a.`type`='0' or  a.`type`='2')  AND a.`del_flag`='0'";
		List<Object[]> objects=this.psiSupplierPotentialDao.findBySql(sql);
		for(Object[] object:objects){
			supplierMap.put(Integer.parseInt(object[0].toString()), object[1].toString());
			refMap.put(Integer.parseInt(object[0].toString()), object[2].toString());
		}
		return supplierMap;
	}
	
	
	
	public boolean shortNameIsExsit(String name){
		long i=0;
		DetachedCriteria dc = psiSupplierPotentialDao.createDetachedCriteria();
		dc.add(Restrictions.eq("shortName",name));
		dc.add(Restrictions.eq("delFlag", "0"));
		i+=psiSupplierPotentialDao.count(dc);
		//简称也不能与正式供应商简称相同
		DetachedCriteria dc1 = psiSupplierDao.createDetachedCriteria();
		dc1.add(Restrictions.eq("shortName",name));
		dc1.add(Restrictions.eq("delFlag", "0"));
		i+=psiSupplierPotentialDao.count(dc1);
		return i>0;
	}

}
