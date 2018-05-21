package com.springrain.erp.modules.psi.scheduler;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.scheduler.SendCustomEmail1Manager;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiLadingBillService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductInStockService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiTransportForecastOrderService;
import com.springrain.erp.modules.psi.service.lc.LcPsiLadingBillService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;

@Component
public class PurchaseOrderDeliveryRateMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderDeliveryRateMonitor.class);
	@Autowired
	private MailManager mailManager;
	@Autowired
	private PsiLadingBillService	 	psiLadingBillService;
	@Autowired
	private LcPsiLadingBillService	 	lcPsiLadingBillService;
	@Autowired
	private PsiTransportForecastOrderService	psiTransportForecastOrderService;
	@Autowired
	private PsiProductService	psiProductService;
	@Autowired
	private PsiInventoryService	psiInventoryService;
	@Autowired
	private PsiInventoryFbaService	psiInventoryFbaService;
	@Autowired
	private PsiProductInStockService	psiProductInStockService;
	@Autowired
	private PsiProductAttributeService    psiProductAttributeService;
	@Autowired
	private SaleReportService	saleReportService;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	@Autowired
	private SendCustomEmail1Manager sendCustomEmail1Manager;
	@Autowired
	private LcPsiTransportOrderService 		psiTransportOrderService;
	@Autowired
	private PsiProductService        	productService;
	public void sendEmailDeliveryRate() {  
		LOGGER.info("开始查询采购交期异常订单...");
		List<Object[]> list=psiLadingBillService.getExciptionPurchaseOrder();
		List<Object[]> list1=lcPsiLadingBillService.getExciptionPurchaseOrder();
		StringBuffer contents= new StringBuffer("");
		Set<String> set=Sets.newHashSet();
		if((list!=null&&list.size()>0)||(list1!=null&&list1.size()>0)){
			contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
			contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>采购员</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>供应商名称</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>订单编号</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>国家</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>产品型号</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>订单交期</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>预计交期</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>可验货日期</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>确认收货日期</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>超期天数</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>备注</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>原因</th>");
			contents.append("</tr>");
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
		    	    String key=obj[2]+"_"+obj[3]+"_"+obj[8]+"_"+obj[4];
		    	    if(set.contains(key)){
		    	    	continue;
		    	    }else{
		    	    	 set.add(key);
		    	    }
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[2]+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[3]+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/purchaseOrder/view?id="+obj[10]+"'>"+obj[8]+"</a></td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(("com".equals(obj[9])?"us":obj[9]).toString().toUpperCase())+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[4]+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[5]+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(obj[6]==null?"":obj[6])+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[14]+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(obj[12]==null?"":obj[12])+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[7]+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+("1".equals(obj[11])?"超过订单交期14天新品":("0".equals(obj[11])?"超过订单交期7天其它产品":"超过预计交期"))+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[13]+"</td>");
					contents.append("</tr>"); 
		       }
			}
					
		    
            if(list1!=null&&list1.size()>0){
            	 for (Object[] obj: list1) {
     	    	    String key=obj[2]+"_"+obj[3]+"_"+obj[8]+"_"+obj[4];
     	    	    if(set.contains(key)){
     	    	    	continue;
     	    	    }else{
     	    	    	 set.add(key);
     	    	    }
     				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
     				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[2]+"</td>");
     				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[3]+"</td>");
     				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/purchaseOrder/view?id="+obj[10]+"'>"+obj[8]+"</a></td>");
     				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(("com".equals(obj[9])?"us":obj[9]).toString().toUpperCase())+"</td>");
     				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[4]+"</td>");
     				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[5]+"</td>");
     				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(obj[6]==null?"":obj[6])+"</td>");
     				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[14]+"</td>");
     				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(obj[12]==null?"":obj[12])+"</td>");
     				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[7]+"</td>");
     				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+("1".equals(obj[11])?"超过订单交期14天新品":("0".equals(obj[11])?"超过订单交期7天其它产品":"超过预计交期"))+"</td>");
     				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+obj[13]+"</td>");
     				contents.append("</tr>"); 
     	       }
			}
		  

		  contents.append("</table><br/>");
		
		if(StringUtils.isNotEmpty(contents)){
			Date date = new Date();
			String  toAddress="supply-chain@inateck.com";
			final MailInfo mailInfo = new MailInfo(toAddress,"采购订单交期异常预警"+new SimpleDateFormat("yyyyMMdd").format(DateUtils.addMonths(new Date(), -1))+"准时交付率统计",date);
			mailInfo.setContent(contents.toString());
		    mailInfo.setCcToAddress("eileen@inateck.com,tim@inateck.com");
			new Thread(){
				public void run(){
					sendCustomEmail1Manager.send(mailInfo);
//					new MailManager().send(mailInfo);
				}
			}.start();
		}
		 }
		LOGGER.info("查询采购订单交期结束！");
	}

	public void sendTaxEmail(){
		
		SimpleDateFormat dateFormat=new SimpleDateFormat("MM");
    	Date today=new Date();
    	String month=dateFormat.format(today);
    	SaleReport saleReport=new SaleReport();
    	today=DateUtils.addMonths(today,-1);
		if("04".equals(month)||"07".equals(month)||"10".equals(month)||"01".equals(month)){
			saleReport.setEnd(DateUtils.getLastDayOfMonth(today));
			today=DateUtils.addMonths(today,-2);
			saleReport.setStart(DateUtils.getFirstDayOfMonth(today));
		}else{
			saleReport.setStart(DateUtils.getFirstDayOfMonth(today));
			saleReport.setEnd(DateUtils.getLastDayOfMonth(today));
		}
		Map<String, Map<String, SaleReport>>  data = saleReportService.getSalesByCountry(saleReport);
		List<String> countryList=Lists.newArrayList("eu","com","uk","jp","ca");
    	StringBuffer contents= new StringBuffer("");
    	contents.append("<table width='100%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
		contents.append("<tr  style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
		contents.append("<th  style='border-left:1px solid;border-top:1px solid;color:#666;'>月份</th>");
		contents.append("<th  style='border-left:1px solid;border-top:1px solid;color:#666;'>德国(€)</th>");
		contents.append("<th  style='border-left:1px solid;border-top:1px solid;color:#666;'>美国($)</th>");
		contents.append("<th  style='border-left:1px solid;border-top:1px solid;color:#666;'>英国(￡)</th>");
		contents.append("<th  style='border-left:1px solid;border-top:1px solid;color:#666;'>日本(¥)</th>");
		contents.append("<th  style='border-left:1px solid;border-top:1px solid;color:#666;'>加拿大(C$)</th>");
		contents.append("</tr>");

		
		
		for (Map.Entry<String,Map<String,SaleReport>>  map:data.entrySet()) {
            if(!"total".equals(map.getKey())){
            	contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
    			contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+map.getKey()+"</td>");
    			Map<String,SaleReport> temp=map.getValue();
    			for(String country:countryList){
    				Float sales=0f;
    				if(temp!=null&&temp.get(country)!=null){
    					sales=temp.get(country).getSales();
    				}
    				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+sales+"</td>");
    			}
    			contents.append("</tr>");
			}
		}
		
		
		if("04".equals(month)||"07".equals(month)||"10".equals(month)||"01".equals(month)){
			contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
			contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>Total</td>");
			Map<String,SaleReport> temp=data.get("total");
			for(String country:countryList){
				Float sales=0f;
				if(temp!=null&&temp.get(country)!=null){
					sales=temp.get(country).getSales();
				}
				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+sales+"</td>");
			}
			contents.append("</tr>");
		}
		
		contents.append("</table>");
		Date date = new Date();
		String  toAddress="maik@inateck.com,emma.chao@inateck.com";
		final MailInfo mailInfo = new MailInfo(toAddress,"增值税资金计划"+new SimpleDateFormat("yyyyMM").format(new Date()),date);
		mailInfo.setContent(contents.toString());
	    mailInfo.setCcToAddress("eileen@inateck.com");
		new Thread(){
			public void run(){
				new MailManager().send(mailInfo);
			}
		}.start();
	}

	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		SaleReportService saleReportService = applicationContext.getBean(SaleReportService.class);
		PurchaseOrderDeliveryRateMonitor aarehouseDeProductMonitor = new PurchaseOrderDeliveryRateMonitor();
		aarehouseDeProductMonitor.setSaleReportService(saleReportService);
		PsiProductService productService=applicationContext.getBean(PsiProductService.class);
		aarehouseDeProductMonitor.setProductService(productService);
		
		LcPsiTransportOrderService psiTransportOrderService =applicationContext.getBean(LcPsiTransportOrderService.class);
		aarehouseDeProductMonitor.setPsiTransportOrderService(psiTransportOrderService);
		aarehouseDeProductMonitor.sendTaxEmail();
		applicationContext.close();
	}



	public LcPsiTransportOrderService getPsiTransportOrderService() {
		return psiTransportOrderService;
	}

	public void setPsiTransportOrderService(
			LcPsiTransportOrderService psiTransportOrderService) {
		this.psiTransportOrderService = psiTransportOrderService;
	}

	public PsiTransportForecastOrderService getPsiTransportForecastOrderService() {
		return psiTransportForecastOrderService;
	}

	public void setPsiTransportForecastOrderService(
			PsiTransportForecastOrderService psiTransportForecastOrderService) {
		this.psiTransportForecastOrderService = psiTransportForecastOrderService;
	}

	public SaleReportService getSaleReportService() {
		return saleReportService;
	}

	public void setSaleReportService(SaleReportService saleReportService) {
		this.saleReportService = saleReportService;
	}

	public PsiProductService getProductService() {
		return productService;
	}

	public void setProductService(PsiProductService productService) {
		this.productService = productService;
	}

	public PsiLadingBillService getPsiLadingBillService() {
		return psiLadingBillService;
	}



	public void setPsiLadingBillService(PsiLadingBillService psiLadingBillService) {
		this.psiLadingBillService = psiLadingBillService;
	}



	
}
