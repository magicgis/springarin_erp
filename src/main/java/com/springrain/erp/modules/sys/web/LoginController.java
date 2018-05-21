/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.springrain.erp.modules.sys.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.util.WebUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.CacheUtils;
import com.springrain.erp.common.utils.CookieUtils;
import com.springrain.erp.common.utils.HttpRequest;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.security.SystemAuthorizingRealm;
import com.springrain.erp.modules.sys.security.UsernamePasswordToken;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 登录Controller
 * 
 * @author ThinkGem
 * @version 2013-5-31
 */
@Controller
public class LoginController extends BaseController {

	@Autowired
	private CookieLocaleResolver localeResolver;
	
	@Autowired
	private SystemService systemService;
	
	/**
	 * 管理登录
	 * http://127.0.0.1:8090/springrain-erp/a/login?username=admin&loginType=0
	 */
	@RequestMapping(value = "${adminPath}/login", method = RequestMethod.GET)
	public String getLogin(String code,
			HttpServletRequest request,
			HttpServletResponse response, Model model) {
		if (StringUtils.isNotEmpty(code)) {
			try {
				String userId = getUserIdByDingCode(code);
				User user = systemService.getUserByUserId(userId);
				if (user != null) {
					Subject subject = SecurityUtils.getSubject();  
			        UsernamePasswordToken up = new UsernamePasswordToken(user.getLoginName(), null, false, "", "", "1");  
			        subject.login(up);
				} else {
					logger.info("用户信息不存在");
				}
			} catch (Exception e) {
			}
		}
     
		User user = UserUtils.getUserKeepSession();
		// 如果已经登录，则跳转到管理首页
		if (user.getId() != null) {
			user = systemService.getUserByLoginName(user.getLoginName());
			if (user != null) {
				return "redirect:" + Global.getAdminPath();
			}
		}
		return "modules/sys/sysLogin";
	}

	/**
	 * 登录失败，真正登录的POST请求由Filter完成
	 */
	@RequestMapping(value = "${adminPath}/login", method = RequestMethod.POST)
	public String login(
			@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String username,
			HttpServletRequest request, HttpServletResponse response,
			Model model) {
		User user = UserUtils.getUser();
		// 如果已经登录，则跳转到管理首页
		if (user.getId() != null) {
			user = systemService.getUserByLoginName(user.getLoginName());
			if (user!= null) {
				return "redirect:" + Global.getAdminPath();
			}
		}
		model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM,
				username);
		model.addAttribute("isValidateCodeLogin",
				isValidateCodeLogin(username, true, false));

		return "modules/sys/sysLogin";
	}

	/**
	 * 登录成功，进入管理首页
	 */
	@RequiresUser
	@RequestMapping(value = "${adminPath}")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		User user = UserUtils.getUser();
		// 未登录，则跳转到登录页
		if (user.getId() == null) {
			return "redirect:" + Global.getAdminPath() + "/login";
		}
		user = systemService.getUserByLoginName(user.getLoginName());
		if (user == null) {	//用户已经删除了
			return "redirect:" + Global.getAdminPath() + "/login";
		}
		// 登录成功后，验证码计算器清零
		isValidateCodeLogin(user.getLoginName(), false, true);
		// 登录成功后，获取上次登录的当前站点ID
		UserUtils.putCache("siteId", CookieUtils.getCookie(request, "siteId"));
		// 更新登录IP和时间
		systemService.updateUserLoginInfo(user.getId());
		//设置默认的语言环境
		Cookie cookie = WebUtils.getCookie(request, "locale");
		if(cookie==null){
			localeResolver.setLocale(request, response, Locale.getDefault());
		}
		return "modules/sys/sysIndex";
	}

	/**
	 * 获取主题方案
	 */
	@RequestMapping(value = "/theme/{theme}")
	public String getThemeInCookie(@PathVariable String theme,
			HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isNotBlank(theme)) {
			CookieUtils.setCookie(response, "theme", theme);
		}
		return "redirect:" + request.getParameter("url");
	}

	/**
	 * 是否是验证码登录
	 * 
	 * @param useruame
	 *            用户名
	 * @param isFail
	 *            计数加1
	 * @param clean
	 *            计数清零
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValidateCodeLogin(String useruame, boolean isFail,
			boolean clean) {
		Map<String, Integer> loginFailMap = (Map<String, Integer>) CacheUtils
				.get("loginFailMap");
		if (loginFailMap == null) {
			loginFailMap = Maps.newHashMap();
			CacheUtils.put("loginFailMap", loginFailMap);
		}
		Integer loginFailNum = loginFailMap.get(useruame);
		if (loginFailNum == null) {
			loginFailNum = 0;
		}
		if (isFail) {
			loginFailNum++;
			loginFailMap.put(useruame, loginFailNum);
		}
		if (clean) {
			loginFailMap.remove(useruame);
		}
		return loginFailNum >= 300;
	}

	@RequestMapping("${adminPath}/download")
	public String download(@RequestParam String filePath,
			HttpServletResponse response) {
		File file = new File(filePath);
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(filePath);
			response.reset();
			response.setContentType("application/octet-stream;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ file.getName() + "\"");
			OutputStream outputStream = new BufferedOutputStream(
					response.getOutputStream());
			byte data[] = new byte[1024];
			while (inputStream.read(data, 0, 1024) >= 0) {
				outputStream.write(data);
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping("${adminPath}/changeLocal")
	public String  changeLocal(HttpServletRequest request, String local,String childId,
			HttpServletResponse response) {
		if ("zh".equals(local))
			localeResolver.setLocale(request, response, Locale.CHINA);
		else if ("en".equals(local))
			localeResolver.setLocale(request, response, Locale.US);
		else if ("de".equals(local))
			localeResolver.setLocale(request, response, Locale.GERMANY);
		if(StringUtils.isNotBlank(childId)){
			UserUtils.getUser().setFirstMenu(systemService.getMenu(childId));
		}
		return "redirect:"+Global.getAdminPath()+"";
	}

	@RequestMapping("${adminPath}/changeToPc")
	public ModelAndView changeToPc(HttpServletRequest request, String type,
			HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (type != null && "1".equals(type)) {
				session.setAttribute("applicationType", "pc");
			} else {
				session.setAttribute("applicationType", "mobile");
			}
			
		}
		return new ModelAndView("redirect:"+Global.getAdminPath()+"");
	}
	
	public String getUserIdByDingCode(String code) throws Exception{
	    String tokenJson = HttpRequest.sendGet("https://oapi.dingtalk.com/sns/gettoken","appid=dingoateeb2gjgtmoyzgch&appsecret=np9m3jMiycP7yTZAZkcajJ7SjBaQEnQ91hfd3OAWAMVWGnJk3V55Km4qpZUV9nYh");
	    Map<String,String> tokenMap = JSON.parseObject(tokenJson, Map.class);
	    String token = tokenMap.get("access_token");
	    Map<String,String> params = Maps.newHashMap();
	    params.put("tmp_auth_code", code);
	    JSONObject object = HttpRequest.httpPost("https://oapi.dingtalk.com/sns/get_persistent_code?access_token="+token, params);
	    String unionid =object.getString("unionid");
	    tokenJson = HttpRequest.sendGet("https://oapi.dingtalk.com/gettoken","corpid=dingfe21d7052d09c52535c2f4657eb6378f&corpsecret=_j1s_hKiM0uaDH3uBM4qpSbsVO-RSZ2pg-XNIR9rHFSQEslMHK8bWvaIE4HECV-o");
	    tokenMap = JSON.parseObject(tokenJson, Map.class);
	    token = tokenMap.get("access_token");
	    tokenJson = HttpRequest.sendGet("https://oapi.dingtalk.com/user/getUseridByUnionid","access_token="+token+"&unionid="+unionid);
	    tokenMap = JSON.parseObject(tokenJson, Map.class);
	    String userId = tokenMap.get("userid");
	    return userId;
	  }
}
