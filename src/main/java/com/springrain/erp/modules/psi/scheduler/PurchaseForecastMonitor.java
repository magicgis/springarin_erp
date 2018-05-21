package com.springrain.erp.modules.psi.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.springrain.erp.modules.psi.service.PurchaseForecastService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;

public class PurchaseForecastMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseForecastMonitor.class);
	
	@Autowired
	private PurchaseForecastService  purchaseForecastService ;
 
	@Autowired
	private LcPsiTransportOrderService lcPsiTransportOrderService;
	
	//每天对财务报表整合表进行更新
	public void createForecastData() {
		LOGGER.info("每天生成预测数据开始！");
		try{
			purchaseForecastService.createForecastData();
		}catch(Exception ex){
			LOGGER.error(ex.getMessage());
			ex.printStackTrace();
		}
		LOGGER.info("每天生成预测数据结束！");
	}
	
	public void countTranDaysData() {
		LOGGER.info("countTranDaysData start！");
		lcPsiTransportOrderService.countTransDays2();
		LOGGER.info("countTranDaysData end！");
	}
	
	public static void main(String[] args) {
		   ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		   LcPsiTransportOrderService lcPsiTransportOrderService = applicationContext.getBean(LcPsiTransportOrderService.class);
		   PurchaseForecastMonitor monitor=new PurchaseForecastMonitor();
		   monitor.setLcPsiTransportOrderService(lcPsiTransportOrderService);
		   monitor.countTranDaysData();
		   applicationContext.close();
	}

	public LcPsiTransportOrderService getLcPsiTransportOrderService() {
		return lcPsiTransportOrderService;
	}

	public void setLcPsiTransportOrderService(
			LcPsiTransportOrderService lcPsiTransportOrderService) {
		this.lcPsiTransportOrderService = lcPsiTransportOrderService;
	}
	
	
}


