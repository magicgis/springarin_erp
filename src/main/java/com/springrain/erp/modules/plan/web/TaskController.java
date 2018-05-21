package com.springrain.erp.modules.plan.web;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.plan.entity.Task;
import com.springrain.erp.modules.plan.service.PlanTaskService;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 任务管理Controller
 * @author tim
 * @version 2014-04-21
 */
@Controller
@RequestMapping(value = "${adminPath}/plan/task")
public class TaskController extends BaseController {

	@Autowired
	private PlanTaskService taskService;
	
	@Autowired
	private MailManager mailManager;
	
	@Autowired
	private SystemService systemService;
	
	@ModelAttribute
	public Task get(@RequestParam(required=false) String id) {
		if (!StringUtils.isBlank(id)){
			return taskService.get(id);
		}else{
			return new Task();
		}
	}
	
	@RequiresPermissions("plan:task:view")
	@RequestMapping(value = {"list", ""})
	public String list(Task task, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			task.setCreateBy(user);
		}
		Page<Task> page = new Page<Task>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("createDate desc");
		}else{
			page.setOrderBy(orderBy+",createDate desc");
		}	
		page = taskService.find(page, task); 
        model.addAttribute("page", page);
        model.addAttribute("cuser", user);
        page.setOrderBy(orderBy);
		return "modules/plan/taskList";
	}

	@RequiresPermissions("plan:task:view")
	@RequestMapping(value = "form")
	public String form(Task task, Model model) {
		model.addAttribute("task", task);
		return "modules/plan/taskForm";
	}

	@RequiresPermissions("plan:task:edit")
	@RequestMapping(value = "save")
	public String save(MultipartFile attchmentFile,Task task, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, task)){
			return form(task, model);
		}
		String toAddress = "";
		Set<String> offids = Sets.newHashSet();
		StringBuffer sb = new StringBuffer("");
		for (User performer : task.getPerformers()) {
			User user = systemService.getUser(performer.getId());
			sb.append(user.getEmail()+",");
			offids.add(user.getOffice().getId());
		}
		//自己也发送一份
		toAddress = sb.toString() +UserUtils.getUser().getEmail();
		//找到对应的部门主管
		Role role = systemService.findRoleByName("部门管理员");
		sb = new StringBuffer("");
		if(role!=null){
			List<User> users = role.getUserList();
			for (User user : users) {
				if(offids.contains(user.getOffice().getId())){
					sb.append(user.getEmail()+",");
				}
			}
		}
		String toCCAddress = sb.toString();
		//发送邮件
		MailInfo mailInfo = new MailInfo(toAddress,task.getSubject(),new Date());
		if(toCCAddress.length()>0){
			mailInfo.setCcToAddress(toCCAddress.substring(0,toCCAddress.length()-1));
		}
		if(attchmentFile.getSize()!=0){
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/task";
			File baseDir = new File(baseDirStr); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String name = attchmentFile.getOriginalFilename();
			name = name +"_"+ new Date().getTime();
			File dest = new File(baseDir,name);
			mailInfo.setFileName(attchmentFile.getOriginalFilename());
			mailInfo.setFilePath(dest.getAbsolutePath());
			try {
				FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
				task.setAttchmentPath(Global.getCkBaseDir()+"task"+"/"+name);
			} catch (IOException e) {
				logger.warn(name+"文件保存失败",e);
			}
		}
		task.setRemarks(HtmlUtils.htmlUnescape(task.getRemarks()));
		taskService.save(task);
		String content = "任务派发人："+task.getCreateBy().getName()+"("
					+task.getCreateBy().getEmail()+")<br/><br/>"+task.getRemarks()+"<br/><br/>---------------系统发送邮件，请不要回复！";
		mailInfo.setContent(content);
		if(mailManager.send(mailInfo)){
			addMessage(redirectAttributes, "'"+task.getSubject()+"'任务邮件发送成功");
		}else{
			addMessage(redirectAttributes, "'"+task.getSubject()+"'任务邮件发送失败");
		};
		return "redirect:"+Global.getAdminPath()+"/plan/task/?repage";
	}
	
	@RequiresPermissions("plan:task:edit")
	@RequestMapping(value = "finish")
	public String save(Task task, Model model, RedirectAttributes redirectAttributes) {
		taskService.save(task);
		addMessage(redirectAttributes, "'"+task.getSubject()+"'任务状态修改成功");
		return "redirect:"+Global.getAdminPath()+"/plan/task/?repage";
	}
	
	@RequiresPermissions("plan:task:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		taskService.delete(id);
		addMessage(redirectAttributes, "删除任务成功");
		return "redirect:"+Global.getAdminPath()+"/plan/task/?repage";
	}

}
