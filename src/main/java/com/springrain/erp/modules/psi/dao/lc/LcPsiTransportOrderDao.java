/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.dao.lc;

import org.springframework.stereotype.Repository;

import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;

/**
 * 运单表DAO接口
 * @author Michael
 * @version 2015-01-15
 */
@Repository
public class LcPsiTransportOrderDao extends BaseDao<LcPsiTransportOrder> {
	public void deleteOrderItem(Integer itemId) {
		Parameter parameter =new Parameter(itemId);
		this.updateBySql("update lc_psi_transport_order_item set del_flag='1' where id =:p1", parameter);
	}
	
	public void deleteConertainItem(Integer itemId) {
		Parameter parameter =new Parameter(itemId);
		this.updateBySql("update lc_psi_transport_order_container set del_flag='1' where id =:p1", parameter);
	}
	
	
	public void updateSuffixName(Integer id,String suffixName) {
		Parameter parameter =new Parameter(suffixName,id);
		this.updateBySql("update lc_psi_transport_order set suffix_name=:p1 where id =:p2", parameter);
	}
	

	public void updateElsePath(Integer id,String elsePath){
		Parameter parameter =new Parameter(elsePath,id);
		this.updateBySql("update lc_psi_transport_order set else_path=:p1 where id =:p2", parameter);
	}
	
	
	public void updateExportPath(Integer id,String exportPath){
		Parameter parameter =new Parameter(exportPath,id);
		this.updateBySql("update lc_psi_transport_order set export_invoice_path=:p1 where id =:p2", parameter);
	}
	
	
	
	public void updateTranSta(Integer id,String sta){
		Parameter parameter =new Parameter(sta,id);
		this.updateBySql("update lc_psi_transport_order set transport_sta=:p1 where id =:p2", parameter);
	}
	
}
