/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.parts;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsInventoryLogDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsInventoryTakingDao;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventory;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventoryLog;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventoryTaking;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventoryTakingItem;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 配件库存盘点Service
 * @author Michael
 * @version 2015-07-31
 */
@Component
@Transactional(readOnly = true)
public class PsiPartsInventoryTakingService extends BaseService {

	@Autowired
	private PsiPartsInventoryTakingDao  psiPartsInventoryTakingDao;
	@Autowired
	private PsiPartsInventoryService	psiPartsInventoryService;
	@Autowired
	private GenerateSequenceDao 		genSequenceDao;
	@Autowired
	private PsiPartsInventoryLogDao		psiPartsInventoryLogDao;
	
	
	public PsiPartsInventoryTaking get(String id) {
		return psiPartsInventoryTakingDao.get(id);
	}
	
	public Page<PsiPartsInventoryTaking> find(Page<PsiPartsInventoryTaking> page, PsiPartsInventoryTaking psiPartsInventoryTaking) {
		DetachedCriteria dc = psiPartsInventoryTakingDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiPartsInventoryTaking.getTakingNo())){
			dc.add(Restrictions.like("takingNo", "%"+psiPartsInventoryTaking.getTakingNo()+"%"));
		}
		if(StringUtils.isNotEmpty(psiPartsInventoryTaking.getTakingType())){
			dc.add(Restrictions.eq("takingType", psiPartsInventoryTaking.getTakingType()));
		}
		if(StringUtils.isNotEmpty(psiPartsInventoryTaking.getRemark())){
			dc.createAlias("this.items", "item");
			dc.add(Restrictions.like("item.partsName", "%"+psiPartsInventoryTaking.getRemark()+"%"));
		}
		page.setOrderBy("id desc");
		return psiPartsInventoryTakingDao.find2(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void takingSave(PsiPartsInventoryTaking psiPartsInventoryTaking) {
		psiPartsInventoryTaking.setCreateDate(new Date());
		psiPartsInventoryTaking.setCreateUser(UserUtils.getUser());
		this.savePartsInventory(psiPartsInventoryTaking);
		psiPartsInventoryTakingDao.save(psiPartsInventoryTaking);
	}
	
	private void savePartsInventory(PsiPartsInventoryTaking psiPartsInventoryTaking){
		String takingNo="";
		if("0".equals(psiPartsInventoryTaking.getTakingType())){
			takingNo=this.genSequenceDao.genSequence("P_IN",2);
			for(PsiPartsInventoryTakingItem item : psiPartsInventoryTaking.getItems()){
				PsiPartsInventory pInventory =psiPartsInventoryService.getPsiPartsInventorys(item.getPartsId());
				if(pInventory==null){
					pInventory = new PsiPartsInventory();
					pInventory.setPoFrozen(0);
					pInventory.setPoNotFrozen(0);
					pInventory.setStockFrozen(0);
					pInventory.setStockNotFrozen(0);
					pInventory.setPartsId(item.getPartsId());
					pInventory.setPartsName(item.getPartsName());
				}
				if(item.getPoFrozen()!=null&&!item.getPoFrozen().equals(0)){
					pInventory.setPoFrozen(pInventory.getPoFrozen()+item.getPoFrozen());
					saveLog(item.getPartsId(), item.getPartsName(), item.getPoFrozen(),"poFrozen",takingNo,psiPartsInventoryTaking.getCreateUser(),psiPartsInventoryTaking.getCreateDate(), "taking-in", psiPartsInventoryTaking.getRemark());
				}
				if(item.getPoNotFrozen()!=null&&!item.getPoNotFrozen().equals(0)){
					pInventory.setPoNotFrozen(pInventory.getPoNotFrozen()+item.getPoNotFrozen());	
					saveLog(item.getPartsId(), item.getPartsName(), item.getPoNotFrozen(),"poNotFrozen",takingNo,psiPartsInventoryTaking.getCreateUser(),psiPartsInventoryTaking.getCreateDate(), "taking-in", psiPartsInventoryTaking.getRemark());
				}
				if(item.getStockFrozen()!=null&&!item.getStockFrozen().equals(0)){
					pInventory.setStockFrozen(pInventory.getStockFrozen()+item.getStockFrozen());
					saveLog(item.getPartsId(), item.getPartsName(), item.getStockFrozen(),"stockFrozen",takingNo,psiPartsInventoryTaking.getCreateUser(),psiPartsInventoryTaking.getCreateDate(), "taking-in", psiPartsInventoryTaking.getRemark());
				}
				if(item.getStockNotFrozen()!=null&&!item.getStockNotFrozen().equals(0)){
					pInventory.setStockNotFrozen(pInventory.getStockNotFrozen()+item.getStockNotFrozen());
					saveLog(item.getPartsId(), item.getPartsName(), item.getStockNotFrozen(),"stockNotFrozen",takingNo,psiPartsInventoryTaking.getCreateUser(),psiPartsInventoryTaking.getCreateDate(), "taking-in", psiPartsInventoryTaking.getRemark());
				}
				this.psiPartsInventoryService.save(pInventory);
				item.setPartsTaking(psiPartsInventoryTaking);
			}
			
		}else{
			takingNo=this.genSequenceDao.genSequence("P_OUT",2);
			for(PsiPartsInventoryTakingItem item : psiPartsInventoryTaking.getItems()){
				PsiPartsInventory pInventory =psiPartsInventoryService.getPsiPartsInventorys(item.getPartsId());
				if(item.getPoFrozen()!=null&&!item.getPoFrozen().equals(0)){
					pInventory.setPoFrozen(pInventory.getPoFrozen()-item.getPoFrozen());
					if(pInventory.getPoFrozen()<0){
						throw new RuntimeException("配件："+pInventory.getPartsName()+"盘出后po冻结数为负值，同时刻有其他人操作同一条数据，操作已取消");
					}
					saveLog(item.getPartsId(), item.getPartsName(), 0-item.getPoFrozen(),"poFrozen",takingNo,psiPartsInventoryTaking.getCreateUser(),psiPartsInventoryTaking.getCreateDate(), "taking-out", psiPartsInventoryTaking.getRemark());
				}
				if(item.getPoNotFrozen()!=null&&!item.getPoNotFrozen().equals(0)){
					pInventory.setPoNotFrozen(pInventory.getPoNotFrozen()-item.getPoNotFrozen());	
					if(pInventory.getPoNotFrozen()<0){
						throw new RuntimeException("配件："+pInventory.getPartsName()+"盘出后po可用数为负值，同时刻有其他人操作同一条数据，操作已取消");
					}
					saveLog(item.getPartsId(), item.getPartsName(), 0-item.getPoNotFrozen(),"poNotFrozen",takingNo,psiPartsInventoryTaking.getCreateUser(),psiPartsInventoryTaking.getCreateDate(), "taking-out", psiPartsInventoryTaking.getRemark());
				}
				if(item.getStockFrozen()!=null&&!item.getStockFrozen().equals(0)){
					pInventory.setStockFrozen(pInventory.getStockFrozen()-item.getStockFrozen());
					if(pInventory.getStockFrozen()<0){
						throw new RuntimeException("配件："+pInventory.getPartsName()+"盘出后stock冻结数为负值，同时刻有其他人操作同一条数据，操作已取消");
					}
					saveLog(item.getPartsId(), item.getPartsName(), 0-item.getStockFrozen(),"stockFrozen",takingNo,psiPartsInventoryTaking.getCreateUser(),psiPartsInventoryTaking.getCreateDate(), "taking-out", psiPartsInventoryTaking.getRemark());
				}
				if(item.getStockNotFrozen()!=null&&!item.getStockNotFrozen().equals(0)){
					pInventory.setStockNotFrozen(pInventory.getStockNotFrozen()-item.getStockNotFrozen());
					if(pInventory.getStockNotFrozen()<0){
						throw new RuntimeException("配件："+pInventory.getPartsName()+"盘出后stock可用数为负值，同时刻有其他人操作同一条数据，操作已取消");
					}
					saveLog(item.getPartsId(), item.getPartsName(), 0-item.getStockNotFrozen(),"stockNotFrozen",takingNo,psiPartsInventoryTaking.getCreateUser(),psiPartsInventoryTaking.getCreateDate(), "taking-out", psiPartsInventoryTaking.getRemark());
				}
				item.setPartsTaking(psiPartsInventoryTaking);
			}
		}
		psiPartsInventoryTaking.setTakingNo(takingNo);
	}
	
	public void saveLog(Integer partsId,String partsName,Integer quantity,String dataType,String relativeNumber,User createUser,Date createDate,String operateType,String remark){
		PsiPartsInventoryLog log = new PsiPartsInventoryLog(partsId, partsName, quantity, dataType, relativeNumber, createUser, createDate, operateType, remark);
		this.psiPartsInventoryLogDao.save(log);
	}
	
	
}
