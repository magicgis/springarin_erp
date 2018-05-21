package com.springrain.erp.modules.amazoninfo.web;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.mapper.JaxbMapper;
import com.springrain.erp.common.mapper.JsonMapper;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonLoginLog;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProductCatalog;
import com.springrain.erp.modules.amazoninfo.entity.AmazonUser;
import com.springrain.erp.modules.amazoninfo.entity.AmazonWarningLetter;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderTmp;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrderItem;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductCatalogService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPromotionsWarningService;
import com.springrain.erp.modules.amazoninfo.service.AmazonUserService;
import com.springrain.erp.modules.amazoninfo.service.AmazonWarningLetterService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonUnlineOrderService;
import com.springrain.erp.modules.custom.entity.UnsubscribeEmail;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.custom.service.UnsubscribeEmailService;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.weixin.utils.ParamesAPI;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

@Controller
@RequestMapping("php")
public class PhpWebService {
	
	@Autowired
	private EventService eventService;
	@Autowired
	private PsiProductService psiProductService;
	@Autowired
	private AmazonOrderService orderService;
	@Autowired
	private UnsubscribeEmailService unsubscribeService;
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	@Autowired
	private AmazonUnlineOrderService amazonUnlineOrderService;
	@Autowired
	private AmazonPromotionsWarningService amazonPromotionsWarningService;

	@Autowired
	private AmazonWarningLetterService warningLetterService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private AmazonUserService amazonUserService;
	
	@Autowired
	private AmazonProductCatalogService amazonProductCatalogService;
	
	private static String key = Global.getConfig("ws.key");
	
	private static Integer num = 0;
	
	private static Logger logger = LoggerFactory.getLogger(PhpWebService.class);
	
	private static final String orderList = "026-5666378-7854709,026-9114187-7458719,203-6616879-9901167,205-7081188-7325938,205-8659855-2250755,205-0417734-6369921,203-1354582-2149947,"+
			"202-0891970-4970766,203-7733663-7612327,203-9092463-7915546,203-9616705-7242736,203-1974885-0405940,203-3326775-1689918,203-9626881-2094757,"+
			"203-6491185-6498735,203-3426561-1209915,203-3100128-4265111,203-3060238-6681938,203-6658130-9281951,203-1985127-5617131,203-2380862-5760315,"+
			"203-4229531-4792328,203-3305168-6859559,203-1010571-6199511,203-2332694-3869952,203-6983989-6597153,203-6604750-2777928,205-9547031-3533935,"+
			"250-6875671-8651838,109-8612672-7224255,109-3526910-1322657,205-7478392-0610752,203-4828673-6557913,109-0736824-0750644,109-0342946-0860200,"+
			"109-6494476-0455443,109-1621261-3900267,303-5617042-1960355,303-0545740-3854768,303-1433547-9185946,303-4094246-7124336,303-1111541-5134726,"+
			"303-5352350-5169143,303-7155130-8476318,303-8652056-4200353,304-7432332-5875500,305-8541129-4647512,305-3172429-8596357,305-6230456-6703511,"+
			"305-9055472-2153960,305-3824147-2424362,305-7875223-1328334,303-4094246-7124336,303-1111541-5134726,303-1433547-9185946,303-5352350-5169143,"+
			"303-8679875-9357123,303-8652056-4200353,303-7155130-8476318,303-2930153-8847538,303-8225430-9890716,303-1764724-6156306,303-2583468-0074716,"+
			"303-6689006-7695563,304-1889232-8009950,304-7432332-5875500,305-8541129-4647512,305-6230456-6703511,305-9268812-7358768,305-5745706-9341166,"+
			"305-9055472-2153960,305-3172429-8596357,305-7875223-1328334,305-3824147-2424362,305-4937934-4154704,303-7251911-1084355,305-0258106-4200309,"+
			"404-3890866-1153912,404-0702283-8570702,404-8594106-5980317,171-6512303-0326713,403-5984073-9381934,403-6540045-8004369,404-1446004-6988348,"+
			"404-7294479-3654748,404-2164328-7334718,404-7677825-0188313,404-8654821-8177917,404-3890866-1153912,404-0702283-8570702,404-8594106-5980317,"+
			"171-6512303-0326713,403-5984073-9381934,403-6540045-8004369,404-1446004-6988348,404-8707912-0641930,203-6658130-9281951,203-1985127-5617131,"+
			"116-8561556-8126668,116-6452623-6927445,116-5800694-5748266,116-5264660-0928229,116-4472195-4114625,002-1758233-2209823,002-7257242-6468229,"+
			"103-0700939-2502609,112-0400901-2367452,116-3379219-7696225,116-1694835-5028245,002-0945033-8266655,002-5434072-0833865,002-8492736-2571429,"+
			"002-5434072-0833865,002-0945033-8266655,102-1576712-1879447,404-1049812-3211555,503-9618256-8923065,249-1907761-4325409,503-7445097-8927046,"+
			"249-2221673-2460604,249-2403704-6837440,249-8139226-3695801,249-4644293-9001455,249-8877642-2739057,249-6339209-0693643,249-8359640-1403049,250-7643854-7245457";
	
	@ResponseBody
	@RequestMapping("getOrdersIsExist")
	public String getOrdersIsExist(@RequestParam(required=false)String key ,@RequestParam(required=false,value="orders")String[]orders){
		if (PhpWebService.key.equals(key) && (orders != null) && (orders.length > 0)) {
			logger.info("getOrdersIsExist。。。");
			String[] rs = new String[orders.length];
			for (int i = 0; i < orders.length; ++i) {
				String order = orders[i];
				if(orderList.contains(order)){
					rs[i] = order + ":true";
					continue;
				}
				rs[i] = order + ":" + this.eventService
						.getEventIsExistByOrder(order);
			}
			return JSON.toJSONString(rs);
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping("getAmazonOrdrs")
	public String getAmazonOrdrs(@RequestParam(required=false)Integer pageSize, @RequestParam(required=false) Integer pageNo,@RequestParam(required=false)String key ,@RequestParam(required=false)String fulfillmentChannel,@RequestParam(required=false)String orderStatus
			,@RequestParam(required=false)String startDate,@RequestParam(required=false)String endDate,@RequestParam(required=false)String dataType){
		logger.info("getAmazonOrdrs。。。");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String returnStr = "";
		int maxPageSize= 50;
		
		
		if(!PhpWebService.key.equals(key)){
			returnStr="error001";
			return returnStr;
		}
		
		
		if(StringUtils.isEmpty(startDate)){
			startDate=sdf.format(new Date());
		}
		
		if(StringUtils.isEmpty(endDate)){
			endDate=sdf.format(new Date());
		}
		
		if(pageSize==null){
			pageSize=10;
		}
		
		if(pageNo==null){
			pageNo=0;
		}
		
		if(pageSize>maxPageSize){
			pageSize=50;
		}
		
		//fulfillmentChannel     MFN      AFN   
		if(StringUtils.isEmpty(fulfillmentChannel)){
			fulfillmentChannel="";
		}
		if(StringUtils.isEmpty(orderStatus)){
			orderStatus="";
		}
		
		Page<AmazonOrder> page = null;
		try {
			 page=orderService.findAmaInterface(new Page<AmazonOrder>(pageNo, pageSize),sdf.parse(startDate), DateUtils.addDays(sdf.parse(endDate),1), fulfillmentChannel, orderStatus);
		} catch (ParseException e) {
			returnStr="Date format exception";
			return returnStr;
		}
		
		if(StringUtils.isEmpty(dataType)){
			dataType="";
		}
		
		if("xml".equals(dataType)){
			returnStr=JaxbMapper.toXml(new AmazonOrderTmp(page.getCount(),page.getList()), "utf-8");
		}else{
			returnStr=JsonMapper.toJsonString(new AmazonOrderTmp(page.getCount(),page.getList()));
		}
		return returnStr;
		
	}
	
	@RequestMapping("cancelEmailNote")
	public String cancelEmailNote(@RequestParam(required=false)String email, @RequestParam(required=false) String key){
		String returnStr ="/modules/custom/error";
		String key1=null;
		if(StringUtils.isNotEmpty(email)&&StringUtils.isNotEmpty(key)){
			key1 =UnsubscribeEmailService.getKeyByMD5(email);
			if(key.equals(key1)){
				Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
				Matcher matcher = pattern.matcher(email);
				//&&email.contains("@marketplace.amazon.")
				if(matcher.matches()){
					//业务处理
					if(unsubscribeService.isNotExist(email)){
						unsubscribeService.save(new UnsubscribeEmail(email,new Date()));
					}
					returnStr="/modules/custom/success";
				}
			}else{
				logger.warn("遭到了攻击！！邮箱:"+email);
			}
		}
		return returnStr;
	}
	
	
	@ResponseBody
	@RequestMapping("reviewMaster")
	public String getReviewMaster(String key ,@RequestParam(required=false,value="links")String[]links){
		if (PhpWebService.key.equals(key) && (links != null) && (links.length > 0)) {
			logger.info("reviewMaster。。。");
			Map<String, String> rs = Maps.newLinkedHashMap();
			for (String link : links) {
				try {
					String linkEncode = URLDecoder.decode(link,"utf-8");
					String name = eventService.getLinksMasterName(linkEncode);
					if(StringUtils.isEmpty(name)){
						name="not find";
					}
					rs.put(link, name);
				} catch (UnsupportedEncodingException e) {logger.error(e.getMessage(),e);}
			}
			return JSON.toJSONString(rs);
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping("getAsin")
	public String getAsins(String key){
		if (PhpWebService.key.equals(key)) {
			logger.info("getAsin。。。");
			return JSON.toJSONString(amazonProduct2Service.findAsinsByReview());
		}
		return null;
	}
	
	
	@ResponseBody
    @RequestMapping("returnInfo")
    public String returnInfo(String orderIds,String key,HttpServletRequest request,HttpServletResponse response) throws IOException {
		if (PhpWebService.key.equals(key)) {
			logger.info("返回官网订单状态。。。");
			String[] orderId=orderIds.split(",");
			List<AmazonUnlineOrder> orderList=amazonUnlineOrderService.getByOrderIds(Sets.newHashSet(orderId));
			StringBuilder buf=new StringBuilder("[");
			for (int i=0;i<orderList.size();i++) {
				AmazonUnlineOrder order =orderList.get(i);
				String status=("Waiting for delivery".equals(order.getOrderStatus())?"Unshipped":order.getOrderStatus());
				buf.append("{\"orderId\":"+order.getSellerOrderId()+",\"amazonOrderId\":\""+order.getAmazonOrderId()+"\",\"orderStatus\":\""+status+"\",\"billNo\":\""+(StringUtils.isBlank(order.getBillNo())?"":order.getBillNo())+"\",\"supplier\":\""+(StringUtils.isBlank(order.getSupplier())?"":order.getSupplier())+"\",\"shippedDate\":\""+(order.getEarliestShipDate()==null?"":new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getEarliestShipDate()))+"\"}");
			    if(i!=orderList.size()-1){
			    	buf.append(",");
			    }
			}
			buf.append("]");
			return buf.toString();
		}
		return null;
	}
	
	 //传递json数组字符串  
	@ResponseBody
    @RequestMapping("saveOrders")
    public String resolveJsonArray(String key,HttpServletRequest request,HttpServletResponse response) throws IOException {
		 String info="";
    	if (PhpWebService.key.equals(key)) {
    		try{
    			logger.info("官网保存线下订单开始。。。");
        		List<AmazonUnlineOrder> amazonUnlineOrders=Lists.newArrayList();
        		Set<Integer> itemIdSet=Sets.newHashSet();
                String str = URLDecoder.decode(request.getParameter("orderJson"),"UTF-8");  
                JSONArray jsonArray=JSON.parseArray(str);
                for (Object object : jsonArray) {
                	  JSONObject o = (JSONObject)object;
                	  String amazonOrderId=(String) o.get("amazonOrderId");
                	  AmazonUnlineOrder unlineOrder=amazonUnlineOrderService.getByOrderId(amazonOrderId);
                	  if(unlineOrder==null){//add
                		  AmazonUnlineOrder amazonUnlineOrder=new AmazonUnlineOrder();
                    	  amazonUnlineOrder.setOutBound("0");
                    	  String country=(String) o.get("countryCode");
                    	  if("US".equals(country)){
                    		  amazonUnlineOrder.setSalesChannel(new Stock(120));
                    	  }else{
                    		  amazonUnlineOrder.setSalesChannel(new Stock(19));
                    	  }
                    	
                		  amazonUnlineOrder.setAmazonOrderId(amazonOrderId);
                		  String orderId=(String) o.get("orderId");
                    	  amazonUnlineOrder.setSellerOrderId(orderId);
                    	  String orderStatus=(String) o.get("orderStatus");
                    	  if("Unshipped".equals(orderStatus)){
                    		  amazonUnlineOrder.setOrderStatus("Waiting for delivery");
                    	  }else{
                    		  amazonUnlineOrder.setOrderStatus(orderStatus);
                    	  }
                    	  
                    	  amazonUnlineOrder.setFulfillmentChannel("MFN");
                  		  amazonUnlineOrder.setLastUpdateDate(new Date());
                  		  amazonUnlineOrder.setShipServiceLevel("Standard");
                  		  amazonUnlineOrder.setShipmentServiceLevelCategory("Standard");
                  		  amazonUnlineOrder.setOrderType("Standard");
                  		  amazonUnlineOrder.setOrderChannel("管理员");
                  		  amazonUnlineOrder.setPaymentMethod("Other");
                  		
                  		  String buyerEmail=(String) o.get("buyerEmail");
                  		  String buyerName=(String) o.get("buyerName");
                  		  amazonUnlineOrder.setBuyerEmail(buyerEmail);
                  		  amazonUnlineOrder.setBuyerName(buyerName);
                  		  String purchaseDate=(String) o.get("purchaseDate");
                  		  try {
            				amazonUnlineOrder.setPurchaseDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(purchaseDate));
            			  } catch (ParseException e) {
            				  amazonUnlineOrder.setPurchaseDate(new Date());
            			  }
                  		
                          Map<String,String> map = (Map<String,String>)o.get("shippingAddress");
                          AmazonUnlineAddress shippingAddress=new AmazonUnlineAddress();
                          shippingAddress.setName(map.get("name"));
                          shippingAddress.setAddressLine1(map.get("addressLine1"));
                          shippingAddress.setAddressLine2(map.get("addressLine2"));
                          shippingAddress.setAddressLine3(map.get("addressLine3"));
                          shippingAddress.setCity(map.get("city"));
                          shippingAddress.setCounty(map.get("country"));
                          shippingAddress.setStateOrRegion(map.get("stateOrRegion"));
                          shippingAddress.setPostalCode(map.get("postalCode"));
                          shippingAddress.setCountryCode(map.get("countryCode"));
                          shippingAddress.setPhone(map.get("phone"));
                          amazonUnlineOrder.setShippingAddress(shippingAddress);
                          
                          List<AmazonUnlineOrderItem> items = Lists.newArrayList();
                          List orderItems=(List)o.get("orderItems");
                         // Float orderTotal = new Float(0);
                          Float orderTotal=Float.parseFloat((String)o.get("orderTotal")); 
                  		  int    shipedTotal = 0;
                  		  int    upshipedTotal =0;
                          for (Object obj : orderItems) {
                        	  AmazonUnlineOrderItem item=new AmazonUnlineOrderItem();
                        	  Map<String,String> itemMap= (Map<String,String>)obj;
                              String asin=(String) itemMap.get("asin");
                             
                              Integer quantityOrdered=(int)Float.parseFloat(itemMap.get("quantityOrdered")); 
                           //   Integer quantityOrdered=10;
                              Float itemPrice=Float.parseFloat(itemMap.get("itemPrice")); 
                              Float shippingPrice=Float.parseFloat(itemMap.get("shippingPrice"));  
                              item.setAsin(asin);
                              String tempCountry="";
                              if("US".equals(country)){
                            	  tempCountry="com";
                        	  }else{
                        		  tempCountry="de";
                        	  }
                              PsiSku psiSku=psiProductService.getProductByAsin(asin,tempCountry);
                              if(psiSku!=null){
                            	  item.setProductName(psiSku.getProductName());
                                  item.setColor(psiSku.getColor());
                                  item.setSellersku(psiSku.getSku());
                                  item.setTitle(psiSku.getProductName());
                                  item.setCountry(psiSku.getCountry());
                              }else{
                            	  info+=orderId+"订单"+asin+"不存在;";
                            	  continue;
                              }
                             
                              item.setQuantityOrdered(quantityOrdered);
                              item.setQuantityShipped(quantityOrdered);
                              item.setItemPrice(itemPrice);
                             
                              if("US".equals(country)){
                            	  item.setItemTax(6.47f);
                        	  }else{
                        		  item.setItemTax(19f);
                        	  }
                              item.setShippingPrice(shippingPrice);
                              item.setGiftWrapPrice(0f);
                              item.setOrder(amazonUnlineOrder);
                              items.add(item);
                              upshipedTotal+=item.getQuantityOrdered()-item.getQuantityShipped();
                  			  shipedTotal+=item.getQuantityShipped();
                  			  //单价   单项总价变换
                  			  //Float itemTotalPrice=item.getItemPrice()*item.getQuantityOrdered()+item.getItemPrice()*item.getQuantityOrdered()*item.getItemTax()/100;
                  			 // orderTotal +=itemTotalPrice+item.getShippingPrice()+item.getGiftWrapPrice();
            			  }
                      	  DecimalFormat df =new DecimalFormat("#.00");
                		  amazonUnlineOrder.setOrderTotal(Float.parseFloat(df.format(orderTotal)));
                		  amazonUnlineOrder.setNumberOfItemsShipped(shipedTotal);//已发货总数
                		  amazonUnlineOrder.setNumberOfItemsUnshipped(upshipedTotal);//未发货数量
                          amazonUnlineOrder.setItems(items);
                          amazonUnlineOrders.add(amazonUnlineOrder);
                	  }else{//edit
                		  if(!"Shipped".equals(unlineOrder.getOrderStatus())&&!"Waiting for delivery".equals(unlineOrder.getOrderStatus())&&!"1".equals(unlineOrder.getOutBound())){
                			  String country=(String) o.get("countryCode");
                			  String orderStatus=(String) o.get("orderStatus");
                			  if("Unshipped".equals(orderStatus)){
                				  unlineOrder.setOrderStatus("Waiting for delivery");
                        	  }else{
                        		  unlineOrder.setOrderStatus(orderStatus);
                        	  }
                    		 
                    		  unlineOrder.setLastUpdateDate(new Date());
                    		  Map<String,String> map = (Map<String,String>)o.get("shippingAddress");
                              AmazonUnlineAddress shippingAddress=unlineOrder.getShippingAddress();
                              shippingAddress.setName(map.get("name"));
                              shippingAddress.setAddressLine1(map.get("addressLine1"));
                              shippingAddress.setAddressLine2(map.get("addressLine2"));
                              shippingAddress.setAddressLine3(map.get("addressLine3"));
                              shippingAddress.setCity(map.get("city"));
                              shippingAddress.setCounty(map.get("country"));
                              shippingAddress.setStateOrRegion(map.get("stateOrRegion"));
                              shippingAddress.setPostalCode(map.get("postalCode"));
                              shippingAddress.setCountryCode(map.get("countryCode"));
                              shippingAddress.setPhone(map.get("phone"));
                              unlineOrder.setShippingAddress(shippingAddress);
                              
                              
                              List<AmazonUnlineOrderItem> newItems = Lists.newArrayList();
                             // Float orderTotal = new Float(0);
                              Float orderTotal=Float.parseFloat((String)o.get("orderTotal")); 
                      		  int    shipedTotal = 0;
                      		  int    upshipedTotal =0;
                              List orderItems=(List)o.get("orderItems");
                              for (Object obj : orderItems) {
                            	  AmazonUnlineOrderItem item=new AmazonUnlineOrderItem();
                            	  Map<String,String> itemMap= (Map<String,String>)obj;
                                  String asin=(String) itemMap.get("asin");
                                //  Integer quantityOrdered=Integer.parseInt(itemMap.get("quantityOrdered")); 
                                  Integer quantityOrdered=(int)Float.parseFloat(itemMap.get("quantityOrdered")); 
                                  Float itemPrice=Float.parseFloat(itemMap.get("itemPrice")); 
                                  Float shippingPrice=Float.parseFloat(itemMap.get("shippingPrice"));  
                                  item.setAsin(asin);
                                  item.setQuantityOrdered(quantityOrdered);
                                  item.setQuantityShipped(quantityOrdered);
                                  item.setItemPrice(itemPrice);
                                 
                                  if("US".equals(country)){
                                	  item.setItemTax(6.47f);
                            	  }else{
                            		  item.setItemTax(19f);
                            	  }
                                  item.setShippingPrice(shippingPrice);
                                  item.setGiftWrapPrice(0f);
                                  item.setOrder(unlineOrder);
                                  String tempCountry="";
                                  if("US".equals(country)){
                                	  tempCountry="com";
                            	  }else{
                            		  tempCountry="de";
                            	  }
                                  PsiSku psiSku=psiProductService.getProductByAsin(asin,tempCountry);
                                  if(psiSku!=null){
                                	  item.setProductName(psiSku.getProductName());
                                      item.setColor(psiSku.getColor());
                                      item.setSellersku(psiSku.getSku());
                                      item.setTitle(psiSku.getProductName());
                                      item.setCountry(psiSku.getCountry());
                                  }else{
                                	  info+=amazonOrderId+"订单"+asin+"不存在;";
                                	  continue;
                                  }
                                  newItems.add(item);
                			  }
                              boolean isExist=false;
                              List<AmazonUnlineOrderItem> removeItem=Lists.newArrayList();
                              for (AmazonUnlineOrderItem item: unlineOrder.getItems()) {
                            	   AmazonUnlineOrderItem updateItem=new AmazonUnlineOrderItem();
                            	    for (AmazonUnlineOrderItem newItem: newItems) {
        								if(item.getAsin().equals(newItem.getAsin())){
        									updateItem=newItem;
        									isExist=true;
        									break;
        								}
        							}
                            	    if(isExist){//update
                            	    	 item.setAsin(updateItem.getAsin());
                                         item.setQuantityOrdered(updateItem.getQuantityOrdered());
                                         item.setQuantityShipped(updateItem.getQuantityOrdered());
                                         item.setItemPrice(updateItem.getItemPrice());
                                         if("US".equals(country)){
                                       	      item.setItemTax(6.47f);
	                                   	  }else{
	                                   		  item.setItemTax(19f);
	                                   	  }
                                         item.setShippingPrice(updateItem.getShippingPrice());
                                         item.setGiftWrapPrice(0f);
                                         item.setProductName(updateItem.getProductName());
                                         item.setColor(updateItem.getColor());
                                         item.setSellersku(updateItem.getSellersku());
                                         item.setTitle(updateItem.getProductName());
                                         item.setCountry(updateItem.getCountry());
                            	    }else{//delete
                            	    	removeItem.add(item);
                            	    	itemIdSet.add(item.getId());
                            	    }
                            	    isExist=false;
        					  }
                              unlineOrder.getItems().removeAll(removeItem);
                              boolean isNotExist=true;
                              for (AmazonUnlineOrderItem newItem: newItems) {
                            	  for (AmazonUnlineOrderItem item: unlineOrder.getItems()) {
                            			if(item.getAsin().equals(newItem.getAsin())){
        									isNotExist=false;
        									break;
        								}
                            	  }
                            	  if(isNotExist){//add
                            		  unlineOrder.getItems().add(newItem);
                            		  newItem.setOrder(unlineOrder);
                            	  }
                            	  isNotExist=true;
                              }
                              for (AmazonUnlineOrderItem item: unlineOrder.getItems()) {
                            	  upshipedTotal+=item.getQuantityOrdered()-item.getQuantityShipped();
                      			  shipedTotal+=item.getQuantityShipped();
                      			  //单价   单项总价变换
                      			 // Float itemTotalPrice=item.getItemPrice()*item.getQuantityOrdered()+item.getItemPrice()*item.getQuantityOrdered()*item.getItemTax()/100;
                      			 // orderTotal +=itemTotalPrice+item.getShippingPrice()+item.getGiftWrapPrice();
                              }
                              DecimalFormat df =new DecimalFormat("#.00");
                              unlineOrder.setOrderTotal(Float.parseFloat(df.format(orderTotal)));
                              unlineOrder.setNumberOfItemsShipped(shipedTotal);//已发货总数
                              unlineOrder.setNumberOfItemsUnshipped(upshipedTotal);//未发货数量
                    		  amazonUnlineOrders.add(unlineOrder);
                			  
                			  
                		  }else{
                			  info+=amazonOrderId+"订单已发货不能修改";
                		  }
                	  }
        		}
                amazonUnlineOrderService.save(amazonUnlineOrders);
                if(itemIdSet!=null&&itemIdSet.size()>0){
                	amazonUnlineOrderService.delete(itemIdSet);
                }
            	logger.info("官网保存线下订单结束。。。"+info);
                return "1;"+info;
    		}catch(Exception e){
    			logger.error(e.getMessage(),e);
    			return "0;"+info;
    		}
    	}
    	return "0;"+info;
    } 
	
	
	
	
	//de 100  jp com 50 http://192.168.20.30:8080/inateck-erp/php/generatePromotionsCode?key=Hip6k8wOkQ2qb2*Bb&country=de&num=1&type=0
	/*@ResponseBody
    @RequestMapping("generatePromotionsCode")
    public String generatePromotionsCode(final String country,Integer num,String key,HttpServletRequest request,HttpServletResponse response) throws IOException {
		if (PhpWebService.key.equals(key)) {
			logger.info("官网获取折扣,请勿重启。。。");
			try{
				    try{
				    	amazonPromotionsWarningService.updateCodeIsActive(country);
				    }catch(Exception e){
				    	logger.error(e.getMessage(),e);
				    }
				    Map<String,Object[]> codeMap=amazonPromotionsWarningService.getPromotionsCodeInventoryDetail(country,num);
					if(codeMap!=null&&codeMap.size()>0){
						try{
							//true 85  德国20-5欧，美国、日本是85折
							String type="0";
							List<AmazonSysPromotionsItem> codeList=Lists.newArrayList();
							if("de".equals(country)||"fr".equals(country)){//-20
								type="1";
							}
							AmazonSysPromotions promotions=new AmazonSysPromotions();
							promotions.setCountry(country);
							promotions.setType("0");
							promotions.setCreateUser(new User("1"));
							promotions.setCreateDate(new Date());
							promotions.setPromotionsType(type);
							promotions.setNum(num);
							promotions.setStatus("1");
							List<AmazonSysPromotionsItem> items=Lists.newArrayList();
							SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							for (Map.Entry<String,Object[]> entry : codeMap.entrySet()) { 
							    String trackId = entry.getKey();
								Object[] obj = entry.getValue();
								AmazonSysPromotionsItem item=new AmazonSysPromotionsItem();
								item.setAmazonSysPromotions(promotions);
								item.setPromotionsId(trackId);
								item.setPromotionsCode(obj[1].toString().trim());
								item.setCountry(country);
								items.add(item);
								
								AmazonSysPromotionsItem item2=new AmazonSysPromotionsItem();
								item2.setPromotionsCode(obj[1].toString().trim());
								item2.setCreateDate(format.format(((Timestamp)obj[2])));//
								codeList.add(item2);
							}
							
							promotions.setItems(items);
							amazonPromotionsWarningService.savePromotionsCode(promotions);
							amazonPromotionsWarningService.updateCodeIsActive(country,codeMap.keySet());
							
							String json=JSONArray.toJSONString(codeList);
							return json;
					}catch(Exception e){
						logger.error(e.getMessage(),e);
					}
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			logger.info("官网获取折扣结束。。。");
		}
		return null;
	}*/
	
	//http://127.0.0.1:8080/inateck-erp/php/viewLetter?key=Hip6k8wOkQ2qb2*Bb&id=1
	@RequestMapping(value = "viewLetter")
	public String viewLetter(@RequestParam(required=false)String key ,Integer id, Model model) {
		if (PhpWebService.key.equals(key)) {
			logger.info("viewLetter。。。");
			AmazonWarningLetter letter = warningLetterService.get(id);
			if (letter == null) {
				return "error/404";
			}
			model.addAttribute("warningLetter", letter);
			return "modules/amazoninfo/amazonWarningLetterForm";
		}
		return "error/404";
	}
	
	/**
	 * 根据ERP账户获取亚马逊后台账户信息
	 * @return
	 *	WebClient client = new WebClient();
		String url = "http://127.0.0.1:8090/springrain-erp/php/getAmazonAccount";
		String requestBody = "{\"country\":\"country\",\"name\":\"name\",\"pw\":\"pw\"}";
		WebRequest req = new WebRequest(new URL(url),HttpMethod.POST);
		req.setAdditionalHeader("X-Requested-With", "XMLHttpRequest");
		req.setRequestBody(requestBody);
		Page page = client.getPage(req);
		System.err.println(page.getWebResponse().getContentAsString());
	 * http://127.0.0.1:8090/springrain-erp/php/getAmazonAccount?json='country':country,'name':name,'pw':pw
	 */
	@ResponseBody
	@RequestMapping(value="getAmazonAccount", method = RequestMethod.POST)
    public String getAmazonAccount(@RequestBody String json, HttpServletRequest request,HttpServletResponse response) throws IOException {
		num++;	//统计调用次数,1000次发一次通知,系统重启后次数重新计算
		if (num % 1000 == 0) {
			WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "亚马逊账户请求次数：" + num);
		}
		Map<String, String> rs = Maps.newHashMap();
		//验证用户
		json = URLDecoder.decode(json,"utf-8");
		json = json.substring(0,json.lastIndexOf("}")+1);
		Map<String, String> map = JSON.parseObject(json, Map.class);
		String loginName = map.get("name");
		String password = map.get("pw");
		String country = map.get("country");
		if (StringUtils.isEmpty(loginName)) {
			rs.put("error", "loginName is null.");
			return JSON.toJSONString(rs);
		}
		if (StringUtils.isEmpty(password)) {
			rs.put("error", "password is null.");
			return JSON.toJSONString(rs);
		}
		if (StringUtils.isEmpty(country)) {
			rs.put("error", "country is null.");
			return JSON.toJSONString(rs);
		}
		User user = systemService.getUserByLoginName(loginName);
		//设置响应头支持跨域访问
		response.setHeader("content-type","application:json;charset=utf8");
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setHeader("Access-Control-Allow-Methods","POST");
		response.setHeader("Access-Control-Allow-Headers","x-requested-with,content-type");
		if (user == null) {
			rs.put("error", "incorrect username or password.");
		}else if (SystemService.validatePassword(password, user.getPassword())) {
			if (!"de,fr,uk,it,es,com,ca,jp,eu,mx".contains(country.replace("ams.", "").replace("vendor.", ""))) {
				rs.put("error", "Invalid country: "+country+",Reboot or refresh your browser and try again.");
			} else {
				if ("de,fr,uk,it,es".contains(country)) {
					country = "eu";
				}
				AmazonUser amazonUser = amazonUserService.getAmazonUserByErpInfo(country, user.getRoleNames(), StringUtils.getRemoteAddr(request));
				if (amazonUser == null) {
					rs.put("error", "Without authorization.");
				} else {
					rs.put("name", amazonUser.getAccount());
					rs.put("pw", amazonUser.getPassword());
					AmazonLoginLog log = new AmazonLoginLog();
					log.setCountry(country);
					log.setUser(user);
					log.setDataDate(new Date());
					log.setIp(com.springrain.erp.common.utils.StringUtils.getRemoteAddr(request));
					amazonUserService.saveLog(log);
				}
			}
		} else {
			rs.put("error", "incorrect username or password.");
		}
		return JSON.toJSONString(rs);
	}
	
	@ResponseBody
	@RequestMapping("getCatalogs")
    public String getCatalogs(@RequestParam(required=false)String key,String country,HttpServletRequest request,HttpServletResponse response) throws IOException {
		if (PhpWebService.key.equals(key)) {
			 List<AmazonProductCatalog> catalogList=amazonProductCatalogService.findCatalog(country);
			 return JSON.toJSONString(catalogList);
		}
		return null;
	}
	
	//http://127.0.0.1:8080/inateck-erp/php/catalogNotice?key=Hip6k8wOkQ2qb2*Bb&message=1
	//return  success:成功   fail:失败,目录扫描程序发送消息接口
	@ResponseBody
	@RequestMapping(value = "catalogNotice")
	public String catalogNotice(@RequestParam(required=false)String key ,String message, Model model) {
		try {
			logger.info("catalogNotice:" + message);
			if (PhpWebService.key.equals(key) && StringUtils.isNotEmpty(message)) {
				WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, message);
			}
			return "success";
		} catch (Exception e) {
			logger.error("目录扫描通知发送失败！！", e);
		}
		return "fail";
	}
	
	//http://127.0.0.1:8080/inateck-erp/php/inateckNotice?key=Hip6k8wOkQ2qb2*Bb&user=user1|user2&message=1
	//return  success:成功   fail:失败,目录扫描程序发送消息接口
	@ResponseBody
	@RequestMapping(value = "inateckNotice")
	public String inateckNotice(@RequestParam(required=false)String key ,String user ,String message, Model model) {
		try {
			logger.info("user：" + user + "\tinateckNotice:" + message);
			if (PhpWebService.key.equals(key) && StringUtils.isNotEmpty(message) && StringUtils.isNotEmpty(user)) {
				WeixinSendMsgUtil.sendTextMsgToUser(user, message);
				return "success";
			}
		} catch (Exception e) {
			logger.error("官网通知发送失败！！", e);
		}
		return "fail";
	}
	
	/**
	 * http://127.0.0.1:8080/inateck-erp/php/erpNotice?key=Hip6k8wOkQ2qb2*Bb&message=message
	 * @param key
	 * @param message	base64编码后的json格式的消息内容
	 * @param model
	 * @return  success:成功   fail:失败,ERP服务推送微信消息接口
	 */
	@ResponseBody
	@RequestMapping(value = "erpNotice")
	public String erpNotice(@RequestParam(required=false)String key ,String message, Model model) {
		try {
			if (PhpWebService.key.equals(key) && StringUtils.isNotEmpty(message)) {
				message = message.replace(" ", "+");	//传输过程中“+”会变成空格
				final String sendMsg = message = Encodes.getUnBASE64String(message.getBytes());
		    	//logger.info("ERPNotice:" + sendMsg);
				new Thread(){
				    public void run(){
						WeixinSendMsgUtil.sendErpTxt(null, sendMsg, 0);
				    }
				}.start();
			}
			return "success";
		} catch (Exception e) {
			logger.error("ERP通知发送失败！！", e);
		}
		return "fail";
	}
	
	private static List<String> errorInfoList = Lists.newArrayList();
	private static String logPath = "/opt/apache-tomcat-7.0.53/logs/jeesite.log";
	
	static {
		errorInfoList.add("Java heap space");
		errorInfoList.add("Could not open connection");
		//errorInfoList.add("abandon connection");
	}
	
	//http://127.0.0.1:8080/inateck-erp/php/erpMonitor?key=Hip6k8wOkQ2qb2*Bb
	//return  0:成功   1:失败
	@ResponseBody
	@RequestMapping(value = "erpMonitor")
	public String erpMonitor(@RequestParam(required=false)String key, Model model) {
		logger.info("系运行统状态监控");
		if (PhpWebService.key.equals(key)) {
			try {
				//解析文件
				File txtFile = new File(logPath);
				if (txtFile.exists()) {
					// 进行处理
					List<String> lines = FileUtils.readLines(txtFile);
					for (final String line : lines) {
						for (String errorInfo : errorInfoList) {
							if (line.contains(errorInfo)) {
								new Thread(){
									public void run(){
										restartService(line);
									}
								}.start();
								logger.info("系统监控到异常,开始自动重启系统");
								return "1";
							}
						}
					}
				}
				logger.info("系统监控完毕");
				return "1";
			} catch (Exception e) {
				logger.error("解析日志文件异常！！", e);
				WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "系统监控解析ERP日志文件异常！！");
			}
		}
		logger.info("系统监控身份验证失败");
		return "0";
	}
	
	private void restartService(String info){
		try {
			Thread.sleep(3000L); //休眠三秒,返回结果给官网后自动重启
			//去掉邮件定时器
			/*File file = new File("/opt/apache-tomcat-7.0.53/webapps/inateck-erp/WEB-INF/classes/spring-context-quartz.xml");
			String lines = FileUtils.readFileToString(file, "UTF-8");
			lines = lines.replace("<ref local=\"sayRunTrigger\"/>", "");
			FileUtils.writeStringToFile(file, lines, "UTF-8");*/
			//发送微信通知
			String content = "系统出现致命异常,已尝试自行重启,请知悉并关注系统状态\n异常信息:" + info;
			WeixinSendMsgUtil.sendTextMsg(WeixinSendMsgUtil.getToken(), null, "7", null, ParamesAPI.appId, content, "0");
			String shpath="/opt/restart.sh";   //程序路径
		    String command2 = "sh " + shpath;
		    Runtime.getRuntime().exec(command2);
		} catch (Exception e) {
			logger.error("系统重启失败", e);
			String content = "系统出现致命异常,系统尝试自行重启已失败,请尽快重启服务\n异常信息:" + info;
			WeixinSendMsgUtil.sendTextMsg(WeixinSendMsgUtil.getToken(), null, "7", null, ParamesAPI.appId, content, "0");
		}
	}
	
}
