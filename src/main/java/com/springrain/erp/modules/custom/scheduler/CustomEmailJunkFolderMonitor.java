package com.springrain.erp.modules.custom.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.springrain.erp.common.email.MailManager;

public class CustomEmailJunkFolderMonitor {
	
	@Autowired
	private CustomEmailManager customEmailManager;
	
/*	@Autowired
	private CustomEmailBakManager customEmailBakManager;
	
	@Autowired
	private CustomUSAEmailManager customUSAEmailManager;
	
	@Autowired
	private CustomJPEmailManager customJPEmailManager;
	
	@Autowired
	private MailManager mailManager;*/
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CustomEmailJunkFolderMonitor.class);
	
	public void monitor(){
		
		try {
			customEmailManager.moveEmailToInbox();
		} catch (Exception e) {
			LOGGER.error(customEmailManager.getManagerInfo().getUserName()+":"+e.getMessage(),e);
			e.printStackTrace();
		}
		
		/*try {
			customEmailBakManager.moveEmailToInbox();
		} catch (Exception e) {
			LOGGER.error(customEmailBakManager.getManagerInfo().getUserName()+":"+e.getMessage(),e);
			e.printStackTrace();
		}
		
		try {
			customUSAEmailManager.moveEmailToInbox();
		} catch (Exception e) {
			LOGGER.error(customUSAEmailManager.getManagerInfo().getUserName()+":"+e.getMessage(),e);
			e.printStackTrace();
		}
		
		try {
			customJPEmailManager.moveEmailToInbox();
		} catch (Exception e) {
			LOGGER.error(customJPEmailManager.getManagerInfo().getUserName()+":"+e.getMessage(),e);
			e.printStackTrace();
		}
		

		try {
			mailManager.moveEmailToInbox();
		} catch (Exception e) {
			LOGGER.error(mailManager.getManagerInfo().getUserName()+":"+e.getMessage(),e);
			e.printStackTrace();
		}*/
	}

}
