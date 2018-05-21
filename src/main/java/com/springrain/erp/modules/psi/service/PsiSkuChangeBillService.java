/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
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
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MapValueComparator;
import com.springrain.erp.modules.psi.dao.PsiInventoryDao;
import com.springrain.erp.modules.psi.dao.PsiSkuChangeBillDao;
import com.springrain.erp.modules.psi.entity.PsiInventory;
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
public class PsiSkuChangeBillService extends BaseService {

	@Autowired
	private PsiSkuChangeBillDao psiSkuChangeBillDao;
	@Autowired
	private PsiInventoryService psiInventoryService;
	@Autowired
	private PsiInventoryDao psiInventoryDao;
	
	public PsiSkuChangeBill get(Integer id) {
		return psiSkuChangeBillDao.get(id);
	}
	
	@Transactional(readOnly=false)
	public void save(PsiSkuChangeBill psiSkuChangeBill) {
		psiSkuChangeBillDao.save(psiSkuChangeBill);
	}
	
	public Page<PsiSkuChangeBill> find(Page<PsiSkuChangeBill> page, PsiSkuChangeBill psiSkuChangeBill) {
		DetachedCriteria dc = psiSkuChangeBillDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiSkuChangeBill.getChangeSta())){
			dc.add(Restrictions.eq("changeSta", psiSkuChangeBill.getChangeSta()));
		}
		
		if(StringUtils.isNotEmpty(psiSkuChangeBill.getFromSku())){
			dc.add(Restrictions.like("fromSku", "%"+psiSkuChangeBill.getFromSku()+"%"));
		}
		
		if(StringUtils.isNotEmpty(psiSkuChangeBill.getToSku())){
			dc.add(Restrictions.like("toSku", "%"+psiSkuChangeBill.getToSku()+"%"));
		}
		
		if(StringUtils.isNotEmpty(psiSkuChangeBill.getEvenName())){
			dc.add(Restrictions.like("evenName", "%"+psiSkuChangeBill.getEvenName()+"%"));
		}
		
		if(psiSkuChangeBill.getApplyUser()!=null&&psiSkuChangeBill.getApplyUser().getId()!=null&&!"".equals(psiSkuChangeBill.getApplyUser().getId())){
			dc.add(Restrictions.eq("applyUser.id", psiSkuChangeBill.getApplyUser().getId()));
		}
		
		
		if(psiSkuChangeBill.getApplyDate()!=null){
			dc.add(Restrictions.ge("applyDate",psiSkuChangeBill.getApplyDate()));
		}
		
		if(psiSkuChangeBill.getSureDate()!=null){
			dc.add(Restrictions.le("applyDate",DateUtils.addDays(psiSkuChangeBill.getSureDate(),1)));
		}
		
		if(psiSkuChangeBill.getWarehouseId()!=null){
			dc.add(Restrictions.eq("warehouseId", psiSkuChangeBill.getWarehouseId()));
		}
		
		dc.addOrder(Order.asc("changeSta"));
		dc.addOrder(Order.desc("id"));
		
		return psiSkuChangeBillDao.find(page, dc);
	}
	
	/**
	 *按月统计转码信息 key:month   productName country  quantity
	 */
	public Map<String,Map<String,Integer>> find(PsiSkuChangeBill psiSkuChangeBill) {
		Map<String,Map<String,Integer>> rs = Maps.newHashMap();
		DetachedCriteria dc = psiSkuChangeBillDao.createDetachedCriteria();
		dc.add(Restrictions.ne("changeSta", "5"));
		
		if(psiSkuChangeBill.getApplyDate()!=null){
			dc.add(Restrictions.ge("applyDate",psiSkuChangeBill.getApplyDate()));
		}
		
		if(psiSkuChangeBill.getSureDate()!=null){
			dc.add(Restrictions.le("applyDate",DateUtils.addDays(psiSkuChangeBill.getSureDate(),1)));
		}
		
		if(psiSkuChangeBill.getWarehouseId()!=null){
			dc.add(Restrictions.eq("warehouseId", psiSkuChangeBill.getWarehouseId()));
		}
		
		List<PsiSkuChangeBill>  list = psiSkuChangeBillDao.find(dc);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Map<String,Integer> monthUnSortMap = Maps.newHashMap();
		for(PsiSkuChangeBill change:list){
			Integer quantity = change.getQuantity();
			Map<String,Integer> inMap = null;
			String conKey = change.getProductColorCountry();
			String month = sdf.format(change.getApplyDate()).substring(0,7);
			if(rs.get(month)==null){
				inMap = Maps.newHashMap();
			}else{
				inMap=rs.get(month);
			}
			
			if(inMap.get(conKey)!=null){
				quantity+=inMap.get(conKey);
			}
			inMap.put(conKey, quantity);
			rs.put(month, inMap);
			if(monthUnSortMap.get(month)==null){
				monthUnSortMap.put(month, Integer.parseInt(month.replace("-","")));
			}
		}
		
		//排序处理
		Map<String,Map<String,Integer>> tempRs = Maps.newLinkedHashMap();
		
		MapValueComparator bvc =  new MapValueComparator(monthUnSortMap,false);  
		TreeMap<String,Integer> sortMonthMap = new TreeMap<String,Integer>(bvc);  
		sortMonthMap.putAll(monthUnSortMap);
		
		for(String month:sortMonthMap.keySet()){
			MapValueComparator bvc1 =  new MapValueComparator(rs.get(month),false);  
			TreeMap<String,Integer> sortKeyMap = new TreeMap<String,Integer>(bvc1);  
			sortKeyMap.putAll(rs.get(month));
			tempRs.put(month, sortKeyMap);
		}
		return tempRs;
	}
	
	
	
	@Transactional(readOnly = false)
	public boolean sureSave(PsiSkuChangeBill psiSkuChangeBill) {
			List<PsiSkuChangeBillItem> items =psiSkuChangeBill.getItems();
			psiSkuChangeBill=this.psiSkuChangeBillDao.get(psiSkuChangeBill.getId());
			String fromSku=psiSkuChangeBill.getFromSku();
			String toSku   =psiSkuChangeBill.getToSku();
			Integer quantity =psiSkuChangeBill.getQuantity();
			Integer warehouseId=psiSkuChangeBill.getWarehouseId();
			Integer productId=psiSkuChangeBill.getProductId();
			String productName =psiSkuChangeBill.getProductName();
			String color=psiSkuChangeBill.getProductColor();
			String toCountry =psiSkuChangeBill.getProductCountry();
			//如果只有一个产品  并且是原来的fromSku
			if(items.size()==1&&items.get(0).getSku().equals(fromSku)){
				
			}else{
				//把原来的还原
				PsiInventory toInventory = new PsiInventory();
				 String res=  this.changeReturn(fromSku,toSku,  warehouseId, quantity, color, toCountry, productId, productName,"sure",toInventory);
				 if(StringUtils.isNotEmpty(res)){
				   throw new RuntimeException(res);
				 }
				 Integer curQ = toInventory.getNewQuantity();//最新的库存信息
				//把新的压入
				for(PsiSkuChangeBillItem item:items){
					Integer fromQuantity =item.getQuantity();
					item.setSkuChangeBill(psiSkuChangeBill);
					//把及时库存更新
					PsiInventory inventoryFrom = psiInventoryService.findBySku(item.getSku(), warehouseId);
					Integer changeQuantity=inventoryFrom.getNewQuantity()-fromQuantity;
					inventoryFrom.setNewQuantity(changeQuantity);
					//添加日志
					curQ+=fromQuantity;
					setTypeDataAndLog(item.getSku(),toSku,fromQuantity, color, inventoryFrom.getCountryCode(),toCountry, productId, productName, warehouseId,changeQuantity,curQ);
					if(changeQuantity<0){
						throw new RuntimeException("系统执行后sku:"+toSku+"库存数量<0，同时刻有其他人操作同一条数据，操作已取消");
					}
					inventoryFrom.setUpdateDate(new Date());
					this.psiInventoryDao.save(inventoryFrom);
				}
				
				psiSkuChangeBill.setItems(items);
			}
			
			psiSkuChangeBill.setSureDate(new Date());
			psiSkuChangeBill.setSureUser(UserUtils.getUser());
			psiSkuChangeBill.setChangeSta("3");
			psiSkuChangeBillDao.save(psiSkuChangeBill);
			return true;
	}
	
	@Transactional(readOnly = false)
	public String cancel(PsiSkuChangeBill psiSkuChangeBill) {
			String fromSku =psiSkuChangeBill.getFromSku();
			String toSku   =psiSkuChangeBill.getToSku();
			Integer quantity =psiSkuChangeBill.getQuantity();
			Integer warehouseId=psiSkuChangeBill.getWarehouseId();
			String res = this.changeReturn(fromSku, toSku, warehouseId, quantity, psiSkuChangeBill.getProductColor(), psiSkuChangeBill.getProductCountry(), psiSkuChangeBill.getProductId(), psiSkuChangeBill.getProductName(),"cancel",null);
			if(StringUtils.isNotEmpty(res)){
				 throw new RuntimeException(res);
			}
			psiSkuChangeBill.setChangeSta("8");
			if(UserUtils.getUser()!=null){
				psiSkuChangeBill.setCancelUser(UserUtils.getUser());
			}
			psiSkuChangeBill.setCancelDate(new Date());
			psiSkuChangeBillDao.save(psiSkuChangeBill);
			return "取消成功！！！";
	}   
	
	public String changeReturn(String fromSku,String toSku,Integer warehouseId,Integer quantity,String color,String toCountry,Integer productId,String productName,String flag,PsiInventory inventoryTo){
		//如果是取消需要把原来的数加上，  如果是确认没必要加
		inventoryTo = psiInventoryService.findBySku(toSku, warehouseId);
		Integer afterQuantity=inventoryTo.getNewQuantity()-quantity;
		if("cancel".equals(flag)){
			inventoryTo.setNewQuantity(afterQuantity);
			if(afterQuantity<0){
				return "error:系统执行后sku:"+toSku+"库存数量<0，操作已取消";
			}
			inventoryTo.setUpdateDate(new Date());
			this.psiInventoryDao.save(inventoryTo);
		}
		
		//把原来转码的信息反过去、库存操作
		PsiInventory inventoryFrom = psiInventoryService.findBySku(fromSku, warehouseId);
		inventoryFrom.setNewQuantity(inventoryFrom.getNewQuantity()+quantity);
		inventoryFrom.setUpdateDate(new Date());
		this.psiInventoryDao.save(inventoryFrom);
		
		//库存日志操作,这里fromSku和，fromCountry   是反放的
		this.setTypeDataAndLog(toSku,fromSku, quantity, color,toCountry,inventoryFrom.getCountryCode(), productId, productName, warehouseId,afterQuantity,inventoryFrom.getNewQuantity());
		return "";
	}
	
	public String getSkuData(Integer warehouseId,Integer productId,String color,String fromSku){
		String sql="SELECT a.`sku`,a.`new_quantity` FROM psi_inventory  AS a WHERE a.`product_id`=:p1 AND a.`warehouse_id`=:p2 AND a.`color_code`=:p3 ";
		List<Object[]> list=this.psiSkuChangeBillDao.findBySql(sql,new Parameter(productId,warehouseId,color));
		StringBuilder res= new StringBuilder("[");
		for(Object[] object:list){
			//如果不为原本转移的sku，库存为0的就不用显示了
			if(!((Integer)object[1]).equals(0)||((String)object[0]).equals(fromSku)){
				res.append("{\"sku\":\"").append((String)object[0]).append("\",\"quantity\":\"").append(object[1]).append("\"},");
			}
		}
		res=new StringBuilder(res.substring(0, res.length()-1)).append("]");
		return res.toString();
	}
	
	
	/***
	 * 获取已经转码但未确认的，
	 * 
	 */
	public Map<String,Integer> getSkuChangeNoSure(Set<String> skuSet,Integer warehouseId){
		Map<String,Integer> skuMap =Maps.newHashMap();
		Parameter para = null;
		String sql="SELECT a.`to_sku`,SUM(a.`quantity`) FROM psi_sku_change_bill AS a WHERE a.`change_sta`='0' AND a.warehouse_id =:p1 ";
		if(skuSet!=null&&skuSet.size()>0){
			sql+=" AND a.`to_sku` in :p2 ";
			para = new  Parameter(warehouseId,skuSet);
		}else{
			para = new  Parameter(warehouseId);
		}
		sql+="GROUP BY a.`to_sku`";
		List<Object[]> objects=this.psiSkuChangeBillDao.findBySql(sql,para);
		if(objects.size()>0){
			for(Object[] object:objects){
				skuMap.put((String)object[0], ((BigDecimal)object[1]).intValue());
			}
		}
		return skuMap;
	}
	
	/***
	 * 获取已经转码但未确认的，
	 * 
	 */
	
	public Map<String,String> getSkuChangeNoSure(String shipmentId){
		Map<String,String> skuMap =Maps.newHashMap();
		Map<String,List<String>> noSureMap = Maps.newHashMap();
		String sql = "SELECT a.`to_sku`,a.`quantity`,(SELECT CONCAT(UPPER(CASE WHEN b.country_code ='com' THEN 'us' ELSE b.country_code  END),'[',a.`from_sku`,']') FROM psi_inventory AS b WHERE b.sku=a.`from_sku` AND b.warehouse_id =19 ) ,GROUP_CONCAT(bb.shipment_id ORDER BY bb.create_date) AS sid FROM psi_sku_change_bill AS a,(SELECT b.`sku`,a.`create_date`,a.`shipment_id` FROM psi_fba_inbound a ,psi_fba_inbound_item b WHERE a.`id` = b.`fba_inbound_id` AND a.shipped_date IS  NULL and a.`shipment_status` in ('WORKING','SHIPPED') ) bb WHERE a.`to_sku` = bb.sku AND a.`change_sta`='0' AND a.warehouse_id =19 GROUP BY a.`to_sku`,a.`quantity` HAVING (sid LIKE :p2 OR sid = :p1)";
		List<Object[]> objects=this.psiSkuChangeBillDao.findBySql(sql,new Parameter(shipmentId,shipmentId+",%"));
		if(objects.size()>0){
			skuMap =Maps.newHashMap();
			for(Object[] object:objects){
				List<String> list = Lists.newArrayList();
				String toSku=(String)object[0];
				if(noSureMap.get(toSku)!=null){
					list =noSureMap.get(toSku);
				}
				list.add(object[1]+","+object[2]);
				noSureMap.put(toSku, list);
			}
		}
		//组装结果map
		for(Map.Entry<String,List<String>>entry :noSureMap.entrySet()){
			String toSku = entry.getKey();
			List<String> resList=entry.getValue();
			String res="";
			if(resList.size()>1){
				StringBuffer resBuf =new StringBuffer("");
				for(String res1:resList){
					String[] countryTempArr=res1.split(",");
					resBuf.append(countryTempArr[0]+" From "+countryTempArr[1].toUpperCase()+";");
				}
				res=resBuf.toString().substring(0,resBuf.toString().length()-1);
			}else{
				String[] countryTempArr=resList.get(0).split(",");
				res=countryTempArr[0]+" From "+countryTempArr[1].toUpperCase();
			}
			skuMap.put(toSku, res);
		}
		return skuMap;
	}
	
	public Map<String,String> getSkuChangeNoSure(Integer id){
		Map<String,String> skuMap =Maps.newHashMap();
		Map<String,List<String>> noSureMap = Maps.newHashMap();
		String sql = "SELECT a.`to_sku`,a.`quantity`,(SELECT CONCAT(UPPER(CASE WHEN b.country_code ='com' THEN 'us' ELSE b.country_code  END),'[',a.`from_sku`,']') FROM psi_inventory AS b WHERE b.sku=a.`from_sku` AND b.warehouse_id =19 ) ,GROUP_CONCAT(bb.id ORDER BY bb.create_date) AS sid FROM psi_sku_change_bill AS a,(SELECT b.`sku`,a.`create_date`,a.id FROM psi_fba_inbound a ,psi_fba_inbound_item b WHERE a.`id` = b.`fba_inbound_id` AND a.shipped_date IS  NULL and a.`shipment_status`='' ) bb WHERE a.`to_sku` = bb.sku AND a.`change_sta`='0' AND a.warehouse_id =19 GROUP BY a.`to_sku`,a.`quantity` HAVING (sid LIKE :p2 OR sid = :p1)";
		List<Object[]> objects=this.psiSkuChangeBillDao.findBySql(sql,new Parameter(id,id+",%"));
		if(objects.size()>0){
			skuMap =Maps.newHashMap();
			for(Object[] object:objects){
				List<String> list = Lists.newArrayList();
				String toSku=(String)object[0];
				if(noSureMap.get(toSku)!=null){
					list =noSureMap.get(toSku);
				}
				list.add(object[1]+","+object[2]);
				noSureMap.put(toSku, list);
			}
		}
		//组装结果map
		for(Map.Entry<String,List<String>>entry :noSureMap.entrySet()){
			String toSku = entry.getKey();
			List<String> resList=entry.getValue();
			String res="";
			if(resList.size()>1){
				StringBuffer resBuf =new StringBuffer("");
				for(String res1:resList){
					String[] countryTempArr=res1.split(",");
					resBuf.append(countryTempArr[0]+" From "+countryTempArr[1].toUpperCase()+";");
				}
				res=resBuf.toString().substring(0,resBuf.toString().length()-1);
			}else{
				String[] countryTempArr=resList.get(0).split(",");
				res=countryTempArr[0]+" From "+countryTempArr[1].toUpperCase();
			}
			skuMap.put(toSku, res);
		}
		return skuMap;
	}
	
	/***
	 * 获取已经转码但未确认的，
	 * 
	 */
	public  List<PsiSkuChangeBill> findSkuChangeNoSure(Integer warehouseId){
		DetachedCriteria dc = psiSkuChangeBillDao.createDetachedCriteria();
		if(warehouseId!=null){
			dc.add(Restrictions.eq("warehouseId", warehouseId));
		}
		dc.add(Restrictions.eq("changeSta", "0"));
		dc.addOrder(Order.desc("id"));
		return this.psiSkuChangeBillDao.find(dc);
	}
	
	
	public void setTypeDataAndLog(String fromSku,String toSku,Integer quantity,String color,String fromCountry,String toCountry,Integer productId,String productName,Integer warehouseId,Integer fromTimelyQ,Integer toTimelyQ){
		String  oprationType = "From_"+fromSku+"_To_"+toSku+" By Sku Confirm";
		this.psiInventoryService.savelog("New_"+oprationType, "new", -quantity, null, color, fromCountry, productId, productName, warehouseId,null,fromSku,null,null,fromTimelyQ);
		this.psiInventoryService.savelog("New_"+oprationType, "new", quantity, null, color, toCountry, productId, productName, warehouseId,null,toSku,null,null,toTimelyQ);
		
	}
	
	public Map<String,String> getShipmentIds(){
		String sql="SELECT a.`to_sku` ,GROUP_CONCAT(bb.shipment_id ORDER BY bb.create_date) AS sid FROM psi_sku_change_bill AS a,(SELECT b.`sku`,a.`create_date`,a.`shipment_id` FROM psi_fba_inbound a ,psi_fba_inbound_item b WHERE a.`id` = b.`fba_inbound_id` AND a.shipped_date IS  NULL AND a.`shipment_status` IN ('WORKING','SHIPPED') ) bb WHERE a.`to_sku` = bb.sku  AND a.`change_sta`='0' AND a.warehouse_id =19 GROUP BY a.`to_sku`";
		Map<String,String> resMap = Maps.newHashMap();
		List<Object[]>	lists=this.psiSkuChangeBillDao.findBySql(sql);
		if(lists.size()>0){
			for(Object[] obj:lists){
				String shippmentId =obj[1].toString();
				if(StringUtils.isNotEmpty(shippmentId)&&shippmentId.contains(",")){
					shippmentId=shippmentId.split(",")[0];//按时间排序如果有两个未确认的，取前面的
				}
				resMap.put(obj[0].toString(), shippmentId);
			}
		}
		return resMap;
	}
	
	/**
	 *查出被取消的付吧贴    DELETED   CANCELLED
	 */
	public boolean getHasCanceledBySku(String sku,Date date){
		String sql="SELECT COUNT(a.id) FROM psi_fba_inbound AS a,psi_fba_inbound_item AS b WHERE a.id=b.`fba_inbound_id` " +
				"AND b.`sku`=:p1 AND a.`create_date`>=:p2 AND a.`shipment_status`in ('CANCELLED','DELETED') ";
		List<BigInteger>	lists=this.psiSkuChangeBillDao.findBySql(sql, new Parameter(sku,date));
		if(lists.get(0).intValue()>0){
		   return true;
		}else{
			return false;
		}
	}

	public List<String> findSkuByProductAndCountry(String productName, String color,
			String country) {
		String sql = "SELECT t.`sku` FROM `psi_sku` t WHERE t.`product_name`=:p1 AND t.`color`=:p2 AND t.`country`=:p3 AND t.`del_flag`='0'";
		return psiSkuChangeBillDao.findBySql(sql, new Parameter(productName,color, country));
		
	}
	
	
	
}
