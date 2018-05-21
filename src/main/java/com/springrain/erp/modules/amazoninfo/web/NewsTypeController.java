package com.springrain.erp.modules.amazoninfo.web;

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
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.NewsType;
import com.springrain.erp.modules.amazoninfo.service.NewsTypeService;

/**
 * 消息订阅内容管理Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/newsType")
public class NewsTypeController extends BaseController {

	@Autowired
	private NewsTypeService newsTypeService;

	
	@ModelAttribute
	public NewsType get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return newsTypeService.get(id);
		}else{
			return new NewsType();
		}
	}

	@RequestMapping(value = {"list", ""})
	public String list(NewsType newsType, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<NewsType> page = new Page<NewsType>(request, response);
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id desc");
		} else {
			page.setOrderBy(orderBy);
		}
		page = newsTypeService.find(page, newsType);
		model.addAttribute("page", page);
		return "modules/amazoninfo/newsTypeList";
	}
	
	@RequestMapping(value = {"form"})
	public String form(NewsType newsType, Model model) {
		model.addAttribute("newsType", newsType);
		return "modules/amazoninfo/newsTypeForm";
	}
	
	@RequestMapping(value = {"save"})
	public String save(NewsType newsType, Model model, RedirectAttributes redirectAttributes) {
		if (newsType.getId() == null) {
			newsType.setNumber(newsTypeService.getNewNumber());
			newsType.setState("1");
			newsType.setType("1");
			newsType.setSort(10);
			newsType.setDelFlag("0");
		}
		newsTypeService.save(newsType);
		addMessage(redirectAttributes, "操作成功！");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/newsType/?repage";
	}
	
	@RequestMapping(value = {"view"})
	public String view(NewsType newsType, Model model) {
		model.addAttribute("newsType", newsType);
		return "modules/amazoninfo/newsTypeView";
	}
	
	@RequestMapping(value = {"delete"})
	public String delete(NewsType newsType, Model model, RedirectAttributes redirectAttributes) {
		newsType.setDelFlag("1");
		addMessage(redirectAttributes, "删除成功！");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/newsType/?repage";
	}
	
}
