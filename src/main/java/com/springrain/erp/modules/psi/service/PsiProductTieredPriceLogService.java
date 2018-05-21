package com.springrain.erp.modules.psi.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.PsiProductTieredPriceLogDao;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPriceLog;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 *	供应商产品Service
 */
@Component
@Transactional(readOnly = true)
public class PsiProductTieredPriceLogService extends BaseService {
	@Autowired
	private PsiProductTieredPriceLogDao psiProductTieredPriceLogDao;
	@Autowired
	private PsiSupplierService  psiSupplierService;
	@Autowired
	private PsiProductTieredPriceService  tieredService;
	
	public Page<PsiProductTieredPriceLog> find(Page<PsiProductTieredPriceLog> page,PsiProductTieredPriceLog log) {
		DetachedCriteria dc = psiProductTieredPriceLogDao.createDetachedCriteria();
		
		
		//对跟单和采购经理     进行权限控制
		String roleStr = UserUtils.getUser().getRoleNames()+",";
		String userId = UserUtils.getUser().getId();
		List<Integer> productIds=Lists.newArrayList();
		
		Set<String> permissionsSet = Sets.newHashSet();
		//查询权限
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			for(Role role:UserUtils.getUser().getRoleList()){
				permissionsSet.addAll(role.getPermissions());
			}
			if(!permissionsSet.contains("psi:product:reviewPrice")){//如果不包含价格审批权限，过滤
				if(UserUtils.hasPermission("psi:purchase:manager")){//采购经理
					//如果是采购经理
					productIds= this.tieredService.getProductIdsByPurchaseUserId(userId);
					dc.add(Restrictions.in("product.id", productIds));
				}else if(UserUtils.hasPermission("psi:order:edit")){
					//如果是跟单员
					productIds = this.psiSupplierService.getProductIdsByFollowId(userId);
					dc.add(Restrictions.in("product.id", productIds));
				}else if(UserUtils.hasPermission("psi:product:manager")){
					//如果是产品经理
					productIds= this.tieredService.getProductIdsByManagerUserId(userId);
					dc.add(Restrictions.in("product.id", productIds));
				}
			}
		}
		if(StringUtils.isNotEmpty(log.getProductIdColor())){
			String[] arr = log.getProductIdColor().split(",");
			dc.add(Restrictions.eq("product.id", Integer.parseInt(arr[0])));
			if(arr.length>1){
				dc.add(Restrictions.eq("color", arr[1]));
			}
		}
		dc.add(Restrictions.ge("createTime",log.getCreateTime()));
		dc.add(Restrictions.le("createTime",DateUtils.addDays(log.getUpdateTime(),1)));
		dc.addOrder(Order.desc("id"));
		return psiProductTieredPriceLogDao.find(page,dc);
	}  
	
	@Transactional(readOnly = false)
	public void save(PsiProductTieredPriceLog log){
		this.psiProductTieredPriceLogDao.save(log);
	}
	
	
	/**
	 * 查询最近的改价备注
	 * 
	 */
	
	public Map<Integer,String> getRemarkMap(String productColor) {
		Map<Integer,String> resMap = Maps.newHashMap();
		String sql="  SELECT a.`remark`,a.`supplier_id` FROM  psi_product_tiered_price_log AS a WHERE a.id IN ( SELECT MAX(a.`id`) FROM psi_product_tiered_price_log AS a WHERE a.`product_name_color`=:p1 GROUP BY a.`supplier_id` ) ";
		List<Object[]> list = this.psiProductTieredPriceLogDao.findBySql(sql,new Parameter(productColor));
		for(Object[] obj:list){
			Integer supplierId = Integer.parseInt(obj[1].toString());
			String remark = obj[0].toString();
			resMap.put(supplierId, remark);
		}
		return resMap;
	}  
	
}
