/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.SalesForecastServiceByMonth;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiInventoryTotalDtoByInStock;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductAttribute;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiProductInStock;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.entity.PsiTransportForecastAnalyseOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportForecastPlanOrder;
import com.springrain.erp.modules.psi.scheduler.PsiConfig;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductInStockService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.psi.service.PsiTransportForecastOrderService;


@Controller
@RequestMapping(value = "${adminPath}/psi/forecastDayOrder")
public class PsiTransportForecastDayOrderController extends BaseController {
	
	@Autowired
	private PsiTransportForecastOrderService	forecastOrderService;
	@Autowired
	private PsiProductService	psiProductService;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	@Autowired
	private PsiInventoryService	psiInventoryService;
	@Autowired
	private SaleReportService	saleReportService;
	@Autowired
	private PsiProductInStockService	psiProductInStockService;
	@Autowired
	private SalesForecastServiceByMonth	salesForecastService;
	@Autowired
	private PsiInventoryFbaService psiInventoryFbaService;
	@Autowired
	private PsiProductAttributeService    psiProductAttributeService;
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	
	@RequestMapping(value = {"list"})
	public String forecastDayOrder(HttpServletRequest request, HttpServletResponse response, Model model,String gap,String lineId,String name,String country){
		Map<String,Map<String,List<PsiTransportForecastAnalyseOrder>>> map=forecastOrderService.findAnalyse(gap,lineId,name,country);
		model.addAttribute("map", map);
		List<PsiProductAttribute> attrs = psiProductAttributeService.findAll();
		Map<String,PsiProductAttribute> productAttr = Maps.newHashMap();
		for (PsiProductAttribute psiProductAttribute : attrs) {
			productAttr.put(psiProductAttribute.getColorName(), psiProductAttribute);
		}
		model.addAttribute("productAttr", productAttr);
		model.addAttribute("name", name);
		model.addAttribute("country", country);
		model.addAttribute("lineId", lineId);
		List<PsiProductTypeGroupDict> lineList=groupDictService.getAllList();
		model.addAttribute("lineList",lineList);
		model.addAttribute("planMap",forecastOrderService.findPlanOrder());
		model.addAttribute("gap",gap);
		return "modules/psi/psiTransportForecastAnalyseOrderList";
	}
	
	
	@RequestMapping(value = {"planList"})
	public String forecastPlanOrder(HttpServletRequest request, HttpServletResponse response, Model model,Date startDate,Date endDate,String state,String type,String lineId,String name,String country){
		if(StringUtils.isBlank(type)){
			type="0";
		}
		if(startDate==null){
			Date today=new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			startDate=DateUtils.addDays(today, -10);
			endDate=today;
		}
		if(StringUtils.isBlank(state)){
			state="0";
		}
		Map<String,Map<String,List<PsiTransportForecastPlanOrder>>> map=forecastOrderService.findPlanOrder( startDate, endDate, state,type,lineId,name,country);
		model.addAttribute("map", map);
		List<PsiProductAttribute> attrs = psiProductAttributeService.findAll();
		Map<String,PsiProductAttribute> productAttr = Maps.newHashMap();
		for (PsiProductAttribute psiProductAttribute : attrs) {
			productAttr.put(psiProductAttribute.getColorName(), psiProductAttribute);
		}
		
		model.addAttribute("productAttr", productAttr);
		model.addAttribute("name", name);
		model.addAttribute("country", country);
		model.addAttribute("lineId", lineId);
		model.addAttribute("state", state);
		model.addAttribute("type", type);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		List<PsiProductTypeGroupDict> lineList=groupDictService.getAllList();
		model.addAttribute("lineList",lineList);
		return "modules/psi/psiTransportForecastPlanOrderList";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"updateInfo"})
	public String updateInfo(Integer itemId,String flag,String content) {
		try {
			return forecastOrderService.updateInfo2(itemId, flag, content);
		} catch (Exception e) {
			return "0";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "saveTransOrPurchase")
	public String addTrans(PsiTransportForecastPlanOrder order,Integer analySeId){
		PsiTransportForecastAnalyseOrder analyseOrder=forecastOrderService.getAnalyseOrder(analySeId);
		String name=analyseOrder.getProductName();
		String country=analyseOrder.getCountryCode();
		String sku="";
		if("0".equals(order.getType())){
			Map<String,String> skuMap=psiProductService.getSkuByProduct();
			sku=skuMap.get(name+"_"+country);
			if(StringUtils.isNotBlank(sku)){
				order.setSku(sku);
			}else{
				order.setSku(name+"_"+country);
				sku=name+"_"+country;
			}
		}	
		Map<String,Integer> packQuantityMap=psiProductService.getPackQuantity();
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
		PsiTransportForecastPlanOrder planOrder=forecastOrderService.findOrder(name,country,order.getType(),sku,order.getTransportType(),order.getModel());
		if(planOrder!=null){
			planOrder.setBoxNum(pack);
			planOrder.setUpdateDate(new Date());
			planOrder.setOrder(analyseOrder);
			planOrder.setQuantity(order.getQuantity());
			planOrder.setRemark(order.getRemark());
			forecastOrderService.savePlanOrder(planOrder);
		}else{
			order.setProductName(name);
			order.setCountryCode(country);
			order.setState("0");
			order.setDelFlag("0");
			order.setUpdateDate(new Date());
			order.setBoxNum(pack);
			order.setOrder(analyseOrder);
			forecastOrderService.savePlanOrder(order);
		}
		return "0";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"checkOrderQuantity"})
	public String checkOrderQuantity(String idArr){
		StringBuilder returnStr=new StringBuilder();
		
		Map<String,Integer> checkMap=forecastOrderService.findCheckQuantity(Sets.newHashSet(idArr.split(",")));
		Map<String,Integer> cnMap=forecastOrderService.findCnStockByName2(checkMap.keySet(),new Date());
		for(Map.Entry<String,Integer> entry : checkMap.entrySet()){
			String name =entry.getKey();
			if(cnMap!=null&&cnMap.get(name)!=null&&entry.getValue()>cnMap.get(name)){
				returnStr.append(name).append(",库存：").append(cnMap.get(name)).append(",运单数：").append(entry.getValue()).append("<br/>");
			}
		}
		if(StringUtils.isNotBlank(returnStr)){
			return returnStr.toString();
		}else{
			return "0";
		}
	}
	
	
	@RequestMapping(value = {"genTransOrder"})
	public String genTransOrder(String idArr){
		try {
			Set<Integer> idSet=Sets.newHashSet();
			for(String id:idArr.split(",")){
				idSet.add(Integer.parseInt(id));
			}
			forecastOrderService.saveTransData(idSet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/psi/forecastDayOrder/planList?repage";
	}
	
	
	@RequestMapping(value = {"genPurchaseOrder"})
	public String genPurchaseOrder(String idArr){
		try {
			Set<Integer> idSet=Sets.newHashSet();
			for(String id:idArr.split(",")){
				idSet.add(Integer.parseInt(id));
			}
			forecastOrderService.savePurchase(idSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/psi/forecastDayOrder/planList?type=1";
	}
	
	
	@RequestMapping(value = {"generateDayOrder"})
	public void generateDayOrder(){
		 List<PsiProduct> productList=psiProductService.findIsComponents("0");
		 Map<String,PsiProductEliminate> productCountryAttrMap=psiProductEliminateService.findProductCountryAttr();
		 Map<String,String> skuMap=psiProductService.getSkuByProduct();
		 Map<String,Map<Date,Map<String,Integer>>> poMap=forecastOrderService.getPurchaseOrder(productCountryAttrMap,skuMap);//name_country date sku/total quantity
		 Map<String,Map<Date,Map<String,Integer>>> tranMap=forecastOrderService.getAllTransportQuantity();
		 Map<String,Integer> receiveFbaTran = psiInventoryService.getFbaTransporttingReceiveByName();
		 Map<String,PsiInventoryFba> amazonStock=psiInventoryService.getAllProductFbaInfo();
		 Map<String,Set<String>> typeMap=saleReportService.findProductByType();////0:keyboard 1:四国泛欧
		 Set<String> keyBoardSet=typeMap.get("0");
		 Set<String> euNoUkSet=typeMap.get("1");
		 Map<String,PsiInventoryTotalDtoByInStock> inventoryMap=psiInventoryService.getInventoryQuantity();
		 Map<String,Map<String,PsiProductInStock>> stockMap=psiProductInStockService.getHistoryInventory();
		 Map<String,Map<String,Integer>> cnStock=forecastOrderService.getCnStockProduct();//name_country sku 
		 
		Map<String,Map<String,Integer>> allDaySalesMap= salesForecastService.findAllWithType();//name+"_"+country 月    月销  
		// Map<String,Map<String,Integer>> allDaySalesMap=Maps.newHashMap();
		 Map<String,Map<String,Integer>>  sale30Map = psiInventoryFbaService.get31SalesMap(null);//country name 
		 /* List<String> monthList=Lists.newArrayList("201711","201712","201801","201802","201803","201804","201805");
	     for(Map.Entry<String,Map<String,Integer>> entry:allDaySalesMap.entrySet()){
			 String country=entry.getKey();
			 Map<String,Integer> temp=entry.getValue();
			 for (Map.Entry<String,Integer> m:temp.entrySet()) {
				String name=m.getKey();
				Integer qty=m.getValue();
				if(qty==0){
					continue;
				}
				for(String month:monthList){
					Map<String,Integer> ncMap=allDaySalesMap.get(name+"_"+country);
					if(ncMap==null){
						ncMap=Maps.newHashMap();
						allDaySalesMap.put(name+"_"+country,ncMap);
					}
					ncMap.put(month,qty);
					
					
					Map<String,Integer> euMap=allDaySalesMap.get(name+"_eu");
					if(euMap==null){
						euMap=Maps.newHashMap();
						allDaySalesMap.put(name+"_eu",euMap);
					}
					euMap.put(month,qty+(euMap.get(month)==null?0:euMap.get(month)));
					
					Map<String,Integer> euNOUkMap=allDaySalesMap.get(name+"_euNoUk");
					if(euNOUkMap==null){
						euNOUkMap=Maps.newHashMap();
						allDaySalesMap.put(name+"_euNoUk",euNOUkMap);
					}
					euNOUkMap.put(month,qty+(euNOUkMap.get(month)==null?0:euNOUkMap.get(month)));
				}
			}
		 }*/
		 
	     SimpleDateFormat dateFormatMonth=new SimpleDateFormat("yyyyMM");		
		 List<String> euCountryList=Lists.newArrayList("de","fr","uk","it","es");	
		 SimpleDateFormat dateFormatDay=new SimpleDateFormat("yyyyMMdd");
		// Map<String,Integer> cnUsedQtyMap=Maps.newHashMap();
		// Map<String,Map<Date,Map<String,Integer>>> poUsedMap=Maps.newHashMap();
		 boolean flag=false;//缓冲期开关
		 Date today=new Date();
		 List<PsiTransportForecastAnalyseOrder> orderList=Lists.newArrayList();
		 for (PsiProduct product : productList) {
			 String platform=product.getPlatform();
			 String[] countryArr=platform.split(",");
			 for(String name:product.getProductNameWithColor()){
				    for (String country: countryArr) {
						  String key=name+"_"+country;
						  PsiProductEliminate eliminate=productCountryAttrMap.get(key);
						  if(eliminate==null){
							  continue;
						  }
						  PsiInventoryTotalDtoByInStock inventorys = inventoryMap.get(name);
						  //淘汰无采购单无库存产品不分析
                          if("淘汰".equals(eliminate.getIsSale())&&(poMap==null||poMap.get(name)==null||poMap.get(name).get(country)==null)&&(inventorys==null||inventorys.getInventorys()==null||inventorys.getInventorys().get(country)==null
									||inventorys.getInventorys().get(country).getQuantityInventory()==null||inventorys.getInventorys().get(country).getQuantityInventory().get("CN")==null
									||inventorys.getInventorys().get(country).getQuantityInventory().get("CN").getNewQuantity()==null||inventorys.getInventorys().get(country).getQuantityInventory().get("CN").getNewQuantity()==0)){
				    		  continue;
				    	  } 
                          PsiTransportForecastAnalyseOrder order=new PsiTransportForecastAnalyseOrder();
                          order.setProductName(name);
                          order.setCountryCode(country);
                          order.setUpdateDate(today);
                          Map<Date,Map<String,Integer>> namePoMap=Maps.newLinkedHashMap();
                          List<Date> poDateList=Lists.newArrayList();
						  if(poMap!=null&&poMap.get(key)!=null){
							  namePoMap=poMap.get(key);
							  poDateList=Lists.newArrayList(namePoMap.keySet());
							  Collections.sort(poDateList);
						  }
					      Map<String,Integer> cnMap=Maps.newHashMap();
					      if(cnStock.get(name+"_"+country)!=null){
					    	  cnMap=cnStock.get(name+"_"+country);
					      }
						  Integer seaDays=PsiConfig.get(country).getFbaBySea();
						  Integer airDays=PsiConfig.get(country).getFbaBySky();
						  Integer period=product.getProducePeriod()+7;//7天备货期
						  Date airDate=DateUtils.addDays(today, airDays+7);
						  Date seaDate=DateUtils.addDays(today, seaDays+7);
						  if(1==eliminate.getTransportType()&&!"ca".equals(country)){//1:海运 2：空运
							  period+=seaDays;
						  }else{
							  period+=airDays;
						  }
						  if(flag){
							  period+=eliminate.getBufferPeriod();
						  }
						  order.setPeirod(period);
						  Integer fbaStock=0;
						  Integer oversea=0;
						  Integer safeInventory=0;
							
						  Map<String,Integer> daySalesMap=Maps.newHashMap();
						  String tip="";
						  String salesInfo="";
						  String poInfo="";
						  String fillUpTip="";
						  String transInfo="";
						  for(Date date:poDateList){
							  Map<String, Integer> dateMap= namePoMap.get(date);
							  poInfo+=dateFormatDay.format(date)+":"+dateMap.get("total")+"<br/><br/>";
						  }
						  order.setPoInfo(poInfo);
						  if(cnMap.get("total")!=null&&cnMap.get("total")>0){
							  order.setCnStock(cnMap.get("total"));
						  }
						  
						  Map<Date,Map<String,Integer>> dateTranMap=Maps.newHashMap();
						  Integer day31Sales=0;
						  if(!keyBoardSet.contains(name)&&!euNoUkSet.contains(name)&&"de".equals(country)){//泛欧
							  PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_eu");
							  if(psiInventoryFba!=null){
									fbaStock=psiInventoryFba.getRealTotal();
									Integer receiveNum=0;
									if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
										for(String euCountry:euCountryList){
											receiveNum+=(receiveFbaTran.get(name+"_"+euCountry)==null?0:receiveFbaTran.get(name+"_"+euCountry));
										}
									}
									if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
										fbaStock=fbaStock-receiveNum;
									}
								}
							    
							    if(inventorys!=null&&inventorys.getQuantityEuro()!=null&&inventorys.getQuantityEuro().get("DE")!=null&&inventorys.getQuantityEuro().get("DE").getNewQuantity()!=null){
									oversea=inventorys.getQuantityEuro().get("DE").getNewQuantity();
								}
							    daySalesMap=allDaySalesMap.get(name+"_eu");
							    
							    if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get("eu")!=null){
									safeInventory=MathUtils.roundUp(stockMap.get(name).get("eu").getSafeInventory());
								}
							    dateTranMap=tranMap.get(name+"_eu");
							    
							    Integer totalSal=0;
								for(String euCountry:euCountryList){
									if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
										totalSal+=sale30Map.get(euCountry).get(name);
									}
								}
								day31Sales=MathUtils.roundUp(totalSal/31d);
								
						  }else if(euNoUkSet.contains(name)&&"de".contains(country)){//四国泛欧
							    PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_four");
								if(psiInventoryFba!=null){
									fbaStock=psiInventoryFba.getRealTotal();
									Integer receiveNum=0;
									if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
										for(String euCountry:euCountryList){
											if(!"uk".equals(euCountry)){
												receiveNum+=(receiveFbaTran.get(name+"_"+euCountry)==null?0:receiveFbaTran.get(name+"_"+euCountry));
											}
										}
									}
									if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
										fbaStock=fbaStock-receiveNum;
									}
								}
								
								for(String cty:euCountryList){
									if(!"uk".equals(cty)){
										if(inventorys!=null&&inventorys.getInventorys()!=null&&inventorys.getInventorys().get(cty)!=null
												&&inventorys.getInventorys().get(cty).getQuantityInventory()!=null&&inventorys.getInventorys().get(cty).getQuantityInventory().get("DE")!=null
												&&inventorys.getInventorys().get(cty).getQuantityInventory().get("DE").getNewQuantity()!=null){
													oversea+=inventorys.getInventorys().get(cty).getQuantityInventory().get("DE").getNewQuantity();
										}
									}
								}
								daySalesMap=allDaySalesMap.get(name+"_euNoUk");
								
								if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get("eu")!=null){
									safeInventory=MathUtils.roundUp(stockMap.get(name).get("eu").getSafeInventory());
									if(stockMap.get(name).get("uk")!=null){
										safeInventory=safeInventory-MathUtils.roundUp(stockMap.get(name).get("uk").getSafeInventory());
									}
								}
								
								dateTranMap=tranMap.get(name+"_euNoUk");
								
								Integer totalSal=0;
								for(String euCountry:euCountryList){
									if(!"uk".equals(euCountry)){
										if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
											totalSal+=sale30Map.get(euCountry).get(name);
										}
									}
								}
								day31Sales=MathUtils.roundUp(totalSal/31d);
								
						  }else if(("ca,com,jp,mx".contains(country))
								  ||(keyBoardSet.contains(name)&&"de,fr,it,es,uk".contains(country))
								  ||(euNoUkSet.contains(name)&&"uk".contains(country))){
							  
							    PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_"+country);
								if(psiInventoryFba!=null){
									fbaStock=psiInventoryFba.getRealTotal();
									Integer receiveNum=0;
									if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
										receiveNum+=(receiveFbaTran.get(name+"_"+country)==null?0:receiveFbaTran.get(name+"_"+country));
									}
									if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
										fbaStock=fbaStock-receiveNum;
									}
								}
								String code="";
								if("jp".equals(country)){
									code="JP";
								}else if("com,ca".contains(country)){
									code="US";
								}else if("de,fr,it,es,uk".contains(country)){
									code="DE";
								}
								if(inventorys!=null&&inventorys.getInventorys()!=null&&inventorys.getInventorys().get(country)!=null
										&&inventorys.getInventorys().get(country).getQuantityInventory()!=null&&inventorys.getInventorys().get(country).getQuantityInventory().get(code)!=null
										&&inventorys.getInventorys().get(country).getQuantityInventory().get(code).getNewQuantity()!=null){
										   oversea=inventorys.getInventorys().get(country).getQuantityInventory().get(code).getNewQuantity();
								}
								daySalesMap=allDaySalesMap.get(name+"_"+country);
								
								if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get(country)!=null){
									safeInventory=MathUtils.roundUp(stockMap.get(name).get(country).getSafeInventory());
								}
								
								dateTranMap=tranMap.get(name+"_"+country);
								
								Integer totalSal=0;
								if(sale30Map.get(country)!=null&&sale30Map.get(country).get(name)!=null){
									totalSal+=sale30Map.get(country).get(name);
								}
								
								day31Sales=MathUtils.roundUp(totalSal/31d);
						  }
						  if(daySalesMap==null||daySalesMap.size()==0||day31Sales==0){//
							  continue;
						  }
						  salesInfo+="31均日销："+day31Sales+"<br/>";
						  for(Map.Entry<String,Integer> monthMap:daySalesMap.entrySet()){
							  salesInfo+=monthMap.getKey()+"预月销："+monthMap.getValue()+"<br/>";
						  }
						  order.setSalesInfo(salesInfo);
						  order.setFbaStock(fbaStock);
						  order.setOversea(oversea);
						  order.setSafeInventory(safeInventory);
						  
						  
						  
						  //先看FBA库存+海外仓-安全库存可售天
						  Integer sellableQty=fbaStock+oversea-safeInventory;
						  Date fbaDate=today;
						  Integer fbaSalesDay=0;
						  if(sellableQty>0){
							  Date start=today;
							  Date lastDate=DateUtils.getLastDayOfMonth(start);
							  String month=dateFormatMonth.format(start);
							  Integer daySales=0;
							  if(daySalesMap.get(month)!=null){
								  daySales=MathUtils.roundUp(daySalesMap.get(month)*1d/getDaysOfMonth(start));
							  }else{
								  daySales=day31Sales;
							  }
							  int days=(int)DateUtils.spaceDays(start,lastDate)+1;
							  Integer requireQty=days*daySales;
							 
							  if(sellableQty>requireQty){
								  Integer  remainingQty=sellableQty-requireQty;
								  fbaDate=DateUtils.getLastDayOfMonth(start);
								  fbaSalesDay=days;
								  while(remainingQty>0){
									  start=DateUtils.addDays(lastDate,1);
									  lastDate=DateUtils.getLastDayOfMonth(start);
									  month=dateFormatMonth.format(start);
									  if(daySalesMap.get(month)==null){
										  daySales= day31Sales;
									  }else{
										  daySales=MathUtils.roundUp(1d*daySalesMap.get(month)/getDaysOfMonth(start)); 
									  }
									 
									  days=(int)DateUtils.spaceDays(start,lastDate)+1;
									  requireQty=days*daySales;
									  if(remainingQty>requireQty){
										  fbaDate=lastDate;
										  fbaSalesDay+=days;
									  }else{
										  Integer fbaSales=remainingQty/daySales;
										  fbaSalesDay+=fbaSales;
										  fbaDate=DateUtils.addDays(fbaDate,fbaSales); 
									  }
									  remainingQty=remainingQty-requireQty;
								  }
							  }else{
								  fbaSalesDay=sellableQty/daySales;
								  fbaDate=DateUtils.addDays(fbaDate,fbaSalesDay);
							  }
						  }else{
							  Date start=today;
							  String month=dateFormatMonth.format(start);
							  Integer daySales=0;
							  if(daySalesMap.get(month)!=null){
								  daySales=MathUtils.roundUp(1d*daySalesMap.get(month)/getDaysOfMonth(start));
							  }else{
								  daySales=day31Sales;
							  }
							  fbaSalesDay=sellableQty/daySales;
						  }
						  if(!fbaDate.equals(today)){
							  transInfo="FBA+LOCAL-安:"+dateFormatDay.format(fbaDate)+"  FBA可售天:"+fbaSalesDay+"<br/><br/>";
						  }
						  
						  Date beforeDate=fbaDate;
						  if(dateTranMap!=null&&dateTranMap.size()>0){
							    Set<Date> tempDateSet=dateTranMap.keySet();
								List<Date> dateSet=Lists.newArrayList(tempDateSet);
								Collections.sort(dateSet);
								for (Date date: dateSet) {
									Map<String, Integer> tranTypeMap=dateTranMap.get(date);
									String tranDate=dateFormatDay.format(date);
									for(Map.Entry<String,Integer> entry:tranTypeMap.entrySet()){
										    String type=("1".equals(entry.getKey())?"[FBA]":"");
										    Integer quantity=entry.getValue();
										 
										    if(date.before(DateUtils.addDays(today,period+7))){//周采购
												if(beforeDate.after(DateUtils.addDays(today, period+7))){
													transInfo+=tranDate+type+"::"+quantity+"<br/><br/>";
												    continue;	
												}
												if(date.before(beforeDate)||date.equals(beforeDate)){
													Date afterDate=getDate(beforeDate,quantity,dateFormatMonth,daySalesMap,day31Sales);
													transInfo+=tranDate+type+":"+quantity+",可售:"+dateFormatDay.format(beforeDate)+"~"+dateFormatDay.format(afterDate)+"<br/><br/>";
													beforeDate=afterDate;
												}else if(date.after(beforeDate)){
													Integer gap=getGapByDate(beforeDate,date,dateFormatMonth,daySalesMap,day31Sales);
													tip+="运输缺口日期:"+dateFormatDay.format(beforeDate)+"~"+dateFormatDay.format(date)+",数量:"+gap+"<br/><br/>";
													if(date.before(airDate)){
														fillUpTip+="缺货最后日期在空运到达期"+dateFormatDay.format(airDate)+"前,不补货<br/><br/>";
													}else{
														if(beforeDate.before(airDate)){
															 fillUpTip+="缺货开始日期在空运到达期前,"+dateFormatDay.format(beforeDate)+"-"+dateFormatDay.format(airDate)+"不补货<br/><br/>";
															 beforeDate=airDate;
														}
														
														if(date.before(seaDate)){//都是空运缺
															fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"0",seaDays,airDays,poDateList,beforeDate,date,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
														}else{
															if(beforeDate.before(seaDate)&&!"ca".equals(country)){
																//1 beforeDate-seaDate
																fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"0",seaDays,airDays,poDateList,beforeDate,seaDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
																//2 seaDate-date
																fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"1",seaDays,airDays,poDateList,seaDate,date,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
															}else{
																fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"1",seaDays,airDays,poDateList,beforeDate,date,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
															}
														}
													}/*else if(beforeDate.after(seaDate)){
														Date lastedDate=DateUtils.addDays(beforeDate,-seaDays);
														fillUpTip+="缺货开始日期"+dateFormatDay.format(beforeDate)+"在海运期"+dateFormatDay.format(seaDate)+"外,最迟海运日期"+dateFormatDay.format(lastedDate)+",";
														//判断是否有货 有无空运
														Integer stock=findProductQuantity(lastedDate,namePoMap,cnMap);
														if(stock>=gap){
															String disQty=distributionQty(gap,lastedDate,namePoMap,cnMap,poDateList);
															fillUpTip+=disQty;
														}
													}else{
														 if(beforeDate.before(airDate)){
															 beforeDate=airDate;
															 fillUpTip+="缺货开始日期在空运到达期前,"+dateFormatDay.format(beforeDate)+"-"+dateFormatDay.format(airDate)+"不补货<br/><br/>";
														 }
														 fillUpTip+=distributionQty(gap,DateUtils.addDays(airDate,-7),namePoMap,cnMap,poDateList);
													}*/
													
													Date afterDate=getDate(date,quantity,dateFormatMonth,daySalesMap,day31Sales);
													transInfo+=tranDate+type+":"+quantity+",可售:"+dateFormatDay.format(date)+"~"+dateFormatDay.format(afterDate)+"<br/><br/>";
													beforeDate=afterDate;
												}
											}else{
												transInfo+=tranDate+type+"::"+quantity+"<br/><br/>";
											}
									}
								}
						  }
						  
						  if(beforeDate.before(DateUtils.addDays(today,period+7))){
							  if(beforeDate.before(DateUtils.addDays(today,period))){
								  Date tempDate=DateUtils.addDays(today,period);
								  Integer gap=getGapByDate(beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales);
								  if(gap>0){
									  tip+="运输缺口日期:"+dateFormatDay.format(beforeDate)+"~"+dateFormatDay.format(tempDate)+",数量:"+gap+"<br/><br/>";
								  }
								  
								  if(beforeDate.before(airDate)){
										 fillUpTip+="缺货开始日期在空运到达期前,"+dateFormatDay.format(beforeDate)+"-"+dateFormatDay.format(airDate)+"不补货<br/><br/>";
										 beforeDate=airDate;
								  }
								  if(tempDate.before(seaDate)){//都是空运缺
										fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"0",seaDays,airDays,poDateList,beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
								  }else{
									    if(beforeDate.before(seaDate)&&!"ca".equals(country)){
											fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"0",seaDays,airDays,poDateList,beforeDate,seaDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
											beforeDate=seaDate;
											fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"1",seaDays,airDays,poDateList,beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
										}else{
											fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"1",seaDays,airDays,poDateList,beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
										}
								  }
								  beforeDate=tempDate;
								  Date endDate=DateUtils.addDays(tempDate,7);
								  if(beforeDate.before(endDate)){
									  Integer poAnalyseGap=getGapByDate(beforeDate,endDate,dateFormatMonth,daySalesMap,day31Sales);
									  tip+="采购缺口日期:"+dateFormatDay.format(beforeDate)+"~"+dateFormatDay.format(endDate)+",数量:"+poAnalyseGap+"<br/><br/>";
									  if("ca".equals(country)){
											fillUpTip+="采购:"+distributionQty(order,product.getProducePeriod(),country,"0",seaDays,airDays,poDateList,beforeDate,endDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
									  }else{
											fillUpTip+="采购:"+distributionQty(order,product.getProducePeriod(),country,"1",seaDays,airDays,poDateList,beforeDate,endDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
									  }
								  }
							  }else{
								  Date tempDate=DateUtils.addDays(today,period+7);
								  if(beforeDate.before(tempDate)){
									  Integer poAnalyseGap=getGapByDate(beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales);
									  tip+="采购缺口日期:"+dateFormatDay.format(beforeDate)+"~"+dateFormatDay.format(tempDate)+",数量:"+poAnalyseGap+"<br/><br/>";
									  if("ca".equals(country)){
											fillUpTip+="采购:"+distributionQty(order,product.getProducePeriod(),country,"0",seaDays,airDays,poDateList,beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
									  }else{
											fillUpTip+="采购:"+distributionQty(order,product.getProducePeriod(),country,"1",seaDays,airDays,poDateList,beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
									  }
								  }
							  }
						  }
						  order.setTransInfo(transInfo);
						  order.setTip(tip);
						  order.setFillUpTip(fillUpTip);
						  
						  orderList.add(order);
					}
			 }
		 }
		 if(orderList.size()>0){
			 forecastOrderService.save(orderList);
		 }
	}
	
	
	public int getDaysOfMonth(Date date) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);  
    }  
	
	public Date getDate(Date beforeDate,Integer quantity,SimpleDateFormat dateFormatMonth,Map<String,Integer> daySalesMap,Integer day31Sales){
		Date start=beforeDate;
		Date lastDate=DateUtils.getLastDayOfMonth(start);
	    String month=dateFormatMonth.format(start);
		Integer daySales=0;
		if(daySalesMap.get(month)!=null){
			  daySales=MathUtils.roundUp(1d*daySalesMap.get(month)/getDaysOfMonth(start));
		}else{
			  daySales=day31Sales;
		}
		int days=(int)DateUtils.spaceDays(start,lastDate)+1;
		Integer requireQty=days*daySales;
		
		if(quantity>requireQty){
			  Integer  remainingQty=quantity-requireQty;
			  beforeDate=DateUtils.getLastDayOfMonth(start);
			  while(remainingQty>0){
				  start=DateUtils.addDays(lastDate,1);
				  lastDate=DateUtils.getLastDayOfMonth(start);
				  month=dateFormatMonth.format(start);
				  //daySales=daySalesMap.get(month)/getDaysOfMonth(start);
				  if(daySalesMap.get(month)!=null){
					  daySales=MathUtils.roundUp(1d*daySalesMap.get(month))/getDaysOfMonth(start);
				  }else{
					  daySales=day31Sales;
				  }
				  days=(int)DateUtils.spaceDays(start,lastDate)+1;
				  requireQty=days*daySales;
				  if(remainingQty>requireQty){
					  beforeDate=lastDate;
				  }else{
					  Integer fbaSales=remainingQty/daySales;
					  beforeDate=DateUtils.addDays(beforeDate,fbaSales); 
				  }
				  remainingQty=remainingQty-requireQty;
			  }
		 }else{
			  beforeDate=DateUtils.addDays(beforeDate,quantity/daySales);
		 }
		 return beforeDate;
	}
	
	public Integer getGapByDate(Date startDate,Date endDate,SimpleDateFormat dateFormatMonth,Map<String,Integer> daySalesMap,Integer day31Sales){
		Integer quantity=0;
		String startMonth=dateFormatMonth.format(startDate);
		String endMonth=dateFormatMonth.format(endDate);
		if(startMonth.equals(endMonth)){
			Integer daySales=0;
			if(daySalesMap.get(startMonth)!=null){
				  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(startDate));
			}else{
				  daySales=day31Sales;
			}
			int days=(int)DateUtils.spaceDays(startDate,endDate)+1;
			return days*daySales;
		}else{
			Date start=startDate;
			Date lastDate=DateUtils.getLastDayOfMonth(start);
			//Integer daySales=daySalesMap.get(startMonth)/getDaysOfMonth(startDate);
			Integer daySales=0;
			if(daySalesMap.get(startMonth)!=null){
				  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(startDate));
			}else{
				  daySales=day31Sales;
			}
			int days=(int)DateUtils.spaceDays(start,lastDate)+1;
			quantity=days*daySales;
			
			start=DateUtils.addDays(lastDate, 1);
			startMonth=dateFormatMonth.format(start);
			endMonth=dateFormatMonth.format(endDate);
			while(!startMonth.equals(endMonth)){
				lastDate=DateUtils.getLastDayOfMonth(start);
				//daySales=daySalesMap.get(startMonth)/getDaysOfMonth(start);
			
				if(daySalesMap.get(startMonth)!=null){
					  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(start));
				}else{
					  daySales=day31Sales;
				}
				days=(int)DateUtils.spaceDays(start,lastDate)+1;
				quantity+=days*daySales;
				
				start=DateUtils.addDays(lastDate, 1);
				startMonth=dateFormatMonth.format(start);
				endMonth=dateFormatMonth.format(endDate);
			}
			//daySales=daySalesMap.get(startMonth)/getDaysOfMonth(start);
			if(daySalesMap.get(startMonth)!=null){
				  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(start));
			}else{
				  daySales=day31Sales;
			}
			days=(int)DateUtils.spaceDays(start,endDate)+1;
			quantity+=days*daySales;
		}
		return quantity;
	}
	
	
	public String distributionGap(PsiTransportForecastAnalyseOrder order,Integer period,String country,SimpleDateFormat dateFormatDay,Date shippingDate,String type,Integer seaDays,Integer airDays,Integer gap,List<Date> poDateList,Date startDate,Date endDate,SimpleDateFormat dateFormatMonth,Map<String,Integer> daySalesMap,Integer daySales,Map<Date,Map<String,Integer>> namePoMap,Map<String,Integer> nameCnMap){
		String info="";
		Date today=new Date();
		String todayStr=dateFormatDay.format(today);
		Integer cnAndPoStock=findProductQuantity(shippingDate,namePoMap,nameCnMap);
		if(cnAndPoStock>=gap){
			info=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+","+("0".equals(type)||"ca".equals(country)?"空运":"海运")+"最迟发货日期"+dateFormatDay.format(shippingDate)+",PO+CN分配"+gap+",可售至"+dateFormatDay.format(endDate)+"<br/><br/>";
			gap=distributionQty2(gap,shippingDate,namePoMap,nameCnMap,poDateList);
			if(todayStr.equals(dateFormatDay.format(shippingDate))){
				if("0".equals(type)||"ca".equals(country)){
					order.setAirGap(gap+(order.getAirGap()==null?0:order.getAirGap()));
				}else{
					order.setSeaGap(gap+(order.getSeaGap()==null?0:order.getSeaGap()));
				}
			}
			return info;
			//return distributionQty(gap,shippingDate,namePoMap,nameCnMap,poDateList);
		}
		startDate=DateUtils.addDays(startDate, MathUtils.roundDown(cnAndPoStock*1d/daySales));
		if(cnAndPoStock>0){
			info=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+","+("0".equals(type)||"ca".equals(country)?"空运":"海运")+"最迟发货日期"+dateFormatDay.format(shippingDate)+",PO+CN分配"+cnAndPoStock+",可售至"+dateFormatDay.format(startDate)+"<br/><br/>";
			gap=distributionQty2(gap,shippingDate,namePoMap,nameCnMap,poDateList);
			if(todayStr.equals(dateFormatDay.format(shippingDate))){
				if("0".equals(type)||"ca".equals(country)){
					order.setAirGap(cnAndPoStock+(order.getAirGap()==null?0:order.getAirGap()));
				}else{
					order.setSeaGap(cnAndPoStock+(order.getSeaGap()==null?0:order.getSeaGap()));
				}
			}
		}
		if(namePoMap!=null&&namePoMap.size()>0){
			for(Date poDate:poDateList){
				if(poDate.before(endDate)||poDate.equals(endDate)){
					Map<String,Integer> temp=namePoMap.get(poDate);
					if(temp.get("total")>0){
						Date tempDate=DateUtils.addDays(poDate,seaDays);
						if(tempDate.before(startDate)||tempDate.equals(startDate)){
							
							if("ca".equals(country)){
								info="空运";
							}else if("0".equals(type)){
								info+="空转海";
							}else{
								info="海运";
							}
							
							info+=",最迟发货日期"+dateFormatDay.format(DateUtils.addDays(startDate,-seaDays));
							if(temp.get("total")>=gap){
								info+=dateFormatDay.format(poDate)+"PO分配"+gap+",可售至"+dateFormatDay.format(endDate)+"<br/><br/>";
								startDate=endDate;
								
								if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(startDate,-seaDays)))){
									if("ca".equals(country)){
										order.setAirGap(gap+(order.getAirGap()==null?0:order.getAirGap()));
									}else{
										order.setSeaGap(gap+(order.getSeaGap()==null?0:order.getSeaGap()));
									}
								}
								
							}else{
								startDate=DateUtils.addDays(startDate, MathUtils.roundDown(temp.get("total")*1d/daySales));
								info+=dateFormatDay.format(poDate)+"PO分配"+temp.get("total")+",可售至"+dateFormatDay.format(startDate)+"<br/><br/>";
								
								if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(startDate,-seaDays)))){
									if("ca".equals(country)){
										order.setAirGap(temp.get("total")+(order.getAirGap()==null?0:order.getAirGap()));
									}else{
										order.setSeaGap(temp.get("total")+(order.getSeaGap()==null?0:order.getSeaGap()));
									}
								}
							}
							gap=distributionQty2(gap,poDate,namePoMap,nameCnMap,poDateList);
						}else{
							tempDate=DateUtils.addDays(poDate,airDays);
							if(tempDate.after(startDate)){
								info+=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(tempDate)+"空运前缺货不补<br/><br/>";
								if(endDate.before(tempDate)){
									break;
								}
								info+=dateFormatDay.format(tempDate)+"~"+dateFormatDay.format(endDate)+",";
								startDate=tempDate;
							}else{
								info+=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+",";
							}
							
							if("ca".equals(country)){
								info="空运,";
							}else if("1".equals(type)){
								info+="海转空,";
							}else{
								info="空运,";
							}
							
							info+="最迟发货日期"+dateFormatDay.format(DateUtils.addDays(poDate,-airDays))+",";
							if(temp.get("total")>=gap){
								info+=dateFormatDay.format(poDate)+"PO分配"+gap+",可售至"+dateFormatDay.format(endDate)+"<br/><br/>";
								startDate=endDate;
								
								if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(poDate,-airDays)))){
									order.setAirGap(gap+(order.getAirGap()==null?0:order.getAirGap()));
								}
							}else{
								startDate=DateUtils.addDays(startDate, MathUtils.roundDown(temp.get("total")*1d/daySales));
								info+=dateFormatDay.format(poDate)+"PO分配"+temp.get("total")+",可售至"+dateFormatDay.format(startDate)+"<br/><br/>";
								
								if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(poDate,-airDays)))){
									order.setAirGap(temp.get("total")+(order.getAirGap()==null?0:order.getAirGap()));
								}
							}
							gap=distributionQty2(gap,poDate,namePoMap,nameCnMap,poDateList);
						}
						if(gap<=0){
							break;
						}
					}
				}else{
					break;
				}
			}
		}
		
		if(gap>0){
			
			if("ca".equals(country)){
				Integer time=period+airDays;
				Date cmpDate=DateUtils.addDays(today,time);
				if(startDate.before(cmpDate)){
					 if(endDate.before(cmpDate)){
						 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购赶不上缺货日,";
					 }else{
						 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购只赶上部分缺货数:("+dateFormatDay.format(cmpDate)+"~"+dateFormatDay.format(endDate)+"),";
						 Integer poDays=(int)DateUtils.spaceDays(cmpDate,endDate)+1;
						 order.setPoGap(poDays*daySales+(order.getPoGap()==null?0:order.getAirGap()));
					 }
				}else{
					Date finalDate=DateUtils.addDays(startDate,-time);
					info+="空运最迟采购日期"+dateFormatDay.format(finalDate)+",";
					if(todayStr.equals(dateFormatDay.format(finalDate))){
						 order.setPoGap(gap+(order.getPoGap()==null?0:order.getAirGap()));
					}
				}
				info+="<font color='red'>"+dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+"采购缺货:"+gap+"</font><br/><br/>";
			}else{
				Integer time=period+seaDays;
				Date cmpDate=DateUtils.addDays(today,time);
				if(startDate.before(cmpDate)){
					time=period+airDays;
					cmpDate=DateUtils.addDays(today,time);
					if(startDate.before(cmpDate)){
						 if(endDate.before(cmpDate)){
							 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购赶不上缺货日,";
						 }else{
							 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购只赶上部分缺货数:("+dateFormatDay.format(cmpDate)+"~"+dateFormatDay.format(endDate)+"),";
							 Integer poDays=(int)DateUtils.spaceDays(cmpDate,endDate)+1;
							 order.setPoGap(poDays*daySales+(order.getPoGap()==null?0:order.getAirGap()));
						 }
					}else{
						Date finalDate=DateUtils.addDays(startDate,-time);
						info+="空运最迟采购日期"+dateFormatDay.format(finalDate)+",";
						if(todayStr.equals(dateFormatDay.format(finalDate))){
							 order.setPoGap(gap+(order.getPoGap()==null?0:order.getAirGap()));
						}
					}
				}else{
					Date finalDate=DateUtils.addDays(startDate,-time);
					info+="海运最迟采购日期"+dateFormatDay.format(finalDate)+",";
					if(todayStr.equals(dateFormatDay.format(finalDate))){
						 order.setPoGap(gap+(order.getPoGap()==null?0:order.getAirGap()));
					}
				}
				info+="<font color='red'>"+dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+"采购缺货:"+gap+"</font><br/><br/>";
			}
		}
		return info;
	}
	
	public String distributionQty(PsiTransportForecastAnalyseOrder order,Integer period,String country,String type,Integer seaDays,Integer airDays,List<Date> poDateList,Date startDate,Date endDate,SimpleDateFormat dateFormatMonth,Map<String,Integer> daySalesMap,Integer day31Sales,Map<Date,Map<String,Integer>> namePoMap,Map<String,Integer> nameCnMap){
		Integer quantity=0;
		String startMonth=dateFormatMonth.format(startDate);
		String endMonth=dateFormatMonth.format(endDate);
		SimpleDateFormat dateFormatDay=new SimpleDateFormat("yyyyMMdd");
		Date shippingDate=null;
		Date today=new Date();
		String todayStr=dateFormatDay.format(today);
		if("0".equals(type)||"ca".equals(country)){
			shippingDate=DateUtils.addDays(startDate,-airDays);
		}else{
			shippingDate=DateUtils.addDays(startDate,-seaDays);
		}
		String info="";
		Integer cnAndPoStock=findProductQuantity(shippingDate,namePoMap,nameCnMap);
		if(startMonth.equals(endMonth)){
			Integer daySales=0;
			if(daySalesMap.get(startMonth)!=null){
				  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(startDate));
			}else{
				  daySales=day31Sales;
			}
			Integer days=(int)DateUtils.spaceDays(startDate,endDate)+1;
			Integer gap=days*daySales;
			if(cnAndPoStock>=gap){
				info=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+","+("0".equals(type)||"ca".equals(country)?"空运":"海运")+"最迟发货日期"+dateFormatDay.format(shippingDate)+",PO+CN分配"+gap+",可售至"+dateFormatDay.format(endDate)+"<br/><br/>";
				distributionQty2(gap,shippingDate,namePoMap,nameCnMap,poDateList);
				if(todayStr.equals(dateFormatDay.format(shippingDate))){
					if("0".equals(type)||"ca".equals(country)){
						order.setAirGap(gap+(order.getAirGap()==null?0:order.getAirGap()));
					}else{
						order.setSeaGap(gap+(order.getSeaGap()==null?0:order.getSeaGap()));
					}
				}
				return info;
				//return distributionQty(gap,shippingDate,namePoMap,nameCnMap,poDateList);
			}
			startDate=DateUtils.addDays(startDate, MathUtils.roundDown(cnAndPoStock*1d/daySales));
			if(cnAndPoStock>0){
				info=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+","+("0".equals(type)||"ca".equals(country)?"空运":"海运")+"最迟发货日期"+dateFormatDay.format(shippingDate)+",PO+CN分配"+cnAndPoStock+",可售至"+dateFormatDay.format(startDate)+"<br/><br/>";
				gap=distributionQty2(gap,shippingDate,namePoMap,nameCnMap,poDateList);
				if(todayStr.equals(dateFormatDay.format(shippingDate))){
					if("0".equals(type)||"ca".equals(country)){
						order.setAirGap(cnAndPoStock+(order.getAirGap()==null?0:order.getAirGap()));
					}else{
						order.setSeaGap(cnAndPoStock+(order.getSeaGap()==null?0:order.getSeaGap()));
					}
				}
			}
			
			if(namePoMap!=null&&namePoMap.size()>0){
				for(Date poDate:poDateList){
					if(poDate.before(endDate)||poDate.equals(endDate)){
						Map<String,Integer> temp=namePoMap.get(poDate);
						if(temp.get("total")>0){
							Date tempDate=DateUtils.addDays(poDate,seaDays);
							if(tempDate.before(startDate)||tempDate.equals(startDate)){
								if("ca".equals(country)){
									info="空运";
								}else if("0".equals(type)){
									info+="空转海";
								}else{
									info="海运";
								}
								info+=",最迟发货日期"+dateFormatDay.format(DateUtils.addDays(startDate,-seaDays));
								if(temp.get("total")>=gap){
									info+=dateFormatDay.format(poDate)+"PO分配"+gap+",可售至"+dateFormatDay.format(endDate)+"<br/><br/>";
									startDate=endDate;
									
									if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(startDate,-seaDays)))){
										if("ca".equals(country)){
											order.setAirGap(gap+(order.getAirGap()==null?0:order.getAirGap()));
										}else{
											order.setSeaGap(gap+(order.getSeaGap()==null?0:order.getSeaGap()));
										}
									}
									
								}else{
									startDate=DateUtils.addDays(startDate, MathUtils.roundDown(temp.get("total")*1d/daySales));
									info+=dateFormatDay.format(poDate)+"PO分配"+temp.get("total")+",可售至"+dateFormatDay.format(startDate)+"<br/><br/>";
									
									if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(startDate,-seaDays)))){
										if("ca".equals(country)){
											order.setAirGap(temp.get("total")+(order.getAirGap()==null?0:order.getAirGap()));
										}else{
											order.setSeaGap(temp.get("total")+(order.getSeaGap()==null?0:order.getSeaGap()));
										}
									}
								}
								
								gap=distributionQty2(gap,poDate,namePoMap,nameCnMap,poDateList);
							}else{
								tempDate=DateUtils.addDays(poDate,airDays);
								if(tempDate.after(startDate)){
									info+=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(tempDate)+"空运前缺货不补<br/><br/>";
									if(endDate.before(tempDate)){
										break;
									}
									info+=dateFormatDay.format(tempDate)+"~"+dateFormatDay.format(endDate)+",";
									startDate=tempDate;
								}else{
									info+=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+",";
								}
								if("ca".equals(country)){
									info="空运";
								}else if("1".equals(type)){
									info+="海转空,";
								}else{
									info="空运";
								}
								
								info+="最迟发货日期"+dateFormatDay.format(DateUtils.addDays(poDate,-airDays))+",";
								if(temp.get("total")>=gap){
									info+=dateFormatDay.format(poDate)+"PO分配"+gap+",可售至"+dateFormatDay.format(endDate)+"<br/><br/>";
									startDate=endDate;
									
									if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(poDate,-airDays)))){
										order.setAirGap(gap+(order.getAirGap()==null?0:order.getAirGap()));
									}
									
								}else{
									startDate=DateUtils.addDays(startDate, MathUtils.roundDown(temp.get("total")*1d/daySales));
									info+=dateFormatDay.format(poDate)+"PO分配"+temp.get("total")+",可售至"+dateFormatDay.format(startDate)+"<br/><br/>";
									
									if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(poDate,-airDays)))){
										order.setAirGap(temp.get("total")+(order.getAirGap()==null?0:order.getAirGap()));
									}
								}
								gap=distributionQty2(gap,poDate,namePoMap,nameCnMap,poDateList);
							}
							if(gap<=0){
								break;
							}
						}
					}else{
						break;
					}
				}
			}
			if(gap>0){
			
				if("ca".equals(country)){
					Integer time=period+airDays;
					Date cmpDate=DateUtils.addDays(today,time);
					if(startDate.before(cmpDate)){
						 if(endDate.before(cmpDate)){
							 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购赶不上缺货日,";
						 }else{
							 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购只赶上部分缺货数:("+dateFormatDay.format(cmpDate)+"~"+dateFormatDay.format(endDate)+"),";
							 Integer poDays=(int)DateUtils.spaceDays(cmpDate,endDate)+1;
							 order.setPoGap(poDays*daySales+(order.getPoGap()==null?0:order.getAirGap()));
						 }
					}else{
						Date finalDate=DateUtils.addDays(startDate,-time);
						info+="空运最迟采购日期"+dateFormatDay.format(finalDate)+",";
						if(todayStr.equals(dateFormatDay.format(finalDate))){
							 order.setPoGap(gap+(order.getPoGap()==null?0:order.getAirGap()));
						}
					}
					info+="<font color='red'>"+dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+"采购缺货:"+gap+"</font><br/><br/>";
				}else{
					Integer time=period+seaDays;
					Date cmpDate=DateUtils.addDays(today,time);
					if(startDate.before(cmpDate)){
						time=period+airDays;
						cmpDate=DateUtils.addDays(today,time);
						if(startDate.before(cmpDate)){
							 if(endDate.before(cmpDate)){
								 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购赶不上缺货日,";
							 }else{
								 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购只赶上部分缺货数:("+dateFormatDay.format(cmpDate)+"~"+dateFormatDay.format(endDate)+"),";
								 Integer poDays=(int)DateUtils.spaceDays(cmpDate,endDate)+1;
								 order.setPoGap(poDays*daySales+(order.getPoGap()==null?0:order.getAirGap()));
							 }
						}else{
							Date finalDate=DateUtils.addDays(startDate,-time);
							info+="空运最迟采购日期"+dateFormatDay.format(finalDate)+",";
							if(todayStr.equals(dateFormatDay.format(finalDate))){
								 order.setPoGap(gap+(order.getPoGap()==null?0:order.getAirGap()));
							}
						}
					}else{
						Date finalDate=DateUtils.addDays(startDate,-time);
						info+="海运最迟采购日期"+dateFormatDay.format(finalDate)+",";
						if(todayStr.equals(dateFormatDay.format(finalDate))){
							 order.setPoGap(gap+(order.getPoGap()==null?0:order.getAirGap()));
						}
					}
					info+="<font color='red'>"+dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+"采购缺货:"+gap+"</font><br/><br/>";
				}
			}
		}else{
			Date start=startDate;
			Date lastDate=DateUtils.getLastDayOfMonth(startDate);
			//Integer daySales=daySalesMap.get(startMonth)/getDaysOfMonth(startDate);
			Integer daySales=0;
			if(daySalesMap.get(startMonth)!=null){
				daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(startDate));
			}else{
				daySales=day31Sales;
			}
			int days=(int)DateUtils.spaceDays(start,lastDate)+1;
			quantity=days*daySales;
			
			info+=distributionGap(order,period,country,dateFormatDay,shippingDate,type,seaDays,airDays,days*daySales,poDateList,startDate,lastDate,dateFormatMonth,daySalesMap,daySales,namePoMap,nameCnMap);
			
			start=DateUtils.addDays(lastDate, 1);
			startMonth=dateFormatMonth.format(start);
			endMonth=dateFormatMonth.format(endDate);
			while(!startMonth.equals(endMonth)){
				lastDate=DateUtils.getLastDayOfMonth(start);
				//daySales=daySalesMap.get(startMonth)/getDaysOfMonth(start);
			
				if(daySalesMap.get(startMonth)!=null){
					  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(start));
				}else{
					  daySales=day31Sales;
				}
				days=(int)DateUtils.spaceDays(start,lastDate)+1;
				quantity+=days*daySales;
				
				if("0".equals(type)||"ca".equals(country)){
					shippingDate=DateUtils.addDays(start,-airDays);
				}else{
					shippingDate=DateUtils.addDays(start,-seaDays);
				}
				
				info+=distributionGap(order,period,country,dateFormatDay,shippingDate,type,seaDays,airDays,days*daySales,poDateList,start,lastDate,dateFormatMonth,daySalesMap,daySales,namePoMap,nameCnMap);
				
				start=DateUtils.addDays(lastDate, 1);
				startMonth=dateFormatMonth.format(start);
				endMonth=dateFormatMonth.format(endDate);
			}
			//daySales=daySalesMap.get(startMonth)/getDaysOfMonth(start);
			if(daySalesMap.get(startMonth)!=null){
				  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(start));
			}else{
				  daySales=day31Sales;
			}
			days=(int)DateUtils.spaceDays(start,endDate)+1;
			quantity+=days*daySales;
			
			if("0".equals(type)||"ca".equals(country)){
				shippingDate=DateUtils.addDays(start,-airDays);
			}else{
				shippingDate=DateUtils.addDays(start,-seaDays);
			}
			
			info+=distributionGap(order,period,country,dateFormatDay,shippingDate,type,seaDays,airDays,days*daySales,poDateList,start,endDate,dateFormatMonth,daySalesMap,daySales,namePoMap,nameCnMap);
			
		}
		return info;
	}
	
	//<=date库存
	public Integer findProductQuantity(Date date,Map<Date,Map<String,Integer>> namePoMap,Map<String,Integer> nameCnMap){
		Integer quantity=0;
		if(nameCnMap!=null&&nameCnMap.get("total")!=null&&nameCnMap.get("total")>0){
			quantity=nameCnMap.get("total");
		}
		if(namePoMap!=null){
			for(Date poDate:namePoMap.keySet()){
				if(poDate.before(date)||poDate.equals(date)){
					Map<String,Integer> temp=namePoMap.get(poDate);
					if(temp.get("total")>0){
						quantity+=temp.get("total");
					}
				}
			}
		}
		
		return quantity;
	}
	
	
	public Integer findProductQuantity(Map<Date,Map<String,Integer>> namePoMap,Map<String,Integer> nameCnMap){
		Integer quantity=0;
		if(nameCnMap!=null&&nameCnMap.get("total")!=null&&nameCnMap.get("total")>0){
			quantity=nameCnMap.get("total");
		}
		if(namePoMap!=null){
			for(Date poDate:namePoMap.keySet()){
					Map<String,Integer> temp=namePoMap.get(poDate);
					if(temp.get("total")>0){
						quantity+=temp.get("total");
					}
			}
		}
		return quantity;
	}
	
	
	public String distributionQty(Integer gap,Date date,Map<Date,Map<String,Integer>> namePoMap,Map<String,Integer> nameCnMap,List<Date> poDateList){
		Integer quantity=0;
		if(nameCnMap!=null&&nameCnMap.get("total")!=null&&nameCnMap.get("total")>0){
			quantity=nameCnMap.get("total");
		}
		if(quantity>0){
			if(quantity>=gap){//中国仓数量满足缺口
				Integer countQty=0;
				for(Map.Entry<String,Integer> entry:nameCnMap.entrySet()){
					  String key=entry.getKey();
					  if(!"total".equals(key)){
						  Integer skuQty=entry.getValue();
						  if(skuQty>0){
							  countQty+=skuQty;//10 3       use 1 r 2 
							  if(countQty>=gap){//13 11 
								  nameCnMap.put(key,countQty-gap);
								  break;
							  }else{
								  nameCnMap.put(key,0);
							  }
						  }
					  }
				}
				nameCnMap.put("total",quantity-gap);
				return "中国仓分配缺口数"+gap+"<br/><br/>";
			}else{
				for(Map.Entry<String,Integer> entry:nameCnMap.entrySet()){
					nameCnMap.put(entry.getKey(),0);
				}
			}
		}
		String info="";
		if(quantity>0){
			info="中国仓分配缺口数"+quantity;
		}
		gap=gap-quantity;
		Integer countQty=0;
		boolean flag=false;
		for(Date poDate:poDateList){
			if(poDate.before(date)||poDate.equals(date)){
				Map<String,Integer> temp=namePoMap.get(poDate);
				Integer useQty=0;
				for(Map.Entry<String,Integer> entry:temp.entrySet()){
					String key=entry.getKey();
					if(!"total".equals(key)){
						Integer skuQty=entry.getValue();
						if(skuQty>0){
							   countQty+=skuQty;
							  if(countQty>=gap){
								  useQty+=(gap-countQty+skuQty);
								  temp.put(key,countQty-gap);
								  flag=true;
								  break;
							  }else{
								  temp.put(key,0);
								  useQty+=skuQty;
							  } 
						}
					}
				}
			    temp.put("total",temp.get("total")-useQty);
			    if(flag){
			    	break;
			    }
			}	
		}
		if(flag){
			if(StringUtils.isNotBlank(info)){
				info+=info+",PO分配缺口数"+gap+"<br/><br/>";
			}else{
				info+="PO分配缺口数"+gap+"<br/><br/>";
			}
			return info;
		}
		if(countQty>0){
			info+=",PO分配缺口数"+countQty;
		}
		if(gap-countQty>0){
			info+=",还剩缺口没有库存分配"+(gap-countQty);
		}
		return  info;
	}
	
	
	public Integer distributionQty2(Integer gap,Date date,Map<Date,Map<String,Integer>> namePoMap,Map<String,Integer> nameCnMap,List<Date> poDateList){
		Integer quantity=0;
		if(nameCnMap!=null&&nameCnMap.get("total")!=null&&nameCnMap.get("total")>0){
			quantity=nameCnMap.get("total");
		}
		if(quantity>0){
			if(quantity>=gap){//中国仓数量满足缺口
				Integer countQty=0;
				for(Map.Entry<String,Integer> entry:nameCnMap.entrySet()){
					  String key=entry.getKey();
					  if(!"total".equals(key)){
						  Integer skuQty=entry.getValue();
						  if(skuQty>0){
							  countQty+=skuQty;//10 3       use 1 r 2 
							  if(countQty>=gap){//13 11 
								  nameCnMap.put(key,countQty-gap);
								  break;
							  }else{
								  nameCnMap.put(key,0);
							  }
						  }
					  }
				}
				nameCnMap.put("total",quantity-gap);
			}else{
				for(Map.Entry<String,Integer> entry:nameCnMap.entrySet()){
					nameCnMap.put(entry.getKey(),0);
				}
			}
		}
		gap=gap-quantity;
		Integer countQty=0;
		boolean flag=false;
		for(Date poDate:poDateList){
			if(poDate.before(date)||poDate.equals(date)){
				Map<String,Integer> temp=namePoMap.get(poDate);
				Integer useQty=0;
				for(Map.Entry<String,Integer> entry:temp.entrySet()){
					String key=entry.getKey();
					if(!"total".equals(key)){
						Integer skuQty=entry.getValue();
						if(skuQty>0){
							   countQty+=skuQty;
							  if(countQty>=gap){
								  useQty+=(gap-countQty+skuQty);
								  temp.put(key,countQty-gap);
								  flag=true;
								  break;
							  }else{
								  temp.put(key,0);
								  useQty+=skuQty;
							  } 
						}
					}
				}
			    temp.put("total",temp.get("total")-useQty);
			    if(flag){
			    	break;
			    }
			}	
		}
		return gap-countQty;
	}
	
	
	
}