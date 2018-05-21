package com.springrain.erp.modules.psi.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.PsiSupplierDao;
import com.springrain.erp.modules.psi.dao.PsiSupplierIndemnifyDao;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PsiSupplierIndemnify;
import com.springrain.erp.modules.psi.entity.PurchaseOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrder;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 *	进销存供应商Service
 */
@Component
@Transactional(readOnly = true)
public class PsiSupplierService extends BaseService {
	@Autowired
	private PsiSupplierDao psiSupplierDao;
	
	@Autowired
	private PsiSupplierIndemnifyDao psiSupplierIndemnifyDao;
	
	@Transactional(readOnly = false)
	public void save(PsiSupplierIndemnify psiSupplierIndemnify){
		psiSupplierIndemnifyDao.save(psiSupplierIndemnify);
	}
	

	public PsiSupplierIndemnify getSupplierIndemnify(Integer id){
		return psiSupplierIndemnifyDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void delSupplierIndemnify(PsiSupplierIndemnify psiSupplierIndemnify){
		String sql="update psi_supplier_indemnify set del_flag='1' where id=:p1";
		psiSupplierIndemnifyDao.updateBySql(sql, new Parameter(psiSupplierIndemnify.getId()));
	}
	
	@Transactional(readOnly = false)
	public void updateAttchment(PsiSupplierIndemnify psiSupplierIndemnify){
		String sql="update psi_supplier_indemnify set attchment_path=:p1 where id=:p2";
		psiSupplierIndemnifyDao.updateBySql(sql, new Parameter(psiSupplierIndemnify.getAttchmentPath(),psiSupplierIndemnify.getId()));
	}
	
	public Page<PsiSupplierIndemnify> find(Page<PsiSupplierIndemnify> page, PsiSupplierIndemnify psiSupplierIndemnify) {
		DetachedCriteria dc = psiSupplierIndemnifyDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiSupplierIndemnify.getRemark())){
			dc.createAlias("this.supplier", "supplier");
			dc.add(Restrictions.eq("supplier.id",psiSupplierIndemnify.getRemark()));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiSupplierIndemnifyDao.find(page, dc);
	}
	
	public Page<PsiSupplier> find(Page<PsiSupplier> page, PsiSupplier supplier) {
		DetachedCriteria dc = psiSupplierDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(supplier.getName())) {
			dc.add(Restrictions.or(Restrictions.like("name", "%" + supplier.getName() + "%"),Restrictions.like("nikename", "%" + supplier.getName() + "%")));
		}
		if(StringUtils.isNotEmpty(supplier.getType())){
			dc.add(Restrictions.eq("type", supplier.getType()));
		}
		if(StringUtils.isNotEmpty(supplier.getCurrencyType())){
			dc.add(Restrictions.eq("currencyType", supplier.getCurrencyType()));
		}
		if(StringUtils.isNotBlank(supplier.getEliminate())){
			dc.add(Restrictions.eq("eliminate", supplier.getEliminate()));
		}
		
		//对采购经理、跟单、产品经理   进行权限控制
		String roleStr = UserUtils.getUser().getRoleNames()+",";
		String userId = UserUtils.getUser().getId();
		List<Integer> supplierIds=Lists.newArrayList();
		
		Set<String> permissionsSet = Sets.newHashSet();
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			for(Role role:UserUtils.getUser().getRoleList()){
				permissionsSet.addAll(role.getPermissions());
			}
			if(!permissionsSet.contains("psi:product:reviewPrice")){//如果不包含价格审批权限，过滤
				if(UserUtils.hasPermission("psi:purchase:manager")){//采购经理
					//如果是采购经理
					List<String> types = this.getSupplierByPurchaseUserId(userId);
					if(types==null||types.size()==0){
						supplierIds.add(0);
					}else{
						supplierIds=this.getSupplierByType(types);
					}
					dc.add(Restrictions.in("id", supplierIds));
				}else if(UserUtils.hasPermission("psi:order:edit")){
					//如果是跟单员
					supplierIds = this.getSupplierByFollowUserId(userId);
					if(supplierIds==null||supplierIds.size()==0){
						supplierIds=Lists.newArrayList();
						supplierIds.add(0);
					}
					dc.add(Restrictions.in("id", supplierIds));
				}else if(UserUtils.hasPermission("psi:product:manager")){
					//如果是产品经理
					List<String> types = this.getSupplierByManagerUserId(userId);
					if(types==null||types.size()==0){
						dc.add(Restrictions.eq("createUser.id", userId));
					}else{
						supplierIds=this.getSupplierByType(types);
						if(supplierIds==null||supplierIds.size()==0){
							supplierIds=Lists.newArrayList();
							supplierIds.add(0);
						}
						dc.add(Restrictions.or(Restrictions.in("id", supplierIds),Restrictions.eq("createUser.id", userId)));
					}
				}	
			}
		}
		
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiSupplierDao.find(page, dc);
	}
	
	/**
	 * 获取产品供应商
	 * @return
	 */
	public List<PsiSupplier> findAll() {
		DetachedCriteria dc = psiSupplierDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", "0"));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiSupplierDao.find(dc);
	}
	
	public List<PsiSupplier> findAll(PurchaseOrder purchaseOrder) {
		DetachedCriteria dc = psiSupplierDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", "0"));
		dc.add(Restrictions.eq("delFlag", "0"));
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("id", purchaseOrder.getSupplier().getId()));
		}
		return psiSupplierDao.find(dc);
	}
	
	public List<PsiSupplier> findAll(LcPurchaseOrder purchaseOrder) {
		DetachedCriteria dc = psiSupplierDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", "0"));
		dc.add(Restrictions.eq("delFlag", "0"));
		if(purchaseOrder.getSupplier()!=null&&purchaseOrder.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("id", purchaseOrder.getSupplier().getId()));
		}
		return psiSupplierDao.find(dc);
	}
	
	public List<PsiSupplier> findAllTransporter() {
		DetachedCriteria dc = psiSupplierDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", "1"));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiSupplierDao.find(dc);
	}
	
	
	/**
	 *根据供应商类型获取供应商列表 
	 * 
	 */
	public List<PsiSupplier> findSupplierByType(String[] types) {
		DetachedCriteria dc = psiSupplierDao.createDetachedCriteria();
		dc.add(Restrictions.in("type", types));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiSupplierDao.find(dc);
	}
	
	public PsiSupplier get(Integer id){
		return psiSupplierDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiSupplier psiSupplier){
		psiSupplierDao.save(psiSupplier);
	}
	
	/**
	 * 逻辑删除
	 */
	@Transactional(readOnly = false)
	public int delete(Integer id){
		return psiSupplierDao.deleteById(id);
	}
	
	@Transactional(readOnly = false)
	public int eliminate(Integer id,String type){
		Parameter parameter =new Parameter(type,id);
		return this.psiSupplierDao.updateBySql("update psi_supplier set eliminate=:p1 where id =:p2",parameter);
	}
	
	public boolean nameIsExsit(String name){
		DetachedCriteria dc = psiSupplierDao.createDetachedCriteria();
		dc.add(Restrictions.eq("nikename",name));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiSupplierDao.count(dc) >0;
	}
	@Transactional(readOnly = false)
	public void updateSuffixName(Integer id,String suffixName) {
		Parameter parameter =new Parameter(suffixName,id);
		this.psiSupplierDao.updateBySql("update psi_supplier set suffix_name=:p1 where id =:p2",parameter);
	}
	
	
	/**
	 * 根据状态查出订单
	 * 
	 */
	public Map<Integer,String> getIdNameCurrency(Map<Integer,String> refMap){
		Map<Integer,String> supplierMap=Maps.newTreeMap();
		String sql ="SELECT a.`id`,a.`nikename`,a.`currency_type` FROM psi_supplier AS a  WHERE (a.`type`='0' or  a.`type`='2')  AND a.`del_flag`='0'";
		List<Object[]> objects=this.psiSupplierDao.findBySql(sql);
		for(Object[] object:objects){
			supplierMap.put(Integer.parseInt(object[0].toString()), object[1].toString());
			refMap.put(Integer.parseInt(object[0].toString()), object[2].toString());
		}
		return supplierMap;
	}
	
	
	/**
	 * 查询供应商的产品
	 */
	public Map<Integer,List<String>> getSupplierProducts(){
		Map<Integer,List<String>> tempMap=Maps.newHashMap();
		String sql ="SELECT b.`supplier_id`,CONCAT(a.`brand`,' ',a.`model`) AS productName,a.`color` FROM psi_product AS a ,psi_product_supplier AS b WHERE   a.id=b.`product_id` AND a.`del_flag`='0'";
		List<Object[]> objects=this.psiSupplierDao.findBySql(sql);
		for(Object[] object:objects){
			String colors = object[2].toString();
			for(String color:colors.split(",")){
				String  productName =object[1].toString();
				if(StringUtils.isNotEmpty(color)){
					productName=productName+"_"+color;
				}
				Integer supplierId =Integer.parseInt(object[0].toString());
				List<String> list = null;
				if(tempMap.get(supplierId)==null){
					list=Lists.newArrayList();
				}else{
					list=tempMap.get(supplierId);
				}
				list.add(productName);
				tempMap.put(supplierId, list);
			}
		}
		return tempMap;
	}
	
	/**
	 * 查询供应商的产品(无颜色)
	 */
	public Map<Integer,List<String>> getSupplierProducts2(){
		Map<Integer,List<String>> tempMap=Maps.newHashMap();
		String sql ="SELECT b.`supplier_id`,CONCAT(a.`brand`,' ',a.`model`) AS productName FROM psi_product AS a ,psi_product_supplier AS b WHERE   a.id=b.`product_id` AND a.`del_flag`='0'";
		List<Object[]> objects=this.psiSupplierDao.findBySql(sql);
		for(Object[] object:objects){
			String  productName =object[1].toString();
			Integer supplierId =Integer.parseInt(object[0].toString());
			List<String> list = null;
			if(tempMap.get(supplierId)==null){
				list=Lists.newArrayList();
			}else{
				list=tempMap.get(supplierId);
			}
			list.add(productName);
			tempMap.put(supplierId, list);
		}
		return tempMap;
	}
	
	public boolean shortNameIsExsit(String name){
		DetachedCriteria dc = psiSupplierDao.createDetachedCriteria();
		dc.add(Restrictions.eq("shortName",name));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiSupplierDao.count(dc) >0;
	}

	//产品&供应商对应关系
	public Map<Integer, Integer> findProductSupplier(){
		Map<Integer, Integer> supplierMap = Maps.newHashMap();
		String sql ="SELECT t.`product_id`,t.`supplier_id` FROM psi_product_supplier t";
		List<Object[]> objects = psiSupplierDao.findBySql(sql);
		for(Object[] obj : objects){
			supplierMap.put(Integer.parseInt(obj[0].toString()), Integer.parseInt(obj[1].toString()));
		}
		return supplierMap;
	}
	
	/**
	 * 根据跟单员id,查询所管辖的供应商
	 */
	public List<Integer> getSupplierByFollowUserId(String followUserId){
		String sql ="(SELECT DISTINCT b.`supplier_id` FROM psi_product AS a,psi_product_supplier AS b WHERE a.id=b.`product_id` AND a.`create_user`=:p1 AND a.`del_flag`='0')" +
				" UNION (SELECT DISTINCT a.`supplier_id` FROM psi_parts AS a WHERE a.`del_flag`='0') ";
		List<Object> list=psiSupplierDao.findBySql(sql,new Parameter(followUserId));
		if(list!=null&&list.size()>0){
			List<Integer> temp = Lists.newArrayList();
			for(Object obj:list){
				temp.add(Integer.parseInt(obj.toString()));
			}
			return temp;
		}
		return null;
	}
	
	/**
	 * 根据跟单员id,查询所管辖的供应商
	 */
	public List<Integer> getSupplierByType(List<String> types){
		String sql ="SELECT DISTINCT b.`supplier_id` FROM psi_product AS a,psi_product_supplier AS b WHERE a.id=b.`product_id` AND a.`del_flag`='0' AND a.`TYPE` IN :p1 ";
		return  psiSupplierDao.findBySql(sql,new Parameter(types));
	}

	/**
	 * 采购经理id,查询所管辖的供应商
	 */
	public List<String> getSupplierByPurchaseUserId(String purchaseUserId){
		String sql ="SELECT b.`value` FROM psi_product_purchase_group AS a,sys_dict AS b WHERE a.`dict_id`=b.`id` AND a.`user_id`=:p1 ";
		return  psiSupplierDao.findBySql(sql,new Parameter(purchaseUserId));
	}
	
	/**
	 * 产品经理id,查询所管辖的供应商
	 */
	public List<String> getSupplierByManagerUserId(String ManagerUserId){
		//String sql ="SELECT b.`value` FROM psi_product_manage_group AS a,sys_dict AS b WHERE a.`dict_id`=b.`id` AND a.`user_id`=:p1 ";
		String sql="SELECT  DISTINCT p.type FROM ( "+
				"	SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,dict_id  "+
				"	FROM psi_product_manage_group a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1)  "+
				"	) d JOIN psi_product_type_group g ON d.dict_id=g.id "+
				"	JOIN sys_dict t ON g.`dict_id`=t.id AND t.`del_flag`='0' AND  t.`type`='product_type' "+
				"	JOIN psi_product p ON p.type=t.value AND p.del_flag='0' "+
				"	JOIN sys_user r ON r.id=d.userId AND r.del_flag='0' where r.id=:p1 ";
		return  psiSupplierDao.findBySql(sql,new Parameter(ManagerUserId));
	}

	/**
	 *查询跟单对应的产品id
	 */
	public List<Integer> getProductIdsByFollowId(String followUserId){
		String sql ="SELECT a.`id` FROM psi_product AS a WHERE a.`create_user`=:p1 ";
		return  psiSupplierDao.findBySql(sql,new Parameter(followUserId));
	}
	
	
	
	/**
	 * 获取产品供应商
	 * @return
	 */
	public Map<Integer,PsiSupplier> findAllMap() {
		 Map<Integer,PsiSupplier> rsMap =Maps.newHashMap();
		DetachedCriteria dc = psiSupplierDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", "0"));
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiSupplier> suppliers=psiSupplierDao.find(dc);
		for(PsiSupplier supplier:suppliers){
			rsMap.put(supplier.getId(), supplier);
		}
		return rsMap;
	}
	
	
	/**
	 * 获取产品供应商
	 * @return
	 */
	public Map<String,PsiSupplier> findNikeMap() {
		 Map<String,PsiSupplier> rsMap =Maps.newHashMap();
		DetachedCriteria dc = psiSupplierDao.createDetachedCriteria();
		dc.add(Restrictions.eq("type", "0"));
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiSupplier> suppliers=psiSupplierDao.find(dc);
		for(PsiSupplier supplier:suppliers){
			rsMap.put(supplier.getNikename(), supplier);
		}
		return rsMap;
	}
}
