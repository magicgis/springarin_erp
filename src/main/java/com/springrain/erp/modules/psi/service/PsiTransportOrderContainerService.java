/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.PsiTransportOrderContainerDao;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderContainer;

/**
 * 运单集装箱明细表Service
 * @author Michael
 * @version 2015-01-15
 */
@Component
@Transactional(readOnly = true)
public class PsiTransportOrderContainerService extends BaseService {

	@Autowired
	private PsiTransportOrderContainerDao psiTransportOrderContainerDao;
	
	public PsiTransportOrderContainer get(Integer id) {
		return psiTransportOrderContainerDao.get(id);
	}
	
	public Page<PsiTransportOrderContainer> find(Page<PsiTransportOrderContainer> page, PsiTransportOrderContainer psiTransportOrderContainer) {
		DetachedCriteria dc = psiTransportOrderContainerDao.createDetachedCriteria();
		return psiTransportOrderContainerDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiTransportOrderContainer psiTransportOrderContainer) {
		psiTransportOrderContainerDao.save(psiTransportOrderContainer);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiTransportOrderContainerDao.deleteById(id);
	}
	
	/**
	 *根据itemId获取list信息 
	 */
	public List<PsiTransportOrderContainer> getTransportContainerItems(Set<Integer> ids){
		DetachedCriteria dc = psiTransportOrderContainerDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		return psiTransportOrderContainerDao.find(dc);
	}
	
}
