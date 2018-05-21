/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.ProductReviewMonitor;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.ProductReviewMonitorService;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 折扣预警Controller
 * @author Michael
 * @version 2015-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/productReviewMonitor")
public class ProductReviewMonitorController extends BaseController {

	@Autowired
	private ProductReviewMonitorService productReviewMonitorService;
	@Autowired
	private AmazonProduct2Service ama2Service;
	
	@RequestMapping(value = {"list", ""})
	public String list(ProductReviewMonitor productReviewMonitor, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
		if(productReviewMonitor.getCreateDate()==null){
			productReviewMonitor.setCreateDate(sdf.parse((sdf.format(DateUtils.addMonths(new Date(), -3)))));
		}
		
        Page<ProductReviewMonitor> page = productReviewMonitorService.find(new Page<ProductReviewMonitor>(request, response), productReviewMonitor); 
        model.addAttribute("page", page);
		return "modules/amazoninfo/productReviewMonitorList";
	}

	@RequestMapping(value = "form")
	public String form(ProductReviewMonitor productReviewMonitor, Model model) {
		if(productReviewMonitor.getId()!=null){
			productReviewMonitor=this.productReviewMonitorService.get(productReviewMonitor.getId());
		}
		model.addAttribute("productReviewMonitor", productReviewMonitor);
		return "modules/amazoninfo/productReviewMonitorForm";
	}
		
	@RequestMapping(value = "show")
	public String show(ProductReviewMonitor productReviewMonitor, Model model) {
		if(productReviewMonitor.getId()!=null){
			productReviewMonitor=this.productReviewMonitorService.get(productReviewMonitor.getId());
		}
		model.addAttribute("productReviewMonitor", productReviewMonitor);
	return "modules/amazoninfo/productReviewMonitorBadList";
	}
  
  
	@RequestMapping(value = "save")
	public String save(ProductReviewMonitor productReviewMonitor, Model model, RedirectAttributes redirectAttributes) {
		if(productReviewMonitor.getId()==null){
			productReviewMonitor.setCreateDate(new Date());
			productReviewMonitor.setCreateUser(UserUtils.getUser());
		}
		productReviewMonitorService.save(productReviewMonitor);
		addMessage(redirectAttributes, "保存产品评论监控'" + ""+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/productReviewMonitor/?repage";
	}
	
	@RequestMapping(value = "cancel")
	public String cancel(ProductReviewMonitor productReviewMonitor, Model model,RedirectAttributes redirectAttributes) {
		productReviewMonitor = this.productReviewMonitorService.get(productReviewMonitor.getId());
		productReviewMonitor.setState("0");//已取消
		this.productReviewMonitorService.save(productReviewMonitor);
		addMessage(redirectAttributes, "取消产品评论监控'" + productReviewMonitor.getProductName()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/productReviewMonitor/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = {"ajaxSelfAsin"})
	public String ajaxSelfAsin(String asin) {
		String res="";
		Set<String> set = ama2Service.getAllAsin();
		if(set.contains(asin)){
			res="1";
		}
		String rs="{\"msg\":\""+res+"\"}";
		return rs;
	} 
	
}
