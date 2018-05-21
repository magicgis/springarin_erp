package com.springrain.erp.modules.psi.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderItem;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderItem;
import com.springrain.erp.modules.psi.service.PsiTransportOrderService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

/**
 * 运单入库时间变更监控
 * @author lee
 * @date 2016-8-3
 */
public class TransportOrderChangeMonitor {
	
	private final static Logger logger = LoggerFactory.getLogger(TransportOrderChangeMonitor.class);
	
	@Autowired
	private PsiTransportOrderService psiTransportOrderService;

	@Autowired
	private LcPsiTransportOrderService lcPsiTransportOrderService;
	
	@Autowired
	private MailManager mailManager;
	
	private final static StringBuffer head = new StringBuffer();
	private final static Map<String, String> transportType = Maps.newHashMap();
	
	static{
		head.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
		head.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>运单号</th>");
		head.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>ShipmentId</th>");
		head.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>运输类型</th>");
		head.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>时间变更记录</th>");
		head.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>产品</th>");
		head.append("</tr>");
		transportType.put("0", "本地运输");
		transportType.put("1", "Fba运输");
		transportType.put("3", "线下运输");
	}

	public void monitor() {
		try {
			logger.info("开始查询运单入库时间变更记录...");
			List<PsiTransportOrder> changes = psiTransportOrderService.findInStockChanges();
			List<LcPsiTransportOrder> lcChanges = lcPsiTransportOrderService.findInStockChanges();
			StringBuffer contents = new StringBuffer("");
			if (changes.size() > 0 || lcChanges.size() > 0) {	//有变更,发送邮件通知
				contents.append("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;以下是运单入库时间变更记录,请知悉.<br/><table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' >");
				contents.append(head.toString());
				for(PsiTransportOrder psiTransportOrder : changes){
					StringBuilder products = new StringBuilder();
					for (PsiTransportOrderItem item : psiTransportOrder.getItems()) {
						String country = "com".equals(item.getCountryCode())?"US":item.getCountryCode().toUpperCase();
						String productName = item.getProductName().split(" ")[1];
						if (StringUtils.isNotEmpty(item.getColorCode())) {
							productName = productName + "_" + item.getColorCode();
						}
						products.append(productName).append("_").append(country).append("<br/>");
					}
					if (StringUtils.isNotEmpty(products)) {
						products = new StringBuilder(products.substring(0, products.length() - 5));
					}
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>" +
							"<td style='border-left:1px solid black;border-top:1px solid;color:black;'><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/psiTransportOrder/view?id="+psiTransportOrder.getId()+"'>"+psiTransportOrder.getTransportNo()+"</a></td>" +
							"<td style='border-left:1px solid black;border-top:1px solid;color:black;'><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/fbaInbound?country=&shipmentId="+psiTransportOrder.getShipmentId()+"'>"+(StringUtils.isNotEmpty(psiTransportOrder.getShipmentId())?psiTransportOrder.getShipmentId():"")+"</a></td>" +
							"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+transportType.get(psiTransportOrder.getTransportType())+"</td>" +
							"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+psiTransportOrder.getChangeRecord()+"</td>" +
							"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+products+"</td>" +
							"</tr>");
				}
				for(LcPsiTransportOrder psiTransportOrder : lcChanges){
					StringBuilder products = new StringBuilder();
					for (LcPsiTransportOrderItem item : psiTransportOrder.getItems()) {
						String country = "com".equals(item.getCountryCode())?"US":item.getCountryCode().toUpperCase();
						String productName = item.getProductName().split(" ")[1];
						if (StringUtils.isNotEmpty(item.getColorCode())) {
							productName = productName + "_" + item.getColorCode();
						}
						products.append(productName).append("_").append(country).append("<br/>");
					}
					if (StringUtils.isNotEmpty(products)) {
						products = new StringBuilder(products.substring(0, products.length() - 5));
					}
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>" +
							"<td style='border-left:1px solid black;border-top:1px solid;color:black;'><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPsiTransportOrder/view?id="+psiTransportOrder.getId()+"'>"+psiTransportOrder.getTransportNo()+"</a></td>" +
							"<td style='border-left:1px solid black;border-top:1px solid;color:black;'><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/fbaInbound?country=&shipmentId="+psiTransportOrder.getShipmentId()+"'>"+(StringUtils.isNotEmpty(psiTransportOrder.getShipmentId())?psiTransportOrder.getShipmentId():"")+"</a></td>" +
							"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+transportType.get(psiTransportOrder.getTransportType())+"</td>" +
							"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+psiTransportOrder.getChangeRecord()+"</td>" +
							"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+products+"</td>" +
							"</tr>");
				}
				contents.append("</table>");
				if (changes.size() > 0) {
					psiTransportOrderService.clearChangeRecord();
				}
				if (lcChanges.size() > 0) {
					lcPsiTransportOrderService.clearChangeRecord();
				}
			}
			if(StringUtils.isNotEmpty(contents)){
				Date date = new Date();
				//发信给销售人员：
				String toAddress="amazon-sales@inateck.com";
				final MailInfo mailInfo = new MailInfo(toAddress,"运单入库时间变更提醒"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent(contents.toString());
				mailInfo.setCcToAddress("leehong@inateck.com,tim@inateck.com");
				new Thread(){
					public void run(){
						 mailManager.send(mailInfo);
					}
				}.start();
			}
			logger.info("运单入库时间变更记录处理结束！");
		} catch (Exception e) {
			logger.error("运单入库时间变更记录处理异常！", e);
			WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "运单入库时间变更记录处理异常！");
		}
	}
	
	
	public void  sendNextWeekArrvialNewProduct(){
		List<Object[]> list=lcPsiTransportOrderService.findArrvalNextWeek();
		if(list!=null&&list.size()>0){
			StringBuffer contents = new StringBuffer("");
			contents.append("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;The following product will be arrive in Germany on next week.<br/>");
			contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' >");
			contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
			contents.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>Transport No</th>");
			contents.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>Arrival Date</th>");
			contents.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>Model</th>");
			contents.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>ProductName</th>");
			contents.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>Country</th>");
			contents.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>Quantity</th>");
			contents.append("</tr>");
			for (Object[] obj: list) {
				String model="";
				if("0".equals(obj[2].toString())){
					model="Air";
				}else if("1".equals(obj[2].toString())){
					model="Sea";
				}else if("2".equals(obj[2].toString())){
					model="Express";
				}else if("3".equals(obj[2].toString())){
					model="Train";
				}
				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>" +
						"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+obj[0].toString()+"</td>" +
						"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+(obj[1]==null?"":obj[1].toString())+"</td>" +
						"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+model+"</td>" +
						"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+obj[3].toString()+"</td>" +
						"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+obj[4].toString()+"</td>" +
						"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+obj[5].toString()+"</td>" +
						"</tr>");
			}
			contents.append("</table>");
			if(StringUtils.isNotEmpty(contents)){
				Date date = new Date();
				//发信给销售人员：
				String toAddress="stephan.seidel@inateck.com,george@inateck.com,bella@inateck.com,dewarehouse@inateck.com,"+UserUtils.logistics1;
				final MailInfo mailInfo = new MailInfo(toAddress,"Labelling Request for New Products"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent(contents.toString());
				mailInfo.setCcToAddress("eileen@inateck.com");
				new Thread(){
					public void run(){
						 mailManager.send(mailInfo);
					}
				}.start();
			}
		}
		
		list=lcPsiTransportOrderService.findArrvalNextWeekUS();
		if(list!=null&&list.size()>0){
			StringBuffer contents = new StringBuffer("");
			contents.append("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;The following product will be arrive in US on next week.<br/>");
			contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' >");
			contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
			contents.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>Transport No</th>");
			contents.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>Arrival Date</th>");
			contents.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>Model</th>");
			contents.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>ProductName</th>");
			contents.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>Country</th>");
			contents.append("<th style='border-left:1px solid black;border-top:1px solid;color:black;'>Quantity</th>");
			contents.append("</tr>");
			for (Object[] obj: list) {
				String model="";
				if("0".equals(obj[2].toString())){
					model="Air";
				}else if("1".equals(obj[2].toString())){
					model="Sea";
				}else if("2".equals(obj[2].toString())){
					model="Express";
				}else if("3".equals(obj[2].toString())){
					model="Train";
				}
				
				
				
				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>" +
						"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+obj[0].toString()+"</td>" +
						"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+(obj[1]==null?"":obj[1].toString())+"</td>" +
						"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+model+"</td>" +
						"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+obj[3].toString()+"</td>" +
						"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+("com2".equals(obj[4].toString())?"USNew":("com3".equals(obj[4].toString())?"US_Tomons":obj[4].toString()))+"</td>" +
						"<td style='border-left:1px solid black;border-top:1px solid;color:black;'>"+obj[5].toString()+"</td>" +
						"</tr>");
			}
			contents.append("</table>");
			if(StringUtils.isNotEmpty(contents)){
				Date date = new Date();
				//发信给销售人员：
				String toAddress="bradley@inateck.com,lena@inateck.com,"+UserUtils.logistics1;
				final MailInfo mailInfo = new MailInfo(toAddress,"Labelling Request for New Products"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent(contents.toString());
				mailInfo.setCcToAddress("eileen@inateck.com");
				new Thread(){
					public void run(){
						 mailManager.send(mailInfo);
					}
				}.start();
			}
		}
		
		
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		PsiTransportOrderService  psiTransportOrderService= applicationContext.getBean(PsiTransportOrderService.class);
		LcPsiTransportOrderService  lcPsiTransportOrderService= applicationContext.getBean(LcPsiTransportOrderService.class);
		TransportOrderChangeMonitor monitor = new TransportOrderChangeMonitor();
		monitor.setLcPsiTransportOrderService(lcPsiTransportOrderService);
		monitor.setPsiTransportOrderService(psiTransportOrderService);
		monitor.sendNextWeekArrvialNewProduct();
		applicationContext.close();
	}

	public PsiTransportOrderService getPsiTransportOrderService() {
		return psiTransportOrderService;
	}

	public void setPsiTransportOrderService(
			PsiTransportOrderService psiTransportOrderService) {
		this.psiTransportOrderService = psiTransportOrderService;
	}

	public LcPsiTransportOrderService getLcPsiTransportOrderService() {
		return lcPsiTransportOrderService;
	}

	public void setLcPsiTransportOrderService(
			LcPsiTransportOrderService lcPsiTransportOrderService) {
		this.lcPsiTransportOrderService = lcPsiTransportOrderService;
	}
	
}
