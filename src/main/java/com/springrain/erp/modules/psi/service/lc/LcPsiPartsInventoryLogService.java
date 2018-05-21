/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.lc;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.lc.LcPsiPartsInventoryLogDao;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsInventoryLog;

/**
 * 配件订单付款详情Service
 * @author Michael
 * @version 2015-06-29
 */
@Component
@Transactional(readOnly = true)
public class LcPsiPartsInventoryLogService extends BaseService {
	@Autowired
	private LcPsiPartsInventoryLogDao psiPartsInventoryLogDao;
	
	public LcPsiPartsInventoryLog get(Integer id) {
		return psiPartsInventoryLogDao.get(id);
	}
	
	public Page<LcPsiPartsInventoryLog> find(Page<LcPsiPartsInventoryLog> page, LcPsiPartsInventoryLog log) {
		DetachedCriteria dc = psiPartsInventoryLogDao.createDetachedCriteria();
		
		if(log.getPartsId()!=null){
			dc.add(Restrictions.eq("partsId", log.getPartsId()));
		}
		
		if(StringUtils.isNotEmpty(log.getDataType())){
			dc.add(Restrictions.eq("dataType", log.getDataType()));
		}
		
		if(StringUtils.isNotEmpty(log.getOperateType())){
			if("noChangeData".equals(log.getOperateType())){
				dc.add(Restrictions.not(Restrictions.like("operateType", "%stock%To%")));
			}else{
				dc.add(Restrictions.eq("operateType", log.getOperateType()));
			}
		}
		//dc.addOrder(Order.desc("id"));
		page.setOrderBy(" id desc");
		return psiPartsInventoryLogDao.find(page, dc);
	}   
	
}
