package com.springrain.erp.modules.amazoninfo.scheduler;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.entity.OpponentStock;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.OpponentAsinService;
import com.springrain.erp.modules.amazoninfo.service.OpponentStockService;
public class OpponentStockOptMonitor {
	private final static Logger logger = LoggerFactory.getLogger(OpponentStockOptMonitor.class);
	private static Pattern pattern = Pattern.compile("\\{\"cartQty\"([^}]*)}");
	@Autowired
	private OpponentAsinService    asinService;
	@Autowired
	private OpponentStockService   opponentService;
	@Autowired
	private AmazonProduct2Service  product2Service;

	public void exeMonitorStock() throws IOException, ParseException{
		logger.info("对手库存扫描(手动录入asin)start");
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date today = sdf.parse(sdf.format(new Date()));
			final Map<String,Set<String>> asinMap=asinService.getAsinAndCountry(product2Service.getAllAsin());
			//排除今天top100已经扫描的asin
			final Map<String,Set<String>> asinTodayMap=asinService.getAsinAndCountryToday(today);
			Date yestarday =DateUtils.addDays(today,-1);
			final Map<String,Integer> yesterdayMap = opponentService.getYesterdayStock(yestarday);
			//开启多线程
			 ExecutorService pool=Executors.newCachedThreadPool();  
		        for(final String country:asinMap.keySet()){
		        	Thread tempThread = new Thread(){
						public void run() {
							exeSingleThread(country, asinMap, yesterdayMap,asinTodayMap);
						}
					};
		            pool.submit(tempThread);  
		        }  
		        pool.shutdown(); 
		}catch(Exception ex){
			logger.error("对手库存扫描(手动录入asin)异常："+ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	/**
	 *获取购物车页面
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public HtmlPage getCartPage(WebClient webClient,String suffix,String asin,Integer num) throws IOException, InterruptedException{
		//查看购物车
		String lookCartUrl="http://www.amazon."+suffix+"/gp/cart/view.html";
		HtmlPage cartPage= getPage(webClient,lookCartUrl,0);
		List<HtmlDivision> divs = (List<HtmlDivision>) cartPage.getByXPath("//div[@data-asin='"+asin+"']");
		if(divs==null||divs.size()==0){ 
			//购物车有可能多次添加不进
			num++;
			if(num>10){
				//如果连续抓10次还是为空，跳出
				return cartPage;
			}else{
				return this.getCartPage(webClient, suffix, asin, num);
			}
		}else{
			return cartPage;
		}
	}
	
	
	/**
	 *分国家处理 
	 * 
	 */
	public void exeSingleThread(String country,Map<String,Set<String>> asinMap,Map<String,Integer> yesterdayMap,Map<String,Set<String>> asinTodayMap){
		try{
			Set<String> asins = Sets.newHashSet();
			if(asinMap!=null){
				asins = asinMap.get(country);
			}
			Set<String> asinTodays = asinTodayMap.get(country);
			String suffix = country;
			if("jp,uk,".contains(country+",")){
				suffix="co."+country;
			}else if("mx".equals(country)){
				suffix="com."+country;
			}
			for(String asin:asins){
				//如果这个asin已经在top100里扫描了，就不用扫了
				if(asinTodays!=null&&asinTodays.size()>0&&asinTodays.contains(asin)){
					continue;
				}
				Integer stockQuantity=getInventoryByAsin(suffix, asin);
				if(stockQuantity>0&&!stockQuantity.equals(999)){
					//找出差值，差值为<=0,差值为空
					String asinCountry = asin+","+country;
					Integer yesterDayQuantity=yesterdayMap.get(asinCountry);
					if(asinMap!=null&&yesterDayQuantity!=null&&yesterDayQuantity>0&&!yesterDayQuantity.equals(999)&&(yesterDayQuantity-stockQuantity)>0){
						opponentService.save(new OpponentStock(asin, country, stockQuantity, new Date(),(yesterDayQuantity-stockQuantity)));
					}else{
						opponentService.save(new OpponentStock(asin, country, stockQuantity, new Date(),null));
					}
				}else{
					opponentService.save(new OpponentStock(asin, country, stockQuantity, new Date(),null));
				}
			}
			logger.info(country+"对手库存扫描(手动录入asin)end");
		}catch(Exception ex){
			logger.error("对手库存扫描(手动录入asin)异常1："+ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	public Integer getInventoryByAsin(String suffix,String asin){
		WebClient webClient =getWebClient(0);
		try{
			//进入产品页面
			String productUrl ="http://www.amazon."+suffix+"/gp/product/"+asin;
			HtmlPage masterPage  = getPage(webClient,productUrl,0);
			//加入购物车
			Integer  putCartTimes=0;
			String addToCart="add-to-cart-button";
			HtmlSubmitInput addToCartButton= (HtmlSubmitInput) masterPage.getElementById(addToCart);
			if(addToCartButton==null){
				while(putCartTimes<10&&addToCartButton==null){
					//如果10次添加购物车失败  就放弃
					Thread.sleep(500);//休息半秒钟
					putCartTimes++;
					addToCartButton= (HtmlSubmitInput) masterPage.getElementById(addToCart);
				}
				if(addToCartButton==null){
					return -1;
				}
			}
			addToCartButton.click();
			HtmlPage cartPage= this.getCartPage(webClient, suffix, asin, 0);
			String requestId= "";
			String token = "";
			String timeStamp ="";
			try{
				if(cartPage.getElementByName("requestID")!=null){
					requestId=cartPage.getElementByName("requestID").getAttribute("value");
				}
				if(cartPage.getElementByName("token")!=null){
					token = cartPage.getElementByName("token").getAttribute("value");
				}
				if(cartPage.getElementByName("timeStamp")!=null){
					timeStamp =cartPage.getElementByName("timeStamp").getAttribute("value");
				}
			}catch(ElementNotFoundException ex){}
			
			List<HtmlDivision> divs = (List<HtmlDivision>) cartPage.getByXPath("//div[@data-asin='"+asin+"']");
			if(divs==null||divs.size()==0){  
				return -2;
			}
			HtmlDivision div= divs.get(0);
			//模拟请求获得库存
			String changeQuantityUrl ="https://www.amazon."+suffix+"/gp/cart/ajax-update.html";
			WebRequest req = new WebRequest(new URL(changeQuantityUrl),HttpMethod.POST);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			String actionItemID=div.getAttribute("data-itemid");
			params.add(new NameValuePair("asin", asin));
			params.add(new NameValuePair("actionItemID", actionItemID));
			params.add(new NameValuePair("submit.update-quantity."+actionItemID, "1"));//可以不填写
			params.add(new NameValuePair("quantity."+actionItemID, "999"));
			//2016-01-27 德国需要添加如下参数
			params.add(new NameValuePair("requestID", requestId));
			params.add(new NameValuePair("token", token));
			params.add(new NameValuePair("timeStamp", timeStamp));
			req.setRequestParameters(params);
			//解析结果
			JavaScriptPage scriptPage = this.javaScriptPage(webClient, req, 18);
			if(scriptPage==null||scriptPage.getContent()==null){    
				return -3;
			}
			String scriptStr =scriptPage.getContent();
			Matcher matcher =pattern.matcher(scriptStr);
			//{"cartQty":"523"}}
			if(matcher.find()){
				Pattern p=Pattern.compile("(\\d+)");   
				Matcher m=p.matcher(matcher.group());       
				if(m.find()){
					return Integer.parseInt(m.group()); 
				}  
			}
		}catch(Exception ex){
			logger.error("扫描竞争asin对手库存异常,asin:"+asin+",suffix:"+suffix);
			logger.error("扫描竞争asin对手库存异常:"+ex.getMessage(),ex);
			ex.printStackTrace();
		}finally{
			webClient.closeAllWindows();
		}
		return -8;
	}
	
	
	public static void main(String [] args) throws IOException{
		String asin ="B00VPTKQ7M";
		String country="com";
		new OpponentStockOptMonitor().getInventoryByAsin(country,asin);
	}
	
	public WebClient getWebClient(int num){
		WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_9);
		try{
			WebClientOptions options = webClient.getOptions();
			options.setTimeout(30000);
			options.setActiveXNative(false);
			options.setCssEnabled(false);
			options.setPopupBlockerEnabled(false);
			options.setThrowExceptionOnFailingStatusCode(false);
			options.setThrowExceptionOnScriptError(false);
			options.setPrintContentOnFailingStatusCode(false);
			webClient.waitForBackgroundJavaScript(30000);
			options.setJavaScriptEnabled(false);
			return webClient;
		} catch (Exception e) {
			webClient.closeAllWindows();
			e.printStackTrace();
			num = num +1;
			return getWebClient(num);
		}
	}
	
	private HtmlPage getPage(WebClient client,String url,int num){
		if(num>10){
			return null;
		}
		try {
			HtmlPage page =  client.getPage(url);
			return page;
		} catch (Exception e) {
			e.printStackTrace();
			num = num +1;
			return getPage(client,url,num);
		}
	}
	
	public JavaScriptPage javaScriptPage(WebClient client,WebRequest req,int num){
		if(num>20){
			return null;
		}
		try {
			JavaScriptPage page =  client.getPage(req);
			return page;
		} catch (Exception e) {
			num = num +1;
			client.closeAllWindows();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {}
			return javaScriptPage(client,req,num);
		}
	}
	
}
