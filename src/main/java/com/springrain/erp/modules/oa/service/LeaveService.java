/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.oa.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.workflow.WorkflowUtils;
import com.springrain.erp.modules.oa.dao.LeaveDao;
import com.springrain.erp.modules.oa.entity.Leave;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 请假Service
 * @author liuj
 * @version 2013-04-05
 */
@Service
@Transactional(readOnly = true)
public class LeaveService extends BaseService {

	@Autowired
	private LeaveDao leaveDao;
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
	private WorkFlowService workFlowService;
	
	private String processDefinitionKey = "leave";
	
	public Leave get(String id) {
		return  leaveDao.get(id);
	}

	public Page<Leave> findTodoTasks(Page<Leave> page, Leave leave) {
		//获取所有未未完成任务
		User user = UserUtils.getUser();
		DetachedCriteria dc = leaveDao.createDetachedCriteria();
		if(leave.getCreateDateStart()!=null) {
			dc.add(Restrictions.ge("createDate", leave.getCreateDateStart()));
		} 
		if(leave.getCreateDateEnd()!=null) {
			dc.add(Restrictions.le("createDate", leave.getCreateDateEnd()));
		} 
		if(StringUtils.isNotBlank(leave.getLeaveType())) {
			dc.add(Restrictions.like("leaveType", leave.getLeaveType()));
		}
		dc.add(Restrictions.ne("processStatus","已完成"));
		dc.add(Restrictions.eq("delFlag", Leave.DEL_FLAG_NORMAL));
		dc.createAlias("createBy", "createBy");
		dc.createAlias("createBy.office", "office");
		dc.add(Restrictions.or(Restrictions.and(Restrictions.eq("office.id", user.getOffice().getId()),Restrictions.eq("processStatus", "部门主管审批")),Restrictions.ne("processStatus", "部门主管审批")));
		dc.addOrder(Order.desc("createDate"));
		List<Leave> list = leaveDao.find(dc);
		List<Leave> result = Lists.newArrayList();
		//过滤出当前用户的任务
		if(list.size()>0) {
			List<Task> tasks =Lists.newArrayList();
			List<Task> todoList = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskAssignee(ObjectUtils.toString(user.getId())).active().list();
			List<Task> unsignedTasks = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskCandidateUser(ObjectUtils.toString(user.getId())).active().list();
			tasks.addAll(todoList);
			tasks.addAll(unsignedTasks);
			Set<String> processInstanceIds = Sets.newHashSet();
			for (Task task : tasks) {
				processInstanceIds.add(task.getProcessInstanceId());
			}
			for(Leave l:list) {
				if(processInstanceIds.contains(l.getProcessInstanceId())) {
					result.add(l);
				}
			}
		}
		page.setCount(result.size());
		page.setList(result.subList(page.getFirstResult(),page.getLastResult()));
		return page;
	}
	
	private void skipTask(String processInstanceId,Map<String, Object> model){
		User cuser = UserUtils.getUser();
		Task task = null;
		if(cuser.hasRoleByName("部门管理员")){
			task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			taskService.addComment(task.getId(),processInstanceId, "自动跳过");
			model.put("days", 1);	//只起到补充参数作用,由isDepLead控制流程
			model.put("isDepLead", true);	//部门管理员需总经理审批
	   		taskService.complete(task.getId(),model);
		}
	}
	
	
	public Page<Leave> find(Page<Leave> page, Leave leave, boolean export) {
		DetachedCriteria dc = leaveDao.createDetachedCriteria();
		if(leave.getCreateDateStart()!=null) {
			dc.add(Restrictions.ge("createDate", leave.getCreateDateStart()));
		} 
		if(leave.getCreateDateEnd()!=null) {
			dc.add(Restrictions.le("createDate", leave.getCreateDateEnd()));
		} 
		if(StringUtils.isNotBlank(leave.getLeaveType())) {
			dc.add(Restrictions.like("leaveType", leave.getLeaveType()));
		}
		dc.add(Restrictions.eq("delFlag", Leave.DEL_FLAG_NORMAL));
		dc.createAlias("createBy", "createBy");
		dc.createAlias("createBy.office", "office");
		//dc.add(dataScopeFilter(UserUtils.getUser(), "office", "createBy"));
		if (!UserUtils.getUser().isAdmin() && !UserUtils.getUser().hasRoleByName("总经理") 
				&& !UserUtils.getUser().hasRoleByName("行政人资")) {
			if (UserUtils.getUser().hasRoleByName("部门管理员")) {
				dc.add(Restrictions.eq("createBy.office", UserUtils.getUser().getOffice()));
			} else {
				dc.add(Restrictions.eq("createBy", UserUtils.getUser()));
			}
		}
		if (leave.getCreateBy() != null) {
			dc.add(Restrictions.eq("createBy", leave.getCreateBy()));
		}
		if (StringUtils.isNotEmpty(leave.getProcessStatus())) {
			dc.add(Restrictions.eq("processStatus", leave.getProcessStatus()));
		}
		if (export) {
			dc.addOrder(Order.asc("createBy.office")).addOrder(Order.asc("createBy"));
		} else {
			dc.addOrder(Order.desc("createDate"));
		}
	    return leaveDao.find(page, dc);
	}

	@Transactional(readOnly = false)
	public void save(Leave leave) {
		leaveDao.save(leave);
		leaveDao.flush();
		String businessKey = leave.getId().toString();
		// 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
		identityService.setAuthenticatedUserId(ObjectUtils.toString(leave.getCreateBy().getId()));
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
		String processInstanceId = processInstance.getId();
		leave.setProcessInstanceId(processInstanceId);
		Map<String, Object> map = Maps.newHashMap();
		map.put("deptLeaderPass", true);
		skipTask(processInstanceId,map);
		Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		leave.setProcessStatus(task.getName());
		leaveDao.save(leave);
		note(leave);
	}
	
	private void note(Leave leave){
		String state = leave.getProcessStatus();
		String address = null;
		if("部门主管审批".equals(state)){
			address = workFlowService.getdepLeaderEmail();
		}else if("总经理审批".equals(state)){
			address = workFlowService.getRoleEmail("总经理");
		}else if("人事记录".equals(state)){
			address = workFlowService.getRoleEmail("行政人资");
		}else if("销假".equals(state)||"调整申请".equals(state)){
			address = leave.getCreateBy().getEmail();
		}
		if(address!=null){
			try {
				state = URLEncoder.encode(URLEncoder.encode(state, "utf-8"),"utf-8");
			} catch (UnsupportedEncodingException e) {}
			String url ="oa/"+processDefinitionKey+"/detail?id="+leave.getId()+"&audit=1&state="+state;
			workFlowService.noteProesser(address, leave.getCreateBy().getName(),"请假流程", url,leave.getProcessStatus());
		}
	}
	
	private void noteTrack(Leave leave){
		String url ="sys/workflow/processMap?processInstanceId="+leave.getProcessInstanceId();
		workFlowService.noteClaimer(leave.getCreateBy().getEmail(),leave.getProcessStatus(), "请假流程", url);
	}

	//部门领导审批
	@Transactional(readOnly = false)
	public void deptLeaderAudit(Leave leave) {
		WorkflowUtils.claim(leave.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(leave.getProcessInstanceId()).singleResult();
		String taskId  = task.getId();
		//添加批注
		taskService.addComment(taskId, leave.getProcessInstanceId(), leave.getAuditRemarks());
		Map<String, Object> map = Maps.newHashMap();
		map.put("deptLeaderPass", leave.isPass());
		map.put("days", leave.getAPPDays());
		map.put("isDepLead", leave.getCreateBy().hasRoleByName("部门管理员")?true:false);
		taskService.setVariableLocal(taskId, "deptLeaderPass", leave.isPass());
		//完成任务
		taskService.complete(task.getId(),map);
		task = taskService.createTaskQuery().processInstanceId(leave.getProcessInstanceId()).singleResult();
		noteTrack(leave);
		leave.setProcessStatus(task.getName());
		leaveDao.save(leave);
		note(leave);
	}
	
	//人事登记
	@Transactional(readOnly = false)
	public void hrAudit(Leave leave) {
		WorkflowUtils.claim(leave.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(leave.getProcessInstanceId()).singleResult();
		String taskId  = task.getId();
		//添加批注
		taskService.addComment(taskId, leave.getProcessInstanceId(), leave.getAuditRemarks());
		//完成任务
		Map<String, Object> map = Maps.newHashMap();
		map.put("hrPass", true);
		taskService.complete(task.getId(),map);
		task = taskService.createTaskQuery().processInstanceId(leave.getProcessInstanceId()).singleResult();
		noteTrack(leave);
		leave.setProcessStatus(task.getName());
		leaveDao.save(leave);
		note(leave);
	}
	
	//总经理审批
	@Transactional(readOnly = false)
	public void mgAudit(Leave leave) {
		WorkflowUtils.claim(leave.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(leave.getProcessInstanceId()).singleResult();
		//添加批注
		taskService.addComment(task.getId(), leave.getProcessInstanceId(), leave.getAuditRemarks());
		Map<String, Object> map = Maps.newHashMap();
		map.put("mgrPass", leave.isPass());
		String taskId  = task.getId();
		taskService.setVariablesLocal(taskId, map);
		//完成任务
		taskService.complete(task.getId(),map);
		task = taskService.createTaskQuery().processInstanceId(leave.getProcessInstanceId()).singleResult();
		noteTrack(leave);
		leave.setProcessStatus(task.getName());
		leaveDao.save(leave);
		note(leave);
	}
	
	//调整申请
	@Transactional(readOnly = false)
	public void modifyApply(Leave leave) {
		WorkflowUtils.claim(leave.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(leave.getProcessInstanceId()).singleResult();
		Map<String, Object> map = Maps.newHashMap();
		map.put("reApply", leave.isPass());
		//完成任务
		String taskId  = task.getId();
		taskService.setVariablesLocal(taskId, map);
		taskService.complete(task.getId(),map);
		task = taskService.createTaskQuery().processInstanceId(leave.getProcessInstanceId()).singleResult();
		if(task==null) {
			leave.setProcessStatus("已完成");
			leave.setDelFlag(Leave.DEL_FLAG_DELETE);
		} else {
			Map<String, Object> model = Maps.newHashMap();
			map.put("deptLeaderPass", true);
			skipTask(leave.getProcessInstanceId(),model);
			leave.setProcessStatus(taskService.createTaskQuery().processInstanceId(leave.getProcessInstanceId()).singleResult().getName());
		}
		leaveDao.save(leave);
		note(leave);
	}
	
	//销假
	@Transactional(readOnly = false)
	public void reportBack(Leave leave) {
		WorkflowUtils.claim(leave.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(leave.getProcessInstanceId()).singleResult();
		//完成任务
		taskService.complete(task.getId());
		task = taskService.createTaskQuery().processInstanceId(leave.getProcessInstanceId()).singleResult();
		leave.setProcessStatus("已完成");
		leaveDao.save(leave);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		leaveDao.deleteById(id);
	}
}
