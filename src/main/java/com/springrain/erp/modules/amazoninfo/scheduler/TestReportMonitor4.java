package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
public class TestReportMonitor4 {
	
	@Autowired
	private SendCustomEmail1Manager sendCustomEmail1Manager;
	@Autowired
	private MailManager mailManager;
	
	private static final Logger logger = LoggerFactory.getLogger(TestReportMonitor4.class);
	
	public static void main(String[] args) {
		final MailInfo mailInfo = new MailInfo("eileen@inateck.com","test("+DateUtils.getDate("yyyy/MM/dd")+")",new Date());
		mailInfo.setContent("test");
		new Thread(){
		    public void run(){
		    	new SendCustomEmail1Manager().send(mailInfo);
		    	//new MailManager().send(mailInfo);
			}
		}.start();
		//new SendCustomEmail1Manager().send(mailInfo);
		//new MailManager().send(mailInfo);
	}

	public SendCustomEmail1Manager getSendCustomEmail1Manager() {
		return sendCustomEmail1Manager;
	}

	public void setSendCustomEmail1Manager(
			SendCustomEmail1Manager sendCustomEmail1Manager) {
		this.sendCustomEmail1Manager = sendCustomEmail1Manager;
	}
	
	
	/*@Autowired
	private AdvertisingService advertisingService;
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AdvertisingService advertisingService = applicationContext.getBean(AdvertisingService.class);
		TestReportMonitor4 monitor=new TestReportMonitor4();
		monitor.setAdvertisingService(advertisingService);
		for (CountryType config : CountryType.values()) {
			String key = config.getName();
			if("com.inateck".equals(key)){
				key = "com";
			}
			WebClient client = LoginUtil.register(config.getName(), false);
			HtmlPage page;
			try {
				page = client.getPage("https://sellercentral.amazon."+config.getSuffix()+"/gp/advertiser/reports/auto-target.html");
				DomNodeList<HtmlElement> trs = page.getElementById("reloadComponent1").getElementsByTagName("tr");
				String id = "";
				if(trs.size()>2){
					id = trs.get(1).getElementsByTagName("td").get(0).getTextContent();
				}
				HtmlPage page1 = client.getPage("https://sellercentral.amazon."+config.getSuffix()+"/gp/advertiser/reports/actions/generateReport.html?type=auto_target");
				if(page1!=null && page1.asText().contains("SUCCESS")){
					Thread.sleep(60000);
					page=(HtmlPage) page.refresh();
					trs = page.getElementById("reloadComponent1").getElementsByTagName("tr");
					String id2 = "";
					if(trs.size()>2){
						id2 = trs.get(1).getElementsByTagName("td").get(0).getTextContent();
					}
					if(StringUtils.isNotEmpty(id)){
						while(id.equals(id2)){
							Thread.sleep(60000);
							page=(HtmlPage) page.refresh();
							trs = page.getElementById("reloadComponent1").getElementsByTagName("tr");
							if(trs.size()>2){
								id2 = trs.get(1).getElementsByTagName("td").get(0).getTextContent();
							}
						}
					}else{
						while(StringUtils.isEmpty(id2)){
							Thread.sleep(60000);
							page=(HtmlPage) page.refresh();
							trs = page.getElementById("reloadComponent1").getElementsByTagName("tr");
							if(trs.size()>2){
								id2 = trs.get(1).getElementsByTagName("td").get(0).getTextContent();
							}
						}
					}
					int index  = page.getElementById("reloadComponent1").getElementsByTagName("tr").get(1).getElementsByTagName("button").size();
					while(index==0){
						Thread.sleep(5000);
						page=(HtmlPage) page.refresh();
						Thread.sleep(1000);
						try {
							index = page.getElementById("reloadComponent1").getElementsByTagName("tr").get(1).getElementsByTagName("button").size();
						} catch (Exception e) {
							logger.warn("获取广告页面异常:"+page.asText(),e);
						}
					}
					if("de,fr,it,es,uk".contains(config.getName())){
						List<?> list =  page.getByXPath("//select[@id='sc-mkt-switcher-select']/option[@selected='selected']/text()");
						if(list.size()==1){
							String temp = list.get(0).toString();
							if(!temp.contains(config.getName())){
								logger.error(key+"改变国家错误!!跳过");
								continue;
							}
						}else{
							logger.error(key+"登陆错误!!跳过");
							continue;
						}
					}
					String href = page.getElementById("reloadComponent1").getElementsByTagName("tr").get(1).getElementsByTagName("button").get(0).getAttribute("onclick");
					href = href.replace("location.href='","").replace("'","");
					href = "https://sellercentral.amazon."+config.getSuffix()+href;
					String itemData = getDownloadData(client,href);
					List<AmazonSearchTermReport> report=saveData(itemData,key);
					if(report!=null&&report.size()>0){
						advertisingService.saveSearchTermList(report);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		applicationContext.close();
	}
	
	public AdvertisingService getAdvertisingService() {
		return advertisingService;
	}

	public void setAdvertisingService(AdvertisingService advertisingService) {
		this.advertisingService = advertisingService;
	}
	
   private  static Map<String, DateFormat> formats;
	
	static{
		formats = Maps.newHashMap();
		formats.put("de",new SimpleDateFormat("dd/MM/yyyy"));
		formats.put("com",new SimpleDateFormat("MM/dd/yyyy"));
		formats.put("jp",new SimpleDateFormat("yyyy/MM/dd"));
		formats.put("ca",new SimpleDateFormat("MM/dd/yyyy"));
	}
	

	private static List<AmazonSearchTermReport> saveData(String data,String country){
		String[]rows = data.split("\r\n");
		Date updateTime=new Date();

		List<AmazonSearchTermReport> report=Lists.newArrayList();
		for (int i = rows.length-1; i >0 ; i--) {
			 String[] rowData = rows[i].split("\t");
			 String campaignName=rowData[0];
			 String adGroupName=rowData[1];
			 String customerSearchTerm=rowData[2];
			 String keyword=rowData[3];
			 String matchType=rowData[4];
			 Date startDate=null;
			 Date endDate=null;
			 try {
				String key=country;
                if("fr,uk,it,es".contains(country)){
						key = "de";
			    }
				startDate= formats.get(key).parse(rowData[5]);
				endDate= formats.get(key).parse(rowData[6]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			 Integer impressions=Integer.parseInt(rowData[7]);
			 Integer clicks=Integer.parseInt(rowData[8]);
			 Float ctr=Float.parseFloat(rowData[9].replace(",", ".").replace("%", ""));//
			 Float totalSpend=Float.parseFloat(rowData[10].replace(",", "."));
			 Float averageCpc=Float.parseFloat(rowData[11].replace(",", "."));
			 Float acos=Float.parseFloat(rowData[12].replace(",", ".").replace("%", ""));//
			 Integer ordersPlaced=Integer.parseInt(rowData[14]);
			 Float productSales=Float.parseFloat(rowData[15].replace(",", "."));
			 Float conversionRate=Float.parseFloat(rowData[16].replace(",", ".").replace("%", ""));//
			 Integer sameSku=Integer.parseInt(rowData[17]);
			 Integer otherSku=Integer.parseInt(rowData[18]);
			 Float sameSkuSale=Float.parseFloat(rowData[19].replace(",", "."));
			 Float otherSkuSale=Float.parseFloat(rowData[20].replace(",", "."));
			 report.add(new AmazonSearchTermReport(country,campaignName,adGroupName,customerSearchTerm,keyword,matchType,startDate,endDate,impressions,clicks,ctr,totalSpend,
			            averageCpc,acos,ordersPlaced,productSales,conversionRate,sameSku,otherSku,sameSkuSale,otherSkuSale, updateTime));
		}
		return report;
	}
	
	
	public static String getDownloadData(WebClient webClient,String url){
		WebRequest request = null;
		try {
			request = new WebRequest(new URL(url),HttpMethod.POST);
			request.setAdditionalHeader("Content-Type",
				"application/x-www-form-urlencoded");
			Page page = webClient.getPage(request);
			if (page != null) {
				String data = "";
				if(url.contains("co.jp")){
					data = page.getWebResponse().getContentAsString("Shift_JIS");
				}else{
					data = page.getWebResponse().getContentAsString();
				}
				return data;
			} 
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return null;
	}*/
}
