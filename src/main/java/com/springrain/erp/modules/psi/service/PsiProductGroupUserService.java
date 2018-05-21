/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.web.PriceFeedController;
import com.springrain.erp.modules.psi.dao.PsiProductGroupCustomerDao;
import com.springrain.erp.modules.psi.dao.PsiProductGroupCustomerEmailDao;
import com.springrain.erp.modules.psi.dao.PsiProductGroupPhotoDao;
import com.springrain.erp.modules.psi.dao.PsiProductGroupUserDao;
import com.springrain.erp.modules.psi.entity.PsiProductGroupCustomer;
import com.springrain.erp.modules.psi.entity.PsiProductGroupCustomerEmail;
import com.springrain.erp.modules.psi.entity.PsiProductGroupPhoto;
import com.springrain.erp.modules.psi.entity.PsiProductGroupUser;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.Menu;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 类型分组Service
 * @author 
 * @version 2015-6-11
 */
@Component
@Transactional(readOnly = true)
public class PsiProductGroupUserService extends BaseService {
	@Autowired
	private PsiProductGroupUserDao 			groupUserDao;
	@Autowired
	private MailManager						mailManager;  
	@Autowired
	private PsiProductTypeGroupDictService  productGroupService;
	@Autowired
	private PsiProductGroupCustomerDao 		groupCustomerDao;
	@Autowired
	private PsiProductGroupCustomerEmailDao groupCustomerEmailDao;
	@Autowired
	private PsiProductGroupPhotoDao 		groupPhotoDao;
	@Autowired
	private SystemService systemService;
	
	private static Logger logger = LoggerFactory.getLogger(PriceFeedController.class);
	
	public PsiProductGroupUser get(String id) {
		return groupUserDao.get(id);
	}
	public List<PsiProductGroupUser> getAll(){
		DetachedCriteria dc = groupUserDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag","0" ));
		return groupUserDao.find(dc);
	}
	
	/** 查询产品组用户关系
	 *产品线：国家：list人 
	 */
	public Map<String,Map<String,List<User>>> getGroupUser(){
		DetachedCriteria dc = groupUserDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag","0" ));
		List<PsiProductGroupUser> groupUsers=groupUserDao.find(dc);
		Map<String,Map<String,List<User>>> resMap = Maps.newHashMap();
	    if(groupUsers!=null&&groupUsers.size()>0){
	    	for(PsiProductGroupUser groupUser:groupUsers ){
	    		String groupId = groupUser.getProductGroupId();
	    		String country = groupUser.getCountry();
	    		String userIds = groupUser.getResponsible();
	    		Map<String,List<User>>  countryMap = null;
	    		if(resMap.get(groupId)==null){
	    			countryMap=Maps.newHashMap();
	    		}else{
	    			countryMap=resMap.get(groupId);
	    		}
	    		List<User>  userList = null;
	    		if(countryMap.get(country)==null){
	    			userList = Lists.newArrayList();
	    		}else{
	    			userList = countryMap.get(country);
	    		}
	    		for (String userId : userIds.split(",")) {
	    			userList.add(systemService.getUser(userId));
				}
	    		countryMap.put(country,userList);
	    		resMap.put(groupId, countryMap);
	    	}
	    }
	 	return resMap;
	}
	
	
	/** 查询产品组用户关系平台负责人 只有一个人
	 *产品线：国家：人id 
	 */
	public Map<String,Map<String,String>> getSingleGroupUser(){
		DetachedCriteria dc = groupUserDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag","0" ));
		dc.add(Restrictions.eq("productGroupId","0" ));
		List<PsiProductGroupUser> groupUsers=groupUserDao.find(dc);
		Map<String,Map<String,String>> resMap = Maps.newHashMap();
	    if(groupUsers!=null&&groupUsers.size()>0){
	    	for(PsiProductGroupUser groupUser:groupUsers ){
	    		String groupId = groupUser.getProductGroupId();
	    		String country = groupUser.getCountry();
	    		if (StringUtils.isEmpty(groupUser.getResponsible())) {
					continue;
				}
	    		User user = systemService.getUser(groupUser.getResponsible());
	    		Map<String,String>  countryMap = null;
	    		if(resMap.get(groupId)==null){
	    			countryMap=Maps.newHashMap();
	    		}else{
	    			countryMap=resMap.get(groupId);
	    		}
	    		if(user!=null){
	    			countryMap.put(country, user.getId()+","+user.getName());
	    			resMap.put(groupId, countryMap);
	    		}
	    	}
	    }
	 	return resMap;
	}
	
	
	/** 
	 * 查询产品线负责人(支持多人负责同一平台同一产品线)
	 */
	public Map<String,Map<String,List<PsiProductGroupUser>>> getLineGroupSales(){
		Map<String,Map<String,List<PsiProductGroupUser>>> map=Maps.newHashMap();
		String sql="SELECT DISTINCT product_group_id,country,SUBSTRING_INDEX(SUBSTRING_INDEX(a.responsible,',',b.help_topic_id+1),',',-1) userId,r.`name`,a.id  "+
		 " FROM psi_product_group_user a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.responsible) - LENGTH(REPLACE(a.responsible,',',''))+1)  "+
		 " JOIN sys_user r ON r.id=SUBSTRING_INDEX(SUBSTRING_INDEX(a.responsible,',',b.help_topic_id+1),',',-1) AND r.del_flag='0' WHERE  a.`del_flag`='0' AND a.`product_group_id`!='0' ";
		List<Object[]> list=groupCustomerDao.findBySql(sql);
		for (Object[] obj: list) {
			String lineId=obj[0].toString();
			String country=obj[1].toString();
			String userId=obj[2].toString();
			String name=obj[3].toString();
			if(systemService.hasPerssion(userId,"amazoninfo:feedSubmission:")){
				Map<String,List<PsiProductGroupUser>> temp=map.get(lineId);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(lineId,temp);
				}
				List<PsiProductGroupUser> salesList=temp.get(country);
				if(salesList==null){
					salesList=Lists.newArrayList();
					temp.put(country,salesList);
				}
				PsiProductGroupUser sales=new PsiProductGroupUser();
				sales.setResponsible(userId);
				sales.setName(name);
				sales.setCountry(country);
				sales.setProductGroupId(lineId);
				sales.setId(Integer.parseInt(obj[4].toString()));
				salesList.add(sales);
			}
			
		}
		return map;
	}
	
	public List<PsiProductGroupUser> getAllByGroupIdCountry(String groupId,String country,String userId){
		DetachedCriteria dc = groupUserDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(groupId)){
			dc.add(Restrictions.eq("productGroupId",groupId));
		}
		
		if(StringUtils.isNotEmpty(country)){
			dc.add(Restrictions.eq("country",country ));
		}
		
		if(StringUtils.isNotEmpty(userId)){
			dc.add(Restrictions.like("responsible", "%" + userId + ""));
		}
		dc.add(Restrictions.eq("delFlag","0" ));
		return groupUserDao.find(dc);
	}
	
	
	
	

	/** 查询用户对应的，产品类型和国家
	 *  人员：国家：产品类型
	 */
	public Map<String,Map<String,List<String>>> getProductGroupCountry(){
		String sql="SELECT a.`country`,a.`responsible`,d.`value` FROM psi_product_group_user AS a ,psi_product_type_dict AS b,psi_product_type_group AS c,sys_dict AS d " +
				"WHERE a.`product_group_id`=b.`id` AND b.id=c.`id` AND c.`dict_id`=d.`id` AND a.`del_flag`='0' AND b.`del_flag`='0' AND d.`del_flag`='0'";
		List<Object[]> list=groupUserDao.findBySql(sql);
		Map<String,Map<String,List<String>>>  resMap =Maps.newHashMap();
	    if(list!=null&&list.size()>0){
	    	for(Object[] obj:list ){
	    		String country=obj[0].toString();
	    		String userId=obj[1].toString();
	    		String productType=obj[2].toString();
	    		Map<String,List<String>> countryMap=null;
	    		for (String id : userId.split(",")) {
	    			if(resMap.get(id)==null){
		    			countryMap=Maps.newHashMap();
		    		}else{
		    			countryMap=resMap.get(id);
		    		}
		    		
		    		List<String> types =null;
		    		if(countryMap.get(country)==null){
		    			types=Lists.newArrayList();
		    		}else{
		    			types=countryMap.get(country);
		    		}
		    		types.add(productType);
		    		countryMap.put(country, types);
		    		resMap.put(id, countryMap);
				}
	    	}
	    }
	 	return resMap;
	}
	
	/** 根据国家、产品id，查出负责人
	 */
	public String getResponsibleByCountryProductId(String country ,Integer productId){
		String sql="SELECT a.`responsible` FROM psi_product_group_user AS a ,psi_product_type_dict AS b," +
				"psi_product_type_group AS c,sys_dict AS d ,psi_product AS e WHERE a.`product_group_id`=b.`id` AND b.id=c.`id` AND c.`dict_id`=d.`id`" +
				" AND e.`TYPE`=d.`value` AND a.`del_flag`='0' AND b.`del_flag`='0' AND d.`del_flag`='0' AND a.`country`=:p1 AND e.id=:p2  ";
	 	List<String> userIds= groupUserDao.findBySql(sql,new Parameter(country,productId));
	 	if(userIds!=null&&userIds.size()>0){
	 		return userIds.get(0);
	 	}
	 	return null;
	}
	
	/** 根据国家 查出产品负责人 email 逗号分隔
	 */
	public Map<String,String> getResponsibleByCountry(String country){
		Map<String,String> resMap =Maps.newHashMap();
		String sql="SELECT DISTINCT b.`name`,b.`email` FROM psi_product_group_user AS a ,sys_user AS b WHERE (a.`responsible` LIKE CONCAT('%,',b.`id`) OR a.`responsible` LIKE CONCAT(b.`id`,',%') OR a.`responsible` LIKE CONCAT(CONCAT('%,',b.`id`),',%')) " +
				"AND a.`country`=:p1 AND b.`del_flag`='0' AND a.`del_flag`='0'";
	 	List<Object[]> objs= groupUserDao.findBySql(sql,new Parameter(country));
	 	if(objs!=null&&objs.size()>0){
	 		for(Object[] obj:objs){
	 			resMap.put(obj[0].toString(), obj[1].toString());
	 		}
	 	}
	 	return resMap;
	}
	
	
	/** 
	 * 获取当前人用户负责的产品，分颜色  :Inateck MP1300_gray_country
	 * 
	 */
	public List<String> getProductByGroupUser(){
		List<String> resList =Lists.newArrayList();
		Set<String> resSet = Sets.newHashSet();
		String userId = UserUtils.getUser().getId();
		String sql="SELECT DISTINCT CONCAT(e.proName,'_',a.country)  FROM psi_product_group_user AS a ,psi_product_type_dict AS b,psi_product_type_group AS c,sys_dict AS d ,(SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName" +
				" ,a.`TYPE` FROM psi_product a JOIN mysql.help_topic b  ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) AND a.`del_flag`='0') AS e " +
				" WHERE a.`product_group_id`=b.`id` AND b.id=c.`id` AND c.`dict_id`=d.`id` AND  e.type=d.value AND a.`del_flag`='0' AND b.`del_flag`='0' " +
				" AND d.`del_flag`='0' AND a.`responsible` like :p1 ";
		List<String> objs= groupUserDao.findBySql(sql,new Parameter("%"+userId+"%"));
		if(objs!=null&&objs.size()>0){
			resSet.addAll(objs);
		}
	 	//判断产品是不是平台负责人
		Set<String> countrys = Sets.newHashSet();
		
		Map<String,User> countryMap =getCountryManager();
		for(Map.Entry<String,User> entry:countryMap.entrySet()){
			String country = entry.getKey();
			if(entry.getValue()!=null&&(entry.getValue().getId().equals(userId))){
				countrys.add(country);
			}
		}
	 	if(countrys.size()>0){
	 		 sql="SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName" +
	 				" FROM psi_product a JOIN mysql.help_topic b  ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) AND a.`del_flag`='0'";
		 	List<String> allProduct = this.groupUserDao.findBySql(sql);
	 		for(String country:countrys){
	 			for(String proName:allProduct){
	 				resSet.add(proName+"_"+country);
	 			}
	 		}
	 	}
	 	
	 	if(resSet.size()>=0){
	 		resList.addAll(resSet);
	 	}
	 	return resList;
	}
	
	
	/** 根据产品type获得skulist
	 */
	public List<String> getSkuByProductType(List<String> productTypes){
		String sql="SELECT DISTINCT a.`sku` FROM psi_sku AS a ,psi_product AS b WHERE a.`product_id`=b.`id` AND a.`del_flag`='0' AND b.`del_flag`='0' AND b.type in :p1 ";
		List<String> list=groupUserDao.findBySql(sql,new Parameter(productTypes));
	 	return list;
	}
	
	
	
	/** 查询产品组用户关系
	 *国家：list人
	 */
	public Map<String,String> getGroupUserByGroupCountry(String groupId,String country){
		Parameter para = null;
		String app="";
		if(StringUtils.isNotEmpty(country)){
			app=" AND a.`country`=:p2 ";
			para = new Parameter(groupId,country);
		}else{
			para = new Parameter(groupId);
		}
		String sql="SELECT a.`country`,GROUP_CONCAT(a.`responsible` ORDER BY a.`create_time`) FROM psi_product_group_user AS a WHERE a.`del_flag`='0' AND a.`product_group_id`=:p1 "+app+" GROUP BY a.`country`";

		List<Object[]> list=groupUserDao.findBySql(sql, para);
		Map<String,String>  resMap =Maps.newHashMap();
	    if(list!=null&&list.size()>0){
	    	for(Object[] obj:list ){
	    		resMap.put(obj[0].toString(), obj[1]+",");
	    	}
	    }
	 	return resMap;
	}
	
	
	
	/** 查询平台负责人
	 */
	public Map<String,User> getCountryManager(){
		String sql="SELECT a.`country`,a.`responsible` FROM psi_product_group_user AS a WHERE a.`del_flag`='0' AND a.`product_group_id`='0'";
		List<Object[]> list=groupUserDao.findBySql(sql);
		Map<String,User>  resMap =Maps.newHashMap();
	    if(list!=null&&list.size()>0){
	    	for(Object[] obj:list ){
	    		User user = null;
	    		if(obj[1]!=null&&!"".equals(obj[1].toString())){
	    			user = UserUtils.getUserById(obj[1].toString());
	    		}
	    		resMap.put(obj[0].toString(), user);
	    	}
	    }
	 	return resMap;
	}
	
	/** 各平台相关人员
	 */
	public Map<String,Set<User>> getRelitiveCountryManager(){
		String sql="SELECT a.`country`,GROUP_CONCAT(DISTINCT(a.`responsible`)) FROM psi_product_group_user AS a WHERE  a.`del_flag`='0' GROUP BY a.`country` ";
		List<Object[]> list=groupUserDao.findBySql(sql);
		Map<String,Set<User>>  resMap =Maps.newHashMap();
	    if(list!=null&&list.size()>0){
	    	for(Object[] obj:list ){
	    		if(obj[1]!=null&&!"".equals(obj[1].toString())){
	    			Set<User> userSet = Sets.newHashSet();
	    			for(String userId:obj[1].toString().split(",")){
	    				User user = UserUtils.getUserById(userId);
	    				userSet.add(user);
	    			}
	    			resMap.put(obj[0].toString(), userSet);
	    		}
	    	}
	    }
	 	return resMap;
	}


	
	
	
	@Transactional(readOnly = false)
	public void save(PsiProductGroupUser groupUser) {
		groupUserDao.save(groupUser);
	}
	
	/**
	 *获取不同国家具有上贴权限的人员 
	 *key:country  value:id,name
	 */
	public Map<String,List<String>> getOnShelves() {
		Map<String,List<String>> rs = Maps.newHashMap();
		String sql ="SELECT DISTINCT CONCAT(b.id,',',b.name),m.`permission` FROM  sys_role AS a,sys_user AS b,sys_user_role AS c ,sys_menu AS m, sys_role_menu s "+
				" WHERE a.id=c.`role_id` AND b.`id`=c.`user_id` AND a.`id`=s.`role_id` AND m.`id`=s.`menu_id`  "+
				" AND a.`del_flag`='0' AND b.`del_flag`='0' AND m.`del_flag`='0' AND  m.`permission` LIKE 'amazoninfo:feedSubmission:%' "+
				" AND m.`permission`!='amazoninfo:feedSubmission:view' AND m.`permission`!='amazoninfo:feedSubmission:all'";
		List<Object[]> list =  groupUserDao.findBySql(sql);
		List<Dict> dicts = DictUtils.getDictList("platform");
		for(Object[] obj:list){
			String userInfo =obj[0].toString();
			String permission = obj[1].toString();
			String countryVaule = permission.substring(permission.lastIndexOf(":")+1);
			for (Dict dict : dicts) {
				if(dict.getValue().equals(countryVaule)){	//国家存在
					List<String> users= null;
					if(rs.get(countryVaule)==null){
						users=Lists.newArrayList();
					}else{
						users=rs.get(countryVaule);
					}
					users.add(userInfo);
					rs.put(countryVaule, users);
					break;
				}
			}
		}
		return rs;
	}
	
	
	/***
	 *根据产品线、国家、及时的用户情况，更新产品线用户关系       多用户情况
	 * 
	 */
	@Transactional(readOnly = false)
	public  void  editGroupUserRelivate(String groupId,String country ,String userId){
		//根据产品id  颜色    查出目前的关系，
		List<PsiProductTypeGroupDict>  list = productGroupService.getAllList();
		Map<String,PsiProductTypeGroupDict> groupMap = Maps.newHashMap();
		for(PsiProductTypeGroupDict group:list){
			groupMap.put(group.getId(), group);
		}
		StringBuilder content =new StringBuilder();
		List<PsiProductGroupUser> groupUsers= this.getAllByGroupIdCountry(groupId, country,null);
		PsiProductGroupUser groupUser = null;
		if (groupUsers == null || groupUsers.size() == 0) {
			groupUser = new PsiProductGroupUser();
			groupUser.setCountry(country);
			groupUser.setProductGroupId(groupId);
			groupUser.setDelFlag("0");
		} else {
			groupUser = groupUsers.get(0);
		}
		groupUser.setResponsible(userId);
		groupUser.setCreateTime(new Date());
		groupUser.setCreateUser(UserUtils.getUser());
		save(groupUser);
		
		//发信通知  manager@inateck.com  组里的成员
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			String emailAddress="manager@inateck.com";
			final MailInfo mailInfo = new MailInfo(emailAddress,"产品线平台负责人关系变更通知"+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent("Hi,All<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;"+content+"<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;以上、请知悉!");
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
	}
	
	/**
	 *删除用户的时候调用接口 
	 */
	@Transactional(readOnly = false)
	public void deleteProductGroupUser(User delUser,Role singleRole){
		try{
		Set<Role> roles = delUser.getRoleList();
		Set<String> countrys = Sets.newHashSet();
		List<Dict> dicts = DictUtils.getDictList("platform");
		if(singleRole!=null){
			List<Menu> menuList=singleRole.getMenuList();
			for (Menu menu : menuList) {
				String permission=menu.getPermission();
				if(StringUtils.isNotBlank(permission)&&permission.contains("amazoninfo:feedSubmission:")){
					for (Dict dict : dicts) {
						if(permission.equals("amazoninfo:feedSubmission:"+dict.getValue())){
							countrys.add(dict.getValue());
						}
					}
				}
			}
		}else{
			for (Role role : roles) {
				List<Menu> menuList=role.getMenuList();
				for (Menu menu : menuList) {
					String permission=menu.getPermission();
					if(StringUtils.isNotBlank(permission)&&permission.contains("amazoninfo:feedSubmission:")){
						for (Dict dict : dicts) {
							if(permission.equals("amazoninfo:feedSubmission:"+dict.getValue())){
								countrys.add(dict.getValue());
							}
						}
					}
				}
			}
		}
		
		// 该用户如果是产品上架员    删除 产品线、平台、用户  关系
		if(countrys!=null&&countrys.size()>0){
			for(String country:countrys){
				List<PsiProductGroupUser> users =this.getAllByGroupIdCountry(null, country, delUser.getId());
				for(PsiProductGroupUser gu :users){
					gu.setDelFlag("1");
					this.groupUserDao.save(gu);
				}
			}
		}
		}catch(Exception ex){
			logger.error("删除用户解除产品线用户关系失败！！！"+ex.getMessage());
		}
		
	}
	
	
	@Transactional(readOnly = false)
	public void save(PsiProductGroupCustomer groupCustomer) {
		groupCustomerDao.save(groupCustomer);
	}
	
	@Transactional(readOnly = false)
	public void saveCustomerEmail(PsiProductGroupCustomerEmail groupCustomerEmail) {
		groupCustomerEmailDao.save(groupCustomerEmail);
	}
	
	@Transactional(readOnly = false)
	public void savePhoto(PsiProductGroupPhoto photo) {
		groupPhotoDao.save(photo);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(String country,String lineId,Set<String> userIdSet) {
		String sql="update psi_product_group_customer set del_flag='1' where line_id=:p1 and country=:p2 and user_id not in :p3";
		groupCustomerDao.updateBySql(sql, new Parameter(lineId,country,userIdSet));
	}
	
	@Transactional(readOnly = false)
	public void deleteEmail(String country,String lineId,Set<String> userIdSet) {
		String sql="update psi_product_group_customer_email set del_flag='1' where line_id=:p1 and country=:p2 and user_id not in :p3";
		groupCustomerDao.updateBySql(sql, new Parameter(lineId,country,userIdSet));
	}
	
	public Map<String,Map<String,List<PsiProductGroupCustomer>>> findAllGroupCustomer(){
		Map<String,Map<String,List<PsiProductGroupCustomer>>> map=Maps.newHashMap();
		String sql="SELECT distinct line_id,country,SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,r.`name`,a.id  "+
		" FROM psi_product_group_customer a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1) "+
		" JOIN sys_user r ON r.id=SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) and r.del_flag='0' WHERE  a.`del_flag`='0' ";
		List<Object[]> list=groupCustomerDao.findBySql(sql);
		for (Object[] obj: list) {
			String lineId=obj[0].toString();
			String country=obj[1].toString();
			String userId=obj[2].toString();
			String name=obj[3].toString();
			if(systemService.hasPerssion(userId,"event:service:")){
				Map<String,List<PsiProductGroupCustomer>> temp=map.get(lineId);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(lineId,temp);
				}
				List<PsiProductGroupCustomer> customerList=temp.get(country);
				if(customerList==null){
					customerList=Lists.newArrayList();
					temp.put(country,customerList);
				}
				PsiProductGroupCustomer customer=new PsiProductGroupCustomer();
				customer.setUserId(userId);
				customer.setName(name);
				customer.setCountry(country);
				customer.setLineId(lineId);
				customer.setId(Integer.parseInt(obj[4].toString()));
				customerList.add(customer);
			}
			
		}
		return map;
	}
	
	
	public Map<String,PsiProductGroupPhoto> findAllGroupPhoto(){
		DetachedCriteria dc = groupPhotoDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag","0" ));
		List<PsiProductGroupPhoto> list=this.groupPhotoDao.find(dc);
		Map<String,PsiProductGroupPhoto> rsMap = Maps.newHashMap();
		for(PsiProductGroupPhoto po:list){
			rsMap.put(po.getLineId(), po);
		}
		return rsMap;
	}
	
	public Map<String,String> findGroupManager(){
		 Map<String,String> map=Maps.newHashMap();
		 String sql="select user_id,dict_id from psi_product_manage_group";
		 List<Object[]> list=groupCustomerDao.findBySql(sql);
		 for (Object[] obj : list) {
			map.put(obj[1].toString(), obj[0].toString());
		 }	
		 return map;
	}
	
	
	public Map<String,Map<String,List<PsiProductGroupCustomerEmail>>> findAllGroupCustomerEmail(){
		Map<String,Map<String,List<PsiProductGroupCustomerEmail>>> map=Maps.newHashMap();
		String sql="SELECT distinct line_id,country,SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,r.`name`,a.id  "+
		" FROM psi_product_group_customer_email a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1) "+
		" JOIN sys_user r ON r.id=SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) and r.del_flag='0' WHERE  a.`del_flag`='0' ";
		List<Object[]> list=groupCustomerDao.findBySql(sql);
		for (Object[] obj: list) {
			String lineId=obj[0].toString();
			String country=obj[1].toString();
			String userId=obj[2].toString();
			String name=obj[3].toString();
//			if(systemService.hasRole(userId,"custom")){
			if(systemService.hasPerssion(userId, "custom:service:")){
				Map<String,List<PsiProductGroupCustomerEmail>> temp=map.get(lineId);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(lineId,temp);
				}
				List<PsiProductGroupCustomerEmail> customerList=temp.get(country);
				if(customerList==null){
					customerList=Lists.newArrayList();
					temp.put(country,customerList);
				}
				PsiProductGroupCustomerEmail customer=new PsiProductGroupCustomerEmail();
				customer.setUserId(userId);
				customer.setName(name);
				customer.setCountry(country);
				customer.setLineId(lineId);
				customer.setId(Integer.parseInt(obj[4].toString()));
				customerList.add(customer);
			}
		}
		return map;
	}

	
	/**
	 *产品线名字,负责人邮箱 
	 */
	public Map<String,String> getEmailProductLine(){
		Map<String,String> rsMap=Maps.newHashMap();
		Map<String,String> lineMap = Maps.newHashMap();
		String sql=" SELECT a.id,a.`name` FROM psi_product_type_dict AS a WHERE a.`del_flag`='0'";
		List<Object[]> list=groupCustomerDao.findBySql(sql);
		for (Object[] obj: list) {
			lineMap.put(obj[0].toString(), obj[1].toString());
		}
		
		DetachedCriteria dc = groupUserDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag","0" ));
		List<PsiProductGroupUser> groupUsers=groupUserDao.find(dc);
	    if(groupUsers!=null&&groupUsers.size()>0){
	    	for(PsiProductGroupUser groupUser:groupUsers ){
	    		String groupId = groupUser.getProductGroupId();
	    		String userIds = groupUser.getResponsible();
	    		if(StringUtils.isEmpty(userIds)){
	    			continue;
	    		}
	    		for (String userId : userIds.split(",")) {
	    			User user = systemService.getUser(userId);
	    			if (user == null || StringUtils.isEmpty(user.getEmail())) {
						continue;
					}
	    			String singEmail = user.getEmail();
		    		String lineName = lineMap.get(groupId);
		    		String email ="";
		    		if(rsMap.get(lineName)==null){
		    			email=(singEmail==null)?"":singEmail;
		    		}else{
		    			email=rsMap.get(lineName)+","+singEmail;
		    		}
		    		rsMap.put(lineName, email);
				}
	    	}   
	    }
	 return rsMap;
	}
	
	public LinkedHashMap<String, LinkedHashMap<String, List<String>>> getSafePriceAndSalePrice(){
	    String sql = "SELECT b.`product_name`,b.`country`,SUBSTRING_INDEX(SUBSTRING_INDEX(product_name,'_',1),' ',-1) model,a.sale_price,b.`safe_price`"
                    +" FROM (SELECT (CASE WHEN color ='' THEN s.`product_name` ELSE CONCAT(s.`product_name`,'_',s.`color`) END) pro_name,s.`country`,MIN(p.`sale_price`) sale_price"
                    +" FROM psi_sku s,amazoninfo_product2 p WHERE p.`sale_price` IS NOT NULL AND p.`active`='1' AND s.`sku`=p.`sku` AND s.`country`=p.`country` AND s.`del_flag`='0'"
                    +" GROUP BY s.`product_name`,s.`country`,s.`color`) a RIGHT JOIN ebay_product_price b ON a.pro_name=b.product_name AND a.country=b.`country` WHERE  b.`update_date`=CURDATE()"
                    +" ORDER BY b.`product_name`";
	    List<Object[]> findBySql = groupUserDao.findBySql(sql);
	    LinkedHashMap<String, LinkedHashMap<String, List<String>>> map = new LinkedHashMap<String, LinkedHashMap<String,List<String>>>();
	    for(Object[] obj : findBySql){
	        String proName = obj[0].toString();
            String country = obj[1].toString();
            String model = obj[2].toString();
            String salePrice = obj[3] == null ? "" : obj[3].toString();
            String safePrice = obj[4] == null ? "" : obj[4].toString();
            List<String> list = new ArrayList<String>();
            list.add(model);
            list.add(salePrice);
            list.add(safePrice);
            if(map.containsKey(proName)){
                LinkedHashMap<String,List<String>> linkedHashMap = map.get(proName);
                linkedHashMap.put(country, list);
                map.put(proName, linkedHashMap);
            }else{
                LinkedHashMap<String, List<String>> mapTemp = new LinkedHashMap<String, List<String>>();
                mapTemp.put(country, list);
                map.put(proName, mapTemp);
            }
	    }
	    return map;
	}
}
