/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.parts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.parts.PsiParts;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.DictUtils;

/**
 * 产品配件Controller
 * @author Michael
 * @version 2015-06-01
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiParts")
public class PsiPartsController extends BaseController {

	@Autowired
	private PsiPartsService psiPartsService;
	@Autowired
	private PsiSupplierService psiSupplierService;
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"list", ""})
	public String list(PsiParts psiParts, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<PsiParts> page = psiPartsService.find(new Page<PsiParts>(request, response), psiParts); 
        //包材供应商
        List<PsiSupplier> suppliers=this.psiSupplierService.findSupplierByType(new String[]{"0","2"});
		model.addAttribute("suppliers", suppliers);
        model.addAttribute("page", page);
		return "modules/psi/parts/psiPartsList";
	}

	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "form")
	public String form(PsiParts psiParts, Model model) {
	    List<PsiSupplier> suppliers=this.psiSupplierService.findSupplierByType(new String[]{"0","2"});
	    if(psiParts.getId()!=null){
	    	psiParts=this.psiPartsService.get(psiParts.getId());
	    	psiParts.setOldPrice(psiParts.getPrice());//放入老美元价格
	    	psiParts.setOldRmbPrice(psiParts.getRmbPrice());//放入老人民币价格
	    	List<Dict> dictPartsTypes=DictUtils.getDictList("parts_type");
		    model.addAttribute("dictPartsTypes", dictPartsTypes);
	    }
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("psiParts", psiParts);
		return "modules/psi/parts/psiPartsForm";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "view")
	public String view(PsiParts psiParts, Model model) {
	    if(psiParts.getId()!=null){
	    	psiParts=this.psiPartsService.get(psiParts.getId());
	    }
		model.addAttribute("psiParts", psiParts);
		return "modules/psi/parts/psiPartsView";
	}
  
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "save")
	public String save(@RequestParam(required= false)MultipartFile imagePeview,@RequestParam("attchmentFile")MultipartFile[] attchmentFiles,PsiParts psiParts, Model model, RedirectAttributes redirectAttributes) {
		psiPartsService.save(psiParts,attchmentFiles,imagePeview);
		addMessage(redirectAttributes, "保存产品配件'" + psiParts.getPartsName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiParts/?repage";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "delete")
	public String delete(Integer id, RedirectAttributes redirectAttributes) {
		psiPartsService.delete(id);
		addMessage(redirectAttributes, "删除产品配件成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiParts/?repage";
	}

	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "isExistName")
	@ResponseBody
	public String isExistName(String partsName,Integer id) {
		return (!psiPartsService.isExistName(partsName,id))+"";
	}
}
