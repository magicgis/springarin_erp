package com.springrain.erp.modules.amazoninfo.web.order;

import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.config.LogisticsSupplier;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.ReturnGoods;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOutboundAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOutboundOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOutboundOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrderItem;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AwsAdverstingService;
import com.springrain.erp.modules.amazoninfo.service.ReturnGoodsService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOutboundOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonUnlineOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.MfnOrderService;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.ebay.entity.EbayOrder;
import com.springrain.erp.modules.ebay.entity.EbayOrderItem;
import com.springrain.erp.modules.ebay.service.EbayOrderService;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.webservice.OutboundOrder;


@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/amazonTestOrReplace")
public class AmazonOutboundOrderController extends BaseController {
	@Autowired
	private AmazonOutboundOrderService amazonOutboundOrderService;
	@Autowired
	private PsiProductService psiProductService;
	@Autowired
	private AmazonOrderService amazonOrderService;
	@Autowired
	private PsiInventoryService	psiInventoryService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private MailManager              mailManager;
	@Autowired
	private ReturnGoodsService returnGoodsService;
	@Autowired
	private SaleReportService saleReportService; 
	@Autowired
	private MfnOrderService mfnOrderService;
	@Autowired
	private AmazonUnlineOrderService amazonUnlineOrderService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private  EbayOrderService ebayOrderService;
	@Autowired
	private EventService eventService;
	@Autowired
	private AwsAdverstingService awsAdverstingService;
	
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	private  final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private static Map<String,String> countryNameMap;
		
	static{
			countryNameMap=Maps.newHashMap();
			countryNameMap.put("德国","de");
			countryNameMap.put("法国","fr");
			countryNameMap.put("意大利","it");
			countryNameMap.put("西班牙","es");
			countryNameMap.put("英国","uk");
			countryNameMap.put("美国","com");
			countryNameMap.put("加拿大","ca");
			countryNameMap.put("日本","jp");
			countryNameMap.put("墨西哥","mx");
	}
		
	/*@ModelAttribute
	public AmazonOutboundOrder get(@RequestParam(required = false) Integer id) {
		if (id != null) {
			return amazonOutboundOrderService.get(id);
		} else {
			return new AmazonOutboundOrder();
		}
	}*/
	
	@RequestMapping(value = "synchronizeMfnOrder")
	@ResponseBody
	public String synchronizeMfnOrder(Integer mfnId){
		AmazonOutboundOrder amazonOutboundOrder=amazonOutboundOrderService.get(mfnId);
		boolean flag=mfnOrderService.synchronizeMfnOrder(amazonOutboundOrder);
		if(flag){
			return "1";
		}else{
			return "0";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = {"getAddrInfo"})
	public AmazonOutboundAddress getAddrInfo(String addrName) {
		return this.amazonOutboundOrderService.findByName(HtmlUtils.htmlUnescape(addrName));
	}
	
	@RequestMapping(value = "synchronizeOrder")
	@ResponseBody
	public void synchronizeOrder(AmazonOutboundOrder amazonOutboundOrder,Model model){
		try{
			AmazonAccountConfig config=amazonAccountConfigService.getByName(amazonOutboundOrder.getAccountName());
			String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"),amazonOutboundOrder.getId(),config.getAccountName()};
			client.invoke("getAmazonOutboundOrder", str);
		}catch(Exception e){}
	}
	
	@RequestMapping(value = "view")
	public String view(AmazonOutboundOrder amazonOutboundOrder, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		try{
			if(amazonOutboundOrder.getId()!=null){
				amazonOutboundOrder= amazonOutboundOrderService.get(amazonOutboundOrder.getId());
			}else{
				String orderId=amazonOutboundOrder.getSellerOrderId();
				if(StringUtils.isNotBlank(orderId)){
					amazonOutboundOrder= amazonOutboundOrderService.getOrderByOrderId(orderId);
					if(amazonOutboundOrder==null){
						amazonOutboundOrder= amazonOutboundOrderService.getOrderByOldOrderId(orderId);
					}
				}
			}
		}catch(Exception e){}
		if(amazonOutboundOrder==null){
			throw new RuntimeException("Order does not exist");
		}
		model.addAttribute("amazonOutboundOrder",amazonOutboundOrder);
		if(amazonOutboundOrder!=null){
			Map<String,ReturnGoods> returnGoods=returnGoodsService.getReturnGoodsByOrderId(amazonOutboundOrder.getSellerOrderId());
			model.addAttribute("returnGoods", returnGoods);
		}
		model.addAttribute("site",LogisticsSupplier.getWebSite());
		return "/modules/amazoninfo/order/amazonTestOrReplaceOrderView";
	}

	@RequestMapping(value = "")
	public String list(AmazonOutboundOrder amazonOutboundOrder, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<AmazonOutboundOrder> page = new Page<AmazonOutboundOrder>(request, response);
		if (amazonOutboundOrder.getCreateDate()== null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			amazonOutboundOrder.setCreateDate(DateUtils.addDays(today, -30));
			amazonOutboundOrder.setLastUpdateDate(today);
		}
		/*if(StringUtils.isBlank(amazonOutboundOrder.getCountry())){
			amazonOutboundOrder.setCountry("de");
		}*/
		
		User user = UserUtils.getUser();
		if (amazonOutboundOrder.getCreateUser()==null){
			amazonOutboundOrder.setCreateUser(user);
		} 
		model.addAttribute("cuser",user);
		
		page = amazonOutboundOrderService.find(page, amazonOutboundOrder);
		model.addAttribute("amazonOutboundOrder", amazonOutboundOrder);
		model.addAttribute("page", page);
		
	
		
		return "/modules/amazoninfo/order/amazonTestOrReplaceOrderList";
	}

	
	@RequestMapping(value = "updateFulfillmentAction")
	public String updateFulfillmentAction(AmazonOutboundOrder amazonOutboundOrder, RedirectAttributes redirectAttributes){//Ship - 立即配送   Hold - 暂缓配送
		amazonOutboundOrder=amazonOutboundOrderService.get(amazonOutboundOrder.getId());
		AmazonAccountConfig config=amazonAccountConfigService.getByName(amazonOutboundOrder.getAccountName());
		String rs=amazonOutboundOrderService.updateSave(amazonOutboundOrder,config);
		if(StringUtils.isNotBlank(rs)){
			addMessage(redirectAttributes,"Hold订单"+amazonOutboundOrder.getSellerOrderId()+"更新成Ship不成功"+rs);
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonTestOrReplace/?country="+amazonOutboundOrder.getCountry()+"&fulfillmentAction="+amazonOutboundOrder.getFulfillmentAction()+"&repage";
	}
	
	@RequestMapping(value = "cancelOrder")
	public String cancelOrder(AmazonOutboundOrder amazonOutboundOrder, RedirectAttributes redirectAttributes){//Ship - 立即配送   Hold - 暂缓配送
		amazonOutboundOrder=amazonOutboundOrderService.get(amazonOutboundOrder.getId());
		AmazonAccountConfig config=amazonAccountConfigService.getByName(amazonOutboundOrder.getAccountName());
		String rs=amazonOutboundOrderService.cancelSave(amazonOutboundOrder,config);
		if(StringUtils.isNotBlank(rs)){
			addMessage(redirectAttributes,"订单"+amazonOutboundOrder.getSellerOrderId()+"取消不成功"+rs);
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonTestOrReplace/?country="+amazonOutboundOrder.getCountry()+"&repage";
	}
	
	
	@RequestMapping(value = "deleteOrder")
	public String deleteOrder(AmazonOutboundOrder amazonOutboundOrder, RedirectAttributes redirectAttributes){//Ship - 立即配送   Hold - 暂缓配送
		amazonOutboundOrder=amazonOutboundOrderService.get(amazonOutboundOrder.getId());
		amazonOutboundOrderService.cancelOrderAndEvent(amazonOutboundOrder);
		addMessage(redirectAttributes,"订单"+amazonOutboundOrder.getSellerOrderId()+"删除成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonTestOrReplace/?country="+amazonOutboundOrder.getCountry()+"&repage";
	}
	
	@RequestMapping(value = "isShipped")
	@ResponseBody
	public String isShipped(AmazonOutboundOrder amazonOutboundOrder) {
		amazonOutboundOrder=amazonOutboundOrderService.get(amazonOutboundOrder.getId());
		boolean flag=mfnOrderService.isShippedOrder(amazonOutboundOrder.getSellerOrderId());
		if(flag){
			return "0";//true已发货 不能删
		}
		return "1";
	}
	
	
	@RequestMapping(value = "findOrderInfo")
	@ResponseBody
	public Map<String,String> findOrderInfo(AmazonOutboundOrder amazonOutboundOrder) {
		Map<String,String> map=Maps.newHashMap();
		AmazonOrder amazonOrder =amazonOrderService.findByEg(amazonOutboundOrder.getAmazonOrderId());
		if(amazonOrder==null){
			map.put("errorMsg","没查询到订单信息");
			return map;
		}
		if(!amazonOrder.getCountryChar().equals(amazonOutboundOrder.getCountry())){
			map.put("errorMsg","订单号和选择的国家不一致");
			return map;
		}
		map.put("buyerUser", amazonOrder.getBuyerName());
		map.put("buyerUserEmail", amazonOrder.getBuyerEmail());
		if(amazonOrder.getShippingAddress()!=null){
			map.put("street1", amazonOrder.getShippingAddress().getAddressLine2());
			map.put("street", amazonOrder.getShippingAddress().getAddressLine1());
			map.put("street2", amazonOrder.getShippingAddress().getAddressLine3());
			map.put("cityName", amazonOrder.getShippingAddress().getCity());
			map.put("country", amazonOrder.getShippingAddress().getCounty());
			map.put("stateOrProvince", amazonOrder.getShippingAddress().getStateOrRegion());
			map.put("countryCode", amazonOrder.getShippingAddress().getCountryCode());
			map.put("postalCode", amazonOrder.getShippingAddress().getPostalCode());
			map.put("phone", amazonOrder.getShippingAddress().getPhone());
			map.put("name", amazonOrder.getShippingAddress().getName());
		}
		map.put("customerId", amazonOrderService.getCustomIdByOrderId(amazonOrder.getAmazonOrderId()));
		for (AmazonOrderItem item : amazonOrder.getItems()) {
			map.put("productName", item.getProductName());
			map.put("asin", item.getAsin());
			map.put("sku", item.getSellersku());
			map.put("color",item.getColor());
			Map<String,PsiInventoryFba>  amazonStock=psiInventoryService.getProductFbaInfo(item.getName());
			if("de,fr,it,es,uk".contains(amazonOutboundOrder.getCountry())){
				
				Map<String,Integer> deStock=psiProductService.findNewQuantityByCn(19);
				Set<String>   asinList=saleReportService.getPanEuProductAsin();
				Map<String, String> powerMap=psiProductService.getHasPowerByName();
				if(asinList.contains(item.getAsin())){
					if(amazonStock!=null&&amazonStock.get(item.getName()+"_eu")!=null){
						map.put("quantity",amazonStock.get(item.getName()+"_eu").getRealTotal()+"");
					}
					String name=item.getName();
					Integer localStock=0;
					if(deStock.get(name+"_de")!=null){
                		localStock+=deStock.get(name+"_de");
					}
                	if(deStock.get(name+"_fr")!=null){
                		localStock+=deStock.get(name+"_fr");
					}
                	if(deStock.get(name+"_it")!=null){
                		localStock+=deStock.get(name+"_it");
					}
                	if(deStock.get(name+"_es")!=null){
                		localStock+=deStock.get(name+"_es");
					}
                	if(deStock.get(name+"_uk")!=null){
                		localStock+=deStock.get(name+"_uk");
					}
                	map.put("localQuantity",localStock+"");
				}else{
					if(amazonStock!=null&&amazonStock.get(item.getName()+"_"+amazonOutboundOrder.getCountry())!=null){
						map.put("quantity",amazonStock.get(item.getName()+"_"+amazonOutboundOrder.getCountry()).getRealTotal()+"");
					}
					Integer localStock=0;
					String name=item.getName();
                    if("0".equals(powerMap.get(name))){//0:不带电
                    	if(deStock.get(name+"_de")!=null){
                    		localStock+=deStock.get(name+"_de");
						}
                    	if(deStock.get(name+"_fr")!=null){
                    		localStock+=deStock.get(name+"_fr");
						}
                    	if(deStock.get(name+"_it")!=null){
                    		localStock+=deStock.get(name+"_it");
						}
                    	if(deStock.get(name+"_es")!=null){
                    		localStock+=deStock.get(name+"_es");
						}
                    	if(deStock.get(name+"_uk")!=null){
                    		localStock+=deStock.get(name+"_uk");
						}
					}else{
						if("uk".equals(amazonOutboundOrder.getCountry())){
							localStock=deStock.get(name+"_uk");
						}else{
							if(deStock.get(name+"_de")!=null){
                        		localStock+=deStock.get(name+"_de");
							}
                        	if(deStock.get(name+"_fr")!=null){
                        		localStock+=deStock.get(name+"_fr");
							}
                        	if(deStock.get(name+"_it")!=null){
                        		localStock+=deStock.get(name+"_it");
							}
                        	if(deStock.get(name+"_es")!=null){
                        		localStock+=deStock.get(name+"_es");
							}
						}
					}
                    map.put("localQuantity",localStock+"");
				}
				
			}else if(amazonOutboundOrder.getCountry().contains("com")){
				Map<String,Integer> stockMap=psiProductService.findNewQuantityByCn(120);
				Integer localStock=0;
				String name=item.getName();
                if(stockMap.get(name+"_"+amazonOutboundOrder.getCountry())!=null){
                	localStock+=stockMap.get(name+"_"+amazonOutboundOrder.getCountry());
				}
                map.put("localQuantity",localStock+"");	
				if(amazonStock!=null&&amazonStock.get(item.getName()+"_"+amazonOutboundOrder.getCountry())!=null){
					map.put("quantity",amazonStock.get(item.getName()+"_"+amazonOutboundOrder.getCountry()).getRealTotal()+"");
				}
			}else if(amazonOutboundOrder.getCountry().contains("jp")){
				Map<String,Integer> stockMap=psiProductService.findNewQuantityByCn(147);
				Integer localStock=0;
				String name=item.getName();
                if(stockMap.get(name+"_"+amazonOutboundOrder.getCountry())!=null){
                	localStock+=stockMap.get(name+"_"+amazonOutboundOrder.getCountry());
				}
                map.put("localQuantity",localStock+"");	
				if(amazonStock!=null&&amazonStock.get(item.getName()+"_"+amazonOutboundOrder.getCountry())!=null){
					map.put("quantity",amazonStock.get(item.getName()+"_"+amazonOutboundOrder.getCountry()).getRealTotal()+"");
				}
			}else{
				if(amazonStock!=null&&amazonStock.get(item.getName()+"_"+amazonOutboundOrder.getCountry())!=null){
					map.put("quantity",amazonStock.get(item.getName()+"_"+amazonOutboundOrder.getCountry()).getRealTotal()+"");
				}
			}
			
			
			break;
		}
		return map;
	}
	
	
	@RequestMapping(value = "findOrderInfo2")
	@ResponseBody
	public Map<String,String> findOrderInfo2(AmazonOutboundOrder amazonOutboundOrder) {
		Map<String,String> map=Maps.newHashMap();
		AmazonUnlineOrder unlineOrder=amazonUnlineOrderService.getByOrderId(amazonOutboundOrder.getAmazonOrderId());
		if(unlineOrder==null){
			map.put("errorMsg","没查询到订单信息");
			return map;
		}
		
		String unlineCountry="";
		if(unlineOrder.getSalesChannel().getId().intValue()==120){
			unlineCountry="com";
		}else{
			unlineCountry="de";
		}
		if(!unlineCountry.equals(amazonOutboundOrder.getCountry())){
			map.put("errorMsg","订单号和选择的国家不一致");
			return map;
		}
		map.put("buyerUser", unlineOrder.getBuyerName());
		map.put("buyerUserEmail", unlineOrder.getBuyerEmail());
		if(unlineOrder.getShippingAddress()!=null){
			map.put("street1", unlineOrder.getShippingAddress().getAddressLine2());
			map.put("street", unlineOrder.getShippingAddress().getAddressLine1());
			map.put("street2", unlineOrder.getShippingAddress().getAddressLine3());
			map.put("cityName", unlineOrder.getShippingAddress().getCity());
			map.put("country", unlineOrder.getShippingAddress().getCounty());
			map.put("stateOrProvince", unlineOrder.getShippingAddress().getStateOrRegion());
			map.put("countryCode", unlineOrder.getShippingAddress().getCountryCode());
			map.put("postalCode", unlineOrder.getShippingAddress().getPostalCode());
			map.put("phone", unlineOrder.getShippingAddress().getPhone());
			map.put("name", unlineOrder.getShippingAddress().getName());
		}
		map.put("customerId", unlineOrder.getCustomId());
		for (AmazonUnlineOrderItem item : unlineOrder.getItems()) {
			map.put("productName", item.getProductName());
			map.put("asin", item.getAsin());
			map.put("sku", item.getSellersku());
			map.put("color",item.getColor());
			String name=item.getProductName();
			if(StringUtils.isNotBlank(item.getColor())){
				name=item.getProductName()+"_"+item.getColor();
			}
			Map<String,PsiInventoryFba>  amazonStock=psiInventoryService.getProductFbaInfo(name);
			if("de,fr,it,es,uk".contains(amazonOutboundOrder.getCountry())){
				
				Map<String,Integer> deStock=psiProductService.findNewQuantityByCn(19);
				Set<String>   asinList=saleReportService.getPanEuProductAsin();
				Map<String, String> powerMap=psiProductService.getHasPowerByName();
				if(asinList.contains(item.getAsin())){
					if(amazonStock!=null&&amazonStock.get(name+"_eu")!=null){
						map.put("quantity",amazonStock.get(name+"_eu").getRealTotal()+"");
					}
					Integer localStock=0;
					if(deStock.get(name+"_de")!=null){
                		localStock+=deStock.get(name+"_de");
					}
                	if(deStock.get(name+"_fr")!=null){
                		localStock+=deStock.get(name+"_fr");
					}
                	if(deStock.get(name+"_it")!=null){
                		localStock+=deStock.get(name+"_it");
					}
                	if(deStock.get(name+"_es")!=null){
                		localStock+=deStock.get(name+"_es");
					}
                	if(deStock.get(name+"_uk")!=null){
                		localStock+=deStock.get(name+"_uk");
					}
                	map.put("localQuantity",localStock+"");
				}else{
					if(amazonStock!=null&&amazonStock.get(name+"_"+amazonOutboundOrder.getCountry())!=null){
						map.put("quantity",amazonStock.get(name+"_"+amazonOutboundOrder.getCountry()).getRealTotal()+"");
					}
					Integer localStock=0;
                    if("0".equals(powerMap.get(name))){//0:不带电
                    	if(deStock.get(name+"_de")!=null){
                    		localStock+=deStock.get(name+"_de");
						}
                    	if(deStock.get(name+"_fr")!=null){
                    		localStock+=deStock.get(name+"_fr");
						}
                    	if(deStock.get(name+"_it")!=null){
                    		localStock+=deStock.get(name+"_it");
						}
                    	if(deStock.get(name+"_es")!=null){
                    		localStock+=deStock.get(name+"_es");
						}
                    	if(deStock.get(name+"_uk")!=null){
                    		localStock+=deStock.get(name+"_uk");
						}
					}else{
						if("uk".equals(amazonOutboundOrder.getCountry())){
							localStock=deStock.get(name+"_uk");
						}else{
							if(deStock.get(name+"_de")!=null){
                        		localStock+=deStock.get(name+"_de");
							}
                        	if(deStock.get(name+"_fr")!=null){
                        		localStock+=deStock.get(name+"_fr");
							}
                        	if(deStock.get(name+"_it")!=null){
                        		localStock+=deStock.get(name+"_it");
							}
                        	if(deStock.get(name+"_es")!=null){
                        		localStock+=deStock.get(name+"_es");
							}
						}
					}
                    map.put("localQuantity",localStock+"");
				}
				
			}else if(amazonOutboundOrder.getCountry().contains("com")){
				Map<String,Integer> stockMap=psiProductService.findNewQuantityByCn(120);
				Integer localStock=0;
                if(stockMap.get(name+"_"+amazonOutboundOrder.getCountry())!=null){
                	localStock+=stockMap.get(name+"_"+amazonOutboundOrder.getCountry());
				}
                map.put("localQuantity",localStock+"");	
            	if(amazonStock!=null&&amazonStock.get(name+"_"+amazonOutboundOrder.getCountry())!=null){
					map.put("quantity",amazonStock.get(name+"_"+amazonOutboundOrder.getCountry()).getRealTotal()+"");
				}
			}else if(amazonOutboundOrder.getCountry().contains("jp")){
				Map<String,Integer> stockMap=psiProductService.findNewQuantityByCn(147);
				Integer localStock=0;
                if(stockMap.get(name+"_"+amazonOutboundOrder.getCountry())!=null){
                	localStock+=stockMap.get(name+"_"+amazonOutboundOrder.getCountry());
				}
                map.put("localQuantity",localStock+"");	
            	if(amazonStock!=null&&amazonStock.get(name+"_"+amazonOutboundOrder.getCountry())!=null){
					map.put("quantity",amazonStock.get(name+"_"+amazonOutboundOrder.getCountry()).getRealTotal()+"");
				}
			}else{
				if(amazonStock!=null&&amazonStock.get(name+"_"+amazonOutboundOrder.getCountry())!=null){
					map.put("quantity",amazonStock.get(name+"_"+amazonOutboundOrder.getCountry()).getRealTotal()+"");
				}
			}
			
			
			break;
		}
		return map;
	}
	
	
	@RequestMapping(value = "findOrderInfo3")
	@ResponseBody
	public Map<String,String> findOrderInfo3(AmazonOutboundOrder amazonOutboundOrder) {
		Map<String,String> map=Maps.newHashMap();
		EbayOrder ebayOrder=ebayOrderService.getOrder(amazonOutboundOrder.getAmazonOrderId(),amazonOutboundOrder.getCountry());
		if(ebayOrder==null){
			map.put("errorMsg","没查询到订单信息");
			return map;
		}
		amazonOutboundOrder.setCountry(amazonOutboundOrder.getCountry());
		
		map.put("buyerUser", ebayOrder.getBuyerUserId());
		map.put("buyerUserEmail", ebayOrder.getBuyerEmail());
		if(ebayOrder.getShippingAddress()!=null){
			map.put("street1", ebayOrder.getShippingAddress().getStreet1());
			map.put("street", ebayOrder.getShippingAddress().getStreet());
			map.put("street2", ebayOrder.getShippingAddress().getStreet2());
			map.put("cityName", ebayOrder.getShippingAddress().getCityName());
			map.put("country", ebayOrder.getShippingAddress().getCounty());
			map.put("stateOrProvince", ebayOrder.getShippingAddress().getStateOrProvince());
			map.put("countryCode", ebayOrder.getShippingAddress().getCountryCode());
			map.put("postalCode", ebayOrder.getShippingAddress().getPostalCode());
			map.put("phone", ebayOrder.getShippingAddress().getPhone());
			map.put("name", ebayOrder.getShippingAddress().getName());
		}
		return map;
	}

	@RequestMapping(value = "nextSave")
	public String nextSave(AmazonOutboundOrder amazonOutboundOrder,Model model, RedirectAttributes redirectAttributes,String ratingEventId) {
		
		if(!"8".equals(amazonOutboundOrder.getOrderType())){//Review
			amazonOutboundOrder.setOldOrderId(amazonOutboundOrder.getAmazonOrderId());
		}
		Integer quantity=0;
		Map<String,String>  nameMap=amazonProduct2Service.getNameByCountry(amazonOutboundOrder.getCountry());
		for (AmazonOutboundOrderItem item : amazonOutboundOrder.getItems()) {
			if(item!=null){
				String name=nameMap.get(item.getAsin());
				if(StringUtils.isNotBlank(name)){
					if(name.indexOf("_")>0){
						item.setProductName(name.substring(0,name.lastIndexOf("_")));
						item.setColor(name.substring(name.lastIndexOf("_")+1));
					}else{
						item.setProductName(name);
						item.setColor("");
					}
				}
				quantity+=(item.getQuantityOrdered()==null?1:item.getQuantityOrdered());
				item.setOrder(amazonOutboundOrder);
			}
		}
		
		
		if((!"12".equals(amazonOutboundOrder.getEventType())&&(quantity>2||"Expedited".equals(amazonOutboundOrder.getShippingSpeedCategory())))||("12".equals(amazonOutboundOrder.getEventType())&&quantity>2)){
			amazonOutboundOrder.setFulfillmentAction("Hold");
			amazonOutboundOrder.setOrderStatus("Planning");
			Set<User> checkUserSet =Sets.newHashSet();
			List<User> list1 = systemService.findUserByPermission("amazoninfo:refund:"+amazonOutboundOrder.getCountry());
			if (list1 != null && list1.size() > 0) {
				checkUserSet.addAll(list1);
			}
			//全球退款审核人
			List<User> list2 = systemService.findUserByPermission("amazoninfo:refund:all");
			if (list2 != null && list2.size() > 0) {
				checkUserSet.addAll(list2);
			}
			model.addAttribute("checkUserSet", checkUserSet);
		}else{
			amazonOutboundOrder.setFulfillmentAction("Ship");
			amazonOutboundOrder.setOrderStatus("Pending");
		}
		
		String index="";
		if("8".equals(amazonOutboundOrder.getEventType())){//Review//Review(评测) Support(替代)
			amazonOutboundOrder.setOrderType("Marketing");
		}else if("12".equals(amazonOutboundOrder.getEventType())){
			amazonOutboundOrder.setOrderType("Ebay");
			Integer num=amazonOutboundOrderService.findSupport(amazonOutboundOrder.getAmazonOrderId());
			if(num>=1){
				index="-00"+num;
			}
		}else if("15".equals(amazonOutboundOrder.getEventType())){
			amazonOutboundOrder.setOrderType("AmzMfn");
			Integer num=amazonOutboundOrderService.findSupport(amazonOutboundOrder.getAmazonOrderId());
			if(num>=1){
				index="-00"+num;
			}
		}else{
			if("13".equals(amazonOutboundOrder.getEventType())){
				amazonOutboundOrder.setOrderType("Website");
			}else if("14".equals(amazonOutboundOrder.getEventType())){//Offline
				amazonOutboundOrder.setOrderType("Offline");
			}else{
				amazonOutboundOrder.setOrderType("Support");
			}
			
			Integer num=amazonOutboundOrderService.findSupport(amazonOutboundOrder.getAmazonOrderId());
			if(num>=1){
				index="-00"+num;
			}
		}
		if(!"15".equals(amazonOutboundOrder.getEventType())){
			if(StringUtils.isBlank(amazonOutboundOrder.getSellerOrderId())||amazonOutboundOrder.getSellerOrderId().startsWith("Local")){
				String sellerOrderId=("Ebay".equals(amazonOutboundOrder.getOrderType())?"DZW":amazonOutboundOrder.getOrderType())+((!"Marketing".equals(amazonOutboundOrder.getOrderType()))?("-"+amazonOutboundOrder.getAmazonOrderId()+index):("-"+new Date().getTime()));
				amazonOutboundOrder.setSellerOrderId(sellerOrderId);
			}
		}else{
			if(StringUtils.isBlank(amazonOutboundOrder.getSellerOrderId())){
				String sellerOrderId=amazonOutboundOrder.getOrderType()+"-"+amazonOutboundOrder.getAmazonOrderId()+index;
				amazonOutboundOrder.setSellerOrderId(sellerOrderId);
			}
		}
			
		
	//	String sellerOrderId=amazonOutboundOrder.getOrderType()+(!"Review".equals(amazonOutboundOrder.getOrderType())?("-"+amazonOutboundOrder.getAmazonOrderId()+index):("-"+new Date().getTime()));
	//	amazonOutboundOrder.setSellerOrderId(sellerOrderId);
		
	//	amazonOutboundOrder.setDisplayableOrderComment(AmazonOutboundOrder.displayableOrderCommentMap.get(amazonOutboundOrder.getCountry()));
		if(StringUtils.isBlank(amazonOutboundOrder.getDisplayableOrderComment())){
			amazonOutboundOrder.setDisplayableOrderComment(AmazonOutboundOrder.displayableOrderCommentMap.get(amazonOutboundOrder.getCountry()));
		}
	
		//运费
		try{
			AmazonAccountConfig config=amazonAccountConfigService.getByName(amazonOutboundOrder.getAccountName());
			//orderJson
			String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"),toJson(amazonOutboundOrder),amazonOutboundOrder.getAccountName()};
			Object[] res = client.invoke("previewFbaOutBoundOrder", str);
			OutboundOrder rs = (OutboundOrder)res[0];
				
			if(StringUtils.isBlank(rs.getErrorMsg())){
				 amazonOutboundOrder.setWeight(rs.getWeight());
				 amazonOutboundOrder.setFbaPerUnitFulfillmentFee(rs.getFbaPerUnitFulfillmentFee());
				 amazonOutboundOrder.setFbaTransportationFee(rs.getFbaTransportationFee());
				 amazonOutboundOrder.setFbaPerOrderFulfillmentFee(rs.getFbaPerOrderFulfillmentFee());
				
				 amazonOutboundOrder.setEarliestShipDate(rs.getEarliestShipDate());
				 amazonOutboundOrder.setEarliestDeliveryDate(rs.getEarliestDeliveryDate());
				 amazonOutboundOrder.setLatestShipDate(rs.getLatestShipDate());
				 amazonOutboundOrder.setLatestDeliveryDate(rs.getLatestDeliveryDate());
				 
				 model.addAttribute("amazonOutboundOrder", amazonOutboundOrder);
				 model.addAttribute("ratingEventId",ratingEventId);
				 return "/modules/amazoninfo/order/amazonTestOrReplaceOrderFeeView";
			}else{
				model.addAttribute("errorMsg","地址不合法或存在库存为空的产品"+rs.getErrorMsg());
				if(StringUtils.isNotBlank(amazonOutboundOrder.getCountry())){
					List<String> accountNameList=amazonAccountConfigService.findAccountByCountry(amazonOutboundOrder.getCountry());
					model.addAttribute("accountNameList",accountNameList);
					
					List<PsiSku> skuList=psiProductService.getProduct(amazonOutboundOrder.getCountry(),amazonOutboundOrder.getAccountName());
					model.addAttribute("skuList",skuList);
					model.addAttribute("amazonOutboundOrder", amazonOutboundOrder);
					
				}
				model.addAttribute("amazonOutboundOrder", amazonOutboundOrder);
				model.addAttribute("ratingEventId",ratingEventId);
				return "/modules/amazoninfo/order/amazonTestOrReplaceOrderAdd";
			}
		}catch(Exception e){
			logger.error("previewFbaOutBoundOrder",e);
			if(StringUtils.isNotBlank(amazonOutboundOrder.getCountry())){
				List<String> accountNameList=amazonAccountConfigService.findAccountByCountry(amazonOutboundOrder.getCountry());
				model.addAttribute("accountNameList",accountNameList);
				
				List<PsiSku> skuList=psiProductService.getProduct(amazonOutboundOrder.getCountry(),amazonOutboundOrder.getAccountName());
				model.addAttribute("skuList",skuList);
				model.addAttribute("amazonOutboundOrder", amazonOutboundOrder);
				
			}
			model.addAttribute("amazonOutboundOrder", amazonOutboundOrder);
			model.addAttribute("ratingEventId",ratingEventId);
			return "/modules/amazoninfo/order/amazonTestOrReplaceOrderAdd";
		}
	}
	
	@RequestMapping(value = "localSave")
	public String localSave(AmazonOutboundOrder amazonOutboundOrder,Model model, RedirectAttributes redirectAttributes) {
		if(!"8".equals(amazonOutboundOrder.getOrderType())){//Review
			amazonOutboundOrder.setOldOrderId(amazonOutboundOrder.getAmazonOrderId());
		}
		Integer quantity=0;
		Set<Integer> delSet=Sets.newHashSet();
		if(amazonOutboundOrder.getId()!=null&&amazonOutboundOrder.getId()>0){
			AmazonOutboundOrder order=amazonOutboundOrderService.get(amazonOutboundOrder.getId());
            for (AmazonOutboundOrderItem oldItem : order.getItems()) {
            	boolean flag=false;
            	for(AmazonOutboundOrderItem newItem:amazonOutboundOrder.getItems()){
            		if(newItem.getId()!=null&&oldItem.getId().intValue()==newItem.getId().intValue()){
            			flag=true;
            			break;
            		}
            	}
            	if(!flag){
            		delSet.add(oldItem.getId());
            	}
			}
		}
		Map<String,String>  nameMap=amazonProduct2Service.getNameByCountry(amazonOutboundOrder.getCountry());
		
		for (AmazonOutboundOrderItem item : amazonOutboundOrder.getItems()) {
			String name=nameMap.get(item.getAsin());
			if(StringUtils.isNotBlank(name)){
				if(name.indexOf("_")>0){
					item.setProductName(name.substring(0,name.lastIndexOf("_")));
					item.setColor(name.substring(name.lastIndexOf("_")+1));
				}else{
					item.setProductName(name);
					item.setColor("");
				}
			}
			quantity+=item.getQuantityOrdered();
			item.setOrder(amazonOutboundOrder);
		}
		amazonOutboundOrder.setOrderStatus("Draft");
		if(quantity>2||"Expedited".equals(amazonOutboundOrder.getShippingSpeedCategory())){
			amazonOutboundOrder.setFulfillmentAction("Hold");
		}else{
			amazonOutboundOrder.setFulfillmentAction("Ship");
		}
		
		String index="";
		if("8".equals(amazonOutboundOrder.getEventType())){//Review//Review(评测) Support(替代)
			amazonOutboundOrder.setOrderType("Marketing");
		}else if("12".equals(amazonOutboundOrder.getEventType())){//Review//Review(评测) Support(替代)
			amazonOutboundOrder.setOrderType("Ebay");
			Integer num=amazonOutboundOrderService.findSupport(amazonOutboundOrder.getAmazonOrderId());
			if(num>=1){
				index="-00"+num;
			}
		}else{
			if("13".equals(amazonOutboundOrder.getEventType())){
				amazonOutboundOrder.setOrderType("Website");
			}else if("14".equals(amazonOutboundOrder.getEventType())){//Offline
				amazonOutboundOrder.setOrderType("Offline");
			}else{
				amazonOutboundOrder.setOrderType("Support");
			}
			//amazonOutboundOrder.setOrderType("Support");
			Integer num=amazonOutboundOrderService.findSupport(amazonOutboundOrder.getAmazonOrderId());
			if(num>=1){
				index="-00"+num;
			}
		}
		if(StringUtils.isBlank(amazonOutboundOrder.getSellerOrderId())){
			String sellerOrderId="Local"+((!"Marketing".equals(amazonOutboundOrder.getOrderType()))?("-"+amazonOutboundOrder.getAmazonOrderId()+index):("-"+new Date().getTime()));
			amazonOutboundOrder.setSellerOrderId(sellerOrderId);
		}
		if(StringUtils.isBlank(amazonOutboundOrder.getDisplayableOrderComment())){
			amazonOutboundOrder.setDisplayableOrderComment(AmazonOutboundOrder.displayableOrderCommentMap.get(amazonOutboundOrder.getCountry()));
		}
		amazonOutboundOrder.setCreateUser(UserUtils.getUser());
		amazonOutboundOrder.setCreateDate(new Date());
		amazonOutboundOrder.setAmazonOrderId(null);
		amazonOutboundOrder.setLastUpdateDate(new Date());
		amazonOutboundOrderService.save(amazonOutboundOrder);
		try{
			if(delSet!=null&&delSet.size()>0){
				amazonOutboundOrderService.delete(delSet);
			}
		}catch(Exception e){
			LOGGER.info("删除测评订单详情异常",e);
		}
		model.addAttribute("amazonOutboundOrder", amazonOutboundOrder);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonTestOrReplace/?country="+amazonOutboundOrder.getCountry()+"&repage";
	}
	
	
	@RequestMapping(value = "localSave2")
	public String localSave2(AmazonOutboundOrder amazonOutboundOrder,String hasStock,Model model, RedirectAttributes redirectAttributes,String ratingEventId) {
		if(!"8".equals(amazonOutboundOrder.getOrderType())){//Review
			amazonOutboundOrder.setOldOrderId(amazonOutboundOrder.getAmazonOrderId());
		}
		Integer quantity=0;
		Set<Integer> delSet=Sets.newHashSet();
		String reviewLink="";
		 if(StringUtils.isNotBlank(ratingEventId)){
        	try{
        		reviewLink=eventService.get(Integer.parseInt(ratingEventId)).getReviewLink();
    		}catch(Exception e){
    			LOGGER.info("替代差评",e);
    		}
        }
		List<Event> eventList=Lists.newArrayList();
		if(amazonOutboundOrder.getId()!=null&&amazonOutboundOrder.getId()>0){
			AmazonOutboundOrder order=amazonOutboundOrderService.get(amazonOutboundOrder.getId());
            for (AmazonOutboundOrderItem oldItem : order.getItems()) {
            	boolean flag=false;
            	for(AmazonOutboundOrderItem newItem:amazonOutboundOrder.getItems()){
            		if(newItem.getId()!=null&&oldItem.getId().intValue()==newItem.getId().intValue()){
            			flag=true;
            			break;
            		}
            	}
            	if(!flag){
            		delSet.add(oldItem.getId());
            	}
			}
		}else{
			 Map<String,String>  nameMap=amazonProduct2Service.getNameByCountry(amazonOutboundOrder.getCountry());
			 if(!"15".equals(amazonOutboundOrder.getEventType())&&!"12".equals(amazonOutboundOrder.getEventType())){
					for (AmazonOutboundOrderItem item : amazonOutboundOrder.getItems()) {
						String name=nameMap.get(item.getAsin());
						if(StringUtils.isNotBlank(name)){
							if(name.indexOf("_")>0){
								item.setProductName(name.substring(0,name.lastIndexOf("_")));
								item.setColor(name.substring(name.lastIndexOf("_")+1));
							}else{
								item.setProductName(name);
								item.setColor("");
							}
						}
						Event event=new Event();
						event.setAccountName(amazonOutboundOrder.getAccountName());
						event.setCreateBy(UserUtils.getUser());
						event.setCreateDate(new Date());
						event.setUpdateBy(UserUtils.getUser());
						event.setUpdateDate(new Date());
						event.setState("0");
						event.setMasterBy(UserUtils.getUser());
						event.setDelFlag("0");
						event.setType(amazonOutboundOrder.getEventType());
						event.setPriority("1");
						event.setCustomName(amazonOutboundOrder.getBuyerName());
						event.setCustomEmail(amazonOutboundOrder.getBuyerEmail());
						event.setCountry(amazonOutboundOrder.getCountry());
						event.setReason(amazonOutboundOrder.getRemark());
						event.setRemarks(item.getAsin());
						event.setProductName(item.getName());
						event.setCustomId(amazonOutboundOrder.getCustomId());
						if(StringUtils.isNotBlank(ratingEventId)){
							 event.setReviewLink(reviewLink);
						}
						Float price=amazonProduct2Service.getSalePrice(amazonOutboundOrder.getCountry(),item.getAsin());
						if(price!=null){
							event.setReviewPrice(price);
						}
						event.setReviewQuantity(item.getQuantityOrdered());
						if("8".equals(amazonOutboundOrder.getEventType())){//Review
							event.setSubject(DictUtils.getDictLabel(amazonOutboundOrder.getCountry(), "platform", "")+item.getName()+"评测");
						}else if("12".equals(amazonOutboundOrder.getEventType())){
							event.setSubject(DictUtils.getDictLabel(amazonOutboundOrder.getCountry(), "platform", "")+item.getName()+" Ebay Order");
						}else{
							if("13".equals(amazonOutboundOrder.getEventType())){
								event.setSubject(DictUtils.getDictLabel(amazonOutboundOrder.getCountry(), "platform", "")+item.getName()+" Website");
							}else if("14".equals(amazonOutboundOrder.getEventType())){//Offline
								event.setSubject(DictUtils.getDictLabel(amazonOutboundOrder.getCountry(), "platform", "")+item.getName()+" Offline");
							}else{
								event.setSubject(DictUtils.getDictLabel(amazonOutboundOrder.getCountry(), "platform", "")+item.getName()+"替代");
							}
						}
						event.setDescription(amazonOutboundOrder.getRemark());
						event.setInvoiceNumber(amazonOutboundOrder.getSellerOrderId());
						quantity+=item.getQuantityOrdered();
						item.setOrder(amazonOutboundOrder);
						eventList.add(event);
					}
			 }else{
				 for (AmazonOutboundOrderItem item : amazonOutboundOrder.getItems()) {
					 String name=nameMap.get(item.getAsin());
						if(StringUtils.isNotBlank(name)){
							if(name.indexOf("_")>0){
								item.setProductName(name.substring(0,name.lastIndexOf("_")));
								item.setColor(name.substring(name.lastIndexOf("_")+1));
							}else{
								item.setProductName(name);
								item.setColor("");
							}
						}
					 item.setOrder(amazonOutboundOrder);
				 }
			 }
		}
		
	
		amazonOutboundOrder.setOrderStatus("Draft");
		/*if(quantity>2||"Expedited".equals(amazonOutboundOrder.getShippingSpeedCategory())){
			amazonOutboundOrder.setFulfillmentAction("Hold");
		}else{*/
			amazonOutboundOrder.setFulfillmentAction("Ship");
		/*}*/
		if("7".equals(amazonOutboundOrder.getEventType())){
			amazonOutboundOrder.setRemark("Support_Voucher,"+(amazonOutboundOrder.getRemark()==null?"":amazonOutboundOrder.getRemark()));
		}
		String index="";
		if("8".equals(amazonOutboundOrder.getEventType())){//Review//Review(评测) Support(替代)
			amazonOutboundOrder.setOrderType("Marketing");
		}else if("12".equals(amazonOutboundOrder.getEventType())){//Review//Review(评测) Support(替代)
			amazonOutboundOrder.setOrderType("Ebay");
		}else if("15".equals(amazonOutboundOrder.getEventType())){//Review//Review(评测) Support(替代)
			amazonOutboundOrder.setOrderType("AmzMfn");
		}else{
			//amazonOutboundOrder.setOrderType("Support");
			if("13".equals(amazonOutboundOrder.getEventType())){
				amazonOutboundOrder.setOrderType("Website");
			}else if("14".equals(amazonOutboundOrder.getEventType())){//Offline
				amazonOutboundOrder.setOrderType("Offline");
			}else{
				amazonOutboundOrder.setOrderType("Support");
			}
			Integer num=amazonOutboundOrderService.findSupport(amazonOutboundOrder.getAmazonOrderId());
			if(num>=1){
				index="-00"+num;
			}
		}
		if(StringUtils.isBlank(amazonOutboundOrder.getSellerOrderId())||amazonOutboundOrder.getSellerOrderId().startsWith("Local")){
			String sellerOrderId=("Ebay".equals(amazonOutboundOrder.getOrderType())?"DZW":amazonOutboundOrder.getOrderType())+((!"Marketing".equals(amazonOutboundOrder.getOrderType())&&!"Ebay".equals(amazonOutboundOrder.getOrderType()))?("-"+amazonOutboundOrder.getAmazonOrderId()+index):("-"+new Date().getTime()));
			amazonOutboundOrder.setSellerOrderId("MFN-"+sellerOrderId);
		}else{
			if(!amazonOutboundOrder.getSellerOrderId().startsWith("MFN-")){
				amazonOutboundOrder.setSellerOrderId("MFN-"+amazonOutboundOrder.getSellerOrderId());
			}
		}
		
		if(StringUtils.isBlank(amazonOutboundOrder.getDisplayableOrderComment())){
			amazonOutboundOrder.setDisplayableOrderComment(AmazonOutboundOrder.displayableOrderCommentMap.get(amazonOutboundOrder.getCountry()));
		}
		amazonOutboundOrder.setCreateUser(UserUtils.getUser());
		amazonOutboundOrder.setCreateDate(new Date());
		amazonOutboundOrder.setAmazonOrderId(null);
		amazonOutboundOrder.setLastUpdateDate(new Date());
		amazonOutboundOrderService.save2(amazonOutboundOrder,eventList,ratingEventId);
		try{
			if(delSet!=null&&delSet.size()>0){
				amazonOutboundOrderService.delete(delSet);
			}
		}catch(Exception e){
			LOGGER.info("删除测评订单详情异常",e);
		}
		//mfnOrderService.synchronizeMfnOrder(amazonOutboundOrder);
		model.addAttribute("amazonOutboundOrder", amazonOutboundOrder);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonTestOrReplace/?country="+amazonOutboundOrder.getCountry()+"&sellerOrderId="+amazonOutboundOrder.getSellerOrderId()+"&repage";
	}
	
	@RequestMapping(value = "shipmentAllDraft")
	@ResponseBody
    public String shipmentAllDraft(String shipmentIds,String country){
		String returnInfo="发货不成功：";
		boolean flag=true;
    	if(StringUtils.isNotBlank(shipmentIds)){
			String[] idArr=shipmentIds.split(",");
			for (String id : idArr) {
				 if(StringUtils.isNotBlank(id)){
					    AmazonOutboundOrder amazonOutboundOrder=amazonOutboundOrderService.get(Integer.parseInt(id));
					    amazonOutboundOrder.setLastUpdateDate(new Date());
						if(StringUtils.isBlank(amazonOutboundOrder.getFlag())){
							amazonOutboundOrder.setFlag("0");
						}
						AmazonAccountConfig config=amazonAccountConfigService.getByName(amazonOutboundOrder.getAccountName());
						String rs=amazonOutboundOrderService.createSave(amazonOutboundOrder,null,null,config);
						if(StringUtils.isNotBlank(amazonOutboundOrder.getOldOrderId())&&amazonOutboundOrder.getOldOrderId().length()==9){
							amazonOutboundOrderService.updateWebsiteStatu(amazonOutboundOrder.getOldOrderId());
						}
						if(StringUtils.isNotBlank(rs)){//不成功
							flag=false;
							returnInfo=amazonOutboundOrder.getSellerOrderId()+":"+rs+"<br/>";
						}
				 }
			}
    	}	
    	if(flag){
    		return "0";
    	}else{
    		return returnInfo;
    	}
    }
	
	@RequestMapping(value = "save")
	public String save(AmazonOutboundOrder amazonOutboundOrder,Model model, RedirectAttributes redirectAttributes,String ratingEventId) {
		List<Event> eventList=Lists.newArrayList();
		
		Set<Integer> delSet=Sets.newHashSet();
		if(amazonOutboundOrder.getId()!=null&&amazonOutboundOrder.getId()>0){
			AmazonOutboundOrder order=amazonOutboundOrderService.get(amazonOutboundOrder.getId());
            for (AmazonOutboundOrderItem oldItem : order.getItems()) {
            	boolean flag=false;
            	for(AmazonOutboundOrderItem newItem:amazonOutboundOrder.getItems()){
            		if(newItem.getId()!=null&&oldItem.getId().intValue()==newItem.getId().intValue()){
            			flag=true;
            			break;
            		}
            	}
            	if(!flag){
            		delSet.add(oldItem.getId());
            	}
			}
		}
		
		String reviewLink="";
		 if(StringUtils.isNotBlank(ratingEventId)){
         	try{
         		reviewLink=eventService.get(Integer.parseInt(ratingEventId)).getReviewLink();
     		}catch(Exception e){
     			LOGGER.info("替代差评",e);
     		}
         }
		 if(!"15".equals(amazonOutboundOrder.getEventType())&&!"12".equals(amazonOutboundOrder.getEventType())){
			 for (AmazonOutboundOrderItem item : amazonOutboundOrder.getItems()) {
					Event event=new Event();
					event.setAccountName(amazonOutboundOrder.getAccountName());
					event.setCreateBy(UserUtils.getUser());
					event.setCreateDate(new Date());
					event.setUpdateBy(UserUtils.getUser());
					event.setUpdateDate(new Date());
					event.setState("0");
					event.setMasterBy(UserUtils.getUser());
					event.setDelFlag("0");
					event.setType(amazonOutboundOrder.getEventType());
					event.setPriority("1");
					event.setCustomName(amazonOutboundOrder.getBuyerName());
					event.setCustomEmail(amazonOutboundOrder.getBuyerEmail());
					event.setCountry(amazonOutboundOrder.getCountry());
					event.setReason(amazonOutboundOrder.getRemark());
					event.setInvoiceNumber(amazonOutboundOrder.getSellerOrderId());
					event.setRemarks(item.getAsin());
					event.setProductName(item.getName());
					event.setCustomId(amazonOutboundOrder.getCustomId());
					event.setDescription(amazonOutboundOrder.getRemark());
					if(StringUtils.isNotBlank(ratingEventId)){
						 event.setReviewLink(reviewLink);
					}
					/*AmazonProduct2 product = amazonProduct2Service.getProduct(amazonOutboundOrder.getCountry(),item.getSellersku());
					if(product!=null){
						event.setReviewPrice(product.getSalePrice());
					}*/
					Float price=amazonProduct2Service.getSalePrice(amazonOutboundOrder.getCountry(),item.getAsin());
					if(price!=null){
						event.setReviewPrice(price);
					}
					
					event.setReviewQuantity(item.getQuantityOrdered());
					if("8".equals(amazonOutboundOrder.getEventType())){//Review
						event.setSubject(DictUtils.getDictLabel(amazonOutboundOrder.getCountry(), "platform", "")+item.getName()+"评测");
					}else{
						event.setSubject(DictUtils.getDictLabel(amazonOutboundOrder.getCountry(), "platform", "")+item.getName()+"替代");
					}
					item.setOrder(amazonOutboundOrder);
					eventList.add(event);
				}
		 }else{
			 for (AmazonOutboundOrderItem item : amazonOutboundOrder.getItems()) {
				 item.setOrder(amazonOutboundOrder);
			 }
		 }
		
	
		amazonOutboundOrder.setCreateUser(UserUtils.getUser());
		amazonOutboundOrder.setCreateDate(new Date());
		amazonOutboundOrder.setAmazonOrderId(null);
		amazonOutboundOrder.setLastUpdateDate(new Date());
		if(StringUtils.isBlank(amazonOutboundOrder.getFlag())){
			amazonOutboundOrder.setFlag("0");
		}
		AmazonAccountConfig config=amazonAccountConfigService.getByName(amazonOutboundOrder.getAccountName());
		String rs=amazonOutboundOrderService.createSave(amazonOutboundOrder,eventList,ratingEventId,config);
		if(StringUtils.isNotBlank(amazonOutboundOrder.getOldOrderId())&&amazonOutboundOrder.getOldOrderId().length()==9){
			amazonOutboundOrderService.updateWebsiteStatu(amazonOutboundOrder.getOldOrderId());
		}
		if(StringUtils.isNotBlank(amazonOutboundOrder.getOldOrderId())
				&&(amazonOutboundOrder.getOldOrderId().startsWith("DZW")||amazonOutboundOrder.getOldOrderId().startsWith("AmzMfn"))){
			amazonOutboundOrderService.updateStatu(amazonOutboundOrder.getOldOrderId());
		}
		if(StringUtils.isNotBlank(rs)){
			addMessage(redirectAttributes,"保存未成功"+rs);
		}
		if("Hold".equals(amazonOutboundOrder.getFulfillmentAction())){
			sendEmail(amazonOutboundOrder);
		}
		try{
			if(delSet!=null&&delSet.size()>0){
				amazonOutboundOrderService.delete(delSet);
			}
		}catch(Exception e){
			LOGGER.info("删除测评订单详情异常",e);
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/amazonTestOrReplace/?country="+amazonOutboundOrder.getCountry()+"&sellerOrderId="+amazonOutboundOrder.getSellerOrderId()+"&repage";
	}
	
	private  boolean sendEmail(AmazonOutboundOrder amazonOutboundOrder){
		String toAddress ="";
		StringBuffer content= new StringBuffer("");
		if(amazonOutboundOrder!=null){
			toAddress = systemService.getUser(amazonOutboundOrder.getCheckUser().getId()).getEmail();
			content.append("<p>请审核"+amazonOutboundOrder.getCreateUser().getName()+"提交的"+("com".equals(amazonOutboundOrder.getCountry())?"US":amazonOutboundOrder.getCountry().toUpperCase())+"测评申请,<a title='点击链接到测评列表' href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/amazonTestOrReplace?country="+amazonOutboundOrder.getCountry()+"&sellerOrderId="+amazonOutboundOrder.getSellerOrderId()+"'>"+amazonOutboundOrder.getSellerOrderId()+"点击审核</a></p>");
			content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>");
			content.append("<th>产品名称</th><th>sku</th><th>asin</th><th>数量</th></tr>");
			for (AmazonOutboundOrderItem item: amazonOutboundOrder.getItems()) {
				content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				content.append("<td>"+item.getName()+"</td><td>"+item.getSellersku()+"</td><td>"+item.getAsin()+"</td>");
				content.append("<td>"+item.getQuantityOrdered()+"</td></tr>");
			}
			content.append("</table>");
		}
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();   
			final MailInfo mailInfo = new MailInfo(toAddress,amazonOutboundOrder.getSellerOrderId()+"测评订单审核通知"+DateUtils.getDate("-yyyy/MM/dd"),date);
			mailInfo.setContent(content.toString());
			new Thread(){
				public void run(){   
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return true;
	}
	
	@RequestMapping(value = "add")
	public String add(AmazonOutboundOrder amazonOutboundOrder,Model model){
		if(amazonOutboundOrder!=null&&amazonOutboundOrder.getId()!=null){
			amazonOutboundOrder=amazonOutboundOrderService.get(amazonOutboundOrder.getId());
			if("Marketing".equals(amazonOutboundOrder.getOrderType())){//Review//Review(评测) Support(替代)
				amazonOutboundOrder.setEventType("8");
			}else if("Ebay".equals(amazonOutboundOrder.getOrderType())){//Review//Review(评测) Support(替代)
				amazonOutboundOrder.setEventType("12");
			}else if("AmzMfn".equals(amazonOutboundOrder.getOrderType())){//Review//Review(评测) Support(替代)
				amazonOutboundOrder.setEventType("15");
			}else{
				if("Website".equals(amazonOutboundOrder.getOrderType())){
					amazonOutboundOrder.setEventType("13");
				}else if("Offline".equals(amazonOutboundOrder.getOrderType())){//Offline
					amazonOutboundOrder.setEventType("14");
				}else{
					amazonOutboundOrder.setEventType("5");
				}
			}
		}
		
		if(amazonOutboundOrder!=null&&StringUtils.isNotBlank(amazonOutboundOrder.getCountry())){
			List<String> accountNameList=amazonAccountConfigService.findAccountByCountry(amazonOutboundOrder.getCountry());
			model.addAttribute("accountNameList",accountNameList);
			String accountName=amazonOutboundOrder.getAccountName();
			if(StringUtils.isBlank(accountName)){
				accountName=accountNameList.get(0);
			}
			List<PsiSku> skuList=psiProductService.getProduct(amazonOutboundOrder.getCountry(),accountName);
			model.addAttribute("skuList",skuList);
			model.addAttribute("amazonOutboundOrder", amazonOutboundOrder);
			if(StringUtils.isBlank(amazonOutboundOrder.getDisplayableOrderComment())){
				amazonOutboundOrder.setDisplayableOrderComment(AmazonOutboundOrder.displayableOrderCommentMap.get(amazonOutboundOrder.getCountry()));
			}
			if("uk".equals(amazonOutboundOrder.getCountry())){
				Map<String,String> powerMap=psiProductService.getPowerOrKeyboardByName();//1 带电
				model.addAttribute("powerMap",powerMap);
			}
		}
		return "/modules/amazoninfo/order/amazonTestOrReplaceOrderAdd";
	}
	
	@RequestMapping(value = "createEbayEvent")
	public String createEbayEvent(String amazonOrderId,String country,Model model){
		EbayOrder ebayOrder=ebayOrderService.getOrder(amazonOrderId,country);
		AmazonOutboundOrder amazonOutboundOrder=new AmazonOutboundOrder();
		amazonOutboundOrder.setEventType("12");		
		amazonOutboundOrder.setAmazonOrderId(amazonOrderId);
		amazonOutboundOrder.setCountry(country);
		if(ebayOrder!=null){
			amazonOutboundOrder.setBuyerName(ebayOrder.getBuyerUserId());
			amazonOutboundOrder.setBuyerEmail(ebayOrder.getBuyerEmail());
			Map<String,String> skuAndName=psiProductService.findProductNameWithSku("de".equals(country)?"ebay":"ebay_com");
			if(ebayOrder.getShippingAddress()!=null){
				AmazonOutboundAddress address=new AmazonOutboundAddress();
				address.setAddressLine1(StringUtils.isBlank(ebayOrder.getShippingAddress().getStreet())?ebayOrder.getShippingAddress().getStreet1():ebayOrder.getShippingAddress().getStreet());
				address.setAddressLine2(ebayOrder.getShippingAddress().getStreet1());
				address.setAddressLine3(ebayOrder.getShippingAddress().getStreet2());
				address.setCity(ebayOrder.getShippingAddress().getCityName());
				address.setCountry( ebayOrder.getShippingAddress().getCounty());
				address.setStateOrRegion(ebayOrder.getShippingAddress().getStateOrProvince());
				address.setCountryCode(ebayOrder.getShippingAddress().getCountryCode());
				address.setPostalCode(ebayOrder.getShippingAddress().getPostalCode());
				address.setPhone(ebayOrder.getShippingAddress().getPhone());
				address.setName(ebayOrder.getShippingAddress().getName());
				amazonOutboundOrder.setShippingAddress(address);
			}
			List<AmazonOutboundOrderItem> items=Lists.newArrayList();
			for (EbayOrderItem orderItem : ebayOrder.getItems()) {
				
				if(skuAndName!=null&&skuAndName.get(orderItem.getSku())!=null){
					String nameWithColor=skuAndName.get(orderItem.getSku());
					String name="";
					String color="";
					if(nameWithColor.contains("_")){
						String[] arr=nameWithColor.split("_");
						name=arr[0];
						color=arr[1];
					}else{
						name=nameWithColor;
					}
					PsiSku psiSku2=psiProductService.getPsiSku2(name,country,color);
					if(psiSku2!=null){
						AmazonOutboundOrderItem item=new AmazonOutboundOrderItem();
						item.setQuantityOrdered(orderItem.getQuantityPurchased());
						item.setProductName(psiSku2.getProductName());
						item.setColor(psiSku2.getColor());
						item.setAsin(psiSku2.getAsin());
						item.setSellersku(psiSku2.getSku());
						item.setOrder(amazonOutboundOrder);
						items.add(item);
					}else{
						PsiSku psiSku=psiProductService.getPsiSku(name,country,color);
						if(psiSku!=null){
							AmazonOutboundOrderItem item=new AmazonOutboundOrderItem();
							item.setQuantityOrdered(orderItem.getQuantityPurchased());
							item.setProductName(psiSku.getProductName());
							item.setColor(psiSku.getColor());
							item.setAsin(psiSku.getAsin());
							item.setSellersku(psiSku.getSku());
							item.setOrder(amazonOutboundOrder);
							items.add(item);
						}
					}
				}
			}
			amazonOutboundOrder.setItems(items);
		}
		List<String> accountNameList=amazonAccountConfigService.findAccountByCountry(country);
		model.addAttribute("accountNameList",accountNameList);
		
		
		
			List<PsiSku> skuList=psiProductService.getProduct(amazonOutboundOrder.getCountry(),accountNameList.get(0));
			model.addAttribute("skuList",skuList);
			amazonOutboundOrder.setDisplayableOrderComment(AmazonOutboundOrder.displayableOrderCommentMap.get(amazonOutboundOrder.getCountry()));
			model.addAttribute("amazonOutboundOrder", amazonOutboundOrder);
			return "/modules/amazoninfo/order/amazonTestOrReplaceOrderAdd";
	}
	
	
	@RequestMapping(value = "createSupportEvent")
	public String createSupportEvent(String amazonOrderId,String problem,Model model,String eventType,String country,String ratingEventId,String orderType){
		
		
		if(StringUtils.isNotBlank(orderType)){
			AmazonOutboundOrder amazonOutboundOrder=new AmazonOutboundOrder();
			AmazonUnlineOrder unlineOrder=amazonUnlineOrderService.getByOrderId(amazonOrderId);
			if(unlineOrder!=null){
				amazonOutboundOrder.setAmazonOrderId(amazonOrderId);
				if("website".equals(orderType)){
					amazonOutboundOrder.setEventType("13");		
				}else{
					amazonOutboundOrder.setEventType("14");		
				}
				if(unlineOrder.getSalesChannel().getId().intValue()==120){
					amazonOutboundOrder.setCountry("com");
				}else{
					amazonOutboundOrder.setCountry("de");
				}
				
				amazonOutboundOrder.setOldOrderId(unlineOrder.getAmazonOrderId());
				amazonOutboundOrder.setBuyerEmail(unlineOrder.getBuyerEmail());
				amazonOutboundOrder.setBuyerName(unlineOrder.getBuyerName());
				amazonOutboundOrder.setRemark(unlineOrder.getRemark());
				
				AmazonOutboundAddress address=new AmazonOutboundAddress();
				AmazonUnlineAddress unlineAddress=unlineOrder.getShippingAddress();
				address.setName(unlineAddress.getName());
				address.setPhone(unlineAddress.getPhone());
				address.setPostalCode(unlineAddress.getPostalCode());
				address.setCountryCode(unlineAddress.getCountryCode());
				address.setStateOrRegion(unlineAddress.getStateOrRegion());
				address.setCity(unlineAddress.getCity());
				address.setCountry(unlineAddress.getCounty());
				address.setAddressLine1(unlineAddress.getAddressLine1());
				address.setAddressLine2(unlineAddress.getAddressLine2());
				address.setAddressLine3(unlineAddress.getAddressLine3());
				amazonOutboundOrder.setShippingAddress(address);
				
				List<AmazonOutboundOrderItem> items=Lists.newArrayList();
				for (AmazonUnlineOrderItem orderItem : unlineOrder.getItems()) {
					AmazonOutboundOrderItem item=new AmazonOutboundOrderItem();
					item.setProductName(orderItem.getProductName());
					item.setColor(orderItem.getColor());
					item.setAsin(orderItem.getAsin());
					item.setSellersku(orderItem.getSellersku());
					item.setOrder(amazonOutboundOrder);
					item.setQuantityOrdered(1);
					items.add(item);
				}
				amazonOutboundOrder.setItems(items);
			}
			List<String> accountNameList=amazonAccountConfigService.findAccountByCountry(amazonOutboundOrder.getCountry());
			model.addAttribute("accountNameList",accountNameList);
			
			List<PsiSku> skuList=psiProductService.getProduct(amazonOutboundOrder.getCountry(),accountNameList.get(0));
			model.addAttribute("skuList",skuList);
			String displayableOrderComment=AmazonOutboundOrder.displayableOrderCommentMap.get(amazonOutboundOrder.getCountry());
			model.addAttribute("displayableOrderComment",displayableOrderComment);
			amazonOutboundOrder.setDisplayableOrderComment(displayableOrderComment);
			model.addAttribute("amazonOutboundOrder", amazonOutboundOrder);
		
			return "/modules/amazoninfo/order/amazonTestOrReplaceOrderAdd";
		}else{
			if(StringUtils.isNotBlank(amazonOrderId)){
				AmazonOrder amazonOrder =amazonOrderService.findByEg(amazonOrderId);
				String customId=amazonOrderService.getCustomIdByOrderId(amazonOrderId); 
				if(StringUtils.isNotBlank(customId)){
					amazonOrder.setCustomId(customId);
				}
				model.addAttribute("amazonOrder",amazonOrder);
				if(StringUtils.isNotBlank(eventType)){
					model.addAttribute("eventType",eventType);
				}
				if(StringUtils.isNotBlank(problem)){
					try {
						problem=URLDecoder.decode(problem, "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					model.addAttribute("problem",problem);
				}
				if(amazonOrder!=null&&StringUtils.isNotBlank(amazonOrder.getCountryChar())){
					List<PsiSku> skuList=psiProductService.getProduct(amazonOrder.getCountryChar(),amazonOrder.getAccountName());
					model.addAttribute("skuList",skuList);
					
					if("uk".equals(amazonOrder.getCountryChar())){
						Map<String,String> powerMap=psiProductService.getPowerOrKeyboardByName();//1 带电
						model.addAttribute("powerMap",powerMap);
					}
					String displayableOrderComment=AmazonOutboundOrder.displayableOrderCommentMap.get(amazonOrder.getCountryChar());
					model.addAttribute("displayableOrderComment",displayableOrderComment);
					
					List<String> accountNameList=amazonAccountConfigService.findAccountByCountry(amazonOrder.getCountryChar());
					model.addAttribute("accountNameList",accountNameList);
					
				}
			}else{
				model.addAttribute("displayableOrderComment",AmazonOutboundOrder.displayableOrderCommentMap.get("com"));
			}
		}
		
		model.addAttribute("ratingEventId",ratingEventId);
		return "/modules/amazoninfo/order/amazonTestOrReplaceOrderEmailAdd";
	}
	
	
	
	@RequestMapping(value = "export")
	public String export(AmazonOutboundOrder amazonOutboundOrder,HttpServletRequest request,HttpServletResponse response, Model model) {
		List<Object[]> list=amazonOutboundOrderService.countOrder(amazonOutboundOrder);
		
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		List<String> title = Lists.newArrayList("产品名","国家","类型", "数量");
		
		  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		  style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		  style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		  style.setBottomBorderColor(HSSFColor.BLACK.index);
		  style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		  style.setLeftBorderColor(HSSFColor.BLACK.index);
		  style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		  style.setRightBorderColor(HSSFColor.BLACK.index);
		  style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		  style.setTopBorderColor(HSSFColor.BLACK.index);
		  HSSFFont font = wb.createFont();
		  font.setFontHeightInPoints((short) 16); // 字体高度
		  font.setFontName(" 黑体 "); // 字体
		  font.setBoldweight((short) 16);
		  style.setFont(font);
		  row.setHeight((short) 600);
		  
		  CellStyle contentStyle = wb.createCellStyle();
		  contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		  contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		  contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		  contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		  contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		  contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		  contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		  contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		  contentStyle.setWrapText(true);
		  
		  HSSFCell cell = null;		
		  for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
		  int rowIndex=1;
          if(list!=null&&list.size()>0){
		    	for (Object[] obj:list) {
		    		    row = sheet.createRow(rowIndex++);
		    		    int j=0;
		    		    row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[1].toString());
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[0].toString());
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[2].toString());
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(obj[3].toString());
				}
		    
		     }
         
          for (int i=0;i<rowIndex-1;i++) {
	        	 for (int j = 0; j < title.size(); j++) {
	        			 sheet.getRow(i+1).getCell(j).setCellStyle(contentStyle);
				 }
	       }
          
	       for (int i = 0; i < title.size(); i++) {
	        		 sheet.autoSizeColumn((short)i);
		   }
	          
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
		
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		
				String fileName = "ReveiwOrSupport" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
	}
	
	
	@RequestMapping(value="upload")
	@ResponseBody
	public  String uploadFile(String psiTransportId,MultipartFile uploadFile,String uploadType,HttpServletResponse response,RedirectAttributes redirectAttributes) throws UnsupportedEncodingException{
		if(uploadFile.getSize()!=0){
			
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/paypalRefund";
			File baseDir = new File(baseDirStr); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			
			String name = uploadFile.getOriginalFilename();
			File dest = new File(baseDir,name);
			if(dest.exists()){
				dest.delete();
			}
			try {//Paypal_Refund
				FileUtils.copyInputStreamToFile(uploadFile.getInputStream(),dest);
				
				Workbook workBook = WorkbookFactory.create(uploadFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				Map<String,AmazonOutboundOrder> tempMap=Maps.newHashMap();
				Set<String> orderSet=Sets.newHashSet();
				Set<String> orderSet2=Sets.newHashSet();
				//Map<String,String> modelBrandMap = awsAdverstingService.findModelBrandColorMap();
				//Map<String,List<String>> skuMap=amazonProduct2Service.getAllBandingSku();
				List<AmazonOutboundOrder> list=Lists.newArrayList();
				
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						continue;
					}
					String country=StringUtils.trim(getData(row.getCell(0))).toLowerCase();
					if("us".equals(country)){
						country="com";
					}
					if(StringUtils.isBlank(country)){
						continue;
					}
					String model=StringUtils.trim(getData(row.getCell(1)));
					
					String amountStr=getData(row.getCell(2));
					String orderId=StringUtils.trim(getData(row.getCell(4)));
					float amount=Float.parseFloat(amountStr);
					//String currency=StringUtils.trim(getData(row.getCell(3)));
					
					AmazonOutboundOrder order=new AmazonOutboundOrder();
					
					if(StringUtils.isBlank(orderId)){
						order.setCreateDate(new Date());
						orderId="W-"+rowNum+"-"+new Date().getTime();
						List<AmazonOutboundOrderItem> items=Lists.newArrayList();
						AmazonOutboundOrderItem item=new AmazonOutboundOrderItem();
						item.setQuantityOrdered(1);
					/*	if(modelBrandMap.get(model)!=null){
							String[] arr=modelBrandMap.get(model).split(";;;");
							String productName = arr[0] + " " + model;
							item.setProductName(productName);
							if(arr.length==1){
								item.setColor("");
							}else{
								item.setColor(arr[1].split(",")[0]);
							}
							if(skuMap.get(item.getName()+","+country)==null){
								item.setSellersku("1016410453");
								LOGGER.info(item.getName()+","+country+"无sku");
							}else{
								item.setSellersku(skuMap.get(item.getName()+","+country).get(0));
							}
							item.setOrder(order);
							items.add(item);
							order.setItems(items);
						}else{
							LOGGER.info(model+" model错误");
							item.setProductName("Inateck Other");
							item.setColor("");
							item.setSellersku("1016512067");
							item.setOrder(order);
							items.add(item);
							order.setItems(items);
						}*/
						item.setProductName("Inateck Other");
						item.setColor("");
						item.setSellersku("1016512067");
						item.setOrder(order);
						items.add(item);
						order.setItems(items);
						orderSet2.add(orderId);
					}else{
						orderSet.add(orderId);
					}
					order.setAmazonOrderId(orderId);
					order.setSellerOrderId(orderId);
					order.setLastUpdateDate(new Date());
					order.setOrderStatus("COMPLETE");
					order.setOrderType("Paypal_Refund");
					order.setCountry(country);
					order.setRemark(model);
					order.setAmazonFee(-amount);
					tempMap.put(orderId,order);
					//if(order.getItems()!=null&&order.getItems().size()>0){
						list.add(order);
					//}
				}	
				Map<String,AmazonOrder> orderMap=amazonOrderService.findOrders(orderSet);
				for (Map.Entry<String,AmazonOrder> entry: orderMap.entrySet()) {
					AmazonOrder amazonOrder=entry.getValue();
					AmazonOutboundOrder order=tempMap.get(entry.getKey());
					if(order!=null){
						order.setCreateDate(amazonOrder.getPurchaseDate());
						AmazonOrderItem orderItem=amazonOrder.getItems().get(0);
						List<AmazonOutboundOrderItem> items=Lists.newArrayList();
						AmazonOutboundOrderItem item=new AmazonOutboundOrderItem();
						item.setQuantityOrdered(orderItem.getQuantityOrdered());
						item.setProductName(orderItem.getProductName());
						item.setColor(orderItem.getColor());
						item.setAsin(orderItem.getAsin());
						item.setSellersku(orderItem.getSellersku());
						item.setOrder(order);
						items.add(item);
						order.setItems(items);
					}
				}
				
				List<String> orderList=amazonOutboundOrderService.findReviewRefund(orderSet);
				if(orderList!=null&&orderList.size()>0){
					List<AmazonOutboundOrder> removeList=Lists.newArrayList();
					for (AmazonOutboundOrder order : list) {
						if(orderList.contains(order.getAmazonOrderId())||order.getItems()==null||order.getItems().size()==0){
							removeList.add(order);
						}
					}
					if(removeList.size()>0){
						list.removeAll(removeList);
					}
				}
				
				amazonOutboundOrderService.save(list);
				if(orderMap.size()>0){
						amazonOutboundOrderService.updateDate(orderMap);
				}
				if(orderSet2.size()>0){
						amazonOutboundOrderService.updateDate(orderSet2);
				}
				
				addMessage(redirectAttributes, "文件上传成功");
				return "0";
			} catch (Exception e) {
				logger.warn("文件保存失败",e);
				addMessage(redirectAttributes, "文件上传失败");
				return "1";
			}
		}else{
			addMessage(redirectAttributes, "上传文件名为空");
			return "2";
		}
	}
	
	public String getData(Cell cell){
		String value="";
		switch (cell.getCellType()) {
	        case HSSFCell.CELL_TYPE_NUMERIC: // 数字
	            value = cell.getNumericCellValue() + "";
	            break;
	        case HSSFCell.CELL_TYPE_STRING: // 字符串
	            value = cell.getStringCellValue();
	            break;
	        case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
	            value = cell.getBooleanCellValue() + "";
	            break;
	        case HSSFCell.CELL_TYPE_FORMULA: // 公式
	            value = cell.getCellFormula() + "";
	            break;
	        case HSSFCell.CELL_TYPE_BLANK: // 空值
	            value = "";
	            break;
	        case HSSFCell.CELL_TYPE_ERROR: // 故障
	            value = "";//非法字符
	            break;
	        default:
	            value = "";//未知类型
	            break;
	        }
		return value;
	}
	

	private String toJson(AmazonOutboundOrder order){
		//String strObject="{\"first\":{\"address\":\"中国上海\",\"age\":\"23\",\"name\":\"JSON\"}}";
		String orderRs = "\"sellerOrderId\":\""+order.getSellerOrderId()+"\",\"displayableOrderComment\":\""+order.getDisplayableOrderComment()+"\",\"shippingSpeedCategory\":\""+order.getShippingSpeedCategory()+"\",\"fulfillmentAction\":\""+order.getFulfillmentAction()+"\",\"buyerEmail\":\""+order.getBuyerEmail()+"\"";
		String items = "";
		for (AmazonOutboundOrderItem item : order.getItems()) {
			items +="{\"sku\":\""+item.getSellersku()+"\",\"quantity\":\""+item.getQuantityOrdered()+"\"},";
		}
		items = items.substring(0,items.length()-1);
		AmazonOutboundAddress orderAddr = order.getShippingAddress();
		String address = "{\"name\":\""+orderAddr.getName()+"\",\"addressLine1\":\""+orderAddr.getAddressLine1()+"\",\"stateOrRegion\":\""+orderAddr.getStateOrRegion()+"\",\"countryCode\":\""+orderAddr.getCountryCode()+"\",\"postalCode\":\""+orderAddr.getPostalCode()+"\",\"city\":\""+orderAddr.getCity()+"\",\"district\":\""+orderAddr.getDistrict()+"\",\"addressLine2\":\""+orderAddr.getAddressLine2()+"\",\"addressLine3\":\""+orderAddr.getAddressLine3()+"\",\"phone\":\""+orderAddr.getPhone()+"\"}";
		String rs="{"+orderRs+",\"orderItems\":["+items+"],\"shippingAddress\":"+address+"}";
		return rs;
	}
	
	
}
