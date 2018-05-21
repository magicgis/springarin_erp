package com.springrain.erp.modules.amazoninfo.scheduler;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsDetail;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct2;
import com.springrain.erp.modules.amazoninfo.entity.NewsSubscribe;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.NewsSubscribeService;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;


/**消息订阅
 * 价格变动实现方案，1小时监控一次价格修改记录，每天补发一封昨日变动防止后台修改等不能实时监控的情况
 * 5日库存预警	每天定时统计一次
 */
public class NewsSubscribeMonitor {

	private final static Logger logger = LoggerFactory.getLogger(NewsSubscribeMonitor.class);

	@Autowired
	private PsiInventoryFbaService fbaService;

	@Autowired
	private PsiInventoryService inventoryService;

	@Autowired
	private PsiProductService productService;
	
	@Autowired
	private MailManager mailManager;

	@Autowired
	private NewsSubscribeService newsSubscribeService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private PsiProductService psiProductService;

	private static String priceChangeNum = "10";	//价格变动编号
	private static String inventoryWarnNum = "11";	//5日FBA预警编号
	private static String hisPriceChangeNum = "12";	//昨日价格变动
	
	private static String subject = "ERP订阅邮件-";
	private final static StringBuffer contentTips = new StringBuffer();
	static{
		contentTips.append("<span style='color:red'><br />以上是你在ERP里订阅的内容，如需订阅更多内容，可以在ERP修改订阅条件");
		contentTips.append("<br />订阅地址：<a target='_blank' href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/newsSubscribe'>"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/newsSubscribe</a><span>");
	}
	
	//每天监控一次
	public void monitorOnce() {
		try {
			List<PsiProduct> productList = psiProductService.findAll();
			Map<String, String> fanouMap = Maps.newHashMap();
			for (PsiProduct psiProduct : productList) {
				for (String colorName : psiProduct.getProductNameWithColor()) {
					fanouMap.put(colorName, psiProduct.getHasPower());
				}
			}
			// 查询订阅了预警邮件的信息编号11
			Map<String, Set<String>> rs = newsSubscribeService.findByNum(inventoryWarnNum);
			if (rs.size() > 0) { // 有人订阅,则继续
				Set<String> countrySet = rs.get("1");
				//查询需要监控的sku集合
				Map<String, Map<String, String>> skuMap = productService.getSkusByCountry(countrySet);
				if (skuMap.size() > 0) {
					// sku:31天销
					Map<String, Integer> sku30Days = fbaService.get31SalesQuantity(skuMap.keySet());
					// 库存
					//Map<String, Integer> fbaMap = fbaService.getFbaInventroy(skuMap.keySet());
					Map<String,PsiInventoryFba> fbaMap = inventoryService.getAllProductFbaInfo();
					//所有预警的sku
					List<String> warnSkus = Lists.newArrayList();
					//记录FBA可销天
					Map<String, Integer> saleDays = Maps.newHashMap();
					 for (Map.Entry<String,Map<String,String>> entry : skuMap.entrySet()) { 
					   String sku=entry.getKey();	//计算库存是否不足5天
						try {
							String country = entry.getValue().get("country");
							String productName = entry.getValue().get("productName");
							if ("fr,uk,it,es".contains(country) && "0".equals(fanouMap.get(productName))) {
								continue;	//泛欧产品欧洲只算德国
							}
							String key = productName + "_" + country;
							Integer quantity = fbaMap.get(key).getFulfillableQuantity();
							if (quantity != null && sku30Days.get(sku) != null
									&& (sku30Days.get(sku).intValue() != 0)
									&& (quantity * 31 / sku30Days.get(sku)) <= 5) {
								warnSkus.add(sku);
								saleDays.put(key, quantity * 31 / sku30Days.get(sku));
							}
						} catch (Exception e) {
							//产品信息为空直接跳过
						}
					}
					List<NewsSubscribe> list = newsSubscribeService.findByEmailType(inventoryWarnNum);
					for (NewsSubscribe newsSubscribe : list) {	//每条订阅记录发一封邮件
						//国家和产品对应的发邮件地址
						Map<String, Map<String, String>> relationsMap = newsSubscribeService.findRelationsBySubscribe(newsSubscribe);
						//需要发邮件的产品信息
						List<Map<String, String>> contentList = Lists.newArrayList();
						List<String> nameList = Lists.newArrayList();
						for (String sku : warnSkus) {
							//sku对应的国家和产品
							String country = skuMap.get(sku).get("country");
							String productName = skuMap.get(sku).get("productName");
							if (nameList.contains(country+productName)) {
								continue;	//排重
							}
							String email = null;
							try {//获取country和productName需要发送的邮箱集合
								email = relationsMap.get(country).get(productName);
							} catch (NullPointerException e) {}
							if (StringUtils.isNotEmpty(email)) {
								Map<String, String> map = Maps.newHashMap();
								map.put("sku", sku);
								map.put("country", country);
								map.put("productName", productName);
								contentList.add(map);
								nameList.add(country+productName);
							}
						}
						if (contentList.size() > 0) {
							StringBuffer contents = new StringBuffer("");
							contents.append("&nbsp;&nbsp;&nbsp;&nbsp;FBA可售天不足5天的产品见下表:");
							contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
							contents.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1;'>");
							contents.append("<th>序号</th><th>国家</th><th>产品</th><th>FBA可销天</th>");
							contents.append("</tr>");
							int index = 1;
							for (Map<String, String> map : contentList) {
								//String sku = map.get("sku");
								String country = map.get("country");
								String productName = map.get("productName");
								int days = saleDays.get(productName + "_" + country);
								contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe;'>");
								contents.append("<td>" + index++ + "</td><td>" + ("com".equals(country.toLowerCase())?"US":country.toUpperCase()) + "</td><td>" + productName + "</td><td>" + days + "</td></tr>");
									
							}
							contents.append("</table><br/>");
							final MailInfo mailInfo = new MailInfo(newsSubscribe.getEmail(), subject + "FBA库存不足5日预警(订阅编号："+newsSubscribe.getId()+")" + DateUtils.getDate("-yyyy/M/dd"), new Date());
							mailInfo.setContent("Hi,All：<br/>" + contents.toString() + contentTips.toString());
							new Thread() {
								public void run() {
									mailManager.send(mailInfo);
								}
							}.start();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("FBA库存不足5日预警异常", e);
			WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "FBA库存不足5日预警异常");
		}
		
		// 查询订阅了昨日价格变动邮件的信息编号12
		try {
			Map<String, Set<String>> rs = newsSubscribeService.findByNum(hisPriceChangeNum);
			if (rs.size() > 0) { // 有人订阅
				Set<String> countrySet = rs.get("1");
				//查询需要监控的sku集合
				Map<String, Map<String, String>> skuMap = productService.getSkusByCountry(countrySet);
				if (skuMap.size() > 0) {
					List<Object[]> priceChangelist = amazonProduct2Service.findPriceChangeProduct();
					Map<String, String> skuNameMap = amazonProduct2Service.findAllProductNamesWithSku();
					Map<String, Map<String, String>> priceChangeReason = amazonProduct2Service.getPriceChangeReason();
					//所有的价格变更集合[国家 详情]
					Map<String, List<Object[]>> priceChangeMap=Maps.newHashMap();
					for (Object[] obj : priceChangelist) {
						List<Object[]> objList = priceChangeMap.get(obj[1].toString());
						if(objList==null){
							objList=Lists.newArrayList();
							priceChangeMap.put(obj[1].toString(), objList);
						}
						objList.add(obj);
					}

					List<NewsSubscribe> list = newsSubscribeService.findByEmailType(hisPriceChangeNum);
					for (NewsSubscribe newsSubscribe : list) {
						//国家和产品对应的发邮件地址
						Map<String, Map<String, String>> relationsMap = newsSubscribeService.findRelationsBySubscribe(newsSubscribe);
						//需要发邮件的产品信息
						List<Object[]> contentList = Lists.newArrayList();
						for (Map.Entry<String, List<Object[]>> entry : priceChangeMap.entrySet()) { 
						    String country=entry.getKey();
							List<Object[]> changeList = entry.getValue();
							for (Object[] obj : changeList) {
								String sku = obj[0].toString();
								String productName = skuNameMap.get(sku);
								String email = null;
								try {
									email = relationsMap.get(country).get(productName);
								} catch (NullPointerException e) {}
								if (StringUtils.isNotEmpty(email)) {
									contentList.add(obj);
								}
							}
						}
						if (contentList.size() > 0) {
							StringBuffer contents = new StringBuffer("");
							contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
							contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='5'><span style='font-weight: bold;font-size:25px'>昨日亚马逊价格变动</span></td></tr>");
							contents.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>country</th><th>产品名</th><th>sku</th><th>价格浮动</th><th>改价理由</th></tr>");
	        				
	    					for (Object[] objs : contentList) {
	        					String sku = objs[0].toString();
	        					String country1 =  objs[1].toString();
	        					Float maxPrice =  ((BigDecimal)objs[3]).floatValue();
	        					Float minPrice =  ((BigDecimal)objs[4]).floatValue();
	        					Float bili =  ((BigDecimal)objs[5]).floatValue();
	        					String productName = skuNameMap.get(sku);
	        					AmazonProduct2 product = amazonProduct2Service.getProduct(country1, sku);
	        					if(product==null){
	        						logger.info("产品为空："+country1+"==="+sku);
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
	        					if(priceChangeReason!=null&&priceChangeReason.size()>0&&priceChangeReason.get(country1)!=null&&priceChangeReason.get(country1).get(sku)!=null){
	        						reason=priceChangeReason.get(country1).get(sku);
	        					}
	        					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
	        					contents.append("<td>"+temp.toUpperCase()+"</td>");
	        					contents.append("<td><a href = \"http://www.amazon."+suff+"/dp/"+product.getAsin()+"\">"+productName+"</a></td>");
	        					contents.append("<td>"+sku+"</td>");
	        					contents.append("<td style=\"color:"+(flag==1?"red":"green")+"\">"+desc+"</td>");
	        					contents.append("<td>"+reason+"</td>");
	        					contents.append("</tr>");
	        				}
	    					contents.append("</table><br/><br/>");
	    					final MailInfo mailInfo = new MailInfo(newsSubscribe.getEmail(), subject + "昨日亚马逊价格变动(订阅编号："+newsSubscribe.getId()+")" + DateUtils.getDate("-yyyy/M/dd"), new Date());
							mailInfo.setContent(contents.toString() + contentTips.toString());
							new Thread() {
								public void run() {
									mailManager.send(mailInfo);
								}
							}.start();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("统计昨日亚马逊价格变动异常", e);
			WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "统计昨日亚马逊价格变动异常");
		}
	}
	
	//监控改价操作,1小时一次,改价信息根据订阅情况发送到相应邮箱
	public void monitorTimes() {
		try {
			Map<String, Set<String>> rs = newsSubscribeService.findByNum(priceChangeNum);
			if (rs.size() > 0) { // 有人订阅
				Set<String> countrySet = rs.get("1");
				//查询需要监控的sku集合
				Map<String, Map<String, String>> skuMap = productService.getSkusByCountry(countrySet);
				if (skuMap.size() > 0) {
					//前一个小时改价信息
					List<Object[]> priceChangelist = newsSubscribeService.findPriceChange();
					if (priceChangelist == null || priceChangelist.size() == 0) {
						return;
					}
					Map<String, String> skuNameMap = amazonProduct2Service.findAllProductNamesWithSku();
					//[国家 详情]
					Map<String, List<Object[]>> priceChangeMap = Maps.newHashMap();
					for (Object[] obj : priceChangelist) {
							List<Object[]> objList = priceChangeMap.get(obj[1].toString());
						if(objList==null){
							objList=Lists.newArrayList();
							priceChangeMap.put(obj[1].toString(), objList);
						}
						objList.add(obj);
					}
					List<NewsSubscribe> list = newsSubscribeService.findByEmailType(priceChangeNum);
					for (NewsSubscribe newsSubscribe : list) {
						//国家和产品对应的发邮件地址
						Map<String, Map<String, String>> relationsMap = newsSubscribeService.findRelationsBySubscribe(newsSubscribe);
						//需要发邮件的产品信息
						List<Object[]> contentList = Lists.newArrayList();
						for (Map.Entry<String, List<Object[]>> entry : priceChangeMap.entrySet()) { 
						    String country=entry.getKey();
							List<Object[]> changeList =entry.getValue();
							for (Object[] obj : changeList) {
								String sku = obj[0].toString();
								String productName = skuNameMap.get(sku);
								String email = null;
								try {
									email = relationsMap.get(country).get(productName);
								} catch (NullPointerException e) {}
								if (StringUtils.isNotEmpty(email)) {
									contentList.add(obj);
								}
							}
						}
						if (contentList.size() > 0) {
							StringBuffer contents = new StringBuffer("");
							contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
							contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='5'><span style='font-weight: bold;font-size:25px'>亚马逊价格实时变动</span></td></tr>");
							contents.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>country</th><th>产品名</th><th>sku</th><th>价格浮动</th><th>改价理由</th></tr>");
		    				
							for (Object[] objs : contentList) {
		    					String sku = objs[0].toString();
		    					String country =  objs[1].toString();
		    					Float price = ((BigDecimal)objs[2]).floatValue();
		    					String reason = objs[3]==null?"":objs[3].toString();
		    					String productName = skuNameMap.get(sku);
		    					AmazonProduct2 product = amazonProduct2Service.getProduct(country, sku);
								if (product == null) {
		    						logger.info("忽略产品为空的信息："+country+"==="+sku);
		    						continue;
		    					}
		    					String temp = country;
		    					if("com".equals(country)){
		    						temp = "us";
		    					}
		    					String suff = country;
		    					if("jp,uk".contains(country)){
		    						suff = "co."+country;
		    					}
		    					int flag = 0 ;
		    					String desc = "";
		    					Float oldPrice = product.getSalePrice();
		    					if (oldPrice == null) {
		    						oldPrice = product.getPrice();
								}
		    					if (oldPrice == null) {
		    						logger.info("价格为空："+country+"==="+sku);
		    						continue;
		    					}
		    					if (price.equals(oldPrice)) {	//价格已经更新,从历史价格中取原价
		    						oldPrice = amazonProduct2Service.findHisPrice(sku, country);
								}
		    					if (oldPrice == null) {
		    						continue;
								}
		    					if(price > oldPrice){
		    						flag =1;
		    						desc = oldPrice +"-->"+price +"(调整幅度"+(String.format("%.2f",(price-oldPrice)*100/oldPrice))+"%)" ;
		    					}else if (price < oldPrice){
		    						desc = oldPrice +"-->"+price+"(调整幅度"+(String.format("%.2f",(oldPrice-price)*100/oldPrice))+"%)";
		    					} else {
		    						logger.info("忽略产品："+country+"==="+sku + "\tprice:" + price + "\toldPrice" + oldPrice);
		    						continue;
		    					}
		    					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		    					contents.append("<td>"+temp.toUpperCase()+"</td>");
		    					contents.append("<td><a href = \"http://www.amazon."+suff+"/dp/"+product.getAsin()+"\">"+productName+"</a></td>");
		    					contents.append("<td>"+sku+"</td>");
		    					contents.append("<td style=\"color:"+(flag==1?"red":"green")+"\">"+desc+"</td>");
		    					contents.append("<td>"+reason+"</td>");
		    					contents.append("</tr>");
		    				}
							contents.append("</table><br/><br/>");
							final MailInfo mailInfo = new MailInfo(newsSubscribe.getEmail(), subject + "亚马逊价格实时变动提醒(订阅编号："+newsSubscribe.getId()+")" + DateUtils.getDate("-yyyy/M/dd"), new Date());
							mailInfo.setContent(contents.toString() + contentTips.toString());
							new Thread() {
								public void run() {
									boolean rs = mailManager.send(mailInfo);
									logger.info(rs + "实时改价邮件："+mailInfo.getToAddress());
								}
							}.start();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("实时改价监控异常", e);
			WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "实时改价监控异常");
		}
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		PsiInventoryFbaService  fbaService= applicationContext.getBean(PsiInventoryFbaService.class);
		PsiProductService  productService= applicationContext.getBean(PsiProductService.class);
		PsiInventoryService inventoryService= applicationContext.getBean(PsiInventoryService.class);
		PsiProductService psiProductService= applicationContext.getBean(PsiProductService.class);
		//MailManager  mailManager= applicationContext.getBean(MailManager.class);
		NewsSubscribeService  newsSubscribeService= applicationContext.getBean(NewsSubscribeService.class);
		AmazonProduct2Service  amazonProduct2Service= applicationContext.getBean(AmazonProduct2Service.class);
		NewsSubscribeMonitor monitor = new NewsSubscribeMonitor();
		monitor.setFbaService(fbaService);
		//monitor.setMailManager(mailManager);
		monitor.setNewsSubscribeService(newsSubscribeService);
		monitor.setProductService(productService);
		monitor.setInventoryService(inventoryService);
		monitor.setAmazonProduct2Service(amazonProduct2Service);
		monitor.setPsiProductService(psiProductService);
		float f = amazonProduct2Service.findHisPrice("98-FE2005-DE", "de");
		System.out.println(f);
		//monitor.monitorOnce();
		//monitor.monitorTimes();
		applicationContext.close();
	}

	public PsiInventoryFbaService getFbaService() {
		return fbaService;
	}

	public void setFbaService(PsiInventoryFbaService fbaService) {
		this.fbaService = fbaService;
	}

	public PsiProductService getProductService() {
		return productService;
	}

	public void setProductService(PsiProductService productService) {
		this.productService = productService;
	}

//	public MailManager getMailManager() {
//		return mailManager;
//	}
//
//	public void setMailManager(MailManager mailManager) {
//		this.mailManager = mailManager;
//	}

	public NewsSubscribeService getNewsSubscribeService() {
		return newsSubscribeService;
	}

	public void setNewsSubscribeService(NewsSubscribeService newsSubscribeService) {
		this.newsSubscribeService = newsSubscribeService;
	}

	public AmazonProduct2Service getAmazonProduct2Service() {
		return amazonProduct2Service;
	}

	public void setAmazonProduct2Service(AmazonProduct2Service amazonProduct2Service) {
		this.amazonProduct2Service = amazonProduct2Service;
	}

	public PsiInventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(PsiInventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public PsiProductService getPsiProductService() {
		return psiProductService;
	}

	public void setPsiProductService(PsiProductService psiProductService) {
		this.psiProductService = psiProductService;
	}

}
