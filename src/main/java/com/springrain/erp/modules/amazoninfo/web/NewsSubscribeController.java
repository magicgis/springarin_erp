package com.springrain.erp.modules.amazoninfo.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.NewsSubscribe;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.NewsSubscribeService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 消息订阅Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/newsSubscribe")
public class NewsSubscribeController extends BaseController {

	@Autowired
	private NewsSubscribeService newsSubscribeService;

	@Autowired
	private AmazonProductService amazonProductService;
	
	@Autowired
	private PsiProductTypeGroupDictService dictService;
	
    public static Map<String, String> emailTypeMap;	//map<编号  名称>
    public static Map<String, String> attrMap = Maps.newHashMap();	//map<编号  名称>
	
	static {
//		emailTypeMap = Maps.newHashMap();
//		emailTypeMap.put("10", "实时价格变动");
//		emailTypeMap.put("11", "FBA库存低于5日预警");
//		emailTypeMap.put("12", "昨日价格变动");
//		emailTypeMap.put("13", "自动cross贴子");
		attrMap = Maps.newHashMap();
		attrMap.put("1", "新品");
		attrMap.put("2", "主力产品");
		attrMap.put("3", "淘汰品");
	}
	
	@ModelAttribute
	public NewsSubscribe get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return newsSubscribeService.get(id);
		}else{
			return new NewsSubscribe();
		}
	}

	@RequestMapping(value = {"list", ""})
	public String list(NewsSubscribe newsSubscribe, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Object[]> list = newsSubscribeService.findNews();
		if (emailTypeMap == null || emailTypeMap.size() != list.size()) {
			emailTypeMap = Maps.newHashMap();
			for (Object[] obj : list) {
				emailTypeMap.put(obj[0].toString(), obj[1].toString());
			}
		}
		Page<NewsSubscribe> page = new Page<NewsSubscribe>(request, response);
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id desc");
		} else {
			page.setOrderBy(orderBy);
		}
		page = newsSubscribeService.find(page, newsSubscribe);
		model.addAttribute("page", page);
		return "modules/amazoninfo/newsSubscribeList";
	}
	
	@RequestMapping(value = {"form"})
	public String form(NewsSubscribe newsSubscribe, Model model) {
		if (StringUtils.isNotEmpty(newsSubscribe.getPlatform())) {
			model.addAttribute("countrys", newsSubscribe.getPlatform().split(","));
		}
		//根据类型设置联动下拉框
		setSelectValue(newsSubscribe, model);
		if (StringUtils.isEmpty(newsSubscribe.getEmail())) {
			newsSubscribe.setEmail(UserUtils.getUser().getEmail());
		}
		model.addAttribute("newsSubscribe", newsSubscribe);
		model.addAttribute("newsList", newsSubscribeService.findNews());
		return "modules/amazoninfo/newsSubscribeForm";
	}
	
	@RequestMapping(value = {"save"})
	public String save(NewsSubscribe newsSubscribe, Model model, RedirectAttributes redirectAttributes) {
		if (StringUtils.isNotEmpty(newsSubscribe.getPlatform()) && newsSubscribe.getPlatform().contains("all")) {
			newsSubscribe.setPlatform("de,com,uk,fr,it,es,jp,ca");
		}
		if (StringUtils.isNotEmpty(newsSubscribe.getProductName()) && newsSubscribe.getProductName().length() >=2000) {
			if (StringUtils.isNotEmpty(newsSubscribe.getPlatform())) {
				model.addAttribute("countrys", newsSubscribe.getPlatform().split(","));
			}
			List<String> productNames = amazonProductService.findAllProductName();
			String[] selectName = newsSubscribe.getProductName().split(",");
			productNames.removeAll(Lists.newArrayList(selectName));
			model.addAttribute("selectName", Lists.newArrayList(selectName));
			model.addAttribute("productNames", productNames);
			model.addAttribute("newsSubscribe", newsSubscribe);
			model.addAttribute("message", "产品长度已超出限制,请减少产品数量！");
			return "modules/amazoninfo/newsSubscribeForm";
		}
		if (StringUtils.isEmpty(newsSubscribe.getEmail())) {
			newsSubscribe.setEmail(UserUtils.getUser().getEmail());
		}
		if (newsSubscribe.getId() == null) {
			newsSubscribe.setDelFlag("0");
			newsSubscribe.setCreateBy(UserUtils.getUser());
			newsSubscribe.setCreateDate(new Date());
		}
		newsSubscribeService.save(newsSubscribe);
		addMessage(redirectAttributes, "操作成功！");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/newsSubscribe/?repage";
	}
	
	@RequestMapping(value = {"updateState"})
	public String updateState(NewsSubscribe newsSubscribe, Model model, RedirectAttributes redirectAttributes) {
		newsSubscribeService.save(newsSubscribe);
		addMessage(redirectAttributes, "操作成功！");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/newsSubscribe/?repage";
	}
	
	@RequestMapping(value = {"view"})
	public String view(NewsSubscribe newsSubscribe, Model model) {
		model.addAttribute("newsSubscribe", newsSubscribe);
		model.addAttribute("newsList", newsSubscribeService.findNews());
		return "modules/amazoninfo/newsSubscribeView";
	}
	
	@RequestMapping(value = {"delete"})
	public String delete(NewsSubscribe newsSubscribe, Model model, RedirectAttributes redirectAttributes) {
		newsSubscribe.setDelFlag("1");
		newsSubscribeService.save(newsSubscribe);
		addMessage(redirectAttributes, "删除成功！");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/newsSubscribe/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = "getOption")
	public String getOption(String type) {
		if (StringUtils.isEmpty(type)) {
			return null;
		}
		List<Map<String, String>> rs = Lists.newArrayList();
		if ("1".equals(type)) {
			List<String> productNames = amazonProductService.findAllProductName();
			for (String productName : productNames) {
				Map<String, String> map = Maps.newHashMap();
				map.put("key", productName);
				map.put("value", productName);
				rs.add(map);
			}
		} else if ("2".equals(type)) {
			for (String productType : dictService.getAllProductTypeList()) {
				Map<String, String> map = Maps.newHashMap();
				map.put("key", productType);
				map.put("value", productType);
				rs.add(map);
			}
		} else if ("3".equals(type)) {
			for (String line : dictService.getAllLineShotrName()) {
				Map<String, String> map = Maps.newHashMap();
				map.put("key", line);
				map.put("value", line);
				rs.add(map);
			}
		} else if ("4".equals(type)) {
			for (String key : attrMap.keySet()) {
				Map<String, String> map = Maps.newHashMap();
				map.put("key", key);
				map.put("value", attrMap.get(key));
				rs.add(map);
			}
		}
		return JSON.toJSONString(rs);
	}
	
	public void setSelectValue(NewsSubscribe newsSubscribe,Model model) {
		if (!"4".equals(newsSubscribe.getType())) {
			List<String> productNames = amazonProductService.findAllProductName();
			if ("2".equals(newsSubscribe.getType())) {
				productNames = dictService.getAllProductTypeList();
			} else if ("3".equals(newsSubscribe.getType())) {
				productNames = dictService.getAllLineShotrName();
			}
			String[] selectName = null;
			if (StringUtils.isNotEmpty(newsSubscribe.getProductName())) {
				selectName = newsSubscribe.getProductName().split(",");
			}
			if (selectName != null && selectName.length > 0) {
				productNames.removeAll(Lists.newArrayList(selectName));
				Map<String, String> map = Maps.newLinkedHashMap();
				for (String selected : selectName) {
					map.put(selected, selected);
				}
				model.addAttribute("selectName", map);
			}
			Map<String, String> notSelectMap = Maps.newLinkedHashMap();
			for (String productName : productNames) {
				notSelectMap.put(productName, productName);
			}
			model.addAttribute("productNames", notSelectMap);
		} else {
			List<String> productNames = Lists.newArrayList("1","2","3");
			String[] selectName = null;
			if (StringUtils.isNotEmpty(newsSubscribe.getProductName())) {
				selectName = newsSubscribe.getProductName().split(",");
			}
			if (selectName != null && selectName.length > 0) {
				productNames.removeAll(Lists.newArrayList(selectName));
				Map<String, String> map = Maps.newLinkedHashMap();
				for (String selected : selectName) {
					map.put(selected, attrMap.get(selected));
				}
				model.addAttribute("selectName", map);
			}
			Map<String, String> notSelectMap = Maps.newLinkedHashMap();
			for (String key : productNames) {
				notSelectMap.put(key, attrMap.get(key));
			}
			model.addAttribute("productNames", notSelectMap);
		}
	}
	
}
