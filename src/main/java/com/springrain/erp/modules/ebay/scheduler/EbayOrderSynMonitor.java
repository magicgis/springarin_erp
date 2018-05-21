package com.springrain.erp.modules.ebay.scheduler;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.ApiException;
import com.ebay.sdk.ApiLogging;
import com.ebay.sdk.SdkException;
import com.ebay.sdk.call.GetOrdersCall;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.OrderStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.PaginationType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import com.ebay.soap.eBLBaseComponents.TradingRoleCodeType;
import com.ebay.soap.eBLBaseComponents.WarningLevelCodeType;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Maps;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrder;
import com.springrain.erp.modules.amazoninfo.service.AwsAdverstingService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonUnlineOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.MfnOrderService;
import com.springrain.erp.modules.ebay.entity.EbayAddress;
import com.springrain.erp.modules.ebay.entity.EbayOrder;
import com.springrain.erp.modules.ebay.entity.EbayOrderItem;
import com.springrain.erp.modules.ebay.service.EbayOrderService;
import com.springrain.erp.modules.psi.entity.PsiBarcode;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.User;

public class EbayOrderSynMonitor implements EbayConstants {

	@Autowired
	private EbayOrderService ebayOrderService;
	@Autowired
	private MfnOrderService mfnOrderService;
	@Autowired
	private AmazonUnlineOrderService amazonUnlineOrderService;
	@Autowired
	private PsiProductService 		productService;
	@Autowired  
	private AwsAdverstingService   adService; 
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 下载ebay订单
	 */
	public void execute() {
		logger.info("开始下载德国ebay订单。");
		logger.info("获取ebayapi --ApiContext");
		Map<String,String> modelBrandMap = adService.findModelBrandMap();
		
		ApiContext apiContext = new ApiContext();
 		ApiCredential cred = apiContext.getApiCredential();
 		cred.seteBayToken(EbayConstants.EBAYTOKEN);
 		ApiAccount account = cred.getApiAccount();
 		account.setDeveloper(EbayConstants.DEVID);
 		account.setApplication(EbayConstants.APPID);
 		account.setCertificate(EbayConstants.CERTID);
 		apiContext.setApiServerUrl(EbayConstants.APISERVERURL);
 		apiContext.setSite(SiteCodeType.GERMANY);

 		ApiLogging apiLog = apiContext.getApiLogging();
 		apiLog.setLogSOAPMessages(false);
 		apiLog.setLogHTTPHeaders(false);
 		apiLog.setLogExceptions(false);
 		apiContext.setApiLogging(apiLog);
		
		logger.info("订单下载call	--GetOrdersCall");
		GetOrdersCall call = new GetOrdersCall(apiContext);
		call.setWarningLevel(WarningLevelCodeType.HIGH);
		call.setOrderRole(TradingRoleCodeType.SELLER);
		call.setOrderStatus(OrderStatusCodeType.ALL);
		call.setEnableCompression(false);
		Timestamp time = ebayOrderService.getLastUpdateTime("de");
		Calendar calTo = Calendar.getInstance();
		Calendar calFrom = Calendar.getInstance();
		calFrom.add(Calendar.HOUR, -5);
		calFrom.set(time.getYear()+1900, time.getMonth(), time.getDate(),0,0,0);
		call.setModTimeFrom(calFrom);
		call.setModTimeTo(calTo);
		logger.info("下载变更时间为" + calFrom.getTime() + "至" + calTo.getTime()
				+ "之内的订单。");
		DetailLevelCodeType[] detailLevels = new DetailLevelCodeType[] { DetailLevelCodeType.RETURN_ALL };
		call.setDetailLevel(detailLevels);
		//分页下载，一页100条
		PaginationType pagination = new PaginationType();
		pagination.setEntriesPerPage(100);
		call.setPagination(pagination);
		int pageIndex = 1;
		int pageCount = 0;
		do {
			pagination.setPageNumber(pageIndex);
			OrderType[] orders = getOrders(call);
			if (orders != null && orders.length > 0) {
				addEbayOrders(orders,"de", modelBrandMap );
				pageCount = call.getReturnedPaginationResult()
						.getTotalNumberOfPages();
			}else{
				break;
			}
			pageIndex++;
		} while (pageIndex <= pageCount);
		if(call.getReturnedPaginationResult()!=null){
			logger.info("ebay订单下载结束，共下载"
					+ call.getReturnedPaginationResult().getTotalNumberOfEntries()
					+ "个订单。");
		}else{
			logger.warn("ebay订单下载不成功!!!");
		}
	
		
		logger.info("开始下载美国ebay订单..");
 		cred.seteBayToken(EbayConstants.EBAYTOKEN_US);
 		account.setDeveloper(EbayConstants.DEVID_US);
 		account.setApplication(EbayConstants.APPID_US);
 		account.setCertificate(EbayConstants.CERTID_US);
 		apiContext.setSite(SiteCodeType.US);
		
		logger.info("订单下载call	--GetOrdersCall");
		call = new GetOrdersCall(apiContext);
		call.setWarningLevel(WarningLevelCodeType.HIGH);
		call.setOrderRole(TradingRoleCodeType.SELLER);
		call.setOrderStatus(OrderStatusCodeType.ALL);
		call.setEnableCompression(false);
		time = ebayOrderService.getLastUpdateTime("com");
		calTo = Calendar.getInstance();
		calFrom = Calendar.getInstance();
		calFrom.add(Calendar.HOUR, -5);
		calFrom.set(time.getYear()+1900, time.getMonth(), time.getDate(),0,0,0);
		call.setModTimeFrom(calFrom);
		call.setModTimeTo(calTo);
		logger.info("下载变更时间为" + calFrom.getTime() + "至" + calTo.getTime()
				+ "之内的订单。");
		detailLevels = new DetailLevelCodeType[] { DetailLevelCodeType.RETURN_ALL };
		call.setDetailLevel(detailLevels);
		//分页下载，一页100条
		pagination = new PaginationType();
		pagination.setEntriesPerPage(100);
		call.setPagination(pagination);
		pageIndex = 1;
		pageCount = 0;
		do {
			pagination.setPageNumber(pageIndex);
			OrderType[] orders = getOrders(call);
			if (orders != null && orders.length > 0) {
				addEbayOrders(orders,"com",modelBrandMap );
				pageCount = call.getReturnedPaginationResult()
						.getTotalNumberOfPages();
			}else{
				break;
			}
			pageIndex++;
		} while (pageIndex <= pageCount);
		if(call.getReturnedPaginationResult()!=null){
			logger.info("美国ebay订单下载结束，共下载"
					+ call.getReturnedPaginationResult().getTotalNumberOfEntries()
					+ "个订单。");
		}else{
			logger.warn("美国ebay订单下载不成功!!!");
		}
		
	
		try{
			logger.info("自发货Ebay订单..");
			mfnOrderService.updateAndSaveEbay();
			logger.info("自发货Ebay订单end..");
		}catch(Exception e){
			logger.info("自发货ebay",e.getMessage());
		}
		
		
		try{
			logger.info("自发check24订单..");
			mfnOrderService.updateAndSaveCheck24();
			logger.info("自发check24订单end..");
		}catch(Exception e){
			logger.info("check24",e.getMessage());
		}
		
		//统计线下订单自发货
		try{
				List<AmazonUnlineOrder> orderList=amazonUnlineOrderService.findWaitSysnOrder();
				if(orderList!=null&&orderList.size()>0){
					for (AmazonUnlineOrder amazonUnlineOrder : orderList) {
						boolean flag=mfnOrderService.synchronizeOrder(amazonUnlineOrder);
						if(!flag){
							logger.info("线下订单同步"+amazonUnlineOrder.getAmazonOrderId());
						}
					}
				}
		}catch(Exception e){
					logger.error("统计线下订单自发货异常",e.getMessage());
		}
	}
	
	private static HtmlPage getPage(WebClient client,String url,int num){
		if(num>5){
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
	
	
	private OrderType[] getOrders(GetOrdersCall call) {
		return getOrders(call,0);
	}
	
	private OrderType[] getOrders(GetOrdersCall call,int num) {
		if(num>3){
			return null;
		}
		try {
			logger.info("开始调用getOrders方法！");
			OrderType[] orders = call.getOrders();
			return orders;
		} catch (ApiException e) {
			logger.warn("获取ebay订单api出错："+e.getMessage(),e);
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e1) {}
			num++;
			return getOrders(call,num);
		} catch (SdkException e) {
			e.printStackTrace();
			logger.warn("获取ebay订单sdk出错："+e.getMessage(),e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("获取ebay订单出错："+e.getMessage(),e);
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e1) {}
			num++;
			return getOrders(call,num);
		}
		return null;
	}

	/**
	 * @param orders
	 */
	private void addEbayOrders(OrderType[] orders,String country,Map<String,String> modelBrandMap ) {
		
		if (orders != null && orders.length > 0) {
			for (OrderType orderType : orders) {
				String orderId = orderType.getOrderID();
				//判断订单是否已经存在，存在修改，不存在增加
				if (ebayOrderService.isNotExist(orderId,country)) {
					EbayOrder order = new EbayOrder(orderType,country);
					order.setCountry(country);
					try {
						ebayOrderService.addOrUpdate(order);
						matchEbaySku(order,modelBrandMap);
					}catch (Exception e) {
						logger.error(orderId+"存储ebay订单时出错!",e);
					}
					
				} else {
					EbayOrder order = ebayOrderService.getOrder(orderId,country);
					order = convert(orderType,order,country);
					try {
						ebayOrderService.addOrUpdate(order);
						matchEbaySku(order,modelBrandMap);
					} catch (Exception e) {
						logger.error(orderId+"存储ebay订单时出错!",e);
					}
				}
			}
		}
	}
	
	
	public boolean isNumeric(String s) {
	    if (s != null && !"".equals(s.trim()))
	        return s.matches("^[0-9]*$");
	    else
	        return false;
	}   
	
	/*如：LG1003_white表示：一条LG1003_white白色数据线
	LG1003_white_q2 表示：两条LG1003_white白色数据线
	依次类推
	二手货SKU即为：LG1003_white_used
	*/
	public void  matchEbaySku(EbayOrder order,Map<String,String> modelBrandMap){
		for (EbayOrderItem item : order.getItems()) {
			PsiSku sku = productService.getSkuBySku(item.getSku());
			if(sku==null){
				 String model="";
				 String name="";
				 String color="";
				 String tempSku= item.getSku().replace("_used","").replace("_nearly new","").replace("_Nearly New","").replace("_Defective","").replace("_defective","").replace("_Unsellable","").replace("_unsellable","");
				 if(tempSku.contains("_")){
					 String qty = tempSku.substring(tempSku.lastIndexOf("_")+1);
					 if(qty.startsWith("q")&&isNumeric(qty.replace("q",""))){
						 tempSku = tempSku.substring(0,tempSku.lastIndexOf("_"));
					 }
					 model = tempSku.substring(0,tempSku.lastIndexOf("_"));
					 color = tempSku.substring(tempSku.lastIndexOf("_")+1);
				 }else{
					 model =  tempSku; 
				 }
				 if(modelBrandMap.get(model)!=null){
					     name = modelBrandMap.get(model)+" "+model;
						 PsiBarcode barcode = productService.getBarcodeByProCouCol(name,order.getCountry(),color,"");
						 if(barcode!=null){
							 PsiSku  psisku =new PsiSku();
							 psisku.setUseBarcode("0");
							 psisku.setUpdateUser(new User("1"));
							
							 psisku.setBarcode(barcode);
							 psisku.setColor(color);
							 if("de".equals(order.getCountry())){
								 psisku.setCountry("ebay");
							 }else{
								 psisku.setCountry("ebay_com");
							 }
							 psisku.setSku(item.getSku());
							 psisku.setAccountName("");
							 psisku.setProductName(name);
							 psisku.setProductId(barcode.getPsiProduct().getId());
							 productService.save(psisku);
						 }
				 }
			}
		}
		
	}
	
	/**
	 * @param orderType
	 * @param order
	 * @return
	 * 将从ebay下载的订单值赋给数据库查出来的对象
	 */
	private EbayOrder convert(OrderType orderType,EbayOrder order,String country) {
		EbayOrder order2 = new EbayOrder(orderType,country);
		order.setOrderStatus(order2.getOrderStatus());
		order.setCheckoutStatus(order2.getCheckoutStatus());
		order.setCreatedTime(order2.getCreatedTime());
		order.setPaymentMethods(order2.getPaymentMethods());
		order.setTotal(order2.getTotal());
		order.setBuyerUserId(order2.getBuyerUserId());
		order.setPaidTime(order2.getPaidTime());
		order.setShippedTime(order2.getShippedTime());
		order.setStatus(order2.getStatus());
		order.setLastModifiedTime(order2.getLastModifiedTime());
		order.setShippingServiceCost(order2.getShippingServiceCost());
		order.setAdjustmentAmount(order2.getAdjustmentAmount());
		order.setAmountPaid(order2.getAmountPaid());
		order.setAmountSaved(order2.getAmountSaved());
		order.setPaymentStatus(order2.getPaymentStatus());
		order.setSubtotal(order2.getSubtotal());
		order.setShippinginsuranceCost(order2.getShippinginsuranceCost());
		order.setShippingService(order2.getShippingService());
		order.setEiasToken(order2.getEiasToken());
		order.setExternaltransactionId(order2.getExternaltransactionId());
		order.setExternaltransactionTime(order2.getExternaltransactionTime());
		order.setPaymentorrefundAmount(order2.getPaymentorrefundAmount());
		order.setFeeorcreditAmount(order2.getFeeorcreditAmount());

		EbayAddress address = order.getShippingAddress();
		EbayAddress address2 = order2.getShippingAddress();
		address.setName(address2.getName());
		address.setStreet(address2.getStreet());
		address.setStreet1(address2.getStreet1());
		address.setStreet2(address2.getStreet2());
		address.setCityName(address2.getCityName());
		address.setCounty(address2.getCounty());
		address.setStateOrProvince(address2.getStateOrProvince());
		address.setCountryCode(address2.getCountryCode());
		address.setPostalCode(address2.getPostalCode());
		address.setPhone(address2.getPhone());

		List<EbayOrderItem> items = order.getItems();
		List<EbayOrderItem> items2 = order2.getItems();
		Map<String, EbayOrderItem> itemsMap = Maps.newHashMap();
		Map<String, EbayOrderItem> items2Map = Maps.newHashMap();
		for (EbayOrderItem item : items2) {
			String key  = item.getSku()+"#"+item.getSellingmanagersalesrecordNumber();
			items2Map.put(key, item);
		}
		for (Iterator<EbayOrderItem> iterator = items.iterator(); iterator.hasNext();) {
			EbayOrderItem oldItem = iterator.next();
			String key  = oldItem.getSku()+"#"+oldItem.getSellingmanagersalesrecordNumber();
			if(items2Map.get(key)==null){
				iterator.remove();
			}else{
				itemsMap.put(key, oldItem);
			}
		}
		
		for (EbayOrderItem newItem : items2) {
			String key  = newItem.getSku()+"#"+newItem.getSellingmanagersalesrecordNumber();
			EbayOrderItem oldItem = itemsMap.get(key);
			if(oldItem==null){
				newItem.setOrder(order);
				items.add(newItem);
			}else{
				oldItem.setSku(newItem.getSku());
				oldItem.setTitle(newItem.getTitle());
				oldItem.setItemId(newItem.getItemId());
				oldItem.setTaxes(newItem.getTaxes());
				oldItem.setTransactionId(newItem.getTransactionId());
				oldItem.setQuantityPurchased(newItem.getQuantityPurchased());
				oldItem.setTransactionPrice(newItem.getTransactionPrice());
				oldItem.setVatPercent(newItem.getVatPercent());
				oldItem.setPaidTime(newItem.getPaidTime());
				oldItem.setShippedTime(newItem.getShippedTime());
				oldItem.setPaypalEmailAddress(newItem
						.getPaypalEmailAddress());
				oldItem.setPaisapayId(newItem.getPaisapayId());
				oldItem.setInvoiceSentTime(newItem.getInvoiceSentTime());
				oldItem.setCommentText(newItem.getCommentText());
				oldItem.setCommentType(newItem.getCommentType());
				oldItem.setTargetUser(newItem.getTargetUser());
				oldItem.setFinalValueFee(newItem.getFinalValueFee());
				oldItem.setEmail(newItem.getEmail());
				oldItem.setSellingmanagersalesrecordNumber(newItem
						.getSellingmanagersalesrecordNumber());
				oldItem.setOrderlineitemId(newItem.getOrderlineitemId());
			}
		}
		return order;
	}
	
	public static void main(String[] args) {
		EbayOrderSynMonitor monitor= new EbayOrderSynMonitor();
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		MfnOrderService  mfn= applicationContext.getBean(MfnOrderService.class);
		monitor.setMfnOrderService(mfn);
		mfn.updateAndSaveEbay();
	}

	public MfnOrderService getMfnOrderService() {
		return mfnOrderService;
	}

	public void setMfnOrderService(MfnOrderService mfnOrderService) {
		this.mfnOrderService = mfnOrderService;
	} 

}
