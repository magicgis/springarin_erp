package com.springrain.erp.modules.amazoninfo.scheduler;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonCatalogRank;
import com.springrain.erp.modules.amazoninfo.entity.AmazonFbaHealthReport;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsDetail;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct2;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.amazoninfo.service.AmazonFbaHealthReportService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.DiscountWarningService;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseGoalService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.custom.service.CustomEmailService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.ebay.service.EbayOrderService;
import com.springrain.erp.modules.psi.entity.FbaInbound;
import com.springrain.erp.modules.psi.entity.FbaInboundItem;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.service.FbaInboundService;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.DictService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.weixin.utils.ParamesAPI;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;
import com.springrain.magento.MagentoClientService;

public class MorningNewsMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(MorningNewsMonitor.class);
	@Autowired
	private SendCustomEmail1Manager sendCustomEmail1Manager;
	@Autowired
	private MailManager mailManager;
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private PsiProductService 		productService;
	@Autowired
	private SaleReportService 		saleReportService;
	@Autowired
	private PsiInventoryFbaService  fbaService;
	@Autowired
	private DiscountWarningService  disWarningService;
	@Autowired
	private DictService             dictService;
	
	@Autowired
	private AmazonPostsDetailService amazonPostsDetailService;
	@Autowired
	private PsiProductService		 psiProductService;
	@Autowired
	private AmazonCustomerService amazonCustomerService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	@Autowired
	private AmazonFbaHealthReportService amazonFbaHealthReportService;
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private CustomEmailService customEmailService;
	
	@Autowired
	private AmazonOrderService amazonOrderService;
	@Autowired
	private EnterpriseGoalService enterpriseGoalService;
	
	@Autowired
	private PsiProductEliminateService productEliminateService;
	
	@Autowired
	private FbaInboundService 		fbaInboundService;
	@Autowired
	private PsiInventoryService     inventoryService;
	@Autowired
	private EbayOrderService ebayOrderService;
	private static Map<String,String> countryNameMap;
	
	private static List<String> countryList;
	
	private final static StringBuffer head = new StringBuffer();
	
	private final static StringBuffer head1 = new StringBuffer();
	
	
	static{
		countryNameMap=Maps.newHashMap();
		countryNameMap.put("de","德国");
		countryNameMap.put("fr","法国");
		countryNameMap.put("it","意大利");
		countryNameMap.put("es","西班牙");
		countryNameMap.put("uk","英国");
		countryNameMap.put("com","美国");
		countryNameMap.put("ca","加拿大");
		countryNameMap.put("jp","日本");
		countryNameMap.put("mx","墨西哥");
		countryList=Lists.newArrayList("de","com","uk","fr","jp","it","es","ca","mx");
		
		
		head.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
		head.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>ProductName</th>");
		head.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>DE/US Stock</th>");
		head.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>Avg Daily Sales</th>");
		head.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>31Sell</th>");
		head.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>Gross FBA Stock</th>");
		head.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>FBA Tran</th>");
		head.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>FBA – Days Until Depletion</th>");
		head.append("</tr>");
		
		head1.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
		head1.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>ProductName</th>");
		head1.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>Asin</th>");
		head1.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>Error Price</th>");
		head1.append("</tr>");
	}
	
	private static String header3 ="<table width=\"96%\" border=\"1\" style='font-size:12px;table-layout:fixed;empty-cells:show; border-collapse: collapse;margin:0 auto;border:1px solid #9db3c5;color:#666;word-wrap:break-word;word-break:break-all'>"+
			  "<tr>"+
			  " <th width=\"30%\" style='background-repeat::repeat-x;height:40px;color:#a7d1fd;border:1px solid #a7d1fd; padding:0 2px 0;'>Country</th>"+
			   " <th width=\"20%\" style='background-repeat::repeat-x;height:40px;color:#a7d1fd;border:1px solid #a7d1fd; padding:0 2px 0;'>Sku</th>"+
			  "  <th width=\"10%\" style='background-repeat::repeat-x;height:30px;color:#a7d1fd;border:1px solid #a7d1fd; padding:0 2px 0;'>Browse</th>"+
			 " </tr>";
	
	public void sendMorningNewsEmail() { 
		
		try{
			Map<String,Set<String>> map= customEmailService.findEmailByUser();
			if(map!=null&&map.size()>0){
				for (Map.Entry<String,Set<String>> entry: map.entrySet()) {
					String email=entry.getKey();
					Set<String> idSet=entry.getValue();
					StringBuffer content= new StringBuffer("");
					content.append("hi <br/> &nbsp;&nbsp;&nbsp;&nbsp;以下邮件设置为今天跟进,请注意查看!<br/><br/>");
					for (String id: idSet) {
						content.append(id+"&nbsp;&nbsp;&nbsp;&nbsp;");
					}
					Date date = new Date();   
					final MailInfo mailInfo = new MailInfo(email,"邮件跟进"+DateUtils.getDate("-yyyy/MM/dd"),date);
					mailInfo.setContent(content.toString());
					new Thread(){
						public void run(){   
							mailManager.send(mailInfo);
						}
					}.start();
				}
			}
		}catch(Exception e){
			LOGGER.warn("邮件跟进异常",e);
		}
		
		/*//昨天上架sku
		try{
			final List<Object[]> nwProduct =  saleReportService.findNewSkuTd();
			if(nwProduct.size()>0){
				new Thread(){public void run() {
					StringBuilder content= new StringBuilder("hi,all <br/> &nbsp;&nbsp;&nbsp;&nbsp;Please Check Below List Of New SKU Listing Monitored By ERP!<br/><br/><br/>");
					int i = 1;
					content.append(header3);
						for (Object[] objs : nwProduct) {
							String country = objs[0].toString();
							String sku = objs[1].toString();
							String asin = objs[2].toString();
							String even = "";
							if(!(i%2==0)){
								even = "background-color:#e8f3fd;";
							}
							content.append("<tr style='font-size:10px;").append(even).append("'>").append(
									"<td style=\"border:1px dotted #cad9ea;padding:0 2px 0\">").append((("com".equals(country)?"US":country.toUpperCase()))).append("</td>"+
									 "<td style=\"border:1px dotted #cad9ea;padding:0 2px 0\">").append(sku).append("</td>").append(
									 "<td style=\"border:1px dotted #cad9ea;padding:0 2px 0\"><a href='http://www.amazon."+("jp,uk".contains(country)?"co.":"")+country+"/dp/").append(asin).append(
											 "' target='_blank'>"+(asin!=null?"Amazon":"")+"</a></td>").append("</tr>");
							i++;
						}
					content.append("</table><br/>");
					noteClaimer("pmg@inateck.com,marketing_dept@inateck.com,maik@inateck.com,amazon-sales@inateck.com,tim@inateck.com,postcheck_de@inateck.com,dale@inateck.com","Reminder of New SKU Listing"+DateUtils.getDateTime(),content.toString(),"");
				};}.start();
			}
			
			
		}catch(Exception e){
			LOGGER.warn("新上贴预警邮件异常",e);
		}*/
		
		
		
		//发送FBA库存不足邮件  3.1日增加 BY tim
		try{
			List<Object[]> sales =  saleReportService.findFbaSalesWithLess15Days();
			Map<String, Integer> inventorys = saleReportService.findInventorys();
			if(sales.size()>0){
				StringBuffer content = new StringBuffer("Hi,All<br/>   The following is the product list that FBA inventory available time less than 30 days, please check it.<br/><table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
				String country ="";
				for (Object[] objs : sales) {
					String temp = objs[1].toString();
					String name = objs[0].toString();
					if("fr,it,es,uk".contains(temp)){
						continue;
					}
					if(country.length()==0|| !temp.equals(country)){
						content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666;font-size:20px '><td style='border-left:1px solid;border-top:1px solid;' colspan = '7'><b>"+(("com".equals(temp)?"us":temp).toString().toUpperCase())+"</b></td></tr>");
						content.append(head.toString());
						country = temp;
					}
					int day = Integer.parseInt(objs[6].toString());
					String color = "color:#666;";
					if(day<=10){
						color = "color:#00bb00;";
					}
					content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					
					if("jp".equals(country)){
						content.append("<td colspan = '2' style='border-left:1px solid;border-top:1px solid;"+color+"'>"+name+"</td>");
					}else{
						String tempStr = "19";
						if("ca,com".contains(country)){
							tempStr = "120";
						}
						content.append("<td style='border-left:1px solid;border-top:1px solid;"+color+"'>"+name+"</td>");
						content.append("<td style='border-left:1px solid;border-top:1px solid;"+color+"'>"+(inventorys.get(name+"_"+tempStr)==null?"":inventorys.get(name+"_"+tempStr))+"</td>");
					}
					content.append("<td style='border-left:1px solid;border-top:1px solid;"+color+"'>"+objs[2]+"</td>");
					content.append("<td style='border-left:1px solid;border-top:1px solid;"+color+"'>"+objs[3]+"</a></td>");
					content.append("<td style='border-left:1px solid;border-top:1px solid;"+color+"'>"+objs[4]+"</td>");
					content.append("<td style='border-left:1px solid;border-top:1px solid;"+color+"'>"+objs[5]+"</td>");
					content.append("<td style='border-left:1px solid;border-top:1px solid;"+color+"'>"+objs[6]+"</td>");
					content.append("</tr>"); 
				}
				content.append("</table><br/>");
				Date date = new Date();
				String  toAddress="amazon-sales@inateck.com,tim@inateck.com,george@inateck.com,supply-chain@inateck.com,"+UserUtils.logistics1;
				final MailInfo mailInfo = new MailInfo(toAddress,"[Warning]FBA inventory available time less than 30 days"+new SimpleDateFormat("yyyyMMdd").format(date),date);
				mailInfo.setContent(content.toString());
				new Thread(){
					public void run(){
						mailManager.send(mailInfo);
					}
				}.start();
			}
		}catch(Exception e){
			LOGGER.warn("发送库存少于15天邮件失败",e);
		}
		
		//发送价格异常邮件  3.15日增加 BY tim
		try{
			Map<String,Map<String,Object[]>> data =  saleReportService.findSalePirceError();
			if(data.size()>0){
				StringBuffer content = new StringBuffer("Hi,All<br/>   The following countries are cross product price anomalies, please view.<br/><table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
				for (Map.Entry<String,Map<String,Object[]>> temp : data.entrySet()) { 
				    String country=temp.getKey();
					Map<String,Object[]> cData =temp.getValue();
					content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666;font-size:20px '><td style='border-left:1px solid;border-top:1px solid;' colspan = '3'><b>"+country.toUpperCase()+"</b></td></tr>");
					content.append(head1.toString());
					for (Map.Entry<String,Object[]> entry : cData.entrySet()) { 
					    String pName=entry.getKey();
					    Object[] obj=entry.getValue();
						content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
						content.append("<td style='border-left:1px solid;border-top:1px solid;'>"+pName+"</td>");
						content.append("<td style='border-left:1px solid;border-top:1px solid;'>"+obj[0].toString()+"</td>");
						content.append("<td style='border-left:1px solid;border-top:1px solid;'>"+obj[2].toString()+"</a></td>");
						content.append("</tr>"); 
					}
				}
				content.append("</table><br/>");
				Date date = new Date();
				String  toAddress="amazon-sales@inateck.com,tim@inateck.com";
				final MailInfo mailInfo = new MailInfo(toAddress,"Cross product price anomalies"+new SimpleDateFormat("yyyyMMdd").format(date),date);
				mailInfo.setContent(content.toString());
				new Thread(){
					public void run(){
						mailManager.send(mailInfo);
					}
				}.start();
			}
		}catch(Exception e){
			LOGGER.warn("Cross 产品价格异常!!",e);
		}
		
		//发送新品未填入HS code,退税率的明细邮件  2017.2.27日增加
		try{
			Calendar calendar = Calendar.getInstance();
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			if (day == 2) {	//周一发送
				Map<String, String> noHashCodeOrTax= psiProductService.findNoHashCodeOrTaxMap();
				if(noHashCodeOrTax != null && noHashCodeOrTax.size()>0){
					StringBuffer content = new StringBuffer("Hi:<br/>   以下是上周录入的新品还未填写HSCode或者退税率明细,请知悉.<br/><table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
					content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666;font-size:20px '><td style='border-left:1px solid;border-top:1px solid;'><b>产品型号</b></td><td style='border-left:1px solid;border-top:1px solid;'><b>明细</b></td></tr>");
					for (Entry<String, String> entry : noHashCodeOrTax.entrySet()) { 
						content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
						content.append("<td style='border-left:1px solid;border-top:1px solid;'>"+entry.getKey()+"</td>");
						content.append("<td style='border-left:1px solid;border-top:1px solid;'>"+entry.getValue()+"</td>");
						content.append("</tr>"); 
					}
					content.append("</table><br/>");
					Date date = new Date();
					String toAddress="alisa@inateck.com,belinda@inateck.com";
					final MailInfo mailInfo = new MailInfo(toAddress,"新品未填写HSCode或者退税率"+new SimpleDateFormat("yyyyMMdd").format(date),date);
					mailInfo.setContent(content.toString());
					new Thread(){
						public void run(){
							mailManager.send(mailInfo);
						}
					}.start();
				}
			}
		}catch(Exception e){
			LOGGER.warn("发送新品未填入HScode,退税率的明细邮件异常!!",e);
		}
		
		try{
			List<Object[]> skuList= fbaService.findErrorInventoryBySku();
			if(skuList!=null&&skuList.size()>0){
				StringBuffer content= new StringBuffer("");
				content.append("Hi All<br/> &nbsp;&nbsp;&nbsp;&nbsp;以下SKU FBA库存异常,请注意查看!<br/><br/>");
				SimpleDateFormat dft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				content.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
				content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666;font-size:20px '><td style='border-left:1px solid;border-top:1px solid;'><b>SKU</b></td><td style='border-left:1px solid;border-top:1px solid;'><b>开始时间</b></td><td style='border-left:1px solid;border-top:1px solid;'><b>结束时间</b></td><td style='border-left:1px solid;border-top:1px solid;'><b>结束时间库存</b></td><td style='border-left:1px solid;border-top:1px solid;'><b>库存差</b></td><td style='border-left:1px solid;border-top:1px solid;'><b>销量</b></td></tr>");
				for (Object[] obj:skuList) { 
					content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					content.append("<td style='border-left:1px solid;border-top:1px solid;'>"+obj[0].toString()+"</td>");
					content.append("<td style='border-left:1px solid;border-top:1px solid;'>"+dft.format((Timestamp)obj[1])+"</td>");
					content.append("<td style='border-left:1px solid;border-top:1px solid;'>"+dft.format((Timestamp)obj[2])+"</td>");
					content.append("<td style='border-left:1px solid;border-top:1px solid;'>"+obj[5].toString()+"</td>");
					content.append("<td style='border-left:1px solid;border-top:1px solid;'>"+obj[3].toString()+"</td>");
					content.append("<td style='border-left:1px solid;border-top:1px solid;'>"+obj[4].toString()+"</td>");
					content.append("</tr>"); 
				}
				content.append("</table><br/>");
				
				Date date = new Date();   
				final MailInfo mailInfo = new MailInfo("bella@inateck.com,tim@inateck.com","FBA库存异常提醒"+DateUtils.getDate("-yyyy/MM/dd"),date);
				mailInfo.setCcToAddress("eileen@inateck.com");
				mailInfo.setContent(content.toString());
				new Thread(){
					public void run(){   
						mailManager.send(mailInfo);
					}
				}.start();
			}
		}catch(Exception e){
			LOGGER.error("FBA库存邮件提醒异常!!",e);
		}
		
		/*try{
			//借调岗位提醒
			Map<String,List<String>> roleMap=systemService.findSecondRole();
			if(roleMap!=null&&roleMap.size()>0){
				List<String> tempList=Lists.newArrayList();
				for (Map.Entry<String,List<String>> entry: roleMap.entrySet()) {
					String userId=entry.getKey();
					User user=systemService.getUser(userId);
					if("0".equals(user.getDelFlag())){
						final MailInfo tempMailInfo = new MailInfo(user.getEmail(),"岗位借调情况"+new SimpleDateFormat("yyyyMM").format(new Date()),new Date());
						StringBuffer buf= new StringBuffer("Hi,"+user.getName()+" 以下用户借调岗位是否回收：<br/>");
						for (String nameAndRole: entry.getValue()) {
							buf.append(nameAndRole+"<br/>");
						}
						tempMailInfo.setContent(buf.toString());
						tempMailInfo.setCcToAddress("eileen@inateck.com");
						new Thread(){
							public void run(){
								mailManager.send(tempMailInfo);
							}
						}.start();
					}else{
						tempList.addAll(entry.getValue());
					}
				}
				if(tempList!=null&&tempList.size()>0){
					final MailInfo tempMailInfo = new MailInfo("tim@inateck.com","岗位借调情况"+new SimpleDateFormat("yyyyMM").format(new Date()),new Date());
					StringBuffer buf= new StringBuffer("Hi,tim 以下用户借调岗位是否回收：<br/>");
					for (String nameAndRole: tempList) {
						buf.append(nameAndRole+"<br/>");
					}
					tempMailInfo.setContent(buf.toString());
					tempMailInfo.setCcToAddress("eileen@inateck.com");
					new Thread(){
						public void run(){
							mailManager.send(tempMailInfo);
						}
					}.start();
				}
			}
		}catch(Exception e){
			LOGGER.warn("岗位借调情况异常!!",e);
		}*/
		
		try {
			LOGGER.info("开始查询...");
		
			Map<String,String> roleNameMap=Maps.newHashMap();
			roleNameMap.put("amazoninfo:feedSubmission:", "1");
			roleNameMap.put("amazoninfo:email:all", "0");
			roleNameMap.put("amazoninfo:email:en", "1");
			roleNameMap.put("amazoninfo:email:nonEn", "1");
			Map<String,Map<String,Set<String>>> newCompose=systemService.getEmailMap(countryList,roleNameMap);
	        SimpleDateFormat pattern=new SimpleDateFormat("yyyyMMddHHmmss");
	        /** 销量统计 start **/
	        Date beforeDate=new Date();
	        SaleReport saleReport=new SaleReport();
	        Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			Date start = DateUtils.addDays(today,-1);
			saleReport.setStart(start);
			saleReport.setEnd(start);
			Map<String, Map<String, SaleReport>>  saleData=Maps.newHashMap();
			try{
				String goalDateStr = enterpriseGoalService.findByCurrentMonth();
				Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
		        saleData = saleReportService.getSales(saleReport,rateMap);//国家[日期 /数据]
			}catch(Exception e) {
				LOGGER.error("销量统计异常！",e);
			}
		//	LOGGER.info("销量统计"+(new Date().getTime()-beforeDate.getTime()));
	        /** 销量统计end** /
	        /** 积压断货 start**/
			
			beforeDate=new Date();
			Map<String,List<String>> refOutOfMap = Maps.newHashMap();
			Map<String,List<String>> refOverMap =Maps.newHashMap();
			Map<String,Map<String,List<String>>> refPreOutOfMap =Maps.newHashMap();
			try{
				fbaService.getOverAndOutOfStock(refOutOfMap,refOverMap);
				refPreOutOfMap=fbaService.getPreOutOfStock();
			  }catch(Exception e){
				LOGGER.error("断货异常！",e);
			}
		//	LOGGER.info("积压断货"+(new Date().getTime()-beforeDate.getTime()));
			
			beforeDate=new Date();
			Set<String> postsSet=Sets.newHashSet();
			try{
				postsSet=amazonProduct2Service.getPostInfo();
			}catch(Exception e){
				LOGGER.error("帖子查询异常！",e);
			}
			//LOGGER.info("帖子查询"+(new Date().getTime()-beforeDate.getTime()));
			
			beforeDate=new Date();
			Map<String,String> asinMap=Maps.newHashMap();
			try{
				asinMap=amazonProduct2Service.getAllAsinByCountrySku();
			}catch(Exception e){
				LOGGER.error("查询Asin异常！",e);
			}
			//LOGGER.info("查询Asin查询"+(new Date().getTime()-beforeDate.getTime()));
			/** 积压断货 end**/
			
			/**星级变动start**/
			beforeDate=new Date();
			Map<String,Map<String,List<AmazonCatalogRank>>> mapList=Maps.newLinkedHashMap();
			Map<String,Map<String,AmazonPostsDetail>> starMap=Maps.newLinkedHashMap();
			Map<String,Map<String,List<String>>> reviewMap=Maps.newLinkedHashMap();
			Map<String,List<AmazonPostsDetail>> lowStarMap=Maps.newLinkedHashMap();
			try{
				mapList=amazonPostsDetailService.getChangeRankMap2();
				starMap=amazonPostsDetailService.getChangeStar();
				reviewMap=amazonCustomerService.getReviews();
				lowStarMap=amazonPostsDetailService.getLowStar();
			}catch(Exception e){
				LOGGER.error("星级变动异常！",e);
			}
			//LOGGER.info("星级变动 "+(new Date().getTime()-beforeDate.getTime()));
			/**星级变动 end**/
			
			/**亚马逊帖子异常及价格变动start  **/
			beforeDate=new Date();
			Map<String,List<Object[]>> warnPosts=Maps.newHashMap();
			Map<String,List<Object[]>> priceChangeMap=Maps.newHashMap();
			Map<String,Map<String,String>> priceChangeReason=Maps.newHashMap();
			try{
				warnPosts=amazonProduct2Service.getWarnPosts();
				try{
					Map<String,Integer> nopriceMap=amazonProduct2Service.countNoPriceProduct();
					if(nopriceMap!=null&&nopriceMap.size()>0){
						boolean recoverFlag=false;
						for (String country : countryList) {
							if(!"mx".equals(country)){
								for (Map.Entry<String,Integer> entry : nopriceMap.entrySet()) {  
								    String noPriceCountry=entry.getKey();
									if(country.equals(noPriceCountry)){
										recoverFlag=true;
									}
									/*if(nopriceMap.get(noPriceCountry)==null){
										warnPosts.remove(noPriceCountry);
										LOGGER.error(noPriceCountry+"帖子价格都为空！");
									}*/
								}
								if(!recoverFlag){
									warnPosts.remove(country);
									LOGGER.error(country+"帖子价格都为空！");
								}
								recoverFlag=false;
							}
						}
					}
				}catch(Exception e){
					LOGGER.error("亚马逊帖子异常及价格变动异常2！",e);
				}
				List<Object[]> priceChangelist = amazonProduct2Service.findPriceChangeProduct();
				priceChangeReason=amazonProduct2Service.getPriceChangeReason();
				for (Object[] obj : priceChangelist) {
					List<Object[]> objList=priceChangeMap.get(obj[1].toString());
					if(objList==null){
						objList=Lists.newArrayList();
						priceChangeMap.put(obj[1].toString(),objList);
					}
					objList.add(obj);
				}
			}catch(Exception e){
				LOGGER.error("亚马逊帖子异常及价格变动异常！",e);
			}
			//LOGGER.info("亚马逊帖子异常及价格变动 "+(new Date().getTime()-beforeDate.getTime()));
			
			/**亚马逊帖子异常及价格变动end  **/
			
			/**24小时未处理事件start **/
			// Map<String,Object[]> unionMap=Maps.newLinkedHashMap();
			// List<Object[]> unionList=Lists.newArrayList();
			beforeDate=new Date();
			 Map<String,Map<String,Object[]>>  undealEventMap=Maps.newLinkedHashMap();
			 try{
				undealEventMap= eventService.getUnDealTypeEventMap();
			 }catch(Exception e){
				LOGGER.error("24小时未处理事件异常！",e);
			 }
			// LOGGER.info("24小时未处理事件 "+(new Date().getTime()-beforeDate.getTime()));
			 
			beforeDate=new Date();
			 Map<String,Map<String,Object[]>>  undealEmailMap=Maps.newLinkedHashMap();
			 try{
				 undealEmailMap= customEmailService.getUnDealEmailMap();
			 }catch(Exception e){
					LOGGER.error("24小时未处理邮件异常！",e);
			 }
			// LOGGER.info("24小时未处理邮件 "+(new Date().getTime()-beforeDate.getTime()));
			/**24小时未处理邮件 end**/
			
			/** 大订单 start**/
			 beforeDate=new Date();
			 Map<String,List<Object[]>> maxOrderMap=Maps.newHashMap();
			 try{
				 maxOrderMap=amazonOrderService.getMaxOrder();
			 }catch(Exception e){
				 LOGGER.error("大订单异常！",e);
			 }
		//	 LOGGER.info("大订单 "+(new Date().getTime()-beforeDate.getTime()));
			/** 大订单 end  **/
			 
			/** 产品预警价格统计**/
			/* Map<String,Object[]> priceWarn=Maps.newHashMap();
			 try{
				 priceWarn=amazonProduct2Service.countWarnPrice();
			 }catch(Exception e){
				 LOGGER.error("产品未设最低最高预警价格统计！",e);
			 }*/
			 beforeDate=new Date();
			 Map<String,Map<String,String>> eventIdMap=Maps.newHashMap();
			 try{
				 eventIdMap=eventService.findEventWarn();
			 }catch(Exception e){
				 LOGGER.error("查询差评跟帖变更事件ID异常！",e);
			 }
			// LOGGER.info("查询差评跟帖变更事件ID"+(new Date().getTime()-beforeDate.getTime()));
			 
			 beforeDate=new Date();
			 List<Object[]> errorFnSku=Lists.newArrayList();
			 try{
				 errorFnSku=psiProductService.getErrorFnSku();
			 }catch(Exception e){
				 LOGGER.error("查询erp sku和实际sku不符异常！",e);
			 }
			 //LOGGER.info("查询erp sku和实际sku不符"+(new Date().getTime()-beforeDate.getTime()));
			 
			 List<Object[]> errorAsin=Lists.newArrayList();
			 try{
				 errorAsin=amazonPostsDetailService.findErrorPosts();
			 }catch(Exception e){
				 LOGGER.error("带电源和键盘产品欧洲5国相同asin！",e);
			 }
			 
			 List<Object[]> errorSameAsin=Lists.newArrayList();
			 try{
				 errorSameAsin=psiProductService.findErrorProductAsin();
			 }catch(Exception e){
				 LOGGER.error("查询asin相同异常！",e);
			 }
			 
			 List<Object[]> changePicList=Lists.newArrayList();
			 try{
				 changePicList=amazonPostsDetailService.findChangePic();
			 }catch(Exception e){
				 LOGGER.error("查询图片异常！",e);
			 }
			 
			 
			 for (Map.Entry<String, Map<String, Set<String>>> entry : newCompose.entrySet()) {   
			    String type =entry.getKey();
				if("0".equals(type)){
					 for (Map.Entry<String, Set<String>> entryRs : entry.getValue().entrySet()) {   
					    String keyStr=entryRs.getKey();
						beforeDate=new Date();
			        	StringBuffer contents= new StringBuffer("");
			        	StringBuffer contents1= new StringBuffer("");
			        	try{
			        		String before=new SimpleDateFormat("yyyyMMdd").format(start);
				        	Float totalSale=0f;
			        		int totalVolume=0;
				        	if(saleData!=null&&saleData.size()>0){
				        		contents1.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			    				contents1.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='4'><span style='font-weight: bold;font-size:25px'>Sales Quantity Statistics</span></td></tr>");
								contents1.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>NO.</th><th>Country</th><th>Sales(€)</th><th>Sales Quantity</th></tr>");
				        		int count=1;
				        		if(saleData.get("total")!=null&&saleData.get("total").get(before)!=null){
				        			totalSale=(saleData.get("total").get(before).getSales()==null?0:saleData.get("total").get(before).getSales());
					        		totalVolume=(saleData.get("total").get(before).getSalesVolume()==null?0:saleData.get("total").get(before).getSalesVolume());
				        		}
								for (String country : countryList) {
					        		if(saleData.get(country)!=null&&saleData.get(country).get(before)!=null){
					        			float sale=(saleData.get(country).get(before).getSales()==null?0:saleData.get(country).get(before).getSales());
					        			int volume=(saleData.get(country).get(before).getSalesVolume()==null?0:saleData.get(country).get(before).getSalesVolume());
					        			contents1.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
					        			contents1.append("<td>"+(count++)+"</td><td>"+("com".equals(country)?"us":country).toUpperCase()+"</td>");
					        			contents1.append("<td>"+new BigDecimal(sale).setScale(2,4)+"</td><td>"+volume+"</td>");
					        			contents1.append("</tr>");
					        		}
					        	}
								contents1.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
			        			contents1.append("<td colspan='2'>Total</td>");
			        			contents1.append("<td>"+totalSale+"</td><td>"+totalVolume+"</td>");
			        			contents1.append("</tr>");
				        		contents1.append("</table><br/><br/>");
				        	}
				        	contents.append("<p><span style='font-size:25px'>Hi all, it's "+new SimpleDateFormat("HH:mm").format(new Date())+" GMT+8.The sale volume of yesterday(00:00-23:59) in all marketplace is "+new BigDecimal(totalSale).setScale(2,4)+"€,with sales quantity "+totalVolume+",The detail for each marketplace is as below.</span></p>");
			        	}catch(Exception e){
			        		 contents1= new StringBuffer(""); 
							 LOGGER.error("各国销量统计渲染异常！",e);
						}
			        	if(contents1!=null&&contents1.length()>0){
			        		contents.append(contents1);
			        	}
			        	//LOGGER.info("各国销量渲染end"+(new Date().getTime()-beforeDate.getTime()));
			        	
			        	   beforeDate=new Date();
			    			if(errorFnSku!=null&&errorFnSku.size()>0){
			    				StringBuffer fnSkuStr= getErrorFnSkuWarn(errorFnSku);
			        			if(fnSkuStr!=null&&fnSkuStr.length()>0){
			        				contents.append(fnSkuStr);
			        			}
			    			}
			    			//LOGGER.info("产品跟帖渲染end"+(new Date().getTime()-beforeDate.getTime()));
			    			
			    			if(errorAsin!=null&&errorAsin.size()>0){
			    				StringBuffer fnSkuStr= getErrorFnAsinWarn(errorAsin);
			        			if(fnSkuStr!=null&&fnSkuStr.length()>0){
			        				contents.append(fnSkuStr);
			        			}
			    			}
			    			
			    			//errorSameAsin
			    			if(errorSameAsin!=null&&errorSameAsin.size()>0){
			    				StringBuffer fnSkuStr= getErrorSameAsin(errorSameAsin);
			        			if(fnSkuStr!=null&&fnSkuStr.length()>0){
			        				contents.append(fnSkuStr);
			        			}
			    			}
			    			
			    			
			    			if(changePicList!=null&&changePicList.size()>0){
			    				StringBuffer str= getChangePic(changePicList);
			        			if(str!=null&&str.length()>0){
			        				contents.append(str);
			        			}
			    			}
			        	 beforeDate=new Date();
		    			StringBuffer event= new StringBuffer("");
		    			try{
		    				if(undealEventMap!=null&&undealEventMap.size()>0){
		    					event.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
		    					event.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='14'><span style='font-weight: bold;font-size:25px'>More than 24 hours without handling the event statistics</span></td></tr>");
		    					event.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>SKU</th><th>Name</th><th>Rating</th><th>Earliest untreated</th><th>Account Rating</th><th>Earliest untreated</th><th>FAQ</th><th>Earliest untreated</th></tr>");
		    					int index=1;
		    					for (Map.Entry<String, Map<String, Object[]>>  entryEvent : undealEventMap.entrySet()) {  
		    					    String name=entryEvent.getKey();
		    					    Map<String, Object[]> entryMap=entryEvent.getValue();
		    						if(entryMap!=null){
		    							DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");  
		    							event.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		    							event.append("<td>"+(index++)+"</td><td>"+name+"</td>");
		    							if(entryMap.get("1")!=null){
		    								String formatDate = dft.format((Timestamp)entryMap.get("1")[1]);   
		        							event.append("<td>"+entryMap.get("1")[0]+"</td><td>"+formatDate+"</td>");
		        						}else{
		        							event.append("<td></td><td></td>");
		        						}
		        						if(entryMap.get("2")!=null){
		        							String formatDate = dft.format((Timestamp)entryMap.get("2")[1]);  
		        							event.append("<td>"+entryMap.get("2")[0]+"</td><td>"+formatDate+"</td>");
		        						}else{
		        							event.append("<td></td><td></td>");
		        						}
		        						if(entryMap.get("6")!=null){
		        							String formatDate = dft.format((Timestamp)entryMap.get("6")[1]);  
		        							event.append("<td>"+entryMap.get("6")[0]+"</td><td>"+formatDate+"</td>");
		        						}else{
		        							event.append("<td></td><td></td>");
		        						}
		        						event.append("</tr>"); 
		    						}
		    					}
		    					event.append("</table><br/><br/>");
		    				}
		    			}catch(Exception e){
		    				event= new StringBuffer("");
		    				LOGGER.info("24小时未处理事件渲染异常",e);
		    			}
		    			
		    			if(event!=null&&event.length()>0){
		    				contents.append(event);
		    			}
		    			//LOGGER.info("24小时未处理事件渲染end"+(new Date().getTime()-beforeDate.getTime()));
		    			
		    			 beforeDate=new Date();
		    			StringBuffer unDealEmail= new StringBuffer("");
		    			try{
		    				if(undealEmailMap!=null&&undealEmailMap.size()>0){
		    					unDealEmail.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
		    					unDealEmail.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='14'><span style='font-weight: bold;font-size:25px'>More than 24 hours without handling the mail statistics</span></td></tr>");
		    					unDealEmail.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>NO.</th><th>name</th><th>Not viewed</th><th>Earliest untreated</th><th>Viewed,No reply</th><th>Earliest untreated</th></tr>");
		    					int index=1;
		    					for (Map.Entry<String, Map<String, Object[]>> entryUndealEmail : undealEmailMap.entrySet()) {  
		    					    String name=entryUndealEmail.getKey();
		    					    Map<String, Object[]> entryMap=entryUndealEmail.getValue();
		    						if(entryMap!=null){
		    							DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");  
		    							unDealEmail.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		    							unDealEmail.append("<td>"+(index++)+"</td><td>"+name+"</td>");
		    							if(entryMap.get("0")!=null){
		    								String formatDate = dft.format((Timestamp)entryMap.get("0")[1]);   
		    								unDealEmail.append("<td>"+entryMap.get("0")[0]+"</td><td>"+formatDate+"</td>");
		        						}else{
		        							unDealEmail.append("<td></td><td></td>");
		        						}
		        						if(entryMap.get("1")!=null){
		        							String formatDate = dft.format((Timestamp)entryMap.get("1")[1]);  
		        							unDealEmail.append("<td>"+entryMap.get("1")[0]+"</td><td>"+formatDate+"</td>");
		        						}else{
		        							unDealEmail.append("<td></td><td></td>");
		        						}
		        						unDealEmail.append("</tr>"); 
		    						}
		    					}
		    					unDealEmail.append("</table><br/><br/>");
		    				}
		    			}catch(Exception e){
		    				unDealEmail= new StringBuffer("");
		    				LOGGER.info("24小时未处理邮件渲染异常",e);
		    			}
		    			
		    			if(unDealEmail!=null&&unDealEmail.length()>0){
		    				contents.append(unDealEmail);
		    			}
		    			//LOGGER.info("24小时未处理邮件渲染end"+(new Date().getTime()-beforeDate.getTime()));
		    			
		    			 beforeDate=new Date();
		    			if(eventIdMap!=null&&eventIdMap.size()>0){
		    				StringBuffer eventStr= getEventWarn(eventIdMap);
		        			if(eventStr!=null&&eventStr.length()>0){
		        				contents.append(eventStr);
		        			}
		    			}
		    			//LOGGER.info("产品跟帖渲染end"+(new Date().getTime()-beforeDate.getTime()));
		    			
		    			
		    			
		    			if(!keyStr.equals("de,com,uk,fr,jp,it,es,ca,mx,")){
		    				contents.append("<p>你已订阅以下国家邮件信息,如果订阅更多国家信息,请联系tim</p>");
		    			}
		    			
		    			beforeDate=new Date();
		    			        StringBuffer order= new StringBuffer("");
			        			try{
			        				if(maxOrderMap!=null&&maxOrderMap.size()>0){
			        					order.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			        					order.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='6'><span style='font-weight: bold;font-size:25px'>昨日亚马逊大订单</span></td></tr>");
			        					order.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>国家</th><th>订单号</th><th>产品名称</th><th>订单数量</th><th>订单状态</th></tr>");
			        					int no=1;
			        					for (String country : countryList) {
							        		if(keyStr.contains(country)){
						        				if(maxOrderMap.get(country)!=null){
							    					for(Object[] obj:maxOrderMap.get(country)){
							    						order.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+(no++)+"</td><td>"+("com".equals(country)?"us":country).toUpperCase()+"</td>");
							        					order.append("<td><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/order/form?amazonOrderId="+obj[1]+"' >"+obj[1]+"</a></td><td>"+obj[2]+"</td><td>"+obj[4]+"</td><td>"+obj[5]+"</td>");
							        					order.append("</tr>");
							        				}   
							        			}
							        		}
				        				}	
			        					order.append("</table><br/><br/>");
			        				}
			        				
			        			}catch(Exception e){
			        				order.delete(0,order.length()); 
			        				LOGGER.info("大订单渲染异常",e);
			        			}
			        			if(order!=null&&order.length()>0){
			        				contents.append(order);
			        			}
			        			//LOGGER.info("大订单渲染end"+(new Date().getTime()-beforeDate.getTime()));
			        			
			        			 beforeDate=new Date();
			        			StringBuffer posts= new StringBuffer("");
			        			try{
			        				if(warnPosts!=null&&warnPosts.size()>0){
			        					posts.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			        					posts.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='4'><span style='font-weight: bold;font-size:25px'>亚马逊帖子异常</span></td></tr>");
			        					posts.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>国家</th><th>sku</th><th>异常原因</th></tr>");
			        					int indexNo=1;
			        					for (String country : countryList) {
							        		if(keyStr.contains(country)){
						        				if(warnPosts.get(country)!=null){
							    					for(Object[] obj:warnPosts.get(country)){
							        					String suffix=country;
							        					if("jp,uk".contains(country)){
							        						suffix="co."+country;
							        					}else if("mx".equals(country)){
							        						suffix="com."+country;
							        					}
							        					String countryStr=suffix+"/dp/"+obj[1];
							        					posts.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+(indexNo++)+"</td><td>"+("com".equals(country)?"us":country).toUpperCase()+"</td><td><a href=http://www.amazon."+countryStr+" target='_blank'>"+obj[2]+"</a></td><td style='color:red;'>"+obj[3]+"</td>");
							        					posts.append("</tr>");
							        				}   
							    					
							        			}
							        		}
				        				}
			        					posts.append("</table><br/><br/>");
			        				}
			        				
			        				if(priceChangeMap!=null&&priceChangeMap.size()>0){
			        					posts.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
				        				posts.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='4'><span style='font-weight: bold;font-size:25px'>昨日亚马逊价格变动</span></td></tr>");
				        				posts.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>country</th><th>sku</th><th>价格浮动</th><th>改价理由</th></tr>");
				        				
			        					for (String country : countryList) {
							        		if(keyStr.contains(country)){
							        			if(priceChangeMap.get(country)!=null){
							        			for (Object[] objs : priceChangeMap.get(country)) {
							        					String sku = objs[0].toString();
							        					String country1 =  objs[1].toString();
							        					Float maxPrice =  ((BigDecimal)objs[3]).floatValue();
							        					Float minPrice =  ((BigDecimal)objs[4]).floatValue();
							        					Float bili =  ((BigDecimal)objs[5]).floatValue();
							        					AmazonProduct2 product = amazonProduct2Service.getProduct(country1, sku);
							        					if(product==null){
							        						LOGGER.info("产品为空："+country1+"==="+sku);
							        						continue;
							        					}
							        					String temp = country1;
							        					if("com".equals(country1)){
							        						temp = "us";
							        					}
							        					String suff = country1;
							        					if("jp,uk".contains(country1)){
							        						suff = "co."+country1;
							        					}
							        					int flag = 0 ;
							        					String desc = "";
							        					if(product!=null&&maxPrice.equals(product.getSalePrice())){
							        						flag =1;
							        						desc = minPrice +"-->"+maxPrice +"(调整幅度"+bili+"%)" ;
							        					}else{
							        						desc = maxPrice +"-->"+minPrice+"(调整幅度"+bili+"%)";
							        					}
							        					String reason="";
							        					if(priceChangeReason!=null&&priceChangeReason.size()>0&&priceChangeReason.get(country)!=null&&priceChangeReason.get(country).get(sku)!=null){
							        						reason=priceChangeReason.get(country).get(sku);
							        					}
							        					posts.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
							        					posts.append("<td>"+temp.toUpperCase()+"</td>");
							        					posts.append("<td><a href = \"http://www.amazon."+suff+"/dp/"+product.getAsin()+"\">"+sku+"</a></td>");
							        					posts.append("<td style=\"color:"+(flag==1?"red":"green")+"\">"+desc+"</td>");
							        					posts.append("<td>"+reason+"</td>");
							        					posts.append("</tr>");
							        				}
							        				
							        			}
							        		}
				        				}
			        					posts.append("</table><br/><br/>");
			        				}
			        					
			        			}catch(Exception e){
			        				posts= new StringBuffer("");
			        				LOGGER.info("帖子价格浮动渲染异常",e);
			        			}
			        			if(posts!=null&&posts.length()>0){
			        				contents.append(posts);
			        			}
			        			//LOGGER.info("亚马逊帖子及价格异常渲染end"+(new Date().getTime()-beforeDate.getTime()));
			        			
			        			/**折扣订单统计**/
			        			 beforeDate=new Date();
			        			StringBuffer promotions= getPromotionsOrder();
			        			if(promotions!=null&&promotions.length()>0){
			        				contents.append(promotions);
			        			}
			        			//LOGGER.info("折扣订单渲染end"+(new Date().getTime()-beforeDate.getTime()));
			        			
			        			beforeDate=new Date();
			        			StringBuffer overAndOut= new StringBuffer("");
			        			try{
			        				overAndOut.append("<table width='90%' style='border:1px solid #7FFFD4;color:#666;border-collapse:collapse;' >");
				        			for (String country : countryList) {
						        		 if(keyStr.contains(country)){
						        			if(refOverMap.get(country)!=null){
					        				   List<String> refOutOfList=refOverMap.get(country);
					        				   overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  padding:0 1em 0;background-color:#9ACD32;'><td colspan='10'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"积压详情<span style='font-weight: bold;font-size:15px'>(库销比大于3个月)</span></span></td></tr>");
											   overAndOut.append("<tr style='background-repeat:repeat-x;height:30px;  padding:0 1em 0;background-color:#96FED1; '><th colspan='3'>产品名</th><th>fba库存</th><th>30天销量</th><th colspan='3'>产品名</th><th>fba库存</th><th>30天销量</th></tr>");
											   for(int i =0;i<refOutOfList.size();i++){
												    String[] info=refOutOfList.get(i).split(",");//name,sales30,fbaQuantiy;
													String name=info[0];
													String sales30=info[1];
													String fbaQuantiy=info[2];
													int index = i+1;
													if(index%2==1){
														overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;   padding:0 1em 0;background-color:#f5fafe; '><td  colspan='3' style='border:1px solid #7FFFD4'>"+name+"</td><td style='border:1px solid #7FFFD4'>"+fbaQuantiy+"</td><td style='border:1px solid #7FFFD4'>"+sales30+"</td>");
													}
													if(index%2==0||index==refOverMap.get(country).size()){
														if(index%2==1){
															overAndOut.append("<td colspan='6' style='border:1px solid #7FFFD4'>&nbsp;</td></tr>");
														}else{
															overAndOut.append("<td colspan='3' style='border:1px solid #7FFFD4'>"+name+"</td><td style='border:1px solid #7FFFD4'>"+fbaQuantiy+"</td><td style='border:1px solid #7FFFD4'>"+sales30+"</td></tr>");
														}
													}
												}
											}
						        			
						        			if(refOutOfMap.get(country)!=null){
												//断货
						        				List<String> refOverList=refOutOfMap.get(country);
												if(refOverList!=null&&refOverList.size()>0){
												   overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  padding:0 1em 0;background-color:#9ACD32;'><td colspan='10'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"断货详情</span></td></tr>");
												   overAndOut.append("<tr style='background-repeat:repeat-x;height:30px;  padding:0 1em 0;background-color:#96FED1; '><th>产品名</th><th>Sku</th><th>断货天</th><th>断货日</th><th>帖子情况</th><th>产品名</th><th>Sku</th><th>断货天</th><th>断货日</th><th>帖子情况</th></tr>");
												   for(int i =0;i<refOverList.size();i++){
													    String[] info=refOverList.get(i).split(",");
														String sku = info[0];
														String name=info[1];
														String outDateStr  = info[3];
														String quantum = info[2];;
														
														//String asin=amazonProduct2Service.findAsin(country,sku);
														String asin=asinMap.get(country+"_"+sku);
														String suffix=country;
							        					if("jp,uk".contains(country)){
							        						suffix="co."+country;
							        					}else if("mx".equals(country)){
							        						suffix="com."+country;
							        					}
														int index = i+1;
														if(index%2==1){
															overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;   padding:0 1em 0;background-color:#f5fafe; '><td style='border:1px solid #7FFFD4'><a href=http://www.amazon."+suffix+"/dp/"+asin+" target='_blank'>"+name+"</a></td><td style='border:1px solid #7FFFD4'>"+sku+"</td><td style='border:1px solid #7FFFD4'>"+quantum+"</td><td style='border:1px solid #7FFFD4'>"+outDateStr.split("-")[0]+"</td><td style='border:1px solid #7FFFD4;"+(postsSet.contains(country+"_"+asin)?"":"color:red;")+" ' >"+(postsSet.contains(country+"_"+asin)?"正常":"异常")+"</td>");
														}
														if(index%2==0||index==refOverList.size()){
															if(index%2==1){
																overAndOut.append("<td colspan='5' style='border:1px solid #7FFFD4'>&nbsp;</td></tr>");
															}else{
																overAndOut.append("<td style='border:1px solid #7FFFD4'><a href=http://www.amazon."+suffix+"/dp/"+asin+" target='_blank'>"+name+"</a></td><td style='border:1px solid #7FFFD4'>"+sku+"</td><td style='border:1px solid #7FFFD4'>"+quantum+"</td><td style='border:1px solid #7FFFD4'>"+outDateStr.split("-")[0]+"</td><td style='border:1px solid #7FFFD4;"+(postsSet.contains(country+"_"+asin)?"":"color:red;")+"'>"+(postsSet.contains(country+"_"+asin)?"正常":"异常")+"</td></tr>");
															}
														}
													}
												}
											}   
						        			//refPreOutOfMap  key:country   key:sku,name,sales30,fbaQuantiy,tranQuantity,canSaleDays  key:sku,inventoryQuantity  key:country  value: price,inventoryQuantity
											if(refPreOutOfMap.get(country)!=null){
												if(refPreOutOfMap.get(country)!=null&&refPreOutOfMap.get(country).size()>0){
													Map<String,List<String>> map=refPreOutOfMap.get(country);
													overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  padding:0 1em 0;background-color:#9ACD32;'><td colspan='10'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"即将断货详情</span></td></tr>");
													if(!"jp,ca,com,mx,".contains(country)){
														overAndOut.append("<tr style='background-repeat:repeat-x;height:30px;  padding:0 1em 0;background-color:#96FED1; '><th>产品名</th><th>可售天</th><th>fba(实)</th><th>fba(总)</th><th>31天销量</th><th colspan='5'>Cross Sku</th></tr>");
													}else{
														overAndOut.append("<tr style='background-repeat:repeat-x;height:30px;  padding:0 1em 0;background-color:#96FED1; '><th>产品名</th><th>可售天</th><th>fba(实)</th><th>fba(总)</th><th>31天销量</th><th>产品名</th><th>可售天</th><th>fba(实)</th><th>fba(总)</th><th>31天销量</th></tr>");
													}
													int preI=0;
													for (Map.Entry<String,List<String>> entryMap : map.entrySet()) {  
													    String key=entryMap.getKey();
														String[] info=key.split(",");
														String name=info[0];
														String shortName = name.substring(name.indexOf(" ")+1);
														String fbaQuantiy=info[1];
														String fbaTotal=info[2];
														String canSaleDays = info[3];
														String sale31Days =info[4];
														if(!"jp,ca,com,mx,".contains(country)){
															overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;   padding:0 1em 0;background-color:#f5fafe; '>");
															overAndOut.append("<td style='border:1px solid #7FFFD4'> "+shortName+"</td><td style='border:1px solid #7FFFD4'>"+canSaleDays+"</td><td style='border:1px solid #7FFFD4'>"+fbaQuantiy+"</td><td style='border:1px solid #7FFFD4'>"+fbaTotal+"</td><td style='border:1px solid #7FFFD4'>"+sale31Days+"</td>");
															String str="";
															StringBuffer buf= new StringBuffer();
															List<String> crossSkus=entryMap.getValue();
															for (String skuCountry : crossSkus) {
																String arr[]=skuCountry.split(",");
																if(!"(请上贴)".equals(arr[1])){
																	buf.append("<span style='color:red'>"+arr[0]+",价格:"+arr[1]+",库存数:"+arr[2]+"</span><br/>");
																}else{
																	buf.append(arr[0]+",价格:"+arr[1]+",库存数:"+arr[2]+"<br/>");
																}
															}
															overAndOut.append("<td colspan='5' style='border:1px solid #7FFFD4' >"+buf.toString()+"</td></tr>");
														}else{
															preI++;
															if(preI%2==1){
																overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;   padding:0 1em 0;background-color:#f5fafe; '>");
																overAndOut.append("<td style='border:1px solid #7FFFD4'>"+shortName+"</td><td style='border:1px solid #7FFFD4'>"+canSaleDays+"</td><td style='border:1px solid #7FFFD4'>"+fbaQuantiy+"</td><td style='border:1px solid #7FFFD4'>"+fbaTotal+"</td><td style='border:1px solid #7FFFD4'>"+sale31Days+"</td>");
															}else{
																if(preI%2==0||preI==map.size()){
																	if(preI%2==1){
																		overAndOut.append("<td colspan='5' style='border:1px solid #7FFFD4'>&nbsp;</td></tr>");
																	}else{
																		overAndOut.append("<td  style='border:1px solid #7FFFD4'>"+shortName+"</td><td style='border:1px solid #7FFFD4'>"+canSaleDays+"</td><td style='border:1px solid #7FFFD4'>"+fbaQuantiy+"</td><td style='border:1px solid #7FFFD4'>"+fbaTotal+"</td><td style='border:1px solid #7FFFD4'>"+sale31Days+"</td></tr>");
																	}
																}
															}
														}
													}
												}
											} 
						        		 }
				        			}
									overAndOut.append("</table><br/><br/>");
			        			}catch(Exception e){
			        				overAndOut=new StringBuffer(""); 
			        				LOGGER.info("积压断货渲染异常",e);
			        			}
			        			if(overAndOut!=null&&overAndOut.length()>0){
			        				contents.append(overAndOut);
			        			}
			        			//LOGGER.info("积压断货渲染end"+(new Date().getTime()-beforeDate.getTime()));
			        			
			        			beforeDate=new Date();
			        			StringBuffer startChange= new StringBuffer("");
			        			try{
			        				startChange.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			        				if(starMap!=null&&starMap.size()>0){
			        					
			        					for (String country : countryList) {
								             if(keyStr.contains(country)){
						        			  Map<String,AmazonPostsDetail> data=starMap.get(country);
						        			   if(data!=null&&data.size()>0){
						        				int flag=0;
						        				for (Map.Entry<String,AmazonPostsDetail> entryData : data.entrySet()) {
						        				    String asin = entryData.getKey();
						        					AmazonPostsDetail post= entryData.getValue();
						        					String[] starStr=post.getCompareStar().split(",");
						        					Float compare=(new BigDecimal(starStr[0])).subtract(new BigDecimal(starStr[1])).floatValue();
						        					String suffix=post.getCountry();
						        					if("jp,uk".contains(post.getCountry())){
						        						suffix="co."+post.getCountry();
						        					}else if("mx".equals(post.getCountry())){
						        						suffix="com."+post.getCountry();
						        					}
						        					String key=asin+"_"+country;
						        					Map<String,List<String>> review=reviewMap.get(key);
						        					if(review!=null&&review.size()>0){
						        						if(flag==0){
						        							startChange.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='11'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"星级评分变动情况表<span style='font-weight: bold;font-size:15px'>(因网络问题有可能导致抓取数据不完整性而引起的误差,请自行核实)</span></span></td></tr>");
						        							startChange.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '>");
						        							startChange.append("<th  style='width:20%'>product_name</th><th  style='width:10%'>昨日评分</th>");
						        							startChange.append("<th  style='width:10%'>今日评分</th>");
						        							startChange.append("<th  style='width:10%'>5 star</th><th  style='width:10%'>4 star</th>");
						        							startChange.append("<th  style='width:10%'>3 star</th><th  style='width:10%'>2 star</th>");
						        							startChange.append("<th  style='width:10%'>1 star</th><th  style='width:10%'>总共评论</th>");
						        							startChange.append("</tr>");
						        						}
						        						startChange.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
						        						startChange.append("<td><a href=http://www.amazon."+suffix+"/dp/"+post.getAsin()+" target='_blank'>"+post.getProductName()+"</a></td>");
						        						startChange.append("<td><font color="+(compare>0?"green":"red")+">"+starStr[1]+"</font></td>");
						        						startChange.append("<td><font color="+(compare>0?"green":"red")+">"+starStr[0]+"</font></td>");
							        						List<String> reviewList5=review.get("5");
						        							if(reviewList5!=null&&reviewList5.size()>0){
						        								startChange.append("<td>");
						        								for (String reviewAsin: reviewList5) {
						        									startChange.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='green'>"+5+"</font></a>&nbsp;&nbsp;");
						        								}
						        								startChange.append("</td>");
						        							}else{
						        								startChange.append("<td></td>");
						        							}
						        							
						        							List<String> reviewList4=review.get("4");
						        							if(reviewList4!=null&&reviewList4.size()>0){
						        								startChange.append("<td>");
						        								for (String reviewAsin: reviewList4) {
						        									startChange.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='green'>"+4+"</font></a>&nbsp;&nbsp;");
						        								}
						        								startChange.append("</td>");
						        							}else{
						        								startChange.append("<td></td>");
						        							}
						        							
						        							List<String> reviewList3=review.get("3");
						        							if(reviewList3!=null&&reviewList3.size()>0){
						        								startChange.append("<td>");
						        								for (String reviewAsin: reviewList3) {
						        									startChange.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='red'>"+3+"</font></a>&nbsp;&nbsp;");
						        								}
						        								startChange.append("</td>");
						        							}else{
						        								startChange.append("<td></td>");
						        							}
						        							
						        							List<String> reviewList2=review.get("2");
						        							if(reviewList2!=null&&reviewList2.size()>0){
						        								startChange.append("<td>");
						        								for (String reviewAsin: reviewList2) {
						        									startChange.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='red'>"+2+"</font></a>&nbsp;&nbsp;");
						        								}
						        								startChange.append("</td>");
						        							}else{
						        								startChange.append("<td></td>");
						        							}
						        							
						        							List<String> reviewList1=review.get("1");
						        							if(reviewList1!=null&&reviewList1.size()>0){
						        								startChange.append("<td>");
						        								for (String reviewAsin: reviewList1) {
						        									startChange.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='red'>"+1+"</font></a>&nbsp;&nbsp;");
						        								}
						        								startChange.append("</td>");
						        							}else{
						        								startChange.append("<td></td>");
						        							}
						        							
						        							startChange.append("<td>"+post.getStar1()+"</td>");
						        							startChange.append("</tr>");
						        						    flag++;
						        					}
						        				}
						        			  }
						        			}
								          }
			        				}
			        			    if(lowStarMap!=null&&lowStarMap.size()>0){//
			        			    	startChange.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='10'><span style='font-weight: bold;font-size:25px'>低分情况表<span style='font-weight: bold;font-size:15px'>(单帖评分,非组合帖评分)</span></span></td></tr>");
				        				startChange.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '>");
				        				startChange.append("<th>product_name</th>");
				        				startChange.append("<th>country</th>");
				        				startChange.append("<th>评分</th>");
				        				startChange.append("<th>5 star</th><th>4 star</th>");
				        				startChange.append("<th>3 star</th><th>2 star</th>");
				        				startChange.append("<th colspan='2'>1 star</th>");
				        				startChange.append("</tr>");
			        			    	for (String country : countryList) {
							        		if(keyStr.contains(country)){
						        			  List<AmazonPostsDetail> lowStar=lowStarMap.get(country);	
						        			  if(lowStar!=null&&lowStar.size()>0){
						        				for (AmazonPostsDetail detail : lowStar) {
						        					String suffix=detail.getCountry();
						        					if("jp,uk".contains(detail.getCountry())){
						        						suffix="co."+detail.getCountry();
						        					}else if("mx".equals(detail.getCountry())){
						        						suffix="com."+detail.getCountry();
						        					}
						        					startChange.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
						        					startChange.append("<td><a href=http://www.amazon."+suffix+"/dp/"+detail.getAsin()+" target='_blank'>"+detail.getProductName()+"</a></td>");
						        					startChange.append("<td>"+("com".equals(country)?"us":country).toUpperCase()+"</td><td>"+detail.getStar()+"</td>");
						        					startChange.append("<td>"+detail.getStar5()+"</td>");
						        					startChange.append("<td>"+detail.getStar4()+"</td>");
						        					startChange.append("<td>"+detail.getStar3()+"</td>");
						        					startChange.append("<td>"+detail.getStar2()+"</td>");
						        					startChange.append("<td colspan='2'>"+detail.getStar1()+"</td>");
						        					startChange.append("</tr>");
						        					
						        				}
						        			   }
							        		 }
					        			  }
			        			    }
			        			    if(mapList!=null&&mapList.size()>0){
			        			    	
			        				    for (String country : countryList) {
							        		if(keyStr.contains(country)){
						        			  Map<String,List<AmazonCatalogRank>> temp=mapList.get(country);
						        			  if(temp!=null&&temp.size()>0){
						        				  startChange.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32'><td colspan='10'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"排名变动情况表</span></td></tr>");
							        				startChange.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>product_name</th>");
							        				startChange.append("<th>catalog_name</th><th>昨日排名</th>");
							        				startChange.append("<th colspan='3'>今日排名</th><th colspan='3'>变动情况</th>");
							        				startChange.append("</tr>");
							        			for (Map.Entry<String,List<AmazonCatalogRank>> entryTemp : temp.entrySet()) {	
						        				    String name = entryTemp.getKey();
						        					int num=0;
						        					int length=temp.get(name).size();
						        					for (AmazonCatalogRank rank :entryTemp.getValue()) {
							        						String[] rankStr=rank.getRankStr().split(",");
							        						String link=rank.getCountry();
							        						String suffix=rank.getCountry();
							        						Integer compare=Integer.parseInt(rankStr[0])-Integer.parseInt(rankStr[1]);
						        							if("jp,uk".contains(rank.getCountry())){
							        							suffix="co."+rank.getCountry();
							        							link = "co."+link;
							        						}else if("mx".equals(rank.getCountry())){
							        							suffix="com."+rank.getCountry();
							        							link = "com."+link;
							        						}
						        							String catalog="";
						        							if("com".equals(country)){
						        								catalog="http://www.amazon."+link+"/gp/bestsellers/pc/"+rank.getCatalog();
						        							}else if("it,ca".contains(country)){
						        								catalog="http://www.amazon."+link+"/gp/bestsellers/electronics/"+rank.getCatalog();
						        							}else{
						        								catalog="http://www.amazon."+link+"/gp/bestsellers/computers/"+rank.getCatalog();
						        							}
							        						
							        						startChange.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
							        						if(num==0){
							        							startChange.append("<td rowspan="+length+"><a href=http://www.amazon."+suffix+"/dp/"+rank.getAsin()+" target='_blank'>"+rank.getProductName()+"</a></td>");
							        							num++;
							        						}
							        						startChange.append("<td><a href="+catalog+" target='_blank'>"+(StringUtils.isBlank(rank.getCatalogName())?"未找到目录名称":rank.getCatalogName())+"</a></td>");
							        						startChange.append("<td><font color="+(compare>0?"red":"green")+">"+rankStr[1]+"</font></td>");
							        						startChange.append("<td  colspan='3'><font color="+(compare>0?"red":"green")+">"+rankStr[0]+"</font></td>");
							        						startChange.append("<td  colspan='3'><font color="+(compare>0?"red":"green")+">"+(compare>0?("↓ "+compare+"名"):("↑ "+compare.toString().replace("-", "")+"名"))+"</font></td>");
							        						startChange.append("</tr>");
						        					}
						        				}
						        			}
							        	   }
					        		    } 
			        			   }
				        			startChange.append("</table><br/><br/>");
			        			}catch(Exception e){
			        				startChange=new StringBuffer("");
			        				LOGGER.info("星级变动渲染异常",e);
			        			}
			        			if(startChange!=null&&startChange.length()>0){
			        				contents.append(startChange);
			        			}
			        			//LOGGER.info("星级变动渲染end"+(new Date().getTime()-beforeDate.getTime()));
			        			
			        			beforeDate=new Date();
			        			StringBuffer fbaInventroy= getFbaInventroy();
			        			if(fbaInventroy!=null&&fbaInventroy.length()>0){
			        				contents.append(fbaInventroy);
			        			}
			        			LOGGER.info("本地帖 fba库存过期渲染end"+(new Date().getTime()-beforeDate.getTime()));
			        			
			        			
			        			contents.append("<br/><br/><br/>");
			        			
			        		//}
			        	//}
			        	
			        	if(StringUtils.isNotEmpty(contents)){
			    			Date date = new Date();
			    			String toEmail="";
			    			StringBuffer buf= new StringBuffer();
			    			for (String set :newCompose.get(type).get(keyStr)) {
			    				buf.append(set+",");
							}
			    			toEmail=buf.toString();
			    			toEmail=toEmail.substring(0, toEmail.length()-1);
			    			//LOGGER.info("======================================"+toEmail);
			    			//String  toAddress="michael@inateck.com";
			    			String  toAddress=toEmail;
			    			final MailInfo mailInfo = new MailInfo(toAddress,"ERP早报("+DateUtils.getDate("yyyy/MM/dd")+")",date);
			    			mailInfo.setContent(contents.toString());
			    			new Thread(){
			    			    public void run(){
			    			    	mailManager.send(mailInfo);
			    				}
			    			}.start();
			    		}
			           }
				}else if("1".equals(type)){
				  for (String keyStr:newCompose.get(type).keySet()) {
		        	StringBuffer contents= new StringBuffer("");
		        	StringBuffer contents1= new StringBuffer("");
		        	try{
		        		String before=new SimpleDateFormat("yyyyMMdd").format(start);
			        	Float totalSale=0f;
		        		int totalVolume=0;
			        	if(saleData!=null&&saleData.size()>0){
			        		contents1.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
		    				contents1.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='4'><span style='font-weight: bold;font-size:25px'>各国销量统计</span></td></tr>");
							contents1.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>平台</th><th>销售额(€)</th><th>销量</th></tr>");
			        		int count=1;
			        		if(saleData.get("total")!=null&&saleData.get("total").get(before)!=null){
			        			totalSale=(saleData.get("total").get(before).getSales()==null?0:saleData.get("total").get(before).getSales());
				        		totalVolume=(saleData.get("total").get(before).getSalesVolume()==null?0:saleData.get("total").get(before).getSalesVolume());
			        		}
							for (String country : countryList) {
				        		if(saleData.get(country)!=null&&saleData.get(country).get(before)!=null){
				        			float sale=(saleData.get(country).get(before).getSales()==null?0:saleData.get(country).get(before).getSales());
				        			int volume=(saleData.get(country).get(before).getSalesVolume()==null?0:saleData.get(country).get(before).getSalesVolume());
				        			contents1.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				        			contents1.append("<td>"+(count++)+"</td><td>"+("com".equals(country)?"us":country).toUpperCase()+"</td>");
				        			contents1.append("<td>"+new BigDecimal(sale).setScale(2,4)+"</td><td>"+volume+"</td>");
				        			contents1.append("</tr>");
				        		}
				        	}
							contents1.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		        			contents1.append("<td colspan='2'>合计</td>");
		        			contents1.append("<td>"+totalSale+"</td><td>"+totalVolume+"</td>");
		        			contents1.append("</tr>");
			        		contents1.append("</table><br/><br/>");
			        	}
			        	contents.append("<p><span style='font-size:25px'>Hi all,it's "+new SimpleDateFormat("HH:mm").format(new Date())+" GMT+8.The sale volume of yesterday in all marketplace is "+new BigDecimal(totalSale).setScale(2,4)+"€, with sales quantity "+totalVolume+",The detail for each marketplace is as below.</span></p>");
		        	}catch(Exception e){
		        		 contents1= new StringBuffer(""); 
						 LOGGER.error("各国销量统计渲染异常！",e);
					}
		        	if(contents1!=null&&contents1.length()>0){
		        		contents.append(contents1);
		        	}
		        
		        	if(errorFnSku!=null&&errorFnSku.size()>0){
	    				StringBuffer fnSkuStr= getErrorFnSkuWarn(errorFnSku);
	        			if(fnSkuStr!=null&&fnSkuStr.length()>0){
	        				contents.append(fnSkuStr);
	        			}
	    			}
		        	if(errorAsin!=null&&errorAsin.size()>0){
	    				StringBuffer fnSkuStr= getErrorFnAsinWarn(errorAsin);
	        			if(fnSkuStr!=null&&fnSkuStr.length()>0){
	        				contents.append(fnSkuStr);
	        			}
	    			}
		        	if(errorSameAsin!=null&&errorSameAsin.size()>0){
	    				StringBuffer fnSkuStr= getErrorSameAsin(errorSameAsin);
	        			if(fnSkuStr!=null&&fnSkuStr.length()>0){
	        				contents.append(fnSkuStr);
	        			}
	    			}
		        	
		        	if(changePicList!=null&&changePicList.size()>0){
	    				StringBuffer str= getChangePic(changePicList);
	        			if(str!=null&&str.length()>0){
	        				contents.append(str);
	        			}
	    			}
	    			StringBuffer event= new StringBuffer("");
	    			try{
	    				if(undealEventMap!=null&&undealEventMap.size()>0){
	    					event.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
	    					event.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='14'><span style='font-weight: bold;font-size:25px'>超过24小时未处理事件统计</span></td></tr>");
	    					event.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>姓名</th><th>Rating</th><th>最早未处理</th><th>Account Rating</th><th>最早未处理</th><th>FAQ</th><th>最早未处理</th></tr>");
	    					int index=1;
	    					for (Map.Entry<String, Map<String, Object[]>> entryMap : undealEventMap.entrySet()) {
	    					    String name = entryMap.getKey();
	    					    Map<String, Object[]> entryEvent = entryMap.getValue();
	    						if(entryEvent!=null){
	    							DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");  
	    							event.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
	    							event.append("<td>"+(index++)+"</td><td>"+name+"</td>");
	    							if(entryEvent.get("1")!=null){
	    								String formatDate = dft.format((Timestamp)entryEvent.get("1")[1]);   
	        							event.append("<td>"+entryEvent.get("1")[0]+"</td><td>"+formatDate+"</td>");
	        						}else{
	        							event.append("<td></td><td></td>");
	        						}
	        						if(entryEvent.get("2")!=null){
	        							String formatDate = dft.format((Timestamp)entryEvent.get("2")[1]);  
	        							event.append("<td>"+entryEvent.get("2")[0]+"</td><td>"+formatDate+"</td>");
	        						}else{
	        							event.append("<td></td><td></td>");
	        						}
	        						if(entryEvent.get("6")!=null){
	        							String formatDate = dft.format((Timestamp)entryEvent.get("6")[1]);  
	        							event.append("<td>"+entryEvent.get("6")[0]+"</td><td>"+formatDate+"</td>");
	        						}else{
	        							event.append("<td></td><td></td>");
	        						}
	        						event.append("</tr>"); 
	    						}
	    					}
	    					event.append("</table><br/><br/>");
	    				}
	    			}catch(Exception e){
	    				event= new StringBuffer("");
	    				LOGGER.info("24小时未处理事件渲染异常",e);
	    			}
	    			
	    			if(event!=null&&event.length()>0){
	    				contents.append(event);
	    			}
	    			
	    			
	    			StringBuffer unDealEmail= new StringBuffer("");
	    			try{
	    				if(undealEmailMap!=null&&undealEmailMap.size()>0){
	    					unDealEmail.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
	    					unDealEmail.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='14'><span style='font-weight: bold;font-size:25px'>超过24小时未处理邮件统计</span></td></tr>");
	    					unDealEmail.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>姓名</th><th>未查看</th><th>最早未处理</th><th>已查看未回复</th><th>最早未处理</th></tr>");
	    					int index=1;
	    					for (Map.Entry<String, Map<String, Object[]>> entryMap : undealEmailMap.entrySet()) {
	    					    String name =entryMap.getKey();
	    					    Map<String, Object[]> entryObj= entryMap.getValue();
	    						if(entryObj!=null){
	    							DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");  
	    							unDealEmail.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
	    							unDealEmail.append("<td>"+(index++)+"</td><td>"+name+"</td>");
	    							if(entryObj.get("0")!=null){
	    								String formatDate = dft.format((Timestamp)entryObj.get("0")[1]);   
	    								unDealEmail.append("<td>"+entryObj.get("0")[0]+"</td><td>"+formatDate+"</td>");
	        						}else{
	        							unDealEmail.append("<td></td><td></td>");
	        						}
	        						if(undealEmailMap.get(name).get("1")!=null){
	        							String formatDate = dft.format((Timestamp)entryObj.get("1")[1]);  
	        							unDealEmail.append("<td>"+entryObj.get("1")[0]+"</td><td>"+formatDate+"</td>");
	        						}else{
	        							unDealEmail.append("<td></td><td></td>");
	        						}
	        						unDealEmail.append("</tr>"); 
	    						}
	    					}
	    					unDealEmail.append("</table><br/><br/>");
	    				}
	    			}catch(Exception e){
	    				unDealEmail= new StringBuffer("");
	    				LOGGER.info("24小时未处理邮件渲染异常",e);
	    			}
	    			
	    			if(unDealEmail!=null&&unDealEmail.length()>0){
	    				contents.append(unDealEmail);
	    			}
	    			
	    			
	    			if(eventIdMap!=null&&eventIdMap.size()>0){
	    				StringBuffer eventStr= getEventWarn(eventIdMap);
	        			if(eventStr!=null&&eventStr.length()>0){
	        				contents.append(eventStr);
	        			}
	    			}

	    			
	    			
	    			if(!keyStr.equals("de,com,uk,fr,jp,it,es,ca,mx,")){
	    				contents.append("<p>你已订阅以下国家邮件信息,如果订阅更多国家信息,请联系tim</p>");
	    			}
	    			
		        	for (String country : countryList) {
		        		if(keyStr.contains(country)){
		        			contents.append("<center><b><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"订阅详情</span></b></center>");
		        			StringBuffer order= new StringBuffer("");
		        			try{
		        				if(maxOrderMap!=null&&maxOrderMap.size()>0&&maxOrderMap.get(country)!=null){
		        					order.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
		        					order.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='5'><span style='font-weight: bold;font-size:25px'>昨日亚马逊大订单</span></td></tr>");
		        					order.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>订单号</th><th>产品名称</th><th>订单数量</th><th>订单状态</th></tr>");
			        				int no=1;
			    					for(Object[] obj:maxOrderMap.get(country)){
			    						order.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+(no++)+"</td>");
			        					order.append("<td><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/order/form?amazonOrderId="+obj[1]+"' >"+obj[1]+"</a></td><td>"+obj[2]+"</td><td>"+obj[4]+"</td><td>"+obj[5]+"</td>");
			        					order.append("</tr>");
			        				}   
			    					order.append("</table>");
			        			}
		        			}catch(Exception e){
		        				order.delete(0,order.length()); 
		        				LOGGER.info("大订单渲染异常",e);
		        			}
		        			if(order!=null&&order.length()>0){
		        				contents.append(order);
		        			}
		        			
		        			StringBuffer posts= new StringBuffer("");
		        			try{
		        				if(warnPosts!=null&&warnPosts.size()>0&&warnPosts.get(country)!=null){
		        					posts.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
		        					posts.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='3'><span style='font-weight: bold;font-size:25px'>亚马逊帖子异常</span></td></tr>");
		        					posts.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>sku</th><th>异常原因</th></tr>");
			        				int indexNo=1;
			    					for(Object[] obj:warnPosts.get(country)){
			        					String suffix=country;
			        					if("jp,uk".contains(country)){
			        						suffix="co."+country;
			        					}else if("mx".equals(country)){
			        						suffix="com."+country;
			        					}
			        					String countryStr=suffix+"/dp/"+obj[1];
			        					posts.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+(indexNo++)+"</td><td><a href=http://www.amazon."+countryStr+" target='_blank'>"+obj[2]+"</a></td><td style='color:red;'>"+obj[3]+"</td>");
			        					posts.append("</tr>");
			        				}   
			    					posts.append("</table>");
			        			}
			        			if(priceChangeMap!=null&&priceChangeMap.size()>0&&priceChangeMap.get(country)!=null){
			        				posts.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			        				posts.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='4'><span style='font-weight: bold;font-size:25px'>昨日亚马逊价格变动</span></td></tr>");
			        				posts.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>country</th><th>sku</th><th>价格浮动</th><th>改价理由</th></tr>");
			        				for (Object[] objs : priceChangeMap.get(country)) {
			        					String sku = objs[0].toString();
			        					String country1 =  objs[1].toString();
			        					Float maxPrice =  ((BigDecimal)objs[3]).floatValue();
			        					Float minPrice =  ((BigDecimal)objs[4]).floatValue();
			        					Float bili =  ((BigDecimal)objs[5]).floatValue();
			        					AmazonProduct2 product = amazonProduct2Service.getProduct(country1, sku);
			        					if(product==null){
			        						LOGGER.info("产品为空："+country1+"==="+sku);
			        						continue;
			        					}
			        					
			        					String temp = country1;
			        					if("com".equals(country1)){
			        						temp = "us";
			        					}
			        					String suff = country1;
			        					if("jp,uk".contains(country1)){
			        						suff = "co."+country1;
			        					}
			        					int flag = 0 ;
			        					String desc = "";
			        					if(product!=null&&maxPrice.equals(product.getSalePrice())&&product.getSalePrice()!=null){
			        						flag =1;
			        						desc = minPrice +"-->"+maxPrice +"(调整幅度"+bili+"%)" ;
			        					}else{
			        						desc = maxPrice +"-->"+minPrice+"(调整幅度"+bili+"%)";
			        					}
			        					String reason="";
			        					if(priceChangeReason!=null&&priceChangeReason.size()>0&&priceChangeReason.get(country)!=null&&priceChangeReason.get(country).get(sku)!=null){
			        						reason=priceChangeReason.get(country).get(sku);
			        					}
			        					posts.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
			        					posts.append("<td>"+temp.toUpperCase()+"</td>");
			        					posts.append("<td><a href = \"http://www.amazon."+suff+"/dp/"+product.getAsin()+"\">"+sku+"</a></td>");
			        					posts.append("<td style=\"color:"+(flag==1?"red":"green")+"\">"+desc+"</td>");
			        					posts.append("<td>"+reason+"</td>");
			        					posts.append("</tr>");
			        				}
			        				posts.append("</table>");
			        			}
		        			}catch(Exception e){
		        				posts= new StringBuffer("");
		        				LOGGER.info("帖子价格浮动渲染异常",e);
		        			}
		        			if(posts!=null&&posts.length()>0){
		        				contents.append(posts);
		        			}
		        			
		        			/**折扣订单统计**/
		        			StringBuffer promotions= getPromotionsOrder(country);
		        			if(promotions!=null&&promotions.length()>0){
		        				contents.append(promotions);
		        			}
		        			
		        			//LOGGER.info("断货渲染start"+pattern.format(new Date()));
		        			StringBuffer overAndOut= new StringBuffer("");
		        			try{
		        				overAndOut.append("<table width='90%' style='border:1px solid #7FFFD4;color:#666; border-collapse:collapse;'>");
		        				beforeDate=new Date();
		        				if(refOverMap.get(country)!=null){
		        				   List<String> refOutOfList=refOverMap.get(country);
		        				   overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  padding:0 1em 0;background-color:#9ACD32;'><td colspan='10'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"积压详情<span style='font-weight: bold;font-size:15px'>(库销比大于3个月)</span></span></td></tr>");
								   overAndOut.append("<tr style='background-repeat:repeat-x;height:30px;  padding:0 1em 0;background-color:#96FED1; '><th colspan='3'>产品名</th><th>fba库存</th><th>30天销量</th><th colspan='3'>产品名</th><th>fba库存</th><th>30天销量</th></tr>");
								   for(int i =0;i<refOutOfList.size();i++){
									    String[] info=refOutOfList.get(i).split(",");//name,sales30,fbaQuantiy;
										String name=info[0];
										String sales30=info[1];
										String fbaQuantiy=info[2];
										int index = i+1;
										if(index%2==1){
											overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;   padding:0 1em 0;background-color:#f5fafe; '><td  colspan='3' style='border:1px solid #7FFFD4'>"+name+"</td><td style='border:1px solid #7FFFD4'>"+fbaQuantiy+"</td><td style='border:1px solid #7FFFD4'>"+sales30+"</td>");
										}
										if(index%2==0||index==refOverMap.get(country).size()){
											if(index%2==1){
												overAndOut.append("<td colspan='6' style='border:1px solid #7FFFD4'>&nbsp;</td></tr>");
											}else{
												overAndOut.append("<td colspan='3' style='border:1px solid #7FFFD4'>"+name+"</td><td style='border:1px solid #7FFFD4'>"+fbaQuantiy+"</td><td style='border:1px solid #7FFFD4'>"+sales30+"</td></tr>");
											}
										}
									}
								}
		        				//LOGGER.info("积压详情end"+(new Date().getTime()-beforeDate.getTime()));
					        			//refOverMap;    key:country  value:sku,name,quantum,断货日期;
		        				beforeDate=new Date();
					        			if(refOutOfMap.get(country)!=null){
											//断货
					        				List<String> refOverList=refOutOfMap.get(country);
											if(refOverList!=null&&refOverList.size()>0){
											   overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  padding:0 1em 0;background-color:#9ACD32;'><td colspan='10'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"断货详情</span></td></tr>");
											   overAndOut.append("<tr style='background-repeat:repeat-x;height:30px;  padding:0 1em 0;background-color:#96FED1; '><th>产品名</th><th>Sku</th><th>断货天</th><th>断货日</th><th>帖子情况</th><th>产品名</th><th>Sku</th><th>断货天</th><th>断货日</th><th>帖子情况</th></tr>");
											   for(int i =0;i<refOverList.size();i++){
												    String[] info=refOverList.get(i).split(",");
													String sku = info[0];
													String name=info[1];
													String outDateStr  = info[3];
													String quantum = info[2];;
													
													//String asin=amazonProduct2Service.findAsin(country,sku);
													String asin=asinMap.get(country+"_"+sku);
													String suffix=country;
						        					if("jp,uk".contains(country)){
						        						suffix="co."+country;
						        					}else if("mx".equals(country)){
						        						suffix="com."+country;
						        					}
													int index = i+1;
													if(index%2==1){
														overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;   padding:0 1em 0;background-color:#f5fafe; '><td><a href=http://www.amazon."+suffix+"/dp/"+asin+" target='_blank'>"+name+"</a></td><td style='border:1px solid #7FFFD4'>"+sku+"</td><td style='border:1px solid #7FFFD4'>"+quantum+"</td><td style='border:1px solid #7FFFD4'>"+outDateStr.split("-")[0]+"</td><td style='border:1px solid #7FFFD4;"+(postsSet.contains(country+"_"+asin)?"":"color:red;")+"' >"+(postsSet.contains(country+"_"+asin)?"正常":"异常")+"</td>");
													}
													if(index%2==0||index==refOverList.size()){
														if(index%2==1){
															overAndOut.append("<td colspan='5' style='border:1px solid #7FFFD4'>&nbsp;</td></tr>");
														}else{
															overAndOut.append("<td style='border:1px solid #7FFFD4'><a href=http://www.amazon."+suffix+"/dp/"+asin+" target='_blank'>"+name+"</a></td><td style='border:1px solid #7FFFD4'>"+sku+"</td><td style='border:1px solid #7FFFD4'>"+quantum+"</td><td style='border:1px solid #7FFFD4'>"+outDateStr.split("-")[0]+"</td><td style='border:1px solid #7FFFD4;"+(postsSet.contains(country+"_"+asin)?"":"color:red;")+"'>"+(postsSet.contains(country+"_"+asin)?"正常":"异常")+"</td></tr>");
														}
													}
												}
											}
										}  
					        			//LOGGER.info("断货详情end"+(new Date().getTime()-beforeDate.getTime()));
										//Map<String,Map<String,List<String>>> refPreOutOfMap
					        			//refPreOutOfMap key:country   key:sku,name,sales30,fbaQuantiy,tranQuantity,canSaleDays  value:sku,country,price,inventoryQuantity
					        			beforeDate=new Date();
					        			if(refPreOutOfMap.get(country)!=null){
											if(refPreOutOfMap.get(country)!=null&&refPreOutOfMap.get(country).size()>0){
												Map<String,List<String>> map=refPreOutOfMap.get(country);
												overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  padding:0 1em 0;background-color:#9ACD32;'><td colspan='10'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"即将断货详情</span></td></tr>");
												if(!"jp,ca,com,mx,".contains(country)){
													overAndOut.append("<tr style='background-repeat:repeat-x;height:30px;  padding:0 1em 0;background-color:#96FED1; '><th>产品名</th><th>可售天</th><th>fba(实)</th><th>fba(总)</th><th>31天销量</th><th colspan='5'>Cross Sku</th></tr>");
												}else{
													overAndOut.append("<tr style='background-repeat:repeat-x;height:30px;  padding:0 1em 0;background-color:#96FED1; '><th>产品名</th><th>可售天</th><th>fba(实)</th><th>fba(总)</th><th>31天销量</th><th>产品名</th><th>可售天</th><th>fba(实)</th><th>fba(总)</th><th>31天销量</th></tr>");
												}
												int preI=0;
												for (Map.Entry<String,List<String>> entryMap :  map.entrySet()) {
												    String key = entryMap.getKey();
													String[] info=key.split(",");
													String name=info[0];
													String shortName = name.substring(name.indexOf(" ")+1);
													String fbaQuantiy=info[1];
													String fbaTotal=info[2];
													String canSaleDays = info[3];
													String sale31Days =info[4];
													if(!"jp,ca,com,mx,".contains(country)){
														overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;   padding:0 1em 0;background-color:#f5fafe; '>");
														overAndOut.append("<td style='border:1px solid #7FFFD4'>"+shortName+"</td><td style='border:1px solid #7FFFD4'>"+canSaleDays+"</td><td style='border:1px solid #7FFFD4'>"+fbaQuantiy+"</td><td style='border:1px solid #7FFFD4'>"+fbaTotal+"</td><td style='border:1px solid #7FFFD4'>"+sale31Days+"</td>");
													
														StringBuffer buf= new StringBuffer();
														List<String> crossSkus=entryMap.getValue();
														for (String skuCountry : crossSkus) {
															String arr[]=skuCountry.split(",");
															if(!"(请上贴)".equals(arr[1])){
																	buf.append("<span style='color:red'>"+arr[0]+",价格:"+arr[1]+",库存数:"+arr[2]+"</span><br/>");
															}else{
																buf.append(arr[0]+",价格:"+arr[1]+",库存数:"+arr[2]+"<br/>");
															}
														}
														overAndOut.append("<td colspan='5' style='border:1px solid #7FFFD4' >"+buf.toString()+"</td></tr>");
													}else{
														preI++;
														if(preI%2==1){
															overAndOut.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;   padding:0 1em 0;background-color:#f5fafe; '>");
															overAndOut.append("<td style='border:1px solid #7FFFD4'>"+shortName+"</td><td style='border:1px solid #7FFFD4'>"+canSaleDays+"</td><td style='border:1px solid #7FFFD4'>"+fbaQuantiy+"</td><td style='border:1px solid #7FFFD4'>"+fbaTotal+"</td><td style='border:1px solid #7FFFD4'>"+sale31Days+"</td>");
														}else{
															if(preI%2==0||preI==map.size()){
																if(preI%2==1){
																	overAndOut.append("<td colspan='5' style='border:1px solid #7FFFD4'>&nbsp;</td></tr>");
																}else{
																	overAndOut.append("<td style='border:1px solid #7FFFD4'>"+shortName+"</td><td style='border:1px solid #7FFFD4'>"+canSaleDays+"</td><td style='border:1px solid #7FFFD4'>"+fbaQuantiy+"</td><td style='border:1px solid #7FFFD4'>"+fbaTotal+"</td><td style='border:1px solid #7FFFD4'>"+sale31Days+"</td></tr>");
																}
															}
														}
													}
												}
											}
										} 
					        			//LOGGER.info("即将断货详情end"+(new Date().getTime()-beforeDate.getTime()));
								overAndOut.append("</table><br/><br/>");
		        			}catch(Exception e){
		        				overAndOut=new StringBuffer(""); 
		        				LOGGER.info("积压断货渲染异常",e);
		        			}
		        			if(overAndOut!=null&&overAndOut.length()>0){
		        				contents.append(overAndOut);
		        			}
		        		//	LOGGER.info("断货渲染end"+pattern.format(new Date()));
		        			
		        			
		        			StringBuffer startChange= new StringBuffer("");
		        			try{
		        				startChange.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			        			Map<String,AmazonPostsDetail> data=starMap.get(country);
			        			beforeDate=new Date();
			        			if(data!=null&&data.size()>0){
			        				int flag=0;
			        				for (Map.Entry<String,AmazonPostsDetail> entryData : data.entrySet()) {
			        				    String asin = entryData.getKey();
			        					AmazonPostsDetail post=entryData.getValue();
			        					String[] starStr=post.getCompareStar().split(",");
			        					Float compare=(new BigDecimal(starStr[0])).subtract(new BigDecimal(starStr[1])).floatValue();
			        					String suffix=post.getCountry();
			        					if("jp,uk".contains(post.getCountry())){
			        						suffix="co."+post.getCountry();
			        					}else if("mx".equals(post.getCountry())){
			        						suffix="com."+post.getCountry();
			        					}
			        					String key=asin+"_"+country;
			        					Map<String,List<String>> review=reviewMap.get(key);
			        					if(review!=null&&review.size()>0){
			        						if(flag==0){
			        							startChange.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='9'><span style='font-weight: bold;font-size:25px'>星级评分变动情况表<span style='font-weight: bold;font-size:15px'>(因网络问题有可能导致抓取数据不完整性而引起的误差,请自行核实)</span></span></td></tr>");
			        							startChange.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '>");
			        							startChange.append("<th  style='width:20%'>product_name</th><th  style='width:10%'>昨日评分</th>");
			        							startChange.append("<th  style='width:10%'>今日评分</th>");
			        							startChange.append("<th  style='width:10%'>5 star</th><th  style='width:10%'>4 star</th>");
			        							startChange.append("<th  style='width:10%'>3 star</th><th  style='width:10%'>2 star</th>");
			        							startChange.append("<th  style='width:10%'>1 star</th><th  style='width:10%'>总共评论</th>");
			        							startChange.append("</tr>");
			        						}
			        						startChange.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
			        						startChange.append("<td><a href=http://www.amazon."+suffix+"/dp/"+post.getAsin()+" target='_blank'>"+post.getProductName()+"</a></td>");
			        						startChange.append("<td><font color="+(compare>0?"green":"red")+">"+starStr[1]+"</font></td>");
			        						startChange.append("<td><font color="+(compare>0?"green":"red")+">"+starStr[0]+"</font></td>");
				        						List<String> reviewList5=review.get("5");
			        							if(reviewList5!=null&&reviewList5.size()>0){
			        								startChange.append("<td>");
			        								for (String reviewAsin: reviewList5) {
			        									startChange.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='green'>"+5+"</font></a>&nbsp;&nbsp;");
			        								}
			        								startChange.append("</td>");
			        							}else{
			        								startChange.append("<td></td>");
			        							}
			        							
			        							List<String> reviewList4=review.get("4");
			        							if(reviewList4!=null&&reviewList4.size()>0){
			        								startChange.append("<td>");
			        								for (String reviewAsin: reviewList4) {
			        									startChange.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='green'>"+4+"</font></a>&nbsp;&nbsp;");
			        								}
			        								startChange.append("</td>");
			        							}else{
			        								startChange.append("<td></td>");
			        							}
			        							
			        							List<String> reviewList3=review.get("3");
			        							if(reviewList3!=null&&reviewList3.size()>0){
			        								startChange.append("<td>");
			        								for (String reviewAsin: reviewList3) {
			        									startChange.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='red'>"+3+"</font></a>&nbsp;&nbsp;");
			        								}
			        								startChange.append("</td>");
			        							}else{
			        								startChange.append("<td></td>");
			        							}
			        							
			        							List<String> reviewList2=review.get("2");
			        							if(reviewList2!=null&&reviewList2.size()>0){
			        								startChange.append("<td>");
			        								for (String reviewAsin: reviewList2) {
			        									startChange.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='red'>"+2+"</font></a>&nbsp;&nbsp;");
			        								}
			        								startChange.append("</td>");
			        							}else{
			        								startChange.append("<td></td>");
			        							}
			        							
			        							List<String> reviewList1=review.get("1");
			        							if(reviewList1!=null&&reviewList1.size()>0){
			        								startChange.append("<td>");
			        								for (String reviewAsin: reviewList1) {
			        									startChange.append("<a href=http://www.amazon."+suffix+"/review/"+reviewAsin+" target='_blank'><font color='red'>"+1+"</font></a>&nbsp;&nbsp;");
			        								}
			        								startChange.append("</td>");
			        							}else{
			        								startChange.append("<td></td>");
			        							}
			        							
			        							startChange.append("<td>"+post.getStar1()+"</td>");
			        							startChange.append("</tr>");
			        						    flag++;
			        					}
			        				}
			        				
			        			}
			        		//	LOGGER.info("星级评分变动情况表end"+(new Date().getTime()-beforeDate.getTime()));
			        			
			        			beforeDate=new Date();
			        			List<AmazonPostsDetail> lowStar=lowStarMap.get(country);	
			        			if(lowStar!=null&&lowStar.size()>0){
			        				startChange.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='9'><span style='font-weight: bold;font-size:25px'>低分情况表<span style='font-weight: bold;font-size:15px'>(单帖评分,非组合帖评分)</span></span></td></tr>");
			        				startChange.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '>");
			    				
			        				startChange.append("<th>product_name</th>");
			        				startChange.append("<th>评分</th>");
			        				startChange.append("<th>5 star</th><th>4 star</th>");
			        				startChange.append("<th>3 star</th><th colspan='2'>2 star</th>");
			        				startChange.append("<th colspan='2'>1 star</th>");
			        				startChange.append("</tr>");
			        				for (AmazonPostsDetail detail : lowStar) {
			        					String suffix=detail.getCountry();
			        					if("jp,uk".contains(detail.getCountry())){
			        						suffix="co."+detail.getCountry();
			        					}else if("mx".equals(detail.getCountry())){
			        						suffix="com."+detail.getCountry();
			        					}
			        					startChange.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
			        					startChange.append("<td><a href=http://www.amazon."+suffix+"/dp/"+detail.getAsin()+" target='_blank'>"+detail.getProductName()+"</a></td>");
			        					startChange.append("<td>"+detail.getStar()+"</td>");
			        					startChange.append("<td>"+detail.getStar5()+"</td>");
			        					startChange.append("<td>"+detail.getStar4()+"</td>");
			        					startChange.append("<td>"+detail.getStar3()+"</td>");
			        					startChange.append("<td colspan='2'>"+detail.getStar2()+"</td>");
			        					startChange.append("<td colspan='2'>"+detail.getStar1()+"</td>");
			        					startChange.append("</tr>");
			        					
			        				}
			        			}
			        		//	LOGGER.info("低分情况表end"+(new Date().getTime()-beforeDate.getTime()));
			        			
			        			beforeDate=new Date();
			        			Map<String,List<AmazonCatalogRank>> temp=mapList.get(country);
			        			if(temp!=null&&temp.size()>0){
			        				startChange.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32'><td colspan='9'><span style='font-weight: bold;font-size:25px'>排名变动情况表</span></td></tr>");
			        				startChange.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>product_name</th>");
			        				startChange.append("<th>catalog_name</th><th>昨日排名</th>");
			        				startChange.append("<th colspan='3'>今日排名</th><th colspan='3'>变动情况</th>");
			        				startChange.append("</tr>");
			        				for (Map.Entry<String, List<AmazonCatalogRank>> entryTemp : temp.entrySet()) {
			        				    String name = entryTemp.getKey();
			        				    List<AmazonCatalogRank> rankList = entryTemp.getValue();
			        					int num=0;
			        					int length=rankList.size();
			        					for (AmazonCatalogRank rank :rankList) {
				        						String[] rankStr=rank.getRankStr().split(",");
				        						String link=rank.getCountry();
				        						String suffix=rank.getCountry();
				        						Integer compare=Integer.parseInt(rankStr[0])-Integer.parseInt(rankStr[1]);
			        							if("jp,uk".contains(rank.getCountry())){
				        							suffix="co."+rank.getCountry();
				        							link = "co."+link;
				        						}else if("mx".equals(rank.getCountry())){
				        							suffix="com."+rank.getCountry();
				        							link = "com."+link;
				        						}
				        						//String catalog="http://www.amazon."+link+"/gp/bestsellers/computers/"+rank.getCatalog();
			        							String catalog="";
			        							if("com".equals(country)){
			        								catalog="http://www.amazon."+link+"/gp/bestsellers/pc/"+rank.getCatalog();
			        							}else if("it,ca".contains(country)){
			        								catalog="http://www.amazon."+link+"/gp/bestsellers/electronics/"+rank.getCatalog();
			        							}else{
			        								catalog="http://www.amazon."+link+"/gp/bestsellers/computers/"+rank.getCatalog();
			        							}
				        						startChange.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				        						if(num==0){
				        							startChange.append("<td rowspan="+length+"><a href=http://www.amazon."+suffix+"/dp/"+rank.getAsin()+" target='_blank'>"+rank.getProductName()+"</a></td>");
				        							num++;
				        						}
				        						startChange.append("<td><a href="+catalog+" target='_blank'>"+(StringUtils.isBlank(rank.getCatalogName())?"未找到目录名称":rank.getCatalogName())+"</a></td>");
				        						startChange.append("<td><font color="+(compare>0?"red":"green")+">"+rankStr[1]+"</font></td>");
				        						startChange.append("<td  colspan='3'><font color="+(compare>0?"red":"green")+">"+rankStr[0]+"</font></td>");
				        						startChange.append("<td  colspan='3'><font color="+(compare>0?"red":"green")+">"+(compare>0?("↓ "+compare+"名"):("↑ "+compare.toString().replace("-", "")+"名"))+"</font></td>");
				        						startChange.append("</tr>");
			        					}
			        				}
			        			}
			        			//LOGGER.info("排名变动情况表end"+(new Date().getTime()-beforeDate.getTime()));
			        			startChange.append("</table>");
		        			}catch(Exception e){
		        				startChange=new StringBuffer("");
		        				LOGGER.info("星级变动渲染异常",e);
		        			}
		        			if(startChange!=null&&startChange.length()>0){
		        				contents.append(startChange);
		        			}
		        			
		        			
		        			StringBuffer fbaInventroy= getFbaInventroy(country);
		        			if(fbaInventroy!=null&&fbaInventroy.length()>0){
		        				contents.append(fbaInventroy);
		        			}
		        			
		        			contents.append("<br/><br/><br/>");
		        			
		        		}
		        	}
		        	
		        	if(StringUtils.isNotEmpty(contents)){
		    			Date date = new Date();
		    			String toEmail="";
		    			StringBuffer buf= new StringBuffer();
		    			for (String set :newCompose.get(type).get(keyStr)) {
		    				buf.append(set+",");
						}
		    			toEmail = buf.toString();
		    					
		    			toEmail=toEmail.substring(0, toEmail.length()-1);
		    		//	LOGGER.info("======================================"+toEmail);
		    			//String  toAddress="michael@inateck.com";
		    			String  toAddress=toEmail;
		    			final MailInfo mailInfo = new MailInfo(toAddress,"ERP早报("+DateUtils.getDate("yyyy/MM/dd")+")",date);
		    			mailInfo.setContent(contents.toString());
		    			new Thread(){
		    			    public void run(){
		    			    	mailManager.send(mailInfo);
		    				}
		    			}.start();
		    		}
		           }
				}
			} 
	       
	        try{
	        	//Integer count=psiProductService.updateIsNew();
	        	Integer count=productEliminateService.updateIsNew(); //区分平台、颜色
	    		LOGGER.info("更新超过180天新品为普通品结束,影响行："+count);
        	}catch(Exception e){
        		LOGGER.error("更新超过180天新品为普通品异常！",e);
        	}
	       
	        try{
	        	List<PsiProductEliminate> addList = Lists.newArrayList();
	    		List<PsiProductEliminate> list = productEliminateService.findNoAddedMonth();
	    		for (PsiProductEliminate eliminate : list) {
	    			SaleReport report = saleReportService.getSaleReport(eliminate.getProductName(),eliminate.getColor(),eliminate.getCountry());
	    			if (report != null) {
	    				String addedMonth = DateUtils.getDate(report.getDate(),"yyyy-MM-dd");
						eliminate.setAddedMonth(addedMonth);
						PsiProduct product = eliminate.getProduct();
						if (product != null && StringUtils.isEmpty(product.getAddedMonth())) {
							product.setAddedMonth(addedMonth);
							productService.save(product);
						}
						productEliminateService.save(eliminate);
						if ("de".equals(eliminate.getCountry())) {
							addList.add(eliminate);
						}
					}
	    		}
	    		//de上架邮件通知相关人员
	    		if (addList.size() > 0) {
	    			StringBuffer contents= new StringBuffer("");
	    	    	StringBuffer contents1= new StringBuffer("");
	    			contents1.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
	    			contents1.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>Product Name</th><th>Added Time</th></tr>");
	    			for (PsiProductEliminate eliminate : addList) {
	    	    		contents1.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
	    	    		contents1.append("<td>"+eliminate.getColorName()+"</td><td>"+eliminate.getAddedMonth()+"</td>");
	    	    		contents1.append("</tr>");
	    			}
	    			contents1.append("</table><br/><br/>");
	    	    	if (StringUtils.isNotEmpty(contents1)) {
	    	        	contents.append("<p><span style='font-size:20px'>Hi all,<br/>&nbsp;&nbsp;&nbsp;&nbsp;The following is products of shelves today：</span></p>");
	    	        	contents.append(contents1);
	    	    	}
	    	    	final MailInfo mailInfo = new MailInfo("online.marketing@inateck.com", "Product Shelves Remind", new Date());
	    	    	mailInfo.setCcToAddress("leehong@inateck.com");
	    			mailInfo.setContent(contents.toString());
	    			new Thread(){
	    			    public void run(){
	    			    	mailManager.send(mailInfo);
	    				}
	    			}.start();
				}
        	}catch(Exception e){
        		WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "自动更新上架时间异常");
        		LOGGER.error("自动更新上架时间异常！",e);
        	}
    		
		}catch (Exception e) {
			LOGGER.error("异常结束！",e);
		}
		
		//free评测订单邮件
		try{
			StringBuffer promotions= new StringBuffer("");
			StringBuffer temp= new StringBuffer("");
			Map<String,List<Object[]>>  map=amazonOrderService.getFreePromotions();
			if(map!=null&&map.size()>0){
				for (Map.Entry<String,List<Object[]>> entry: map.entrySet()) {
				    String country = entry.getKey();
					List<Object[]> tempList= entry.getValue();
					if(tempList!=null&&tempList.size()>0){
						for (Object[] obj: tempList) {
							String type="";
							if(obj[5]!=null){
								if("5".equals(obj[5].toString())||"7".equals(obj[5].toString())){
									type="替代";
								}else if("8".equals(obj[5].toString())){
									type="评测";
								}
							}
							temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+("com".equals(country)?"us":country).toUpperCase()+"</td>");
		        			temp.append("<td>"+(obj[1]==null?"":obj[1].toString())+"</td><td>"+(obj[3]==null?"":obj[3].toString())+"</td><td>"+(obj[6]==null?"":obj[6].toString())+"</td><td>"+(obj[4]==null?"":obj[4].toString())+"</td><td>"+type+"</td></tr>");
						}
					}
				}
				if(temp!=null&&temp.length()>0){
	    			promotions.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
	        		promotions.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='6'><span style='font-weight: bold;font-size:25px'>昨日Free折扣详情</span></td></tr>");
	        		promotions.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>国家</th><th>PromotionIds</th><th>订单号</th><th>产品</th><th>单价</th><th>用途</th></tr>");
	        		promotions.append(temp);
	        		promotions.append("</table><br/><br/>");
	        		
	        		Date date = new Date();
					String  toAddress="amazon-sales@inateck.com,after-sales@inateck.com,marketing_dept@inateck.com,eileen@inateck.com";//
					final MailInfo mailInfo = new MailInfo(toAddress,"昨日Free订单详情"+new SimpleDateFormat("yyyyMMdd").format(date),date);
					mailInfo.setContent(promotions.toString());
					new Thread(){
						public void run(){
							mailManager.send(mailInfo);
						}
					}.start();
				}
			}
		}catch(Exception e){
			LOGGER.error("free评测订单邮件渲染异常！",e);
			e.printStackTrace();
		}
		//微信推送销量信息,折扣订单数量、trackID排名前三
		try {
			if (DateUtils.isHoliday()) {	//只在节假日和周末推送
				LOGGER.info("开始统计昨日销量&折扣信息");
				String goalDateStr = enterpriseGoalService.findByCurrentMonth();
				Map<String, Float> rateMap = amazonProduct2Service.getRateByDate(goalDateStr);
	
				//设置起止时间
				SaleReport saleReport =  new SaleReport();
				Date end = new Date();
				saleReport.setStart(DateUtils.addDays(end, -3));
				saleReport.setEnd(end);
				Map<String, Map<String, SaleReport>>  data = saleReportService.getSales(saleReport, rateMap);
				
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy年MM月dd日");
				Date date = DateUtils.addDays(new Date(), -1);
				StringBuffer sb = new StringBuffer();
				String tip = "节假日销量数据提醒(欧元)：\n时间:" + format1.format(date) + "\n\n";
				//sb.append("节假日销量数据提醒(欧元)：\n时间:" + format1.format(date) + "\n\n");
				String key = format.format(date);
				String total = "";
				//折扣订单数量
				Map<String, Integer> promotions = amazonOrderService.findPromotions();
				//折扣ID
				Map<String,Map<String, Integer>> promotionIds = amazonOrderService.findPromotionsId();
				for (Map.Entry<String,Map<String,SaleReport>> entryData: data.entrySet()) {
				    String string = entryData.getKey();
					Map<String, SaleReport> rs = entryData.getValue();
					if (!"total".equals(string)) {
						if ("eu".equals(string) || "en".equals(string) || "unEn".equals(string)) {
							continue;
						}
						for (Map.Entry<String,SaleReport> entryRs : rs.entrySet()) {
						    String str = entryRs.getKey();
						    SaleReport report = entryRs.getValue();
							if (key.equals(str)) {
								sb.append("国家：" + SystemService.countryNameMap.get(string)+"\n");
								sb.append(report.getSales()==null?"销售额:\n": "销售额:" + report.getSales()+ "(€)\n");
								sb.append("销量:" + report.getSalesVolume() + "\n");
								if (promotions.get(string) != null) {
									sb.append("折扣销量:" + promotions.get(string) + "\n");
								}
								if (promotionIds.get(string) != null && promotionIds.get(string).size() > 0) {
									Map<String, Integer> idAndQuantity = promotionIds.get(string);
									int i = 0;
									sb.append("销量排名前三折扣信息:\n");
									for (Map.Entry<String,Integer> entry : idAndQuantity.entrySet()) {
									    String promotionId = entry.getKey();
										if (i == 3) {
											break;
										}
										if (promotionId.contains(",")) {
											promotionId = promotionId.split(",")[0];//避免字符串太长超出微信限制
										}
										sb.append(promotionId + ",销量:" + entry.getValue() + "\n");
										i++;
									}
								}
								sb.append("\n");
							}
						}
					} else {
						for (Map.Entry<String,SaleReport> entry : rs.entrySet()) {
						    String str = entry.getKey();
						    SaleReport report = entry.getValue();
							if (key.equals(str)) {
								total = "总计\n销售额:" + report.getSales()+ "(€)\n销量:" + report.getSalesVolume() + "\n";
							}
						}
					}
				}
				total = total + "\n";
				try {
					//微信发送运营部&系统开发部
					WeixinSendMsgUtil.sendTextMsg(WeixinSendMsgUtil.getToken(), "maik|frank", "3|7", null, ParamesAPI.appId, tip + total + sb.toString(), "0");
				} catch (Exception e) {
					//发送失败则10s后重发一次
					Thread.sleep(10000l);
					WeixinSendMsgUtil.sendTextMsg(WeixinSendMsgUtil.getToken(), "maik|frank", "3|7", null, ParamesAPI.appId, tip + total + sb.toString(), "0");
				}
				LOGGER.info("微信推送销量信息任务结束");
			}
		} catch (Exception e) {
			LOGGER.error("微信推送销量信息失败", e);
			WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "微信推送销量信息失败");
		}
		
		//淘汰产品库存为0，官网下架
		try{
			List<Object[]> list = productEliminateService.findOffWebsiteProduct();
			if(list!=null && list.size()>0){
				Map<String,Integer> products = productEliminateService.findStockProduct();
				Map<String, Set<String>> result = Maps.newHashMap();
				Map<String, String> asinAndProduct = Maps.newHashMap();
				for (Object[] objs : list) {
					String hasPower = objs[2].toString();
					String country = objs[1].toString();
					String pname = objs[0].toString();
					String asin = objs[3].toString();
					String key = pname+"_"+country;
					if("1".equals(hasPower)){
						if("de,fr,es,it".contains(country)){
							Integer num = products.get(pname+"_eu");
							Integer num1 = products.get(pname+"_uk");
							if(num!=null&&num1!=null&&num.equals(num1)){
								Set<String> set = result.get(country);
								if(set==null){
									set = Sets.newHashSet();
									result.put(country, set);
								}
								set.add(asin);
								asinAndProduct.put(asin, pname);
							}
						}else{
							Integer num = products.get(key);
							if(num!=null&&num==0){
								Set<String> set = result.get(country);
								if(set==null){
									set = Sets.newHashSet();
									result.put(country, set);
								}
								set.add(asin);
								asinAndProduct.put(asin, pname);
							}
						}
					}else{
						Integer num = 0 ;
						if("de,fr,es,it,uk".contains(country)){
							num = products.get(pname+"_eu");
						}else{
							num = products.get(key);
						}
						if(num!=null&&num==0){
							Set<String> set = result.get(country);
							if(set==null){
								set = Sets.newHashSet();
								result.put(country, set);
							}
							set.add(asin);
							asinAndProduct.put(asin, pname);
						}
					}
				}
				Map<String, String> note = Maps.newHashMap();
				for (Map.Entry<String,Set<String>> entry : result.entrySet()) {
				    String  country = entry.getKey();
					Map<String,String> rs = MagentoClientService.catalogProductDelete(country, entry.getValue());
					Set<String> names = Sets.newHashSet();
					Set<String> updates = Sets.newHashSet();
					for (Map.Entry<String,String> entryRs : rs.entrySet()) {
					    String asin = entryRs.getKey();
						if("1".equals(entry.getValue())){
							names.add(asinAndProduct.get(asin));
						}
						updates.add(asinAndProduct.get(asin));
					}
					if(names.size()>0){
						note.put(country, names.toString().replace("[","").replace("]",""));
					}
					if(updates.size()>0){
						productEliminateService.updateOffWebsite(country, updates);
					}
				}
				if(note.size()>0){
					StringBuffer noteStr = new StringBuffer();
					noteStr.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
					for (Map.Entry<String,String> entry : note.entrySet()) {
					    String country = entry.getKey();
						noteStr.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td><span style='font-weight: bold;font-size:25px'>"+(country.equals("com")?"US":country.toUpperCase())+"淘汰0库存产品官网下架列表</span></td></tr>");
						noteStr.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><td><span>"+entry.getValue()+"</span></td></tr>");
					}
					noteStr.append("</table>");
					List<User> users = systemService.findUserByPermission("sys:website:manage");
					if(users!=null && users.size()>0){
						String  toAddress="";
						StringBuffer buf= new StringBuffer();
						for (User user : users) {
							buf.append(user.getEmail()+",");
						}
						toAddress = buf.toString();
						toAddress = toAddress.substring(0,toAddress.length()-1);
		    			final MailInfo mailInfo = new MailInfo(toAddress,"官网产品自动下架提醒("+DateUtils.getDate("yyyy/MM/dd")+")",new Date());
		    			mailInfo.setContent(noteStr.toString());
		    			new Thread(){
		    			    public void run(){
		    			    	mailManager.send(mailInfo);
		    				}
		    			}.start();
					}
				}
			}
		}catch(Exception e){
			LOGGER.error("自动官网下架出问题了",e);
		}
		
		
		//监控忘记出库的产品
		try{
			fbaInboundWarning();
			LOGGER.info("每天监控fba未出库，状态却为Working以后的状态检查经过");
		}catch(Exception e){
			LOGGER.error("每天监控fba未出库，状态却为Working以后的状态异常",e);
		}
		
		//每天监控FBA到货异常提醒(到货5天收货未达到80%)
		try{
			fbaReceiveWarning();
		}catch(Exception e){
			LOGGER.error("FBA到货异常提醒失败",e);
		}
		
		//每天监控备品满整箱提醒
//		try{
//			inventoryPackWarning();
//			LOGGER.info("每天监控备品满整箱提醒检查经过");
//		}catch(Exception e){
//			LOGGER.error("每天监控备品满整箱提,异常!!!",e);
//		}
				
		
	}
	
	//每天监控fba未出库，状态却为Working以后的状态
	public void fbaInboundWarning(){
		Map<String,List<Object[]>>  rs = fbaInboundService.getFbaInboundWarning();
		if(rs!=null&&rs.size()>0){
			String address="";
			List<Object[]> list = null;
			if(rs.get("0")!=null){
				list=rs.get("0");
				address="supply-chain@inateck.com,amazon-sales@inateck.com,erp_development@inateck.com";
			}else if(rs.get("1")!=null){
				list=rs.get("1");
				address="erp_development@inateck.com";
			}
			if(list!=null&&list.size()>0){
				StringBuffer contents= new StringBuffer("");
				contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>ShipmentId</th><th>Name</th><th>country</th><th>Status</th></tr>");
				for(Object[] obj:list){
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>" +
							"<td>"+obj[0]+"</td><td>"+obj[1]+"</td><td>"+(obj[2]!=null&&"com".equals(obj[2].toString())?"us":obj[2])+"</td><td>"+obj[3]+"</td></tr>");
				}   
				contents.append("</table>");
				final MailInfo mailInfo = new MailInfo(address,"fba贴为Working以后状态,却没及时出库邮件提醒"+DateUtils.getDate("-yyyy/M/dd"),new Date());
				mailInfo.setCcToAddress(UserUtils.logistics1);
				mailInfo.setContent(contents.toString());
				new Thread(){
					public void run(){
						mailManager.send(mailInfo);
					}
				}.start();
				
			}
		}
	}
	
	public void inventoryPackWarning(){
		List<Object[]>  list = this.inventoryService.getOverPackQuantity();
			if(list!=null&&list.size()>0){
				String address="supply-chain@inateck.com,tim@inateck.com";
				StringBuffer contents= new StringBuffer("");
				contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>" +
						"<th>Product Name</th><th>Sku</th><th>新品数</th><th>备品数</th><th>装箱数</th><th>可合成箱数</th></tr>");
				for(Object[] obj:list){
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>" +
							"<td>"+obj[0]+"</td><td>"+obj[1]+"</td><td>"+obj[2]+"</td><td>"+obj[3]+"</td><td>"+obj[4]+"</td><td>"+obj[5]+"</td></tr>");
				}   
				contents.append("</table>");
				final MailInfo mailInfo = new MailInfo(address,"理诚仓库备品数凑整箱提醒"+DateUtils.getDate("-yyyy/M/dd"),new Date());
				mailInfo.setContent("Hi,All<br/>仓库大货的散箱数加上备品数，可整理成整箱的产品提醒(请把备品整理成整箱并转化成new)<br/>"+contents.toString());
				new Thread(){
					public void run(){
						mailManager.send(mailInfo);
					}
				}.start();
				
			}
	}
	
	/**
	 * 折扣订单统计
	 */
    public StringBuffer getPromotionsOrder(){
    	StringBuffer promotions= new StringBuffer("");
    	try{
    		StringBuffer temp= new StringBuffer("");
    		for (String country : countryList) {
		    		Date end=new Date();
		        	Date start=DateUtils.addDays(end, -1);
		        	start.setHours(0);
		        	start.setSeconds(0);
		        	start.setMinutes(0);
		        	List<Map<String,Object>> data = amazonOrderService.countAllPromotions(start,end,country);
		        	if(data!=null&&data.size()>0){
		        		String suffix=country;
						if("jp,uk".contains(country)){
							suffix="co."+country;
						}else if("mx".equals(country)){
							suffix="com."+country;
						}
		        		for (Map<String, Object> map : data) {
		        			String countryStr=suffix+"/dp/"+map.get("asin");
		        			temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td><a href=http://www.amazon."+countryStr+" target='_blank'>"+map.get("name")+"</a></td><td>"+("com".equals(country)?"us":country).toUpperCase()+"</td>");
		        			temp.append("<td "+("闪购".equals(map.get("promotionIds"))?"style='color:red;'":"")+">"+map.get("promotionIds")+"</td><td>"+map.get("promotionDiscount")+"</td><td>"+map.get("sum")+"</td><td>"+map.get("sales")+"</td></tr>");
						}
		        	}
    		}
    		if(temp!=null&&temp.length()>0){
    			promotions.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
        		promotions.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='8'><span style='font-weight: bold;font-size:25px'>昨日折扣订单统计</span></td></tr>");
        		promotions.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>ProductName</th><th>Country</th><th>PromotionIds</th><th>PromotionDiscount</th><th>ItemSum</th><th>Sales</th></tr>");
        		promotions.append(temp);
        		promotions.append("</table><br/><br/>");
			}
    	}catch(Exception e){
    		promotions= new StringBuffer("");
			LOGGER.error("折扣订单统计渲染异常！",e);
    	}
    	return promotions;
    }
	
	/**
	 * 折扣订单统计
	 */
    public StringBuffer getPromotionsOrder(String country){
    	StringBuffer promotions= new StringBuffer("");
    	try{
    		Date end=new Date();
        	Date start=DateUtils.addDays(end, -1);
        	start.setHours(0);
        	start.setSeconds(0);
        	start.setMinutes(0);
        	List<Map<String,Object>> data = amazonOrderService.countAllPromotions(start,end,country);
        	if(data!=null&&data.size()>0){
        		promotions.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
        		promotions.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='7'><span style='font-weight: bold;font-size:25px'>昨日折扣订单统计</span></td></tr>");
        		promotions.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>ProductName</th><th>PromotionIds</th><th>PromotionDiscount</th><th>ItemSum</th><th>Sales</th></tr>");
        		String suffix=country;
				if("jp,uk".contains(country)){
					suffix="co."+country;
				}else if("mx".equals(country)){
					suffix="com."+country;
				}
        		for (Map<String, Object> map : data) {
        			String countryStr=suffix+"/dp/"+map.get("asin");
        			promotions.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td><a href=http://www.amazon."+countryStr+" target='_blank'>"+map.get("name")+"</a></td>");
        			promotions.append("<td "+("闪购".equals(map.get("promotionIds"))?"style='color:red;'":"")+">"+map.get("promotionIds")+"</td><td>"+map.get("promotionDiscount")+"</td><td>"+map.get("sum")+"</td><td>"+map.get("sales")+"</td></tr>");
				}
        		promotions.append("</table>");
        	}
    	}catch(Exception e){
    		promotions= new StringBuffer("");
			LOGGER.error("折扣订单统计渲染异常！",e);
    	}
    	return promotions;
    }
	
    
    /**
	 * 本地帖、fba库存过期
	 * @param country
	 * @return
	 */
	public StringBuffer getFbaInventroy(){
		StringBuffer fbaInventroy= new StringBuffer("");
		try{
			List<List<Object>> postsList=Lists.newArrayList();
			List<PsiInventory> list=new ArrayList<PsiInventory>();
			postsList=amazonProduct2Service.findMfnInventoyInfo("de","Inateck_DE");
			for (List<Object> obj : postsList) {
	        	if(Integer.parseInt(obj.get(4).toString())<Integer.parseInt(obj.get(3).toString()) || (Integer.parseInt(obj.get(4).toString())<15 &&Integer.parseInt(obj.get(3).toString())>0)){
	        		PsiInventory psiInventory=new PsiInventory();
	    			psiInventory.setSku(obj.get(1)==null?"":obj.get(1).toString());
	    			psiInventory.setProductName(obj.get(0)==null?"":obj.get(0).toString());
	    			psiInventory.setCountryCode("de");
	    			psiInventory.setOldQuantity(Integer.parseInt(obj.get(3).toString()));
	    			psiInventory.setAsin(obj.get(2)==null?"":obj.get(2).toString());
	    			psiInventory.setNewQuantity(obj.get(4)==null?0:Integer.parseInt(obj.get(4).toString()));
	    			list.add(psiInventory);
	        	}
			}
			
			if(list!=null&&list.size()>0){
				fbaInventroy.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
				fbaInventroy.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='8'><span style='font-weight: bold;font-size:25px'>本地帖库存预警</span></td></tr>");
				fbaInventroy.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>Sku</th><th>country</th><th>产品名</th><th>亚马逊显示库存</th><th>本地实际库存</th></tr>");
				for(PsiInventory tempPosts:list){
					String suffix=tempPosts.getCountryCode();
					if("jp,uk".contains(tempPosts.getCountryCode())){
						suffix="co."+tempPosts.getCountryCode();
					}else if("mx".equals(tempPosts.getCountryCode())){
						suffix="com."+tempPosts.getCountryCode();
					}
					String countryStr=suffix+"/dp/"+tempPosts.getAsin();
					fbaInventroy.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+tempPosts.getSku()+"</td><td>"+("com".equals(tempPosts.getCountryCode())?"us":tempPosts.getCountryCode()).toUpperCase()+"</td><td><a href=http://www.amazon."+countryStr+" target='_blank'>"+tempPosts.getProductName()+"</a></td><td>"+tempPosts.getOldQuantity()+"</td>");
					fbaInventroy.append("<td>"+tempPosts.getNewQuantity()+"</td></tr>");
				}   
				fbaInventroy.append("</table>");
			}
			
			AmazonFbaHealthReport report=new AmazonFbaHealthReport();
			List<AmazonFbaHealthReport> reportList=amazonFbaHealthReportService.findWarnning(report);
			if(reportList!=null&&reportList.size()>0){
				fbaInventroy.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
				fbaInventroy.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='12'><span style='font-weight: bold;font-size:25px'>FBA库存超时预警</span></td></tr>");
				fbaInventroy.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '>");
				fbaInventroy.append("<th>snapshot_date</th><th>country</th><th>sku</th><th>asin</th><th>inv-age-271<br/>-to-365-days</th><th>inv-age-365<br/>-plus-days</th>");
				fbaInventroy.append("<th>weeks-of<br/>-cover-t7</th><th>weeks-of<br/>-cover-t30</th><th>weeks-of<br/>-cover-t90</th><th>weeks-of<br/>-cover-t180</th><th>weeks-of<br/>-cover-t365</th>");
				fbaInventroy.append("</tr>");
				for(AmazonFbaHealthReport tempReport:reportList){
					fbaInventroy.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+new SimpleDateFormat("yyyy-MM-dd").format(tempReport.getSnapshotDate())+"</td><td>"+tempReport.getSku()+"</td>");
					String country=tempReport.getCountry();
					if("jp,uk".contains(tempReport.getCountry())){
						tempReport.setCountry("co."+tempReport.getCountry());
					}else if("mx".equals(tempReport.getCountry())){
						tempReport.setCountry("com."+tempReport.getCountry());
					}
					fbaInventroy.append("<td>"+("com".equals(country)?"us":country).toUpperCase()+"</td><td><a href=http://www.amazon."+tempReport.getCountry()+"/dp/"+tempReport.getAsin()+" target='_blank'>"+tempReport.getAsin()+"</a></td>" );
					fbaInventroy.append("<td>"+tempReport.getAgeDays365()+"</td><td>"+tempReport.getAgePlusDays365()+"</td>");
					fbaInventroy.append("<td>"+(tempReport.getWeeksCover7()==null?"":tempReport.getWeeksCover7())+"</td><td>"+(tempReport.getWeeksCover30()==null?"":tempReport.getWeeksCover30())+"</td>");
					fbaInventroy.append("<td>"+(tempReport.getWeeksCover90()==null?"":tempReport.getWeeksCover90())+"</td><td>"+(tempReport.getWeeksCover180()==null?"":tempReport.getWeeksCover180())+"</td>");
					fbaInventroy.append("<td>"+(tempReport.getWeeksCover365()==null?"":tempReport.getWeeksCover365())+"</td>");
					fbaInventroy.append("</tr>");
				}   
				fbaInventroy.append("</table><br/><br/>");
			}
		}catch(Exception e){
			fbaInventroy= new StringBuffer("");
			LOGGER.error("本地帖、fba库存过期渲染异常！",e);
		}
		return fbaInventroy;
	}
	
	/**
	 * 本地帖、fba库存过期
	 * @param country
	 * @return
	 */
	public StringBuffer getFbaInventroy(String country){
		StringBuffer fbaInventroy= new StringBuffer("");
		try{
			List<List<Object>> postsList=Lists.newArrayList();
			List<PsiInventory> list=new ArrayList<PsiInventory>();
			if("de".equals(country)){
				postsList=amazonProduct2Service.findMfnInventoyInfo(country,"Inateck_DE");
			}else if("com".equals(country)){
				postsList=amazonProduct2Service.findMfnInventoyInfo(country,"Inateck_US");
			}
			for (List<Object> obj : postsList) {
	        	if(Integer.parseInt(obj.get(4).toString())<Integer.parseInt(obj.get(3).toString()) || (Integer.parseInt(obj.get(4).toString())<15 &&Integer.parseInt(obj.get(3).toString())>0)){
	        		PsiInventory psiInventory=new PsiInventory();
	    			psiInventory.setSku(obj.get(1)==null?"":obj.get(1).toString());
	    			psiInventory.setProductName(obj.get(0)==null?"":obj.get(0).toString());
	    			psiInventory.setCountryCode(country);
	    			psiInventory.setOldQuantity(Integer.parseInt(obj.get(3).toString()));
	    			psiInventory.setAsin(obj.get(2)==null?"":obj.get(2).toString());
	    			psiInventory.setNewQuantity(obj.get(4)==null?0:Integer.parseInt(obj.get(4).toString()));
	    			list.add(psiInventory);
	        	}
			}
			if(list!=null&&list.size()>0){
				fbaInventroy.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
				fbaInventroy.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='7'><span style='font-weight: bold;font-size:25px'>本地帖库存预警</span></td></tr>");
				fbaInventroy.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>Sku</th><th>产品名</th><th>亚马逊显示库存</th><th>本地实际库存</th></tr>");
				for(PsiInventory tempPosts:list){
					String suffix=country;
					if("jp,uk".contains(country)){
						suffix="co."+country;
					}else if("mx".equals(country)){
						suffix="com."+country;
					}
					String countryStr=suffix+"/dp/"+tempPosts.getAsin();
					fbaInventroy.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+tempPosts.getSku()+"</td><td><a href=http://www.amazon."+countryStr+" target='_blank'>"+tempPosts.getProductName()+"</a></td><td>"+tempPosts.getOldQuantity()+"</td>");
					fbaInventroy.append("<td>"+tempPosts.getNewQuantity()+"</td></tr>");
				}   
				fbaInventroy.append("</table>");
			}
			
			AmazonFbaHealthReport report=new AmazonFbaHealthReport();
			report.setCountry(country);
			List<AmazonFbaHealthReport> reportList=amazonFbaHealthReportService.findWarnning(report);
			if(reportList!=null&&reportList.size()>0){
				fbaInventroy.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
				fbaInventroy.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='11'><span style='font-weight: bold;font-size:25px'>FBA库存超时预警</span></td></tr>");
				fbaInventroy.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '>");
				fbaInventroy.append("<th>snapshot_date</th><th>sku</th><th>asin</th><th>inv-age-271<br/>-to-365-days</th><th>inv-age-365<br/>-plus-days</th>");
				fbaInventroy.append("<th>weeks-of<br/>-cover-t7</th><th>weeks-of<br/>-cover-t30</th><th>weeks-of<br/>-cover-t90</th><th>weeks-of<br/>-cover-t180</th><th>weeks-of<br/>-cover-t365</th>");
				fbaInventroy.append("</tr>");
				for(AmazonFbaHealthReport tempReport:reportList){
					fbaInventroy.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+new SimpleDateFormat("yyyy-MM-dd").format(tempReport.getSnapshotDate())+"</td><td>"+tempReport.getSku()+"</td>");
					if("jp,uk".contains(tempReport.getCountry())){
						tempReport.setCountry("co."+tempReport.getCountry());
					}else if("mx".equals(tempReport.getCountry())){
						tempReport.setCountry("com."+tempReport.getCountry());
					}
					fbaInventroy.append("<td><a href=http://www.amazon."+tempReport.getCountry()+"/dp/"+tempReport.getAsin()+" target='_blank'>"+tempReport.getAsin()+"</a></td>" );
					fbaInventroy.append("<td>"+tempReport.getAgeDays365()+"</td><td>"+tempReport.getAgePlusDays365()+"</td>");
					fbaInventroy.append("<td>"+(tempReport.getWeeksCover7()==null?"":tempReport.getWeeksCover7())+"</td><td>"+(tempReport.getWeeksCover30()==null?"":tempReport.getWeeksCover30())+"</td>");
					fbaInventroy.append("<td>"+(tempReport.getWeeksCover90()==null?"":tempReport.getWeeksCover90())+"</td><td>"+(tempReport.getWeeksCover180()==null?"":tempReport.getWeeksCover180())+"</td>");
					fbaInventroy.append("<td>"+(tempReport.getWeeksCover365()==null?"":tempReport.getWeeksCover365())+"</td>");
					fbaInventroy.append("</tr>");
				}   
				fbaInventroy.append("</table>");
			}
		}catch(Exception e){
			fbaInventroy= new StringBuffer("");
			LOGGER.error("本地帖、fba库存过期渲染异常！",e);
		}
		return fbaInventroy;
	}
	
	
	public StringBuffer getEventWarn(Map<String,Map<String,String>> eventIdMap){
		StringBuffer eventId= new StringBuffer("");
		try{
			eventId.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			eventId.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='5'><span style='font-weight: bold;font-size:25px'>Update of Negative Reviews</span></td></tr>");
			eventId.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>NO.</th><th>Country</th><th>Rating change</th><th>Follow-up review</th><th>Negative review deleted or changed to positive</th></tr>");
    		int count=1;
			for (String country : countryList) {
        		if(eventIdMap.get(country)!=null){
        			eventId.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
        			eventId.append("<td>"+(count++)+"</td><td>"+("com".equals(country)?"us":country).toUpperCase()+"</td>");
        			eventId.append("<td>"+(eventIdMap.get(country).get("3")==null?"":eventIdMap.get(country).get("3"))+"</td>");
        			eventId.append("<td>"+(eventIdMap.get(country).get("2")==null?"":eventIdMap.get(country).get("2"))+"</td>");
        			eventId.append("<td>"+(eventIdMap.get(country).get("1")==null?"":eventIdMap.get(country).get("1"))+"</td>");
        			eventId.append("</tr>");
        		}
        	}
			eventId.append("</table><br/><br/>");
		}catch(Exception e){
			eventId= new StringBuffer("");
			LOGGER.error("差评跟帖变更事件ID渲染异常！",e);
		}
		return eventId;
	}
	
	public StringBuffer getErrorFnSkuWarn(List<Object[]> fnSkuList){
		StringBuffer barcodeStr= new StringBuffer("");
		try{
			barcodeStr.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			barcodeStr.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='5'><span style='font-weight: bold;font-size:25px'>ERP条码和实际条码不符</span></td></tr>");
			barcodeStr.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>productName</th><th>sku</th><th>ERP条码</th><th>实际条码</th></tr>");
    		int count=1;
			for (Object[] obj: fnSkuList) {
				barcodeStr.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				barcodeStr.append("<td>"+(count++)+"</td>");
				barcodeStr.append("<td style='color:red;'>"+obj[3]+"</td>");
				barcodeStr.append("<td style='color:red;'>"+obj[0]+"</td>");
				barcodeStr.append("<td style='color:red;'>"+obj[1]+"</td>");
				barcodeStr.append("<td style='color:red;'>"+obj[2]+"</td>");
				barcodeStr.append("</tr>");
        	}
			barcodeStr.append("</table><br/><br/>");
		}catch(Exception e){
			barcodeStr= new StringBuffer("");
			LOGGER.error("ERP条码和实际条码不符渲染异常！",e);
		}
		return barcodeStr;
	}
	
	//0:帖子预警
	public  void noteClaimer(final String address,final String subject,final String content,final String flag){
		new Thread(){
			@Override
			public void run() {
				try{
					MailInfo mailInfo = new MailInfo(address, subject, new Date());
					mailInfo.setContent(HtmlUtils.htmlUnescape(content));
					boolean rs = mailManager.send(mailInfo);
					if(!rs){
						if("0".equals(flag)){
							LOGGER.error("帖子预警邮件发送失败-->"+content);
						}else if("1".equals(flag)){
							LOGGER.error("折扣库存预警发送失败-->"+content);
						}else if("2".equals(flag)){
							LOGGER.error("Title异常发送失败-->"+content);
						}else{
							LOGGER.error("新帖创建提醒邮件发送失败-->"+content);
						}
						
					}
				} catch (Exception e) {
					if("0".equals(flag)){
						LOGGER.error("帖子预警改动邮件",e);
					}else if("1".equals(flag)){
						LOGGER.error("折扣库存预警邮件",e);
					}else if("2".equals(flag)){
						LOGGER.error("Title异常发送失败-->"+content);
					}else{
						LOGGER.error("新帖创建提醒邮件发送失败-->"+content,e);
					}
				}
			}
		}.start();		
	} 
	
	
	public StringBuffer getErrorFnAsinWarn(List<Object[]> asinList){
		StringBuffer barcodeStr= new StringBuffer("");
		try{
			barcodeStr.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			barcodeStr.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='3'><span style='font-weight: bold;font-size:25px'>带电源和键盘产品欧洲5国同Asin</span></td></tr>");
			barcodeStr.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>产品</th><th>Asin</th></tr>");
    		int count=1;
			for (Object[] obj: asinList) {
				barcodeStr.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				barcodeStr.append("<td>"+(count++)+"</td>");
				barcodeStr.append("<td style='color:red;'>"+obj[0]+"</td>");
				barcodeStr.append("<td style='color:red;'>"+obj[2]+"</td>");
				barcodeStr.append("</tr>");
        	}
			barcodeStr.append("</table><br/><br/>");
		}catch(Exception e){
			barcodeStr= new StringBuffer("");
			LOGGER.error("Asin渲染异常！",e);
		}
		return barcodeStr;
	}
	
	public StringBuffer getErrorSameAsin(List<Object[]> asinList){
		StringBuffer barcodeStr= new StringBuffer("");
		try{
			barcodeStr.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			barcodeStr.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='3'><span style='font-weight: bold;font-size:25px'>不同产品同Asin</span></td></tr>");
			barcodeStr.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>国家</th><th>Asin</th><th>产品</th></tr>");
			for (Object[] obj: asinList) {
				barcodeStr.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				barcodeStr.append("<td>"+(obj[1])+"</td>");
				barcodeStr.append("<td>"+obj[0]+"</td>");
				barcodeStr.append("<td style='color:red;'>"+obj[2]+"</td>");
				barcodeStr.append("</tr>");
        	}
			barcodeStr.append("</table><br/><br/>");
		}catch(Exception e){
			barcodeStr= new StringBuffer("");
			LOGGER.error("Asin相同渲染异常！",e);
		}
		return barcodeStr;
	}
	
	public StringBuffer getChangePic(List<Object[]> picList){
		StringBuffer barcodeStr= new StringBuffer("");
		try{
			barcodeStr.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			barcodeStr.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='5'><span style='font-weight: bold;font-size:25px'>图片变更异常</span></td></tr>");
			barcodeStr.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>国家</th><th>产品</th><th>类型</th><th>昨</th><th>今</th></tr>");
			for (Object[] obj: picList) {
				barcodeStr.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				barcodeStr.append("<td>"+(obj[1])+"</td>");
				barcodeStr.append("<td>"+obj[0]+"</td>");
				String type="";
				if("Main".equals(obj[3].toString())){
					type="首图";
				}else{
					type=obj[3].toString();
				}
				barcodeStr.append("<td style='color:red;'>"+type+"</td>");
				barcodeStr.append("<td><img style='width:100px;height:100px' src="+obj[5]+"></td>");
				barcodeStr.append("<td><img style='width:100px;height:100px' src="+obj[4]+"></td>");
				barcodeStr.append("</tr>");
        	}
			barcodeStr.append("</table><br/><br/>");
		}catch(Exception e){
			barcodeStr= new StringBuffer("");
			LOGGER.error("picList不同渲染异常！",e);
		}
		return barcodeStr;
	}
	
	//fba库存积压
		public Map<String,List<String>> overStock(Map<String,Integer> sale30Map,Map<String,String> fbaMap){
			Map<String,List<String>> overMap = Maps.newHashMap();
			Integer type =3;
			//获取积压的sku
			for (Map.Entry<String,String> entry : fbaMap.entrySet()) { 
			    String skuKey=entry.getKey();
				Integer fullQuantity = Integer.parseInt(entry.getValue().split(",")[0]);
				if(fullQuantity.equals(0)){
					continue;
				}
				String sku = skuKey.split(",")[0];
				String country = skuKey.split(",")[1];
				//如果30天销售没有这个    或者库存数为0   或者fba库存/30天销售>3
				if(sale30Map.get(sku)==null||sale30Map.get(sku).equals(0)||fullQuantity/sale30Map.get(sku)>type){
					List<String> skus = Lists.newArrayList();
					if(overMap.get(country)!=null){
						skus= overMap.get(country);
					}
					skus.add(sku);
					overMap.put(country, skus);
				}
			}
			return overMap;
		}
		
		//即将断货
		public Map<String,List<String>> preOutOfStock(Map<String,Integer> sale30Map,Map<String,String> fbaMap){
			Map<String,List<String>> preOutOfMap = Maps.newHashMap();
			//获取积压的sku
			for (Map.Entry<String,String> entry : fbaMap.entrySet()) { 
				String skuKey=entry.getKey();
				String sku = skuKey.split(",")[0];
				String country = skuKey.split(",")[1];
				Integer fullQuantity = Integer.parseInt(entry.getValue().split(",")[0]);
				if(fullQuantity.equals(0)||sale30Map.get(sku)==null||sale30Map.get(sku).equals(0)){
					continue;
				}
				
//				Integer tranQuantity = Integer.parseInt(fbaMap.get(skuKey).split(",")[1]);
				// 或者fba库存/30天日均销<=15 并且在途数量为0
//				if((fullQuantity*30)/sale30Map.get(sku)<=15&&tranQuantity.equals(0)){
				if((fullQuantity*30)/sale30Map.get(sku)<=15){
					List<String> skus = Lists.newArrayList();
					if(preOutOfMap.get(country)!=null){
						skus= preOutOfMap.get(country);
					}
					skus.add(sku);
					preOutOfMap.put(country, skus);
				}
			}
			return preOutOfMap;
		}
		
		

		// UK,FR,DE,IT,ES  45 x 34 x 26 cm   12kg以下  1英寸(in)=2.54厘米(cm)
		// CA,JP 45 x 35 x 20 cm   9kg以下 1pounds=0.4535924kg
		// COM 46 x 36 x 20 cm  9kg以下  
		public void sendPostsExceptionSizeEmail(){
			
			try{
				Map<String,List<AmazonPostsDetail>>  map=amazonPostsDetailService.getExceptionSize();
				if(map!=null&&map.size()>0){
					StringBuffer content= new StringBuffer("Hi,All<br/>   以下产品长宽高重超标(UK,FR,DE,IT,ES:45*34*26 cm,12kg以下;CA,JP:45*35*20 cm,9kg以下;US:45*35*20 cm,9kg以下  ).<br/>");//46 x 36 x 20
					content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
					content.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>国家</th><th>产品</th><th>长(cm)</th><th>宽(cm)</th><th>高(cm)</th><th>重(kg)</th></tr>");
					 for (Map.Entry<String, List<AmazonPostsDetail>>  entry: map.entrySet()) { 
						List<AmazonPostsDetail> list= entry.getValue();
						for (AmazonPostsDetail detail : list) {
							content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		        			content.append("<td>"+("com".equals(detail.getCountry())?"us":detail.getCountry()).toUpperCase()+"</td>");
		        			content.append("<td>"+detail.getProductName()+"</td>");
		        			content.append("<td>"+(detail.getPackageLength()==0?"":detail.getPackageLength())+"</td><td>"+(detail.getPackageWidth()==0?"":detail.getPackageWidth())+"</td>");
		        			content.append("<td>"+(detail.getPackageHeight()==0?"":detail.getPackageHeight())+"</td><td>"+(detail.getPackageWeight()==0?"":detail.getPackageWeight())+"</td>");
		        			content.append("</tr>");
						}
		        	}
					content.append("</table><br/><br/>");
					
					Date date = new Date();
					String  toAddress="amazon-sales@inateck.com,supply-chain@inateck.com,eileen@inateck.com";
					final MailInfo mailInfo = new MailInfo(toAddress,"Oversize Storage"+new SimpleDateFormat("yyyyMMdd").format(date),date);
					mailInfo.setContent(content.toString());
					new Thread(){
						public void run(){
							mailManager.send(mailInfo);
						}
					}.start();
				}
			}catch(Exception e){
				LOGGER.warn("Oversize Storage异常!!",e);
			}
			
			
			try{
				Map<String,Set<String>> ebayMap=ebayOrderService.findExceptionEbayOrder();
				if(ebayMap!=null&&ebayMap.size()>0){
					StringBuffer content= new StringBuffer("Hi,All<br/>   以下ebay订单手动标记成已付款状态,请确认是否发货.<br/>");
					content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
					content.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>国家</th><th>订单号</th></tr>");
					 for (Map.Entry<String,Set<String>>  entry: ebayMap.entrySet()) { 
						 Set<String> sets= entry.getValue();
						 String country=entry.getKey();
						for (String orderId: sets) {
							content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		        			content.append("<td>"+("com".equals(country)?"US":country.toUpperCase())+"</td>");
		        			content.append("<td>"+orderId+"</td>");
		        			content.append("</tr>");
						}
		        	}
					content.append("</table><br/><br/>");
					
					Date date = new Date();
					String  toAddress="isa@inateck.com,eileen@inateck.com";
					final MailInfo mailInfo = new MailInfo(toAddress,"ebay订单异常提醒"+new SimpleDateFormat("yyyyMMdd").format(date),date);
					mailInfo.setContent(content.toString());
					new Thread(){
						public void run(){
							mailManager.send(mailInfo);
						}
					}.start();
				}
			}catch(Exception e){
				LOGGER.warn("ebay order 异常!!",e);
			}
			
			try{
				 Map<String,List<AmazonPostsDetail>> postsMap=amazonPostsDetailService.findExceptionData();
				 Map<String,List<Object[]>>  catalogMap=amazonPostsDetailService.findRankCatalog();
				 Map<String,List<Object[]>>  asinChangeMap=amazonPostsDetailService.findAsinChange();
				 Map<String,List<Object[]>>  priceChanges=amazonPostsDetailService.findPriceChange();
				 Map<String,List<AmazonPostsDetail>> partNumberMap=Maps.newHashMap();
				 try{
					 partNumberMap=amazonPostsDetailService.findWarnPartNumber();
				 }catch(Exception e){
					 LOGGER.warn("partNumberMap!!",e);
				 }
				 Map<String,String>  noCatalogMap=Maps.newHashMap();
				 try{
					 noCatalogMap=amazonPostsDetailService.findNoCatalog();
				 }catch(Exception e){
					 LOGGER.warn("noCatalogMap!!",e);
				 }
				
				 if((postsMap!=null&&postsMap.size()>0)||(catalogMap!=null&&catalogMap.size()>0)||(priceChanges!=null&&priceChanges.size()>0)||(partNumberMap!=null&&partNumberMap.size()>0)||(noCatalogMap!=null&&noCatalogMap.size()>0)){
					 StringBuffer content= new StringBuffer("Hi,All<br/>");
					 if(priceChanges!=null&&priceChanges.size()>0){
						    content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
						    for (Map.Entry<String, List<Object[]>>  entry: priceChanges.entrySet()) { 
							    String country = entry.getKey();
								content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32'><td colspan='4'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"保本价变动</span></td></tr>");
								content.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>产品</th><th>sku</th><th>价格变动幅度</th><th>变动明细</th></tr>");
								List<Object[]> list= entry.getValue();
								for (Object[] obj: list) {
									content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				        			content.append("<td>"+obj[1].toString()+"</td>");
				        			content.append("<td>"+(obj[2]==null?"":obj[2].toString())+"</td>");
				        			int change =  Integer.parseInt(obj[3].toString().replace(".00",""));
				        			
				        			content.append("<td><span style='color:"+(change>0?"green":"red")+"'>"+change+"%</span></td>");
				        			
				        			int p1 = Integer.parseInt(obj[4]==null?"0":obj[4].toString().replace(".00",""));
				        			int p2 = Integer.parseInt(obj[5]==null?"0":obj[5].toString().replace(".00",""));
				        			int p3 = Integer.parseInt(obj[6]==null?"0":obj[6].toString().replace(".00",""));
				        			String remark = "";
				        			if(Math.abs(p1)>0){
				        				remark +=("采购成本调整:<span style='color:'"+(p1>0?"green":"red")+"'>"+p1+"%</span><br/>");
				        			}
				        			if(Math.abs(p2)>0){
				        				remark +=("处理费调整:<span style='color:'"+(p2>0?"green":"red")+"'>"+p2+"%</span><br/>");
				        			}
				        			if(Math.abs(p3)>0){
				        				remark +=("佣金调整:<span style='color:'"+(p3>0?"green":"red")+"'>"+p3+"%</span>");
				        			}
				        			content.append("<td>"+remark+"</td>");
				        			content.append("</tr>");
								}
				        	}
							content.append("</table><br/><br/>");
					 }
					 if(postsMap!=null&&postsMap.size()>0){
						    //46 x 36 x 20
							content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
							for (Map.Entry<String, List<AmazonPostsDetail>> entry: postsMap.entrySet()) { 
							    String country = entry.getKey();
								content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32'><td colspan='3'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"帖子</span></td></tr>");
								content.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>产品</th><th>SKU</th><th>类型</th></tr>");
								List<AmazonPostsDetail> list= entry.getValue();
								for (AmazonPostsDetail detail : list) {
									content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
									String suffix=country;
		        					if("jp,uk".contains(country)){
		        						suffix="co."+country;
		        					}else if("mx".equals(country)){
		        						suffix="com."+country;
		        					}
		        					String countryStr=suffix+"/dp/"+detail.getAsin();
				        			content.append("<td><a href='http://www.amazon."+countryStr+"' target='_blank'>"+detail.getProductName().substring(detail.getProductName().indexOf(" ")+1)+"</a></td>");
				        			content.append("<td>"+detail.getSku()+"</td>");
				        			content.append("<td>"+("1".equals(detail.getTitle())?"关键字缺失":("2".equals(detail.getTitle())?"描述缺失":"卖点缺失"))+"</td>");
				        			content.append("</tr>");
								}
				        	}
							content.append("</table><br/><br/>");
					 }
					 if(catalogMap!=null&&catalogMap.size()>0){
						    content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
						    for (Map.Entry<String, List<Object[]>> entry : catalogMap.entrySet()) {  
							    String country=entry.getKey();
								content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32'><td colspan='4'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"目录变化</span></td></tr>");
								content.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>产品</th><th>今日目录</th><th>昨日目录</th><th>昨日目录路径</th></tr>");
								List<Object[]> list=entry.getValue();
								for (Object[] obj: list) {
									content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");

				        			content.append("<td>"+obj[1].toString()+"</td>");
				        			content.append("<td>"+(obj[2]==null?"":obj[2].toString())+"</td>");
				        			content.append("<td>"+(obj[5]==null?"":obj[5].toString())+"</td>");
				        			content.append("<td>"+(obj[6]==null?"":obj[6].toString().replaceAll(",", "<br/>"))+"</td>");
				        			content.append("</tr>");
								}
				        	}
							content.append("</table><br/><br/>");
					 }

					 //asin变化
					 if(asinChangeMap!=null&&asinChangeMap.size()>0){
						    //增加微信通知
						    StringBuffer buffer = new StringBuffer("Hi All:以下是产品Asin变化明细,请知悉");
						    content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
						    for (Map.Entry<String,List<Object[]>> entry : asinChangeMap.entrySet()) {  
							    String country=entry.getKey();
								String countryStr = ("com".equals(country)?"us":country).toUpperCase();
								buffer.append("\n\n国家：" + countryStr);
								content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32'><td colspan='3'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"Asin变化</span></td></tr>");
								content.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>产品</th><th>今日Asin</th><th>昨日Asin</th></tr>");
								List<Object[]> list=entry.getValue();
								for (Object[] obj: list) {
									content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				        			content.append("<td style='color:red' >"+obj[1].toString()+"</td>");
				        			content.append("<td>"+(obj[3]==null?"":obj[3].toString())+"</td>");
				        			content.append("<td>"+(obj[2]==null?"":obj[2].toString())+"</td>");
				        			content.append("</tr>");
				        			buffer.append("\n产品：" + obj[1].toString());
				        			buffer.append("\n今日Asin：" + obj[3].toString());
				        			buffer.append("\n昨日Asin：" + obj[2].toString());
								}
				        	}
							content.append("</table><br/><br/>");
							//微信通知销售部&系统开发部
							WeixinSendMsgUtil.sendTextMsg(WeixinSendMsgUtil.getToken(), null, "3|7", null, ParamesAPI.appId, buffer.toString(), "0");
					 }
					 
					 if(noCatalogMap!=null&&noCatalogMap.size()>0){
						    content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
						    content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32'><td colspan='2'><span style='font-weight: bold;font-size:25px'>产品无排名目录</span></td></tr>");
						    content.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>国家</th><th>产品</th></tr>");
						    for (Map.Entry<String,String> entry : noCatalogMap.entrySet()) { 
						            String country=entry.getKey();
									content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
									content.append("<td>"+country+"</td>");
				        			content.append("<td>"+entry.getValue()+"</td>");
				        			content.append("</tr>");
				        	}
							content.append("</table><br/><br/>");
					 }
					 if(partNumberMap!=null&&partNumberMap.size()>0){
						    //46 x 36 x 20
							content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
							for (Map.Entry<String,List<AmazonPostsDetail>> entry : partNumberMap.entrySet()) { 
							    String country=entry.getKey();
								content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32'><td colspan='4'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"不规范PartNumber</span></td></tr>");
								content.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>产品</th><th>SKU</th><th>PartNumber</th><th>在售</th></tr>");
								List<AmazonPostsDetail> list=entry.getValue();
								for (AmazonPostsDetail detail : list) {
									content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				        			content.append("<td>"+detail.getProductName()+"</td>");
				        			content.append("<td>"+detail.getSku()+"</td>");
				        			content.append("<td>"+detail.getPartNumber()+"</td>");
				        			content.append("<td>"+("1".equals(detail.getTitle())?"在售":"淘汰")+"</td>");
				        			content.append("</tr>");
								}
				        	}
							content.append("</table><br/><br/>");
					 }
					 
					    Date date = new Date();
						String  toAddress="amazon-sales@inateck.com,eileen@inateck.com,tim@inateck.com";
						final MailInfo mailInfo = new MailInfo(toAddress,"线上运营部：保本价变动&帖子缺失异常&目录变更提醒&不规范PartNumber"+new SimpleDateFormat("yyyyMMdd").format(date),date);
						mailInfo.setContent(content.toString());
						new Thread(){
							public void run(){
								mailManager.send(mailInfo);
							}
						}.start();
				 }
			}catch(Exception e){
				LOGGER.warn("帖子关键字异常!!",e);
			}
			
			
			
		}

	//每天监控FBA到货异常提醒(到货5天收货未达到80%)
	public void fbaReceiveWarning() {
		List<FbaInbound> rs = fbaInboundService.getFbaReceiveWarning();
		if (rs != null && rs.size() > 0) {
			Map<String, String> productNameMap = amazonProduct2Service.findAllProductNamesWithSku();
			String address = "amazon-sales@inateck.com";
			StringBuffer contents = new StringBuffer("");
			contents.append("<p><span style='font-size:20px'>Hi All:<br/>&nbsp;&nbsp;&nbsp;&nbsp;FBA到货异常明细如下,请知悉。</span></p>");
			contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			
			contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe;'>" +
					"<td colspan='6'><span style='font-weight: bold;font-size:25px'>FBA到货异常清单(到货5天收货未达到80%)</span></td></tr>");
			
			contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
			contents.append("<td>ShipmentID</td>");
			contents.append("<td>产品名</td>");
			contents.append("<td>SKU</td>");
			contents.append("<td>发货数</td>");
			contents.append("<td>收货数</td>");
			contents.append("<td>状态</td>");
			contents.append("</tr>");
			for (FbaInbound fbaInbound : rs) {
				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe;'>" +
						"<td colspan='6'><span style='font-weight: bold;font-size:25px'>"+fbaInbound.getShipmentName()+"</span></td></tr>");
				int totalShipped = 0;
				int totalReceived = 0;
				for (FbaInboundItem item : fbaInbound.getItems()) {
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
					contents.append("<td>"+fbaInbound.getShipmentId()+"</td>");
					String name = productNameMap.get(item.getSku());
					name = name == null ? "" : name;
					contents.append("<td>"+name+"</td>");
					contents.append("<td>"+item.getSku()+"</td>");
					totalShipped += item.getQuantityShipped();
					totalReceived += item.getQuantityReceived();
					contents.append("<td>"+item.getQuantityShipped()+"</td>");
					contents.append("<td>"+item.getQuantityReceived()+"</td>");
					contents.append("<td>"+fbaInbound.getShipmentStatus()+"</td>");
					contents.append("</tr>");
				}
				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				contents.append("<td colspan='3'>总计</td>");
				contents.append("<td>"+totalShipped+"</td>");
				contents.append("<td>"+totalReceived+"</td>");
				contents.append("<td></td>");
				contents.append("</tr>");
			}
				
			contents.append("</table><br/>");
			final MailInfo mailInfo = new MailInfo(address, "fba到货异常邮件提醒"+ DateUtils.getDate("-yyyy/M/dd"), new Date());
			mailInfo.setCcToAddress("leehong@inateck.com");
			mailInfo.setContent(contents.toString());
			new Thread() {
				public void run() {
					mailManager.send(mailInfo);
				}
			}.start();
		}
	}
}
