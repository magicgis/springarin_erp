package com.springrain.erp.modules.amazoninfo.web.order;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.ApiException;
import com.ebay.sdk.ApiLogging;
import com.ebay.sdk.SdkException;
import com.ebay.sdk.call.CompleteSaleCall;
import com.ebay.soap.eBLBaseComponents.ShipmentTrackingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShipmentType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.IdGen;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.MessageUtils;
import com.springrain.erp.common.utils.ZipUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonUnlineOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnPackage;
import com.springrain.erp.modules.amazoninfo.htmlunit.LoginUtil;
import com.springrain.erp.modules.amazoninfo.scheduler.SendCustomEmail1Manager;
import com.springrain.erp.modules.amazoninfo.scheduler.SendEmailByOrderMonitor;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonUnlineOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.MfnOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.MfnPackageService;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.ebay.scheduler.EbayConstants;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.logistics.dpd.Dpd;
import com.springrain.logistics.dpd.DpdService;
import com.springrain.logistics.usps.UspsL5;
import com.springrain.logistics.usps.UspsL5Service;


@Controller
@RequestMapping(value = "${adminPath}/amazonAndEbay/mfnOrder")
public class MfnOrderController extends BaseController {

	@Autowired
	private MfnOrderService mfnOrderService;
	@Autowired
	private PsiInventoryService psiInventoryService;
	@Autowired
	private PsiProductService productService;
	@Autowired
	private MfnPackageService mfnPackageService;
	@Autowired
	private EventService eventService;
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	@Autowired
	private AmazonUnlineOrderService amazonUnlineOrderService;
	@Autowired
	private MailManager              mailManager;
	@Autowired
	private SendCustomEmail1Manager customEmailBakManager;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	private  final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@ModelAttribute
	public MfnOrder get(@RequestParam(required = false) String id) {
		if (id != null) {
			return mfnOrderService.get(id);
		} else {
			return new MfnOrder();
		}
	}

	@RequestMapping(value = "")
	public String toExport(MfnOrder mfnOrder, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<MfnOrder> page = new Page<MfnOrder>(request, response);
		if (mfnOrder.getBuyTime()== null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			mfnOrder.setBuyTime(DateUtils.addDays(today, -15));
			mfnOrder.setLastModifiedTime(today);
		}
		if(StringUtils.isBlank(mfnOrder.getCountry())){
			mfnOrder.setCountry("de");
		}
		if(StringUtils.isBlank(mfnOrder.getStatus())){
			mfnOrder.setStatus("0");
		}
		page = mfnOrderService.ordersManager(page, mfnOrder);
		if(mfnOrder.getCountry().startsWith("com")){
			 Map<String,Integer> stockMap=psiInventoryService.findQuantityByWarehouseId(120);
			 model.addAttribute("stockMap",stockMap);
		}else if(mfnOrder.getCountry().startsWith("de")){
			 Map<String,Integer> printQtyMap=mfnOrderService.findPrintQuantity("de");
			 Map<String,Integer> stockMap=psiInventoryService.findQuantityByWarehouseId2(19);
			 if(printQtyMap!=null&&printQtyMap.size()>0){
				 for (Map.Entry<String,Integer> entry: printQtyMap.entrySet()) {
					  String name=entry.getKey();
					  Integer qty=entry.getValue();
					  if(stockMap.get(name)==null){
						  stockMap.put(name,0);
					  }else{
						  stockMap.put(name, stockMap.get(name)-qty);
					  }
				 }
			 }
			 model.addAttribute("stockMap",stockMap);
		}
		model.addAttribute("page", page);
		return "/modules/ebay/order/shipmentManager";
	}

	@RequestMapping(value = "countDPD")
	public String countDPD(MfnOrder mfnOrder, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		if (mfnOrder.getBuyTime()== null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			mfnOrder.setBuyTime(DateUtils.addDays(today, -30));
			mfnOrder.setPaidTime(today);
		}
		 Map<String,Map<String,Integer>> map=mfnOrderService.findDPDAmount(mfnOrder);
		 model.addAttribute("map", map);
		return "/modules/ebay/order/dpdAmountManager";
	}
	
	/*@RequestMapping(value = "synchronizeOrder")
	@ResponseBody
	public String synchronizeOrder(Integer unlineId){
		AmazonUnlineOrder amazonUnlineOrder=amazonUnlineOrderService.get(unlineId);
		try{
			boolean flag=mfnOrderService.synchronizeOrder(amazonUnlineOrder);
			if(flag){
				return "1";
			}else{
				return "0";
			}
		}catch(Exception e){
			return "0";
		}
		
	}*/
	
	@RequestMapping(value = "synchronizeOrderStatus")
	@ResponseBody
	public String synchronizeOrder(Integer unlineId){
		AmazonUnlineOrder amazonUnlineOrder=amazonUnlineOrderService.get(unlineId);
		//如果同步失败就返回0
		StringBuffer sb = new StringBuffer();
		String orderChannel = amazonUnlineOrder.getOrderChannel();
		if((!"管理员".equals(orderChannel)&&!"CHECK24".equals(orderChannel))&&(amazonUnlineOrder.getSalesChannel()!=null&&(amazonUnlineOrder.getSalesChannel().getId().intValue()==19||amazonUnlineOrder.getSalesChannel().getId().intValue()==120))){
			Map<String,Integer> unShippedMap =this.psiInventoryService.getUnShippedQuantity(amazonUnlineOrder.getSalesChannel().getId());
			for(AmazonUnlineOrderItem item:amazonUnlineOrder.getItems()){
				String sku = item.getSellersku();
				PsiInventory inventory = psiInventoryService.findBySku(sku, amazonUnlineOrder.getSalesChannel().getId()) ;
				if(inventory!=null){
					Integer stockQ = inventory.getOfflineQuantity();
					if(unShippedMap!=null&&unShippedMap.get(sku)!=null){
							stockQ-=unShippedMap.get(sku);
					}
					if(stockQ.intValue()<item.getQuantityOrdered().intValue()){
						sb.append(sku+" not enough stock(offline),need ("+item.getQuantityOrdered()+"),only ("+stockQ+")");
					}
				}else{
					sb.append(sku+" not enough stock(offline),need ("+item.getQuantityOrdered()+"),only (0)");
				}
			}
		}
		
		if(StringUtils.isNotEmpty(sb.toString())){
			return "error:"+sb.toString();
		}
		boolean flag=mfnOrderService.synchronizeOrderStatus(amazonUnlineOrder);
		if(flag){
			return "1";
		}else{
			return "0";
		}
	}
	
	
	@RequestMapping(value = "printPdf")
	@ResponseBody
	public String invoice(String printIds,String country) {
		if(StringUtils.isNotBlank(printIds)){
			String[] idArr=printIds.split(",");
			String  flag=mfnOrderService.getIsExistPackage(idArr);
			if("1".equals(flag)){
				Map<String,MfnOrder> idMap=mfnOrderService.findAllOrder(idArr);
				if(country.startsWith("de")){
					 Map<String,Integer> printQtyMap=mfnOrderService.findPrintQuantity("de");
					 Map<String,Integer> stockMap=psiInventoryService.findQuantityByWarehouseId(19);
					 if(printQtyMap!=null&&printQtyMap.size()>0){
						 for (Map.Entry<String,Integer> entry: printQtyMap.entrySet()) {
							  String name=entry.getKey();
							  Integer qty=entry.getValue();
							  if(stockMap.get(name)==null){
								  stockMap.put(name,0);
							  }else{
								  stockMap.put(name, stockMap.get(name)-qty);
							  }
						 }
					 }
					 Map<String,Integer> curMap=Maps.newHashMap();
					 for (Map.Entry<String,MfnOrder> orderMap:idMap.entrySet()) {
						 MfnOrder order=orderMap.getValue();
						 for (MfnOrderItem item: order.getItems()) {
							  if(!"Inateck Old".equals(item.getTitle())&&!"Inateck other".equals(item.getTitle())&&StringUtils.isNotBlank(item.getTitle())){
								  curMap.put(item.getTitle(),item.getQuantityPurchased()+(curMap.get(item.getTitle())==null?0:curMap.get(item.getTitle()) ));
							  }
						 }
					 }
					 
					 String info="";
					 for(Map.Entry<String,Integer> curEty: curMap.entrySet()) {
						  String name=curEty.getKey();
						  Integer qty=curEty.getValue();
						  if(stockMap.get(name)==null){
							  info+=name+" no stocks<br/>";
						  }else if(qty>stockMap.get(name)){
							  info+=name+" lower stocks,warehouse:"+stockMap.get(name)+",print quantity:"+qty+"<br/>";
						  }
					 }
					 if(StringUtils.isNotBlank(info)){
						 return "Operation has been canceled！！！<br/>"+info;
					 }
				}
				 
				List<MfnOrder> mfnOrderList=new ArrayList<MfnOrder>();
				String packageNo="PK_"+new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
				Map<Integer,Map<String,Integer>> map=mfnOrderService.savePackage(idArr,packageNo,country);
				Integer tempId=(Integer)Lists.newArrayList(map.keySet()).get(0);
				List<MfnOrder> amazonAndEbayOrder=new ArrayList<MfnOrder>();
				
				for (int i = 0; i < idArr.length; i++) {
					MfnOrder mfnOrder=idMap.get(idArr[i]);
					mfnOrder.setBillNo(map.get(tempId).get(idArr[i]));
					mfnOrder.setShowBillNo(mfnOrder.getGroupBillNo());
					mfnOrderList.add(mfnOrder);
					if(idArr[i].endsWith("amazon")||idArr[i].endsWith("ebay")){
						amazonAndEbayOrder.add(mfnOrder);
					}
				}

				File file = SendEmailByOrderMonitor.genPackagePdf(mfnOrderList,packageNo,country);
				if("de".equals(country)||"com".equals(country)||"jp".equals(country)||country.startsWith("com")||country.startsWith("de")||country.startsWith("jp")){
					if (file != null) {
						final List<MfnOrder> mfnOrderList2=amazonAndEbayOrder;
						final Integer packageId=tempId;
						
						if(mfnOrderList2.size()>0){
							new Thread(){
								public void run() {
									boolean returnFlag=updateShippedByOrders(mfnOrderList2);
									mfnOrderService.updateAmazonAndEbayStatu(returnFlag, packageId);
								}
					       }.start();
						}
						return packageNo;
					} else {
						return "0";
					}
				}else{
					if (file != null) {
						return packageNo;
					}else{
						return "0";
					}
				}
			}else{
				return "2";
			}
			
		}else{
			return "0";
		}
	}
	
	
	public void updateDpdTracking(Map<String,String> trackMap){
		if(trackMap!=null&&trackMap.size()>0){

			Map<String,List<MfnOrder>> orderMap=mfnOrderService.updateTrackNumber("DPD",trackMap,"de");
			final List<MfnOrder> allList=Lists.newArrayList();
			final List<MfnOrder> amazonOrder=orderMap.get("0");
			List<MfnOrder> amazonOrder1=orderMap.get("1");
			final List<MfnOrder> amazonOrder3=orderMap.get("3");
			if(amazonOrder!=null&&amazonOrder.size()>0){
				allList.addAll(amazonOrder);
				String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/mfnTrackNumber/";
				String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
				final File dir = new File(ctxPath+dateStr);
				dir.mkdirs();
				
				Map<String,List<MfnOrder>> tempMap=Maps.newHashMap();
				for (MfnOrder mfnOrder : amazonOrder) {
					List<MfnOrder> temp=tempMap.get(mfnOrder.getAccountName());
					if(temp==null){
						temp=Lists.newArrayList();
						tempMap.put(mfnOrder.getAccountName(),temp);
					}
				    temp.add(mfnOrder);
				}
				for (final Map.Entry<String,List<MfnOrder>> accountEntry: tempMap.entrySet()) {
					final String account=accountEntry.getKey();
					final String jsonStr =toJson(accountEntry.getValue());
					new Thread(){
						public void run(){   
							AmazonAccountConfig config=amazonAccountConfigService.getByName(account);
							submitTrackCode(jsonStr,config); 
						}
					}.start();
				}
				
			}
			if(amazonOrder3!=null&&amazonOrder3.size()>0){
				String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/mfnTrackNumber/";
				String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
				final File dir = new File(ctxPath+dateStr);
				dir.mkdirs();
				new Thread(){
					public void run(){   
						updateTrackNumberInEbay(amazonOrder3,"de") ;
					}
				}.start();
			}
			if(amazonOrder1!=null&&amazonOrder1.size()>0){
				allList.addAll(amazonOrder1);
			}
			try{
				List<MfnOrder> amazonOrder2=orderMap.get("2");
				if(amazonOrder2!=null&&amazonOrder2.size()>0){
					amazonUnlineOrderService.updateTrackNumber(amazonOrder2);
				}
			}catch(Exception e){
				LOGGER.info("error",e);
		    }
		}
	}
	
	
	@RequestMapping(value = "genDpdTrackingNumber")
	@ResponseBody
	public String genDpdTrackingNumber(MfnOrder mfnOrder,String type,String senderType){
		MfnAddress addr = mfnOrder.getShippingAddress();
		Dpd dpd= new Dpd();
		if(StringUtils.isNotBlank(type)){
			dpd.setProduct(type);
		}
		dpd.setReceiverName(addr.getName());
		dpd.setReceiverState(addr.getCountryCode()==null?"":addr.getCountryCode());
		dpd.setReceiverCountry(addr.getCountryCode()==null?"":addr.getCountryCode());
		if("1".equals(senderType)){
			dpd.setSenderName("F&amp;M Technology GmbH");
			dpd.setSenderAddress1("Brünner Str. 10");
			dpd.setSenderCity("Leipzig");
			dpd.setSenderZip("04209");
			dpd.setSenderCountry("DE");
		}
		String street = "";
		/*if(StringUtils.isNotBlank(addr.getStreet())&&addr.getStreet().contains("<br/>")){
			
		}else{*/
			dpd.setReceiverCity(addr.getCityName()==null?"":addr.getCityName());
			dpd.setReceiverZip(addr.getPostalCode()==null?"":addr.getPostalCode());
			if(StringUtils.isNotBlank(addr.getStreet())){
				street=addr.getStreet();
			}
			if(StringUtils.isBlank(street)&&StringUtils.isNotBlank(addr.getStreet1())){
				street=addr.getStreet1();
			}
			if(StringUtils.isBlank(street)&&StringUtils.isNotBlank(addr.getStreet2())){
				street=addr.getStreet2();
			}
			dpd.setReceiverAddress1(street);
		/*}*/
		
	
		String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir()+"dpdTracking";
		final File baseDir = new File(baseDirStr);
		if (!baseDir.isDirectory())
			baseDir.mkdirs();
	
		Map<String,String> trackMap=Maps.newHashMap();
	
			 JSONObject object = DpdService.storeOrders(dpd);
			 if (StringUtils.isNotEmpty(object.getString("error"))) {
				return mfnOrder.getOrderId()+" Error:"+object.getString("error");
			 }else{
				 String trackingNumber = object.getString("parcelLabelNumber");
				 String parcellabelsPDF = object.getString("parcellabelsPDF");
				 
				 String image=Global.getCkBaseDir()+"dpdTracking/"+mfnOrder.getGroupBillNo()+"-"+trackingNumber+".pdf";
				 mfnOrderService.updateTrackingInfo("DPD",trackingNumber,image,"",null,mfnOrder.getId());
				 File imageFile = new File(baseDir, mfnOrder.getGroupBillNo()+"-"+trackingNumber+".pdf");
	    		 try {
					FileUtils.writeByteArrayToFile(imageFile, Encodes.getUnBASE64Byte(parcellabelsPDF.getBytes()));
				 } catch (IOException e) {
					e.printStackTrace();
				 }
	    		 trackMap.put(mfnOrder.getGroupBillNo(), trackingNumber);
			 }
	
		     updateDpdTracking(trackMap);
		     return "0";
	}
	
	
	
	@RequestMapping(value = "genDpdTracking")
	@ResponseBody
	public String genDpdTracking(String pkId,String type,String senderType){  
		    MfnPackage mfnPackage=mfnPackageService.get(Integer.parseInt(pkId));
		    List<MfnOrder> mfnList = mfnPackage.getOrders();
		    final Map<String,Dpd> dpdMap=Maps.newLinkedHashMap();
			final List<Dpd> dpdList=Lists.newArrayList();
			final Map<String,MfnOrder> dpdMnfOrderMap = Maps.newHashMap();
			List<MfnOrder> mfnOrderList=new ArrayList<MfnOrder>();
			for (MfnOrder mfnOrder:mfnList) {
				mfnOrder.setBillNo(mfnOrder.getBillNo());
				mfnOrder.setShowBillNo(mfnOrder.getGroupBillNo());
				mfnOrderList.add(mfnOrder);
				
				MfnAddress addr = mfnOrder.getShippingAddress();
				Dpd dpd= new Dpd();
				if(StringUtils.isNotBlank(type)){
					dpd.setProduct(type);
				}
				if("1".equals(senderType)){
					dpd.setSenderName("F&amp;M Technology GmbH");
					dpd.setSenderAddress1("Brünner Str. 10");
					dpd.setSenderCity("Leipzig");
					dpd.setSenderZip("04209");
					dpd.setSenderCountry("DE");
				}
				dpd.setReceiverName(addr.getName());
				dpd.setReceiverState(addr.getCountryCode()==null?"":addr.getCountryCode());
				dpd.setReceiverCountry(addr.getCountryCode()==null?"":addr.getCountryCode());
				String street = "";
				
					dpd.setReceiverCity(addr.getCityName()==null?"":addr.getCityName());
					dpd.setReceiverZip(addr.getPostalCode()==null?"":addr.getPostalCode());
					if(StringUtils.isNotBlank(addr.getStreet())){
						street=addr.getStreet();
					}
					if(StringUtils.isBlank(street)&&StringUtils.isNotBlank(addr.getStreet1())){
						street=addr.getStreet1();
					}
					if(StringUtils.isBlank(street)&&StringUtils.isNotBlank(addr.getStreet2())){
						street=addr.getStreet2();
					}
					dpd.setReceiverAddress1(street);
				
				dpdMap.put(mfnOrder.getId(), dpd);
				dpdMnfOrderMap.put(mfnOrder.getId(), mfnOrder);
				dpdList.add(dpd);
			}

			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir()+"dpdTracking";
			final File baseDir = new File(baseDirStr);
			if (!baseDir.isDirectory())
				baseDir.mkdirs();
			final Integer packageId=mfnPackage.getId();
			final String packageNumber = mfnPackage.getPackageNo();
			new Thread(){
				public void run() {
					Map<String,String> trackMap=Maps.newHashMap();
					JSONObject object = DpdService.storeOrders(dpdList);
					if (StringUtils.isNotEmpty(object.getString("error"))) {
						 String remark = object.getString("error");
						 mfnOrderService.updatePackageRemark(packageId,remark);
					}else{
						 String trackingNumber = object.getString("parcelLabelNumber");
						 String parcellabelsPDF = object.getString("parcellabelsPDF");
						 File imageFile = new File(baseDir,packageNumber+"-"+packageId+".pdf");
						 try {
								FileUtils.writeByteArrayToFile(imageFile, Encodes.getUnBASE64Byte(parcellabelsPDF.getBytes()));
						 } catch (IOException e) {
								e.printStackTrace();
						 }
						 String[] arr = trackingNumber.split(",");
						 int i=0;
						 for (Map.Entry<String,Dpd> entry: dpdMap.entrySet()) {
							 String id = entry.getKey();
							 MfnOrder tempOrder = dpdMnfOrderMap.get(id);
							 mfnOrderService.updateTrackingInfo("DPD",arr[i],"","",null,tempOrder.getId());
				    		 trackMap.put(tempOrder.getShowBillNo(), arr[i]);
				    		 i++;
						 }
					}
					updateDpdTracking(trackMap);
				}
	       }.start();
	       
		 return "0";
	}		    

 
	
	@RequestMapping(value = "dpdPrintPdf")
	@ResponseBody
	public String dpdPrintPdf(String printIds,String country,String type,String senderType) {
		if(StringUtils.isNotBlank(printIds)){
			String[] idArr=printIds.split(",");
			String  flag=mfnOrderService.getIsExistPackage(idArr);
			if("1".equals(flag)){
				Map<String,MfnOrder> idMap=mfnOrderService.findAllOrder(idArr);
				if(country.startsWith("de")){
					 Map<String,Integer> printQtyMap=mfnOrderService.findPrintQuantity("de");
					 Map<String,Integer> stockMap=psiInventoryService.findQuantityByWarehouseId(19);
					 if(printQtyMap!=null&&printQtyMap.size()>0){
						 for (Map.Entry<String,Integer> entry: printQtyMap.entrySet()) {
							  String name=entry.getKey();
							  Integer qty=entry.getValue();
							  if(stockMap.get(name)==null){
								  stockMap.put(name,0);
							  }else{
								  stockMap.put(name, stockMap.get(name)-qty);
							  }
						 }
					 }
					 Map<String,Integer> curMap=Maps.newHashMap();
					 for (Map.Entry<String,MfnOrder> orderMap:idMap.entrySet()) {
						 MfnOrder order=orderMap.getValue();
						 for (MfnOrderItem item: order.getItems()) {
							  if(!"Inateck Old".equals(item.getTitle())&&!"Inateck other".equals(item.getTitle())&&StringUtils.isNotBlank(item.getTitle())){
								  curMap.put(item.getTitle(),item.getQuantityPurchased()+(curMap.get(item.getTitle())==null?0:curMap.get(item.getTitle()) ));
							  }
						 }
					 }
					 
					 String info="";
					 for(Map.Entry<String,Integer> curEty: curMap.entrySet()) {
						  String name=curEty.getKey();
						  Integer qty=curEty.getValue();
						  if(stockMap.get(name)==null){
							  info+=name+" no stocks<br/>";
						  }else if(qty>stockMap.get(name)){
							  info+=name+" lower stocks,warehouse:"+stockMap.get(name)+",print quantity:"+qty+"<br/>";
						  }
					 }
					 if(StringUtils.isNotBlank(info)){
						 return "Operation has been canceled！！！<br/>"+info;
					 }
				}
				 
				List<MfnOrder> mfnOrderList=new ArrayList<MfnOrder>();
				String packageNo="PK_"+new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
				Map<Integer,Map<String,Integer>> map=mfnOrderService.savePackage(idArr,packageNo,country);
				Integer tempId=(Integer)Lists.newArrayList(map.keySet()).get(0);
				List<MfnOrder> amazonAndEbayOrder=new ArrayList<MfnOrder>();
				
				final Map<String,Dpd> dpdMap=Maps.newLinkedHashMap();
				final List<Dpd> dpdList=Lists.newArrayList();
				final Map<String,MfnOrder> dpdMnfOrderMap = Maps.newHashMap();
				for (int i = 0; i < idArr.length; i++) {
					MfnOrder mfnOrder=idMap.get(idArr[i]);
					mfnOrder.setBillNo(map.get(tempId).get(idArr[i]));
					mfnOrder.setShowBillNo(mfnOrder.getGroupBillNo());
					mfnOrderList.add(mfnOrder);
					if(idArr[i].endsWith("amazon")||idArr[i].endsWith("ebay")){
						amazonAndEbayOrder.add(mfnOrder);
					}
					
					MfnAddress addr = mfnOrder.getShippingAddress();
					Dpd dpd= new Dpd();
					if(StringUtils.isNotBlank(type)){
						dpd.setProduct(type);
					}
					if("1".equals(senderType)){
						dpd.setSenderName("F&amp;M Technology GmbH");
						dpd.setSenderAddress1("Brünner Str. 10");
						dpd.setSenderCity("Leipzig");
						dpd.setSenderZip("04209");
						dpd.setSenderCountry("DE");
					}
					dpd.setReceiverName(addr.getName());
					dpd.setReceiverState(addr.getCountryCode()==null?"":addr.getCountryCode());
					dpd.setReceiverCountry(addr.getCountryCode()==null?"":addr.getCountryCode());
					String street = "";
					
						dpd.setReceiverCity(addr.getCityName()==null?"":addr.getCityName());
						dpd.setReceiverZip(addr.getPostalCode()==null?"":addr.getPostalCode());
						if(StringUtils.isNotBlank(addr.getStreet())){
							street=addr.getStreet();
						}
						if(StringUtils.isBlank(street)&&StringUtils.isNotBlank(addr.getStreet1())){
							street=addr.getStreet1();
						}
						if(StringUtils.isBlank(street)&&StringUtils.isNotBlank(addr.getStreet2())){
							street=addr.getStreet2();
						}
						dpd.setReceiverAddress1(street);
					
					dpdMap.put(mfnOrder.getId(), dpd);
					dpdMnfOrderMap.put(mfnOrder.getId(), mfnOrder);
					dpdList.add(dpd);
				}

				String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir()+"dpdTracking";
				final File baseDir = new File(baseDirStr);
				if (!baseDir.isDirectory())
					baseDir.mkdirs();
				
				File file = SendEmailByOrderMonitor.genPackagePdf(mfnOrderList,packageNo,country);
				final Integer packageId=tempId;
				final String packageNumber = packageNo;
				if("de".equals(country)||"com".equals(country)||"jp".equals(country)||country.startsWith("com")||country.startsWith("de")||country.startsWith("jp")){
					if (file != null) {
						final List<MfnOrder> mfnOrderList2=amazonAndEbayOrder;
						
						if(mfnOrderList2.size()>0){
							new Thread(){
								public void run() {
									boolean returnFlag=updateShippedByOrders(mfnOrderList2);
									mfnOrderService.updateAmazonAndEbayStatu(returnFlag, packageId);
								}
					       }.start();
						}
						
						new Thread(){
							public void run() {
								Map<String,String> trackMap=Maps.newHashMap();
								JSONObject object = DpdService.storeOrders(dpdList);
								if (StringUtils.isNotEmpty(object.getString("error"))) {
									 String remark = object.getString("error");
									 mfnOrderService.updatePackageRemark(packageId,remark);
								}else{
									 String trackingNumber = object.getString("parcelLabelNumber");
									 String parcellabelsPDF = object.getString("parcellabelsPDF");
									 File imageFile = new File(baseDir,packageNumber+"-"+packageId+".pdf");
									 try {
											FileUtils.writeByteArrayToFile(imageFile, Encodes.getUnBASE64Byte(parcellabelsPDF.getBytes()));
									 } catch (IOException e) {
											e.printStackTrace();
									 }
									 String[] arr = trackingNumber.split(",");
									 int i=0;
									 for (Map.Entry<String,Dpd> entry: dpdMap.entrySet()) {
										 String id = entry.getKey();
										 MfnOrder tempOrder = dpdMnfOrderMap.get(id);
										 mfnOrderService.updateTrackingInfo("DPD",arr[i],"","",null,tempOrder.getId());
							    		 trackMap.put(tempOrder.getShowBillNo(), arr[i]);
							    		 i++;
									 }
								}
								updateDpdTracking(trackMap);
							}
				       }.start();
				       
						return packageNo;
					} else {
						return "0";
					}
				}else{
					if (file != null) {
						new Thread(){
							public void run() {
								Map<String,String> trackMap=Maps.newHashMap();
								JSONObject object = DpdService.storeOrders(dpdList);
								if (StringUtils.isNotEmpty(object.getString("error"))) {
									 String remark = object.getString("error");
									 mfnOrderService.updatePackageRemark(packageId,remark);
								}else{
									 String trackingNumber = object.getString("parcelLabelNumber");
									 String parcellabelsPDF = object.getString("parcellabelsPDF");
									 File imageFile = new File(baseDir,packageNumber+"-"+packageId+".pdf");
									 try {
											FileUtils.writeByteArrayToFile(imageFile, Encodes.getUnBASE64Byte(parcellabelsPDF.getBytes()));
									 } catch (IOException e) {
											e.printStackTrace();
									 }
									 String[] arr = trackingNumber.split(",");
									 int i=0;
									 for (Map.Entry<String,Dpd> entry: dpdMap.entrySet()) {
										 String id = entry.getKey();
										 MfnOrder tempOrder = dpdMnfOrderMap.get(id);
										 mfnOrderService.updateTrackingInfo("DPD",arr[i],"","",null,tempOrder.getId());
							    		 trackMap.put(tempOrder.getShowBillNo(), arr[i]);
							    		 i++;
									 }
								}
								updateDpdTracking(trackMap);
							}
				       }.start();
						return packageNo;
					}else{
						return "0";
					}
				}
			}else{
				return "2";
			}
			
		}else{
			return "0";
		}
	}
	
	
	
	@RequestMapping(value = "dpdPrintPdf2")
	@ResponseBody
	public String dpdPrintPdf2(String printIds,String country,String type,String senderType) {
		if(StringUtils.isNotBlank(printIds)){
			String[] idArr=printIds.split(",");
			String  flag=mfnOrderService.getIsExistPackage(idArr);
			if("1".equals(flag)){
				Map<String,MfnOrder> idMap=mfnOrderService.findAllOrder(idArr);
				if(country.startsWith("de")){
					 Map<String,Integer> printQtyMap=mfnOrderService.findPrintQuantity("de");
					 Map<String,Integer> stockMap=psiInventoryService.findQuantityByWarehouseId(19);
					 if(printQtyMap!=null&&printQtyMap.size()>0){
						 for (Map.Entry<String,Integer> entry: printQtyMap.entrySet()) {
							  String name=entry.getKey();
							  Integer qty=entry.getValue();
							  if(stockMap.get(name)==null){
								  stockMap.put(name,0);
							  }else{
								  stockMap.put(name, stockMap.get(name)-qty);
							  }
						 }
					 }
					 Map<String,Integer> curMap=Maps.newHashMap();
					 for (Map.Entry<String,MfnOrder> orderMap:idMap.entrySet()) {
						 MfnOrder order=orderMap.getValue();
						 for (MfnOrderItem item: order.getItems()) {
							  if(!"Inateck Old".equals(item.getTitle())&&!"Inateck other".equals(item.getTitle())&&StringUtils.isNotBlank(item.getTitle())){
								  curMap.put(item.getTitle(),item.getQuantityPurchased()+(curMap.get(item.getTitle())==null?0:curMap.get(item.getTitle()) ));
							  }
						 }
					 }
					 
					 String info="";
					 for(Map.Entry<String,Integer> curEty: curMap.entrySet()) {
						  String name=curEty.getKey();
						  Integer qty=curEty.getValue();
						  if(stockMap.get(name)==null){
							  info+=name+" no stocks<br/>";
						  }else if(qty>stockMap.get(name)){
							  info+=name+" lower stocks,warehouse:"+stockMap.get(name)+",print quantity:"+qty+"<br/>";
						  }
					 }
					 if(StringUtils.isNotBlank(info)){
						 return "Operation has been canceled！！！<br/>"+info;
					 }
				}
				 
				Map<String,MfnOrder> dpdOrderMap = Maps.newHashMap();
				Map<String,Dpd> dpdMap=Maps.newHashMap();
				for (int i = 0; i < idArr.length; i++) {
					MfnOrder mfnOrder=idMap.get(idArr[i]);
					dpdOrderMap.put(mfnOrder.getId(), mfnOrder);
					MfnAddress addr = mfnOrder.getShippingAddress();
					Dpd dpd= new Dpd();
					if(StringUtils.isNotBlank(type)){
						dpd.setProduct(type);
					}
					if("1".equals(senderType)){
						dpd.setSenderName("F&amp;M Technology GmbH");
						dpd.setSenderAddress1("Brünner Str. 10");
						dpd.setSenderCity("Leipzig");
						dpd.setSenderZip("04209");
						dpd.setSenderCountry("DE");
					}
					dpd.setReceiverName(addr.getName());
					dpd.setReceiverState(addr.getCountryCode()==null?"":addr.getCountryCode());
					dpd.setReceiverCountry(addr.getCountryCode()==null?"":addr.getCountryCode());
					String street = "";
					if(StringUtils.isNotBlank(addr.getStreet())&&addr.getStreet().contains("<br/>")){
						/*<br/>
						   Alexander Baron von der Pahlen
						  <br/>
						  Drosselweg 5 /1
						  <br/>
						  71065 Sindelfingen
						  <br/>
						  Deutschland
						  <br/>
						  07031 / 9892723*/
					}else{
						dpd.setReceiverCity(addr.getCityName()==null?"":addr.getCityName());
						dpd.setReceiverZip(addr.getPostalCode()==null?"":addr.getPostalCode());
						if(StringUtils.isNotBlank(addr.getStreet())){
							street=addr.getStreet();
						}
						if(StringUtils.isBlank(street)&&StringUtils.isNotBlank(addr.getStreet1())){
							street=addr.getStreet1();
						}
						if(StringUtils.isBlank(street)&&StringUtils.isNotBlank(addr.getStreet2())){
							street=addr.getStreet2();
						}
						dpd.setReceiverAddress1(street);
					}
					dpdMap.put(mfnOrder.getId(), dpd);
				}
				
				String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir()+"dpdTracking";
				File baseDir = new File(baseDirStr);
				if (!baseDir.isDirectory())
					baseDir.mkdirs();
				
                 if(country.startsWith("de")){
					
					for (Map.Entry<String,Dpd> entry: dpdMap.entrySet()) {
						 String id = entry.getKey();
						 Dpd dpd = entry.getValue();
						 MfnOrder tempOrder = dpdOrderMap.get(id);
						 JSONObject object = DpdService.storeOrders(dpd);
						 if (StringUtils.isNotEmpty(object.getString("error"))) {
							  return tempOrder.getOrderId()+" Error:"+object.getString("error");
						 }
						 String trackingNumber = object.getString("parcelLabelNumber");
						 tempOrder.setTrackNumber(trackingNumber);
						 tempOrder.setSupplier("DPD");
						 String parcellabelsPDF = object.getString("parcellabelsPDF");
						 tempOrder.setPdfImage(parcellabelsPDF);
					}
				}
				
				List<MfnOrder> mfnOrderList=new ArrayList<MfnOrder>();
				
				String packageNo="PK_"+new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
				Map<Integer,Map<String,Integer>> map=mfnOrderService.savePackage(idArr,packageNo,country);
				Integer tempId=(Integer)Lists.newArrayList(map.keySet()).get(0);
				List<MfnOrder> amazonAndEbayOrder=new ArrayList<MfnOrder>();
				MfnPackage mfnPackage = new MfnPackage();
				mfnPackage.setId(tempId);
				
				for (int i = 0; i < idArr.length; i++) {
					 MfnOrder mfnOrder=idMap.get(idArr[i]);
					 mfnOrder.setBillNo(map.get(tempId).get(idArr[i]));
					 mfnOrder.setShowBillNo(mfnOrder.getGroupBillNo());
					
					 mfnOrder.setMfnPackage(mfnPackage);
					 mfnOrderList.add(mfnOrder);
					 if(idArr[i].endsWith("amazon")||idArr[i].endsWith("ebay")){
						amazonAndEbayOrder.add(mfnOrder);
					 }else{
						mfnOrder.setStatus("1");
					 }
					 String image=Global.getCkBaseDir()+"dpdTracking/"+mfnOrder.getShowBillNo()+"-"+mfnOrder.getTrackNumber()+".pdf";
					 mfnOrder.setLabelImage(image);
					 mfnOrderService.updateTrackingInfo("DPD",mfnOrder.getTrackNumber(),image,"",null,mfnOrder.getId());
					 File imageFile = new File(baseDir, mfnOrder.getShowBillNo()+"-"+mfnOrder.getTrackNumber()+".pdf");
		    		 try {
						FileUtils.writeByteArrayToFile(imageFile, Encodes.getUnBASE64Byte(mfnOrder.getPdfImage().getBytes()));
					 } catch (IOException e) {
						e.printStackTrace();
					 }
		    		 
				}
				File file = SendEmailByOrderMonitor.genPackagePdf(mfnOrderList,packageNo,country);
				if("de".equals(country)||"com".equals(country)||"jp".equals(country)||country.startsWith("com")||country.startsWith("de")||country.startsWith("jp")){
					if (file != null) {
						final List<MfnOrder> mfnOrderList2=amazonAndEbayOrder;
						final Integer packageId=tempId;
						
						if(mfnOrderList2.size()>0){
							new Thread(){
								public void run() {
									boolean returnFlag=updateShippedByOrders(mfnOrderList2);
									mfnOrderService.updateAmazonAndEbayStatu(returnFlag, packageId);
								}
					       }.start();
						}
						return packageNo;
					} else {
						return "0";
					}
				}else{
					if (file != null) {
						return packageNo;
					}else{
						return "0";
					}
				}
			}else{
				return "2";
			}
			
		}else{
			return "0";
		}
	}
	
	
	@RequestMapping(value = "exportCsv")
	public void exportCsv(String packageId,HttpServletRequest request, HttpServletResponse response){
		try {
			MfnPackage mfnPackage=mfnPackageService.get(Integer.parseInt(packageId));
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/download;charset=utf-8");
			response.setHeader("Content-disposition", "attachment;filename="+(mfnPackage.getPackageNo()+".csv"));
			OutputStream o = response.getOutputStream();
			OutputStreamWriter os = new OutputStreamWriter(o, "utf-8");
			BufferedWriter br=new BufferedWriter(os);
			List<MfnOrder> mfnOrderList=mfnPackage.getOrders();
			List<MfnOrder> amazonAndEbayOrder=new ArrayList<MfnOrder>();
			for (int i = 0; i < mfnOrderList.size(); i++) {
				MfnOrder mfnOrder=mfnOrderList.get(i);
				if(mfnOrder.getId().endsWith("amazon")||mfnOrder.getId().endsWith("ebay")){
					if(mfnOrder.getBillNo().toString().length()<8){
						String num="";
						StringBuffer buf= new StringBuffer();
						for(int m=0;m<8-mfnOrder.getBillNo().toString().length();m++){
							buf.append("0");
						}
						num = buf.toString();
						mfnOrder.setShowBillNo(num+mfnOrder.getBillNo());
					}else{
						mfnOrder.setShowBillNo(mfnOrder.getBillNo()+"");
					}
					//amazonAndEbayOrder.add(mfnOrder);
				}else if("1".equals(mfnOrder.getOrderType())){//test
					mfnOrder.setShowBillNo("Test "+mfnOrder.getBillNo());
				}else if("2".equals(mfnOrder.getOrderType())||"5".equals(mfnOrder.getOrderType())){//support
					mfnOrder.setShowBillNo("Ersatz "+mfnOrder.getBillNo());
				}else if("3".equals(mfnOrder.getOrderType())){
					mfnOrder.setShowBillNo("Mfn "+mfnOrder.getBillNo());
				}
				amazonAndEbayOrder.add(mfnOrder);
			}
			
			Collections.sort(amazonAndEbayOrder);
			
			String txt = "Lname;ISOLLand;Lstrasse;Lplz;Lort;Gewicht;ReNr;Zinfo;LFirma;LBundesland;Kundennummer;HausNr\n";
			br.write(txt);
			//throw new RuntimeException("xxx");
			for (MfnOrder mfn : amazonAndEbayOrder) {
				MfnAddress add=mfn.getShippingAddress();
				boolean flag=true;
				if(StringUtils.isNotBlank(add.getStreet())){
					String lowAddr=StringUtils.lowerCase(add.getStreet());
					if(lowAddr.contains("packstation")||lowAddr.contains("paketshop")||lowAddr.contains("postfiliale")){
						flag=false;
					}
				}
				if(StringUtils.isNotBlank(add.getStreet1())){
					String lowAddr=StringUtils.lowerCase(add.getStreet1());
					if(lowAddr.contains("packstation")||lowAddr.contains("paketshop")||lowAddr.contains("postfiliale")){
						flag=false;
					}
				}
				if(StringUtils.isNotBlank(add.getStreet2())){
					String lowAddr=StringUtils.lowerCase(add.getStreet2());
					if(lowAddr.contains("packstation")||lowAddr.contains("paketshop")||lowAddr.contains("postfiliale")){
						flag=false;
					}
				}
				if(flag){
					Float totalQuantity=0f;
					for(MfnOrderItem mfnOrderItem:mfn.getItems()){
						Object[] obj=amazonProduct2Service.findProductPackAndTypeBySku(mfnOrderItem.getSku());
						if(obj==null){
							totalQuantity+=0;
						}else{
							totalQuantity+=MathUtils.roundUp(((BigDecimal)(obj[2]==null?new BigDecimal(0):obj[2])).doubleValue()/1000*mfnOrderItem.getQuantityShipped());
						}
					}
					//2 5 15 31.5
					if("de".equals(mfn.getCountry())){
						if(totalQuantity<2){
							totalQuantity=2f;
						}else if(totalQuantity>=2&&totalQuantity<5){
							totalQuantity=5f;
						}else if(totalQuantity>=5&&totalQuantity<15){
							totalQuantity=15f;
						}else{
							totalQuantity=31.5f;
						}
					}
					String zinfo=(StringUtils.isBlank(add.getStreet2())?"":add.getStreet2().replaceAll("<br/>","/"));
					String street="";
					StringBuilder buf=new StringBuilder();
					if(StringUtils.isNotBlank(add.getStreet())){
						if(add.getStreet().contains("<br/>")){
							String[] arr=add.getStreet().split("<br/>");
							for (String adr: arr) {
								buf.append(adr.trim()+"/");
							}
						}else{
							buf.append(add.getStreet());
						}
					}
					if(StringUtils.isNotBlank(buf.toString())){
						street=buf.toString();
					}
					txt = (add.getName()==null?"":add.getName().replaceAll("<br/>","").trim())+";"+add.getCountryCode()+";"+(StringUtils.isBlank(add.getStreet1())?"":add.getStreet1().replaceAll("<br/>","/"))+";"+add.getPostalCode()+";"+add.getCityName()+";"+totalQuantity+";"+mfn.getShowBillNo()+";"+zinfo+";"+(street)+";"+(StringUtils.isBlank(add.getStateOrProvince())?"":add.getStateOrProvince())+";"+((mfn.getBuyerUser()==null?"":mfn.getBuyerUser().replaceAll("<br/>","").trim()))+";;\n";
					br.write(txt);
				}
				
			}
			br.flush();
			br.close();
		} catch (Exception e1) {
			LOGGER.warn(e1.getMessage());
			e1.printStackTrace();
		}
		
	}
	
	@RequestMapping(value = "reUpdateStatus")
	public String reUpdateStatus(final String packageId) {
		MfnPackage mfnPackage=mfnPackageService.get(Integer.parseInt(packageId));
		mfnOrderService.updatePackageStatu(Integer.parseInt(packageId));
		List<MfnOrder> mfnOrderList=mfnPackage.getOrders();
		List<MfnOrder> mfnOrderList2=new ArrayList<MfnOrder>();
		for (MfnOrder mfnOrder : mfnOrderList) {
			if("0".equals(mfnOrder.getStatus())){
				if(mfnOrder.getId().endsWith("amazon")||mfnOrder.getId().endsWith("ebay")){
					mfnOrderList2.add(mfnOrder);
				}
			}
		}
		final List<MfnOrder> orderList=mfnOrderList2;
		if(orderList.size()>0){
			new Thread(){
				public void run() {
					//boolean returnFlag=mfnOrderService.getShippedByOrders(orderList);
					boolean returnFlag=updateShippedByOrders(orderList);
					mfnOrderService.updateAmazonAndEbayStatu(returnFlag,Integer.parseInt(packageId));
				}
	       }.start();
		}
		
		//mfnOrderService.reSavePackage(orderList,Integer.parseInt(packageId));
		return "redirect:"+Global.getAdminPath()+"/amazonAndEbay/mfnOrder/package/packageListDown/";
	}
	
	@RequestMapping(value = "form")
	public String form(MfnOrder mfnOrder, Model model) {
		model.addAttribute("mfnOrder", mfnOrder);
		return "modules/ebay/order/mfnOrderForm";
	}
	
	@RequestMapping(value = "form2")
	public String form2(MfnOrder mfnOrder, Model model) {
		model.addAttribute("mfnOrder", mfnOrderService.getByOrderId(mfnOrder.getOrderId()));
		return "modules/ebay/order/mfnOrderForm";
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateQuantity"})
	public String updateQuantity(String id,Integer quantity) {
		return this.mfnOrderService.updateQuantity(id,quantity);
	}
	
	@RequestMapping(value = "showCurrentOrder")
	public String showCurrentOrder(MfnOrder mfnOrder,Model model){
		if (mfnOrder.getBuyTime()== null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			mfnOrder.setBuyTime(today);
			mfnOrder.setLastModifiedTime(today);
		}
		if(StringUtils.isBlank(mfnOrder.getCountry())){
			mfnOrder.setCountry("de");
		}
		Map<String,Integer> totalMap=mfnOrderService.getCurrentDateTotal(mfnOrder);
		Map<String,List<MfnOrderItem>> itemMap=mfnOrderService.getCurrentDateList(mfnOrder);
		model.addAttribute("totalMap", totalMap);
		model.addAttribute("itemMap", itemMap);
		return "modules/ebay/order/createCurrentOrder";
	}
	


	@RequestMapping(value = "save")
	@ResponseBody
	public String save(MfnOrder mfnOrder) {
		if(mfnOrder.getInvoiceAddress()!=null&&StringUtils.isBlank(mfnOrder.getInvoiceAddress().getId())){
			mfnOrder.getInvoiceAddress().setId(IdGen.uuid());
		}
		mfnOrderService.save(mfnOrder);
		return mfnOrder.getInvoiceAddress().getId() + "";
	}
	
	@RequestMapping(value = "add")
	public String add(MfnOrder mfnOrder,Model model){
		List<PsiInventory> inventorys=new ArrayList<PsiInventory>();
		Map<String,String> eventMap=new HashMap<String,String>();
		if(StringUtils.isBlank(mfnOrder.getCountry())){
			inventorys=psiInventoryService.findByStock(19);
		}else if("de".equals(mfnOrder.getCountry())){
			inventorys=psiInventoryService.findByStock(19);
		}else{
			inventorys=psiInventoryService.findByStock(120);
		}
		if("1".equals(mfnOrder.getOrderType())){ //'5'Support 8:Review Order
			eventMap=eventService.getEventMap("8",StringUtils.isBlank(mfnOrder.getCountry())?"de":mfnOrder.getCountry());
		}else if("5".equals(mfnOrder.getOrderType())){
			eventMap=eventService.getEventMap("7",StringUtils.isBlank(mfnOrder.getCountry())?"de":mfnOrder.getCountry());
		}else if(StringUtils.isBlank(mfnOrder.getOrderType())||"2".equals(mfnOrder.getOrderType())){
			eventMap=eventService.getEventMap("5",StringUtils.isBlank(mfnOrder.getCountry())?"de":mfnOrder.getCountry());
		}
		Map<String,String>  productNameSkuMap = Maps.newHashMap();
		for(PsiInventory inventory:inventorys){
			productNameSkuMap.put(inventory.getSku(), inventory.getProductName()+(StringUtils.isBlank(inventory.getColorCode())?"":("_"+inventory.getColorCode()))+"["+inventory.getSku()+"]");
		}
	    
		model.addAttribute("eventMap", eventMap);
		model.addAttribute("sku", JSON.toJSON(productNameSkuMap));
		model.addAttribute("mfnOrder", mfnOrder);
		return "modules/ebay/order/ebayOrderAdd";
	}
	
	@RequestMapping(value = "getEventType")
	@ResponseBody
	public List<Event> getEventType(String type,String country){
		List<Event> eventMap=new ArrayList<Event>();
		if("1".equals(type)){//Review Order
			eventMap=eventService.getEventList("8",country);
		}else if("5".equals(type)){
			eventMap=eventService.getEventList("7",country);
		}else{
			eventMap=eventService.getEventList("5",country);
		}
		return eventMap;
	}
	
	@RequestMapping(value = "getOrderInfo")
	@ResponseBody
	public Event getOrderInfo(String eventId,String type){
		if(StringUtils.isNotBlank(eventId)){
			return eventService.getById(Integer.parseInt(eventId),type);
		}
		return null;
	}
	
	@RequestMapping(value = "download")
	public String download(MfnOrder mfnOrder,Model model){
		if (mfnOrder.getBuyTime()== null) {
			Date today = new Date();
			today.setHours(0);
			today.setSeconds(0);
			today.setMinutes(0);
			mfnOrder.setBuyTime(DateUtils.addDays(today, -1));
			mfnOrder.setLastModifiedTime(today);
		}
		Map<String,List<String>> fileList= mfnOrderService.getDownloadFileList(mfnOrder);
		model.addAttribute("mfnOrder", mfnOrder);
		model.addAttribute("fileList", fileList);
		return "modules/ebay/order/packageListDownload";
	}
	
	@RequestMapping(value = "editAmazonOrEbay")
	public String editAmazonOrEbay(MfnOrder mfnOrder,Model model){
		BigDecimal length=mfnOrder.getLength();
		BigDecimal width=mfnOrder.getWidth();
		BigDecimal height=mfnOrder.getHeight();
		Integer weight=mfnOrder.getWeight();
		if(length==null||width==null||height==null||weight==null){
			for (MfnOrderItem item : mfnOrder.getItems()) {
				String name=item.getTitle();
				if(StringUtils.isNotBlank(name)&&name.contains(" ")){
					name=name.substring(name.indexOf(" ")+1);
					if(name.contains("_")){
						name=name.substring(0,name.lastIndexOf("_"));
					}
					PsiProduct product = productService.findSize(name);
					if(product!=null){
						  if(product.getProductPackLength()!=null&&(length==null||length.floatValue()<product.getProductPackLength().floatValue())){
							  length=product.getProductPackLength();
						  }
						  if(product.getProductPackWidth()!=null&&(width==null||width.floatValue()<product.getProductPackWidth().floatValue())){
							  width=product.getProductPackWidth();
						  }
						  if(product.getProductPackHeight()!=null&&(height==null||height.floatValue()<product.getProductPackHeight().floatValue())){
							  height=product.getProductPackHeight();
						  }
						  if(product.getProductPackWeight()!=null&&(weight==null||weight<product.getProductPackWeight().doubleValue())){
							  weight=MathUtils.roundDown(product.getProductPackWeight().doubleValue());
						  }
					}
				}
			}
		}
		mfnOrder.setLength(length);
		mfnOrder.setWidth(width);
		mfnOrder.setHeight(height);
		mfnOrder.setWeight(weight);
		model.addAttribute("mfnOrder", mfnOrder);
		return "modules/ebay/order/amazonOrEbayEdit";
	}
	
	@RequestMapping(value = "saveAddress")
	public String saveAddress(MfnOrder mfnOrder,Model model){
		mfnOrderService.save(mfnOrder);
		return "redirect:"+Global.getAdminPath()+"/amazonAndEbay/mfnOrder/?repage";
	}
	
	@RequestMapping(value = "cancelOrder")
	public String cancelOrder(MfnOrder mfnOrder,Model model){
		mfnOrderService.cancelOrder(mfnOrder.getOrderId());
		return "redirect:"+Global.getAdminPath()+"/amazonAndEbay/mfnOrder/?repage";
	}
	
	@RequestMapping(value = "edit")
	public String edit(MfnOrder mfnOrder,Model model) {
		model.addAttribute("mfnOrder", mfnOrder);
		List<PsiInventory> inventorys=new ArrayList<PsiInventory>();
		Map<String,String> eventMap=new HashMap<String,String>();
		if(StringUtils.isBlank(mfnOrder.getCountry())){
			inventorys=psiInventoryService.findByStock(19);
		}else if("de".equals(mfnOrder.getCountry())){
			inventorys=psiInventoryService.findByStock(19);
		}else{
			inventorys=psiInventoryService.findByStock(120);
		}
		if("1".equals(mfnOrder.getOrderType())){ //'5'Support 8:Review Order
			eventMap=eventService.getEventMap("8",StringUtils.isBlank(mfnOrder.getCountry())?"de":mfnOrder.getCountry());
		}else if("5".equals(mfnOrder.getOrderType())){
			eventMap=eventService.getEventMap("7",StringUtils.isBlank(mfnOrder.getCountry())?"de":mfnOrder.getCountry());
		}else{
			eventMap=eventService.getEventMap("5",StringUtils.isBlank(mfnOrder.getCountry())?"de":mfnOrder.getCountry());
		}
		Map<String,String>  productNameSkuMap = Maps.newHashMap();
		for(PsiInventory inventory:inventorys){
			productNameSkuMap.put(inventory.getSku(), inventory.getProductName()+(StringUtils.isBlank(inventory.getColorCode())?"":("_"+inventory.getColorCode()))+"["+inventory.getSku()+"]");
		}
		model.addAttribute("eventMap", eventMap);
		model.addAttribute("sku", JSON.toJSON(productNameSkuMap));
		return "modules/ebay/order/ebayOrderEdit";
	}
	
	
	@RequestMapping(value = "saveAdd")
	public String saveAdd(MfnOrder mfnOrder,RedirectAttributes redirectAttributes) {
		Float subTotal = 0f;
		for(MfnOrderItem item:mfnOrder.getItems()){
			if("unknown".equals(item.getSku())){
				item.setTitle("unknown");
			}else{
				PsiInventory psiInventory=this.psiInventoryService.findBySku(item.getSku(),"de".equals(mfnOrder.getCountry())?19:120);
				PsiSku product=productService.getProductBySku(item.getSku(),psiInventory.getCountryCode());
				if(product!=null){
					if(StringUtils.isNotBlank(product.getColor())){
						item.setTitle(product.getProductName()+"_"+product.getColor());
					}else{
						item.setTitle(product.getProductName());
					}
				}
			}
			
			if(item.getItemPrice()==null){
				item.setItemPrice(0f);
			}
			if(item.getItemTax()==null){
				item.setItemTax(0f);
			}
			if(item.getQuantityPurchased()==null){
				item.setQuantityPurchased(0);
			}
			
			if(item.getOrder()==null){
				item.setOrder(mfnOrder);
			}
            item.setQuantityShipped(item.getQuantityPurchased());
			subTotal+=item.getItemPrice()+item.getItemPrice()*item.getItemTax()/100-(item.getCodFee()==null?0:item.getCodFee());
			if(StringUtils.isBlank(item.getId())){
				item.setId(IdGen.uuid());
			}
		}
		if(StringUtils.isEmpty(mfnOrder.getOrderId())){
			String orderId=Math.round(Math.random()*9000+1000)+""+ new Date().getTime();
			String newOrderId="11-"+orderId;
			mfnOrder.setOrderId(newOrderId);
		}
		if(mfnOrder.getBuyTime()==null){
			mfnOrder.setBuyTime(new Date());
		}
		mfnOrder.setOrderTotal(subTotal+(mfnOrder.getShippingServiceCost()==null?0:mfnOrder.getShippingServiceCost()));
		mfnOrder.setLastModifiedTime(new Date());
		if(mfnOrder.getPaidTime()==null){
			mfnOrder.setPaidTime(new Date());
		}
		//mfnOrder.setCountry("Germany");
		if("2".equals(mfnOrder.getStatus())){
			mfnOrder.setShippedTime(new Date());
		}
		if(mfnOrder.getShippingAddress()!=null&&StringUtils.isBlank(mfnOrder.getShippingAddress().getId())){
			mfnOrder.getShippingAddress().setId(IdGen.uuid());
		}
		mfnOrderService.save(mfnOrder);
		addMessage(redirectAttributes,MessageUtils.format("amazon_order_tips18",new Object[]{mfnOrder.getOrderId()}));
		return "redirect:"+Global.getAdminPath()+"/amazonAndEbay/mfnOrder/";
	}
	
	
	
	@RequestMapping(value = "exportBillNoFile")
	public String exportBillNoFile(String packageId,HttpServletRequest request, HttpServletResponse response){
		MfnPackage mfnPackage=mfnPackageService.get(Integer.parseInt(packageId));
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		List<String> title = Lists.newArrayList("bill_no","track_number");
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		 HSSFCell cell = null;		
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int index=1;
		for (MfnOrder order: mfnPackage.getOrders()) { 
			int j=0;
			row=sheet.createRow(index++);
			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(order.getGroupBillNo());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(order.getTrackNumber()==null?"":order.getTrackNumber());
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
			String fileName = "mfn_" + sdf.format(new Date()) + ".xls";
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
	
	@RequestMapping(value = "trackNumberAdd")
	public String trackNumberAdd(String country,HttpServletRequest request,HttpServletResponse response, Model model) {
		model.addAttribute("country", country);
		return "/modules/ebay/order/orderTrackNumberAdd";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"checkStock"})
	public String checkStock(String id,Integer quantity) {
		
		return null;
	}
	
	
	@RequestMapping(value = "readTrackNumberFile")
	public String readTrackNumberFile(@RequestParam("excel")MultipartFile txtFile,final String type, RedirectAttributes redirectAttributes){
			try {
				try {
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/mfnOrderTrackNumber/"+type+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					File baseDir = new File(baseDirStr); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String name = txtFile.getOriginalFilename();
					File dest = new File(baseDir,name);
					if(dest.exists()){
						dest.delete();
					}
					FileUtils.copyInputStreamToFile(txtFile.getInputStream(),dest);
				}catch(Exception e){}	
				Pattern pattern = Pattern.compile("[0-9]*"); 
				   
				Map<String,String> trackMap=Maps.newHashMap();
				InputStream ism=txtFile.getInputStream();
				InputStreamReader isr = new InputStreamReader(ism);
				BufferedReader br = new BufferedReader(isr);
				
				if("DHL".equals(type)){//https://nolp.dhl.de/nextt-online-public/set_identcodes.do?lang=de&rfn=&extendedSearch=true&idc=00340434137171506298
					String s = null;
					
					while((s = br.readLine())!=null){
						 // System.out.println(s);
						try{
							String[] arr=s.split(";");
							String trackNum=arr[1];
							String billNo=arr[13];
							if((billNo.length()==8&&pattern.matcher(billNo).matches())||billNo.contains("Test")||billNo.contains("Ersatz")||billNo.contains("Mfn")){
								trackMap.put(billNo, trackNum);
							}
						}catch(Exception e){
							LOGGER.info(s);
						}
						
					}
				}else if("DPD".equals(type)){//https://tracking.dpd.de/parcelstatus?locale=en_D2&query=01045209809775
					
					String s = null;
					int i=0;
					while((s = br.readLine())!=null){
						 if(i!=0){
							 try{
								 String[] arr=s.split(";");
								 String trackNum=arr[0];
								 String billNo=arr[3];
								 if((billNo.length()==8&&pattern.matcher(billNo).matches())||billNo.contains("Test")||billNo.contains("Ersatz")||billNo.contains("Mfn")){
									 trackMap.put(billNo, trackNum);
								 }
							 }catch(Exception e){
									LOGGER.info(s);
							 }
						 }
						i++;
					}
				}
				br.close();
				Map<String,List<MfnOrder>> orderMap=mfnOrderService.updateTrackNumber(type,trackMap,"de");
				final List<MfnOrder> allList=Lists.newArrayList();
				final List<MfnOrder> amazonOrder=orderMap.get("0");
				List<MfnOrder> amazonOrder1=orderMap.get("1");
				final List<MfnOrder> amazonOrder3=orderMap.get("3");
				if(amazonOrder!=null&&amazonOrder.size()>0){
					allList.addAll(amazonOrder);
					String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/mfnTrackNumber/";
					String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
					final File dir = new File(ctxPath+dateStr);
					dir.mkdirs();
					
					Map<String,List<MfnOrder>> tempMap=Maps.newHashMap();
					for (MfnOrder mfnOrder : amazonOrder) {
						List<MfnOrder> temp=tempMap.get(mfnOrder.getAccountName());
						if(temp==null){
							temp=Lists.newArrayList();
							tempMap.put(mfnOrder.getAccountName(),temp);
						}
					    temp.add(mfnOrder);
					}
					for (final Map.Entry<String,List<MfnOrder>> accountEntry: tempMap.entrySet()) {
						final String account=accountEntry.getKey();
						final String jsonStr =toJson(accountEntry.getValue());
						new Thread(){
							public void run(){   
								AmazonAccountConfig config=amazonAccountConfigService.getByName(account);
								submitTrackCode(jsonStr,config); 
							}
						}.start();
					}
					
				}
				if(amazonOrder3!=null&&amazonOrder3.size()>0){
					String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/mfnTrackNumber/";
					String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
					final File dir = new File(ctxPath+dateStr);
					dir.mkdirs();
					new Thread(){
						public void run(){   
							updateTrackNumberInEbay(amazonOrder3,"de") ;
						}
					}.start();
				}
				if(amazonOrder1!=null&&amazonOrder1.size()>0){
					allList.addAll(amazonOrder1);
				}
				try{
					List<MfnOrder> amazonOrder2=orderMap.get("2");
					if(amazonOrder2!=null&&amazonOrder2.size()>0){
						amazonUnlineOrderService.updateTrackNumber(amazonOrder2);
					}
				}catch(Exception e){
					LOGGER.info("error",e);
			    }
				
				if(allList!=null&&allList.size()>0){
					new Thread(){
						public void run(){  
							String href="";
							if("DHL".equals(type)){
								href="https://nolp.dhl.de/nextt-online-public/set_identcodes.do?lang=de&rfn=&extendedSearch=true&idc=";
							}else if("DPD".equals(type)){
								href="https://tracking.dpd.de/parcelstatus?locale=en_D2&query=";
							}
							for (MfnOrder order: allList) {
							    if(StringUtils.isNotBlank(order.getBuyerUserEmail())&&!"Invalid Request".equals(order.getBuyerUserEmail())){
							    	Date date = new Date(); 
							    	/*if(order.getBuyerUserEmail().contains("marketplace.amazon.de")){
							    		final MailInfo mailInfo = new MailInfo(order.getBuyerUserEmail(),"Logistik Tracking("+order.getOrderId()+")",date);
										String content="Sehr geehrter Inateck-Kunde,<br/><br/>";
										content+="vielen Dank für Ihre Bestellung bei uns. Die Sendung wurde soeben vorbereitet und wird voraussichtlich heute noch in den Versand gehen. Die Sendungsverfolgungsnummer wäre die <a href='"+href+order.getTrackNumber()+"' title='"+order.getOrderId()+"'>"+type+":"+order.getTrackNumber()+"</a>.<br/><br/>";
										content+="Bei Fragen und Problemen können Sie sich jederzeit an uns wenden, wir helfen Ihnen gerne weiter.<br/><br/>";
										content+="Mit freundlichen Grüßen,<br/><br/>Inateck Kundendienst";
										mailInfo.setContent(content);
										if(StringUtils.isNotBlank(order.getRemark())){
											mailInfo.setBccToAddress(order.getRemark());
										}
										customEmailBakManager.send(mailInfo);
							    	}else if(order.getBuyerUserEmail().contains("marketplace.amazon.fr")){
							    		final MailInfo mailInfo = new MailInfo(order.getBuyerUserEmail(),"Suivi logistique("+order.getOrderId()+")",date);
							    		String content="Cher  "+order.getBuyerUser()+",<br/><br/>Nous vous remercions de l'avoir acheté chez Inateck.<br/><br/>";
							    		//if(order.getId().endsWith("_ebay")||order.getId().endsWith("_amazon")){
										    content+="Nous sommes heureux de vous informer que les produits de la commande  ("+order.getOrderId()+") ont été expédiés via "+type+". Le numéro de suivi est  <a href='"+href+order.getTrackNumber()+"'>"+order.getTrackNumber()+"</a>.<br/><br/>";
							    		//}else{
							    		//	 content+="Nous sommes heureux de vous informer que les produits de la commande  ("+order.getOrderId()+") ont été expédiés via "+type+". Le numéro de suivi est  <a href='"+href+order.getTrackNumber()+"'>"+order.getTrackNumber()+"</a>.<br/><br/>";
									    //}
										content+="Meilleures salutations,<br/><br/>Inateck Service clientèle";
										mailInfo.setContent(content);
										if(StringUtils.isNotBlank(order.getRemark())){
											mailInfo.setBccToAddress(order.getRemark());
										}
										customEmailBakManager.send(mailInfo);
							    	}else if(order.getBuyerUserEmail().contains("marketplace.amazon.it")){
							    		final MailInfo mailInfo = new MailInfo(order.getBuyerUserEmail(),"Monitoraggio Logistica("+order.getOrderId()+")",date);
										String content="Gentile cliente Inateck,<br/><br/>";
										content+="la ringraziamo per aver acquistato uno dei nostri prodotti. L’ordine verrà elaborato il prima possibile e spedito in giornata. Il numero di tracking è il seguente: <a href='"+href+order.getTrackNumber()+"' title='"+order.getOrderId()+"'>"+type+":"+order.getTrackNumber()+"</a>.<br/><br/>";
										content+="Per qualsiasi problema o domanda, la preghiamo di mettersi in contatto con noi, saremo felici di aiutarla.<br/><br/>";
										content+="I più cordiali saluti,<br/><br/>Servizio clienti Inateck";
										mailInfo.setContent(content);
										if(StringUtils.isNotBlank(order.getRemark())){
											mailInfo.setBccToAddress(order.getRemark());
										}
										customEmailBakManager.send(mailInfo);
							    	}else if(order.getBuyerUserEmail().contains("marketplace.amazon.es")){
							    		final MailInfo mailInfo = new MailInfo(order.getBuyerUserEmail(),"Seguimiento de la logística("+order.getOrderId()+")",date);
										String content="Estimado cliente Inateck,<br/><br/>";
										content+="gracias por comprar uno de nuestros productos. Su pedido será elaborado cuanto antes y enviado hoy mismo. El número de tracking es el siguiente <a href='"+href+order.getTrackNumber()+"' title='"+order.getOrderId()+"'>"+type+":"+order.getTrackNumber()+"</a>.<br/><br/>";
										content+="Si tiene Usted preguntas o si encuentra algún problema con su pedido, por favor póngase en contacto con nosotros. Estamos aquí para ayudarle.<br/><br/>";
										content+="Muy atentamente,<br/><br/>Servicio de atención al cliente Inateck";
										mailInfo.setContent(content);
										if(StringUtils.isNotBlank(order.getRemark())){
											mailInfo.setBccToAddress(order.getRemark());
										}
										customEmailBakManager.send(mailInfo);
							    	}*/
							    	if(!order.getBuyerUserEmail().contains("marketplace.amazon")){
							    		final MailInfo mailInfo = new MailInfo(order.getBuyerUserEmail(),"Logistics Tracking("+order.getOrderId()+")",date);
										String content="Dear "+order.getBuyerUser()+",<br/><br/>Thank you for purchasing Inateck products!<br/><br/>";
									    content+="We are glad to inform you that products with order ID ("+order.getOrderId()+") have been shipped via "+type+". The tracking number is <a href='"+href+order.getTrackNumber()+"'>"+order.getTrackNumber()+"</a>.<br/><br/>";
										
										content+="Best regards,<br/><br/>Inateck Customer Service";
										mailInfo.setContent(content);
										if(StringUtils.isNotBlank(order.getRemark())){
											mailInfo.setBccToAddress(order.getRemark());
										}
										if(order.getBuyerUserEmail().contains("marketplace.amazon")){
											customEmailBakManager.send(mailInfo);
										}else{
											customEmailBakManager.send(mailInfo);
											try {
												Thread.sleep(1000);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
										}
							    	}
							     }
						     }
						}
					}.start();	
				}
				
			} catch (Exception e) {
				addMessage(redirectAttributes,"Failed to upload track number,Since the order is being synced,please try it later(Wait a few minutes)");
				LOGGER.info("更新track number异常",e);
			}
			return "redirect:"+Global.getAdminPath()+"/amazonAndEbay/mfnOrder/";
	}
	
	
	@RequestMapping(value = "readTrackNumberFile2")
	public String readTrackNumberFile2(@RequestParam("excel")MultipartFile txtFile,final String type,final String country,RedirectAttributes redirectAttributes){
			try {
				try {
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/mfnOrderTrackNumber/"+type+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					File baseDir = new File(baseDirStr); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String name = txtFile.getOriginalFilename();
					File dest = new File(baseDir,name);
					if(dest.exists()){
						dest.delete();
					}
					FileUtils.copyInputStreamToFile(txtFile.getInputStream(),dest);
				}catch(Exception e){}	
				Pattern pattern = Pattern.compile("[0-9]*"); 
				
				Map<String,Set<String>> billNoMap=Maps.newHashMap();
				
				Map<String,String> trackMap=Maps.newHashMap();
				Workbook workBook = WorkbookFactory.create(txtFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null||row.getCell(0)==null||row.getCell(1)==null) {
						continue;
					}
				    String billNo=StringUtils.trim(row.getCell(0).getStringCellValue());
				    String trackNum=StringUtils.trim(row.getCell(1).getStringCellValue());
				    if(StringUtils.isBlank(billNo)||StringUtils.isBlank(trackNum)){
				    	continue;
				    }
				    String orderType="";
				    if((billNo.length()==8&&pattern.matcher(billNo).matches())){//0
				    	orderType="0";
				    }else if(billNo.contains("Test")){//1
				    	orderType="1";
				    }else if(billNo.contains("Ersatz")){//2
				    	orderType="2";
				    }else if(billNo.contains("Mfn")){//3
				    	orderType="3";
				    }
				    if(StringUtils.isNotBlank(orderType)){
				    	Set<String> temp=billNoMap.get(orderType);
				    	if(temp==null){
				    		temp=Sets.newHashSet();
				    		billNoMap.put(orderType, temp);
				    	}
				    	Integer tempNo=Integer.parseInt(StringUtils.trim(billNo.replace("Mfn","").replace("Ersatz","").replace("Test","")));
				    	temp.add(tempNo.toString());
				    	trackMap.put(billNo, trackNum);
				    }
				}   
				
				Map<String,MfnOrder> orderTrackMap=mfnOrderService.findOrderByBillNo(billNoMap,country);
				Map<String,Integer> quantityMap=Maps.newHashMap();
				Map<String,List<MfnOrder>> orderMap=Maps.newHashMap();
				Map<String,String> idTrackMap=Maps.newHashMap();
				
				Map<String,List<MfnOrder>> countryOrderMap=Maps.newHashMap();
				Map<String,List<MfnOrder>> ebayCountryOrderMap=Maps.newHashMap();
				
				for (Map.Entry<String,MfnOrder>  entry: orderTrackMap.entrySet()) {
					String billNo=entry.getKey();
					MfnOrder order =entry.getValue();
					if(StringUtils.isBlank(order.getTrackNumber())|| (StringUtils.isNotBlank(order.getTrackNumber())&&!order.getTrackNumber().equals(trackMap.get(billNo)))){
						if(StringUtils.isBlank(order.getTrackNumber())){
							for(MfnOrderItem item:order.getItems()){
								Integer quantity=(quantityMap.get(item.getTitle())==null?0:quantityMap.get(item.getTitle()));
								quantityMap.put(item.getTitle(), quantity+item.getQuantityPurchased());
							}
						}
						
						idTrackMap.put(order.getId(), trackMap.get(billNo));
						if(order.getId().endsWith("_amazon")){
							
							List<MfnOrder> orderList=countryOrderMap.get(order.getCountry());
							if(orderList==null){
								orderList=Lists.newArrayList();
								countryOrderMap.put(order.getCountry(), orderList);
							}
							order.setTrackNumber(trackMap.get(billNo));
							order.setSupplier(type);
							orderList.add(order);
							
						}else{
							if(order.getId().endsWith("_ebay")||order.getOrderId().startsWith("MFN-DZW-")){
								List<MfnOrder> orderList=ebayCountryOrderMap.get(order.getCountry());
								if(orderList==null){
									orderList=Lists.newArrayList();
									ebayCountryOrderMap.put(order.getCountry(), orderList);
								}
								order.setTrackNumber(trackMap.get(billNo));
								order.setSupplier(type);
								orderList.add(order);
							}
							if(order.getId().endsWith("_mfn")){
								List<MfnOrder> orderList=orderMap.get("2");
								if(orderList==null){
									orderList=Lists.newArrayList();
									orderMap.put("2", orderList);
								}
								order.setTrackNumber(trackMap.get(billNo));
								order.setSupplier(type);
								orderList.add(order);
							}
						}
						
					}
				}
				
            
				String info=mfnOrderService.updateTrackNumber(idTrackMap,type,quantityMap,(country.startsWith("com"))?120:147);//id num
				if(StringUtils.isNotBlank(info)){
            		addMessage(redirectAttributes,"error:"+info);
    			    return "redirect:"+Global.getAdminPath()+"/amazonAndEbay/mfnOrder/trackNumberAdd?country="+country;
            	}
				
				if(countryOrderMap!=null&&countryOrderMap.size()>0){
					for (Map.Entry<String,List<MfnOrder>> entry: countryOrderMap.entrySet()) {
						final String tempCountry=entry.getKey();
						final List<MfnOrder> tempOrders=entry.getValue();
						String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/mfnTrackNumber/";
						String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
						final File dir = new File(ctxPath+dateStr);
						dir.mkdirs();
						
						Map<String,List<MfnOrder>> tempMap=Maps.newHashMap();
						for (MfnOrder mfnOrder : tempOrders) {
							List<MfnOrder> temp=tempMap.get(mfnOrder.getAccountName());
							if(temp==null){
								temp=Lists.newArrayList();
								tempMap.put(mfnOrder.getAccountName(),temp);
							}
						    temp.add(mfnOrder);
						}
						for (final Map.Entry<String,List<MfnOrder>> accountEntry: tempMap.entrySet()) {
							final String account=accountEntry.getKey();
							final String jsonStr =toJson(accountEntry.getValue());
							new Thread(){
								public void run(){   
									AmazonAccountConfig config=amazonAccountConfigService.getByName(account);
									submitTrackCode(jsonStr,config); 
								}
							}.start();
						}
						
//						new Thread(){
//							public void run(){   
//								submitTrackCode(tempCountry,tempOrders,dir) ;
//							}
//						}.start();
					}
				}
				
		
				if(ebayCountryOrderMap!=null&&ebayCountryOrderMap.size()>0){
					for (Map.Entry<String,List<MfnOrder>> entry: ebayCountryOrderMap.entrySet()) {
						final String tempCountry=entry.getKey();
						final List<MfnOrder> tempOrders=entry.getValue();
						String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/mfnTrackNumber/";
						String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
						final File dir = new File(ctxPath+dateStr);
						dir.mkdirs();
						new Thread(){
							public void run(){   
								updateTrackNumberInEbay(tempOrders,tempCountry) ;
							}
						}.start();
					}
				}
				
				try{
					List<MfnOrder> amazonOrder2=orderMap.get("2");
					if(amazonOrder2!=null&&amazonOrder2.size()>0){
						amazonUnlineOrderService.updateTrackNumber(amazonOrder2);
					}
				}catch(Exception e){
					LOGGER.info("error",e);
			    }
				
			} catch (Exception e) {
				addMessage(redirectAttributes,"Failed to upload track number,Since the order is being synced,please try it later(Wait a few minutes),"+e);
				LOGGER.info("更新track number异常",e);
			}
			return "redirect:"+Global.getAdminPath()+"/amazonAndEbay/mfnOrder/?country=com";
	}
	
	@RequestMapping(value = "readCountryTrackNumberFile")
	public String readCountryTrackNumberFile(@RequestParam("excel")MultipartFile excelFile,RedirectAttributes redirectAttributes){
			try {
				Map<String,AmazonAccountConfig> configMap=amazonAccountConfigService.findConfigByAccountName();
				try {
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/mfnOrderTrackNumber/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
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
				
				Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				// 循环行Row  amazonorderId  supplier  number
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			    Map<String,List<MfnOrder>> tempMap=Maps.newHashMap();
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null||StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
						continue;
					}
				    String amazonOrderId=StringUtils.trim(row.getCell(0).getStringCellValue());
				    MfnOrder mfnOrder=mfnOrderService.getByOrderId2(amazonOrderId); 
				    if(mfnOrder!=null){
				    	    String supplier=StringUtils.trim(row.getCell(1).getStringCellValue());
						    String number=StringUtils.trim(row.getCell(2).getStringCellValue());
						    MfnOrder order=new MfnOrder();
						    order.setOrderId(amazonOrderId);
						    order.setSupplier(supplier);
						    order.setTrackNumber(number);
						    try{
						    	order.setTrackingDate(sdf.parse(StringUtils.trim(row.getCell(3).getStringCellValue())+" 23:00:00"));
						    }catch(Exception e){
						    	order.setTrackingDate(sdf1.parse(StringUtils.trim(row.getCell(3).getStringCellValue())+" 23:00:00"));
						    }
						    
						    AmazonAccountConfig config=configMap.get(mfnOrder.getAccountName());
						    if(config!=null){
						    	    order.setCountry(config.getCountry());
								    List<MfnOrder> list=tempMap.get(mfnOrder.getAccountName());
								    if(list==null){
								    	list=Lists.newArrayList();
								    	tempMap.put(mfnOrder.getAccountName(),list);
								    }
								    list.add(order);
						    }
				    }
				}
				
				if(tempMap!=null&&tempMap.size()>0){
					for ( Map.Entry<String,List<MfnOrder>> entry: tempMap.entrySet()) {
						List<MfnOrder> amazonOrder=entry.getValue();
						final String jsonStr =toJson(amazonOrder);
						mfnOrderService.updateTrackNumber(amazonOrder);
						final AmazonAccountConfig config=configMap.get(entry.getKey());
						final String country=config.getCountry(); 
						String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/mfnTrackNumber/"+country+"/";
						String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
						final File dir = new File(ctxPath+dateStr);
						dir.mkdirs();
						
						String ctxPath2 = ContextLoader.getCurrentWebApplicationContext()
								.getServletContext().getRealPath("/")
								+ Global.getCkBaseDir() + "/orderStatuFeeds/";
						File dir2 = new File(ctxPath2 + country+"/" + dateStr);
						dir2.mkdirs();
						
						new Thread(){
							public void run(){   
								submitTrackCode(jsonStr,config); 
							}
						}.start();
					}
					
				}
				addMessage(redirectAttributes,"Successful");
			} catch (Exception e) {
				addMessage(redirectAttributes,"Failed to upload track number,Since the order is being synced,please try it later(Wait a few minutes)");
				LOGGER.info("更新track number异常",e);
			}
			
			return "redirect:"+Global.getAdminPath()+"/amazonAndEbay/mfnOrder/shipAndUpdateTrackNumber";
	}
	
	
	@RequestMapping(value = "createCurrentOrder")
	public String createCurrentOrder(MfnOrder mfnOrder,HttpServletRequest request,HttpServletResponse response, Model model){
		Map<String,Integer> totalMap=mfnOrderService.getCurrentDateTotal(mfnOrder);
		Map<String,List<MfnOrderItem>> itemMap=mfnOrderService.getCurrentDateList(mfnOrder);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		List<String> title = Lists.newArrayList("product_name","total_quantity","order_no","bill_no","order_quantity","order_time");
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		 HSSFCell cell = null;		
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int index=1;
		for (Map.Entry<String,Integer> entry: totalMap.entrySet()) { 
		    String sku = entry.getKey();
			int num=1;
			if(itemMap!=null&&itemMap.get(sku)!=null&&itemMap.get(sku).size()>0){
				for (MfnOrderItem item : itemMap.get(sku)) {
					int j=0;
					row=sheet.createRow(index++);
		    		row.setHeight((short) 400);
					if(num==1){
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sku);
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
						row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(entry.getValue());
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
						/*row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getProductName());
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);*/
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
						row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
						/*row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);*/
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getOrderId());
					sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getBillNo());
					sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getQuantityShipped());
					sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(item.getPrintTime()));
					sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					if(itemMap.get(sku).size()==num){
						sheet.addMergedRegion(new CellRangeAddress(index-1-itemMap.get(sku).size()+1,index-1,0,0));
						sheet.addMergedRegion(new CellRangeAddress(index-1-itemMap.get(sku).size()+1,index-1,1,1));
						/*sheet.addMergedRegion(new CellRangeAddress(index-1-itemMap.get(sku).size()+1,index-1,2,2));*/
					}
					num++;
				}
			}
				
		}
		for (int i = 0; i < title.size(); i++) {
       		 sheet.autoSizeColumn((short)i,true);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmm");
			String fileName = "mfn_" + sdf.format(new Date()) + ".xls";
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
	
	@RequestMapping(value = "shipAndUpdateTrackNumber")
	public String shipAndUpdateTrackNumber(Model model){
	    model.addAttribute("accountMap",amazonAccountConfigService.findCountryByAccountByServer());
		return "/modules/ebay/order/orderTrackNumberAllCountryAdd";
	}
	
	
	public  boolean  updateShippedByOrders(List<MfnOrder> orders) {
		
		final Map<String,List<MfnOrder>> ebayOrders=Maps.newHashMap();
		final Map<String,List<String>> amazonOrders=Maps.newHashMap();
		for (MfnOrder mfnOrder : orders) {
			String country=mfnOrder.getCountry();
			if (mfnOrder.getId().contains("ebay")) {
				List<MfnOrder> temp=ebayOrders.get(country);
				if(temp==null){
					temp=Lists.newArrayList();
					ebayOrders.put(country, temp);
				}
				temp.add(mfnOrder);
			} else if (mfnOrder.getId().contains("amazon")) {
				List<String> temp=amazonOrders.get(mfnOrder.getAccountName());
				if(temp==null){
					temp=Lists.newArrayList();
					amazonOrders.put(mfnOrder.getAccountName(), temp);
				}
				temp.add(mfnOrder.getOrderId());
			}
		}
		if(ebayOrders.size()>0){
			for (Map.Entry<String,List<MfnOrder>> entry: ebayOrders.entrySet()) {
				 final String country=entry.getKey();
				 final List<MfnOrder> orderList=entry.getValue();
				 new Thread(){
						public void run() {
							updateOrderShippedInEbay(country,orderList);
						}
				}.start();	
			}
		}
		
		
		boolean flag = true;
		if(amazonOrders.size()>0){
			for (Map.Entry<String,List<String>> entry: amazonOrders.entrySet()) {
				 final String accountName=entry.getKey();
				 final List<String> orderIds = entry.getValue();
				 new Thread(){
						public void run() {
							AmazonAccountConfig config=amazonAccountConfigService.getByName(accountName);
							try {
								String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
								Client client = BaseService.getCxfClient(interfaceUrl);
								Object[] str = new Object[]{Global.getConfig("ws.key"),config.getCountry(),orderIds,config.getAccountName()};
								client.invoke("updateShippedState", str);
							} catch (Exception e) {
								logger.error(config.getAccountName()+"更新发货状态错误："+e.getMessage(), e);
							}
						}
				}.start();	
			}
		}
		return flag;
	}
	
	public void submitTrackCode(String orders,AmazonAccountConfig config) {
		try {
			String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"),config.getCountry(),orders,config.getAccountName()};
			client.invoke("submitAmzTrackCode", str);
		} catch (Exception e) {
			logger.error(config.getAccountName()+"上传物流错误："+e.getMessage(), e);
		}
	}
	
	public void updateOrderShippedInEbay(String country,List<MfnOrder> orders) {
		ApiContext apiContext = new ApiContext();
		ApiCredential cred = apiContext.getApiCredential();
	
		if("com".equals(country)){
			cred.seteBayToken(EbayConstants.EBAYTOKEN_US);
			ApiAccount account = cred.getApiAccount();
			account.setDeveloper(EbayConstants.DEVID_US);
			account.setApplication(EbayConstants.APPID_US);
			account.setCertificate(EbayConstants.CERTID_US);
			apiContext.setApiServerUrl(EbayConstants.APISERVERURL);
			apiContext.setSite(SiteCodeType.US);
		}else{
			cred.seteBayToken(EbayConstants.EBAYTOKEN);
			ApiAccount account = cred.getApiAccount();
			account.setDeveloper(EbayConstants.DEVID);
			account.setApplication(EbayConstants.APPID);
			account.setCertificate(EbayConstants.CERTID);
			apiContext.setApiServerUrl(EbayConstants.APISERVERURL);
			apiContext.setSite(SiteCodeType.GERMANY);
		}
		
		ApiLogging apiLog = apiContext.getApiLogging();
		apiLog.setLogSOAPMessages(false);
		apiLog.setLogHTTPHeaders(false);
		apiLog.setLogExceptions(false);
		apiContext.setApiLogging(apiLog);
		for (MfnOrder mfnOrder : orders) {
			try {
				CompleteSaleCall call = new CompleteSaleCall(apiContext);
				call.setOrderID(mfnOrder.getOrderId());
				call.setEnableCompression(false);
				call.setShipped(true);
				call.completeSale();
			} catch (ApiException e) {
				logger.warn("更改ebay订单状态api出错：" + e.getMessage(), e);
			} catch (SdkException e) {
				// logger.warn("更改ebay订单状态sdk出错："+e.getMessage(),e);
			} catch (Exception e) {
				logger.warn("更改ebay订单状态出错：" + e.getMessage(), e);
			}
		}
	}

	public void updateTrackNumberInEbay(List<MfnOrder> orderList,String country) {
		ApiContext apiContext = new ApiContext();
		ApiCredential cred = apiContext.getApiCredential();
		if("de".equals(country)){
			cred.seteBayToken(EbayConstants.EBAYTOKEN);
			ApiAccount account = cred.getApiAccount();
			account.setDeveloper(EbayConstants.DEVID);
			account.setApplication(EbayConstants.APPID);
			account.setCertificate(EbayConstants.CERTID);
			apiContext.setApiServerUrl(EbayConstants.APISERVERURL);
			apiContext.setSite(SiteCodeType.GERMANY);
		}else{
			cred.seteBayToken(EbayConstants.EBAYTOKEN_US);
			ApiAccount account = cred.getApiAccount();
			account.setDeveloper(EbayConstants.DEVID_US);
			account.setApplication(EbayConstants.APPID_US);
			account.setCertificate(EbayConstants.CERTID_US);
			apiContext.setApiServerUrl(EbayConstants.APISERVERURL);
			apiContext.setSite(SiteCodeType.US);
		}

		ApiLogging apiLog = apiContext.getApiLogging();
		apiLog.setLogSOAPMessages(false);
		apiLog.setLogHTTPHeaders(false);
		apiLog.setLogExceptions(false);
		apiContext.setApiLogging(apiLog);
		CompleteSaleCall call = new CompleteSaleCall(apiContext);
		call.setEnableCompression(false);
		call.setShipped(true);
		LOGGER.info("ebay物流更新开始");
		 for (MfnOrder order:orderList) { 
		    String orderId =order.getOrderId();
			try {
				call.setOrderID(orderId);
				ShipmentType shipmentType = new ShipmentType();
				List<ShipmentTrackingDetailsType> detailsTypeList = Lists.newArrayList(); 
				ShipmentTrackingDetailsType detailsType = new ShipmentTrackingDetailsType();
				detailsType.setShippingCarrierUsed(order.getSupplier());
				detailsType.setShipmentTrackingNumber(order.getTrackNumber());
				detailsTypeList.add(detailsType);
				shipmentType.setShipmentTrackingDetails(detailsTypeList.toArray(new ShipmentTrackingDetailsType[detailsTypeList.size()]));
				call.setShipment(shipmentType);
				call.completeSale();
			} catch (ApiException e) {
				
				try{
					call.setOrderID(orderId);
					ShipmentType shipmentType = new ShipmentType();
					List<ShipmentTrackingDetailsType> detailsTypeList = Lists.newArrayList(); 
					ShipmentTrackingDetailsType detailsType = new ShipmentTrackingDetailsType();
					detailsType.setShippingCarrierUsed(order.getSupplier());
					detailsType.setShipmentTrackingNumber(order.getTrackNumber()+"G");
					detailsTypeList.add(detailsType);
					shipmentType.setShipmentTrackingDetails(detailsTypeList.toArray(new ShipmentTrackingDetailsType[detailsTypeList.size()]));
					call.setShipment(shipmentType);
					call.completeSale();
				}catch(Exception ex){
					logger.warn(country+"更改ebay订单物流api出错：" +orderId+ e.getMessage(), e);
				}
			} catch (SdkException e) {
				// logger.warn("更改ebay订单状态sdk出错："+e.getMessage(),e);
			} catch (Exception e) {
				logger.warn(country+"更改ebay订单物流出错：" + e.getMessage(), e);
			}
		}	
		LOGGER.info("ebay物流更新结束");
	}
	

	@RequestMapping(value = "updateShipFlag")
	@ResponseBody
	public String updateShipFlag(String id,String flag){
		mfnOrderService.updateShipFlag(id);
		return "1";
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
	

	@RequestMapping(value = "genTrackingNumber")
	@ResponseBody
	public String genTrackingNumber(MfnOrder mfnOrder,String packageType,String mailClass){
		if(StringUtils.isNotBlank(mfnOrder.getTrackNumber())){
			throw new RuntimeException("Tracking number must be empty");
		}
		BigDecimal length=mfnOrder.getLength();
		BigDecimal width=mfnOrder.getWidth();
		BigDecimal height=mfnOrder.getHeight();
		Integer weight=mfnOrder.getWeight();
		if(length==null||width==null||height==null||weight==null){
			for (MfnOrderItem item : mfnOrder.getItems()) {
				String name=item.getTitle();
				if(StringUtils.isNotBlank(name)&&name.contains(" ")){
					name=name.substring(name.indexOf(" ")+1);
					if(name.contains("_")){
						name=name.substring(0,name.lastIndexOf("_"));
					}
					PsiProduct product = productService.findSize(name);
					if(product!=null){
						  if(product.getProductPackLength()!=null&&(length==null||length.floatValue()<product.getProductPackLength().floatValue())){
							  length=product.getProductPackLength();
						  }
						  if(product.getProductPackWidth()!=null&&(width==null||width.floatValue()<product.getProductPackWidth().floatValue())){
							  width=product.getProductPackWidth();
						  }
						  if(product.getProductPackHeight()!=null&&(height==null||height.floatValue()<product.getProductPackHeight().floatValue())){
							  height=product.getProductPackHeight();
						  }
						  if(product.getProductPackWeight()!=null&&(weight==null||weight<product.getProductPackWeight().doubleValue())){
							  weight=MathUtils.roundDown(product.getProductPackWeight().doubleValue());
						  }
					}
				}
			}
		}
		if(length==null||width==null||height==null||weight==null){
			return "0";
		}
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		UspsL5 uspsL5 = new UspsL5();
		uspsL5.setDate(format.format(DateUtils.addHours(new Date(),-15)));
		uspsL5.setMailClass(mailClass);
		uspsL5.setPackageType(packageType);
		//inch oz
		uspsL5.setWeight(weight);
		uspsL5.setLength(length.floatValue());
		uspsL5.setWidth(width.floatValue());
		uspsL5.setHeight(height.floatValue());
		
		uspsL5.setSenderName("SHIPPING DEPT");
		uspsL5.setSenderCompany("INATECK TECHNOLOGY");
		uspsL5.setSenderAddress1("6045 HARRISON DR #6");
		uspsL5.setSenderCity("LAS VEGAS");
		uspsL5.setSenderState("NV");
		uspsL5.setSenderZip("89120");
		uspsL5.setSenderZip4("6045");
		
		MfnAddress mfnAddress=mfnOrder.getShippingAddress();
		
		uspsL5.setReceiverName(mfnAddress.getName());
		uspsL5.setReceiverCompany("");
		String address=mfnAddress.getStreet();
		String address2 = "";
		if(StringUtils.isNotBlank(address)&&StringUtils.isNotBlank(mfnAddress.getStreet1())){
			address2=mfnAddress.getStreet1();
		}
		if(StringUtils.isBlank(address)&&StringUtils.isNotBlank(mfnAddress.getStreet1())){
		    address=mfnAddress.getStreet1();
		}
		if(StringUtils.isNotBlank(address)&&StringUtils.isNotBlank(mfnAddress.getStreet2())){
			address2=mfnAddress.getStreet2();
		}
		if(StringUtils.isBlank(address)&&StringUtils.isNotBlank(mfnAddress.getStreet2())){
		    address=mfnAddress.getStreet2();
		}
		uspsL5.setReceiverAddress1(address);
        if(StringUtils.isNotBlank(address2)){
        	uspsL5.setReceiverAddress2(address2);
        }
		uspsL5.setReceiverCity(mfnAddress.getCityName()==null?"":mfnAddress.getCityName());
		uspsL5.setReceiverState(mfnAddress.getStateOrProvince()==null?"":mfnAddress.getStateOrProvince());
		uspsL5.setReceiverZip(mfnAddress.getPostalCode());
		uspsL5.setInsurance(0);
		
		String result = UspsL5Service.createShipment(uspsL5);
		
		JSONObject object = (JSONObject) JSON.parse(result);
		if (StringUtils.isNotEmpty(object.getString("error"))) {
			return object.getString("error");
		}
		String trackingNumber = object.getString("trackingNumber");
		String labelImage = object.getString("labelImage");	//标签图片(png)base64编码后的字符串
		Float total = object.getJSONObject("quote").getInteger("total")/100f;	//总费用美分(基础费用+保险)
		
		try {
			//保存图片方法
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir()+"trackingImg";
			File baseDir = new File(baseDirStr);
			if (!baseDir.isDirectory())
				baseDir.mkdirs();
			String image=Global.getCkBaseDir()+"trackingImg/"+mfnOrder.getOrderId()+"-"+trackingNumber+".png";
			
			File imageFile = new File(baseDir, mfnOrder.getOrderId()+"-"+trackingNumber+".png");
			FileUtils.writeByteArrayToFile(imageFile, Encodes.getUnBASE64Byte(labelImage.getBytes()));
			mfnOrder.setSupplier("USPS");
			mfnOrder.setTrackNumber(trackingNumber);
			mfnOrder.setTrackingFlag("0");
			mfnOrder.setLabelImage(image);
			mfnOrder.setFee(total);
			mfnOrderService.updateTrackingInfo("USPS",trackingNumber,image,"0",total,mfnOrder.getId());
			String temp= mfnOrderService.get(mfnOrder.getId()).getTrackNumber();
			if(StringUtils.isBlank(temp)){
				logger.error(mfnOrder.getOrderId()+"更新物流失败");
				return "Update tracking number failed:"+UspsL5Service.voidByTrackingNumber(trackingNumber);
			}
			return "1,"+image;
		} catch (Exception e) {
			logger.error("写入标签失败"+trackingNumber, e);
		}
		LOGGER.info(mfnOrder.getId()+" cancel tracking number ");
		return "Cancel TrackingNumber:"+UspsL5Service.voidByTrackingNumber(trackingNumber);
	}
	
	@RequestMapping(value = "voidByTrackingNumber")
	@ResponseBody
	public String voidByTrackingNumber(MfnOrder mfnOrder){
		String result=UspsL5Service.voidByTrackingNumber(mfnOrder.getTrackNumber());
		if (!result.contains("error")) {
			mfnOrderService.updateTrackingInfo(null,null,null,null,null,mfnOrder.getId());
		}
		LOGGER.info(mfnOrder.getId()+" cancel tracking number ");
		return "Cancel TrackingNumber:"+result;
	}
	
	@RequestMapping(value = "postageBalance")
	@ResponseBody
	public String postageBalance(){
		String resultBalance = UspsL5Service.postageBalance();
		JSONObject object = (JSONObject) JSON.parse(resultBalance);
		Integer total = object.getInteger("postageBalance");
		if(total<100000){
			return "purchasePostage:"+UspsL5Service.purchasePostage(100000-total);
		}
		return "PostageBalance:"+total;
	}
	
	
	@RequestMapping(value = "feeView")
	@ResponseBody
	public String feeView(MfnOrder mfnOrder,String packageType,String mailClass){
		BigDecimal length=mfnOrder.getLength();
		BigDecimal width=mfnOrder.getWidth();
		BigDecimal height=mfnOrder.getHeight();
		Integer weight=mfnOrder.getWeight();
		if(length==null||width==null||height==null||weight==null){
			for (MfnOrderItem item : mfnOrder.getItems()) {
				String name=item.getTitle();
				if(StringUtils.isNotBlank(name)&&name.contains(" ")){
					name=name.substring(name.indexOf(" ")+1);
					if(name.contains("_")){
						name=name.substring(0,name.lastIndexOf("_"));
					}
					PsiProduct product = productService.findSize(name);
					if(product!=null){
						  if(product.getProductPackLength()!=null&&(length==null||length.floatValue()<product.getProductPackLength().floatValue())){
							  length=product.getProductPackLength();
						  }
						  if(product.getProductPackWidth()!=null&&(width==null||width.floatValue()<product.getProductPackWidth().floatValue())){
							  width=product.getProductPackWidth();
						  }
						  if(product.getProductPackHeight()!=null&&(height==null||height.floatValue()<product.getProductPackHeight().floatValue())){
							  height=product.getProductPackHeight();
						  }
						  if(product.getProductPackWeight()!=null&&(weight==null||weight<product.getProductPackWeight().doubleValue())){
							  weight=MathUtils.roundDown(product.getProductPackWeight().doubleValue());
						  }
					}
				}
			}
		}
		if(length==null||width==null||height==null||weight==null){
			return "0";
		}
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		UspsL5 uspsL5 = new UspsL5();
		uspsL5.setDate(format.format(DateUtils.addHours(new Date(),-15)));
		uspsL5.setMailClass(mailClass);
		uspsL5.setPackageType(packageType);
		//inch oz
		uspsL5.setWeight(weight);
		uspsL5.setLength(length.floatValue());
		uspsL5.setWidth(width.floatValue());
		uspsL5.setHeight(height.floatValue());
		
		uspsL5.setSenderName("SHIPPING DEPT");
		uspsL5.setSenderCompany("INATECK TECHNOLOGY");
		uspsL5.setSenderAddress1("6045 HARRISON DR #6");
		uspsL5.setSenderCity("LAS VEGAS");
		uspsL5.setSenderState("NV");
		uspsL5.setSenderZip("89120");
		uspsL5.setSenderZip4("6045");
		
		MfnAddress mfnAddress=mfnOrder.getShippingAddress();
		
		uspsL5.setReceiverName(mfnAddress.getName());
		uspsL5.setReceiverCompany("");
		String address=mfnAddress.getStreet();
		String address2 = "";
		if(StringUtils.isNotBlank(address)&&StringUtils.isNotBlank(mfnAddress.getStreet1())){
			address2=mfnAddress.getStreet1();
		}
		if(StringUtils.isBlank(address)&&StringUtils.isNotBlank(mfnAddress.getStreet1())){
		    address=mfnAddress.getStreet1();
		}
		if(StringUtils.isNotBlank(address)&&StringUtils.isNotBlank(mfnAddress.getStreet2())){
			address2=mfnAddress.getStreet2();
		}
		if(StringUtils.isBlank(address)&&StringUtils.isNotBlank(mfnAddress.getStreet2())){
		    address=mfnAddress.getStreet2();
		}
		uspsL5.setReceiverAddress1(address);
        if(StringUtils.isNotBlank(address2)){
        	uspsL5.setReceiverAddress2(address2);
        }
		uspsL5.setReceiverCity(mfnAddress.getCityName()==null?"":mfnAddress.getCityName());
		uspsL5.setReceiverState(mfnAddress.getStateOrProvince()==null?"":mfnAddress.getStateOrProvince());
		uspsL5.setReceiverZip(mfnAddress.getPostalCode());
		uspsL5.setInsurance(0);
		
		String result = UspsL5Service.quote(uspsL5);
		JSONObject object = (JSONObject) JSON.parse(result);
		if (StringUtils.isNotEmpty(object.getString("error"))) {
			return object.getString("error");
		}
		Integer total = object.getInteger("total");	//总费用美分(基础费用+保险)
		return "Fee:"+total/100f+"$,"+UspsL5Service.postageBalance()+"￠";
	}
	
	
	@RequestMapping(value = "trackingView")
	public String trackingView(MfnOrder mfnOrder,Model model){
		BigDecimal length=mfnOrder.getLength();
		BigDecimal width=mfnOrder.getWidth();
		BigDecimal height=mfnOrder.getHeight();
		Integer weight=mfnOrder.getWeight();
		if(length==null||width==null||height==null||weight==null){
			for (MfnOrderItem item : mfnOrder.getItems()) {
				String name=item.getTitle();
				if(StringUtils.isNotBlank(name)&&name.contains(" ")){
					name=name.substring(name.indexOf(" ")+1);
					if(name.contains("_")){
						name=name.substring(0,name.lastIndexOf("_"));
					}
					PsiProduct product = productService.findSize(name);
					if(product!=null){
						  if(product.getProductPackLength()!=null&&(length==null||length.floatValue()<product.getProductPackLength().floatValue())){
							  length=product.getProductPackLength();
						  }
						  if(product.getProductPackWidth()!=null&&(width==null||width.floatValue()<product.getProductPackWidth().floatValue())){
							  width=product.getProductPackWidth();
						  }
						  if(product.getProductPackHeight()!=null&&(height==null||height.floatValue()<product.getProductPackHeight().floatValue())){
							  height=product.getProductPackHeight();
						  }
						  if(product.getProductPackWeight()!=null&&(weight==null||weight<product.getProductPackWeight().doubleValue())){
							  weight=MathUtils.roundDown(product.getProductPackWeight().doubleValue());
						  }
					}
				}
			}
		}
		mfnOrder.setLength(length);
		mfnOrder.setWidth(width);
		mfnOrder.setHeight(height);
		mfnOrder.setWeight(weight);
		model.addAttribute("mfnOrder", mfnOrder);
		return "modules/ebay/order/mfnTrackingEdit";
	}
	
	
	@RequestMapping(value = "outbound")
	@ResponseBody
	public String outbound(final String printIds,String country,String flag) {//0  1  2 
		String[] idArr=printIds.split(",");
		Map<String,Integer> quantityMap=Maps.newHashMap();
		Map<String,Integer> offlineQuantityMap=Maps.newHashMap();
		Map<String,String> trackMap=Maps.newHashMap();
		
		Map<String,List<MfnOrder>> countryOrderMap=Maps.newHashMap();
		Map<String,List<MfnOrder>> ebayCountryOrderMap=Maps.newHashMap();
		Map<String,List<MfnOrder>> orderMap=Maps.newHashMap();
		
		for (String orderId: idArr) {
			     MfnOrder order=mfnOrderService.get(orderId);
				 if(StringUtils.isBlank(order.getSupplier())){
					   continue;
				 }
				 trackMap.put(order.getId(), order.getOrderId());
				 for (MfnOrderItem item : order.getItems()) {
					 if("Offline".equals(order.getRateSn())){
						 Integer qty = (offlineQuantityMap.get(item.getTitle())==null?0:offlineQuantityMap.get(item.getTitle()));
						 offlineQuantityMap.put(item.getTitle(), qty+item.getQuantityPurchased());
					 }else{
						 Integer qty = (quantityMap.get(item.getTitle())==null?0:quantityMap.get(item.getTitle()));
						 quantityMap.put(item.getTitle(), qty+item.getQuantityPurchased());
					 }
				 }
				  
				   if(order.getId().endsWith("_amazon")){
						List<MfnOrder> orderList=countryOrderMap.get(order.getAccountName());
						if(orderList==null){
							orderList=Lists.newArrayList();
							countryOrderMap.put(order.getAccountName(), orderList);
						}
						orderList.add(order);
					}else{
						if(order.getId().endsWith("_ebay")||order.getOrderId().startsWith("MFN-DZW-")){
							List<MfnOrder> orderList=ebayCountryOrderMap.get(order.getCountry());
							if(orderList==null){
								orderList=Lists.newArrayList();
								ebayCountryOrderMap.put(order.getCountry(), orderList);
							}
							orderList.add(order);
						}
						if(order.getId().endsWith("_mfn")){
							List<MfnOrder> orderList=orderMap.get("2");
							if(orderList==null){
								orderList=Lists.newArrayList();
								orderMap.put("2", orderList);
							}
							orderList.add(order);
						}
					}
			
		}
		if(StringUtils.isBlank(flag)){
			String info=mfnOrderService.updateTrackNumber(trackMap,null,quantityMap,offlineQuantityMap,(country.startsWith("com"))?120:147);//id num
			if(StringUtils.isNotBlank(info)){
	    		return "error:"+info;
	    	}
		}
		
		if(countryOrderMap!=null&&countryOrderMap.size()>0){
			for (Map.Entry<String,List<MfnOrder>> entry: countryOrderMap.entrySet()) {
				final String tempCountry=entry.getKey();
				final List<MfnOrder> tempOrders=entry.getValue();
				String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/mfnTrackNumber/";
				String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
				final File dir = new File(ctxPath+dateStr);
				dir.mkdirs();
				
				Map<String,List<MfnOrder>> tempMap=Maps.newHashMap();
				for (MfnOrder mfnOrder : tempOrders) {
					List<MfnOrder> temp=tempMap.get(mfnOrder.getAccountName());
					if(temp==null){
						temp=Lists.newArrayList();
						tempMap.put(mfnOrder.getAccountName(),temp);
					}
				    temp.add(mfnOrder);
				}
				for (final Map.Entry<String,List<MfnOrder>> accountEntry: tempMap.entrySet()) {
					final String account=accountEntry.getKey();
					final String jsonStr =toJson(accountEntry.getValue());
					new Thread(){
						public void run(){   
							AmazonAccountConfig config=amazonAccountConfigService.getByName(account);
							submitTrackCode(jsonStr,config); 
						}
					}.start();
				}
			}
		}
		

		if(ebayCountryOrderMap!=null&&ebayCountryOrderMap.size()>0){
			for (Map.Entry<String,List<MfnOrder>> entry: ebayCountryOrderMap.entrySet()) {
				final String tempCountry=entry.getKey();
				final List<MfnOrder> tempOrders=entry.getValue();
				String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/mfnTrackNumber/";
				String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
				final File dir = new File(ctxPath+dateStr);
				dir.mkdirs();
				new Thread(){
					public void run(){   
						updateTrackNumberInEbay(tempOrders,tempCountry) ;
					}
				}.start();
			}
		}
		
		try{
			List<MfnOrder> amazonOrder2=orderMap.get("2");
			if(amazonOrder2!=null&&amazonOrder2.size()>0){
				amazonUnlineOrderService.updateTrackNumber(amazonOrder2);
			}
		}catch(Exception e){
			LOGGER.info("error",e);
	    }
		
		return "0";
	}
	
	public String toJson(List<MfnOrder> mfnOrderList) {
		String rsStr="[";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String date = "";
		String temp= "";
		for (MfnOrder order : mfnOrderList) {
			if(order.getTrackingDate()!=null){
				try{
			    	date= sdf.format(order.getTrackingDate());
			    }catch(Exception e){
			    	date = sdf1.format(order.getTrackingDate());
			    }
			}else{
				date = sdf.format(new Date());
			}
			temp +="{\"supplier\":\""+order.getSupplier()+"\",\"tracking\":\""+order.getTrackNumber()+"\",\"orderId\":\""+order.getOrderId()+"\",\"trackingDate\":\""+date+"\"},";
		}
		
		temp = temp.substring(0,temp.length()-1);
		rsStr+=temp+"]";
		return rsStr;
	}
	
	
	
	@RequestMapping(value = "exportTrackingFeeFile")
	public String exportTrackingFeeFile(MfnOrder mfnOrder,HttpServletRequest request, HttpServletResponse response){
		List<Object[]> list = mfnOrderService.findOrderNoFee(mfnOrder);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		List<String> title = Lists.newArrayList("OrderId","Supplier","TrackingNumber","Fee");
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		 HSSFCell cell = null;		
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int index=1;
		for (Object[] obj: list) { 
			int j=0;
			row=sheet.createRow(index++);
			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(obj[0].toString());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(obj[1].toString());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(obj[2].toString());
			row.getCell(j-1).setCellStyle(contentStyle);
			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
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
			String fileName = "fee_" + sdf.format(new Date()) + ".xls";
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
	
	
	
	@RequestMapping(value = "updateFee")
	public String updateFee(@RequestParam("excel")MultipartFile excelFile,String country,RedirectAttributes redirectAttributes){
			try {
				try {
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/TrackNumberFee/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
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
				
				Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				Map<String,Float> feeMap = Maps.newHashMap();
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null||StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
						continue;
					}
				    String fee = getData(row.getCell(3));
					if(StringUtils.isNotBlank(fee)){
					    String orderId=StringUtils.trim(row.getCell(0).getStringCellValue());
					    feeMap.put(orderId, Float.parseFloat(fee));
					}
				}
				if(feeMap!=null&&feeMap.size()>0){
					mfnOrderService.updateFee(feeMap);
				}
				addMessage(redirectAttributes,"Successful");
			} catch (Exception e) {
				addMessage(redirectAttributes,"Failed to upload fee");
				LOGGER.info("更新track number异常",e);
			}
			
			return "redirect:"+Global.getAdminPath()+"/amazonAndEbay/mfnOrder/?repage";
	}
	
	
	@RequestMapping(value="downloadZipFile")
	public String downloadZipFile(String packageId,HttpServletResponse response) throws Exception {  
		    MfnPackage mfnPackage=mfnPackageService.get(Integer.parseInt(packageId));
		
		    String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir()+"dpdTracking";
			List<File> files = new ArrayList<File>();

			for (MfnOrder order : mfnPackage.getOrders()) {
                if (StringUtils.isNotBlank(order.getTrackNumber())) {
                    String fileName= order.getGroupBillNo()+"-"+order.getTrackNumber()+".pdf";
                    String tempPath=baseDirStr+"/"+fileName;
                    files.add(new File(tempPath));
				}
			}
      
			if(files!=null&&files.size()>0){
				String fileName=baseDirStr+"/"+mfnPackage.getPackageNo();
		    	File zipFile = new File (fileName+".zip");

		    	if(!zipFile.exists()){
		    	    //先得到文件的上级目录，并创建上级目录，在创建文件
		    		zipFile.getParentFile().mkdir();
		    	    try {
		    	        //创建文件
		    	    	zipFile.createNewFile();
		    	    } catch (IOException e) {
		    	        e.printStackTrace();
		    	    }
		    	}
		    	FileOutputStream fous = new FileOutputStream(zipFile); 
		    	ZipOutputStream zipOut = new ZipOutputStream(fous);
		    	ZipUtil.zipFile(files, zipOut);
		    	zipOut.close();
		    	fous.close();
		    	downloadZip(zipFile,response);
			}
		
		return null;
	}
	
	
	private static HttpServletResponse downloadZip(File file,HttpServletResponse response) {
        try {
        // 以流的形式下载文件。
        InputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        // 清空response
        response.reset();

        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/octet-stream");

       //如果输出的是中文名的文件，在此处就要用URLEncoder.encode方法进行处理
        response.setHeader("Content-Disposition", "attachment;filename=" +URLEncoder.encode(file.getName(), "UTF-8"));
        toClient.write(buffer);
        toClient.flush();
        toClient.close();
        } catch (IOException ex) {
        ex.printStackTrace();
        }finally{
             try {
                    File f = new File(file.getPath());
                    f.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return response;
    }
	
	
}
