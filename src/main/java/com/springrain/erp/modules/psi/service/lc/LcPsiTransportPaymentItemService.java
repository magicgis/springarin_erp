/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.lc;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.lc.LcPsiTransportPaymentItemDao;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportPaymentItem;

/**
 * 运单付款表Service
 * @author Michael
 * @version 2015-01-21
 */
@Component
@Transactional(readOnly = true)
public class LcPsiTransportPaymentItemService extends BaseService {
	
	
	@Autowired
	private LcPsiTransportPaymentItemDao psiTransportPaymentItemDao;
	
	public LcPsiTransportPaymentItem get(Integer id) {
		return psiTransportPaymentItemDao.get(id);
	}
	
	public Page<LcPsiTransportPaymentItem> find(Page<LcPsiTransportPaymentItem> page, LcPsiTransportPaymentItem psiTransportPaymentItem) {
		DetachedCriteria dc = psiTransportPaymentItemDao.createDetachedCriteria();
		return psiTransportPaymentItemDao.find(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public void save(LcPsiTransportPaymentItem psiTransportPaymentItem) {
		psiTransportPaymentItemDao.save(psiTransportPaymentItem);
	}
	
	/**
	 *根据运单编号，查出所有未取消的运单项信息 
	 *
	 */
	public List<String> findPayItemByTranId(Integer tranId){
		String sql ="SELECT it.payment_type FROM lc_psi_transport_payment_item AS it,lc_psi_transport_payment AS p WHERE it.payment_id=p.id AND p.payment_sta <> '8' AND del_flag='0' AND  it.tran_order_id=:p1 ";
		List<String> list=this.psiTransportPaymentItemDao.findBySql(sql, new Parameter(tranId));
		return list;
	}
	
	
}
