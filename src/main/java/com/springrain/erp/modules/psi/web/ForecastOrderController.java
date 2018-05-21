/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecastByMonth;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.SalesForecastServiceByMonth;
import com.springrain.erp.modules.psi.entity.ForecastOrder;
import com.springrain.erp.modules.psi.entity.ForecastOrderItem;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductAttribute;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.service.ForecastOrderItemService;
import com.springrain.erp.modules.psi.service.ForecastOrderService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiMarketingPlanService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.utils.UserUtils;
/**
 * 预测订单Controller
 * @author Michael
 * @version 2016-2-26
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/forecastOrder")
public class ForecastOrderController extends BaseController {
	@Autowired
	private ForecastOrderService	 	    forecastOrderService;
	@Autowired
	private PsiProductService	 	        productService;
	@Autowired
	private PsiInventoryService             psiInventoryService;
	@Autowired
	private SaleReportService               saleReportService;
	@Autowired
	private PsiProductAttributeService      psiProductAttributeService;
	@Autowired
	private PsiProductEliminateService      psiProductEliminateService;
	@Autowired
	private ForecastOrderItemService        forecastItemService;
	@Autowired
	private SalesForecastServiceByMonth     salesForecastService;
	@Autowired
	private PsiProductTypeGroupDictService  groupDictService;
	@Autowired
	private PsiMarketingPlanService   		planService;
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"list", ""})
	public String list(ForecastOrder forecastOrder,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(forecastOrder.getCreateDate()==null){
			forecastOrder.setCreateDate(sdf.parse(sdf.format(DateUtils.addMonths(new Date(),-1))));
		}
		if(forecastOrder.getUpdateDate()==null){
			forecastOrder.setUpdateDate(sdf.parse(sdf.format(new Date())));
		}
		
		Date startDate=sdf.parse(sdf.format(DateUtils.getMonday(new Date())));
		Boolean canFlag=this.forecastOrderService.hasLastWeekOrder(startDate);
		Page<ForecastOrder> page = forecastOrderService.find(new Page<ForecastOrder>(request, response), forecastOrder); 
		
		//如果离月底还差3天以内
		Date today = sdf.parse(sdf.format(new Date()));
		long space=DateUtils.spaceDays(today,DateUtils.getLastDayOfMonth(today));
		if(space<=3){
			model.addAttribute("endMonthFlag", "1");
		}else{
			model.addAttribute("endMonthFlag", "0");
		}
		model.addAttribute("canFlag", canFlag);
		model.addAttribute("page", page);
		return "modules/psi/forecastOrderList";
	}

	@RequestMapping(value = {"edit"})
	public String edit(ForecastOrder forecastOrder,String lineId,String country,String nameColor,String isCheck,String isMain,String isNew,String isPriceChange,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		getData(forecastOrder,model,country,nameColor,lineId,isCheck,isMain,isNew,isPriceChange);
		return "modules/psi/forecastOrderEdit";
	}
	
	@RequestMapping(value = {"review"})
	public String review(ForecastOrder forecastOrder,String lineId,String country,String nameColor,String isCheck,String isMain,String isNew,String isPriceChange,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		getData(forecastOrder,model,country,nameColor,lineId,isCheck,isMain,isNew,isPriceChange);
		return "modules/psi/forecastOrderReview";
	}
	
	@RequiresPermissions("psi:forecastOrder:overReview")
	@RequestMapping(value = {"overReview"})
	public String overReview(ForecastOrder forecastOrder,String lineId,String country,String nameColor,String isCheck,String isMain,String isNew,String isPriceChange,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		getData(forecastOrder,model,country,nameColor,lineId,isCheck,isMain,isNew,isPriceChange);
		return "modules/psi/forecastOrderOverReview";
	}
	
	
	@RequestMapping(value = {"view"})
	public String view(ForecastOrder forecastOrder,String lineId,String country,String nameColor,String isCheck,String isMain,String isNew,String isPriceChange,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		getData(forecastOrder,model,country,nameColor,lineId,isCheck,isMain,isNew,isPriceChange);
		return "modules/psi/forecastOrderView";
	}
	
	
	@RequestMapping(value = {"bossReview"})
	public String bossReview(ForecastOrder forecastOrder,String lineId,String country,String nameColor,String isCheck,String isMain,String isNew,String isPriceChange,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		getData(forecastOrder,model,country,nameColor,lineId,isCheck,isMain,isNew,isPriceChange);
		return "modules/psi/forecastOrderBossReview";
	}
	
	
	public void getData(ForecastOrder forecastOrder,Model model,String countryCode,String nameColor,
			String lineId,String isCheck,String isMain,String isNew,String isPriceChange){
		if(StringUtils.isEmpty(isCheck)){
			isCheck="0";
		}
		List<PsiProductTypeGroupDict> lineList=groupDictService.getAllList();
		if(forecastOrder.getId()==null){
			return;
		}
		forecastOrder = forecastOrderService.get(forecastOrder.getId()); 
		Map<Integer,String> supMap = this.forecastOrderService.getSupplierMap();
		Map<String,String> orderDateMap=this.forecastOrderService.getLastOrderDate();
		List<PsiProduct>  products = productService.findAll();
		Map<String,String> promotionTips=this.forecastOrderService.getLast2MonthPromotionQ(forecastOrder.getCreateDate());
		if(StringUtils.isNotEmpty(countryCode)){
			for (Iterator<ForecastOrderItem> iterator = forecastOrder.getItems().iterator(); iterator.hasNext();) {
				ForecastOrderItem item = iterator.next();
				if(!countryCode.equals(item.getCountryCode())){
					iterator.remove();
				}
			}
		}
		
		if(StringUtils.isNotEmpty(nameColor)){
			for (Iterator<ForecastOrderItem> iterator = forecastOrder.getItems().iterator(); iterator.hasNext();) {
				ForecastOrderItem item = iterator.next();
				String tempC=item.getProductNameColor();
				if(!nameColor.equals(tempC)){
					iterator.remove();
				}
			}
		}
		
		
		if(StringUtils.isNotEmpty(lineId)){
			 Map<String,Set<Integer>> lineMap =groupDictService.getLineProductIds();
			 Set<Integer> proIds = lineMap.get(lineId);
			 if(proIds!=null&&proIds.size()>0){
				 for(Iterator<ForecastOrderItem> iterator = forecastOrder.getItems().iterator(); iterator.hasNext();) {
						ForecastOrderItem item = iterator.next();
						if(!proIds.contains(item.getProduct().getId())){
							iterator.remove();
						}
				}
			 }
		}
		
		if("1".equals(isCheck)){
			for (Iterator<ForecastOrderItem> iterator = forecastOrder.getItems().iterator(); iterator.hasNext();) {
				ForecastOrderItem item = iterator.next();
				Integer quantity=item.getQuantity();
				if(quantity==null||quantity.intValue()==0){
					iterator.remove();
				}
			}
		}
		
		if(StringUtils.isNotEmpty(isMain)){
			for (Iterator<ForecastOrderItem> iterator = forecastOrder.getItems().iterator(); iterator.hasNext();) {
				ForecastOrderItem item = iterator.next();
				if(!isMain.equals(item.getIsMain())){
					iterator.remove();
				}
			}
		}
		
		if("1".equals(isPriceChange)){
			for (Iterator<ForecastOrderItem> iterator = forecastOrder.getItems().iterator(); iterator.hasNext();) {
				ForecastOrderItem item = iterator.next();
				if(StringUtils.isEmpty(item.getPriceChange())){
					iterator.remove();
				}
			}
		}
		
		if("1".equals(isNew)){
			for (Iterator<ForecastOrderItem> iterator = forecastOrder.getItems().iterator(); iterator.hasNext();) {
				ForecastOrderItem item = iterator.next();
				if("0".equals(item.getIsNew())){
					iterator.remove();
				}
			}
		}
		
		List<PsiProductAttribute> attrs = psiProductAttributeService.findAll();
		Map<String,PsiProductAttribute> productAttr = Maps.newHashMap();
		for (PsiProductAttribute psiProductAttribute : attrs) {
			productAttr.put(psiProductAttribute.getColorName(), psiProductAttribute);
		}
		//运输方式和缓冲周期
		model.addAttribute("productTranTypeAndBP", psiProductEliminateService.findProductAttr());
		
		//查询是否带电
		List<Object> hasPowers=this.saleReportService.hasPowerProducts();
		model.addAttribute("hasPowers", hasPowers);
		model.addAttribute("fanOuFlag", psiProductEliminateService.findProductFanOuFlag());
		Map<String, String> positionMap = psiProductEliminateService.findProductPositionByCountry(null);
		model.addAttribute("positionMap", positionMap);

		//查询产品颜色
		List<Object[]> list=this.forecastOrderService.getCanAddInfos(forecastOrder.getId(),countryCode,nameColor);
		Map<String,List<String>>  proCountryMap = Maps.newHashMap();
		Map<String,String>  itemMap = Maps.newHashMap();
		for(Object[] obj:list){
			Integer productId  = Integer.parseInt(obj[0].toString());
			String productName = obj[1].toString().split(" ")[1];
			String color       = obj[2].toString();
			String country     = obj[3].toString();
			Integer id         = Integer.parseInt(obj[4].toString());
			if(StringUtils.isNotEmpty(color)){
				productName=productName+"_"+color;
			}
			List<String> inList = null; 
			if(proCountryMap.get(productName)==null){
				inList=Lists.newArrayList();
			}else{
				inList=proCountryMap.get(productName);
			}
			inList.add(country);
			itemMap.put(productName+","+country, id+","+productId);
			proCountryMap.put(productName, inList);
		}
		//控制数量修改标记,运营只能编辑新品单数量,供应链只能编辑非新品单数量(但是能互相编辑备注)
		boolean canEdit = false;
		if ("1".equals(forecastOrder.getType()) && "3".equals(UserUtils.getUser().getOffice().getId())) {
			canEdit = true;
		}else if ("0".equals(forecastOrder.getType()) && "9".equals(UserUtils.getUser().getOffice().getId())) {
			canEdit = true;
		}
		model.addAttribute("canEdit", canEdit);
		
		//查询促销数据
//		Date  orderDate = forecastOrder.getTargetDate()!=null?forecastOrder.getTargetDate():(forecastOrder.getCreateDate()!=null?forecastOrder.getCreateDate():new Date());
//		String curWeek=DateUtils.getWeekStr(orderDate, new SimpleDateFormat("yyyyww"), 4, "");
//		Map<String, Map<String, Integer>> planMap = planService.getMarketingPlanQuantity(curWeek);
		Map<String,String> alertPriceMap = this.forecastOrderService.getDiffPriceMap(); 
		
		Map<String,Float> saleMonth =this.forecastOrderService.getSaleMonth();
		model.addAttribute("saleMonth",saleMonth);
		model.addAttribute("isCheck",isCheck);
		model.addAttribute("isMain",isMain);
		model.addAttribute("isNew",isNew);
		model.addAttribute("isPriceChange",isPriceChange);
//		model.addAttribute("planMap",planMap);
		model.addAttribute("alertPriceMap",alertPriceMap);
		model.addAttribute("lineList",lineList);
		model.addAttribute("lineId", lineId);
		model.addAttribute("promotionTips", promotionTips);
		model.addAttribute("nameColor", nameColor);
		model.addAttribute("country", countryCode);
		model.addAttribute("itemMap", JSON.toJSON(itemMap));
		model.addAttribute("proCountryMap", JSON.toJSON(proCountryMap));
		model.addAttribute("supMap", JSON.toJSON(supMap));
		model.addAttribute("orderDateJson", JSON.toJSON(orderDateMap));
		model.addAttribute("orderDateMap", orderDateMap);
		model.addAttribute("productAttr", productAttr);
		model.addAttribute("products", products);
		model.addAttribute("forecastOrder", forecastOrder);
	}
	
	@RequestMapping(value = {"editSave"})
	public String editSave(ForecastOrder forecastOrder,HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) throws ParseException {
		if(forecastOrder.getItems()==null||forecastOrder.getItems().size()==0){
			return null;
		}
		for(ForecastOrderItem item:forecastOrder.getItems()){
			item.setForecastOrder(forecastOrder);
		}
		forecastOrder.setUpdateDate(new Date());
		forecastOrder.setUpdateUser(UserUtils.getUser());
		this.forecastOrderService.save(forecastOrder);
		addMessage(redirectAttributes, "编辑预测订单'" + forecastOrder.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/forecastOrder/?repage";
	}
	
	@RequestMapping(value = {"reviewSave"})
	public String reviewSave(ForecastOrder forecastOrder,HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) throws ParseException {
		logger.info("审核人(reviewSave)" + UserUtils.getUser().getName());
		forecastOrder = this.forecastOrderService.get(forecastOrder.getId());
		if ("1".equals(forecastOrder.getOrderSta()) && "1".equals(forecastOrder.getType())) {
			forecastOrder.setOrderSta("5");//新品订单运营审核算终极审核,不需要后续审核流程
			forecastOrder.setReviewDate(new Date());
			forecastOrder.setReviewUser(UserUtils.getUser());
			this.forecastOrderService.salesSave(forecastOrder);
			addMessage(redirectAttributes, "审核订单'" + forecastOrder.getId() + "'成功");
			return "redirect:"+Global.getAdminPath()+"/psi/forecastOrder/?repage";
		}
		Map<String,List<ForecastOrderItem>> overMap =Maps.newHashMap();
		for(ForecastOrderItem item :forecastOrder.getItems()){
			if(item.getMaxStock()!=null&&item.getMaxStock().intValue()>0){
				//如果总库存大于最大限制，进入第二步审核
				String productNameColor= item.getProductNameColor();
				List<ForecastOrderItem> list = null;
				if(overMap.get(productNameColor)==null){
					list = Lists.newArrayList();
				}else{
					list = overMap.get(productNameColor);
				}
				list.add(item);
				overMap.put(productNameColor, list);
			}
		}
		forecastOrder.setReviewDate(new Date());
		forecastOrder.setReviewUser(UserUtils.getUser());
		
		if(overMap.size()>0){
			forecastOrder.setOrderSta("3");//待超标审核
			this.forecastOrderService.reviewSave(forecastOrder,overMap);
			addMessage(redirectAttributes, "初级审核通过待超标审核，订单'" + forecastOrder.getId() + "'成功");
		}else{
			forecastOrder.setOrderSta("4");//待终极审核
			this.forecastOrderService.reviewOverSave(forecastOrder,false);
			addMessage(redirectAttributes, "初级审核通过(未超标)待终极审核，订单'" + forecastOrder.getId() + "'成功");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/forecastOrder/?repage";
	}
	
	@RequestMapping(value = {"reviewOverSave"})
	public String reviewOverSave(ForecastOrder forecastOrder,HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) throws ParseException {
		logger.info("超标审核人(reviewOverSave)" + UserUtils.getUser().getName());
		forecastOrder = this.forecastOrderService.get(forecastOrder.getId());
		if("3".equals(forecastOrder.getOrderSta())){
			forecastOrder.setReviewDate(new Date());
			forecastOrder.setReviewUser(UserUtils.getUser());
			forecastOrder.setOrderSta("4");//待终极审核
			this.forecastOrderService.reviewOverSave(forecastOrder,true);
		}
		addMessage(redirectAttributes, "超标审核通过,订单'" + forecastOrder.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/forecastOrder/?repage";
	}

	@RequestMapping(value = {"bossSave"})
	public String bossSave(ForecastOrder forecastOrder,HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) throws ParseException {
		logger.info("终极审核人(bossSave)" + UserUtils.getUser().getName());
		forecastOrder = this.forecastOrderService.get(forecastOrder.getId());
		String res=this.forecastOrderService.bossSave(forecastOrder);
		if("审核失败".equals(res)){
			addMessage(redirectAttributes, "终极审核预测订单'" + forecastOrder.getId() + "'失败");
		}else{
			addMessage(redirectAttributes, "终极审核通过订单'" + forecastOrder.getId() + "'成功,["+res+"]");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/forecastOrder/?repage";
	}
	
	
	@RequestMapping(value = {"cancel"})
	public String cancel(ForecastOrder forecastOrder,HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) throws ParseException {
		this.forecastOrderService.cancel(forecastOrder);
		addMessage(redirectAttributes, "取消预测订单'" + forecastOrder.getId() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/forecastOrder/?repage";
	}
	
	@RequestMapping(value = "generateOrder")
	public String generateOrder(String date, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) throws ParseException {
		Date targetDate = null;	//备货日期,为空按当前周计算下单
		if (StringUtils.isNotEmpty(date)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			targetDate = format.parse(date + " 23:59:59");	//为了计算天数差手动设置时分秒
		}
		//如果有未完成的订单，全部取消
		this.forecastOrderService.updateCancelSta();
		this.forecastOrderService.generateOrder(targetDate);
		addMessage(redirectAttributes, "生成预测订单数据成功");
		return "redirect:"+Global.getAdminPath()+"/psi/forecastOrder/?repage";
		
	}

	
	@ResponseBody
	@RequestMapping(value = {"updateQuantity"})
	public String updateQuantity(Integer itemId,Integer quantity,String flag) {
		 this.forecastOrderService.updateQuantity(itemId, quantity,flag);
		 if(quantity!=null){
			return this.forecastOrderService.isOver(itemId,quantity);
		 }
		 return "";
	}
	
	//销售批量改动产品数量和备注
	@ResponseBody
	@RequestMapping(value = {"batchUpdate"})
	public String batchUpdate(Integer forecastOrderId,String itemIds,Integer batchQuantity,String batchRemark) throws UnsupportedEncodingException {
			List<Integer> list = Lists.newArrayList();
			for(String itemId:itemIds.split(",")){
				if(StringUtils.isNotEmpty(itemId)){
					list.add(Integer.parseInt(itemId));
				}
			}	
			if(list.size()>0){
				 this.forecastOrderService.batchUpdate(list, batchQuantity, URLDecoder.decode(batchRemark, "UTF-8"),forecastOrderId);
				 for(Integer itemId:list){
					 this.forecastOrderService.isOver(itemId,batchQuantity);//更新是否超标
				 }
				 return "true";
			}else{
				return "false";
			}
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateRemarkAdd"})
	public String updateRemarkAdd(Integer itemId,Integer quantity,String remark,String flag) throws UnsupportedEncodingException {
			if(quantity==null){
				quantity=0;
			}
			this.forecastOrderService.updateQuantityAdd(itemId, quantity,URLDecoder.decode(remark, "UTF-8"),flag);
			if(quantity!=null&&quantity>0){
				return this.forecastOrderService.isOver(itemId,quantity);
			 }
			return "";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"updateRemarkDel"})
	public String updateRemarkDel(Integer itemId) {
		 this.forecastOrderService.updateQuantityDel(itemId);
		 return this.forecastOrderService.isOver(itemId,0);
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateRemark"})
	public String updateRemark(Integer itemId,String remark,String flag) {
		try {
			remark = URLDecoder.decode(remark, "UTF-8");
			if ("0".equals(flag)) {
				if ("3".equals(UserUtils.getUser().getOffice().getId())) {	//运营部
					remark = "运营备注:" + remark.replace("运营备注:", "");
				} else {	//供应链管理部
					remark = "供应链备注:" + remark.replace("供应链备注:", "");
				}
			}
			return this.forecastOrderService.updateRemark(itemId, remark, flag);
		} catch (UnsupportedEncodingException e) {
			return "false";
		}
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"ajaxItemData"})
	public String ajaxItemData(Integer tranId) {
		ForecastOrderItem item=this.forecastOrderService.getItemInfo(tranId);
		return item.toJson();
	}
	
	
	@RequestMapping(value = {"exportSingle"})
	public String exportSingle(Integer forecastOrderId,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException, UnsupportedEncodingException {
		ForecastOrder  order = this.forecastOrderService.get(forecastOrderId);
		List<ForecastOrderItem> forecastOrderItems =this.forecastItemService.find(forecastOrderId);
		Map<String,Integer> productTotalMap = Maps.newHashMap();
		Map<String,List<ForecastOrderItem>> productItemsMap = Maps.newTreeMap();
		for(ForecastOrderItem item:forecastOrderItems){
			String productName = item.getProductNameColor();
			Integer quantity   = item.getQuantity();
			if(!"1".equals(item.getDisplaySta())){
				productName="0,"+productName;
			}else{
				productName="1,"+productName;
			}
			
			if(productTotalMap.get(productName)!=null){
				quantity+=productTotalMap.get(productName);
			}
			
			
			List<ForecastOrderItem> items = null;
			if(productItemsMap.get(productName)==null){
				items = Lists.newArrayList();
			}else{
				items = productItemsMap.get(productName);
			}
			items.add(item);
			
			
			productTotalMap.put(productName, quantity);
			productItemsMap.put(productName, items);
		}
		
		int excelNo =1;
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		Date basisDate = order.getCreateDate();
		String oneWeek ;
		String twoWeek ;
		String threeWeek ;
		String fourWeek ;
		if(order.getTargetDate()!=null){
			basisDate=order.getTargetDate();
			oneWeek = DateUtils.getDate(DateUtils.addDays(basisDate,-21), "w");
			twoWeek = DateUtils.getDate(DateUtils.addDays(basisDate,-14), "w");
			threeWeek = DateUtils.getDate(DateUtils.addDays(basisDate,-7), "w");
			fourWeek = DateUtils.getDate(basisDate, "w");
		}else{
			oneWeek = DateUtils.getDate(basisDate, "w");
			twoWeek = DateUtils.getDate(DateUtils.addDays(basisDate,7), "w");
			threeWeek = DateUtils.getDate(DateUtils.addDays(basisDate,14), "w");
			fourWeek = DateUtils.getDate(DateUtils.addDays(basisDate,21), "w");
		}
		
		Date today = DateUtils.getLastDayOfMonth(DateUtils.getDateStart(new Date()));
		Date start = DateUtils.addMonths(today, -1);
		Date end = DateUtils.addMonths(today, 5);
		
		//产品 [国家[月  数]]
		Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAllWithType(start,end);
		DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
		List<String> monthList = Lists.newArrayList();	//当前月开始4个月
		for (int i = 1; i < 7; i++) {
			monthList.add(monthFormat.format(DateUtils.addMonths(start,i)));
		}
		
		
		String[] title = { " 序号 ", "  产品    ","  MOQ  ", "  总下单量  ", "   国家   ", " 下单依据 "," 总库存  "," 安全库存 "," 生产运输缓冲周期  ","31天销量","去除营销数31天销量","主力","新品"
				,"第1个月销售预测","第2个月销售预测","第3个月销售预测","第4个月销售预测","第5个月销售预测","第6个月销售预测","下单周","装箱数",
				"("+oneWeek+")周","("+twoWeek+")周","("+threeWeek+")周","("+fourWeek+")周","常规数量","促销数量","广告数量","备注","审核备注","终极备注"};
		
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
		  
		  int j =0;
		  for(Map.Entry<String, List<ForecastOrderItem>> entry:productItemsMap.entrySet()){
			  String key = entry.getKey();
			  j++;
			  Integer totalQuantity = productTotalMap.get(key);
			  List<ForecastOrderItem> items = entry.getValue();
			  for(int ii=0;ii<items.size();ii++){
				  int i =0;
				  row = sheet.createRow(excelNo++);
				  ForecastOrderItem item = items.get(ii);
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(j); 
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getProductNameColor()); 
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getProduct().getMinOrderPlaced()==null?"":(item.getProduct().getMinOrderPlaced()+"")); 
				  if(ii==0){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(totalQuantity); 
				  }else{
					  i++;
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getCountryCode().equals("com")?"us":item.getCountryCode());
				  //下单依据
				  String byType="";
				  if("0".equals(item.getBy31sales())){
					  byType="预销";
				  }else if("1".equals(item.getBy31sales())){
					  byType="31销";
				  }else if("2".equals(item.getBy31sales())){
					  byType="预销";
				  }else if("3".equals(item.getBy31sales())){
					  byType="31销";
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(byType); 
				  if(item.getTotalStock()!=null){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getTotalStock());  
				  }else{
					  i++;
				  }
				  if(item.getSafeStock()!=null){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getSafeStock());  
				  }else{
					  i++;
				  }
				  if(item.getPeriod()!=null){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getPeriod());  
				  }else{
					  i++;
				  }
				 
				  if(item.getDay31sales()!=null){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getDay31sales());  
				  }else{
					  i++;
				  }
				 
				  if(item.getRealDay31sales()!=null){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getRealDay31sales());  
				  }else{
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("");  
				  }
				  if ("1".equals(item.getIsMain())) {
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("主力");
				  } else if ("0".equals(item.getIsMain())) {
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("普通");
				  } else {
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("");  
				  }
				  if ("1".equals(item.getIsNew())) {
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("新品");
				  } else if ("0".equals(item.getIsNew())) {
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("普通");
				  } else {
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue("");  
				  }
				  
				  if(data.get(item.getProductNameColor())!=null&&data.get(item.getProductNameColor()).get(item.getCountryCode())!=null){
					  if(data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(0))!=null&&data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(0)).getQuantityForecast()!=null){
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(0)).getQuantityForecast());  
					  }else{
						  i++;
					  }
					  if(data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(1))!=null&&data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(1)).getQuantityForecast()!=null){
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(1)).getQuantityForecast());  
					  }else{
						  i++;
					  }
					  if(data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(2))!=null&&data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(2)).getQuantityForecast()!=null){
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(2)).getQuantityForecast());  
					  }else{
						  i++;
					  }
					  if(data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(3))!=null&&data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(3)).getQuantityForecast()!=null){
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(3)).getQuantityForecast());  
					  }else{
						  i++;
					  }
					  if(data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(4))!=null&&data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(4)).getQuantityForecast()!=null){
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(4)).getQuantityForecast());  
					  }else{
						  i++;
					  }
					  if(data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(5))!=null&&data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(5)).getQuantityForecast()!=null){
						  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(data.get(item.getProductNameColor()).get(item.getCountryCode()).get(monthList.get(5)).getQuantityForecast());  
					  }else{
						  i++;
					  }
				  }else{
					  i++;
					  i++;
					  i++;
					  i++;
					  i++;
					  i++;
				  }
				  
				  
				  String orderWeek ="";
				  if("2,3".contains(item.getBy31sales())){
					  if("3".equals(item.getByWeek())){
						  orderWeek=oneWeek;
					  }else if("0".equals(item.getByWeek())){
						  orderWeek=oneWeek;
					  }else if("1".equals(item.getByWeek())){
						  orderWeek=twoWeek;
					  }else if("2".equals(item.getByWeek())){
						  orderWeek=threeWeek;
					  }else if("3".equals(item.getByWeek())){
						  orderWeek=fourWeek;
					  }
				  }
				
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(orderWeek); 
				  //分产品国家判断装箱数
				  Integer packQuantity =  item.getProduct().getPackQuantity();
				  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(packQuantity); 
				  if(item.getForecast1week()!=null){
					  HSSFCell  cellTemp = row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					  if(item.getForecast1week()>0){
						  cellTemp.setCellStyle(style1);
					  }
					  cellTemp.setCellValue(-item.getForecast1week());  
				  }else{
					  i++;
				  }
				  if(item.getForecast2week()!=null){
					  HSSFCell  cellTemp = row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					  if(item.getForecast2week()>0){
						  cellTemp.setCellStyle(style1);
					  }
					  cellTemp.setCellValue(-item.getForecast2week());  
				  }else{
					  i++;
				  }
				  if(item.getForecast3week()!=null){
					  HSSFCell  cellTemp = row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					  if(item.getForecast3week()>0){
						  cellTemp.setCellStyle(style1);
					  }
					  cellTemp.setCellValue(-item.getForecast3week());  
				  }else{
					  i++;
				  }
				  if(item.getForecast4week()!=null){
					  HSSFCell  cellTemp = row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					  if(item.getForecast4week()>0){
						  cellTemp.setCellStyle(style1);
					  }   
					  cellTemp.setCellValue(-item.getForecast4week());    
				  }else{
					  i++;
				  }
				  if(item.getQuantity()!=null){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getQuantity());  
				  }else{
					  i++;
				  }
				  
				  if(item.getPromotionQuantity()!=null){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getPromotionQuantity());  
				  }else{
					  i++;
				  }
				  
				  if(item.getPromotionBossQuantity()!=null){
					  row.createCell(i++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getPromotionBossQuantity());  
				  }else{
					  i++;
				  }
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getRemark()==null?"":item.getRemark()); 
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getReviewRemark()==null?"":item.getReviewRemark()); 
				  row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getBossRemark()==null?"":item.getBossRemark()); 
			  }
			 
		  }
		  
		
		  	request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			String fileName = "ForecastOrderData " + sdf.format(order.getCreateDate()) + ".xls";
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
	
}