/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.parts;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsInventoryDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsInventoryLogDao;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventory;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventoryLog;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 配件订单付款详情Service
 * @author Michael
 * @version 2015-06-29
 */
@Component
@Transactional(readOnly = true)
public class PsiPartsInventoryService extends BaseService {
	@Autowired
	private PsiPartsInventoryDao psiPartsInventoryDao;
	@Autowired
	private PsiPartsInventoryLogDao psiPartsInventoryLogDao;
	
	public PsiPartsInventory get(Integer id) {
		return psiPartsInventoryDao.get(id);
	}
	
	public Page<PsiPartsInventory> find(Page<PsiPartsInventory> page, PsiPartsInventory psiPartsInventory) {
		DetachedCriteria dc = psiPartsInventoryDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiPartsInventory.getPartsName())){
			dc.add(Restrictions.like("partsName", "%"+psiPartsInventory.getPartsName()+"%"));
		}
		return psiPartsInventoryDao.find(page, dc);
	}   
	
	@Transactional(readOnly = false)
	public void save(PsiPartsInventory psiPartsInventory) {
		psiPartsInventoryDao.save(psiPartsInventory);
	}
	
	@Transactional(readOnly = false)
	public void adjustSave(PsiPartsInventory psiPartsInventory) {
		String type=psiPartsInventory.getOperateType();
		for(PsiPartsInventoryLog log: psiPartsInventory.getPartsLogs()){
			Integer partsId = log.getPartsId();
			Integer quantity = log.getQuantity();
			log.setCreateDate(new Date());
			log.setCreateUser(UserUtils.getUser());
			PsiPartsInventory inventory =getPsiPartsInventorys(partsId);
			if("1".equals(type)){   
				//可用转换成冻结
				inventory.setStockNotFrozen(inventory.getStockNotFrozen()-quantity);
				inventory.setStockFrozen(inventory.getStockFrozen()+quantity);
				if(inventory.getStockNotFrozen()<0){
					throw new RuntimeException("配件转换后可用数为负数，操作已取消");
				}
				//stock增加日志
				saveLog(partsId, log.getPartsName(), quantity,"stockFrozen",log.getRelativeNumber(),log.getCreateUser(),log.getCreateDate(), "stock可用To冻结", log.getRemark());
				saveLog(partsId, log.getPartsName(), 0-quantity,"stockNotFrozen",log.getRelativeNumber(),log.getCreateUser(),log.getCreateDate(), "stock可用To冻结", log.getRemark());
			}else if("2".equals(type)){
				//冻结转化成可用
				inventory.setStockNotFrozen(inventory.getStockNotFrozen()+quantity);
				inventory.setStockFrozen(inventory.getStockFrozen()-quantity);
				if(inventory.getStockFrozen()<0){
					throw new RuntimeException("配件转换后冻结数为负数，操作已取消");
				}
				//stock增加日志
				saveLog(partsId, log.getPartsName(), quantity,"stockNotFrozen",log.getRelativeNumber(),log.getCreateUser(),log.getCreateDate(), "stock冻结To可用", log.getRemark());
				saveLog(partsId, log.getPartsName(), 0-quantity,"stockFrozen",log.getRelativeNumber(),log.getCreateUser(),log.getCreateDate(), "stock冻结To可用", log.getRemark());
			}
			this.psiPartsInventoryDao.save(inventory);
		}
	}
	
	
	public void saveLog(Integer partsId,String partsName,Integer quantity,String dataType,String relativeNumber,User createUser,Date createDate,String operateType,String remark){
		PsiPartsInventoryLog log = new PsiPartsInventoryLog(partsId, partsName, quantity, dataType, relativeNumber, createUser, createDate, operateType, remark);
		this.psiPartsInventoryLogDao.save(log);
	}
	/**
	 *根据partsId集合 查出配件库存信息 
	 */
	public Map<Integer,PsiPartsInventory>  getPsiPartsInventorys(Set<Integer> partsId){
		DetachedCriteria dc = psiPartsInventoryDao.createDetachedCriteria();
		if(partsId!=null){
			dc.add(Restrictions.in("partsId", partsId));
		}
		List<PsiPartsInventory> partsInventorys =psiPartsInventoryDao.find(dc);
		 Map<Integer,PsiPartsInventory> partsMap =Maps.newHashMap();
		for(PsiPartsInventory inventory:partsInventorys){
			partsMap.put(inventory.getPartsId(), inventory);
		}
		return partsMap;
	}
	
	/**
	 *根据partsId  查出该配件库存信息 
	 */
	public PsiPartsInventory  getPsiPartsInventorys(Integer partsId){
		DetachedCriteria dc = psiPartsInventoryDao.createDetachedCriteria();
		dc.add(Restrictions.eq("partsId", partsId));
		List<PsiPartsInventory> partsInventorys =psiPartsInventoryDao.find(dc);
		if(partsInventorys.size()==1){
			return partsInventorys.get(0);
		}
		return null;
	}
	
	
	/***
	 *根据partsId更新 
	 *   
	 */
	
	public void updateInventoryByPartsId(Integer partsId,Integer quantity){
		String sql="UPDATE psi_parts_inventory AS a SET a.`stock_frozen`=(a.`stock_frozen`-:p1) WHERE a.`parts_id`=:p2";
		this.psiPartsInventoryDao.updateBySql(sql, new Parameter(quantity,partsId));
	}
	
	
	public Map<Integer,Integer> getPartsInventoryMap(Set<Integer> partsIdSet){
		Map<Integer,Integer>   partsInventoryMap = Maps.newHashMap();
		String sql ="SELECT a.`parts_id`,a.`stock_frozen` FROM psi_parts_inventory AS a ";
		List<Object[]> list = null;
		if(partsIdSet!=null&&partsIdSet.size()>0){
			list = this.psiPartsInventoryDao.findBySql(sql+" WHERE a.`parts_id` IN :p1" ,new Parameter(partsIdSet));
		}else{
			list = this.psiPartsInventoryDao.findBySql(sql);
		}
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Object [] obj=list.get(i);
				partsInventoryMap.put((Integer)obj[0], (Integer)obj[1]);
			}
		}
		return partsInventoryMap;
	}
	
	
	
}
