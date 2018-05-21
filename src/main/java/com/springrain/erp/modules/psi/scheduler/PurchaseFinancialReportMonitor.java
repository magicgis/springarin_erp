package com.springrain.erp.modules.psi.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.springrain.erp.modules.psi.service.PsiLadingBillService;
import com.springrain.erp.modules.psi.service.PurchaseFinancialService;
import com.springrain.erp.modules.psi.service.lc.LcPsiLadingBillService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseFinancialService;

public class PurchaseFinancialReportMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseFinancialReportMonitor.class);
	
	@Autowired
	private PurchaseFinancialService  purchaseFinancialService ;
	@Autowired
	private LcPurchaseFinancialService  lcPurchaseFinancialService ;
	@Autowired
	private PsiLadingBillService  			 ladingBillService;
	@Autowired
	private LcPsiLadingBillService 			 lcLadingBillService;

	//每天对财务报表整合表进行更新
	public void createFinaData() {
		LOGGER.info("生成财务报表数据开始！");
		try{
			purchaseFinancialService.createFinancicalReprotData();
			//理诚
			lcPurchaseFinancialService.createFinancicalReprotData();
		}catch(Exception ex){
			LOGGER.error(ex.getMessage());
			ex.printStackTrace();
		}
		LOGGER.info("生成财务报表数据结束！");
		//每天算下供应商逾期率开始
		try{
			LOGGER.info("每天算提单付款预期时间开始...");
			ladingBillService.updatePayDelayDays();
			lcLadingBillService.updatePayDelayDays();
			LOGGER.info("每天算提单付款预期时间结束...");
		}catch(Exception ex){
			LOGGER.error("每天算提单付款预期时间异常...");
		}
	}
	
}


