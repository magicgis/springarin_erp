/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.dao.parts;

import org.springframework.stereotype.Repository;

import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrderItem;

/**
 * 产品配件DAO接口
 * @author Michael
 * @version 2015-06-02
 */
@Repository
public class PsiPartsOrderItemDao extends BaseDao<PsiPartsOrderItem> {

	public void updateReceivedQuantity(Integer orderId,Integer productId,Integer receivedQuantity){
		String updateSql="UPDATE psi_parts_order_item AS a SET a.`quantity_received`=(a.`quantity_received`+:p3) WHERE a.`parts_order_id` in (SELECT b.`id` FROM psi_parts_order AS b WHERE b.`purchase_order_id`=:p1) AND a.`product_id`=:p2 ";
		this.updateBySql(updateSql, new Parameter(orderId,productId,receivedQuantity));
	}  
	
	
}
