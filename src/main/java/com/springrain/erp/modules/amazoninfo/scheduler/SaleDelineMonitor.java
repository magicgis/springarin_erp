package com.springrain.erp.modules.amazoninfo.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MapValueComparator;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.psi.scheduler.PurchaseOrderDeliveryRateMonitor;

public class SaleDelineMonitor {
	@Autowired  
	private SaleReportService   saleService; 
	@Autowired
	private MailManager 	mailManager;
	@Autowired
	private PurchaseOrderDeliveryRateMonitor  orderDeliveryMonitor;  
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private void saleWarn(){
		logger.info("连续两个月销量下滑扫描-开始...");    
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		    Date endDay = DateUtils.getFirstDayOfMonth(new Date());
		    String thirdMonth=sdf.format(DateUtils.addMonths(endDay, -3));//三个月前
		    String secondMonth=sdf.format(DateUtils.addMonths(endDay, -2));
		    String firstMonth=sdf.format(DateUtils.addMonths(endDay, -1));
			
			Map<String,Map<String,String>> rsMap= saleService.getSaleWarnByMonth();
			
			StringBuffer contents= new StringBuffer("");
			if(rsMap!=null&&rsMap.size()>0){
				contents.append("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;以下是连续两个月销量下滑大于15%的产品（Tips:新品不统计,欧洲、美国市场前两个月销量小于150、日本市场小于100的不统计）：<br/><table width='90%' style='border:1px solid #cad9ea;color:#666; '>" +
						"<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>国家</th><th>产品名</th>" +
						"<th>"+thirdMonth+"销量</th><th>"+secondMonth+"销量</th><th>"+firstMonth+"销量</th><th>下滑率</th></tr>");
				String coutryStr="eu,com,jp";
				for(String country:coutryStr.split(",")){
					if(rsMap.get(country)==null){
						continue;
					}
					
					Map<String,String> productMap = rsMap.get(country);
					Map<String,Integer> delineMap= Maps.newHashMap();
					//加入排序
					for (Map.Entry<String,String> entry : productMap.entrySet()) { 
					    String productName=entry.getKey();
						String info = entry.getValue();
						String arr[] = info.split(",");
						delineMap.put(productName, Integer.parseInt(arr[3]));
					}
					List<String> productNames= Lists.newArrayList();
					MapValueComparator bvc =  new MapValueComparator(delineMap,false);  
					TreeMap<String,Integer> sortKeyMap = new TreeMap<String,Integer>(bvc);  
					sortKeyMap.putAll(delineMap); 
			        for(String sortKey:sortKeyMap.keySet()){
			        	productNames.add(sortKey);
			        }
					
					for(int i=0;i<productNames.size();i++){
						String productName = productNames.get(i);
						String info = productMap.get(productName);
						String arr[] = info.split(",");
						Integer thirdQ = Integer.parseInt(arr[0]);
						Integer secondQ = Integer.parseInt(arr[1]);
						Integer firstQ = Integer.parseInt(arr[2]);
						Integer rate   = Integer.parseInt(arr[3]);
						String color="#f5fafe";
						if(i==0&&!"eu".equals(country)){
							color="#99CCFF";
						}
						contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:"+color+"; '><td>"+("com".equals(country)?"us":country)+
								"</td><td>"+productName+"</td><td>"+thirdQ+"</td><td>"+secondQ+"</td><td>"+firstQ+"</td><td>"+rate+"%</td></tr>");
					}
				}
				contents.append("</table>");
				Date date = new Date();
				//发信给德国仓库人员：
				String toAddress="amazon-sales@inateck.com,tim@inateck.com,pmg@inateck.com";
				final MailInfo mailInfo = new MailInfo(toAddress,"连续两个月销量下滑预警15%"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent(contents.toString());
				new Thread(){
					public void run(){
						 mailManager.send(mailInfo);
					}
				}.start();
			}
		}catch(Exception e){
			logger.error("连续两个月销量下滑监控异常"+e);
		}
		logger.info("连续两个月销量下滑扫描-结束...");   
		
		
		//发送上个月异常邮件
		/*try{
			orderDeliveryMonitor.sendEmailDeliveryRate();
		}catch(Exception ex){
			logger.error("月初发送上月异常收货信息异常...."); 
		}
		*/
		
		
		
	}

	
	
}
