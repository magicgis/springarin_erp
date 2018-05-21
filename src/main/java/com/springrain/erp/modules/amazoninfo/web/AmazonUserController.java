package com.springrain.erp.modules.amazoninfo.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonLoginLog;
import com.springrain.erp.modules.amazoninfo.entity.AmazonUser;
import com.springrain.erp.modules.amazoninfo.service.AmazonUserService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/amazonUser")
public class AmazonUserController extends BaseController {
	
	@Autowired
	private AmazonUserService amazonUserService;
	
	@ModelAttribute
	public AmazonUser get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return amazonUserService.get(id);
		}else{
			return new AmazonUser();
		}
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		List<AmazonUser> list = amazonUserService.findAll();
		model.addAttribute("list", list);
		return "modules/amazoninfo/amazonUserList";
	}
	
	@RequestMapping(value = "form")
	public String form(AmazonUser amazonUser, HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("amazonUser", amazonUser);
		return "modules/amazoninfo/amazonUserModifyPwd";
	}
	
	//修改密码
	@RequestMapping(value = "modifyPwd")
	public String modifyPwd(AmazonUser amazonUser, String oldPassword, String newPassword, 
			HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		if (oldPassword.equals(amazonUser.getPassword())) {
			amazonUser.setPassword(newPassword);
			amazonUser.setUpdateBy(UserUtils.getUser());
			amazonUser.setUpdateDate(new Date());
			amazonUserService.save(amazonUser);
			addMessage(redirectAttributes, "操作成功！");
			return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonUser/?repage";
		} else {
			model.addAttribute("amazonUser", amazonUser);
			model.addAttribute("message", "修改密码失败，旧密码错误");
			return "modules/amazoninfo/amazonUserModifyPwd";
		}
	}
	
	@RequestMapping(value = "logList")
	public String logList(AmazonLoginLog amazonLoginLog, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		if (amazonLoginLog.getDataDate() == null) {
			Date date = new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			amazonLoginLog.setDataDate(DateUtils.addMonths(date, -1));
			amazonLoginLog.setEndDate(date);
		}
		Page<AmazonLoginLog> page = amazonUserService.findLog(new Page<AmazonLoginLog>(request, response), amazonLoginLog); 
	    model.addAttribute("page", page);
	    model.addAttribute("users", amazonUserService.findAllUsers());
		return "modules/amazoninfo/amazonLoginLogList";
	}
	
}
