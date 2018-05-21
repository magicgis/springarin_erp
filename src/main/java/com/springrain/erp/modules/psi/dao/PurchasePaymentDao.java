/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.entity.PurchasePayment;

/**
 * 采购付款DAO接口
 * @author Michael
 * @version 2014-11-21
 */
@Repository
public class PurchasePaymentDao extends BaseDao<PurchasePayment> {
	
	public void deleteOrderItem(Integer itemId) {
		Parameter parameter =new Parameter(itemId);
		this.updateBySql("update psi_purchase_payment_item set del_flag='1' where id =:p1", parameter);
	}
	
	//查出付款部分支付的
	public Map<Integer,BigDecimal> getPartsPayment(){
		Map<Integer,BigDecimal> map = Maps.newHashMap();
		String getFinalSql="SELECT a.`supplier_id`, SUM( CASE WHEN a.currency_type='CNY' THEN a.`real_payment_amount`/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+" ELSE a.`real_payment_amount` END  ) FROM psi_purchase_payment AS a WHERE a.`payment_sta`='1'  AND a.`real_payment_amount`>0 group by a.`supplier_id`";
		List<Object[]> list=this.findBySql(getFinalSql);
		for(Object[] obj:list){
			map.put(Integer.parseInt(obj[0].toString()), new BigDecimal(obj[1].toString()));
		}
		return map;
	}
	
	//查出付款部分支付的
		public Float getPartsPayment(Integer supplierId){
			String getFinalSql="SELECT SUM( CASE WHEN a.currency_type='CNY' THEN a.`real_payment_amount`/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+" ELSE a.`real_payment_amount` END  ) FROM psi_purchase_payment AS a WHERE a.`payment_sta`='1' AND a.`supplier_id`=:p1 AND a.`real_payment_amount`>0";
			List<BigDecimal> list=this.findBySql(getFinalSql, new Parameter(supplierId));
			if(list.size()>0&&list.get(0)!=null){
				return list.get(0).floatValue();
			}else{
				return 0f;
			}
		}
	
}
