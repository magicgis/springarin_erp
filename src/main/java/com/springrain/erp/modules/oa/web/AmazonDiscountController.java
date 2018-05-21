package com.springrain.erp.modules.oa.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.common.workflow.WorkflowUtils;
import com.springrain.erp.modules.oa.entity.AmazonDiscount;
import com.springrain.erp.modules.oa.service.AmazonDiscountService;

/**
 * 
 * @author tim
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/amazonDiscount")
public class AmazonDiscountController extends BaseController {

	@Autowired
	protected AmazonDiscountService amazonDiscountService;
		
	@ModelAttribute
	public AmazonDiscount get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return amazonDiscountService.get(id);
		}else{
			return new AmazonDiscount();
		}
	}
	
	@RequiresPermissions("oa:amazonDiscount:view")
	@RequestMapping(value = {"list",""})
	public String list(AmazonDiscount amazonDiscount, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(amazonDiscount.getCreateDateStart()==null){
			amazonDiscount.setCreateDateStart(DateUtils.addMonths(today,-1));
			amazonDiscount.setCreateDateEnd(today);
		}
		Page<AmazonDiscount> page = amazonDiscountService.find(new Page<AmazonDiscount>(request, response), amazonDiscount); 
        model.addAttribute("page", page);
		model.addAttribute("amazonDiscount", amazonDiscount);
		return "modules/oa/amazonDiscountList";
	}

	@RequiresPermissions("oa:amazonDiscount:view")
	@RequestMapping(value = "form")
	public String form(AmazonDiscount amazonDiscount, Model model) {
		model.addAttribute("amazonDiscount", amazonDiscount);
		return "modules/oa/amazonDiscountForm";
	}
	

	@RequiresPermissions("oa:amazonDiscount:view")
	@RequestMapping(value = "save")
	public String save(AmazonDiscount amazonDiscount, Model model, RedirectAttributes redirectAttributes) {
		amazonDiscountService.save(amazonDiscount);
		addMessage(redirectAttributes, "保存打折申请成功");
		return "redirect:"+Global.getAdminPath()+"/oa/amazonDiscount/";
	}

	@RequiresPermissions("oa:amazonDiscount:view")
	@RequestMapping(value = {"list/task"})
	public String listTask(AmazonDiscount amazonDiscount, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(amazonDiscount.getCreateDateStart()==null){
			amazonDiscount.setCreateDateStart(DateUtils.addMonths(today,-1));
			amazonDiscount.setCreateDateEnd(today);
		}
		Page<AmazonDiscount> page = amazonDiscountService.findTodoTasks(new Page<AmazonDiscount>(request, response), amazonDiscount); 
        model.addAttribute("page", page);
		model.addAttribute("amazonDiscount", amazonDiscount);
		return "modules/oa/amazonDiscountTask";
	}
	
	@RequiresPermissions("oa:amazonDiscount:view")
	@RequestMapping(value = "detail")
	public String detail(String state,AmazonDiscount amazonDiscount, Model model ,RedirectAttributes redirectAttributes) {
		if(!StringUtils.isEmpty(state)){
			try {
				state = URLDecoder.decode(state,"utf-8");
			} catch (UnsupportedEncodingException e) {}
			if(!state.equals(amazonDiscount.getProcessStatus())){
				addMessage(redirectAttributes, "审批状态已经被变化！请确定流程状态");
				return "redirect:"+Global.getAdminPath()+"/oa/amazonDiscount/";
			}
		}
		model.addAttribute("amazonDiscount", amazonDiscount);
		model.addAttribute("workflowEntity",WorkflowUtils.getWorkflowEntity(amazonDiscount.getProcessInstanceId()));
		return "modules/oa/amazonDiscountDetail";
	}

	@RequiresPermissions("oa:leave:deptLeaderAudit")
	@RequestMapping(value = "deptLeaderAudit")
	public String deptLeaderAudit(AmazonDiscount amazonDiscount, RedirectAttributes redirectAttributes) {
		try {
			amazonDiscountService.deptLeaderAudit(amazonDiscount);
			addMessage(redirectAttributes, "部门主管审批成功");
		} catch (Exception e) {
			if(e.getMessage().contains("suspended task")){
				addMessage(redirectAttributes, "该流程已经挂起，暂时不能审批！");
			}else{
				addMessage(redirectAttributes, "审批状态已经被变化！请确定流程状态");
			}
		}
		return "redirect:"+Global.getAdminPath()+"/oa/amazonDiscount/";
	}
	
	@RequiresPermissions("oa:leave:mgAudit")
	@RequestMapping(value = "mgrAudit")
	public String mgAudit(AmazonDiscount amazonDiscount, RedirectAttributes redirectAttributes) {
		try {
			amazonDiscountService.mgAudit(amazonDiscount);
			addMessage(redirectAttributes, "总经理审批成功");
		} catch (Exception e) {
			addMessage(redirectAttributes, "审批状态已经被变化！请确定流程状态");
		}
		return "redirect:"+Global.getAdminPath()+"/oa/amazonDiscount/";
	}

	@RequiresPermissions("oa:amazonDiscount:view")
	@RequestMapping(value = "modifyApply")
	public String modifyApply(AmazonDiscount amazonDiscount, RedirectAttributes redirectAttributes) {
		amazonDiscountService.modifyApply(amazonDiscount);
		addMessage(redirectAttributes, "个人调整成功");
		return "redirect:"+Global.getAdminPath()+"/oa/amazonDiscount/";
	}
	
	@RequiresPermissions("oa:amazonDiscount:view")
	@RequestMapping(value = "excute")
	public String getMoney(AmazonDiscount amazonDiscount, RedirectAttributes redirectAttributes) {
		amazonDiscountService.excute(amazonDiscount);
		addMessage(redirectAttributes, "个人已执行打折!");
		return "redirect:"+Global.getAdminPath()+"/oa/amazonDiscount/";
	}

	@RequiresPermissions("oa:amazonDiscount:view")
	@RequestMapping(value = "report")
	public String report(AmazonDiscount amazonDiscount, RedirectAttributes redirectAttributes) {
		amazonDiscountService.report(amazonDiscount);
		addMessage(redirectAttributes, "主管已确认成功");
		return "redirect:"+Global.getAdminPath()+"/oa/amazonDiscount/";
	}
	
}
