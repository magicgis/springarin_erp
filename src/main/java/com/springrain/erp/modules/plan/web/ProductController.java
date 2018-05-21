package com.springrain.erp.modules.plan.web;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.Collections3;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.plan.entity.Product;
import com.springrain.erp.modules.plan.entity.ProductFlow;
import com.springrain.erp.modules.plan.service.ProductService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.OfficeService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 产品管理Controller
 * @author tim
 * @version 2014-04-02
 */
@Controller
@RequestMapping(value = "${adminPath}/plan/product")
public class ProductController extends BaseController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private OfficeService officeService;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@ModelAttribute
	public Product get(@RequestParam(required=false) String id) {
		if (!StringUtils.isBlank(id)){
			return productService.get(id);
		}else{
			return new Product();
		}
	}
	
	@RequiresPermissions("plan:product:view")
	@RequestMapping(value = {""})
	public String list(Product product, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			product.setCreateBy(user);
		}
		Page<Product> page = new Page<Product>(request, response,20);
		String orderBy = page.getOrderBy();
		
		if("".equals(orderBy)){
			page.setOrderBy("createDate desc");
		}else{
			page.setOrderBy(orderBy+",createDate desc");
		}
        page = productService.find(page, product); 
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
		return "modules/plan/productList";
	}

	@RequiresPermissions("plan:product:view")
	@RequestMapping(value = "form")
	public String form(Product product, Model model) {
		Map masterItems = Collections3.extractToMap(officeService.get("5").getUserList(),"id","name");
		model.addAttribute("masterItems", masterItems);
		model.addAttribute("product", product);
		return "modules/plan/productForm";
	}

	@RequiresPermissions("plan:product:edit")
	@RequestMapping(value = "save")
	public String save(MultipartFile imagePeview,Product product,Model model, RedirectAttributes redirectAttributes) {
		
		if (!beanValidator(model, product)){
			return form(product, model);
		}
		if(imagePeview.getSize()!=0){
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir();
			File baseDir = new File(baseDirStr); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String name = imagePeview.getOriginalFilename();
			String suffix = name.substring(name.lastIndexOf("."));
			name = UUID.randomUUID().toString();
			name = name +suffix;
			try {
				File imageFile = new File(baseDir,name);
				FileUtils.copyInputStreamToFile(imagePeview.getInputStream(),imageFile);
				logger.info("图片保存成功:"+imageFile.getAbsolutePath());
				product.setImgPath(Global.getCkBaseDir()+name);
			} catch (IOException e) {
				logger.warn(name+"文件保存失败",e);
			}
		}
		if("7".equals(product.getFinish())){
			addMessage(redirectAttributes, product.getName()+"新品开发完成");
			List<ProductFlow> list =  product.getListFlow();
			int index = list.size();
			if(index>0){
				product.setEndDate(list.get(index-1).getEndDate());
			}
		}else{
			addMessage(redirectAttributes, "保存产品管理'" + product.getName() + "'成功");
		}
		productService.save(product);
		return "redirect:"+Global.getAdminPath()+"/plan/product";
	}
		
	@RequiresPermissions("plan:product:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		productService.delete(id);
		addMessage(redirectAttributes, "删除产品管理成功");
		return "redirect:"+Global.getAdminPath()+"/plan/product/?repage";
	}

}
