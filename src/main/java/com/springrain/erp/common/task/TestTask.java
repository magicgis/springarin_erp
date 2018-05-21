package com.springrain.erp.common.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomer;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;

public class TestTask{
	public final static Logger log = LoggerFactory.getLogger(TestTask.class);
	
	@Autowired
	private AmazonCustomerService amazonCustomerService;
	
	public void monitor() {
		Date date = new Date();
		AmazonCustomer customer = amazonCustomerService.get("A00012942TO8E8T0GB5C9");
		log.info("动态任务测试" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + customer.getName());
	}
	
	public void monitor1() {
		Date date = new Date();
		log.info("动态任务测试2" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
	}
	
}
