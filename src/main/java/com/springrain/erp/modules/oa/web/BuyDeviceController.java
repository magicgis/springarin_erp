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
import com.springrain.erp.modules.oa.entity.BuyDevice;
import com.springrain.erp.modules.oa.service.BuyDeviceService;

/**
 * 
 * @author tim
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/buyDevice")
public class BuyDeviceController extends BaseController {

	@Autowired
	protected BuyDeviceService buyDeviceService;
		
	@ModelAttribute
	public BuyDevice get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return buyDeviceService.get(id);
		}else{
			return new BuyDevice();
		}
	}
	
	@RequiresPermissions("oa:buyDevice:view")
	@RequestMapping(value = {"list",""})
	public String list(BuyDevice buyDevice, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(buyDevice.getCreateDateStart()==null){
			buyDevice.setCreateDateStart(DateUtils.addMonths(today,-1));
			buyDevice.setCreateDateEnd(today);
		}
		Page<BuyDevice> page = buyDeviceService.find(new Page<BuyDevice>(request, response), buyDevice); 
        model.addAttribute("page", page);
		model.addAttribute("buyDevice", buyDevice);
		return "modules/oa/buyDeviceList";
	}

	@RequiresPermissions("oa:buyDevice:view")
	@RequestMapping(value = "form")
	public String form(BuyDevice buyDevice, Model model) {
		model.addAttribute("buyDevice", buyDevice);
		return "modules/oa/buyDeviceForm";
	}
	

	@RequiresPermissions("oa:buyDevice:view")
	@RequestMapping(value = "save")
	public String save(BuyDevice buyDevice, Model model, RedirectAttributes redirectAttributes) {
		buyDeviceService.save(buyDevice);
		addMessage(redirectAttributes, "保存采购申请成功");
		return "redirect:"+Global.getAdminPath()+"/oa/buyDevice/";
	}

	@RequiresPermissions("oa:buyDevice:view")
	@RequestMapping(value = {"list/task"})
	public String listTask(BuyDevice buyDevice, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(buyDevice.getCreateDateStart()==null){
			buyDevice.setCreateDateStart(DateUtils.addMonths(today,-1));
			buyDevice.setCreateDateEnd(today);
		}
		Page<BuyDevice> page = buyDeviceService.findTodoTasks(new Page<BuyDevice>(request, response), buyDevice); 
        model.addAttribute("page", page);
		model.addAttribute("buyDevice", buyDevice);
		return "modules/oa/buyDeviceTask";
	}
	
	@RequiresPermissions("oa:buyDevice:view")
	@RequestMapping(value = "detail")
	public String detail(String state,BuyDevice buyDevice, Model model ,RedirectAttributes redirectAttributes) {
		if(!StringUtils.isEmpty(state)){
			try {
				state = URLDecoder.decode(state,"utf-8");
			} catch (UnsupportedEncodingException e) {}
			if(!state.equals(buyDevice.getProcessStatus())){
				addMessage(redirectAttributes, "审批状态已经被变化！请确定流程状态");
				return "redirect:"+Global.getAdminPath()+"/oa/buyDevice/";
			}
		}
		model.addAttribute("buyDevice", buyDevice);
		model.addAttribute("workflowEntity",WorkflowUtils.getWorkflowEntity(buyDevice.getProcessInstanceId()));
		return "modules/oa/buyDeviceDetail";
	}

	@RequiresPermissions("oa:leave:deptLeaderAudit")
	@RequestMapping(value = "deptLeaderAudit")
	public String deptLeaderAudit(BuyDevice buyDevice, RedirectAttributes redirectAttributes) {
		try {
			buyDeviceService.deptLeaderAudit(buyDevice);
			addMessage(redirectAttributes, "办公设备采购办公设备采购审批成功");
		} catch (Exception e) {
			if(e.getMessage().contains("suspended task")){
				addMessage(redirectAttributes, "该流程已经挂起，暂时不能审批！");
			}else{
				addMessage(redirectAttributes, "审批状态已经被变化！请确定流程状态");
			}
		}
		return "redirect:"+Global.getAdminPath()+"/oa/buyDevice/";
	}
	
	@RequiresPermissions("oa:leave:mgAudit")
	@RequestMapping(value = "mgrAudit")
	public String mgAudit(BuyDevice buyDevice, RedirectAttributes redirectAttributes) {
		try {
			buyDeviceService.mgAudit(buyDevice);
			addMessage(redirectAttributes, "办公设备采购审批成功");
		} catch (Exception e) {
			addMessage(redirectAttributes, "审批状态已经被变化！请确定流程状态");
		}
		return "redirect:"+Global.getAdminPath()+"/oa/buyDevice/";
	}

	@RequiresPermissions("oa:buyDevice:view")
	@RequestMapping(value = "modifyApply")
	public String modifyApply(BuyDevice buyDevice, RedirectAttributes redirectAttributes) {
		buyDeviceService.modifyApply(buyDevice);
		addMessage(redirectAttributes, "办公设备采购调整成功");
		return "redirect:"+Global.getAdminPath()+"/oa/buyDevice/";
	}
	
	@RequiresPermissions("oa:buyDevice:view")
	@RequestMapping(value = "getMoney")
	public String getMoney(BuyDevice buyDevice, RedirectAttributes redirectAttributes) {
		buyDeviceService.getMoney(buyDevice);
		addMessage(redirectAttributes, "办公设备采购财务费用已支付成功");
		return "redirect:"+Global.getAdminPath()+"/oa/buyDevice/";
	}

	@RequiresPermissions("oa:buyDevice:view")
	@RequestMapping(value = "report")
	public String report(BuyDevice buyDevice, RedirectAttributes redirectAttributes) {
		buyDeviceService.report(buyDevice);
		addMessage(redirectAttributes, "办公设备已登记成功");
		return "redirect:"+Global.getAdminPath()+"/oa/buyDevice/";
	}
	
}
