package com.springrain.erp.modules.sys.web;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springrain.erp.common.web.BaseController;

/**
 * 帮助Controller
 * @author Michael
 * @version 2016-2-15
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/help")
public class HelpController extends BaseController {

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"","help"})
	public String help(String url, Model model) {
		return "help"+url;
	}

	
}
