/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseTypeGoalService;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.UserUtils;


/**
 * 产品类型分组Controller
 * @author 
 * @version 2015-06-11
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiProductTypeGroupDict")
public class PsiProductTypeGroupDictController extends BaseController {

	@Autowired
	private PsiProductTypeGroupDictService psiTypeGroupService;

	@Autowired
	private EnterpriseTypeGoalService typeGoalService;
	
	@ModelAttribute("dictType")
	public PsiProductTypeGroupDict get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return psiTypeGroupService.get(id);
		}else{
			return new PsiProductTypeGroupDict();
		}
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
	
	@ResponseBody
	@RequestMapping(value = "isDeleteGroupNode")
	public String isDeleteGroupNode(PsiProductTypeGroupDict dictType,String deleteIds, Model model) {
		String[] allId=deleteIds.split(",");
		for (String id : allId) {
			if(psiTypeGroupService.get(id)==null){
				return "0";
			}
		}
		return "1";
	}
	
	@RequestMapping(value = "deleteNode")
	public String deleteNode(PsiProductTypeGroupDict dictType,String delIds, Model model,RedirectAttributes redirectAttributes) {
		String[] ids=delIds.split(",");
		boolean flag=psiTypeGroupService.deleteNode(ids);
		if(flag){
			addMessage(redirectAttributes, "删除成功");
		}else{
			addMessage(redirectAttributes, "删除失败");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiProductTypeGroupDict/psiProductTypeEdit";
	}
	
	@RequestMapping(value = "psiProductUpdateAll")
	public String psiProductUpdateAll(PsiProductTypeGroupDict dictType,String selectNodes, RedirectAttributes redirectAttributes, Model model) {
		String[] dict_ids=selectNodes.split(",");
		String parent_id=dictType.getParent().getId();
		boolean flag=psiTypeGroupService.insertRelation(dict_ids, parent_id);
		if(flag){
	    	//更新分类型目标本月的产品线关系
    		SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
    		Date date = new Date();
    		for (int i = 0; i < 3; i++) {
        		String month = monthFormat.format(DateUtils.addMonths(date, i));
    	    	typeGoalService.updateLine(month, psiTypeGroupService.getTypeLine(null));
			}
			addMessage(redirectAttributes, "更新成功");
		}else{
			addMessage(redirectAttributes, "更新失败");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiProductTypeGroupDict/psiProductTypeEdit";
	}
	
	@RequestMapping(value = "psiProductTypeForm")
	public String list(PsiProductTypeGroupDict dictType, HttpServletRequest request, HttpServletResponse response, Model model) {
		//List<PsiProductTypeGroupDict> typeGroupList= psiTypeGroupService.getAllList();
		return "modules/psi/psiProductTypeGroupAdd";
	}
	
	@ResponseBody
	@RequestMapping(value = "isExistName")
	public String isExistName(PsiProductTypeGroupDict dictType,Model model) {
		if(psiTypeGroupService.isExistName(dictType.getName())){
				return "1";//不存在
			}
		return "0";
	}
	
	@ResponseBody
	@RequestMapping(value = "getAllGroupType")
	public List<PsiProductTypeGroupDict> getAllGroupType(PsiProductTypeGroupDict dictType,Model model) {
		List<PsiProductTypeGroupDict> groupType =psiTypeGroupService.getAllList();
		return groupType;
	}
	
	@ResponseBody
	@RequestMapping(value = "getProductType")
	public List<Dict> getProductType(String groupId,Model model) {
		if("unGrouped".equals(groupId)){
			 List<Dict> dict=psiTypeGroupService.getProductUnGroupedType();
			 return dict;
		}else{
			 List<Dict> dict=psiTypeGroupService.getProductType(groupId);
			 return dict;
		}
	}
	
	@RequestMapping(value = "psiProductTypeGroupAdd")
	public String psiProductTypeGroupAdd(PsiProductTypeGroupDict dictType, String name,RedirectAttributes redirectAttributes, Model model) {
		if(StringUtils.isNotEmpty(dictType.getId())){
			dictType=psiTypeGroupService.get(dictType.getId());
			dictType.setName(name);
		}
		dictType.setCreateTime(new Date());
		dictType.setUser(UserUtils.getUser());
		dictType.setDelFlag("0");
		if(dictType.getParent()==null||StringUtils.isBlank(dictType.getParent().getId())){
			PsiProductTypeGroupDict dictParentType=new PsiProductTypeGroupDict();
			dictParentType.setId("0");
			dictType.setParent(dictParentType);
		}
		psiTypeGroupService.save(dictType);
		addMessage(redirectAttributes, (StringUtils.isNotEmpty(dictType.getId())?"修改":"保存") + dictType.getName() + "成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiProductTypeGroupDict/psiProductTypeEdit";
	}
	
	@RequestMapping(value = "psiProductTypeEdit")
	public String getAllGroupList(String type,HttpServletRequest request, HttpServletResponse response, Model model){
		if(StringUtils.isBlank(type)){
			type="1";
		}
		List<PsiProductTypeGroupDict> typeList= psiTypeGroupService.getAllList();
		List<PsiProductTypeGroupDict> typeGroupList= psiTypeGroupService.getAllGroupList();
		List<PsiProductTypeGroupDict> typeProductList= psiTypeGroupService.getAllGroupTypeProductList(type);
		model.addAttribute("typeGroupList", typeGroupList);
		model.addAttribute("typeList", typeList);
		model.addAttribute("type", type);
		model.addAttribute("typeProductList", typeProductList);
		return "modules/psi/psiProductTypeEdit";
	}
	
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<PsiProductTypeGroupDict> list= psiTypeGroupService.getAllList();
		for (int i=0; i<list.size(); i++){
			PsiProductTypeGroupDict e = list.get(i);
			if (extId == null || (extId!=null && !extId.equals(e.getId()))){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent()!=null?e.getParent().getId():0);
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
	
	 private List<String> recursionFn(List<PsiProductTypeGroupDict> list, PsiProductTypeGroupDict node) {
	        List<PsiProductTypeGroupDict> childList = getChildList(list, node);
	        List<String> returnList = new ArrayList<String>();
	        if (hasChild(list, node)) {
	        	returnList.add(node.getId());
	            Iterator<PsiProductTypeGroupDict> it = childList.iterator();
	            while (it.hasNext()) {
	            	PsiProductTypeGroupDict n = (PsiProductTypeGroupDict) it.next();
	                recursionFn(list, n);
	            }
	        } else {
	            returnList.add(node.getId());
	        }
	        return returnList;
	    }
	
    private boolean hasChild(List<PsiProductTypeGroupDict> list, PsiProductTypeGroupDict node) {
        return getChildList(list, node).size() > 0 ? true : false;
    }
    
    private List<PsiProductTypeGroupDict> getChildList(List<PsiProductTypeGroupDict> list, PsiProductTypeGroupDict node) {
        List<PsiProductTypeGroupDict> nodeList = new ArrayList<PsiProductTypeGroupDict>();
        Iterator<PsiProductTypeGroupDict> it = list.iterator();
        while (it.hasNext()) {
        	PsiProductTypeGroupDict n = (PsiProductTypeGroupDict) it.next();
            if (n.getParent()!=null&&n.getParent().getId().equals(node.getId())) {
                nodeList.add(n);
            }
        }
        return nodeList;
    }
}
