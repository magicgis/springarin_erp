package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;

public class CountCustomersMonitor {
	
	
	@Autowired
	private AmazonCustomerService amazonCustomerService;
	
	private static Logger logger = LoggerFactory.getLogger(CountCustomersMonitor.class);
	

	public void countCustomers() {
		logger.info("开始关联客人购买记录");
		Date  date = amazonCustomerService.getMaxBuyDate();
		if(date==null){
			date = DateUtils.addDays(new Date(), -10);
		}else{
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
		}
		int page = 1;
		long count = 0;
		while(page>0){		
			count = amazonCustomerService.countCustomersData(date,page);
			if((count - 100*page)>0){
				page++;
			}else{
				page = -1;
			}
		}
		//统计customerid
		amazonCustomerService.sycnCid();
		
		logger.info("关联客人购买记录结束");
	}
	


	
	
}
