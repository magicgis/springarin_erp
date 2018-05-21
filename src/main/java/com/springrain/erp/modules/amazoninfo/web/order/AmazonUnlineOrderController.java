package com.springrain.erp.modules.amazoninfo.web.order;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.MessageUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrderItem;
import com.springrain.erp.modules.amazoninfo.scheduler.SendEmailByOrderMonitor;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonUnlineOrderService;
import com.springrain.erp.modules.custom.scheduler.CustomEmailManager;
import com.springrain.erp.modules.custom.service.UnsubscribeEmailService;
import com.springrain.erp.modules.psi.dao.PsiInventoryDao;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiInventoryOut;
import com.springrain.erp.modules.psi.entity.PsiInventoryOutItem;
import com.springrain.erp.modules.psi.entity.PsiQualityChangeBill;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderItem;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderItem;
import com.springrain.erp.modules.psi.service.PsiInventoryOutService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiQualityChangeBillService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/unlineOrder")
public class AmazonUnlineOrderController extends BaseController {
	@Autowired
	private AmazonUnlineOrderService amazonUnlineOrderService;

	@Autowired
	private AmazonProductService amazonProductService;
	@Autowired
	private StockService stockService;
	@Autowired
	private PsiInventoryService psiInventoryService;
	@Autowired
	private PsiInventoryOutService psiInventoryOutService;
	@Autowired
	private PsiProductService productService;
	@Autowired
	private CustomEmailManager sendCustomEmailManager;
	@Autowired
	private PsiProductService psiProductService;
	@Autowired
	private PsiQualityChangeBillService  qualityBillService;
	@Autowired
	private MailManager						mailManager;
	@Autowired
	private PsiProductGroupUserService      groupUserService;
	@Autowired
	private PsiTransportOrderService psiTransportOrderService;
	@Autowired
	private LcPsiTransportOrderService lcPsiTransportOrderService;
	@Autowired
	private PsiProductEliminateService eliminateService;
	@Autowired
	private PsiInventoryDao psiInventoryDao;
	@Autowired
	private AmazonOrderService amazonOrderService;
	
	private  final Logger LOGGER = LoggerFactory.getLogger(getClass());
	 //传递json数组字符串  
    @RequestMapping(value = {"saveOrders"})
    public void resolveJsonArray(HttpServletRequest request,HttpServletResponse response) throws IOException { 
    	List<AmazonUnlineOrder> amazonUnlineOrders=Lists.newArrayList();
        String str = URLDecoder.decode(request.getParameter("orderJson"),"UTF-8");  
        JSONArray jsonArray=JSON.parseArray(str);
        for (Object object : jsonArray) {
        	  JSONObject o = (JSONObject)object;
        	  String amazonOrderId=(String) o.get("amazonOrderId");
        	  AmazonUnlineOrder unlineOrder=amazonUnlineOrderService.getByOrderId(amazonOrderId);
        	  if(unlineOrder==null){//add
        		  AmazonUnlineOrder amazonUnlineOrder=new AmazonUnlineOrder();
            	  amazonUnlineOrder.setOutBound("0");
        		  amazonUnlineOrder.setAmazonOrderId(amazonOrderId);
            	  amazonUnlineOrder.setSellerOrderId(amazonOrderId);
            	  String orderStatus=(String) o.get("orderStatus");
            	  amazonUnlineOrder.setOrderStatus(orderStatus);
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
                  Float orderTotal = new Float(0);
          		  int    shipedTotal = 0;
          		  int    upshipedTotal =0;
                  for (Object obj : orderItems) {
                	  AmazonUnlineOrderItem item=new AmazonUnlineOrderItem();
                	  Map<String,String> itemMap= (Map<String,String>)obj;
                      String asin=(String) itemMap.get("asin");
                     
                      Integer quantityOrdered=Integer.parseInt(itemMap.get("quantityOrdered")); 
                      Float itemPrice=Float.parseFloat(itemMap.get("itemPrice")); 
                      Float shippingPrice=Float.parseFloat(itemMap.get("shippingPrice"));  
                      item.setAsin(asin);
                      PsiSku psiSku=psiProductService.getProductByAsin(asin);
                      item.setProductName(psiSku.getProductName());
                      item.setColor(psiSku.getColor());
                      item.setQuantityOrdered(quantityOrdered);
                      item.setQuantityShipped(quantityOrdered);
                      item.setItemPrice(itemPrice);
                      item.setItemTax(0f);
                      item.setShippingPrice(shippingPrice);
                      item.setGiftWrapPrice(0f);
                      item.setOrder(amazonUnlineOrder);
                      items.add(item);
                      upshipedTotal+=item.getQuantityOrdered()-item.getQuantityShipped();
          			  shipedTotal+=item.getQuantityShipped();
          			  //单价   单项总价变换
          			  Float itemTotalPrice=item.getItemPrice()*item.getQuantityOrdered()+item.getItemPrice()*item.getQuantityOrdered()*item.getItemTax()/100;
          			  orderTotal +=itemTotalPrice+item.getShippingPrice()+item.getGiftWrapPrice();
    			  }
              	  DecimalFormat df =new DecimalFormat("#.00");
        		  amazonUnlineOrder.setOrderTotal(Float.parseFloat(df.format(orderTotal)));
        		  amazonUnlineOrder.setNumberOfItemsShipped(shipedTotal);//已发货总数
        		  amazonUnlineOrder.setNumberOfItemsUnshipped(upshipedTotal);//未发货数量
                  amazonUnlineOrder.setItems(items);
                  amazonUnlineOrders.add(amazonUnlineOrder);
        	  }else{//edit
        		  String orderStatus=(String) o.get("orderStatus");
        		  unlineOrder.setOrderStatus(orderStatus);
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
                  Float orderTotal = new Float(0);
          		  int    shipedTotal = 0;
          		  int    upshipedTotal =0;
                  List orderItems=(List)o.get("orderItems");
                  for (Object obj : orderItems) {
                	  AmazonUnlineOrderItem item=new AmazonUnlineOrderItem();
                	  Map<String,String> itemMap= (Map<String,String>)obj;
                      String asin=(String) itemMap.get("asin");
                      Integer quantityOrdered=Integer.parseInt(itemMap.get("quantityOrdered")); 
                      Float itemPrice=Float.parseFloat(itemMap.get("itemPrice")); 
                      Float shippingPrice=Float.parseFloat(itemMap.get("shippingPrice"));  
                      item.setAsin(asin);
                      item.setQuantityOrdered(quantityOrdered);
                      item.setQuantityShipped(quantityOrdered);
                      item.setItemPrice(itemPrice);
                      item.setItemTax(0f);
                      item.setShippingPrice(shippingPrice);
                      item.setGiftWrapPrice(0f);
                      item.setOrder(unlineOrder);
                      PsiSku psiSku=psiProductService.getProductByAsin(asin);
                      item.setProductName(psiSku.getProductName());
                      item.setColor(psiSku.getColor());
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
                             item.setItemTax(0f);
                             item.setShippingPrice(updateItem.getShippingPrice());
                             item.setGiftWrapPrice(0f);
                             item.setProductName(updateItem.getProductName());
                             item.setColor(updateItem.getColor());
                	    }else{//delete
                	    	removeItem.add(item);
                	    	amazonUnlineOrderService.delete(item.getId());
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
          			  Float itemTotalPrice=item.getItemPrice()*item.getQuantityOrdered()+item.getItemPrice()*item.getQuantityOrdered()*item.getItemTax()/100;
          			  orderTotal +=itemTotalPrice+item.getShippingPrice()+item.getGiftWrapPrice();
                  }
                  DecimalFormat df =new DecimalFormat("#.00");
                  unlineOrder.setOrderTotal(Float.parseFloat(df.format(orderTotal)));
                  unlineOrder.setNumberOfItemsShipped(shipedTotal);//已发货总数
                  unlineOrder.setNumberOfItemsUnshipped(upshipedTotal);//未发货数量
        		  amazonUnlineOrders.add(unlineOrder);
        	  }
		}
        amazonUnlineOrderService.save(amazonUnlineOrders);
    }   

	@RequestMapping(value = { "list", "" })
	public String list(AmazonUnlineOrder amazonUnlineOrder, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<AmazonUnlineOrder> page = new Page<AmazonUnlineOrder>(request, response);
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (amazonUnlineOrder.getPurchaseDate() == null) {
			amazonUnlineOrder.setPurchaseDate(DateUtils.addMonths(today, -1));
			amazonUnlineOrder.setLastUpdateDate(today);
		}
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id desc");
		} else {
			page.setOrderBy(orderBy + ",id desc");
		}
		page = amazonUnlineOrderService.find(page, amazonUnlineOrder);
		page.setOrderBy(orderBy);
		model.addAttribute("page", page);
		List<Stock> stocks =stockService.findStocks("0");
		model.addAttribute("stocks",stocks);
		model.addAttribute("site",LogisticsSupplier.getWebSite());
		return "modules/amazoninfo/order/amazonUnlineOrderList";
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateLadingBillNo"})
	public String updateLadingBillNo(Integer id,String billNo) {
		return this.amazonUnlineOrderService.updateLadingBillNo(id, billNo);
	}
	
	@ResponseBody
	@RequestMapping(value = {"getAddrInfo"})
	public AmazonUnlineAddress getAddrInfo(String addrName) {
		return this.amazonUnlineOrderService.findByName(addrName);
	}
	
	@RequestMapping(value = {"updateCancelInfo"})
	public String updateCancelInfo(Integer id) {
		this.amazonUnlineOrderService.updateCancelInfo(id);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/unlineOrder/?repage";
	}
	
	@RequestMapping(value = "edit")
	public String edit(AmazonUnlineOrder amazonUnlineOrder, Model model) {
		Integer stock=19;
		if(amazonUnlineOrder.getSalesChannel()!=null&&amazonUnlineOrder.getSalesChannel().getId()!=null){
			stock=amazonUnlineOrder.getSalesChannel().getId();
		}
		amazonUnlineOrder = this.amazonUnlineOrderService.get(amazonUnlineOrder.getId());
		
		/*int key = amazonUnlineOrder.getSalesChannel().getId();
		List<AmazonUnlineOrderItem> items = amazonUnlineOrder.getItems();
		if (items != null && items.size() > 0) {
			for (AmazonUnlineOrderItem amazonOrderItem : items) {
				 PsiInventory psiInventory=this.psiInventoryService.findBySku(amazonOrderItem.getSellersku(),key);
				if(StringUtils.isBlank(amazonOrderItem.getTitle())||StringUtils.isBlank(amazonOrderItem.getProductName())){
					amazonOrderItem.setTitle(psiInventory.getProductName());
					amazonOrderItem.setProductName(psiInventory.getProductName());
					amazonOrderItem.setColor(psiInventory.getColorCode());
				}
			}
		}*/
		List<PsiInventory> inventorys=Lists.newArrayList();
		if("管理员".equals(amazonUnlineOrder.getOrderChannel())){
			inventorys=psiInventoryService.findByStock(stock);
			amazonUnlineOrder.setSalesChannel(new Stock(stock));
		}else{
			inventorys=psiInventoryService.findByStock(amazonUnlineOrder.getSalesChannel().getId());
		}
		
		Map<String,String>  productNameSkuMap = Maps.newHashMap();
		Map<String,List<String>>  asinMap = Maps.newHashMap();
		Map<String,Map<String,String>> skuMap=psiProductService.findSkuByCountryAsin();
		for(PsiInventory inventory:inventorys){
			productNameSkuMap.put(inventory.getSku(), inventory.getProductName()+(StringUtils.isBlank(inventory.getColorCode())?"":("_"+inventory.getColorCode()))+"["+inventory.getSku()+"]");
			if("管理员".equals(amazonUnlineOrder.getOrderChannel())){
				/*PsiSku psiSku=psiProductService.getProductBySku(inventory.getSku(),inventory.getCountryCode());
				if(psiSku!=null){
					List<String> temp=asinMap.get(psiSku.getAsin());
					if(temp==null){
						temp=Lists.newArrayList();
						asinMap.put(psiSku.getAsin(), temp);
					}
					temp.add(inventory.getSku());
				}*/
				if(skuMap!=null&&skuMap.get(inventory.getSku())!=null&&skuMap.get(inventory.getSku()).get(inventory.getCountryCode())!=null){
					String asin=skuMap.get(inventory.getSku()).get(inventory.getCountryCode());
					List<String> temp=asinMap.get(asin);
					if(temp==null){
						temp=Lists.newArrayList();
						asinMap.put(asin, temp);
					}
					temp.add(inventory.getSku());
				}
			}
		}
		model.addAttribute("asinMap",asinMap);
		model.addAttribute("sku", JSON.toJSON(productNameSkuMap));
		List<Stock> stocks =stockService.findStocks("0");
		model.addAttribute("stocks",stocks);
		model.addAttribute("amazonUnlineOrder",amazonUnlineOrder);
		/*List<String> supplier=new ArrayList<String>();
		List<String> airSupplier= LogisticsSupplier.getLogisticsSupplierByType("1");
		List<String> expSupplier= LogisticsSupplier.getLogisticsSupplierByType("2");
		List<String> seaSupplier= LogisticsSupplier.getLogisticsSupplierByType("3");
		supplier.addAll(airSupplier);
		supplier.addAll(expSupplier);
		supplier.addAll(seaSupplier);*/
		model.addAttribute("typeSupplier", LogisticsSupplier.getLogisticsSupplierByType("4"));
		if("管理员".equals(amazonUnlineOrder.getOrderChannel())){
			return "modules/amazoninfo/order/amazonOrderEdit2";
		}else{
			return "modules/amazoninfo/order/amazonOrderEdit";
		}
		
	}
	
	@RequestMapping(value = "form")
	public String form(AmazonUnlineOrder amazonUnlineOrder, Model model) {
		amazonUnlineOrder = this.amazonUnlineOrderService.get(amazonUnlineOrder.getId());
		model.addAttribute("amazonUnlineOrder", amazonUnlineOrder);
		/*int key = amazonUnlineOrder.getSalesChannel().getId();
		List<AmazonUnlineOrderItem> items = amazonUnlineOrder.getItems();
		if (items != null && items.size() > 0) {
			for (AmazonUnlineOrderItem amazonOrderItem : items) {
				 PsiInventory psiInventory=this.psiInventoryService.findBySku(amazonOrderItem.getSellersku(),key);
				String name = amazonProductService.findProductName(
						amazonOrderItem.getAsin(), psiInventory.getCountryCode());
				if(StringUtils.isNotEmpty(name))
					amazonOrderItem.setTitle(name);
			}
		}*/
		
		return "modules/amazoninfo/order/amazonUnlineOrderForm";
	}

	@RequestMapping(value = "save")
	@ResponseBody
	public String save(AmazonUnlineOrder amazonUnlineOrder) {
		AmazonUnlineOrder temp = this.amazonUnlineOrderService.get(amazonUnlineOrder.getId());
		temp.setRateSn(amazonUnlineOrder.getRateSn());
		temp.setInvoiceAddress(amazonUnlineOrder.getInvoiceAddress());
		amazonUnlineOrderService.save(temp);
		return temp.getInvoiceAddress().getId() + "";
	}
	
	@RequestMapping(value = "getAmazonUnlineOrder")
	@ResponseBody
	public  String  getAmazonUnlineOrder(AmazonUnlineOrder amazonUnlineOrder, Model model) {
		AmazonUnlineOrder temp = this.amazonUnlineOrderService.get(amazonUnlineOrder.getId());
		String rs="{\"carrier\":\""+temp.getSupplier()+"\",\"ladingBillNo\":\""+temp.getBillNo()+"\",\"addressLine\":\""+toJson(temp.getShippingAddress().getAddressLine1())+"\",\"sales_channel\":"+temp.getSalesChannel().getId()+",\"items\":"+toJson(temp.getItems())+"}";
		return rs;
	
	}
	
	@RequestMapping(value = "outbound")
	public String outbound(AmazonUnlineOrder amazonUnlineOrder, Model model) {
		AmazonUnlineOrder temp = this.amazonUnlineOrderService.get(amazonUnlineOrder.getId());
        //amazonUnlineOrderService.updateOutBound(temp);
		List<PsiInventory> inventorys=psiInventoryService.findByStock(temp.getSalesChannel().getId());
		Map<String,Integer>  productQuantityMap = Maps.newHashMap();
		Map<String,String>  productNameSkuMap = Maps.newHashMap();
		for(PsiInventory inventory:inventorys){ 
			productQuantityMap.put(inventory.getSku(), inventory.getOfflineQuantity());
			productNameSkuMap.put(inventory.getSku(), inventory.getProductName()+(StringUtils.isBlank(inventory.getColorCode())?"":("_"+inventory.getColorCode()))+"["+inventory.getSku()+"]");
			
		}
		model.addAttribute("productNameSkuMap",productNameSkuMap);
		model.addAttribute("productQuantityMap",productQuantityMap);
		model.addAttribute("amazonUnlineOrder",temp);
		return "modules/amazoninfo/order/unlineOrderInventoryOut";
	}
	
	@RequestMapping(value = "outboundSave")
	public String addSave(MultipartFile memoFile,MultipartFile excelFile,PsiInventoryOut psiInventoryOut,RedirectAttributes redirectAttributes,String id) throws Exception {
		psiInventoryOut.setId(null);
		String res=psiInventoryOutService.addSave(memoFile,excelFile, null, psiInventoryOut);
	
		if(StringUtils.isNotEmpty(res)){
			addMessage(redirectAttributes, res);
		}else{
			addMessage(redirectAttributes, "保存出库单" +psiInventoryOut.getBillNo()+ "成功");
            Map<String,Integer> skuMap=Maps.newHashMap();
			AmazonUnlineOrder temp = this.amazonUnlineOrderService.get(Integer.parseInt(id));
		   // amazonUnlineOrderService.updateOutBound(temp,psiInventoryOut.getBillNo());
			for (PsiInventoryOutItem item : psiInventoryOut.getItems()) {
				skuMap.put(item.getSku(), item.getQuantity());
			}
			boolean flag=true;
			for(AmazonUnlineOrderItem item : temp.getItems()){
				if(skuMap.get(item.getSellersku())!=null){
					item.setQuantityOut(skuMap.get(item.getSellersku())+(item.getQuantityOut()==null?0:item.getQuantityOut()));
				}
				if(item.getQuantityOrdered().intValue()!=item.getQuantityOut().intValue()){
					flag=false;
				}
			}
			temp.setLatestShipDate(new Date());
			if(StringUtils.isBlank(temp.getOutBoundNo())){
				temp.setOutBoundNo(psiInventoryOut.getBillNo());
			}else{
				temp.setOutBoundNo(temp.getOutBoundNo()+","+psiInventoryOut.getBillNo());
			}
			
			if(flag){//全部出货
				temp.setOutBound("1");
				temp.setOrderStatus("Shipped");
			}else{
				temp.setOrderStatus("PartiallyShipped");
				temp.setOutBound("2");
			}
			amazonUnlineOrderService.save(temp);
			try{
				List<PsiTransportOrder> transportList=psiTransportOrderService.findByUnlineOrderId(Integer.parseInt(id));
				if(transportList!=null){
					if(transportList.size()==1){
						PsiTransportOrder tranOrder=transportList.get(0);
						tranOrder.setTransportSta("1");
						tranOrder.setOperDeliveryDate(new Date());
						tranOrder.setOperDeliveryUser(UserUtils.getUser());
						psiTransportOrderService.save(tranOrder);
					}else{
						for (PsiTransportOrder psiTransportOrder : transportList) {
							boolean updateFlag=true;
							for(PsiTransportOrderItem item:psiTransportOrder.getItems()){
								if(skuMap.get(item.getSku())==null||(item.getQuantity().intValue()!=skuMap.get(item.getSku()).intValue())){
									updateFlag=false;
									break;
								}
							}
							if(updateFlag){
								psiTransportOrder.setTransportSta("1");
								psiTransportOrder.setOperDeliveryDate(new Date());
								psiTransportOrder.setOperDeliveryUser(UserUtils.getUser());
								psiTransportOrderService.save(psiTransportOrder);
								break;
							}
						}
					}
				}else{
					try{
						List<LcPsiTransportOrder> lcTransportList=lcPsiTransportOrderService.findByUnlineOrderId(Integer.parseInt(id));
						if(lcTransportList!=null){
							if(lcTransportList.size()==1){
								LcPsiTransportOrder tranOrder=lcTransportList.get(0);
								tranOrder.setTransportSta("1");
								tranOrder.setOperDeliveryDate(new Date());
								tranOrder.setOperDeliveryUser(UserUtils.getUser());
								lcPsiTransportOrderService.save(tranOrder);
							}else{
								for (LcPsiTransportOrder psiTransportOrder : lcTransportList) {
									boolean updateFlag=true;
									for(LcPsiTransportOrderItem item:psiTransportOrder.getItems()){
										if(skuMap.get(item.getSku())==null||(item.getQuantity().intValue()!=skuMap.get(item.getSku()).intValue())){
											updateFlag=false;
											break;
										}
									}
									if(updateFlag){
										psiTransportOrder.setTransportSta("1");
										psiTransportOrder.setOperDeliveryDate(new Date());
										psiTransportOrder.setOperDeliveryUser(UserUtils.getUser());
										lcPsiTransportOrderService.save(psiTransportOrder);
										break;
									}
								}
							}
						}
					}catch(Exception e){
						logger.warn("更新订单出库状态异常",e);
					}
				}
			}catch(Exception e){
				logger.warn("更新订单出库状态异常",e);
			}
			
		}  
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/unlineOrder/?repage";
	}
	
	
	/*@RequestMapping(value = "deleverGoods")
	@ResponseBody
	public String deleverGoods(AmazonUnlineOrder amazonUnlineOrder) {
		AmazonUnlineOrder temp = this.amazonUnlineOrderService.get(amazonUnlineOrder.getId());
		Map<String,Integer> skuMap=new HashMap<String,Integer>();
	    List<AmazonUnlineOrderItem> items=temp.getItems();
	    for (AmazonUnlineOrderItem item : items) {
			skuMap.put(item.getSellersku(), item.getQuantityOrdered());
		}
		boolean flag=psiInventoryOutService.offLineOrderOutInventory(skuMap,temp.getSalesChannel().getId(),temp.getSalesChannel().getStockName(),temp.getAmazonOrderId());
		if(flag){
			amazonUnlineOrderService.updateOutBound(temp);
			return "1";
		}else{
			return "0";
		}
	}*/
	
	@RequestMapping(value = "saveAdd")
	public String saveAdd(AmazonUnlineOrder amazonUnlineOrder,RedirectAttributes redirectAttributes) {
		Map<String, String> productPositionMap = eliminateService.findAllProductPosition();
		String  isAdd="1";
		Set<Integer> delSet=Sets.newHashSet();
		Date purchaseDate=new Date();
		if(amazonUnlineOrder.getId()!=null&&amazonUnlineOrder.getId()>0){
			isAdd="0";
			AmazonUnlineOrder order=amazonUnlineOrderService.get(amazonUnlineOrder.getId());
			purchaseDate=order.getPurchaseDate();
			amazonUnlineOrder.setInvoiceAddress(order.getInvoiceAddress());
            for (AmazonUnlineOrderItem oldItem : order.getItems()) {
            	boolean flag=false;
            	for(AmazonUnlineOrderItem newItem:amazonUnlineOrder.getItems()){
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
		if(StringUtils.isEmpty(amazonUnlineOrder.getOutBound())){
			amazonUnlineOrder.setOutBound("0");
		}
		Map<String,String> applyInfos=Maps.newHashMap();
		Map<String,Integer> skuInfos=Maps.newHashMap();
		Float orderTotal = new Float(0);
		int    shipedTotal = 0;
		int    upshipedTotal =0;
		Map<String,Integer> unShippedMap =this.psiInventoryService.getUnShippedQuantity(amazonUnlineOrder.getSalesChannel().getId());
		for(AmazonUnlineOrderItem item:amazonUnlineOrder.getItems()){
			 PsiInventory psiInventory=this.psiInventoryService.findBySku(item.getSellersku(),amazonUnlineOrder.getSalesChannel().getId());
			//根据国家和sku     查询出asin    title
			//AmazonProduct  product =this.amazonProductService.findNameAndAsin(item.getSellersku(),psiInventory.getCountryCode());
			PsiSku product=productService.getProductBySku(item.getSellersku(),psiInventory.getCountryCode());
			if(product!=null){
				item.setAsin(product.getAsin());
				item.setTitle(product.getProductName());
				item.setProductName(product.getProductName());
				item.setColor(product.getColor());
				item.setCountry(psiInventory.getCountryCode());
			}
			if(item.getItemPrice()==null){
				item.setItemPrice(0f);
			}
			if(item.getItemTax()==null){
				item.setItemTax(0f);
			}
			if(item.getShippingPrice()==null){
				item.setShippingPrice(0f);
			}
			if(item.getGiftWrapPrice()==null){
				item.setGiftWrapPrice(0f);
			}
			
			if(item.getQuantityOrdered()==null){
				item.setQuantityOrdered(0);
			}
			
			if(item.getOrder()==null){
				item.setOrder(amazonUnlineOrder);
			}
			item.setQuantityShipped(item.getQuantityOrdered());
			upshipedTotal+=item.getQuantityOrdered()-item.getQuantityShipped();
			shipedTotal+=item.getQuantityShipped();
			//单价   单项总价变换
			Float itemTotalPrice=item.getItemPrice()*item.getQuantityOrdered()+item.getItemPrice()*item.getQuantityOrdered()*item.getItemTax()/100;
			orderTotal +=itemTotalPrice+item.getShippingPrice()+item.getGiftWrapPrice();
			item.setItemPrice(item.getItemPrice());
			
			
			String appStr=amazonUnlineOrder.getSalesChannel().getId()+","+item.getSellersku()+","+item.getQuantityOrdered()+","+psiInventory.getProductId()+","+psiInventory.getProductName()+","+psiInventory.getCountryCode()+","+psiInventory.getColorCode();
			applyInfos.put(item.getSellersku(),appStr);
						
			skuInfos.put(item.getSellersku(), item.getQuantityOrdered());
		}
		
		if(StringUtils.isEmpty(amazonUnlineOrder.getAmazonOrderId())){
			String orderId=Math.round(Math.random()*9000+1000)+""+ new Date().getTime();
			String newOrderId=orderId.substring(0, 3)+"-"+orderId.substring(3, 17);
			amazonUnlineOrder.setSellerOrderId(newOrderId);
			amazonUnlineOrder.setAmazonOrderId(newOrderId);
		}
		
	
		amazonUnlineOrder.setPurchaseDate(purchaseDate);
		
		
		DecimalFormat df =new DecimalFormat("#.00");
		amazonUnlineOrder.setOrderTotal(Float.parseFloat(df.format(orderTotal)));
		
		amazonUnlineOrder.setFulfillmentChannel("MFN");
		amazonUnlineOrder.setLastUpdateDate(new Date());
		amazonUnlineOrder.setShipServiceLevel("Standard");
		amazonUnlineOrder.setShipmentServiceLevelCategory("Standard");
		amazonUnlineOrder.setOrderType("Standard");
		amazonUnlineOrder.setNumberOfItemsShipped(shipedTotal);//已发货总数
		amazonUnlineOrder.setNumberOfItemsUnshipped(upshipedTotal);//未发货数量
		
		//order channel 保存创建人的name
		amazonUnlineOrder.setOrderChannel(UserUtils.getUser().getName());
		//订单状态                 如果未发货数量为0，状态改为Shipped
		/*if(upshipedTotal==0){
			amazonUnlineOrder.setOrderStatus("Shipped");
		}*/
		//付款方式
		amazonUnlineOrder.setPaymentMethod("Other");
		
		amazonUnlineOrderService.save(amazonUnlineOrder);
		try{
			if(delSet!=null&&delSet.size()>0){
				amazonUnlineOrderService.delete(delSet);
			}
		}catch(Exception e){
			LOGGER.info("删除线下订单详情异常",e);
		}
		
		//String info="";
		StringBuffer buf= new StringBuffer();
		try{
			if((amazonUnlineOrder.getSalesChannel().getId().intValue()==21||amazonUnlineOrder.getSalesChannel().getId().intValue()==130)&&"0".equals(amazonUnlineOrder.getOrigin())){
				//如果是中国仓并且来源为新建订单的，不需要生成数据
			}else{
				//如果订单在new_to_offline 表里面，并且状态为申请状态，更新sku的数量
				if("0".equals(isAdd)){
					Integer warehouseId=amazonUnlineOrder.getSalesChannel().getId();
					List<PsiQualityChangeBill> changeBills = this.qualityBillService.findNoCancelInfos(warehouseId, amazonUnlineOrder.getId());
					if(changeBills!=null&&changeBills.size()>0){
						//如果数量没变
						Integer appQuantity=0;
						Integer sureQuantity=0;
						Set<String>  appSkuSet = Sets.newHashSet();
						for(PsiQualityChangeBill changeBill:changeBills){
							sureQuantity+=changeBill.getQuantity();
							appSkuSet.add(changeBill.getSku());
						}
						for (Map.Entry<String,String> entry: applyInfos.entrySet()) { 
							String arr[] =entry.getValue().split(",");
							appQuantity+=Integer.parseInt(arr[2]);
						}
						
						if(!appQuantity.equals(sureQuantity)){
							//编辑时如果原有数量有改变
							buf.append("线下订单["+amazonUnlineOrder.getId()+"]信息变更！");
							for(PsiQualityChangeBill changeBill:changeBills){
								if("0".equals(changeBill.getChangeSta())){
									//如果是申请状态
									String sku=changeBill.getSku();
									Integer unSureId = changeBill.getId();
									Integer quantity = changeBill.getQuantity();
									if(applyInfos.get(sku)!=null){
										String arr1[]=applyInfos.get(sku).split(",");
										Integer newQuantity = Integer.parseInt(arr1[2]);
										//如果数量有变动,更新申请数量
										if(!quantity.equals(newQuantity)){
											this.qualityBillService.updateQuantityById(unSureId, amazonUnlineOrder.getId(), newQuantity);
											buf.append("&nbsp;&nbsp;未确认转码[id:"+unSureId+"]：sku["+sku+"]数量由：("+quantity+")变为("+newQuantity+")个");
										}
									}else{
										//删除了，自动取消
										this.qualityBillService.cancel(unSureId,null);
										buf.append("&nbsp;&nbsp;未确认转码[id:"+unSureId+"]：sku["+sku+"]自动取消数量：("+quantity+")个");
									}
								}else if("3".equals(changeBill.getChangeSta())){
									//如果是已审核状态
									if(changeBill.getItems()!=null&&changeBill.getItems().size()>0){
										for(PsiQualityChangeBill cBill:changeBills){
											buf.append("&nbsp;&nbsp;已确认转码[id:"+cBill.getId()+"]：sku["+cBill.getSku()+"]数量("+cBill.getQuantity()+")，已追回转换数量，");
											this.amazonUnlineOrderService.cancelQualityTypeInfos(cBill.getSku(), warehouseId, cBill.getQuantity());
										}
									}else{
										buf.append("&nbsp;&nbsp;已确认转码[id:"+changeBill.getId()+"]：sku["+changeBill.getSku()+"]数量("+changeBill.getQuantity()+")，已追回转换数量，");
										this.amazonUnlineOrderService.cancelQualityTypeInfos(changeBill.getSku(), warehouseId, changeBill.getQuantity());
									}
									//生成
									buf.append("(转码已确认后线下订单更改数量,追回原来("+sureQuantity+"个)确认的,重新生成转码确认信息)");
									if(applyInfos.size()>0){
										for (Map.Entry<String,String> entry: applyInfos.entrySet()) { 
										    String sku = entry.getKey();
											String arr[] =entry.getValue().split(",");
											String color = arr.length>6?arr[6]:"";
											psiInventoryService.createNewToOffline(Integer.parseInt(arr[0]),arr[1],Integer.parseInt(arr[2]), null, Integer.parseInt(arr[3]), arr[4],arr[5], color,amazonUnlineOrder.getId(),amazonUnlineOrder.getAmazonOrderId());
											buf.append("&nbsp;&nbsp; sku["+sku+"]数量：("+Integer.parseInt(arr[2])+")个");
										}
									}
								}
							}
						}
						//有些不需要申请转码，直接生效的，隐藏下面的
//						for (Map.Entry<String,String> entry: applyInfos.entrySet()) { 
//						    String sku = entry.getKey();
//							//如果本次编辑后的sku，已申请的里面没有
//						    buf.append("线下订单["+amazonUnlineOrder.getId()+"]新增sku:"+sku);
//							if(!appSkuSet.contains(sku)){
//								String arr[] = entry.getValue().split(",");
//								String color = arr.length>6?arr[6]:"";
//								psiInventoryService.createNewToOffline(Integer.parseInt(arr[0]),arr[1],Integer.parseInt(arr[2]), null, Integer.parseInt(arr[3]), arr[4],arr[5], color,amazonUnlineOrder.getId(),amazonUnlineOrder.getAmazonOrderId());
//								buf.append("&nbsp;&nbsp; sku["+sku+"]数量：("+Integer.parseInt(arr[2])+")个");
//								
//							}
//						}
					}
				}else{
					if(applyInfos.size()>0){
						for (Map.Entry<String,String> entry: applyInfos.entrySet()) { 
							String sku = entry.getKey();
							String arr[] = entry.getValue().split(",");
							String color = arr.length>6?arr[6]:"";
							Integer quantity = Integer.parseInt(arr[2]);
							
							PsiInventory inventory = this.psiInventoryService.findBySku(sku, amazonUnlineOrder.getSalesChannel().getId());
							//如果为淘汰，有库存就直接转成线下，没有还按原来逻辑走
							String key = inventory.getProductColorCountry();
							//如果是(淘汰品或者数量不大于10)(线下不够用、线上有库存)，，直接生效不用申请审核
							boolean offlineNoEnough = (inventory.getOfflineQuantity()-(unShippedMap!=null&&unShippedMap.get(sku)!=null?unShippedMap.get(sku):0)<quantity);
							boolean onlineEnough =(inventory.getNewQuantity()>quantity);
							if(("4".equals(productPositionMap.get(key))||(quantity<=10))&&offlineNoEnough&&onlineEnough){
								psiInventoryService.createSureNewToOffline(Integer.parseInt(arr[0]),arr[1],quantity, null, Integer.parseInt(arr[3]), arr[4],arr[5], color,amazonUnlineOrder.getId(),amazonUnlineOrder.getAmazonOrderId());
								this.amazonUnlineOrderService.updateInventory(sku, amazonUnlineOrder.getSalesChannel().getId(), quantity);
							}else{
								//新增时       生成转码申请
								buf.append("线下订单["+amazonUnlineOrder.getId()+"]新增！");
								psiInventoryService.createNewToOffline(Integer.parseInt(arr[0]),arr[1],quantity, null, Integer.parseInt(arr[3]), arr[4],arr[5], color,amazonUnlineOrder.getId(),amazonUnlineOrder.getAmazonOrderId());
								buf.append("&nbsp;&nbsp; sku["+sku+"]数量：("+quantity+")个");
							}
						}
					}
				}   
				
				//找出相应国家
				String country="de";
				for(AmazonUnlineOrderItem item:amazonUnlineOrder.getItems()){
					if(item.getSellersku().contains("-US")||item.getSellersku().contains("-COM")){
						country="us";
					}else if(item.getSellersku().contains("-JP")){
						country="jp";
					}else if(item.getSellersku().contains("-CA")){
						country="ca";
					}
					break;
				}
				
				//发信提醒销售
				Map<String,String> userInfo =this.groupUserService.getResponsibleByCountry(country);
				if(userInfo.size()>0&&StringUtils.isNotBlank(buf.toString())){
					String name="";
					String email ="";
					StringBuffer buf1= new StringBuffer();
					StringBuffer buf2= new StringBuffer();
					for (Map.Entry<String,String> entry: userInfo.entrySet()) { 
					    String userName = entry.getKey();
					    buf1.append(userName+",");
						buf2.append(entry.getValue()+",");
					}
					name=buf1.toString();
					email =buf2.toString();
					String content="Hi,"+name.substring(0, name.length()-1)+"<br/><br/>"+buf.toString()+"，请及时跟建单同事("+UserUtils.getUser().getName()+")联系，确认订单信息是否真实及确认sku等信息，【<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/psiQualityChangeBill/list'>点击此处</a>】进行确认或取消!<br/><br/><br/>best regards<br/>Erp System";
					String ccEmail="lena@inateck.com,"+UserUtils.getUser().getEmail();
					sendEmail(content, buf.toString(), email.substring(0, email.length()-1),ccEmail);
				}
				
			}
			
		}catch(Exception ex){
			LOGGER.info("线下订单转码",ex);
		}
		addMessage(redirectAttributes,MessageUtils.format("amazon_order_tips18",new Object[]{amazonUnlineOrder.getSellerOrderId()}));
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/unlineOrder/?repage";
	}
	
	@RequestMapping(value = "invoice")
	@ResponseBody
	public String invoice(String hasTax,String payment, String country,AmazonUnlineOrder amazonUnlineOrder,String order,String invoice) {
		File file = null;
		String itemIds=amazonUnlineOrder.getPaymentMethod();
		String quantitys=amazonUnlineOrder.getMarketplaceId();
		Date date=amazonUnlineOrder.getLastUpdateDate();
		Date deliveryDate=amazonUnlineOrder.getDeliveryDate();
		String remark=amazonUnlineOrder.getRemark();
		amazonUnlineOrder = this.amazonUnlineOrderService.get(amazonUnlineOrder.getId());
		if(StringUtils.isNotBlank(amazonUnlineOrder.getBuyerEmail())){
			String mail = amazonUnlineOrder.getBuyerEmail();
			String toAddress="";
			String toEmail = mail;
			if(toEmail.indexOf(",")>0){
				toEmail = mail.substring(0,mail.indexOf(","));
				String[] emailArr=mail.split(",");
				
				for (String arr: emailArr) {
					if(arr.startsWith("erp")){
						 String[] temp=mail.split("@");
						 arr=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
					 }
					toAddress+=arr+",";
				}
			}else{
				if(mail.startsWith("erp")){
					String[] arr=mail.split("@");
					mail=new String(Encodes.decodeBase64(arr[0].substring(3)))+"@"+arr[1];
				 }
				toAddress+=mail+",";
			}
			toAddress = toAddress.substring(0,toAddress.length()-1);
			amazonUnlineOrder.setBuyerEmail(toAddress);
		}
		if(StringUtils.isBlank(amazonUnlineOrder.getInvoiceNo())){
				String flag="";
				String suffix="";
				if(amazonUnlineOrder.getSalesChannel().getId()==19){
					flag="INVOICE_EU";
					suffix="E";
				}else if(amazonUnlineOrder.getSalesChannel().getId()==120){
					flag="INVOICE_US";
					suffix="U";
				}else if(amazonUnlineOrder.getSalesChannel().getId()==147){
					flag="INVOICE_JP";
					suffix="J";
				}
				
				if(amazonUnlineOrder.getInvoiceFlag().startsWith("000")){//未发送账单
					String invoiceNo=amazonOrderService.createFlowNo(flag,8,suffix);
					amazonUnlineOrder.setInvoiceNo(invoiceNo);
					amazonUnlineOrderService.updateInvoiceNoById(amazonUnlineOrder.getInvoiceNo(),amazonUnlineOrder.getId()) ;
				}else{
					amazonUnlineOrder.setInvoiceNo(amazonUnlineOrder.getId()+"");
				}
		}
		
		if(StringUtils.isBlank(amazonUnlineOrder.getInvoiceNo())){
			amazonUnlineOrder.setInvoiceNo("");
		}
		
	    if(StringUtils.isNotBlank(payment)){
	    	amazonUnlineOrder.setPaymentMethod(payment);
	    }
		if(StringUtils.isNotBlank(remark)){
			amazonUnlineOrder.setRemark(remark);
		}
		if(itemIds!=null&&quantitys!=null){
			//说明是退款单   itemid  数量
			if(date!=null){
				amazonUnlineOrder.setLastUpdateDate(date);
			}
			if("4".equals(hasTax)){
				file = SendEmailByOrderMonitor.genPartPdfByRefund(amazonProductService,country, amazonUnlineOrder, hasTax,itemIds,quantitys);
			}else{
				file = SendEmailByOrderMonitor.genPdfByRefund(amazonProductService,country, amazonUnlineOrder, hasTax,itemIds,quantitys,order,invoice);
			}
		}else{
			if(deliveryDate!=null){
				amazonUnlineOrder.setDeliveryDate(deliveryDate);
			}
			if("管理员".equals(amazonUnlineOrder.getOrderChannel())){
				file = SendEmailByOrderMonitor.genPdf2(amazonProductService,country, amazonUnlineOrder, hasTax,order,invoice);
			}else{
				file = SendEmailByOrderMonitor.genPdf(amazonProductService,country, amazonUnlineOrder, hasTax,order,invoice);
			}
			
		}
		if (file != null) {
			return "1";
		} else {
			return "0";
		}
	}
	
	@RequestMapping(value = "send")
	public String send(String bcc, String mail, String hasTax,String payment,String country,AmazonUnlineOrder amazonUnlineOrder, Model model,RedirectAttributes redirectAttributes,String order,String invoice) {
		//File file = null;
		//String itemIds=amazonUnlineOrder.getPaymentMethod();
		//String quantitys=amazonUnlineOrder.getMarketplaceId();
		//Date date=amazonUnlineOrder.getLastUpdateDate();
		//amazonUnlineOrder = this.amazonUnlineOrderService.get(amazonUnlineOrder.getId());
		/*if(itemIds!=null&&quantitys!=null){
			if(date!=null){
				amazonUnlineOrder.setLastUpdateDate(date);
			}
			file = SendEmailByOrderMonitor.genPdfByRefund(amazonProductService,country, amazonUnlineOrder, hasTax,itemIds,quantitys,order,invoice);
		}else{
			file = SendEmailByOrderMonitor.genPdf(amazonProductService,country, amazonUnlineOrder, hasTax,order,invoice);
		}*/
		File file = null;
		String itemIds=amazonUnlineOrder.getPaymentMethod();
		String quantitys=amazonUnlineOrder.getMarketplaceId();
		Date date=amazonUnlineOrder.getLastUpdateDate();
		Date deliveryDate=amazonUnlineOrder.getDeliveryDate();
		String remark=amazonUnlineOrder.getRemark();
		amazonUnlineOrder = this.amazonUnlineOrderService.get(amazonUnlineOrder.getId());
		
		if(StringUtils.isBlank(amazonUnlineOrder.getInvoiceNo())){
			String flag="";
			String suffix="";
			if(amazonUnlineOrder.getSalesChannel().getId()==19){
				flag="INVOICE_EU";
				suffix="E";
			}else if(amazonUnlineOrder.getSalesChannel().getId()==120){
				flag="INVOICE_US";
				suffix="U";
			}else if(amazonUnlineOrder.getSalesChannel().getId()==147){
				flag="INVOICE_JP";
				suffix="J";
			}
			if(amazonUnlineOrder.getInvoiceFlag().startsWith("000")){//未发送账单
				String invoiceNo=amazonOrderService.createFlowNo(flag,8,suffix);
				amazonUnlineOrder.setInvoiceNo(invoiceNo);
				amazonUnlineOrderService.updateInvoiceNoById(amazonUnlineOrder.getInvoiceNo(),amazonUnlineOrder.getId()) ;
			}else{
				amazonUnlineOrder.setInvoiceNo(amazonUnlineOrder.getId()+"");
			}
		}
		
		if(StringUtils.isBlank(amazonUnlineOrder.getInvoiceNo())){
			amazonUnlineOrder.setInvoiceNo("");
		}
		
	    if(StringUtils.isNotBlank(payment)){
	    	amazonUnlineOrder.setPaymentMethod(payment);
	    }
		if(StringUtils.isNotBlank(remark)){
			amazonUnlineOrder.setRemark(remark);
		}
		if(itemIds!=null&&quantitys!=null){
			//说明是退款单   itemid  数量
			if(date!=null){
				amazonUnlineOrder.setLastUpdateDate(date);
			}
			if("4".equals(hasTax)){
				file = SendEmailByOrderMonitor.genPartPdfByRefund(amazonProductService,country, amazonUnlineOrder, hasTax,itemIds,quantitys);
			}else{
				file = SendEmailByOrderMonitor.genPdfByRefund(amazonProductService,country, amazonUnlineOrder, hasTax,itemIds,quantitys,order,invoice);
			}
		}else{
			if(deliveryDate!=null){
				amazonUnlineOrder.setDeliveryDate(deliveryDate);
			}
			if("管理员".equals(amazonUnlineOrder.getOrderChannel())){
				file = SendEmailByOrderMonitor.genPdf2(amazonProductService,country, amazonUnlineOrder, hasTax,order,invoice);
			}else{
				file = SendEmailByOrderMonitor.genPdf(amazonProductService,country, amazonUnlineOrder, hasTax,order,invoice);
			}
		}
		
		if (file != null) {
			Map<String, String> params = Maps.newHashMap();
			for (AmazonUnlineOrderItem amazonOrderItem : amazonUnlineOrder.getItems()) {
				params.put("asin", amazonOrderItem.getAsin());
				break;
			}
			String toEmail = mail;
			if(toEmail.indexOf(",")>0){
				toEmail = mail.substring(0,mail.indexOf(","));
			}
			
			String toAddress="";
			if(toEmail.indexOf(",")>0){
				toEmail = mail.substring(0,mail.indexOf(","));
				String[] emailArr=mail.split(",");
				
				for (String arr: emailArr) {
					if(arr.startsWith("erp")){
						 String[] temp=mail.split("@");
						 arr=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
					 }
					toAddress+=arr+",";
				}
			}else{
				if(mail.startsWith("erp")){
					String[] arr=mail.split("@");
					mail=new String(Encodes.decodeBase64(arr[0].substring(3)))+"@"+arr[1];
				 }
				toAddress+=mail+",";
			}
			toAddress = toAddress.substring(0,toAddress.length()-1);
			
			//不管怎样都放入token
			params.put("token",UnsubscribeEmailService.getUnsubscribeEmaliHref(toEmail));
			String template = SendEmailByOrderMonitor.getTemplate("invoice","_" + country, params);
			String subject = "invoice";
			if ("de".equals(country)) {	subject = "Kaufbeleg";
			} else if ("fr".equals(country)) {
				subject = "La facture de votre commande";
			}
			MailInfo mailInfo = new MailInfo(toAddress, subject + " "+ amazonUnlineOrder.getAmazonOrderId(), new Date());
			mailInfo.setContent(HtmlUtils.htmlUnescape(template));
			mailInfo.setFileName(file.getName());
			mailInfo.setFilePath(file.getAbsolutePath());
			if (StringUtils.isNotEmpty(bcc)) {
				mailInfo.setBccToAddress(bcc);
			}
			if (sendCustomEmailManager.send(mailInfo)) {
				addMessage(redirectAttributes, MessageUtils.format("amazon_order_tips19"));
				try{
					amazonUnlineOrderService.updateInvoiceFlag(Sets.newHashSet(amazonUnlineOrder.getAmazonOrderId()));
				}catch(Exception e){}
				
			} else {
				addMessage(redirectAttributes, MessageUtils.format("amazon_order_tips20"));
			}
		}
		return "redirect:" + Global.getAdminPath()+ "/amazoninfo/unlineOrder/form?id=" + amazonUnlineOrder.getId();
	}
	
	@RequestMapping(value = "add")
	public String add(AmazonUnlineOrder amazonUnlineOrder,Model model) throws UnsupportedEncodingException {
	
			//model.addAttribute("sku",amazonProductService.findSku(amazonUnlineOrder.getSalesChannel()));
		   if(amazonUnlineOrder!=null){
			   if(StringUtils.isNotEmpty(amazonUnlineOrder.getBuyerName())){
				   amazonUnlineOrder.setBuyerName(URLDecoder.decode(amazonUnlineOrder.getBuyerName(),"utf-8"));
			   }
			  if(amazonUnlineOrder.getShippingAddress()!=null){
				  AmazonUnlineAddress addr=amazonUnlineOrder.getShippingAddress();
				   if(StringUtils.isNotEmpty(addr.getAddressLine1())){
						addr.setAddressLine1(URLDecoder.decode(addr.getAddressLine1(),"utf-8"));
					}
					if(StringUtils.isNotEmpty(addr.getAddressLine2())){
						addr.setAddressLine2(URLDecoder.decode(addr.getAddressLine2(),"utf-8"));
					}
					if(StringUtils.isNotEmpty(addr.getAddressLine3())){
						addr.setAddressLine3(URLDecoder.decode(addr.getAddressLine3(),"utf-8"));
					}
					
					if(StringUtils.isNotEmpty(addr.getCity())){
						addr.setCity(URLDecoder.decode(addr.getCity(),"utf-8"));
					}
					if(StringUtils.isNotEmpty(addr.getCounty())){
						addr.setCounty(URLDecoder.decode(addr.getCounty(),"utf-8"));
					}
					if(StringUtils.isNotEmpty(addr.getStateOrRegion())){
						addr.setStateOrRegion(URLDecoder.decode(addr.getStateOrRegion(),"utf-8"));
					}
					if(StringUtils.isNotEmpty(addr.getName())){
						addr.setName(URLDecoder.decode(addr.getName(),"utf-8"));
					}
					if(StringUtils.isNotEmpty(addr.getPhone())){
						addr.setPhone(URLDecoder.decode(addr.getPhone(),"utf-8"));
					}
					amazonUnlineOrder.setShippingAddress(addr);
			  }
			 
		   }
		List<PsiInventory> inventorys=psiInventoryService.findByStock((amazonUnlineOrder==null||amazonUnlineOrder.getSalesChannel()==null)?19:amazonUnlineOrder.getSalesChannel().getId());
		Map<String,String>  productNameSkuMap = Maps.newHashMap();
		for(PsiInventory inventory:inventorys){
			productNameSkuMap.put(inventory.getSku(), inventory.getProductName()+(StringUtils.isBlank(inventory.getColorCode())?"":("_"+inventory.getColorCode()))+"["+inventory.getSku()+"]");
		}
		model.addAttribute("sku", JSON.toJSON(productNameSkuMap));
		model.addAttribute("amazonUnlineOrder", amazonUnlineOrder);
		List<Stock> stocks =stockService.findStocks("0");
		model.addAttribute("stocks",stocks);
		/*List<String> supplier=new ArrayList<String>();
		List<String> airSupplier= LogisticsSupplier.getLogisticsSupplierByType("1");
		List<String> expSupplier= LogisticsSupplier.getLogisticsSupplierByType("2");
		List<String> seaSupplier= LogisticsSupplier.getLogisticsSupplierByType("3");
		supplier.addAll(airSupplier);
		supplier.addAll(expSupplier);
		supplier.addAll(seaSupplier);*/
		model.addAttribute("typeSupplier",model.addAttribute("typeSupplier", LogisticsSupplier.getLogisticsSupplierByType("4")));
		return "modules/amazoninfo/order/amazonOrderAdd";
	}
	
	
	@RequestMapping(value = "exportDetail")
	public String exportDetail(AmazonUnlineOrder amazonUnlineOrder, HttpServletRequest request,HttpServletResponse response, Model model) {
		List<AmazonUnlineOrder> list = amazonUnlineOrderService.find(amazonUnlineOrder);
		HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
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
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		row.setHeight((short) 600);
		HSSFCell cell = null;	
		List<String>  title=Lists.newArrayList("email","name","purchase_date","amazon_order_id","order_state","createUser","warehouse","country","quantity","item_price");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		int  rowIndex=1;
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		for (AmazonUnlineOrder order : list) {
			for (AmazonUnlineOrderItem item: order.getItems()) {
				row=sheet.createRow(rowIndex++);
				int m=0;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(order.getBuyerEmail()==null?"":order.getBuyerEmail());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(item.getProductName()+(StringUtils.isNotBlank(item.getColor())?("_"+item.getColor()):""));
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(dateFormat.format(order.getPurchaseDate()));
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(order.getAmazonOrderId());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(order.getOrderStatus());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(order.getOrderChannel());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(("21".equals(order.getSalesChannel().getId().toString())||"130".equals(order.getSalesChannel().getId().toString()))?"中国仓":("120".equals(order.getSalesChannel().getId().toString())?"美国仓":"德国仓"));
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(item.getCountry());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantityOrdered());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(item.getItemPrice());
			}
		}
		 for (int i=1;i<rowIndex;i++) {
       	      for (int j = 0; j < title.size(); j++) {
       	    	 if(j==title.size()-1){
       	    		sheet.getRow(i).getCell(j).setCellStyle(cellStyle);
       	    	 }else{
       	    		sheet.getRow(i).getCell(j).setCellStyle(contentStyle);
       	    	 }
	        	  
			 }
         }
		  for (int i = 0; i < title.size(); i++) {
       		 sheet.autoSizeColumn((short)i, true);
		  }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "线下订单" + sdf.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value = "exportCount")
	public String exportCount(AmazonUnlineOrder amazonUnlineOrder, HttpServletRequest request,HttpServletResponse response, Model model) {
		List<Object[]> list = amazonUnlineOrderService.count(amazonUnlineOrder);
		HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
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
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		
		row.setHeight((short) 600);
		HSSFCell cell = null;	
		List<String>  title=Lists.newArrayList("warehouse","country","name","quantity");
		for(int i = 0; i < title.size(); i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short)i);
		}
		int  rowIndex=1;
		for (Object[] obj : list) {
				row=sheet.createRow(rowIndex++);
				int m=0;
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(("21".equals(obj[1])||"130".equals(obj[1]))?"中国仓":("120".equals(obj[1])?"美国仓":"德国仓"));
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[2]==null?"":obj[2].toString());
				row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(obj[0]==null?"":obj[0].toString());
				row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(((BigDecimal)obj[3]).intValue());
		}
		 for (int i=1;i<rowIndex;i++) {
       	      for (int j = 0; j < title.size(); j++) {
       	    		sheet.getRow(i).getCell(j).setCellStyle(contentStyle);
			 }
         }
		  for (int i = 0; i < title.size(); i++) {
       		 sheet.autoSizeColumn((short)i, true);
		  }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "线下订单汇总" + sdf.format(new Date()) + ".xls";
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
	
	
	
	public String toJson(List<AmazonUnlineOrderItem> items) {
		StringBuffer buf= new StringBuffer("[");
		for (int i=0;i<items.size();i++) {
			AmazonUnlineOrderItem item=items.get(i);
			buf.append(item.toString());
			if(i!=items.size()-1){
				buf.append(",");
			}
		}
		buf.append("]");
		return buf.toString();
	}
	
	 public  String toJson(String s) {
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < s.length(); i++) {
             char c = s.toCharArray()[i];
             switch (c) {
                 case '\"': sb.append("\\\""); break;
                 case '\\': sb.append("\\\\"); break;
                 case '/': sb.append("\\/"); break;
                 case '\b': sb.append("\\b"); break;
                 case '\f': sb.append("\\f"); break;
                 case '\n': sb.append("\\n"); break;
                 case '\r': sb.append("\\r"); break;
                 case '\t': sb.append("\\t"); break;
                 default: if ((c >= 0 && c <= 31)||c ==127){}                  
                 else{sb.append(c);}break;
             }
         }
         return sb.toString();
     }
	
	 public void sendEmail(String content,String title,String sendEmail,String ccEmail){
			Date date = new Date();
			final MailInfo mailInfo1 = new MailInfo(sendEmail,title,date);
			mailInfo1.setContent(content);
			mailInfo1.setCcToAddress(ccEmail);
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo1);
				}
			}.start();
		}
	 
	 
	 @RequestMapping(value = "readOrderFile")
	 public String readOrderFile(@RequestParam("excel")MultipartFile excelFile,Integer stockId,RedirectAttributes redirectAttributes){
		     String userName=UserUtils.getUser().getName();
			 try{	
				   Date date=new Date();
				   try {
							String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/otherOrder/"+new SimpleDateFormat("yyyyMMddHHmmss").format(date);
							File baseDir = new File(baseDirStr); 
							if(!baseDir.isDirectory())
								baseDir.mkdirs();
							String name = excelFile.getOriginalFilename();
							File dest = new File(baseDir,name);
							if(dest.exists()){
								dest.delete();
							}
							FileUtils.copyInputStreamToFile(excelFile.getInputStream(),dest);
					}catch(Exception e){}	
					
					Workbook workBook= WorkbookFactory.create(excelFile.getInputStream());
					Sheet sheet = workBook.getSheetAt(0);
					sheet.setForceFormulaRecalculation(true);
				   
					Map<String,AmazonUnlineOrder> orderMap=Maps.newHashMap();
					for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
						Row row = sheet.getRow(rowNum);
						if (row == null) {
							continue;
						}
					    String orderId=StringUtils.trim(getData(row.getCell(0)));
					    if(StringUtils.isBlank(orderId)){
					    	continue;
					    }
					    AmazonUnlineOrder existOrder=amazonUnlineOrderService.getByOrderId(orderId);
					    if(existOrder!=null){
					    	continue;
					    }
					    if(orderMap.get(orderId)!=null){
					    	    AmazonUnlineOrder order=orderMap.get(orderId);
					    	    String country=StringUtils.trim(getData(row.getCell(4)));
							    String productName=StringUtils.trim(getData(row.getCell(5)));
							    String quantity=StringUtils.trim(getData(row.getCell(6)));
							    String itemPrice=StringUtils.trim(getData(row.getCell(7)));
							    String itemTax=StringUtils.trim(getData(row.getCell(9)));
							    
							    List<AmazonUnlineOrderItem> items=order.getItems();
							    AmazonUnlineOrderItem item=new AmazonUnlineOrderItem();
							    
							    String color = "";
								String name=productName;
								if(productName.indexOf("_")>0){
									name=productName.substring(0,productName.lastIndexOf("_"));
									color = productName.substring(productName.lastIndexOf("_")+1);
								}
							    
							    PsiSku psiSku=productService.getPsiSku(name,country,color);
							    if(psiSku!=null){
							    	item.setSellersku(psiSku.getSku());
				                    item.setAsin(psiSku.getAsin());
				                    item.setProductName(psiSku.getProductName());
	                                item.setColor(psiSku.getColor());
							    }else{
							    	item.setSellersku("");
				                    item.setAsin("");
				                    item.setProductName(productName);
	                                item.setColor("");
							    }
							    item.setCountry(country);
			                    item.setItemPrice(Float.parseFloat(itemPrice));
			                    item.setQuantityShipped(Integer.parseInt(quantity));
			                    item.setQuantityOrdered(Integer.parseInt(quantity));
			                    item.setItemTax(Float.parseFloat(itemTax));
			                    item.setShippingPrice(0f);
	                            item.setGiftWrapPrice(0f);
			                    item.setTitle(productName);
			                    item.setOrder(order);
			                    items.add(item);
			                    order.setOrderTotal(order.getOrderTotal()+item.getItemPrice()*item.getQuantityShipped());
			                    order.setNumberOfItemsShipped(order.getNumberOfItemsShipped()+item.getQuantityShipped());
			                    order.setNumberOfItemsUnshipped(order.getNumberOfItemsUnshipped()+(item.getQuantityOrdered()-item.getQuantityShipped()));
			                  
					    }else{
					    	String purchaseDate=StringUtils.trim(getData(row.getCell(1)));
						    String email=StringUtils.trim(getData(row.getCell(2)));
						    String buyerName=StringUtils.trim(getData(row.getCell(3)));
						    
						    String country=StringUtils.trim(getData(row.getCell(4)));
						    String productName=StringUtils.trim(getData(row.getCell(5)));
						    String quantity=StringUtils.trim(getData(row.getCell(6)));
						    String itemPrice=StringUtils.trim(getData(row.getCell(7)));
						    String currency=StringUtils.trim(getData(row.getCell(8)));
						    String itemTax=StringUtils.trim(getData(row.getCell(9)));
						    
						    
						    String receiver=StringUtils.trim(getData(row.getCell(10)));
						    String address=StringUtils.trim(getData(row.getCell(11)));
						    String city=StringUtils.trim(getData(row.getCell(12)));
						    String stateOrRegion=StringUtils.trim(getData(row.getCell(13)));
						    String postalCode=StringUtils.trim(getData(row.getCell(14)));
						    String countryCode=StringUtils.trim(getData(row.getCell(15)));
						    String phone=StringUtils.trim(getData(row.getCell(16)));
						    
						    String type=StringUtils.trim(getData(row.getCell(17)));
						  
						    AmazonUnlineOrder order=new AmazonUnlineOrder();
						    order.setOutBound("0");
						    order.setSalesChannel(new Stock(stockId));
						    order.setAmazonOrderId(orderId);
					    	order.setSellerOrderId(orderId);
					    	order.setOrderStatus("Waiting for delivery");
					    	order.setFulfillmentChannel("MFN");
					    	order.setLastUpdateDate(new Date());
					    	order.setShipServiceLevel("Standard");
	                  		order.setShipmentServiceLevelCategory("Standard");
	                  		order.setOrderType("Standard");
	                  		order.setOrderChannel(userName+"-OTHER-"+type);
	                  		order.setPaymentMethod("Other");
	                  		order.setMarketplaceId(currency);
	                  		order.setBuyerEmail(email);
	                  		order.setBuyerName(buyerName);
	                  		order.setPurchaseDate(dateFormat.parse(purchaseDate));
	                  		
	                  		AmazonUnlineAddress shippingAddress=new AmazonUnlineAddress();
		                    shippingAddress.setName(receiver);
		                    shippingAddress.setAddressLine1(address);
		                    shippingAddress.setCity(city);
		                    shippingAddress.setStateOrRegion(stateOrRegion);
		                    shippingAddress.setPostalCode(postalCode);
		                    shippingAddress.setCountryCode(countryCode);
		                    shippingAddress.setPhone(phone);
		                    order.setShippingAddress(shippingAddress);
		                    
		                    List<AmazonUnlineOrderItem> items = Lists.newArrayList();
		                    AmazonUnlineOrderItem item=new AmazonUnlineOrderItem();
		                    
		                    String color = "";
							String name=productName;
							if(productName.indexOf("_")>0){
								name=productName.substring(0,productName.lastIndexOf("_"));
								color = productName.substring(productName.lastIndexOf("_")+1);
							}
						    
						    PsiSku psiSku=productService.getPsiSku(name,country,color);
						    if(psiSku!=null){
						    	item.setSellersku(psiSku.getSku());
			                    item.setAsin(psiSku.getAsin());
			                    item.setProductName(psiSku.getProductName());
                                item.setColor(psiSku.getColor());
						    }else{
						    	item.setSellersku("");
			                    item.setAsin("");
			                    item.setProductName(productName);
                                item.setColor("");
						    }
						    item.setCountry(country);
		                    item.setItemPrice(Float.parseFloat(itemPrice));
		                    item.setQuantityShipped(Integer.parseInt(quantity));
		                    item.setQuantityOrdered(Integer.parseInt(quantity));
		                    item.setItemTax(Float.parseFloat(itemTax));
		                    item.setShippingPrice(0f);
                            item.setGiftWrapPrice(0f);
		                    item.setTitle(productName);
		                    item.setOrder(order);
		                    items.add(item);
		                    order.setOrderTotal(item.getItemPrice()*item.getQuantityShipped());
		                    order.setNumberOfItemsShipped(item.getQuantityShipped());
		                    order.setNumberOfItemsUnshipped(item.getQuantityOrdered()-item.getQuantityShipped());
		                    order.setItems(items);
		                    orderMap.put(orderId, order);
					    }
					}   
					 if(orderMap.size()>0){
						 List<AmazonUnlineOrder> orders=Lists.newArrayList();
						 for (Map.Entry<String,AmazonUnlineOrder> entry: orderMap.entrySet()) {
							 orders.add(entry.getValue());
						 }
						 amazonUnlineOrderService.save(orders);
					 }
				}catch (Exception e) {
					LOGGER.warn("Other order"+e.getMessage(),e.getMessage());
					addMessage(redirectAttributes,"error:保存订单失败");
					return "redirect:"+Global.getAdminPath()+"/amazoninfo/unlineOrder/?repage";
				}
			  return "redirect:"+Global.getAdminPath()+"/amazoninfo/unlineOrder/?repage";
	 }
	 
	 
	 @RequestMapping(value = "otherOrderAdd")
	 public String otherOrderAdd(HttpServletRequest request,HttpServletResponse response, Model model) {
			model.addAttribute("stocks", stockService.findStocks("0"));
			return "/modules/amazoninfo/order/otherOrderAdd";
	 }
	 
	 @RequestMapping(value = "downloadTemplate")
		public String downloadTemplate(HttpServletRequest request, HttpServletResponse response){
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			List<String> title = Lists.newArrayList("order_id","purchase_date","email","buyer_name","country","product_name","quantity","item_price","currency","item_price_tax","receiver","address","city","stateOrRegion","postalCode","countryCode","phone","platform");
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
			
			CellStyle contentStyle = wb.createCellStyle();
			contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
			contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
			contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 16); // 字体高度
			font.setFontName(" 黑体 "); // 字体
			font.setBoldweight((short) 16);
			style.setFont(font);
			row.setHeight((short) 600);
		
			
			HSSFCell cell = null;		
			for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
			int index=1;
			
		
			for (int i=0;i<=2;i++) {
				int j=0;
				row=sheet.createRow(index++);
				if(i==0||i==1){
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("111-11-11");
				}else{
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("111-11-12");
				}
			
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("2017-11-13");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("test@xxx.com");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("Ronny");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com");
				row.getCell(j-1).setCellStyle(contentStyle);
			
				if(i==0||i==2){
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("Inateck MP1300_black");
				}else{
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("Inateck FEU3NS-1_black");
				}
				row.getCell(j-3).setCellStyle(contentStyle);
				row.getCell(j-2).setCellStyle(contentStyle);
				row.getCell(j-1).setCellStyle(contentStyle);
				
				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("1");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("19.99");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("USD");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("6.47");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("Ronny");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("Waldstr. 11");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("Bonn");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("Nordrhein-Westfalen");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("53179");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("US");
				row.getCell(j-1).setCellStyle(contentStyle);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("10000000");
				row.getCell(j-1).setCellStyle(contentStyle);
				if(i==0||i==1){
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("淘宝");
				}else{
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("京东");
				}
				row.getCell(j-1).setCellStyle(contentStyle);
			}
			
			for (int i = 0; i < title.size(); i++) {
	       		 sheet.autoSizeColumn((short)i,true);
			}
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmm");
				String fileName = "Template_" + sdf.format(new Date()) + ".xls";
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
	 
	 private static SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		private static SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy/MM/dd");
		public String getData(Cell cell){
			String value="";
			if(cell!=null){

				switch (cell.getCellType()) {
			        case HSSFCell.CELL_TYPE_NUMERIC: // 数字
			            value = cell.getNumericCellValue() + "";
			            if (HSSFDateUtil.isCellDateFormatted(cell)) {
			                Date date = cell.getDateCellValue();
			                try{
			                	 value = dateFormat.format(date);
			                }catch(Exception e){
			                	 value = dateFormat.format(dateFormat2.format(date));
			                }
			               
			             } else {
			            	 value = new DecimalFormat("0.00").format(cell.getNumericCellValue());
			                 value=value.replace(".00","");
			             }
			            break;
			        case HSSFCell.CELL_TYPE_STRING: // 字符串
			            value = cell.getStringCellValue();
			            break;
			        case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
			            value = cell.getBooleanCellValue() + "";
			            break;
			        case HSSFCell.CELL_TYPE_FORMULA: // 公式
			            value = new DecimalFormat("0.00").format(cell.getNumericCellValue());
		                value=value.replace(".00","");
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
			}
			return value;
		}
	
}
