package com.springrain.erp.modules.amazoninfo.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.springrain.erp.modules.amazoninfo.entity.AmazonCatalogRank;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPromotionsWarning;
import com.springrain.erp.modules.amazoninfo.entity.OutsidePromotion;
import com.springrain.erp.modules.amazoninfo.entity.OutsidePromotionDto;
import com.springrain.erp.modules.amazoninfo.entity.OutsidePromotionWebsite;
import com.springrain.erp.modules.amazoninfo.service.AmazonCatalogRankService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPromotionsWarningService;
import com.springrain.erp.modules.amazoninfo.service.OutsidePromotionService;
import com.springrain.erp.modules.amazoninfo.service.ProductPriceService;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;

@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/outsidePromotion")
public class OutsidePromotionController extends BaseController {

	@Autowired
	private OutsidePromotionService outsidePromotionService;
	@Autowired
	private AmazonPromotionsWarningService    promotionsService;
	@Autowired
	private PsiInventoryFbaService            fbaService;
	@Autowired
	private AmazonCatalogRankService          rankService;
	@Autowired
	private ProductPriceService               priceService;
	
	private SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd");

	@ModelAttribute
	public OutsidePromotion get(@RequestParam(required = false) Integer id) {
		if (id != null) {
			return outsidePromotionService.get(id);
		} else {
			return new OutsidePromotion();
		}
	}
	
	@RequestMapping(value = { "list", "" })
	public String list(OutsidePromotion outsidePromotion,String isCheck, HttpServletRequest request,HttpServletResponse response, Model model) throws ParseException {
		if(outsidePromotion.getEndDate()!=null){
			outsidePromotion.setEndDate(sdf.parse(sdf.format(new Date())));
			outsidePromotion.setStartDate(DateUtils.addMonths(outsidePromotion.getEndDate(), -1));
		}
		Page<OutsidePromotion> page = new Page<OutsidePromotion>(request, response);
		outsidePromotionService.find(page, outsidePromotion,isCheck);
		
		model.addAttribute("isCheck", isCheck);
		model.addAttribute("page", page);
		model.addAttribute("outside", outsidePromotion);
		return "modules/amazoninfo/outsidePromotionList";
	}
	
	@RequestMapping(value = {"refresh"})
	public String refresh(OutsidePromotion outsidePromotion, Model model, HttpServletRequest request,HttpServletResponse response,RedirectAttributes redirectAttributes) throws ParseException {
		outsidePromotionService.refresh(outsidePromotion.getPromoWarning().getId());
		addMessage(redirectAttributes, "刷新开始结束日期成功！！！");
		return "redirect:" + Global.getAdminPath() + "/amazoninfo/outsidePromotion/list";
	}

	@RequestMapping(value = "view")
	public String view(OutsidePromotion outsidePromotion, Model model) {
		model.addAttribute("outsidePromotion", outsidePromotion);
		return "modules/amazoninfo/outsidePromotionView";
	}
	
	@ResponseBody
	@RequestMapping(value = "getProInfo")
	public String getProInfo(Integer id, Model model) {
		AmazonPromotionsWarning promotion = this.promotionsService.get(id);
		if(promotion!=null){
			return promotion.toJson();
		}else{
			return "";
		}
		
	}
	
	
	@RequestMapping(value = {"analysis"})
	public String analysis(OutsidePromotion outsidePromotion, Model model) throws ParseException {
		Date today = sdf.parse(sdf.format(new Date()));
		String currency="USD";
		//查询是否endDate
		Date startDate = outsidePromotion.getStartDate();
		Date endDate   =outsidePromotion.getEndDate();
		//如果end时间为空，查折扣主表，获得end时间// 成功结束折扣于2016-06-10 16:46
		if(outsidePromotion.getEndRealDate()==null){
			AmazonPromotionsWarning warn= outsidePromotion.getPromoWarning();
			if(StringUtils.isNotEmpty(warn.getRemark())&&warn.getRemark().contains("成功结束折扣于")){
				String dateStr=warn.getRemark();
				if(warn.getRemark().contains(",")){
					dateStr=warn.getRemark().split(",")[1];
				}
				dateStr= dateStr.replace("成功结束折扣于", "").trim();
				endDate=sdf.parse(dateStr);
				outsidePromotion.setEndRealDate(endDate);
				outsidePromotionService.save(outsidePromotion);
			}
		}else{
			endDate=outsidePromotion.getEndRealDate();
		}
		
		//设置
		Date beforeStartDate30 =DateUtils.addDays(startDate, -30); 
		Date afterEndDate7   =DateUtils.addDays(endDate, 7);
		//如果结束天加7天大于今天，取今天
		if(afterEndDate7.after(today)){
			afterEndDate7=today;
		}
		String country = outsidePromotion.getCountry();
		String asin    = outsidePromotion.getAsin();
		String productName = outsidePromotion.getProductName();
		String trackId     = outsidePromotion.getTrackId();
		//查询跟踪id，产品信息
		
		List<Object[]> productInfos =this.outsidePromotionService.getProductInfoByTrackId(trackId);
		Map<String,String>  proMap = Maps.newHashMap();
		Map<String,String>  asinMap = Maps.newHashMap();
		for(Object[] obj:productInfos){
			String proName = obj[1].toString();
			String tempAsin = obj[0].toString();
			proMap.put(proName,tempAsin);
			asinMap.put(tempAsin, proName);
		}
		//根据跟踪id查询站外促销的站点信息
		List<OutsidePromotionWebsite> websites =this.outsidePromotionService.findWebsite(trackId);
		Set<String> promoDates = Sets.newHashSet();
		for(OutsidePromotionWebsite website: websites){
			promoDates.add(sdf.format(website.getPromoDate()));
		}
		//获取排名
		Map<Date,List<AmazonCatalogRank>> rankMap =rankService.getCatalogRank(asin,country,beforeStartDate30, afterEndDate7);
		//获取库存
		Map<Date,Integer>  inventoryMap = fbaService.getFbaInventoryByAsinCountry(country, asin, beforeStartDate30, afterEndDate7);
		//获取流量转化率
		Map<Date,Map<String,String>>   sessionMap   = this.outsidePromotionService.getSessionByAsinCountry(country, asinMap.keySet(), beforeStartDate30, afterEndDate7);
		//获取当天销量
		Map<Date,Map<String,String>>   saleMap      = this.outsidePromotionService.getSalesByAsinCountry(proMap.keySet(), country, beforeStartDate30, afterEndDate7);
		//获取当天(促销)销量
		Map<Date,Map<String,String>>   saleProMap   = this.outsidePromotionService.getSalesByAsinCountryPro(asinMap.keySet(), country, beforeStartDate30, afterEndDate7,trackId.replaceAll("&amp;", "&"));
		//获取该产品的保本价
		Map<String,Float>  priceMap     = priceService.findAllProducSalePrice(currency);
		
		
		String oldCurrency="EUR";
		if("de,it,es,fr".contains(country)){
			oldCurrency="EUR";
		}else if("uk".equals(country)){
			oldCurrency="GBP";
		}else if("com".equals(country)){
			oldCurrency="USD";
		}else if("ca".equals(country)){
			oldCurrency="CAD";
		}else if("jp".equals(country)){
			oldCurrency="JPY";
		}
		Float rate=MathUtils.getRate(oldCurrency, currency,null);
		
		Date dataDate=  DateUtils.addDays(startDate, -7);
		Map<Long,OutsidePromotionDto>  rsMap = Maps.newTreeMap();
		List<String>  productList = Lists.newArrayList();
		
		
		productList.add(productName);
		for (Map.Entry<String,String> entry : asinMap.entrySet()) { 
		    String name= entry.getValue();
			if(!name.equals(productName)){
				productList.add(name);
			}
		}
		
		//组合图表内容
		Map<String,String>  saleQuantityMap = Maps.newLinkedHashMap();   //key:日期   value：自然销量，促销销量
		List<String> axis = Lists.newArrayList();
		while(!dataDate.after(afterEndDate7)){
			List<Integer> 		sessions=Lists.newArrayList();
			List<Float> 		conversions=Lists.newArrayList();
			List<Integer> 		saleQuantitys=Lists.newArrayList();
			List<Integer> 		saleQuantityPros=Lists.newArrayList();
			
			Float   saleAmount=0f;		//总的销售额
			Float   saleAmountPro=0f;	//促销销售额
			Float   discount=0f;
			Float   unitPrice=0f;
			Integer saleQ=0;
			Integer saleQPro=0;
			Integer session=0;
			Float   cone=0f;
			if(sessionMap.get(dataDate)!=null){
				Map<String,String> inMap = sessionMap.get(dataDate);
				for(String proName:productList){
					Integer sessionTemp=0;
					Integer orderQ =0;
					Float  convTemp=0f;
					String tempAsin = proMap.get(proName);
					String res =inMap.get(tempAsin);
					if(StringUtils.isNotEmpty(res)){
						String arr[] =res.split(",");
						sessionTemp = Integer.parseInt(arr[0]);
						orderQ = Integer.parseInt(arr[1]);
						if(sessionTemp.intValue()!=0){
							convTemp=Math.round(orderQ*10000f/sessionTemp)*0.01f;
						}
					}
					if(productName.equals(proName)){
						session=sessionTemp;
						cone=convTemp;
					}
					sessions.add(sessionTemp);
					conversions.add(convTemp);
				}
			}
			if(saleMap.get(dataDate)!=null){
				Map<String,String> inMap = saleMap.get(dataDate);
				for(String proName:productList){
					Integer saleQuantity=0;
					String res =inMap.get(proName);
					if(StringUtils.isNotEmpty(res)){
						String arr[] =inMap.get(proName).split(",,");
						saleQuantity =Integer.parseInt(arr[0]);
					//	if(proName.equals(proName)){
						if(productName.equals(proName)){
							saleQ =saleQuantity;
							saleAmount=Float.parseFloat(arr[1]);
							if(saleQ.intValue()!=0){
								unitPrice=saleAmount/saleQ;
							}
						}
					}
					saleQuantitys.add(saleQuantity);
				}
				
			}
			
			if(saleProMap.get(dataDate)!=null){
				Map<String,String> inMap = saleProMap.get(dataDate);
				for(String proName:productList){
					Integer saleQuantityPro=0;
					String tempAsin = proMap.get(proName);
					String res =inMap.get(tempAsin);
					if(StringUtils.isNotEmpty(res)){
						String arr[] =res.split(",,");
						saleQuantityPro =Integer.parseInt(arr[0]);
						if(productName.equals(proName)){
							saleQPro=saleQuantityPro;
							saleAmountPro=Float.parseFloat(arr[1]);
							discount=Float.parseFloat(arr[2]);
						}
					}
					saleQuantityPros.add(saleQuantityPro);
				}
				
			}
			
			//如果是促销前一和促销后一周
			Float productPrice =priceMap.get(productName+"_"+country);
			rsMap.put(DateUtils.spaceDays(dataDate, afterEndDate7), new OutsidePromotionDto(inventoryMap.get(dataDate), rankMap.get(dataDate), session, cone,saleQ,
					unitPrice*rate,saleAmount*rate,(unitPrice*rate-productPrice)*saleQ==0?0:(unitPrice*rate-productPrice)*saleQ,
					discount*rate,dataDate,saleQPro,saleAmountPro*rate,sdf.format(dataDate), sessions,conversions,saleQuantityPros,saleQuantitys));
			saleQuantityMap.put(sdf.format(dataDate), (saleQ-saleQPro)+","+saleQPro);
			axis.add("'"+sdf.format(dataDate)+"'");
			dataDate=DateUtils.addDays(dataDate, 1);
		}
		
		model.addAttribute("axis", axis);
		model.addAttribute("saleQuantityMap", saleQuantityMap);
		model.addAttribute("productList", productList);
		model.addAttribute("websites", websites);
		model.addAttribute("promoDates", promoDates);
		model.addAttribute("outsidePromotion", outsidePromotion);
		model.addAttribute("rsMap", rsMap);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		return "modules/amazoninfo/outsidePromotionAnalysis";
	}
	
	

	@RequestMapping(value = {"compare"})
	public String compare(OutsidePromotion outsidePromotion, Model model) throws ParseException {
		Date today = sdf.parse(sdf.format(new Date()));
		String currency="USD";
		//查询是否endDate
		Date startDate = outsidePromotion.getStartDate();
		Date endDate   =outsidePromotion.getEndDate();
		//如果end时间为空，查折扣主表，获得end时间// 成功结束折扣于2016-06-10 16:46
		if(outsidePromotion.getEndRealDate()==null){
			AmazonPromotionsWarning warn= outsidePromotion.getPromoWarning();
			if(StringUtils.isNotEmpty(warn.getRemark())&&warn.getRemark().contains("成功结束折扣于")){
				String dateStr= warn.getRemark().replace("成功结束折扣于", "").trim();
				endDate=sdf.parse(dateStr);
				outsidePromotion.setEndRealDate(endDate);
				outsidePromotionService.save(outsidePromotion);
			}
		}else{
			endDate=outsidePromotion.getEndRealDate();
		}
		
		//设置
		Date beforeStartDate30 =DateUtils.addDays(startDate, -30); 
		Date afterEndDate7   =DateUtils.addDays(endDate, 7);
		//如果结束天加7天大于今天，取今天
		if(afterEndDate7.after(today)){
			afterEndDate7=today;
		}
		String country = outsidePromotion.getCountry();
		String asin    = outsidePromotion.getAsin();
		String productName = outsidePromotion.getProductName();
		String trackId     = outsidePromotion.getTrackId();
		//查询跟踪id，产品信息
		
		List<Object[]> productInfos =this.outsidePromotionService.getProductInfoByTrackId(trackId);
		Map<String,String>  proMap = Maps.newHashMap();
		Map<String,String>  asinMap = Maps.newHashMap();
		for(Object[] obj:productInfos){
			String proName = obj[1].toString();
			String tempAsin = obj[0].toString();
			proMap.put(proName,tempAsin);
			asinMap.put(tempAsin, proName);
		}
		//根据跟踪id查询站外促销的站点信息
		List<OutsidePromotionWebsite> websites =this.outsidePromotionService.findWebsite(trackId);
		Set<String> promoDates = Sets.newHashSet();
		for(OutsidePromotionWebsite website: websites){
			promoDates.add(sdf.format(website.getPromoDate()));
		}
		//获取排名
		Map<Date,List<AmazonCatalogRank>> rankMap =rankService.getCatalogRank(asin,country,beforeStartDate30, afterEndDate7);
		//获取库存
		Map<Date,Integer>  inventoryMap = fbaService.getFbaInventoryByAsinCountry(country, asin, beforeStartDate30, afterEndDate7);
		//获取流量转化率
		Map<Date,Map<String,String>>   sessionMap   = this.outsidePromotionService.getSessionByAsinCountry(country, asinMap.keySet(), beforeStartDate30, afterEndDate7);
		//获取当天销量
		Map<Date,Map<String,String>>   saleMap      = this.outsidePromotionService.getSalesByAsinCountry(proMap.keySet(), country, beforeStartDate30, afterEndDate7);
		//获取当天(促销)销量
		Map<Date,Map<String,String>>   saleProMap   = this.outsidePromotionService.getSalesByAsinCountryPro(asinMap.keySet(), country, beforeStartDate30, afterEndDate7,trackId.replaceAll("&amp;", "&"));
		//获取该产品的保本价
		Map<String,Float>  priceMap     = priceService.findAllProducSalePrice(currency);
		
		
		String oldCurrency="EUR";
		if("de,it,es,fr".contains(country)){
			oldCurrency="EUR";
		}else if("uk".equals(country)){
			oldCurrency="GBP";
		}else if("com".equals(country)){
			oldCurrency="USD";
		}else if("ca".equals(country)){
			oldCurrency="CAD";
		}else if("jp".equals(country)){
			oldCurrency="JPY";
		}
		Float rate=MathUtils.getRate(oldCurrency, currency,null);
		
		Date dataDate=  DateUtils.addDays(startDate, -7);
		Map<Long,OutsidePromotionDto>  rsMap = Maps.newTreeMap();
		List<String>  productList = Lists.newArrayList();
		
		
		productList.add(productName);
		for (Map.Entry<String,String> entry : asinMap.entrySet()) {
			String name=entry.getValue();
			if(!name.equals(productName)){
				productList.add(name);
			}
		}
		
		
		while(!dataDate.after(afterEndDate7)){
			List<Integer> 		sessions=Lists.newArrayList();
			List<Float> 		conversions=Lists.newArrayList();
			List<Integer> 		saleQuantitys=Lists.newArrayList();
			List<Integer> 		saleQuantityPros=Lists.newArrayList();
			
			Float   saleAmount=0f;		//总的销售额
			Float   saleAmountPro=0f;	//促销销售额
			Float   discount=0f;
			Float   unitPrice=0f;
			Integer saleQ=0;
			Integer saleQPro=0;
		
			if(sessionMap.get(dataDate)!=null){
				Map<String,String> inMap = sessionMap.get(dataDate);
				for(String proName:productList){
					Integer session=0;
					Integer orderQ =0;
					Float  conv=0f;
					String tempAsin = proMap.get(proName);
					String res =inMap.get(tempAsin);
					if(StringUtils.isNotEmpty(res)){
						String arr[] =res.split(",");
						session = Integer.parseInt(arr[0]);
						orderQ = Integer.parseInt(arr[1]);
						if(session.intValue()!=0){
							conv=Math.round(orderQ*10000f/session)*0.01f;
						}
					}
					sessions.add(session);
					conversions.add(conv);
				}
			}
			if(saleMap.get(dataDate)!=null){
				Map<String,String> inMap = saleMap.get(dataDate);
				for(String proName:productList){
					Integer saleQuantity=0;
					String res =inMap.get(proName);
					if(StringUtils.isNotEmpty(res)){
						String arr[] =inMap.get(proName).split(",,");
						saleQuantity =Integer.parseInt(arr[0]);
						if(productName.equals(proName)){
							saleQ =saleQuantity;
							saleAmount=Float.parseFloat(arr[1]);
							if(saleQ.intValue()!=0){
								unitPrice=saleAmount/saleQ;
							}
						}
					}
					saleQuantitys.add(saleQuantity);
				}
				
			}
			
			if(saleProMap.get(dataDate)!=null){
				Map<String,String> inMap = saleProMap.get(dataDate);
				for(String proName:productList){
					Integer saleQuantityPro=0;
					String tempAsin = proMap.get(proName);
					String res =inMap.get(tempAsin);
					if(StringUtils.isNotEmpty(res)){
						String arr[] =res.split(",,");
						saleQuantityPro =Integer.parseInt(arr[0]);
						if(productName.equals(proName)){
							saleQPro=saleQuantityPro;
							saleAmountPro=Float.parseFloat(arr[1]);
							discount=Float.parseFloat(arr[2]);
						}
					}
					saleQuantityPros.add(saleQuantityPro);
				}
				
			}
			
			//如果是促销前一和促销后一周
			Float productPrice =priceMap.get(productName+"_"+country);
			rsMap.put(DateUtils.spaceDays(dataDate, afterEndDate7), new OutsidePromotionDto(inventoryMap.get(dataDate), rankMap.get(dataDate), null, null,saleQ,
					unitPrice*rate,saleAmount*rate,(unitPrice*rate-productPrice)*saleQ==0?0:(unitPrice*rate-productPrice)*saleQ,
					discount*rate,dataDate,null,saleAmountPro*rate,sdf.format(dataDate), sessions,conversions,saleQuantityPros,saleQuantitys));
			dataDate=DateUtils.addDays(dataDate, 1);
		}
		
		
		model.addAttribute("productList", productList);
		model.addAttribute("websites", websites);
		model.addAttribute("promoDates", promoDates);
		model.addAttribute("outsidePromotion", outsidePromotion);
		model.addAttribute("rsMap", rsMap);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		return "modules/amazoninfo/outsidePromotionCompare";
	}
	
	@RequestMapping(value = {"add"})
	public String add(OutsidePromotion outsidePromotion, Model model) throws ParseException {
		if(StringUtils.isEmpty(outsidePromotion.getCountry())){  
			outsidePromotion.setCountry("de");//新建默认德国
		}
		Map<Integer,String> promotionMap =promotionsService.getAtcivePromotions(outsidePromotion.getCountry());
		model.addAttribute("promotionMap", promotionMap);
		model.addAttribute("outsidePromotion", outsidePromotion);
		return "modules/amazoninfo/outsidePromotionForm";
	}
	
	
	@RequestMapping(value = {"edit"})
	public String edit(OutsidePromotion outsidePromotion, Model model) throws ParseException {
		List<OutsidePromotionWebsite> webSites=this.outsidePromotionService.findWebsite(outsidePromotion.getTrackId());
		model.addAttribute("webSites", webSites);
		model.addAttribute("outsidePromotion", outsidePromotion);
		return "modules/amazoninfo/outsidePromotionEdit";
	}
	
	@RequestMapping(value = "save")
	public String save(OutsidePromotion outsidePromotion,RedirectAttributes redirectAttributes) {
		outsidePromotionService.addSave(outsidePromotion);
		addMessage(redirectAttributes, "站外促销分析保存成功！");
		return "redirect:" + Global.getAdminPath() + "/amazoninfo/outsidePromotion/list";
	}
	
	
	@RequestMapping(value = "editSave")
	public String editSave(OutsidePromotion outsidePromotion,RedirectAttributes redirectAttributes) {
		outsidePromotionService.editSave(outsidePromotion);
		addMessage(redirectAttributes, "更新促销站点成功！");
		return "redirect:" + Global.getAdminPath() + "/amazoninfo/outsidePromotion/list";
	}
	
	

	@RequestMapping(value = "delete")
	public String delete(OutsidePromotion outsidePromotion,	RedirectAttributes redirectAttributes) {
		outsidePromotion.setDelFlag("1");
		outsidePromotionService.save(outsidePromotion);
		addMessage(redirectAttributes, "站外促销分析删除成功！");
		return "redirect:" + Global.getAdminPath() + "/amazoninfo/outsidePromotion/list";
	}
	
	
	
}
