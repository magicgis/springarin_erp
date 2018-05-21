package com.springrain.erp.modules.amazoninfo.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonCatalogRank;
import com.springrain.erp.modules.amazoninfo.service.AdvertisingService;
import com.springrain.erp.modules.psi.entity.PsiSkuChangeBill;
import com.springrain.erp.modules.psi.entity.PurchaseOrderItem;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiSkuChangeBillService;


//广告里面sku切换条码提醒
public class AdSkuMonitor {
	private final static Logger LOGGER = LoggerFactory.getLogger(AdSkuMonitor.class);
	@Autowired
	private PsiInventoryFbaService  fbaService ;
	@Autowired
	private AdvertisingService      adService;
	@Autowired
	private PsiProductService       productService;
	@Autowired
	private MailManager             mailManager;

	
	//每天监控未确认skuChange
	public void adSkuWarning() throws ParseException {
		LOGGER.info("广告sku开始...");
		Map<String,Date> dateMap = this.adService.getCountryDateMap();
		Set<String> adSkus=Sets.newHashSet();
		for (Map.Entry<String,Date> entry : dateMap.entrySet()) { 
		    String country=entry.getKey();
			//3天以内没结束的
			List<String> adList =this.adService.getOneDaySku(country,entry.getValue());
			if(adList!=null&&adList.size()>0){
				adSkus.addAll(adList);
			}
		}
		
		Map<String,String> bangSku=productService.getBandingSkuProduct();
		Set<String> bangSet = bangSku.keySet();
		Set<String> noUseSet = Sets.newHashSet();
		Set<String> useSet = Sets.newHashSet();
		for(String sku :adSkus){
			if(bangSet.contains(sku)){
				useSet.add(sku);
			}else{
				noUseSet.add(sku);
			}
		}
		StringBuffer contents= new StringBuffer("");
		if(useSet.size()>0){
			//sku:31天销
			Map<String,Integer> sku30Days=fbaService.get31SalesQuantity(useSet);
			//库存
			Map<String,Integer> fbaMap=fbaService.getFbaInventroy(useSet);
			List<String> warnSkus =Lists.newArrayList();
			for (Map.Entry<String,Integer> entry : fbaMap.entrySet()) {  
				   String sku=entry.getKey();
				   Integer quantity =entry.getValue();  
				   if(quantity!=null&&sku30Days.get(sku)!=null&&(sku30Days.get(sku).intValue()!=0)&&(quantity*31/sku30Days.get(sku))<=5){
						warnSkus.add(sku);
				   }
			}  
			
			if(warnSkus.size()>0){
				contents.append("可售5天以内的sku见下表:");
				contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;width:100%' cellpadding='0' cellspacing='0' >");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:20%'>sku</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:5%'>sku</th>");
				contents.append("</tr>");
				for(int i =0;i<warnSkus.size();i++){
					String sku=warnSkus.get(i);
					int index = i+1;
					if(index%2==1){
						contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+sku+"</td>");
					}
					if(index%2==0||index==warnSkus.size()){
						if(index%2==1){
							contents.append("<td></td></tr>");
						}else{
							contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+sku+"</td></tr>");
						}
					}
				}
				contents.append("</table><br/>");
			}
			if(noUseSet.size()>0){
				contents.append("已弃用的sku见下表:");
				contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;width:100%' cellpadding='0' cellspacing='0' >");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:20%'>已弃用sku</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:5%'>目前使用sku</th>");
				contents.append("</tr>");
				for(String sku :noUseSet){
					String bangdingSku=this.productService.getBangSkuBySku(sku);
					if(bangdingSku==null){
						bangdingSku="未绑定";
					}
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+sku+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+bangdingSku+"</td></tr>");
				}
				contents.append("</table>");
					
				}
			}
			        
			if(StringUtils.isNotEmpty(contents.toString())){
				Date date = new Date();
				String toAddress="ted@inateck.com,tim@inateck.com";
				final MailInfo mailInfo = new MailInfo(toAddress,"广告弃用sku提醒"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent("Hi,All<br/>"+contents.toString());
				new Thread(){
					public void run(){
						 mailManager.send(mailInfo);
					}
				}.start();
				
			}
		LOGGER.info("广告sku结束...");
	}
	
}
