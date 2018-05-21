package com.springrain.erp.modules.amazoninfo.scheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.tree.DefaultText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.HttpRequest;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.SpringContextHolder;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonLightningDeals;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct2;
import com.springrain.erp.modules.amazoninfo.entity.ProductHistoryPrice;
import com.springrain.erp.modules.amazoninfo.entity.ProductPrice;
import com.springrain.erp.modules.amazoninfo.entity.SaleProfit;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecastByMonth;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.service.AmazonAndFacebookService;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.amazoninfo.service.AmazonOutOfProductService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonPromotionsWarningService;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseGoalService;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseTypeGoalService;
import com.springrain.erp.modules.amazoninfo.service.ProductHistoryPriceService;
import com.springrain.erp.modules.amazoninfo.service.ProductPriceService;
import com.springrain.erp.modules.amazoninfo.service.ReturnGoodsService;
import com.springrain.erp.modules.amazoninfo.service.SaleProfitService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportMonthTypeService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.SalesForecastServiceByMonth;
import com.springrain.erp.modules.amazoninfo.service.SettlementReportOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonRemovalOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonSalesSummaryFileService;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.service.CustomProductProblemService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.psi.entity.FbaInbound;
import com.springrain.erp.modules.psi.entity.FbaInboundItem;
import com.springrain.erp.modules.psi.entity.ProductSalesInfo;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiInventoryTotalDto;
import com.springrain.erp.modules.psi.entity.PsiInventoryTotalDtoByInStock;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductAttribute;
import com.springrain.erp.modules.psi.entity.PsiProductGroupCustomer;
import com.springrain.erp.modules.psi.entity.PsiProductInStock;
import com.springrain.erp.modules.psi.entity.PsiProductTransportRate;
import com.springrain.erp.modules.psi.service.FbaInboundService;
import com.springrain.erp.modules.psi.service.ProductSalesInfoService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiInventoryTurnoverDataService;
import com.springrain.erp.modules.psi.service.PsiLadingBillService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductInStockService;
import com.springrain.erp.modules.psi.service.PsiProductMoldFeeService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.psi.service.PsiTransportPaymentService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.DictService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

public class SaveHistoryInfoMonitor {

	private final static Logger logger = LoggerFactory.getLogger(SaveHistoryInfoMonitor.class);
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private ProductHistoryPriceService productHistoryPriceService;
	
	@Autowired
	private CustomProductProblemService customProductProblemService;

	@Autowired
	private PsiInventoryService	psiInventoryService;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private ProductSalesInfoService productSalesInfoService;
	
	@Autowired
	private PsiProductInStockService productInStockService;
	
	@Autowired
	private ProductPriceService productPriceService;
	
	@Autowired
	private SaleReportService saleReportService;
	
	
	@Autowired
	private DictService dictService;
	
	@Autowired
	private PsiTransportPaymentService psiTransportPaymentService;
	
	
	@Autowired
	private SalesForecastServiceByMonth salesForecastService;
	
	@Autowired
	private AmazonOutOfProductService   outOfProductService;
	
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	
	@Autowired
	private PsiProductAttributeService psiProductAttributeService;

	@Autowired
	private FbaInboundService fbaInboundService;

	@Autowired
	private EventService eventService;
	
	@Autowired
	private AmazonOrderService orderService;
	
	@Autowired
	private MailManager mailManager;

	@Autowired
	private SaleReportMonthTypeService saleReportMonthTypeService;
	
	@Autowired
	private EnterpriseGoalService enterpriseGoalService;
	
	@Autowired
	private SaleProfitService saleProfitService;
	
	@Autowired
	private EnterpriseTypeGoalService enterpriseTypeGoalService;

	@Autowired
	private PsiProductTieredPriceService psiProductTieredPriceService;
	@Autowired
	private PsiLadingBillService psiLadingBillService;

	@Autowired
	private AmazonCustomerService amazonCustomerService;

	@Autowired
	private AmazonSalesSummaryFileService amazonSalesSummaryFileService;
	
	@Autowired
	private AmazonAndFacebookService facebookService;
	
	@Autowired
	private AmazonRemovalOrderService amazonRemovalOrderService;
	
	@Autowired
	private PsiInventoryTurnoverDataService turnoverDataService;
	
	@Autowired
	private PsiProductGroupUserService psiProductGroupUserService;
	
	@Autowired
	private PsiProductTypeGroupDictService psiProductTypeGroupDictService;
	
	@Autowired
	private AmazonPromotionsWarningService amazonPromotionsWarningService;
	
	@Autowired
	private SettlementReportOrderService settlementReportOrderService;
	@Autowired
	private ReturnGoodsService returnGoodsService;
	
	@Autowired
	private PsiProductMoldFeeService psiProductMoldFeeService;
	
	private static List<String> rateCurrency = Lists.newArrayList("USD/CNY","USD/EUR","USD/JPY","USD/GBP","USD/CAD","USD/MXN");
	
	private static final String WEB_LINK = "http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote";

	//主力类型产品特殊类型销量标准map,其他类型默认为1000
	private static Map<String, Integer> isMainMap;
	
	static{
		isMainMap=Maps.newHashMap();
		isMainMap.put("express card", 700);
		isMainMap.put("wireless presenter", 800);
	}

	public void goalTest() {
	}
	
	//初始化闪促费用和仓储费用
	public void dealInit(){
		Map<String,Map<String,Float>> allRate = amazonProduct2Service.getAllRateByDate();
		SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
		Date today = new Date();
		try {
			Date start = dayFormat.parse("20170101");//初始化时间
			List<SaleProfit> list = Lists.newArrayList();
			while(true){
				String dateStr = dayFormat.format(start);
				Map<String, Float> rateRs = allRate.get(dateStr);
				Map<String, Map<String, AmazonLightningDeals>> dealsFeeMap = 
						amazonPromotionsWarningService.findDealDetailByDay(dateStr, rateRs);
				for (Entry<String, Map<String, AmazonLightningDeals>> entry : dealsFeeMap.entrySet()) {
					String productName = entry.getKey();
					Map<String, AmazonLightningDeals> productMap = entry.getValue();
					for (Entry<String, AmazonLightningDeals> productEntry : productMap.entrySet()) {
						String accountName = productEntry.getKey();
						AmazonLightningDeals deals = productEntry.getValue();
						String country = deals.getCountry();
						SaleProfit profit = saleProfitService.getByUnique(dateStr, country, productName, accountName);
						if (profit != null) {
							float vat = 0;
							if ("jp".equals(country)) {
								vat = deals.getDealFee() * 0.08f;	//当前只有日本收取消费税
							}
							profit.setDealFee(deals.getDealFee()+vat);
							profit.setDealSalesVolume(deals.getActualQuantity());
							profit.setDealProfit(deals.getConv1());
							list.add(profit);
						}
					}
				}
				start = DateUtils.addDays(start, 1);
				if (start.after(today)) {
					break;
				}
			}
			if (list.size() > 0) {
				saleProfitService.saveList(list);
			}

			//按天统计完毕后按月汇总
			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
				start = dayFormat.parse("20170101");
				int i = 0;
				while (true) {
					String month = format.format(DateUtils.addMonths(start, i));
					logger.info("统计月份" + month);
					saleReportMonthTypeService.saveOrUpdate(month);
					Map<String,Float> avgRateMap = amazonProduct2Service.getRateByAvg(month, "1");
					saleReportMonthTypeService.updateStorageFee(month, avgRateMap);
					saleReportMonthTypeService.updateLongStorageFee(month, avgRateMap);
					i++;
					if (month.equals(format.format(today))) {
						break;
					}
				}
			} catch (Exception e) {
				logger.error("Note:初始化闪促费用后按月汇总数据异常", e);
			}
		} catch (Exception e) {
			logger.error("Note:统计闪促费用数据异常", e);
		}
	}
	
	 /**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	private static String sendGet(String url, String param,int num) {
		if(num>2){
    		return null;
    	}
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			if (StringUtils.isNotBlank(param)) {
				urlNameString = urlNameString + "?" + param;
			}
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();			
						
			String redirect = connection.getHeaderField("Location");
		    if (redirect != null){
		    	connection = new URL(redirect).openConnection();
		    	connection.setRequestProperty("accept", "*/*");
				connection.setRequestProperty("connection", "Keep-Alive");
				connection.setRequestProperty("user-agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				// 建立实际的连接
				connection.connect();
		    }
			
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error("", e);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
			}
			return sendGet(url, param,++num);
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
	@SuppressWarnings("deprecation")
	public void saveHistoryInfo() {
		try{
		//保存当天断货信息
			logger.info("保存断货产品开始");
			outOfProductService.createOutOfData();
			logger.info("保存断货产品结束");
		}catch(Exception e){
			logger.error("Note:保持断货数据异常！", e);
		}
		/*try{
			updateAttributePrice();
		}catch(Exception e){
			logger.error("更新产品属性价格信息异常！！", e);
		}
		*/
		StringBuffer sb = new StringBuffer();
		final Date today = new Date();
		logger.info("开始保存价格历史信息！！");
		try {
			AmazonProduct2 amazonProduct2 = new AmazonProduct2();
			amazonProduct2.setActive("1");
			Page<AmazonProduct2> page = new Page<AmazonProduct2>(1, 600000);
			page = amazonProduct2Service.find(page,amazonProduct2);
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			List<ProductHistoryPrice> list = Lists.newArrayList();
			for (AmazonProduct2 product2 : page.getList()) {
				Float salePrice = product2.getSalePrice();
				salePrice = (salePrice==null?0f:salePrice);
				list.add(new ProductHistoryPrice(today,product2.getSku(),salePrice,product2.getCountry()));
			}
			productHistoryPriceService.save(list);
		} catch (Exception e) {
			logger.error("Note:保存价格历史信息异常！！", e);
		}
		logger.info("保存价格历史信息结束！！");
		logger.info("开始计算保本价格！！");
		//开始计算产品成本价格
		try{
			//先插入昨天的价格，再修改
			productPriceService.saveYestDay();
			Map<String, String> skuAndName = psiProductService.findProductNameWithSku(null);
			//亚马逊佣金比例
			Map<String,Map<String,Integer>> commiFee = saleReportService.findAllCommission();
			//亚马逊处理费
			Map<String,Map<String,Float>> fbaFee = saleReportService.findAllFbaFee();
			Map<String, Map<String,Float>> costAndGw = saleReportService.getProductPriceAndTranGw("USD",null);
			Map<String,Object[]> tran = saleReportService.getProductTranVatPcent();
			Map<String, PsiProductTransportRate>  countryTranFee = psiTransportPaymentService.findTransportPriceByToCountry();
			Map<String, Map<String, PsiProductTransportRate>> productTranFee = psiTransportPaymentService.findTransportRateAndPrice();
		
			//计算采购价
			Map<String,Map<String,Object>> beforeMap=psiProductAttributeService.findBeforePrice();
			//需要计算模具费的产品
			Map<String, Float> moldFeeMap = psiProductMoldFeeService.findMoldFeeForBuyCost();
			
			List<ProductPrice> products = Lists.newArrayList();
			for (Map.Entry<String, Map<String, Integer>>  entry : commiFee.entrySet()) {  
			    String country =entry.getKey();
			    if ("com1".equals(country)||"com2".equals(country)||"com3".equals(country)) {
					continue;
				}
				Map<String,Integer> data =entry.getValue();
				for (Map.Entry<String, Integer>  entryData : data.entrySet()) {  
				    String sku =entryData.getKey();
					Float fba = fbaFee.get(country).get(sku);
					fba = fba ==null?0:fba;
					String productName = skuAndName.get(sku);
					if(productName==null || "Inateck Old".equals(productName)|| "Inateck other".equals(productName)){
						continue;
					}
					Map<String, PsiProductTransportRate> tranPrice = productTranFee.get(productName);
					if(tranPrice==null){
						tranPrice = countryTranFee;
					}
					
					Map<String,Float> map = costAndGw.get(productName);
					Float cost = 0f;
					Float tranGw = 0f;
					try{
						if(beforeMap.get(productName)!=null&&beforeMap.get(productName).get("price")!=null){
									Float beforePrice=(Float)beforeMap.get(productName).get("price");
			                       if(map!=null){
			                        	cost =beforePrice+map.get("parts");
										tranGw = map.get("gw");
										cost = cost ==null?0f:cost;
										tranGw = tranGw==null?0f:tranGw;
			                       }
						}else{
								if(map!=null){
									cost = map.get("price");
									tranGw = map.get("gw");
									cost = cost ==null?0f:cost;
									tranGw = tranGw==null?0f:tranGw;
					 			}
						}
					}catch(Exception e){
						logger.error("采购价变更生效异常",e);
						if(map!=null){
							cost = map.get("price");
							tranGw = map.get("gw");
							cost = cost ==null?0f:cost;
							tranGw = tranGw==null?0f:tranGw;
		 				}
					}
					
					if(cost==0f){
						continue;
					}
					//算保本价时把模具费计入到采购价里面(换算为美元)
					if (moldFeeMap.get(productName)!=null && moldFeeMap.get(productName)>0) {
						float moldFee = moldFeeMap.get(productName)*MathUtils.getRate("CNY", "USD", null);
						cost += moldFee;
						logger.info(productName + "采购价包含模具费(USD)" + moldFee);
					}
					
					Object[] tranVats = tran.get(productName);
					Float tariffPcent = 0f;
					if(tranVats!=null){
						if("jp".equals(country)){
							tariffPcent = Float.parseFloat(tranVats[1].toString());
						}else if ("com".equals(country)){
							tariffPcent = Float.parseFloat(tranVats[3].toString());
						}else if("de,fr,es,it,uk".contains(country)){
							tariffPcent = Float.parseFloat(tranVats[2].toString());
						}else if ("ca".equals(country)){
							tariffPcent = Float.parseFloat(tranVats[4].toString());
						}else if ("mx".equals(country)){
							tariffPcent = Float.parseFloat(tranVats[5].toString());
							
						}
						tariffPcent = tariffPcent==null?0f:tariffPcent;
					}
					ProductPrice productPrice = new ProductPrice(productName, sku, cost, fba, entryData.getValue(), tranGw, tariffPcent, today, country);
					productPrice.count(MathUtils.getRate("CNY", "USD", null),tranPrice,countryTranFee);
					products.add(productPriceService.find(productPrice));
				}
			}
			productPriceService.save(products);
			//加入泛欧产品价格
			productPriceService.updatePanEu();
			amazonProduct2Service.countCostPrice(today);
			amazonProduct2Service.updateAndSaveCostPrice();
			sb.append("计算保本价格结束\n");
		}catch(Exception e){
			sb.append("计算保本价格异常！！\n");
			logger.error("Note:计算保本价格异常",e);
		}
		logger.info("保本价格计算结束！！");
		
		new Thread(){
			public void run() {
				try {
					logger.info("开始抓取实时汇率！！");
					Map<String, Float> data = getRate();
					if(data.size()>0){
						amazonProduct2Service.saveRate(data);
						AmazonProduct2Service.setRateConfig(data);
					}
					logger.info("实时汇率保存完毕！！");
				} catch (Exception e) {
					logger.error("抓取实时汇率发送异常！！", e);
				}
			}
		}.start();
		
		logger.info("开始处理邮件&事件归总任务");
		try {
			customProductProblemService.saveOrUpdateEmailProblem();//处理邮件问题
			customProductProblemService.saveOrUpdateEventProblem();//处理事件问题
			customProductProblemService.duplicateEmailProblem(); //去掉邮件问题中重复数据
			sb.append("处理邮件&事件归总任务成功\n");
		} catch (Exception e) {
			sb.append("处理邮件&事件归总任务异常！！\n");
			logger.error("Note:邮件&事件归总任务处理异常", e);
		}
		logger.info("处理邮件&事件归总任务结束");
		
		logger.info("开始保存产品库存信息...");
		try {
			savaProductInfo();
			sb.append("保存产品库存信息成功\n");
		} catch (Exception e) {
			sb.append("保存产品库存信息出现异常！！\n");
			logger.error("Note:保存产品库存信息出现异常", e);
		}
		logger.info("保存产品库存信息完毕");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {	//星期天更新
			try {
				logger.info("开始更新产品采购周属性...");
				psiProductAttributeService.updatePurchaseWeek();//采购周循环更新
				logger.info("更新产品采购周属性完毕...");
				sb.append("每星期天更新产品采购周属性完毕\n");
			} catch (Exception e) {
				sb.append("每星期天更新产品采购周属性异常！！\n");
				logger.error("Note:更新产品采购周属性发生异常", e);
			}
		}
		
		//更新session事件
		try {
			psiProductService.updateSessionEvents();
		} catch (Exception e) {
			logger.error("Note:更新session影响事件数据出错!!!", e);
		}
		
		logger.info("更新上架周期");
		try {
			saleReportService.updateProductCycle();
		} catch (Exception e) {
			logger.error("Note:更新上架周期异常", e);
		}
		logger.info("更新上架周期结束");
		
		//更新FBAInbound发货完成时间
		try {
			logger.info("开始更新FBAInbound完成时间...");
			Map<Integer, List<FbaInboundItem>> fbaInboundItemMap = fbaInboundService.findNotFinish();
			List<FbaInbound> finishList = Lists.newArrayList();
			for (Map.Entry<Integer, List<FbaInboundItem>> entry : fbaInboundItemMap.entrySet()) { 
				List<FbaInboundItem> itemList =entry.getValue();
				int quantityReceived = 0;
				int quantityShiped = 0;
				for (FbaInboundItem item : itemList) {
					quantityReceived+=(item.getQuantityReceived()==null?0:item.getQuantityReceived());
					quantityShiped+=(item.getQuantityShipped()==null?0:item.getQuantityShipped());
				}
				if (quantityShiped > 0 && (double)quantityReceived/quantityShiped >= 0.9) {
					FbaInbound fbaInbound = itemList.get(0).getFbaInbound();
					fbaInbound.setFinishDate(new Date());
					finishList.add(fbaInbound);
				}
			}
			if (finishList.size() > 0) {
				fbaInboundService.save(finishList);
			}
			logger.info("更新FBAInbound完成时间完毕...");
			sb.append("更新FBAInbound完成时间完毕\n");
		} catch (Exception e) {
			sb.append("更新FBAInbound完成时间发生异常\n");
			logger.error("Note:更新FBAInbound完成时间发生异常", e);
		}
		
		try {
			logger.info("开始更新Review Refund事件关联订单信息...");
			List<Event> eventList = eventService.findReviewRefundEvent();
			List<Event> newList = Lists.newArrayList();
			for (Event event : eventList) {
				//查订单
				AmazonOrder order = orderService.findByLazy(event.getInvoiceNumber());
				if(order!=null && "Shipped".equals(order.getOrderStatus())){
					event.setCustomEmail(order.getBuyerEmail());
					event.setCustomId(orderService.getCustomIdByOrderId(order.getAmazonOrderId()));
					event.setCustomName(order.getBuyerName());
					if (!"1".equals(event.getRefundType())) {
						event.setTotalPrice(order.getOrderTotal());
					}
					newList.add(event);
				}
			}
			if (newList.size() > 0) {
				eventService.save(newList);
			}
			logger.info("更新Review Refund事件关联订单信息完毕");
			sb.append("更新Review Refund事件关联订单信息完毕\n");
		} catch (Exception e) {
			sb.append("更新Review Refund事件关联订单信息发生异常\n");
			logger.error("Note:更新Review Refund事件关联订单信息发生异常", e);
		}
		
		try {
			logger.info("开始更新客户起止购买时间相差天数...");
			amazonCustomerService.updateDays();
			logger.info("更新客户起止购买时间相差天数完毕...");
			sb.append("更新客户起止购买时间相差天数成功\n");
		} catch (Exception e) {
			sb.append("更新客户购买时间相差天数发生异常！！\n");
			logger.error("Note:更新客户购买时间相差天数发生异常", e);
		}
		
		try {
			Thread.sleep(1000 * 60 * 10l);	//睡眠十分钟到第二天凌晨开始统计
			logger.info("开始统计昨日发货订单...");
			amazonSalesSummaryFileService.salesSummary();
			logger.info("统计昨日发货订单完毕...");
			sb.append("统计昨日发货订单成功\n");
		} catch (Exception e) {
			sb.append("统计昨日发货订单异常！！\n");
			logger.error("Note:统计昨日发货订单异常", e);
		}
		if (dayOfMonth == 2) {	//每月2号生成上月订单报表
			try {
				logger.info("开始更新订单报表...");
				String month = new SimpleDateFormat("yyyy-MM").format(DateUtils.addMonths(today, -1));
				amazonSalesSummaryFileService.generateReports(month, "eu");
				amazonSalesSummaryFileService.generateReports(month, "us");
				amazonSalesSummaryFileService.generateReports(month, "jp");
				amazonSalesSummaryFileService.generateReports(month, "ca");
				amazonSalesSummaryFileService.generateReports(month, "mx");

				amazonSalesSummaryFileService.generateCsvReports(month, "eu");
				amazonSalesSummaryFileService.generateCsvReports(month, "us");
				amazonSalesSummaryFileService.generateCsvReports(month, "jp");
				amazonSalesSummaryFileService.generateCsvReports(month, "ca");
				amazonSalesSummaryFileService.generateCsvReports(month, "mx");
				logger.info("更新订单报表完毕...");
				sb.append("每月2号更新订单报表成功\n");
			} catch (Exception e) {
				sb.append("每月2号更新订单报表发生异常！！\n");
				logger.error("Note:每月2号更新订单报表发生异常", e);
			}
		}
		//统计退货率
		returnRate();
		//设置默认预测方案
		try {
			salesForecastService.initSalefastcast();
		} catch (Exception e) {
			logger.error("Note:预测方案更改错误", e);
		}
		try {
			WeixinSendMsgUtil.sendTextMsgToUser("tim|eileen", sb.toString());
		} catch (Exception e) {
			logger.warn("微信消息内容：" + sb.toString(), e);
		}
	}
	
	//统计销售额利润数据
	public void updateProfit() {
		logger.info("开始统计销售额利润数据");
		StringBuilder sbBuilder = new StringBuilder();
		Date today = new Date();

		//统计召回订单运费和采购成本
		try {
			amazonRemovalOrderService.updateOrderFee();
			logger.info("更新召回订单成本完毕");
		} catch (Exception e) {
			sbBuilder.append("更新召回订单成本异常");
			logger.error("Note:更新召回订单成本异常", e);
		}

		try {
			settlementReportOrderService.saveAndUpdateFee();
			logger.info("已经更新产品费用表！！");
		} catch (Exception e) {
			sbBuilder.append("更新产品费用表异常");
			logger.error("Note:更新产品费用表异常", e);
		}
		//统计多渠道发货订单的费用
		try {
			saleProfitService.updateOutboundFee();
		} catch (Exception e) {
			sbBuilder.append("统计多渠道订单费用数据异常");
			logger.error("Note:统计多渠道订单费用数据异常", e);
		}
		try {
			SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
			Map<String,Map<String,Float>> allRate = amazonProduct2Service.getAllRateByDate();
			Map<String, String> skuNameMap = saleProfitService.findSkuNames();
			Date start = DateUtils.addMonths(today, -3);	//更新三个月之内的数据
			//退货数据[日期[国家[sku  退货数]]]
			Map<String, Map<String, Map<String, Integer>>> returns = 
					saleProfitService.findReturnNums(dayFormat.format(start), skuNameMap);
			while(true){
				String dateStr = dayFormat.format(start);
				//产品保本价[sku_国家 保本价]
				Map<String, Float> priceMap = saleProfitService.findPriceMapByDay(dateStr, allRate.get(dateStr));
				List<SaleReport> reports = saleProfitService.findSaleReportListByDay(dateStr);
				int flag = 0;
				while (true) {
					if (flag > 5) {
						logger.info("5次更新异常:" + dateStr);
						break;
					}
					try {
						saleProfitService.updatePrice(reports, dateStr, returns, priceMap);
						break;	//最多尝试5次,成功后立即跳出循环
					} catch (Exception e) {
						flag++;
						if (flag == 5) {
							logger.error("更新异常", e);
						}
						Thread.sleep(2000l);	//2秒后重试
					}
				}
				start = DateUtils.addDays(start, 1);
				if (start.after(today)) {
					break;
				}
			}
		} catch (Exception e) {
			sbBuilder.append("插入保本价格和退货数量异常\n");
			logger.error("Note:插入保本价格异常", e);
		}

		Map<String,Map<String,Float>> allRate = amazonProduct2Service.getAllRateByDate();
		try {
			//插入运费数据(只处理2016年的数据)
			Map<String, Map<String, Float>> allFreight = saleProfitService.findAllFreight(allRate);	//所有产品运费信息(转换成欧元)
			Map<String, String> minDateMap = saleProfitService.findAllFreightMinDate();
			int num = 0;
			while (true) {
				if (num > 10) {	//尝试10次
					sbBuilder.append("10次插入运费数据异常\n");
					break;
				}
				List<SaleReport> list = saleProfitService.findNoFreight();
				if (list == null || list.size() == 0) {
					break;
				}
				try {
					saleProfitService.updateFreight(list, minDateMap, allFreight);
				} catch (Exception e) {
					num++;
					logger.warn("第"+num+"次插入运费数据异常,一分钟后重试" + e.getMessage());
					Thread.sleep(60 * 1000);	//1分钟后重试
				}
			}
		} catch (Exception e) {
			sbBuilder.append("插入运费数据异常\n");
			logger.error("Note:插入运费数据异常", e);
		}
		SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			//先按天统计
			Date start = DateUtils.addMonths(today, -3);	//更新三个月之内的数据
			while(true){
				String dateStr = dayFormat.format(start);
				Map<String, Float> rateRs = allRate.get(dateStr);
				saleProfitService.saveOrUpdate(dateStr, rateRs);
				start = DateUtils.addDays(start, 1);
				if (start.after(today)) {
					break;
				}
			}
		} catch (Exception e) {
			sbBuilder.append("统计销售额利润信息数据异常\n");
			logger.error("Note:统计销售额利润信息数据异常", e);
		}
		try {
			//统计三个月之内召回订单数据
			saleProfitService.updateRecallOrder(dayFormat.format(DateUtils.addMonths(today, -3)), allRate);
		} catch (Exception e) {
			sbBuilder.append("统计召回订单数据异常\n");
			logger.error("Note:统计召回订单数据异常", e);
		}
		try {
			//统计三个月之内评测订单数据
			saleProfitService.updateReviewOrder(dayFormat.format(DateUtils.addMonths(today, -3)), allRate);
		} catch (Exception e) {
			sbBuilder.append("统计评测订单数据异常\n");
			logger.error("Note:统计评测订单数据异常", e);
		}
		try {
			//统计三个月之内自发货快递费用
			saleProfitService.updateExpressFee(dayFormat.format(DateUtils.addMonths(today, -3)), allRate);
		} catch (Exception e) {
			sbBuilder.append("统计自发货快递费用异常\n");
			logger.error("Note:统计自发货快递费用异常", e);
		}
		try {
			//统计三个月之内vine项目费用
			saleProfitService.updateVineFee(dayFormat.format(DateUtils.addMonths(today, -3)), allRate);
		} catch (Exception e) {
			sbBuilder.append("统计vine项目费用异常\n");
			logger.error("Note:统计vine项目费用异常", e);
		}
		try {
			//统计市场推广部B2B订单
			saleProfitService.updateMarketOrder(allRate);
		} catch (Exception e) {
			sbBuilder.append("统计B2B订单数据异常\n");
			logger.error("Note:统计B2B订单数据异常", e);
		}
		try {
			//统计关税
			Date start = DateUtils.addDays(today, -5);	//从5天之前开始
			while(true){
				String dateStr = dayFormat.format(start);
				saleProfitService.updateTariffs(allRate.get(dateStr), dateStr);
				start = DateUtils.addDays(start, 1);
				if (start.after(today)) {
					break;
				}
			}
		} catch (Exception e) {
			sbBuilder.append("插入关税数据异常\n");
			logger.error("Note:插入关税数据异常", e);
		}
		try {
			//补齐产品分类属性
			saleProfitService.updateProductAttr();
		} catch (Exception e) {
			sbBuilder.append("补齐产品分类属性异常\n");
			logger.error("Note:补齐产品分类属性异常", e);
		}
		//按天统计完毕后按月汇总
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
			Date start = DateUtils.addMonths(today, -3);	//从三个月之前开始
			int i = 0;
			while (true) {
				String month = format.format(DateUtils.addMonths(start, i));
				logger.info("汇总月份" + month);
				saleReportMonthTypeService.saveOrUpdate(month);
				i++;
				if (month.equals(format.format(today))) {
					break;
				}
			}
		} catch (Exception e) {
			sbBuilder.append("按月汇总销售额利润信息数据异常\n");
			logger.error("Note:按月汇总销售额利润信息数据异常", e);
		}
		//统计仓储费用
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
			Date start = DateUtils.addMonths(today, -2);	//最近两个月的
			int i = 0;
			while (true) {
				String month = format.format(DateUtils.addMonths(start, i));
				if (month.equals(format.format(today))) {
					break;
				}
				Map<String,Float> avgRateMap = amazonProduct2Service.getRateByAvg(month, "1");
				saleReportMonthTypeService.updateStorageFee(month, avgRateMap);
				saleReportMonthTypeService.updateLongStorageFee(month, avgRateMap);
				i++;
			}
		} catch (Exception e) {
			sbBuilder.append("统计仓储费用数据异常\n");
			logger.error("Note:统计仓储费用数据异常", e);
		}
		try {
			//补齐月度产品分类属性
			saleReportMonthTypeService.updateProductAttr();
		} catch (Exception e) {
			sbBuilder.append("补齐月度产品分类属性异常\n");
			logger.error("Note:补齐月度产品分类属性异常", e);
		}
		
		//统计存货周转率
		Date date = DateUtils.addHours(new Date(), 1);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {	//一小时后是一号说明是月底了
			try {
				//计算月存货周转率
				SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
				String month1 = format.format(DateUtils.addMonths(date, -2));
				String month2 = format.format(DateUtils.addMonths(date, -1));
				turnoverDataService.saveTurnoverDataByMonth(month1, month2);
				if (calendar.get(Calendar.MONTH) == 0) {	//一月份,说明上个月是年底
					int year = calendar.get(Calendar.YEAR);
					int year1 = year - 2;
					int year2 = year - 1;
					//计算年存货周转率
					logger.info("统计年度存货周转率");
					turnoverDataService.saveTurnoverDataByYear(year1+"", year2+"");
					logger.info("统计年度存货周转率完毕");
				}
			} catch (Exception e) {
				sbBuilder.append("统计存货周转率异常！");
				logger.error("Note:统计存货周转率异常", e);
			}
		}
		try {
			psiProductMoldFeeService.updateFlag();
		} catch (Exception e) {
			sbBuilder.append("统计模具费用标记异常！" + e.getMessage());
			logger.error("Note:统计模具费用标记异常！", e);
		}
		sbBuilder.append("统计近3个月销售额利润信息完毕");
		WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, sbBuilder.toString());
	}
	
	public static Map<String, Float> getRate(){
		Map<String, Float> rs = Maps.newConcurrentMap();
		String data = null;
		try {
			data = sendGet(WEB_LINK, "", 0);
		} catch (NullPointerException e) {}
		String serverId = Global.getConfig("server.id");
		if ("1".equals(serverId)) {
			int i = 0;
			while(StringUtils.isEmpty(data)&&i<2){
				try {
					data = HttpRequest.sendGet(WEB_LINK, "");
				} catch (NullPointerException e) {}
				i++;
			}
			
		}
		if(StringUtils.isNotEmpty(data)){
            try {
				Document document = DocumentHelper.parseText(data);
				for (String currency : rateCurrency) {
					DefaultText priceText = (DefaultText)document.selectSingleNode("//resource[.//field[@name='name'][text()='"+currency+"']]/field[@name='price']/text()");
					if(priceText==null){
						logger.error("缺失汇率数据:"+currency + "\t" + data);
						break;
					}
					String priceStr = priceText.getText();
					try {
						Float price = Float.parseFloat(priceStr);
						rs.put(currency, price);
						String[] temp = currency.split("/");
						rs.put(temp[1]+"/"+temp[0], 1f/price);
					} catch (NumberFormatException e) {
						rs.clear();
						return rs;
					}
				}
				if(rs.size()==rateCurrency.size()*2){
					String jpy_To_cny =  "JPY/CNY";
					rs.put(jpy_To_cny,rs.get("USD/CNY")/rs.get("USD/JPY"));
					String gbp_To_eur =  "GBP/EUR";
					rs.put(gbp_To_eur,rs.get("USD/EUR")/rs.get("USD/GBP"));
					String jpy_To_eur =  "JPY/EUR";
					rs.put(jpy_To_eur,rs.get("USD/EUR")/rs.get("USD/JPY"));
					String cad_To_eur =  "CAD/EUR";
					rs.put(cad_To_eur,rs.get("USD/EUR")/rs.get("USD/CAD"));
					String mxn_To_eur =  "MXN/EUR";
					rs.put(mxn_To_eur,rs.get("USD/EUR")/rs.get("USD/MXN"));
					String jpy_To_cad =  "JPY/CAD";
					rs.put(jpy_To_cad,rs.get("USD/CAD")/rs.get("USD/JPY"));
					String jpy_To_gbp =  "JPY/GBP";
					rs.put(jpy_To_gbp,rs.get("USD/GBP")/rs.get("USD/JPY"));
				}
			} catch (DocumentException e) {
				logger.error("汇率数据解析异常:"+data, e);
			}
		}else{
			logger.info("汇率数据无法获取"+DateUtils.getDate());
		}
		
		if(rs.size()==0){
			AmazonProduct2Service amazonProduct2Service = SpringContextHolder.getBean(AmazonProduct2Service.class);
			return amazonProduct2Service.getRateNewest();
			//测试灾备时用
//			rs.put("USD/CNY",6.64f);
//			rs.put("JPY/CNY", 0.06f);
//			rs.put("EUR/CNY", 7.34f);
//			rs.put("CAD/CNY", 5.11f);
//			rs.put("GBP/CNY", 8.85f);
//			
//			rs.put("JPY/USD", 0.0097f);
//			rs.put("EUR/USD", 1.1f);
//			rs.put("CAD/USD", 0.77f);
//			rs.put("GBP/USD", 1.35f);
//			
//			rs.put("GBP/EUR", 1.21f);
//			rs.put("JPY/EUR", 0.0088f);
//			rs.put("CAD/EUR", 0.7f);
//			rs.put("JPY/CAD", 0.01f);
//			rs.put("JPY/GBP", 0.0073f);
//			
//			rs.put("USD/EUR", 0.9006f);
//			rs.put("CNY/USD", 0.15f);
//			rs.put("USD/CAD", 1.3f);
//			rs.put("USD/GBP", 0.74f);
//			rs.put("USD/JPY", 102.705f);
//			rs.put("USD/MXN", 18.558f);
//			rs.put("MXN/EUR", 0.048f);
//			rs.put("MXN/USD", 0.053f);
		}
		return rs;
	} 
	
	
	public void initData(){
		List<PsiProduct> list = psiProductService.findAll();
		Map<String,String> merchandiserMap=psiProductService.findCreateUserMap();
		Map<String,String> productManagerMap=psiProductService.getManagerByProductType();
		//产品类型-采购经理
		Map<String,String> purchaseManagerMap=psiProductService.getPurchaseByProductType();
		//产品线-国家-客服
		Map<String,Map<String,List<PsiProductGroupCustomer>>> customerMap=psiProductGroupUserService.findAllGroupCustomer();
		//产品类型-摄影
		Map<String,String> photoMap=psiProductAttributeService.getCameraman();
		//产品名称-产品线ID
		Map<String,String> nameAndLineIdMap=psiProductTypeGroupDictService.getLineByName();
		//产品线-国家-人id+","+name
		Map<String,Map<String,String>> saleUserMap=psiProductGroupUserService.getSingleGroupUser();
		
		for (PsiProduct psiProduct : list) {
			List<String> productNameWithColor = psiProduct.getProductNameWithColor();
			for (int i = 0; i < productNameWithColor.size(); i++) {
				String productName = productNameWithColor.get(i);
				PsiProductInStock totalInStock = new PsiProductInStock(); //汇总
				totalInStock.setCountry("total");
				totalInStock.setProductName(productName);
				String productType=psiProduct.getType();
				try{
					totalInStock.setMerchandiser(merchandiserMap.get(psiProduct.getName()));
					if(productManagerMap!=null&&productManagerMap.get(productType)!=null){
						totalInStock.setProductManager(productManagerMap.get(productType));
					}
					if(purchaseManagerMap!=null&&purchaseManagerMap.get(productType)!=null){
						totalInStock.setPurchaseUser(purchaseManagerMap.get(productType));
					}
					if(photoMap!=null&&photoMap.get(productName)!=null){
						totalInStock.setCameraman(photoMap.get(productName));
					}
					productInStockService.updateTypeUserName(totalInStock);
				}catch(Exception e){
					logger.error("产品各类型人员",e.getMessage());
				}
				
				
				String platForm = psiProduct.getPlatform() + ",eu";
				for (String country : platForm.split(",")) {
					/*if ("mx".equals(country)) {
						continue;
					}*/
					PsiProductInStock inStock = new PsiProductInStock();
					inStock.setProductName(productName);
					inStock.setCountry(country);
					try{
						inStock.setMerchandiser(merchandiserMap.get(psiProduct.getName()));
						if(productManagerMap!=null&&productManagerMap.get(productType)!=null){
							inStock.setProductManager(productManagerMap.get(productType));
						}
						if(purchaseManagerMap!=null&&purchaseManagerMap.get(productType)!=null){
							inStock.setPurchaseUser(purchaseManagerMap.get(productType));
						}
						if(photoMap!=null&&photoMap.get(productType)!=null){
							inStock.setCameraman(photoMap.get(productType));
						}
						if(!"eu".equals(country)&&nameAndLineIdMap!=null&&nameAndLineIdMap.get(productName)!=null){
							String lineId=nameAndLineIdMap.get(productName);
							if(saleUserMap!=null&&saleUserMap.get(lineId)!=null&&saleUserMap.get(lineId).get(country)!=null){
								inStock.setSaleUser(saleUserMap.get(lineId).get(country).split(",")[1]);
							}
							if(customerMap!=null&&customerMap.get(lineId)!=null&&customerMap.get(lineId).get(country)!=null){
								List<PsiProductGroupCustomer> customerList=customerMap.get(lineId).get(country);
								String name="";
								StringBuffer buf= new StringBuffer();
								for (PsiProductGroupCustomer groupCustomer : customerList) {
									buf.append(groupCustomer.getName()+",");
								}
								name=buf.toString();
								inStock.setCustomer(name.substring(0, name.length()-1));
							}
						}
						productInStockService.updateTypeUserName(inStock);
					}catch(Exception e){
						logger.error("产品各类型人员",e.getMessage());
					}
				  }	
			  }
		  }	
	}
	
	public void savaProductInfo(){
		//已经处理过的淘汰总库存为0且31日销为0的产品集合
		List<String> hiddenList = psiProductService.findAllHidden();
		//待处理的淘汰总库存为0且31日销为0的产品集合
		List<String> newHiddenList = Lists.newArrayList();
		List<PsiProductInStock> stockList = Lists.newArrayList();
		List<PsiProduct> list = psiProductService.findAll();
		Map<String,String> merchandiserMap=psiProductService.findCreateUserMap();
		//产品名,在产数据String
		Map<String, PsiInventoryTotalDto> producting = psiInventoryService.getProducingQuantity();
		Map<String, PsiInventoryTotalDtoByInStock> inventorys = psiInventoryService.getInventoryQuantity2();//理诚仓为CnLc
		Map<String,Map<String, PsiInventoryTotalDto>> rs = psiInventoryService.getTransporttingQuantity();
		Map<String, PsiInventoryTotalDto> transportting = rs.get("1");
		//召回途中的数量（召回未完成的可售数）
		Map<String,Map<String, Integer>> recall = amazonRemovalOrderService.getInProcessQuantity();
		//产品名_国家
		Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();
		//产品名_国家 fba
		Map<String, PsiInventoryFba>  fbas = psiInventoryService.getAllProductFbaInfo();
		Map<String, Map<String, List<String>>> skuAndBarcode = productInStockService.getSkuAndBarcode();
		Map<String, List<Float>> priceMap = productInStockService.getProductPrice();
		
		//查询所有产品在售或淘汰,区分平台和颜色 map<产品名_颜色_国家, isSale>
		Map<String, String> produPositionMap = psiProductEliminateService.findAllProductPosition();
		Map<String, String> isNewMap = psiProductEliminateService.findIsNewMap();
		Map<String, String> productPositionMap = psiProductEliminateService.findProductPositionByCountry(null);
		List<String> isNewList = psiProductEliminateService.findIsNewProductName();
		
		List<Object[]> isNewAll = psiProductEliminateService.findIsNewProductNameWithTotalAndEu();
		Map<String, String> isSaleEuMap = psiProductEliminateService.findProductPositionByCountry(Lists.newArrayList("de","uk","fr","it","es"));
		Map<String, String> isNewEuMap = Maps.newHashMap();
		for (Object[] objs : isNewAll) {
			String name = objs[0].toString();
			if(Double.parseDouble(objs[2].toString())>0){
				isNewEuMap.put(name+"_eu", "1");
			}
		}
		
		Date today = DateUtils.getLastDayOfMonth(DateUtils.getDateStart(new Date()));
		Date start = DateUtils.addMonths(today, -1);
		Date end = DateUtils.addMonths(today, 12);
		
		//产品 [国家[月  数]]
		Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAllWithType(start,end);
		DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
		List<String> dates1 = Lists.newArrayList();
		for (int i = 1; i < 13; i++) {
			dates1.add(monthFormat.format(DateUtils.addMonths(start,i)));
		}
		
		List<PsiProductAttribute> attrList = psiProductAttributeService.findAll();
		Map<String, PsiProductAttribute> attrMap = Maps.newHashMap();
		for (PsiProductAttribute psiProductAttribute : attrList) {
			attrMap.put(psiProductAttribute.getColorName(), psiProductAttribute);
		}
		//获取产品缓冲周期([产品名_颜色, 缓冲周期])
		//Map<String, Integer> productBufferPeriod = psiProductAttributeService.findBufferPeriod();
		//获取产品缓冲周期([产品名_颜色, [国家 缓冲周期]])
		Map<String, Map<String, Integer>> productBufferPeriod = psiProductEliminateService.findBufferPeriod();
		
		//2017-02-04统计 
		//产品类型-产品经理
		Map<String,String> productManagerMap=psiProductService.getManagerByProductType();
		//产品类型-采购经理
		Map<String,String> purchaseManagerMap=psiProductService.getPurchaseByProductType();
		//产品线-国家-客服
		Map<String,Map<String,List<PsiProductGroupCustomer>>> customerMap=psiProductGroupUserService.findAllGroupCustomer();
		//产品类型-摄影
		Map<String,String> photoMap=psiProductAttributeService.getCameraman();;
		//产品名称-产品线ID
		Map<String,String> nameAndLineIdMap=psiProductTypeGroupDictService.getLineByName();
		//产品线-国家-人id+","+name
		Map<String,Map<String,String>> saleUserMap=psiProductGroupUserService.getSingleGroupUser();
		
		Float ratio=0.84f;
		if("02".equals(DateUtils.getMonth())){
			ratio=0.77f;
		}else if("03".equals(DateUtils.getMonth())){
			ratio=0.90f;
		}if("04".equals(DateUtils.getMonth())){
			ratio=0.96f;
		}if("05".equals(DateUtils.getMonth())){
			ratio=1f;
		}if("06".equals(DateUtils.getMonth())){
			ratio=0.95f;
		}if("07".equals(DateUtils.getMonth())){
			ratio=0.56f;
		}if("08".equals(DateUtils.getMonth())){
			ratio=0.62f;
		}if("09".equals(DateUtils.getMonth())){
			ratio=0.68f;
		}if("10".equals(DateUtils.getMonth())){
			ratio=0.73f;
		}if("11".equals(DateUtils.getMonth())){
			ratio=0.67f;
		}if("12".equals(DateUtils.getMonth())){
			ratio=0.63f;
		}
		Date date = new Date();
		for (PsiProduct psiProduct : list) {
			List<String> productNameWithColor = psiProduct.getProductNameWithColor();
			for (int i = 0; i < productNameWithColor.size(); i++) {
				String productName = productNameWithColor.get(i);
				if (hiddenList!= null && hiddenList.contains(productName)) {
					continue; //产品淘汰已无库存且31日销为0
				}
				PsiProductInStock totalInStock = new PsiProductInStock(); //汇总
				totalInStock.setCountry("total");
				totalInStock.setProductName(productName);
				totalInStock.setDataDate(date);
				totalInStock.setIsSale(productPositionMap.get(productName));
				totalInStock.setIsNew(isNewList.contains(productName)?"1":"0");
				
				String productType=psiProduct.getType();
				try{
					totalInStock.setMerchandiser(merchandiserMap.get(psiProduct.getName()));
					if(productManagerMap!=null&&productManagerMap.get(productType)!=null){
						totalInStock.setProductManager(productManagerMap.get(productType));
					}
					if(purchaseManagerMap!=null&&purchaseManagerMap.get(productType)!=null){
						totalInStock.setPurchaseUser(purchaseManagerMap.get(productType));
					}
					if(photoMap!=null&&photoMap.get(productName)!=null){
						totalInStock.setCameraman(photoMap.get(productName));
					}
					//productInStockService.updateTypeUserName(totalInStock);
				}catch(Exception e){
					logger.error("产品各类型人员",e.getMessage());
				}
				
				
				String platForm = psiProduct.getPlatform() + ",eu,eunouk";
				for (String country : platForm.split(",")) {
					/*if ("mx".equals(country)) {
						continue;
					}*/
					String keyStock = "";
					//eu&eunouk
					boolean noUk = "eunouk".equals(country)?true:false;
					boolean isEu = false;	//是否是欧洲
					if ("eu".equals(country) || "eunouk".equals(country)) {
						isEu = true;
					}
					if ("fr,de,uk,it,es,eu".contains(country) || isEu) {
						keyStock = "DE";
					} else if ("com,ca,mx,com2,com3".contains(country)) {
						keyStock = "US";
					} else if ("jp".equals(country)) {//日本新增海外仓
						keyStock = "JP";
					}
					String key = productName + "_" + country;
				
					Integer total = 0;//总计,需要计算
					PsiProductInStock inStock = new PsiProductInStock();
					inStock.setProductName(productName);
					try{
						inStock.setMerchandiser(merchandiserMap.get(psiProduct.getName()));
						if(productManagerMap!=null&&productManagerMap.get(productType)!=null){
							inStock.setProductManager(productManagerMap.get(productType));
						}
						if(purchaseManagerMap!=null&&purchaseManagerMap.get(productType)!=null){
							inStock.setPurchaseUser(purchaseManagerMap.get(productType));
						}
						if(photoMap!=null&&photoMap.get(productType)!=null){
							inStock.setCameraman(photoMap.get(productType));
						}
						if(!("eu".equals(country) || "eunouk".equals(country))&&nameAndLineIdMap!=null&&nameAndLineIdMap.get(productName)!=null){
							String lineId=nameAndLineIdMap.get(productName);
							if(saleUserMap!=null&&saleUserMap.get(lineId)!=null&&saleUserMap.get(lineId).get(country)!=null){
								inStock.setSaleUser(saleUserMap.get(lineId).get(country).split(",")[1]);
							}
							if(customerMap!=null&&customerMap.get(lineId)!=null&&customerMap.get(lineId).get(country)!=null){
								List<PsiProductGroupCustomer> customerList=customerMap.get(lineId).get(country);
								String name="";
								StringBuffer buf= new StringBuffer();
								for (PsiProductGroupCustomer groupCustomer : customerList) {
									buf.append(groupCustomer.getName()+",");
								}
								name=buf.toString();
								inStock.setCustomer(name.substring(0, name.length()-1));
							}
						}
						//productInStockService.updateTypeUserName(inStock);
					}catch(Exception e){
						logger.error("产品各类型人员",e.getMessage());
					}
					
					//在产
					try {
						Integer productingNum = 0;
						if (isEu) {
							try {
								productingNum += producting.get(productName).getInventorys().get("de").getQuantity();
							} catch (NullPointerException e) {}
							try {
								productingNum += producting.get(productName).getInventorys().get("fr").getQuantity();
							} catch (NullPointerException e) {}
							if (!noUk) {
								try {
									productingNum += producting.get(productName).getInventorys().get("uk").getQuantity();
								} catch (NullPointerException e) {}
							}
							try {
								productingNum += producting.get(productName).getInventorys().get("it").getQuantity();
							} catch (NullPointerException e) {}
							try {
								productingNum += producting.get(productName).getInventorys().get("es").getQuantity();
							} catch (NullPointerException e) {}
							inStock.setProducting(productingNum);
						} else {
							productingNum = producting.get(productName).getInventorys().get(country).getQuantity();
							inStock.setProducting(productingNum);
							totalInStock.setProducting(totalInStock.getProducting() + inStock.getProducting());
						}
						total += productingNum;
					} catch (NullPointerException e) {}
					//中国仓
					try {
						Integer cn = 0;
						if (isEu) {
							try {
								cn += inventorys.get(productName).getInventorys().get("de").getQuantityInventory().get("CN").getNewQuantity();
							} catch (Exception e) {}
							try {
								cn += inventorys.get(productName).getInventorys().get("fr").getQuantityInventory().get("CN").getNewQuantity();
							} catch (Exception e) {}
							if (!noUk) {
								try {
									cn += inventorys.get(productName).getInventorys().get("uk").getQuantityInventory().get("CN").getNewQuantity();
								} catch (Exception e) {}
							}
							try {
								cn += inventorys.get(productName).getInventorys().get("it").getQuantityInventory().get("CN").getNewQuantity();
							} catch (Exception e) {}
							try {
								cn += inventorys.get(productName).getInventorys().get("es").getQuantityInventory().get("CN").getNewQuantity();
							} catch (Exception e) {}
							inStock.setCn(cn);
						} else {
							cn = inventorys.get(productName).getInventorys().get(country).getQuantityInventory().get("CN").getNewQuantity();
							inStock.setCn(cn);
							totalInStock.setCn(totalInStock.getCn() + inStock.getCn());
						}
						total += cn;
					} catch (NullPointerException e) {}
					
					//中国仓(理诚)
					try {
						Integer cnLc = 0;
						if (isEu) {
							try {
								cnLc += inventorys.get(productName).getInventorys().get("de").getQuantityInventory().get("CnLc").getNewQuantity();
							} catch (Exception e) {}
							try {
								cnLc += inventorys.get(productName).getInventorys().get("fr").getQuantityInventory().get("CnLc").getNewQuantity();
							} catch (Exception e) {}
							if (!noUk) {
								try {
									cnLc += inventorys.get(productName).getInventorys().get("uk").getQuantityInventory().get("CnLc").getNewQuantity();
								} catch (Exception e) {}
							}
							try {
								cnLc += inventorys.get(productName).getInventorys().get("it").getQuantityInventory().get("CnLc").getNewQuantity();
							} catch (Exception e) {}
							try {
								cnLc += inventorys.get(productName).getInventorys().get("es").getQuantityInventory().get("CnLc").getNewQuantity();
							} catch (Exception e) {}
							inStock.setCnLc(cnLc);
						} else {
							cnLc = inventorys.get(productName).getInventorys().get(country).getQuantityInventory().get("CnLc").getNewQuantity();
							inStock.setCnLc(cnLc);
//							totalInStock.setCn(totalInStock.getCn() + inStock.getCn());
						}
//						total += cn;
					} catch (NullPointerException e) {}
					//在途
					Integer transit = 0;
					try {
						if (isEu) {
							try {
								transit += transportting.get(productName).getInventorys().get("de").getQuantity();
							} catch (Exception e) {}
							try {
								transit += transportting.get(productName).getInventorys().get("fr").getQuantity();
							} catch (Exception e) {}
							if (!noUk) {
								try {
									transit += transportting.get(productName).getInventorys().get("uk").getQuantity();
								} catch (Exception e) {}
							}
							try {
								transit += transportting.get(productName).getInventorys().get("it").getQuantity();
							} catch (Exception e) {}
							try {
								transit += transportting.get(productName).getInventorys().get("es").getQuantity();
							} catch (Exception e) {}
							inStock.setTransit(transit);
						} else {
							transit = transportting.get(productName).getInventorys().get(country).getQuantity();
							inStock.setTransit(transit);
							totalInStock.setTransit(totalInStock.getTransit() + inStock.getTransit());
						}
						total += transit;
					} catch (NullPointerException e) {}
					
					//海外仓(实)
					Integer deNew = 0;
					try {
						if (isEu) {
							try {
								deNew += inventorys.get(productName).getInventorys().get("de").getQuantityInventory().get(keyStock).getNewQuantity();
							} catch (Exception e) {}
							try {
								deNew += inventorys.get(productName).getInventorys().get("fr").getQuantityInventory().get(keyStock).getNewQuantity();
							} catch (Exception e) {}
							if (!noUk) {
								try {
									deNew += inventorys.get(productName).getInventorys().get("uk").getQuantityInventory().get(keyStock).getNewQuantity();
								} catch (Exception e) {}
							}
							try {
								deNew += inventorys.get(productName).getInventorys().get("it").getQuantityInventory().get(keyStock).getNewQuantity();
							} catch (Exception e) {}
							try {
								deNew += inventorys.get(productName).getInventorys().get("es").getQuantityInventory().get(keyStock).getNewQuantity();
							} catch (Exception e) {}
							inStock.setOverseas(deNew);
						} else {
							deNew = inventorys.get(productName).getInventorys().get(country).getQuantityInventory().get(keyStock).getNewQuantity();
							inStock.setOverseas(deNew);
							totalInStock.setOverseas(totalInStock.getOverseas() + inStock.getOverseas());
						}
						total += deNew;
					} catch (NullPointerException e) {}
					//fba仓
					int fba = 0;
					if (isEu) {
						try {
							fba += fbas.get(productName + "_de").getTotal();
						} catch (NullPointerException e) {}
						try {
							fba += fbas.get(productName + "_fr").getTotal();
						} catch (NullPointerException e) {}
						if (!noUk) {
							try {
								fba += fbas.get(productName + "_uk").getTotal();
							} catch (NullPointerException e) {}
						}
						try {
							fba += fbas.get(productName + "_it").getTotal();
						} catch (NullPointerException e) {}
						try {
							fba += fbas.get(productName + "_es").getTotal();
						} catch (NullPointerException e) {}
					} else {
						try {
							fba = fbas.get(key).getTotal();
						} catch (NullPointerException e) {}
					}
					total += fba;
					//产品总库存计算完毕
					//召回途中的(跟之前数据保持一致,召回不算在总数里面,如有需要再另行处理)
					Integer recallNum = 0;
					if (recall.get(productName)!= null) {
						if (isEu) {
							if (recall.get(productName).get("de") != null) {
								recallNum += recall.get(productName).get("de");
							}
							if (recall.get(productName).get("fr") != null) {
								recallNum += recall.get(productName).get("fr");
							}
							if (!noUk) {
								if (recall.get(productName).get("uk") != null) {
									recallNum += recall.get(productName).get("uk");
								}
							}
							if (recall.get(productName).get("it") != null) {
								recallNum += recall.get(productName).get("it");
							}
							if (recall.get(productName).get("es") != null) {
								recallNum += recall.get(productName).get("es");
							}
							inStock.setRecall(recallNum);
						} else {
							if (recall.get(productName).get(country) != null) {
								recallNum = recall.get(productName).get(country);
							}
							inStock.setRecall(recallNum);
							totalInStock.setRecall(totalInStock.getRecall() + recallNum);
						}
					}
					
					//31日销
					try {
						inStock.setDay31Sales(fancha.get(key).getDay31Sales());
						if (!isEu) {
							totalInStock.setDay31Sales(totalInStock.getDay31Sales() + inStock.getDay31Sales());
						}
					} catch (NullPointerException e) {}
					//采购期预日销
					try {
						inStock.setDaySales(fancha.get(key).getForecastPreiodAvg());
						totalInStock.setDaySales(totalInStock.getDaySales() + inStock.getDaySales());
					} catch (NullPointerException e) {}
					//销售期预月销
					try {
						inStock.setMonthSales(fancha.get(key).getForecastAfterPreiodSalesByMonth());
						totalInStock.setMonthSales(totalInStock.getMonthSales() + inStock.getMonthSales());
					} catch (NullPointerException e) {}
					//安全库存量&可销天&下单点
					double safe = 0; //安全库存量
					try {
						safe = fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33;
						if (safe > 0) {
							inStock.setSafeInventory(safe);
							try{
								if (fancha.get(key).getForecastPreiodAvg() != null && fancha.get(key).getForecastPreiodAvg() > 0) {
									inStock.setSafeDay(MathUtils.roundUp(safe/fancha.get(key).getForecastPreiodAvg()));
								}else if (fancha.get(key).getDay31Sales() != null && fancha.get(key).getDay31Sales() > 0) {
									inStock.setSafeDay(MathUtils.roundUp(safe/(fancha.get(key).getDay31Sales()/31d)));
								}
							}catch (NullPointerException e) {}
						}
					} catch (NullPointerException e) {}
					
					double point = 0;	//下单点

					int bufferPeriod = 0;	//缓冲周期
					if (productBufferPeriod.get(productName)!=null && productBufferPeriod.get(productName).get(country) != null) {
						bufferPeriod = productBufferPeriod.get(productName).get(country);	//缓冲周期
					}
					try{
						if (fancha.get(key).getForecastPreiodAvg() != null && fancha.get(key).getForecastPreiodAvg() > 0) {
							point = fancha.get(key).getForecastPreiodAvg() * (fancha.get(key).getPeriod() + bufferPeriod) + safe;
							inStock.setOrderPoint(point);
						}else if (fancha.get(key).getDay31Sales() != null && fancha.get(key).getDay31Sales() > 0) {
							point = (fancha.get(key).getDay31Sales()/31d) * (fancha.get(key).getPeriod() + bufferPeriod) + safe;
							inStock.setOrderPoint(point);
						}
					}catch (Exception e) {}
					//结余
					double balance = total - point;
					if (balance != 0) {
						inStock.setBalance(balance);
					}
					try {
						if (fancha.get(key).getForecastAfterPreiodSalesByMonth() != null && fancha.get(key).getForecastAfterPreiodSalesByMonth() > 0) {
							inStock.setInventoryDay(balance/(fancha.get(key).getForecastAfterPreiodSalesByMonth()/31));
						}else if (fancha.get(key).getDay31Sales() != null && fancha.get(key).getDay31Sales() > 0) {
							inStock.setInventoryDay(balance/((double)fancha.get(key).getDay31Sales()/31));
						}
					} catch (NullPointerException e) {}
					//下单量
					if (balance < 0) {
						inStock.setOrderQuantity(MathUtils.roundUp((Math.abs(balance))/psiProduct.getPackQuantity())*psiProduct.getPackQuantity());
					}
					//空运补货量
					double sky = 0;
					try {
						if (fancha.get(key).getForecastPreiodAvg()!= null && fancha.get(key).getForecastPreiodAvg() > 0) {
							sky = (fancha.get(key).getPeriod() - psiProduct.getProducePeriod())*MathUtils.roundUp(fancha.get(key).getForecastPreiodAvg()) - 
								fbas.get(key).getTotal() - deNew - transit;
						}
					} catch (NullPointerException e) {}
					try {
						if(sky==0 && fancha.get(key).getDay31Sales()!= null && fancha.get(key).getDay31Sales() > 0){
							sky = (fancha.get(key).getPeriod() - psiProduct.getProducePeriod())*MathUtils.roundUp((fancha.get(key).getDay31Sales()/31d)) - 
								fbas.get(key).getTotal() - deNew - transit;
						}
					} catch (NullPointerException e) {}
					if (sky>0) {
						inStock.setAirReplenishment(MathUtils.roundUp(sky/psiProduct.getPackQuantity()) * psiProduct.getPackQuantity());
					}
					try {
						List<String> skuAndBarcodeList = skuAndBarcode.get(productName).get(country);
						inStock.setSku(skuAndBarcodeList.get(0));
						inStock.setBarcode(skuAndBarcodeList.get(1));
						String userId = skuAndBarcodeList.get(2);
						if (userId != null) {
							inStock.setLastUpdateBy(new User(userId));
						}
					} catch (NullPointerException e) {}
					inStock.setPeriod(fancha.get(key) != null ?fancha.get(key).getPeriod():null);
					inStock.setCountry(country);
					inStock.setDataDate(date);
					
					List<Float> priceList = priceMap.get(productName);
					if (priceList != null && priceList.size() > 0) {
						inStock.setPrice(priceList.get(0));//美金价格
						inStock.setRmbPrice(priceList.get(1));//人民币价格
					}
					
					inStock.setTotalStock(total);
					if (!isEu) {
						totalInStock.setTotalStock(totalInStock.getTotalStock() + inStock.getTotalStock());
						totalInStock.setOrderPoint(totalInStock.getOrderPoint() + inStock.getOrderPoint());
						totalInStock.setBalance(totalInStock.getBalance() + inStock.getBalance());
						totalInStock.setOrderQuantity(totalInStock.getOrderQuantity() + inStock.getOrderQuantity());
						totalInStock.setSafeInventory(totalInStock.getSafeInventory() + inStock.getSafeInventory());
					}
					if (isEu) {
						inStock.setIsSale(isSaleEuMap.get(productName));
						inStock.setIsNew("1".equals(isNewEuMap.get(productName + "_eu"))?"1":"0");
					} else {
						inStock.setIsSale(produPositionMap.get(key));
						inStock.setIsNew(isNewMap.get(key));
					}
					//计算库销比(库存预测可销月数),无预算按31日销计算
					double inventorySaleMonth = 0;
					int inventory = inStock.getTotalStock();
					if (inventory > 0) {
						try {
							for (int j = 0; j < dates1.size(); j++) {
								String month = dates1.get(j);
								//产品 [国家[月  数]]
								int forecast = 0;
								if (isEu && noUk) {
									forecast = data.get(productName).get("eu").get(month).getQuantityForecast();
								} else {
									forecast = data.get(productName).get(country).get(month).getQuantityForecast();
								}
								if (isEu && !noUk) {	//预测统计时欧洲平台没有统计UK
									try {	//泛欧产品UK每月预测数据,会空指针
										forecast += data.get(productName).get("uk").get(month).getQuantityForecast();
									} catch (NullPointerException e) {}
								}
								if (inventory >= forecast) {
									inventorySaleMonth += 1;
									inventory = inventory - forecast;
								} else {
									inventorySaleMonth = inventorySaleMonth + inventory/(double)forecast;
									inventory = 0;
									break;
								}
							}
						} catch (NullPointerException e) {}
					}
					inStock.setInventorySaleMonth(inventorySaleMonth);
					
					//365/(生产运输期加缓冲期加安全库存天数)   欧洲的取整个平台的安全库存天数     淘汰品不算  新品算上架2个月以上的     加拿和US的标准一样  JP的和EU的标准一样  
					if("eu".equals(country)||"com".equals(country)||"eunouk".equals(country)){
						try{
							Integer temp=((inStock.getPeriod()==null?0:inStock.getPeriod())+(inStock.getSafeDay()==null?0:inStock.getSafeDay()));
							if(temp>0){//ratio*365/(fns:roundUp(safeDay)+period)/12
								Float turnoverStandard=ratio*365f/temp/12;
								inStock.setTurnoverStandard(turnoverStandard);
							}
							
						}catch(Exception e){}
					}
					stockList.add(inStock);
				}
				if (totalInStock.getSafeInventory() > 0 && totalInStock.getDaySales() > 0) {
					totalInStock.setSafeDay(MathUtils.roundDown(totalInStock.getSafeInventory()/totalInStock.getDaySales()));
				}
				//计算库销比(库存预测可销月数),无预算数据按31日销计算
				double inventorySaleMonth = 0;
				int inventory = totalInStock.getTotalStock();
				if (inventory > 0) {
					try {
						for (int j = 0; j < dates1.size(); j++) {
							String month = dates1.get(j);
							//产品 [国家[月  数]]
							int forecast = data.get(productName).get("total").get(month).getQuantityForecast();
							if (inventory >= forecast) {
								inventorySaleMonth += 1;
								inventory = inventory - forecast;
							} else {
								inventorySaleMonth = inventorySaleMonth + inventory/(double)forecast;
								inventory = 0;
								break;
							}
						}
					} catch (NullPointerException e) {}
				}
				PsiProductAttribute attr = attrMap.get(productName);
				if (attr != null) {
					//淘汰并总库存为0,算下架
					if (attr.getUnshelveDate()==null && totalInStock.getTotalStock()==0 && "0".equals(totalInStock.getIsSale())) {
						attr.setUnshelveDate(new Date());
					}
					attr.setInventorySaleMonth(inventorySaleMonth);
				}
				//淘汰且总库存为0&31日销为0
				if (totalInStock.getTotalStock()==0 && "4".equals(totalInStock.getIsSale()) 
						&& totalInStock.getDay31Sales()==0) {
					newHiddenList.add(productName);
				}
				
				List<Float> priceList = priceMap.get(productName);
				if (priceList != null && priceList.size() > 0) {
					totalInStock.setPrice(priceList.get(0));//美金价格
					totalInStock.setRmbPrice(priceList.get(1));//人民币价格
				}
				totalInStock.setInventorySaleMonth(inventorySaleMonth);
				stockList.add(totalInStock);
			}
		}
		productInStockService.save(stockList);
		psiProductAttributeService.save(attrList);
		//标记已淘汰且总库存为0的产品(31日销也为0)
		try {
			logger.info("处理淘汰总库存为0且31日销为0产品" + newHiddenList);
			psiProductService.saveHidden(newHiddenList);
		} catch (Exception e) {
			logger.error("处理淘汰且总库存为0产品时异常", e);
		}
	}
	
	public static Map<String, Map<String, String>> getAmazonAttrFromDb(){
		AmazonProduct2Service amazonProduct2Service;
		try {
			amazonProduct2Service = SpringContextHolder.getBean(AmazonProduct2Service.class);
		} catch (Exception e) {
			return null;
		}
		return amazonProduct2Service.initAmazonAttr();
	}
	
	public  void  updateAttributePrice(){
		Map<String,Map<String,Object>> beforeMap=psiProductAttributeService.findProductPrice();
		Map<String,Map<String,Object>>  curPriceMap=psiProductTieredPriceService.getMoqPriceBaseMoqNoSupplier();
		Map<String,Map<String,Float>> typePriceMap=psiProductTieredPriceService.getPriceBaseMoqNoSupplier();
		Map<String,List<Object[]>> allTypeMap=psiProductTieredPriceService.getPriceBaseMoqNoSupplier2();
		
		Date today = new Date();
		for (Map.Entry<String, Map<String, Object>>  entry : beforeMap.entrySet()) {  
		    String productName=entry.getKey();
		    Map<String, Object> nameMap=entry.getValue();
			if(nameMap!=null&&nameMap.get("price")!=null
					  &&curPriceMap.get(productName)!=null&&curPriceMap.get(productName).get("price")!=null
					  &&(Float)nameMap.get("price")!=((BigDecimal)curPriceMap.get(productName).get("price")).floatValue()){
						Float beforePrice=(Float)nameMap.get("price");
						//Float afterPrice=((BigDecimal)curPriceMap.get(productName).get("price")).floatValue();
						//Date sureDate=psiLadingBillService.isExistLadingBill(productName,afterPrice);
						Date sureDate=null;
					/*	Map<String,Float> typeMap=typePriceMap.get(productName);
						if(typeMap==null){
							continue;
						}
						*/
						List<Object[]> tempList=allTypeMap.get(productName);
						
						Float latestPrice=null;
						String unit="";
						if(tempList!=null&&tempList.size()>0){
							 for (Object[] obj: tempList) {
								 String productColor=obj[0].toString();
								 String currency = obj[2].toString();
								 Float price =Float.parseFloat(obj[1].toString());
								 Date tempDate=psiLadingBillService.isExistLadingBill(productColor,price,currency);
								 if(tempDate!=null){
									 if(sureDate==null||tempDate.after(sureDate)){
										 sureDate=tempDate;
										 latestPrice=price;
										 unit=currency;
									 }
								 }
								// break;
							}
						}
					
                      if(sureDate==null){//还未用最新价格收过货,用之前价格
                      	   continue;
                      }
                      float tempPirce=latestPrice;
                      if("CNY".equals(unit)){
                    	  tempPirce=latestPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY");
                      }
                      if((beforePrice>tempPirce&&today.after(DateUtils.addDays(sureDate,70)))||beforePrice<tempPirce){
                    	  psiProductAttributeService.update(latestPrice,unit,productName);
                      }
                     /* if((beforePrice>afterPrice&&today.after(DateUtils.addDays(sureDate,70)))||beforePrice<afterPrice){//降价
                    	  for (Map.Entry<String,Float> entryType : typeMap.entrySet()) { 
                    		        String type=entryType.getKey();
      								if(entry.getValue()!=null){
      										psiProductAttributeService.update(entryType.getValue(),type,productName);
	        								break;
      								}
							}
                       }  */
					}else{
						try{
							if((beforeMap.get(productName)==null)||(beforeMap.get(productName)!=null&&beforeMap.get(productName).get("price")==null)){
								Map<String,Float> typeMap=typePriceMap.get(productName);
								if(typeMap!=null&&typeMap.size()>0){
									for (Map.Entry<String,Float> entryType : typeMap.entrySet()) { 
									    String type=entryType.getKey();
										if(entry.getValue()!=null){
											logger.info("更新"+productName+"价格");
											psiProductAttributeService.update(entryType.getValue(),type,productName);
  										    break;
										}
									}
								}
							}
						}catch(Exception e){
							logger.error("更新价格异常",e);
						}
				}
		}
	}
	
	public void returnRate() {
		try {
			Calendar cal = Calendar.getInstance();
			int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
			if (dayOfMonth == 1) {	//每月1号初始化本月产品类型与产品线对应关系
				try {
					psiProductTypeGroupDictService.saveTypeLine(null);
					WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "每月1号初始化本月产品类型与产品线对应关系完毕！！");
				} catch (Exception e) {
					logger.error("每月1号初始化本月产品类型与产品线对应关系发生异常！！", e);
					WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "每月1号初始化本月产品类型与产品线对应关系发生异常！！");
				}
				
				try{
					logger.info("统计pi价格start！");
					SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM");
					String date=dateFormat.format(DateUtils.addMonths(new Date(),-1));
					Map<String,Map<String,Float>> map=psiProductEliminateService.findAmazonPiPrice();
					Map<String,Map<String,Float>> newMap=psiProductEliminateService.findModelPiPrice();
					for (Map.Entry<String,Map<String,Float>> countryMap: map.entrySet()) {
						 String country=countryMap.getKey();
						 for (Map.Entry<String,Float> temp: countryMap.getValue().entrySet()) {
							 String model=temp.getKey();
							 Float price=temp.getValue();
							 if(newMap.get(country)!=null&&newMap.get(country).get(model)!=null&&newMap.get(country).get(model)!=price){
								 psiProductEliminateService.savePiPrice(country,model,date,newMap.get(country).get(model));
							 }else{
								 psiProductEliminateService.savePiPrice(country,model,date,price);
							 }
						}
					}
					List<String> countryList=Lists.newArrayList("jp","com","de");
					for (String country: countryList) {
						Map<String,Float> temp=newMap.get(country);
						for (Map.Entry<String,Float> modelTemp: temp.entrySet()) {
							 String model=modelTemp.getKey();
							 if(map.get(country)==null||map.get(country).get(model)==null){
								 psiProductEliminateService.savePiPrice(country,model,date,modelTemp.getValue());
							 }
						}
					}
					logger.info("统计pi价格end！");
				}catch(Exception e){
					logger.info("统计pi价格异常",e.getMessage());
				}
			}
			if (dayOfMonth == 5) {	//每月5号统计退货率情况，前两个月进行比较
				logger.info("开始统计退货率情况...");
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
				Date date = new Date();
				Date date1 = DateUtils.addMonths(date , -1);
				Date date2 = DateUtils.addMonths(date , -2);
				String month1 = monthFormat.format(date1);
				String month2 = monthFormat.format(date2);
				String start1 = format.format(DateUtils.getFirstDayOfMonth(date1));
				String end1 = format.format(DateUtils.getLastDayOfMonth(date1));
				String start2 = format.format(DateUtils.getFirstDayOfMonth(date2));
				String end2 = format.format(DateUtils.getLastDayOfMonth(date2));
				//上个月退货情况
				Map<String, Map<String, Integer>> sales1 = returnGoodsService.getSalesVolume(start1, end1);
				Map<String, Map<String, Map<String, Integer>>> returnGoods1 = returnGoodsService.getRetrunGoods(start1, end1);
				Map<String, Map<String, Map<String, Float>>> returnRate1 = returnGoodsService.getRetrunGoodsRate(returnGoods1, sales1);
				//上两个月退货情况
				Map<String, Map<String, Integer>> sales2 = returnGoodsService.getSalesVolume(start2, end2);
				Map<String, Map<String, Map<String, Integer>>> returnGoods2 = returnGoodsService.getRetrunGoods(start2, end2);
				Map<String, Map<String, Map<String, Float>>> returnRate2 = returnGoodsService.getRetrunGoodsRate(returnGoods2, sales2);
				StringBuffer contents= new StringBuffer("");
		    	StringBuffer contents1= new StringBuffer("");
		    	//按退货率排序
		    	Map<String, List<Entry<String, Float>>> returnRankMap = Maps.newHashMap();
		    	for (Map.Entry<String, Map<String, Map<String, Float>>> entryReturnRate: returnRate1.entrySet()) {  
				    String country =entryReturnRate.getKey();
					Map<String, Map<String, Float>> countryMap = entryReturnRate.getValue();
					Map<String, Float> productRankMap = Maps.newTreeMap();//国家退货率排名前三的产品
					
					for (Map.Entry<String, Map<String, Float>> entryCountry: countryMap.entrySet()) {  
					    String productName=entryCountry.getKey();
						Float rate = entryCountry.getValue().get("total");
						if (rate != null) {
							productRankMap.put(productName, rate);
						}
					}
					List<Entry<String, Float>> list_Data = new ArrayList<Entry<String, Float>>(productRankMap.entrySet());
					Collections.sort(list_Data, new Comparator<Map.Entry<String, Float>>() {
						@Override
						public int compare(Entry<String, Float> o1, Entry<String, Float> o2) {
							if (o1.getValue() < o2.getValue()) {
								return 1;
							} else {
								return -1;
							}
						}
					});
					returnRankMap.put(country, list_Data);
				}
				for (Map.Entry<String,List<Map.Entry<String,Float>>> entryReturn: returnRankMap.entrySet()) { 
				    String country=entryReturn.getKey();
					List<Entry<String, Float>> countryList = entryReturn.getValue();
					String countryStr = SystemService.countryNameMap.get(country);
			    	String title = countryStr+"月度退货率情况明细";
					contents1.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
					contents1.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='7'><span style='font-weight: bold;font-size:25px'>"+title+"</span></td></tr>");
					contents1.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>国家</th><th>产品</th><th>"+month1+"销量</th><th>"+month1+"退货率</th><th>"+month2+"销量</th><th>"+month2+"退货率</th><th>退货率浮动</th></tr>");
					for (Entry<String, Float> entry : countryList) {
						String productName = entry.getKey();
						Float rate1 = entry.getValue();
						if (rate1 == null) {
							continue;
						}
						Float rate2 = 0f;
						try {
							rate2 = returnRate2.get(country).get(productName).get("total");
							if (rate2 == null) {
								rate2 = 0f;
							}
						} catch (NullPointerException e) {}
						Float rateCha = rate1 - rate2;
						Integer saleVolume2 = 0;
						try {
							saleVolume2 = sales2.get(country).get(productName);
						} catch (NullPointerException e) {}
						if (saleVolume2 == null) {
							saleVolume2 = 0;
						}
			    		contents1.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
			    		contents1.append("<td>"+(countryStr)+"</td><td>"+productName+"</td>");
			    		contents1.append("<td>"+(sales1.get(country).get(productName))+"</td>");
			    		contents1.append("<td>"+(String.format("%.2f", rate1*100f))+"%</td>");
			    		contents1.append("<td>"+(saleVolume2)+"</td>");
			    		contents1.append("<td>"+(String.format("%.2f", rate2*100f))+"%</td>");
    					contents1.append("<td style=\"color:"+(Math.abs(rateCha)>0.03?"red":"green")+"\">"+(String.format("%.2f", rateCha*100f))+"%</td>");
			    		contents1.append("</tr>");
					}
					contents1.append("</table><br/><br/>");
				}
		    	if (StringUtils.isNotEmpty(contents1)) {
		        	contents.append("<p><span style='font-size:20px'>Hi all,<br/>&nbsp;&nbsp;&nbsp;&nbsp;前两月退货率明细如下,请知悉。</span></p>");
		        	contents.append(contents1);
		        	logger.info("退货率邮件内容："+ contents.toString());
		        	String toAddress ="amazon-sales@inateck.com";
					final MailInfo mailInfo = new MailInfo(toAddress, "月度退货率明细" + DateUtils.getDate("-yyyy/M/dd"), new Date());
					mailInfo.setContent(contents.toString());
					mailInfo.setCcToAddress("erp_development@inateck.com");
					new Thread() {
						public void run() {
							mailManager.send(mailInfo);
						}
					}.start();
		    	}
				WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "每月5号统计退货率情况完毕！！");
			}
		} catch (Exception e) {
			WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "每月5号统计退货率情况发生异常！！");
			logger.error("每月5号统计退货率情况发生异常！！", e);
		}
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		//AmazonProduct2Service  a= applicationContext.getBean(AmazonProduct2Service.class);
		//ProductPriceService  b= applicationContext.getBean(ProductPriceService.class);
		//SaleReportService  c= applicationContext.getBean(SaleReportService.class);
		/*AmazonOutOfProductService  s= applicationContext.getBean(AmazonOutOfProductService.class);
		for (int i = 1; i < 365; i++) {
			if((DateUtils.addDays(new Date(), 0-i).getYear()+1900)==2016){
				break;
			}
			s.createOutOfData(i);
		}*/
		
		
		
		
		
		/*SaveHistoryInfoMonitor d = new SaveHistoryInfoMonitor() ;
		d.setProductPriceService(b);
		//d.setPsiProductService(a);
		d.setSaleReportService(c);
		d.saveHistoryInfo();*/
		//a.updateAndSaveCostPrice();
		/*Date today = DateUtils.addDays(new Date(),-1);
		today.setHours(0);
		today.setMinutes(0);
		today.setSeconds(0);
		a.countCostPrice(today);*/
		
		//b.updatePanEu();
		
		/*AmazonProduct2Service  b= applicationContext.getBean(AmazonProduct2Service.class);
		b.updateAndSaveCostPrice();*/
		applicationContext.close();
	}

	public PsiProductService getPsiProductService() {
		return psiProductService;
	}

	public void setPsiProductService(PsiProductService psiProductService) {
		this.psiProductService = psiProductService;
	}

	public ProductPriceService getProductPriceService() {
		return productPriceService;
	}

	public void setProductPriceService(ProductPriceService productPriceService) {
		this.productPriceService = productPriceService;
	}

	public SaleReportService getSaleReportService() {
		return saleReportService;
	}

	public void setSaleReportService(SaleReportService saleReportService) {
		this.saleReportService = saleReportService;
	}
}
