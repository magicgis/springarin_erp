/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.PsiInventoryRevisionLogDao;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiInventoryRevisionLog;

/**
 * 采购付款明细Service
 * @author Michael
 * @version 2014-12-24
 */
@Component
@Transactional(readOnly = true)
public class PsiInventoryRevisionLogService extends BaseService {

	@Autowired
	private PsiInventoryRevisionLogDao psiInventoryRevisionLogDao;
	
	public PsiInventoryRevisionLog get(Integer id) {
		return psiInventoryRevisionLogDao.get(id);
	}
	
	public Page<PsiInventoryRevisionLog> findBySingleProduct(Page<PsiInventoryRevisionLog> page, PsiInventoryRevisionLog psiInventoryRevisionLog,StringBuffer sb) {
		DetachedCriteria dc = psiInventoryRevisionLogDao.createDetachedCriteria();
		//dc.createAlias("this.operationUser", "operationUser");
		if(psiInventoryRevisionLog.getProductId()!=null){
			dc.add(Restrictions.eq("productId",psiInventoryRevisionLog.getProductId()));
			sb.append(" AND product_id='"+psiInventoryRevisionLog.getProductId()+"' ");
		}
		if(psiInventoryRevisionLog.getWarehouseId()!=null){
			dc.add(Restrictions.eq("warehouseId",psiInventoryRevisionLog.getWarehouseId()));
			sb.append(" AND warehouse_id='"+psiInventoryRevisionLog.getWarehouseId()+"' ");
		}
		if(psiInventoryRevisionLog.getColorCode()!=null&&!psiInventoryRevisionLog.getColorCode().equals("All")){
			dc.add(Restrictions.eq("colorCode",psiInventoryRevisionLog.getColorCode()));
			sb.append(" AND color_code='"+psiInventoryRevisionLog.getColorCode()+"' ");
		}
		if(psiInventoryRevisionLog.getCountryCode()!=null&&!psiInventoryRevisionLog.getCountryCode().equals("")){
			dc.add(Restrictions.eq("countryCode",psiInventoryRevisionLog.getCountryCode()));
			sb.append(" AND country_code='"+psiInventoryRevisionLog.getCountryCode()+"' ");
		}
		if(psiInventoryRevisionLog.getOperationType()!=null&&!psiInventoryRevisionLog.getOperationType().equals("")){
			dc.add(Restrictions.like("operationType","%"+psiInventoryRevisionLog.getOperationType()+"%"));
			//sb.append(" AND operation_type like'%"+psiInventoryRevisionLog.getOperationType()+"%'");
		}
		if(psiInventoryRevisionLog.getIsNewOperation()!=null&&!psiInventoryRevisionLog.getIsNewOperation().equals("")){
			dc.add(Restrictions.eq("dataType",psiInventoryRevisionLog.getDataType()));
			sb.append(" AND data_type='new' ");
		}
		if(psiInventoryRevisionLog.getShowFlag().equals("0")){
			dc.add(Restrictions.sqlRestriction("operation_type NOT LIKE '%\\_To\\_%' "));
			sb.append(" AND operation_type NOT LIKE'%\\_To\\_%' ");
		}else{
			dc.add(Restrictions.like("operationType","%\\_To\\_%"));
			//dc.add(Restrictions.ge("quantity", 0));
			//sb.append(" AND operation_type LIKE '%\\_To\\_%' AND quantity>0 ");
		}
		if(psiInventoryRevisionLog.getOperationUser()!=null&&psiInventoryRevisionLog.getOperationUser().getId()!=null&&!psiInventoryRevisionLog.getOperationUser().getId().equals("")){
			dc.add(Restrictions.eq("operationUser.id",psiInventoryRevisionLog.getOperationUser().getId()));
			sb.append(" AND  operation_user_id = '"+psiInventoryRevisionLog.getOperationUser().getId()+"'");
		}
		dc.addOrder(Order.desc("id"));
		return psiInventoryRevisionLogDao.find(page, dc);
	}
	
	
	public List<Object[]> getSumdata(StringBuffer whereSql,String showFlag){
		StringBuilder sqlsb= new StringBuilder("");
		if(showFlag.equals("0")){
			sqlsb.append("SELECT  a.operation_type, SUM(quantity) AS quantity FROM psi_inventory_revision_log a WHERE 1=1");
			sqlsb.append(whereSql);
			sqlsb.append(" GROUP BY a.operation_type HAVING quantity<>0");
		}else{
			sqlsb.append("SELECT  a.operation_type, SUM(quantity) AS quantity FROM psi_inventory_revision_log  a  WHERE  1=1");
			sqlsb.append(whereSql);
			sqlsb.append(" GROUP BY a.operation_type");
		}
		List<Object[]> list=this.psiInventoryRevisionLogDao.findBySql(sqlsb.toString());
		return list;
	}
	
	/**
	 *从操作表获得实时单个产品new.broken.old.renew状态 
	 * 
	 */
	public void getSumByInventory(Integer warehouseId,PsiInventory inventory){
		String sql ="SELECT  data_type,SUM(quantity) AS quantity FROM psi_inventory_revision_log  a  WHERE a.warehouse_id=:p1 AND a.product_id=:p2 AND a.country_code=:p3 AND a.color_code=:p4  GROUP BY a.data_type";
		List<Object[]> objects =this.psiInventoryRevisionLogDao.findBySql(sql, new Parameter(new Object[]{warehouseId,inventory.getProductId(),inventory.getCountryCode(),inventory.getColorCode()}));
		for(Object[] object:objects){
			String qualityType=object[0].toString();
			Integer quantity=Integer.parseInt(object[1].toString());
			if(qualityType.equals("new")){
				inventory.setNewQuantity(quantity);
			}else if(qualityType.equals("old")){
				inventory.setOldQuantity(quantity);
			}else if(qualityType.equals("broken")){
				inventory.setBrokenQuantity(quantity);
			}else if(qualityType.equals("renew")){
				inventory.setRenewQuantity(quantity);
			}
		}
	}
	
//	@Transactional(readOnly = false)
//	public void save(PsiInventoryRevisionLog psiInventoryRevisionLog) {
//		psiInventoryRevisionLogDao.save(psiInventoryRevisionLog);
//	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiInventoryRevisionLogDao.deleteById(id);
	}
	
}
