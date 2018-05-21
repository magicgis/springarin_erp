/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiProductGroupCustomer;
import com.springrain.erp.modules.psi.entity.PsiProductGroupCustomerEmail;
import com.springrain.erp.modules.psi.entity.PsiProductGroupPhoto;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;


/**
 * 产品类型分组Controller
 * @author 
 * @version 2015-06-11
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiProductManageGroup")
public class PsiProductManageGroupController extends BaseController {
	@Autowired
	private PsiProductTypeGroupDictService psiTypeGroupService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private PsiProductGroupUserService 	    groupUserService;
	
	/**
	 *进入产品类型编辑页面 
	 */
	@RequestMapping(value = "psiProductManageEdit")
	public String getAllGroupList(HttpServletRequest request, HttpServletResponse response, Model model){
		/*model.addAttribute("typeList",systemService.hasPerssion("psi:product:manager"));
		List<PsiProductTypeGroupDict> typeGroupList= psiTypeGroupService.getAllManageGroupList();
		List<PsiProductTypeGroupDict> typeProductList= psiTypeGroupService.getAllGroupTypeProductList();
		model.addAttribute("typeGroupList", typeGroupList);
		model.addAttribute("typeProductList", typeProductList);
		psiTypeGroupService.updateManageRelation();*/
		Map<String,String>  managerMap=groupUserService.findGroupManager();
		List<PsiProductTypeGroupDict>  lineList = psiTypeGroupService.getAllList();
		List<User> userList = systemService.findUserByPermission("psi:product:manager");
		if(userList!=null){
			model.addAttribute("users", userList);
		}
		model.addAttribute("lineList", lineList);
		model.addAttribute("managerMap", managerMap);
		return "modules/psi/psiProductManageTypeEdit";
	}
	
	
	/**
	 *更新产品类型、产品经理，关系 
	 */
	@RequestMapping(value = "psiProductUpdateAll")
	public String psiProductUpdateAll(PsiProductTypeGroupDict dictType,String selectNodes, RedirectAttributes redirectAttributes, Model model) {
		String[] dict_ids=selectNodes.split(",");
		String parent_id=dictType.getParent().getId();
		boolean flag=psiTypeGroupService.insertManageRelation(dict_ids, parent_id);
		if(flag){
			addMessage(redirectAttributes, "更新成功");
		}else{
			addMessage(redirectAttributes, "更新失败");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiProductManageGroup/psiProductManageEdit";
	}
	
	
	/**
	 *进入产品类型编辑页面 
	 */
	@RequestMapping(value = "psiProductPurchaseEdit")
	public String getPurchaseManagerTypeEdit(HttpServletRequest request, HttpServletResponse response, Model model){
		//采购经理列表
		model.addAttribute("typeList", systemService.findUserByPermission("psi:purchase:manager"));
		List<PsiProductTypeGroupDict> typeGroupList= psiTypeGroupService.getAllPurchaseGroupList();
		List<PsiProductTypeGroupDict> typeProductList= psiTypeGroupService.getAllGroupTypeProductList();
		model.addAttribute("typeGroupList", typeGroupList);
		model.addAttribute("typeProductList", typeProductList);
		psiTypeGroupService.updateManageRelation();
		return "modules/psi/psiProductPurchaseTypeEdit";
	}
	
	
	/**
	 *更新产品类型、产品经理，关系 
	 */
	@RequestMapping(value = "psiProductPurchaseSave")
	public String savePurchaseManagerType(PsiProductTypeGroupDict dictType,String selectNodes, RedirectAttributes redirectAttributes, Model model) {
		String[] dict_ids=selectNodes.split(",");
		String parent_id=dictType.getParent().getId();
		boolean flag=psiTypeGroupService.insertPurchaseRelation(dict_ids, parent_id);
		if(flag){
			addMessage(redirectAttributes, "更新成功");
		}else{
			addMessage(redirectAttributes, "更新失败");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiProductManageGroup/psiProductPurchaseEdit";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "isUpdateGroupNode")
	public String isGroupNode(PsiProductTypeGroupDict dictType,String updateIds, Model model) {
		String[] allId=updateIds.split(",");
		for (String id : allId) {
			if(psiTypeGroupService.get(id)!=null){
				return "0";
			}
		}
		return "1";
	}
	
	

	@RequestMapping(value = "findGroupCustomer")
	public String findGroupCustomer(HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String,Map<String,List<PsiProductGroupCustomer>>> customerMap=groupUserService.findAllGroupCustomer();
		List<PsiProductTypeGroupDict>  lineList = psiTypeGroupService.getAllList();
//		Map<String,List<User>> roleMap=systemService.findRoleByMatchName("事件客服");
		Map<String,List<User>> roleMap=systemService.findUserByPermissionName("event:service:");
		model.addAttribute("roleMap", roleMap);
		model.addAttribute("lineList", lineList);
		model.addAttribute("customerMap", customerMap);
		return "modules/psi/psiProductGroupCustomerList";
	}
	
	
	@RequestMapping(value = "findGroupPhoto")
	public String findGroupPhoto(HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String,PsiProductGroupPhoto> photoMap=groupUserService.findAllGroupPhoto();
		List<PsiProductTypeGroupDict>  lineList = psiTypeGroupService.getAllList();
		List<User> userList = systemService.findUserByPermission("cms:ckfinder:upload");
		if(userList!=null){
			model.addAttribute("users", userList);
		}
		model.addAttribute("lineList", lineList);
		model.addAttribute("photoMap", photoMap);
		return "modules/psi/psiProductGroupPhotoList";
	}
	
	@RequestMapping(value = "findGroupCustomerEmail")
	public String findGroupCustomerEmail(HttpServletRequest request, HttpServletResponse response, Model model){
		Map<String,Map<String,List<PsiProductGroupCustomerEmail>>> customerMap=groupUserService.findAllGroupCustomerEmail();
		List<PsiProductTypeGroupDict>  lineList = psiTypeGroupService.getAllList();
//		Map<String,List<User>> roleMap=systemService.findRoleByMatchRemarks("custom");
		Map<String,List<User>> roleMap=systemService.findUserByPermissionName("custom:service:");
		model.addAttribute("roleMap", roleMap);
		model.addAttribute("lineList", lineList);
		model.addAttribute("customerMap", customerMap);
		return "modules/psi/psiProductGroupCustomerEmailList";
	}
	
	@ResponseBody
	@RequestMapping(value = "seveGroupCustomer")
	public Integer seveGroupCustomer(Integer id,String country,String[] userId,String lineId,HttpServletRequest request, HttpServletResponse response, Model model){
		StringBuilder userIdStr=new StringBuilder();
		for (int m = 0; m < userId.length; m++) {
			try {
				userId[m] = URLDecoder.decode(userId[m],"utf-8");
				if(m==userId.length-1){
					userIdStr.append(userId[m]);
				}else{
					userIdStr.append(userId[m]).append(",");
				}
			} catch (UnsupportedEncodingException e) {}
		}
		if("null".equals(userIdStr.toString())){
			PsiProductGroupCustomer customer=new PsiProductGroupCustomer(id,lineId,userIdStr.toString(),UserUtils.getUser(),new Date(),country,"1");
			groupUserService.save(customer);
			return customer.getId();
		}else{
			PsiProductGroupCustomer customer=new PsiProductGroupCustomer(id,lineId,userIdStr.toString(),UserUtils.getUser(),new Date(),country,"0");
			groupUserService.save(customer);
			groupUserService.delete(country,lineId,Sets.newHashSet(userId)); 
			return customer.getId();
		}
		
	}
	
	
	@ResponseBody
	@RequestMapping(value = "seveGroupCustomerEmail")
	public Integer seveGroupCustomerEmail(Integer id,String country,String[] userId,String lineId,HttpServletRequest request, HttpServletResponse response, Model model){
		StringBuilder userIdStr=new StringBuilder();
		for (int m = 0; m < userId.length; m++) {
			try {
				userId[m] = URLDecoder.decode(userId[m],"utf-8");
				if(m==userId.length-1){
					userIdStr.append(userId[m]);
				}else{
					userIdStr.append(userId[m]).append(",");
				}
			} catch (UnsupportedEncodingException e) {}
		}
		if("null".equals(userIdStr.toString())){
			PsiProductGroupCustomerEmail customer=new PsiProductGroupCustomerEmail(id,lineId,userIdStr.toString(),UserUtils.getUser(),new Date(),country,"1");
			groupUserService.saveCustomerEmail(customer);
			return customer.getId();
		}else{
			PsiProductGroupCustomerEmail customer=new PsiProductGroupCustomerEmail(id,lineId,userIdStr.toString(),UserUtils.getUser(),new Date(),country,"0");
			groupUserService.saveCustomerEmail(customer);
			groupUserService.deleteEmail(country,lineId,Sets.newHashSet(userId)); 
			return customer.getId();
		}
		
	}
	
	@ResponseBody
	@RequestMapping(value = "saveGroupPhoto")
	public Integer saveGroupPhoto(Integer id,String country,String[] userId,String lineId,HttpServletRequest request, HttpServletResponse response, Model model){
		StringBuilder userIdStr=new StringBuilder();
		for (int m = 0; m < userId.length; m++) {
			try {
				userId[m] = URLDecoder.decode(userId[m],"utf-8");
				if(m==userId.length-1){
					userIdStr.append(userId[m]);
				}else{
					userIdStr.append(userId[m]).append(",");
				}
			} catch (UnsupportedEncodingException e) {}
		}
		if("null".equals(userIdStr.toString())){
			PsiProductGroupPhoto photo=new PsiProductGroupPhoto(id,lineId,userIdStr.toString(),UserUtils.getUser(),new Date(),"1");
			groupUserService.savePhoto(photo);
			return photo.getId();
		}else{
			PsiProductGroupPhoto photo=new PsiProductGroupPhoto(id,lineId,userIdStr.toString(),UserUtils.getUser(),new Date(),"0");
			groupUserService.savePhoto(photo);
			return photo.getId();
		}
		
	}
	
	
	@ResponseBody
	@RequestMapping(value = "saveGroupManager")
	public Integer saveGroupManager(String[] userId,String lineId,HttpServletRequest request, HttpServletResponse response, Model model){
		StringBuilder userIdStr=new StringBuilder();
		for (int m = 0; m < userId.length; m++) {
			try {
				userId[m] = URLDecoder.decode(userId[m],"utf-8");
				if(m==userId.length-1){
					userIdStr.append(userId[m]);
				}else{
					userIdStr.append(userId[m]).append(",");
				}
			} catch (UnsupportedEncodingException e) {}
		}
		if("null".equals(userIdStr.toString())){
			psiTypeGroupService.deleteManageRelation(lineId);
			return 0;
		}else{
			psiTypeGroupService.insertManageRelation(lineId,userIdStr.toString());
			return 0;
		}
	}
}
