/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.psi.dao.PsiInventoryDao;
import com.springrain.erp.modules.psi.dao.PsiQualityChangeBillDao;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiQualityChangeBill;
import com.springrain.erp.modules.psi.entity.PsiQualityChangeBillItem;
import com.springrain.erp.modules.psi.entity.PsiSkuChangeBill;
import com.springrain.erp.modules.psi.entity.PsiSkuChangeBillItem;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * sku调换清单Service
 * @author Michael
 * @version 2015-05-25
 */
@Component
@Transactional(readOnly = true)
public class PsiQualityChangeBillService extends BaseService {
	@Autowired
	private PsiQualityChangeBillDao 		psiQualityChangeBillDao;
	@Autowired
	private PsiInventoryDao 				psiInventoryDao;
	@Autowired
	private PsiInventoryService  			psiInventoryService;
	@Autowired
	private MailManager						mailManager;
	public PsiQualityChangeBill get(Integer id) {
		return psiQualityChangeBillDao.get(id);
	}
	
	public Page<PsiQualityChangeBill> find(Page<PsiQualityChangeBill> page, PsiQualityChangeBill qualityBill) {
		DetachedCriteria dc = psiQualityChangeBillDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(qualityBill.getChangeSta())){
			dc.add(Restrictions.eq("changeSta", qualityBill.getChangeSta()));
		}
		if(StringUtils.isNotEmpty(qualityBill.getSku())){
			dc.add(Restrictions.like("sku", "%"+qualityBill.getSku()+"%"));
		}
		if(qualityBill.getApplyUser()!=null&&qualityBill.getApplyUser().getId()!=null&&!"".equals(qualityBill.getApplyUser().getId())){
			dc.add(Restrictions.eq("applyUser.id", qualityBill.getApplyUser().getId()));
		}
		if(qualityBill.getApplyDate()!=null){
			dc.add(Restrictions.ge("applyDate",qualityBill.getApplyDate()));
		}
		if(qualityBill.getSureDate()!=null){
			dc.add(Restrictions.le("applyDate",DateUtils.addDays(qualityBill.getSureDate(),1)));
		}
		
		if(qualityBill.getWarehouseId()!=null){
			dc.add(Restrictions.eq("warehouseId", qualityBill.getWarehouseId()));
		}
		dc.addOrder(Order.asc("changeSta"));
		dc.addOrder(Order.desc("id"));
		return psiQualityChangeBillDao.find(page, dc);
	}
	
	
	public List<PsiQualityChangeBill> findNoCancelInfos(Integer warehouseId,Integer unlineOrderId) {
		DetachedCriteria dc = psiQualityChangeBillDao.createDetachedCriteria();
		if(warehouseId!=null){
			dc.add(Restrictions.eq("warehouseId", warehouseId));
		}
		if(unlineOrderId!=null){
			dc.add(Restrictions.eq("unlineOrderId", unlineOrderId));
		}
		dc.add(Restrictions.ne("changeSta", "8"));
		return psiQualityChangeBillDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void sure(PsiQualityChangeBill changeBill){
		changeBill = this.psiQualityChangeBillDao.get(changeBill.getId());
		changeBill.setSureDate(new Date());
		changeBill.setSureUser(UserUtils.getUser());
		changeBill.setChangeSta("3");
		this.psiQualityChangeBillDao.save(changeBill);
		//对库存进行处理
		Integer quantity = changeBill.getQuantity();
		PsiInventory inventory = this.psiInventoryService.findBySku(changeBill.getSku(), changeBill.getWarehouseId());
		inventory.setNewQuantity(inventory.getNewQuantity()-quantity);
		inventory.setOfflineQuantity(inventory.getOfflineQuantity()+quantity);
		if(inventory.getNewQuantity()<0||inventory.getOfflineQuantity()<0){
			throw new RuntimeException("New_To_Offline确认库存数后有为负值；new:"+inventory.getNewQuantity()+"offline:"+inventory.getOfflineQuantity()+",请核查,操作已取消");
		}
		this.psiInventoryDao.save(inventory);
		//添加日志
		this.psiInventoryService.savelog("New_To_Offline", "new", -quantity, changeBill.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,inventory.getNewQuantity());
		this.psiInventoryService.savelog("New_To_Offline", "offline", quantity, changeBill.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,inventory.getOfflineQuantity());
	}
	
	@Transactional(readOnly = false)
	public boolean sureSave(PsiQualityChangeBill qualityBill) {
			List<PsiQualityChangeBillItem> items =qualityBill.getItems();
			qualityBill=this.psiQualityChangeBillDao.get(qualityBill.getId());
			Integer warehouseId=qualityBill.getWarehouseId();
			String sku = qualityBill.getSku();
			if(items==null||items.size()==0){
				throw new RuntimeException("提交失败，请关闭tab页，直接打开，请关闭浏览器重新提交！！！");
			}
			String info="线下转换确认["+qualityBill.getId()+"]";
			if(qualityBill.getUnlineOrderId()!=null){
				info+=",线下订单["+qualityBill.getUnlineOrderId()+"]";
			}
			//如果只有一个产品  并且是原来的fromSku
			if(items.size()==1&&items.get(0).getSku().equals(sku)){
				//对库存进行处理
				qualityBill.setRemark((StringUtils.isNotEmpty(qualityBill.getRemark())?qualityBill.getRemark():"")+(StringUtils.isNotEmpty(items.get(0).getRemark())?("[销售确认备注："+items.get(0).getRemark()+"]"):""));
				Integer quantity = qualityBill.getQuantity();
				PsiInventory inventory = this.psiInventoryService.findBySku(sku, warehouseId);
				inventory.setNewQuantity(inventory.getNewQuantity()-quantity);
				inventory.setOfflineQuantity(inventory.getOfflineQuantity()+quantity);
				info+="&nbsp;&nbsp;sku["+sku+"]确认数量：("+quantity+")个,备注:"+(qualityBill.getRemark()==null?"":qualityBill.getRemark())+";";
				if(inventory.getNewQuantity()<0||inventory.getOfflineQuantity()<0){
					throw new RuntimeException("New_To_Offline确认库存数后有为负值；new:"+inventory.getNewQuantity()+"offline:"+inventory.getOfflineQuantity()+",请核查,操作已取消");
				}
				this.psiInventoryDao.save(inventory);
				//添加日志
				this.psiInventoryService.savelog("New_To_Offline", "new", -quantity, null, inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,inventory.getNewQuantity());
				this.psiInventoryService.savelog("New_To_Offline", "offline", quantity, null, inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,inventory.getOfflineQuantity());
			}else{
				//把新的压入
				for(PsiQualityChangeBillItem item:items){
					item.setQualityBill(qualityBill);
					//对库存进行处理
					Integer quantity = item.getQuantity();
					PsiInventory inventory = this.psiInventoryService.findBySku(item.getSku(), warehouseId);
					inventory.setNewQuantity(inventory.getNewQuantity()-quantity);
					inventory.setOfflineQuantity(inventory.getOfflineQuantity()+quantity);
					info+="&nbsp;&nbsp;sku["+sku+"]确认数量：("+quantity+")个,备注:"+(item.getRemark()==null?"":item.getRemark())+";";
					if(inventory.getNewQuantity()<0||inventory.getOfflineQuantity()<0){
						throw new RuntimeException("New_To_Offline确认库存数后有为负值；new:"+inventory.getNewQuantity()+"offline:"+inventory.getOfflineQuantity()+",请核查,操作已取消");
					}
					this.psiInventoryDao.save(inventory);
					//添加日志
					this.psiInventoryService.savelog("New_To_Offline", "new", -quantity, null, inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,inventory.getNewQuantity());
					this.psiInventoryService.savelog("New_To_Offline", "offline", quantity, null, inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,inventory.getOfflineQuantity());
				}
				qualityBill.setItems(items);
			}
			qualityBill.setSureDate(new Date());
			qualityBill.setSureUser(UserUtils.getUser());
			qualityBill.setChangeSta("3");
			this.psiQualityChangeBillDao.save(qualityBill);
			
			//确认后给德国仓人员发信
			try{
				if(StringUtils.isNotEmpty(info)){
					String email ="george@inateck.com";
					String content="Hi,All<br/><br/>"+info+"，已确认。【<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/psiQualityChangeBill/list'>点击此处</a>】进行查看!<br/><br/><br/>best regards<br/>Erp System";
					String ccEmail=UserUtils.logistics1+",tim@inateck.com,lena@inateck.com";
					sendEmail(content, info, email,ccEmail);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return true;
	}
	
	@Transactional(readOnly = false)
	public void cancel(Integer billId,String remark){
		PsiQualityChangeBill changeBill = this.psiQualityChangeBillDao.get(billId);
		changeBill.setCancelDate(new Date());
		changeBill.setCancelUser(UserUtils.getUser());
		changeBill.setChangeSta("8");
		this.psiQualityChangeBillDao.save(changeBill);
		//取消后给德国方面发信
		if(changeBill.getWarehouseId().intValue()==19&&StringUtils.isNotEmpty(remark)){
			String info="取消转码[id:"+billId+"]：sku["+changeBill.getSku()+"]取消数量：("+changeBill.getQuantity()+")个";
			String content="Hi,All<br/><br/>"+info+"，"+info+"，【<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/psiQualityChangeBill/list'>点击此处</a>】进行查看!<br/><br/><br/>best regards<br/>Erp System";
			String ccEmail="george@inateck.com,amazon-sales@inateck.com";
			sendEmail(content, info, ccEmail,"tim@inateck.com");
		}
	}
	
	
	/**
	 *获得线下订单未确认的信息   key:sku value:id,quantity
	 */
	public Map<String,String> getUnCancelInfoByOrderId(Integer orderId,Integer warehouseId){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT a.id,a.sku,a.`quantity`,a.`change_sta` FROM psi_quality_change_bill AS a WHERE a.`unline_order_id`=:p1 AND a.`change_sta`!='8' AND a.`warehouse_id`=:p2 ";
		List<Object[]> list=this.psiQualityChangeBillDao.findBySql(sql,new Parameter(orderId,warehouseId));
		for (Object[] obj: list) {
			map.put(obj[1].toString(),obj[0].toString()+","+obj[2].toString()+","+obj[3]);
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void updateQuantityById(Integer unSureId,Integer unlineOrderId,Integer quantity){
		String sql="UPDATE psi_quality_change_bill AS a  SET a.`quantity`=:p3 WHERE a.`unline_order_id`=:p2 AND a.id=:p1";
		this.psiInventoryDao.updateBySql(sql,new Parameter(unSureId,unlineOrderId,quantity));
	}
	
	 public void sendEmail(String content,String title,String sendEmail,String ccEmail){
			Date date = new Date();
			final MailInfo mailInfo1 = new MailInfo(sendEmail,title,date);
			mailInfo1.setContent(content);
			mailInfo1.setCcToAddress(ccEmail);
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo1);
				}
			}.start();
		}
	
}
