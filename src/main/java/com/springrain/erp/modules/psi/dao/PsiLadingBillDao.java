/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.entity.PsiLadingBill;

/**
 * 提单明细DAO接口
 * @author Michael
 * @version 2014-11-11
 */
@Repository
public class PsiLadingBillDao extends BaseDao<PsiLadingBill> {
	
	public void deleteOrderItem(Integer itemId) {
		Parameter parameter =new Parameter(itemId);
		this.updateBySql("update psi_lading_bill_item set del_flag='1' where id =:p1", parameter);
	}
	
	//查出提单未支付完的，已支付的金额
	public Float getFinalPayment(Integer supplierId){
		String getFinalSql="SELECT SUM( CASE WHEN ps.currency_type='CNY' THEN plb.total_payment_amount/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE plb.total_payment_amount END  ) FROM psi_lading_bill AS plb,psi_supplier AS ps WHERE plb.supplier_id =ps.id AND plb.supplier_id=:p1 " +
				" AND  plb.total_amount>plb.total_payment_amount  AND plb.`bill_sta`!='2' "; 
		List<BigDecimal> list=this.findBySql(getFinalSql, new Parameter(supplierId));
		if(list.size()>0&&list.get(0)!=null){
			return list.get(0).floatValue();
		}else{
			return 0f;
		}
	}
	
}
