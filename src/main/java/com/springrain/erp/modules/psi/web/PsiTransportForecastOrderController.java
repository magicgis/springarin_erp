/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiInventoryTotalDtoByInStock;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductAttribute;
import com.springrain.erp.modules.psi.entity.PsiProductInStock;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.entity.PsiTransportForecastOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportForecastOrderItem;
import com.springrain.erp.modules.psi.scheduler.PsiConfig;
import com.springrain.erp.modules.psi.service.ForecastOrderService;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductInStockService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.psi.service.PsiTransportForecastOrderService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.sys.utils.UserUtils;


@Controller
@RequestMapping(value = "${adminPath}/psi/transportForecastOrder")
public class PsiTransportForecastOrderController extends BaseController {
	
	@Autowired
	private PsiTransportForecastOrderService	psiTransportForecastOrderService;
	@Autowired
	private PsiProductService	psiProductService;
	@Autowired
	private PsiInventoryService	psiInventoryService;
	@Autowired
	private PsiInventoryFbaService	psiInventoryFbaService;
	@Autowired
	private PsiProductInStockService	psiProductInStockService;
	@Autowired
	private PsiProductAttributeService    psiProductAttributeService;
	@Autowired
	private SaleReportService	saleReportService;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	@Autowired
	private ForecastOrderService 	   forecastOrderService;
	@Autowired
	private LcPsiTransportOrderService lcPsiTransportOrderService;
	
	@Autowired
	private PsiProductTypeGroupDictService dictService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiTransportForecastOrder psiTransportForecastOrder,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(psiTransportForecastOrder.getCreateDate()==null){
			psiTransportForecastOrder.setCreateDate(sdf.parse(sdf.format(DateUtils.addMonths(new Date(),-1))));
		}
		if(psiTransportForecastOrder.getUpdateDate()==null){
			psiTransportForecastOrder.setUpdateDate(sdf.parse(sdf.format(new Date())));
		}
		Page<PsiTransportForecastOrder> page = psiTransportForecastOrderService.find(new Page<PsiTransportForecastOrder>(request, response), psiTransportForecastOrder); 
		model.addAttribute("page", page);
		Date startDate=sdf.parse(sdf.format(DateUtils.getMonday(new Date())));
		Boolean canFlag=this.psiTransportForecastOrderService.countOrder(startDate);
		model.addAttribute("canFlag", canFlag);
		return "modules/psi/psiTransportForecastOrderList";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"saveOrderItem"})
	public Integer saveOrderItem(Integer id,String colorCode,String productName,String countryCode,Integer forecastOrderId,Integer safeStock,Integer amazonStock,Integer day31sales,Integer quantity,
			Integer checkQuantity,String model,String transportType,String displaySta,Integer boxNum,String sku,String remark,Integer totalStock,Integer oversea,String transSta) {
		PsiTransportForecastOrderItem item;
		try {
			if(checkQuantity==null){
				checkQuantity=0;
			}
			if(boxNum==null){
				if(productName.contains("Inateck DB1001")){
					if("com,uk,jp,ca,mx,".contains(countryCode)){
						boxNum=60;
					}else{
						boxNum=44;
					}
				}else if(productName.contains("Inateck DB2001")){
					if("com,jp,ca,mx,".contains(countryCode)){
						boxNum=32;
					}else{
						boxNum=24;
					}
				}else{
					Map<String,Integer> packQuantityMap=psiProductService.getPackQuantity();
					if(StringUtils.isNotBlank(colorCode)){
						boxNum=packQuantityMap.get(productName+"_"+colorCode);
					}else{
						boxNum=packQuantityMap.get(productName);
					}
					
				}
			}
			
			item = new PsiTransportForecastOrderItem(id,colorCode,countryCode,URLDecoder.decode(remark, "UTF-8"),null,safeStock,amazonStock,day31sales,quantity,checkQuantity,model,
					transportType,displaySta,boxNum,productName,sku);
			item.setTotalStock(totalStock);
			item.setPsiTransportForecastOrder(psiTransportForecastOrderService.get(forecastOrderId));
			item.setOverseaStock(oversea);
			item.setTransSta(transSta);
			psiTransportForecastOrderService.saveItem(item);
			return item.getId();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping(value = {"deleteItem"})
	public String deleteItem(Integer itemId) {
		try {
			return psiTransportForecastOrderService.deleteItem(itemId);
		} catch (Exception e) {
			return "0";
		}
	}
	
	@RequestMapping(value = {"cancel"})
	public String cancel(Integer id) {
		psiTransportForecastOrderService.cancel(id);
		return "redirect:"+Global.getAdminPath()+"/psi/transportForecastOrder/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = {"checkOrder"})
	public String checkOrder(Integer id) {
		try {
			return psiTransportForecastOrderService.checkOrder(id);
		} catch (Exception e) {
			return "0";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateInfo"})
	public String updateInfo(Integer itemId,String flag,String content) {
		try {
			return psiTransportForecastOrderService.updateInfo(itemId, flag, content);
		} catch (Exception e) {
			return "0";
		}
	}
	
	@RequestMapping(value = {"view"})
	public String view(PsiTransportForecastOrder psiTransportForecastOrder,String lineId,String name,String country,String transModel,String transSta,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		psiTransportForecastOrder=psiTransportForecastOrderService.get(psiTransportForecastOrder.getId());
		Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> map=psiTransportForecastOrderService.getByCountryName(psiTransportForecastOrder.getId(),name,country,transModel,transSta);
		model.addAttribute("map", map);
		List<PsiProductAttribute> attrs = psiProductAttributeService.findAll();
		Map<String,PsiProductAttribute> productAttr = Maps.newHashMap();
		for (PsiProductAttribute psiProductAttribute : attrs) {
			productAttr.put(psiProductAttribute.getColorName(), psiProductAttribute);
		}
		model.addAttribute("productAttr", productAttr);
		model.addAttribute("name", name);
		model.addAttribute("country", country);
		model.addAttribute("transModel", transModel);
		model.addAttribute("transSta", transSta);
		model.addAttribute("psiTransportForecastOrder", psiTransportForecastOrder);
		Map<String,String> nameAndLineMap=groupDictService.getLineByName();//产品名-产品线ID
		List<PsiProductTypeGroupDict> lineList=groupDictService.getAllList();
		model.addAttribute("nameAndLineMap",nameAndLineMap);
		model.addAttribute("lineId", lineId);
		model.addAttribute("lineList",lineList);
		Map<String, String> powerMap=psiProductService.getHasChargedByName();
		model.addAttribute("powerMap",powerMap);
		return "modules/psi/psiTransportForecastOrderView";
	}
	
	@RequestMapping(value = {"review"})
	public String review(PsiTransportForecastOrder psiTransportForecastOrder,String name,String country,String transModel,String transSta,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		psiTransportForecastOrder=psiTransportForecastOrderService.get(psiTransportForecastOrder.getId());
		Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> map=psiTransportForecastOrderService.getByCountryName(psiTransportForecastOrder.getId(),name,country,transModel,transSta);
		model.addAttribute("map", map);
		List<PsiProductAttribute> attrs = psiProductAttributeService.findAll();
		Map<String,PsiProductAttribute> productAttr = Maps.newHashMap();
		for (PsiProductAttribute psiProductAttribute : attrs) {
			productAttr.put(psiProductAttribute.getColorName(), psiProductAttribute);
		}
		model.addAttribute("productAttr", productAttr);
		List<String> componentsList=psiProductService.findComponents();
		String nameStr=componentsList.toString();
		model.addAttribute("nameStr",nameStr);
		model.addAttribute("name", name);
		model.addAttribute("transModel", transModel);
		model.addAttribute("country", country);
		model.addAttribute("psiTransportForecastOrder", psiTransportForecastOrder);
		model.addAttribute("transSta", transSta);
		Map<String, String> powerMap=psiProductService.getHasChargedByName();
		model.addAttribute("powerMap",powerMap);
		return "modules/psi/psiTransportForecastOrderReview";
	}
	
	@RequestMapping(value = {"updateEuModel"})
	public String updateEuModel(Integer id, RedirectAttributes redirectAttributes){
		psiTransportForecastOrderService.updateEuModel(id);
		addMessage(redirectAttributes,"更新成功");
		return "redirect:"+Global.getAdminPath()+"/psi/transportForecastOrder/review?id="+id;
	}
	
	@RequestMapping(value = {"edit"})
	public String edit(PsiTransportForecastOrder psiTransportForecastOrder,String lineId,String name,String country,String transModel,String transSta,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		psiTransportForecastOrder=psiTransportForecastOrderService.get(psiTransportForecastOrder.getId());
		Map<String,String> nameAndLineMap=groupDictService.getLineByName();//产品名-产品线ID
		List<PsiProductTypeGroupDict> lineList=groupDictService.getAllList();
		Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> map=psiTransportForecastOrderService.getByCountryName(psiTransportForecastOrder.getId(),name,country,transModel,transSta);
		model.addAttribute("map", map);
		List<PsiProductAttribute> attrs = psiProductAttributeService.findAll();
		List<String> componentsList=psiProductService.findComponents();
		String nameStr=componentsList.toString();
		model.addAttribute("nameStr",nameStr);
		Map<String,PsiProductAttribute> productAttr = Maps.newHashMap();
		for (PsiProductAttribute psiProductAttribute : attrs) {
			productAttr.put(psiProductAttribute.getColorName(), psiProductAttribute);
		}
		model.addAttribute("lineList",lineList);
		model.addAttribute("nameAndLineMap",nameAndLineMap);
		model.addAttribute("lineId", lineId);
		model.addAttribute("transModel", transModel);
		model.addAttribute("productAttr", productAttr);
		model.addAttribute("name", name);
		model.addAttribute("country", country);
		model.addAttribute("transSta", transSta);
		model.addAttribute("psiTransportForecastOrder", psiTransportForecastOrder);
		Map<String,Map<String,Integer>> offlineMap=psiInventoryService.findOfflineQuantity();
		model.addAttribute("offlineMap", offlineMap);
		Map<String, String> powerMap=psiProductService.getHasChargedByName();
		model.addAttribute("powerMap",powerMap);
		return "modules/psi/psiTransportForecastOrderEdit";
	}
	
	@RequestMapping(value = {"findTotalStock"})
	@ResponseBody
	public Integer findTotalStock(String name,String country,String sku,String type){
		if(StringUtils.isBlank(type)||"0".equals(type)){
			Map<String,Map<String,Integer>> cnStock=psiTransportForecastOrderService.getCnInventoryProduct1(name,country);
			if(cnStock!=null&&cnStock.get(name)!=null&&cnStock.get(name).get(sku)!=null){
				return cnStock.get(name).get(sku);
			}
		}else{
			Map<String,Map<String,Integer>> cnStock=psiTransportForecastOrderService.getCnInventoryProduct2(name,country);
			if(cnStock!=null&&cnStock.get(name)!=null&&cnStock.get(name).get(sku)!=null){
				return cnStock.get(name).get(sku);
			}
		}
		return 0;
	}
	
	
	@RequestMapping(value = {"findQuantityInfo"})
	@ResponseBody
	public Map<String,Object> findQuantityInfo(String name,String country){
		Map<String,PsiInventoryFba>  amazonStock=psiInventoryService.getProductFbaInfo(name);
		List<String> keyBoardAndHasPowerList=saleReportService.findKeyBoardAndHasPowerAllProduct();
		Map<String,Object> map=Maps.newHashMap();
		Integer fbaStock=0;
		Integer safeStock=0;
		Integer daySales=0;
		PsiInventoryTotalDtoByInStock inventorys = psiInventoryService.getInventoryQuantity(name,null);
		Integer oversea=0;
		if(!keyBoardAndHasPowerList.contains(name)&&"de".equals(country)){//不带电源
			if(inventorys!=null&&inventorys.getQuantityEuro()!=null&&inventorys.getQuantityEuro().get("DE")!=null&&inventorys.getQuantityEuro().get("DE").getNewQuantity()!=null){
				oversea=inventorys.getQuantityEuro().get("DE").getNewQuantity();
			}
			PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_eu");
			if(psiInventoryFba!=null){
				fbaStock=psiInventoryFba.getRealTotal();
			}
			PsiProductInStock stock=psiProductInStockService.getPsiProductInStock("eu",name);
			if(stock!=null){
				safeStock=MathUtils.roundUp(stock.getSafeInventory());
			}
			daySales=MathUtils.roundUp(psiInventoryFbaService.get31Sales("eu",name)/31d);
			//map.put("skuList",psiProductService.getSkuByCountryProduct("eu",name));
			List<String> skuList=psiProductService.getSkuByCountryProduct("eu",name);
			if(skuList!=null&&skuList.size()>0){
				map.put("skuList",skuList);
			}else{
				skuList=Lists.newArrayList();
				skuList.add(name+"_"+country);
				map.put("skuList",skuList);
			}
		}else{
			if("de,fr,it,es,uk".contains(country)&&inventorys!=null&&inventorys.getInventorys()!=null&&inventorys.getInventorys().get(country)!=null
					&&inventorys.getInventorys().get(country).getQuantityInventory()!=null&&inventorys.getInventorys().get(country).getQuantityInventory().get("DE")!=null
					&&inventorys.getInventorys().get(country).getQuantityInventory().get("DE").getNewQuantity()!=null){
						oversea=inventorys.getInventorys().get(country).getQuantityInventory().get("DE").getNewQuantity();
		    }else if("com,ca".contains(country)){
				if(inventorys!=null&&inventorys.getInventorys()!=null&&inventorys.getInventorys().get(country)!=null
						&&inventorys.getInventorys().get(country).getQuantityInventory()!=null&&inventorys.getInventorys().get(country).getQuantityInventory().get("US")!=null
						&&inventorys.getInventorys().get(country).getQuantityInventory().get("US").getNewQuantity()!=null){
							oversea=inventorys.getInventorys().get(country).getQuantityInventory().get("US").getNewQuantity();
						}
			}else if("jp".equals(country)){
				if(inventorys!=null&&inventorys.getInventorys()!=null&&inventorys.getInventorys().get(country)!=null
						&&inventorys.getInventorys().get(country).getQuantityInventory()!=null&&inventorys.getInventorys().get(country).getQuantityInventory().get("JP")!=null
						&&inventorys.getInventorys().get(country).getQuantityInventory().get("JP").getNewQuantity()!=null){
							oversea=inventorys.getInventorys().get(country).getQuantityInventory().get("JP").getNewQuantity();
						}
			}
			PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_"+country);
			if(psiInventoryFba!=null){
				fbaStock=psiInventoryFba.getRealTotal();
			}
			PsiProductInStock stock=psiProductInStockService.getPsiProductInStock(country,name);
			if(stock!=null){
				safeStock=MathUtils.roundUp(stock.getSafeInventory());
			}
			daySales=MathUtils.roundUp(psiInventoryFbaService.get31Sales(country,name)/31d);
			List<String> skuList=psiProductService.getSkuByCountryProduct(country,name);
			if(skuList!=null&&skuList.size()>0){
				map.put("skuList",skuList);
			}else{
				skuList=Lists.newArrayList();
				skuList.add(name+"_"+country);
				map.put("skuList",skuList);
			}
			
		}
		map.put("fbaStock", fbaStock);
		map.put("safeStock",safeStock);
		map.put("daySales", daySales);
		map.put("oversea", oversea);
		Integer pack=0;
		if(name.contains("Inateck DB1001")){
			if("com,uk,jp,ca,mx,".contains(country)){
				pack=60;
			}else{
				pack=44;
			}
		}else if(name.contains("Inateck DB2001")){
			if("com,jp,ca,mx,".contains(country)){
				pack=32;
			}else{
				pack=24;
			}
		}else{
			Map<String,Integer> packQuantityMap=psiProductService.getPackQuantity();
			pack=packQuantityMap.get(name);
		}
		map.put("pack", pack);
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value = {"checkOrderQuantity"})
	public String checkOrderQuantity(Integer id){
		StringBuilder returnStr=new StringBuilder();
		Date date=psiTransportForecastOrderService.get(id).getCreateDate();
		Map<String,Map<String,Integer>> checkMap=psiTransportForecastOrderService.findCheckQuantity(id);
		
		for (Map.Entry<String,Map<String,Integer>> entry: checkMap.entrySet()) {
			String tranType = entry.getKey();
			Map<String,Integer> temp=checkMap.get(tranType);
			if("0".equals(tranType)){
				Map<String,Integer> cnMap=psiTransportForecastOrderService.findCnStockByName(temp.keySet(),date);//21
				for (Map.Entry<String,Integer> entry1: temp.entrySet()) {
					String name =entry1.getKey();
					if(cnMap!=null&&entry1.getValue()!=null&&entry1.getValue()>entry1.getValue()){
						returnStr.append(name).append(",春雨库存：").append(entry1.getValue()).append(",审核数量：").append(entry1.getValue()).append("<br/>");
					}
				}
			}else{
				Map<String,Integer> cnMap2=psiTransportForecastOrderService.findCnStockByName2(temp.keySet(),date);
				for (Map.Entry<String,Integer> entry1: temp.entrySet()) {
					String name =entry1.getKey();
					if(cnMap2!=null&&cnMap2.get(name)!=null&&entry1.getValue()>cnMap2.get(name)){
						returnStr.append(name).append(",理诚库存：").append(cnMap2.get(name)).append(",审核数量：").append(entry1.getValue()).append("<br/>");
					}
				}
			}
			
		}
		
		if(StringUtils.isNotBlank(returnStr)){
			return returnStr.toString();
		}else{
			return "0";
		}
	}
	
	@RequestMapping(value = {"generateTransportOrder"})
	public String generateTransportOrder(Integer id){
		try {
			psiTransportForecastOrderService.addSaveData(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportOrder/?repage";
	}
	
	
	public void countGap(Map<String,Map<String,Map<String,Integer>>> cnStock1,String name,String country,String tempSku,PsiTransportForecastOrderItem childItem,
			Map<String,Map<String,Map<String,Integer>>> poStock,Map<String,Map<String,Map<String,Integer>>> transStock1,Map<String,Map<String,Map<String,Integer>>> cnMap){
		if(cnStock1.get(name)!=null&&cnStock1.get(name).get(country)!=null&&cnStock1.get(name).get(country).get(tempSku)!=null){
            childItem.setTotalStock(cnStock1.get(name).get(country).get(tempSku));
		}else{
			childItem.setTotalStock(0);
		}
		if(poStock!=null&&poStock.get(name)!=null&&poStock.get(name).get(country)!=null&&poStock.get(name).get(country).get(tempSku)!=null){
			childItem.setPoStock(poStock.get(name).get(country).get(tempSku));
		}else{
			childItem.setPoStock(0);
		}
		
		if(cnMap!=null&&cnMap.get(name)!=null&&cnMap.get(name).get(country)!=null&&cnMap.get(name).get(country).get(tempSku)!=null){
			childItem.setReviewRemark(cnMap.get(name).get(country).get(tempSku)+"");
		}else{
			childItem.setReviewRemark("0");
		}
		
		if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("total")!=null&&transStock1.get(name+"_"+country).get("total").get(tempSku)!=null){
			childItem.setTransStock(transStock1.get(name+"_"+country).get("total").get(tempSku));
		}else{
			childItem.setTransStock(0);
		}
		if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("0")!=null&&transStock1.get(name+"_"+country).get("0").get("total")!=null){//0 2
			childItem.setTotalAir(transStock1.get(name+"_"+country).get("0").get("total"));
		}else{
			childItem.setTotalAir(0);
		}
        if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("2")!=null&&transStock1.get(name+"_"+country).get("2").get("total")!=null){//0 2
        	childItem.setTotalExp(transStock1.get(name+"_"+country).get("2").get("total"));
		}else{
			childItem.setTotalExp(0);
		}
        
        if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("0")!=null&&transStock1.get(name+"_"+country).get("0").get(tempSku)!=null){//0 2
			childItem.setAirQuantity(transStock1.get(name+"_"+country).get("0").get(tempSku));
		}else{
			childItem.setAirQuantity(0);
		}
        if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("2")!=null&&transStock1.get(name+"_"+country).get("2").get(tempSku)!=null){//0 2
        	childItem.setExpQuantity(transStock1.get(name+"_"+country).get("2").get(tempSku));
		}else{
			childItem.setExpQuantity(0);
		}
	}
	
	public Integer findPackNum(String name,String country){
		Integer pack=0;
		if(name.contains("Inateck DB1001")){
			if("com,uk,jp,ca,mx,".contains(country)){
				pack=60;
			}else{
				pack=44;
			}
		}else if(name.contains("Inateck DB2001")){
			if("com,jp,ca,mx,".contains(country)){
				pack=32;
			}else{
				pack=24;
			}
		}
		return pack;
	}
	
	//@ResponseBody
	@RequestMapping(value = {"generateOrder"})
	public String generateOrder(){
		new Thread(){
			public void run(){  
				SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
				Map<String,Map<String,Map<String,Integer>>> cnStock=psiTransportForecastOrderService.getCnInventoryProduct();//name-country-sku-quantiy
				Map<String,Map<String,Map<String,Integer>>> cnStock1=psiTransportForecastOrderService.getCnInventoryProduct1();//name-country-sku-quantiy
				Map<String,Map<String,Map<String,Integer>>> cnStock2=psiTransportForecastOrderService.getCnInventoryProduct2();//name-country-sku-quantiy
				
				Map<String,Map<String,Map<String,Integer>>> cnMap=psiTransportForecastOrderService.getCnInventory();//name-country-sku-quantiy
				Map<String,Map<String,Map<String,Integer>>> lcCnMap=psiTransportForecastOrderService.getLCCnInventory();//name-country-sku-quantiy
				
				Map<String,Map<String,Map<String,Integer>>> poStock=Maps.newHashMap();
				psiTransportForecastOrderService.getPOInventoryProduct(poStock);
				
				Map<String,Map<String,Map<String,Integer>>> poStock2=Maps.newHashMap();
				psiTransportForecastOrderService.getPOInventoryProduct2(poStock2);
				
				Map<String,Map<String,Map<String,Integer>>> transStock=psiTransportForecastOrderService.getNewProduct();
				
				Map<String,Map<String,Map<String,Integer>>> transStock1=psiTransportForecastOrderService.getNewProduct1();
				Map<String,Map<String,Map<String,Integer>>> transStock2=psiTransportForecastOrderService.getNewProduct2();

				Map<String,String> allSkuMap=psiProductService.getSkuByProduct();
			
				Map<String,Map<Date,Map<String,Integer>>> allTranMap=psiTransportForecastOrderService.getAllTransportQuantity();
				Map<String,Map<String,Integer>>  sale30Map = psiInventoryFbaService.get31SalesMap(null);//country_name_quantity
				Map<String,PsiInventoryFba> amazonStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
				
				Map<String,Integer> packQuantityMap=psiProductService.getPackQuantity();
				List<String> countryList=Lists.newArrayList("de","fr","it","es","uk","ca","jp","mx","com");//ca默认全部空运
				List<String> euCountryList=Lists.newArrayList("de","fr","uk","it","es");
				List<String> fourCountryList=Lists.newArrayList("de","fr","it","es");
				List<PsiTransportForecastOrderItem> items = Lists.newArrayList();
				Map<String,Map<String,PsiProductInStock>> stockMap=psiProductInStockService.getHistoryInventory();
				PsiTransportForecastOrder order=new PsiTransportForecastOrder();
				//带电源+KeyBoard产品
				//List<String> keyBoardAndHasPowerList=saleReportService.findKeyBoardAndHasPowerAllProduct();
				Map<String,Set<String>> typeMap=saleReportService.findProductByType();////0:keyboard 1:四国泛欧
				Set<String> keyBoardSet=typeMap.get("0");
				Set<String> fourCtySet=typeMap.get("1");
				
				List<String> componentsList=psiProductService.findComponents();
				Map<String, String> newMap=psiProductEliminateService.findIsNewMap();//产品名_颜色_国   1新品
				Map<String, String> productPositionMap = psiProductEliminateService.findAllProductPosition(); //产品定位-产品名_颜色_国 
				
				Map<String,Map<String,PsiTransportForecastOrderItem>> countryGap=Maps.newHashMap();
				Map<String,Integer> gapMap=Maps.newHashMap();
				
				Date today=new Date();
				Map<String,String> tranMap=Maps.newHashMap();
				Map<String,Integer> salesDayMap=Maps.newHashMap();
				Map<String,Integer> overseaMap=Maps.newHashMap();
				//[name_country [data]]
				Map<String, Map<String, Float>>  forecastDatas = psiInventoryService.getForecastByMonthSalesData();
				Map<String, Integer> daySalesMap=Maps.newHashMap();
				
				String month=new SimpleDateFormat("MM").format(new Date());
				String index="1";
				if(month=="01"||month=="02"||month=="03"){
					index="1";
				}else if(month=="04"||month=="05"||month=="06"){
					index="2";
				}else if(month=="07"||month=="08"||month=="09"){
					index="3";
				}else{
					index="4";
				}
				Map<String,Integer> seaDayMap=lcPsiTransportOrderService.findTranDays(index);
				
				 
				if(cnStock!=null&&cnStock.size()>0){
					//算产品各个国家空运数量
					for (String  name: cnStock.keySet()) {
						if(componentsList.contains(name)){
							continue;
						}
						for(String country:countryList){
							Integer seaDays=PsiConfig.get(country).getFbaBySea();
							if("de,jp,com".contains(country)&&seaDayMap!=null&&seaDayMap.get(country)!=null){
								seaDays=seaDayMap.get(country)+2;
							}
							if("ca".equals(country)){
								seaDays=PsiConfig.get(country).getFbaBySky();
							}
							
							Integer pack=packQuantityMap.get(name);
							if(name.contains("Inateck DB1001")){
								if("com,uk,jp,ca,mx,".contains(country)){
									pack=60;
								}else{
									pack=44;
								}
							}else if(name.contains("Inateck DB2001")){
								if("com,jp,ca,mx,".contains(country)){
									pack=32;
								}else{
									pack=24;
								}
							}
							PsiInventoryTotalDtoByInStock inventorys = psiInventoryService.getInventoryQuantity(name,null);
							if(!keyBoardSet.contains(name)&&!fourCtySet.contains(name)&&"de".equals(country)){//不带电源
								Integer oversea=0;
								if(inventorys!=null&&inventorys.getQuantityEuro2()!=null&&inventorys.getQuantityEuro2().get("DE")!=null&&inventorys.getQuantityEuro2().get("DE").getNewQuantity()!=null){
									oversea=inventorys.getQuantityEuro2().get("DE").getNewQuantity();
								}
								overseaMap.put(name+"_"+country,oversea);
							
								PsiTransportForecastOrderItem item=new PsiTransportForecastOrderItem();
								if(name.contains("_")){
									String[] arr=name.split("_");
									item.setProductName(arr[0]);
									item.setColorCode(arr[1]);
								}else{
									item.setProductName(name);
									item.setColorCode("");
								}
								item.setCountryCode(country);
								//Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory("eu");
								Integer safeInventory=0;
								if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get("eu")!=null){
									safeInventory=MathUtils.roundUp(stockMap.get(name).get("eu").getSafeInventory());
								}
								item.setSafeStock(safeInventory);
								
								Integer fbaStock=0;
								PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_eu");
								if(psiInventoryFba!=null){
									fbaStock=psiInventoryFba.getRealTotal();
									Integer receiveNum=0;
									Map<String,Integer> receiveFbaTran = psiInventoryService.getFbaTransporttingReceiveByName(name);
									if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
										for(String euCountry:euCountryList){
											receiveNum+=(receiveFbaTran.get(name+"_"+euCountry)==null?0:receiveFbaTran.get(name+"_"+euCountry));
										}
									}
									if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
										fbaStock=fbaStock-receiveNum;
									}
								}
								item.setAmazonStock(fbaStock);
								
								Integer daySales=0;
								Map<String, Double> forecastAvgMap = forecastOrderService.getAvg(DateUtils.addDays(today,seaDays+7), forecastDatas);
								if(forecastAvgMap!=null&&forecastAvgMap.get(name+"_eu")!=null&&forecastAvgMap.get(name+"_eu")>0){//name_country _eunouk  _eu
									daySales=MathUtils.roundUp(forecastAvgMap.get(name+"_eu"));
								}else{
									Integer totalSal=0;
									for(String euCountry:euCountryList){
										if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
											totalSal+=sale30Map.get(euCountry).get(name);
										}
									}
									daySales=MathUtils.roundUp(totalSal/31d);
								}
								item.setDay31sales(daySales);
								daySalesMap.put(name+"_eu",daySales);
								if(daySales==0){
									continue;
								}
								
								Integer hotSales=0;
								if("1".equals(productPositionMap.get(name+"_"+country))){
									hotSales=15*daySales;
							    }
								  
								Integer fbaSalesDay=(fbaStock+oversea-safeInventory-hotSales)/daySales;
							//	if(fbaSalesDay<seaDays+7){
									Date fbaDate=DateUtils.addDays(today, fbaSalesDay<0?0:fbaSalesDay);
									
									Map<Date,Map<String,Integer>> dateTranMap=allTranMap.get(name+"_eu");
									Integer gapQuantity=0;
									Integer tranSalesDay=0;
									Integer tempSalesDay=0;
									if(dateTranMap!=null&&dateTranMap.size()>0){
										    Set<Date> tempDateSet=dateTranMap.keySet();
											List<Date> dateSet=Lists.newArrayList(tempDateSet);
											Collections.sort(dateSet);
											Date beforeDate=null;
											for (Date date: dateSet) {
												Map<String, Integer> tranTypeMap=dateTranMap.get(date);
												for(Map.Entry<String,Integer> entry:tranTypeMap.entrySet()){
												    String type=("1".equals(entry.getKey())?"FBA,":"");
												    Integer quantity=entry.getValue();
												    int day=quantity/daySales;
												    if(date.before(DateUtils.addDays(today, seaDays+7))){//判断到达时间在海运前
														if(beforeDate==null){
															beforeDate=fbaDate;
														}
														if(beforeDate.after(DateUtils.addDays(today, seaDays+7))){
															tempSalesDay+=quantity/daySales;
															item.setDetail((StringUtils.isNotBlank(item.getDetail())?item.getDetail():"")+type+dateFormat.format(date)+"::"+quantity+";");
														    continue;	
														}	
														if(date.before(fbaDate)||date.equals(fbaDate)||date.before(beforeDate)||date.equals(beforeDate)){
															tranSalesDay+=day;
															item.setDetail((StringUtils.isNotBlank(item.getDetail())?item.getDetail():"")+type+dateFormat.format(date)+":"+quantity+";");
														}else if(date.after(beforeDate)){
															Date dateSale=DateUtils.addDays(date,day);
															if(dateSale.after(DateUtils.addDays(today, seaDays+7))){
																tranSalesDay+=(int) DateUtils.spaceDays(date,DateUtils.addDays(today, seaDays+7));
															}else{
																tranSalesDay+=quantity/daySales;
															}
															item.setDetail((StringUtils.isNotBlank(item.getDetail())?item.getDetail():"")+type+dateFormat.format(date)+":"+quantity+";");
														}
														beforeDate=DateUtils.addDays(date,day);
													}else{
														tempSalesDay+=quantity/daySales;
														item.setDetail((StringUtils.isNotBlank(item.getDetail())?item.getDetail():"")+type+dateFormat.format(date)+"::"+quantity+";");
													}
												}    
											}
									}		
									
									gapQuantity=(seaDays+7-(fbaSalesDay+tranSalesDay))*daySales;
									//transStock
									Integer airAndExp=0;
									if(transStock!=null&&transStock.get(name+"_"+country)!=null){//0 AE 2EX
										if(transStock.get(name+"_"+country).get("0")!=null&&transStock.get(name+"_"+country).get("0").get("total")!=null){//0 2
											airAndExp+=transStock.get(name+"_"+country).get("0").get("total");
										}
		                                if(transStock.get(name+"_"+country).get("2")!=null&&transStock.get(name+"_"+country).get("2").get("total")!=null){//0 2
		                                	airAndExp+=transStock.get(name+"_"+country).get("2").get("total");
										}
		                               // gapQuantity=gapQuantity-airAndExp;
									}
									
									if(StringUtils.isNotBlank(item.getDetail())){
										tranMap.put(name+"_"+country, item.getDetail());
									}
									
									if(gapQuantity>0){
										int num=MathUtils.roundUp(gapQuantity*1.0d/pack);
										if(num<=0){
											continue;
										}
										item.setGap(gapQuantity);
										item.setBoxNum(pack);
										item.setQuantity(num*pack);
										item.setTransportType("0");//o本地运输 1FBA
										item.setModel("0");//0空运
									//	items.add(item);
										Map<String,PsiTransportForecastOrderItem> temp=countryGap.get(name);
										if(temp==null){
											temp=Maps.newHashMap();
											countryGap.put(name, temp);
										}
										temp.put(country, item);
										gapMap.put(name+"_"+country, item.getQuantity());
										//countrySalesDay.put(name+"_"+country,tranSalesDay+fbaSalesDay);
										salesDayMap.put(name+"_"+country,fbaSalesDay+tranSalesDay);
									}else{
										salesDayMap.put(name+"_"+country,fbaSalesDay+tranSalesDay+tempSalesDay);
									}
									
								
							}else if(fourCtySet.contains(name)&&"de".contains(country)){
								Integer oversea=0;
								for(String cty:fourCountryList){
									if(inventorys!=null&&inventorys.getInventorys()!=null&&inventorys.getInventorys().get(cty)!=null
											&&inventorys.getInventorys().get(cty).getQuantityInventory()!=null&&inventorys.getInventorys().get(cty).getQuantityInventory().get("DE")!=null
											&&inventorys.getInventorys().get(cty).getQuantityInventory().get("DE").getNewQuantity()!=null){
												oversea+=inventorys.getInventorys().get(cty).getQuantityInventory().get("DE").getNewQuantity();
											}
								}
								overseaMap.put(name+"_"+country,oversea);
							
								PsiTransportForecastOrderItem item=new PsiTransportForecastOrderItem();
								if(name.contains("_")){
									String[] arr=name.split("_");
									item.setProductName(arr[0]);
									item.setColorCode(arr[1]);
								}else{
									item.setProductName(name);
									item.setColorCode("");
								}
								item.setCountryCode(country);
								//Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory("eu");
								Integer safeInventory=0;
								if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get("eu")!=null){
									safeInventory=MathUtils.roundUp(stockMap.get(name).get("eu").getSafeInventory());
									if(stockMap.get(name).get("uk")!=null){
										safeInventory=safeInventory-MathUtils.roundUp(stockMap.get(name).get("uk").getSafeInventory());
									}
								}
								item.setSafeStock(safeInventory);
								
								Integer fbaStock=0;
								PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_four");
								if(psiInventoryFba!=null){
									fbaStock=psiInventoryFba.getRealTotal();
									Integer receiveNum=0;
									Map<String,Integer> receiveFbaTran = psiInventoryService.getFbaTransporttingReceiveByName(name);
									if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
										for(String euCountry:fourCountryList){
											receiveNum+=(receiveFbaTran.get(name+"_"+euCountry)==null?0:receiveFbaTran.get(name+"_"+euCountry));
										}
									}
									if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
										fbaStock=fbaStock-receiveNum;
									}
								}
								item.setAmazonStock(fbaStock);
								
								
								Integer daySales=0;
								Map<String, Double> forecastAvgMap = forecastOrderService.getAvg(DateUtils.addDays(today,seaDays+7), forecastDatas);
								if(forecastAvgMap!=null&&forecastAvgMap.get(name+"_eunouk")!=null&&forecastAvgMap.get(name+"_eunouk")>0){//name_country _eunouk  _eu
									daySales=MathUtils.roundUp(forecastAvgMap.get(name+"_eunouk"));
								}else{
									Integer totalSal=0;
									for(String euCountry:fourCountryList){
										if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
											totalSal+=sale30Map.get(euCountry).get(name);
										}
									}
									daySales=MathUtils.roundUp(totalSal/31d);
								}
								item.setDay31sales(daySales);
								daySalesMap.put(name+"_euNoUK",daySales);
								if(daySales==0){
									continue;
								}
								Integer hotSales=0;
								if("1".equals(productPositionMap.get(name+"_"+country))){
									hotSales=15*daySales;
							    }
								
								Integer fbaSalesDay=(fbaStock+oversea-safeInventory-hotSales)/daySales;
								Map<Date,Map<String,Integer>> dateTranMap=allTranMap.get(name+"_euNoUk");
								
									Date fbaDate=DateUtils.addDays(new Date(), fbaSalesDay<0?0:fbaSalesDay);
									Integer gapQuantity=0;
									Integer tranSalesDay=0;
									Integer tempSalesDay=0;
									if(dateTranMap!=null&&dateTranMap.size()>0){
										    Set<Date> tempDateSet=dateTranMap.keySet();
											List<Date> dateSet=Lists.newArrayList(tempDateSet);
											Collections.sort(dateSet);
											Date beforeDate=null;
											for (Date date: dateSet) {
												Map<String, Integer> tranTypeMap=dateTranMap.get(date);
												for(Map.Entry<String,Integer> entry:tranTypeMap.entrySet()){
												    String type=("1".equals(entry.getKey())?"FBA,":"");
												    Integer quantity=entry.getValue();
												    int day=quantity/daySales;
												    if(date.before(DateUtils.addDays(today, seaDays+7))){//判断到达时间在海运前
														
														if(beforeDate==null){
															beforeDate=fbaDate;
														}
														if(beforeDate.after(DateUtils.addDays(today, seaDays+7))){
															tempSalesDay+=quantity/daySales;
															item.setDetail((StringUtils.isNotBlank(item.getDetail())?item.getDetail():"")+type+dateFormat.format(date)+"::"+quantity+";");
														    continue;	
														}	
														if(date.before(fbaDate)||date.equals(fbaDate)||date.before(beforeDate)||date.equals(beforeDate)){
															tranSalesDay+=day;
															item.setDetail((StringUtils.isNotBlank(item.getDetail())?item.getDetail():"")+type+dateFormat.format(date)+":"+quantity+";");
														}else if(date.after(beforeDate)){
															Date dateSale=DateUtils.addDays(date,day);
															if(dateSale.after(DateUtils.addDays(today, seaDays+7))){
																tranSalesDay+=(int) DateUtils.spaceDays(date,DateUtils.addDays(today, seaDays+7));
															}else{
																tranSalesDay+=quantity/daySales;
															}
															item.setDetail((StringUtils.isNotBlank(item.getDetail())?item.getDetail():"")+type+dateFormat.format(date)+":"+quantity+";");
														}
														beforeDate=DateUtils.addDays(date,day);
													}else{
														tempSalesDay+=quantity/daySales;
														item.setDetail((StringUtils.isNotBlank(item.getDetail())?item.getDetail():"")+type+dateFormat.format(date)+"::"+quantity+";");
													}
												}    
											}
									}		
									
									
									gapQuantity=(seaDays+7-(fbaSalesDay+tranSalesDay))*daySales;
									//transStock
									Integer airAndExp=0;
									if(transStock!=null&&transStock.get(name+"_"+country)!=null){//0 AE 2EX
										if(transStock.get(name+"_"+country).get("0")!=null&&transStock.get(name+"_"+country).get("0").get("total")!=null){//0 2
											airAndExp+=transStock.get(name+"_"+country).get("0").get("total");
										}
		                                if(transStock.get(name+"_"+country).get("2")!=null&&transStock.get(name+"_"+country).get("2").get("total")!=null){//0 2
		                                	airAndExp+=transStock.get(name+"_"+country).get("2").get("total");
										}
		                               // gapQuantity=gapQuantity-airAndExp;
									}
									
									if(StringUtils.isNotBlank(item.getDetail())){
										tranMap.put(name+"_"+country, item.getDetail());
									}
									
									if(gapQuantity>0){
										int num=MathUtils.roundUp(gapQuantity*1.0d/pack);
										if(num<=0){
											continue;
										}
										item.setGap(gapQuantity);
										item.setBoxNum(pack);
										item.setQuantity(num*pack);
										item.setTransportType("0");//o本地运输 1FBA
										item.setModel("0");//0空运
									//	items.add(item);
										Map<String,PsiTransportForecastOrderItem> temp=countryGap.get(name);
										if(temp==null){
											temp=Maps.newHashMap();
											countryGap.put(name, temp);
										}
										temp.put(country, item);
										gapMap.put(name+"_"+country, item.getQuantity());
										//countrySalesDay.put(name+"_"+country,tranSalesDay+fbaSalesDay);
										salesDayMap.put(name+"_"+country,fbaSalesDay+tranSalesDay);
									}else{
										salesDayMap.put(name+"_"+country,fbaSalesDay+tranSalesDay+tempSalesDay);
									}
							}else if(("ca,com,jp,mx".contains(country))||country.startsWith("com")||(keyBoardSet.contains(name)&&"de,fr,it,es,uk".contains(country))||(fourCtySet.contains(name)&&"uk".contains(country))){
								Integer oversea=0;//inventorys.inventorys['es'].quantityInventory['DE'].newQuantity
								if("jp".equals(country)){
									if(inventorys!=null&&inventorys.getInventorys()!=null&&inventorys.getInventorys().get(country)!=null
											&&inventorys.getInventorys().get(country).getQuantityInventory()!=null&&inventorys.getInventorys().get(country).getQuantityInventory().get("JP")!=null
											&&inventorys.getInventorys().get(country).getQuantityInventory().get("JP").getNewQuantity()!=null){
												oversea=inventorys.getInventorys().get(country).getQuantityInventory().get("JP").getNewQuantity();
											}
								}else if(country.startsWith("com")){
									if(inventorys!=null&&inventorys.getInventorys()!=null&&inventorys.getInventorys().get(country)!=null
											&&inventorys.getInventorys().get(country).getQuantityInventory()!=null&&inventorys.getInventorys().get(country).getQuantityInventory().get("US")!=null
											&&inventorys.getInventorys().get(country).getQuantityInventory().get("US").getNewQuantity()!=null){
												oversea=inventorys.getInventorys().get(country).getQuantityInventory().get("US").getNewQuantity();
											}
								}else if("de,fr,it,es,uk".contains(country)){
									if(inventorys!=null&&inventorys.getInventorys()!=null&&inventorys.getInventorys().get(country)!=null
											&&inventorys.getInventorys().get(country).getQuantityInventory()!=null&&inventorys.getInventorys().get(country).getQuantityInventory().get("DE")!=null
											&&inventorys.getInventorys().get(country).getQuantityInventory().get("DE").getNewQuantity()!=null){
												oversea=inventorys.getInventorys().get(country).getQuantityInventory().get("DE").getNewQuantity();
											}
								}
								
								overseaMap.put(name+"_"+country,oversea);
								
								PsiTransportForecastOrderItem item=new PsiTransportForecastOrderItem();
								if(name.contains("_")){
									String[] arr=name.split("_");
									item.setProductName(arr[0]);
									item.setColorCode(arr[1]);
								}else{
									item.setProductName(name);
									item.setColorCode("");
								}
								item.setCountryCode(country);
								Integer safeInventory=0;
								if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get(country)!=null){
									safeInventory=MathUtils.roundUp(stockMap.get(name).get(country).getSafeInventory());
								}
								item.setSafeStock(safeInventory);
								
								Integer fbaStock=0;
								PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_"+country);
								if(psiInventoryFba!=null){
									fbaStock=psiInventoryFba.getRealTotal();
									Integer receiveNum=0;
									Map<String,Integer> receiveFbaTran = psiInventoryService.getFbaTransporttingReceiveByName(name);
									if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
										receiveNum+=(receiveFbaTran.get(name+"_"+country)==null?0:receiveFbaTran.get(name+"_"+country));
									}
									if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
										fbaStock=fbaStock-receiveNum;
									}
								}
								item.setAmazonStock(fbaStock);
								
								Integer daySales=0;
								Map<String, Double> forecastAvgMap = forecastOrderService.getAvg(DateUtils.addDays(today,seaDays+7), forecastDatas);
								if(forecastAvgMap!=null&&forecastAvgMap.get(name+"_"+country)!=null&&forecastAvgMap.get(name+"_"+country)>0){//name_country _eunouk  _eu
									daySales=MathUtils.roundUp(forecastAvgMap.get(name+"_"+country));
								}else{
									if(sale30Map!=null&&sale30Map.get(country)!=null&&sale30Map.get(country).get(name)!=null){
										daySales=MathUtils.roundUp(sale30Map.get(country).get(name)/31d);
									}
								}
								
								item.setDay31sales(daySales);
								daySalesMap.put(name+"_"+country,daySales);
								if(daySales==0){
									continue;
								}
								Integer hotSales=0;
								if("1".equals(productPositionMap.get(name+"_"+country))){
									hotSales=15*daySales;
							    }
								Integer fbaSalesDay=(fbaStock+oversea-safeInventory-hotSales)/daySales;
								Map<Date,Map<String,Integer>> dateTranMap=allTranMap.get(name+"_"+country);
								
							//	if(fbaSalesDay<seaDays+7){
									Date fbaDate=DateUtils.addDays(new Date(), fbaSalesDay<0?0:fbaSalesDay);
									Integer gapQuantity=0;
									Integer tranSalesDay=0;
									Integer tempSalesDay=0;
									if(dateTranMap!=null&&dateTranMap.size()>0){
										    Set<Date> tempDateSet=dateTranMap.keySet();
											List<Date> dateSet=Lists.newArrayList(tempDateSet);
											Collections.sort(dateSet);
											Date beforeDate=null;
											for (Date date: dateSet) {
												Map<String, Integer> tranTypeMap=dateTranMap.get(date);
												for(Map.Entry<String,Integer> entry:tranTypeMap.entrySet()){
												    String type=("1".equals(entry.getKey())?"FBA,":"");
												    Integer quantity=entry.getValue();
												    int day=quantity/daySales;
												    if(date.before(DateUtils.addDays(today, seaDays+7))){//判断到达时间在海运前
														
														if(beforeDate==null){
															beforeDate=fbaDate;
														}
														if(beforeDate.after(DateUtils.addDays(today, seaDays+7))){
															tempSalesDay+=quantity/daySales;
															item.setDetail((StringUtils.isNotBlank(item.getDetail())?item.getDetail():"")+type+dateFormat.format(date)+"::"+quantity+";");
														    continue;	
														}	
														if(date.before(fbaDate)||date.equals(fbaDate)||date.before(beforeDate)||date.equals(beforeDate)){
															tranSalesDay+=day;
															item.setDetail((StringUtils.isNotBlank(item.getDetail())?item.getDetail():"")+type+dateFormat.format(date)+":"+quantity+";");
														}else if(date.after(beforeDate)){
															Date dateSale=DateUtils.addDays(date,day);
															if(dateSale.after(DateUtils.addDays(today, seaDays+7))){
																tranSalesDay+=(int) DateUtils.spaceDays(date,DateUtils.addDays(today, seaDays+7));
															}else{
																tranSalesDay+=quantity/daySales;
															}
															item.setDetail((StringUtils.isNotBlank(item.getDetail())?item.getDetail():"")+type+dateFormat.format(date)+":"+quantity+";");
														}
														beforeDate=DateUtils.addDays(date,day);
													}else{
														tempSalesDay+=quantity/daySales;
														item.setDetail((StringUtils.isNotBlank(item.getDetail())?item.getDetail():"")+type+dateFormat.format(date)+"::"+quantity+";");
													}
												}    
											}
									}		
									
									
									if(StringUtils.isNotBlank(item.getDetail())){
										tranMap.put(name+"_"+country, item.getDetail());
									}
									
									gapQuantity=(seaDays+7-(fbaSalesDay+tranSalesDay))*daySales;
									Integer airAndExp=0;
									if(transStock!=null&&transStock.get(name+"_"+country)!=null){//0 AE 2EX
										if(transStock.get(name+"_"+country).get("0")!=null&&transStock.get(name+"_"+country).get("0").get("total")!=null){//0 2
											airAndExp+=transStock.get(name+"_"+country).get("0").get("total");
										}
		                                if(transStock.get(name+"_"+country).get("2")!=null&&transStock.get(name+"_"+country).get("2").get("total")!=null){//0 2
		                                	airAndExp+=transStock.get(name+"_"+country).get("2").get("total");
										}
		                                //gapQuantity=gapQuantity-airAndExp;
									}
									if(gapQuantity>0){
										int num=MathUtils.roundUp(gapQuantity*1.0d/pack);
										if(num<=0){
											continue;
										}
										item.setGap(gapQuantity);
										item.setBoxNum(pack);
										item.setQuantity(num*pack);
										if("ca,com,jp,mx".contains(country)){
											item.setTransportType("1");//o本地运输 1FBA
										}else{
											item.setTransportType("0");//o本地运输 1FBA
										}
										
										item.setModel("0");//0空运
									//	items.add(item);
										Map<String,PsiTransportForecastOrderItem> temp=countryGap.get(name);
										if(temp==null){
											temp=Maps.newHashMap();
											countryGap.put(name, temp);
										}
										temp.put(country, item);
										gapMap.put(name+"_"+country, item.getQuantity());
										//countrySalesDay.put(name+"_"+country,tranSalesDay+fbaSalesDay);
										salesDayMap.put(name+"_"+country,fbaSalesDay+tranSalesDay);
									}else{
										salesDayMap.put(name+"_"+country,fbaSalesDay+tranSalesDay+tempSalesDay);
									}
							}
						}
					}
					
					
					Map<String,Map<String,Map<String,Integer>>> residueStock=Maps.newLinkedHashMap();
					Map<String,Map<String,Map<String,Integer>>> residueStock2=Maps.newLinkedHashMap();
					
					//先分配库存国家SR
					for (String  name: cnStock1.keySet()) {
						//air
						if(componentsList.contains(name)){
							continue;
						}
						Map<String,Map<String,Integer>> countryMap=cnStock1.get(name);
						for (String country: countryMap.keySet()) {
							Map<String,Integer> skuMap=countryMap.get(country);
							Integer totalQuantity=skuMap.get("total");
							if(countryGap!=null&&countryGap.get(name)!=null&&countryGap.get(name).get(country)!=null){//有空运数量
								PsiTransportForecastOrderItem item=countryGap.get(name).get(country);
								Integer quantity=item.getQuantity();
								if(totalQuantity<quantity){
									Integer jyQuantity=0;
									int num=item.getBoxNum();//多少一箱
									for (String sku: skuMap.keySet()) {
										if("total".equals(sku)){
											continue;
										}
										Integer skuQuantity=skuMap.get(sku);
										Integer boxNum=skuQuantity/num;
										Integer suitQuantity=0;
										if(boxNum>0){
											suitQuantity=boxNum*num;
											PsiTransportForecastOrderItem childItem=new PsiTransportForecastOrderItem();
											childItem.setQuantity(suitQuantity);
											jyQuantity+=suitQuantity;
											childItem.setCheckQuantity(suitQuantity);
											childItem.setBoxNum(item.getBoxNum());
											childItem.setProductName(item.getProductName());
											childItem.setColorCode(item.getColorCode());
											childItem.setCountryCode(item.getCountryCode());
											childItem.setSafeStock(item.getSafeStock());
											childItem.setAmazonStock(item.getAmazonStock());
											childItem.setDay31sales(item.getDay31sales());
											childItem.setModel(item.getModel());
											childItem.setTransportType(item.getTransportType());
											childItem.setDisplaySta("0");
											childItem.setSku(sku);
											childItem.setDetail(item.getDetail());
											childItem.setGap(item.getGap());
											childItem.setPsiTransportForecastOrder(order);
											childItem.setTotalStock(skuQuantity);
											if(poStock!=null&&poStock.get(name)!=null&&poStock.get(name).get(country)!=null&&poStock.get(name).get(country).get(sku)!=null){
												childItem.setPoStock(poStock.get(name).get(country).get(sku));
											}else{
												childItem.setPoStock(0);
											}
											//name-country-sku-quantiy
											if(cnMap!=null&&cnMap.get(name)!=null&&cnMap.get(name).get(country)!=null&&cnMap.get(name).get(country).get(sku)!=null){
												childItem.setReviewRemark(cnMap.get(name).get(country).get(sku)+"");
											}else{
												childItem.setReviewRemark("0");
											}
											
											if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("total")!=null&&transStock1.get(name+"_"+country).get("total").get(sku)!=null){
												childItem.setTransStock(transStock1.get(name+"_"+country).get("total").get(sku));
											}else{
												childItem.setTransStock(0);
											}
											if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("0")!=null&&transStock1.get(name+"_"+country).get("0").get("total")!=null){//0 2
												childItem.setTotalAir(transStock1.get(name+"_"+country).get("0").get("total"));
											}else{
												childItem.setTotalAir(0);
											}
			                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("2")!=null&&transStock1.get(name+"_"+country).get("2").get("total")!=null){//0 2
			                                	childItem.setTotalExp(transStock1.get(name+"_"+country).get("2").get("total"));
											}else{
												childItem.setTotalExp(0);
											}
			                                
			                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("0")!=null&&transStock1.get(name+"_"+country).get("0").get(sku)!=null){//0 2
												childItem.setAirQuantity(transStock1.get(name+"_"+country).get("0").get(sku));
											}else{
												childItem.setAirQuantity(0);
											}
			                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("2")!=null&&transStock1.get(name+"_"+country).get("2").get(sku)!=null){//0 2
			                                	childItem.setExpQuantity(transStock1.get(name+"_"+country).get("2").get(sku));
											}else{
												childItem.setExpQuantity(0);
											}
			                                
			                                
			                                
											Integer oversea=0;
											if(overseaMap!=null&&overseaMap.get(name+"_"+country)!=null){
												oversea=overseaMap.get(name+"_"+country);
											}
											childItem.setOverseaStock(oversea);
											childItem.setSalesDay(salesDayMap.get(name+"_"+country));
											childItem.setTransSta("0");
											if(StringUtils.isNotBlank(childItem.getSku())&&childItem.getSku().contains(childItem.getProductNameColor())){
												childItem.setRemark("新品");
											}
											items.add(childItem);
										}
										
										if(skuQuantity-suitQuantity>0){//有剩余
											Map<String,Map<String,Integer>> tempCountryMap=residueStock.get(name);
											if(tempCountryMap==null){
												tempCountryMap=Maps.newLinkedHashMap();
												residueStock.put(name, tempCountryMap);
											}
											Map<String,Integer> tempSkuMap=tempCountryMap.get(country);
											if(tempSkuMap==null){
												tempSkuMap=Maps.newLinkedHashMap();
												tempCountryMap.put(country, tempSkuMap);
											}
											tempSkuMap.put(sku,skuQuantity-suitQuantity);
											tempSkuMap.put("total",tempSkuMap.get("total")==null?skuQuantity-suitQuantity:tempSkuMap.get("total")+skuQuantity-suitQuantity );
										}
									}
									//if(quantity-jyQuantity>0){
										gapMap.put(name+"_"+country, quantity-jyQuantity);
									//}
								}else{
									int num=item.getBoxNum();//多少一箱
									int tempQuantity=0;
									Integer jyQuantity=0;
									for (String sku: skuMap.keySet()) {
										if("total".equals(sku)){
											continue;
										}
										Integer skuQuantity=skuMap.get(sku);
										Integer boxNum=0;
										Integer suitQuantity=0;
										if(tempQuantity<quantity){
											boxNum=skuQuantity/num;
											if(boxNum>0){
												suitQuantity=boxNum*num;
												
												PsiTransportForecastOrderItem childItem=new PsiTransportForecastOrderItem();
												if(tempQuantity+suitQuantity>quantity){
													suitQuantity=quantity-tempQuantity;
													boxNum=suitQuantity/num;
													childItem.setQuantity(suitQuantity);
													jyQuantity+=suitQuantity;
													childItem.setCheckQuantity(suitQuantity);
													childItem.setBoxNum(item.getBoxNum());
												}else{
													childItem.setQuantity(suitQuantity);
													childItem.setCheckQuantity(suitQuantity);
													jyQuantity+=suitQuantity;
													childItem.setBoxNum(item.getBoxNum());
												}
												childItem.setProductName(item.getProductName());
												childItem.setColorCode(item.getColorCode());
												childItem.setCountryCode(item.getCountryCode());
												childItem.setSafeStock(item.getSafeStock());
												childItem.setAmazonStock(item.getAmazonStock());
												childItem.setDay31sales(item.getDay31sales());
												childItem.setModel(item.getModel());
												childItem.setTransportType(item.getTransportType());
												childItem.setDisplaySta("0");
												childItem.setSku(sku);
												childItem.setDetail(item.getDetail());
												childItem.setSalesDay(salesDayMap.get(name+"_"+country));
												childItem.setTransSta("0");
												Integer oversea=0;
												if(overseaMap!=null&&overseaMap.get(name+"_"+country)!=null){
													oversea=overseaMap.get(name+"_"+country);
												}
												childItem.setOverseaStock(oversea);
												childItem.setPsiTransportForecastOrder(order);
												childItem.setTotalStock(skuQuantity);
												childItem.setGap(item.getGap());
												if(poStock!=null&&poStock.get(name)!=null&&poStock.get(name).get(country)!=null&&poStock.get(name).get(country).get(sku)!=null){
													childItem.setPoStock(poStock.get(name).get(country).get(sku));
												}else{
													childItem.setPoStock(0);
												}
												if(cnMap!=null&&cnMap.get(name)!=null&&cnMap.get(name).get(country)!=null&&cnMap.get(name).get(country).get(sku)!=null){
													childItem.setReviewRemark(cnMap.get(name).get(country).get(sku)+"");
												}else{
													childItem.setReviewRemark("0");
												}
												
												if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("total")!=null&&transStock1.get(name+"_"+country).get("total").get(sku)!=null){
													childItem.setTransStock(transStock1.get(name+"_"+country).get("total").get(sku));
												}else{
													childItem.setTransStock(0);
												}
												if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("0")!=null&&transStock1.get(name+"_"+country).get("0").get("total")!=null){//0 2
													childItem.setTotalAir(transStock1.get(name+"_"+country).get("0").get("total"));
												}else{
													childItem.setTotalAir(0);
												}
				                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("2")!=null&&transStock1.get(name+"_"+country).get("2").get("total")!=null){//0 2
				                                	childItem.setTotalExp(transStock1.get(name+"_"+country).get("2").get("total"));
												}else{
													childItem.setTotalExp(0);
												}
				                                
				                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("0")!=null&&transStock1.get(name+"_"+country).get("0").get(sku)!=null){//0 2
													childItem.setAirQuantity(transStock1.get(name+"_"+country).get("0").get(sku));
												}else{
													childItem.setAirQuantity(0);
												}
				                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("2")!=null&&transStock1.get(name+"_"+country).get("2").get(sku)!=null){//0 2
				                                	childItem.setExpQuantity(transStock1.get(name+"_"+country).get("2").get(sku));
												}else{
													childItem.setExpQuantity(0);
												}
				                                if(StringUtils.isNotBlank(childItem.getSku())&&childItem.getSku().contains(childItem.getProductNameColor())){
													childItem.setRemark("新品");
												}
				                                
												items.add(childItem);
											}
											
										}
										tempQuantity+=suitQuantity;
										
										if(skuQuantity-suitQuantity>0){//有剩余
											Map<String,Map<String,Integer>> tempCountryMap=residueStock.get(name);
											if(tempCountryMap==null){
												tempCountryMap=Maps.newLinkedHashMap();
												residueStock.put(name, tempCountryMap);
											}
											Map<String,Integer> tempSkuMap=tempCountryMap.get(country);
											if(tempSkuMap==null){
												tempSkuMap=Maps.newLinkedHashMap();
												tempCountryMap.put(country, tempSkuMap);
											}
											tempSkuMap.put(sku,skuQuantity-suitQuantity);
											tempSkuMap.put("total",tempSkuMap.get("total")==null?skuQuantity-suitQuantity:tempSkuMap.get("total")+skuQuantity-suitQuantity );
										}
									}
									//if(quantity-jyQuantity>0){
										gapMap.put(name+"_"+country, quantity-jyQuantity);
									//}
									
								}
							}else{
								Map<String,Map<String,Integer>> tempCountryMap=residueStock.get(name);
								if(tempCountryMap==null){
									tempCountryMap=Maps.newLinkedHashMap();
									residueStock.put(name, tempCountryMap);
								}
								Map<String,Integer> tempSkuMap=tempCountryMap.get(country);
								if(tempSkuMap==null){
									tempSkuMap=Maps.newLinkedHashMap();
									tempCountryMap.put(country, tempSkuMap);
								}
								for (String sku: skuMap.keySet()) {
									if("total".equals(sku)){
										continue;
									}
									tempSkuMap.put(sku, skuMap.get(sku));
									tempSkuMap.put("total",tempSkuMap.get("total")==null?skuMap.get(sku):tempSkuMap.get("total")+skuMap.get(sku) );
								}
							}
						}
					}
					
					
					
					//先分配库存国家LC
					for (String  name: cnStock2.keySet()) {
						//air
						if(componentsList.contains(name)){
							continue;
						}
						Map<String,Map<String,Integer>> countryMap=cnStock2.get(name);
						for (String country: countryMap.keySet()) {
							Map<String,Integer> skuMap=countryMap.get(country);
							Integer totalQuantity=skuMap.get("total");
							if(countryGap!=null&&countryGap.get(name)!=null&&countryGap.get(name).get(country)!=null&&gapMap!=null&&gapMap.get(name+"_"+country)!=null&&gapMap.get(name+"_"+country)>0){//有空运数量
								PsiTransportForecastOrderItem item=countryGap.get(name).get(country);
								//Integer quantity=item.getQuantity();
								Integer quantity=gapMap.get(name+"_"+country);
								if(totalQuantity<quantity){
									Integer jyQuantity=0;
									int num=item.getBoxNum();//多少一箱
									for (String sku: skuMap.keySet()) {
										if("total".equals(sku)){
											continue;
										}
										Integer skuQuantity=skuMap.get(sku);
										Integer boxNum=skuQuantity/num;
										Integer suitQuantity=0;
										if(boxNum>0){
											suitQuantity=boxNum*num;
											PsiTransportForecastOrderItem childItem=new PsiTransportForecastOrderItem();
											childItem.setQuantity(suitQuantity);
											jyQuantity+=suitQuantity;
											childItem.setCheckQuantity(suitQuantity);
											childItem.setBoxNum(item.getBoxNum());
											childItem.setProductName(item.getProductName());
											childItem.setColorCode(item.getColorCode());
											childItem.setCountryCode(item.getCountryCode());
											childItem.setSafeStock(item.getSafeStock());
											childItem.setAmazonStock(item.getAmazonStock());
											childItem.setDay31sales(item.getDay31sales());
											childItem.setModel(item.getModel());
											childItem.setTransportType(item.getTransportType());
											childItem.setDisplaySta("0");
											childItem.setSku(sku);
											childItem.setDetail(item.getDetail());
											childItem.setGap(item.getGap());
											childItem.setPsiTransportForecastOrder(order);
											childItem.setTotalStock(skuQuantity);
											if(poStock2!=null&&poStock2.get(name)!=null&&poStock2.get(name).get(country)!=null&&poStock2.get(name).get(country).get(sku)!=null){
												childItem.setPoStock(poStock2.get(name).get(country).get(sku));
											}else{
												childItem.setPoStock(0);
											}
											
											if(lcCnMap!=null&&lcCnMap.get(name)!=null&&lcCnMap.get(name).get(country)!=null&&lcCnMap.get(name).get(country).get(sku)!=null){
												childItem.setReviewRemark(lcCnMap.get(name).get(country).get(sku)+"");
											}else{
												childItem.setReviewRemark("0");
											}
											
											if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("total")!=null&&transStock2.get(name+"_"+country).get("total").get(sku)!=null){
												childItem.setTransStock(transStock2.get(name+"_"+country).get("total").get(sku));
											}else{
												childItem.setTransStock(0);
											}
											if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("0")!=null&&transStock2.get(name+"_"+country).get("0").get("total")!=null){//0 2
												childItem.setTotalAir(transStock2.get(name+"_"+country).get("0").get("total"));
											}else{
												childItem.setTotalAir(0);
											}
			                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("2")!=null&&transStock2.get(name+"_"+country).get("2").get("total")!=null){//0 2
			                                	childItem.setTotalExp(transStock2.get(name+"_"+country).get("2").get("total"));
											}else{
												childItem.setTotalExp(0);
											}
			                                
			                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("0")!=null&&transStock2.get(name+"_"+country).get("0").get(sku)!=null){//0 2
												childItem.setAirQuantity(transStock2.get(name+"_"+country).get("0").get(sku));
											}else{
												childItem.setAirQuantity(0);
											}
			                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("2")!=null&&transStock2.get(name+"_"+country).get("2").get(sku)!=null){//0 2
			                                	childItem.setExpQuantity(transStock2.get(name+"_"+country).get("2").get(sku));
											}else{
												childItem.setExpQuantity(0);
											}
			                                
			                                
			                                
											Integer oversea=0;
											if(overseaMap!=null&&overseaMap.get(name+"_"+country)!=null){
												oversea=overseaMap.get(name+"_"+country);
											}
											childItem.setOverseaStock(oversea);
											childItem.setTransSta("1");
											childItem.setSalesDay(salesDayMap.get(name+"_"+country));
											if(StringUtils.isNotBlank(childItem.getSku())&&childItem.getSku().contains(childItem.getProductNameColor())){
												childItem.setRemark("新品");
											}
											items.add(childItem);
										}
										
										if(skuQuantity-suitQuantity>0){//有剩余
											Map<String,Map<String,Integer>> tempCountryMap=residueStock2.get(name);
											if(tempCountryMap==null){
												tempCountryMap=Maps.newLinkedHashMap();
												residueStock2.put(name, tempCountryMap);
											}
											Map<String,Integer> tempSkuMap=tempCountryMap.get(country);
											if(tempSkuMap==null){
												tempSkuMap=Maps.newLinkedHashMap();
												tempCountryMap.put(country, tempSkuMap);
											}
											tempSkuMap.put(sku,skuQuantity-suitQuantity);
											tempSkuMap.put("total",tempSkuMap.get("total")==null?skuQuantity-suitQuantity:tempSkuMap.get("total")+skuQuantity-suitQuantity );
										}
									}
									//if(quantity-jyQuantity>0){
										gapMap.put(name+"_"+country, quantity-jyQuantity);
									//}
								}else{
									int num=item.getBoxNum();//多少一箱
									int tempQuantity=0;
									Integer jyQuantity=0;
									for (String sku: skuMap.keySet()) {
										if("total".equals(sku)){
											continue;
										}
										Integer skuQuantity=skuMap.get(sku);
										Integer boxNum=0;
										Integer suitQuantity=0;
										if(tempQuantity<quantity){
											boxNum=skuQuantity/num;
											if(boxNum>0){
												suitQuantity=boxNum*num;
												
												PsiTransportForecastOrderItem childItem=new PsiTransportForecastOrderItem();
												if(tempQuantity+suitQuantity>quantity){
													suitQuantity=quantity-tempQuantity;
													boxNum=suitQuantity/num;
													childItem.setQuantity(suitQuantity);
													jyQuantity+=suitQuantity;
													childItem.setCheckQuantity(suitQuantity);
													childItem.setBoxNum(item.getBoxNum());
												}else{
													childItem.setQuantity(suitQuantity);
													childItem.setCheckQuantity(suitQuantity);
													jyQuantity+=suitQuantity;
													childItem.setBoxNum(item.getBoxNum());
												}
												childItem.setProductName(item.getProductName());
												childItem.setColorCode(item.getColorCode());
												childItem.setCountryCode(item.getCountryCode());
												childItem.setSafeStock(item.getSafeStock());
												childItem.setAmazonStock(item.getAmazonStock());
												childItem.setDay31sales(item.getDay31sales());
												childItem.setModel(item.getModel());
												childItem.setTransportType(item.getTransportType());
												childItem.setDisplaySta("0");
												childItem.setSku(sku);
												childItem.setDetail(item.getDetail());
												childItem.setSalesDay(salesDayMap.get(name+"_"+country));
												Integer oversea=0;
												if(overseaMap!=null&&overseaMap.get(name+"_"+country)!=null){
													oversea=overseaMap.get(name+"_"+country);
												}
												childItem.setOverseaStock(oversea);
												childItem.setPsiTransportForecastOrder(order);
												childItem.setTotalStock(skuQuantity);
												childItem.setGap(item.getGap());
												childItem.setTransSta("1");
												if(poStock2!=null&&poStock2.get(name)!=null&&poStock2.get(name).get(country)!=null&&poStock2.get(name).get(country).get(sku)!=null){
													childItem.setPoStock(poStock2.get(name).get(country).get(sku));
												}else{
													childItem.setPoStock(0);
												}

												if(lcCnMap!=null&&lcCnMap.get(name)!=null&&lcCnMap.get(name).get(country)!=null&&lcCnMap.get(name).get(country).get(sku)!=null){
													childItem.setReviewRemark(lcCnMap.get(name).get(country).get(sku)+"");
												}else{
													childItem.setReviewRemark("0");
												}
												if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("total")!=null&&transStock2.get(name+"_"+country).get("total").get(sku)!=null){
													childItem.setTransStock(transStock2.get(name+"_"+country).get("total").get(sku));
												}else{
													childItem.setTransStock(0);
												}
												if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("0")!=null&&transStock2.get(name+"_"+country).get("0").get("total")!=null){//0 2
													childItem.setTotalAir(transStock2.get(name+"_"+country).get("0").get("total"));
												}else{
													childItem.setTotalAir(0);
												}
				                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("2")!=null&&transStock2.get(name+"_"+country).get("2").get("total")!=null){//0 2
				                                	childItem.setTotalExp(transStock2.get(name+"_"+country).get("2").get("total"));
												}else{
													childItem.setTotalExp(0);
												}
				                                
				                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("0")!=null&&transStock2.get(name+"_"+country).get("0").get(sku)!=null){//0 2
													childItem.setAirQuantity(transStock2.get(name+"_"+country).get("0").get(sku));
												}else{
													childItem.setAirQuantity(0);
												}
				                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("2")!=null&&transStock2.get(name+"_"+country).get("2").get(sku)!=null){//0 2
				                                	childItem.setExpQuantity(transStock2.get(name+"_"+country).get("2").get(sku));
												}else{
													childItem.setExpQuantity(0);
												}
				                                if(StringUtils.isNotBlank(childItem.getSku())&&childItem.getSku().contains(childItem.getProductNameColor())){
													childItem.setRemark("新品");
												}
												items.add(childItem);
											}
											
										}
										tempQuantity+=suitQuantity;
										
										if(skuQuantity-suitQuantity>0){//有剩余
											Map<String,Map<String,Integer>> tempCountryMap=residueStock2.get(name);
											if(tempCountryMap==null){
												tempCountryMap=Maps.newLinkedHashMap();
												residueStock2.put(name, tempCountryMap);
											}
											Map<String,Integer> tempSkuMap=tempCountryMap.get(country);
											if(tempSkuMap==null){
												tempSkuMap=Maps.newLinkedHashMap();
												tempCountryMap.put(country, tempSkuMap);
											}
											tempSkuMap.put(sku,skuQuantity-suitQuantity);
											tempSkuMap.put("total",tempSkuMap.get("total")==null?skuQuantity-suitQuantity:tempSkuMap.get("total")+skuQuantity-suitQuantity );
										}
									}
									//if(quantity-jyQuantity>0){
										gapMap.put(name+"_"+country, quantity-jyQuantity);
									//}
									
								}
							}else{
								Map<String,Map<String,Integer>> tempCountryMap=residueStock2.get(name);
								if(tempCountryMap==null){
									tempCountryMap=Maps.newLinkedHashMap();
									residueStock2.put(name, tempCountryMap);
								}
								Map<String,Integer> tempSkuMap=tempCountryMap.get(country);
								if(tempSkuMap==null){
									tempSkuMap=Maps.newLinkedHashMap();
									tempCountryMap.put(country, tempSkuMap);
								}
								for (String sku: skuMap.keySet()) {
									if("total".equals(sku)){
										continue;
									}
									tempSkuMap.put(sku, skuMap.get(sku));
									tempSkuMap.put("total",tempSkuMap.get("total")==null?skuMap.get(sku):tempSkuMap.get("total")+skuMap.get(sku) );
								}
							}
						}
					}
					
				
					Set<String> nameSet=Sets.newHashSet();
					if(residueStock!=null&&residueStock.size()>0){
						nameSet.addAll(residueStock.keySet());
					}
		            if(residueStock2!=null&&residueStock2.size()>0){
		            	nameSet.addAll(residueStock2.keySet());
					}
					
					if(nameSet!=null&&nameSet.size()>0){//name-country-sku-quantity
						for (String  name: nameSet) {
							if(componentsList.contains(name)){
								continue;
							}
							if(keyBoardSet.contains(name)||fourCtySet.contains(name)){
								continue;
							}
							//1.没有库存 有缺口
							Integer pack=packQuantityMap.get(name);
							Set<String> countrySet=cnStock.get(name).keySet();		
							if(countryGap!=null&&countryGap.get(name)!=null){
								for (String country: countryGap.get(name).keySet()) {
									if("4".equals(productPositionMap.get(name+"_"+country))){
										continue;
									}
									if(!countrySet.contains(country)){//没库存
										PsiTransportForecastOrderItem item=countryGap.get(name).get(country);
										Integer quantity=item.getQuantity();//缺口数
										if(name.contains("Inateck DB1001")){
											if("com,uk,jp,ca,mx,".contains(country)){
												pack=60;
											}else{
												pack=44;
											}
										}else if(name.contains("Inateck DB2001")){
											if("com,jp,ca,mx,".contains(country)){
												pack=32;
											}else{
												pack=24;
											}
										}
										int tempQuantity=0;
										Map<String,Map<String,Integer>> countryMap=residueStock.get(name);
										if(countryMap!=null&&countryMap.size()>0){
											for (String  stockCountry: countryMap.keySet()) {
												Map<String,Integer> skuMap=countryMap.get(stockCountry);
												for (String sku: skuMap.keySet()) {
													if("total".equals(sku)){
														continue;
													}
													Integer skuQuantity=skuMap.get(sku);
													Integer boxNum=0;
													Integer suitQuantity=0;
													if(tempQuantity<quantity){
														boxNum=skuQuantity/pack;
														if(boxNum>0){
															suitQuantity=boxNum*pack;
															PsiTransportForecastOrderItem childItem=new PsiTransportForecastOrderItem();
															if(tempQuantity+suitQuantity>quantity){
																suitQuantity=quantity-tempQuantity;
																boxNum=suitQuantity/pack;
																childItem.setQuantity(suitQuantity);
																childItem.setCheckQuantity(suitQuantity);
																childItem.setBoxNum(pack);
															}else{
																childItem.setQuantity(suitQuantity);
																childItem.setCheckQuantity(suitQuantity);
																childItem.setBoxNum(pack);
															}
															childItem.setProductName(item.getProductName());
															childItem.setColorCode(item.getColorCode());
															childItem.setCountryCode(item.getCountryCode());
															childItem.setSafeStock(item.getSafeStock());
															childItem.setAmazonStock(item.getAmazonStock());
															childItem.setDay31sales(item.getDay31sales());
															childItem.setModel(item.getModel());
															childItem.setTransportType(item.getTransportType());
															childItem.setDetail(item.getDetail());
															childItem.setDisplaySta("0");
															String tempSku=sku;
															if(allSkuMap!=null&&allSkuMap.get(name+"_"+country)!=null){
																tempSku=allSkuMap.get(name+"_"+country);
																childItem.setSku(allSkuMap.get(name+"_"+country));
															}else{
																childItem.setSku(sku);
															}
														
															//childItem.setTotalStock(0);
														//	childItem.setPoStock(0);
															//childItem.setTransStock(0);
															if(cnStock1.get(name)!=null&&cnStock1.get(name).get(country)!=null&&cnStock1.get(name).get(country).get(tempSku)!=null){
								                                childItem.setTotalStock(cnStock1.get(name).get(country).get(tempSku));
															}else{
																childItem.setTotalStock(0);
															}
															if(poStock!=null&&poStock.get(name)!=null&&poStock.get(name).get(country)!=null&&poStock.get(name).get(country).get(tempSku)!=null){
																childItem.setPoStock(poStock.get(name).get(country).get(tempSku));
															}else{
																childItem.setPoStock(0);
															}
															

															if(cnMap!=null&&cnMap.get(name)!=null&&cnMap.get(name).get(country)!=null&&cnMap.get(name).get(country).get(tempSku)!=null){
																childItem.setReviewRemark(cnMap.get(name).get(country).get(tempSku)+"");
															}else{
																childItem.setReviewRemark("0");
															}
															
															/*if(transStock!=null&&transStock.get(name)!=null&&transStock.get(name).get(country)!=null&&transStock.get(name).get(country).get(tempSku)!=null){
																childItem.setPoStock(transStock.get(name).get(country).get(tempSku));
															}else{
																childItem.setTransStock(0);
															}*/
															if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("total")!=null&&transStock1.get(name+"_"+country).get("total").get(tempSku)!=null){
																childItem.setTransStock(transStock1.get(name+"_"+country).get("total").get(tempSku));
															}else{
																childItem.setTransStock(0);
															}
															if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("0")!=null&&transStock1.get(name+"_"+country).get("0").get("total")!=null){//0 2
																childItem.setTotalAir(transStock1.get(name+"_"+country).get("0").get("total"));
															}else{
																childItem.setTotalAir(0);
															}
							                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("2")!=null&&transStock1.get(name+"_"+country).get("2").get("total")!=null){//0 2
							                                	childItem.setTotalExp(transStock1.get(name+"_"+country).get("2").get("total"));
															}else{
																childItem.setTotalExp(0);
															}
							                                
							                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("0")!=null&&transStock1.get(name+"_"+country).get("0").get(tempSku)!=null){//0 2
																childItem.setAirQuantity(transStock1.get(name+"_"+country).get("0").get(tempSku));
															}else{
																childItem.setAirQuantity(0);
															}
							                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("2")!=null&&transStock1.get(name+"_"+country).get("2").get(tempSku)!=null){//0 2
							                                	childItem.setExpQuantity(transStock1.get(name+"_"+country).get("2").get(tempSku));
															}else{
																childItem.setExpQuantity(0);
															}
							                                
															
															childItem.setGap(item.getGap());
															childItem.setPsiTransportForecastOrder(order);
															childItem.setSalesDay(salesDayMap.get(name+"_"+country));
															Integer oversea=0;
															if(overseaMap!=null&&overseaMap.get(name+"_"+country)!=null){
																oversea=overseaMap.get(name+"_"+country);
															}
															childItem.setOverseaStock(oversea);
															childItem.setTransSta("0");
															childItem.setRemark(("com".equals(stockCountry)?"us":stockCountry)+":"+sku+"("+suitQuantity+")");
															if(StringUtils.isNotBlank(childItem.getSku())&&childItem.getSku().contains(childItem.getProductNameColor())){
																childItem.setRemark("新品");
															}
															items.add(childItem);
														}
													}
													tempQuantity+=suitQuantity;
													if(skuQuantity-suitQuantity>=0){
														skuMap.put(sku, skuQuantity-suitQuantity);
													}
												}	
											}
										}
										
										//gapMap.put(name+"_"+country, quantity-tempQuantity);
										//不满足
										if(tempQuantity<quantity){
											Map<String,Map<String,Integer>> countryMap2=residueStock2.get(name);
											if(countryMap2!=null&&countryMap2.size()>0){
												for (String  stockCountry: countryMap2.keySet()) {
													Map<String,Integer> skuMap=countryMap2.get(stockCountry);
													for (String sku: skuMap.keySet()) {
														if("total".equals(sku)){
															continue;
														}
														Integer skuQuantity=skuMap.get(sku);
														Integer boxNum=0;
														Integer suitQuantity=0;
														if(tempQuantity<quantity){
															boxNum=skuQuantity/pack;
															if(boxNum>0){
																suitQuantity=boxNum*pack;
																PsiTransportForecastOrderItem childItem=new PsiTransportForecastOrderItem();
																if(tempQuantity+suitQuantity>quantity){
																	suitQuantity=quantity-tempQuantity;
																	boxNum=suitQuantity/pack;
																	childItem.setQuantity(suitQuantity);
																	childItem.setCheckQuantity(suitQuantity);
																	childItem.setBoxNum(pack);
																}else{
																	childItem.setQuantity(suitQuantity);
																	childItem.setCheckQuantity(suitQuantity);
																	childItem.setBoxNum(pack);
																}
																childItem.setProductName(item.getProductName());
																childItem.setColorCode(item.getColorCode());
																childItem.setCountryCode(item.getCountryCode());
																childItem.setSafeStock(item.getSafeStock());
																childItem.setAmazonStock(item.getAmazonStock());
																childItem.setDay31sales(item.getDay31sales());
																childItem.setModel(item.getModel());
																childItem.setTransportType(item.getTransportType());
																childItem.setDetail(item.getDetail());
																childItem.setDisplaySta("0");
																String tempSku=sku;
																if(allSkuMap!=null&&allSkuMap.get(name+"_"+country)!=null){
																	tempSku=allSkuMap.get(name+"_"+country);
																	childItem.setSku(allSkuMap.get(name+"_"+country));
																}else{
																	childItem.setSku(sku);
																}
															
																//childItem.setTotalStock(0);
															//	childItem.setPoStock(0);
																//childItem.setTransStock(0);
																if(cnStock2.get(name)!=null&&cnStock2.get(name).get(country)!=null&&cnStock2.get(name).get(country).get(tempSku)!=null){
									                                childItem.setTotalStock(cnStock2.get(name).get(country).get(tempSku));
																}else{
																	childItem.setTotalStock(0);
																}
																if(poStock2!=null&&poStock2.get(name)!=null&&poStock2.get(name).get(country)!=null&&poStock2.get(name).get(country).get(tempSku)!=null){
																	childItem.setPoStock(poStock2.get(name).get(country).get(tempSku));
																}else{
																	childItem.setPoStock(0);
																}
																if(lcCnMap!=null&&lcCnMap.get(name)!=null&&lcCnMap.get(name).get(country)!=null&&lcCnMap.get(name).get(country).get(tempSku)!=null){
																	childItem.setReviewRemark(lcCnMap.get(name).get(country).get(tempSku)+"");
																}else{
																	childItem.setReviewRemark("0");
																}
																/*if(transStock!=null&&transStock.get(name)!=null&&transStock.get(name).get(country)!=null&&transStock.get(name).get(country).get(tempSku)!=null){
																	childItem.setPoStock(transStock.get(name).get(country).get(tempSku));
																}else{
																	childItem.setTransStock(0);
																}*/
																if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("total")!=null&&transStock2.get(name+"_"+country).get("total").get(tempSku)!=null){
																	childItem.setTransStock(transStock2.get(name+"_"+country).get("total").get(tempSku));
																}else{
																	childItem.setTransStock(0);
																}
																if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("0")!=null&&transStock2.get(name+"_"+country).get("0").get("total")!=null){//0 2
																	childItem.setTotalAir(transStock2.get(name+"_"+country).get("0").get("total"));
																}else{
																	childItem.setTotalAir(0);
																}
								                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("2")!=null&&transStock2.get(name+"_"+country).get("2").get("total")!=null){//0 2
								                                	childItem.setTotalExp(transStock2.get(name+"_"+country).get("2").get("total"));
																}else{
																	childItem.setTotalExp(0);
																}
								                                
								                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("0")!=null&&transStock2.get(name+"_"+country).get("0").get(tempSku)!=null){//0 2
																	childItem.setAirQuantity(transStock2.get(name+"_"+country).get("0").get(tempSku));
																}else{
																	childItem.setAirQuantity(0);
																}
								                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("2")!=null&&transStock2.get(name+"_"+country).get("2").get(tempSku)!=null){//0 2
								                                	childItem.setExpQuantity(transStock2.get(name+"_"+country).get("2").get(tempSku));
																}else{
																	childItem.setExpQuantity(0);
																}
								                                
																
																childItem.setGap(item.getGap());
																childItem.setPsiTransportForecastOrder(order);
																childItem.setSalesDay(salesDayMap.get(name+"_"+country));
																Integer oversea=0;
																if(overseaMap!=null&&overseaMap.get(name+"_"+country)!=null){
																	oversea=overseaMap.get(name+"_"+country);
																}
																childItem.setOverseaStock(oversea);
																childItem.setTransSta("1");
																childItem.setRemark(("com".equals(stockCountry)?"us":stockCountry)+":"+sku+"("+suitQuantity+")");
																if(StringUtils.isNotBlank(childItem.getSku())&&childItem.getSku().contains(childItem.getProductNameColor())){
																	childItem.setRemark("新品");
																}
																items.add(childItem);
															}
														}
														tempQuantity+=suitQuantity;
														if(skuQuantity-suitQuantity>=0){
															skuMap.put(sku, skuQuantity-suitQuantity);
														}
													}	
												}
											}
										}
										gapMap.put(name+"_"+country, quantity-tempQuantity);
									}
								}
							}
						}
					}
					
					//库存不满足缺口
					if(residueStock!=null&&residueStock.size()>0){//name-country-sku-quantity
						for (String  name: residueStock.keySet()) {
							if(componentsList.contains(name)){
								continue;
							}
							if(keyBoardSet.contains(name)||fourCtySet.contains(name)){
								continue;
							}
							Integer pack=packQuantityMap.get(name);
							if(countryGap!=null&&countryGap.get(name)!=null&&gapMap!=null){
								for (String country: countryGap.get(name).keySet()) {
									    if(gapMap.get(name+"_"+country)==null||gapMap.get(name+"_"+country)<=0){
									    	continue;
									    }
									    if("4".equals(productPositionMap.get(name+"_"+country))){
											continue;
										}
										PsiTransportForecastOrderItem item=countryGap.get(name).get(country);
										Integer quantity=gapMap.get(name+"_"+country);//缺口数
										
										if(name.contains("Inateck DB1001")){
											if("com,uk,jp,ca,mx,".contains(country)){
												pack=60;
											}else{
												pack=44;
											}
										}else if(name.contains("Inateck DB2001")){
											if("com,jp,ca,mx,".contains(country)){
												pack=32;
											}else{
												pack=24;
											}
										}
										int tempQuantity=0;
										Map<String,Map<String,Integer>> countryMap=residueStock.get(name);
										for (String  stockCountry: countryMap.keySet()) {
											Map<String,Integer> skuMap=countryMap.get(stockCountry);
											for (String sku: skuMap.keySet()) {
												if("total".equals(sku)){
													continue;
												}
												Integer skuQuantity=skuMap.get(sku);
												Integer boxNum=0;
												Integer suitQuantity=0;
												if(tempQuantity<quantity){
													boxNum=skuQuantity/pack;
													if(boxNum>0){
														suitQuantity=boxNum*pack;
														PsiTransportForecastOrderItem childItem=new PsiTransportForecastOrderItem();
														if(tempQuantity+suitQuantity>quantity){
															suitQuantity=quantity-tempQuantity;
															boxNum=suitQuantity/pack;
															childItem.setQuantity(suitQuantity);
															childItem.setCheckQuantity(suitQuantity);
															childItem.setBoxNum(pack);
															gapMap.put(name+"_"+country,gapMap.get(name+"_"+country)-suitQuantity);
														}else{
															childItem.setQuantity(suitQuantity);
															childItem.setCheckQuantity(suitQuantity);
															childItem.setBoxNum(pack);
															gapMap.put(name+"_"+country,gapMap.get(name+"_"+country)-suitQuantity);
														}
														childItem.setProductName(item.getProductName());
														childItem.setColorCode(item.getColorCode());
														childItem.setCountryCode(item.getCountryCode());
														childItem.setSafeStock(item.getSafeStock());
														childItem.setAmazonStock(item.getAmazonStock());
														childItem.setDay31sales(item.getDay31sales());
														childItem.setModel(item.getModel());
														childItem.setTransportType(item.getTransportType());
														childItem.setDetail(item.getDetail());
														childItem.setDisplaySta("0");
														childItem.setTransSta("0");
														String tempSku="";
														if(allSkuMap!=null&&allSkuMap.get(name+"_"+country)!=null){
															tempSku=allSkuMap.get(name+"_"+country);
															childItem.setSku(allSkuMap.get(name+"_"+country));
														}else{
															tempSku=sku;
															childItem.setSku(sku);
														}
														if(cnStock1.get(name)!=null&&cnStock1.get(name).get(country)!=null&&cnStock1.get(name).get(country).get(tempSku)!=null){
							                                childItem.setTotalStock(cnStock1.get(name).get(country).get(tempSku));
														}else{
															childItem.setTotalStock(0);
														}
														if(poStock!=null&&poStock.get(name)!=null&&poStock.get(name).get(country)!=null&&poStock.get(name).get(country).get(tempSku)!=null){
															childItem.setPoStock(poStock.get(name).get(country).get(tempSku));
														}else{
															childItem.setPoStock(0);
														}
														if(cnMap!=null&&cnMap.get(name)!=null&&cnMap.get(name).get(country)!=null&&cnMap.get(name).get(country).get(tempSku)!=null){
															childItem.setReviewRemark(cnMap.get(name).get(country).get(tempSku)+"");
														}else{
															childItem.setReviewRemark("0");
														}
														/*if(transStock!=null&&transStock.get(name)!=null&&transStock.get(name).get(country)!=null&&transStock.get(name).get(country).get(sku)!=null){
															childItem.setPoStock(transStock.get(name).get(country).get(sku));
														}else{
															childItem.setTransStock(0);
														}*/
														if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("total")!=null&&transStock1.get(name+"_"+country).get("total").get(tempSku)!=null){
															childItem.setTransStock(transStock1.get(name+"_"+country).get("total").get(tempSku));
														}else{
															childItem.setTransStock(0);
														}
														if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("0")!=null&&transStock1.get(name+"_"+country).get("0").get("total")!=null){//0 2
															childItem.setTotalAir(transStock1.get(name+"_"+country).get("0").get("total"));
														}else{
															childItem.setTotalAir(0);
														}
						                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("2")!=null&&transStock1.get(name+"_"+country).get("2").get("total")!=null){//0 2
						                                	childItem.setTotalExp(transStock1.get(name+"_"+country).get("2").get("total"));
														}else{
															childItem.setTotalExp(0);
														}
						                                
						                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("0")!=null&&transStock1.get(name+"_"+country).get("0").get(tempSku)!=null){//0 2
															childItem.setAirQuantity(transStock1.get(name+"_"+country).get("0").get(tempSku));
														}else{
															childItem.setAirQuantity(0);
														}
						                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("2")!=null&&transStock1.get(name+"_"+country).get("2").get(tempSku)!=null){//0 2
						                                	childItem.setExpQuantity(transStock1.get(name+"_"+country).get("2").get(tempSku));
														}else{
															childItem.setExpQuantity(0);
														}
														
														
														childItem.setGap(item.getGap());
														childItem.setPsiTransportForecastOrder(order);
														childItem.setSalesDay(salesDayMap.get(name+"_"+country));
														Integer oversea=0;
														if(overseaMap!=null&&overseaMap.get(name+"_"+country)!=null){
															oversea=overseaMap.get(name+"_"+country);
														}
														childItem.setOverseaStock(oversea);
														childItem.setRemark(("com".equals(stockCountry)?"us":stockCountry)+":"+sku+"("+suitQuantity+")");
														if(StringUtils.isNotBlank(childItem.getSku())&&childItem.getSku().contains(childItem.getProductNameColor())){
															childItem.setRemark("新品");
														}
														items.add(childItem);
													}
												}
												tempQuantity+=suitQuantity;
												if(skuQuantity-suitQuantity>=0){
													skuMap.put(sku, skuQuantity-suitQuantity);
												}
											}	
										}
										gapMap.put(name+"_"+country, quantity-tempQuantity);
								}
							}
						}
					}
					
					
					//库存不满足缺口
					if(residueStock2!=null&&residueStock2.size()>0){//name-country-sku-quantity
						for (String  name: residueStock2.keySet()) {
							if(componentsList.contains(name)){
								continue;
							}
							if(keyBoardSet.contains(name)||fourCtySet.contains(name)){
								continue;
							}
							Integer pack=packQuantityMap.get(name);
							if(countryGap!=null&&countryGap.get(name)!=null&&gapMap!=null){
								for (String country: countryGap.get(name).keySet()) {
									    if(gapMap.get(name+"_"+country)==null||gapMap.get(name+"_"+country)<=0){
									    	continue;
									    }
									    if("4".equals(productPositionMap.get(name+"_"+country))){
											continue;
										}
										PsiTransportForecastOrderItem item=countryGap.get(name).get(country);
										Integer quantity=gapMap.get(name+"_"+country);//缺口数
										
										if(name.contains("Inateck DB1001")){
											if("com,uk,jp,ca,mx,".contains(country)){
												pack=60;
											}else{
												pack=44;
											}
										}else if(name.contains("Inateck DB2001")){
											if("com,jp,ca,mx,".contains(country)){
												pack=32;
											}else{
												pack=24;
											}
										}
										int tempQuantity=0;
										Map<String,Map<String,Integer>> countryMap=residueStock2.get(name);
										for (String  stockCountry: countryMap.keySet()) {
											Map<String,Integer> skuMap=countryMap.get(stockCountry);
											for (String sku: skuMap.keySet()) {
												if("total".equals(sku)){
													continue;
												}
												Integer skuQuantity=skuMap.get(sku);
												Integer boxNum=0;
												Integer suitQuantity=0;
												if(tempQuantity<quantity){
													boxNum=skuQuantity/pack;
													if(boxNum>0){
														suitQuantity=boxNum*pack;
														PsiTransportForecastOrderItem childItem=new PsiTransportForecastOrderItem();
														if(tempQuantity+suitQuantity>quantity){
															suitQuantity=quantity-tempQuantity;
															boxNum=suitQuantity/pack;
															childItem.setQuantity(suitQuantity);
															childItem.setCheckQuantity(suitQuantity);
															childItem.setBoxNum(pack);
															gapMap.put(name+"_"+country,gapMap.get(name+"_"+country)-suitQuantity);
														}else{
															childItem.setQuantity(suitQuantity);
															childItem.setCheckQuantity(suitQuantity);
															childItem.setBoxNum(pack);
															gapMap.put(name+"_"+country,gapMap.get(name+"_"+country)-suitQuantity);
														}
														childItem.setProductName(item.getProductName());
														childItem.setColorCode(item.getColorCode());
														childItem.setCountryCode(item.getCountryCode());
														childItem.setSafeStock(item.getSafeStock());
														childItem.setAmazonStock(item.getAmazonStock());
														childItem.setDay31sales(item.getDay31sales());
														childItem.setModel(item.getModel());
														childItem.setTransportType(item.getTransportType());
														childItem.setDetail(item.getDetail());
														childItem.setDisplaySta("0");
														childItem.setTransSta("1");
														String tempSku="";
														if(allSkuMap!=null&&allSkuMap.get(name+"_"+country)!=null){
															tempSku=allSkuMap.get(name+"_"+country);
															childItem.setSku(allSkuMap.get(name+"_"+country));
														}else{
															tempSku=sku;
															childItem.setSku(sku);
														}
														if(cnStock2.get(name)!=null&&cnStock2.get(name).get(country)!=null&&cnStock2.get(name).get(country).get(tempSku)!=null){
							                                childItem.setTotalStock(cnStock2.get(name).get(country).get(tempSku));
														}else{
															childItem.setTotalStock(0);
														}
														if(poStock2!=null&&poStock2.get(name)!=null&&poStock2.get(name).get(country)!=null&&poStock2.get(name).get(country).get(tempSku)!=null){
															childItem.setPoStock(poStock2.get(name).get(country).get(tempSku));
														}else{
															childItem.setPoStock(0);
														}
														if(lcCnMap!=null&&lcCnMap.get(name)!=null&&lcCnMap.get(name).get(country)!=null&&lcCnMap.get(name).get(country).get(tempSku)!=null){
															childItem.setReviewRemark(lcCnMap.get(name).get(country).get(tempSku)+"");
														}else{
															childItem.setReviewRemark("0");
														}
														/*if(transStock!=null&&transStock.get(name)!=null&&transStock.get(name).get(country)!=null&&transStock.get(name).get(country).get(sku)!=null){
															childItem.setPoStock(transStock.get(name).get(country).get(sku));
														}else{
															childItem.setTransStock(0);
														}*/
														if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("total")!=null&&transStock2.get(name+"_"+country).get("total").get(tempSku)!=null){
															childItem.setTransStock(transStock2.get(name+"_"+country).get("total").get(tempSku));
														}else{
															childItem.setTransStock(0);
														}
														if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("0")!=null&&transStock2.get(name+"_"+country).get("0").get("total")!=null){//0 2
															childItem.setTotalAir(transStock2.get(name+"_"+country).get("0").get("total"));
														}else{
															childItem.setTotalAir(0);
														}
						                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("2")!=null&&transStock2.get(name+"_"+country).get("2").get("total")!=null){//0 2
						                                	childItem.setTotalExp(transStock2.get(name+"_"+country).get("2").get("total"));
														}else{
															childItem.setTotalExp(0);
														}
						                                
						                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("0")!=null&&transStock2.get(name+"_"+country).get("0").get(tempSku)!=null){//0 2
															childItem.setAirQuantity(transStock2.get(name+"_"+country).get("0").get(tempSku));
														}else{
															childItem.setAirQuantity(0);
														}
						                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("2")!=null&&transStock2.get(name+"_"+country).get("2").get(tempSku)!=null){//0 2
						                                	childItem.setExpQuantity(transStock2.get(name+"_"+country).get("2").get(tempSku));
														}else{
															childItem.setExpQuantity(0);
														}
														
														
														childItem.setGap(item.getGap());
														childItem.setPsiTransportForecastOrder(order);
														childItem.setSalesDay(salesDayMap.get(name+"_"+country));
														Integer oversea=0;
														if(overseaMap!=null&&overseaMap.get(name+"_"+country)!=null){
															oversea=overseaMap.get(name+"_"+country);
														}
														childItem.setOverseaStock(oversea);
														childItem.setRemark(("com".equals(stockCountry)?"us":stockCountry)+":"+sku+"("+suitQuantity+")");
														if(StringUtils.isNotBlank(childItem.getSku())&&childItem.getSku().contains(childItem.getProductNameColor())){
															childItem.setRemark("新品");
														}
														items.add(childItem);
													}
												}
												tempQuantity+=suitQuantity;
												if(skuQuantity-suitQuantity>=0){
													skuMap.put(sku, skuQuantity-suitQuantity);
												}
											}	
										}
								}
							}
						}
					}
					
					//剩余数量海运
					if(residueStock!=null&&residueStock.size()>0){
						for (String  name: residueStock.keySet()) {
							if(componentsList.contains(name)){
								continue;
							}
							Map<String,Map<String,Integer>> countryMap=residueStock.get(name);
							for (String country: countryMap.keySet()) {
								//if(!"ca".equals(country)){
									Map<String,Integer> skuMap=countryMap.get(country);
									for (String sku: skuMap.keySet()) {
										if("total".equals(sku)){
											continue;
										}
										Integer skuQuantity=skuMap.get(sku);
										if(skuQuantity==0){
											continue;
										}
										//PsiTransportForecastOrderItem item=countryGap.get(name).get(country);
										Integer pack=packQuantityMap.get(name);
										if(name.contains("Inateck DB1001")){
											if("com,uk,jp,ca,mx,".contains(country)){
												pack=60;
											}else{
												pack=44;
											}
										}else if(name.contains("Inateck DB2001")){
											if("com,jp,ca,mx,".contains(country)){
												pack=32;
											}else{
												pack=24;
											}
										}
										
										Integer boxNum=skuQuantity/pack;
										if(boxNum<=0){
											continue;
										}
										Integer suitQuantity=boxNum*pack;
										PsiTransportForecastOrderItem childItem=new PsiTransportForecastOrderItem();
		                              
		                                if(cnStock1.get(name)!=null&&cnStock1.get(name).get(country)!=null&&cnStock1.get(name).get(country).get(sku)!=null){
			                                childItem.setTotalStock(cnStock1.get(name).get(country).get(sku));
										}else{
											childItem.setTotalStock(0);
										}
										if(poStock!=null&&poStock.get(name)!=null&&poStock.get(name).get(country)!=null&&poStock.get(name).get(country).get(sku)!=null){
											childItem.setPoStock(poStock.get(name).get(country).get(sku));
										}else{
											childItem.setPoStock(0);
										}
										if(cnMap!=null&&cnMap.get(name)!=null&&cnMap.get(name).get(country)!=null&&cnMap.get(name).get(country).get(sku)!=null){
											childItem.setReviewRemark(cnMap.get(name).get(country).get(sku)+"");
										}else{
											childItem.setReviewRemark("0");
										}
										/*if(transStock!=null&&transStock.get(name)!=null&&transStock.get(name).get(country)!=null&&transStock.get(name).get(country).get(sku)!=null){
											childItem.setTransStock(transStock.get(name).get(country).get(sku));
										}else{
											childItem.setTransStock(0);
										}*/
										if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("total")!=null&&transStock1.get(name+"_"+country).get("total").get(sku)!=null){
											childItem.setTransStock(transStock1.get(name+"_"+country).get("total").get(sku));
										}else{
											childItem.setTransStock(0);
										}
										if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("0")!=null&&transStock1.get(name+"_"+country).get("0").get("total")!=null){//0 2
											childItem.setTotalAir(transStock1.get(name+"_"+country).get("0").get("total"));
										}else{
											childItem.setTotalAir(0);
										}
		                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("2")!=null&&transStock1.get(name+"_"+country).get("2").get("total")!=null){//0 2
		                                	childItem.setTotalExp(transStock1.get(name+"_"+country).get("2").get("total"));
										}else{
											childItem.setTotalExp(0);
										}
		                                
		                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("0")!=null&&transStock1.get(name+"_"+country).get("0").get(sku)!=null){//0 2
											childItem.setAirQuantity(transStock1.get(name+"_"+country).get("0").get(sku));
										}else{
											childItem.setAirQuantity(0);
										}
		                                if(transStock1!=null&&transStock1.get(name+"_"+country)!=null&&transStock1.get(name+"_"+country).get("2")!=null&&transStock1.get(name+"_"+country).get("2").get(sku)!=null){//0 2
		                                	childItem.setExpQuantity(transStock1.get(name+"_"+country).get("2").get(sku));
										}else{
											childItem.setExpQuantity(0);
										}
		                                if("ca".equals(country)){
		                                	childItem.setQuantity(0);
		    								childItem.setCheckQuantity(0);
		                                }else{
		                                	childItem.setQuantity(suitQuantity);
		    								childItem.setCheckQuantity(suitQuantity);
		                                }
										
										childItem.setBoxNum(pack);
										childItem.setPsiTransportForecastOrder(order);
										if(name.contains("_")){
											String[] arr=name.split("_");
											childItem.setProductName(arr[0]);
											childItem.setColorCode(arr[1]);
										}else{
											childItem.setProductName(name);
											childItem.setColorCode("");
										}
										childItem.setCountryCode(country);
										Integer safeInventory=0;
										Integer fbaStock=0;
										Integer daySales=0;
										if(!keyBoardSet.contains(name)&&!fourCtySet.contains(name)&&"de".equals(country)){
											if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get("eu")!=null){
												safeInventory=MathUtils.roundUp(stockMap.get(name).get("eu").getSafeInventory());
											}
											PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_eu");
											if(psiInventoryFba!=null){
												fbaStock=psiInventoryFba.getRealTotal();
												Integer receiveNum=0;
												Map<String,Integer> receiveFbaTran = psiInventoryService.getFbaTransporttingReceiveByName(name);
												if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
													for(String euCountry:euCountryList){
														receiveNum+=(receiveFbaTran.get(name+"_"+euCountry)==null?0:receiveFbaTran.get(name+"_"+euCountry));
													}
												}
												if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
													fbaStock=fbaStock-receiveNum;
												}
											}
											
											if(daySalesMap.get(name+"_eu")!=null){
												daySales=daySalesMap.get(name+"_eu");
											}else{
												Integer totalSal=0;
												for(String euCountry:euCountryList){
													if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
														totalSal+=sale30Map.get(euCountry).get(name);
													}
												}
												daySales=MathUtils.roundUp(totalSal/31d);
											}
											
										}else if(fourCtySet.contains(name)&&"de".contains(country)){
											if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get("eu")!=null){
												safeInventory=MathUtils.roundUp(stockMap.get(name).get("eu").getSafeInventory());
												if(stockMap.get(name).get("uk")!=null){
													safeInventory=safeInventory-MathUtils.roundUp(stockMap.get(name).get("uk").getSafeInventory());
												}
											}
											PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_four");
											if(psiInventoryFba!=null){
												fbaStock=psiInventoryFba.getRealTotal();
												Integer receiveNum=0;
												Map<String,Integer> receiveFbaTran = psiInventoryService.getFbaTransporttingReceiveByName(name);
												if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
													for(String euCountry:fourCountryList){
														receiveNum+=(receiveFbaTran.get(name+"_"+euCountry)==null?0:receiveFbaTran.get(name+"_"+euCountry));
													}
												}
												if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
													fbaStock=fbaStock-receiveNum;
												}
											}
											
											if(daySalesMap.get(name+"_euNoUK")!=null){
												daySales=daySalesMap.get(name+"_euNoUK");
											}else{
												Integer totalSal=0;
												for(String euCountry:fourCountryList){
													if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
														totalSal+=sale30Map.get(euCountry).get(name);
													}
												}
												daySales=MathUtils.roundUp(totalSal/31d);
											}
										}else if(("ca,jp,com,mx".contains(country))||country.startsWith("com")||(keyBoardSet.contains(name)&&"de,fr,it,es,uk".contains(country))||(fourCtySet.contains(name)&&"uk".contains(country))){
											if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get(country)!=null){
												safeInventory=MathUtils.roundUp(stockMap.get(name).get(country).getSafeInventory());
											}
											PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_"+country);
											if(psiInventoryFba!=null){
												fbaStock=psiInventoryFba.getRealTotal();
												Integer receiveNum=0;
												Map<String,Integer> receiveFbaTran = psiInventoryService.getFbaTransporttingReceiveByName(name);
												if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
														receiveNum+=(receiveFbaTran.get(name+"_"+country)==null?0:receiveFbaTran.get(name+"_"+country));
												}
												if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
													fbaStock=fbaStock-receiveNum;
												}
											}
											if(daySalesMap.get(name+"_"+country)!=null){
												daySales=daySalesMap.get(name+"_"+country);
											}else{
												if(sale30Map!=null&&sale30Map.get(country)!=null&&sale30Map.get(country).get(name)!=null){
													daySales=MathUtils.roundUp(sale30Map.get(country).get(name)/31d);
												}
											}
											
										}
		                                if((daySales==0&&"1".equals(newMap.get(name+"_"+country)))||"ca".equals(country)){
		                                	childItem.setModel("0");
										}else{
											childItem.setModel("1");//海运
										}
										childItem.setSafeStock(safeInventory);
										childItem.setAmazonStock(fbaStock);
										childItem.setDay31sales(daySales);
										
										if("uk,fr,it,es,de".contains(country)||country.startsWith("com")){
											childItem.setTransportType("0");
										}else{
											childItem.setTransportType("1");
										}
										childItem.setDisplaySta("0");
										childItem.setTransSta("0");
										childItem.setSku(sku);
										childItem.setSalesDay(salesDayMap.get(name+"_"+country));
										if(tranMap!=null&&StringUtils.isNotBlank(tranMap.get(name+"_"+country))){
											childItem.setDetail(tranMap.get(name+"_"+country));
										}
										Integer oversea=0;
										if(overseaMap!=null&&overseaMap.get(name+"_"+country)!=null){
											oversea=overseaMap.get(name+"_"+country);
										}
										childItem.setOverseaStock(oversea);
										childItem.setGap(0);
										if(StringUtils.isNotBlank(childItem.getSku())&&childItem.getSku().contains(childItem.getProductNameColor())){
											childItem.setRemark("新品");
										}
										items.add(childItem);
									}
								//}
							}
						 
						}	
					}
					
					//剩余数量海运
					if(residueStock2!=null&&residueStock2.size()>0){
						for (String  name: residueStock2.keySet()) {
							if(componentsList.contains(name)){
								continue;
							}
							Map<String,Map<String,Integer>> countryMap=residueStock2.get(name);
							for (String country: countryMap.keySet()) {
							//	if(!"ca".equals(country)){
									Map<String,Integer> skuMap=countryMap.get(country);
									for (String sku: skuMap.keySet()) {
										if("total".equals(sku)){
											continue;
										}
										Integer skuQuantity=skuMap.get(sku);
										if(skuQuantity==0){
											continue;
										}
										//PsiTransportForecastOrderItem item=countryGap.get(name).get(country);
										Integer pack=packQuantityMap.get(name);
										if(name.contains("Inateck DB1001")){
											if("com,uk,jp,ca,mx,".contains(country)){
												pack=60;
											}else{
												pack=44;
											}
										}else if(name.contains("Inateck DB2001")){
											if("com,jp,ca,mx,".contains(country)){
												pack=32;
											}else{
												pack=24;
											}
										}
										
										Integer boxNum=skuQuantity/pack;
										if(boxNum<=0){
											continue;
										}
										Integer suitQuantity=boxNum*pack;
										PsiTransportForecastOrderItem childItem=new PsiTransportForecastOrderItem();
		                              
		                                if(cnStock2.get(name)!=null&&cnStock2.get(name).get(country)!=null&&cnStock2.get(name).get(country).get(sku)!=null){
			                                childItem.setTotalStock(cnStock2.get(name).get(country).get(sku));
										}else{
											childItem.setTotalStock(0);
										}
										if(poStock2!=null&&poStock2.get(name)!=null&&poStock2.get(name).get(country)!=null&&poStock2.get(name).get(country).get(sku)!=null){
											childItem.setPoStock(poStock2.get(name).get(country).get(sku));
										}else{
											childItem.setPoStock(0);
										}
										if(lcCnMap!=null&&lcCnMap.get(name)!=null&&lcCnMap.get(name).get(country)!=null&&lcCnMap.get(name).get(country).get(sku)!=null){
											childItem.setReviewRemark(lcCnMap.get(name).get(country).get(sku)+"");
										}else{
											childItem.setReviewRemark("0");
										}
										/*if(transStock!=null&&transStock.get(name)!=null&&transStock.get(name).get(country)!=null&&transStock.get(name).get(country).get(sku)!=null){
											childItem.setTransStock(transStock.get(name).get(country).get(sku));
										}else{
											childItem.setTransStock(0);
										}*/
										if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("total")!=null&&transStock2.get(name+"_"+country).get("total").get(sku)!=null){
											childItem.setTransStock(transStock2.get(name+"_"+country).get("total").get(sku));
										}else{
											childItem.setTransStock(0);
										}
										if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("0")!=null&&transStock2.get(name+"_"+country).get("0").get("total")!=null){//0 2
											childItem.setTotalAir(transStock2.get(name+"_"+country).get("0").get("total"));
										}else{
											childItem.setTotalAir(0);
										}
		                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("2")!=null&&transStock2.get(name+"_"+country).get("2").get("total")!=null){//0 2
		                                	childItem.setTotalExp(transStock2.get(name+"_"+country).get("2").get("total"));
										}else{
											childItem.setTotalExp(0);
										}
		                                
		                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("0")!=null&&transStock2.get(name+"_"+country).get("0").get(sku)!=null){//0 2
											childItem.setAirQuantity(transStock2.get(name+"_"+country).get("0").get(sku));
										}else{
											childItem.setAirQuantity(0);
										}
		                                if(transStock2!=null&&transStock2.get(name+"_"+country)!=null&&transStock2.get(name+"_"+country).get("2")!=null&&transStock2.get(name+"_"+country).get("2").get(sku)!=null){//0 2
		                                	childItem.setExpQuantity(transStock2.get(name+"_"+country).get("2").get(sku));
										}else{
											childItem.setExpQuantity(0);
										}
										
										if("ca".equals(country)){
			                                	childItem.setQuantity(0);
			    								childItem.setCheckQuantity(0);
			                            }else{
			                                	childItem.setQuantity(suitQuantity);
			    								childItem.setCheckQuantity(suitQuantity);
			                            }
										childItem.setBoxNum(pack);
										childItem.setPsiTransportForecastOrder(order);
										if(name.contains("_")){
											String[] arr=name.split("_");
											childItem.setProductName(arr[0]);
											childItem.setColorCode(arr[1]);
										}else{
											childItem.setProductName(name);
											childItem.setColorCode("");
										}
										childItem.setCountryCode(country);
										Integer safeInventory=0;
										Integer fbaStock=0;
										Integer daySales=0;
										
										Integer seaDays=PsiConfig.get(country).getFbaBySea();
										if("de,jp,com".contains(country)&&seaDayMap!=null&&seaDayMap.get(country)!=null){
											seaDays=seaDayMap.get(country)+2;
										}
										if("ca".equals(country)){
											seaDays=PsiConfig.get(country).getFbaBySky();
										}
										
										if(!keyBoardSet.contains(name)&&!fourCtySet.contains(name)&&"de".equals(country)){
											if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get("eu")!=null){
												safeInventory=MathUtils.roundUp(stockMap.get(name).get("eu").getSafeInventory());
											}
											PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_eu");
											if(psiInventoryFba!=null){
												fbaStock=psiInventoryFba.getRealTotal();
												Integer receiveNum=0;
												Map<String,Integer> receiveFbaTran = psiInventoryService.getFbaTransporttingReceiveByName(name);
												if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
													for(String euCountry:euCountryList){
														receiveNum+=(receiveFbaTran.get(name+"_"+euCountry)==null?0:receiveFbaTran.get(name+"_"+euCountry));
													}
												}
												if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
													fbaStock=fbaStock-receiveNum;
												}
											}
											
											Map<String, Double> forecastAvgMap = forecastOrderService.getAvg(DateUtils.addDays(today,seaDays+7), forecastDatas);
											if(forecastAvgMap!=null&&forecastAvgMap.get(name+"_eu")!=null&&forecastAvgMap.get(name+"_eu")>0){//name_country _eunouk  _eu
												daySales=MathUtils.roundUp(forecastAvgMap.get(name+"_eu"));
											}else{
												Integer totalSal=0;
												for(String euCountry:euCountryList){
													if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
														totalSal+=sale30Map.get(euCountry).get(name);
													}
												}
												daySales=MathUtils.roundUp(totalSal/31d);
											}
										}else if(fourCtySet.contains(name)&&"de".contains(country)){
											if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get("eu")!=null){
												safeInventory=MathUtils.roundUp(stockMap.get(name).get("eu").getSafeInventory());
												if(stockMap.get(name).get("uk")!=null){
													safeInventory=safeInventory-MathUtils.roundUp(stockMap.get(name).get("uk").getSafeInventory());
												}
											}
											PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_four");
											if(psiInventoryFba!=null){
												fbaStock=psiInventoryFba.getRealTotal();
												Integer receiveNum=0;
												Map<String,Integer> receiveFbaTran = psiInventoryService.getFbaTransporttingReceiveByName(name);
												if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
													for(String euCountry:fourCountryList){
														receiveNum+=(receiveFbaTran.get(name+"_"+euCountry)==null?0:receiveFbaTran.get(name+"_"+euCountry));
													}
												}
												if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
													fbaStock=fbaStock-receiveNum;
												}
											}
											
											Map<String, Double> forecastAvgMap = forecastOrderService.getAvg(DateUtils.addDays(today,seaDays+7), forecastDatas);
											if(forecastAvgMap!=null&&forecastAvgMap.get(name+"_eunouk")!=null&&forecastAvgMap.get(name+"_eunouk")>0){//name_country _eunouk  _eu
												daySales=MathUtils.roundUp(forecastAvgMap.get(name+"_eunouk"));
											}else{
												Integer totalSal=0;
												for(String euCountry:fourCountryList){
													if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
														totalSal+=sale30Map.get(euCountry).get(name);
													}
												}
												daySales=MathUtils.roundUp(totalSal/31d);
											}
											
										}else if(("ca,jp,com,mx".contains(country))||country.startsWith("com")||(keyBoardSet.contains(name)&&"de,fr,it,es,uk".contains(country))||(fourCtySet.contains(name)&&"uk".contains(country))){
											if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get(country)!=null){
												safeInventory=MathUtils.roundUp(stockMap.get(name).get(country).getSafeInventory());
											}
											PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_"+country);
											if(psiInventoryFba!=null){
												fbaStock=psiInventoryFba.getRealTotal();
												Integer receiveNum=0;
												Map<String,Integer> receiveFbaTran = psiInventoryService.getFbaTransporttingReceiveByName(name);
												if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
														receiveNum+=(receiveFbaTran.get(name+"_"+country)==null?0:receiveFbaTran.get(name+"_"+country));
												}
												if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
													fbaStock=fbaStock-receiveNum;
												}
											}
											
											Map<String, Double> forecastAvgMap = forecastOrderService.getAvg(DateUtils.addDays(today,seaDays+7), forecastDatas);
											if(forecastAvgMap!=null&&forecastAvgMap.get(name+"_"+country)!=null&&forecastAvgMap.get(name+"_"+country)>0){//name_country _eunouk  _eu
												daySales=MathUtils.roundUp(forecastAvgMap.get(name+"_"+country));
											}else{
												if(sale30Map!=null&&sale30Map.get(country)!=null&&sale30Map.get(country).get(name)!=null){
													daySales=MathUtils.roundUp(sale30Map.get(country).get(name)/31d);
												}
											}
											
										}
		                                if((daySales==0&&"1".equals(newMap.get(name+"_"+country)))||"ca".equals(country)){
		                                	childItem.setModel("0");
										}else{
											childItem.setModel("1");//海运
										}
										childItem.setSafeStock(safeInventory);
										childItem.setAmazonStock(fbaStock);
										childItem.setDay31sales(daySales);
										
										if("uk,fr,it,es,de".contains(country)||country.startsWith("com")){
											childItem.setTransportType("0");
										}else{
											childItem.setTransportType("1");
										}
										childItem.setDisplaySta("0");
										childItem.setTransSta("1");
										childItem.setSku(sku);
										childItem.setSalesDay(salesDayMap.get(name+"_"+country));
										if(tranMap!=null&&StringUtils.isNotBlank(tranMap.get(name+"_"+country))){
											childItem.setDetail(tranMap.get(name+"_"+country));
										}
										Integer oversea=0;
										if(overseaMap!=null&&overseaMap.get(name+"_"+country)!=null){
											oversea=overseaMap.get(name+"_"+country);
										}
										childItem.setOverseaStock(oversea);
										childItem.setGap(0);
										if(StringUtils.isNotBlank(childItem.getSku())&&childItem.getSku().contains(childItem.getProductNameColor())){
											childItem.setRemark("新品");
										}
										items.add(childItem);
									}
								//}
							}
						 
						}	
					}
					
					if(items!=null&&items.size()>0){
						//生成data
						
						order.setOrderSta("1");
						order.setCreateDate(new Date());
						order.setCreateUser(UserUtils.getUser());
						order.setItems(items);
						psiTransportForecastOrderService.save(order);
						
						Integer id=order.getId();
						Map<String,Map<String,String>> map=psiInventoryService.find();
						if(map!=null&&map.size()>0){
							psiTransportForecastOrderService.updateRemark(map,id);
						}
						List<String> nameList = dictService.getProductNameByLineId("be2ed665694e4fd19dcda7e839bafd03");
						psiTransportForecastOrderService.updateQuantity(nameList,id);
						//psiTransportForecastOrderService.updateUsDelFlag(id);
					}
					
				}
			}
	    }.start();
		
		return "redirect:"+Global.getAdminPath()+"/psi/transportForecastOrder/?repage";
	}
	
	@RequestMapping(value = {"exportSingle"})
	public String exportSingle(PsiTransportForecastOrder psiTransportForecastOrder,String name,String selCountry,String transModel,HttpServletRequest request, HttpServletResponse response) throws ParseException, UnsupportedEncodingException {
	
		Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> map=psiTransportForecastOrderService.get(psiTransportForecastOrder.getId(),name,selCountry,transModel);
		
		
		int excelNo =1;
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
	
		String[] title = {"国家","运输类型","Model","产品","Sku","FBA库存","安全库存","平均日销","在途","系统数量","审核数量","装箱数","备注","审批备注"};
		
		  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		  //设置Excel中的边框(表头的边框)
//		  //设置字体
		  HSSFFont font = wb.createFont();
		  font.setFontHeightInPoints((short) 11); // 字体高度
		  style.setFont(font);
		  row.setHeight((short) 400);
		  HSSFCell cell = null;		
		  for (int i = 0; i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
		  HSSFCellStyle  style1 = wb.createCellStyle();
		  HSSFFont font1 = wb.createFont();
		  font1.setColor(HSSFFont.COLOR_RED);
		  style1.setFont(font1);
		  
		  for( Map.Entry<String, Map<String, Map<String, List<PsiTransportForecastOrderItem>>>> entry:map.entrySet()){
				Map<String,Map<String,List<PsiTransportForecastOrderItem>>> countryMap=entry.getValue();
				for(Map.Entry<String,Map<String,List<PsiTransportForecastOrderItem>>> entry1:countryMap.entrySet()){
					Map<String,List<PsiTransportForecastOrderItem>> typeMap=entry1.getValue();
					for(Map.Entry<String,List<PsiTransportForecastOrderItem>> entry2:typeMap.entrySet()){
						List<PsiTransportForecastOrderItem> list=entry2.getValue();
						for (PsiTransportForecastOrderItem item : list) {
							  row = sheet.createRow(excelNo++);
							  int i =0;
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getCountryCode().equals("com")?"us":item.getCountryCode());
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getTransportTypeName());
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getModelName());
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getProductName());
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getSku());
							  
							  if(item.getAmazonStock()==null){
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(""); 
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getAmazonStock());
							  }
							  if(item.getSafeStock()==null){
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(""); 
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getSafeStock()); 
							  }
							  
							  if(item.getDay31sales()==null){
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(""); 
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getDay31sales()); 
							  }
							  
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getDetail()==null?"":item.getDetail());
							  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getQuantity());
							  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getCheckQuantity());
							  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getBoxNum());
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getRemark()==null?"":item.getRemark());
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getReviewRemark()==null?"":item.getReviewRemark());
						}
					}
				}
			}
		
		  	request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			  for (int i = 0; i <title.length; i++) {
		       		 sheet.autoSizeColumn((short)i, true);
				 }
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			String fileName = "TransportData" + sdf.format(new Date()) + ".xls";
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
	
	@RequestMapping(value = {"export"})
	public String export(PsiTransportForecastOrder psiTransportForecastOrder,String name,String selCountry,String transModel,HttpServletRequest request, HttpServletResponse response) throws ParseException, UnsupportedEncodingException {
	
		Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> map=psiTransportForecastOrderService.get(psiTransportForecastOrder.getId(),name,selCountry,transModel);
		Map<Integer, String>  productMap=psiProductService.getVomueAndWeight();
		
		int excelNo =1;
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCellStyle cellStyle= wb.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
	
		String[] title = {"国家","运输类型","Model","产品","Sku","审核数量","装箱数","箱数","体积","重量"};
		
		  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		  //设置Excel中的边框(表头的边框)
//		  //设置字体
		  HSSFFont font = wb.createFont();
		  font.setFontHeightInPoints((short) 11); // 字体高度
		  style.setFont(font);
		  row.setHeight((short) 400);
		  HSSFCell cell = null;		
		  for (int i = 0; i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
		  HSSFCellStyle  style1 = wb.createCellStyle();
		  HSSFFont font1 = wb.createFont();
		  font1.setColor(HSSFFont.COLOR_RED);
		  style1.setFont(font1);
		  
		  Integer totalBox=0;
		  Float totalWeight=0f;
		  Float totalVolume=0f;
		  for( Map.Entry<String, Map<String, Map<String, List<PsiTransportForecastOrderItem>>>> entry:map.entrySet()){
				Map<String,Map<String,List<PsiTransportForecastOrderItem>>> countryMap=entry.getValue();
				for(Map.Entry<String,Map<String,List<PsiTransportForecastOrderItem>>> entry1:countryMap.entrySet()){
					Map<String,List<PsiTransportForecastOrderItem>> typeMap=entry1.getValue();
					for(Map.Entry<String,List<PsiTransportForecastOrderItem>> entry2:typeMap.entrySet()){
						List<PsiTransportForecastOrderItem> list=entry2.getValue();
						for (PsiTransportForecastOrderItem item : list) {
							  row = sheet.createRow(excelNo++);
							  int i =0;
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getCountryCode().equals("com")?"us":item.getCountryCode());
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getTransportTypeName());
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getModelName());
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getProductName());
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getSku());
							  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getCheckQuantity());
							  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getBoxNum());
							  Integer box=MathUtils.roundUp(item.getCheckQuantity()*1.0d/item.getBoxNum());
							  totalBox+=box ;
							  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(box);
							  PsiProduct product=psiProductService.findProductByProductName(item.getProductName());
							  float volume=item.getCheckQuantity()/(float)(item.getBoxNum()*(Float.parseFloat(productMap.get(product.getId()).split(",")[0])));
							  totalVolume+=volume;
							  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(volume);
							  row.getCell(i-1).setCellStyle(cellStyle);
							  float weight=item.getCheckQuantity()/(float)(item.getBoxNum()*(Float.parseFloat(productMap.get(product.getId()).split(",")[1])));
							  totalWeight+=weight;
							  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(weight);
							  row.getCell(i-1).setCellStyle(cellStyle);
						}
					}
				}
			}
		  row = sheet.createRow(excelNo++);
		  int i =0;
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(totalBox);
		  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(totalVolume);
		  row.getCell(i-1).setCellStyle(cellStyle);
		  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(totalWeight);
		  row.getCell(i-1).setCellStyle(cellStyle);
		  for (int j = 0; j<title.length; j++) {
	       		 sheet.autoSizeColumn((short)j, true);
			 }
		  	request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			String fileName = "TransportData" + sdf.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value = {"exportEU"})
	public String exportEU(PsiTransportForecastOrder psiTransportForecastOrder,String name,String selCountry,String transModel,HttpServletRequest request, HttpServletResponse response) throws ParseException, UnsupportedEncodingException {
	
		Map<String,Map<String,Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>>>> map=psiTransportForecastOrderService.getByCountry(psiTransportForecastOrder.getId(),name,"eu",transModel);
		Map<Integer, String>  productMap=psiProductService.getVomueAndWeight();
		
		int excelNo =1;
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCellStyle cellStyle= wb.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
	
		String[] title = {"运输类型","Model","产品","Sku","DE审核数","DE箱数","UK审核数","UK箱数","FR审核数","FR箱数","IT审核数","IT箱数","ES审核数","ES箱数","总箱数","体积","重量"};
		
		  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		  //设置Excel中的边框(表头的边框)
//		  //设置字体
		  HSSFFont font = wb.createFont();
		  font.setFontHeightInPoints((short) 11); // 字体高度
		  style.setFont(font);
		  row.setHeight((short) 400);
		  HSSFCell cell = null;		
		  for (int i = 0; i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
		  HSSFCellStyle  style1 = wb.createCellStyle();
		  HSSFFont font1 = wb.createFont();
		  font1.setColor(HSSFFont.COLOR_RED);
		  style1.setFont(font1);
		  
		  Integer totalBox=0;
		  Float totalWeight=0f;
		  Float totalVolume=0f;
		  
		  
		  for(Map.Entry<String, Map<String, Map<String, Map<String, Map<String, PsiTransportForecastOrderItem>>>>> entry:map.entrySet()){
			    String type = entry.getKey();
				Map<String,Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>>> countryMap=entry.getValue();
				for(Map.Entry<String,Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>>> entry1:countryMap.entrySet()){
					String model = entry1.getKey();
					Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>> typeMap=entry1.getValue();
					for(Map.Entry<String,Map<String,Map<String,PsiTransportForecastOrderItem>>> entry2:typeMap.entrySet()){
						String productName = entry2.getKey();
						Map<String,Map<String,PsiTransportForecastOrderItem>> nameMap=entry2.getValue();
						for (Map.Entry<String,Map<String,PsiTransportForecastOrderItem>> entry3: nameMap.entrySet()) {
							String sku = entry3.getKey();
							Map<String,PsiTransportForecastOrderItem> skuMap=entry3.getValue();
							
							PsiTransportForecastOrderItem deItem=skuMap.get("de");
							PsiTransportForecastOrderItem ukItem=skuMap.get("uk");
							PsiTransportForecastOrderItem frItem=skuMap.get("fr");
							PsiTransportForecastOrderItem itItem=skuMap.get("it");
							PsiTransportForecastOrderItem esItem=skuMap.get("es");
							
							
							  row = sheet.createRow(excelNo++);
							  int i =0;
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("0".equals(type)?"本地运输":"FBA运输");
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("0".equals(model)?"空运":("1".equals(model)?"海运":"快递"));
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(productName);
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sku);
							  Integer allBox=0;
							  Integer quantity=0;
							  Integer boxNum=0;
							 // row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getBoxNum());
							  if(deItem!=null){
								  quantity+=deItem.getCheckQuantity();
								  boxNum=deItem.getBoxNum();
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(deItem.getCheckQuantity());
								  Integer box=MathUtils.roundUp(deItem.getCheckQuantity()*1.0d/deItem.getBoxNum());
								  allBox+=box ;
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(box);
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  if(ukItem!=null){
								  boxNum=ukItem.getBoxNum();
								  quantity+=ukItem.getCheckQuantity();
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(ukItem.getCheckQuantity());
								  Integer box=MathUtils.roundUp(ukItem.getCheckQuantity()*1.0d/ukItem.getBoxNum());
								  allBox+=box ;
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(box);
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  if(frItem!=null){
								  boxNum=frItem.getBoxNum();
								  quantity+=frItem.getCheckQuantity();
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(frItem.getCheckQuantity());
								  Integer box=MathUtils.roundUp(frItem.getCheckQuantity()*1.0d/frItem.getBoxNum());
								  allBox+=box ;
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(box);
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  if(itItem!=null){
								  boxNum=itItem.getBoxNum();
								  quantity+=itItem.getCheckQuantity();
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(itItem.getCheckQuantity());
								  Integer box=MathUtils.roundUp(itItem.getCheckQuantity()*1.0d/itItem.getBoxNum());
								  allBox+=box ;
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(box);
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  if(esItem!=null){
								  boxNum=esItem.getBoxNum();
								  quantity+=esItem.getCheckQuantity();
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(esItem.getCheckQuantity());
								  Integer box=MathUtils.roundUp(esItem.getCheckQuantity()*1.0d/esItem.getBoxNum());
								  allBox+=box ;
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(box);
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(allBox);
							  totalBox+=allBox;
							  PsiProduct product=psiProductService.findProductByProductName(productName);
							  float volume=quantity/(float)(boxNum*(Float.parseFloat(productMap.get(product.getId()).split(",")[0])));
							  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(volume);
							  totalVolume+=volume;
							  row.getCell(i-1).setCellStyle(cellStyle);
							  float weight=quantity/(float)(boxNum*(Float.parseFloat(productMap.get(product.getId()).split(",")[1])));
							  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(weight);
							  totalWeight+=weight;
							  row.getCell(i-1).setCellStyle(cellStyle);
						}
					}
				}
			}
		  row = sheet.createRow(excelNo++);
		  int i =0;
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("");
		  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(totalBox);
		  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(totalVolume);
		  row.getCell(i-1).setCellStyle(cellStyle);
		  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(totalWeight);
		  row.getCell(i-1).setCellStyle(cellStyle);
		  for (int j = 0; j <title.length; j++) {
	       		 sheet.autoSizeColumn((short)j, true);
			 }
		  
		  	request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			String fileName = "EUTransportData" + sdf.format(new Date()) + ".xls";
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
	
	@RequestMapping(value = {"exportAllCountry"})
	public String exportAllCountry(PsiTransportForecastOrder psiTransportForecastOrder,String name,String transModel,HttpServletRequest request, HttpServletResponse response) throws ParseException, UnsupportedEncodingException {
	
		Map<String,Map<String,Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>>>> map=psiTransportForecastOrderService.getAllCountry(psiTransportForecastOrder.getId(),name,transModel);
		
		int excelNo =1;
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCellStyle cellStyle= wb.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
	
		String[] title = {"运输类型","Model","产品","Sku","DE","UK","FR","IT","ES","CA","US","JP"};
		
		  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		  //设置Excel中的边框(表头的边框)
//		  //设置字体
		  HSSFFont font = wb.createFont();
		  font.setFontHeightInPoints((short) 11); // 字体高度
		  style.setFont(font);
		  row.setHeight((short) 400);
		  HSSFCell cell = null;		
		  for (int i = 0; i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
		  HSSFCellStyle  style1 = wb.createCellStyle();
		  HSSFFont font1 = wb.createFont();
		  font1.setColor(HSSFFont.COLOR_RED);
		  style1.setFont(font1);
		  

		  for(Map.Entry<String, Map<String, Map<String, Map<String, Map<String, PsiTransportForecastOrderItem>>>>> entry:map.entrySet()){
			    String type = entry.getKey();
				Map<String,Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>>> countryMap=entry.getValue();
				for(Map.Entry<String,Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>>> entry1:countryMap.entrySet()){
					String model = entry1.getKey();
					Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>> typeMap=entry1.getValue();
					for(Map.Entry<String,Map<String,Map<String,PsiTransportForecastOrderItem>>> entry2:typeMap.entrySet()){
						String productName = entry2.getKey();
						Map<String,Map<String,PsiTransportForecastOrderItem>> nameMap=entry2.getValue();
						for (Map.Entry<String,Map<String,PsiTransportForecastOrderItem>> entry3: nameMap.entrySet()) {
							String sku = entry3.getKey();
							Map<String,PsiTransportForecastOrderItem> skuMap=entry3.getValue();
							
							PsiTransportForecastOrderItem deItem=skuMap.get("de");
							PsiTransportForecastOrderItem ukItem=skuMap.get("uk");
							PsiTransportForecastOrderItem frItem=skuMap.get("fr");
							PsiTransportForecastOrderItem itItem=skuMap.get("it");
							PsiTransportForecastOrderItem esItem=skuMap.get("es");
							PsiTransportForecastOrderItem caItem=skuMap.get("ca");
							PsiTransportForecastOrderItem usItem=skuMap.get("com");
							PsiTransportForecastOrderItem jpItem=skuMap.get("jp");
							
							  row = sheet.createRow(excelNo++);
							  int i =0;
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("0".equals(type)?"本地运输":"FBA运输");
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("0".equals(model)?"空运":("1".equals(model)?"海运":"快递"));
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(productName);
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sku);
							 // row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getBoxNum());
							  if(deItem!=null){
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(deItem.getCheckQuantity());
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  if(ukItem!=null){
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(ukItem.getCheckQuantity());
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  if(frItem!=null){
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(frItem.getCheckQuantity());
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  if(itItem!=null){
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(itItem.getCheckQuantity());
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  if(esItem!=null){
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(esItem.getCheckQuantity());
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  if(caItem!=null){
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(caItem.getCheckQuantity());
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  if(usItem!=null){
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(usItem.getCheckQuantity());
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  if(jpItem!=null){
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(jpItem.getCheckQuantity());
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  
						}
					}
				}
			}
		    for (int i = 0; i <title.length; i++) {
	       		 sheet.autoSizeColumn((short)i, true);
			 }
		  	request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			String fileName = "EUTransportData" + sdf.format(new Date()) + ".xls";
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
	
	//2
	@RequestMapping(value = {"exportPackQuantity"})
	public String exportPackQuantity(PsiTransportForecastOrder psiTransportForecastOrder,HttpServletRequest request, HttpServletResponse response) throws ParseException, UnsupportedEncodingException {
	
		//国家-model-产品-数量
		Map<String,Map<String,Map<String,Object[]>>> map=psiTransportForecastOrderService.findPackQuantityByModelCountry(psiTransportForecastOrder.getId(),"0");
		Map<String,Integer> packQuantityMap=psiProductService.getPackQuantity();
		//Map<String, PsiInventoryTotalDtoByInStock> inventorys = psiInventoryService.getInventoryQuantity();
		Map<String,Integer> inventorys=psiInventoryService.findNewQuantityByCn(21);
		//带电源+KeyBoard产品
		List<String> keyBoardAndHasPowerList=saleReportService.findKeyBoardAndHasPowerAllProduct();
				
        HSSFWorkbook wb = new HSSFWorkbook();
		
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCellStyle cellStyle= wb.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
	
		  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		  //设置Excel中的边框(表头的边框)
//		  //设置字体
		  HSSFFont font = wb.createFont();
		  font.setFontHeightInPoints((short) 11); // 字体高度
		  style.setFont(font);
		  
		  HSSFCellStyle  style1 = wb.createCellStyle();
		  HSSFFont font1 = wb.createFont();
		  font1.setColor(HSSFFont.COLOR_RED);
		  style1.setFont(font1);
		  
		  HSSFCellStyle contentStyle = wb.createCellStyle();
		  contentStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
		  contentStyle.setFillBackgroundColor(HSSFColor.BLUE.index);
		  contentStyle.setFillForegroundColor(HSSFColor.BLUE.index);
		  
		  Map<String,Map<String,Object[]>> deMap=map.get("de");
		  Map<String,Map<String,Object[]>> ukMap=map.get("uk");
		  Map<String,Map<String,Object[]>> frMap=map.get("fr");
		  Map<String,Map<String,Object[]>> itMap=map.get("it");
		  Map<String,Map<String,Object[]>> esMap=map.get("es");
		  Set<String> modelSet=Sets.newHashSet();
		  if(deMap!=null){modelSet.addAll(deMap.keySet());}
		  if(ukMap!=null){modelSet.addAll(ukMap.keySet());}
		  if(frMap!=null){modelSet.addAll(frMap.keySet());}
		  if(itMap!=null){modelSet.addAll(itMap.keySet());}
		  if(esMap!=null){modelSet.addAll(esMap.keySet());}
		  
		  
		  for (String model : modelSet) {
			      Set<String> nameSet=Sets.newHashSet();
			      if(deMap!=null&&deMap.get(model)!=null){nameSet.addAll(deMap.get(model).keySet());}
				  if(ukMap!=null&&ukMap.get(model)!=null){nameSet.addAll(ukMap.get(model).keySet());}
				  if(frMap!=null&&frMap.get(model)!=null){nameSet.addAll(frMap.get(model).keySet());}
				  if(itMap!=null&&itMap.get(model)!=null){nameSet.addAll(itMap.get(model).keySet());}
				  if(esMap!=null&&esMap.get(model)!=null){nameSet.addAll(esMap.get(model).keySet());}
				  
				  
				  String[] title = {"Product","DE","DE箱数","UK","UK箱数","FR","FR箱数","IT","IT箱数","ES","ES箱数","备注"};
				  HSSFSheet sheet= wb.createSheet("欧洲"+("0".equals(model)?"空运":("1".equals(model)?"海运":"铁路")));
				  HSSFRow row = sheet.createRow(0);
				  row.setHeight((short) 400);
				  HSSFCell cell = null;		
				  for (int i = 0; i < title.length; i++) {
						cell = row.createCell(i);
						cell.setCellValue(title[i]);
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
				  }
				  Integer deQuantity=0;
				  Integer ukQuantity=0;
				  Integer frQuantity=0;
				  Integer itQuantity=0;
				  Integer esQuantity=0;
				  int excelNo =1;
				  for (String productName: nameSet) {
					  Integer pack=packQuantityMap.get(productName);
					  row = sheet.createRow(excelNo++);
					  int i =0;
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(productName);
					  boolean flag=true;
					  String remark="";
					  if(deMap!=null&&deMap.get(model)!=null&&deMap.get(model).get(productName)!=null){
						  String quantityStr=deMap.get(model).get(productName)[0]+"";
						  remark=deMap.get(model).get(productName)[1]+"";
						  if(!keyBoardAndHasPowerList.contains(productName)){//eu
							    Integer cnStock=0;
							    try {
									  cnStock+= (inventorys.get(productName+"_de")==null?0:inventorys.get(productName+"_de"));
								}catch (Exception e) {}
							    try {
							    	 cnStock+= (inventorys.get(productName+"_fr")==null?0:inventorys.get(productName+"_fr"));
								} catch (Exception e) {}
								try {
									 cnStock+= (inventorys.get(productName+"_uk")==null?0:inventorys.get(productName+"_uk"));
								} catch (Exception e) {}
								try {
									 cnStock+= (inventorys.get(productName+"_it")==null?0:inventorys.get(productName+"_it"));
								} catch (Exception e) {}
								try {
									 cnStock+= (inventorys.get(productName+"_es")==null?0:inventorys.get(productName+"_es"));
								} catch (Exception e) {}
								/*if(cnStock<(Integer)deMap.get(model).get(productName)[0]){
									 quantityStr+="(在仓"+cnStock+")";
									 flag=false;
								}*/
								if("0".equals(model)){//空运
									if(cnStock<(Integer)deMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+cnStock+")";
										flag=false;
									}
								}else{//海运
									if(deMap!=null&&deMap.get("0")!=null&&deMap.get("0").get(productName)!=null){
										if((cnStock-(Integer)deMap.get("0").get(productName)[0])<(Integer)deMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+((cnStock-(Integer)deMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)deMap.get("0").get(productName)[0]))+")";
											flag=false;
										}	 
									}else{
										if(cnStock<(Integer)deMap.get(model).get(productName)[0]){
											 quantityStr+="(在仓"+cnStock+")";
											flag=false;
										}
									}
								}
						  }else{
							  Integer cnStock=0;
							  try {
								  cnStock=(inventorys.get(productName+"_de")==null?0:inventorys.get(productName+"_de"));
							  }catch (Exception e) {}
							/*  if(cnStock<(Integer)deMap.get(model).get(productName)[0]){
								  quantityStr+="(在仓"+cnStock+")";
								  flag=false;
							  }*/
							  if("0".equals(model)){//空运
									if(cnStock<(Integer)deMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+cnStock+")";
										flag=false;
									}
								}else{//海运
									if(deMap!=null&&deMap.get("0")!=null&&deMap.get("0").get(productName)!=null){
										if((cnStock-(Integer)deMap.get("0").get(productName)[0])<(Integer)deMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+((cnStock-(Integer)deMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)deMap.get("0").get(productName)[0]))+")";
											flag=false;
										}	 
									}else{
										if(cnStock<(Integer)deMap.get(model).get(productName)[0]){
											 quantityStr+="(在仓"+cnStock+")";
											flag=false;
										}
									}
								}
						  }
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(quantityStr);
						  if(productName.contains("Inateck DB1001")){
								pack=44;
						  }else if(productName.contains("Inateck DB2001")){
								pack=24;
						  }
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp((Integer)deMap.get(model).get(productName)[0]*1.0d/pack));
						  deQuantity+=MathUtils.roundUp((Integer)deMap.get(model).get(productName)[0]*1.0d/pack);
					      row.getCell(i-1).setCellStyle(style1);
					  }else{
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  }
					  if(ukMap!=null&&ukMap.get(model)!=null&&ukMap.get(model).get(productName)!=null){
						  String quantityStr=ukMap.get(model).get(productName)[0]+"";
						  remark=ukMap.get(model).get(productName)[1]+"";
						  if(productName.contains("Inateck DB1001")){
								pack=60;
						  }else if(productName.contains("Inateck DB2001")){
								pack=24;
						  }
						  Integer cnStock=0;
						  try {
							  cnStock= (inventorys.get(productName+"_uk")==null?0:inventorys.get(productName+"_uk"));
						  }catch (Exception e) {}
						 /* if(cnStock<(Integer)ukMap.get(model).get(productName)[0]){
							  quantityStr+="(在仓"+cnStock+")";
							  flag=false;
						  }*/
						  if("0".equals(model)){//空运
								if(cnStock<(Integer)ukMap.get(model).get(productName)[0]){
									quantityStr+="(在仓"+cnStock+")";
									flag=false;
								}
							}else{//海运
								if(ukMap!=null&&ukMap.get("0")!=null&&ukMap.get("0").get(productName)!=null){
									if((cnStock-(Integer)ukMap.get("0").get(productName)[0])<(Integer)ukMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+((cnStock-(Integer)ukMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)ukMap.get("0").get(productName)[0]))+")";
										flag=false;
									}	 
								}else{
									if(cnStock<(Integer)ukMap.get(model).get(productName)[0]){
										 quantityStr+="(在仓"+cnStock+")";
										flag=false;
									}
								}
							}
						  
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(quantityStr);
						  
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp((Integer)ukMap.get(model).get(productName)[0]*1.0d/pack));
						  ukQuantity+=MathUtils.roundUp((Integer)ukMap.get(model).get(productName)[0]*1.0d/pack);
						  row.getCell(i-1).setCellStyle(style1);
					  }else{
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  }
					  if(frMap!=null&&frMap.get(model)!=null&&frMap.get(model).get(productName)!=null){
						  String quantityStr=frMap.get(model).get(productName)[0]+"";
						  remark=frMap.get(model).get(productName)[1]+"";
						  if(productName.contains("Inateck DB1001")){
								pack=44;
						  }else if(productName.contains("Inateck DB2001")){
								pack=24;
						  }
						  frQuantity+=MathUtils.roundUp((Integer)frMap.get(model).get(productName)[0]*1.0d/pack);
						  Integer cnStock=0;
						  try {
							  cnStock= (inventorys.get(productName+"_fr")==null?0:inventorys.get(productName+"_fr"));
						  }catch (Exception e) {}
						  
						  /*if(cnStock<(Integer)frMap.get(model).get(productName)[0]){
							  quantityStr+="(在仓"+cnStock+")";
							  flag=false;
						  }*/
						  if("0".equals(model)){//空运
								if(cnStock<(Integer)frMap.get(model).get(productName)[0]){
									quantityStr+="(在仓"+cnStock+")";
									flag=false;
								}
							}else{//海运
								if(frMap!=null&&frMap.get("0")!=null&&frMap.get("0").get(productName)!=null){
									if((cnStock-(Integer)frMap.get("0").get(productName)[0])<(Integer)frMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+((cnStock-(Integer)frMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)frMap.get("0").get(productName)[0]))+")";
										flag=false;
									}	 
								}else{
									if(cnStock<(Integer)frMap.get(model).get(productName)[0]){
										 quantityStr+="(在仓"+cnStock+")";
										flag=false;
									}
								}
							}
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(quantityStr);
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp((Integer)frMap.get(model).get(productName)[0]*1.0d/pack));
						  row.getCell(i-1).setCellStyle(style1);
					  }else{
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  }
					  if(itMap!=null&&itMap.get(model)!=null&&itMap.get(model).get(productName)!=null){
						  String quantityStr=itMap.get(model).get(productName)[0]+"";
						  remark=itMap.get(model).get(productName)[1]+"";
						  Integer cnStock=0;
						  try {
							  cnStock= (inventorys.get(productName+"_it")==null?0:inventorys.get(productName+"_it"));
						  }catch (Exception e) {}
						 
						 /* if(cnStock<(Integer)itMap.get(model).get(productName)[0]){
							  quantityStr+="(在仓"+cnStock+")";
							  flag=false;
						  }*/
						  if("0".equals(model)){//空运
								if(cnStock<(Integer)itMap.get(model).get(productName)[0]){
									quantityStr+="(在仓"+cnStock+")";
									flag=false;
								}
							}else{//海运
								if(itMap!=null&&itMap.get("0")!=null&&itMap.get("0").get(productName)!=null){
									if((cnStock-(Integer)itMap.get("0").get(productName)[0])<(Integer)itMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+((cnStock-(Integer)itMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)itMap.get("0").get(productName)[0]))+")";
										flag=false;
									}	 
								}else{
									if(cnStock<(Integer)itMap.get(model).get(productName)[0]){
										 quantityStr+="(在仓"+cnStock+")";
										flag=false;
									}
								}
							}
						  if(productName.contains("Inateck DB1001")){
								pack=44;
						  }else if(productName.contains("Inateck DB2001")){
								pack=24;
						  }
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(quantityStr);
						  itQuantity+=MathUtils.roundUp((Integer)itMap.get(model).get(productName)[0]*1.0d/pack);
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp((Integer)itMap.get(model).get(productName)[0]*1.0d/pack));
						  row.getCell(i-1).setCellStyle(style1);
					  }else{
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  }
					  if(esMap!=null&&esMap.get(model)!=null&&esMap.get(model).get(productName)!=null){
						  String quantityStr=esMap.get(model).get(productName)[0]+"";
						  remark=esMap.get(model).get(productName)[1]+"";
						  Integer cnStock=0;
						  try {
							  cnStock=(inventorys.get(productName+"_es")==null?0:inventorys.get(productName+"_es"));
						  }catch (Exception e) {}
						 
						/*  if(cnStock<(Integer)esMap.get(model).get(productName)[0]){
							  quantityStr+="(在仓"+cnStock+")";
							  flag=false;
						  }*/
						  if("0".equals(model)){//空运
								if(cnStock<(Integer)esMap.get(model).get(productName)[0]){
									quantityStr+="(在仓"+cnStock+")";
									flag=false;
								}
							}else{//海运
								if(esMap!=null&&esMap.get("0")!=null&&esMap.get("0").get(productName)!=null){
									if((cnStock-(Integer)esMap.get("0").get(productName)[0])<(Integer)esMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+((cnStock-(Integer)esMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)esMap.get("0").get(productName)[0]))+")";
										flag=false;
									}	 
								}else{
									if(cnStock<(Integer)esMap.get(model).get(productName)[0]){
										 quantityStr+="(在仓"+cnStock+")";
										flag=false;
									}
								}
							}
						  if(productName.contains("Inateck DB1001")){
								pack=44;
						  }else if(productName.contains("Inateck DB2001")){
								pack=24;
						  }
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(quantityStr);
						  esQuantity+=MathUtils.roundUp((Integer)esMap.get(model).get(productName)[0]*1.0d/pack);
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp((Integer)esMap.get(model).get(productName)[0]*1.0d/pack));
						  row.getCell(i-1).setCellStyle(style1);
					  }else{
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  }
					  if(flag){
						  row.getCell(0).setCellStyle(contentStyle);
					  }
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(remark);
				  }
				  row = sheet.createRow(excelNo++);
				  int i =0;
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("总计");
				  row.getCell(i-1).setCellStyle(style1);
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  if(deQuantity>0){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(deQuantity);
					  row.getCell(i-1).setCellStyle(style1);
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  if(ukQuantity>0){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(ukQuantity);
					  row.getCell(i-1).setCellStyle(style1);
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  if(frQuantity>0){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(frQuantity);
					  row.getCell(i-1).setCellStyle(style1);
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  if(itQuantity>0){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(itQuantity);
					  row.getCell(i-1).setCellStyle(style1);
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  if(esQuantity>0){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(esQuantity);
					  row.getCell(i-1).setCellStyle(style1);
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  
				  for (int j = 0; j <title.length; j++) {
			       		 sheet.autoSizeColumn((short)j, true);
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  }
		  
		  List<String> countryList=Lists.newArrayList("ca","com","jp");
		  for (String country : countryList) {
			  Map<String,Map<String,Object[]>> countryMap=map.get(country);
			  if(countryMap!=null&&countryMap.size()>0){
				  for (Map.Entry<String,Map<String,Object[]>> entry : countryMap.entrySet()) {
					  String model = entry.getKey();
					  Integer quantity=0;
					  String[] title = {"Product","com".equals(country)?"US":country.toUpperCase(),"com".equals(country)?"US":country.toUpperCase()+"箱数"};
					  HSSFSheet sheet= wb.createSheet(("com".equals(country)?"US":country.toUpperCase())+("0".equals(model)?"空运":("1".equals(model)?"海运":"铁路")));
					  HSSFRow row = sheet.createRow(0);
					  row.setHeight((short) 400);
					  HSSFCell cell = null;		
					  for (int i = 0; i < title.length; i++) {
							cell = row.createCell(i);
							cell.setCellValue(title[i]);
							cell.setCellStyle(style);
							sheet.autoSizeColumn((short) i);
					  }
					  int excelNo =1;
					 Map<String,Object[]> modelMap=entry.getValue();
					 for (Map.Entry<String,Object[]> entry1: modelMap.entrySet()) {
						 String productName = entry1.getKey();
						 Integer cnStock=0;
						  try {
							  cnStock= (inventorys.get(productName+"_"+country)==null?0:inventorys.get(productName+"_"+country));
						  }catch (Exception e) {}
						  Integer pack=packQuantityMap.get(productName);
						    if(productName.contains("Inateck DB1001")){
								if("com,uk,jp,ca,mx,".contains(country)){
									pack=60;
								}else{
									pack=44;
								}
							}else if(productName.contains("Inateck DB2001")){
								if("com,jp,ca,mx,".contains(country)){
									pack=32;
								}else{
									pack=24;
								}
							}
						    Integer checkQuantity=0;
						    String remark="";
						    if(modelMap!=null&&entry1.getValue()!=null){
						    	checkQuantity=(Integer)modelMap.get(productName)[0];
						    	remark=entry1.getValue()[1]+"";
								quantity+=MathUtils.roundUp((Integer)entry1.getValue()[0]*1.0d/pack);
							  }
						      row = sheet.createRow(excelNo++);
							  int i =0;
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(productName);
							  String quantityStr=checkQuantity+"";
								 
							  
							  if("0".equals(model)){//空运
								  if(cnStock>=checkQuantity){
									  row.getCell(0).setCellStyle(contentStyle);
								  }else{
									 quantityStr+="(在仓"+cnStock+")"; 
								  }
							  }else{//海运
								    if(countryMap!=null&&countryMap.get("0")!=null&&countryMap.get("0").get(productName)!=null){
										if((cnStock-(Integer)countryMap.get("0").get(productName)[0])>=(Integer)modelMap.get(productName)[0]){
											row.getCell(0).setCellStyle(contentStyle);
										}else{
											quantityStr+="(在仓"+((cnStock-(Integer)countryMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)countryMap.get("0").get(productName)[0]))+")";
										}
									}else{
										  if(cnStock>=checkQuantity){
											  row.getCell(0).setCellStyle(contentStyle);
										  }else{
											 quantityStr+="(在仓"+cnStock+")"; 
										  }
									}
							  }
							  if(checkQuantity>0){
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(quantityStr);
								  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(checkQuantity*1.0d/pack));
								  row.getCell(i-1).setCellStyle(style1);
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(remark);
					 }
					 row = sheet.createRow(excelNo++);
					  int i =0;
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("总计");
					  row.getCell(i-1).setCellStyle(style1);
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  if(quantity>0){
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(quantity);
						  row.getCell(i-1).setCellStyle(style1);
					  }else{
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  }
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  
					 for (int j = 0; j <title.length; j++) {
			       		 sheet.autoSizeColumn((short)j, true);
				     }
				  }
			  }
		  }
		  
		  
		  	request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			String fileName = "春雨物流分配" + sdf.format(new Date()) ;
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName+ ".xls");
			try {
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
				return null;
		}
	
	
	@RequestMapping(value = {"exportPackQuantity2"})
	public String exportPackQuantity2(String createDate,PsiTransportForecastOrder psiTransportForecastOrder,HttpServletRequest request, HttpServletResponse response) throws ParseException, UnsupportedEncodingException {
	
		//国家-model-产品-数量
		Map<String,Map<String,Map<String,Object[]>>> map=psiTransportForecastOrderService.findPackQuantityByModelCountry(psiTransportForecastOrder.getId(),"1");
		Map<String,Integer> packQuantityMap=psiProductService.getPackQuantity();
		//Map<String, PsiInventoryTotalDtoByInStock> inventorys = psiInventoryService.getInventoryQuantity();
		Map<String,Integer> inventorys=psiInventoryService.findNewQuantityByCn(130);
		//带电源+KeyBoard产品
		List<String> keyBoardAndHasPowerList=saleReportService.findKeyBoardAndHasPowerAllProduct();
		Map<String,Map<String,Map<String,Integer>>> newMap=psiTransportForecastOrderService.getNewProduct2(createDate);
	    HSSFWorkbook wb = new HSSFWorkbook();
		
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCellStyle cellStyle= wb.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
	
		  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		  //设置Excel中的边框(表头的边框)
//		  //设置字体
		  HSSFFont font = wb.createFont();
		  font.setFontHeightInPoints((short) 11); // 字体高度
		  style.setFont(font);
		  
		  HSSFCellStyle  style1 = wb.createCellStyle();
		  HSSFFont font1 = wb.createFont();
		  font1.setColor(HSSFFont.COLOR_RED);
		  style1.setFont(font1);
		  
		  HSSFCellStyle contentStyle = wb.createCellStyle();
		  contentStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);   
		  contentStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
		  contentStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		  
		  Map<String,Map<String,Object[]>> deMap=map.get("de");
		  Map<String,Map<String,Object[]>> ukMap=map.get("uk");
		  Map<String,Map<String,Object[]>> frMap=map.get("fr");
		  Map<String,Map<String,Object[]>> itMap=map.get("it");
		  Map<String,Map<String,Object[]>> esMap=map.get("es");
		  Set<String> modelSet=Sets.newHashSet();
		  if(deMap!=null){modelSet.addAll(deMap.keySet());}
		  if(ukMap!=null){modelSet.addAll(ukMap.keySet());}
		  if(frMap!=null){modelSet.addAll(frMap.keySet());}
		  if(itMap!=null){modelSet.addAll(itMap.keySet());}
		  if(esMap!=null){modelSet.addAll(esMap.keySet());}
		  
		  
		  for (String model : modelSet) {
			      Set<String> nameSet=Sets.newHashSet();
			      if(deMap!=null&&deMap.get(model)!=null){nameSet.addAll(deMap.get(model).keySet());}
				  if(ukMap!=null&&ukMap.get(model)!=null){nameSet.addAll(ukMap.get(model).keySet());}
				  if(frMap!=null&&frMap.get(model)!=null){nameSet.addAll(frMap.get(model).keySet());}
				  if(itMap!=null&&itMap.get(model)!=null){nameSet.addAll(itMap.get(model).keySet());}
				  if(esMap!=null&&esMap.get(model)!=null){nameSet.addAll(esMap.get(model).keySet());}
				  
				  
				  String[] title = {"Product","DE","DE审核数","DE装箱数","DE箱数","UK","UK审核数","UK装箱数","UK箱数","FR","FR审核数","FR装箱数","FR箱数","IT","IT审核数","IT装箱数","IT箱数","ES","ES审核数","ES装箱数","ES箱数","备注"};
				  HSSFSheet sheet= wb.createSheet("欧洲"+("0".equals(model)?"空运":("1".equals(model)?"海运":"铁路")));
				  HSSFRow row = sheet.createRow(0);
				  row.setHeight((short) 400);
				  HSSFCell cell = null;		
				  for (int i = 0; i < title.length; i++) {
						cell = row.createCell(i);
						cell.setCellValue(title[i]);
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
				  }
				  Integer deQuantity=0;
				  Integer ukQuantity=0;
				  Integer frQuantity=0;
				  Integer itQuantity=0;
				  Integer esQuantity=0;
				  int excelNo =1;
				  for (String productName: nameSet) {
					  Integer pack=packQuantityMap.get(productName);
					  row = sheet.createRow(excelNo++);
					  int i =0;
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(productName);
					  boolean flag=true;
					  String remark="";
					  if(deMap!=null&&deMap.get(model)!=null&&deMap.get(model).get(productName)!=null){
						  String quantityStr=deMap.get(model).get(productName)[0]+"";
						  remark=deMap.get(model).get(productName)[1]+"";
						  if(!keyBoardAndHasPowerList.contains(productName)){//eu
							    Integer cnStock=0;
							    try {
									  cnStock+= (inventorys.get(productName+"_de")==null?0:inventorys.get(productName+"_de"));
								}catch (Exception e) {}
							    try {
							    	 cnStock+= (inventorys.get(productName+"_fr")==null?0:inventorys.get(productName+"_fr"));
								} catch (Exception e) {}
								try {
									 cnStock+= (inventorys.get(productName+"_uk")==null?0:inventorys.get(productName+"_uk"));
								} catch (Exception e) {}
								try {
									 cnStock+= (inventorys.get(productName+"_it")==null?0:inventorys.get(productName+"_it"));
								} catch (Exception e) {}
								try {
									 cnStock+= (inventorys.get(productName+"_es")==null?0:inventorys.get(productName+"_es"));
								} catch (Exception e) {}
								if(newMap!=null&&newMap.get(productName+"_de")!=null&&newMap.get(productName+"_de").get("total")!=null&&newMap.get(productName+"_de").get("total").get("total")!=null){
									cnStock=cnStock-newMap.get(productName+"_de").get("total").get("total");
								}
								if(newMap!=null&&newMap.get(productName+"_fr")!=null&&newMap.get(productName+"_fr").get("total")!=null&&newMap.get(productName+"_fr").get("total").get("total")!=null){
									cnStock=cnStock-newMap.get(productName+"_fr").get("total").get("total");
								}
								if(newMap!=null&&newMap.get(productName+"_it")!=null&&newMap.get(productName+"_it").get("total")!=null&&newMap.get(productName+"_it").get("total").get("total")!=null){
									cnStock=cnStock-newMap.get(productName+"_it").get("total").get("total");
								}
								if(newMap!=null&&newMap.get(productName+"_es")!=null&&newMap.get(productName+"_es").get("total")!=null&&newMap.get(productName+"_es").get("total").get("total")!=null){
									cnStock=cnStock-newMap.get(productName+"_es").get("total").get("total");
								}
								if(newMap!=null&&newMap.get(productName+"_uk")!=null&&newMap.get(productName+"_uk").get("total")!=null&&newMap.get(productName+"_uk").get("total").get("total")!=null){
									cnStock=cnStock-newMap.get(productName+"_uk").get("total").get("total");
								}
								if("0".equals(model)){//空运
									if(cnStock<(Integer)deMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+(cnStock<0?0:cnStock)+")";
										flag=false;
									}
								}else{//海运
									if("3".equals(model)){//铁路
										if(deMap!=null&&deMap.get("0")!=null&&deMap.get("0").get(productName)!=null){
											if((cnStock-(Integer)deMap.get("0").get(productName)[0])<(Integer)deMap.get(model).get(productName)[0]){
												quantityStr+="(在仓"+((cnStock-(Integer)deMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)deMap.get("0").get(productName)[0]))+")";
												flag=false;
											}	 
										}else{
											if(cnStock<(Integer)deMap.get(model).get(productName)[0]){
												 quantityStr+="(在仓"+(cnStock<0?0:cnStock)+")";
												flag=false;
											}
										}
									}else{
										Integer allQuantity=0;
										if(deMap!=null&&deMap.get("0")!=null&&deMap.get("0").get(productName)!=null){
											allQuantity+=(Integer)deMap.get("0").get(productName)[0];
										}
										if(deMap!=null&&deMap.get("3")!=null&&deMap.get("3").get(productName)!=null){
											allQuantity+=(Integer)deMap.get("3").get(productName)[0];
										}
										Integer markQuantity=cnStock-allQuantity;
										if(markQuantity<(Integer)deMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+(markQuantity<0?0:markQuantity)+")";
											flag=false;
										}
									}
								}
								
						  }else{
							  Integer cnStock=0;
							  try {
								  cnStock=(inventorys.get(productName+"_de")==null?0:inventorys.get(productName+"_de"));
							  }catch (Exception e) {}
							  if(newMap!=null&&newMap.get(productName+"_de")!=null&&newMap.get(productName+"_de").get("total")!=null&&newMap.get(productName+"_de").get("total").get("total")!=null){
									cnStock=cnStock-newMap.get(productName+"_de").get("total").get("total");
							  }
							 /* if(cnStock<(Integer)deMap.get(model).get(productName)[0]){
								  quantityStr+="(在仓"+cnStock+")";
								  flag=false;
							  }*/
							    if("0".equals(model)){//空运
									if(cnStock<(Integer)deMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+(cnStock<0?0:cnStock)+")";
										flag=false;
									}
								}else{//海运
									/*if(deMap!=null&&deMap.get("0")!=null&&deMap.get("0").get(productName)!=null){
										if((cnStock-(Integer)deMap.get("0").get(productName)[0])<(Integer)deMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+((cnStock-(Integer)deMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)deMap.get("0").get(productName)[0]))+")";
											flag=false;
										}	 
									}else{
										if(cnStock<(Integer)deMap.get(model).get(productName)[0]){
											 quantityStr+="(在仓"+cnStock+")";
											flag=false;
										}
									}*/
									
									if("3".equals(model)){//铁路
										if(deMap!=null&&deMap.get("0")!=null&&deMap.get("0").get(productName)!=null){
											if((cnStock-(Integer)deMap.get("0").get(productName)[0])<(Integer)deMap.get(model).get(productName)[0]){
												quantityStr+="(在仓"+((cnStock-(Integer)deMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)deMap.get("0").get(productName)[0]))+")";
												flag=false;
											}	 
										}else{
											if(cnStock<(Integer)deMap.get(model).get(productName)[0]){
												quantityStr+="(在仓"+(cnStock<0?0:cnStock)+")";
												flag=false;
											}
										}
									}else{
										Integer allQuantity=0;
										if(deMap!=null&&deMap.get("0")!=null&&deMap.get("0").get(productName)!=null){
											allQuantity+=(Integer)deMap.get("0").get(productName)[0];
										}
										if(deMap!=null&&deMap.get("3")!=null&&deMap.get("3").get(productName)!=null){
											allQuantity+=(Integer)deMap.get("3").get(productName)[0];
										}
										Integer markQuantity=cnStock-allQuantity;
										if(markQuantity<(Integer)deMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+(markQuantity<0?0:markQuantity)+")";
											flag=false;
										}
									}
								}
						  }
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(quantityStr);
						  if(productName.contains("Inateck DB1001")){
								pack=44;
						  }else if(productName.contains("Inateck DB2001")){
								pack=24;
						  }
						  
						 // row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp((Integer)deMap.get(model).get(productName)[0]*1.0d/pack));
						  deQuantity+=MathUtils.roundUp((Integer)deMap.get(model).get(productName)[0]*1.0d/pack);
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((Integer)deMap.get(model).get(productName)[0]);
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(pack);
						 // row.createCell(i++).setCellFormula("roundup(C"+(excelNo)+"/D"+(excelNo)+",0)");  IF(MOD(C2,D2)>0,ROUND(C2/D2,1),C2/D2)
						  row.createCell(i++).setCellFormula("IF(MOD(C"+(excelNo)+",D"+(excelNo)+")>0,ROUND(C"+(excelNo)+"/D"+(excelNo)+",1),C"+(excelNo)+"/D"+(excelNo)+")");
					      row.getCell(i-1).setCellStyle(style1);
					  }else{
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  }
					  if(ukMap!=null&&ukMap.get(model)!=null&&ukMap.get(model).get(productName)!=null){
						  String quantityStr=ukMap.get(model).get(productName)[0]+"";
						  remark+=ukMap.get(model).get(productName)[1]+"";
						  if(productName.contains("Inateck DB1001")){
								pack=60;
						  }else if(productName.contains("Inateck DB2001")){
								pack=24;
						  }
						  Integer cnStock=0;
						  try {
							  cnStock= (inventorys.get(productName+"_uk")==null?0:inventorys.get(productName+"_uk"));
						  }catch (Exception e) {}
						 /* if(cnStock<(Integer)ukMap.get(model).get(productName)[0]){
							  quantityStr+="(在仓"+cnStock+")";
							  flag=false;
						  }*/
						  if(newMap!=null&&newMap.get(productName+"_uk")!=null&&newMap.get(productName+"_uk").get("total")!=null&&newMap.get(productName+"_uk").get("total").get("total")!=null){
								cnStock=cnStock-newMap.get(productName+"_uk").get("total").get("total");
						  }
						  if("0".equals(model)){//空运
								if(cnStock<(Integer)ukMap.get(model).get(productName)[0]){
									quantityStr+="(在仓"+(cnStock<0?0:cnStock)+")";
									flag=false;
								}
							}else{//海运
								/*if(ukMap!=null&&ukMap.get("0")!=null&&ukMap.get("0").get(productName)!=null){
									if((cnStock-(Integer)ukMap.get("0").get(productName)[0])<(Integer)ukMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+((cnStock-(Integer)ukMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)ukMap.get("0").get(productName)[0]))+")";
										flag=false;
									}	 
								}else{
									if(cnStock<(Integer)ukMap.get(model).get(productName)[0]){
										 quantityStr+="(在仓"+cnStock+")";
										flag=false;
									}
								}*/
								
								
								if("3".equals(model)){//铁路
									if(ukMap!=null&&ukMap.get("0")!=null&&ukMap.get("0").get(productName)!=null){
										if((cnStock-(Integer)ukMap.get("0").get(productName)[0])<(Integer)ukMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+((cnStock-(Integer)ukMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)ukMap.get("0").get(productName)[0]))+")";
											flag=false;
										}	 
									}else{
										if(cnStock<(Integer)ukMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+(cnStock<0?0:cnStock)+")";
											flag=false;
										}
									}
								}else{
									Integer allQuantity=0;
									if(ukMap!=null&&ukMap.get("0")!=null&&ukMap.get("0").get(productName)!=null){
										allQuantity+=(Integer)ukMap.get("0").get(productName)[0];
									}
									if(ukMap!=null&&ukMap.get("3")!=null&&ukMap.get("3").get(productName)!=null){
										allQuantity+=(Integer)ukMap.get("3").get(productName)[0];
									}
									Integer markQuantity=cnStock-allQuantity;
									if(markQuantity<(Integer)ukMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+(markQuantity<0?0:markQuantity)+")";
										flag=false;
									}
								}
							}
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(quantityStr);
						  
						 // row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp((Integer)ukMap.get(model).get(productName)[0]*1.0d/pack));
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((Integer)ukMap.get(model).get(productName)[0]);
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(pack);
						//  row.createCell(i++).setCellFormula("roundup(G"+(excelNo)+"/H"+(excelNo)+",0)"); IF(MOD(C2,D2)>0,ROUND(C2/D2,1),C2/D2)
						  row.createCell(i++).setCellFormula("IF(MOD(G"+(excelNo)+",H"+(excelNo)+")>0,ROUND(G"+(excelNo)+"/H"+(excelNo)+",1),G"+(excelNo)+"/H"+(excelNo)+")");
						  ukQuantity+=MathUtils.roundUp((Integer)ukMap.get(model).get(productName)[0]*1.0d/pack);
						  row.getCell(i-1).setCellStyle(style1);
					  }else{
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  }
					  if(frMap!=null&&frMap.get(model)!=null&&frMap.get(model).get(productName)!=null){
						  String quantityStr=frMap.get(model).get(productName)[0]+"";
						  remark+=frMap.get(model).get(productName)[1]+"";
						  if(productName.contains("Inateck DB1001")){
								pack=44;
						  }else if(productName.contains("Inateck DB2001")){
								pack=24;
						  }
						  frQuantity+=MathUtils.roundUp((Integer)frMap.get(model).get(productName)[0]*1.0d/pack);
						  Integer cnStock=0;
						  try {
							  cnStock= (inventorys.get(productName+"_fr")==null?0:inventorys.get(productName+"_fr"));
						  }catch (Exception e) {}
						  if(newMap!=null&&newMap.get(productName+"_fr")!=null&&newMap.get(productName+"_fr").get("total")!=null&&newMap.get(productName+"_fr").get("total").get("total")!=null){
								cnStock=cnStock-newMap.get(productName+"_fr").get("total").get("total");
							}
						/*  if(cnStock<(Integer)frMap.get(model).get(productName)[0]){
							  quantityStr+="(在仓"+cnStock+")";
							  flag=false;
						  }*/
						  if("0".equals(model)){//空运
								if(cnStock<(Integer)frMap.get(model).get(productName)[0]){
									quantityStr+="(在仓"+(cnStock<0?0:cnStock)+")";
									flag=false;
								}
							}else{//海运
								if("3".equals(model)){//铁路
									if(frMap!=null&&frMap.get("0")!=null&&frMap.get("0").get(productName)!=null){
										if((cnStock-(Integer)frMap.get("0").get(productName)[0])<(Integer)frMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+((cnStock-(Integer)frMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)frMap.get("0").get(productName)[0]))+")";
											flag=false;
										}	 
									}else{
										if(cnStock<(Integer)frMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+(cnStock<0?0:cnStock)+")";
											flag=false;
										}
									}
								}else{
									Integer allQuantity=0;
									if(frMap!=null&&frMap.get("0")!=null&&frMap.get("0").get(productName)!=null){
										allQuantity+=(Integer)frMap.get("0").get(productName)[0];
									}
									if(frMap!=null&&frMap.get("3")!=null&&frMap.get("3").get(productName)!=null){
										allQuantity+=(Integer)frMap.get("3").get(productName)[0];
									}
									Integer markQuantity=cnStock-allQuantity;
									if(markQuantity<(Integer)frMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+(markQuantity<0?0:markQuantity)+")";
										flag=false;
									}
								}
							}
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(quantityStr);
						//  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp((Integer)frMap.get(model).get(productName)[0]*1.0d/pack));
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((Integer)frMap.get(model).get(productName)[0]);
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(pack);
						  //row.createCell(i++).setCellFormula("roundup(K"+(excelNo)+"/L"+(excelNo)+",0)"); 
						  row.createCell(i++).setCellFormula("IF(MOD(K"+(excelNo)+",L"+(excelNo)+")>0,ROUND(K"+(excelNo)+"/L"+(excelNo)+",1),K"+(excelNo)+"/L"+(excelNo)+")");
						  row.getCell(i-1).setCellStyle(style1);
					  }else{
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  }
					  if(itMap!=null&&itMap.get(model)!=null&&itMap.get(model).get(productName)!=null){
						  String quantityStr=itMap.get(model).get(productName)[0]+"";
						  remark+=itMap.get(model).get(productName)[1]+"";
						  Integer cnStock=0;
						  try {
							  cnStock= (inventorys.get(productName+"_it")==null?0:inventorys.get(productName+"_it"));
						  }catch (Exception e) {}
						  if(newMap!=null&&newMap.get(productName+"_it")!=null&&newMap.get(productName+"_it").get("total")!=null&&newMap.get(productName+"_it").get("total").get("total")!=null){
								cnStock=cnStock-newMap.get(productName+"_it").get("total").get("total");
						  }
						 /* if(cnStock<(Integer)itMap.get(model).get(productName)[0]){
							  quantityStr+="(在仓"+cnStock+")";
							  flag=false;
						  }*/
						  if("0".equals(model)){//空运
								if(cnStock<(Integer)itMap.get(model).get(productName)[0]){
									quantityStr+="(在仓"+(cnStock<0?0:cnStock)+")";
									flag=false;
								}
							}else{//海运
								if("3".equals(model)){//铁路
									if(itMap!=null&&itMap.get("0")!=null&&itMap.get("0").get(productName)!=null){
										if((cnStock-(Integer)itMap.get("0").get(productName)[0])<(Integer)itMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+((cnStock-(Integer)itMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)itMap.get("0").get(productName)[0]))+")";
											flag=false;
										}	 
									}else{
										if(cnStock<(Integer)itMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+(cnStock<0?0:cnStock)+")";
											flag=false;
										}
									}
								}else{
									Integer allQuantity=0;
									if(itMap!=null&&itMap.get("0")!=null&&itMap.get("0").get(productName)!=null){
										allQuantity+=(Integer)itMap.get("0").get(productName)[0];
									}
									if(itMap!=null&&itMap.get("3")!=null&&itMap.get("3").get(productName)!=null){
										allQuantity+=(Integer)itMap.get("3").get(productName)[0];
									}
									Integer markQuantity=cnStock-allQuantity;
									if(markQuantity<(Integer)itMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+(markQuantity<0?0:markQuantity)+")";
										flag=false;
									}
								}
							}
						  if(productName.contains("Inateck DB1001")){
								pack=44;
						  }else if(productName.contains("Inateck DB2001")){
								pack=24;
						  }
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(quantityStr);
						  itQuantity+=MathUtils.roundUp((Integer)itMap.get(model).get(productName)[0]*1.0d/pack);
						 // row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp((Integer)itMap.get(model).get(productName)[0]*1.0d/pack));
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((Integer)itMap.get(model).get(productName)[0]);
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(pack);
						 // row.createCell(i++).setCellFormula("roundup(O"+(excelNo)+"/P"+(excelNo)+",0)"); 
						  row.createCell(i++).setCellFormula("IF(MOD(O"+(excelNo)+",P"+(excelNo)+")>0,ROUND(O"+(excelNo)+"/P"+(excelNo)+",1),O"+(excelNo)+"/P"+(excelNo)+")");
						  row.getCell(i-1).setCellStyle(style1);
					  }else{
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  }
					  if(esMap!=null&&esMap.get(model)!=null&&esMap.get(model).get(productName)!=null){
						  String quantityStr=esMap.get(model).get(productName)[0]+"";
						  remark+=esMap.get(model).get(productName)[1]+"";
						  Integer cnStock=0;
						  try {
							  cnStock=(inventorys.get(productName+"_es")==null?0:inventorys.get(productName+"_es"));
						  }catch (Exception e) {}
						  if(newMap!=null&&newMap.get(productName+"_es")!=null&&newMap.get(productName+"_es").get("total")!=null&&newMap.get(productName+"_es").get("total").get("total")!=null){
								cnStock=cnStock-newMap.get(productName+"_es").get("total").get("total");
							}
						  /*if(cnStock<(Integer)esMap.get(model).get(productName)[0]){
							  quantityStr+="(在仓"+cnStock+")";
							  flag=false;
						  }*/
						  if("0".equals(model)){//空运
								if(cnStock<(Integer)esMap.get(model).get(productName)[0]){
									quantityStr+="(在仓"+(cnStock<0?0:cnStock)+")";
									flag=false;
								}
							}else{//海运
								if("3".equals(model)){//铁路
									if(esMap!=null&&esMap.get("0")!=null&&esMap.get("0").get(productName)!=null){
										if((cnStock-(Integer)esMap.get("0").get(productName)[0])<(Integer)esMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+((cnStock-(Integer)esMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)esMap.get("0").get(productName)[0]))+")";
											flag=false;
										}	 
									}else{
										if(cnStock<(Integer)esMap.get(model).get(productName)[0]){
											quantityStr+="(在仓"+(cnStock<0?0:cnStock)+")";
											flag=false;
										}
									}
								}else{
									Integer allQuantity=0;
									if(esMap!=null&&esMap.get("0")!=null&&esMap.get("0").get(productName)!=null){
										allQuantity+=(Integer)esMap.get("0").get(productName)[0];
									}
									if(esMap!=null&&esMap.get("3")!=null&&esMap.get("3").get(productName)!=null){
										allQuantity+=(Integer)esMap.get("3").get(productName)[0];
									}
									Integer markQuantity=cnStock-allQuantity;
									if(markQuantity<(Integer)esMap.get(model).get(productName)[0]){
										quantityStr+="(在仓"+(markQuantity<0?0:markQuantity)+")";
										flag=false;
									}
								}
							}
						  if(productName.contains("Inateck DB1001")){
								pack=44;
						  }else if(productName.contains("Inateck DB2001")){
								pack=24;
						  }
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(quantityStr);
						  esQuantity+=MathUtils.roundUp((Integer)esMap.get(model).get(productName)[0]*1.0d/pack);
						  //row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp((Integer)esMap.get(model).get(productName)[0]*1.0d/pack));
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((Integer)esMap.get(model).get(productName)[0]);
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(pack);
						  //row.createCell(i++).setCellFormula("roundup(S"+(excelNo)+"/T"+(excelNo)+",0)"); 
						  row.createCell(i++).setCellFormula("IF(MOD(S"+(excelNo)+",T"+(excelNo)+")>0,ROUND(S"+(excelNo)+"/T"+(excelNo)+",1),S"+(excelNo)+"/T"+(excelNo)+")");
						  row.getCell(i-1).setCellStyle(style1);
					  }else{
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  }
					  if(flag){
						  row.getCell(0).setCellStyle(contentStyle);
					  }
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(remark);
				  }
				  row = sheet.createRow(excelNo++);
				  int i =0;
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("总计");
				  row.getCell(i-1).setCellStyle(style1);
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  if(deQuantity>0){
					  //row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(deQuantity);
					  row.createCell(i++).setCellFormula("SUM(E2:E"+(excelNo-1)+")");
					  row.getCell(i-1).setCellStyle(style1);
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  if(ukQuantity>0){
					  //row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(ukQuantity);
					  row.createCell(i++).setCellFormula("SUM(I2:I"+(excelNo-1)+")");
					  row.getCell(i-1).setCellStyle(style1);
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  if(frQuantity>0){
					 // row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(frQuantity);
					  row.createCell(i++).setCellFormula("SUM(M2:M"+(excelNo-1)+")");
					  row.getCell(i-1).setCellStyle(style1);
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  if(itQuantity>0){
					  //row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(itQuantity);
					  row.createCell(i++).setCellFormula("SUM(Q2:Q"+(excelNo-1)+")");
					  row.getCell(i-1).setCellStyle(style1);
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  if(esQuantity>0){
					 // row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(esQuantity);
					  row.createCell(i++).setCellFormula("SUM(U2:U"+(excelNo-1)+")");
					  row.getCell(i-1).setCellStyle(style1);
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
				  
				  for (int j = 0; j <title.length; j++) {
			       		 sheet.autoSizeColumn((short)j, true);
				  }
				  sheet.setForceFormulaRecalculation(true);
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
		  }
		  
		  List<String> countryList=Lists.newArrayList("ca","com","jp","mx","com2","com3");
		  for (String country : countryList) {
			  Map<String,Map<String,Object[]>> countryMap=map.get(country);
			  if(countryMap!=null&&countryMap.size()>0){
				  for (Map.Entry<String,Map<String,Object[]>> entry : countryMap.entrySet()) {
					  String model = entry.getKey();
					  Integer quantity=0;
					 
					  String suffix="";
					  if("com".equals(country)){
						  suffix="US";
					  }else if("com".equals(country)){
						  suffix="USNEW";
					  }else if("com".equals(country)){
						  suffix="US_Tomons";
					  }else{
						  suffix=country.toUpperCase();
					  }
					  
					  String[] title = {"Product",suffix,"审核数","装箱数",suffix+"箱数"};
					  HSSFSheet sheet= wb.createSheet(suffix+("0".equals(model)?"空运":("1".equals(model)?"海运":"铁路")));
					  HSSFRow row = sheet.createRow(0);
					  row.setHeight((short) 400);
					  HSSFCell cell = null;		
					  for (int i = 0; i < title.length; i++) {
							cell = row.createCell(i);
							cell.setCellValue(title[i]);
							cell.setCellStyle(style);
							sheet.autoSizeColumn((short) i);
					  }
					  int excelNo =1;
					 Map<String,Object[]> modelMap=entry.getValue();
					 for (Map.Entry<String,Object[]> entry1: modelMap.entrySet()) {
						 String productName = entry1.getKey(); 
						 Integer cnStock=0;
						  try {
							  cnStock= (inventorys.get(productName+"_"+country)==null?0:inventorys.get(productName+"_"+country));
						  }catch (Exception e) {}
						  if(newMap!=null&&newMap.get(productName+"_"+country)!=null&&newMap.get(productName+"_"+country).get("total")!=null&&newMap.get(productName+"_"+country).get("total").get("total")!=null){
								cnStock=cnStock-newMap.get(productName+"_"+country).get("total").get("total");
						  }
						  Integer pack=packQuantityMap.get(productName);
						    if(productName.contains("Inateck DB1001")){
								if("com,uk,jp,ca,mx,".contains(country)){
									pack=60;
								}else{
									pack=44;
								}
							}else if(productName.contains("Inateck DB2001")){
								if("com,jp,ca,mx,".contains(country)){
									pack=32;
								}else{
									pack=24;
								}
							}
						    Integer checkQuantity=0;
						    String remark="";
						    if(modelMap!=null&&entry1.getValue()!=null){
						    	checkQuantity=(Integer)entry1.getValue()[0];
						    	remark=entry1.getValue()[1]+"";
								quantity+=MathUtils.roundUp((Integer)entry1.getValue()[0]*1.0d/pack);
							  }
						      row = sheet.createRow(excelNo++);
							  int i =0;
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(productName);
							  String quantityStr=checkQuantity+"";
								 
							  if("0".equals(model)){//空运
								  if(cnStock>=checkQuantity){
									  row.getCell(0).setCellStyle(contentStyle);
								  }else{
									 quantityStr+="(在仓"+cnStock+")"; 
								  }
							  }else{//海运
								    if(countryMap!=null&&countryMap.get("0")!=null&&countryMap.get("0").get(productName)!=null){
										if((cnStock-(Integer)countryMap.get("0").get(productName)[0])>=(Integer)modelMap.get(productName)[0]){
											row.getCell(0).setCellStyle(contentStyle);
										}else{
											quantityStr+="(在仓"+((cnStock-(Integer)countryMap.get("0").get(productName)[0])<0?0:(cnStock-(Integer)countryMap.get("0").get(productName)[0]))+")";
										}
									}else{
										  if(cnStock>=checkQuantity){
											  row.getCell(0).setCellStyle(contentStyle);
										  }else{
											 quantityStr+="(在仓"+cnStock+")"; 
										  }
									}
							  }
							  /*if(cnStock>=checkQuantity){
								  row.getCell(0).setCellStyle(contentStyle);
							  }else{
								 quantityStr+="(在仓"+cnStock+")"; 
							  }*/
							  if(checkQuantity>0){
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(quantityStr);
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(checkQuantity);
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(pack);
								  //row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(MathUtils.roundUp(checkQuantity*1.0d/pack));
								 // row.createCell(i++).setCellFormula("roundup(C"+(excelNo)+"/D"+(excelNo)+",0)"); 
								  row.createCell(i++).setCellFormula("IF(MOD(C"+(excelNo)+",D"+(excelNo)+")>0,ROUND(C"+(excelNo)+"/D"+(excelNo)+",1),C"+(excelNo)+"/D"+(excelNo)+")");
								  row.getCell(i-1).setCellStyle(style1);
							  }else{
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
								  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
							  }
							  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(remark);
					 }
					  row = sheet.createRow(excelNo++);
					  int i =0;
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("总计");
					  row.getCell(i-1).setCellStyle(style1);
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  if(quantity>0){
						//  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(quantity);
						  row.createCell(i++).setCellFormula("SUM(E2:E"+(excelNo-1)+")");
						  row.getCell(i-1).setCellStyle(style1);
					  }else{
						  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  }
					  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					  
					 for (int j = 0; j <title.length; j++) {
			       		 sheet.autoSizeColumn((short)j, true);
				     }
					 sheet.setForceFormulaRecalculation(true);
				  }
			  }
		  }

		  
		  
		  	request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			String fileName = "理诚物流分配" + sdf.format(new Date()) ;
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName+ ".xls");
			try {
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
				return null;
		}
	
}