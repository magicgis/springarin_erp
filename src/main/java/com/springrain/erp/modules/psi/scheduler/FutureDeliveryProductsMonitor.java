package com.springrain.erp.modules.psi.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.BusinessReportService;
import com.springrain.erp.modules.psi.service.PurchaseOrderService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;

public class FutureDeliveryProductsMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(FutureDeliveryProductsMonitor.class);
	@Autowired
	private PurchaseOrderService    purchaseOrderService ;
	@Autowired
	private LcPurchaseOrderService  lcPurchaseOrderService ;
	@Autowired
	private MailManager			    mailManager;  
	@Autowired
	private BusinessReportService	businessService;  
	@Autowired
	private SystemService systemService;  
	/**
	 *未来9-15天收货产品提示邮件 
	 * @throws ParseException 
	 */
	public void futureDeliveryProducts() throws ParseException {
		LOGGER.info("周一生成未来9-15天收货的数据开始！");
		/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date todayDate = sdf.parse(sdf.format(new Date()));
		Date startDate=DateUtils.addDays(todayDate, 9);
		try{
			List<Object[]> objs=Lists.newArrayList();
			List<Object[]> objs1=purchaseOrderService.getLastDeliveryProducts(startDate);
			List<Object[]> objs2=lcPurchaseOrderService.getLastDeliveryProducts(startDate);
			Map<String, StringBuffer> merchandiserMap = Maps.newHashMap();
			if(objs1!=null&&objs1.size()>0){
				objs.addAll(objs1);
			}
			if(objs2!=null&&objs2.size()>0){
				objs.addAll(objs2);	
			}
			StringBuffer headBuffer = new StringBuffer("");
			headBuffer.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
			headBuffer.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
			headBuffer.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>产品名</th>");
			headBuffer.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>国家</th>");
			headBuffer.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>收货数量</th>");
			headBuffer.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>收货时间</th>");
			headBuffer.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>距收货天数</th>");
			headBuffer.append("</tr>");
			StringBuffer contents= new StringBuffer("");
			if(objs.size()>0){
			    for (Object[] obj: objs) {
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[0]+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(("com".equals(obj[1].toString())?"us":obj[1].toString()).toUpperCase())+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[2]+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[3]+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+DateUtils.spaceDays(todayDate, sdf.parse(obj[3].toString()))+"</td>");
					contents.append("</tr>");
			    	String userId = obj[4]==null?"":obj[4].toString();
					if (StringUtils.isEmpty(userId)) {
						continue;
					}
			    	StringBuffer merchandiserBuff = merchandiserMap.get(userId);
			    	if (merchandiserBuff == null) {
			    		merchandiserBuff = new StringBuffer("");
			    		merchandiserMap.put(userId, merchandiserBuff);
					}
			    	merchandiserBuff.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
			    	merchandiserBuff.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[0]+"</td>");
			    	merchandiserBuff.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(("com".equals(obj[1].toString())?"us":obj[1].toString()).toUpperCase())+"</td>");
			    	merchandiserBuff.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[2]+"</td>");
			    	merchandiserBuff.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[3]+"</td>");
			    	merchandiserBuff.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+DateUtils.spaceDays(todayDate, sdf.parse(obj[3].toString()))+"</td>");
			    	merchandiserBuff.append("</tr>");
			    }
				contents.append("</table><br/>");
				if(StringUtils.isNotEmpty(contents)){
					Date date = new Date();
					String sendContent = headBuffer.toString() + contents.toString();
					String  toAddress="amazon-sales@inateck.com";
					final MailInfo mailInfo = new MailInfo(toAddress,"未来9-15天要收货的产品",date);
					mailInfo.setContent(sendContent);
					new Thread(){
						public void run(){
							mailManager.send(mailInfo);
						}
					}.start();
				}
				for (Entry<String, StringBuffer> entry : merchandiserMap.entrySet()) {
					if (StringUtils.isNotEmpty(entry.getValue())) {
						User user = systemService.getUser(entry.getKey());
						Date date = new Date();
						String  toAddress=user.getEmail();
						String sendContent = headBuffer.toString() + entry.getValue().toString() + "</table><br/>";
						final MailInfo mailInfo = new MailInfo(toAddress,"未来9-15天要收货的产品",date);
						mailInfo.setContent(sendContent);
						new Thread(){
							public void run(){
								mailManager.send(mailInfo);
							}
						}.start();
					}
				}
			}
		}catch(Exception ex){
			LOGGER.error("周一生成未来9-15天收货的数据结束！", ex);
		}*/
		try{
			businessService.sendSessionWarnEmail();
		}catch(Exception ex){
			LOGGER.error("sendSessionWarnEmail异常！", ex);
		}
		
		LOGGER.info("周一生成未来9-15天收货的数据结束！");
	}
	
}


