package com.springrain.erp.modules.psi.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.psi.entity.PsiSkuChangeBill;
import com.springrain.erp.modules.psi.service.FbaInboundService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiSkuChangeBillService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderService;

public class SkuChangeConfirmMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SkuChangeConfirmMonitor.class);
	@Autowired
	private PsiSkuChangeBillService  skuChangeService ;
	@Autowired
	private PsiProductService  productService ;
	@Autowired
	private MailManager mailManager;
	@Autowired
	private LcPurchaseOrderService lcOrderService;
	@Autowired
	private FbaInboundService     fbaInboundService;
	
	public void sendEmailByUnconfirmSkuChange() {
		LOGGER.info("开始查询今天未确认Sku Change infos...");
		//每天监控未确认skuChange
		List<PsiSkuChangeBill> skuChanges= skuChangeService.findSkuChangeNoSure(19);
		StringBuffer contents= new StringBuffer("");
		if(skuChanges.size()>0){
			contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>Name</th><th>Quantity</th><th>ShipmentId</th><th>Creater</th></tr>");
			Map<String,String> skuMap = this.skuChangeService.getShipmentIds();
			for(PsiSkuChangeBill skuChange:skuChanges){
				String shipmentId="";
				if(skuMap.size()>0&&skuMap.get(skuChange.getToSku())!=null){
					shipmentId=skuMap.get(skuChange.getToSku());
				}
				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+skuChange.getEvenName()+"</td><td>"+skuChange.getQuantity()+"</td><td>"+shipmentId+"</td><td>"+skuChange.getApplyUser().getName()+"</td></tr>");
			}   
			contents.append("</table>");
		}
		        
		if(StringUtils.isNotEmpty(contents)){
			Date date = new Date();
			//发信给德国仓库人员：
			String toAddress="george@inateck.com";
			final MailInfo mailInfo = new MailInfo(toAddress,"Has unconfirm sku change info"+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(contents.toString());
			mailInfo.setCcToAddress("amazon-sales@inateck.com,tim@inateck.com");
			new Thread(){
				public void run(){
					 mailManager.send(mailInfo);
				}
			}.start();
			
		}
		LOGGER.info("查询未确认Sku Change infos结束！");
		
		lcOrderService.deliveryDateChangeMonitor();
		
		//监控产品颜色变化
		monitorProductColorChange();
	}
	
	//每天监控产品颜色改变
	public void monitorProductColorChange(){
		String res=productService.getColorChangeInfo();
		if(StringUtils.isNotEmpty(res)){
			Date date = new Date();
			String toAddress="it@inateck.com";
			final MailInfo mailInfo = new MailInfo(toAddress,"采购订单里的颜色，产品信息里没有"+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(res);
			new Thread(){
				public void run(){
					 mailManager.send(mailInfo);
				}
			}.start();
		}
	}
	

}
