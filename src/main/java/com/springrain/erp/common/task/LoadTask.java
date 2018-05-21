//package com.springrain.erp.common.task;
//
//import java.util.List;
//
//import org.quartz.SchedulerException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//
//import com.google.common.collect.Lists;
//import com.springrain.erp.modules.sys.service.JobService;
//
//@Component("taskInitDoWork")
//@Lazy(false)
//public class LoadTask implements InitializingBean{
//	public final static Logger logger = LoggerFactory.getLogger(LoadTask.class);
//
//	@Autowired
//	private JobService jobService;
//
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		logger.error("开始加载任务");
//		new Thread(){
//			public void run() {
//		        // 这里从数据库中获取任务信息数据  
//		        List<ScheduleJob> jobList = getTasks();
//		        for (ScheduleJob job : jobList) {  
//		            try {
//		            	jobService.addJob(job);
//					} catch (SchedulerException e) {
//						logger.error("动态任务添加失败", e);
//					}  
//		        } 
//			}
//		}.start(); 
//	}
//
//	private List<ScheduleJob> getTasks() {
//		List<ScheduleJob> taskList = Lists.newArrayList();
//		ScheduleJob task1 = new ScheduleJob();
//	 	task1.setJobId(1);
//		task1.setCronExpression("*/30 * * ? * *");
//		task1.setJobName("test1");
//		task1.setJobGroup("test1");
//		task1.setBeanClass("com.springrain.erp.common.task.TestTask");
//		task1.setJobStatus("1");
//		task1.setIsConcurrent("1");
//		task1.setMethodName("monitor");	
//		taskList.add(task1);
//		ScheduleJob task2 = new ScheduleJob();
//		task2.setJobId(1);
//		task2.setCronExpression("*/10 * * ? * *");
//		task2.setJobName("测试");
//		task2.setJobGroup("test1");
//		task2.setBeanClass("com.springrain.erp.common.task.TestTask");
//		task2.setJobStatus("1");
//		task2.setIsConcurrent("0");
//		task2.setMethodName("monitor1");	
//		taskList.add(task2);
//		return taskList;
//	}
//}
