/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.springrain.erp.modules.sys.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.shiro.SecurityUtils;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.security.Digests;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.Collections3;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.dao.MenuDao;
import com.springrain.erp.modules.sys.dao.RoleDao;
import com.springrain.erp.modules.sys.dao.UserDao;
import com.springrain.erp.modules.sys.entity.Menu;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.security.SystemAuthorizingRealm;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 * @author ThinkGem
 * @version 2013-5-15
 */
@Service
@Transactional(readOnly = true)
public class SystemService extends BaseService  {
	private final static Logger LOGGER = LoggerFactory.getLogger(SystemService.class);
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private SystemAuthorizingRealm systemRealm;
	
	@Autowired
	private IdentityService identityService;
	
    public static Map<String,String> countryNameMap;
	
	static{
		countryNameMap=Maps.newHashMap();
		countryNameMap.put("de","德国DE");
		countryNameMap.put("fr","法国FR");
		countryNameMap.put("it","意大利IT");
		countryNameMap.put("es","西班牙ES");
		countryNameMap.put("uk","英国UK");
		countryNameMap.put("com","美国US");
		countryNameMap.put("ca","加拿大CA");
		countryNameMap.put("jp","日本JP");
		countryNameMap.put("mx","墨西哥MX");
		countryNameMap.put("com2","美国NEW");
		countryNameMap.put("com3","美国Tomons");
	}

	//-- User Service --//
	
	public User getUser(String id) {
		return userDao.get(id);
	}
	
	public List<User> findAllUsers() {
		return userDao.findAll();
	}
	
	
	public List<User> findActiveUsers() {
		DetachedCriteria dc = userDao.createDetachedCriteria();
		if (!UserUtils.getUser().isAdmin()){
			dc.add(Restrictions.ne("id", "1")); 
		}
		return userDao.find(dc);
	}
	
	public List<Object> findDual() {
		String sql="SELECT 1 FROM DUAL";
		return userDao.findBySql(sql);
	}
	
	
	public Page<User> findUser(Page<User> page, User user) {
		DetachedCriteria dc = userDao.createDetachedCriteria();
		User currentUser = UserUtils.getUser();
		dc.createAlias("company", "company");
		if (user.getCompany()!=null && StringUtils.isNotBlank(user.getCompany().getId())){
			dc.add(Restrictions.or(
					Restrictions.eq("company.id", user.getCompany().getId()),
					Restrictions.like("company.parentIds", "%,"+user.getCompany().getId()+",%")
					));
		}
		dc.createAlias("office", "office");
		if (user.getOffice()!=null && StringUtils.isNotBlank(user.getOffice().getId())){
			dc.add(Restrictions.or(
					Restrictions.eq("office.id", user.getOffice().getId()),
					Restrictions.like("office.parentIds", "%,"+user.getOffice().getId()+",%")
					));
		}
		// 如果不是超级管理员，则不显示超级管理员用户
		if (!currentUser.isAdmin()){
			dc.add(Restrictions.ne("id", "1")); 
		}
		dc.add(dataScopeFilter(currentUser, "office", ""));
		//System.out.println(dataScopeFilterString(currentUser, "office", ""));
		if (StringUtils.isNotEmpty(user.getLoginName())){
			dc.add(Restrictions.like("loginName", "%"+user.getLoginName()+"%"));
		}
		if (StringUtils.isNotEmpty(user.getName())){
			dc.add(Restrictions.like("name", "%"+user.getName()+"%"));
		}
		dc.add(Restrictions.eq(User.FIELD_DEL_FLAG, User.DEL_FLAG_NORMAL));
		if (!StringUtils.isNotEmpty(page.getOrderBy())){
			dc.addOrder(Order.asc("company.code")).addOrder(Order.asc("office.code")).addOrder(Order.desc("name"));
		}
		return userDao.find(page, dc);
	}

	
	public List<User> findRoleUser(User user) {
		DetachedCriteria dc = userDao.createDetachedCriteria();
		User currentUser = UserUtils.getUser();
		dc.createAlias("company", "company");
		dc.createAlias("office", "office");
		
		// 如果不是超级管理员，则不显示超级管理员用户
		if (!currentUser.isAdmin()){
			dc.add(Restrictions.ne("id", "1")); 
		}
		dc.add(dataScopeFilter(currentUser, "office", ""));
		
		if (StringUtils.isNotEmpty(user.getName())){
			dc.add(Restrictions.like("name", "%"+user.getName()+"%"));
		}
		dc.createAlias("roleList", "roleList");
		dc.add(Restrictions.in("roleList.id",currentUser.getRoleIdList())); 
		dc.add(Restrictions.in("office.id", currentUser.getOfficeIdList()));
		dc.add(Restrictions.eq(User.FIELD_DEL_FLAG, User.DEL_FLAG_NORMAL));
		return userDao.find(dc);
	}
	
	public List<Object[]> findSecondList(){
		User user = UserUtils.getUser();
		String sql="SELECT f.`name` fname,u.`name` uname,u.id uid,e.id eid,e.name ename,e.`update_date` FROM sys_user_role r "+
				" JOIN sys_user u ON r.`user_id`=u.id AND u.`del_flag`='0' "+
				" JOIN sys_role e ON r.`role_id`=e.id AND e.`del_flag`='0' "+
				" JOIN sys_office f ON f.id=u.`office_id` AND f.`del_flag`='0' "+
				" WHERE r.`update_date` IS NOT NULL ";
		if (!user.isAdmin()) {
			sql += " AND r.`update_user`=:p1";
			return userDao.findBySql(sql,new Parameter(UserUtils.getUser().getId()));
		} else {
			return userDao.findBySql(sql);
		}
	}
	
	
	//取用户的数据范围
	public String getDataScope(User user){
		return dataScopeFilterString(user, "office", "");
	}
	
	public User getUserByLoginName(String loginName) {
		return userDao.findByLoginName(loginName);
	}
	
	public User getUserByUserId(String userId) {
		return userDao.getByHql("from User where userId = :p1 and delFlag = :p2", new Parameter(userId, User.DEL_FLAG_NORMAL));
	}
	

	@Transactional(readOnly = false)
	public void saveUser(User user) {
		userDao.clear();
		userDao.save(user);
		systemRealm.clearAllCachedAuthorizationInfo();
		// 同步到Activiti
		saveActiviti(user);
	}

	@Transactional(readOnly = false)
	public void deleteUser(String id) {
		userDao.deleteById(id);
		// 同步到Activiti
		deleteActiviti(userDao.get(id));
	}
	
	@Transactional(readOnly = false)
	public void updatePasswordById(String id, String loginName, String newPassword) {
		userDao.updatePasswordById(entryptPassword(newPassword), id);
		systemRealm.clearCachedAuthorizationInfo(loginName);
	}
	
	@Transactional(readOnly = false)
	public void updateUserLoginInfo(String id) {
		userDao.updateLoginInfo(SecurityUtils.getSubject().getSession().getHost(), new Date(), id);
	}
	
	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
	}
	
	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
	}
	
	//-- Role Service --//
	
	public Role getRole(String id) {
		return roleDao.get(id);
	}

	public Role findRoleByName(String name) {
		return roleDao.findByName(name);
	}

	/**
	 * 根据权限查找用户集合
	 * @param permission 权限标识(如：psi:product:review)
	 * @return
	 */
	public List<User> findUserByPermission(String permission) {
		if(StringUtils.isEmpty(permission)){
			return Lists.newArrayList();
		}
		return userDao.findUserByPermission(permission);
	}

	/**
	 * 根据权限查找用户集合(精准匹配,不支持后缀模糊匹配)
	 * @return
	 */
	public List<User> findUserByRealPermission(String permission) {
		if(StringUtils.isEmpty(permission)){
			return Lists.newArrayList();
		}
		return userDao.findUserByRealPermission(permission);
	}
	
	
	
	/**
	 * 根据权限模糊匹配
	 * @param permission 权限标识(如：event:service:de)
	 * @return
	 */
	public Map<String,List<User>> findUserByPermissionName(String name) {
		Map<String,List<User>> map=Maps.newHashMap();
		if(StringUtils.isEmpty(name)){
			return map;
		}
		 DetachedCriteria dc = menuDao.createDetachedCriteria();
		 dc.add(Restrictions.like("permission", name+"%"));
		 dc.add(Restrictions.eq("delFlag","0"));
		 List<Menu> list=menuDao.find(dc);
		 for (Menu menu : list) {
			 String coutry =menu.getPermission().split(":")[2];
			 Map<String,User> userMap = Maps.newHashMap();
			 for(Role role :menu.getRoleList()){
				 if(role.getUserList()!=null){
					 for(User user: role.getUserList()){
						 userMap.put(user.getId(), user);
					 };
				 }
			 }
			 if(userMap.size()>0){
				 List<User> users = Lists.newArrayList();
				 for(Map.Entry<String,User> entry : userMap.entrySet()){
					 users.add(entry.getValue());
				 }
				 map.put(coutry, users);
			 }
		 }
		 return map;
	}
	
	
	public Set<String> findUserIdByPermissionName() {
		 Set<String> idList=Sets.newHashSet();
		/* DetachedCriteria dc = menuDao.createDetachedCriteria();
		 dc.add(Restrictions.or(Restrictions.like("permission","custom:service:%"),Restrictions.like("permission","event:service:%")));
		 dc.add(Restrictions.eq("delFlag","0"));
		 List<Menu> list=menuDao.find(dc);
		 for (Menu menu : list) {
			 if(!menu.getPermission().contains("custom:service:other")){
				 for(Role role :menu.getRoleList()){
					 if(role.getUserList()!=null){
						 for(User user: role.getUserList()){
							 idList.add(user.getId());
						 }
					 }
				 }
			 }
		 }*/
		 String sql="SELECT DISTINCT e.`user_id` FROM sys_menu s "+
			     " JOIN sys_role_menu m ON m.`menu_id`=s.`id`  "+
				 " JOIN sys_user_role e ON m.`role_id`=e.`role_id`  "+
				 " JOIN sys_user u ON u.id=e.`user_id`  "+
				 " WHERE s.`del_flag`='0' AND u.del_flag='0' AND ((s.`permission`!='custom:service:other.' AND s.`permission` LIKE 'custom:service:%') OR s.`permission` LIKE 'event:service:%') ";
		 List<String> list=menuDao.findBySql(sql);
		 idList.addAll(list);
		 return idList;
	}
	
	public List<User> findUsersByRoleName(String name) {
		Role role =  roleDao.findByName(name);
		if(role!=null){
			List<User> users = role.getUserList();
			Hibernate.initialize(users);
			return users;
		}
		return null;
	}
	
	
	public Map<String,List<User>> findRoleByMatchName(String name){
		 Map<String,List<User>> map=Maps.newHashMap();
		 DetachedCriteria dc = roleDao.createDetachedCriteria();
		 dc.add(Restrictions.like("name", "%"+name));
		 dc.add(Restrictions.eq("delFlag","0"));
		 List<Role> list=roleDao.find(dc);
		 for (Role role : list) {
			map.put(role.getRemarks().split(":")[1], role.getUserList());
		 }
		 return map;
	}
	
	public Set<User> findUserListByMatchName(String name){
		 Set<User> userSet=Sets.newHashSet();
		 DetachedCriteria dc = roleDao.createDetachedCriteria();
		 dc.add(Restrictions.like("name", "%"+name));
		 dc.add(Restrictions.eq("delFlag","0"));
		 List<Role> list=roleDao.find(dc);
		 for (Role role : list) {
			 userSet.addAll(role.getUserList());
		 }
		 return userSet;
	}
	
	public Map<String,List<User>> findRoleByMatchRemarks(String name){
		 Map<String,List<User>> map=Maps.newHashMap();
	     DetachedCriteria dc = menuDao.createDetachedCriteria();
		 dc.add(Restrictions.like("permission", name+"%"));
		 dc.add(Restrictions.eq("delFlag","0"));
		 List<Menu> list=menuDao.find(dc);
		 for (Menu menu : list) {
			 String coutry =menu.getPermission().split(":")[2];
			 Map<String,User> userMap = Maps.newHashMap();
			 for(Role role :menu.getRoleList()){
				 if(role.getUserList()!=null){
					 for(User user: role.getUserList()){
						 userMap.put(user.getId(), user);
					 };
				 }
			 }
			 if(userMap.size()>0){
				 List<User> users = Lists.newArrayList();
				 for(Map.Entry<String,User> entry : userMap.entrySet()){
					 users.add(entry.getValue());
				 }
				 map.put(coutry, users);
			 }
		 }
		 return map;
	}
	
	//替换成findUserByMenuName
	public List<String> findUserByRoleName(String name) {
		String sql="SELECT DISTINCT email FROM sys_user u JOIN sys_user_role r ON r.`user_id`=u.`id` JOIN sys_role s ON r.`role_id`=s.`id` WHERE s.`name`=:p1 and email is not null AND u.`del_flag`='0' ";
		return roleDao.findBySql(sql,new Parameter(name));
	}
	
	public List<String> findUserByMenuName(String name) {
		String sql="SELECT DISTINCT email FROM sys_user u JOIN sys_user_role r ON r.`user_id`=u.`id` "+
				" JOIN sys_role_menu m ON r.`role_id`=m.`role_id` "+
				" JOIN sys_menu n ON m.`menu_id`=n.`id` AND n.`del_flag`='0' "+
				" WHERE n.permission=:p1 AND email IS NOT NULL AND u.`del_flag`='0' ";
		return roleDao.findBySql(sql,new Parameter(name));
	}
	
	public List<String> findUserNameByRoleName(String name) {
		String sql="SELECT DISTINCT u.name FROM sys_user u JOIN sys_user_role r ON r.`user_id`=u.`id` JOIN sys_role s ON r.`role_id`=s.`id` WHERE s.`name` like :p1 and email is not null AND u.`del_flag`='0' ";
		return roleDao.findBySql(sql,new Parameter("%"+name));
	}
	
	public List<String> findUserNameByMenuName(Set<String> roleSet) {
		String sql="SELECT DISTINCT u.name FROM sys_user u JOIN sys_user_role r ON r.`user_id`=u.`id` "+
				" JOIN sys_role_menu m ON r.`role_id`=m.`role_id` "+
				" JOIN sys_menu n ON m.`menu_id`=n.`id` AND n.`del_flag`='0' "+
				" WHERE n.permission in :p1 AND email IS NOT NULL AND u.`del_flag`='0' ";
		return roleDao.findBySql(sql,new Parameter(roleSet));
	}
	
	
	
	//Map<email, loginName>
	public Map<String, String> findLoginNameByEmail() {
		Map<String, String> rs = Maps.newHashMap();
		String sql="SELECT t.`email`,t.`login_name` FROM `sys_user` t WHERE t.`del_flag`='0' AND t.`id` != '1' ";
		List<Object[]> list = userDao.findBySql(sql);
		for (Object[] objs : list) {
			String email = objs[0].toString();
			String loginName = objs[1].toString();
			rs.put(email, loginName);
		}
		return rs;
	}
	
	public List<Role> findAllRole(){
		return UserUtils.getRoleList();
	}
	
	@Transactional(readOnly = false)
	public void saveRole(Role role) {
		roleDao.clear();
		roleDao.save(role);
		systemRealm.clearAllCachedAuthorizationInfo();
		// 同步到Activiti
		saveActiviti(role);
		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
	}

	@Transactional(readOnly = false)
	public void deleteRole(String id) {
		roleDao.deleteById(id);
		systemRealm.clearAllCachedAuthorizationInfo();
		// 同步到Activiti
		deleteActiviti(roleDao.get(id));
		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
	}
	
	@Transactional(readOnly = false)
	public Boolean outUserInRole(Role role, String userId) {
		User user = userDao.get(userId);
		List<String> roleIds = user.getRoleIdList();
		Set<Role> roles = user.getRoleList();
		// 
		if (roleIds.contains(role.getId())) {
			roles.remove(role);
			saveUser(user);
			role.getUserList().remove(user);
			saveRole(role);
			return true;
		}
		return false;
	}
	
	@Transactional(readOnly = false)
	public User assignUserToRole(Role role, String userId) {
		User user = userDao.get(userId);
		List<String> roleIds = user.getRoleIdList();
		if (roleIds.contains(role.getId())) {
			return null;
		}
		user.getRoleList().add(role);
		saveUser(user);
		role.getUserList().add(user);
		saveRole(role);
		return user;
	}

	//-- Menu Service --//
	
	public Menu getMenu(String id) {
		return menuDao.get(id);
	}

	public List<Menu> findAllMenu(boolean all){
		if (all) {	//获取整个ERP中的菜单，忽略服务器编号限制
			return menuDao.findAllMenu();
		} else {	//获取当前服务中用户的菜单(会被服务器编号限制)
			return UserUtils.getMenuList();
		}
	}
	
	@Transactional(readOnly = false)
	public void saveMenu(Menu menu) {
		menu.setParent(this.getMenu(menu.getParent().getId()));
		String oldParentIds = menu.getParentIds(); // 获取修改前的parentIds，用于更新子节点的parentIds
		menu.setParentIds(menu.getParent().getParentIds()+menu.getParent().getId()+",");
		menuDao.clear();
		menuDao.save(menu);
		// 更新子节点 parentIds
		List<Menu> list = menuDao.findByParentIdsLike("%,"+menu.getId()+",%");
		for (Menu e : list){
			e.setParentIds(e.getParentIds().replace(oldParentIds, menu.getParentIds()));
		}
		menuDao.save(list);
		systemRealm.clearAllCachedAuthorizationInfo();
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
		saveActiviti(menu);
	}

	@Transactional(readOnly = false)
	public void deleteMenu(String id) {
		menuDao.deleteById(id, "%,"+id+",%");
		systemRealm.clearAllCachedAuthorizationInfo();
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
		deleteActiviti(id);
	}

	///////////////// Synchronized to the Activiti //////////////////

	/**
	 * 手工同步所有Activiti数据
	 */
	@Transactional(readOnly = false)
	public void synToActiviti()  {
		menuDao.updateBySql("delete from ACT_ID_MEMBERSHIP",null);
		menuDao.updateBySql("delete from ACT_ID_GROUP", null);
		menuDao.updateBySql("delete from ACT_ID_USER", null);
		
		List<Group> activitiGroupList = identityService.createGroupQuery().list();
		List<org.activiti.engine.identity.User> activitiUserList = identityService.createUserQuery().list();
		if (activitiGroupList.size() == 0 &&activitiUserList.size() == 0){
		 	//同步时候添加所有用户，所有组，以及关联关系，之后增删改用户，增删改角色时不需要判断用户，组是否存在。
		 	List<User> userList = userDao.findAllList();
		 	for(User user:userList){
		 		org.activiti.engine.identity.User activitiUesr = identityService.newUser(ObjectUtils.toString(user.getId()));
		 		identityService.saveUser(activitiUesr);
		 	}
		 	for(Menu menu:menuDao.findAllActivitiList()){
		 		if (StringUtils.isNotEmpty(menu.getActivitiGroupId())){
			 		Group group = identityService.newGroup(menu.getActivitiGroupId());
			 		identityService.saveGroup(group);
		 		}
		 	}
		 	//创建关联关系
		 	for(User user:userList) {
		 		List<Menu> menuList = menuDao.findAllActivitiList(user.getId());
		 		if(!Collections3.isEmpty(menuList)){
		 			for(Menu menu:menuList) {
		 				if (StringUtils.isNotEmpty(menu.getActivitiGroupId())){
		 					identityService.createMembership(ObjectUtils.toString(user.getId()), menu.getActivitiGroupId());
		 				}
		 			}
		 		}
		 	}
		}
	}
	
	private void saveActiviti(Role role) {
		if(role!=null) {
			List<User> userList = roleDao.get(role.getId()).getUserList();
			if(!Collections3.isEmpty(userList)) {
			 	for(User user:userList) {
			 		String userId = ObjectUtils.toString(user.getId());
					org.activiti.engine.identity.User activitiUser = identityService.createUserQuery().userId(userId).singleResult();
					// 是新增用户
					if (activitiUser == null) {
						activitiUser = identityService.newUser(userId);
						identityService.saveUser(activitiUser);
					} 
					// 同步用户角色关联数据
			 		List<Menu> menuList = menuDao.findAllActivitiList(user.getId());
			 		merge(user, menuList);
			 	}
			}
		}
	}
	

	private void deleteActiviti(Role role) {
		if(role!=null) {
			List<User> userList = roleDao.get(role.getId()).getUserList();
			if(!Collections3.isEmpty(userList)) {
			 	for(User user:userList) {
			 		List<Menu> menuList = menuDao.findAllActivitiList(user.getId());
			 		merge(user, menuList);
			 	}
			}
		}
	}

	private void saveActiviti(User user) {
		if(user!=null) {
			String userId = ObjectUtils.toString(user.getId());
			org.activiti.engine.identity.User activitiUser = identityService.createUserQuery().userId(userId).singleResult();
			// 是新增用户
			if (activitiUser == null) {
				activitiUser = identityService.newUser(userId);
				identityService.saveUser(activitiUser);
			} 
			// 同步用户角色关联数据
	 		List<Menu> menuList = menuDao.findAllActivitiList(user.getId());
	 		merge(user, menuList);
		}
	}

	private void deleteActiviti(User user) {
		if(user!=null) {
			String userId = ObjectUtils.toString(user.getId());
			identityService.deleteUser(userId);
		}
	}

	private void saveActiviti(Menu menu) {
		if(menu!=null){
			Group group = identityService.createGroupQuery().groupId(menu.getActivitiGroupId()).singleResult();
			if(group!=null) {
				identityService.deleteGroup(group.getId());
			}
			if(Menu.YES.equals(menu.getIsActiviti()) && StringUtils.isNotBlank(menu.getActivitiGroupId())){
				group = identityService.newGroup(menu.getActivitiGroupId());
				group.setName(menu.getActivitiGroupName());
				identityService.saveGroup(group);
				List<Role> roleList = menuDao.get(menu.getId()).getRoleList();
				if(!Collections3.isEmpty(roleList)) {
					for(Role role:roleList) {
						List<User> userList = role.getUserList();
						if(!Collections3.isEmpty(userList)) {
							for(User user:userList) {
								identityService.createMembership(ObjectUtils.toString(user.getId()), menu.getActivitiGroupId());
							}
						}
					}
				}
			}
		}
	}
	private void deleteActiviti(String id) {
		if(id!=null) {
			Menu menu = menuDao.get(id);
			if(menu != null && Menu.YES.equals(menu.getIsActiviti()) && StringUtils.isNotBlank(menu.getActivitiGroupId())){
				identityService.deleteGroup(menu.getActivitiGroupId());
			}
			if(menu!=null) {
				List<Menu> menuList = menuDao.findByParentIdsLike("%,"+menu.getId()+",%");
				for(Menu m:menuList) {
					if(Menu.YES.equals(menu.getIsActiviti()) && StringUtils.isNotBlank(m.getActivitiGroupId())){
						identityService.deleteGroup(m.getActivitiGroupId());
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void merge(User user,List<Menu> menuList) {
		String userId = ObjectUtils.toString(user.getId());
		List<Group> activitiGroupList = identityService.createGroupQuery().groupMember(userId).list();
		if(Collections3.isEmpty(menuList)) {
			for(Group group:activitiGroupList) {
				identityService.deleteMembership(userId, group.getId());
			}
		} else {
			Map<String,String> groupMap =Maps.newHashMap();
			for(Menu menu:menuList) {
				groupMap.put(menu.getActivitiGroupId(), menu.getActivitiGroupName());
			}
			Map<String,String> activitiGroupMap = Collections3.extractToMap(activitiGroupList, "id", "name");
			for(String groupId:activitiGroupMap.keySet()) {
				if(StringUtils.isNotBlank(groupId) && !groupMap.containsKey(groupId)) {
					identityService.deleteMembership(userId, groupId);
				}
			}
			for(String groupId:groupMap.keySet()) {
				if(StringUtils.isNotBlank(groupId) && !activitiGroupMap.containsKey(groupId)) {
					identityService.createMembership(userId, groupId);
				}
			}
		}
	}
	
	
	public Map<String,Map<String,Set<String>>> getEmailMap(List<String> countryList,Map<String,String> roleNameMap) {
		Map<String,Map<String,Set<String>>> map=Maps.newHashMap();
		Map<String,Map<String,String>> typeMap=Maps.newHashMap();//类型-邮箱-国家字符串
		
		for (Entry<String, String> entry : roleNameMap.entrySet()) {
				String role = entry.getKey();
				if("amazoninfo:email:all".equals(role)){
					List<String> emaliList=this.findUserByMenuName(role);
					if(emaliList!=null&&emaliList.size()>0){
						for (String email : emaliList) {
							Map<String,String> emailAndCountry=typeMap.get(entry.getValue());
							if(emailAndCountry==null){
								emailAndCountry=Maps.newHashMap();
								typeMap.put(entry.getValue(), emailAndCountry);
							}
							String countryStr=emailAndCountry.get(email);
							if(StringUtils.isBlank(countryStr)){
								emailAndCountry.put(email,StringUtils.join(countryList.toArray(),",")+",");
							}else{
								emailAndCountry.put(email,countryStr+StringUtils.join(countryList.toArray(),",")+",");
							}
						}
					}
				}else if("amazoninfo:email:en".equals(role)){
					List<String> userListAllRight=this.findUserByMenuName(role);
					if(userListAllRight!=null&&userListAllRight.size()>0){
						for (String email : userListAllRight) {
							Map<String,String> emailAndCountry=typeMap.get(roleNameMap.get(role));
							if(emailAndCountry==null){
								emailAndCountry=Maps.newHashMap();
								typeMap.put(roleNameMap.get(role), emailAndCountry);
							}
							String countryStr=emailAndCountry.get(email);
							if(StringUtils.isBlank(countryStr)){
								emailAndCountry.put(email,"uk,com,ca,");
							}else{
								emailAndCountry.put(email,countryStr+"uk,com,ca");
							}
						}
					}
				}else if("amazoninfo:email:nonEn".equals(role)){
					List<String> userListAllRight=this.findUserByMenuName(role);
					if(userListAllRight!=null&&userListAllRight.size()>0){
						for (String email : userListAllRight) {
							Map<String,String> emailAndCountry=typeMap.get(roleNameMap.get(role));
							if(emailAndCountry==null){
								emailAndCountry=Maps.newHashMap();
								typeMap.put(roleNameMap.get(role), emailAndCountry);
							}
							String countryStr=emailAndCountry.get(email);
							if(StringUtils.isBlank(countryStr)){
								emailAndCountry.put(email,"de,jp,fr,it,es,mx,");
							}else{
								emailAndCountry.put(email,countryStr+"de,jp,fr,it,es,mx,");
							}
						}
					}
				}else{
					for (String country : countryList) {
						List<String> emaliList=this.findUserByMenuName(role+country);
						if(emaliList!=null){
							for (String email: emaliList) {
								Map<String,String> emailAndCountry=typeMap.get(roleNameMap.get(role));
								if(emailAndCountry==null){
									emailAndCountry=Maps.newHashMap();
									typeMap.put(roleNameMap.get(role), emailAndCountry);
								}
								String countryStr=emailAndCountry.get(email);
								if(StringUtils.isBlank(countryStr)){
									emailAndCountry.put(email,country+",");
								}else{
									emailAndCountry.put(email,countryStr+country+",");
								}
							}
						}
					}
				}
		
		}
		
		try{
			//排除重复的email
			Map<String,String> email1=typeMap.get("0");//国家一起
			Map<String,String> email2=typeMap.get("1");
			if(email1!=null&&email1.size()>0){
				Iterator<Map.Entry<String,String>> it= email2.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry<String,String> cmpMap=it.next();
					for (String type1: email1.keySet()) {
						if(cmpMap.getKey().equals(type1)){
							it.remove();
						}
					}
				}
			}
			/*
			  List<String> removeType=Lists.newArrayList();
			  if(email1!=null&&email1.size()>0){
				for (String type1: email1.keySet()) {
					for (String type2: email2.keySet()) {
						 if(type2.equals(type1)){
							 String countryStr2=email2.get(type2);
							// email2.remove(type2);
							 removeType.add(type2);
							 String countryStr1=email1.get(type1);
							 email1.put(type1, countryStr1+countryStr2);
						 }
					}
				}
				
			}
			if(removeType!=null&&removeType.size()>0){
				email2.remove(removeType);
			}*/
			
		}catch(Exception e){
			LOGGER.error("邮件去重异常！",e);
		}
		
		
		for (String type : typeMap.keySet()) {
			Map<String,Set<String>> composeMap=map.get(type);
			if(composeMap==null){
				composeMap=Maps.newHashMap();
				map.put(type, composeMap);
			}
			Map<String,String> emailAndCountry=typeMap.get(type);
			for (String key:emailAndCountry.keySet()) {
				Set<String> set=composeMap.get(emailAndCountry.get(key));
				if(set==null){
	        		set=Sets.newHashSet();
	        		composeMap.put(emailAndCountry.get(key),set);
	        	}
	        	set.add(key);
			}
		}
		return map;
	}
	
	public Map<String,Set<String>> getEmailMap(List<String> countryList,List<String> roleNameList) {
		Map<String,String> userAndCountry=Maps.newHashMap();
		for (String role : roleNameList) {
			if("amazoninfo:email:all".equals(role)){
				List<String> emaliList=this.findUserByMenuName(role);
				if(emaliList!=null&&emaliList.size()>0){
					for (String email : emaliList) {
						String countryStr=userAndCountry.get(email);
						if(StringUtils.isBlank(countryStr)){
							userAndCountry.put(email,StringUtils.join(countryList.toArray(),",")+",");
						}else{
							userAndCountry.put(email,countryStr+StringUtils.join(countryList.toArray(),",")+",");
						}
					}
				}
			}else if("amazoninfo:postsEmail:all".equals(role)){
				List<String> emaliList=this.findUserByMenuName(role);
				if(emaliList!=null&&emaliList.size()>0){
					for (String email : emaliList) {
						String countryStr=userAndCountry.get(email);
						if(StringUtils.isBlank(countryStr)){
							userAndCountry.put(email,StringUtils.join(countryList.toArray(),",")+",");
						}else{
							userAndCountry.put(email,countryStr+StringUtils.join(countryList.toArray(),",")+",");
						}
					}
				}
			}else if("amazoninfo:email:en".equals(role)){
				List<String> userListAllRight=this.findUserByMenuName(role);
				if(userListAllRight!=null&&userListAllRight.size()>0){
					for (String email : userListAllRight) {
						String countryStr=userAndCountry.get(email);
						if(StringUtils.isBlank(countryStr)){
							userAndCountry.put(email,"uk,com,ca,");
						}else{
							userAndCountry.put(email,countryStr+"uk,com,ca,");
						}
					}
				}
			}else if("amazoninfo:email:nonEn".equals(role)){
				List<String> userListAllRight=this.findUserByMenuName(role);
				if(userListAllRight!=null&&userListAllRight.size()>0){
					for (String email : userListAllRight) {
						String countryStr=userAndCountry.get(email);
						if(StringUtils.isBlank(countryStr)){
							userAndCountry.put(email,"de,jp,fr,it,es,mx,");
						}else{
							userAndCountry.put(email,countryStr+"de,jp,fr,it,es,mx,");
						}
					}
				}
			}else{
				for (String country : countryList) {
					List<String> emaliList=this.findUserByMenuName(role+country);
					if(emaliList!=null){
						for (String email: emaliList) {
							String countryStr=userAndCountry.get(email);
							if(StringUtils.isBlank(countryStr)){
								userAndCountry.put(email,country+",");
							}else{
								userAndCountry.put(email,countryStr+country+",");
							}
						}
					}
				}
			}
			
		}
		
		Map<String,Set<String>> composeMap=Maps.newHashMap();
        for (Entry<String, String> entry: userAndCountry.entrySet()) {
        	String key = entry.getKey();
        	Set<String> set=composeMap.get(userAndCountry.get(key));
        	if(set==null){
        		set=Sets.newHashSet();
        		composeMap.put(userAndCountry.get(key),set);
        	}
        	set.add(key);
        }
		return composeMap;
	}

//	public boolean hasRole(String userId,String role){
//		String sql="SELECT 1 FROM sys_user_role r JOIN sys_role s ON r.`role_id`=s.id  WHERE r.`user_id`=:p1 AND s.`remarks` LIKE :p2";
//		List<String> list=roleDao.findBySql(sql,new Parameter(userId,"%"+role+"%"));
//		if(list!=null&&list.size()>0){
//			return true;
//		}
//		return false;
//	}
	
	public boolean hasPerssion(String userId,String perssion){
		String sql="SELECT 1 FROM sys_role AS a ,sys_menu AS b,sys_role_menu AS c,sys_user AS d,sys_user_role AS e" +
				"   WHERE  d.`id`=e.`user_id` AND a.`id`=e.`role_id` AND d.`del_flag`='0'  AND " +
				"  a.id=c.`role_id` AND b.`id`=c.`menu_id` AND a.`del_flag`='0' AND b.`del_flag`='0' " +
				" AND d.`id`=:p1 AND b.`permission` LIKE :p2";
		List<String> list=roleDao.findBySql(sql,new Parameter(userId,perssion+"%"));
		if(list!=null&&list.size()>0){
			return true;
		}
		return false;
	}
	
	public List<User> hasPerssion(String perssion){
		List<User> users = Lists.newArrayList();
		String sql="SELECT d.id FROM sys_role AS a ,sys_menu AS b,sys_role_menu AS c,sys_user AS d,sys_user_role AS e" +
				"   WHERE  d.`id`=e.`user_id` AND a.`id`=e.`role_id` AND d.`del_flag`='0'  AND " +
				"  a.id=c.`role_id` AND b.`id`=c.`menu_id` AND a.`del_flag`='0' AND b.`del_flag`='0' " +
				"  AND b.`permission` LIKE :p1";
		List<String> userIds=roleDao.findBySql(sql,new Parameter(perssion+"%"));
		if(userIds!=null&&userIds.size()>0){
			for(String userId:userIds){
				users.add(UserUtils.getUserById(userId));
			}
		}
		return users;
	}
	
	
	@Transactional(readOnly = false)
	public void updateRoleDate(String userId,String role){
		String sql="UPDATE sys_user_role r SET update_date=NOW(),update_user=:p1 WHERE user_id=:p2 AND role_id=:p3 ";
		roleDao.updateBySql(sql, new Parameter(UserUtils.getUser().getId(),userId,role));
	}
	
	public Map<String,List<String>> findSecondRole(){
		Map<String,List<String>>  map=Maps.newHashMap();
		String sql="SELECT r.`update_user`,u.name,GROUP_CONCAT(e.name) roleName FROM sys_user_role r "+
					" JOIN sys_user u ON r.`user_id`=u.id AND u.`del_flag`='0' "+
					" JOIN sys_role e ON r.`role_id`=e.id AND e.`del_flag`='0' "+
					" WHERE r.`update_date`<=:p1 GROUP BY r.`update_user`,u.name ";
		List<Object[]> list=roleDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-30)));
		for (Object[] obj: list) {
			String updateUser=obj[0].toString();
			String value=obj[1].toString()+"-->"+obj[2].toString();
			List<String> temp=map.get(updateUser);
			if(temp==null){
				temp=Lists.newArrayList();
				map.put(updateUser, temp);
			}
			temp.add(value);
		}
		return map;
	}
	
	public List<Role> findRoleByIds(List<String> roleIds){
		DetachedCriteria dc = roleDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", roleIds));
		dc.add(Restrictions.eq("delFlag", "0"));
		return roleDao.find(dc);
	}
	
	/**
	 * 
	 * @param officeNames 部门名称集合
	 * @param type	角色类型  0：普通角色  1：岗位 2：特殊角色
	 * @return
	 */
	public List<Role> findRoleByOfficeName(List<String> officeNames, String type, boolean isAdmin){
		DetachedCriteria dc = roleDao.createDetachedCriteria();
		dc.createAlias("office", "office");
		if (!isAdmin) {
			dc.add(Restrictions.in("office.name", officeNames));
		}
		dc.add(Restrictions.eq("office.delFlag", "0"));
		dc.add(Restrictions.eq("delFlag", "0"));
		if (StringUtils.isNotEmpty(type) && !isAdmin) {
			dc.add(Restrictions.eq("type", type));
		}
		dc.addOrder(Order.desc("office"));
		return roleDao.find(dc);
	}
	
	/**
	 * 
	 * @param officeNames 部门名称集合
	 * @return
	 */
	public List<User> findUserByOfficeName(List<String> officeNames, boolean isAdmin){
		DetachedCriteria dc = userDao.createDetachedCriteria();
		dc.createAlias("office", "office");
		if (!isAdmin) {	//管理员查看全部
			dc.add(Restrictions.in("office.name", officeNames));
		}
		dc.add(Restrictions.eq("office.delFlag", "0"));
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("office"));
		return userDao.find(dc);
	}
	
	/**
	 * 根据用户查询菜单权限集合
	 * @param userId 用户ID
	 * @return
	 */
	public List<Menu> findMenuByUserId(String userId, boolean all){
		if (all) {
			return menuDao.findAllList();
		}
		if (StringUtils.isEmpty(userId)) {
			return Lists.newArrayList();
		}
		return menuDao.findUserAllPermissions(userId);
	}
	
	/**
	 * 根据用户查询菜单权限集合
	 * @param userId 用户ID
	 * @return
	 */
	public List<Object[]> findSecondmentByUserId(String userId){
		String sql = "SELECT t.`role_id`,t.`update_date`,t.`update_user` FROM `sys_user_role` t WHERE t.`user_id`=:p1 " +
				" AND t.`update_date` IS NOT NULL AND t.`update_user` IS NOT NULL";
		return roleDao.findBySql(sql, new Parameter(userId));
	}

	@Transactional(readOnly = false)
	public void updateSecondment(List<Object[]> secondment, String userId) {
		String sql = "update `sys_user_role` set update_date=:p1,update_user=:p2 where user_id=:p3 and role_id=:p4";
		for (Object[] obj : secondment) {
			roleDao.updateBySql(sql, new Parameter(obj[1], obj[2], userId, obj[0]));
		}
	}
	
	/**
	 * 根据erp账户查找对应的亚马逊账号平台google认证授权码
	 * @return
	 */
	public Map<String, String> getAmazonUserByErpInfo(String roleNames){
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT t.`country`,t.`auth_code`,t.`role_name` FROM `amazon_user` t ORDER BY t.`sort` ";
		List<Object[]> list = userDao.findBySql(sql, null);
		for (Object[] obj : list) {
			if (obj[1] == null || StringUtils.isEmpty(obj[1].toString())) {
				continue;	//没有授权码
			}
			String roles = obj[2].toString();
			for (String roleName : roles.split(",")) {
				if (roleNames.contains(roleName)) {
					String country = obj[0].toString();
					country = country.split(",")[0];
					String name = "";
					if (country.contains("ams.")) {
						name = "ams";
					}
					if (country.contains("vendor.")) {
						name = "vendor";
					}
					country = country.replace("ams.", "").replace("vendor.", "");
					String authCode = obj[1].toString();
					if (rs.get(name+getCountryStr(country)) == null) {
						rs.put(name+getCountryStr(country), authCode);
					}
				}
			}
		}
		return rs;
	}

	public static String getCountryStr(String country) {
		if ("eu".equals(country)) {
			return "欧洲EU";
		} else if("com,ca".equals(country)||"com,ca,mx".equals(country)){
			return "美洲US";
		} else {
			return countryNameMap.get(country);	
		}
	}
}
