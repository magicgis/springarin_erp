package com.springrain.erp.modules.psi.scheduler;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.service.PsiLadingBillService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;


public class SupplierDeliveryRateMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SupplierDeliveryRateMonitor.class);
	@Autowired
	private MailManager mailManager;
	@Autowired
	private PsiSupplierService 		 psiSupplierService;
	@Autowired
	private PsiLadingBillService     billService;

	public void sendEmailSupplierDeliveryRate() {  
		LOGGER.info("开始查询总准时交付率...");
		Map<String,Map<String,Float>> mapRate=new HashMap<String,Map<String,Float>>();
		Map<String,Map<String,Integer>> map=billService.getMapRate();//类型-供应商-计数
	   Map<String,Map<String,String>> mapRemark=billService.getMapRemark();
		List<PsiSupplier> supplierList  =this.psiSupplierService.findAll();
		List<PsiSupplier> useSupplierList=new ArrayList<PsiSupplier>();
		//类型/供应商/数据
		for (PsiSupplier supplier : supplierList) {
			String name=supplier.getNikename();
			int total=0;
			int afterDate=0;
			int beforeDate=0;
			int suitDate=0;
			int otherAfterDate=0; 
			if(map.get("0")!=null&&map.get("0").get(name)!=null){
				afterDate=map.get("0").get(name);
				total+=afterDate; 
			}
			if(map.get("1")!=null&&map.get("1").get(name)!=null){
				beforeDate=map.get("1").get(name);
				total+=beforeDate;
			}
			if(map.get("2")!=null&&map.get("2").get(name)!=null){
				suitDate=map.get("2").get(name);
				total+=suitDate;
			}
			if(map.get("3")!=null&&map.get("3").get(name)!=null){
				otherAfterDate=map.get("3").get(name);
				total+=otherAfterDate;
			}
			if(total!=0){
				useSupplierList.add(supplier);
				Map<String,Float> data=mapRate.get("0");
				if(data==null){
					data= Maps.newHashMap();
					mapRate.put("0",data);
				}
				data.put(name,total==0?0:afterDate*100f/total);
				
				Map<String,Float> data1=mapRate.get("1");
				if(data1==null){
					data1= Maps.newHashMap();
					mapRate.put("1",data1);
				}

				data1.put(name,total==0?0:beforeDate*100f/total);
				
				Map<String,Float> data2=mapRate.get("2");
				if(data2==null){
					data2= Maps.newHashMap();
					mapRate.put("2",data2);
				}
				data2.put(name,total==0?0:suitDate*100f/total);
				
				Map<String,Float> data3=mapRate.get("3");
				if(data3==null){
					data3= Maps.newHashMap();
					mapRate.put("3",data3);
				}
				data3.put(name,total==0?0:otherAfterDate*100f/total);
			}
			
		}
		List<String> typeList=Lists.newArrayList("0","1","2","3");
		StringBuffer contents= new StringBuffer("");
		if(useSupplierList.size()>0){
			contents.append("<p>总准时交付率分析, TTL为全部供应商各交货情况计数，percent为全部供应商各交货情况百分比</p>");
			contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
			contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>vendor</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>delivery status</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>remark</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>TTL</th>");
			contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>Percent(%)</th>");
			contents.append("</tr>");
			int totalBefore=0;
			int totalSuit=0;
			int totalAfter=0;
			int totalOtherAfter=0;
		for (PsiSupplier supplier : useSupplierList) {
			int num=0;
				for (String type : typeList) {
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					if(num==0){
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;' rowspan='4'>"+supplier.getNikename()+"</td>");
						num++;
					}
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+("0".equals(type)?"非供应商逾期":("1".equals(type)?"提前":("2".equals(type)?"正常":"供应商逾期")))+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(mapRemark.get(type)==null||mapRemark.get(type).get(supplier.getNikename())==null?"":mapRemark.get(type).get(supplier.getNikename()))+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(map.get(type)==null||map.get(type).get(supplier.getNikename())==null?"":map.get(type).get(supplier.getNikename()))+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+new BigDecimal((mapRate.get(type)==null||mapRate.get(type).get(supplier.getNikename())==null)?0:mapRate.get(type).get(supplier.getNikename())).setScale(2, 4)+"</td>");
					contents.append("</tr>"); 
				}
				if(map.get("0")!=null&&map.get("0").get(supplier.getNikename())!=null){
					totalAfter+=map.get("0").get(supplier.getNikename());
				}
				if(map.get("1")!=null&&map.get("1").get(supplier.getNikename())!=null){
					totalBefore+=map.get("1").get(supplier.getNikename());
				}
				if(map.get("2")!=null&&map.get("2").get(supplier.getNikename())!=null){
					totalSuit+=map.get("2").get(supplier.getNikename());
				}
				if(map.get("3")!=null&&map.get("3").get(supplier.getNikename())!=null){
					totalOtherAfter+=map.get("3").get(supplier.getNikename());
				}
				
		}
		  contents.append("</table><br/>");
		int alltotal=totalBefore+totalSuit+totalAfter+totalOtherAfter;
		contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
		contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>delivery status</th>");
		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>TTL</th>");
		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>Percent(%)</th>");
		contents.append("</tr>");
		contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
		contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>提前</td>");
		contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+totalBefore+"</td>");
		contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(alltotal==0?"":new BigDecimal(totalBefore*100f/alltotal).setScale(2, 4))+"</td>");
		contents.append("</tr>"); 
		contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
		contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>非供应商逾期</td>");
		contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+totalAfter+"</td>");
		contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(alltotal==0?"":new BigDecimal(totalAfter*100f/alltotal).setScale(2, 4))+"</td>");
		contents.append("</tr>"); 
		contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
		contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>供应商逾期</td>");
		contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+totalOtherAfter+"</td>");
		contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(alltotal==0?"":new BigDecimal(totalOtherAfter*100f/alltotal).setScale(2, 4))+"</td>");
		contents.append("</tr>"); 
		contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
		contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>正常</td>");
		contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+totalSuit+"</td>");
		contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(alltotal==0?"":new BigDecimal(totalSuit*100f/alltotal).setScale(2, 4))+"</td>");
		contents.append("</tr>"); 
		contents.append("</table>");
		if(StringUtils.isNotEmpty(contents)){
			Date date = new Date();
			//String  toAddress="eileen@inateck.com";
			String  toAddress="supply-chain@inateck.com";
			final MailInfo mailInfo = new MailInfo(toAddress,"供应商"+new SimpleDateFormat("yyyyMM").format(DateUtils.addMonths(new Date(), -1))+"准时交付率统计",date);
			mailInfo.setContent(contents.toString());
		//	mailInfo.setCcToAddress("eileen@inateck.com");
			mailInfo.setCcToAddress("eileen@inateck.com");
			new Thread(){
				public void run(){
					mailManager.send(mailInfo);
					//new MailManager().send(mailInfo);
				}
			}.start();
		}
		 }
		LOGGER.info("查询总准时交付率结束！");

	}


	public void setPsiSupplierService(PsiSupplierService psiSupplierService) {
		this.psiSupplierService = psiSupplierService;
	}
	public void setBillService(PsiLadingBillService billService) {
		this.billService = billService;
	}

	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		PsiSupplierService psiSupplierService = applicationContext.getBean(PsiSupplierService.class);
		PsiLadingBillService billService = applicationContext.getBean(PsiLadingBillService.class);
		SupplierDeliveryRateMonitor aarehouseDeProductMonitor = new SupplierDeliveryRateMonitor();
		aarehouseDeProductMonitor.setPsiSupplierService(psiSupplierService);
		aarehouseDeProductMonitor.setBillService(billService);
		aarehouseDeProductMonitor.sendEmailSupplierDeliveryRate();
		applicationContext.close();
	}
	
}
