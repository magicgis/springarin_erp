/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.dao.lc;

import org.springframework.stereotype.Repository;

import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportPayment;

/**
 * 运单付款表DAO接口
 * @author Michael
 * @version 2015-01-21
 */
@Repository
public class LcPsiTransportPaymentDao extends BaseDao<LcPsiTransportPayment> {
	public void deleteOrderItem(Integer itemId) {
		Parameter parameter =new Parameter(itemId);
		this.updateBySql("update lc_psi_transport_payment_item set del_flag='1' where id =:p1", parameter);
	}
	
}
