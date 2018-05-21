package com.springrain.erp.modules.custom.web;
import java.util.Date;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.custom.entity.AutoReply;
import com.springrain.erp.modules.custom.service.AutoReplyService;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.OfficeService;

/**
 * 邮件自动回复Controller
 * @author tim
 * @version 2014-09-24
 */
@Controller
@RequestMapping(value = "${adminPath}/custom/autoReply")
public class AutoReplyController extends BaseController {

	@Autowired
	private AutoReplyService autoReplyService;
	
	@Autowired
	private OfficeService officeService;
	
	@RequiresUser
	@RequestMapping(value = "form")
	public String form(AutoReply autoReply, Model model) {
		AutoReply temp = autoReplyService.findByType(autoReply);
		model.addAttribute("autoReply",temp==null?autoReply:temp);
		List<User> all = Lists.newArrayList();
        for (Office office :  officeService.findAll()) {
        	all.addAll(office.getUserList());
		}
        model.addAttribute("all", all);
		return "modules/custom/autoReplyForm";
	}
	
	@RequiresUser
	@RequestMapping(value = "save")
	public String save(AutoReply autoReply, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, autoReply)){
			return form(autoReply, model);
		}
		autoReply.setSubject(HtmlUtils.htmlUnescape(autoReply.getSubject()));
		autoReply.setContent(HtmlUtils.htmlUnescape(autoReply.getContent()));
		autoReplyService.save(autoReply);
		addMessage(redirectAttributes, "save autoReply success!");
		return "redirect:"+Global.getAdminPath()+"/custom/autoReply/form?type="+autoReply.getType();
	}
	public static void main(String[] args) {
		long a =new Date(1970, 0, 1).getTime();
		long b =new Date(2014, 0, 1).getTime();
		long c =new Date(2014, 5, 20).getTime();
		System.out.println(b-a);
		System.out.println(c-a);
	}
}
