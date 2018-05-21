package com.springrain.erp.modules.psi.web;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.Region;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.mapper.JsonMapper;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonCatalogRank;
import com.springrain.erp.modules.amazoninfo.entity.AmazonNewReleasesRank;
import com.springrain.erp.modules.amazoninfo.entity.AmazonOutOfProduct;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsDetail;
import com.springrain.erp.modules.amazoninfo.entity.OutOffStockDto;
import com.springrain.erp.modules.amazoninfo.entity.ProductPrice;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecastByMonth;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonOutOfProductService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonWarningLetterService;
import com.springrain.erp.modules.amazoninfo.service.BusinessReportService;
import com.springrain.erp.modules.amazoninfo.service.ProductHistoryPriceService;
import com.springrain.erp.modules.amazoninfo.service.ReturnGoodsService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportMonthTypeService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.SalesForecastServiceByMonth;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOutboundOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonRemovalOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonUnlineOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.MfnOrderService;
import com.springrain.erp.modules.psi.entity.ProductParts;
import com.springrain.erp.modules.psi.entity.ProductSalesInfo;
import com.springrain.erp.modules.psi.entity.PsiAttrDto;
import com.springrain.erp.modules.psi.entity.PsiBarcode;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiInventoryInnerDto;
import com.springrain.erp.modules.psi.entity.PsiInventoryRevisionLog;
import com.springrain.erp.modules.psi.entity.PsiInventoryTotalDto;
import com.springrain.erp.modules.psi.entity.PsiInventoryTotalDtoByInStock;
import com.springrain.erp.modules.psi.entity.PsiInventoryTurnoverData;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductAttribute;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderItem;
import com.springrain.erp.modules.psi.entity.PurchasePlanItem;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.service.ProductSalesInfoService;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiInventoryTurnoverDataService;
import com.springrain.erp.modules.psi.service.PsiOutOfStockInfoService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductInStockService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceLogService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.psi.service.PsiSkuChangeBillService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderService;
import com.springrain.erp.modules.psi.service.PsiTransportPaymentService;
import com.springrain.erp.modules.psi.service.PurchaseOrderService;
import com.springrain.erp.modules.psi.service.PurchasePlanService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 库存Controller
 * @author Michael
 * @version 2014-12-24
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiInventory")
public class PsiInventoryController extends BaseController {
	
	
	@Autowired
	private PsiInventoryService 			psiInventoryService;
	@Autowired
	private PsiProductService 				psiProductService;
	@Autowired
	private StockService 					stockService;
	@Autowired
	private ProductSalesInfoService 		productSalesInfoService;
	@Autowired
	private AmazonProduct2Service 			amazonProduct2Service;
	@Autowired
	private SaleReportService 				saleReportService;
	@Autowired
	private ReturnGoodsService 				returnGoodsService;
	@Autowired
	private ProductHistoryPriceService		productHistoryPriceService; 
	@Autowired
	private BusinessReportService 			businessReportService;
	@Autowired
	private SalesForecastServiceByMonth 	salesForecastService;
	@Autowired
	private AmazonPostsDetailService 		amazonPostsDetailService;
	@Autowired
	private PsiSkuChangeBillService         skuChangeBillService;
	@Autowired
	private MailManager						mailManager;
	@Autowired
	private PsiInventoryFbaService 			fbaService;
	@Autowired
	private PsiTransportOrderService 		psiTransportOrderService;
	@Autowired
	private PsiProductAttributeService 		psiProductAttributeService;
	@Autowired
	private AmazonOutOfProductService  		outOfService;
	@Autowired
	private PsiProductTieredPriceLogService  priceLogService;
	@Autowired
	private PsiProductTieredPriceService   	tieredService;
	@Autowired
	private PsiProductEliminateService 		psiProductEliminateService;
	@Autowired
	private PsiProductInStockService 		psiProductInStockService;
	@Autowired
	private PurchaseOrderService 			purchaseOrderService;
	@Autowired
	private PsiOutOfStockInfoService 		psiOutOfStockInfoService; 
	@Autowired
	private PsiProductGroupUserService 		groupUserService;
	@Autowired
	private SaleReportMonthTypeService 		saleReportMonthTypeService;
	@Autowired
	private MfnOrderService 				mfnOrderService;
	@Autowired
	private AmazonUnlineOrderService 		unlineOrderService;
	@Autowired
	private AmazonOutboundOrderService 		outboundOrderService;
	@Autowired
	private AmazonRemovalOrderService 		removalOrderService;
	@Autowired
	private PsiProductTypeGroupDictService 	groupDictService;
	@Autowired
	private PsiInventoryTurnoverDataService turnoverDataService;
    @Autowired
	private PurchasePlanService purchasePlanService;
    @Autowired
  	private PsiTransportPaymentService psiTransportPaymentService; 
    @Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	@Autowired
	private AmazonWarningLetterService warningLetterService;
	
	private DateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
	private DateFormat formatWeek = new SimpleDateFormat("yyyyww");
	private DateFormat formatMonth = new SimpleDateFormat("yyyyMM");
	public  static boolean fangChaFlag = false;
	
	private  Cache<String, Float> salePrice = CacheBuilder.newBuilder().expireAfterWrite(24*7L, TimeUnit.HOURS).build();
	private static Map<String, Float> cacheMap = Maps.newConcurrentMap();

	private  DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
	private  DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = {"list", ""})
	public String list(PsiInventory psiInventory, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		
		List<Stock> stocks= Lists.newArrayList();
		stocks=stockService.findStocks("0");
		if(psiInventory.getWarehouse()==null){
			//首次进来
			Set<String> permissionsSet = Sets.newHashSet();
			//查询权限
			User user = UserUtils.getUser();
			if(!user.isAdmin()){
				for(Role role:UserUtils.getUser().getRoleList()){
					permissionsSet.addAll(role.getPermissions());
				}
				String countryCode = "CN";
				if(UserUtils.hasPermission("psi:inventory:edit:DE")||UserUtils.hasPermission("psi:inventory:revise:DE")){
					countryCode="DE";
				}else if(UserUtils.hasPermission("psi:inventory:edit:CN")||UserUtils.hasPermission("psi:inventory:revise:CN")){
					countryCode="CN";
				}else if(UserUtils.hasPermission("psi:inventory:edit:US")||UserUtils.hasPermission("psi:inventory:revise:US")){
					countryCode="US";
				}else if(UserUtils.hasPermission("psi:inventory:edit:JP")||UserUtils.hasPermission("psi:inventory:revise:JP")){
					countryCode="JP";
				}
				List<Stock> psiStocks =this.stockService.findByCountryCode(countryCode,"",null);
				if(psiStocks!=null&&psiStocks.size()>0){
					psiInventory.setWarehouse(psiStocks.get(0));
				}else{
					psiInventory.setWarehouse(stocks.get(0));
				}
				//借用sku存储是选中了哪个仓库
				psiInventory.setSku(countryCode);
			}else{
				//是admin
				psiInventory.setWarehouse(stocks.get(0));
				//借用sku存储是选中了哪个仓库
				psiInventory.setSku(stocks.get(0).getCountrycode());
			}
		}else{
			//操作库内调换   或其他可能只传个stockId
			Integer wareHouseId=psiInventory.getWarehouse().getId();
			if(wareHouseId!=null){
				for(Stock stock :stocks){
					if(stock.getId().equals(wareHouseId)){
						psiInventory.setWarehouse(stock);
						//借用sku存储是选中了哪个仓库
						psiInventory.setSku(stock.getCountrycode());
						break;
					}
				}
			}
		}
		Map<Integer,String> volumeMap= this.psiProductService.getVomueAndWeight();
		List<String>  productNames = Lists.newArrayList();
		Page<PsiInventory> page =new Page<PsiInventory>(request, response);
        page = psiInventoryService.find(page, psiInventory); 
        for(PsiInventory inventory:page.getList()){
        	Float  volume =0f;
        	if(volumeMap.get(inventory.getProductId())!=null){
        		String arr[] = volumeMap.get(inventory.getProductId()).split(",");
        		if(arr[0]!=null&&arr[0]!=null){
        			Float boxV = Float.parseFloat(arr[0]);
            		Integer boxP = Integer.parseInt(arr[2]);
                	volume = (int)Math.ceil(inventory.getNewQuantity()/boxP.doubleValue())*boxV;
        		}
        	}
        
        	inventory.setVolume(volume);
        	productNames.add(inventory.getProductNameColor());
        	inventory.setInventoryList(this.psiInventoryService.findByProductAndStock(inventory.getProductId(),inventory.getColorCode(), psiInventory));
        }
       Map<String, Map<String, String>> saleMap=psiProductEliminateService.findAll(productNames);
       Map<String,Integer> tranMap = this.psiInventoryService.getTranSkuQuantity(true,psiInventory.getWarehouse().getId());
       Map<String,Integer> tranTotalMap = Maps.newHashMap(); 
        for(Map.Entry<String,Integer> entry:tranMap.entrySet()){
        	String pro_color_country = entry.getKey();
        	String pro_color= pro_color_country.split(",,")[0];
        	String country =pro_color_country.split(",,")[1];
        	Integer qua= tranMap.get(pro_color_country);
        	//中国仓               海外本地仓查看发往本仓库的数量
        	if(psiInventory.getSku().equals("US")&&"com,ca".contains(country)||psiInventory.getSku().equals("JP")&&"jp".contains(country) ||psiInventory.getSku().equals("DE")&&"de,uk,fr,es,it".contains(country)||psiInventory.getSku().equals("CN")){
        		if(tranTotalMap.get(pro_color)!=null){
            		qua+=tranTotalMap.get(pro_color);
            	}
        	}else{
        		continue;
        	}
        	tranTotalMap.put(pro_color, qua);
        }
        
        if(psiInventory.getSku().equals("CN")){
        	 Map<String,Integer> waitTranMap = this.psiInventoryService.getTranSkuQuantity(false,psiInventory.getWarehouse().getId());
             Map<String,Integer> waitTranTotalMap = Maps.newHashMap(); 
             for(Map.Entry<String,Integer> mapEntry:waitTranMap.entrySet()){
            	 String pro_color_country = mapEntry.getKey();
             	String pro_color= pro_color_country.split(",,")[0];
             	Integer qua= mapEntry.getValue();
         		if(waitTranTotalMap.get(pro_color)!=null){
             		qua+=waitTranTotalMap.get(pro_color);
             	}
             	waitTranTotalMap.put(pro_color, qua);
             }
             model.addAttribute("waitTranMap", waitTranMap);
             model.addAttribute("waitTranTotalMap", waitTranTotalMap);
        }
       
        //新品
        List<String> newProducts=this.psiInventoryService.getNoSkus(psiInventory.getWarehouse().getId());
        Set<String> newProCountrySets = Sets.newHashSet();
        Set<String> newProSets = Sets.newHashSet();
        if(newProducts!=null&&newProducts.size()>0){
        	for(String con:newProducts){
        		newProSets.add(con.substring(0, con.lastIndexOf("_")));
        		newProCountrySets.add(con);
        	}
        	
        }
        //算出及时库存容量
        Float capacityTimely = this.psiInventoryService.getTimelyCapacity(psiInventory.getWarehouse().getId());
        
        //根据仓库 、sku  算出上周出库数
        Date lastMonday = DateUtils.getMonday(DateUtils.addDays(sdf.parse(sdf.format(new Date())), -7));
        Map<String,Map<String,Integer>> lastWeekMap=this.psiInventoryService.getLastWeekOutInventoryQuantity(psiInventory.getWarehouse().getId(), lastMonday, DateUtils.addDays(lastMonday, 8));
        model.addAttribute("tranMap", tranMap);
        model.addAttribute("tranTotalMap", tranTotalMap);
        model.addAttribute("stocks", stocks);
        model.addAttribute("page", page);
        model.addAttribute("newProSets", newProSets);
        model.addAttribute("newProCountrySets", newProCountrySets);
        model.addAttribute("lastWeekMap", lastWeekMap);
        model.addAttribute("capacityTimely", capacityTimely);
        model.addAttribute("saleMap", saleMap);
        model.addAttribute("volumeMap", volumeMap);
		return "modules/psi/psiInventoryList";
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = {"ajaxView"})
	public String ajaxView(PsiInventory psiInventory, HttpServletRequest request, HttpServletResponse response, Model model) {
		String warehouseName=psiInventory.getWarehouseName();
		Integer warehouseId=psiInventory.getWarehouse().getId();
		List<PsiInventoryRevisionLog> logs= Lists.newArrayList();
		String[] dataArr =warehouseName.split(";");
		String showStr="";
		for(String data:dataArr){
			PsiInventoryRevisionLog log = new PsiInventoryRevisionLog();
			String[] single=data.split(",");
			log.setSku(single[0]);
			log.setOperationType(single[1]);
			log.setQuantity(Integer.parseInt(single[2]));
			logs.add(log);
		}
		psiInventory=this.psiInventoryService.findBySku(psiInventory.getSku(),warehouseId );//根据仓库和sku查出唯一的一条库存数据
		String jsonStr = "{\"msg\":\"true\"";
		Integer newTotal    = 0;
		Integer oldTotal    = 0;
		Integer brokenTotal = 0;
		Integer renewTotal  = 0;
		List<PsiInventory> inventorys = Lists.newLinkedList();
		
		Map<String,PsiInventory> skuMap = Maps.newHashMap();
		for(PsiInventoryRevisionLog item:logs){
			PsiInventory tempInventory = new PsiInventory();
			//key:国家  整合new old broken renew 数据
			String key = item.getSku();
			if(skuMap.containsKey(key)){
				tempInventory = skuMap.get(key);
				setTypeDataAndLog(item, tempInventory);
			}else{
				tempInventory.setSku(item.getSku());
				setTypeDataAndLog(item, tempInventory);
				skuMap.put(key, tempInventory);
			}
		}
		
		for(Map.Entry<String, PsiInventory> entry:skuMap.entrySet()){
			String key = entry.getKey();
			PsiInventory temp = entry.getValue();
			//PsiInventory toInventory = this.psiInventoryService.findInventory(productId, warehouse, temp.getCountryCode(), colorCode);
			PsiInventory toInventory = this.psiInventoryService.findBySku(temp.getSku(), warehouseId);
			showStr="";
			//如果这个对象为null 重新生成一个数据
			if(toInventory==null){
				toInventory =new PsiInventory();
				toInventory.setNewQuantity(0);
				toInventory.setOldQuantity(0);
				toInventory.setBrokenQuantity(0);
				toInventory.setRenewQuantity(0);
				toInventory.setSparesQuantity(0);
				toInventory.setOfflineQuantity(0);
				toInventory.setSku(temp.getSku());
			}
			if(temp.getNewQuantity()!=null){
				newTotal+=temp.getNewQuantity();
				showStr+="+"+temp.getNewQuantity()+";";
			}else{
				showStr+=";";
			}
			if(temp.getOldQuantity()!=null){
				oldTotal +=temp.getOldQuantity();
				showStr+="+"+temp.getOldQuantity()+";";
			}else{
				showStr+=";";
			}
			if(temp.getBrokenQuantity()!=null){
				brokenTotal+=temp.getBrokenQuantity();
				showStr+="+"+temp.getBrokenQuantity()+";";
			}else{
				showStr+=";";
			}
			if(temp.getRenewQuantity()!=null){
				renewTotal +=temp.getRenewQuantity();
				showStr+="+"+temp.getRenewQuantity()+";";
			}else{
				showStr+=";";
			}
			toInventory.setWarehouseName(showStr);
			inventorys.add(toInventory);
		}
		showStr="";
		if(newTotal>0){
			showStr+="-"+newTotal+";";
		}else{
			showStr+=";";
		}
		if(oldTotal>0){
			showStr+="-"+oldTotal+";";
		}else{
			showStr+=";";
		}
		
		if(brokenTotal>0){
			showStr+="-"+brokenTotal+";";
		}else{
			showStr+=";";
		}
		
		if(renewTotal>0){
			showStr+="-"+renewTotal+";";
		}else{
			showStr+=";";
		}
		String saveMsg = "";
		if(psiInventory.getNewQuantity()<newTotal){
			saveMsg+="new quantity is too large，";
		}
		if(psiInventory.getOldQuantity()<oldTotal){
			saveMsg+="old quantity is too large，";
		}
		if(psiInventory.getBrokenQuantity()<brokenTotal){
			saveMsg+="broken quantity is too large，";
		}
		if(psiInventory.getRenewQuantity()<renewTotal){
			saveMsg+="renew quantity is too large，";
		}
		psiInventory.setWarehouseName(showStr);
		//前台用countryCode传的sku的值
		//psiInventory.setSku(psiInventory.getCountryCode());
		inventorys.add(psiInventory);
		
		String listStr=JsonMapper.toJsonString(inventorys);
		String returnStr=jsonStr+",\"inventory\":"+listStr+",\"saveMsg\":\""+saveMsg+"\"}";
		return returnStr;
	}
	
	@ResponseBody
	@RequestMapping(value = {"ajaxSkuQuantity"})
	public String ajaxSkuQuantity(Integer warehouseId, Integer productId,String countryCode,String colorCode, HttpServletResponse response, Model model) {
		List<PsiInventory> inventorys = this.psiInventoryService.findInventorySum(productId, warehouseId, countryCode, colorCode);
		StringBuilder returnStr= new StringBuilder();
		for (PsiInventory psiInventory : inventorys) {
			returnStr.append(psiInventory.getSku()).append("<b>(").append(psiInventory.getNewQuantity()+psiInventory.getOldQuantity()+psiInventory.getBrokenQuantity()+psiInventory.getRenewQuantity()).append(")</b><br/>");
		} 
		return returnStr.toString();
	}
	
	@ResponseBody
	@RequestMapping(value = {"ajaxSkuTotalQuantity"})
	public String ajaxSkuTotalQuantity(Integer warehouseId, Integer productId,String colorCode, HttpServletResponse response, Model model) {
		List<PsiInventory> inventorys = this.psiInventoryService.findInventorySum(productId, warehouseId, null, colorCode);
		//对多个sku进行分解，不加结尾的国家
		StringBuilder returnStr= new StringBuilder();
		Map<String,Integer> map = Maps.newHashMap();
		for (PsiInventory psiInventory : inventorys) {
			String preSku = psiInventory.getSku();
			boolean res = preSku.matches("[0-9]+");
			if(!res&&preSku.contains("-")){
				preSku = preSku.substring(0,preSku.lastIndexOf("-"));
			}
			Integer total = psiInventory.getNewQuantity()+psiInventory.getOldQuantity()+psiInventory.getBrokenQuantity()+psiInventory.getRenewQuantity();
			if(map.get(preSku)!=null){
				total+=map.get(preSku);
			}
			map.put(preSku, total);
		} 
//		for(String pre:map.keySet()){
		for(Map.Entry<String, Integer> entry:map.entrySet()){
			String pre = entry.getKey();
			if(!entry.getValue().equals(0)){
				returnStr.append(pre).append("<b>(").append(entry.getValue()).append(")</b><br/>");
			}
		}
		return returnStr.toString();
	}
	
	@ResponseBody
	@RequestMapping(value = {"fba/ajaxSkuQuantity"})
	public String fbaAjaxSkuQuantity(String country, String name ,HttpServletResponse response) {
		return psiInventoryService.getFbaTip(country, name);
	}
	
	@ResponseBody
	@RequestMapping(value = {"ajaxResidue"})
	public String ajaxResidue(String valiStr, HttpServletResponse response, Model model) {
		StringBuilder res= new StringBuilder();
		if(StringUtils.isNotEmpty(valiStr)){
			Set<String> skus = Sets.newHashSet();
			for(String skuQuantity:valiStr.split(";")){
				String arr[] = skuQuantity.split(",");
				skus.add(arr[0]);
			}
			
			Map<String,Integer> skuMap = this.psiProductService.getResidueMap(skus); 
			if(skuMap!=null&&skuMap.size()>0){
				for(String skuQuantity:valiStr.split(";")){
					String arr[] = skuQuantity.split(",");
					String sku = arr[0];
					Integer quantity=Integer.parseInt(arr[1]);
					if(skuMap.get(sku)>quantity){
						res.append(sku).append("库存剩余数：").append(quantity).append("预留数：").append(skuMap.get(sku)).append(";");
					}
				}
			}
		}
		return res.toString();
	}
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "countryChange")
	public String countryChange(PsiInventory psiInventory,Model model){
		Stock  stock = this.stockService.get(psiInventory.getWarehouse().getId());
		//查询已建贴数量
		Map<String, Integer> usableMap =this.psiInventoryService.getFbaWorkingMap(stock.getCountrycode()); 
		//查询未确认数量
		Map<String,Integer>  unSureMap =this.skuChangeBillService.getSkuChangeNoSure(null, 19);
		
		Integer residue =0;
		//如果被转化国家为de 并且仓库为德国仓
		if(psiInventory.getCountryCode().equals("de")&&stock.getCountrycode().equals("DE")){
			residue = this.psiProductService.getResidueById(psiInventory.getProductId());
		}
		
		for(Map.Entry<String, Integer> entry:usableMap.entrySet()){
			String sku = entry.getKey();
			//被建fba贴+未确认
			if(unSureMap.get(sku)!=null){
				usableMap.put(sku, entry.getValue()+unSureMap.get(sku));
			}
		}
//		for(String sku:unSureMap.keySet()){
		for(Map.Entry<String, Integer> entry:unSureMap.entrySet()){
			String sku = entry.getKey();
			//如果未确认的不在新建fba贴里面，则被占用的加上这部分
			if(usableMap.get(sku)==null){
				usableMap.put(sku, entry.getValue());
			}
		}
		
		List<PsiSku> psiSkus=this.psiProductService.getSkus(psiInventory.getProductName(), psiInventory.getColorCode());
		Set<String> selfs=Sets.newLinkedHashSet();
		Set<String> others=Sets.newHashSet();
		Map<String,Integer[]> inventoryMaps = Maps.newHashMap();
		String curBoundingSku ="";
		String productName=psiInventory.getProductName();
		if(StringUtils.isNotBlank(psiInventory.getColorCode())){
			productName=productName+"_"+psiInventory.getColorCode();
		}
		PsiProduct product = psiProductService.findProductByName(productName);
		Set<String> countrySet=Sets.newHashSet(product.getPlatform().split(","));
		for(PsiSku sku:psiSkus){
			if(sku.getCountry().equals(psiInventory.getCountryCode())&&"1".equals(sku.getUseBarcode())){
				curBoundingSku=sku.getSku();
			}
			//只能转换成当前使用的条码
			if("1".equals(sku.getUseBarcode())){
				others.add(sku.getSku());
				if(countrySet.contains(sku.getCountry())){
					countrySet.remove(sku.getCountry());
				}
			}
		}
		
		List<PsiInventory> lists=this.psiInventoryService.findInventoryByPCCW(psiInventory.getProductId(), "", psiInventory.getColorCode(), psiInventory.getWarehouse().getId());
		for(PsiInventory inventory:lists){
			//如果这个同一产品、国家、颜色有多个sku，而这个sku在库里没数据，则转出项不包含这个
			if(psiInventory.getCountryCode().equals(inventory.getCountryCode())){
				selfs.add(inventory.getSku());
			}
			//if(!inventory.getProductColorCountry().equals(inventory.getSku())){
				others.add(inventory.getSku());//这个产品库里有的都可转换
				if(countrySet.contains(inventory.getCountryCode())){
					countrySet.remove(inventory.getCountryCode());
				}
			//}
			Integer[] obj={inventory.getNewQuantity(),inventory.getOldQuantity(),inventory.getBrokenQuantity(),inventory.getRenewQuantity(),inventory.getNewQuantity()-(usableMap.get(inventory.getSku())==null?0:usableMap.get(inventory.getSku()))};
			inventoryMaps.put(inventory.getSku(),obj);
		}
		//未贴码新品无库存记录产品
		if(countrySet.size()>0){
			for (String country: countrySet) {
				if(!country.contains("uk")&&!country.contains("fr")&&!country.contains("it")&&!country.contains("es")){
					others.add(productName+"_"+country);
				}
			}
		}
		
		//转码只能转库里面有的
		String sku ="";
		//如果库里有当前使用条码
		if(selfs.contains(curBoundingSku)){
			sku=curBoundingSku;
		}else{
			for(String skus:selfs){
				sku=skus;
				break;
			}
		}
		
		//获取第一个sku给初始页面赋值
		psiInventory =this.psiInventoryService.findBySku(sku, psiInventory.getWarehouse().getId());
		//psiInventory.setUsableQuantity(psiInventory.getNewQuantity()-(usableMap.get(sku)==null?0:usableMap.get(sku))-residue);
		psiInventory.setUsableQuantity(psiInventory.getNewQuantity()-(usableMap.get(sku)==null?0:usableMap.get(sku)));
		Map<String, String> typeMap = Maps.newLinkedHashMap();
		typeMap.put("1", "new");
		if(psiInventory.getOldQuantity()!=0){
			typeMap.put("2", "old");
		}
		if(psiInventory.getBrokenQuantity()!=0){
			typeMap.put("3", "broken");
		}
		if(psiInventory.getRenewQuantity()!=0){
			typeMap.put("4", "renew");
		}
		
		Integer packQuantity = this.psiProductService.findPackQuantity(psiInventory.getProductId());
		Map<String,String> fnskuMap=amazonProduct2Service.getSkuAndFnskuMap();
		model.addAttribute("packQuantity", packQuantity);
		model.addAttribute("usableMap", usableMap);
		model.addAttribute("typeMap", typeMap);
		model.addAttribute("selfs", selfs);
		model.addAttribute("others", others);
		model.addAttribute("inventoryMaps", JSON.toJSON(inventoryMaps));
		model.addAttribute("fnskuMap", JSON.toJSON(fnskuMap));
		model.addAttribute("psiInventory", psiInventory);
		model.addAttribute("residue", residue);
		return "modules/psi/psiInventoryCountryChange";
	}
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "countryChangeSave")
	public String countryChangeSave(PsiInventory psiInventory,String overResidue,Model model,RedirectAttributes redirectAttributes){
		List<PsiInventoryRevisionLog> changeItems = psiInventory.getChangeItems();
		psiInventory = this.psiInventoryService.findBySku(psiInventory.getSku(), psiInventory.getWarehouse().getId());
		psiInventory.setChangeItems(changeItems);
		this.psiInventoryService.changeCountrySave(psiInventory);
		if(StringUtils.isNotEmpty(overResidue)){
			//如果剩余数量小于库存剩余，发信给ada
			String content =psiInventory.getSku()+","+overResidue;
			String title ="SKU:"+psiInventory.getSku()+"库存预留不足!"+DateUtils.getDate("-yyyy/M/dd");
			String sendEmail ="george@inateck.com";
			this.sendEmail(content, title,sendEmail,null);
		}
		addMessage(redirectAttributes, "SKU change success");
		return "redirect:"+Global.getAdminPath()+"/psi/psiInventory?warehouse.id="+psiInventory.getWarehouse().getId();
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
	
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "adjust")
	public String adjust(Integer warehouseId,Model model,RedirectAttributes redirectAttributes){
		//查询获取需要矫正的数据
		String res=this.psiInventoryService.getAdjustInventoryDataBylog(warehouseId);
		if(StringUtils.isEmpty(res)){
			res="No Data Need To Adjust!";
		}
		addMessage(redirectAttributes, res);
		return "redirect:"+Global.getAdminPath()+"/psi/psiInventory?warehouse.id="+warehouseId;
	}

	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "newOldChange")
	public String newOldChange(PsiInventory psiInventory,Model model){
		Map<String,Integer[]> inventoryMaps = Maps.newHashMap();
		Set<String> skuSet = Sets.newHashSet();
		List<PsiInventory> inventorys =this.psiInventoryService.findInventoryByPCCW(psiInventory.getProductId(), psiInventory.getCountryCode(),psiInventory.getColorCode(),psiInventory.getWarehouse().getId());
		for(PsiInventory inventory:inventorys){
			Integer[] obj={inventory.getNewQuantity(),inventory.getOldQuantity(),inventory.getBrokenQuantity(),inventory.getRenewQuantity(),inventory.getSparesQuantity(),inventory.getOfflineQuantity()};
			inventoryMaps.put(inventory.getSku(),obj);
			skuSet.add(inventory.getSku());
		}
		
		
		Map<String, String> typeMap = Maps.newLinkedHashMap();
		typeMap.put("1", "New_To_Old");
		typeMap.put("2", "New_To_Broken");
		typeMap.put("3", "New_To_Renew");
		typeMap.put("4", "Old_To_New");
		typeMap.put("5", "Old_To_Broken");
		typeMap.put("6", "Old_To_Renew");
		typeMap.put("7", "Broken_To_New");
		typeMap.put("8", "Broken_To_Old");
		typeMap.put("9", "Broken_To_Renew");
		typeMap.put("10", "Renew_To_New");
		typeMap.put("11", "Renew_To_Old");
		typeMap.put("12", "Renew_To_Broken");
		typeMap.put("13", "Spares_To_New");
		if(UserUtils.hasPermission("psi:inventory:offline")){
			typeMap.put("14", "New_To_Offline");
			typeMap.put("15", "Offline_To_New");
		}
		model.addAttribute("typeMap", typeMap);
		model.addAttribute("skuSet", skuSet);
		model.addAttribute("psiInventory", psiInventory);
		model.addAttribute("inventoryMaps", JSON.toJSON(inventoryMaps));
		return "modules/psi/psiInventoryNewOldChange";
	}
	
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "newOldChangeSave")
	public String newOldChangeSave(PsiInventory psiInventory,Model model,RedirectAttributes redirectAttributes){
		List<PsiInventoryRevisionLog> changeItems = psiInventory.getChangeItems();
		psiInventory = this.psiInventoryService.findBySku(psiInventory.getSku(), psiInventory.getWarehouse().getId());
		psiInventory.setChangeItems(changeItems);
		String rs=this.psiInventoryService.newOldChangeSave(psiInventory);
		//如果是new_to_offline
		if(StringUtils.isNotEmpty(rs)){
			String[] arr =rs.split(","); 
			Integer newId = Integer.parseInt(arr[0]);
			String userId =this.groupUserService.getResponsibleByCountryProductId(psiInventory.getCountryCode(),psiInventory.getProductId());
			String name="All";
			String sendEmail="";
			if(StringUtils.isNotEmpty(userId)){
				User user =null;
				if(userId.contains(",")){
					user = UserUtils.getUserById(userId.split(",")[0]);
				}else{
					user = UserUtils.getUserById(userId);
				}
				name=user.getName();
				sendEmail = user.getEmail();
			}else{
				sendEmail="amazon-sales@inateck.com";
			}
			rs=" Notice:New_To_Offline's,please contact Sales to review pass!!!";
			String content="Hi,"+name+"<br/><br/>&nbsp;&nbsp;产品["+psiInventory.getProductNameColor()+"]线下需要("+arr[1]+")个，请及时【<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/psiQualityChangeBill/list'>点击此处</a>】进行确认!<br/><br/><br/>best regards<br/>Erp System";
			String title="库存线下转线上已生成,请及时确认,"+psiInventory.getProductNameColor()+"线下需要("+arr[1]+")个(id:"+newId+")";
			String ccEmail="george@inateck.com,amazon-sales@inateck.com,tim@inateck.com,supply-chain@inateck.com";
			sendEmail(content, title, sendEmail,ccEmail);
		}
		addMessage(redirectAttributes, "Change Success"+rs);
		return "redirect:"+Global.getAdminPath()+"/psi/psiInventory?warehouse.id="+psiInventory.getWarehouse().getId();
	}

	private void setTypeDataAndLog(PsiInventoryRevisionLog log,PsiInventory tempInventory){
		String type= log.getOperationType();
		Integer quantity = log.getQuantity();
		if(type.equals("1")){
			tempInventory.setNewQuantity(quantity);
		}else if(type.equals("2")){
			tempInventory.setOldQuantity(quantity);
		}else if(type.equals("3")){
			tempInventory.setBrokenQuantity(quantity);
		}else if(type.equals("4")){
			tempInventory.setRenewQuantity(quantity);
		}
	}
	
	@RequestMapping(value = "inventoryWarn")
	public String inventoryWarn(PsiProduct product,Model model){
		List<PsiProduct> list = psiProductService.findIsComponents("0");
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountry();
		model.addAttribute("accountMap", accountMap);
		
		//产品名_国家 fba
		Map<String, PsiInventoryFba>  fbas = psiInventoryService.getAllProductFbaInfo();
		//产品名_国家
		Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();
		//产品名
		Map<String, PsiInventoryTotalDto> producting = psiInventoryService.getProducingQuantity();
		Map<String,Map<String, PsiInventoryTotalDto>> rs = psiInventoryService.getTransporttingQuantity();
		Map<String, PsiInventoryTotalDto> transportting = rs.get("1");
		Map<String, PsiInventoryTotalDtoByInStock> inventorys = psiInventoryService.getInventoryQuantity();
		
		Map<String,Map<String,Object>> productsMoqAndPrice = psiInventoryService.getProductsMoqAndPrice();
		model.addAttribute("productsMoqAndPrice", productsMoqAndPrice);
		//销售价格
		if(salePrice==null||salePrice.size()==0){
			salePrice=CacheBuilder.newBuilder().expireAfterWrite(24*7L, TimeUnit.HOURS).build();
			salePrice.cleanUp();
			salePrice.putAll(this.amazonProduct2Service.getSalePriceDollers());
		}
		model.addAttribute("salePrice", salePrice.asMap());
		//已建Working状态的fba贴
		Map<String, Object[]> fbaWorking =  psiInventoryService.getFbaWorking();
		Map<String, Integer> fbaWorkingByEuro =  psiInventoryService.getFbaWorkingByEuro();
		
		//产品名_国家 fba在途数据
		Map<String,Integer> fbaTran = psiInventoryService.getAllFbaTransporttingByNameAndCountry();
		
		List<Object[]> isNewAll = psiProductEliminateService.findIsNewProductNameWithTotalAndEu();
		List<Object[]> productPositionAndIsNew = psiProductEliminateService.findPositionAndIsNew();
		Map<String, String> isSaleEuMap = psiProductEliminateService.findProductPositionByCountry(Lists.newArrayList("de","uk","fr","it","es"));
		
		Map<String, String> isSaleMap = Maps.newHashMap();
		Map<String, String> isNewMap = Maps.newHashMap();
		
		for (Object[] objs : isNewAll) {
			String name = objs[0].toString();
			if(Double.parseDouble(objs[1].toString())>0){
				isNewMap.put(name, "1");
			}
			if(Double.parseDouble(objs[2].toString())>0){
				isNewMap.put(name+"_eu", "1");
			}
			//总的产品定位
			isSaleMap.put(name, objs[3].toString());
			if (StringUtils.isNotEmpty(isSaleEuMap.get(name))) {
				isSaleMap.put(name+"_eu", isSaleEuMap.get(name));
			}
		}
		for (Object[] objs : productPositionAndIsNew) {
			isSaleMap.put(objs[0].toString(), objs[1].toString());
			if(Integer.parseInt(objs[2].toString())>0){
				isNewMap.put(objs[0].toString(), "1");
			}
		}
		
		model.addAttribute("isSaleMap", isSaleMap);
		model.addAttribute("isNewMap", isNewMap);
		
		//库销比[产品名[国家 	库销比]]
		model.addAttribute("inventorySalesMonthMap", psiProductInStockService.getInventorySalesMonth());
		
		
		model.addAttribute("isPanEuMap", psiInventoryService.isPanEuMap());
		model.addAttribute("fanOuMap", psiProductEliminateService.findProductFanOuFlag());
		model.addAttribute("list", list);
		model.addAttribute("producting", producting);
		model.addAttribute("transportting", transportting);
		model.addAttribute("preTransportting", rs.get("0"));
		model.addAttribute("inventorys", inventorys);
		model.addAttribute("fbas", fbas);
		model.addAttribute("fbaTran", fbaTran);
		model.addAttribute("fancha", fancha);
		model.addAttribute("fbaWorking", fbaWorking);
		model.addAttribute("fbaWorkingByEuro", fbaWorkingByEuro);
		model.addAttribute("queMap", psiOutOfStockInfoService.getEuCountryThreeWeekGap());
		
		List<PsiProductAttribute> attrs = psiProductAttributeService.findAll();
		Map<String,PsiProductAttribute> productAttr=Maps.newHashMap();
		for (PsiProductAttribute psiProductAttribute : attrs) {
			productAttr.put(psiProductAttribute.getColorName(), psiProductAttribute);
		}
		model.addAttribute("productAttr", productAttr);
		//运输方式和缓冲周期
		model.addAttribute("productTranTypeAndBP", psiProductEliminateService.findProductAttr());
		model.addAttribute("hasPower", psiProductService.getHasPower());
		model.addAttribute("newTwoMonth",psiProductEliminateService.findNewAfterTwoMonth());
		Date beforeDate=DateUtils.addMonths(new Date(), -1);
		String month=formatMonth.format(beforeDate);
		Map<String,PsiProductEliminate> productCountryAttrMap=psiProductEliminateService.findProductCountryAttr();
		Map<String,Map<String,Float>> starandMap=psiProductInStockService.findYearTurnoverStarand(DateUtils.getFirstDayOfMonth(beforeDate),DateUtils.getFirstDayOfMonth(beforeDate),productCountryAttrMap);
		model.addAttribute("starandMap", starandMap.get(month));
		Map<String, Map<String, Map<String, PsiInventoryTurnoverData>>> turnoverMap=turnoverDataService.getTurnoverRate(month,month, "1",null);
		model.addAttribute("turnoverMap", turnoverMap.get(month));
		model.addAttribute("typeLineMap", groupDictService.getTypeLine(null));
		String hiddens = psiProductService.findAllHidden().toString();
		model.addAttribute("hiddens", hiddens);
		return "modules/psi/psiWarnInventory";
	}
	
	@RequestMapping(value = "productInfoDetail")
	public String productInfoDetail(HttpServletRequest request,String productName,SaleReport saleReport,Model model){
		productName = HtmlUtils.htmlUnescape(productName);
		if(StringUtils.isBlank(productName)){
			throw new RuntimeException("Product name can not empty");
		}
		if(StringUtils.isBlank(saleReport.getCurrencyType())){
			saleReport.setCurrencyType("EUR");
		}
		if(productName.endsWith("[GROUP]")){
			String tempPName=productName.substring(0,productName.length()-7);
			String name=tempPName;
			String color = "";
			if(name.indexOf("_")>0){
				name=tempPName.substring(0,tempPName.lastIndexOf("_"));
				color = tempPName.substring(tempPName.lastIndexOf("_")+1);
			}
			Map<String,Set<String>> nameList=psiProductService.findGroupName(name,color);
			Map<String, Map<String,SaleReport>> map=saleReportService.getSalesByUnionProduct(saleReport,nameList.get("0"),color); 
			model.addAttribute("data", map);
			model.addAttribute("nameList", nameList.get("1"));
			model.addAttribute("productName", productName);
			//构建x轴
			List<String> xAxis  = Lists.newArrayList();
			String language=LocaleContextHolder.getLocale().getLanguage();
			Date start = saleReport.getStart();
			if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
				start = DateUtils.addDays(start, -1);
			}
			Date end = saleReport.getEnd();
			Map<String, String> tip = Maps.newHashMap();
			SimpleDateFormat enFormat=new java.text.SimpleDateFormat("E",Locale.US);
			SimpleDateFormat deFormat=new java.text.SimpleDateFormat("E",Locale.GERMANY);
			while(end.after(start)||end.equals(start)){
				if("2".equals(saleReport.getSearchType())){
					String key = formatWeek.format(start);
					int year =DateUtils.getSunday(start).getYear()+1900;
					int week =  Integer.parseInt(key.substring(4));
					if(week==53){
		                year =DateUtils.getMonday(start).getYear()+1900;
				    }
					if(week<10){
						key = year+"0"+week;
					}else{
						key =year+""+week;
					}
					xAxis.add(key);
					Date first = DateUtils.getFirstDayOfWeek(year, week);
					tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
					start = DateUtils.addWeeks(start, 1);
				}else if("3".equals(saleReport.getSearchType())){
					String key = formatMonth.format(start);
					xAxis.add(key);
					tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
					start = DateUtils.addMonths(start, 1);
				}else{
					String key = formatDay.format(start);
					xAxis.add(key);
					if("zh".equals(language)){
						tip.put(key,DateUtils.getDate(start,"E"));
					}else if("de".equals(language)){
						tip.put(key,deFormat.format(start));
					}else{
						tip.put(key,enFormat.format(start));
					}
					
					start = DateUtils.addDays(start, 1);
				}
			}
			
			if("zh".equals(language)){
				model.addAttribute("flashOrder", "闪购：");
				model.addAttribute("bulkOrder", "最大订单：");
				model.addAttribute("freeSales", "免费或评测：");
				model.addAttribute("promotionsSales", "促销站内：");
				model.addAttribute("outsideSales", "促销站外：");
				model.addAttribute("adsSales", "广告销量：");
			}else if("de".equals(language)){
				model.addAttribute("flashOrder", "Blitzangebot：");
				model.addAttribute("bulkOrder", "Großauftrag：");
				model.addAttribute("freeSales", "Kostenfrei：");
				model.addAttribute("promotionsSales", "Promo-Verkäufe：");
				model.addAttribute("outsideSales", "outside promotion：");
				model.addAttribute("adsSales", "Ad-Verkäufe：");
			}else{
				model.addAttribute("flashOrder", "FlashSales：");
				model.addAttribute("bulkOrder", "BulkOrder：");
				model.addAttribute("freeSales", "FreeSales Or Review：");
				model.addAttribute("promotionsSales", "inside promotion：");
				model.addAttribute("outsideSales", "outside promotion：");
				model.addAttribute("adsSales", "AdsSales：");
			}
			
			model.addAttribute("xAxis", xAxis);
			model.addAttribute("tip", tip);
			Set<String> nameSet=nameList.get("1");
			for (String tempName: nameSet) {
				PsiProduct product = psiProductService.findProductByName(tempName);
				model.addAttribute("product", product);
				String  image=amazonPostsDetailService.findImage(tempName);
				if(StringUtils.isNotBlank(image)){
					model.addAttribute("firstImage", image);
					break;
				}
			}
			
			Map<String,PsiInventoryTotalDto> producting=psiInventoryService.getProducingQuantity(nameSet);
			model.addAttribute("producting", producting);
			Map<String,Map<String,PsiInventoryTotalDto>> rs = psiInventoryService.getTransporttingQuantity(nameSet);
			model.addAttribute("transportting", rs.get("1"));
			model.addAttribute("preTransportting", rs.get("0"));
			Map<String, PsiInventoryTotalDtoByInStock> inventorys = psiInventoryService.getInventoryQuantity(nameSet);
			model.addAttribute("inventorys", inventorys);
			//fbaTran fbas returnMap
			Map<String,Object[]> attr=psiProductAttributeService.findQuantity(nameSet);
			model.addAttribute("productAttr", attr);
			Map<String,Map<String,Integer>> returnMap=removalOrderService.getReturningByProductName(nameSet);
			model.addAttribute("returnMap", returnMap);
			Map<String,Integer> fbaTran  = psiInventoryService.getFbaTransporttingByName(nameSet);
			model.addAttribute("fbaTran", fbaTran);
			Map<String, PsiInventoryFba>  fbas = psiInventoryService.getProductFbaInfo(nameSet);
			model.addAttribute("fbas", fbas);
			Map<String,ProductSalesInfo> fancha = Maps.newHashMap();
			model.addAttribute("fangChaFlag", fangChaFlag);
			if (!fangChaFlag) {	//保存方差数据时暂停业务
				fancha = productSalesInfoService.find(nameSet);
				model.addAttribute("fancha", fancha);
			}
			return "modules/amazoninfo/productInfoUnionDetail";
		}
		
		//销量信息
		saleReport.setSku(productName);
		String language=LocaleContextHolder.getLocale().getLanguage();
		
		Object[] safePrice=amazonProduct2Service.getAllSafePrice(productName);
		model.addAttribute("safePrice",safePrice);
		
		//海运保本价
		Object[] safePriceBysea=amazonProduct2Service.getAllSafePriceBySea(productName);
		model.addAttribute("safePriceBysea",safePriceBysea);
		
		
		Map<String, Map<String, SaleReport>>  data = saleReportService.getSalesBySingleProduct(saleReport);
		model.addAttribute("data", data);

		Map<String, Map<String,SaleReport>> otherData = saleReportService.getSalesBySingleProduct2(saleReport);
		model.addAttribute("otherData", otherData);
		PsiProductAttribute attr= psiProductAttributeService.get(productName);
		model.addAttribute("productAttr", attr);
		Map<String, Integer> bufferPeriodMap = psiProductEliminateService.findBufferPeriod(productName);
		model.addAttribute("bufferPeriodMap", bufferPeriodMap);
		//构建x轴
		List<String> xAxis  = Lists.newArrayList();
		
		Date start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		Date end = saleReport.getEnd();
		Map<String, String> tip = Maps.newHashMap();
		SimpleDateFormat enFormat=new java.text.SimpleDateFormat("E",Locale.US);
		SimpleDateFormat deFormat=new java.text.SimpleDateFormat("E",Locale.GERMANY);
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(start).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				Date first = DateUtils.getFirstDayOfWeek(year, week);
				tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
				start = DateUtils.addWeeks(start, 1);
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				if("zh".equals(language)){
					tip.put(key,DateUtils.getDate(start,"E"));
				}else if("de".equals(language)){
					tip.put(key,deFormat.format(start));
				}else{
					tip.put(key,enFormat.format(start));
				}
				
				start = DateUtils.addDays(start, 1);
			}
		}
		
		if("zh".equals(language)){
			model.addAttribute("flashOrder", "闪购：");
			model.addAttribute("bulkOrder", "最大订单：");
			model.addAttribute("freeSales", "免费或评测：");
			model.addAttribute("promotionsSales", "促销站内：");
			model.addAttribute("outsideSales", "促销站外：");
			model.addAttribute("adsSales", "广告销量：");
		}else if("de".equals(language)){
			model.addAttribute("flashOrder", "Blitzangebot：");
			model.addAttribute("bulkOrder", "Großauftrag：");
			model.addAttribute("freeSales", "Kostenfrei：");
			model.addAttribute("promotionsSales", "Promo-Verkäufe：");
			model.addAttribute("outsideSales", "outside promotion：");
			model.addAttribute("adsSales", "Ad-Verkäufe：");
		}else{
			model.addAttribute("flashOrder", "FlashSales：");
			model.addAttribute("bulkOrder", "BulkOrder：");
			model.addAttribute("freeSales", "FreeSales Or Review：");
			model.addAttribute("promotionsSales", "inside promotion：");
			model.addAttribute("outsideSales", "outside promotion：");
			model.addAttribute("adsSales", "AdsSales：");
		}
		
		model.addAttribute("xAxis", xAxis);
		model.addAttribute("tip", tip);
		
		//产品信息
		PsiProduct product = psiProductService.findProductByName(productName);
	
		if(product==null){
			throw new RuntimeException("产品不存在(未匹配sku)或者产品名存在'&'符;如果产品名内存在'&'符,请在搜索栏内查看该产品信息");
		}
		String  image=amazonPostsDetailService.findImage(productName);
		model.addAttribute("firstImage", image);
		
		String color = "";
		if(productName.indexOf("_")>0){
			color = productName.substring(productName.indexOf("_")+1);
		}
		
		
		Float partsPrice =0f;
		//隐藏配件价格
//		for(ProductParts parts: product.getProductParts()){
//			if(color.equals(parts.getColor())){
//				if(parts.getParts().getPrice()!=null&&parts.getMixtureRatio()!=null){
//					partsPrice+=parts.getParts().getPrice()*parts.getMixtureRatio();
//				}else if(parts.getParts().getRmbPrice()!=null&&parts.getMixtureRatio()!=null){
//					partsPrice+=parts.getParts().getRmbPrice()*parts.getMixtureRatio()/AmazonProduct2Service.getRateConfig().get("USD/CNY");
//				}
//				break;
//			};
//		}
		
		//查询所有跟单
		List<Integer> follows =psiProductService.getFollowMan();
		model.addAttribute("follows", follows);
		//查询产品经理
		String managerName ="";
		if(StringUtils.isNotEmpty(product.getType())){
			managerName=this.psiProductService.getManagerByProductType(product.getType());
		}
		model.addAttribute("managerName", managerName);
		
		//查询产品经理
		String purchaseName ="";
		if(StringUtils.isNotEmpty(product.getType())){
			purchaseName=this.psiProductService.getPurchaseByProductType(product.getType());
		}
		model.addAttribute("purchaseName", purchaseName);
		
		//查出线别
		String lineInfo = this.psiProductService.getLineName(product.getType());
		if(StringUtils.isNotEmpty(lineInfo)){
			String arr[] =lineInfo.split(",");
			String lineName=arr[0];
			String lineId = arr[1];
			String salesName = this.psiProductService.getResponseName(lineId);
			model.addAttribute("salesName", salesName);
			model.addAttribute("lineName", lineName);
		}
		
		
		
		
		model.addAttribute("photoName",attr.getCameraman()==null?"":attr.getCameraman());
		model.addAttribute("merchandiser",product.getCreateUser().getName());
		//准备该产品的所有sku
		Map<String, List<String>> skuMap = Maps.newHashMap();
		//Map<String, List<String>> asinMap = Maps.newHashMap();
		Map<String, Set<String>> asinMap = Maps.newHashMap();
		Set<String> skus = Sets.newHashSet();
		Set<String> asins = Sets.newHashSet();
		Map<String, List<PsiBarcode>> barMap = product.getDupBarcodeMap().get(color);		
		for (Map.Entry<String, List<PsiBarcode>> entry : barMap.entrySet()) {
			String country = entry.getKey();
			List<String> list = new ArrayList<String>();
			//List<String> asinList = new ArrayList<String>();
			Set<String> asinSet=Sets.newHashSet();
			skuMap.put(country, list);
			//asinMap.put(country, asinList);
			asinMap.put(country, asinSet);
			List<PsiBarcode> barcodeList = entry.getValue();
			for (PsiBarcode psiBarcode : barcodeList) {
				for (PsiSku psiSku :psiBarcode.getSkus()) {
					list.add(psiSku.getSku());
					skus.add(psiSku.getSku());
					String asin = psiSku.getAsin();
					if(StringUtils.isNotBlank(asin)){
						asins.add(asin);
						asinSet.add(asin);
					}
				}
			}
			
		}
		if(asinMap!=null&&asinMap.size()>0){
			 List<Dict> dictAll=DictUtils.getDictList("platform");
	         for (Dict dict : dictAll) {
	        	 Set<String> countryAsins=asinMap.get(dict.getValue());
	        	 if(countryAsins!=null&&countryAsins.size()>0){
	        		 model.addAttribute("deAsin",Lists.newArrayList(countryAsins).get(0));
	        		 model.addAttribute("asinsCountry", dict.getValue());
	        		 break;
	        	 }
	    	}
		}
		 
		//产品名_国家 fba
		Map<String, PsiInventoryFba>  fbas = psiInventoryService.getProductFbaInfo(productName);
		
		//产品名_国家
		Map<String,ProductSalesInfo> fancha = Maps.newHashMap();
		model.addAttribute("fangChaFlag", fangChaFlag);
		if (!fangChaFlag) {	//保存方差数据时暂停业务
			fancha = productSalesInfoService.find(productName);
		}
		
		//产品名
		PsiInventoryTotalDto producting = psiInventoryService.getProducingQuantity(productName,null);
		Map<String, PsiInventoryTotalDto >rs = psiInventoryService.getTransporttingQuantity(productName,null);
		PsiInventoryTotalDto transportting = rs.get("1");
		PsiInventoryTotalDtoByInStock inventorys = psiInventoryService.getInventoryQuantity(productName,null);
		model.addAttribute("producting", producting);
		model.addAttribute("transportting", transportting);
		model.addAttribute("preTransportting", rs.get("0"));
		model.addAttribute("inventorys", inventorys);
		//产品名_国家 fba在途数据
		Map<String,Integer> fbaTran  = psiInventoryService.getFbaTransporttingByName(productName);
		
	    //产品定位
	    Map<String, String> productPositionMap = psiProductEliminateService.findProductPositionByCountry(null);
	   	Map<String, PsiProductEliminate> productAttrMap = psiProductEliminateService.findAllInfoByNameWithColor(productName);
	   	Map<String, String> allInfo = Maps.newHashMap();
	   	String isNew = "1";
	   	String addedMonth = "";
	   	for (Map.Entry<String, PsiProductEliminate> entry : productAttrMap.entrySet()) {
	   		PsiProductEliminate psiProductEliminate = entry.getValue();
	   		if ("1".equals(isNew)) {	//有非新品就显示非新品
	   			isNew = psiProductEliminate.getIsNew();
			}
	   		if (StringUtils.isNotEmpty(psiProductEliminate.getAddedMonth()) && StringUtils.isEmpty(addedMonth)) {
	   			addedMonth = psiProductEliminate.getAddedMonth();
			}
		}
	   	allInfo.put("isNew", isNew);
	   	allInfo.put("addedMonth", addedMonth);
   		model.addAttribute("allInfo", allInfo);
   		
	   	model.addAttribute("productIsSale", productPositionMap.get(productName));	//新的方式标记是否在售
	   	
		
		//查询产品价格变化日志
		Map<Integer,String> priceLogMap= priceLogService.getRemarkMap(productName);
		Map<Integer,Map<String,Float>> purchasePriceMap=tieredService.getSinglePriceBaseMoq(productName,partsPrice);
		model.addAttribute("purchasePriceMap", purchasePriceMap);
		
		
		
		if(safePrice==null||safePrice[0]==null||safePrice[1]==null||safePrice[6]==null){
			 Map<String,AmazonPostsDetail> sizeMap=amazonPostsDetailService.findPostsSize(productName);
			 String platform=product.getPlatform();
			 if(product.getProductPackLength()!=null&&product.getProductPackWidth()!=null&&product.getProductPackHeight()!=null&&product.getProductPackWeight()!=null){
				 float length=product.getProductPackLength().floatValue()*0.3937008f;
				 float width=product.getProductPackWidth().floatValue()*0.3937008f;
				 float height=product.getProductPackHeight().floatValue()*0.3937008f;
				 float weight=product.getProductPackWeight().floatValue()/1000f*2.2046226f;
				 AmazonPostsDetail detail=new AmazonPostsDetail();
				 detail.setPackageLength(length);
				 detail.setPackageWidth(width);
		         detail.setPackageHeight(height);
		         detail.setPackageWeight(weight);
		         for (String cnt: platform.split(",")) {
					 if(sizeMap==null||sizeMap.size()==0||sizeMap.get(cnt)==null){
						 sizeMap.put(cnt, detail);
					 }
				 }
			 }
			
			 
			 if(sizeMap!=null&&sizeMap.size()>0){
				 Map<String, String> isEuMap=psiProductService.getPowerOrKeyboardByName();//1:keyboard+带电
				 Map<String,Float> fbaFeeMap=countFbaFee(productName,sizeMap,isEuMap);
				 if(fbaFeeMap!=null&&fbaFeeMap.size()>0){
					 Map<String,Integer> commissionMap=amazonPostsDetailService.find(product.getType());
					 Map<String,Float> purchasePrice=psiProductAttributeService.findTaxPrice(null,Sets.newHashSet(productName));//产品-美元价
					// Map<String,Float> transFeeMap=psiProductService.findTransportAvgPrice(productName);
					 Map<String,Float> transFeeMap=psiTransportPaymentService.findAvgAirFee();
					 if(purchasePrice!=null&&purchasePrice.get(productName)!=null){
						 Map<String,Double> avgPriceMap=Maps.newHashMap(); 
						 for (String country: fbaFeeMap.keySet()) {
							 float price=purchasePrice.get(productName);
							 Integer commission=15;
							 float duty=0f;
							 float vat=0f;
							 float tranFee=0f;
							 commission=(commissionMap.get(country)==null?15:commissionMap.get(country));
							 String temp = country.toUpperCase();
							 if("UK".equals(temp)){
									temp = "GB";
							 }
							 if("COM".equals(temp)){
									temp = "US";
							 }
							 CountryCode vatCode = CountryCode.valueOf(temp);
							 if(vatCode!=null){
								vat=1/(1+(vatCode.getVat()/100f));
							 }
							 float rate=0f;	
							 float cnyToUsd=AmazonProduct2Service.getRateConfig().get("USD/CNY");
							 if("de,fr,it,es".contains(country)){
								 rate=AmazonProduct2Service.getRateConfig().get("EUR/USD");
								 duty=(product.getEuCustomDuty()==null?0:product.getEuCustomDuty());
								 if(transFeeMap!=null&&transFeeMap.get("EU")!=null){
									 tranFee=transFeeMap.get("EU")/cnyToUsd/rate*product.getTranGw();
								 }else{
									 tranFee=ProductPrice.sky.get(country)/cnyToUsd/rate*product.getTranGw();
								 }
								 price=price/rate;
							 }else if("ca".equals(country)){
								 rate=AmazonProduct2Service.getRateConfig().get("CAD/USD");
								 duty=(product.getCaCustomDuty()==null?0:product.getCaCustomDuty());
								 if(transFeeMap!=null&&transFeeMap.get("US")!=null){
									 tranFee=transFeeMap.get("US")/cnyToUsd/rate*product.getTranGw();
								 }else{
									 tranFee=ProductPrice.sky.get(country)/cnyToUsd/rate*product.getTranGw();
								 }
								 price=price/rate;
							 }else if("uk".equals(country)){
								 duty=(product.getEuCustomDuty()==null?0:product.getEuCustomDuty());
								 rate=AmazonProduct2Service.getRateConfig().get("GBP/USD");
								 if(transFeeMap!=null&&transFeeMap.get("EU")!=null){
									 tranFee=transFeeMap.get("EU")/cnyToUsd/rate*product.getTranGw();
								 }else{
									 tranFee=ProductPrice.sky.get(country)/cnyToUsd/rate*product.getTranGw();
								 }
								 price=price/rate;
							 }else if("jp".equals(country)){
								 duty=(product.getJpCustomDuty()==null?0:product.getJpCustomDuty());
								 rate=AmazonProduct2Service.getRateConfig().get("USD/JPY");
								 if(transFeeMap!=null&&transFeeMap.get("JP")!=null){
									 tranFee=transFeeMap.get("JP")/cnyToUsd*rate*product.getTranGw();
								 }else{
									 tranFee=ProductPrice.sky.get(country)/cnyToUsd*rate*product.getTranGw();
								 }
								 price=price*rate;
							 }else if("mx".equals(country)){
								 duty=(product.getMxCustomDuty()==null?0:product.getMxCustomDuty());
								 rate=AmazonProduct2Service.getRateConfig().get("MXN/USD");
								 if(transFeeMap!=null&&transFeeMap.get("US")!=null){
									 tranFee=transFeeMap.get("US")/cnyToUsd/rate*product.getTranGw();
								 }else{
									 tranFee=ProductPrice.sky.get(country)/cnyToUsd/rate*product.getTranGw();
								 }
								 price=price/rate;
							 }else if("com".equals(country)){
								 duty=(product.getUsCustomDuty()==null?0:product.getUsCustomDuty());
								 rate=1f;
								 if(transFeeMap!=null&&transFeeMap.get("US")!=null){
									 tranFee=transFeeMap.get("US")/cnyToUsd*product.getTranGw();
								 }else{
									 tranFee=ProductPrice.sky.get(country)/cnyToUsd*product.getTranGw();
								 }
							 }
							
							 float avgPrice=(price*(1+duty/100)+tranFee+fbaFeeMap.get(country))/(vat-(commission/100f));
							 avgPriceMap.put(country,avgPrice+avgPrice*0.1);
						 }
						 model.addAttribute("avgPriceMap", avgPriceMap);
					 }
				 }
			 }
		}
		List<PurchasePlanItem> productPositionByProductName = purchasePlanService.getProductPositionByProductName(productName);
		if(productPositionByProductName.size()>0){
		model.addAttribute("purchasePlan", productPositionByProductName.get(0).getPlan());
		}
		model.addAttribute("priceLogMap", priceLogMap);
		
		Map<String,Integer> returnMap=removalOrderService.getReturningByProductName(productName);
		model.addAttribute("returnMap", returnMap);
		
		model.addAttribute("color", color);
		model.addAttribute("productName", productName);
		model.addAttribute("product", product);
		model.addAttribute("skuMap", skuMap);
		model.addAttribute("asinMap", asinMap);
		model.addAttribute("fbas", fbas);
		model.addAttribute("fbaTran", fbaTran);
		model.addAttribute("fancha", fancha);
		model.addAttribute("transportTypeMap", psiProductAttributeService.findtransportType());
		model.addAttribute("warnNum", warningLetterService.countByProductName(productName));
		return "modules/amazoninfo/productInfoDetail";
	}
	
	@RequestMapping(value ="getCharts")
	public String getCharts(String productName,String startDate,String endDate,String searchType,String selCountry,Model model){
		SaleReport saleReport=new SaleReport();
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("searchType", searchType);
		
		if(StringUtils.isBlank(selCountry)){
			selCountry="de";
		}
		model.addAttribute("selCountry", selCountry);
		try {
			saleReport.setStart(formatDay.parse(startDate));
			saleReport.setEnd(formatDay.parse(endDate));
			saleReport.setCountry(selCountry);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		saleReport.setSearchType(searchType);
		productName = HtmlUtils.htmlUnescape(productName);
		List<String> priceXAxis  = Lists.newArrayList();
		Map<String, String> priceXAxisMap = Maps.newHashMap();
		Date start = saleReport.getStart();
		Date end = saleReport.getEnd();
		Date today=new Date();
		while(end.after(start)||end.equals(start)){
			String key = formatDay.format(start);
			priceXAxis.add(key);
			if("2".equals(saleReport.getSearchType())){	//周
				priceXAxisMap.put(key, formatWeek.format(start) + "周");
			}else if("3".equals(saleReport.getSearchType())){
				priceXAxisMap.put(key, formatMonth.format(start) + "月");
			} else {
				priceXAxisMap.put(key, key);
			}
			start = DateUtils.addDays(start, 1);
			if(formatDay.format(start).equals(formatDay.format(today))){
				break;
			}
		}
		List<String> xAxis  = Lists.newArrayList();
		start = saleReport.getStart();
		if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
			start = DateUtils.addDays(start, -1);
		}
		 end = saleReport.getEnd();
		while(end.after(start)||end.equals(start)){
			if("2".equals(saleReport.getSearchType())){
				String key = formatWeek.format(start);
				int year =DateUtils.getSunday(start).getYear()+1900;
				int week =  Integer.parseInt(key.substring(4));
				if(week==53){
	                year =DateUtils.getMonday(start).getYear()+1900;
			    }
				if(week<10){
					key = year+"0"+week;
				}else{
					key =year+""+week;
				}
				xAxis.add(key);
				start = DateUtils.addWeeks(start, 1);
			}else if("3".equals(saleReport.getSearchType())){
				String key = formatMonth.format(start);
				xAxis.add(key);
				start = DateUtils.addMonths(start, 1);
			}else{
				String key = formatDay.format(start);
				xAxis.add(key);
				start = DateUtils.addDays(start, 1);
			}
		}
		List<String> sessionXAxis  = Lists.newArrayList();	//构建单独的session图表X轴,解决数据延时
		String dateStr = formatDay.format(DateUtils.addDays(today, -2));	//设置零界点
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		if ("2".equals(saleReport.getSearchType())) {	//周三以后显示当前周
			if (calendar.get(Calendar.DAY_OF_WEEK) > 3) {	
				dateStr = formatWeek.format(today);
			} else {
				dateStr = formatWeek.format(DateUtils.addWeeks(today, -1));
			}
		} else if ("3".equals(saleReport.getSearchType())) {
			if (calendar.get(Calendar.DAY_OF_MONTH) > 2) {	//每月三号以后显示当前月	
				dateStr = formatMonth.format(today);
			} else {
				dateStr = formatMonth.format(DateUtils.addMonths(today, -1));
			}
		}else{
			model.addAttribute("eventMap", psiProductService.findEvent(productName,startDate,endDate));
		}
		for (String string : xAxis) {
			if (Integer.parseInt(dateStr) >= Integer.parseInt(string)) {
				sessionXAxis.add(string);
			}
		}
		model.addAttribute("sessionXAxis", sessionXAxis);
		model.addAttribute("priceXAxisMap",priceXAxisMap);
		model.addAttribute("priceXAxis", priceXAxis);
		model.addAttribute("productName", productName);
		model.addAttribute("priceXAxis", priceXAxis);
		//List<String> productNameList = psiProductEliminateService.findIsSaleProductName();
		//if(productNameList.contains(productName)){
	   		model.addAttribute("productIsSale", "1");
	   		Map<String,List<AmazonCatalogRank>> catalogMap2=amazonPostsDetailService.getCatalogName2(productName,saleReport.getStart(),saleReport.getEnd(),selCountry);
			if(catalogMap2!=null&&catalogMap2.size()>0){
				Map<String,Map<String,AmazonCatalogRank>> rankMap2=amazonPostsDetailService.getRank2(productName,saleReport.getStart(),saleReport.getEnd(),selCountry);
				model.addAttribute("rankMap", rankMap2);
				model.addAttribute("catalogMap", catalogMap2);
				Map<String,Integer> salesMap=saleReportService.getSalesByLine(productName,saleReport.getStart(),saleReport.getEnd(),selCountry);
				model.addAttribute("salesMap", salesMap);
			}
	//	}	
		
		//产品信息
				PsiProduct product = psiProductService.findProductByName(productName);
				if(product!=null){
					Map<String, Set<String>> asinMap = Maps.newHashMap();
					String color = "";
					if(productName.indexOf("_")>0){
						color = productName.substring(productName.indexOf("_")+1);
					}
					Set<String> skus = Sets.newHashSet();
					Set<String> asins = Sets.newHashSet();
					Map<String, List<PsiBarcode>> barMap = product.getDupBarcodeMap().get(color);	
					List<PsiBarcode> barcodeList=barMap.get(selCountry);
					Set<String> asinSet=Sets.newHashSet();
					asinMap.put(selCountry, asinSet);
					if(barcodeList!=null){
						for (PsiBarcode psiBarcode : barcodeList) {
							for (PsiSku psiSku : psiBarcode.getSkus()) {
								skus.add(psiSku.getSku());
								String asin = psiSku.getAsin();
								if(StringUtils.isNotBlank(asin)){
									asins.add(asin);
									asinSet.add(asin);
								}
							}
						}	
					}
					
				/*	for (Map.Entry<String, PsiBarcode> entry : barMap.entrySet()) {
						String country = entry.getKey();
						Set<String> asinSet=Sets.newHashSet();
						asinMap.put(country, asinSet);
						for (PsiSku psiSku : entry.getValue().getSkus()) {
							skus.add(psiSku.getSku());
							String asin = psiSku.getAsin();
							if(StringUtils.isNotBlank(asin)){
								asins.add(asin);
								asinSet.add(asin);
							}
						}
					
					}*/
					model.addAttribute("asinMap",asinMap);
					if(skus.size()>0){
						model.addAttribute("hisPriceMap",productHistoryPriceService.find(skus,saleReport.getStart(),saleReport.getEnd(),selCountry));
					}
					if(asins.size()>0){
						model.addAttribute("sessions", businessReportService.findCountProductsData(asins,saleReport));	//支持周、月
						Map<String, Map<String, Integer>> adsMap=businessReportService.getAdsQuantity(skus,saleReport);
						Map<String, Map<String, Integer>> amsMap=businessReportService.getAmsQuantity(productName,saleReport);
						Map<String, Map<String, Integer>> adsData=Maps.newHashMap();
						for (Map.Entry<String,Map<String,Integer>> rs: adsMap.entrySet()) {
							String country=rs.getKey();
							for (Map.Entry<String,Integer> temp: rs.getValue().entrySet()) {
								String date=temp.getKey();
								Integer ads=temp.getValue();
								
								Map<String, Integer> adsTemp=adsData.get(country);
								if(adsTemp==null){
									adsTemp=Maps.newHashMap();
									adsData.put(country, adsTemp);
								}
								if(amsMap!=null&&amsMap.get(country)!=null&&amsMap.get(country).get(date)!=null){
									adsTemp.put(date, ads+amsMap.get(country).get(date));
								}else{
									adsTemp.put(date, ads);
								}
							}
						}
						
						for (Map.Entry<String,Map<String,Integer>> rs: amsMap.entrySet()) {
							String country=rs.getKey();
							for (Map.Entry<String,Integer> temp: rs.getValue().entrySet()) {
								String date=temp.getKey();
								if(adsMap==null||adsMap.get(country)==null||adsMap.get(country).get(date)==null){
									Integer ads=temp.getValue();
									Map<String, Integer> adsTemp=adsData.get(country);
									if(adsTemp==null){
										adsTemp=Maps.newHashMap();
										adsData.put(country, adsTemp);
									}
									adsTemp.put(date, ads);
								}
							}
						}
						
						model.addAttribute("adsData",adsData);
						model.addAttribute("amsMap",amsMap);
						model.addAttribute("adsMap",adsMap);
					}
				}
				model.addAttribute("saleReport",saleReport);
				AmazonNewReleasesRank amazonNewReleasesRank =new AmazonNewReleasesRank();
				amazonNewReleasesRank.setProductName(productName);
				amazonNewReleasesRank.setQueryTime(saleReport.getStart());
				amazonNewReleasesRank.setEndTime(saleReport.getEnd());
				amazonNewReleasesRank.setCountry(selCountry);
				Map<String,Map<String,Map<String,AmazonNewReleasesRank>>>  newReleasesRank=amazonPostsDetailService.findNewReleasesRank2(amazonNewReleasesRank);
				model.addAttribute("newReleasesRank",newReleasesRank);
				
		        return "modules/amazoninfo/productInfoCharts";
	}
	
	@ResponseBody
	@RequestMapping(value ="getReturnGoods")
	public Map<String,Object> getReturnGoods(String productName,Date start,Date end){
		Map<String,Object> map=Maps.newHashMap();
		productName = HtmlUtils.htmlUnescape(productName);
		Map<String, Object[]>  returnGoods = returnGoodsService.findReturnCommentInfo(start,end, productName);
		map.put("returnGoods", returnGoods);
		return map;
	}

	@ResponseBody
	@RequestMapping(value ="getAmazonDetail")
	public Map<String,List<PsiAttrDto>> getAmazonDetail(String productName){
		return psiProductService.findBarcodeAndSku(productName);
	}
	
	@ResponseBody
	@RequestMapping(value ="getShipddQuantityAvailable")
	public String getShipddQuantityAvailable(String stockCode,String country,String productName){
		Map<String,PsiInventoryInnerDto>  dto =   psiInventoryService.getInventoryByNameCountryWarehouseCodeSum(productName, country, stockCode);
		Map<String,PsiInventoryInnerDto>  dto1  = null;
		if("CN".equalsIgnoreCase(stockCode)){
			dto1 = Maps.newHashMap();
		}else{
			dto1 =  psiInventoryService.getTransporttingByNameAndCountrySum(productName, country, stockCode);
		}
		Map<String,PsiInventoryInnerDto>  rs = Maps.newLinkedHashMap(); 
		List<Object> set =psiInventoryService.getSkusInStock(productName, country);
		
		int i = 0 ;
		for (Object skuStr : set) {
			String sku = skuStr.toString();
			if(i==0){
				int num = psiInventoryService.getProductFbaWorking(sku,stockCode);
				int quantity = dto.get(sku)==null?0:dto.get(sku).getQuantity();
				int tran = dto1.get(sku)==null?0:dto1.get(sku).getQuantity();
				
				Integer pack = amazonProduct2Service.findProductPackBySku(sku);
				
				rs.put(sku, new PsiInventoryInnerDto(null, null,(quantity-num), null, null, sku, null, pack, tran, null,null,null,null,null,null));
				i++;
			}else{
				rs.put(sku,new PsiInventoryInnerDto());
			}
		}
		return JSON.toJSONString(rs);
	}
	
	@ResponseBody
	@RequestMapping(value ="getAllProductNames")
	public String getAllProductNames(){
		return JSON.toJSONString(amazonProduct2Service.findAllProductNames());
	}
	
	@ResponseBody
	@RequestMapping(value ="getUnionProductNames")
	public String getUnionProductNames(){
		return JSON.toJSONString(amazonProduct2Service.findUnionProductNames());
	}
	
	@ResponseBody
	@RequestMapping(value ="getShipddQuantityAvailableBySku")
	public String getQuantityAvailableBySku(String sku,String stockCode,String country){
		int num = psiInventoryService.getProductFbaWorking(sku,stockCode);
		int quantity = psiInventoryService.getInventoryBySku(sku,stockCode);
		int tran = 0;
		if(!"CN".equalsIgnoreCase(stockCode)){
			tran = psiInventoryService.getTransporttingBySku(sku);
		}
		return JSON.toJSONString(new PsiInventoryInnerDto(null, null,(quantity-num), null, null, sku, null, amazonProduct2Service.findProductPackBySku(sku), tran, null,null,null,null,null,null));
	}
	
	@RequestMapping(value = "inventoryWarnSaleExport")
	public String inventoryWarnSaleExport(String country,String type,Model model, HttpServletRequest request, HttpServletResponse response){
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
		List<PsiProduct> list = psiProductService.findIsComponents("0");
		Map<String, PsiInventoryTotalDto> producting = psiInventoryService.getProducingQuantity();
		Map<String, PsiInventoryTotalDto> transportting = psiInventoryService.getTransporttingQuantity().get("1");
		Map<String, PsiInventoryTotalDto> transportting0 = psiInventoryService.getTransporttingQuantity().get("0");
		Map<String, PsiInventoryTotalDtoByInStock> inventorys = psiInventoryService.getInventoryQuantity();
		Map<String, PsiInventoryFba>  fbas = psiInventoryService.getAllProductFbaInfo();
		Map<String,Integer> fbaTran = psiInventoryService.getAllFbaTransporttingByNameAndCountry();
		Map<String,Map<String,Object>> productsMoqAndPrice = psiInventoryService.getProductsMoqAndPrice();
		Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();
		
		List<PsiProductAttribute> attrs = psiProductAttributeService.findAll();
		Map<String,PsiProductAttribute> productAttr = Maps.newHashMap();
		for (PsiProductAttribute psiProductAttribute : attrs) {
			productAttr.put(psiProductAttribute.getColorName(), psiProductAttribute);
		}

            List<String> title = Lists.newArrayList("产品","新品","在售","产","中国仓","待发货","途","海外仓","FBA总","FBA途","总库存","库存上限","MOQ","装箱数","31日销","平均日销","滚动31日销库销比","预测库销比");
            if(SecurityUtils.getSubject().isPermitted("psi:inventory:stockPriceView")){
            	title.add("单价($)");
            	title.add("总价($)");
            	title.add("金额($)");
//			}else{
//				title.add("单价($)");
//            	title.add("金额($)");
//            	//销售价格
//    			if(this.salePrice==null||this.salePrice.size()==0){
//    				salePrice.cleanUp();
//    				salePrice.putAll(this.amazonProduct2Service.getSalePriceDollers());
//    			}
			}
            for(int i = 0; i < title.size(); i++){
				cell = row.createCell(i);
				cell.setCellStyle(style);
				cell.setCellValue(title.get(i));
				sheet.autoSizeColumn((short)i);
			}
            Float totalPrice=0f;
            for (PsiProduct psiProduct : list) {
        		List<String> nameWithColorList=psiProduct.getProductNameWithColor();
        		for (String nameWithColor2 : nameWithColorList) {
        			 int total=(fbas.get(nameWithColor2)==null?0:fbas.get(nameWithColor2).getTotal())+(producting.get(nameWithColor2)==null?0:(producting.get(nameWithColor2).getQuantity()))
		                		+(transportting.get(nameWithColor2)==null?0:transportting.get(nameWithColor2).getQuantity())+(inventorys.get(nameWithColor2)==null?0:inventorys.get(nameWithColor2).getTotalQuantityCN())
		                		+(inventorys.get(nameWithColor2)==null?0:inventorys.get(nameWithColor2).getTotalQuantityNotCN());
        			totalPrice+=(productsMoqAndPrice.get(nameWithColor2)==null||productsMoqAndPrice.get(nameWithColor2).get("price")==null)?0:((BigDecimal)productsMoqAndPrice.get(nameWithColor2).get("price")).floatValue()*total;
        		}
            }	
            int rowIndex=1;

    	    //产品定位
    	    Map<String, String> productPositionMap = psiProductEliminateService.findProductPositionByCountry(null);
    	   	List<String> isNewList = psiProductEliminateService.findIsNewProductName();
    	   	
			Map<String,Integer> maxQuantity = psiProductAttributeService.getAllMaxInventory();
    	   	
        	for (PsiProduct psiProduct : list) {
        		List<String> nameWithColorList=psiProduct.getProductNameWithColor();
        		Map<String,Float> incProPartsPrice=psiProduct.getTempPartsTotalMap();
        		for (String nameWithColor : nameWithColorList) {
        			int j=0;
        			row=sheet.createRow(rowIndex++);
	        		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(nameWithColor);
	        		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(isNewList.contains(nameWithColor)?"新品":"普通");
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(DictUtils.getDictLabel(productPositionMap.get(nameWithColor), "product_position", ""));
	        		if(producting.get(nameWithColor)!=null&&producting.get(nameWithColor).getQuantity()!=null&&producting.get(nameWithColor).getQuantity()>0){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(producting.get(nameWithColor).getQuantity());
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	        		if(inventorys.get(nameWithColor)!=null&&inventorys.get(nameWithColor).getTotalQuantityCN()>0){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getTotalQuantityCN());
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	        		
	        		if(transportting0.get(nameWithColor)!=null&&transportting0.get(nameWithColor).getQuantity()!=null&&transportting0.get(nameWithColor).getQuantity()>0){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(transportting0.get(nameWithColor).getQuantity());
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	        		
	        		if(transportting.get(nameWithColor)!=null&&transportting.get(nameWithColor).getQuantity()!=null&&transportting.get(nameWithColor).getQuantity()>0){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(transportting.get(nameWithColor).getQuantity());
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	        		if(inventorys.get(nameWithColor)!=null&&inventorys.get(nameWithColor).getTotalQuantityNotCN()>0){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getTotalQuantityNotCN());
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	        		
	        	    List<String> countryName=Lists.newArrayList("_de","_uk","_fr","_it","_es","_com","_ca","_jp");
	        		int fbasTotal=0;
	        		for (String name : countryName) {
						fbasTotal+=fbas.get(nameWithColor+name)==null?0:(fbas.get(nameWithColor+name).getTotal());
					}
	        		if(fbasTotal>0){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(fbasTotal);
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	                int fbaTranTotal=0;
	                for (String name : countryName) {
	                	fbaTranTotal+=fbaTran.get(nameWithColor+name)==null?0:(fbaTran.get(nameWithColor+name));
					}
	            	if(fbaTranTotal>0){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(fbaTranTotal);
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	                int total=(fbas.get(nameWithColor)==null?0:fbas.get(nameWithColor).getTotal())+(producting.get(nameWithColor)==null?0:(producting.get(nameWithColor).getQuantity()))
	                		+(transportting.get(nameWithColor)==null?0:transportting.get(nameWithColor).getQuantity())+(inventorys.get(nameWithColor)==null?0:inventorys.get(nameWithColor).getTotalQuantityCN())
	                		+(inventorys.get(nameWithColor)==null?0:inventorys.get(nameWithColor).getTotalQuantityNotCN());
	                if(total>0){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(total);
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	                //库存上限
	            	if (maxQuantity != null && maxQuantity.get(nameWithColor) != null) {
	            		row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(maxQuantity.get(nameWithColor));
	            	}else{
	            		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	            	}
	                if(productsMoqAndPrice.get(nameWithColor)!=null&&productsMoqAndPrice.get(nameWithColor).get("moq")!=null){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue((Integer) productsMoqAndPrice.get(nameWithColor).get("moq"));
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	                if(psiProduct.getPackQuantity()!=null){
	                	row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(psiProduct.getPackQuantity());
	                }else{
	                	row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	                }
	                float fanchaTotal=0;
	                for (String name : countryName) {
	                	fanchaTotal+=fancha.get(nameWithColor+name)==null?0:(fancha.get(nameWithColor+name).getDay31Sales());
					}
	                if(fanchaTotal>0){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(fanchaTotal);
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	                if(fanchaTotal/31 >0.5){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(fanchaTotal/31));
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	                if(fanchaTotal>0){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(total/fanchaTotal*10)/10.0);
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	                //预测库销比
	                if(productAttr.get(nameWithColor) != null && productAttr.get(nameWithColor).getInventorySaleMonth() > 0){
	        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(String.format("%.1f", productAttr.get(nameWithColor).getInventorySaleMonth()));
	        		}else{
	        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	        		}
	                if(SecurityUtils.getSubject().isPermitted("psi:inventory:stockPriceView")){
	                	if(productsMoqAndPrice.get(nameWithColor)!=null&&productsMoqAndPrice.get(nameWithColor).get("price")!=null){
		        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(((BigDecimal) productsMoqAndPrice.get(nameWithColor).get("price")).doubleValue());
		        		}else{
		        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
		        		}
	                	Float singlePrice=0f;
	                	if(productsMoqAndPrice.get(nameWithColor)!=null&&productsMoqAndPrice.get(nameWithColor).get("price")!=null){
	                	    if(incProPartsPrice!=null&&incProPartsPrice.get(nameWithColor)!=null){
	                	    	singlePrice=incProPartsPrice.get(nameWithColor)+((BigDecimal)productsMoqAndPrice.get(nameWithColor).get("price")).floatValue();
	                			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(singlePrice);
	                		}else{
	                			singlePrice=((BigDecimal)productsMoqAndPrice.get(nameWithColor).get("price")).floatValue();
	                			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(singlePrice);
	                		}
	                	}else{
	                		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
	                	}
	               
	                	row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(singlePrice*total);
	    			}else{
	    				//没权限的就显示销售单价和总金额
						cacheMap=salePrice.asMap();
						Float salePrice = cacheMap.get(nameWithColor);
						if (salePrice!= null) {
							row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(salePrice);
							row.getCell(j - 1).setCellStyle(contentStyle);
						} else {
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(contentStyle);
						}
						
						if (salePrice!= null) {
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(salePrice*total);
								row.getCell(j - 1).setCellStyle(cellStyle);
						} else {
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(cellStyle);
						}
	    			}
				}
			}
        	for (int i=1;i<rowIndex;i++) {
	        	 for (int j = 0; j < title.size(); j++) {
	        		 if(j==title.size()-1||j==title.size()-2){
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
				String fileName = "库存预警总计表" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "redirect:"+Global.getAdminPath()+"/psi/psiInventory/inventoryWarn";
	}
	
	@RequestMapping(value = "inventoryWarnExport")
	public String inventoryWarnExport(String country,String type,Model model, HttpServletRequest request, HttpServletResponse response){
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
		if(StringUtils.isNotEmpty(type)){
			try {
				type = URLDecoder.decode(type, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		 }
		
		//获得德国码  库存预留值 
		//Map<String, Object[]> residueMap =this.psiProductService.getResidueMap();
		Map<String, String> nameTypeMap = psiProductService.findProductTypeMap();
		Map<String, String> typeLineMap = groupDictService.getTypeLine(null);
		
	    if("eu".equals(country)){
			List<PsiProduct> list = psiProductService.findAllByCountry(country,type);
			Map<String, PsiInventoryTotalDto> producting = psiInventoryService.getProducingQuantityEu();
			//Map<String, PsiInventoryTotalDto> transportting = psiInventoryService.getTransporttingQuantityEu();
			
			Map<String, PsiInventoryTotalDto> transportting = psiInventoryService.getTransporttingQuantity().get("1");
			Map<String, PsiInventoryTotalDto> transportting0 = psiInventoryService.getTransporttingQuantity().get("0");
			
			
			Map<String, PsiInventoryTotalDtoByInStock> inventorys = psiInventoryService.getInventoryQuantityEu();
			Map<String, Integer> fbaWorkingByEuro =  psiInventoryService.getFbaWorkingByEuro();
			Map<String, PsiInventoryFba>  fbas = psiInventoryService.getAllProductFbaInfo(country);
			Map<String,Integer> fbaTran = psiInventoryService.getAllFbaTransporttingByNameAndCountry(country);
			Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();
			
		    Map<String, PsiProductAttribute> attrMap = Maps.newHashMap();
		    List<PsiProductAttribute> attrList = psiProductAttributeService.findAll();
		    for (PsiProductAttribute psiProductAttribute : attrList) {
		    	attrMap.put(psiProductAttribute.getColorName(), psiProductAttribute);
			}
		    Map<String, Map<String, Integer>> bufferPeriodMap = psiProductEliminateService.findBufferPeriod();
				
			List<String>  title=Lists.newArrayList("产品","产品线","新品","产品定位","采购周","最近采购周","产","中国仓","待发货","途","实","可","翻","旧","坏","实","途","总","实","途","总","实","途","总","实","途","总","实","途","总",
					"31Sell","FBA可销天","采购期预日销","销售期预月销","量","天","下单点","结余","库存可销天","下单量","空运补货量","周期");
			for(int i = 0; i < title.size(); i++){
				cell = row.createCell(i);
				cell.setCellStyle(style);
				if(i<11||(i>30&&i<34)||i>35){
					cell.setCellValue(title.get(i));
				}
				if(i==11){cell.setCellValue("德国仓");}
				if(i==16){cell.setCellValue("德国FBA");}
				if(i==19){cell.setCellValue("英国FBA");}
				if(i==22){cell.setCellValue("法国FBA");}
				if(i==25){cell.setCellValue("意大利FBA");}
				if(i==28){cell.setCellValue("西班牙FBA");}
				if(i==34){cell.setCellValue("安全库存");}
				if(i==15){sheet.addMergedRegion(new CellRangeAddress(0, 0,11,15));}
				if(i==18){sheet.addMergedRegion(new CellRangeAddress(0, 0,16,18));}
				if(i==21){sheet.addMergedRegion(new CellRangeAddress(0, 0, 19,21));}
				if(i==24){sheet.addMergedRegion(new CellRangeAddress(0, 0, 22,24));}
				if(i==27){sheet.addMergedRegion(new CellRangeAddress(0, 0, 25,27));}
				if(i==30){sheet.addMergedRegion(new CellRangeAddress(0, 0, 28,30));}
				if(i==35){sheet.addMergedRegion(new CellRangeAddress(0, 0, 34,35));}
				sheet.autoSizeColumn((short)i);
			}
			row=sheet.createRow(1);
			for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellStyle(style);
				if ((i >= 11 && i <= 30) || i == 34 || i == 35) {
					cell.setCellValue(title.get(i));
				}
				if (i <= 10 || i == 31 || i == 32 || i == 33 || i == 34
						|| i >= 36) {
					sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
				}
				sheet.autoSizeColumn((short) i);
			}
			int  rowIndex=2;
    		
    	   	//产品淘汰分平台、颜色
    	   	List<String> isNewList = psiProductEliminateService.findIsNewProductName();
    		Map<String, String> isSaleEuMap = psiProductEliminateService.findProductPositionByCountry(Lists.newArrayList("de","uk","fr","it","es"));
    		
		    Map<Integer, String> yearWeek = Maps.newHashMap();
		    Date date = new Date();
			yearWeek.put(0, DateUtils.getWeekOfYearStr(date));
			yearWeek.put(1, DateUtils.getWeekOfYearStr(DateUtils.addWeeks(date, 1)));
			yearWeek.put(2, DateUtils.getWeekOfYearStr(DateUtils.addWeeks(date, 2)));
			yearWeek.put(3, DateUtils.getWeekOfYearStr(DateUtils.addWeeks(date, 3)));
			
			Map<Integer,String> orderDateMap = purchaseOrderService.getLastOrderDate();
			for (PsiProduct psiProduct : list) {
				List<String> nameWithColorList=psiProduct.getProductNameWithColor();
				for (String nameWithColor : nameWithColorList) {
					String fangChaKey = nameWithColor + "_eu";
					int m = 0;
					int total = 0;
					row=sheet.createRow(rowIndex++);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nameWithColor);
					//产品线
					if (StringUtils.isNotBlank(nameTypeMap.get(nameWithColor)) && StringUtils.isNotBlank(typeLineMap.get(nameTypeMap.get(nameWithColor).toLowerCase()))) {
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(typeLineMap.get(nameTypeMap.get(nameWithColor).toLowerCase())+"线");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(isNewList.contains(nameWithColor)?"新品":"普通");
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(DictUtils.getDictLabel(isSaleEuMap.get(nameWithColor), "product_position", ""));
        			
        			//采购周
        			if(attrMap.get(nameWithColor) != null && attrMap.get(nameWithColor).getPurchaseWeek() != null){
        				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(yearWeek.get(attrMap.get(nameWithColor).getPurchaseWeek()));
        			} else {
        				row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
        			}
        			//最近采购周
        			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(orderDateMap.get(psiProduct.getId()));
		    		//在产
					if(producting.get(nameWithColor)==null||producting.get(nameWithColor).getQuantityEuro()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
					     row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(producting.get(nameWithColor).getQuantityEuro());
					     total+=producting.get(nameWithColor).getQuantityEuro();
					}
					//中国仓
					if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getQuantityEuro()==null||inventorys.get(nameWithColor).getQuantityEuro().get("CN")==null||inventorys.get(nameWithColor).getQuantityEuro().get("CN").getNewQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getQuantityEuro().get("CN").getNewQuantity());
						 total+=inventorys.get(nameWithColor).getQuantityEuro().get("CN").getNewQuantity();
					}
					//待发货
					if (transportting0.get(nameWithColor) == null) {
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						 row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(transportting0.get(nameWithColor).getQuantityEuro());
					}
					//在途
					int transportVar = 0;
					if (transportting.get(nameWithColor) == null) {
						row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
					} else {
						transportVar = transportting.get(nameWithColor).getQuantityEuro();
						row.createCell(m++, Cell.CELL_TYPE_NUMERIC).setCellValue(transportVar);
						total += transportVar;
					}
					//海外仓
					int deNew = 0;
					if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getQuantityEuro()==null||inventorys.get(nameWithColor).getQuantityEuro().get("DE")==null||inventorys.get(nameWithColor).getQuantityEuro().get("DE").getNewQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						deNew = inventorys.get(nameWithColor).getQuantityEuro().get("DE").getNewQuantity();
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(deNew);
						total += deNew;
						if(fbaWorkingByEuro.get(nameWithColor)!=null){
							row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getQuantityEuro().get("DE").getNewQuantity()-fbaWorkingByEuro.get(nameWithColor));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getQuantityEuro().get("DE").getNewQuantity());
						}
					}
					if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getQuantityEuro()==null||inventorys.get(nameWithColor).getQuantityEuro().get("DE")==null||inventorys.get(nameWithColor).getQuantityEuro().get("DE").getRenewQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getQuantityEuro().get("DE").getRenewQuantity());
					}
					if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getQuantityEuro()==null||inventorys.get(nameWithColor).getQuantityEuro().get("DE")==null||inventorys.get(nameWithColor).getQuantityEuro().get("DE").getOldQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getQuantityEuro().get("DE").getOldQuantity());
					}
					if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getQuantityEuro()==null||inventorys.get(nameWithColor).getQuantityEuro().get("DE")==null||inventorys.get(nameWithColor).getQuantityEuro().get("DE").getBrokenQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getQuantityEuro().get("DE").getBrokenQuantity());
					}
					List<String> countryName=Lists.newArrayList("_de","_uk","_fr","_it","_es");
					int totalFba=0;
					int totalFbaShi=0;	//fba实
					for (String name : countryName) {
						if(fbas.get(nameWithColor+name)==null||fbas.get(nameWithColor+name).getFulfillableQuantity()<=0){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}else{
							int fbaShi = fbas.get(nameWithColor+name).getFulfillableQuantity();
							row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fbaShi);
							totalFbaShi += fbaShi;
						}
						if(fbaTran.get(nameWithColor+name)==null||fbaTran.get(nameWithColor+name)<=0){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}else{
							row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fbaTran.get(nameWithColor+name));
						}
						if(fbas.get(nameWithColor+name)==null||fbas.get(nameWithColor+name).getTotal()<=0){
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}else{
							row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fbas.get(nameWithColor+name).getTotal());
							totalFba+=fbas.get(nameWithColor+name).getTotal();
						}
					}
					if(fancha.get(fangChaKey)==null||fancha.get(fangChaKey).getDay31Sales()==null){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fancha.get(fangChaKey).getDay31Sales());
						
					}
					double safe = 0; //安全库存
					if(fancha.get(fangChaKey)!=null && MathUtils.roundUp(fancha.get(fangChaKey).getPeriodSqrt()*fancha.get(fangChaKey).getVariance()*2.33)>0){
						safe = fancha.get(fangChaKey).getPeriodSqrt()*fancha.get(fangChaKey).getVariance()*2.33;
					}
					//fba可销天
					if(fancha.get(fangChaKey)==null||fancha.get(fangChaKey).getDay31Sales()==null||fancha.get(fangChaKey).getDay31Sales()/31.0==0){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round((totalFbaShi-safe)/(fancha.get(fangChaKey).getDay31Sales()/31.0)));
					}
					//采购期预日销
					if(fancha.get(fangChaKey)==null||fancha.get(fangChaKey).getForecastPreiodAvg()==null||fancha.get(fangChaKey).getForecastPreiodAvg()<=0){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(fancha.get(fangChaKey).getForecastPreiodAvg()));
					}
					//采购期预月销
					if(fancha.get(fangChaKey)==null||fancha.get(fangChaKey).getForecastAfterPreiodSalesByMonth()==null||fancha.get(fangChaKey).getForecastAfterPreiodSalesByMonth()<=0){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(fancha.get(fangChaKey).getForecastAfterPreiodSalesByMonth()));
					}
					//安全库存
					if(safe<=0){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(safe));
					}
					//安全库存可销天
					double safeDay=0;
					if(fancha.get(fangChaKey)!=null&&fancha.get(fangChaKey).getForecastPreiodAvg()!=null&&fancha.get(fangChaKey).getForecastPreiodAvg()>0 && safe>0){
						safeDay=safe/fancha.get(fangChaKey).getForecastPreiodAvg();
					}else if(fancha.get(fangChaKey)!=null&&fancha.get(fangChaKey).getDay31Sales()!=null&&fancha.get(fangChaKey).getDay31Sales()>0 && safe>0){
						safeDay=safe/(fancha.get(fangChaKey).getDay31Sales()/31.0);
					}
					if(safeDay>0){
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(safeDay));
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
					int period = 0;
					if(fancha.get(fangChaKey)!=null&&fancha.get(fangChaKey).getPeriod()>0){
						period = fancha.get(fangChaKey).getPeriod();
					}
            	    if (bufferPeriodMap.get(nameWithColor) != null && bufferPeriodMap.get(nameWithColor).get("de") != null && period > 0) {
            	    	period += bufferPeriodMap.get(nameWithColor).get("de");	//缓冲周期欧洲以德国为准
					}
					double sale=0;
//					if(fancha.get(fangChaKey)!=null&&fancha.get(fangChaKey).getForecastPreiodAvg()!=null&&fancha.get(fangChaKey).getForecastPreiodAvg()>0){
//						sale= (total+totalFba)/fancha.get(fangChaKey).getForecastPreiodAvg()-period-safeDay;
//					}else if(fancha.get(fangChaKey)!=null&&fancha.get(fangChaKey).getDay31Sales()!=null&&fancha.get(fangChaKey).getDay31Sales()>0&&fancha.get(fangChaKey).getDay31Sales()/31.0>0){
//						sale=(total+totalFba)/(fancha.get(fangChaKey).getDay31Sales()/31.0)-period-safeDay;
//					}
					//下单点
					double point=0;
					try{
						if (fancha.get(fangChaKey)!=null && fancha.get(fangChaKey).getForecastPreiodAvg() != null) {
							point = fancha.get(fangChaKey).getForecastPreiodAvg() * period + safe;
						}else if (fancha.get(fangChaKey)!=null && fancha.get(fangChaKey).getDay31Sales() != null) {
							point = (fancha.get(fangChaKey).getDay31Sales()/31d) * period + safe;
						}
					}catch (Exception e) {}
					if(point>0){
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(point));
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}

					//结余
					double jy = total + totalFba - point;
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(jy));
					if(fancha.get(fangChaKey)!=null&&fancha.get(fangChaKey).getForecastAfterPreiodSalesByMonth()!=null){
            		    sale = jy / (fancha.get(fangChaKey).getForecastAfterPreiodSalesByMonth() / 31);
				    }else if(fancha.get(fangChaKey)!=null&&fancha.get(fangChaKey).getDay31Sales()!=null&& fancha.get(fangChaKey).getDay31Sales()>0){
				    	sale = jy / (fancha.get(fangChaKey).getDay31Sales() / 31.0);
				    }
					
					//库存可销天
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(sale));
					
					//下单量
					if (jy < 0) {
				    	row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(
				    			MathUtils.roundUp((Math.abs(jy))/psiProduct.getPackQuantity())*psiProduct.getPackQuantity());
					} else {
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
					
					int sky=0;
					if(fancha.get(fangChaKey)!=null){
						int tempTotal = totalFba;
						if(fancha.get(fangChaKey).getForecastPreiodAvg()!=null){
							sky=(period -(psiProduct.getProducePeriod()==null?0:psiProduct.getProducePeriod()))
									*MathUtils.roundUp(fancha.get(fangChaKey).getForecastPreiodAvg())-tempTotal-deNew-transportVar;
						}else if(fancha.get(fangChaKey).getDay31Sales()!=null){
							sky=(period -(psiProduct.getProducePeriod()==null?0:psiProduct.getProducePeriod()))
									*MathUtils.roundUp(fancha.get(fangChaKey).getDay31Sales()/31.0)-tempTotal-deNew-transportVar;
						}
					}
					//空运补货量
					if(sky>0){
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(sky*1.0/psiProduct.getPackQuantity())*psiProduct.getPackQuantity());
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
					//周期(包含缓冲周期)
					if (period > 0) {
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(period);
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
				}
			}
			for (int i = 2; i < rowIndex; i++) {
				for (int j = 0; j < title.size(); j++) {
					sheet.getRow(i).getCell(j).setCellStyle(contentStyle);
				}
			}
			for (int i = 0; i < title.size(); i++) {
				sheet.autoSizeColumn((short) i, true);
			}
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
				String fileName = "库存预警欧洲总计表" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(StringUtils.isBlank(country)){//分国家导出
			List<PsiProduct> list =psiProductService.findAllByCountry(country,type);
			Map<String, PsiInventoryTotalDto> producting = psiInventoryService.getProducingQuantity();
			Map<String, PsiInventoryTotalDto> transportting = psiInventoryService.getTransporttingQuantity().get("1");
			Map<String, PsiInventoryTotalDto> transportting0 = psiInventoryService.getTransporttingQuantity().get("0");
			Map<String, PsiInventoryTotalDtoByInStock> inventorys = psiInventoryService.getInventoryQuantity();
			Map<String, PsiInventoryFba>  fbas = psiInventoryService.getAllProductFbaInfo();
			Map<String,Integer> fbaTran = psiInventoryService.getAllFbaTransporttingByNameAndCountry();
			Map<String,Map<String,Object>> productsMoqAndPrice = psiInventoryService.getProductsMoqAndPrice();
			Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();
			SalesForecastByMonth salesForecast=new SalesForecastByMonth();
			Date today = DateUtils.getLastDayOfMonth(DateUtils.getDateStart(new Date()));
			salesForecast.setDataDate(today);
			Date start = salesForecast.getDataDate();
			Date end = DateUtils.addMonths(salesForecast.getDataDate(), 6);
			//Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAll(salesForecast,start,end);
			Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAllWithType(start,end);
			Map<String,Integer> maxQuantity=psiProductAttributeService.getAllMaxInventory();
			List<String> newTowMonth=psiProductEliminateService.findNewAfterTwoMonth();
			List<String> dates = Lists.newArrayList();
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
	            List<String> title = Lists.newArrayList("产品","产品线","MOQ","装箱数","新品","淘汰","采购周","最近采购周","周期","国家","在产","中国仓","待发货","途","海外仓","FBA总","FBA途","总库存","库存上限","31日销","库存可销天","FBA可销天","采购期预日销","销售期预月销","量(安全库存)","天(安全库存)","下单点","结余","下单量","空运补货量","周转率","年周转率");
	            if(SecurityUtils.getSubject().isPermitted("psi:inventory:stockPriceView")){
	            	title.add("单价($)");
	            	title.add("总价($)");
	            	title.add("金额($)");
         	   }/*else{
         		 //title.add("单价($)");
	             // title.add("金额($)");
	            //销售价格
	  			if(salePrice==null || salePrice.size()==0){
	  				salePrice.cleanUp();
	  				salePrice.putAll(this.amazonProduct2Service.getSalePriceDollers());
	  			}
         	   }*/
	            for (int i = 0; i < 6; i++) {
					dates.add(monthFormat.format(DateUtils.addMonths(start,i)));
					title.add(monthFormat.format(DateUtils.addMonths(start,i)));
				}
	            for(int i = 0; i < title.size(); i++){
					cell = row.createCell(i);
					cell.setCellStyle(style);
					cell.setCellValue(title.get(i));
					sheet.autoSizeColumn((short)i);
				}
	          
	            int rowIndex=1;
	            String keyStock="";
			    List<String> sortCountry=Lists.newArrayList("de","fr","it","es","uk","com","ca","jp");
				
			   	//产品定位分平台、颜色
			    Map<String, String> productPositionMap = psiProductEliminateService.findAllProductPosition();
			    Map<String, String> productIsNewMap = psiProductEliminateService.findIsNewMap();
			    Map<String, PsiProductAttribute> attrMap = Maps.newHashMap();
			    List<PsiProductAttribute> attrList = psiProductAttributeService.findAll();
			    for (PsiProductAttribute psiProductAttribute : attrList) {
			    	attrMap.put(psiProductAttribute.getColorName(), psiProductAttribute);
				}
			    Map<String, Map<String, Integer>> bufferPeriodMap = psiProductEliminateService.findBufferPeriod();
			    Map<Integer, String> yearWeek = Maps.newHashMap();
			    Date date = new Date();
				yearWeek.put(0, DateUtils.getWeekOfYearStr(date));
				yearWeek.put(1, DateUtils.getWeekOfYearStr(DateUtils.addWeeks(date, 1)));
				yearWeek.put(2, DateUtils.getWeekOfYearStr(DateUtils.addWeeks(date, 2)));
				yearWeek.put(3, DateUtils.getWeekOfYearStr(DateUtils.addWeeks(date, 3)));
				
				Map<Integer,String> orderDateMap = purchaseOrderService.getLastOrderDate();
					
	        	for (PsiProduct psiProduct : list) {
	        		List<String> nameWithColorList=psiProduct.getProductNameWithColor();
	        		Map<String,Float> incProPartsPrice=psiProduct.getTempPartsTotalMap();
	        		List<String> countryList = Arrays.asList(psiProduct.getPlatform().split(","));
	        		boolean fanOu = "0".equals(psiProduct.getHasPower()) ? true : false;//泛欧产品欧洲国家合并显示
	        		for (String nameWithColor : nameWithColorList) {
	        			for (String saleCountry : sortCountry) {
			                if(countryList.contains(saleCountry)){
		        				if("fr,de,uk,es,it".contains(saleCountry)){
		        					keyStock="DE";
		        				}else if("com,ca".contains(saleCountry)){
		        					keyStock="US";
		        				}else if("jp".contains(saleCountry)){
		        					keyStock="JP";
		        				}
		        				//筛选,泛欧产品欧洲国家汇总导出
		        				if (fanOu && "fr,uk,es,it".contains(saleCountry)) {
		        					continue;
		        				}
		        				String key = nameWithColor + "_" + saleCountry;
		        				/*if ("0".equals(productIsSaleMap.get(key))) {	//淘汰产品不导出
		        					continue;
								}*/
		        				String fangChaKey = key;
		        				if (fanOu && "de".equals(saleCountry)) {
		        					fangChaKey = nameWithColor + "_eu";
								}
			        			int j=0;
			        			int total=0;
			        			row=sheet.createRow(rowIndex++);
			        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(nameWithColor);
								//产品线
								if (StringUtils.isNotBlank(nameTypeMap.get(nameWithColor)) && StringUtils.isNotBlank(typeLineMap.get(nameTypeMap.get(nameWithColor).toLowerCase()))) {
									row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(typeLineMap.get(nameTypeMap.get(nameWithColor).toLowerCase())+"线");
								}else{
									row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
								}
				            	//MOQ
				            	if(productsMoqAndPrice.get(nameWithColor)!=null&&productsMoqAndPrice.get(nameWithColor).get("moq")!=null){
					        		row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue((Integer) productsMoqAndPrice.get(nameWithColor).get("moq"));
					        	}else{
					        		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					        	}
				            	//装箱数
				            	if(psiProduct.getPackQuantity()!=null){
				            		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(psiProduct.getPackQuantity());
				            	}else{
				            		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				            	}
			        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("0".equals(productIsNewMap.get(key))?"普通":"新品");
			        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("4".equals(productPositionMap.get(key))?"淘汰":"在售");
			        			
			        			//采购周
			        			if(attrMap.get(nameWithColor) != null && attrMap.get(nameWithColor).getPurchaseWeek() != null){
			        				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(yearWeek.get(attrMap.get(nameWithColor).getPurchaseWeek()));
			        			} else {
			        				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			        			}
			        			//最近采购周
			        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(orderDateMap.get(psiProduct.getId()));
			            	    int bufferPeriod = 0;	//缓冲周期
			            	    if (bufferPeriodMap.get(nameWithColor) != null && bufferPeriodMap.get(nameWithColor).get(country) != null) {
			            	    	bufferPeriod = bufferPeriodMap.get(nameWithColor).get(country);
								}
								//周期
			            	    Integer period=0;
								if(fancha.get(fangChaKey)!=null&&fancha.get(fangChaKey).getPeriod()>0){
									period=fancha.get(fangChaKey).getPeriod() + bufferPeriod;
									row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(period);
								}else{
									row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
								}
			        			//国家
			        			String countryString = "com".equals(saleCountry) ? "us" : saleCountry;
			        			if (fanOu && "de".equals(countryString)) {
			        				countryString = "eu";
								}
			        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(countryString);
								//在产
								Integer productingNum = 0;
								try {
									if (fanOu && "de".equals(saleCountry)) {
										try {
											productingNum += producting.get(nameWithColor).getInventorys().get("de").getQuantity();
										} catch (NullPointerException e) {}
										try {
											productingNum += producting.get(nameWithColor).getInventorys().get("fr").getQuantity();
										} catch (NullPointerException e) {}
										try {
											productingNum += producting.get(nameWithColor).getInventorys().get("uk").getQuantity();
										} catch (NullPointerException e) {}
										try {
											productingNum += producting.get(nameWithColor).getInventorys().get("it").getQuantity();
										} catch (NullPointerException e) {}
										try {
											productingNum += producting.get(nameWithColor).getInventorys().get("es").getQuantity();
										} catch (NullPointerException e) {}
									} else {
										productingNum = producting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity();
									}
								} catch (NullPointerException e) {}
								if(productingNum == 0){
									 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
								}else{
								     row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(productingNum);
								     total += productingNum;
								}
								//中国仓
								Integer cn = 0;
								try {
									if (fanOu && "de".equals(saleCountry)) {
										try {
											cn += inventorys.get(nameWithColor).getInventorys().get("de").getQuantityInventory().get("CN").getNewQuantity();
										} catch (NullPointerException e) {}
										try {
											cn += inventorys.get(nameWithColor).getInventorys().get("fr").getQuantityInventory().get("CN").getNewQuantity();
										} catch (NullPointerException e) {}
										try {
											cn += inventorys.get(nameWithColor).getInventorys().get("uk").getQuantityInventory().get("CN").getNewQuantity();
										} catch (NullPointerException e) {}
										try {
											cn += inventorys.get(nameWithColor).getInventorys().get("it").getQuantityInventory().get("CN").getNewQuantity();
										} catch (NullPointerException e) {}
										try {
											cn += inventorys.get(nameWithColor).getInventorys().get("es").getQuantityInventory().get("CN").getNewQuantity();
										} catch (NullPointerException e) {}
									} else {
										cn = inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("CN").getNewQuantity();
									}
								} catch (NullPointerException e) {}
								if(cn == 0){
									row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
								}else{
									row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(cn);
									total+=cn;
								}
								//待发货
								Integer temp = 0;
								try {
									if (fanOu && "de".equals(saleCountry)) {
										try {
											temp += transportting0.get(nameWithColor).getInventorys().get("de").getQuantity();
										} catch (NullPointerException e) {}
										try {
											temp += transportting0.get(nameWithColor).getInventorys().get("uk").getQuantity();
										} catch (NullPointerException e) {}
										try {
											temp += transportting0.get(nameWithColor).getInventorys().get("fr").getQuantity();
										} catch (NullPointerException e) {}
										try {
											temp += transportting0.get(nameWithColor).getInventorys().get("it").getQuantity();
										} catch (NullPointerException e) {}
										try {
											temp += transportting0.get(nameWithColor).getInventorys().get("es").getQuantity();
										} catch (NullPointerException e) {}
									} else {
										temp = transportting0.get(nameWithColor).getInventorys().get(saleCountry).getQuantity();
									}
								} catch (NullPointerException e) {}
								if (temp == 0) {
									 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
								}else{
									 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(temp);
								}
								//在途
								int transportVar=0;
								try {
									if (fanOu && "de".equals(saleCountry)) {
										try {
											transportVar += transportting.get(nameWithColor).getInventorys().get("de").getQuantity();
										} catch (NullPointerException e) {}
										try {
											transportVar += transportting.get(nameWithColor).getInventorys().get("uk").getQuantity();
										} catch (NullPointerException e) {}
										try {
											transportVar += transportting.get(nameWithColor).getInventorys().get("fr").getQuantity();
										} catch (NullPointerException e) {}
										try {
											transportVar += transportting.get(nameWithColor).getInventorys().get("it").getQuantity();
										} catch (NullPointerException e) {}
										try {
											transportVar += transportting.get(nameWithColor).getInventorys().get("es").getQuantity();
										} catch (NullPointerException e) {}
									} else {
										transportVar = transportting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity();
									}
								} catch (NullPointerException e) {}
								if(transportVar==0){
									 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
								}else{
									 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(transportVar);
									 total += transportVar;
								}
	                            int allTotal = total;
								//海外仓
								int deNew=0;
								try {
									if (fanOu && "de".equals(saleCountry)) {
										try {
											deNew += inventorys.get(nameWithColor).getInventorys().get("de").getQuantityInventory().get(keyStock).getNewQuantity();
										} catch (NullPointerException e) {}
										try {
											deNew += inventorys.get(nameWithColor).getInventorys().get("fr").getQuantityInventory().get(keyStock).getNewQuantity();
										} catch (NullPointerException e) {}
										try {
											deNew += inventorys.get(nameWithColor).getInventorys().get("uk").getQuantityInventory().get(keyStock).getNewQuantity();
										} catch (NullPointerException e) {}
										try {
											deNew += inventorys.get(nameWithColor).getInventorys().get("it").getQuantityInventory().get(keyStock).getNewQuantity();
										} catch (NullPointerException e) {}
										try {
											deNew += inventorys.get(nameWithColor).getInventorys().get("es").getQuantityInventory().get(keyStock).getNewQuantity();
										} catch (NullPointerException e) {}
									} else {
										deNew = inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get(keyStock).getNewQuantity();
									}
									total += deNew;
								} catch (NullPointerException e) {}
								allTotal = allTotal + deNew;
								//海外仓
								if (deNew > 0) {
									row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(deNew);
								} else {
									row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
								}
				        		//FBA总
								int fba = 0;
								if (fanOu && "de".equals(saleCountry)) {
									try {
										fba += fbas.get(nameWithColor + "_de").getTotal();
									} catch (NullPointerException e) {}
									try {
										fba += fbas.get(nameWithColor + "_fr").getTotal();
									} catch (NullPointerException e) {}
									try {
										fba += fbas.get(nameWithColor + "_uk").getTotal();
									} catch (NullPointerException e) {}
									try {
										fba += fbas.get(nameWithColor + "_it").getTotal();
									} catch (NullPointerException e) {}
									try {
										fba += fbas.get(nameWithColor + "_es").getTotal();
									} catch (NullPointerException e) {}
								} else {
									try {
										fba = fbas.get(key).getTotal();
									} catch (NullPointerException e) {}
								}
								total += fba;
								
			            	   //安全库存量&可销天&下单点
								double safe = 0; //安全库存量
				            	double safeDay=0; //安全库存可销天
								try {
									safe = fancha.get(fangChaKey).getPeriodSqrt()*fancha.get(fangChaKey).getVariance()*2.33;
									if (safe > 0) {
										try{
											if (fancha.get(fangChaKey).getForecastPreiodAvg() != null && fancha.get(fangChaKey).getForecastPreiodAvg() > 0) {
												safeDay = MathUtils.roundUp(safe/fancha.get(fangChaKey).getForecastPreiodAvg());
											}else if (fancha.get(fangChaKey).getDay31Sales() != null && fancha.get(fangChaKey).getDay31Sales() > 0) {
												safeDay = MathUtils.roundUp(safe/(fancha.get(fangChaKey).getDay31Sales()/31d));
											}
										}catch (NullPointerException e) {}
									}
								} catch (NullPointerException e) {}

				                //FBA实
				                int fbaShiTotal=0;
				                if (fanOu && "de".equals(saleCountry)) {
									try {
										fbaShiTotal += fbas.get(nameWithColor + "_de").getFulfillableQuantity();
									} catch (NullPointerException e) {}
									try {
										fbaShiTotal += fbas.get(nameWithColor + "_fr").getFulfillableQuantity();
									} catch (NullPointerException e) {}
									try {
										fbaShiTotal += fbas.get(nameWithColor + "_uk").getFulfillableQuantity();
									} catch (NullPointerException e) {}
									try {
										fbaShiTotal += fbas.get(nameWithColor + "_it").getFulfillableQuantity();
									} catch (NullPointerException e) {}
									try {
										fbaShiTotal += fbas.get(nameWithColor + "_es").getFulfillableQuantity();
									} catch (NullPointerException e) {}
								} else {
									try {
										fbaShiTotal = fbas.get(key).getFulfillableQuantity();
									} catch (NullPointerException e) {}
								}
								double fbaDay=0;	//FBA可销天
								if (fbaShiTotal <= 0) {
									row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
								} else {
									row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(fba);
									if(fancha.get(fangChaKey)!=null&&fancha.get(fangChaKey).getDay31Sales()!=null&&(fancha.get(fangChaKey).getDay31Sales()/31.0)>0){
										fbaDay = (fbaShiTotal-safe)/(fancha.get(fangChaKey).getDay31Sales()/31.0);
									}
								}
				                //FBA在途
				                int fbaTranTotal=0;
				                if (fanOu && "de".equals(saleCountry)) {
									try {
										fbaTranTotal += fbaTran.get(nameWithColor + "_de");
									} catch (NullPointerException e) {}
									try {
										fbaTranTotal += fbaTran.get(nameWithColor + "_fr");
									} catch (NullPointerException e) {}
									try {
										fbaTranTotal += fbaTran.get(nameWithColor + "_uk");
									} catch (NullPointerException e) {}
									try {
										fbaTranTotal += fbaTran.get(nameWithColor + "_it");
									} catch (NullPointerException e) {}
									try {
										fbaTranTotal += fbaTran.get(nameWithColor + "_es");
									} catch (NullPointerException e) {}
								} else {
									try {
										fbaTranTotal = fbaTran.get(key);
									} catch (NullPointerException e) {}
								}
	                            if (fbaTranTotal > 0) {
				        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(fbaTranTotal);
				        		}else{
				        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				        		}
				            	//总库存
				            	if(total>0){
				        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(total);
				        		}else{
				        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				        		}
				            	//库存上限
				            	if (maxQuantity != null && maxQuantity.get(nameWithColor) != null) {
				            		row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(maxQuantity.get(nameWithColor));
				            	}else{
				            		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				            	}
				            	//31日销
			            	   if(fancha.get(fangChaKey)!=null&&fancha.get(fangChaKey).getDay31Sales()!=null&&fancha.get(fangChaKey).getDay31Sales()>0){
			                    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(fancha.get(fangChaKey).getDay31Sales());
								}else{
									row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
								}
									
			            	    double sale=0;
			            	    double point=0;	//下单点
								try{
									if (fancha.get(fangChaKey)!=null && fancha.get(fangChaKey).getForecastPreiodAvg() != null) {
										point = fancha.get(fangChaKey).getForecastPreiodAvg() * (fancha.get(fangChaKey).getPeriod() + bufferPeriod) + safe;
									}else if (fancha.get(fangChaKey)!=null && fancha.get(fangChaKey).getDay31Sales() != null) {
										point = (fancha.get(fangChaKey).getDay31Sales()/31d) * (fancha.get(fangChaKey).getPeriod() + bufferPeriod) + safe;
									}
								}catch (Exception e) {}
				            	
								double jy = allTotal + fba - point;
								if(fancha.get(fangChaKey)!=null&&fancha.get(fangChaKey).getForecastAfterPreiodSalesByMonth()!=null){
			            		    sale = jy / (fancha.get(fangChaKey).getForecastAfterPreiodSalesByMonth() / 31);
							    }else if(fancha.get(fangChaKey)!=null&&fancha.get(fangChaKey).getDay31Sales()!=null){
							    	sale = jy / (fancha.get(fangChaKey).getDay31Sales() / 31.0);
							    }
								//库存可销天
								row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(sale));
								//FBA可销天	
				                  if(fbaDay>0){
				                	  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(Math.round(fbaDay));
				                  }else{
				                	  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
				                  }
				                  //采购期预日销
				                   if(fancha.get(fangChaKey)!=null && fancha.get(fangChaKey).getForecastPreiodAvg()!=null){
				                	   row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(fancha.get(fangChaKey).getForecastPreiodAvg()));
									}else{
										row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
									}
					                  //采购期预月销
									if(fancha.get(fangChaKey)!=null && fancha.get(fangChaKey).getForecastAfterPreiodSalesByMonth()!=null){
										row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(fancha.get(fangChaKey).getForecastAfterPreiodSalesByMonth()));
									}else{
										row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
									}
									//安全库存量
									if(safe > 0){
										row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(safe));
									}else{
										row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
									}
									//安全库存可销天
									if(safeDay>0){
										row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(safeDay));
									}else{
										row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
									}
									//下单点
									if(point>0){
										row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(point));
									}else{
										row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
									}
									//结余
								    if(jy!=0){
										row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(jy));
									}else{
										row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
									}
									//下单量
								    if (jy < 0) {
								    	row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(
								    			MathUtils.roundUp((Math.abs(jy))/psiProduct.getPackQuantity())*psiProduct.getPackQuantity());
									} else {
										row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
									}
									
									int sky=0;
									if(fancha.get(fangChaKey)!=null){
										int tempTotal = fba;
										if(fancha.get(fangChaKey).getForecastPreiodAvg()!=null){
											sky=(fancha.get(fangChaKey).getPeriod() + bufferPeriod -(psiProduct.getProducePeriod()==null?0:psiProduct.getProducePeriod()))
													*MathUtils.roundUp(fancha.get(fangChaKey).getForecastPreiodAvg())-tempTotal-deNew-transportVar;
										}else if(fancha.get(fangChaKey).getDay31Sales()!=null){
											sky=(fancha.get(fangChaKey).getPeriod() + bufferPeriod -(psiProduct.getProducePeriod()==null?0:psiProduct.getProducePeriod()))
													*MathUtils.roundUp(fancha.get(fangChaKey).getDay31Sales()/31.0)-tempTotal-deNew-transportVar;
										}
									}
									//空运补货量
									if(sky>0){
										row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(sky*1.0/psiProduct.getPackQuantity())*psiProduct.getPackQuantity());
									}else{
										row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
									}
									
									if (!"4".equals(productPositionMap.get(key))&&!newTowMonth.contains(key)) {	//淘汰产品不导出
										double tempSafeDay=0;
										Integer tempPeriod=0;
										String tempKey="";
										//fancha[tempKey].period+(empty productAttr[name].bufferPeriod ? 0: productAttr[name].bufferPeriod)
										if("de,fr,it,es,uk,jp".contains(saleCountry)){
											tempKey=nameWithColor+"_eu";
										}else if("com,ca".contains(saleCountry)){
											tempKey=nameWithColor+"_com";
										}
										if(fancha!=null&&fancha.get(tempKey)!=null&&fancha.get(tempKey).getPeriod()>0){
											tempPeriod=fancha.get(tempKey).getPeriod();
										}
										tempPeriod=tempPeriod+bufferPeriod;
										
										Double tempSafe =0d;
										if(fancha.get(tempKey)!=null && MathUtils.roundUp(fancha.get(tempKey).getPeriodSqrt()*fancha.get(tempKey).getVariance()*2.33)>0){
											tempSafe = fancha.get(tempKey).getPeriodSqrt()*fancha.get(tempKey).getVariance()*2.33;
										}
										if (tempSafe > 0) {
											if (fancha.get(tempKey).getForecastPreiodAvg() != null && fancha.get(tempKey).getForecastPreiodAvg() > 0) {
												tempSafeDay = MathUtils.roundUp(tempSafe/fancha.get(tempKey).getForecastPreiodAvg());
											}else if (fancha.get(tempKey).getDay31Sales() != null && fancha.get(tempKey).getDay31Sales() > 0) {
												tempSafeDay = MathUtils.roundUp(tempSafe/(fancha.get(tempKey).getDay31Sales()/31d));
											}
										}
										
										if(MathUtils.roundUp(tempSafeDay)+tempPeriod>0 ){
											row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ratio*365/(MathUtils.roundUp(tempSafeDay)+tempPeriod)/12);
											row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(365f/(MathUtils.roundUp(tempSafeDay)+tempPeriod));
										}else{
											row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
											row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
										}
									}else{
										row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
										row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
									}
									
									if(SecurityUtils.getSubject().isPermitted("psi:inventory:stockPriceView")){
										if(productsMoqAndPrice.get(nameWithColor)!=null&&productsMoqAndPrice.get(nameWithColor).get("price")!=null){
						        			row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(((BigDecimal) productsMoqAndPrice.get(nameWithColor).get("price")).doubleValue());
						        		}else{
						        			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
						        		}
										Float singlePrice=0f;
					                	if(productsMoqAndPrice.get(nameWithColor)!=null&&productsMoqAndPrice.get(nameWithColor).get("price")!=null){
					                	    if(incProPartsPrice!=null&&incProPartsPrice.get(nameWithColor)!=null){
					                	    	singlePrice=incProPartsPrice.get(nameWithColor)+((BigDecimal)productsMoqAndPrice.get(nameWithColor).get("price")).floatValue();
					                			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(singlePrice);
					                		}else{
					                			singlePrice=((BigDecimal)productsMoqAndPrice.get(nameWithColor).get("price")).floatValue();
					                			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(singlePrice);
					                		}
					                	}else{
					                		row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					                	}
					               
					                	row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(singlePrice*total);
					               
						         	}
									
									for(String time:dates){
										if(data.get(nameWithColor)!=null&&data.get(nameWithColor).get(saleCountry)!=null&&data.get(nameWithColor).get(saleCountry).get(time)!=null&&data.get(nameWithColor).get(saleCountry).get(time).getQuantityForecast()!=null){
											row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(data.get(nameWithColor).get(saleCountry).get(time).getQuantityForecast());
										}else{
											row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(0);
										}
									}
			                }
						}
	        		}
				}
	        	
	          
	        	
        	//  if(SecurityUtils.getSubject().isPermitted("psi:inventory:stockPriceView")){
		        	for (int i=1;i<rowIndex;i++) {
			        	 for (int j = 0; j < title.size(); j++) {
				        	  sheet.getRow(i).getCell(j).setCellStyle(contentStyle);
				        	  if("金额($)".equals(title.get(j))||"总价($)".equals(title.get(j))||"年周转率".equals(title.get(j))||"周转率".equals(title.get(j))){
				        		  sheet.getRow(i).getCell(j).setCellStyle(cellStyle);
					          }
						 }
			        	
			         }
		        	//}
	        	
	        	
				/*  for (int i = 0; i < title.size(); i++) {
		        		 sheet.autoSizeColumn((short)i, true);
				  }*/
				try {
					request.setCharacterEncoding("UTF-8");
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/x-download");
					SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
					String fileName = "库存预警分国家总计表" + sdf.format(new Date()) + ".xls";
					fileName = URLEncoder.encode(fileName, "UTF-8");
					response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
					OutputStream out = response.getOutputStream();
					wb.write(out);
					out.close();
					return null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			
		}else{
			List<PsiProduct> list = psiProductService.findAllByCountry(country,type);
			Map<String, PsiInventoryTotalDto> producting = psiInventoryService.getProducingQuantity(country);
			//Map<String, PsiInventoryTotalDto> transportting = psiInventoryService.getTransporttingQuantity(country);
			Map<String, PsiInventoryTotalDto> transportting = psiInventoryService.getTransporttingQuantity().get("1");
			Map<String, PsiInventoryTotalDto> transportting0 = psiInventoryService.getTransporttingQuantity().get("0");
			Map<String, PsiInventoryTotalDtoByInStock> inventorys = psiInventoryService.getInventoryQuantity();
			Map<String, Integer> fbaWorkingByEuro =  psiInventoryService.getFbaWorkingByEuro();
			Map<String, PsiInventoryFba>  fbas = psiInventoryService.getAllProductFbaInfo(country);
			Map<String,Integer> fbaTran = psiInventoryService.getAllFbaTransporttingByNameAndCountry(country);
			Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();
			Map<String, Object[]> fbaWorking =  psiInventoryService.getFbaWorking();	 //4
			List<String>  title=Lists.newArrayList("产品","产品线","新品","淘汰","产","中国仓","待发货","途","实","可","翻","旧","坏","实","途","总","补",
					"31日销","FBA可销天","总库存","采购期预日销","销售期预月销","量","天","下单点","结余","下单量","空运补货量","库存可销天","周期");
			if("fr,de,uk,it,es".contains(country)){
				title.add(9, "实(EU)");
				title.add(10, "可(EU)");
				for(int i = 0; i < title.size(); i++){
					cell = row.createCell(i);
					cell.setCellStyle(style);
					if(i<9||(i>19&&i<24)||i>25){
						cell.setCellValue(title.get(i));
					}
					if(i==9){cell.setCellValue("本地仓");}
					if(i==16){cell.setCellValue("FBA仓");}
					if(i==24){cell.setCellValue("安全库存");}
					if(i==15){sheet.addMergedRegion(new CellRangeAddress(0, 0, 9,15));}
					if(i==19){sheet.addMergedRegion(new CellRangeAddress(0, 0, 16,19));}
					if(i==25){sheet.addMergedRegion(new CellRangeAddress(0, 0, 24,25));}
					sheet.autoSizeColumn((short)i);
				}
				row=sheet.createRow(1);
				for(int i = 0; i < title.size(); i++){
					cell = row.createCell(i);
					 cell.setCellStyle(style);
					if((i>=9&&i<=19)||i==24||i==25){
					  cell.setCellValue(title.get(i));
					}  
					if(i<9||(i>=20&&i<=23)||i>=25){
						sheet.addMergedRegion(new CellRangeAddress(0,1, i,i));
					}
					sheet.autoSizeColumn((short)i);
				}
			}else{
				for(int i = 0; i < title.size(); i++){
					cell = row.createCell(i);
					cell.setCellStyle(style);
					if(i<9||(i>=18&&i<=22)||i>=25){
						cell.setCellValue(title.get(i));
					}//
					if(i==9){cell.setCellValue("本地仓");}
					if(i==14){cell.setCellValue("FBA仓");}
					if(i==22){cell.setCellValue("安全库存");}
					if(i==13){sheet.addMergedRegion(new CellRangeAddress(0, 0, 9,13));}
					if(i==17){sheet.addMergedRegion(new CellRangeAddress(0, 0, 14,17));}
					if(i==24){sheet.addMergedRegion(new CellRangeAddress(0, 0, 23,24));}
					sheet.autoSizeColumn((short)i);
				}
				row=sheet.createRow(1);
				for(int i = 0; i < title.size(); i++){
					cell = row.createCell(i);
					 cell.setCellStyle(style);
					if((i>=9&&i<=17)||i==23||i==24){
					  cell.setCellValue(title.get(i));
					}  
					if(i<9||(i>=18&&i<=22)||i>=25){
						sheet.addMergedRegion(new CellRangeAddress(0,1, i,i));
					}
					sheet.autoSizeColumn((short)i);
				}
			}
			
			int  rowIndex=2;
			String keyStock="";
			if("fr,de,uk,es,it".contains(country)){
				keyStock="DE";
			}else if("com,ca".contains(country)){
				keyStock="US";
			}
			
		   	//产品淘汰分平台、颜色
    	   	List<String> isNewList = psiProductEliminateService.findIsNewProductName();
    		//查询所有产品定位,区分平台和颜色 map<产品名_颜色_国家, isSale>
    		Map<String, String> productPositionMap = psiProductEliminateService.findAllProductPosition();
		    Map<String, PsiProductAttribute> attrMap = Maps.newHashMap();
		    List<PsiProductAttribute> attrList = psiProductAttributeService.findAll();
		    for (PsiProductAttribute psiProductAttribute : attrList) {
		    	attrMap.put(psiProductAttribute.getColorName(), psiProductAttribute);
			}
		    Map<String, Map<String, Integer>> bufferPeriodMap = psiProductEliminateService.findBufferPeriod();
			for (PsiProduct psiProduct : list) {
				List<String> nameWithColorList=psiProduct.getProductNameWithColor();
				for (String nameWithColor : nameWithColorList) {
					int m=0;
					int total=0;
					String key=nameWithColor+"_"+country;
					/*if ("0".equals(isSaleMap.get(key))) {	//淘汰品不下单
						continue;
					}*/
					row=sheet.createRow(rowIndex++);
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(nameWithColor);
					//产品线
					if (StringUtils.isNotBlank(nameTypeMap.get(nameWithColor)) && StringUtils.isNotBlank(typeLineMap.get(nameTypeMap.get(nameWithColor).toLowerCase()))) {
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(typeLineMap.get(nameTypeMap.get(nameWithColor).toLowerCase())+"线");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(isNewList.contains(nameWithColor)?"新品":"普通");
					row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("4".equals(productPositionMap.get(key))?"淘汰":"在售");
        			
        			//在产
					if(producting.get(nameWithColor)==null||producting.get(nameWithColor).getInventorys()==null||producting.get(nameWithColor).getInventorys().get(country)==null||producting.get(nameWithColor).getInventorys().get(country).getQuantity()==null||producting.get(nameWithColor).getInventorys().get(country).getQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
					     row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(producting.get(nameWithColor).getInventorys().get(country).getQuantity());
					     total+=producting.get(nameWithColor).getInventorys().get(country).getQuantity();
					}
        			//中国仓
					if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getInventorys()==null||inventorys.get(nameWithColor).getInventorys().get(country)==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory()==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get("CN")==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get("CN").getNewQuantity()==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get("CN").getNewQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get("CN").getNewQuantity());
						 total+=inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get("CN").getNewQuantity();
					}
					//待发货
					if(transportting0.get(nameWithColor)==null||transportting0.get(nameWithColor).getInventorys()==null||transportting0.get(nameWithColor).getInventorys().get(country)==null||transportting0.get(nameWithColor).getInventorys().get(country).getQuantity()==null||transportting0.get(nameWithColor).getInventorys().get(country).getQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						 row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(transportting0.get(nameWithColor).getInventorys().get(country).getQuantity());
					}
					//在途
					int transportVar=0;
					if(transportting.get(nameWithColor)==null||transportting.get(nameWithColor).getInventorys()==null||transportting.get(nameWithColor).getInventorys().get(country)==null||transportting.get(nameWithColor).getInventorys().get(country).getQuantity()==null||transportting.get(nameWithColor).getInventorys().get(country).getQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						 row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(transportting.get(nameWithColor).getInventorys().get(country).getQuantity());
						 total+=transportting.get(nameWithColor).getInventorys().get(country).getQuantity();
						 transportVar=transportting.get(nameWithColor).getInventorys().get(country).getQuantity();
					}
					
					if("fr,de,uk,it,es".contains(country)){
						int euNew=0;
						if(inventorys.get(nameWithColor)!=null&&inventorys.get(nameWithColor).getQuantityEuro()!=null&&inventorys.get(nameWithColor).getQuantityEuro().get("DE")!=null&&inventorys.get(nameWithColor).getQuantityEuro().get("DE").getNewQuantity()!=null&&inventorys.get(nameWithColor).getQuantityEuro().get("DE").getNewQuantity()>0){
							euNew=inventorys.get(nameWithColor).getQuantityEuro().get("DE").getNewQuantity();
							row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(euNew);
						}else{
							 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
						if(euNew-(fbaWorkingByEuro.get(nameWithColor)==null?0:fbaWorkingByEuro.get(nameWithColor))>0||(fbaWorkingByEuro.get(nameWithColor)!=null&&fbaWorkingByEuro.get(nameWithColor)>0)){
					      row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(euNew-(fbaWorkingByEuro.get(nameWithColor)==null?0:fbaWorkingByEuro.get(nameWithColor)));
						}else{
							row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
					}
					int deNew=0;
					if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getInventorys()==null||inventorys.get(nameWithColor).getInventorys().get(country)==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory()==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock)==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getNewQuantity()==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getNewQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getNewQuantity());
						 total+=inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getNewQuantity();
					     deNew=inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getNewQuantity();
					}
					if(deNew-((fbaWorking.get(key)==null||fbaWorking.get(key)[2]==null)?0:((BigDecimal)fbaWorking.get(key)[2])).intValue()!=0){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(deNew-((fbaWorking.get(key)==null||fbaWorking.get(key)[2]==null)?0:((BigDecimal)fbaWorking.get(key)[2])).intValue());
					}else{
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					
					if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getInventorys()==null||inventorys.get(nameWithColor).getInventorys().get(country)==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory()==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock)==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getRenewQuantity()==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getRenewQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getRenewQuantity());
					}
					if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getInventorys()==null||inventorys.get(nameWithColor).getInventorys().get(country)==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory()==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock)==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getOldQuantity()==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getOldQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getOldQuantity());
					}
					if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getInventorys()==null||inventorys.get(nameWithColor).getInventorys().get(country)==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory()==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock)==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getBrokenQuantity()==null||inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getBrokenQuantity()<=0){
						 row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(inventorys.get(nameWithColor).getInventorys().get(country).getQuantityInventory().get(keyStock).getBrokenQuantity());
					}
					
                    if(fbas.get(key)==null||fbas.get(key).getFulfillableQuantity()==null||fbas.get(key).getFulfillableQuantity()<=0){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fbas.get(key).getFulfillableQuantity());
					}
                    if(fbaTran.get(key)==null||fbaTran.get(key)<=0){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fbaTran.get(key));
					}
					double safe=0; //计算安全库存量
					if(fancha.get(key)!=null && MathUtils.roundUp(fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33)>0){
						safe = fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33;
					}
                    if(fbas.get(key)==null||fbas.get(key).getTotal()<=0){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(fbas.get(key).getTotal());
					}
                    double fbaDay=0;	//计算fba可销天
					if(fancha.get(key)!=null&&fancha.get(key).getDay31Sales()!=null&&(fancha.get(key).getDay31Sales()/31.0)>0){
						int fbaShi = 0;
						if (fbas.get(key) != null && fbas.get(key).getFulfillableQuantity()!=null) {
							fbaShi = fbas.get(key).getFulfillableQuantity();
						}
						fbaDay = Math.round((fbaShi-safe)/(fancha.get(key).getDay31Sales()/31.0));
					}
                    row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
                    if(fancha.get(key)!=null&&fancha.get(key).getDay31Sales()!=null&&fancha.get(key).getDay31Sales()>0){
                    	row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(fancha.get(key).getDay31Sales());
                    	row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue(Math.round(fbaDay));
					}else{
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
                    //总库存
                   if((fbas.get(key)==null?0:fbas.get(key).getTotal())+total>0){
                	   row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue((fbas.get(key)==null?0:fbas.get(key).getTotal())+total);
                   }else{
                	   row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
                   }
                   //采购期预日销
                   if(fancha.get(key)==null||fancha.get(key).getForecastPreiodAvg()==null||fancha.get(key).getForecastPreiodAvg()<=0){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(fancha.get(key).getForecastPreiodAvg()));
					}
                   //采购期预月销
					if(fancha.get(key)==null||fancha.get(key).getForecastAfterPreiodSalesByMonth()==null||fancha.get(key).getForecastAfterPreiodSalesByMonth()<=0){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(fancha.get(key).getForecastAfterPreiodSalesByMonth()));
					}
					//安全库存量
					if(fancha.get(key)==null||MathUtils.roundUp(fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33)<=0){
						row.createCell(m++,Cell.CELL_TYPE_STRING).setCellValue("");
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33));
					    safe=MathUtils.roundUp(fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33);
					}

					int period = 0;
					if(fancha.get(key)!=null&&fancha.get(key).getPeriod()>0){
						period = fancha.get(key).getPeriod();
					}
            	    if (bufferPeriodMap.get(nameWithColor) != null && bufferPeriodMap.get(nameWithColor).get(country) != null && period > 0) {
            	    	period += bufferPeriodMap.get(nameWithColor).get(country);
					}
					double safeDay=0;
					double point=0;
					if(fancha.get(key)!=null&&fancha.get(key).getForecastPreiodAvg()!=null&&fancha.get(key).getForecastPreiodAvg()>0&&fancha.get(key).getPeriodSqrt()!=null&&fancha.get(key).getVariance()!=null){
						safeDay=fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33/fancha.get(key).getForecastPreiodAvg();
					}else if(fancha.get(key)!=null&&fancha.get(key).getDay31Sales()!=null&&fancha.get(key).getDay31Sales()>0&&fancha.get(key).getPeriodSqrt()!=null&&fancha.get(key).getVariance()!=null&&fancha.get(key).getDay31Sales()/31.0!=0){
						safeDay=fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33/(fancha.get(key).getDay31Sales()/31.0);
					}
					//安全库存可销天
					if(safe>0){
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(safeDay));
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
					
					if(fancha.get(key)!=null&&fancha.get(key).getForecastPreiodAvg()!=null&&fancha.get(key).getForecastPreiodAvg()>0){
						point=fancha.get(key).getForecastPreiodAvg()*period+safe;
					}else if(fancha.get(key)!=null&&fancha.get(key).getDay31Sales()!=null&&fancha.get(key).getDay31Sales()>0){
						point=(fancha.get(key).getDay31Sales()/31.0)*period+safe;
					}
					//下单点
					if(point>0){
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(point));
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
					//结余
					double jy=total+(fbas.get(key)==null?0:fbas.get(key).getTotal())-point;
				    if(jy!=0){
							row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(jy));
					}else{
							row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}    
					//下单量
					if (jy < 0) {
				    	row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(
				    			MathUtils.roundUp((Math.abs(jy))/psiProduct.getPackQuantity())*psiProduct.getPackQuantity());
					} else {
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}

					//空运补货量
					int sky=0;
					if(fancha.get(key)!=null){
						int tempTotal=0;
						if(fbas.get(key)!=null){
							tempTotal=fbas.get(key).getTotal();
						}
						if(fancha.get(key)!=null&&fancha.get(key).getForecastPreiodAvg()!=null&&fancha.get(key).getForecastPreiodAvg()>0){
							sky=(period-(psiProduct.getProducePeriod()==null?0:psiProduct.getProducePeriod()))
									*MathUtils.roundUp(fancha.get(key).getForecastPreiodAvg())-tempTotal-deNew-transportVar;
						}else if(fancha.get(key)!=null&&fancha.get(key).getDay31Sales()!=null&&fancha.get(key).getDay31Sales()>0){
							sky=(period-(psiProduct.getProducePeriod()==null?0:psiProduct.getProducePeriod()))
									*MathUtils.roundUp(fancha.get(key).getDay31Sales()/31.0)-tempTotal-deNew-transportVar;
						}
					}
					if(sky>0){
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(sky*1.0/psiProduct.getPackQuantity())*psiProduct.getPackQuantity());
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
					double sale=0;
					if(fancha.get(key)!=null&&fancha.get(key).getForecastAfterPreiodSalesByMonth()!=null&&fancha.get(key).getForecastAfterPreiodSalesByMonth()>0){
						   sale=jy/(fancha.get(key).getForecastAfterPreiodSalesByMonth()/31);
					}else if(fancha.get(key)!=null&&fancha.get(key).getDay31Sales()!=null&&fancha.get(key).getDay31Sales()>0){
						   sale=jy/(fancha.get(key).getDay31Sales()/31.0);
					}
					//库存可销天
					row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(Math.round(sale));
					
					//周期(含缓冲周期)
					if(period>0){
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue(period);
					}else{
						row.createCell(m++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
					}
				}
			}
			for (int i=2;i<rowIndex;i++) {
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
				String fileName = "库存预警"+country+"总计表" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiInventory/inventoryWarn";
	}
	
	@RequestMapping(value = "inventoryPriceExport")
	public String inventoryPriceExport(String country,String type,Model model, HttpServletRequest request, HttpServletResponse response){
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
		if (StringUtils.isNotEmpty(type)) {
			try {
				type = URLDecoder.decode(type, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		// 获得德国码 库存预留值
		// Map<String, Object[]> residueMap =this.psiProductService.getResidueMap();
		List<PsiProduct> list = psiProductService.findAllByCountry(country, type);
		Map<String, PsiInventoryTotalDto> producting = psiInventoryService.getProducingQuantity();
		Map<String, PsiInventoryTotalDto> transportting = psiInventoryService.getTransporttingQuantity().get("1");
		Map<String, PsiInventoryTotalDtoByInStock> inventorys = psiInventoryService.getInventoryQuantity();
		Map<String, Map<String, Object>> productsMoqAndPrice = psiInventoryService.getProductsMoqAndPrice();
		SalesForecastByMonth salesForecast = new SalesForecastByMonth();
		Date today = DateUtils.getLastDayOfMonth(DateUtils.getDateStart(new Date()));
		Map<String, PsiInventoryFba> fbas = psiInventoryService.getAllProductFbaInfo();
		salesForecast.setDataDate(today);
		Map<String, Float> piPriceMap = psiInventoryService.getPiPrice(country, DateUtils.addMonths(new Date(), -1));
		float rate = 1;	//实时汇率
		if ("eu".equals(country)) {
			rate = MathUtils.getRate("EUR", "USD", null);
		} else if ("ca".equals(country)) {
			rate = MathUtils.getRate("CAD", "USD", null);
		} else if ("jp".equals(country)) {
			rate = MathUtils.getRate("JPY", "USD", null);
		}

		//库存为排除在产后的库存
		List<String> title = Lists.newArrayList("Product Name", "Total Inventory(Not included in production)");
		if (SecurityUtils.getSubject().isPermitted("psi:inventory:stockPriceView")) {
			title.add("PI Price($)");
			title.add("PI Price(€)");
			title.add("Total Amount($)");
			title.add("Total Amount(€)");
		}else{
			title.add("Price($)");
			title.add("Price(€)");
			title.add("Total Amount($)");
			title.add("Total Amount(€)");
			//销售价格
			if(salePrice==null || salePrice.size()==0){
				salePrice.cleanUp();
				salePrice.putAll(this.amazonProduct2Service.getSalePriceDollers());
			}
		}

		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short) i);
		}

		int rowIndex = 1;
		float totalMoney = 0f;
		List<String> sortCountry = Lists.newArrayList();
		if ("eu".equals(country)) {
			sortCountry = Lists.newArrayList("de", "fr", "it", "es", "uk");
		} else {
			sortCountry = Lists.newArrayList(country);
		}
		for (PsiProduct psiProduct : list) {
			List<String> nameWithColorList = psiProduct.getProductNameWithColor();
			Map<String, Float> incProPartsPrice = psiProduct.getTempPartsTotalMap();
			for (String nameWithColor : nameWithColorList) {
				int total = 0;
				int product = 0;
				int j = 0;
				for (String saleCountry : sortCountry) {
					String key = nameWithColor + "_" + saleCountry;
					//在产
					try {
						product += producting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity();
						total += producting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity();
					} catch (NullPointerException e) {}
					//中国仓
					try {
						total += inventorys.get(nameWithColor).getInventorys().get(saleCountry)
								.getQuantityInventory().get("CN").getNewQuantity();
					} catch (NullPointerException e) {}
					//在途
					try {
						total += transportting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity();
					} catch (NullPointerException e) {}
					//海外仓
					try {
						total += inventorys.get(nameWithColor).getInventorys().get(saleCountry)
									.getQuantityInventory().get("DE").getNewQuantity();
					} catch (NullPointerException e) {}
					
					try {
						total += inventorys.get(nameWithColor).getInventorys().get(saleCountry)
									.getQuantityInventory().get("US").getNewQuantity();
					} catch (NullPointerException e) {}
					//fba
					try {
						total += fbas.get(key).getTotal();
					} catch (NullPointerException e) {}
				}
				if (total - product > 0) {
					row = sheet.createRow(rowIndex++);
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(nameWithColor);
					row.getCell(j - 1).setCellStyle(contentStyle);

					row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(total - product);
					row.getCell(j - 1).setCellStyle(contentStyle);

					if (SecurityUtils.getSubject().isPermitted("psi:inventory:stockPriceView")) {
						Float price = 0f;
						if (productsMoqAndPrice.get(nameWithColor) != null
								&& productsMoqAndPrice.get(nameWithColor).get("price") != null) {
							price = ((BigDecimal) productsMoqAndPrice.get(nameWithColor).get("price")).floatValue();
						}
						Float singlePrice = price;
						if (productsMoqAndPrice.get(nameWithColor) != null
								&& productsMoqAndPrice.get(nameWithColor).get("price") != null) {
							if (incProPartsPrice != null && incProPartsPrice.get(nameWithColor) != null) {
								singlePrice += incProPartsPrice.get(nameWithColor);
							}
						}
						if (piPriceMap.get(nameWithColor) != null) {
							singlePrice = piPriceMap.get(nameWithColor) * rate;
						}
						if (singlePrice != null && singlePrice > 0) {
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(singlePrice);
							row.getCell(j - 1).setCellStyle(cellStyle);
							//欧元
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(MathUtils.getRate("USD", "EUR", null)*singlePrice);
							row.getCell(j - 1).setCellStyle(cellStyle);
						} else {
							continue;
							/*row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(cellStyle);
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(cellStyle);*/
						}

						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(singlePrice * (total - product));
						row.getCell(j - 1).setCellStyle(cellStyle);
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(MathUtils.getRate("USD", "EUR", null)*singlePrice * (total - product));
						row.getCell(j - 1).setCellStyle(cellStyle);
						totalMoney += singlePrice * (total - product);
					}else{
						//没权限的就显示销售单价和总金额
						cacheMap=salePrice.asMap();
						Float salePrice = cacheMap.get(nameWithColor);
						if (salePrice!= null) {
							row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(salePrice);
							row.getCell(j - 1).setCellStyle(cellStyle);
							//欧元
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(MathUtils.getRate("USD", "EUR", null)*salePrice);
							row.getCell(j - 1).setCellStyle(cellStyle);
						} else {
							/*row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(contentStyle);
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(contentStyle);*/
							continue;
						}
						
						if (salePrice!= null) {
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(salePrice*(total-product));
								row.getCell(j - 1).setCellStyle(cellStyle);
								//欧元
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(MathUtils.getRate("USD", "EUR", null)*salePrice*(total-product));
								row.getCell(j - 1).setCellStyle(cellStyle);
						} 
						/*else {
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(cellStyle);
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(cellStyle);
						}*/
						
					}
				}
			}
		}

		if (SecurityUtils.getSubject().isPermitted("psi:inventory:stockPriceView")) {
			row = sheet.createRow(rowIndex++);
			row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("total");
			row.getCell(0).setCellStyle(contentStyle);
			row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(1).setCellStyle(contentStyle);
			row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(2).setCellStyle(contentStyle);
			row.createCell(3, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(3).setCellStyle(contentStyle);
			sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 3));
			row.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(totalMoney);
			row.getCell(4).setCellStyle(cellStyle);
			row.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(MathUtils.getRate("USD", "EUR", null)*totalMoney);
			row.getCell(5).setCellStyle(cellStyle);
		}

		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i, true);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = country + "InventoryAmountTable" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
			return null;
		} catch (Exception e) {
			logger.error(country + "库存金额表导出异常", e);
		}
		return "redirect:" + Global.getAdminPath() + "/psi/psiInventory/inventoryWarn";
	}
	
	/**
	 * 海外产品导出(fba+海外仓+非中国仓发的在途产品)
	 * @param country	eu:欧洲  am:美洲   jp:日本
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "inventoryOverseaExport")
	public String inventoryOverseaExport(String country,String type,Model model, HttpServletRequest request, HttpServletResponse response){
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
		if (StringUtils.isNotEmpty(type)) {
			try {
				type = URLDecoder.decode(type, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		// 获得德国码 库存预留值
		// Map<String, Object[]> residueMap =this.psiProductService.getResidueMap();
		List<PsiProduct> list = psiProductService.findAllByCountry(country, type);
		
		//在途(剔除中国仓发的货)
		Map<String, PsiInventoryTotalDto> transportting = psiInventoryService.getTransporttingWithoutCn().get("1");
		//海外仓及中国仓(只计算海外仓数据)
		Map<String, PsiInventoryTotalDtoByInStock> inventorys = psiInventoryService.getInventoryQuantity();
		//产品MOQ价格信息
		Map<String, Map<String, Object>> productsMoqAndPrice = psiInventoryService.getProductsMoqAndPrice();
		//fba
		Map<String, PsiInventoryFba> fbas = psiInventoryService.getAllProductFbaInfo();
		Map<String, Float> piPriceMap = psiInventoryService.getPiPrice(country, DateUtils.addMonths(new Date(), -1));
		float rate = 1;	//实时汇率
		if ("eu".equals(country)) {
			rate = MathUtils.getRate("EUR", "USD", null);
		} else if ("ca".equals(country)) {
			rate = MathUtils.getRate("CAD", "USD", null);
		} else if ("jp".equals(country)) {
			rate = MathUtils.getRate("JPY", "USD", null);
		}
		//库存为排除在产后的库存
		List<String> title = Lists.newArrayList("Product Name", "Total Inventory(Overseas)");
		if (SecurityUtils.getSubject().isPermitted("psi:inventory:stockPriceView")) {
			//title.add("Price($)");
			title.add("PI Price($)");
			title.add("PI Price(€)");
			title.add("Total Amount($)");
			title.add("Total Amount(€)");
		}else{
			title.add("Price($)");
			title.add("Price(€)");
			title.add("Total Amount($)");
			title.add("Total Amount(€)");
			//销售价格
			if(salePrice==null||salePrice.size()==0){
				salePrice.cleanUp();
				salePrice.putAll(this.amazonProduct2Service.getSalePriceDollers());
			}
		}

		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short) i);
		}

		int rowIndex = 1;
		float totalMoney = 0f;
		List<String> sortCountry = Lists.newArrayList();
		if ("eu".equals(country)) {
			sortCountry = Lists.newArrayList("de", "fr", "it", "es", "uk");
		} else {
			sortCountry = Lists.newArrayList(country);
		}
		for (PsiProduct psiProduct : list) {
			List<String> nameWithColorList = psiProduct.getProductNameWithColor();
			Map<String, Float> incProPartsPrice = psiProduct.getTempPartsTotalMap();
			for (String nameWithColor : nameWithColorList) {
				int total = 0;
				int j = 0;
				for (String saleCountry : sortCountry) {
					String key = nameWithColor + "_" + saleCountry;
					//在途(剔除中国仓发出的产品)
					try {
						total += transportting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity();
					} catch (NullPointerException e) {}
					//海外仓
					if ("com".equals(saleCountry)) {
						try {
							total += inventorys.get(nameWithColor).getInventorys().get(saleCountry)
										.getQuantityInventory().get("US").getNewQuantity();
						} catch (NullPointerException e) {}
						try {	//美国仓的产品都算在美国上面（加拿大的货也算）
							total += inventorys.get(nameWithColor).getInventorys().get("ca")
										.getQuantityInventory().get("US").getNewQuantity();
						} catch (NullPointerException e) {}
					} else if (!"ca".equals(saleCountry)) {
						try {
							total += inventorys.get(nameWithColor).getInventorys().get(saleCountry)
										.getQuantityInventory().get("DE").getNewQuantity();
						} catch (NullPointerException e) {}
					}
					//fba
					try {
						total += fbas.get(key).getTotal();
					} catch (NullPointerException e) {}
				}
				if (total > 0) {
					row = sheet.createRow(rowIndex++);
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(nameWithColor);
					row.getCell(j - 1).setCellStyle(contentStyle);

					row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(total);
					row.getCell(j - 1).setCellStyle(contentStyle);

					if (SecurityUtils.getSubject().isPermitted("psi:inventory:stockPriceView")) {
						Float price = 0f;
						if (productsMoqAndPrice.get(nameWithColor) != null
								&& productsMoqAndPrice.get(nameWithColor).get("price") != null) {
							price = ((BigDecimal) productsMoqAndPrice.get(nameWithColor).get("price")).floatValue();
						}
						Float singlePrice = price;
						if (productsMoqAndPrice.get(nameWithColor) != null
								&& productsMoqAndPrice.get(nameWithColor).get("price") != null) {
							if (incProPartsPrice != null && incProPartsPrice.get(nameWithColor) != null) {
								singlePrice += incProPartsPrice.get(nameWithColor);
							}
						}
						if (piPriceMap.get(nameWithColor) != null) {
							singlePrice = piPriceMap.get(nameWithColor) * rate;
						}
						if (singlePrice != null && singlePrice > 0) {
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(singlePrice);
							row.getCell(j - 1).setCellStyle(cellStyle);
							//欧元
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(MathUtils.getRate("USD", "EUR", null)*singlePrice);
							row.getCell(j - 1).setCellStyle(cellStyle);
						} else {
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(cellStyle);
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(cellStyle);
						}

						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(singlePrice * total);
						row.getCell(j - 1).setCellStyle(cellStyle);
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(MathUtils.getRate("USD", "EUR", null)*singlePrice * total);
						row.getCell(j - 1).setCellStyle(cellStyle);
						totalMoney += singlePrice * (total);
					}else{
						//没权限的就显示销售单价和总金额
						cacheMap=salePrice.asMap();
						Float salePrice = cacheMap.get(nameWithColor);
						if (salePrice!= null) {
							row.createCell(j++, Cell.CELL_TYPE_NUMERIC).setCellValue(salePrice);
							row.getCell(j - 1).setCellStyle(cellStyle);
							//欧元
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(MathUtils.getRate("USD", "EUR", null)*salePrice);
							row.getCell(j - 1).setCellStyle(cellStyle);
						} else {
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(contentStyle);
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(contentStyle);
						}
						
						if (salePrice!= null) {
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(salePrice*total);
								row.getCell(j - 1).setCellStyle(cellStyle);
								//欧元
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(MathUtils.getRate("USD", "EUR", null)*salePrice*total);
								row.getCell(j - 1).setCellStyle(cellStyle);
						} else {
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(cellStyle);
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
							row.getCell(j - 1).setCellStyle(cellStyle);
						}
						
					}
				}
			}
		}

		if (SecurityUtils.getSubject().isPermitted("psi:inventory:stockPriceView")) {
			row = sheet.createRow(rowIndex++);
			row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue("total");
			row.getCell(0).setCellStyle(contentStyle);
			row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(1).setCellStyle(contentStyle);
			row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(2).setCellStyle(contentStyle);
			row.createCell(3, Cell.CELL_TYPE_STRING).setCellValue("");
			row.getCell(3).setCellStyle(contentStyle);
			sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 3));
			row.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(totalMoney);
			row.getCell(4).setCellStyle(cellStyle);
			row.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(MathUtils.getRate("USD", "EUR", null)*totalMoney);
			row.getCell(5).setCellStyle(cellStyle);
		}

		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i, true);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = ("com".equals(country)?"US":country.toUpperCase()) + "InventoryOversea" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
			return null;
		} catch (Exception e) {
			logger.error(country + "海外库存金额表导出异常", e);
		}
		return "redirect:" + Global.getAdminPath() + "/psi/psiInventory/inventoryWarn";
	}
	
	
	@RequestMapping(value = {"exportInventory"})
	public String exportInventory(PsiInventory psiInventory, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Stock> stocks= Lists.newArrayList();
		stocks=stockService.findStocks("0");
		if(psiInventory.getWarehouse()==null){
			//首次进来
			Set<String> permissionsSet = Sets.newHashSet();
			//查询权限
			User user = UserUtils.getUser();
			if(!user.isAdmin()){
				for(Role role:UserUtils.getUser().getRoleList()){
					permissionsSet.addAll(role.getPermissions());
				}
				String countryCode = "";
				if(UserUtils.hasPermission("psi:inventory:edit:DE")||UserUtils.hasPermission("psi:inventory:revise:DE")){
					countryCode="DE";
				}else	if(UserUtils.hasPermission("psi:inventory:edit:CN")||UserUtils.hasPermission("psi:inventory:revise:CN")){
					countryCode="CN";
				}else	if(UserUtils.hasPermission("psi:inventory:edit:US")||UserUtils.hasPermission("psi:inventory:revise:US")){
					countryCode="US";
				}else	if(UserUtils.hasPermission("psi:inventory:edit:JP")||UserUtils.hasPermission("psi:inventory:revise:JP")){
					countryCode="JP";
				}
				List<Stock> psiStocks =this.stockService.findByCountryCode(countryCode,"",null);
				if(psiStocks!=null&&psiStocks.size()>0){
					psiInventory.setWarehouse(psiStocks.get(0));
				}
				//借用sku存储是选中了哪个仓库
				psiInventory.setSku(countryCode);
			}else{
				//是admin
				psiInventory.setWarehouse(stocks.get(0));
				//借用sku存储是选中了哪个仓库
				psiInventory.setSku(stocks.get(0).getCountrycode());
			}
		}else{
			//操作库内调换   或其他可能只传个stockId
			Integer wareHouseId=psiInventory.getWarehouse().getId();
			if(wareHouseId!=null){
				for(Stock stock :stocks){
					if(stock.getId().equals(wareHouseId)){
						psiInventory.setWarehouse(stock);
						//借用sku存储是选中了哪个仓库
						psiInventory.setSku(stock.getCountrycode());
						break;
					}
				}
			}
		}
		Page<PsiInventory> page =new Page<PsiInventory>(request, response);
		page.setPageSize(60000);
        page = psiInventoryService.find(page, psiInventory); 
        Set<String> countrySet=Sets.newHashSet();
        Map<String, Map<String, Integer>> map=Maps.newLinkedHashMap();
        Map<String, Integer> offlineMap=Maps.newLinkedHashMap();
        for (PsiInventory inventory : page.getList()) {
        	List<PsiInventory> list=this.psiInventoryService.findByProductAndStock(inventory.getProductId(),inventory.getColorCode(),psiInventory);
			if(list!=null){
				for (PsiInventory inventory2 : list) {
					countrySet.add(inventory2.getCountryCode());
					String key = "";
					if(!StringUtils.isBlank(inventory2.getColorCode())){
						key=inventory2.getProductName()+"_"+inventory2.getColorCode();
					}else{
						key=inventory2.getProductName();
					}
					Map<String, Integer> tempMap = map.get(key);
					if(tempMap==null){
						tempMap = Maps.newLinkedHashMap();
						map.put(key, tempMap);
					}
					Integer quantity=tempMap.get(inventory2.getCountryCode());
					tempMap.put(inventory2.getCountryCode(), inventory2.getNewQuantity()+inventory2.getOfflineQuantity()+(quantity==null?0:quantity));
					Integer offlineQuantity = inventory2.getOfflineQuantity();
					if(offlineMap.get(key+","+inventory2.getCountryCode())!=null&&offlineMap.get(key+","+inventory2.getCountryCode()).intValue()!=0){
						offlineQuantity+=inventory2.getOfflineQuantity();
					}
					offlineMap.put(key+","+inventory2.getCountryCode(), offlineQuantity);
				}
			}	
		}
        
        Set<String> waitCountrySet=Sets.newHashSet();
        Map<String,Map<String,Integer>> waitQuantity=Maps.newHashMap();
        List<PsiTransportOrder> waitOutBound=psiTransportOrderService.findNewTranOrder();
        if(waitOutBound!=null&&waitOutBound.size()>0){
        	 for (PsiTransportOrder psiTransportOrder : waitOutBound) {
				  for (PsiTransportOrderItem item: psiTransportOrder.getItems()) {
					   waitCountrySet.add(item.getCountryCode());
					   String key="";
					   if(!StringUtils.isBlank(item.getColorCode())){
						   key=item.getProductName()+"_"+item.getColorCode();
					   }else{
						   key=item.getProductName();
					   }
					   Map<String,Integer> tempMap=waitQuantity.get(key);
					   if(tempMap==null){
						   tempMap = Maps.newLinkedHashMap();
						   waitQuantity.put(key, tempMap);
					   }
					   Integer quantity=tempMap.get(item.getCountryCode());
					   tempMap.put(item.getCountryCode(),item.getQuantity()+(quantity==null?0:quantity));
				  }
			 }
        }
        
        List<String> countryList = Lists.newArrayList(countrySet);
        List<String> waitCountryList = Lists.newArrayList(waitCountrySet);
        List<String> list = Lists.newArrayList(map.keySet());
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
	      HSSFFont font1 = wb.createFont();
		  font1.setColor(HSSFFont.COLOR_RED);  
		  font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
		  cellStyle.setFont(font1);
		  
		  HSSFCellStyle cellStyleBlue = wb.createCellStyle();
		  cellStyleBlue.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		  cellStyleBlue.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		  cellStyleBlue.setBottomBorderColor(HSSFColor.BLACK.index);
		  cellStyleBlue.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		  cellStyleBlue.setLeftBorderColor(HSSFColor.BLACK.index);
		  cellStyleBlue.setBorderRight(HSSFCellStyle.BORDER_THIN);
		  cellStyleBlue.setRightBorderColor(HSSFColor.BLACK.index);
		  cellStyleBlue.setBorderTop(HSSFCellStyle.BORDER_THIN);
		  cellStyleBlue.setTopBorderColor(HSSFColor.BLACK.index);
	      HSSFFont fontBlue = wb.createFont();
	      fontBlue.setColor(HSSFColor.BLUE.index);  
	      fontBlue.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
	      cellStyleBlue.setFont(fontBlue);
	      
		  HSSFCell cell = null;	
		  List<String> newCountryList=Lists.newArrayList("Product");
		  for (String country: countryList) {
			  newCountryList.add(country);
			  newCountryList.add("箱数");
		  }
		  //countryList.add(0, "Product");
		  for (int i = 0; i < newCountryList.size(); i++) {
				cell = row.createCell(i);
				cell.setCellStyle(style);
				if(i==0||i%2==0){
					cell.setCellValue(newCountryList.get(i));
				}else{
				    cell.setCellValue(DictUtils.getDictLabel(newCountryList.get(i),"platform",""));
				}
				sheet.autoSizeColumn((short) i);
		  }
		 
		 cell = row.createCell(newCountryList.size());
		 cell.setCellStyle(style);
		 cell.setCellValue("New总计");
		 
		 for (int i =newCountryList.size()+1; i <newCountryList.size()+1+waitCountryList.size(); i++) {
				cell = row.createCell(i);
				cell.setCellStyle(style);
				cell.setCellValue(DictUtils.getDictLabel(waitCountryList.get(i-newCountryList.size()-1),"platform",""));
				sheet.autoSizeColumn((short) i);
		  }
		 cell = row.createCell(newCountryList.size()+1+waitCountryList.size());
		 cell.setCellStyle(style);
		 cell.setCellValue("待发货总计");
		 
		 for(int i=0;i<list.size();i++){
			String productName = list.get(i);
			row = sheet.createRow(i+1);
			int total=0;
			for(int j=0;j<newCountryList.size(); j++){
				String country = newCountryList.get(j);
				if(j==0){
					row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(productName);
					row.getCell(0).setCellStyle(contentStyle); 
				}else if(j%2==0){
					cell=row.createCell(j,Cell.CELL_TYPE_NUMERIC);
					row.getCell(j).setCellStyle(contentStyle); 
					if(map.get(productName)!=null&&map.get(productName).get(newCountryList.get(j-1))!=null&&map.get(productName).get(newCountryList.get(j-1))!=0){
						Integer stock=map.get(productName).get(newCountryList.get(j-1));
						PsiProduct product=psiProductService.findProductByName(productName);
						if(stock%product.getPackQuantity()==0){
							int num = stock/ product.getPackQuantity();//多少箱
							//如果有线下数量
							if(offlineMap.get(productName+","+newCountryList.get(j-1))!=null&&offlineMap.get(productName+","+newCountryList.get(j-1))>0){
								cell.setCellValue(num+"("+offlineMap.get(productName+","+newCountryList.get(j-1))*1.0f/product.getPackQuantity()+")");
								cell.setCellStyle(cellStyleBlue);
							}else{
								cell.setCellValue(num);
							}
							
						}else{
							float num = stock*1.0f/ product.getPackQuantity();//多少箱
							//如果有线下数量
							if(offlineMap.get(productName+","+newCountryList.get(j-1))!=null&&offlineMap.get(productName+","+newCountryList.get(j-1))>0){
								cell.setCellValue(num+"("+offlineMap.get(productName+","+newCountryList.get(j-1))*1.0f/product.getPackQuantity()+")");
								cell.setCellStyle(cellStyleBlue);
							}else{
								cell.setCellValue(num);
								cell.setCellStyle(cellStyle);
							}
						}
					}
				}else{
					cell=row.createCell(j,Cell.CELL_TYPE_STRING);
					row.getCell(j).setCellStyle(contentStyle); 
					if(map.get(productName)!=null&&map.get(productName).get(country)!=null){
						if(map.get(productName).get(country)==0){
							cell.setCellValue("");
						}else{
							total+=map.get(productName).get(country);
							//如果有线下数量
							if(offlineMap.get(productName+","+country)!=null&&offlineMap.get(productName+","+country)>0){
								 cell.setCellValue(map.get(productName).get(country)+"("+offlineMap.get(productName+","+country)+")");
								 cell.setCellStyle(cellStyleBlue);
							}else{
								 cell.setCellValue(map.get(productName).get(country));
							}
						   
						} 
					}
				}
			}
			cell=row.createCell(newCountryList.size(),Cell.CELL_TYPE_NUMERIC);
			row.getCell(newCountryList.size()).setCellStyle(contentStyle); 
			cell.setCellValue(total);
			
			int total1=0;
			for(int j=newCountryList.size()+1;j<newCountryList.size()+1+waitCountryList.size(); j++){
					cell=row.createCell(j,Cell.CELL_TYPE_NUMERIC);
					row.getCell(j).setCellStyle(contentStyle); 
					if(waitQuantity.get(list.get(i))!=null&&waitQuantity.get(list.get(i)).get(waitCountryList.get(j-newCountryList.size()-1))!=null){
						if(waitQuantity.get(list.get(i)).get(waitCountryList.get(j-newCountryList.size()-1))==0){
							cell.setCellValue("");
						}else{
							total1+=waitQuantity.get(list.get(i)).get(waitCountryList.get(j-newCountryList.size()-1));
						    cell.setCellValue(waitQuantity.get(list.get(i)).get(waitCountryList.get(j-newCountryList.size()-1)));
						} 
					}
			}
			cell=row.createCell(newCountryList.size()+1+waitCountryList.size(),Cell.CELL_TYPE_NUMERIC);
			row.getCell(newCountryList.size()+1+waitCountryList.size()).setCellStyle(contentStyle); 
			cell.setCellValue(total1);
			
		}
//		 for (int i = 0; i < newCountryList.size()+waitCountryList.size(); i++) {
//				sheet.autoSizeColumn((short) i);
//		  }
        try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "库存" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "modules/psi/psiInventoryList";
	}
	
	
	@RequestMapping(value = {"exportInventorySpares"})
	public String exportInventorySpares(PsiInventory psiInventory, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Stock> stocks= Lists.newArrayList();
		stocks=stockService.findStocks("0");
		if(psiInventory.getWarehouse()==null){
			//查询权限
			User user = UserUtils.getUser();
			if(!user.isAdmin()){
				String countryCode = "";
				if(UserUtils.hasPermission("psi:inventory:edit:DE")||UserUtils.hasPermission("psi:inventory:revise:DE")){
					countryCode="DE";
				}else	if(UserUtils.hasPermission("psi:inventory:edit:CN")||UserUtils.hasPermission("psi:inventory:revise:CN")){
					countryCode="CN";
				}else	if(UserUtils.hasPermission("psi:inventory:edit:US")||UserUtils.hasPermission("psi:inventory:revise:US")){
					countryCode="US";
				}else	if(UserUtils.hasPermission("psi:inventory:edit:JP")||UserUtils.hasPermission("psi:inventory:revise:JP")){
					countryCode="JP";
				}
				List<Stock> psiStocks =this.stockService.findByCountryCode(countryCode,"",null);
				if(psiStocks!=null&&psiStocks.size()>0){
					psiInventory.setWarehouse(psiStocks.get(0));
				}
				//借用sku存储是选中了哪个仓库
				psiInventory.setSku(countryCode);
			}else{
				//是admin
				psiInventory.setWarehouse(stocks.get(0));
				//借用sku存储是选中了哪个仓库
				psiInventory.setSku(stocks.get(0).getCountrycode());
			}
		}else{
			//操作库内调换   或其他可能只传个stockId
			Integer wareHouseId=psiInventory.getWarehouse().getId();
			if(wareHouseId!=null){
				for(Stock stock :stocks){
					if(stock.getId().equals(wareHouseId)){
						psiInventory.setWarehouse(stock);
						//借用sku存储是选中了哪个仓库
						psiInventory.setSku(stock.getCountrycode());
						break;
					}
				}
			}
		}
		Page<PsiInventory> page =new Page<PsiInventory>(request, response);
		page.setPageSize(60000);
        page = psiInventoryService.find(page, psiInventory); 
        Set<String> countrySet=Sets.newHashSet();
        Map<String, Map<String, Integer>> map=Maps.newLinkedHashMap();
        for (PsiInventory inventory : page.getList()) {
        	List<PsiInventory> list=this.psiInventoryService.findByProductAndStock(inventory.getProductId(),inventory.getColorCode(),psiInventory);
			if(list!=null){
				for (PsiInventory inventory2 : list) {
					countrySet.add(inventory2.getCountryCode());
					String key = "";
					if(!StringUtils.isBlank(inventory2.getColorCode())){
						key=inventory2.getProductName()+"_"+inventory2.getColorCode();
					}else{
						key=inventory2.getProductName();
					}
					Map<String, Integer> tempMap = map.get(key);
					if(tempMap==null){
						tempMap = Maps.newLinkedHashMap();
						map.put(key, tempMap);
					}
					Integer quantity=tempMap.get(inventory2.getCountryCode());
					tempMap.put(inventory2.getCountryCode(), inventory2.getSparesQuantity()+(quantity==null?0:quantity));
				}
			}	
		}
        
        Set<String> waitCountrySet=Sets.newHashSet();
        Map<String,Map<String,Integer>> waitQuantity=Maps.newHashMap();
        List<PsiTransportOrder> waitOutBound=psiTransportOrderService.findNewTranOrder();
        if(waitOutBound!=null&&waitOutBound.size()>0){
        	 for (PsiTransportOrder psiTransportOrder : waitOutBound) {
				  for (PsiTransportOrderItem item: psiTransportOrder.getItems()) {
					   waitCountrySet.add(item.getCountryCode());
					   String key="";
					   if(!StringUtils.isBlank(item.getColorCode())){
						   key=item.getProductName()+"_"+item.getColorCode();
					   }else{
						   key=item.getProductName();
					   }
					   Map<String,Integer> tempMap=waitQuantity.get(key);
					   if(tempMap==null){
						   tempMap = Maps.newLinkedHashMap();
						   waitQuantity.put(key, tempMap);
					   }
					   Integer quantity=tempMap.get(item.getCountryCode());
					   tempMap.put(item.getCountryCode(),item.getQuantity()+(quantity==null?0:quantity));
				  }
			 }
        }
        
        List<String> countryList = Lists.newArrayList(countrySet);
        List<String> waitCountryList = Lists.newArrayList(waitCountrySet);
        List<String> list = Lists.newArrayList(map.keySet());
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
	      HSSFFont font1 = wb.createFont();
		  font1.setColor(HSSFFont.COLOR_RED);  
		  font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
		  cellStyle.setFont(font1);
		  
		  HSSFCellStyle cellStyleBlue = wb.createCellStyle();
		  cellStyleBlue.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		  cellStyleBlue.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		  cellStyleBlue.setBottomBorderColor(HSSFColor.BLACK.index);
		  cellStyleBlue.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		  cellStyleBlue.setLeftBorderColor(HSSFColor.BLACK.index);
		  cellStyleBlue.setBorderRight(HSSFCellStyle.BORDER_THIN);
		  cellStyleBlue.setRightBorderColor(HSSFColor.BLACK.index);
		  cellStyleBlue.setBorderTop(HSSFCellStyle.BORDER_THIN);
		  cellStyleBlue.setTopBorderColor(HSSFColor.BLACK.index);
	      HSSFFont fontBlue = wb.createFont();
	      fontBlue.setColor(HSSFColor.BLUE.index);  
	      fontBlue.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
	      cellStyleBlue.setFont(fontBlue);
	      
		  HSSFCell cell = null;	
		  List<String> newCountryList=Lists.newArrayList("Product");
		  for (String country: countryList) {
			  newCountryList.add(country);
			  newCountryList.add("箱数");
		  }
		  //countryList.add(0, "Product");
		  for (int i = 0; i < newCountryList.size(); i++) {
				cell = row.createCell(i);
				cell.setCellStyle(style);
				if(i==0||i%2==0){
					cell.setCellValue(newCountryList.get(i));
				}else{
				    cell.setCellValue(DictUtils.getDictLabel(newCountryList.get(i),"platform",""));
				}
				sheet.autoSizeColumn((short) i);
		  }
		 
		 cell = row.createCell(newCountryList.size());
		 cell.setCellStyle(style);
		 cell.setCellValue("New总计");
		 
		 for (int i =newCountryList.size()+1; i <newCountryList.size()+1+waitCountryList.size(); i++) {
				cell = row.createCell(i);
				cell.setCellStyle(style);
				cell.setCellValue(DictUtils.getDictLabel(waitCountryList.get(i-newCountryList.size()-1),"platform",""));
				sheet.autoSizeColumn((short) i);
		  }
		 cell = row.createCell(newCountryList.size()+1+waitCountryList.size());
		 cell.setCellStyle(style);
		 cell.setCellValue("待发货总计");
		 
		 for(int i=0;i<list.size();i++){
			String productName = list.get(i);
			row = sheet.createRow(i+1);
			int total=0;
			for(int j=0;j<newCountryList.size(); j++){
				String country = newCountryList.get(j);
				if(j==0){
					row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(productName);
					row.getCell(0).setCellStyle(contentStyle); 
				}else if(j%2==0){
					cell=row.createCell(j,Cell.CELL_TYPE_NUMERIC);
					row.getCell(j).setCellStyle(contentStyle); 
					if(map.get(productName)!=null&&map.get(productName).get(newCountryList.get(j-1))!=null&&map.get(productName).get(newCountryList.get(j-1))!=0){
						Integer stock=map.get(productName).get(newCountryList.get(j-1));
						PsiProduct product=psiProductService.findProductByName(productName);
						if(stock%product.getPackQuantity()==0){
							int num = stock/ product.getPackQuantity();//多少箱
							cell.setCellValue(num);
							
						}else{
							float num = stock*1.0f/ product.getPackQuantity();//多少箱
							cell.setCellValue(num);
							cell.setCellStyle(cellStyle);
						}
					}
				}else{
					cell=row.createCell(j,Cell.CELL_TYPE_STRING);
					row.getCell(j).setCellStyle(contentStyle); 
					if(map.get(productName)!=null&&map.get(productName).get(country)!=null){
						if(map.get(productName).get(country)==0){
							cell.setCellValue("");
						}else{
							total+=map.get(productName).get(country);
							cell.setCellValue(map.get(productName).get(country));
						} 
					}
				}
			}
			cell=row.createCell(newCountryList.size(),Cell.CELL_TYPE_NUMERIC);
			row.getCell(newCountryList.size()).setCellStyle(contentStyle); 
			cell.setCellValue(total);
			
			int total1=0;
			for(int j=newCountryList.size()+1;j<newCountryList.size()+1+waitCountryList.size(); j++){
					cell=row.createCell(j,Cell.CELL_TYPE_NUMERIC);
					row.getCell(j).setCellStyle(contentStyle); 
					if(waitQuantity.get(list.get(i))!=null&&waitQuantity.get(list.get(i)).get(waitCountryList.get(j-newCountryList.size()-1))!=null){
						if(waitQuantity.get(list.get(i)).get(waitCountryList.get(j-newCountryList.size()-1))==0){
							cell.setCellValue("");
						}else{
							total1+=waitQuantity.get(list.get(i)).get(waitCountryList.get(j-newCountryList.size()-1));
						    cell.setCellValue(waitQuantity.get(list.get(i)).get(waitCountryList.get(j-newCountryList.size()-1)));
						} 
					}
			}
			cell=row.createCell(newCountryList.size()+1+waitCountryList.size(),Cell.CELL_TYPE_NUMERIC);
			row.getCell(newCountryList.size()+1+waitCountryList.size()).setCellStyle(contentStyle); 
			cell.setCellValue(total1);
			
		}
//		 for (int i = 0; i < newCountryList.size()+waitCountryList.size(); i++) {
//				sheet.autoSizeColumn((short) i);
//		  }
        try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "备品库存" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "modules/psi/psiInventoryList";
	}
	
	@ResponseBody
	@RequestMapping(value ="getTipInfo")
	public String getTipInfo(String name,String type,String country){
		String rs = "";
		if("1".equals(type)){
			String warehouseCode = "";
			if(StringUtils.isNotEmpty(country)){
				if("eu".equals(country)||"fr,es,it,uk,de".contains(country)){
					warehouseCode = "DE";
				}else if(country.startsWith("com")){
					warehouseCode = "US";
				}else if("jp".equals(country)){
					warehouseCode = "JP";
				}
			}
			rs = JSON.toJSONString(psiInventoryService.getTransporttingByNameAndCountry(name, country, warehouseCode));
		}else if("2".equals(type)){
			rs = JSON.toJSONString(psiInventoryService.getFbaTransporttingByNameAndCountry(name, country));
		}else if("3".equals(type)){
			rs = JSON.toJSONString( psiInventoryService.getProducingByNameAndCountry(name, country));
		}else if("4".equals(type)){
			String warehouseCode = "";
			
			if(StringUtils.isNotEmpty(country)){
				if("eu".equals(country)||"fr,es,it,uk,de".contains(country)){
					warehouseCode = "DE";
				}else if(country.startsWith("com")){  
					warehouseCode = "US";
				}else if("jp".equals(country)){
					warehouseCode = "JP";
				}
			}
			rs = JSON.toJSONString(psiInventoryService.getPreTransporttingByNameAndCountry(name, country, warehouseCode));
		}else if("5".equals(type)){
			rs = JSON.toJSONString(psiInventoryService.getPreWkFbaTransporttingByNameAndCountry(name, country));
		}else if("8".equals(type)){
			rs = JSON.toJSONString(psiInventoryService.getRecallingOrder(name, country));
		}
		return rs;
	}
	
	@ResponseBody
	@RequestMapping(value ="getTranTipInfo")
	public String getTranTipInfo(String name,String type,String country){
		String rs = "";
		String warehouseCode = "";
		if(StringUtils.isNotEmpty(country)){
			if("eu".equals(country)||"fr,es,it,uk,de".contains(country)){
				warehouseCode = "DE";
			}else if("com,ca,mx".contains(country)){
				warehouseCode = "US";
			}else if("jp".equals(country)){
				warehouseCode = "JP";
			}
		}
		rs = JSON.toJSONString(psiInventoryService.getTranByNameAndCountry(name, country, warehouseCode));
		return rs;
	}
	
	@ResponseBody
	@RequestMapping(value ="getWaitTranTipInfo")
	public String getWaitTranTipInfo(String name,String type,String country){
		String rs = "";
		rs = JSON.toJSONString(psiInventoryService.getWaitTranByNameAndCountry(name, country, "CN"));
		return rs;
	}
	//fba库存积压
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "overStock")
	public String overStock(Integer type,String country,Date referenceDate,Model model,RedirectAttributes redirectAttributes){
		Map<String,Integer> canSaleDayMap = Maps.newHashMap();
		Map<String,Float>   priceMap = Maps.newHashMap();
		if(type==null){
			type=3;
		}
		if(referenceDate==null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				referenceDate=sdf.parse(sdf.format(new Date()));
			} catch (ParseException e) {
			}
		}
		Map<String,String> skuMap = this.amazonProduct2Service.findAllProductNamesWithSku();
		//不销售的sku
		List<String> noSaleAndNewSku= this.fbaService.getNoSaleAndNewSku();
		//查询30天销量
		Map<String,Integer> sale30Map = this.fbaService.get30DaysSales(noSaleAndNewSku,country,referenceDate);
		//查询所有的fba库存
		Map<String,Integer> fbaMap = this.fbaService.getFbaInventroy(noSaleAndNewSku,country,referenceDate);
		//获取积压的sku
//		for(String skuKey:fbaMap.keySet()){
		for(Map.Entry<String, Integer> entry:fbaMap.entrySet()){
			String skuKey = entry.getKey();
			//fba库存数为0
			if(entry.getValue().equals(0)){
				continue;
			}
			
			//如果30天销售没有这个    或者库存数为0   或者fba库存/30天销售>5
			if(sale30Map.get(skuKey)==null||sale30Map.get(skuKey).equals(0)||entry.getValue()/sale30Map.get(skuKey)>type){
				Integer canSalesDay = 0;
				//30天无销售数据，或销售数据为0
				if(sale30Map.get(skuKey)!=null&&!sale30Map.get(skuKey).equals(0)){
					float avg1 = sale30Map.get(skuKey)/30f;
					canSalesDay=Math.round(entry.getValue()/avg1);
				}
				canSaleDayMap.put(skuKey,canSalesDay);
			}
		}
		
		 Map<String,String[]> periodSaleMap =Maps.newHashMap();
		//积压标准：3个月，4个月，5个月
		if(canSaleDayMap.size()>0){
			periodSaleMap =this.fbaService.getPeriodSales(type,canSaleDayMap.keySet(),referenceDate);
			priceMap=this.fbaService.getSalesPrice(canSaleDayMap.keySet(), country);
		}
		
		model.addAttribute("canSaleDayMap", canSaleDayMap);
		model.addAttribute("sale30Map", sale30Map);
		model.addAttribute("priceMap", priceMap);
		model.addAttribute("fbaMap", fbaMap);
		model.addAttribute("periodSaleMap", periodSaleMap);
		model.addAttribute("country", country);
		model.addAttribute("type", type);
		model.addAttribute("skuMap", skuMap);
		model.addAttribute("referenceDate", referenceDate);
		return "modules/amazoninfo/overStock";
	}
	
	
	//fba库存积压
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "overStockProduct")
	public String overStockProduct(Integer type,String country,Model model,RedirectAttributes redirectAttributes){
		if(type==null){
			type=3;
		}
		//start
		//获取fba仓库最新的日期
		Date dataDate =this.fbaService.getLastDateFbaStock();
		 //依产品名颜色为粒度的库存 排国家除淘汰和新品		key:国家  value名字、数量		key:国家  value名字、数量
		Map<String,Map<String,String>> nameColorfbaMap = fbaService.getFbaInventoryMap(dataDate,country);
		//从方差表获得最近一个月的销量
		Map<String,Map<String,Integer>> sale31Map=fbaService.get31SalesMap(country);
		Map<String,Set<String>> notSaleOrNewProColorMap = this.psiProductEliminateService.getNotSaleOrNewProduct();
		
		Map<String,List<String>> overMap = this.fbaService.overStock(sale31Map, nameColorfbaMap,notSaleOrNewProColorMap,type);
		
		model.addAttribute("nameColorfbaMap", nameColorfbaMap);
		model.addAttribute("sale31Map", sale31Map);
		model.addAttribute("overMap", overMap);
		model.addAttribute("country", country);
		model.addAttribute("type", type);
		return "modules/amazoninfo/overStockProduct";
	}
	
	//fba库存断货
		@RequiresPermissions("psi:inventory:view")
		@RequestMapping(value = "outOfStock")
		public String outOfStock(Date startDate,Date endDate,String country,String isCheck,Model model,RedirectAttributes redirectAttributes) throws ParseException{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(startDate==null){
				try {
					startDate=sdf.parse(sdf.format(DateUtils.addMonths(new Date(), -1)));
					endDate = sdf.parse(sdf.format(new Date()));
				} catch (ParseException e) {
				}
			}
			if("1".equals(isCheck)){
				endDate = sdf.parse(sdf.format(new Date()));
			}
			Map<String,String> skuMap = this.psiProductService.getBandingSkuProduct();
			Map<String,String> skuBandingMap = this.amazonProduct2Service.findAllProductNamesWithSku();
			//淘汰的sku
			List<String> noSaleSku= this.fbaService.getNoSaleSku();
			//查询所有断货的sku
			Map<String,String> outOfMap= this.fbaService.getOutOfStockSku(startDate, endDate, country, noSaleSku);
			
			//查询30天销量
			Map<String,Integer> sale30Map = this.fbaService.get30DaysSales(noSaleSku,country,new Date());
					
			//查询断货
			Map<String,Float> priceMap = Maps.newHashMap();
			Map<String,OutOffStockDto> tempMap = Maps.newLinkedHashMap();
			if(outOfMap!=null&&outOfMap.size()>0){
				tempMap=this.fbaService.getOutOfStock(startDate, endDate, DateUtils.spaceDays(startDate, endDate), country, outOfMap,isCheck);
				priceMap=this.fbaService.getSalesPrice(outOfMap.keySet(), country);
			}
			
//			for(String sku :tempMap.keySet()){
			for(Map.Entry<String, OutOffStockDto> entry:tempMap.entrySet()){
				String sku = entry.getKey();
				String priceKey = "";
				if(StringUtils.isNotEmpty(country)){
					priceKey = sku+","+country;
				}else{
					if(skuMap.get(sku)!=null){
						priceKey = sku+","+skuMap.get(sku).split(",")[1];
					}
				}
				Float price = priceMap.get(priceKey);
				if(sale30Map.get(sku)!=null&&!sale30Map.get(sku).equals(0)&&price!=null){
					Float delAmount=sale30Map.get(sku)*entry.getValue().getOutOffDays()*price/30;
					entry.getValue().setDelAmount(delAmount);
				}
				
				entry.getValue().setPrice(price);
			}
			
			model.addAttribute("country", country);
			model.addAttribute("startDate", startDate);
			model.addAttribute("endDate", endDate);
			model.addAttribute("outOfStockMap", tempMap);
			model.addAttribute("isCheck", isCheck);
			model.addAttribute("skuMap", skuBandingMap);
			model.addAttribute("sale30Map", sale30Map);
			return "modules/amazoninfo/outOfStock";
		}
		
		
		
		//fba库存断货
		@RequiresPermissions("psi:inventory:view")
		@RequestMapping(value = "outOfStockByProduct")
		public String outOfStockByProduct(Date startDate,Date endDate,String isCheck,String country,Model model,RedirectAttributes redirectAttributes) throws ParseException{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
			if(startDate==null){
				try {
					endDate = sdf.parse(sdf.format(DateUtils.addDays(new Date(),-1)));
					startDate=sdf.parse(sdf.format(DateUtils.addMonths(endDate, -1)));
				} catch (ParseException e) {
				}
			}
			
			if(StringUtils.isEmpty(isCheck)){
				isCheck="1";
			}
			
			String endDateStr=null;
			if("1".equals(isCheck)){
				endDateStr=sdf1.format(endDate);
			}
			List<AmazonOutOfProduct> list = this.outOfService.getOutOfData(startDate, endDate,endDateStr,country);
			List<PsiProductAttribute> attrs = psiProductAttributeService.findAll();
			Map<String,PsiProductAttribute> productAttr = Maps.newHashMap();
			for (PsiProductAttribute psiProductAttribute : attrs) {
				productAttr.put(psiProductAttribute.getColorName(), psiProductAttribute);
			}
			List<String> countryList = Lists.newArrayList();
			if (StringUtils.isNotEmpty(country)) {
				countryList.add(country);
			}
			Map<String, String> productPosition = psiProductEliminateService.findProductPositionByCountry(countryList);
			model.addAttribute("productPosition", productPosition);
			List<String> newProducts =this.psiProductEliminateService.getNewProductsCountry();
			
			
			model.addAttribute("newProducts", newProducts);
			model.addAttribute("productAttr", productAttr);
			model.addAttribute("isCheck", isCheck);
			model.addAttribute("country", country);
			model.addAttribute("startDate", startDate);
			model.addAttribute("endDate", endDate);
			model.addAttribute("list", list);
			return "modules/amazoninfo/outOfStockByProduct";
		}
		
		
		@RequiresPermissions("psi:inventory:view")
		@RequestMapping(value = "expOverStockProduct")
		public String expOverStockProduct(Integer type,String country,Model model,RedirectAttributes redirectAttributes,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, ParseException{
				if(type==null){
					type=3;
				}
				//获取fba仓库最新的日期
				Date dataDate =this.fbaService.getLastDateFbaStock();
				 //依产品名颜色为粒度的库存 排国家除淘汰和新品		key:国家  value名字、数量
				Map<String,Map<String,String>> nameColorfbaMap = fbaService.getFbaInventoryMap(dataDate,country);
				//从方差表获得最近一个月的销量
				Map<String,Map<String,Integer>> sale31Map=fbaService.get31SalesMap(country);
				Map<String,Set<String>> notSaleOrNewProColorMap = this.psiProductEliminateService.getNotSaleOrNewProduct();
				
				Map<String,List<String>> overMap = this.fbaService.overStock(sale31Map, nameColorfbaMap,notSaleOrNewProColorMap,type);
				
				
				DecimalFormat df = new DecimalFormat("0.00");
				if(overMap.size()>0){
					
					HSSFWorkbook wb = new HSSFWorkbook();
					HSSFSheet sheet = wb.createSheet();
					HSSFRow row = sheet.createRow(0);
					HSSFCellStyle style = wb.createCellStyle();
					style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
					
					String[] title = { " 产品名  ","  国家   ","  目前FBA库存数  ","30天销售数","  可售月数   "};
				    style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
				    style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
				    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				    //设置字体
				    HSSFFont font = wb.createFont();
				    font.setFontHeightInPoints((short) 11); // 字体高度
				    style.setFont(font);
				    row.setHeight((short) 400);
				    HSSFCell cell = null;		
				    for (int i = 0; i < title.length; i++) {
						cell = row.createCell(i);
						cell.setCellValue(title[i]);
						cell.setCellStyle(style);
					}
					int j =1;
					for(Map.Entry<String, List<String>> entry :overMap.entrySet()){
						String countryKey = entry.getKey();
						for(String proNameColor:entry.getValue()){
							int i =0;
							row = sheet.createRow(j++);
							row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(proNameColor); 
							row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(countryKey)?"us":countryKey);
							row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(nameColorfbaMap.get(countryKey).get(proNameColor).split(",")[0]);
							row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sale31Map.get(countryKey).get(proNameColor)==null?"":(sale31Map.get(countryKey).get(proNameColor)+""));
							if(sale31Map.get(countryKey).get(proNameColor)!=null&&!sale31Map.get(countryKey).get(proNameColor).equals(0)){
								row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(df.format(Integer.parseInt(nameColorfbaMap.get(countryKey).get(proNameColor).split(",")[0])/sale31Map.get(countryKey).get(proNameColor)));
							}
						}
					}
					request.setCharacterEncoding("UTF-8");
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/x-download");
				
					SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
				
					String fileName = "overStockProductInfos" + sdf.format(new Date()) + ".xls";
					fileName = URLEncoder.encode(fileName, "UTF-8");
					response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
					try {
						OutputStream out = response.getOutputStream();
						wb.write(out);
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
				
				return "modules/amazoninfo/overStock";
			}
		
		@RequiresPermissions("psi:inventory:view")
		@RequestMapping(value = "expOutOfStockByProduct")
		public String expOutOfStockByProduct(Date startDate,Date endDate,String country,String isCheck,Model model,RedirectAttributes redirectAttributes,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, ParseException{
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
			String endDateStr=null;
			if("1".equals(isCheck)){
				endDateStr=sdf1.format(endDate);
			}
			List<AmazonOutOfProduct> list = this.outOfService.getOutOfData(startDate, endDate,endDateStr,null);
			
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			String[] title = { " 产品名  ","  国家   "," 断货天数   " ,"  断货日期   "};
		    style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		    style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		    //设置字体
		    HSSFFont font = wb.createFont();
		    font.setFontHeightInPoints((short) 11); // 字体高度
		    style.setFont(font);
		    row.setHeight((short) 400);
		    HSSFCell cell = null;		
		    for (int i = 0; i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style);
			}
			int j =1;
			for(AmazonOutOfProduct outOf:list){
				int i =0;
				row = sheet.createRow(j++);
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(outOf.getProductNameColor()); 
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(outOf.getCountry()); 
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(outOf.getDaySpace()); 
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(outOf.getDayStr()); 
			 }
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "outOfStockByProductInfos" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			try {
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	
	//fba库存积压导出
		@RequiresPermissions("psi:inventory:view")
		@RequestMapping(value = "expOverStock")
		public String expOverStock(Integer type,String country,Date referenceDate,Model model,RedirectAttributes redirectAttributes,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
			Map<String,Integer> canSaleDayMap = Maps.newHashMap();
			Map<String,Float>   priceMap = Maps.newHashMap();
			if(type==null){
				type=3;
			}
			if(referenceDate==null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					referenceDate=sdf.parse(sdf.format(new Date()));
				} catch (ParseException e) {
				}
			}
			Map<String,String> skuMap = this.psiProductService.getBandingSkuProduct();
			//不销售的sku
			List<String> noSaleAndNewSku= this.fbaService.getNoSaleAndNewSku();
			//查询30天销量
			Map<String,Integer> sale30Map = this.fbaService.get30DaysSales(noSaleAndNewSku,country,referenceDate);
			//查询所有的fba库存
			Map<String,Integer> fbaMap = this.fbaService.getFbaInventroy(noSaleAndNewSku,country,referenceDate);
			//获取积压的sku
//			for(String skuKey:fbaMap.keySet()){
			for(Map.Entry<String, Integer> entry:fbaMap.entrySet()){
				String skuKey = entry.getKey();
				//fba库存数为0
				if(entry.getValue().equals(0)){
					continue;
				}
				
				//如果30天销售没有这个    或者库存数为0   或者fba库存/30天销售>5
				if(sale30Map.get(skuKey)==null||sale30Map.get(skuKey).equals(0)||entry.getValue()/sale30Map.get(skuKey)>type){
					Integer canSalesDay = 0;
					//30天无销售数据，或销售数据为0
					if(sale30Map.get(skuKey)!=null&&!sale30Map.get(skuKey).equals(0)){
						float avg1 = sale30Map.get(skuKey)/30f;
						canSalesDay=Math.round(entry.getValue()/avg1);
					}
					canSaleDayMap.put(skuKey,canSalesDay);
				}
			}
			
			 Map<String,String[]> periodSaleMap =Maps.newHashMap();
			//积压标准：3个月，4个月，5个月
			if(canSaleDayMap.size()>0){
				periodSaleMap =this.fbaService.getPeriodSales(type,canSaleDayMap.keySet(),referenceDate);
				priceMap=this.fbaService.getSalesPrice(canSaleDayMap.keySet(), country);
			}
			
			DecimalFormat df = new DecimalFormat("0.00");
			if(periodSaleMap.size()>0){
				
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet sheet = wb.createSheet();
				HSSFRow row = sheet.createRow(0);
				HSSFCellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				
				String[] title = { " 产品名  ","  国家   ","  SKU  ","  目前FBA库存数  "," 过去"+type+"个月的销售数  " ,"30天销售数","  可售库存天数   "," 超出"+type+"个月库存天数  ","  目前售价   "};
			    style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
			    style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
			    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			    //设置字体
			    HSSFFont font = wb.createFont();
			    font.setFontHeightInPoints((short) 11); // 字体高度
			    style.setFont(font);
			    row.setHeight((short) 400);
			    HSSFCell cell = null;		
			    for (int i = 0; i < title.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(title[i]);
					cell.setCellStyle(style);
				}
				int j =1;
				for(Map.Entry<String, String[]> entry :periodSaleMap.entrySet()){
					String sku = entry.getKey();
					int i =0;
					row = sheet.createRow(j++);
					String [] saleArr = entry.getValue();
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(skuMap.get(sku)!=null?skuMap.get(sku).split(",")[0]:""); 
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(saleArr[5])?"us":saleArr[5]);
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sku);
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(fbaMap.get(sku));
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(saleArr[4]==null?"0":saleArr[4]);
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sale30Map.get(sku)!=null?sale30Map.get(sku)+"":"");
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(canSaleDayMap.get(sku).equals(0)?"":df.format(canSaleDayMap.get(sku)));
					String delDays = "";
					if(canSaleDayMap.get(sku).equals(0)){
						delDays="";
					}else{
						delDays=canSaleDayMap.get(sku)-30*type+"";
					}
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(delDays);
					
					String priceKey = "";
					if(StringUtils.isNotEmpty(country)){
						priceKey = sku+","+country;
					}else{
						if(skuMap.get(sku)!=null){
							priceKey = sku+","+skuMap.get(sku).split(",")[1];
						}
					}
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(priceMap.get(priceKey)==null?"":df.format(priceMap.get(priceKey)));
				}
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
			
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			
				String fileName = "overStockInfos" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				try {
					OutputStream out = response.getOutputStream();
					wb.write(out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			return "modules/amazoninfo/overStock";
		}
	
	
	
	
	//fba库存积压
		@RequiresPermissions("psi:inventory:view")
		@RequestMapping(value = "expOutOfStock")
		public String expOutOfStock(Date startDate,Date endDate,String country,String isCheck,Model model,RedirectAttributes redirectAttributes,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, ParseException{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(startDate==null){
				startDate=sdf.parse(sdf.format(DateUtils.addMonths(new Date(), -1)));
				endDate = sdf.parse(sdf.format(new Date()));
			}
			
			if("1".equals(isCheck)){
				endDate = sdf.parse(sdf.format(new Date()));
			}
			Map<String,String> skuMap = this.psiProductService.getBandingSkuProduct();
			//淘汰的sku
			List<String> noSaleSku= this.fbaService.getNoSaleSku();
			//查询所有断货的sku
			Map<String,String> outOfMap= this.fbaService.getOutOfStockSku(startDate, endDate, country, noSaleSku);
			
			//查询30天销量
			Map<String,Integer> sale30Map = this.fbaService.get30DaysSales(noSaleSku,country,new Date());
					
			//查询断货
			Map<String,OutOffStockDto> tempMap = Maps.newLinkedHashMap();
			if(outOfMap!=null&&outOfMap.size()>0){
				tempMap=this.fbaService.getOutOfStock(startDate, endDate, DateUtils.spaceDays(startDate, endDate), country, outOfMap,isCheck);
			}
			
			Map<String,Float> priceMap=this.fbaService.getSalesPrice(outOfMap.keySet(), country);
			
			for(Map.Entry<String, OutOffStockDto> entry:tempMap.entrySet()){
				String sku = entry.getKey();
				String priceKey = "";
				if(StringUtils.isNotEmpty(country)){
					priceKey = sku+","+country;
				}else{
					if(skuMap.get(sku)!=null){
						priceKey = sku+","+skuMap.get(sku).split(",")[1];
					}
				}
				
				Float price = priceMap.get(priceKey);
				if(sale30Map.get(sku)!=null&&!sale30Map.get(sku).equals(0)&&price!=null){
					Float delAmount=sale30Map.get(sku)*entry.getValue().getOutOffDays()*price/30;
					entry.getValue().setDelAmount(delAmount);
				}
				
				entry.getValue().setPrice(price);
			}
			
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			String[] title = { " 产品名  ","  国家   ","  sku  "," 断货天数   " ,"最近30天销量","  售价   "," 损失  ","  断货日期   "};
		    style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		    style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		    //设置字体
		    HSSFFont font = wb.createFont();
		    font.setFontHeightInPoints((short) 11); // 字体高度
		    style.setFont(font);
		    row.setHeight((short) 400);
		    HSSFCell cell = null;		
		    for (int i = 0; i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style);
			}
			 DecimalFormat df = new DecimalFormat("0.00");
			int j =1;
			for(Map.Entry<String, OutOffStockDto> entry:tempMap.entrySet()){
				String sku = entry.getKey();
				int i =0;
				row = sheet.createRow(j++);
				OutOffStockDto outDto = entry.getValue();
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(skuMap.get(sku)!=null?skuMap.get(sku).split(",")[0]:""); 
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(outDto.getCountry())?"us":outDto.getCountry());
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sku);
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(outDto.getOutOffDays());
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sale30Map.get(sku)==null?"":sale30Map.get(sku)+"");
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(outDto.getPrice()==null?"":df.format(outDto.getPrice()));
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(outDto.getDelAmount()==null?"":df.format(outDto.getDelAmount()));
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(outDto.getOutOffDaysStr());
			 }
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
		
			SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");
		
			String fileName = "outOfStockInfos" + sdf1.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			try {
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * 手机版页面专用
		 * @return
		 */
		@RequestMapping(value = "mobileProductInfoDetail")
		public String mobileProductInfoDetail(String sTime, String sCountry, String sType, String searchType, String productName, String orderType,SaleReport saleReport,Model model){
			productName = HtmlUtils.htmlUnescape(productName);
			if(searchType != null){
				saleReport.setSearchType(searchType);
			}

			//销量信息
			saleReport.setSku(productName);
			Map<String, Map<String, SaleReport>>  data = saleReportService.getSalesBySingleProduct(saleReport);
			model.addAttribute("data", data);
			/* 手机版使用颜色区分销量订单类型
			Map<String, Map<String,Map<String,Integer>>> typeData=saleReportService.getSalesTypeBySingleProduct(saleReport);
			model.addAttribute("typeData", typeData);*/
			Map<String, Map<String,SaleReport>> otherData = saleReportService.getSalesBySingleProduct2(saleReport);
			model.addAttribute("otherData", otherData);
			
			//构建x轴
			List<String> xAxis  = Lists.newArrayList();
			List<String> priceXAxis  = Lists.newArrayList();
			List<String> rankXAxis  = Lists.newArrayList();
			Date start = saleReport.getStart();
			Date end = saleReport.getEnd();
			Map<String, String> tip = Maps.newHashMap();
			while(end.after(start)||end.equals(start)){
				if("2".equals(saleReport.getSearchType())){
					String key = formatWeek.format(start);
					xAxis.add(key);
					int year = Integer.parseInt(key.substring(0,4));
					int week =  Integer.parseInt(key.substring(4));
					Date first = DateUtils.getFirstDayOfWeek(year, week);
					tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
					start = DateUtils.addWeeks(start, 1);
				}else if("3".equals(saleReport.getSearchType())){
					String key = formatMonth.format(start);
					xAxis.add(key);
					tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
					start = DateUtils.addMonths(start, 1);
				}else{
					String key = formatDay.format(start);
					xAxis.add(key);
					tip.put(key,DateUtils.getDate(start,"E"));
					start = DateUtils.addDays(start, 1);
				}
			}
			start = saleReport.getStart();
			end = saleReport.getEnd();
			Date today = new Date();
			while(end.after(start)||end.equals(start)){
				String key = formatDay.format(start);
				priceXAxis.add(key);
				if(Integer.parseInt(key) >= 20150902){
					rankXAxis.add(key);
				}
				
				start = DateUtils.addDays(start, 1);
				if(formatDay.format(start).equals(formatDay.format(today))){
					break;
				}
			}
			model.addAttribute("priceXAxis", priceXAxis);
			model.addAttribute("xAxis", xAxis);
			model.addAttribute("tip", tip);
	        if(formatDay.format(end).equals(formatDay.format(today))){
	        	rankXAxis.add(formatDay.format(today));
	        }
			
			model.addAttribute("rankXAxis", rankXAxis);
			
			//产品信息
			PsiProduct product = psiProductService.findProductByName(productName);
			if(product==null){
				throw new RuntimeException("产品不存在(未匹配sku)或者产品名存在'&'符;如果产品名内存在'&'符,请在搜索栏内查看该产品信息");
			}
			String color = "";
			if(productName.indexOf("_")>0){
				color = productName.substring(productName.indexOf("_")+1);
			}
			
			Float partsPrice =0f;
			for(ProductParts parts: product.getProductParts()){
				if(color.equals(parts.getColor())){
					if(parts.getParts().getPrice()!=null&&parts.getMixtureRatio()!=null){
						partsPrice+=parts.getParts().getPrice()*parts.getMixtureRatio();
					}else if(parts.getParts().getRmbPrice()!=null&&parts.getMixtureRatio()!=null){
						partsPrice+=parts.getParts().getRmbPrice()*parts.getMixtureRatio()/AmazonProduct2Service.getRateConfig().get("USD/CNY");
					}
					break;
				};
			}
			//退货率
			Map<String, Object[]>  returnGoods = returnGoodsService.findReturnCommentInfo(saleReport.getStart(), saleReport.getEnd(), productName);
			model.addAttribute("returnGoods", returnGoods);
			//产品名_国家 fba
			Map<String, PsiInventoryFba>  fbas = psiInventoryService.getProductFbaInfo(productName);
			
			//产品名_国家
			Map<String,ProductSalesInfo> fancha = productSalesInfoService.find(productName);
			
			//查询产品经理
			String managerName ="";
			if(StringUtils.isNotEmpty(product.getType())){
				managerName=this.psiProductService.getManagerByProductType(product.getType());
			}
			model.addAttribute("managerName", managerName);
			
			//产品名
			PsiInventoryTotalDto producting = psiInventoryService.getProducingQuantity(productName,null);
			Map<String, PsiInventoryTotalDto >rs = psiInventoryService.getTransporttingQuantity(productName,null);
			PsiInventoryTotalDto transportting = rs.get("1");
			PsiInventoryTotalDtoByInStock inventorys = psiInventoryService.getInventoryQuantity(productName,null);
			model.addAttribute("producting", producting);
			model.addAttribute("transportting", transportting);
			model.addAttribute("preTransportting", rs.get("0"));
			model.addAttribute("inventorys", inventorys);
			//产品名_国家 fba在途数据
			Map<String,Integer> fbaTran  = psiInventoryService.getFbaTransporttingByName(productName);
			
			model.addAttribute("color", color);
			model.addAttribute("productName", productName);
			model.addAttribute("product", product);
			model.addAttribute("fbas", fbas);
			model.addAttribute("fbaTran", fbaTran);
			model.addAttribute("fancha", fancha);
			
			//查询产品价格变化日志
			Map<Integer,String> priceLogMap= priceLogService.getRemarkMap(productName);
			Map<Integer,Map<String,Float>> purchasePriceMap=tieredService.getSinglePriceBaseMoq(productName,partsPrice);
			model.addAttribute("purchasePriceMap", purchasePriceMap);
			model.addAttribute("priceLogMap", priceLogMap);
			
			//记住查询条件
			model.addAttribute("time", sTime);
			model.addAttribute("country", sCountry);
			model.addAttribute("sType", sType);
			model.addAttribute("orderType", orderType);
			model.addAttribute("transportTypeMap", psiProductAttributeService.findtransportType());
			return "modules/amazoninfo/productInfoDetail";
		}
		
		@RequestMapping(value = "expSingleProductSales")
		public String expSingleProductSales(String productName,SaleReport saleReport,Model model,RedirectAttributes redirectAttributes,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, ParseException{
			productName = HtmlUtils.htmlUnescape(productName);
			//销量信息
			saleReport.setSku(productName);
			Map<String, Map<String, SaleReport>>  data = saleReportService.getSalesBySingleProduct(saleReport);
			
			List<String> xAxis  = Lists.newArrayList();
			Date start = saleReport.getStart();
			if(start.getDay()==0&&"2".equals(saleReport.getSearchType())){
				start = DateUtils.addDays(start, -1);
			}
			Date end = saleReport.getEnd();
			Map<String, String> tip = Maps.newHashMap();
			while(end.after(start)||end.equals(start)){
				if("2".equals(saleReport.getSearchType())){
					String key = formatWeek.format(start);
					int year =DateUtils.getSunday(start).getYear()+1900;
					int week =  Integer.parseInt(key.substring(4));
					if(week==53){
		                year =DateUtils.getMonday(start).getYear()+1900;
				    }
					if(week<10){
						key = year+"0"+week;
					}else{
						key =year+""+week;
					}
					xAxis.add(key);
					Date first = DateUtils.getFirstDayOfWeek(year, week);
					tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+" ~ "+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
					start = DateUtils.addWeeks(start, 1);
				}else if("3".equals(saleReport.getSearchType())){
					String key = formatMonth.format(start);
					xAxis.add(key);
					tip.put(key, key+"-1 ~ "+DateUtils.getDate(DateUtils.getLastDayOfMonth(start),"yyyy-MM-dd"));
					start = DateUtils.addMonths(start, 1);
				}else{
					String key = formatDay.format(start);
					xAxis.add(key);
					tip.put(key,DateUtils.getDate(start,"E"));
					start = DateUtils.addDays(start, 1);
				}
			}
			
			
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			String[] title = { "日期","区间 ","德国","英国" ,"法国","意大利 ","西班牙 ","欧洲 ","美国 ","日本 ","加拿大 ","墨西哥 ","全球 "};
			List<String> countryList=Lists.newArrayList("de","uk" ,"fr","it","es","euTotal","com","jp","ca","mx","total");
		    style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		    style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		    //设置字体
		    HSSFFont font = wb.createFont();
		    font.setFontHeightInPoints((short) 11); // 字体高度
		    style.setFont(font);
		    row.setHeight((short) 400);
		    HSSFCell cell = null;		
		    for (int i = 0; i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style);
			}
		    String type=("1".equals(saleReport.getSearchType())?"日":("2".equals(saleReport.getSearchType())?"周":"月"));
			
			int num=1;
			Collections.reverse(xAxis);
		    for (String formatDate : xAxis) {
				Integer euTotal=0;
				Integer total=0;
				row = sheet.createRow(num++);
				int j=0;
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(formatDate+type);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(tip.get(formatDate));
				
				for (String country : countryList) {
					if(!"euTotal".equals(country)&&!"total".equals(country)){
						if(data!=null&&data.get(formatDate)!=null&&data.get(formatDate).get(country)!=null&&data.get(formatDate).get(country).getSalesVolume()!=null){
							Integer temp=data.get(formatDate).get(country).getSalesVolume();
							row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(temp);
							total+=temp;
							if("de,fr,uk,it,es".contains(country)){
								euTotal+=temp;
							}
						}else{
							row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
						}
					}else if("euTotal".equals(country)){
						row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(euTotal);
					}else if("total".equals(country)){
						row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(total);
					}
				}
				
			}
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
		
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddhhmm");
		
			String fileName = "productSale" + sdf1.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			try {
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		//导出指定月份的历史库存金额信息(导出选择月份在数据库中最后一天数据)
		@RequestMapping(value = "exportHisInventory")
		public String exportHisInventory(String month, String country, HttpServletRequest request,HttpServletResponse response, Model model) throws ParseException {
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
			contentStyle.setWrapText(true);

			CellStyle contentStyle1 = wb.createCellStyle();
			if ("jp".equals(country)) {
				contentStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
			} else {
				contentStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
			}
			contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
			contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
			contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
			contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
			contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
			contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
			contentStyle1.setWrapText(true);
			
			//两位小数显示
			HSSFCellStyle percentageOne = wb.createCellStyle();
			percentageOne.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
			percentageOne.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			percentageOne.setBottomBorderColor(HSSFColor.BLACK.index);
			percentageOne.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			percentageOne.setLeftBorderColor(HSSFColor.BLACK.index);
			percentageOne.setBorderRight(HSSFCellStyle.BORDER_THIN);
			percentageOne.setRightBorderColor(HSSFColor.BLACK.index);
			percentageOne.setBorderTop(HSSFCellStyle.BORDER_THIN);
			percentageOne.setTopBorderColor(HSSFColor.BLACK.index);
			
			//标题，粗体
			HSSFCellStyle titleStyle = wb.createCellStyle();
			titleStyle.setFillBackgroundColor(HSSFColor.WHITE.index);
			titleStyle.setFillForegroundColor(HSSFColor.WHITE.index);
			titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			titleStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
			titleStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			titleStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
			titleStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			titleStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
			titleStyle.setRightBorderColor(HSSFColor.BLACK.index);
			titleStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
			titleStyle.setTopBorderColor(HSSFColor.BLACK.index);
			HSSFFont titleFont = wb.createFont();
			titleFont.setFontHeightInPoints((short) 20); // 字体高度
			titleFont.setFontName(" 黑体 "); // 字体
			titleFont.setBoldweight((short) 20);
			titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			titleStyle.setFont(titleFont);
			
			CellStyle totalStyle = wb.createCellStyle();
			totalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			totalStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			totalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			totalStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			totalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			totalStyle.setRightBorderColor(HSSFColor.BLACK.index);
			totalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			totalStyle.setTopBorderColor(HSSFColor.BLACK.index);
			totalStyle.setWrapText(true);
			HSSFFont totalFont = wb.createFont();
			totalFont.setFontHeightInPoints((short) 18); // 字体高度
			totalFont.setFontName(" 黑体 "); // 字体
			totalFont.setBoldweight((short) 18);
			totalFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
			totalStyle.setFont(totalFont);
			

			CellStyle totalPecentStyle = wb.createCellStyle();
			if ("jp".equals(country)) {
				totalPecentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
			} else {
				totalPecentStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
			}
			totalPecentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			totalPecentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
			totalPecentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			totalPecentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
			totalPecentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			totalPecentStyle.setRightBorderColor(HSSFColor.BLACK.index);
			totalPecentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			totalPecentStyle.setTopBorderColor(HSSFColor.BLACK.index);
			totalPecentStyle.setWrapText(true);
			totalPecentStyle.setFont(totalFont);
			
			HSSFCell cell = null;
			
			//历史库存(排除在产)
			Map<String, Integer> rs = psiProductInStockService.findInventoryByMonth(month, country);
			/*//历史成本价
			Map<String, Map<String, Map<String, Float>>> hisPriceMap = psiProductInStockService.findPriceByMonth(month, country);
			//月初历史库存(排除在产)
			Map<String, Integer> startRs = psiProductInStockService.findInventoryByMonthStart(month, country);
			//产品销量
			Map<String, Integer> salesVolumeMap = saleReportMonthTypeService.findSalesVolumeByMonth(month, country);*/
			Map<String,Map<String,Object>> productsMoqAndPrice = psiInventoryService.getProductsMoqAndPrice();
			Map<String, PsiProduct> productMap = Maps.newHashMap();
			List<PsiProduct> list = psiProductService.findAll();
			for (PsiProduct psiProduct : list) {
				for (String colorName : psiProduct.getProductNameWithColor()) {
					productMap.put(colorName, psiProduct);
				}
			}
			String type = "USD";
			if ("eu".equals(country)) {
				type = "EUR";
			} else if ("ca".equals(country)) {
				type = "CAD";
			}else if ("jp".equals(country)) {
				type = "JPY";
			}

			DateFormat format = new SimpleDateFormat("yyyyMM");
			DateFormat usFormat = new SimpleDateFormat("yyyy MMM", Locale.US);
			DateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
			Map<String, Float> piPriceMap = psiInventoryService.getPiPrice(country, format.parse(month));
			//List<String> title = Lists.newArrayList("Product Name", "Total Inventory(Not included in production)", "Price("+type+")", "Total Price("+type+")", "Total Amount("+type+")", "Inventory Turnover Ratio");
			//去掉price和Inventory Turnover Ratio
			List<String> title = Lists.newArrayList("Product Name", "Total Inventory", "PI Price("+type+")", "Total Amount("+type+")");
			//第一行标题
			String monthStr = month;
			try {
				monthStr = usFormat.format(format.parse(month));
			} catch (Exception e) {}
			
			cell = row.createCell(0);
			int rowIndex = 1;
			String countryStr = "com".equals(country)?"US":country.toUpperCase();
			cell.setCellValue(countryStr + " " + monthStr + " Inventory(Not included in production)");
			CellRangeAddress cra=new CellRangeAddress(0, 0, 0, 3);
			sheet.addMergedRegion(cra);
			cell.setCellStyle(titleStyle);
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
			
			float rate = 1;	//当月最后一天汇率
			Date end = DateUtils.getLastDayOfMonth(format.parse(month));
			Map<String,Float> rateMap = amazonProduct2Service.getRateByDate(dayFormat.format(end));
			if ("eu".equals(country)) {
				rate = MathUtils.getRate("USD", "EUR", rateMap);
			} else if ("ca".equals(country)) {
				rate = 1/MathUtils.getRate("CAD", "USD", rateMap);
			} else if ("jp".equals(country)) {
				rate = 1/MathUtils.getRate("JPY", "USD", rateMap);
			}
			
			Integer allTotal = 0;
			float allPrice = 0;
			for (Map.Entry<String, Integer> entry : rs.entrySet()) {
				String productName = entry.getKey();
				float singlePrice = 0f;	//单价
				float price = 0f;
				try {
					singlePrice = ((BigDecimal)productsMoqAndPrice.get(productName).get("price")).floatValue();
					price += singlePrice;
				} catch (NullPointerException e) {
					logger.info(productName + "singlePrice价格为空");
				}
				try {
					price += productMap.get(productName).getTempPartsTotalMap().get(productName);
				} catch (NullPointerException e) {}
				if (singlePrice == 0) {	//价格为0的不需要
					continue;
				}
				int j = 0;
				row = sheet.createRow(rowIndex++);
				row.setHeight((short) 400);
				//产品名称
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(productName);
				int total = entry.getValue();
				allTotal += total;
				//总库存
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(total);
				price = price * rate;
				singlePrice = singlePrice * rate;
				// 有PI价格的话用PI价格
				if (piPriceMap.get(productName) != null && piPriceMap.get(productName) > 0) {
					price = piPriceMap.get(productName);
				}
				BigDecimal   b  =   new BigDecimal(price);
				price = b.setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue();
				//row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(singlePrice);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(price);
				float totalPrice = total * price;
				allPrice += totalPrice;
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(totalPrice);
				//计算月存货周转率（存货周转率=该产品当月销售成本/[（月初余额+月末余额）/2]）
				/*Integer salesVolume = salesVolumeMap.get(productName);
				if (salesVolume == null) {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				} else {
					float sPrice = 0;	//月初价
					float ePrice = 0;	//月底价
					try {//先用美金价格计算
						sPrice = hisPriceMap.get("0").get(productName).get("price");
						ePrice = hisPriceMap.get("1").get(productName).get("price");
					} catch (NullPointerException e) {}
					if (sPrice > 0 && ePrice > 0) {
						float avgPrice = (sPrice + ePrice)/2;	//平均价
						Integer sTotal = startRs.get(productName);
						if (sTotal == null) {
							float ratio = (salesVolume * avgPrice)/(total * ePrice);
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ratio);
						} else {
							float ratio = (salesVolume * avgPrice)/((sTotal * sPrice + total * ePrice)/2);
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ratio);
						}
					} else {//美金价为空则用人民币价格计算
						try {
							sPrice = hisPriceMap.get("0").get(productName).get("rmbPrice");
							ePrice = hisPriceMap.get("1").get(productName).get("rmbPrice");
						} catch (NullPointerException e) {
							logger.info(productName + "价格为空");
						}
						if (sPrice > 0 && ePrice > 0) {
							float avgPrice = (sPrice + ePrice)/2;	//平均价
							Integer sTotal = startRs.get(productName);
							if (sTotal == null) {
								float ratio = (salesVolume * avgPrice)/(total * ePrice);
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ratio);
							} else {
								float ratio = (salesVolume * avgPrice)/((sTotal * sPrice + total * ePrice)/2);
								row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(ratio);
							}
						} else {
							row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
						}
					}
				}*/
			}
			//合计
			int m = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("Total");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(allTotal);
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(allPrice);
			

			try {
				for (int i = 1; i < rowIndex - 2; i++) {
					for (int j = 0; j < title.size(); j++) {
						if (j == 2 || j == 3) {
							sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle1);
						} else {
							sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
						}
					}
				}
				for (int j = 0; j < title.size(); j++) {
					if (j == 2 || j == 3) {
						sheet.getRow(rowIndex-1).getCell(j).setCellStyle(totalPecentStyle);
					} else {
						sheet.getRow(rowIndex-1).getCell(j).setCellStyle(totalStyle);
					}
				}
			} catch (Exception e) {
				logger.error("导出历史库存格式设置异常", e);
			}

			for (int i = 0; i < title.size(); i++) {
				sheet.autoSizeColumn((short) i, true);
			}

			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");

				String fileName = month + country + "HistoryOfInventory" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				logger.error("按月历史库存金额信息导出异常", e);
			}
			return null;
		}


	//导出单品评论信息
	@RequestMapping(value = "exportProductReview")
	public String exportProductReview(String country, String productName, SaleReport saleReport,HttpServletRequest request,HttpServletResponse response) {
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
		contentStyle.setWrapText(true);
		HSSFCell cell = null;

		List<Object[]> list = saleReportService.findProductReview(country, productName, saleReport);
		List<String> title = Lists.newArrayList("评论时间", "ReviewAsin", "评分", "主题", "订单号", "客户名称", 
				"订单时间", "客户邮箱", "客户ID", "购买数量", "购买单价", "折扣码", "折扣", "联系电话", "邮编", "收货国家", "收货城市");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		int rowIndex = 1;
		for (Object[] obj : list) {
			int j = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			for (int i = 0; i < obj.length; i++) {
				if (i == obj.length -1) {
					continue; //最后一列asin不需要
				}
				if (i == 0) {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(obj[i]==null?"":obj[i].toString().split(" ")[0]);
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(obj[i]==null?"":obj[i].toString());
				}
			}
		}

		for (int i = 0; i < rowIndex - 1; i++) {
			for (int j = 0; j < title.size(); j++) {
				sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
			}
		}

		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i);
		}

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
			String countryStr = StringUtils.isEmpty(country)?"全平台":"com".equals(country)?"US":country.toUpperCase();
			String fileName = "单品评论导出" + countryStr;
			fileName = URLEncoder.encode(fileName, "UTF-8");
			fileName = fileName + "("+productName + ")" + sdf.format(new Date()) + ".xls";
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("单品评论导出异常", e);
		}
		return null;
	}
	
	
	
	/**
	 *导出理诚出库单数据
	 * @throws ParseException 
	 * 
	 */
	@RequiresPermissions("psi:order:financeReview")
	@RequestMapping(value ="payExport")
	public String payExport(Integer warehouseId,String startDate,String endDate, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
			List<String> bothDate = Lists.newArrayList();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			//由于每天日志记录是晚上12点左右记录，所以开始时间往前推一天
			Date beforeStartDay = DateUtils.addDays(sdf1.parse(startDate),-1);
			Date afterEndDay = DateUtils.addDays(sdf1.parse(endDate),1);
			String beforeStartDayStr = sdf1.format(beforeStartDay);
			String afterEndDayStr = sdf1.format(afterEndDay);
			
			bothDate.add(startDate);
			bothDate.add(endDate);
			
			
			//查询所有产品信息
			List<Object[]> productColorInfos=this.psiProductService.findProducColorInfo();
//			//查询产品及时单价
			Map<String,Float> productPriceMap =tieredService.findMoqPriceByCurrency();
			
			//查询产品库存均价
//			Map<String,Map<String,Object>> inventoryMap = this.psiInventoryService.findInventoryAndPrice(warehouseId);  
			//暂时从库存日志里面推当天的数量
			Map<String,String> dateInventoryMap=psiInventoryService.dateInventoryMap(beforeStartDayStr,afterEndDayStr);
//			Map<String,Map<String,Integer>> dateInventoryMap=this.psiInventoryService.getInventoryByDate(bothDate);
			
			
			//查询这段时间理诚收货的产品数量
			Map<String,String> inMap =psiInventoryService.inInventoryDataByDate(startDate, afterEndDayStr);
			//查询期初价格
			Map<String,Float>  startPriceMap = psiInventoryService.startDateyPrice(startDate);
			//查询这段时间理诚出货的产品数量
			Map<String,Integer> outMap =psiInventoryService.outInventoryDataByDate(startDate, afterEndDayStr);
			     
	        List<String> title=Lists.newArrayList("产品型号+颜色","单位","期初数量","期初单价","期初金额","入库数量","入库单价","入库金额","出库数量","出库单价","出库金额","期末数量","期末单价","期末金额", "产品描述");
	        HSSFWorkbook wb = new HSSFWorkbook();
	  		HSSFSheet sheet = wb.createSheet();
	  		
	  		sheet.addMergedRegion(new Region(0, new Short("0"), 0, new Short("2"))); 
	  		sheet.addMergedRegion(new Region(1, new Short("0"), 1, new Short("2")));   
	  		sheet.addMergedRegion(new Region(1, new Short("3"), 1, new Short("5")));  
			
	  		HSSFCellStyle style1 = wb.createCellStyle();
			HSSFDataFormat df1 = wb.createDataFormat();  //此处设置数据格式  
			style1.setDataFormat(df1.getFormat("#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));  
			
			int  rowIndex=0;
			short rowHeight=300;
			HSSFRow row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue("深圳市理诚科技有限公司");
			 row.setHeight(rowHeight);
			row = sheet.createRow(rowIndex++);
			row.setHeight(rowHeight);
			row.createCell(0).setCellValue(startDate.substring(0, 4)+"进销存明细账");
			row.createCell(3).setCellValue("会计期间："+startDate+"---"+endDate);
			
			row = sheet.createRow(rowIndex++);
			row.setHeight(rowHeight);
	  		HSSFCell cell = null;
	  		for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				sheet.autoSizeColumn((short) i);
			}
	  		
			for(Object[] proInfo:productColorInfos){
				String productName = proInfo[0].toString();
				String chineseName = proInfo[1].toString();
				String desc        = proInfo[2].toString();
				Integer startQuantity=0;
				Integer endQuantity=0;
				Integer inQuantity =0;
				Integer outQuantity=0;
				Float   inPrice =0f;
				Float   startPrice=0f;
				Float   outPrice=0f;
				if(dateInventoryMap.get(productName)!=null){
					String arr[]=dateInventoryMap.get(productName).split(",");
					startQuantity=Integer.parseInt(arr[0]);
					endQuantity=Integer.parseInt(arr[1]);	
//					startQuantity=dateInventoryMap.get(productName).get(beforeStartDayStr);
//					endQuantity=dateInventoryMap.get(productName).get(endDate);
				}
//				if(dateInventoryMap.get(beforeOneDay)!=null&&dateInventoryMap.get(beforeOneDay).get(productName)!=null){
//					startQuantity=dateInventoryMap.get(beforeOneDay).get(productName);
//				}
//				if(dateInventoryMap.get(endDate)!=null&&dateInventoryMap.get(endDate).get(productName)!=null){
//					endQuantity=dateInventoryMap.get(endDate).get(productName);
//				}
				
				if(inMap.get(productName)!=null){
					String arr[] = inMap.get(productName).split(",");
					inQuantity=Integer.parseInt(arr[0].toString());
					inPrice= Float.parseFloat(arr[1].toString());
					if(inPrice.floatValue()==0f){
						inPrice = productPriceMap.get(productName)!=null?productPriceMap.get(productName):0f;
					}
				}
				
				if(outMap.get(productName)!=null){
					outQuantity=outMap.get(productName);
				}
				
				if(startQuantity.intValue()==0&&endQuantity.intValue()==0&&inQuantity.intValue()==0&&outQuantity.intValue()==0){
					continue;
				}
				
				if(startPriceMap!=null&&startPriceMap.get(productName)!=null){
					startPrice=startPriceMap.get(productName);
				}
				
				row = sheet.createRow(rowIndex++);
				row.setHeight(rowHeight);
				int j=0;
				//"产品型号+颜色", "产品描述","单位","期初数量","期初单价","期初金额","入库数量","入库单价","入库金额","出库数量","出库单价","出库金额","期末数量","期末单价","期末金额");
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(productName);
				
				String unit ="";
				if(chineseName.contains("(")){
					unit=chineseName.substring(chineseName.indexOf("(")+1,chineseName.indexOf(")"));
				}
				
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unit);
				
				//--------期初
				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(startQuantity);
				
				cell = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
				cell.setCellValue(startPrice);
				//cell.setCellValue(0f);
				
				cell = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
				cell.setCellValue(startPrice*startQuantity);
				//cell.setCellValue(0f);
				
				//--------入库
				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(inQuantity);
				
				cell = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
				cell.setCellValue(inPrice);
				//cell.setCellValue(0f);
				
				
				cell = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
				cell.setCellValue(inPrice*inQuantity);
				//cell.setCellValue(0f);
				
				//算出出库价
				if(startQuantity+inQuantity>0){
					outPrice = (startPrice*startQuantity+inPrice*inQuantity)/(startQuantity+inQuantity);
				}
				
				//--------出库
				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(outQuantity);
				
				cell = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
				cell.setCellValue(outPrice);
				//cell.setCellValue(0f);
				
				cell = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
				cell.setCellValue(outPrice*outQuantity);
				//cell.setCellValue(0f);
				
				//--------期末
				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(endQuantity);
				
				cell = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
			    cell.setCellValue(outPrice);
				//cell.setCellValue(0f);
				
				cell = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
				cell.setCellValue(outPrice*endQuantity);
				//cell.setCellValue(0f);
				
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(desc);
			}
			
		
      		try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
				String fileName = "psiInventory" + sdf.format(new Date()) + ".xls";
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
	 * 所有产品库存、销量汇总(亚马逊、ebay、线下、多渠道替代&评测、销毁&清算)
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "exportProductCheck")
	public String exportProductCheck(HttpServletRequest request,HttpServletResponse response, Model model) {
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
		contentStyle.setWrapText(true);

		//百分比两位小数
		CellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
		contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setWrapText(true);
		
		HSSFCell cell = null;
		
		List<String> title = Lists.newArrayList("产品名称", "采购数量", "销量", "总库存", "差额", "差额采购比", 
				"上架时间", "淘汰", "新品", "ERP订单最早收货时间");
		//亚马逊总销量
		Map<String, Integer> salesVolumeMap = saleReportService.getAllSalesVolume();
		//ebay总销量
		Map<String, Integer> ebaySalesMap = mfnOrderService.getAllSalesVolume();
		//unline总销量
		Map<String, Integer> unlineSalesMap = unlineOrderService.getAllSalesVolume();
		//多渠道替代&评测
		Map<String, Integer> outboundSalesMap = outboundOrderService.getAllSalesVolume();
		//销毁&清算
		Map<String, Integer> removalSalesMap = removalOrderService.getAllSalesVolume();
		
		//总采购
		Map<String, Integer> purchaseQtyMap = purchaseOrderService.getAllQty("1");
		//产品名_在产
		Map<String, Integer> producting = purchaseOrderService.getAllQty("2");
		//产品名_在途
		Map<String, Integer> transportting = psiInventoryService.getAllTransportting();
		//产品名_在库数库(中国仓、海外仓)
		Map<String, Integer> inventorys = psiInventoryService.getAllInventory();
		//产品名_FBA
		Map<String, Integer> fbas = psiInventoryService.getAllFba();
		//产品属性
		Map<String, PsiProductEliminate> allAttrMap = psiProductEliminateService.findProductAllAttr();
		
		//ERP有记录的采购单最早交付时间
		Map<String, String> deliveryDates = purchaseOrderService.getAllDeliveryDate();
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}

		int rowIndex = 1;
		int totalSalesVolume = 0;
		int totalPurchaseQty = 0;
		int totalInventoryQty = 0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//		for (String productName : salesVolumeMap.keySet()) {
		for (Map.Entry<String, Integer> entry : salesVolumeMap.entrySet()) {
			String productName = entry.getKey();
			int j = 0;
			row = sheet.createRow(rowIndex++);
			row.setHeight((short) 400);
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(productName);
			//单品采购数
			int purchaseQty = 0;
			if (purchaseQtyMap.get(productName) != null) {
				purchaseQty = purchaseQtyMap.get(productName);
				totalPurchaseQty += purchaseQty;
			}
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(purchaseQty);
			//单品总销量
			Integer salesVolume = entry.getValue();
			//亚马逊销量根据采购记录截取统计时间,其他销量时间都在2015年3月之后不需要考虑
			if (allAttrMap.get(productName) != null) {
				String addedMonth = allAttrMap.get(productName).getAddedMonth();
				String deliveryDate = deliveryDates.get(productName);
				if (StringUtils.isNotEmpty(addedMonth) && StringUtils.isNotEmpty(deliveryDate)) {
					// 上架时间在最小交付时间之前,取最小交付时间一个月后的销量
					if (Integer.parseInt(addedMonth.replaceAll("-", ""))<Integer.parseInt(deliveryDate.replaceAll("-", ""))) {
						try {
							Date date = format.parse(deliveryDate);
							date = DateUtils.addMonths(date, 1);
							salesVolume = saleReportService.getSalesVolumeByProduct(productName, format.format(date));
						} catch (ParseException e) {
							logger.warn("时间转换异常！" +deliveryDate, e);
						}
					}
				}
			}
			if (ebaySalesMap.get(productName) != null) {	//ebay销量
				salesVolume += ebaySalesMap.get(productName);
			}
			if (unlineSalesMap.get(productName) != null) {	//线下销量
				salesVolume += unlineSalesMap.get(productName);
			}
			if (outboundSalesMap.get(productName) != null) {	//多渠道发货销量
				salesVolume += outboundSalesMap.get(productName);
			}
			if (removalSalesMap.get(productName) != null) {	//召回订单
				salesVolume += removalSalesMap.get(productName);
			}
			totalSalesVolume += salesVolume;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(salesVolume);
			//单品总库存
			int inventory = 0;
			if (producting.get(productName) != null) { //在产
				inventory += producting.get(productName);
			}
			if (transportting.get(productName) != null) { //在途
				inventory += transportting.get(productName);
			}
			if (inventorys.get(productName) != null) { //在库
				inventory += inventorys.get(productName);
			}
			if (fbas.get(productName) != null) { //FBA
				inventory += fbas.get(productName);
			}
			totalInventoryQty += inventory;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(inventory);
			//差额(采购-销量-库存)
			int chaQty = salesVolume+inventory-purchaseQty;
			row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(chaQty);
			//差额占采购比例
			if (purchaseQty > 0) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(chaQty/(float)purchaseQty);
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			//上架时间、淘汰、新品、主力
			PsiProductEliminate eliminate = allAttrMap.get(productName);
			if (eliminate != null) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(eliminate.getAddedMonth());
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(eliminate.getIsSale());
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(eliminate.getIsNew());
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			}
			if (deliveryDates.get(productName)==null) {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
			} else {
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(deliveryDates.get(productName));
			}
			
		}
		//total
		int m = 0;
		row = sheet.createRow(rowIndex++);
		row.setHeight((short) 400);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("总计");
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalPurchaseQty);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalSalesVolume);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(totalInventoryQty);
		int chaQty = totalSalesVolume+totalInventoryQty-totalPurchaseQty;
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(chaQty);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue(chaQty/(float)totalPurchaseQty);
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");
		row.createCell(m++, Cell.CELL_TYPE_STRING).setCellValue("");

		for (int i = 0; i < rowIndex - 1; i++) {
			for (int j = 0; j < title.size(); j++) {
				if (j==5) {
					sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle1);
				} else {
					sheet.getRow(i + 1).getCell(j).setCellStyle(contentStyle);
				}
			}
		}

		for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i);
		}

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");

			String fileName = "库存核对报表导出" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("库存核对报表导出异常", e);
		}
		return null;
	}
	
	
	@RequestMapping(value = "findOutOfStock")
	public String findOutOfStock(SaleReport saleReport,Model model){
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
		if(saleReport.getStart()==null){
			Date date=DateUtils.addMonths(new Date(),-1);
			saleReport.setStart(DateUtils.getFirstDayOfMonth(date));
			saleReport.setEnd(DateUtils.getLastDayOfMonth(date));
			//saleReport.setStart(DateUtils.addDays(new Date(), -60));
			//saleReport.setEnd(new Date());
		}
		Map<String,Map<String,List<Date>>> map=fbaService.getOutOfStock2(saleReport.getStart(),saleReport.getEnd());
		List<String> keyBoardAndHasPowerList=saleReportService.findKeyBoardAndHasPowerAllProduct();
		Map<String,String> nameAndLineMap=groupDictService.getLineByName();//产品名-产品线ID
		model.addAttribute("nameAndLineMap", nameAndLineMap);
		List<PsiProductTypeGroupDict> lineList=groupDictService.getAllList();
		model.addAttribute("lineList", lineList);
		model.addAttribute("saleReport", saleReport);
		Map<String,Map<String,String>> detailMap=Maps.newHashMap();
		Map<String,Map<String,Integer>> lineMap=Maps.newHashMap();
		Map<String,Map<String,String>> groupMap=groupUserService.getSingleGroupUser();
		model.addAttribute("groupMap", groupMap);
		Map<String,Map<String,PsiProductEliminate>> isNewMap=psiProductEliminateService.findAllByNameAndCountry();
		//产品名_颜色 、国家   isSale>0?"在售":"淘汰"  isNew>0?"新品":"普通品" 
		model.addAttribute("isNewMap", isNewMap);
		if(map!=null&&map.size()>0){
			for (Map.Entry<String, Map<String, List<Date>>> entry: map.entrySet()) {
				String name = entry.getKey();
				if(nameAndLineMap.get(name)!=null){
					  String lineId=nameAndLineMap.get(name);
					  Map<String,List<Date>> dateMap=entry.getValue();
					  for (Map.Entry<String,List<Date>> entry1: dateMap.entrySet()) {
						  String country = entry1.getKey();
						     if("de,fr,it,uk,es".contains(country)&&!keyBoardAndHasPowerList.contains(name)){//泛欧
						    	if(dateMap.get("de")==null){
						    		continue;
						    	}
						     }
						      String key="";
							  if("de,fr,it,jp,es".contains(country)){
								  key="unEn";
							  }else{
								 key="en"; 
							  }
							  List<Date> dateList=entry1.getValue();
							  String time=dateFormat.format(dateList.get(0))+";";
							
							  for(int i=1;i<dateList.size();i=i+1){
								  Date beforeDate=dateList.get(i-1);
								  Date afterDate=dateList.get(i);
								  if(DateUtils.addDays(beforeDate, 1).equals(afterDate)){
									    if(time.contains("~"+dateFormat.format(beforeDate))){
									    	time=time.replace("~"+dateFormat.format(beforeDate)+";","");
											time+="~"+dateFormat.format(afterDate)+";";
									    }else if(time.contains(dateFormat.format(beforeDate)+";")){
									    	time=time.replace(dateFormat.format(beforeDate)+";","");
											time+=dateFormat.format(beforeDate)+"~"+dateFormat.format(afterDate)+";";
									    }
								  }else{
									  time+=dateFormat.format(afterDate)+";";
								  }
							  }
							 
							  int num=time.split(";").length;
							   Map<String,Integer> lineTemp=lineMap.get(lineId);
							   if(lineTemp==null){
								   lineTemp=Maps.newHashMap();
								   lineMap.put(lineId, lineTemp);
							   }
							   Integer count=lineTemp.get(key);
							   lineTemp.put(key, count==null?num:count+num);
							   
							   Integer countryCount=lineTemp.get("total");
							   lineTemp.put("total", countryCount==null?num:countryCount+num);
							   
							   
							   Map<String,Integer> totalTemp=lineMap.get("total");
							   if(totalTemp==null){
								   totalTemp=Maps.newHashMap();
								   lineMap.put("total", totalTemp);
							   }
							   Integer totalCount=totalTemp.get(key);
							   totalTemp.put(key, totalCount==null?num:totalCount+num);
							   Integer totalCountCount=totalTemp.get("total");
							   totalTemp.put("total", totalCountCount==null?num:totalCountCount+num);
							   
							   
							   Map<String,String> nameTemp=detailMap.get(name);
							   if(nameTemp==null){
								   nameTemp=Maps.newHashMap();
								   detailMap.put(name, nameTemp);
							   }
							   nameTemp.put(country, time);
							   
					  }
				}
			}
			model.addAttribute("detailMap", detailMap);
			model.addAttribute("lineMap", lineMap);
		}
		 return "modules/psi/countOutOfProduct";
	}
	
	//psiProductAttributeService
	@RequestMapping(value = "countPurchase")
	public String countPurchase(Integer week,Model model){
		Map<String,String> nameAndLineMap=groupDictService.getLineByName();//产品名-产品线ID
		model.addAttribute("nameAndLineMap", nameAndLineMap);
		List<PsiProductTypeGroupDict> lineList=groupDictService.getAllList();
		model.addAttribute("lineList", lineList);
		model.addAttribute("lineSize", lineList.size());
		Map<String,Map<String,String>> groupMap=groupUserService.getSingleGroupUser();
		
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newHashMap();//国家-采购周-产品线-次数
		Map<String,Map<String,Map<String,String>>> detailMap=Maps.newHashMap();//国家-采购周-产品线-次数
		model.addAttribute("groupMap", groupMap);
	
		if(week==null){
			week=2;
		}
		List<String> weekList=Lists.newArrayList();
		for (int i=week-1;i>=0;i--) {
			weekList.add(i+"");
		}
		model.addAttribute("weekList", weekList);
		Map<String,Map<String,Map<String,String>>> purchaseMap=psiProductAttributeService.findPurchaseWeek3(week);
		if(purchaseMap!=null&&purchaseMap.size()>0){////country 采购周   产品名   采购单号
			for (Map.Entry<String,Map<String,Map<String,String>>> entry: purchaseMap.entrySet()) {
				String country = entry.getKey();
				  String key="";
				  if("de,fr,it,jp,es".contains(country)){
					  key="unEn";
				  }else{
					 key="en"; 
				  }
				  Map<String,Map<String,String>> purchaseWeek=entry.getValue();
				  for (Map.Entry<String,Map<String,String>> entry1: purchaseWeek.entrySet()) {
					  Map<String,String> temp=entry1.getValue();
					  String tempWeek = entry1.getKey();
					  for (Map.Entry<String,String> entry2: temp.entrySet()) {
						  String name = entry2.getKey();
						  int size=entry2.getValue().split(",").length;
						  if(size>=2){
							  Map<String,Map<String,Integer>> totalMap=map.get(key);
							  if(totalMap==null){
								  totalMap=Maps.newHashMap();
								  map.put(key, totalMap);
							  }
							  
							  Map<String,Map<String,Integer>> totalCountryMap=map.get("total");
							  if(totalCountryMap==null){
								  totalCountryMap=Maps.newHashMap();
								  map.put("total", totalCountryMap);
							  }
							  
							  Map<String,Integer> totalPurchase=totalMap.get(tempWeek);
							  if(totalPurchase==null){
								  totalPurchase=Maps.newHashMap();
								  totalMap.put(tempWeek, totalPurchase);
							  }
							  
							  Map<String,Integer> totalCountryPurchase=totalCountryMap.get(tempWeek);
							  if(totalCountryPurchase==null){
								  totalCountryPurchase=Maps.newHashMap();
								  totalCountryMap.put(tempWeek, totalCountryPurchase);
							  }
							  
							  
							  String lineId=nameAndLineMap.get(name);
							  Integer count=totalPurchase.get(lineId);
							  totalPurchase.put(lineId, count==null?(size-1):count+(size-1));
							  
							  Integer totalCount=totalCountryPurchase.get(lineId);
							  totalCountryPurchase.put(lineId, totalCount==null?(size-1):totalCount+(size-1));
							  
							  Map<String,Map<String,String>> totalDetailMap=detailMap.get(country);
							  if(totalDetailMap==null){
								  totalDetailMap=Maps.newHashMap();
								  detailMap.put(country, totalDetailMap);
							  }
							 
							  Map<String,String> totalPurchaseDetail=totalDetailMap.get(tempWeek);
							  if(totalPurchaseDetail==null){
								  totalPurchaseDetail=Maps.newHashMap();
								  totalDetailMap.put(tempWeek, totalPurchaseDetail);
							  }
							  
							  String detail=totalPurchaseDetail.get(name);
							  totalPurchaseDetail.put(name,StringUtils.isBlank(detail)?temp.get(name):(detail+","+temp.get(name)));
						  }
					  }
				  }
			}
		}
		model.addAttribute("map", map);
		model.addAttribute("week", week);
		model.addAttribute("detailMap", detailMap);
		return "modules/psi/countPurchase";
	}
	
	@RequestMapping(value = "getTurnoverRate")
	public  String getTurnoverRate(String start,String end,Model model) throws ParseException{
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMM");
		if(StringUtils.isBlank(start)||StringUtils.isBlank(end)){
			
			Date today=new Date();
			start=dateFormat.format(DateUtils.addMonths(today,-4));
			end=dateFormat.format(DateUtils.addMonths(today,-1));
		}
		List<String> monthList=Lists.newArrayList();
		Date startDate=dateFormat.parse(start);
		Date endDate=dateFormat.parse(end);
		while(endDate.after(startDate)||endDate.equals(startDate)){
			String key = dateFormat.format(startDate);
			monthList.add(key);
			startDate = DateUtils.addMonths(startDate, 1);
		}	
		model.addAttribute("monthList", monthList);
		Map<String, Map<String, Map<String, PsiInventoryTurnoverData>>> map=turnoverDataService.getTurnoverRate(start, end, "1","2");
		model.addAttribute("map", map);
		model.addAttribute("start", dateFormat.parse(start));
		model.addAttribute("end", dateFormat.parse(end));
		List<PsiProductTypeGroupDict> lineList=groupDictService.getAllList();
		model.addAttribute("lineList", lineList);
		model.addAttribute("lineSize", lineList.size());
        Map<String,Map<String,String>> groupMap=groupUserService.getSingleGroupUser();
		model.addAttribute("groupMap", groupMap);
		return "modules/psi/countTurnOverDataList";
	}
	
	
	@RequestMapping(value = "exportTurnoverRateByLine")
	public  String exportTurnoverRateByLine(String start,String end,Model model, HttpServletRequest request, HttpServletResponse response) throws ParseException{
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMM");
		SimpleDateFormat dayFormat=new SimpleDateFormat("yyyyMMdd");
		if(StringUtils.isBlank(start)||StringUtils.isBlank(end)){
			Date today=new Date();
			start=dateFormat.format(DateUtils.addMonths(today,-4));
			end=dateFormat.format(DateUtils.addMonths(today,-1));
		}
		Date startDate=null;
		Date endDate=null;
		try {
			startDate = dayFormat.parse(start+"01");
			endDate = dayFormat.parse(end+"31");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//产品名称 品线 市场 运营负责人 周转率标准 实际 
		//时间 [国家[产品/类型/线  PsiInventoryTurnoverData]]
		Map<String,PsiProductEliminate> productCountryAttrMap=psiProductEliminateService.findProductCountryAttr();
		Map<String, Map<String, Map<String, PsiInventoryTurnoverData>>> map=turnoverDataService.getTurnoverRate2(start, end, "1",null,productCountryAttrMap);
        Map<String,Map<String,String>> groupMap=groupUserService.getSingleGroupUser();
        Map<String,Map<String,Float>> starandMap=psiProductInStockService.findYearTurnoverStarand(startDate,endDate,productCountryAttrMap);
        
        Map<String,PsiProductTypeGroupDict> nameAndLineMap=groupDictService.getLineByProductName();//产品名-产品线名称
      //带电源+KeyBoard产品
      	List<String> keyBoardAndHasPowerList=saleReportService.findKeyBoardAndHasPowerAllProduct();
		Map<String,Map<String,Integer>> finalMap=Maps.newHashMap();
	 	List<String> title=Lists.newArrayList("产品品线","月份","市场","运营负责人","<50","50~60","60~70","70~80","80~90","90~100");
        HSSFWorkbook wb = new HSSFWorkbook();
  		HSSFSheet sheet = wb.createSheet();
  		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));

		
		int  rowIndex=0;
		HSSFRow row = sheet.createRow(rowIndex++);
  		HSSFCell cell = null;
  		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short) i);
		}
  		DateFormat monthFormatData= new SimpleDateFormat("yyyyMM");
  		DateFormat dayFormatData= new SimpleDateFormat("yyyyMMdd");
        if(map!=null&&map.size()>0){
        	for ( Map.Entry<String, Map<String, Map<String, PsiInventoryTurnoverData>>> entry: map.entrySet()) {
        		String month = entry.getKey();
        		Float ratio=0.84f;
        		if(month.endsWith("02")){
        			ratio=0.77f;
        		}else if(month.endsWith("03")){
        			ratio=0.90f;
        		}else if(month.endsWith("04")){
        			ratio=0.96f;
        		}else if(month.endsWith("05")){
        			ratio=1f;
        		}else if(month.endsWith("06")){
        			ratio=0.95f;
        		}else if(month.endsWith("07")){
        			ratio=0.56f;
        		}else if(month.endsWith("08")){
        			ratio=0.62f;
        		}else if(month.endsWith("09")){
        			ratio=0.68f;
        		}else if(month.endsWith("10")){
        			ratio=0.73f;
        		}else if(month.endsWith("11")){
        			ratio=0.67f;
        		}else if(month.endsWith("12")){
        			ratio=0.63f;
        		}
        		Map<String, Map<String, PsiInventoryTurnoverData>> monthMap=entry.getValue();
        		for (Map.Entry<String, Map<String, PsiInventoryTurnoverData>> entry1: monthMap.entrySet()) {
        			String country = entry1.getKey();
        			Map<String, PsiInventoryTurnoverData> countryMap=entry1.getValue();
        			for (Map.Entry<String, PsiInventoryTurnoverData> entry2: countryMap.entrySet()) {
        				String name = entry2.getKey();
        				if("fr,it,es".contains(country)||("uk".equals(country)&&!keyBoardAndHasPowerList.contains(name))){
        					continue;
        				}
        				String key=name+"_"+country;
        				if(productCountryAttrMap.get(key)==null){
        					continue;
        				}
        				PsiProductEliminate ate=productCountryAttrMap.get(key);
        				//在售非两个月新品
        				if("淘汰".equals(ate.getIsSale())&&ate.getEliminateTime()==null){
        					continue;
        				}
        				if("淘汰".equals(ate.getIsSale())&&ate.getEliminateTime().before(monthFormatData.parse(month))){
        					continue;
        				}
        				if("新品".equals(ate.getIsNew())&&monthFormatData.parse(month).after(DateUtils.addMonths(dayFormatData.parse(ate.getAddedMonth()),2))){
        					continue;
        				}
        				String tempKey="";
        				if("de,fr,it,es,uk,jp".contains(country)){
        					tempKey=name+"_eu";
        				}else if("com,ca".contains(country)){
        					tempKey=name+"_com";
        				}
        				if(starandMap.get(month)==null||starandMap.get(month).get(tempKey)==null){
        					continue;
        				}
        				int j=0;
        				PsiInventoryTurnoverData data=countryMap.get(name);
        			    float starand=ratio*starandMap.get(month).get(tempKey)/12;
        				
        			    String salesName="";
        			    String lineName="";
        			    if(nameAndLineMap.get(name)!=null){
        					String lineId=nameAndLineMap.get(name).getId();
        					lineName=nameAndLineMap.get(name).getName();
        					 if(groupMap.get(lineId)!=null&&groupMap.get(lineId).get(country)!=null){
        						 salesName=groupMap.get(lineId).get(country).split(",")[1];
        					 }
        				}
        			    
        				float actual=0f;
                        if(!keyBoardAndHasPowerList.contains(name)&&"de".equals(country)){
                        	data=monthMap.get("eu").get(name);
                        	if(data.getePrice()>0){
            					actual=data.getsPrice()/data.getePrice();
            				}
        				}else{
        					if(data.getePrice()>0){
            					actual=data.getsPrice()/data.getePrice();
            				}else{
            					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
            				}
        				}
        				String type="0";
    					Float percent=actual*100f/starand;
        				if(percent<50){
        					type="0";
        				}else if(percent>=50&&percent<60){
        					type="1";
        				}else if(percent>=60&&percent<70){
        					type="2";
        				}else if(percent>=70&&percent<80){
        					type="3";
        				}else if(percent>=80&&percent<90){
        					type="4";
        				}else if(percent>=90){
        					type="5";
        				}
        				String totalKey="";
        				if("de,fr,it,es,jp".contains(country)){//nonEn 
        					totalKey=lineName+";"+month+";nonEn;"+salesName;
        					
        				}else if("uk,com,ca".contains(country)){//en
        					totalKey=lineName+";"+month+";en;"+salesName;
        				}
        				Map<String,Integer> temp=finalMap.get(totalKey);
    					if(temp==null){
    						temp=Maps.newHashMap();
    						finalMap.put(totalKey, temp);
    					}
    					Integer quantity=temp.get(type);
    					temp.put(type,(quantity==null?0:quantity)+1);
    					
    					Integer totalQuantity=temp.get("total");
    					temp.put("total",(totalQuantity==null?0:totalQuantity)+1);
					}
				}
			}
        }
        
        if(finalMap!=null&&finalMap.size()>0){//"产品品线","月份","市场","运营负责人"
        	for (Map.Entry<String, Map<String, Integer>> entry: finalMap.entrySet()) {
        		String key = entry.getKey();
				String[] arr=key.split(";");
				int j=0;
				Map<String,Integer> tempMap=entry.getValue();
				row = sheet.createRow(rowIndex++);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(arr[0]);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(arr[1]);
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("nonEn".equals(arr[2])?"非英语国家":"英语国家");
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(arr[3]);
				if(tempMap!=null&&tempMap.get("0")!=null){
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(tempMap.get("0")*1.0f/tempMap.get("total"));
				}else{
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
				}
				row.getCell(j-1).setCellStyle(style1);
				if(tempMap!=null&&tempMap.get("1")!=null){
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(tempMap.get("1")*1.0f/tempMap.get("total"));
					
				}else{
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
				}
				row.getCell(j-1).setCellStyle(style1);
				if(tempMap!=null&&tempMap.get("2")!=null){
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(tempMap.get("2")*1.0f/tempMap.get("total"));
					
				}else{
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
				}
				row.getCell(j-1).setCellStyle(style1);
				if(tempMap!=null&&tempMap.get("3")!=null){
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(tempMap.get("3")*1.0f/tempMap.get("total"));
					
				}else{
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
				}
				row.getCell(j-1).setCellStyle(style1);
				if(tempMap!=null&&tempMap.get("4")!=null){
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(tempMap.get("4")*1.0f/tempMap.get("total"));
					
				}else{
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
				}
				row.getCell(j-1).setCellStyle(style1);
				if(tempMap!=null&&tempMap.get("5")!=null){
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(tempMap.get("5")*1.0f/tempMap.get("total"));
					
				}else{
					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(0);
				}
				row.getCell(j-1).setCellStyle(style1);
			}
        }
        
        for (int i = 0; i < title.size(); i++) {
			sheet.autoSizeColumn((short) i);
		}

        try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "按月按运营周转率" + sdf.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value = "exportTurnoverRate")
	public  String exportTurnoverRate(String start,String end,Model model, HttpServletRequest request, HttpServletResponse response) throws ParseException{
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMM");
		SimpleDateFormat dayFormat=new SimpleDateFormat("yyyyMMdd");
		if(StringUtils.isBlank(start)||StringUtils.isBlank(end)){
			Date today=new Date();
			start=dateFormat.format(DateUtils.addMonths(today,-4));
			end=dateFormat.format(DateUtils.addMonths(today,-1));
		}
		Date startDate=null;
		Date endDate=null;
		try {
			startDate = dayFormat.parse(start+"01");
			endDate = dayFormat.parse(end+"31");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//产品名称 品线 市场 运营负责人 周转率标准 实际 
		//时间 [国家[产品/类型/线  PsiInventoryTurnoverData]]
		Map<String,PsiProductEliminate> productCountryAttrMap=psiProductEliminateService.findProductCountryAttr();
		Map<String, Map<String, Map<String, PsiInventoryTurnoverData>>> map=turnoverDataService.getTurnoverRate2(start, end, "1",null,productCountryAttrMap);
        Map<String,Map<String,String>> groupMap=groupUserService.getSingleGroupUser();
        Map<String,Map<String,Float>> starandMap=psiProductInStockService.findYearTurnoverStarand(startDate,endDate,productCountryAttrMap);
       
        Map<String,PsiProductTypeGroupDict> nameAndLineMap=groupDictService.getLineByProductName();//产品名-产品线名称
      //带电源+KeyBoard产品
      	List<String> keyBoardAndHasPowerList=saleReportService.findKeyBoardAndHasPowerAllProduct();
      	Map<String,Map<String,PsiProductEliminate>> isNewMap=psiProductEliminateService.findAllByNameAndCountry();
		
	 	List<String> title=Lists.newArrayList("产品","产品品线","在售","新品","月份","市场","运营负责人","周转率标准","实际");
        HSSFWorkbook wb = new HSSFWorkbook();
  		HSSFSheet sheet = wb.createSheet();
  		HSSFCellStyle style1 = wb.createCellStyle();
		HSSFDataFormat df1 = wb.createDataFormat(); 
		style1.setDataFormat(df1.getFormat("#0.00")); 
		
		HSSFCellStyle style2 = wb.createCellStyle();
		HSSFDataFormat df2 = wb.createDataFormat(); 
		style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
		style2.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style2.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style2.setDataFormat(df2.getFormat("#0.00"));
		
		HSSFCellStyle style3 = wb.createCellStyle();
		style3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); 
		style3.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style3.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		
		int  rowIndex=0;
		HSSFRow row = sheet.createRow(rowIndex++);
  		HSSFCell cell = null;
  		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short) i);
		}
  		DateFormat monthFormatData= new SimpleDateFormat("yyyyMM");
  		DateFormat dayFormatData= new SimpleDateFormat("yyyyMMdd");
        if(map!=null&&map.size()>0){
//        	for (String month: map.keySet()) {
        	for ( Map.Entry<String, Map<String, Map<String, PsiInventoryTurnoverData>>> entry: map.entrySet()) {
        		String month = entry.getKey();
        		Float ratio=0.84f;
        		if(month.endsWith("02")){
        			ratio=0.77f;
        		}else if(month.endsWith("03")){
        			ratio=0.90f;
        		}else if(month.endsWith("04")){
        			ratio=0.96f;
        		}else if(month.endsWith("05")){
        			ratio=1f;
        		}else if(month.endsWith("06")){
        			ratio=0.95f;
        		}else if(month.endsWith("07")){
        			ratio=0.56f;
        		}else if(month.endsWith("08")){
        			ratio=0.62f;
        		}else if(month.endsWith("09")){
        			ratio=0.68f;
        		}else if(month.endsWith("10")){
        			ratio=0.73f;
        		}else if(month.endsWith("11")){
        			ratio=0.67f;
        		}else if(month.endsWith("12")){
        			ratio=0.63f;
        		}
        		Map<String, Map<String, PsiInventoryTurnoverData>> monthMap=entry.getValue();
//        		for (String country: monthMap.keySet()) {
//        			Map<String, PsiInventoryTurnoverData> countryMap=monthMap.get(country);
//        			int allProduct=0;
//        			int suitProduct=0;
//        			
//        			for (String name: countryMap.keySet()) {
        		for (Map.Entry<String, Map<String, PsiInventoryTurnoverData>> entry1: monthMap.entrySet()) {
        			String country = entry1.getKey();
        			Map<String, PsiInventoryTurnoverData> countryMap=entry1.getValue();
        			int allProduct=0;
        			int suitProduct=0;
        			for (Map.Entry<String, PsiInventoryTurnoverData> entry2: countryMap.entrySet()) {
        				String name = entry2.getKey();
        				if("fr,it,es".contains(country)||("uk".equals(country)&&!keyBoardAndHasPowerList.contains(name))){
        					continue;
        				}
        				
        				String key=name+"_"+country;
        				if(productCountryAttrMap.get(key)==null){
        					continue;
        				}
        				PsiProductEliminate ate=productCountryAttrMap.get(key);
        				//在售非两个月新品
        				if("淘汰".equals(ate.getIsSale())&&ate.getEliminateTime()==null){
        					continue;
        				}
        				if("淘汰".equals(ate.getIsSale())&&ate.getEliminateTime().before(monthFormatData.parse(month))){
        					continue;
        				}
        				if("新品".equals(ate.getIsNew())&&monthFormatData.parse(month).after(DateUtils.addMonths(dayFormatData.parse(ate.getAddedMonth()),2))){
        					continue;
        				}
        				String tempKey="";
        				if("de,fr,it,es,uk,jp".contains(country)){
        					tempKey=name+"_eu";
        				}else if("com,ca".contains(country)){
        					tempKey=name+"_com";
        				}
        				if(starandMap.get(month)==null||starandMap.get(month).get(tempKey)==null){
        					continue;
        				}
        				int j=0;
        				PsiInventoryTurnoverData data=entry2.getValue();
        				row = sheet.createRow(rowIndex++);
        				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
        				if(nameAndLineMap.get(name)!=null){
        					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(nameAndLineMap.get(name).getName());
        				}else{
        					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
        				}
        				
        				if(isNewMap.get(name)!=null&&isNewMap.get(name).get(country)!=null){
            				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(DictUtils.getDictLabel(isNewMap.get(name).get(country).getIsSale(), "product_position", ""));
            				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("0".equals(isNewMap.get(name).get(country).getIsNew())?"普通品":"新品");
            			}else{
            				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
            				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
            			}
            			
        				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(month);
        				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(country);
        				if(nameAndLineMap.get(name)!=null){
        					String lineId=nameAndLineMap.get(name).getId();
        					 if(groupMap.get(lineId)!=null&&groupMap.get(lineId).get(country)!=null){
        						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(groupMap.get(lineId).get(country).split(",")[1]);
        					 }else{
        						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
        					 }
        				}else{
        					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
        				}
        				
        			    float starand=ratio*starandMap.get(month).get(tempKey)/12;
        				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(starand);
        				row.getCell(j-1).setCellStyle(style1);
        				
        				float actual=0f;
                        if(!keyBoardAndHasPowerList.contains(name)&&"de".equals(country)){
                        	data=monthMap.get("eu").get(name);
                        	if(data.getePrice()>0){
            					actual=data.getsPrice()/data.getePrice();
            					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.getsPrice()/data.getePrice());
                				row.getCell(j-1).setCellStyle(style1);
            				}else{
            					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
            				}
        				}else{
        					if(data.getePrice()>0){
            					actual=data.getsPrice()/data.getePrice();
            					row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.getsPrice()/data.getePrice());
                				row.getCell(j-1).setCellStyle(style1);
            				}else{
            					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
            				}
        				}
        				
        				allProduct++;
        				if(actual>=starand){
        					suitProduct++;
        				}
					}
        			//country
        			if(allProduct>0){
        				row = sheet.createRow(rowIndex++);
            			Float percent=suitProduct*100f/allProduct;
            			row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("达标率");
            			row.getCell(0).setCellStyle(style3);
            			row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(percent);
            			row.getCell(1).setCellStyle(style2);
            			row.createCell(2,Cell.CELL_TYPE_STRING).setCellValue("");
            			row.getCell(2).setCellStyle(style3);
            			row.createCell(3,Cell.CELL_TYPE_STRING).setCellValue("");
            			row.getCell(3).setCellStyle(style3);
            			row.createCell(4,Cell.CELL_TYPE_STRING).setCellValue("");
            			row.getCell(4).setCellStyle(style3);
            			row.createCell(5,Cell.CELL_TYPE_STRING).setCellValue("");
            			row.getCell(5).setCellStyle(style3);
        			}
        			
				}
        		
        		
			}
        }
        try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "按月分产品周转率" + sdf.format(new Date()) + ".xls";
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
	
	@RequestMapping(value ="singleDateExport")
	public String singleDateExport(Integer warehouseId,String endDate, HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) throws ParseException {
	
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		if(sdf1.parse(sdf1.format(new Date())).compareTo(sdf1.parse(endDate))<=0){
			addMessage(redirectAttributes, "不能选择当天的时间，因为每天库存是23点才保存的");
			return "redirect:"+Global.getAdminPath()+"/psi/psiInventory?warehouse.id="+warehouseId;
		}
		//获得各个本地仓的数据
		Map<String,Integer> localMap=this.psiInventoryService.getInventoryByDate(warehouseId,endDate);
		Map<String,Integer> fbaMap=Maps.newHashMap();
		//获得fba当天的数据
		String countryCode="";
		String subject="";
		if(warehouseId.intValue()==120){
			countryCode="am";
			subject+=endDate+"美国仓库存数据";
		}else if(warehouseId.intValue()==19){
			countryCode="eu";
			subject+=endDate+"德国仓库存数据";
		}else if(warehouseId.intValue()==21){
			subject+=endDate+"[春雨]中国仓库存数据";
		}else if(warehouseId.intValue()==130){
			subject+=endDate+"[理诚]中国仓库存数据";
		}
		
		if(StringUtils.isNotEmpty(countryCode)){
			fbaMap=this.fbaService.getFbaInventoryByDataDate(countryCode, endDate);
		}
		
		
	 	List<String> title=Lists.newArrayList("产品型号+颜色","单位","数量","成本单价","成本金额", "产品描述");
        HSSFWorkbook wb = new HSSFWorkbook();
  		HSSFSheet sheet = wb.createSheet();
  		HSSFCellStyle style1 = wb.createCellStyle();
		HSSFDataFormat df1 = wb.createDataFormat();  //此处设置数据格式  
		style1.setDataFormat(df1.getFormat("#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));  
		
		int  rowIndex=0;
		short rowHeight=500;
		HSSFRow row = sheet.createRow(rowIndex++);
		row.createCell(0).setCellValue(subject);
		row.setHeight(rowHeight);
		sheet.addMergedRegion(new Region(0, new Short("0"), 0, new Short("6"))); 
		
		
		row = sheet.createRow(rowIndex++);
		
  		HSSFCell cell = null;
  		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			sheet.autoSizeColumn((short) i);
		}
  		
		//查询所有产品信息
		List<Object[]> productColorInfos=this.psiProductService.findProducColorInfo();
  		for(Object[] proInfo:productColorInfos){
			String productName = proInfo[0].toString();
			String chineseName = proInfo[1].toString();
			String desc        = proInfo[2].toString();
			
			row = sheet.createRow(rowIndex++);
			row.setHeight(rowHeight);
			int j=0;
			//("产品型号+颜色","单位","数量","成本单价","成本金额", "产品描述");
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(productName);
			
			String unit ="";
			if(chineseName.contains("(")){
				unit=chineseName.substring(chineseName.indexOf("(")+1,chineseName.indexOf(")"));
			}
			int quantity=0;
			if(localMap.get(productName)!=null){
				quantity+=localMap.get(productName);
			}
			if(fbaMap.get(productName)!=null){
				quantity+=fbaMap.get(productName);
			}
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unit);
			
			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(quantity);
			
			float price =0f;
			/*if(productPriceMap.get(productName)!=null){
				if(productPriceMap.get(productName).get("CNY")!=null){
					price=productPriceMap.get(productName).get("CNY");
				}else if(productPriceMap.get(productName).get("USD")!=null){
					price=productPriceMap.get(productName).get("USD")*AmazonProduct2Service.getRateConfig().get("USD/CNY");
				}
				
			}*/
			cell = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style1);
			cell.setCellValue(price);
			
			cell = row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style1);
			cell.setCellValue(quantity*price);
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(desc);
		}
		
		
   		try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
				String fileName = "psiInventory" + sdf.format(new Date()) + ".xls";
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
	
	public  Map<String,Float> countFbaFee(String name,Map<String,AmazonPostsDetail> sizeMap,Map<String, String> isEuMap){
		Map<String,Float>  map=Maps.newHashMap();
		for (Map.Entry<String,AmazonPostsDetail> entry: sizeMap.entrySet()) {
			String country=entry.getKey();
			AmazonPostsDetail detail=entry.getValue();

			boolean flag=true;
			if("1".equals(isEuMap.get(name))){//不泛欧
				flag=false;
			}
			
			float length=detail.getPackageLength();
			float width=detail.getPackageWidth();
			float height=detail.getPackageHeight();
			float weight=detail.getPackageWeight();
			
			float length1=detail.getPackageLength()*2.54f;
			float width1=detail.getPackageWidth()*2.54f;
			float height1=detail.getPackageHeight()*2.54f;
			float weight1=detail.getPackageWeight()*0.4535924f;
			
			float fbaFee=0f;
			float fbaFeeEu=0f;
			if("com".equals(country)){
				float vw=length*width*height/139;
				if(vw<=weight){
		    		vw=weight;
		    	}
				float girth = 2 * (width+height);
				if(weight<=1&&length<=15&&width<=12&&height<=0.75){//Small standard-size
					fbaFee=2.41f;
		    	}else if(weight<=20&&length<=18&&width<=14&&height<=8){//Large standard-size
		    	    vw=vw+0.25f;
		    	    if(vw<=1){
		    	    	fbaFee=3.19f;
		    	    }else if(vw>1&&vw<=2){
		    	    	fbaFee=4.71f;
		    	    }else{
		    	    	fbaFee=4.71f+0.38f*(vw-2);
		    	    }
		    	}else if(weight<=70&&length<=60&&width<=30&&girth<=130){//Small oversize
		    		vw=vw+1;
		    		fbaFee=8.13f+0.38f*(vw-2);
		    	}else if(weight<=150&&length<=108&&girth<=130){//Medium oversize
		    		vw=vw+1;
		    		fbaFee=9.44f+0.38f*(vw-2);
		    	}else if(weight<=150&&length<=108&&girth<=165){//Large oversize
		    		vw=vw+1;
		    		fbaFee=73.18f+0.79f*(vw-90);
		    	}else if(weight>150||length>108&&girth>165){//Special oversize
		    		vw=weight+1;
		    		fbaFee=137.32f+0.91f*(vw-90);
		    	}
			}else if("de".equals(country)){
				if(length1<=20&&width1<=15&&height1<=1){
					weight1=weight1+0.02f;
				}else if(length1<=33&&width1<=23&&height1<=2.5){
					weight1=weight1+0.04f;
				}else if(length1<=33&&width1<=23&&height1<=5){
					weight1=weight1+0.04f;
				}else if(length1<=45&&width1<=34&&height1<=26){
					weight1=weight1+0.1f;
				}else if(length1<=61&&width1<=46&&height1<=46){
					weight1=weight1+0.24f;
				}else if(length1<=120&&width1<=60&&height1<=60){
					weight1=weight1+0.24f;
				}else{
					weight1=weight1+0.24f;
				}
				if(length1<=20&&width1<=15&&height1<=1&&weight1*1000<=100){
					fbaFee=1.64f;
				}else if(length1<=33&&width1<=23&&height1<=2.5&&weight1*1000<=500){
					if(weight1*1000<101){
						fbaFee=1.81f;
					}else if(weight1*1000>=101&&weight1*1000<251){
						fbaFee=1.82f;
					}else{
						fbaFee=1.95f;
					}
				}else if(length1<=33&&width1<=23&&height1<=5&&weight1*1000<=1000){
					fbaFee=2.34f;
				}else if(length1<=45&&width1<=34&&height1<=26&&weight1<=12){
					if(weight1*1000<251){
						fbaFee=2.39f;
					}else if(weight1*1000>=251&&weight1*1000<501){
						fbaFee=2.5f;
					}else if(weight1*1000>=501&&weight1*1000<1001){
						fbaFee=3.08f;
					}else if(weight1*1000>=1001&&weight1*1000<1501){
						fbaFee=3.62f;
					}else if(weight1*1000>=1501&&weight1*1000<2001){
						fbaFee=3.66f;
					}else if(weight1*1000>=2001&&weight1*1000<3001){
						fbaFee=4.34f;
					}else if(weight1*1000>=3001&&weight1*1000<4001){
						fbaFee=4.36f;
					}else if(weight1*1000>=4001&&weight1*1000<5001){
						fbaFee=4.37f;
					}else if(weight1*1000>=5001&&weight1*1000<6001){
						fbaFee=4.7f;
					}else if(weight1*1000>=6001&&weight1*1000<7001){
						fbaFee=4.7f;
					}else if(weight1*1000>=7001&&weight1*1000<8001){
						fbaFee=4.83f;
					}else if(weight1*1000>=8001&&weight1*1000<9001){
						fbaFee=4.83f;
					}else if(weight1*1000>=9001&&weight1*1000<10001){
						fbaFee=4.83f;
					}else if(weight1*1000>=10001&&weight1*1000<11001){
						fbaFee=4.99f;
					}else if(weight1*1000>=11001&&weight1*1000<12001){
						fbaFee=5f;
					}
				}else if(length1<=61&&width1<=46&&height1<=46&&weight1*1000<=2000){
					if(weight1*1000<1001){
						fbaFee=5.03f;
					}else if(weight1*1000>=1001&&weight1*1000<1251){
						fbaFee=5.14f;
					}else if(weight1*1000>=1251&&weight1*1000<1501){
						fbaFee=5.18f;
					}else if(weight1*1000>=1501&&weight1*1000<1751){
						fbaFee=5.18f;
					}else if(weight1*1000>=1751&&weight1*1000<2000){
						fbaFee=5.25f;
					}
				}else if(length1<=120&&width1<=60&&height1<=60&&weight1<=30){
					
					if(weight1<=1){
						fbaFee=5.01f;
					}else if(weight1<=2){
						fbaFee=5.21f;
					}else if(weight1<=3){
						fbaFee=6.13f;
					}else if(weight1<=4){
						fbaFee=6.18f;
					}else if(weight1<=5){
						fbaFee=6.18f;
					}else if(weight1<=6){
						fbaFee=6.38f;
					}else if(weight1<=7){
						fbaFee=6.47f;
					}else if(weight1<=8){
						fbaFee=6.52f;
					}else if(weight1<=9){
						fbaFee=6.52f;
					}else if(weight1<=10){
						fbaFee=6.55f;
					}else if(weight1<=15){
						fbaFee=7.1f;
					}else if(weight1<=20){
						fbaFee=7.55f;
					}else if(weight1<=25){
						fbaFee=8.55f;
					}else if(weight1<=30){
						fbaFee=8.55f;
					}
				}else{
					if(weight1<=5){
						fbaFee=6.71f;
					}else if(weight1<=10){
						fbaFee=7.74f;
					}else if(weight1<=15){
						fbaFee=8.28f;
					}else if(weight1<=20){
						fbaFee=8.75f;
					}else if(weight1<=25){
						fbaFee=9.69f;
					}else if(weight1<=30){
						fbaFee=9.71f;
					}
				}
			}else if("uk".equals(country)){
				
					if(length1<=20&&width1<=15&&height1<=1){
						weight1=weight1+0.02f;
					}else if(length1<=33&&width1<=23&&height1<=2.5){
						weight1=weight1+0.04f;
					}else if(length1<=33&&width1<=23&&height1<=5){
						weight1=weight1+0.04f;
					}else if(length1<=45&&width1<=34&&height1<=26){
						weight1=weight1+0.1f;
					}else if(length1<=61&&width1<=46&&height1<=46){
						weight1=weight1+0.24f;
					}else if(length1<=120&&width1<=60&&height1<=60){
						weight1=weight1+0.24f;
					}else{
						weight1=weight1+0.24f;
					}

					if(length1<=20&&width1<=15&&height1<=1&&weight1*1000<=100){
						fbaFee=1.34f;
					}else if(length1<=33&&width1<=23&&height1<=2.5&&weight1*1000<=500){
						if(weight1*1000<101){
							fbaFee=1.47f;
						}else if(weight1*1000>=101&&weight1*1000<251){
							fbaFee=1.62f;
						}else{
							fbaFee=1.72f;
						}
					}else if(length1<=33&&width1<=23&&height1<=5&&weight1*1000<=1000){
						fbaFee=1.97f;
					}else if(length1<=45&&width1<=34&&height1<=26&&weight1<=12){
						if(weight1*1000<251){
							fbaFee=1.98f;
						}else if(weight1*1000>=251&&weight1*1000<501){
							fbaFee=2.09f;
						}else if(weight1*1000>=501&&weight1*1000<1001){
							fbaFee=2.17f;
						}else if(weight1*1000>=1001&&weight1*1000<1501){
							fbaFee=2.31f;
						}else if(weight1*1000>=1501&&weight1*1000<2001){
							fbaFee=2.53f;
						}else if(weight1*1000>=2001&&weight1*1000<3001){
							fbaFee=3.61f;
						}else if(weight1*1000>=3001&&weight1*1000<4001){
							fbaFee=3.61f;
						}else if(weight1*1000>=4001&&weight1*1000<5001){
							fbaFee=3.71f;
						}else if(weight1*1000>=5001&&weight1*1000<6001){
							fbaFee=3.76f;
						}else if(weight1*1000>=6001&&weight1*1000<7001){
							fbaFee=3.76f;
						}else if(weight1*1000>=7001&&weight1*1000<8001){
							fbaFee=3.85f;
						}else if(weight1*1000>=8001&&weight1*1000<9001){
							fbaFee=3.85f;
						}else if(weight1*1000>=9001&&weight1*1000<10001){
							fbaFee=3.85f;
						}else if(weight1*1000>=10001&&weight1*1000<11001){
							fbaFee=3.86f;
						}else if(weight1*1000>=11001&&weight1*1000<12001){
							fbaFee=4f;
						}
					}else if(length1<=61&&width1<=46&&height1<=46&&weight1*1000<=2000){
						if(weight1*1000<1001){
							fbaFee=3.66f;
						}else if(weight1*1000>=1001&&weight1*1000<1251){
							fbaFee=4.08f;
						}else if(weight1*1000>=1251&&weight1*1000<1501){
							fbaFee=4.39f;
						}else if(weight1*1000>=1501&&weight1*1000<1751){
							fbaFee=4.48f;
						}else if(weight1*1000>=1751&&weight1*1000<2001){
							fbaFee=4.54f;
						}
					}else if(length1<=120&&width1<=60&&height1<=60&&weight1<=30){
						if(weight1<=1){
							fbaFee=4.65f;
						}else if(weight1<=2){
							fbaFee=4.96f;
						}else if(weight1<=3){
							fbaFee=5.05f;
						}else if(weight1<=4){
							fbaFee=5.08f;
						}else if(weight1<=5){
							fbaFee=5.12f;
						}else if(weight1<=6){
							fbaFee=6.04f;
						}else if(weight1<=7){
							fbaFee=6.1f;
						}else if(weight1<=8){
							fbaFee=6.13f;
						}else if(weight1<=9){
							fbaFee=6.13f;
						}else if(weight1<=10){
							fbaFee=6.16f;
						}else if(weight1<=15){
							fbaFee=6.55f;
						}else if(weight1<=20){
							fbaFee=6.88f;
						}else if(weight1<=25){
							fbaFee=7.62f;
						}else if(weight1<=30){
							fbaFee=7.62f;
						}
					}else{
						
						if(weight1<=5){
							fbaFee=5.71f;
						}else if(weight1<=10){
							fbaFee=6.88f;
						}else if(weight1<=15){
							fbaFee=7.27f;
						}else if(weight1<=20){
							fbaFee=7.62f;
						}else if(weight1<=25){
							fbaFee=8.3f;
						}else if(weight1<=30){
							fbaFee=8.32f;
						}
					}
				/*}*/
			}else if("fr,it,es".contains(country)){
				//泛欧和不泛欧-country  用本地    -de 用cross
				if("it".equals(country)){
					if(length1<=20&&width1<=15&&height1<=1){
						weight1=weight1+0.02f;
					}else if(length1<=33&&width1<=23&&height1<=2.5){
						weight1=weight1+0.04f;
					}else if(length1<=33&&width1<=23&&height1<=5){
						weight1=weight1+0.04f;
					}else if(length1<=45&&width1<=34&&height1<=26){
						weight1=weight1+0.1f;
					}else if(length1<=61&&width1<=46&&height1<=46){
						weight1=weight1+0.24f;
					}else if(length1<=120&&width1<=60&&height1<=60){
						weight1=weight1+0.24f;
					}else{
						weight1=weight1+0.24f;
					}
					

					if(length1<=20&&width1<=15&&height1<=1&&weight1*1000<=100){
						fbaFee=2.55f;
					}else if(length1<=33&&width1<=23&&height1<=2.5&&weight1*1000<=500){
						if(weight1*1000<101){
							fbaFee=2.64f;
						}else if(weight1*1000>=101&&weight1*1000<251){
							fbaFee=2.89f;
						}else{
							fbaFee=3.14f;
						}
					}else if(length1<=33&&width1<=23&&height1<=5&&weight1*1000<=1000){
						fbaFee=3.39f;
					}else if(length1<=45&&width1<=34&&height1<=26&&weight1<=11){
						
						if(weight1*1000<251){
							fbaFee=3.52f;
						}else if(weight1*1000>=251&&weight1*1000<501){
							fbaFee=3.78f;
						}else if(weight1*1000>=501&&weight1*1000<1001){
							fbaFee=4.41f;
						}else if(weight1*1000>=1001&&weight1*1000<1501){
							fbaFee=4.75f;
						}else if(weight1*1000>=1501&&weight1*1000<2001){
							fbaFee=4.96f;
						}else if(weight1*1000>=2001&&weight1*1000<3001){
							fbaFee=5.39f;
						}else if(weight1*1000>=3001&&weight1*1000<4001){
							fbaFee=5.92f;
						}else if(weight1*1000>=4001&&weight1*1000<5001){
							fbaFee=6.16f;
						}else if(weight1*1000>=5001&&weight1*1000<6001){
							fbaFee=6.26f;
						}else if(weight1*1000>=6001&&weight1*1000<7001){
							fbaFee=6.26f;
						}else if(weight1*1000>=7001&&weight1*1000<8001){
							fbaFee=6.46f;
						}else if(weight1*1000>=8001&&weight1*1000<9001){
							fbaFee=6.48f;
						}else if(weight1*1000>=9001&&weight1*1000<10001){
							fbaFee=6.62f;
						}else if(weight1*1000>=10001&&weight1*1000<11001){
							fbaFee=6.62f;
						}else if(weight1*1000>=11001&&weight1*1000<12000){
							fbaFee=6.63f;
						}
					}else if(length1<=61&&width1<=46&&height1<=46&&weight1*1000<=2000){
						
						if(weight1*1000<1001){
							fbaFee=6.66f;
						}else if(weight1*1000>=1001&&weight1*1000<1251){
							fbaFee=6.81f;
						}else if(weight1*1000>=1251&&weight1*1000<1501){
							fbaFee=7.05f;
						}else if(weight1*1000>=1501&&weight1*1000<1751){
							fbaFee=7.1f;
						}else if(weight1*1000>=1751&&weight1*1000<2001){
							fbaFee=7.14f;
						}
					}else if(length1<=120&&width1<=60&&height1<=60&&weight1<=30){
						
						if(weight1<=1){
							fbaFee=7.02f;
						}else if(weight1<=2){
							fbaFee=7.14f;
						}else if(weight1<=3){
							fbaFee=7.15f;
						}else if(weight1<=4){
							fbaFee=7.64f;
						}else if(weight1<=5){
							fbaFee=7.68f;
						}else if(weight1<=6){
							fbaFee=8.52f;
						}else if(weight1<=7){
							fbaFee=8.52f;
						}else if(weight1<=8){
							fbaFee=8.64f;
						}else if(weight1<=9){
							fbaFee=8.69f;
						}else if(weight1<=10){
							fbaFee=8.74f;
						}else if(weight1<=15){
							fbaFee=9.68f;
						}else if(weight1<=20){
							fbaFee=9.98f;
						}else if(weight1<=25){
							fbaFee=10.62f;
						}else if(weight1<=30){
							fbaFee=11.15f;
						}
					}else{
						if(weight1<=5){
							fbaFee=7.68f;
						}else if(weight1<=10){
							fbaFee=8.74f;
						}else if(weight1<=15){
							fbaFee=9.63f;
						}else if(weight1<=20){
							fbaFee=9.94f;
						}else if(weight1<=25){
							fbaFee=11.15f;
						}else if(weight1<=30){
							fbaFee=11.22f;
						}
					}
				}else if("es".equals(country)){
					if(length1<=20&&width1<=15&&height1<=1){
						weight1=weight1+0.02f;
					}else if(length1<=33&&width1<=23&&height1<=2.5){
						weight1=weight1+0.04f;
					}else if(length1<=33&&width1<=23&&height1<=5){
						weight1=weight1+0.04f;
					}else if(length1<=45&&width1<=34&&height1<=26){
						weight1=weight1+0.1f;
					}else if(length1<=61&&width1<=46&&height1<=46){
						weight1=weight1+0.24f;
					}else if(length1<=120&&width1<=60&&height1<=60){
						weight1=weight1+0.24f;
					}else{
						weight1=weight1+0.24f;
					}

					if(length1<=20&&width1<=15&&height1<=1&&weight1*1000<=100){
						fbaFee=2.07f;
					}else if(length1<=33&&width1<=23&&height1<=2.5&&weight1*1000<=500){
						if(weight1*1000<101){
							fbaFee=2.4f;
						}else if(weight1*1000>=101&&weight1*1000<251){
							fbaFee=2.61f;
						}else{
							fbaFee=2.82f;
						}
					}else if(length1<=33&&width1<=23&&height1<=5&&weight1*1000<=1000){
						fbaFee=2.93f;
					}else if(length1<=45&&width1<=34&&height1<=26&&weight1<=11){
						
						if(weight1*1000<251){
							fbaFee=2.76f;
						}else if(weight1*1000>=251&&weight1*1000<501){
							fbaFee=3.19f;
						}else if(weight1*1000>=501&&weight1*1000<1001){
							fbaFee=3.41f;
						}else if(weight1*1000>=1001&&weight1*1000<1501){
							fbaFee=3.82f;
						}else if(weight1*1000>=1501&&weight1*1000<2001){
							fbaFee=3.88f;
						}else if(weight1*1000>=2001&&weight1*1000<3001){
							fbaFee=4.41f;
						}else if(weight1*1000>=3001&&weight1*1000<4001){
							fbaFee=4.85f;
						}else if(weight1*1000>=4001&&weight1*1000<5001){
							fbaFee=5.16f;
						}else if(weight1*1000>=5001&&weight1*1000<6001){
							fbaFee=5.25f;
						}else if(weight1*1000>=6001&&weight1*1000<7001){
							fbaFee=5.25f;
						}else if(weight1*1000>=7001&&weight1*1000<8001){
							fbaFee=5.38f;
						}else if(weight1*1000>=8001&&weight1*1000<9001){
							fbaFee=5.38f;
						}else if(weight1*1000>=9001&&weight1*1000<10001){
							fbaFee=5.38f;
						}else if(weight1*1000>=10001&&weight1*1000<11001){
							fbaFee=5.38f;
						}else if(weight1*1000>=11001&&weight1*1000<12001){
							fbaFee=5.39f;
						}
					}else if(length1<=61&&width1<=46&&height1<=46&&weight1*1000<=1750){
						
						if(weight1*1000<1001){
							fbaFee=3.67f;
						}else if(weight1*1000>=1001&&weight1*1000<1251){
							fbaFee=3.67f;
						}else if(weight1*1000>=1251&&weight1*1000<1501){
							fbaFee=3.98f;
						}else if(weight1*1000>=1501&&weight1*1000<1751){
							fbaFee=3.98f;
						}else if(weight1*1000>=1751&&weight1*1000<2001){
							fbaFee=4.23f;
						}
					}else if(length1<=120&&width1<=60&&height1<=60&&weight1<=30){
						
						if(weight1<=1){
							fbaFee=4.02f;
						}else if(weight1<=2){
							fbaFee=4.49f;
						}else if(weight1<=3){
							fbaFee=4.97f;
						}else if(weight1<=4){
							fbaFee=5.02f;
						}else if(weight1<=5){
							fbaFee=5.18f;
						}else if(weight1<=6){
							fbaFee=6.59f;
						}else if(weight1<=7){
							fbaFee=6.71f;
						}else if(weight1<=8){
							fbaFee=6.91f;
						}else if(weight1<=9){
							fbaFee=7.32f;
						}else if(weight1<=10){
							fbaFee=7.62f;
						}else if(weight1<=15){
							fbaFee=8.2f;
						}else if(weight1<=20){
							fbaFee=8.9f;
						}else if(weight1<=25){
							fbaFee=8.9f;
						}else if(weight1<=30){
							fbaFee=9.89f;
						}
					}else{
						
						if(weight1<=5){
							fbaFee=5.18f;
						}else if(weight1<=10){
							fbaFee=7.62f;
						}else if(weight1<=15){
							fbaFee=8.24f;
						}else if(weight1<=20){
							fbaFee=8.9f;
						}else if(weight1<=25){
							fbaFee=9.65f;
						}else if(weight1<=30){
							fbaFee=11.07f;
						}
					}
				}else if("fr".equals(country)){
					if(length1<=20&&width1<=15&&height1<=1){
						weight1=weight1+0.02f;
					}else if(length1<=33&&width1<=23&&height1<=2.5){
						weight1=weight1+0.04f;
					}else if(length1<=33&&width1<=23&&height1<=5){
						weight1=weight1+0.04f;
					}else if(length1<=45&&width1<=34&&height1<=26){
						weight1=weight1+0.1f;
					}else if(length1<=61&&width1<=46&&height1<=46){
						weight1=weight1+0.24f;
					}else if(length1<=120&&width1<=60&&height1<=60){
						weight1=weight1+0.24f;
					}else{
						weight1=weight1+0.24f;
					}

					if(length1<=20&&width1<=15&&height1<=1&&weight1*1000<=100){
						fbaFee=2.11f;
					}else if(length1<=33&&width1<=23&&height1<=2.5&&weight1*1000<=500){
						if(weight1*1000<100){
							fbaFee=2.24f;
						}else if(weight1*1000>=101&&weight1*1000<251){
							fbaFee=2.83f;
						}else{
							fbaFee=3.47f;
						}
					}else if(length1<=33&&width1<=23&&height1<=5&&weight1*1000<=1000){
						fbaFee=4.15f;
					}else if(length1<=45&&width1<=34&&height1<=26&&weight1<=11){
						
						if(weight1*1000<251){
							fbaFee=4.39f;
						}else if(weight1*1000>=251&&weight1*1000<501){
							fbaFee=4.98f;
						}else if(weight1*1000>=501&&weight1*1000<1001){
							fbaFee=5.05f;
						}else if(weight1*1000>=1001&&weight1*1000<1501){
							fbaFee=5.16f;
						}else if(weight1*1000>=1501&&weight1*1000<2001){
							fbaFee=5.27f;
						}else if(weight1*1000>=2001&&weight1*1000<3001){
							fbaFee=6.52f;
						}else if(weight1*1000>=3001&&weight1*1000<4001){
							fbaFee=6.54f;
						}else if(weight1*1000>=4001&&weight1*1000<5001){
							fbaFee=6.54f;
						}else if(weight1*1000>=5001&&weight1*1000<6001){
							fbaFee=6.65f;
						}else if(weight1*1000>=6001&&weight1*1000<7001){
							fbaFee=6.65f;
						}else if(weight1*1000>=7001&&weight1*1000<8001){
							fbaFee=6.82f;
						}else if(weight1*1000>=8001&&weight1*1000<9001){
							fbaFee=6.82f;
						}else if(weight1*1000>=9001&&weight1*1000<10001){
							fbaFee=6.82f;
						}else if(weight1*1000>=10001&&weight1*1000<11001){
							fbaFee=6.86f;
						}else if(weight1*1000>=11001&&weight1*1000<12001){
							fbaFee=6.87f;
						}
					}else if(length1<=61&&width1<=46&&height1<=46&&weight1*1000<=2000){
						
						if(weight1*1000<1001){
							fbaFee=6.64f;
						}else if(weight1*1000>=1001&&weight1*1000<1251){
							fbaFee=6.88f;
						}else if(weight1*1000>=1251&&weight1*1000<1501){
							fbaFee=6.96f;
						}else if(weight1*1750>=1501&&weight1*1000<1751){
							fbaFee=6.96f;
						}else if(weight1*1000>=1751&&weight1*1000<2001){
							fbaFee=7.42f;
						}
					}else if(length1<=120&&width1<=60&&height1<=60&&weight1<=30){
						
						if(weight1<=1){
							fbaFee=7.14f;
						}else if(weight1<=2){
							fbaFee=8.15f;
						}else if(weight1<=3){
							fbaFee=8.56f;
						}else if(weight1<=4){
							fbaFee=8.92f;
						}else if(weight1<=5){
							fbaFee=8.98f;
						}else if(weight1<=6){
							fbaFee=9.51f;
						}else if(weight1<=7){
							fbaFee=9.62f;
						}else if(weight1<=8){
							fbaFee=9.67f;
						}else if(weight1<=9){
							fbaFee=9.67f;
						}else if(weight1<=10){
							fbaFee=9.72f;
						}else if(weight1<=15){
							fbaFee=10.39f;
						}else if(weight1<=20){
							fbaFee=10.92f;
						}else if(weight1<=25){
							fbaFee=10.92f;
						}else if(weight1<=30){
							fbaFee=12.16f;
						}
					}else{
						
						if(weight1<=5){
							fbaFee=9.03f;
						}else if(weight1<=10){
							fbaFee=10.95f;
						}else if(weight1<=15){
							fbaFee=11.59f;
						}else if(weight1<=20){
							fbaFee=12.16f;
						}else if(weight1<=25){
							fbaFee=13.29f;
						}else if(weight1<=30){
							fbaFee=13.61f;
						}
					}
				  }
				
				
				
				
				if(!flag){//不泛欧
					 length=detail.getPackageLength();
					 width=detail.getPackageWidth();
					 height=detail.getPackageHeight();
					 weight=detail.getPackageWeight();
					
					 length1=detail.getPackageLength()*2.54f;
					 width1=detail.getPackageWidth()*2.54f;
					 height1=detail.getPackageHeight()*2.54f;
					 weight1=detail.getPackageWeight()*0.4535924f;
					
					if(length1<=20&&width1<=15&&height1<=1){
						weight1=weight1+0.02f;
					}else if(length1<=33&&width1<=23&&height1<=2.5){
						weight1=weight1+0.04f;
					}else if(length1<=33&&width1<=23&&height1<=5){
						weight1=weight1+0.04f;
					}else if(length1<=45&&width1<=34&&height1<=26){
						weight1=weight1+0.1f;
					}else if(length1<=61&&width1<=46&&height1<=46){
						weight1=weight1+0.24f;
					}else if(length1<=120&&width1<=60&&height1<=60){
						weight1=weight1+0.24f;
					}else{
						weight1=weight1+0.24f;
					}
					
					if(length1<=20&&width1<=15&&height1<=1&&weight1*1000<=100){
						fbaFeeEu=2.9f;
					}else if(length1<=33&&width1<=23&&height1<=2.5&&weight1*1000<=500){
						if(weight1*1000<101){
							fbaFeeEu=3.04f;
						}else if(weight1*1000>=101&&weight1*1000<251){
							fbaFeeEu=3.09f;
						}else{
							fbaFeeEu=3.15f;
						}
					}else if(length1<=33&&width1<=23&&height1<=5&&weight1*1000<=1000){
						fbaFeeEu=3.58f;
					}else if(length1<=45&&width1<=34&&height1<=26&&weight1<=12){
						if(weight1*1000<251){
							fbaFeeEu=3.68f;
						}else if(weight1*1000>=251&&weight1*1000<501){
							fbaFeeEu=3.81f;
						}else if(weight1*1000>=501&&weight1*1000<1001){
							fbaFeeEu=4.4f;
						}else if(weight1*1000>=1001&&weight1*1000<1501){
							fbaFeeEu=4.54f;
						}else if(weight1*1000>=1501&&weight1*1000<2001){
							fbaFeeEu=4.61f;
						}else if(weight1*1000>=2001&&weight1*1000<3001){
							fbaFeeEu=5.34f;
						}else if(weight1*1000>=3001&&weight1*1000<4001){
							fbaFeeEu=5.52f;
						}else if(weight1*1000>=4001&&weight1*1000<5001){
							fbaFeeEu=5.54f;
						}else if(weight1*1000>=5001&&weight1*1000<6001){
							fbaFeeEu=5.88f;
						}else if(weight1*1000>=6001&&weight1*1000<7001){
							fbaFeeEu=5.88f;
						}else if(weight1*1000>=7001&&weight1*1000<8001){
							fbaFeeEu=5.9f;
						}else if(weight1*1000>=8001&&weight1*1000<9001){
							fbaFeeEu=5.9f;
						}else if(weight1*1000>=9001&&weight1*1000<10001){
							fbaFeeEu=5.9f;
						}else if(weight1*1000>=10001&&weight1*1000<11001){
							fbaFeeEu=6.28f;
						}else if(weight1*1000>=11001&&weight1*1000<12001){
							fbaFeeEu=6.28f;
						}
					}else if(length1<=61&&width1<=46&&height1<=46&&weight1*1000<=2000){
						if(weight1*1000<1001){
							fbaFeeEu=6.94f;
						}else if(weight1*1000>=1001&&weight1*1000<1251){
							fbaFeeEu=7.16f;
						}else if(weight1*1000>=1251&&weight1*1000<1501){
							fbaFeeEu=7.18f;
						}else if(weight1*1000>=1501&&weight1*1000<1751){
							fbaFeeEu=7.22f;
						}else if(weight1*1000>=1751&&weight1*1000<2001){
							fbaFeeEu=7.24f;
						}
					}else if(length1<=120&&width1<=60&&height1<=60&&weight1<=30){
						if(weight1<=1){
							fbaFeeEu=6.93f;
						}else if(weight1<=2){
							fbaFeeEu=7.13f;
						}else if(weight1<=3){
							fbaFeeEu=7.61f;
						}else if(weight1<=4){
							fbaFeeEu=7.65f;
						}else if(weight1<=5){
							fbaFeeEu=7.71f;
						}else if(weight1<=6){
							fbaFeeEu=8.46f;
						}else if(weight1<=7){
							fbaFeeEu=8.53f;
						}else if(weight1<=8){
							fbaFeeEu=8.53f;
						}else if(weight1<=9){
							fbaFeeEu=8.53f;
						}else if(weight1<=10){
							fbaFeeEu=8.73f;
						}else if(weight1<=15){
							fbaFeeEu=9.25f;
						}else if(weight1<=20){
							fbaFeeEu=10.06f;
						}else if(weight1<=25){
							fbaFeeEu=10.79f;
						}else if(weight1<=30){
							fbaFeeEu=10.95f;
						}
					}else{
						if(weight1<=5){
							fbaFeeEu=7.96f;
						}else if(weight1<=10){
							fbaFeeEu=9.53f;
						}else if(weight1<=15){
							fbaFeeEu=10.13f;
						}else if(weight1<=20){
							fbaFeeEu=11.17f;
						}else if(weight1<=25){
							fbaFeeEu=12.12f;
						}else if(weight1<=30){
							fbaFeeEu=12.12f;
						}
					}
				}
				
			}else if("jp".equals(country)){
				
				if(length1<=25&&width1<=18&&height1<=2&&weight1<0.25){
					fbaFee=226f;
		    	}else if(length1<=45&&width1<=35&&height1<=20&&weight1<9){
		    		if(weight1<2){
		    			fbaFee = 360f;
		    		}else{
		    			fbaFee=360f+MathUtils.roundUp(weight1-2d)*6;
		    		}
		    	}else if((length1>45||width1>35||height1>20)&&weight1<9&&(length1+width1+height1)<170){
		    		if(length1+width1+height1<100){
		    			fbaFee=622f;
		    		}else if(length1+width1+height1>=100&&length1+width1+height1<140){
		    			fbaFee=676f;
		    		}else if(length1+width1+height1>=140&&length1+width1+height1<170){
		    			fbaFee=738f;
		    		}
		    	}else if((length1+width1+height1)>=170&&(length1+width1+height1)<200&&length1<90&&weight1<40){
		    		fbaFee=1398f;
		    	}
				
			}else if("ca".equals(country)){
				float vw=length1*height1*width1/6000;
				if(vw<weight1){
					vw=weight1;
				}
				if(weight1<=0.5&&length1<=38&&width1<=27&&height1<=2){//Small standard-size
					vw=vw+0.025f;
					if(vw>0.1){
						int mod=MathUtils.roundUp((vw-0.1d)/0.1d);
						fbaFee=1.6f+1.9f+mod*0.25f;
					}else{
						fbaFee=1.6f+1.9f;
					}
		    	}else if(weight1<=9&&length1<=45&&width1<=35&&height1<=20){//Small standard-size
		    		vw=vw+0.125f;
		    		if(vw>0.5){
						int mod=MathUtils.roundUp((vw-0.5d)/0.5d);
						fbaFee=1.6f+4f+mod*0.4f;
					}else{
						fbaFee=1.6f+4f;
					}
		    	}else{
		    		vw=vw+0.5f;
		    		if((length1+2*height1+2*width1)>419&&vw>69){
		    			fbaFee=125;
		    		}else{
		    			if(vw>0.5){
		    				int mod=MathUtils.roundUp((vw-0.5d)/0.5d);
							fbaFee=2.65f+4f+mod*0.4f;
						}else{
							fbaFee=2.65f+4f;
						}
		    		}
		    	}
				fbaFee=fbaFee*1.15f;
			}
			
			if(fbaFee>0){
				map.put(country, fbaFee);
			}
		}
		return map;
	}
		
	@RequestMapping(value = "updateRemark")
	@ResponseBody
	public String updateRemark(Integer productId,String countryCode,String colorCode,Integer warehouseId,String remark){
		try {
			remark=URLDecoder.decode(remark, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		psiInventoryService.updateRemark(productId,countryCode,colorCode,warehouseId,remark);
		return "1";
	}
	
}
