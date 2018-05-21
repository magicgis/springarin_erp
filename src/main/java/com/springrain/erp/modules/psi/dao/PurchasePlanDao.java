/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.BaseDao;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PurchasePlan;
import com.springrain.erp.modules.psi.entity.PurchasePlanItem;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * @author Michael
 * @version 2017-08-29
 */
@Repository
public class PurchasePlanDao extends BaseDao<PurchasePlan> {
	
	
	@Transactional(readOnly=false)
	public void genPlan(PsiProduct product,Set<String> colorSet){
		String countrys ="de,com,jp";
		if("keyboard".equals(product.getType().toLowerCase())||"1".equals(product.getHasPower())) {
			countrys="de,com,jp,uk";
		}
		//根据产品、颜色、找出计划
		List<PurchasePlan> plans =this.findByProductName(product.getName());
		if(plans!=null&&plans.size()>0){
			PurchasePlan plan = plans.get(0);
			for(String color:colorSet){
				for(String country:countrys.split(",")){
					plan.getItems().add(new PurchasePlanItem(plan, product, product.getName(), color, country, 0, "0"));
				}
			}
			this.save(plan);
		}else{
			PurchasePlan plan = new PurchasePlan();
			plan.setCreateDate(new Date());
			plan.setCreateUser(UserUtils.getUser());
			plan.setPlanSta("1");
			List<PurchasePlanItem> items = Lists.newArrayList();
			for(String color:colorSet){
				for(String country:countrys.split(",")){
					items.add(new PurchasePlanItem(plan, product, product.getName(), color, country, 0, "0"));
				}
			}
			if(items.size()>0){
				plan.setItems(items);
				this.save(plan);
			}
		}
		
	}
	
	public List<PurchasePlan> findByProductName(String productName) {
		DetachedCriteria dc = this.createDetachedCriteria();
		dc.createAlias("this.items", "item");
		dc.add(Restrictions.eq("item.productName",productName));   
		dc.add(Restrictions.eq("planSta","1"));
		return this.find(dc);
	}
	
}
