/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.AmazonLightningDeals;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPromotionsWarning;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPromotionsWarningItem;
import com.springrain.erp.modules.amazoninfo.entity.AmazonSysPromotions;
import com.springrain.erp.modules.amazoninfo.entity.AmazonSysPromotionsItem;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPromotionsWarningService;
import com.springrain.erp.modules.amazoninfo.service.ProductPriceService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/promotionsWarning")
public class AmazonPromotionsWarningController extends BaseController {
	@Autowired
	private AmazonPromotionsWarningService amazonPromotionsWarningService;

	@Autowired
	private AmazonProductService amazonProductService;
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	@Autowired
	private AmazonOrderService amazonOrderService;
	@Autowired
	private PsiInventoryFbaService fbaService; 
	@Autowired
	private SystemService systemService;
	@Autowired
	private PsiProductService productService;
	@Autowired
	private MailManager  mailManager;
	@Autowired
	private SaleReportService	saleReportService;
	@Autowired
	private PsiInventoryService psiInventoryService;
	@Autowired
	private ProductPriceService productPriceService;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	
	@Autowired
	private AmazonPostsDetailService amazonPostsDetailService;
	
	@Autowired
	private PsiProductTypeGroupDictService psiTypeGroupService;
	
	@Autowired
	private PsiProductGroupUserService  psiProductGroupUserService;
	
	@Autowired
	private     AmazonAccountConfigService      amazonAccountConfigService;
	
	 public static Map<String,String> countryNameMap;
		
		static{
			countryNameMap=Maps.newHashMap();
			countryNameMap.put("de","德国");
			countryNameMap.put("fr","法国");
			countryNameMap.put("it","意大利");
			countryNameMap.put("es","西班牙");
			countryNameMap.put("uk","英国");
			countryNameMap.put("com","美国");
			countryNameMap.put("ca","加拿大");
			countryNameMap.put("jp","日本");
			countryNameMap.put("mx","墨西哥");
		}
	
	@RequestMapping(value = {"list", ""})
	public String list(AmazonPromotionsWarning amazonPromotionsWarning,String proId,HttpServletRequest request, HttpServletResponse response, Model model) {
		
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
		if(amazonPromotionsWarning.getCreateDate()==null){
			try {
				amazonPromotionsWarning.setCreateDate(sdf.parse((sdf.format(DateUtils.addMonths(new Date(), -1)))));
			} catch (ParseException e) {
			}
		}
		if(amazonPromotionsWarning.getUpdateDate()==null){
			try {
				amazonPromotionsWarning.setUpdateDate(sdf.parse((sdf.format(new Date()))));
			} catch (ParseException e) {
			}
		}
	    if(StringUtils.isNotBlank(proId)){
	    	try {
				proId = proId.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
				proId = proId.replaceAll("\\+", "%2B");
				proId = URLDecoder.decode(proId, "utf-8");
				amazonPromotionsWarning.setPromotionId(proId);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	    }
		Page<AmazonPromotionsWarning>  page = new Page<AmazonPromotionsWarning>(request, response);
		String orderBy = page.getOrderBy();
		 if("".equals(orderBy)){
			 page.setOrderBy("createDate desc");
		 }else{
			 page.setOrderBy(orderBy+",createDate desc");
		 }	 
			User user = UserUtils.getUser();
			if (amazonPromotionsWarning.getCreateUser()==null){
				amazonPromotionsWarning.setCreateUser(user);
			} 
			model.addAttribute("cuser",user);
			
		page = amazonPromotionsWarningService.find(page, amazonPromotionsWarning); 
		page.setOrderBy(orderBy);
        model.addAttribute("page", page);
    	Map<String,Map<String,Integer>> fbaMap=fbaService.getFbaInventroyDataByAsin();
		model.addAttribute("fbaMap", fbaMap);
		Set<String>   asinList=saleReportService.getPanEuProductAsin();
		model.addAttribute("asinList", asinList);
        Map<String,String> minPrice=amazonProduct2Service.getMinPrice();
        model.addAttribute("minPrice", minPrice);
      
        Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountryNoServer();
		model.addAttribute("accountMap",accountMap);
		
        if(StringUtils.isNotEmpty(amazonPromotionsWarning.getCountry())){
        	if(StringUtils.isBlank(amazonPromotionsWarning.getAccountName())&&accountMap!=null&&accountMap.get(amazonPromotionsWarning.getCountry())!=null){
				amazonPromotionsWarning.setAccountName(accountMap.get(amazonPromotionsWarning.getCountry()).get(0));
			}
        	Map<String,String> asinMap=amazonPromotionsWarningService.getAsinMap(amazonPromotionsWarning.getCountry(),amazonPromotionsWarning.getAccountName());
        	model.addAttribute("asinMap", asinMap);
        }
        model.addAttribute("groupType", psiTypeGroupService.getAllList());
        
    	
		
		return "modules/amazoninfo/amazonPromotionsWarningList";
	}

	
	@ResponseBody
	@RequestMapping(value = {"deleteItem"})
	public String deleteItem(Integer itemId) {
		try {
			return amazonPromotionsWarningService.deleteItem(itemId);
		} catch (Exception e) {
			return "0";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = {"saveOrderItem"})
	public Integer saveOrderItem(Integer id,Integer halfHourQuantity,Integer cumulativeQuantity,String productNameColor,String asin,Integer orderId) {
		    AmazonPromotionsWarningItem item=new AmazonPromotionsWarningItem();
		    item.setProductNameColor(productNameColor);
		    item.setAsin(asin);
		    item.setWarning(amazonPromotionsWarningService.get(orderId));
		    if(halfHourQuantity!=null){
		    	item.setHalfHourQuantity(halfHourQuantity);
		    }
		    if(cumulativeQuantity!=null){
		    	 item.setCumulativeQuantity(cumulativeQuantity);
		    }
		    if(id!=null){
		    	item.setId(id);
		    }
		    amazonPromotionsWarningService.saveItem(item);
			return item.getId();
	}
	
	@RequestMapping(value = {"findQuantityInfo"})
	@ResponseBody
	public Map<String,Object> findQuantityInfo(String name,String country,String accountName){
		Map<String,PsiInventoryFba>  amazonStock=psiInventoryService.getProductFbaInfo(name);
		List<String> keyBoardAndHasPowerList=saleReportService.findKeyBoardAndHasPowerAllProduct();
		Map<String,Object> map=Maps.newHashMap();
		Integer fbaStock=0;
		if(!keyBoardAndHasPowerList.contains(name)&&"de,fr,it,es,uk".contains(country)){//不带电源
			PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_eu");
			if(psiInventoryFba!=null){
				fbaStock=psiInventoryFba.getRealTotal();
			}
		}else{
			PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_"+country);
			if(psiInventoryFba!=null){
				fbaStock=psiInventoryFba.getRealTotal();
			}
		}
		map.put("fbaStock", fbaStock);
		return map;
	}
	
	
	@RequestMapping(value = {"add"})
	public String add(AmazonPromotionsWarning amazonPromotionsWarning, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(amazonPromotionsWarning.getStartDate()==null){
			amazonPromotionsWarning.setStartDate(new Date());
			amazonPromotionsWarning.setEndDate(DateUtils.addDays(new Date(),30));
		}
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountryNoServer();
		model.addAttribute("accountMap",accountMap);
		if(StringUtils.isNotBlank(amazonPromotionsWarning.getAccountName())){
			
			Map<String,String> asinMap=amazonPromotionsWarningService.getAsinMap(amazonPromotionsWarning.getCountry(),amazonPromotionsWarning.getAccountName());
			//匹配上架员
			List<User> userList = systemService.findUserByPermission("amazoninfo:feedSubmission:");
			userList.remove(UserUtils.getUser());
			model.addAttribute("all", userList);
			//特批折扣审核
			List<User> userList2 = systemService.findUserByPermission("amazon:promotionsWarning:review");
			if(userList2 != null){
				model.addAttribute("specialUser", userList2);
			}
			model.addAttribute("asinMap", asinMap);
			model.addAttribute("model",productService.find());
			
			Map<String, String> productPositionMap = psiProductEliminateService.findProductPositionByCountry(Lists.newArrayList(amazonPromotionsWarning.getCountry()));
			Map<String, String> productIsNewMap = psiProductEliminateService.findIsNewMap(amazonPromotionsWarning.getCountry());
			model.addAttribute("productPositionMap", productPositionMap);
			model.addAttribute("productIsNewMap",productIsNewMap);
			
			
		}
		return "modules/amazoninfo/amazonPromotionsWarningAdd";
	}
	
	@RequestMapping(value = "addSave")
	public String addSave(final AmazonPromotionsWarning amazonPromotionsWarning, Model model, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
		//String proId=URLDecoder.decode(amazonPromotionsWarning.getPromotionId(),"utf-8");
		String proId=HtmlUtils.htmlUnescape(amazonPromotionsWarning.getPromotionId());
		amazonPromotionsWarning.setPromotionId(proId);
		amazonPromotionsWarning.setCreateDate(new Date());
		amazonPromotionsWarning.setCreateUser(UserUtils.getUser());
		amazonPromotionsWarning.setUpdateUser(UserUtils.getUser());
		amazonPromotionsWarning.setUpdateDate(new Date());
		amazonPromotionsWarning.setWarningSta("3");
		amazonPromotionsWarning.setIsActive("0");
		amazonPromotionsWarning.setBuyerPurchases(HtmlUtils.htmlUnescape(amazonPromotionsWarning.getBuyerPurchases()));
		amazonPromotionsWarning.setBuyerGets(HtmlUtils.htmlUnescape(amazonPromotionsWarning.getBuyerGets()));
		if(amazonPromotionsWarning.getPromotionId().startsWith("R-")||amazonPromotionsWarning.getPromotionId().startsWith("C-")){
			amazonPromotionsWarning.setProType(null);
		}
		for(AmazonPromotionsWarningItem item :amazonPromotionsWarning.getItems()){
			if(item.getAsin()==null){
				item.setDelFlag("1");
			}
			item.setWarning(amazonPromotionsWarning);
		}
		amazonPromotionsWarningService.save(amazonPromotionsWarning);
		new Thread(){
			public void run(){  
				try {
					Thread.sleep(60000*10);
					sendCheckEmail(amazonPromotionsWarning);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	    }.start();
		
		addMessage(redirectAttributes, "新增折扣预警'" + ""+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/promotionsWarning/?warningSta=3&proId="+URLEncoder.encode(proId,"utf-8")+"&country="+amazonPromotionsWarning.getCountry();
	}
	
	private  boolean sendCheckEmail(AmazonPromotionsWarning amazonPromotionsWarning){
		String toAddress="";
		if(amazonPromotionsWarning!=null){
			 toAddress =systemService.getUser(amazonPromotionsWarning.getCheckUser().getId()).getEmail();
		}
		StringBuffer content= new StringBuffer("");
		if(amazonPromotionsWarning!=null){
			String suff = amazonPromotionsWarning.getCountry();
			if("uk,jp".contains(amazonPromotionsWarning.getCountry())){
					 suff = "co."+suff;
			}else if("mx".equals(amazonPromotionsWarning.getCountry())){
					suff = "com."+suff;
			}else if(amazonPromotionsWarning.getCountry().startsWith("com")){
				 suff= "com";
			}
			String amazonUrl="";
			try {
				amazonUrl = "https://sellercentral.amazon."+suff+"/promotions/view?trackingId="+URLEncoder.encode(amazonPromotionsWarning.getPromotionId(),"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			content.append("<p>请审核"+systemService.getUser(amazonPromotionsWarning.getCreateUser().getId()).getName()+"提交的"+("com".equals(amazonPromotionsWarning.getCountry())?"US":amazonPromotionsWarning.getCountry().toUpperCase())+"折扣<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/promotionsWarning?warningSta=3&country="+amazonPromotionsWarning.getCountry()+"'>"+amazonPromotionsWarning.getPromotionId()+"</a>申请--<a href='"+amazonUrl+"'>Amazon折扣链接</a></p>");
			content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>");
			content.append("<th>产品</th><th>Asin</th><th>累计监控数量</th><th>半小时监控数量</th></tr>");
			for (AmazonPromotionsWarningItem item:amazonPromotionsWarning.getItems()) {
				content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				content.append("<td>"+item.getProductNameColor()+"</td><td>"+item.getAsin()+"</td><td>"+(item.getCumulativeQuantity()==null?"":item.getCumulativeQuantity())+"</td><td>"+(item.getHalfHourQuantity()==null?"":item.getHalfHourQuantity())+"</td></tr>");
			}
			content.append("</table>");
		}
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();   
			final MailInfo mailInfo = new MailInfo(toAddress,"折扣审核"+amazonPromotionsWarning.getPromotionId()+DateUtils.getDate("-yyyy/MM/dd"),date);
			mailInfo.setContent(content.toString());
			new Thread(){
				public void run(){   
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return true;
	}
	
	@ResponseBody
	@RequestMapping(value = {"halfHourQuantity"})
	public String halfHourQuantity(Integer quantity,Integer id) {
		amazonPromotionsWarningService.updateHalfQuantity(quantity, id);
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value = {"findPromotionsByName"})
	public Map<String,List<String>> findPromotionsByName(String country,String name) {
		List<String> countrys=Arrays.asList(country.split(","));
		return amazonPromotionsWarningService.findPromotions(countrys, name);
	}
	
	@ResponseBody
	@RequestMapping(value = {"isExistItem"})
	public String isExistItem(Integer id) {
		AmazonPromotionsWarning warn=amazonPromotionsWarningService.get(id);
		String flag="1";
		if(warn!=null){
			for (AmazonPromotionsWarningItem item : warn.getItems()) {
				if(item.getHalfHourQuantity()!=null||item.getCumulativeQuantity()!=null){
					flag="0";
					break;
				}
			}
		}
		return flag;
	}
	
	@ResponseBody
	@RequestMapping(value = {"cumulativeQuantity"})
	public String updateCumulativeQuantity(Integer quantity,Integer id) {
		amazonPromotionsWarningService.updateCumulativeQuantity(quantity, id);
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateRemark"})
	public String updateRemark(String remark,Integer id) {
		amazonPromotionsWarningService.updateRemark(remark, id);
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value = {"isExistPromotionId"})
	public String isExistPromotionId(String promotionId,String country) {
		try {
			promotionId = URLDecoder.decode(promotionId, "UTF-8");
		} catch (UnsupportedEncodingException e) {}
		Integer promotionsId=amazonPromotionsWarningService.isNotExist(promotionId,country);
		if(promotionsId==null){//不存在
			return "0";
		}
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value = "passPromotion")
	public String passPromotion(String state,Integer id, String country,Model model, RedirectAttributes redirectAttributes) {
		amazonPromotionsWarningService.checkState(state, id);
		return "0";
	}
	
	@ResponseBody
	@RequestMapping(value = "noPassPromotion")
	public String noPassPromotion(String state,Integer id,Model model, RedirectAttributes redirectAttributes) {
		amazonPromotionsWarningService.updateState(state, id);
		if("4".equals(state)){
			
				AmazonPromotionsWarning amazonPromotionsWarning=amazonPromotionsWarningService.get(id);
				String proId= amazonPromotionsWarningService.get(id).getPromotionId();
				String createUser =systemService.getUser(amazonPromotionsWarning.getCreateUser().getId()).getName();
				Set<String> promotionIds=Sets.newHashSet(proId);
				AmazonAccountConfig config=amazonAccountConfigService.getByName(amazonPromotionsWarning.getAccountName(),false);
				Map<String,String> curResMap = PsiProductService.endPromotions(config,promotionIds);
				WeixinSendMsgUtil.sendTextMsgToUser(createUser+"|"+UserUtils.getUser().getName()+"|eileen",proId+",Result:"+curResMap.get(proId));
			
		}
		return "0";
	}
	
	@RequestMapping(value = "updateState")
	public String updateState(String state,Integer id, String country,Model model, RedirectAttributes redirectAttributes) {
		amazonPromotionsWarningService.updateState(state, id);
		if("4".equals(state)){

				AmazonPromotionsWarning amazonPromotionsWarning=amazonPromotionsWarningService.get(id);
				String proId= amazonPromotionsWarningService.get(id).getPromotionId();
				String createUser =systemService.getUser(amazonPromotionsWarning.getCreateUser().getId()).getName();
				Set<String> promotionIds=Sets.newHashSet(proId);
				AmazonAccountConfig config=amazonAccountConfigService.getByName(amazonPromotionsWarning.getAccountName(),false);
				Map<String,String> curResMap = PsiProductService.endPromotions(config,promotionIds);
				WeixinSendMsgUtil.sendTextMsgToUser(createUser+"|"+UserUtils.getUser().getName()+"|eileen",proId+",Result:"+curResMap.get(proId));
			
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/promotionsWarning/?state="+state+"&country="+country+"&repage";
	}
	
	
	@RequestMapping(value = "closePromotion")
	public String closePromotion(Integer id, String country,Model model, RedirectAttributes redirectAttributes) {
		AmazonPromotionsWarning amazonPromotionsWarning=amazonPromotionsWarningService.get(id);
		String proId= amazonPromotionsWarningService.get(id).getPromotionId();
		String createUser =systemService.getUser(amazonPromotionsWarning.getCreateUser().getId()).getName();
		Set<String> promotionIds=Sets.newHashSet(proId);
		AmazonAccountConfig config=amazonAccountConfigService.getByName(amazonPromotionsWarning.getAccountName(),false);
		Map<String,String> curResMap = PsiProductService.endPromotions(config,promotionIds);
		amazonPromotionsWarningService.updateStaAndRes(proId,country,UserUtils.getUser().getName()+"于"+new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())+"关闭折扣,"+curResMap.get(proId));
		WeixinSendMsgUtil.sendTextMsgToUser(createUser+"|"+UserUtils.getUser().getName()+"|eileen",UserUtils.getUser().getName()+"于"+new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())+"关闭折扣"+proId+",Result:"+curResMap.get(proId));
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/promotionsWarning/?country="+country+"&repage";
	}
	
	@RequestMapping(value = "reUpdateState")
	public String reUpdateState(String state,Integer id, String country,Model model, RedirectAttributes redirectAttributes) {
		amazonPromotionsWarningService.updateStateAndRemark(state, id);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/promotionsWarning/?country="+country+"&repage";
	}
	
	@RequestMapping(value = "checkState")
	public String checkState(String state,Integer id, String country,Model model, RedirectAttributes redirectAttributes) {
		amazonPromotionsWarningService.checkState(state, id);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/promotionsWarning/?country="+country+"&repage";
	}
	
	@RequestMapping(value = "specialCheck")
	public String specialCheck(Integer id, String country,Model model, RedirectAttributes redirectAttributes) {
		AmazonPromotionsWarning promotionsWarning=amazonPromotionsWarningService.get(id);
		Set<String> roleSet=Sets.newHashSet();
		for (Dict dict : DictUtils.getDictList("platform")) {
			String temp= dict.getValue();
			if(!"com.unitek".equals(temp)){
				roleSet.add("amazoninfo:feedSubmission:"+temp);
			}
			roleSet.add("amazoninfo:feedSubmission:all");
		}
		List<String> userNameList=systemService.findUserNameByMenuName(roleSet);
		String wxName="";
		StringBuffer buf= new StringBuffer();
		for (String name: userNameList) {
			buf.append(name+"|");
		}
		wxName = buf.toString();
		if(StringUtils.isBlank(promotionsWarning.getSpecialCheckUser())){
			String checkUser=UserUtils.getUser().getName();
			amazonPromotionsWarningService.specialCheck(id,checkUser);
			try{
				Date date = new Date();   
				final MailInfo mailInfo = new MailInfo("amazon-sales@inateck.com",promotionsWarning.getPromotionId()+"特批折扣审核"+DateUtils.getDate("-yyyy/MM/dd"),date);
				mailInfo.setContent("特批折扣"+promotionsWarning.getPromotionId()+","+checkUser+"审核一次,还需一人再次审核");
				new Thread(){
					public void run(){   
						 mailManager.send(mailInfo);
					}
				}.start();
				WeixinSendMsgUtil.sendTextMsgToUser(wxName+"|eileen|tim","特批折扣"+promotionsWarning.getPromotionId()+","+checkUser+"审核一次,还需一人再次审核");
			}catch(Exception e){}
		}else{
			if(!promotionsWarning.getSpecialCheckUser().contains(UserUtils.getUser().getName())){
				String checkUser=promotionsWarning.getSpecialCheckUser()+","+UserUtils.getUser().getName();
				amazonPromotionsWarningService.specialCheck(id,checkUser);
				try{
					Date date = new Date();   
					final MailInfo mailInfo = new MailInfo("amazon-sales@inateck.com",promotionsWarning.getPromotionId()+"特批折扣审核"+DateUtils.getDate("-yyyy/MM/dd"),date);
					mailInfo.setContent("特批折扣"+promotionsWarning.getPromotionId()+","+checkUser+"已经审核,审核通过");
					amazonPromotionsWarningService.updateSpecialNoFlag(country,Sets.newHashSet(promotionsWarning.getPromotionId()));
					new Thread(){
						public void run(){   
							 mailManager.send(mailInfo);
						}
					}.start();
					WeixinSendMsgUtil.sendTextMsgToUser(wxName+"|eileen|tim","特批折扣"+promotionsWarning.getPromotionId()+","+checkUser+"已经审核,审核通过");
				}catch(Exception e){}
			}else{
				addMessage(redirectAttributes, UserUtils.getUser().getName()+"已经审核过,不能再次审核");
			}
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/promotionsWarning/?country="+country+"&repage";
	}
	
	
	@RequestMapping(value = "nextCheckState")
	public String nextCheckState(Integer id, String country,Model model, RedirectAttributes redirectAttributes) {
		AmazonPromotionsWarning promotionsWarning=amazonPromotionsWarningService.get(id);
		promotionsWarning.setLastCheckDate(new Date());
		promotionsWarning.setCheckFlag(null);
		if(StringUtils.isNotBlank(promotionsWarning.getLastCheckUser())){
			promotionsWarning.setLastCheckUser(promotionsWarning.getLastCheckUser()+","+UserUtils.getUser().getName());
		}else{
			promotionsWarning.setLastCheckUser(UserUtils.getUser().getName());
		}
		amazonPromotionsWarningService.save(promotionsWarning);
		try{
			WeixinSendMsgUtil.sendTextMsgToUser(promotionsWarning.getCreateUser().getName()+"|eileen|maya|alice",UserUtils.getUser().getName()+"再次审核通过折扣："+promotionsWarning.getPromotionId());
		}catch(Exception e){}
		
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/promotionsWarning/?country="+country+"&repage";
	}
	
	/*
	public static boolean flag;
	@RequestMapping(value = "synchronizePromotions")
	@ResponseBody
	public void synchronizePromotions(Model model,final String accountName){
		final  String email=UserUtils.getUser().getEmail();
		final  String loginName=UserUtils.getUser().getLoginName();
		final  AmazonAccountConfig config=amazonAccountConfigService.getByName(accountName);		
		if(!flag){
			new Thread(){
				public void run() {
					flag=true;
					new PromotionsWarnMonitor().scannerData(email,config,amazonPromotionsWarningService,amazonProductService,loginName,productService,mailManager);
					flag=false;
				}
	       }.start();
		}   
		
	}
	
	*/
	
	@RequestMapping(value = "export")
	public String export(AmazonPromotionsWarning amazonPromotionsWarning, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AmazonPromotionsWarning> page=new Page<AmazonPromotionsWarning>(request, response,-1);
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
		if(amazonPromotionsWarning.getCreateDate()==null){
			try {
				amazonPromotionsWarning.setCreateDate(sdf.parse((sdf.format(DateUtils.addMonths(new Date(), -3)))));
			} catch (ParseException e) {
			}
		}
		if(amazonPromotionsWarning.getUpdateDate()==null){
			try {
				amazonPromotionsWarning.setUpdateDate(sdf.parse((sdf.format(new Date()))));
			} catch (ParseException e) {
			}
		}
		page.setPageSize(800);
        page = amazonPromotionsWarningService.find(page, amazonPromotionsWarning); 
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
		
		HSSFCell cell = null;
		List<String> title = Lists.newArrayList("产品","Tracking Id","国家","开始时间","结束时间","Buyer Gets","One redemptionper customer","半小时销量","累计销量","实时销量");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
	    }
		int rownum=1;
		for (AmazonPromotionsWarning warn:page.getList()) {
			for (AmazonPromotionsWarningItem item:warn.getItems()) {
				row=sheet.createRow(rownum++);
				int j=0;
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getProductNameColor());
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(warn.getPromotionId());
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(warn.getCountry())?"us":warn.getCountry());
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(warn.getStartDate()));
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(warn.getEndDate()));
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(warn.getBuyerGets());
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(warn.getOneRedemption());
	    		
	    		if(item.getHalfHourQuantity()!=null){
	    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getHalfHourQuantity());
	    		}else{
	    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	    		}
	    		if(item.getCumulativeQuantity()!=null){
	    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getCumulativeQuantity());
	    		}else{
	    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	    		}
	    		Integer num=amazonOrderService.getCumulativeQuantityByAsin(warn.getCountry(),item.getAsin(), warn.getPromotionId());
	    		if(num!=null){
	    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(num);	
	    		}else{
	    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");	
	    		}
			}
		}
		for(int j=1;j<rownum;j++){
			 for(int i=0;i<title.size();i++){
		    	 sheet.getRow(j).getCell(i).setCellStyle(contentStyle);
			 }	  
	       }
		for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
		    }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
            String fileName ="折扣监控统计" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value = "promotionsCodeList")
	public String list(AmazonSysPromotions amazonSysPromotions, HttpServletRequest request, HttpServletResponse response, Model model) {

		if(amazonSysPromotions.getCreateDate()==null){
			amazonSysPromotions.setCreateDate(DateUtils.addMonths(new Date(), -1));
		}
		if(amazonSysPromotions.getReviewDate()==null){
			amazonSysPromotions.setReviewDate(new Date());
		}
	
		Page<AmazonSysPromotions>  page = new Page<AmazonSysPromotions>(request, response);
		String orderBy = page.getOrderBy();
		 if("".equals(orderBy)){
			 page.setOrderBy("createDate desc");
		 }else{
			 page.setOrderBy(orderBy+",createDate desc");
		 }	 
		page = amazonPromotionsWarningService.findPromotions(page, amazonSysPromotions); 
		page.setOrderBy(orderBy);
        model.addAttribute("page", page);

		return "modules/amazoninfo/amazonPromotionsCodeList";
	}
	
	@RequestMapping(value = "savePromotions")
	public String savePromotions(AmazonSysPromotions amazonSysPromotions, HttpServletRequest request, HttpServletResponse response, Model model){
		amazonSysPromotions.setType("1");
		amazonSysPromotions.setStatus("0");
		amazonSysPromotions.setCreateDate(new Date());
		amazonSysPromotions.setCreateUser(UserUtils.getUser());
		amazonPromotionsWarningService.savePromotionsCode(amazonSysPromotions);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/promotionsWarning/promotionsCodeList?repage";
	}
	
	@RequestMapping(value = "createPromotionsCode")
	public String createPromotionsCode(final AmazonSysPromotions amazonSysPromotions, HttpServletRequest request, HttpServletResponse response, Model model){
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/promotionsWarning/promotionsCodeList?repage";
	}
	
	

	@RequestMapping(value = "cancelPromotions")
	public String cancelPromotions(AmazonSysPromotions amazonSysPromotions, HttpServletRequest request, HttpServletResponse response, Model model){
		amazonPromotionsWarningService.cancelPromtionsCode(amazonSysPromotions.getId());
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/promotionsWarning/promotionsCodeList?repage";
	}
	
	
	@RequestMapping(value = "promotionsForm")
	public String promotionsForm(AmazonSysPromotions amazonSysPromotions, HttpServletRequest request, HttpServletResponse response, Model model){
		return "modules/amazoninfo/amazonPromotionsCodeAdd";
	}
	
	
	@RequestMapping(value = "exportDetail")
	public String exportDetail(AmazonSysPromotions amazonSysPromotions, HttpServletRequest request, HttpServletResponse response, Model model) {
		amazonSysPromotions=amazonPromotionsWarningService.getPromotionsCodeById(amazonSysPromotions.getId());
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
		
		HSSFCell cell = null;
		List<String> title = Lists.newArrayList("PromotionsId","PromotionsCode","Email","CustomId","AmazonOrderId","ProductName");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
	    }
		int rownum=1;
		for (AmazonSysPromotionsItem item:amazonSysPromotions.getItems()) {
				row=sheet.createRow(rownum++);
				int j=0;
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getPromotionsId());
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getPromotionsCode());
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getEmail());
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getCustomId());
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getAmazonOrderId());
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getProductName());
		}
		for(int j=1;j<rownum;j++){
			 for(int i=0;i<title.size();i++){
		    	 sheet.getRow(j).getCell(i).setCellStyle(contentStyle);
			 }	  
	       }
		for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
		    }
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
            String fileName ="PromotionsCode" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";
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
	
	@ResponseBody
	@RequestMapping(value = "countProfit")
	public String countProfit(String qualifyingItem,String proId,String proType,String country,String accountName,String buyerPurchases,String buyerGets,String purchaseStr,String offPriceStr,String asin,HttpServletRequest request, HttpServletResponse response, Model model){
		String returnInfo="";
		if(StringUtils.isNotBlank(buyerGets)&&buyerGets.equals("Post-order benefit")){
			return "折扣利润分析正常<br/>";
		}
		if(StringUtils.isNotBlank(buyerGets)&&buyerGets.equals("Free items")){
			if(StringUtils.isNotBlank(asin)&&!"3".equals(proType)){
				String[] arrAsin=asin.split(",");
				Map<String,String> minPriceMap=amazonProduct2Service.getMinPrice2ByAccount();
				float minPrice=0f;
				String minAsin="";
				for (String singleAsin: arrAsin) {
					String key=accountName+"_"+singleAsin;
					String priceStr=minPriceMap.get(key);
					if(priceStr!=null){
						String[] arr=priceStr.split(",");
						if(minPrice==0f||Float.parseFloat(arr[1])<minPrice){
							minPrice=Float.parseFloat(arr[1]);
							minAsin=singleAsin;
						}
					}
				}
				if(minPrice>0){
					String key=accountName+"_"+qualifyingItem;
					String priceStr=minPriceMap.get(key);
					if(priceStr!=null){
						String[] arr=priceStr.split(",");
						Float price=Float.parseFloat(arr[1]);
					    if(price>minPrice){
					    	return "<font color='red'>免费赠送产品"+qualifyingItem+"售价 "+price+" 大于产品"+minAsin+"最低价 "+minPrice+",只能选择特批</font>";
					    }
					}	
				}
			}	
			return "折扣利润分析正常<br/>";
		}
		if(StringUtils.isNotBlank(asin)){
			String[] arrAsin=asin.split(",");
			Map<String,String> maxPriceMap=amazonProduct2Service.getMaxPrice2ByAccount();
			Map<String,Float>  safePriceMap=productPriceService.findAllProducSalePrice();
			Map<String, String> productPositionMap = psiProductEliminateService.findAllProductPosition();
			Map<String, String> productIsNewMap = psiProductEliminateService.findIsNewMap();
			Map<String,Float> priceMap=amazonProduct2Service.findAllProductPriceByAccount();
			Float maxPrice=0f;
			String maxPriceName="";
		    Float purchase=Float.parseFloat(purchaseStr.replace(",", "."));
		    Float offPrice=Float.parseFloat(offPriceStr.replace(",", "."));
			if(proId.startsWith("R-")&&!buyerGets.contains("Amount off")){//R-
				returnInfo+="R-没有用Amount off 折扣<br/><br/>";
			}
			if(proId.startsWith("F-")){
				boolean flag=false;
				for (String singleAsin: arrAsin) {
					String key=accountName+"_"+singleAsin;
					String priceStr=maxPriceMap.get(key);
					PsiSku psiSku=productService.getProductByAsin2(singleAsin);
					String safeKey=psiSku.getNameWithColor()+"_"+country;
					Float minCodePrice=safePriceMap.get(safeKey);
					if(minCodePrice==null){
						continue;
					}
					
					Float promotionsPrice=0f;
					String priceKey=accountName+"_"+singleAsin;
					Float mainAsinPrice=priceMap.get(priceKey);
					if(mainAsinPrice!=null){
						if(buyerGets.contains("Amount off")&&buyerPurchases.contains("At least this quantity of items")){
							promotionsPrice=mainAsinPrice-offPrice/purchase;
						}else if(buyerGets.contains("Amount off")&&buyerPurchases.contains("At least amount")){
							promotionsPrice=mainAsinPrice*(1-offPrice/purchase);
						}else{
							promotionsPrice=mainAsinPrice*(100-offPrice)/100;
						}
						String nameKey=psiSku.getNameWithColor()+"_"+country;
						if("4".equals(productPositionMap.get(nameKey))){//淘汰
							if(promotionsPrice/minCodePrice<0.3){
								 returnInfo+="<font color='red'>淘汰品不能低于保本价的3折,产品("+singleAsin+")"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+"</font>";
								 flag=true;
								 break;
							}
						}else if("0".equals(productIsNewMap.get(nameKey))){//普通(7折)
							if(promotionsPrice/minCodePrice<0.5){
								returnInfo+="<font color='red'>普通品不能低于保本价的5折,产品("+singleAsin+")"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+"</font>";
								flag=true;
								break;
							}
						}else{//新品
							if(promotionsPrice/minCodePrice<0.5){
								returnInfo+="<font color='red'>新品不能低于保本价的5折,产品("+singleAsin+")"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+"</font>";
								flag=true;
								break;
							}
						}
					}
					if(priceStr!=null){
						String[] arr=priceStr.split(",");
						if(Float.parseFloat(arr[1])>maxPrice){
							maxPrice=Float.parseFloat(arr[1]);
							maxPriceName=psiSku.getNameWithColor();
						}
					}
				}
				
				if(flag){
					return returnInfo;
				}
				
			}

			if(maxPrice>0){
				if(proId.startsWith("R-")&&buyerPurchases.contains("At least this quantity of items")){//R-
					if(offPrice/purchase>maxPrice){
						returnInfo+="<font color='red'>减免金额高于最高单价,产品"+maxPriceName+"最高价"+maxPrice+",优惠金额："+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"</font><br/><br/>";
					}else{
						returnInfo+="减免金额不能高于最高单价,产品"+maxPriceName+"最高价"+maxPrice+",优惠金额："+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"<br/><br/>";
					}
				}
			}
			
			
			
			if((proId.startsWith("C-")||proId.startsWith("F-"))&&!"3".equals(proType)){
				boolean flag=false;
				for (String singleAsin: arrAsin) {
					PsiSku psiSku=productService.getProductByAsin2(singleAsin);
					String safeKey=psiSku.getNameWithColor()+"_"+country;
					Float minCodePrice=safePriceMap.get(safeKey);
					if(minCodePrice==null){
						continue;
					}
					String priceKey=accountName+"_"+singleAsin;
					Float mainAsinPrice=priceMap.get(priceKey);
					if(mainAsinPrice!=null){
						if((proId.startsWith("C-")||proId.startsWith("F-"))&&"2".equals(proType)){//不能亏本 就最低折扣价格-保本价
							String msg="F-有利润促销";
                            if(proId.startsWith("C-")){
                            	msg="C-不能亏本";
                            }
							if(buyerGets.contains("Amount off")&&buyerPurchases.contains("At least this quantity of items")){
								if(mainAsinPrice-offPrice/purchase-minCodePrice<0){
									returnInfo+="<font color='red'>"+msg+"(亏本金额"+(new BigDecimal(mainAsinPrice-offPrice/purchase-minCodePrice).setScale(2, 4).floatValue())+"),产品"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"</font><br/><br/>";
									flag=true;
									break;
								}else{
									returnInfo+=""+msg+"(盈利"+(new BigDecimal(mainAsinPrice-offPrice/purchase-minCodePrice).setScale(2, 4).floatValue())+"),产品"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"<br/><br/>";
								}
							}else if(buyerGets.contains("Amount off")&&buyerPurchases.contains("At least amount")){
								if(mainAsinPrice-mainAsinPrice*(offPrice/purchase)-minCodePrice<0){
								  	returnInfo+="<font color='red'>"+msg+"(亏本金额"+(new BigDecimal(mainAsinPrice-mainAsinPrice*(offPrice/purchase)-minCodePrice).setScale(2, 4).floatValue())+"),产品"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"</font><br/><br/>";
								  	flag=true;
									break;
								}else{
									returnInfo+=""+msg+"(盈利"+(new BigDecimal(mainAsinPrice-mainAsinPrice*(offPrice/purchase)-minCodePrice).setScale(2, 4).floatValue())+"),产品"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"<br/><br/>";
								}
							}else{
								if(mainAsinPrice-mainAsinPrice*offPrice/100d-minCodePrice<0){
									returnInfo+="<font color='red'>"+msg+"(亏本金额"+(new BigDecimal(mainAsinPrice-mainAsinPrice*offPrice/100d-minCodePrice).setScale(2, 4).floatValue())+"),产品"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100d).setScale(2, 4).floatValue()+"</font><br/><br/>";
									flag=true;
									break;
								}else{
									returnInfo+=""+msg+"(盈利"+(new BigDecimal(mainAsinPrice-mainAsinPrice*offPrice/100d-minCodePrice).setScale(2, 4).floatValue())+"),产品"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100d).setScale(2, 4).floatValue()+"<br/><br/>";
									
								}
							}
						}//不能亏本
						
						if(proId.startsWith("F-")&&!"2".equals(proType)){//不能亏本 就最低折扣价格-保本价
							Integer pecentNum=15;
							String nameKey=psiSku.getNameWithColor()+"_"+country;
							String msg="非淘汰品亏本 ";
							if("4".equals(productPositionMap.get(nameKey))){//淘汰
								 pecentNum=50;
								 msg="淘汰品亏本 ";
							}
							if(buyerGets.contains("Amount off")&&buyerPurchases.contains("At least this quantity of items")){
								if((minCodePrice-(mainAsinPrice-offPrice/purchase))*100d/minCodePrice>=pecentNum){
									returnInfo+="<font color='red'>"+msg+"≧"+pecentNum+"%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-offPrice/purchase))*100d/minCodePrice))+"%),产品"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"</font><br/><br/>";
								}else{
									returnInfo+=msg+"<"+pecentNum+"%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-offPrice/purchase))*100d/minCodePrice))+"%),产品"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"<br/><br/>";
									
								}
							}else if(buyerGets.contains("Amount off")&&buyerPurchases.contains("At least amount")){
								if((minCodePrice-(mainAsinPrice-mainAsinPrice*(offPrice/purchase)))*100d/minCodePrice>=pecentNum){
									returnInfo+="<font color='red'>"+msg+" ≧"+pecentNum+"%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*(offPrice/purchase)))*100d/minCodePrice))+"%),产品"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"</font><br/><br/>";
								}else{
									returnInfo+=msg+"<"+pecentNum+"%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*(offPrice/purchase)))*100d/minCodePrice))+"%),产品"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"<br/><br/>";
									
								}
							}else{
								if((minCodePrice-(mainAsinPrice-mainAsinPrice*offPrice/100d))*100d/minCodePrice>=pecentNum){
									returnInfo+="<font color='red'>"+msg+" ≧"+pecentNum+"%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*offPrice/100d))*100d/minCodePrice))+"%),产品"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100d).setScale(2, 4).floatValue()+"</font><br/><br/>";
								}else{
									returnInfo+=msg+"<"+pecentNum+"%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*offPrice/100d))*100d/minCodePrice))+"%),产品"+psiSku.getNameWithColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100d).setScale(2, 4).floatValue()+"<br/><br/>";
								}
							}
						}
						
						
					}
				}
				if(flag){
					if(StringUtils.isBlank(returnInfo)){
						returnInfo="折扣利润分析正常<br/>";
					}
					returnInfo+=checkPromotions2(proId,proType,country,accountName,buyerPurchases,buyerGets,purchaseStr,offPriceStr,asin);
					return returnInfo;
				}
			}
		}
		if(StringUtils.isBlank(returnInfo)){
			returnInfo="折扣利润分析正常<br/>";
		}
		returnInfo+=checkPromotions2(proId,proType,country,accountName,buyerPurchases,buyerGets,purchaseStr,offPriceStr,asin);
		return returnInfo;
	}
	
	@ResponseBody
	@RequestMapping(value = "promotionsProfit")
	public String promotionsProfit(AmazonPromotionsWarning amazonPromotionsWarning, HttpServletRequest request, HttpServletResponse response, Model model){
		amazonPromotionsWarning=amazonPromotionsWarningService.get(amazonPromotionsWarning.getId());
		String returnInfo="";
		if(StringUtils.isNotBlank(amazonPromotionsWarning.getBuyerGets())&&StringUtils.isNotBlank(amazonPromotionsWarning.getBuyerPurchases())){
			Map<String,Float> priceMap=amazonProduct2Service.findAllProductPriceByAccount();
			Map<String,String> maxPriceMap=amazonProduct2Service.getMaxPrice2ByAccount();
			Map<String,Float>  safePriceMap=productPriceService.findAllProducSalePrice();
			Map<String, String> productPositionMap = psiProductEliminateService.findAllProductPosition();
			Float maxPrice=0f;
			String maxPriceName="";
			String mainAsin="";
			String mainProduct="";
			String[] tempArr=amazonPromotionsWarning.getBuyerPurchases().split(" ");
			Float purchase=Float.parseFloat(tempArr[tempArr.length-1].replaceAll(",", "."));
			String[] arr2=amazonPromotionsWarning.getBuyerGets().split(" ");
			Float offPrice=Float.parseFloat(arr2[arr2.length-1].replaceAll(",", "."));
	
			
			if(amazonPromotionsWarning.getPromotionId().startsWith("R-")&&!amazonPromotionsWarning.getBuyerGets().contains("Amount off")){//R-
				returnInfo+="R-没有用Amount off 折扣<br/><br/>";
			}
			if(amazonPromotionsWarning.getBuyerPurchases().contains("For every quantity of items purchased")){
				returnInfo+="没有禁用For every quantity of items purchased<br/><br/>";
			}
			if("de,ca,uk,fr,es,it".contains(amazonPromotionsWarning.getCountry())&&"Entire catalogue".equals(amazonPromotionsWarning.getPurchasedItems())){
				returnInfo+="使用了全局折扣<br/><br/><br/>";
			}else if("jp,com".contains(amazonPromotionsWarning.getCountry())&&"Entire catalog".equals(amazonPromotionsWarning.getPurchasedItems())){
				returnInfo+="使用了全局折扣<br/><br/>";
			}
			if(amazonPromotionsWarning.getPromotionId().startsWith("R-")&&!"Single-use claim code required".equals(amazonPromotionsWarning.getPromotionCode())){//R-
				returnInfo+="R-必须用Single Use Code<br/><br/>";
			}
           /* if(amazonPromotionsWarning.getPromotionId().startsWith("F-")&&"Single-use claim code required".equals(amazonPromotionsWarning.getPromotionCode())){//F- group
				returnInfo+="F-必须用Group Code<br/><br/>";
			}*/
			boolean flag=false;
			for (AmazonPromotionsWarningItem item : amazonPromotionsWarning.getItems()) {
				String key=amazonPromotionsWarning.getAccountName()+"_"+item.getAsin();
				String priceStr=maxPriceMap.get(key);
				if("0".equals(item.getIsMain())&&StringUtils.isNotEmpty(item.getProductNameColor())){
					flag=true;
					mainAsin=item.getAsin();
					mainProduct=item.getProductNameColor();
					break;
				}
				if(priceStr!=null){
					String[] arr=priceStr.split(",");
					if(Float.parseFloat(arr[1])>maxPrice){
						maxPrice=Float.parseFloat(arr[1]);
						maxPriceName=item.getProductNameColor();
					}
				}
			}
			if(flag){
				String key=amazonPromotionsWarning.getAccountName()+"_"+mainAsin;
				String priceStr=maxPriceMap.get(key);
				if(priceStr!=null){
					String[] arr=priceStr.split(",");
					Float mainAsinPrice=Float.parseFloat(arr[1]);
					if(mainAsinPrice!=null&&mainAsinPrice>0){
						String tempName=mainProduct;
						String nameKey=tempName+"_"+amazonPromotionsWarning.getCountry();
						Float minCodePrice=safePriceMap.get(nameKey);
						if(minCodePrice!=null){
							if(amazonPromotionsWarning.getPromotionId().startsWith("R-")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least this quantity of items")){//R-
								if(offPrice/purchase>mainAsinPrice){
									returnInfo+="<font color='red'>减免金额高于最高单价,产品("+mainAsin+")"+tempName+"价格"+mainAsinPrice+",优惠金额："+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"</font><br/><br/>";
								}else{
									returnInfo+="减免金额低于最高单价,产品("+mainAsin+")"+tempName+"价格"+mainAsinPrice+",优惠金额："+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"<br/><br/>";
								}
						    } 
							if(amazonPromotionsWarning.getPromotionId().startsWith("C-")){//不能亏本 就最低折扣价格-保本价
								if(amazonPromotionsWarning.getBuyerGets().contains("Amount off")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least this quantity of items")){
									if(mainAsinPrice-offPrice/purchase-minCodePrice<0){
										returnInfo+="<font color='red'>C-不能亏本(亏本金额"+(new BigDecimal(mainAsinPrice-offPrice/purchase-minCodePrice).setScale(2, 4).floatValue())+"),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"</font><br/><br/>";
									}else{
										returnInfo+="C-不能亏本(盈利"+(new BigDecimal(mainAsinPrice-offPrice/purchase-minCodePrice).setScale(2, 4).floatValue())+"),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"<br/><br/>";
									}
								}else if(amazonPromotionsWarning.getBuyerGets().contains("Amount off")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least amount")){
									if(mainAsinPrice-mainAsinPrice*(offPrice/purchase)-minCodePrice<0){
										returnInfo+="<font color='red'>C-不能亏本(亏本金额"+(new BigDecimal(mainAsinPrice-mainAsinPrice*(offPrice/purchase)-minCodePrice).setScale(2, 4).floatValue())+"),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"</font><br/><br/>";
									}else{
										returnInfo+="C-不能亏本(盈利"+(new BigDecimal(mainAsinPrice-mainAsinPrice*(offPrice/purchase)-minCodePrice).setScale(2, 4).floatValue())+"),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"<br/><br/>";
										
									}
								}else{
									if(mainAsinPrice-mainAsinPrice*offPrice/100-minCodePrice<0){
										returnInfo+="<font color='red'>C-不能亏本(亏本金额"+(new BigDecimal(mainAsinPrice-mainAsinPrice*offPrice/100-minCodePrice).setScale(2, 4).floatValue())+"),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100).setScale(2, 4).floatValue()+"</font><br/><br/>";
									}else{
										returnInfo+="C-不能亏本(盈利"+(new BigDecimal(mainAsinPrice-mainAsinPrice*offPrice/100-minCodePrice).setScale(2, 4).floatValue())+"),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100).setScale(2, 4).floatValue()+"<br/><br/>";
										
									}
								}
							}
							
							if(amazonPromotionsWarning.getPromotionId().startsWith("F-")){//(折扣价-保本价) /保本价  按利润：非淘汰 亏本 ≧15% 淘汰品 亏本 ≧ 50% Alice或Maya 审核
								//淘汰
								if("2".equals(amazonPromotionsWarning.getProType())){//有利润促销
									if(amazonPromotionsWarning.getBuyerGets().contains("Amount off")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least this quantity of items")){
										if(mainAsinPrice-offPrice/purchase-minCodePrice<0){
											returnInfo+="<font color='red'>F-有利润促销(亏本金额"+(new BigDecimal(mainAsinPrice-offPrice/purchase-minCodePrice).setScale(2, 4).floatValue())+"),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"</font><br/><br/>";
										}else{
											returnInfo+="F-有利润促销(盈利"+(new BigDecimal(mainAsinPrice-offPrice/purchase-minCodePrice).setScale(2, 4).floatValue())+"),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"<br/><br/>";
											
										}
									}else if(amazonPromotionsWarning.getBuyerGets().contains("amazonPromotionsWarning")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least amount")){
										if(mainAsinPrice-mainAsinPrice*(offPrice/purchase)-minCodePrice<0){
											returnInfo+="<font color='red'>F-有利润促销(亏本金额"+(new BigDecimal(mainAsinPrice-mainAsinPrice*(offPrice/purchase)-minCodePrice).setScale(2, 4).floatValue())+"),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"</font><br/><br/>";
										}else{
											returnInfo+="F-有利润促销(盈利"+(new BigDecimal(mainAsinPrice-mainAsinPrice*(offPrice/purchase)-minCodePrice).setScale(2, 4).floatValue())+"),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"<br/><br/>";
										}
									}else{
										if(mainAsinPrice-mainAsinPrice*offPrice/100-minCodePrice<0){
											returnInfo+="<font color='red'>F-有利润促销(亏本金额"+(new BigDecimal(mainAsinPrice-mainAsinPrice*offPrice/100-minCodePrice).setScale(2, 4).floatValue())+"),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100).setScale(2, 4).floatValue()+"</font><br/><br/>";
										}else{
											returnInfo+="F-有利润促销(盈利"+(new BigDecimal(mainAsinPrice-mainAsinPrice*offPrice/100-minCodePrice).setScale(2, 4).floatValue())+"),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100).setScale(2, 4).floatValue()+"<br/><br/>";
										}
									}
								}else{
									String nameAndCountry=tempName+"_"+amazonPromotionsWarning.getCountry();
									if(!"4".equals(productPositionMap.get(nameAndCountry))){//在售
										if(amazonPromotionsWarning.getBuyerGets().contains("Amount off")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least this quantity of items")){
											if((minCodePrice-(mainAsinPrice-offPrice/purchase))*100/minCodePrice>=15){
												returnInfo+="<font color='red'>非淘汰亏本 ≧15%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-offPrice/purchase))*100d/minCodePrice))+"%),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"</font><br/><br/>";
											}else{
												returnInfo+="非淘汰亏本 <15%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-offPrice/purchase))*100d/minCodePrice))+"%),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"<br/><br/>";
											}
										}else if(amazonPromotionsWarning.getBuyerGets().contains("Amount off")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least amount")){
											if((minCodePrice-(mainAsinPrice-mainAsinPrice*(offPrice/purchase)))*100/minCodePrice>=15){
												returnInfo+="<font color='red'>非淘汰亏本 ≧15%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*(offPrice/purchase)))*100d/minCodePrice))+"%),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"</font><br/><br/>";
											}else{
												returnInfo+="非淘汰亏本 <15%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*(offPrice/purchase)))*100d/minCodePrice))+"%),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"<br/><br/>";
												
											}
										}else{
											if((minCodePrice-(mainAsinPrice-mainAsinPrice*offPrice/100))*100/minCodePrice>=15){
												returnInfo+="<font color='red'>非淘汰亏本 ≧15%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*offPrice/100d))*100/minCodePrice))+"%),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100).setScale(2, 4).floatValue()+"</font><br/><br/>";
											}else{
												returnInfo+="非淘汰亏本 <15%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*offPrice/100))*100d/minCodePrice))+"%),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100).setScale(2, 4).floatValue()+"<br/><br/>";
												
											}
											
										}
									}else{
										if(amazonPromotionsWarning.getBuyerGets().contains("Amount off")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least this quantity of items")){
											if((minCodePrice-(mainAsinPrice-offPrice/purchase))*100/minCodePrice>=50){
												returnInfo+="<font color='red'>淘汰品亏本 ≧ 50%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-offPrice/purchase))*100d/minCodePrice))+"%),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"</font><br/><br/>";
											}else{
												returnInfo+="淘汰品亏本< 50%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-offPrice/purchase))*100d/minCodePrice))+"%),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"<br/><br/>";
												
											}
										}else if(amazonPromotionsWarning.getBuyerGets().contains("Amount off")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least amount")){
											if((minCodePrice-(mainAsinPrice-mainAsinPrice*(offPrice/purchase)))*100/minCodePrice>=50){
												returnInfo+="<font color='red'>淘汰品亏本 ≧ 50%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*(offPrice/purchase)))*100d/minCodePrice))+"%),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"</font><br/><br/>";
											}else{
												returnInfo+="淘汰品亏本< 50%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*(offPrice/purchase)))*100d/minCodePrice))+"%),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"<br/><br/>";
												
											}
										}else{
											if((minCodePrice-(mainAsinPrice-mainAsinPrice*offPrice/100))*100/minCodePrice>=50){
												returnInfo+="<font color='red'>淘汰品亏本 ≧ 50%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*offPrice/100))*100d/minCodePrice))+"%),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100).setScale(2, 4).floatValue()+"</font><br/><br/>";
											}else{
												returnInfo+="淘汰品亏本 < 50%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*offPrice/100))*100d/minCodePrice))+"%),产品("+mainAsin+")"+tempName+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100).setScale(2, 4).floatValue()+"<br/><br/>";
												
											}
											
										}
									}
									
									
								}
								
							}
							
						}
					}

				}
				
			}else{

				if(maxPrice>0){
					if(amazonPromotionsWarning.getPromotionId().startsWith("R-")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least this quantity of items")){//R-
						if(offPrice/purchase>maxPrice){
							returnInfo+="<font color='red'>减免金额高于最高单价,产品"+maxPriceName+"最高价"+maxPrice+",优惠金额："+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"</font><br/><br/>";
						}else{
							returnInfo+="减免金额不能高于最高单价,产品"+maxPriceName+"最高价"+maxPrice+",优惠金额："+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"<br/><br/>";
						}
					}
				}
				
				if((amazonPromotionsWarning.getPromotionId().startsWith("C-")||amazonPromotionsWarning.getPromotionId().startsWith("F-"))&&!"3".equals(amazonPromotionsWarning.getProType())){
					flag=false;
					for (AmazonPromotionsWarningItem item : amazonPromotionsWarning.getItems()) {
						if(StringUtils.isBlank(item.getProductNameColor())){
							continue;
						}
						String safeKey=item.getProductNameColor()+"_"+amazonPromotionsWarning.getCountry();
						Float minCodePrice=safePriceMap.get(safeKey);
						if(minCodePrice==null){
							continue;
						}
						String priceKey=amazonPromotionsWarning.getAccountName()+"_"+item.getAsin();
						Float mainAsinPrice=priceMap.get(priceKey);
						if(mainAsinPrice!=null){
							if((amazonPromotionsWarning.getPromotionId().startsWith("C-")||amazonPromotionsWarning.getPromotionId().startsWith("F-"))&&"2".equals(amazonPromotionsWarning.getProType())){//不能亏本 就最低折扣价格-保本价
								String msg="F-有利润促销";
	                            if(amazonPromotionsWarning.getPromotionId().startsWith("C-")){
	                            	msg="C-不能亏本";
	                            }
								if(amazonPromotionsWarning.getBuyerGets().contains("Amount off")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least this quantity of items")){
									if(mainAsinPrice-offPrice/purchase-minCodePrice<0){
										returnInfo+="<font color='red'>"+msg+"(亏本金额"+(new BigDecimal(mainAsinPrice-offPrice/purchase-minCodePrice).setScale(2, 4).floatValue())+"),产品"+item.getProductNameColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"</font><br/><br/>";
										flag=true;
										break;
									}else{
										returnInfo+=""+msg+"(盈利"+(new BigDecimal(mainAsinPrice-offPrice/purchase-minCodePrice).setScale(2, 4).floatValue())+"),产品"+item.getProductNameColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"<br/><br/>";
									}
								}else if(amazonPromotionsWarning.getBuyerGets().contains("Amount off")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least amount")){
									if(mainAsinPrice-mainAsinPrice*(offPrice/purchase)-minCodePrice<0){
									  	returnInfo+="<font color='red'>"+msg+"(亏本金额"+(new BigDecimal(mainAsinPrice-mainAsinPrice*(offPrice/purchase)-minCodePrice).setScale(2, 4).floatValue())+"),产品"+item.getProductNameColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"</font><br/><br/>";
									  	flag=true;
										break;
									}else{
										returnInfo+=""+msg+"(盈利"+(new BigDecimal(mainAsinPrice-mainAsinPrice*(offPrice/purchase)-minCodePrice).setScale(2, 4).floatValue())+"),产品"+item.getProductNameColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"<br/><br/>";
									}
								}else{
									if(mainAsinPrice-mainAsinPrice*offPrice/100-minCodePrice<0){
										returnInfo+="<font color='red'>"+msg+"(亏本金额"+(new BigDecimal(mainAsinPrice-mainAsinPrice*offPrice/100-minCodePrice).setScale(2, 4).floatValue())+"),产品"+item.getProductNameColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100).setScale(2, 4).floatValue()+"</font><br/><br/>";
										flag=true;
										break;
									}else{
										returnInfo+=""+msg+"(盈利"+(new BigDecimal(mainAsinPrice-mainAsinPrice*offPrice/100-minCodePrice).setScale(2, 4).floatValue())+"),产品"+item.getProductNameColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100).setScale(2, 4).floatValue()+"<br/><br/>";
										
									}
								}
							}//不能亏本
							
							if(amazonPromotionsWarning.getPromotionId().startsWith("F-")&&!"2".equals(amazonPromotionsWarning.getProType())){//不能亏本 就最低折扣价格-保本价
								Integer pecentNum=15;
								String nameKey=item.getProductNameColor()+"_"+amazonPromotionsWarning.getCountry();
								if("4".equals(productPositionMap.get(nameKey))){//淘汰
									 pecentNum=50;
								}
								if(amazonPromotionsWarning.getBuyerGets().contains("Amount off")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least this quantity of items")){
									if((minCodePrice-(mainAsinPrice-offPrice/purchase))*100d/minCodePrice>=pecentNum){
										returnInfo+="<font color='red'>"+("4".equals(productPositionMap.get(nameKey))?"淘汰品亏本":"非淘汰品亏本")+"≧"+pecentNum+"%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-offPrice/purchase))*100d/minCodePrice))+"%),产品"+item.getProductNameColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"</font><br/><br/>";
									}else{
										returnInfo+=""+("4".equals(productPositionMap.get(nameKey))?"淘汰品亏本":"非淘汰品亏本")+"<"+pecentNum+"%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-offPrice/purchase))*100d/minCodePrice))+"%),产品"+item.getProductNameColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(offPrice/purchase).setScale(2, 4).floatValue()+"<br/><br/>";
										
									}
								}else if(amazonPromotionsWarning.getBuyerGets().contains("Amount off")&&amazonPromotionsWarning.getBuyerPurchases().contains("At least amount")){
									if((minCodePrice-(mainAsinPrice-mainAsinPrice*(offPrice/purchase)))*100d/minCodePrice>=pecentNum){
										returnInfo+="<font color='red'>"+("4".equals(productPositionMap.get(nameKey))?"淘汰品亏本":"非淘汰品亏本")+"≧"+pecentNum+"%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*(offPrice/purchase)))*100d/minCodePrice))+"%),产品"+item.getProductNameColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"</font><br/><br/>";
									}else{
										returnInfo+=""+("4".equals(productPositionMap.get(nameKey))?"淘汰品亏本":"非淘汰品亏本")+"<"+pecentNum+"%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*(offPrice/purchase)))*100d/minCodePrice))+"%),产品"+item.getProductNameColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*(offPrice/purchase)).setScale(2, 4).floatValue()+"<br/><br/>";
										
									}
								}else{
									if((minCodePrice-(mainAsinPrice-mainAsinPrice*offPrice/100d))*100d/minCodePrice>=pecentNum){
										returnInfo+="<font color='red'>"+("4".equals(productPositionMap.get(nameKey))?"淘汰品亏本":"非淘汰品亏本")+"≧"+pecentNum+"%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*offPrice/100d))*100d/minCodePrice))+"%),产品"+item.getProductNameColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100d).setScale(2, 4).floatValue()+"</font><br/><br/>";
									}else{
										returnInfo+=""+("4".equals(productPositionMap.get(nameKey))?"淘汰品亏本":"非淘汰品亏本")+"<"+pecentNum+"%("+(MathUtils.roundUp((minCodePrice-(mainAsinPrice-mainAsinPrice*offPrice/100d))*100d/minCodePrice))+"%),产品"+item.getProductNameColor()+"最低价:"+mainAsinPrice+",优惠金额:"+new BigDecimal(mainAsinPrice*offPrice/100d).setScale(2, 4).floatValue()+"<br/><br/>";
									}
								}
							}
						}
					}
					if(flag){
						return returnInfo;
					}
				}
			}
		}
		return returnInfo;
	}
	
	@RequestMapping(value = "exportPromotions")
	public String exportPromotions(AmazonPromotionsWarning amazonPromotionsWarning,String proId,HttpServletRequest request, HttpServletResponse response, Model model) {
		 List<AmazonPromotionsWarning> list=amazonPromotionsWarningService.find(); 
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
			contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
			
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
	    	HSSFCell cell = null;
	    	List<String> title=Lists.newArrayList("国家","折扣","类型","buyer_purchases","buyer_gets","产品","属性","售价","折扣比例");
	    	for(int i=0;i<title.size();i++){
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
	    	}
	    	
		 if(list!=null&&list.size()>0){
			 Map<String,Float>  safePriceMap=productPriceService.findAllProducSalePrice();
			 Map<String,Float> priceMap=amazonProduct2Service.findAllProductPriceByAccount();
			 Map<String, String> productPositionMap = psiProductEliminateService.findAllProductPosition();
			 Map<String, String> productIsNewMap = psiProductEliminateService.findIsNewMap();
			 int rownum=1;
			 for (AmazonPromotionsWarning warn : list) {
				 String type="";
				 if(StringUtils.isBlank(warn.getProType())){
					 continue;
				 }
				 if("0".equals(warn.getProType())){
					 type="亏本非淘汰品促销";
				 }else if("1".equals(warn.getProType())){
					 type="亏本淘汰品促销";
				 }else if("2".equals(warn.getProType())){
					 type="有利润促销";
				 }else if("3".equals(warn.getProType())){
					 type="特批";
				 }
				 String buyerGets=warn.getBuyerGets();
				 String buyerPurchase=warn.getBuyerPurchases();
				 if(StringUtils.isBlank(buyerGets)||StringUtils.isBlank(buyerPurchase)){
					 continue;
				 }
				 Float proBuyerGets=Float.parseFloat(warn.getBuyerGets().substring(warn.getBuyerGets().lastIndexOf(" ")).trim().replace(",", "."));
			     Float proBuyerPurchase=Float.parseFloat(warn.getBuyerPurchases().substring(warn.getBuyerPurchases().lastIndexOf(" ")).trim().replace(",", "."));
				 if(warn.getItems()!=null){
					for (AmazonPromotionsWarningItem item : warn.getItems()) {
						
						if(StringUtils.isNotBlank(item.getProductNameColor())){
							String priceKey=warn.getAccountName()+"_"+item.getAsin();
							Float salePrice=priceMap.get(priceKey);
							if(salePrice!=null){
								String nameKey=item.getProductNameColor()+"_"+warn.getCountry();
								Float minCodePrice=safePriceMap.get(nameKey);
								if(minCodePrice!=null){
									row = sheet.createRow(rownum++);
									int j=0;
									row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(warn.getCountry())?"US":warn.getCountry().toUpperCase());
									row.getCell(j-1).setCellStyle(contentStyle);
									row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(warn.getPromotionId());
									row.getCell(j-1).setCellStyle(contentStyle);
									row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
									row.getCell(j-1).setCellStyle(contentStyle);
									row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(warn.getBuyerPurchases());
									row.getCell(j-1).setCellStyle(contentStyle);
									row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(warn.getBuyerGets());
									row.getCell(j-1).setCellStyle(contentStyle);
									row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getProductNameColor());
									row.getCell(j-1).setCellStyle(contentStyle);
									String key = item.getProductNameColor() + "_" +warn.getCountry();
				        			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("4".equals(productPositionMap.get(key))?"淘汰":("0".equals(productIsNewMap.get(key))?"普通":"新品"));
				        			row.getCell(j-1).setCellStyle(contentStyle);
				        			
									Float promotionsPrice=0f;
									if(buyerGets.contains("Amount off")&&buyerPurchase.contains("At least this quantity of items")){
										promotionsPrice=salePrice-proBuyerGets/proBuyerPurchase;
									}else if(buyerGets.contains("Amount off")&&buyerPurchase.contains("At least amount")){
										promotionsPrice=salePrice*(1-proBuyerGets/proBuyerPurchase);
									}else{
										promotionsPrice=salePrice*(100-proBuyerGets)/100;
									}
									
									row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salePrice);
									row.getCell(j-1).setCellStyle(cellStyle);
									row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(promotionsPrice/minCodePrice);
									row.getCell(j-1).setCellStyle(cellStyle);
								}
							}
						}
					}
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
	            String fileName = "promotions.xls";
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
	
	
	@RequestMapping(value = "checkPromotions")
	public String checkPromotions(String proId,String proType,String country,String accountName,String buyerPurchases,String buyerGets,String purchaseStr,String offPriceStr,String asin,HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes){
		Map<Integer,Object> map=Maps.newHashMap();
		if(StringUtils.isNotBlank(asin)){
				String[] arrAsin=asin.split(",");
				Map<String,String> priceMap=amazonProduct2Service.findAllProductPrice1ByAccount();
			    Float purchase=Float.parseFloat(purchaseStr.replace(",", "."));
			    Float offPrice=Float.parseFloat(offPriceStr.replace(",", "."));
			    Float minPrice=0f;
			    Float maxPrice=0f;
			    String minNameAndAsin="";
			    String maxNameAndAsin="";
				for (String singleAsin: arrAsin) {
					String key=accountName+"_"+singleAsin;
					PsiSku psiSku=productService.getProductByAsin2(singleAsin);
					String priceStr=priceMap.get(key);
					if(StringUtils.isNotBlank(priceStr)){
						String[] priceArr=priceStr.split(",");
						for (String price: priceArr) {
							Float temp=Float.parseFloat(price);
							if(minPrice==0){
								minPrice=temp;
								minNameAndAsin=psiSku.getNameWithColor();
							}
							if(maxPrice==0){
								maxPrice=temp;
								maxNameAndAsin=psiSku.getNameWithColor();
							}
							if(temp<minPrice){
								minPrice=temp;
								minNameAndAsin=psiSku.getNameWithColor();
							}
							if(temp>maxPrice){
								maxPrice=temp;
								maxNameAndAsin=psiSku.getNameWithColor();
							}
						}
					}
				}
				if(buyerGets.contains("Amount off")&&buyerPurchases.contains("At least this quantity of items")){
					map.put(MathUtils.roundUp(purchase*1d), "折扣力度："+offPrice);
					map.put(MathUtils.roundUp(purchase*1d)*2,"折扣力度："+offPrice);
					map.put(MathUtils.roundUp(purchase*1d)*5,"折扣力度："+offPrice);
					map.put(-1,"买多个减固定金额");
				}else if(buyerGets.contains("Percent off")&&buyerPurchases.contains("At least this quantity of items")){
					if(minPrice!=maxPrice){
						map.put(MathUtils.roundUp(purchase*1d), minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*offPrice/100f).setScale(2,4)+";<br/>");
						map.put(MathUtils.roundUp(purchase*1d)*2,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*2*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*2*offPrice/100f).setScale(2,4)+";<br/>");
						map.put(MathUtils.roundUp(purchase*1d)*5,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*5*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*5*offPrice/100f).setScale(2,4)+";<br/>");
					}else{
						map.put(MathUtils.roundUp(purchase*1d), minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*offPrice/100f).setScale(2,4));
						map.put(MathUtils.roundUp(purchase*1d)*2,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*2*offPrice/100f).setScale(2,4));
						map.put(MathUtils.roundUp(purchase*1d)*5,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*5*offPrice/100f).setScale(2,4));
					}
					map.put(-1,"买多减多");
				}else if(buyerGets.contains("Amount off")&&buyerPurchases.contains("At least amount")){//
					if(minPrice!=maxPrice){
						if(minPrice>=purchase){
							if(maxPrice>=purchase){
								map.put(1,minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+offPrice);
							}else{
								map.put(1,minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0");
							}
						}else{
							if(maxPrice>=purchase){
								map.put(1,minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+offPrice);
							}else{
								map.put(1,minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0");
							}
						}
						if(minPrice*2>=purchase){
							if(maxPrice*2>=purchase){
								map.put(2,minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+offPrice);
							}else{
								map.put(2,minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0");
							}
						}else{
							if(maxPrice*2>=purchase){
								map.put(2,minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+offPrice);
							}else{
								map.put(2,minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0");
							}
						}
						
						if(minPrice*5>=purchase){
							if(maxPrice*5>=purchase){
								map.put(5,minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+offPrice);
							}else{
								map.put(5,minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0");
							}
						}else{
							if(maxPrice*5>=purchase){
								map.put(5,minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+offPrice);
							}else{
								map.put(5,minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0");
							}
						}
					}else{
						if(minPrice>=purchase){
							map.put(1,minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice);
						}else{
							map.put(1,minNameAndAsin+",单价:"+minPrice+",折扣力度：0");
						}
						if(minPrice*2>=purchase){
							map.put(2,minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice);
						}else{
							map.put(2,minNameAndAsin+",单价:"+minPrice+",折扣力度：0");
						}
						if(minPrice*5>=purchase){
							map.put(5,minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice);
						}else{
							map.put(5,minNameAndAsin+",单价:"+minPrice+",折扣力度：0");
						}
					}
					map.put(-1,"买多个减固定金额");
				}else{//At least amount Percent off
					if(minPrice!=maxPrice){
						if(minPrice>=purchase){
							if(maxPrice>=purchase){
								map.put(1,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*offPrice/100f).setScale(2,4));
							}else{
								map.put(1,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0");
							}
						}else{
							if(maxPrice>=purchase){
								map.put(1,minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*offPrice/100f).setScale(2,4));
							}else{
								map.put(1,minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0");
							}
						}
						if(minPrice*2>=purchase){
							if(maxPrice*2>=purchase){
								map.put(2,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*2*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*2*offPrice/100f).setScale(2,4));
							}else{
								map.put(2,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*2*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0");
							}
						}else{
							if(maxPrice*2>=purchase){
								map.put(2,minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*2*offPrice/100f).setScale(2,4));
							}else{
								map.put(2,minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0");
							}
						}
						
						if(minPrice*5>=purchase){
							if(maxPrice*5>=purchase){
								map.put(5,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*5*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*5*offPrice/100f).setScale(2,4));
							}else{
								map.put(5,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*5*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0");
							}
						}else{
							if(maxPrice*5>=purchase){
								map.put(5,minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*5*offPrice/100f).setScale(2,4));
							}else{
								map.put(5,minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0");
							}
						}
					}else{
						if(minPrice>=purchase){
							map.put(1,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*offPrice/100f).setScale(2,4));
						}else{
							map.put(1,minNameAndAsin+",单价:"+minPrice+",折扣力度：0");
						}
						if(minPrice*2>=purchase){
							map.put(2,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*2*offPrice/100f).setScale(2,4));
						}else{
							map.put(2,minNameAndAsin+",单价:"+minPrice+",折扣力度：0");
						}
						if(minPrice*5>=purchase){
							map.put(5,minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*5*offPrice/100f).setScale(2,4));
						}else{
							map.put(5,minNameAndAsin+",单价:"+minPrice+",折扣力度：0");
						}
					}
					map.put(-1,"买多减多");
				}
		}
		model.addAttribute("checkMap",map);
		return "modules/amazoninfo/amazonPromotionsWarningCheck";
	}
	
	public String checkPromotions2(String proId,String proType,String country,String accountName,String buyerPurchases,String buyerGets,String purchaseStr,String offPriceStr,String asin){
		String returnInfo="";
		if(StringUtils.isNotBlank(asin)){
				String[] arrAsin=asin.split(",");
				Map<String,String> priceMap=amazonProduct2Service.findAllProductPrice1ByAccount();
			    Float purchase=Float.parseFloat(purchaseStr.replace(",", "."));
			    Float offPrice=Float.parseFloat(offPriceStr.replace(",", "."));
			    Float minPrice=0f;
			    Float maxPrice=0f;
			    String minNameAndAsin="";
			    String maxNameAndAsin="";
				for (String singleAsin: arrAsin) {
					String key=accountName+"_"+singleAsin;
					PsiSku psiSku=productService.getProductByAsin2(singleAsin);
					String priceStr=priceMap.get(key);
					if(StringUtils.isNotBlank(priceStr)){
						String[] priceArr=priceStr.split(",");
						for (String price: priceArr) {
							Float temp=Float.parseFloat(price);
							if(minPrice==0){
								minPrice=temp;
								minNameAndAsin=psiSku.getNameWithColor();
							}
							if(maxPrice==0){
								maxPrice=temp;
								maxNameAndAsin=psiSku.getNameWithColor();
							}
							if(temp<minPrice){
								minPrice=temp;
								minNameAndAsin=psiSku.getNameWithColor();
							}
							if(temp>maxPrice){
								maxPrice=temp;
								maxNameAndAsin=psiSku.getNameWithColor();
							}
						}
					}
				}
				if(buyerGets.contains("Amount off")&&buyerPurchases.contains("At least this quantity of items")){
					returnInfo+="<span style='color:#ff0033;'>买多个减固定金额</span><br/>";
					returnInfo+="购买数量："+MathUtils.roundUp(purchase*1d)+",折扣力度："+offPrice+"<br/>";
					returnInfo+="购买数量："+MathUtils.roundUp(purchase*1d)*2+",折扣力度："+offPrice+"<br/>";
					returnInfo+="购买数量："+MathUtils.roundUp(purchase*1d)*5+",折扣力度："+offPrice+"<br/>";
					
				}else if(buyerGets.contains("Percent off")&&buyerPurchases.contains("At least this quantity of items")){
					returnInfo+="<span style='color:#ff0033;'>买多减多</span><br/>";
					if(minPrice!=maxPrice){
						returnInfo+="购买数量："+MathUtils.roundUp(purchase*1d)+","+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*offPrice/100f).setScale(2,4)+";<br/><br/>";
						returnInfo+="购买数量："+MathUtils.roundUp(purchase*1d)*2+","+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*2*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*2*offPrice/100f).setScale(2,4)+";<br/><br/>";
						returnInfo+="购买数量："+MathUtils.roundUp(purchase*1d)*5+","+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*5*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*5*offPrice/100f).setScale(2,4)+";<br/>";
					}else{
						returnInfo+="购买数量："+MathUtils.roundUp(purchase*1d)+","+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*offPrice/100f).setScale(2,4)+"<br/><br/>";
						returnInfo+="购买数量："+MathUtils.roundUp(purchase*1d)*2+","+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*2*offPrice/100f).setScale(2,4)+"<br/><br/>";
						returnInfo+="购买数量："+MathUtils.roundUp(purchase*1d)*5+","+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*5*offPrice/100f).setScale(2,4)+"<br/>";
					}
					
				}else if(buyerGets.contains("Amount off")&&buyerPurchases.contains("At least amount")){//
					returnInfo+="<span style='color:#ff0033;'>买多个减固定金额</span><br/>";
					if(minPrice!=maxPrice){
						if(minPrice>=purchase){
							if(maxPrice>=purchase){
								returnInfo+="购买数量：1,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+offPrice+"<br/><br/>";
							}else{
								returnInfo+="购买数量：1,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0"+"<br/><br/>";
							}
						}else{
							if(maxPrice>=purchase){
								returnInfo+="购买数量：1,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+offPrice+"<br/><br/>";
							}else{
								returnInfo+="购买数量：1,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0"+"<br/><br/>";
							}
						}
						if(minPrice*2>=purchase){
							if(maxPrice*2>=purchase){
								returnInfo+="购买数量：2,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+offPrice+"<br/><br/>";
							}else{
								returnInfo+="购买数量：2,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0"+"<br/><br/>";
							}
						}else{
							if(maxPrice*2>=purchase){
								returnInfo+="购买数量：2,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+offPrice+"<br/><br/>";
							}else{
								returnInfo+="购买数量：2,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0"+"<br/><br/>";
							}
						}
						
						if(minPrice*5>=purchase){
							if(maxPrice*5>=purchase){
								returnInfo+="购买数量：5,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+offPrice+"<br/>";
							}else{
								returnInfo+="购买数量：5,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0"+"<br/>";
							}
						}else{
							if(maxPrice*5>=purchase){
								returnInfo+="购买数量：5,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+offPrice+"<br/>";
							}else{
								returnInfo+="购买数量：5,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0"+"<br/>";
							}
						}
					}else{
						if(minPrice>=purchase){
							returnInfo+="购买数量：1,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+"<br/>";
						}else{
							returnInfo+="购买数量：1,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0"+"<br/>";
						}
						if(minPrice*2>=purchase){
							returnInfo+="购买数量：2,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+"<br/>";
						}else{
							returnInfo+="购买数量：2,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0"+"<br/>";
						}
						if(minPrice*5>=purchase){
							returnInfo+="购买数量：5,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+offPrice+"<br/>";
						}else{
							returnInfo+="购买数量：5,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0"+"<br/>";
						}
					}
					
				}else{//At least amount Percent off
					returnInfo+="<span style='color:#ff0033;'>买多减多</span><br/>";
					if(minPrice!=maxPrice){
						if(minPrice>=purchase){
							if(maxPrice>=purchase){
								returnInfo+="购买数量：1,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*offPrice/100f).setScale(2,4)+"<br/><br/>";
							}else{
								returnInfo+="购买数量：1,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0"+"<br/><br/>";
							}
						}else{
							if(maxPrice>=purchase){
								returnInfo+="购买数量：1,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*offPrice/100f).setScale(2,4)+"<br/><br/>";
							}else{
								returnInfo+="购买数量：1,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0"+"<br/><br/>";
							}
						}
						if(minPrice*2>=purchase){
							if(maxPrice*2>=purchase){
								returnInfo+="购买数量：2,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*2*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*2*offPrice/100f).setScale(2,4)+"<br/><br/>";
							}else{
								returnInfo+="购买数量：2,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*2*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0"+"<br/><br/>";
							}
						}else{
							if(maxPrice*2>=purchase){
								returnInfo+="购买数量：2,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*2*offPrice/100f).setScale(2,4)+"<br/><br/>";
							}else{
								returnInfo+="购买数量：2,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0"+"<br/><br/>";
							}
						}
						
						if(minPrice*5>=purchase){
							if(maxPrice*5>=purchase){
								returnInfo+="购买数量：5,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*5*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*5*offPrice/100f).setScale(2,4)+"<br/>";
							}else{
								returnInfo+="购买数量：5,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*5*offPrice/100f).setScale(2,4)+";<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0"+"<br/>";
							}
						}else{
							if(maxPrice*5>=purchase){
								returnInfo+="购买数量：5,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度："+new BigDecimal(maxPrice*5*offPrice/100f).setScale(2,4)+"<br/>";
							}else{
								returnInfo+="购买数量：5,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0;<br/>或"+maxNameAndAsin+",单价:"+maxPrice+",折扣力度：0"+"<br/>";
							}
						}
					}else{
						if(minPrice>=purchase){
							returnInfo+="购买数量：1,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*offPrice/100f).setScale(2,4)+"<br/><br/>";
						}else{
							returnInfo+="购买数量：1,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0"+"<br/><br/>";
						}
						if(minPrice*2>=purchase){
							returnInfo+="购买数量：2,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*2*offPrice/100f).setScale(2,4)+"<br/><br/>";
						}else{
							returnInfo+="购买数量：2,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0"+"<br/><br/>";
						}
						if(minPrice*5>=purchase){
							returnInfo+="购买数量：5,"+minNameAndAsin+",单价:"+minPrice+",折扣力度："+new BigDecimal(minPrice*5*offPrice/100f).setScale(2,4)+"<br/>";
						}else{
							returnInfo+="购买数量：5,"+minNameAndAsin+",单价:"+minPrice+",折扣力度：0"+"<br/>";
						}
					}
					
				}
		}
		
		return returnInfo;
	}
	
	//
	@RequestMapping(value = {"lightningDealList"})
	public String countByProducts(String name,String endDate,@RequestParam(required=false,value="productsName")String[] productsName,AmazonLightningDeals amazonLightningDeals, Model model){
		
		if(StringUtils.isBlank(amazonLightningDeals.getCountry())){
			amazonLightningDeals.setCountry("de");
		}
		if(StringUtils.isNotBlank(endDate)){
			try {
				Date lastDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
				amazonLightningDeals.setStart(DateUtils.addMonths(lastDate,-3));
				amazonLightningDeals.setEnd(DateUtils.addDays(lastDate, 1));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(amazonLightningDeals.getStart()==null){
			Date date=new Date();
			amazonLightningDeals.setStart(DateUtils.addMonths(date,-3));
			amazonLightningDeals.setEnd(date);
		}
		List<String> nameList=amazonPromotionsWarningService.findAllProductName(amazonLightningDeals.getCountry(),amazonLightningDeals.getStart(),amazonLightningDeals.getEnd());
		if(StringUtils.isNotBlank(name)){
			productsName=new String[1];
			productsName[0]=name;
		}
		if(productsName!=null){
			Map<String,Map<String,AmazonLightningDeals>> map=amazonPromotionsWarningService.findLightningDeal(amazonLightningDeals,productsName);
			nameList.removeAll(Lists.newArrayList(productsName));
			model.addAttribute("productNames", nameList);
			model.addAttribute("map",map);
		}else{
			model.addAttribute("productNames",nameList);
			return "modules/amazoninfo/lightningDealList";
		}
		if("uk".contains(amazonLightningDeals.getCountry())){
			model.addAttribute("rate",MathUtils.getRate("GBP","EUR",null));
		}else if("com".contains(amazonLightningDeals.getCountry())){
			model.addAttribute("rate",MathUtils.getRate("USD","EUR",null));
		}else if("ca".contains(amazonLightningDeals.getCountry())){
			model.addAttribute("rate",MathUtils.getRate("CAD","EUR",null));
		}else if("jp".contains(amazonLightningDeals.getCountry())){
			model.addAttribute("rate",MathUtils.getRate("JPY","EUR",null));
		}else{
			model.addAttribute("rate",1f);
		}
		if(productsName!=null){
			model.addAttribute("productsName", Lists.newArrayList(productsName));
		}
		return "modules/amazoninfo/lightningDealList";
	}	
	
	@RequestMapping(value = "lightningDealRankReport")
	public String lightningDealRankReport(String country,String productName,String start,String end,HttpServletRequest request, HttpServletResponse response, Model model) {
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		model.addAttribute("country",country);
		model.addAttribute("productName",productName);
		model.addAttribute("start",start);
		model.addAttribute("end",end);
		
		Date startDate=null;
		Date endDate=null;
				
		try {
			startDate=DateUtils.addDays(formatDay.parse(start),-7);
			endDate=DateUtils.addDays(formatDay.parse(end),7);
			if(endDate.after(new Date())){
				endDate=new Date();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Map<String,Integer> rankMap=amazonPostsDetailService.getProductRank(productName,startDate,endDate,country);
		model.addAttribute("rankMap", rankMap);
		
		List<String> xAxis  = Lists.newArrayList();
		while(endDate.after(startDate)||endDate.equals(startDate)){
			 String key = formatDay.format(startDate);
		     xAxis.add(key);
		     startDate = DateUtils.addDays(startDate, 1);
		}
		model.addAttribute("xAxis",xAxis);
		return "modules/amazoninfo/lightningDealRankReportList";
	}
	
	@RequestMapping(value = "lightningDealSalesReport")
	public String lightningDealSalesReport(String country,String productName,String start,String end,HttpServletRequest request, HttpServletResponse response, Model model) {
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		model.addAttribute("country",country);
		model.addAttribute("productName",productName);
		model.addAttribute("start",start);
		model.addAttribute("end",end);
		model.addAttribute("type","1");
		Date startDate=null;
		Date endDate=null;
				
		try {
			startDate=DateUtils.addDays(formatDay.parse(start),-7);
			endDate=DateUtils.addDays(formatDay.parse(end),7);
			if(endDate.after(new Date())){
				endDate=new Date();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Map<String,Integer> salesMap=saleReportService.getSales(productName,startDate,endDate,country);
		model.addAttribute("salesMap", salesMap);
		
		List<String> xAxis  = Lists.newArrayList();
		while(endDate.after(startDate)||endDate.equals(startDate)){
			 String key = formatDay.format(startDate);
		     xAxis.add(key);
		     startDate = DateUtils.addDays(startDate, 1);
		}
		model.addAttribute("xAxis",xAxis);
		return "modules/amazoninfo/lightningDealSalesReportList";
	}
	
	@RequestMapping(value = "exportLightDeal")
	public String exportLightDeal(AmazonLightningDeals amazonLightningDeals,HttpServletRequest request, HttpServletResponse response, Model model) {
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
			contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
			
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
	        
	        HSSFCellStyle cellStyle1 = wb.createCellStyle();
	        cellStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
	        cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	        cellStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
	        cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	        cellStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
	        cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
	        cellStyle1.setRightBorderColor(HSSFColor.BLACK.index);
	        cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
	        cellStyle1.setTopBorderColor(HSSFColor.BLACK.index);
	        
	        HSSFFont font1 = wb.createFont();
			font1.setColor(HSSFFont.COLOR_RED);  
			font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
			cellStyle1.setFont(font1);
			
	    	HSSFCell cell = null;
	    	amazonLightningDeals.setStatus("Ended");
	    	Float rate=1f;
	    	String rateName="";
	    	if("uk".contains(amazonLightningDeals.getCountry())){
				rate=MathUtils.getRate("GBP","EUR",null);
				rateName="GBP/EUR";
			}else if("com".contains(amazonLightningDeals.getCountry())){
				rate=MathUtils.getRate("USD","EUR",null);
				rateName="USD/EUR";
			}else if("ca".contains(amazonLightningDeals.getCountry())){
				rate=MathUtils.getRate("CAD","EUR",null);
				rateName="CAD/EUR";
			}else if("jp".contains(amazonLightningDeals.getCountry())){
				rate=MathUtils.getRate("JPY","EUR",null);
				rateName="JPY/EUR";
			}
	    	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    	SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy-MM-dd");
	    	Map<String,List<AmazonLightningDeals>> map=amazonPromotionsWarningService.findLightningDealList(amazonLightningDeals);
	    	Map<String,Map<String, Float>> rateMap=amazonProduct2Service.getRate(DateUtils.addDays(amazonLightningDeals.getStart(), -1),DateUtils.addDays(amazonLightningDeals.getEnd(),1));
	    	List<String> title=Lists.newArrayList("国家","时间","产品","闪促销量","两天前销量","一天前销量","当天销量","一天后销量","两天后销量","两天前排名","一天前排名","当天排名","一天后排名","两天后排名","上月平均session","session","转化率","上月平均转化率","原价(€)","促销价(€)","loss/pcs(€)","loss(€)");
	    	for(int i=0;i<title.size();i++){
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
	    	}
	    	int index=1;
	    	for ( Map.Entry<String,List<AmazonLightningDeals>> temp: map.entrySet()) {
	    		List<AmazonLightningDeals> list=temp.getValue();
	    		int m=0;
	    		for (AmazonLightningDeals deal : list) {
	    			 int j=0;
					 row = sheet.createRow(index++);
					 if(m==0){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(deal.getCountry())?"us":deal.getCountry());
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dateFormat.format(deal.getStart())+"-"+dateFormat.format(deal.getEnd()));
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }
					 row.getCell(j-2).setCellStyle(contentStyle);
					 row.getCell(j-1).setCellStyle(contentStyle);
					 String date=dateFormat2.format(deal.getEnd());
					 Float tempRate=1f;
					 if(!"de,fr,it,es".contains(deal.getCountry())){
						 if(rateMap.get(date)!=null&&rateMap.get(date).get(rateName)!=null){
							 tempRate=rateMap.get(date).get(rateName);
						 }else{
							 tempRate=rate;
						 }
					 }
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getProductName());
					 row.getCell(j-1).setCellStyle(contentStyle);
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getActualQuantity());
					 row.getCell(j-1).setCellStyle(contentStyle);
					 if(deal.getSale1()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getSale1());
					 }
					 row.getCell(j-1).setCellStyle(contentStyle);
					 if(deal.getSale2()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getSale2());
					 }
					 row.getCell(j-1).setCellStyle(contentStyle);
					 if(deal.getSale3()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getSale3());
					 }
					 row.getCell(j-1).setCellStyle(contentStyle);
					 if(deal.getSale4()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getSale4());
					 }
					 row.getCell(j-1).setCellStyle(contentStyle);
					 if(deal.getSale5()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getSale5());
					 }
					 row.getCell(j-1).setCellStyle(contentStyle);
					 
					 if(deal.getRank1()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getRank1());
					 }
					 row.getCell(j-1).setCellStyle(contentStyle);
					 
					 if(deal.getRank2()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getRank2());
					 }
					 row.getCell(j-1).setCellStyle(contentStyle);
					 
					 if(deal.getRank3()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getRank3());
					 }
					 row.getCell(j-1).setCellStyle(contentStyle);
					 
					 if(deal.getRank4()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getRank4());
					 }
					 row.getCell(j-1).setCellStyle(contentStyle);
					 
					 if(deal.getRank5()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getRank5());
					 }
					 row.getCell(j-1).setCellStyle(contentStyle);
					 
					 if(deal.getSession1()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getSession1());
					 }
					 row.getCell(j-1).setCellStyle(contentStyle);
					 
					 if(deal.getSession2()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getSession2());
					 }
					 row.getCell(j-1).setCellStyle(contentStyle);
					 
					 
					 if(deal.getConv1()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getConv1());
					 }
					 row.getCell(j-1).setCellStyle(cellStyle);
					 
					 if(deal.getConv2()==null){
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getConv2());
					 }
					 row.getCell(j-1).setCellStyle(cellStyle);
					 
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getSalePrice()*tempRate);
					 row.getCell(j-1).setCellStyle(cellStyle);
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getDealPrice()*tempRate);
					 row.getCell(j-1).setCellStyle(cellStyle);
					
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(deal.getDealPrice()*tempRate-deal.getSafePrice());
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue((deal.getDealPrice()*tempRate-deal.getSafePrice())*deal.getActualQuantity());
					 
                     if(deal.getDealPrice()*tempRate-deal.getSafePrice()<0){
                    	  row.getCell(j-2).setCellStyle(cellStyle1);
                    	  row.getCell(j-1).setCellStyle(cellStyle1);
					 }else{
						  row.getCell(j-2).setCellStyle(cellStyle);
                   	      row.getCell(j-1).setCellStyle(cellStyle);
					 }
				}
	    		sheet.addMergedRegion(new CellRangeAddress(index-list.size(),index-1,0,0));
	    		sheet.addMergedRegion(new CellRangeAddress(index-list.size(),index-1,1,1));
			}
		    for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
		    }
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
	            String fileName = "lightningDeals.xls";
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
	
	
	
	@RequestMapping(value = {"lightningDealAllList"})
	public String lightningDealAllList(AmazonLightningDeals amazonLightningDeals, Model model){
		if(StringUtils.isBlank(amazonLightningDeals.getCountry())){
			amazonLightningDeals.setCountry("de");
		}
		if(amazonLightningDeals.getStart()==null){
			Date date=new Date();
			amazonLightningDeals.setStart(DateUtils.addMonths(date,-3));
			amazonLightningDeals.setEnd(date);
		}
		Map<String,List<AmazonLightningDeals>> map=amazonPromotionsWarningService.findLightningDealList(amazonLightningDeals);
		model.addAttribute("map",map);
		if("uk".contains(amazonLightningDeals.getCountry())){
			model.addAttribute("rate",MathUtils.getRate("GBP","EUR",null));
		}else if("com".contains(amazonLightningDeals.getCountry())){
			model.addAttribute("rate",MathUtils.getRate("USD","EUR",null));
		}else if("ca".contains(amazonLightningDeals.getCountry())){
			model.addAttribute("rate",MathUtils.getRate("CAD","EUR",null));
		}else if("jp".contains(amazonLightningDeals.getCountry())){
			model.addAttribute("rate",MathUtils.getRate("JPY","EUR",null));
		}else{
			model.addAttribute("rate",1f);
		}
		model.addAttribute("amazonLightningDeals",amazonLightningDeals);
		model.addAttribute("groupType", psiTypeGroupService.getAllList());
		return "modules/amazoninfo/lightningDealAllList";
	}
	
	
	@RequestMapping(value = "exportPromotionsByDate")
	public String exportPromotionsByDate(AmazonPromotionsWarning amazonPromotionsWarning,String type,HttpServletRequest request, HttpServletResponse response) {
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
			contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
			
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
	        
	        HSSFCellStyle cellStyle1 = wb.createCellStyle();
	        cellStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
	        cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	        cellStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
	        cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	        cellStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
	        cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
	        cellStyle1.setRightBorderColor(HSSFColor.BLACK.index);
	        cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
	        cellStyle1.setTopBorderColor(HSSFColor.BLACK.index);
	        
	        HSSFFont font1 = wb.createFont();
			font1.setColor(HSSFFont.COLOR_RED);  
			font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
			cellStyle1.setFont(font1);
			
	    	HSSFCell cell = null;
	    	SaleReport saleReport=new SaleReport();
	    	saleReport.setStart(amazonPromotionsWarning.getCreateDate());
	    	saleReport.setEnd(amazonPromotionsWarning.getUpdateDate());
	    	saleReport.setSearchType(type);
	    	saleReport.setCountry(amazonPromotionsWarning.getCountry());
	    	//country date name
	    	Map<String,Map<String, Map<String,SaleReport>>> salesMap=saleReportService.getSalesByProductName(saleReport,null);
	    	Map<String,Map<String, Map<String,SaleReport>>> proMap=amazonOrderService.getPromotions(saleReport);
	    	//name_country
	    	Map<String,Float> priceMap=productPriceService.findAllProducSalePrice("EUR");
	    	 Map<String,String> typeMap=psiTypeGroupService.getLineNameByName();
	    	List<String> title=Lists.newArrayList("国家","时间","产品","产品线","促销量","总销量","折扣率","折扣销售额","loss(€)");
	    	for(int i=0;i<title.size();i++){
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
	    	}
	    	
	    	Set<String> productSet=Sets.newHashSet();
			if(!UserUtils.hasPermission("amazoninfo:productSalePrice:all")){
				List<String> temp = psiProductGroupUserService.getProductByGroupUser();
				for (String product: temp) {
					productSet.add(product.substring(0,product.lastIndexOf("_")));
				}
			}
	    	
	    	int index=1;
	    	for (Map.Entry<String,Map<String,Map<String,SaleReport>>> entity : proMap.entrySet()) {
				 String country=entity.getKey();
				 for (Map.Entry<String,Map<String,SaleReport>> dateEntity: entity.getValue().entrySet()) {
					String date=dateEntity.getKey();
					for (Map.Entry<String,SaleReport> temp: dateEntity.getValue().entrySet()) {
						 String name=temp.getKey();
						 if(productSet.size()>0&&!productSet.contains(name)){
							 continue;
						 }
						 SaleReport report=temp.getValue();
						 int j=0;
						 row = sheet.createRow(index++);
                         row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("com".equals(country)?"us":country);
                         row.getCell(j-1).setCellStyle(contentStyle);
                         row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
                         row.getCell(j-1).setCellStyle(contentStyle);
                         row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
                         row.getCell(j-1).setCellStyle(contentStyle);
                         if(typeMap.get(name)!=null){
                        	   row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(typeMap.get(name));
                         }else{
                        	   row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
                         }
                         row.getCell(j-1).setCellStyle(contentStyle);
                         row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSalesVolume());
                         row.getCell(j-1).setCellStyle(contentStyle);
                         if(salesMap!=null&&salesMap.get(country)!=null&&salesMap.get(country).get(date)!=null
                        		 &&salesMap.get(country).get(date).get(name)!=null&&salesMap.get(country).get(date).get(name).getSalesVolume()>0){
                        	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(salesMap.get(country).get(date).get(name).getSalesVolume());
                             row.getCell(j-1).setCellStyle(contentStyle);
                             row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSalesVolume()*1.0f/salesMap.get(country).get(date).get(name).getSalesVolume());
                             row.getCell(j-1).setCellStyle(cellStyle);
                         }else{
                        	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
                             row.getCell(j-1).setCellStyle(contentStyle);
                             row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
                             row.getCell(j-1).setCellStyle(contentStyle);
                         }
                         
                         row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSales());
                         row.getCell(j-1).setCellStyle(cellStyle);
                         
                         if(priceMap.get(name+"_"+country)!=null){
                        	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(report.getSales()-priceMap.get(name+"_"+country)*report.getSalesVolume());
                        	  if(report.getSales()-priceMap.get(name+"_"+country)*report.getSalesVolume()>0){
                                  row.getCell(j-1).setCellStyle(cellStyle);
                        	  }else{
                                  row.getCell(j-1).setCellStyle(cellStyle1);
                        	  }
                         }else{
                        	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
                             row.getCell(j-1).setCellStyle(cellStyle);
                         }
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
	            String fileName = "PromotionsReport.xls";
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
	
	@RequestMapping(value = "findProductNameList")
	@ResponseBody
	public List<String>  findProductNameList(String country,String start,String end) throws ParseException {
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		Date startDate=dateFormat.parse(start);
		Date endDate=dateFormat.parse(end);
		return amazonPromotionsWarningService.findAllProductName(country,startDate,endDate);
	}
}