package com.springrain.erp.modules.amazoninfo.scheduler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.modules.amazoninfo.entity.ProductPrice;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonBuyComment;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomer;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOutboundOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOutboundOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonRefund;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazoninfoRefundItem;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.amazoninfo.service.AmazonOperationReportService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductCatalogService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.ReturnGoodsService;
import com.springrain.erp.modules.amazoninfo.service.SaleAnalysisReportService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOutboundOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonRefundService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonUnlineOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.MfnOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.VendorShipmentService;
import com.springrain.erp.modules.custom.scheduler.CustomNoReplyEmailManager;
import com.springrain.erp.modules.custom.service.CommentService;
import com.springrain.erp.modules.custom.service.CustomEmailService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.ebay.entity.EbayProductPrice;
import com.springrain.erp.modules.ebay.entity.EbayProductProfit;
import com.springrain.erp.modules.ebay.service.EbayOrderService;
import com.springrain.erp.modules.psi.entity.PsiProductTransportRate;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceService;
import com.springrain.erp.modules.psi.service.PsiTransportPaymentService;
import com.springrain.logistics.usps.UspsL5Service;
public class AmazonOperationReportMonitor {
	
	private final static Logger logger = LoggerFactory.getLogger(AmazonOperationReportMonitor.class);
	@Autowired
	private AmazonOperationReportService  amazonOperationReportService;
	@Autowired
	private VendorShipmentService vendorShipmentService;
	@Autowired
	private AmazonProductService amazonProductService;
	@Autowired
	private PsiProductTieredPriceService psiProductTieredPriceService;
	@Autowired
	private PsiInventoryService psiInventoryService;
	@Autowired
	private PsiProductAttributeService psiProductAttributeService;
	@Autowired
	private AmazonOrderService amazonOrderService;
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	@Autowired
	private PsiProductService psiProductService;
	@Autowired
	private AmazonOutboundOrderService amazonOutboundOrderService;
	//private static final String CUSTOM_URL ="https://sellercentral.amazon.suffix/gp/orders-v2/list/ref=ag_myo_apsearch_myosearch?searchType=OrderID&searchKeyword=";
	@Autowired
	private AmazonRefundService amazonRefundService;
	@Autowired
	private AmazonCustomerService amazonCustomerService;
	@Autowired
	private EventService eventService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private CustomNoReplyEmailManager  mailManager;
	
	@Autowired
	private AmazonProductCatalogService amazonProductCatalogService;
	@Autowired
	private MfnOrderService mfnOrderService;
	
	@Autowired
	private AmazonUnlineOrderService amazonUnlineOrderService;
	@Autowired
	private AmazonPostsDetailService amazonPostsDetailService;
	@Autowired
	private PsiTransportPaymentService psiTransportPaymentService;
	@Autowired
	private EbayOrderService ebayOrderService;
	@Autowired
	private SaleReportService saleReportService;
	@Autowired
	private CustomEmailService customEmailService;
	@Autowired
	private ReturnGoodsService returnGoodsService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	@Autowired
	private SaleAnalysisReportService saleAnalysisReportService;
	
	public void saveEbayProfit(){
		List<Object[]> ebayProduct=ebayOrderService.findSalesByDate();
		Map<String,EbayProductPrice> priceMap=ebayOrderService.findPrice();
		Map<String,String> productMap=psiProductService.findEbayName();
		Map<String,Map<String,Object>> productTypeMap=psiProductService.getProductTypeAndWeight();
		Map<String, Map<String, PsiProductTransportRate>> productTranFee = psiTransportPaymentService.findTransportRateAndPrice();
		Map<String, Map<String,Float>> costAndGw = saleReportService.getProductPriceAndTranGw("USD",null);
		Map<String, PsiProductTransportRate>  countryTranFee = psiTransportPaymentService.findTransportPriceByToCountry();
		Map<String,Float> fbaFeeMap=ebayOrderService.findFbaShipmentFee();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
		List<EbayProductProfit> profitList=Lists.newArrayList();
		if(ebayProduct!=null&&ebayProduct.size()>0){
			for (Object[] obj: ebayProduct) {
				String country=obj[0].toString();
				Date date=(Date)obj[1];
				String sku=obj[2].toString();
				Float totalMoney=Float.parseFloat(obj[3].toString());
				Integer totalQuantity=Integer.parseInt(obj[4].toString());
				Float price=Float.parseFloat(obj[5].toString());
				
				String countryKey="ebay";
				String key = "EU";
				Float rate=MathUtils.getRate("USD","EUR", null);
				if("com".equals(country)){
					countryKey="ebay_com";
					rate=MathUtils.getRate("USD","USD", null);
					key="US";
				}
				String productName=productMap.get(countryKey+"_"+sku);
				if(StringUtils.isBlank(productName)){
					continue;
				}
				EbayProductPrice ebayProductPrice=priceMap.get(dateFormat.format(date)+"_"+productName+"_"+country);
				Float purchasePrice=0f;
				Float tranPrice=0f;
				
				if(ebayProductPrice==null||ebayProductPrice.getPurchasePrice()==null||ebayProductPrice.getPurchasePrice()==0){
					if(costAndGw.get(productName)!=null){
						Float partsPrice=0f;
					    if(costAndGw.get(productName).get("parts")!=null){
					    	partsPrice=costAndGw.get(productName).get("parts");
					    }
					    if(costAndGw.get(productName).get("price")!=null){
					    	purchasePrice=(costAndGw.get(productName).get("price")+partsPrice)*rate;
					    }else{
					    	continue;
					    }
					 }else{
						 continue;
					 }
				}else{
					purchasePrice=ebayProductPrice.getPurchasePrice();
				}
				Float tranGw=(Float)productTypeMap.get(productName).get("tranGw");
				if(ebayProductPrice==null||ebayProductPrice.getTranPrice()==null||ebayProductPrice.getTranPrice()==0){
					Map<String, PsiProductTransportRate> transportPrice = productTranFee.get(productName);
					if(transportPrice==null){
						transportPrice = countryTranFee;
					}
					if(transportPrice!=null){
						if(transportPrice.get(key)!=null){
							tranPrice = transportPrice.get(key).getAirPrice();
							if(!"ca".equals(country)){
								tranPrice = transportPrice.get(key).getAvgPrice();
							}
							if(StringUtils.isEmpty(transportPrice.get(key).getProductName())){
								if(tranPrice!=null){
									tranPrice = tranPrice*tranGw;
								}
							}
						}
					}

					if((tranPrice==null||tranPrice==0f)&&countryTranFee!=null&&countryTranFee.get(key)!=null&&countryTranFee.get(key).getAvgPrice()!=null){
						tranPrice = countryTranFee.get(key).getAvgPrice()*tranGw;
					}
					
					if(tranPrice==null||tranPrice==0f){
						tranPrice = ProductPrice.tranFee.get(country)*tranGw;
					}
					if("com".equals(country)){
						tranPrice=tranPrice*MathUtils.getRate("CNY","USD", null);
					}else{
						tranPrice=tranPrice*MathUtils.getRate("CNY","EUR", null);
					}
				}else{
					tranPrice=ebayProductPrice.getTranFee();
				}
				if(purchasePrice==0||tranPrice==0){
					continue;
				}
				
				//A(1+关税)+B*ebay成交费+B*paypal成交费+运费+利润=B/(1+VAT)
				Float duty=0f;
				if("uk,it,es,fr,de".contains(country)){
					duty=(Float)productTypeMap.get(productName).get("euDuty")/100;
				}else{
					duty=(Float)productTypeMap.get(productName).get("usDuty")/100;
				}
				Float totalPurchasePrice=purchasePrice*(1+duty)*totalQuantity;
				Float totalTranFee=tranPrice*totalQuantity;
				Float shippingPrice=0f;
				if("com".equals(country)){
				/*	if(fbaFeeMap!=null&&fbaFeeMap.get(productName+"_"+dateFormat.format(date))!=null){
						totalTranFee=totalTranFee+fbaFeeMap.get(productName+"_"+dateFormat.format(date));
					}*/
					if(fbaFeeMap!=null&&fbaFeeMap.get(productName+"_"+dateFormat.format(date))!=null){
						shippingPrice=fbaFeeMap.get(productName+"_"+dateFormat.format(date));
					}
				}else{
					shippingPrice=3.99f*totalQuantity;
				}
				
				Float paypalFee=(price*0.06f*rate+ 0.05f*rate)*totalQuantity;
				
				Float dealRate=0f;
				String type=(String)productTypeMap.get(productName).get("type");
				if("HDD Adapter".equals(type)||"HDD case".equals(type)||"HDD enclosures".equals(type)){
				    dealRate=0.04f*rate*totalQuantity;
				}else if("Headset".equals(type)||"Hub".equals(type)||"Earphone".equals(type)||"Speaker".equals(type)||"Wireless presenter".equals(type)){
				    dealRate=0.06f*rate*totalQuantity;
				}else{
				    dealRate=0.09f*rate*totalQuantity;
				}
				
				Float ebayFee=paypalFee+dealRate;
				
				String temp = country.toUpperCase();
				if("UK".equals(temp)){
					temp = "GB";
				}
				if("COM".equals(temp)){
					temp = "US";
				}
				CountryCode vatCode = CountryCode.valueOf(temp);
				
				Float salesNoTax=totalMoney/(1+(vatCode.getVat()/100f));
				Float profit=salesNoTax-ebayFee-totalTranFee-totalPurchasePrice-shippingPrice;
				EbayProductProfit profitEnty=ebayOrderService.findProfit(date,productName,country);
				if(profitEnty==null){
					profitList.add(new EbayProductProfit(productName,country,date,totalQuantity,totalMoney,salesNoTax,totalTranFee,totalPurchasePrice,ebayFee,profit,shippingPrice));
				}else{
					if(totalQuantity!=profitEnty.getSalesVolume()){
						profitEnty.setProfits(profit);
						profitEnty.setPrice(shippingPrice);
						profitEnty.setEbayFee(ebayFee);
						profitEnty.setBuyCost(totalPurchasePrice);
						profitEnty.setTransportFee(totalTranFee);
						profitEnty.setSalesNoTax(salesNoTax);
						profitEnty.setSales(totalMoney);
						profitEnty.setSalesVolume(totalQuantity);
						profitList.add(profitEnty);
					}
				}
				
			}
			if(profitList!=null&&profitList.size()>0){
				logger.info("save");
				ebayOrderService.saveProfit(profitList);
			}
		}
	}
	
	public void saveEbayPrice(){
		Map<String, PsiProductTransportRate>  countryTranFee = psiTransportPaymentService.findTransportPriceByToCountry();
		Map<String, Map<String, PsiProductTransportRate>> productTranFee = psiTransportPaymentService.findTransportRateAndPrice();
		Map<String, Map<String,Float>> costAndGw = saleReportService.getProductPriceAndTranGw("USD",null);
		Map<String,Map<String,Object>> purchasePriceMap=psiProductAttributeService.findBeforePrice();
		Map<String,Map<String,Object>> productMap=psiProductService.getProductTypeAndWeight();
		List<String> countryList = Lists.newArrayList("de","com");
		Map<String, Float> vat = Maps.newHashMap();
		Date date=new Date();
		List<EbayProductPrice> priceList=Lists.newArrayList();
		for (String country : countryList) {
			String temp = country.toUpperCase();
			if("UK".equals(temp)){
				temp = "GB";
			}
			if("COM".equals(temp)){
				temp = "US";
			}
			CountryCode vatCode = CountryCode.valueOf(temp);
			if(vatCode!=null){
				vat.put(country, vatCode.getVat()/100f);
			}
		}

		for (Map.Entry<String,Map<String,Object>> entry : productMap.entrySet()) { 
			String productName=entry.getKey();
			Map<String,Object> dutMap=entry.getValue();
			for (String country: countryList) {
				if(productName==null || "Inateck Old".equals(productName)|| "Inateck other".equals(productName)){
					continue;
				}
				Map<String, PsiProductTransportRate> tranPrice = productTranFee.get(productName);
				if(tranPrice==null){
					tranPrice = countryTranFee;
				}
			
				String key = "";
				Float duty=0f;
				String currency="";
				if("uk,it,es,fr,de".contains(country)){
					key = "EU";
					duty=(Float)dutMap.get("euDuty")/100;
					currency="EUR";
				}else{
					key = "US";
					duty=(Float)dutMap.get("usDuty")/100;
					currency="USD";
				}
                Float rate=MathUtils.getRate("USD",currency, null);
                Float cnyRate=MathUtils.getRate("CNY",currency, null);
				Float avgTranFee=0f;
				Float seaTranFee=0f;
				Float skyTranFee=0f;
				Float tranGw=(Float)dutMap.get("tranGw");
				if(tranPrice!=null){
					if(tranPrice.get(key)!=null){
						skyTranFee = tranPrice.get(key).getAirPrice();
						if(!"ca".equals(country)){
							seaTranFee = tranPrice.get(key).getSeaPrice();
							avgTranFee = tranPrice.get(key).getAvgPrice();
						}else{
							seaTranFee = skyTranFee;
							avgTranFee = skyTranFee;
						}
						if(StringUtils.isEmpty(tranPrice.get(key).getProductName())){
							if(seaTranFee!=null){
								seaTranFee = seaTranFee*tranGw;
							}
							if(skyTranFee!=null){
								skyTranFee = skyTranFee*tranGw;
							}
							if(avgTranFee!=null){
								avgTranFee = avgTranFee*tranGw;
							}
						}
					}
				}
				if((seaTranFee==null||seaTranFee==0f)&&countryTranFee!=null&&countryTranFee.get(key)!=null&&countryTranFee.get(key).getSeaPrice()!=null){
					seaTranFee = countryTranFee.get(key).getSeaPrice()*tranGw;
				}
				
				if((skyTranFee==null||skyTranFee==0f)&&countryTranFee!=null&&countryTranFee.get(key)!=null&&countryTranFee.get(key).getAirPrice()!=null){
					skyTranFee = countryTranFee.get(key).getAirPrice()*tranGw;
				}
				
				if((avgTranFee==null||avgTranFee==0f)&&countryTranFee!=null&&countryTranFee.get(key)!=null&&countryTranFee.get(key).getAvgPrice()!=null){
					avgTranFee = countryTranFee.get(key).getAvgPrice()*tranGw;
				}
				
				
				if(seaTranFee==null||seaTranFee==0f){
					seaTranFee = ProductPrice.sea.get(country)*tranGw;
				}
				if(skyTranFee==null||skyTranFee==0f){
					skyTranFee = ProductPrice.sky.get(country)*tranGw;
				}
				if(avgTranFee==null||avgTranFee==0f){
					avgTranFee = ProductPrice.tranFee.get(country)*tranGw;
				}
				Float tempTranPrice=avgTranFee*cnyRate;
			  //EstimatedShipmentWeight 1POUNDS FBAPerUnitFulfillmentFee 0.75 FBATransportationFee 0.45 FBAPerOrderFulfillmentFee 4.75
			    if("com".equals(country)){
			    	avgTranFee=avgTranFee*cnyRate+0.75f+MathUtils.roundUp(tranGw*2.2046226)*0.45f+4.75f;
			    	skyTranFee=skyTranFee*cnyRate+0.75f+MathUtils.roundUp(tranGw*2.2046226)*0.45f+4.75f;
			    	seaTranFee=seaTranFee*cnyRate+0.75f+MathUtils.roundUp(tranGw*2.2046226)*0.45f+4.75f;
			    }else{
			    	avgTranFee=avgTranFee*cnyRate+3.99f;
			    	skyTranFee=skyTranFee*cnyRate+3.99f;
			    	seaTranFee=seaTranFee*cnyRate+3.99f;
			    }
			    
			    Float purchasePrice=0f;
			    Float partsPrice=0f;
			    Float purchasePrice2=0f;
			    if(costAndGw.get(productName)!=null){
			    	if(costAndGw.get(productName).get("parts")!=null){
			    		partsPrice=costAndGw.get(productName).get("parts");
			    	}
			    	if(costAndGw.get(productName).get("price")!=null){
			    		purchasePrice2=costAndGw.get(productName).get("price");
			    	}
			    }
			    if(purchasePriceMap.get(productName)!=null&&purchasePriceMap.get(productName).get("price")!=null){
			    	Float beforePrice=(Float)purchasePriceMap.get(productName).get("price");
			    	purchasePrice=beforePrice+partsPrice;
			    }else{
			    	purchasePrice=purchasePrice2+partsPrice;
			    }
			    
			    if(purchasePrice==0f){
					continue;
				}
			    if("de".equals(country)){
			    	purchasePrice=purchasePrice*rate;
			    }
			    //ebay成交费：a.电脑配件（Hard Drives /HDD/SDD）：4%  b.数据线，内胆包：9%  
			    //c.消费电子（耳机，音响，hub，激光笔）：6%  d.家居（台灯，香薰机）：9%  e:Business & Industrial(扫描枪)：9%
			    Float dealRate=0f;
			    String type=(String)dutMap.get("type");
			    if("HDD Adapter".equals(type)||"HDD case".equals(type)||"HDD enclosures".equals(type)){
			    	dealRate=0.04f*rate;
			    }else if("Headset".equals(type)||"Hub".equals(type)||"Earphone".equals(type)||"Speaker".equals(type)||"Wireless presenter".equals(type)){
			    	dealRate=0.06f*rate;
			    }else{
			    	dealRate=0.09f*rate;
			    }
			    
			    //paypal成交费:6.0% + 0.05  
			    // A(1+关税)+B*ebay成交费+B*paypal成交费+运费=B/(1+VAT) A(1+关税)+运费=B/(1+VAT)-B*ebay成交费-B*paypal成交费
			    Double safePrice=(purchasePrice*(1+duty)+avgTranFee+0.05*rate)/( 1/(1+vat.get(country))-dealRate-0.06*rate);
			    Double skyPrice=(purchasePrice*(1+duty)+skyTranFee+0.05*rate)/( 1/(1+vat.get(country))-dealRate-0.06*rate);
			    Double seaPrice=(purchasePrice*(1+duty)+seaTranFee+0.05*rate)/( 1/(1+vat.get(country))-dealRate-0.06*rate);
			    priceList.add(new EbayProductPrice(productName,country,avgTranFee,tranGw,safePrice,date,skyPrice,seaPrice,purchasePrice,tempTranPrice));
			}
		}
		
		if(priceList!=null&&priceList.size()>0){
			ebayOrderService.savePrice(priceList);
		}
	}
	
	
	
	public void saveSupportOrRefundOrEventComment(){
		logger.info("测评替代start");
		try{
			List<AmazonOutboundOrder> outboudnOrder=amazonOutboundOrderService.findAllCompleteOrders();
			Map<String,AmazonCustomer> customMap=Maps.newHashMap();
			for(AmazonOutboundOrder order:outboudnOrder){
				if(amazonOutboundOrderService.isExistCommentOrder(order.getSellerOrderId(),"3")){//no exist
					 if(StringUtils.isNotBlank(order.getBuyerEmail())){
						 try{
								AmazonCustomer customer =  amazonCustomerService.getByEg(order.getBuyerEmail());
								if(customer!=null){
									if(customMap.get(order.getCustomId())!=null){
										AmazonCustomer tempCustomer=customMap.get(order.getCustomId());
										int totalQuantity=0;
										List<AmazonBuyComment> list = tempCustomer.getBuyComments();
										for (AmazonOutboundOrderItem item : order.getItems()) {
											totalQuantity+=item.getQuantityOrdered();
											list.add(new AmazonBuyComment(new Date(),order.getCreateDate(), "3", order.getSellerOrderId(), item.getAsin(), item.getSellersku(), item.getName(),item.getQuantityOrdered(), item.getId(),order.getRemark(), customer));
										}
										tempCustomer.setSupportQuantity(tempCustomer.getSupportQuantity()+totalQuantity);
										customMap.put(order.getCustomId(), tempCustomer);
									}else{
										List<AmazonBuyComment> list = customer.getBuyComments();
										if(list==null){
											list = Lists.newArrayList();
											customer.setBuyComments(list);
										}
										int totalQuantity=0;
										for (AmazonOutboundOrderItem item : order.getItems()) {
											totalQuantity+=item.getQuantityOrdered();
											list.add(new AmazonBuyComment(new Date(),order.getCreateDate(), "3", order.getSellerOrderId(), item.getAsin(), item.getSellersku(), item.getName(),item.getQuantityOrdered(), item.getId(),order.getRemark(), customer));
										}
										customer.setSupportQuantity(customer.getSupportQuantity()+totalQuantity);
										customMap.put(order.getCustomId(), customer);
									}
								}
						 }catch(Exception e){
							 logger.warn(e.getMessage(), e);
						 }
							
					 }
				}
			}
			if(customMap!=null&&customMap.size()>0){
				List<AmazonCustomer> amazonCustomerList=Lists.newArrayList();
				for (Map.Entry<String,AmazonCustomer> entry : customMap.entrySet()) {  
					amazonCustomerList.add(entry.getValue());
				}
				amazonCustomerService.save(amazonCustomerList);
			}
		}catch(Exception e){
			 logger.warn("测评替代"+e.getMessage(), e);
		}
		
		logger.info("测评替代end");
	/*	try{
			saveRefundComment();
		}catch(Exception e){
			logger.warn("退款"+e.getMessage(), e);
		}*/
		
		logger.info("evevt start");
		try{
			Map<String,String> eventMap=eventService.getEventByCustomId();
			//List<AmazonCustomer> customerEventList=Lists.newArrayList();
			Map<String,AmazonCustomer> customerMap=Maps.newHashMap();
			if(eventMap!=null&&eventMap.size()>0){
				for (Map.Entry<String,String> entry : eventMap.entrySet()) { 
				    String customId=entry.getKey();
					AmazonCustomer customer =  amazonCustomerService.getByEg(customId);
					if(customer!=null){
						if(customerMap.get(customId)!=null){
							AmazonCustomer tempCustomer=customerMap.get(customId);
							
							if(StringUtils.isBlank(tempCustomer.getEventId())){
								tempCustomer.setEventId(entry.getValue());
							}else{
								String oldEventId=tempCustomer.getEventId();
								String newEventId=entry.getValue();
								String[] newArr=newEventId.split(",");
								String tempId=oldEventId;
								StringBuffer buf= new StringBuffer(oldEventId);
								for (String newId : newArr) {
									boolean flag=true;
									if(oldEventId.contains(newId+",")||oldEventId.contains(","+newId)||oldEventId.contains(","+newId+",")||oldEventId.equals(newId)){
										flag=false;
										break;
									}
									if(flag){
										buf.append(","+newId);
									}
								}
								tempId=buf.toString();
								tempCustomer.setEventId(tempId);
							}
							customerMap.put(customId, tempCustomer);
						}else{
							if(StringUtils.isBlank(customer.getEventId())){
								customer.setEventId(eventMap.get(customId));
							}else{
								String oldEventId=customer.getEventId();
								String newEventId=eventMap.get(customId);
								String[] newArr=newEventId.split(",");
								String tempId="";
								StringBuffer buf= new StringBuffer(oldEventId);
								for (String newId : newArr) {
									boolean flag=true;
									if(oldEventId.contains(newId+",")||oldEventId.contains(","+newId)||oldEventId.contains(","+newId+",")||oldEventId.equals(newId)){
										flag=false;
										break;
									}
									if(flag){
										buf.append(","+newId);
									}
								}
								tempId=buf.toString();
								customer.setEventId(tempId);
							}
							customerMap.put(customId, customer);
						}
						//customerEventList.add(customer);
					}
				}
			}
			if(customerMap!=null&&customerMap.size()>0){
			//	List<AmazonCustomer> customerEventList=(List<AmazonCustomer>) customerMap.values();
				List<AmazonCustomer> customerEventList=Lists.newArrayList();
				for (Map.Entry<String,AmazonCustomer> entry : customerMap.entrySet()) { 
					customerEventList.add(entry.getValue());
				}
				amazonCustomerService.save(customerEventList);
			}
		}catch(Exception e){
			logger.warn("event"+e.getMessage(), e);
		}
		logger.info("evevt end");
	}
	
	
	
	public void saveRefundComment(){
		List<AmazonRefund> refundList=amazonRefundService.getAllRefundRecord();
		Map<String,AmazonCustomer> customerMap=Maps.newHashMap();
		for (AmazonRefund amazonRefund : refundList) {
			if(amazonOutboundOrderService.isExistCommentOrder(amazonRefund.getAmazonOrderId(),"4")){
				AmazonOrder amazonOrder=amazonOrderService.findByEg(amazonRefund.getAmazonOrderId());
				try{
					if(amazonRefund.getResult()!=null&&amazonRefund.getResult().contains("&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;")){
						String amzEmail = amazonOrder.getBuyerEmail();
						if(StringUtils.isNotBlank(amzEmail)){
							AmazonCustomer customer =  amazonCustomerService.getByEg(amzEmail);
							if(customer!=null){
								if(customerMap.get(amzEmail)!=null){
									AmazonCustomer tempCustomer=customerMap.get(amzEmail);
									List<AmazonBuyComment> list = tempCustomer.getBuyComments();
									for (AmazoninfoRefundItem item : amazonRefund.getItems()) {
										list.add(new AmazonBuyComment(new Date(),amazonRefund.getCreateDate(), "4", amazonRefund.getAmazonOrderId(), item.getAsin(), item.getSku(), item.getProductName(),item.getMoney(), item.getId(),  item.getRemark(), customer));
									}
									tempCustomer.setRefundMoney(tempCustomer.getRefundMoney()+customer.getRefundMoney());
									customerMap.put(amzEmail, tempCustomer);
								}else{
									customer.setRefundMoney(customer.getRefundMoney()+amazonRefund.getRefundTotal());
									List<AmazonBuyComment> list = customer.getBuyComments();
									if(list==null){
										list = Lists.newArrayList();
										customer.setBuyComments(list);
									}
									for (AmazoninfoRefundItem item : amazonRefund.getItems()) {
										list.add(new AmazonBuyComment(new Date(),amazonRefund.getCreateDate(), "4", amazonRefund.getAmazonOrderId(), item.getAsin(), item.getSku(), item.getProductName(),item.getMoney(), item.getId(),  item.getRemark(), customer));
									}
									customerMap.put(amzEmail, customer);
								}
							}
						}
					}
					
				}catch(Exception e){
					logger.warn(e.getMessage(), e);
				}
			}
		}
		if(customerMap!=null&&customerMap.size()>0){
			List<AmazonCustomer> amazonCustomerList=Lists.newArrayList();
			for (Map.Entry<String,AmazonCustomer> entry : customerMap.entrySet()) { 
				amazonCustomerList.add(entry.getValue());
			}
			amazonCustomerService.save(amazonCustomerList);
		}
	}
	
	
	public Date xmlGregorianToLocalDate(XMLGregorianCalendar calendar,String country) {
		DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(calendar!=null){
			Date date =  calendar.toGregorianCalendar().getTime();
			if("de,it,es,fr".contains(country)){
				sdf.setTimeZone(AmazonWSConfig.get("de").getTimeZone());
			}else{
				sdf.setTimeZone(AmazonWSConfig.get(country).getTimeZone());
			}
			String time = sdf.format(date);
			sdf.setTimeZone(TimeZone.getDefault());
			try {
				return sdf.parse(time);
			} catch (ParseException e) {}
		}
		return null;
	}
	
	
	public void saveHistoryMonitor(){
		try{
			logger.info("ebay price start");
			saveEbayPrice();
			logger.info("ebay price  end");
		}catch(Exception ex){
			logger.error("ebay price：",ex);
		}
		try{
			saveEbayProfit();
		}catch(Exception ex){
			logger.error("ebay profit：",ex);
		}
		try{
			logger.info("运营数据start");
			amazonOperationReportService.saveHistoryData();
			amazonOperationReportService.updateAndSaveByType();
			logger.info("运营数据end");
		}catch(Exception ex){
			logger.error("运营数据：",ex);
			ex.printStackTrace();
		}
		try{
			 returnGoodsService.deleteReturnGoodsErrorData();
			 amazonOrderService.saveReturnsAndBadReview();
		}catch(Exception ex){
				logger.error("统计退货率差评：",ex);
		}
		
		try{
			logger.info("测评替代退款订单start");
			saveSupportOrRefundOrEventComment();
			logger.info("测评替代退款订单end");
		}catch(Exception e){
			logger.error("测评替代退款订单：",e);
		}
		
		
		try{
			saleAnalysisReportService.updateAndSaveReportData();
		}catch(Exception e){
			logger.error("运营分析数据：",e);
		}
		
		try{
			String resultBalance = UspsL5Service.postageBalance();
			JSONObject object = (JSONObject) JSON.parse(resultBalance);
			Integer total = object.getInteger("postageBalance");
			if(total<50000){
				UspsL5Service.purchasePostage(100000-total);
			}
		}catch(Exception e){
			logger.error("自动充值：",e);
		}
	}
	
	public void initDate(){
		
		try{
			logger.info("广告运营数据start");
			amazonOperationReportService.saveSpreadDate();
			logger.info("广告运营数据end");
		}catch(Exception ex){
			logger.error("广告运营数据："+ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	
	public AmazonOutboundOrderService getAmazonOutboundOrderService() {
		return amazonOutboundOrderService;
	}



	public void setAmazonOutboundOrderService(
			AmazonOutboundOrderService amazonOutboundOrderService) {
		this.amazonOutboundOrderService = amazonOutboundOrderService;
	}



	public AmazonOperationReportService getAmazonOperationReportService() {
		return amazonOperationReportService;
	}

	public void setAmazonOperationReportService(
			AmazonOperationReportService amazonOperationReportService) {
		this.amazonOperationReportService = amazonOperationReportService;
	}
	
	
	
}
