package com.springrain.erp.modules.amazoninfo.web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct2;
import com.springrain.erp.modules.amazoninfo.entity.ProductPriceApproval;
import com.springrain.erp.modules.amazoninfo.entity.ProductPriceApprovalTemp;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.ProductPriceApprovalService;
import com.springrain.erp.modules.amazoninfo.service.ProductPriceService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

/**
 * 产品特殊定价管理Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/productPriceApproval")
public class ProductPriceApprovalController extends BaseController {
	@Autowired
	private ProductPriceApprovalService productPriceApprovalService;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private AmazonProductService amazonProductService;
	
	@Autowired
	private MailManager mailManager;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private ProductPriceService productPriceService;
	
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	
	private static Map<String, String> currencySymbolMap = Maps.newHashMap();
	
	static{
		currencySymbolMap.put("de","EUR");
		currencySymbolMap.put("fr","EUR");
		currencySymbolMap.put("it","EUR");
		currencySymbolMap.put("es","EUR");
		currencySymbolMap.put("uk","GBP");
		currencySymbolMap.put("com","USD");
		currencySymbolMap.put("ca","CAD");
		currencySymbolMap.put("jp","JPY");
		currencySymbolMap.put("mx","MXN");
	}
	
	@ModelAttribute
	public ProductPriceApproval get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return productPriceApprovalService.get(id);
		}else{
			return new ProductPriceApproval();
		}
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(ProductPriceApproval productPriceApproval, HttpServletRequest request, HttpServletResponse response, Model model) {
		if (productPriceApproval.getState() == null) {
			productPriceApproval.setState("0");
		}
		Page<ProductPriceApproval> page = productPriceApprovalService.find(new Page<ProductPriceApproval>(request, response), productPriceApproval); 
        Map<String, Map<String, AmazonProduct2>> priceMap = amazonProduct2Service.findAllSalePrice();
        Map<String,BigDecimal> safePriceMap = this.productPriceService.getAmazonSafePrice();
        model.addAttribute("safePriceMap", safePriceMap);
        model.addAttribute("priceMap", priceMap);
        model.addAttribute("page", page);
		return "modules/amazoninfo/productPriceApprovalList";
	}
	
	@RequestMapping(value = "form")
	public String form(ProductPriceApproval productPriceApproval, Model model) {
		if (productPriceApproval.getSaleStartDate() == null) {
			Date date = new Date();
			productPriceApproval.setSaleStartDate(DateUtils.addDays(date, 1));
			productPriceApproval.setSaleEndDate(DateUtils.addMonths(date, 1));
		}
		if(StringUtils.isNotEmpty(productPriceApproval.getAccountName())){
			model.addAttribute("sku", amazonProductService.findSkuByAccount(productPriceApproval.getAccountName()));
		}
		Map<String,String> productMap = psiProductService.findProductTypeMap();
        model.addAttribute("productMapJson", JSON.toJSON(productMap));
		model.addAttribute("productPriceApproval", productPriceApproval);
		Map<String,String> accountMap=amazonAccountConfigService.findCountryByAccount();
		model.addAttribute("accountMap", accountMap);
		if (productPriceApproval.getId() == null) {
			return "modules/amazoninfo/productPriceApprovalAdd";
		} else {
			return "modules/amazoninfo/productPriceApprovalForm";
		}
	}
	
	@RequestMapping(value = "view")
	public String view(ProductPriceApproval productPriceApproval, Model model) {
		if(StringUtils.isNotEmpty(productPriceApproval.getAccountName())){
			model.addAttribute("sku", amazonProductService.findSkuByAccount(productPriceApproval.getAccountName()));
		}
		Map<String,String> productMap = psiProductService.findProductTypeMap();
        model.addAttribute("productMapJson", JSON.toJSON(productMap));
		model.addAttribute("productPriceApproval", productPriceApproval);
		return "modules/amazoninfo/productPriceApprovalForm";
	}

	@RequestMapping(value = "save")
	public String save(ProductPriceApproval productPriceApproval, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, productPriceApproval)){
			return form(productPriceApproval, model);
		}
		//修改后需重新审批
		productPriceApproval.setState("0");
		productPriceApproval.setIsActive("0");
		productPriceApproval.setCreateBy(UserUtils.getUser());
		productPriceApproval.setCreateDate(new Date());
		productPriceApproval.setReviewUser(null);
		productPriceApprovalService.save(productPriceApproval);
		addMessage(redirectAttributes, "操作成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/productPriceApproval/?repage";
	}
	
	@RequestMapping(value = "addForm")
	public String addForm(ProductPriceApprovalTemp productPriceApprovalTemp, Model model) {
		if(StringUtils.isNotEmpty(productPriceApprovalTemp.getAccountName())){
			model.addAttribute("sku", amazonProductService.findSkuByAccount(productPriceApprovalTemp.getAccountName()));
		}
		model.addAttribute("productPriceApprovalTemp", productPriceApprovalTemp);
		Map<String,String> accountMap=amazonAccountConfigService.findCountryByAccount();
		model.addAttribute("accountMap", accountMap);
		return "modules/amazoninfo/productPriceApprovalAdd";
	}
	
	@RequestMapping(value = "batchSave")
	public String batchSave(ProductPriceApprovalTemp productPriceApprovalTemp, Model model, RedirectAttributes redirectAttributes) {
		String rs = "";
		List<ProductPriceApproval> list = Lists.newArrayList();
		Map<String,String> accountCountryMap=amazonAccountConfigService.findCountryByAccount();
		productPriceApprovalTemp.setCountry(accountCountryMap.get(productPriceApprovalTemp.getAccountName()));
		for (Iterator<ProductPriceApproval> iterator =  productPriceApprovalTemp.getPrices().iterator(); iterator.hasNext();) {
			ProductPriceApproval price = iterator.next();
			price.setCountry(productPriceApprovalTemp.getCountry());
			price.setReason(productPriceApprovalTemp.getReason());
			price.setAccountName(productPriceApprovalTemp.getAccountName());
			price.setState("0");
			price.setIsActive("0");
			price.setIsMonitor("0");
			price.setNoticeFlag("0");
			if (price.getSaleStartDate().after(price.getSaleEndDate())) {
				Date start = price.getSaleEndDate();
				price.setSaleStartDate(price.getSaleEndDate());
				price.setSaleEndDate(start);
			}
			price.setCreateBy(UserUtils.getUser());
			price.setCreateDate(new Date());
			AmazonProduct2 product = amazonProduct2Service.getProductByAccount(price.getAccountName(), price.getSku());
			price.setType("1");
			if (product != null) {
				if (product.getSalePrice() == null) {
					product.setSalePrice(product.getPrice());
				}
				if (product.getSalePrice()!=null && product.getSalePrice()< price.getPrice()) {
					price.setType("2"); //涨价
				}
			}
			list.add(price);
		}
		if (list.size() > 0) {
			productPriceApprovalService.save(list);
			//邮件通知相关人员
        	StringBuffer contents= new StringBuffer("");
        	StringBuffer contents1= new StringBuffer("");
        	int count=1;
    		contents1.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
    		contents1.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>平台</th><th>产品名称</th><th>sku</th><th>审批价格</th></tr>");
        	for (ProductPriceApproval productPriceApproval : list) {
        		contents1.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
        		contents1.append("<td>"+(count++)+"</td><td>"+("com".equals(productPriceApproval.getCountry())?"us":productPriceApproval.getCountry()).toUpperCase()+"</td>");
        		contents1.append("<td>"+productPriceApproval.getProductName()+"</td><td>"+productPriceApproval.getSku()+"</td><td>"+productPriceApproval.getPrice()+"</td>");
        		contents1.append("</tr>");
			}
    		contents1.append("</table><br/><br/>");
        	if (StringUtils.isNotEmpty(contents1)) {
        		List<User> userList = systemService.findUserByPermission("amazoninfo:productPrice:approval");
            	String toAddress = "";
            	String toUser = "";
            	StringBuffer buf1= new StringBuffer();
            	StringBuffer buf2= new StringBuffer();
            	if (userList==null || userList.size() == 0) {
            		addMessage(redirectAttributes, "未找到产品价格审批人");
            		return "redirect:"+Global.getAdminPath()+"/amazoninfo/productPriceApproval/?repage";
				}
            	for (User user : userList) {
            		buf1.append( user.getEmail() + ",");
            		buf2.append(user.getLoginName() + "|");
    			}
            	toAddress=buf1.toString();
            	toUser=buf2.toString();
            	if (StringUtils.isNotEmpty(toAddress)) {
            		toAddress = toAddress.substring(0, toAddress.length()-1);
	            	contents.append("<p><span style='font-size:20px'>Hi all,<br/>&nbsp;&nbsp;&nbsp;&nbsp;系统有待审批的产品定价,请尽快处理。</span>" +
	            			"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/productPriceApproval'>点击处理</a></p>");
	            	contents.append(contents1);
	            	final MailInfo mailInfo = new MailInfo(toAddress, "产品价格审批提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
	    			mailInfo.setContent(contents.toString());
	    			mailInfo.setCcToAddress(list.get(0).getCreateBy().getEmail());	//抄送给申请人
	    			new Thread(){
	    			    public void run(){
	    					mailManager.send(mailInfo);
	    				}
	    			}.start();

	    			toUser = toUser.substring(0, toUser.length()-1);
	    			WeixinSendMsgUtil.sendTextMsgToUser(toUser, "价格审批提醒：\n系统有待审批的产品定价,请尽快处理。");
				} else {
					logger.warn("未找到产品价格审批人");
				}
			}
		}
		addMessage(redirectAttributes, rs);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/productPriceApproval/?repage";
	}
	
	@RequestMapping(value = "approval")
	public String approval(ProductPriceApproval productPriceApproval, String state, HttpServletRequest request, 
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		if (productPriceApproval != null) {
			if (UserUtils.getUser().getId().equals(productPriceApproval.getCreateBy().getId())) {
				addMessage(redirectAttributes, "审批操作失败,审批人和提交人相同,不允许进行审批操作！");
		        return "redirect:"+Global.getAdminPath()+"/amazoninfo/productPriceApproval/?repage";
			}
			productPriceApproval.setState(state);
			productPriceApproval.setReviewUser(UserUtils.getUser());
			productPriceApproval.setReviewDate(new Date());
			if ("1".equals(state)) {	//满足条件时立即生效
				Date date = new Date();
				if (productPriceApproval.getSaleStartDate().before(date) 
						&& DateUtils.addDays(productPriceApproval.getSaleEndDate(), 1).after(date)) {
					productPriceApproval.setIsActive("1");
				}
				if (productPriceApproval.getWarnQty() != null || (productPriceApproval.getChangeQty() != null && productPriceApproval.getChangePrice() != null)) {	//审批通过判断是否需要监控
					productPriceApproval.setIsMonitor("1");
				}
			}
			productPriceApprovalService.save(productPriceApproval);
			//如果价格低于保本价并且审核通过,邮件告知相关人员
			if ("1".equals(state)) {
				//实时汇率
				float rate = MathUtils.getRate(currencySymbolMap.get(productPriceApproval.getCountry()), "USD", null);
				//保本价(美元)
				Float price = productPriceService.getPriceBySkuAndCountry(productPriceApproval.getCountry(), productPriceApproval.getSku());
				float ratePrice = productPriceApproval.getPrice() * rate;
				if (price != null && price > ratePrice) {
					List<ProductPriceApproval> list = Lists.newArrayList(productPriceApproval);
					Map<Integer, Float> priceMap = Maps.newHashMap();
					priceMap.put(productPriceApproval.getId(), price);
					
					String emailContent = emailString(list, priceMap);
					String toAddress = "cuichun@inateck.com,tim@inateck.com";
					final MailInfo mailInfo = new MailInfo(toAddress, "产品审批价格低于亚马逊保本价提醒", new Date());
					mailInfo.setContent(emailContent);
					new Thread(){
					    public void run(){
							mailManager.send(mailInfo);
						}
					}.start();
				}
			}
			if (productPriceApproval.getCreateBy() != null) {
				final MailInfo mailInfo = new MailInfo(productPriceApproval.getCreateBy().getEmail(), "产品价格审批提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
				String result = "1".equals(state)?"审批通过":"被否决";
				String contents = "<p><span style='font-size:20px'>Hi "+productPriceApproval.getCreateBy().getName()+",<br/>&nbsp;&nbsp;&nbsp;&nbsp;你提交的产品价格审批申请已经"+result+"，请知悉。</span>";
				mailInfo.setContent(contents);
				new Thread(){
				    public void run(){
						mailManager.send(mailInfo);
					}
				}.start();
				WeixinSendMsgUtil.sendTextMsgToUser(productPriceApproval.getCreateBy().getLoginName(), "你提交的产品价格审批申请已经"+result+"，请知悉。");
			}
		}
		addMessage(redirectAttributes, "审批操作成功！");
        return "redirect:"+Global.getAdminPath()+"/amazoninfo/productPriceApproval/?repage";
	}
	
	@RequestMapping(value = "batchApproval")
	public String batchApproval(@RequestParam("eid[]")String[] eid, String state, HttpServletRequest request, 
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUser();
		Set<String> emailSet = Sets.newHashSet();
		Set<String> userSet = Sets.newHashSet();

		List<ProductPriceApproval> list = Lists.newArrayList();
		Map<Integer, Float> priceMap = Maps.newHashMap();
		boolean flag = false;
		for (String str : eid) {
			Integer id = Integer.parseInt(str);
			ProductPriceApproval product = productPriceApprovalService.get(id);
			if (UserUtils.getUser().getId().equals(product.getCreateBy().getId())) {
				flag = true;
				continue;
			}
			product.setState(state);
			product.setReviewUser(user);
			product.setReviewDate(new Date());
			if ("1".equals(state)) {	//满足条件时立即生效
				if (product.getWarnQty() != null || (product.getChangeQty() != null && product.getChangePrice() != null)) {	//审批通过判断是否需要监控
					product.setIsMonitor("1");
				}
				Date date = new Date();
				if (product.getSaleStartDate().before(date) && DateUtils.addDays(product.getSaleEndDate(), 1).after(date)) {
					product.setIsActive("1");
				}
				//实时汇率
				float rate = MathUtils.getRate(currencySymbolMap.get(product.getCountry()), "USD", null);
				//保本价(美元)
				Float price = productPriceService.getPriceBySkuAndCountry(product.getCountry(), product.getSku());
				float ratePrice = product.getPrice() * rate;
				if (price != null && price > ratePrice) {
					list.add(product);
					priceMap.put(product.getId(), price);
				}
			}
			emailSet.add(product.getCreateBy().getEmail());
			userSet.add(product.getCreateBy().getLoginName());
			productPriceApprovalService.save(product);
		}

		if (list.size() > 0) {
			String emailContent = emailString(list, priceMap);
			String toAddress = "cuichun@inateck.com,tim@inateck.com";
			final MailInfo mailInfo = new MailInfo(toAddress, "产品审批价格低于亚马逊保本价提醒", new Date());
			mailInfo.setContent(emailContent);
			new Thread(){
			    public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
		
		if (emailSet.size() > 0) {
			String toAddress = "";
			StringBuffer buf= new StringBuffer();
			for (String string : emailSet) {
				buf.append(string + ",");
			}
			toAddress=buf.toString();
        	toAddress = toAddress.substring(0, toAddress.length()-1);
        	final MailInfo mailInfo = new MailInfo(toAddress, "产品价格审批提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
        	String result = "1".equals(state)?"审批通过":"被否决";
			String contents = "<p><span style='font-size:20px'>Hi,<br/>&nbsp;&nbsp;&nbsp;&nbsp;你提交的产品价格审批申请已经"+result+"，请知悉。</span>";
			mailInfo.setContent(contents);
			new Thread(){
			    public void run(){
					mailManager.send(mailInfo);
				}
			}.start();

			String toUser = "";
			StringBuffer buff= new StringBuffer();
			for (String string : userSet) {
				buff.append( string + "|");
			}
			toUser = buff.toString();
			toUser = toUser.substring(0, toUser.length()-1);
			WeixinSendMsgUtil.sendTextMsgToUser(toUser, "你提交的产品价格审批申请已经"+result+"，请知悉。");
		}
		if (flag) {
			addMessage(redirectAttributes, "部分操作失败,系统自动跳过审批人和提交人相同的审批信息,请仔细核对！");
		} else {
			addMessage(redirectAttributes, "审批操作成功！");
		}
        return "redirect:"+Global.getAdminPath()+"/amazoninfo/productPriceApproval/?repage";
	}
	
	@RequestMapping(value = {"getPrice"})
	@ResponseBody
	public AmazonProduct2 getPrice(String country,String sku) {
		return amazonProduct2Service.getProduct(country, sku);
	}
	
	@RequestMapping(value = {"getSkus"})
	@ResponseBody
	public List<Map<String, String>> getSkus(String accountName, String productName) {
		List<Map<String, String>> skus = Lists.newArrayList();
		List<Map<String, String>> list = amazonProductService.findSkuByAccount(accountName);
		if(StringUtils.isEmpty(productName)){
			return null;
		}
		for (Map<String, String> map : list) {
			if (map.get("value").contains(productName+"[")) {
				skus.add(map);
			}
		}
		return skus;
	}
	
	//组合邮件内容
	private static String emailString(List<ProductPriceApproval> list, Map<Integer, Float> amazonPrice){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = format.format(new Date());
		String cUserName = UserUtils.getUser().getName();
		StringBuffer contents= new StringBuffer("");
    	StringBuffer contents1= new StringBuffer("");
		contents1.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
		contents1.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>平台</th><th>产品名称</th><th>sku</th><th>审批价格</th><th>对美元汇率</th><th>审批价格($)</th><th>亚马逊保本价($)</th><th>审批人</th><th>审批时间</th></tr>");
		int count = 1;
		for (ProductPriceApproval approval : list) {
			//实时汇率
			float rate = MathUtils.getRate(currencySymbolMap.get(approval.getCountry()), "USD", null);
    		contents1.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
    		contents1.append("<td>"+(count++)+"</td><td>"+approval.getCountry().toUpperCase()+"</td><td>"+approval.getProductName()+"</td>");
    		contents1.append("<td>"+approval.getSku()+"</td><td>"+approval.getPrice()+"</td><td>"+rate+"</td><td>"+String.format("%.2f", approval.getPrice() * rate)+"</td><td>"+amazonPrice.get(approval.getId())+"</td>");
    		contents1.append("<td>"+cUserName+"</td><td>"+now+"</td>");
    		contents1.append("</tr>");
		}
		contents1.append("</table><br/><br/>");
    	if (StringUtils.isNotEmpty(contents1)) {
        	contents.append("<p><span style='font-size:20px'>Hi all,<br/>&nbsp;&nbsp;&nbsp;&nbsp;系统监控到产品审批价格低于亚马逊保本价,明细如下：</span></p>");
        	contents.append(contents1);
    	}
		return contents.toString();
	}

}
