package com.springrain.erp.modules.sys.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.beanvalidator.BeanValidators;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MessageUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.utils.excel.ImportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.htmlunit.GoogleAuthenticator;
import com.springrain.erp.modules.oa.service.WorkFlowService;
import com.springrain.erp.modules.plan.service.PlanService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.sys.entity.Menu;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 用户Controller
 * @author ThinkGem
 * @version 2013-5-31
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController {

	@Autowired
	private SystemService systemService;
	@Autowired
	private PsiProductGroupUserService groupUserService;
	@ModelAttribute
	public User get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return systemService.getUser(id);
		}else{
			return new User();
		}
	}
	
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"list", ""})
	public String list(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<User> page = systemService.findUser(new Page<User>(request, response), user); 
        model.addAttribute("page", page);
		return "modules/sys/userList";
	}

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "form")
	public String form(User user, Model model) {
		if (user.getCompany()==null || user.getCompany().getId()==null){
			user.setCompany(UserUtils.getUser().getCompany());
		}
		if (user.getOffice()==null || user.getOffice().getId()==null){
			user.setOffice(UserUtils.getUser().getOffice());
		}
		
		//判断显示的用户是否在授权范围内
		String officeId = user.getOffice().getId();
		User currentUser = UserUtils.getUser();
		if (!currentUser.isAdmin()){
			String dataScope = systemService.getDataScope(currentUser);
			//System.out.println(dataScope);
			if(dataScope.indexOf("office.id=")!=-1){
				String AuthorizedOfficeId = dataScope.substring(dataScope.indexOf("office.id=")+10, dataScope.indexOf(" or"));
				if(!AuthorizedOfficeId.equalsIgnoreCase(officeId)){
					return "error/403";
				}
			}
		}
		
		model.addAttribute("user", user);
		model.addAttribute("allRoles", systemService.findAllRole());
		return "modules/sys/userForm";
	}

	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "save")
	public String save(User user, String oldLoginName, String newPassword, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/sys/user/?repage";
		}
		// 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
		user.setCompany(new Office(request.getParameter("company.id")));
		user.setOffice(new Office(request.getParameter("office.id")));
		// 如果新密码为空，则不更换密码
		if (StringUtils.isNotBlank(newPassword)) {
			user.setPassword(SystemService.entryptPassword(newPassword));
		}
		if (!beanValidator(model, user)){
			return form(user, model);
		}
		if (!"true".equals(checkLoginName(oldLoginName, user.getLoginName()))){
			addMessage(model, "保存用户'" + user.getLoginName() + "'失败，登录名已存在");
			return form(user, model);
		}
		// 角色数据有效性验证，过滤不在授权内的角色
		Set<Role> roleList = Sets.newHashSet();
		List<String> roleIdList = user.getRoleIdList();
		for (Role r : systemService.findAllRole()){
			if (roleIdList.contains(r.getId())){
				roleList.add(r);
			}
		}
		user.setRoleList(roleList);
		List<Object[]> secondment = systemService.findSecondmentByUserId(user.getId());
		// 保存用户信息
		systemService.saveUser(user);
		if (secondment != null && secondment.size() > 0) {
			systemService.updateSecondment(secondment, user.getId());
		}
		// 清除当前用户缓存
		if (user.getLoginName().equals(UserUtils.getUser().getLoginName())){
			UserUtils.getCacheMap().clear();
		}
		addMessage(redirectAttributes, "保存用户'" + user.getLoginName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/sys/user/?repage";
	}
	
	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/sys/user/?repage";
		}
		if (UserUtils.getUser().getId().equals(id)){
			addMessage(redirectAttributes, "删除用户失败, 不允许删除当前用户");
		}else if (User.isAdmin(id)){
			addMessage(redirectAttributes, "删除用户失败, 不允许删除超级管理员用户");
		}else{
			//删除产品线关系
			this.groupUserService.deleteProductGroupUser(UserUtils.getUserById(id),null);
			systemService.deleteUser(id);
			addMessage(redirectAttributes, "删除用户成功");
		}
		return "redirect:"+Global.getAdminPath()+"/sys/user/?repage";
	}
	
	@RequiresPermissions("sys:user:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(User user, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "用户数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
    		Page<User> page = systemService.findUser(new Page<User>(request, response, -1), user); 
    		new ExportExcel("用户数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出用户失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/user/?repage";
    }

	@RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:"+Global.getAdminPath()+"/sys/user/?repage";
		}
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<User> list = ei.getDataList(User.class);
			for (User user : list){
				try{
					if ("true".equals(checkLoginName("", user.getLoginName()))){
						user.setPassword(SystemService.entryptPassword("123456"));
						BeanValidators.validateWithException(validator, user);
						systemService.saveUser(user);
						successNum++;
					}else{
						failureMsg.append("<br/>登录名 "+user.getLoginName()+" 已存在; ");
						failureNum++;
					}
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>登录名 "+user.getLoginName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>登录名 "+user.getLoginName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条用户，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条用户"+failureMsg);
		} catch (IOException e) {
			addMessage(redirectAttributes, "导入用户失败！失败信息："+e.getMessage());
		} catch (InvalidFormatException e) {
			addMessage(redirectAttributes, "导入用户失败！失败信息："+e.getMessage());
		} catch (InstantiationException e) {
			addMessage(redirectAttributes, "导入用户失败！失败信息："+e.getMessage());
		} catch (IllegalAccessException e) {
			addMessage(redirectAttributes, "导入用户失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/user/?repage";
    }
	
	@RequiresPermissions("sys:user:view")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "用户数据导入模板.xlsx";
    		List<User> list = Lists.newArrayList(); list.add(UserUtils.getUser());
    		new ExportExcel("用户数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/user/?repage";
    }

	@ResponseBody
	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "checkLoginName")
	public String checkLoginName(String oldLoginName, String loginName) {
		if (loginName !=null && loginName.equals(oldLoginName)) {
			return "true";
		} else if (loginName !=null && systemService.getUserByLoginName(loginName) == null) {
			return "true";
		}
		return "false";
	}

	@RequiresUser
	@RequestMapping(value = "info")
	public String info(User user, Model model,HttpServletRequest request) {
		User currentUser = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getName())){
			if(Global.isDemoMode()){
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userInfo";
			}
			currentUser = UserUtils.getUser(true);
			currentUser.setEmail(user.getEmail());
			currentUser.setPhone(user.getPhone());
			currentUser.setMobile(user.getMobile());
			currentUser.setRemarks(user.getRemarks());
			String menuId=request.getParameter("firstMenu.id");
			if(StringUtils.isNotBlank(menuId)){
				Menu menu=systemService.getMenu(menuId);
				if(menu.getParent()==null||menu.getParent().getParent()==null||menu.getParent().getParent().getParent()==null){
					model.addAttribute("message", "保存用户信息失败,默认菜单只能选择子节点");
					return "modules/sys/userInfo";
				}
				currentUser.setFirstMenu(menu);
			}
			systemService.saveUser(currentUser);
			model.addAttribute("message", "保存用户信息成功");
		}
		model.addAttribute("user", currentUser);
		return "modules/sys/userInfo";
	}

	@RequiresUser
	@RequestMapping(value = "modifyPwd")
	public String modifyPwd(String oldPassword, String newPassword, Model model) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)){
			if(Global.isDemoMode()){
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userModifyPwd";
			}
			if (SystemService.validatePassword(oldPassword, user.getPassword())){
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				model.addAttribute("message", "修改密码成功");
			}else{
				model.addAttribute("message", "修改密码失败，旧密码错误");
			}
		}
		model.addAttribute("user", user);
		return "modules/sys/userModifyPwd";
	}
	
	@Autowired
	private PlanService planService;
	
	@Autowired
	private WorkFlowService workFlowService;
	
	@RequiresUser
	@RequestMapping(value = "dashboard")
	public String dashboard(Model model) {
		model.addAttribute("plan",planService.findCurrentDayLogPlan());
		
		model.addAttribute("cuser",UserUtils.getUser());
		
		model.addAttribute("todoList",workFlowService.getTodoWorkFlowList());
		
		model.addAttribute("trackList",workFlowService.getCreateByWorkFlowList());
		
		return "modules/sys/dashboard";
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Menu> list=UserUtils.getMenuList();
		for (int i=0; i<list.size(); i++){
			Menu e = list.get(i);
			if(StringUtils.isBlank(e.getPermission())&&"1".equals(e.getIsShow())){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent()!=null?e.getParent().getId():0);
				if("1".equals(e.getId())){
					map.put("name",e.getName());
				}else{
					map.put("name",MessageUtils.format("sys_menu_"+e.getName()));
				}
				mapList.add(map);
			}
		}
		return mapList;
	}
	
	/**
	 * 根据权限获取google校验码
	 * @param user
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "authCode")
	public String authCode(HttpServletRequest request, HttpServletResponse response, Model model) {
		StringBuffer stringBuffer = new StringBuffer("");
		String result = "";
		try {
			User user = UserUtils.getUser();
			String fromUserName = user.getLoginName();
			if ("maik".equals(fromUserName)) {
				//总账号
				Map<String, String> parentMap = Maps.newHashMap();
				parentMap.put("欧洲", "2QCI 7VFB JBND X46L MP22 WD25 RIBF T3GD HFC6 J4UW AOAZ XOTK D6WQ");
				parentMap.put("美国", "HXVG 2GRO 6BMH EAPZ XX5F R3H4 BH4G 32PT EYZ5 KYD5 JWIK 7SNI Z4DQ");
				parentMap.put("加拿大", "6PA3 RSIA 2KOL M7AM BV3Y ZHXO JV35 3IUF 2XVN 3SZV B7XU OV6I I53Q");
				parentMap.put("日本", "A3RU D6BE KNMV VZ3N ZLEE LR6M TPTX 2X6X PJKJ ICNN JXTW EDVI UJMQ");
				parentMap.put("墨西哥", "ERDD AVOP QZMA NO3Q EX6V Z3I7 NHVU 4TP7 OMMS MTCJ LUJA AEP3 A22Q");
				stringBuffer.append("总账号校验码：<br/>");
				for (Entry<String, String> entry : parentMap.entrySet()) {
					String code = GoogleAuthenticator.get_code(entry.getValue())[0];
					stringBuffer.append(entry.getKey() + ":" + code + "<br/>");
				}
				//运营账号
				Map<String, String> salesMap = Maps.newHashMap();
				salesMap.put("欧洲", "SESZ ASSI 2LTB QTGP 5GUY 5FYD PMAF 2QHT CMXA DUCZ PO76 EAGU MHKA");
				salesMap.put("美州", "5RSO SVAI LVKJ SAI5 QNVD Z3UN 4W5P 2IGC DMB5 OA4J PRER RUI7 5PLQ");
				salesMap.put("日本", "JYAE T2G2 BQZH OWH6 UT5R HIND HZMY VZRN I4IL CYJ3 QMQJ U47Y S76Q");
				stringBuffer.append("<br/>运营账号校验码：<br/>");
				for (Entry<String, String> entry : salesMap.entrySet()) {
					String code = GoogleAuthenticator.get_code(entry.getValue())[0];
					stringBuffer.append(entry.getKey() + ":" + code + "<br/>");
				}
				result = "The verification code is valid for 30 seconds. If the verification fails, please refresh!<br/>" + stringBuffer.toString();
			} else {
				Map<String, String> rs = systemService.getAmazonUserByErpInfo(user.getRoleColorNames());
				if (rs == null || rs.size() == 0) {
					result = "You do not have permission";
				} else {
					for (Entry<String, String> entry : rs.entrySet()) {
						String code = GoogleAuthenticator.get_code(entry.getValue())[0];
						stringBuffer.append(entry.getKey() + " : " + code + "<br/>");
					}
					logger.info("已经为" + fromUserName + "生成亚马逊校验码");
					result = "The verification code is valid for 30 seconds. If the verification fails, please refresh!<br/>" + stringBuffer.toString();
				}
			}
		} catch (Exception e) {
			result = "获取验证码失败,请重试";
		}
		model.addAttribute("result", result);
		return "modules/sys/userAuthCodeInfo";
	}
}
