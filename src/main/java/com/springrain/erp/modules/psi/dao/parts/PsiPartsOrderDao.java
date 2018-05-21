/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.dao.parts;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrder;

/**
 * 产品配件DAO接口
 * @author Michael
 * @version 2015-06-02
 */
@Repository
public class PsiPartsOrderDao extends BaseDao<PsiPartsOrder> {
	
	
	public void  updateOrderSta(Integer orderId,String sta){
		Parameter parameter =new Parameter(orderId,sta);
		this.updateBySql("update psi_parts_order set order_sta=:p2 where id = :p1 ", parameter);
	}
	
	public void  updateOrderStaAndFinishedDate(Integer orderId,String sta,Date finishedDate){
		Parameter parameter =new Parameter(orderId,sta,finishedDate);
		this.updateBySql("update psi_parts_order set order_sta=:p2 , receive_finished_date=:p3 where id = :p1 ", parameter);
	}
	
	// 根据产品订单id 取消所有配件订单  
	public void  updateCancelStaByOrderID(Integer productOrderId,String sta){
		Parameter parameter =new Parameter(productOrderId,sta);
		this.updateBySql("update psi_parts_order set order_sta=:p2 where purchase_order_id = :p1 ", parameter);
	}
	
	public void updateOrderStaByPurchaseOrderId(Integer purchaseOrderId,String sta){
		String updateSql="";
		if("5".equals(sta)){
			updateSql="UPDATE psi_parts_order AS a SET a.order_sta= CASE WHEN a.`payment_sta`='2' THEN '7' ELSE :p2 END WHERE a.purchase_order_id=:p1";
		}else{
			updateSql="UPDATE psi_parts_order AS a SET a.order_sta= :p2  WHERE a.purchase_order_id=:p1";
		}
		
		this.updateBySql(updateSql, new Parameter(purchaseOrderId,sta));
	}  
	
	/**
	 *更新预支付金额
	 * 
	 */
	public  void  updatePrePaymentAmount(Integer partsOrderId,Float preAmount,String flag){
		String sql="";
		if("cancel".equals(flag)){
			sql ="UPDATE psi_parts_order AS a SET a.`pre_payment_amount`=(a.`pre_payment_amount`-:p2) WHERE a.`id`=:p1 ";
		}else{
			sql ="UPDATE psi_parts_order AS a SET a.`pre_payment_amount`=(a.`pre_payment_amount`+:p2) WHERE a.`id`=:p1 ";
		}
		
		this.updateBySql(sql,new Parameter(partsOrderId,preAmount));
	}
	
}
