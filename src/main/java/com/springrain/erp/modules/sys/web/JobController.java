package com.springrain.erp.modules.sys.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.endpoint.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.task.ScheduleJob;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.sys.service.JobService;

/**
 * 任务Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/job")
public class JobController extends BaseController {

	@Autowired
	private JobService jobService;
	
	@Autowired
	AmazonAccountConfigService configService;
	
	/**
	 * 客户端webservice接口地址
	 */
	private static String url = "http://host/springrain-client/cxf/timeTask?wsdl";
	private static String key = Global.getConfig("ws.key");

	@RequestMapping(value = {"list", ""})
	public String list(HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			List<ScheduleJob> list = jobService.getAllJob();
	        model.addAttribute("list", list);
	        List<AmazonAccountConfig> wsConfigs = configService.findWsInfo();
	        model.addAttribute("wsConfigs", wsConfigs);
		} catch (Exception e) {
			logger.error("查询任务异常", e);
		}
		return "modules/sys/taskList";
	}

	@RequestMapping(value = {"changeStatus"})
	public String changeStatus(String action, String jobName, String jobGroup, Model model, RedirectAttributes redirectAttributes) {
		try {
			ScheduleJob scheduleJob = new ScheduleJob();
			scheduleJob.setJobName(jobName);
			scheduleJob.setJobGroup(jobGroup);
			if ("start".equals(action)) {
				jobService.resumeJob(scheduleJob);
			} else if ("stop".equals(action)) {
				jobService.pauseJob(scheduleJob);
			} else if ("delete".equals(action)) {
				jobService.deleteJob(scheduleJob);
			} else if ("run".equals(action)) {
				jobService.runAJobNow(scheduleJob);
			}
		} catch (Exception e) {
			logger.error("操作失败", e);
			addMessage(redirectAttributes, "操作失败！");
			return "redirect:"+Global.getAdminPath()+"/sys/job/?repage";
		}
		addMessage(redirectAttributes, "操作成功！");
		return "redirect:"+Global.getAdminPath()+"/sys/job/?repage";
	}
	
	@RequestMapping(value = "wsList")
	public String wsList(String name, String host, HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			name = URLDecoder.decode(name, "UTF-8");
		} catch (UnsupportedEncodingException e1) {}
		model.addAttribute("name", name);
        List<AmazonAccountConfig> wsConfigs = configService.findWsInfo();
        model.addAttribute("wsConfigs", wsConfigs);
		if (StringUtils.isEmpty(host)) {
			model.addAttribute("message", "操作失败:webservice address is null!");
			return "modules/sys/taskList";
		}
		String interfaceUrl = url.replace("host", host+":8080");
		try {
			Client client = BaseService.getCxfClient(interfaceUrl);
			String[] str = new String[]{key};
			Object[] res = client.invoke("listTask", str);
	        model.addAttribute("list", res[0]);
		} catch (Exception e) {
			model.addAttribute("message", "操作失败:webservice failed! " + interfaceUrl);
			logger.error(e.getMessage(), e);
		}
        model.addAttribute("host", host);
		return "modules/sys/wsTaskList";
	}

	//远程执行定时任务
	@RequestMapping(value = {"runWsTask"})
	public String runWsTask(String name, String host, String jobName, String jobGroup, Model model, RedirectAttributes redirectAttributes) {
		if (StringUtils.isEmpty(host)) {
			addMessage(redirectAttributes, "操作失败:webservice address is null!");
			return "redirect:"+Global.getAdminPath()+"/sys/job/?repage";
		}
		try {
			ScheduleJob scheduleJob = new ScheduleJob();
			scheduleJob.setJobName(jobName);
			scheduleJob.setJobGroup(jobGroup);
			
			String interfaceUrl = url.replace("host", host+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			String[] str = new String[]{key, jobName, jobGroup};
			Object[] res = client.invoke("runTask", str);
			boolean rs = (Boolean)res[0];
			name = URLEncoder.encode(URLEncoder.encode(name, "UTF-8"), "UTF-8");
			if (!rs) {
				logger.info("远程调用接口执行任务失败！" + host);
				addMessage(redirectAttributes, "操作失败！");
				return "redirect:"+Global.getAdminPath()+"/sys/job/wsList/?host=" + host + "&name=" + name;
			}
		} catch (Exception e) {
			logger.error("操作失败", e);
			addMessage(redirectAttributes, "操作失败！");
			return "redirect:"+Global.getAdminPath()+"/sys/job/wsList/?host=" + host + "&name=" + name;
		}
		addMessage(redirectAttributes, "操作成功！");
		return "redirect:"+Global.getAdminPath()+"/sys/job/wsList/?host=" + host + "&name=" + name;
	}

	@RequestMapping(value = {"updateCron"})
	public String updateCron(String cronExpression, String jobName, String jobGroup, Model model, RedirectAttributes redirectAttributes) {
		try {
			ScheduleJob scheduleJob = new ScheduleJob();
			scheduleJob.setJobName(jobName);
			scheduleJob.setJobGroup(jobGroup);
			scheduleJob.setCronExpression(cronExpression);
			jobService.updateJobCron(scheduleJob);
		} catch (Exception e) {
			logger.error("操作失败", e);
			addMessage(redirectAttributes, "操作失败！");
			return "redirect:"+Global.getAdminPath()+"/sys/job/?repage";
		}
		addMessage(redirectAttributes, "操作成功！");
		return "redirect:"+Global.getAdminPath()+"/sys/job/?repage";
	}
}
