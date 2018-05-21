/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.dao;

import org.springframework.stereotype.Repository;

import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.modules.psi.entity.PurchaseAmountAdjust;

/**
 * 采购金额调整DAO接口
 * @author Michael
 * @version 2015-06-01
 */
@Repository
public class PurchaseAmountAdjustDao extends BaseDao<PurchaseAmountAdjust> {
	
	public void updateStaByPaymentId(Integer id,String sta ) {
		String sql ="UPDATE psi_purchase_amount_adjust AS a SET a.`adjust_sta`=:p2 WHERE a.`payment_id`=:p1";
		this.updateBySql(sql, new Parameter(id,sta));
	}
	public void updateStaPaymentIdById(Integer id,String sta ,Integer paymentId) {
		String sql ="UPDATE psi_purchase_amount_adjust AS a SET a.`adjust_sta`=:p2,a.`payment_id`=:p3 WHERE a.`id`=:p1";
		this.updateBySql(sql, new Parameter(id,sta,paymentId));
	}
}  
