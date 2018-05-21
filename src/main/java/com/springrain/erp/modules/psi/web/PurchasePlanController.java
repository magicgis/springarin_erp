/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PurchasePlan;
import com.springrain.erp.modules.psi.entity.PurchasePlanItem;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PurchasePlanService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 采购订单Controller
 * @author Michael
 * @version 2014-10-29
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/purchasePlan")
public class PurchasePlanController extends BaseController {
	@Autowired
	private 	PurchasePlanService	 		 purchasePlanService;
	@Autowired
	private 	PsiProductService        	 productService;
	@Autowired
	private 	PsiProductEliminateService 	 productEliminateService;
	
	
	@RequiresPermissions("psi:purchasePlan:view")
	@RequestMapping(value = {"list", ""})
	public String list(PurchasePlan purchasePlan, HttpServletRequest request, HttpServletResponse response, Model model,String isCheck,String productName) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today =sdf.parse(sdf.format(new Date()));
		if (purchasePlan.getCreateDate() == null) {
			purchasePlan.setCreateDate(DateUtils.addMonths(today, -3));
			purchasePlan.setReviewDate(today);
		}
		
		Page<PurchasePlan> page =new Page<PurchasePlan>(request, response);
        purchasePlanService.find(page, purchasePlan,isCheck,productName); 
        
        Map<String,String> proColorMap =this.purchasePlanService.getAllProductColors(); 
        model.addAttribute("proColorMap", proColorMap);
        model.addAttribute("purchasePlan", purchasePlan);
        model.addAttribute("page", page);
        model.addAttribute("isCheck", isCheck);
		return "modules/psi/purchasePlanList";
	}
	
	@RequiresPermissions("psi:purchasePlan:edit")
	@RequestMapping(value = "form")
	public String form(PurchasePlan purchasePlan, Model model) {
		if(purchasePlan.getId()!=null){
			purchasePlan = purchasePlanService.get(purchasePlan.getId());
			StringBuilder sb = new StringBuilder("");
			//获取该产品的颜色和国家  组成list，供页面选择
			for(PurchasePlanItem item :purchasePlan.getItems()){
				sb.append(item.getId()+",");
			}
			String itemIds="";
			if(StringUtils.isNotEmpty(sb.toString())){
				itemIds=sb.toString().substring(0,sb.toString().length()-1);
			}
			purchasePlan.setOldItemIds(itemIds);
		}
		
		Map<String,Set<String>> saleMap=productEliminateService.getSaleNewProduct();
		Map<String,String> packInfoMap= this.productService.getProductColorPackInfo();
		model.addAttribute("purchasePlan", purchasePlan);
		model.addAttribute("packInfoMap", JSON.toJSON(packInfoMap));
		model.addAttribute("saleMap", JSON.toJSON(saleMap));
		model.addAttribute("saleInfos", saleMap);
		return "modules/psi/purchasePlanForm";
	}
	
	
	
	
	@RequiresPermissions("psi:purchasePlan:review")
	@RequestMapping(value = "review")
	public String review(PurchasePlan purchasePlan, Model model, RedirectAttributes redirectAttributes) {
		if(purchasePlan.getId()!=null){
			purchasePlan = purchasePlanService.get(purchasePlan.getId());
			if("2".equals(purchasePlan.getPlanSta())){
				Map<String,Set<String>> saleMap=productEliminateService.getSaleNewProduct();
				Map<String,String> packInfoMap= this.productService.getProductColorPackInfo();
				model.addAttribute("purchasePlan", purchasePlan);
				model.addAttribute("packInfoMap", JSON.toJSON(packInfoMap));
				model.addAttribute("saleMap", JSON.toJSON(saleMap));
				model.addAttribute("saleInfos", saleMap);
				return "modules/psi/purchasePlanReview";
			} else {
				addMessage(redirectAttributes, "该采购计划已处理!");
				return "redirect:"+Global.getAdminPath()+"/psi/purchasePlan/?repage";
			}
		}
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePlan/?repage";
	}
	
	
	@RequiresPermissions("psi:purchasePlan:bossReview")
	@RequestMapping(value = "bossReview")
	public String bossReview(PurchasePlan purchasePlan, Model model, RedirectAttributes redirectAttributes) {
		if(purchasePlan.getId()!=null){
			purchasePlan = purchasePlanService.get(purchasePlan.getId());
			if("3".equals(purchasePlan.getPlanSta())){
				Map<String,Set<String>> saleMap=productEliminateService.getSaleNewProduct();
				Map<String,String> packInfoMap= this.productService.getProductColorPackInfo();
				model.addAttribute("purchasePlan", purchasePlan);
				model.addAttribute("packInfoMap", JSON.toJSON(packInfoMap));
				model.addAttribute("saleMap", JSON.toJSON(saleMap));
				model.addAttribute("saleInfos", saleMap);
				return "modules/psi/purchasePlanBossReview";
			} else {
				addMessage(redirectAttributes, "该采购计划已处理!");
				return "redirect:"+Global.getAdminPath()+"/psi/purchasePlan/?repage";
			}
		}
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePlan/?repage";
	}
	


	@RequestMapping(value = "save")
	public String save(PurchasePlan purchasePlan, Model model, RedirectAttributes redirectAttributes) throws Exception {
		if(purchasePlan.getItems()!=null||purchasePlan.getItems().size()>0){
			this.purchasePlanService.editSave(purchasePlan);   
			addMessage(redirectAttributes, "保存新品采购计划成功！"+purchasePlan.getId());
		}
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePlan/?repage";
	}
	
	@RequestMapping(value = "createOrder")
	public String createOrder(String planItemId,String planId,Model model, RedirectAttributes redirectAttributes) throws ParseException{
		//根据选中的产品生成采购订单
		String res=this.purchasePlanService.createOrder(planItemId,planId);
		addMessage(redirectAttributes, res);
		//更新新品下单申请的状态
		this.purchasePlanService.updatePlanSta(planId);
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePlan/?repage";
	}
	
	
	@RequestMapping(value = "reviewSave")
	public String reviewSave(PurchasePlan purchasePlan, Model model, RedirectAttributes redirectAttributes) throws Exception {
		this.purchasePlanService.reviewSave(purchasePlan);   
		addMessage(redirectAttributes, "初级审核采购计划成功！"+purchasePlan.getId());
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePlan/?repage";
	}

	@RequestMapping(value = "bossReviewSave")
	public String bossReviewSave(PurchasePlan purchasePlan, Model model, RedirectAttributes redirectAttributes) throws Exception {
		this.purchasePlanService.bossReviewSave(purchasePlan);   
		addMessage(redirectAttributes, "终极审核采购计划成功！"+purchasePlan.getId());
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePlan/?repage";
	}
	
	@RequiresPermissions("psi:order:view")
	@RequestMapping(value = "view")
	public String view(PurchasePlan purchasePlan, Model model) {
		if(purchasePlan.getId()!=null){
			purchasePlan = purchasePlanService.get(purchasePlan.getId());
		}else{
			return "redirect:"+Global.getAdminPath()+"/psi/purchasePlan/?repage";
		}
		
		model.addAttribute("purchasePlan", purchasePlan);
		return "modules/psi/purchasePlanView";
	}
	

	
	@RequiresPermissions("psi:order:edit")
	@RequestMapping(value = "cancel")
	public String cancle(Integer id, Model model,RedirectAttributes redirectAttributes) {
		PurchasePlan purchasePlan = purchasePlanService.get(id);
		purchasePlan.setPlanSta("6");//已取消
		purchasePlan.setCancelDate(new Date());
		purchasePlan.setCancelUser(UserUtils.getUser());
		this.purchasePlanService.save(purchasePlan);
		addMessage(redirectAttributes, "取消新品采购计划'" + purchasePlan.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePlan/?repage";
	}
	
	
	@RequiresPermissions("psi:order:edit")
	@RequestMapping(value = "toReview")
	public String toReview(Integer id, Model model,RedirectAttributes redirectAttributes) {
		PurchasePlan purchasePlan = purchasePlanService.get(id);
		purchasePlan.setPlanSta("2");//已申请
		this.purchasePlanService.toReviewEmail(purchasePlan);
		this.purchasePlanService.save(purchasePlan);
		addMessage(redirectAttributes, "新品采购计划'" + purchasePlan.getId() + "'申请审核成功");
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePlan/?repage";
	}
	
	@RequestMapping("/download")   
    public ModelAndView download(String fileName,String productName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/");  
        String downLoadPath = ctxPath + fileName;
        String subBuffix = fileName.substring(fileName.lastIndexOf(".")+1);
        try {   
            long fileLength = new File(downLoadPath).length();   
            response.setContentType("application/x-msdownload;");   
            response.setHeader("Content-disposition", "attachment; filename=" + productName+"."+subBuffix);   
            response.setHeader("Content-Length", String.valueOf(fileLength));   
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));   
            bos = new BufferedOutputStream(response.getOutputStream());   
            byte[] buff = new byte[2048];   
            int bytesRead;   
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {   
                bos.write(buff, 0, bytesRead);   
            }   
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            if (bis != null)   
                bis.close();   
            if (bos != null)   
                bos.close();   
        }   
        return null;   
    }
	

}
