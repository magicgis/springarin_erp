package com.springrain.erp.modules.amazoninfo.scheduler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonFollowSellerService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.service.DictService;

@Component
public class FollowSellerMonitor {
	@Autowired
	private PsiProductService 		productService;
	@Autowired
	private PsiInventoryFbaService  fbaService;
	@Autowired
	private MailManager 			mailManager;
	@Autowired
	private DictService             dictService;
	@Autowired
	private AmazonProduct2Service   amazonProduct2Service;
	@Autowired
	private AmazonFollowSellerService   followSellerService;

	private  final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private static Map<String, List<String>> sellerMap = Maps.newHashMap();
	static{
		sellerMap.put("com", Lists.newArrayList("A2QGX098CVHYJ7","A3C6FYH2UWE3LL","A3GKWI06GSC9E3"));
		sellerMap.put("ca",  Lists.newArrayList("A2AB7Q6LDAB3U0"));
		sellerMap.put("jp",  Lists.newArrayList("A2744L3VQVPUXF"));
		sellerMap.put("mx",  Lists.newArrayList("A2ANQDE1QGDL8X"));
		sellerMap.put("de",  Lists.newArrayList("A5JH7MGCI556L"));
		sellerMap.put("fr",  Lists.newArrayList("A5JH7MGCI556L"));
		sellerMap.put("it",  Lists.newArrayList("A5JH7MGCI556L"));
		sellerMap.put("es",  Lists.newArrayList("A5JH7MGCI556L"));
		sellerMap.put("uk",  Lists.newArrayList("A5JH7MGCI556L"));
	}
	public void followSellers() throws ParseException, IOException{
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		LOGGER.info("跟卖扫描开始时间："+sdf1.format(new Date()));
		SimpleDateFormat sdf = new SimpleDateFormat();
		Date date = sdf.parse(sdf.format(new Date()));
		Map<String,Set<String>> countryMap =amazonProduct2Service.getFollowSellerAsins();
		WebClient webClient = new WebClient();
		WebClientOptions options = webClient.getOptions();
		options.setTimeout(30000);
		options.setJavaScriptEnabled(false);
		options.setActiveXNative(false);
		options.setCssEnabled(false);
		options.setPopupBlockerEnabled(false);
		options.setThrowExceptionOnFailingStatusCode(false);
		options.setThrowExceptionOnScriptError(false);
		options.setPrintContentOnFailingStatusCode(false);
//		webClient.waitForBackgroundJavaScript(3000);
		
		
		for(Map.Entry<String,Set<String>> entry :countryMap.entrySet()){
			try{
				Set<String> asinInfos = entry.getValue();
				String country = entry.getKey();
//				if(!"de".equals(country)){
//					continue;
//				}
				String suffix =country;
				if("jp".equals(country)){
					suffix="co.jp";
				}else if("uk".equals(country)){
					suffix="co.uk";
				}else if("mx".equals(country)){
					suffix="com.mx";
				}
				List<String> sellersId =sellerMap.get(country);
				for(String asinInfo:asinInfos){
					try{
						String arr[]=asinInfo.split(",");
						String asin = arr[0];
//						if(!"B00PC3YBXM".equals(asin)){
//							continue;
//						}
						String productName = "";
						if(arr.length>1){   
							productName= arr[1];
						}
						
						String url = "https://www.amazon."+suffix+"/gp/offer-listing/"+asin+"?condition=new";
						HtmlPage page =webClient.getPage(url);
						Document doc =Jsoup.parse(page.asXml());
//						Document doc = HttpRequest.reqUrl(url, null, true);
					    if (doc == null) {
					      continue;
					    }
					    String title = doc.title();
					    if((title != null) && (title.contains("404"))) {
					    	continue;
					    }
					    String   productTitle = doc.select("#olpProductDetails").select("h1").text();
						if(StringUtils.isEmpty(productTitle)){
							continue;
						}
						
				        List<String> list = catchData(doc,sellersId,"https://www.amazon."+suffix);
				        if(list!=null&&list.size()>0){
				        	for(String a : list){
				        		//如果存在+1，如果不存在更新
				        		Document aDoc= Jsoup.parse(a);
				        		String sellerName=aDoc.text();
				        		this.followSellerService.updateSeller(date, sellerName, country, asin, a, productTitle, productName);
				        	}
				        }
					}catch(Exception ex){
						LOGGER.error("跟卖扫描异常1："+ex.getMessage(),ex);
						ex.printStackTrace();
					}
				}
			}catch(Exception ex){
				LOGGER.error("跟卖扫描异常2："+ex.getMessage(),ex);
				ex.printStackTrace();
			}
		}
		
		webClient.closeAllWindows();
		LOGGER.info("跟卖扫描结束时间："+sdf1.format(new Date()));
	}
	
	
	
	 public List<String> catchData(Document doc,List<String> sellersId,String url){
	    List<String> listLink = Lists.newArrayList();
	    Elements selectables = doc.getElementsByClass("olpOffer");
	    if(selectables != null&&selectables.size()>0) {
	         for (Element divSeller : selectables) {
		          if (divSeller == null) {
		            continue;
		          }
		          Elements a = divSeller.select("h3.olpSellerName a");
		          if(a==null||a.size()==0){
		        	  continue;
		          }
		          Element aDoc = a.get(0);
		          String href =url+aDoc.attr("href");
	        	  aDoc.attr("href", href);
		          String link = a.toString();
		          //排除亚马逊自营
		          boolean flag = false;
		          for (String seller : sellersId) {
					flag = link.contains(seller);
		        	if(flag){
		        		break;
		        	}  
				  }
		          if(!flag){
		        	  listLink.add(link);
		          }
		      }
	      }
	    return listLink;
	  }
	 
	public static void main(String [] arr) throws ParseException, IOException{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring-context.xml");
		FollowSellerMonitor seller = context.getBean(FollowSellerMonitor.class);
		seller.followSellers();
	}
	
}
