/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.config.LogisticsSupplier;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.excel.ExportTransportExcel;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.VendorShipment;
import com.springrain.erp.modules.amazoninfo.service.order.MfnOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.VendorShipmentService;
import com.springrain.erp.modules.psi.entity.FbaInbound;
import com.springrain.erp.modules.psi.entity.FbaInboundItem;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiInventoryOut;
import com.springrain.erp.modules.psi.entity.PsiInventoryOutItem;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderItem;
import com.springrain.erp.modules.psi.service.FbaInboundService;
import com.springrain.erp.modules.psi.service.PsiInventoryOutService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 出库管理Controller
 * @author Michael
 * @version 2015-01-05
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiInventoryOut")
public class PsiInventoryOutController extends BaseController {
	@Autowired
	private PsiInventoryOutService 		psiInventoryOutService;
	@Autowired
	private SystemService 				userService;
	@Autowired
	private StockService 				stockService;
	@Autowired
	private FbaInboundService 			fbaInBoundService;
	@Autowired
	private PsiTransportOrderService 	psiTransportOrderService;
	@Autowired
	private LcPsiTransportOrderService 	lcTransportOrderService;
	@Autowired
	private PsiInventoryService 		psiInventoryService;
	@Autowired
	private PsiProductService 			productService;
	@Autowired
	private MfnOrderService 			mfnOrderService;
	@Autowired
	private VendorShipmentService	 	vendorShipmentService;
	@Autowired
	private MailManager 				mailManager;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PsiInventoryOutController.class);
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = {"list", ""})
	public String list(PsiInventoryOut psiInventoryOut, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (psiInventoryOut.getAddDate()== null) {
			psiInventoryOut.setAddDate(DateUtils.addMonths(today, -1));
			psiInventoryOut.setAddDateS(today);
		}
		Page<PsiInventoryOut> page = new Page<PsiInventoryOut>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("addDate desc");
		}else{
			page.setOrderBy(orderBy+",addDate desc");
		}	
        page = psiInventoryOutService.find(page, psiInventoryOut); 
        
        
        
        //添加运输方式一栏   如果是运单入库，就查出运输模式，如果是fba运输，就查出是哪个供应商运输的
        Set<String>  tranNos = Sets.newHashSet();
        Map<Integer,String> fbaTranModel = Maps.newHashMap();
        Map<Integer,String> localTranModel = Maps.newHashMap();
        for(PsiInventoryOut out:page.getList()){
        	if("Transport Delivery".equals(out.getOperationType())){
        		try{
        			tranNos.add(out.getTranLocalNo());
        			localTranModel.put(out.getId(), out.getTranLocalNo());
        		}catch(Exception ex){}
        	}else if("FBA Delivery".equals(out.getOperationType())){
        		if("DHL".equals(out.getSupplier())){
        			fbaTranModel.put(out.getId(), "LTL");
        		}else if("DPD".equals(out.getSupplier())){
        			fbaTranModel.put(out.getId(), "Parcel");
        		}
        		
        	}
        }
        
       Map<String,String>  modelMap= Maps.newHashMap();
       if(tranNos.size()>0){
    	   modelMap=  this.psiTransportOrderService.getTranModel(tranNos);
       }
       
        if(localTranModel.size()>0){
        	for(Map.Entry<Integer, String> entry:localTranModel.entrySet()){
        		Integer outId = entry.getKey();
         	   String tranNo = entry.getValue();;
         	   localTranModel.put(outId, modelMap.get(tranNo)) ;
            } 
        }
       
       model.addAttribute("localTranModel", localTranModel);
       model.addAttribute("fbaTranModel", fbaTranModel);
        
        
        //查询用户和本地仓库
        List<User> allUser = userService.findAllUsers();
        List<Stock> stocks =stockService.findStocks("0");
    	
        model.addAttribute("stocks", stocks);
  		model.addAttribute("allUser", allUser);
        model.addAttribute("page", page);
		return "modules/psi/psiInventoryOutList";
	}
	
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "add")
	public String add(PsiInventoryOut psiInventoryOut, Model model) throws UnsupportedEncodingException {
		Integer   warehouseId=psiInventoryOut.getWarehouseId();
		String   warehouseName=psiInventoryOut.getWarehouseName();
		psiInventoryOut.setWarehouseName(URLDecoder.decode(warehouseName,"utf-8"));
		List<Stock> stocks =stockService.findStocks("0");
		List<Stock> viewStocks = stocks;
		String stockName="仓库名称错误!";
		//根据用户权限获得仓库可选择信息
		List<Stock> tempStocks = Lists.newArrayList();
		//首次进来
		Set<String> permissionsSet = Sets.newHashSet();
		//查询权限
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			for(Role role:UserUtils.getUser().getRoleList()){
				permissionsSet.addAll(role.getPermissions());
			}
			for(Stock stock:stocks){
				String countryCode=stock.getCountrycode();
				if(permissionsSet.contains("psi:inventory:edit:"+countryCode+"")){
					tempStocks.add(stock);
				}
//				if("CN,US,DE,JP,".contains(countryCode+",")){
//					viewStocks.add(stock);
//				}
				if(stock.getId().equals(warehouseId)){
					stockName=countryCode;
				}
			}
			stocks=tempStocks;
		}else{
			for(Stock stock:stocks){
				String countryCode=stock.getCountrycode();
				tempStocks.add(stock);
//				if("CN,US,DE,JP,".contains(countryCode+",")){
//					viewStocks.add(stock);
//				}
				if(stock.getId().equals(warehouseId)){
					stockName=countryCode;
				}
			}
			stocks=tempStocks;
		}  

		List<String> operationTypes = Lists.newArrayList();
		
		operationTypes.add("Inventory Taking Delivery");
		operationTypes.add("mmf-line Delivery");       //自发货出库
		operationTypes.add("FBA Delivery");
		operationTypes.add("Transport Delivery");
		operationTypes.add("Lot Delivery");
		operationTypes.add("Replacement/Testing Delivery");
		operationTypes.add("Wholesale Delivery");
		operationTypes.add("Manual Operation");
		
		List<String> qualityTypes= Lists.newArrayList();
		qualityTypes.add("new");
		qualityTypes.add("old");
		qualityTypes.add("broken");  
		qualityTypes.add("renew");
		qualityTypes.add("spares");
		qualityTypes.add("offline");
		

		
		Map<String,List<Object[]>> fbaMap=Maps.newHashMap();
		Map<String,List<Object[]>> tranMap=Maps.newHashMap();
		
		
//		Set<String>   shipmentSet = Sets.newHashSet();
		
			//如果出库理诚的运单
			List<LcPsiTransportOrder> tranList=lcTransportOrderService.findTranOrderBySta(psiInventoryOut.getWarehouseId(),"0");
			for(LcPsiTransportOrder order:tranList){
				if("1".equals(order.getTransportType())){
					continue;
				}
				String key ="";
				if(StringUtils.isNotEmpty(order.getToCountry())){
					key=order.getToCountry();
				}else{
					key="wholesaleAddress";
				}
				
				Object[] object={order.getId(),order.getTransportNo(),order.getLadingBillNo()};
				List<Object[]> list=Lists.newArrayList();
				if(tranMap.get(key)!=null){
				 list=tranMap.get(key);
				}
				
				list.add(object);
				tranMap.put(key, list);
			}
	
		
		
			List<PsiTransportOrder> tranList1=psiTransportOrderService.findTranOrderBySta(psiInventoryOut.getWarehouseId(),"0");
			for(PsiTransportOrder order:tranList1){
				if("1".equals(order.getTransportType())){
					continue;
				}
				String key ="";
				if(StringUtils.isNotEmpty(order.getToCountry())){
					key=order.getToCountry();
				}else{
					key="wholesaleAddress";
				}
				 
				Object[] object={order.getId(),order.getTransportNo(),order.getLadingBillNo()};
				List<Object[]> list=Lists.newArrayList();
				if(tranMap.get(key)!=null){
				 list=tranMap.get(key);
				}
				
				list.add(object);
				tranMap.put(key, list);
			}
		
		
		//查询所有fba贴：WORKING
		List<FbaInbound> fbaList=fbaInBoundService.findFbaNoCancel(null,new String[]{"CANCELLED","DELETED","ERROR"},stockName,true,null);
		List<FbaInbound> newFbaList = Lists.newArrayList();
		for(FbaInbound bound:fbaList){
			FbaInbound fba = new FbaInbound();
			fba.setShipmentId(bound.getShipmentId());
			fba.setShipmentName(bound.getShipmentName());
			fba.setCountry(bound.getCountry());
			fba.setId(bound.getId());
			fba.setShipFromAddress(bound.getShipFromAddress());
			newFbaList.add(fba);
		}
		
		Set<String> delShippmentIds = Sets.newHashSet();
		Map<String,String> replaceMap= Maps.newHashMap();
		//查询这些fba贴里面，哪些是匹配了多个运单的
		List<String> mulIds=null;
		if(warehouseId.intValue()==130){
			mulIds=this.lcTransportOrderService.getMutiShippmentIds();
		}else{
			mulIds=this.psiTransportOrderService.getMutiShippmentIds();
		}
		
		
		if(mulIds!=null&&mulIds.size()>0){
			for(String mulId:mulIds){
				String arr[]=mulId.split(",");
				//除第一个的其余的都删除
				for(int i =0;i<arr.length;i++){
					if(i==0){
						replaceMap.put(arr[i], mulId);
					}else{
						delShippmentIds.add(arr[i]);
					}
				}
			}
		}
		
		for (Iterator<FbaInbound> iterator = newFbaList.iterator(); iterator.hasNext();) {
			FbaInbound fba = (FbaInbound) iterator.next();
			if(delShippmentIds.contains(fba.getShipmentId())){
				iterator.remove();
			}else{
				if(replaceMap.containsKey(fba.getShipmentId())){
					fba.setShipmentId(replaceMap.get(fba.getShipmentId()));
				}
			}
			
		}
		
		
		for(FbaInbound fbaInbound: newFbaList){
			String key = fbaInbound.getCountry();
			List<Object[]> list=null;
			if(fbaMap.get(key)==null){
				list=Lists.newArrayList();
			}else{
				list=fbaMap.get(key);
			}
			Object[] object={fbaInbound.getShipmentId(),fbaInbound.getShipmentName(),fbaInbound.getId(),fbaInbound.getShippingAddress()};
			list.add(object);
			fbaMap.put(key, list);
		}
		
		
		
		List<PsiInventory> inventorys=psiInventoryService.findByStock(warehouseId);
		
		Map<String,Integer>  packQuantityMap = Maps.newHashMap();
		//出库时   key:sku  库存数
		Map<String,Integer> inventorySkuMap=Maps.newHashMap();
		Map<String,String>  fnSkuMap = this.productService.getAllBandingProductSku();
		Map<Integer,Integer> allPackQuantity = this.productService.findAllPackQuantity();
		
		for(PsiInventory inventory:inventorys){
			//根据产品id查出 packQuantity ；
			packQuantityMap.put(inventory.getSku(), allPackQuantity.get(inventory.getProductId()));
		}
		List<String> noSkus = this.psiInventoryService.getNoSkus(warehouseId);   
		model.addAttribute("noSkus", JSON.toJSON(noSkus));
		model.addAttribute("tranMap", JSON.toJSON(tranMap));
		model.addAttribute("fbaMap", JSON.toJSON(fbaMap));
		
		model.addAttribute("fnSkuMap", JSON.toJSON(fnSkuMap));
		model.addAttribute("inventorySkuMap", JSON.toJSON(inventorySkuMap));
		model.addAttribute("packQuantityMap", JSON.toJSON(packQuantityMap));
		
		model.addAttribute("warehouseName", psiInventoryOut.getWarehouseName());
		model.addAttribute("stockName", stockName);
		model.addAttribute("qualityTypes", qualityTypes);
		model.addAttribute("viewStocks", viewStocks);
		model.addAttribute("stocks", stocks);
		model.addAttribute("operationTypes", operationTypes);
		model.addAttribute("inventorys",inventorys);
		model.addAttribute("psiInventoryOut", psiInventoryOut);
		model.addAttribute("logisticsSupplier",LogisticsSupplier.getLogisticsSupplierByType("4"));
		model.addAttribute("skuFnskuMap",productService.getSkuAndFnskuMap(null));
		return "modules/psi/psiInventoryOutAdd";
	}

	
	@RequestMapping(value = "validQuantity")
	@ResponseBody
	public String validQuantity(MultipartFile excelFile,PsiInventoryOut psiInventoryOut) throws Exception {
		Map<String,Integer>  actualQuantity=psiInventoryOutService.getLotDeliveryQuantity(excelFile);
		MfnOrder mfnOrder=new MfnOrder();
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		mfnOrder.setBuyTime(today);
		mfnOrder.setLastModifiedTime(today);
		if(psiInventoryOut.getWarehouseId()==19){
			mfnOrder.setCountry("de");
		}else if(psiInventoryOut.getWarehouseId()==120){
			mfnOrder.setCountry("com");
		}else{
			mfnOrder.setCountry("de");
		}
		StringBuilder returnInfo=new StringBuilder();
		Map<String,Integer> shouldQuantity=mfnOrderService.getCurrentDateTotal(mfnOrder);
		for (Map.Entry<String,Integer>entry : shouldQuantity.entrySet()) {
			String sku = entry.getKey();
		   if(shouldQuantity.get(sku)!=actualQuantity.get(sku))	{
			   returnInfo.append(sku).append("上传文件数量:").append(actualQuantity.get(sku)).append(",应发数量").append(shouldQuantity.get(sku)).append(";");
		   }
		}
		
		return returnInfo.toString();
		//return "1";
	}
	
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "addSave")
	public String addSave(MultipartFile memoFile,MultipartFile excelFile,MultipartFile pdfOutboundFile,PsiInventoryOut psiInventoryOut,RedirectAttributes redirectAttributes) throws Exception {
		if(!"Lot Delivery".equals(psiInventoryOut.getOperationType())){
			if(psiInventoryOut.getItems()==null||psiInventoryOut.getItems().size()==0){
				return null;
			}
		}
		
		String res=psiInventoryOutService.addSave(memoFile,excelFile, pdfOutboundFile, psiInventoryOut);
		if(StringUtils.isNotEmpty(res)){
			addMessage(redirectAttributes, res);
		}else{
			Stock stock = this.stockService.get(psiInventoryOut.getWarehouseId());
			if("FBA Delivery".equals(psiInventoryOut.getOperationType())){
				//如果是运输出库就发邮件
				this.sendEmailToSales(psiInventoryOut,stock);
			}
			//如果是中国仓，并且是fba或者运单出库，通知物流人员
			String warehoueCode=stock.getCountrycode();
			if("CN".equals(warehoueCode)&&("FBA Delivery".equals(psiInventoryOut.getOperationType())||"Transport Delivery".equals(psiInventoryOut.getOperationType()))){
				String subject="运单号："+psiInventoryOut.getTranLocalNo();
				if("FBA Delivery".equals(psiInventoryOut.getOperationType())){
					subject+="Fba贴："+psiInventoryOut.getTranFbaNo();
				}
				subject+="已出库";
				StringBuffer sb=new StringBuffer("Hi,All<br/><br/>"+subject+"详情如下：<br/>");
				sb.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
				sb.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#f2f4f6;color:#666;'>");
				sb.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>产品名称</th>");
				sb.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>Sku</th>");
				sb.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>数量</th>");
				sb.append("</tr>");
				for(PsiInventoryOutItem item:psiInventoryOut.getItems()){
					sb.append("<tr style='background-repeat:repeat-x;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					sb.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getProductNameColor()+"</td>");
					sb.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getSku()+"</td>");
					sb.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getQuantity()+"</td>");
					sb.append("</tr>");
				}
				
				sendNoticeEmail("supply-chain@inateck.com,"+UserUtils.logistics1+","+UserUtils.logistics2,sb.toString(), "出库通知,"+subject, "", "");
			}
			
			addMessage(redirectAttributes, "Save outBound:'" +psiInventoryOut.getBillNo()+ "'success");
		}  
		return "redirect:"+Global.getAdminPath()+"/psi/psiInventory/list?warehouse.id="+psiInventoryOut.getWarehouseId();
	}
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "view")
	public String view(PsiInventoryOut psiInventoryOut, Model model) {
		if(psiInventoryOut.getId()!=null){
			psiInventoryOut=this.psiInventoryOutService.get(psiInventoryOut.getId());
		}else if(StringUtils.isNotEmpty(psiInventoryOut.getBillNo())){
			psiInventoryOut=this.psiInventoryOutService.get(psiInventoryOut.getBillNo());
		}else{
			return null;
		}
		model.addAttribute("psiInventoryOut", psiInventoryOut);
		return "modules/psi/psiInventoryOutView";
	}
	
	@RequestMapping(value = "compareDateFile")
	public String compareDateFile(PsiInventoryOut psiInventoryOut, Model model, HttpServletRequest request, HttpServletResponse response) {
		psiInventoryOut=this.psiInventoryOutService.get(psiInventoryOut.getId());
		MfnOrder mfnOrder=new MfnOrder();
		VendorShipment vendorShipment=new VendorShipment();
		if(psiInventoryOut.getWarehouseId()==19){
			mfnOrder.setCountry("de");
			vendorShipment.setCountry("de");
		}else if(psiInventoryOut.getWarehouseId()==120){
			mfnOrder.setCountry("com");
			vendorShipment.setCountry("com");
		}else{
			mfnOrder.setCountry("de");
			vendorShipment.setCountry("de");
		}
		Date start=new Date(psiInventoryOutService.getMaxDate(psiInventoryOut.getAddDate()).getTime());
		mfnOrder.setBuyTime(start);
		mfnOrder.setLastModifiedTime(psiInventoryOut.getAddDate());
		
		vendorShipment.setShipDate(start);
		vendorShipment.setDeliveryDate(psiInventoryOut.getAddDate());
		//Map<String,Integer> shouldQuantity=mfnOrderService.getTotalByProductName2(mfnOrder);
		Map<String,Integer> shouldQuantity=Maps.newHashMap();
		Map<String,Integer> vendorInfo=vendorShipmentService.getTotalByProductName(vendorShipment);
		Map<String,Integer> mfnInfo=mfnOrderService.getTotalByProductName2(mfnOrder);
		if(vendorInfo!=null&&vendorInfo.size()>0){
			for (Map.Entry<String,Integer> entry: vendorInfo.entrySet()) {
				String productName = entry.getKey();
				Integer mfnQuantity=mfnInfo.get(productName);
				Integer vendorQuantity=entry.getValue();
				shouldQuantity.put(productName, vendorQuantity+(mfnQuantity==null?0:mfnQuantity));
			}
			if(mfnInfo!=null&&mfnInfo.size()>0){
				for (Map.Entry<String,Integer> entry: mfnInfo.entrySet()) {
					String productName = entry.getKey();
					 if(!vendorInfo.keySet().contains(productName)){
						 shouldQuantity.put(productName,entry.getValue());
					 }
				}
			}
		}else{
			if(mfnInfo!=null&&mfnInfo.size()>0){
				for (Map.Entry<String, Integer> entry: mfnInfo.entrySet()) {
					String productName = entry.getKey();
					shouldQuantity.put(productName,entry.getValue());
				}
			}
			
		}
		
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
		 
		  CellStyle contentStyle1 = wb.createCellStyle();
		  contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		  contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		  contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		  contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		  contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		  contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		  contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		  contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		  contentStyle1.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		  contentStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		HSSFCell cell = null;	
		List<String>  title=Lists.newArrayList("product name","should out bound","actual out bound");
		  for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
		  }

		  int index=1;
		  List<PsiInventoryOutItem> items=psiInventoryOut.getItems();
		  
		  Map<String,Integer> actualQuantity=Maps.newHashMap();
		  for (PsiInventoryOutItem item : items) {
			  String name=item.getProductName()+(StringUtils.isNotBlank(item.getColorCode())?("_"+item.getColorCode()):"");
			  Integer quantity=actualQuantity.get(name);
			  if(quantity==null){
				  actualQuantity.put(name, item.getQuantity());
			  }else{
				  actualQuantity.remove(name);
				  actualQuantity.put(name,quantity+item.getQuantity());
			  }
		  }
		  
		  if(shouldQuantity!=null&&shouldQuantity.size()>0){
			  for(Map.Entry<String,Integer>entry:actualQuantity.entrySet()){
					String name = entry.getKey();
				  //String name=item.getProductName()+(StringUtils.isNotBlank(item.getColorCode())?("_"+item.getColorCode()):"");
				  int j=0;
				  row=sheet.createRow(index++);
				  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
				  Integer quantity=(shouldQuantity.get(name)==null?0:shouldQuantity.get(name));
				  row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(quantity);
				  row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(entry.getValue());
				  if(quantity.intValue()!=entry.getValue().intValue()){
					 row .getCell(j-1).setCellStyle(contentStyle1);
					 row .getCell(j-2).setCellStyle(contentStyle1);
					 row .getCell(j-3).setCellStyle(contentStyle1);
				  }else{
					 row .getCell(j-1).setCellStyle(contentStyle);
					 row .getCell(j-2).setCellStyle(contentStyle);
					 row .getCell(j-3).setCellStyle(contentStyle);
				  }
			  }
			  for(Map.Entry<String,Integer>entry:shouldQuantity.entrySet()){
					String productName = entry.getKey();
				 if(!actualQuantity.keySet().contains(productName)){
					 int j=0;
					 row=sheet.createRow(index++);
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(productName);
					 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(entry.getValue()==null?0:entry.getValue());
					 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
					 row .getCell(j-1).setCellStyle(contentStyle1);
					 row .getCell(j-2).setCellStyle(contentStyle1);
					 row .getCell(j-3).setCellStyle(contentStyle1);
				 }
			 }
			  
		  }else{
			  for (PsiInventoryOutItem item : items) {
				  int j=0;
				  row=sheet.createRow(index++);
				  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getProductName()+(StringUtils.isNotBlank(item.getColorCode())?("_"+item.getColorCode()):""));
				  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("应发自发货为空");
				  row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getQuantity());
				  row .getCell(j-1).setCellStyle(contentStyle);
				  row .getCell(j-2).setCellStyle(contentStyle);
				  row .getCell(j-3).setCellStyle(contentStyle);
			  } 
		  }
		  
		 try {
			    request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
				String fileName = new SimpleDateFormat("yyyy-MM-dd").format(psiInventoryOut.getAddDate())+"库存比对结果" + sdf.format(new Date()) + ".xls";
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
	
	@RequestMapping(value = "downloadPdf")
	public String downloadPdf(PsiInventoryOut psiInventoryOut,Model model,RedirectAttributes redirectAttributes,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, ParseException{
		psiInventoryOut = psiInventoryOutService.get(psiInventoryOut.getId());
		String fileName = psiInventoryOut.getPdfFile();
		fileName = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + fileName;
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(fileName);
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[4096];
			int len = 0;
			while ((len = inStream.read(buff, 0, 4096)) > 0) {
				swapStream.write(buff, 0, len);
				swapStream.flush();
			}
	        byte[] in2b = swapStream.toByteArray();
	        swapStream.close();
	        if (StringUtils.isNotEmpty(psiInventoryOut.getTranFbaNo())) {
	        	fileName = psiInventoryOut.getTranFbaNo() + ".pdf";
			} else {
				fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
			}
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			out.write(in2b);
			out.close();
		} catch (Exception e) {
			logger.error("下载Fba帖凭证文件异常", e);
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					logger.warn("关闭流异常", e);
				}
			}
		}
		return null;
	}
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "track")
	public String track(String billNo) {
		PsiInventoryOut out=this.psiInventoryOutService.get(billNo);
		return "redirect:"+Global.getAdminPath()+"/psi/fbaInbound?shipmentId="+out.getTranFbaNo()+"&country="+out.getWhereabouts();
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"ajaxFbaData"})
	public String ajaxFbaData(String shippmentId) {
		Map<String,Integer> skuMap = Maps.newHashMap();
		Map<String,Integer> packMap =Maps.newHashMap();
		StringBuilder itemStr=new StringBuilder();
		//遍历多个帖子，组成skumap
		for(String shipId:shippmentId.split(",")){
			final FbaInbound fba=this.fbaInBoundService.getByShipmentId(shipId);
			//LOGGER.info(shipId+"==="+fba.getAccountName());
			//fbaInBoundService.sync(fba);
			for(FbaInboundItem item:fba.getItems()){
				String  sku = item.getSku();
				Integer quantity = item.getQuantityShipped();
				if(skuMap.get(sku)!=null){
					quantity+=skuMap.get(sku);
				}
				skuMap.put(sku, quantity);
				packMap.put(sku,(item.getPackQuantity()==null?0:item.getPackQuantity()));
			}
		}
		
		Map<String,String> volWeiMap = this.productService.getVolumeWeightBySku(skuMap.keySet());
		for(Map.Entry<String,Integer>entry:skuMap.entrySet()){
			String sku = entry.getKey();
			String volumeWeight = volWeiMap.get(sku);
			if(StringUtils.isNotEmpty(volumeWeight)){
				itemStr.append("{\"sku\":\"").append(sku).append("\",\"pack\":\"").append(packMap.get(sku)).append("\",\"quantity\":\"").append(entry.getValue()).append("\",\"volume\":\"").append(
						Float.parseFloat(volumeWeight.split(",")[0])).append("\",\"weight\":\"").append(Float.parseFloat(volumeWeight.split(",")[1])).append("\"},");
			}else{
				itemStr.append("{\"sku\":\"").append(sku).append("\",\"pack\":\"").append(packMap.get(sku)).append("\",\"quantity\":\"").append(entry.getValue()).append("\",\"volume\":\"").append(0).append("\",\"weight\":\"").append(0).append("\"},");
			}
			
		}
		
		if(itemStr.length()>0){
			itemStr=new StringBuilder(itemStr.substring(0, itemStr.length()-1));
		}
		
		String rs="{\"msg\":\"true\",\"items\":["+itemStr+"]}";
		return rs;
	}

	@ResponseBody
	@RequestMapping(value = {"ajaxTranData"})
	public String ajaxTranData(Integer tranId,String isLiCheng) {
		String rs="";
		if("1".equals(isLiCheng)){
			LcPsiTransportOrder tranOrder=this.lcTransportOrderService.get(tranId);
			rs="{\"msg\":\"true\","+tranOrder.toJson()+"}";
		}else{
			PsiTransportOrder tranOrder=this.psiTransportOrderService.get(tranId);
			if(tranOrder==null){
				LcPsiTransportOrder tranOrder1=this.lcTransportOrderService.get(tranId);
				rs="{\"msg\":\"true\","+tranOrder1.toJson()+"}";
			}else{
				rs="{\"msg\":\"true\","+tranOrder.toJson()+"}";
			}
			
		}
		return rs;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = {"updateRemark"})
	public String updateRemark(Integer outId,String content,String flag) throws UnsupportedEncodingException {
		return this.psiInventoryOutService.updateRemark(outId, URLDecoder.decode(content,"UTF-8"),flag);
	}
	
	@RequestMapping(value = "goUpdatePdf")
	public String goUpdatePdf(PsiInventoryOut psiInventoryOut, Model model) {
		if (psiInventoryOut.getId() != null) {
			psiInventoryOut = this.psiInventoryOutService.get(psiInventoryOut.getId());
		} else {
			return null;
		}
		model.addAttribute("psiInventoryOut", psiInventoryOut);
		return "modules/psi/psiInventoryOutUpdate";
	}
	
	//更新出库单扫描件PDF
	@RequestMapping(value = "updatePdf")
	public String updatePdf(PsiInventoryOut psiInventoryOut,MultipartFile pdfOutboundFile, Model model, RedirectAttributes redirectAttributes) {
		String message = "update success!";
		if (psiInventoryOut.getId() != null) {
			psiInventoryOut = this.psiInventoryOutService.get(psiInventoryOut.getId());
		} else {
			message = "update failed, id is required";
			addMessage(redirectAttributes, message);
			return "redirect:"+Global.getAdminPath()+"/psi/psiInventoryOut/?repage";
		}
		if (pdfOutboundFile != null && pdfOutboundFile.getSize() != 0) {
			String inNo = psiInventoryOut.getBillNo();
			if (StringUtils.isEmpty(inNo)) {
				SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmsss");
				String flowNo=sdf.format(new Date());
				inNo = flowNo.substring(0, 8) + "_CKD" + flowNo.substring(8);
			}
			String filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/psiInventoryOut";
			File baseDir = new File(filePath + "/" + inNo);
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String suffix = pdfOutboundFile.getOriginalFilename().substring(pdfOutboundFile.getOriginalFilename().lastIndexOf("."));
			String name = UUID.randomUUID().toString() + suffix;
			File dest = new File(baseDir, name);
			try {
				FileUtils.copyInputStreamToFile(pdfOutboundFile.getInputStream(), dest);
				psiInventoryOut.setPdfFile("/psi/psiInventoryOut/" + inNo	+ "/" + name);
				psiInventoryOutService.save(psiInventoryOut);
			} catch (IOException e) {
				message = "update failed, pdf File save fail";
				logger.error(name+",pdf File save fail, 编号为：" + psiInventoryOut.getId(), e);
			}
		} else {
			message = "update failed, pdf File is null";	
		}
		addMessage(redirectAttributes, message);
		return "redirect:"+Global.getAdminPath()+"/psi/psiInventoryOut/?repage";
	}
	
	
	private  void sendEmailToSales(PsiInventoryOut inventoryOut,Stock stock) throws Exception{
		String warehoueCode=stock.getCountrycode();
		if(!"CN".equals(warehoueCode)){
			Map<String, Object> prarms = Maps.newHashMap();
			prarms.put("inventoryOut",inventoryOut);
			String toAddress="";
			if("DE".equals(warehoueCode)){
				toAddress = "logistics.eu@inateck.com,fbamitteilung@inateck.com";
			}else{
				toAddress = "logistics.usa@inateck.com";
			}
			FbaInbound fbaInbound = this.fbaInBoundService.getByShipmentId(inventoryOut.getTranFbaNo());
			Map<String,Integer> skuMap = Maps.newHashMap();
			
			Stock  fbaStock= this.stockService.findBySign(fbaInbound.getDestinationFulfillmentCenterId());
			String fbaName=fbaInbound.getShipmentName();
			Integer shippedQuantity =fbaInbound.getQuantityShipped();
			String address=fbaStock.getAddress();
			
			prarms.put("shippedQuantity",shippedQuantity);
			prarms.put("fbaName",fbaName);
			prarms.put("address",address);
			
			for(FbaInboundItem item:fbaInbound.getItems()){
				skuMap.put(item.getSku(), item.getQuantityShipped());
			}
			
			prarms.put("skuMap",skuMap);
			
			String content = PdfUtil.getPsiTemplate("warehouseOutEmail.ftl",prarms);
			if(StringUtils.isNotBlank(content)){
				String subject="FBA Out-bound："+inventoryOut.getTranFbaNo()+",By "+UserUtils.getUser().getName()+" Operation ";
				sendNoticeEmail(toAddress+","+UserUtils.logistics1, content, subject, "", "");
//				final MailInfo mailInfo = new MailInfo(toAddress,"FBA Out-bound："+inventoryOut.getTranFbaNo()+",By "+UserUtils.getUser().getName()+" Operation "+DateUtils.getDate("-yyyy/M/dd"),date);
//				mailInfo.setContent(content);
//				new Thread(){
//					public void run(){
//						mailManager.send(mailInfo);
//					} 
//				}.start();
			}
		}
	}
	
	
	
	//sendemail
	public void sendNoticeEmail(String email,String content,String subject,String ccEmail,String bccEmail){
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			final MailInfo mailInfo = new MailInfo(email,subject+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			if(StringUtils.isNotEmpty(bccEmail)){
				mailInfo.setBccToAddress(bccEmail);
			}
			if(StringUtils.isNotEmpty(ccEmail)){
				mailInfo.setCcToAddress(ccEmail);
			}
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
	}
	
	//将运单信息导出为excel表格
	@RequestMapping(value="expOutBound")
	public String exportOutBoundBill(Integer id,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		PsiInventoryOut outBound = this.psiInventoryOutService.get(id);
		if(StringUtils.isNotEmpty(outBound.getTranLocalId())){
			Integer transportOrderId = Integer.parseInt(outBound.getTranLocalId());
			LcPsiTransportOrder realTranOrder = this.lcTransportOrderService.get(transportOrderId);
			LcPsiTransportOrder psiTransportOrder=this.lcTransportOrderService.getById(transportOrderId);
			ExportTransportExcel ete = new ExportTransportExcel();
			SimpleDateFormat sdfD = new SimpleDateFormat("yyyy-MM-dd");
			
			
			psiTransportOrder.setTranMan(outBound.getTranMan());
			psiTransportOrder.setPhone(outBound.getPhone());
			psiTransportOrder.setIdCard(outBound.getIdCard());
			psiTransportOrder.setCarNo(outBound.getCarNo());
			psiTransportOrder.setBoxNo(outBound.getBoxNo());
			psiTransportOrder.setFlowNo(outBound.getFlowNo());
			psiTransportOrder.setFormatDate(sdfD.format(outBound.getAddDate()));
			psiTransportOrder.setOrgin(realTranOrder.getOrgin());
			psiTransportOrder.setDestination(realTranOrder.getDestination());
			psiTransportOrder.setShipmentId(realTranOrder.getShipmentId());
			
			List<LcPsiTransportOrderItem> newItems = new ArrayList<LcPsiTransportOrderItem>();
			List<LcPsiTransportOrderItem> items1 = psiTransportOrder.getItems();
			Map<String, LcPsiTransportOrderItem> map = new HashMap<String, LcPsiTransportOrderItem>();
			for (LcPsiTransportOrderItem orderItem : items1) {//合并相同产品
				LcPsiTransportOrderItem transItem = map.get(orderItem.getProduct().getName()+"_"+orderItem.getPackQuantity());
				if (transItem != null) {//存在
					transItem.setQuantity(transItem.getQuantity()+ orderItem.getQuantity());
				} else {
					map.put(orderItem.getProduct().getName()+"_"+orderItem.getPackQuantity(), orderItem);
				}
			}
			List<LcPsiTransportOrderItem> mapValuesList = new ArrayList<LcPsiTransportOrderItem>(map.values());
			Collections.sort(mapValuesList);
			
			psiTransportOrder.setItems(mapValuesList);
			List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
			int count = 1;
			for (int i = 0; i < items.size(); i++) {
				LcPsiTransportOrderItem item = items.get(i);
				int num = item.getQuantity()/ item.getPackQuantity();//多少箱
				if (num > 0	&& item.getQuantity()% item.getPackQuantity() == 0) {//整箱
					if (count != (count + num - 1)) {
						item.setCartonNo(count + "-"+ (count + num - 1));
					} else {
						item.setCartonNo(count + "");
					}
					count = count + num;
					item.getProduct().setGw(item.getProduct().getGw().multiply(new BigDecimal(num)));
					item.getProduct().setWeight(item.getProduct().getGw().subtract(new BigDecimal(num)));
					item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
					
					newItems.add(item);
				} else if (num > 0	&& item.getQuantity()% item.getPackQuantity() != 0) {
					int mod = item.getQuantity()% item.getPackQuantity();
					//整箱部分
					if (count != (count + num - 1)) {
						item.setCartonNo(count + "-"+ (count + num - 1));
					} else {
						item.setCartonNo(count + "");
					}
					count = count + num;
					item.getProduct().setGw(item.getProduct().getGw().multiply(new BigDecimal(num)));
					item.getProduct().setWeight(item.getProduct().getGw().subtract(new BigDecimal(num)));
					item.setQuantity(item.getQuantity() - mod);
					item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
					newItems.add(item);
					//剩余部分
					LcPsiTransportOrderItem item1 = new LcPsiTransportOrderItem();
					item1.setProduct(new PsiProduct());
					item1.setCartonNo(count + "");
					item1.setProductName(item.getProductName());
					item1.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
					
					count = count + 1;
					item1.setQuantity(mod);
					item1.getProduct().setBrand(item.getProduct().getBrand());
					item1.getProduct().setModel(item.getProduct().getModel());
					item1.getProduct().setPackLength(new BigDecimal(0));
					item1.getProduct().setPackWidth(new BigDecimal(0));
					item1.getProduct().setPackHeight(new BigDecimal(0));
					item1.getProduct().setGw(new BigDecimal(0));
					item1.getProduct().setWeight(new BigDecimal(0));
					item1.getProduct().setMaterial(item.getProduct().getMaterial());
					item1.setPackQuantity(item.getPackQuantity());
					newItems.add(item1);
				} else if (num <= 0) {
					//不足一箱
					item.setCartonNo(count + "");
					count = count + 1;
					item.getProduct().setWeight(new BigDecimal(0));
					item.getProduct().setPackLength(new BigDecimal(0));
					item.getProduct().setPackWidth(new BigDecimal(0));
					item.getProduct().setPackHeight(new BigDecimal(0));
					item.getProduct().setGw(new BigDecimal(0));
					item.getProduct().setType(item.getProduct().getChineseName()==null?"":item.getProduct().getChineseName().replace("(个)", "").replace("(台)", "").replace("(套)", "").replace("(条)", ""));
					
					newItems.add(item);
				}
			}
			psiTransportOrder.setItems(newItems);
			Workbook workbook = null;
			String modelName = "OutBound";//模板文件名称
			String xmlName = "OutBound";
			
			workbook = ete.writeData(psiTransportOrder,	xmlName, modelName, 0);

			//下载excel文档
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = modelName + sdf.format(new Date())+ ".xlsx";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition","attachment;filename=" + fileName);
			try {
				OutputStream out = response.getOutputStream();
				workbook.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	
	/**
	 *导出理诚出库单数据
	 * 
	 */
	@RequiresPermissions("psi:order:financeReview")
	@RequestMapping(value ="payExport")
	public String payExport(PsiInventoryOut inventoryOut, HttpServletRequest request, HttpServletResponse response, Model model) {
		inventoryOut.setWarehouseId(130);
		List<PsiInventoryOut> list = this.psiInventoryOutService.find(inventoryOut); 
        List<String> title=Lists.newArrayList("出库单编号","报关单编号","品名","sku", "数量","单价","类型");
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
  		
  		
  		HSSFCellStyle style1 = wb.createCellStyle();
		HSSFDataFormat df1 = wb.createDataFormat();  //此处设置数据格式  
		style1.setDataFormat(df1.getFormat("#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));  
		 
  		
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
  		int  rowIndex=1;
  		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
  		if(list!=null){
  			Map<String,Float> avgPrice=this.psiInventoryService.getAvgPriceByWarehouseId(130);
  			List<String> transportNos = Lists.newArrayList();
  			for(PsiInventoryOut out:list){
  				if(StringUtils.isNotEmpty(out.getTranLocalNo())){
  					transportNos.add(out.getTranLocalNo());
  				}
  			}
  			Map<String,String> declareMap = Maps.newHashMap();
  			if(transportNos.size()>0){
  				declareMap=lcTransportOrderService.getTransportDeclareNo(transportNos);
  			}
  			
  			for(int i=0;i<list.size();i++){
  				PsiInventoryOut out=list.get(i);
				//"入库单编号","品名","国家", "数量","不含税单价","含税单价","不含税总计","应付","已付"
				for(PsiInventoryOutItem outItem:out.getItems()){
					String sku =outItem.getSku();
					row=sheet.createRow(rowIndex++);
					int j=0;
					Float price =0f;
					if(avgPrice.get(sku)!=null){
						price=avgPrice.get(sku);
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(out.getBillNo());
					String delareNo =""; 
					if(StringUtils.isNotEmpty(out.getTranLocalNo())&&declareMap.get(out.getTranLocalNo())!=null){
						delareNo=declareMap.get(out.getTranLocalNo());
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(delareNo);
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(outItem.getProductNameColor());
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(outItem.getSku());
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(outItem.getQuantity());
//    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(price);
    				cell=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(style1);
					cell.setCellValue(price);
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(out.getOperationType());
  				}
  			}
  		}
  		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "psiInventoryOut" + sdf.format(new Date()) + ".xls";
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
	
	
	
	/**
	 *导出理诚出库单数据
	 * 
	 */
	@RequiresPermissions("psi:order:financeReview")
	@RequestMapping(value ="payExportLc")
	public String payExportLc(PsiInventoryOut inventoryOut, HttpServletRequest request, HttpServletResponse response, Model model) {
		inventoryOut.setWarehouseId(130);
		List<PsiInventoryOut> list = this.psiInventoryOutService.find(inventoryOut); 
        List<String> title=Lists.newArrayList("品名","出库单编号","出库日期","报关单编号","报关出口日期", "数量","装箱数","箱数","出库成本金额","单位","产品描述");
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
  		
  		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
  		HSSFCellStyle style1 = wb.createCellStyle();
		HSSFDataFormat df1 = wb.createDataFormat();  //此处设置数据格式  
		style1.setDataFormat(df1.getFormat("#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));  
		 
  		
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
  		int  rowIndex=1;
  		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
  		if(list!=null){
  			List<String> transportNos = Lists.newArrayList();
  			for(PsiInventoryOut out:list){
  				if(StringUtils.isNotEmpty(out.getTranLocalNo())){
  					transportNos.add(out.getTranLocalNo());
  				}
  			}
  			Map<String,String> declareMap = Maps.newHashMap();
  			if(transportNos.size()>0){
  				declareMap=lcTransportOrderService.getTransportDeclareNo(transportNos);
  			}
  			Map<String,PsiProduct> productMap = this.productService.getProductMap();
  			for(int i=0;i<list.size();i++){
  				PsiInventoryOut out=list.get(i);
				//"入库单编号","品名","国家", "数量","不含税单价","含税单价","不含税总计","应付","已付"
  				//"品名","出库单编号","出库日期","报关单编号","报关出口日期", "数量","出库成本金额","单位","产品描述");
				Map<String,Integer> productQuantity = Maps.newHashMap();
  				Map<String,Float> productPrice = Maps.newHashMap();
				for(PsiInventoryOutItem outItem:out.getItems()){
					String productName =outItem.getProductName();
					if("spares".equals(outItem.getQualityType())){//备品不要
						continue;
					}
					Integer quantity = outItem.getQuantity();
					Float price = outItem.getAvgPrice();
					if(productQuantity.get(productName)!=null){
						quantity+=productQuantity.get(productName);
					}
					productQuantity.put(productName, quantity);
					
					if(price!=null&&productPrice.get(productName)==null){
						productPrice.put(productName, price);
					}
				}
				for(Map.Entry<String, Integer> entry :productQuantity.entrySet()){
      				int j =0;
      				row=sheet.createRow(rowIndex++);
					Float price =0f;
					if(productPrice.get(entry.getKey())!=null){
						price=productPrice.get(entry.getKey())/1.17f;  //不含税单价
					}
					String delareNo =""; 
					String delareDate="";
					if(StringUtils.isNotEmpty(out.getTranLocalNo())&&declareMap.get(out.getTranLocalNo())!=null){
						String arr[] =declareMap.get(out.getTranLocalNo()).split(",,");
						delareNo=arr[0].toString().trim();
						delareDate=arr[1].toString().trim();
					}
					PsiProduct product = productMap.get(entry.getKey());
					//"品名","出库单编号","出库日期","报关单编号","报关出口日期", "数量",pack,boxNum,"出库成本金额","单位","产品描述");
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(entry.getKey());
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(out.getBillNo());
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sdf1.format(out.getDataDate()));
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(delareNo);
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(delareDate);
    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(entry.getValue());
    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getPackQuantity());
    				int boxN=(int)(entry.getValue()/product.getPackQuantity());
    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getPackQuantity());
    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue((entry.getValue()%product.getPackQuantity()==0)?boxN:(boxN+1));
    				cell=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(style1);
					cell.setCellValue(price*entry.getValue());
					
					
    				
    				String chineseName = product.getChineseName()==null?"":product.getChineseName();
    				String unit ="";
    				if(chineseName.contains("(")){
    					unit=chineseName.substring(chineseName.indexOf("(")+1,chineseName.indexOf(")"));
    				}
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unit);
    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getDescription());
  				}
  			}
  		}
  		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "psiInventoryOut" + sdf.format(new Date()) + ".xls";
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
	
}
