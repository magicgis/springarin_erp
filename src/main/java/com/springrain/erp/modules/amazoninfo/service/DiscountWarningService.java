/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.DiscountWarningDao;
import com.springrain.erp.modules.amazoninfo.dao.DiscountWarningItemDao;
import com.springrain.erp.modules.amazoninfo.entity.DiscountWarning;
import com.springrain.erp.modules.amazoninfo.entity.DiscountWarningItem;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 折扣预警Service
 * @author Michael
 * @version 2015-08-24
 */
@Component
@Transactional(readOnly = true)
public class DiscountWarningService extends BaseService {

	@Autowired
	private DiscountWarningDao discountWarningDao;
	
	@Autowired
	private DiscountWarningItemDao discountWarningItemDao;
	
	public DiscountWarning get(Integer id) {
		return discountWarningDao.get(id);
	}
	
	
	public Page<DiscountWarning> find(Page<DiscountWarning> page, DiscountWarning discountWarning) {
		DetachedCriteria dc = discountWarningDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(discountWarning.getPromotionId())){
			dc.add(Restrictions.like("promotionId","%"+discountWarning.getPromotionId()+"%"));
		}
		
		if(StringUtils.isNotEmpty(discountWarning.getCountry())){
			dc.add(Restrictions.eq("country", discountWarning.getCountry()));
		}
		
		if(StringUtils.isNotEmpty(discountWarning.getRemark())){
			dc.createAlias("this.items", "item");
			dc.add(Restrictions.like("item.productNameColor", "%"+discountWarning.getRemark()+"%"));
		}
		
		if(discountWarning.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate", discountWarning.getCreateDate()));
		}
		
		if (discountWarning.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",discountWarning.getUpdateDate()));
		}
		
		if(StringUtils.isNotEmpty(discountWarning.getWarningSta())){
			if("1".equals(discountWarning.getWarningSta())){
				dc.add(Restrictions.and(Restrictions.ne("warningSta","2"),Restrictions.ne("warningSta","0")));
			}else{
				dc.add(Restrictions.eq("warningSta",discountWarning.getWarningSta()));
			}
			
		}
		
		page.setOrderBy("id desc");
		return discountWarningDao.find2(page, dc);
	}
	
	public Map<String,String> getSkuMap(Set<String> countrys,Set<String> skus){
		// AND a.`use_barcode`='1'
		String sql ="SELECT DISTINCT CONCAT(a.`product_name`,IF(a.`color`=' ','',CONCAT('_',a.`color`))),a.`sku` FROM psi_sku AS a WHERE a.`del_flag`='0' AND  a.`country` in :p1  AND a.`sku` IN :p2";
		List<Object[]> list=this.discountWarningDao.findBySql(sql,new Parameter(countrys,skus));
		Map<String,String> skuMap = Maps.newHashMap();
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				skuMap.put(obj[1].toString(), obj[0].toString());
			}
		}
		return skuMap;
	}
	
	@Transactional(readOnly = false)
	public void addSave(DiscountWarning discountWarning) {
		for(DiscountWarningItem item :discountWarning.getItems()){
			item.setWarning(discountWarning);
		}
		discountWarning.setCreateUser(UserUtils.getUser());
		discountWarning.setCreateDate(new Date());
		discountWarning.setWarningSta("0");
		discountWarningDao.save(discountWarning);
	}
	
	
	@Transactional(readOnly = false)
	public void editSave(DiscountWarning discountWarning) {
		Set<Integer>  delItemSet = Sets.newHashSet();
		Set<String> setNewIds = new HashSet<String>();
		String oldItemIds=discountWarning.getOldItemIds();
		String [] oldIds = oldItemIds.split(",");
		
		for(DiscountWarningItem item :discountWarning.getItems()){
			item.setWarning(discountWarning);
			if(item.getId()!=null&&!"".equals(item.getId().toString())){
				setNewIds.add(item.getId().toString());
			}else{
				//如果id为空     说明是新增的
				item.setDelFlag("0");
			}
		}
		
		if(setNewIds!=null&&setNewIds.size()>0){
			for(int j=0;j<oldIds.length;j++){
				if(!setNewIds.contains(oldIds[j])){
					//不包含就干掉
					delItemSet.add(Integer.valueOf(oldIds[j]));
				};
			}
		}else{
			//说明原来的都删除了
			for(int j=0;j<oldIds.length;j++){
				delItemSet.add(Integer.valueOf(oldIds[j]));
			}
		}
		
		
		if(delItemSet.size()>0){
			for(DiscountWarningItem item:this.getDiscountWarningItems(delItemSet)){
				item.setDelFlag("1");
				item.setWarning(discountWarning);
				discountWarning.getItems().add(item);
			};
		}
		
		//如果状态不为0，置0
		if(!"0".equals(discountWarning.getWarningSta())){
			discountWarning.setWarningSta("0");
		}
		discountWarning.setUpdateUser(UserUtils.getUser());
		discountWarning.setUpdateDate(new Date());
		discountWarningDao.getSession().merge(discountWarning);
	}
	
	@Transactional(readOnly = false)
	public void save(DiscountWarning discountWarning) {
		discountWarningDao.save(discountWarning);
	}
	
	/**
	 *更新结束状态及原因 
	 */
	@Transactional(readOnly = false)
	public void updateStaAndRes(String tranId,String country,String res) {
		String sql ="UPDATE amazoninfo_discount_warning  AS a SET a.`warning_sta`=:p3 WHERE a.`promotion_id` =:p1 AND a.`country`=:p2 AND a.`warning_sta`='0'";
		discountWarningDao.updateBySql(sql, new Parameter(tranId,country,res));
	}
	
	/**
	 *更新结束状态及原因 
	 */
	@Transactional(readOnly = false)
	public void updateStaAndResById(Integer id,String res) {
		String sql ="UPDATE amazoninfo_discount_warning  AS a SET a.`warning_sta`=:p2 WHERE a.`id` =:p1 AND a.`warning_sta`='0'";
		discountWarningDao.updateBySql(sql, new Parameter(id,res));
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		discountWarningDao.deleteById(id);
	}
	
	public List<DiscountWarning> find() {
		DetachedCriteria dc = discountWarningDao.createDetachedCriteria();
		dc.add(Restrictions.eq("warningSta","0"));
		return discountWarningDao.find(dc);
	}
	
	public List<DiscountWarning> findByEage() {
		DetachedCriteria dc = discountWarningDao.createDetachedCriteria();
		dc.add(Restrictions.eq("warningSta","0"));
		 List<DiscountWarning> list =discountWarningDao.find(dc);
		for(DiscountWarning warn:list){
			Hibernate.initialize(warn.getItems());
		}
		return list;
	}
	
	/**
	 *根据itemId获取list信息 
	 */
	public List<DiscountWarningItem> getDiscountWarningItems(Set<Integer> ids){
		DetachedCriteria dc = this.discountWarningItemDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		return discountWarningItemDao.find(dc);
	}
	
	
}
