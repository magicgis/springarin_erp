/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.springrain.erp.modules.sys.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.Menu;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.OfficeService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 角色Controller
 * @author ThinkGem
 * @version 2013-5-15 update 2013-06-08
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/role")
public class RoleController extends BaseController {

	@Autowired
	private SystemService systemService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private PsiProductGroupUserService  groupUserService;
	
	@ModelAttribute("role")
	public Role get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return systemService.getRole(id);
		}else{
			return new Role();
		}
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(Role role, Model model) {
		List<Role> list = Lists.newArrayList(systemService.findAllRole());
		model.addAttribute("offices", officeService.findAll());
		if(role.getOffice()!=null&&StringUtils.isNotBlank(role.getOffice().getId())){
			for (Iterator<Role> iterator = list.iterator(); iterator.hasNext();) {
				Role role2 = (Role) iterator.next();
				if(!role.getOffice().getId().equals(role2.getOffice().getId())){
					iterator.remove();
				}
			}
		}
		model.addAttribute("list", list);
		return "modules/sys/roleList";
	}

	@RequestMapping(value = "form")
	public String form(Role role, Model model) {
		if (role.getOffice()==null){
			role.setOffice(UserUtils.getUser().getOffice());
		}
		model.addAttribute("role", role);
		model.addAttribute("menuList", systemService.findAllMenu(true));
//		model.addAttribute("categoryList", categoryService.findByUser(false, null));
		model.addAttribute("officeList", officeService.findAll());
		return "modules/sys/roleForm";
	}
	
	@RequestMapping(value = "save")
	public String save(Role role, Model model, String oldName, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/sys/role/?repage";
		}
		if (!beanValidator(model, role)){
			return form(role, model);
		}
		if (!"true".equals(checkName(oldName, role.getName()))){
			addMessage(model, "保存角色'" + role.getName() + "'失败, 角色名已存在");
			return form(role, model);
		}
		systemService.saveRole(role);
		addMessage(redirectAttributes, "保存角色'" + role.getName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/sys/role/?repage";
	}
	
	@RequestMapping(value = "delete")
	public String delete(@RequestParam String id, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/sys/role/?repage";
		}
		if (Role.isAdmin(id)){
			addMessage(redirectAttributes, "删除角色失败, 不允许内置角色或编号空");
//		}else if (UserUtils.getUser().getRoleIdList().contains(id)){
//			addMessage(redirectAttributes, "删除角色失败, 不能删除当前用户所在角色");
		}else{
			systemService.deleteRole(id);
			addMessage(redirectAttributes, "删除角色成功");
		}
		return "redirect:"+Global.getAdminPath()+"/sys/role/?repage";
	}
	
	@RequestMapping(value = "assign")
	public String assign(Role role, Model model) {
		List<User> users = role.getUserList();
		model.addAttribute("users", users);
		return "modules/sys/roleAssign";
	}
	
	@RequestMapping(value = "usertorole")
	public String selectUserToRole(Role role, Model model) {
		model.addAttribute("role", role);
		model.addAttribute("selectIds", role.getUserIds());
		model.addAttribute("officeList", officeService.findAll());
		return "modules/sys/selectUserToRole";
	}
	
	@ResponseBody
	@RequestMapping(value = "users")
	public List<Map<String, Object>> users(String officeId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		Office office = officeService.get(officeId);
		List<User> userList = office.getUserList();
		for (User user : userList) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", user.getId());
			map.put("pId", 0);
			map.put("name", user.getName());
			mapList.add(map);			
		}
		return mapList;
	}
	
	@RequestMapping(value = "outrole")
	public String outrole(String userId, String roleId, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/sys/role/assign?id="+roleId;
		}
		Role role = systemService.getRole(roleId);
		User user = systemService.getUser(userId);
		if (user.equals(UserUtils.getUser())) {
			addMessage(redirectAttributes, "无法从角色【" + role.getName() + "】中移除用户【" + user.getName() + "】自己！");
		}else {
			Boolean flag = systemService.outUserInRole(role, userId);
			//删除产品线与用户关系
			List<Menu> menuList=role.getMenuList();
			List<Dict> dicts = DictUtils.getDictList("platform");
			boolean delFlag=false;
			
			if(menuList!=null&&menuList.size()>0){
				for (Menu menu : menuList) {
					String permission=menu.getPermission();
					if(StringUtils.isNotBlank(permission)&&permission.contains("amazoninfo:feedSubmission:")){
						for (Dict dict : dicts) {
							if(permission.equals("amazoninfo:feedSubmission:"+dict.getValue())){
								delFlag=true;
								break;
							}
						}
					}
				}
			}
			if(delFlag){
				this.groupUserService.deleteProductGroupUser(UserUtils.getUserById(userId),role);
			}
			if (!flag) {
				addMessage(redirectAttributes, "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！");
			}else {
				addMessage(redirectAttributes, "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除成功！");
			}			
		}
		return "redirect:"+Global.getAdminPath()+"/sys/role/assign?id="+role.getId();
	}
	
	@RequestMapping(value = "assignrole")
	public String assignRole(Role role, String[] idsArr, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/sys/role/assign?id="+role.getId();
		}
		StringBuilder msg = new StringBuilder();
		int newNum = 0;
		for (int i = 0; i < idsArr.length; i++) {
			User user = systemService.assignUserToRole(role, idsArr[i]);
			if (null != user) {
				msg.append("<br/>新增用户【" + user.getName() + "】到角色【" + role.getName() + "】！");
				newNum++;
			}
		}
		addMessage(redirectAttributes, "已成功分配 "+newNum+" 个用户"+msg);
		return "redirect:"+Global.getAdminPath()+"/sys/role/assign?id="+role.getId();
	}

	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "checkName")
	public String checkName(String oldName, String name) {
		if (name!=null && name.equals(oldName)) {
			return "true";
		} else if (name!=null && systemService.findRoleByName(name) == null) {
			return "true";
		}
		return "false";
	}
	
	
	@RequestMapping(value = "positionList")
	public String positionList(Role role, Model model) {
		User user = UserUtils.getUser();
		Set<Role> roles = user.getRoleList();
		List<String> officeNameList = Lists.newArrayList();
		for (Role role2 : roles) {
			if (role2.getName().contains("主管")) {
				officeNameList.add(role2.getName().split("主管")[0]);
			}
		}
		String type = null;	//0:普通权限 1：岗位  2：特殊权限
		if (officeNameList.size() > 0 || user.isAdmin()) {
			model.addAttribute("roleSet", systemService.findRoleByOfficeName(officeNameList, type, user.isAdmin()));
		}
		return "modules/sys/positionList";
	}
	
	@RequestMapping(value = "findRoleUser")
	public String findRoleUser(HttpServletRequest request, HttpServletResponse response, Model model) {
		/*List<User> page = systemService.findRoleUser(user); 
        model.addAttribute("page", page);*/
        User user = UserUtils.getUser();
		Set<Role> roles = user.getRoleList();
		List<String> officeNameList = Lists.newArrayList();
		for (Role role2 : roles) {
			if (role2.getName().contains("主管")) {
				officeNameList.add(role2.getName().split("主管")[0]);
			}
		}
		if (officeNameList.size() > 0 || user.isAdmin()) {
			model.addAttribute("page", systemService.findUserByOfficeName(officeNameList, user.isAdmin()));
		}
		return "modules/sys/positionUserList";
	}
	
	@RequestMapping(value = "findSecondRoleUser")
	public String findSecondRoleUser(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Object[]> page = systemService.findSecondList(); 
        model.addAttribute("page", page);
		return "modules/sys/positionSecondUserList";
	}

	@RequestMapping(value = "positionAssign")
	public String positionAssign(Role role, Model model,String userId,String type) {
		List<User> users = role.getUserList();
		if(StringUtils.isNotBlank(userId)){
			for (User user : users) {
				if(user.getId().equals(userId)){
					users.remove(user);
					break;
				}
			}
		}
		model.addAttribute("users", users);
		model.addAttribute("type", type);
		return "modules/sys/positionAssign";
	}
	
	@RequestMapping(value = "outPosition")
	public String outPosition(String userId, String roleId,String type,RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/sys/role/positionAssign?id="+roleId;
		}
		Role role = systemService.getRole(roleId);
		User user = systemService.getUser(userId);
		if (user.equals(UserUtils.getUser())) {
			addMessage(redirectAttributes, "无法从岗位【" + role.getName() + "】中移除用户【" + user.getName() + "】自己！");
		}else {
			Boolean flag = systemService.outUserInRole(role, userId);
			//删除产品线与用户关系
			List<Menu> menuList=role.getMenuList();
			boolean delFlag=false;
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Menu menu : menuList) {
				String permission=menu.getPermission();
				if(StringUtils.isNotBlank(permission)&&permission.contains("amazoninfo:feedSubmission:")){
					for (Dict dict : dicts) {
						if(permission.equals("amazoninfo:feedSubmission:"+dict.getValue())){
							delFlag=true;
							break;
						}
					}
				}
			}
			
			if(delFlag){
				this.groupUserService.deleteProductGroupUser(UserUtils.getUserById(userId),role);
			}
			if (!flag) {
				addMessage(redirectAttributes, "用户【" + user.getName() + "】从岗位【" + role.getName() + "】中移除失败！");
			}else {
				addMessage(redirectAttributes, "用户【" + user.getName() + "】从岗位【" + role.getName() + "】中移除成功！");
			}			
		}
		if("3".equals(type)){
			return "redirect:"+Global.getAdminPath()+"/sys/role/findSecondRoleUser";
		}
		systemService.findAllRole();
		return "redirect:"+Global.getAdminPath()+"/sys/role/positionAssign?id="+role.getId()+"&userId="+user.getId()+"&type="+type;
	}
	
	@RequestMapping(value = "positionAssignrole")
	public String positionAssignrole(Role role, String[] idsArr, RedirectAttributes redirectAttributes,String type) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/sys/role/positionAssign?id="+role.getId();
		}
		StringBuilder msg = new StringBuilder();
		int newNum = 0;
		for (int i = 0; i < idsArr.length; i++) {
			User user = systemService.assignUserToRole(role, idsArr[i]);
			if (null != user) {
				if("2".equals(type)){
					systemService.updateRoleDate(idsArr[i],role.getId());
				}
				msg.append("<br/>新增用户【" + user.getName() + "】到岗位【" + role.getName() + "】！");
				newNum++;
			}
		}
		addMessage(redirectAttributes, "已成功分配 "+newNum+" 个用户"+msg);
		return "redirect:"+Global.getAdminPath()+"/sys/role/positionAssign?id="+role.getId()+"&type="+type;
	}
	
	@RequestMapping(value = "positionUserToRole")
	public String positionUserToRole(Role role, Model model,String type) {
		model.addAttribute("role", role);
		model.addAttribute("selectIds", role.getUserIds());
		
		if("1".equals(type)){//分配
			model.addAttribute("officeList",Lists.newArrayList(role.getOffice()));
		}else if("2".equals(type)){//借调
			String roleOffice=role.getOffice().getId();
			List<Office> officeList=officeService.findAll();
			for (Office office : officeList) {
				if(office.getId().equals(roleOffice)){
					officeList.remove(office);
					break;
				}
			}
			model.addAttribute("officeList",officeList);
		}
		return "modules/sys/selectUserToRole";
	}

	@RequestMapping(value = "viewPermissions")
	public String viewPermissions(String id, String type, Model model) {
		if ("1".equals(type)) { //查看角色权限
			Role role = systemService.getRole(id);
			model.addAttribute("role", role);
			//model.addAttribute("menuList", systemService.findAllMenu());
			model.addAttribute("menuList", role.getMenuList());
		} else {	//查看用户权限
			Role role = new Role();
			User user = systemService.getUser(id);
			List<Menu> menuList = systemService.findMenuByUserId(id, false);
			role.setMenuList(menuList);
			role.setOffice(user.getOffice());
			//model.addAttribute("menuList", systemService.findAllMenu(null, true));
			model.addAttribute("menuList", role.getMenuList());
			model.addAttribute("role", role);
			model.addAttribute("user", user);
		}
		model.addAttribute("type", type);
		model.addAttribute("officeList", officeService.findAll());
		return "modules/sys/viewPermissions";
	}
    
}
