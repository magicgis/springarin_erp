/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.entity.PsiTransportPaymentItem;
import com.springrain.erp.modules.psi.dao.PsiTransportPaymentItemDao;

/**
 * 运单付款表Service
 * @author Michael
 * @version 2015-01-21
 */
@Component
@Transactional(readOnly = true)
public class PsiTransportPaymentItemService extends BaseService {

	@Autowired
	private PsiTransportPaymentItemDao psiTransportPaymentItemDao;
	
	public PsiTransportPaymentItem get(Integer id) {
		return psiTransportPaymentItemDao.get(id);
	}
	
	public Page<PsiTransportPaymentItem> find(Page<PsiTransportPaymentItem> page, PsiTransportPaymentItem psiTransportPaymentItem) {
		DetachedCriteria dc = psiTransportPaymentItemDao.createDetachedCriteria();
		return psiTransportPaymentItemDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiTransportPaymentItem psiTransportPaymentItem) {
		psiTransportPaymentItemDao.save(psiTransportPaymentItem);
	}
	
	/**
	 *根据运单编号，查出所有未取消的运单项信息 
	 *
	 */
	public List<String> findPayItemByTranId(Integer tranId){
		String sql ="SELECT it.payment_type FROM psi_transport_payment_item AS it,psi_transport_payment AS p WHERE it.payment_id=p.id AND p.payment_sta <> '8' AND del_flag='0' AND  it.tran_order_id=:p1 ";
		List<String> list=this.psiTransportPaymentItemDao.findBySql(sql, new Parameter(tranId));
		return list;
	}
	
	
}
