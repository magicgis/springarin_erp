package com.springrain.erp.modules.oa.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 工作流Service
 */
@Service
@Transactional(readOnly = true)
public class WorkFlowService extends BaseService {

	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	protected TaskService taskService;
	@Autowired
	protected HistoryService historyService;
	@Autowired
	protected RepositoryService repositoryService;
	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private MailManager mailManager;
	@Autowired
	private SystemService systemService;
	
	private static Logger logger = LoggerFactory.getLogger(WorkFlowService.class);

	protected static Map<String, ProcessDefinition> PROCESS_DEFINITION_CACHE = new HashMap<String, ProcessDefinition>();

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d H:mm");
	
	public List<Map<String, Object>> getCreateByWorkFlowList() {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		User user = UserUtils.getUser();
		List<HistoricProcessInstance> lists = historyService.createHistoricProcessInstanceQuery().startedBy(user.getId()).unfinished().list();
		for (HistoricProcessInstance historicProcessInstance : lists) {
			result.add(packageTaskInfo(historicProcessInstance));
		}
		return result;
	}

	public List<Map<String, Object>> getTodoWorkFlowList() {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		User user = UserUtils.getUser();
		// 已经签收的任务
		List<Task> todoList = taskService.createTaskQuery()
				.taskAssignee(user.getId()).active().list();
		for (Task task : todoList) {
			String processDefinitionId = task.getProcessDefinitionId();
			ProcessDefinition processDefinition = getProcessDefinition(processDefinitionId);

			Map<String, Object> singleTask = packageTaskInfo(task,
					processDefinition);
			if(null!=singleTask){
				singleTask.put("status", "todo");
				result.add(singleTask);
			}
		}
		// 等待签收的任务
		List<Task> toClaimList = taskService.createTaskQuery()
				.taskCandidateUser(user.getId()).active().list();
		for (Task task : toClaimList) {
			String processDefinitionId = task.getProcessDefinitionId();
			ProcessDefinition processDefinition = getProcessDefinition(processDefinitionId);

			Map<String, Object> singleTask = packageTaskInfo(task,
					processDefinition);
			if(null!=singleTask){
				singleTask.put("status", "claim");
				result.add(singleTask);
			}
		}
		return result;
	}

	private Map<String, Object> packageTaskInfo(Task task, ProcessDefinition processDefinition) {
		String name = processDefinition.getName();
		String taskName = task.getName();
		ProcessInstance proessIns =  runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).active().singleResult();
		String businessId = proessIns.getBusinessKey();
		Map<String, Object> singleTask = new HashMap<String, Object>();
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessId).singleResult();
		User createUser = UserUtils.getUserById(historicProcessInstance.getStartUserId());
		if("部门主管审批".equals(taskName)||"部门主管确认".equals(taskName)){
			if(!createUser.getOffice().getId().equals(UserUtils.getUser().getOffice().getId())){
				return null;
			}
		}
		singleTask.put("createName",createUser.getName());
		singleTask.put("id", task.getId());
		singleTask.put("name",taskName);
		singleTask.put("createTime", sdf.format(task.getCreateTime()));
		singleTask.put("pdname", name);
		singleTask.put("pdversion", processDefinition.getVersion());
		singleTask.put("pid", task.getProcessInstanceId());
		
		singleTask.put("viewUrl","oa/"+processDefinition.getKey()+"/detail?id="+businessId);
		singleTask.put("processUrl","oa/"+processDefinition.getKey()+"/detail?id="+businessId+"&audit=1&state="+taskName);
		return singleTask;
	}
	
	private Map<String, Object> packageTaskInfo(HistoricProcessInstance historicProcessInstance) {
		String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
		ProcessDefinition processDefinition = getProcessDefinition(processDefinitionId);
		String businessId = historicProcessInstance.getBusinessKey();
		Task task = taskService.createTaskQuery().processDefinitionId(processDefinitionId).processInstanceBusinessKey(businessId).singleResult();
		String name = processDefinition.getName();
		String taskName = task.getName();
		Map<String, Object> singleTask = new HashMap<String, Object>();
		singleTask.put("createName",UserUtils.getUserById(historicProcessInstance.getStartUserId()).getName());
		singleTask.put("id", task.getId());
		singleTask.put("name",taskName);
		singleTask.put("createTime", sdf.format(task.getCreateTime()));
		singleTask.put("pdname", name);
		singleTask.put("pdversion", processDefinition.getVersion());
		String pid = task.getProcessInstanceId();
		singleTask.put("pid", pid);
		
		singleTask.put("viewUrl","oa/"+processDefinition.getKey()+"/detail?id="+businessId);
		
		singleTask.put("trackUrl","/sys/workflow/processMap?processInstanceId="+pid);
		return singleTask;
	}
	

	private ProcessDefinition getProcessDefinition(String processDefinitionId) {
		ProcessDefinition processDefinition = PROCESS_DEFINITION_CACHE
				.get(processDefinitionId);
		if (processDefinition == null) {
			processDefinition = repositoryService
					.createProcessDefinitionQuery()
					.processDefinitionId(processDefinitionId).singleResult();
			PROCESS_DEFINITION_CACHE
					.put(processDefinitionId, processDefinition);
		}
		return processDefinition;
	}
	
	public  void noteProesser(final String toAddress,final String userName,final String type,final String url,final String wfType){
		new Thread(){
			@Override
			public void run() {
				try{
					MailInfo mailInfo = new MailInfo(toAddress, "ERP-->OA业务办理提醒邮件", new Date());
					String content = "流程类型："+type+"<br/>申请人："+userName+"<br/>需要您办理["+wfType+"]----><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/"+url+"' target='_blank'>点击办理</a>"+"<br/><br/>---------------系统发送邮件，请不要回复！";
					mailInfo.setContent(HtmlUtils.htmlUnescape(content));
					boolean rs = mailManager.send(mailInfo);
					int i = 0 ;
					while(!rs && i<3){
						Thread.sleep(5000);
						rs = mailManager.send(mailInfo);
						i++;
					}
					if(!rs){
						logger.error(userName+"的审批提醒:发送失败");
					}
					
				} catch (Exception e) {
					logger.error(userName+":"+e.getMessage());
				}
			}
		}.start();
	} 
	
	public  void noteClaimer(final String toAddress, final String passType,final String type,final String url){
		new Thread(){
			@Override
			public void run() {
				try{
					MailInfo mailInfo = new MailInfo(toAddress, "ERP-->OA流程状态变动提醒邮件", new Date());
					String content = "您申请的"+type+"已经已完成"+passType+"<br/>----><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/"+url+"' target='_blank'>点击可查看</a>"+"<br/><br/>---------------系统发送邮件，请不要回复！";
					mailInfo.setContent(HtmlUtils.htmlUnescape(content));
					boolean rs = mailManager.send(mailInfo);
					int i = 0 ;
					while(!rs && i<3){
						Thread.sleep(5000);
						rs = mailManager.send(mailInfo);
						i++;
					}
					if(!rs){
						logger.error(toAddress+"的审批结果提醒:发送失败");
					}
					
				} catch (Exception e) {
					logger.error(toAddress+":"+e.getMessage());
				}
			}
		}.start();		
	} 
	
	public String getdepLeaderEmail(){
		String rs = "";
		List<User> users = UserUtils.getUser().getOffice().getUserList();
		for (User user : users) {
			if(user.getRoleNames().contains("部门管理员")){
				rs +=(user.getEmail()+",");
			}
		}
		if(rs.length()>0){
			return rs.substring(0,rs.length()-1);
		}
		return null;
	}
	
	public String getRoleEmail(String roleName){
		List<User> users = systemService.findRoleByName(roleName).getUserList();
		String rs = "";
		for (User user : users) {
			rs +=(user.getEmail()+",");
		}
		if(rs.length()>0){
			return rs.substring(0,rs.length()-1);
		}
		return null;
	}
}
