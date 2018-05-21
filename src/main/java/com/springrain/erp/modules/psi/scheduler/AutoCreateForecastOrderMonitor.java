package com.springrain.erp.modules.psi.scheduler;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.psi.service.ForecastOrderService;
import com.springrain.erp.modules.psi.service.PurchaseFinancialService;

public class AutoCreateForecastOrderMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AutoCreateForecastOrderMonitor.class);
	@Autowired
	private ForecastOrderService  forecastService ;
	@Autowired
	private MailManager mailManager;
	//每周二早10点生成预测下单数据开始
	public void generateOrder() {
		LOGGER.info("每周二早10点生成预测下单数据开始！");
		try{
			//有过有没有审核通过的预测订单就不生成
			if(!forecastService.isExistOrder()){
				forecastService.generateOrder(null);
				Date date = new Date();
				//发信给德国仓库人员：
				String toAddress="amazon-sales@inateck.com";
				final MailInfo mailInfo = new MailInfo(toAddress,"采购计划已生成，请各产品线及市场负责人于下午3点之前更新补充采购计划"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent("Hi,ALL<br/>&nbsp;&nbsp;&nbsp;&nbsp;采购计划已生成，请各产品线及市场负责人于下午3点之前更新补充采购计划");
				new Thread(){
					public void run(){
						 mailManager.send(mailInfo);
					}
				}.start();
			};
		}catch(Exception ex){
			LOGGER.error(ex.getMessage());
			ex.printStackTrace();
		}
		LOGGER.info("每周二早10点生成预测下单数据结束！");
	}
	
}


