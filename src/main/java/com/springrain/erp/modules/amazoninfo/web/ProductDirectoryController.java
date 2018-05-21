/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectory;
import com.springrain.erp.modules.amazoninfo.service.ProductDirectoryService;

/**
 * 亚马逊产品目录Controller
 * @author Michael
 * @version 2015-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/productDirectory")
public class ProductDirectoryController extends BaseController {
	@Autowired
	private ProductDirectoryService 	productDirectoryService;
	@RequestMapping(value = {"list", ""})
	public String list(ProductDirectory productDirectory, String isCheck,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if(StringUtils.isEmpty(isCheck)){
			isCheck="1";
		}
		Page<ProductDirectory> page = productDirectoryService.find(new Page<ProductDirectory>(request, response), productDirectory,isCheck);
        model.addAttribute("isCheck", isCheck);
        model.addAttribute("page", page);
		return "modules/amazoninfo/productDirectoryList";
	}
	
	
	
	@RequestMapping(value = "form")
	public String form(ProductDirectory productDirectory, Model model) {
		if(productDirectory.getId()!=null){
			productDirectory = this.productDirectoryService.get(productDirectory.getId());
		}
		model.addAttribute("productDirectory", productDirectory);
		return "modules/amazoninfo/productDirectoryForm";
	}
	

	@RequestMapping(value = "scan")
	public String scan(ProductDirectory productDirectory, Model model) {
		if(productDirectory.getId()!=null){
			productDirectory=this.productDirectoryService.get(productDirectory.getId());
		}
		model.addAttribute("productDirectory", productDirectory);
		return "modules/amazoninfo/productDirectoryView";
	}
	
	
	@RequestMapping(value = "save")
	public String save(ProductDirectory productDirectory,String inStockSta, Model model, RedirectAttributes redirectAttributes) {
		if(productDirectory.getId()==null||"0".equals(productDirectory.getLockSta())){
			productDirectory.setActiveDate(new Date());
		}
		this.productDirectoryService.save(productDirectory);
		addMessage(redirectAttributes, "保存产品目录'" +productDirectory.getUrl()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/productDirectory/?repage";
	}
	
	
	@RequestMapping(value = "cancel")
	public String cancel(ProductDirectory productDirectory, Model model,RedirectAttributes redirectAttributes) {
		this.productDirectoryService.updateSta(productDirectory.getId(), "1");
		addMessage(redirectAttributes, "取消产品目录'" + productDirectory.getId()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/productDirectory/?repage";
	}
	
	/**
	 *解冻7天 
	 */
	@RequestMapping(value = "unLock")
	public String unLock(ProductDirectory productDirectory, Model model,RedirectAttributes redirectAttributes) {
		this.productDirectoryService.unLock(productDirectory.getId());
		addMessage(redirectAttributes, "解冻产品目录7天'" + productDirectory.getId()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/productDirectory/?repage";
	}
	
	@RequestMapping(value = "isExistUrl")
	@ResponseBody
	public String isExistName(String url,Integer id) {
		return (!this.productDirectoryService.isExistUrl(url,id))+"";
	}
	
}
