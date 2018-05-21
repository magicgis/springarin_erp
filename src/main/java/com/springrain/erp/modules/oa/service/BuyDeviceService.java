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
import com.springrain.erp.modules.oa.dao.BuyDeviceDao;
import com.springrain.erp.modules.oa.entity.BuyDevice;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 采购Service
 * @author liuj
 * @version 2013-04-05
 */
@Service
@Transactional(readOnly = true)
public class BuyDeviceService extends BaseService {

	@Autowired
	private BuyDeviceDao buyDeviceDao;
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
	
	private String processDefinitionKey = "buyDevice";
	
	public BuyDevice get(String id) {
		return  buyDeviceDao.get(id);
	}

	public Page<BuyDevice> findTodoTasks(Page<BuyDevice> page, BuyDevice buyDevice) {
		//获取所有未未完成任务
		User user = UserUtils.getUser();
		DetachedCriteria dc = buyDeviceDao.createDetachedCriteria();
		if(buyDevice.getCreateDateStart()!=null) {
			dc.add(Restrictions.ge("createDate", buyDevice.getCreateDateStart()));
		} 
		if(buyDevice.getCreateDateEnd()!=null) {
			dc.add(Restrictions.le("createDate", buyDevice.getCreateDateEnd()));
		} 
		if(StringUtils.isNotBlank(buyDevice.getDeviceType())) {
			dc.add(Restrictions.like("leaveType", buyDevice.getDeviceType()));
		}
		dc.add(Restrictions.ne("processStatus","已完成"));
		dc.add(Restrictions.eq("delFlag", BuyDevice.DEL_FLAG_NORMAL));
		dc.createAlias("createBy", "createBy");
		dc.createAlias("createBy.office", "office");
		dc.add(Restrictions.or(Restrictions.and(Restrictions.eq("office.id", user.getOffice().getId()),Restrictions.eq("processStatus", "部门主管审批")),Restrictions.ne("processStatus", "部门主管审批")));
		dc.addOrder(Order.desc("createDate"));
		List<BuyDevice> list = buyDeviceDao.find(dc);
		List<BuyDevice> result = Lists.newArrayList();
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
			for(BuyDevice l:list) {
				if(processInstanceIds.contains(l.getProcessInstanceId())) {
					result.add(l);
				}
			}
		}
		page.setCount(result.size());
		page.setList(result.subList(page.getFirstResult(),page.getLastResult()));
		return page;
	}

	public Page<BuyDevice> find(Page<BuyDevice> page, BuyDevice buyDevice) {
		DetachedCriteria dc = buyDeviceDao.createDetachedCriteria();
		if(buyDevice.getCreateDateStart()!=null) {
			dc.add(Restrictions.ge("createDate", buyDevice.getCreateDateStart()));
		} 
		if(buyDevice.getCreateDateEnd()!=null) {
			dc.add(Restrictions.le("createDate", buyDevice.getCreateDateEnd()));
		} 
		if(StringUtils.isNotBlank(buyDevice.getDeviceType())) {
			dc.add(Restrictions.like("leaveType", buyDevice.getDeviceType()));
		}
		dc.add(Restrictions.eq("delFlag", BuyDevice.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("createDate"));
	    return buyDeviceDao.find(page, dc);
	}

	@Transactional(readOnly = false)
	public void save(BuyDevice buyDevice) {
		buyDeviceDao.save(buyDevice);
		buyDeviceDao.flush();
		String businessKey = buyDevice.getId().toString();
		// 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
		identityService.setAuthenticatedUserId(ObjectUtils.toString(buyDevice.getCreateBy().getId()));
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
		String processInstanceId = processInstance.getId();
		buyDevice.setProcessInstanceId(processInstanceId);
		Map<String, Object> map = Maps.newHashMap();
		map.put("pass", true);
		map.put("price", buyDevice.getPrice());
		skipTask(processInstanceId,map);
		buyDevice.setProcessStatus(taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getName());
		buyDeviceDao.save(buyDevice);
		note(buyDevice);
	}
	
	private void skipTask(String processInstanceId,Map<String, Object> model){
		User cuser = UserUtils.getUser();
		Task task = null;
		if(cuser.hasRoleByName("总经理")){
			task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			taskService.addComment(task.getId(),processInstanceId, "自动跳过");
	   		taskService.complete(task.getId(),model);
	   		task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
	   		if("总经理审批".equals(task.getName())){
	   			taskService.complete(task.getId(),model);
	   		}
		}else if(cuser.hasRoleByName("部门管理员")){
			task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			taskService.addComment(task.getId(),processInstanceId, "自动跳过");
	   		taskService.complete(task.getId(),model);
		}
	}
	
	//部门领导审批
	@Transactional(readOnly = false)
	public void deptLeaderAudit(BuyDevice buyDevice) {
		WorkflowUtils.claim(buyDevice.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(buyDevice.getProcessInstanceId()).singleResult();
		String taskId  = task.getId();
		//添加批注
		taskService.addComment(taskId, buyDevice.getProcessInstanceId(), buyDevice.getAuditRemarks());
		Map<String, Object> map = Maps.newHashMap();
		map.put("pass", buyDevice.isPass());
		map.put("price", buyDevice.getPrice());
		taskService.setVariableLocal(taskId, "pass", buyDevice.isPass());
		//完成任务
		taskService.complete(task.getId(),map);
		task = taskService.createTaskQuery().processInstanceId(buyDevice.getProcessInstanceId()).singleResult();
		noteTrack(buyDevice);
		buyDevice.setProcessStatus(task.getName());
		buyDeviceDao.save(buyDevice);
		note(buyDevice);
	}
	
	//总经理审批
	@Transactional(readOnly = false)
	public void mgAudit(BuyDevice buyDevice) {
		WorkflowUtils.claim(buyDevice.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(buyDevice.getProcessInstanceId()).singleResult();
		//添加批注
		taskService.addComment(task.getId(), buyDevice.getProcessInstanceId(), buyDevice.getAuditRemarks());
		Map<String, Object> map = Maps.newHashMap();
		map.put("pass", buyDevice.isPass());
		String taskId  = task.getId();
		taskService.setVariablesLocal(taskId, map);
		//完成任务
		taskService.complete(task.getId(),map);
		task = taskService.createTaskQuery().processInstanceId(buyDevice.getProcessInstanceId()).singleResult();
		noteTrack(buyDevice);
		buyDevice.setProcessStatus(task.getName());
		buyDeviceDao.save(buyDevice);
		note(buyDevice);
	}
	
	//调整申请
	@Transactional(readOnly = false)
	public void modifyApply(BuyDevice buyDevice) {
		WorkflowUtils.claim(buyDevice.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(buyDevice.getProcessInstanceId()).singleResult();
		Map<String, Object> map = Maps.newHashMap();
		map.put("pass", buyDevice.isPass());
		//完成任务
		String taskId  = task.getId();
		taskService.setVariablesLocal(taskId, map);
		taskService.complete(task.getId(),map);
		task = taskService.createTaskQuery().processInstanceId(buyDevice.getProcessInstanceId()).singleResult();
		if(task==null) {
			buyDevice.setProcessStatus("已完成");
			buyDevice.setDelFlag(BuyDevice.DEL_FLAG_DELETE);
		} else {
			Map<String, Object> model = Maps.newHashMap();
			model.put("pass", true);
			model.put("price", buyDevice.getPrice());
			skipTask(buyDevice.getProcessInstanceId(),model);
			buyDevice.setProcessStatus(taskService.createTaskQuery().processInstanceId(buyDevice.getProcessInstanceId()).singleResult().getName());
			note(buyDevice);
		}
		buyDeviceDao.save(buyDevice);
	}
	
	//财务支出
	@Transactional(readOnly = false)
	public void getMoney(BuyDevice buyDevice) {
		WorkflowUtils.claim(buyDevice.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(buyDevice.getProcessInstanceId()).singleResult();
		//完成任务
		taskService.complete(task.getId());
		task = taskService.createTaskQuery().processInstanceId(buyDevice.getProcessInstanceId()).singleResult();
		noteTrack(buyDevice);
		buyDevice.setProcessStatus(task.getName());
		buyDeviceDao.save(buyDevice);
		note(buyDevice);
	}
	
	//行政登记
	@Transactional(readOnly = false)
	public void report(BuyDevice buyDevice) {
		WorkflowUtils.claim(buyDevice.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(buyDevice.getProcessInstanceId()).singleResult();
		//完成任务
		taskService.complete(task.getId());
		task = taskService.createTaskQuery().processInstanceId(buyDevice.getProcessInstanceId()).singleResult();
		noteTrack(buyDevice);
		buyDevice.setProcessStatus("已完成");
		buyDeviceDao.save(buyDevice);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		buyDeviceDao.deleteById(id);
	}
	
	private void note(BuyDevice buyDevice){
		String state = buyDevice.getProcessStatus();
		String address = null;
		if("部门主管审批".equals(state)){
			address = workFlowService.getdepLeaderEmail();
		}else if("总经理审批".equals(state)){
			address = workFlowService.getRoleEmail("总经理");
		}else if("资产登记".equals(state)){
			address = workFlowService.getRoleEmail("行政人资");
		}else if("财务支出费用".equals(state)){
			address = workFlowService.getRoleEmail("财务人员");
		}else if("调整申请".equals(state)){
			address = buyDevice.getCreateBy().getEmail();
		}
		if(address!=null){
			try {
				state = URLEncoder.encode(URLEncoder.encode(state, "utf-8"),"utf-8");
			} catch (UnsupportedEncodingException e) {}
			String url ="oa/"+processDefinitionKey+"/detail?id="+buyDevice.getId()+"&audit=1&state="+state;
			workFlowService.noteProesser(address, buyDevice.getCreateBy().getName(),"办公室采购审批", url,buyDevice.getProcessStatus());
		}
	}
	
	private void noteTrack(BuyDevice buyDevice){
		String url ="sys/workflow/processMap?processInstanceId="+buyDevice.getProcessInstanceId();
		workFlowService.noteClaimer(buyDevice.getCreateBy().getEmail(),buyDevice.getProcessStatus(), "办公室采购审批", url);
	}
	
}
