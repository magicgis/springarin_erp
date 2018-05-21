package com.springrain.erp.modules.oa.web;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.common.workflow.WorkflowUtils;
import com.springrain.erp.modules.oa.entity.Leave;
import com.springrain.erp.modules.oa.service.LeaveService;

/**
 * 
 * @author tim
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/leave")
public class LeaveController extends BaseController {

	@Autowired
	protected LeaveService leaveService;
		
	@ModelAttribute
	public Leave get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return leaveService.get(id);
		}else{
			return new Leave();
		}
	}
	
	@RequiresPermissions("oa:leave:view")
	@RequestMapping(value = {"list",""})
	public String list(Leave leave, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(leave.getCreateDateStart()==null){
			leave.setCreateDateStart(DateUtils.addMonths(today,-1));
			leave.setCreateDateEnd(today);
		}
		Page<Leave> page = leaveService.find(new Page<Leave>(request, response), leave, false); 
        model.addAttribute("page", page);
		model.addAttribute("leave", leave);
		return "modules/oa/leaveList";
	}

	@RequiresPermissions("oa:leave:view")
	@RequestMapping(value = "form")
	public String form(Leave leave, Model model) {
		model.addAttribute("leave", leave);
		return "modules/oa/leaveForm";
	}
	

	@RequiresPermissions("oa:leave:view")
	@RequestMapping(value = "save")
	public String save(Leave leave, Model model, RedirectAttributes redirectAttributes) {
		leaveService.save(leave);
		addMessage(redirectAttributes, "保存请假成功");
		return "redirect:"+Global.getAdminPath()+"/oa/leave/";
	}

	@RequiresPermissions("oa:leave:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		leaveService.delete(id);
		addMessage(redirectAttributes, "删除请假成功");
		return "redirect:"+Global.getAdminPath()+"/oa/leave/";
	}

	@RequiresPermissions("oa:leave:view")
	@RequestMapping(value = {"list/task"})
	public String listTask(Leave leave, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if(leave.getCreateDateStart()==null){
			leave.setCreateDateStart(DateUtils.addMonths(today,-1));
			leave.setCreateDateEnd(today);
		}
		Page<Leave> page = leaveService.findTodoTasks(new Page<Leave>(request, response), leave); 
        model.addAttribute("page", page);
		model.addAttribute("leave", leave);
		return "modules/oa/leaveTask";
	}
	
	@RequiresPermissions("oa:leave:view")
	@RequestMapping(value = "detail")
	public String detail(String state,Leave leave, Model model ,RedirectAttributes redirectAttributes) {
		if(!StringUtils.isEmpty(state)){
			try {
				state = URLDecoder.decode(state,"utf-8");
			} catch (UnsupportedEncodingException e) {}
			if(!state.equals(leave.getProcessStatus())){
				addMessage(redirectAttributes, "审批状态已经被变化！请确定流程状态");
				return "redirect:"+Global.getAdminPath()+"/oa/leave/";
			}
		}
		model.addAttribute("leave", leave);
		model.addAttribute("workflowEntity",WorkflowUtils.getWorkflowEntity(leave.getProcessInstanceId()));
		return "modules/oa/leaveDetail";
	}

	@RequiresPermissions("oa:leave:deptLeaderAudit")
	@RequestMapping(value = "deptLeaderAudit")
	public String deptLeaderAudit(Leave leave, RedirectAttributes redirectAttributes) {
		try {
			leaveService.deptLeaderAudit(leave);
			addMessage(redirectAttributes, "请假审批成功");
		} catch (Exception e) {
			if(e.getMessage() != null && e.getMessage().contains("suspended task")){
				addMessage(redirectAttributes, "该流程已经挂起，暂时不能审批！");
			}else{
				addMessage(redirectAttributes, "审批状态已经被变化！请确定流程状态");
				logger.error("部门管理员审批异常", e);
			}
		}
		return "redirect:"+Global.getAdminPath()+"/oa/leave/";
	}

	@RequiresPermissions("oa:leave:hrAudit")
	@RequestMapping(value = "hrAudit")
	public String hrAudit(Leave leave, RedirectAttributes redirectAttributes) {
		try {
			leaveService.hrAudit(leave);
			addMessage(redirectAttributes, "请假审批成功");
		} catch (Exception e) {
			addMessage(redirectAttributes, "审批状态已经被变化！请确定流程状态");
		}
		return "redirect:"+Global.getAdminPath()+"/oa/leave/";
	}
	
	@RequiresPermissions("oa:leave:mgAudit")
	@RequestMapping(value = "mgrAudit")
	public String mgAudit(Leave leave, RedirectAttributes redirectAttributes) {
		try {
			leaveService.mgAudit(leave);
			addMessage(redirectAttributes, "请假审批成功");
		} catch (Exception e) {
			addMessage(redirectAttributes, "审批状态已经被变化！请确定流程状态");
			logger.error("总经理审批异常", e);
		}
		return "redirect:"+Global.getAdminPath()+"/oa/leave/";
	}

	@RequiresPermissions("oa:leave:edit")
	@RequestMapping(value = "modifyApply")
	public String modifyApply(Leave leave, RedirectAttributes redirectAttributes) {
		leaveService.modifyApply(leave);
		addMessage(redirectAttributes, "请假调整成功");
		return "redirect:"+Global.getAdminPath()+"/oa/leave/";
	}
	
	@RequiresPermissions("oa:leave:edit")
	@RequestMapping(value = "reportBack")
	public String reportBack(Leave leave, RedirectAttributes redirectAttributes) {
		leaveService.reportBack(leave);
		addMessage(redirectAttributes, "请假销假成功");
		return "redirect:"+Global.getAdminPath()+"/oa/leave/";
	}
	
	//导出请假明细
	@RequestMapping(value = "exportDetail")
	public String exportProductDetail(Leave leave, HttpServletRequest request,HttpServletResponse response, Model model) {
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		row.setHeight((short) 600);

		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setWrapText(true);

		CellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		contentStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		contentStyle1.setWrapText(true);
		HSSFCell cell = null;

		Page<Leave> page = new Page<Leave>(request, response);
		page.setPageSize(10000);
		leave.setProcessStatus("已完成");
		page = leaveService.find(new Page<Leave>(request, response), leave, true);
		List<Leave> list = page.getList();
		List<String> title = Lists.newArrayList("员工名称", "提交申请时间", "请假开始时间", "请假结束时间", "请假天数", "实际开始时间", "实际结束时间", "实际请假天数", "请假类型");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		List<Integer> index = new ArrayList<Integer>();
		int rowIndex = 1;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < list.size(); i++) {
			Leave leaves = list.get(i);
			int j = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			//标记申请信息跟销假信息不一致的信息
			if(!leaves.getApplyDay().equals(leaves.getRealityDay())
					|| !leaves.getApplyHour().equals(leaves.getRealityHour())
					|| !leaves.getStartTime().equals(leaves.getRealityStartTime())
					|| !leaves.getEndTime().equals(leaves.getRealityEndTime())){
				index.add(rowIndex - 1);
			}
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(leaves.getCreateBy().getName());
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(format.format(leaves.getCreateDate()));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(format.format(leaves.getStartTime()));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(format.format(leaves.getEndTime()));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(leaves.getApplyDay()+ "天" + leaves.getApplyHour() + "小时");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(format.format(leaves.getRealityStartTime()));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(format.format(leaves.getRealityEndTime()));
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(leaves.getRealityDay()+ "天" + leaves.getRealityHour() + "小时");
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(leaves.getLeaveTypeDictLabel());
		}

		for (int i = 0; i < rowIndex - 1; i++) {
			for (int j = 0; j < title.size(); j++) {
				if (index.contains(i + 1)) {	//标记请假时间有变更的信息
					sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle1);
				} else {
					sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
				}
			}
		}
		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			String fileName = "员工请假统计报表(" + (sdf.format(leave.getCreateDateStart()) + "-" +sdf.format(leave.getCreateDateEnd())) + ").xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("请假数据导出异常", e);
		}
		return null;
	}

}
