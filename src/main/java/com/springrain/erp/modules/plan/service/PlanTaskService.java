package com.springrain.erp.modules.plan.service;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.plan.dao.TaskDao;
import com.springrain.erp.modules.plan.entity.Task;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 任务管理Service
 * @author tim
 * @version 2014-04-21
 */
@Component
@Transactional(readOnly = true)
public class PlanTaskService extends BaseService {

	@Autowired
	private TaskDao taskDao;
	
	public Task get(String id) {
		return taskDao.get(id);
	}
	
	public Page<Task> find(Page<Task> page, Task task) {
		DetachedCriteria dc = taskDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(task.getSubject())){
			dc.add(Restrictions.like("subject", "%"+task.getSubject()+"%"));
		}
		dc.add(Restrictions.eq(Task.FIELD_DEL_FLAG, Task.DEL_FLAG_NORMAL));
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			dc.add(Restrictions.or(Restrictions.eq("createBy",user),Restrictions.sqlRestriction(" ? in ( select b.user_id from plan_task_user b " +   
                " where b.task_id = {alias}.id)",user.getId(),StringType.INSTANCE)));
		}
		return taskDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(Task task) {
		taskDao.save(task);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		taskDao.deleteById(id);
	}
	
}
