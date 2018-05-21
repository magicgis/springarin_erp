package com.springrain.erp.modules.psi.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.psi.entity.PsiSkuChangeBill;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiSkuChangeBillService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;

public class SkuChangeCancelMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SkuChangeCancelMonitor.class);
	@Autowired
	private PsiSkuChangeBillService  skuChangeService ;
//	@Autowired
//	private SystemService  systemService ;
	
	@Autowired
	private MailManager mailManager;

	//每天监控未确认skuChange
	public void canceledSku() {
		LOGGER.info("自动取消转码开始...");
		Map<String,User>  userMap = Maps.newHashMap();
		List<PsiSkuChangeBill> skuChanges= skuChangeService.findSkuChangeNoSure(19);
		Map<String,List<String>> sendMap = Maps.newHashMap();
		if(skuChanges.size()>0){
			Map<String,String> skuMap = this.skuChangeService.getShipmentIds();
			for(PsiSkuChangeBill skuChange:skuChanges){
				if((skuMap==null||skuMap.get(skuChange.getToSku())==null)&&!skuChange.getToSku().contains("-JP")&&!skuChange.getToSku().contains("-CA")&&!skuChange.getToSku().contains("-US")){
					//如果匹配关系不存在，并且结尾不为JP CA US   取消
					try {
						String userId = skuChange.getApplyUser().getId();
						userMap.put(userId, skuChange.getApplyUser());
						List<String> list = null;
						if(sendMap.get(userId)==null){
							list = Lists.newArrayList();
						}else{
							list = sendMap.get(userId);
						}
						list.add(skuChange.getEvenName()+" 数量: "+skuChange.getQuantity());
						this.skuChangeService.cancel(skuChange);
						sendMap.put(userId, list);
					} catch (Exception e) {
						LOGGER.error("自动取消转码("+skuChange.getToSku()+")异常:"+e.getMessage());
					}
					
				}
			}   
		}
		        
		if(sendMap.size()>0){
			for(Map.Entry<String,List<String>> userEntry:sendMap.entrySet()){
				String userId = userEntry.getKey();
				User user=userMap.get(userId);
				String toAddress=user.getEmail();
				String userName=user.getLoginName();
				Date date = new Date();
				StringBuffer contents= new StringBuffer("Hi,"+userName+"<br/>以下转码因为没有匹配到fba贴,已经强制取消:<br/>");
				for(String content:userEntry.getValue()){
					contents.append(content+"<br/>");
				}
				final MailInfo mailInfo = new MailInfo(toAddress+",tim@inateck.com","Cancel Sku Change (No Fba-Inbound) Info"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent(contents.toString());
				new Thread(){
					public void run(){
						 mailManager.send(mailInfo);
					}
				}.start();
			}
		}
		LOGGER.info("自动取消转码结束！");
	}
	
}
