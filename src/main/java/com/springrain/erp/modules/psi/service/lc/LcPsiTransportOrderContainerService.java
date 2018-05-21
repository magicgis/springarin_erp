/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.lc;

import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.lc.LcPsiTransportOrderContainerDao;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderContainer;

/**
 * 运单集装箱明细表Service
 * @author Michael
 * @version 2015-01-15
 */
@Component
@Transactional(readOnly = true)
public class LcPsiTransportOrderContainerService extends BaseService {
	@Autowired
	private LcPsiTransportOrderContainerDao psiTransportOrderContainerDao;
	
	
	
	public LcPsiTransportOrderContainer get(Integer id) {
		return psiTransportOrderContainerDao.get(id);
	}
	
	public Page<LcPsiTransportOrderContainer> find(Page<LcPsiTransportOrderContainer> page, LcPsiTransportOrderContainer psiTransportOrderContainer) {
		DetachedCriteria dc = psiTransportOrderContainerDao.createDetachedCriteria();
		return psiTransportOrderContainerDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(LcPsiTransportOrderContainer psiTransportOrderContainer) {
		psiTransportOrderContainerDao.save(psiTransportOrderContainer);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiTransportOrderContainerDao.deleteById(id);
	}
	
	/**
	 *根据itemId获取list信息 
	 */
	public List<LcPsiTransportOrderContainer> getTransportContainerItems(Set<Integer> ids){
		DetachedCriteria dc = psiTransportOrderContainerDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		return psiTransportOrderContainerDao.find(dc);
	}
	
}
