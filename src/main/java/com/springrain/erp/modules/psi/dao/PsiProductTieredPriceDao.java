package com.springrain.erp.modules.psi.dao;

import org.springframework.stereotype.Repository;

import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPrice;

/**
 *产品阶梯价格
 */
@Repository
public class PsiProductTieredPriceDao extends BaseDao<PsiProductTieredPrice> {
	
	public void  deleteByProductColorSupplier(Integer productId,String color,String supplierId){
		String sql="UPDATE psi_product_tiered_price AS a SET a.`del_flag`='1' WHERE a.`product_id`=:p1 AND a.`color`=:p2 AND a.`supplier_id`=:p3 ";
		this.updateBySql(sql, new Parameter(productId,color,supplierId));
	}
	
}
