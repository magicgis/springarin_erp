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
import com.springrain.erp.common.workflow.WorkflowUtils;
import com.springrain.erp.modules.oa.dao.AmazonDiscountDao;
import com.springrain.erp.modules.oa.entity.AmazonDiscount;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 采购Service
 * @author liuj
 * @version 2013-04-05
 */
@Service
@Transactional(readOnly = true)
public class AmazonDiscountService extends BaseService {

	@Autowired
	private AmazonDiscountDao amazonDiscountDao;
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
	
	private String processDefinitionKey = "amazonDiscount";
	
	public AmazonDiscount get(String id) {
		return  amazonDiscountDao.get(id);
	}

	public Page<AmazonDiscount> findTodoTasks(Page<AmazonDiscount> page, AmazonDiscount amazonDiscount) {
		//获取所有未未完成任务
		User user = UserUtils.getUser();
		DetachedCriteria dc = amazonDiscountDao.createDetachedCriteria();
		if(amazonDiscount.getCreateDateStart()!=null) {
			dc.add(Restrictions.ge("createDate", amazonDiscount.getCreateDateStart()));
		} 
		if(amazonDiscount.getCreateDateEnd()!=null) {
			dc.add(Restrictions.le("createDate", amazonDiscount.getCreateDateEnd()));
		} 
		dc.add(Restrictions.ne("processStatus","已完成"));
		dc.add(Restrictions.eq("delFlag", AmazonDiscount.DEL_FLAG_NORMAL));
		dc.createAlias("createBy", "createBy");
		dc.createAlias("createBy.office", "office");
		dc.add(Restrictions.or(Restrictions.and(Restrictions.eq("office.id", user.getOffice().getId()),Restrictions.in("processStatus",new String[]{"部门主管确认","部门主管审批"})),Restrictions.not(Restrictions.in("processStatus", new String[]{"部门主管确认","部门主管审批"}))));
		dc.addOrder(Order.desc("createDate"));
		List<AmazonDiscount> list = amazonDiscountDao.find(dc);
		List<AmazonDiscount> result = Lists.newArrayList();
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
			for(AmazonDiscount l:list) {
				if(processInstanceIds.contains(l.getProcessInstanceId())) {
					result.add(l);
				}
			}
		}
		page.setCount(result.size());
		page.setList(result.subList(page.getFirstResult(),page.getLastResult()));
		return page;
	}

	public Page<AmazonDiscount> find(Page<AmazonDiscount> page, AmazonDiscount amazonDiscount) {
		DetachedCriteria dc = amazonDiscountDao.createDetachedCriteria();
		if(amazonDiscount.getCreateDateStart()!=null) {
			dc.add(Restrictions.ge("createDate", amazonDiscount.getCreateDateStart()));
		} 
		if(amazonDiscount.getCreateDateEnd()!=null) {
			dc.add(Restrictions.le("createDate", amazonDiscount.getCreateDateEnd()));
		} 
		dc.add(Restrictions.eq("delFlag", AmazonDiscount.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("createDate"));
	    return amazonDiscountDao.find(page, dc);
	}

	@Transactional(readOnly = false)
	public void save(AmazonDiscount amazonDiscount) {
		amazonDiscountDao.save(amazonDiscount);
		amazonDiscountDao.flush();
		String businessKey = amazonDiscount.getId().toString();
		// 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
		identityService.setAuthenticatedUserId(ObjectUtils.toString(amazonDiscount.getCreateBy().getId()));
		Map<String, Object> map = Maps.newHashMap();
		map.put("price", amazonDiscount.getPrice());
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey,map);
		String processInstanceId = processInstance.getId();
		amazonDiscount.setProcessInstanceId(processInstanceId);
		map.clear();
		map.put("pass", true);
		skipTask(processInstanceId,map);
		amazonDiscount.setProcessStatus(taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getName());
		amazonDiscountDao.save(amazonDiscount);
		note(amazonDiscount);
	}
	
	private void skipTask(String processInstanceId,Map<String, Object> model){
		User cuser = UserUtils.getUser();
		Task task = null;
		if(cuser.hasRoleByName("总经理")){
			task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			taskService.addComment(task.getId(),processInstanceId, "自动跳过");
	   		taskService.complete(task.getId(),model);
	   		task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		}else if(cuser.hasRoleByName("部门管理员")){
			task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			if("部门主管审批".equals(task.getName())){
				taskService.addComment(task.getId(),processInstanceId, "自动跳过");
		   		taskService.complete(task.getId(),model);
			}
		}
	}
	
	//部门领导审批
	@Transactional(readOnly = false)
	public void deptLeaderAudit(AmazonDiscount amazonDiscount) {
		WorkflowUtils.claim(amazonDiscount.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(amazonDiscount.getProcessInstanceId()).singleResult();
		String taskId  = task.getId();
		//添加批注
		taskService.addComment(taskId, amazonDiscount.getProcessInstanceId(), amazonDiscount.getAuditRemarks());
		Map<String, Object> map = Maps.newHashMap();
		map.put("pass", amazonDiscount.isPass());
		map.put("price", amazonDiscount.getPrice());
		taskService.setVariableLocal(taskId, "pass", amazonDiscount.isPass());
		//完成任务
		taskService.complete(task.getId(),map);
		task = taskService.createTaskQuery().processInstanceId(amazonDiscount.getProcessInstanceId()).singleResult();
		noteTrack(amazonDiscount);
		amazonDiscount.setProcessStatus(task.getName());
		amazonDiscountDao.save(amazonDiscount);
		note(amazonDiscount);
	}
	
	//总经理审批
	@Transactional(readOnly = false)
	public void mgAudit(AmazonDiscount amazonDiscount) {
		WorkflowUtils.claim(amazonDiscount.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(amazonDiscount.getProcessInstanceId()).singleResult();
		//添加批注
		taskService.addComment(task.getId(), amazonDiscount.getProcessInstanceId(), amazonDiscount.getAuditRemarks());
		Map<String, Object> map = Maps.newHashMap();
		map.put("pass", amazonDiscount.isPass());
		String taskId  = task.getId();
		taskService.setVariablesLocal(taskId, map);
		//完成任务
		taskService.complete(task.getId(),map);
		task = taskService.createTaskQuery().processInstanceId(amazonDiscount.getProcessInstanceId()).singleResult();
		noteTrack(amazonDiscount);
		amazonDiscount.setProcessStatus(task.getName());
		amazonDiscountDao.save(amazonDiscount);
		note(amazonDiscount);
	}
	
	//调整申请
	@Transactional(readOnly = false)
	public void modifyApply(AmazonDiscount amazonDiscount) {
		WorkflowUtils.claim(amazonDiscount.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(amazonDiscount.getProcessInstanceId()).singleResult();
		Map<String, Object> map = Maps.newHashMap();
		map.put("price", amazonDiscount.getPrice());
		map.put("pass", amazonDiscount.isPass());
		//完成任务
		String taskId  = task.getId();
		taskService.setVariablesLocal(taskId, map);
		taskService.complete(task.getId(),map);
		task = taskService.createTaskQuery().processInstanceId(amazonDiscount.getProcessInstanceId()).singleResult();
		if(task==null) {
			amazonDiscount.setProcessStatus("已完成");
			amazonDiscount.setDelFlag(AmazonDiscount.DEL_FLAG_DELETE);
		} else {
			Map<String, Object> model = Maps.newHashMap();
			model.put("pass", true);
			skipTask(amazonDiscount.getProcessInstanceId(),model);
			amazonDiscount.setProcessStatus(taskService.createTaskQuery().processInstanceId(amazonDiscount.getProcessInstanceId()).singleResult().getName());
			note(amazonDiscount);
		}
		amazonDiscountDao.save(amazonDiscount);
	}
	
	//执行打折
	@Transactional(readOnly = false)
	public void excute(AmazonDiscount amazonDiscount) {
		WorkflowUtils.claim(amazonDiscount.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(amazonDiscount.getProcessInstanceId()).singleResult();
		//完成任务
		taskService.complete(task.getId());
		task = taskService.createTaskQuery().processInstanceId(amazonDiscount.getProcessInstanceId()).singleResult();
		amazonDiscount.setProcessStatus(task.getName());
		amazonDiscountDao.save(amazonDiscount);
		note(amazonDiscount);
	}
	
	//主管确认
	@Transactional(readOnly = false)
	public void report(AmazonDiscount amazonDiscount) {
		WorkflowUtils.claim(amazonDiscount.getProcessInstanceId());
		Task task = taskService.createTaskQuery().processInstanceId(amazonDiscount.getProcessInstanceId()).singleResult();
		//完成任务
		taskService.complete(task.getId());
		noteTrack(amazonDiscount);
		task = taskService.createTaskQuery().processInstanceId(amazonDiscount.getProcessInstanceId()).singleResult();
		amazonDiscount.setProcessStatus("已完成");
		amazonDiscountDao.save(amazonDiscount);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		amazonDiscountDao.deleteById(id);
	}
	
	private void note(AmazonDiscount amazonDiscount){
		String state = amazonDiscount.getProcessStatus();
		String address = null;
		if("部门主管审批".equals(state)||"部门主管确认".equals(state)){
			address = workFlowService.getdepLeaderEmail();
		}else if("总经理审批".equals(state)){
			address = workFlowService.getRoleEmail("总经理");
		}else if("重新调整".equals(state)||"执行打折".equals(state)){
			address = amazonDiscount.getCreateBy().getEmail();
		}
		if(address!=null){
			try {
				state = URLEncoder.encode(URLEncoder.encode(state, "utf-8"),"utf-8");
			} catch (UnsupportedEncodingException e) {}
			String url ="oa/"+processDefinitionKey+"/detail?id="+amazonDiscount.getId()+"&audit=1&state="+state;
			workFlowService.noteProesser(address, amazonDiscount.getCreateBy().getName(),"亚马逊折扣申请", url,amazonDiscount.getProcessStatus());
		}
	}
	
	private void noteTrack(AmazonDiscount amazonDiscount){
		String url ="sys/workflow/processMap?processInstanceId="+amazonDiscount.getProcessInstanceId();
		workFlowService.noteClaimer(amazonDiscount.getCreateBy().getEmail(),amazonDiscount.getProcessStatus(), "亚马逊折扣申请", url);
	}
}
