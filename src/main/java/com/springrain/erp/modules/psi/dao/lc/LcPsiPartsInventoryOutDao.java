/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.dao.lc;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsInventoryOut;

/**
 * 配件出库DAO接口
 * @author Michael
 * @version 2015-07-16
 */
@Repository
public class LcPsiPartsInventoryOutDao extends BaseDao<LcPsiPartsInventoryOut> {   
	//增加一条可收货信息
	public void insertProductCanDeliveryCheck(Integer purchaseOrderId,Integer productId,Integer quantity,String color){
		String sql="INSERT INTO lc_psi_parts_delivery_check(purchase_order_id,product_id,can_lading_quantity,color) VALUES (:p1,:p2,:p3,:p4)";
		this.updateBySql(sql, new Parameter(purchaseOrderId,productId,quantity,color));
	}
	
	//增加产品可收货数量
	public void addProductCanDeliveryQuantity(Integer purchaseOrderId,Integer productId,Integer quantity,String color){
		String sql="UPDATE lc_psi_parts_delivery_check AS a SET a.`can_lading_quantity`=(a.`can_lading_quantity`+:p3) WHERE a.`purchase_order_id`=:p1 AND a.`product_id`=:p2 AND a.color=:p4";
		this.updateBySql(sql, new Parameter(purchaseOrderId,productId,quantity,color));
	}
	
	//减少产品可收货数量
	public void minusProductCanDeliveryQuantity(Integer purchaseOrderId,Integer productId,Integer quantity,String color){
		String sql="UPDATE lc_psi_parts_delivery_check AS a SET a.`can_lading_quantity`=(a.`can_lading_quantity`-:p3) WHERE a.`purchase_order_id`=:p1 AND a.`product_id`=:p2 AND a.color=:p4";
		this.updateBySql(sql, new Parameter(purchaseOrderId,productId,quantity,color));
	}
	 
	//查询某产品某订单可收货数
	public Integer getCanDeliveryQuantity(Integer purchaseOrderId,Integer productId,String color){
		String sql="SELECT a.`can_lading_quantity` FROM  lc_psi_parts_delivery_check AS a WHERE a.`purchase_order_id`=:p1 AND a.`product_id`=:p2 AND a.color=:p3";
		List<Integer> list=this.findBySql(sql, new Parameter(purchaseOrderId,productId,color));
		if(list!=null&&list.size()==1){
			return list.get(0);
		}else{
			return null;
		}
	}
	
}
